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


import LateDataDroppingDoFnRunner.DROPPED_DUE_TO_LATENESS;
import Timer.Type.WATERMARK;
import TimestampCombiner.EARLIEST;
import WorkItem.Builder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.beam.runners.core.DoFnRunner;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.KeyedWorkItem;
import org.apache.beam.runners.core.LateDataDroppingDoFnRunner;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.StepContext;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.runners.core.WindowMatchers;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.runners.dataflow.worker.util.ListOutputManager;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.InputMessageBundle;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.WorkItem;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.CollectionCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricsEnvironment;
import org.apache.beam.sdk.transforms.Combine.CombineFn;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.util.AppliedCombineFn;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

import static KvMatcher.isKv;


/**
 * Unit tests for {@link StreamingGroupAlsoByWindowsDoFns}.
 */
@RunWith(JUnit4.class)
public class StreamingGroupAlsoByWindowFnsTest {
    private static final String KEY = "k";

    private static final String STATE_FAMILY = "stateFamily";

    private static final long WORK_TOKEN = 1000L;

    private static final String SOURCE_COMPUTATION_ID = "sourceComputationId";

    private static final String STEP_NAME = "merge";

    @Mock
    private TimerInternals mockTimerInternals;

    private Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();

    private Coder<Collection<IntervalWindow>> windowsCoder = CollectionCoder.of(windowCoder);

    private StreamingGroupAlsoByWindowFnsTest.TestStepContext stepContext;

    @Test
    public void testReshufle() throws Exception {
        GroupAlsoByWindowFn<?, ?> fn = StreamingGroupAlsoByWindowsDoFns.createForIterable(WindowingStrategy.of(FixedWindows.of(Duration.standardSeconds(30))).withTrigger(new org.apache.beam.sdk.transforms.windowing.ReshuffleTrigger()), new StepContextStateInternalsFactory(stepContext), VarIntCoder.of());
        Assert.assertThat(fn, Matchers.instanceOf(StreamingGroupAlsoByWindowReshuffleFn.class));
    }

