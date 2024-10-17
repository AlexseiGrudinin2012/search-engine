package ru.learning.searchengine.infrastructure.multithreads;

import java.util.concurrent.ForkJoinTask;

public interface ForkJoinPoolWrapper<RETURN_VALUE> extends AutoCloseable {
    RETURN_VALUE invokeAndGet(ForkJoinTask<RETURN_VALUE> task);

    void invoke(ForkJoinTask<Void> task);

    void close();
}
