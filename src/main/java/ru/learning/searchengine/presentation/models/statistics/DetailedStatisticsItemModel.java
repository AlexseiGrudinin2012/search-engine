package ru.learning.searchengine.presentation.models.statistics;

import lombok.Data;
import ru.learning.searchengine.domain.enums.SiteStatus;

@Data
public class DetailedStatisticsItemModel {
    private String url;
    private String name;
    private SiteStatus status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;
}
