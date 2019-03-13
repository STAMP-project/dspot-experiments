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


import GlobalWindow.INSTANCE;
import PaneInfo.NO_FIRING;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import TimeDomain.EVENT_TIME;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.sdk.state.TimeDomain;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link SimplePushbackSideInputDoFnRunner}.
 */
@RunWith(JUnit4.class)
public class SimplePushbackSideInputDoFnRunnerTest {
    @Mock
    private ReadyCheckingSideInputReader reader;

    private SimplePushbackSideInputDoFnRunnerTest.TestDoFnRunner<Integer, Integer> underlying;

    private PCollectionView<Integer> singletonView;

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void startFinishBundleDelegates() {
        PushbackSideInputDoFnRunner runner = createRunner(ImmutableList.of(singletonView));
        Assert.assertThat(underlying.started, Matchers.is(true));
        Assert.assertThat(underlying.finished, Matchers.is(false));
        runner.finishBundle();
        Assert.assertThat(underlying.finished, Matchers.is(true));
    }

    @Test
    public void processElementSideInputNotReady() {
        Mockito.when(reader.isReady(Mockito.eq(singletonView), Mockito.any(BoundedWindow.class))).thenReturn(false);
        SimplePushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(ImmutableList.of(singletonView));
        WindowedValue<Integer> oneWindow = WindowedValue.of(2, new Instant((-2)), new IntervalWindow(new Instant((-500L)), new Instant(0L)), ON_TIME_AND_ONLY_FIRING);
        Iterable<WindowedValue<Integer>> oneWindowPushback = runner.processElementInReadyWindows(oneWindow);
        Assert.assertThat(oneWindowPushback, Matchers.containsInAnyOrder(oneWindow));
        Assert.assertThat(underlying.inputElems, Matchers.emptyIterable());
    }

    @Test
    public void processElementSideInputNotReadyMultipleWindows() {
        Mockito.when(reader.isReady(Mockito.eq(singletonView), Mockito.any(BoundedWindow.class))).thenReturn(false);
        SimplePushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(ImmutableList.of(singletonView));
        WindowedValue<Integer> multiWindow = WindowedValue.of(2, new Instant((-2)), ImmutableList.of(new IntervalWindow(new Instant((-500L)), new Instant(0L)), new IntervalWindow(BoundedWindow.TIMESTAMP_MIN_VALUE, new Instant(250L)), INSTANCE), ON_TIME_AND_ONLY_FIRING);
        Iterable<WindowedValue<Integer>> multiWindowPushback = runner.processElementInReadyWindows(multiWindow);
        Assert.assertThat(multiWindowPushback, Matchers.equalTo(multiWindow.explodeWindows()));
        Assert.assertThat(underlying.inputElems, Matchers.emptyIterable());
    }

    @Test
    public void processElementSideInputNotReadySomeWindows() {
        Mockito.when(reader.isReady(Mockito.eq(singletonView), Mockito.eq(INSTANCE))).thenReturn(false);
        Mockito.when(reader.isReady(Mockito.eq(singletonView), AdditionalMatchers.not(Mockito.eq(INSTANCE)))).thenReturn(true);
        SimplePushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(ImmutableList.of(singletonView));
        IntervalWindow littleWindow = new IntervalWindow(new Instant((-500L)), new Instant(0L));
        IntervalWindow bigWindow = new IntervalWindow(BoundedWindow.TIMESTAMP_MIN_VALUE, new Instant(250L));
        WindowedValue<Integer> multiWindow = WindowedValue.of(2, new Instant((-2)), ImmutableList.of(littleWindow, bigWindow, INSTANCE), NO_FIRING);
        Iterable<WindowedValue<Integer>> multiWindowPushback = runner.processElementInReadyWindows(multiWindow);
        Assert.assertThat(multiWindowPushback, Matchers.containsInAnyOrder(WindowedValue.timestampedValueInGlobalWindow(2, new Instant((-2L)))));
        Assert.assertThat(underlying.inputElems, Matchers.containsInAnyOrder(WindowedValue.of(2, new Instant((-2)), ImmutableList.of(littleWindow), NO_FIRING), WindowedValue.of(2, new Instant((-2)), ImmutableList.of(bigWindow), NO_FIRING)));
    }

