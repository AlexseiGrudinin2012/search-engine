package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.impl.indexing.model.IndexingResultDto;
import ru.learning.searchengine.domain.services.impl.indexing.tasks.RecursiveWebAnalyzerTask;
import ru.learning.searchengine.infrastructure.jsoup.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreads.ForkJoinPoolWrapper;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadTaskExecutor;
import ru.learning.searchengine.infrastructure.multithreads.impl.ForkJoinPoolWrapperImpl;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final SiteService siteService;
    private final PageService pageService;
    private final JsoupConfig jsoupConfig;
    private final MultithreadTaskExecutor multithreadTaskExecutor;
    private volatile boolean isIndexationStarted = false;

    @Override
    public void startIndexation() {
        isIndexationStarted = true;

        Optional<SiteDto> siteDto = siteService.findSiteById(4L);

        if (siteDto.isEmpty()) {
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> startParsePagesTask(siteDto.get()));


//        siteService.getAllSites().forEach(s ->);
//        multithreadTaskExecutor.run();
    }

    @Override
    public void stopIndexation() {
        isIndexationStarted = false;
        System.out.println("Is stoped");
//        multithreadTaskExecutor.shutdownAll();
    }

    @Override
    public boolean isIndexationStarted() {
        return isIndexationStarted;
    }

    private void saveResult(IndexingResultDto indexingResultDto) {

        System.out.printf(
                "Status - %s, lastError: %s%n",
                indexingResultDto.getSite().getStatus(),
                indexingResultDto.getSite().getLastError()
        );

    }


    private void startParsePagesTask(SiteDto siteDto) {
        try (ForkJoinPoolWrapper<Void> forkJoinPoolWrapper = new ForkJoinPoolWrapperImpl<>()) {
            forkJoinPoolWrapper.invoke(
                    new RecursiveWebAnalyzerTask(
                            siteDto,
                            jsoupConfig.getConnection(),
                            this::saveResult,
                            this::isIndexationStarted
                    )
            );
        }
    }
}
