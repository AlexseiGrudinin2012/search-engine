package ru.learning.searchengine.infrastructure.multithreadswrappers;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinPoolWrapper<RETURN_VALUE> implements AutoCloseable {

    private final ForkJoinPool forkJoinPool;

    public ForkJoinPoolWrapper() {
        this.forkJoinPool = new ForkJoinPool();
    }

    public RETURN_VALUE invokeAndGet(ForkJoinTask<RETURN_VALUE> task) {
        return this.forkJoinPool.invoke(task);
    }

    public void invoke(ForkJoinTask<Void> task) {
        this.forkJoinPool.invoke(task);
    }

    @Override
    public void close() {
        this.forkJoinPool.shutdownNow();
    }
}
