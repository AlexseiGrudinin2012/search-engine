package ru.learning.searchengine.domain.services;

import ru.learning.searchengine.presentation.models.StatusResponseModel;

public interface IndexingService {
    StatusResponseModel startIndexation();

    StatusResponseModel stopIndexation();
}
