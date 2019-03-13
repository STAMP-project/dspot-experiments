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
package com.hazelcast.client.proxy;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistributedObjectListenerTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void destroyedNotReceivedOnClient() throws Exception {
        HazelcastInstance instance = newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        final CountDownLatch createdLatch = new CountDownLatch(1);
        final CountDownLatch destroyedLatch = new CountDownLatch(1);
        client.addDistributedObjectListener(new DistributedObjectListener() {
            @Override
            public void distributedObjectCreated(DistributedObjectEvent event) {
                createdLatch.countDown();
            }

            @Override
            public void distributedObjectDestroyed(DistributedObjectEvent event) {
                destroyedLatch.countDown();
            }
        });
        final String name = randomString();
        final ITopic<Object> topic = instance.getTopic(name);
        assertOpenEventually(createdLatch, 10);
        Assert.assertEquals(1, client.getDistributedObjects().size());
        topic.destroy();
        assertOpenEventually(destroyedLatch, 10);
        assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<DistributedObject> distributedObjects = client.getDistributedObjects();
                Assert.assertTrue(distributedObjects.isEmpty());
            }
        }, 5);
    }

    @Test
    public void testGetDistributedObjectsAfterRemove_FromNode() {
        HazelcastInstance server = newHazelcastInstance();
        IMap firstMap = server.getMap("firstMap");
        server.getMap("secondMap");
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        Assert.assertEquals(2, client.getDistributedObjects().size());
        firstMap.destroy();
        Assert.assertEquals(1, client.getDistributedObjects().size());
    }

    @Test
    public void testGetDistributedObjectsAfterRemove_fromClient() {
        hazelcastFactory.newHazelcastInstance();
        HazelcastInstance client1 = hazelcastFactory.newHazelcastClient();
        IMap<Object, Object> firstMap = client1.getMap("firstMap");
        client1.getMap("secondMap");
        HazelcastInstance client2 = hazelcastFactory.newHazelcastClient();
        Assert.assertEquals(2, client1.getDistributedObjects().size());
        Assert.assertEquals(2, client2.getDistributedObjects().size());
        firstMap.destroy();
        Assert.assertEquals(1, client1.getDistributedObjects().size());
        Assert.assertEquals(1, client2.getDistributedObjects().size());
    }

    @Test
    public void distributedObjectsCreatedBack_whenClusterRestart_withSingleNode() {
        final HazelcastInstance instance = newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(100);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        client.getMap("test");
        Collection<DistributedObject> distributedObjects = instance.getDistributedObjects();
        Assert.assertEquals(1, distributedObjects.size());
        instance.shutdown();
        final HazelcastInstance instance2 = newHazelcastInstance();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<DistributedObject> distributedObjects = instance2.getDistributedObjects();
                Assert.assertEquals(1, distributedObjects.size());
            }
        });
    }

    @Test
    public void distributedObjectsCreatedBack_whenClusterRestart_withMultipleNode() {
        final HazelcastInstance instance1 = newHazelcastInstance();
        final HazelcastInstance instance2 = newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(100);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        client.getMap("test");
        instance1.shutdown();
        instance2.shutdown();
        final HazelcastInstance newClusterInstance1 = newHazelcastInstance();
        final HazelcastInstance newClusterInstance2 = newHazelcastInstance();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<DistributedObject> distributedObjects1 = newClusterInstance1.getDistributedObjects();
                Assert.assertEquals(1, distributedObjects1.size());
                Collection<DistributedObject> distributedObjects2 = newClusterInstance2.getDistributedObjects();
                Assert.assertEquals(1, distributedObjects2.size());
            }
        });
    }
}

