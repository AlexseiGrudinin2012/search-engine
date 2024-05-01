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
@Table(name = "page")
@Data
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_id_seq")
    @SequenceGenerator(name = "page_id_seq", sequenceName = "page_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    @NotNull
    private SiteEntity site;

    @NotNull
    private String path;

    @NotNull
    private int code;

    @NotNull
    private String content;
}
