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
package org.apache.hadoop.yarn.server.resourcemanager.resourcetracker;


import NodeAction.RESYNC;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("rawtypes")
public class TestRMNMRPCResponseId {
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    ResourceTrackerService resourceTrackerService;

    private NodeId nodeId;

    @Test
    public void testRPCResponseId() throws IOException, YarnException {
        String node = "localhost";
        Resource capability = BuilderUtils.newResource(1024, 1);
        RegisterNodeManagerRequest request = TestRMNMRPCResponseId.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        nodeId = NodeId.newInstance(node, 1234);
        request.setNodeId(nodeId);
        request.setHttpPort(0);
        request.setResource(capability);
        RegisterNodeManagerRequest request1 = TestRMNMRPCResponseId.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        request1.setNodeId(nodeId);
        request1.setHttpPort(0);
        request1.setResource(capability);
        resourceTrackerService.registerNodeManager(request1);
        NodeStatus nodeStatus = TestRMNMRPCResponseId.recordFactory.newRecordInstance(NodeStatus.class);
        nodeStatus.setNodeId(nodeId);
        NodeHealthStatus nodeHealthStatus = TestRMNMRPCResponseId.recordFactory.newRecordInstance(NodeHealthStatus.class);
        nodeHealthStatus.setIsNodeHealthy(true);
        nodeStatus.setNodeHealthStatus(nodeHealthStatus);
        NodeHeartbeatRequest nodeHeartBeatRequest = TestRMNMRPCResponseId.recordFactory.newRecordInstance(NodeHeartbeatRequest.class);
        nodeHeartBeatRequest.setNodeStatus(nodeStatus);
        nodeStatus.setResponseId(0);
        NodeHeartbeatResponse response = resourceTrackerService.nodeHeartbeat(nodeHeartBeatRequest);
        Assert.assertTrue(((response.getResponseId()) == 1));
        nodeStatus.setResponseId(response.getResponseId());
        response = resourceTrackerService.nodeHeartbeat(nodeHeartBeatRequest);
        Assert.assertTrue(((response.getResponseId()) == 2));
        /* try calling with less response id */
        response = resourceTrackerService.nodeHeartbeat(nodeHeartBeatRequest);
        Assert.assertTrue(((response.getResponseId()) == 2));
        nodeStatus.setResponseId(0);
        response = resourceTrackerService.nodeHeartbeat(nodeHeartBeatRequest);
        Assert.assertTrue(RESYNC.equals(response.getNodeAction()));
        Assert.assertEquals("Too far behind rm response id:2 nm response id:0", response.getDiagnosticsMessage());
    }
}

