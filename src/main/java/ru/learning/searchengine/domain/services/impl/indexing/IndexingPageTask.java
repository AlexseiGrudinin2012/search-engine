package ru.learning.searchengine.domain.services.impl.indexing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.IndexingParsingService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Slf4j
public class IndexingPageTask extends RecursiveTask<Boolean> {

    private final Set<String> linkStorage;
    private final String currentPagePath;
    private final SiteDto siteDto;
    private static Connection jsoupConnection;
    private static final String RESPONSE_CONTENT_TYPE = "text/html";

    private static IndexingParsingService indexingParsingService;

    public IndexingPageTask(
            SiteDto siteDto,
            Connection connection,
            IndexingParsingService indexingParsingService
    ) {
        this(siteDto, null, ConcurrentHashMap.newKeySet());
        jsoupConnection = connection;
        IndexingPageTask.indexingParsingService = indexingParsingService;
    }

    private IndexingPageTask(
            SiteDto siteDto,
            String currentPagePath,
            Set<String> linkStorage
    ) {
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
    protected Boolean compute() {
        String currentLink = StringUtils.isEmpty(this.currentPagePath) ?
                this.siteDto.getUrl() :
                this.currentPagePath;

        Set<PageDto> children = this.getChildren(currentLink);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        if (SiteStatus.FAILED.equals(siteDto.getStatus())) {
            this.siteDto.setStatus(SiteStatus.INDEXING);
        }
        this.siteDto.setStatusTime(new Date());
        IndexingPageTask.indexingParsingService.saveAllBySite(this.siteDto, children);

        List<IndexingPageTask> newTasks = children
                .stream()
                .map(PageDto::getPath)
                .map(this::forkNewPageParseTask)
                .toList();

        newTasks.forEach(IndexingPageTask::join);
        return true;
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
            String errorMessage = String.format("Ошибка во время парсинга страницы: %s", e.getMessage());
            log.atError()
                    .setCause(e)
                    .addKeyValue("siteUrl", this.siteDto.getUrl())
                    .addKeyValue("currentLink", link)
                    .log(errorMessage);
            this.siteDto.setStatus(SiteStatus.FAILED);
            this.siteDto.setLastError(errorMessage);
            IndexingPageTask.indexingParsingService.saveAllBySite(this.siteDto);
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
