package ru.learning.searchengine.presentation.controllers.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.learning.searchengine.domain.services.StatisticsService;
import ru.learning.searchengine.presentation.models.statistics.StatisticsResponseModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Метод возвращает информацию о статистике запросов к сайтам
     *
     * @return - модель статистики или исключение
     * {@link ru.learning.searchengine.infrastructure.handlers.ControllersExceptionHandler}
     */
    @GetMapping
    public ResponseEntity<StatisticsResponseModel> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
