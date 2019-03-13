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
package com.hazelcast.client.listeners.leak;


import com.hazelcast.client.spi.impl.listener.ClientEventRegistration;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.MessageListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.instance.Node;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ListenerLeakTestSmartRouting extends ListenerLeakTestSupport {
    @Test
    public void testMapEntryListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        IMap map = client.getMap(randomString());
        String id = map.addEntryListener(Mockito.mock(MapListener.class), false);
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(map.removeEntryListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testMapPartitionLostListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        IMap map = client.getMap(randomString());
        String id = map.addPartitionLostListener(Mockito.mock(MapPartitionLostListener.class));
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(map.removePartitionLostListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testMultiMapEntryListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        MultiMap multiMap = client.getMultiMap(randomString());
        String id = multiMap.addEntryListener(Mockito.mock(EntryListener.class), false);
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(multiMap.removeEntryListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testListListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        IList<Object> list = client.getList(randomString());
        String id = list.addItemListener(Mockito.mock(ItemListener.class), false);
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(list.removeItemListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testSetListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        ISet<Object> set = client.getSet(randomString());
        String id = set.addItemListener(Mockito.mock(ItemListener.class), false);
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(set.removeItemListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testQueueListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        IQueue<Object> queue = client.getQueue(randomString());
        String id = queue.addItemListener(Mockito.mock(ItemListener.class), false);
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(queue.removeItemListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testReplicatedMapListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        ReplicatedMap<Object, Object> replicatedMap = client.getReplicatedMap(randomString());
        String id = replicatedMap.addEntryListener(Mockito.mock(EntryListener.class));
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(replicatedMap.removeEntryListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testDistributedObjectListeners() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        String id = client.addDistributedObjectListener(Mockito.mock(DistributedObjectListener.class));
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(client.removeDistributedObjectListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }

    @Test
    public void testTopicMessageListener() {
        Collection<Node> nodes = createNodes();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        ITopic<Object> topic = client.getTopic(randomString());
        String id = topic.addMessageListener(Mockito.mock(MessageListener.class));
        Collection<ClientEventRegistration> registrations = getClientEventRegistrations(client, id);
        Assert.assertTrue(topic.removeMessageListener(id));
        assertNoLeftOver(nodes, client, id, registrations);
    }
}

