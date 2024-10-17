package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

public interface PageService {
    Long getPagesCount(SiteDto siteDto);

    void saveAll(Set<PageDto> fetchedPages);

    void deleteAll();
}
