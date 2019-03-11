package com.birbit.android.jobqueue;


import android.annotation.SuppressLint;
import com.birbit.android.jobqueue.timer.SystemTimer;
import java.util.concurrent.TimeUnit;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static JobManager.NS_PER_MS;


@RunWith(JUnit4.class)
public class SystemTimerTest {
    @SuppressLint({ "DIRECT_TIME_ACCESS", "SLEEP_IN_CODE" })
    @Test
    public void testNow() throws Throwable {
        SystemTimer timer = new SystemTimer();
        // noinspection DIRECT_TIME_ACCESS
        long startNs = System.nanoTime();
        // noinspection DIRECT_TIME_ACCESS
        long start = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
        // noinspection SLEEP_IN_CODE
        Thread.sleep(3000);
        assertReasonable(timer.nanoTime(), (((System.nanoTime()) - startNs) + start));
        SystemTimer timer2 = new SystemTimer();
        assertReasonable(timer2.nanoTime(), (((System.nanoTime()) - startNs) + start));
    }

    @SuppressLint("DIRECT_TIME_ACCESS")
    @Test
    public void testWaitOnObject() throws Throwable {
        SystemTimer timer = new SystemTimer();
        // noinspection DIRECT_TIME_ACCESS
        long startRealTime = System.nanoTime();
        long startNs = timer.nanoTime();
        long waitNs = (NS_PER_MS) * 3000;
        long waitUntil = startNs + waitNs;
        final Object object = new Object();
        synchronized(object) {
            timer.waitOnObjectUntilNs(object, waitUntil);
        }
        assertBetween(((System.nanoTime()) - startRealTime), waitNs, (waitNs + (2000 * (NS_PER_MS))));
    }

    static class Range extends BaseMatcher<Long> {
        final long min;

        final long max;

        public Range(long min, long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean matches(Object item) {
            long value = ((long) (item));
            return (value >= (min)) && (value <= (max));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("should be between").appendValue(min).appendText(" and ").appendValue(max);
        }
    }
}

