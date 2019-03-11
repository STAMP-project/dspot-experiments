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


import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.internal.statistics.DefaultStatisticsService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Provides basic tests for creation of a cache using a {@link org.ehcache.clustered.client.internal.store.ClusteredStore ClusteredStore}.
 */
public class BasicClusteredCacheTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/my-application");

    @Test
    public void testClusteredCacheSingleClient() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache = cacheManager.getCache("clustered-cache", Long.class, String.class);
        cache.put(1L, "value");
        Assert.assertThat(cache.get(1L), Matchers.is("value"));
        cacheManager.close();
    }

    @Test
    public void testClusteredCacheTwoClients() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).add(new org.ehcache.clustered.client.config.ClusteredStoreConfiguration(Consistency.STRONG)));
        final PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true);
        final PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
        final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        cache1.put(1L, "value1");
        Assert.assertThat(cache2.get(1L), Matchers.is("value1"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value1"));
        cache1.put(1L, "value2");
        Assert.assertThat(cache2.get(1L), Matchers.is("value2"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value2"));
        cacheManager2.close();
        cacheManager1.close();
    }

    @Test
    public void testClustered3TierCacheTwoClients() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).add(new org.ehcache.clustered.client.config.ClusteredStoreConfiguration(Consistency.STRONG)));
        final PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true);
        final PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
        final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        cache1.put(1L, "value1");
        cache1.put(2L, "value2");
        cache1.put(3L, "value3");
        Assert.assertThat(cache2.get(1L), Matchers.is("value1"));
        Assert.assertThat(cache2.get(2L), Matchers.is("value2"));
        Assert.assertThat(cache2.get(3L), Matchers.is("value3"));
        Assert.assertThat(cache2.get(1L), Matchers.is("value1"));
        Assert.assertThat(cache2.get(2L), Matchers.is("value2"));
        Assert.assertThat(cache2.get(3L), Matchers.is("value3"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value1"));
        Assert.assertThat(cache1.get(2L), Matchers.is("value2"));
        Assert.assertThat(cache1.get(3L), Matchers.is("value3"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value1"));
        Assert.assertThat(cache1.get(2L), Matchers.is("value2"));
        Assert.assertThat(cache1.get(3L), Matchers.is("value3"));
        cache1.put(1L, "value11");
        cache1.put(2L, "value12");
        cache1.put(3L, "value13");
        Assert.assertThat(cache2.get(1L), Matchers.is("value11"));
        Assert.assertThat(cache2.get(2L), Matchers.is("value12"));
        Assert.assertThat(cache2.get(3L), Matchers.is("value13"));
        Assert.assertThat(cache2.get(1L), Matchers.is("value11"));
        Assert.assertThat(cache2.get(2L), Matchers.is("value12"));
        Assert.assertThat(cache2.get(3L), Matchers.is("value13"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value11"));
        Assert.assertThat(cache1.get(2L), Matchers.is("value12"));
        Assert.assertThat(cache1.get(3L), Matchers.is("value13"));
        Assert.assertThat(cache1.get(1L), Matchers.is("value11"));
        Assert.assertThat(cache1.get(2L), Matchers.is("value12"));
        Assert.assertThat(cache1.get(3L), Matchers.is("value13"));
        cacheManager2.close();
        cacheManager1.close();
    }

    @Test
    public void testTieredClusteredCache() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(2).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache = cacheManager.getCache("clustered-cache", Long.class, String.class);
        cache.put(1L, "value");
        Assert.assertThat(cache.get(1L), Matchers.is("value"));
        cacheManager.close();
    }

    @Test
    public void testClusteredCacheWithSerializableValue() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, BasicClusteredCacheTest.Person.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        Cache<Long, BasicClusteredCacheTest.Person> cache = cacheManager.getCache("clustered-cache", Long.class, BasicClusteredCacheTest.Person.class);
        cache.put(38L, new BasicClusteredCacheTest.Person("Clustered Joe", 28));
        cacheManager.close();
        cacheManager = clusteredCacheManagerBuilder.build(true);
        cache = cacheManager.getCache("clustered-cache", Long.class, BasicClusteredCacheTest.Person.class);
        Assert.assertThat(cache.get(38L).name, Matchers.is("Clustered Joe"));
    }

    @Test
    public void testLargeValues() throws Exception {
        DefaultStatisticsService statisticsService = new DefaultStatisticsService();
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().using(statisticsService).with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheTest.CLUSTER_URI).autoCreate()).withCache("small-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, BigInteger.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("secondary-server-resource", 4, MemoryUnit.MB))));
        // The idea here is to add big things in the cache, and cause eviction of them to see if something crashes
        try (PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true)) {
            Cache<Long, BigInteger> cache = cacheManager.getCache("small-cache", Long.class, BigInteger.class);
            Random random = new Random();
            for (long i = 0; i < 100; i++) {
                BigInteger value = new BigInteger((((30 * 1024) * 128) * (1 + (random.nextInt(10)))), random);
                cache.put(i, value);
            }
        }
    }

    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        final String name;

        final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}

