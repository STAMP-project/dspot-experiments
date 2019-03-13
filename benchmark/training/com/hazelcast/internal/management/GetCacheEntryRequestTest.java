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
package com.hazelcast.internal.management;


import com.hazelcast.cache.CacheTestSupport;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.request.GetCacheEntryRequest;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Random;
import javax.cache.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class GetCacheEntryRequestTest extends CacheTestSupport {
    private static final Random random = new Random();

    private TestHazelcastInstanceFactory instanceFactory;

    private HazelcastInstance[] instances;

    private String cacheName = HazelcastTestSupport.randomName();

    private String value = HazelcastTestSupport.randomString();

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void testGetCacheEntry_string() {
        String key = HazelcastTestSupport.randomString();
        cacheManager.getCache(cacheName).put(key, value);
        JsonObject result = sendRequestToInstance(instances[0], new GetCacheEntryRequest("string", cacheName, key));
        Assert.assertEquals(value, result.get("cacheBrowse_value").asString());
    }

    @Test
    public void testGetCacheEntry_long() {
        long key = GetCacheEntryRequestTest.random.nextLong();
        cacheManager.getCache(cacheName).put(key, value);
        JsonObject result = sendRequestToInstance(instances[0], new GetCacheEntryRequest("long", cacheName, String.valueOf(key)));
        Assert.assertEquals(value, result.get("cacheBrowse_value").asString());
    }

    @Test
    public void testGetCacheEntry_integer() {
        int key = GetCacheEntryRequestTest.random.nextInt();
        cacheManager.getCache(cacheName).put(key, value);
        JsonObject result = sendRequestToInstance(instances[0], new GetCacheEntryRequest("integer", cacheName, String.valueOf(key)));
        Assert.assertEquals(value, result.get("cacheBrowse_value").asString());
    }

    @Test
    public void testGetCacheEntry_remoteMember() {
        Cache<String, String> cache = cacheManager.getCache(cacheName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        JsonObject result = sendRequestToInstance(instances[1], new GetCacheEntryRequest("string", cacheName, key));
        Assert.assertEquals(value, result.get("cacheBrowse_value").asString());
    }

    @Test
    public void testGetCacheEntry_missingKey() {
        // ensure CacheConfig already exists on all members
        cacheManager.getCache(cacheName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        JsonObject result = sendRequestToInstance(instances[0], new GetCacheEntryRequest("string", cacheName, key));
        Assert.assertNull(result.get("cacheBrowse_value"));
    }
}

