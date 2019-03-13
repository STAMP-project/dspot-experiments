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
package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;


import ContainerState.COMPLETE;
import FinalApplicationStatus.SUCCEEDED;
import Priority.UNDEFINED;
import RMAppAttemptImpl.AM_CONTAINER_PRIORITY;
import RMAppAttemptState.ALLOCATED_SAVING;
import RMAppAttemptState.FAILED;
import RMAppAttemptState.FINAL_SAVING;
import RMAppAttemptState.KILLED;
import RMAppAttemptState.RUNNING;
import RMAppAttemptState.SUBMITTED;
import ResourceRequest.ANY;
import SchedulerUtils.LOST_CONTAINER;
import YarnApplicationAttemptState.ALLOCATED;
import YarnApplicationAttemptState.LAUNCHED;
import YarnApplicationAttemptState.NEW;
import YarnApplicationAttemptState.SCHEDULED;
import YarnConfiguration.APPLICATION_HISTORY_ENABLED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.ApplicationMasterService;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.AMLauncherEvent;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.ApplicationMasterLauncher;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppFailedAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeFinishedContainersPulledByAMEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerUpdates;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RMAppAttemptEventType.ATTEMPT_ADDED;
import static RMAppAttemptEventType.ATTEMPT_NEW_SAVED;
import static RMAppAttemptEventType.CONTAINER_ALLOCATED;
import static RMAppAttemptEventType.EXPIRE;
import static RMAppAttemptEventType.FAIL;
import static RMAppAttemptEventType.KILL;
import static RMAppAttemptEventType.LAUNCHED;
import static RMAppAttemptEventType.LAUNCH_FAILED;
import static RMAppAttemptEventType.RECOVER;
import static RMAppAttemptEventType.REGISTERED;
import static RMAppAttemptEventType.START;


@RunWith(Parameterized.class)
public class TestRMAppAttemptTransitions {
    private static final Logger LOG = LoggerFactory.getLogger(TestRMAppAttemptTransitions.class);

    private static final String EMPTY_DIAGNOSTICS = "";

    private static final String FAILED_DIAGNOSTICS = "Attempt failed by user.";

    private static final String RM_WEBAPP_ADDR = WebAppUtils.getResolvedRMWebAppURLWithScheme(new Configuration());

    private static final String AHS_WEBAPP_ADDR = (WebAppUtils.getHttpSchemePrefix(new Configuration())) + (WebAppUtils.getAHSWebAppURLWithoutScheme(new Configuration()));

    private boolean isSecurityEnabled;

    private RMContext rmContext;

    private RMContext spyRMContext;

    private YarnScheduler scheduler;

    private ResourceScheduler resourceScheduler;

    private ApplicationMasterService masterService;

    private ApplicationMasterLauncher applicationMasterLauncher;

    private AMLivelinessMonitor amLivelinessMonitor;

    private AMLivelinessMonitor amFinishingMonitor;

    private RMApplicationHistoryWriter writer;

    private SystemMetricsPublisher publisher;

    private RMStateStore store;

    private RMAppImpl application;

    private RMAppAttempt applicationAttempt;

    private Configuration conf = new Configuration();

    private AMRMTokenSecretManager amRMTokenManager = Mockito.spy(new AMRMTokenSecretManager(conf, rmContext));

    private ClientToAMTokenSecretManagerInRM clientToAMTokenManager = Mockito.spy(new ClientToAMTokenSecretManagerInRM());

    private NMTokenSecretManagerInRM nmTokenManager = Mockito.spy(new NMTokenSecretManagerInRM(conf));

    private boolean transferStateFromPreviousAttempt = false;

    private EventHandler<RMNodeEvent> rmnodeEventHandler;

    private final class TestApplicationAttemptEventDispatcher implements EventHandler<RMAppAttemptEvent> {
        @Override
        public void handle(RMAppAttemptEvent event) {
            ApplicationAttemptId appID = event.getApplicationAttemptId();
            Assert.assertEquals(applicationAttempt.getAppAttemptId(), appID);
            try {
                applicationAttempt.handle(event);
            } catch (Throwable t) {
                TestRMAppAttemptTransitions.LOG.error(((("Error in handling event type " + (event.getType())) + " for application ") + appID), t);
            }
        }
    }

