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
package org.ehcache.clustered.client.internal.service;


import ConnectionPropertyNames.CONNECTION_TIMEOUT;
import java.net.URI;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.UnitTestConnectionService;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.connection.Connection;

import static java.time.Duration.ofSeconds;


public class ConnectionClosedTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://connection.com:9540/timeout");

    @Test
    public void testCacheOperationThrowsAfterConnectionClosed() throws Exception {
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 2, MemoryUnit.MB));
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(ConnectionClosedTest.CLUSTER_URI).timeouts(org.ehcache.clustered.client.config.builders.TimeoutsBuilder.timeouts().connection(ofSeconds(20)).build()).autoCreate()).withCache("clustered-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, resourcePoolsBuilder));
        PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        Cache<Long, String> cache = cacheManager.getCache("clustered-cache", Long.class, String.class);
        Collection<Properties> connectionProperties = UnitTestConnectionService.getConnectionProperties(ConnectionClosedTest.CLUSTER_URI);
        Assert.assertThat(connectionProperties.size(), Matchers.is(1));
        Properties properties = connectionProperties.iterator().next();
        Assert.assertThat(properties.getProperty(CONNECTION_TIMEOUT), Matchers.is("20000"));
        cache.put(1L, "value");
        Assert.assertThat(cache.get(1L), Matchers.is("value"));
        Collection<Connection> connections = UnitTestConnectionService.getConnections(ConnectionClosedTest.CLUSTER_URI);
        Assert.assertThat(connections.size(), Matchers.is(1));
        Connection connection = connections.iterator().next();
        connection.close();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // 
                }
                String result;
                if ((result = cache.get(1L)) != null) {
                    return result;
                }
            } 
        });
        Assert.assertThat(future.get(5, TimeUnit.SECONDS), Matchers.is("value"));
    }
}

