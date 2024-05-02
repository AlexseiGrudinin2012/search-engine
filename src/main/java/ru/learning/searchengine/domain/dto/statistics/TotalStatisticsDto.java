package ru.learning.searchengine.domain.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalStatisticsDto {
    private Integer sites;
    private long pages;
    private long lemmas;
    private boolean indexing;
}
