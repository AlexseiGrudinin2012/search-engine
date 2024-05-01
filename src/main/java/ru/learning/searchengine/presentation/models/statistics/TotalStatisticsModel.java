package ru.learning.searchengine.presentation.models.statistics;

import lombok.Data;

@Data
public class TotalStatisticsModel {
    private int sites;
    private int pages;
    private int lemmas;
    private boolean indexing;
}
