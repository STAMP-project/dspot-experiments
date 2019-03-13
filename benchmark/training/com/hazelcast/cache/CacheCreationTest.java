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


import CacheService.SERVICE_NAME;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class CacheCreationTest extends HazelcastTestSupport {
    private static final int THREAD_COUNT = 4;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createSingleCache() {
        CachingProvider cachingProvider = createCachingProvider(getDeclarativeConfig());
        Cache<Object, Object> cache = cachingProvider.getCacheManager().getCache(("xmlCache" + 1));
        cache.get(1);
    }

    @Test
    public void createOrGetConcurrentlySingleCache_fromMultiProviders() {
        ExecutorService executorService = Executors.newFixedThreadPool(CacheCreationTest.THREAD_COUNT);
        final CountDownLatch latch = new CountDownLatch(CacheCreationTest.THREAD_COUNT);
        for (int i = 0; i < (CacheCreationTest.THREAD_COUNT); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    CachingProvider cachingProvider = createCachingProvider(getDeclarativeConfig());
                    Cache<Object, Object> cache = cachingProvider.getCacheManager().getCache("xmlCache");
                    cache.get(1);
                    latch.countDown();
                }
            });
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        executorService.shutdown();
    }

    @Test
    public void createConcurrentlyMultipleCaches_fromMultipleProviders() {
        ExecutorService executorService = Executors.newFixedThreadPool(CacheCreationTest.THREAD_COUNT);
        final CountDownLatch latch = new CountDownLatch(CacheCreationTest.THREAD_COUNT);
        for (int i = 0; i < (CacheCreationTest.THREAD_COUNT); i++) {
            final String cacheName = "xmlCache" + i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    CachingProvider cachingProvider = createCachingProvider(getDeclarativeConfig());
                    Cache<Object, Object> cache = cachingProvider.getCacheManager().getCache(cacheName);
                    cache.get(1);
                    latch.countDown();
                }
            });
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        executorService.shutdown();
    }

    @Test
    public void createInvalidCache_fromProgrammaticConfig_throwsException_fromCacheManager_getCache() {
        Config config = createInvalidConfig();
        CachingProvider cachingProvider = createCachingProvider(config);
        CacheManager defaultCacheManager = cachingProvider.getCacheManager();
        thrown.expect(IllegalArgumentException.class);
        defaultCacheManager.getCache("test");
    }

    @Test
    public void createInvalidCache_throwsException_fromCacheManager_createCache() {
        CachingProvider cachingProvider = createCachingProvider(createBasicConfig());
        CacheManager defaultCacheManager = cachingProvider.getCacheManager();
        thrown.expect(IllegalArgumentException.class);
        defaultCacheManager.createCache("test", createInvalidCacheConfig());
    }

    @Test
    public void createInvalidCache_fromDeclarativeConfig_throwsException_fromHazelcastInstanceCreation() {
        System.setProperty("hazelcast.config", "classpath:test-hazelcast-invalid-cache.xml");
        CachingProvider cachingProvider = Caching.getCachingProvider();
        thrown.expect(CacheException.class);
        cachingProvider.getCacheManager();
    }

    // test special Cache proxy creation, required for compatibility with 3.6 clients
    // should be removed in 4.0
    @Test
    public void test_createSetupRef() {
        Assume.assumeFalse("test_createSetupRef is only applicable for Hazelcast members", ClassLoaderUtil.isClassAvailable(null, "com.hazelcast.client.HazelcastClient"));
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        DistributedObject setupRef = HazelcastTestSupport.getNodeEngineImpl(hz).getProxyService().getDistributedObject(SERVICE_NAME, "setupRef");
        Assert.assertNotNull(setupRef);
    }

    @Test
    public void getExistingCache_onNewCacheManager_afterManagerClosed() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager();
        String cacheName = HazelcastTestSupport.randomName();
        Cache cache = manager.createCache(cacheName, new CacheConfig());
        Assert.assertEquals(manager, cache.getCacheManager());
        manager.close();
        // cache is no longer managed
        Assert.assertNull(cache.getCacheManager());
        manager = provider.getCacheManager();
        cache = manager.getCache(cacheName);
        Assert.assertEquals(manager, cache.getCacheManager());
    }

    @Test
    public void getExistingCache_afterCacheClosed() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager();
        String cacheName = HazelcastTestSupport.randomName();
        Cache cache = manager.createCache(cacheName, new CacheConfig());
        Assert.assertEquals(manager, cache.getCacheManager());
        cache.close();
        // cache is no longer managed
        Assert.assertNull(cache.getCacheManager());
        cache = manager.getCache(cacheName);
        // cache is now managed
        Assert.assertEquals(manager, cache.getCacheManager());
    }
}

