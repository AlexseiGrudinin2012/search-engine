package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.IndexEntity;

public interface IndexRepository extends JpaRepository<IndexEntity, Long> {

}
