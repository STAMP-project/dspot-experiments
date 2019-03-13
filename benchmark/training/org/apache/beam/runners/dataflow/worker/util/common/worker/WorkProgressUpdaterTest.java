/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import NativeReader.DynamicSplitResult;
import com.google.api.client.testing.http.FixedClock;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link WorkProgressUpdater}.
 */
@RunWith(JUnit4.class)
public class WorkProgressUpdaterTest {
    /**
     * WorkProgressUpdater relies on subclasses to implement some of its functionality, particularly
     * the actual reporting of progress. In the tests below we would like to mock some of what goes on
     * in that helper. This interface provides methods that our subclass of WorkProgressUpdater will
     * call during progress reporting. Specifically it will call the methods on a mock of the
     * interface. We can then set up return values and verify calls on that mock.
     */
    private interface ProgressHelper {
        /**
         * WorkProgressUpdater has called {@code reportProgressHelper}.
         *
         * @param splitPos
         * 		the dynamic split result to report as part of the update
         * @return the number of ms to the next update
         */
        long reportProgress(NativeReader.DynamicSplitResult splitPos);

        /**
         * Return whether to try doing a checkpoint as part of {@code reportProgressHelper}.
         */
        boolean shouldCheckpoint();

        /**
         * Return the exception that (if not null) will be thrown in {@code reportProgressHelper}.
         */
        @Nullable
        Exception shouldThrow();
    }

    private long startTimeMs;

    private int checkpointPeriodSec;

    private long checkpointTimeMs;

    private long initialLeaseExpirationMs;

    private FixedClock clock;

    private StubbedExecutor executor;

    @Mock
    private WorkExecutor workExecutor;

    @Mock
    private WorkProgressUpdaterTest.ProgressHelper progressHelper;

    private WorkProgressUpdater progressUpdater;

    private static class TestSplitResult implements NativeReader.DynamicSplitResult {}

    private DynamicSplitResult checkpointPos = new WorkProgressUpdaterTest.TestSplitResult();

