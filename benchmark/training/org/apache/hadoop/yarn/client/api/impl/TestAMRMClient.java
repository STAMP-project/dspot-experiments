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
package org.apache.hadoop.yarn.client.api.impl;


import CommonConfigurationKeysPublic.HADOOP_RPC_PROTECTION;
import ContainerUpdateType.DEMOTE_EXECUTION_TYPE;
import ContainerUpdateType.PROMOTE_EXECUTION_TYPE;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import FinalApplicationStatus.SUCCEEDED;
import ResourceRequest.ANY;
import STATE.STARTED;
import YarnConfiguration.RM_RESOURCE_PROFILES_ENABLED;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.service.Service.STATE;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.NMTokenCache;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.util.Records;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ExecutionType.GUARANTEED;
import static ExecutionType.OPPORTUNISTIC;
import static Priority.UNDEFINED;


/**
 * Test application master client class to resource manager.
 */
@RunWith(Parameterized.class)
public class TestAMRMClient extends BaseAMRMClientTest {
    private static final int DEFAULT_ITERATION = 3;

    public TestAMRMClient(String schedulerName, boolean autoUpdate) {
        this.schedulerName = schedulerName;
        this.autoUpdate = autoUpdate;
    }

    @Test(timeout = 60000)
    public void testAMRMClientNoMatchingRequests() throws IOException, YarnException {
        AMRMClient<ContainerRequest> amClient = AMRMClient.createAMRMClient();
        amClient.init(conf);
        amClient.start();
        amClient.registerApplicationMaster("Host", 10000, "");
        Resource testCapability1 = Resource.newInstance(1024, 2);
        List<? extends Collection<ContainerRequest>> matches = amClient.getMatchingRequests(priority, node, testCapability1);
        Assert.assertEquals("Expected no matching requests.", matches.size(), 0);
    }

