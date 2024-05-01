package ru.learning.searchengine.persistance.repositories.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.learning.searchengine.persistance.repositories.HealthCheckRepository;

@Repository
public class HealthCheckRepositoryImpl implements HealthCheckRepository {
    private final JdbcTemplate jdbcTemplate;

    public HealthCheckRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void performDbHealthCheck() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class); // Выполнение нативного SQL-запроса
    }
}
