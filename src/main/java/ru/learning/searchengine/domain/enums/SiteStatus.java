package ru.learning.searchengine.domain.enums;

import java.util.List;

public enum SiteStatus {
    INDEXING,
    INDEXED,
    FAILED;

    public static List<SiteStatus> getNonIndexedStatuses() {
        return List.of(INDEXING, FAILED);
    }
}
