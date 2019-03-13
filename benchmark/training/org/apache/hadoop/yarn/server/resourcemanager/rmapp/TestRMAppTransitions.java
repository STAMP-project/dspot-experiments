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
package org.apache.hadoop.yarn.server.resourcemanager.rmapp;


import RMAppState.ACCEPTED;
import RMAppState.FAILED;
import RMAppState.FINAL_SAVING;
import RMAppState.FINISHED;
import RMAppState.FINISHING;
import RMAppState.KILLED;
import RMAppState.KILLING;
import RMAppState.NEW;
import RMAppState.RUNNING;
import RMServerUtils.DUMMY_APPLICATION_RESOURCE_USAGE_REPORT;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.RMAppManagerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.RMState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RMAppEventType.APP_ACCEPTED;
import static RMAppEventType.APP_REJECTED;
import static RMAppEventType.APP_SAVE_FAILED;
import static RMAppEventType.APP_UPDATE_SAVED;
import static RMAppEventType.ATTEMPT_FAILED;
import static RMAppEventType.ATTEMPT_FINISHED;
import static RMAppEventType.ATTEMPT_KILLED;
import static RMAppEventType.ATTEMPT_REGISTERED;
import static RMAppEventType.KILL;
import static RMAppEventType.START;


@RunWith(Parameterized.class)
public class TestRMAppTransitions {
    static final Logger LOG = LoggerFactory.getLogger(TestRMAppTransitions.class);

    private boolean isSecurityEnabled;

    private Configuration conf;

    private RMContext rmContext;

    private static int maxAppAttempts = YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;

    private static int appId = 1;

    private DrainDispatcher rmDispatcher;

    private RMStateStore store;

    private RMApplicationHistoryWriter writer;

    private SystemMetricsPublisher publisher;

    private YarnScheduler scheduler;

    private TestRMAppTransitions.TestSchedulerEventDispatcher schedulerDispatcher;

    private TestRMAppTransitions.TestApplicationManagerEventDispatcher appManagerDispatcher;

    private long testCaseStartTime;

    // ignore all the RM application attempt events
    private static final class TestApplicationAttemptEventDispatcher implements EventHandler<RMAppAttemptEvent> {
        private final RMContext rmContext;

        public TestApplicationAttemptEventDispatcher(RMContext rmContext) {
            this.rmContext = rmContext;
        }

        @Override
        public void handle(RMAppAttemptEvent event) {
            ApplicationId appId = event.getApplicationAttemptId().getApplicationId();
            RMApp rmApp = this.rmContext.getRMApps().get(appId);
            if (rmApp != null) {
                try {
                    rmApp.getRMAppAttempt(event.getApplicationAttemptId()).handle(event);
                } catch (Throwable t) {
                    TestRMAppTransitions.LOG.error(((("Error in handling event type " + (event.getType())) + " for application ") + appId), t);
                }
            }
        }
    }

    // handle all the RM application events - same as in ResourceManager.java
    private static final class TestApplicationEventDispatcher implements EventHandler<RMAppEvent> {
        private final RMContext rmContext;

        public TestApplicationEventDispatcher(RMContext rmContext) {
            this.rmContext = rmContext;
        }

        @Override
        public void handle(RMAppEvent event) {
            ApplicationId appID = event.getApplicationId();
            RMApp rmApp = this.rmContext.getRMApps().get(appID);
            if (rmApp != null) {
                try {
                    rmApp.handle(event);
                } catch (Throwable t) {
                    TestRMAppTransitions.LOG.error(((("Error in handling event type " + (event.getType())) + " for application ") + appID), t);
                }
            }
        }
    }

    // handle all the RM application manager events - same as in
    // ResourceManager.java
    private static final class TestApplicationManagerEventDispatcher implements EventHandler<RMAppManagerEvent> {
        List<RMAppManagerEvent> events = Lists.newArrayList();

        @Override
        public void handle(RMAppManagerEvent event) {
            TestRMAppTransitions.LOG.info(("Handling app manager event: " + event));
            events.add(event);
        }
    }

    // handle all the scheduler events - same as in ResourceManager.java
    private static final class TestSchedulerEventDispatcher implements EventHandler<SchedulerEvent> {
        public SchedulerEvent lastSchedulerEvent;

