package ru.learning.searchengine.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class PageDto {
    private Long id;

    private SiteDto site;

    private String path;

    private Integer code;

    private String content;
}
