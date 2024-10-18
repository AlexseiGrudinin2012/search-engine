package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.dto.statistics.DetailedStatisticsItemDto;
import ru.learning.searchengine.domain.dto.statistics.StatisticsDataDto;
import ru.learning.searchengine.domain.dto.statistics.StatisticsDto;
import ru.learning.searchengine.domain.dto.statistics.TotalStatisticsDto;
import ru.learning.searchengine.domain.services.LemmaService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.StatisticsService;
import ru.learning.searchengine.infrastructure.exceptions.SiteNotFoundException;
import ru.learning.searchengine.infrastructure.mappers.StatisticsMapper;
import ru.learning.searchengine.presentation.models.statistics.StatisticsResponseModel;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteService siteService;

    private final PageService pageService;

    private final LemmaService lemmaService;

    @Override
    public StatisticsResponseModel getStatistics() {
        List<SiteDto> siteDtos = this.siteService.getAllSites();
        if (CollectionUtils.isEmpty(siteDtos)) {
            throw new SiteNotFoundException();
        }

        List<DetailedStatisticsItemDto> detailed =
                siteDtos.stream()
                        .map(this::getDetailedStatisticsItem)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .sorted(Comparator.comparing(DetailedStatisticsItemDto::getName))
                        .toList();

        return StatisticsMapper.INSTANCE.dtoToModel(
                StatisticsDto
                        .builder()
                        .statistics(getStatisticsDataDto(detailed))
                        .result(true)
                        .build()
        );
    }

    private StatisticsDataDto getStatisticsDataDto(List<DetailedStatisticsItemDto> detailedStatisticsItemDtos) {
        return StatisticsDataDto
                .builder()
                .total(getTotalStatisticsDto(detailedStatisticsItemDtos))
                .detailed(detailedStatisticsItemDtos)
                .build();
    }

    private TotalStatisticsDto getTotalStatisticsDto(List<DetailedStatisticsItemDto> detailedStatisticsItemDtos) {
        return TotalStatisticsDto
                .builder()
                .sites(detailedStatisticsItemDtos.size())
                .indexing(true)
                .pages(detailedStatisticsItemDtos.stream().mapToLong(DetailedStatisticsItemDto::getPages).sum())
                .lemmas(detailedStatisticsItemDtos.stream().mapToLong(DetailedStatisticsItemDto::getLemmas).sum())
                .build();
    }

    private Optional<DetailedStatisticsItemDto> getDetailedStatisticsItem(SiteDto siteDto) {
        return Optional.ofNullable(siteDto)
                .map(s ->
                        DetailedStatisticsItemDto
                                .builder()
                                .name(s.getName())
                                .url(s.getUrl())
                                .status(s.getStatus())
                                .pages(pageService.getPagesCount(s))
                                .lemmas(lemmaService.getLemmaCount(s))
                                .error(s.getLastError())
                                .statusTime(s.getStatusTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .build()
                );
    }
}
