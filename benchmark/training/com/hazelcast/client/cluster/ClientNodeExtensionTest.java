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
package com.hazelcast.client.cluster;


import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.mocknetwork.MockNodeContext;
import com.hazelcast.test.mocknetwork.TestNodeRegistry;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientNodeExtensionTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory;

    private class ManagedExtensionNodeContext extends MockNodeContext {
        private AtomicBoolean startupDone;

        protected ManagedExtensionNodeContext(TestNodeRegistry registry, Address thisAddress, boolean isStarted) {
            super(registry, thisAddress);
            startupDone = new AtomicBoolean(isStarted);
        }

        @Override
        public NodeExtension createNodeExtension(Node node) {
            return new DefaultNodeExtension(node) {
                @Override
                public boolean isStartCompleted() {
                    return (startupDone.get()) && (super.isStartCompleted());
                }
            };
        }

        public void setStartupDone(boolean started) {
            this.startupDone.set(started);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void test_canNotConnect_whenNodeExtensionIsNotComplete() throws UnknownHostException {
        HazelcastInstanceFactory.newHazelcastInstance(new Config(), randomName(), new ClientNodeExtensionTest.ManagedExtensionNodeContext(getRegistry(), new Address("127.0.0.1", 5555), false));
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5555");
        factory.newHazelcastClient(clientConfig);
    }

    @Test(expected = OperationTimeoutException.class)
    public void test_canGetFromMap_whenNodeExtensionIsNotComplete() {
        IMap<Object, Object> map = null;
        ClientNodeExtensionTest.ManagedExtensionNodeContext nodeContext = null;
        try {
            nodeContext = new ClientNodeExtensionTest.ManagedExtensionNodeContext(getRegistry(), new Address("127.0.0.1", 5555), true);
            HazelcastInstanceFactory.newHazelcastInstance(new Config(), randomName(), nodeContext);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), "3");
            clientConfig.getNetworkConfig().addAddress("127.0.0.1:5555");
            HazelcastInstance hazelcastClient = factory.newHazelcastClient(clientConfig);
            map = hazelcastClient.getMap(randomMapName());
            Assert.assertNull(map.get("dummy"));
        } catch (Throwable t) {
            Assert.fail(("Should not throw exception! Error:" + t));
        }
        nodeContext.setStartupDone(false);
        map.get("dummy");
    }
}

