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
package org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager;


import NodeState.LOST;
import NodeState.RUNNING;
import NodeState.UNHEALTHY;
import NodeUpdateType.NODE_DECOMMISSIONING;
import NodeUpdateType.NODE_UNUSABLE;
import NodeUpdateType.NODE_USABLE;
import java.util.List;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.server.resourcemanager.ApplicationMasterService;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.junit.Assert;
import org.junit.Test;


public class TestAMRMRPCNodeUpdates {
    private MockRM rm;

    private ApplicationMasterService amService;

    @Test
    public void testAMRMDecommissioningNodes() throws Exception {
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", 10000);
        MockNM nm2 = rm.registerNode("127.0.0.2:1234", 10000);
        rm.drainEvents();
        RMApp app1 = rm.submitApp(2000);
        // Trigger the scheduling so the AM gets 'launched' on nm1
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        // register AM returns no unusable node
        am1.registerAppAttempt();
        Integer decommissioningTimeout = 600;
        syncNodeGracefulDecommission(nm2, decommissioningTimeout);
        AllocateRequest allocateRequest1 = AllocateRequest.newInstance(0, 0.0F, null, null, null);
        AllocateResponse response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        List<NodeReport> updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        NodeReport nr = updatedNodes.iterator().next();
        Assert.assertEquals(decommissioningTimeout, nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_DECOMMISSIONING, nr.getNodeUpdateType());
    }

    @Test
    public void testAMRMUnusableNodes() throws Exception {
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", 10000);
        MockNM nm2 = rm.registerNode("127.0.0.2:1234", 10000);
        MockNM nm3 = rm.registerNode("127.0.0.3:1234", 10000);
        MockNM nm4 = rm.registerNode("127.0.0.4:1234", 10000);
        rm.drainEvents();
        RMApp app1 = rm.submitApp(2000);
        // Trigger the scheduling so the AM gets 'launched' on nm1
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        // register AM returns no unusable node
        am1.registerAppAttempt();
        // allocate request returns no updated node
        AllocateRequest allocateRequest1 = AllocateRequest.newInstance(0, 0.0F, null, null, null);
        AllocateResponse response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        List<NodeReport> updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(0, updatedNodes.size());
        syncNodeHeartbeat(nm4, false);
        // allocate request returns updated node
        allocateRequest1 = AllocateRequest.newInstance(response1.getResponseId(), 0.0F, null, null, null);
        response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        NodeReport nr = updatedNodes.iterator().next();
        Assert.assertEquals(nm4.getNodeId(), nr.getNodeId());
        Assert.assertEquals(UNHEALTHY, nr.getNodeState());
        Assert.assertNull(nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_UNUSABLE, nr.getNodeUpdateType());
        // resending the allocate request returns the same result
        response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        nr = updatedNodes.iterator().next();
        Assert.assertEquals(nm4.getNodeId(), nr.getNodeId());
        Assert.assertEquals(UNHEALTHY, nr.getNodeState());
        Assert.assertNull(nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_UNUSABLE, nr.getNodeUpdateType());
        syncNodeLost(nm3);
        // subsequent allocate request returns delta
        allocateRequest1 = AllocateRequest.newInstance(response1.getResponseId(), 0.0F, null, null, null);
        response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        nr = updatedNodes.iterator().next();
        Assert.assertEquals(nm3.getNodeId(), nr.getNodeId());
        Assert.assertEquals(LOST, nr.getNodeState());
        Assert.assertNull(nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_UNUSABLE, nr.getNodeUpdateType());
        // registering another AM gives it the complete failed list
        RMApp app2 = rm.submitApp(2000);
        // Trigger nm2 heartbeat so that AM gets launched on it
        nm2.nodeHeartbeat(true);
        RMAppAttempt attempt2 = app2.getCurrentAppAttempt();
        MockAM am2 = rm.sendAMLaunched(attempt2.getAppAttemptId());
        // register AM returns all unusable nodes
        am2.registerAppAttempt();
        // allocate request returns no updated node
        AllocateRequest allocateRequest2 = AllocateRequest.newInstance(0, 0.0F, null, null, null);
        AllocateResponse response2 = allocate(attempt2.getAppAttemptId(), allocateRequest2);
        updatedNodes = response2.getUpdatedNodes();
        Assert.assertEquals(0, updatedNodes.size());
        syncNodeHeartbeat(nm4, true);
        // both AM's should get delta updated nodes
        allocateRequest1 = AllocateRequest.newInstance(response1.getResponseId(), 0.0F, null, null, null);
        response1 = allocate(attempt1.getAppAttemptId(), allocateRequest1);
        updatedNodes = response1.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        nr = updatedNodes.iterator().next();
        Assert.assertEquals(nm4.getNodeId(), nr.getNodeId());
        Assert.assertEquals(RUNNING, nr.getNodeState());
        Assert.assertNull(nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_USABLE, nr.getNodeUpdateType());
        allocateRequest2 = AllocateRequest.newInstance(response2.getResponseId(), 0.0F, null, null, null);
        response2 = allocate(attempt2.getAppAttemptId(), allocateRequest2);
        updatedNodes = response2.getUpdatedNodes();
        Assert.assertEquals(1, updatedNodes.size());
        nr = updatedNodes.iterator().next();
        Assert.assertEquals(nm4.getNodeId(), nr.getNodeId());
        Assert.assertEquals(RUNNING, nr.getNodeState());
        Assert.assertNull(nr.getDecommissioningTimeout());
        Assert.assertEquals(NODE_USABLE, nr.getNodeUpdateType());
        // subsequent allocate calls should return no updated nodes
        allocateRequest2 = AllocateRequest.newInstance(response2.getResponseId(), 0.0F, null, null, null);
        response2 = allocate(attempt2.getAppAttemptId(), allocateRequest2);
        updatedNodes = response2.getUpdatedNodes();
        Assert.assertEquals(0, updatedNodes.size());
        // how to do the above for LOST node
    }
}

