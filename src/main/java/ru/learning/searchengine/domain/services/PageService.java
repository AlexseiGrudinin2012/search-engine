package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;

public interface PageService {
        Long getPagesCount(SiteDto siteDto);
}
