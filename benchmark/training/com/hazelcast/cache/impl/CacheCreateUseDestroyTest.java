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
package com.hazelcast.cache.impl;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.memory.MemorySize;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class CacheCreateUseDestroyTest extends HazelcastTestSupport {
    private static final MemorySize NATIVE_MEMORY_SIZE = new MemorySize(32, MemoryUnit.MEGABYTES);

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    protected CacheManager defaultCacheManager;

    protected Cache<String, String> cache;

    protected ICacheService cacheService;

    @Test
    public void testCache_whenDestroyedByCacheManager() {
        String cacheName = HazelcastTestSupport.randomMapName("cache");
        cache = defaultCacheManager.getCache(cacheName);
        cache.put("key", "value");
        cache.get("key");
        assertCreatedCache(cacheName);
        defaultCacheManager.destroyCache(cacheName);
        assertDestroyedCache(cacheName);
    }

    @Test
    public void testCache_whenDestroyedByICache_destroy() {
        String cacheName = HazelcastTestSupport.randomMapName("cache");
        cache = defaultCacheManager.getCache(cacheName);
        DistributedObject internalCacheProxy = cache.unwrap(DistributedObject.class);
        cache.put("key", "value");
        cache.get("key");
        assertCreatedCache(cacheName);
        internalCacheProxy.destroy();
        assertDestroyedCache(cacheName);
    }

    public static class CacheEntryListener implements Closeable , Serializable , CacheEntryCreatedListener<String, String> {
        volatile boolean closed;

        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends String>> cacheEntryEvents) throws CacheEntryListenerException {
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    public static class CacheEntryListenerFactory implements Factory<CacheCreateUseDestroyTest.CacheEntryListener> {
        public static volatile CacheCreateUseDestroyTest.CacheEntryListener listener;

        @Override
        public CacheCreateUseDestroyTest.CacheEntryListener create() {
            CacheCreateUseDestroyTest.CacheEntryListenerFactory.listener = new CacheCreateUseDestroyTest.CacheEntryListener();
            return CacheCreateUseDestroyTest.CacheEntryListenerFactory.listener;
        }
    }
}

