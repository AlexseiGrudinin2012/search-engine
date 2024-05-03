package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.learning.searchengine.domain.dto.healthcheck.HealthCheckDto;
import ru.learning.searchengine.domain.services.HealthCheckService;
import ru.learning.searchengine.persistance.repositories.HealthCheckRepository;

@Service
@RequiredArgsConstructor
public class HealthCheckServiceImpl implements HealthCheckService {
    private final HealthCheckRepository healthCheckRepository;

    @Override
    public HealthCheckDto getHealthCheck() {
        boolean isDbAlive = this.performDBHealthCheck();
        return new HealthCheckDto(isDbAlive);
    }

    public boolean performDBHealthCheck() {
        try {
            this.healthCheckRepository.performDbHealthCheck(); // Вызов запроса SELECT 1
            return true; // Если запрос выполнен успешно, возвращаем true
        } catch (Exception e) {
            return false; // Если произошла ошибка, возвращаем false
        }
    }
}
