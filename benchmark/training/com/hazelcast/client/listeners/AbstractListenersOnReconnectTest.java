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
package com.hazelcast.client.listeners;


import GroupProperty.CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS;
import GroupProperty.CLIENT_HEARTBEAT_TIMEOUT_SECONDS;
import LifecycleEvent.LifecycleState;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.annotation.SlowTest;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public abstract class AbstractListenersOnReconnectTest extends ClientTestSupport {
    private static final int EVENT_COUNT = 10;

    private final AtomicInteger eventCount = new AtomicInteger();

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private CountDownLatch eventsLatch = new CountDownLatch(1);

    private final Set<String> events = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private String registrationId;

    private int clusterSize;

    protected HazelcastInstance client;

    // -------------------------- testListenersTerminateRandomNode --------------------- //
    @Test
    public void testListenersNonSmartRoutingTerminateRandomNode() {
        newInstances(null, 3);
        ClientConfig clientConfig = getNonSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateRandomNode();
    }

    @Test
    public void testListenersSmartRoutingTerminateRandomNode() {
        newInstances(null, 3);
        ClientConfig clientConfig = getSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateRandomNode();
    }

    // -------------------------- testListenersWaitMemberDestroy --------------------- //
    @Test
    public void testListenersWaitMemberDestroySmartRouting() {
        Config config = new Config();
        int endpointDelaySeconds = 2;
        config.setProperty(CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS.getName(), String.valueOf(endpointDelaySeconds));
        factory.newInstances(config, 3);
        client = factory.newHazelcastClient(getSmartClientConfig());
        setupListener();
        Collection<HazelcastInstance> allHazelcastInstances = getAllHazelcastInstances();
        final CountDownLatch disconnectedLatch = new CountDownLatch(1);
        final CountDownLatch connectedLatch = new CountDownLatch(1);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((LifecycleState.CLIENT_DISCONNECTED) == (event.getState())) {
                    disconnectedLatch.countDown();
                }
                if ((LifecycleState.CLIENT_CONNECTED) == (event.getState())) {
                    connectedLatch.countDown();
                }
            }
        });
        final HazelcastClientInstanceImpl clientInstanceImpl = getHazelcastClientInstanceImpl(client);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(3, clientInstanceImpl.getConnectionManager().getActiveConnections().size());
            }
        });
        HazelcastInstance ownerMember = getOwnerServer(factory, clientInstanceImpl);
        for (HazelcastInstance member : allHazelcastInstances) {
            blockMessagesFromInstance(member, client);
        }
        ownerMember.getLifecycleService().terminate();
        for (HazelcastInstance member : allHazelcastInstances) {
            unblockMessagesFromInstance(member, client);
        }
        assertOpenEventually(disconnectedLatch);
        assertOpenEventually(connectedLatch);
        sleepAtLeastMillis(((endpointDelaySeconds * 1000) + 2000));
        clusterSize = (clusterSize) - 1;
        validateRegistrationsAndListenerFunctionality();
    }

    // --------------------------------------------------------------------------------- //
    @Test
    public void testListenersWhenClientDisconnectedOperationRuns_whenOwnerMemberRemoved() {
        Config config = new Config();
        int endpointDelaySeconds = 2;
        config.setProperty(CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS.getName(), String.valueOf(endpointDelaySeconds));
        HazelcastInstance ownerServer = factory.newHazelcastInstance(config);
        client = factory.newHazelcastClient(getSmartClientConfig());
        HazelcastInstance server2 = factory.newHazelcastInstance(config);
        setupListener();
        final CountDownLatch disconnectedLatch = new CountDownLatch(1);
        final CountDownLatch connectedLatch = new CountDownLatch(1);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((LifecycleState.CLIENT_DISCONNECTED) == (event.getState())) {
                    disconnectedLatch.countDown();
                }
                if ((LifecycleState.CLIENT_CONNECTED) == (event.getState())) {
                    connectedLatch.countDown();
                }
            }
        });
        blockMessagesToInstance(server2, client);
        ownerServer.shutdown();
        sleepAtLeastMillis(((TimeUnit.SECONDS.toMillis(endpointDelaySeconds)) * 2));
        unblockMessagesToInstance(server2, client);
        assertOpenEventually(disconnectedLatch);
        assertOpenEventually(connectedLatch);
        clusterSize = (clusterSize) - 1;
        validateRegistrationsAndListenerFunctionality();
    }

    @Test
    @Category(SlowTest.class)
    public void testListenersWhenClientDisconnectedOperationRuns_whenOwnerConnectionRemoved() {
        Config config = new Config();
        int endpointDelaySeconds = 10;
        config.setProperty(CLIENT_ENDPOINT_REMOVE_DELAY_SECONDS.getName(), String.valueOf(endpointDelaySeconds));
        config.setProperty(CLIENT_HEARTBEAT_TIMEOUT_SECONDS.getName(), "20");
        HazelcastInstance ownerServer = factory.newHazelcastInstance(config);
        ClientConfig smartClientConfig = getSmartClientConfig();
        client = factory.newHazelcastClient(smartClientConfig);
        factory.newHazelcastInstance(config);
        setupListener();
        final CountDownLatch disconnectedLatch = new CountDownLatch(1);
        final CountDownLatch connectedLatch = new CountDownLatch(1);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((LifecycleState.CLIENT_DISCONNECTED) == (event.getState())) {
                    disconnectedLatch.countDown();
                }
                if ((LifecycleState.CLIENT_CONNECTED) == (event.getState())) {
                    connectedLatch.countDown();
                }
            }
        });
        blockMessagesToInstance(ownerServer, client);
        assertOpenEventually(disconnectedLatch);
        sleepAtLeastMillis(((TimeUnit.SECONDS.toMillis(endpointDelaySeconds)) * 2));
        unblockMessagesToInstance(ownerServer, client);
        assertOpenEventually(connectedLatch);
        validateRegistrationsAndListenerFunctionality();
    }

    // -------------------------- testListenersTemporaryNetworkBlockage --------------------- //
    @Test
    public void testTemporaryBlockedNoDisconnectionSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTemporaryNetworkBlockage();
    }

    @Test
    public void testTemporaryBlockedNoDisconnectionNonSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getNonSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTemporaryNetworkBlockage();
    }

    @Test
    public void testTemporaryBlockedNoDisconnectionMultipleServerSmartRouting() {
        newInstances(null, 3);
        ClientConfig clientConfig = getSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTemporaryNetworkBlockage();
    }

    @Test
    public void testTemporaryBlockedNoDisconnectionMultipleServerNonSmartRouting() {
        newInstances(null, 3);
        ClientConfig clientConfig = getNonSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTemporaryNetworkBlockage();
    }

    // -------------------------- testListenersHeartbeatTimeoutToOwner --------------------- //
    @Test
    public void testClusterReconnectDueToHeartbeatSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersHeartbeatTimeoutToOwner();
    }

    @Test
    public void testClusterReconnectMultipleServersDueToHeartbeatSmartRouting() {
        newInstances(null, 3);
        ClientConfig clientConfig = getSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersHeartbeatTimeoutToOwner();
    }

    @Test
    public void testClusterReconnectDueToHeartbeatNonSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getNonSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersHeartbeatTimeoutToOwner();
    }

    @Test
    public void testClusterReconnectMultipleServerDueToHeartbeatNonSmartRouting() {
        newInstances(null, 3);
        ClientConfig clientConfig = getNonSmartClientConfigWithHeartbeat();
        client = factory.newHazelcastClient(clientConfig);
        testListenersHeartbeatTimeoutToOwner();
    }

    // -------------------------- testListenersTerminateOwnerNode --------------------- //
    @Test
    public void testListenersSmartRoutingMultipleServer() {
        newInstances(null, 3);
        ClientConfig clientConfig = getSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateOwnerNode();
    }

    @Test
    public void testListenersNonSmartRoutingMultipleServer() {
        newInstances(null, 3);
        ClientConfig clientConfig = getNonSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateOwnerNode();
    }

    @Test
    public void testListenersSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateOwnerNode();
    }

    @Test
    public void testListenersNonSmartRouting() {
        newHazelcastInstance();
        ClientConfig clientConfig = getNonSmartClientConfig();
        client = factory.newHazelcastClient(clientConfig);
        testListenersTerminateOwnerNode();
    }
}

