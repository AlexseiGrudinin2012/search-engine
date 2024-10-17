package ru.learning.searchengine.infrastructure.multithreads.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.learning.searchengine.infrastructure.multithreads.MultithreadTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MultithreadTaskExecutorImpl implements MultithreadTaskExecutor {
    @Value("${app.multithread.executor.threads.max.count}")
    private String threadsCount;
    private final int defaultCoreCount = Runtime
            .getRuntime()
            .availableProcessors() - 1;
    private final ExecutorService executor = Executors.newFixedThreadPool(
            NumberUtils.toInt(
                    StringUtils.trim(threadsCount),
                    defaultCoreCount
            )
    );

    public void run(Runnable runnable) {
        synchronized (executor) {
            executor.submit(runnable);
        }
    }

    public void shutdownAll() {
        synchronized (executor) {
            executor.shutdownNow();
        }
    }
}