    // handle all the RM application events - same as in ResourceManager.java
    private final class TestApplicationEventDispatcher implements EventHandler<RMAppEvent> {
        @Override
        public void handle(RMAppEvent event) {
            Assert.assertEquals(application.getApplicationId(), event.getApplicationId());
            if (event instanceof RMAppFailedAttemptEvent) {
                transferStateFromPreviousAttempt = getTransferStateFromPreviousAttempt();
            }
            try {
                application.handle(event);
            } catch (Throwable t) {
                TestRMAppAttemptTransitions.LOG.error(((("Error in handling event type " + (event.getType())) + " for application ") + (application.getApplicationId())), t);
            }
        }
    }

    private final class TestSchedulerEventDispatcher implements EventHandler<SchedulerEvent> {
        @Override
        public void handle(SchedulerEvent event) {
            scheduler.handle(event);
        }
    }

    private final class TestAMLauncherEventDispatcher implements EventHandler<AMLauncherEvent> {
        @Override
        public void handle(AMLauncherEvent event) {
            applicationMasterLauncher.handle(event);
        }
    }

    private static int appId = 1;

    private ApplicationSubmissionContext submissionContext = null;

    private boolean unmanagedAM;

    public TestRMAppAttemptTransitions(Boolean isSecurityEnabled) {
        this.isSecurityEnabled = isSecurityEnabled;
    }

