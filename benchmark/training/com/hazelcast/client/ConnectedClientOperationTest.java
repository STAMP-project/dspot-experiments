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


import ClientEngineImpl.SERVICE_NAME;
import ClientType.CPP;
import ClientType.CSHARP;
import ClientType.GO;
import ClientType.JAVA;
import ClientType.NODEJS;
import ClientType.OTHER;
import ClientType.PYTHON;
import com.hazelcast.client.impl.operations.GetConnectedClientsOperation;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.ClientType;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConnectedClientOperationTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testNumberOfConnectedClients() throws Exception {
        HazelcastInstance h1 = newHazelcastInstance();
        HazelcastInstance h2 = newHazelcastInstance();
        assertClusterSize(2, h1, h2);
        int numberOfClients = 6;
        for (int i = 0; i < numberOfClients; i++) {
            factory.newHazelcastClient();
        }
        Node node = getNode(h1);
        Map<ClientType, Integer> clientStats = node.clientEngine.getConnectedClientStats();
        Assert.assertEquals(numberOfClients, clientStats.get(JAVA).intValue());
        Assert.assertEquals(0, clientStats.get(CPP).intValue());
        Assert.assertEquals(0, clientStats.get(CSHARP).intValue());
        Assert.assertEquals(0, clientStats.get(NODEJS).intValue());
        Assert.assertEquals(0, clientStats.get(PYTHON).intValue());
        Assert.assertEquals(0, clientStats.get(GO).intValue());
        Assert.assertEquals(0, clientStats.get(OTHER).intValue());
    }

    @Test
    public void testGetConnectedClientsOperation_WhenZeroClientConnects() throws Exception {
        HazelcastInstance instance = newHazelcastInstance();
        Node node = getNode(instance);
        Operation operation = new GetConnectedClientsOperation();
        OperationService operationService = node.nodeEngine.getOperationService();
        Future<Map<String, ClientType>> future = operationService.invokeOnTarget(SERVICE_NAME, operation, node.address);
        Map<String, ClientType> clients = future.get();
        Assert.assertEquals(0, clients.size());
    }

    @Test
    public void testGetConnectedClientsOperation_WhenMoreThanZeroClientConnects() throws Exception {
        HazelcastInstance instance = newHazelcastInstance();
        factory.newHazelcastClient();
        factory.newHazelcastClient();
        Node node = getNode(instance);
        Operation operation = new GetConnectedClientsOperation();
        OperationService operationService = node.nodeEngine.getOperationService();
        Future<Map<String, ClientType>> future = operationService.invokeOnTarget(SERVICE_NAME, operation, node.address);
        Map<String, ClientType> clients = future.get();
        Assert.assertEquals(2, clients.size());
    }
}

