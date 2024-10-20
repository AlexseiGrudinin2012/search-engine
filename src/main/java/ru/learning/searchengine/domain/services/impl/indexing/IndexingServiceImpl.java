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
    private final static String INDEXING_STARTED_ERROR_MESSAGE = "Индексация уже запущена!";
    private final static String EMPTY_SITE_LIST_ERROR_MESSAGE = "Отсутствуют сайты для выполнения индексации";
    private final SiteService siteService;
    private final PageService pageService;
    private volatile boolean isIndexingStarted = false;
    private final JsoupConfig jsoupConfig;
    private final MultithreadTaskExecutor<Void> multithreadTaskExecutor;

    @Override
    public StatusResponseModel start() {
        updateIndexingState(!multithreadTaskExecutor.isPoolSizeEmpty());
        if (isIndexingStarted) {
            log.warn(INDEXING_STARTED_ERROR_MESSAGE);
            return buildResponseModel(INDEXING_STARTED_ERROR_MESSAGE, false);
        }
        List<SiteDto> siteDtos = siteService.getAllSites()
                //TODO Убрать фильтр
                .stream()
                .filter(s -> s.getId().equals(4L))
                .toList();
        if (CollectionUtils.isEmpty(siteDtos)) {
            log.warn(EMPTY_SITE_LIST_ERROR_MESSAGE);
            return buildResponseModel(EMPTY_SITE_LIST_ERROR_MESSAGE, false);
        }

        updateIndexingState(true);
        pageService.deleteAll();
        List<RecursiveWebAnalyzerTask> tasks = siteDtos
                .stream()
                .map(this::getNewIndexingTask)
                .toList();
        tasks.forEach(multithreadTaskExecutor::run);
        log.info("Индексация запущена");
        return buildResponseModel(null, true);
    }

    @Override
    public StatusResponseModel stop() {
        updateIndexingState(false);
        multithreadTaskExecutor.shutdownAll();
        log.info("Индексация остановлена");
        return buildResponseModel(null, true);
    }

    private boolean isIndexingStarted() {
        return isIndexingStarted;
    }

    private RecursiveWebAnalyzerTask getNewIndexingTask(SiteDto siteDto) {
        return new RecursiveWebAnalyzerTask(
                siteDto,
                jsoupConfig.getConnection(),
                this::saveResult,
                this::isIndexingStarted
        );
    }

    private void updateIndexingState(boolean isIndexingStarted) {
        this.isIndexingStarted = isIndexingStarted;
    }

    private void saveResult(IndexingResultDto indexingResultDto) {
        if (indexingResultDto == null) {
            return;
        }
        SiteDto siteDto = indexingResultDto.getSite();
        siteService.save(siteDto);
        pageService.saveAll(indexingResultDto.getPages());
        log.atInfo()
                .addKeyValue("siteId", siteDto.getId())
                .addKeyValue("siteName", siteDto.getName())
                .addKeyValue("siteUrl", siteDto.getUrl())
                .log("Информация об индексации сайта обновлена");
    }

    private StatusResponseModel buildResponseModel(String error, boolean result) {
        return StatusResponseModel
                .builder()
                .result(result)
                .error(error)
                .build();
    }
}
