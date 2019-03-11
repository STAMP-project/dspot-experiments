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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher;


import ContainersLauncherEventType.CLEANUP_CONTAINER;
import ContainersLauncherEventType.CLEANUP_CONTAINER_FOR_REINIT;
import ContainersLauncherEventType.LAUNCH_CONTAINER;
import ContainersLauncherEventType.PAUSE_CONTAINER;
import ContainersLauncherEventType.RECOVER_CONTAINER;
import ContainersLauncherEventType.RECOVER_PAUSED_CONTAINER;
import ContainersLauncherEventType.RELAUNCH_CONTAINER;
import ContainersLauncherEventType.RESUME_CONTAINER;
import ContainersLauncherEventType.SIGNAL_CONTAINER;
import NodeManager.NMContext;
import SignalContainerCommand.GRACEFUL_SHUTDOWN;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.Whitebox;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.LocalDirsHandlerService;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.ContainerManagerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests to verify all the Container's Launcher Events in
 * {@link ContainersLauncher} are handled as expected.
 */
public class TestContainersLauncher {
    @Mock
    private ApplicationImpl app1;

    @Mock
    private ContainerImpl container;

    @Mock
    private ApplicationId appId;

    @Mock
    private ApplicationAttemptId appAttemptId;

    @Mock
    private ContainerId containerId;

    @Mock
    private ContainersLauncherEvent event;

    @Mock
    private NMContext context;

    @Mock
    private AsyncDispatcher dispatcher;

    @Mock
    private ContainerExecutor exec;

    @Mock
    private LocalDirsHandlerService dirsHandler;

    @Mock
    private ContainerManagerImpl containerManager;

    @Mock
    private ExecutorService containerLauncher;

    @Mock
    private Configuration conf;

    @Mock
    private ContainerLaunch containerLaunch;

    private ContainersLauncher spy;

