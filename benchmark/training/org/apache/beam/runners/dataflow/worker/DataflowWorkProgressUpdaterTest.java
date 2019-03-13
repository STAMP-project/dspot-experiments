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
package org.apache.beam.runners.dataflow.worker;


import com.google.api.client.testing.http.FixedClock;
import com.google.api.services.dataflow.model.WorkItemServiceState;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitRequest;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResult;
import org.apache.beam.runners.dataflow.worker.util.common.worker.StubbedExecutor;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link DataflowWorkProgressUpdater}.
 */
@RunWith(JUnit4.class)
public class DataflowWorkProgressUpdaterTest {
    private static final long LEASE_MS = 2000;

    private static final String PROJECT_ID = "TEST_PROJECT_ID";

    private static final String JOB_ID = "TEST_JOB_ID";

    private static final Long WORK_ID = 1234567890L;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private StubbedExecutor executor;

    private DataflowWorkProgressUpdater progressUpdater;

    private long startTime;

    private FixedClock clock;

    @Mock
    private WorkItemStatusClient workItemStatusClient;

    @Mock
    private DataflowWorkExecutor worker;

    @Captor
    private ArgumentCaptor<DynamicSplitResult> splitResultCaptor;

    @Test
    public void workProgressSendsAnUpdate() throws Exception {
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(generateServiceState(null, 1000));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // The initial update should be sent at startTime + 300 ms.
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 300));
        Mockito.verify(workItemStatusClient, Mockito.atLeastOnce()).reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class));
        progressUpdater.stopReportingProgress();
    }

    /**
     * Verifies that the update after a split is requested acknowledeges it.
     */
    @Test
    public void workProgressSendsSplitResults() throws Exception {
        // The setup process sends one update after 300ms. Enqueue another that should be scheduled
        // 1000ms after that.
        WorkItemServiceState firstResponse = generateServiceState(ReaderTestUtils.positionAtIndex(2L), 1000);
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(firstResponse);
        Mockito.when(worker.getWorkerProgress()).thenReturn(SourceTranslationUtils.cloudProgressToReaderProgress(ReaderTestUtils.approximateProgressAtIndex(1L)));
        Mockito.when(worker.requestDynamicSplit(SourceTranslationUtils.toDynamicSplitRequest(firstResponse.getSplitRequest()))).thenReturn(new org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(firstResponse.getSplitRequest().getPosition())));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // The initial update should be sent at startTime + 300 ms.
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 300));
        Mockito.verify(workItemStatusClient).reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class));
        Mockito.verify(worker).requestDynamicSplit(ArgumentMatchers.isA(DynamicSplitRequest.class));
        // The second update should be sent at startTime + 1300 ms and includes the split response.
        // Also schedule another update after 1000ms.
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(generateServiceState(null, 1000));
        executor.runNextRunnable();
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 1300));
        // Verify that the update includes the respuonse to the split request.
        Mockito.verify(workItemStatusClient, Mockito.atLeastOnce()).reportUpdate(splitResultCaptor.capture(), ArgumentMatchers.isA(Duration.class));
        Assert.assertEquals("Second update is sent and contains the latest split result", splitResultCaptor.getValue(), new org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(ReaderTestUtils.positionAtIndex(2L))));
        executor.runNextRunnable();
        Mockito.verify(workItemStatusClient, Mockito.times(3)).reportUpdate(splitResultCaptor.capture(), ArgumentMatchers.isA(Duration.class));
        // Stop the progressUpdater now, and expect the last update immediately
        progressUpdater.stopReportingProgress();
    }

    @Test
    public void workProgressAdaptsNextDuration() throws Exception {
        progressUpdater.startReportingProgress();
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(generateServiceState(null, 1000));
        executor.runNextRunnable();
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 300));
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(generateServiceState(null, 400));
        executor.runNextRunnable();
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 1300));
        executor.runNextRunnable();
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 1700));
        progressUpdater.stopReportingProgress();
    }

    /**
     * Verifies that a last update is sent when there is an unacknowledged split request.
     */
    @Test
    public void workProgressUpdaterSendsLastPendingUpdateWhenStopped() throws Exception {
        // The setup process sends one update after 300ms. Enqueue another that should be scheduled
        // 1000ms after that.
        WorkItemServiceState firstResponse = generateServiceState(ReaderTestUtils.positionAtIndex(2L), 1000);
        Mockito.when(workItemStatusClient.reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class))).thenReturn(firstResponse);
        Mockito.when(worker.getWorkerProgress()).thenReturn(SourceTranslationUtils.cloudProgressToReaderProgress(ReaderTestUtils.approximateProgressAtIndex(1L)));
        Mockito.when(worker.requestDynamicSplit(SourceTranslationUtils.toDynamicSplitRequest(firstResponse.getSplitRequest()))).thenReturn(new org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(firstResponse.getSplitRequest().getPosition())));
        progressUpdater.startReportingProgress();
        executor.runNextRunnable();
        // The initial update should be sent at startTime + 300 ms.
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 300));
        Mockito.verify(workItemStatusClient).reportUpdate(ArgumentMatchers.isNull(DynamicSplitResult.class), ArgumentMatchers.isA(Duration.class));
        Mockito.verify(worker).requestDynamicSplit(ArgumentMatchers.isA(DynamicSplitRequest.class));
        clock.setTime(((clock.currentTimeMillis()) + 100));
        // Stop the progressUpdater now, and expect the last update immediately
        progressUpdater.stopReportingProgress();
        Assert.assertEquals(clock.currentTimeMillis(), ((startTime) + 400));
        // Verify that the last update is sent immediately and contained the latest split result.
        Mockito.verify(workItemStatusClient, Mockito.atLeastOnce()).reportUpdate(splitResultCaptor.capture(), ArgumentMatchers.isA(Duration.class));
        Assert.assertEquals("Final update is sent and contains the latest split result", splitResultCaptor.getValue(), new org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(ReaderTestUtils.positionAtIndex(2L))));
        // And nothing happened after that.
        Mockito.verify(workItemStatusClient, Mockito.atLeastOnce()).uniqueWorkId();
        Mockito.verifyNoMoreInteractions(workItemStatusClient);
    }
}

