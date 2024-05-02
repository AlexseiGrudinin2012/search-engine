package ru.learning.searchengine.domain.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.dto.statistics.DetailedStatisticsItemDto;
import ru.learning.searchengine.domain.dto.statistics.StatisticsDataDto;
import ru.learning.searchengine.domain.dto.statistics.StatisticsDto;
import ru.learning.searchengine.domain.dto.statistics.TotalStatisticsDto;
import ru.learning.searchengine.infrastructure.exceptions.SiteNotFoundException;
import ru.learning.searchengine.domain.services.LemmaService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.StatisticsService;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteService siteService;

    private final PageService pageService;

    private final LemmaService lemmaService;

    @Autowired
    public StatisticsServiceImpl(SiteService siteService, PageService pageService, LemmaService lemmaService) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
    }

    @Override
    public StatisticsDto getStatistics() {
        List<SiteDto> siteDtos = this.siteService.getSiteList();
        if (CollectionUtils.isEmpty(siteDtos)) {
            throw new SiteNotFoundException();
        }

        List<DetailedStatisticsItemDto> detailed =
                siteDtos.stream()
                        .map(this::getDetailedStatisticsItem)
                        .toList();

        return StatisticsDto.builder()
                .statistics(this.getStatisticsDataDto(detailed))
                .result(true)
                .build();
    }

    private StatisticsDataDto getStatisticsDataDto(List<DetailedStatisticsItemDto> detailedStatisticsItemDtos) {
        return StatisticsDataDto
                .builder()
                .total(this.getTotalStatisticsDto(detailedStatisticsItemDtos))
                .detailed(detailedStatisticsItemDtos)
                .build();
    }

    private TotalStatisticsDto getTotalStatisticsDto(List<DetailedStatisticsItemDto> detailedStatisticsItemDtos) {
        //Можем делать запросы по сайтам на запрос страниц и лемм, но так как уже всё вычислили,
        // то лучше взять из detailedStatisticsItem и высчитать значения
        return TotalStatisticsDto
                .builder()
                .sites(detailedStatisticsItemDtos.size())
                .indexing(true)
                .pages(detailedStatisticsItemDtos.stream().mapToLong(DetailedStatisticsItemDto::getPages).sum())
                .lemmas(detailedStatisticsItemDtos.stream().mapToLong(DetailedStatisticsItemDto::getLemmas).sum())
                .build();
    }

    private DetailedStatisticsItemDto getDetailedStatisticsItem(SiteDto siteDto) {
        return DetailedStatisticsItemDto
                .builder()
                .name(siteDto.getName())
                .url(siteDto.getUrl())
                .status(siteDto.getStatus())
                .pages(this.pageService.getPagesCount(siteDto))
                .lemmas(this.lemmaService.getLemmaCount(siteDto))
                .error(siteDto.getLastError())
                .statusTime(System.currentTimeMillis())
                .build();
    }
}
