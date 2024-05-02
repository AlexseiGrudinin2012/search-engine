package ru.learning.searchengine.presentation.models.handlers;

import lombok.Data;

@Data
public class ErrorDetailsResponseModel {
    private String error;

    private boolean result;
}
