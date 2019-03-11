/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;


import java.util.Random;
import org.apache.hadoop.hdfs.server.datanode.BPServiceActor.Scheduler;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify the block report and heartbeat scheduling logic of BPServiceActor
 * using a few different values .
 */
public class TestBpServiceActorScheduler {
    protected static final Logger LOG = LoggerFactory.getLogger(TestBpServiceActorScheduler.class);

    @Rule
    public Timeout timeout = new Timeout(300000);

    private static final long HEARTBEAT_INTERVAL_MS = 5000;// 5 seconds


    private static final long LIFELINE_INTERVAL_MS = 3 * (TestBpServiceActorScheduler.HEARTBEAT_INTERVAL_MS);

    private static final long BLOCK_REPORT_INTERVAL_MS = 10000;// 10 seconds


    private static final long OUTLIER_REPORT_INTERVAL_MS = 10000;// 10 seconds


    private final Random random = new Random(System.nanoTime());

    @Test
    public void testInit() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            Assert.assertTrue(scheduler.isHeartbeatDue(now));
            Assert.assertTrue(scheduler.isBlockReportDue(scheduler.monotonicNow()));
        }
    }

    @Test
    public void testScheduleBlockReportImmediate() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.scheduleBlockReport(0);
            Assert.assertTrue(scheduler.resetBlockReportTime);
            Assert.assertThat(scheduler.nextBlockReportTime, Is.is(now));
        }
    }

    @Test
    public void testScheduleBlockReportDelayed() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            final long delayMs = 10;
            scheduler.scheduleBlockReport(delayMs);
            Assert.assertTrue(scheduler.resetBlockReportTime);
            Assert.assertTrue((((scheduler.nextBlockReportTime) - now) >= 0));
            Assert.assertTrue((((scheduler.nextBlockReportTime) - (now + delayMs)) < 0));
        }
    }

    /**
     * If resetBlockReportTime is true then the next block report must be scheduled
     * in the range [now, now + BLOCK_REPORT_INTERVAL_SEC).
     */
    @Test
    public void testScheduleNextBlockReport() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            Assert.assertTrue(scheduler.resetBlockReportTime);
            scheduler.scheduleNextBlockReport();
            Assert.assertTrue((((scheduler.nextBlockReportTime) - (now + (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS))) < 0));
        }
    }

    /**
     * If resetBlockReportTime is false then the next block report must be scheduled
     * exactly at (now + BLOCK_REPORT_INTERVAL_SEC).
     */
    @Test
    public void testScheduleNextBlockReport2() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.resetBlockReportTime = false;
            scheduler.scheduleNextBlockReport();
            Assert.assertThat(scheduler.nextBlockReportTime, Is.is((now + (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS))));
        }
    }

    /**
     * Tests the case when a block report was delayed past its scheduled time.
     * In that case the next block report should not be delayed for a full interval.
     */
    @Test
    public void testScheduleNextBlockReport3() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.resetBlockReportTime = false;
            // Make it look like the block report was scheduled to be sent between 1-3
            // intervals ago but sent just now.
            final long blockReportDelay = (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS) + (random.nextInt((2 * ((int) (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS)))));
            final long origBlockReportTime = now - blockReportDelay;
            scheduler.nextBlockReportTime = origBlockReportTime;
            scheduler.scheduleNextBlockReport();
            Assert.assertTrue((((scheduler.nextBlockReportTime) - now) < (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS)));
            Assert.assertTrue(((((scheduler.nextBlockReportTime) - origBlockReportTime) % (TestBpServiceActorScheduler.BLOCK_REPORT_INTERVAL_MS)) == 0));
        }
    }

    @Test
    public void testScheduleHeartbeat() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.scheduleNextHeartbeat();
            Assert.assertFalse(scheduler.isHeartbeatDue(now));
            scheduler.scheduleHeartbeat();
            Assert.assertTrue(scheduler.isHeartbeatDue(now));
        }
    }

    /**
     * Regression test for HDFS-9305.
     * Delayed processing of a heartbeat can cause a subsequent heartbeat
     * storm.
     */
    @Test
    public void testScheduleDelayedHeartbeat() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.scheduleNextHeartbeat();
            Assert.assertFalse(scheduler.isHeartbeatDue(now));
            // Simulate a delayed heartbeat e.g. due to slow processing by NN.
            scheduler.nextHeartbeatTime = now - ((TestBpServiceActorScheduler.HEARTBEAT_INTERVAL_MS) * 10);
            scheduler.scheduleNextHeartbeat();
            // Ensure that the next heartbeat is not due immediately.
            Assert.assertFalse(scheduler.isHeartbeatDue(now));
        }
    }

    @Test
    public void testScheduleLifeline() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            scheduler.scheduleNextLifeline(now);
            Assert.assertFalse(scheduler.isLifelineDue(now));
            Assert.assertThat(scheduler.getLifelineWaitTime(), Is.is(TestBpServiceActorScheduler.LIFELINE_INTERVAL_MS));
            scheduler.scheduleNextLifeline((now - (TestBpServiceActorScheduler.LIFELINE_INTERVAL_MS)));
            Assert.assertTrue(scheduler.isLifelineDue(now));
            Assert.assertThat(scheduler.getLifelineWaitTime(), Is.is(0L));
        }
    }

    @Test
    public void testOutlierReportScheduling() {
        for (final long now : getTimestamps()) {
            Scheduler scheduler = makeMockScheduler(now);
            Assert.assertTrue(scheduler.isOutliersReportDue(now));
            scheduler.scheduleNextOutlierReport();
            Assert.assertFalse(scheduler.isOutliersReportDue(now));
            Assert.assertFalse(scheduler.isOutliersReportDue((now + 1)));
            Assert.assertTrue(scheduler.isOutliersReportDue((now + (TestBpServiceActorScheduler.OUTLIER_REPORT_INTERVAL_MS))));
        }
    }
}

