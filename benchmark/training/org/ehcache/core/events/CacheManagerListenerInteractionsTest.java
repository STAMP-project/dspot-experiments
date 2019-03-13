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
import org.ehcache.Status;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.EhcacheManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CacheManagerListenerInteractionsTest {
    public static final String CACHE_NAME = "myCache";

    @Test
    public void testCacheManagerListener_called_after_configuration_updated() throws Exception {
        EhcacheManager cacheManager = ((EhcacheManager) (CacheManagerBuilder.newCacheManagerBuilder().build()));
        CacheManagerListener cacheManagerListener = Mockito.spy(new CacheManagerListenerInteractionsTest.AssertiveCacheManagerListener(cacheManager.getRuntimeConfiguration()));
        cacheManager.registerListener(cacheManagerListener);
        cacheManager.init();
        CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB)).build();
        cacheManager.createCache(CacheManagerListenerInteractionsTest.CACHE_NAME, cacheConfiguration);
        Mockito.verify(cacheManagerListener).cacheAdded(ArgumentMatchers.eq(CacheManagerListenerInteractionsTest.CACHE_NAME), ((Cache<?, ?>) (ArgumentMatchers.isNotNull())));
        cacheManager.removeCache(CacheManagerListenerInteractionsTest.CACHE_NAME);
        Mockito.verify(cacheManagerListener).cacheRemoved(ArgumentMatchers.eq(CacheManagerListenerInteractionsTest.CACHE_NAME), ((Cache<?, ?>) (ArgumentMatchers.isNotNull())));
    }

    static class AssertiveCacheManagerListener implements CacheManagerListener {
        private final Configuration configuration;

        public AssertiveCacheManagerListener(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public void cacheAdded(String alias, Cache<?, ?> cache) {
            // when this callback is reached, the configuration should already know about the cache
            Assert.assertThat(configuration.getCacheConfigurations().keySet().contains(CacheManagerListenerInteractionsTest.CACHE_NAME), Matchers.is(true));
        }

        @Override
        public void cacheRemoved(String alias, Cache<?, ?> cache) {
            // when this callback is reached, the configuration should no longer know about the cache
            Assert.assertThat(configuration.getCacheConfigurations().keySet().contains(CacheManagerListenerInteractionsTest.CACHE_NAME), Matchers.is(false));
        }

        @Override
        public void stateTransition(Status from, Status to) {
        }
    }
}