        @Override
        public void handle(SchedulerEvent event) {
            lastSchedulerEvent = event;
        }
    }

    public TestRMAppTransitions(boolean isSecurityEnabled) {
        this.isSecurityEnabled = isSecurityEnabled;
    }

    @Test
    public void testUnmanagedApp() throws Exception {
        ApplicationSubmissionContext subContext = new ApplicationSubmissionContextPBImpl();
        subContext.setUnmanagedAM(true);
        // test success path
        TestRMAppTransitions.LOG.info("--- START: testUnmanagedAppSuccessPath ---");
        final String diagMsg = "some diagnostics";
        RMApp application = testCreateAppFinished(subContext, diagMsg);
        Assert.assertTrue("Finished app missing diagnostics", ((application.getDiagnostics().indexOf(diagMsg)) != (-1)));
        // reset the counter of Mockito.verify
        Mockito.reset(writer);
        Mockito.reset(publisher);
        // test app fails after 1 app attempt failure
        TestRMAppTransitions.LOG.info("--- START: testUnmanagedAppFailPath ---");
        application = testCreateAppRunning(subContext);
        RMAppEvent event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, "", false);
        application.handle(event);
        rmDispatcher.await();
        RMAppAttempt appAttempt = application.getCurrentAppAttempt();
        Assert.assertEquals(1, appAttempt.getAppAttemptId().getAttemptId());
        sendAppUpdateSavedEvent(application);
        assertFailed(application, ".*Unmanaged application.*Failing the application.*");
        assertAppFinalStateSaved(application);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppSuccessPath() throws Exception {
        TestRMAppTransitions.LOG.info("--- START: testAppSuccessPath ---");
        final String diagMsg = "some diagnostics";
        RMApp application = testCreateAppFinished(null, diagMsg);
        Assert.assertTrue("Finished application missing diagnostics", ((application.getDiagnostics().indexOf(diagMsg)) != (-1)));
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppRecoverPath() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppRecoverPath ---");
        ApplicationSubmissionContext sub = Records.newRecord(ApplicationSubmissionContext.class);
        sub.setAMContainerSpec(prepareContainerLaunchContext());
        testCreateAppSubmittedRecovery(sub);
    }

