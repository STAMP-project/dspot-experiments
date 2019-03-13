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
package com.hazelcast.internal.cluster.impl;


import GroupProperty.TCP_JOIN_PORT_TRY_COUNT;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class DiscoveryJoinerTest {
    @Mock
    private DiscoveryService service = Mockito.mock(DiscoveryService.class);

    private List<DiscoveryNode> discoveryNodes;

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance hz;

    @Test
    public void test_DiscoveryJoiner_returns_public_address() {
        DiscoveryJoiner joiner = new DiscoveryJoiner(TestUtil.getNode(hz), service, true);
        Mockito.doReturn(discoveryNodes).when(service).discoverNodes();
        Collection<Address> addresses = joiner.getPossibleAddresses();
        Assert.assertEquals("[[127.0.0.1]:50001, [127.0.0.1]:50002]", addresses.toString());
    }

    @Test
    public void test_DiscoveryJoiner_returns_private_address() {
        DiscoveryJoiner joiner = new DiscoveryJoiner(TestUtil.getNode(hz), service, false);
        Mockito.doReturn(discoveryNodes).when(service).discoverNodes();
        Collection<Address> addresses = joiner.getPossibleAddresses();
        Assert.assertEquals("[[127.0.0.2]:50001, [127.0.0.2]:50002]", addresses.toString());
    }

    @Test
    public void test_DiscoveryJoinerJoin_whenTargetMemberSet() {
        Node node = TestUtil.getNode(hz);
        node.config.getNetworkConfig().getJoin().getTcpIpConfig().setRequiredMember("127.0.0.1");
        DiscoveryJoiner joiner = new DiscoveryJoiner(node, service, true);
        Mockito.doReturn(discoveryNodes).when(service).discoverNodes();
        joiner.join();
        Assert.assertTrue(node.getClusterService().isJoined());
    }

    @Test
    public void test_DiscoveryJoinerJoin_whenTargetMemberHasSameAddressAsNode() throws UnknownHostException {
        Node node = TestUtil.getNode(hz);
        String hostAddress = node.getThisAddress().getInetAddress().getHostAddress();
        node.config.getNetworkConfig().getJoin().getTcpIpConfig().setRequiredMember(hostAddress);
        List<DiscoveryNode> nodes = new ArrayList<DiscoveryNode>();
        nodes.add(new com.hazelcast.spi.discovery.SimpleDiscoveryNode(node.getThisAddress(), node.getThisAddress()));
        DiscoveryJoiner joiner = new DiscoveryJoiner(node, service, true);
        Mockito.doReturn(nodes).when(service).discoverNodes();
        joiner.join();
        Assert.assertTrue(node.getClusterService().isJoined());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_DiscoveryJoinerConstructor_throws_whenTryCountInvalid() {
        Node node = Mockito.spy(TestUtil.getNode(hz));
        HazelcastProperties properties = Mockito.mock(HazelcastProperties.class);
        Mockito.when(node.getProperties()).thenReturn(properties);
        Mockito.when(properties.getInteger(TCP_JOIN_PORT_TRY_COUNT)).thenReturn(0);
        new DiscoveryJoiner(node, service, false);
    }
}

