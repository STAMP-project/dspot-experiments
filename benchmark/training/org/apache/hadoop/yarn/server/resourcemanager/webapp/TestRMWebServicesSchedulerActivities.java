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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import ContainerState.COMPLETE;
import MediaType.APPLICATION_JSON;
import Priority.UNDEFINED;
import RMContainerEventType.FINISHED;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Arrays;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMWebServicesSchedulerActivities extends TestRMWebServicesCapacitySched {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMWebServicesSchedulerActivities.class);

    @Test
    public void testAssignMultipleContainersPerNodeHeartbeat() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (24 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "127.0.0.1", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "/default-rack", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(1024), 10)), null);
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.1:1234");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(1000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 11);
            JSONArray allocations = json.getJSONArray("allocations");
            for (int i = 0; i < (allocations.length()); i++) {
                if (i != ((allocations.length()) - 1)) {
                    verifyStateOfAllocations(allocations.getJSONObject(i), "finalAllocationState", "ALLOCATED");
                    verifyQueueOrder(allocations.getJSONObject(i), "root-a-b-b2-b3-b1");
                }
            }
        } finally {
            stop();
        }
    }

    @Test
    public void testAssignWithoutAvailableResource() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (1 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(1024, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "127.0.0.1", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "/default-rack", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(1024), 10)), null);
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.1");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(1000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 0);
        } finally {
            stop();
        }
    }

    @Test
    public void testNoNM() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        try {
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.1:1234");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Thread.sleep(1000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 0);
        } finally {
            stop();
        }
    }

    @Test
    public void testWrongNodeId() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (24 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(1024, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "127.0.0.1", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "/default-rack", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(1024), 10)), null);
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.0");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(1000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 0);
        } finally {
            stop();
        }
    }

    @Test
    public void testReserveNewContainer() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", (4 * 1024), getResourceTrackerService());
        MockNM nm2 = new MockNM("127.0.0.2:1234", (4 * 1024), getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm1);
            RMApp app2 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app2", "user1", null, "b2");
            MockAM am2 = MockRM.launchAndRegisterAM(app2, TestRMWebServicesCapacitySched.rm, nm2);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(4096), 10)), null);
            // Reserve new container
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.2");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            verifyQueueOrder(json.getJSONObject("allocations"), "root-a-b-b3-b1");
            JSONObject allocations = json.getJSONObject("allocations");
            verifyStateOfAllocations(allocations, "finalAllocationState", "RESERVED");
            // Do a node heartbeat again without releasing container from app2
            r = resource();
            params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.2");
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            verifyQueueOrder(json.getJSONObject("allocations"), "b1");
            allocations = json.getJSONObject("allocations");
            verifyStateOfAllocations(allocations, "finalAllocationState", "SKIPPED");
            // Finish application 2
            CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
            ContainerId containerId = ContainerId.newContainerId(am2.getApplicationAttemptId(), 1);
            cs.completedContainer(cs.getRMContainer(containerId), ContainerStatus.newInstance(containerId, COMPLETE, "", 0), FINISHED);
            // Do a node heartbeat again
            r = resource();
            params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.2");
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            verifyQueueOrder(json.getJSONObject("allocations"), "b1");
            allocations = json.getJSONObject("allocations");
            verifyStateOfAllocations(allocations, "finalAllocationState", "ALLOCATED_FROM_RESERVED");
        } finally {
            stop();
        }
    }

    @Test
    public void testActivityJSON() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (24 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("nodeId", "127.0.0.1");
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(1000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            JSONObject allocations = json.getJSONObject("allocations");
            verifyStateOfAllocations(allocations, "finalAllocationState", "ALLOCATED");
            verifyNumberOfNodes(allocations, 5);
            verifyQueueOrder(json.getJSONObject("allocations"), "root-b-b1");
        } finally {
            stop();
        }
    }

    @Test
    public void testAppActivityJSON() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (24 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(5000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            JSONObject allocations = json.getJSONObject("allocations");
            verifyStateOfAllocations(allocations, "allocationState", "ACCEPTED");
            verifyNumberOfAllocationAttempts(allocations, 1);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppAssignMultipleContainersPerNodeHeartbeat() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (24 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(1024, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "127.0.0.1", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "/default-rack", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(1024), 10)), null);
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(5000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 10);
            JSONArray allocations = json.getJSONArray("allocations");
            for (int i = 0; i < (allocations.length()); i++) {
                verifyStateOfAllocations(allocations.getJSONObject(i), "allocationState", "ACCEPTED");
            }
        } finally {
            stop();
        }
    }

    @Test
    public void testAppAssignWithoutAvailableResource() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", (1 * 1024), getResourceTrackerService());
        nm.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(1024, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "127.0.0.1", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "/default-rack", Resources.createResource(1024), 10), ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(1024), 10)), null);
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm.nodeHeartbeat(true);
            Thread.sleep(5000);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 0);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppNoNM() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(1024, "app1", "user1", null, "b1");
            // Get JSON
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            // Get JSON
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 0);
        } finally {
            stop();
        }
    }

    @Test
    public void testAppReserveNewContainer() throws Exception {
        // Start RM so that it accepts app submissions
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", (4 * 1024), getResourceTrackerService());
        MockNM nm2 = new MockNM("127.0.0.2:1234", (4 * 1024), getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        try {
            RMApp app1 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app1", "user1", null, "b1");
            MockAM am1 = MockRM.launchAndRegisterAM(app1, TestRMWebServicesCapacitySched.rm, nm1);
            RMApp app2 = TestRMWebServicesCapacitySched.rm.submitApp(10, "app2", "user1", null, "b2");
            MockAM am2 = MockRM.launchAndRegisterAM(app2, TestRMWebServicesCapacitySched.rm, nm2);
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "*", Resources.createResource(4096), 10)), null);
            // Reserve new container
            WebResource r = resource();
            MultivaluedMapImpl params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            ClientResponse response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 1);
            // Do a node heartbeat again without releasing container from app2
            r = resource();
            params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 2);
            // Finish application 2
            CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
            ContainerId containerId = ContainerId.newContainerId(am2.getApplicationAttemptId(), 1);
            cs.completedContainer(cs.getRMContainer(containerId), ContainerStatus.newInstance(containerId, COMPLETE, "", 0), FINISHED);
            // Do a node heartbeat again
            r = resource();
            params = new MultivaluedMapImpl();
            params.add("appId", app1.getApplicationId().toString());
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            nm2.nodeHeartbeat(true);
            Thread.sleep(1000);
            response = r.path("ws").path("v1").path("cluster").path("scheduler/app-activities").queryParams(params).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            json = response.getEntity(JSONObject.class);
            verifyNumberOfAllocations(json, 3);
        } finally {
            stop();
        }
    }
}

