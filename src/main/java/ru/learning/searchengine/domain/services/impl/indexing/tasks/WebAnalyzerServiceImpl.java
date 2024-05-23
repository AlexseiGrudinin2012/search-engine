package ru.learning.searchengine.domain.services.impl.indexing.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.WebAnalyzerService;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class WebAnalyzerServiceImpl implements WebAnalyzerService {

    private final PageService pageService;
    private final SiteService siteService;

    private static final String STOP_TASK_MESSAGE = "Индексация остановлена пользователем";

    private final AtomicBoolean isStopped = new AtomicBoolean(true);

    @Override
    public synchronized void updateStatus(SiteDto siteDto, Throwable throwable, Set<PageDto> pageDtoSet) {
        if (!CollectionUtils.isEmpty(pageDtoSet)) {
            //TODO
        }
        this.updateStatus(siteDto, throwable);
    }

    @Override
    public synchronized void updateStatus(SiteDto siteDto, Throwable throwable) {
        if (this.isStopped.get()) {
            this.saveSiteStatusFailed(siteDto, STOP_TASK_MESSAGE);
            return;
        } else if (!this.isStopped.get() || throwable == null) {
            this.updateSiteStatus(siteDto, SiteStatus.INDEXING);
            return;
        }
        this.saveSiteStatusFailed(siteDto, throwable);
    }

    @Override
    public synchronized void saveSiteStatusIndexed(SiteDto siteDto) {
        siteDto.setLastError(null);
        this.updateSiteStatus(siteDto, SiteStatus.INDEXED);
    }

    @Override
    public void stop(boolean stop) {
        this.isStopped.compareAndSet(!stop, stop);
    }

    @Override
    public boolean isStopped() {
        return this.isStopped.get();
    }

    private synchronized void saveSiteStatusFailed(SiteDto siteDto, Throwable throwable) {
        this.saveSiteStatusFailed(
                siteDto,
                throwable == null || throwable instanceof CancellationException
                        ? STOP_TASK_MESSAGE
                        : throwable.getMessage()
        );
    }


    private synchronized void updateSiteStatus(SiteDto siteDto, SiteStatus siteStatus) {
        if (siteStatus == null || siteDto == null) {
            return;
        }
        if (!siteStatus.equals(siteDto.getStatus())) {
            siteDto.setStatus(siteStatus);
        }
        siteDto.setStatusTime(new Date());
        this.siteService.save(siteDto);
    }

    private void saveSiteStatusFailed(SiteDto siteDto, String errorMessage) {
        siteDto.setLastError(errorMessage);
        this.updateSiteStatus(siteDto, SiteStatus.FAILED);
    }
}
