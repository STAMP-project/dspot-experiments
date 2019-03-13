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


import DataflowElementExecutionTracker.TIME_PER_ELEMENT_EXPERIMENT;
import NativeReader.DynamicSplitRequest;
import NativeReader.DynamicSplitResult;
import NativeReader.Progress;
import com.google.api.services.dataflow.model.ApproximateReportedProgress;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.runners.core.metrics.ExecutionStateSampler;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricUpdateMatchers;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowExecutionStateTracker;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.ReaderTestUtils;
import org.apache.beam.runners.dataflow.worker.SourceTranslationUtils;
import org.apache.beam.runners.dataflow.worker.TestOperationContext;
import org.apache.beam.runners.dataflow.worker.counters.Counter;
import org.apache.beam.runners.dataflow.worker.counters.Counter.CounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.CounterFactory.CounterDistribution;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.dataflow.worker.profiler.ScopedProfiler.NoopProfileScope;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Tests for {@link MapTaskExecutor}.
 */
@RunWith(JUnit4.class)
public class MapTaskExecutorTest {
    private static final String COUNTER_PREFIX = "test-";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final CounterSet counterSet = new CounterSet();

    static class TestOperation extends Operation {
        boolean aborted = false;

        private final Counter<Long, ?> counter;

        TestOperation(String counterPrefix, long count, OperationContext context) {
            super(new OutputReceiver[]{  }, context);
            counter = context.counterFactory().longSum(CounterName.named((counterPrefix + "ElementCount")));
            this.counter.addValue(count);
        }

        @Override
        public void abort() throws Exception {
            aborted = true;
            super.abort();
        }
    }

    // A mock ReadOperation fed to a MapTaskExecutor in test.
    static class TestReadOperation extends ReadOperation {
        private ApproximateReportedProgress progress = null;

        TestReadOperation(OutputReceiver outputReceiver, OperationContext context) {
            super(new ExecutorTestUtils.TestReader(), new OutputReceiver[]{ outputReceiver }, context, ReadOperation.bytesCounterName(context));
        }

        @Override
        public Progress getProgress() {
            return SourceTranslationUtils.cloudProgressToReaderProgress(progress);
        }

        @Override
        public DynamicSplitResult requestDynamicSplit(NativeReader.DynamicSplitRequest splitRequest) {
            // Fakes the return with the same position as proposed.
            return new NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(SourceTranslationUtils.splitRequestToApproximateSplitRequest(splitRequest).getPosition()));
        }

