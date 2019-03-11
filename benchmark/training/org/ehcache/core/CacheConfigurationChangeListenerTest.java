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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.core.config.ResourcePoolsHelper;
import org.ehcache.core.events.CacheEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author rism
 */
public class CacheConfigurationChangeListenerTest {
    private Store<Object, Object> store;

    private CacheEventDispatcher<Object, Object> eventNotifier;

    private EhcacheRuntimeConfiguration<Object, Object> runtimeConfiguration;

    private CacheConfiguration<Object, Object> config;

    private Ehcache<Object, Object> cache;

    @Test
    public void testCacheConfigurationChangeFiresEvent() {
        CacheConfigurationChangeListenerTest.Listener configurationListener = new CacheConfigurationChangeListenerTest.Listener();
        List<CacheConfigurationChangeListener> cacheConfigurationChangeListeners = new ArrayList<>();
        cacheConfigurationChangeListeners.add(configurationListener);
        this.runtimeConfiguration.addCacheConfigurationListener(cacheConfigurationChangeListeners);
        this.cache.getRuntimeConfiguration().updateResourcePools(ResourcePoolsHelper.createHeapOnlyPools(10));
        MatcherAssert.assertThat(configurationListener.eventSet.size(), Matchers.is(1));
    }

    @Test
    public void testRemovingCacheConfigurationListener() {
        CacheConfigurationChangeListenerTest.Listener configurationListener = new CacheConfigurationChangeListenerTest.Listener();
        List<CacheConfigurationChangeListener> cacheConfigurationChangeListeners = new ArrayList<>();
        cacheConfigurationChangeListeners.add(configurationListener);
        this.runtimeConfiguration.addCacheConfigurationListener(cacheConfigurationChangeListeners);
        this.cache.getRuntimeConfiguration().updateResourcePools(ResourcePoolsHelper.createHeapOnlyPools(20));
        MatcherAssert.assertThat(configurationListener.eventSet.size(), Matchers.is(1));
        this.runtimeConfiguration.removeCacheConfigurationListener(configurationListener);
        this.cache.getRuntimeConfiguration().updateResourcePools(ResourcePoolsHelper.createHeapOnlyPools(5));
        MatcherAssert.assertThat(configurationListener.eventSet.size(), Matchers.is(1));
    }

    private class Listener implements CacheConfigurationChangeListener {
        private final Set<CacheConfigurationChangeEvent> eventSet = new HashSet<>();

        @Override
        public void cacheConfigurationChange(CacheConfigurationChangeEvent event) {
            this.eventSet.add(event);
            Logger logger = LoggerFactory.getLogger((((Ehcache.class) + "-") + "GettingStarted"));
            logger.info(("Setting size: " + (event.getNewValue().toString())));
        }
    }
}

