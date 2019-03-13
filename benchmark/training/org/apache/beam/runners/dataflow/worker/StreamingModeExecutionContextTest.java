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


import ByteString.EMPTY;
import ExecutionStateTracker.PROCESS_STATE_NAME;
import ExecutionStateTracker.START_STATE_NAME;
import NoopProfileScope.NOOP;
import StreamingModeExecutionContext.StepContext;
import TimeDomain.EVENT_TIME;
import Windmill.Timer;
import Windmill.Timer.Type.REALTIME;
import Windmill.Timer.Type.WATERMARK;
import Windmill.WorkItem;
import Windmill.WorkItemCommitRequest.Builder;
import com.google.api.services.dataflow.model.CounterUpdate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.StateNamespaceForTest;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.core.metrics.ExecutionStateSampler;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker.ExecutionState;
import org.apache.beam.runners.dataflow.worker.StreamingModeExecutionContext.StreamingModeExecutionState;
import org.apache.beam.runners.dataflow.worker.StreamingModeExecutionContext.StreamingModeExecutionStateRegistry;
import org.apache.beam.runners.dataflow.worker.counters.DataflowCounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.dataflow.worker.profiler.ScopedProfiler.NoopProfileScope;
import org.apache.beam.runners.dataflow.worker.profiler.ScopedProfiler.ProfileScope;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.metrics.MetricsContainer;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamingModeExecutionContext}.
 */
@RunWith(JUnit4.class)
public class StreamingModeExecutionContextTest {
    @Mock
    private StateFetcher stateFetcher;

    @Mock
    private WindmillStateReader stateReader;

    private StreamingModeExecutionStateRegistry executionStateRegistry = new StreamingModeExecutionStateRegistry(null);

    private StreamingModeExecutionContext executionContext;

    @Test
    public void testTimerInternalsSetTimer() {
        Windmill.WorkItemCommitRequest.Builder outputBuilder = Windmill.WorkItemCommitRequest.newBuilder();
        NameContext nameContext = NameContextsForTests.nameContextForTest();
        DataflowOperationContext operationContext = executionContext.createOperationContext(nameContext);
        StreamingModeExecutionContext.StepContext stepContext = executionContext.getStepContext(operationContext);
        // input watermark
        // output watermark
        // synchronized processing time
        executionContext.start("key", WorkItem.newBuilder().setKey(EMPTY).setWorkToken(17L).build(), new Instant(1000), null, null, stateReader, stateFetcher, outputBuilder);
        TimerInternals timerInternals = stepContext.timerInternals();
        timerInternals.setTimer(TimerData.of(new StateNamespaceForTest("key"), new Instant(5000), EVENT_TIME));
        executionContext.flushState();
        Windmill.Timer timer = outputBuilder.buildPartial().getOutputTimers(0);
        Assert.assertThat(timer.getTag().toStringUtf8(), Matchers.equalTo("/skey+0:5000"));
        Assert.assertThat(timer.getTimestamp(), Matchers.equalTo(TimeUnit.MILLISECONDS.toMicros(5000)));
        Assert.assertThat(timer.getType(), Matchers.equalTo(WATERMARK));
    }

    @Test
    public void testTimerInternalsProcessingTimeSkew() {
        Windmill.WorkItemCommitRequest.Builder outputBuilder = Windmill.WorkItemCommitRequest.newBuilder();
        NameContext nameContext = NameContextsForTests.nameContextForTest();
        DataflowOperationContext operationContext = executionContext.createOperationContext(nameContext);
        StreamingModeExecutionContext.StepContext stepContext = executionContext.getStepContext(operationContext);
        Windmill.WorkItem.Builder workItemBuilder = WorkItem.newBuilder().setKey(EMPTY).setWorkToken(17L);
        Windmill.Timer.Builder timerBuilder = workItemBuilder.getTimersBuilder().addTimersBuilder();
        // Trigger a realtime timer that with clock skew but ensure that it would
        // still fire.
        Instant now = Instant.now();
        long offsetMillis = 60 * 1000;
        Instant timerTimestamp = now.plus(offsetMillis);
        timerBuilder.setTag(ByteString.copyFromUtf8("a")).setTimestamp(((timerTimestamp.getMillis()) * 1000)).setType(REALTIME);
        // input watermark
        // output watermark
        // synchronized processing time
        executionContext.start("key", workItemBuilder.build(), new Instant(1000), null, null, stateReader, stateFetcher, outputBuilder);
        TimerInternals timerInternals = stepContext.timerInternals();
        Assert.assertTrue(timerTimestamp.isBefore(timerInternals.currentProcessingTime()));
    }

