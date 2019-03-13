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
import ContainerState.COMPLETE;
import ContainerUpdateType.DEMOTE_EXECUTION_TYPE;
import ContainerUpdateType.PROMOTE_EXECUTION_TYPE;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import ResourceRequest.ANY;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ExecutionType;
import org.apache.hadoop.yarn.api.records.ExecutionTypeRequest;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.UpdateContainerRequest;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Class that tests the allocation of OPPORTUNISTIC containers through the
 * centralized ResourceManager.
 */
public class TestOpportunisticContainerAllocationE2E {
    private static Configuration conf = null;

    private static MiniYARNCluster yarnCluster = null;

    private static YarnClient yarnClient = null;

    private static List<NodeReport> nodeReports = null;

    private static int nodeCount = 3;

    private static final int ROLLING_INTERVAL_SEC = 13;

    private static final long AM_EXPIRE_MS = 4000;

    private static Resource capability;

    private static Priority priority;

    private static Priority priority2;

    private static Priority priority3;

    private static Priority priority4;

    private static String node;

    private static String rack;

    private static String[] nodes;

    private static String[] racks;

    private static final int DEFAULT_ITERATION = 3;

    // Per test..
    private ApplicationAttemptId attemptId = null;

    private AMRMClientImpl<AMRMClient.ContainerRequest> amClient = null;

    private long availMB;

    private int availVCores;

    private long allocMB;

    private int allocVCores;

