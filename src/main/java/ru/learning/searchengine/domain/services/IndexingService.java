package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.ErrorDetailsDto;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

public interface IndexingService {
    void startIndexation();

    void stopIndexation();

    boolean isIndexationStarted();


    void savePages(SiteDto siteDto, Set<PageDto> pageDtoSet);


    void saveSiteStatusFailed(SiteDto siteDto, ErrorDetailsDto errorDetailsDto);

    void saveSiteStatusIndexed(SiteDto siteDto);

    void saveSiteStatusIndexing(SiteDto siteDto);
}
