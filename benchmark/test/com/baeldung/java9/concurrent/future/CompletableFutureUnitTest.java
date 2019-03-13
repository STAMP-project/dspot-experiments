package com.baeldung.java9.concurrent.future;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class CompletableFutureUnitTest {
    @Test
    public void testDelay() throws Exception {
        Object input = new Object();
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeAsync(() -> input, CompletableFuture.delayedExecutor(CompletableFuture, 1, TimeUnit.SECONDS));
        Thread.sleep(100);
        Assert.assertFalse(future.isDone());
        Thread.sleep(1000);
        Assert.assertTrue(future.isDone());
        Assert.assertSame(input, future.get());
    }

    @Test
    public void testTimeoutTriggered() throws Exception {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.orTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(1100);
        Assert.assertTrue(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
    }

    @Test
    public void testTimeoutNotTriggered() throws Exception {
        Object input = new Object();
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.orTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(100);
        future.complete(input);
        Thread.sleep(1000);
        Assert.assertTrue(future.isDone());
        Assert.assertSame(input, future.get());
    }

    @Test
    public void completeOnTimeout() throws Exception {
        Object input = new Object();
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeOnTimeout(input, 1, TimeUnit.SECONDS);
        Thread.sleep(1100);
        Assert.assertTrue(future.isDone());
        Assert.assertSame(input, future.get());
    }
}