    @SuppressWarnings("unchecked")
    @Test
    public void testLaunchContainerEvent() throws IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = ((Map<ContainerId, ContainerLaunch>) (Whitebox.getInternalState(spy, "running")));
        Mockito.when(event.getType()).thenReturn(LAUNCH_CONTAINER);
        Assert.assertEquals(0, dummyMap.size());
        spy.handle(event);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLauncher, Mockito.times(1)).submit(Mockito.any(ContainerLaunch.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRelaunchContainerEvent() throws IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = ((Map<ContainerId, ContainerLaunch>) (Whitebox.getInternalState(spy, "running")));
        Mockito.when(event.getType()).thenReturn(RELAUNCH_CONTAINER);
        Assert.assertEquals(0, dummyMap.size());
        spy.handle(event);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLauncher, Mockito.times(1)).submit(Mockito.any(ContainerRelaunch.class));
        for (ContainerId cid : dummyMap.keySet()) {
            Object o = dummyMap.get(cid);
            Assert.assertEquals(true, (o instanceof ContainerRelaunch));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRecoverContainerEvent() throws IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = ((Map<ContainerId, ContainerLaunch>) (Whitebox.getInternalState(spy, "running")));
        Mockito.when(event.getType()).thenReturn(RECOVER_CONTAINER);
        Assert.assertEquals(0, dummyMap.size());
        spy.handle(event);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLauncher, Mockito.times(1)).submit(Mockito.any(RecoveredContainerLaunch.class));
        for (ContainerId cid : dummyMap.keySet()) {
            Object o = dummyMap.get(cid);
            Assert.assertEquals(true, (o instanceof RecoveredContainerLaunch));
        }
    }

    @Test
    public void testRecoverPausedContainerEvent() throws IllegalAccessException, IllegalArgumentException {
        Mockito.when(event.getType()).thenReturn(RECOVER_PAUSED_CONTAINER);
        spy.handle(event);
        Mockito.verify(containerLauncher, Mockito.times(1)).submit(Mockito.any(RecoverPausedContainerLaunch.class));
    }

    @Test
    public void testCleanupContainerEvent() throws IOException, IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = Collections.synchronizedMap(new HashMap<ContainerId, ContainerLaunch>());
        dummyMap.put(containerId, containerLaunch);
        Whitebox.setInternalState(spy, "running", dummyMap);
        Mockito.when(event.getType()).thenReturn(CLEANUP_CONTAINER);
        Assert.assertEquals(1, dummyMap.size());
        spy.handle(event);
        Assert.assertEquals(0, dummyMap.size());
        Mockito.verify(containerLauncher, Mockito.times(1)).submit(Mockito.any(ContainerCleanup.class));
    }

    @Test
    public void testCleanupContainerForReINITEvent() throws IOException, IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = Collections.synchronizedMap(new HashMap<ContainerId, ContainerLaunch>());
        dummyMap.put(containerId, containerLaunch);
        Whitebox.setInternalState(spy, "running", dummyMap);
        Mockito.when(event.getType()).thenReturn(CLEANUP_CONTAINER_FOR_REINIT);
        final List<ContainerId> cleanedContainers = new ArrayList<>();
        Mockito.doAnswer(( invocation) -> {
            cleanedContainers.add(((ContainerId) (invocation.getArguments()[1])));
            return null;
        }).when(spy).cleanup(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        spy.handle(event);
        Assert.assertEquals("container not cleaned", containerId, cleanedContainers.get(0));
    }

    @Test
    public void testSignalContainerEvent() throws IOException, IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = Collections.synchronizedMap(new HashMap<ContainerId, ContainerLaunch>());
        dummyMap.put(containerId, containerLaunch);
        SignalContainersLauncherEvent dummyEvent = Mockito.mock(SignalContainersLauncherEvent.class);
        Mockito.when(dummyEvent.getContainer()).thenReturn(container);
        Mockito.when(container.getContainerId()).thenReturn(containerId);
        Mockito.when(containerId.getApplicationAttemptId()).thenReturn(appAttemptId);
        Mockito.when(containerId.getApplicationAttemptId().getApplicationId()).thenReturn(appId);
        Whitebox.setInternalState(spy, "running", dummyMap);
        Mockito.when(dummyEvent.getType()).thenReturn(SIGNAL_CONTAINER);
        Mockito.when(dummyEvent.getCommand()).thenReturn(GRACEFUL_SHUTDOWN);
        Mockito.doNothing().when(containerLaunch).signalContainer(GRACEFUL_SHUTDOWN);
        spy.handle(dummyEvent);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLaunch, Mockito.times(1)).signalContainer(GRACEFUL_SHUTDOWN);
    }

    @Test
    public void testPauseContainerEvent() throws IOException, IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = Collections.synchronizedMap(new HashMap<ContainerId, ContainerLaunch>());
        dummyMap.put(containerId, containerLaunch);
        Whitebox.setInternalState(spy, "running", dummyMap);
        Mockito.when(event.getType()).thenReturn(PAUSE_CONTAINER);
        Mockito.doNothing().when(containerLaunch).pauseContainer();
        spy.handle(event);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLaunch, Mockito.times(1)).pauseContainer();
    }

    @Test
    public void testResumeContainerEvent() throws IOException, IllegalAccessException, IllegalArgumentException {
        Map<ContainerId, ContainerLaunch> dummyMap = Collections.synchronizedMap(new HashMap<ContainerId, ContainerLaunch>());
        dummyMap.put(containerId, containerLaunch);
        Whitebox.setInternalState(spy, "running", dummyMap);
        Mockito.when(event.getType()).thenReturn(RESUME_CONTAINER);
        Mockito.doNothing().when(containerLaunch).resumeContainer();
        spy.handle(event);
        Assert.assertEquals(1, dummyMap.size());
        Mockito.verify(containerLaunch, Mockito.times(1)).resumeContainer();
    }
}

