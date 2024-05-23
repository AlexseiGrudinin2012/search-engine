package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.WebAnalyzerService;
import ru.learning.searchengine.domain.services.impl.indexing.tasks.RecursiveWebAnalyzerTask;
import ru.learning.searchengine.infrastructure.jsoup.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreadswrappers.ForkJoinPoolWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final JsoupConfig jsoupConfig;

    private final SiteService siteService;

    private final WebAnalyzerService webAnalyzerService;
    //TODO подумать, как всё красиво остановить....
    private final List<ForkJoinPoolWrapper<Void>> taskPool = new ArrayList<>();
    @Value("${app.multithread.thread.max}")
    private String nThreads;

    private ExecutorService executorService;

    @Override
    public void start() {
        //TODO, самому не нравится
        this.webAnalyzerService.stop(false);
        int processorsCount = NumberUtils.toInt(StringUtils.trim(this.nThreads), Runtime.getRuntime().availableProcessors() - 1);
        this.executorService = Executors.newFixedThreadPool(processorsCount);
        this.siteService
                .getSiteListByStatuses(SiteStatus.getNonIndexedStatuses())
                .forEach(siteDto -> this.executorService.submit(() -> this.startParsePagesTask(siteDto)));
        log.info("Задание на индексацию отправлено на выполнение");
    }

    private void startParsePagesTask(SiteDto siteDto) {
        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapper<>()) {
            //TODO возможно я где-то не прав ...
            this.taskPool.add(forkJoinPoolWrapper);
            forkJoinPoolWrapper.invoke(new RecursiveWebAnalyzerTask(
                    siteDto,
                    this.jsoupConfig.getConnection(),
                    this.webAnalyzerService
            ));
        }
    }

    @Override
    public void stop() {
        this.webAnalyzerService.stop(true);
        Optional.ofNullable(this.executorService).ifPresent(ExecutorService::shutdownNow);
        if (!CollectionUtils.isEmpty(this.taskPool)) {
            //TODO подумать, как всё красиво остановить....
            int processorsCount = NumberUtils.toInt(StringUtils.trim(this.nThreads), Runtime.getRuntime().availableProcessors() - 1);
            this.executorService = Executors.newFixedThreadPool(processorsCount);
            this.taskPool.forEach(t -> this.executorService.submit(t::close));
            this.taskPool.clear();
        }
        this.siteService.getSiteListByStatuses(SiteStatus.getNonIndexedStatuses())
                .forEach(s -> this.webAnalyzerService.updateStatus(s, null));
        log.info("Остановка индексации произведена успешно");
    }

    @Override
    public boolean isStarted() {
        return !this.webAnalyzerService.isStopped();
    }

    @Override
    public boolean isAllSitesIndexed() {
        return this.siteService.isAllSitesIndexed();
    }
}
