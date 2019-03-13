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
package org.apache.beam.runners.direct;


import GlobalWindow.INSTANCE;
import Timing.LATE;
import Timing.ON_TIME;
import java.util.Collection;
import java.util.Collections;
import org.apache.beam.runners.core.WindowMatchers;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IncompatibleWindowException;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.NonMergingWindowFn;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.transforms.windowing.WindowMappingFn;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link WindowEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class WindowEvaluatorFactoryTest {
    private static final Instant EPOCH = new Instant(0);

    private PCollection<Long> input;

    private WindowEvaluatorFactory factory;

    @Mock
    private EvaluationContext evaluationContext;

    private BundleFactory bundleFactory;

    private WindowedValue<Long> valueInGlobalWindow = WindowedValue.timestampedValueInGlobalWindow(3L, new Instant(2L));

    private final PaneInfo intervalWindowPane = PaneInfo.createPane(false, false, LATE, 3, 2);

    private WindowedValue<Long> valueInIntervalWindow = WindowedValue.of(2L, new Instant((-10L)), new IntervalWindow(new Instant((-100)), WindowEvaluatorFactoryTest.EPOCH), intervalWindowPane);

    private IntervalWindow intervalWindow1 = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH, BoundedWindow.TIMESTAMP_MAX_VALUE);

    private IntervalWindow intervalWindow2 = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(3)), WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(6)));

    private final PaneInfo multiWindowPane = PaneInfo.createPane(false, true, ON_TIME, 3, 0);

    private WindowedValue<Long> valueInGlobalAndTwoIntervalWindows = WindowedValue.of(1L, WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(3)), ImmutableList.of(INSTANCE, intervalWindow1, intervalWindow2), multiWindowPane);

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void singleWindowFnSucceeds() throws Exception {
        Duration windowDuration = Duration.standardDays(7);
        Window<Long> transform = Window.into(FixedWindows.of(windowDuration));
        PCollection<Long> windowed = input.apply(transform);
        CommittedBundle<Long> inputBundle = createInputBundle();
        UncommittedBundle<Long> outputBundle = createOutputBundle(windowed, inputBundle);
        BoundedWindow firstSecondWindow = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH, WindowEvaluatorFactoryTest.EPOCH.plus(windowDuration));
        BoundedWindow thirdWindow = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH.minus(windowDuration), WindowEvaluatorFactoryTest.EPOCH);
        TransformResult<Long> result = runEvaluator(windowed, inputBundle);
        Assert.assertThat(Iterables.getOnlyElement(result.getOutputBundles()), Matchers.equalTo(outputBundle));
        CommittedBundle<Long> committed = outputBundle.commit(Instant.now());
        Assert.assertThat(committed.getElements(), // value in global window
        // value in just interval window
        // value in global window and two interval windows
        Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(3L, new Instant(2L), firstSecondWindow, NO_FIRING), WindowMatchers.isSingleWindowedValue(2L, new Instant((-10L)), thirdWindow, intervalWindowPane), WindowMatchers.isSingleWindowedValue(1L, WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(3)), firstSecondWindow, multiWindowPane), WindowMatchers.isSingleWindowedValue(1L, WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(3)), firstSecondWindow, multiWindowPane), WindowMatchers.isSingleWindowedValue(1L, WindowEvaluatorFactoryTest.EPOCH.plus(Duration.standardDays(3)), firstSecondWindow, multiWindowPane)));
    }

    @Test
    public void multipleWindowsWindowFnSucceeds() throws Exception {
        Duration windowDuration = Duration.standardDays(6);
        Duration slidingBy = Duration.standardDays(3);
        Window<Long> transform = Window.into(SlidingWindows.of(windowDuration).every(slidingBy));
        PCollection<Long> windowed = input.apply(transform);
        CommittedBundle<Long> inputBundle = createInputBundle();
        UncommittedBundle<Long> outputBundle = createOutputBundle(windowed, inputBundle);
        TransformResult<Long> result = runEvaluator(windowed, inputBundle);
        Assert.assertThat(Iterables.getOnlyElement(result.getOutputBundles()), Matchers.equalTo(outputBundle));
        CommittedBundle<Long> committed = outputBundle.commit(Instant.now());
        BoundedWindow w1 = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH, WindowEvaluatorFactoryTest.EPOCH.plus(windowDuration));
        BoundedWindow w2 = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH.plus(slidingBy), WindowEvaluatorFactoryTest.EPOCH.plus(slidingBy).plus(windowDuration));
        BoundedWindow wMinus1 = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH.minus(windowDuration), WindowEvaluatorFactoryTest.EPOCH);
        BoundedWindow wMinusSlide = new IntervalWindow(WindowEvaluatorFactoryTest.EPOCH.minus(windowDuration).plus(slidingBy), WindowEvaluatorFactoryTest.EPOCH.plus(slidingBy));
        Assert.assertThat(committed.getElements(), // Value in global window mapped to one windowed value in multiple windows
        // Value in interval window mapped to one windowed value in multiple windows
        // Value in three windows mapped to three windowed values in the same multiple windows
        Matchers.containsInAnyOrder(WindowMatchers.isWindowedValue(valueInGlobalWindow.getValue(), valueInGlobalWindow.getTimestamp(), ImmutableSet.of(w1, wMinusSlide), NO_FIRING), WindowMatchers.isWindowedValue(valueInIntervalWindow.getValue(), valueInIntervalWindow.getTimestamp(), ImmutableSet.of(wMinus1, wMinusSlide), valueInIntervalWindow.getPane()), WindowMatchers.isWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), ImmutableSet.of(w1, w2), valueInGlobalAndTwoIntervalWindows.getPane()), WindowMatchers.isWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), ImmutableSet.of(w1, w2), valueInGlobalAndTwoIntervalWindows.getPane()), WindowMatchers.isWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), ImmutableSet.of(w1, w2), valueInGlobalAndTwoIntervalWindows.getPane())));
    }

    @Test
    public void referencesEarlierWindowsSucceeds() throws Exception {
        Window<Long> transform = Window.into(new WindowEvaluatorFactoryTest.EvaluatorTestWindowFn());
        PCollection<Long> windowed = input.apply(transform);
        CommittedBundle<Long> inputBundle = createInputBundle();
        UncommittedBundle<Long> outputBundle = createOutputBundle(windowed, inputBundle);
        TransformResult<Long> result = runEvaluator(windowed, inputBundle);
        Assert.assertThat(Iterables.getOnlyElement(result.getOutputBundles()), Matchers.equalTo(outputBundle));
        CommittedBundle<Long> committed = outputBundle.commit(Instant.now());
        Assert.assertThat(committed.getElements(), // Value in global window mapped to [timestamp, timestamp+1)
        // Value in interval window mapped to the same window
        // Value in global window and two interval windows exploded and mapped in both ways
        Matchers.containsInAnyOrder(WindowMatchers.isSingleWindowedValue(valueInGlobalWindow.getValue(), valueInGlobalWindow.getTimestamp(), new IntervalWindow(valueInGlobalWindow.getTimestamp(), valueInGlobalWindow.getTimestamp().plus(1L)), valueInGlobalWindow.getPane()), WindowMatchers.isWindowedValue(valueInIntervalWindow.getValue(), valueInIntervalWindow.getTimestamp(), valueInIntervalWindow.getWindows(), valueInIntervalWindow.getPane()), WindowMatchers.isSingleWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), new IntervalWindow(valueInGlobalAndTwoIntervalWindows.getTimestamp(), valueInGlobalAndTwoIntervalWindows.getTimestamp().plus(1L)), valueInGlobalAndTwoIntervalWindows.getPane()), WindowMatchers.isSingleWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), intervalWindow1, valueInGlobalAndTwoIntervalWindows.getPane()), WindowMatchers.isSingleWindowedValue(valueInGlobalAndTwoIntervalWindows.getValue(), valueInGlobalAndTwoIntervalWindows.getTimestamp(), intervalWindow2, valueInGlobalAndTwoIntervalWindows.getPane())));
    }

    private static class EvaluatorTestWindowFn extends NonMergingWindowFn<Long, BoundedWindow> {
        @Override
        public Collection<BoundedWindow> assignWindows(AssignContext c) throws Exception {
            if (c.window().equals(INSTANCE)) {
                return Collections.singleton(new IntervalWindow(c.timestamp(), c.timestamp().plus(1L)));
            }
            return Collections.singleton(c.window());
        }

        @Override
        public boolean isCompatible(WindowFn<?, ?> other) {
            return false;
        }

        @Override
        public void verifyCompatibility(WindowFn<?, ?> other) throws IncompatibleWindowException {
            throw new IncompatibleWindowException(other, String.format("%s is not compatible with any other %s.", WindowEvaluatorFactoryTest.EvaluatorTestWindowFn.class.getSimpleName(), WindowFn.class.getSimpleName()));
        }

        @Override
        public Coder<BoundedWindow> windowCoder() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Coder coder = ((Coder) (Coder.INSTANCE));
            return coder;
        }

        @Override
        public WindowMappingFn<BoundedWindow> getDefaultWindowMappingFn() {
            throw new UnsupportedOperationException("Cannot be used as a side input");
        }
    }
}

