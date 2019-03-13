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
package com.hazelcast.cache.instance;


import ICacheService.SERVICE_NAME;
import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICacheManager;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheThroughHazelcastInstanceTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "MyCache";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getCache_whenThereIsNoCacheConfig_thenFail() {
        thrown.expect(CacheNotExistsException.class);
        whenThereIsNoCacheConfig_thenFail(true);
    }

    @Test
    public void getDistributedObject_whenThereIsNoCacheConfig_thenFail() {
        thrown.expect(CacheNotExistsException.class);
        whenThereIsNoCacheConfig_thenFail(false);
    }

    @Test
    public void getCache_whenJCacheLibIsNotAvailable_thenFail() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = new CacheThroughHazelcastInstanceTest.NonJCacheAwareClassLoader();
            Config config = createConfig();
            config.setClassLoader(classLoader);
            Thread.currentThread().setContextClassLoader(classLoader);
            HazelcastInstance instance = createInstance(config);
            thrown.expect(IllegalStateException.class);
            CacheThroughHazelcastInstanceTest.retrieveCache(instance, true);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Test
    public void whenJCacheLibIsNotAvailable_thenOtherServicesWorks() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = new CacheThroughHazelcastInstanceTest.NonJCacheAwareClassLoader();
            Config config = createConfig();
            config.setClassLoader(classLoader);
            Thread.currentThread().setContextClassLoader(classLoader);
            HazelcastInstance instance = createInstance(config);
            IMap<Integer, String> map = instance.getMap(HazelcastTestSupport.randomName());
            map.put(1, "Value-1");
            Assert.assertEquals("Value-1", map.get(1));
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Test
    public void getCache_whenThereIsCacheConfigAndDefinedInConfig_thenRetrieveCacheSucceeds() {
        whenThereIsCacheConfigAndDefinedInConfig_thenRetrieveCacheSucceeds(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigAndDefinedInConfig_thenRetrieveCacheSucceeds() {
        whenThereIsCacheConfigAndDefinedInConfig_thenRetrieveCacheSucceeds(false);
    }

    @Test
    public void getCache_whenThereIsCacheConfigAndCreatedByCacheManager_thenReturnsSameCache() {
        whenThereIsCacheConfigAndCreatedByCacheManager_thenReturnsSameCache(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigAndCreatedByCacheManager_thenReturnsSameCache() {
        whenThereIsCacheConfigAndCreatedByCacheManager_thenReturnsSameCache(false);
    }

    @Test
    public void getCache_whenThereIsCacheConfigWithURIandCreatedByCacheManager_thenReturnsSameCache() throws Exception {
        whenThereIsCacheConfigWithURIandCreatedByCacheManager_thenReturnsSameCache(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigWithURIandCreatedByCacheManager_thenReturnsSameCache() throws Exception {
        whenThereIsCacheConfigWithURIandCreatedByCacheManager_thenReturnsSameCache(false);
    }

    @Test
    public void getCache_whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByCacheManager_thenFail() throws Exception {
        thrown.expect(CacheNotExistsException.class);
        whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByCacheManager_thenFail(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByCacheManager_thenFail() throws Exception {
        thrown.expect(CacheNotExistsException.class);
        whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByCacheManager_thenFail(false);
    }

    @Test
    public void getCache_whenThereIsCacheConfigAndCreatedByInstance_thenReturnSameCache() {
        whenThereIsCacheConfigAndCreatedByInstance_thenReturnSameCache(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigAndCreatedByInstance_thenReturnSameCache() {
        whenThereIsCacheConfigAndCreatedByInstance_thenReturnSameCache(false);
    }

    @Test
    public void getCache_whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByInstance_thenReturnDifferentCache() throws Exception {
        whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByInstance_thenReturnDifferentCache(true);
    }

    @Test
    public void getDistributedObject_whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByInstance_thenReturnDifferentCache() throws Exception {
        whenThereIsCacheConfigWithSameNameButDifferentFullNameAndCreatedByInstance_thenReturnDifferentCache(false);
    }

    @Test
    public void getCache_whenOwnerInstanceIsShutdown_thenOperateOnCacheFails() {
        whenOwnerInstanceIsShutdown_thenOperateOnCacheFails(true);
    }

    @Test
    public void getDistributedObject_whenOwnerInstanceIsShutdown_thenOperateOnCacheFails() {
        whenOwnerInstanceIsShutdown_thenOperateOnCacheFails(false);
    }

    @Test
    public void getCache_whenCacheIsDestroyed_thenCacheIsRemovedFromDistributedObject() {
        getCache_whenCacheIsDestroyed_thenCacheIsRemovedFromDistributedObject(true);
    }

    @Test
    public void getDistributedObject_getCache_whenCacheIsDestroyed_thenCacheIsRemovedFromDistributedObject() {
        getCache_whenCacheIsDestroyed_thenCacheIsRemovedFromDistributedObject(false);
    }

    @Test
    public void getCache_whenOtherHazelcastExceptionIsThrown_thenFail() {
        // when one attempts to getCache but a HazelcastException other than ServiceNotFoundException is thrown
        HazelcastInstanceImpl hzInstanceImpl = Mockito.mock(HazelcastInstanceImpl.class);
        Mockito.when(hzInstanceImpl.getDistributedObject(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new HazelcastException("mock hz exception"));
        // then the thrown HazelcastException is rethrown by getCache
        ICacheManager hzCacheManager = new com.hazelcast.instance.HazelcastInstanceCacheManager(hzInstanceImpl);
        thrown.expect(HazelcastException.class);
        hzCacheManager.getCache("any-cache");
    }

    @Test
    public void cacheConfigIsAvailableOnAllMembers_afterGetCacheCompletes() {
        Config config = createConfig();
        config.addCacheConfig(createCacheSimpleConfig(CacheThroughHazelcastInstanceTest.CACHE_NAME));
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = instanceFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = instanceFactory.newHazelcastInstance(config);
        ICacheService cacheServiceOnInstance2 = HazelcastTestSupport.getNodeEngineImpl(instance2).getService(SERVICE_NAME);
        CacheThroughHazelcastInstanceTest.retrieveCache(instance1, true);
        Assert.assertNotNull("Cache config was not available on other instance after cache proxy was created", cacheServiceOnInstance2.getCacheConfig(((HazelcastCacheManager.CACHE_MANAGER_PREFIX) + (CacheThroughHazelcastInstanceTest.CACHE_NAME))));
    }

    private static class NonJCacheAwareClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith("javax.cache.")) {
                throw new ClassNotFoundException((("Couldn't load class " + name) + ". Because JCache is disabled!"));
            }
            return super.loadClass(name);
        }
    }
}

