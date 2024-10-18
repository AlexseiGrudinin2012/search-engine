package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.learning.searchengine.persistance.entities.PageEntity;

import java.util.List;

@Transactional
public interface PageRepository extends JpaRepository<PageEntity, Long> {
    Long countBySiteId(Long siteId);

    boolean existsByPath(String path);

    List<PageEntity> findBySiteId(Long siteId);
}
