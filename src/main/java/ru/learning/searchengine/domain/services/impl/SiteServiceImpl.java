package ru.learning.searchengine.domain.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.mappers.SiteMapper;
import ru.learning.searchengine.persistance.entities.SiteEntity;
import ru.learning.searchengine.persistance.repositories.SiteRepository;

import java.util.Collections;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    public SiteServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<SiteDto> getSiteList() {
        List<SiteEntity> siteEntityList = this.siteRepository.findAll();
        return CollectionUtils.isEmpty(siteEntityList) ?
                Collections.emptyList() :
                siteEntityList.stream().map(SiteMapper.INSTANCE::entityToDto).toList();
    }
}
