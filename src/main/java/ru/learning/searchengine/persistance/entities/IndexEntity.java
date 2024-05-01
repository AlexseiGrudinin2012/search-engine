package ru.learning.searchengine.persistance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "index")
@Data
public class IndexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "index_id_seq")
    @SequenceGenerator(name = "index_id_seq", sequenceName = "index_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    @NotNull
    private PageEntity page;

    @ManyToOne
    @JoinColumn(name = "lemma_id")
    @NotNull
    private LemmaEntity lemma;

    @NotNull
    private float rank;
}


