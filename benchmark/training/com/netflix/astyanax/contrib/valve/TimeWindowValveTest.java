package com.netflix.astyanax.contrib.valve;


import RequestStatus.PastWindow;
import RequestStatus.Permitted;
import com.netflix.astyanax.contrib.valve.TimeWindowValve.RequestStatus;
import junit.framework.Assert;
import org.junit.Test;


public class TimeWindowValveTest {
    @Test
    public void testSingleThread1SecWindow() throws Exception {
        TimeWindowValve window = new TimeWindowValve(1000L, System.currentTimeMillis(), 1000);
        testSingleThread(window, 100000, 1000);
    }

    @Test
    public void testSingleThread100MsWindow() throws Exception {
        TimeWindowValve window = new TimeWindowValve(1000L, System.currentTimeMillis(), 100);
        testSingleThread(window, 100000, 1000);
    }

    @Test
    public void testMultipleThreads1SecWindow() throws Exception {
        final TimeWindowValve window = new TimeWindowValve(100000L, System.currentTimeMillis(), 1000);
        testMultipleThreads(window, 100000L, 300);
    }

    @Test
    public void testMultipleThreads100MsWindow() throws Exception {
        final TimeWindowValve window = new TimeWindowValve(10000L, System.currentTimeMillis(), 500);
        testMultipleThreads(window, 10000L, 300);
    }

    @Test
    public void testPastWindow() throws Exception {
        final TimeWindowValve window = new TimeWindowValve(100000L, System.currentTimeMillis(), 100);
        for (int i = 0; i < 1000; i++) {
            RequestStatus status = window.decrementAndCheckQuota();
            Assert.assertEquals(Permitted, status);
        }
        Thread.sleep(150);
        for (int i = 0; i < 1000; i++) {
            RequestStatus status = window.decrementAndCheckQuota();
            Assert.assertEquals(PastWindow, status);
        }
    }
}

