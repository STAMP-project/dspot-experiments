/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * TestCase for StopWatch.
 */
public class StopWatchTest {
    // -----------------------------------------------------------------------
    @Test
    public void testStopWatchSimple() {
        final StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.stop();
        final long time = watch.getTime();
        Assertions.assertEquals(time, watch.getTime());
        Assertions.assertTrue((time >= 500));
        Assertions.assertTrue((time < 700));
        watch.reset();
        Assertions.assertEquals(0, watch.getTime());
    }

    @Test
    public void testStopWatchStatic() {
        final StopWatch watch = StopWatch.createStarted();
        Assertions.assertTrue(watch.isStarted());
    }

    @Test
    public void testStopWatchSimpleGet() {
        final StopWatch watch = new StopWatch();
        Assertions.assertEquals(0, watch.getTime());
        Assertions.assertEquals("00:00:00.000", watch.toString());
        watch.start();
        try {
            Thread.sleep(500);
        } catch (final InterruptedException ex) {
        }
        Assertions.assertTrue(((watch.getTime()) < 2000));
    }

    @Test
    public void testStopWatchGetWithTimeUnit() {
        // Create a mock StopWatch with a time of 2:59:01.999
        final StopWatch watch = createMockStopWatch(((((TimeUnit.HOURS.toNanos(2)) + (TimeUnit.MINUTES.toNanos(59))) + (TimeUnit.SECONDS.toNanos(1))) + (TimeUnit.MILLISECONDS.toNanos(999))));
        Assertions.assertEquals(2L, watch.getTime(TimeUnit.HOURS));
        Assertions.assertEquals(179L, watch.getTime(TimeUnit.MINUTES));
        Assertions.assertEquals(10741L, watch.getTime(TimeUnit.SECONDS));
        Assertions.assertEquals(10741999L, watch.getTime(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testStopWatchSplit() {
        final StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.split();
        final long splitTime = watch.getSplitTime();
        final String splitStr = watch.toSplitString();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.unsplit();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.stop();
        final long totalTime = watch.getTime();
        Assertions.assertEquals(splitStr.length(), 12, "Formatted split string not the correct length");
        Assertions.assertTrue((splitTime >= 500));
        Assertions.assertTrue((splitTime < 700));
        Assertions.assertTrue((totalTime >= 1500));
        Assertions.assertTrue((totalTime < 1900));
    }

    @Test
    public void testStopWatchSuspend() {
        final StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.suspend();
        final long suspendTime = watch.getTime();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.resume();
        try {
            Thread.sleep(550);
        } catch (final InterruptedException ex) {
        }
        watch.stop();
        final long totalTime = watch.getTime();
        Assertions.assertTrue((suspendTime >= 500));
        Assertions.assertTrue((suspendTime < 700));
        Assertions.assertTrue((totalTime >= 1000));
        Assertions.assertTrue((totalTime < 1300));
    }

    @Test
    public void testLang315() {
        final StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(200);
        } catch (final InterruptedException ex) {
        }
        watch.suspend();
        final long suspendTime = watch.getTime();
        try {
            Thread.sleep(200);
        } catch (final InterruptedException ex) {
        }
        watch.stop();
        final long totalTime = watch.getTime();
        Assertions.assertEquals(suspendTime, totalTime);
    }

    // test bad states
    @Test
    public void testBadStates() {
        final StopWatch watch = new StopWatch();
        Assertions.assertThrows(IllegalStateException.class, watch::stop, "Calling stop on an unstarted StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::suspend, "Calling suspend on an unstarted StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::split, "Calling split on a non-running StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::unsplit, "Calling unsplit on an unsplit StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::resume, "Calling resume on an unsuspended StopWatch should throw an exception. ");
        watch.start();
        Assertions.assertThrows(IllegalStateException.class, watch::start, "Calling start on a started StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::unsplit, "Calling unsplit on an unsplit StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::getSplitTime, "Calling getSplitTime on an unsplit StopWatch should throw an exception. ");
        Assertions.assertThrows(IllegalStateException.class, watch::resume, "Calling resume on an unsuspended StopWatch should throw an exception. ");
        watch.stop();
        Assertions.assertThrows(IllegalStateException.class, watch::start, "Calling start on a stopped StopWatch should throw an exception as it needs to be reset. ");
    }

    @Test
    public void testGetStartTime() {
        final long beforeStopWatch = System.currentTimeMillis();
        final StopWatch watch = new StopWatch();
        Assertions.assertThrows(IllegalStateException.class, watch::getStartTime, "Calling getStartTime on an unstarted StopWatch should throw an exception");
        watch.start();
        watch.getStartTime();
        Assertions.assertTrue(((watch.getStartTime()) >= beforeStopWatch));
        watch.reset();
        Assertions.assertThrows(IllegalStateException.class, watch::getStartTime, "Calling getStartTime on a reset, but unstarted StopWatch should throw an exception");
    }

    @Test
    public void testBooleanStates() {
        final StopWatch watch = new StopWatch();
        Assertions.assertFalse(watch.isStarted());
        Assertions.assertFalse(watch.isSuspended());
        Assertions.assertTrue(watch.isStopped());
        watch.start();
        Assertions.assertTrue(watch.isStarted());
        Assertions.assertFalse(watch.isSuspended());
        Assertions.assertFalse(watch.isStopped());
        watch.suspend();
        Assertions.assertTrue(watch.isStarted());
        Assertions.assertTrue(watch.isSuspended());
        Assertions.assertFalse(watch.isStopped());
        watch.stop();
        Assertions.assertFalse(watch.isStarted());
        Assertions.assertFalse(watch.isSuspended());
        Assertions.assertTrue(watch.isStopped());
    }
}

