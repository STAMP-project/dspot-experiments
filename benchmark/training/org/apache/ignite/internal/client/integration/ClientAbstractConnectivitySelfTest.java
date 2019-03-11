/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.client.integration;


import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.ignite.internal.client.GridClient;
import org.apache.ignite.internal.client.GridClientNode;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests the REST client-server connectivity with various configurations.
 */
public abstract class ClientAbstractConnectivitySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String WILDCARD_IP = "0.0.0.0";

    /**
     *
     */
    private static final String LOOPBACK_IP = "127.0.0.1";

    /**
     * Tests correct behavior in case of 1 REST-enabled node
     * with default settings.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOneNodeDefaultHostAndPort() throws Exception {
        startRestNode("grid1", null, null);
        checkConnectivityByIp(ClientAbstractConnectivitySelfTest.LOOPBACK_IP, ClientAbstractConnectivitySelfTest.getAllIps());
        String extIp = F.find(U.allLocalIps(), null, new ClientAbstractConnectivitySelfTest.IpV4AddressPredicate());
        checkConnectivityByIp(extIp, ClientAbstractConnectivitySelfTest.getAllIps());
    }

    /**
     * Simple test of address list filtering.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testResolveReachableOneAddress() throws Exception {
        InetAddress addr = InetAddress.getByAddress(new byte[]{ 127, 0, 0, 1 });
        List<InetAddress> filtered = IgniteUtils.filterReachable(Collections.singletonList(addr));
        assertEquals(1, filtered.size());
        assertEquals(addr, filtered.get(0));
    }

    /**
     * Tests correct behavior in case of 1 REST-enabled node
     * with explicitly specified loopback address setting.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testOneNodeLoopbackHost() throws Exception {
        startRestNode("grid1", ClientAbstractConnectivitySelfTest.LOOPBACK_IP, defaultRestPort());
        checkConnectivityByIp(ClientAbstractConnectivitySelfTest.LOOPBACK_IP, F.t(((Collection<String>) (Collections.singleton(ClientAbstractConnectivitySelfTest.LOOPBACK_IP))), ((Collection<String>) (Collections.<String>emptySet()))));
    }

    /**
     * Tests correct behavior in case of 1 REST-enabled node
     * with explicitly specified 0.0.0.0 address.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testOneNodeZeroIpv4Address() throws Exception {
        startRestNode("grid1", ClientAbstractConnectivitySelfTest.WILDCARD_IP, defaultRestPort());
        Collection<String> addrs = new LinkedList<>();
        addrs.add(ClientAbstractConnectivitySelfTest.LOOPBACK_IP);
        Collection<String> nonLoopbackAddrs = U.allLocalIps();
        assertNotNull(nonLoopbackAddrs);
        addrs.addAll(F.view(nonLoopbackAddrs, new ClientAbstractConnectivitySelfTest.IpV4AddressPredicate()));
        // The client should be able to connect through all IPv4 addresses.
        for (String addr : addrs) {
            log.info(("Trying address: " + addr));
            GridClient cli = startClient(addr, defaultRestPort());
            List<GridClientNode> nodes = cli.compute().refreshTopology(true, false);
            assertEquals(1, nodes.size());
            GridClientNode node = F.first(nodes);
            assertNotNull(node);
            assertEquals(ClientAbstractConnectivitySelfTest.getAllIps().get1(), node.attribute(restAddressAttributeName()));
            assertEquals(ClientAbstractConnectivitySelfTest.getAllIps().get2(), node.attribute(restHostNameAttributeName()));
            List<String> nodeAddrs = node.tcpAddresses();
            assertTrue(nodeAddrs.contains(ClientAbstractConnectivitySelfTest.LOOPBACK_IP));
            assertTrue(F.containsAll(nodeAddrs, addrs));
        }
    }

    /**
     * Tests correct behavior in case of 2 REST-enabled nodes with default
     * settings.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testTwoNodesDefaultHostAndPort() throws Exception {
        startRestNode("grid1", null, null);
        startRestNode("grid2", null, null);
        GridClient cli = startClient(ClientAbstractConnectivitySelfTest.LOOPBACK_IP, defaultRestPort());
        List<GridClientNode> nodes = cli.compute().refreshTopology(true, false);
        assertEquals(2, nodes.size());
        assertTrue(F.forAll(nodes, new org.apache.ignite.internal.util.typedef.P1<GridClientNode>() {
            @Override
            public boolean apply(GridClientNode node) {
                return node.tcpAddresses().contains(ClientAbstractConnectivitySelfTest.LOOPBACK_IP);
            }
        }));
        GridTestUtils.assertOneToOne(nodes, new org.apache.ignite.internal.util.typedef.P1<GridClientNode>() {
            @Override
            public boolean apply(GridClientNode node) {
                try {
                    return (eqAddresses(ClientAbstractConnectivitySelfTest.getAllIps(), node)) && (Integer.valueOf(defaultRestPort()).equals(node.attribute(restPortAttributeName())));
                } catch (Exception ignored) {
                    return false;
                }
            }
        }, new org.apache.ignite.internal.util.typedef.P1<GridClientNode>() {
            @Override
            public boolean apply(GridClientNode node) {
                try {
                    return (eqAddresses(ClientAbstractConnectivitySelfTest.getAllIps(), node)) && (Integer.valueOf(((defaultRestPort()) + 1)).equals(node.attribute(restPortAttributeName())));
                } catch (Exception ignored) {
                    return false;
                }
            }
        });
    }

    /**
     * Tests correct behavior in case of shutdown node used to refresh topology state.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testRefreshTopologyOnNodeLeft() throws Exception {
        startRestNode("grid1", null, null);
        startRestNode("grid2", null, null);
        GridClient cli = startClient(ClientAbstractConnectivitySelfTest.LOOPBACK_IP, defaultRestPort());
        List<GridClientNode> nodes = cli.compute().refreshTopology(true, false);
        assertEquals(2, nodes.size());
        stopGrid("grid1");
        nodes = cli.compute().refreshTopology(true, false);
        assertEquals(1, nodes.size());
        startRestNode("grid3", null, null);
        nodes = cli.compute().refreshTopology(true, false);
        assertEquals(2, nodes.size());
        stopGrid("grid2");
        nodes = cli.compute().refreshTopology(true, false);
        assertEquals(1, nodes.size());
    }

    /**
     * Predicate that returns IPv4 address strings.
     */
    private static class IpV4AddressPredicate implements org.apache.ignite.internal.util.typedef.P1<String> {
        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(String s) {
            return s.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
        }
    }
}

