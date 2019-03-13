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
package com.hazelcast.client.config;


import Employee.CLASS_ID;
import PortableFactory.FACTORY_ID;
import com.hazelcast.client.test.PortableFactory;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientConfigTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testCopyConstructor_withFullyConfiguredClientConfig() throws IOException {
        URL schemaResource = ClientConfigTest.class.getClassLoader().getResource("hazelcast-client-full.xml");
        ClientConfig expected = new XmlClientConfigBuilder(schemaResource).build();
        ClientConfig actual = new ClientConfig(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCopyConstructor_withDefaultClientConfig() {
        ClientConfig expected = new ClientConfig();
        ClientConfig actual = new ClientConfig(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAccessGroupNameOverClientInstance() {
        Config config = new Config();
        String groupName = "aGroupName";
        config.getGroupConfig().setName(groupName);
        hazelcastFactory.newHazelcastInstance(config);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getGroupConfig().setName(groupName);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        Assert.assertEquals(groupName, client.getConfig().getGroupConfig().getName());
    }

    @Test
    public void testAccessSerializationConfigOverClientInstance() {
        newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().addPortableFactory(FACTORY_ID, new PortableFactory());
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        SerializationConfig serializationConfig = client.getConfig().getSerializationConfig();
        Map<Integer, com.hazelcast.nio.serialization.PortableFactory> factories = serializationConfig.getPortableFactories();
        Assert.assertEquals(1, factories.size());
        Assert.assertEquals(factories.get(FACTORY_ID).create(CLASS_ID).getClassId(), CLASS_ID);
    }

    @Test
    public void testUserContext_passContext() {
        newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        ConcurrentMap<String, Object> context = new ConcurrentHashMap<String, Object>();
        context.put("key1", "value1");
        Object value2 = new Object();
        context.put("key2", value2);
        // check set setter returns ClientConfig instance
        clientConfig = clientConfig.setUserContext(context);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        ConcurrentMap<String, Object> returnedContext = client.getUserContext();
        Assert.assertEquals(context, returnedContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUserContext_throwExceptionWhenContextNull() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setUserContext(null);
    }

    @Test
    public void testReliableTopic() {
        ClientConfig clientConfig = new ClientConfig();
        ClientReliableTopicConfig defaultReliableTopicConfig = new ClientReliableTopicConfig("default");
        defaultReliableTopicConfig.setReadBatchSize(100);
        clientConfig.addReliableTopicConfig(defaultReliableTopicConfig);
        ClientReliableTopicConfig newConfig = clientConfig.getReliableTopicConfig("newConfig");
        Assert.assertEquals(100, newConfig.getReadBatchSize());
    }
}

