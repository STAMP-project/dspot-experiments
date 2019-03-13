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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheListenerTest extends HazelcastTestSupport {
    protected HazelcastInstance hazelcastInstance;

    @Test
    public void testSyncListener() throws Exception {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        final AtomicInteger counter = new AtomicInteger();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(counter)), null, true, true));
        final Cache<String, String> cache = cacheManager.createCache("test", config);
        final int threadCount = 10;
        final int shutdownWaitTimeInSeconds = threadCount;
        final int putCount = 1000;
        final AtomicInteger actualPutCount = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicBoolean shutdown = new AtomicBoolean(false);
        for (int i = 0; i < threadCount; i++) {
            new Thread() {
                public void run() {
                    Random rand = new Random();
                    for (int i = 0; (i < putCount) && (!(shutdown.get())); i++) {
                        String key = String.valueOf(rand.nextInt(putCount));
                        String value = UUID.randomUUID().toString();
                        cache.put(key, value);
                        actualPutCount.incrementAndGet();
                    }
                    latch.countDown();
                }
            }.start();
        }
        if (!(latch.await(HazelcastTestSupport.ASSERT_TRUE_EVENTUALLY_TIMEOUT, TimeUnit.SECONDS))) {
            shutdown.set(true);
            if (!(latch.await(shutdownWaitTimeInSeconds, TimeUnit.SECONDS))) {
                Assert.fail((("Cache operations have not finished in " + ((HazelcastTestSupport.ASSERT_TRUE_EVENTUALLY_TIMEOUT) + shutdownWaitTimeInSeconds)) + " seconds when sync listener is present!"));
            }
        }
        Assert.assertEquals(actualPutCount.get(), counter.get());
    }

    @Test(timeout = 30000)
    public void testPutIfAbsentWithSyncListener_whenEntryExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        String key = HazelcastTestSupport.randomString();
        cache.put(key, HazelcastTestSupport.randomString());
        // there should not be any hanging due to sync listener
        cache.putIfAbsent(key, HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testReplaceWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.replace(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testReplaceIfSameWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.replace(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testReplaceIfSameWithSyncListener_whenValueIsNotSame() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        String key = HazelcastTestSupport.randomString();
        cache.put(key, HazelcastTestSupport.randomString());
        // there should not be any hanging due to sync listener
        cache.replace(key, HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testRemoveWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.remove(HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testRemoveIfSameWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.remove(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testRemoveIfSameWithSyncListener_whenValueIsNotSame() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        String key = HazelcastTestSupport.randomString();
        cache.put(key, HazelcastTestSupport.randomString());
        // there should not be any hanging due to sync listener
        cache.remove(key, HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testGetAndReplaceWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.getAndReplace(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
    }

    @Test(timeout = 30000)
    public void testGetAndRemoveWithSyncListener_whenEntryNotExists() {
        CachingProvider cachingProvider = getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().setTypes(String.class, String.class).addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        Cache<String, String> cache = cacheManager.createCache(HazelcastTestSupport.randomString(), config);
        // there should not be any hanging due to sync listener
        cache.getAndRemove(HazelcastTestSupport.randomString());
    }

    @Test
    public void testSyncListener_shouldNotHang_whenHazelcastInstanceShutdown() {
        CachingProvider provider = getCachingProvider();
        testSyncListener_shouldNotHang_AfterAction(HazelcastTestSupport.randomMapName(), provider, new Runnable() {
            @Override
            public void run() {
                hazelcastInstance.shutdown();
            }
        });
    }

    @Test
    public void testSyncListener_shouldNotHang_whenCacheClosed() {
        final CachingProvider provider = getCachingProvider();
        final String cacheName = HazelcastTestSupport.randomMapName();
        testSyncListener_shouldNotHang_AfterAction(cacheName, provider, new Runnable() {
            @Override
            public void run() {
                Cache cache = provider.getCacheManager().getCache(cacheName);
                cache.close();
            }
        });
    }

    @Test
    public void testSyncListener_shouldNotHang_whenCacheDestroyed() {
        final CachingProvider provider = getCachingProvider();
        final String cacheName = HazelcastTestSupport.randomMapName();
        testSyncListener_shouldNotHang_AfterAction(cacheName, provider, new Runnable() {
            @Override
            public void run() {
                provider.getCacheManager().destroyCache(cacheName);
            }
        });
    }

    public static class TestListener implements Serializable , CacheEntryCreatedListener<String, String> , CacheEntryUpdatedListener<String, String> {
        private final AtomicInteger counter;

        public TestListener(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends String>> cacheEntryEvents) throws CacheEntryListenerException {
            onEvent(cacheEntryEvents);
        }

        @Override
        public void onUpdated(Iterable<CacheEntryEvent<? extends String, ? extends String>> cacheEntryEvents) throws CacheEntryListenerException {
            onEvent(cacheEntryEvents);
        }

        private void onEvent(Iterable<CacheEntryEvent<? extends String, ? extends String>> cacheEntryEvents) {
            int count = 0;
            for (CacheEntryEvent cacheEntryEvent : cacheEntryEvents) {
                count++;
            }
            // add some random delay to simulate sync listener
            LockSupport.parkNanos(((long) (((Math.random()) * 10) * count)));
            counter.addAndGet(count);
        }
    }

    @Test
    public void cacheEntryListenerCountIncreasedAndDecreasedCorrectly() {
        final CachingProvider provider = getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();
        CompleteConfiguration<String, String> config = new javax.cache.configuration.MutableConfiguration<String, String>().addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<String, String>(FactoryBuilder.factoryOf(new CacheListenerTest.TestListener(new AtomicInteger())), null, true, true));
        cacheManager.createCache("MyCache", config);
    }
}

