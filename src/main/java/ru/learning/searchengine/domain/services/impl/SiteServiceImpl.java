package ru.learning.searchengine.domain.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.learning.searchengine.domain.dto.SiteDto;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.domain.services.SiteService;
import ru.learning.searchengine.infrastructure.mappers.SiteMapper;
import ru.learning.searchengine.persistance.entities.SiteEntity;
import ru.learning.searchengine.persistance.repositories.SiteRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    public List<SiteDto> getAllSites() {
        return siteRepository
                .findAll()
                .stream()
                .map(SiteMapper.INSTANCE::entityToDto)
                .toList();
    }


    @Transactional
    public synchronized void save(SiteDto fetchedSite) {
        Optional<SiteEntity> persistedSite = siteRepository.findById(fetchedSite.getId());
        siteRepository.save(
                persistedSite.isEmpty()
                        ? SiteMapper.INSTANCE.dtoToEntity(fetchedSite)
                        : SiteMapper.INSTANCE.updateEntity(fetchedSite, persistedSite.get())
        );
    }

    @Override
    public boolean isAllSitesIndexed() {
        return !siteRepository.existsAllByStatusIn(SiteStatus.getNonIndexedStatuses());
    }

    @Override
    public Optional<SiteDto> findSiteById(Long siteId) {
        return Optional.ofNullable(siteId)
                .map(siteRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SiteMapper.INSTANCE::entityToDto);
    }
}
