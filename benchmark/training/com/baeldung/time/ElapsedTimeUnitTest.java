package com.baeldung.time;


import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;


public class ElapsedTimeUnitTest {
    @Test
    public void givenRunningTask_whenMeasuringTimeWithCurrentTimeMillis_thenGetElapsedTime() throws InterruptedException {
        long start = System.currentTimeMillis();
        ElapsedTimeUnitTest.simulateRunningTask();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        Assert.assertEquals(true, ((2000L <= timeElapsed) && (timeElapsed <= 3000L)));
    }

    @Test
    public void giveRunningTask_whenMeasuringTimeWithNanoTime_thenGetElapsedTime() throws InterruptedException {
        long start = System.nanoTime();
        ElapsedTimeUnitTest.simulateRunningTask();
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        Assert.assertEquals(true, ((2000000000L <= timeElapsed) && (timeElapsed <= 3000000000L)));
    }

    @Test
    public void givenRunningTask_whenMeasuringTimeWithStopWatch_thenGetElapsedTime() throws InterruptedException {
        StopWatch watch = new StopWatch();
        watch.start();
        ElapsedTimeUnitTest.simulateRunningTask();
        watch.stop();
        long timeElapsed = watch.getTime();
        Assert.assertEquals(true, ((2000L <= timeElapsed) && (timeElapsed <= 3000L)));
    }

    @Test
    public void givenRunningTask_whenMeasuringTimeWithInstantClass_thenGetElapsedTime() throws InterruptedException {
        Instant start = Instant.now();
        ElapsedTimeUnitTest.simulateRunningTask();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        Assert.assertEquals(true, ((2000L <= timeElapsed) && (timeElapsed <= 3000L)));
    }
}

