package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.healthcheck.HealthCheckDto;
import ru.learning.searchengine.domain.services.HealthCheckService;
import ru.learning.searchengine.infrastructure.mappers.HealthCheckMapper;
import ru.learning.searchengine.persistance.repositories.HealthCheckRepository;
import ru.learning.searchengine.presentation.models.healthcheck.HealthCheckResponseModel;

@Service
@RequiredArgsConstructor
public class HealthCheckServiceImpl implements HealthCheckService {
    private final HealthCheckRepository healthCheckRepository;

    @Override
    public HealthCheckResponseModel getHealthCheck() {
        boolean isDbAlive = performDBHealthCheck();
        return HealthCheckMapper.INSTANCE.dtoToModel(
                new HealthCheckDto(isDbAlive)
        );
    }

    public boolean performDBHealthCheck() {
        try {
            healthCheckRepository.performDbHealthCheck(); // Вызов запроса SELECT 1
            return true; // Если запрос выполнен успешно, возвращаем true
        } catch (Exception e) {
            return false; // Если произошла ошибка, возвращаем false
        }
    }
}
