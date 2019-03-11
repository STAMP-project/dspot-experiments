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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.internals.Change;
import org.apache.kafka.streams.processor.internals.ProcessorRecordContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class CachingKeyValueStoreTest extends AbstractKeyValueStoreTest {
    private final int maxCacheSizeBytes = 150;

    private InternalMockProcessorContext context;

    private CachingKeyValueStore store;

    private InMemoryKeyValueStore underlyingStore;

    private ThreadCache cache;

    private CachingKeyValueStoreTest.CacheFlushListenerStub<String, String> cacheFlushListener;

    private String topic;

    @Test
    public void shouldSetFlushListener() {
        Assert.assertTrue(store.setFlushListener(null, true));
        Assert.assertTrue(store.setFlushListener(null, false));
    }

    @Test
    public void shouldAvoidFlushingDeletionsWithoutDirtyKeys() {
        final int added = addItemsToCache();
        // all dirty entries should have been flushed
        Assert.assertEquals(added, underlyingStore.approximateNumEntries());
        Assert.assertEquals(added, cacheFlushListener.forwarded.size());
        store.put(bytesKey("key"), bytesValue("value"));
        Assert.assertEquals(added, underlyingStore.approximateNumEntries());
        Assert.assertEquals(added, cacheFlushListener.forwarded.size());
        store.put(bytesKey("key"), null);
        store.flush();
        Assert.assertEquals(added, underlyingStore.approximateNumEntries());
        Assert.assertEquals(added, cacheFlushListener.forwarded.size());
    }

    @Test
    public void shouldCloseAfterErrorWithFlush() {
        try {
            cache = EasyMock.niceMock(ThreadCache.class);
            context = new InternalMockProcessorContext(null, null, null, null, cache);
            context.setRecordContext(new ProcessorRecordContext(10, 0, 0, topic, null));
            store.init(context, null);
            cache.flush("0_0-store");
            EasyMock.expectLastCall().andThrow(new NullPointerException("Simulating an error on flush"));
            EasyMock.replay(cache);
            store.close();
        } catch (final NullPointerException npe) {
            Assert.assertFalse(underlyingStore.isOpen());
        }
    }

    @Test
    public void shouldPutGetToFromCache() {
        store.put(bytesKey("key"), bytesValue("value"));
        store.put(bytesKey("key2"), bytesValue("value2"));
        MatcherAssert.assertThat(store.get(bytesKey("key")), CoreMatchers.equalTo(bytesValue("value")));
        MatcherAssert.assertThat(store.get(bytesKey("key2")), CoreMatchers.equalTo(bytesValue("value2")));
        // nothing evicted so underlying store should be empty
        Assert.assertEquals(2, cache.size());
        Assert.assertEquals(0, underlyingStore.approximateNumEntries());
    }

    @Test
    public void shouldFlushEvictedItemsIntoUnderlyingStore() {
        final int added = addItemsToCache();
        // all dirty entries should have been flushed
        Assert.assertEquals(added, underlyingStore.approximateNumEntries());
        Assert.assertEquals(added, store.approximateNumEntries());
        Assert.assertNotNull(underlyingStore.get(Bytes.wrap("0".getBytes())));
    }

    @Test
    public void shouldForwardDirtyItemToListenerWhenEvicted() {
        final int numRecords = addItemsToCache();
        Assert.assertEquals(numRecords, cacheFlushListener.forwarded.size());
    }

    @Test
    public void shouldForwardDirtyItemsWhenFlushCalled() {
        store.put(bytesKey("1"), bytesValue("a"));
        store.flush();
        Assert.assertEquals("a", cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertNull(cacheFlushListener.forwarded.get("1").oldValue);
    }

    @Test
    public void shouldForwardOldValuesWhenEnabled() {
        store.setFlushListener(cacheFlushListener, true);
        store.put(bytesKey("1"), bytesValue("a"));
        store.flush();
        Assert.assertEquals("a", cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertNull(cacheFlushListener.forwarded.get("1").oldValue);
        store.put(bytesKey("1"), bytesValue("b"));
        store.put(bytesKey("1"), bytesValue("c"));
        store.flush();
        Assert.assertEquals("c", cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertEquals("a", cacheFlushListener.forwarded.get("1").oldValue);
        store.put(bytesKey("1"), null);
        store.flush();
        Assert.assertNull(cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertEquals("c", cacheFlushListener.forwarded.get("1").oldValue);
        cacheFlushListener.forwarded.clear();
        store.put(bytesKey("1"), bytesValue("a"));
        store.put(bytesKey("1"), bytesValue("b"));
        store.put(bytesKey("1"), null);
        store.flush();
        Assert.assertNull(cacheFlushListener.forwarded.get("1"));
        cacheFlushListener.forwarded.clear();
    }

    @Test
    public void shouldNotForwardOldValuesWhenDisabled() {
        store.put(bytesKey("1"), bytesValue("a"));
        store.flush();
        Assert.assertEquals("a", cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertNull(cacheFlushListener.forwarded.get("1").oldValue);
        store.put(bytesKey("1"), bytesValue("b"));
        store.flush();
        Assert.assertEquals("b", cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertNull(cacheFlushListener.forwarded.get("1").oldValue);
        store.put(bytesKey("1"), null);
        store.flush();
        Assert.assertNull(cacheFlushListener.forwarded.get("1").newValue);
        Assert.assertNull(cacheFlushListener.forwarded.get("1").oldValue);
        cacheFlushListener.forwarded.clear();
        store.put(bytesKey("1"), bytesValue("a"));
        store.put(bytesKey("1"), bytesValue("b"));
        store.put(bytesKey("1"), null);
        store.flush();
        Assert.assertNull(cacheFlushListener.forwarded.get("1"));
        cacheFlushListener.forwarded.clear();
    }

    @Test
    public void shouldIterateAllStoredItems() {
        final int items = addItemsToCache();
        final KeyValueIterator<Bytes, byte[]> all = store.all();
        final List<Bytes> results = new ArrayList<>();
        while (all.hasNext()) {
            results.add(all.next().key);
        } 
        Assert.assertEquals(items, results.size());
    }

    @Test
    public void shouldIterateOverRange() {
        final int items = addItemsToCache();
        final KeyValueIterator<Bytes, byte[]> range = store.range(bytesKey(String.valueOf(0)), bytesKey(String.valueOf(items)));
        final List<Bytes> results = new ArrayList<>();
        while (range.hasNext()) {
            results.add(range.next().key);
        } 
        Assert.assertEquals(items, results.size());
    }

    @Test
    public void shouldDeleteItemsFromCache() {
        store.put(bytesKey("a"), bytesValue("a"));
        store.delete(bytesKey("a"));
        Assert.assertNull(store.get(bytesKey("a")));
        Assert.assertFalse(store.range(bytesKey("a"), bytesKey("b")).hasNext());
        Assert.assertFalse(store.all().hasNext());
    }

    @Test
    public void shouldNotShowItemsDeletedFromCacheButFlushedToStoreBeforeDelete() {
        store.put(bytesKey("a"), bytesValue("a"));
        store.flush();
        store.delete(bytesKey("a"));
        Assert.assertNull(store.get(bytesKey("a")));
        Assert.assertFalse(store.range(bytesKey("a"), bytesKey("b")).hasNext());
        Assert.assertFalse(store.all().hasNext());
    }

    @Test
    public void shouldClearNamespaceCacheOnClose() {
        store.put(bytesKey("a"), bytesValue("a"));
        Assert.assertEquals(1, cache.size());
        store.close();
        Assert.assertEquals(0, cache.size());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToGetFromClosedCachingStore() {
        store.close();
        store.get(bytesKey("a"));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToWriteToClosedCachingStore() {
        store.close();
        store.put(bytesKey("a"), bytesValue("a"));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDoRangeQueryOnClosedCachingStore() {
        store.close();
        store.range(bytesKey("a"), bytesKey("b"));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDoAllQueryOnClosedCachingStore() {
        store.close();
        store.all();
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDoGetApproxSizeOnClosedCachingStore() {
        store.close();
        store.approximateNumEntries();
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDoPutAllClosedCachingStore() {
        store.close();
        store.putAll(Collections.singletonList(KeyValue.pair(bytesKey("a"), bytesValue("a"))));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDoPutIfAbsentClosedCachingStore() {
        store.close();
        store.putIfAbsent(bytesKey("b"), bytesValue("c"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnPutWithNullKey() {
        store.put(null, bytesValue("c"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnPutIfAbsentWithNullKey() {
        store.putIfAbsent(null, bytesValue("c"));
    }

    @Test
    public void shouldThrowNullPointerExceptionOnPutAllWithNullKey() {
        final List<KeyValue<Bytes, byte[]>> entries = new ArrayList<>();
        entries.add(new KeyValue(null, bytesValue("a")));
        try {
            store.putAll(entries);
            Assert.fail("Should have thrown NullPointerException while putAll null key");
        } catch (final NullPointerException expected) {
        }
    }

    @Test
    public void shouldPutIfAbsent() {
        store.putIfAbsent(bytesKey("b"), bytesValue("2"));
        MatcherAssert.assertThat(store.get(bytesKey("b")), CoreMatchers.equalTo(bytesValue("2")));
        store.putIfAbsent(bytesKey("b"), bytesValue("3"));
        MatcherAssert.assertThat(store.get(bytesKey("b")), CoreMatchers.equalTo(bytesValue("2")));
    }

    @Test
    public void shouldPutAll() {
        final List<KeyValue<Bytes, byte[]>> entries = new ArrayList<>();
        entries.add(new KeyValue(bytesKey("a"), bytesValue("1")));
        entries.add(new KeyValue(bytesKey("b"), bytesValue("2")));
        store.putAll(entries);
        MatcherAssert.assertThat(store.get(bytesKey("a")), CoreMatchers.equalTo(bytesValue("1")));
        MatcherAssert.assertThat(store.get(bytesKey("b")), CoreMatchers.equalTo(bytesValue("2")));
    }

    @Test
    public void shouldReturnUnderlying() {
        Assert.assertEquals(underlyingStore, store.wrapped());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToDeleteFromClosedCachingStore() {
        store.close();
        store.delete(bytesKey("key"));
    }

    public static class CacheFlushListenerStub<K, V> implements CacheFlushListener<byte[], byte[]> {
        final Deserializer<K> keyDeserializer;

        final Deserializer<V> valueDesializer;

        final Map<K, Change<V>> forwarded = new HashMap<>();

        CacheFlushListenerStub(final Deserializer<K> keyDeserializer, final Deserializer<V> valueDesializer) {
            this.keyDeserializer = keyDeserializer;
            this.valueDesializer = valueDesializer;
        }

        @Override
        public void apply(final byte[] key, final byte[] newValue, final byte[] oldValue, final long timestamp) {
            forwarded.put(keyDeserializer.deserialize(null, key), new Change(valueDesializer.deserialize(null, newValue), valueDesializer.deserialize(null, oldValue)));
        }
    }
}

