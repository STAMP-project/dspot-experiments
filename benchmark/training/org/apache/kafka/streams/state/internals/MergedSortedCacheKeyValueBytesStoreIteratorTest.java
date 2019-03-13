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


import ThreadCache.MemoryLRUCacheBytesIterator;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.processor.internals.MockStreamsMetrics;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StateSerdes;
import org.junit.Assert;
import org.junit.Test;


public class MergedSortedCacheKeyValueBytesStoreIteratorTest {
    private final String namespace = "0.0-one";

    private final StateSerdes<byte[], byte[]> serdes = new StateSerdes("dummy", Serdes.ByteArray(), Serdes.ByteArray());

    private KeyValueStore<Bytes, byte[]> store;

    private ThreadCache cache;

    @Test
    public void shouldIterateOverRange() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }, new byte[]{ 4 }, new byte[]{ 5 }, new byte[]{ 6 }, new byte[]{ 7 }, new byte[]{ 8 }, new byte[]{ 9 }, new byte[]{ 10 }, new byte[]{ 11 } };
        for (int i = 0; i < (bytes.length); i += 2) {
            store.put(Bytes.wrap(bytes[i]), bytes[i]);
            cache.put(namespace, Bytes.wrap(bytes[(i + 1)]), new LRUCacheEntry(bytes[(i + 1)]));
        }
        final Bytes from = Bytes.wrap(new byte[]{ 2 });
        final Bytes to = Bytes.wrap(new byte[]{ 9 });
        final KeyValueIterator<Bytes, byte[]> storeIterator = new DelegatingPeekingKeyValueIterator("store", store.range(from, to));
        final ThreadCache.MemoryLRUCacheBytesIterator cacheIterator = cache.range(namespace, from, to);
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = new MergedSortedCacheKeyValueBytesStoreIterator(cacheIterator, storeIterator);
        final byte[][] values = new byte[8][];
        int index = 0;
        int bytesIndex = 2;
        while (iterator.hasNext()) {
            final byte[] value = iterator.next().value;
            values[(index++)] = value;
            Assert.assertArrayEquals(bytes[(bytesIndex++)], value);
        } 
        iterator.close();
    }

    @Test
    public void shouldSkipLargerDeletedCacheValue() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 } };
        store.put(Bytes.wrap(bytes[0]), bytes[0]);
        cache.put(namespace, Bytes.wrap(bytes[1]), new LRUCacheEntry(null));
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = createIterator();
        Assert.assertArrayEquals(bytes[0], iterator.next().key.get());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldSkipSmallerDeletedCachedValue() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 } };
        cache.put(namespace, Bytes.wrap(bytes[0]), new LRUCacheEntry(null));
        store.put(Bytes.wrap(bytes[1]), bytes[1]);
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = createIterator();
        Assert.assertArrayEquals(bytes[1], iterator.next().key.get());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldIgnoreIfDeletedInCacheButExistsInStore() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 } };
        cache.put(namespace, Bytes.wrap(bytes[0]), new LRUCacheEntry(null));
        store.put(Bytes.wrap(bytes[0]), bytes[0]);
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = createIterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldNotHaveNextIfAllCachedItemsDeleted() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 }, new byte[]{ 2 } };
        for (final byte[] aByte : bytes) {
            final Bytes aBytes = Bytes.wrap(aByte);
            store.put(aBytes, aByte);
            cache.put(namespace, aBytes, new LRUCacheEntry(null));
        }
        Assert.assertFalse(createIterator().hasNext());
    }

    @Test
    public void shouldNotHaveNextIfOnlyCacheItemsAndAllDeleted() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 }, new byte[]{ 2 } };
        for (final byte[] aByte : bytes) {
            cache.put(namespace, Bytes.wrap(aByte), new LRUCacheEntry(null));
        }
        Assert.assertFalse(createIterator().hasNext());
    }

    @Test
    public void shouldSkipAllDeletedFromCache() throws Exception {
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }, new byte[]{ 4 }, new byte[]{ 5 }, new byte[]{ 6 }, new byte[]{ 7 }, new byte[]{ 8 }, new byte[]{ 9 }, new byte[]{ 10 }, new byte[]{ 11 } };
        for (final byte[] aByte : bytes) {
            final Bytes aBytes = Bytes.wrap(aByte);
            store.put(aBytes, aByte);
            cache.put(namespace, aBytes, new LRUCacheEntry(aByte));
        }
        cache.put(namespace, Bytes.wrap(bytes[1]), new LRUCacheEntry(null));
        cache.put(namespace, Bytes.wrap(bytes[2]), new LRUCacheEntry(null));
        cache.put(namespace, Bytes.wrap(bytes[3]), new LRUCacheEntry(null));
        cache.put(namespace, Bytes.wrap(bytes[8]), new LRUCacheEntry(null));
        cache.put(namespace, Bytes.wrap(bytes[11]), new LRUCacheEntry(null));
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = createIterator();
        Assert.assertArrayEquals(bytes[0], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[4], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[5], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[6], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[7], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[9], iterator.next().key.get());
        Assert.assertArrayEquals(bytes[10], iterator.next().key.get());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldPeekNextKey() throws Exception {
        final KeyValueStore<Bytes, byte[]> kv = new InMemoryKeyValueStore("one");
        final ThreadCache cache = new ThreadCache(new LogContext("testCache "), 1000000L, new MockStreamsMetrics(new Metrics()));
        final byte[][] bytes = new byte[][]{ new byte[]{ 0 }, new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }, new byte[]{ 4 }, new byte[]{ 5 }, new byte[]{ 6 }, new byte[]{ 7 }, new byte[]{ 8 }, new byte[]{ 9 }, new byte[]{ 10 } };
        for (int i = 0; i < ((bytes.length) - 1); i += 2) {
            kv.put(Bytes.wrap(bytes[i]), bytes[i]);
            cache.put(namespace, Bytes.wrap(bytes[(i + 1)]), new LRUCacheEntry(bytes[(i + 1)]));
        }
        final Bytes from = Bytes.wrap(new byte[]{ 2 });
        final Bytes to = Bytes.wrap(new byte[]{ 9 });
        final KeyValueIterator<Bytes, byte[]> storeIterator = kv.range(from, to);
        final ThreadCache.MemoryLRUCacheBytesIterator cacheIterator = cache.range(namespace, from, to);
        final MergedSortedCacheKeyValueBytesStoreIterator iterator = new MergedSortedCacheKeyValueBytesStoreIterator(cacheIterator, storeIterator);
        final byte[][] values = new byte[8][];
        int index = 0;
        int bytesIndex = 2;
        while (iterator.hasNext()) {
            final byte[] keys = iterator.peekNextKey().get();
            values[(index++)] = keys;
            Assert.assertArrayEquals(bytes[(bytesIndex++)], keys);
            iterator.next();
        } 
        iterator.close();
    }
}

