package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.presentation.models.healthcheck.HealthCheckResponseModel;

public interface HealthCheckService {
    HealthCheckResponseModel getHealthCheck();
}
