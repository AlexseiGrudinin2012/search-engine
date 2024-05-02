package ru.learning.searchengine.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndexItemDto {

    private Long id;

    private PageDto page;

    private LemmaDto lemma;

    private float rank;
}


