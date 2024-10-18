package ru.learning.searchengine.infrastructure.multithreads.impl;

import org.springframework.stereotype.Component;
import ru.learning.searchengine.infrastructure.multithreads.ForkJoinPoolWrapper;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@Component
public
class DefaultForkJoinPoolWrapperImpl<RETURN_VALUE> implements ForkJoinPoolWrapper<RETURN_VALUE> {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Override
    public RETURN_VALUE invokeAndGet(ForkJoinTask<RETURN_VALUE> task) {
        return forkJoinPool.invoke(task);
    }

    @Override
    public void invoke(ForkJoinTask<RETURN_VALUE> task) {
        forkJoinPool.invoke(task);
    }

    @Override
    public void close() {
        forkJoinPool.shutdown();
    }
}
