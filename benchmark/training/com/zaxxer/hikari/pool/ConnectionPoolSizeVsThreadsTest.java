/**
 * Copyright (C) 2013, 2017 Brett Wooldridge
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaxxer.hikari.pool;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Matthew Tambara (matthew.tambara@liferay.com)
 */
public class ConnectionPoolSizeVsThreadsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPoolSizeVsThreadsTest.class);

    private static final int ITERATIONS = 50000;

    @Test
    public void testPoolSizeAboutSameSizeAsThreadCount() throws Exception {
        final int threadCount = 50;
        final ConnectionPoolSizeVsThreadsTest.Counts counts = /* minIdle */
        /* maxPoolSize */
        /* workTimeMs */
        /* restTimeMs */
        /* connectionAcquisitionTimeMs */
        /* postTestTimeMs */
        testPoolSize(2, 100, threadCount, 1, 0, 20, ConnectionPoolSizeVsThreadsTest.ITERATIONS, TimeUnit.SECONDS.toMillis(2));
        // maxActive may never make it to threadCount but it shouldn't be any higher
        /* delta */
        Assert.assertEquals(threadCount, counts.maxActive, 15);
        /* delta */
        Assert.assertEquals(threadCount, counts.maxTotal, 5);
    }

    @Test
    public void testSlowConnectionTimeBurstyWork() throws Exception {
        // setup a bursty work load, 50 threads all needing to do around 100 units of work.
        // Using a more realistic time for connection startup of 250 ms and only 5 seconds worth of work will mean that we end up finishing
        // all of the work before we actually have setup 50 connections even though we have requested 50 connections
        final int threadCount = 50;
        final int workItems = threadCount * 100;
        final int workTimeMs = 0;
        final int connectionAcquisitionTimeMs = 250;
        final ConnectionPoolSizeVsThreadsTest.Counts counts = /* minIdle */
        /* maxPoolSize */
        /* restTimeMs */
        /* iterations */
        /* postTestTimeMs */
        testPoolSize(2, 100, threadCount, workTimeMs, 0, connectionAcquisitionTimeMs, workItems, TimeUnit.SECONDS.toMillis(3));
        // hard to put exact bounds on how many thread we will use but we can put an upper bound on usage (if there was only one thread)
        final long totalWorkTime = workItems * workTimeMs;
        final long connectionMax = totalWorkTime / connectionAcquisitionTimeMs;
        Assert.assertTrue((connectionMax <= (counts.maxActive)));
        /* delta */
        Assert.assertEquals(connectionMax, counts.maxTotal, (2 + 2));
    }

    private static class Counts {
        int maxTotal = 0;

        int maxActive = 0;

        void updateMaxCounts(final HikariPool pool) {
            maxTotal = Math.max(pool.getTotalConnections(), maxTotal);
            maxActive = Math.max(pool.getActiveConnections(), maxActive);
        }

        @Override
        public String toString() {
            return (((("Counts{" + "maxTotal=") + (maxTotal)) + ", maxActive=") + (maxActive)) + '}';
        }
    }
}

