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
package org.ehcache.clustered.client;


import java.net.URI;
import java.util.Map;
import org.ehcache.Cache;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.TierStatistics;
import org.ehcache.impl.internal.statistics.DefaultStatisticsService;
import org.junit.Test;


public class ClusteredCacheExpirationTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/my-application");

    private static final String CLUSTERED_CACHE = "clustered-cache";

    private TestTimeSource timeSource = new TestTimeSource();

    private StatisticsService statisticsService = new DefaultStatisticsService();

    @Test
    public void testGetExpirationPropagatedToHigherTiers() throws CachePersistenceException {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = cacheManagerBuilder(oneSecondExpiration());
        try (PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true)) {
            Map<String, TierStatistics> tierStatistics = statisticsService.getCacheStatistics(ClusteredCacheExpirationTest.CLUSTERED_CACHE).getTierStatistics();
            TierStatistics onheap = tierStatistics.get("OnHeap");
            TierStatistics offheap = tierStatistics.get("OffHeap");
            Cache<Long, String> cache = cacheManager.getCache(ClusteredCacheExpirationTest.CLUSTERED_CACHE, Long.class, String.class);
            for (long i = 0; i < 30; i++) {
                cache.put(i, "value");// store on the cluster

                cache.get(i);// push it up on heap and offheap tier

            }
            assertThat(onheap.getMappings()).isEqualTo(10);
            assertThat(offheap.getMappings()).isEqualTo(20);
            timeSource.advanceTime(1500);// go after expiration

            for (long i = 0; i < 30; i++) {
                assertThat(cache.get(i)).isEqualTo(null);// the value should have expired

            }
            assertThat(onheap.getMappings()).isEqualTo(0);
            assertThat(offheap.getMappings()).isEqualTo(0);
        }
    }

    @Test
    public void testGetNoExpirationPropagatedToHigherTiers() throws CachePersistenceException {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = cacheManagerBuilder(ExpiryPolicyBuilder.noExpiration());
        try (PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true)) {
            Map<String, TierStatistics> tierStatistics = statisticsService.getCacheStatistics(ClusteredCacheExpirationTest.CLUSTERED_CACHE).getTierStatistics();
            TierStatistics onheap = tierStatistics.get("OnHeap");
            TierStatistics offheap = tierStatistics.get("OffHeap");
            Cache<Long, String> cache = cacheManager.getCache(ClusteredCacheExpirationTest.CLUSTERED_CACHE, Long.class, String.class);
            for (long i = 0; i < 30; i++) {
                cache.put(i, "value");// store on the cluster

                cache.get(i);// push it up on heap and offheap tier

            }
            assertThat(onheap.getMappings()).isEqualTo(10);
            assertThat(offheap.getMappings()).isEqualTo(20);
        }
    }

    @Test
    public void testPutIfAbsentExpirationPropagatedToHigherTiers() throws CachePersistenceException {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = cacheManagerBuilder(oneSecondExpiration());
        try (PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true)) {
            Cache<Long, String> cache = cacheManager.getCache(ClusteredCacheExpirationTest.CLUSTERED_CACHE, Long.class, String.class);
            cache.put(1L, "value");// store on the cluster

            cache.putIfAbsent(1L, "newvalue");// push it up on heap tier

            timeSource.advanceTime(1500);// go after expiration

            assertThat(cache.get(1L)).isEqualTo(null);// the value should have expired

        }
    }
}

