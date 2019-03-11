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
package org.apache.hadoop.yarn.server;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater;
import org.junit.Assert;
import org.junit.Test;


public class TestMiniYarnClusterNodeUtilization {
    // Mini YARN cluster setup
    private static final int NUM_RM = 1;

    private static final int NUM_NM = 1;

    // Values for the first round
    private static final int CONTAINER_PMEM_1 = 1024;

    private static final int CONTAINER_VMEM_1 = 2048;

    private static final float CONTAINER_CPU_1 = 11.0F;

    private static final int NODE_PMEM_1 = 10240;

    private static final int NODE_VMEM_1 = 20480;

    private static final float NODE_CPU_1 = 51.0F;

    // Values for the second round
    private static final int CONTAINER_PMEM_2 = 2048;

    private static final int CONTAINER_VMEM_2 = 4096;

    private static final float CONTAINER_CPU_2 = 22.0F;

    private static final int NODE_PMEM_2 = 20480;

    private static final int NODE_VMEM_2 = 40960;

    private static final float NODE_CPU_2 = 61.0F;

    private MiniYARNCluster cluster;

    private MiniYARNCluster.CustomNodeManager nm;

    private Configuration conf;

    private NodeStatus nodeStatus;

    /**
     * Simulates a NM heartbeat using the simulated NodeStatus fixture. Verify
     * both the RMNode and SchedulerNode have been updated with the new
     * utilization.
     */
    @Test(timeout = 60000)
    public void testUpdateNodeUtilization() throws IOException, InterruptedException, YarnException {
        Assert.assertTrue("NMs fail to connect to the RM", cluster.waitForNodeManagersToConnect(10000));
        // Give the heartbeat time to propagate to the RM
        verifySimulatedUtilization();
        // Alter utilization
        nodeStatus = createNodeStatus(getNMContext().getNodeId(), 0, TestMiniYarnClusterNodeUtilization.CONTAINER_PMEM_2, TestMiniYarnClusterNodeUtilization.CONTAINER_VMEM_2, TestMiniYarnClusterNodeUtilization.CONTAINER_CPU_2, TestMiniYarnClusterNodeUtilization.NODE_PMEM_2, TestMiniYarnClusterNodeUtilization.NODE_VMEM_2, TestMiniYarnClusterNodeUtilization.NODE_CPU_2);
        nm.setNodeStatus(nodeStatus);
        // Give the heartbeat time to propagate to the RM
        verifySimulatedUtilization();
    }

    /**
     * Trigger the NM to send a heartbeat using the simulated NodeStatus fixture.
     * Verify both the RMNode and SchedulerNode have been updated with the new
     * utilization.
     */
    @Test(timeout = 60000)
    public void testMockNodeStatusHeartbeat() throws InterruptedException, YarnException {
        Assert.assertTrue("NMs fail to connect to the RM", cluster.waitForNodeManagersToConnect(10000));
        NodeStatusUpdater updater = getNodeStatusUpdater();
        updater.sendOutofBandHeartBeat();
        // Give the heartbeat time to propagate to the RM
        verifySimulatedUtilization();
        // Alter utilization
        nodeStatus = createNodeStatus(getNMContext().getNodeId(), 0, TestMiniYarnClusterNodeUtilization.CONTAINER_PMEM_2, TestMiniYarnClusterNodeUtilization.CONTAINER_VMEM_2, TestMiniYarnClusterNodeUtilization.CONTAINER_CPU_2, TestMiniYarnClusterNodeUtilization.NODE_PMEM_2, TestMiniYarnClusterNodeUtilization.NODE_VMEM_2, TestMiniYarnClusterNodeUtilization.NODE_CPU_2);
        nm.setNodeStatus(nodeStatus);
        updater.sendOutofBandHeartBeat();
        verifySimulatedUtilization();
    }
}

