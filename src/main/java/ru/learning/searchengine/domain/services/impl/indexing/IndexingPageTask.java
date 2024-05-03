package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.PageService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

@Slf4j
public class IndexingPageTask extends RecursiveAction {

    private final Set<String> linkStorage;
    private final String currentPagePath;
    private final SiteDto siteDto;
    private static Connection jsoupConnection;
    private static final String RESPONSE_CONTENT_TYPE = "text/html";
    private static PageService service;
    private static int total = 0;

    public IndexingPageTask(SiteDto siteDto, Connection connection, PageService pageService) {
        this(siteDto, null, ConcurrentHashMap.newKeySet());
        jsoupConnection = connection;
        service = pageService;
        total = 0;
    }

    private IndexingPageTask(SiteDto siteDto, String currentPagePath, Set<String> linkStorage) {
        this.siteDto = siteDto;
        this.currentPagePath = currentPagePath;
        this.linkStorage = linkStorage;
    }

    private synchronized void sleep() {
        try {
            Thread.sleep(125);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void compute() {
        String currentLink = StringUtils.isEmpty(this.currentPagePath) ?
                this.siteDto.getUrl() :
                this.currentPagePath;

        Set<PageDto> children = this.getChildren(currentLink);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        service.saveAllBySite(siteDto.getId(), children);
        total += children.size();
        System.out.printf("Current size: %s, total: %s, ActiveThreads: %s;%n", children.size(), total, Thread.activeCount());

        List<IndexingPageTask> newTasks = children
                .stream()
                .map(PageDto::getPath)
                .map(this::forkNewPageParseTask)
                .toList();

        newTasks.forEach(IndexingPageTask::join);
    }


    private IndexingPageTask forkNewPageParseTask(String newPageTaskLink) {
        this.sleep();
        IndexingPageTask indexingPageTask = new IndexingPageTask(this.siteDto, newPageTaskLink, this.linkStorage);
        indexingPageTask.fork();
        return indexingPageTask;
    }

    public Set<PageDto> getChildren(final String link) {
        try {
            Connection.Response response = jsoupConnection
                    .newRequest()
                    .url(link)
                    .execute();

            if (response == null || !this.isValidContentType(response.contentType())) {
                return Collections.emptySet();
            }

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

        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("siteUrl", this.siteDto.getUrl())
                    .addKeyValue("currentLink", link)
                    .log("Произошла ошибка во время парсинга страниц сайта");
            return Collections.emptySet();
        }
    }

    private boolean isLinkValid(final String link) {
        String trimLink = StringUtils.trim(link);
        return StringUtils.isNotEmpty(trimLink)
                && trimLink.contains(this.siteDto.getUrl())
                && !trimLink.contains("#")
                && trimLink.endsWith("/");
    }

    private boolean isValidContentType(final String contentType) {
        return StringUtils.isNotEmpty(contentType)
                && contentType.contains(RESPONSE_CONTENT_TYPE);
    }
}
