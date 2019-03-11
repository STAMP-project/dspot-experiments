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


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("PointlessArithmeticExpression")
public class CachingSessionStoreTest {
    private static final int MAX_CACHE_SIZE_BYTES = 600;

    private static final Long DEFAULT_TIMESTAMP = 10L;

    private static final long SEGMENT_INTERVAL = 100L;

    private final Bytes keyA = Bytes.wrap("a".getBytes());

    private final Bytes keyAA = Bytes.wrap("aa".getBytes());

    private final Bytes keyB = Bytes.wrap("b".getBytes());

    private CachingSessionStore cachingStore;

    private ThreadCache cache;

    @Test
    public void shouldPutFetchFromCache() {
        cachingStore.put(new Windowed(keyA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyAA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyB, new SessionWindow(0, 0)), "1".getBytes());
        Assert.assertEquals(3, cache.size());
        final KeyValueIterator<Windowed<Bytes>, byte[]> a = cachingStore.findSessions(keyA, 0, 0);
        final KeyValueIterator<Windowed<Bytes>, byte[]> b = cachingStore.findSessions(keyB, 0, 0);
        StreamsTestUtils.verifyWindowedKeyValue(a.next(), new Windowed(keyA, new SessionWindow(0, 0)), "1");
        StreamsTestUtils.verifyWindowedKeyValue(b.next(), new Windowed(keyB, new SessionWindow(0, 0)), "1");
        Assert.assertFalse(a.hasNext());
        Assert.assertFalse(b.hasNext());
    }

    @Test
    public void shouldPutFetchAllKeysFromCache() {
        cachingStore.put(new Windowed(keyA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyAA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyB, new SessionWindow(0, 0)), "1".getBytes());
        Assert.assertEquals(3, cache.size());
        final KeyValueIterator<Windowed<Bytes>, byte[]> all = cachingStore.findSessions(keyA, keyB, 0, 0);
        StreamsTestUtils.verifyWindowedKeyValue(all.next(), new Windowed(keyA, new SessionWindow(0, 0)), "1");
        StreamsTestUtils.verifyWindowedKeyValue(all.next(), new Windowed(keyAA, new SessionWindow(0, 0)), "1");
        StreamsTestUtils.verifyWindowedKeyValue(all.next(), new Windowed(keyB, new SessionWindow(0, 0)), "1");
        Assert.assertFalse(all.hasNext());
    }

    @Test
    public void shouldPutFetchRangeFromCache() {
        cachingStore.put(new Windowed(keyA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyAA, new SessionWindow(0, 0)), "1".getBytes());
        cachingStore.put(new Windowed(keyB, new SessionWindow(0, 0)), "1".getBytes());
        Assert.assertEquals(3, cache.size());
        final KeyValueIterator<Windowed<Bytes>, byte[]> some = cachingStore.findSessions(keyAA, keyB, 0, 0);
        StreamsTestUtils.verifyWindowedKeyValue(some.next(), new Windowed(keyAA, new SessionWindow(0, 0)), "1");
        StreamsTestUtils.verifyWindowedKeyValue(some.next(), new Windowed(keyB, new SessionWindow(0, 0)), "1");
        Assert.assertFalse(some.hasNext());
    }

    @Test
    public void shouldFetchAllSessionsWithSameRecordKey() {
        final List<KeyValue<Windowed<Bytes>, byte[]>> expected = Arrays.asList(KeyValue.pair(new Windowed(keyA, new SessionWindow(0, 0)), "1".getBytes()), KeyValue.pair(new Windowed(keyA, new SessionWindow(10, 10)), "2".getBytes()), KeyValue.pair(new Windowed(keyA, new SessionWindow(100, 100)), "3".getBytes()), KeyValue.pair(new Windowed(keyA, new SessionWindow(1000, 1000)), "4".getBytes()));
        for (final KeyValue<Windowed<Bytes>, byte[]> kv : expected) {
            cachingStore.put(kv.key, kv.value);
        }
        // add one that shouldn't appear in the results
        cachingStore.put(new Windowed(keyAA, new SessionWindow(0, 0)), "5".getBytes());
        final List<KeyValue<Windowed<Bytes>, byte[]>> results = StreamsTestUtils.toList(cachingStore.fetch(keyA));
        StreamsTestUtils.verifyKeyValueList(expected, results);
    }

