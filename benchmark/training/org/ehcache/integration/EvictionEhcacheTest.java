/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.integration;


import java.util.HashMap;
import java.util.Map;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ludovic Orban
 */
public class EvictionEhcacheTest {
    private CacheManager cacheManager;

    @Test
    public void testSimplePutWithEviction() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(2)).build());
        testCache.put(1, "one");
        testCache.put(2, "two");
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(testCache.get(2), Matchers.<CharSequence>equalTo("two"));
        testCache.put(3, "three");
        testCache.put(4, "four");
        int count = 0;
        for (@SuppressWarnings("unused")
        Cache.Entry<Number, CharSequence> entry : testCache) {
            count++;
        }
        Assert.assertThat(count, Is.is(2));
    }

    @Test
    public void testSimplePutIfAbsentWithEviction() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(2)).build());
        testCache.putIfAbsent(1, "one");
        testCache.putIfAbsent(2, "two");
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(testCache.get(2), Matchers.<CharSequence>equalTo("two"));
        testCache.putIfAbsent(3, "three");
        testCache.putIfAbsent(4, "four");
        int count = 0;
        for (@SuppressWarnings("unused")
        Cache.Entry<Number, CharSequence> entry : testCache) {
            count++;
        }
        Assert.assertThat(count, Is.is(2));
    }

    @Test
    public void testSimplePutAllWithEviction() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(2)).build());
        Map<Integer, String> values = new HashMap<>();
        values.put(1, "one");
        values.put(2, "two");
        values.put(3, "three");
        values.put(4, "four");
        testCache.putAll(values);
        int count = 0;
        for (@SuppressWarnings("unused")
        Cache.Entry<Number, CharSequence> entry : testCache) {
            count++;
        }
        Assert.assertThat(count, Is.is(2));
    }
}

