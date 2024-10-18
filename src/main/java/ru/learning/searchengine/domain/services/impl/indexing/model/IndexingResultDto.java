package ru.learning.searchengine.domain.services.impl.indexing.model;

import lombok.Builder;
import lombok.Data;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.domain.dto.SiteDto;

import java.util.Set;

@Data
@Builder
public class IndexingResultDto {
    Set<PageDto> pages;
    SiteDto site;
}
