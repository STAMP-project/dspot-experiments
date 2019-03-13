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


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for checking Scheduling with allocationRequestId, i.e. mapping of
 * allocated containers to the original client {@code ResourceRequest}.
 */
public class TestSchedulingWithAllocationRequestId extends ParameterizedSchedulerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestSchedulingWithAllocationRequestId.class);

    private static final int GB = 1024;

    public TestSchedulingWithAllocationRequestId(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @Test(timeout = 10000)
    public void testMultipleAllocationRequestIds() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm = new MockRM(conf);
        try {
            start();
            MockNM nm1 = rm.registerNode("127.0.0.1:1234", (4 * (TestSchedulingWithAllocationRequestId.GB)));
            MockNM nm2 = rm.registerNode("127.0.0.2:5678", (4 * (TestSchedulingWithAllocationRequestId.GB)));
            RMApp app1 = rm.submitApp(2048);
            // kick the scheduling
            nm1.nodeHeartbeat(true);
            RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
            MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
            am1.registerAppAttempt();
            // send requests for containers with id 10 & 20
            am1.allocate(am1.createReq(new String[]{ "127.0.0.1" }, (2 * (TestSchedulingWithAllocationRequestId.GB)), 1, 1, 10L), null);
            am1.allocate(am1.createReq(new String[]{ "127.0.0.2" }, (2 * (TestSchedulingWithAllocationRequestId.GB)), 1, 2, 20L), null);
            // check if request id 10 is satisfied
            AllocateResponse allocResponse = waitForAllocResponse(rm, am1, nm1, 1);
            List<Container> allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (2 * (TestSchedulingWithAllocationRequestId.GB)), nm1.getNodeId(), 10);
            // check now if request id 20 is satisfied
            allocResponse = waitForAllocResponse(rm, am1, nm2, 2);
            allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(2, allocated.size());
            for (Container container : allocated) {
                checkAllocatedContainer(container, (2 * (TestSchedulingWithAllocationRequestId.GB)), nm2.getNodeId(), 20);
            }
        } finally {
            if (rm != null) {
                stop();
            }
        }
    }

    @Test(timeout = 10000)
    public void testMultipleAllocationRequestDiffPriority() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm = new MockRM(conf);
        try {
            start();
            MockNM nm1 = rm.registerNode("127.0.0.1:1234", (4 * (TestSchedulingWithAllocationRequestId.GB)));
            MockNM nm2 = rm.registerNode("127.0.0.2:5678", (4 * (TestSchedulingWithAllocationRequestId.GB)));
            RMApp app1 = rm.submitApp(2048);
            // kick the scheduling
            nm1.nodeHeartbeat(true);
            RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
            MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
            am1.registerAppAttempt();
            // send requests for containers with id 10 & 20
            am1.allocate(am1.createReq(new String[]{ "127.0.0.1" }, (2 * (TestSchedulingWithAllocationRequestId.GB)), 2, 1, 10L), null);
            am1.allocate(am1.createReq(new String[]{ "127.0.0.2" }, (2 * (TestSchedulingWithAllocationRequestId.GB)), 1, 2, 20L), null);
            // check if request id 20 is satisfied first
            AllocateResponse allocResponse = waitForAllocResponse(rm, am1, nm2, 2);
            List<Container> allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(2, allocated.size());
            for (Container container : allocated) {
                checkAllocatedContainer(container, (2 * (TestSchedulingWithAllocationRequestId.GB)), nm2.getNodeId(), 20);
            }
            // check now if request id 10 is satisfied
            allocResponse = waitForAllocResponse(rm, am1, nm1, 1);
            allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (2 * (TestSchedulingWithAllocationRequestId.GB)), nm1.getNodeId(), 10);
        } finally {
            if (rm != null) {
                stop();
            }
        }
    }

    @Test(timeout = 10000)
    public void testMultipleAppsWithAllocationReqId() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm = new MockRM(conf);
        try {
            start();
            // Register node1
            String host0 = "host_0";
            String host1 = "host_1";
            MockNM nm1 = new MockNM((host0 + ":1234"), (8 * (TestSchedulingWithAllocationRequestId.GB)), getResourceTrackerService());
            nm1.registerNode();
            // Register node2
            MockNM nm2 = new MockNM((host1 + ":2351"), (8 * (TestSchedulingWithAllocationRequestId.GB)), getResourceTrackerService());
            nm2.registerNode();
            // submit 1st app
            RMApp app1 = rm.submitApp((1 * (TestSchedulingWithAllocationRequestId.GB)), "user_0", "a1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm1);
            // Submit app1 RR with allocationReqId = 5
            int numContainers = 1;
            am1.allocate(am1.createReq(new String[]{ host0, host1 }, (1 * (TestSchedulingWithAllocationRequestId.GB)), 1, numContainers, 5L), null);
            // wait for container to be allocated.
            AllocateResponse allocResponse = waitForAllocResponse(rm, am1, nm1, 1);
            List<Container> allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (1 * (TestSchedulingWithAllocationRequestId.GB)), nm1.getNodeId(), 5L);
            // Submit another application
            RMApp app2 = rm.submitApp((1 * (TestSchedulingWithAllocationRequestId.GB)), "user_1", "a2");
            MockAM am2 = MockRM.launchAndRegisterAM(app2, rm, nm2);
            // Submit app2 RR with allocationReqId = 5
            am2.allocate(am1.createReq(new String[]{ host0, host1 }, (2 * (TestSchedulingWithAllocationRequestId.GB)), 1, numContainers, 5L), null);
            // wait for container to be allocated.
            allocResponse = waitForAllocResponse(rm, am2, nm2, 1);
            allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (2 * (TestSchedulingWithAllocationRequestId.GB)), nm2.getNodeId(), 5L);
            // Now submit app2 RR with allocationReqId = 10
            am2.allocate(am1.createReq(new String[]{ host0, host1 }, (3 * (TestSchedulingWithAllocationRequestId.GB)), 1, numContainers, 10L), null);
            // wait for container to be allocated.
            allocResponse = waitForAllocResponse(rm, am2, nm1, 1);
            allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (3 * (TestSchedulingWithAllocationRequestId.GB)), nm1.getNodeId(), 10L);
            // Now submit app1 RR with allocationReqId = 10
            am1.allocate(am1.createReq(new String[]{ host0, host1 }, (4 * (TestSchedulingWithAllocationRequestId.GB)), 1, numContainers, 10L), null);
            // wait for container to be allocated.
            allocResponse = waitForAllocResponse(rm, am1, nm2, 1);
            allocated = allocResponse.getAllocatedContainers();
            Assert.assertEquals(1, allocated.size());
            checkAllocatedContainer(allocated.get(0), (4 * (TestSchedulingWithAllocationRequestId.GB)), nm2.getNodeId(), 10L);
        } finally {
            if (rm != null) {
                stop();
            }
        }
    }
}

