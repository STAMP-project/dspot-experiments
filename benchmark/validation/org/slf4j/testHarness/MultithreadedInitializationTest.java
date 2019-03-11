package org.slf4j.testHarness;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerAccessingThread;
import org.slf4j.LoggerFactory;


public abstract class MultithreadedInitializationTest {
    protected static final int THREAD_COUNT = 4 + ((Runtime.getRuntime().availableProcessors()) * 2);

    private final List<Logger> createdLoggers = Collections.synchronizedList(new ArrayList<Logger>());

    private final AtomicLong eventCount = new AtomicLong(0);

    private final CyclicBarrier barrier = new CyclicBarrier(((MultithreadedInitializationTest.THREAD_COUNT) + 1));

    int diff = new Random().nextInt(10000);

    @Test
    public void multiThreadedInitialization() throws InterruptedException, BrokenBarrierException {
        @SuppressWarnings("unused")
        LoggerAccessingThread[] accessors = harness();
        Logger logger = LoggerFactory.getLogger(getClass().getName());
        logger.info("hello");
        eventCount.getAndIncrement();
        assertAllSubstLoggersAreFixed();
        long recordedEventCount = getRecordedEventCount();
        int LENIENCY_COUNT = 20;
        long expectedEventCount = (eventCount.get()) + (extraLogEvents());
        Assert.assertTrue(((expectedEventCount + " >= ") + recordedEventCount), (expectedEventCount >= recordedEventCount));
        Assert.assertTrue(((((expectedEventCount + " < ") + recordedEventCount) + "+") + LENIENCY_COUNT), (expectedEventCount < (recordedEventCount + LENIENCY_COUNT)));
    }
}

