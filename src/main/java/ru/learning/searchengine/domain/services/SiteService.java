package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.List;
import java.util.Optional;


public interface SiteService {
    List<SiteDto> getAllSites();

    void save(SiteDto siteDto);

    boolean isAllSitesIndexed();

    Optional<SiteDto> findSiteById(Long siteId);
}
