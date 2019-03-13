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


import ApplicationState.FINISHED;
import CMgrCompletedAppsEvent.Reason;
import ContainerEventType.CONTAINER_EXITED_WITH_FAILURE;
import ContainerEventType.CONTAINER_KILLED_ON_REQUEST;
import ContainerEventType.CONTAINER_LAUNCHED;
import ContainerEventType.INIT_CONTAINER;
import ContainerEventType.REINITIALIZE_CONTAINER;
import ContainerEventType.RESOURCE_FAILED;
import ContainerEventType.RESOURCE_LOCALIZED;
import ContainerEventType.ROLLBACK_REINIT;
import ContainerEventType.UPDATE_DIAGNOSTICS_MSG;
import ContainerManagerImpl.INVALID_CONTAINERTOKEN_MSG;
import ContainerManagerImpl.INVALID_NMTOKEN_MSG;
import ContainerState.COMPLETE;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import LocalizationState.COMPLETED;
import ResourceManagerConstants.RM_INVALID_IDENTIFIER;
import SignalContainerCommand.FORCEFUL_SHUTDOWN;
import SignalContainerCommand.GRACEFUL_SHUTDOWN;
import SignalContainerCommand.OUTPUT_THREAD_DUMP;
import YarnConfiguration.NM_AUX_SERVICES;
import YarnConfiguration.NM_AUX_SERVICE_FMT;
import YarnConfiguration.NM_LOCAL_DIRS;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ResourceLocalizationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerStatusesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StartContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StopContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalizationStatus;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.InvalidContainerException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.security.NMTokenIdentifier;
import org.apache.hadoop.yarn.server.api.AuxiliaryLocalPathHandler;
import org.apache.hadoop.yarn.server.api.ResourceManagerConstants;
import org.apache.hadoop.yarn.server.nodemanager.ContainerStateTransitionListener;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.DefaultContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.LOCALIZING;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.NEW;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.REINITIALIZING;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.REINITIALIZING_AWAITING_KILL;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.RUNNING;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState.SCHEDULED;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestContainerManager extends BaseContainerManagerTest {
    public TestContainerManager() throws UnsupportedFileSystemException {
        super();
    }

    static {
        BaseContainerManagerTest.LOG = LoggerFactory.getLogger(TestContainerManager.class);
    }

    private static class Listener implements ContainerStateTransitionListener {
        private final Map<ContainerId, List<org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState>> states = new HashMap<>();

        private final Map<ContainerId, List<ContainerEventType>> events = new HashMap<>();

        @Override
        public void init(Context context) {
        }

        @Override
        public void preTransition(ContainerImpl op, org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState beforeState, ContainerEvent eventToBeProcessed) {
            if (!(states.containsKey(op.getContainerId()))) {
                states.put(op.getContainerId(), new ArrayList());
                states.get(op.getContainerId()).add(beforeState);
                events.put(op.getContainerId(), new ArrayList());
            }
        }

        @Override
        public void postTransition(ContainerImpl op, org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState beforeState, org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState afterState, ContainerEvent processedEvent) {
            states.get(op.getContainerId()).add(afterState);
            events.get(op.getContainerId()).add(processedEvent.getType());
        }
    }

    private boolean delayContainers = false;

    @Test
    public void testContainerManagerInitialization() throws IOException {
        containerManager.start();
        InetAddress localAddr = InetAddress.getLocalHost();
        String fqdn = localAddr.getCanonicalHostName();
        if (!(localAddr.getHostAddress().equals(fqdn))) {
            // only check if fqdn is not same as ip
            // api returns ip in case of resolution failure
            Assert.assertEquals(fqdn, context.getNodeId().getHost());
        }
        // Just do a query for a non-existing container.
        boolean throwsException = false;
        try {
            List<ContainerId> containerIds = new ArrayList<>();
            ContainerId id = BaseContainerManagerTest.createContainerId(0);
            containerIds.add(id);
            GetContainerStatusesRequest request = GetContainerStatusesRequest.newInstance(containerIds);
            GetContainerStatusesResponse response = containerManager.getContainerStatuses(request);
            if (response.getFailedRequests().containsKey(id)) {
                throw response.getFailedRequests().get(id).deSerialize();
            }
        } catch (Throwable e) {
            throwsException = true;
        }
        Assert.assertTrue(throwsException);
    }

    @Test
    public void testContainerSetup() throws Exception {
        containerManager.start();
        // ////// Create the resources for the container
        File dir = new File(BaseContainerManagerTest.tmpDir, "dir");
        dir.mkdirs();
        File file = new File(dir, "file");
        PrintWriter fileWriter = new PrintWriter(file);
        fileWriter.write("Hello World!");
        fileWriter.close();
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        // ////// Construct the container-spec.
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        URL resource_alpha = URL.fromPath(BaseContainerManagerTest.localFS.makeQualified(new Path(file.getAbsolutePath())));
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(file.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, COMPLETE, 40);
        // Now ascertain that the resources are localised correctly.
        ApplicationId appId = cId.getApplicationAttemptId().getApplicationId();
        String appIDStr = appId.toString();
        String containerIDStr = cId.toString();
        File userCacheDir = new File(BaseContainerManagerTest.localDir, ContainerLocalizer.USERCACHE);
        File userDir = new File(userCacheDir, user);
        File appCache = new File(userDir, ContainerLocalizer.APPCACHE);
        File appDir = new File(appCache, appIDStr);
        File containerDir = new File(appDir, containerIDStr);
        File targetFile = new File(containerDir, destinationFile);
        File sysDir = new File(BaseContainerManagerTest.localDir, ResourceLocalizationService.NM_PRIVATE_DIR);
        File appSysDir = new File(sysDir, appIDStr);
        File containerSysDir = new File(appSysDir, containerIDStr);
        for (File f : new File[]{ BaseContainerManagerTest.localDir, sysDir, userCacheDir, appDir, appSysDir, containerDir, containerSysDir }) {
            Assert.assertTrue(((f.getAbsolutePath()) + " doesn't exist!!"), f.exists());
            Assert.assertTrue(((f.getAbsolutePath()) + " is not a directory!!"), f.isDirectory());
        }
        Assert.assertTrue(((targetFile.getAbsolutePath()) + " doesn't exist!!"), targetFile.exists());
        // Now verify the contents of the file
        BufferedReader reader = new BufferedReader(new FileReader(targetFile));
        Assert.assertEquals("Hello World!", reader.readLine());
        Assert.assertEquals(null, reader.readLine());
    }

    @Test(timeout = 10000L)
    public void testAuxPathHandler() throws Exception {
        File testDir = GenericTestUtils.getTestDir(((TestContainerManager.class.getSimpleName()) + "LocDir"));
        testDir.mkdirs();
        File testFile = new File(testDir, "test");
        testFile.createNewFile();
        YarnConfiguration configuration = new YarnConfiguration();
        configuration.set(NM_LOCAL_DIRS, testDir.getAbsolutePath());
        LocalDirsHandlerService spyDirHandlerService = Mockito.spy(new LocalDirsHandlerService());
        spyDirHandlerService.init(configuration);
        Mockito.when(spyDirHandlerService.getConfig()).thenReturn(configuration);
        AuxiliaryLocalPathHandler auxiliaryLocalPathHandler = new ContainerManagerImpl.AuxiliaryLocalPathHandlerImpl(spyDirHandlerService);
        Path p = auxiliaryLocalPathHandler.getLocalPathForRead("test");
        Assert.assertTrue(((p != null) && (!(spyDirHandlerService.getLocalDirsForRead().isEmpty()))));
        Mockito.when(spyDirHandlerService.getLocalDirsForRead()).thenReturn(new ArrayList<String>());
        try {
            auxiliaryLocalPathHandler.getLocalPathForRead("test");
            Assert.fail("Should not have passed!");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Could not find"));
        } finally {
            testFile.delete();
            testDir.delete();
        }
    }

    @Test
    public void testContainerRestart() throws IOException, InterruptedException, YarnException {
        containerManager.start();
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        File oldStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_o.txt").getAbsoluteFile();
        String pid = prepareInitialContainer(cId, oldStartFile);
        // Test that the container can restart
        // Also, Since there was no rollback context present before the
        // restart, rollback should NOT be possible after the restart
        doRestartTests(cId, oldStartFile, "Hello World!", pid, false);
    }

    @Test
    public void testContainerUpgradeSuccessAutoCommit() throws IOException, InterruptedException, YarnException {
        TestContainerManager.Listener listener = new TestContainerManager.Listener();
        addListener(listener);
        testContainerReInitSuccess(true);
        // Should not be able to Commit (since already auto committed)
        try {
            containerManager.commitLastReInitialization(BaseContainerManagerTest.createContainerId(0));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Nothing to Commit"));
        }
        List<org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState> containerStates = listener.states.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(NEW, LOCALIZING, SCHEDULED, RUNNING, REINITIALIZING, REINITIALIZING_AWAITING_KILL, REINITIALIZING_AWAITING_KILL, SCHEDULED, RUNNING), containerStates);
        List<ContainerEventType> containerEventTypes = listener.events.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(INIT_CONTAINER, RESOURCE_LOCALIZED, CONTAINER_LAUNCHED, REINITIALIZE_CONTAINER, RESOURCE_LOCALIZED, UPDATE_DIAGNOSTICS_MSG, CONTAINER_KILLED_ON_REQUEST, CONTAINER_LAUNCHED), containerEventTypes);
    }

    @Test
    public void testContainerUpgradeSuccessExplicitCommit() throws IOException, InterruptedException, YarnException {
        testContainerReInitSuccess(false);
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        containerManager.commitLastReInitialization(cId);
        // Should not be able to Rollback once committed
        try {
            containerManager.rollbackLastReInitialization(cId);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Nothing to rollback to"));
        }
    }

    @Test
    public void testContainerUpgradeSuccessExplicitRollback() throws IOException, InterruptedException, YarnException {
        TestContainerManager.Listener listener = new TestContainerManager.Listener();
        addListener(listener);
        String[] pids = testContainerReInitSuccess(false);
        // Test that the container can be Restarted after the successful upgrrade.
        // Also, since there is a rollback context present before the restart, it
        // should be possible to rollback the container AFTER the restart.
        pids[1] = doRestartTests(BaseContainerManagerTest.createContainerId(0), new File(BaseContainerManagerTest.tmpDir, "start_file_n.txt").getAbsoluteFile(), "Upgrade World!", pids[1], true);
        // Delete the old start File..
        File oldStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_o.txt").getAbsoluteFile();
        oldStartFile.delete();
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        // Explicit Rollback
        containerManager.rollbackLastReInitialization(cId);
        Container container = containerManager.getContext().getContainers().get(cId);
        Assert.assertTrue(container.isReInitializing());
        // Original should be dead anyway
        Assert.assertFalse("Original Process is still alive!", DefaultContainerExecutor.containerIsAlive(pids[0]));
        // Wait for new container to startup
        int timeoutSecs = 0;
        while ((container.isReInitializing()) && ((timeoutSecs++) < 20)) {
            Thread.sleep(1000);
            BaseContainerManagerTest.LOG.info("Waiting for ReInitialization to complete..");
        } 
        Assert.assertFalse(container.isReInitializing());
        timeoutSecs = 0;
        // Wait for new processStartfile to be created
        while ((!(oldStartFile.exists())) && ((timeoutSecs++) < 20)) {
            Thread.sleep(1000);
            BaseContainerManagerTest.LOG.info("Waiting for New process start-file to be created");
        } 
        // Now verify the contents of the file
        BufferedReader reader = new BufferedReader(new FileReader(oldStartFile));
        Assert.assertEquals("Hello World!", reader.readLine());
        // Get the pid of the process
        String rolledBackPid = reader.readLine().trim();
        // No more lines
        Assert.assertEquals(null, reader.readLine());
        Assert.assertNotEquals("The Rolled-back process should be a different pid", pids[0], rolledBackPid);
        List<org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState> containerStates = listener.states.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(// This is the successful restart
        // This is the rollback
        Arrays.asList(NEW, LOCALIZING, SCHEDULED, RUNNING, REINITIALIZING, REINITIALIZING_AWAITING_KILL, REINITIALIZING_AWAITING_KILL, SCHEDULED, RUNNING, REINITIALIZING_AWAITING_KILL, REINITIALIZING_AWAITING_KILL, SCHEDULED, RUNNING, REINITIALIZING_AWAITING_KILL, REINITIALIZING_AWAITING_KILL, SCHEDULED, RUNNING), containerStates);
        List<ContainerEventType> containerEventTypes = listener.events.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(INIT_CONTAINER, RESOURCE_LOCALIZED, CONTAINER_LAUNCHED, REINITIALIZE_CONTAINER, RESOURCE_LOCALIZED, UPDATE_DIAGNOSTICS_MSG, CONTAINER_KILLED_ON_REQUEST, CONTAINER_LAUNCHED, REINITIALIZE_CONTAINER, UPDATE_DIAGNOSTICS_MSG, CONTAINER_KILLED_ON_REQUEST, CONTAINER_LAUNCHED, ROLLBACK_REINIT, UPDATE_DIAGNOSTICS_MSG, CONTAINER_KILLED_ON_REQUEST, CONTAINER_LAUNCHED), containerEventTypes);
    }

    @Test
    public void testContainerUpgradeLocalizationFailure() throws IOException, InterruptedException, YarnException {
        if (Shell.WINDOWS) {
            return;
        }
        containerManager.start();
        TestContainerManager.Listener listener = new TestContainerManager.Listener();
        addListener(listener);
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        File oldStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_o.txt").getAbsoluteFile();
        String pid = prepareInitialContainer(cId, oldStartFile);
        File newStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_n.txt").getAbsoluteFile();
        prepareContainerUpgrade(false, true, true, cId, newStartFile);
        // Assert that the First process is STILL alive
        // since upgrade was terminated..
        Assert.assertTrue("Process is NOT alive!", DefaultContainerExecutor.containerIsAlive(pid));
        List<org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState> containerStates = listener.states.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(NEW, LOCALIZING, SCHEDULED, RUNNING, REINITIALIZING, RUNNING), containerStates);
        List<ContainerEventType> containerEventTypes = listener.events.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(INIT_CONTAINER, RESOURCE_LOCALIZED, CONTAINER_LAUNCHED, REINITIALIZE_CONTAINER, RESOURCE_FAILED), containerEventTypes);
    }

    @Test
    public void testContainerUpgradeProcessFailure() throws IOException, InterruptedException, YarnException {
        if (Shell.WINDOWS) {
            return;
        }
        containerManager.start();
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        File oldStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_o.txt").getAbsoluteFile();
        String pid = prepareInitialContainer(cId, oldStartFile);
        File newStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_n.txt").getAbsoluteFile();
        // Since Autocommit is true, there is also no rollback context...
        // which implies that if the new process fails, since there is no
        // rollback, it is terminated.
        prepareContainerUpgrade(true, true, false, cId, newStartFile);
        // Assert that the First process is not alive anymore
        Assert.assertFalse("Process is still alive!", DefaultContainerExecutor.containerIsAlive(pid));
    }

    @Test
    public void testContainerUpgradeRollbackDueToFailure() throws IOException, InterruptedException, YarnException {
        if (Shell.WINDOWS) {
            return;
        }
        containerManager.start();
        TestContainerManager.Listener listener = new TestContainerManager.Listener();
        addListener(listener);
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        File oldStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_o.txt").getAbsoluteFile();
        String pid = prepareInitialContainer(cId, oldStartFile);
        File newStartFile = new File(BaseContainerManagerTest.tmpDir, "start_file_n.txt").getAbsoluteFile();
        prepareContainerUpgrade(false, true, false, cId, newStartFile);
        // Assert that the First process is not alive anymore
        Assert.assertFalse("Original Process is still alive!", DefaultContainerExecutor.containerIsAlive(pid));
        int timeoutSecs = 0;
        // Wait for oldStartFile to be created
        while ((!(oldStartFile.exists())) && ((timeoutSecs++) < 20)) {
            System.out.println(("\nFiles: " + (Arrays.toString(oldStartFile.getParentFile().list()))));
            Thread.sleep(1000);
            BaseContainerManagerTest.LOG.info("Waiting for New process start-file to be created");
        } 
        // Now verify the contents of the file
        BufferedReader reader = new BufferedReader(new FileReader(oldStartFile));
        Assert.assertEquals("Hello World!", reader.readLine());
        // Get the pid of the process
        String rolledBackPid = reader.readLine().trim();
        // No more lines
        Assert.assertEquals(null, reader.readLine());
        Assert.assertNotEquals("The Rolled-back process should be a different pid", pid, rolledBackPid);
        List<org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState> containerStates = listener.states.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(NEW, LOCALIZING, SCHEDULED, RUNNING, REINITIALIZING, REINITIALIZING_AWAITING_KILL, REINITIALIZING_AWAITING_KILL, SCHEDULED, RUNNING, RUNNING, SCHEDULED, RUNNING), containerStates);
        List<ContainerEventType> containerEventTypes = listener.events.get(BaseContainerManagerTest.createContainerId(0));
        Assert.assertEquals(Arrays.asList(INIT_CONTAINER, RESOURCE_LOCALIZED, CONTAINER_LAUNCHED, REINITIALIZE_CONTAINER, RESOURCE_LOCALIZED, UPDATE_DIAGNOSTICS_MSG, CONTAINER_KILLED_ON_REQUEST, CONTAINER_LAUNCHED, UPDATE_DIAGNOSTICS_MSG, CONTAINER_EXITED_WITH_FAILURE, CONTAINER_LAUNCHED), containerEventTypes);
    }

    @Test
    public void testContainerLaunchAndExitSuccess() throws IOException, InterruptedException, YarnException {
        containerManager.start();
        int exitCode = 0;
        // launch context for a command that will return exit code 0
        // and verify exit code returned
        testContainerLaunchAndExit(exitCode);
    }

    @Test
    public void testContainerLaunchAndExitFailure() throws IOException, InterruptedException, YarnException {
        containerManager.start();
        int exitCode = 50;
        // launch context for a command that will return exit code 0
        // and verify exit code returned
        testContainerLaunchAndExit(exitCode);
    }

    // Start the container
    // While the container is running, localize new resources.
    // Verify the symlink is created properly
    @Test
    public void testLocalizingResourceWhileContainerRunning() throws Exception {
        // Real del service
        delSrvc = new org.apache.hadoop.yarn.server.nodemanager.DeletionService(exec);
        delSrvc.init(conf);
        ((NodeManager.NMContext) (context)).setContainerExecutor(exec);
        containerManager = createContainerManager(delSrvc);
        containerManager.init(conf);
        containerManager.start();
        // set up local resources
        Map<String, LocalResource> localResource = setupLocalResources("file", "symLink1");
        ContainerLaunchContext context = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        context.setLocalResources(localResource);
        // a long running container - sleep
        context.setCommands(Arrays.asList("sleep 6"));
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        // start the container
        StartContainerRequest scRequest = StartContainerRequest.newInstance(context, BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, this.context.getNodeId(), user, this.context.getContainerTokenSecretManager()));
        StartContainersRequest allRequests = StartContainersRequest.newInstance(Arrays.asList(scRequest));
        containerManager.startContainers(allRequests);
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, ContainerState.RUNNING);
        BaseContainerManagerTest.waitForApplicationState(containerManager, cId.getApplicationAttemptId().getApplicationId(), ApplicationState.RUNNING);
        checkResourceLocalized(cId, "symLink1");
        // Localize new local resources while container is running
        Map<String, LocalResource> localResource2 = setupLocalResources("file2", "symLink2");
        ResourceLocalizationRequest request = ResourceLocalizationRequest.newInstance(cId, localResource2);
        containerManager.localize(request);
        // Verify resource is localized and symlink is created.
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            public Boolean get() {
                try {
                    checkResourceLocalized(cId, "symLink2");
                    return true;
                } catch (Throwable e) {
                    return false;
                }
            }
        }, 500, 20000);
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, COMPLETE);
        // Verify container cannot localize resources while at non-running state.
        try {
            containerManager.localize(request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot perform LOCALIZE"));
        }
    }

    @Test
    public void testLocalFilesCleanup() throws IOException, InterruptedException, YarnException {
        // Real del service
        delSrvc = new org.apache.hadoop.yarn.server.nodemanager.DeletionService(exec);
        delSrvc.init(conf);
        containerManager = createContainerManager(delSrvc);
        containerManager.init(conf);
        containerManager.start();
        // ////// Create the resources for the container
        File dir = new File(BaseContainerManagerTest.tmpDir, "dir");
        dir.mkdirs();
        File file = new File(dir, "file");
        PrintWriter fileWriter = new PrintWriter(file);
        fileWriter.write("Hello World!");
        fileWriter.close();
        // ////// Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        ApplicationId appId = cId.getApplicationAttemptId().getApplicationId();
        // ////// Construct the container-spec.
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        // containerLaunchContext.resources =
        // new HashMap<CharSequence, LocalResource>();
        URL resource_alpha = URL.fromPath(FileContext.getLocalFSFileContext().makeQualified(new Path(file.getAbsolutePath())));
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(file.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        BaseContainerManagerTest.waitForContainerState(containerManager, cId, COMPLETE);
        BaseContainerManagerTest.waitForApplicationState(containerManager, cId.getApplicationAttemptId().getApplicationId(), ApplicationState.RUNNING);
        // Now ascertain that the resources are localised correctly.
        String appIDStr = appId.toString();
        String containerIDStr = cId.toString();
        File userCacheDir = new File(BaseContainerManagerTest.localDir, ContainerLocalizer.USERCACHE);
        File userDir = new File(userCacheDir, user);
        File appCache = new File(userDir, ContainerLocalizer.APPCACHE);
        File appDir = new File(appCache, appIDStr);
        File containerDir = new File(appDir, containerIDStr);
        File targetFile = new File(containerDir, destinationFile);
        File sysDir = new File(BaseContainerManagerTest.localDir, ResourceLocalizationService.NM_PRIVATE_DIR);
        File appSysDir = new File(sysDir, appIDStr);
        File containerSysDir = new File(appSysDir, containerIDStr);
        // AppDir should still exist
        Assert.assertTrue((("AppDir " + (appDir.getAbsolutePath())) + " doesn't exist!!"), appDir.exists());
        Assert.assertTrue((("AppSysDir " + (appSysDir.getAbsolutePath())) + " doesn't exist!!"), appSysDir.exists());
        for (File f : new File[]{ containerDir, containerSysDir }) {
            Assert.assertFalse(((f.getAbsolutePath()) + " exists!!"), f.exists());
        }
        Assert.assertFalse(((targetFile.getAbsolutePath()) + " exists!!"), targetFile.exists());
        // Simulate RM sending an AppFinish event.
        containerManager.handle(new org.apache.hadoop.yarn.server.nodemanager.CMgrCompletedAppsEvent(Arrays.asList(new ApplicationId[]{ appId }), Reason.ON_SHUTDOWN));
        BaseContainerManagerTest.waitForApplicationState(containerManager, cId.getApplicationAttemptId().getApplicationId(), FINISHED);
        // Now ascertain that the resources are localised correctly.
        for (File f : new File[]{ appDir, containerDir, appSysDir, containerSysDir }) {
            // Wait for deletion. Deletion can happen long after AppFinish because of
            // the async DeletionService
            int timeout = 0;
            while ((f.exists()) && ((timeout++) < 15)) {
                Thread.sleep(1000);
            } 
            Assert.assertFalse(((f.getAbsolutePath()) + " exists!!"), f.exists());
        }
        // Wait for deletion
        int timeout = 0;
        while ((targetFile.exists()) && ((timeout++) < 15)) {
            Thread.sleep(1000);
        } 
        Assert.assertFalse(((targetFile.getAbsolutePath()) + " exists!!"), targetFile.exists());
    }

    @Test
    public void testContainerLaunchFromPreviousRM() throws IOException, InterruptedException, YarnException {
        containerManager.start();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ContainerId cId1 = BaseContainerManagerTest.createContainerId(0);
        ContainerId cId2 = BaseContainerManagerTest.createContainerId(0);
        containerLaunchContext.setLocalResources(new HashMap<String, LocalResource>());
        // Construct the Container with Invalid RMIdentifier
        StartContainerRequest startRequest1 = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId1, RM_INVALID_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<>();
        list.add(startRequest1);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        boolean catchException = false;
        try {
            StartContainersResponse response = containerManager.startContainers(allRequests);
            if (response.getFailedRequests().containsKey(cId1)) {
                throw response.getFailedRequests().get(cId1).deSerialize();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            catchException = true;
            Assert.assertTrue(e.getMessage().contains((("Container " + cId1) + " rejected as it is allocated by a previous RM")));
            Assert.assertTrue(e.getClass().getName().equalsIgnoreCase(InvalidContainerException.class.getName()));
        }
        // Verify that startContainer fail because of invalid container request
        Assert.assertTrue(catchException);
        // Construct the Container with a RMIdentifier within current RM
        StartContainerRequest startRequest2 = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId2, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list2 = new ArrayList<>();
        list.add(startRequest2);
        StartContainersRequest allRequests2 = StartContainersRequest.newInstance(list2);
        containerManager.startContainers(allRequests2);
        boolean noException = true;
        try {
            containerManager.startContainers(allRequests2);
        } catch (YarnException e) {
            noException = false;
        }
        // Verify that startContainer get no YarnException
        Assert.assertTrue(noException);
    }

    @Test
    public void testMultipleContainersLaunch() throws Exception {
        containerManager.start();
        List<StartContainerRequest> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContainerId cId = BaseContainerManagerTest.createContainerId(i);
            long identifier = 0;
            // container with even id fail
            if ((i & 1) == 0)
                identifier = ResourceManagerConstants.RM_INVALID_IDENTIFIER;
            else
                identifier = DUMMY_RM_IDENTIFIER;

            Token containerToken = BaseContainerManagerTest.createContainerToken(cId, identifier, context.getNodeId(), user, context.getContainerTokenSecretManager());
            StartContainerRequest request = StartContainerRequest.newInstance(BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class), containerToken);
            list.add(request);
        }
        StartContainersRequest requestList = StartContainersRequest.newInstance(list);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Thread.sleep(5000);
        Assert.assertEquals(5, response.getSuccessfullyStartedContainers().size());
        for (ContainerId id : response.getSuccessfullyStartedContainers()) {
            // Containers with odd id should succeed.
            Assert.assertEquals(1, ((id.getContainerId()) & 1));
        }
        Assert.assertEquals(5, response.getFailedRequests().size());
        for (Map.Entry<ContainerId, SerializedException> entry : response.getFailedRequests().entrySet()) {
            // Containers with even id should fail.
            Assert.assertEquals(0, ((entry.getKey().getContainerId()) & 1));
            Assert.assertTrue(entry.getValue().getMessage().contains((("Container " + (entry.getKey())) + " rejected as it is allocated by a previous RM")));
        }
    }

    @Test
    public void testMultipleContainersStopAndGetStatus() throws Exception {
        containerManager.start();
        List<StartContainerRequest> startRequest = new ArrayList<>();
        List<ContainerId> containerIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContainerId cId;
            if ((i & 1) == 0) {
                // Containers with even id belong to an unauthorized app
                cId = BaseContainerManagerTest.createContainerId(i, 1);
            } else {
                cId = BaseContainerManagerTest.createContainerId(i, 0);
            }
            Token containerToken = BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
            StartContainerRequest request = StartContainerRequest.newInstance(BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class), containerToken);
            startRequest.add(request);
            containerIds.add(cId);
        }
        // start containers
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        containerManager.startContainers(requestList);
        Thread.sleep(5000);
        // Get container statuses
        GetContainerStatusesRequest statusRequest = GetContainerStatusesRequest.newInstance(containerIds);
        GetContainerStatusesResponse statusResponse = containerManager.getContainerStatuses(statusRequest);
        Assert.assertEquals(5, statusResponse.getContainerStatuses().size());
        for (ContainerStatus status : statusResponse.getContainerStatuses()) {
            // Containers with odd id should succeed
            Assert.assertEquals(1, ((status.getContainerId().getContainerId()) & 1));
        }
        Assert.assertEquals(5, statusResponse.getFailedRequests().size());
        for (Map.Entry<ContainerId, SerializedException> entry : statusResponse.getFailedRequests().entrySet()) {
            // Containers with even id should fail.
            Assert.assertEquals(0, ((entry.getKey().getContainerId()) & 1));
            Assert.assertTrue(entry.getValue().getMessage().contains("attempted to get status for non-application container"));
        }
        // stop containers
        StopContainersRequest stopRequest = StopContainersRequest.newInstance(containerIds);
        StopContainersResponse stopResponse = containerManager.stopContainers(stopRequest);
        Assert.assertEquals(5, stopResponse.getSuccessfullyStoppedContainers().size());
        for (ContainerId id : stopResponse.getSuccessfullyStoppedContainers()) {
            // Containers with odd id should succeed.
            Assert.assertEquals(1, ((id.getContainerId()) & 1));
        }
        Assert.assertEquals(5, stopResponse.getFailedRequests().size());
        for (Map.Entry<ContainerId, SerializedException> entry : stopResponse.getFailedRequests().entrySet()) {
            // Containers with even id should fail.
            Assert.assertEquals(0, ((entry.getKey().getContainerId()) & 1));
            Assert.assertTrue(entry.getValue().getMessage().contains("attempted to stop non-application container"));
        }
    }

    @Test
    public void testUnauthorizedRequests() throws IOException, YarnException {
        containerManager.start();
        // Create a containerId that belongs to an unauthorized appId
        ContainerId cId = BaseContainerManagerTest.createContainerId(0, 1);
        // startContainers()
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        StartContainersResponse startResponse = containerManager.startContainers(allRequests);
        Assert.assertFalse("Should not be authorized to start container", startResponse.getSuccessfullyStartedContainers().contains(cId));
        Assert.assertTrue("Start container request should fail", startResponse.getFailedRequests().containsKey(cId));
        // Insert the containerId into context, make it as if it is running
        ContainerTokenIdentifier containerTokenIdentifier = BuilderUtils.newContainerTokenIdentifier(scRequest.getContainerToken());
        Container container = new ContainerImpl(conf, null, containerLaunchContext, null, metrics, containerTokenIdentifier, context);
        context.getContainers().put(cId, container);
        // stopContainers()
        List<ContainerId> containerIds = new ArrayList<>();
        containerIds.add(cId);
        StopContainersRequest stopRequest = StopContainersRequest.newInstance(containerIds);
        StopContainersResponse stopResponse = containerManager.stopContainers(stopRequest);
        Assert.assertFalse("Should not be authorized to stop container", stopResponse.getSuccessfullyStoppedContainers().contains(cId));
        Assert.assertTrue("Stop container request should fail", stopResponse.getFailedRequests().containsKey(cId));
        // getContainerStatuses()
        containerIds = new ArrayList();
        containerIds.add(cId);
        GetContainerStatusesRequest request = GetContainerStatusesRequest.newInstance(containerIds);
        GetContainerStatusesResponse response = containerManager.getContainerStatuses(request);
        Assert.assertEquals("Should not be authorized to get container status", response.getContainerStatuses().size(), 0);
        Assert.assertTrue("Get status request should fail", response.getFailedRequests().containsKey(cId));
    }

    @Test
    public void testStartContainerFailureWithUnknownAuxService() throws Exception {
        conf.setStrings(NM_AUX_SERVICES, new String[]{ "existService" });
        conf.setClass(String.format(NM_AUX_SERVICE_FMT, "existService"), TestAuxServices.ServiceA.class, Service.class);
        containerManager.start();
        List<StartContainerRequest> startRequest = new ArrayList<>();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        Map<String, ByteBuffer> serviceData = new HashMap<String, ByteBuffer>();
        String serviceName = "non_exist_auxService";
        serviceData.put(serviceName, ByteBuffer.wrap(serviceName.getBytes()));
        containerLaunchContext.setServiceData(serviceData);
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        String user = "start_container_fail";
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        StartContainerRequest request = StartContainerRequest.newInstance(containerLaunchContext, containerToken);
        // start containers
        startRequest.add(request);
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Assert.assertEquals(1, response.getFailedRequests().size());
        Assert.assertEquals(0, response.getSuccessfullyStartedContainers().size());
        Assert.assertTrue(response.getFailedRequests().containsKey(cId));
        Assert.assertTrue(response.getFailedRequests().get(cId).getMessage().contains((("The auxService:" + serviceName) + " does not exist")));
    }

    /* Test added to verify fix in YARN-644 */
    @Test
    public void testNullTokens() throws Exception {
        ContainerManagerImpl cMgrImpl = new ContainerManagerImpl(context, exec, delSrvc, nodeStatusUpdater, metrics, dirsHandler);
        String strExceptionMsg = "";
        try {
            cMgrImpl.authorizeStartAndResourceIncreaseRequest(null, new ContainerTokenIdentifier(), true);
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_NMTOKEN_MSG);
        strExceptionMsg = "";
        try {
            cMgrImpl.authorizeStartAndResourceIncreaseRequest(new NMTokenIdentifier(), null, true);
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_CONTAINERTOKEN_MSG);
        strExceptionMsg = "";
        try {
            cMgrImpl.authorizeGetAndStopContainerRequest(null, null, true, null, null);
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_NMTOKEN_MSG);
        strExceptionMsg = "";
        try {
            cMgrImpl.authorizeUser(null, null);
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_NMTOKEN_MSG);
        ContainerManagerImpl spyContainerMgr = Mockito.spy(cMgrImpl);
        UserGroupInformation ugInfo = UserGroupInformation.createRemoteUser("a");
        Mockito.when(spyContainerMgr.getRemoteUgi()).thenReturn(ugInfo);
        Mockito.when(spyContainerMgr.selectNMTokenIdentifier(ugInfo)).thenReturn(null);
        strExceptionMsg = "";
        try {
            spyContainerMgr.stopContainers(new StopContainersRequestPBImpl());
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_NMTOKEN_MSG);
        strExceptionMsg = "";
        try {
            spyContainerMgr.getContainerStatuses(new GetContainerStatusesRequestPBImpl());
        } catch (YarnException ye) {
            strExceptionMsg = ye.getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_NMTOKEN_MSG);
        Mockito.doNothing().when(spyContainerMgr).authorizeUser(ugInfo, null);
        List<StartContainerRequest> reqList = new ArrayList<>();
        reqList.add(StartContainerRequest.newInstance(null, null));
        StartContainersRequest reqs = new StartContainersRequestPBImpl();
        reqs.setStartContainerRequests(reqList);
        strExceptionMsg = "";
        try {
            spyContainerMgr.startContainers(reqs);
        } catch (YarnException ye) {
            strExceptionMsg = ye.getCause().getMessage();
        }
        Assert.assertEquals(strExceptionMsg, INVALID_CONTAINERTOKEN_MSG);
    }

    @Test
    public void testIncreaseContainerResourceWithInvalidRequests() throws Exception {
        containerManager.start();
        // Start 4 containers 0..4 with default resource (1024, 1)
        List<StartContainerRequest> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ContainerId cId = BaseContainerManagerTest.createContainerId(i);
            long identifier = DUMMY_RM_IDENTIFIER;
            Token containerToken = BaseContainerManagerTest.createContainerToken(cId, identifier, context.getNodeId(), user, context.getContainerTokenSecretManager());
            StartContainerRequest request = StartContainerRequest.newInstance(BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class), containerToken);
            list.add(request);
        }
        StartContainersRequest requestList = StartContainersRequest.newInstance(list);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Assert.assertEquals(4, response.getSuccessfullyStartedContainers().size());
        int i = 0;
        for (ContainerId id : response.getSuccessfullyStartedContainers()) {
            Assert.assertEquals(i, id.getContainerId());
            i++;
        }
        Thread.sleep(2000);
        // Construct container resource increase request,
        List<Token> increaseTokens = new ArrayList<>();
        // Add increase request for container-0, the request will fail as the
        // container will have exited, and won't be in RUNNING state
        ContainerId cId0 = BaseContainerManagerTest.createContainerId(0);
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId0, 1, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, Resource.newInstance(1234, 3), context.getContainerTokenSecretManager(), null);
        increaseTokens.add(containerToken);
        // Add increase request for container-7, the request will fail as the
        // container does not exist
        ContainerId cId7 = BaseContainerManagerTest.createContainerId(7);
        containerToken = BaseContainerManagerTest.createContainerToken(cId7, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, Resource.newInstance(1234, 3), context.getContainerTokenSecretManager(), null);
        increaseTokens.add(containerToken);
        ContainerUpdateRequest updateRequest = ContainerUpdateRequest.newInstance(increaseTokens);
        ContainerUpdateResponse updateResponse = containerManager.updateContainer(updateRequest);
        // Check response
        Assert.assertEquals(1, updateResponse.getSuccessfullyUpdatedContainers().size());
        Assert.assertEquals(1, updateResponse.getFailedRequests().size());
        for (Map.Entry<ContainerId, SerializedException> entry : updateResponse.getFailedRequests().entrySet()) {
            Assert.assertNotNull("Failed message", entry.getValue().getMessage());
            if (cId7.equals(entry.getKey())) {
                Assert.assertTrue(entry.getValue().getMessage().contains((("Container " + (cId7.toString())) + " is not handled by this NodeManager")));
            } else {
                throw new YarnException((("Received failed request from wrong" + " container: ") + (entry.getKey().toString())));
            }
        }
    }

    @Test
    public void testChangeContainerResource() throws Exception {
        containerManager.start();
        File scriptFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "scriptFile");
        PrintWriter fileWriter = new PrintWriter(scriptFile);
        // Construct the Container-id
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        if (Shell.WINDOWS) {
            fileWriter.println("@ping -n 100 127.0.0.1 >nul");
        } else {
            fileWriter.write("\numask 0");
            fileWriter.write("\nexec sleep 100");
        }
        fileWriter.close();
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        URL resource_alpha = URL.fromPath(BaseContainerManagerTest.localFS.makeQualified(new Path(scriptFile.getAbsolutePath())));
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(resource_alpha);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(scriptFile.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, rsrc_alpha);
        containerLaunchContext.setLocalResources(localResources);
        List<String> commands = Arrays.asList(Shell.getRunScriptCommand(scriptFile));
        containerLaunchContext.setCommands(commands);
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<>();
        list.add(scRequest);
        StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        containerManager.startContainers(allRequests);
        // Make sure the container reaches RUNNING state
        BaseContainerManagerTest.waitForNMContainerState(containerManager, cId, RUNNING);
        // Construct container resource increase request,
        List<Token> increaseTokens = new ArrayList<>();
        // Add increase request.
        Resource targetResource = Resource.newInstance(4096, 2);
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId, 1, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, targetResource, context.getContainerTokenSecretManager(), null);
        increaseTokens.add(containerToken);
        ContainerUpdateRequest updateRequest = ContainerUpdateRequest.newInstance(increaseTokens);
        ContainerUpdateResponse updateResponse = containerManager.updateContainer(updateRequest);
        Assert.assertEquals(1, updateResponse.getSuccessfullyUpdatedContainers().size());
        Assert.assertTrue(updateResponse.getFailedRequests().isEmpty());
        // Check status
        List<ContainerId> containerIds = new ArrayList<>();
        containerIds.add(cId);
        GetContainerStatusesRequest gcsRequest = GetContainerStatusesRequest.newInstance(containerIds);
        ContainerStatus containerStatus = containerManager.getContainerStatuses(gcsRequest).getContainerStatuses().get(0);
        // Check status immediately as resource increase is blocking
        Assert.assertEquals(targetResource, containerStatus.getCapability());
        // Simulate a decrease request
        List<Token> decreaseTokens = new ArrayList<>();
        targetResource = Resource.newInstance(2048, 2);
        Token token = BaseContainerManagerTest.createContainerToken(cId, 2, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, targetResource, context.getContainerTokenSecretManager(), null);
        decreaseTokens.add(token);
        updateRequest = ContainerUpdateRequest.newInstance(decreaseTokens);
        updateResponse = containerManager.updateContainer(updateRequest);
        Assert.assertEquals(1, updateResponse.getSuccessfullyUpdatedContainers().size());
        Assert.assertTrue(updateResponse.getFailedRequests().isEmpty());
        // Check status with retry
        containerStatus = containerManager.getContainerStatuses(gcsRequest).getContainerStatuses().get(0);
        int retry = 0;
        while ((!(targetResource.equals(containerStatus.getCapability()))) && ((retry++) < 5)) {
            Thread.sleep(200);
            containerStatus = containerManager.getContainerStatuses(gcsRequest).getContainerStatuses().get(0);
        } 
        Assert.assertEquals(targetResource, containerStatus.getCapability());
    }

    @Test
    public void testOutputThreadDumpSignal() throws IOException, InterruptedException, YarnException {
        testContainerLaunchAndSignal(OUTPUT_THREAD_DUMP);
    }

    @Test
    public void testGracefulShutdownSignal() throws IOException, InterruptedException, YarnException {
        testContainerLaunchAndSignal(GRACEFUL_SHUTDOWN);
    }

    @Test
    public void testForcefulShutdownSignal() throws IOException, InterruptedException, YarnException {
        testContainerLaunchAndSignal(FORCEFUL_SHUTDOWN);
    }

    @Test
    public void testStartContainerFailureWithInvalidLocalResource() throws Exception {
        containerManager.start();
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(null);
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(System.currentTimeMillis());
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put("invalid_resource", rsrc_alpha);
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ContainerLaunchContext spyContainerLaunchContext = Mockito.spy(containerLaunchContext);
        Mockito.when(spyContainerLaunchContext.getLocalResources()).thenReturn(localResources);
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        String user = "start_container_fail";
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        StartContainerRequest request = StartContainerRequest.newInstance(spyContainerLaunchContext, containerToken);
        // start containers
        List<StartContainerRequest> startRequest = new ArrayList<StartContainerRequest>();
        startRequest.add(request);
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Assert.assertTrue(((response.getFailedRequests().size()) == 1));
        Assert.assertTrue(((response.getSuccessfullyStartedContainers().size()) == 0));
        Assert.assertTrue(response.getFailedRequests().containsKey(cId));
        Assert.assertTrue(response.getFailedRequests().get(cId).getMessage().contains("Null resource URL for local resource"));
    }

    @Test
    public void testStartContainerFailureWithNullTypeLocalResource() throws Exception {
        containerManager.start();
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(URL.fromPath(new Path("./")));
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(APPLICATION);
        rsrc_alpha.setType(null);
        rsrc_alpha.setTimestamp(System.currentTimeMillis());
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put("null_type_resource", rsrc_alpha);
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ContainerLaunchContext spyContainerLaunchContext = Mockito.spy(containerLaunchContext);
        Mockito.when(spyContainerLaunchContext.getLocalResources()).thenReturn(localResources);
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        String user = "start_container_fail";
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        StartContainerRequest request = StartContainerRequest.newInstance(spyContainerLaunchContext, containerToken);
        // start containers
        List<StartContainerRequest> startRequest = new ArrayList<StartContainerRequest>();
        startRequest.add(request);
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Assert.assertTrue(((response.getFailedRequests().size()) == 1));
        Assert.assertTrue(((response.getSuccessfullyStartedContainers().size()) == 0));
        Assert.assertTrue(response.getFailedRequests().containsKey(cId));
        Assert.assertTrue(response.getFailedRequests().get(cId).getMessage().contains("Null resource type for local resource"));
    }

    @Test
    public void testStartContainerFailureWithNullVisibilityLocalResource() throws Exception {
        containerManager.start();
        LocalResource rsrc_alpha = BaseContainerManagerTest.recordFactory.newRecordInstance(LocalResource.class);
        rsrc_alpha.setResource(URL.fromPath(new Path("./")));
        rsrc_alpha.setSize((-1));
        rsrc_alpha.setVisibility(null);
        rsrc_alpha.setType(FILE);
        rsrc_alpha.setTimestamp(System.currentTimeMillis());
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put("null_visibility_resource", rsrc_alpha);
        ContainerLaunchContext containerLaunchContext = BaseContainerManagerTest.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        ContainerLaunchContext spyContainerLaunchContext = Mockito.spy(containerLaunchContext);
        Mockito.when(spyContainerLaunchContext.getLocalResources()).thenReturn(localResources);
        ContainerId cId = BaseContainerManagerTest.createContainerId(0);
        String user = "start_container_fail";
        Token containerToken = BaseContainerManagerTest.createContainerToken(cId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        StartContainerRequest request = StartContainerRequest.newInstance(spyContainerLaunchContext, containerToken);
        // start containers
        List<StartContainerRequest> startRequest = new ArrayList<StartContainerRequest>();
        startRequest.add(request);
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        StartContainersResponse response = containerManager.startContainers(requestList);
        Assert.assertTrue(((response.getFailedRequests().size()) == 1));
        Assert.assertTrue(((response.getSuccessfullyStartedContainers().size()) == 0));
        Assert.assertTrue(response.getFailedRequests().containsKey(cId));
        Assert.assertTrue(response.getFailedRequests().get(cId).getMessage().contains("Null resource visibility for local resource"));
    }

    @Test
    public void testGetLocalizationStatuses() throws Exception {
        containerManager.start();
        ContainerId containerId = BaseContainerManagerTest.createContainerId(0, 0);
        Token containerToken = BaseContainerManagerTest.createContainerToken(containerId, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        // localization resource
        File scriptFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "scriptFile_new");
        PrintWriter fileWriter = new PrintWriter(scriptFile);
        File file1 = new File(BaseContainerManagerTest.tmpDir, "file1.txt").getAbsoluteFile();
        writeScriptFile(fileWriter, "Upgrade World!", file1, containerId, false);
        ContainerLaunchContext containerLaunchContext = prepareContainerLaunchContext(scriptFile, "dest_file1", false, 0);
        StartContainerRequest request = StartContainerRequest.newInstance(containerLaunchContext, containerToken);
        List<StartContainerRequest> startRequest = new ArrayList<>();
        startRequest.add(request);
        // start container
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        containerManager.startContainers(requestList);
        Thread.sleep(5000);
        // Get localization statuses
        GetLocalizationStatusesRequest statusRequest = GetLocalizationStatusesRequest.newInstance(Lists.newArrayList(containerId));
        GetLocalizationStatusesResponse statusResponse = containerManager.getLocalizationStatuses(statusRequest);
        Assert.assertEquals(1, statusResponse.getLocalizationStatuses().get(containerId).size());
        LocalizationStatus status = statusResponse.getLocalizationStatuses().get(containerId).iterator().next();
        Assert.assertEquals("resource key", "dest_file1", status.getResourceKey());
        Assert.assertEquals("resource status", COMPLETED, status.getLocalizationState());
        Assert.assertEquals(0, statusResponse.getFailedRequests().size());
        // stop containers
        StopContainersRequest stopRequest = StopContainersRequest.newInstance(Lists.newArrayList(containerId));
        containerManager.stopContainers(stopRequest);
    }

    @Test
    public void testGetLocalizationStatusesMultiContainers() throws Exception {
        containerManager.start();
        ContainerId container1 = BaseContainerManagerTest.createContainerId(0, 0);
        ContainerId container2 = BaseContainerManagerTest.createContainerId(1, 0);
        Token containerToken1 = BaseContainerManagerTest.createContainerToken(container1, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        Token containerToken2 = BaseContainerManagerTest.createContainerToken(container2, DUMMY_RM_IDENTIFIER, context.getNodeId(), user, context.getContainerTokenSecretManager());
        // localization resource
        File scriptFile = Shell.appendScriptExtension(BaseContainerManagerTest.tmpDir, "scriptFile_new");
        PrintWriter fileWriter = new PrintWriter(scriptFile);
        File file1 = new File(BaseContainerManagerTest.tmpDir, "file1.txt").getAbsoluteFile();
        writeScriptFile(fileWriter, "Upgrade World!", file1, container1, false);
        ContainerLaunchContext containerLaunchContext = prepareContainerLaunchContext(scriptFile, "dest_file1", false, 0);
        StartContainerRequest request1 = StartContainerRequest.newInstance(containerLaunchContext, containerToken1);
        StartContainerRequest request2 = StartContainerRequest.newInstance(containerLaunchContext, containerToken2);
        List<StartContainerRequest> startRequest = new ArrayList<>();
        startRequest.add(request1);
        startRequest.add(request2);
        // start container
        StartContainersRequest requestList = StartContainersRequest.newInstance(startRequest);
        containerManager.startContainers(requestList);
        Thread.sleep(5000);
        // Get localization statuses
        GetLocalizationStatusesRequest statusRequest = GetLocalizationStatusesRequest.newInstance(Lists.newArrayList(container1, container2));
        GetLocalizationStatusesResponse statusResponse = containerManager.getLocalizationStatuses(statusRequest);
        Assert.assertEquals(2, statusResponse.getLocalizationStatuses().size());
        ContainerId[] containerIds = new ContainerId[]{ container1, container2 };
        Arrays.stream(containerIds).forEach(( cntnId) -> {
            List<LocalizationStatus> statuses = statusResponse.getLocalizationStatuses().get(container1);
            Assert.assertEquals(1, statuses.size());
            LocalizationStatus status = statuses.get(0);
            Assert.assertEquals("resource key", "dest_file1", status.getResourceKey());
            Assert.assertEquals("resource status", LocalizationState.COMPLETED, status.getLocalizationState());
        });
        Assert.assertEquals(0, statusResponse.getFailedRequests().size());
        // stop containers
        StopContainersRequest stopRequest = StopContainersRequest.newInstance(Lists.newArrayList(container1, container2));
        containerManager.stopContainers(stopRequest);
    }
}

