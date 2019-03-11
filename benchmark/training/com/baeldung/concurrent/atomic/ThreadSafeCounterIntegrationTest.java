package com.baeldung.concurrent.atomic;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class ThreadSafeCounterIntegrationTest {
    @Test
    public void givenMultiThread_whenSafeCounterWithLockIncrement() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        SafeCounterWithLock safeCounter = new SafeCounterWithLock();
        IntStream.range(0, 1000).forEach(( count) -> service.submit(safeCounter::increment));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1000, safeCounter.getValue());
    }

    @Test
    public void givenMultiThread_whenSafeCounterWithoutLockIncrement() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        SafeCounterWithoutLock safeCounter = new SafeCounterWithoutLock();
        IntStream.range(0, 1000).forEach(( count) -> service.submit(safeCounter::increment));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1000, safeCounter.getValue());
    }
}