    // Test that periodic checkpoint works when the checkpoint itself fails.
    // Specifically, have the first thing the updater does is a periodic checkpoint, have that
    // checkpoint return a null stop position.
    @Test
    public void periodicCheckpointThatFails() throws Exception {
        // Set the initial lease expiration to 60s so that the periodic checkpoint occurs before the
        // first progress update.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (60 * 1000L);
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(null);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((25 * 1000L));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        Assert.assertEquals(checkpointTimeMs, clock.currentTimeMillis());
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper).reportProgress(null);
        progressUpdater.stopReportingProgress();
    }

    // Test that periodic checkpoint works when the checkpoint itself succeeds.
    // Specifically, have the first thing the updater does is a periodic checkpoint, have that
    // checkpoint return a (non-null) dummy stop position.
    @Test
    public void periodicCheckpointThatSucceeds() throws Exception {
        // Set the initial lease expiration to 60s so that the periodic checkpoint occurs before the
        // first progress update.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (60 * 1000L);
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((25 * 1000L));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        Assert.assertEquals(checkpointTimeMs, clock.currentTimeMillis());
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper).reportProgress(checkpointPos);
        progressUpdater.stopReportingProgress();
    }

    // Test that periodic checkpoints work after several regular updates.  Specifically, have three
    // updates and then the periodic checkpoint.
    @Test
    public void updatesBeforePeriodicCheckpoint() throws Exception {
        // Set the initial lease expiration to 20s so that the first update occurs at 10s, ie before
        // the periodic checkpoint.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (20 * 1000L);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));// Next update at 14s.

        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // Verify first update at 10s and no checkpoint.
        Assert.assertEquals(((startTimeMs) + (10 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor, Mockito.never()).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.times(1)).reportProgress(null);
        Mockito.verify(progressHelper, Mockito.never()).reportProgress(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));// Next update at 18s.

        executor.runNextRunnable();
        // Verify second update at 14s and no checkpoint.
        Assert.assertEquals(((startTimeMs) + (14 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor, Mockito.never()).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.times(2)).reportProgress(null);
        Mockito.verify(progressHelper, Mockito.never()).reportProgress(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));// Next update at 22s.

        executor.runNextRunnable();
        // Verify third update at 18s and no checkpoint.
        Assert.assertEquals(((startTimeMs) + (18 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor, Mockito.never()).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.times(3)).reportProgress(null);
        Mockito.verify(progressHelper, Mockito.never()).reportProgress(checkpointPos);
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));
        executor.runNextRunnable();
        // Verify periodic checkpoint at 20s.
        Assert.assertEquals(checkpointTimeMs, clock.currentTimeMillis());
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.times(3)).reportProgress(null);
        Mockito.verify(progressHelper).reportProgress(checkpointPos);
        progressUpdater.stopReportingProgress();
    }

    // Test that an asynchronous checkpoint request works.  Specifically, do one update, then
    // call requestCheckpoint.
    @Test
    public void requestCheckpointSucceeds() throws Exception {
        // Set the initial lease expiration to 20s so that the first update occurs at 10s, ie before
        // the periodic checkpoint.
        // Do one update.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (20 * 1000L);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        Assert.assertEquals(((startTimeMs) + (10 * 1000L)), clock.currentTimeMillis());
        // Now asynchronously request a checkpoint that actually succeeds.
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));
        progressUpdater.requestCheckpoint();
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper).reportProgress(checkpointPos);
        progressUpdater.stopReportingProgress();
    }

    // Test that an asynchronous checkpoint request works when the first checkpoint attempt fails, but
    // the second attempt succeeds.  Specifically, do one update, then call requestCheckpoint, have
    // the executor's requestCheckpoint return null; then do another update and have the executor's
    // requestCheckpoint return a dummy stop position.
    @Test
    public void requestCheckpointThatFailsOnce() throws Exception {
        // Set the initial lease expiration to 20s so that the first update occurs at 10s, ie before
        // the periodic checkpoint.
        // Do one update.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (20 * 1000L);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));// Next update at 14s.

        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        Assert.assertEquals(((startTimeMs) + (10 * 1000L)), clock.currentTimeMillis());
        // Now asynchronously request a checkpoint that initial fails.
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(null);
        progressUpdater.requestCheckpoint();
        // Verify checkpoint attempted, but no report of dummy position.
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.never()).reportProgress(checkpointPos);
        // Do another update, but this time the checkpoint succeeds.
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(checkpointPos);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));
        executor.runNextRunnable();
        // Verify checkpointed attempted twice and dymmy position reported.
        Assert.assertEquals(((startTimeMs) + (14 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor, Mockito.times(2)).requestCheckpoint();
        Mockito.verify(progressHelper).reportProgress(checkpointPos);
        progressUpdater.stopReportingProgress();
    }

    // Test checkpoint request in the helper method works.  When an update is sent to the service,
    // the response might request that a checkpoint be done.  In this case {@code
    // reportProgressHelper} would update {@code checkpointState} appropriately and call {@code
    // tryCheckpointIfNeeded}.  Here we simulate that scenario and verify that it works.  Specically,
    // do one update, then on the second update, have the helper method attempt a checkpoint.
    @Test
    public void updateResponseCheckpointSucceeds() throws Exception {
        // Set the initial lease expiration to 20s so that the first update occurs at 10s, ie before
        // the periodic checkpoint.
        // Do one update that tries to checkpoint.
        initialLeaseExpirationMs = (clock.currentTimeMillis()) + (20 * 1000L);
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));// Next update would be at 14s, but successful checkpoint will

        // change it to be at 10s.
        Mockito.when(progressHelper.shouldCheckpoint()).thenReturn(true);
        Mockito.when(workExecutor.requestCheckpoint()).thenReturn(checkpointPos);
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // At this point, the helper method should have been called to do the update, it should have
        // attempted the checkpoint, which succeded.  However, the new stop position is not yet reported
        // back as that will occur in the next update, which should be scheduled immediately (at 10s).
        Assert.assertEquals(((startTimeMs) + (10 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper, Mockito.never()).reportProgress(checkpointPos);
        // Run another update to see the split result reported.
        Mockito.when(progressHelper.reportProgress(ArgumentMatchers.any(DynamicSplitResult.class))).thenReturn((4 * 1000L));
        Mockito.when(progressHelper.shouldCheckpoint()).thenReturn(false);
        executor.runNextRunnable();
        // Verify that new stop position now reported back.
        Assert.assertEquals(((startTimeMs) + (10 * 1000L)), clock.currentTimeMillis());
        Mockito.verify(workExecutor).requestCheckpoint();
        Mockito.verify(progressHelper).reportProgress(checkpointPos);
        progressUpdater.stopReportingProgress();
    }

    // Test that InterruptedException aborts the work item, and that other exceptions are retried.
    @Test
    public void interruptedExceptionAbortsWork() throws Exception {
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // Most exceptions should be logged and retried.
        Mockito.when(progressHelper.shouldThrow()).thenReturn(new RuntimeException("Something Failed"));
        executor.runNextRunnable();
        Mockito.verify(workExecutor, Mockito.never()).abort();
        // InterruptedException should cause the work to abort.
        Mockito.when(progressHelper.shouldThrow()).thenReturn(new InterruptedException("Lease expired"));
        executor.runNextRunnable();
        Mockito.verify(workExecutor).abort();
        progressUpdater.stopReportingProgress();
    }
}

