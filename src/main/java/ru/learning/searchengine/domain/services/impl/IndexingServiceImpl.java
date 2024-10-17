package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.jsoup.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreads.ForkJoinPoolWrapper;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadTaskExecutor;
import ru.learning.searchengine.infrastructure.multithreads.impl.ForkJoinPoolWrapperImpl;
import ru.learning.searchengine.infrastructure.multithreads.impl.tasks.RecursiveWebAnalyzerTask;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {
    private static final String STOP_TASK_MESSAGE = "Индексация остановлена пользователем";

    private final SiteService siteService;
    private final PageService pageService;
    private final JsoupConfig jsoupConfig;
    private final MultithreadTaskExecutor multithreadTaskExecutor;
    private final AtomicBoolean isIndexationStarted = new AtomicBoolean(false);

    @Override
    public void startIndexation() {
        isIndexationStarted.set(true);
        siteService.getAllSites().forEach(s ->);
        multithreadTaskExecutor.run();
    }

    @Override
    public void stopIndexation() {
        isIndexationStarted.set(false);
        multithreadTaskExecutor.shutdownAll();
    }

    @Override
    public boolean isIndexationStarted() {
        return isIndexationStarted.get();
    }


    private void startParsePagesTask(SiteDto siteDto) {
        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapperImpl<>()) {
            forkJoinPoolWrapper.invoke(new RecursiveWebAnalyzerTask(
                    siteDto,
                    jsoupConfig.getConnection()
            ));
        }
    }
}
