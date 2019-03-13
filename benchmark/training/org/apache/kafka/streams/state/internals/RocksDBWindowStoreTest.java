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
package org.apache.kafka.streams.state.internals;


import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.internals.MockStreamsMetrics;
import org.apache.kafka.streams.processor.internals.RecordCollector;
import org.apache.kafka.streams.processor.internals.RecordCollectorImpl;
import org.apache.kafka.streams.state.StateSerdes;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("PointlessArithmeticExpression")
public class RocksDBWindowStoreTest {
    private static final long DEFAULT_CACHE_SIZE_BYTES = 1024 * 1024L;

    private final int numSegments = 3;

    private final long windowSize = 3L;

    private final long segmentInterval = 60000L;

    private final long retentionPeriod = (segmentInterval) * ((numSegments) - 1);

    private final String windowName = "window";

    private final KeyValueSegments segments = new KeyValueSegments(windowName, retentionPeriod, segmentInterval);

    private final StateSerdes<Integer, String> serdes = new StateSerdes("", Serdes.Integer(), Serdes.String());

    private final List<KeyValue<byte[], byte[]>> changeLog = new ArrayList<>();

    private final ThreadCache cache = new ThreadCache(new LogContext("TestCache "), RocksDBWindowStoreTest.DEFAULT_CACHE_SIZE_BYTES, new MockStreamsMetrics(new Metrics()));

    private final Producer<byte[], byte[]> producer = new org.apache.kafka.clients.producer.MockProducer(true, Serdes.ByteArray().serializer(), Serdes.ByteArray().serializer());

    private final RecordCollector recordCollector = new RecordCollectorImpl("RocksDBWindowStoreTestTask", new LogContext("RocksDBWindowStoreTestTask "), new DefaultProductionExceptionHandler(), new Metrics().sensor("skipped-records")) {
        @Override
        public <K1, V1> void send(final String topic, final K1 key, final V1 value, final Headers headers, final Integer partition, final Long timestamp, final Serializer<K1> keySerializer, final Serializer<V1> valueSerializer) {
            changeLog.add(new KeyValue(keySerializer.serialize(topic, headers, key), valueSerializer.serialize(topic, headers, value)));
        }
    };

    private final File baseDir = TestUtils.tempDirectory("test");

    private final InternalMockProcessorContext context = new InternalMockProcessorContext(baseDir, Serdes.ByteArray(), Serdes.ByteArray(), recordCollector, cache);

    private WindowStore<Integer, String> windowStore;

