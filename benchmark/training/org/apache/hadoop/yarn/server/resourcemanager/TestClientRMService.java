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
package org.apache.hadoop.yarn.server.resourcemanager;


import ApplicationAccessType.VIEW_APP;
import ApplicationsRequestScope.ALL;
import ApplicationsRequestScope.OWN;
import ApplicationsRequestScope.VIEWABLE;
import CapacitySchedulerConfiguration.RESOURCE_CALCULATOR_CLASS;
import NodeAttribute.PREFIX_CENTRALIZED;
import NodeAttribute.PREFIX_DISTRIBUTED;
import NodeAttributeType.STRING;
import NodeLabel.DEFAULT_NODE_LABEL_PARTITION;
import NodeLabel.NODE_LABEL_EXPRESSION_NOT_SET;
import NodeState.DECOMMISSIONING;
import NodeState.RUNNING;
import NodeState.UNHEALTHY;
import QueueACL.ADMINISTER_QUEUE;
import QueueACL.SUBMIT_APPLICATIONS;
import RMServerUtils.DUMMY_APPLICATION_RESOURCE_USAGE_REPORT;
import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import YarnApplicationState.KILLED;
import YarnConfiguration.DEFAULT_APPLICATION_NAME;
import YarnConfiguration.DEFAULT_QUEUE_NAME;
import YarnConfiguration.MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY;
import YarnConfiguration.RM_NODES_EXCLUDE_FILE_PATH;
import YarnConfiguration.RM_SCHEDULER;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.AccessControlException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetAllResourceTypeInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetAllResourceTypeInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetAttributesToNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetAttributesToNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeAttributesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeAttributesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetLabelsToNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetLabelsToNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToAttributesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToAttributesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationListRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationListResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.UpdateApplicationPriorityRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.api.records.NodeAttributeInfo;
import org.apache.hadoop.yarn.api.records.NodeAttributeKey;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.NodeToAttributeValue;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueConfigurations;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.nodelabels.NodeAttributesManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.resourcemanager.timelineservice.RMTimelineCollectorManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClientRMService {
    private static final Logger LOG = LoggerFactory.getLogger(TestClientRMService.class);

    private RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private String appType = "MockApp";

    private static final String QUEUE_1 = "Q-1";

    private static final String QUEUE_2 = "Q-2";

    private File resourceTypesFile = null;

    @Test
    public void testGetDecommissioningClusterNodes() throws Exception {
        MockRM rm = new MockRM() {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        int nodeMemory = 1024;
        MockNM nm1 = rm.registerNode("host1:1234", nodeMemory);
        rm.sendNodeStarted(nm1);
        nm1.nodeHeartbeat(true);
        rm.waitForState(nm1.getNodeId(), RUNNING);
        Integer decommissioningTimeout = 600;
        rm.sendNodeGracefulDecommission(nm1, decommissioningTimeout);
        rm.waitForState(nm1.getNodeId(), DECOMMISSIONING);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Make call
        List<NodeReport> nodeReports = client.getClusterNodes(GetClusterNodesRequest.newInstance(EnumSet.of(DECOMMISSIONING))).getNodeReports();
        Assert.assertEquals(1, nodeReports.size());
        NodeReport nr = nodeReports.iterator().next();
        Assert.assertEquals(decommissioningTimeout, nr.getDecommissioningTimeout());
        Assert.assertNull(nr.getNodeUpdateType());
        rpc.stopProxy(client, conf);
        close();
    }

    @Test
    public void testGetClusterNodes() throws Exception {
        MockRM rm = new MockRM() {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        RMNodeLabelsManager labelsMgr = getRMContext().getNodeLabelManager();
        labelsMgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        // Add a healthy node with label = x
        MockNM node = rm.registerNode("host1:1234", 1024);
        Map<NodeId, Set<String>> map = new HashMap<NodeId, Set<String>>();
        map.put(node.getNodeId(), ImmutableSet.of("x"));
        labelsMgr.replaceLabelsOnNode(map);
        rm.sendNodeStarted(node);
        node.nodeHeartbeat(true);
        // Add and lose a node with label = y
        MockNM lostNode = rm.registerNode("host2:1235", 1024);
        rm.sendNodeStarted(lostNode);
        lostNode.nodeHeartbeat(true);
        rm.waitForState(lostNode.getNodeId(), RUNNING);
        rm.sendNodeLost(lostNode);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Make call
        GetClusterNodesRequest request = GetClusterNodesRequest.newInstance(EnumSet.of(RUNNING));
        List<NodeReport> nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals(1, nodeReports.size());
        Assert.assertNotSame("Node is expected to be healthy!", UNHEALTHY, nodeReports.get(0).getNodeState());
        // Check node's label = x
        Assert.assertTrue(nodeReports.get(0).getNodeLabels().contains("x"));
        Assert.assertNull(nodeReports.get(0).getDecommissioningTimeout());
        Assert.assertNull(nodeReports.get(0).getNodeUpdateType());
        // Now make the node unhealthy.
        node.nodeHeartbeat(false);
        rm.waitForState(node.getNodeId(), UNHEALTHY);
        // Call again
        nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals("Unhealthy nodes should not show up by default", 0, nodeReports.size());
        // Change label of host1 to y
        map = new HashMap<NodeId, Set<String>>();
        map.put(node.getNodeId(), ImmutableSet.of("y"));
        labelsMgr.replaceLabelsOnNode(map);
        // Now query for UNHEALTHY nodes
        request = GetClusterNodesRequest.newInstance(EnumSet.of(UNHEALTHY));
        nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals(1, nodeReports.size());
        Assert.assertEquals("Node is expected to be unhealthy!", UNHEALTHY, nodeReports.get(0).getNodeState());
        Assert.assertTrue(nodeReports.get(0).getNodeLabels().contains("y"));
        Assert.assertNull(nodeReports.get(0).getDecommissioningTimeout());
        Assert.assertNull(nodeReports.get(0).getNodeUpdateType());
        // Remove labels of host1
        map = new HashMap<NodeId, Set<String>>();
        map.put(node.getNodeId(), ImmutableSet.of("y"));
        labelsMgr.removeLabelsFromNode(map);
        // Query all states should return all nodes
        rm.registerNode("host3:1236", 1024);
        request = GetClusterNodesRequest.newInstance(EnumSet.allOf(NodeState.class));
        nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals(3, nodeReports.size());
        // All host1-3's label should be empty (instead of null)
        for (NodeReport report : nodeReports) {
            Assert.assertTrue((((report.getNodeLabels()) != null) && (report.getNodeLabels().isEmpty())));
            Assert.assertNull(report.getDecommissioningTimeout());
            Assert.assertNull(report.getNodeUpdateType());
        }
        rpc.stopProxy(client, conf);
        close();
    }

    @Test
    public void testNonExistingApplicationReport() throws YarnException {
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getRMApps()).thenReturn(new ConcurrentHashMap<ApplicationId, RMApp>());
        ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, null);
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        GetApplicationReportRequest request = recordFactory.newRecordInstance(GetApplicationReportRequest.class);
        request.setApplicationId(ApplicationId.newInstance(0, 0));
        try {
            rmService.getApplicationReport(request);
            Assert.fail();
        } catch (ApplicationNotFoundException ex) {
            Assert.assertEquals(ex.getMessage(), ((("Application with id '" + (request.getApplicationId())) + "' doesn't exist in RM. Please check that the ") + "job submission was successful."));
        }
    }

    @Test
    public void testGetApplicationReport() throws Exception {
        ResourceScheduler scheduler = Mockito.mock(ResourceScheduler.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        ApplicationId appId1 = TestClientRMService.getApplicationId(1);
        ApplicationACLsManager mockAclsManager = Mockito.mock(ApplicationACLsManager.class);
        Mockito.when(mockAclsManager.checkAccess(UserGroupInformation.getCurrentUser(), VIEW_APP, null, appId1)).thenReturn(true);
        ClientRMService rmService = new ClientRMService(rmContext, scheduler, null, mockAclsManager, null, null);
        try {
            RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
            GetApplicationReportRequest request = recordFactory.newRecordInstance(GetApplicationReportRequest.class);
            request.setApplicationId(appId1);
            GetApplicationReportResponse response = rmService.getApplicationReport(request);
            ApplicationReport report = response.getApplicationReport();
            ApplicationResourceUsageReport usageReport = report.getApplicationResourceUsageReport();
            Assert.assertEquals(10, usageReport.getMemorySeconds());
            Assert.assertEquals(3, usageReport.getVcoreSeconds());
            Assert.assertEquals("<Not set>", report.getAmNodeLabelExpression());
            Assert.assertEquals("<Not set>", report.getAppNodeLabelExpression());
            // if application has am node label set to blank
            ApplicationId appId2 = TestClientRMService.getApplicationId(2);
            Mockito.when(mockAclsManager.checkAccess(UserGroupInformation.getCurrentUser(), VIEW_APP, null, appId2)).thenReturn(true);
            request.setApplicationId(appId2);
            response = rmService.getApplicationReport(request);
            report = response.getApplicationReport();
            Assert.assertEquals(DEFAULT_NODE_LABEL_PARTITION, report.getAmNodeLabelExpression());
            Assert.assertEquals(NODE_LABEL_EXPRESSION_NOT_SET, report.getAppNodeLabelExpression());
            // if application has am node label set to blank
            ApplicationId appId3 = TestClientRMService.getApplicationId(3);
            Mockito.when(mockAclsManager.checkAccess(UserGroupInformation.getCurrentUser(), VIEW_APP, null, appId3)).thenReturn(true);
            request.setApplicationId(appId3);
            response = rmService.getApplicationReport(request);
            report = response.getApplicationReport();
            Assert.assertEquals("high-mem", report.getAmNodeLabelExpression());
            Assert.assertEquals("high-mem", report.getAppNodeLabelExpression());
            // if application id is null
            GetApplicationReportRequest invalidRequest = recordFactory.newRecordInstance(GetApplicationReportRequest.class);
            invalidRequest.setApplicationId(null);
            try {
                rmService.getApplicationReport(invalidRequest);
            } catch (YarnException e) {
                // rmService should return a ApplicationNotFoundException
                // when a null application id is provided
                Assert.assertTrue((e instanceof ApplicationNotFoundException));
            }
        } finally {
            rmService.close();
        }
    }

    @Test
    public void testGetApplicationAttemptReport() throws IOException, YarnException {
        ClientRMService rmService = createRMService();
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        GetApplicationAttemptReportRequest request = recordFactory.newRecordInstance(GetApplicationAttemptReportRequest.class);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(123456, 1), 1);
        request.setApplicationAttemptId(attemptId);
        try {
            GetApplicationAttemptReportResponse response = rmService.getApplicationAttemptReport(request);
            Assert.assertEquals(attemptId, response.getApplicationAttemptReport().getApplicationAttemptId());
        } catch (ApplicationNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetApplicationResourceUsageReportDummy() throws IOException, YarnException {
        ApplicationAttemptId attemptId = TestClientRMService.getApplicationAttemptId(1);
        ResourceScheduler scheduler = TestClientRMService.mockResourceScheduler();
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        Mockito.when(rmContext.getDispatcher().getEventHandler()).thenReturn(new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            public void handle(Event event) {
            }
        });
        ApplicationSubmissionContext asContext = Mockito.mock(ApplicationSubmissionContext.class);
        YarnConfiguration config = new YarnConfiguration();
        RMAppAttemptImpl rmAppAttemptImpl = new RMAppAttemptImpl(attemptId, rmContext, scheduler, null, asContext, config, null, null);
        ApplicationResourceUsageReport report = rmAppAttemptImpl.getApplicationResourceUsageReport();
        Assert.assertEquals(report, DUMMY_APPLICATION_RESOURCE_USAGE_REPORT);
    }

    @Test
    public void testGetApplicationAttempts() throws IOException, YarnException {
        ClientRMService rmService = createRMService();
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        GetApplicationAttemptsRequest request = recordFactory.newRecordInstance(GetApplicationAttemptsRequest.class);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(123456, 1), 1);
        request.setApplicationId(ApplicationId.newInstance(123456, 1));
        try {
            GetApplicationAttemptsResponse response = rmService.getApplicationAttempts(request);
            Assert.assertEquals(1, response.getApplicationAttemptList().size());
            Assert.assertEquals(attemptId, response.getApplicationAttemptList().get(0).getApplicationAttemptId());
        } catch (ApplicationNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetContainerReport() throws IOException, YarnException {
        ClientRMService rmService = createRMService();
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        GetContainerReportRequest request = recordFactory.newRecordInstance(GetContainerReportRequest.class);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(123456, 1), 1);
        ContainerId containerId = ContainerId.newContainerId(attemptId, 1);
        request.setContainerId(containerId);
        try {
            GetContainerReportResponse response = rmService.getContainerReport(request);
            Assert.assertEquals(containerId, response.getContainerReport().getContainerId());
        } catch (ApplicationNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetContainers() throws IOException, YarnException {
        ClientRMService rmService = createRMService();
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        GetContainersRequest request = recordFactory.newRecordInstance(GetContainersRequest.class);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(123456, 1), 1);
        ContainerId containerId = ContainerId.newContainerId(attemptId, 1);
        request.setApplicationAttemptId(attemptId);
        try {
            GetContainersResponse response = rmService.getContainers(request);
            Assert.assertEquals(containerId, response.getContainerList().get(0).getContainerId());
        } catch (ApplicationNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testForceKillNonExistingApplication() throws YarnException {
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getRMApps()).thenReturn(new ConcurrentHashMap<ApplicationId, RMApp>());
        ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, null);
        ApplicationId applicationId = BuilderUtils.newApplicationId(System.currentTimeMillis(), 0);
        KillApplicationRequest request = KillApplicationRequest.newInstance(applicationId);
        try {
            rmService.forceKillApplication(request);
            Assert.fail();
        } catch (ApplicationNotFoundException ex) {
            Assert.assertEquals(ex.getMessage(), (("Trying to kill an absent " + "application ") + (request.getApplicationId())));
        }
    }

    @Test
    public void testForceKillApplication() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(MockRM.ENABLE_WEBAPP, true);
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        start();
        ClientRMService rmService = rm.getClientRMService();
        GetApplicationsRequest getRequest = GetApplicationsRequest.newInstance(EnumSet.of(KILLED));
        RMApp app1 = rm.submitApp(1024);
        RMApp app2 = rm.submitApp(1024, true);
        Assert.assertEquals("Incorrect number of apps in the RM", 0, rmService.getApplications(getRequest).getApplicationList().size());
        KillApplicationRequest killRequest1 = KillApplicationRequest.newInstance(app1.getApplicationId());
        String diagnostic = "message1";
        killRequest1.setDiagnostics(diagnostic);
        KillApplicationRequest killRequest2 = KillApplicationRequest.newInstance(app2.getApplicationId());
        int killAttemptCount = 0;
        for (int i = 0; i < 100; i++) {
            KillApplicationResponse killResponse1 = rmService.forceKillApplication(killRequest1);
            killAttemptCount++;
            if (killResponse1.getIsKillCompleted()) {
                break;
            }
            Thread.sleep(10);
        }
        Assert.assertTrue("Kill attempt count should be greater than 1 for managed AMs", (killAttemptCount > 1));
        Assert.assertEquals("Incorrect number of apps in the RM", 1, rmService.getApplications(getRequest).getApplicationList().size());
        Assert.assertTrue("Diagnostic message is incorrect", app1.getDiagnostics().toString().contains(diagnostic));
        KillApplicationResponse killResponse2 = rmService.forceKillApplication(killRequest2);
        Assert.assertTrue("Killing UnmanagedAM should falsely acknowledge true", killResponse2.getIsKillCompleted());
        for (int i = 0; i < 100; i++) {
            if (2 == (rmService.getApplications(getRequest).getApplicationList().size())) {
                break;
            }
            Thread.sleep(10);
        }
        Assert.assertEquals("Incorrect number of apps in the RM", 2, rmService.getApplications(getRequest).getApplicationList().size());
    }

    @Test(expected = ApplicationNotFoundException.class)
    public void testMoveAbsentApplication() throws YarnException {
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getRMApps()).thenReturn(new ConcurrentHashMap<ApplicationId, RMApp>());
        ClientRMService rmService = new ClientRMService(rmContext, null, null, null, null, null);
        ApplicationId applicationId = BuilderUtils.newApplicationId(System.currentTimeMillis(), 0);
        MoveApplicationAcrossQueuesRequest request = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "newqueue");
        rmService.moveApplicationAcrossQueues(request);
    }

    @Test
    public void testMoveApplicationSubmitTargetQueue() throws Exception {
        // move the application as the owner
        ApplicationId applicationId = TestClientRMService.getApplicationId(1);
        UserGroupInformation aclUGI = UserGroupInformation.getCurrentUser();
        QueueACLsManager queueACLsManager = getQueueAclManager("allowed_queue", SUBMIT_APPLICATIONS, aclUGI);
        ApplicationACLsManager appAclsManager = getAppAclManager();
        ClientRMService rmService = createClientRMServiceForMoveApplicationRequest(applicationId, aclUGI.getShortUserName(), appAclsManager, queueACLsManager);
        // move as the owner queue in the acl
        MoveApplicationAcrossQueuesRequest moveAppRequest = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "allowed_queue");
        rmService.moveApplicationAcrossQueues(moveAppRequest);
        // move as the owner queue not in the acl
        moveAppRequest = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "not_allowed");
        try {
            rmService.moveApplicationAcrossQueues(moveAppRequest);
            Assert.fail("The request should fail with an AccessControlException");
        } catch (YarnException rex) {
            Assert.assertTrue("AccessControlException is expected", ((rex.getCause()) instanceof AccessControlException));
        }
        // ACL is owned by "moveuser", move is performed as a different user
        aclUGI = UserGroupInformation.createUserForTesting("moveuser", new String[]{  });
        queueACLsManager = getQueueAclManager("move_queue", SUBMIT_APPLICATIONS, aclUGI);
        appAclsManager = getAppAclManager();
        ClientRMService rmService2 = createClientRMServiceForMoveApplicationRequest(applicationId, aclUGI.getShortUserName(), appAclsManager, queueACLsManager);
        // access to the queue not OK: user not allowed in this queue
        MoveApplicationAcrossQueuesRequest moveAppRequest2 = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "move_queue");
        try {
            rmService2.moveApplicationAcrossQueues(moveAppRequest2);
            Assert.fail("The request should fail with an AccessControlException");
        } catch (YarnException rex) {
            Assert.assertTrue("AccessControlException is expected", ((rex.getCause()) instanceof AccessControlException));
        }
        // execute the move as the acl owner
        // access to the queue OK: user allowed in this queue
        aclUGI.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                return rmService2.moveApplicationAcrossQueues(moveAppRequest2);
            }
        });
    }

    @Test
    public void testMoveApplicationAdminTargetQueue() throws Exception {
        ApplicationId applicationId = TestClientRMService.getApplicationId(1);
        UserGroupInformation aclUGI = UserGroupInformation.getCurrentUser();
        QueueACLsManager queueAclsManager = getQueueAclManager("allowed_queue", ADMINISTER_QUEUE, aclUGI);
        ApplicationACLsManager appAclsManager = getAppAclManager();
        ClientRMService rmService = createClientRMServiceForMoveApplicationRequest(applicationId, aclUGI.getShortUserName(), appAclsManager, queueAclsManager);
        // user is admin move to queue in acl
        MoveApplicationAcrossQueuesRequest moveAppRequest = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "allowed_queue");
        rmService.moveApplicationAcrossQueues(moveAppRequest);
        // user is admin move to queue not in acl
        moveAppRequest = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "not_allowed");
        try {
            rmService.moveApplicationAcrossQueues(moveAppRequest);
            Assert.fail("The request should fail with an AccessControlException");
        } catch (YarnException rex) {
            Assert.assertTrue("AccessControlException is expected", ((rex.getCause()) instanceof AccessControlException));
        }
        // ACL is owned by "moveuser", move is performed as a different user
        aclUGI = UserGroupInformation.createUserForTesting("moveuser", new String[]{  });
        queueAclsManager = getQueueAclManager("move_queue", ADMINISTER_QUEUE, aclUGI);
        appAclsManager = getAppAclManager();
        ClientRMService rmService2 = createClientRMServiceForMoveApplicationRequest(applicationId, aclUGI.getShortUserName(), appAclsManager, queueAclsManager);
        // no access to this queue
        MoveApplicationAcrossQueuesRequest moveAppRequest2 = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "move_queue");
        try {
            rmService2.moveApplicationAcrossQueues(moveAppRequest2);
            Assert.fail("The request should fail with an AccessControlException");
        } catch (YarnException rex) {
            Assert.assertTrue("AccessControlException is expected", ((rex.getCause()) instanceof AccessControlException));
        }
        // execute the move as the acl owner
        // access to the queue OK: user allowed in this queue
        aclUGI.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                return rmService2.moveApplicationAcrossQueues(moveAppRequest2);
            }
        });
    }

    @Test(expected = YarnException.class)
    public void testNonExistingQueue() throws Exception {
        ApplicationId applicationId = TestClientRMService.getApplicationId(1);
        UserGroupInformation aclUGI = UserGroupInformation.getCurrentUser();
        QueueACLsManager queueAclsManager = getQueueAclManager();
        ApplicationACLsManager appAclsManager = getAppAclManager();
        ClientRMService rmService = createClientRMServiceForMoveApplicationRequest(applicationId, aclUGI.getShortUserName(), appAclsManager, queueAclsManager);
        MoveApplicationAcrossQueuesRequest moveAppRequest = MoveApplicationAcrossQueuesRequest.newInstance(applicationId, "unknown_queue");
        rmService.moveApplicationAcrossQueues(moveAppRequest);
    }

    @Test
    public void testGetQueueInfo() throws Exception {
        ResourceScheduler scheduler = Mockito.mock(ResourceScheduler.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        ApplicationACLsManager mockAclsManager = Mockito.mock(ApplicationACLsManager.class);
        QueueACLsManager mockQueueACLsManager = Mockito.mock(QueueACLsManager.class);
        Mockito.when(mockQueueACLsManager.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(QueueACL.class), ArgumentMatchers.any(RMApp.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(mockAclsManager.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(ApplicationAccessType.class), ArgumentMatchers.any(), ArgumentMatchers.any(ApplicationId.class))).thenReturn(true);
        ClientRMService rmService = new ClientRMService(rmContext, scheduler, null, mockAclsManager, mockQueueACLsManager, null);
        GetQueueInfoRequest request = recordFactory.newRecordInstance(GetQueueInfoRequest.class);
        request.setQueueName("testqueue");
        request.setIncludeApplications(true);
        GetQueueInfoResponse queueInfo = rmService.getQueueInfo(request);
        List<ApplicationReport> applications = queueInfo.getQueueInfo().getApplications();
        Assert.assertEquals(2, applications.size());
        Map<String, QueueConfigurations> queueConfigsByPartition = queueInfo.getQueueInfo().getQueueConfigurations();
        Assert.assertEquals(1, queueConfigsByPartition.size());
        Assert.assertTrue(queueConfigsByPartition.containsKey("*"));
        QueueConfigurations queueConfigs = queueConfigsByPartition.get("*");
        Assert.assertEquals(0.5F, queueConfigs.getCapacity(), 1.0E-4F);
        Assert.assertEquals(0.1F, queueConfigs.getAbsoluteCapacity(), 1.0E-4F);
        Assert.assertEquals(1.0F, queueConfigs.getMaxCapacity(), 1.0E-4F);
        Assert.assertEquals(1.0F, queueConfigs.getAbsoluteMaxCapacity(), 1.0E-4F);
        Assert.assertEquals(0.2F, queueConfigs.getMaxAMPercentage(), 1.0E-4F);
        request.setQueueName("nonexistentqueue");
        request.setIncludeApplications(true);
        // should not throw exception on nonexistent queue
        queueInfo = rmService.getQueueInfo(request);
        // Case where user does not have application access
        ApplicationACLsManager mockAclsManager1 = Mockito.mock(ApplicationACLsManager.class);
        QueueACLsManager mockQueueACLsManager1 = Mockito.mock(QueueACLsManager.class);
        Mockito.when(mockQueueACLsManager1.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(QueueACL.class), ArgumentMatchers.any(RMApp.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockAclsManager1.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(ApplicationAccessType.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(ApplicationId.class))).thenReturn(false);
        ClientRMService rmService1 = new ClientRMService(rmContext, scheduler, null, mockAclsManager1, mockQueueACLsManager1, null);
        request.setQueueName("testqueue");
        request.setIncludeApplications(true);
        GetQueueInfoResponse queueInfo1 = rmService1.getQueueInfo(request);
        List<ApplicationReport> applications1 = queueInfo1.getQueueInfo().getApplications();
        Assert.assertEquals(0, applications1.size());
    }

    @Test(timeout = 30000)
    @SuppressWarnings("rawtypes")
    public void testAppSubmit() throws Exception {
        ResourceScheduler scheduler = TestClientRMService.mockResourceScheduler();
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        RMStateStore stateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(stateStore);
        RMAppManager appManager = new RMAppManager(rmContext, scheduler, null, Mockito.mock(ApplicationACLsManager.class), new Configuration());
        Mockito.when(rmContext.getDispatcher().getEventHandler()).thenReturn(new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            public void handle(Event event) {
            }
        });
        Mockito.doReturn(Mockito.mock(RMTimelineCollectorManager.class)).when(rmContext).getRMTimelineCollectorManager();
        ApplicationId appId1 = TestClientRMService.getApplicationId(100);
        ApplicationACLsManager mockAclsManager = Mockito.mock(ApplicationACLsManager.class);
        Mockito.when(mockAclsManager.checkAccess(UserGroupInformation.getCurrentUser(), VIEW_APP, null, appId1)).thenReturn(true);
        QueueACLsManager mockQueueACLsManager = Mockito.mock(QueueACLsManager.class);
        Mockito.when(mockQueueACLsManager.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(QueueACL.class), ArgumentMatchers.any(RMApp.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any())).thenReturn(true);
        ClientRMService rmService = new ClientRMService(rmContext, scheduler, appManager, mockAclsManager, mockQueueACLsManager, null);
        rmService.init(new Configuration());
        // without name and queue
        SubmitApplicationRequest submitRequest1 = mockSubmitAppRequest(appId1, null, null);
        try {
            rmService.submitApplication(submitRequest1);
        } catch (YarnException e) {
            Assert.fail("Exception is not expected.");
        }
        RMApp app1 = rmContext.getRMApps().get(appId1);
        Assert.assertNotNull("app doesn't exist", app1);
        Assert.assertEquals("app name doesn't match", DEFAULT_APPLICATION_NAME, app1.getName());
        Assert.assertEquals("app queue doesn't match", DEFAULT_QUEUE_NAME, app1.getQueue());
        // with name and queue
        String name = MockApps.newAppName();
        String queue = MockApps.newQueue();
        ApplicationId appId2 = TestClientRMService.getApplicationId(101);
        SubmitApplicationRequest submitRequest2 = mockSubmitAppRequest(appId2, name, queue);
        submitRequest2.getApplicationSubmissionContext().setApplicationType("matchType");
        try {
            rmService.submitApplication(submitRequest2);
        } catch (YarnException e) {
            Assert.fail("Exception is not expected.");
        }
        RMApp app2 = rmContext.getRMApps().get(appId2);
        Assert.assertNotNull("app doesn't exist", app2);
        Assert.assertEquals("app name doesn't match", name, app2.getName());
        Assert.assertEquals("app queue doesn't match", queue, app2.getQueue());
        // duplicate appId
        try {
            rmService.submitApplication(submitRequest2);
        } catch (YarnException e) {
            Assert.fail("Exception is not expected.");
        }
        GetApplicationsRequest getAllAppsRequest = GetApplicationsRequest.newInstance(new HashSet<String>());
        GetApplicationsResponse getAllApplicationsResponse = rmService.getApplications(getAllAppsRequest);
        Assert.assertEquals(5, getAllApplicationsResponse.getApplicationList().size());
        Set<String> appTypes = new HashSet<String>();
        appTypes.add("matchType");
        getAllAppsRequest = GetApplicationsRequest.newInstance(appTypes);
        getAllApplicationsResponse = rmService.getApplications(getAllAppsRequest);
        Assert.assertEquals(1, getAllApplicationsResponse.getApplicationList().size());
        Assert.assertEquals(appId2, getAllApplicationsResponse.getApplicationList().get(0).getApplicationId());
        // Test query with uppercase appType also works
        appTypes = new HashSet<String>();
        appTypes.add("MATCHTYPE");
        getAllAppsRequest = GetApplicationsRequest.newInstance(appTypes);
        getAllApplicationsResponse = rmService.getApplications(getAllAppsRequest);
        Assert.assertEquals(1, getAllApplicationsResponse.getApplicationList().size());
        Assert.assertEquals(appId2, getAllApplicationsResponse.getApplicationList().get(0).getApplicationId());
    }

    @Test
    public void testGetApplications() throws Exception {
        /**
         * 1. Submit 3 applications alternately in two queues
         * 2. Test each of the filters
         */
        // Basic setup
        ResourceScheduler scheduler = TestClientRMService.mockResourceScheduler();
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        RMStateStore stateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(stateStore);
        Mockito.doReturn(Mockito.mock(RMTimelineCollectorManager.class)).when(rmContext).getRMTimelineCollectorManager();
        RMAppManager appManager = new RMAppManager(rmContext, scheduler, null, Mockito.mock(ApplicationACLsManager.class), new Configuration());
        Mockito.when(rmContext.getDispatcher().getEventHandler()).thenReturn(new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            public void handle(Event event) {
            }
        });
        ApplicationACLsManager mockAclsManager = Mockito.mock(ApplicationACLsManager.class);
        QueueACLsManager mockQueueACLsManager = Mockito.mock(QueueACLsManager.class);
        Mockito.when(mockQueueACLsManager.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(QueueACL.class), ArgumentMatchers.any(RMApp.class), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        ClientRMService rmService = new ClientRMService(rmContext, scheduler, appManager, mockAclsManager, mockQueueACLsManager, null);
        rmService.init(new Configuration());
        // Initialize appnames and queues
        String[] queues = new String[]{ TestClientRMService.QUEUE_1, TestClientRMService.QUEUE_2 };
        String[] appNames = new String[]{ MockApps.newAppName(), MockApps.newAppName(), MockApps.newAppName() };
        ApplicationId[] appIds = new ApplicationId[]{ TestClientRMService.getApplicationId(101), TestClientRMService.getApplicationId(102), TestClientRMService.getApplicationId(103) };
        List<String> tags = Arrays.asList("Tag1", "Tag2", "Tag3");
        long[] submitTimeMillis = new long[3];
        // Submit applications
        for (int i = 0; i < (appIds.length); i++) {
            ApplicationId appId = appIds[i];
            Mockito.when(mockAclsManager.checkAccess(UserGroupInformation.getCurrentUser(), VIEW_APP, null, appId)).thenReturn(true);
            SubmitApplicationRequest submitRequest = mockSubmitAppRequest(appId, appNames[i], queues[(i % (queues.length))], new HashSet<String>(tags.subList(0, (i + 1))));
            // make sure each app is submitted at a different time
            Thread.sleep(1);
            rmService.submitApplication(submitRequest);
            submitTimeMillis[i] = rmService.getApplicationReport(GetApplicationReportRequest.newInstance(appId)).getApplicationReport().getStartTime();
        }
        // Test different cases of ClientRMService#getApplications()
        GetApplicationsRequest request = GetApplicationsRequest.newInstance();
        Assert.assertEquals("Incorrect total number of apps", 6, rmService.getApplications(request).getApplicationList().size());
        // Check limit
        request.setLimit(1L);
        Assert.assertEquals("Failed to limit applications", 1, rmService.getApplications(request).getApplicationList().size());
        // Check start range
        request = GetApplicationsRequest.newInstance();
        request.setStartRange(((submitTimeMillis[0]) + 1), System.currentTimeMillis());
        // 2 applications are submitted after first timeMills
        Assert.assertEquals("Incorrect number of matching start range", 2, rmService.getApplications(request).getApplicationList().size());
        // 1 application is submitted after the second timeMills
        request.setStartRange(((submitTimeMillis[1]) + 1), System.currentTimeMillis());
        Assert.assertEquals("Incorrect number of matching start range", 1, rmService.getApplications(request).getApplicationList().size());
        // no application is submitted after the third timeMills
        request.setStartRange(((submitTimeMillis[2]) + 1), System.currentTimeMillis());
        Assert.assertEquals("Incorrect number of matching start range", 0, rmService.getApplications(request).getApplicationList().size());
        // Check queue
        request = GetApplicationsRequest.newInstance();
        Set<String> queueSet = new HashSet<String>();
        request.setQueues(queueSet);
        queueSet.add(queues[0]);
        Assert.assertEquals("Incorrect number of applications in queue", 2, rmService.getApplications(request).getApplicationList().size());
        Assert.assertEquals("Incorrect number of applications in queue", 2, rmService.getApplications(request).getApplicationList().size());
        queueSet.add(queues[1]);
        Assert.assertEquals("Incorrect number of applications in queue", 3, rmService.getApplications(request).getApplicationList().size());
        // Check user
        request = GetApplicationsRequest.newInstance();
        Set<String> userSet = new HashSet<String>();
        request.setUsers(userSet);
        userSet.add("random-user-name");
        Assert.assertEquals("Incorrect number of applications for user", 0, rmService.getApplications(request).getApplicationList().size());
        userSet.add(UserGroupInformation.getCurrentUser().getShortUserName());
        Assert.assertEquals("Incorrect number of applications for user", 3, rmService.getApplications(request).getApplicationList().size());
        rmService.setDisplayPerUserApps(true);
        userSet.clear();
        Assert.assertEquals("Incorrect number of applications for user", 6, rmService.getApplications(request).getApplicationList().size());
        rmService.setDisplayPerUserApps(false);
        // Check tags
        request = GetApplicationsRequest.newInstance(ALL, null, null, null, null, null, null, null, null);
        Set<String> tagSet = new HashSet<String>();
        request.setApplicationTags(tagSet);
        Assert.assertEquals("Incorrect number of matching tags", 6, rmService.getApplications(request).getApplicationList().size());
        tagSet = Sets.newHashSet(tags.get(0));
        request.setApplicationTags(tagSet);
        Assert.assertEquals("Incorrect number of matching tags", 3, rmService.getApplications(request).getApplicationList().size());
        tagSet = Sets.newHashSet(tags.get(1));
        request.setApplicationTags(tagSet);
        Assert.assertEquals("Incorrect number of matching tags", 2, rmService.getApplications(request).getApplicationList().size());
        tagSet = Sets.newHashSet(tags.get(2));
        request.setApplicationTags(tagSet);
        Assert.assertEquals("Incorrect number of matching tags", 1, rmService.getApplications(request).getApplicationList().size());
        // Check scope
        request = GetApplicationsRequest.newInstance(VIEWABLE);
        Assert.assertEquals("Incorrect number of applications for the scope", 6, rmService.getApplications(request).getApplicationList().size());
        request = GetApplicationsRequest.newInstance(OWN);
        Assert.assertEquals("Incorrect number of applications for the scope", 3, rmService.getApplications(request).getApplicationList().size());
    }

    @Test(timeout = 4000)
    public void testConcurrentAppSubmit() throws IOException, InterruptedException, BrokenBarrierException, YarnException {
        ResourceScheduler scheduler = TestClientRMService.mockResourceScheduler();
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        RMStateStore stateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(stateStore);
        RMAppManager appManager = new RMAppManager(rmContext, scheduler, null, Mockito.mock(ApplicationACLsManager.class), new Configuration());
        final ApplicationId appId1 = TestClientRMService.getApplicationId(100);
        final ApplicationId appId2 = TestClientRMService.getApplicationId(101);
        final SubmitApplicationRequest submitRequest1 = mockSubmitAppRequest(appId1, null, null);
        final SubmitApplicationRequest submitRequest2 = mockSubmitAppRequest(appId2, null, null);
        final CyclicBarrier startBarrier = new CyclicBarrier(2);
        final CyclicBarrier endBarrier = new CyclicBarrier(2);
        org.apache.hadoop.yarn.event.EventHandler<Event> eventHandler = new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            @Override
            public void handle(Event rawEvent) {
                if (rawEvent instanceof RMAppEvent) {
                    RMAppEvent event = ((RMAppEvent) (rawEvent));
                    if (event.getApplicationId().equals(appId1)) {
                        try {
                            startBarrier.await();
                            endBarrier.await();
                        } catch (BrokenBarrierException e) {
                            TestClientRMService.LOG.warn("Broken Barrier", e);
                        } catch (InterruptedException e) {
                            TestClientRMService.LOG.warn("Interrupted while awaiting barriers", e);
                        }
                    }
                }
            }
        };
        Mockito.when(rmContext.getDispatcher().getEventHandler()).thenReturn(eventHandler);
        Mockito.doReturn(Mockito.mock(RMTimelineCollectorManager.class)).when(rmContext).getRMTimelineCollectorManager();
        final ClientRMService rmService = new ClientRMService(rmContext, scheduler, appManager, null, null, null);
        rmService.init(new Configuration());
        // submit an app and wait for it to block while in app submission
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    rmService.submitApplication(submitRequest1);
                } catch (YarnException | IOException e) {
                }
            }
        };
        t.start();
        // submit another app, so go through while the first app is blocked
        startBarrier.await();
        rmService.submitApplication(submitRequest2);
        endBarrier.await();
        t.join();
    }

    @Test
    public void testCreateReservation() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        // Submit the reservation again with the same request and make sure it
        // passes.
        try {
            clientService.submitReservation(sRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // Submit the reservation with the same reservation id but different
        // reservation definition, and ensure YarnException is thrown.
        arrival = clock.getTime();
        ReservationDefinition rDef = sRequest.getReservationDefinition();
        rDef.setArrival((arrival + duration));
        sRequest.setReservationDefinition(rDef);
        try {
            clientService.submitReservation(sRequest);
            Assert.fail(("Reservation submission should fail if a duplicate " + ("reservation id is used, but the reservation definition has been " + "updated.")));
        } catch (Exception e) {
            Assert.assertTrue((e instanceof YarnException));
        }
        rm.stop();
    }

    @Test
    public void testUpdateReservation() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        ReservationDefinition rDef = sRequest.getReservationDefinition();
        ReservationRequest rr = rDef.getReservationRequests().getReservationResources().get(0);
        ReservationId reservationID = sRequest.getReservationId();
        rr.setNumContainers(5);
        arrival = clock.getTime();
        duration = 30000;
        deadline = ((long) (arrival + (1.05 * duration)));
        rr.setDuration(duration);
        rDef.setArrival(arrival);
        rDef.setDeadline(deadline);
        ReservationUpdateRequest uRequest = ReservationUpdateRequest.newInstance(rDef, reservationID);
        ReservationUpdateResponse uResponse = null;
        try {
            uResponse = clientService.updateReservation(uRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(uResponse);
        System.out.println(("Update reservation response: " + uResponse));
        rm.stop();
    }

    @Test
    public void testListReservationsByReservationId() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        ReservationId reservationID = sRequest.getReservationId();
        ReservationListResponse response = null;
        ReservationListRequest request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, reservationID.toString(), (-1), (-1), false);
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getReservationAllocationState().size());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getResourceAllocationRequests().size(), 0);
        rm.stop();
    }

    @Test
    public void testListReservationsByTimeInterval() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        // List reservations, search by a point in time within the reservation
        // range.
        arrival = clock.getTime();
        ReservationId reservationID = sRequest.getReservationId();
        ReservationListRequest request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", (arrival + (duration / 2)), (arrival + (duration / 2)), true);
        ReservationListResponse response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getReservationAllocationState().size());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
        // List reservations, search by time within reservation interval.
        request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", 1, Long.MAX_VALUE, true);
        response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getReservationAllocationState().size());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
        // Verify that the full resource allocations exist.
        Assert.assertTrue(((response.getReservationAllocationState().get(0).getResourceAllocationRequests().size()) > 0));
        // Verify that the full RDL is returned.
        ReservationRequests reservationRequests = response.getReservationAllocationState().get(0).getReservationDefinition().getReservationRequests();
        Assert.assertEquals("R_ALL", reservationRequests.getInterpreter().toString());
        Assert.assertTrue(((reservationRequests.getReservationResources().get(0).getDuration()) == duration));
        rm.stop();
    }

    @Test
    public void testListReservationsByInvalidTimeInterval() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        // List reservations, search by invalid end time == -1.
        ReservationListRequest request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", 1, (-1), true);
        ReservationListResponse response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getReservationAllocationState().size());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), sRequest.getReservationId().getId());
        // List reservations, search by invalid end time < -1.
        request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", 1, (-10), true);
        response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getReservationAllocationState().size());
        Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), sRequest.getReservationId().getId());
        rm.stop();
    }

    @Test
    public void testListReservationsByTimeIntervalContainingNoReservations() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        // List reservations, search by very large start time.
        ReservationListRequest request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", Long.MAX_VALUE, (-1), false);
        ReservationListResponse response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // Ensure all reservations are filtered out.
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getReservationAllocationState().size(), 0);
        duration = 30000;
        deadline = sRequest.getReservationDefinition().getDeadline();
        // List reservations, search by start time after the reservation
        // end time.
        request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", (deadline + duration), (deadline + (2 * duration)), false);
        response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // Ensure all reservations are filtered out.
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getReservationAllocationState().size(), 0);
        arrival = clock.getTime();
        // List reservations, search by end time before the reservation start
        // time.
        request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", 0, (arrival - duration), false);
        response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // Ensure all reservations are filtered out.
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getReservationAllocationState().size(), 0);
        // List reservations, search by very small end time.
        request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, "", 0, 1, false);
        response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // Ensure all reservations are filtered out.
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getReservationAllocationState().size(), 0);
        rm.stop();
    }

    @Test
    public void testReservationDelete() {
        ResourceManager rm = setupResourceManager();
        ClientRMService clientService = rm.getClientRMService();
        Clock clock = new UTCClock();
        long arrival = clock.getTime();
        long duration = 60000;
        long deadline = ((long) (arrival + (1.05 * duration)));
        ReservationSubmissionRequest sRequest = submitReservationTestHelper(clientService, arrival, deadline, duration);
        ReservationId reservationID = sRequest.getReservationId();
        // Delete the reservation
        ReservationDeleteRequest dRequest = ReservationDeleteRequest.newInstance(reservationID);
        ReservationDeleteResponse dResponse = null;
        try {
            dResponse = clientService.deleteReservation(dRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(dResponse);
        System.out.println(("Delete reservation response: " + dResponse));
        // List reservations, search by non-existent reservationID
        ReservationListRequest request = ReservationListRequest.newInstance(ReservationSystemTestUtil.reservationQ, reservationID.toString(), (-1), (-1), false);
        ReservationListResponse response = null;
        try {
            response = clientService.listReservations(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.getReservationAllocationState().size());
        rm.stop();
    }

    @Test
    public void testGetNodeLabels() throws Exception {
        MockRM rm = new MockRM() {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        NodeLabel labelX = NodeLabel.newInstance("x", false);
        NodeLabel labelY = NodeLabel.newInstance("y");
        RMNodeLabelsManager labelsMgr = getRMContext().getNodeLabelManager();
        labelsMgr.addToCluserNodeLabels(ImmutableSet.of(labelX, labelY));
        NodeId node1 = NodeId.newInstance("host1", 1234);
        NodeId node2 = NodeId.newInstance("host2", 1234);
        Map<NodeId, Set<String>> map = new HashMap<NodeId, Set<String>>();
        map.put(node1, ImmutableSet.of("x"));
        map.put(node2, ImmutableSet.of("y"));
        labelsMgr.replaceLabelsOnNode(map);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Get node labels collection
        GetClusterNodeLabelsResponse response = client.getClusterNodeLabels(GetClusterNodeLabelsRequest.newInstance());
        Assert.assertTrue(response.getNodeLabelList().containsAll(Arrays.asList(labelX, labelY)));
        // Get node labels mapping
        GetNodesToLabelsResponse response1 = client.getNodeToLabels(GetNodesToLabelsRequest.newInstance());
        Map<NodeId, Set<String>> nodeToLabels = response1.getNodeToLabels();
        Assert.assertTrue(nodeToLabels.keySet().containsAll(Arrays.asList(node1, node2)));
        Assert.assertTrue(nodeToLabels.get(node1).containsAll(Arrays.asList(labelX.getName())));
        Assert.assertTrue(nodeToLabels.get(node2).containsAll(Arrays.asList(labelY.getName())));
        // Below label "x" is not present in the response as exclusivity is true
        Assert.assertFalse(nodeToLabels.get(node1).containsAll(Arrays.asList(NodeLabel.newInstance("x"))));
        rpc.stopProxy(client, conf);
        stop();
    }

    @Test
    public void testGetLabelsToNodes() throws Exception {
        MockRM rm = new MockRM() {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        NodeLabel labelX = NodeLabel.newInstance("x", false);
        NodeLabel labelY = NodeLabel.newInstance("y", false);
        NodeLabel labelZ = NodeLabel.newInstance("z", false);
        RMNodeLabelsManager labelsMgr = getRMContext().getNodeLabelManager();
        labelsMgr.addToCluserNodeLabels(ImmutableSet.of(labelX, labelY, labelZ));
        NodeId node1A = NodeId.newInstance("host1", 1234);
        NodeId node1B = NodeId.newInstance("host1", 5678);
        NodeId node2A = NodeId.newInstance("host2", 1234);
        NodeId node3A = NodeId.newInstance("host3", 1234);
        NodeId node3B = NodeId.newInstance("host3", 5678);
        Map<NodeId, Set<String>> map = new HashMap<NodeId, Set<String>>();
        map.put(node1A, ImmutableSet.of("x"));
        map.put(node1B, ImmutableSet.of("z"));
        map.put(node2A, ImmutableSet.of("y"));
        map.put(node3A, ImmutableSet.of("y"));
        map.put(node3B, ImmutableSet.of("z"));
        labelsMgr.replaceLabelsOnNode(map);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Get node labels collection
        GetClusterNodeLabelsResponse response = client.getClusterNodeLabels(GetClusterNodeLabelsRequest.newInstance());
        Assert.assertTrue(response.getNodeLabelList().containsAll(Arrays.asList(labelX, labelY, labelZ)));
        // Get labels to nodes mapping
        GetLabelsToNodesResponse response1 = client.getLabelsToNodes(GetLabelsToNodesRequest.newInstance());
        Map<String, Set<NodeId>> labelsToNodes = response1.getLabelsToNodes();
        Assert.assertTrue(labelsToNodes.keySet().containsAll(Arrays.asList(labelX.getName(), labelY.getName(), labelZ.getName())));
        Assert.assertTrue(labelsToNodes.get(labelX.getName()).containsAll(Arrays.asList(node1A)));
        Assert.assertTrue(labelsToNodes.get(labelY.getName()).containsAll(Arrays.asList(node2A, node3A)));
        Assert.assertTrue(labelsToNodes.get(labelZ.getName()).containsAll(Arrays.asList(node1B, node3B)));
        // Get labels to nodes mapping for specific labels
        Set<String> setlabels = new HashSet<String>(Arrays.asList(new String[]{ "x", "z" }));
        GetLabelsToNodesResponse response2 = client.getLabelsToNodes(GetLabelsToNodesRequest.newInstance(setlabels));
        labelsToNodes = response2.getLabelsToNodes();
        Assert.assertTrue(labelsToNodes.keySet().containsAll(Arrays.asList(labelX.getName(), labelZ.getName())));
        Assert.assertTrue(labelsToNodes.get(labelX.getName()).containsAll(Arrays.asList(node1A)));
        Assert.assertTrue(labelsToNodes.get(labelZ.getName()).containsAll(Arrays.asList(node1B, node3B)));
        Assert.assertEquals(labelsToNodes.get(labelY.getName()), null);
        rpc.stopProxy(client, conf);
        close();
    }

    @Test(timeout = 120000)
    public void testGetClusterNodeAttributes() throws IOException, YarnException {
        Configuration newConf = NodeAttributeTestUtils.getRandomDirConf(null);
        MockRM rm = new MockRM(newConf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        NodeAttributesManager mgr = getRMContext().getNodeAttributesManager();
        NodeId host1 = NodeId.newInstance("host1", 0);
        NodeId host2 = NodeId.newInstance("host2", 0);
        NodeAttribute gpu = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "GPU", STRING, "nvida");
        NodeAttribute os = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "OS", STRING, "windows64");
        NodeAttribute docker = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "DOCKER", STRING, "docker0");
        Map<String, Set<NodeAttribute>> nodes = new HashMap<>();
        nodes.put(host1.getHost(), ImmutableSet.of(gpu, os));
        nodes.put(host2.getHost(), ImmutableSet.of(docker));
        mgr.addNodeAttributes(nodes);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        GetClusterNodeAttributesRequest request = GetClusterNodeAttributesRequest.newInstance();
        GetClusterNodeAttributesResponse response = client.getClusterNodeAttributes(request);
        Set<NodeAttributeInfo> attributes = response.getNodeAttributes();
        Assert.assertEquals("Size not correct", 3, attributes.size());
        Assert.assertTrue(attributes.contains(NodeAttributeInfo.newInstance(gpu)));
        Assert.assertTrue(attributes.contains(NodeAttributeInfo.newInstance(os)));
        Assert.assertTrue(attributes.contains(NodeAttributeInfo.newInstance(docker)));
        rpc.stopProxy(client, conf);
        close();
    }

    @Test(timeout = 120000)
    public void testGetAttributesToNodes() throws IOException, YarnException {
        Configuration newConf = NodeAttributeTestUtils.getRandomDirConf(null);
        MockRM rm = new MockRM(newConf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        NodeAttributesManager mgr = getRMContext().getNodeAttributesManager();
        String node1 = "host1";
        String node2 = "host2";
        NodeAttribute gpu = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "GPU", STRING, "nvidia");
        NodeAttribute os = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "OS", STRING, "windows64");
        NodeAttribute docker = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "DOCKER", STRING, "docker0");
        NodeAttribute dist = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "VERSION", STRING, "3_0_2");
        Map<String, Set<NodeAttribute>> nodes = new HashMap<>();
        nodes.put(node1, ImmutableSet.of(gpu, os, dist));
        nodes.put(node2, ImmutableSet.of(docker, dist));
        mgr.addNodeAttributes(nodes);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        GetAttributesToNodesRequest request = GetAttributesToNodesRequest.newInstance();
        GetAttributesToNodesResponse response = client.getAttributesToNodes(request);
        Map<NodeAttributeKey, List<NodeToAttributeValue>> attrs = response.getAttributesToNodes();
        Assert.assertEquals(response.getAttributesToNodes().size(), 4);
        Assert.assertEquals(attrs.get(dist.getAttributeKey()).size(), 2);
        Assert.assertEquals(attrs.get(os.getAttributeKey()).size(), 1);
        Assert.assertEquals(attrs.get(gpu.getAttributeKey()).size(), 1);
        Assert.assertTrue(findHostnameAndValInMapping(node1, "3_0_2", attrs.get(dist.getAttributeKey())));
        Assert.assertTrue(findHostnameAndValInMapping(node2, "3_0_2", attrs.get(dist.getAttributeKey())));
        Assert.assertTrue(findHostnameAndValInMapping(node2, "docker0", attrs.get(docker.getAttributeKey())));
        GetAttributesToNodesRequest request2 = GetAttributesToNodesRequest.newInstance(ImmutableSet.of(docker.getAttributeKey()));
        GetAttributesToNodesResponse response2 = client.getAttributesToNodes(request2);
        Map<NodeAttributeKey, List<NodeToAttributeValue>> attrs2 = response2.getAttributesToNodes();
        Assert.assertEquals(attrs2.size(), 1);
        Assert.assertTrue(findHostnameAndValInMapping(node2, "docker0", attrs2.get(docker.getAttributeKey())));
        GetAttributesToNodesRequest request3 = GetAttributesToNodesRequest.newInstance(ImmutableSet.of(docker.getAttributeKey(), os.getAttributeKey()));
        GetAttributesToNodesResponse response3 = client.getAttributesToNodes(request3);
        Map<NodeAttributeKey, List<NodeToAttributeValue>> attrs3 = response3.getAttributesToNodes();
        Assert.assertEquals(attrs3.size(), 2);
        Assert.assertTrue(findHostnameAndValInMapping(node1, "windows64", attrs3.get(os.getAttributeKey())));
        Assert.assertTrue(findHostnameAndValInMapping(node2, "docker0", attrs3.get(docker.getAttributeKey())));
        rpc.stopProxy(client, conf);
        close();
    }

    @Test(timeout = 120000)
    public void testGetNodesToAttributes() throws IOException, YarnException {
        Configuration newConf = NodeAttributeTestUtils.getRandomDirConf(null);
        MockRM rm = new MockRM(newConf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        NodeAttributesManager mgr = getRMContext().getNodeAttributesManager();
        String node1 = "host1";
        String node2 = "host2";
        NodeAttribute gpu = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "GPU", STRING, "nvida");
        NodeAttribute os = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "OS", STRING, "windows64");
        NodeAttribute docker = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "DOCKER", STRING, "docker0");
        NodeAttribute dist = NodeAttribute.newInstance(PREFIX_DISTRIBUTED, "VERSION", STRING, "3_0_2");
        Map<String, Set<NodeAttribute>> nodes = new HashMap<>();
        nodes.put(node1, ImmutableSet.of(gpu, os, dist));
        nodes.put(node2, ImmutableSet.of(docker, dist));
        mgr.addNodeAttributes(nodes);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Specify null for hostnames.
        GetNodesToAttributesRequest request1 = GetNodesToAttributesRequest.newInstance(null);
        GetNodesToAttributesResponse response1 = client.getNodesToAttributes(request1);
        Map<String, Set<NodeAttribute>> hostToAttrs = response1.getNodeToAttributes();
        Assert.assertEquals(2, hostToAttrs.size());
        Assert.assertTrue(hostToAttrs.get(node2).contains(dist));
        Assert.assertTrue(hostToAttrs.get(node2).contains(docker));
        Assert.assertTrue(hostToAttrs.get(node1).contains(dist));
        // Specify particular node
        GetNodesToAttributesRequest request2 = GetNodesToAttributesRequest.newInstance(ImmutableSet.of(node1));
        GetNodesToAttributesResponse response2 = client.getNodesToAttributes(request2);
        hostToAttrs = response2.getNodeToAttributes();
        Assert.assertEquals(1, response2.getNodeToAttributes().size());
        Assert.assertTrue(hostToAttrs.get(node1).contains(dist));
        // Test queury with empty set
        GetNodesToAttributesRequest request3 = GetNodesToAttributesRequest.newInstance(Collections.emptySet());
        GetNodesToAttributesResponse response3 = client.getNodesToAttributes(request3);
        hostToAttrs = response3.getNodeToAttributes();
        Assert.assertEquals(2, hostToAttrs.size());
        Assert.assertTrue(hostToAttrs.get(node2).contains(dist));
        Assert.assertTrue(hostToAttrs.get(node2).contains(docker));
        Assert.assertTrue(hostToAttrs.get(node1).contains(dist));
        // test invalid hostname
        GetNodesToAttributesRequest request4 = GetNodesToAttributesRequest.newInstance(ImmutableSet.of("invalid"));
        GetNodesToAttributesResponse response4 = client.getNodesToAttributes(request4);
        hostToAttrs = response4.getNodeToAttributes();
        Assert.assertEquals(0, hostToAttrs.size());
        rpc.stopProxy(client, conf);
        close();
    }

    @Test(timeout = 120000)
    public void testUpdatePriorityAndKillAppWithZeroClusterResource() throws Exception {
        int maxPriority = 10;
        int appPriority = 5;
        YarnConfiguration conf = new YarnConfiguration();
        Assume.assumeFalse("FairScheduler does not support Application Priorities", conf.get(RM_SCHEDULER).equals(FairScheduler.class.getName()));
        conf.setInt(MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY, maxPriority);
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        start();
        RMApp app1 = rm.submitApp(1024, Priority.newInstance(appPriority));
        ClientRMService rmService = rm.getClientRMService();
        testApplicationPriorityUpdation(rmService, app1, appPriority, appPriority);
        rm.killApp(app1.getApplicationId());
        rm.waitForState(app1.getApplicationId(), RMAppState.KILLED);
        stop();
    }

    @Test(timeout = 120000)
    public void testUpdateApplicationPriorityRequest() throws Exception {
        int maxPriority = 10;
        int appPriority = 5;
        YarnConfiguration conf = new YarnConfiguration();
        Assume.assumeFalse("FairScheduler does not support Application Priorities", conf.get(RM_SCHEDULER).equals(FairScheduler.class.getName()));
        conf.setInt(MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY, maxPriority);
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        start();
        rm.registerNode("host1:1234", 1024);
        // Start app1 with appPriority 5
        RMApp app1 = rm.submitApp(1024, Priority.newInstance(appPriority));
        Assert.assertEquals("Incorrect priority has been set to application", appPriority, app1.getApplicationPriority().getPriority());
        appPriority = 11;
        ClientRMService rmService = rm.getClientRMService();
        testApplicationPriorityUpdation(rmService, app1, appPriority, maxPriority);
        appPriority = 9;
        testApplicationPriorityUpdation(rmService, app1, appPriority, appPriority);
        rm.killApp(app1.getApplicationId());
        rm.waitForState(app1.getApplicationId(), RMAppState.KILLED);
        // Update priority request for invalid application id.
        ApplicationId invalidAppId = ApplicationId.newInstance(123456789L, 3);
        UpdateApplicationPriorityRequest updateRequest = UpdateApplicationPriorityRequest.newInstance(invalidAppId, Priority.newInstance(appPriority));
        try {
            rmService.updateApplicationPriority(updateRequest);
            Assert.fail(("ApplicationNotFoundException should be thrown " + "for invalid application id"));
        } catch (ApplicationNotFoundException e) {
            // Expected
        }
        updateRequest = UpdateApplicationPriorityRequest.newInstance(app1.getApplicationId(), Priority.newInstance(11));
        Assert.assertEquals("Incorrect priority has been set to application", appPriority, rmService.updateApplicationPriority(updateRequest).getApplicationPriority().getPriority());
        stop();
    }

    @Test
    public void testRMStartWithDecommissionedNode() throws Exception {
        String excludeFile = "excludeFile";
        createExcludeFile(excludeFile);
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(RM_NODES_EXCLUDE_FILE_PATH, excludeFile);
        MockRM rm = new MockRM(conf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Make call
        GetClusterNodesRequest request = GetClusterNodesRequest.newInstance(EnumSet.allOf(NodeState.class));
        List<NodeReport> nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals(1, nodeReports.size());
        stop();
        rpc.stopProxy(client, conf);
        new File(excludeFile).delete();
    }

    @Test
    public void testGetResourceTypesInfoWhenResourceProfileDisabled() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        MockRM rm = new MockRM(conf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Make call
        GetAllResourceTypeInfoRequest request = GetAllResourceTypeInfoRequest.newInstance();
        GetAllResourceTypeInfoResponse response = client.getResourceTypeInfo(request);
        Assert.assertEquals(2, response.getResourceTypeInfo().size());
        // Check memory
        Assert.assertEquals(MEMORY_MB.getName(), response.getResourceTypeInfo().get(0).getName());
        Assert.assertEquals(MEMORY_MB.getUnits(), response.getResourceTypeInfo().get(0).getDefaultUnit());
        // Check vcores
        Assert.assertEquals(VCORES.getName(), response.getResourceTypeInfo().get(1).getName());
        Assert.assertEquals(VCORES.getUnits(), response.getResourceTypeInfo().get(1).getDefaultUnit());
        stop();
        rpc.stopProxy(client, conf);
    }

    @Test
    public void testGetApplicationsWithPerUserApps() throws IOException, YarnException {
        /* Submit 3 applications alternately in two queues */
        // Basic setup
        ResourceScheduler scheduler = TestClientRMService.mockResourceScheduler();
        RMContext rmContext = Mockito.mock(RMContext.class);
        mockRMContext(scheduler, rmContext);
        RMStateStore stateStore = Mockito.mock(RMStateStore.class);
        Mockito.when(rmContext.getStateStore()).thenReturn(stateStore);
        Mockito.doReturn(Mockito.mock(RMTimelineCollectorManager.class)).when(rmContext).getRMTimelineCollectorManager();
        RMAppManager appManager = new RMAppManager(rmContext, scheduler, null, Mockito.mock(ApplicationACLsManager.class), new Configuration());
        Mockito.when(rmContext.getDispatcher().getEventHandler()).thenReturn(new org.apache.hadoop.yarn.event.EventHandler<Event>() {
            public void handle(Event event) {
            }
        });
        // Simulate Queue ACL manager which returns false always
        QueueACLsManager queueAclsManager = Mockito.mock(QueueACLsManager.class);
        Mockito.when(queueAclsManager.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(QueueACL.class), ArgumentMatchers.any(RMApp.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyList())).thenReturn(false);
        // Simulate app ACL manager which returns false always
        ApplicationACLsManager appAclsManager = Mockito.mock(ApplicationACLsManager.class);
        Mockito.when(appAclsManager.checkAccess(ArgumentMatchers.eq(UserGroupInformation.getCurrentUser()), ArgumentMatchers.any(ApplicationAccessType.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ApplicationId.class))).thenReturn(false);
        ClientRMService rmService = new ClientRMService(rmContext, scheduler, appManager, appAclsManager, queueAclsManager, null);
        rmService.init(new Configuration());
        // Initialize appnames and queues
        String[] queues = new String[]{ TestClientRMService.QUEUE_1, TestClientRMService.QUEUE_2 };
        String[] appNames = new String[]{ MockApps.newAppName(), MockApps.newAppName(), MockApps.newAppName() };
        ApplicationId[] appIds = new ApplicationId[]{ TestClientRMService.getApplicationId(101), TestClientRMService.getApplicationId(102), TestClientRMService.getApplicationId(103) };
        List<String> tags = Arrays.asList("Tag1", "Tag2", "Tag3");
        long[] submitTimeMillis = new long[3];
        // Submit applications
        for (int i = 0; i < (appIds.length); i++) {
            ApplicationId appId = appIds[i];
            SubmitApplicationRequest submitRequest = mockSubmitAppRequest(appId, appNames[i], queues[(i % (queues.length))], new HashSet<String>(tags.subList(0, (i + 1))));
            rmService.submitApplication(submitRequest);
            submitTimeMillis[i] = System.currentTimeMillis();
        }
        // Test different cases of ClientRMService#getApplications()
        GetApplicationsRequest request = GetApplicationsRequest.newInstance();
        Assert.assertEquals("Incorrect total number of apps", 6, rmService.getApplications(request).getApplicationList().size());
        rmService.setDisplayPerUserApps(true);
        Assert.assertEquals("Incorrect number of applications for user", 0, rmService.getApplications(request).getApplicationList().size());
        rmService.setDisplayPerUserApps(false);
    }

    @Test
    public void testRegisterNMWithDiffUnits() throws Exception {
        ResourceUtils.resetResourceTypes();
        Configuration yarnConf = new YarnConfiguration();
        String resourceTypesFileName = "resource-types-4.xml";
        InputStream source = yarnConf.getClassLoader().getResourceAsStream(resourceTypesFileName);
        resourceTypesFile = new File(yarnConf.getClassLoader().getResource(".").getPath(), "resource-types.xml");
        FileUtils.copyInputStreamToFile(source, resourceTypesFile);
        ResourceUtils.getResourceTypes();
        yarnConf.setClass(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class, ResourceCalculator.class);
        MockRM rm = new MockRM(yarnConf) {
            protected ClientRMService createClientRMService() {
                return new ClientRMService(this.rmContext, scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, getRMContext().getRMDelegationTokenSecretManager());
            }
        };
        start();
        Resource resource = BuilderUtils.newResource(1024, 1);
        resource.setResourceInformation("memory-mb", ResourceInformation.newInstance("memory-mb", "G", 1024));
        resource.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "T", 1));
        resource.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "M", 1));
        MockNM node = rm.registerNode("host1:1234", resource);
        node.nodeHeartbeat(true);
        // Create a client.
        Configuration conf = new Configuration();
        YarnRPC rpc = YarnRPC.create(conf);
        InetSocketAddress rmAddress = getClientRMService().getBindAddress();
        TestClientRMService.LOG.info(("Connecting to ResourceManager at " + rmAddress));
        ApplicationClientProtocol client = ((ApplicationClientProtocol) (rpc.getProxy(ApplicationClientProtocol.class, rmAddress, conf)));
        // Make call
        GetClusterNodesRequest request = GetClusterNodesRequest.newInstance(EnumSet.of(RUNNING));
        List<NodeReport> nodeReports = client.getClusterNodes(request).getNodeReports();
        Assert.assertEquals(1, nodeReports.size());
        Assert.assertNotSame("Node is expected to be healthy!", UNHEALTHY, nodeReports.get(0).getNodeState());
        Assert.assertEquals(1, nodeReports.size());
        // Resource 'resource1' has been passed as 1T while registering NM.
        // 1T should be converted to 1000G
        Assert.assertEquals("G", nodeReports.get(0).getCapability().getResourceInformation("resource1").getUnits());
        Assert.assertEquals(1000, nodeReports.get(0).getCapability().getResourceInformation("resource1").getValue());
        // Resource 'resource2' has been passed as 1M while registering NM
        // 1M should be converted to 1000000000M
        Assert.assertEquals("m", nodeReports.get(0).getCapability().getResourceInformation("resource2").getUnits());
        Assert.assertEquals(1000000000, nodeReports.get(0).getCapability().getResourceInformation("resource2").getValue());
        // Resource 'memory-mb' has been passed as 1024G while registering NM
        // 1024G should be converted to 976562Mi
        Assert.assertEquals("Mi", nodeReports.get(0).getCapability().getResourceInformation("memory-mb").getUnits());
        Assert.assertEquals(976562, nodeReports.get(0).getCapability().getResourceInformation("memory-mb").getValue());
        rpc.stopProxy(client, conf);
        close();
    }
}

