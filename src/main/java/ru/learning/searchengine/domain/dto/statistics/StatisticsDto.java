package ru.learning.searchengine.domain.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDto {
    private boolean result;
    private StatisticsDataDto statistics;
}
