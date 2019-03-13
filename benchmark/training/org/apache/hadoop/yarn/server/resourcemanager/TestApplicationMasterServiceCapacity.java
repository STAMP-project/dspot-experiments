/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager;


import ContainerUpdateType.DECREASE_RESOURCE;
import ContainerUpdateType.INCREASE_RESOURCE;
import YarnConfiguration.MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY;
import YarnConfiguration.RM_SCHEDULER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateRequestPBImpl;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.UpdateContainerRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ApplicationMasterService}
 * with {@link CapacityScheduler}.
 */
public class TestApplicationMasterServiceCapacity extends ApplicationMasterServiceTestBase {
    private static final String DEFAULT_QUEUE = "default";

    @Test(timeout = 60000)
    public void testInvalidIncreaseDecreaseRequest() throws Exception {
        ApplicationMasterServiceTestBase.conf = new YarnConfiguration();
        ApplicationMasterServiceTestBase.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        try (MockRM rm = new MockRM(ApplicationMasterServiceTestBase.conf)) {
            start();
            // Register node1
            MockNM nm1 = registerNode((((ApplicationMasterServiceTestBase.DEFAULT_HOST) + ":") + (ApplicationMasterServiceTestBase.DEFAULT_PORT)), (6 * (ApplicationMasterServiceTestBase.GB)));
            // Submit an application
            RMApp app1 = submitApp(1024);
            // kick the scheduling
            nm1.nodeHeartbeat(true);
            RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
            MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
            RegisterApplicationMasterResponse registerResponse = am1.registerAppAttempt();
            sentRMContainerLaunched(rm, ContainerId.newContainerId(am1.getApplicationAttemptId(), 1));
            // Ask for a normal increase should be successful
            am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, ContainerId.newContainerId(attempt1.getAppAttemptId(), 1), INCREASE_RESOURCE, Resources.createResource(2048), null)));
            // Target resource is negative, should fail
            AllocateResponse response = am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, ContainerId.newContainerId(attempt1.getAppAttemptId(), 1), INCREASE_RESOURCE, Resources.createResource((-1)), null)));
            Assert.assertEquals(1, response.getUpdateErrors().size());
            Assert.assertEquals("RESOURCE_OUTSIDE_ALLOWED_RANGE", response.getUpdateErrors().get(0).getReason());
            // Target resource is more than maxAllocation, should fail
            response = am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, ContainerId.newContainerId(attempt1.getAppAttemptId(), 1), INCREASE_RESOURCE, Resources.add(registerResponse.getMaximumResourceCapability(), Resources.createResource(1)), null)));
            Assert.assertEquals(1, response.getUpdateErrors().size());
            Assert.assertEquals("RESOURCE_OUTSIDE_ALLOWED_RANGE", response.getUpdateErrors().get(0).getReason());
            // Contains multiple increase/decrease requests for same containerId
            response = am1.sendContainerResizingRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, ContainerId.newContainerId(attempt1.getAppAttemptId(), 1), INCREASE_RESOURCE, Resources.createResource(2048, 4), null), UpdateContainerRequest.newInstance(0, ContainerId.newContainerId(attempt1.getAppAttemptId(), 1), DECREASE_RESOURCE, Resources.createResource(1024, 1), null)));
            Assert.assertEquals(1, response.getUpdateErrors().size());
            Assert.assertEquals("UPDATE_OUTSTANDING_ERROR", response.getUpdateErrors().get(0).getReason());
        }
    }

    @Test(timeout = 300000)
    public void testPriorityInAllocatedResponse() throws Exception {
        ApplicationMasterServiceTestBase.conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        // Set Max Application Priority as 10
        ApplicationMasterServiceTestBase.conf.setInt(MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY, 10);
        MockRM rm = new MockRM(ApplicationMasterServiceTestBase.conf);
        start();
        // Register node1
        MockNM nm1 = rm.registerNode((((ApplicationMasterServiceTestBase.DEFAULT_HOST) + ":") + (ApplicationMasterServiceTestBase.DEFAULT_PORT)), (6 * (ApplicationMasterServiceTestBase.GB)));
        // Submit an application
        Priority appPriority1 = Priority.newInstance(5);
        RMApp app1 = rm.submitApp(2048, appPriority1);
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        am1.registerAppAttempt();
        AllocateRequestPBImpl allocateRequest = new AllocateRequestPBImpl();
        List<ContainerId> release = new ArrayList<>();
        List<ResourceRequest> ask = new ArrayList<>();
        allocateRequest.setReleaseList(release);
        allocateRequest.setAskList(ask);
        AllocateResponse response1 = am1.allocate(allocateRequest);
        Assert.assertEquals(appPriority1, response1.getApplicationPriority());
        // Change the priority of App1 to 8
        Priority appPriority2 = Priority.newInstance(8);
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(app1.getUser());
        rm.getRMAppManager().updateApplicationPriority(ugi, app1.getApplicationId(), appPriority2);
        AllocateResponse response2 = am1.allocate(allocateRequest);
        Assert.assertEquals(appPriority2, response2.getApplicationPriority());
        stop();
    }
}

