package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.mappers.SiteMapper;
import ru.learning.searchengine.persistance.repositories.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    public List<SiteDto> getSiteList() {
        return this.siteRepository.findAll()
                .stream()
                .map(SiteMapper.INSTANCE::entityToDto)
                .toList();
    }

    @Override
    public List<SiteDto> getSiteListByStatuses(List<SiteStatus> siteStatuses) {
        return this.siteRepository.findByStatusIn(siteStatuses)
                .stream()
                .map(SiteMapper.INSTANCE::entityToDto)
                .toList();
    }

    @Override
    @Transactional
    public synchronized void save(SiteDto siteDto) {
        this.siteRepository.findById(siteDto.getId())
                .ifPresent(
                        e ->
                                this.siteRepository.save(
                                        SiteMapper.INSTANCE.updateEntity(siteDto, e)
                                )
                );
    }

    @Override
    public boolean isAllSitesIndexed() {
        return !this.siteRepository.existsAllByStatusIn(SiteStatus.getNonIndexedStatuses());
    }
}
