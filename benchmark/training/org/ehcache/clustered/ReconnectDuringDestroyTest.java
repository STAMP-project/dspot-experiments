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


import VoltronReadWriteLock.Hold;
import com.tc.net.proxy.TCPProxy;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.internal.ClusterTierManagerClientEntity;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLock;
import org.ehcache.clustered.client.service.EntityBusyException;
import org.ehcache.clustered.common.internal.ClusterTierManagerConfiguration;
import org.ehcache.clustered.reconnect.BasicCacheReconnectTest;
import org.ehcache.clustered.reconnect.ThrowingResiliencyStrategy;
import org.ehcache.clustered.util.TCPProxyUtil;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.connection.Connection;
import org.terracotta.connection.entity.EntityRef;
import org.terracotta.exception.EntityNotFoundException;
import org.terracotta.lease.connection.LeasedConnectionFactory;
import org.terracotta.testing.rules.Cluster;


/**
 * ReconnectDuringDestroyTest
 */
public class ReconnectDuringDestroyTest extends ClusteredTests {
    private static URI connectionURI;

    private static List<TCPProxy> proxies;

    PersistentCacheManager cacheManager;

    @ClassRule
    public static Cluster CLUSTER = newCluster().in(new File("build/cluster")).withServiceFragment(BasicCacheReconnectTest.RESOURCE_CONFIG).build();

    /* This is to test the scenario in which reconnect happens while cache manager
    destruction is in progress. This test checks whether the cache manager
    gets destructed properly in the reconnect path once the connection is closed
    after the prepareForDestroy() call.
     */
    @Test
    public void reconnectDuringDestroyTest() throws Exception {
        cacheManager.close();
        Connection client = null;
        try {
            client = LeasedConnectionFactory.connect(ReconnectDuringDestroyTest.connectionURI, new Properties());
            VoltronReadWriteLock voltronReadWriteLock = new VoltronReadWriteLock(client, "crud-cm");
            try (VoltronReadWriteLock.Hold localMaintenance = voltronReadWriteLock.tryWriteLock()) {
                if (localMaintenance == null) {
                    throw new EntityBusyException(("Unable to obtain maintenance lease for " + "crud-cm"));
                }
                EntityRef<ClusterTierManagerClientEntity, ClusterTierManagerConfiguration, Void> ref = getEntityRef(client);
                try {
                    ClusterTierManagerClientEntity entity = ref.fetchEntity(null);
                    entity.prepareForDestroy();
                    entity.close();
                } catch (EntityNotFoundException e) {
                    Assert.fail();
                }
            }
            // For reconnection.
            TCPProxyUtil.setDelay(6000, ReconnectDuringDestroyTest.proxies);// Connection Lease time is 5 seconds so delaying for more than 5 seconds.

            Thread.sleep(6000);
            TCPProxyUtil.setDelay(0L, ReconnectDuringDestroyTest.proxies);
            client = LeasedConnectionFactory.connect(ReconnectDuringDestroyTest.connectionURI, new Properties());
            // For mimicking the cacheManager.destroy() in the reconnect path.
            voltronReadWriteLock = new VoltronReadWriteLock(client, "crud-cm");
            try (VoltronReadWriteLock.Hold localMaintenance = voltronReadWriteLock.tryWriteLock()) {
                if (localMaintenance == null) {
                    throw new EntityBusyException(("Unable to obtain maintenance lease for " + "crud-cm"));
                }
                EntityRef<ClusterTierManagerClientEntity, ClusterTierManagerConfiguration, Void> ref = getEntityRef(client);
                try {
                    ClusterTierManagerClientEntity entity = ref.fetchEntity(null);
                    entity.prepareForDestroy();
                    entity.close();
                } catch (EntityNotFoundException e) {
                    Assert.fail(("Unexpected exception " + (e.getMessage())));
                }
                if (!(ref.destroy())) {
                    Assert.fail("Unexpected exception while trying to destroy cache manager");
                }
            }
        } finally {
            client.close();
        }
    }

    @Test
    public void reconnectAfterDestroyOneOfTheCache() throws Exception {
        try {
            CacheConfiguration<Long, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 1, MemoryUnit.MB))).withResilienceStrategy(new ThrowingResiliencyStrategy()).build();
            Cache<Long, String> cache1 = cacheManager.createCache("clustered-cache-1", config);
            Cache<Long, String> cache2 = cacheManager.createCache("clustered-cache-2", config);
            cache1.put(1L, "The one");
            cache1.put(2L, "The two");
            cache2.put(1L, "The one");
            cache2.put(2L, "The two");
            cacheManager.destroyCache("clustered-cache-1");
            // For reconnection.
            TCPProxyUtil.setDelay(6000, ReconnectDuringDestroyTest.proxies);// Connection Lease time is 5 seconds so delaying for more than 5 seconds.

            Thread.sleep(6000);
            TCPProxyUtil.setDelay(0L, ReconnectDuringDestroyTest.proxies);
            cache2 = cacheManager.getCache("clustered-cache-2", Long.class, String.class);
            int count = 0;
            while (count < 5) {
                Thread.sleep(2000);
                count++;
                try {
                    cache2.get(1L);
                    break;
                } catch (Exception e) {
                    // Can happen during reconnect
                }
            } 
            if (count == 5) {
                Assert.fail("Unexpected reconnection exception");
            }
            Assert.assertThat(cache2.get(1L), Matchers.equalTo("The one"));
            Assert.assertThat(cache2.get(2L), Matchers.equalTo("The two"));
            cache2.put(3L, "The three");
            Assert.assertThat(cache2.get(3L), Matchers.equalTo("The three"));
        } finally {
            cacheManager.close();
        }
    }
}

