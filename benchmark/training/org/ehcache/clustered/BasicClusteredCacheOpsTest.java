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
package org.ehcache.clustered;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.testing.rules.Cluster;


public class BasicClusteredCacheOpsTest extends ClusteredTests {
    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + ((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>\n");

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(BasicClusteredCacheOpsTest.RESOURCE_CONFIG).build();

    @Test
    public void basicCacheCRUD() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(BasicClusteredCacheOpsTest.CLUSTER.getConnectionURI().resolve("/crud-cm")).autoCreate().defaultServerResource("primary-server-resource"));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            CacheConfiguration<Long, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 1, MemoryUnit.MB))).build();
            Cache<Long, String> cache = cacheManager.createCache("clustered-cache", config);
            cache.put(1L, "The one");
            Assert.assertThat(cache.containsKey(2L), Matchers.is(false));
            cache.put(2L, "The two");
            Assert.assertThat(cache.containsKey(2L), Matchers.is(true));
            cache.put(1L, "Another one");
            cache.put(3L, "The three");
            Assert.assertThat(cache.get(1L), Matchers.equalTo("Another one"));
            Assert.assertThat(cache.get(2L), Matchers.equalTo("The two"));
            Assert.assertThat(cache.get(3L), Matchers.equalTo("The three"));
            cache.remove(1L);
            Assert.assertThat(cache.get(1L), Matchers.is(Matchers.nullValue()));
            cache.clear();
            Assert.assertThat(cache.get(1L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(cache.get(2L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(cache.get(3L), Matchers.is(Matchers.nullValue()));
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void basicCacheCAS() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(cluster(BasicClusteredCacheOpsTest.CLUSTER.getConnectionURI().resolve("/cas-cm")).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).add(new org.ehcache.clustered.client.config.ClusteredStoreConfiguration(Consistency.STRONG)));
        try (PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true)) {
            try (PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true)) {
                final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
                final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
                Assert.assertThat(cache1.putIfAbsent(1L, "one"), Matchers.nullValue());
                Assert.assertThat(cache2.putIfAbsent(1L, "another one"), Matchers.is("one"));
                Assert.assertThat(cache2.remove(1L, "another one"), Matchers.is(false));
                Assert.assertThat(cache1.replace(1L, "another one"), Matchers.is("one"));
                Assert.assertThat(cache2.replace(1L, "another one", "yet another one"), Matchers.is(true));
                Assert.assertThat(cache1.remove(1L, "yet another one"), Matchers.is(true));
                Assert.assertThat(cache2.replace(1L, "one"), Matchers.nullValue());
                Assert.assertThat(cache1.replace(1L, "another one", "yet another one"), Matchers.is(false));
            }
        }
    }

    @Test
    public void basicClusteredBulk() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(cluster(BasicClusteredCacheOpsTest.CLUSTER.getConnectionURI().resolve("/bulk-cm")).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).add(new org.ehcache.clustered.client.config.ClusteredStoreConfiguration(Consistency.STRONG)));
        try (PersistentCacheManager cacheManager1 = clusteredCacheManagerBuilder.build(true)) {
            try (PersistentCacheManager cacheManager2 = clusteredCacheManagerBuilder.build(true)) {
                final Cache<Long, String> cache1 = cacheManager1.getCache("clustered-cache", Long.class, String.class);
                final Cache<Long, String> cache2 = cacheManager2.getCache("clustered-cache", Long.class, String.class);
                Map<Long, String> entriesMap = new HashMap<>();
                entriesMap.put(1L, "one");
                entriesMap.put(2L, "two");
                entriesMap.put(3L, "three");
                cache1.putAll(entriesMap);
                Set<Long> keySet = new HashSet<>(Arrays.asList(1L, 2L, 3L));
                Map<Long, String> all = cache2.getAll(keySet);
                Assert.assertThat(all.get(1L), Matchers.is("one"));
                Assert.assertThat(all.get(2L), Matchers.is("two"));
                Assert.assertThat(all.get(3L), Matchers.is("three"));
                Map<Long, String> entries1 = new HashMap<>();
                Assert.assertThat(cache1, iterableWithSize(3));
                cache1.forEach(( e) -> entries1.putIfAbsent(e.getKey(), e.getValue()));
                Assert.assertThat(entries1, Matchers.hasEntry(1L, "one"));
                Assert.assertThat(entries1, Matchers.hasEntry(2L, "two"));
                Assert.assertThat(entries1, Matchers.hasEntry(3L, "three"));
                Map<Long, String> entries2 = new HashMap<>();
                Assert.assertThat(cache2, iterableWithSize(3));
                cache2.forEach(( e) -> entries2.putIfAbsent(e.getKey(), e.getValue()));
                Assert.assertThat(entries2, Matchers.hasEntry(1L, "one"));
                Assert.assertThat(entries2, Matchers.hasEntry(2L, "two"));
                Assert.assertThat(entries2, Matchers.hasEntry(3L, "three"));
                cache2.removeAll(keySet);
                all = cache1.getAll(keySet);
                Assert.assertThat(all.get(1L), Matchers.nullValue());
                Assert.assertThat(all.get(2L), Matchers.nullValue());
                Assert.assertThat(all.get(3L), Matchers.nullValue());
            }
        }
    }
}

