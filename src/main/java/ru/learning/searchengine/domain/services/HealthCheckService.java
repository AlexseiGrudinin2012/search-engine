package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.domain.dto.healthcheck.HealthCheckDto;

public interface HealthCheckService {
    HealthCheckDto getHealthCheck();
}
