/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class TimingInfoTest {
    @Test
    public void startEndTiming() {
        // Start timing
        final long startTimeNano = System.nanoTime();
        final long startTimeMilli = System.currentTimeMillis();
        TimingInfo[] tis = new TimingInfo[]{ TimingInfo.startTimingFullSupport(), TimingInfo.startTiming() };
        for (TimingInfo ti : tis) {
            Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
            Assert.assertTrue(((ti.getStartTimeNano()) >= startTimeNano));
            Assert.assertTrue(((ti.getStartEpochTimeMilli()) >= startTimeMilli));
            // End time is not known
            Assert.assertFalse(ti.isEndTimeKnown());
            Assert.assertTrue(((ti.getEndTimeNano()) == (TimingInfo.UNKNOWN)));
            Assert.assertTrue(((ti.getEndEpochTimeMilli()) == (TimingInfo.UNKNOWN)));
            Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
            Assert.assertTrue(((ti.getTimeTakenMillis()) == (TimingInfo.UNKNOWN)));
            Assert.assertTrue(((ti.getElapsedTimeMillis()) == (TimingInfo.UNKNOWN)));
            // End timing
            ti.endTiming();
            Assert.assertTrue(ti.isEndTimeKnown());
            Assert.assertTrue(((ti.getEndTimeNano()) >= startTimeNano));
            Assert.assertTrue(((ti.getEndEpochTimeMilli()) >= startTimeMilli));
            Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
            Assert.assertTrue(((ti.getTimeTakenMillis()) >= 0));
            Assert.assertTrue(((ti.getElapsedTimeMillis()) >= 0));
        }
    }

    @Test
    public void newTimingWithClockTime() throws InterruptedException {
        final long startTimeMilli = System.currentTimeMillis();
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeMilli, startTimeNano, endTimeNano);
        Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) == startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilli()) == startTimeMilli));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNano()) > startTimeNano));
        Assert.assertTrue(((ti.getEndEpochTimeMilli()) >= startTimeMilli));
        Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
        Assert.assertTrue(((ti.getTimeTakenMillis()) >= 0));
        Assert.assertTrue(((ti.getElapsedTimeMillis()) >= 0));
    }

    @Test
    public void newTimingWithNoClockTime() throws InterruptedException {
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeNano, endTimeNano);
        Assert.assertFalse(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) == startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilli()) == (TimingInfo.UNKNOWN)));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNano()) > startTimeNano));
        Assert.assertTrue(((ti.getEndEpochTimeMilli()) == (TimingInfo.UNKNOWN)));
        Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
        Assert.assertTrue(((ti.getTimeTakenMillis()) >= 0));
        Assert.assertTrue(((ti.getElapsedTimeMillis()) >= 0));
    }

    // Test the absurd case when the start/end times were insanely swapped
    // perhaps by accident ?
    @Test
    public void absurdTimingWithNoClock() throws InterruptedException {
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        // absurdly swap the start/end times
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(endTimeNano, startTimeNano);
        Assert.assertFalse(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) > startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilli()) == (TimingInfo.UNKNOWN)));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNano()) < endTimeNano));
        Assert.assertTrue(((ti.getEndEpochTimeMilli()) == (TimingInfo.UNKNOWN)));
        Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
        double double_diff = ((double) (TimeUnit.NANOSECONDS.toMicros((startTimeNano - endTimeNano)))) / 1000.0;
        Assert.assertTrue(((ti.getTimeTakenMillis()) == double_diff));
        long long_diff = TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano));
        Assert.assertTrue(((ti.getElapsedTimeMillis()) == long_diff));
    }

    // Test the absurd case when the start/end times were insanely swapped
    // perhaps by accident
    @Test
    public void absurdTimingWithClock() throws InterruptedException {
        final long startTimeMilli = System.currentTimeMillis();
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        // absurdly swap the start/end times
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeMilli, endTimeNano, startTimeNano);
        Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) > startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilli()) == startTimeMilli));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNano()) < endTimeNano));
        long end_epoch_time = startTimeMilli + (TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano)));
        Assert.assertTrue(((ti.getEndEpochTimeMilli()) == end_epoch_time));
        Assert.assertTrue(((ti.getEndTime()) == (ti.getEndEpochTimeMilli())));
        double double_diff = ((double) (TimeUnit.NANOSECONDS.toMicros((startTimeNano - endTimeNano)))) / 1000.0;
        Assert.assertTrue(((ti.getTimeTakenMillis()) == double_diff));
        long long_diff = TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano));
        Assert.assertTrue(((ti.getElapsedTimeMillis()) == long_diff));
    }

    @Test
    public void startEndTimingIfKnown() {
        // Start timing
        final long startTimeNano = System.nanoTime();
        final long startTimeMilli = System.currentTimeMillis();
        TimingInfo ti = TimingInfo.startTimingFullSupport();
        Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) >= startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilliIfKnown().longValue()) >= startTimeMilli));
        // End time is not known
        Assert.assertFalse(ti.isEndTimeKnown());
        Assert.assertNull(ti.getEndTimeNanoIfKnown());
        Assert.assertNull(ti.getEndEpochTimeMilliIfKnown());
        Assert.assertNull(ti.getTimeTakenMillisIfKnown());
        // End timing
        ti.endTiming();
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNanoIfKnown().longValue()) >= startTimeNano));
        Assert.assertTrue(((ti.getEndEpochTimeMilliIfKnown().longValue()) >= startTimeMilli));
        Assert.assertTrue(((ti.getEndEpochTimeMilli()) == (ti.getEndEpochTimeMilliIfKnown().longValue())));
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().longValue()) >= 0));
    }

    @Test
    public void newTimingWithClockTimeIfKnown() throws InterruptedException {
        final long startTimeMilli = System.currentTimeMillis();
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeMilli, startTimeNano, endTimeNano);
        Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) == startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilliIfKnown().longValue()) == startTimeMilli));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNanoIfKnown().longValue()) > startTimeNano));
        Assert.assertTrue(((ti.getEndEpochTimeMilliIfKnown().longValue()) >= startTimeMilli));
        Assert.assertTrue(((ti.getEndEpochTimeMilliIfKnown().longValue()) == (ti.getEndEpochTimeMilli())));
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().longValue()) >= 0));
    }

    @Test
    public void newTimingWithNoClockTimeIfKnown() throws InterruptedException {
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeNano, endTimeNano);
        Assert.assertFalse(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) == startTimeNano));
        Assert.assertNull(ti.getStartEpochTimeMilliIfKnown());
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNanoIfKnown().longValue()) > startTimeNano));
        Assert.assertNull(ti.getEndEpochTimeMilliIfKnown());
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().longValue()) >= 0));
    }

    // Test the absurd case when the start/end times were insanely swapped
    // perhaps by accident ?
    @Test
    public void absurdTimingWithNoClockIfKnown() throws InterruptedException {
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        // absurdly swap the start/end times
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(endTimeNano, startTimeNano);
        Assert.assertFalse(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) > startTimeNano));
        Assert.assertNull(ti.getStartEpochTimeMilliIfKnown());
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNanoIfKnown().longValue()) < endTimeNano));
        Assert.assertNull(ti.getEndEpochTimeMilliIfKnown());
        double double_diff = ((double) (TimeUnit.NANOSECONDS.toMicros((startTimeNano - endTimeNano)))) / 1000.0;
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().doubleValue()) == double_diff));
        long long_diff = TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano));
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().longValue()) == long_diff));
    }

    // Test the absurd case when the start/end times were insanely swapped
    // perhaps by accident
    @Test
    public void absurdTimingWithClockIfKnown() throws InterruptedException {
        final long startTimeMilli = System.currentTimeMillis();
        final long startTimeNano = System.nanoTime();
        Thread.sleep(1);// sleep for 1 millisecond

        final long endTimeNano = System.nanoTime();
        // absurdly swap the start/end times
        TimingInfo ti = TimingInfo.newTimingInfoFullSupport(startTimeMilli, endTimeNano, startTimeNano);
        Assert.assertTrue(ti.isStartEpochTimeMilliKnown());
        Assert.assertTrue(((ti.getStartTimeNano()) > startTimeNano));
        Assert.assertTrue(((ti.getStartEpochTimeMilliIfKnown().longValue()) == startTimeMilli));
        Assert.assertTrue(ti.isEndTimeKnown());
        Assert.assertTrue(((ti.getEndTimeNanoIfKnown().longValue()) < endTimeNano));
        long end_epoch_time = startTimeMilli + (TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano)));
        Assert.assertTrue(((ti.getEndEpochTimeMilliIfKnown().longValue()) == end_epoch_time));
        Assert.assertTrue(((ti.getEndEpochTimeMilliIfKnown().longValue()) == (ti.getEndEpochTimeMilli())));
        double double_diff = ((double) (TimeUnit.NANOSECONDS.toMicros((startTimeNano - endTimeNano)))) / 1000.0;
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().doubleValue()) == double_diff));
        long long_diff = TimeUnit.NANOSECONDS.toMillis((startTimeNano - endTimeNano));
        Assert.assertTrue(((ti.getTimeTakenMillisIfKnown().longValue()) == long_diff));
    }

    @Test
    public void subEventsEnabled() {
        TimingInfo ti = TimingInfo.startTimingFullSupport();
        ti.addSubMeasurement("m1", TimingInfo.startTimingFullSupport());
        Assert.assertNotNull(ti.getAllSubMeasurements("m1"));
        ti.incrementCounter("c1");
        Assert.assertTrue(((ti.getAllCounters().size()) == 1));
        ti.setCounter("c2", 0);
        Assert.assertTrue(((ti.getAllCounters().size()) == 2));
    }

    @Test
    public void subEventsDisabled() {
        TimingInfo ti = TimingInfo.startTiming();
        ti.addSubMeasurement("m1", TimingInfo.startTiming());
        Assert.assertNull(ti.getAllSubMeasurements("m1"));
        ti.incrementCounter("c1");
        Assert.assertTrue(((ti.getAllCounters().size()) == 0));
        ti.setCounter("c2", 0);
        Assert.assertTrue(((ti.getAllCounters().size()) == 0));
    }
}