        public void setProgress(ApproximateReportedProgress progress) {
            this.progress = progress;
        }
    }

    @Test
    public void testExecuteMapTaskExecutor() throws Exception {
        List<String> log = new ArrayList<>();
        Operation o1 = Mockito.mock(Operation.class);
        Operation o2 = Mockito.mock(Operation.class);
        Operation o3 = Mockito.mock(Operation.class);
        List<Operation> operations = Arrays.asList(new Operation[]{ o1, o2, o3 });
        ExecutionStateTracker stateTracker = Mockito.mock(ExecutionStateTracker.class);
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            executor.execute();
        }
        InOrder inOrder = Mockito.inOrder(stateTracker, o1, o2, o3);
        inOrder.verify(o3).start();
        inOrder.verify(o2).start();
        inOrder.verify(o1).start();
        inOrder.verify(o1).finish();
        inOrder.verify(o2).finish();
        inOrder.verify(o3).finish();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetOutputCounters() throws Exception {
        List<Operation> operations = Arrays.asList(new Operation[]{ createOperation("o1", 1), createOperation("o2", 2), createOperation("o3", 3) });
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            CounterSet counterSet = executor.getOutputCounters();
            CounterUpdateExtractor<?> updateExtractor = Mockito.mock(CounterUpdateExtractor.class);
            counterSet.extractUpdates(false, updateExtractor);
            Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("test-o1-ElementCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(1L));
            Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("test-o2-ElementCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(2L));
            Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("test-o3-ElementCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(3L));
            Mockito.verifyNoMoreInteractions(updateExtractor);
        }
    }

    private static class NoopParDoFn implements ParDoFn {
        @Override
        public void startBundle(Receiver... receivers) {
        }

        @Override
        public void processElement(Object elem) {
        }

        @Override
        public void processTimers() {
        }

        @Override
        public void finishBundle() {
        }

        @Override
        public void abort() {
        }
    }

    /**
     * Verify counts for the per-element-output-time counter are correct.
     */
    @Test
    public void testPerElementProcessingTimeCounters() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.as(DataflowPipelineDebugOptions.class).setExperiments(Lists.newArrayList(TIME_PER_ELEMENT_EXPERIMENT));
        ExecutionStateSampler stateSampler = ExecutionStateSampler.newForTest();
        DataflowExecutionStateTracker stateTracker = new DataflowExecutionStateTracker(stateSampler, /* requestingStepName */
        /* sideInputIndex */
        /* metricsContainer */
        new TestOperationContext.TestDataflowExecutionState(NameContext.forStage("test-stage"), "other", null, null, null, NoopProfileScope.NOOP), counterSet, options, "test-work-item-id");
        NameContext parDoName = nameForStep("s1");
        // Wire a read operation with 3 elements to a ParDoOperation and assert that we count
        // the correct number of elements.
        ReadOperation read = ReadOperation.forTest(new ExecutorTestUtils.TestReader("a", "b", "c"), new OutputReceiver(), TestOperationContext.create(counterSet, nameForStep("s0"), null, stateTracker));
        ParDoOperation parDo = new ParDoOperation(new MapTaskExecutorTest.NoopParDoFn(), new OutputReceiver[0], TestOperationContext.create(counterSet, parDoName, null, stateTracker));
        parDo.attachInput(read, 0);
        List<Operation> operations = Lists.newArrayList(read, parDo);
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            executor.execute();
        }
        stateSampler.doSampling(100L);
        CounterName counterName = CounterName.named("per-element-processing-time").withOriginalName(parDoName);
        Counter<Long, CounterDistribution> counter = ((Counter<Long, CounterDistribution>) (counterSet.getExistingCounter(counterName)));
        Assert.assertThat(counter.getAggregate().getCount(), Matchers.equalTo(3L));
    }

    /**
     * This test makes sure that any metrics reported within an operation are part of the metric
     * containers returned by {@link getMetricContainers}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetMetricContainers() throws Exception {
        ExecutionStateTracker stateTracker = new DataflowExecutionStateTracker(ExecutionStateSampler.newForTest(), /* requestingStepName */
        /* sideInputIndex */
        /* metricsContainer */
        new TestOperationContext.TestDataflowExecutionState(NameContext.forStage("testStage"), "other", null, null, null, NoopProfileScope.NOOP), new CounterSet(), PipelineOptionsFactory.create(), "test-work-item-id");
        final String o1 = "o1";
        TestOperationContext context1 = createContext(o1, stateTracker);
        final String o2 = "o2";
        TestOperationContext context2 = createContext(o2, stateTracker);
        final String o3 = "o3";
        TestOperationContext context3 = createContext(o3, stateTracker);
        List<Operation> operations = Arrays.asList(new Operation(new OutputReceiver[]{  }, context1) {
            @Override
            public void start() throws Exception {
                super.start();
                try (Closeable scope = context.enterStart()) {
                    Metrics.counter("TestMetric", "MetricCounter").inc(1L);
                }
            }
        }, new Operation(new OutputReceiver[]{  }, context2) {
            @Override
            public void start() throws Exception {
                super.start();
                try (Closeable scope = context.enterStart()) {
                    Metrics.counter("TestMetric", "MetricCounter").inc(2L);
                }
            }
        }, new Operation(new OutputReceiver[]{  }, context3) {
            @Override
            public void start() throws Exception {
                super.start();
                try (Closeable scope = context.enterStart()) {
                    Metrics.counter("TestMetric", "MetricCounter").inc(3L);
                }
            }
        });
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            // Call execute so that we run all the counters
            executor.execute();
            Assert.assertThat(context1.metricsContainer().getUpdates().counterUpdates(), Matchers.contains(MetricUpdateMatchers.metricUpdate("TestMetric", "MetricCounter", o1, 1L)));
            Assert.assertThat(context2.metricsContainer().getUpdates().counterUpdates(), Matchers.contains(MetricUpdateMatchers.metricUpdate("TestMetric", "MetricCounter", o2, 2L)));
            Assert.assertThat(context3.metricsContainer().getUpdates().counterUpdates(), Matchers.contains(MetricUpdateMatchers.metricUpdate("TestMetric", "MetricCounter", o3, 3L)));
        }
    }

    @Test
    public void testNoOperation() throws Exception {
        // Test MapTaskExecutor without a single operation.
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(new ArrayList<Operation>(), counterSet, stateTracker)) {
            thrown.expect(IllegalStateException.class);
            thrown.expectMessage("has no operation");
            executor.getReadOperation();
        }
    }

    @Test
    public void testNoReadOperation() throws Exception {
        // Test MapTaskExecutor without ReadOperation.
        List<Operation> operations = Arrays.<Operation>asList(createOperation("o1", 1), createOperation("o2", 2));
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            thrown.expect(IllegalStateException.class);
            thrown.expectMessage("is not a ReadOperation");
            executor.getReadOperation();
        }
    }

    @Test
    public void testValidOperations() throws Exception {
        TestOutputReceiver receiver = new TestOutputReceiver(counterSet, NameContextsForTests.nameContextForTest());
        List<Operation> operations = Arrays.<Operation>asList(new MapTaskExecutorTest.TestReadOperation(receiver, createContext("ReadOperation")));
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(operations, counterSet, stateTracker)) {
            Assert.assertEquals(operations.get(0), executor.getReadOperation());
        }
    }

    @Test
    public void testGetProgressAndRequestSplit() throws Exception {
        TestOutputReceiver receiver = new TestOutputReceiver(counterSet, NameContextsForTests.nameContextForTest());
        MapTaskExecutorTest.TestReadOperation operation = new MapTaskExecutorTest.TestReadOperation(receiver, createContext("ReadOperation"));
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(Arrays.asList(new Operation[]{ operation }), counterSet, stateTracker)) {
            operation.setProgress(ReaderTestUtils.approximateProgressAtIndex(1L));
            Assert.assertEquals(ReaderTestUtils.positionAtIndex(1L), ReaderTestUtils.positionFromProgress(executor.getWorkerProgress()));
            Assert.assertEquals(ReaderTestUtils.positionAtIndex(1L), ReaderTestUtils.positionFromSplitResult(executor.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(1L))));
        }
    }

    @Test
    public void testExceptionInStartAbortsAllOperations() throws Exception {
        Operation o1 = Mockito.mock(Operation.class);
        Operation o2 = Mockito.mock(Operation.class);
        Operation o3 = Mockito.mock(Operation.class);
        Mockito.doThrow(new Exception("in start")).when(o2).start();
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(Arrays.<Operation>asList(o1, o2, o3), counterSet, stateTracker)) {
            executor.execute();
            Assert.fail("Should have thrown");
        } catch (Exception e) {
            InOrder inOrder = Mockito.inOrder(o1, o2, o3);
            inOrder.verify(o3).start();
            inOrder.verify(o2).start();
            // Order of abort doesn't matter
            Mockito.verify(o1).abort();
            Mockito.verify(o2).abort();
            Mockito.verify(o3).abort();
            Mockito.verifyNoMoreInteractions(o1, o2, o3);
        }
    }

    @Test
    public void testExceptionInFinishAbortsAllOperations() throws Exception {
        Operation o1 = Mockito.mock(Operation.class);
        Operation o2 = Mockito.mock(Operation.class);
        Operation o3 = Mockito.mock(Operation.class);
        Mockito.doThrow(new Exception("in finish")).when(o2).finish();
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(Arrays.<Operation>asList(o1, o2, o3), counterSet, stateTracker)) {
            executor.execute();
            Assert.fail("Should have thrown");
        } catch (Exception e) {
            InOrder inOrder = Mockito.inOrder(o1, o2, o3);
            inOrder.verify(o3).start();
            inOrder.verify(o2).start();
            inOrder.verify(o1).start();
            inOrder.verify(o1).finish();
            inOrder.verify(o2).finish();
            // Order of abort doesn't matter
            Mockito.verify(o1).abort();
            Mockito.verify(o2).abort();
            Mockito.verify(o3).abort();
            Mockito.verifyNoMoreInteractions(o1, o2, o3);
        }
    }

    @Test
    public void testExceptionInAbortSuppressed() throws Exception {
        Operation o1 = Mockito.mock(Operation.class);
        Operation o2 = Mockito.mock(Operation.class);
        Operation o3 = Mockito.mock(Operation.class);
        Operation o4 = Mockito.mock(Operation.class);
        Mockito.doThrow(new Exception("in finish")).when(o2).finish();
        Mockito.doThrow(new Exception("suppressed in abort")).when(o3).abort();
        ExecutionStateTracker stateTracker = ExecutionStateTracker.newForTest();
        try (MapTaskExecutor executor = new MapTaskExecutor(Arrays.<Operation>asList(o1, o2, o3, o4), counterSet, stateTracker)) {
            executor.execute();
            Assert.fail("Should have thrown");
        } catch (Exception e) {
            InOrder inOrder = Mockito.inOrder(o1, o2, o3, o4);
            inOrder.verify(o4).start();
            inOrder.verify(o3).start();
            inOrder.verify(o2).start();
            inOrder.verify(o1).start();
            inOrder.verify(o1).finish();
            inOrder.verify(o2).finish();// this fails

            // Order of abort doesn't matter
            Mockito.verify(o1).abort();
            Mockito.verify(o2).abort();
            Mockito.verify(o3).abort();// will throw an exception, but we shouldn't fail

            Mockito.verify(o4).abort();
            Mockito.verifyNoMoreInteractions(o1, o2, o3, o4);
            // Make sure the failure while aborting shows up as a suppressed error
            Assert.assertThat(e.getMessage(), Matchers.equalTo("in finish"));
            Assert.assertThat(e.getSuppressed(), Matchers.arrayWithSize(1));
            Assert.assertThat(e.getSuppressed()[0].getMessage(), Matchers.equalTo("suppressed in abort"));
        }
    }
}

