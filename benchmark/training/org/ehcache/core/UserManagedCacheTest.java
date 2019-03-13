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
package org.ehcache.core;


import org.ehcache.config.CacheConfiguration;
import org.ehcache.core.config.BaseCacheConfiguration;
import org.ehcache.core.config.ResourcePoolsHelper;
import org.ehcache.core.events.CacheEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.resilience.ResilienceStrategy;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class UserManagedCacheTest {
    @Test
    public void testUserManagedCacheDelegatesLifecycleCallsToStore() throws Exception {
        final Store store = Mockito.mock(Store.class);
        CacheConfiguration<Object, Object> config = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Ehcache ehcache = new Ehcache(config, store, Mockito.mock(ResilienceStrategy.class), Mockito.mock(CacheEventDispatcher.class), LoggerFactory.getLogger(((Ehcache.class) + "testUserManagedCacheDelegatesLifecycleCallsToStore")));
        assertCacheDelegatesLifecycleCallsToStore(ehcache);
        Ehcache ehcacheWithLoaderWriter = new Ehcache(config, store, Mockito.mock(ResilienceStrategy.class), Mockito.mock(CacheEventDispatcher.class), LoggerFactory.getLogger(((Ehcache.class) + "testUserManagedCacheDelegatesLifecycleCallsToStore")), Mockito.mock(CacheLoaderWriter.class));
        assertCacheDelegatesLifecycleCallsToStore(ehcacheWithLoaderWriter);
    }

    @Test
    public void testUserManagedEhcacheFailingTransitionGoesToLowestStatus() throws Exception {
        final Store store = Mockito.mock(Store.class);
        CacheConfiguration<Object, Object> config = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Ehcache ehcache = new Ehcache(config, store, Mockito.mock(ResilienceStrategy.class), Mockito.mock(CacheEventDispatcher.class), LoggerFactory.getLogger(((Ehcache.class) + "testUserManagedEhcacheFailingTransitionGoesToLowestStatus")));
        assertFailingTransitionGoesToLowestStatus(ehcache);
        Ehcache ehcacheWithLoaderWriter = new Ehcache(config, store, Mockito.mock(ResilienceStrategy.class), Mockito.mock(CacheEventDispatcher.class), LoggerFactory.getLogger(((Ehcache.class) + "testUserManagedCacheDelegatesLifecycleCallsToStore")), Mockito.mock(CacheLoaderWriter.class));
        assertFailingTransitionGoesToLowestStatus(ehcacheWithLoaderWriter);
    }
}

