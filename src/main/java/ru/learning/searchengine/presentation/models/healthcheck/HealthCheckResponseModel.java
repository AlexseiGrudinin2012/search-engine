package ru.learning.searchengine.presentation.models.healthcheck;

import lombok.Data;

import java.util.Date;

@Data
public class HealthCheckResponseModel {
    boolean isHealthy;
    Date date;
}
