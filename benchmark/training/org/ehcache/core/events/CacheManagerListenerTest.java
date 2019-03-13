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
package org.ehcache.core.events;


import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.EhcacheManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class CacheManagerListenerTest {
    private static final String CACHE = "myCache";

    private EhcacheManager cacheManager;

    private CacheManagerListener cacheManagerListener;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCacheAdded() throws Exception {
        CacheConfigurationBuilder<Long, String> otherCacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).disk(2, MemoryUnit.MB));
        Cache<Long, String> otherCache = cacheManager.createCache("otherCache", otherCacheConfiguration);
        Mockito.verify(cacheManagerListener).cacheAdded("otherCache", otherCache);
    }

    @Test
    public void testCacheDestroyTriggersCacheRemoved() throws Exception {
        Cache<Long, String> cache = cacheManager.getCache(CacheManagerListenerTest.CACHE, Long.class, String.class);
        cacheManager.destroyCache(CacheManagerListenerTest.CACHE);
        Mockito.verify(cacheManagerListener).cacheRemoved(CacheManagerListenerTest.CACHE, cache);
    }

    @Test
    public void testCacheRemoved() throws Exception {
        Cache<Long, String> cache = cacheManager.getCache(CacheManagerListenerTest.CACHE, Long.class, String.class);
        cacheManager.removeCache(CacheManagerListenerTest.CACHE);
        Mockito.verify(cacheManagerListener).cacheRemoved(CacheManagerListenerTest.CACHE, cache);
    }
}

