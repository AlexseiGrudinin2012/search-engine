package ru.learning.searchengine.presentation.controllers.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.learning.searchengine.domain.dto.healthcheck.HealthCheckDto;
import ru.learning.searchengine.domain.services.HealthCheckService;
import ru.learning.searchengine.infrastructure.mappers.HealthCheckMapper;
import ru.learning.searchengine.presentation.models.healthcheck.HealthCheckResponseModel;

@RestController()
@RequestMapping("/api/hc")
public class HealthCheckController {
    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping()
    public HealthCheckResponseModel getHealthCheck() {
        HealthCheckDto healthCheckDto = this.healthCheckService.getHealthCheck();
        return HealthCheckMapper.INSTANCE.dtoToModel(healthCheckDto);
    }
}