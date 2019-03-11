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
package org.ehcache.management.providers.statistics;


import java.io.IOException;
import java.util.List;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Builder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.core.config.store.StoreStatisticsConfiguration;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.management.ManagementRegistryService;
import org.ehcache.management.registry.DefaultManagementRegistryConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.terracotta.management.model.context.Context;


@RunWith(Parameterized.class)
public class StandardEhCacheStatisticsQueryTest {
    @Rule
    public final Timeout globalTimeout = Timeout.seconds(60);

    @Rule
    public final TemporaryFolder diskPath = new TemporaryFolder();

    private static final long CACHE_HIT_TOTAL = 4;

    private final ResourcePools resources;

    private final List<String> statNames;

    private final List<Long> tierExpectedValues;

    private final Long cacheExpectedValue;

    public StandardEhCacheStatisticsQueryTest(Builder<? extends ResourcePools> resources, List<String> statNames, List<Long> tierExpectedValues, Long cacheExpectedValue) {
        this.resources = resources.build();
        this.statNames = statNames;
        this.tierExpectedValues = tierExpectedValues;
        this.cacheExpectedValue = cacheExpectedValue;
    }

    @Test
    public void test() throws IOException {
        DefaultManagementRegistryConfiguration registryConfiguration = new DefaultManagementRegistryConfiguration().setCacheManagerAlias("myCacheManager");
        ManagementRegistryService managementRegistry = new org.ehcache.management.registry.DefaultManagementRegistryService(registryConfiguration);
        CacheConfiguration<Long, String> cacheConfiguration = // explicitly enable statistics to make sure they are there even when using only one tier
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, resources).withEvictionAdvisor(( key, value) -> key.equals(2L)).add(new StoreStatisticsConfiguration(true)).build();
        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", cacheConfiguration).using(managementRegistry).using(new DefaultPersistenceConfiguration(diskPath.newFolder())).build(true)) {
            Context context = StatsUtil.createContext(managementRegistry);
            Cache<Long, String> cache = cacheManager.getCache("myCache", Long.class, String.class);
            cache.put(1L, "1");// put in lowest tier

            cache.put(2L, "2");// put in lowest tier

            cache.put(3L, "3");// put in lowest tier

            cache.get(1L);// HIT lowest tier

            cache.get(2L);// HIT lowest tier

            cache.get(2L);// HIT highest tier

            cache.get(1L);// HIT middle/highest tier. Depends on tier configuration.

            long tierHitCountSum = 0;
            for (int i = 0; i < (statNames.size()); i++) {
                tierHitCountSum += getAndAssertExpectedValueFromCounter(statNames.get(i), context, managementRegistry, tierExpectedValues.get(i));
            }
            long cacheHitCount = getAndAssertExpectedValueFromCounter("Cache:HitCount", context, managementRegistry, cacheExpectedValue);
            Assert.assertThat(tierHitCountSum, CoreMatchers.is(cacheHitCount));
        }
    }
}

