package ru.learning.searchengine.presentation.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusResponseModel {
    private String error;

    private final boolean result;
}
