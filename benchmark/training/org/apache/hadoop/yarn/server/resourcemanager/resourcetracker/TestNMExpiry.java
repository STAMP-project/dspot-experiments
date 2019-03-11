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


import YarnConfiguration.RM_NM_EXPIRY_INTERVAL_MS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.ClusterMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.NMLivelinessMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNMExpiry {
    private static final Logger LOG = LoggerFactory.getLogger(TestNMExpiry.class);

    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    ResourceTrackerService resourceTrackerService;

    private class TestNmLivelinessMonitor extends NMLivelinessMonitor {
        public TestNmLivelinessMonitor(Dispatcher dispatcher) {
            super(dispatcher);
        }

        @Override
        public void serviceInit(Configuration conf) throws Exception {
            conf.setLong(RM_NM_EXPIRY_INTERVAL_MS, 1000);
            super.serviceInit(conf);
        }
    }

    private class ThirdNodeHeartBeatThread extends Thread {
        public void run() {
            int lastResponseID = 0;
            while (!(stopT)) {
                try {
                    NodeStatus nodeStatus = TestNMExpiry.recordFactory.newRecordInstance(NodeStatus.class);
                    nodeStatus.setNodeId(request3.getNodeId());
                    nodeStatus.setResponseId(lastResponseID);
                    nodeStatus.setNodeHealthStatus(TestNMExpiry.recordFactory.newRecordInstance(NodeHealthStatus.class));
                    nodeStatus.getNodeHealthStatus().setIsNodeHealthy(true);
                    NodeHeartbeatRequest request = TestNMExpiry.recordFactory.newRecordInstance(NodeHeartbeatRequest.class);
                    request.setNodeStatus(nodeStatus);
                    lastResponseID = resourceTrackerService.nodeHeartbeat(request).getResponseId();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    TestNMExpiry.LOG.info("failed to heartbeat ", e);
                }
            } 
        }
    }

    boolean stopT = false;

    RegisterNodeManagerRequest request3;

    @Test
    public void testNMExpiry() throws Exception {
        String hostname1 = "localhost1";
        String hostname2 = "localhost2";
        String hostname3 = "localhost3";
        Resource capability = BuilderUtils.newResource(1024, 1);
        RegisterNodeManagerRequest request1 = TestNMExpiry.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        NodeId nodeId1 = NodeId.newInstance(hostname1, 0);
        request1.setNodeId(nodeId1);
        request1.setHttpPort(0);
        request1.setResource(capability);
        resourceTrackerService.registerNodeManager(request1);
        RegisterNodeManagerRequest request2 = TestNMExpiry.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        NodeId nodeId2 = NodeId.newInstance(hostname2, 0);
        request2.setNodeId(nodeId2);
        request2.setHttpPort(0);
        request2.setResource(capability);
        resourceTrackerService.registerNodeManager(request2);
        int waitCount = 0;
        while (((ClusterMetrics.getMetrics().getNumLostNMs()) != 2) && ((waitCount++) < 20)) {
            synchronized(this) {
                wait(100);
            }
        } 
        Assert.assertEquals(2, ClusterMetrics.getMetrics().getNumLostNMs());
        request3 = TestNMExpiry.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        NodeId nodeId3 = NodeId.newInstance(hostname3, 0);
        request3.setNodeId(nodeId3);
        request3.setHttpPort(0);
        request3.setResource(capability);
        resourceTrackerService.registerNodeManager(request3);
        /* test to see if hostanme 3 does not expire */
        stopT = false;
        new TestNMExpiry.ThirdNodeHeartBeatThread().start();
        Assert.assertEquals(2, ClusterMetrics.getMetrics().getNumLostNMs());
        stopT = true;
    }
}

