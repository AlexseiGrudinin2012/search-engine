package ru.learning.searchengine.domain.exceptions;

public class StatisticsNotFoundException extends RuntimeException{
    public StatisticsNotFoundException(String message) {
        super(message);
    }

    public StatisticsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
