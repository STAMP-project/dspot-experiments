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


import com.google.api.services.dataflow.model.MapTask;
import com.google.api.services.dataflow.model.SourceSplitResponse;
import com.google.api.services.dataflow.model.SourceSplitShard;
import com.google.api.services.dataflow.model.WorkItem;
import com.google.api.services.dataflow.model.WorkItemStatus;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.beam.runners.dataflow.options.DataflowWorkerHarnessOptions;
import org.apache.beam.runners.dataflow.util.TimeUtil;
import org.apache.beam.sdk.util.FastNanoClockAndSleeper;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link BatchDataflowWorker}.
 */
@RunWith(JUnit4.class)
public class BatchDataflowWorkerTest {
    private static class WorkerException extends Exception {}

    @Rule
    public FastNanoClockAndSleeper clockAndSleeper = new FastNanoClockAndSleeper();

    @Mock
    WorkUnitClient mockWorkUnitClient;

    @Mock
    DataflowWorkProgressUpdater mockProgressUpdater;

    @Mock
    DataflowWorkExecutor mockWorkExecutor;

    DataflowWorkerHarnessOptions options;

    @Test
    public void testWhenNoWorkIsReturnedThatWeImmediatelyRetry() throws Exception {
        final String workItemId = "14";
        BatchDataflowWorker worker = /* pipeline */
        new BatchDataflowWorker(null, SdkHarnessRegistries.emptySdkHarnessRegistry(), mockWorkUnitClient, IntrinsicMapTaskExecutorFactory.defaultFactory(), options);
        WorkItem workItem = new WorkItem();
        workItem.setId(Long.parseLong(workItemId));
        workItem.setJobId("SuccessfulEmptyMapTask");
        workItem.setInitialReportIndex(12L);
        workItem.setMapTask(new MapTask().setInstructions(new ArrayList<com.google.api.services.dataflow.model.ParallelInstruction>()).setStageName("testStage"));
        workItem.setLeaseExpireTime(TimeUtil.toCloudTime(Instant.now()));
        workItem.setReportStatusInterval(TimeUtil.toCloudDuration(Duration.standardMinutes(1)));
        Mockito.when(mockWorkUnitClient.getWorkItem()).thenReturn(Optional.<WorkItem>absent()).thenReturn(Optional.of(workItem));
        Assert.assertTrue(worker.getAndPerformWork());
        Mockito.verify(mockWorkUnitClient).reportWorkItemStatus(ArgumentMatchers.argThat(new TypeSafeMatcher<WorkItemStatus>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(WorkItemStatus item) {
                Assert.assertTrue(item.getCompleted());
                Assert.assertEquals(workItemId, item.getWorkItemId());
                return true;
            }
        }));
    }

    @Test
    public void testWhenProcessingWorkUnitFailsWeReportStatus() throws Exception {
        BatchDataflowWorker worker = /* pipeline */
        new BatchDataflowWorker(null, SdkHarnessRegistries.emptySdkHarnessRegistry(), mockWorkUnitClient, IntrinsicMapTaskExecutorFactory.defaultFactory(), options);
        // In practice this value is always 1, but for the sake of testing send a different value.
        long initialReportIndex = 4L;
        WorkItem workItem = new WorkItem().setId(1L).setJobId("Expected to fail the job").setInitialReportIndex(initialReportIndex);
        WorkItemStatusClient workItemStatusClient = Mockito.mock(WorkItemStatusClient.class);
        worker.doWork(workItem, workItemStatusClient);
        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(workItemStatusClient).reportError(errorCaptor.capture());
        Throwable error = errorCaptor.getValue();
        Assert.assertThat(error, Matchers.notNullValue());
        Assert.assertThat(error.getMessage(), Matchers.equalTo(("Unknown kind of work item: " + (workItem.toString()))));
    }

    @Test
    public void testStartAndStopProgressReport() throws Exception {
        BatchDataflowWorker worker = /* pipeline */
        new BatchDataflowWorker(null, SdkHarnessRegistries.emptySdkHarnessRegistry(), mockWorkUnitClient, IntrinsicMapTaskExecutorFactory.defaultFactory(), options);
        worker.executeWork(mockWorkExecutor, mockProgressUpdater);
        Mockito.verify(mockProgressUpdater, Mockito.times(1)).startReportingProgress();
        Mockito.verify(mockProgressUpdater, Mockito.times(1)).stopReportingProgress();
    }

    @Test
    public void testStopProgressReportInCaseOfFailure() throws Exception {
        Mockito.doThrow(new BatchDataflowWorkerTest.WorkerException()).when(mockWorkExecutor).execute();
        BatchDataflowWorker worker = /* pipeline */
        new BatchDataflowWorker(null, SdkHarnessRegistries.emptySdkHarnessRegistry(), mockWorkUnitClient, IntrinsicMapTaskExecutorFactory.defaultFactory(), options);
        try {
            worker.executeWork(mockWorkExecutor, mockProgressUpdater);
        } catch (BatchDataflowWorkerTest.WorkerException e) {
            /* Expected - ignore. */
        }
        Mockito.verify(mockProgressUpdater, Mockito.times(1)).stopReportingProgress();
    }

    @Test
    public void testIsSplitResponseTooLarge() throws IOException {
        SourceSplitResponse splitResponse = new SourceSplitResponse();
        splitResponse.setShards(ImmutableList.<SourceSplitShard>of(new SourceSplitShard(), new SourceSplitShard()));
        Assert.assertThat(DataflowApiUtils.computeSerializedSizeBytes(splitResponse), Matchers.greaterThan(0L));
    }
}

