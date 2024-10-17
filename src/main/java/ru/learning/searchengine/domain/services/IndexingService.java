package ru.learning.searchengine.domain.services;

public interface IndexingService {
    void startIndexation();

    void stopIndexation();

    boolean isIndexationStarted();
}
