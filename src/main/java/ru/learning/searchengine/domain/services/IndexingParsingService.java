package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.List;
import java.util.Set;

public interface IndexingParsingService {
    List<SiteDto> getNonIndexingSites();

    void saveAllBySite(SiteDto siteDto);

    void saveAllBySite(SiteDto siteDto, Set<PageDto> fetchedPages);
}
