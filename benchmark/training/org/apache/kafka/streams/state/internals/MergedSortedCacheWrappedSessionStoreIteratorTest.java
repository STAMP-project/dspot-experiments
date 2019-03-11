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


import java.util.Collections;
import java.util.Iterator;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class MergedSortedCacheWrappedSessionStoreIteratorTest {
    private static final SegmentedCacheFunction SINGLE_SEGMENT_CACHE_FUNCTION = new SegmentedCacheFunction(null, (-1)) {
        @Override
        public long segmentId(final Bytes key) {
            return 0;
        }
    };

    private final Bytes storeKey = Bytes.wrap("a".getBytes());

    private final Bytes cacheKey = Bytes.wrap("b".getBytes());

    private final SessionWindow storeWindow = new SessionWindow(0, 1);

    private final Iterator<KeyValue<Windowed<Bytes>, byte[]>> storeKvs = Collections.singleton(KeyValue.pair(new Windowed(storeKey, storeWindow), storeKey.get())).iterator();

    private final SessionWindow cacheWindow = new SessionWindow(10, 20);

    private final Iterator<KeyValue<Bytes, LRUCacheEntry>> cacheKvs = Collections.singleton(KeyValue.pair(MergedSortedCacheWrappedSessionStoreIteratorTest.SINGLE_SEGMENT_CACHE_FUNCTION.cacheKey(SessionKeySchema.toBinary(new Windowed(cacheKey, cacheWindow))), new LRUCacheEntry(cacheKey.get()))).iterator();

    @Test
    public void shouldHaveNextFromStore() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(storeKvs, Collections.emptyIterator());
        Assert.assertTrue(mergeIterator.hasNext());
    }

    @Test
    public void shouldGetNextFromStore() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(storeKvs, Collections.emptyIterator());
        MatcherAssert.assertThat(mergeIterator.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed(storeKey, storeWindow), storeKey.get())));
    }

    @Test
    public void shouldPeekNextKeyFromStore() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(storeKvs, Collections.emptyIterator());
        MatcherAssert.assertThat(mergeIterator.peekNextKey(), CoreMatchers.equalTo(new Windowed(storeKey, storeWindow)));
    }

    @Test
    public void shouldHaveNextFromCache() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(Collections.emptyIterator(), cacheKvs);
        Assert.assertTrue(mergeIterator.hasNext());
    }

    @Test
    public void shouldGetNextFromCache() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(Collections.emptyIterator(), cacheKvs);
        MatcherAssert.assertThat(mergeIterator.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed(cacheKey, cacheWindow), cacheKey.get())));
    }

    @Test
    public void shouldPeekNextKeyFromCache() {
        final MergedSortedCacheSessionStoreIterator mergeIterator = createIterator(Collections.emptyIterator(), cacheKvs);
        MatcherAssert.assertThat(mergeIterator.peekNextKey(), CoreMatchers.equalTo(new Windowed(cacheKey, cacheWindow)));
    }

    @Test
    public void shouldIterateBothStoreAndCache() {
        final MergedSortedCacheSessionStoreIterator iterator = createIterator(storeKvs, cacheKvs);
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed(storeKey, storeWindow), storeKey.get())));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.equalTo(KeyValue.pair(new Windowed(cacheKey, cacheWindow), cacheKey.get())));
        Assert.assertFalse(iterator.hasNext());
    }
}

