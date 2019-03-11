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


import NodeAction.NORMAL;
import NodeState.DECOMMISSIONING;
import NodeState.RUNNING;
import RMNodeEventType.RECONNECTED;
import RMNodeEventType.STARTED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;
import org.apache.hadoop.yarn.conf.ConfigurationProviderFactory;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType.GRACEFUL_DECOMMISSION;


/**
 * TestNMReconnect run tests against the scheduler set by
 * {@link ParameterizedSchedulerTestBase} which is configured
 * in {@link YarnConfiguration}.
 */
public class TestNMReconnect extends ParameterizedSchedulerTestBase {
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private List<RMNodeEvent> rmNodeEvents = new ArrayList<RMNodeEvent>();

    private Dispatcher dispatcher;

    private RMContextImpl context;

    public TestNMReconnect(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    private class TestRMNodeEventDispatcher implements EventHandler<RMNodeEvent> {
        @Override
        public void handle(RMNodeEvent event) {
            rmNodeEvents.add(event);
        }
    }

    ResourceTrackerService resourceTrackerService;

    @Test
    public void testReconnect() throws Exception {
        String hostname1 = "localhost1";
        Resource capability = BuilderUtils.newResource(1024, 1);
        RegisterNodeManagerRequest request1 = TestNMReconnect.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        NodeId nodeId1 = NodeId.newInstance(hostname1, 0);
        request1.setNodeId(nodeId1);
        request1.setHttpPort(0);
        request1.setResource(capability);
        resourceTrackerService.registerNodeManager(request1);
        Assert.assertEquals(STARTED, rmNodeEvents.get(0).getType());
        rmNodeEvents.clear();
        resourceTrackerService.registerNodeManager(request1);
        Assert.assertEquals(RECONNECTED, rmNodeEvents.get(0).getType());
        rmNodeEvents.clear();
        resourceTrackerService.registerNodeManager(request1);
        capability = BuilderUtils.newResource(1024, 2);
        request1.setResource(capability);
        Assert.assertEquals(RECONNECTED, rmNodeEvents.get(0).getType());
    }

    @Test
    public void testCompareRMNodeAfterReconnect() throws Exception {
        AbstractYarnScheduler scheduler = getScheduler();
        Configuration yarnConf = new YarnConfiguration();
        ConfigurationProvider configurationProvider = ConfigurationProviderFactory.getConfigurationProvider(yarnConf);
        configurationProvider.init(yarnConf);
        context.setConfigurationProvider(configurationProvider);
        RMNodeLabelsManager nlm = new RMNodeLabelsManager();
        nlm.init(yarnConf);
        nlm.start();
        context.setNodeLabelManager(nlm);
        scheduler.setRMContext(context);
        scheduler.init(yarnConf);
        scheduler.start();
        dispatcher.register(SchedulerEventType.class, scheduler);
        String hostname1 = "localhost1";
        Resource capability = BuilderUtils.newResource(4096, 4);
        RegisterNodeManagerRequest request1 = TestNMReconnect.recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
        NodeId nodeId1 = NodeId.newInstance(hostname1, 0);
        request1.setNodeId(nodeId1);
        request1.setHttpPort(0);
        request1.setResource(capability);
        resourceTrackerService.registerNodeManager(request1);
        Assert.assertNotNull(context.getRMNodes().get(nodeId1));
        // verify Scheduler and RMContext use same RMNode reference.
        Assert.assertTrue(((scheduler.getSchedulerNode(nodeId1).getRMNode()) == (context.getRMNodes().get(nodeId1))));
        Assert.assertEquals(context.getRMNodes().get(nodeId1).getTotalCapability(), capability);
        Resource capability1 = BuilderUtils.newResource(2048, 2);
        request1.setResource(capability1);
        resourceTrackerService.registerNodeManager(request1);
        Assert.assertNotNull(context.getRMNodes().get(nodeId1));
        // verify Scheduler and RMContext use same RMNode reference
        // after reconnect.
        Assert.assertTrue(((scheduler.getSchedulerNode(nodeId1).getRMNode()) == (context.getRMNodes().get(nodeId1))));
        // verify RMNode's capability is changed.
        Assert.assertEquals(context.getRMNodes().get(nodeId1).getTotalCapability(), capability1);
        nlm.stop();
        scheduler.stop();
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testDecommissioningNodeReconnect() throws Exception {
        MockRM rm = new MockRM();
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        rm.waitForState(nm1.getNodeId(), RUNNING);
        getRMContext().getDispatcher().getEventHandler().handle(new RMNodeEvent(nm1.getNodeId(), GRACEFUL_DECOMMISSION));
        rm.waitForState(nm1.getNodeId(), DECOMMISSIONING);
        MockNM nm2 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        RegisterNodeManagerResponse response = nm2.registerNode();
        // not SHUTDOWN
        Assert.assertTrue(response.getNodeAction().equals(NORMAL));
        stop();
    }

    @Test(timeout = 10000)
    public void testRMNodeStatusAfterReconnect() throws Exception {
        // The node(127.0.0.1:1234) reconnected with RM. When it registered with
        // RM, RM set its lastNodeHeartbeatResponse's id to 0 asynchronously. But
        // the node's heartbeat come before RM succeeded setting the id to 0.
        MockRM rm = new MockRM();
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        int i = 0;
        while (i < 3) {
            nm1.nodeHeartbeat(true);
            rm.drainEvents();
            i++;
        } 
        MockNM nm2 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm2.registerNode();
        RMNode rmNode = getRMContext().getRMNodes().get(nm2.getNodeId());
        nm2.nodeHeartbeat(true);
        rm.drainEvents();
        Assert.assertEquals("Node is Not in Running state.", RUNNING, rmNode.getState());
        stop();
    }
}

