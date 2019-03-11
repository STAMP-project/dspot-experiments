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
package org.apache.beam.sdk.transforms;


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import Watch.Growth.BinaryCombined;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Watch.Growth;
import org.apache.beam.sdk.transforms.Watch.Growth.PollResult;
import org.apache.beam.sdk.transforms.Watch.GrowthState;
import org.apache.beam.sdk.transforms.Watch.GrowthTracker;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableDuration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Growth.afterTimeSinceNewOutput;
import static Growth.allOf;
import static Growth.eitherOf;
import static Growth.never;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.apache.beam.sdk.transforms.Watch.Growth.PollFn.<init>;


/**
 * Tests for {@link Watch}.
 */
@RunWith(JUnit4.class)
public class WatchTest implements Serializable {
    @Rule
    public transient TestPipeline p = TestPipeline.create();

    @Test
    @Category(NeedsRunner.class)
    public void testSinglePollMultipleInputs() {
        PCollection<KV<String, String>> res = p.apply(Create.of("a", "b")).apply(Watch.growthOf(new org.apache.beam.sdk.transforms.Watch.Growth.PollFn<String, String>() {
            @Override
            public PollResult<String> apply(String element, Context c) throws Exception {
                return PollResult.complete(Instant.now(), asList((element + ".foo"), (element + ".bar")));
            }
        }).withPollInterval(Duration.ZERO));
        PAssert.that(res).containsInAnyOrder(asList(KV.of("a", "a.foo"), KV.of("a", "a.bar"), KV.of("b", "b.foo"), KV.of("b", "b.bar")));
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSinglePollMultipleInputsWithSideInput() {
        final PCollectionView<String> moo = p.apply("moo", Create.of("moo")).apply("moo singleton", View.asSingleton());
        final PCollectionView<String> zoo = p.apply("zoo", Create.of("zoo")).apply("zoo singleton", View.asSingleton());
        PCollection<KV<String, String>> res = p.apply("input", Create.of("a", "b")).apply(Watch.growthOf(new org.apache.beam.sdk.transforms.Watch.Growth.PollFn<String, String>() {
            @Override
            public PollResult<String> apply(String element, Context c) throws Exception {
                return PollResult.complete(Instant.now(), asList(((((element + " ") + (c.sideInput(moo))) + " ") + (c.sideInput(zoo)))));
            }
        }, Requirements.requiresSideInputs(moo, zoo)).withPollInterval(Duration.ZERO));
        PAssert.that(res).containsInAnyOrder(asList(KV.of("a", "a moo zoo"), KV.of("b", "b moo zoo")));
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testMultiplePollsWithTerminationBecauseOutputIsFinal() {
        testMultiplePolls(false);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testMultiplePollsWithTerminationDueToTerminationCondition() {
        testMultiplePolls(true);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testMultiplePollsWithKeyExtractor() {
        List<KV<Integer, String>> polls = asList(KV.of(0, "0"), KV.of(10, "10"), KV.of(20, "20"), KV.of(30, "30"), KV.of(40, "40"), KV.of(40, "40.1"), KV.of(20, "20.1"), KV.of(50, "50"), KV.of(10, "10.1"), KV.of(10, "10.2"), KV.of(60, "60"), KV.of(70, "70"), KV.of(60, "60.1"), KV.of(80, "80"), KV.of(40, "40.2"), KV.of(90, "90"), KV.of(90, "90.1"));
        List<Integer> expected = asList(0, 10, 20, 30, 40, 50, 60, 70, 80, 90);
        PCollection<Integer> res = p.apply(Create.of("a")).apply(Watch.growthOf(Contextful.of(/* timeToOutputEverything */
        /* timeToDeclareOutputFinal */
        /* timeToFail */
        new WatchTest.TimedPollFn<String, KV<Integer, String>>(polls, standardSeconds(1), standardSeconds(3), standardSeconds(30)), Requirements.empty()), KV::getKey).withTerminationPerInput(org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(5))).withPollInterval(Duration.millis(100)).withOutputCoder(org.apache.beam.sdk.coders.KvCoder.of(org.apache.beam.sdk.coders.VarIntCoder.of(), org.apache.beam.sdk.coders.StringUtf8Coder.of())).withOutputKeyCoder(org.apache.beam.sdk.coders.VarIntCoder.of())).apply("Drop input", Values.create()).apply("Drop auxiliary string", Keys.create());
        PAssert.that(res).containsInAnyOrder(expected);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testMultiplePollsStopAfterTimeSinceNewOutput() {
        List<Integer> all = asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PCollection<Integer> res = p.apply(Create.of("a")).apply(// Should terminate after 4 seconds - earlier than timeToFail
        Watch.growthOf(/* timeToOutputEverything */
        // Never declare output final
        /* timeToDeclareOutputFinal */
        /* timeToFail */
        new WatchTest.TimedPollFn<String, Integer>(all, standardSeconds(1), standardSeconds(1000), standardSeconds(30))).withTerminationPerInput(afterTimeSinceNewOutput(standardSeconds(3))).withPollInterval(Duration.millis(300)).withOutputCoder(org.apache.beam.sdk.coders.VarIntCoder.of())).apply("Drop input", Values.create());
        PAssert.that(res).containsInAnyOrder(all);
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSinglePollWithManyResults() {
        // More than the default 100 elements per checkpoint for direct runner.
        final long numResults = 3000;
        PCollection<KV<String, Integer>> res = p.apply(Create.of("a")).apply(Watch.growthOf(new org.apache.beam.sdk.transforms.Watch.Growth.PollFn<String, KV<String, Integer>>() {
            @Override
            public PollResult<KV<String, Integer>> apply(String element, Context c) throws Exception {
                String pollId = randomUUID().toString();
                List<KV<String, Integer>> output = Lists.newArrayList();
                for (int i = 0; i < numResults; ++i) {
                    output.add(KV.of(pollId, i));
                }
                return PollResult.complete(Instant.now(), output);
            }
        }).withTerminationPerInput(org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(1))).withPollInterval(Duration.millis(1)).withOutputCoder(org.apache.beam.sdk.coders.KvCoder.of(org.apache.beam.sdk.coders.StringUtf8Coder.of(), org.apache.beam.sdk.coders.VarIntCoder.of()))).apply("Drop input", Values.create());
        PAssert.that("Poll called only once", res.apply(Keys.create())).satisfies(( pollIds) -> {
            assertEquals(1, Sets.newHashSet(pollIds).size());
            return null;
        });
        PAssert.that("Yields all expected results", res.apply("Drop poll id", Values.create())).satisfies(( input) -> {
            assertEquals("Total number of results mismatches", numResults, Lists.newArrayList(input).size());
            assertEquals("Results are not unique", numResults, Sets.newHashSet(input).size());
            return null;
        });
        p.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testMultiplePollsWithManyResults() {
        final long numResults = 3000;
        List<Integer> all = Lists.newArrayList();
        for (int i = 0; i < numResults; ++i) {
            all.add(i);
        }
        PCollection<TimestampedValue<Integer>> res = p.apply(Create.of("a")).apply(Watch.growthOf(/* timeToOutputEverything */
        /* timeToDeclareOutputFinal */
        /* timeToFail */
        new WatchTest.TimedPollFn<String, Integer>(all, standardSeconds(1), standardSeconds(3), standardSeconds(30))).withPollInterval(Duration.millis(500)).withOutputCoder(org.apache.beam.sdk.coders.VarIntCoder.of())).apply(Reify.timestampsInValue()).apply("Drop timestamped input", Values.create());
        PAssert.that(res).satisfies(( outputs) -> {
            Function<TimestampedValue<Integer>, Integer> extractValueFn = new Function<TimestampedValue<Integer>, Integer>() {
                @Nullable
                @Override
                public Integer apply(@Nullable
                TimestampedValue<Integer> input) {
                    return input.getValue();
                }
            };
            Function<TimestampedValue<Integer>, Instant> extractTimestampFn = new Function<TimestampedValue<Integer>, Instant>() {
                @Nullable
                @Override
                public Instant apply(@Nullable
                TimestampedValue<Integer> input) {
                    return input.getTimestamp();
                }
            };
            Ordering<TimestampedValue<Integer>> byValue = org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Ordering.natural().onResultOf(extractValueFn);
            Ordering<TimestampedValue<Integer>> byTimestamp = org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Ordering.natural().onResultOf(extractTimestampFn);
            // New outputs appear in timestamp order because each output's assigned timestamp
            // is Instant.now() at the time of poll.
            assertTrue("Outputs must be in timestamp order", byTimestamp.isOrdered(byValue.sortedCopy(outputs)));
            assertEquals("Yields all expected values", numResults, Sets.newHashSet(StreamSupport.stream(outputs.spliterator(), false).map(extractValueFn::apply).collect(Collectors.toList())).size());
            assertThat("Poll called more than once", Sets.newHashSet(StreamSupport.stream(outputs.spliterator(), false).map(extractTimestampFn::apply).collect(Collectors.toList())).size(), greaterThan(1));
            return null;
        });
        p.run();
    }

    /**
     * Gradually emits all items from the given list, pairing each one with a UUID that identifies the
     * round of polling, so a client can check how many rounds of polling there were.
     */
    private static class TimedPollFn<InputT, OutputT> extends org.apache.beam.sdk.transforms.Watch.Growth.PollFn<InputT, OutputT> {
        private final Instant baseTime;

        private final List<OutputT> outputs;

        private final Duration timeToOutputEverything;

        private final Duration timeToDeclareOutputFinal;

        private final Duration timeToFail;

        public TimedPollFn(List<OutputT> outputs, Duration timeToOutputEverything, Duration timeToDeclareOutputFinal, Duration timeToFail) {
            this.baseTime = Instant.now();
            this.outputs = outputs;
            this.timeToOutputEverything = timeToOutputEverything;
            this.timeToDeclareOutputFinal = timeToDeclareOutputFinal;
            this.timeToFail = timeToFail;
        }

        @Override
        public PollResult<OutputT> apply(InputT element, Context c) throws Exception {
            Instant now = Instant.now();
            Duration elapsed = new Duration(baseTime, Instant.now());
            if (elapsed.isLongerThan(timeToFail)) {
                Assert.fail(String.format("Poll called %s after base time, which is longer than the threshold of %s", elapsed, timeToFail));
            }
            double fractionElapsed = (1.0 * (elapsed.getMillis())) / (timeToOutputEverything.getMillis());
            int numToEmit = ((int) (Math.min(outputs.size(), (fractionElapsed * (outputs.size())))));
            List<TimestampedValue<OutputT>> toEmit = Lists.newArrayList();
            for (int i = 0; i < numToEmit; ++i) {
                toEmit.add(TimestampedValue.of(outputs.get(i), now));
            }
            return elapsed.isLongerThan(timeToDeclareOutputFinal) ? PollResult.complete(toEmit) : PollResult.incomplete(toEmit).withWatermark(now);
        }
    }

    @Test
    public void testTerminationConditionsNever() {
        Watch.Growth.Never<Object> c = never();
        Integer state = c.forNewInput(Instant.now(), null);
        Assert.assertFalse(c.canStopPolling(Instant.now(), state));
    }

    @Test
    public void testTerminationConditionsAfterTotalOf() {
        Instant now = Instant.now();
        Watch.Growth.AfterTotalOf<Object> c = org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(5));
        KV<Instant, ReadableDuration> state = c.forNewInput(now, null);
        Assert.assertFalse(c.canStopPolling(now, state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(6)), state));
    }

    @Test
    public void testTerminationConditionsAfterTimeSinceNewOutput() {
        Instant now = Instant.now();
        Watch.Growth.AfterTimeSinceNewOutput<Object> c = afterTimeSinceNewOutput(standardSeconds(5));
        KV<Instant, ReadableDuration> state = c.forNewInput(now, null);
        Assert.assertFalse(c.canStopPolling(now, state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(6)), state));
        state = c.onSeenNewOutput(now.plus(standardSeconds(3)), state);
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(6)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(9)), state));
        state = c.onSeenNewOutput(now.plus(standardSeconds(5)), state);
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(6)), state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(9)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(11)), state));
    }

    @Test
    public void testTerminationConditionsEitherOf() {
        Instant now = Instant.now();
        Watch.Growth.AfterTotalOf<Object> a = org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(5));
        Watch.Growth.AfterTotalOf<Object> b = org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(10));
        BinaryCombined<Object, KV<Instant, ReadableDuration>, KV<Instant, ReadableDuration>> c = eitherOf(a, b);
        KV<KV<Instant, ReadableDuration>, KV<Instant, ReadableDuration>> state = c.forNewInput(now, null);
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(7)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(12)), state));
    }

    @Test
    public void testTerminationConditionsAllOf() {
        Instant now = Instant.now();
        Watch.Growth.AfterTotalOf<Object> a = org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(5));
        Watch.Growth.AfterTotalOf<Object> b = org.apache.beam.sdk.transforms.Watch.Growth.afterTotalOf(standardSeconds(10));
        BinaryCombined<Object, KV<Instant, ReadableDuration>, KV<Instant, ReadableDuration>> c = allOf(a, b);
        KV<KV<Instant, ReadableDuration>, KV<Instant, ReadableDuration>> state = c.forNewInput(now, null);
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(3)), state));
        Assert.assertFalse(c.canStopPolling(now.plus(standardSeconds(7)), state));
        Assert.assertTrue(c.canStopPolling(now.plus(standardSeconds(12)), state));
    }

    @Test
    public void testGrowthTrackerCheckpointNonEmpty() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))).withWatermark(now.plus(standardSeconds(7))));
        Assert.assertEquals(now.plus(standardSeconds(1)), tracker.getWatermark());
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertTrue(tracker.hasPending());
        Assert.assertEquals(now.plus(standardSeconds(3)), tracker.getWatermark());
        GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(tracker.checkpoint());
        GrowthTracker<String, String, Integer> primaryTracker = WatchTest.newTracker(tracker.currentRestriction());
        // Verify primary: should contain what the current tracker claimed, and nothing else.
        Assert.assertEquals(now.plus(standardSeconds(1)), primaryTracker.getWatermark());
        Assert.assertEquals("a", tryClaimNextPending(primaryTracker));
        Assert.assertEquals("b", tryClaimNextPending(primaryTracker));
        Assert.assertFalse(primaryTracker.hasPending());
        Assert.assertFalse(primaryTracker.shouldPollMore());
        // No more pending elements in primary restriction, and no polling.
        primaryTracker.checkDone();
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, primaryTracker.getWatermark());
        // Verify residual: should contain what the current tracker didn't claim.
        Assert.assertEquals(now.plus(standardSeconds(3)), residualTracker.getWatermark());
        Assert.assertEquals("c", tryClaimNextPending(residualTracker));
        Assert.assertEquals("d", tryClaimNextPending(residualTracker));
        Assert.assertFalse(residualTracker.hasPending());
        Assert.assertTrue(residualTracker.shouldPollMore());
        // No more pending elements in residual restriction, but poll watermark still holds.
        Assert.assertEquals(now.plus(standardSeconds(7)), residualTracker.getWatermark());
        // Verify current tracker: it was checkpointed, so should contain nothing else.
        Assert.assertFalse(tracker.hasPending());
        tracker.checkDone();
        Assert.assertFalse(tracker.shouldPollMore());
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, tracker.getWatermark());
    }

    @Test
    public void testGrowthTrackerOutputFullyBeforeCheckpointIncomplete() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))).withWatermark(now.plus(standardSeconds(7))));
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertEquals("c", tryClaimNextPending(tracker));
        Assert.assertEquals("d", tryClaimNextPending(tracker));
        Assert.assertFalse(tracker.hasPending());
        Assert.assertEquals(now.plus(standardSeconds(7)), tracker.getWatermark());
        GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(tracker.checkpoint());
        GrowthTracker<String, String, Integer> primaryTracker = WatchTest.newTracker(tracker.currentRestriction());
        // Verify primary: should contain what the current tracker claimed, and nothing else.
        Assert.assertEquals(now.plus(standardSeconds(1)), primaryTracker.getWatermark());
        Assert.assertEquals("a", tryClaimNextPending(primaryTracker));
        Assert.assertEquals("b", tryClaimNextPending(primaryTracker));
        Assert.assertEquals("c", tryClaimNextPending(primaryTracker));
        Assert.assertEquals("d", tryClaimNextPending(primaryTracker));
        Assert.assertFalse(primaryTracker.hasPending());
        Assert.assertFalse(primaryTracker.shouldPollMore());
        // No more pending elements in primary restriction, and no polling.
        primaryTracker.checkDone();
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, primaryTracker.getWatermark());
        // Verify residual: should contain what the current tracker didn't claim.
        Assert.assertFalse(residualTracker.hasPending());
        Assert.assertTrue(residualTracker.shouldPollMore());
        // No more pending elements in residual restriction, but poll watermark still holds.
        Assert.assertEquals(now.plus(standardSeconds(7)), residualTracker.getWatermark());
        // Verify current tracker: it was checkpointed, so should contain nothing else.
        tracker.checkDone();
        Assert.assertFalse(tracker.hasPending());
        Assert.assertFalse(tracker.shouldPollMore());
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, tracker.getWatermark());
    }

    @Test
    public void testGrowthTrackerPollAfterCheckpointIncompleteWithNewOutputs() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))).withWatermark(now.plus(standardSeconds(7))));
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertEquals("c", tryClaimNextPending(tracker));
        Assert.assertEquals("d", tryClaimNextPending(tracker));
        GrowthState<String, String, Integer> checkpoint = tracker.checkpoint();
        // Simulate resuming from the checkpoint and adding more elements.
        {
            GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(checkpoint);
            residualTracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("e", now.plus(standardSeconds(5))), TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))), TimestampedValue.of("f", now.plus(standardSeconds(8))))).withWatermark(now.plus(standardSeconds(12))));
            Assert.assertEquals(now.plus(standardSeconds(5)), residualTracker.getWatermark());
            Assert.assertEquals("e", tryClaimNextPending(residualTracker));
            Assert.assertEquals(now.plus(standardSeconds(8)), residualTracker.getWatermark());
            Assert.assertEquals("f", tryClaimNextPending(residualTracker));
            Assert.assertFalse(residualTracker.hasPending());
            Assert.assertTrue(residualTracker.shouldPollMore());
            Assert.assertEquals(now.plus(standardSeconds(12)), residualTracker.getWatermark());
        }
        // Try same without an explicitly specified watermark.
        {
            GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(checkpoint);
            residualTracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("e", now.plus(standardSeconds(5))), TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))), TimestampedValue.of("f", now.plus(standardSeconds(8))))));
            Assert.assertEquals(now.plus(standardSeconds(5)), residualTracker.getWatermark());
            Assert.assertEquals("e", tryClaimNextPending(residualTracker));
            Assert.assertEquals(now.plus(standardSeconds(5)), residualTracker.getWatermark());
            Assert.assertEquals("f", tryClaimNextPending(residualTracker));
            Assert.assertFalse(residualTracker.hasPending());
            Assert.assertTrue(residualTracker.shouldPollMore());
            Assert.assertEquals(now.plus(standardSeconds(5)), residualTracker.getWatermark());
        }
    }

    @Test
    public void testGrowthTrackerPollAfterCheckpointWithoutNewOutputs() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))).withWatermark(now.plus(standardSeconds(7))));
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertEquals("c", tryClaimNextPending(tracker));
        Assert.assertEquals("d", tryClaimNextPending(tracker));
        // Simulate resuming from the checkpoint but there are no new elements.
        GrowthState<String, String, Integer> checkpoint = tracker.checkpoint();
        {
            GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(checkpoint);
            residualTracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))).withWatermark(now.plus(standardSeconds(12))));
            Assert.assertFalse(residualTracker.hasPending());
            Assert.assertTrue(residualTracker.shouldPollMore());
            Assert.assertEquals(now.plus(standardSeconds(12)), residualTracker.getWatermark());
        }
        // Try the same without an explicitly specified watermark
        {
            GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(checkpoint);
            residualTracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))));
            // No new elements and no explicit watermark supplied - should reuse old watermark.
            Assert.assertEquals(now.plus(standardSeconds(7)), residualTracker.getWatermark());
        }
    }

    @Test
    public void testGrowthTrackerPollAfterCheckpointWithoutNewOutputsNoWatermark() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))));
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertEquals("c", tryClaimNextPending(tracker));
        Assert.assertEquals("d", tryClaimNextPending(tracker));
        Assert.assertEquals(now.plus(standardSeconds(1)), tracker.getWatermark());
        // Simulate resuming from the checkpoint but there are no new elements.
        GrowthState<String, String, Integer> checkpoint = tracker.checkpoint();
        GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(checkpoint);
        residualTracker.addNewAsPending(PollResult.incomplete(asList(TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))));
        // No new elements and no explicit watermark supplied - should keep old watermark.
        Assert.assertEquals(now.plus(standardSeconds(1)), residualTracker.getWatermark());
    }

    @Test
    public void testGrowthTrackerRepeatedEmptyPollWatermark() {
        // Empty poll result with no watermark
        {
            GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
            tracker.addNewAsPending(PollResult.incomplete(Collections.emptyList()));
            Assert.assertEquals(TIMESTAMP_MIN_VALUE, tracker.getWatermark());
        }
        // Empty poll result with watermark
        {
            Instant now = Instant.now();
            GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
            tracker.addNewAsPending(PollResult.incomplete(Collections.<TimestampedValue<String>>emptyList()).withWatermark(now));
            Assert.assertEquals(now, tracker.getWatermark());
        }
    }

    @Test
    public void testGrowthTrackerOutputFullyBeforeCheckpointComplete() {
        Instant now = Instant.now();
        GrowthTracker<String, String, Integer> tracker = WatchTest.newTracker();
        tracker.addNewAsPending(PollResult.complete(asList(TimestampedValue.of("d", now.plus(standardSeconds(4))), TimestampedValue.of("c", now.plus(standardSeconds(3))), TimestampedValue.of("a", now.plus(standardSeconds(1))), TimestampedValue.of("b", now.plus(standardSeconds(2))))));
        Assert.assertEquals("a", tryClaimNextPending(tracker));
        Assert.assertEquals("b", tryClaimNextPending(tracker));
        Assert.assertEquals("c", tryClaimNextPending(tracker));
        Assert.assertEquals("d", tryClaimNextPending(tracker));
        Assert.assertFalse(tracker.hasPending());
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, tracker.getWatermark());
        GrowthTracker<String, String, Integer> residualTracker = WatchTest.newTracker(tracker.checkpoint());
        // Verify residual: should be empty, since output was final.
        residualTracker.checkDone();
        Assert.assertFalse(residualTracker.hasPending());
        Assert.assertFalse(residualTracker.shouldPollMore());
        // No more pending elements in residual restriction, but poll watermark still holds.
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, residualTracker.getWatermark());
        // Verify current tracker: it was checkpointed, so should contain nothing else.
        tracker.checkDone();
        Assert.assertFalse(tracker.hasPending());
        Assert.assertFalse(tracker.shouldPollMore());
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, tracker.getWatermark());
    }
}

