package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.infrastructure.mappers.PageMapper;
import ru.learning.searchengine.persistance.entities.PageEntity;
import ru.learning.searchengine.persistance.repositories.PageRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    @Override
    public Long getPagesCount(SiteDto siteDto) {
        return siteDto == null || siteDto.getId() == null ? 0L :
                this.pageRepository.countBySiteId(siteDto.getId());
    }

    @Override
    @Transactional
    public void saveAllBySite(Long siteId, Set<PageDto> fetchedPages) {
        if (fetchedPages == null) {
            return;
        }

        List<PageEntity> forSavedPagesList = fetchedPages
                .stream()
                .map(e -> this.convertForEntityAndUpdate(e, siteId))
                .toList();

        if (CollectionUtils.isEmpty(forSavedPagesList)) {
            return;
        }
        this.pageRepository.saveAllAndFlush(forSavedPagesList);
    }

    private PageEntity convertForEntityAndUpdate(PageDto fetchedPageDto, Long siteId) {
        //TODO УБрать
        Optional<PageEntity> persistedPageEntity =
                this.pageRepository.findByPathAndSiteId(fetchedPageDto.getPath(), siteId);

        if (persistedPageEntity.isEmpty()) {
            log.atInfo()
                    .addKeyValue("@fetchedPageDto", fetchedPageDto)
                    .addKeyValue("siteName", fetchedPageDto.getSite().getName())
                    .log("Найдена новая страница у сайта");
            return PageMapper.INSTANCE.dtoToEntity(fetchedPageDto);
        }

        log.atInfo()
                .addKeyValue("@fetchedPageDto", fetchedPageDto)
                .log("Обновляем содержимое страницы");
        return PageMapper.INSTANCE.updateEntity(fetchedPageDto, persistedPageEntity.get());
    }

    @Override
    @Transactional
    public void deleteAllBySite(SiteDto siteDto) {
        if (siteDto == null || siteDto.getId() == null) {
            return;
        }
        this.pageRepository.deleteAllBySiteId(siteDto.getId());
    }
}