    @Test(timeout = 60000)
    public void testPromotionFromAcquired() throws IOException, YarnException {
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        int oppContainersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority2, ANY, OPPORTUNISTIC, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        Assert.assertEquals(1, oppContainersRequestedAny);
        Assert.assertEquals(1, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        Map<ContainerId, Container> allocatedOpportContainers = new HashMap<>();
        int iterationsLeft = 50;
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        HashMap<String, Token> receivedNMTokens = new HashMap<>();
        updateMetrics("Before Opp Allocation");
        while ((allocatedContainerCount < oppContainersRequestedAny) && ((iterationsLeft--) > 0)) {
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            allocatedContainerCount += allocResponse.getAllocatedContainers().size();
            for (Container container : allocResponse.getAllocatedContainers()) {
                if ((container.getExecutionType()) == (ExecutionType.OPPORTUNISTIC)) {
                    allocatedOpportContainers.put(container.getId(), container);
                    removeCR(container);
                }
            }
            for (NMToken token : allocResponse.getNMTokens()) {
                String nodeID = token.getNodeId().toString();
                receivedNMTokens.put(nodeID, token.getToken());
            }
            if (allocatedContainerCount < oppContainersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals(oppContainersRequestedAny, allocatedContainerCount);
        Assert.assertEquals(oppContainersRequestedAny, allocatedOpportContainers.size());
        updateMetrics("After Opp Allocation / Before Promotion");
        try {
            Container c = allocatedOpportContainers.values().iterator().next();
            amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), PROMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC));
            Assert.fail("Should throw Exception..");
        } catch (IllegalArgumentException e) {
            System.out.println(("## " + (e.getMessage())));
            Assert.assertTrue(e.getMessage().contains("target should be GUARANTEED and original should be OPPORTUNISTIC"));
        }
        Container c = allocatedOpportContainers.values().iterator().next();
        amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED));
        iterationsLeft = 120;
        Map<ContainerId, UpdatedContainer> updatedContainers = new HashMap<>();
        // do a few iterations to ensure RM is not going to send new containers
        while (((iterationsLeft--) > 0) && (updatedContainers.isEmpty())) {
            // inform RM of rejection
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            // RM did not send new containers because AM does not need any
            if ((allocResponse.getUpdatedContainers()) != null) {
                for (UpdatedContainer updatedContainer : allocResponse.getUpdatedContainers()) {
                    System.out.println("Got update..");
                    updatedContainers.put(updatedContainer.getContainer().getId(), updatedContainer);
                }
            }
            if (iterationsLeft > 0) {
                // sleep to make sure NM's heartbeat
                sleep(100);
            }
        } 
        updateMetrics("After Promotion");
        Assert.assertEquals(1, updatedContainers.size());
        for (ContainerId cId : allocatedOpportContainers.keySet()) {
            Container orig = allocatedOpportContainers.get(cId);
            UpdatedContainer updatedContainer = updatedContainers.get(cId);
            Assert.assertNotNull(updatedContainer);
            Assert.assertEquals(GUARANTEED, updatedContainer.getContainer().getExecutionType());
            Assert.assertEquals(orig.getResource(), updatedContainer.getContainer().getResource());
            Assert.assertEquals(orig.getNodeId(), updatedContainer.getContainer().getNodeId());
            Assert.assertEquals(((orig.getVersion()) + 1), updatedContainer.getContainer().getVersion());
        }
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.ask.clear();
    }

    @Test(timeout = 60000)
    public void testDemotionFromAcquired() throws IOException, YarnException {
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority3));
        int guarContainersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority3, ANY, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        Assert.assertEquals(1, guarContainersRequestedAny);
        Assert.assertEquals(1, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        Map<ContainerId, Container> allocatedGuarContainers = new HashMap<>();
        int iterationsLeft = 50;
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        HashMap<String, Token> receivedNMTokens = new HashMap<>();
        updateMetrics("Before Guar Allocation");
        while ((allocatedContainerCount < guarContainersRequestedAny) && ((iterationsLeft--) > 0)) {
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            allocatedContainerCount += allocResponse.getAllocatedContainers().size();
            for (Container container : allocResponse.getAllocatedContainers()) {
                if ((container.getExecutionType()) == (ExecutionType.GUARANTEED)) {
                    allocatedGuarContainers.put(container.getId(), container);
                    removeCR(container);
                }
            }
            for (NMToken token : allocResponse.getNMTokens()) {
                String nodeID = token.getNodeId().toString();
                receivedNMTokens.put(nodeID, token.getToken());
            }
            if (allocatedContainerCount < guarContainersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals(guarContainersRequestedAny, allocatedContainerCount);
        Assert.assertEquals(guarContainersRequestedAny, allocatedGuarContainers.size());
        updateMetrics("After Guar Allocation / Before Demotion");
        try {
            Container c = allocatedGuarContainers.values().iterator().next();
            amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), DEMOTE_EXECUTION_TYPE, null, GUARANTEED));
            Assert.fail("Should throw Exception..");
        } catch (IllegalArgumentException e) {
            System.out.println(("## " + (e.getMessage())));
            Assert.assertTrue(e.getMessage().contains("target should be OPPORTUNISTIC and original should be GUARANTEED"));
        }
        Container c = allocatedGuarContainers.values().iterator().next();
        amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), DEMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC));
        iterationsLeft = 120;
        Map<ContainerId, UpdatedContainer> updatedContainers = new HashMap<>();
        // do a few iterations to ensure RM is not going to send new containers
        while (((iterationsLeft--) > 0) && (updatedContainers.isEmpty())) {
            // inform RM of rejection
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            // RM did not send new containers because AM does not need any
            if ((allocResponse.getUpdatedContainers()) != null) {
                for (UpdatedContainer updatedContainer : allocResponse.getUpdatedContainers()) {
                    System.out.println("Got update..");
                    updatedContainers.put(updatedContainer.getContainer().getId(), updatedContainer);
                }
            }
            if (iterationsLeft > 0) {
                // sleep to make sure NM's heartbeat
                sleep(100);
            }
        } 
        updateMetrics("After Demotion");
        Assert.assertEquals(1, updatedContainers.size());
        for (ContainerId cId : allocatedGuarContainers.keySet()) {
            Container orig = allocatedGuarContainers.get(cId);
            UpdatedContainer updatedContainer = updatedContainers.get(cId);
            Assert.assertNotNull(updatedContainer);
            Assert.assertEquals(OPPORTUNISTIC, updatedContainer.getContainer().getExecutionType());
            Assert.assertEquals(orig.getResource(), updatedContainer.getContainer().getResource());
            Assert.assertEquals(orig.getNodeId(), updatedContainer.getContainer().getNodeId());
            Assert.assertEquals(((orig.getVersion()) + 1), updatedContainer.getContainer().getVersion());
        }
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.ask.clear();
    }

    @Test(timeout = 60000)
    public void testMixedAllocationAndRelease() throws IOException, YarnException {
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        int containersRequestedNode = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, TestOpportunisticContainerAllocationE2E.node, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        int containersRequestedRack = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, TestOpportunisticContainerAllocationE2E.rack, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        int containersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, ANY, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        int oppContainersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority2, ANY, OPPORTUNISTIC, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        Assert.assertEquals(4, containersRequestedNode);
        Assert.assertEquals(4, containersRequestedRack);
        Assert.assertEquals(4, containersRequestedAny);
        Assert.assertEquals(2, oppContainersRequestedAny);
        Assert.assertEquals(4, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        containersRequestedNode = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, TestOpportunisticContainerAllocationE2E.node, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        containersRequestedRack = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, TestOpportunisticContainerAllocationE2E.rack, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        containersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority, ANY, GUARANTEED, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        oppContainersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority2, ANY, OPPORTUNISTIC, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        Assert.assertEquals(2, containersRequestedNode);
        Assert.assertEquals(2, containersRequestedRack);
        Assert.assertEquals(2, containersRequestedAny);
        Assert.assertEquals(1, oppContainersRequestedAny);
        Assert.assertEquals(4, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        int allocatedOpportContainerCount = 0;
        int iterationsLeft = 50;
        Set<ContainerId> releases = new TreeSet<>();
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        HashMap<String, Token> receivedNMTokens = new HashMap<>();
        while ((allocatedContainerCount < (containersRequestedAny + oppContainersRequestedAny)) && ((iterationsLeft--) > 0)) {
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            allocatedContainerCount += allocResponse.getAllocatedContainers().size();
            for (Container container : allocResponse.getAllocatedContainers()) {
                if ((container.getExecutionType()) == (ExecutionType.OPPORTUNISTIC)) {
                    allocatedOpportContainerCount++;
                }
                ContainerId rejectContainerId = container.getId();
                releases.add(rejectContainerId);
            }
            for (NMToken token : allocResponse.getNMTokens()) {
                String nodeID = token.getNodeId().toString();
                receivedNMTokens.put(nodeID, token.getToken());
            }
            if (allocatedContainerCount < containersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals((containersRequestedAny + oppContainersRequestedAny), allocatedContainerCount);
        Assert.assertEquals(oppContainersRequestedAny, allocatedOpportContainerCount);
        for (ContainerId rejectContainerId : releases) {
            amClient.releaseAssignedContainer(rejectContainerId);
        }
        Assert.assertEquals(3, amClient.release.size());
        Assert.assertEquals(0, amClient.ask.size());
        // need to tell the AMRMClient that we don't need these resources anymore
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority));
        amClient.removeContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, TestOpportunisticContainerAllocationE2E.nodes, TestOpportunisticContainerAllocationE2E.racks, TestOpportunisticContainerAllocationE2E.priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        Assert.assertEquals(4, amClient.ask.size());
        iterationsLeft = 3;
        // do a few iterations to ensure RM is not going to send new containers
        while ((iterationsLeft--) > 0) {
            // inform RM of rejection
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            // RM did not send new containers because AM does not need any
            Assert.assertEquals(0, allocResponse.getAllocatedContainers().size());
            if ((allocResponse.getCompletedContainersStatuses().size()) > 0) {
                for (ContainerStatus cStatus : allocResponse.getCompletedContainersStatuses()) {
                    if (releases.contains(cStatus.getContainerId())) {
                        Assert.assertEquals(cStatus.getState(), COMPLETE);
                        Assert.assertEquals((-100), cStatus.getExitStatus());
                        releases.remove(cStatus.getContainerId());
                    }
                }
            }
            if (iterationsLeft > 0) {
                // sleep to make sure NM's heartbeat
                sleep(100);
            }
        } 
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
    }

    /**
     * Tests allocation with requests comprising only opportunistic containers.
     */
    @Test(timeout = 60000)
    public void testOpportunisticAllocation() throws IOException, YarnException {
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority3, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(TestOpportunisticContainerAllocationE2E.capability, null, null, TestOpportunisticContainerAllocationE2E.priority3, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        int oppContainersRequestedAny = amClient.getTable(0).get(TestOpportunisticContainerAllocationE2E.priority3, ANY, OPPORTUNISTIC, TestOpportunisticContainerAllocationE2E.capability).remoteRequest.getNumContainers();
        Assert.assertEquals(2, oppContainersRequestedAny);
        Assert.assertEquals(1, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        int iterationsLeft = 10;
        Set<ContainerId> releases = new TreeSet<>();
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        HashMap<String, Token> receivedNMTokens = new HashMap<>();
        while ((allocatedContainerCount < oppContainersRequestedAny) && ((iterationsLeft--) > 0)) {
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            for (Container container : allocResponse.getAllocatedContainers()) {
                allocatedContainerCount++;
                ContainerId rejectContainerId = container.getId();
                releases.add(rejectContainerId);
            }
            for (NMToken token : allocResponse.getNMTokens()) {
                String nodeID = token.getNodeId().toString();
                receivedNMTokens.put(nodeID, token.getToken());
            }
            if (allocatedContainerCount < oppContainersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals(oppContainersRequestedAny, allocatedContainerCount);
        Assert.assertEquals(1, receivedNMTokens.values().size());
    }
}

