package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.learning.searchengine.domain.enums.SiteStatus;
import ru.learning.searchengine.persistance.entities.SiteEntity;

import java.util.List;

@Transactional
public interface SiteRepository extends JpaRepository<SiteEntity, Long> {
    List<SiteEntity> findByStatusIn(List<SiteStatus> siteStatus);

    boolean existsAllByStatusIn(List<SiteStatus> siteStatuses);
}
