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
@Table(name = "lemma")
@Data
public class LemmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lemma_id_seq")
    @SequenceGenerator(name = "lemma_id_seq", sequenceName = "lemma_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    @NotNull
    private SiteEntity site;

    @NotNull
    private String lemma;

    @NotNull
    private int frequency;
}