    @Test
    public void testEmpty() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(FixedWindows.of(Duration.millis(10))));
        runner.startBundle();
        runner.finishBundle();
        List<?> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(0));
    }

    @Test
    public void testFixedWindows() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(FixedWindows.of(Duration.millis(10))));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(0));
        runner.startBundle();
        WorkItem.Builder workItem1 = WorkItem.newBuilder();
        workItem1.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem1.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem1.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowFnsTest.SOURCE_COMPUTATION_ID);
        Coder<String> valueCoder = StringUtf8Coder.of();
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(1), valueCoder, "v1");
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(2), valueCoder, "v2");
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(0), valueCoder, "v0");
        addElement(messageBundle, Arrays.asList(window(10, 20)), new Instant(13), valueCoder, "v3");
        runner.processElement(createValue(workItem1, valueCoder));
        runner.finishBundle();
        runner.startBundle();
        WorkItem.Builder workItem2 = WorkItem.newBuilder();
        workItem2.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem2.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        addTimer(workItem2, window(0, 10), new Instant(9), WATERMARK);
        addTimer(workItem2, window(10, 20), new Instant(19), WATERMARK);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(20));
        runner.processElement(createValue(workItem2, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Iterable<String>>>> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(2));
        Assert.assertThat(result, Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v0", "v1", "v2")), Matchers.equalTo(window(0, 10).maxTimestamp()), Matchers.equalTo(window(0, 10))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v3")), Matchers.equalTo(window(10, 20).maxTimestamp()), Matchers.equalTo(window(10, 20)))));
    }

    @Test
    public void testSlidingWindows() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(SlidingWindows.of(Duration.millis(20)).every(Duration.millis(10))).withTimestampCombiner(EARLIEST));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(5));
        runner.startBundle();
        WorkItem.Builder workItem1 = WorkItem.newBuilder();
        workItem1.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem1.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem1.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowFnsTest.SOURCE_COMPUTATION_ID);
        Coder<String> valueCoder = StringUtf8Coder.of();
        addElement(messageBundle, Arrays.asList(window((-10), 10), window(0, 20)), new Instant(5), valueCoder, "v1");
        addElement(messageBundle, Arrays.asList(window((-10), 10), window(0, 20)), new Instant(2), valueCoder, "v0");
        addElement(messageBundle, Arrays.asList(window(0, 20), window(10, 30)), new Instant(15), valueCoder, "v2");
        runner.processElement(createValue(workItem1, valueCoder));
        runner.finishBundle();
        runner.startBundle();
        WorkItem.Builder workItem2 = WorkItem.newBuilder();
        workItem2.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem2.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        addTimer(workItem2, window((-10), 10), new Instant(9), WATERMARK);
        addTimer(workItem2, window(0, 20), new Instant(19), WATERMARK);
        addTimer(workItem2, window(10, 30), new Instant(29), WATERMARK);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(30));
        runner.processElement(createValue(workItem2, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Iterable<String>>>> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(3));
        Assert.assertThat(result, // For this sliding window, the minimum output timestmap was 10, since we didn't want to
        // overlap with the previous window that was [-10, 10).
        Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v0", "v1")), Matchers.equalTo(new Instant(2)), Matchers.equalTo(window((-10), 10))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v0", "v1", "v2")), Matchers.equalTo(window((-10), 10).maxTimestamp().plus(1)), Matchers.equalTo(window(0, 20))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v2")), Matchers.equalTo(window(0, 20).maxTimestamp().plus(1)), Matchers.equalTo(window(10, 30)))));
    }

    @Test
    public void testSlidingWindowsAndLateData() throws Exception {
        MetricsContainerImpl container = new MetricsContainerImpl("step");
        MetricsEnvironment.setCurrentContainer(container);
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        WindowingStrategy<? super String, IntervalWindow> windowingStrategy = WindowingStrategy.of(SlidingWindows.of(Duration.millis(20)).every(Duration.millis(10))).withTimestampCombiner(EARLIEST);
        GroupAlsoByWindowFn<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> fn = StreamingGroupAlsoByWindowsDoFns.createForIterable(windowingStrategy, new StepContextStateInternalsFactory<String>(stepContext), StringUtf8Coder.of());
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, windowingStrategy, fn);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(15));
        runner.startBundle();
        WorkItem.Builder workItem1 = WorkItem.newBuilder();
        workItem1.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem1.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem1.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowFnsTest.SOURCE_COMPUTATION_ID);
        Coder<String> valueCoder = StringUtf8Coder.of();
        addElement(messageBundle, Arrays.asList(window((-10), 10), window(0, 20)), new Instant(5), valueCoder, "v1");
        addElement(messageBundle, Arrays.asList(window((-10), 10), window(0, 20)), new Instant(2), valueCoder, "v0");
        addElement(messageBundle, Arrays.asList(window(0, 20), window(10, 30)), new Instant(15), valueCoder, "v2");
        runner.processElement(createValue(workItem1, valueCoder));
        runner.finishBundle();
        runner.startBundle();
        WorkItem.Builder workItem2 = WorkItem.newBuilder();
        workItem2.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem2.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        addTimer(workItem2, window((-10), 10), new Instant(9), WATERMARK);
        addTimer(workItem2, window(0, 20), new Instant(19), WATERMARK);
        addTimer(workItem2, window(10, 30), new Instant(29), WATERMARK);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(30));
        runner.processElement(createValue(workItem2, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Iterable<String>>>> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(3));
        Assert.assertThat(result, // For this sliding window, the minimum output timestmap was 10, since we didn't want to
        // overlap with the previous window that was [-10, 10).
        Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.emptyIterable()), Matchers.equalTo(window((-10), 10).maxTimestamp()), Matchers.equalTo(window((-10), 10))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v0", "v1", "v2")), Matchers.equalTo(window((-10), 10).maxTimestamp().plus(1)), Matchers.equalTo(window(0, 20))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v2")), Matchers.equalTo(window(0, 20).maxTimestamp().plus(1)), Matchers.equalTo(window(10, 30)))));
        long droppedValues = container.getCounter(MetricName.named(LateDataDroppingDoFnRunner.class, DROPPED_DUE_TO_LATENESS)).getCumulative().longValue();
        Assert.assertThat(droppedValues, Matchers.equalTo(2L));
    }

    @Test
    public void testSessions() throws Exception {
        TupleTag<KV<String, Iterable<String>>> outputTag = new TupleTag();
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, String>, KV<String, Iterable<String>>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(Sessions.withGapDuration(Duration.millis(10))).withTimestampCombiner(EARLIEST));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(0));
        runner.startBundle();
        WorkItem.Builder workItem1 = WorkItem.newBuilder();
        workItem1.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem1.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem1.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowFnsTest.SOURCE_COMPUTATION_ID);
        Coder<String> valueCoder = StringUtf8Coder.of();
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(0), valueCoder, "v1");
        addElement(messageBundle, Arrays.asList(window(5, 15)), new Instant(5), valueCoder, "v2");
        addElement(messageBundle, Arrays.asList(window(15, 25)), new Instant(15), valueCoder, "v3");
        addElement(messageBundle, Arrays.asList(window(3, 13)), new Instant(3), valueCoder, "v0");
        runner.processElement(createValue(workItem1, valueCoder));
        runner.finishBundle();
        runner.startBundle();
        WorkItem.Builder workItem2 = WorkItem.newBuilder();
        workItem2.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem2.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        // Note that the WATERMARK timer for Instant(9) will have been deleted by
        // ReduceFnRunner when window(0, 10) was merged away.
        addTimer(workItem2, window(0, 15), new Instant(14), WATERMARK);
        addTimer(workItem2, window(15, 25), new Instant(24), WATERMARK);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(25));
        runner.processElement(createValue(workItem2, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Iterable<String>>>> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(2));
        Assert.assertThat(result, Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v0", "v1", "v2")), Matchers.equalTo(new Instant(0)), Matchers.equalTo(window(0, 15))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.containsInAnyOrder("v3")), Matchers.equalTo(new Instant(15)), Matchers.equalTo(window(15, 25)))));
    }

    private final class TestStepContext implements StepContext {
        private StateInternals stateInternals;

        private TestStepContext(String stepName) {
            this.stateInternals = InMemoryStateInternals.forKey(stepName);
        }

        @Override
        public TimerInternals timerInternals() {
            return mockTimerInternals;
        }

        @Override
        public StateInternals stateInternals() {
            return stateInternals;
        }
    }

    /**
     * A custom combine fn that doesn't take any performance shortcuts to ensure that we are using the
     * CombineFn API properly.
     */
    private static class SumLongs extends CombineFn<Long, Long, Long> {
        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long addInput(Long accumulator, Long input) {
            return accumulator + input;
        }

        @Override
        public Long mergeAccumulators(Iterable<Long> accumulators) {
            Long sum = 0L;
            for (Long value : accumulators) {
                sum += value;
            }
            return sum;
        }

        @Override
        public Long extractOutput(Long accumulator) {
            return accumulator;
        }
    }

    @Test
    public void testSessionsCombine() throws Exception {
        TupleTag<KV<String, Long>> outputTag = new TupleTag();
        CombineFn<Long, ?, Long> combineFn = new StreamingGroupAlsoByWindowFnsTest.SumLongs();
        CoderRegistry registry = CoderRegistry.createDefault();
        AppliedCombineFn<String, Long, ?, Long> appliedCombineFn = AppliedCombineFn.withInputCoder(combineFn, registry, KvCoder.of(StringUtf8Coder.of(), BigEndianLongCoder.of()));
        ListOutputManager outputManager = new ListOutputManager();
        DoFnRunner<KeyedWorkItem<String, Long>, KV<String, Long>> runner = makeRunner(outputTag, outputManager, WindowingStrategy.of(Sessions.withGapDuration(Duration.millis(10))), appliedCombineFn);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(0));
        runner.startBundle();
        WorkItem.Builder workItem1 = WorkItem.newBuilder();
        workItem1.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem1.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        InputMessageBundle.Builder messageBundle = workItem1.addMessageBundlesBuilder();
        messageBundle.setSourceComputationId(StreamingGroupAlsoByWindowFnsTest.SOURCE_COMPUTATION_ID);
        Coder<Long> valueCoder = BigEndianLongCoder.of();
        addElement(messageBundle, Arrays.asList(window(0, 10)), new Instant(0), valueCoder, 1L);
        addElement(messageBundle, Arrays.asList(window(5, 15)), new Instant(5), valueCoder, 2L);
        addElement(messageBundle, Arrays.asList(window(15, 25)), new Instant(15), valueCoder, 3L);
        addElement(messageBundle, Arrays.asList(window(3, 13)), new Instant(3), valueCoder, 4L);
        runner.processElement(createValue(workItem1, valueCoder));
        runner.finishBundle();
        runner.startBundle();
        WorkItem.Builder workItem2 = WorkItem.newBuilder();
        workItem2.setKey(ByteString.copyFromUtf8(StreamingGroupAlsoByWindowFnsTest.KEY));
        workItem2.setWorkToken(StreamingGroupAlsoByWindowFnsTest.WORK_TOKEN);
        // Note that the WATERMARK timer for Instant(9) will have been deleted by
        // ReduceFnRunner when window(0, 10) was merged away.
        addTimer(workItem2, window(0, 15), new Instant(14), WATERMARK);
        addTimer(workItem2, window(15, 25), new Instant(24), WATERMARK);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(25));
        runner.processElement(createValue(workItem2, valueCoder));
        runner.finishBundle();
        List<WindowedValue<KV<String, Long>>> result = outputManager.getOutput(outputTag);
        Assert.assertThat(result.size(), Matchers.equalTo(2));
        Assert.assertThat(result, Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.equalTo(7L)), Matchers.equalTo(window(0, 15).maxTimestamp()), Matchers.equalTo(window(0, 15))), WindowMatchers.isSingleWindowedValue(isKv(Matchers.equalTo(StreamingGroupAlsoByWindowFnsTest.KEY), Matchers.equalTo(3L)), Matchers.equalTo(window(15, 25).maxTimestamp()), Matchers.equalTo(window(15, 25)))));
    }
}

