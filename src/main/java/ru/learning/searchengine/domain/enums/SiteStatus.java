package ru.learning.searchengine.domain.enums;

import java.util.List;

public enum SiteStatus {
    INDEXING, //Индексируется
    INDEXED, //Индексация завершена
    FAILED; //Остановлено или ошибка

    public static List<SiteStatus> getNonIndexedStatuses() {
        return List.of(INDEXING, FAILED);
    }
}