    @Test
    public void testUsageReport() {
        // scheduler has info on running apps
        ApplicationAttemptId attemptId = applicationAttempt.getAppAttemptId();
        ApplicationResourceUsageReport appResUsgRpt = Mockito.mock(ApplicationResourceUsageReport.class);
        Mockito.when(appResUsgRpt.getMemorySeconds()).thenReturn(123456L);
        Mockito.when(appResUsgRpt.getVcoreSeconds()).thenReturn(55544L);
        Mockito.when(scheduler.getAppResourceUsageReport(ArgumentMatchers.any(ApplicationAttemptId.class))).thenReturn(appResUsgRpt);
        // start and finish the attempt
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptUnregistrationEvent(attemptId, "", FinalApplicationStatus.SUCCEEDED, ""));
        // expect usage stats to come from the scheduler report
        ApplicationResourceUsageReport report = applicationAttempt.getApplicationResourceUsageReport();
        Assert.assertEquals(123456L, report.getMemorySeconds());
        Assert.assertEquals(55544L, report.getVcoreSeconds());
        // finish app attempt and remove it from scheduler
        Mockito.when(appResUsgRpt.getMemorySeconds()).thenReturn(223456L);
        Mockito.when(appResUsgRpt.getVcoreSeconds()).thenReturn(75544L);
        sendAttemptUpdateSavedEvent(applicationAttempt);
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(attemptId, ContainerStatus.newInstance(amContainer.getId(), COMPLETE, "", 0), anyNodeId));
        Mockito.when(scheduler.getSchedulerAppInfo(ArgumentMatchers.eq(attemptId))).thenReturn(null);
        report = applicationAttempt.getApplicationResourceUsageReport();
        Assert.assertEquals(223456, report.getMemorySeconds());
        Assert.assertEquals(75544, report.getVcoreSeconds());
    }

    @Test
    public void testUnmanagedAMUnexpectedRegistration() {
        unmanagedAM = true;
        Mockito.when(submissionContext.getUnmanagedAM()).thenReturn(true);
        // submit AM and check it goes to SUBMITTED state
        submitApplicationAttempt();
        Assert.assertEquals(SUBMITTED, applicationAttempt.getAppAttemptState());
        // launch AM and verify attempt failed
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptRegistrationEvent(applicationAttempt.getAppAttemptId(), "host", 8042, "oldtrackingurl"));
        Assert.assertEquals(YarnApplicationAttemptState.SUBMITTED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptSubmittedToFailedState("Unmanaged AM must register after AM attempt reaches LAUNCHED state.");
    }

    @Test
    public void testUnmanagedAMContainersCleanup() {
        unmanagedAM = true;
        Mockito.when(submissionContext.getUnmanagedAM()).thenReturn(true);
        Mockito.when(submissionContext.getKeepContainersAcrossApplicationAttempts()).thenReturn(true);
        // submit AM and check it goes to SUBMITTED state
        submitApplicationAttempt();
        // launch AM and verify attempt failed
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptRegistrationEvent(applicationAttempt.getAppAttemptId(), "host", 8042, "oldtrackingurl"));
        Assert.assertEquals(YarnApplicationAttemptState.SUBMITTED, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertFalse(transferStateFromPreviousAttempt);
    }

    @Test
    public void testNewToKilled() {
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(NEW, applicationAttempt.createApplicationAttemptState());
        testAppAttemptKilledState(null, TestRMAppAttemptTransitions.EMPTY_DIAGNOSTICS);
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
    }

    @Test
    public void testNewToRecovered() {
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), RECOVER));
        testAppAttemptRecoveredState();
    }

    @Test
    public void testSubmittedToKilled() {
        submitApplicationAttempt();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(YarnApplicationAttemptState.SUBMITTED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptKilledState(null, TestRMAppAttemptTransitions.EMPTY_DIAGNOSTICS);
    }

    @Test
    public void testScheduledToKilled() {
        scheduleApplicationAttempt();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(SCHEDULED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptKilledState(null, TestRMAppAttemptTransitions.EMPTY_DIAGNOSTICS);
    }

    @Test
    public void testAMCrashAtScheduled() {
        // This is to test sending CONTAINER_FINISHED event at SCHEDULED state.
        // Verify the state transition is correct.
        scheduleApplicationAttempt();
        ContainerStatus cs = SchedulerUtils.createAbnormalContainerStatus(BuilderUtils.newContainerId(applicationAttempt.getAppAttemptId(), 1), LOST_CONTAINER);
        // send CONTAINER_FINISHED event at SCHEDULED state,
        // The state should be FINAL_SAVING with previous state SCHEDULED
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), cs, anyNodeId));
        // createApplicationAttemptState will return previous state (SCHEDULED),
        // if the current state is FINAL_SAVING.
        Assert.assertEquals(SCHEDULED, applicationAttempt.createApplicationAttemptState());
        // send ATTEMPT_UPDATE_SAVED event,
        // verify the state is changed to state FAILED.
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        verifyApplicationAttemptFinished(FAILED);
    }

    @Test
    public void testAllocatedToKilled() {
        Container amContainer = allocateApplicationAttempt();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(ALLOCATED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptKilledState(amContainer, TestRMAppAttemptTransitions.EMPTY_DIAGNOSTICS);
    }

    @Test
    public void testAllocatedToFailed() {
        Container amContainer = allocateApplicationAttempt();
        String diagnostics = "Launch Failed";
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCH_FAILED, diagnostics));
        Assert.assertEquals(ALLOCATED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptFailedState(amContainer, diagnostics);
    }

    @Test(timeout = 10000)
    public void testAllocatedToRunning() {
        Container amContainer = allocateApplicationAttempt();
        // Register attempt event arrives before launched attempt event
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        launchApplicationAttempt(amContainer, RUNNING);
    }

    @Test(timeout = 10000)
    public void testCreateAppAttemptReport() {
        RMAppAttemptState[] attemptStates = RMAppAttemptState.values();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        // ALL RMAppAttemptState TO BE CHECK
        RMAppAttempt attempt = Mockito.spy(applicationAttempt);
        for (RMAppAttemptState rmAppAttemptState : attemptStates) {
            Mockito.when(attempt.getState()).thenReturn(rmAppAttemptState);
            attempt.createApplicationAttemptReport();
        }
    }

    @Test(timeout = 10000)
    public void testLaunchedAtFinalSaving() {
        Container amContainer = allocateApplicationAttempt();
        // ALLOCATED->FINAL_SAVING
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        // verify for both launched and launch_failed transitions in final_saving
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCHED));
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCH_FAILED, "Launch Failed"));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        testAppAttemptKilledState(amContainer, TestRMAppAttemptTransitions.EMPTY_DIAGNOSTICS);
        // verify for both launched and launch_failed transitions in killed
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCHED));
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCH_FAILED, "Launch Failed"));
        Assert.assertEquals(KILLED, applicationAttempt.getAppAttemptState());
    }

    @Test(timeout = 10000)
    public void testAttemptAddedAtFinalSaving() {
        submitApplicationAttempt();
        // SUBMITTED->FINAL_SAVING
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), ATTEMPT_ADDED));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
    }

    @Test(timeout = 10000)
    public void testAttemptRegisteredAtFailed() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        // send CONTAINER_FINISHED event
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        // send REGISTERED event
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), REGISTERED));
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
    }

    @Test
    public void testAttemptLaunchFailedAtFailed() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        // send CONTAINER_FINISHED event
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        // send LAUNCH_FAILED event
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), LAUNCH_FAILED));
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
    }

    @Test
    public void testAMCrashAtAllocated() {
        Container amContainer = allocateApplicationAttempt();
        String containerDiagMsg = "some error";
        int exitCode = 123;
        ContainerStatus cs = BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, containerDiagMsg, exitCode, amContainer.getResource());
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), cs, anyNodeId));
        Assert.assertEquals(ALLOCATED, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
        verifyApplicationAttemptFinished(FAILED);
        boolean shouldCheckURL = (applicationAttempt.getTrackingUrl()) != null;
        verifyAMCrashAtAllocatedDiagnosticInfo(applicationAttempt.getDiagnostics(), exitCode, shouldCheckURL);
    }

    @Test
    public void testRunningToFailed() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        String containerDiagMsg = "some error";
        int exitCode = 123;
        ContainerStatus cs = BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, containerDiagMsg, exitCode, amContainer.getResource());
        ApplicationAttemptId appAttemptId = applicationAttempt.getAppAttemptId();
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(appAttemptId, cs, anyNodeId));
        // ignored ContainerFinished and Expire at FinalSaving if we were supposed
        // to Failed state.
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(0, applicationAttempt.getJustFinishedContainers().size());
        Assert.assertEquals(amContainer, applicationAttempt.getMasterContainer());
        Assert.assertEquals(0, application.getRanNodes().size());
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.RM_WEBAPP_ADDR, "cluster", "app", applicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getTrackingUrl());
        verifyAMHostAndPortInvalidated();
        verifyApplicationAttemptFinished(FAILED);
    }

    @Test
    public void testRunningToKilled() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        // ignored ContainerFinished and Expire at FinalSaving if we were supposed
        // to Killed state.
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(KILLED, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(0, applicationAttempt.getJustFinishedContainers().size());
        Assert.assertEquals(amContainer, applicationAttempt.getMasterContainer());
        Assert.assertEquals(0, application.getRanNodes().size());
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.RM_WEBAPP_ADDR, "cluster", "app", applicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getTrackingUrl());
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
        verifyAMHostAndPortInvalidated();
        verifyApplicationAttemptFinished(KILLED);
    }

    @Test(timeout = 10000)
    public void testLaunchedExpire() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        Assert.assertEquals(LAUNCHED, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        Assert.assertTrue("expire diagnostics missing", applicationAttempt.getDiagnostics().contains("timed out"));
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.RM_WEBAPP_ADDR, "cluster", "app", applicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getTrackingUrl());
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
        verifyApplicationAttemptFinished(FAILED);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testLaunchedFailWhileAHSEnabled() {
        Configuration myConf = new Configuration(conf);
        myConf.setBoolean(APPLICATION_HISTORY_ENABLED, true);
        ApplicationId applicationId = MockApps.newAppID(TestRMAppAttemptTransitions.appId);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.newInstance(applicationId, 2);
        RMAppAttempt myApplicationAttempt = new RMAppAttemptImpl(applicationAttempt.getAppAttemptId(), spyRMContext, scheduler, masterService, submissionContext, myConf, Collections.singletonList(BuilderUtils.newResourceRequest(AM_CONTAINER_PRIORITY, ANY, submissionContext.getResource(), 1)), application);
        // submit, schedule and allocate app attempt
        myApplicationAttempt.handle(new RMAppAttemptEvent(myApplicationAttempt.getAppAttemptId(), START));
        myApplicationAttempt.handle(new RMAppAttemptEvent(myApplicationAttempt.getAppAttemptId(), ATTEMPT_ADDED));
        Container amContainer = Mockito.mock(Container.class);
        Resource resource = BuilderUtils.newResource(2048, 1);
        Mockito.when(amContainer.getId()).thenReturn(BuilderUtils.newContainerId(myApplicationAttempt.getAppAttemptId(), 1));
        Mockito.when(amContainer.getResource()).thenReturn(resource);
        Allocation allocation = Mockito.mock(Allocation.class);
        Mockito.when(allocation.getContainers()).thenReturn(Collections.singletonList(amContainer));
        Mockito.when(scheduler.allocate(ArgumentMatchers.any(ApplicationAttemptId.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(), ArgumentMatchers.any(List.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(ContainerUpdates.class))).thenReturn(allocation);
        RMContainer rmContainer = Mockito.mock(RMContainerImpl.class);
        Mockito.when(scheduler.getRMContainer(amContainer.getId())).thenReturn(rmContainer);
        myApplicationAttempt.handle(new RMAppAttemptEvent(myApplicationAttempt.getAppAttemptId(), CONTAINER_ALLOCATED));
        Assert.assertEquals(ALLOCATED_SAVING, myApplicationAttempt.getAppAttemptState());
        myApplicationAttempt.handle(new RMAppAttemptEvent(myApplicationAttempt.getAppAttemptId(), ATTEMPT_NEW_SAVED));
        // launch app attempt
        myApplicationAttempt.handle(new RMAppAttemptEvent(myApplicationAttempt.getAppAttemptId(), LAUNCHED));
        Assert.assertEquals(LAUNCHED, myApplicationAttempt.createApplicationAttemptState());
        // fail container right after launched
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        myApplicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(myApplicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        sendAttemptUpdateSavedEvent(myApplicationAttempt);
        Assert.assertEquals(FAILED, myApplicationAttempt.getAppAttemptState());
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.AHS_WEBAPP_ADDR, "applicationhistory", "app", myApplicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, myApplicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, myApplicationAttempt.getTrackingUrl());
    }

    @Test(timeout = 20000)
    public void testRunningExpire() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        Assert.assertTrue("expire diagnostics missing", applicationAttempt.getDiagnostics().contains("timed out"));
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.RM_WEBAPP_ADDR, "cluster", "app", applicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getTrackingUrl());
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
        verifyAMHostAndPortInvalidated();
        verifyApplicationAttemptFinished(FAILED);
    }

    @Test
    public void testUnregisterToKilledFinishing() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        unregisterApplicationAttempt(amContainer, FinalApplicationStatus.KILLED, "newtrackingurl", "Killed by user");
    }

    @Test
    public void testTrackingUrlUnmanagedAM() {
        testUnmanagedAMSuccess("oldTrackingUrl");
    }

    @Test
    public void testEmptyTrackingUrlUnmanagedAM() {
        testUnmanagedAMSuccess("");
    }

    @Test
    public void testNullTrackingUrlUnmanagedAM() {
        testUnmanagedAMSuccess(null);
    }

    @Test
    public void testManagedAMWithTrackingUrl() {
        testTrackingUrlManagedAM("theTrackingUrl");
    }

    @Test
    public void testManagedAMWithEmptyTrackingUrl() {
        testTrackingUrlManagedAM("");
    }

    @Test
    public void testManagedAMWithNullTrackingUrl() {
        testTrackingUrlManagedAM(null);
    }

    @Test
    public void testUnregisterToSuccessfulFinishing() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        unregisterApplicationAttempt(amContainer, SUCCEEDED, "mytrackingurl", "Successful");
    }

    @Test
    public void testFinishingKill() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.FAILED;
        String trackingUrl = "newtrackingurl";
        String diagnostics = "Job failed";
        unregisterApplicationAttempt(amContainer, finalStatus, trackingUrl, diagnostics);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        testAppAttemptFinishingState(amContainer, finalStatus, trackingUrl, diagnostics);
    }

    @Test
    public void testFinishingExpire() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.SUCCEEDED;
        String trackingUrl = "mytrackingurl";
        String diagnostics = "Successful";
        unregisterApplicationAttempt(amContainer, finalStatus, trackingUrl, diagnostics);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        testAppAttemptFinishedState(amContainer, finalStatus, trackingUrl, diagnostics, 0, false);
    }

    @Test
    public void testFinishingToFinishing() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.SUCCEEDED;
        String trackingUrl = "mytrackingurl";
        String diagnostics = "Successful";
        unregisterApplicationAttempt(amContainer, finalStatus, trackingUrl, diagnostics);
        // container must be AM container to move from FINISHING to FINISHED
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(BuilderUtils.newContainerId(applicationAttempt.getAppAttemptId(), 42), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        testAppAttemptFinishingState(amContainer, finalStatus, trackingUrl, diagnostics);
    }

    @Test
    public void testSuccessfulFinishingToFinished() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.SUCCEEDED;
        String trackingUrl = "mytrackingurl";
        String diagnostics = "Successful";
        unregisterApplicationAttempt(amContainer, finalStatus, trackingUrl, diagnostics);
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        testAppAttemptFinishedState(amContainer, finalStatus, trackingUrl, diagnostics, 0, false);
    }

    // While attempt is at FINAL_SAVING, Contaienr_Finished event may come before
    // Attempt_Saved event, we stay on FINAL_SAVING on Container_Finished event
    // and then directly jump from FINAL_SAVING to FINISHED state on Attempt_Saved
    // event
    @Test
    public void testFinalSavingToFinishedWithContainerFinished() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.SUCCEEDED;
        String trackingUrl = "mytrackingurl";
        String diagnostics = "Successful";
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptUnregistrationEvent(applicationAttempt.getAppAttemptId(), trackingUrl, finalStatus, diagnostics));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        // Container_finished event comes before Attempt_Saved event.
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        // send attempt_saved
        sendAttemptUpdateSavedEvent(applicationAttempt);
        testAppAttemptFinishedState(amContainer, finalStatus, trackingUrl, diagnostics, 0, false);
    }

    // While attempt is at FINAL_SAVING, Expire event may come before
    // Attempt_Saved event, we stay on FINAL_SAVING on Expire event and then
    // directly jump from FINAL_SAVING to FINISHED state on Attempt_Saved event.
    @Test
    public void testFinalSavingToFinishedWithExpire() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        FinalApplicationStatus finalStatus = FinalApplicationStatus.SUCCEEDED;
        String trackingUrl = "mytrackingurl";
        String diagnostics = "Successssseeeful";
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptUnregistrationEvent(applicationAttempt.getAppAttemptId(), trackingUrl, finalStatus, diagnostics));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        // Expire event comes before Attempt_saved event.
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), EXPIRE));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        // send attempt_saved
        sendAttemptUpdateSavedEvent(applicationAttempt);
        testAppAttemptFinishedState(amContainer, finalStatus, trackingUrl, diagnostics, 0, false);
    }

    @Test
    public void testFinishedContainer() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        // Complete one container
        ContainerId containerId1 = BuilderUtils.newContainerId(applicationAttempt.getAppAttemptId(), 2);
        Container container1 = Mockito.mock(Container.class);
        ContainerStatus containerStatus1 = Mockito.mock(ContainerStatus.class);
        Mockito.when(container1.getId()).thenReturn(containerId1);
        Mockito.when(containerStatus1.getContainerId()).thenReturn(containerId1);
        Mockito.when(container1.getNodeId()).thenReturn(NodeId.newInstance("host", 1234));
        application.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRunningOnNodeEvent(application.getApplicationId(), container1.getNodeId()));
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), containerStatus1, container1.getNodeId()));
        ArgumentCaptor<RMNodeFinishedContainersPulledByAMEvent> captor = ArgumentCaptor.forClass(RMNodeFinishedContainersPulledByAMEvent.class);
        // Verify justFinishedContainers
        Assert.assertEquals(1, applicationAttempt.getJustFinishedContainers().size());
        Assert.assertEquals(container1.getId(), applicationAttempt.getJustFinishedContainers().get(0).getContainerId());
        Assert.assertEquals(0, TestRMAppAttemptTransitions.getFinishedContainersSentToAM(applicationAttempt).size());
        // Verify finishedContainersSentToAM gets container after pull
        List<ContainerStatus> containerStatuses = applicationAttempt.pullJustFinishedContainers();
        Assert.assertEquals(1, containerStatuses.size());
        Mockito.verify(rmnodeEventHandler, Mockito.never()).handle(Mockito.any(RMNodeEvent.class));
        Assert.assertTrue(applicationAttempt.getJustFinishedContainers().isEmpty());
        Assert.assertEquals(1, TestRMAppAttemptTransitions.getFinishedContainersSentToAM(applicationAttempt).size());
        // Verify container is acked to NM via the RMNodeEvent after second pull
        containerStatuses = applicationAttempt.pullJustFinishedContainers();
        Assert.assertEquals(0, containerStatuses.size());
        Mockito.verify(rmnodeEventHandler).handle(captor.capture());
        Assert.assertEquals(container1.getId(), captor.getValue().getContainers().get(0));
        Assert.assertTrue(applicationAttempt.getJustFinishedContainers().isEmpty());
        Assert.assertEquals(0, TestRMAppAttemptTransitions.getFinishedContainersSentToAM(applicationAttempt).size());
        // verify if no containers to acknowledge to NM then event should not be
        // triggered. Number of times event invoked is 1 i.e on second pull
        containerStatuses = applicationAttempt.pullJustFinishedContainers();
        Assert.assertEquals(0, containerStatuses.size());
        Mockito.verify(rmnodeEventHandler, Mockito.times(1)).handle(Mockito.any(RMNodeEvent.class));
    }

    // this is to test user can get client tokens only after the client token
    // master key is saved in the state store and also registered in
    // ClientTokenSecretManager
    @Test
    public void testGetClientToken() throws Exception {
        Assume.assumeTrue(isSecurityEnabled);
        Container amContainer = allocateApplicationAttempt();
        // before attempt is launched, can not get ClientToken
        Token<ClientToAMTokenIdentifier> token = applicationAttempt.createClientToken(null);
        Assert.assertNull(token);
        launchApplicationAttempt(amContainer);
        // after attempt is launched , can get ClientToken
        token = applicationAttempt.createClientToken(null);
        Assert.assertNull(token);
        token = applicationAttempt.createClientToken("clientuser");
        Assert.assertNotNull(token);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(LAUNCHED, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        // after attempt is killed, can not get Client Token
        token = applicationAttempt.createClientToken(null);
        Assert.assertNull(token);
        token = applicationAttempt.createClientToken("clientuser");
        Assert.assertNull(token);
    }

    // this is to test master key is saved in the secret manager only after
    // attempt is launched and in secure-mode
    @Test
    public void testApplicationAttemptMasterKey() throws Exception {
        Container amContainer = allocateApplicationAttempt();
        ApplicationAttemptId appid = applicationAttempt.getAppAttemptId();
        boolean isMasterKeyExisted = clientToAMTokenManager.hasMasterKey(appid);
        if (isSecurityEnabled) {
            Assert.assertTrue(isMasterKeyExisted);
            Assert.assertNotNull(clientToAMTokenManager.getMasterKey(appid));
        } else {
            Assert.assertFalse(isMasterKeyExisted);
        }
        launchApplicationAttempt(amContainer);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), KILL));
        Assert.assertEquals(LAUNCHED, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        // after attempt is killed, can not get MasterKey
        isMasterKeyExisted = clientToAMTokenManager.hasMasterKey(appid);
        Assert.assertFalse(isMasterKeyExisted);
    }

    @Test
    public void testFailedToFailed() {
        // create a failed attempt.
        Mockito.when(submissionContext.getKeepContainersAcrossApplicationAttempts()).thenReturn(true);
        Mockito.when(application.getMaxAppAttempts()).thenReturn(2);
        Mockito.when(application.getNumFailedAppAttempts()).thenReturn(1);
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        ContainerStatus cs1 = ContainerStatus.newInstance(amContainer.getId(), COMPLETE, "some error", 123);
        ApplicationAttemptId appAttemptId = applicationAttempt.getAppAttemptId();
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(appAttemptId, cs1, anyNodeId));
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        // should not kill containers when attempt fails.
        Assert.assertTrue(transferStateFromPreviousAttempt);
        verifyApplicationAttemptFinished(FAILED);
        // failed attempt captured the container finished event.
        Assert.assertEquals(0, applicationAttempt.getJustFinishedContainers().size());
        ContainerStatus cs2 = ContainerStatus.newInstance(ContainerId.newContainerId(appAttemptId, 2), COMPLETE, "", 0);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(appAttemptId, cs2, anyNodeId));
        Assert.assertEquals(1, applicationAttempt.getJustFinishedContainers().size());
        boolean found = false;
        for (ContainerStatus containerStatus : applicationAttempt.getJustFinishedContainers()) {
            if (cs2.getContainerId().equals(containerStatus.getContainerId())) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testContainerRemovedBeforeAllocate() {
        scheduleApplicationAttempt();
        // Mock the allocation of AM container
        Container container = Mockito.mock(Container.class);
        Resource resource = BuilderUtils.newResource(2048, 1);
        Mockito.when(container.getId()).thenReturn(BuilderUtils.newContainerId(applicationAttempt.getAppAttemptId(), 1));
        Mockito.when(container.getResource()).thenReturn(resource);
        Allocation allocation = Mockito.mock(Allocation.class);
        Mockito.when(allocation.getContainers()).thenReturn(Collections.singletonList(container));
        Mockito.when(scheduler.allocate(ArgumentMatchers.any(ApplicationAttemptId.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(ContainerUpdates.class))).thenReturn(allocation);
        // container removed, so return null
        Mockito.when(scheduler.getRMContainer(container.getId())).thenReturn(null);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), CONTAINER_ALLOCATED));
        Assert.assertEquals(RMAppAttemptState.SCHEDULED, applicationAttempt.getAppAttemptState());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testContainersCleanupForLastAttempt() {
        // create a failed attempt.
        applicationAttempt = new RMAppAttemptImpl(applicationAttempt.getAppAttemptId(), spyRMContext, scheduler, masterService, submissionContext, new Configuration(), Collections.singletonList(BuilderUtils.newResourceRequest(AM_CONTAINER_PRIORITY, ANY, submissionContext.getResource(), 1)), application);
        Mockito.when(submissionContext.getKeepContainersAcrossApplicationAttempts()).thenReturn(true);
        Mockito.when(submissionContext.getMaxAppAttempts()).thenReturn(1);
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        ContainerStatus cs1 = ContainerStatus.newInstance(amContainer.getId(), COMPLETE, "some error", 123);
        ApplicationAttemptId appAttemptId = applicationAttempt.getAppAttemptId();
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(appAttemptId, cs1, anyNodeId));
        Assert.assertEquals(YarnApplicationAttemptState.RUNNING, applicationAttempt.createApplicationAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        Assert.assertFalse(transferStateFromPreviousAttempt);
        verifyApplicationAttemptFinished(FAILED);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testScheduleTransitionReplaceAMContainerRequestWithDefaults() {
        YarnScheduler mockScheduler = Mockito.mock(YarnScheduler.class);
        Mockito.when(mockScheduler.allocate(ArgumentMatchers.any(ApplicationAttemptId.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(ContainerUpdates.class))).thenAnswer(new Answer<Allocation>() {
            @SuppressWarnings("rawtypes")
            @Override
            public Allocation answer(InvocationOnMock invocation) throws Throwable {
                ResourceRequest rr = ((ResourceRequest) (((List) (invocation.getArguments()[1])).get(0)));
                // capacity shouldn't changed
                Assert.assertEquals(Resource.newInstance(3333, 1), rr.getCapability());
                Assert.assertEquals("label-expression", rr.getNodeLabelExpression());
                // priority, #container, relax-locality will be changed
                Assert.assertEquals(AM_CONTAINER_PRIORITY, rr.getPriority());
                Assert.assertEquals(1, rr.getNumContainers());
                Assert.assertEquals(ANY, rr.getResourceName());
                // just return an empty allocation
                List l = new ArrayList();
                Set s = new HashSet();
                return new Allocation(l, Resources.none(), s, s, l);
            }
        });
        // create an attempt.
        applicationAttempt = new RMAppAttemptImpl(applicationAttempt.getAppAttemptId(), spyRMContext, scheduler, masterService, submissionContext, new Configuration(), Collections.singletonList(ResourceRequest.newInstance(UNDEFINED, "host1", Resource.newInstance(3333, 1), 3, false, "label-expression")), application);
        new RMAppAttemptImpl.ScheduleTransition().transition(((RMAppAttemptImpl) (applicationAttempt)), null);
    }

    @Test(timeout = 30000)
    public void testNewToFailed() {
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), FAIL, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS));
        Assert.assertEquals(NEW, applicationAttempt.createApplicationAttemptState());
        testAppAttemptFailedState(null, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS);
        verifyTokenCount(applicationAttempt.getAppAttemptId(), 1);
    }

    @Test(timeout = 30000)
    public void testSubmittedToFailed() {
        submitApplicationAttempt();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), FAIL, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS));
        Assert.assertEquals(YarnApplicationAttemptState.SUBMITTED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptFailedState(null, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS);
    }

    @Test(timeout = 30000)
    public void testScheduledToFailed() {
        scheduleApplicationAttempt();
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), FAIL, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS));
        Assert.assertEquals(SCHEDULED, applicationAttempt.createApplicationAttemptState());
        testAppAttemptFailedState(null, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS);
    }

    @Test(timeout = 30000)
    public void testAllocatedToFailedUserTriggeredFailEvent() {
        Container amContainer = allocateApplicationAttempt();
        Assert.assertEquals(ALLOCATED, applicationAttempt.createApplicationAttemptState());
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), FAIL, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS));
        testAppAttemptFailedState(amContainer, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS);
    }

    @Test(timeout = 30000)
    public void testRunningToFailedUserTriggeredFailEvent() {
        Container amContainer = allocateApplicationAttempt();
        launchApplicationAttempt(amContainer);
        runApplicationAttempt(amContainer, "host", 8042, "oldtrackingurl", false);
        applicationAttempt.handle(new RMAppAttemptEvent(applicationAttempt.getAppAttemptId(), FAIL, TestRMAppAttemptTransitions.FAILED_DIAGNOSTICS));
        Assert.assertEquals(FINAL_SAVING, applicationAttempt.getAppAttemptState());
        sendAttemptUpdateSavedEvent(applicationAttempt);
        Assert.assertEquals(FAILED, applicationAttempt.getAppAttemptState());
        NodeId anyNodeId = NodeId.newInstance("host", 1234);
        applicationAttempt.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent(applicationAttempt.getAppAttemptId(), BuilderUtils.newContainerStatus(amContainer.getId(), COMPLETE, "", 0, amContainer.getResource()), anyNodeId));
        Assert.assertEquals(1, applicationAttempt.getJustFinishedContainers().size());
        Assert.assertEquals(amContainer, applicationAttempt.getMasterContainer());
        Assert.assertEquals(0, application.getRanNodes().size());
        String rmAppPageUrl = pjoin(TestRMAppAttemptTransitions.RM_WEBAPP_ADDR, "cluster", "app", applicationAttempt.getAppAttemptId().getApplicationId());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getOriginalTrackingUrl());
        Assert.assertEquals(rmAppPageUrl, applicationAttempt.getTrackingUrl());
        verifyAMHostAndPortInvalidated();
        verifyApplicationAttemptFinished(FAILED);
    }
}

