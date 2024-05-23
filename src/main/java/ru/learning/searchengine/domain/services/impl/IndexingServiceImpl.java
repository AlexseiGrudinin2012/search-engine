package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.impl.indexing.tasks.RecursiveWebAnalyzerTask;
import ru.learning.searchengine.infrastructure.jsoup.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreads.ForkJoinPoolWrapper;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private static final String STOP_TASK_MESSAGE = "Индексация остановлена пользователем";
    private final SiteService siteService;
    private final MultithreadExecutor executor;
    private final PageService pageService;
    private final JsoupConfig jsoupConfig;
    //TODO подумать, как всё красиво остановить....
    private final List<ForkJoinPoolWrapper<Void>> taskPool = new ArrayList<>();
    private final AtomicBoolean isStopped = new AtomicBoolean(true);

    @Override
    public synchronized void save(SiteDto siteDto, Throwable throwable, Set<PageDto> pageDtoSet) {
        if (!CollectionUtils.isEmpty(pageDtoSet)) {
            this.pageService.saveAllBySite(siteDto.getId(), pageDtoSet);
        }
        this.updateStatus(siteDto, throwable);
    }

    @Override
    public void updateStatus(SiteDto siteDto, Throwable throwable) {
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
    public void saveSiteStatusIndexed(SiteDto siteDto) {
        siteDto.setLastError(null);
        this.updateSiteStatus(siteDto, SiteStatus.INDEXED);
    }

    private void saveSiteStatusFailed(SiteDto siteDto, Throwable throwable) {
        this.saveSiteStatusFailed(
                siteDto,
                throwable == null || throwable instanceof CancellationException
                        ? STOP_TASK_MESSAGE
                        : throwable.getMessage()
        );
    }

    private void updateSiteStatus(SiteDto siteDto, SiteStatus siteStatus) {
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

    public void startParsePagesTask(SiteDto siteDto) {
        this.updateSiteStatus(siteDto, SiteStatus.INDEXING);
        //TODO сделать в потоке ?
        this.pageService.deleteAllBySite(siteDto);

        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapper<>()) {
            //TODO возможно я где-то не прав ...
            this.taskPool.add(forkJoinPoolWrapper);
            forkJoinPoolWrapper.invoke(new RecursiveWebAnalyzerTask(
                    siteDto,
                    this.jsoupConfig.getConnection(),
                    this
            ));
        }
    }

    @Override
    public void startIndexation() {
        this.isStopped.set(false);
        this.siteService.getSiteList()
                .forEach(siteDto -> this.executor.runNewTask(() -> this.startParsePagesTask(siteDto)));
        log.info("Задание на индексацию отправлено на выполнение");
    }

    @Override
    public void stopIndexation() {
        this.isStopped.set(true);
        if (!CollectionUtils.isEmpty(this.taskPool)) {
            //TODO подумать, как всё красиво остановить....
            this.taskPool.forEach(t -> this.executor.runNewTask(t::close));
            this.taskPool.clear();
        }
        this.siteService.getSiteListByStatuses(SiteStatus.getNonIndexedStatuses())
                .forEach(s -> this.updateStatus(s, null));
        this.executor.shutdownTasksNow();
        log.info("Остановка индексации произведена успешно");
    }

    @Override
    public boolean isIndexationStarted() {
        return !this.isStopped.get();
    }
}
