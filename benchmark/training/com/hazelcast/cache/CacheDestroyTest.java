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


import ICacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CacheDestroyOperation;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static HazelcastCacheManager.CACHE_MANAGER_PREFIX;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheDestroyTest extends CacheTestSupport {
    private static final int INSTANCE_COUNT = 2;

    private TestHazelcastInstanceFactory factory = getInstanceFactory(CacheDestroyTest.INSTANCE_COUNT);

    private HazelcastInstance[] hazelcastInstances;

    private HazelcastInstance hazelcastInstance;

    @Test
    public void test_cacheDestroyOperation() {
        final String CACHE_NAME = "MyCache";
        final String FULL_CACHE_NAME = (CACHE_MANAGER_PREFIX) + CACHE_NAME;
        final CountDownLatch cacheProxyCreatedLatch = new CountDownLatch(CacheDestroyTest.INSTANCE_COUNT);
        for (HazelcastInstance hz : hazelcastInstances) {
            hz.addDistributedObjectListener(new CacheDestroyTest.CacheProxyListener(cacheProxyCreatedLatch));
        }
        CachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(getHazelcastInstance());
        CacheManager cacheManager = cachingProvider.getCacheManager();
        cacheManager.createCache(CACHE_NAME, new CacheConfig());
        NodeEngineImpl nodeEngine1 = HazelcastTestSupport.getNode(getHazelcastInstance()).getNodeEngine();
        final ICacheService cacheService1 = nodeEngine1.getService(SERVICE_NAME);
        InternalOperationService operationService1 = nodeEngine1.getOperationService();
        NodeEngineImpl nodeEngine2 = HazelcastTestSupport.getNode(hazelcastInstances[1]).getNodeEngine();
        final ICacheService cacheService2 = nodeEngine2.getService(SERVICE_NAME);
        Assert.assertNotNull(cacheService1.getCacheConfig(FULL_CACHE_NAME));
        Assert.assertNotNull(cacheService2.getCacheConfig(FULL_CACHE_NAME));
        // wait for the latch to ensure proxy registration events have been processed (otherwise
        // the cache config may be added on a member after having been removed by CacheDestroyOp)
        HazelcastTestSupport.assertOpenEventually(("A cache proxy should have been created on each instance, latch count was " + (cacheProxyCreatedLatch.getCount())), cacheProxyCreatedLatch);
        // Invoke on single node and the operation is also forward to others nodes by the operation itself
        operationService1.invokeOnTarget(SERVICE_NAME, new CacheDestroyOperation(FULL_CACHE_NAME), nodeEngine1.getThisAddress());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNull(cacheService1.getCacheConfig(FULL_CACHE_NAME));
                Assert.assertNull(cacheService2.getCacheConfig(FULL_CACHE_NAME));
            }
        });
    }

    @Test
    public void testInvalidationListenerCallCount() {
        final ICache<String, String> cache = createCache();
        final AtomicInteger counter = new AtomicInteger(0);
        final CacheConfig config = cache.getConfiguration(CacheConfig.class);
        registerInvalidationListener(new CacheEventListener() {
            @Override
            public void handleEvent(Object eventObject) {
                if (eventObject instanceof Invalidation) {
                    Invalidation event = ((Invalidation) (eventObject));
                    if ((null == (event.getKey())) && (config.getNameWithPrefix().equals(event.getName()))) {
                        counter.incrementAndGet();
                    }
                }
            }
        }, config.getNameWithPrefix());
        cache.destroy();
        // Make sure that at least 1 invalidation event has been received
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((counter.get()) >= 1));
            }
        }, 2);
        // Make sure that no more than INSTNACE_COUNT events are received ever
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((counter.get()) <= (CacheDestroyTest.INSTANCE_COUNT)));
            }
        }, 3);
    }

    @Test
    public void test_whenCacheDestroyedConcurrently_thenNoExceptionThrown() throws InterruptedException, ExecutionException {
        String cacheName = HazelcastTestSupport.randomName();
        CacheConfig<Integer, Integer> cacheConfig = createCacheConfig();
        final Cache<Integer, Integer> cache = cacheManager.createCache(cacheName, cacheConfig);
        final CountDownLatch latch = new CountDownLatch(1);
        int concurrency = RuntimeAvailableProcessors.get();
        Future[] destroyFutures = new Future[concurrency];
        CacheDestroyTest.DestroyCacheTask destroyCacheTask = new CacheDestroyTest.DestroyCacheTask(cacheName, cacheManager, latch, cache);
        for (int i = 0; i < concurrency; i++) {
            destroyFutures[i] = HazelcastTestSupport.spawn(destroyCacheTask);
        }
        latch.countDown();
        HazelcastTestSupport.sleepSeconds(5);
        destroyCacheTask.stop();
        for (int i = 0; i < concurrency; i++) {
            destroyFutures[i].get();
        }
    }

    public abstract static class CacheTask implements Runnable {
        protected final AtomicBoolean running = new AtomicBoolean(true);

        protected final String cacheName;

        protected final CacheManager cacheManager;

        protected final CountDownLatch latch;

        public CacheTask(String cacheName, CacheManager cacheManager, CountDownLatch latch) {
            this.cacheName = cacheName;
            this.cacheManager = cacheManager;
            this.latch = latch;
        }

        public void stop() {
            running.set(false);
        }

        @Override
        public void run() {
            try {
                latch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            while (running.get()) {
                run0();
            } 
        }

        protected abstract void run0();
    }

    public static class DestroyCacheTask extends CacheDestroyTest.CacheTask {
        private final Cache cache;

        public DestroyCacheTask(String cacheName, CacheManager cacheManager, CountDownLatch latch, Cache cache) {
            super(cacheName, cacheManager, latch);
            this.cache = cache;
        }

        @Override
        protected void run0() {
            cacheManager.destroyCache(cacheName);
        }
    }

    public static class CreateCacheTask extends CacheDestroyTest.CacheTask {
        private final CacheConfig cacheConfig;

        public CreateCacheTask(String cacheName, CacheManager cacheManager, CountDownLatch latch, CacheConfig cacheConfig) {
            super(cacheName, cacheManager, latch);
            this.cacheConfig = cacheConfig;
        }

        @Override
        protected void run0() {
            try {
                cacheManager.createCache(cacheName, cacheConfig);
            } catch (CacheException e) {
                // cache may have already been created by another thread, so ignore
                if (e.getMessage().startsWith(String.format("A cache named %s already exists", cacheName))) {
                    // ignore
                } else {
                    throw e;
                }
            }
        }
    }

    public static class CacheProxyListener implements DistributedObjectListener {
        private final CountDownLatch objectCreatedLatch;

        public CacheProxyListener(CountDownLatch objectCreatedLatch) {
            this.objectCreatedLatch = objectCreatedLatch;
        }

        @Override
        public void distributedObjectCreated(DistributedObjectEvent event) {
            if ((event.getDistributedObject()) instanceof Cache) {
                objectCreatedLatch.countDown();
            }
        }

        @Override
        public void distributedObjectDestroyed(DistributedObjectEvent event) {
        }
    }
}

