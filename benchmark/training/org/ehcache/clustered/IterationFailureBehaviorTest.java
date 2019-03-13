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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.ehcache.Cache;
import org.ehcache.CacheIterationException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.store.ServerStoreProxyException;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.exception.ConnectionClosedException;
import org.terracotta.testing.rules.Cluster;


public class IterationFailureBehaviorTest extends ClusteredTests {
    private static final int KEYS = 100;

    private static final String RESOURCE_CONFIG = "<config xmlns:ohr='http://www.terracotta.org/config/offheap-resource'>" + (((((((("<ohr:offheap-resources>" + "<ohr:resource name=\"primary-server-resource\" unit=\"MB\">64</ohr:resource>") + "</ohr:offheap-resources>") + "</config>") + "<service xmlns:lease='http://www.terracotta.org/service/lease'>") + "<lease:connection-leasing>") + "<lease:lease-length unit='seconds'>5</lease:lease-length>") + "</lease:connection-leasing>") + "</service>");

    @ClassRule
    public static Cluster CLUSTER = newCluster(2).in(new File("build/cluster")).withServiceFragment(IterationFailureBehaviorTest.RESOURCE_CONFIG).build();

    @Test
    public void testIteratorFailover() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(IterationFailureBehaviorTest.CLUSTER.getConnectionURI().resolve("/iterator-cm")).autoCreate().defaultServerResource("primary-server-resource"));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            CacheConfiguration<Long, String> smallConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 1, MemoryUnit.MB))).build();
            Cache<Long, String> smallCache = cacheManager.createCache("small-cache", smallConfig);
            LongStream.range(0, IterationFailureBehaviorTest.KEYS).forEach(( k) -> smallCache.put(k, Long.toString(k)));
            CacheConfiguration<Long, byte[]> largeConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, byte[].class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 4, MemoryUnit.MB))).build();
            Cache<Long, byte[]> largeCache = cacheManager.createCache("large-cache", largeConfig);
            byte[] value = new byte[10 * 1024];
            LongStream.range(0, IterationFailureBehaviorTest.KEYS).forEach(( k) -> {
                largeCache.put(k, value);
            });
            Map<Long, String> smallMap = new HashMap<>();
            Iterator<Cache.Entry<Long, String>> smallIterator = smallCache.iterator();
            Cache.Entry<Long, String> smallNext = smallIterator.next();
            smallMap.put(smallNext.getKey(), smallNext.getValue());
            Iterator<Cache.Entry<Long, byte[]>> largeIterator = largeCache.iterator();
            Cache.Entry<Long, byte[]> largeNext = largeIterator.next();
            Assert.assertThat(largeCache.get(largeNext.getKey()), IsNull.notNullValue());
            IterationFailureBehaviorTest.CLUSTER.getClusterControl().terminateActive();
            // large iterator fails
            try {
                largeIterator.forEachRemaining(( k) -> {
                });
                Assert.fail("Expected CacheIterationException");
            } catch (CacheIterationException e) {
                Assert.assertThat(e.getCause(), Matchers.instanceOf(StoreAccessException.class));
                Assert.assertThat(e.getCause().getCause(), Matchers.instanceOf(ServerStoreProxyException.class));
                Assert.assertThat(e.getCause().getCause().getCause(), Matchers.instanceOf(ConnectionClosedException.class));
            }
            // small iterator completes... it fetched the entire batch in one shot
            smallIterator.forEachRemaining(( k) -> smallMap.put(k.getKey(), k.getValue()));
            Assert.assertThat(smallMap, Matchers.is(LongStream.range(0, IterationFailureBehaviorTest.KEYS).boxed().collect(Collectors.toMap(Function.identity(), ( k) -> Long.toString(k)))));
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void testIteratorReconnect() throws Exception {
        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(IterationFailureBehaviorTest.CLUSTER.getConnectionURI().resolve("/iterator-cm")).autoCreate().defaultServerResource("primary-server-resource"));
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(false);
        cacheManager.init();
        try {
            CacheConfiguration<Long, String> smallConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 1, MemoryUnit.MB))).build();
            Cache<Long, String> smallCache = cacheManager.createCache("small-cache", smallConfig);
            LongStream.range(0, IterationFailureBehaviorTest.KEYS).forEach(( k) -> smallCache.put(k, Long.toString(k)));
            CacheConfiguration<Long, byte[]> largeConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, byte[].class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 4, MemoryUnit.MB))).build();
            Cache<Long, byte[]> largeCache = cacheManager.createCache("large-cache", largeConfig);
            byte[] value = new byte[10 * 1024];
            LongStream.range(0, IterationFailureBehaviorTest.KEYS).forEach(( k) -> {
                largeCache.put(k, value);
            });
            Map<Long, String> smallMap = new HashMap<>();
            Iterator<Cache.Entry<Long, String>> smallIterator = smallCache.iterator();
            Cache.Entry<Long, String> smallNext = smallIterator.next();
            smallMap.put(smallNext.getKey(), smallNext.getValue());
            Iterator<Cache.Entry<Long, byte[]>> largeIterator = largeCache.iterator();
            Cache.Entry<Long, byte[]> largeNext = largeIterator.next();
            Assert.assertThat(largeCache.get(largeNext.getKey()), IsNull.notNullValue());
            IterationFailureBehaviorTest.CLUSTER.getClusterControl().terminateAllServers();
            Thread.sleep(10000);
            IterationFailureBehaviorTest.CLUSTER.getClusterControl().startAllServers();
            IterationFailureBehaviorTest.CLUSTER.getClusterControl().waitForActive();
            // large iterator fails
            try {
                largeIterator.forEachRemaining(( k) -> {
                });
                Assert.fail("Expected CacheIterationException");
            } catch (CacheIterationException e) {
                Assert.assertThat(e.getCause(), Matchers.instanceOf(StoreAccessException.class));
                Assert.assertThat(e.getCause().getCause(), Matchers.instanceOf(ServerStoreProxyException.class));
                Assert.assertThat(e.getCause().getCause().getCause(), Matchers.instanceOf(ConnectionClosedException.class));
            }
            // small iterator completes... it fetched the entire batch in one shot
            smallIterator.forEachRemaining(( k) -> smallMap.put(k.getKey(), k.getValue()));
            Assert.assertThat(smallMap, Matchers.is(LongStream.range(0, IterationFailureBehaviorTest.KEYS).boxed().collect(Collectors.toMap(Function.identity(), ( k) -> Long.toString(k)))));
        } finally {
            cacheManager.close();
        }
    }
}

