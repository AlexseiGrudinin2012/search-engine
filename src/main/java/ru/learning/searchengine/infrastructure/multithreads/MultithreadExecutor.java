package ru.learning.searchengine.infrastructure.multithreads;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MultithreadExecutor {
    private final int defaultCoreCount = Runtime.getRuntime().availableProcessors() - 1;
    private final List<ExecutorService> executorServiceList = new ArrayList<>();
    @Value("${app.multithread.executor.core.count}")
    private String coreCount;

    public void runNewTask(Runnable runnable) {
        int processorsCount = NumberUtils.toInt(StringUtils.trim(this.coreCount), this.defaultCoreCount);
        ExecutorService executor = Executors.newFixedThreadPool(processorsCount);
        executor.submit(runnable);
        this.executorServiceList.add(executor);
    }

    public void shutdownTasksNow() {
        if (CollectionUtils.isEmpty(this.executorServiceList)) {
            return;
        }
        this.executorServiceList.forEach(ExecutorService::shutdownNow);
    }
}
