package ru.learning.searchengine.infrastructure.multithreads.impl;

import org.springframework.stereotype.Component;
import ru.learning.searchengine.infrastructure.multithreads.ForkJoinPoolWrapper;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinTask;

@Component
public class MultithreadTaskExecutorImpl<RETURN_VALUE> implements MultithreadTaskExecutor<RETURN_VALUE> {
    private final int DEFAULT_CORE_COUNT = Runtime.getRuntime().availableProcessors() - 1;
    private final List<ForkJoinPoolWrapper<RETURN_VALUE>> pools = new ArrayList<>();
    private ExecutorService executor;

    public MultithreadTaskExecutorImpl() {
        this.executor = createExecutor();
    }

    private ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(DEFAULT_CORE_COUNT);
    }

    @Override
    public void run(ForkJoinTask<RETURN_VALUE> action) {
        if (executor.isShutdown()) {
            restartExecutor();
        }
        executor.submit(() -> runTaskWithNewPoolWrapper(action));
    }

    @Override
    public void shutdownAll() {
        executor.shutdownNow();
        pools.forEach(ForkJoinPoolWrapper::close);
        pools.clear();
    }

    @Override
    public boolean isPoolSizeEmpty() {
        synchronized (pools) {
            return pools.isEmpty();
        }
    }

    private void runTaskWithNewPoolWrapper(ForkJoinTask<RETURN_VALUE> action) {
        synchronized (pools) {
            try (ForkJoinPoolWrapper<RETURN_VALUE> forkJoinPoolWrapper = ForkJoinPoolWrapper.getNew()) {
                pools.add(forkJoinPoolWrapper);
                forkJoinPoolWrapper.invoke(action);
            } finally {
                pools.removeIf(pools::contains);
            }
        }
    }

    private void restartExecutor() {
        shutdownAll();
        this.executor = createExecutor();
    }
}
