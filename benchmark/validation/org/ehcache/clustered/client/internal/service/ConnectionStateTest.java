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


import java.net.URI;
import java.util.Properties;
import org.ehcache.clustered.client.config.ClusteredResourcePool;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.Timeouts;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.store.ClusterTierClientEntity;
import org.ehcache.clustered.common.internal.ServerStoreConfiguration;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConnectionStateTest {
    private static URI CLUSTER_URI = URI.create("terracotta://localhost:9510");

    private final ClusteringServiceConfiguration serviceConfiguration = ClusteringServiceConfigurationBuilder.cluster(ConnectionStateTest.CLUSTER_URI).autoCreate().build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testInitializeStateAfterConnectionCloses() throws Exception {
        ConnectionState connectionState = new ConnectionState(Timeouts.DEFAULT, new Properties(), serviceConfiguration);
        connectionState.initClusterConnection();
        closeConnection();
        expectedException.expect(IllegalStateException.class);
        connectionState.getConnection().close();
        connectionState.initializeState();
        Assert.assertThat(connectionState.getConnection(), Matchers.notNullValue());
        Assert.assertThat(connectionState.getEntityFactory(), Matchers.notNullValue());
        connectionState.getConnection().close();
    }

    @Test
    public void testCreateClusterTierEntityAfterConnectionCloses() throws Exception {
        ConnectionState connectionState = new ConnectionState(Timeouts.DEFAULT, new Properties(), serviceConfiguration);
        connectionState.initClusterConnection();
        connectionState.initializeState();
        closeConnection();
        ClusteredResourcePool resourcePool = ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 4, MemoryUnit.MB);
        ServerStoreConfiguration serverStoreConfiguration = new ServerStoreConfiguration(resourcePool.getPoolAllocation(), Long.class.getName(), String.class.getName(), LongSerializer.class.getName(), StringSerializer.class.getName(), null, false);
        ClusterTierClientEntity clientEntity = connectionState.createClusterTierClientEntity("cache1", serverStoreConfiguration, false);
        Assert.assertThat(clientEntity, Matchers.notNullValue());
    }
}

