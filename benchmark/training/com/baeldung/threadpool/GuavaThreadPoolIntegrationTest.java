package com.baeldung.threadpool;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class GuavaThreadPoolIntegrationTest {
    @Test
    public void whenExecutingTaskWithDirectExecutor_thenTheTaskIsExecutedInTheCurrentThread() {
        Executor executor = MoreExecutors.directExecutor();
        AtomicBoolean executed = new AtomicBoolean();
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executed.set(true);
        });
        Assert.assertTrue(executed.get());
    }

    @Test
    public void whenJoiningFuturesWithAllAsList_thenCombinedFutureCompletesAfterAllFuturesComplete() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
        ListenableFuture<String> future1 = listeningExecutorService.submit(() -> "Hello");
        ListenableFuture<String> future2 = listeningExecutorService.submit(() -> "World");
        String greeting = Futures.allAsList(future1, future2).get().stream().collect(Collectors.joining(" "));
        Assert.assertEquals("Hello World", greeting);
    }
}

