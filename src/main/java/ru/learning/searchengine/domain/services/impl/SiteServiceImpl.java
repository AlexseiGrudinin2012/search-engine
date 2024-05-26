package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.mappers.SiteMapper;
import ru.learning.searchengine.persistance.entities.SiteEntity;
import ru.learning.searchengine.persistance.repositories.SiteRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    public List<SiteDto> getSiteList() {
        List<SiteEntity> siteEntityList = this.siteRepository.findAll();
        return CollectionUtils.isEmpty(siteEntityList) ?
                Collections.emptyList() :
                siteEntityList.stream().map(SiteMapper.INSTANCE::entityToDto).toList();
    }

    @Override
    public List<SiteDto> getSiteListByStatuses(List<SiteStatus> siteStatuses) {
        if (CollectionUtils.isEmpty(siteStatuses)) {
            return Collections.emptyList();
        }
        List<SiteEntity> siteEntityList = this.siteRepository.findByStatusIn(siteStatuses);
        if (CollectionUtils.isEmpty(siteEntityList)) {
            return Collections.emptyList();
        }
        return siteEntityList.stream().map(SiteMapper.INSTANCE::entityToDto).toList();
    }

    @Override
    public void save(SiteDto siteDto) {
        this.siteRepository.findById(siteDto.getId()).ifPresent(
                siteEntity ->
                        this.siteRepository.saveAndFlush(SiteMapper.INSTANCE.updateEntity(siteDto, siteEntity))
        );
    }
}
