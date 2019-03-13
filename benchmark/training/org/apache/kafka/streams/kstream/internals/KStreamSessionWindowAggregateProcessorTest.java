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
package org.apache.kafka.streams.kstream.internals;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Merger;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.internals.ProcessorRecordContext;
import org.apache.kafka.streams.processor.internals.testutil.LogCaptureAppender;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class KStreamSessionWindowAggregateProcessorTest {
    private static final long GAP_MS = (5 * 60) * 1000L;

    private static final String STORE_NAME = "session-store";

    private final Initializer<Long> initializer = () -> 0L;

    private final Aggregator<String, String, Long> aggregator = ( aggKey, value, aggregate) -> aggregate + 1;

    private final Merger<String, Long> sessionMerger = ( aggKey, aggOne, aggTwo) -> aggOne + aggTwo;

    private final KStreamSessionWindowAggregate<String, String, Long> sessionAggregator = new KStreamSessionWindowAggregate(SessionWindows.with(Duration.ofMillis(KStreamSessionWindowAggregateProcessorTest.GAP_MS)), KStreamSessionWindowAggregateProcessorTest.STORE_NAME, initializer, aggregator, sessionMerger);

    private final List<KeyValue> results = new ArrayList<>();

    private final Processor<String, String> processor = sessionAggregator.get();

    private SessionStore<String, Long> sessionStore;

    private InternalMockProcessorContext context;

    private Metrics metrics;

    @Test
    public void shouldCreateSingleSessionWhenWithinGap() {
        context.setTime(0);
        processor.process("john", "first");
        context.setTime(500);
        processor.process("john", "second");
        final KeyValueIterator<Windowed<String>, Long> values = sessionStore.findSessions("john", 0, 2000);
        Assert.assertTrue(values.hasNext());
        Assert.assertEquals(Long.valueOf(2), values.next().value);
    }

    @Test
    public void shouldMergeSessions() {
        context.setTime(0);
        final String sessionId = "mel";
        processor.process(sessionId, "first");
        Assert.assertTrue(sessionStore.findSessions(sessionId, 0, 0).hasNext());
        // move time beyond gap
        context.setTime(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        processor.process(sessionId, "second");
        Assert.assertTrue(sessionStore.findSessions(sessionId, ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1), ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1)).hasNext());
        // should still exist as not within gap
        Assert.assertTrue(sessionStore.findSessions(sessionId, 0, 0).hasNext());
        // move time back
        context.setTime(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2));
        processor.process(sessionId, "third");
        final KeyValueIterator<Windowed<String>, Long> iterator = sessionStore.findSessions(sessionId, 0, ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        final KeyValue<Windowed<String>, Long> kv = iterator.next();
        Assert.assertEquals(Long.valueOf(3), kv.value);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldUpdateSessionIfTheSameTime() {
        context.setTime(0);
        processor.process("mel", "first");
        processor.process("mel", "second");
        final KeyValueIterator<Windowed<String>, Long> iterator = sessionStore.findSessions("mel", 0, 0);
        Assert.assertEquals(Long.valueOf(2L), iterator.next().value);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldHaveMultipleSessionsForSameIdWhenTimestampApartBySessionGap() {
        final String sessionId = "mel";
        long time = 0;
        context.setTime(time);
        processor.process(sessionId, "first");
        context.setTime((time += (KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        processor.process(sessionId, "second");
        processor.process(sessionId, "second");
        context.setTime((time += (KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        processor.process(sessionId, "third");
        processor.process(sessionId, "third");
        processor.process(sessionId, "third");
        sessionStore.flush();
        Assert.assertEquals(Arrays.asList(KeyValue.pair(new Windowed(sessionId, new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed(sessionId, new SessionWindow(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1), ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1))), new Change(2L, null)), KeyValue.pair(new Windowed(sessionId, new SessionWindow(time, time)), new Change(3L, null))), results);
    }

    @Test
    public void shouldRemoveMergedSessionsFromStateStore() {
        context.setTime(0);
        processor.process("a", "1");
        // first ensure it is in the store
        final KeyValueIterator<Windowed<String>, Long> a1 = sessionStore.findSessions("a", 0, 0);
        Assert.assertEquals(KeyValue.pair(new Windowed("a", new SessionWindow(0, 0)), 1L), a1.next());
        context.setTime(100);
        processor.process("a", "2");
        // a1 from above should have been removed
        // should have merged session in store
        final KeyValueIterator<Windowed<String>, Long> a2 = sessionStore.findSessions("a", 0, 100);
        Assert.assertEquals(KeyValue.pair(new Windowed("a", new SessionWindow(0, 100)), 2L), a2.next());
        Assert.assertFalse(a2.hasNext());
    }

    @Test
    public void shouldHandleMultipleSessionsAndMerging() {
        context.setTime(0);
        processor.process("a", "1");
        processor.process("b", "1");
        processor.process("c", "1");
        processor.process("d", "1");
        context.setTime(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2));
        processor.process("d", "2");
        context.setTime(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        processor.process("a", "2");
        processor.process("b", "2");
        context.setTime((((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1) + ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2)));
        processor.process("a", "3");
        processor.process("c", "3");
        sessionStore.flush();
        Assert.assertEquals(Arrays.asList(KeyValue.pair(new Windowed("a", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("b", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("c", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("d", new SessionWindow(0, ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2))), new Change(2L, null)), KeyValue.pair(new Windowed("b", new SessionWindow(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1), ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1))), new Change(1L, null)), KeyValue.pair(new Windowed("a", new SessionWindow(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1), (((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1) + ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2)))), new Change(2L, null)), KeyValue.pair(new Windowed("c", new SessionWindow((((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1) + ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2)), (((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1) + ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) / 2)))), new Change(1L, null))), results);
    }

    @Test
    public void shouldGetAggregatedValuesFromValueGetter() {
        final KTableValueGetter<Windowed<String>, Long> getter = sessionAggregator.view().get();
        getter.init(context);
        context.setTime(0);
        processor.process("a", "1");
        context.setTime(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1));
        processor.process("a", "1");
        processor.process("a", "2");
        final long t0 = getter.get(new Windowed("a", new SessionWindow(0, 0)));
        final long t1 = getter.get(new Windowed("a", new SessionWindow(((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1), ((KStreamSessionWindowAggregateProcessorTest.GAP_MS) + 1))));
        Assert.assertEquals(1L, t0);
        Assert.assertEquals(2L, t1);
    }

    @Test
    public void shouldImmediatelyForwardNewSessionWhenNonCachedStore() {
        initStore(false);
        processor.init(context);
        context.setTime(0);
        processor.process("a", "1");
        processor.process("b", "1");
        processor.process("c", "1");
        Assert.assertEquals(Arrays.asList(KeyValue.pair(new Windowed("a", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("b", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("c", new SessionWindow(0, 0)), new Change(1L, null))), results);
    }

    @Test
    public void shouldImmediatelyForwardRemovedSessionsWhenMerging() {
        initStore(false);
        processor.init(context);
        context.setTime(0);
        processor.process("a", "1");
        context.setTime(5);
        processor.process("a", "1");
        Assert.assertEquals(Arrays.asList(KeyValue.pair(new Windowed("a", new SessionWindow(0, 0)), new Change(1L, null)), KeyValue.pair(new Windowed("a", new SessionWindow(0, 0)), new Change(null, null)), KeyValue.pair(new Windowed("a", new SessionWindow(0, 5)), new Change(2L, null))), results);
    }

    @Test
    public void shouldLogAndMeterWhenSkippingNullKey() {
        initStore(false);
        processor.init(context);
        context.setRecordContext(new ProcessorRecordContext((-1), (-2), (-3), "topic", null));
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        processor.process(null, "1");
        LogCaptureAppender.unregister(appender);
        Assert.assertEquals(1.0, StreamsTestUtils.getMetricByName(metrics(), "skipped-records-total", "stream-metrics").metricValue());
        MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record due to null key. value=[1] topic=[topic] partition=[-3] offset=[-2]"));
    }

    @Test
    public void shouldLogAndMeterWhenSkippingLateRecord() {
        LogCaptureAppender.setClassLoggerToDebug(KStreamSessionWindowAggregate.class);
        final LogCaptureAppender appender = LogCaptureAppender.createAndRegister();
        final Processor<String, String> processor = new KStreamSessionWindowAggregate(SessionWindows.with(Duration.ofMillis(10L)).grace(Duration.ofMillis(10L)), KStreamSessionWindowAggregateProcessorTest.STORE_NAME, initializer, aggregator, sessionMerger).get();
        initStore(false);
        processor.init(context);
        // dummy record to advance stream time
        context.setRecordContext(new ProcessorRecordContext(20, (-2), (-3), "topic", null));
        processor.process("dummy", "dummy");
        context.setRecordContext(new ProcessorRecordContext(0, (-2), (-3), "topic", null));
        processor.process("A", "1");
        context.setRecordContext(new ProcessorRecordContext(1, (-2), (-3), "topic", null));
        processor.process("A", "1");
        LogCaptureAppender.unregister(appender);
        final MetricName dropMetric = new MetricName("late-record-drop-total", "stream-processor-node-metrics", "The total number of occurrence of late-record-drop operations.", mkMap(mkEntry("client-id", "test"), mkEntry("task-id", "0_0"), mkEntry("processor-node-id", "TESTING_NODE")));
        MatcherAssert.assertThat(metrics.metrics().get(dropMetric).metricValue(), CoreMatchers.is(2.0));
        final MetricName dropRate = new MetricName("late-record-drop-rate", "stream-processor-node-metrics", "The average number of occurrence of late-record-drop operations.", mkMap(mkEntry("client-id", "test"), mkEntry("task-id", "0_0"), mkEntry("processor-node-id", "TESTING_NODE")));
        MatcherAssert.assertThat(((Double) (metrics.metrics().get(dropRate).metricValue())), Matchers.greaterThan(0.0));
        MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record for expired window. key=[A] topic=[topic] partition=[-3] offset=[-2] timestamp=[0] window=[0,0) expiration=[10]"));
        MatcherAssert.assertThat(appender.getMessages(), CoreMatchers.hasItem("Skipping record for expired window. key=[A] topic=[topic] partition=[-3] offset=[-2] timestamp=[1] window=[1,1) expiration=[10]"));
    }
}

