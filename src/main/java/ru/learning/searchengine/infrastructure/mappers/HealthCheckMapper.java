package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.healthcheck.HealthCheckDto;
import ru.learning.searchengine.presentation.models.healthcheck.HealthCheckResponseModel;

@Mapper
public interface HealthCheckMapper {
    HealthCheckMapper INSTANCE = Mappers.getMapper(HealthCheckMapper.class);

    HealthCheckResponseModel dtoToModel(HealthCheckDto healthCheckDto);
}
