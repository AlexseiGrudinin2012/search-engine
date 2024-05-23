package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

//TODO Подумать над названием!!
public interface WebAnalyzerService {

    //Костыли, они такие...
    void updateStatus(SiteDto siteDto, Throwable throwable, Set<PageDto> pageDtoSet);

    void updateStatus(SiteDto siteDto, Throwable throwable);

    void saveSiteStatusIndexed(SiteDto siteDto);

    void stop(boolean stop);

    boolean isStopped();
}
