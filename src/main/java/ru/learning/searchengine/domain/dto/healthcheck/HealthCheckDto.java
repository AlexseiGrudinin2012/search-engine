package ru.learning.searchengine.domain.dto.healthcheck;

import lombok.Data;

import java.util.Date;

@Data
public class HealthCheckDto {
    private final boolean isHealthy;
    private final Date date;

    public HealthCheckDto(boolean isHealthy) {
        this.isHealthy = isHealthy;
        date = new Date();
    }
}