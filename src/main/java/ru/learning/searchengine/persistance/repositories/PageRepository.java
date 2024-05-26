package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.learning.searchengine.persistance.entities.PageEntity;

import java.util.Optional;


public interface PageRepository extends JpaRepository<PageEntity, Long> {
    Long countBySiteId(Long siteId);

    Optional<PageEntity> findByPathAndSiteId(String path, Long siteId);

    //TRUNCATE быстрее ...
    @Modifying
    @Transactional
    @Query(value = "truncate table page CASCADE", nativeQuery = true)
    void truncatePages();
}
