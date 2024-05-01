package ru.learning.searchengine.presentation.models.statistics;

import lombok.Data;

@Data
public class DetailedStatisticsItemModel {
    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;
}
