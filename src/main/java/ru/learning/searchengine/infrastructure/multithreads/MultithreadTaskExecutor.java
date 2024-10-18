package ru.learning.searchengine.infrastructure.multithreads;

import java.util.concurrent.ForkJoinTask;

public interface MultithreadTaskExecutor<RETURN_VALUE> {

    void run(ForkJoinTask<RETURN_VALUE> action);

    void shutdownAll();
}
