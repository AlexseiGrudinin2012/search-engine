package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.PageEntity;

import java.util.Optional;


public interface PageRepository extends JpaRepository<PageEntity, Long> {
        Long countBySiteId(Long siteId);

        Optional<PageEntity> findByPathAndSiteId(String path, Long siteId);

        void deleteAllBySiteId(Long siteId);
}
