package ru.learning.searchengine.infrastructure.multithreads;

public interface MultithreadTaskExecutor {

    void run(Runnable runnable);

    void shutdownAll();
}