    /**
     * Tests that the {@link SideInputReader} returned by the {@link StreamingModeExecutionContext}
     * contains the expected views when they are deserialized, as occurs on the service.
     */
    @Test
    public void testSideInputReaderReconstituted() {
        Pipeline p = Pipeline.create();
        PCollectionView<String> preview1 = p.apply(Create.of("")).apply(View.asSingleton());
        PCollectionView<String> preview2 = p.apply(Create.of("")).apply(View.asSingleton());
        PCollectionView<String> preview3 = p.apply(Create.of("")).apply(View.asSingleton());
        SideInputReader sideInputReader = executionContext.getSideInputReaderForViews(Arrays.asList(preview1, preview2));
        Assert.assertTrue(sideInputReader.contains(preview1));
        Assert.assertTrue(sideInputReader.contains(preview2));
        Assert.assertFalse(sideInputReader.contains(preview3));
        PCollectionView<String> view1 = SerializableUtils.ensureSerializable(preview1);
        PCollectionView<String> view2 = SerializableUtils.ensureSerializable(preview2);
        PCollectionView<String> view3 = SerializableUtils.ensureSerializable(preview3);
        Assert.assertTrue(sideInputReader.contains(view1));
        Assert.assertTrue(sideInputReader.contains(view2));
        Assert.assertFalse(sideInputReader.contains(view3));
    }

    @Test
    public void extractMsecCounters() {
        MetricsContainer metricsContainer = Mockito.mock(MetricsContainer.class);
        ProfileScope profileScope = Mockito.mock(ProfileScope.class);
        ExecutionState start1 = executionContext.executionStateRegistry.getState(NameContext.create("stage", "original-1", "system-1", "user-1"), START_STATE_NAME, metricsContainer, profileScope);
        ExecutionState process1 = executionContext.executionStateRegistry.getState(NameContext.create("stage", "original-1", "system-1", "user-1"), PROCESS_STATE_NAME, metricsContainer, profileScope);
        ExecutionState start2 = executionContext.executionStateRegistry.getState(NameContext.create("stage", "original-2", "system-2", "user-2"), START_STATE_NAME, metricsContainer, profileScope);
        ExecutionState other = executionContext.executionStateRegistry.getState(NameContext.forStage("stage"), "other", null, NOOP);
        other.takeSample(120);
        start1.takeSample(100);
        process1.takeSample(500);
        Assert.assertThat(executionStateRegistry.extractUpdates(false), Matchers.containsInAnyOrder(msecStage("other-msecs", "stage", 120), msec("start-msecs", "stage", "original-1", 100), msec("process-msecs", "stage", "original-1", 500)));
        process1.takeSample(200);
        start2.takeSample(200);
        Assert.assertThat(executionStateRegistry.extractUpdates(false), Matchers.containsInAnyOrder(msec("process-msecs", "stage", "original-1", 200), msec("start-msecs", "stage", "original-2", 200)));
        process1.takeSample(300);
        Assert.assertThat(executionStateRegistry.extractUpdates(false), Matchers.containsInAnyOrder(msec("process-msecs", "stage", "original-1", 300)));
    }

    /**
     * Ensure that incrementing and extracting counter updates are correct under concurrent reader and
     * writer threads.
     */
    @Test
    public void testAtomicExtractUpdate() throws InterruptedException, ExecutionException {
        long numUpdates = 1000000;
        StreamingModeExecutionState state = new StreamingModeExecutionState(NameContextsForTests.nameContextForTest(), "testState", null, NoopProfileScope.NOOP, null);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicBoolean doneWriting = new AtomicBoolean(false);
        Callable<Long> reader = () -> {
            long count = 0;
            boolean isLastRead;
            do {
                isLastRead = doneWriting.get();
                CounterUpdate update = state.extractUpdate(false);
                if (update != null) {
                    count += DataflowCounterUpdateExtractor.splitIntToLong(update.getInteger());
                }
            } while (!isLastRead );
            return count;
        };
        Runnable writer = () -> {
            for (int i = 0; i < numUpdates; i++) {
                state.takeSample(1L);
            }
            doneWriting.set(true);
        };
        // NB: Reader is invoked before writer to ensure they execute concurrently.
        List<Future<Long>> results = executor.invokeAll(Lists.newArrayList(reader, Executors.callable(writer, 0L)), 2, TimeUnit.SECONDS);
        long count = results.get(0).get();
        Assert.assertThat(count, Matchers.equalTo(numUpdates));
    }

    @Test(timeout = 2000)
    public void stateSamplingInStreaming() {
        // Test that when writing on one thread and reading from another, updates always eventually
        // reach the reading thread.
        StreamingModeExecutionState state = new StreamingModeExecutionState(NameContextsForTests.nameContextForTest(), "testState", null, NoopProfileScope.NOOP, null);
        ExecutionStateSampler sampler = ExecutionStateSampler.newForTest();
        try {
            sampler.start();
            ExecutionStateTracker tracker = new ExecutionStateTracker(sampler);
            Thread executionThread = new Thread();
            executionThread.setName("looping-thread-for-test");
            tracker.activate(executionThread);
            tracker.enterState(state);
            // Wait for the state to be incremented 3 times
            for (int i = 0; i < 3; i++) {
                CounterUpdate update = null;
                while (update == null) {
                    update = state.extractUpdate(false);
                } 
                long newValue = DataflowCounterUpdateExtractor.splitIntToLong(update.getInteger());
                Assert.assertThat(newValue, Matchers.greaterThan(0L));
            }
        } finally {
            sampler.stop();
        }
    }
}

