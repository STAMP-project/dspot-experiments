/**
 * Copyright (C) 2012 The Guava Authors
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
package com.google.common.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.testing.FakeTicker;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


/**
 * Test suite for {@link CacheBuilder}.
 * TODO(cpovirk): merge into CacheBuilderTest?
 *
 * @author Jon Donovan
 */
@GwtCompatible
public class CacheBuilderGwtTest extends TestCase {
    private FakeTicker fakeTicker;

    public void testLoader() throws ExecutionException {
        final Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder());
        Callable<Integer> loader = new Callable<Integer>() {
            private int i = 0;

            @Override
            public Integer call() throws Exception {
                return ++(i);
            }
        };
        cache.put(0, 10);
        TestCase.assertEquals(Integer.valueOf(10), cache.get(0, loader));
        TestCase.assertEquals(Integer.valueOf(1), cache.get(20, loader));
        TestCase.assertEquals(Integer.valueOf(2), cache.get(34, loader));
        cache.invalidate(0);
        TestCase.assertEquals(Integer.valueOf(3), cache.get(0, loader));
        cache.put(0, 10);
        cache.invalidateAll();
        TestCase.assertEquals(Integer.valueOf(4), cache.get(0, loader));
    }

    public void testSizeConstraint() {
        final Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().executor(MoreExecutors.directExecutor()).initialCapacity(100).maximumSize(4));
        // Enforce full initialization of internal structures
        for (int i = 0; i < 4; i++) {
            cache.put(i, i);
        }
        cache.invalidateAll();
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.put(4, 40);
        cache.put(5, 50);
        TestCase.assertEquals(null, cache.getIfPresent(10));
        // Order required to remove dependence on access order / write order constraint.
        TestCase.assertEquals(Integer.valueOf(10), cache.getIfPresent(1));
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(2));
        TestCase.assertEquals(Integer.valueOf(30), cache.getIfPresent(3));
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(5));
        cache.put(1, 10);
        TestCase.assertEquals(Integer.valueOf(10), cache.getIfPresent(1));
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(2));
        TestCase.assertEquals(Integer.valueOf(30), cache.getIfPresent(3));
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(5));
        TestCase.assertEquals(null, cache.getIfPresent(4));
    }

    public void testExpireAfterAccess() {
        final Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterAccess(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(0, 10);
        cache.put(2, 30);
        fakeTicker.advance(999, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(Integer.valueOf(30), cache.getIfPresent(2));
        fakeTicker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(Integer.valueOf(30), cache.getIfPresent(2));
        fakeTicker.advance(1000, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(null, cache.getIfPresent(0));
    }

    public void testExpireAfterWrite() {
        final Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 100);
        cache.put(20, 200);
        cache.put(4, 2);
        fakeTicker.advance(999, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(Integer.valueOf(100), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(200), cache.getIfPresent(20));
        TestCase.assertEquals(Integer.valueOf(2), cache.getIfPresent(4));
        fakeTicker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(null, cache.getIfPresent(10));
        TestCase.assertEquals(null, cache.getIfPresent(20));
        TestCase.assertEquals(null, cache.getIfPresent(4));
        cache.put(10, 20);
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(10));
        fakeTicker.advance(1000, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(null, cache.getIfPresent(10));
    }

    public void testExpireAfterWriteAndAccess() {
        final Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).expireAfterAccess(500, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 100);
        cache.put(20, 200);
        cache.put(4, 2);
        fakeTicker.advance(499, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(Integer.valueOf(100), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(200), cache.getIfPresent(20));
        fakeTicker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(Integer.valueOf(100), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(200), cache.getIfPresent(20));
        TestCase.assertEquals(null, cache.getIfPresent(4));
        fakeTicker.advance(499, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(null, cache.getIfPresent(10));
        TestCase.assertEquals(null, cache.getIfPresent(20));
        cache.put(10, 20);
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(10));
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        TestCase.assertEquals(null, cache.getIfPresent(10));
    }

    public void testMapMethods() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder());
        ConcurrentMap<Integer, Integer> asMap = cache.asMap();
        cache.put(10, 100);
        cache.put(2, 52);
        asMap.replace(2, 79);
        asMap.replace(3, 60);
        TestCase.assertEquals(null, cache.getIfPresent(3));
        TestCase.assertEquals(null, asMap.get(3));
        TestCase.assertEquals(Integer.valueOf(79), cache.getIfPresent(2));
        TestCase.assertEquals(Integer.valueOf(79), asMap.get(2));
        asMap.replace(10, 100, 50);
        asMap.replace(2, 52, 99);
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(50), asMap.get(10));
        TestCase.assertEquals(Integer.valueOf(79), cache.getIfPresent(2));
        TestCase.assertEquals(Integer.valueOf(79), asMap.get(2));
        asMap.remove(10, 100);
        asMap.remove(2, 79);
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(50), asMap.get(10));
        TestCase.assertEquals(null, cache.getIfPresent(2));
        TestCase.assertEquals(null, asMap.get(2));
        asMap.putIfAbsent(2, 20);
        asMap.putIfAbsent(10, 20);
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(2));
        TestCase.assertEquals(Integer.valueOf(20), asMap.get(2));
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(50), asMap.get(10));
    }

    public void testRemovalListener() {
        final int[] stats = new int[4];
        RemovalListener<Integer, Integer> countingListener = new RemovalListener<Integer, Integer>() {
            @Override
            public void onRemoval(Integer key, Integer value, RemovalCause cause) {
                switch (cause) {
                    case EXPIRED :
                        (stats[0])++;
                        break;
                    case EXPLICIT :
                        (stats[1])++;
                        break;
                    case REPLACED :
                        (stats[2])++;
                        break;
                    case SIZE :
                        (stats[3])++;
                        break;
                    default :
                        throw new IllegalStateException("No collected exceptions in GWT CacheBuilder.");
                }
            }
        };
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).removalListener(countingListener).initialCapacity(100).ticker(fakeTicker::read).maximumSize(2));
        // Enforce full initialization of internal structures
        cache.putAll(ImmutableMap.of(1, 1, 2, 2, 3, 3));
        cache.invalidateAll();
        Arrays.fill(stats, 0);
        // Add more than two elements to increment size removals.
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.put(4, 40);
        cache.put(5, 50);
        // Replace the two present elements.
        Integer key1 = Iterables.get(cache.asMap().keySet(), 0);
        Integer key2 = Iterables.get(cache.asMap().keySet(), 1);
        cache.put(key1, 60);
        cache.put(key2, 70);
        cache.put(key1, 80);
        cache.put(key2, 90);
        // Expire the two present elements.
        key1 = Iterables.get(cache.asMap().keySet(), 0);
        key2 = Iterables.get(cache.asMap().keySet(), 1);
        fakeTicker.advance(1001, TimeUnit.MILLISECONDS);
        cache.getIfPresent(key1);
        cache.getIfPresent(key2);
        // Add two elements and invalidate them.
        cache.put(6, 100);
        cache.put(7, 200);
        cache.invalidateAll();
        TestCase.assertEquals(2, stats[0]);
        TestCase.assertEquals(2, stats[1]);
        TestCase.assertEquals(4, stats[2]);
        TestCase.assertEquals(3, stats[3]);
    }

    public void testPutAll() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder());
        cache.putAll(ImmutableMap.of(10, 20, 30, 50, 60, 90));
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(30));
        TestCase.assertEquals(Integer.valueOf(90), cache.getIfPresent(60));
        cache.asMap().putAll(ImmutableMap.of(10, 50, 30, 20, 60, 70, 5, 5));
        TestCase.assertEquals(Integer.valueOf(50), cache.getIfPresent(10));
        TestCase.assertEquals(Integer.valueOf(20), cache.getIfPresent(30));
        TestCase.assertEquals(Integer.valueOf(70), cache.getIfPresent(60));
        TestCase.assertEquals(Integer.valueOf(5), cache.getIfPresent(5));
    }

    public void testInvalidate() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder());
        cache.put(654, 2675);
        cache.put(2456, 56);
        cache.put(2, 15);
        cache.invalidate(654);
        TestCase.assertFalse(cache.asMap().containsKey(654));
        TestCase.assertTrue(cache.asMap().containsKey(2456));
        TestCase.assertTrue(cache.asMap().containsKey(2));
    }

    public void testInvalidateAll() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder());
        cache.put(654, 2675);
        cache.put(2456, 56);
        cache.put(2, 15);
        cache.invalidateAll();
        TestCase.assertFalse(cache.asMap().containsKey(654));
        TestCase.assertFalse(cache.asMap().containsKey(2456));
        TestCase.assertFalse(cache.asMap().containsKey(2));
        cache.put(654, 2675);
        cache.put(2456, 56);
        cache.put(2, 15);
        cache.put(1, 3);
        cache.invalidateAll(ImmutableSet.of(1, 2));
        TestCase.assertFalse(cache.asMap().containsKey(1));
        TestCase.assertFalse(cache.asMap().containsKey(2));
        TestCase.assertTrue(cache.asMap().containsKey(654));
        TestCase.assertTrue(cache.asMap().containsKey(2456));
    }

    public void testAsMap_containsValue() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(20000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(654, 2675);
        fakeTicker.advance(10000, TimeUnit.MILLISECONDS);
        cache.put(2456, 56);
        cache.put(2, 15);
        fakeTicker.advance(10001, TimeUnit.MILLISECONDS);
        TestCase.assertTrue(cache.asMap().containsValue(15));
        TestCase.assertTrue(cache.asMap().containsValue(56));
        TestCase.assertFalse(cache.asMap().containsValue(2675));
    }

    public void testAsMap_containsKey() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(20000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(654, 2675);
        fakeTicker.advance(10000, TimeUnit.MILLISECONDS);
        cache.put(2456, 56);
        cache.put(2, 15);
        fakeTicker.advance(10001, TimeUnit.MILLISECONDS);
        TestCase.assertTrue(cache.asMap().containsKey(2));
        TestCase.assertTrue(cache.asMap().containsKey(2456));
        TestCase.assertFalse(cache.asMap().containsKey(654));
    }

    public void testAsMapValues_contains() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 20);
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        cache.put(20, 22);
        cache.put(5, 10);
        fakeTicker.advance(501, TimeUnit.MILLISECONDS);
        TestCase.assertTrue(cache.asMap().values().contains(22));
        TestCase.assertTrue(cache.asMap().values().contains(10));
        TestCase.assertFalse(cache.asMap().values().contains(20));
    }

    public void testAsMapKeySet() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 20);
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        cache.put(20, 22);
        cache.put(5, 10);
        fakeTicker.advance(501, TimeUnit.MILLISECONDS);
        Set<Integer> foundKeys = Sets.newHashSet();
        for (Integer current : cache.asMap().keySet()) {
            foundKeys.add(current);
        }
        TestCase.assertEquals(ImmutableSet.of(20, 5), foundKeys);
    }

    public void testAsMapKeySet_contains() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 20);
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        cache.put(20, 22);
        cache.put(5, 10);
        fakeTicker.advance(501, TimeUnit.MILLISECONDS);
        TestCase.assertTrue(cache.asMap().keySet().contains(20));
        TestCase.assertTrue(cache.asMap().keySet().contains(5));
        TestCase.assertFalse(cache.asMap().keySet().contains(10));
    }

    public void testAsMapEntrySet() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 20);
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        cache.put(20, 22);
        cache.put(5, 10);
        fakeTicker.advance(501, TimeUnit.MILLISECONDS);
        int sum = 0;
        for (Map.Entry<Integer, Integer> current : cache.asMap().entrySet()) {
            sum += (current.getKey()) + (current.getValue());
        }
        TestCase.assertEquals(57, sum);
    }

    public void testAsMapValues_iteratorRemove() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1000, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()).ticker(fakeTicker::read));
        cache.put(10, 20);
        Iterator<Integer> iterator = cache.asMap().values().iterator();
        iterator.next();
        iterator.remove();
        TestCase.assertEquals(0, cache.size());
    }
}

