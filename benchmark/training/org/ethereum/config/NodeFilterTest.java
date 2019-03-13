/**
 * Copyright (c) [2017] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config;


import java.net.InetAddress;
import junit.framework.TestCase;
import org.ethereum.net.rlpx.Node;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


public class NodeFilterTest {
    private static final byte[] NODE_1 = "node-1".getBytes();

    private static final byte[] NODE_2 = "node-2".getBytes();

    @Test
    public void addByHostIpPattern() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "1.2.3.4");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
    }

    @Test
    public void doNotAcceptDifferentNodeNameButSameIp() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "1.2.3.4");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.4")));
    }

    @Test
    public void acceptDifferentNodeWithoutNameButSameIp() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(null, "1.2.3.4");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.4")));
    }

    @Test
    public void acceptDuplicateFilter() {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "1.2.3.4");
        filter.add(NodeFilterTest.NODE_1, "1.2.3.4");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
    }

    @Test
    public void acceptDifferentNodeNameButOverlappingIp() {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "1.2.3.4");
        filter.add(NodeFilterTest.NODE_2, "1.2.3.*");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.99")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("invalid-1", "1.2.4.1")));
    }

    @Test
    public void acceptMultipleWildcards() {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "1.2.3.*");
        filter.add(NodeFilterTest.NODE_2, "1.*.3.*");
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.99")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.99.99")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-2", "1.99.99.99")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "2.99.99.99")));
    }

    @Test
    public void addInvalidNodeShouldThrowException() throws Exception {
        NodeFilter filter = new NodeFilter();
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("invalid-1", null)));
    }

    @Test
    public void neverAcceptOnEmptyFilter() throws Exception {
        NodeFilter filter = new NodeFilter();
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.4")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.3.99")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "1.2.99.99")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "1.99.99.99")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-2", "2.99.99.99")));
    }

    @Test
    public void acceptByInetAddress() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "8.*");
        Assert.assertTrue(filter.accept(InetAddress.getByName("8.8.8.8")));
    }

    @Test
    public void doNotAcceptTheCatchAllWildcard() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "*");
        TestCase.assertFalse(filter.accept(InetAddress.getByName("1.2.3.4")));
        TestCase.assertFalse(filter.accept(NodeFilterTest.createTestNode("node-1", "255.255.255.255")));
    }

    @Test
    public void acceptNullIpPatternAsCatchAllForNodes() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, null);
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "1.2.3.4")));
        Assert.assertTrue(filter.accept(NodeFilterTest.createTestNode("node-1", "255.255.255.255")));
    }

    @Test
    public void acceptNullIpPatternAsCatchAllForInetAddresses() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, null);
        Assert.assertTrue(filter.accept(InetAddress.getByName("1.2.3.4")));
        Assert.assertTrue(filter.accept(InetAddress.getByName("255.255.255.255")));
    }

    @Test
    public void acceptLoopback() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(NodeFilterTest.NODE_1, "127.0.0.1");
        Assert.assertTrue(filter.accept(InetAddress.getByName("localhost")));
    }

    @Test
    public void doNotAcceptInvalidNodeHostnameWhenUsingPattern() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(null, "1.2.3.4");
        Node nodeWithInvalidHostname = new Node((("enode://" + (Hex.toHexString(NodeFilterTest.NODE_1))) + "@unknown:30303"));
        TestCase.assertFalse(filter.accept(nodeWithInvalidHostname));
    }

    @Test
    public void doNotAcceptInvalidNodeHostnameWhenUsingWildcard() throws Exception {
        NodeFilter filter = new NodeFilter();
        filter.add(null, null);
        Node nodeWithInvalidHostname = new Node((("enode://" + (Hex.toHexString(NodeFilterTest.NODE_1))) + "@unknown:30303"));
        TestCase.assertFalse(filter.accept(nodeWithInvalidHostname));
    }
}

