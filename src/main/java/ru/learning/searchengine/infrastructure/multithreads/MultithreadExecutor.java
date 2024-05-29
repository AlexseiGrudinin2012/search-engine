package ru.learning.searchengine.infrastructure.multithreads;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MultithreadExecutor {
    private final int defaultCoreCount = Runtime.getRuntime().availableProcessors() - 1;
    private final Set<ExecutorService> executorServiceList = ConcurrentHashMap.newKeySet();
    @Value("${app.multithread.executor.threads.max.count}")
    private String threadsCount;

    public void runNewTask(Runnable runnable) {
        int processorsCount = NumberUtils.toInt(StringUtils.trim(this.threadsCount), this.defaultCoreCount);
        synchronized (this.executorServiceList) {
            ExecutorService executor = Executors.newFixedThreadPool(processorsCount);
            executor.submit(runnable);
            this.executorServiceList.add(executor);
        }
    }

    public void shutdownTasksNow() {
        if (CollectionUtils.isEmpty(this.executorServiceList)) {
            return;
        }
        synchronized (this.executorServiceList) {
            this.executorServiceList.forEach(ExecutorService::shutdownNow);
            this.executorServiceList.clear();
        }
    }
}
