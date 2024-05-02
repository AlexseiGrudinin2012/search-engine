package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.IndexItemEntity;

public interface IndexRepository extends JpaRepository<IndexItemEntity, Long> {

}
