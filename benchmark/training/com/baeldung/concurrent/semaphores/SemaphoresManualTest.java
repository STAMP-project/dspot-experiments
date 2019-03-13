package com.baeldung.concurrent.semaphores;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class SemaphoresManualTest {
    // ========= login queue ======
    @Test
    public void givenLoginQueue_whenReachLimit_thenBlocked() throws InterruptedException {
        final int slots = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(slots);
        final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, slots).forEach(( user) -> executorService.execute(loginQueue::tryLogin));
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, loginQueue.availableSlots());
        Assert.assertFalse(loginQueue.tryLogin());
    }

    @Test
    public void givenLoginQueue_whenLogout_thenSlotsAvailable() throws InterruptedException {
        final int slots = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(slots);
        final LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
        IntStream.range(0, slots).forEach(( user) -> executorService.execute(loginQueue::tryLogin));
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, loginQueue.availableSlots());
        loginQueue.logout();
        Assert.assertTrue(((loginQueue.availableSlots()) > 0));
        Assert.assertTrue(loginQueue.tryLogin());
    }

    // ========= delay queue =======
    @Test
    public void givenDelayQueue_whenReachLimit_thenBlocked() throws InterruptedException {
        final int slots = 50;
        final ExecutorService executorService = Executors.newFixedThreadPool(slots);
        final DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
        IntStream.range(0, slots).forEach(( user) -> executorService.execute(delayQueue::tryAdd));
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, delayQueue.availableSlots());
        Assert.assertFalse(delayQueue.tryAdd());
    }

    @Test
    public void givenDelayQueue_whenTimePass_thenSlotsAvailable() throws InterruptedException {
        final int slots = 50;
        final ExecutorService executorService = Executors.newFixedThreadPool(slots);
        final DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
        IntStream.range(0, slots).forEach(( user) -> executorService.execute(delayQueue::tryAdd));
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, delayQueue.availableSlots());
        Thread.sleep(1000);
        Assert.assertTrue(((delayQueue.availableSlots()) > 0));
        Assert.assertTrue(delayQueue.tryAdd());
    }

    // ========== mutex ========
    @Test
    public void whenMutexAndMultipleThreads_thenBlocked() throws InterruptedException {
        final int count = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(count);
        final CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count).forEach(( user) -> executorService.execute(() -> {
            try {
                counter.increase();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
        Assert.assertTrue(counter.hasQueuedThreads());
    }

    @Test
    public void givenMutexAndMultipleThreads_ThenDelay_thenCorrectCount() throws InterruptedException {
        final int count = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(count);
        final CounterUsingMutex counter = new CounterUsingMutex();
        IntStream.range(0, count).forEach(( user) -> executorService.execute(() -> {
            try {
                counter.increase();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
        Assert.assertTrue(counter.hasQueuedThreads());
        Thread.sleep(5000);
        Assert.assertFalse(counter.hasQueuedThreads());
        Assert.assertEquals(count, counter.getCount());
    }
}

