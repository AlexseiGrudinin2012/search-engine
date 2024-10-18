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
        setIndexationState(!multithreadTaskExecutor.isPoolSizeEmpty());
        if (isIndexationStarted) {
            log.atWarn()
                    .addKeyValue("isIndexationStarted", isIndexationStarted)
                    .log(INDEXATION_STARTED_MESSAGE);
            return buildResponseModel(INDEXATION_STARTED_MESSAGE, false);
        }
        List<SiteDto> siteDtos = siteService.getAllSites()
                //TODO Убрать фильтр
                .stream()
                .filter(s -> s.getId().equals(4L))
                .toList();
        if (CollectionUtils.isEmpty(siteDtos)) {
            setIndexationState(false);
            log.atWarn()
                    .addKeyValue("siteDtosSize", 0)
                    .log(EMPTY_SITE_LIST_MESSAGE);
            return buildResponseModel(EMPTY_SITE_LIST_MESSAGE, false);
        }
        pageService.deleteAll();
        setIndexationState(true);
        List<RecursiveWebAnalyzerTask> tasks = siteDtos
                .stream()
                .map(this::getNewIndexationTask)
                .toList();
        tasks.forEach(multithreadTaskExecutor::run);
        log.info("Индексация запущена");
        return buildResponseModel(null, true);
    }

    @Override
    public StatusResponseModel stopIndexation() {
        setIndexationState(false);
        multithreadTaskExecutor.shutdownAll();
        log.info("Индексация остановлена");
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
