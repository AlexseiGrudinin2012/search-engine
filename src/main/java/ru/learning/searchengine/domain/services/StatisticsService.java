package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.presentation.models.statistics.StatisticsResponseModel;


public interface StatisticsService {
    StatisticsResponseModel getStatistics();
}
