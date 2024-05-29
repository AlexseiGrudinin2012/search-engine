package ru.learning.searchengine.domain.services.impl.indexing.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.IndexingService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

@Slf4j
public
//Пусть будет с модификатором доступа только внутри пакета
class RecursiveWebAnalyzerTask extends RecursiveAction {

    //Для обеспечения уникальности ссылки
    private final Set<String> linkStorage;
    private final String currentLink;
    private final SiteDto siteDto;

    private static final String RESPONSE_CONTENT_TYPE = "text/html";
    //Ну, думаю тут static не помешает ...конфиг не будет меняться динамически без перезапуска
    private static Connection connection;
    //Плохой подход ... но за отсутствием альтернатив решения без fork-join-pool...
    private static IndexingService service;


    //Для создания из-вне
    public RecursiveWebAnalyzerTask(SiteDto siteDto, Connection connection, IndexingService service) {
        this(siteDto, null, ConcurrentHashMap.newKeySet());
        RecursiveWebAnalyzerTask.connection = connection;
        RecursiveWebAnalyzerTask.service = service;
    }

    //Для рекурсии
    private RecursiveWebAnalyzerTask(SiteDto siteDto,
                                     String currentLink,
                                     Set<String> linkStorage) {
        this.siteDto = siteDto;
        this.currentLink = currentLink;
        this.linkStorage = linkStorage;
    }

    @Override
    public void compute() {
        try {
            String currentLink = StringUtils.isEmpty(this.currentLink)
                    ? this.siteDto.getUrl()
                    : this.currentLink;

            if (!RecursiveWebAnalyzerTask.service.isIndexationStarted()) {
                //Ставим статус завершения пользователем
                RecursiveWebAnalyzerTask.service.saveSiteStatusFailed(this.siteDto, null);
                return;
            }

            RecursiveWebAnalyzerTask.service.saveSiteStatusIndexing(this.siteDto);

            Set<PageDto> children = this.getChildren(currentLink);
            if (CollectionUtils.isEmpty(children)) {
                return;
            }

            RecursiveWebAnalyzerTask.service.savePages(this.siteDto, children);
            List<RecursiveWebAnalyzerTask> newTasks = children
                    .stream()
                    .map(PageDto::getPath)
                    .map(this::forkNewPageParseTask)
                    .toList();

            newTasks.forEach(ForkJoinTask::join);
        } catch (CancellationException e) {
            log.atInfo()
                    .setCause(e)
                    .log("Задача была отменена автоматически");
            RecursiveWebAnalyzerTask.service.saveSiteStatusFailed(this.siteDto, e);
        } catch (Exception e) {
            //Останавливаем индексацию в случае ошибки, проставляем статус и время
            log.atError()
                    .setCause(e)
                    .addKeyValue("currentLink", currentLink)
                    .log("Ошибка во время парсинга страницы");
            RecursiveWebAnalyzerTask.service.saveSiteStatusFailed(this.siteDto, e);
        }
    }

    private RecursiveWebAnalyzerTask forkNewPageParseTask(String newPageTaskLink) {
        //Поспим и работать!
        this.sleep();
        RecursiveWebAnalyzerTask recursiveWebAnalyzerTask =
                new RecursiveWebAnalyzerTask(this.siteDto, newPageTaskLink, this.linkStorage);
        recursiveWebAnalyzerTask.fork();
        return recursiveWebAnalyzerTask;
    }

    //Даже у него была девушка ...
    public Set<PageDto> getChildren(final String link) throws IOException {
        Connection.Response response = connection
                .newRequest()
                .url(link)
                .execute();
        return response == null || !this.isValidContentType(response.contentType())
                ? Collections.emptySet()
                : this.parsePages(response);
    }

    private Set<PageDto> parsePages(Connection.Response response) throws IOException {
        String content = response.body();
        int httpCode = response.statusCode();
        return response.parse()
                .select("a")
                .stream()
                .map(el -> el.attr("abs:href"))
                .filter(this::isLinkValid)
                .filter(this.linkStorage::add)
                .map(path -> PageDto.builder()
                        .code(httpCode)
                        .site(this.siteDto)
                        .content(content)
                        .path(path)
                        .build())
                .collect(Collectors.toSet());
    }

    private boolean isLinkValid(final String link) {
        String trimLink = StringUtils.trim(link);
        return StringUtils.isNotEmpty(trimLink)
                && trimLink.contains(this.siteDto.getUrl())
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
