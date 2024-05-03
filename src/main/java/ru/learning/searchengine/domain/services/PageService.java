package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

public interface PageService {
        Long getPagesCount(SiteDto siteDto);

        void saveAllBySite(Long siteId, Set<PageDto> fetchedPages);
}
