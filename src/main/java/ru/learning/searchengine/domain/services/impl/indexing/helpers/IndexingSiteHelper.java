package ru.learning.searchengine.domain.services.impl.indexing.helpers;

import lombok.extern.slf4j.Slf4j;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.impl.indexing.model.IndexingResultDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CancellationException;

@Slf4j
public class IndexingSiteHelper {
    private static final String STOP_TASK_MESSAGE = "Индексация остановлена пользователем";
    private static final IndexingSiteHelper INSTANCE = new IndexingSiteHelper();

    private IndexingSiteHelper() {
    }

    public static IndexingSiteHelper getInstance() {
        return INSTANCE;
    }

    public void updateSiteInfo(SiteDto siteDto,
                               SiteStatus siteStatus,
                               boolean isIndexationStarted) {
        updateSiteInfo(siteDto, siteStatus, isIndexationStarted, null);
    }

    public void updateSiteInfo(SiteDto siteDto,
                               SiteStatus siteStatus,
                               Throwable throwable) {
        updateSiteInfo(siteDto, siteStatus, true, throwable);
    }

    public void updateSiteInfo(SiteDto siteDto, SiteStatus siteStatus) {
        updateSiteInfo(siteDto, siteStatus, true, null);
    }

    public void updateSiteInfo(SiteDto siteDto,
                               SiteStatus siteStatus,
                               boolean isIndexationStarted,
                               Throwable throwable
    ) {
        siteDto.setStatus(siteStatus);
        siteDto.setStatusTime(LocalDateTime.now());
        //Если сайт уже проиндексирован, даже если пользователь остановил индексацию, то нет смысла говорить об этом
        if (SiteStatus.INDEXED.equals(siteStatus)) {
            siteDto.setLastError(null);
            return;
        }

        //Если пользователь остановил индексацию, то говорим об этом
        if (!isIndexationStarted) {
            siteDto.setLastError(STOP_TASK_MESSAGE);
            return;
        }

        if (throwable != null) {
            siteDto.setLastError(
                    throwable instanceof CancellationException ?
                            STOP_TASK_MESSAGE :
                            throwable.getMessage()
            );
        }
    }

    public IndexingResultDto buildResult(Set<PageDto> pages, SiteDto site) {
        return IndexingResultDto
                .builder()
                .pages(pages)
                .site(site)
                .build();
    }
}