    @Test
    public void processElementSideInputReadyAllWindows() {
        Mockito.when(reader.isReady(Mockito.eq(singletonView), Mockito.any(BoundedWindow.class))).thenReturn(true);
        ImmutableList<PCollectionView<?>> views = ImmutableList.of(singletonView);
        SimplePushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(views);
        WindowedValue<Integer> multiWindow = WindowedValue.of(2, new Instant((-2)), ImmutableList.of(new IntervalWindow(new Instant((-500L)), new Instant(0L)), new IntervalWindow(BoundedWindow.TIMESTAMP_MIN_VALUE, new Instant(250L)), INSTANCE), ON_TIME_AND_ONLY_FIRING);
        Iterable<WindowedValue<Integer>> multiWindowPushback = runner.processElementInReadyWindows(multiWindow);
        Assert.assertThat(multiWindowPushback, Matchers.emptyIterable());
        Assert.assertThat(underlying.inputElems, Matchers.containsInAnyOrder(ImmutableList.copyOf(multiWindow.explodeWindows()).toArray()));
    }

    @Test
    public void processElementNoSideInputs() {
        SimplePushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(ImmutableList.of());
        WindowedValue<Integer> multiWindow = WindowedValue.of(2, new Instant((-2)), ImmutableList.of(new IntervalWindow(new Instant((-500L)), new Instant(0L)), new IntervalWindow(BoundedWindow.TIMESTAMP_MIN_VALUE, new Instant(250L)), INSTANCE), ON_TIME_AND_ONLY_FIRING);
        Iterable<WindowedValue<Integer>> multiWindowPushback = runner.processElementInReadyWindows(multiWindow);
        Assert.assertThat(multiWindowPushback, Matchers.emptyIterable());
        // Should preserve the compressed representation when there's no side inputs.
        Assert.assertThat(underlying.inputElems, Matchers.containsInAnyOrder(multiWindow));
    }

    /**
     * Tests that a call to onTimer gets delegated.
     */
    @Test
    public void testOnTimerCalled() {
        PushbackSideInputDoFnRunner<Integer, Integer> runner = createRunner(ImmutableList.of());
        String timerId = "fooTimer";
        IntervalWindow window = new IntervalWindow(new Instant(4), new Instant(16));
        Instant timestamp = new Instant(72);
        // Mocking is not easily compatible with annotation analysis, so we manually record
        // the method call.
        runner.onTimer(timerId, window, new Instant(timestamp), EVENT_TIME);
        Assert.assertThat(underlying.firedTimers, Matchers.contains(TimerData.of(timerId, StateNamespaces.window(IntervalWindow.getCoder(), window), timestamp, EVENT_TIME)));
    }

    private static class TestDoFnRunner<InputT, OutputT> implements DoFnRunner<InputT, OutputT> {
        List<WindowedValue<InputT>> inputElems;

        List<TimerData> firedTimers;

        private boolean started = false;

        private boolean finished = false;

        @Override
        public DoFn<InputT, OutputT> getFn() {
            return null;
        }

        @Override
        public void startBundle() {
            started = true;
            inputElems = new ArrayList();
            firedTimers = new ArrayList();
        }

        @Override
        public void processElement(WindowedValue<InputT> elem) {
            inputElems.add(elem);
        }

        @Override
        public void onTimer(String timerId, BoundedWindow window, Instant timestamp, TimeDomain timeDomain) {
            firedTimers.add(TimerData.of(timerId, StateNamespaces.window(IntervalWindow.getCoder(), ((IntervalWindow) (window))), timestamp, timeDomain));
        }

        @Override
        public void finishBundle() {
            finished = true;
        }
    }
}

