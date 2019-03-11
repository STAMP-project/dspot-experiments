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
package org.apache.beam.runners.core;


import PaneInfo.NO_FIRING;
import StatefulDoFnRunner.DROPPED_DUE_TO_LATENESS_COUNTER;
import StatefulDoFnRunner.TimeInternalsCleanupTimer.GC_DELAY_MS;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricsEnvironment;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link StatefulDoFnRunnerTest}.
 */
@RunWith(JUnit4.class)
public class StatefulDoFnRunnerTest {
    private static final long WINDOW_SIZE = 10;

    private static final long ALLOWED_LATENESS = 1;

    private static final WindowingStrategy<?, ?> WINDOWING_STRATEGY = WindowingStrategy.of(FixedWindows.of(Duration.millis(StatefulDoFnRunnerTest.WINDOW_SIZE))).withAllowedLateness(Duration.millis(StatefulDoFnRunnerTest.ALLOWED_LATENESS));

    private static final IntervalWindow WINDOW_1 = new IntervalWindow(new Instant(0), new Instant(10));

    private static final IntervalWindow WINDOW_2 = new IntervalWindow(new Instant(10), new Instant(20));

    @Mock
    StepContext mockStepContext;

    private InMemoryStateInternals<String> stateInternals;

    private InMemoryTimerInternals timerInternals;

    @Test
    public void testLateDropping() throws Exception {
        MetricsContainerImpl container = new MetricsContainerImpl("any");
        MetricsEnvironment.setCurrentContainer(container);
        timerInternals.advanceInputWatermark(new Instant(BoundedWindow.TIMESTAMP_MAX_VALUE));
        timerInternals.advanceOutputWatermark(new Instant(BoundedWindow.TIMESTAMP_MAX_VALUE));
        DoFn<KV<String, Integer>, Integer> fn = new StatefulDoFnRunnerTest.MyDoFn();
        DoFnRunner<KV<String, Integer>, Integer> runner = DoFnRunners.defaultStatefulDoFnRunner(fn, getDoFnRunner(fn), StatefulDoFnRunnerTest.WINDOWING_STRATEGY, new StatefulDoFnRunner.TimeInternalsCleanupTimer(timerInternals, StatefulDoFnRunnerTest.WINDOWING_STRATEGY), new StatefulDoFnRunner.StateInternalsStateCleaner<>(fn, stateInternals, ((Coder) (WINDOWING_STRATEGY.getWindowFn().windowCoder()))));
        runner.startBundle();
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant((0L + (StatefulDoFnRunnerTest.WINDOW_SIZE))));
        Instant timestamp = new Instant(0);
        runner.processElement(WindowedValue.of(KV.of("hello", 1), timestamp, window, NO_FIRING));
        long droppedValues = container.getCounter(MetricName.named(StatefulDoFnRunner.class, DROPPED_DUE_TO_LATENESS_COUNTER)).getCumulative();
        Assert.assertEquals(1L, droppedValues);
        runner.finishBundle();
    }

    @Test
    public void testGarbageCollect() throws Exception {
        timerInternals.advanceInputWatermark(new Instant(1L));
        StatefulDoFnRunnerTest.MyDoFn fn = new StatefulDoFnRunnerTest.MyDoFn();
        StateTag<ValueState<Integer>> stateTag = StateTags.tagForSpec(fn.stateId, fn.intState);
        DoFnRunner<KV<String, Integer>, Integer> runner = DoFnRunners.defaultStatefulDoFnRunner(fn, getDoFnRunner(fn), StatefulDoFnRunnerTest.WINDOWING_STRATEGY, new StatefulDoFnRunner.TimeInternalsCleanupTimer(timerInternals, StatefulDoFnRunnerTest.WINDOWING_STRATEGY), new StatefulDoFnRunner.StateInternalsStateCleaner<>(fn, stateInternals, ((Coder) (WINDOWING_STRATEGY.getWindowFn().windowCoder()))));
        Instant elementTime = new Instant(1);
        // first element, key is hello, WINDOW_1
        runner.processElement(WindowedValue.of(KV.of("hello", 1), elementTime, StatefulDoFnRunnerTest.WINDOW_1, NO_FIRING));
        Assert.assertEquals(1, ((int) (stateInternals.state(StatefulDoFnRunnerTest.windowNamespace(StatefulDoFnRunnerTest.WINDOW_1), stateTag).read())));
        // second element, key is hello, WINDOW_2
        runner.processElement(WindowedValue.of(KV.of("hello", 1), elementTime.plus(StatefulDoFnRunnerTest.WINDOW_SIZE), StatefulDoFnRunnerTest.WINDOW_2, NO_FIRING));
        runner.processElement(WindowedValue.of(KV.of("hello", 1), elementTime.plus(StatefulDoFnRunnerTest.WINDOW_SIZE), StatefulDoFnRunnerTest.WINDOW_2, NO_FIRING));
        Assert.assertEquals(2, ((int) (stateInternals.state(StatefulDoFnRunnerTest.windowNamespace(StatefulDoFnRunnerTest.WINDOW_2), stateTag).read())));
        // advance watermark past end of WINDOW_1 + allowed lateness
        // the cleanup timer is set to window.maxTimestamp() + allowed lateness + 1
        // to ensure that state is still available when a user timer for window.maxTimestamp() fires
        // so the watermark is past the GC horizon, not on it
        StatefulDoFnRunnerTest.advanceInputWatermark(timerInternals, StatefulDoFnRunnerTest.WINDOW_1.maxTimestamp().plus(StatefulDoFnRunnerTest.ALLOWED_LATENESS).plus(GC_DELAY_MS).plus(1), runner);
        Assert.assertTrue(stateInternals.isEmptyForTesting(stateInternals.state(StatefulDoFnRunnerTest.windowNamespace(StatefulDoFnRunnerTest.WINDOW_1), stateTag)));
        Assert.assertEquals(2, ((int) (stateInternals.state(StatefulDoFnRunnerTest.windowNamespace(StatefulDoFnRunnerTest.WINDOW_2), stateTag).read())));
        // advance watermark past end of WINDOW_2 + allowed lateness
        // so the watermark is past the GC horizon, not on it
        StatefulDoFnRunnerTest.advanceInputWatermark(timerInternals, StatefulDoFnRunnerTest.WINDOW_2.maxTimestamp().plus(StatefulDoFnRunnerTest.ALLOWED_LATENESS).plus(GC_DELAY_MS).plus(1), runner);
        Assert.assertTrue(stateInternals.isEmptyForTesting(stateInternals.state(StatefulDoFnRunnerTest.windowNamespace(StatefulDoFnRunnerTest.WINDOW_2), stateTag)));
    }

    private static class MyDoFn extends DoFn<KV<String, Integer>, Integer> {
        public final String stateId = "foo";

        @StateId(stateId)
        public final StateSpec<ValueState<Integer>> intState = StateSpecs.value(VarIntCoder.of());

        @ProcessElement
        public void processElement(ProcessContext c, @StateId(stateId)
        ValueState<Integer> state) {
            Integer currentValue = MoreObjects.firstNonNull(state.read(), 0);
            state.write((currentValue + 1));
        }
    }
}

