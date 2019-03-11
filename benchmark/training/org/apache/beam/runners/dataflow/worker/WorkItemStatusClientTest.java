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


import com.google.api.services.dataflow.model.CounterMetadata;
import com.google.api.services.dataflow.model.CounterStructuredName;
import com.google.api.services.dataflow.model.CounterStructuredNameAndMetadata;
import com.google.api.services.dataflow.model.CounterUpdate;
import com.google.api.services.dataflow.model.MetricUpdate;
import com.google.api.services.dataflow.model.NameAndKind;
import com.google.api.services.dataflow.model.Position;
import com.google.api.services.dataflow.model.Status;
import com.google.api.services.dataflow.model.WorkItem;
import com.google.api.services.dataflow.model.WorkItemServiceState;
import com.google.api.services.dataflow.model.WorkItemStatus;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.beam.runners.core.metrics.CounterCell;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker.ExecutionState;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.worker.WorkerCustomSources.BoundedSourceSplit;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.DataflowCounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.DynamicSplitResult;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader.Progress;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
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
import org.mockito.internal.verification.VerificationModeFactory;


/**
 * Tests for {@link WorkItemStatusClient}.
 */
@RunWith(JUnit4.class)
public class WorkItemStatusClientTest {
    private static final String PROJECT_ID = "ProjectId";

    private static final String JOB_ID = "JobId";

    private static final long WORK_ID = -559038737;

    private static final Duration LEASE_DURATION = Duration.standardSeconds(10);

    private static final long INITIAL_REPORT_INDEX = 5;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private WorkUnitClient workUnitClient;

    private WorkItem workItem = new WorkItem().setProjectId(WorkItemStatusClientTest.PROJECT_ID).setJobId(WorkItemStatusClientTest.JOB_ID).setId(WorkItemStatusClientTest.WORK_ID).setInitialReportIndex(WorkItemStatusClientTest.INITIAL_REPORT_INDEX);

    private DataflowPipelineOptions options;

    @Mock
    private DataflowWorkExecutor worker;

    private BatchModeExecutionContext executionContext;

    @Captor
    private ArgumentCaptor<WorkItemStatus> statusCaptor;

    private WorkItemStatusClient statusClient;

    /**
     * Verify that we can set the worker once, but not again.
     */
    @Test
    public void setWorker() {
        // We should be able to set the worker the first time.
        statusClient.setWorker(worker, executionContext);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("setWorker once");
        statusClient.setWorker(worker, executionContext);
    }

