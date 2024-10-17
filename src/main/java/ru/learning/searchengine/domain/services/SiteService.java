package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;

import java.util.List;


public interface SiteService {
    List<SiteDto> getAllSites();

    void save(SiteDto siteDto);

    boolean isAllSitesIndexed();
}
