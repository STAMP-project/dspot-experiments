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


import DoFnTester.CloningBehavior.DO_NOT_CLONE;
import GlobalWindow.INSTANCE;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import TimerInternals.TimerData;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import org.apache.beam.runners.core.SplittableParDoViaKeyedWorkItems.ProcessFn;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.InstantCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.apache.beam.sdk.transforms.splittabledofn.HasDefaultTracker;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.ValueInSingleWindow;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SplittableParDoViaKeyedWorkItems.ProcessFn}.
 */
@RunWith(JUnit4.class)
public class SplittableParDoProcessFnTest {
    private static final int MAX_OUTPUTS_PER_BUNDLE = 10000;

    private static final Duration MAX_BUNDLE_DURATION = Duration.standardSeconds(5);

    // ----------------- Tests for whether the transform sets boundedness correctly --------------
    private static class SomeRestriction implements Serializable , HasDefaultTracker<SplittableParDoProcessFnTest.SomeRestriction, SplittableParDoProcessFnTest.SomeRestrictionTracker> {
        @Override
        public SplittableParDoProcessFnTest.SomeRestrictionTracker newTracker() {
            return new SplittableParDoProcessFnTest.SomeRestrictionTracker(this);
        }
    }

    private static class SomeRestrictionTracker extends RestrictionTracker<SplittableParDoProcessFnTest.SomeRestriction, Void> {
        private final SplittableParDoProcessFnTest.SomeRestriction someRestriction;

        public SomeRestrictionTracker(SplittableParDoProcessFnTest.SomeRestriction someRestriction) {
            this.someRestriction = someRestriction;
        }

        @Override
        protected boolean tryClaimImpl(Void position) {
            return true;
        }

        @Override
        public SplittableParDoProcessFnTest.SomeRestriction currentRestriction() {
            return someRestriction;
        }

        @Override
        public SplittableParDoProcessFnTest.SomeRestriction checkpoint() {
            return someRestriction;
        }

        @Override
        public void checkDone() {
        }
    }

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    /**
     * A helper for testing {@link ProcessFn} on 1 element (but possibly over multiple {@link DoFn.ProcessElement} calls).
     */
    private static class ProcessFnTester<InputT, OutputT, RestrictionT, PositionT, TrackerT extends RestrictionTracker<RestrictionT, PositionT>> implements AutoCloseable {
        private final DoFnTester<KeyedWorkItem<byte[], KV<InputT, RestrictionT>>, OutputT> tester;

        private Instant currentProcessingTime;

        private InMemoryTimerInternals timerInternals;

        private TestInMemoryStateInternals<String> stateInternals;

        ProcessFnTester(Instant currentProcessingTime, final DoFn<InputT, OutputT> fn, Coder<InputT> inputCoder, Coder<RestrictionT> restrictionCoder, int maxOutputsPerBundle, Duration maxBundleDuration) throws Exception {
            // The exact windowing strategy doesn't matter in this test, but it should be able to
            // encode IntervalWindow's because that's what all tests here use.
            WindowingStrategy<InputT, BoundedWindow> windowingStrategy = ((WindowingStrategy) (WindowingStrategy.of(FixedWindows.of(Duration.standardSeconds(1)))));
            final ProcessFn<InputT, OutputT, RestrictionT, TrackerT> processFn = new ProcessFn(fn, inputCoder, restrictionCoder, windowingStrategy);
            this.tester = DoFnTester.of(processFn);
            this.timerInternals = new InMemoryTimerInternals();
            this.stateInternals = new TestInMemoryStateInternals("dummy");
            processFn.setStateInternalsFactory(( key) -> stateInternals);
            processFn.setTimerInternalsFactory(( key) -> timerInternals);
            processFn.setProcessElementInvoker(new OutputAndTimeBoundedSplittableProcessElementInvoker(fn, tester.getPipelineOptions(), new SplittableParDoProcessFnTest.OutputWindowedValueToDoFnTester(tester), new SideInputReader() {
                @Override
                public <T> T get(PCollectionView<T> view, BoundedWindow window) {
                    throw new NoSuchElementException();
                }

                @Override
                public <T> boolean contains(PCollectionView<T> view) {
                    return false;
                }

                @Override
                public boolean isEmpty() {
                    return true;
                }
            }, Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory()), maxOutputsPerBundle, maxBundleDuration));
            // Do not clone since ProcessFn references non-serializable DoFnTester itself
            // through the state/timer/output callbacks.
            this.tester.setCloningBehavior(DO_NOT_CLONE);
            this.tester.startBundle();
            timerInternals.advanceProcessingTime(currentProcessingTime);
            this.currentProcessingTime = currentProcessingTime;
        }

