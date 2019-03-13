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


import ClientConnectionStrategyConfig.ReconnectMode.ASYNC;
import ClientConnectionStrategyConfig.ReconnectMode.ON;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.connection.nio.ClientConnectionManagerImpl;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClientRegressionWithRealNetworkTest extends ClientTestSupport {
    @Test
    public void testClientPortConnection() {
        final Config config1 = new Config();
        config1.getGroupConfig().setName("foo");
        config1.getNetworkConfig().setPort(5701);
        final HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config1);
        instance1.getMap("map").put("key", "value");
        final Config config2 = new Config();
        config2.getGroupConfig().setName("bar");
        config2.getNetworkConfig().setPort(5702);
        Hazelcast.newHazelcastInstance(config2);
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName("bar");
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        final IMap<Object, Object> map = client.getMap("map");
        Assert.assertNull(map.put("key", "value"));
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testClientConnectionBeforeServerReady() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Hazelcast.newHazelcastInstance();
            }
        });
        final CountDownLatch clientLatch = new CountDownLatch(1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                ClientConfig config = new ClientConfig();
                config.getNetworkConfig().setConnectionAttemptLimit(10);
                HazelcastClient.newHazelcastClient(config);
                clientLatch.countDown();
            }
        });
        assertOpenEventually(clientLatch);
    }

    @Test
    public void testConnectionCountAfterOwnerReconnect_memberHostname_clientIp() {
        testConnectionCountAfterOwnerReconnect("localhost", "127.0.0.1");
    }

    @Test
    public void testConnectionCountAfterOwnerReconnect_memberHostname_clientHostname() {
        testConnectionCountAfterOwnerReconnect("localhost", "localhost");
    }

    @Test
    public void testConnectionCountAfterOwnerReconnect_memberIp_clientIp() {
        testConnectionCountAfterOwnerReconnect("127.0.0.1", "127.0.0.1");
    }

    @Test
    public void testConnectionCountAfterOwnerReconnect_memberIp_clientHostname() {
        testConnectionCountAfterOwnerReconnect("127.0.0.1", "localhost");
    }

    @Test
    public void testListenersAfterOwnerDisconnect_memberHostname_clientIp() {
        testListenersAfterOwnerDisconnect("localhost", "127.0.0.1");
    }

    @Test
    public void testListenersAfterOwnerDisconnect_memberHostname_clientHostname() {
        testListenersAfterOwnerDisconnect("localhost", "localhost");
    }

    @Test
    public void testListenersAfterOwnerDisconnect_memberIp_clientIp() {
        testListenersAfterOwnerDisconnect("127.0.0.1", "127.0.0.1");
    }

    @Test
    public void testListenersAfterOwnerDisconnect_memberIp_clientHostname() {
        testListenersAfterOwnerDisconnect("127.0.0.1", "localhost");
    }

    @Test
    public void testOperationsContinueWhenOwnerDisconnected_reconnectModeAsync() throws Exception {
        testOperationsContinueWhenOwnerDisconnected(ASYNC);
    }

    @Test
    public void testOperationsContinueWhenOwnerDisconnected_reconnectModeOn() throws Exception {
        testOperationsContinueWhenOwnerDisconnected(ON);
    }

    @Test
    public void testNioChannelLeakTest() {
        ClientConfig config = new ClientConfig();
        config.getConnectionStrategyConfig().setAsyncStart(true).setReconnectMode(ASYNC).getConnectionRetryConfig().setEnabled(true).setInitialBackoffMillis(1).setMaxBackoffMillis(1000);
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final HazelcastClientInstanceImpl clientInstanceImpl = getHazelcastClientInstanceImpl(client);
        final ClientConnectionManagerImpl connectionManager = ((ClientConnectionManagerImpl) (clientInstanceImpl.getConnectionManager()));
        sleepSeconds(2);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, connectionManager.getNetworking().getChannels().size());
            }
        });
        client.shutdown();
    }
}

