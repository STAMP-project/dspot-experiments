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


import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class FilteredCacheIteratorTest {
    private static final CacheFunction IDENTITY_FUNCTION = new CacheFunction() {
        @Override
        public Bytes key(final Bytes cacheKey) {
            return cacheKey;
        }

        @Override
        public Bytes cacheKey(final Bytes key) {
            return key;
        }
    };

    @SuppressWarnings("unchecked")
    private final KeyValueStore<Bytes, LRUCacheEntry> store = new org.apache.kafka.test.GenericInMemoryKeyValueStore("my-store");

    private final KeyValue<Bytes, LRUCacheEntry> firstEntry = KeyValue.pair(Bytes.wrap("a".getBytes()), new LRUCacheEntry("1".getBytes()));

    private final List<KeyValue<Bytes, LRUCacheEntry>> entries = Arrays.asList(firstEntry, KeyValue.pair(Bytes.wrap("b".getBytes()), new LRUCacheEntry("2".getBytes())), KeyValue.pair(Bytes.wrap("c".getBytes()), new LRUCacheEntry("3".getBytes())));

    private FilteredCacheIterator allIterator;

    private FilteredCacheIterator firstEntryIterator;

    @Test
    public void shouldAllowEntryMatchingHasNextCondition() {
        final List<KeyValue<Bytes, LRUCacheEntry>> keyValues = StreamsTestUtils.toList(allIterator);
        MatcherAssert.assertThat(keyValues, CoreMatchers.equalTo(entries));
    }

    @Test
    public void shouldPeekNextKey() {
        while (allIterator.hasNext()) {
            final Bytes nextKey = allIterator.peekNextKey();
            final KeyValue<Bytes, LRUCacheEntry> next = allIterator.next();
            MatcherAssert.assertThat(next.key, CoreMatchers.equalTo(nextKey));
        } 
    }

    @Test
    public void shouldPeekNext() {
        while (allIterator.hasNext()) {
            final KeyValue<Bytes, LRUCacheEntry> peeked = allIterator.peekNext();
            final KeyValue<Bytes, LRUCacheEntry> next = allIterator.next();
            MatcherAssert.assertThat(peeked, CoreMatchers.equalTo(next));
        } 
    }

    @Test
    public void shouldNotHaveNextIfHasNextConditionNotMet() {
        Assert.assertTrue(firstEntryIterator.hasNext());
        firstEntryIterator.next();
        Assert.assertFalse(firstEntryIterator.hasNext());
    }

    @Test
    public void shouldFilterEntriesNotMatchingHasNextCondition() {
        final List<KeyValue<Bytes, LRUCacheEntry>> keyValues = StreamsTestUtils.toList(firstEntryIterator);
        MatcherAssert.assertThat(keyValues, CoreMatchers.equalTo(Arrays.asList(firstEntry)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationExeceptionOnRemove() {
        allIterator.remove();
    }
}

