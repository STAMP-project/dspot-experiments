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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.container;


import ContainerEventType.CONTAINER_EXITED_WITH_SUCCESS;
import ContainerEventType.CONTAINER_LAUNCHED;
import ContainerEventType.CONTAINER_RESOURCES_CLEANEDUP;
import ContainerEventType.INIT_CONTAINER;
import ContainerEventType.KILL_CONTAINER;
import ContainerEventType.RESOURCE_LOCALIZED;
import ContainerExitStatus.KILLED_BY_RESOURCEMANAGER;
import ContainerRetryPolicy.NEVER_RETRY;
import ContainerRetryPolicy.RETRY_ON_ALL_ERRORS;
import ContainerRetryPolicy.RETRY_ON_SPECIFIC_ERROR_CODES;
import ContainerRuntimeConstants.CONTAINER_RUNTIME_DOCKER;
import ContainerRuntimeConstants.ENV_CONTAINER_TYPE;
import ContainerState.CONTAINER_CLEANEDUP_AFTER_KILL;
import ContainerState.DONE;
import ContainerState.EXITED_WITH_FAILURE;
import ContainerState.EXITED_WITH_SUCCESS;
import ContainerState.KILLING;
import ContainerState.LOCALIZATION_FAILED;
import ContainerState.LOCALIZING;
import ContainerState.NEW;
import ContainerState.PAUSED;
import ContainerState.RUNNING;
import ContainerState.SCHEDULED;
import ExitCode.FORCE_KILLED;
import LocalResourceVisibility.APPLICATION;
import LocalResourceVisibility.PRIVATE;
import LocalResourceVisibility.PUBLIC;
import NodeManager.DefaultContainerStateListener;
import YarnConfiguration.NM_CONTAINER_RETRY_MINIMUM_INTERVAL_MS;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerRetryContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.ContainerStateTransitionListener;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.DeletionService;
import org.apache.hadoop.yarn.server.nodemanager.NodeManager;
import org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdater;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.AuxServicesEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.AuxServicesEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainerLaunch;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainersLauncher;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainersLauncherEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainersLauncherEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.LocalResourceRequest;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationCleanupEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ContainerLocalizationRequestEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizationEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizationEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainersMonitorEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainersMonitorEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler.ContainerScheduler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler.ContainerSchedulerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler.ContainerSchedulerEventType;
import org.apache.hadoop.yarn.server.nodemanager.metrics.NodeManagerMetrics;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMNullStateStoreService;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ContainerEventType.CONTAINER_EXITED_WITH_FAILURE;
import static ContainerEventType.CONTAINER_EXITED_WITH_SUCCESS;
import static ContainerEventType.CONTAINER_KILLED_ON_REQUEST;
import static ContainerEventType.CONTAINER_LAUNCHED;
import static ContainerEventType.CONTAINER_RESOURCES_CLEANEDUP;
import static ContainerEventType.INIT_CONTAINER;
import static ContainerEventType.RESOURCE_FAILED;
import static ContainerState.RELAUNCHING;


public class TestContainer {
    final NodeManagerMetrics metrics = NodeManagerMetrics.create();

    final Configuration conf = new YarnConfiguration();

    final String FAKE_LOCALIZATION_ERROR = "Fake localization error";

