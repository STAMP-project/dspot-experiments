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
package org.ehcache.clustered.loaderWriter;


import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CachePersistenceException;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.service.ClusterTierValidationException;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.junit.Assert;
import org.junit.Test;


public class BasicClusteredLoaderWriterTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/clustered-loader-writer");

    @Test
    public void testAllClientsNeedToHaveLoaderWriterConfigured() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        CacheConfiguration<Long, String> withoutLoaderWriter = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).withResilienceStrategy(new org.ehcache.core.internal.resilience.ThrowingResilienceStrategy()).build();
        try {
            CacheManager anotherManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", withoutLoaderWriter).build(true);
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause().getCause().getCause().getCause(), instanceOf(CachePersistenceException.class));
            Assert.assertThat(e.getCause().getCause().getCause().getCause().getCause(), instanceOf(ClusterTierValidationException.class));
        }
    }

    @Test
    public void testBasicClusteredCacheLoaderWriter() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        Cache<Long, String> cache = cacheManager.getCache("cache-1", Long.class, String.class);
        cache.put(1L, "1");
        Assert.assertThat(cache.get(1L), is("1"));
        Assert.assertThat(loaderWriter.storeMap.get(1L), is("1"));
    }

    @Test
    public void testLoaderWriterMultipleClients() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager1 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        CacheManager cacheManager2 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        Cache<Long, String> client1 = cacheManager1.getCache("cache-1", Long.class, String.class);
        Cache<Long, String> client2 = cacheManager2.getCache("cache-1", Long.class, String.class);
        client1.put(1L, "1");
        client2.put(1L, "2");
        Assert.assertThat(client1.get(1L), is("2"));
        Assert.assertThat(loaderWriter.storeMap.get(1L), is("2"));
        client1.remove(1L);
        Assert.assertThat(client2.get(1L), nullValue());
        Assert.assertThat(loaderWriter.storeMap.get(1L), nullValue());
    }

    @Test
    public void testCASOpsMultipleClients() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager1 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        CacheManager cacheManager2 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        Cache<Long, String> client1 = cacheManager1.getCache("cache-1", Long.class, String.class);
        Cache<Long, String> client2 = cacheManager2.getCache("cache-1", Long.class, String.class);
        Assert.assertThat(client1.putIfAbsent(1L, "1"), nullValue());
        Assert.assertThat(client2.putIfAbsent(1L, "2"), is("1"));
        Assert.assertThat(client1.get(1L), is("1"));
        Assert.assertThat(loaderWriter.storeMap.get(1L), is("1"));
        Assert.assertThat(client1.replace(1L, "2"), is("1"));
        Assert.assertThat(client2.replace(1L, "3"), is("2"));
        Assert.assertThat(client1.get(1L), is("3"));
        Assert.assertThat(loaderWriter.storeMap.get(1L), is("3"));
        Assert.assertThat(client1.replace(1L, "2", "4"), is(false));
        Assert.assertThat(client2.replace(1L, "3", "4"), is(true));
        Assert.assertThat(client1.get(1L), is("4"));
        Assert.assertThat(loaderWriter.storeMap.get(1L), is("4"));
        Assert.assertThat(client1.remove(1L, "5"), is(false));
        Assert.assertThat(client2.remove(1L, "4"), is(true));
    }

    @Test
    public void testBulkOps() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        Cache<Long, String> cache = cacheManager.getCache("cache-1", Long.class, String.class);
        Map<Long, String> mappings = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            mappings.put(((long) (i)), ("" + i));
        }
        cache.putAll(mappings);
        Assert.assertThat(loaderWriter.storeMap.keySet(), containsInAnyOrder(mappings.keySet().toArray()));
        cache.clear();
        Map<Long, String> loadedData = cache.getAll(mappings.keySet());
        Assert.assertThat(mappings.keySet(), containsInAnyOrder(loadedData.keySet().toArray()));
        cache.removeAll(mappings.keySet());
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), is(true));
    }

    @Test
    public void testCASOps() {
        TestCacheLoaderWriter loaderWriter = new TestCacheLoaderWriter();
        CacheConfiguration<Long, String> cacheConfiguration = getCacheConfiguration(loaderWriter);
        CacheManager cacheManager1 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        CacheManager cacheManager2 = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredLoaderWriterTest.CLUSTER_URI).autoCreate()).withCache("cache-1", cacheConfiguration).build(true);
        Cache<Long, String> client1 = cacheManager1.getCache("cache-1", Long.class, String.class);
        Cache<Long, String> client2 = cacheManager2.getCache("cache-1", Long.class, String.class);
        Assert.assertThat(loaderWriter.storeMap.isEmpty(), is(true));
        Set<Long> keys = new HashSet<>();
        ThreadLocalRandom.current().longs(10).forEach(( x) -> {
            keys.add(x);
            client1.put(x, Long.toString(x));
        });
        Assert.assertThat(loaderWriter.storeMap.size(), is(10));
        keys.forEach(( x) -> Assert.assertThat(client2.putIfAbsent(x, ("Again" + x)), is(Long.toString(x))));
        keys.stream().limit(5).forEach(( x) -> Assert.assertThat(client2.replace(x, ("Replaced" + x)), is(Long.toString(x))));
        keys.forEach(( x) -> client1.remove(x, Long.toString(x)));
        Assert.assertThat(loaderWriter.storeMap.size(), is(5));
    }
}

