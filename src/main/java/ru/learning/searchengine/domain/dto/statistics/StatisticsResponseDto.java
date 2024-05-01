package ru.learning.searchengine.domain.dto.statistics;

import lombok.Data;

@Data
public class StatisticsResponseDto {
    private boolean result;
    private StatisticsDataDto statistics;
}
