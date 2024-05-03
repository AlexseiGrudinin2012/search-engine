package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.config.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreadswrappers.ForkJoinPoolWrapper;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final JsoupConfig jsoupConfig;

    private final PageService pageService;

    private final SiteService siteService;

    private ForkJoinPoolWrapper<Void> forkJoinPoolWrapper;

    private ExecutorService executorService;

    @Getter
    private boolean isStarted;

    @Override
    public void start() {
        //TODO, самому не нравится
        this.isStarted = true;
        this.executorService = Executors.newSingleThreadExecutor();
        this.siteService
                .getSiteListByStatuses(SiteStatus.getNonIndexedStatuses())
                .forEach(siteDto -> this.executorService.submit(() -> this.startParsePagesTask(siteDto)));
        log.info("Задание на индексацию отправлено на выполнение");
    }

    private void startParsePagesTask(SiteDto siteDto) {
        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapper<>()) {
            this.forkJoinPoolWrapper = forkJoinPoolWrapper;
            forkJoinPoolWrapper.invoke(new IndexingPageTask(siteDto, this.jsoupConfig.getConnection(), this.pageService));
        }
    }

    @Override
    public void stop() {
        Optional.ofNullable(this.forkJoinPoolWrapper).ifPresent(ForkJoinPoolWrapper::close);
        Optional.ofNullable(this.executorService).ifPresent(ExecutorService::shutdownNow);
        this.isStarted = false;
        log.info("Остановка индексации произведена успешно");
    }
}
