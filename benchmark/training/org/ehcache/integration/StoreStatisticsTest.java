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
package org.ehcache.integration;


import java.io.File;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.store.StoreStatisticsConfiguration;
import org.ehcache.core.statistics.AuthoritativeTierOperationOutcomes;
import org.ehcache.core.statistics.CachingTierOperationOutcomes;
import org.ehcache.core.statistics.LowerCachingTierOperationsOutcome;
import org.ehcache.core.statistics.StoreOperationOutcomes;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.MISS;


/**
 *
 *
 * @author Ludovic Orban
 */
public class StoreStatisticsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void test1TierStoreStatsAvailableInContextManager() throws Exception {
        try (CacheManager cacheManager = // explicitly enable statistics
        CacheManagerBuilder.newCacheManagerBuilder().withCache("threeTieredCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(1)).add(new StoreStatisticsConfiguration(true))).build(true)) {
            Cache<Long, String> cache = cacheManager.getCache("threeTieredCache", Long.class, String.class);
            Assert.assertNull(cache.get(0L));
            long onHeapMisses = StoreStatisticsTest.<StoreOperationOutcomes.GetOutcome>findStat(cache, "get", "OnHeap").count(MISS);
            Assert.assertThat(onHeapMisses, Matchers.equalTo(1L));
        }
    }

    @Test
    public void test1TierStoreStatsAvailableInContextManager_disabledByDefault() throws Exception {
        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("threeTieredCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(1))).build(true)) {
            Cache<Long, String> cache = cacheManager.getCache("threeTieredCache", Long.class, String.class);
            Assert.assertNull(cache.get(0L));
            Assert.assertNull("Statistics are disabled so nothing is expected here", StoreStatisticsTest.<StoreOperationOutcomes.GetOutcome>findStat(cache, "get", "OnHeap"));
        }
    }

    @Test
    public void test2TiersStoreStatsAvailableInContextManager() throws Exception {
        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("threeTieredCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB))).build(true)) {
            Cache<Long, String> cache = cacheManager.getCache("threeTieredCache", Long.class, String.class);
            Assert.assertNull(cache.get(0L));
            long onHeapMisses = StoreStatisticsTest.<CachingTierOperationOutcomes.GetOrComputeIfAbsentOutcome>findStat(cache, "getOrComputeIfAbsent", "OnHeap").count(CachingTierOperationOutcomes.GetOrComputeIfAbsentOutcome.MISS);
            Assert.assertThat(onHeapMisses, Matchers.equalTo(1L));
            long offheapMisses = StoreStatisticsTest.<AuthoritativeTierOperationOutcomes.GetAndFaultOutcome>findStat(cache, "getAndFault", "OffHeap").count(AuthoritativeTierOperationOutcomes.GetAndFaultOutcome.MISS);
            Assert.assertThat(offheapMisses, Matchers.equalTo(1L));
        }
    }

    @Test
    public void test3TiersStoreStatsAvailableInContextManager() throws Exception {
        try (PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(new File(getStoragePath(), "StoreStatisticsTest"))).withCache("threeTieredCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB).disk(5, MemoryUnit.MB))).build(true)) {
            Cache<Long, String> cache = cacheManager.getCache("threeTieredCache", Long.class, String.class);
            Assert.assertNull(cache.get(0L));
            long onHeapMisses = StoreStatisticsTest.<CachingTierOperationOutcomes.GetOrComputeIfAbsentOutcome>findStat(cache, "getOrComputeIfAbsent", "OnHeap").count(CachingTierOperationOutcomes.GetOrComputeIfAbsentOutcome.MISS);
            Assert.assertThat(onHeapMisses, Matchers.equalTo(1L));
            long offHeapMisses = StoreStatisticsTest.<LowerCachingTierOperationsOutcome.GetAndRemoveOutcome>findStat(cache, "getAndRemove", "OffHeap").count(LowerCachingTierOperationsOutcome.GetAndRemoveOutcome.MISS);
            Assert.assertThat(offHeapMisses, Matchers.equalTo(1L));
            long diskMisses = StoreStatisticsTest.<AuthoritativeTierOperationOutcomes.GetAndFaultOutcome>findStat(cache, "getAndFault", "Disk").count(AuthoritativeTierOperationOutcomes.GetAndFaultOutcome.MISS);
            Assert.assertThat(diskMisses, Matchers.equalTo(1L));
        }
    }
}

