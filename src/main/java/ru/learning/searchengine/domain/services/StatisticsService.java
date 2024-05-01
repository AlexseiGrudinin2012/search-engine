package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.statistics.StatisticsResponseDto;


public interface StatisticsService {
    StatisticsResponseDto getStatistics();
}