        @Override
        public void close() throws Exception {
            tester.close();
        }

        /**
         * Performs a seed {@link DoFn.ProcessElement} call feeding the element and restriction.
         */
        void startElement(InputT element, RestrictionT restriction) throws Exception {
            startElement(WindowedValue.of(KV.of(element, restriction), currentProcessingTime, INSTANCE, ON_TIME_AND_ONLY_FIRING));
        }

        void startElement(WindowedValue<KV<InputT, RestrictionT>> windowedValue) throws Exception {
            tester.processElement(KeyedWorkItems.elementsWorkItem("key".getBytes(StandardCharsets.UTF_8), Collections.singletonList(windowedValue)));
        }

        /**
         * Advances processing time by a given duration and, if any timers fired, performs a non-seed
         * {@link DoFn.ProcessElement} call, feeding it the timers.
         */
        boolean advanceProcessingTimeBy(Duration duration) throws Exception {
            currentProcessingTime = currentProcessingTime.plus(duration);
            timerInternals.advanceProcessingTime(currentProcessingTime);
            List<TimerInternals.TimerData> timers = new ArrayList<>();
            TimerInternals.TimerData nextTimer;
            while ((nextTimer = timerInternals.removeNextProcessingTimer()) != null) {
                timers.add(nextTimer);
            } 
            if (timers.isEmpty()) {
                return false;
            }
            tester.processElement(KeyedWorkItems.timersWorkItem("key".getBytes(StandardCharsets.UTF_8), timers));
            return true;
        }

        List<TimestampedValue<OutputT>> peekOutputElementsInWindow(BoundedWindow window) {
            return tester.peekOutputElementsInWindow(window);
        }

        List<OutputT> takeOutputElements() {
            return tester.takeOutputElements();
        }

