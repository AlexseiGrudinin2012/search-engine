package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.LemmaDto;
import ru.learning.searchengine.persistance.entities.LemmaEntity;

@Mapper
public interface LemmaMapper {
    LemmaMapper INSTANCE = Mappers.getMapper(LemmaMapper.class);

    LemmaDto entityToDto(LemmaEntity lemmaEntity);
}
