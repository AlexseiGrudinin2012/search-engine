package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.statistics.StatisticsDto;
import ru.learning.searchengine.presentation.models.statistics.StatisticsResponseModel;

@Mapper
public interface StatisticsMapper {
    StatisticsMapper INSTANCE = Mappers.getMapper(StatisticsMapper.class);

    StatisticsResponseModel dtoToModel(StatisticsDto statisticsDto);
}
