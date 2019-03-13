/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.cache;


import Cache.Entry;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import javax.cache.Cache;
import javax.cache.spi.CachingProvider;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachePartitionIteratorTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public boolean prefetchValues;

    private CachingProvider cachingProvider;

    private HazelcastInstance server;

    @Test
    public void test_HasNext_Returns_False_On_EmptyPartition() throws Exception {
        CacheProxy<Integer, Integer> cache = getCacheProxy();
        Iterator<Entry<Integer, Integer>> iterator = cache.iterator(10, 1, prefetchValues);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void test_HasNext_Returns_True_On_NonEmptyPartition() throws Exception {
        CacheProxy<String, String> cache = getCacheProxy();
        String key = HazelcastTestSupport.generateKeyForPartition(server, 1);
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        Iterator<Entry<String, String>> iterator = cache.iterator(10, 1, prefetchValues);
        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition() throws Exception {
        CacheProxy<String, String> cache = getCacheProxy();
        String key = HazelcastTestSupport.generateKeyForPartition(server, 1);
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        Iterator<Entry<String, String>> iterator = cache.iterator(10, 1, prefetchValues);
        Cache.Entry entry = iterator.next();
        assertEquals(value, entry.getValue());
    }

    @Test
    public void test_Next_Returns_Value_On_NonEmptyPartition_and_HasNext_Returns_False_when_Item_Consumed() throws Exception {
        CacheProxy<String, String> cache = getCacheProxy();
        String key = HazelcastTestSupport.generateKeyForPartition(server, 1);
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        Iterator<Entry<String, String>> iterator = cache.iterator(10, 1, prefetchValues);
        Cache.Entry entry = iterator.next();
        assertEquals(value, entry.getValue());
        boolean hasNext = iterator.hasNext();
        Assert.assertFalse(hasNext);
    }

    @Test
    public void test_Next_Returns_Values_When_FetchSizeExceeds_On_NonEmptyPartition() throws Exception {
        CacheProxy<String, String> cache = getCacheProxy();
        String value = HazelcastTestSupport.randomString();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            String key = HazelcastTestSupport.generateKeyForPartition(server, 42);
            cache.put(key, value);
        }
        Iterator<Entry<String, String>> iterator = cache.iterator(10, 42, prefetchValues);
        for (int i = 0; i < count; i++) {
            Cache.Entry entry = iterator.next();
            assertEquals(value, entry.getValue());
        }
    }
}

