package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.IndexingService;
import ru.learning.searchengine.domain.services.PageService;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.domain.services.impl.indexing.model.IndexingResultDto;
import ru.learning.searchengine.domain.services.impl.indexing.tasks.RecursiveWebAnalyzerTask;
import ru.learning.searchengine.infrastructure.jsoup.JsoupConfig;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadTaskExecutor;
import ru.learning.searchengine.presentation.models.StatusResponseModel;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final static String INDEXATION_STARTED_MESSAGE = "Индексация уже запущена!";
    private final static String EMPTY_SITE_LIST_MESSAGE = "Отсутствуют сайты для выполнения индексации";
    private final SiteService siteService;
    private final PageService pageService;
    private volatile boolean isIndexationStarted = false;
    private final JsoupConfig jsoupConfig;
    private final MultithreadTaskExecutor<Void> multithreadTaskExecutor;

    @Override
    public StatusResponseModel startIndexation() {
        if (isIndexationStarted) {
            return buildResponseModel(INDEXATION_STARTED_MESSAGE, false);
        }
        List<SiteDto> siteDtos = siteService.getAllSites();
        if (CollectionUtils.isEmpty(siteDtos)) {
            setIndexationState(false);
            return buildResponseModel(EMPTY_SITE_LIST_MESSAGE, false);
        }
        setIndexationState(true);
        List<RecursiveWebAnalyzerTask> tasks = siteDtos
                .stream()
                .map(this::getNewIndexationTask)
                .toList();
        tasks.forEach(multithreadTaskExecutor::run);
        return buildResponseModel(null, true);
    }

    @Override
    public StatusResponseModel stopIndexation() {
        setIndexationState(false);
        multithreadTaskExecutor.shutdownAll();
        return buildResponseModel(null, true);
    }


    private boolean isIndexationStarted() {
        return isIndexationStarted;
    }

    private RecursiveWebAnalyzerTask getNewIndexationTask(SiteDto siteDto) {
        return new RecursiveWebAnalyzerTask(
                siteDto,
                jsoupConfig.getConnection(),
                this::saveResult,
                this::isIndexationStarted
        );
    }

    private void setIndexationState(boolean isStarted) {
        this.isIndexationStarted = isStarted;
    }

    private void saveResult(IndexingResultDto indexingResultDto) {
        System.out.printf(
                "Status - %s, lastError: %s, siteUrl - %s%n",
                indexingResultDto.getSite().getStatus(),
                indexingResultDto.getSite().getLastError(),
                indexingResultDto.getSite().getUrl()
        );
    }

    private StatusResponseModel buildResponseModel(String error, boolean result) {
        return StatusResponseModel
                .builder()
                .result(result)
                .error(error)
                .build();
    }
}