    @Test
    public void shouldFlushItemsToStoreOnEviction() {
        final List<KeyValue<Windowed<Bytes>, byte[]>> added = addSessionsUntilOverflow("a", "b", "c", "d");
        Assert.assertEquals(((added.size()) - 1), cache.size());
        final KeyValueIterator<Windowed<Bytes>, byte[]> iterator = cachingStore.findSessions(added.get(0).key.key(), 0, 0);
        final KeyValue<Windowed<Bytes>, byte[]> next = iterator.next();
        Assert.assertEquals(added.get(0).key, next.key);
        Assert.assertArrayEquals(added.get(0).value, next.value);
    }

    @Test
    public void shouldQueryItemsInCacheAndStore() {
        final List<KeyValue<Windowed<Bytes>, byte[]>> added = addSessionsUntilOverflow("a");
        final KeyValueIterator<Windowed<Bytes>, byte[]> iterator = cachingStore.findSessions(Bytes.wrap("a".getBytes(StandardCharsets.UTF_8)), 0, ((added.size()) * 10));
        final List<KeyValue<Windowed<Bytes>, byte[]>> actual = StreamsTestUtils.toList(iterator);
        StreamsTestUtils.verifyKeyValueList(added, actual);
    }

    @Test
    public void shouldRemove() {
        final Windowed<Bytes> a = new Windowed(keyA, new SessionWindow(0, 0));
        final Windowed<Bytes> b = new Windowed(keyB, new SessionWindow(0, 0));
        cachingStore.put(a, "2".getBytes());
        cachingStore.put(b, "2".getBytes());
        cachingStore.remove(a);
        final KeyValueIterator<Windowed<Bytes>, byte[]> rangeIter = cachingStore.findSessions(keyA, 0, 0);
        Assert.assertFalse(rangeIter.hasNext());
        Assert.assertNull(cachingStore.fetchSession(keyA, 0, 0));
        MatcherAssert.assertThat(cachingStore.fetchSession(keyB, 0, 0), Matchers.equalTo("2".getBytes()));
    }

