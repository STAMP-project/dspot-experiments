/**
 * Copyright (c) [2016] [ <ether.camp> ]
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
package org.ethereum.net.rlpx;


import KademliaOptions.BUCKET_SIZE;
import java.util.List;
import org.ethereum.net.rlpx.discover.table.KademliaOptions;
import org.ethereum.net.rlpx.discover.table.NodeTable;
import org.junit.Assert;
import org.junit.Test;


public class KademliaTest {
    @Test
    public void test2() {
        NodeTable t = KademliaTest.getTestNodeTable(0);
        Node n = KademliaTest.getNode();
        t.addNode(n);
        Assert.assertTrue(KademliaTest.containsNode(t, n));
        t.dropNode(n);
        Assert.assertFalse(KademliaTest.containsNode(t, n));
    }

    @Test
    public void test3() {
        NodeTable t = KademliaTest.getTestNodeTable(1000);
        KademliaTest.showBuckets(t);
        List<Node> closest1 = t.getClosestNodes(t.getNode().getId());
        List<Node> closest2 = t.getClosestNodes(KademliaTest.getNodeId());
        Assert.assertNotEquals(closest1, closest2);
    }

    @Test
    public void test4() {
        NodeTable t = KademliaTest.getTestNodeTable(0);
        Node homeNode = t.getNode();
        // t.getBucketsCount() returns non empty buckets
        Assert.assertEquals(t.getBucketsCount(), 1);
        // creates very close nodes
        for (int i = 1; i < (KademliaOptions.BUCKET_SIZE); i++) {
            Node n = KademliaTest.getNode(homeNode.getId(), i);
            t.addNode(n);
        }
        Assert.assertEquals(t.getBucketsCount(), 1);
        Assert.assertEquals(t.getBuckets()[0].getNodesCount(), BUCKET_SIZE);
    }
}

