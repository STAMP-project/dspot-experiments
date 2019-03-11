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


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
public class SimpleEhcacheTest {
    private CacheManager cacheManager;

    @Test
    public void testSimplePut() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
    }

    @Test
    public void testSimplePutIfAbsent() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        CharSequence one = testCache.putIfAbsent(1, "one");
        Assert.assertThat(one, Is.is(Matchers.nullValue()));
        CharSequence one_2 = testCache.putIfAbsent(1, "one#2");
        Assert.assertThat(one_2, Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
    }

    @Test
    public void testSimplePutAll() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        Map<Integer, String> values = new HashMap<>();
        values.put(1, "one");
        values.put(2, "two");
        values.put(3, "three");
        testCache.putAll(values);
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(testCache.get(2), Matchers.<CharSequence>equalTo("two"));
        Assert.assertThat(testCache.get(3), Matchers.<CharSequence>equalTo("three"));
    }

    @Test
    public void testSimpleGetAll() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        testCache.put(2, "two");
        Map<Number, CharSequence> all = testCache.getAll(new HashSet<Number>(Arrays.asList(1, 2, 3)));
        Assert.assertThat(all.keySet(), Matchers.containsInAnyOrder(((Number) (1)), 2, 3));
        Assert.assertThat(all.get(1), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(all.get(2), Matchers.<CharSequence>equalTo("two"));
        Assert.assertThat(all.get(3), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimpleContainsKey() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        Assert.assertThat(testCache.containsKey(1), Is.is(true));
        Assert.assertThat(testCache.containsKey(2), Is.is(false));
    }

    @Test
    public void testClear() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        testCache.put(2, "two");
        testCache.clear();
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimpleRemove() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        testCache.put(2, "two");
        testCache.remove(1);
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.notNullValue()));
    }

    @Test
    public void testSimpleRemoveAll() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        testCache.put(2, "two");
        testCache.put(3, "three");
        testCache.removeAll(new HashSet<Number>(Arrays.asList(1, 2)));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(3), Is.is(Matchers.notNullValue()));
    }

    @Test
    public void testSimpleRemove2Args() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        Assert.assertThat(testCache.remove(1, "one_"), Is.is(false));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.notNullValue()));
        Assert.assertThat(testCache.remove(1, "one"), Is.is(true));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimpleReplace() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        Assert.assertThat(testCache.replace(1, "one_"), Matchers.<CharSequence>equalTo("one"));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one_"));
        Assert.assertThat(testCache.replace(2, "two_"), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimpleReplace3Args() throws Exception {
        Cache<Number, CharSequence> testCache = cacheManager.createCache("testCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Number.class, CharSequence.class, ResourcePoolsBuilder.heap(10)));
        testCache.put(1, "one");
        Assert.assertThat(testCache.replace(1, "one_", "one@"), Is.is(false));
        Assert.assertThat(testCache.replace(1, "one", "one#"), Is.is(true));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one#"));
        Assert.assertThat(testCache.replace(2, "two", "two#"), Is.is(false));
    }
}

