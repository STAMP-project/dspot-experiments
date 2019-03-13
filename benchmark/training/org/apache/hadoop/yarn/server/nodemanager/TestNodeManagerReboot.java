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
package org.apache.hadoop.yarn.server.nodemanager;


import ContainerLocalizer.FILECACHE;
import ContainerLocalizer.USERCACHE;
import ContainerState.DONE;
import LocalResourceType.FILE;
import LocalResourceVisibility.APPLICATION;
import ResourceLocalizationService.NM_PRIVATE_DIR;
import YarnConfiguration.NM_ADDRESS;
import YarnConfiguration.NM_LOCALIZER_ADDRESS;
import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.NM_LOG_RETAIN_SECONDS;
import YarnConfiguration.NM_PMEM_MB;
import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.NMTokenIdentifier;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.BaseContainerManagerTest;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.TestContainerManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.task.FileDeletionMatcher;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNodeManagerReboot {
    static final File basedir = new File("target", TestNodeManagerReboot.class.getName());

    static final File logsDir = new File(TestNodeManagerReboot.basedir, "logs");

    static final File nmLocalDir = new File(TestNodeManagerReboot.basedir, "nm0");

    static final File localResourceDir = new File(TestNodeManagerReboot.basedir, "resource");

    static final String user = System.getProperty("user.name");

    private FileContext localFS;

    private TestNodeManagerReboot.MyNodeManager nm;

    private DeletionService delService;

    static final Logger LOG = LoggerFactory.getLogger(TestNodeManagerReboot.class);

    @Test(timeout = 2000000)
    public void testClearLocalDirWhenNodeReboot() throws IOException, InterruptedException, YarnException {
        nm = new TestNodeManagerReboot.MyNodeManager();
        start();
        final ContainerManagementProtocol containerManager = getContainerManager();
        // create files under fileCache
        createFiles(TestNodeManagerReboot.nmLocalDir.getAbsolutePath(), FILECACHE, 100);
        TestNodeManagerReboot.localResourceDir.mkdirs();
        ContainerLaunchContext containerLaunchContext = Records.newRecord(ContainerLaunchContext.class);
        // Construct the Container-id
        ContainerId cId = createContainerId();
        URL localResourceUri = URL.fromPath(localFS.makeQualified(new Path(TestNodeManagerReboot.localResourceDir.getAbsolutePath())));
        LocalResource localResource = LocalResource.newInstance(localResourceUri, FILE, APPLICATION, (-1), TestNodeManagerReboot.localResourceDir.lastModified());
        String destinationFile = "dest_file";
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        localResources.put(destinationFile, localResource);
        containerLaunchContext.setLocalResources(localResources);
        List<String> commands = new ArrayList<String>();
        containerLaunchContext.setCommands(commands);
        NodeId nodeId = getNMContext().getNodeId();
        StartContainerRequest scRequest = StartContainerRequest.newInstance(containerLaunchContext, TestContainerManager.createContainerToken(cId, 0, nodeId, destinationFile, getNMContext().getContainerTokenSecretManager()));
        List<StartContainerRequest> list = new ArrayList<StartContainerRequest>();
        list.add(scRequest);
        final StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        final UserGroupInformation currentUser = UserGroupInformation.createRemoteUser(cId.getApplicationAttemptId().toString());
        NMTokenIdentifier nmIdentifier = new NMTokenIdentifier(cId.getApplicationAttemptId(), nodeId, TestNodeManagerReboot.user, 123);
        currentUser.addTokenIdentifier(nmIdentifier);
        currentUser.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws IOException, YarnException {
                nm.getContainerManager().startContainers(allRequests);
                return null;
            }
        });
        List<ContainerId> containerIds = new ArrayList<ContainerId>();
        containerIds.add(cId);
        GetContainerStatusesRequest request = GetContainerStatusesRequest.newInstance(containerIds);
        Container container = getNMContext().getContainers().get(request.getContainerIds().get(0));
        final int MAX_TRIES = 20;
        int numTries = 0;
        while ((!(container.getContainerState().equals(DONE))) && (numTries <= MAX_TRIES)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Do nothing
            }
            numTries++;
        } 
        Assert.assertEquals(DONE, container.getContainerState());
        Assert.assertTrue((("The container should create a subDir named currentUser: " + (TestNodeManagerReboot.user)) + "under localDir/usercache"), ((numOfLocalDirs(TestNodeManagerReboot.nmLocalDir.getAbsolutePath(), USERCACHE)) > 0));
        Assert.assertTrue(("There should be files or Dirs under nm_private when " + "container is launched"), ((numOfLocalDirs(TestNodeManagerReboot.nmLocalDir.getAbsolutePath(), NM_PRIVATE_DIR)) > 0));
        // restart the NodeManager
        restartNM(MAX_TRIES);
        checkNumOfLocalDirs();
        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, new Path(((ResourceLocalizationService.NM_PRIVATE_DIR) + "_DEL_")), null)));
        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, new Path(((ContainerLocalizer.FILECACHE) + "_DEL_")), null)));
        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, TestNodeManagerReboot.user, null, Arrays.asList(new Path(destinationFile)))));
        Mockito.verify(delService, Mockito.times(1)).delete(ArgumentMatchers.argThat(new FileDeletionMatcher(delService, null, new Path(((ContainerLocalizer.USERCACHE) + "_DEL_")), new ArrayList<Path>())));
        // restart the NodeManager again
        // this time usercache directory should be empty
        restartNM(MAX_TRIES);
        checkNumOfLocalDirs();
    }

    private class MyNodeManager extends NodeManager {
        public MyNodeManager() throws IOException {
            super();
            this.init(createNMConfig());
        }

        @Override
        protected NodeStatusUpdater createNodeStatusUpdater(Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker) {
            MockNodeStatusUpdater myNodeStatusUpdater = new MockNodeStatusUpdater(context, dispatcher, healthChecker, metrics);
            return myNodeStatusUpdater;
        }

        @Override
        protected DeletionService createDeletionService(ContainerExecutor exec) {
            delService = Mockito.spy(new DeletionService(exec));
            return delService;
        }

        private YarnConfiguration createNMConfig() throws IOException {
            YarnConfiguration conf = new YarnConfiguration();
            conf.setInt(NM_PMEM_MB, (5 * 1024));// 5GB

            conf.set(NM_ADDRESS, ("127.0.0.1:" + (ServerSocketUtil.getPort(49152, 10))));
            conf.set(NM_LOCALIZER_ADDRESS, ("127.0.0.1:" + (ServerSocketUtil.getPort(49153, 10))));
            conf.set(NM_LOG_DIRS, TestNodeManagerReboot.logsDir.getAbsolutePath());
            conf.set(NM_LOCAL_DIRS, TestNodeManagerReboot.nmLocalDir.getAbsolutePath());
            conf.setLong(NM_LOG_RETAIN_SECONDS, 1);
            return conf;
        }
    }
}

