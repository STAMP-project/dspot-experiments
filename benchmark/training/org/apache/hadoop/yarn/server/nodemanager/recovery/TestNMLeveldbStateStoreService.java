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
package org.apache.hadoop.yarn.server.nodemanager.recovery;


import ContainerExitStatus.INVALID;
import ContainerManagerApplicationProto.Builder;
import LocalResourceType.ARCHIVE;
import LocalResourceType.FILE;
import LocalResourceType.PATTERN;
import LocalResourceVisibility.APPLICATION;
import LocalResourceVisibility.PRIVATE;
import LocalResourceVisibility.PUBLIC;
import RecoveredContainerStatus.COMPLETED;
import RecoveredContainerStatus.LAUNCHED;
import RecoveredContainerStatus.PAUSED;
import RecoveredContainerStatus.QUEUED;
import RecoveredContainerStatus.REQUESTED;
import RecoveredContainerType.KILL;
import YarnConfiguration.NM_RECOVERY_COMPACTION_INTERVAL_SECS;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.service.ServiceStateException;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.impl.pb.LocalResourcePBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.proto.YarnProtos.LocalResourceProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerRecoveryProtos.ContainerManagerApplicationProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerRecoveryProtos.DeletionServiceDeleteTaskProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerRecoveryProtos.LocalizedResourceProto;
import org.apache.hadoop.yarn.proto.YarnServerNodemanagerRecoveryProtos.LogDeleterProto;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.nodemanager.amrmproxy.AMRMProxyTokenSecretManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ResourceMappings;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.LocalResourceTrackerState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredAMRMProxyState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredApplicationsState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredContainerState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredContainerTokensState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredDeletionServiceState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredLocalizationState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredLogDeleterState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredNMTokensState;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredUserResources;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.server.security.BaseContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.security.BaseNMTokenSecretManager;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestNMLeveldbStateStoreService {
    private static final File TMP_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestNMLeveldbStateStoreService.class.getName());

    YarnConfiguration conf;

    NMLeveldbStateStoreService stateStore;

    @Test
    public void testIsNewlyCreated() throws IOException {
        Assert.assertTrue(stateStore.isNewlyCreated());
        restartStateStore();
        Assert.assertFalse(stateStore.isNewlyCreated());
    }

    @Test
    public void testEmptyState() throws IOException {
        Assert.assertTrue(stateStore.canRecover());
        verifyEmptyState();
    }

    @Test
    public void testCheckVersion() throws IOException {
        // default version
        Version defaultVersion = stateStore.getCurrentVersion();
        Assert.assertEquals(defaultVersion, stateStore.loadVersion());
        // compatible version
        Version compatibleVersion = Version.newInstance(defaultVersion.getMajorVersion(), ((defaultVersion.getMinorVersion()) + 2));
        stateStore.storeVersion(compatibleVersion);
        Assert.assertEquals(compatibleVersion, stateStore.loadVersion());
        restartStateStore();
        // overwrite the compatible version
        Assert.assertEquals(defaultVersion, stateStore.loadVersion());
        // incompatible version
        Version incompatibleVersion = Version.newInstance(((defaultVersion.getMajorVersion()) + 1), defaultVersion.getMinorVersion());
        stateStore.storeVersion(incompatibleVersion);
        try {
            restartStateStore();
            Assert.fail("Incompatible version, should expect fail here.");
        } catch (ServiceStateException e) {
            Assert.assertTrue("Exception message mismatch", e.getMessage().contains("Incompatible version for NM state:"));
        }
    }

    @Test
    public void testApplicationStorage() throws IOException {
        // test empty when no state
        RecoveredApplicationsState state = stateStore.loadApplicationsState();
        List<ContainerManagerApplicationProto> apps = loadApplicationProtos(state.getIterator());
        Assert.assertTrue(apps.isEmpty());
        // store an application and verify recovered
        final ApplicationId appId1 = ApplicationId.newInstance(1234, 1);
        ContainerManagerApplicationProto.Builder builder = ContainerManagerApplicationProto.newBuilder();
        builder.setId(getProto());
        builder.setUser("user1");
        ContainerManagerApplicationProto appProto1 = builder.build();
        stateStore.storeApplication(appId1, appProto1);
        restartStateStore();
        state = stateStore.loadApplicationsState();
        apps = loadApplicationProtos(state.getIterator());
        Assert.assertEquals(1, apps.size());
        Assert.assertEquals(appProto1, apps.get(0));
        // add a new app
        final ApplicationId appId2 = ApplicationId.newInstance(1234, 2);
        builder = ContainerManagerApplicationProto.newBuilder();
        builder.setId(getProto());
        builder.setUser("user2");
        ContainerManagerApplicationProto appProto2 = builder.build();
        stateStore.storeApplication(appId2, appProto2);
        restartStateStore();
        state = stateStore.loadApplicationsState();
        apps = loadApplicationProtos(state.getIterator());
        Assert.assertEquals(2, apps.size());
        Assert.assertTrue(apps.contains(appProto1));
        Assert.assertTrue(apps.contains(appProto2));
        // test removing an application
        stateStore.removeApplication(appId2);
        restartStateStore();
        state = stateStore.loadApplicationsState();
        apps = loadApplicationProtos(state.getIterator());
        Assert.assertEquals(1, apps.size());
        Assert.assertEquals(appProto1, apps.get(0));
    }

    @Test
    public void testContainerStorage() throws IOException {
        // test empty when no state
        List<RecoveredContainerState> recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertTrue(recoveredContainers.isEmpty());
        // create a container request
        ApplicationId appId = ApplicationId.newInstance(1234, 3);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 4);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 5);
        Resource containerResource = Resource.newInstance(1024, 2);
        StartContainerRequest containerReq = createContainerRequest(containerId, containerResource);
        // store a container and verify recovered
        long containerStartTime = System.currentTimeMillis();
        stateStore.storeContainer(containerId, 0, containerStartTime, containerReq);
        // verify the container version key is not stored for new containers
        DB db = stateStore.getDB();
        Assert.assertNull("version key present for new container", db.get(bytes(stateStore.getContainerVersionKey(containerId.toString()))));
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        RecoveredContainerState rcs = recoveredContainers.get(0);
        Assert.assertEquals(0, rcs.getVersion());
        Assert.assertEquals(containerStartTime, rcs.getStartTime());
        Assert.assertEquals(REQUESTED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(containerReq, rcs.getStartRequest());
        Assert.assertTrue(rcs.getDiagnostics().isEmpty());
        Assert.assertEquals(containerResource, rcs.getCapability());
        // store a new container record without StartContainerRequest
        ContainerId containerId1 = ContainerId.newContainerId(appAttemptId, 6);
        stateStore.storeContainerLaunched(containerId1);
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        // check whether the new container record is discarded
        Assert.assertEquals(1, recoveredContainers.size());
        // queue the container, and verify recovered
        stateStore.storeContainerQueued(containerId);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(QUEUED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(containerReq, rcs.getStartRequest());
        Assert.assertTrue(rcs.getDiagnostics().isEmpty());
        Assert.assertEquals(containerResource, rcs.getCapability());
        // launch the container, add some diagnostics, and verify recovered
        StringBuilder diags = new StringBuilder();
        stateStore.storeContainerLaunched(containerId);
        diags.append("some diags for container");
        stateStore.storeContainerDiagnostics(containerId, diags);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(LAUNCHED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(containerReq, rcs.getStartRequest());
        Assert.assertEquals(diags.toString(), rcs.getDiagnostics());
        Assert.assertEquals(containerResource, rcs.getCapability());
        // pause the container, and verify recovered
        stateStore.storeContainerPaused(containerId);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(PAUSED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(containerReq, rcs.getStartRequest());
        // Resume the container
        stateStore.removeContainerPaused(containerId);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        // increase the container size, and verify recovered
        ContainerTokenIdentifier updateTokenIdentifier = new ContainerTokenIdentifier(containerId, "host", "user", Resource.newInstance(2468, 4), 9876543210L, 42, 2468, Priority.newInstance(7), 13579);
        stateStore.storeContainerUpdateToken(containerId, updateTokenIdentifier);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(0, rcs.getVersion());
        Assert.assertEquals(LAUNCHED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(Resource.newInstance(2468, 4), rcs.getCapability());
        // mark the container killed, add some more diags, and verify recovered
        diags.append("some more diags for container");
        stateStore.storeContainerDiagnostics(containerId, diags);
        stateStore.storeContainerKilled(containerId);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(LAUNCHED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertTrue(rcs.getKilled());
        ContainerTokenIdentifier tokenReadFromRequest = BuilderUtils.newContainerTokenIdentifier(rcs.getStartRequest().getContainerToken());
        Assert.assertEquals(updateTokenIdentifier, tokenReadFromRequest);
        Assert.assertEquals(diags.toString(), rcs.getDiagnostics());
        // add yet more diags, mark container completed, and verify recovered
        diags.append("some final diags");
        stateStore.storeContainerDiagnostics(containerId, diags);
        stateStore.storeContainerCompleted(containerId, 21);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(COMPLETED, rcs.getStatus());
        Assert.assertEquals(21, rcs.getExitCode());
        Assert.assertTrue(rcs.getKilled());
        Assert.assertEquals(diags.toString(), rcs.getDiagnostics());
        // store remainingRetryAttempts, workDir and logDir
        stateStore.storeContainerRemainingRetryAttempts(containerId, 6);
        stateStore.storeContainerWorkDir(containerId, "/test/workdir");
        stateStore.storeContainerLogDir(containerId, "/test/logdir");
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        rcs = recoveredContainers.get(0);
        Assert.assertEquals(6, rcs.getRemainingRetryAttempts());
        Assert.assertEquals("/test/workdir", rcs.getWorkDir());
        Assert.assertEquals("/test/logdir", rcs.getLogDir());
        validateRetryAttempts(containerId);
        // remove the container and verify not recovered
        stateStore.removeContainer(containerId);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertTrue(recoveredContainers.isEmpty());
        // recover again to check remove clears all containers
        restartStateStore();
        NMStateStoreService nmStoreSpy = Mockito.spy(stateStore);
        loadContainersState(nmStoreSpy.getContainerStateIterator());
        Mockito.verify(nmStoreSpy, Mockito.times(0)).removeContainer(ArgumentMatchers.any(ContainerId.class));
    }

    @Test
    public void testLocalTrackerStateIterator() throws IOException {
        String user1 = "somebody";
        ApplicationId appId1 = ApplicationId.newInstance(1, 1);
        ApplicationId appId2 = ApplicationId.newInstance(2, 2);
        String user2 = "someone";
        ApplicationId appId3 = ApplicationId.newInstance(3, 3);
        // start and finish local resource for applications
        Path appRsrcPath1 = new Path("hdfs://some/app/resource1");
        LocalResourcePBImpl rsrcPb1 = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath1), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto1 = rsrcPb1.getProto();
        Path appRsrcLocalPath1 = new Path("/some/local/dir/for/apprsrc1");
        Path appRsrcPath2 = new Path("hdfs://some/app/resource2");
        LocalResourcePBImpl rsrcPb2 = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath2), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto2 = rsrcPb2.getProto();
        Path appRsrcLocalPath2 = new Path("/some/local/dir/for/apprsrc2");
        Path appRsrcPath3 = new Path("hdfs://some/app/resource3");
        LocalResourcePBImpl rsrcPb3 = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath3), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto3 = rsrcPb3.getProto();
        Path appRsrcLocalPath3 = new Path("/some/local/dir/for/apprsrc2");
        stateStore.startResourceLocalization(user1, appId1, appRsrcProto1, appRsrcLocalPath1);
        stateStore.startResourceLocalization(user1, appId2, appRsrcProto2, appRsrcLocalPath2);
        stateStore.startResourceLocalization(user2, appId3, appRsrcProto3, appRsrcLocalPath3);
        LocalizedResourceProto appLocalizedProto1 = LocalizedResourceProto.newBuilder().setResource(appRsrcProto1).setLocalPath(appRsrcLocalPath1.toString()).setSize(1234567L).build();
        LocalizedResourceProto appLocalizedProto2 = LocalizedResourceProto.newBuilder().setResource(appRsrcProto2).setLocalPath(appRsrcLocalPath2.toString()).setSize(1234567L).build();
        LocalizedResourceProto appLocalizedProto3 = LocalizedResourceProto.newBuilder().setResource(appRsrcProto3).setLocalPath(appRsrcLocalPath3.toString()).setSize(1234567L).build();
        stateStore.finishResourceLocalization(user1, appId1, appLocalizedProto1);
        stateStore.finishResourceLocalization(user1, appId2, appLocalizedProto2);
        stateStore.finishResourceLocalization(user2, appId3, appLocalizedProto3);
        List<LocalizedResourceProto> completedResources = new ArrayList<LocalizedResourceProto>();
        Map<LocalResourceProto, Path> startedResources = new HashMap<LocalResourceProto, Path>();
        // restart and verify two users exist and two apps completed for user1.
        restartStateStore();
        RecoveredLocalizationState state = stateStore.loadLocalizationState();
        Map<String, RecoveredUserResources> userResources = loadUserResources(state.getIterator());
        Assert.assertEquals(2, userResources.size());
        RecoveredUserResources uResource = userResources.get(user1);
        Assert.assertEquals(2, uResource.getAppTrackerStates().size());
        LocalResourceTrackerState app1ts = uResource.getAppTrackerStates().get(appId1);
        Assert.assertNotNull(app1ts);
        completedResources = loadCompletedResources(app1ts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(app1ts.getStartedResourcesIterator());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(appLocalizedProto1, completedResources.iterator().next());
        LocalResourceTrackerState app2ts = uResource.getAppTrackerStates().get(appId2);
        Assert.assertNotNull(app2ts);
        completedResources = loadCompletedResources(app2ts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(app2ts.getStartedResourcesIterator());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(appLocalizedProto2, completedResources.iterator().next());
    }

    @Test
    public void testStartResourceLocalization() throws IOException {
        String user = "somebody";
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        // start a local resource for an application
        Path appRsrcPath = new Path("hdfs://some/app/resource");
        LocalResourcePBImpl rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto = rsrcPb.getProto();
        Path appRsrcLocalPath = new Path("/some/local/dir/for/apprsrc");
        stateStore.startResourceLocalization(user, appId, appRsrcProto, appRsrcLocalPath);
        List<LocalizedResourceProto> completedResources = new ArrayList<LocalizedResourceProto>();
        Map<LocalResourceProto, Path> startedResources = new HashMap<LocalResourceProto, Path>();
        // restart and verify only app resource is marked in-progress
        restartStateStore();
        RecoveredLocalizationState state = stateStore.loadLocalizationState();
        LocalResourceTrackerState pubts = state.getPublicTrackerState();
        completedResources = loadCompletedResources(pubts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(pubts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertTrue(startedResources.isEmpty());
        Map<String, RecoveredUserResources> userResources = loadUserResources(state.getIterator());
        Assert.assertEquals(1, userResources.size());
        RecoveredUserResources rur = userResources.get(user);
        LocalResourceTrackerState privts = rur.getPrivateTrackerState();
        Assert.assertNotNull(privts);
        completedResources = loadCompletedResources(privts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(privts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, rur.getAppTrackerStates().size());
        LocalResourceTrackerState appts = rur.getAppTrackerStates().get(appId);
        Assert.assertNotNull(appts);
        completedResources = loadCompletedResources(appts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(appts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertEquals(1, startedResources.size());
        Assert.assertEquals(appRsrcLocalPath, startedResources.get(appRsrcProto));
        // start some public and private resources
        Path pubRsrcPath1 = new Path("hdfs://some/public/resource1");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath1), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto1 = rsrcPb.getProto();
        Path pubRsrcLocalPath1 = new Path("/some/local/dir/for/pubrsrc1");
        stateStore.startResourceLocalization(null, null, pubRsrcProto1, pubRsrcLocalPath1);
        Path pubRsrcPath2 = new Path("hdfs://some/public/resource2");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath2), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto2 = rsrcPb.getProto();
        Path pubRsrcLocalPath2 = new Path("/some/local/dir/for/pubrsrc2");
        stateStore.startResourceLocalization(null, null, pubRsrcProto2, pubRsrcLocalPath2);
        Path privRsrcPath = new Path("hdfs://some/private/resource");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(privRsrcPath), PATTERN, PRIVATE, 789L, 680L, "*pattern*")));
        LocalResourceProto privRsrcProto = rsrcPb.getProto();
        Path privRsrcLocalPath = new Path("/some/local/dir/for/privrsrc");
        stateStore.startResourceLocalization(user, null, privRsrcProto, privRsrcLocalPath);
        // restart and verify resources are marked in-progress
        restartStateStore();
        state = stateStore.loadLocalizationState();
        pubts = state.getPublicTrackerState();
        completedResources = loadCompletedResources(pubts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(pubts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertEquals(2, startedResources.size());
        Assert.assertEquals(pubRsrcLocalPath1, startedResources.get(pubRsrcProto1));
        Assert.assertEquals(pubRsrcLocalPath2, startedResources.get(pubRsrcProto2));
        userResources = loadUserResources(state.getIterator());
        Assert.assertEquals(1, userResources.size());
        rur = userResources.get(user);
        privts = rur.getPrivateTrackerState();
        Assert.assertNotNull(privts);
        completedResources = loadCompletedResources(privts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(privts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertEquals(1, startedResources.size());
        Assert.assertEquals(privRsrcLocalPath, startedResources.get(privRsrcProto));
        Assert.assertEquals(1, rur.getAppTrackerStates().size());
        appts = rur.getAppTrackerStates().get(appId);
        Assert.assertNotNull(appts);
        completedResources = loadCompletedResources(appts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(appts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertEquals(1, startedResources.size());
        Assert.assertEquals(appRsrcLocalPath, startedResources.get(appRsrcProto));
    }

    @Test
    public void testFinishResourceLocalization() throws IOException {
        String user = "somebody";
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        // start and finish a local resource for an application
        Path appRsrcPath = new Path("hdfs://some/app/resource");
        LocalResourcePBImpl rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto = rsrcPb.getProto();
        Path appRsrcLocalPath = new Path("/some/local/dir/for/apprsrc");
        stateStore.startResourceLocalization(user, appId, appRsrcProto, appRsrcLocalPath);
        LocalizedResourceProto appLocalizedProto = LocalizedResourceProto.newBuilder().setResource(appRsrcProto).setLocalPath(appRsrcLocalPath.toString()).setSize(1234567L).build();
        stateStore.finishResourceLocalization(user, appId, appLocalizedProto);
        List<LocalizedResourceProto> completedResources = new ArrayList<LocalizedResourceProto>();
        Map<LocalResourceProto, Path> startedResources = new HashMap<LocalResourceProto, Path>();
        // restart and verify only app resource is completed
        restartStateStore();
        RecoveredLocalizationState state = stateStore.loadLocalizationState();
        LocalResourceTrackerState pubts = state.getPublicTrackerState();
        completedResources = loadCompletedResources(pubts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(pubts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertTrue(startedResources.isEmpty());
        Map<String, RecoveredUserResources> userResources = loadUserResources(state.getIterator());
        Assert.assertEquals(1, userResources.size());
        RecoveredUserResources rur = userResources.get(user);
        LocalResourceTrackerState privts = rur.getPrivateTrackerState();
        Assert.assertNotNull(privts);
        completedResources = loadCompletedResources(privts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(privts.getStartedResourcesIterator());
        Assert.assertTrue(completedResources.isEmpty());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, rur.getAppTrackerStates().size());
        LocalResourceTrackerState appts = rur.getAppTrackerStates().get(appId);
        Assert.assertNotNull(appts);
        completedResources = loadCompletedResources(appts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(appts.getStartedResourcesIterator());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(appLocalizedProto, completedResources.iterator().next());
        // start some public and private resources
        Path pubRsrcPath1 = new Path("hdfs://some/public/resource1");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath1), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto1 = rsrcPb.getProto();
        Path pubRsrcLocalPath1 = new Path("/some/local/dir/for/pubrsrc1");
        stateStore.startResourceLocalization(null, null, pubRsrcProto1, pubRsrcLocalPath1);
        Path pubRsrcPath2 = new Path("hdfs://some/public/resource2");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath2), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto2 = rsrcPb.getProto();
        Path pubRsrcLocalPath2 = new Path("/some/local/dir/for/pubrsrc2");
        stateStore.startResourceLocalization(null, null, pubRsrcProto2, pubRsrcLocalPath2);
        Path privRsrcPath = new Path("hdfs://some/private/resource");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(privRsrcPath), PATTERN, PRIVATE, 789L, 680L, "*pattern*")));
        LocalResourceProto privRsrcProto = rsrcPb.getProto();
        Path privRsrcLocalPath = new Path("/some/local/dir/for/privrsrc");
        stateStore.startResourceLocalization(user, null, privRsrcProto, privRsrcLocalPath);
        // finish some of the resources
        LocalizedResourceProto pubLocalizedProto1 = LocalizedResourceProto.newBuilder().setResource(pubRsrcProto1).setLocalPath(pubRsrcLocalPath1.toString()).setSize(pubRsrcProto1.getSize()).build();
        stateStore.finishResourceLocalization(null, null, pubLocalizedProto1);
        LocalizedResourceProto privLocalizedProto = LocalizedResourceProto.newBuilder().setResource(privRsrcProto).setLocalPath(privRsrcLocalPath.toString()).setSize(privRsrcProto.getSize()).build();
        stateStore.finishResourceLocalization(user, null, privLocalizedProto);
        // restart and verify state
        restartStateStore();
        state = stateStore.loadLocalizationState();
        pubts = state.getPublicTrackerState();
        completedResources = loadCompletedResources(pubts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(pubts.getStartedResourcesIterator());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(pubLocalizedProto1, completedResources.iterator().next());
        Assert.assertEquals(1, startedResources.size());
        Assert.assertEquals(pubRsrcLocalPath2, startedResources.get(pubRsrcProto2));
        userResources = loadUserResources(state.getIterator());
        Assert.assertEquals(1, userResources.size());
        rur = userResources.get(user);
        privts = rur.getPrivateTrackerState();
        Assert.assertNotNull(privts);
        completedResources = loadCompletedResources(privts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(privts.getStartedResourcesIterator());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(privLocalizedProto, completedResources.iterator().next());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, rur.getAppTrackerStates().size());
        appts = rur.getAppTrackerStates().get(appId);
        Assert.assertNotNull(appts);
        completedResources = loadCompletedResources(appts.getCompletedResourcesIterator());
        startedResources = loadStartedResources(appts.getStartedResourcesIterator());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(appLocalizedProto, completedResources.iterator().next());
    }

    @Test
    public void testRemoveLocalizedResource() throws IOException {
        String user = "somebody";
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        // go through the complete lifecycle for an application local resource
        Path appRsrcPath = new Path("hdfs://some/app/resource");
        LocalResourcePBImpl rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(appRsrcPath), ARCHIVE, APPLICATION, 123L, 456L)));
        LocalResourceProto appRsrcProto = rsrcPb.getProto();
        Path appRsrcLocalPath = new Path("/some/local/dir/for/apprsrc");
        stateStore.startResourceLocalization(user, appId, appRsrcProto, appRsrcLocalPath);
        LocalizedResourceProto appLocalizedProto = LocalizedResourceProto.newBuilder().setResource(appRsrcProto).setLocalPath(appRsrcLocalPath.toString()).setSize(1234567L).build();
        stateStore.finishResourceLocalization(user, appId, appLocalizedProto);
        stateStore.removeLocalizedResource(user, appId, appRsrcLocalPath);
        restartStateStore();
        verifyEmptyState();
        // remove an app resource that didn't finish
        stateStore.startResourceLocalization(user, appId, appRsrcProto, appRsrcLocalPath);
        stateStore.removeLocalizedResource(user, appId, appRsrcLocalPath);
        restartStateStore();
        verifyEmptyState();
        // add public and private resources and remove some
        Path pubRsrcPath1 = new Path("hdfs://some/public/resource1");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath1), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto1 = rsrcPb.getProto();
        Path pubRsrcLocalPath1 = new Path("/some/local/dir/for/pubrsrc1");
        stateStore.startResourceLocalization(null, null, pubRsrcProto1, pubRsrcLocalPath1);
        LocalizedResourceProto pubLocalizedProto1 = LocalizedResourceProto.newBuilder().setResource(pubRsrcProto1).setLocalPath(pubRsrcLocalPath1.toString()).setSize(789L).build();
        stateStore.finishResourceLocalization(null, null, pubLocalizedProto1);
        Path pubRsrcPath2 = new Path("hdfs://some/public/resource2");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(pubRsrcPath2), FILE, PUBLIC, 789L, 135L)));
        LocalResourceProto pubRsrcProto2 = rsrcPb.getProto();
        Path pubRsrcLocalPath2 = new Path("/some/local/dir/for/pubrsrc2");
        stateStore.startResourceLocalization(null, null, pubRsrcProto2, pubRsrcLocalPath2);
        LocalizedResourceProto pubLocalizedProto2 = LocalizedResourceProto.newBuilder().setResource(pubRsrcProto2).setLocalPath(pubRsrcLocalPath2.toString()).setSize(7654321L).build();
        stateStore.finishResourceLocalization(null, null, pubLocalizedProto2);
        stateStore.removeLocalizedResource(null, null, pubRsrcLocalPath2);
        Path privRsrcPath = new Path("hdfs://some/private/resource");
        rsrcPb = ((LocalResourcePBImpl) (LocalResource.newInstance(URL.fromPath(privRsrcPath), PATTERN, PRIVATE, 789L, 680L, "*pattern*")));
        LocalResourceProto privRsrcProto = rsrcPb.getProto();
        Path privRsrcLocalPath = new Path("/some/local/dir/for/privrsrc");
        stateStore.startResourceLocalization(user, null, privRsrcProto, privRsrcLocalPath);
        stateStore.removeLocalizedResource(user, null, privRsrcLocalPath);
        // restart and verify state
        restartStateStore();
        RecoveredLocalizationState state = stateStore.loadLocalizationState();
        LocalResourceTrackerState pubts = state.getPublicTrackerState();
        List<LocalizedResourceProto> completedResources = loadCompletedResources(pubts.getCompletedResourcesIterator());
        Map<LocalResourceProto, Path> startedResources = loadStartedResources(pubts.getStartedResourcesIterator());
        Assert.assertTrue(startedResources.isEmpty());
        Assert.assertEquals(1, completedResources.size());
        Assert.assertEquals(pubLocalizedProto1, completedResources.iterator().next());
        Map<String, RecoveredUserResources> userResources = loadUserResources(state.getIterator());
        Assert.assertTrue(userResources.isEmpty());
    }

    @Test
    public void testDeletionTaskStorage() throws IOException {
        // test empty when no state
        RecoveredDeletionServiceState state = stateStore.loadDeletionServiceState();
        List<DeletionServiceDeleteTaskProto> deleteTaskProtos = loadDeletionTaskProtos(state.getIterator());
        Assert.assertTrue(deleteTaskProtos.isEmpty());
        // store a deletion task and verify recovered
        DeletionServiceDeleteTaskProto proto = DeletionServiceDeleteTaskProto.newBuilder().setId(7).setUser("someuser").setSubdir("some/subdir").addBasedirs("some/dir/path").addBasedirs("some/other/dir/path").setDeletionTime(123456L).addSuccessorIds(8).addSuccessorIds(9).build();
        stateStore.storeDeletionTask(proto.getId(), proto);
        restartStateStore();
        state = stateStore.loadDeletionServiceState();
        deleteTaskProtos = loadDeletionTaskProtos(state.getIterator());
        Assert.assertEquals(1, deleteTaskProtos.size());
        Assert.assertEquals(proto, deleteTaskProtos.get(0));
        // store another deletion task
        DeletionServiceDeleteTaskProto proto2 = DeletionServiceDeleteTaskProto.newBuilder().setId(8).setUser("user2").setSubdir("subdir2").setDeletionTime(789L).build();
        stateStore.storeDeletionTask(proto2.getId(), proto2);
        restartStateStore();
        state = stateStore.loadDeletionServiceState();
        deleteTaskProtos = loadDeletionTaskProtos(state.getIterator());
        Assert.assertEquals(2, deleteTaskProtos.size());
        Assert.assertTrue(deleteTaskProtos.contains(proto));
        Assert.assertTrue(deleteTaskProtos.contains(proto2));
        // delete a task and verify gone after recovery
        stateStore.removeDeletionTask(proto2.getId());
        restartStateStore();
        state = stateStore.loadDeletionServiceState();
        deleteTaskProtos = loadDeletionTaskProtos(state.getIterator());
        Assert.assertEquals(1, deleteTaskProtos.size());
        Assert.assertEquals(proto, deleteTaskProtos.get(0));
        // delete the last task and verify none left
        stateStore.removeDeletionTask(proto.getId());
        restartStateStore();
        state = stateStore.loadDeletionServiceState();
        deleteTaskProtos = loadDeletionTaskProtos(state.getIterator());
        Assert.assertTrue(deleteTaskProtos.isEmpty());
    }

    @Test
    public void testNMTokenStorage() throws IOException {
        // test empty when no state
        RecoveredNMTokensState state = stateStore.loadNMTokensState();
        Map<ApplicationAttemptId, MasterKey> loadedAppKeys = loadNMTokens(state.getIterator());
        Assert.assertNull(state.getCurrentMasterKey());
        Assert.assertNull(state.getPreviousMasterKey());
        Assert.assertTrue(loadedAppKeys.isEmpty());
        // store a master key and verify recovered
        TestNMLeveldbStateStoreService.NMTokenSecretManagerForTest secretMgr = new TestNMLeveldbStateStoreService.NMTokenSecretManagerForTest();
        MasterKey currentKey = secretMgr.generateKey();
        stateStore.storeNMTokenCurrentMasterKey(currentKey);
        restartStateStore();
        state = stateStore.loadNMTokensState();
        loadedAppKeys = loadNMTokens(state.getIterator());
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertNull(state.getPreviousMasterKey());
        Assert.assertTrue(loadedAppKeys.isEmpty());
        // store a previous key and verify recovered
        MasterKey prevKey = secretMgr.generateKey();
        stateStore.storeNMTokenPreviousMasterKey(prevKey);
        restartStateStore();
        state = stateStore.loadNMTokensState();
        loadedAppKeys = loadNMTokens(state.getIterator());
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertTrue(loadedAppKeys.isEmpty());
        // store a few application keys and verify recovered
        ApplicationAttemptId attempt1 = ApplicationAttemptId.newInstance(ApplicationId.newInstance(1, 1), 1);
        MasterKey attemptKey1 = secretMgr.generateKey();
        stateStore.storeNMTokenApplicationMasterKey(attempt1, attemptKey1);
        ApplicationAttemptId attempt2 = ApplicationAttemptId.newInstance(ApplicationId.newInstance(2, 3), 4);
        MasterKey attemptKey2 = secretMgr.generateKey();
        stateStore.storeNMTokenApplicationMasterKey(attempt2, attemptKey2);
        restartStateStore();
        state = stateStore.loadNMTokensState();
        loadedAppKeys = loadNMTokens(state.getIterator());
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertEquals(2, loadedAppKeys.size());
        Assert.assertEquals(attemptKey1, loadedAppKeys.get(attempt1));
        Assert.assertEquals(attemptKey2, loadedAppKeys.get(attempt2));
        // add/update/remove keys and verify recovered
        ApplicationAttemptId attempt3 = ApplicationAttemptId.newInstance(ApplicationId.newInstance(5, 6), 7);
        MasterKey attemptKey3 = secretMgr.generateKey();
        stateStore.storeNMTokenApplicationMasterKey(attempt3, attemptKey3);
        stateStore.removeNMTokenApplicationMasterKey(attempt1);
        attemptKey2 = prevKey;
        stateStore.storeNMTokenApplicationMasterKey(attempt2, attemptKey2);
        prevKey = currentKey;
        stateStore.storeNMTokenPreviousMasterKey(prevKey);
        currentKey = secretMgr.generateKey();
        stateStore.storeNMTokenCurrentMasterKey(currentKey);
        restartStateStore();
        state = stateStore.loadNMTokensState();
        loadedAppKeys = loadNMTokens(state.getIterator());
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertEquals(2, loadedAppKeys.size());
        Assert.assertNull(loadedAppKeys.get(attempt1));
        Assert.assertEquals(attemptKey2, loadedAppKeys.get(attempt2));
        Assert.assertEquals(attemptKey3, loadedAppKeys.get(attempt3));
    }

    @Test
    public void testContainerTokenStorage() throws IOException {
        // test empty when no state
        RecoveredContainerTokensState state = stateStore.loadContainerTokensState();
        Map<ContainerId, Long> loadedActiveTokens = loadContainerTokens(state.it);
        Assert.assertNull(state.getCurrentMasterKey());
        Assert.assertNull(state.getPreviousMasterKey());
        Assert.assertTrue(loadedActiveTokens.isEmpty());
        // store a master key and verify recovered
        TestNMLeveldbStateStoreService.ContainerTokenKeyGeneratorForTest keygen = new TestNMLeveldbStateStoreService.ContainerTokenKeyGeneratorForTest(new YarnConfiguration());
        MasterKey currentKey = keygen.generateKey();
        stateStore.storeContainerTokenCurrentMasterKey(currentKey);
        restartStateStore();
        state = stateStore.loadContainerTokensState();
        loadedActiveTokens = loadContainerTokens(state.it);
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertNull(state.getPreviousMasterKey());
        Assert.assertTrue(loadedActiveTokens.isEmpty());
        // store a previous key and verify recovered
        MasterKey prevKey = keygen.generateKey();
        stateStore.storeContainerTokenPreviousMasterKey(prevKey);
        restartStateStore();
        state = stateStore.loadContainerTokensState();
        loadedActiveTokens = loadContainerTokens(state.it);
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertTrue(loadedActiveTokens.isEmpty());
        // store a few container tokens and verify recovered
        ContainerId cid1 = BuilderUtils.newContainerId(1, 1, 1, 1);
        Long expTime1 = 1234567890L;
        ContainerId cid2 = BuilderUtils.newContainerId(2, 2, 2, 2);
        Long expTime2 = 9876543210L;
        stateStore.storeContainerToken(cid1, expTime1);
        stateStore.storeContainerToken(cid2, expTime2);
        restartStateStore();
        state = stateStore.loadContainerTokensState();
        loadedActiveTokens = loadContainerTokens(state.it);
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertEquals(2, loadedActiveTokens.size());
        Assert.assertEquals(expTime1, loadedActiveTokens.get(cid1));
        Assert.assertEquals(expTime2, loadedActiveTokens.get(cid2));
        // add/update/remove tokens and verify recovered
        ContainerId cid3 = BuilderUtils.newContainerId(3, 3, 3, 3);
        Long expTime3 = 135798642L;
        stateStore.storeContainerToken(cid3, expTime3);
        stateStore.removeContainerToken(cid1);
        expTime2 += 246897531L;
        stateStore.storeContainerToken(cid2, expTime2);
        prevKey = currentKey;
        stateStore.storeContainerTokenPreviousMasterKey(prevKey);
        currentKey = keygen.generateKey();
        stateStore.storeContainerTokenCurrentMasterKey(currentKey);
        restartStateStore();
        state = stateStore.loadContainerTokensState();
        loadedActiveTokens = loadContainerTokens(state.it);
        Assert.assertEquals(currentKey, state.getCurrentMasterKey());
        Assert.assertEquals(prevKey, state.getPreviousMasterKey());
        Assert.assertEquals(2, loadedActiveTokens.size());
        Assert.assertNull(loadedActiveTokens.get(cid1));
        Assert.assertEquals(expTime2, loadedActiveTokens.get(cid2));
        Assert.assertEquals(expTime3, loadedActiveTokens.get(cid3));
    }

    @Test
    public void testLogDeleterStorage() throws IOException {
        // test empty when no state
        RecoveredLogDeleterState state = stateStore.loadLogDeleterState();
        Assert.assertTrue(state.getLogDeleterMap().isEmpty());
        // store log deleter state
        final ApplicationId appId1 = ApplicationId.newInstance(1, 1);
        LogDeleterProto proto1 = LogDeleterProto.newBuilder().setUser("user1").setDeletionTime(1234).build();
        stateStore.storeLogDeleter(appId1, proto1);
        // restart state store and verify recovered
        restartStateStore();
        state = stateStore.loadLogDeleterState();
        Assert.assertEquals(1, state.getLogDeleterMap().size());
        Assert.assertEquals(proto1, state.getLogDeleterMap().get(appId1));
        // store another log deleter
        final ApplicationId appId2 = ApplicationId.newInstance(2, 2);
        LogDeleterProto proto2 = LogDeleterProto.newBuilder().setUser("user2").setDeletionTime(5678).build();
        stateStore.storeLogDeleter(appId2, proto2);
        // restart state store and verify recovered
        restartStateStore();
        state = stateStore.loadLogDeleterState();
        Assert.assertEquals(2, state.getLogDeleterMap().size());
        Assert.assertEquals(proto1, state.getLogDeleterMap().get(appId1));
        Assert.assertEquals(proto2, state.getLogDeleterMap().get(appId2));
        // remove a deleter and verify removed after restart and recovery
        stateStore.removeLogDeleter(appId1);
        restartStateStore();
        state = stateStore.loadLogDeleterState();
        Assert.assertEquals(1, state.getLogDeleterMap().size());
        Assert.assertEquals(proto2, state.getLogDeleterMap().get(appId2));
        // remove last deleter and verify empty after restart and recovery
        stateStore.removeLogDeleter(appId2);
        restartStateStore();
        state = stateStore.loadLogDeleterState();
        Assert.assertTrue(state.getLogDeleterMap().isEmpty());
    }

    @Test
    public void testCompactionCycle() throws IOException {
        final DB mockdb = Mockito.mock(DB.class);
        conf.setInt(NM_RECOVERY_COMPACTION_INTERVAL_SECS, 1);
        NMLeveldbStateStoreService store = new NMLeveldbStateStoreService() {
            @Override
            protected void checkVersion() {
            }

            @Override
            protected DB openDatabase(Configuration conf) {
                return mockdb;
            }
        };
        store.init(conf);
        store.start();
        Mockito.verify(mockdb, Mockito.timeout(10000).atLeastOnce()).compactRange(((byte[]) (ArgumentMatchers.isNull())), ((byte[]) (ArgumentMatchers.isNull())));
        store.close();
    }

    @Test
    public void testUnexpectedKeyDoesntThrowException() throws IOException {
        // test empty when no state
        List<RecoveredContainerState> recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertTrue(recoveredContainers.isEmpty());
        ApplicationId appId = ApplicationId.newInstance(1234, 3);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 4);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 5);
        StartContainerRequest startContainerRequest = storeMockContainer(containerId);
        // add a invalid key
        byte[] invalidKey = (("ContainerManager/containers/" + (containerId.toString())) + "/invalidKey1234").getBytes();
        stateStore.getDB().put(invalidKey, new byte[1]);
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        RecoveredContainerState rcs = recoveredContainers.get(0);
        Assert.assertEquals(REQUESTED, rcs.getStatus());
        Assert.assertEquals(INVALID, rcs.getExitCode());
        Assert.assertEquals(false, rcs.getKilled());
        Assert.assertEquals(startContainerRequest, rcs.getStartRequest());
        Assert.assertTrue(rcs.getDiagnostics().isEmpty());
        Assert.assertEquals(KILL, rcs.getRecoveryType());
        // assert unknown keys are cleaned up finally
        Assert.assertNotNull(stateStore.getDB().get(invalidKey));
        stateStore.removeContainer(containerId);
        Assert.assertNull(stateStore.getDB().get(invalidKey));
    }

    @Test
    public void testAMRMProxyStorage() throws IOException {
        RecoveredAMRMProxyState state = stateStore.loadAMRMProxyState();
        Assert.assertEquals(state.getCurrentMasterKey(), null);
        Assert.assertEquals(state.getNextMasterKey(), null);
        Assert.assertEquals(state.getAppContexts().size(), 0);
        ApplicationId appId1 = ApplicationId.newInstance(1, 1);
        ApplicationId appId2 = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId attemptId1 = ApplicationAttemptId.newInstance(appId1, 1);
        ApplicationAttemptId attemptId2 = ApplicationAttemptId.newInstance(appId2, 2);
        String key1 = "key1";
        String key2 = "key2";
        byte[] data1 = "data1".getBytes();
        byte[] data2 = "data2".getBytes();
        AMRMProxyTokenSecretManager secretManager = new AMRMProxyTokenSecretManager(stateStore);
        secretManager.init(conf);
        // Generate currentMasterKey
        secretManager.start();
        try {
            // Add two applications, each with two data entries
            stateStore.storeAMRMProxyAppContextEntry(attemptId1, key1, data1);
            stateStore.storeAMRMProxyAppContextEntry(attemptId2, key1, data1);
            stateStore.storeAMRMProxyAppContextEntry(attemptId1, key2, data2);
            stateStore.storeAMRMProxyAppContextEntry(attemptId2, key2, data2);
            // restart state store and verify recovered
            restartStateStore();
            secretManager.setNMStateStoreService(stateStore);
            state = stateStore.loadAMRMProxyState();
            Assert.assertEquals(state.getCurrentMasterKey(), secretManager.getCurrentMasterKeyData().getMasterKey());
            Assert.assertEquals(state.getNextMasterKey(), null);
            Assert.assertEquals(state.getAppContexts().size(), 2);
            // app1
            Map<String, byte[]> map = state.getAppContexts().get(attemptId1);
            Assert.assertNotEquals(map, null);
            Assert.assertEquals(map.size(), 2);
            Assert.assertTrue(Arrays.equals(map.get(key1), data1));
            Assert.assertTrue(Arrays.equals(map.get(key2), data2));
            // app2
            map = state.getAppContexts().get(attemptId2);
            Assert.assertNotEquals(map, null);
            Assert.assertEquals(map.size(), 2);
            Assert.assertTrue(Arrays.equals(map.get(key1), data1));
            Assert.assertTrue(Arrays.equals(map.get(key2), data2));
            // Generate next master key and remove one entry of app2
            secretManager.rollMasterKey();
            stateStore.removeAMRMProxyAppContextEntry(attemptId2, key1);
            // restart state store and verify recovered
            restartStateStore();
            secretManager.setNMStateStoreService(stateStore);
            state = stateStore.loadAMRMProxyState();
            Assert.assertEquals(state.getCurrentMasterKey(), secretManager.getCurrentMasterKeyData().getMasterKey());
            Assert.assertEquals(state.getNextMasterKey(), secretManager.getNextMasterKeyData().getMasterKey());
            Assert.assertEquals(state.getAppContexts().size(), 2);
            // app1
            map = state.getAppContexts().get(attemptId1);
            Assert.assertNotEquals(map, null);
            Assert.assertEquals(map.size(), 2);
            Assert.assertTrue(Arrays.equals(map.get(key1), data1));
            Assert.assertTrue(Arrays.equals(map.get(key2), data2));
            // app2
            map = state.getAppContexts().get(attemptId2);
            Assert.assertNotEquals(map, null);
            Assert.assertEquals(map.size(), 1);
            Assert.assertTrue(Arrays.equals(map.get(key2), data2));
            // Activate next master key and remove all entries of app1
            secretManager.activateNextMasterKey();
            stateStore.removeAMRMProxyAppContext(attemptId1);
            // restart state store and verify recovered
            restartStateStore();
            secretManager.setNMStateStoreService(stateStore);
            state = stateStore.loadAMRMProxyState();
            Assert.assertEquals(state.getCurrentMasterKey(), secretManager.getCurrentMasterKeyData().getMasterKey());
            Assert.assertEquals(state.getNextMasterKey(), null);
            Assert.assertEquals(state.getAppContexts().size(), 1);
            // app2 only
            map = state.getAppContexts().get(attemptId2);
            Assert.assertNotEquals(map, null);
            Assert.assertEquals(map.size(), 1);
            Assert.assertTrue(Arrays.equals(map.get(key2), data2));
        } finally {
            secretManager.stop();
        }
    }

    @Test
    public void testStateStoreForResourceMapping() throws IOException {
        // test empty when no state
        List<RecoveredContainerState> recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertTrue(recoveredContainers.isEmpty());
        ApplicationId appId = ApplicationId.newInstance(1234, 3);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 4);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 5);
        storeMockContainer(containerId);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getContainerId()).thenReturn(containerId);
        ResourceMappings resourceMappings = new ResourceMappings();
        Mockito.when(container.getResourceMappings()).thenReturn(resourceMappings);
        // Store ResourceMapping
        stateStore.storeAssignedResources(container, "gpu", Arrays.asList("1", "2", "3"));
        // This will overwrite above
        List<Serializable> gpuRes1 = Arrays.asList("1", "2", "4");
        stateStore.storeAssignedResources(container, "gpu", gpuRes1);
        List<Serializable> fpgaRes = Arrays.asList("3", "4", "5", "6");
        stateStore.storeAssignedResources(container, "fpga", fpgaRes);
        List<Serializable> numaRes = Arrays.asList("numa1");
        stateStore.storeAssignedResources(container, "numa", numaRes);
        // add a invalid key
        restartStateStore();
        recoveredContainers = loadContainersState(stateStore.getContainerStateIterator());
        Assert.assertEquals(1, recoveredContainers.size());
        RecoveredContainerState rcs = recoveredContainers.get(0);
        List<Serializable> res = rcs.getResourceMappings().getAssignedResources("gpu");
        Assert.assertTrue(res.equals(gpuRes1));
        Assert.assertTrue(resourceMappings.getAssignedResources("gpu").equals(gpuRes1));
        res = rcs.getResourceMappings().getAssignedResources("fpga");
        Assert.assertTrue(res.equals(fpgaRes));
        Assert.assertTrue(resourceMappings.getAssignedResources("fpga").equals(fpgaRes));
        res = rcs.getResourceMappings().getAssignedResources("numa");
        Assert.assertTrue(res.equals(numaRes));
        Assert.assertTrue(resourceMappings.getAssignedResources("numa").equals(numaRes));
    }

    @Test
    public void testStateStoreNodeHealth() throws IOException {
        // keep the working DB clean, break a temp DB
        DB keepDB = stateStore.getDB();
        DB myMocked = Mockito.mock(DB.class);
        stateStore.setDB(myMocked);
        ApplicationId appId = ApplicationId.newInstance(1234, 1);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        DBException toThrow = new DBException();
        Mockito.doThrow(toThrow).when(myMocked).put(ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class));
        // write some data
        try {
            // chosen a simple method could be any of the "void" methods
            ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);
            stateStore.storeContainerKilled(containerId);
        } catch (IOException ioErr) {
            // Cause should be wrapped DBException
            Assert.assertTrue(((ioErr.getCause()) instanceof DBException));
            // check the store is marked unhealthy
            Assert.assertFalse("Statestore should have been unhealthy", stateStore.isHealthy());
            return;
        } finally {
            // restore the working DB
            stateStore.setDB(keepDB);
        }
        Assert.fail("Expected exception not thrown");
    }

    @Test
    public void testEmptyRestartTimes() throws IOException {
        List<Long> restartTimes = new ArrayList<>();
        ApplicationId appId = ApplicationId.newInstance(1234, 3);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 4);
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 5);
        storeMockContainer(containerId);
        stateStore.storeContainerRestartTimes(containerId, restartTimes);
        restartStateStore();
        RecoveredContainerState rcs = loadContainersState(stateStore.getContainerStateIterator()).get(0);
        List<Long> recoveredRestartTimes = rcs.getRestartTimes();
        Assert.assertTrue(recoveredRestartTimes.isEmpty());
    }

    private static class NMTokenSecretManagerForTest extends BaseNMTokenSecretManager {
        public MasterKey generateKey() {
            return createNewMasterKey().getMasterKey();
        }
    }

    private static class ContainerTokenKeyGeneratorForTest extends BaseContainerTokenSecretManager {
        public ContainerTokenKeyGeneratorForTest(Configuration conf) {
            super(conf);
        }

        public MasterKey generateKey() {
            return createNewMasterKey().getMasterKey();
        }
    }
}

