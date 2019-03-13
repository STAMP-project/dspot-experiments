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
package com.hazelcast.client.partitionservice;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.ClientTestUtil;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.partition.PartitionLostListenerStressTest.EventCollectingPartitionLostListener;
import com.hazelcast.spi.impl.PortablePartitionLostEvent;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientPartitionLostListenerTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void test_partitionLostListener_registered() {
        final HazelcastInstance instance = newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        client.getPartitionService().addPartitionLostListener(Mockito.mock(PartitionLostListener.class));
        // Expected = 4 -> 1 added & 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        assertRegistrationsSizeEventually(instance, 4);
    }

    @Test
    public void test_partitionLostListener_removed() {
        final HazelcastInstance instance = newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        final String registrationId = client.getPartitionService().addPartitionLostListener(Mockito.mock(PartitionLostListener.class));
        // Expected = 4 -> 1 added & 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        assertRegistrationsSizeEventually(instance, 4);
        client.getPartitionService().removePartitionLostListener(registrationId);
        // Expected = 3 -> see {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        assertRegistrationsSizeEventually(instance, 3);
    }

    @Test
    public void test_partitionLostListener_invoked() {
        final HazelcastInstance instance = newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        HazelcastTestSupport.warmUpPartitions(instance, client);
        final EventCollectingPartitionLostListener listener = new EventCollectingPartitionLostListener();
        client.getPartitionService().addPartitionLostListener(listener);
        // Expected = 4 -> 1 added & 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers
        assertRegistrationsSizeEventually(instance, 4);
        final InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(instance).getNodeEngine().getService(SERVICE_NAME);
        final int partitionId = 5;
        partitionService.onPartitionLost(new IPartitionLostEvent(partitionId, 0, null));
        assertPartitionLostEventEventually(listener, partitionId);
    }

    @Test
    public void test_partitionLostListener_invoked_fromOtherNode() {
        final HazelcastInstance instance1 = newHazelcastInstance();
        final HazelcastInstance instance2 = newHazelcastInstance();
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setSmartRouting(false);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        HazelcastTestSupport.warmUpPartitions(instance1, instance2, client);
        final HazelcastClientInstanceImpl clientInstanceImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
        final Address clientOwnerAddress = clientInstanceImpl.getConnectionManager().getOwnerConnectionAddress();
        final HazelcastInstance other = (HazelcastTestSupport.getAddress(instance1).equals(clientOwnerAddress)) ? instance2 : instance1;
        final EventCollectingPartitionLostListener listener = new EventCollectingPartitionLostListener();
        client.getPartitionService().addPartitionLostListener(listener);
        // Expected = 2 -> 1 added & 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers * instances
        assertRegistrationsSizeEventually(instance1, 7);
        assertRegistrationsSizeEventually(instance2, 7);
        final InternalPartitionServiceImpl partitionService = HazelcastTestSupport.getNode(other).getNodeEngine().getService(SERVICE_NAME);
        final int partitionId = 5;
        partitionService.onPartitionLost(new IPartitionLostEvent(partitionId, 0, null));
        assertPartitionLostEventEventually(listener, partitionId);
    }

    @Test
    public void test_portableMapPartitionLostEvent_serialization() throws IOException {
        final Address source = new Address();
        final PortablePartitionLostEvent event = new PortablePartitionLostEvent(1, 2, source);
        final PortableWriter writer = Mockito.mock(PortableWriter.class);
        final ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);
        Mockito.when(writer.getRawDataOutput()).thenReturn(output);
        event.writePortable(writer);
        Mockito.verify(writer).writeInt("p", 1);
        Mockito.verify(writer).writeInt("l", 2);
        Mockito.verify(output).writeObject(source);
    }

    @Test
    public void test_portableMapPartitionLostEvent_deserialization() throws IOException {
        final Address source = new Address();
        final PortablePartitionLostEvent event = new PortablePartitionLostEvent();
        final PortableReader reader = Mockito.mock(PortableReader.class);
        final ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(reader.getRawDataInput()).thenReturn(input);
        Mockito.when(reader.readInt("p")).thenReturn(1);
        Mockito.when(reader.readInt("l")).thenReturn(2);
        Mockito.when(input.readObject()).thenReturn(source);
        event.readPortable(reader);
        Assert.assertEquals(1, event.getPartitionId());
        Assert.assertEquals(2, event.getLostBackupCount());
        Assert.assertEquals(source, event.getSource());
    }
}