    @Test
    public void shouldOnlyIterateOpenSegments() {
        windowStore = createWindowStore(context, false);
        long currentTime = 0;
        setCurrentTime(currentTime);
        windowStore.put(1, "one");
        currentTime = currentTime + (segmentInterval);
        setCurrentTime(currentTime);
        windowStore.put(1, "two");
        currentTime = currentTime + (segmentInterval);
        setCurrentTime(currentTime);
        windowStore.put(1, "three");
        final WindowStoreIterator<String> iterator = windowStore.fetch(1, Instant.ofEpochMilli(0), Instant.ofEpochMilli(currentTime));
        // roll to the next segment that will close the first
        currentTime = currentTime + (segmentInterval);
        setCurrentTime(currentTime);
        windowStore.put(1, "four");
        // should only have 2 values as the first segment is no longer open
        Assert.assertEquals(new KeyValue(segmentInterval, "two"), iterator.next());
        Assert.assertEquals(new KeyValue((2 * (segmentInterval)), "three"), iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testRangeAndSinglePointFetch() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        Assert.assertEquals("zero", windowStore.fetch(0, startTime));
        Assert.assertEquals("one", windowStore.fetch(1, (startTime + 1L)));
        Assert.assertEquals("two", windowStore.fetch(2, (startTime + 2L)));
        Assert.assertEquals("four", windowStore.fetch(4, (startTime + 4L)));
        Assert.assertEquals("five", windowStore.fetch(5, (startTime + 5L)));
        Assert.assertEquals(Collections.singletonList("zero"), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 0) - (windowSize))), Instant.ofEpochMilli(((startTime + 0) + (windowSize))))));
        putSecondBatch(windowStore, startTime, context);
        Assert.assertEquals("two+1", windowStore.fetch(2, (startTime + 3L)));
        Assert.assertEquals("two+2", windowStore.fetch(2, (startTime + 4L)));
        Assert.assertEquals("two+3", windowStore.fetch(2, (startTime + 5L)));
        Assert.assertEquals("two+4", windowStore.fetch(2, (startTime + 6L)));
        Assert.assertEquals("two+5", windowStore.fetch(2, (startTime + 7L)));
        Assert.assertEquals("two+6", windowStore.fetch(2, (startTime + 8L)));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime - 2L) - (windowSize))), Instant.ofEpochMilli(((startTime - 2L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime - 1L) - (windowSize))), Instant.ofEpochMilli(((startTime - 1L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 1L) - (windowSize))), Instant.ofEpochMilli(((startTime + 1L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 2L) - (windowSize))), Instant.ofEpochMilli(((startTime + 2L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3", "two+4"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 3L) - (windowSize))), Instant.ofEpochMilli(((startTime + 3L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3", "two+4", "two+5"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 4L) - (windowSize))), Instant.ofEpochMilli(((startTime + 4L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 5L) - (windowSize))), Instant.ofEpochMilli(((startTime + 5L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+1", "two+2", "two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 6L) - (windowSize))), Instant.ofEpochMilli(((startTime + 6L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+2", "two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 7L) - (windowSize))), Instant.ofEpochMilli(((startTime + 7L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 8L) - (windowSize))), Instant.ofEpochMilli(((startTime + 8L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 9L) - (windowSize))), Instant.ofEpochMilli(((startTime + 9L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 10L) - (windowSize))), Instant.ofEpochMilli(((startTime + 10L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 11L) - (windowSize))), Instant.ofEpochMilli(((startTime + 11L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 12L) - (windowSize))), Instant.ofEpochMilli(((startTime + 12L) + (windowSize))))));
        // Flush the store and verify all current entries were properly flushed ...
        windowStore.flush();
        final Map<Integer, Set<String>> entriesByKey = entriesByKey(changeLog, startTime);
        Assert.assertEquals(Utils.mkSet("zero@0"), entriesByKey.get(0));
        Assert.assertEquals(Utils.mkSet("one@1"), entriesByKey.get(1));
        Assert.assertEquals(Utils.mkSet("two@2", "two+1@3", "two+2@4", "two+3@5", "two+4@6", "two+5@7", "two+6@8"), entriesByKey.get(2));
        Assert.assertNull(entriesByKey.get(3));
        Assert.assertEquals(Utils.mkSet("four@4"), entriesByKey.get(4));
        Assert.assertEquals(Utils.mkSet("five@5"), entriesByKey.get(5));
        Assert.assertNull(entriesByKey.get(6));
    }

    @Test
    public void shouldGetAll() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        final KeyValue<Windowed<Integer>, String> zero = windowedPair(0, "zero", (startTime + 0));
        final KeyValue<Windowed<Integer>, String> one = windowedPair(1, "one", (startTime + 1));
        final KeyValue<Windowed<Integer>, String> two = windowedPair(2, "two", (startTime + 2));
        final KeyValue<Windowed<Integer>, String> four = windowedPair(4, "four", (startTime + 4));
        final KeyValue<Windowed<Integer>, String> five = windowedPair(5, "five", (startTime + 5));
        Assert.assertEquals(Arrays.asList(zero, one, two, four, five), StreamsTestUtils.toList(windowStore.all()));
    }

    @Test
    public void shouldFetchAllInTimeRange() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        final KeyValue<Windowed<Integer>, String> zero = windowedPair(0, "zero", (startTime + 0));
        final KeyValue<Windowed<Integer>, String> one = windowedPair(1, "one", (startTime + 1));
        final KeyValue<Windowed<Integer>, String> two = windowedPair(2, "two", (startTime + 2));
        final KeyValue<Windowed<Integer>, String> four = windowedPair(4, "four", (startTime + 4));
        final KeyValue<Windowed<Integer>, String> five = windowedPair(5, "five", (startTime + 5));
        Assert.assertEquals(Arrays.asList(one, two, four), StreamsTestUtils.toList(windowStore.fetchAll(Instant.ofEpochMilli((startTime + 1)), Instant.ofEpochMilli((startTime + 4)))));
        Assert.assertEquals(Arrays.asList(zero, one, two), StreamsTestUtils.toList(windowStore.fetchAll(Instant.ofEpochMilli((startTime + 0)), Instant.ofEpochMilli((startTime + 3)))));
        Assert.assertEquals(Arrays.asList(one, two, four, five), StreamsTestUtils.toList(windowStore.fetchAll(Instant.ofEpochMilli((startTime + 1)), Instant.ofEpochMilli((startTime + 5)))));
    }

    @Test
    public void testFetchRange() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        final KeyValue<Windowed<Integer>, String> zero = windowedPair(0, "zero", (startTime + 0));
        final KeyValue<Windowed<Integer>, String> one = windowedPair(1, "one", (startTime + 1));
        final KeyValue<Windowed<Integer>, String> two = windowedPair(2, "two", (startTime + 2));
        final KeyValue<Windowed<Integer>, String> four = windowedPair(4, "four", (startTime + 4));
        final KeyValue<Windowed<Integer>, String> five = windowedPair(5, "five", (startTime + 5));
        Assert.assertEquals(Arrays.asList(zero, one), StreamsTestUtils.toList(windowStore.fetch(0, 1, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli(((startTime + 0L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList(one), StreamsTestUtils.toList(windowStore.fetch(1, 1, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli(((startTime + 0L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList(one, two), StreamsTestUtils.toList(windowStore.fetch(1, 3, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli(((startTime + 0L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList(zero, one, two), StreamsTestUtils.toList(windowStore.fetch(0, 5, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli(((startTime + 0L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList(zero, one, two, four, five), StreamsTestUtils.toList(windowStore.fetch(0, 5, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli((((startTime + 0L) + (windowSize)) + 5L)))));
        Assert.assertEquals(Arrays.asList(two, four, five), StreamsTestUtils.toList(windowStore.fetch(0, 5, Instant.ofEpochMilli((startTime + 2L)), Instant.ofEpochMilli((((startTime + 0L) + (windowSize)) + 5L)))));
        Assert.assertEquals(Collections.emptyList(), StreamsTestUtils.toList(windowStore.fetch(4, 5, Instant.ofEpochMilli((startTime + 2L)), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), StreamsTestUtils.toList(windowStore.fetch(0, 3, Instant.ofEpochMilli((startTime + 3L)), Instant.ofEpochMilli(((startTime + (windowSize)) + 5)))));
    }

    @Test
    public void testPutAndFetchBefore() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        Assert.assertEquals(Collections.singletonList("zero"), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli((startTime + 0L)))));
        Assert.assertEquals(Collections.singletonList("one"), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + 1L) - (windowSize))), Instant.ofEpochMilli((startTime + 1L)))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 2L) - (windowSize))), Instant.ofEpochMilli((startTime + 2L)))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + 3L) - (windowSize))), Instant.ofEpochMilli((startTime + 3L)))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + 4L) - (windowSize))), Instant.ofEpochMilli((startTime + 4L)))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + 5L) - (windowSize))), Instant.ofEpochMilli((startTime + 5L)))));
        putSecondBatch(windowStore, startTime, context);
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime - 1L) - (windowSize))), Instant.ofEpochMilli((startTime - 1L)))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 0L) - (windowSize))), Instant.ofEpochMilli((startTime + 0L)))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 1L) - (windowSize))), Instant.ofEpochMilli((startTime + 1L)))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 2L) - (windowSize))), Instant.ofEpochMilli((startTime + 2L)))));
        Assert.assertEquals(Arrays.asList("two", "two+1"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 3L) - (windowSize))), Instant.ofEpochMilli((startTime + 3L)))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 4L) - (windowSize))), Instant.ofEpochMilli((startTime + 4L)))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 5L) - (windowSize))), Instant.ofEpochMilli((startTime + 5L)))));
        Assert.assertEquals(Arrays.asList("two+1", "two+2", "two+3", "two+4"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 6L) - (windowSize))), Instant.ofEpochMilli((startTime + 6L)))));
        Assert.assertEquals(Arrays.asList("two+2", "two+3", "two+4", "two+5"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 7L) - (windowSize))), Instant.ofEpochMilli((startTime + 7L)))));
        Assert.assertEquals(Arrays.asList("two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 8L) - (windowSize))), Instant.ofEpochMilli((startTime + 8L)))));
        Assert.assertEquals(Arrays.asList("two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 9L) - (windowSize))), Instant.ofEpochMilli((startTime + 9L)))));
        Assert.assertEquals(Arrays.asList("two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 10L) - (windowSize))), Instant.ofEpochMilli((startTime + 10L)))));
        Assert.assertEquals(Collections.singletonList("two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 11L) - (windowSize))), Instant.ofEpochMilli((startTime + 11L)))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 12L) - (windowSize))), Instant.ofEpochMilli((startTime + 12L)))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + 13L) - (windowSize))), Instant.ofEpochMilli((startTime + 13L)))));
        // Flush the store and verify all current entries were properly flushed ...
        windowStore.flush();
        final Map<Integer, Set<String>> entriesByKey = entriesByKey(changeLog, startTime);
        Assert.assertEquals(Utils.mkSet("zero@0"), entriesByKey.get(0));
        Assert.assertEquals(Utils.mkSet("one@1"), entriesByKey.get(1));
        Assert.assertEquals(Utils.mkSet("two@2", "two+1@3", "two+2@4", "two+3@5", "two+4@6", "two+5@7", "two+6@8"), entriesByKey.get(2));
        Assert.assertNull(entriesByKey.get(3));
        Assert.assertEquals(Utils.mkSet("four@4"), entriesByKey.get(4));
        Assert.assertEquals(Utils.mkSet("five@5"), entriesByKey.get(5));
        Assert.assertNull(entriesByKey.get(6));
    }

    @Test
    public void testPutAndFetchAfter() {
        windowStore = createWindowStore(context, false);
        final long startTime = (segmentInterval) - 4L;
        putFirstBatch(windowStore, startTime, context);
        Assert.assertEquals(Collections.singletonList("zero"), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime + 0L)), Instant.ofEpochMilli(((startTime + 0L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("one"), toList(windowStore.fetch(1, Instant.ofEpochMilli((startTime + 1L)), Instant.ofEpochMilli(((startTime + 1L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 2L)), Instant.ofEpochMilli(((startTime + 2L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli((startTime + 3L)), Instant.ofEpochMilli(((startTime + 3L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli((startTime + 4L)), Instant.ofEpochMilli(((startTime + 4L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli((startTime + 5L)), Instant.ofEpochMilli(((startTime + 5L) + (windowSize))))));
        putSecondBatch(windowStore, startTime, context);
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime - 2L)), Instant.ofEpochMilli(((startTime - 2L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime - 1L)), Instant.ofEpochMilli(((startTime - 1L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1"), toList(windowStore.fetch(2, Instant.ofEpochMilli(startTime), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 1L)), Instant.ofEpochMilli(((startTime + 1L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two", "two+1", "two+2", "two+3"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 2L)), Instant.ofEpochMilli(((startTime + 2L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+1", "two+2", "two+3", "two+4"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 3L)), Instant.ofEpochMilli(((startTime + 3L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+2", "two+3", "two+4", "two+5"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 4L)), Instant.ofEpochMilli(((startTime + 4L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+3", "two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 5L)), Instant.ofEpochMilli(((startTime + 5L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+4", "two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 6L)), Instant.ofEpochMilli(((startTime + 6L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("two+5", "two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 7L)), Instant.ofEpochMilli(((startTime + 7L) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two+6"), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 8L)), Instant.ofEpochMilli(((startTime + 8L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 9L)), Instant.ofEpochMilli(((startTime + 9L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 10L)), Instant.ofEpochMilli(((startTime + 10L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 11L)), Instant.ofEpochMilli(((startTime + 11L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli((startTime + 12L)), Instant.ofEpochMilli(((startTime + 12L) + (windowSize))))));
        // Flush the store and verify all current entries were properly flushed ...
        windowStore.flush();
        final Map<Integer, Set<String>> entriesByKey = entriesByKey(changeLog, startTime);
        Assert.assertEquals(Utils.mkSet("zero@0"), entriesByKey.get(0));
        Assert.assertEquals(Utils.mkSet("one@1"), entriesByKey.get(1));
        Assert.assertEquals(Utils.mkSet("two@2", "two+1@3", "two+2@4", "two+3@5", "two+4@6", "two+5@7", "two+6@8"), entriesByKey.get(2));
        Assert.assertNull(entriesByKey.get(3));
        Assert.assertEquals(Utils.mkSet("four@4"), entriesByKey.get(4));
        Assert.assertEquals(Utils.mkSet("five@5"), entriesByKey.get(5));
        Assert.assertNull(entriesByKey.get(6));
    }

    @Test
    public void testPutSameKeyTimestamp() {
        windowStore = createWindowStore(context, true);
        final long startTime = (segmentInterval) - 4L;
        setCurrentTime(startTime);
        windowStore.put(0, "zero");
        Assert.assertEquals(Collections.singletonList("zero"), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        windowStore.put(0, "zero");
        windowStore.put(0, "zero+");
        windowStore.put(0, "zero++");
        Assert.assertEquals(Arrays.asList("zero", "zero", "zero+", "zero++"), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Arrays.asList("zero", "zero", "zero+", "zero++"), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 1L) - (windowSize))), Instant.ofEpochMilli(((startTime + 1L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("zero", "zero", "zero+", "zero++"), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 2L) - (windowSize))), Instant.ofEpochMilli(((startTime + 2L) + (windowSize))))));
        Assert.assertEquals(Arrays.asList("zero", "zero", "zero+", "zero++"), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 3L) - (windowSize))), Instant.ofEpochMilli(((startTime + 3L) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli(((startTime + 4L) - (windowSize))), Instant.ofEpochMilli(((startTime + 4L) + (windowSize))))));
        // Flush the store and verify all current entries were properly flushed ...
        windowStore.flush();
        final Map<Integer, Set<String>> entriesByKey = entriesByKey(changeLog, startTime);
        Assert.assertEquals(Utils.mkSet("zero@0", "zero@0", "zero+@0", "zero++@0"), entriesByKey.get(0));
    }

    @Test
    public void testRolling() {
        windowStore = createWindowStore(context, false);
        // to validate segments
        final long startTime = (segmentInterval) * 2;
        final long increment = (segmentInterval) / 2;
        setCurrentTime(startTime);
        windowStore.put(0, "zero");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(2)), segmentDirs(baseDir));
        setCurrentTime((startTime + increment));
        windowStore.put(1, "one");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(2)), segmentDirs(baseDir));
        setCurrentTime((startTime + (increment * 2)));
        windowStore.put(2, "two");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(2), segments.segmentName(3)), segmentDirs(baseDir));
        setCurrentTime((startTime + (increment * 4)));
        windowStore.put(4, "four");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(2), segments.segmentName(3), segments.segmentName(4)), segmentDirs(baseDir));
        setCurrentTime((startTime + (increment * 5)));
        windowStore.put(5, "five");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(2), segments.segmentName(3), segments.segmentName(4)), segmentDirs(baseDir));
        Assert.assertEquals(Collections.singletonList("zero"), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("one"), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        setCurrentTime((startTime + (increment * 6)));
        windowStore.put(6, "six");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(3), segments.segmentName(4), segments.segmentName(5)), segmentDirs(baseDir));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("six"), toList(windowStore.fetch(6, Instant.ofEpochMilli(((startTime + (increment * 6)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 6)) + (windowSize))))));
        setCurrentTime((startTime + (increment * 7)));
        windowStore.put(7, "seven");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(3), segments.segmentName(4), segments.segmentName(5)), segmentDirs(baseDir));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("two"), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("six"), toList(windowStore.fetch(6, Instant.ofEpochMilli(((startTime + (increment * 6)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 6)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("seven"), toList(windowStore.fetch(7, Instant.ofEpochMilli(((startTime + (increment * 7)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 7)) + (windowSize))))));
        setCurrentTime((startTime + (increment * 8)));
        windowStore.put(8, "eight");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(4), segments.segmentName(5), segments.segmentName(6)), segmentDirs(baseDir));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("six"), toList(windowStore.fetch(6, Instant.ofEpochMilli(((startTime + (increment * 6)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 6)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("seven"), toList(windowStore.fetch(7, Instant.ofEpochMilli(((startTime + (increment * 7)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 7)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("eight"), toList(windowStore.fetch(8, Instant.ofEpochMilli(((startTime + (increment * 8)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 8)) + (windowSize))))));
        // check segment directories
        windowStore.flush();
        Assert.assertEquals(Utils.mkSet(segments.segmentName(4), segments.segmentName(5), segments.segmentName(6)), segmentDirs(baseDir));
    }

    @Test
    public void testRestore() throws Exception {
        final long startTime = (segmentInterval) * 2;
        final long increment = (segmentInterval) / 2;
        windowStore = createWindowStore(context, false);
        setCurrentTime(startTime);
        windowStore.put(0, "zero");
        setCurrentTime((startTime + increment));
        windowStore.put(1, "one");
        setCurrentTime((startTime + (increment * 2)));
        windowStore.put(2, "two");
        setCurrentTime((startTime + (increment * 3)));
        windowStore.put(3, "three");
        setCurrentTime((startTime + (increment * 4)));
        windowStore.put(4, "four");
        setCurrentTime((startTime + (increment * 5)));
        windowStore.put(5, "five");
        setCurrentTime((startTime + (increment * 6)));
        windowStore.put(6, "six");
        setCurrentTime((startTime + (increment * 7)));
        windowStore.put(7, "seven");
        setCurrentTime((startTime + (increment * 8)));
        windowStore.put(8, "eight");
        windowStore.flush();
        windowStore.close();
        // remove local store image
        Utils.delete(baseDir);
        windowStore = createWindowStore(context, false);
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(6, Instant.ofEpochMilli(((startTime + (increment * 6)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 6)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(7, Instant.ofEpochMilli(((startTime + (increment * 7)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 7)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(8, Instant.ofEpochMilli(((startTime + (increment * 8)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 8)) + (windowSize))))));
        context.restore(windowName, changeLog);
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(0, Instant.ofEpochMilli((startTime - (windowSize))), Instant.ofEpochMilli((startTime + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(1, Instant.ofEpochMilli(((startTime + increment) - (windowSize))), Instant.ofEpochMilli(((startTime + increment) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(2, Instant.ofEpochMilli(((startTime + (increment * 2)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 2)) + (windowSize))))));
        Assert.assertEquals(Collections.emptyList(), toList(windowStore.fetch(3, Instant.ofEpochMilli(((startTime + (increment * 3)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 3)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("four"), toList(windowStore.fetch(4, Instant.ofEpochMilli(((startTime + (increment * 4)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 4)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("five"), toList(windowStore.fetch(5, Instant.ofEpochMilli(((startTime + (increment * 5)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 5)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("six"), toList(windowStore.fetch(6, Instant.ofEpochMilli(((startTime + (increment * 6)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 6)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("seven"), toList(windowStore.fetch(7, Instant.ofEpochMilli(((startTime + (increment * 7)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 7)) + (windowSize))))));
        Assert.assertEquals(Collections.singletonList("eight"), toList(windowStore.fetch(8, Instant.ofEpochMilli(((startTime + (increment * 8)) - (windowSize))), Instant.ofEpochMilli(((startTime + (increment * 8)) + (windowSize))))));
        // check segment directories
        windowStore.flush();
        Assert.assertEquals(Utils.mkSet(segments.segmentName(4L), segments.segmentName(5L), segments.segmentName(6L)), segmentDirs(baseDir));
    }

    @Test
    public void testSegmentMaintenance() {
        windowStore = createWindowStore(context, true);
        context.setTime(0L);
        setCurrentTime(0);
        windowStore.put(0, "v");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(0L)), segmentDirs(baseDir));
        setCurrentTime(((segmentInterval) - 1));
        windowStore.put(0, "v");
        windowStore.put(0, "v");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(0L)), segmentDirs(baseDir));
        setCurrentTime(segmentInterval);
        windowStore.put(0, "v");
        Assert.assertEquals(Utils.mkSet(segments.segmentName(0L), segments.segmentName(1L)), segmentDirs(baseDir));
        WindowStoreIterator iter;
        int fetchedCount;
        iter = windowStore.fetch(0, Instant.ofEpochMilli(0L), Instant.ofEpochMilli(((segmentInterval) * 4)));
        fetchedCount = 0;
        while (iter.hasNext()) {
            iter.next();
            fetchedCount++;
        } 
        Assert.assertEquals(4, fetchedCount);
        Assert.assertEquals(Utils.mkSet(segments.segmentName(0L), segments.segmentName(1L)), segmentDirs(baseDir));
        setCurrentTime(((segmentInterval) * 3));
        windowStore.put(0, "v");
        iter = windowStore.fetch(0, Instant.ofEpochMilli(0L), Instant.ofEpochMilli(((segmentInterval) * 4)));
        fetchedCount = 0;
        while (iter.hasNext()) {
            iter.next();
            fetchedCount++;
        } 
        Assert.assertEquals(2, fetchedCount);
        Assert.assertEquals(Utils.mkSet(segments.segmentName(1L), segments.segmentName(3L)), segmentDirs(baseDir));
        setCurrentTime(((segmentInterval) * 5));
        windowStore.put(0, "v");
        iter = windowStore.fetch(0, Instant.ofEpochMilli(((segmentInterval) * 4)), Instant.ofEpochMilli(((segmentInterval) * 10)));
        fetchedCount = 0;
        while (iter.hasNext()) {
            iter.next();
            fetchedCount++;
        } 
        Assert.assertEquals(1, fetchedCount);
        Assert.assertEquals(Utils.mkSet(segments.segmentName(3L), segments.segmentName(5L)), segmentDirs(baseDir));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testInitialLoading() {
        final File storeDir = new File(baseDir, windowName);
        windowStore = createWindowStore(context, false);
        new File(storeDir, segments.segmentName(0L)).mkdir();
        new File(storeDir, segments.segmentName(1L)).mkdir();
        new File(storeDir, segments.segmentName(2L)).mkdir();
        new File(storeDir, segments.segmentName(3L)).mkdir();
        new File(storeDir, segments.segmentName(4L)).mkdir();
        new File(storeDir, segments.segmentName(5L)).mkdir();
        new File(storeDir, segments.segmentName(6L)).mkdir();
        windowStore.close();
        windowStore = createWindowStore(context, false);
        // put something in the store to advance its stream time and expire the old segments
        windowStore.put(1, "v", (6L * (segmentInterval)));
        final List<String> expected = Arrays.asList(segments.segmentName(4L), segments.segmentName(5L), segments.segmentName(6L));
        expected.sort(String::compareTo);
        final List<String> actual = Utils.toList(segmentDirs(baseDir).iterator());
        actual.sort(String::compareTo);
        Assert.assertEquals(expected, actual);
        try (final WindowStoreIterator iter = windowStore.fetch(0, Instant.ofEpochMilli(0L), Instant.ofEpochMilli(1000000L))) {
            while (iter.hasNext()) {
                iter.next();
            } 
        }
        Assert.assertEquals(Utils.mkSet(segments.segmentName(4L), segments.segmentName(5L), segments.segmentName(6L)), segmentDirs(baseDir));
    }

    @Test
    public void shouldCloseOpenIteratorsWhenStoreIsClosedAndNotThrowInvalidStateStoreExceptionOnHasNext() {
        windowStore = createWindowStore(context, false);
        setCurrentTime(0);
        windowStore.put(1, "one", 1L);
        windowStore.put(1, "two", 2L);
        windowStore.put(1, "three", 3L);
        final WindowStoreIterator<String> iterator = windowStore.fetch(1, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(3L));
        Assert.assertTrue(iterator.hasNext());
        windowStore.close();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldFetchAndIterateOverExactKeys() {
        final long windowSize = 8791026472627208192L;
        final long retentionPeriod = 8791026472627208192L;
        final WindowStore<String, String> windowStore = Stores.windowStoreBuilder(Stores.persistentWindowStore(windowName, Duration.ofMillis(retentionPeriod), Duration.ofMillis(windowSize), true), Serdes.String(), Serdes.String()).build();
        windowStore.init(context, windowStore);
        windowStore.put("a", "0001", 0);
        windowStore.put("aa", "0002", 0);
        windowStore.put("a", "0003", 1);
        windowStore.put("aa", "0004", 1);
        windowStore.put("a", "0005", (8791026472627208192L - 1));
        final List expected = Arrays.asList("0001", "0003", "0005");
        MatcherAssert.assertThat(toList(windowStore.fetch("a", Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))), CoreMatchers.equalTo(expected));
        List<KeyValue<Windowed<String>, String>> list = StreamsTestUtils.toList(windowStore.fetch("a", "a", Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE)));
        MatcherAssert.assertThat(list, CoreMatchers.equalTo(Arrays.asList(RocksDBWindowStoreTest.windowedPair("a", "0001", 0, windowSize), RocksDBWindowStoreTest.windowedPair("a", "0003", 1, windowSize), RocksDBWindowStoreTest.windowedPair("a", "0005", (8791026472627208192L - 1), windowSize))));
        list = StreamsTestUtils.toList(windowStore.fetch("aa", "aa", Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE)));
        MatcherAssert.assertThat(list, CoreMatchers.equalTo(Arrays.asList(RocksDBWindowStoreTest.windowedPair("aa", "0002", 0, windowSize), RocksDBWindowStoreTest.windowedPair("aa", "0004", 1, windowSize))));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnPutNullKey() {
        windowStore = createWindowStore(context, false);
        windowStore.put(null, "anyValue");
    }

    @Test
    public void shouldNotThrowNullPointerExceptionOnPutNullValue() {
        windowStore = createWindowStore(context, false);
        windowStore.put(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnGetNullKey() {
        windowStore = createWindowStore(context, false);
        windowStore.fetch(null, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnRangeNullFromKey() {
        windowStore = createWindowStore(context, false);
        windowStore.fetch(null, 2, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnRangeNullToKey() {
        windowStore = createWindowStore(context, false);
        windowStore.fetch(1, null, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }

    @Test
    public void shouldFetchAndIterateOverExactBinaryKeys() {
        final WindowStore<Bytes, String> windowStore = Stores.windowStoreBuilder(Stores.persistentWindowStore(windowName, Duration.ofMillis(60000L), Duration.ofMillis(60000L), true), Serdes.Bytes(), Serdes.String()).build();
        windowStore.init(context, windowStore);
        final Bytes key1 = Bytes.wrap(new byte[]{ 0 });
        final Bytes key2 = Bytes.wrap(new byte[]{ 0, 0 });
        final Bytes key3 = Bytes.wrap(new byte[]{ 0, 0, 0 });
        windowStore.put(key1, "1", 0);
        windowStore.put(key2, "2", 0);
        windowStore.put(key3, "3", 0);
        windowStore.put(key1, "4", 1);
        windowStore.put(key2, "5", 1);
        windowStore.put(key3, "6", 59999);
        windowStore.put(key1, "7", 59999);
        windowStore.put(key2, "8", 59999);
        windowStore.put(key3, "9", 59999);
        final List expectedKey1 = Arrays.asList("1", "4", "7");
        MatcherAssert.assertThat(toList(windowStore.fetch(key1, Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))), CoreMatchers.equalTo(expectedKey1));
        final List expectedKey2 = Arrays.asList("2", "5", "8");
        MatcherAssert.assertThat(toList(windowStore.fetch(key2, Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))), CoreMatchers.equalTo(expectedKey2));
        final List expectedKey3 = Arrays.asList("3", "6", "9");
        MatcherAssert.assertThat(toList(windowStore.fetch(key3, Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))), CoreMatchers.equalTo(expectedKey3));
    }
}

