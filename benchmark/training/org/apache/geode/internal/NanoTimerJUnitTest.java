/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import org.apache.geode.internal.NanoTimer.TimeService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for NanoTimer.
 *
 * @since GemFire 7.0
 */
public class NanoTimerJUnitTest {
    @Test
    public void testMillisToNanos() {
        Assert.assertEquals(0, NanoTimer.millisToNanos(0));
        Assert.assertEquals(1000000, NanoTimer.millisToNanos(1));
    }

    @Test
    public void testNanosToMillis() {
        Assert.assertEquals(0, NanoTimer.nanosToMillis(1));
        Assert.assertEquals(1, NanoTimer.nanosToMillis(1000000));
    }

    @Test
    public void testDefaultNanoTimer() {
        // All the other unit test methods of NanoTimer
        // inject TestTimeService into the NanoTimer.
        // This method verifies that the default constructor
        // works.
        final NanoTimer timer = new NanoTimer();
        timer.getConstructionTime();
        timer.getLastResetTime();
        timer.getTimeSinceConstruction();
        timer.getTimeSinceReset();
        timer.reset();
    }

    @Test
    public void testInitialTimes() {
        NanoTimerJUnitTest.TestTimeService ts = new NanoTimerJUnitTest.TestTimeService();
        final long nanoTime = ts.getTime();
        final NanoTimer timer = new NanoTimer(ts);
        Assert.assertEquals(timer.getConstructionTime(), timer.getLastResetTime());
        Assert.assertTrue(((timer.getTimeSinceConstruction()) <= (timer.getTimeSinceReset())));
        Assert.assertTrue(((timer.getLastResetTime()) >= nanoTime));
        Assert.assertTrue(((timer.getConstructionTime()) >= nanoTime));
        Assert.assertTrue(((ts.getTime()) >= nanoTime));
        final long nanosOne = ts.getTime();
        ts.incTime();
        Assert.assertEquals(1, timer.getTimeSinceConstruction());
    }

    @Test
    public void testReset() {
        NanoTimerJUnitTest.TestTimeService ts = new NanoTimerJUnitTest.TestTimeService();
        final NanoTimer timer = new NanoTimer(ts);
        final long nanosOne = ts.getTime();
        ts.incTime();
        Assert.assertEquals(timer.getConstructionTime(), timer.getLastResetTime());
        Assert.assertTrue(((timer.getTimeSinceConstruction()) <= (timer.getTimeSinceReset())));
        final long nanosTwo = ts.getTime();
        final long resetOne = timer.reset();
        Assert.assertTrue((resetOne >= (nanosTwo - nanosOne)));
        Assert.assertFalse(((timer.getConstructionTime()) == (timer.getLastResetTime())));
        final long nanosThree = ts.getTime();
        ts.incTime();
        Assert.assertTrue(((timer.getLastResetTime()) >= nanosTwo));
        Assert.assertTrue(((timer.getTimeSinceReset()) < (timer.getTimeSinceConstruction())));
        Assert.assertTrue(((timer.getLastResetTime()) <= nanosThree));
        Assert.assertTrue(((timer.getTimeSinceReset()) <= ((ts.getTime()) - (timer.getLastResetTime()))));
        final long nanosFour = ts.getTime();
        final long resetTwo = timer.reset();
        Assert.assertTrue((resetTwo >= (nanosFour - nanosThree)));
        ts.incTime();
        Assert.assertTrue(((timer.getLastResetTime()) >= nanosFour));
        Assert.assertTrue(((timer.getTimeSinceReset()) < (timer.getTimeSinceConstruction())));
        Assert.assertTrue(((timer.getTimeSinceReset()) <= ((ts.getTime()) - (timer.getLastResetTime()))));
    }

    /**
     * Simple deterministic clock. Any time you want your clock to tick call incTime.
     */
    private class TestTimeService implements TimeService {
        private long now;

        public void incTime() {
            (this.now)++;
        }

        @Override
        public long getTime() {
            return this.now;
        }
    }
}

