package ru.learning.searchengine.domain.dto.statistics;

import lombok.Builder;
import lombok.Data;
import ru.learning.searchengine.domain.enums.SiteStatus;

@Data
@Builder
public class DetailedStatisticsItemDto {
    private String url;
    private String name;
    private SiteStatus status;
    private long statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
