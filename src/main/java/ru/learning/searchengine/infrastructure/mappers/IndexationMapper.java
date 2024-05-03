package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.QueryStatusDto;
import ru.learning.searchengine.presentation.models.StatusResponseModel;

@Mapper
public interface IndexationMapper {
    IndexationMapper INSTANCE = Mappers.getMapper(IndexationMapper.class);

    StatusResponseModel dtoToResponseModel(QueryStatusDto indexationDto);
}
