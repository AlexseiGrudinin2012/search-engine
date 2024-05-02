package ru.learning.searchengine.domain.dto.statistics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisticsDataDto {
    private TotalStatisticsDto total;
    private List<DetailedStatisticsItemDto> detailed;
}
