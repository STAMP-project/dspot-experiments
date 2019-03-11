/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals.suppress;


import MockProcessorContext.CapturedForward;
import java.time.Duration;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.kstream.internals.Change;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.processor.MockProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.internals.ProcessorNode;
import org.apache.kafka.streams.state.internals.InMemoryTimeOrderedKeyValueBuffer;
import org.apache.kafka.test.MockInternalProcessorContext;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static BufferConfig.maxBytes;
import static BufferConfig.maxRecords;
import static BufferConfig.unbounded;


@SuppressWarnings("PointlessArithmeticExpression")
public class KTableSuppressProcessorTest {
    private static final long ARBITRARY_LONG = 5L;

    private static final Change<Long> ARBITRARY_CHANGE = new Change(7L, 14L);

    private static class Harness<K, V> {
        private final KTableSuppressProcessor<K, V> processor;

        private final MockInternalProcessorContext context;

        Harness(final Suppressed<K> suppressed, final Serde<K> keySerde, final Serde<V> valueSerde) {
            final String storeName = "test-store";
            final StateStore buffer = new InMemoryTimeOrderedKeyValueBuffer.Builder(storeName).withLoggingDisabled().build();
            final KTableSuppressProcessor<K, V> processor = new KTableSuppressProcessor(((SuppressedInternal<K>) (suppressed)), storeName, keySerde, new org.apache.kafka.streams.kstream.internals.FullChangeSerde(valueSerde));
            final MockInternalProcessorContext context = new MockInternalProcessorContext();
            context.setCurrentNode(new ProcessorNode("testNode"));
            buffer.init(context, buffer);
            processor.init(context);
            this.processor = processor;
            this.context = context;
        }
    }

