/**
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.common.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 *
 *
 * @author Charles Fry
 */
public class CacheManualTest extends TestCase {
    public void testGetIfPresent() {
        Cache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats());
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        Object one = new Object();
        Object two = new Object();
        TestCase.assertNull(cache.getIfPresent(one));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertNull(cache.asMap().get(one));
        TestCase.assertFalse(cache.asMap().containsKey(one));
        TestCase.assertFalse(cache.asMap().containsValue(two));
        TestCase.assertNull(cache.getIfPresent(two));
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertNull(cache.asMap().get(two));
        TestCase.assertFalse(cache.asMap().containsKey(two));
        TestCase.assertFalse(cache.asMap().containsValue(one));
        cache.put(one, two);
        TestCase.assertSame(two, cache.getIfPresent(one));
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        TestCase.assertSame(two, cache.asMap().get(one));
        TestCase.assertTrue(cache.asMap().containsKey(one));
        TestCase.assertTrue(cache.asMap().containsValue(two));
        TestCase.assertNull(cache.getIfPresent(two));
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        TestCase.assertNull(cache.asMap().get(two));
        TestCase.assertFalse(cache.asMap().containsKey(two));
        TestCase.assertFalse(cache.asMap().containsValue(one));
        cache.put(two, one);
        TestCase.assertSame(two, cache.getIfPresent(one));
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        TestCase.assertSame(two, cache.asMap().get(one));
        TestCase.assertTrue(cache.asMap().containsKey(one));
        TestCase.assertTrue(cache.asMap().containsValue(two));
        TestCase.assertSame(one, cache.getIfPresent(two));
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
        TestCase.assertSame(one, cache.asMap().get(two));
        TestCase.assertTrue(cache.asMap().containsKey(two));
        TestCase.assertTrue(cache.asMap().containsValue(one));
    }

    public void testGetAllPresent() {
        Cache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats());
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(), cache.getAllPresent(ImmutableList.<Integer>of()));
        stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(), cache.getAllPresent(Arrays.asList(1, 2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.put(2, 22);
        TestCase.assertEquals(ImmutableMap.of(2, 22), cache.getAllPresent(Arrays.asList(1, 2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(5, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        cache.put(3, 33);
        TestCase.assertEquals(ImmutableMap.of(2, 22, 3, 33), cache.getAllPresent(Arrays.asList(1, 2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(6, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
        cache.put(1, 11);
        TestCase.assertEquals(ImmutableMap.of(1, 11, 2, 22, 3, 33), cache.getAllPresent(Arrays.asList(1, 2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(6, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(6, stats.hitCount());
    }
}

