/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.collections;


import Int2ObjectCache.KeyIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class Int2ObjectCacheTest {
    public static final int NUM_SETS = 16;

    public static final int SET_SIZE = 4;

    public static final int CAPACITY = (Int2ObjectCacheTest.NUM_SETS) * (Int2ObjectCacheTest.SET_SIZE);

    public static final Consumer<String> EVICTION_CONSUMER = ( s) -> {
    };

    private final Int2ObjectCache<String> cache = new Int2ObjectCache(Int2ObjectCacheTest.NUM_SETS, Int2ObjectCacheTest.SET_SIZE, Int2ObjectCacheTest.EVICTION_CONSUMER);

    @Test
    public void shouldDoPutAndThenGet() {
        final String value = "Seven";
        cache.put(7, value);
        Assert.assertThat(cache.get(7), Is.is(value));
    }

    @Test
    public void shouldReplaceExistingValueForTheSameKey() {
        final int key = 7;
        final String value = "Seven";
        cache.put(key, value);
        final String newValue = "New Seven";
        cache.put(key, newValue);
        Assert.assertThat(cache.get(key), Is.is(newValue));
        Assert.assertThat(cache.size(), Is.is(1));
    }

    @Test
    public void shouldLimitSizeToMaxSize() {
        for (int i = 0; i < ((Int2ObjectCacheTest.CAPACITY) * 2); i++) {
            cache.put(i, Integer.toString(i));
        }
        Assert.assertThat(cache.size(), Matchers.greaterThan(0));
        Assert.assertThat(cache.size(), Matchers.lessThanOrEqualTo(Int2ObjectCacheTest.CAPACITY));
    }

    @Test
    public void shouldClearCollection() {
        for (int i = 0; i < (Int2ObjectCacheTest.CAPACITY); i++) {
            cache.put(i, Integer.toString(i));
        }
        Assert.assertThat(cache.size(), Matchers.greaterThan(0));
        cache.clear();
        Assert.assertThat(cache.size(), Is.is(0));
    }

    @Test
    public void shouldContainValue() {
        final int key = 7;
        final String value = "Seven";
        cache.put(key, value);
        Assert.assertTrue(cache.containsValue(value));
        Assert.assertFalse(cache.containsValue("NoKey"));
    }

    @Test
    public void shouldContainKey() {
        final int key = 7;
        final String value = "Seven";
        cache.put(key, value);
        Assert.assertTrue(cache.containsKey(key));
        Assert.assertFalse(cache.containsKey(0));
    }

    @Test
    public void shouldRemoveEntry() {
        final int key = 7;
        final String value = "Seven";
        cache.put(key, value);
        Assert.assertTrue(cache.containsKey(key));
        cache.remove(key);
        Assert.assertFalse(cache.containsKey(key));
    }

    @Test
    public void shouldIterateValues() {
        final Collection<String> initialSet = new HashSet<>();
        for (int i = 0; i < ((Int2ObjectCacheTest.CAPACITY) - 1); i++) {
            final String value = Integer.toString(i);
            cache.put(i, value);
            initialSet.add(value);
        }
        final Collection<String> copyToSet = new HashSet<>();
        for (final String s : cache.values()) {
            // noinspection UseBulkOperation
            copyToSet.add(s);
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeysGettingIntAsPrimitive() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < ((Int2ObjectCacheTest.CAPACITY) - 1); i++) {
            final String value = Integer.toString(i);
            cache.put(i, value);
            initialSet.add(i);
        }
        final Collection<Integer> copyToSet = new HashSet<>();
        for (final Int2ObjectCache.KeyIterator iter = cache.keySet().iterator(); iter.hasNext();) {
            copyToSet.add(iter.nextInt());
        }
        Assert.assertThat(copyToSet, Is.is(initialSet));
    }

    @Test
    public void shouldIterateKeys() {
        final Collection<Integer> initialSet = new HashSet<>();
        for (int i = 0; i < ((Int2ObjectCacheTest.CAPACITY) - 1); i++) {
            final String value = Integer.toString(i);
            cache.put(i, value);
            initialSet.add(i);
        }
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
        assertIterateKeys(initialSet);
    }

    @Test
    public void shouldIterateEntries() {
        final int count = (Int2ObjectCacheTest.CAPACITY) - 1;
        for (int i = 0; i < count; i++) {
            final String value = Integer.toString(i);
            cache.put(i, value);
        }
        for (final Map.Entry<Integer, String> entry : cache.entrySet()) {
            Assert.assertThat(String.valueOf(entry.getKey()), IsEqual.equalTo(entry.getValue()));
        }
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            cache.put(testEntry, String.valueOf(testEntry));
        }
        final String mapAsAString = "{12=12, 11=11, 7=7, 19=19, 3=3, 1=1}";
        Assert.assertThat(cache.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldEvictAsMaxSizeIsExceeded() {
        final HashSet<String> evictedItems = new HashSet<>();
        final Consumer<String> evictionConsumer = evictedItems::add;
        final Int2ObjectCache<String> cache = new Int2ObjectCache(Int2ObjectCacheTest.NUM_SETS, Int2ObjectCacheTest.SET_SIZE, evictionConsumer);
        final int count = (Int2ObjectCacheTest.CAPACITY) * 2;
        for (int i = 0; i < count; i++) {
            final String value = Integer.toString(i);
            cache.put(i, value);
        }
        Assert.assertThat(((cache.size()) + (evictedItems.size())), Is.is(count));
    }

    @Test
    public void shouldComputeIfAbsent() {
        final int testKey = 7;
        final String testValue = "7";
        final IntFunction<String> function = ( i) -> testValue;
        Assert.assertNull(cache.get(testKey));
        Assert.assertThat(cache.computeIfAbsent(testKey, function), Is.is(testValue));
        Assert.assertThat(cache.get(testKey), Is.is(testValue));
    }

    @Test
    public void shouldTestStats() {
        Assert.assertThat(cache.cachePuts(), Is.is(0L));
        Assert.assertThat(cache.cacheMisses(), Is.is(0L));
        Assert.assertThat(cache.cacheHits(), Is.is(0L));
        cache.get(7);
        Assert.assertThat(cache.cacheMisses(), Is.is(1L));
        Assert.assertThat(cache.cacheHits(), Is.is(0L));
        Assert.assertThat(cache.cachePuts(), Is.is(0L));
        cache.put(7, "Seven");
        Assert.assertThat(cache.cacheMisses(), Is.is(1L));
        Assert.assertThat(cache.cacheHits(), Is.is(0L));
        Assert.assertThat(cache.cachePuts(), Is.is(1L));
        cache.get(7);
        Assert.assertThat(cache.cacheMisses(), Is.is(1L));
        Assert.assertThat(cache.cacheHits(), Is.is(1L));
        Assert.assertThat(cache.cachePuts(), Is.is(1L));
        cache.resetCounters();
        Assert.assertThat(cache.cachePuts(), Is.is(0L));
        Assert.assertThat(cache.cacheMisses(), Is.is(0L));
        Assert.assertThat(cache.cacheHits(), Is.is(0L));
    }
}

