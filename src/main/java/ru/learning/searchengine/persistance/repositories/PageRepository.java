package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.PageEntity;


public interface PageRepository extends JpaRepository<PageEntity, Long> {
        Long countBySiteId(Long siteId);
}
