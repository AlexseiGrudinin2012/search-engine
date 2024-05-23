package ru.learning.searchengine.domain.services;

public interface IndexingService {
    void start();

    void stop();

    boolean isStarted();

    boolean isAllSitesIndexed();
}
