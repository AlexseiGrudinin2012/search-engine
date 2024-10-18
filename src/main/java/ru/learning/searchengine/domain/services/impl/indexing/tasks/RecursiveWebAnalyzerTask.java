package ru.learning.searchengine.domain.services.impl.indexing.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.impl.indexing.helpers.IndexingSiteHelper;
import ru.learning.searchengine.domain.services.impl.indexing.model.IndexingResultDto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
final public class RecursiveWebAnalyzerTask extends RecursiveAction {
    private static Connection connection;

    private final String currentLink;
    //Метод извлечения промежуточных данных
    private static Consumer<IndexingResultDto> runIntermediateAction;
    //Метод проверки запущена ли индексация всех страниц
    private static Supplier<Boolean> webAnalyzerRunningCheck;
    private final SiteDto rootSiteDto;
    private final Set<String> linkStorage;

    private static final String RESPONSE_CONTENT_TYPE = "text/html";

    public RecursiveWebAnalyzerTask(
            SiteDto rootSiteDto,
            Connection connection,
            Consumer<IndexingResultDto> runIntermediateAction,
            Supplier<Boolean> webAnalyzerRunningCheck
    ) {
        this(rootSiteDto, null, ConcurrentHashMap.newKeySet());
        RecursiveWebAnalyzerTask.connection = connection;
        RecursiveWebAnalyzerTask.runIntermediateAction = runIntermediateAction;
        RecursiveWebAnalyzerTask.webAnalyzerRunningCheck = webAnalyzerRunningCheck;
    }

    private RecursiveWebAnalyzerTask(SiteDto rootSiteDto,
                                     String currentLink,
                                     Set<String> linkStorage) {
        this.rootSiteDto = rootSiteDto;
        this.currentLink = currentLink;
        this.linkStorage = linkStorage;
    }

    @Override
    public void compute() {
        try {
            if (!webAnalyzerRunningCheck.get()) {
                IndexingSiteHelper.getInstance().updateSiteInfo(rootSiteDto, SiteStatus.FAILED, false);
                return;
            }

            String currentLink = StringUtils.isEmpty(this.currentLink)
                    ? rootSiteDto.getUrl()
                    : this.currentLink;

            IndexingSiteHelper.getInstance().updateSiteInfo(rootSiteDto, SiteStatus.INDEXING);

            Set<PageDto> children = getChildren(currentLink);
            if (CollectionUtils.isEmpty(children)) {
                return;
            }

            List<RecursiveWebAnalyzerTask> newTasks = children
                    .stream()
                    .map(PageDto::getPath)
                    .map(this::forkNewTask)
                    .toList();
            newTasks.forEach(ForkJoinTask::join);

            if (newTasks.stream().allMatch(ForkJoinTask::isDone) && webAnalyzerRunningCheck.get()) {
                IndexingSiteHelper.getInstance().updateSiteInfo(rootSiteDto, SiteStatus.INDEXED);
                accept(children);
            }
        } catch (CancellationException e) {
            log.atInfo()
                    .setCause(e)
                    .log("Задача была отменена автоматически");
            IndexingSiteHelper.getInstance().updateSiteInfo(rootSiteDto, SiteStatus.FAILED);
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("currentLink", currentLink)
                    .log("Неизвестная ошибка во время парсинга страницы");
            IndexingSiteHelper.getInstance().updateSiteInfo(rootSiteDto, SiteStatus.FAILED, e);
        } finally {
            //Для получения последних данных независимо от состояния метода - выполним необходимое
            accept(Collections.emptySet());
        }
    }

    private void accept(Set<PageDto> pages) {
        runIntermediateAction.accept(IndexingSiteHelper.getInstance().buildResult(pages, rootSiteDto));
    }

    private RecursiveWebAnalyzerTask forkNewTask(String newLink) {
        //Поспим и работать!
        sleep();
        RecursiveWebAnalyzerTask recursiveWebAnalyzerTask = new RecursiveWebAnalyzerTask(
                rootSiteDto,
                newLink,
                linkStorage
        );
        recursiveWebAnalyzerTask.fork();
        return recursiveWebAnalyzerTask;
    }

    public Set<PageDto> getChildren(final String link) throws IOException {
        Connection.Response response = connection
                .newRequest()
                .url(link)
                .execute();
        return response == null || !this.isValidContentType(response.contentType())
                ? Collections.emptySet()
                : parsePages(response);
    }

    private Set<PageDto> parsePages(Connection.Response response) throws IOException {
        String content = response.body();
        int httpCode = response.statusCode();
        return response.parse()
                .select("a")
                .stream()
                .map(el -> el.attr("abs:href"))
                .filter(this::isLinkValid)
                .filter(linkStorage::add)
                .map(path -> PageDto
                        .builder()
                        .code(httpCode)
                        .site(rootSiteDto)
                        .content(content)
                        .path(path)
                        .build())
                .collect(Collectors.toSet());
    }


    private boolean isLinkValid(final String link) {
        String trimLink = StringUtils.trim(link);
        return StringUtils.isNotEmpty(trimLink)
                && trimLink.contains(rootSiteDto.getUrl())
                && !trimLink.contains("#")
                && trimLink.endsWith("/");
    }

    private boolean isValidContentType(final String contentType) {
        return StringUtils.isNotEmpty(contentType) && contentType.contains(RESPONSE_CONTENT_TYPE);
    }

    private synchronized void sleep() {
        try {
            Thread.sleep(125);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}