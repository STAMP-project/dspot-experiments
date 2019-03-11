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


import NamedCache.LRUNode;
import ThreadCache.DirtyEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.metrics.JmxReporter;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.internals.metrics.StreamsMetricsImpl;
import org.apache.kafka.test.StreamsTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class NamedCacheTest {
    private final Headers headers = new org.apache.kafka.common.header.internals.RecordHeaders(new Header[]{ new RecordHeader("key", "value".getBytes()) });

    private NamedCache cache;

    private Metrics innerMetrics;

    private StreamsMetricsImpl metrics;

    private final String taskIDString = "0.0";

    private final String underlyingStoreName = "storeName";

    @Test
    public void shouldKeepTrackOfMostRecentlyAndLeastRecentlyUsed() throws IOException {
        final List<KeyValue<String, String>> toInsert = Arrays.asList(new KeyValue("K1", "V1"), new KeyValue("K2", "V2"), new KeyValue("K3", "V3"), new KeyValue("K4", "V4"), new KeyValue("K5", "V5"));
        for (int i = 0; i < (toInsert.size()); i++) {
            final byte[] key = toInsert.get(i).key.getBytes();
            final byte[] value = toInsert.get(i).value.getBytes();
            cache.put(Bytes.wrap(key), new LRUCacheEntry(value, null, true, 1, 1, 1, ""));
            final LRUCacheEntry head = cache.first();
            final LRUCacheEntry tail = cache.last();
            Assert.assertEquals(new String(head.value()), toInsert.get(i).value);
            Assert.assertEquals(new String(tail.value()), toInsert.get(0).value);
            Assert.assertEquals(cache.flushes(), 0);
            Assert.assertEquals(cache.hits(), 0);
            Assert.assertEquals(cache.misses(), 0);
            Assert.assertEquals(cache.overwrites(), 0);
        }
    }

    @Test
    public void testMetrics() {
        final Map<String, String> metricTags = new LinkedHashMap<>();
        metricTags.put("record-cache-id", underlyingStoreName);
        metricTags.put("task-id", taskIDString);
        metricTags.put("client-id", "test");
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-avg", "stream-record-cache-metrics", metricTags);
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-min", "stream-record-cache-metrics", metricTags);
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-max", "stream-record-cache-metrics", metricTags);
        // test "all"
        metricTags.put("record-cache-id", "all");
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-avg", "stream-record-cache-metrics", metricTags);
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-min", "stream-record-cache-metrics", metricTags);
        StreamsTestUtils.getMetricByNameFilterByTags(metrics.metrics(), "hitRatio-max", "stream-record-cache-metrics", metricTags);
        final JmxReporter reporter = new JmxReporter("kafka.streams");
        innerMetrics.addReporter(reporter);
        Assert.assertTrue(reporter.containsMbean(String.format("kafka.streams:type=stream-record-cache-metrics,client-id=test,task-id=%s,record-cache-id=%s", taskIDString, underlyingStoreName)));
        Assert.assertTrue(reporter.containsMbean(String.format("kafka.streams:type=stream-record-cache-metrics,client-id=test,task-id=%s,record-cache-id=%s", taskIDString, "all")));
    }

    @Test
    public void shouldKeepTrackOfSize() {
        final LRUCacheEntry value = new LRUCacheEntry(new byte[]{ 0 });
        cache.put(Bytes.wrap(new byte[]{ 0 }), value);
        cache.put(Bytes.wrap(new byte[]{ 1 }), value);
        cache.put(Bytes.wrap(new byte[]{ 2 }), value);
        final long size = cache.sizeInBytes();
        // 1 byte key + 24 bytes overhead
        Assert.assertEquals((((value.size()) + 25) * 3), size);
    }

    @Test
    public void shouldPutGet() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 11 }));
        cache.put(Bytes.wrap(new byte[]{ 2 }), new LRUCacheEntry(new byte[]{ 12 }));
        Assert.assertArrayEquals(new byte[]{ 10 }, cache.get(Bytes.wrap(new byte[]{ 0 })).value());
        Assert.assertArrayEquals(new byte[]{ 11 }, cache.get(Bytes.wrap(new byte[]{ 1 })).value());
        Assert.assertArrayEquals(new byte[]{ 12 }, cache.get(Bytes.wrap(new byte[]{ 2 })).value());
        Assert.assertEquals(cache.hits(), 3);
    }

    @Test
    public void shouldPutIfAbsent() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }));
        cache.putIfAbsent(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 20 }));
        cache.putIfAbsent(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 30 }));
        Assert.assertArrayEquals(new byte[]{ 10 }, cache.get(Bytes.wrap(new byte[]{ 0 })).value());
        Assert.assertArrayEquals(new byte[]{ 30 }, cache.get(Bytes.wrap(new byte[]{ 1 })).value());
    }

    @Test
    public void shouldDeleteAndUpdateSize() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }));
        final LRUCacheEntry deleted = cache.delete(Bytes.wrap(new byte[]{ 0 }));
        Assert.assertArrayEquals(new byte[]{ 10 }, deleted.value());
        Assert.assertEquals(0, cache.sizeInBytes());
    }

    @Test
    public void shouldPutAll() {
        cache.putAll(Arrays.asList(KeyValue.pair(new byte[]{ 0 }, new LRUCacheEntry(new byte[]{ 0 })), KeyValue.pair(new byte[]{ 1 }, new LRUCacheEntry(new byte[]{ 1 })), KeyValue.pair(new byte[]{ 2 }, new LRUCacheEntry(new byte[]{ 2 }))));
        Assert.assertArrayEquals(new byte[]{ 0 }, cache.get(Bytes.wrap(new byte[]{ 0 })).value());
        Assert.assertArrayEquals(new byte[]{ 1 }, cache.get(Bytes.wrap(new byte[]{ 1 })).value());
        Assert.assertArrayEquals(new byte[]{ 2 }, cache.get(Bytes.wrap(new byte[]{ 2 })).value());
    }

    @Test
    public void shouldOverwriteAll() {
        cache.putAll(Arrays.asList(KeyValue.pair(new byte[]{ 0 }, new LRUCacheEntry(new byte[]{ 0 })), KeyValue.pair(new byte[]{ 0 }, new LRUCacheEntry(new byte[]{ 1 })), KeyValue.pair(new byte[]{ 0 }, new LRUCacheEntry(new byte[]{ 2 }))));
        Assert.assertArrayEquals(new byte[]{ 2 }, cache.get(Bytes.wrap(new byte[]{ 0 })).value());
        Assert.assertEquals(cache.overwrites(), 2);
    }

    @Test
    public void shouldEvictEldestEntry() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 20 }));
        cache.put(Bytes.wrap(new byte[]{ 2 }), new LRUCacheEntry(new byte[]{ 30 }));
        cache.evict();
        Assert.assertNull(cache.get(Bytes.wrap(new byte[]{ 0 })));
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void shouldFlushDirtEntriesOnEviction() {
        final List<ThreadCache.DirtyEntry> flushed = new ArrayList<>();
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }, headers, true, 0, 0, 0, ""));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 20 }));
        cache.put(Bytes.wrap(new byte[]{ 2 }), new LRUCacheEntry(new byte[]{ 30 }, headers, true, 0, 0, 0, ""));
        cache.setListener(new ThreadCache.DirtyEntryFlushListener() {
            @Override
            public void apply(final List<ThreadCache.DirtyEntry> dirty) {
                flushed.addAll(dirty);
            }
        });
        cache.evict();
        Assert.assertEquals(2, flushed.size());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 0 }), flushed.get(0).key());
        Assert.assertEquals(headers, flushed.get(0).entry().context().headers());
        Assert.assertArrayEquals(new byte[]{ 10 }, flushed.get(0).newValue());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 2 }), flushed.get(1).key());
        Assert.assertArrayEquals(new byte[]{ 30 }, flushed.get(1).newValue());
        Assert.assertEquals(cache.flushes(), 1);
    }

    @Test
    public void shouldGetRangeIteratorOverKeys() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }, headers, true, 0, 0, 0, ""));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 20 }));
        cache.put(Bytes.wrap(new byte[]{ 2 }), new LRUCacheEntry(new byte[]{ 30 }, null, true, 0, 0, 0, ""));
        final Iterator<Bytes> iterator = cache.keyRange(Bytes.wrap(new byte[]{ 1 }), Bytes.wrap(new byte[]{ 2 }));
        Assert.assertEquals(Bytes.wrap(new byte[]{ 1 }), iterator.next());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 2 }), iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldGetIteratorOverAllKeys() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }, headers, true, 0, 0, 0, ""));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 20 }));
        cache.put(Bytes.wrap(new byte[]{ 2 }), new LRUCacheEntry(new byte[]{ 30 }, null, true, 0, 0, 0, ""));
        final Iterator<Bytes> iterator = cache.allKeys();
        Assert.assertEquals(Bytes.wrap(new byte[]{ 0 }), iterator.next());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 1 }), iterator.next());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 2 }), iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldNotThrowNullPointerWhenCacheIsEmptyAndEvictionCalled() {
        cache.evict();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionWhenTryingToOverwriteDirtyEntryWithCleanEntry() {
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }, headers, true, 0, 0, 0, ""));
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(new byte[]{ 10 }, null, false, 0, 0, 0, ""));
    }

    @Test
    public void shouldRemoveDeletedValuesOnFlush() {
        cache.setListener(new ThreadCache.DirtyEntryFlushListener() {
            @Override
            public void apply(final List<ThreadCache.DirtyEntry> dirty) {
                // no-op
            }
        });
        cache.put(Bytes.wrap(new byte[]{ 0 }), new LRUCacheEntry(null, headers, true, 0, 0, 0, ""));
        cache.put(Bytes.wrap(new byte[]{ 1 }), new LRUCacheEntry(new byte[]{ 20 }, null, true, 0, 0, 0, ""));
        cache.flush();
        Assert.assertEquals(1, cache.size());
        Assert.assertNotNull(cache.get(Bytes.wrap(new byte[]{ 1 })));
    }

    @Test
    public void shouldBeReentrantAndNotBreakLRU() {
        final LRUCacheEntry dirty = new LRUCacheEntry(new byte[]{ 3 }, null, true, 0, 0, 0, "");
        final LRUCacheEntry clean = new LRUCacheEntry(new byte[]{ 3 });
        cache.put(Bytes.wrap(new byte[]{ 0 }), dirty);
        cache.put(Bytes.wrap(new byte[]{ 1 }), clean);
        cache.put(Bytes.wrap(new byte[]{ 2 }), clean);
        Assert.assertEquals((3 * (cache.head().size())), cache.sizeInBytes());
        cache.setListener(new ThreadCache.DirtyEntryFlushListener() {
            @Override
            public void apply(final List<ThreadCache.DirtyEntry> dirty) {
                cache.put(Bytes.wrap(new byte[]{ 3 }), clean);
                // evict key 1
                cache.evict();
                // evict key 2
                cache.evict();
            }
        });
        Assert.assertEquals((3 * (cache.head().size())), cache.sizeInBytes());
        // Evict key 0
        cache.evict();
        final Bytes entryFour = Bytes.wrap(new byte[]{ 4 });
        cache.put(entryFour, dirty);
        // check that the LRU is still correct
        final NamedCache.LRUNode head = cache.head();
        final NamedCache.LRUNode tail = cache.tail();
        Assert.assertEquals(2, cache.size());
        Assert.assertEquals((2 * (head.size())), cache.sizeInBytes());
        // dirty should be the newest
        Assert.assertEquals(entryFour, head.key());
        Assert.assertEquals(Bytes.wrap(new byte[]{ 3 }), tail.key());
        Assert.assertSame(tail, head.next());
        Assert.assertNull(head.previous());
        Assert.assertSame(head, tail.previous());
        Assert.assertNull(tail.next());
        // evict key 3
        cache.evict();
        Assert.assertSame(cache.head(), cache.tail());
        Assert.assertEquals(entryFour, cache.head().key());
        Assert.assertNull(cache.head().next());
        Assert.assertNull(cache.head().previous());
    }

    @Test
    public void shouldNotThrowIllegalArgumentAfterEvictingDirtyRecordAndThenPuttingNewRecordWithSameKey() {
        final LRUCacheEntry dirty = new LRUCacheEntry(new byte[]{ 3 }, null, true, 0, 0, 0, "");
        final LRUCacheEntry clean = new LRUCacheEntry(new byte[]{ 3 });
        final Bytes key = Bytes.wrap(new byte[]{ 3 });
        cache.setListener(new ThreadCache.DirtyEntryFlushListener() {
            @Override
            public void apply(final List<ThreadCache.DirtyEntry> dirty) {
                cache.put(key, clean);
            }
        });
        cache.put(key, dirty);
        cache.evict();
    }

    @Test
    public void shouldReturnNullIfKeyIsNull() {
        Assert.assertNull(cache.get(null));
    }
}

