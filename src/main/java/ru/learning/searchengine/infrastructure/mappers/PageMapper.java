package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.PageDto;
import ru.learning.searchengine.persistance.entities.PageEntity;

@Mapper
public interface PageMapper {
    PageMapper INSTANCE = Mappers.getMapper(PageMapper.class);

    PageDto entityToDto(PageEntity pageEntity);

    PageEntity dtoToEntity(PageDto pageDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "site", ignore = true)
    PageEntity updateEntity(PageDto newValue, @MappingTarget PageEntity entityToUpdate);
}
