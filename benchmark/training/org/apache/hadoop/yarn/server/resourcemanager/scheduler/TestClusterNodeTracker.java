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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler;


import ResourceRequest.ANY;
import YarnConfiguration.RESOURCE_TYPES;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSSchedulerNode;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class to verify ClusterNodeTracker. Using FSSchedulerNode without
 * loss of generality.
 */
public class TestClusterNodeTracker {
    private ClusterNodeTracker<FSSchedulerNode> nodeTracker;

    @Test
    public void testGetNodeCount() {
        addEight4x4Nodes();
        Assert.assertEquals("Incorrect number of nodes in the cluster", 8, nodeTracker.nodeCount());
        Assert.assertEquals("Incorrect number of nodes in each rack", 4, nodeTracker.nodeCount("rack0"));
    }

    @Test
    public void testGetNodesForResourceName() throws Exception {
        addEight4x4Nodes();
        Assert.assertEquals("Incorrect number of nodes matching ANY", 8, nodeTracker.getNodesByResourceName(ANY).size());
        Assert.assertEquals("Incorrect number of nodes matching rack", 4, nodeTracker.getNodesByResourceName("rack0").size());
        Assert.assertEquals("Incorrect number of nodes matching node", 1, nodeTracker.getNodesByResourceName("host0").size());
    }

    @Test
    public void testMaxAllowedAllocation() {
        // Add a third resource
        Configuration conf = new Configuration();
        conf.set(RESOURCE_TYPES, "test1");
        ResourceUtils.resetResourceTypes(conf);
        setup();
        Resource maximum = Resource.newInstance(10240, 10, Collections.singletonMap("test1", 10L));
        nodeTracker.setConfiguredMaxAllocation(maximum);
        Resource result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("With no nodes added, the ClusterNodeTracker did not return " + "the configured max allocation"), maximum, result);
        List<RMNode> smallNodes = MockNodes.newNodes(1, 1, Resource.newInstance(1024, 2, Collections.singletonMap("test1", 4L)));
        FSSchedulerNode smallNode = new FSSchedulerNode(smallNodes.get(0), false);
        List<RMNode> mediumNodes = MockNodes.newNodes(1, 1, Resource.newInstance(4096, 2, Collections.singletonMap("test1", 2L)));
        FSSchedulerNode mediumNode = new FSSchedulerNode(mediumNodes.get(0), false);
        List<RMNode> largeNodes = MockNodes.newNodes(1, 1, Resource.newInstance(16384, 4, Collections.singletonMap("test1", 1L)));
        FSSchedulerNode largeNode = new FSSchedulerNode(largeNodes.get(0), false);
        nodeTracker.addNode(mediumNode);
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("With a single node added, the ClusterNodeTracker did not " + "return that node's resources as the maximum allocation"), mediumNodes.get(0).getTotalCapability(), result);
        nodeTracker.addNode(smallNode);
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("With two nodes added, the ClusterNodeTracker did not " + ("return a the maximum allocation that was the max of their aggregate " + "resources")), Resource.newInstance(4096, 2, Collections.singletonMap("test1", 4L)), result);
        nodeTracker.removeNode(smallNode.getNodeID());
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("After removing a node, the ClusterNodeTracker did not " + "recalculate the adjusted maximum allocation correctly"), mediumNodes.get(0).getTotalCapability(), result);
        nodeTracker.addNode(largeNode);
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("With two nodes added, the ClusterNodeTracker did not " + ("return a the maximum allocation that was the max of their aggregate " + "resources")), Resource.newInstance(10240, 4, Collections.singletonMap("test1", 2L)), result);
        nodeTracker.removeNode(largeNode.getNodeID());
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("After removing a node, the ClusterNodeTracker did not " + "recalculate the adjusted maximum allocation correctly"), mediumNodes.get(0).getTotalCapability(), result);
        nodeTracker.removeNode(mediumNode.getNodeID());
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("After removing all nodes, the ClusterNodeTracker did not " + "return the configured maximum allocation"), maximum, result);
        nodeTracker.addNode(smallNode);
        nodeTracker.addNode(mediumNode);
        nodeTracker.addNode(largeNode);
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("With three nodes added, the ClusterNodeTracker did not " + ("return a the maximum allocation that was the max of their aggregate " + "resources")), Resource.newInstance(10240, 4, Collections.singletonMap("test1", 4L)), result);
        nodeTracker.removeNode(smallNode.getNodeID());
        nodeTracker.removeNode(mediumNode.getNodeID());
        nodeTracker.removeNode(largeNode.getNodeID());
        result = nodeTracker.getMaxAllowedAllocation();
        Assert.assertEquals(("After removing all nodes, the ClusterNodeTracker did not " + "return the configured maximum allocation"), maximum, result);
    }
}

