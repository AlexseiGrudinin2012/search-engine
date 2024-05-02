package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.IndexItemDto;
import ru.learning.searchengine.persistance.entities.IndexItemEntity;

@Mapper
public interface IndexItemMapper {
    IndexItemMapper INSTANCE = Mappers.getMapper(IndexItemMapper.class);

    IndexItemDto entityToDto(IndexItemEntity indexItemEntity);
}
