package ru.learning.searchengine.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.persistance.entities.SiteEntity;

@Mapper
public interface SiteMapper {
    SiteMapper INSTANCE = Mappers.getMapper(SiteMapper.class);

    SiteDto entityToDto(SiteEntity siteEntity);
}
