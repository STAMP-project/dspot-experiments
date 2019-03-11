package com.baeldung.concurrent.locks;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;
import org.junit.Test;


public class SharedObjectWithLockManualTest {
    @Test
    public void whenLockAcquired_ThenLockedIsTrue() {
        final SharedObjectWithLock object = new SharedObjectWithLock();
        final int threadCount = 2;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        executeThreads(object, threadCount, service);
        TestCase.assertEquals(true, object.isLocked());
        service.shutdown();
    }

    @Test
    public void whenLocked_ThenQueuedThread() {
        final int threadCount = 4;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        final SharedObjectWithLock object = new SharedObjectWithLock();
        executeThreads(object, threadCount, service);
        TestCase.assertEquals(object.hasQueuedThreads(), true);
        service.shutdown();
    }

    @Test
    public void whenGetCount_ThenCorrectCount() throws InterruptedException {
        final int threadCount = 4;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        final SharedObjectWithLock object = new SharedObjectWithLock();
        executeThreads(object, threadCount, service);
        Thread.sleep(1000);
        TestCase.assertEquals(object.getCounter(), 4);
        service.shutdown();
    }
}

