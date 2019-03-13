package com.baeldung.guava;


import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class MoreExecutorsUnitTest {
    @Test
    public void whenExecutingRunnableInThreadPool_shouldLogAllThreadsExecutions() throws Exception {
        ConcurrentHashMap<String, Boolean> threadExecutions = new ConcurrentHashMap<>();
        Runnable logThreadRun = () -> threadExecutions.put(Thread.currentThread().getName(), true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(logThreadRun);
        executorService.submit(logThreadRun);
        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(threadExecutions.get("pool-1-thread-1"));
        Assert.assertTrue(threadExecutions.get("pool-1-thread-2"));
    }

    @Test
    public void whenExecutingRunnableInDirectExecutor_shouldLogThreadExecution() throws Exception {
        ConcurrentHashMap<String, Boolean> threadExecutions = new ConcurrentHashMap<>();
        Runnable logThreadRun = () -> threadExecutions.put(Thread.currentThread().getName(), true);
        Executor executor = MoreExecutors.directExecutor();
        executor.execute(logThreadRun);
        Assert.assertTrue(threadExecutions.get("main"));
    }

    @Test
    public void whenExecutingRunnableInListeningExecutor_shouldLogThreadExecution() throws Exception {
        ConcurrentHashMap<String, Boolean> threadExecutions = new ConcurrentHashMap<>();
        Runnable logThreadRun = () -> threadExecutions.put(Thread.currentThread().getName(), true);
        ListeningExecutorService executor = MoreExecutors.newDirectExecutorService();
        executor.execute(logThreadRun);
        Assert.assertTrue(threadExecutions.get("main"));
    }
}

