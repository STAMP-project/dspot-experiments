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


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import RMAppAttemptImpl.AM_CONTAINER_PRIORITY;
import RMAppEventType.APP_REJECTED;
import RMAppEventType.KILL;
import RMAppEventType.START;
import RMAppManager.ApplicationSummary.SummaryBuilder;
import RMAppState.FINISHED;
import RMAppState.RUNNING;
import RMNodeLabelsManager.NO_LABEL;
import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import ResourceRequest.ANY;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_MAX_COMPLETED_APPLICATIONS;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS;
import YarnConfiguration.YARN_ACL_ENABLE;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ExecutionTypeRequest;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.placement.ApplicationPlacementContext;
import org.apache.hadoop.yarn.server.resourcemanager.placement.PlacementManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.MockRMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.ManagedParentQueue;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.PREFIX;


/**
 * Testing applications being retired from RM.
 */
public class TestAppManager extends AppManagerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestAppManager.class);

    private static RMAppEventType appEventType = RMAppEventType.KILL;

    private static String USER = "user_";

    private static String USER0 = (TestAppManager.USER) + 0;

    public class TestAppManagerDispatcher implements EventHandler<RMAppManagerEvent> {
        public TestAppManagerDispatcher() {
        }

        @Override
        public void handle(RMAppManagerEvent event) {
            // do nothing
        }
    }

    public class TestDispatcher implements EventHandler<RMAppEvent> {
        public TestDispatcher() {
        }

        @Override
        public void handle(RMAppEvent event) {
            // RMApp rmApp = this.rmContext.getRMApps().get(appID);
            setAppEventType(event.getType());
            System.out.println(("in handle routine " + (getAppEventType().toString())));
        }
    }

    private RMContext rmContext;

    private SystemMetricsPublisher metricsPublisher;

    private AppManagerTestBase.TestRMAppManager appMonitor;

    private ApplicationSubmissionContext asContext;

    private ApplicationId appId;

    private QueueInfo mockDefaultQueueInfo;

    @Test
    public void testQueueSubmitWithACLsEnabledWithQueueMapping() throws IOException, InterruptedException, YarnException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf, false);
        csConf.set(((PREFIX) + "root.queues"), "default,test");
        csConf.setFloat(((PREFIX) + "root.default.capacity"), 50.0F);
        csConf.setFloat(((PREFIX) + "root.default.maximum-capacity"), 100.0F);
        csConf.setFloat(((PREFIX) + "root.test.capacity"), 50.0F);
        csConf.setFloat(((PREFIX) + "root.test.maximum-capacity"), 100.0F);
        csConf.set(((PREFIX) + "root.acl_submit_applications"), " ");
        csConf.set(((PREFIX) + "root.acl_administer_queue"), " ");
        csConf.set(((PREFIX) + "root.default.acl_submit_applications"), " ");
        csConf.set(((PREFIX) + "root.default.acl_administer_queue"), " ");
        csConf.set(((PREFIX) + "root.test.acl_submit_applications"), "test");
        csConf.set(((PREFIX) + "root.test.acl_administer_queue"), "test");
        csConf.set(((PREFIX) + "root.test.acl_submit_applications"), "test");
        csConf.set(((PREFIX) + "root.test.acl_administer_queue"), "test");
        csConf.set(YARN_ACL_ENABLE, "true");
        // Setup a PlacementManager returns a new queue
        PlacementManager placementMgr = Mockito.mock(PlacementManager.class);
        Mockito.doAnswer(new Answer<ApplicationPlacementContext>() {
            @Override
            public ApplicationPlacementContext answer(InvocationOnMock invocation) throws Throwable {
                return new ApplicationPlacementContext("test");
            }
        }).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.matches("test"));
        asContext.setQueue("oldQueue");
        MockRM newMockRM = new MockRM(conf);
        RMContext newMockRMContext = getRMContext();
        newMockRMContext.setQueuePlacementManager(placementMgr);
        ApplicationMasterService masterService = new ApplicationMasterService(newMockRMContext, newMockRMContext.getScheduler());
        AppManagerTestBase.TestRMAppManager newAppMonitor = new AppManagerTestBase.TestRMAppManager(newMockRMContext, new ClientToAMTokenSecretManagerInRM(), newMockRMContext.getScheduler(), masterService, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), conf);
        // only user test has permission to submit to 'test' queue
        newAppMonitor.submitApplication(asContext, "test");
        try {
            // should fail since user does not have permission to submit to queue
            // 'test'
            asContext.setApplicationId((appId = MockApps.newAppID(2)));
            newAppMonitor.submitApplication(asContext, "test1");
        } catch (YarnException e) {
            Assert.assertTrue(((e.getCause()) instanceof AccessControlException));
        }
    }

    @Test
    public void testQueueSubmitWithACLsEnabledWithQueueMappingForAutoCreatedQueue() throws IOException, InterruptedException, YarnException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(YARN_ACL_ENABLE, "true");
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf, false);
        csConf.set(((PREFIX) + "root.queues"), "default,managedparent");
        csConf.setFloat(((PREFIX) + "root.default.capacity"), 50.0F);
        csConf.setFloat(((PREFIX) + "root.default.maximum-capacity"), 100.0F);
        csConf.set(((PREFIX) + "root.acl_submit_applications"), " ");
        csConf.set(((PREFIX) + "root.acl_administer_queue"), " ");
        csConf.set(((PREFIX) + "root.default.acl_submit_applications"), " ");
        csConf.set(((PREFIX) + "root.default.acl_administer_queue"), " ");
        csConf.set(((PREFIX) + "root.managedparent.acl_administer_queue"), "admin");
        csConf.set(((PREFIX) + "root.managedparent.acl_submit_applications"), "user1");
        csConf.setAutoCreateChildQueueEnabled("root.managedparent", true);
        csConf.setAutoCreatedLeafQueueConfigCapacity("root.managedparent", 30.0F);
        csConf.setAutoCreatedLeafQueueConfigMaxCapacity("root.managedparent", 100.0F);
        // Setup a PlacementManager returns a new queue
        PlacementManager placementMgr = Mockito.mock(PlacementManager.class);
        Mockito.doAnswer(new Answer<ApplicationPlacementContext>() {
            @Override
            public ApplicationPlacementContext answer(InvocationOnMock invocation) throws Throwable {
                return new ApplicationPlacementContext("user1", "managedparent");
            }
        }).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.matches("user1|user2"));
        asContext.setQueue("oldQueue");
        MockRM newMockRM = new MockRM(conf);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        ManagedParentQueue managedParentQueue = new ManagedParentQueue(cs, "managedparent", cs.getQueue("root"), null);
        cs.getCapacitySchedulerQueueManager().addQueue("managedparent", managedParentQueue);
        RMContext newMockRMContext = getRMContext();
        newMockRMContext.setQueuePlacementManager(placementMgr);
        ApplicationMasterService masterService = new ApplicationMasterService(newMockRMContext, newMockRMContext.getScheduler());
        AppManagerTestBase.TestRMAppManager newAppMonitor = new AppManagerTestBase.TestRMAppManager(newMockRMContext, new ClientToAMTokenSecretManagerInRM(), newMockRMContext.getScheduler(), masterService, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), conf);
        // only user test has permission to submit to 'user1' queue
        newAppMonitor.submitApplication(asContext, "user1");
        try {
            // should fail since user does not have permission to submit to queue
            // 'managedparent'
            asContext.setApplicationId((appId = MockApps.newAppID(2)));
            newAppMonitor.submitApplication(asContext, "user2");
        } catch (YarnException e) {
            Assert.assertTrue(((e.getCause()) instanceof AccessControlException));
        }
    }

    @Test
    public void testRMAppRetireNone() throws Exception {
        long now = System.currentTimeMillis();
        // Create such that none of the applications will retire since
        // haven't hit max #
        RMContext rmContext = mockRMContext(10, (now - 10));
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, 10);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        Assert.assertEquals("Number of apps incorrect before checkAppTimeLimit", 10, rmContext.getRMApps().size());
        // add them to completed apps list
        addToCompletedApps(appMonitor, rmContext);
        // shouldn't  have to many apps
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", 10, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", 10, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.never()).removeApplication(ArgumentMatchers.isA(RMApp.class));
    }

    @Test
    public void testQueueSubmitWithNoPermission() throws IOException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(((PREFIX) + "root.acl_submit_applications"), " ");
        conf.set(((PREFIX) + "root.acl_administer_queue"), " ");
        conf.set(((PREFIX) + "root.default.acl_submit_applications"), " ");
        conf.set(((PREFIX) + "root.default.acl_administer_queue"), " ");
        conf.set(YARN_ACL_ENABLE, "true");
        MockRM mockRM = new MockRM(conf);
        ClientRMService rmService = getClientRMService();
        SubmitApplicationRequest req = Records.newRecord(SubmitApplicationRequest.class);
        ApplicationSubmissionContext sub = Records.newRecord(ApplicationSubmissionContext.class);
        sub.setApplicationId(appId);
        ResourceRequest resReg = ResourceRequest.newInstance(Priority.newInstance(0), ANY, Resource.newInstance(1024, 1), 1);
        sub.setAMContainerResourceRequests(Collections.singletonList(resReg));
        req.setApplicationSubmissionContext(sub);
        sub.setAMContainerSpec(Mockito.mock(ContainerLaunchContext.class));
        try {
            rmService.submitApplication(req);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof YarnException) {
                Assert.assertTrue(((e.getCause()) instanceof AccessControlException));
            } else {
                Assert.fail(("Yarn exception is expected : " + (e.getMessage())));
            }
        } finally {
            close();
        }
    }

    @Test
    public void testRMAppRetireSome() throws Exception {
        long now = System.currentTimeMillis();
        RMContext rmContext = mockRMContext(10, (now - 20000));
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, 3);
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, 3);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        Assert.assertEquals("Number of apps incorrect before", 10, rmContext.getRMApps().size());
        // add them to completed apps list
        addToCompletedApps(appMonitor, rmContext);
        // shouldn't  have to many apps
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", 3, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", 3, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.times(7)).removeApplication(ArgumentMatchers.isA(RMApp.class));
    }

    @Test
    public void testRMAppRetireSomeDifferentStates() throws Exception {
        long now = System.currentTimeMillis();
        // these parameters don't matter, override applications below
        RMContext rmContext = mockRMContext(10, (now - 20000));
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, 2);
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, 2);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        // clear out applications map
        rmContext.getRMApps().clear();
        Assert.assertEquals("map isn't empty", 0, rmContext.getRMApps().size());
        // 6 applications are in final state, 4 are not in final state.
        // / set with various finished states
        RMApp app = new MockRMApp(0, (now - 20000), RMAppState.KILLED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(1, (now - 200000), RMAppState.FAILED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(2, (now - 30000), RMAppState.FINISHED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(3, (now - 20000), RMAppState.RUNNING);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(4, (now - 20000), RMAppState.NEW);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        // make sure it doesn't expire these since still running
        app = new MockRMApp(5, (now - 10001), RMAppState.KILLED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(6, (now - 30000), RMAppState.ACCEPTED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(7, (now - 20000), RMAppState.SUBMITTED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(8, (now - 10001), RMAppState.FAILED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        app = new MockRMApp(9, (now - 20000), RMAppState.FAILED);
        rmContext.getRMApps().put(app.getApplicationId(), app);
        Assert.assertEquals("Number of apps incorrect before", 10, rmContext.getRMApps().size());
        // add them to completed apps list
        addToCompletedApps(appMonitor, rmContext);
        // shouldn't  have to many apps
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", 6, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", 2, appMonitor.getCompletedAppsListSize());
        // 6 applications in final state, 4 of them are removed
        Mockito.verify(rmContext.getStateStore(), Mockito.times(4)).removeApplication(ArgumentMatchers.isA(RMApp.class));
    }

    @Test
    public void testRMAppRetireNullApp() throws Exception {
        long now = System.currentTimeMillis();
        RMContext rmContext = mockRMContext(10, (now - 20000));
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, new Configuration());
        Assert.assertEquals("Number of apps incorrect before", 10, rmContext.getRMApps().size());
        appMonitor.finishApplication(null);
        Assert.assertEquals("Number of completed apps incorrect after check", 0, appMonitor.getCompletedAppsListSize());
    }

    @Test
    public void testRMAppRetireZeroSetting() throws Exception {
        long now = System.currentTimeMillis();
        RMContext rmContext = mockRMContext(10, (now - 20000));
        Configuration conf = new YarnConfiguration();
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, 0);
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, 0);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        Assert.assertEquals("Number of apps incorrect before", 10, rmContext.getRMApps().size());
        addToCompletedApps(appMonitor, rmContext);
        Assert.assertEquals("Number of completed apps incorrect", 10, appMonitor.getCompletedAppsListSize());
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", 0, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", 0, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.times(10)).removeApplication(ArgumentMatchers.isA(RMApp.class));
    }

    @Test
    public void testStateStoreAppLimitLessThanMemoryAppLimit() {
        long now = System.currentTimeMillis();
        final int allApps = 10;
        RMContext rmContext = mockRMContext(allApps, (now - 20000));
        Configuration conf = new YarnConfiguration();
        int maxAppsInMemory = 8;
        int maxAppsInStateStore = 4;
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, maxAppsInMemory);
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, maxAppsInStateStore);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        addToCompletedApps(appMonitor, rmContext);
        Assert.assertEquals("Number of completed apps incorrect", allApps, appMonitor.getCompletedAppsListSize());
        int numRemoveAppsFromStateStore = allApps - maxAppsInStateStore;
        Set<ApplicationId> appsShouldBeRemovedFromStateStore = appMonitor.getFirstNCompletedApps(numRemoveAppsFromStateStore);
        appMonitor.checkAppNumCompletedLimit();
        Set<ApplicationId> removedAppsFromStateStore = appMonitor.getRemovedAppsFromStateStore(numRemoveAppsFromStateStore);
        Assert.assertEquals("Number of apps incorrect after # completed check", maxAppsInMemory, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", maxAppsInMemory, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.times(numRemoveAppsFromStateStore)).removeApplication(ArgumentMatchers.isA(RMApp.class));
        Assert.assertEquals(maxAppsInStateStore, appMonitor.getNumberOfCompletedAppsInStateStore());
        List<ApplicationId> completedApps = appMonitor.getCompletedApps();
        Assert.assertEquals(maxAppsInMemory, completedApps.size());
        Assert.assertEquals(numRemoveAppsFromStateStore, removedAppsFromStateStore.size());
        Assert.assertEquals(numRemoveAppsFromStateStore, Sets.intersection(appsShouldBeRemovedFromStateStore, removedAppsFromStateStore).size());
    }

    @Test
    public void testStateStoreAppLimitGreaterThanMemoryAppLimit() {
        long now = System.currentTimeMillis();
        final int allApps = 10;
        RMContext rmContext = mockRMContext(allApps, (now - 20000));
        Configuration conf = new YarnConfiguration();
        int maxAppsInMemory = 8;
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, maxAppsInMemory);
        // greater than maxCompletedAppsInMemory, reset to RM_MAX_COMPLETED_APPLICATIONS.
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, 1000);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        addToCompletedApps(appMonitor, rmContext);
        Assert.assertEquals("Number of completed apps incorrect", allApps, appMonitor.getCompletedAppsListSize());
        int numRemoveApps = allApps - maxAppsInMemory;
        Set<ApplicationId> appsShouldBeRemoved = appMonitor.getFirstNCompletedApps(numRemoveApps);
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", maxAppsInMemory, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", maxAppsInMemory, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.times(numRemoveApps)).removeApplication(ArgumentMatchers.isA(RMApp.class));
        Assert.assertEquals(maxAppsInMemory, appMonitor.getNumberOfCompletedAppsInStateStore());
        List<ApplicationId> completedApps = appMonitor.getCompletedApps();
        Assert.assertEquals(maxAppsInMemory, completedApps.size());
        Assert.assertEquals(numRemoveApps, appsShouldBeRemoved.size());
        Assert.assertTrue(Collections.disjoint(completedApps, appsShouldBeRemoved));
    }

    @Test
    public void testStateStoreAppLimitSomeAppsHaveNotFinishedLogAggregation() {
        long now = System.currentTimeMillis();
        final int allApps = 10;
        RMContext rmContext = mockRMContextWithMixedLogAggregationStatus(allApps, (now - 20000));
        Configuration conf = new YarnConfiguration();
        int maxAppsInMemory = 2;
        conf.setInt(RM_MAX_COMPLETED_APPLICATIONS, maxAppsInMemory);
        // greater than maxCompletedAppsInMemory, reset to
        // RM_MAX_COMPLETED_APPLICATIONS.
        conf.setInt(RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS, 1000);
        AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, conf);
        addToCompletedApps(appMonitor, rmContext);
        Assert.assertEquals("Number of completed apps incorrect", allApps, appMonitor.getCompletedAppsListSize());
        int numRemoveApps = allApps - maxAppsInMemory;
        int effectiveNumRemoveApps = numRemoveApps / 2;
        // only apps with even ID would be deleted due to log aggregation status
        int expectedNumberOfAppsInMemory = maxAppsInMemory + effectiveNumRemoveApps;
        Set<ApplicationId> appsShouldBeRemoved = appMonitor.getCompletedAppsWithEvenIdsInRange(numRemoveApps);
        appMonitor.checkAppNumCompletedLimit();
        Assert.assertEquals("Number of apps incorrect after # completed check", expectedNumberOfAppsInMemory, rmContext.getRMApps().size());
        Assert.assertEquals("Number of completed apps incorrect after check", expectedNumberOfAppsInMemory, appMonitor.getCompletedAppsListSize());
        Mockito.verify(rmContext.getStateStore(), Mockito.times(effectiveNumRemoveApps)).removeApplication(ArgumentMatchers.isA(RMApp.class));
        Assert.assertEquals(expectedNumberOfAppsInMemory, appMonitor.getNumberOfCompletedAppsInStateStore());
        List<ApplicationId> completedApps = appMonitor.getCompletedApps();
        Assert.assertEquals(expectedNumberOfAppsInMemory, completedApps.size());
        Assert.assertEquals(effectiveNumRemoveApps, appsShouldBeRemoved.size());
        Assert.assertTrue(Collections.disjoint(completedApps, appsShouldBeRemoved));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRMAppSubmitAMContainerResourceRequests() throws Exception {
        asContext.setResource(Resources.createResource(1024));
        asContext.setAMContainerResourceRequest(ResourceRequest.newInstance(Priority.newInstance(0), ANY, Resources.createResource(1024), 1, true));
        List<ResourceRequest> reqs = new ArrayList<>();
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(0), ANY, Resources.createResource(1025), 1, false));
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(0), "/rack", Resources.createResource(1025), 1, false));
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(0), "/rack/node", Resources.createResource(1025), 1, true));
        asContext.setAMContainerResourceRequests(TestAppManager.cloneResourceRequests(reqs));
        // getAMContainerResourceRequest uses the first entry of
        // getAMContainerResourceRequests
        Assert.assertEquals(reqs.get(0), asContext.getAMContainerResourceRequest());
        Assert.assertEquals(reqs, asContext.getAMContainerResourceRequests());
        RMApp app = testRMAppSubmit();
        for (ResourceRequest req : reqs) {
            req.setNodeLabelExpression(NO_LABEL);
        }
        // setAMContainerResourceRequests has priority over
        // setAMContainerResourceRequest and setResource
        Assert.assertEquals(reqs, app.getAMResourceRequests());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRMAppSubmitAMContainerResourceRequest() throws Exception {
        asContext.setResource(Resources.createResource(1024));
        asContext.setAMContainerResourceRequests(null);
        ResourceRequest req = ResourceRequest.newInstance(Priority.newInstance(0), ANY, Resources.createResource(1025), 1, true);
        req.setNodeLabelExpression(NO_LABEL);
        asContext.setAMContainerResourceRequest(ResourceRequest.clone(req));
        // getAMContainerResourceRequests uses a singleton list of
        // getAMContainerResourceRequest
        Assert.assertEquals(req, asContext.getAMContainerResourceRequest());
        Assert.assertEquals(req, asContext.getAMContainerResourceRequests().get(0));
        Assert.assertEquals(1, asContext.getAMContainerResourceRequests().size());
        RMApp app = testRMAppSubmit();
        // setAMContainerResourceRequest has priority over setResource
        Assert.assertEquals(Collections.singletonList(req), app.getAMResourceRequests());
    }

    @Test
    public void testRMAppSubmitResource() throws Exception {
        asContext.setResource(Resources.createResource(1024));
        asContext.setAMContainerResourceRequests(null);
        RMApp app = testRMAppSubmit();
        // setResource
        Assert.assertEquals(Collections.singletonList(ResourceRequest.newInstance(AM_CONTAINER_PRIORITY, ANY, Resources.createResource(1024), 1, true, "")), app.getAMResourceRequests());
    }

    @Test
    public void testRMAppSubmitNoResourceRequests() throws Exception {
        asContext.setResource(null);
        asContext.setAMContainerResourceRequests(null);
        try {
            testRMAppSubmit();
            Assert.fail("Should have failed due to no ResourceRequest");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals("Invalid resource request, no resources requested", e.getMessage());
        }
    }

    @Test
    public void testRMAppSubmitAMContainerResourceRequestsDisagree() throws Exception {
        asContext.setResource(null);
        List<ResourceRequest> reqs = new ArrayList<>();
        Mockito.when(mockDefaultQueueInfo.getAccessibleNodeLabels()).thenReturn(new HashSet<String>() {
            {
                add("label1");
                add("");
            }
        });
        ResourceRequest anyReq = ResourceRequest.newInstance(Priority.newInstance(1), ANY, Resources.createResource(1024), 1, false, "label1", ExecutionTypeRequest.newInstance(GUARANTEED));
        reqs.add(anyReq);
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(2), "/rack", Resources.createResource(1025), 2, false, "", ExecutionTypeRequest.newInstance(OPPORTUNISTIC)));
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(3), "/rack/node", Resources.createResource(1026), 3, true, "", ExecutionTypeRequest.newInstance(OPPORTUNISTIC)));
        asContext.setAMContainerResourceRequests(TestAppManager.cloneResourceRequests(reqs));
        RMApp app = testRMAppSubmit();
        // It should force the requests to all agree on these points
        for (ResourceRequest req : reqs) {
            req.setCapability(anyReq.getCapability());
            req.setExecutionTypeRequest(ExecutionTypeRequest.newInstance(GUARANTEED));
            req.setNumContainers(1);
            req.setPriority(Priority.newInstance(0));
        }
        Assert.assertEquals(reqs, app.getAMResourceRequests());
    }

    @Test
    public void testRMAppSubmitAMContainerResourceRequestsNoAny() throws Exception {
        asContext.setResource(null);
        List<ResourceRequest> reqs = new ArrayList<>();
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(1), "/rack", Resources.createResource(1025), 1, false));
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(1), "/rack/node", Resources.createResource(1025), 1, true));
        asContext.setAMContainerResourceRequests(TestAppManager.cloneResourceRequests(reqs));
        // getAMContainerResourceRequest uses the first entry of
        // getAMContainerResourceRequests
        Assert.assertEquals(reqs, asContext.getAMContainerResourceRequests());
        try {
            testRMAppSubmit();
            Assert.fail("Should have failed due to missing ANY ResourceRequest");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals("Invalid resource request, no resource request specified with *", e.getMessage());
        }
    }

    @Test
    public void testRMAppSubmitAMContainerResourceRequestsTwoManyAny() throws Exception {
        asContext.setResource(null);
        List<ResourceRequest> reqs = new ArrayList<>();
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(1), ANY, Resources.createResource(1025), 1, false));
        reqs.add(ResourceRequest.newInstance(Priority.newInstance(1), ANY, Resources.createResource(1025), 1, false));
        asContext.setAMContainerResourceRequests(TestAppManager.cloneResourceRequests(reqs));
        // getAMContainerResourceRequest uses the first entry of
        // getAMContainerResourceRequests
        Assert.assertEquals(reqs, asContext.getAMContainerResourceRequests());
        try {
            testRMAppSubmit();
            Assert.fail("Should have failed due to too many ANY ResourceRequests");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(("Invalid resource request, only one resource request with * is " + "allowed"), e.getMessage());
        }
    }

    @Test
    public void testRMAppSubmitWithInvalidTokens() throws Exception {
        // Setup invalid security tokens
        DataOutputBuffer dob = new DataOutputBuffer();
        ByteBuffer securityTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        asContext.getAMContainerSpec().setTokens(securityTokens);
        try {
            appMonitor.submitApplication(asContext, "test");
            Assert.fail(("Application submission should fail because" + " Tokens are invalid."));
        } catch (YarnException e) {
            // Exception is expected
            Assert.assertTrue(("The thrown exception is not" + " java.io.EOFException"), e.getMessage().contains("java.io.EOFException"));
        }
        int timeoutSecs = 0;
        while (((getAppEventType()) == (RMAppEventType.KILL)) && ((timeoutSecs++) < 20)) {
            Thread.sleep(1000);
        } 
        Assert.assertEquals("app event type sent is wrong", APP_REJECTED, getAppEventType());
        asContext.getAMContainerSpec().setTokens(null);
    }

    @Test(timeout = 30000)
    public void testRMAppSubmitMaxAppAttempts() throws Exception {
        int[] globalMaxAppAttempts = new int[]{ 10, 1 };
        int[][] individualMaxAppAttempts = new int[][]{ new int[]{ 9, 10, 11, 0 }, new int[]{ 1, 10, 0, -1 } };
        int[][] expectedNums = new int[][]{ new int[]{ 9, 10, 10, 10 }, new int[]{ 1, 1, 1, 1 } };
        for (int i = 0; i < (globalMaxAppAttempts.length); ++i) {
            for (int j = 0; j < (individualMaxAppAttempts.length); ++j) {
                ResourceScheduler scheduler = TestAppManager.mockResourceScheduler();
                Configuration conf = new Configuration();
                conf.setInt(RM_AM_MAX_ATTEMPTS, globalMaxAppAttempts[i]);
                ApplicationMasterService masterService = new ApplicationMasterService(rmContext, scheduler);
                AppManagerTestBase.TestRMAppManager appMonitor = new AppManagerTestBase.TestRMAppManager(rmContext, new ClientToAMTokenSecretManagerInRM(), scheduler, masterService, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), conf);
                ApplicationId appID = MockApps.newAppID((((i * 4) + j) + 1));
                asContext.setApplicationId(appID);
                if ((individualMaxAppAttempts[i][j]) != 0) {
                    asContext.setMaxAppAttempts(individualMaxAppAttempts[i][j]);
                }
                appMonitor.submitApplication(asContext, "test");
                RMApp app = rmContext.getRMApps().get(appID);
                Assert.assertEquals("max application attempts doesn't match", expectedNums[i][j], app.getMaxAppAttempts());
                // wait for event to be processed
                int timeoutSecs = 0;
                while (((getAppEventType()) == (RMAppEventType.KILL)) && ((timeoutSecs++) < 20)) {
                    Thread.sleep(1000);
                } 
                setAppEventType(KILL);
            }
        }
    }

    @Test(timeout = 30000)
    public void testRMAppSubmitDuplicateApplicationId() throws Exception {
        ApplicationId appId = MockApps.newAppID(0);
        asContext.setApplicationId(appId);
        RMApp appOrig = rmContext.getRMApps().get(appId);
        Assert.assertTrue(("app name matches " + "but shouldn't"), ("testApp1" != (appOrig.getName())));
        // our testApp1 should be rejected and original app with same id should be left in place
        try {
            appMonitor.submitApplication(asContext, "test");
            Assert.fail("Exception is expected when applicationId is duplicate.");
        } catch (YarnException e) {
            Assert.assertTrue("The thrown exception is not the expectd one.", e.getMessage().contains("Cannot add a duplicate!"));
        }
        // make sure original app didn't get removed
        RMApp app = rmContext.getRMApps().get(appId);
        Assert.assertNotNull("app is null", app);
        Assert.assertEquals("app id doesn't match", appId, app.getApplicationId());
        Assert.assertEquals("app state doesn't match", FINISHED, app.getState());
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 30000)
    public void testRMAppSubmitInvalidResourceRequest() throws Exception {
        asContext.setResource(Resources.createResource(((YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB) + 1)));
        // submit an app
        try {
            appMonitor.submitApplication(asContext, "test");
            Assert.fail(("Application submission should fail because resource" + " request is invalid."));
        } catch (YarnException e) {
            // Exception is expected
            // TODO Change this to assert the expected exception type - post YARN-142
            // sub-task related to specialized exceptions.
            Assert.assertTrue(("The thrown exception is not" + " InvalidResourceRequestException"), e.getMessage().contains("Invalid resource request"));
        }
    }

    @Test(timeout = 30000)
    public void testEscapeApplicationSummary() {
        RMApp app = Mockito.mock(RMAppImpl.class);
        Mockito.when(app.getApplicationId()).thenReturn(ApplicationId.newInstance(100L, 1));
        Mockito.when(app.getName()).thenReturn("Multiline\n\n\r\rAppName");
        Mockito.when(app.getUser()).thenReturn("Multiline\n\n\r\rUserName");
        Mockito.when(app.getQueue()).thenReturn("Multiline\n\n\r\rQueueName");
        Mockito.when(app.getState()).thenReturn(RUNNING);
        Mockito.when(app.getApplicationType()).thenReturn("MAPREDUCE");
        Mockito.when(app.getSubmitTime()).thenReturn(1000L);
        Mockito.when(app.getLaunchTime()).thenReturn(2000L);
        Map<String, Long> resourceSecondsMap = new HashMap<>();
        resourceSecondsMap.put(MEMORY_MB.getName(), 16384L);
        resourceSecondsMap.put(VCORES.getName(), 64L);
        RMAppMetrics metrics = new RMAppMetrics(Resource.newInstance(1234, 56), 10, 1, resourceSecondsMap, new HashMap());
        Mockito.when(app.getRMAppMetrics()).thenReturn(metrics);
        RMAppManager.ApplicationSummary.SummaryBuilder summary = new RMAppManager.ApplicationSummary().createAppSummary(app);
        String msg = summary.toString();
        TestAppManager.LOG.info(("summary: " + msg));
        Assert.assertFalse(msg.contains("\n"));
        Assert.assertFalse(msg.contains("\r"));
        String escaped = "\\n\\n\\r\\r";
        Assert.assertTrue(msg.contains((("Multiline" + escaped) + "AppName")));
        Assert.assertTrue(msg.contains((("Multiline" + escaped) + "UserName")));
        Assert.assertTrue(msg.contains((("Multiline" + escaped) + "QueueName")));
        Assert.assertTrue(msg.contains("submitTime=1000"));
        Assert.assertTrue(msg.contains("launchTime=2000"));
        Assert.assertTrue(msg.contains("memorySeconds=16384"));
        Assert.assertTrue(msg.contains("vcoreSeconds=64"));
        Assert.assertTrue(msg.contains("preemptedAMContainers=1"));
        Assert.assertTrue(msg.contains("preemptedNonAMContainers=10"));
        Assert.assertTrue(msg.contains("preemptedResources=<memory:1234\\, vCores:56>"));
        Assert.assertTrue(msg.contains("applicationType=MAPREDUCE"));
    }

    @Test
    public void testRMAppSubmitWithQueueChanged() throws Exception {
        // Setup a PlacementManager returns a new queue
        PlacementManager placementMgr = Mockito.mock(PlacementManager.class);
        Mockito.doAnswer(new Answer<ApplicationPlacementContext>() {
            @Override
            public ApplicationPlacementContext answer(InvocationOnMock invocation) throws Throwable {
                return new ApplicationPlacementContext("newQueue");
            }
        }).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.any(String.class));
        rmContext.setQueuePlacementManager(placementMgr);
        asContext.setQueue("oldQueue");
        appMonitor.submitApplication(asContext, "test");
        RMApp app = rmContext.getRMApps().get(appId);
        RMAppEvent event = new RMAppEvent(appId, RMAppEventType.START);
        rmContext.getRMApps().get(appId).handle(event);
        event = new RMAppEvent(appId, RMAppEventType.APP_NEW_SAVED);
        rmContext.getRMApps().get(appId).handle(event);
        Assert.assertNotNull("app is null", app);
        Assert.assertEquals("newQueue", asContext.getQueue());
        // wait for event to be processed
        int timeoutSecs = 0;
        while (((getAppEventType()) == (RMAppEventType.KILL)) && ((timeoutSecs++) < 20)) {
            Thread.sleep(1000);
        } 
        Assert.assertEquals("app event type sent is wrong", START, getAppEventType());
    }
}

