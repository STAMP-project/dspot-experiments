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


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientNameTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void test_clientName_overClientInstance() {
        newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        String name = "aClient";
        clientConfig.setInstanceName(name);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        Assert.assertEquals(name, client.getName());
    }

    @Test
    public void test_clientName_overGetConnectedClients() {
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        String name = "aClient";
        clientConfig.setInstanceName(name);
        hazelcastFactory.newHazelcastClient(clientConfig);
        Collection<Client> connectedClients = instance.getClientService().getConnectedClients();
        Client client = connectedClients.iterator().next();
        Assert.assertEquals(name, client.getName());
    }

    @Test
    public void test_clientName_overClientConnectedEvent() {
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance();
        final CountDownLatch clientConnected = new CountDownLatch(1);
        final AtomicReference<String> clientName = new AtomicReference<String>();
        instance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                clientName.set(client.getName());
                clientConnected.countDown();
            }

            @Override
            public void clientDisconnected(Client client) {
            }
        });
        ClientConfig clientConfig = new ClientConfig();
        String name = "aClient";
        clientConfig.setInstanceName(name);
        hazelcastFactory.newHazelcastClient(clientConfig);
        HazelcastTestSupport.assertOpenEventually(clientConnected);
        Assert.assertEquals(name, clientName.get());
    }

    @Test
    public void test_clientName_overClientDisconnectedEvent() {
        HazelcastInstance instance = hazelcastFactory.newHazelcastInstance();
        final CountDownLatch clientDisconnected = new CountDownLatch(1);
        final AtomicReference<String> clientName = new AtomicReference<String>();
        instance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
            }

            @Override
            public void clientDisconnected(Client client) {
                clientName.set(client.getName());
                clientDisconnected.countDown();
            }
        });
        ClientConfig clientConfig = new ClientConfig();
        String name = "aClient";
        clientConfig.setInstanceName(name);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        client.shutdown();
        HazelcastTestSupport.assertOpenEventually(clientDisconnected);
        Assert.assertEquals(name, clientName.get());
    }
}

