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


import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.ClientTestUtil;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.spi.ProxyManager;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ProxyManagerTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory;

    @Test
    public void testNextAddressToSendCreateRequestOnSingleDataMember() {
        final List<HazelcastInstance> instances = createNodes(3, 1);
        final Address dataInstanceAddress = getAddress(instances.get(3));
        final HazelcastInstance client = factory.newHazelcastClient();
        final HazelcastClientInstanceImpl clientInstanceImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
        final ProxyManager proxyManager = clientInstanceImpl.getProxyManager();
        for (int i = 0; i < (instances.size()); i++) {
            Assert.assertEquals(dataInstanceAddress, proxyManager.findNextAddressToSendCreateRequest());
        }
    }

    @Test
    public void testNextAddressToSendCreateRequestOnMultipleDataMembers() {
        final List<HazelcastInstance> instances = createNodes(3, 3);
        final HazelcastInstance client = factory.newHazelcastClient();
        final HazelcastClientInstanceImpl clientInstanceImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
        Set<Address> addresses = new HashSet<Address>();
        final ProxyManager proxyManager = clientInstanceImpl.getProxyManager();
        for (int i = 0; i < ((instances.size()) * 100); i++) {
            addresses.add(proxyManager.findNextAddressToSendCreateRequest());
        }
        Assert.assertEquals(3, addresses.size());
        for (HazelcastInstance lite : instances.subList(3, 6)) {
            assertContains(addresses, getAddress(lite));
        }
    }

    @Test
    public void testNextAddressToSendCreateRequestOnMultipleLiteMembers() {
        final List<HazelcastInstance> instances = createNodes(3, 0);
        final HazelcastInstance client = factory.newHazelcastClient();
        final HazelcastClientInstanceImpl clientInstanceImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
        Set<Address> addresses = new HashSet<Address>();
        final ProxyManager proxyManager = clientInstanceImpl.getProxyManager();
        for (int i = 0; i < ((instances.size()) * 100); i++) {
            addresses.add(proxyManager.findNextAddressToSendCreateRequest());
        }
        Assert.assertEquals(1, addresses.size());
    }

    @Test(expected = OperationTimeoutException.class)
    public void testProxyCreateTimeout_whenClusterIsNotReachable() {
        HazelcastInstance instance = newHazelcastInstance();
        ClientConfig config = new ClientConfig();
        config.setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), "1");
        config.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        HazelcastInstance client = factory.newHazelcastClient(config);
        instance.shutdown();
        client.getMap("test");
    }
}