    @Test(timeout = 30000)
    public void testAppNewKill() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewKill ---");
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppNewKill", new String[]{ "foo_group" });
        RMApp application = createNewTestApp(null);
        // NEW => KILLED event RMAppEventType.KILL
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        assertAppFinalStateNotSaved(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppNewReject() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewReject ---");
        RMApp application = createNewTestApp(null);
        // NEW => FAILED event RMAppEventType.APP_REJECTED
        String rejectedText = "Test Application Rejected";
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_REJECTED, rejectedText);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, rejectedText);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppNewRejectAddToStore() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewRejectAddToStore ---");
        RMApp application = createNewTestApp(null);
        // NEW => FAILED event RMAppEventType.APP_REJECTED
        String rejectedText = "Test Application Rejected";
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_REJECTED, rejectedText);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, rejectedText);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
        verifyRMAppFieldsForFinalTransitions(application);
        rmContext.getStateStore().removeApplication(application);
    }

    @Test(timeout = 30000)
    public void testAppNewSavingKill() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewSavingKill ---");
        RMApp application = testCreateAppNewSaving(null);
        // NEW_SAVING => KILLED event RMAppEventType.KILL
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppNewSavingKill", new String[]{ "foo_group" });
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppNewSavingReject() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewSavingReject ---");
        RMApp application = testCreateAppNewSaving(null);
        // NEW_SAVING => FAILED event RMAppEventType.APP_REJECTED
        String rejectedText = "Test Application Rejected";
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_REJECTED, rejectedText);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, rejectedText);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppNewSavingSaveReject() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppNewSavingSaveReject ---");
        RMApp application = testCreateAppNewSaving(null);
        // NEW_SAVING => FAILED event RMAppEventType.APP_SAVE_FAILED
        String rejectedText = "Test Application Rejected";
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_SAVE_FAILED, rejectedText);
        application.handle(event);
        rmDispatcher.await();
        assertFailed(application, rejectedText);
        Mockito.verify(store, Mockito.times(0)).updateApplicationState(ArgumentMatchers.any(ApplicationStateData.class));
        verifyApplicationFinished(FAILED);
        assertTimesAtFinish(application);
    }

    @Test(timeout = 30000)
    public void testAppSubmittedRejected() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppSubmittedRejected ---");
        RMApp application = testCreateAppSubmittedNoRecovery(null);
        // SUBMITTED => FAILED event RMAppEventType.APP_REJECTED
        String rejectedText = "app rejected";
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_REJECTED, rejectedText);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, rejectedText);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppSubmittedKill() throws IOException, InterruptedException {
        TestRMAppTransitions.LOG.info("--- START: testAppSubmittedKill---");
        RMApp application = testCreateAppSubmittedNoRecovery(null);
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppSubmittedKill", new String[]{ "foo_group" });
        // SUBMITTED => KILLED event RMAppEventType.KILL
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppAcceptedFailed() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppAcceptedFailed ---");
        RMApp application = testCreateAppAccepted(null);
        // ACCEPTED => ACCEPTED event RMAppEventType.RMAppEventType.ATTEMPT_FAILED
        Assert.assertTrue(((TestRMAppTransitions.maxAppAttempts) > 1));
        for (int i = 1; i < (TestRMAppTransitions.maxAppAttempts); i++) {
            RMAppEvent event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, "", false);
            application.handle(event);
            TestRMAppTransitions.assertAppState(ACCEPTED, application);
            event = new RMAppEvent(application.getApplicationId(), APP_ACCEPTED);
            application.handle(event);
            rmDispatcher.await();
            TestRMAppTransitions.assertAppState(ACCEPTED, application);
        }
        // ACCEPTED => FAILED event RMAppEventType.RMAppEventType.ATTEMPT_FAILED
        // after max application attempts
        String message = "Test fail";
        RMAppEvent event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, message, false);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, ((".*" + message) + ".*Failing the application.*"));
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
    }

    @Test
    public void testAppAcceptedKill() throws IOException, InterruptedException {
        TestRMAppTransitions.LOG.info("--- START: testAppAcceptedKill ---");
        RMApp application = testCreateAppAccepted(null);
        // ACCEPTED => KILLED event RMAppEventType.KILL
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppAcceptedKill", new String[]{ "foo_group" });
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        TestRMAppTransitions.assertAppState(KILLING, application);
        RMAppEvent appAttemptKilled = new RMAppEvent(application.getApplicationId(), ATTEMPT_KILLED, "Application killed by user.");
        application.handle(appAttemptKilled);
        TestRMAppTransitions.assertAppState(FINAL_SAVING, application);
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppAcceptedAttemptKilled() throws IOException, InterruptedException {
        TestRMAppTransitions.LOG.info("--- START: testAppAcceptedAttemptKilled ---");
        RMApp application = testCreateAppAccepted(null);
        // ACCEPTED => FINAL_SAVING event RMAppEventType.ATTEMPT_KILLED
        // When application recovery happens for attempt is KILLED but app is
        // RUNNING.
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), ATTEMPT_KILLED, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        TestRMAppTransitions.assertAppState(FINAL_SAVING, application);
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
    }

    @Test
    public void testAppRunningKill() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppRunningKill ---");
        RMApp application = testCreateAppRunning(null);
        // RUNNING => KILLED event RMAppEventType.KILL
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppRunningKill", new String[]{ "foo_group" });
        // SUBMITTED => KILLED event RMAppEventType.KILL
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        sendAttemptUpdateSavedEvent(application);
        sendAppUpdateSavedEvent(application);
        assertKilled(application);
        verifyApplicationFinished(KILLED);
        verifyAppRemovedSchedulerEvent(application, KILLED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppRunningFailed() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppRunningFailed ---");
        RMApp application = testCreateAppRunning(null);
        RMAppAttempt appAttempt = application.getCurrentAppAttempt();
        int expectedAttemptId = 1;
        Assert.assertEquals(expectedAttemptId, appAttempt.getAppAttemptId().getAttemptId());
        // RUNNING => FAILED/RESTARTING event RMAppEventType.ATTEMPT_FAILED
        Assert.assertTrue(((TestRMAppTransitions.maxAppAttempts) > 1));
        for (int i = 1; i < (TestRMAppTransitions.maxAppAttempts); i++) {
            RMAppEvent event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, "", false);
            application.handle(event);
            rmDispatcher.await();
            TestRMAppTransitions.assertAppState(ACCEPTED, application);
            appAttempt = application.getCurrentAppAttempt();
            Assert.assertEquals((++expectedAttemptId), appAttempt.getAppAttemptId().getAttemptId());
            event = new RMAppEvent(application.getApplicationId(), APP_ACCEPTED);
            application.handle(event);
            rmDispatcher.await();
            TestRMAppTransitions.assertAppState(ACCEPTED, application);
            event = new RMAppEvent(application.getApplicationId(), ATTEMPT_REGISTERED);
            application.handle(event);
            rmDispatcher.await();
            TestRMAppTransitions.assertAppState(RUNNING, application);
        }
        // RUNNING => FAILED/RESTARTING event RMAppEventType.ATTEMPT_FAILED
        // after max application attempts
        RMAppEvent event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, "", false);
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertFailed(application, ".*Failing the application.*");
        assertAppFinalStateSaved(application);
        // FAILED => FAILED event RMAppEventType.KILL
        event = new RMAppEvent(application.getApplicationId(), KILL, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        assertFailed(application, ".*Failing the application.*");
        assertAppFinalStateSaved(application);
        verifyApplicationFinished(FAILED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppAtFinishingIgnoreKill() throws Exception {
        TestRMAppTransitions.LOG.info("--- START: testAppAtFinishingIgnoreKill ---");
        RMApp application = testCreateAppFinishing(null);
        // FINISHING => FINISHED event RMAppEventType.KILL
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), KILL, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        TestRMAppTransitions.assertAppState(FINISHING, application);
    }

    // While App is at FINAL_SAVING, Attempt_Finished event may come before
    // App_Saved event, we stay on FINAL_SAVING on Attempt_Finished event
    // and then directly jump from FINAL_SAVING to FINISHED state on App_Saved
    // event
    @Test
    public void testAppFinalSavingToFinished() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppFinalSavingToFinished ---");
        RMApp application = testCreateAppFinalSaving(null);
        final String diagMsg = "some diagnostics";
        // attempt_finished event comes before attempt_saved event
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), ATTEMPT_FINISHED, diagMsg);
        application.handle(event);
        TestRMAppTransitions.assertAppState(FINAL_SAVING, application);
        RMAppEvent appUpdated = new RMAppEvent(application.getApplicationId(), APP_UPDATE_SAVED);
        application.handle(appUpdated);
        TestRMAppTransitions.assertAppState(FINISHED, application);
        assertTimesAtFinish(application);
        // finished without a proper unregister implies failed
        TestRMAppTransitions.assertFinalAppStatus(FinalApplicationStatus.FAILED, application);
        Assert.assertTrue("Finished app missing diagnostics", ((application.getDiagnostics().indexOf(diagMsg)) != (-1)));
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test
    public void testAppFinishedFinished() throws Exception {
        TestRMAppTransitions.LOG.info("--- START: testAppFinishedFinished ---");
        RMApp application = testCreateAppFinished(null, "");
        // FINISHED => FINISHED event RMAppEventType.KILL
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), KILL, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(FINISHED, application);
        StringBuilder diag = application.getDiagnostics();
        Assert.assertEquals("application diagnostics is not correct", "", diag.toString());
        verifyApplicationFinished(FINISHED);
        verifyAppRemovedSchedulerEvent(application, FINISHED);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppFailedFailed() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppFailedFailed ---");
        RMApp application = testCreateAppNewSaving(null);
        // NEW_SAVING => FAILED event RMAppEventType.APP_REJECTED
        RMAppEvent event = new RMAppEvent(application.getApplicationId(), APP_REJECTED, "");
        application.handle(event);
        rmDispatcher.await();
        sendAppUpdateSavedEvent(application);
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(FAILED, application);
        // FAILED => FAILED event RMAppEventType.KILL
        event = new RMAppEvent(application.getApplicationId(), KILL, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(FAILED, application);
        verifyApplicationFinished(FAILED);
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(FAILED, application);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppKilledKilled() throws IOException {
        TestRMAppTransitions.LOG.info("--- START: testAppKilledKilled ---");
        RMApp application = testCreateAppRunning(null);
        // RUNNING => KILLED event RMAppEventType.KILL
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("fooTestAppKilledKill", new String[]{ "foo_group" });
        // SUBMITTED => KILLED event RMAppEventType.KILL
        RMAppEvent event = new RMAppKillByClientEvent(application.getApplicationId(), "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        sendAttemptUpdateSavedEvent(application);
        sendAppUpdateSavedEvent(application);
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
        // KILLED => KILLED event RMAppEventType.ATTEMPT_FINISHED
        event = new RMAppEvent(application.getApplicationId(), ATTEMPT_FINISHED, "");
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
        // KILLED => KILLED event RMAppEventType.ATTEMPT_FAILED
        event = new RMAppFailedAttemptEvent(application.getApplicationId(), ATTEMPT_FAILED, "", false);
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
        // KILLED => KILLED event RMAppEventType.KILL
        event = new RMAppEvent(application.getApplicationId(), KILL, "Application killed by user.");
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
        verifyApplicationFinished(KILLED);
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
        verifyRMAppFieldsForFinalTransitions(application);
    }

    @Test(timeout = 30000)
    public void testAppStartAfterKilled() {
        TestRMAppTransitions.LOG.info("--- START: testAppStartAfterKilled ---");
        ApplicationId applicationId = MockApps.newAppID(((TestRMAppTransitions.appId)++));
        RMApp application = new RMAppImpl(applicationId, rmContext, conf, null, null, null, new ApplicationSubmissionContextPBImpl(), null, null, System.currentTimeMillis(), "YARN", null, null) {
            @Override
            protected void onInvalidStateTransition(RMAppEventType rmAppEventType, RMAppState state) {
                Assert.fail(((("RMAppImpl: can't handle " + rmAppEventType) + " at state ") + state));
            }
        };
        // NEW => KILLED event RMAppEventType.KILL
        UserGroupInformation fooUser = UserGroupInformation.createUserForTesting("testAppStartAfterKilled", new String[]{ "foo_group" });
        RMAppEvent event = new RMAppKillByClientEvent(applicationId, "Application killed by user.", fooUser, Server.getRemoteIp());
        application.handle(event);
        rmDispatcher.await();
        assertKilled(application);
        // KILLED => KILLED event RMAppEventType.START
        event = new RMAppFailedAttemptEvent(application.getApplicationId(), START, "", false);
        application.handle(event);
        rmDispatcher.await();
        assertTimesAtFinish(application);
        TestRMAppTransitions.assertAppState(KILLED, application);
    }

    @Test(timeout = 30000)
    public void testAppsRecoveringStates() throws Exception {
        RMState state = new RMState();
        Map<ApplicationId, ApplicationStateData> applicationState = state.getApplicationState();
        createRMStateForApplications(applicationState, FINISHED);
        createRMStateForApplications(applicationState, KILLED);
        createRMStateForApplications(applicationState, FAILED);
        for (ApplicationStateData appState : applicationState.values()) {
            testRecoverApplication(appState, state);
        }
    }

    @Test
    public void testGetAppReport() throws IOException {
        RMApp app = createNewTestApp(null);
        TestRMAppTransitions.assertAppState(NEW, app);
        ApplicationReport report = app.createAndGetApplicationReport(null, true);
        Assert.assertNotNull(report.getApplicationResourceUsageReport());
        Assert.assertEquals(report.getApplicationResourceUsageReport(), DUMMY_APPLICATION_RESOURCE_USAGE_REPORT);
        report = app.createAndGetApplicationReport("clientuser", true);
        Assert.assertNotNull(report.getApplicationResourceUsageReport());
        Assert.assertTrue("bad proxy url for app", report.getTrackingUrl().endsWith((("/proxy/" + (app.getApplicationId())) + "/")));
    }
}

