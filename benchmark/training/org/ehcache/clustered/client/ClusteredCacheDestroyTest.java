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
import org.ehcache.Cache;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.Status;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ClusteredCacheDestroyTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/my-application");

    private static final String CLUSTERED_CACHE = "clustered-cache";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(ClusteredCacheDestroyTest.CLUSTER_URI).autoCreate()).withCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB))).add(ClusteredStoreConfigurationBuilder.withConsistency(STRONG)));

    @Test
    public void testDestroyCacheWhenSingleClientIsConnected() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        final Cache<Long, String> cache = persistentCacheManager.getCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE, Long.class, String.class);
        Assert.assertThat(cache, Matchers.nullValue());
        persistentCacheManager.close();
    }

    @Test
    public void testDestroyFreesUpTheAllocatedResource() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        CacheConfigurationBuilder<Long, String> configBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 10, MemoryUnit.MB)));
        try {
            Cache<Long, String> anotherCache = persistentCacheManager.createCache("another-cache", configBuilder);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Cache 'another-cache' creation in EhcacheManager failed."));
        }
        persistentCacheManager.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        Cache<Long, String> anotherCache = persistentCacheManager.createCache("another-cache", configBuilder);
        anotherCache.put(1L, "One");
        Assert.assertThat(anotherCache.get(1L), Matchers.is("One"));
        persistentCacheManager.close();
    }

    @Test
    public void testDestroyUnknownCacheAlias() throws Exception {
        ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true).close();
        PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(ClusteredCacheDestroyTest.CLUSTER_URI).expecting()).build(true);
        cacheManager.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        try {
            cacheManager.createCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clustered())));
            Assert.fail("Expected exception as clustered store no longer exists");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(ClusteredCacheDestroyTest.CLUSTERED_CACHE));
        }
        cacheManager.close();
    }

    @Test
    public void testDestroyNonExistentCache() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        String nonExistent = "this-is-not-the-cache-you-are-looking-for";
        Assert.assertThat(persistentCacheManager.getCache(nonExistent, Long.class, String.class), Matchers.nullValue());
        persistentCacheManager.destroyCache(nonExistent);
        persistentCacheManager.close();
    }

    @Test
    public void testDestroyCacheWhenMultipleClientsConnected() {
        PersistentCacheManager persistentCacheManager1 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        final Cache<Long, String> cache1 = persistentCacheManager1.getCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE, Long.class, String.class);
        final Cache<Long, String> cache2 = persistentCacheManager2.getCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE, Long.class, String.class);
        try {
            persistentCacheManager1.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
            Assert.fail();
        } catch (CachePersistenceException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot destroy cluster tier"));
        }
        try {
            cache1.put(1L, "One");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("State is UNINITIALIZED"));
        }
        Assert.assertThat(cache2.get(1L), Matchers.nullValue());
        cache2.put(1L, "One");
        Assert.assertThat(cache2.get(1L), Matchers.is("One"));
        persistentCacheManager1.close();
        persistentCacheManager2.close();
    }

    @Test
    public void testDestroyCacheWithCacheManagerStopped() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager.close();
        persistentCacheManager.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        Assert.assertThat(persistentCacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testDestroyNonExistentCacheWithCacheManagerStopped() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager.close();
        persistentCacheManager.destroyCache("this-is-not-the-cache-you-are-looking-for");
        Assert.assertThat(persistentCacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testDestroyCacheOnNonExistentCacheManager() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager.close();
        persistentCacheManager.destroy();
        persistentCacheManager.destroyCache("this-is-not-the-cache-you-are-looking-for");
        Assert.assertThat(persistentCacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testDestroyCacheWithTwoCacheManagerOnSameCache_forbiddenWhenInUse() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager1 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        expectedException.expect(CachePersistenceException.class);
        expectedException.expectMessage("Cannot destroy cluster tier 'clustered-cache': in use by other client(s)");
        persistentCacheManager1.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
    }

    @Test
    public void testDestroyCacheWithTwoCacheManagerOnSameCache_firstRemovesSecondDestroy() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager1 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        PersistentCacheManager persistentCacheManager2 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager2.removeCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        persistentCacheManager1.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
    }

    @Test
    public void testDestroyCacheWithTwoCacheManagerOnSameCache_secondDoesntHaveTheCacheButPreventExclusiveAccessToCluster() throws CachePersistenceException {
        PersistentCacheManager persistentCacheManager1 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(false);
        PersistentCacheManager persistentCacheManager2 = ClusteredCacheDestroyTest.clusteredCacheManagerBuilder.build(true);
        persistentCacheManager2.removeCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
        persistentCacheManager1.destroyCache(ClusteredCacheDestroyTest.CLUSTERED_CACHE);
    }
}

