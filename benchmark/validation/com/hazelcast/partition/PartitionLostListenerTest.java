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
package com.hazelcast.partition;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionLostListenerTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance[] instances;

    @Test
    public void test_partitionLostListenerInvoked() {
        HazelcastInstance instance = instances[0];
        PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener = new PartitionLostListenerStressTest.EventCollectingPartitionLostListener();
        instance.getPartitionService().addPartitionLostListener(listener);
        IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 0, null);
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNode(instance).getNodeEngine();
        InternalPartitionServiceImpl partitionService = ((InternalPartitionServiceImpl) (nodeEngine.getPartitionService()));
        partitionService.onPartitionLost(internalEvent);
        assertEventEventually(listener, internalEvent);
    }

    @Test
    public void test_allPartitionLostListenersInvoked() {
        HazelcastInstance instance1 = instances[0];
        HazelcastInstance instance2 = instances[1];
        PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener1 = new PartitionLostListenerStressTest.EventCollectingPartitionLostListener();
        PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener2 = new PartitionLostListenerStressTest.EventCollectingPartitionLostListener();
        instance1.getPartitionService().addPartitionLostListener(listener1);
        instance2.getPartitionService().addPartitionLostListener(listener2);
        IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 0, null);
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNode(instance1).getNodeEngine();
        InternalPartitionServiceImpl partitionService = ((InternalPartitionServiceImpl) (nodeEngine.getPartitionService()));
        partitionService.onPartitionLost(internalEvent);
        assertEventEventually(listener1, internalEvent);
        assertEventEventually(listener2, internalEvent);
    }

    @Test
    public void test_partitionLostListenerInvoked_whenNodeCrashed() {
        HazelcastInstance survivingInstance = instances[0];
        HazelcastInstance terminatingInstance = instances[1];
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        final PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener = new PartitionLostListenerStressTest.EventCollectingPartitionLostListener();
        survivingInstance.getPartitionService().addPartitionLostListener(listener);
        Node survivingNode = HazelcastTestSupport.getNode(survivingInstance);
        final Address survivingAddress = survivingNode.getThisAddress();
        final Set<Integer> survivingPartitionIds = new HashSet<Integer>();
        for (InternalPartition partition : survivingNode.getPartitionService().getInternalPartitions()) {
            if (survivingAddress.equals(partition.getReplicaAddress(0))) {
                survivingPartitionIds.add(partition.getPartitionId());
            }
        }
        terminatingInstance.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(survivingInstance);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                List<PartitionLostEvent> events = listener.getEvents();
                Assert.assertFalse(events.isEmpty());
                for (PartitionLostEvent event : events) {
                    Assert.assertEquals(survivingAddress, event.getEventSource());
                    Assert.assertFalse(survivingPartitionIds.contains(event.getPartitionId()));
                    Assert.assertEquals(0, event.getLostBackupCount());
                }
            }
        });
    }

    @Test
    public void test_partitionLostListenerInvoked_whenAllPartitionReplicasCrashed() {
        HazelcastInstance lite = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.warmUpPartitions(lite);
        HazelcastTestSupport.waitAllForSafeState(instances);
        HazelcastTestSupport.waitInstanceForSafeState(lite);
        final PartitionLostListenerStressTest.EventCollectingPartitionLostListener listener = new PartitionLostListenerStressTest.EventCollectingPartitionLostListener();
        lite.getPartitionService().addPartitionLostListener(listener);
        instances[0].getLifecycleService().terminate();
        instances[1].getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                List<PartitionLostEvent> events = listener.getEvents();
                Assert.assertFalse(events.isEmpty());
            }
        });
    }

    @Test
    public void test_internalPartitionLostEvent_serialization() throws IOException {
        Address address = new Address();
        IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 2, address);
        ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);
        internalEvent.writeData(output);
        Mockito.verify(output).writeInt(1);
        Mockito.verify(output).writeInt(2);
    }

    @Test
    public void test_internalPartitionLostEvent_deserialization() throws IOException {
        IPartitionLostEvent internalEvent = new IPartitionLostEvent();
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readInt()).thenReturn(1, 2);
        internalEvent.readData(input);
        Assert.assertEquals(1, internalEvent.getPartitionId());
        Assert.assertEquals(2, internalEvent.getLostReplicaIndex());
    }

    @Test
    public void test_internalPartitionLostEvent_toString() {
        Assert.assertNotNull(new IPartitionLostEvent().toString());
    }
}

