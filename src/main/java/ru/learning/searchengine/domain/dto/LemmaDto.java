package ru.learning.searchengine.domain.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LemmaDto {

    private Long id;

    private SiteDto site;

    private String lemma;

    private Integer frequency;
}
