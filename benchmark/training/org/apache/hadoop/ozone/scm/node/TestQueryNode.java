/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.scm.node;


import HddsProtos.Node;
import HddsProtos.QueryScope.CLUSTER;
import java.util.List;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos;
import org.apache.hadoop.hdds.scm.client.ContainerOperationClient;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Query Node Operation.
 */
public class TestQueryNode {
    private static int numOfDatanodes = 5;

    private MiniOzoneCluster cluster;

    private ContainerOperationClient scmClient;

    @Test
    public void testHealthyNodesCount() throws Exception {
        List<HddsProtos.Node> nodes = scmClient.queryNode(HEALTHY, CLUSTER, "");
        Assert.assertEquals("Expected  live nodes", TestQueryNode.numOfDatanodes, nodes.size());
    }

    @Test(timeout = 10 * 1000L)
    public void testStaleNodesCount() throws Exception {
        cluster.shutdownHddsDatanode(0);
        cluster.shutdownHddsDatanode(1);
        GenericTestUtils.waitFor(() -> (cluster.getStorageContainerManager().getNodeCount(STALE)) == 2, 100, (4 * 1000));
        int nodeCount = scmClient.queryNode(STALE, CLUSTER, "").size();
        Assert.assertEquals("Mismatch of expected nodes count", 2, nodeCount);
        GenericTestUtils.waitFor(() -> (cluster.getStorageContainerManager().getNodeCount(DEAD)) == 2, 100, (4 * 1000));
        // Assert that we don't find any stale nodes.
        nodeCount = scmClient.queryNode(STALE, CLUSTER, "").size();
        Assert.assertEquals("Mismatch of expected nodes count", 0, nodeCount);
        // Assert that we find the expected number of dead nodes.
        nodeCount = scmClient.queryNode(DEAD, CLUSTER, "").size();
        Assert.assertEquals("Mismatch of expected nodes count", 2, nodeCount);
    }
}