    @Test(timeout = 60000)
    public void testAMRMClientMatchingFit() throws IOException, YarnException {
        AMRMClient<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = AMRMClient.<ContainerRequest>createAMRMClient();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Resource capability1 = Resource.newInstance(1024, 2);
            Resource capability2 = Resource.newInstance(1024, 1);
            Resource capability3 = Resource.newInstance(1000, 2);
            Resource capability4 = Resource.newInstance(2000, 1);
            Resource capability5 = Resource.newInstance(1000, 3);
            Resource capability6 = Resource.newInstance(2000, 1);
            Resource capability7 = Resource.newInstance(2000, 1);
            ContainerRequest storedContainer1 = new ContainerRequest(capability1, nodes, racks, priority);
            ContainerRequest storedContainer2 = new ContainerRequest(capability2, nodes, racks, priority);
            ContainerRequest storedContainer3 = new ContainerRequest(capability3, nodes, racks, priority);
            ContainerRequest storedContainer4 = new ContainerRequest(capability4, nodes, racks, priority);
            ContainerRequest storedContainer5 = new ContainerRequest(capability5, nodes, racks, priority);
            ContainerRequest storedContainer6 = new ContainerRequest(capability6, nodes, racks, priority);
            ContainerRequest storedContainer7 = new ContainerRequest(capability7, nodes, racks, priority2, false);
            amClient.addContainerRequest(storedContainer1);
            amClient.addContainerRequest(storedContainer2);
            amClient.addContainerRequest(storedContainer3);
            amClient.addContainerRequest(storedContainer4);
            amClient.addContainerRequest(storedContainer5);
            amClient.addContainerRequest(storedContainer6);
            amClient.addContainerRequest(storedContainer7);
            // Add some CRs with allocReqIds... These will not be returned by
            // the default getMatchingRequests
            ContainerRequest storedContainer11 = new ContainerRequest(capability1, nodes, racks, priority, 1);
            ContainerRequest storedContainer33 = new ContainerRequest(capability3, nodes, racks, priority, 3);
            ContainerRequest storedContainer43 = new ContainerRequest(capability4, nodes, racks, priority, 3);
            amClient.addContainerRequest(storedContainer11);
            amClient.addContainerRequest(storedContainer33);
            amClient.addContainerRequest(storedContainer43);
            // test matching of containers
            List<? extends Collection<ContainerRequest>> matches;
            ContainerRequest storedRequest;
            // exact match
            Resource testCapability1 = Resource.newInstance(1024, 2);
            matches = amClient.getMatchingRequests(priority, node, testCapability1);
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedContainer1, storedRequest);
            amClient.removeContainerRequest(storedContainer1);
            // exact match for allocReqId 1
            Collection<ContainerRequest> reqIdMatches = amClient.getMatchingRequests(1);
            Assert.assertEquals(1, reqIdMatches.size());
            storedRequest = reqIdMatches.iterator().next();
            Assert.assertEquals(storedContainer11, storedRequest);
            amClient.removeContainerRequest(storedContainer11);
            // exact match for allocReqId 3
            reqIdMatches = amClient.getMatchingRequests(3);
            Assert.assertEquals(2, reqIdMatches.size());
            Iterator<ContainerRequest> iter = reqIdMatches.iterator();
            storedRequest = iter.next();
            Assert.assertEquals(storedContainer43, storedRequest);
            amClient.removeContainerRequest(storedContainer43);
            storedRequest = iter.next();
            Assert.assertEquals(storedContainer33, storedRequest);
            amClient.removeContainerRequest(storedContainer33);
            // exact matching with order maintained
            Resource testCapability2 = Resource.newInstance(2000, 1);
            matches = amClient.getMatchingRequests(priority, node, testCapability2);
            verifyMatches(matches, 2);
            // must be returned in the order they were made
            int i = 0;
            for (ContainerRequest storedRequest1 : matches.get(0)) {
                if ((i++) == 0) {
                    Assert.assertEquals(storedContainer4, storedRequest1);
                } else {
                    Assert.assertEquals(storedContainer6, storedRequest1);
                }
            }
            amClient.removeContainerRequest(storedContainer6);
            // matching with larger container. all requests returned
            Resource testCapability3 = Resource.newInstance(4000, 4);
            matches = amClient.getMatchingRequests(priority, node, testCapability3);
            assert (matches.size()) == 4;
            Resource testCapability4 = Resource.newInstance(1024, 2);
            matches = amClient.getMatchingRequests(priority, node, testCapability4);
            assert (matches.size()) == 2;
            // verify non-fitting containers are not returned and fitting ones are
            for (Collection<ContainerRequest> testSet : matches) {
                Assert.assertEquals(1, testSet.size());
                ContainerRequest testRequest = testSet.iterator().next();
                Assert.assertTrue((testRequest != storedContainer4));
                Assert.assertTrue((testRequest != storedContainer5));
                assert (testRequest == storedContainer2) || (testRequest == storedContainer3);
            }
            Resource testCapability5 = Resource.newInstance(512, 4);
            matches = amClient.getMatchingRequests(priority, node, testCapability5);
            assert (matches.size()) == 0;
            // verify requests without relaxed locality are only returned at specific
            // locations
            Resource testCapability7 = Resource.newInstance(2000, 1);
            matches = amClient.getMatchingRequests(priority2, ANY, testCapability7);
            assert (matches.size()) == 0;
            matches = amClient.getMatchingRequests(priority2, node, testCapability7);
            assert (matches.size()) == 1;
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    /**
     * Test fit of both GUARANTEED and OPPORTUNISTIC containers.
     */
    @Test(timeout = 60000)
    public void testAMRMClientMatchingFitExecType() throws IOException, YarnException {
        AMRMClient<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = AMRMClient.<ContainerRequest>createAMRMClient();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Resource capability1 = Resource.newInstance(1024, 2);
            Resource capability2 = Resource.newInstance(1024, 1);
            Resource capability3 = Resource.newInstance(1000, 2);
            Resource capability4 = Resource.newInstance(1000, 2);
            Resource capability5 = Resource.newInstance(2000, 2);
            Resource capability6 = Resource.newInstance(2000, 3);
            Resource capability7 = Resource.newInstance(6000, 3);
            // Add 2 GUARANTEED and 7 OPPORTUNISTIC requests.
            ContainerRequest storedGuarContainer1 = new ContainerRequest(capability1, nodes, racks, priority);
            ContainerRequest storedGuarContainer2 = new ContainerRequest(capability2, nodes, racks, priority);
            ContainerRequest storedOpportContainer1 = new ContainerRequest(capability1, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer2 = new ContainerRequest(capability2, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer3 = new ContainerRequest(capability3, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer4 = new ContainerRequest(capability4, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer5 = new ContainerRequest(capability5, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer6 = new ContainerRequest(capability6, nodes, racks, priority, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            ContainerRequest storedOpportContainer7 = new ContainerRequest(capability7, nodes, racks, priority2, 0, false, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC));
            amClient.addContainerRequest(storedGuarContainer1);
            amClient.addContainerRequest(storedGuarContainer2);
            amClient.addContainerRequest(storedOpportContainer1);
            amClient.addContainerRequest(storedOpportContainer2);
            amClient.addContainerRequest(storedOpportContainer3);
            amClient.addContainerRequest(storedOpportContainer4);
            amClient.addContainerRequest(storedOpportContainer5);
            amClient.addContainerRequest(storedOpportContainer6);
            amClient.addContainerRequest(storedOpportContainer7);
            // Make sure 3 entries are generated in the ask list for each added
            // container request of a given capability, locality, execution type and
            // priority (one node-local, one rack-local, and one ANY).
            Assert.assertEquals(24, ((AMRMClientImpl<ContainerRequest>) (amClient)).ask.size());
            // test exact matching of GUARANTEED containers
            List<? extends Collection<ContainerRequest>> matches;
            ContainerRequest storedRequest;
            Resource testCapability1 = Resource.newInstance(1024, 2);
            matches = amClient.getMatchingRequests(priority, node, GUARANTEED, testCapability1);
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedGuarContainer1, storedRequest);
            amClient.removeContainerRequest(storedGuarContainer1);
            // test exact matching of OPPORTUNISTIC containers
            matches = amClient.getMatchingRequests(priority, node, OPPORTUNISTIC, testCapability1);
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedOpportContainer1, storedRequest);
            amClient.removeContainerRequest(storedOpportContainer1);
            // exact OPPORTUNISTIC matching with order maintained
            Resource testCapability2 = Resource.newInstance(1000, 2);
            matches = amClient.getMatchingRequests(priority, node, OPPORTUNISTIC, testCapability2);
            verifyMatches(matches, 2);
            // must be returned in the order they were made
            int i = 0;
            for (ContainerRequest storedRequest1 : matches.get(0)) {
                if ((i++) == 0) {
                    Assert.assertEquals(storedOpportContainer3, storedRequest1);
                } else {
                    Assert.assertEquals(storedOpportContainer4, storedRequest1);
                }
            }
            amClient.removeContainerRequest(storedOpportContainer3);
            // matching with larger container
            Resource testCapability3 = Resource.newInstance(4000, 4);
            matches = amClient.getMatchingRequests(priority, node, OPPORTUNISTIC, testCapability3);
            assert (matches.size()) == 4;
            // verify requests without relaxed locality are only returned at specific
            // locations
            Resource testCapability4 = Resource.newInstance(6000, 3);
            matches = amClient.getMatchingRequests(priority2, ANY, OPPORTUNISTIC, testCapability4);
            assert (matches.size()) == 0;
            matches = amClient.getMatchingRequests(priority2, node, OPPORTUNISTIC, testCapability4);
            assert (matches.size()) == 1;
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testAMRMClientMatchingFitInferredRack() throws IOException, YarnException {
        AMRMClientImpl<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = new AMRMClientImpl<ContainerRequest>();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Resource capability = Resource.newInstance(1024, 2);
            ContainerRequest storedContainer1 = new ContainerRequest(capability, nodes, null, priority);
            amClient.addContainerRequest(storedContainer1);
            // verify matching with original node and inferred rack
            List<? extends Collection<ContainerRequest>> matches;
            ContainerRequest storedRequest;
            // exact match node
            matches = amClient.getMatchingRequests(priority, node, capability);
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedContainer1, storedRequest);
            // inferred match rack
            matches = amClient.getMatchingRequests(priority, rack, capability);
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedContainer1, storedRequest);
            // inferred rack match no longer valid after request is removed
            amClient.removeContainerRequest(storedContainer1);
            matches = amClient.getMatchingRequests(priority, rack, capability);
            Assert.assertTrue(matches.isEmpty());
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    // (timeout=60000)
    @Test
    public void testAMRMClientMatchStorage() throws IOException, YarnException {
        AMRMClientImpl<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = ((AMRMClientImpl<ContainerRequest>) (AMRMClient.<ContainerRequest>createAMRMClient()));
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Priority priority1 = Records.newRecord(Priority.class);
            priority1.setPriority(2);
            ContainerRequest storedContainer1 = new ContainerRequest(capability, nodes, racks, priority);
            ContainerRequest storedContainer2 = new ContainerRequest(capability, nodes, racks, priority);
            ContainerRequest storedContainer3 = new ContainerRequest(capability, null, null, priority1);
            amClient.addContainerRequest(storedContainer1);
            amClient.addContainerRequest(storedContainer2);
            amClient.addContainerRequest(storedContainer3);
            // test addition and storage
            RemoteRequestsTable<ContainerRequest> remoteRequestsTable = amClient.getTable(0);
            int containersRequestedAny = remoteRequestsTable.get(priority, ANY, GUARANTEED, capability).remoteRequest.getNumContainers();
            Assert.assertEquals(2, containersRequestedAny);
            containersRequestedAny = remoteRequestsTable.get(priority1, ANY, GUARANTEED, capability).remoteRequest.getNumContainers();
            Assert.assertEquals(1, containersRequestedAny);
            List<? extends Collection<ContainerRequest>> matches = amClient.getMatchingRequests(priority, node, capability);
            verifyMatches(matches, 2);
            matches = amClient.getMatchingRequests(priority, rack, capability);
            verifyMatches(matches, 2);
            matches = amClient.getMatchingRequests(priority, ANY, capability);
            verifyMatches(matches, 2);
            matches = amClient.getMatchingRequests(priority1, rack, capability);
            Assert.assertTrue(matches.isEmpty());
            matches = amClient.getMatchingRequests(priority1, ANY, capability);
            verifyMatches(matches, 1);
            // test removal
            amClient.removeContainerRequest(storedContainer3);
            matches = amClient.getMatchingRequests(priority, node, capability);
            verifyMatches(matches, 2);
            amClient.removeContainerRequest(storedContainer2);
            matches = amClient.getMatchingRequests(priority, node, capability);
            verifyMatches(matches, 1);
            matches = amClient.getMatchingRequests(priority, rack, capability);
            verifyMatches(matches, 1);
            // test matching of containers
            ContainerRequest storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedContainer1, storedRequest);
            amClient.removeContainerRequest(storedContainer1);
            matches = amClient.getMatchingRequests(priority, ANY, capability);
            Assert.assertTrue(matches.isEmpty());
            matches = amClient.getMatchingRequests(priority1, ANY, capability);
            Assert.assertTrue(matches.isEmpty());
            // 0 requests left. everything got cleaned up
            Assert.assertTrue(amClient.getTable(0).isEmpty());
            // go through an exemplary allocation, matching and release cycle
            amClient.addContainerRequest(storedContainer1);
            amClient.addContainerRequest(storedContainer3);
            // RM should allocate container within 2 calls to allocate()
            int allocatedContainerCount = 0;
            int iterationsLeft = 3;
            while ((allocatedContainerCount < 2) && ((iterationsLeft--) > 0)) {
                Log.getLog().info(((((("Allocated " + allocatedContainerCount) + " containers") + " with ") + iterationsLeft) + " iterations left"));
                AllocateResponse allocResponse = amClient.allocate(0.1F);
                Assert.assertEquals(0, amClient.ask.size());
                Assert.assertEquals(0, amClient.release.size());
                Assert.assertEquals(nodeCount, amClient.getClusterNodeCount());
                allocatedContainerCount += allocResponse.getAllocatedContainers().size();
                for (Container container : allocResponse.getAllocatedContainers()) {
                    ContainerRequest expectedRequest = (container.getPriority().equals(storedContainer1.getPriority())) ? storedContainer1 : storedContainer3;
                    matches = amClient.getMatchingRequests(container.getPriority(), ANY, container.getResource());
                    // test correct matched container is returned
                    verifyMatches(matches, 1);
                    ContainerRequest matchedRequest = matches.get(0).iterator().next();
                    Assert.assertEquals(matchedRequest, expectedRequest);
                    amClient.removeContainerRequest(matchedRequest);
                    // assign this container, use it and release it
                    amClient.releaseAssignedContainer(container.getId());
                }
                if (allocatedContainerCount < containersRequestedAny) {
                    // let NM heartbeat to RM and trigger allocations
                    triggerSchedulingWithNMHeartBeat();
                }
            } 
            Assert.assertEquals(2, allocatedContainerCount);
            AllocateResponse allocResponse = amClient.allocate(0.1F);
            Assert.assertEquals(0, amClient.release.size());
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, allocResponse.getAllocatedContainers().size());
            // 0 requests left. everything got cleaned up
            Assert.assertTrue(remoteRequestsTable.isEmpty());
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testAllocationWithBlacklist() throws IOException, YarnException {
        AMRMClientImpl<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = ((AMRMClientImpl<ContainerRequest>) (AMRMClient.<ContainerRequest>createAMRMClient()));
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            ContainerRequest storedContainer1 = new ContainerRequest(capability, nodes, racks, priority);
            amClient.addContainerRequest(storedContainer1);
            Assert.assertEquals(3, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            List<String> localNodeBlacklist = new ArrayList<String>();
            localNodeBlacklist.add(node);
            // put node in black list, so no container assignment
            amClient.updateBlacklist(localNodeBlacklist, null);
            int allocatedContainerCount = getAllocatedContainersNumber(amClient, TestAMRMClient.DEFAULT_ITERATION);
            // the only node is in blacklist, so no allocation
            Assert.assertEquals(0, allocatedContainerCount);
            // Remove node from blacklist, so get assigned with 2
            amClient.updateBlacklist(null, localNodeBlacklist);
            ContainerRequest storedContainer2 = new ContainerRequest(capability, nodes, racks, priority);
            amClient.addContainerRequest(storedContainer2);
            allocatedContainerCount = getAllocatedContainersNumber(amClient, TestAMRMClient.DEFAULT_ITERATION);
            Assert.assertEquals(2, allocatedContainerCount);
            // Test in case exception in allocate(), blacklist is kept
            Assert.assertTrue(amClient.blacklistAdditions.isEmpty());
            Assert.assertTrue(amClient.blacklistRemovals.isEmpty());
            // create a invalid ContainerRequest - memory value is minus
            ContainerRequest invalidContainerRequest = new ContainerRequest(Resource.newInstance((-1024), 1), nodes, racks, priority);
            amClient.addContainerRequest(invalidContainerRequest);
            amClient.updateBlacklist(localNodeBlacklist, null);
            try {
                // allocate() should complain as ContainerRequest is invalid.
                amClient.allocate(0.1F);
                Assert.fail("there should be an exception here.");
            } catch (Exception e) {
                Assert.assertEquals(1, amClient.blacklistAdditions.size());
            }
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithBlacklist() throws IOException, YarnException {
        AMRMClientImpl<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = ((AMRMClientImpl<ContainerRequest>) (AMRMClient.<ContainerRequest>createAMRMClient()));
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            String[] nodes = new String[]{ "node1", "node2", "node3" };
            // Add nodes[0] and nodes[1]
            List<String> nodeList01 = new ArrayList<String>();
            nodeList01.add(nodes[0]);
            nodeList01.add(nodes[1]);
            amClient.updateBlacklist(nodeList01, null);
            Assert.assertEquals(2, amClient.blacklistAdditions.size());
            Assert.assertEquals(0, amClient.blacklistRemovals.size());
            // Add nodes[0] again, verify it is not added duplicated.
            List<String> nodeList02 = new ArrayList<String>();
            nodeList02.add(nodes[0]);
            nodeList02.add(nodes[2]);
            amClient.updateBlacklist(nodeList02, null);
            Assert.assertEquals(3, amClient.blacklistAdditions.size());
            Assert.assertEquals(0, amClient.blacklistRemovals.size());
            // Add nodes[1] and nodes[2] to removal list,
            // Verify addition list remove these two nodes.
            List<String> nodeList12 = new ArrayList<String>();
            nodeList12.add(nodes[1]);
            nodeList12.add(nodes[2]);
            amClient.updateBlacklist(null, nodeList12);
            Assert.assertEquals(1, amClient.blacklistAdditions.size());
            Assert.assertEquals(2, amClient.blacklistRemovals.size());
            // Add nodes[1] again to addition list,
            // Verify removal list will remove this node.
            List<String> nodeList1 = new ArrayList<String>();
            nodeList1.add(nodes[1]);
            amClient.updateBlacklist(nodeList1, null);
            Assert.assertEquals(2, amClient.blacklistAdditions.size());
            Assert.assertEquals(1, amClient.blacklistRemovals.size());
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testAMRMClient() throws IOException, YarnException {
        initAMRMClientAndTest(false);
    }

    @Test(timeout = 60000)
    public void testAMRMClientAllocReqId() throws IOException, YarnException {
        initAMRMClientAndTest(true);
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithSaslEncryption() throws Exception {
        // we have to create a new instance of MiniYARNCluster to avoid SASL qop
        // mismatches between client and server
        teardown();
        conf = new YarnConfiguration();
        conf.set(HADOOP_RPC_PROTECTION, "privacy");
        createClusterAndStartApplication(conf);
        initAMRMClientAndTest(false);
    }

    @Test(timeout = 30000)
    public void testAskWithNodeLabels() {
        AMRMClientImpl<ContainerRequest> client = new AMRMClientImpl<ContainerRequest>();
        // add exp=x to ANY
        client.addContainerRequest(new ContainerRequest(Resource.newInstance(1024, 1), null, null, UNDEFINED, true, "x"));
        Assert.assertEquals(1, client.ask.size());
        Assert.assertEquals("x", client.ask.iterator().next().getNodeLabelExpression());
        // add exp=x then add exp=a to ANY in same priority, only exp=a should kept
        client.addContainerRequest(new ContainerRequest(Resource.newInstance(1024, 1), null, null, UNDEFINED, true, "x"));
        client.addContainerRequest(new ContainerRequest(Resource.newInstance(1024, 1), null, null, UNDEFINED, true, "a"));
        Assert.assertEquals(1, client.ask.size());
        Assert.assertEquals("a", client.ask.iterator().next().getNodeLabelExpression());
        // add exp=x to ANY, rack and node, only resource request has ANY resource
        // name will be assigned the label expression
        // add exp=x then add exp=a to ANY in same priority, only exp=a should kept
        client.addContainerRequest(new ContainerRequest(Resource.newInstance(1024, 1), null, null, UNDEFINED, true, "y"));
        Assert.assertEquals(1, client.ask.size());
        for (ResourceRequest req : client.ask) {
            if (ANY.equals(req.getResourceName())) {
                Assert.assertEquals("y", req.getNodeLabelExpression());
            } else {
                Assert.assertNull(req.getNodeLabelExpression());
            }
        }
        // set container with nodes and racks with labels
        client.addContainerRequest(new ContainerRequest(Resource.newInstance(1024, 1), new String[]{ "rack1" }, new String[]{ "node1", "node2" }, UNDEFINED, true, "y"));
        for (ResourceRequest req : client.ask) {
            if (ANY.equals(req.getResourceName())) {
                Assert.assertEquals("y", req.getNodeLabelExpression());
            } else {
                Assert.assertNull(req.getNodeLabelExpression());
            }
        }
    }

    @Test(timeout = 30000)
    public void testAskWithInvalidNodeLabels() {
        AMRMClientImpl<ContainerRequest> client = new AMRMClientImpl<ContainerRequest>();
        // specified exp with more than one node labels
        verifyAddRequestFailed(client, new ContainerRequest(Resource.newInstance(1024, 1), null, null, UNDEFINED, true, "x && y"));
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithContainerResourceChange() throws IOException, YarnException {
        // Fair scheduler does not support resource change
        Assume.assumeTrue(schedulerName.equals(CapacityScheduler.class.getName()));
        AMRMClient<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = AMRMClient.createAMRMClient();
            Assert.assertNotNull(amClient);
            // asserting we are using the singleton instance cache
            Assert.assertSame(NMTokenCache.getSingleton(), amClient.getNMTokenCache());
            amClient.init(conf);
            amClient.start();
            Assert.assertEquals(STARTED, amClient.getServiceState());
            // start am nm client
            NMClientImpl nmClient = ((NMClientImpl) (NMClient.createNMClient()));
            Assert.assertNotNull(nmClient);
            // asserting we are using the singleton instance cache
            Assert.assertSame(NMTokenCache.getSingleton(), nmClient.getNMTokenCache());
            nmClient.init(conf);
            nmClient.start();
            Assert.assertEquals(STARTED, nmClient.getServiceState());
            // am rm client register the application master with RM
            amClient.registerApplicationMaster("Host", 10000, "");
            // allocate three containers and make sure they are in RUNNING state
            List<Container> containers = allocateAndStartContainers(amClient, nmClient, 3);
            // perform container resource increase and decrease tests
            doContainerResourceChange(amClient, containers);
            // unregister and finish up the test
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test
    public void testAMRMContainerPromotionAndDemotionWithAutoUpdate() throws Exception {
        AMRMClientImpl<AMRMClient.ContainerRequest> amClient = ((AMRMClientImpl<AMRMClient.ContainerRequest>) (AMRMClient.createAMRMClient()));
        amClient.init(conf);
        amClient.start();
        // start am nm client
        NMClientImpl nmClient = ((NMClientImpl) (NMClient.createNMClient()));
        Assert.assertNotNull(nmClient);
        nmClient.init(conf);
        nmClient.start();
        Assert.assertEquals(STARTED, nmClient.getServiceState());
        amClient.registerApplicationMaster("Host", 10000, "");
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // START OPPORTUNISTIC Container, Send allocation request to RM
        Resource reqResource = Resource.newInstance(512, 1);
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(reqResource, null, null, priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        // RM should allocate container within 1 calls to allocate()
        AllocateResponse allocResponse = waitForAllocation(amClient, 1, 0);
        Assert.assertEquals(1, allocResponse.getAllocatedContainers().size());
        startContainer(allocResponse, nmClient);
        Container c = allocResponse.getAllocatedContainers().get(0);
        amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED));
        allocResponse = waitForAllocation(amClient, 0, 1);
        // Make sure container is updated.
        UpdatedContainer updatedContainer = allocResponse.getUpdatedContainers().get(0);
        // If container auto update is not enabled, we need to notify
        // NM about this update.
        if (!(autoUpdate)) {
            nmClient.updateContainerResource(updatedContainer.getContainer());
        }
        // Wait until NM context updated, or fail on timeout.
        waitForNMContextUpdate(updatedContainer, GUARANTEED);
        // Once promoted, demote it back to OPPORTUNISTIC
        amClient.requestContainerUpdate(updatedContainer.getContainer(), UpdateContainerRequest.newInstance(updatedContainer.getContainer().getVersion(), updatedContainer.getContainer().getId(), DEMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC));
        allocResponse = waitForAllocation(amClient, 0, 1);
        // Make sure container is updated.
        updatedContainer = allocResponse.getUpdatedContainers().get(0);
        if (!(autoUpdate)) {
            nmClient.updateContainerResource(updatedContainer.getContainer());
        }
        // Wait until NM context updated, or fail on timeout.
        waitForNMContextUpdate(updatedContainer, OPPORTUNISTIC);
        amClient.close();
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithContainerPromotion() throws IOException, YarnException {
        AMRMClientImpl<AMRMClient.ContainerRequest> amClient = ((AMRMClientImpl<AMRMClient.ContainerRequest>) (AMRMClient.createAMRMClient()));
        // asserting we are not using the singleton instance cache
        Assert.assertSame(NMTokenCache.getSingleton(), amClient.getNMTokenCache());
        amClient.init(conf);
        amClient.start();
        // start am nm client
        NMClientImpl nmClient = ((NMClientImpl) (NMClient.createNMClient()));
        Assert.assertNotNull(nmClient);
        // asserting we are using the singleton instance cache
        Assert.assertSame(NMTokenCache.getSingleton(), nmClient.getNMTokenCache());
        nmClient.init(conf);
        nmClient.start();
        Assert.assertEquals(STARTED, nmClient.getServiceState());
        amClient.registerApplicationMaster("Host", 10000, "");
        // setup container request
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // START OPPORTUNISTIC Container, Send allocation request to RM
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(capability, null, null, priority2, 0, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true)));
        int oppContainersRequestedAny = amClient.getTable(0).get(priority2, ANY, OPPORTUNISTIC, capability).remoteRequest.getNumContainers();
        Assert.assertEquals(1, oppContainersRequestedAny);
        Assert.assertEquals(1, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        Map<ContainerId, Container> allocatedOpportContainers = new HashMap<>();
        int iterationsLeft = 50;
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        AllocateResponse allocResponse = null;
        while ((allocatedContainerCount < oppContainersRequestedAny) && ((iterationsLeft--) > 0)) {
            allocResponse = amClient.allocate(0.1F);
            // let NM heartbeat to RM and trigger allocations
            // triggerSchedulingWithNMHeartBeat();
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            allocatedContainerCount += allocResponse.getAllocatedContainers().size();
            for (Container container : allocResponse.getAllocatedContainers()) {
                if ((container.getExecutionType()) == (OPPORTUNISTIC)) {
                    allocatedOpportContainers.put(container.getId(), container);
                }
            }
            if (allocatedContainerCount < oppContainersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals(oppContainersRequestedAny, allocatedContainerCount);
        Assert.assertEquals(oppContainersRequestedAny, allocatedOpportContainers.size());
        startContainer(allocResponse, nmClient);
        // SEND PROMOTION REQUEST TO RM
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
            allocResponse = amClient.allocate(0.1F);
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
        // SEND UPDATE EXECTYPE UPDATE TO NM
        updateContainerExecType(allocResponse, GUARANTEED, nmClient);
        amClient.ask.clear();
    }

    @Test(timeout = 60000)
    public void testAMRMClientWithContainerDemotion() throws IOException, YarnException {
        AMRMClientImpl<AMRMClient.ContainerRequest> amClient = ((AMRMClientImpl<AMRMClient.ContainerRequest>) (AMRMClient.createAMRMClient()));
        // asserting we are not using the singleton instance cache
        Assert.assertSame(NMTokenCache.getSingleton(), amClient.getNMTokenCache());
        amClient.init(conf);
        amClient.start();
        NMClientImpl nmClient = ((NMClientImpl) (NMClient.createNMClient()));
        Assert.assertNotNull(nmClient);
        // asserting we are using the singleton instance cache
        Assert.assertSame(NMTokenCache.getSingleton(), nmClient.getNMTokenCache());
        nmClient.init(conf);
        nmClient.start();
        Assert.assertEquals(STARTED, nmClient.getServiceState());
        amClient.registerApplicationMaster("Host", 10000, "");
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // START OPPORTUNISTIC Container, Send allocation request to RM
        amClient.addContainerRequest(new AMRMClient.ContainerRequest(capability, null, null, priority2, 0, true, null, ExecutionTypeRequest.newInstance(GUARANTEED, true)));
        int oppContainersRequestedAny = amClient.getTable(0).get(priority2, ANY, GUARANTEED, capability).remoteRequest.getNumContainers();
        Assert.assertEquals(1, oppContainersRequestedAny);
        Assert.assertEquals(1, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        // RM should allocate container within 2 calls to allocate()
        int allocatedContainerCount = 0;
        Map<ContainerId, Container> allocatedGuaranteedContainers = new HashMap<>();
        int iterationsLeft = 50;
        amClient.getNMTokenCache().clearCache();
        Assert.assertEquals(0, amClient.getNMTokenCache().numberOfTokensInCache());
        AllocateResponse allocResponse = null;
        while ((allocatedContainerCount < oppContainersRequestedAny) && ((iterationsLeft--) > 0)) {
            allocResponse = amClient.allocate(0.1F);
            // let NM heartbeat to RM and trigger allocations
            // triggerSchedulingWithNMHeartBeat();
            Assert.assertEquals(0, amClient.ask.size());
            Assert.assertEquals(0, amClient.release.size());
            allocatedContainerCount += allocResponse.getAllocatedContainers().size();
            for (Container container : allocResponse.getAllocatedContainers()) {
                if ((container.getExecutionType()) == (GUARANTEED)) {
                    allocatedGuaranteedContainers.put(container.getId(), container);
                }
            }
            if (allocatedContainerCount < oppContainersRequestedAny) {
                // sleep to let NM's heartbeat to RM and trigger allocations
                sleep(100);
            }
        } 
        Assert.assertEquals(oppContainersRequestedAny, allocatedContainerCount);
        Assert.assertEquals(oppContainersRequestedAny, allocatedGuaranteedContainers.size());
        startContainer(allocResponse, nmClient);
        // SEND DEMOTION REQUEST TO RM
        try {
            Container c = allocatedGuaranteedContainers.values().iterator().next();
            amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), DEMOTE_EXECUTION_TYPE, null, GUARANTEED));
            Assert.fail("Should throw Exception..");
        } catch (IllegalArgumentException e) {
            System.out.println(("## " + (e.getMessage())));
            Assert.assertTrue(e.getMessage().contains("target should be OPPORTUNISTIC and original should be GUARANTEED"));
        }
        Container c = allocatedGuaranteedContainers.values().iterator().next();
        amClient.requestContainerUpdate(c, UpdateContainerRequest.newInstance(c.getVersion(), c.getId(), DEMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC));
        iterationsLeft = 120;
        Map<ContainerId, UpdatedContainer> updatedContainers = new HashMap<>();
        // do a few iterations to ensure RM is not going to send new containers
        while (((iterationsLeft--) > 0) && (updatedContainers.isEmpty())) {
            // inform RM of rejection
            allocResponse = amClient.allocate(0.1F);
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
        Assert.assertEquals(1, updatedContainers.size());
        for (ContainerId cId : allocatedGuaranteedContainers.keySet()) {
            Container orig = allocatedGuaranteedContainers.get(cId);
            UpdatedContainer updatedContainer = updatedContainers.get(cId);
            Assert.assertNotNull(updatedContainer);
            Assert.assertEquals(OPPORTUNISTIC, updatedContainer.getContainer().getExecutionType());
            Assert.assertEquals(orig.getResource(), updatedContainer.getContainer().getResource());
            Assert.assertEquals(orig.getNodeId(), updatedContainer.getContainer().getNodeId());
            Assert.assertEquals(((orig.getVersion()) + 1), updatedContainer.getContainer().getVersion());
        }
        Assert.assertEquals(0, amClient.ask.size());
        Assert.assertEquals(0, amClient.release.size());
        updateContainerExecType(allocResponse, OPPORTUNISTIC, nmClient);
        amClient.ask.clear();
    }

    class CountDownSupplier implements Supplier<Boolean> {
        int counter = 0;

        @Override
        public Boolean get() {
            (counter)++;
            if ((counter) >= 3) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Test
    public void testWaitFor() throws InterruptedException {
        AMRMClientImpl<ContainerRequest> amClient = null;
        TestAMRMClient.CountDownSupplier countDownChecker = new TestAMRMClient.CountDownSupplier();
        try {
            // start am rm client
            amClient = ((AMRMClientImpl<ContainerRequest>) (AMRMClient.<ContainerRequest>createAMRMClient()));
            amClient.init(new YarnConfiguration());
            amClient.start();
            amClient.waitFor(countDownChecker, 1000);
            Assert.assertEquals(3, countDownChecker.counter);
        } finally {
            if (amClient != null) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testAMRMClientOnAMRMTokenRollOver() throws IOException, YarnException {
        AMRMClient<ContainerRequest> amClient = null;
        try {
            AMRMTokenSecretManager amrmTokenSecretManager = yarnCluster.getResourceManager().getRMContext().getAMRMTokenSecretManager();
            // start am rm client
            amClient = AMRMClient.<ContainerRequest>createAMRMClient();
            amClient.init(conf);
            amClient.start();
            Long startTime = System.currentTimeMillis();
            amClient.registerApplicationMaster("Host", 10000, "");
            Token<AMRMTokenIdentifier> amrmToken_1 = getAMRMToken();
            Assert.assertNotNull(amrmToken_1);
            Assert.assertEquals(amrmToken_1.decodeIdentifier().getKeyId(), amrmTokenSecretManager.getMasterKey().getMasterKey().getKeyId());
            // Wait for enough time and make sure the roll_over happens
            // At mean time, the old AMRMToken should continue to work
            while (((System.currentTimeMillis()) - startTime) < ((rollingIntervalSec) * 1000)) {
                amClient.allocate(0.1F);
                sleep(1000);
            } 
            amClient.allocate(0.1F);
            Token<AMRMTokenIdentifier> amrmToken_2 = getAMRMToken();
            Assert.assertNotNull(amrmToken_2);
            Assert.assertEquals(amrmToken_2.decodeIdentifier().getKeyId(), amrmTokenSecretManager.getMasterKey().getMasterKey().getKeyId());
            Assert.assertNotEquals(amrmToken_1, amrmToken_2);
            // can do the allocate call with latest AMRMToken
            AllocateResponse response = amClient.allocate(0.1F);
            // Verify latest AMRMToken can be used to send allocation request.
            UserGroupInformation testUser1 = UserGroupInformation.createRemoteUser("testUser1");
            AMRMTokenIdentifierForTest newVersionTokenIdentifier = new AMRMTokenIdentifierForTest(amrmToken_2.decodeIdentifier(), "message");
            Assert.assertEquals("Message is changed after set to newVersionTokenIdentifier", "message", newVersionTokenIdentifier.getMessage());
            Token<AMRMTokenIdentifier> newVersionToken = new Token<AMRMTokenIdentifier>(newVersionTokenIdentifier.getBytes(), amrmTokenSecretManager.retrievePassword(newVersionTokenIdentifier), newVersionTokenIdentifier.getKind(), new Text());
            SecurityUtil.setTokenService(newVersionToken, yarnCluster.getResourceManager().getApplicationMasterService().getBindAddress());
            testUser1.addToken(newVersionToken);
            AllocateRequest request = Records.newRecord(AllocateRequest.class);
            request.setResponseId(response.getResponseId());
            testUser1.doAs(new PrivilegedAction<ApplicationMasterProtocol>() {
                @Override
                public ApplicationMasterProtocol run() {
                    return ((ApplicationMasterProtocol) (YarnRPC.create(conf).getProxy(ApplicationMasterProtocol.class, yarnCluster.getResourceManager().getApplicationMasterService().getBindAddress(), conf)));
                }
            }).allocate(request);
            // Make sure previous token has been rolled-over
            // and can not use this rolled-over token to make a allocate all.
            while (true) {
                if ((amrmToken_2.decodeIdentifier().getKeyId()) != (amrmTokenSecretManager.getCurrnetMasterKeyData().getMasterKey().getKeyId())) {
                    if ((amrmTokenSecretManager.getNextMasterKeyData()) == null) {
                        break;
                    } else
                        if ((amrmToken_2.decodeIdentifier().getKeyId()) != (amrmTokenSecretManager.getNextMasterKeyData().getMasterKey().getKeyId())) {
                            break;
                        }

                }
                amClient.allocate(0.1F);
                sleep(1000);
            } 
            try {
                UserGroupInformation testUser2 = UserGroupInformation.createRemoteUser("testUser2");
                SecurityUtil.setTokenService(amrmToken_2, yarnCluster.getResourceManager().getApplicationMasterService().getBindAddress());
                testUser2.addToken(amrmToken_2);
                testUser2.doAs(new PrivilegedAction<ApplicationMasterProtocol>() {
                    @Override
                    public ApplicationMasterProtocol run() {
                        return ((ApplicationMasterProtocol) (YarnRPC.create(conf).getProxy(ApplicationMasterProtocol.class, yarnCluster.getResourceManager().getApplicationMasterService().getBindAddress(), conf)));
                    }
                }).allocate(Records.newRecord(AllocateRequest.class));
                Assert.fail("The old Token should not work");
            } catch (Exception ex) {
                Assert.assertTrue((ex instanceof InvalidToken));
                Assert.assertTrue(ex.getMessage().contains(("Invalid AMRMToken from " + (amrmToken_2.decodeIdentifier().getApplicationAttemptId()))));
            }
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testGetMatchingFitWithProfiles() throws Exception {
        teardown();
        conf.setBoolean(RM_RESOURCE_PROFILES_ENABLED, true);
        createClusterAndStartApplication(conf);
        AMRMClient<ContainerRequest> amClient = null;
        try {
            // start am rm client
            amClient = AMRMClient.<ContainerRequest>createAMRMClient();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            ContainerRequest storedContainer1 = new ContainerRequest(Resource.newInstance(0, 0), nodes, racks, priority, "minimum");
            ContainerRequest storedContainer2 = new ContainerRequest(Resource.newInstance(0, 0), nodes, racks, priority, "default");
            ContainerRequest storedContainer3 = new ContainerRequest(Resource.newInstance(0, 0), nodes, racks, priority, "maximum");
            ContainerRequest storedContainer4 = new ContainerRequest(Resource.newInstance(2048, 1), nodes, racks, priority, "minimum");
            ContainerRequest storedContainer5 = new ContainerRequest(Resource.newInstance(2048, 1), nodes, racks, priority2, "default");
            ContainerRequest storedContainer6 = new ContainerRequest(Resource.newInstance(2048, 1), nodes, racks, priority, "default");
            ContainerRequest storedContainer7 = new ContainerRequest(Resource.newInstance(0, 0), nodes, racks, priority, "http");
            amClient.addContainerRequest(storedContainer1);
            amClient.addContainerRequest(storedContainer2);
            amClient.addContainerRequest(storedContainer3);
            amClient.addContainerRequest(storedContainer4);
            amClient.addContainerRequest(storedContainer5);
            amClient.addContainerRequest(storedContainer6);
            amClient.addContainerRequest(storedContainer7);
            // test matching of containers
            List<? extends Collection<ContainerRequest>> matches;
            ContainerRequest storedRequest;
            // exact match
            matches = amClient.getMatchingRequests(priority, node, GUARANTEED, Resource.newInstance(0, 0), "minimum");
            verifyMatches(matches, 1);
            storedRequest = matches.get(0).iterator().next();
            Assert.assertEquals(storedContainer1, storedRequest);
            amClient.removeContainerRequest(storedContainer1);
            // exact matching with order maintained
            // we should get back 3 matches - default + http because they have the
            // same capability
            matches = amClient.getMatchingRequests(priority, node, GUARANTEED, Resource.newInstance(0, 0), "default");
            verifyMatches(matches, 2);
            // must be returned in the order they were made
            int i = 0;
            for (ContainerRequest storedRequest1 : matches.get(0)) {
                switch (i) {
                    case 0 :
                        Assert.assertEquals(storedContainer2, storedRequest1);
                        break;
                    case 1 :
                        Assert.assertEquals(storedContainer7, storedRequest1);
                        break;
                }
                i++;
            }
            amClient.removeContainerRequest(storedContainer5);
            // matching with larger container. all requests returned
            Resource testCapability3 = Resource.newInstance(8192, 8);
            matches = amClient.getMatchingRequests(priority, node, testCapability3);
            Assert.assertEquals(3, matches.size());
            Resource testCapability4 = Resource.newInstance(2048, 1);
            matches = amClient.getMatchingRequests(priority, node, testCapability4);
            Assert.assertEquals(1, matches.size());
        } finally {
            if ((amClient != null) && ((amClient.getServiceState()) == (STATE.STARTED))) {
                amClient.stop();
            }
        }
    }

    @Test(timeout = 60000)
    public void testNoUpdateTrackingUrl() {
        try {
            AMRMClientImpl<ContainerRequest> amClient = null;
            amClient = new AMRMClientImpl();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            Assert.assertEquals("", amClient.appTrackingUrl);
            ApplicationMasterProtocol mockRM = Mockito.mock(ApplicationMasterProtocol.class);
            AllocateResponse mockResponse = Mockito.mock(AllocateResponse.class);
            Mockito.when(mockRM.allocate(ArgumentMatchers.any(AllocateRequest.class))).thenReturn(mockResponse);
            ApplicationMasterProtocol realRM = amClient.rmClient;
            amClient.rmClient = mockRM;
            // Do allocate without updated tracking url
            amClient.allocate(0.1F);
            ArgumentCaptor<AllocateRequest> argument = ArgumentCaptor.forClass(AllocateRequest.class);
            Mockito.verify(mockRM).allocate(argument.capture());
            Assert.assertNull(argument.getValue().getTrackingUrl());
            amClient.rmClient = realRM;
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } catch (IOException | YarnException e) {
            throw new AssertionError(("testNoUpdateTrackingUrl unexpectedly threw exception: " + e));
        }
    }

    @Test(timeout = 60000)
    public void testUpdateTrackingUrl() {
        try {
            AMRMClientImpl<ContainerRequest> amClient = null;
            amClient = new AMRMClientImpl();
            amClient.init(conf);
            amClient.start();
            amClient.registerApplicationMaster("Host", 10000, "");
            String trackingUrl = "hadoop.apache.org";
            Assert.assertEquals("", amClient.appTrackingUrl);
            ApplicationMasterProtocol mockRM = Mockito.mock(ApplicationMasterProtocol.class);
            AllocateResponse mockResponse = Mockito.mock(AllocateResponse.class);
            Mockito.when(mockRM.allocate(ArgumentMatchers.any(AllocateRequest.class))).thenReturn(mockResponse);
            ApplicationMasterProtocol realRM = amClient.rmClient;
            amClient.rmClient = mockRM;
            // Do allocate with updated tracking url
            amClient.updateTrackingUrl(trackingUrl);
            Assert.assertEquals(trackingUrl, amClient.newTrackingUrl);
            Assert.assertEquals("", amClient.appTrackingUrl);
            amClient.allocate(0.1F);
            Assert.assertNull(amClient.newTrackingUrl);
            Assert.assertEquals(trackingUrl, amClient.appTrackingUrl);
            ArgumentCaptor<AllocateRequest> argument = ArgumentCaptor.forClass(AllocateRequest.class);
            Mockito.verify(mockRM).allocate(argument.capture());
            Assert.assertEquals(trackingUrl, argument.getValue().getTrackingUrl());
            amClient.rmClient = realRM;
            amClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        } catch (IOException | YarnException e) {
            throw new AssertionError(("testUpdateTrackingUrl unexpectedly threw exception: " + e));
        }
    }
}

