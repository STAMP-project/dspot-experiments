/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.map;


import MapService.SERVICE_NAME;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.ClientTestUtil;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.TestEventCollectingMapPartitionLostListener;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapPartitionLostListenerTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void test_mapPartitionLostListener_registered() {
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance(getConfig());
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        String mapName = randomMapName();
        client.getMap(mapName).addPartitionLostListener(Mockito.mock(MapPartitionLostListener.class));
        ClientMapPartitionLostListenerTest.assertRegistrationEventually(instance, mapName, true);
    }

    @Test
    public void test_mapPartitionLostListener_removed() {
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance(getConfig());
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        String mapName = randomMapName();
        String registrationId = client.getMap(mapName).addPartitionLostListener(Mockito.mock(MapPartitionLostListener.class));
        ClientMapPartitionLostListenerTest.assertRegistrationEventually(instance, mapName, true);
        Assert.assertTrue(client.getMap(mapName).removePartitionLostListener(registrationId));
        ClientMapPartitionLostListenerTest.assertRegistrationEventually(instance, mapName, false);
    }

    @Test
    public void test_mapPartitionLostListener_invoked() {
        String mapName = randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setBackupCount(0);
        ClientConfig clientConfig = getClientConfig();
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        warmUpPartitions(instance, client);
        TestEventCollectingMapPartitionLostListener listener = new TestEventCollectingMapPartitionLostListener(0);
        client.getMap(mapName).addPartitionLostListener(listener);
        MapService mapService = getNode(instance).getNodeEngine().getService(SERVICE_NAME);
        int partitionId = 5;
        mapService.onPartitionLost(new IPartitionLostEvent(partitionId, 0, null));
        ClientMapPartitionLostListenerTest.assertMapPartitionLostEventEventually(listener, partitionId);
    }

    @Test
    public void test_mapPartitionLostListener_invoked_fromOtherNode() {
        String mapName = randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setBackupCount(0);
        HazelcastInstance instance1 = hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = hazelcastFactory.newHazelcastInstance(config);
        ClientConfig clientConfig = getClientConfig();
        clientConfig.getNetworkConfig().setSmartRouting(false);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        HazelcastClientInstanceImpl clientInstanceImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
        Address clientOwnerAddress = clientInstanceImpl.getConnectionManager().getOwnerConnectionAddress();
        HazelcastInstance other = (getAddress(instance1).equals(clientOwnerAddress)) ? instance2 : instance1;
        TestEventCollectingMapPartitionLostListener listener = new TestEventCollectingMapPartitionLostListener(0);
        client.getMap(mapName).addPartitionLostListener(listener);
        ClientMapPartitionLostListenerTest.assertRegistrationEventually(instance1, mapName, true);
        ClientMapPartitionLostListenerTest.assertRegistrationEventually(instance2, mapName, true);
        ClientMapPartitionLostListenerTest.assertProxyExistsEventually(instance1, mapName);
        ClientMapPartitionLostListenerTest.assertProxyExistsEventually(instance2, mapName);
        MapService mapService = getNode(other).getNodeEngine().getService(SERVICE_NAME);
        int partitionId = 5;
        mapService.onPartitionLost(new IPartitionLostEvent(partitionId, 0, null));
        ClientMapPartitionLostListenerTest.assertMapPartitionLostEventEventually(listener, partitionId);
    }
}

