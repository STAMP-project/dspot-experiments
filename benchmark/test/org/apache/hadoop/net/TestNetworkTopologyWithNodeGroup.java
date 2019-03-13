/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.net;


import NodeBase.ROOT;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestNetworkTopologyWithNodeGroup {
    private static final NetworkTopologyWithNodeGroup cluster = new NetworkTopologyWithNodeGroup();

    private static final NodeBase[] dataNodes = new NodeBase[]{ new NodeBase("h1", "/d1/r1/s1"), new NodeBase("h2", "/d1/r1/s1"), new NodeBase("h3", "/d1/r1/s2"), new NodeBase("h4", "/d1/r2/s3"), new NodeBase("h5", "/d1/r2/s3"), new NodeBase("h6", "/d1/r2/s4"), new NodeBase("h7", "/d2/r3/s5"), new NodeBase("h8", "/d2/r3/s6") };

    private static final NodeBase computeNode = new NodeBase("/d1/r1/s1/h9");

    private static final NodeBase rackOnlyNode = new NodeBase("h10", "/r2");

    static {
        for (int i = 0; i < (TestNetworkTopologyWithNodeGroup.dataNodes.length); i++) {
            TestNetworkTopologyWithNodeGroup.cluster.add(TestNetworkTopologyWithNodeGroup.dataNodes[i]);
        }
    }

    @Test
    public void testNumOfChildren() throws Exception {
        Assert.assertEquals(TestNetworkTopologyWithNodeGroup.dataNodes.length, TestNetworkTopologyWithNodeGroup.cluster.getNumOfLeaves());
    }

    @Test
    public void testNumOfRacks() throws Exception {
        Assert.assertEquals(3, TestNetworkTopologyWithNodeGroup.cluster.getNumOfRacks());
    }

    @Test
    public void testRacks() throws Exception {
        Assert.assertEquals(3, TestNetworkTopologyWithNodeGroup.cluster.getNumOfRacks());
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[1]));
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[1], TestNetworkTopologyWithNodeGroup.dataNodes[2]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[2], TestNetworkTopologyWithNodeGroup.dataNodes[3]));
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[3], TestNetworkTopologyWithNodeGroup.dataNodes[4]));
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[4], TestNetworkTopologyWithNodeGroup.dataNodes[5]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[5], TestNetworkTopologyWithNodeGroup.dataNodes[6]));
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameRack(TestNetworkTopologyWithNodeGroup.dataNodes[6], TestNetworkTopologyWithNodeGroup.dataNodes[7]));
    }

    @Test
    public void testNodeGroups() throws Exception {
        Assert.assertEquals(3, TestNetworkTopologyWithNodeGroup.cluster.getNumOfRacks());
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[1]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[1], TestNetworkTopologyWithNodeGroup.dataNodes[2]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[2], TestNetworkTopologyWithNodeGroup.dataNodes[3]));
        Assert.assertTrue(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[3], TestNetworkTopologyWithNodeGroup.dataNodes[4]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[4], TestNetworkTopologyWithNodeGroup.dataNodes[5]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[5], TestNetworkTopologyWithNodeGroup.dataNodes[6]));
        Assert.assertFalse(TestNetworkTopologyWithNodeGroup.cluster.isOnSameNodeGroup(TestNetworkTopologyWithNodeGroup.dataNodes[6], TestNetworkTopologyWithNodeGroup.dataNodes[7]));
    }

    @Test
    public void testGetDistance() throws Exception {
        Assert.assertEquals(0, TestNetworkTopologyWithNodeGroup.cluster.getDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[0]));
        Assert.assertEquals(2, TestNetworkTopologyWithNodeGroup.cluster.getDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[1]));
        Assert.assertEquals(4, TestNetworkTopologyWithNodeGroup.cluster.getDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[2]));
        Assert.assertEquals(6, TestNetworkTopologyWithNodeGroup.cluster.getDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[3]));
        Assert.assertEquals(8, TestNetworkTopologyWithNodeGroup.cluster.getDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], TestNetworkTopologyWithNodeGroup.dataNodes[6]));
    }

    @Test
    public void testSortByDistance() throws Exception {
        NodeBase[] testNodes = new NodeBase[4];
        // array contains both local node, local node group & local rack node
        testNodes[0] = TestNetworkTopologyWithNodeGroup.dataNodes[1];
        testNodes[1] = TestNetworkTopologyWithNodeGroup.dataNodes[2];
        testNodes[2] = TestNetworkTopologyWithNodeGroup.dataNodes[3];
        testNodes[3] = TestNetworkTopologyWithNodeGroup.dataNodes[0];
        TestNetworkTopologyWithNodeGroup.cluster.sortByDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], testNodes, testNodes.length);
        Assert.assertTrue(((testNodes[0]) == (TestNetworkTopologyWithNodeGroup.dataNodes[0])));
        Assert.assertTrue(((testNodes[1]) == (TestNetworkTopologyWithNodeGroup.dataNodes[1])));
        Assert.assertTrue(((testNodes[2]) == (TestNetworkTopologyWithNodeGroup.dataNodes[2])));
        Assert.assertTrue(((testNodes[3]) == (TestNetworkTopologyWithNodeGroup.dataNodes[3])));
        // array contains local node & local node group
        testNodes[0] = TestNetworkTopologyWithNodeGroup.dataNodes[3];
        testNodes[1] = TestNetworkTopologyWithNodeGroup.dataNodes[4];
        testNodes[2] = TestNetworkTopologyWithNodeGroup.dataNodes[1];
        testNodes[3] = TestNetworkTopologyWithNodeGroup.dataNodes[0];
        TestNetworkTopologyWithNodeGroup.cluster.sortByDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], testNodes, testNodes.length);
        Assert.assertTrue(((testNodes[0]) == (TestNetworkTopologyWithNodeGroup.dataNodes[0])));
        Assert.assertTrue(((testNodes[1]) == (TestNetworkTopologyWithNodeGroup.dataNodes[1])));
        // array contains local node & rack node
        testNodes[0] = TestNetworkTopologyWithNodeGroup.dataNodes[5];
        testNodes[1] = TestNetworkTopologyWithNodeGroup.dataNodes[3];
        testNodes[2] = TestNetworkTopologyWithNodeGroup.dataNodes[2];
        testNodes[3] = TestNetworkTopologyWithNodeGroup.dataNodes[0];
        TestNetworkTopologyWithNodeGroup.cluster.sortByDistance(TestNetworkTopologyWithNodeGroup.dataNodes[0], testNodes, testNodes.length);
        Assert.assertTrue(((testNodes[0]) == (TestNetworkTopologyWithNodeGroup.dataNodes[0])));
        Assert.assertTrue(((testNodes[1]) == (TestNetworkTopologyWithNodeGroup.dataNodes[2])));
        // array contains local-nodegroup node (not a data node also) & rack node
        testNodes[0] = TestNetworkTopologyWithNodeGroup.dataNodes[6];
        testNodes[1] = TestNetworkTopologyWithNodeGroup.dataNodes[7];
        testNodes[2] = TestNetworkTopologyWithNodeGroup.dataNodes[2];
        testNodes[3] = TestNetworkTopologyWithNodeGroup.dataNodes[0];
        TestNetworkTopologyWithNodeGroup.cluster.sortByDistance(TestNetworkTopologyWithNodeGroup.computeNode, testNodes, testNodes.length);
        Assert.assertTrue(((testNodes[0]) == (TestNetworkTopologyWithNodeGroup.dataNodes[0])));
        Assert.assertTrue(((testNodes[1]) == (TestNetworkTopologyWithNodeGroup.dataNodes[2])));
    }

    /**
     * This test checks that chooseRandom works for an excluded node.
     */
    /**
     * Test replica placement policy in case last node is invalid.
     * We create 6 nodes but the last node is in fault topology (with rack info),
     * so cannot be added to cluster. We should test proper exception is thrown in
     * adding node but shouldn't affect the cluster.
     */
    @Test
    public void testChooseRandomExcludedNode() {
        String scope = "~" + (NodeBase.getPath(TestNetworkTopologyWithNodeGroup.dataNodes[0]));
        Map<Node, Integer> frequency = pickNodesAtRandom(100, scope);
        for (Node key : TestNetworkTopologyWithNodeGroup.dataNodes) {
            // all nodes except the first should be more than zero
            Assert.assertTrue((((frequency.get(key)) > 0) || (key == (TestNetworkTopologyWithNodeGroup.dataNodes[0]))));
        }
    }

    @Test
    public void testNodeGroup() throws Exception {
        String res = TestNetworkTopologyWithNodeGroup.cluster.getNodeGroup("");
        Assert.assertTrue("NodeGroup should be NodeBase.ROOT for empty location", res.equals(ROOT));
        try {
            TestNetworkTopologyWithNodeGroup.cluster.getNodeGroup(null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("Null Network Location should throw exception!", e.getMessage().contains("Network Location is null"));
        }
    }

    /**
     * This test checks that adding a node with invalid topology will be failed
     * with an exception to show topology is invalid.
     */
    @Test
    public void testAddNodeWithInvalidTopology() {
        // The last node is a node with invalid topology
        try {
            TestNetworkTopologyWithNodeGroup.cluster.add(TestNetworkTopologyWithNodeGroup.rackOnlyNode);
            Assert.fail("Exception should be thrown, so we should not have reached here.");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                Assert.fail(("Expecting IllegalArgumentException, but caught:" + e));
            }
            Assert.assertTrue(e.getMessage().contains("illegal network location"));
        }
    }
}

