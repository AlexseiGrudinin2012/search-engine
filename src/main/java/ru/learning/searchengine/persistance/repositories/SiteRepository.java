package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.SiteEntity;

public interface SiteRepository extends JpaRepository<SiteEntity, Long> {

}
