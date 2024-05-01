package ru.learning.searchengine.presentation.models.statistics;

import lombok.Data;

@Data
public class StatisticsResponseModel {
    private boolean result;
    private StatisticsDataModel statistics;
}
