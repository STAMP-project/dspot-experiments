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
import org.ehcache.Cache;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.Status;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CacheManagerDestroyTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/my-application");

    private static final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(CacheManagerDestroyTest.CLUSTER_URI).autoCreate());

    @Test
    public void testDestroyCacheManagerWithSingleClient() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = CacheManagerDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager.close();
        persistentCacheManager.destroy();
        Assert.assertThat(persistentCacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testCreateDestroyCreate() throws Exception {
        PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(CacheManagerDestroyTest.CLUSTER_URI).autoCreate().defaultServerResource("primary-server-resource")).withCache("my-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10).with(ClusteredResourcePoolBuilder.clusteredDedicated(2, MemoryUnit.MB)))).build(true);
        cacheManager.close();
        cacheManager.destroy();
        cacheManager.init();
    }

    @Test
    public void testDestroyCacheManagerWithMultipleClients() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager1 = CacheManagerDestroyTest.clusteredCacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = CacheManagerDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager1.close();
        try {
            persistentCacheManager1.destroy();
            Assert.fail("StateTransitionException expected");
        } catch (StateTransitionException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Couldn't acquire cluster-wide maintenance lease"));
        }
        Assert.assertThat(persistentCacheManager1.getStatus(), Matchers.is(Status.UNINITIALIZED));
        Assert.assertThat(persistentCacheManager2.getStatus(), Matchers.is(Status.AVAILABLE));
        Cache<Long, String> cache = persistentCacheManager2.createCache("test", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        cache.put(1L, "One");
        Assert.assertThat(cache.get(1L), Matchers.is("One"));
        persistentCacheManager2.close();
    }

    @Test
    public void testDestroyCacheManagerDoesNotAffectsExistingCacheWithExistingClientsConnected() throws CachePersistenceException {
        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilder = CacheManagerDestroyTest.clusteredCacheManagerBuilder.withCache("test", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        PersistentCacheManager persistentCacheManager1 = cacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = cacheManagerBuilder.build(true);
        persistentCacheManager1.close();
        try {
            persistentCacheManager1.destroy();
            Assert.fail("StateTransitionException expected");
        } catch (StateTransitionException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Couldn't acquire cluster-wide maintenance lease"));
        }
        Cache<Long, String> cache = persistentCacheManager2.getCache("test", Long.class, String.class);
        cache.put(1L, "One");
        Assert.assertThat(cache.get(1L), Matchers.is("One"));
        persistentCacheManager2.close();
    }

    @Test
    public void testCloseCacheManagerSingleClient() {
        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilder = CacheManagerDestroyTest.clusteredCacheManagerBuilder.withCache("test", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        PersistentCacheManager persistentCacheManager1 = cacheManagerBuilder.build(true);
        persistentCacheManager1.close();
        persistentCacheManager1.init();
        Cache<Long, String> cache = persistentCacheManager1.getCache("test", Long.class, String.class);
        cache.put(1L, "One");
        Assert.assertThat(cache.get(1L), Matchers.is("One"));
        persistentCacheManager1.close();
    }

    @Test
    public void testCloseCacheManagerMultipleClients() {
        CacheManagerBuilder<PersistentCacheManager> cacheManagerBuilder = CacheManagerDestroyTest.clusteredCacheManagerBuilder.withCache("test", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB))));
        PersistentCacheManager persistentCacheManager1 = cacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = cacheManagerBuilder.build(true);
        Cache<Long, String> cache = persistentCacheManager1.getCache("test", Long.class, String.class);
        cache.put(1L, "One");
        Assert.assertThat(cache.get(1L), Matchers.is("One"));
        persistentCacheManager1.close();
        Assert.assertThat(persistentCacheManager1.getStatus(), Matchers.is(Status.UNINITIALIZED));
        Cache<Long, String> cache2 = persistentCacheManager2.getCache("test", Long.class, String.class);
        Assert.assertThat(cache2.get(1L), Matchers.is("One"));
        persistentCacheManager2.close();
    }
}

