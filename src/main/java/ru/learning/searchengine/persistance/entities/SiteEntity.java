package ru.learning.searchengine.persistance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.learning.searchengine.domain.enums.SiteStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "site")
@Data
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_id_seq")
    @SequenceGenerator(name = "site_id_seq", sequenceName = "site_id_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SiteStatus status;

    @NotNull
    private LocalDateTime statusTime;

    private String lastError;

    @NotNull
    private String url;

    @NotNull
    private String name;
}