    @Test
    public void zeroTimeLimitShouldImmediatelyEmit() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ZERO, unbounded()), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = KTableSuppressProcessorTest.ARBITRARY_LONG;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final String key = "hey";
        final Change<Long> value = KTableSuppressProcessorTest.ARBITRARY_CHANGE;
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void windowedZeroTimeLimitShouldImmediatelyEmit() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ZERO, unbounded()), timeWindowedSerdeFrom(String.class, 100L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = KTableSuppressProcessorTest.ARBITRARY_LONG;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(0L, 100L));
        final Change<Long> value = KTableSuppressProcessorTest.ARBITRARY_CHANGE;
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void intermediateSuppressionShouldBufferAndEmitLater() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofMillis(1), unbounded()), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 0L;
        context.setRecordMetadata("topic", 0, 0, null, timestamp);
        final String key = "hey";
        final Change<Long> value = new Change(null, 1L);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
        context.setRecordMetadata("topic", 0, 1, null, 1L);
        processor.process("tick", new Change(null, null));
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void finalResultsSuppressionShouldBufferAndEmitAtGraceExpiration() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(finalResults(Duration.ofMillis(1L)), timeWindowedSerdeFrom(String.class, 1L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long windowStart = 99L;
        final long recordTime = 99L;
        final long windowEnd = 100L;
        context.setRecordMetadata("topic", 0, 0, null, recordTime);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(windowStart, windowEnd));
        final Change<Long> value = KTableSuppressProcessorTest.ARBITRARY_CHANGE;
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
        // although the stream time is now 100, we have to wait 1 ms after the window *end* before we
        // emit "hey", so we don't emit yet.
        final long windowStart2 = 100L;
        final long recordTime2 = 100L;
        final long windowEnd2 = 101L;
        context.setRecordMetadata("topic", 0, 1, null, recordTime2);
        processor.process(new Windowed("dummyKey1", new TimeWindow(windowStart2, windowEnd2)), KTableSuppressProcessorTest.ARBITRARY_CHANGE);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
        // ok, now it's time to emit "hey"
        final long windowStart3 = 101L;
        final long recordTime3 = 101L;
        final long windowEnd3 = 102L;
        context.setRecordMetadata("topic", 0, 1, null, recordTime3);
        processor.process(new Windowed("dummyKey2", new TimeWindow(windowStart3, windowEnd3)), KTableSuppressProcessorTest.ARBITRARY_CHANGE);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(recordTime));
    }

    /**
     * Testing a special case of final results: that even with a grace period of 0,
     * it will still buffer events and emit only after the end of the window.
     * As opposed to emitting immediately the way regular suppression would with a time limit of 0.
     */
    @Test
    public void finalResultsWithZeroGraceShouldStillBufferUntilTheWindowEnd() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(finalResults(Duration.ofMillis(0L)), timeWindowedSerdeFrom(String.class, 100L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        // note the record is in the past, but the window end is in the future, so we still have to buffer,
        // even though the grace period is 0.
        final long timestamp = 5L;
        final long streamTime = 99L;
        final long windowEnd = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(0, windowEnd));
        final Change<Long> value = KTableSuppressProcessorTest.ARBITRARY_CHANGE;
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
        setRecordMetadata("", 0, 1L, null, windowEnd);
        processor.process(new Windowed("dummyKey", new TimeWindow(windowEnd, (windowEnd + 100L))), KTableSuppressProcessorTest.ARBITRARY_CHANGE);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void finalResultsWithZeroGraceAtWindowEndShouldImmediatelyEmit() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(finalResults(Duration.ofMillis(0L)), timeWindowedSerdeFrom(String.class, 100L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(0, 100L));
        final Change<Long> value = KTableSuppressProcessorTest.ARBITRARY_CHANGE;
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    /**
     * It's desirable to drop tombstones for final-results windowed streams, since (as described in the
     * {@link SuppressedInternal} javadoc), they are unnecessary to emit.
     */
    @Test
    public void finalResultsShouldDropTombstonesForTimeWindows() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(finalResults(Duration.ofMillis(0L)), timeWindowedSerdeFrom(String.class, 100L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(0, 100L));
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
    }

    /**
     * It's desirable to drop tombstones for final-results windowed streams, since (as described in the
     * {@link SuppressedInternal} javadoc), they are unnecessary to emit.
     */
    @Test
    public void finalResultsShouldDropTombstonesForSessionWindows() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(finalResults(Duration.ofMillis(0L)), WindowedSerdes.sessionWindowedSerdeFrom(String.class), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new SessionWindow(0L, 0L));
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(0));
    }

    /**
     * It's NOT OK to drop tombstones for non-final-results windowed streams, since we may have emitted some results for
     * the window before getting the tombstone (see the {@link SuppressedInternal} javadoc).
     */
    @Test
    public void suppressShouldNotDropTombstonesForTimeWindows() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofMillis(0), maxRecords(0)), timeWindowedSerdeFrom(String.class, 100L), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new TimeWindow(0L, 100L));
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    /**
     * It's NOT OK to drop tombstones for non-final-results windowed streams, since we may have emitted some results for
     * the window before getting the tombstone (see the {@link SuppressedInternal} javadoc).
     */
    @Test
    public void suppressShouldNotDropTombstonesForSessionWindows() {
        final KTableSuppressProcessorTest.Harness<Windowed<String>, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofMillis(0), maxRecords(0)), WindowedSerdes.sessionWindowedSerdeFrom(String.class), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<Windowed<String>, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final Windowed<String> key = new Windowed("hey", new SessionWindow(0L, 0L));
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    /**
     * It's SUPER NOT OK to drop tombstones for non-windowed streams, since we may have emitted some results for
     * the key before getting the tombstone (see the {@link SuppressedInternal} javadoc).
     */
    @Test
    public void suppressShouldNotDropTombstonesForKTable() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofMillis(0), maxRecords(0)), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final String key = "hey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void suppressShouldEmitWhenOverRecordCapacity() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofDays(100), maxRecords(1)), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final String key = "hey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        setRecordMetadata("", 0, 1L, null, (timestamp + 1));
        processor.process("dummyKey", value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void suppressShouldEmitWhenOverByteCapacity() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofDays(100), maxBytes(60L)), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final String key = "hey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        setRecordMetadata("", 0, 1L, null, (timestamp + 1));
        processor.process("dummyKey", value);
        MatcherAssert.assertThat(forwarded(), KTableSuppressProcessorTest.hasSize(1));
        final MockProcessorContext.CapturedForward capturedForward = forwarded().get(0);
        MatcherAssert.assertThat(capturedForward.keyValue(), CoreMatchers.is(new org.apache.kafka.streams.KeyValue(key, value)));
        MatcherAssert.assertThat(capturedForward.timestamp(), CoreMatchers.is(timestamp));
    }

    @Test
    public void suppressShouldShutDownWhenOverRecordCapacity() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofDays(100), maxRecords(1).shutDownWhenFull()), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        context.setCurrentNode(new ProcessorNode("testNode"));
        final String key = "hey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        setRecordMetadata("", 0, 1L, null, timestamp);
        try {
            processor.process("dummyKey", value);
            Assert.fail("expected an exception");
        } catch (final StreamsException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("buffer exceeded its max capacity"));
        }
    }

    @Test
    public void suppressShouldShutDownWhenOverByteCapacity() {
        final KTableSuppressProcessorTest.Harness<String, Long> harness = new KTableSuppressProcessorTest.Harness(Suppressed.untilTimeLimit(Duration.ofDays(100), maxBytes(60L).shutDownWhenFull()), Serdes.String(), Serdes.Long());
        final MockInternalProcessorContext context = harness.context;
        final KTableSuppressProcessor<String, Long> processor = harness.processor;
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        context.setCurrentNode(new ProcessorNode("testNode"));
        final String key = "hey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorTest.ARBITRARY_LONG);
        processor.process(key, value);
        setRecordMetadata("", 0, 1L, null, timestamp);
        try {
            processor.process("dummyKey", value);
            Assert.fail("expected an exception");
        } catch (final StreamsException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("buffer exceeded its max capacity"));
        }
    }
}

