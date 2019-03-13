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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import ContainerState.RUNNING;
import RMContainerState.ALLOCATED;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.TestRMRestart;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NullRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.junit.Assert;
import org.junit.Test;


public class TestWorkPreservingRMRestartForNodeLabel {
    private Configuration conf;

    private static final int GB = 1024;// 1024 MB


    RMNodeLabelsManager mgr;

    @Test
    public void testWorkPreservingRestartForNodeLabel() throws Exception {
        // This test is pretty much similar to testContainerAllocateWithLabel.
        // Difference is, this test doesn't specify label expression in ResourceRequest,
        // instead, it uses default queue label expression
        // set node -> label
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h1", 0), toSet("x"), NodeId.newInstance("h2", 0), toSet("y")));
        conf = TestUtils.getConfigurationWithDefaultQueueLabels(conf);
        // inject node label manager
        MockRM rm1 = new MockRM(conf) {
            @Override
            public RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        MemoryRMStateStore memStore = ((MemoryRMStateStore) (rm1.getRMStateStore()));
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm1.registerNode("h1:1234", 8000);// label = x

        MockNM nm2 = rm1.registerNode("h2:1234", 8000);// label = y

        MockNM nm3 = rm1.registerNode("h3:1234", 8000);// label = <empty>

        ContainerId containerId;
        // launch an app to queue a1 (label = x), and check all container will
        // be allocated in h1
        RMApp app1 = rm1.submitApp(200, "app", "user", null, "a1");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // request a container.
        am1.allocate("*", 1024, 1, new ArrayList<ContainerId>());
        containerId = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        Assert.assertTrue(rm1.waitForState(nm1, containerId, ALLOCATED));
        checkRMContainerLabelExpression(ContainerId.newContainerId(am1.getApplicationAttemptId(), 1), rm1, "x");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am1.getApplicationAttemptId(), 2), rm1, "x");
        // launch an app to queue b1 (label = y), and check all container will
        // be allocated in h2
        RMApp app2 = rm1.submitApp(200, "app", "user", null, "b1");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm2);
        // request a container.
        am2.allocate("*", 1024, 1, new ArrayList<ContainerId>());
        containerId = ContainerId.newContainerId(am2.getApplicationAttemptId(), 2);
        Assert.assertTrue(rm1.waitForState(nm2, containerId, ALLOCATED));
        checkRMContainerLabelExpression(ContainerId.newContainerId(am2.getApplicationAttemptId(), 1), rm1, "y");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am2.getApplicationAttemptId(), 2), rm1, "y");
        // launch an app to queue c1 (label = ""), and check all container will
        // be allocated in h3
        RMApp app3 = rm1.submitApp(200, "app", "user", null, "c1");
        MockAM am3 = MockRM.launchAndRegisterAM(app3, rm1, nm3);
        // request a container.
        am3.allocate("*", 1024, 1, new ArrayList<ContainerId>());
        containerId = ContainerId.newContainerId(am3.getApplicationAttemptId(), 2);
        Assert.assertTrue(rm1.waitForState(nm3, containerId, ALLOCATED));
        checkRMContainerLabelExpression(ContainerId.newContainerId(am3.getApplicationAttemptId(), 1), rm1, "");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am3.getApplicationAttemptId(), 2), rm1, "");
        // Re-start RM
        mgr = new NullRMNodeLabelsManager();
        mgr.init(conf);
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h1", 0), toSet("x"), NodeId.newInstance("h2", 0), toSet("y")));
        MockRM rm2 = new MockRM(conf, memStore) {
            @Override
            public RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        nm2.setResourceTrackerService(getResourceTrackerService());
        nm3.setResourceTrackerService(getResourceTrackerService());
        // recover app
        NMContainerStatus app1c1 = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 1, RUNNING, "x");
        NMContainerStatus app1c2 = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 2, RUNNING, "x");
        nm1.registerNode(Arrays.asList(app1c1, app1c2), null);
        TestWorkPreservingRMRestartForNodeLabel.waitForNumContainersToRecover(2, rm2, am1.getApplicationAttemptId());
        checkRMContainerLabelExpression(ContainerId.newContainerId(am1.getApplicationAttemptId(), 1), rm1, "x");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am1.getApplicationAttemptId(), 2), rm1, "x");
        NMContainerStatus app2c1 = TestRMRestart.createNMContainerStatus(am2.getApplicationAttemptId(), 1, RUNNING, "y");
        NMContainerStatus app2c2 = TestRMRestart.createNMContainerStatus(am2.getApplicationAttemptId(), 2, RUNNING, "y");
        nm2.registerNode(Arrays.asList(app2c1, app2c2), null);
        TestWorkPreservingRMRestartForNodeLabel.waitForNumContainersToRecover(2, rm2, am2.getApplicationAttemptId());
        checkRMContainerLabelExpression(ContainerId.newContainerId(am2.getApplicationAttemptId(), 1), rm1, "y");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am2.getApplicationAttemptId(), 2), rm1, "y");
        NMContainerStatus app3c1 = TestRMRestart.createNMContainerStatus(am3.getApplicationAttemptId(), 1, RUNNING, "");
        NMContainerStatus app3c2 = TestRMRestart.createNMContainerStatus(am3.getApplicationAttemptId(), 2, RUNNING, "");
        nm3.registerNode(Arrays.asList(app3c1, app3c2), null);
        TestWorkPreservingRMRestartForNodeLabel.waitForNumContainersToRecover(2, rm2, am3.getApplicationAttemptId());
        checkRMContainerLabelExpression(ContainerId.newContainerId(am3.getApplicationAttemptId(), 1), rm1, "");
        checkRMContainerLabelExpression(ContainerId.newContainerId(am3.getApplicationAttemptId(), 2), rm1, "");
        // Check recovered resource usage
        checkAppResourceUsage("x", app1.getApplicationId(), rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkAppResourceUsage("y", app2.getApplicationId(), rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkAppResourceUsage("", app3.getApplicationId(), rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("x", "a1", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("y", "b1", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("", "c1", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("x", "a", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("y", "b", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("", "c", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("x", "root", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("y", "root", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        checkQueueResourceUsage("", "root", rm1, (2 * (TestWorkPreservingRMRestartForNodeLabel.GB)));
        close();
        close();
    }
}

