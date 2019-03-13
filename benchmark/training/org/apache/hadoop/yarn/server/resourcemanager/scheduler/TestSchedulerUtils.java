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


import ApplicationAccessType.VIEW_APP;
import ContainerExitStatus.ABORTED;
import ContainerExitStatus.PREEMPTED;
import RMNodeLabelsManager.NO_LABEL;
import ResourceRequest.ANY;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES;
import YarnConfiguration.NODE_LABELS_ENABLED;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourceRequestPBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.InvalidLabelResourceRequestException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceBlacklistRequestException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException.InvalidResourceType;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.resourcetypes.ResourceTypesTestHelper;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.TestAMAuthorization;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.yarn.server.resourcemanager.TestAMAuthorization.MockRMWithAMS.setupAndReturnAMRMToken;


public class TestSchedulerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestSchedulerUtils.class);

    private static Resource configuredMaxAllocation;

    private RMContext rmContext = TestSchedulerUtils.getMockRMContext();

    private static YarnConfiguration conf = new YarnConfiguration();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 30000)
    public void testNormalizeRequest() {
        ResourceCalculator resourceCalculator = new DefaultResourceCalculator();
        final int minMemory = 1024;
        final int maxMemory = 8192;
        Resource minResource = Resources.createResource(minMemory, 0);
        Resource maxResource = Resources.createResource(maxMemory, 0);
        ResourceRequest ask = new ResourceRequestPBImpl();
        // case negative memory
        ask.setCapability(Resources.createResource((-1024)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(minMemory, ask.getCapability().getMemorySize());
        // case zero memory
        ask.setCapability(Resources.createResource(0));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(minMemory, ask.getCapability().getMemorySize());
        // case memory is a multiple of minMemory
        ask.setCapability(Resources.createResource((2 * minMemory)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals((2 * minMemory), ask.getCapability().getMemorySize());
        // case memory is not a multiple of minMemory
        ask.setCapability(Resources.createResource((minMemory + 10)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals((2 * minMemory), ask.getCapability().getMemorySize());
        // case memory is equal to max allowed
        ask.setCapability(Resources.createResource(maxMemory));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(maxMemory, ask.getCapability().getMemorySize());
        // case memory is just less than max
        ask.setCapability(Resources.createResource((maxMemory - 10)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(maxMemory, ask.getCapability().getMemorySize());
        // max is not a multiple of min
        maxResource = Resources.createResource((maxMemory - 10), 0);
        ask.setCapability(Resources.createResource((maxMemory - 100)));
        // multiple of minMemory > maxMemory, then reduce to maxMemory
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(maxResource.getMemorySize(), ask.getCapability().getMemorySize());
        // ask is more than max
        maxResource = Resources.createResource(maxMemory, 0);
        ask.setCapability(Resources.createResource((maxMemory + 100)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(maxResource.getMemorySize(), ask.getCapability().getMemorySize());
    }

    @Test(timeout = 30000)
    public void testNormalizeRequestWithDominantResourceCalculator() {
        ResourceCalculator resourceCalculator = new DominantResourceCalculator();
        Resource minResource = Resources.createResource(1024, 1);
        Resource maxResource = Resources.createResource(10240, 10);
        Resource clusterResource = Resources.createResource((10 * 1024), 10);
        ResourceRequest ask = new ResourceRequestPBImpl();
        // case negative memory/vcores
        ask.setCapability(Resources.createResource((-1024), (-1)));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(minResource, ask.getCapability());
        // case zero memory/vcores
        ask.setCapability(Resources.createResource(0, 0));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(minResource, ask.getCapability());
        Assert.assertEquals(1, ask.getCapability().getVirtualCores());
        Assert.assertEquals(1024, ask.getCapability().getMemorySize());
        // case non-zero memory & zero cores
        ask.setCapability(Resources.createResource(1536, 0));
        SchedulerUtils.normalizeRequest(ask, resourceCalculator, minResource, maxResource);
        Assert.assertEquals(Resources.createResource(2048, 1), ask.getCapability());
        Assert.assertEquals(1, ask.getCapability().getVirtualCores());
        Assert.assertEquals(2048, ask.getCapability().getMemorySize());
    }

    @Test(timeout = 30000)
    public void testValidateResourceRequestWithErrorLabelsPermission() throws IOException {
        // mock queue and scheduler
        ResourceScheduler scheduler = Mockito.mock(ResourceScheduler.class);
        Set<String> queueAccessibleNodeLabels = Sets.newHashSet();
        QueueInfo queueInfo = Mockito.mock(QueueInfo.class);
        Mockito.when(getQueueName()).thenReturn("queue");
        Mockito.when(getAccessibleNodeLabels()).thenReturn(queueAccessibleNodeLabels);
        Mockito.when(scheduler.getQueueInfo(ArgumentMatchers.any(String.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(queueInfo);
        Mockito.when(rmContext.getScheduler()).thenReturn(scheduler);
        Resource maxResource = Resources.createResource(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        // queue has labels, success cases
        try {
            // set queue accessible node labesl to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("y");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression(" ");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            e.printStackTrace();
            Assert.fail("Should be valid when request labels is a subset of queue labels");
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y"));
        }
        // same as above, but cluster node labels don't contains label being
        // requested. should fail
        try {
            // set queue accessible node labesl to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        }
        // queue has labels, failed cases (when ask a label not included by queue)
        try {
            // set queue accessible node labesl to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("z");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y"));
        }
        // we don't allow specify more than two node labels in a single expression
        // now
        try {
            // set queue accessible node labesl to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x && y");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y"));
        }
        // queue doesn't have label, succeed (when request no label)
        queueAccessibleNodeLabels.clear();
        try {
            // set queue accessible node labels to empty
            queueAccessibleNodeLabels.clear();
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("  ");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            e.printStackTrace();
            Assert.fail("Should be valid when request labels is empty");
        }
        boolean invalidlabelexception = false;
        // queue doesn't have label, failed (when request any label)
        try {
            // set queue accessible node labels to empty
            queueAccessibleNodeLabels.clear();
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidLabelResourceRequestException e) {
            invalidlabelexception = true;
        } catch (InvalidResourceRequestException e) {
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x"));
        }
        Assert.assertTrue("InvalidLabelResourceRequestException expected", invalidlabelexception);
        // queue is "*", always succeeded
        try {
            // set queue accessible node labels to empty
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.add(RMNodeLabelsManager.ANY);
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y"), NodeLabel.newInstance("z")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("y");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            resReq.setNodeLabelExpression("z");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            e.printStackTrace();
            Assert.fail("Should be valid when queue can access any labels");
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y", "z"));
        }
        // same as above, but cluster node labels don't contains label, should fail
        try {
            // set queue accessible node labels to empty
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.add(RMNodeLabelsManager.ANY);
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        }
        // we don't allow resource name other than ANY and specify label
        try {
            // set queue accessible node labesl to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), "rack", resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y"));
        }
        // we don't allow resource name other than ANY and specify label even if
        // queue has accessible label = *
        try {
            // set queue accessible node labesl to *
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList(CommonNodeLabelsManager.ANY));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), "rack", resource, 1);
            resReq.setNodeLabelExpression("x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x"));
        }
        try {
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq1 = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), "*", resource, 1, "x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq1, "queue", scheduler, rmContext, maxResource);
            Assert.fail("Should fail");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(("Invalid label resource request, cluster do not contain , " + "label= x"), e.getMessage());
        }
        try {
            rmContext.getYarnConfiguration().set(NODE_LABELS_ENABLED, "false");
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq1 = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), "*", resource, 1, "x");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq1, "queue", scheduler, rmContext, maxResource);
            Assert.assertEquals(NO_LABEL, resReq1.getNodeLabelExpression());
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(("Invalid resource request, node label not enabled but " + "request contains label expression"), e.getMessage());
        }
    }

    @Test(timeout = 30000)
    public void testValidateResourceRequest() throws IOException {
        ResourceScheduler mockScheduler = Mockito.mock(ResourceScheduler.class);
        QueueInfo queueInfo = Mockito.mock(QueueInfo.class);
        Mockito.when(getQueueName()).thenReturn("queue");
        Resource maxResource = Resources.createResource(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        Mockito.when(rmContext.getScheduler()).thenReturn(mockScheduler);
        Mockito.when(mockScheduler.getQueueInfo(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(queueInfo);
        // zero memory
        try {
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail("Zero memory should be accepted");
        }
        // zero vcores
        try {
            Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 0);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail("Zero vcores should be accepted");
        }
        // max memory
        try {
            Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail("Max memory should be accepted");
        }
        // max vcores
        try {
            Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail("Max vcores should not be accepted");
        }
        // negative memory
        try {
            Resource resource = Resources.createResource((-1), DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
            Assert.fail("Negative memory should not be accepted");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(LESS_THAN_ZERO, e.getInvalidResourceType());
        }
        // negative vcores
        try {
            Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB, (-1));
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
            Assert.fail("Negative vcores should not be accepted");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(LESS_THAN_ZERO, e.getInvalidResourceType());
        }
        // more than max memory
        try {
            Resource resource = Resources.createResource(((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB) + 1), DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
            Assert.fail("More than max memory should not be accepted");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(GREATER_THEN_MAX_ALLOCATION, e.getInvalidResourceType());
        }
        // more than max vcores
        try {
            Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB, ((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES) + 1));
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, null, mockScheduler, rmContext, maxResource);
            Assert.fail("More than max vcores should not be accepted");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(GREATER_THEN_MAX_ALLOCATION, e.getInvalidResourceType());
        }
    }

    @Test
    public void testValidateResourceBlacklistRequest() throws Exception {
        TestAMAuthorization.MyContainerManager containerManager = new TestAMAuthorization.MyContainerManager();
        final TestAMAuthorization.MockRMWithAMS rm = new TestAMAuthorization.MockRMWithAMS(new YarnConfiguration(), containerManager);
        start();
        MockNM nm1 = rm.registerNode("localhost:1234", 5120);
        Map<ApplicationAccessType, String> acls = new HashMap<ApplicationAccessType, String>(2);
        acls.put(VIEW_APP, "*");
        RMApp app = rm.submitApp(1024, "appname", "appuser", acls);
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt = app.getCurrentAppAttempt();
        ApplicationAttemptId applicationAttemptId = attempt.getAppAttemptId();
        waitForLaunchedState(attempt);
        // Create a client to the RM.
        final Configuration yarnConf = getConfig();
        final YarnRPC rpc = YarnRPC.create(yarnConf);
        UserGroupInformation currentUser = UserGroupInformation.createRemoteUser(applicationAttemptId.toString());
        Credentials credentials = containerManager.getContainerCredentials();
        final InetSocketAddress rmBindAddress = getApplicationMasterService().getBindAddress();
        Token<? extends TokenIdentifier> amRMToken = setupAndReturnAMRMToken(rmBindAddress, credentials.getAllTokens());
        currentUser.addToken(amRMToken);
        ApplicationMasterProtocol client = currentUser.doAs(new PrivilegedAction<ApplicationMasterProtocol>() {
            @Override
            public ApplicationMasterProtocol run() {
                return ((ApplicationMasterProtocol) (rpc.getProxy(ApplicationMasterProtocol.class, rmBindAddress, yarnConf)));
            }
        });
        RegisterApplicationMasterRequest request = Records.newRecord(RegisterApplicationMasterRequest.class);
        client.registerApplicationMaster(request);
        ResourceBlacklistRequest blacklistRequest = ResourceBlacklistRequest.newInstance(Collections.singletonList(ANY), null);
        AllocateRequest allocateRequest = AllocateRequest.newInstance(0, 0.0F, null, null, blacklistRequest);
        boolean error = false;
        try {
            client.allocate(allocateRequest);
        } catch (InvalidResourceBlacklistRequestException e) {
            error = true;
        }
        stop();
        Assert.assertTrue("Didn't not catch InvalidResourceBlacklistRequestException", error);
    }

    @Test
    public void testComparePriorities() {
        Priority high = Priority.newInstance(1);
        Priority low = Priority.newInstance(2);
        Assert.assertTrue(((high.compareTo(low)) > 0));
    }

    @Test
    public void testCreateAbnormalContainerStatus() {
        ContainerStatus cd = SchedulerUtils.createAbnormalContainerStatus(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(System.currentTimeMillis(), 1), 1), 1), "x");
        Assert.assertEquals(ABORTED, cd.getExitStatus());
    }

    @Test
    public void testCreatePreemptedContainerStatus() {
        ContainerStatus cd = SchedulerUtils.createPreemptedContainerStatus(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(System.currentTimeMillis(), 1), 1), 1), "x");
        Assert.assertEquals(PREEMPTED, cd.getExitStatus());
    }

    @Test(timeout = 30000)
    public void testNormalizeNodeLabelExpression() throws IOException {
        // mock queue and scheduler
        ResourceScheduler scheduler = Mockito.mock(ResourceScheduler.class);
        Set<String> queueAccessibleNodeLabels = Sets.newHashSet();
        QueueInfo queueInfo = Mockito.mock(QueueInfo.class);
        Mockito.when(getQueueName()).thenReturn("queue");
        Mockito.when(getAccessibleNodeLabels()).thenReturn(queueAccessibleNodeLabels);
        Mockito.when(getDefaultNodeLabelExpression()).thenReturn(" x ");
        Mockito.when(scheduler.getQueueInfo(ArgumentMatchers.any(String.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(queueInfo);
        Resource maxResource = Resources.createResource(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        Mockito.when(rmContext.getScheduler()).thenReturn(scheduler);
        // queue has labels, success cases
        try {
            // set queue accessible node labels to [x, y]
            queueAccessibleNodeLabels.clear();
            queueAccessibleNodeLabels.addAll(Arrays.asList("x", "y"));
            rmContext.getNodeLabelManager().addToCluserNodeLabels(ImmutableSet.of(NodeLabel.newInstance("x"), NodeLabel.newInstance("y")));
            Resource resource = Resources.createResource(0, DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
            ResourceRequest resReq = BuilderUtils.newResourceRequest(Mockito.mock(Priority.class), ANY, resource, 1);
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.assertEquals("x", resReq.getNodeLabelExpression());
            resReq.setNodeLabelExpression(" y ");
            TestSchedulerUtils.normalizeAndvalidateRequest(resReq, "queue", scheduler, rmContext, maxResource);
            Assert.assertEquals("y", resReq.getNodeLabelExpression());
        } catch (InvalidResourceRequestException e) {
            e.printStackTrace();
            Assert.fail("Should be valid when request labels is a subset of queue labels");
        } finally {
            rmContext.getNodeLabelManager().removeFromClusterNodeLabels(Arrays.asList("x", "y"));
        }
    }

    @Test
    public void testCustomResourceRequestedUnitIsSmallerThanAvailableUnit() throws InvalidResourceRequestException {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "11"));
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "0G"));
        exception.expect(InvalidResourceRequestException.class);
        exception.expectMessage(TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator.create().withRequestedResourceType("custom-resource-1").withRequestedResource(requestedResource).withAvailableAllocation(availableResource).withMaxAllocation(TestSchedulerUtils.configuredMaxAllocation).withInvalidResourceType(GREATER_THEN_MAX_ALLOCATION).build());
        SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
    }

    @Test
    public void testCustomResourceRequestedUnitIsSmallerThanAvailableUnit2() {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "11"));
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "1G"));
        try {
            SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail(String.format("Resource request should be accepted. Requested: %s, available: %s", requestedResource, availableResource));
        }
    }

    @Test
    public void testCustomResourceRequestedUnitIsGreaterThanAvailableUnit() throws InvalidResourceRequestException {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "1M"));
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.<String, String>builder().put("custom-resource-1", "120k").build());
        exception.expect(InvalidResourceRequestException.class);
        exception.expectMessage(TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator.create().withRequestedResourceType("custom-resource-1").withRequestedResource(requestedResource).withAvailableAllocation(availableResource).withMaxAllocation(TestSchedulerUtils.configuredMaxAllocation).withInvalidResourceType(GREATER_THEN_MAX_ALLOCATION).build());
        SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
    }

    @Test
    public void testCustomResourceRequestedUnitIsGreaterThanAvailableUnit2() {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.<String, String>builder().put("custom-resource-1", "11M").build());
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "1G"));
        try {
            SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail(String.format("Resource request should be accepted. Requested: %s, available: %s", requestedResource, availableResource));
        }
    }

    @Test
    public void testCustomResourceRequestedUnitIsSameAsAvailableUnit() {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "11M"));
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "100M"));
        try {
            SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
        } catch (InvalidResourceRequestException e) {
            Assert.fail(String.format("Resource request should be accepted. Requested: %s, available: %s", requestedResource, availableResource));
        }
    }

    @Test
    public void testCustomResourceRequestedUnitIsSameAsAvailableUnit2() throws InvalidResourceRequestException {
        Resource requestedResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "110M"));
        Resource availableResource = ResourceTypesTestHelper.newResource(1, 1, ImmutableMap.of("custom-resource-1", "100M"));
        exception.expect(InvalidResourceRequestException.class);
        exception.expectMessage(TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator.create().withRequestedResourceType("custom-resource-1").withRequestedResource(requestedResource).withAvailableAllocation(availableResource).withInvalidResourceType(GREATER_THEN_MAX_ALLOCATION).withMaxAllocation(TestSchedulerUtils.configuredMaxAllocation).build());
        SchedulerUtils.checkResourceRequestAgainstAvailableResource(requestedResource, availableResource);
    }

    private static class InvalidResourceRequestExceptionMessageGenerator {
        private StringBuilder sb;

        private Resource requestedResource;

        private Resource availableAllocation;

        private Resource configuredMaxAllowedAllocation;

        private String resourceType;

        private InvalidResourceType invalidResourceType;

        InvalidResourceRequestExceptionMessageGenerator(StringBuilder sb) {
            this.sb = sb;
        }

        public static TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator create() {
            return new TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator(new StringBuilder());
        }

        TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator withRequestedResource(Resource r) {
            this.requestedResource = r;
            return this;
        }

        TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator withRequestedResourceType(String rt) {
            this.resourceType = rt;
            return this;
        }

        TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator withAvailableAllocation(Resource r) {
            this.availableAllocation = r;
            return this;
        }

        TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator withMaxAllocation(Resource r) {
            this.configuredMaxAllowedAllocation = r;
            return this;
        }

        TestSchedulerUtils.InvalidResourceRequestExceptionMessageGenerator withInvalidResourceType(InvalidResourceType invalidResourceType) {
            this.invalidResourceType = invalidResourceType;
            return this;
        }

        public String build() {
            if ((invalidResourceType) == (LESS_THAN_ZERO)) {
                return sb.append(("Invalid resource request! " + ("Cannot allocate containers as " + "requested resource is less than 0! "))).append("Requested resource type=[").append(resourceType).append("]").append(", Requested resource=").append(requestedResource).toString();
            } else
                if ((invalidResourceType) == (GREATER_THEN_MAX_ALLOCATION)) {
                    return sb.append(("Invalid resource request! " + (("Cannot allocate containers as " + "requested resource is greater than ") + "maximum allowed allocation. "))).append("Requested resource type=[").append(resourceType).append("], ").append("Requested resource=").append(requestedResource).append(", maximum allowed allocation=").append(availableAllocation).append((", please note that maximum allowed allocation is " + (("calculated by scheduler based on maximum resource " + "of registered NodeManagers, which might be less ") + "than configured maximum allocation="))).append(configuredMaxAllowedAllocation).toString();
                }

            throw new IllegalStateException(("Wrong type of InvalidResourceType is " + "detected!"));
        }
    }
}

