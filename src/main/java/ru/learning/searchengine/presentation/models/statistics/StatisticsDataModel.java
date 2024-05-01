package ru.learning.searchengine.presentation.models.statistics;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsDataModel {
    private TotalStatisticsModel total;
    private List<DetailedStatisticsItemModel> detailed;
}
