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
package org.apache.hadoop.yarn.server.resourcemanager;


import DecommissioningNodeStatus.READY;
import DecommissioningNodeStatus.WAIT_APP;
import DecommissioningNodeStatus.WAIT_CONTAINER;
import NodeState.DECOMMISSIONING;
import NodeState.RUNNING;
import RMAppState.FINISHED;
import YarnConfiguration.DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT;
import YarnConfiguration.RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests DecommissioningNodesWatcher.
 */
public class TestDecommissioningNodesWatcher {
    private MockRM rm;

    @Test
    public void testDecommissioningNodesWatcher() throws Exception {
        Configuration conf = new Configuration();
        conf.set(RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT, "40");
        rm = new MockRM(conf);
        start();
        DecommissioningNodesWatcher watcher = new DecommissioningNodesWatcher(getRMContext());
        MockNM nm1 = rm.registerNode("host1:1234", 10240);
        RMNode node1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        NodeId id1 = nm1.getNodeId();
        rm.waitForState(id1, RUNNING);
        Assert.assertFalse(watcher.checkReadyToBeDecommissioned(id1));
        RMApp app = rm.submitApp(2000);
        MockAM am = MockRM.launchAndRegisterAM(app, rm, nm1);
        // Setup nm1 as DECOMMISSIONING for DecommissioningNodesWatcher.
        rm.sendNodeGracefulDecommission(nm1, DEFAULT_RM_NODE_GRACEFUL_DECOMMISSION_TIMEOUT);
        rm.waitForState(id1, DECOMMISSIONING);
        // Update status with decreasing number of running containers until 0.
        watcher.update(node1, createNodeStatus(id1, app, 12));
        watcher.update(node1, createNodeStatus(id1, app, 11));
        Assert.assertFalse(watcher.checkReadyToBeDecommissioned(id1));
        watcher.update(node1, createNodeStatus(id1, app, 1));
        Assert.assertEquals(WAIT_CONTAINER, watcher.checkDecommissioningStatus(id1));
        watcher.update(node1, createNodeStatus(id1, app, 0));
        Assert.assertEquals(WAIT_APP, watcher.checkDecommissioningStatus(id1));
        // Set app to be FINISHED and verified DecommissioningNodeStatus is READY.
        MockRM.finishAMAndVerifyAppState(app, rm, nm1, am);
        rm.waitForState(app.getApplicationId(), FINISHED);
        Assert.assertEquals(READY, watcher.checkDecommissioningStatus(id1));
    }
}

