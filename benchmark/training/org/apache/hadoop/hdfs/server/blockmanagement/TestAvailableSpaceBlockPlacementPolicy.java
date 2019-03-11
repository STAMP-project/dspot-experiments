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
package org.apache.hadoop.hdfs.server.blockmanagement;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.TestBlockStoragePolicy;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.net.Node;
import org.junit.Assert;
import org.junit.Test;


public class TestAvailableSpaceBlockPlacementPolicy {
    private static final int numRacks = 4;

    private static final int nodesPerRack = 5;

    private static final int blockSize = 1024;

    private static final int chooseTimes = 10000;

    private static final String file = "/tobers/test";

    private static final int replica = 3;

    private static DatanodeStorageInfo[] storages;

    private static DatanodeDescriptor[] dataNodes;

    private static Configuration conf;

    private static NameNode namenode;

    private static BlockPlacementPolicy placementPolicy;

    private static NetworkTopology cluster;

    /* To verify that the BlockPlacementPolicy can be replaced by AvailableSpaceBlockPlacementPolicy via
    changing the configuration.
     */
    @Test
    public void testPolicyReplacement() {
        Assert.assertTrue(((TestAvailableSpaceBlockPlacementPolicy.placementPolicy) instanceof AvailableSpaceBlockPlacementPolicy));
    }

    /* Call choose target many times and verify that nodes with more remaining percent will be chosen
    with high possibility.
     */
    @Test
    public void testChooseTarget() {
        int total = 0;
        int moreRemainingNode = 0;
        for (int i = 0; i < (TestAvailableSpaceBlockPlacementPolicy.chooseTimes); i++) {
            DatanodeStorageInfo[] targets = TestAvailableSpaceBlockPlacementPolicy.namenode.getNamesystem().getBlockManager().getBlockPlacementPolicy().chooseTarget(TestAvailableSpaceBlockPlacementPolicy.file, TestAvailableSpaceBlockPlacementPolicy.replica, null, new ArrayList<DatanodeStorageInfo>(), false, null, TestAvailableSpaceBlockPlacementPolicy.blockSize, TestBlockStoragePolicy.DEFAULT_STORAGE_POLICY, null);
            Assert.assertTrue(((targets.length) == (TestAvailableSpaceBlockPlacementPolicy.replica)));
            for (int j = 0; j < (TestAvailableSpaceBlockPlacementPolicy.replica); j++) {
                total++;
                if ((targets[j].getDatanodeDescriptor().getRemainingPercent()) > 60) {
                    moreRemainingNode++;
                }
            }
        }
        Assert.assertTrue((total == ((TestAvailableSpaceBlockPlacementPolicy.replica) * (TestAvailableSpaceBlockPlacementPolicy.chooseTimes))));
        double possibility = (1.0 * moreRemainingNode) / total;
        Assert.assertTrue((possibility > 0.52));
        Assert.assertTrue((possibility < 0.55));
    }

    @Test
    public void testChooseDataNode() {
        try {
            Collection<Node> allNodes = new ArrayList(TestAvailableSpaceBlockPlacementPolicy.dataNodes.length);
            Collections.addAll(allNodes, TestAvailableSpaceBlockPlacementPolicy.dataNodes);
            if ((TestAvailableSpaceBlockPlacementPolicy.placementPolicy) instanceof AvailableSpaceBlockPlacementPolicy) {
                // exclude all datanodes when chooseDataNode, no NPE should be thrown
                ((AvailableSpaceBlockPlacementPolicy) (TestAvailableSpaceBlockPlacementPolicy.placementPolicy)).chooseDataNode("~", allNodes);
            }
        } catch (NullPointerException npe) {
            Assert.fail("NPE should not be thrown");
        }
    }
}

