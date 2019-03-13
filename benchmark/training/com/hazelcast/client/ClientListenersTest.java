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
package com.hazelcast.client;


import MapService.SERVICE_NAME;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Message;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.operation.LegacyMergeOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientListenersTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    HazelcastInstance client;

    HazelcastInstance server;

    @Test
    public void testEntryListener_withPortableNotRegisteredInNode() throws Exception {
        final IMap<Object, Object> map = client.getMap(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.core.EntryAdapter<Object, Object>() {
            @Override
            public void entryAdded(EntryEvent<Object, Object> event) {
                latch.countDown();
            }
        }, true);
        map.put(1, new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        assertOpenEventually(latch);
    }

    @Test
    public void testEntryMergeListener_withPortableNotRegisteredInNode() throws Exception {
        final IMap<Object, Object> map = client.getMap(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryMergedListener<Object, Object>() {
            @Override
            public void entryMerged(EntryEvent<Object, Object> event) {
                latch.countDown();
            }
        }, true);
        Node node = getNode(server);
        NodeEngineImpl nodeEngine = node.nodeEngine;
        OperationServiceImpl operationService = ((OperationServiceImpl) (nodeEngine.getOperationService()));
        SerializationService serializationService = getSerializationService(server);
        Data key = serializationService.toData(1);
        Data value = serializationService.toData(new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        EntryView entryView = EntryViews.createSimpleEntryView(key, value, Mockito.mock(Record.class));
        LegacyMergeOperation op = new LegacyMergeOperation(map.getName(), entryView, new PassThroughMergePolicy(), false);
        int partitionId = nodeEngine.getPartitionService().getPartitionId(key);
        operationService.invokeOnPartition(SERVICE_NAME, op, partitionId);
        assertOpenEventually(latch);
    }

    @Test
    public void testItemListener_withPortableNotRegisteredInNode() throws Exception {
        final IQueue<Object> queue = client.getQueue(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        queue.addItemListener(new com.hazelcast.core.ItemListener<Object>() {
            @Override
            public void itemAdded(ItemEvent<Object> item) {
                latch.countDown();
            }

            @Override
            public void itemRemoved(ItemEvent<Object> item) {
            }
        }, true);
        queue.offer(new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        assertOpenEventually(latch);
    }

    @Test
    public void testSetListener_withPortableNotRegisteredInNode() throws Exception {
        final ISet<Object> set = client.getSet(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        set.addItemListener(new com.hazelcast.core.ItemListener<Object>() {
            @Override
            public void itemAdded(ItemEvent<Object> item) {
                latch.countDown();
            }

            @Override
            public void itemRemoved(ItemEvent<Object> item) {
            }
        }, true);
        set.add(new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        assertOpenEventually(latch);
    }

    @Test
    public void testListListener_withPortableNotRegisteredInNode() throws Exception {
        final IList<Object> list = client.getList(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        list.addItemListener(new com.hazelcast.core.ItemListener<Object>() {
            @Override
            public void itemAdded(ItemEvent<Object> item) {
                latch.countDown();
            }

            @Override
            public void itemRemoved(ItemEvent<Object> item) {
            }
        }, true);
        list.add(new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        assertOpenEventually(latch);
    }

    @Test
    public void testTopic_withPortableNotRegisteredInNode() throws Exception {
        final ITopic<Object> topic = client.getTopic(randomMapName());
        final CountDownLatch latch = new CountDownLatch(1);
        topic.addMessageListener(new com.hazelcast.core.MessageListener<Object>() {
            @Override
            public void onMessage(Message<Object> message) {
                latch.countDown();
            }
        });
        topic.publish(new ClientRegressionWithMockNetworkTest.SamplePortable(1));
        assertOpenEventually(latch);
    }

    @Test
    public void testLifecycleListener_registeredViaClassName() {
        Assert.assertTrue(ClientListenersTest.StaticListener.CALLED_AT_LEAST_ONCE.get());
    }

    public static class StaticListener implements LifecycleListener {
        private static final AtomicBoolean CALLED_AT_LEAST_ONCE = new AtomicBoolean();

        @Override
        public void stateChanged(LifecycleEvent event) {
            ClientListenersTest.StaticListener.CALLED_AT_LEAST_ONCE.set(true);
        }
    }
}

