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
package org.apache.hadoop.yarn.server.nodemanager.containermanager;


import ApplicationAccessType.MODIFY_APP;
import ApplicationAccessType.VIEW_APP;
import ApplicationState.APPLICATION_RESOURCES_CLEANINGUP;
import ApplicationState.FINISHED;
import ApplicationState.INITING;
import ContainerType.APPLICATION_MASTER;
import ContainerType.TASK;
import YarnConfiguration.NM_RECOVERY_ENABLED;
import YarnConfiguration.NM_RECOVERY_SUPERVISED;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceUtilization;
import org.apache.hadoop.yarn.server.nodemanager.CMgrCompletedAppsEvent;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.RUNNING;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.metrics.NodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.metrics.TestNodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMMemoryStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestContainerManagerRecovery extends BaseContainerManagerTest {
    public TestContainerManagerRecovery() throws UnsupportedFileSystemException {
        super();
    }

    @Test
    public void testApplicationRecovery() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "yarn_admin_user");
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        Context context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        String appName = "app_name1";
        String appUser = "app_user1";
        String modUser = "modify_user1";
        String viewUser = "view_user1";
        String enemyUser = "enemy_user";
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        Map<String, LocalResource> localResources = Collections.emptyMap();
        Map<String, String> containerEnv = new HashMap<>();
        setFlowContext(containerEnv, appName, appId);
        List<String> containerCmds = Collections.emptyList();
        Map<String, ByteBuffer> serviceData = Collections.emptyMap();
        Credentials containerCreds = new Credentials();
        DataOutputBuffer dob = new DataOutputBuffer();
        containerCreds.writeTokenStorageToStream(dob);
        ByteBuffer containerTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        Map<ApplicationAccessType, String> acls = new HashMap<ApplicationAccessType, String>();
        acls.put(MODIFY_APP, modUser);
        acls.put(VIEW_APP, viewUser);
        ContainerLaunchContext clc = ContainerLaunchContext.newInstance(localResources, containerEnv, containerCmds, serviceData, containerTokens, acls);
        // create the logAggregationContext
        LogAggregationContext logAggregationContext = LogAggregationContext.newInstance("includePattern", "excludePattern", "includePatternInRollingAggregation", "excludePatternInRollingAggregation");
        StartContainersResponse startResponse = startContainer(context, cm, cid, clc, logAggregationContext, TASK);
        Assert.assertTrue(startResponse.getFailedRequests().isEmpty());
        Assert.assertEquals(1, context.getApplications().size());
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        waitForAppState(app, INITING);
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(modUser), MODIFY_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), MODIFY_APP, appUser, appId));
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), VIEW_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(enemyUser), VIEW_APP, appUser, appId));
        // reset container manager and verify app recovered with proper acls
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        // check whether LogAggregationContext is recovered correctly
        LogAggregationContext recovered = getLogAggregationContext();
        Assert.assertNotNull(recovered);
        Assert.assertEquals(logAggregationContext.getIncludePattern(), recovered.getIncludePattern());
        Assert.assertEquals(logAggregationContext.getExcludePattern(), recovered.getExcludePattern());
        Assert.assertEquals(logAggregationContext.getRolledLogsIncludePattern(), recovered.getRolledLogsIncludePattern());
        Assert.assertEquals(logAggregationContext.getRolledLogsExcludePattern(), recovered.getRolledLogsExcludePattern());
        waitForAppState(app, INITING);
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(modUser), MODIFY_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), MODIFY_APP, appUser, appId));
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), VIEW_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(enemyUser), VIEW_APP, appUser, appId));
        // simulate application completion
        List<ApplicationId> finishedApps = new ArrayList<ApplicationId>();
        finishedApps.add(appId);
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationFinishEvent(appId, "Application killed by ResourceManager"));
        waitForAppState(app, APPLICATION_RESOURCES_CLEANINGUP);
        // restart and verify app is marked for finishing
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        // no longer saving FINISH_APP event in NM stateStore,
        // simulate by resending FINISH_APP event
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationFinishEvent(appId, "Application killed by ResourceManager"));
        waitForAppState(app, APPLICATION_RESOURCES_CLEANINGUP);
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(modUser), MODIFY_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), MODIFY_APP, appUser, appId));
        Assert.assertTrue(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(viewUser), VIEW_APP, appUser, appId));
        Assert.assertFalse(context.getApplicationACLsManager().checkAccess(UserGroupInformation.createRemoteUser(enemyUser), VIEW_APP, appUser, appId));
        // simulate log aggregation completion
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent(app.getAppId(), ApplicationEventType.APPLICATION_RESOURCES_CLEANEDUP));
        Assert.assertEquals(app.getApplicationState(), FINISHED);
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent(app.getAppId(), ApplicationEventType.APPLICATION_LOG_HANDLING_FINISHED));
        // restart and verify app is no longer present after recovery
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertTrue(context.getApplications().isEmpty());
        cm.stop();
    }

    @Test
    public void testNMRecoveryForAppFinishedWithLogAggregationFailure() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        Context context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        Map<String, LocalResource> localResources = Collections.emptyMap();
        Map<String, String> containerEnv = new HashMap<>();
        setFlowContext(containerEnv, "app_name1", appId);
        List<String> containerCmds = Collections.emptyList();
        Map<String, ByteBuffer> serviceData = Collections.emptyMap();
        ContainerLaunchContext clc = ContainerLaunchContext.newInstance(localResources, containerEnv, containerCmds, serviceData, null, null);
        StartContainersResponse startResponse = startContainer(context, cm, cid, clc, null, TASK);
        Assert.assertTrue(startResponse.getFailedRequests().isEmpty());
        Assert.assertEquals(1, context.getApplications().size());
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        waitForAppState(app, INITING);
        // simulate application completion
        List<ApplicationId> finishedApps = new ArrayList<ApplicationId>();
        finishedApps.add(appId);
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationFinishEvent(appId, "Application killed by ResourceManager"));
        waitForAppState(app, APPLICATION_RESOURCES_CLEANINGUP);
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent(app.getAppId(), ApplicationEventType.APPLICATION_RESOURCES_CLEANEDUP));
        Assert.assertEquals(app.getApplicationState(), FINISHED);
        // application is still in NM context.
        Assert.assertEquals(1, context.getApplications().size());
        // restart and verify app is still there and marked as finished.
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        // no longer saving FINISH_APP event in NM stateStore,
        // simulate by resending FINISH_APP event
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationFinishEvent(appId, "Application killed by ResourceManager"));
        waitForAppState(app, APPLICATION_RESOURCES_CLEANINGUP);
        // TODO need to figure out why additional APPLICATION_RESOURCES_CLEANEDUP
        // is needed.
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent(app.getAppId(), ApplicationEventType.APPLICATION_RESOURCES_CLEANEDUP));
        Assert.assertEquals(app.getApplicationState(), FINISHED);
        // simulate log aggregation failed.
        app.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent(app.getAppId(), ApplicationEventType.APPLICATION_LOG_HANDLING_FAILED));
        // restart and verify app is no longer present after recovery
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertTrue(context.getApplications().isEmpty());
        cm.stop();
    }

    @Test
    public void testNodeManagerMetricsRecovery() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        Context context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context, delSrvc);
        cm.init(conf);
        cm.start();
        metrics.addResource(Resource.newInstance(10240, 8));
        // add an application by starting a container
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        Map<String, String> containerEnv = Collections.emptyMap();
        Map<String, ByteBuffer> serviceData = Collections.emptyMap();
        Map<String, LocalResource> localResources = Collections.emptyMap();
        List<String> commands = Arrays.asList("sleep 60s".split(" "));
        ContainerLaunchContext clc = ContainerLaunchContext.newInstance(localResources, containerEnv, commands, serviceData, null, null);
        StartContainersResponse startResponse = startContainer(context, cm, cid, clc, null, TASK);
        Assert.assertTrue(startResponse.getFailedRequests().isEmpty());
        Assert.assertEquals(1, context.getApplications().size());
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        // make sure the container reaches RUNNING state
        BaseContainerManagerTest.waitForNMContainerState(cm, cid, RUNNING);
        TestNodeManagerMetrics.checkMetrics(1, 0, 0, 0, 0, 1, 1, 1, 9, 1, 7);
        // restart and verify metrics could be recovered
        cm.stop();
        DefaultMetricsSystem.shutdown();
        metrics = NodeManagerMetrics.create();
        metrics.addResource(Resource.newInstance(10240, 8));
        TestNodeManagerMetrics.checkMetrics(0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 8);
        context = createContext(conf, stateStore);
        cm = createContainerManager(context, delSrvc);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        TestNodeManagerMetrics.checkMetrics(1, 0, 0, 0, 0, 1, 1, 1, 9, 1, 7);
        cm.stop();
    }

    @Test
    public void testContainerResizeRecovery() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context, delSrvc);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        commonLaunchContainer(appId, cid, cm);
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        Resource targetResource = Resource.newInstance(2048, 2);
        ContainerUpdateResponse updateResponse = updateContainers(context, cm, cid, targetResource);
        Assert.assertTrue(updateResponse.getFailedRequests().isEmpty());
        // check status
        ContainerStatus containerStatus = getContainerStatus(context, cm, cid);
        Assert.assertEquals(targetResource, containerStatus.getCapability());
        // restart and verify container is running and recovered
        // to the correct size
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        containerStatus = getContainerStatus(context, cm, cid);
        Assert.assertEquals(targetResource, containerStatus.getCapability());
        cm.stop();
    }

    @Test
    public void testContainerSchedulerRecovery() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context, delSrvc);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        commonLaunchContainer(appId, cid, cm);
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        ResourceUtilization utilization = ResourceUtilization.newInstance(1024, 2048, 1.0F);
        Assert.assertEquals(cm.getContainerScheduler().getNumRunningContainers(), 1);
        Assert.assertEquals(utilization, cm.getContainerScheduler().getCurrentUtilization());
        // restart and verify container scheduler has recovered correctly
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context, delSrvc);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        BaseContainerManagerTest.waitForNMContainerState(cm, cid, ContainerState.RUNNING);
        Assert.assertEquals(cm.getContainerScheduler().getNumRunningContainers(), 1);
        Assert.assertEquals(utilization, cm.getContainerScheduler().getCurrentUtilization());
        cm.stop();
    }

    @Test
    public void testResourceMappingRecoveryForContainer() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context, delSrvc);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        commonLaunchContainer(appId, cid, cm);
        Container nmContainer = context.getContainers().get(cid);
        Application app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        // store resource mapping of the container
        List<Serializable> gpuResources = Arrays.asList("1", "2", "3");
        stateStore.storeAssignedResources(nmContainer, "gpu", gpuResources);
        List<Serializable> numaResources = Arrays.asList("numa1");
        stateStore.storeAssignedResources(nmContainer, "numa", numaResources);
        List<Serializable> fpgaResources = Arrays.asList("fpga1", "fpga2");
        stateStore.storeAssignedResources(nmContainer, "fpga", fpgaResources);
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        ((NMContext) (context)).setContainerManager(cm);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = context.getApplications().get(appId);
        Assert.assertNotNull(app);
        Assert.assertNotNull(nmContainer);
        ResourceMappings resourceMappings = nmContainer.getResourceMappings();
        List<Serializable> assignedResource = resourceMappings.getAssignedResources("gpu");
        Assert.assertTrue(assignedResource.equals(gpuResources));
        Assert.assertTrue(resourceMappings.getAssignedResources("numa").equals(numaResources));
        Assert.assertTrue(resourceMappings.getAssignedResources("fpga").equals(fpgaResources));
        cm.stop();
    }

    @Test
    public void testContainerCleanupOnShutdown() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cid = ContainerId.newContainerId(attemptId, 1);
        Map<String, LocalResource> localResources = Collections.emptyMap();
        Map<String, String> containerEnv = new HashMap<>();
        setFlowContext(containerEnv, "app_name1", appId);
        List<String> containerCmds = Collections.emptyList();
        Map<String, ByteBuffer> serviceData = Collections.emptyMap();
        Credentials containerCreds = new Credentials();
        DataOutputBuffer dob = new DataOutputBuffer();
        containerCreds.writeTokenStorageToStream(dob);
        ByteBuffer containerTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        Map<ApplicationAccessType, String> acls = Collections.emptyMap();
        ContainerLaunchContext clc = ContainerLaunchContext.newInstance(localResources, containerEnv, containerCmds, serviceData, containerTokens, acls);
        // create the logAggregationContext
        LogAggregationContext logAggregationContext = LogAggregationContext.newInstance("includePattern", "excludePattern");
        // verify containers are stopped on shutdown without recovery
        conf.setBoolean(NM_RECOVERY_ENABLED, false);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, false);
        Context context = createContext(conf, new NMNullStateStoreService());
        ContainerManagerImpl cm = Mockito.spy(createContainerManager(context));
        cm.init(conf);
        cm.start();
        StartContainersResponse startResponse = startContainer(context, cm, cid, clc, logAggregationContext, TASK);
        Assert.assertEquals(1, startResponse.getSuccessfullyStartedContainers().size());
        cm.stop();
        Mockito.verify(cm).handle(ArgumentMatchers.isA(CMgrCompletedAppsEvent.class));
        // verify containers are stopped on shutdown with unsupervised recovery
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, false);
        NMMemoryStateStoreService memStore = new NMMemoryStateStoreService();
        memStore.init(conf);
        start();
        context = createContext(conf, memStore);
        cm = Mockito.spy(createContainerManager(context));
        cm.init(conf);
        cm.start();
        startResponse = startContainer(context, cm, cid, clc, logAggregationContext, TASK);
        Assert.assertEquals(1, startResponse.getSuccessfullyStartedContainers().size());
        cm.stop();
        close();
        Mockito.verify(cm).handle(ArgumentMatchers.isA(CMgrCompletedAppsEvent.class));
        // verify containers are not stopped on shutdown with supervised recovery
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        memStore = new NMMemoryStateStoreService();
        memStore.init(conf);
        start();
        context = createContext(conf, memStore);
        cm = Mockito.spy(createContainerManager(context));
        cm.init(conf);
        cm.start();
        startResponse = startContainer(context, cm, cid, clc, logAggregationContext, TASK);
        Assert.assertEquals(1, startResponse.getSuccessfullyStartedContainers().size());
        cm.stop();
        close();
        Mockito.verify(cm, Mockito.never()).handle(ArgumentMatchers.isA(CMgrCompletedAppsEvent.class));
    }

    @Test
    public void testApplicationRecoveryAfterFlowContextUpdated() throws Exception {
        conf.setBoolean(NM_RECOVERY_ENABLED, true);
        conf.setBoolean(NM_RECOVERY_SUPERVISED, true);
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "yarn_admin_user");
        NMStateStoreService stateStore = new NMMemoryStateStoreService();
        stateStore.init(conf);
        stateStore.start();
        Context context = createContext(conf, stateStore);
        ContainerManagerImpl cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        // add an application by starting a container
        String appName = "app_name1";
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(appId, 1);
        // create 1nd attempt container with containerId 2
        ContainerId cid = ContainerId.newContainerId(attemptId, 2);
        Map<String, LocalResource> localResources = Collections.emptyMap();
        Map<String, String> containerEnv = new HashMap<>();
        List<String> containerCmds = Collections.emptyList();
        Map<String, ByteBuffer> serviceData = Collections.emptyMap();
        Credentials containerCreds = new Credentials();
        DataOutputBuffer dob = new DataOutputBuffer();
        containerCreds.writeTokenStorageToStream(dob);
        ByteBuffer containerTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        Map<ApplicationAccessType, String> acls = new HashMap<ApplicationAccessType, String>();
        ContainerLaunchContext clc = ContainerLaunchContext.newInstance(localResources, containerEnv, containerCmds, serviceData, containerTokens, acls);
        // create the logAggregationContext
        LogAggregationContext logAggregationContext = LogAggregationContext.newInstance("includePattern", "excludePattern", "includePatternInRollingAggregation", "excludePatternInRollingAggregation");
        StartContainersResponse startResponse = startContainer(context, cm, cid, clc, logAggregationContext, TASK);
        Assert.assertTrue(startResponse.getFailedRequests().isEmpty());
        Assert.assertEquals(1, context.getApplications().size());
        ApplicationImpl app = ((ApplicationImpl) (context.getApplications().get(appId)));
        Assert.assertNotNull(app);
        waitForAppState(app, INITING);
        Assert.assertNull(app.getFlowName());
        // 2nd attempt
        ApplicationAttemptId attemptId2 = ApplicationAttemptId.newInstance(appId, 2);
        // create 2nd attempt master container
        ContainerId cid2 = ContainerId.newContainerId(attemptId, 1);
        setFlowContext(containerEnv, appName, appId);
        // once again create for updating launch context
        clc = ContainerLaunchContext.newInstance(localResources, containerEnv, containerCmds, serviceData, containerTokens, acls);
        // start container with container type AM.
        startResponse = startContainer(context, cm, cid2, clc, logAggregationContext, APPLICATION_MASTER);
        Assert.assertTrue(startResponse.getFailedRequests().isEmpty());
        Assert.assertEquals(1, context.getApplications().size());
        waitForAppState(app, INITING);
        Assert.assertEquals(appName, app.getFlowName());
        // reset container manager and verify flow context information
        cm.stop();
        context = createContext(conf, stateStore);
        cm = createContainerManager(context);
        cm.init(conf);
        cm.start();
        Assert.assertEquals(1, context.getApplications().size());
        app = ((ApplicationImpl) (context.getApplications().get(appId)));
        Assert.assertNotNull(app);
        Assert.assertEquals(appName, app.getFlowName());
        waitForAppState(app, INITING);
        cm.stop();
    }
}

