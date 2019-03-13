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


import com.hazelcast.cache.CacheReadWriteThroughTest;
import com.hazelcast.config.CacheConfiguration;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientCacheReadWriteThroughTest extends CacheReadWriteThroughTest {
    private static final String CACHE_WITH_NEARCACHE = randomName();

    private static final int NEARCACHE_SIZE = 100;

    private CachingProvider serverCachingProvider;

    // https://github.com/hazelcast/hazelcast/issues/6676
    @Test
    public void test_cacheLoaderIsUsedOnlyAtServerSide() {
        final String cacheName = randomName();
        CacheManager serverCacheManager = serverCachingProvider.getCacheManager();
        CompleteConfiguration<Integer, String> config = new com.hazelcast.config.CacheConfig<Integer, String>().setTypes(Integer.class, String.class).setReadThrough(true).setCacheLoaderFactory(new ClientCacheReadWriteThroughTest.ServerSideCacheLoaderFactory());
        serverCacheManager.createCache(cacheName, config);
        CacheManager clientCacheManager = cachingProvider.getCacheManager();
        Cache<Integer, String> cache = clientCacheManager.getCache(cacheName, Integer.class, String.class);
        Assert.assertNotNull(cache);
        Set<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 100; i++) {
            keys.add(i);
        }
        Map<Integer, String> loaded = cache.getAll(keys);
        Assert.assertEquals(keys.size(), loaded.size());
        for (Map.Entry<Integer, String> entry : loaded.entrySet()) {
            Assert.assertEquals(ClientCacheReadWriteThroughTest.ServerSideCacheLoader.valueOf(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void test_readThroughCacheLoader_withNearCache() {
        String cacheName = ClientCacheReadWriteThroughTest.CACHE_WITH_NEARCACHE;
        CacheConfiguration<Integer, String> cacheConfig = new com.hazelcast.config.CacheConfig<Integer, String>().setReadThrough(true).setCacheLoaderFactory(new ClientCacheReadWriteThroughTest.ServerSideCacheLoaderFactory());
        serverCachingProvider.getCacheManager().createCache(cacheName, cacheConfig);
        Cache<Integer, String> cache = cachingProvider.getCacheManager().getCache(cacheName);
        for (int i = 0; i < ((ClientCacheReadWriteThroughTest.NEARCACHE_SIZE) * 5); i++) {
            Assert.assertNotNull(cache.get(i));
        }
    }

    public static class ServerSideCacheLoaderFactory implements HazelcastInstanceAware , Factory<ClientCacheReadWriteThroughTest.ServerSideCacheLoader> {
        private transient HazelcastInstance hazelcastInstance;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hazelcastInstance = hazelcastInstance;
        }

        @Override
        public ClientCacheReadWriteThroughTest.ServerSideCacheLoader create() {
            if ((hazelcastInstance) instanceof HazelcastInstanceImpl) {
                return new ClientCacheReadWriteThroughTest.ServerSideCacheLoader();
            } else {
                throw new IllegalStateException("This factory can only be used at server side!");
            }
        }
    }

    private static class ServerSideCacheLoader implements CacheLoader<Integer, String> {
        static String valueOf(Integer key) {
            return "value-of-" + key;
        }

        @Override
        public String load(Integer key) {
            return ClientCacheReadWriteThroughTest.ServerSideCacheLoader.valueOf(key);
        }

        @Override
        public Map<Integer, String> loadAll(Iterable<? extends Integer> keys) throws CacheLoaderException {
            Map<Integer, String> result = new HashMap<Integer, String>();
            for (Integer key : keys) {
                String value = load(key);
                result.put(key, value);
            }
            return result;
        }
    }
}

