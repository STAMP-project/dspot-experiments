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


import ClientExecutionServiceImpl.INTERNAL_EXECUTOR_POOL_SIZE;
import ClientProperty.HEARTBEAT_INTERVAL;
import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import GroupProperty.CLIENT_HEARTBEAT_TIMEOUT_SECONDS;
import LifecycleState.CLIENT_CONNECTED;
import LifecycleState.CLIENT_DISCONNECTED;
import LifecycleState.SHUTDOWN;
import LifecycleState.SHUTTING_DOWN;
import LifecycleState.STARTED;
import LifecycleState.STARTING;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientSecurityConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionConfig.MaxSizePolicy;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.security.UsernamePasswordCredentials;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SlowTest;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static LifecycleState.CLIENT_DISCONNECTED;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientRegressionWithMockNetworkTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    /**
     * Test for issues #267 and #493
     */
    @Test
    public void testIssue493() {
        final HazelcastInstance hz1 = newHazelcastInstance();
        hazelcastFactory.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setRedoOperation(true);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final ILock lock = client.getLock("lock");
        for (int k = 0; k < 10; k++) {
            lock.lock();
            try {
                sleepMillis(100);
            } finally {
                lock.unlock();
            }
        }
        lock.lock();
        hz1.shutdown();
        lock.unlock();
    }

    @Test
    public void testOperationRedo() {
        final HazelcastInstance hz1 = newHazelcastInstance();
        hazelcastFactory.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setRedoOperation(true);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hz1.getLifecycleService().shutdown();
            }
        };
        final IMap<Integer, String> map = client.getMap("m");
        thread.start();
        int expected = 1000;
        for (int i = 0; i < expected; i++) {
            map.put(i, ("item" + i));
        }
        assertJoinable(thread);
        Assert.assertEquals(expected, map.size());
    }

    @Test
    public void testOperationRedo_smartRoutingDisabled() {
        final HazelcastInstance hz1 = newHazelcastInstance();
        hazelcastFactory.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRedoOperation(true);
        clientConfig.setSmartRouting(false);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hz1.getLifecycleService().shutdown();
            }
        };
        final IMap<Integer, Integer> map = client.getMap("m");
        thread.start();
        int expected = 1000;
        for (int i = 0; i < expected; i++) {
            map.put(i, i);
        }
        assertJoinable(thread);
        Assert.assertEquals(expected, map.size());
    }

    @Test
    public void testGetDistributedObjectsIssue678() {
        final HazelcastInstance hz = newHazelcastInstance();
        hz.getQueue("queue");
        hz.getMap("map");
        hz.getSemaphore("s");
        final HazelcastInstance instance = hazelcastFactory.newHazelcastClient();
        final Collection<DistributedObject> distributedObjects = instance.getDistributedObjects();
        Assert.assertEquals(3, distributedObjects.size());
    }

    @Test
    public void testMapDestroyIssue764() {
        HazelcastInstance server = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        assertNoOfDistributedObject("Initially the server should have %d distributed objects, but had %d", 0, server.getDistributedObjects());
        assertNoOfDistributedObject("Initially the client should have %d distributed objects, but had %d", 0, client.getDistributedObjects());
        IMap map = client.getMap("mapToDestroy");
        assertNoOfDistributedObject("After getMap() the server should have %d distributed objects, but had %d", 1, server.getDistributedObjects());
        assertNoOfDistributedObject("After getMap() the client should have %d distributed objects, but had %d", 1, client.getDistributedObjects());
        map.destroy();
        // Get the distributed objects as fast as possible to catch a race condition more likely
        Collection<DistributedObject> serverDistributedObjects = server.getDistributedObjects();
        Collection<DistributedObject> clientDistributedObjects = client.getDistributedObjects();
        assertNoOfDistributedObject("After destroy() the server should should have %d distributed objects, but had %d", 0, serverDistributedObjects);
        assertNoOfDistributedObject("After destroy() the client should should have %d distributed objects, but had %d", 0, clientDistributedObjects);
    }

    /**
     * Client hangs at map.get after shutdown
     */
    @Test
    public void testIssue821() {
        final HazelcastInstance instance = newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        final IMap<Object, Object> map = client.getMap("default");
        map.put("key1", "value1");
        instance.shutdown();
        try {
            map.get("key1");
            Assert.fail();
        } catch (Exception ignored) {
        }
        Assert.assertFalse(instance.getLifecycleService().isRunning());
    }

    @Test
    public void testClientConnectionEvents() {
        final LinkedList<LifecycleState> list = new LinkedList<LifecycleState>();
        list.offer(STARTING);
        list.offer(STARTED);
        list.offer(CLIENT_CONNECTED);
        list.offer(CLIENT_DISCONNECTED);
        list.offer(CLIENT_CONNECTED);
        list.offer(CLIENT_DISCONNECTED);
        list.offer(SHUTTING_DOWN);
        list.offer(SHUTDOWN);
        hazelcastFactory.newHazelcastInstance();
        final CountDownLatch latch = new CountDownLatch(list.size());
        final CountDownLatch connectedLatch = new CountDownLatch(2);
        final CountDownLatch disconnectedLatch = new CountDownLatch(2);
        LifecycleListener listener = new LifecycleListener() {
            public void stateChanged(LifecycleEvent event) {
                Logger.getLogger(getClass()).info(("stateChanged: " + event));
                final LifecycleState state = list.poll();
                LifecycleState eventState = event.getState();
                if ((state != null) && (state.equals(eventState))) {
                    latch.countDown();
                }
                if (CLIENT_CONNECTED.equals(eventState)) {
                    connectedLatch.countDown();
                }
                if (CLIENT_DISCONNECTED.equals(eventState)) {
                    disconnectedLatch.countDown();
                }
            }
        };
        final ListenerConfig listenerConfig = new ListenerConfig(listener);
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.addListenerConfig(listenerConfig);
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(100);
        HazelcastInstance hazelcastClient = hazelcastFactory.newHazelcastClient(clientConfig);
        hazelcastFactory.shutdownAllMembers();
        hazelcastFactory.newHazelcastInstance();
        assertOpenEventually("LifecycleState failed. Expected two CLIENT_CONNECTED events!", connectedLatch);
        hazelcastFactory.shutdownAllMembers();
        // wait for disconnect then call client.shutdown(). Otherwise shutdown could prevent firing DISCONNECTED event
        assertOpenEventually("LifecycleState failed. Expected two CLIENT_DISCONNECTED events!", disconnectedLatch);
        hazelcastClient.shutdown();
        assertOpenEventually("LifecycleState failed", latch);
    }

    @Test
    public void testInterceptor() {
        hazelcastFactory.newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        final IMap<Object, Object> map = client.getMap("map");
        final ClientRegressionWithMockNetworkTest.MapInterceptorImpl interceptor = new ClientRegressionWithMockNetworkTest.MapInterceptorImpl();
        final String id = map.addInterceptor(interceptor);
        Assert.assertNotNull(id);
        map.put("key1", "value");
        Assert.assertEquals("value", map.get("key1"));
        map.put("key1", "value1");
        Assert.assertEquals("getIntercepted", map.get("key1"));
        Assert.assertFalse(map.replace("key1", "getIntercepted", "val"));
        Assert.assertTrue(map.replace("key1", "value1", "val"));
        Assert.assertEquals("val", map.get("key1"));
        map.put("key2", "oldValue");
        Assert.assertEquals("oldValue", map.get("key2"));
        map.put("key2", "newValue");
        Assert.assertEquals("putIntercepted", map.get("key2"));
        map.put("key3", "value2");
        Assert.assertEquals("value2", map.get("key3"));
        Assert.assertEquals("removeIntercepted", map.remove("key3"));
    }

    private static class MapInterceptorImpl implements MapInterceptor {
        MapInterceptorImpl() {
        }

        public Object interceptGet(Object value) {
            if ("value1".equals(value)) {
                return "getIntercepted";
            }
            return null;
        }

        public void afterGet(Object value) {
        }

        public Object interceptPut(Object oldValue, Object newValue) {
            if (("oldValue".equals(oldValue)) && ("newValue".equals(newValue))) {
                return "putIntercepted";
            }
            return null;
        }

        public void afterPut(Object value) {
        }

        public Object interceptRemove(Object removedValue) {
            if ("value2".equals(removedValue)) {
                return "removeIntercepted";
            }
            return null;
        }

        public void afterRemove(Object value) {
        }
    }

    @Test
    public void testClientPortableWithoutRegisteringToNode() {
        hazelcastFactory.newHazelcastInstance();
        final SerializationConfig serializationConfig = new SerializationConfig();
        serializationConfig.addPortableFactory(5, new PortableFactory() {
            public Portable create(int classId) {
                return new ClientRegressionWithMockNetworkTest.SamplePortable();
            }
        });
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setSerializationConfig(serializationConfig);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final IMap<Integer, ClientRegressionWithMockNetworkTest.SamplePortable> sampleMap = client.getMap(randomString());
        sampleMap.put(1, new ClientRegressionWithMockNetworkTest.SamplePortable(666));
        final ClientRegressionWithMockNetworkTest.SamplePortable samplePortable = sampleMap.get(1);
        Assert.assertEquals(666, samplePortable.a);
    }

    @Test
    public void testCredentials() {
        final Config config = new Config();
        config.getGroupConfig().setName("foo").setPassword("bar");
        hazelcastFactory.newHazelcastInstance(config);
        final ClientConfig clientConfig = new ClientConfig();
        final ClientSecurityConfig securityConfig = clientConfig.getSecurityConfig();
        securityConfig.setCredentialsClassname(ClientRegressionWithMockNetworkTest.MyCredentials.class.getName());
        hazelcastFactory.newHazelcastClient(clientConfig);
    }

    public static class MyCredentials extends UsernamePasswordCredentials {
        public MyCredentials() {
            super("foo", "bar");
        }
    }

    static class SamplePortable implements Portable {
        public int a;

        public SamplePortable(int a) {
            this.a = a;
        }

        public SamplePortable() {
        }

        public int getFactoryId() {
            return 5;
        }

        public int getClassId() {
            return 6;
        }

        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("a", a);
        }

        public void readPortable(PortableReader reader) throws IOException {
            a = reader.readInt("a");
        }
    }

    @Test
    public void testNearCache_WhenRegisteredNodeIsDead() {
        final HazelcastInstance instance = newHazelcastInstance();
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        final String mapName = randomMapName();
        NearCacheConfig nearCacheConfig = new NearCacheConfig();
        nearCacheConfig.setName(mapName);
        nearCacheConfig.setInvalidateOnChange(true);
        clientConfig.addNearCacheConfig(nearCacheConfig);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final IMap<Object, Object> map = client.getMap(mapName);
        map.put("a", "b");
        // populate Near Cache
        map.get("a");
        instance.shutdown();
        hazelcastFactory.newHazelcastInstance();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNull(map.get("a"));
            }
        });
    }

    @Category(NightlyTest.class)
    @Test
    public void testLock_WhenDummyClientAndOwnerNodeDiesTogether() throws Exception {
        testLock_WhenClientAndOwnerNodeDiesTogether(false);
    }

    @Category(NightlyTest.class)
    @Test
    public void testLock_WhenSmartClientAndOwnerNodeDiesTogether() throws Exception {
        testLock_WhenClientAndOwnerNodeDiesTogether(true);
    }

    @Test
    public void testDeadlock_WhenDoingOperationFromListeners() {
        hazelcastFactory.newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(new ClientConfig().setExecutorPoolSize(1));
        final int putCount = 1000;
        final CountDownLatch latch = new CountDownLatch(putCount);
        final IMap<Object, Object> map1 = client.getMap(randomMapName());
        final IMap<Object, Object> map2 = client.getMap(randomMapName());
        map1.addEntryListener(new com.hazelcast.core.EntryAdapter<Object, Object>() {
            @Override
            public void entryAdded(EntryEvent<Object, Object> event) {
                map2.put(1, 1);
                latch.countDown();
            }
        }, false);
        for (int i = 0; i < putCount; i++) {
            map1.put(i, i);
        }
        assertOpenEventually(latch);
    }

    @Test
    public void testDeadlock_WhenDoingOperationFromLifecycleListener() {
        HazelcastInstance instance = newHazelcastInstance();
        final ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig.setExecutorPoolSize(1));
        hazelcastFactory.newHazelcastInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final IMap<Object, Object> map = client.getMap(randomMapName());
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (CLIENT_DISCONNECTED)) {
                    map.get(1);
                    latch.countDown();
                }
            }
        });
        instance.shutdown();
        assertOpenEventually(latch);
    }

    @Test
    public void testDeadlock_WhenDoingOperationFromLifecycleListenerWithInitialPartitionTable() {
        HazelcastInstance instance = newHazelcastInstance();
        final ClientConfig clientConfig = new ClientConfig();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig.setExecutorPoolSize(1));
        hazelcastFactory.newHazelcastInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final IMap<Object, Object> map = client.getMap(randomMapName());
        // Let the partition table retrieved the first time
        map.get(1);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (LifecycleState.CLIENT_DISCONNECTED)) {
                    for (int i = 0; i < 1000; i++) {
                        map.get(i);
                    }
                    latch.countDown();
                }
            }
        });
        instance.shutdown();
        assertOpenEventually(latch);
    }

    @Test
    public void testDeadlock_whenDoingOperationFromLifecycleListener_withNearCache() {
        String mapName = randomMapName();
        EvictionConfig evictionConfig = new EvictionConfig().setMaximumSizePolicy(MaxSizePolicy.ENTRY_COUNT).setSize(1);
        NearCacheConfig nearCacheConfig = new NearCacheConfig().setName(mapName).setEvictionConfig(evictionConfig);
        ClientConfig clientConfig = new ClientConfig().addNearCacheConfig(nearCacheConfig).setExecutorPoolSize(1);
        HazelcastInstance instance = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        hazelcastFactory.newHazelcastInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final IMap<Object, Object> map = client.getMap(mapName);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (LifecycleState.CLIENT_DISCONNECTED)) {
                    map.get(1);
                    map.get(2);
                    latch.countDown();
                }
            }
        });
        instance.shutdown();
        assertOpenEventually(latch);
    }

    @Test(expected = ExecutionException.class, timeout = 120000)
    public void testGithubIssue3557() throws Exception {
        HazelcastInstance hz = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient();
        ClientRegressionWithMockNetworkTest.UnDeserializable unDeserializable = new ClientRegressionWithMockNetworkTest.UnDeserializable(1);
        IExecutorService executorService = client.getExecutorService("default");
        ClientRegressionWithMockNetworkTest.Issue2509Runnable task = new ClientRegressionWithMockNetworkTest.Issue2509Runnable(unDeserializable);
        Future<?> future = executorService.submitToMember(task, hz.getCluster().getLocalMember());
        future.get();
    }

    public static class Issue2509Runnable implements DataSerializable , Callable<Integer> {
        private ClientRegressionWithMockNetworkTest.UnDeserializable unDeserializable;

        public Issue2509Runnable() {
        }

        public Issue2509Runnable(ClientRegressionWithMockNetworkTest.UnDeserializable unDeserializable) {
            this.unDeserializable = unDeserializable;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(unDeserializable);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            unDeserializable = in.readObject();
        }

        @Override
        public Integer call() {
            return unDeserializable.foo;
        }
    }

    public static class UnDeserializable implements DataSerializable {
        private int foo;

        public UnDeserializable(int foo) {
            this.foo = foo;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt(foo);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            foo = in.readInt();
        }
    }

    @Test
    public void testNearCache_shutdownClient() {
        final ClientConfig clientConfig = new ClientConfig();
        NearCacheConfig invalidateConfig = new NearCacheConfig();
        final String mapName = randomMapName();
        invalidateConfig.setName(mapName);
        invalidateConfig.setInvalidateOnChange(true);
        clientConfig.addNearCacheConfig(invalidateConfig);
        hazelcastFactory.newHazelcastInstance();
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final IMap<Integer, Integer> map = client.getMap(mapName);
        map.get(1);
        // test should finish without throwing any exception.
        client.shutdown();
    }

    @Test
    public void testClientReconnect_thenCheckRequestsAreRetriedWithoutException() {
        final HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final CountDownLatch clientStartedDoingRequests = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientStartedDoingRequests.await();
                } catch (InterruptedException ignored) {
                }
                hazelcastInstance.shutdown();
                hazelcastFactory.newHazelcastInstance();
            }
        }).start();
        ClientConfig clientConfig = new ClientConfig();
        // Retry all requests
        clientConfig.getNetworkConfig().setRedoOperation(true);
        // retry to connect to cluster forever(never shutdown the client)
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        // Retry all requests forever(until client is shutdown)
        clientConfig.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), String.valueOf(Integer.MAX_VALUE));
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        IMap<Object, Object> map = client.getMap(randomMapName());
        int mapSize = 1000;
        for (int i = 0; i < mapSize; i++) {
            if (i == (mapSize / 4)) {
                clientStartedDoingRequests.countDown();
            }
            try {
                map.put(i, i);
            } catch (Exception e) {
                Assert.assertTrue(("Requests should not throw exception with this configuration. Last put key: " + i), false);
            }
        }
    }

    @Test
    public void testClusterShutdown_thenCheckOperationsNotHanging() throws Exception {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        // Retry all requests
        clientConfig.getNetworkConfig().setRedoOperation(true);
        // Retry all requests forever(until client is shutdown)
        clientConfig.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), String.valueOf(Integer.MAX_VALUE));
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        final IMap<Object, Object> map = client.getMap(randomMapName());
        final int mapSize = 1000;
        final CountDownLatch clientStartedDoingRequests = new CountDownLatch(1);
        int threadCount = 100;
        final CountDownLatch testFinishedLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < mapSize; i++) {
                            if (i == (mapSize / 4)) {
                                clientStartedDoingRequests.countDown();
                            }
                            map.put(i, i);
                        }
                    } catch (Throwable ignored) {
                    } finally {
                        testFinishedLatch.countDown();
                    }
                }
            });
            thread.start();
        }
        Assert.assertTrue(clientStartedDoingRequests.await(30, TimeUnit.SECONDS));
        hazelcastInstance.shutdown();
        assertOpenEventually("Put operations should not hang.", testFinishedLatch);
    }

    @Test(timeout = 120000)
    public void testMemberAddedWithListeners_thenCheckOperationsNotHanging() {
        hazelcastFactory.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(INTERNAL_EXECUTOR_POOL_SIZE.getName(), "1");
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        IMap map = client.getMap("map");
        map.addEntryListener(Mockito.mock(MapListener.class), true);
        HazelcastInstance h2 = newHazelcastInstance();
        String key = generateKeyOwnedBy(h2);
        map.get(key);
    }

    @Test
    @Category(SlowTest.class)
    public void testServerShouldNotCloseClientWhenClientOnlyListening() {
        Config config = new Config();
        int clientHeartbeatSeconds = 8;
        config.setProperty(CLIENT_HEARTBEAT_TIMEOUT_SECONDS.getName(), String.valueOf(clientHeartbeatSeconds));
        hazelcastFactory.newHazelcastInstance(config);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(HEARTBEAT_INTERVAL.getName(), "1000");
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        HazelcastInstance client2 = hazelcastFactory.newHazelcastClient();
        final AtomicBoolean isClientDisconnected = new AtomicBoolean();
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if (CLIENT_DISCONNECTED.equals(event.getState())) {
                    isClientDisconnected.set(true);
                }
            }
        });
        String key = "topicName";
        ITopic topic = client.getTopic(key);
        MessageListener listener = new MessageListener() {
            public void onMessage(Message message) {
            }
        };
        String id = topic.addMessageListener(listener);
        ITopic<Object> client2Topic = client2.getTopic(key);
        long begin = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - begin) < (TimeUnit.SECONDS.toMillis((clientHeartbeatSeconds * 2)))) {
            client2Topic.publish("message");
        } 
        topic.removeMessageListener(id);
        Assert.assertFalse(isClientDisconnected.get());
    }
}

