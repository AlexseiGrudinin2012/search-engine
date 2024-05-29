package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.ErrorDetailsDto;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private static final String STOP_TASK_MESSAGE = "Индексация остановлена пользователем";
    private final SiteService siteService;
    //Синхронизируемый
    private final MultithreadExecutor executor;
    private final PageService pageService;
    private final JsoupConfig jsoupConfig;
    //Синхронизируем в этом классе
    private final Set<ForkJoinPoolWrapper<Void>> taskPool = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean isStopped = new AtomicBoolean(true);

    /****************************** SAVE PAGES and etc ********************************/
    public void savePages(SiteDto siteDto, Set<PageDto> pageDtoSet) {
        if (!CollectionUtils.isEmpty(pageDtoSet)) {
            this.pageService.saveAll(pageDtoSet);
        }
        this.saveSiteStatusIndexing(siteDto);
    }

    //Будут еще методы или перегрузки...

    /****************************** INDEXATION MANAGER ********************************/
    private void startParsePagesTask(SiteDto siteDto) {
        this.saveSiteStatusIndexing(siteDto);
        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapper<>()) {
            this.taskPool.add(forkJoinPoolWrapper);
            forkJoinPoolWrapper.invoke(new RecursiveWebAnalyzerTask(
                    siteDto,
                    this.jsoupConfig.getConnection(),
                    this
            ));
            if (!this.isStopped.get()) {
                this.saveSiteStatusIndexed(siteDto);
            }
        }
    }

    @Override
    public void startIndexation() {
        this.pageService.truncatePages();
        this.isStopped.set(false);
        this.siteService
                .getSiteList()
                .forEach(siteDto -> this.executor.runNewTask(() -> this.startParsePagesTask(siteDto)));
        log.info("Задание на индексацию отправлено на выполнение");
    }

    @Override
    public void stopIndexation() {
        this.isStopped.set(true);
        if (!CollectionUtils.isEmpty(this.taskPool)) {
            synchronized (this.taskPool) {
                this.taskPool.forEach(t -> this.executor.runNewTask(t::close));
                this.taskPool.clear();
            }
        }

        this.executor.shutdownTasksNow();
        this.siteService
                .getSiteListByStatuses(SiteStatus.getNonIndexedStatuses())
                .forEach(s -> this.saveSiteStatusFailed(s, STOP_TASK_MESSAGE));
        log.atInfo()
                .addKeyValue("activeThreadCount", Thread.activeCount())
                .log("Остановка индексации произведена успешно");
    }

    @Override
    public boolean isIndexationStarted() {
        return !this.isStopped.get();
    }

    /****************************** STATUSES ********************************/
    @Override
    public void saveSiteStatusFailed(SiteDto siteDto, ErrorDetailsDto errorDetailsDto) {
        String errorMessage = errorDetailsDto == null
                || errorDetailsDto.getThrowable() == null
                || errorDetailsDto.getThrowable() instanceof CancellationException
                ? STOP_TASK_MESSAGE
                : errorDetailsDto.getErrorMessage();
        this.saveSiteStatusFailed(siteDto, errorMessage);
    }

    private void saveSiteStatusFailed(SiteDto siteDto, String errorMessage) {
        siteDto.setLastError(errorMessage);
        this.updateSiteStatus(siteDto, SiteStatus.FAILED);
    }

    @Override
    public void saveSiteStatusIndexed(SiteDto siteDto) {
        siteDto.setLastError(null);
        this.updateSiteStatus(siteDto, SiteStatus.INDEXED);
    }

    @Override
    public void saveSiteStatusIndexing(SiteDto siteDto) {
        this.updateSiteStatus(siteDto, SiteStatus.INDEXING);
    }

    private void updateSiteStatus(SiteDto siteDto, SiteStatus siteStatus) {
        if (siteStatus == null || siteDto == null) {
            return;
        }

        if (!siteStatus.equals(siteDto.getStatus())) {
            siteDto.setStatus(siteStatus);
        }

        siteDto.setStatusTime(LocalDateTime.now());
        this.siteService.save(siteDto);
    }

    /***********************************************************/
    //Для того чтобы после завершения всех задач вновь можно было нажать на Start Indexing
    // (иначе будет сообщение, что индексация уже запущена)
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    private void isAllTaskComplete() {
        boolean isExecuting = this.taskPool.stream().anyMatch(ForkJoinPoolWrapper::isExecuting);
        if (isExecuting) {
            return;
        }
        this.isStopped.set(true);
    }
}
