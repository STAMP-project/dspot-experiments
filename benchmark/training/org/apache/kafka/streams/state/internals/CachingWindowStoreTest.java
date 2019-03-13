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


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class CachingWindowStoreTest {
    private static final int MAX_CACHE_SIZE_BYTES = 150;

    private static final long DEFAULT_TIMESTAMP = 10L;

    private static final Long WINDOW_SIZE = 10L;

    private static final long SEGMENT_INTERVAL = 100L;

    private InternalMockProcessorContext context;

    private RocksDBSegmentedBytesStore underlying;

    private CachingWindowStore cachingStore;

    private CachingKeyValueStoreTest.CacheFlushListenerStub<Windowed<String>, String> cacheListener;

    private ThreadCache cache;

    private String topic;

    private WindowKeySchema keySchema;

    @Test
    public void shouldNotReturnDuplicatesInRanges() {
        final StreamsBuilder builder = new StreamsBuilder();
        final StoreBuilder<WindowStore<String, String>> storeBuilder = Stores.windowStoreBuilder(Stores.persistentWindowStore("store-name", Duration.ofHours(1L), Duration.ofMinutes(1L), false), Serdes.String(), Serdes.String()).withCachingEnabled();
        builder.addStateStore(storeBuilder);
        builder.stream(topic, Consumed.with(Serdes.String(), Serdes.String())).transform(() -> new Transformer<String, String, KeyValue<String, String>>() {
            private WindowStore<String, String> store;

            private int numRecordsProcessed;

            @SuppressWarnings("unchecked")
            @Override
            public void init(final ProcessorContext processorContext) {
                this.store = ((WindowStore<String, String>) (processorContext.getStateStore("store-name")));
                int count = 0;
                final KeyValueIterator<Windowed<String>, String> all = store.all();
                while (all.hasNext()) {
                    count++;
                    all.next();
                } 
                assertThat(count, equalTo(0));
            }

            @Override
            public KeyValue<String, String> transform(final String key, final String value) {
                int count = 0;
                final KeyValueIterator<Windowed<String>, String> all = store.all();
                while (all.hasNext()) {
                    count++;
                    all.next();
                } 
                assertThat(count, equalTo(numRecordsProcessed));
                store.put(value, value);
                (numRecordsProcessed)++;
                return new KeyValue<>(key, value);
            }

            @Override
            public void close() {
            }
        }, "store-name");
        final String bootstrapServers = "localhost:9092";
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(APPLICATION_ID_CONFIG, "test-app");
        streamsConfiguration.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath());
        streamsConfiguration.put(COMMIT_INTERVAL_MS_CONFIG, (10 * 1000));
        final long initialWallClockTime = 0L;
        final TopologyTestDriver driver = new TopologyTestDriver(builder.build(), streamsConfiguration, initialWallClockTime);
        final ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory(Serdes.String().serializer(), Serdes.String().serializer(), initialWallClockTime);
        for (int i = 0; i < 5; i++) {
            driver.pipeInput(recordFactory.create(topic, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }
        driver.advanceWallClockTime((10 * 1000L));
        recordFactory.advanceTimeMs((10 * 1000L));
        for (int i = 0; i < 5; i++) {
            driver.pipeInput(recordFactory.create(topic, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }
        driver.advanceWallClockTime((10 * 1000L));
        recordFactory.advanceTimeMs((10 * 1000L));
        for (int i = 0; i < 5; i++) {
            driver.pipeInput(recordFactory.create(topic, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }
        driver.advanceWallClockTime((10 * 1000L));
        recordFactory.advanceTimeMs((10 * 1000L));
        for (int i = 0; i < 5; i++) {
            driver.pipeInput(recordFactory.create(topic, UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }
    }

    @Test
    public void shouldPutFetchFromCache() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("b"), CachingWindowStoreTest.bytesValue("b"));
        MatcherAssert.assertThat(cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), 10), CoreMatchers.equalTo(CachingWindowStoreTest.bytesValue("a")));
        MatcherAssert.assertThat(cachingStore.fetch(CachingWindowStoreTest.bytesKey("b"), 10), CoreMatchers.equalTo(CachingWindowStoreTest.bytesValue("b")));
        MatcherAssert.assertThat(cachingStore.fetch(CachingWindowStoreTest.bytesKey("c"), 10), CoreMatchers.equalTo(null));
        MatcherAssert.assertThat(cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), 0), CoreMatchers.equalTo(null));
        final WindowStoreIterator<byte[]> a = cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), Instant.ofEpochMilli(10), Instant.ofEpochMilli(10));
        final WindowStoreIterator<byte[]> b = cachingStore.fetch(CachingWindowStoreTest.bytesKey("b"), Instant.ofEpochMilli(10), Instant.ofEpochMilli(10));
        verifyKeyValue(a.next(), CachingWindowStoreTest.DEFAULT_TIMESTAMP, "a");
        verifyKeyValue(b.next(), CachingWindowStoreTest.DEFAULT_TIMESTAMP, "b");
        Assert.assertFalse(a.hasNext());
        Assert.assertFalse(b.hasNext());
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void shouldPutFetchRangeFromCache() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("b"), CachingWindowStoreTest.bytesValue("b"));
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> iterator = cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesKey("b"), Instant.ofEpochMilli(10), Instant.ofEpochMilli(10));
        StreamsTestUtils.verifyWindowedKeyValue(iterator.next(), new Windowed(CachingWindowStoreTest.bytesKey("a"), new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)))), "a");
        StreamsTestUtils.verifyWindowedKeyValue(iterator.next(), new Windowed(CachingWindowStoreTest.bytesKey("b"), new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)))), "b");
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void shouldGetAllFromCache() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("b"), CachingWindowStoreTest.bytesValue("b"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("c"), CachingWindowStoreTest.bytesValue("c"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("d"), CachingWindowStoreTest.bytesValue("d"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("e"), CachingWindowStoreTest.bytesValue("e"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("f"), CachingWindowStoreTest.bytesValue("f"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("g"), CachingWindowStoreTest.bytesValue("g"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("h"), CachingWindowStoreTest.bytesValue("h"));
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> iterator = cachingStore.all();
        final String[] array = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h" };
        for (final String s : array) {
            StreamsTestUtils.verifyWindowedKeyValue(iterator.next(), new Windowed(CachingWindowStoreTest.bytesKey(s), new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)))), s);
        }
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldFetchAllWithinTimestampRange() {
        final String[] array = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h" };
        for (int i = 0; i < (array.length); i++) {
            context.setTime(i);
            cachingStore.put(CachingWindowStoreTest.bytesKey(array[i]), CachingWindowStoreTest.bytesValue(array[i]));
        }
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> iterator = cachingStore.fetchAll(Instant.ofEpochMilli(0), Instant.ofEpochMilli(7));
        for (int i = 0; i < (array.length); i++) {
            final String str = array[i];
            StreamsTestUtils.verifyWindowedKeyValue(iterator.next(), new Windowed(CachingWindowStoreTest.bytesKey(str), new TimeWindow(i, (i + (CachingWindowStoreTest.WINDOW_SIZE)))), str);
        }
        Assert.assertFalse(iterator.hasNext());
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> iterator1 = cachingStore.fetchAll(Instant.ofEpochMilli(2), Instant.ofEpochMilli(4));
        for (int i = 2; i <= 4; i++) {
            final String str = array[i];
            StreamsTestUtils.verifyWindowedKeyValue(iterator1.next(), new Windowed(CachingWindowStoreTest.bytesKey(str), new TimeWindow(i, (i + (CachingWindowStoreTest.WINDOW_SIZE)))), str);
        }
        Assert.assertFalse(iterator1.hasNext());
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> iterator2 = cachingStore.fetchAll(Instant.ofEpochMilli(5), Instant.ofEpochMilli(7));
        for (int i = 5; i <= 7; i++) {
            final String str = array[i];
            StreamsTestUtils.verifyWindowedKeyValue(iterator2.next(), new Windowed(CachingWindowStoreTest.bytesKey(str), new TimeWindow(i, (i + (CachingWindowStoreTest.WINDOW_SIZE)))), str);
        }
        Assert.assertFalse(iterator2.hasNext());
    }

    @Test
    public void shouldFlushEvictedItemsIntoUnderlyingStore() {
        final int added = addItemsToCache();
        // all dirty entries should have been flushed
        final org.apache.kafka.streams.state.KeyValueIterator<Bytes, byte[]> iter = underlying.fetch(Bytes.wrap("0".getBytes(StandardCharsets.UTF_8)), CachingWindowStoreTest.DEFAULT_TIMESTAMP, CachingWindowStoreTest.DEFAULT_TIMESTAMP);
        final org.apache.kafka.streams.KeyValue<Bytes, byte[]> next = iter.next();
        Assert.assertEquals(CachingWindowStoreTest.DEFAULT_TIMESTAMP, keySchema.segmentTimestamp(next.key));
        Assert.assertArrayEquals("0".getBytes(), next.value);
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals((added - 1), cache.size());
    }

    @Test
    public void shouldForwardDirtyItemsWhenFlushCalled() {
        final Windowed<String> windowedKey = new Windowed("1", new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.flush();
        Assert.assertEquals("a", cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).oldValue);
    }

    @Test
    public void shouldSetFlushListener() {
        Assert.assertTrue(cachingStore.setFlushListener(null, true));
        Assert.assertTrue(cachingStore.setFlushListener(null, false));
    }

    @Test
    public void shouldForwardOldValuesWhenEnabled() {
        cachingStore.setFlushListener(cacheListener, true);
        final Windowed<String> windowedKey = new Windowed("1", new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"));
        cachingStore.flush();
        Assert.assertEquals("b", cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).oldValue);
        cacheListener.forwarded.clear();
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("c"));
        cachingStore.flush();
        Assert.assertEquals("c", cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertEquals("b", cacheListener.forwarded.get(windowedKey).oldValue);
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), null);
        cachingStore.flush();
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertEquals("c", cacheListener.forwarded.get(windowedKey).oldValue);
        cacheListener.forwarded.clear();
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), null);
        cachingStore.flush();
        Assert.assertNull(cacheListener.forwarded.get(windowedKey));
        cacheListener.forwarded.clear();
    }

    @Test
    public void shouldForwardOldValuesWhenDisabled() {
        final Windowed<String> windowedKey = new Windowed("1", new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"));
        cachingStore.flush();
        Assert.assertEquals("b", cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).oldValue);
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("c"));
        cachingStore.flush();
        Assert.assertEquals("c", cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).oldValue);
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), null);
        cachingStore.flush();
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).newValue);
        Assert.assertNull(cacheListener.forwarded.get(windowedKey).oldValue);
        cacheListener.forwarded.clear();
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"));
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), null);
        cachingStore.flush();
        Assert.assertNull(cacheListener.forwarded.get(windowedKey));
        cacheListener.forwarded.clear();
    }

    @Test
    public void shouldForwardDirtyItemToListenerWhenEvicted() {
        final int numRecords = addItemsToCache();
        Assert.assertEquals(numRecords, cacheListener.forwarded.size());
    }

    @Test
    public void shouldTakeValueFromCacheIfSameTimestampFlushedToRocks() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"), CachingWindowStoreTest.DEFAULT_TIMESTAMP);
        cachingStore.flush();
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"), CachingWindowStoreTest.DEFAULT_TIMESTAMP);
        final WindowStoreIterator<byte[]> fetch = cachingStore.fetch(CachingWindowStoreTest.bytesKey("1"), Instant.ofEpochMilli(CachingWindowStoreTest.DEFAULT_TIMESTAMP), Instant.ofEpochMilli(CachingWindowStoreTest.DEFAULT_TIMESTAMP));
        verifyKeyValue(fetch.next(), CachingWindowStoreTest.DEFAULT_TIMESTAMP, "b");
        Assert.assertFalse(fetch.hasNext());
    }

    @Test
    public void shouldIterateAcrossWindows() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("a"), CachingWindowStoreTest.DEFAULT_TIMESTAMP);
        cachingStore.put(CachingWindowStoreTest.bytesKey("1"), CachingWindowStoreTest.bytesValue("b"), ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)));
        final WindowStoreIterator<byte[]> fetch = cachingStore.fetch(CachingWindowStoreTest.bytesKey("1"), Instant.ofEpochMilli(CachingWindowStoreTest.DEFAULT_TIMESTAMP), Instant.ofEpochMilli(((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        verifyKeyValue(fetch.next(), CachingWindowStoreTest.DEFAULT_TIMESTAMP, "a");
        verifyKeyValue(fetch.next(), ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)), "b");
        Assert.assertFalse(fetch.hasNext());
    }

    @Test
    public void shouldIterateCacheAndStore() {
        final Bytes key = Bytes.wrap("1".getBytes());
        underlying.put(WindowKeySchema.toStoreKeyBinary(key, CachingWindowStoreTest.DEFAULT_TIMESTAMP, 0), "a".getBytes());
        cachingStore.put(key, CachingWindowStoreTest.bytesValue("b"), ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)));
        final WindowStoreIterator<byte[]> fetch = cachingStore.fetch(CachingWindowStoreTest.bytesKey("1"), Instant.ofEpochMilli(CachingWindowStoreTest.DEFAULT_TIMESTAMP), Instant.ofEpochMilli(((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        verifyKeyValue(fetch.next(), CachingWindowStoreTest.DEFAULT_TIMESTAMP, "a");
        verifyKeyValue(fetch.next(), ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)), "b");
        Assert.assertFalse(fetch.hasNext());
    }

    @Test
    public void shouldIterateCacheAndStoreKeyRange() {
        final Bytes key = Bytes.wrap("1".getBytes());
        underlying.put(WindowKeySchema.toStoreKeyBinary(key, CachingWindowStoreTest.DEFAULT_TIMESTAMP, 0), "a".getBytes());
        cachingStore.put(key, CachingWindowStoreTest.bytesValue("b"), ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)));
        final org.apache.kafka.streams.state.KeyValueIterator<Windowed<Bytes>, byte[]> fetchRange = cachingStore.fetch(key, CachingWindowStoreTest.bytesKey("2"), Instant.ofEpochMilli(CachingWindowStoreTest.DEFAULT_TIMESTAMP), Instant.ofEpochMilli(((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE))));
        StreamsTestUtils.verifyWindowedKeyValue(fetchRange.next(), new Windowed(key, new TimeWindow(CachingWindowStoreTest.DEFAULT_TIMESTAMP, ((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)))), "a");
        StreamsTestUtils.verifyWindowedKeyValue(fetchRange.next(), new Windowed(key, new TimeWindow(((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)), (((CachingWindowStoreTest.DEFAULT_TIMESTAMP) + (CachingWindowStoreTest.WINDOW_SIZE)) + (CachingWindowStoreTest.WINDOW_SIZE)))), "b");
        Assert.assertFalse(fetchRange.hasNext());
    }

    @Test
    public void shouldClearNamespaceCacheOnClose() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("a"));
        Assert.assertEquals(1, cache.size());
        cachingStore.close();
        Assert.assertEquals(0, cache.size());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToFetchFromClosedCachingStore() {
        cachingStore.close();
        cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(10));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToFetchRangeFromClosedCachingStore() {
        cachingStore.close();
        cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesKey("b"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(10));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToWriteToClosedCachingStore() {
        cachingStore.close();
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("a"));
    }

    @Test
    public void shouldFetchAndIterateOverExactKeys() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0001"), 0);
        cachingStore.put(CachingWindowStoreTest.bytesKey("aa"), CachingWindowStoreTest.bytesValue("0002"), 0);
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0003"), 1);
        cachingStore.put(CachingWindowStoreTest.bytesKey("aa"), CachingWindowStoreTest.bytesValue("0004"), 1);
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0005"), CachingWindowStoreTest.SEGMENT_INTERVAL);
        final List<org.apache.kafka.streams.KeyValue<Long, byte[]>> expected = Arrays.asList(org.apache.kafka.streams.KeyValue.pair(0L, CachingWindowStoreTest.bytesValue("0001")), org.apache.kafka.streams.KeyValue.pair(1L, CachingWindowStoreTest.bytesValue("0003")), org.apache.kafka.streams.KeyValue.pair(CachingWindowStoreTest.SEGMENT_INTERVAL, CachingWindowStoreTest.bytesValue("0005")));
        final List<org.apache.kafka.streams.KeyValue<Long, byte[]>> actual = StreamsTestUtils.toList(cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE)));
        StreamsTestUtils.verifyKeyValueList(expected, actual);
    }

    @Test
    public void shouldFetchAndIterateOverKeyRange() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0001"), 0);
        cachingStore.put(CachingWindowStoreTest.bytesKey("aa"), CachingWindowStoreTest.bytesValue("0002"), 0);
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0003"), 1);
        cachingStore.put(CachingWindowStoreTest.bytesKey("aa"), CachingWindowStoreTest.bytesValue("0004"), 1);
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesValue("0005"), CachingWindowStoreTest.SEGMENT_INTERVAL);
        StreamsTestUtils.verifyKeyValueList(Arrays.asList(CachingWindowStoreTest.windowedPair("a", "0001", 0), CachingWindowStoreTest.windowedPair("a", "0003", 1), CachingWindowStoreTest.windowedPair("a", "0005", CachingWindowStoreTest.SEGMENT_INTERVAL)), StreamsTestUtils.toList(cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesKey("a"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))));
        StreamsTestUtils.verifyKeyValueList(Arrays.asList(CachingWindowStoreTest.windowedPair("aa", "0002", 0), CachingWindowStoreTest.windowedPair("aa", "0004", 1)), StreamsTestUtils.toList(cachingStore.fetch(CachingWindowStoreTest.bytesKey("aa"), CachingWindowStoreTest.bytesKey("aa"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))));
        StreamsTestUtils.verifyKeyValueList(Arrays.asList(CachingWindowStoreTest.windowedPair("a", "0001", 0), CachingWindowStoreTest.windowedPair("a", "0003", 1), CachingWindowStoreTest.windowedPair("aa", "0002", 0), CachingWindowStoreTest.windowedPair("aa", "0004", 1), CachingWindowStoreTest.windowedPair("a", "0005", CachingWindowStoreTest.SEGMENT_INTERVAL)), StreamsTestUtils.toList(cachingStore.fetch(CachingWindowStoreTest.bytesKey("a"), CachingWindowStoreTest.bytesKey("aa"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(Long.MAX_VALUE))));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnPutNullKey() {
        cachingStore.put(null, CachingWindowStoreTest.bytesValue("anyValue"));
    }

    @Test
    public void shouldNotThrowNullPointerExceptionOnPutNullValue() {
        cachingStore.put(CachingWindowStoreTest.bytesKey("a"), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFetchNullKey() {
        cachingStore.fetch(null, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnRangeNullFromKey() {
        cachingStore.fetch(null, CachingWindowStoreTest.bytesKey("anyTo"), Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnRangeNullToKey() {
        cachingStore.fetch(CachingWindowStoreTest.bytesKey("anyFrom"), null, Instant.ofEpochMilli(1L), Instant.ofEpochMilli(2L));
    }
}