    @Test
    public void shouldFetchCorrectlyAcrossSegments() {
        final Windowed<Bytes> a1 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0)));
        final Windowed<Bytes> a2 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 1), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 1)));
        final Windowed<Bytes> a3 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2)));
        cachingStore.put(a1, "1".getBytes());
        cachingStore.put(a2, "2".getBytes());
        cachingStore.put(a3, "3".getBytes());
        cachingStore.flush();
        final KeyValueIterator<Windowed<Bytes>, byte[]> results = cachingStore.findSessions(keyA, 0, ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2));
        Assert.assertEquals(a1, results.next().key);
        Assert.assertEquals(a2, results.next().key);
        Assert.assertEquals(a3, results.next().key);
        Assert.assertFalse(results.hasNext());
    }

    @Test
    public void shouldFetchRangeCorrectlyAcrossSegments() {
        final Windowed<Bytes> a1 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0)));
        final Windowed<Bytes> aa1 = new Windowed(keyAA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 0)));
        final Windowed<Bytes> a2 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 1), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 1)));
        final Windowed<Bytes> a3 = new Windowed(keyA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2)));
        final Windowed<Bytes> aa3 = new Windowed(keyAA, new SessionWindow(((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2), ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2)));
        cachingStore.put(a1, "1".getBytes());
        cachingStore.put(aa1, "1".getBytes());
        cachingStore.put(a2, "2".getBytes());
        cachingStore.put(a3, "3".getBytes());
        cachingStore.put(aa3, "3".getBytes());
        final KeyValueIterator<Windowed<Bytes>, byte[]> rangeResults = cachingStore.findSessions(keyA, keyAA, 0, ((CachingSessionStoreTest.SEGMENT_INTERVAL) * 2));
        final Set<Windowed<Bytes>> keys = new HashSet<>();
        while (rangeResults.hasNext()) {
            keys.add(rangeResults.next().key);
        } 
        rangeResults.close();
        Assert.assertEquals(mkSet(a1, a2, a3, aa1, aa3), keys);
    }

    @Test
    public void shouldSetFlushListener() {
        Assert.assertTrue(cachingStore.setFlushListener(null, true));
        Assert.assertTrue(cachingStore.setFlushListener(null, false));
    }

    @Test
    public void shouldForwardChangedValuesDuringFlush() {
        final Windowed<Bytes> a = new Windowed(keyA, new SessionWindow(2, 4));
        final Windowed<Bytes> b = new Windowed(keyA, new SessionWindow(1, 2));
        final Windowed<String> aDeserialized = new Windowed("a", new SessionWindow(2, 4));
        final Windowed<String> bDeserialized = new Windowed("a", new SessionWindow(1, 2));
        final CachingKeyValueStoreTest.CacheFlushListenerStub<Windowed<String>, String> flushListener = new CachingKeyValueStoreTest.CacheFlushListenerStub(new org.apache.kafka.streams.kstream.SessionWindowedDeserializer(new StringDeserializer()), new StringDeserializer());
        cachingStore.setFlushListener(flushListener, true);
        cachingStore.put(b, "1".getBytes());
        cachingStore.flush();
        Assert.assertEquals(Collections.singletonMap(bDeserialized, new org.apache.kafka.streams.kstream.internals.Change("1", null)), flushListener.forwarded);
        flushListener.forwarded.clear();
        cachingStore.put(a, "1".getBytes());
        cachingStore.flush();
        Assert.assertEquals(Collections.singletonMap(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change("1", null)), flushListener.forwarded);
        flushListener.forwarded.clear();
        cachingStore.put(a, "2".getBytes());
        cachingStore.flush();
        Assert.assertEquals(Collections.singletonMap(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change("2", "1")), flushListener.forwarded);
        flushListener.forwarded.clear();
        cachingStore.remove(a);
        cachingStore.flush();
        Assert.assertEquals(Collections.singletonMap(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change(null, "2")), flushListener.forwarded);
        flushListener.forwarded.clear();
        cachingStore.put(a, "1".getBytes());
        cachingStore.put(a, "2".getBytes());
        cachingStore.remove(a);
        cachingStore.flush();
        Assert.assertEquals(Collections.emptyMap(), flushListener.forwarded);
        flushListener.forwarded.clear();
    }

    @Test
    public void shouldNotForwardChangedValuesDuringFlushWhenSendOldValuesDisabled() {
        final Windowed<Bytes> a = new Windowed(keyA, new SessionWindow(0, 0));
        final Windowed<String> aDeserialized = new Windowed("a", new SessionWindow(0, 0));
        final CachingKeyValueStoreTest.CacheFlushListenerStub<Windowed<String>, String> flushListener = new CachingKeyValueStoreTest.CacheFlushListenerStub(new org.apache.kafka.streams.kstream.SessionWindowedDeserializer(new StringDeserializer()), new StringDeserializer());
        cachingStore.setFlushListener(flushListener, false);
        cachingStore.put(a, "1".getBytes());
        cachingStore.flush();
        cachingStore.put(a, "2".getBytes());
        cachingStore.flush();
        cachingStore.remove(a);
        cachingStore.flush();
        Assert.assertEquals(mkMap(mkEntry(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change("1", null)), mkEntry(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change("2", null)), mkEntry(aDeserialized, new org.apache.kafka.streams.kstream.internals.Change(null, null))), flushListener.forwarded);
        flushListener.forwarded.clear();
        cachingStore.put(a, "1".getBytes());
        cachingStore.put(a, "2".getBytes());
        cachingStore.remove(a);
        cachingStore.flush();
        Assert.assertEquals(Collections.emptyMap(), flushListener.forwarded);
        flushListener.forwarded.clear();
    }

    @Test
    public void shouldClearNamespaceCacheOnClose() {
        final Windowed<Bytes> a1 = new Windowed(keyA, new SessionWindow(0, 0));
        cachingStore.put(a1, "1".getBytes());
        Assert.assertEquals(1, cache.size());
        cachingStore.close();
        Assert.assertEquals(0, cache.size());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToFetchFromClosedCachingStore() {
        cachingStore.close();
        cachingStore.fetch(keyA);
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToFindMergeSessionFromClosedCachingStore() {
        cachingStore.close();
        cachingStore.findSessions(keyA, 0, Long.MAX_VALUE);
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToRemoveFromClosedCachingStore() {
        cachingStore.close();
        cachingStore.remove(new Windowed(keyA, new SessionWindow(0, 0)));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowIfTryingToPutIntoClosedCachingStore() {
        cachingStore.close();
        cachingStore.put(new Windowed(keyA, new SessionWindow(0, 0)), "1".getBytes());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFindSessionsNullKey() {
        cachingStore.findSessions(null, 1L, 2L);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFindSessionsNullFromKey() {
        cachingStore.findSessions(null, keyA, 1L, 2L);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFindSessionsNullToKey() {
        cachingStore.findSessions(keyA, null, 1L, 2L);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFetchNullFromKey() {
        cachingStore.fetch(null, keyA);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFetchNullToKey() {
        cachingStore.fetch(keyA, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnFetchNullKey() {
        cachingStore.fetch(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnRemoveNullKey() {
        cachingStore.remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnPutNullKey() {
        cachingStore.put(null, "1".getBytes());
    }
}

