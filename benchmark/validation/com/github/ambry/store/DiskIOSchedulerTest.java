/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Throttler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Test;


public class DiskIOSchedulerTest {
    /**
     * Test that the correct throttlers are used for different job types.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicTest() throws Exception {
        final int numJobTypes = 5;
        Map<String, Throttler> throttlers = new HashMap<>();
        for (int i = 0; i < numJobTypes; i++) {
            String jobType = Integer.toString(i);
            throttlers.put(jobType, new DiskIOSchedulerTest.MockThrottler());
        }
        DiskIOScheduler scheduler = new DiskIOScheduler(throttlers);
        // recognized job types
        for (int i = 0; i < numJobTypes; i++) {
            String jobType = Integer.toString(i);
            Long usedSinceLastCall = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            Assert.assertEquals("Unexpected i/o slice availability returned", Long.MAX_VALUE, scheduler.getSlice(jobType, "job", usedSinceLastCall));
            DiskIOSchedulerTest.MockThrottler throttler = ((DiskIOSchedulerTest.MockThrottler) (throttlers.get(jobType)));
            Assert.assertTrue("maybeThrottle should have been called for this jobType", throttler.called);
            Assert.assertEquals("observed units passed to throttler not as expected", usedSinceLastCall, throttler.observedUnits, 0.0);
        }
        for (Throttler throttler : throttlers.values()) {
            ((DiskIOSchedulerTest.MockThrottler) (throttler)).reset();
        }
        // unrecognized job type
        String jobType = Integer.toString(numJobTypes);
        Assert.assertEquals("Unexpected i/o slice availability returned", Long.MAX_VALUE, scheduler.getSlice(jobType, "job", 0));
        for (Throttler throttler : throttlers.values()) {
            Assert.assertFalse("maybeThrottle should not have been called for this jobType", ((DiskIOSchedulerTest.MockThrottler) (throttler)).called);
        }
        // disabling scheduler
        scheduler.disable();
        for (Throttler throttler : throttlers.values()) {
            Assert.assertTrue("Throttler should be closed.", ((DiskIOSchedulerTest.MockThrottler) (throttler)).closed);
        }
    }

    /**
     * Test for correct behavior when a null throttler map is passed in.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void nullThrottlersTest() throws Exception {
        DiskIOScheduler scheduler = new DiskIOScheduler(null);
        Assert.assertEquals("Unexpected i/o slice availability returned", Long.MAX_VALUE, scheduler.getSlice("jobType", "job", 0));
    }

    /**
     * A mock of {@link Throttler} for testing purposes.
     */
    private static class MockThrottler extends Throttler {
        boolean called;

        boolean closed;

        double observedUnits;

        /**
         * Build a {@link MockThrottler}.
         */
        private MockThrottler() {
            super(0, 0, false, new MockTime());
            reset();
        }

        @Override
        public void maybeThrottle(double observed) {
            called = true;
            observedUnits = observed;
        }

        @Override
        public void disable() {
            closed = true;
        }

        /**
         * Reset the testing related variables to their default values.
         */
        private void reset() {
            called = false;
            observedUnits = Double.MIN_VALUE;
            closed = false;
        }
    }
}

