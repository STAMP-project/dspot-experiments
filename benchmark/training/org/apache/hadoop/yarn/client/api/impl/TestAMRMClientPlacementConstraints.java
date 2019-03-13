/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.client.api.impl;


import AMRMClient.ContainerRequest;
import AMRMClientAsync.AbstractCallbackHandler;
import ExecutionType.GUARANTEED;
import YarnConfiguration.PROCESSOR_RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.RejectedSchedulingRequest;
import org.apache.hadoop.yarn.api.records.SchedulingRequest;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.NMTokenCache;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Placement Constraints and Scheduling Requests.
 */
public class TestAMRMClientPlacementConstraints extends BaseAMRMClientTest {
    private List<Container> allocatedContainers = null;

    private List<RejectedSchedulingRequest> rejectedSchedulingRequests = null;

    private Map<Set<String>, PlacementConstraint> pcMapping = null;

    @Test(timeout = 60000)
    public void testAMRMClientWithPlacementConstraintsByPlacementProcessor() throws Exception {
        // we have to create a new instance of MiniYARNCluster to avoid SASL qop
        // mismatches between client and server
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, PROCESSOR_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        createClusterAndStartApplication(conf);
        allocatedContainers.clear();
        rejectedSchedulingRequests.clear();
        AMRMClient<AMRMClient.ContainerRequest> amClient = AMRMClient.<AMRMClient.ContainerRequest>createAMRMClient();
        amClient.setNMTokenCache(new NMTokenCache());
        // asserting we are not using the singleton instance cache
        Assert.assertNotSame(NMTokenCache.getSingleton(), amClient.getNMTokenCache());
        AMRMClientAsync asyncClient = new org.apache.hadoop.yarn.client.api.async.impl.AMRMClientAsyncImpl(amClient, 1000, new TestAMRMClientPlacementConstraints.TestCallbackHandler());
        asyncClient.init(conf);
        asyncClient.start();
        asyncClient.registerApplicationMaster("Host", 10000, "", pcMapping);
        // Send two types of requests - 4 with source tag "foo" have numAlloc = 1
        // and 1 with source tag "bar" and has numAlloc = 4. Both should be
        // handled similarly. i.e: Since there are only 3 nodes,
        // 2 schedulingRequests - 1 with source tag "foo" on one with source
        // tag "bar" should get rejected.
        asyncClient.addSchedulingRequests(// 4 reqs with numAlloc = 1
        // 1 req with numAlloc = 4
        Arrays.asList(TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 1, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 2, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 3, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 4, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(4, 1, 5, 1, 512, "bar")));
        // kick the scheduler
        TestAMRMClientPlacementConstraints.waitForContainerAllocation(allocatedContainers, rejectedSchedulingRequests, 6, 2);
        Assert.assertEquals(6, allocatedContainers.size());
        Map<NodeId, List<Container>> containersPerNode = allocatedContainers.stream().collect(Collectors.groupingBy(Container::getNodeId));
        Map<Set<String>, List<SchedulingRequest>> outstandingSchedRequests = getOutstandingSchedRequests();
        // Check the outstanding SchedulingRequests
        Assert.assertEquals(2, outstandingSchedRequests.size());
        Assert.assertEquals(1, outstandingSchedRequests.get(new HashSet(Collections.singletonList("foo"))).size());
        Assert.assertEquals(1, outstandingSchedRequests.get(new HashSet(Collections.singletonList("bar"))).size());
        // Ensure 2 containers allocated per node.
        // Each node should have a "foo" and a "bar" container.
        Assert.assertEquals(3, containersPerNode.entrySet().size());
        HashSet<String> srcTags = new HashSet<>(Arrays.asList("foo", "bar"));
        containersPerNode.entrySet().forEach(( x) -> Assert.assertEquals(srcTags, x.getValue().stream().map(( y) -> y.getAllocationTags().iterator().next()).collect(Collectors.toSet())));
        // Ensure 2 rejected requests - 1 of "foo" and 1 of "bar"
        Assert.assertEquals(2, rejectedSchedulingRequests.size());
        Assert.assertEquals(srcTags, rejectedSchedulingRequests.stream().map(( x) -> x.getRequest().getAllocationTags().iterator().next()).collect(Collectors.toSet()));
        asyncClient.stop();
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithPlacementConstraintsByScheduler() throws Exception {
        // we have to create a new instance of MiniYARNCluster to avoid SASL qop
        // mismatches between client and server
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        createClusterAndStartApplication(conf);
        allocatedContainers.clear();
        rejectedSchedulingRequests.clear();
        AMRMClient<AMRMClient.ContainerRequest> amClient = AMRMClient.<AMRMClient.ContainerRequest>createAMRMClient();
        amClient.setNMTokenCache(new NMTokenCache());
        // asserting we are not using the singleton instance cache
        Assert.assertNotSame(NMTokenCache.getSingleton(), amClient.getNMTokenCache());
        AMRMClientAsync asyncClient = new org.apache.hadoop.yarn.client.api.async.impl.AMRMClientAsyncImpl(amClient, 1000, new TestAMRMClientPlacementConstraints.TestCallbackHandler());
        asyncClient.init(conf);
        asyncClient.start();
        asyncClient.registerApplicationMaster("Host", 10000, "", pcMapping);
        // Send two types of requests - 4 with source tag "foo" have numAlloc = 1
        // and 1 with source tag "bar" and has numAlloc = 4. Both should be
        // handled similarly. i.e: Since there are only 3 nodes,
        // 2 schedulingRequests - 1 with source tag "foo" on one with source
        // tag "bar" should get rejected.
        asyncClient.addSchedulingRequests(// 4 reqs with numAlloc = 1
        // 1 req with numAlloc = 4
        // 1 empty tag
        Arrays.asList(TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 1, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 2, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 3, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 4, 1, 512, "foo"), TestAMRMClientPlacementConstraints.schedulingRequest(4, 1, 5, 1, 512, "bar"), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 6, 1, 512, new HashSet<>())));
        // kick the scheduler
        TestAMRMClientPlacementConstraints.waitForContainerAllocation(allocatedContainers, rejectedSchedulingRequests, 7, 0);
        Assert.assertEquals(7, allocatedContainers.size());
        Map<NodeId, List<Container>> containersPerNode = allocatedContainers.stream().collect(Collectors.groupingBy(Container::getNodeId));
        Map<Set<String>, List<SchedulingRequest>> outstandingSchedRequests = getOutstandingSchedRequests();
        // Check the outstanding SchedulingRequests
        Assert.assertEquals(3, outstandingSchedRequests.size());
        Assert.assertEquals(1, outstandingSchedRequests.get(new HashSet(Collections.singletonList("foo"))).size());
        Assert.assertEquals(1, outstandingSchedRequests.get(new HashSet(Collections.singletonList("bar"))).size());
        Assert.assertEquals(0, outstandingSchedRequests.get(new HashSet<String>()).size());
        // Each node should have a "foo" and a "bar" container.
        Assert.assertEquals(3, containersPerNode.entrySet().size());
        HashSet<String> srcTags = new HashSet<>(Arrays.asList("foo", "bar"));
        containersPerNode.entrySet().forEach(( x) -> Assert.assertEquals(srcTags, x.getValue().stream().filter(( y) -> !(y.getAllocationTags().isEmpty())).map(( y) -> y.getAllocationTags().iterator().next()).collect(Collectors.toSet())));
        // The rejected requests were not set by scheduler
        Assert.assertEquals(0, rejectedSchedulingRequests.size());
        asyncClient.stop();
    }

    /* Three cases of empty HashSet key of outstandingSchedRequests
    1. Not set any tags
    2. Set a empty set, e.g ImmutableSet.of(), new HashSet<>()
    3. Set tag as null
     */
    @Test
    public void testEmptyKeyOfOutstandingSchedRequests() {
        AMRMClient<AMRMClient.ContainerRequest> amClient = AMRMClient.<AMRMClient.ContainerRequest>createAMRMClient();
        HashSet<String> schedRequest = null;
        amClient.addSchedulingRequests(Arrays.asList(TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 1, 1, 512, GUARANTEED), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 2, 1, 512, new HashSet<>()), TestAMRMClientPlacementConstraints.schedulingRequest(1, 1, 3, 1, 512, schedRequest)));
        Map<Set<String>, List<SchedulingRequest>> outstandingSchedRequests = getOutstandingSchedRequests();
        Assert.assertEquals(1, outstandingSchedRequests.size());
        Assert.assertEquals(3, outstandingSchedRequests.get(new HashSet<String>()).size());
    }

    private class TestCallbackHandler extends AMRMClientAsync.AbstractCallbackHandler {
        @Override
        public void onContainersAllocated(List<Container> containers) {
            allocatedContainers.addAll(containers);
        }

        @Override
        public void onRequestsRejected(List<RejectedSchedulingRequest> rejReqs) {
            rejectedSchedulingRequests.addAll(rejReqs);
        }

        @Override
        public void onContainersCompleted(List<ContainerStatus> statuses) {
        }

        @Override
        public void onContainersUpdated(List<UpdatedContainer> containers) {
        }

        @Override
        public void onShutdownRequest() {
        }

        @Override
        public void onNodesUpdated(List<NodeReport> updatedNodes) {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public float getProgress() {
            return 0.1F;
        }
    }
}

