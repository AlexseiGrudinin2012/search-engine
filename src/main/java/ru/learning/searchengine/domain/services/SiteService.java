package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.List;


public interface SiteService {
    List<SiteDto> getSiteList();
}
