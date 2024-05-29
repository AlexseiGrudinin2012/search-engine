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
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    @Override
    public Long getPagesCount(SiteDto siteDto) {
        return siteDto != null && siteDto.getId() != null
                ? this.pageRepository.countBySiteId(siteDto.getId())
                : 0L;
    }

    @Transactional
    public void saveAll(Set<PageDto> fetchedPages) {
        if (CollectionUtils.isEmpty(fetchedPages)) {
            return;
        }
        List<PageEntity> saveList = fetchedPages
                .stream()
                .filter(p -> !this.pageRepository.existsByPath(p.getPath()))
                .map(PageMapper.INSTANCE::dtoToEntity)
                .toList();
        this.pageRepository.saveAll(saveList);
    }

    @Override
    @Transactional
    public void truncatePages() {
        this.pageRepository.truncatePages();
    }
}
