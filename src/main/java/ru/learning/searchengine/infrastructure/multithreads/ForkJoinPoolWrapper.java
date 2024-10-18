package ru.learning.searchengine.infrastructure.multithreads;

import ru.learning.searchengine.infrastructure.multithreads.impl.DefaultForkJoinPoolWrapperImpl;

import java.util.concurrent.ForkJoinTask;

public interface ForkJoinPoolWrapper<RETURN_VALUE> extends AutoCloseable {
    RETURN_VALUE invokeAndGet(ForkJoinTask<RETURN_VALUE> task);

    void close();

    static <RETURN_VALUE> ForkJoinPoolWrapper<RETURN_VALUE> getNew() {
        return new DefaultForkJoinPoolWrapperImpl<>();
    }

    void invoke(ForkJoinTask<RETURN_VALUE> task);
}