    /**
     * Verify correct container request events sent to localizer.
     */
    @Test
    public void testLocalizationRequest() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(7, 314159265358979L, 4344, "yak");
            Assert.assertEquals(NEW, wc.c.getContainerState());
            wc.initContainer();
            // Verify request for public/private resources to localizer
            TestContainer.ResourcesRequestedMatcher matchesReq = new TestContainer.ResourcesRequestedMatcher(wc.localResources, EnumSet.of(PUBLIC, PRIVATE, APPLICATION));
            Mockito.verify(wc.localizerBus).handle(ArgumentMatchers.argThat(matchesReq));
            Assert.assertEquals(LOCALIZING, wc.c.getContainerState());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    /**
     * Verify container launch when all resources already cached.
     */
    @Test
    public void testLocalizationLaunch() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(8, 314159265358979L, 4344, "yak");
            Assert.assertEquals(NEW, wc.c.getContainerState());
            wc.initContainer();
            Map<Path, List<String>> localPaths = wc.localizeResources();
            // all resources should be localized
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            Assert.assertNotNull(wc.c.getLocalizedResources());
            for (Map.Entry<Path, List<String>> loc : wc.c.getLocalizedResources().entrySet()) {
                Assert.assertEquals(localPaths.remove(loc.getKey()), loc.getValue());
            }
            Assert.assertTrue(localPaths.isEmpty());
            final TestContainer.WrappedContainer wcf = wc;
            // verify container launch
            ArgumentMatcher<ContainersLauncherEvent> matchesContainerLaunch = ( event) -> (wcf.c) == (event.getContainer());
            Mockito.verify(wc.launcherBus).handle(ArgumentMatchers.argThat(matchesContainerLaunch));
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testExternalKill() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(13, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            int running = metrics.getRunningContainers();
            wc.launchContainer();
            Assert.assertEquals((running + 1), metrics.getRunningContainers());
            Mockito.reset(wc.localizerBus);
            wc.containerKilledOnRequest();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int failed = metrics.getFailedContainers();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((failed + 1), metrics.getFailedContainers());
            Assert.assertEquals(running, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testDockerContainerExternalKill() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(13, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            int running = metrics.getRunningContainers();
            wc.launchContainer();
            Assert.assertEquals((running + 1), metrics.getRunningContainers());
            Mockito.reset(wc.localizerBus);
            wc.containerKilledOnRequest();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int failed = metrics.getFailedContainers();
            wc.dockerContainerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((failed + 1), metrics.getFailedContainers());
            Assert.assertEquals(running, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testContainerPauseAndResume() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(13, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            int running = metrics.getRunningContainers();
            wc.launchContainer();
            Assert.assertEquals((running + 1), metrics.getRunningContainers());
            Mockito.reset(wc.localizerBus);
            wc.pauseContainer();
            Assert.assertEquals(PAUSED, wc.c.getContainerState());
            wc.resumeContainer();
            Assert.assertEquals(RUNNING, wc.c.getContainerState());
            wc.containerKilledOnRequest();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int failed = metrics.getFailedContainers();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((failed + 1), metrics.getFailedContainers());
            Assert.assertEquals(running, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testCleanupOnFailure() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(10, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerFailed(FORCE_KILLED.getExitCode());
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testDockerContainerCleanupOnFailure() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(10, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerFailed(FORCE_KILLED.getExitCode());
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            wc.dockerContainerResourcesCleanup();
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testCleanupOnSuccess() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(11, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            int running = metrics.getRunningContainers();
            wc.launchContainer();
            Assert.assertEquals((running + 1), metrics.getRunningContainers());
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            Assert.assertEquals(EXITED_WITH_SUCCESS, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int completed = metrics.getCompletedContainers();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((completed + 1), metrics.getCompletedContainers());
            Assert.assertEquals(running, metrics.getRunningContainers());
            ContainerEventType e1 = wc.initStateToEvent.get(NEW);
            ContainerState s2 = wc.eventToFinalState.get(e1);
            ContainerEventType e2 = wc.initStateToEvent.get(s2);
            ContainerState s3 = wc.eventToFinalState.get(e2);
            ContainerEventType e3 = wc.initStateToEvent.get(s3);
            ContainerState s4 = wc.eventToFinalState.get(e3);
            ContainerEventType e4 = wc.initStateToEvent.get(s4);
            ContainerState s5 = wc.eventToFinalState.get(e4);
            ContainerEventType e5 = wc.initStateToEvent.get(s5);
            ContainerState s6 = wc.eventToFinalState.get(e5);
            Assert.assertEquals(LOCALIZING, s2);
            Assert.assertEquals(SCHEDULED, s3);
            Assert.assertEquals(RUNNING, s4);
            Assert.assertEquals(EXITED_WITH_SUCCESS, s5);
            Assert.assertEquals(DONE, s6);
            Assert.assertEquals(INIT_CONTAINER, e1);
            Assert.assertEquals(RESOURCE_LOCALIZED, e2);
            Assert.assertEquals(CONTAINER_LAUNCHED, e3);
            Assert.assertEquals(CONTAINER_EXITED_WITH_SUCCESS, e4);
            Assert.assertEquals(CONTAINER_RESOURCES_CLEANEDUP, e5);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testDockerContainerCleanupOnSuccess() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(11, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            int running = metrics.getRunningContainers();
            wc.launchContainer();
            Assert.assertEquals((running + 1), metrics.getRunningContainers());
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            Assert.assertEquals(EXITED_WITH_SUCCESS, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int completed = metrics.getCompletedContainers();
            wc.dockerContainerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((completed + 1), metrics.getCompletedContainers());
            Assert.assertEquals(running, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testInitWhileDone() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(6, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            verifyOutofBandHeartBeat(wc);
            Assert.assertNull(wc.c.getLocalizedResources());
            // Now in DONE, issue INIT
            wc.initContainer();
            // Verify still in DONE
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testDockerContainerInitWhileDone() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(6, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            wc.dockerContainerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            verifyOutofBandHeartBeat(wc);
            Assert.assertNull(wc.c.getLocalizedResources());
            // Now in DONE, issue INIT
            wc.initContainer();
            // Verify still in DONE
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testLocalizationFailureAtDone() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(6, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            verifyOutofBandHeartBeat(wc);
            Assert.assertNull(wc.c.getLocalizedResources());
            // Now in DONE, issue RESOURCE_FAILED as done by LocalizeRunner
            wc.resourceFailedContainer();
            // Verify still in DONE
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testDockerContainerLocalizationFailureAtDone() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(6, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.containerSuccessful();
            wc.dockerContainerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            verifyOutofBandHeartBeat(wc);
            Assert.assertNull(wc.c.getLocalizedResources());
            // Now in DONE, issue RESOURCE_FAILED as done by LocalizeRunner
            wc.resourceFailedContainer();
            // Verify still in DONE
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLocalizationFailureWhileRunning() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(6, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            Assert.assertEquals(RUNNING, wc.c.getContainerState());
            // Now in RUNNING, handle ContainerResourceFailedEvent, cause NPE before
            wc.handleContainerResourceFailedEvent();
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testCleanupOnKillRequest() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(12, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            Mockito.reset(wc.localizerBus);
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.containerKilledOnRequest();
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnNew() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(13, 314159265358979L, 4344, "yak");
            Assert.assertEquals(NEW, wc.c.getContainerState());
            int killed = metrics.getKilledContainers();
            wc.killContainer();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            verifyOutofBandHeartBeat(wc);
            Assert.assertEquals(KILLED_BY_RESOURCEMANAGER, wc.c.cloneAndGetContainerStatus().getExitStatus());
            Assert.assertTrue(wc.c.cloneAndGetContainerStatus().getDiagnostics().contains("KillRequest"));
            Assert.assertEquals((killed + 1), metrics.getKilledContainers());
            // check container metrics is generated.
            ContainerMetrics containerMetrics = ContainerMetrics.forContainer(wc.cId, 1, 5000);
            Assert.assertEquals(KILLED_BY_RESOURCEMANAGER, containerMetrics.exitCode.value());
            Assert.assertTrue(((containerMetrics.startTime.value()) > 0));
            Assert.assertTrue(((containerMetrics.finishTime.value()) >= (containerMetrics.startTime.value())));
            Assert.assertEquals(KILL_CONTAINER, wc.initStateToEvent.get(NEW));
            Assert.assertEquals(DONE, wc.eventToFinalState.get(KILL_CONTAINER));
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizing() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(14, 314159265358979L, 4344, "yak");
            wc.initContainer();
            Assert.assertEquals(LOCALIZING, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertEquals(KILLED_BY_RESOURCEMANAGER, wc.c.cloneAndGetContainerStatus().getExitStatus());
            Assert.assertTrue(wc.c.cloneAndGetContainerStatus().getDiagnostics().contains("KillRequest"));
            int killed = metrics.getKilledContainers();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((killed + 1), metrics.getKilledContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizationFailed() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(15, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.failLocalizeResources(wc.getLocalResourceCount());
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.killContainer();
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int failed = metrics.getFailedContainers();
            wc.containerResourcesCleanup();
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((failed + 1), metrics.getFailedContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizedWhenContainerNotLaunchedContainerKilled() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            ContainerLaunch launcher = wc.launcher.running.get(wc.c.getContainerId());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            // check that container cleanup hasn't started at this point.
            TestContainer.LocalizationCleanupMatcher cleanupResources = new TestContainer.LocalizationCleanupMatcher(wc.c);
            Mockito.verify(wc.localizerBus, Mockito.times(0)).handle(ArgumentMatchers.argThat(cleanupResources));
            // check if containerlauncher cleans up the container launch.
            Mockito.verify(wc.launcherBus).handle(ArgumentMatchers.refEq(new ContainersLauncherEvent(wc.c, ContainersLauncherEventType.CLEANUP_CONTAINER), "timestamp"));
            launcher.call();
            wc.drainDispatcherEvents();
            Assert.assertEquals(CONTAINER_CLEANEDUP_AFTER_KILL, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            int killed = metrics.getKilledContainers();
            wc.c.handle(new ContainerEvent(wc.c.getContainerId(), CONTAINER_RESOURCES_CLEANEDUP));
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((killed + 1), metrics.getKilledContainers());
            Assert.assertEquals(0, metrics.getRunningContainers());
            Assert.assertEquals(0, wc.launcher.running.size());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testDockerKillOnLocalizedWhenContainerNotLaunchedContainerKilled() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            ContainerLaunch launcher = wc.launcher.running.get(wc.c.getContainerId());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            launcher.call();
            wc.drainDispatcherEvents();
            Assert.assertEquals(CONTAINER_CLEANEDUP_AFTER_KILL, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyDockerContainerCleanupCall(wc);
            int killed = metrics.getKilledContainers();
            wc.c.handle(new ContainerEvent(wc.c.getContainerId(), CONTAINER_RESOURCES_CLEANEDUP));
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals((killed + 1), metrics.getKilledContainers());
            Assert.assertEquals(0, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizedWhenContainerNotLaunchedContainerSuccess() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            wc.containerSuccessful();
            wc.drainDispatcherEvents();
            Assert.assertEquals(EXITED_WITH_SUCCESS, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            wc.c.handle(new ContainerEvent(wc.c.getContainerId(), CONTAINER_RESOURCES_CLEANEDUP));
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals(0, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizedWhenContainerNotLaunchedContainerFailure() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            wc.containerFailed(FORCE_KILLED.getExitCode());
            wc.drainDispatcherEvents();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            wc.c.handle(new ContainerEvent(wc.c.getContainerId(), CONTAINER_RESOURCES_CLEANEDUP));
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals(0, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testDockerKillOnLocalizedContainerNotLaunchedContainerFailure() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            wc.containerFailed(FORCE_KILLED.getExitCode());
            wc.drainDispatcherEvents();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyDockerContainerCleanupCall(wc);
            wc.c.handle(new ContainerEvent(wc.c.getContainerId(), CONTAINER_RESOURCES_CLEANEDUP));
            Assert.assertEquals(DONE, wc.c.getContainerState());
            Assert.assertEquals(0, metrics.getRunningContainers());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testKillOnLocalizedWhenContainerLaunched() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            ContainerLaunch launcher = wc.launcher.running.get(wc.c.getContainerId());
            launcher.call();
            wc.drainDispatcherEvents();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testDockerKillOnLocalizedWhenContainerLaunched() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(17, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            Assert.assertEquals(SCHEDULED, wc.c.getContainerState());
            ContainerLaunch launcher = wc.launcher.running.get(wc.c.getContainerId());
            launcher.call();
            wc.drainDispatcherEvents();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            wc.killContainer();
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyDockerContainerCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testResourceLocalizedOnLocalizationFailed() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(16, 314159265358979L, 4344, "yak");
            wc.initContainer();
            int failCount = (wc.getLocalResourceCount()) / 2;
            if (failCount == 0) {
                failCount = 1;
            }
            wc.failLocalizeResources(failCount);
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.localizeResourcesFromInvalidState(failCount);
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
            Assert.assertTrue(wc.getDiagnostics().contains(FAKE_LOCALIZATION_ERROR));
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testResourceFailedOnLocalizationFailed() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(16, 314159265358979L, 4344, "yak");
            wc.initContainer();
            Iterator<String> lRsrcKeys = wc.localResources.keySet().iterator();
            String key1 = lRsrcKeys.next();
            String key2 = lRsrcKeys.next();
            wc.failLocalizeSpecificResource(key1);
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.failLocalizeSpecificResource(key2);
            Assert.assertEquals(LOCALIZATION_FAILED, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testResourceFailedOnKilling() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(16, 314159265358979L, 4344, "yak");
            wc.initContainer();
            Iterator<String> lRsrcKeys = wc.localResources.keySet().iterator();
            String key1 = lRsrcKeys.next();
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.failLocalizeSpecificResource(key1);
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    /**
     * Verify serviceData correctly sent.
     */
    @Test
    public void testServiceData() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(9, 314159265358979L, 4344, "yak", false, true);
            Assert.assertEquals(NEW, wc.c.getContainerState());
            wc.initContainer();
            for (final Map.Entry<String, ByteBuffer> e : wc.serviceData.entrySet()) {
                ArgumentMatcher<AuxServicesEvent> matchesServiceReq = ( evt) -> (e.getKey().equals(evt.getServiceID())) && (0 == (e.getValue().compareTo(evt.getServiceData())));
                Mockito.verify(wc.auxBus).handle(ArgumentMatchers.argThat(matchesServiceReq));
            }
            final TestContainer.WrappedContainer wcf = wc;
            // verify launch on empty resource request
            ArgumentMatcher<ContainersLauncherEvent> matchesLaunchReq = ( evt) -> ((evt.getType()) == (ContainersLauncherEventType.LAUNCH_CONTAINER)) && (wcf.cId.equals(evt.getContainer().getContainerId()));
            Mockito.verify(wc.launcherBus).handle(ArgumentMatchers.argThat(matchesLaunchReq));
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testLaunchAfterKillRequest() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(14, 314159265358979L, 4344, "yak");
            wc.initContainer();
            wc.localizeResources();
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.launchContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.containerKilledOnRequest();
            verifyCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testDockerContainerLaunchAfterKillRequest() throws Exception {
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(14, 314159265358979L, 4344, "yak");
            wc.setupDockerContainerEnv();
            wc.initContainer();
            wc.localizeResources();
            wc.killContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.launchContainer();
            Assert.assertEquals(KILLING, wc.c.getContainerState());
            Assert.assertNull(wc.c.getLocalizedResources());
            wc.containerKilledOnRequest();
            verifyDockerContainerCleanupCall(wc);
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    @Test
    public void testContainerRetry() throws Exception {
        ContainerRetryContext containerRetryContext1 = ContainerRetryContext.newInstance(NEVER_RETRY, null, 3, 0);
        testContainerRetry(containerRetryContext1, 2, 0);
        ContainerRetryContext containerRetryContext2 = ContainerRetryContext.newInstance(RETRY_ON_ALL_ERRORS, null, 3, 0);
        testContainerRetry(containerRetryContext2, 2, 3);
        ContainerRetryContext containerRetryContext3 = ContainerRetryContext.newInstance(RETRY_ON_ALL_ERRORS, null, 3, 0);
        // If exit code is 0, it will not retry
        testContainerRetry(containerRetryContext3, 0, 0);
        ContainerRetryContext containerRetryContext4 = ContainerRetryContext.newInstance(RETRY_ON_SPECIFIC_ERROR_CODES, null, 3, 0);
        testContainerRetry(containerRetryContext4, 2, 0);
        HashSet<Integer> errorCodes = new HashSet<>();
        errorCodes.add(2);
        errorCodes.add(6);
        ContainerRetryContext containerRetryContext5 = ContainerRetryContext.newInstance(RETRY_ON_SPECIFIC_ERROR_CODES, errorCodes, 3, 0);
        testContainerRetry(containerRetryContext5, 2, 3);
        HashSet<Integer> errorCodes2 = new HashSet<>();
        errorCodes.add(143);
        ContainerRetryContext containerRetryContext6 = ContainerRetryContext.newInstance(RETRY_ON_SPECIFIC_ERROR_CODES, errorCodes2, 3, 0);
        // If exit code is 143(SIGTERM), it will not retry even it is in errorCodes.
        testContainerRetry(containerRetryContext6, 143, 0);
    }

    @Test
    public void testContainerRestartInterval() throws IOException {
        conf.setInt(NM_CONTAINER_RETRY_MINIMUM_INTERVAL_MS, 2000);
        ContainerRetryContext containerRetryContext1 = ContainerRetryContext.newInstance(NEVER_RETRY, null, 3, 0);
        testContainerRestartInterval(containerRetryContext1, 0);
        ContainerRetryContext containerRetryContext2 = ContainerRetryContext.newInstance(RETRY_ON_ALL_ERRORS, null, 3, 0);
        testContainerRestartInterval(containerRetryContext2, 2000);
        ContainerRetryContext containerRetryContext3 = ContainerRetryContext.newInstance(RETRY_ON_ALL_ERRORS, null, 3, 4000);
        testContainerRestartInterval(containerRetryContext3, 4000);
    }

    @Test
    public void testContainerRetryFailureValidityInterval() throws Exception {
        ContainerRetryContext containerRetryContext = ContainerRetryContext.newInstance(RETRY_ON_ALL_ERRORS, null, 1, 0, 10);
        TestContainer.WrappedContainer wc = null;
        try {
            wc = new TestContainer.WrappedContainer(25, 314159265358980L, 4200, "test", containerRetryContext);
            ControlledClock clock = new ControlledClock();
            wc.getRetryPolicy().setClock(clock);
            wc.initContainer();
            wc.localizeResources();
            wc.launchContainer();
            wc.containerFailed(12);
            Assert.assertEquals(RUNNING, wc.c.getContainerState());
            clock.setTime(20);
            wc.containerFailed(12);
            Assert.assertEquals(RUNNING, wc.c.getContainerState());
            clock.setTime(40);
            wc.containerFailed(12);
            Assert.assertEquals(RUNNING, wc.c.getContainerState());
            clock.setTime(45);
            wc.containerFailed(12);
            Assert.assertEquals(EXITED_WITH_FAILURE, wc.c.getContainerState());
        } finally {
            if (wc != null) {
                wc.finished();
            }
        }
    }

    // Argument matcher for matching container localization cleanup event.
    private static class LocalizationCleanupMatcher implements ArgumentMatcher<LocalizationEvent> {
        Container c;

        LocalizationCleanupMatcher(Container c) {
            this.c = c;
        }

        @Override
        public boolean matches(LocalizationEvent e) {
            if (!(e instanceof ContainerLocalizationCleanupEvent)) {
                return false;
            }
            ContainerLocalizationCleanupEvent evt = ((ContainerLocalizationCleanupEvent) (e));
            return (evt.getContainer()) == (c);
        }
    }

    private static class ResourcesReleasedMatcher extends TestContainer.LocalizationCleanupMatcher {
        final HashSet<LocalResourceRequest> resources = new HashSet<LocalResourceRequest>();

        ResourcesReleasedMatcher(Map<String, LocalResource> allResources, EnumSet<LocalResourceVisibility> vis, Container c) throws URISyntaxException {
            super(c);
            for (Map.Entry<String, LocalResource> e : allResources.entrySet()) {
                if (vis.contains(e.getValue().getVisibility())) {
                    resources.add(new LocalResourceRequest(e.getValue()));
                }
            }
        }

        @Override
        public boolean matches(LocalizationEvent e) {
            // match event type and container.
            if (!(super.matches(e))) {
                return false;
            }
            // match resources.
            ContainerLocalizationCleanupEvent evt = ((ContainerLocalizationCleanupEvent) (e));
            final HashSet<LocalResourceRequest> expected = new HashSet<LocalResourceRequest>(resources);
            for (Collection<LocalResourceRequest> rc : evt.getResources().values()) {
                for (LocalResourceRequest rsrc : rc) {
                    if (!(expected.remove(rsrc))) {
                        return false;
                    }
                }
            }
            return expected.isEmpty();
        }
    }

    // Accept iff the resource payload matches.
    private static class ResourcesRequestedMatcher implements ArgumentMatcher<LocalizationEvent> {
        final HashSet<LocalResourceRequest> resources = new HashSet<LocalResourceRequest>();

        ResourcesRequestedMatcher(Map<String, LocalResource> allResources, EnumSet<LocalResourceVisibility> vis) throws URISyntaxException {
            for (Map.Entry<String, LocalResource> e : allResources.entrySet()) {
                if (vis.contains(e.getValue().getVisibility())) {
                    resources.add(new LocalResourceRequest(e.getValue()));
                }
            }
        }

        @Override
        public boolean matches(LocalizationEvent e) {
            ContainerLocalizationRequestEvent evt = ((ContainerLocalizationRequestEvent) (e));
            final HashSet<LocalResourceRequest> expected = new HashSet<LocalResourceRequest>(resources);
            for (Collection<LocalResourceRequest> rc : evt.getRequestedResources().values()) {
                for (LocalResourceRequest rsrc : rc) {
                    if (!(expected.remove(rsrc))) {
                        return false;
                    }
                }
            }
            return expected.isEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    private class WrappedContainer {
        final DrainDispatcher dispatcher;

        final EventHandler<LocalizationEvent> localizerBus;

        final EventHandler<ContainersLauncherEvent> launcherBus;

        final EventHandler<ContainersMonitorEvent> monitorBus;

        final EventHandler<AuxServicesEvent> auxBus;

        final EventHandler<ApplicationEvent> appBus;

        final EventHandler<LogHandlerEvent> LogBus;

        final EventHandler<ContainerSchedulerEvent> schedBus;

        final ContainersLauncher launcher;

        final ContainerLaunchContext ctxt;

        final ContainerId cId;

        final Container c;

        final Map<String, LocalResource> localResources;

        final Map<String, ByteBuffer> serviceData;

        final Context context = Mockito.mock(Context.class);

        private final DeletionService delService;

        private final Map<ContainerState, ContainerEventType> initStateToEvent = new HashMap<>();

        private final Map<ContainerEventType, ContainerState> eventToFinalState = new HashMap<>();

        WrappedContainer(int appId, long timestamp, int id, String user) throws IOException {
            this(appId, timestamp, id, user, null);
        }

        WrappedContainer(int appId, long timestamp, int id, String user, ContainerRetryContext containerRetryContext) throws IOException {
            this(appId, timestamp, id, user, true, false, containerRetryContext);
        }

        WrappedContainer(int appId, long timestamp, int id, String user, boolean withLocalRes, boolean withServiceData) throws IOException {
            this(appId, timestamp, id, user, withLocalRes, withServiceData, null);
        }

        @SuppressWarnings("rawtypes")
        WrappedContainer(int appId, long timestamp, int id, String user, boolean withLocalRes, boolean withServiceData, ContainerRetryContext containerRetryContext) throws IOException {
            dispatcher = new DrainDispatcher();
            dispatcher.init(new Configuration());
            localizerBus = Mockito.mock(EventHandler.class);
            launcherBus = Mockito.mock(EventHandler.class);
            monitorBus = Mockito.mock(EventHandler.class);
            auxBus = Mockito.mock(EventHandler.class);
            appBus = Mockito.mock(EventHandler.class);
            LogBus = Mockito.mock(EventHandler.class);
            delService = Mockito.mock(DeletionService.class);
            schedBus = new ContainerScheduler(context, dispatcher, metrics, 0) {
                @Override
                protected void scheduleContainer(Container container) {
                    container.sendLaunchEvent();
                }
            };
            dispatcher.register(LocalizationEventType.class, localizerBus);
            dispatcher.register(ContainersLauncherEventType.class, launcherBus);
            dispatcher.register(ContainersMonitorEventType.class, monitorBus);
            dispatcher.register(ContainerSchedulerEventType.class, schedBus);
            dispatcher.register(AuxServicesEventType.class, auxBus);
            dispatcher.register(ApplicationEventType.class, appBus);
            dispatcher.register(LogHandlerEventType.class, LogBus);
            Mockito.when(context.getApplications()).thenReturn(new ConcurrentHashMap<org.apache.hadoop.yarn.api.records.ApplicationId, org.apache.hadoop.yarn.server.nodemanager.containermanager.application.Application>());
            NMNullStateStoreService stateStore = new NMNullStateStoreService();
            Mockito.when(context.getNMStateStore()).thenReturn(stateStore);
            NodeStatusUpdater nodeStatusUpdater = Mockito.mock(NodeStatusUpdater.class);
            Mockito.when(context.getNodeStatusUpdater()).thenReturn(nodeStatusUpdater);
            ContainerExecutor executor = Mockito.mock(ContainerExecutor.class);
            Mockito.doNothing().when(executor).pauseContainer(ArgumentMatchers.any(Container.class));
            Mockito.doNothing().when(executor).resumeContainer(ArgumentMatchers.any(Container.class));
            launcher = new ContainersLauncher(context, dispatcher, executor, null, null);
            // create a mock ExecutorService, which will not really launch
            // ContainerLaunch at all.
            launcher.containerLauncher = Mockito.mock(ExecutorService.class);
            Future future = Mockito.mock(Future.class);
            Mockito.when(launcher.containerLauncher.submit(ArgumentMatchers.any(Callable.class))).thenReturn(future);
            Mockito.when(future.isDone()).thenReturn(false);
            Mockito.when(future.cancel(false)).thenReturn(true);
            launcher.init(new Configuration());
            launcher.start();
            dispatcher.register(ContainersLauncherEventType.class, launcher);
            ctxt = Mockito.mock(ContainerLaunchContext.class);
            org.apache.hadoop.yarn.api.records.Container mockContainer = Mockito.mock(org.apache.hadoop.yarn.api.records.Container.class);
            cId = BuilderUtils.newContainerId(appId, 1, timestamp, id);
            Mockito.when(mockContainer.getId()).thenReturn(cId);
            Resource resource = BuilderUtils.newResource(1024, 1);
            Mockito.when(mockContainer.getResource()).thenReturn(resource);
            String host = "127.0.0.1";
            int port = 1234;
            long currentTime = System.currentTimeMillis();
            ContainerTokenIdentifier identifier = new ContainerTokenIdentifier(cId, "127.0.0.1", user, resource, (currentTime + 10000L), 123, currentTime, Priority.newInstance(0), 0);
            Token token = BuilderUtils.newContainerToken(BuilderUtils.newNodeId(host, port), "password".getBytes(), identifier);
            Mockito.when(mockContainer.getContainerToken()).thenReturn(token);
            if (withLocalRes) {
                Random r = new Random();
                long seed = r.nextLong();
                r.setSeed(seed);
                System.out.println(("WrappedContainerLocalResource seed: " + seed));
                localResources = TestContainer.createLocalResources(r);
            } else {
                localResources = Collections.<String, LocalResource>emptyMap();
            }
            Mockito.when(ctxt.getLocalResources()).thenReturn(localResources);
            if (withServiceData) {
                Random r = new Random();
                long seed = r.nextLong();
                r.setSeed(seed);
                System.out.println(("ServiceData seed: " + seed));
                serviceData = TestContainer.createServiceData(r);
            } else {
                serviceData = Collections.<String, ByteBuffer>emptyMap();
            }
            Mockito.when(ctxt.getServiceData()).thenReturn(serviceData);
            Mockito.when(ctxt.getContainerRetryContext()).thenReturn(containerRetryContext);
            Mockito.when(context.getDeletionService()).thenReturn(delService);
            ContainerStateTransitionListener listener = new ContainerStateTransitionListener() {
                @Override
                public void init(Context cntxt) {
                }

                @Override
                public void preTransition(ContainerImpl op, ContainerState beforeState, ContainerEvent eventToBeProcessed) {
                    initStateToEvent.put(beforeState, eventToBeProcessed.getType());
                }

                @Override
                public void postTransition(ContainerImpl op, ContainerState beforeState, ContainerState afterState, ContainerEvent processedEvent) {
                    eventToFinalState.put(processedEvent.getType(), afterState);
                }
            };
            NodeManager.DefaultContainerStateListener multi = new NodeManager.DefaultContainerStateListener();
            multi.addListener(listener);
            Mockito.when(context.getContainerStateTransitionListener()).thenReturn(multi);
            c = new ContainerImpl(conf, dispatcher, ctxt, null, metrics, identifier, context);
            dispatcher.register(ContainerEventType.class, new EventHandler<ContainerEvent>() {
                @Override
                public void handle(ContainerEvent event) {
                    c.handle(event);
                }
            });
            dispatcher.start();
        }

        private void drainDispatcherEvents() {
            dispatcher.await();
        }

        public void finished() {
            dispatcher.stop();
        }

        public void initContainer() {
            c.handle(new ContainerEvent(cId, INIT_CONTAINER));
            drainDispatcherEvents();
        }

        public void resourceFailedContainer() {
            c.handle(new ContainerEvent(cId, RESOURCE_FAILED));
            drainDispatcherEvents();
        }

        public void handleContainerResourceFailedEvent() {
            c.handle(new ContainerResourceFailedEvent(cId, null, null));
            drainDispatcherEvents();
        }

        // Localize resources
        // Skip some resources so as to consider them failed
        public Map<Path, List<String>> doLocalizeResources(boolean checkLocalizingState, int skipRsrcCount) throws URISyntaxException {
            Path cache = new Path("file:///cache");
            Map<Path, List<String>> localPaths = new HashMap<Path, List<String>>();
            int counter = 0;
            for (Map.Entry<String, LocalResource> rsrc : localResources.entrySet()) {
                if ((counter++) < skipRsrcCount) {
                    continue;
                }
                if (checkLocalizingState) {
                    Assert.assertEquals(LOCALIZING, c.getContainerState());
                }
                LocalResourceRequest req = new LocalResourceRequest(rsrc.getValue());
                Path p = new Path(cache, rsrc.getKey());
                localPaths.put(p, Arrays.asList(rsrc.getKey()));
                // rsrc copied to p
                c.handle(new ContainerResourceLocalizedEvent(c.getContainerId(), req, p));
            }
            drainDispatcherEvents();
            return localPaths;
        }

        public Map<Path, List<String>> localizeResources() throws URISyntaxException {
            return doLocalizeResources(true, 0);
        }

        public void localizeResourcesFromInvalidState(int skipRsrcCount) throws URISyntaxException {
            doLocalizeResources(false, skipRsrcCount);
        }

        public void failLocalizeSpecificResource(String rsrcKey) throws URISyntaxException {
            LocalResource rsrc = localResources.get(rsrcKey);
            LocalResourceRequest req = new LocalResourceRequest(rsrc);
            Exception e = new Exception(FAKE_LOCALIZATION_ERROR);
            c.handle(new ContainerResourceFailedEvent(c.getContainerId(), req, e.getMessage()));
            drainDispatcherEvents();
        }

        // fail to localize some resources
        public void failLocalizeResources(int failRsrcCount) throws URISyntaxException {
            int counter = 0;
            for (Map.Entry<String, LocalResource> rsrc : localResources.entrySet()) {
                if (counter >= failRsrcCount) {
                    break;
                }
                ++counter;
                LocalResourceRequest req = new LocalResourceRequest(rsrc.getValue());
                Exception e = new Exception(FAKE_LOCALIZATION_ERROR);
                c.handle(new ContainerResourceFailedEvent(c.getContainerId(), req, e.getMessage()));
            }
            drainDispatcherEvents();
        }

        public void launchContainer() {
            c.handle(new ContainerEvent(cId, CONTAINER_LAUNCHED));
            drainDispatcherEvents();
        }

        public void containerSuccessful() {
            c.handle(new ContainerEvent(cId, CONTAINER_EXITED_WITH_SUCCESS));
            drainDispatcherEvents();
        }

        public void containerResourcesCleanup() {
            c.handle(new ContainerEvent(cId, ContainerEventType.CONTAINER_RESOURCES_CLEANEDUP));
            drainDispatcherEvents();
        }

        public void dockerContainerResourcesCleanup() {
            c.handle(new ContainerEvent(cId, ContainerEventType.CONTAINER_RESOURCES_CLEANEDUP));
            // check if containerlauncher cleans up the container launch.
            Mockito.verify(this.launcherBus).handle(ArgumentMatchers.refEq(new ContainersLauncherEvent(this.c, ContainersLauncherEventType.CLEANUP_CONTAINER), "timestamp"));
            drainDispatcherEvents();
        }

        public void setupDockerContainerEnv() {
            Map<String, String> env = new HashMap<>();
            env.put(ENV_CONTAINER_TYPE, CONTAINER_RUNTIME_DOCKER);
            Mockito.when(this.ctxt.getEnvironment()).thenReturn(env);
        }

        public void containerFailed(int exitCode) {
            String diagnosticMsg = "Container completed with exit code " + exitCode;
            c.handle(new ContainerExitEvent(cId, CONTAINER_EXITED_WITH_FAILURE, exitCode, diagnosticMsg));
            ContainerStatus containerStatus = c.cloneAndGetContainerStatus();
            assert containerStatus.getDiagnostics().contains(diagnosticMsg);
            assert (containerStatus.getExitStatus()) == exitCode;
            drainDispatcherEvents();
            // If container needs retry, relaunch it
            if ((c.getContainerState()) == (RELAUNCHING)) {
                launchContainer();
            }
        }

        public void killContainer() {
            c.handle(new ContainerKillEvent(cId, ContainerExitStatus.KILLED_BY_RESOURCEMANAGER, "KillRequest"));
            drainDispatcherEvents();
        }

        public void pauseContainer() {
            c.handle(new ContainerPauseEvent(cId, "PauseRequest"));
            drainDispatcherEvents();
        }

        public void resumeContainer() {
            c.handle(new ContainerResumeEvent(cId, "ResumeRequest"));
            drainDispatcherEvents();
        }

        public void containerKilledOnRequest() {
            int exitCode = ContainerExitStatus.KILLED_BY_RESOURCEMANAGER;
            String diagnosticMsg = "Container completed with exit code " + exitCode;
            c.handle(new ContainerExitEvent(cId, CONTAINER_KILLED_ON_REQUEST, exitCode, diagnosticMsg));
            ContainerStatus containerStatus = c.cloneAndGetContainerStatus();
            assert containerStatus.getDiagnostics().contains(diagnosticMsg);
            assert (containerStatus.getExitStatus()) == exitCode;
            drainDispatcherEvents();
        }

        public int getLocalResourceCount() {
            return localResources.size();
        }

        public String getDiagnostics() {
            return c.cloneAndGetContainerStatus().getDiagnostics();
        }

        public SlidingWindowRetryPolicy getRetryPolicy() {
            return ((ContainerImpl) (c)).getRetryPolicy();
        }
    }
}

