package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;

public interface LemmaService {
    Long getLemmaCount(SiteDto siteDto);
}
