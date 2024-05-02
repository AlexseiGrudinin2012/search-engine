package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.ErrorDetailsDto;
import ru.learning.searchengine.presentation.models.handlers.ErrorDetailsResponseModel;

@Mapper
public interface ErrorDetailsMapper {
    ErrorDetailsMapper INSTANCE = Mappers.getMapper(ErrorDetailsMapper.class);

    @Mapping(source = "errorMessage", target = "error")
    ErrorDetailsResponseModel dtoToResponseModel(ErrorDetailsDto errorDetailsDto);
}
