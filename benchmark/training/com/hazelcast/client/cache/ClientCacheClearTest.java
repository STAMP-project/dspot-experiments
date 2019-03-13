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
package com.hazelcast.client.cache;


import com.hazelcast.cache.CacheClearTest;
import com.hazelcast.cache.ICache;
import com.hazelcast.client.spi.EventHandler;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientCacheClearTest extends CacheClearTest {
    private TestHazelcastFactory clientFactory;

    private HazelcastInstance client;

    @Test
    public void testClientInvalidationListenerCallCount() {
        ICache<String, String> cache = createCache();
        Map<String, String> entries = createAndFillEntries();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
        // Verify that put works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        final AtomicInteger counter = new AtomicInteger(0);
        CacheConfig config = cache.getConfiguration(CacheConfig.class);
        registerInvalidationListener(new EventHandler() {
            @Override
            public void handle(Object event) {
                counter.getAndIncrement();
            }

            @Override
            public void beforeListenerRegister() {
            }

            @Override
            public void onListenerRegister() {
            }
        }, config.getNameWithPrefix());
        cache.clear();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, counter.get());
            }
        }, 2);
        // Make sure that the callback is not called for a while
        assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((counter.get()) <= 1));
            }
        }, 3);
    }

    @Test
    public void testClientInvalidationListenerCallCountWhenServerCacheClearUsed() {
        ICache<String, String> cache = createCache();
        Map<String, String> entries = createAndFillEntries();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
        // Verify that put works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        final AtomicInteger counter = new AtomicInteger(0);
        CacheConfig config = cache.getConfiguration(CacheConfig.class);
        registerInvalidationListener(new EventHandler() {
            @Override
            public void handle(Object event) {
                counter.getAndIncrement();
            }

            @Override
            public void beforeListenerRegister() {
            }

            @Override
            public void onListenerRegister() {
            }
        }, config.getNameWithPrefix());
        ICache<Object, Object> serverCache = getHazelcastInstance().getCacheManager().getCache(config.getName());
        serverCache.clear();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, counter.get());
            }
        }, 2);
        // Make sure that the callback is not called for a while
        assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((counter.get()) <= 1));
            }
        }, 3);
    }
}

