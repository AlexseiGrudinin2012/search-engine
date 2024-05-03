package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;

import java.util.List;


public interface SiteService {
    List<SiteDto> getSiteList();

    List<SiteDto> getSiteListByStatuses(List<SiteStatus> siteStatuses);

    void save(SiteDto siteDto);
}