    /**
     * Reporting an error before setWorker has been called should work.
     */
    @Test
    public void reportError() throws IOException {
        RuntimeException error = new RuntimeException();
        error.fillInStackTrace();
        statusClient.reportError(error);
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getWorkItemId(), Matchers.equalTo(Long.toString(WorkItemStatusClientTest.WORK_ID)));
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(true));
        Assert.assertThat(workStatus.getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(workStatus.getErrors(), Matchers.hasSize(1));
        Status status = workStatus.getErrors().get(0);
        Assert.assertThat(status.getCode(), Matchers.equalTo(2));
        Assert.assertThat(status.getMessage(), Matchers.containsString("WorkItemStatusClientTest"));
    }

    /**
     * Reporting an error after setWorker has been called should also work.
     */
    @Test
    public void reportErrorAfterSetWorker() throws IOException {
        RuntimeException error = new RuntimeException();
        error.fillInStackTrace();
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportError(error);
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getWorkItemId(), Matchers.equalTo(Long.toString(WorkItemStatusClientTest.WORK_ID)));
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(true));
        Assert.assertThat(workStatus.getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(workStatus.getErrors(), Matchers.hasSize(1));
        Status status = workStatus.getErrors().get(0);
        Assert.assertThat(status.getCode(), Matchers.equalTo(2));
        Assert.assertThat(status.getMessage(), Matchers.containsString("WorkItemStatusClientTest"));
    }

    /**
     * Reporting an out of memory error should log it in addition to the regular flow.
     */
    @Test
    public void reportOutOfMemoryErrorAfterSetWorker() throws IOException {
        OutOfMemoryError error = new OutOfMemoryError();
        error.fillInStackTrace();
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportError(error);
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getWorkItemId(), Matchers.equalTo(Long.toString(WorkItemStatusClientTest.WORK_ID)));
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(true));
        Assert.assertThat(workStatus.getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(workStatus.getErrors(), Matchers.hasSize(1));
        Status status = workStatus.getErrors().get(0);
        Assert.assertThat(status.getCode(), Matchers.equalTo(2));
        Assert.assertThat(status.getMessage(), Matchers.containsString("WorkItemStatusClientTest"));
        Assert.assertThat(status.getMessage(), Matchers.containsString("An OutOfMemoryException occurred."));
    }

    @Test
    public void reportUpdateAfterErrorShouldFail() throws Exception {
        RuntimeException error = new RuntimeException();
        error.fillInStackTrace();
        statusClient.reportError(error);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("reportUpdate");
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
    }

    @Test
    public void reportSuccessBeforeSetWorker() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("setWorker");
        thrown.expectMessage("reportSuccess");
        statusClient.reportSuccess();
    }

    @Test
    public void reportSuccess() throws IOException {
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportSuccess();
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getWorkItemId(), Matchers.equalTo(Long.toString(WorkItemStatusClientTest.WORK_ID)));
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(true));
        Assert.assertThat(workStatus.getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(workStatus.getErrors(), Matchers.nullValue());
    }

    @Test
    public void reportSuccessWithSourceOperation() throws IOException {
        SourceOperationExecutor sourceWorker = Mockito.mock(SourceOperationExecutor.class);
        Mockito.when(sourceWorker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(sourceWorker, executionContext);
        statusClient.reportSuccess();
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getWorkItemId(), Matchers.equalTo(Long.toString(WorkItemStatusClientTest.WORK_ID)));
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(true));
        Assert.assertThat(workStatus.getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(workStatus.getErrors(), Matchers.nullValue());
    }

    @Test
    public void reportUpdateAfterSuccess() throws Exception {
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportSuccess();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("reportUpdate");
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
    }

    @Test
    public void reportUpdateNullSplit() throws Exception {
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(false));
    }

    @Test
    public void reportUpdate() throws Exception {
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
        Mockito.verify(workUnitClient).reportWorkItemStatus(statusCaptor.capture());
        WorkItemStatus workStatus = statusCaptor.getValue();
        Assert.assertThat(workStatus.getCompleted(), Matchers.equalTo(false));
    }

    @Test
    public void reportIndexSequence() throws Exception {
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        Mockito.when(workUnitClient.reportWorkItemStatus(ArgumentMatchers.isA(WorkItemStatus.class))).thenReturn(new WorkItemServiceState().setNextReportIndex(((WorkItemStatusClientTest.INITIAL_REPORT_INDEX) + 4)));
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
        Mockito.when(workUnitClient.reportWorkItemStatus(ArgumentMatchers.isA(WorkItemStatus.class))).thenReturn(new WorkItemServiceState().setNextReportIndex(((WorkItemStatusClientTest.INITIAL_REPORT_INDEX) + 8)));
        statusClient.reportUpdate(null, WorkItemStatusClientTest.LEASE_DURATION);
        statusClient.reportSuccess();
        Mockito.verify(workUnitClient, VerificationModeFactory.times(3)).reportWorkItemStatus(statusCaptor.capture());
        List<WorkItemStatus> updates = statusCaptor.getAllValues();
        Assert.assertThat(updates.get(0).getReportIndex(), Matchers.equalTo(WorkItemStatusClientTest.INITIAL_REPORT_INDEX));
        Assert.assertThat(updates.get(1).getReportIndex(), Matchers.equalTo(((WorkItemStatusClientTest.INITIAL_REPORT_INDEX) + 4)));
        Assert.assertThat(updates.get(2).getReportIndex(), Matchers.equalTo(((WorkItemStatusClientTest.INITIAL_REPORT_INDEX) + 8)));
    }

    @Test
    public void populateMetricUpdatesNoStateSamplerInfo() throws Exception {
        // When executionContext.getExecutionStateTracker() returns null, we get no metric updates.
        WorkItemStatus status = new WorkItemStatus();
        BatchModeExecutionContext executionContext = Mockito.mock(BatchModeExecutionContext.class);
        Mockito.when(executionContext.getExecutionStateTracker()).thenReturn(null);
        statusClient.setWorker(worker, executionContext);
        statusClient.populateMetricUpdates(status);
        Assert.assertThat(status.getMetricUpdates(), Matchers.empty());
    }

    @Test
    public void populateMetricUpdatesStateSamplerInfo() throws Exception {
        // When executionContext.getExecutionStateTracker() returns non-null, we get one metric update.
        WorkItemStatus status = new WorkItemStatus();
        BatchModeExecutionContext executionContext = Mockito.mock(BatchModeExecutionContext.class);
        ExecutionStateTracker executionStateTracker = Mockito.mock(ExecutionStateTracker.class);
        ExecutionState executionState = Mockito.mock(ExecutionState.class);
        Mockito.when(executionState.getDescription()).thenReturn("stageName-systemName-some-state");
        Mockito.when(executionContext.getExecutionStateTracker()).thenReturn(executionStateTracker);
        Mockito.when(executionStateTracker.getMillisSinceLastTransition()).thenReturn(20L);
        Mockito.when(executionStateTracker.getNumTransitions()).thenReturn(10L);
        Mockito.when(executionStateTracker.getCurrentState()).thenReturn(executionState);
        statusClient.setWorker(worker, executionContext);
        statusClient.populateMetricUpdates(status);
        Assert.assertThat(status.getMetricUpdates(), Matchers.hasSize(1));
        MetricUpdate update = status.getMetricUpdates().get(0);
        Assert.assertThat(update.getName().getName(), Matchers.equalTo("state-sampler"));
        Assert.assertThat(update.getKind(), Matchers.equalTo("internal"));
        Map<String, Object> samplerMetrics = ((Map<String, Object>) (update.getInternal()));
        Assert.assertThat(samplerMetrics, Matchers.hasEntry("last-state-name", "stageName-systemName-some-state"));
        Assert.assertThat(samplerMetrics, Matchers.hasEntry("num-transitions", 10L));
        Assert.assertThat(samplerMetrics, Matchers.hasEntry("last-state-duration-ms", 20L));
    }

    @Test
    public void populateCounterUpdatesEmptyOutputCounters() throws Exception {
        // When worker.getOutputCounters == null, there should be no counters.
        WorkItemStatus status = new WorkItemStatus();
        statusClient.setWorker(worker, executionContext);
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.populateCounterUpdates(status);
        Assert.assertThat(status.getCounterUpdates(), Matchers.hasSize(0));
    }

    /**
     * Validates that an "internal" Counter is reported.
     */
    @Test
    public void populateCounterUpdatesWithOutputCounters() throws Exception {
        final CounterUpdate counter = new CounterUpdate().setNameAndKind(new NameAndKind().setName("some-counter").setKind("SUM")).setCumulative(true).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(42));
        CounterSet counterSet = new CounterSet();
        counterSet.intSum(CounterName.named("some-counter")).addValue(42);
        WorkItemStatus status = new WorkItemStatus();
        Mockito.when(worker.getOutputCounters()).thenReturn(counterSet);
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, executionContext);
        statusClient.populateCounterUpdates(status);
        Assert.assertThat(status.getCounterUpdates(), Matchers.containsInAnyOrder(counter));
    }

    /**
     * Validates that Beam Metrics and "internal" Counters are merged in the update.
     */
    @Test
    public void populateCounterUpdatesWithMetricsAndCounters() throws Exception {
        final CounterUpdate expectedCounter = new CounterUpdate().setNameAndKind(new NameAndKind().setName("some-counter").setKind("SUM")).setCumulative(true).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(42));
        CounterSet counterSet = new CounterSet();
        counterSet.intSum(CounterName.named("some-counter")).addValue(42);
        final CounterUpdate expectedMetric = new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin("USER").setOriginNamespace("namespace").setName("some-counter").setOriginalStepName("step")).setMetadata(new CounterMetadata().setKind("SUM"))).setCumulative(true).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(42));
        MetricsContainerImpl metricsContainer = new MetricsContainerImpl("step");
        BatchModeExecutionContext context = Mockito.mock(BatchModeExecutionContext.class);
        Mockito.when(context.extractMetricUpdates(ArgumentMatchers.anyBoolean())).thenReturn(ImmutableList.of(expectedMetric));
        Mockito.when(context.extractMsecCounters(ArgumentMatchers.anyBoolean())).thenReturn(Collections.emptyList());
        CounterCell counter = metricsContainer.getCounter(MetricName.named("namespace", "some-counter"));
        counter.inc(1);
        counter.inc(41);
        counter.inc(1);
        counter.inc((-1));
        WorkItemStatus status = new WorkItemStatus();
        Mockito.when(worker.getOutputCounters()).thenReturn(counterSet);
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, context);
        statusClient.populateCounterUpdates(status);
        Assert.assertThat(status.getCounterUpdates(), Matchers.containsInAnyOrder(expectedCounter, expectedMetric));
    }

    @Test
    public void populateCounterUpdatesWithMsecCounter() throws Exception {
        final CounterUpdate expectedMsec = new CounterUpdate().setStructuredNameAndMetadata(new CounterStructuredNameAndMetadata().setName(new CounterStructuredName().setOrigin("SYSTEM").setName("start-msecs").setOriginalStepName("step")).setMetadata(new CounterMetadata().setKind("SUM"))).setCumulative(true).setInteger(DataflowCounterUpdateExtractor.longToSplitInt(42));
        BatchModeExecutionContext context = Mockito.mock(BatchModeExecutionContext.class);
        Mockito.when(context.extractMetricUpdates(ArgumentMatchers.anyBoolean())).thenReturn(ImmutableList.of());
        Mockito.when(context.extractMsecCounters(ArgumentMatchers.anyBoolean())).thenReturn(ImmutableList.of(expectedMsec));
        WorkItemStatus status = new WorkItemStatus();
        Mockito.when(worker.extractMetricUpdates()).thenReturn(Collections.emptyList());
        statusClient.setWorker(worker, context);
        statusClient.populateCounterUpdates(status);
        Assert.assertThat(status.getCounterUpdates(), Matchers.containsInAnyOrder(expectedMsec));
    }

    @Test
    public void populateProgressNull() throws Exception {
        WorkItemStatus status = new WorkItemStatus();
        statusClient.setWorker(worker, executionContext);
        statusClient.populateProgress(status);
        Assert.assertThat(status.getReportedProgress(), Matchers.nullValue());
    }

    @Test
    public void populateProgress() throws Exception {
        WorkItemStatus status = new WorkItemStatus();
        Progress progress = SourceTranslationUtils.cloudProgressToReaderProgress(ReaderTestUtils.approximateProgressAtIndex(42L));
        Mockito.when(worker.getWorkerProgress()).thenReturn(progress);
        statusClient.setWorker(worker, executionContext);
        statusClient.populateProgress(status);
        Assert.assertThat(status.getReportedProgress(), Matchers.equalTo(ReaderTestUtils.approximateProgressAtIndex(42L)));
    }

    @Test
    public void populateSplitResultNativeReader() throws Exception {
        WorkItemStatus status = new WorkItemStatus();
        statusClient.setWorker(worker, executionContext);
        Position position = ReaderTestUtils.positionAtIndex(42L);
        DynamicSplitResult result = new NativeReader.DynamicSplitResultWithPosition(new org.apache.beam.runners.dataflow.worker.SourceTranslationUtils.DataflowReaderPosition(position));
        statusClient.populateSplitResult(status, result);
        Assert.assertThat(status.getStopPosition(), Matchers.equalTo(position));
        Assert.assertThat(status.getDynamicSourceSplit(), Matchers.nullValue());
    }

    @Test
    public void populateSplitResultCustomReader() throws Exception {
        WorkItemStatus status = new WorkItemStatus();
        statusClient.setWorker(worker, executionContext);
        BoundedSource<Integer> primary = new WorkItemStatusClientTest.DummyBoundedSource(5);
        BoundedSource<Integer> residual = new WorkItemStatusClientTest.DummyBoundedSource(10);
        BoundedSourceSplit<Integer> split = new BoundedSourceSplit(primary, residual);
        statusClient.populateSplitResult(status, split);
        Assert.assertThat(status.getDynamicSourceSplit(), Matchers.equalTo(WorkerCustomSources.toSourceSplit(split)));
        Assert.assertThat(status.getStopPosition(), Matchers.nullValue());
    }

    @Test
    public void populateSplitResultNull() throws Exception {
        WorkItemStatus status = new WorkItemStatus();
        statusClient.setWorker(worker, executionContext);
        statusClient.populateSplitResult(status, null);
        Assert.assertThat(status.getDynamicSourceSplit(), Matchers.nullValue());
        Assert.assertThat(status.getStopPosition(), Matchers.nullValue());
    }

    @Test
    public void reportUpdateBeforeSetWorker() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("setWorker");
        thrown.expectMessage("reportUpdate");
        statusClient.reportUpdate(null, null);
    }

    private static class DummyBoundedSource extends BoundedSource<Integer> {
        private final int number;

        public DummyBoundedSource(int number) {
            this.number = number;
        }

        @Override
        public List<? extends BoundedSource<Integer>> split(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public BoundedReader<Integer> createReader(PipelineOptions options) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void validate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Coder<Integer> getDefaultOutputCoder() {
            return null;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(number);
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (!(obj instanceof WorkItemStatusClientTest.DummyBoundedSource)) {
                return false;
            }
            return (number) == (((WorkItemStatusClientTest.DummyBoundedSource) (obj)).number);
        }
    }
}

