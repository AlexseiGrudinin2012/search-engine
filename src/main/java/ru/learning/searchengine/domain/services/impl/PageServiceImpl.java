package ru.learning.searchengine.domain.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.persistance.repositories.PageRepository;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    @Autowired
    public PageServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public Long getPagesCount(SiteDto siteDto) {
        return siteDto == null || siteDto.getId() == null ? 0L :
                this.pageRepository.countBySiteId(siteDto.getId());
    }
}
