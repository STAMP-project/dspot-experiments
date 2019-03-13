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
package com.hazelcast.map;


import MapService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapPartitionLostEventFilter;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.AbstractPartitionLostListenerTest;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
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
public class MapPartitionLostListenerTest extends AbstractPartitionLostListenerTest {
    @Test
    public void test_partitionLostListenerInvoked() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp(1);
        HazelcastInstance instance = instances.get(0);
        final TestEventCollectingMapPartitionLostListener listener = new TestEventCollectingMapPartitionLostListener(0);
        instance.getMap(getIthMapName(0)).addPartitionLostListener(listener);
        final IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 0, null);
        MapService mapService = HazelcastTestSupport.getNode(instance).getNodeEngine().getService(SERVICE_NAME);
        mapService.onPartitionLost(internalEvent);
        MapPartitionLostListenerTest.assertEventEventually(listener, internalEvent);
    }

    @Test
    public void test_allPartitionLostListenersInvoked() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp(2);
        HazelcastInstance instance1 = instances.get(0);
        HazelcastInstance instance2 = instances.get(0);
        final TestEventCollectingMapPartitionLostListener listener1 = new TestEventCollectingMapPartitionLostListener(0);
        final TestEventCollectingMapPartitionLostListener listener2 = new TestEventCollectingMapPartitionLostListener(0);
        instance1.getMap(getIthMapName(0)).addPartitionLostListener(listener1);
        instance2.getMap(getIthMapName(0)).addPartitionLostListener(listener2);
        final IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 0, null);
        MapService mapService = HazelcastTestSupport.getNode(instance1).getNodeEngine().getService(SERVICE_NAME);
        mapService.onPartitionLost(internalEvent);
        MapPartitionLostListenerTest.assertEventEventually(listener1, internalEvent);
        MapPartitionLostListenerTest.assertEventEventually(listener2, internalEvent);
    }

    @Test
    public void test_partitionLostListenerInvoked_whenEntryListenerIsAlsoRegistered() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp(1);
        HazelcastInstance instance = instances.get(0);
        final TestEventCollectingMapPartitionLostListener listener = new TestEventCollectingMapPartitionLostListener(0);
        instance.getMap(getIthMapName(0)).addPartitionLostListener(listener);
        instance.getMap(getIthMapName(0)).addEntryListener(Mockito.mock(EntryAddedListener.class), true);
        final IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 0, null);
        MapService mapService = HazelcastTestSupport.getNode(instance).getNodeEngine().getService(SERVICE_NAME);
        mapService.onPartitionLost(internalEvent);
        MapPartitionLostListenerTest.assertEventEventually(listener, internalEvent);
    }

    @Test
    public void test_partitionLostListenerInvoked_whenNodeCrashed() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp();
        HazelcastInstance survivingInstance = instances.get(0);
        HazelcastInstance terminatingInstance = instances.get(1);
        final TestEventCollectingMapPartitionLostListener listener = new TestEventCollectingMapPartitionLostListener(0);
        survivingInstance.getMap(getIthMapName(0)).addPartitionLostListener(listener);
        final Set<Integer> survivingPartitionIds = new HashSet<Integer>();
        Node survivingNode = HazelcastTestSupport.getNode(survivingInstance);
        Address survivingAddress = survivingNode.getThisAddress();
        for (IPartition partition : survivingNode.getPartitionService().getPartitions()) {
            if (survivingAddress.equals(partition.getReplicaAddress(0))) {
                survivingPartitionIds.add(partition.getPartitionId());
            }
        }
        terminatingInstance.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(survivingInstance);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                List<MapPartitionLostEvent> events = listener.getEvents();
                Assert.assertFalse(events.isEmpty());
                for (MapPartitionLostEvent event : events) {
                    Assert.assertFalse(survivingPartitionIds.contains(event.getPartitionId()));
                }
            }
        });
    }

    @Test
    public void testMapPartitionLostEventFilter() {
        MapPartitionLostEventFilter filter = new MapPartitionLostEventFilter();
        Assert.assertEquals(new MapPartitionLostEventFilter(), filter);
        Assert.assertFalse(filter.eval(null));
    }
}

