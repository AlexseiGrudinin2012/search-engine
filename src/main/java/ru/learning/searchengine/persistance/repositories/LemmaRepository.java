package ru.learning.searchengine.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.learning.searchengine.persistance.entities.LemmaEntity;

public interface LemmaRepository  extends JpaRepository<LemmaEntity, Long> {

}
