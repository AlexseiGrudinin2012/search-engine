package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

public interface IndexingService {
    void startIndexation();

    void stopIndexation();

    boolean isIndexationStarted();

    void save(SiteDto siteDto, Throwable throwable, Set<PageDto> pageDtoSet);

    void updateStatus(SiteDto siteDto, Throwable throwable);

    void saveSiteStatusIndexed(SiteDto siteDto);
}
