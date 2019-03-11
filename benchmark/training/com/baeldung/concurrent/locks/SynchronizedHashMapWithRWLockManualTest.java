package com.baeldung.concurrent.locks;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;
import org.junit.Test;


public class SynchronizedHashMapWithRWLockManualTest {
    @Test
    public void whenWriting_ThenNoReading() {
        SynchronizedHashMapWithRWLock object = new SynchronizedHashMapWithRWLock();
        final int threadCount = 3;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        executeWriterThreads(object, threadCount, service);
        TestCase.assertEquals(object.isReadLockAvailable(), false);
        service.shutdown();
    }

    @Test
    public void whenReading_ThenMultipleReadingAllowed() {
        SynchronizedHashMapWithRWLock object = new SynchronizedHashMapWithRWLock();
        final int threadCount = 5;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        executeReaderThreads(object, threadCount, service);
        TestCase.assertEquals(object.isReadLockAvailable(), true);
        service.shutdown();
    }
}

