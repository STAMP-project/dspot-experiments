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
package org.apache.hadoop.yarn.server.nodemanager.webapp;


import YarnConfiguration.NM_LOCAL_DIRS;
import YarnConfiguration.NM_LOG_DIRS;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.NodeHealthCheckerService;
import org.apache.hadoop.yarn.server.nodemanager.ResourceView;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState;
import org.apache.hadoop.yarn.server.nodemanager.metrics.NodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Test;
import org.mockito.Mockito;


public class TestNMWebServer {
    private static final File testRootDir = new File("target", TestNMWebServer.class.getSimpleName());

    private static File testLogDir = new File("target", ((TestNMWebServer.class.getSimpleName()) + "LogDir"));

    @Test
    public void testNMWebAppWithOutPort() throws IOException {
        int port = startNMWebAppServer("0.0.0.0");
        validatePortVal(port);
    }

    @Test
    public void testNMWebAppWithEphemeralPort() throws IOException {
        int port = startNMWebAppServer("0.0.0.0:0");
        validatePortVal(port);
    }

    @Test
    public void testNMWebApp() throws IOException, YarnException {
        Configuration conf = new Configuration();
        Context nmContext = new org.apache.hadoop.yarn.server.nodemanager.NodeManager.NMContext(null, null, null, null, null, false, conf);
        ResourceView resourceView = new ResourceView() {
            @Override
            public long getVmemAllocatedForContainers() {
                return 0;
            }

            @Override
            public long getPmemAllocatedForContainers() {
                return 0;
            }

            @Override
            public long getVCoresAllocatedForContainers() {
                return 0;
            }

            @Override
            public boolean isVmemCheckEnabled() {
                return true;
            }

            @Override
            public boolean isPmemCheckEnabled() {
                return true;
            }
        };
        conf.set(NM_LOCAL_DIRS, TestNMWebServer.testRootDir.getAbsolutePath());
        conf.set(NM_LOG_DIRS, TestNMWebServer.testLogDir.getAbsolutePath());
        NodeHealthCheckerService healthChecker = createNodeHealthCheckerService(conf);
        healthChecker.init(conf);
        LocalDirsHandlerService dirsHandler = healthChecker.getDiskHandler();
        WebServer server = new WebServer(nmContext, resourceView, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(conf), dirsHandler);
        server.init(conf);
        server.start();
        // Add an application and the corresponding containers
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(conf);
        Dispatcher dispatcher = new AsyncDispatcher();
        String user = "nobody";
        long clusterTimeStamp = 1234;
        ApplicationId appId = BuilderUtils.newApplicationId(recordFactory, clusterTimeStamp, 1);
        Application app = Mockito.mock(Application.class);
        Mockito.when(app.getUser()).thenReturn(user);
        Mockito.when(app.getAppId()).thenReturn(appId);
        nmContext.getApplications().put(appId, app);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId container1 = BuilderUtils.newContainerId(recordFactory, appId, appAttemptId, 0);
        ContainerId container2 = BuilderUtils.newContainerId(recordFactory, appId, appAttemptId, 1);
        NodeManagerMetrics metrics = Mockito.mock(NodeManagerMetrics.class);
        NMStateStoreService stateStore = new NMNullStateStoreService();
        for (ContainerId containerId : new ContainerId[]{ container1, container2 }) {
            // TODO: Use builder utils
            ContainerLaunchContext launchContext = recordFactory.newRecordInstance(ContainerLaunchContext.class);
            long currentTime = System.currentTimeMillis();
            Token containerToken = BuilderUtils.newContainerToken(containerId, 0, "127.0.0.1", 1234, user, BuilderUtils.newResource(1024, 1), (currentTime + 10000L), 123, "password".getBytes(), currentTime);
            Context context = Mockito.mock(Context.class);
            Container container = new ContainerImpl(conf, dispatcher, launchContext, null, metrics, BuilderUtils.newContainerTokenIdentifier(containerToken), context) {
                @Override
                public ContainerState getContainerState() {
                    return ContainerState.RUNNING;
                }
            };
            nmContext.getContainers().put(containerId, container);
            // TODO: Gross hack. Fix in code.
            ApplicationId applicationId = containerId.getApplicationAttemptId().getApplicationId();
            nmContext.getApplications().get(applicationId).getContainers().put(containerId, container);
            writeContainerLogs(nmContext, containerId, dirsHandler);
        }
        // TODO: Pull logs and test contents.
        // Thread.sleep(1000000);
    }
}

