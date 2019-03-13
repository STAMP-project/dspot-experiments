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
package com.hazelcast.client.usercodedeployment;


import LifecycleEvent.LifecycleState;
import UserCodeDeploymentConfig.ClassCacheMode;
import UserCodeDeploymentConfig.ProviderMode;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import usercodedeployment.CapitalizatingFirstnameExtractor;
import usercodedeployment.EntryProcessorWithAnonymousAndInner;
import usercodedeployment.IncrementingEntryProcessor;
import usercodedeployment.Person;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class })
public class ClientUserCodeDeploymentTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Parameterized.Parameter(0)
    public ClassCacheMode classCacheMode;

    @Parameterized.Parameter(1)
    public ProviderMode providerMode;

    @Test
    public void testSingleMember() {
        ClientConfig clientConfig = createClientConfig();
        Config config = createNodeConfig();
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        assertCodeDeploymentWorking(client, new IncrementingEntryProcessor());
    }

    @Test
    public void testWithMultipleMembers() {
        ClientConfig clientConfig = createClientConfig();
        Config config = createNodeConfig();
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        factory.newHazelcastInstance(config);
        assertCodeDeploymentWorking(client, new IncrementingEntryProcessor());
    }

    @Test
    public void testWithMultipleMembersAtStart() {
        ClientConfig clientConfig = createClientConfig();
        Config config = createNodeConfig();
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        assertCodeDeploymentWorking(client, new IncrementingEntryProcessor());
    }

    @Test
    public void testWithMultipleNodes_clientReconnectsToNewNode() {
        ClientConfig clientConfig = createClientConfig();
        Config config = createNodeConfig();
        HazelcastInstance firstInstance = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        factory.newHazelcastInstance(config);
        final CountDownLatch clientReconnectedLatch = new CountDownLatch(1);
        client.getLifecycleService().addLifecycleListener(new ClientUserCodeDeploymentTest.ClientReconnectionListener(clientReconnectedLatch));
        firstInstance.getLifecycleService().shutdown();
        assertOpenEventually(clientReconnectedLatch);
        assertCodeDeploymentWorking(client, new IncrementingEntryProcessor());
    }

    @Test
    public void testWithMultipleMembers_anonymousAndInnerClasses() {
        ClientConfig clientConfig = new ClientConfig();
        ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
        clientUserCodeDeploymentConfig.addJar("EntryProcessorWithAnonymousAndInner.jar");
        clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig.setEnabled(true));
        Config config = createNodeConfig();
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        factory.newHazelcastInstance(config);
        assertCodeDeploymentWorking(client, new EntryProcessorWithAnonymousAndInner());
    }

    @Test
    public void testCustomAttributeExtractor() {
        String mapName = randomMapName();
        String attributeName = "syntheticAttribute";// this attribute does not exist in the domain class

        ClientConfig clientConfig = new ClientConfig();
        ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
        clientUserCodeDeploymentConfig.addClass(CapitalizatingFirstnameExtractor.class);
        clientUserCodeDeploymentConfig.addClass(Person.class);
        clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig.setEnabled(true));
        Config config = createNodeConfig();
        config.getMapConfig(mapName).addMapAttributeConfig(new MapAttributeConfig(attributeName, "usercodedeployment.CapitalizatingFirstnameExtractor"));
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        IMap<Integer, Person> map = client.getMap(mapName);
        map.put(0, new Person("ada"));
        map.put(1, new Person("non-ada"));
        Set<Map.Entry<Integer, Person>> results = map.entrySet(equal(attributeName, "ADA"));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("ada", results.iterator().next().getValue().getName());
    }

    private static class ClientReconnectionListener implements LifecycleListener {
        private final CountDownLatch clientReconnectedLatch;

        private ClientReconnectionListener(CountDownLatch clientReconnectedLatch) {
            this.clientReconnectedLatch = clientReconnectedLatch;
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.CLIENT_CONNECTED)) {
                clientReconnectedLatch.countDown();
            }
        }
    }
}