        public Instant getWatermarkHold() {
            return stateInternals.earliestWatermarkHold();
        }
    }

    private static class OutputWindowedValueToDoFnTester<OutputT> implements OutputWindowedValue<OutputT> {
        private final DoFnTester<?, OutputT> tester;

        private OutputWindowedValueToDoFnTester(DoFnTester<?, OutputT> tester) {
            this.tester = tester;
        }

        @Override
        public void outputWindowedValue(OutputT output, Instant timestamp, Collection<? extends BoundedWindow> windows, PaneInfo pane) {
            outputWindowedValue(tester.getMainOutputTag(), output, timestamp, windows, pane);
        }

        @Override
        public <AdditionalOutputT> void outputWindowedValue(TupleTag<AdditionalOutputT> tag, AdditionalOutputT output, Instant timestamp, Collection<? extends BoundedWindow> windows, PaneInfo pane) {
            for (BoundedWindow window : windows) {
                tester.getMutableOutput(tag).add(ValueInSingleWindow.of(output, timestamp, window, pane));
            }
        }
    }

    /**
     * A simple splittable {@link DoFn} that's actually monolithic.
     */
    private static class ToStringFn extends DoFn<Integer, String> {
        @ProcessElement
        public void process(ProcessContext c, SplittableParDoProcessFnTest.SomeRestrictionTracker tracker) {
            checkState(tryClaim(null));
            c.output(((c.element().toString()) + "a"));
            c.output(((c.element().toString()) + "b"));
            c.output(((c.element().toString()) + "c"));
        }

        @GetInitialRestriction
        public SplittableParDoProcessFnTest.SomeRestriction getInitialRestriction(Integer elem) {
            return new SplittableParDoProcessFnTest.SomeRestriction();
        }
    }

    @Test
    public void testTrivialProcessFnPropagatesOutputWindowAndTimestamp() throws Exception {
        // Tests that ProcessFn correctly propagates the window and timestamp of the element
        // inside the KeyedWorkItem.
        // The underlying DoFn is actually monolithic, so this doesn't test splitting.
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.ToStringFn();
        Instant base = Instant.now();
        IntervalWindow w = new IntervalWindow(base.minus(Duration.standardMinutes(1)), base.plus(Duration.standardMinutes(1)));
        SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, SplittableParDoProcessFnTest.SomeRestriction, Void, SplittableParDoProcessFnTest.SomeRestrictionTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, BigEndianIntegerCoder.of(), SerializableCoder.of(SplittableParDoProcessFnTest.SomeRestriction.class), SplittableParDoProcessFnTest.MAX_OUTPUTS_PER_BUNDLE, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION);
        tester.startElement(WindowedValue.of(KV.of(42, new SplittableParDoProcessFnTest.SomeRestriction()), base, Collections.singletonList(w), ON_TIME_AND_ONLY_FIRING));
        Assert.assertEquals(Arrays.asList(TimestampedValue.of("42a", base), TimestampedValue.of("42b", base), TimestampedValue.of("42c", base)), tester.peekOutputElementsInWindow(w));
    }

    private static class WatermarkUpdateFn extends DoFn<Instant, String> {
        @ProcessElement
        public void process(ProcessContext c, OffsetRangeTracker tracker) {
            for (long i = tracker.currentRestriction().getFrom(); tracker.tryClaim(i); ++i) {
                c.updateWatermark(c.element().plus(Duration.standardSeconds(i)));
                c.output(String.valueOf(i));
            }
        }

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(Instant elem) {
            throw new IllegalStateException("Expected to be supplied explicitly in this test");
        }

        @NewTracker
        public OffsetRangeTracker newTracker(OffsetRange range) {
            return new OffsetRangeTracker(range);
        }
    }

    @Test
    public void testUpdatesWatermark() throws Exception {
        DoFn<Instant, String> fn = new SplittableParDoProcessFnTest.WatermarkUpdateFn();
        Instant base = Instant.now();
        SplittableParDoProcessFnTest.ProcessFnTester<Instant, String, OffsetRange, Long, OffsetRangeTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, InstantCoder.of(), SerializableCoder.of(OffsetRange.class), 3, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION);
        tester.startElement(base, new OffsetRange(0, 8));
        Assert.assertThat(tester.takeOutputElements(), Matchers.hasItems("0", "1", "2"));
        Assert.assertEquals(base.plus(Duration.standardSeconds(2)), tester.getWatermarkHold());
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.hasItems("3", "4", "5"));
        Assert.assertEquals(base.plus(Duration.standardSeconds(5)), tester.getWatermarkHold());
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.hasItems("6", "7"));
        Assert.assertEquals(null, tester.getWatermarkHold());
    }

    /**
     * A simple splittable {@link DoFn} that outputs the given element every 5 seconds forever.
     */
    private static class SelfInitiatedResumeFn extends DoFn<Integer, String> {
        @ProcessElement
        public ProcessContinuation process(ProcessContext c, SplittableParDoProcessFnTest.SomeRestrictionTracker tracker) {
            checkState(tryClaim(null));
            c.output(c.element().toString());
            return resume().withResumeDelay(Duration.standardSeconds(5));
        }

        @GetInitialRestriction
        public SplittableParDoProcessFnTest.SomeRestriction getInitialRestriction(Integer elem) {
            return new SplittableParDoProcessFnTest.SomeRestriction();
        }
    }

    @Test
    public void testResumeSetsTimer() throws Exception {
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.SelfInitiatedResumeFn();
        Instant base = Instant.now();
        SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, SplittableParDoProcessFnTest.SomeRestriction, Void, SplittableParDoProcessFnTest.SomeRestrictionTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, BigEndianIntegerCoder.of(), SerializableCoder.of(SplittableParDoProcessFnTest.SomeRestriction.class), SplittableParDoProcessFnTest.MAX_OUTPUTS_PER_BUNDLE, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION);
        tester.startElement(42, new SplittableParDoProcessFnTest.SomeRestriction());
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("42"));
        // Should resume after 5 seconds: advancing by 3 seconds should have no effect.
        Assert.assertFalse(tester.advanceProcessingTimeBy(Duration.standardSeconds(3)));
        Assert.assertTrue(tester.takeOutputElements().isEmpty());
        // 6 seconds should be enough  should invoke the fn again.
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(3)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("42"));
        // Should again resume after 5 seconds: advancing by 3 seconds should again have no effect.
        Assert.assertFalse(tester.advanceProcessingTimeBy(Duration.standardSeconds(3)));
        Assert.assertTrue(tester.takeOutputElements().isEmpty());
        // 6 seconds should again be enough.
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(3)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("42"));
    }

    /**
     * A splittable {@link DoFn} that generates the sequence [init, init + total).
     */
    private static class CounterFn extends DoFn<Integer, String> {
        private final int numOutputsPerCall;

        public CounterFn(int numOutputsPerCall) {
            this.numOutputsPerCall = numOutputsPerCall;
        }

        @ProcessElement
        public ProcessContinuation process(ProcessContext c, OffsetRangeTracker tracker) {
            for (long i = tracker.currentRestriction().getFrom(), numIterations = 0; tracker.tryClaim(i); ++i , ++numIterations) {
                c.output(String.valueOf(((c.element()) + i)));
                if (numIterations == ((numOutputsPerCall) - 1)) {
                    return resume();
                }
            }
            return stop();
        }

        @GetInitialRestriction
        public OffsetRange getInitialRestriction(Integer elem) {
            throw new UnsupportedOperationException("Expected to be supplied explicitly in this test");
        }
    }

    @Test
    public void testResumeCarriesOverState() throws Exception {
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.CounterFn(1);
        Instant base = Instant.now();
        SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, OffsetRange, Long, OffsetRangeTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, BigEndianIntegerCoder.of(), SerializableCoder.of(OffsetRange.class), SplittableParDoProcessFnTest.MAX_OUTPUTS_PER_BUNDLE, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION);
        tester.startElement(42, new OffsetRange(0, 3));
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("42"));
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("43"));
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        Assert.assertThat(tester.takeOutputElements(), Matchers.contains("44"));
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        // After outputting all 3 items, should not output anything more.
        Assert.assertEquals(0, tester.takeOutputElements().size());
        // Should also not ask to resume.
        Assert.assertFalse(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
    }

    @Test
    public void testCheckpointsAfterNumOutputs() throws Exception {
        int max = 100;
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.CounterFn(Integer.MAX_VALUE);
        Instant base = Instant.now();
        int baseIndex = 42;
        SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, OffsetRange, Long, OffsetRangeTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, BigEndianIntegerCoder.of(), SerializableCoder.of(OffsetRange.class), max, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION);
        List<String> elements;
        // Create an fn that attempts to 2x output more than checkpointing allows.
        tester.startElement(baseIndex, new OffsetRange(0, ((2 * max) + (max / 2))));
        elements = tester.takeOutputElements();
        Assert.assertEquals(max, elements.size());
        // Should output the range [0, max)
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf(baseIndex)));
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf(((baseIndex + max) - 1))));
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        elements = tester.takeOutputElements();
        Assert.assertEquals(max, elements.size());
        // Should output the range [max, 2*max)
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf((baseIndex + max))));
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf(((baseIndex + (2 * max)) - 1))));
        Assert.assertTrue(tester.advanceProcessingTimeBy(Duration.standardSeconds(1)));
        elements = tester.takeOutputElements();
        Assert.assertEquals((max / 2), elements.size());
        // Should output the range [2*max, 2*max + max/2)
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf((baseIndex + (2 * max)))));
        Assert.assertThat(elements, Matchers.hasItem(String.valueOf((((baseIndex + (2 * max)) + (max / 2)) - 1))));
        Assert.assertThat(elements, Matchers.not(Matchers.hasItem(String.valueOf(((baseIndex + (2 * max)) + (max / 2))))));
    }

    @Test
    public void testCheckpointsAfterDuration() throws Exception {
        // Don't bound number of outputs.
        int max = Integer.MAX_VALUE;
        // But bound bundle duration - the bundle should terminate.
        Duration maxBundleDuration = Duration.standardSeconds(1);
        // Create an fn that attempts to 2x output more than checkpointing allows.
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.CounterFn(Integer.MAX_VALUE);
        Instant base = Instant.now();
        int baseIndex = 42;
        SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, OffsetRange, Long, OffsetRangeTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(base, fn, BigEndianIntegerCoder.of(), SerializableCoder.of(OffsetRange.class), max, maxBundleDuration);
        List<String> elements;
        tester.startElement(baseIndex, new OffsetRange(0, Long.MAX_VALUE));
        // Bundle should terminate, and should do at least some processing.
        elements = tester.takeOutputElements();
        Assert.assertFalse(elements.isEmpty());
        // Bundle should have run for at least the requested duration.
        Assert.assertThat(((Instant.now().getMillis()) - (base.getMillis())), Matchers.greaterThanOrEqualTo(maxBundleDuration.getMillis()));
    }

    private static class LifecycleVerifyingFn extends DoFn<Integer, String> {
        private enum State {

            BEFORE_SETUP,
            OUTSIDE_BUNDLE,
            INSIDE_BUNDLE,
            TORN_DOWN;}

        private SplittableParDoProcessFnTest.LifecycleVerifyingFn.State state = SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.BEFORE_SETUP;

        @ProcessElement
        public void process(ProcessContext c, SplittableParDoProcessFnTest.SomeRestrictionTracker tracker) {
            Assert.assertEquals(SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.INSIDE_BUNDLE, state);
        }

        @GetInitialRestriction
        public SplittableParDoProcessFnTest.SomeRestriction getInitialRestriction(Integer element) {
            return new SplittableParDoProcessFnTest.SomeRestriction();
        }

        @Setup
        public void setup() {
            Assert.assertEquals(SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.BEFORE_SETUP, state);
            state = SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.OUTSIDE_BUNDLE;
        }

        @Teardown
        public void tearDown() {
            Assert.assertEquals(SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.OUTSIDE_BUNDLE, state);
            state = SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.TORN_DOWN;
        }

        @StartBundle
        public void startBundle() {
            Assert.assertEquals(SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.OUTSIDE_BUNDLE, state);
            state = SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.INSIDE_BUNDLE;
        }

        @FinishBundle
        public void finishBundle() {
            Assert.assertEquals(SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.INSIDE_BUNDLE, state);
            state = SplittableParDoProcessFnTest.LifecycleVerifyingFn.State.OUTSIDE_BUNDLE;
        }
    }

    @Test
    public void testInvokesLifecycleMethods() throws Exception {
        DoFn<Integer, String> fn = new SplittableParDoProcessFnTest.LifecycleVerifyingFn();
        try (SplittableParDoProcessFnTest.ProcessFnTester<Integer, String, SplittableParDoProcessFnTest.SomeRestriction, Void, SplittableParDoProcessFnTest.SomeRestrictionTracker> tester = new SplittableParDoProcessFnTest.ProcessFnTester(Instant.now(), fn, BigEndianIntegerCoder.of(), SerializableCoder.of(SplittableParDoProcessFnTest.SomeRestriction.class), SplittableParDoProcessFnTest.MAX_OUTPUTS_PER_BUNDLE, SplittableParDoProcessFnTest.MAX_BUNDLE_DURATION)) {
            tester.startElement(42, new SplittableParDoProcessFnTest.SomeRestriction());
        }
    }
}

