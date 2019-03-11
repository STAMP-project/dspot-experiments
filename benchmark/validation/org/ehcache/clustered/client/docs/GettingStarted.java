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
package org.ehcache.clustered.client.docs;


import Consistency.STRONG;
import java.net.URI;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.xml.XmlConfiguration;
import org.junit.Test;


/**
 * Samples demonstrating use of a clustered cache.
 */
public class GettingStarted {
    @Test
    public void clusteredCacheManagerExample() throws Exception {
        // tag::clusteredCacheManagerExample[]
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = // <1>
        CacheManagerBuilder.newCacheManagerBuilder().with(// <2>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate());// <3>

        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);// <4>

        cacheManager.close();// <5>

        // end::clusteredCacheManagerExample[]
    }

    @Test
    public void clusteredCacheManagerWithServerSideConfigExample() throws Exception {
        // tag::clusteredCacheManagerWithServerSideConfigExample[]
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = // <6>
        // <5>
        // <3>
        CacheManagerBuilder.newCacheManagerBuilder().with(// <2>
        // <1>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().defaultServerResource("primary-server-resource").resourcePool("resource-pool-a", 8, MemoryUnit.MB, "secondary-server-resource").resourcePool("resource-pool-b", 10, MemoryUnit.MB)).withCache("clustered-cache", // <4>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB)))).withCache("shared-cache-1", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-a")))).withCache("shared-cache-2", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool-a"))));// <7>

        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);// <8>

        cacheManager.close();
        // end::clusteredCacheManagerWithServerSideConfigExample[]
    }

    @Test
    public void clusteredCacheManagerWithDynamicallyAddedCacheExample() throws Exception {
        // tag::clusteredCacheManagerWithDynamicallyAddedCacheExample[]
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().defaultServerResource("primary-server-resource").resourcePool("resource-pool-a", 8, MemoryUnit.MB));
        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            CacheConfiguration<Long, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).build();
            Cache<Long, String> cache = cacheManager.createCache("clustered-cache", config);
        } finally {
            cacheManager.close();
        }
        // end::clusteredCacheManagerWithDynamicallyAddedCacheExample[]
    }

    @Test
    public void explicitConsistencyConfiguration() throws Exception {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().defaultServerResource("primary-server-resource").resourcePool("resource-pool-a", 8, MemoryUnit.MB));
        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            // tag::clusteredCacheConsistency[]
            CacheConfiguration<Long, String> config = // <1>
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)).build();
            Cache<Long, String> cache = cacheManager.createCache("clustered-cache", config);
            cache.put(42L, "All you need to know!");// <2>

            // end::clusteredCacheConsistency[]
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void clusteredCacheTieredExample() throws Exception {
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().defaultServerResource("primary-server-resource").resourcePool("resource-pool-a", 8, MemoryUnit.MB));
        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            // tag::clusteredCacheTieredExample[]
            CacheConfiguration<Long, String> config = // <2>
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, // <1>
            ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, MemoryUnit.MB).with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB))).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)).build();
            Cache<Long, String> cache = cacheManager.createCache("clustered-cache-tiered", config);
            cache.put(42L, "All you need to know!");
            // end::clusteredCacheTieredExample[]
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void clusteredCacheManagerLifecycleExamples() throws Exception {
        // tag::clusteredCacheManagerLifecycle[]
        CacheManagerBuilder<PersistentCacheManager> autoCreate = CacheManagerBuilder.newCacheManagerBuilder().with(// <1>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().resourcePool("resource-pool", 8, MemoryUnit.MB, "primary-server-resource")).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool"))));
        CacheManagerBuilder<PersistentCacheManager> expecting = CacheManagerBuilder.newCacheManagerBuilder().with(// <2>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).expecting().resourcePool("resource-pool", 8, MemoryUnit.MB, "primary-server-resource")).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool"))));
        CacheManagerBuilder<PersistentCacheManager> configless = // <3>
        CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application"))).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("resource-pool"))));
        // end::clusteredCacheManagerLifecycle[]
        autoCreate.build(true).close();
        expecting.build(true).close();
        configless.build(true).close();
    }

    @Test
    public void loadDocsXml() throws Exception {
        new XmlConfiguration(getClass().getResource("/configs/docs/ehcache-clustered.xml"));
    }

    @Test
    public void unknownClusteredCacheExample() {
        // tag::unspecifiedClusteredCacheExample[]
        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilderAutoCreate = CacheManagerBuilder.newCacheManagerBuilder().with(// <1>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate().resourcePool("resource-pool", 8, MemoryUnit.MB, "primary-server-resource"));
        PersistentCacheManager cacheManager1 = cacheManagerBuilderAutoCreate.build(false);
        cacheManager1.init();
        CacheConfiguration<Long, String> cacheConfigDedicated = // <2>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB))).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)).build();
        Cache<Long, String> cacheDedicated = cacheManager1.createCache("my-dedicated-cache", cacheConfigDedicated);// <3>

        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilderExpecting = CacheManagerBuilder.newCacheManagerBuilder().with(// <4>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).expecting().resourcePool("resource-pool", 8, MemoryUnit.MB, "primary-server-resource"));
        PersistentCacheManager cacheManager2 = cacheManagerBuilderExpecting.build(false);
        cacheManager2.init();
        CacheConfiguration<Long, String> cacheConfigUnspecified = // <5>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clustered())).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)).build();
        Cache<Long, String> cacheUnspecified = cacheManager2.createCache("my-dedicated-cache", cacheConfigUnspecified);// <6>

        // end::unspecifiedClusteredCacheExample[]
        cacheManager1.close();
        cacheManager2.close();
    }
}

