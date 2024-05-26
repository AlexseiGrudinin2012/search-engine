package ru.learning.searchengine.infrastructure.multithreads;

import lombok.Getter;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ForkJoinPoolWrapper<RETURN_VALUE> implements AutoCloseable {

    private final ForkJoinPool forkJoinPool;

    @Getter
    private boolean isExecuting;

    public ForkJoinPoolWrapper() {
        this.isExecuting = true;
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
        this.isExecuting = false;
        this.forkJoinPool.shutdownNow();
    }
}
