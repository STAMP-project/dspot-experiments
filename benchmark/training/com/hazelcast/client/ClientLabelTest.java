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
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientLabelTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private final String label = "attributeValue";

    private final String nonExistingLabel = "nonExistingLabel";

    @Test
    public void test_clientLabel_overClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addLabel(label);
        TestCase.assertTrue(clientConfig.getLabels().contains(label));
        Assert.assertEquals(1, clientConfig.getLabels().size());
        Assert.assertFalse(clientConfig.getLabels().contains(nonExistingLabel));
    }

    @Test
    public void test_clientLabel_overGetConnectedClients() {
        HazelcastInstance instance = newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addLabel(label);
        hazelcastFactory.newHazelcastClient(clientConfig);
        Collection<Client> connectedClients = instance.getClientService().getConnectedClients();
        Client client = connectedClients.iterator().next();
        Assert.assertEquals(1, client.getLabels().size());
        HazelcastTestSupport.assertContains(client.getLabels(), label);
        HazelcastTestSupport.assertNotContains(client.getLabels(), nonExistingLabel);
    }

    @Test
    public void test_clientAttribute_overClientConnectedEvent() {
        HazelcastInstance instance = newHazelcastInstance();
        final CountDownLatch clientConnected = new CountDownLatch(1);
        final AtomicReference<Client> clientRef = new AtomicReference<Client>();
        instance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                clientRef.set(client);
                clientConnected.countDown();
            }

            @Override
            public void clientDisconnected(Client client) {
            }
        });
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addLabel(label);
        hazelcastFactory.newHazelcastClient(clientConfig);
        HazelcastTestSupport.assertOpenEventually(clientConnected);
        Client client = clientRef.get();
        Assert.assertEquals(1, client.getLabels().size());
        HazelcastTestSupport.assertContains(client.getLabels(), label);
        HazelcastTestSupport.assertNotContains(client.getLabels(), nonExistingLabel);
    }

    @Test
    public void test_clientAttribute_overClientDisconnectedEvent() {
        HazelcastInstance instance = newHazelcastInstance();
        final CountDownLatch clientDisconnected = new CountDownLatch(1);
        final AtomicReference<Client> clientRef = new AtomicReference<Client>();
        instance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
            }

            @Override
            public void clientDisconnected(Client client) {
                clientRef.set(client);
                clientDisconnected.countDown();
            }
        });
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addLabel(label);
        HazelcastInstance clientInstance = hazelcastFactory.newHazelcastClient(clientConfig);
        clientInstance.shutdown();
        HazelcastTestSupport.assertOpenEventually(clientDisconnected);
        Client client = clientRef.get();
        Assert.assertEquals(1, client.getLabels().size());
        HazelcastTestSupport.assertContains(client.getLabels(), label);
        HazelcastTestSupport.assertNotContains(client.getLabels(), nonExistingLabel);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_modifyClientLabels_overGetConnectedClients() {
        HazelcastInstance instance = newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addLabel(label);
        hazelcastFactory.newHazelcastClient(clientConfig);
        Collection<Client> connectedClients = instance.getClientService().getConnectedClients();
        Client client = connectedClients.iterator().next();
        client.getLabels().add(label);
    }
}

