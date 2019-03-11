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


import Consistency.STRONG;
import java.net.URI;
import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.internal.TimeSourceConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class BasicClusteredCacheExpiryTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/my-application");

    private static final CacheManagerBuilder<PersistentCacheManager> commonClusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheExpiryTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1L))).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)));

    @Test
    public void testGetExpiredSingleClient() {
        TestTimeSource timeSource = new TestTimeSource();
        TimeSourceConfiguration timeSourceConfiguration = new TimeSourceConfiguration(timeSource);
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = BasicClusteredCacheExpiryTest.commonClusteredCacheManagerBuilder.using(timeSourceConfiguration);
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache = cacheManager.getCache("clustered-cache", Long.class, String.class);
        cache.put(1L, "value");
        Assert.assertThat(cache.get(1L), Matchers.is("value"));
        timeSource.advanceTime(1);
        Assert.assertThat(cache.get(1L), Matchers.nullValue());
        cacheManager.close();
    }

    @Test
    public void testGetExpiredTwoClients() {
        TestTimeSource timeSource = new TestTimeSource();
        TimeSourceConfiguration timeSourceConfiguration = new TimeSourceConfiguration(timeSource);
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = BasicClusteredCacheExpiryTest.commonClusteredCacheManagerBuilder.using(timeSourceConfiguration);
        final PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true);
        final PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
        final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        cache1.put(1L, "value1");
        Assert.assertThat(cache1.get(1L), Matchers.is("value1"));
        timeSource.advanceTime(1L);
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        Assert.assertThat(cache1.get(1L), Matchers.nullValue());
        cacheManager2.close();
        cacheManager1.close();
    }

    @Test
    public void testContainsKeyExpiredTwoClients() {
        TestTimeSource timeSource = new TestTimeSource();
        TimeSourceConfiguration timeSourceConfiguration = new TimeSourceConfiguration(timeSource);
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = BasicClusteredCacheExpiryTest.commonClusteredCacheManagerBuilder.using(timeSourceConfiguration);
        final PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true);
        final PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
        final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        cache1.put(1L, "value1");
        Assert.assertThat(cache1.containsKey(1L), Matchers.is(true));
        timeSource.advanceTime(1L);
        Assert.assertThat(cache1.containsKey(1L), Matchers.is(false));
        Assert.assertThat(cache2.containsKey(1L), Matchers.is(false));
        cacheManager2.close();
        cacheManager1.close();
    }
}

