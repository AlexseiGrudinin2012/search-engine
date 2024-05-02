package ru.learning.searchengine.infrastructure.exceptions;

public class SiteNotFoundException extends RuntimeException {
    final static String NOT_FOUND_SITE_MESSAGE = "Сайты для отображения статистики не найдены";

    public SiteNotFoundException() {
        super(NOT_FOUND_SITE_MESSAGE);
    }
}
