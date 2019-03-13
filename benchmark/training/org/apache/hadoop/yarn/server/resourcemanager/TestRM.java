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


import CapacitySchedulerConfiguration.NODE_LOCALITY_DELAY;
import ContainerState.COMPLETE;
import FinalApplicationStatus.SUCCEEDED;
import RMAppAttemptEventType.KILL;
import RMAppAttemptState.ALLOCATED;
import RMAppAttemptState.FAILED;
import RMAppAttemptState.FINISHED;
import RMAppAttemptState.FINISHING;
import RMAppAttemptState.LAUNCHED;
import RMAppAttemptState.SCHEDULED;
import RMAppState.ACCEPTED;
import RMAppState.KILLED;
import RMAppState.KILLING;
import RMAppState.RUNNING;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestRM extends ParameterizedSchedulerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestRM.class);

    // Milliseconds to sleep for when waiting for something to happen
    private static final int WAIT_SLEEP_MS = 100;

    private YarnConfiguration conf;

    public TestRM(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @Test
    public void testGetNewAppId() throws Exception {
        GenericTestUtils.setRootLogLevel(Level.DEBUG);
        MockRM rm = new MockRM(conf);
        start();
        GetNewApplicationResponse resp = rm.getNewAppId();
        assert (resp.getApplicationId().getId()) != 0;
        assert (resp.getMaximumResourceCapability().getMemorySize()) > 0;
        stop();
    }

    @Test(timeout = 30000)
    public void testAppWithNoContainers() throws Exception {
        GenericTestUtils.setRootLogLevel(Level.DEBUG);
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("h1:1234", 5120);
        RMApp app = rm.submitApp(2000);
        // kick the scheduling
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt = app.getCurrentAppAttempt();
        MockAM am = rm.sendAMLaunched(attempt.getAppAttemptId());
        am.registerAppAttempt();
        am.unregisterAppAttempt();
        nm1.nodeHeartbeat(attempt.getAppAttemptId(), 1, COMPLETE);
        rm.waitForState(am.getApplicationAttemptId(), FINISHED);
        stop();
    }

    @Test(timeout = 30000)
    public void testAppOnMultiNode() throws Exception {
        GenericTestUtils.setRootLogLevel(Level.DEBUG);
        conf.set(NODE_LOCALITY_DELAY, "-1");
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("h1:1234", 5120);
        MockNM nm2 = rm.registerNode("h2:5678", 10240);
        RMApp app = rm.submitApp(2000);
        // kick the scheduling
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt = app.getCurrentAppAttempt();
        MockAM am = rm.sendAMLaunched(attempt.getAppAttemptId());
        am.registerAppAttempt();
        // request for containers
        int request = 13;
        am.allocate("h1", 1000, request, new ArrayList<ContainerId>());
        // kick the scheduler
        List<Container> conts = am.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        int contReceived = conts.size();
        while (contReceived < 3) {
            // only 3 containers are available on node1
            nm1.nodeHeartbeat(true);
            conts.addAll(am.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            contReceived = conts.size();
            TestRM.LOG.info(((("Got " + contReceived) + " containers. Waiting to get ") + 3));
            Thread.sleep(TestRM.WAIT_SLEEP_MS);
        } 
        Assert.assertEquals(3, conts.size());
        // send node2 heartbeat
        conts = am.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        contReceived = conts.size();
        while (contReceived < 10) {
            nm2.nodeHeartbeat(true);
            conts.addAll(am.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            contReceived = conts.size();
            TestRM.LOG.info(((("Got " + contReceived) + " containers. Waiting to get ") + 10));
            Thread.sleep(TestRM.WAIT_SLEEP_MS);
        } 
        Assert.assertEquals(10, conts.size());
        am.unregisterAppAttempt();
        nm1.nodeHeartbeat(attempt.getAppAttemptId(), 1, COMPLETE);
        rm.waitForState(am.getApplicationAttemptId(), FINISHED);
        stop();
    }

    // Test even if AM container is allocated with containerId not equal to 1, the
    // following allocate requests from AM should be able to retrieve the
    // corresponding NM Token.
    @Test(timeout = 20000)
    public void testNMTokenSentForNormalContainer() throws Exception {
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm1 = rm.registerNode("h1:1234", 5120);
        RMApp app = rm.submitApp(2000);
        RMAppAttempt attempt = app.getCurrentAppAttempt();
        // Call getNewContainerId to increase container Id so that the AM container
        // Id doesn't equal to one.
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        scheduler.getApplicationAttempt(attempt.getAppAttemptId()).getNewContainerId();
        MockAM am = MockRM.launchAM(app, rm, nm1);
        // am container Id not equal to 1.
        Assert.assertTrue(((attempt.getMasterContainer().getId().getContainerId()) != 1));
        // NMSecretManager doesn't record the node on which the am is allocated.
        Assert.assertFalse(getRMContext().getNMTokenSecretManager().isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm1.getNodeId()));
        am.registerAppAttempt();
        rm.waitForState(app.getApplicationId(), RUNNING);
        int NUM_CONTAINERS = 1;
        List<Container> containers = new ArrayList<Container>();
        // nmTokens keeps track of all the nmTokens issued in the allocate call.
        List<NMToken> expectedNMTokens = new ArrayList<NMToken>();
        // am1 allocate 1 container on nm1.
        while (true) {
            AllocateResponse response = am.allocate("127.0.0.1", 2000, NUM_CONTAINERS, new ArrayList<ContainerId>());
            nm1.nodeHeartbeat(true);
            containers.addAll(response.getAllocatedContainers());
            expectedNMTokens.addAll(response.getNMTokens());
            if ((containers.size()) == NUM_CONTAINERS) {
                break;
            }
            Thread.sleep(200);
            System.out.println("Waiting for container to be allocated.");
        } 
        NodeId nodeId = expectedNMTokens.get(0).getNodeId();
        // NMToken is sent for the allocated container.
        Assert.assertEquals(nm1.getNodeId(), nodeId);
    }

    @Test(timeout = 40000)
    public void testNMToken() throws Exception {
        MockRM rm = new MockRM(conf);
        try {
            start();
            MockNM nm1 = rm.registerNode("h1:1234", 10000);
            NMTokenSecretManagerInRM nmTokenSecretManager = getRMContext().getNMTokenSecretManager();
            // submitting new application
            RMApp app = rm.submitApp(1000);
            // start scheduling.
            nm1.nodeHeartbeat(true);
            // Starting application attempt and launching
            // It should get registered with NMTokenSecretManager.
            RMAppAttempt attempt = app.getCurrentAppAttempt();
            MockAM am = rm.sendAMLaunched(attempt.getAppAttemptId());
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptRegistered(attempt.getAppAttemptId()));
            // This will register application master.
            am.registerAppAttempt();
            ArrayList<Container> containersReceivedForNM1 = new ArrayList<Container>();
            List<ContainerId> releaseContainerList = new ArrayList<ContainerId>();
            HashMap<String, Token> nmTokens = new HashMap<String, Token>();
            // initially requesting 2 containers.
            AllocateResponse response = am.allocate("h1", 1000, 2, releaseContainerList);
            Assert.assertEquals(0, response.getAllocatedContainers().size());
            allocateContainersAndValidateNMTokens(am, containersReceivedForNM1, 2, nmTokens, nm1);
            Assert.assertEquals(1, nmTokens.size());
            // requesting 2 more containers.
            response = am.allocate("h1", 1000, 2, releaseContainerList);
            Assert.assertEquals(0, response.getAllocatedContainers().size());
            allocateContainersAndValidateNMTokens(am, containersReceivedForNM1, 4, nmTokens, nm1);
            Assert.assertEquals(1, nmTokens.size());
            // We will be simulating NM restart so restarting newly added h2:1234
            // NM 2 now registers.
            MockNM nm2 = rm.registerNode("h2:1234", 10000);
            nm2.nodeHeartbeat(true);
            ArrayList<Container> containersReceivedForNM2 = new ArrayList<Container>();
            response = am.allocate("h2", 1000, 2, releaseContainerList);
            Assert.assertEquals(0, response.getAllocatedContainers().size());
            allocateContainersAndValidateNMTokens(am, containersReceivedForNM2, 2, nmTokens, nm2);
            Assert.assertEquals(2, nmTokens.size());
            // Simulating NM-2 restart.
            nm2 = rm.registerNode("h2:1234", 10000);
            // Wait for reconnect to make it through the RM and create a new RMNode
            Map<NodeId, RMNode> nodes = getRMContext().getRMNodes();
            while ((nodes.get(nm2.getNodeId()).getLastNodeHeartBeatResponse().getResponseId()) > 0) {
                Thread.sleep(TestRM.WAIT_SLEEP_MS);
            } 
            int interval = 40;
            // Wait for nm Token to be cleared.
            while ((nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm2.getNodeId())) && ((interval--) > 0)) {
                TestRM.LOG.info(("waiting for nmToken to be cleared for : " + (nm2.getNodeId())));
                Thread.sleep(TestRM.WAIT_SLEEP_MS);
            } 
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptRegistered(attempt.getAppAttemptId()));
            // removing NMToken for h2:1234
            nmTokens.remove(nm2.getNodeId().toString());
            Assert.assertEquals(1, nmTokens.size());
            // We should again receive the NMToken.
            response = am.allocate("h2", 1000, 2, releaseContainerList);
            Assert.assertEquals(0, response.getAllocatedContainers().size());
            allocateContainersAndValidateNMTokens(am, containersReceivedForNM2, 4, nmTokens, nm2);
            Assert.assertEquals(2, nmTokens.size());
            // Now rolling over NMToken masterKey. it should resend the NMToken in
            // next allocate call.
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm1.getNodeId()));
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm2.getNodeId()));
            nmTokenSecretManager.rollMasterKey();
            nmTokenSecretManager.activateNextMasterKey();
            Assert.assertFalse(nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm1.getNodeId()));
            Assert.assertFalse(nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm2.getNodeId()));
            // It should not remove application attempt entry.
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptRegistered(attempt.getAppAttemptId()));
            nmTokens.clear();
            Assert.assertEquals(0, nmTokens.size());
            // We should again receive the NMToken.
            response = am.allocate("h2", 1000, 1, releaseContainerList);
            Assert.assertEquals(0, response.getAllocatedContainers().size());
            allocateContainersAndValidateNMTokens(am, containersReceivedForNM2, 5, nmTokens, nm2);
            Assert.assertEquals(1, nmTokens.size());
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptNMTokenPresent(attempt.getAppAttemptId(), nm2.getNodeId()));
            // After AM is finished making sure that nmtoken entry for app
            Assert.assertTrue(nmTokenSecretManager.isApplicationAttemptRegistered(attempt.getAppAttemptId()));
            am.unregisterAppAttempt();
            // marking all the containers as finished.
            for (Container container : containersReceivedForNM1) {
                nm1.nodeHeartbeat(attempt.getAppAttemptId(), container.getId().getContainerId(), COMPLETE);
            }
            for (Container container : containersReceivedForNM2) {
                nm2.nodeHeartbeat(attempt.getAppAttemptId(), container.getId().getContainerId(), COMPLETE);
            }
            nm1.nodeHeartbeat(am.getApplicationAttemptId(), 1, COMPLETE);
            rm.waitForState(am.getApplicationAttemptId(), FINISHED);
            Assert.assertFalse(nmTokenSecretManager.isApplicationAttemptRegistered(attempt.getAppAttemptId()));
        } finally {
            stop();
        }
    }

    @Test(timeout = 300000)
    public void testActivatingApplicationAfterAddingNM() throws Exception {
        MockRM rm1 = new MockRM(conf);
        // start like normal because state is empty
        start();
        // app that gets launched
        RMApp app1 = rm1.submitApp(200);
        // app that does not get launched
        RMApp app2 = rm1.submitApp(200);
        // app1 and app2 should be scheduled, but because no resource is available,
        // they are not activated.
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        ApplicationAttemptId attemptId1 = attempt1.getAppAttemptId();
        rm1.waitForState(attemptId1, SCHEDULED);
        RMAppAttempt attempt2 = app2.getCurrentAppAttempt();
        ApplicationAttemptId attemptId2 = attempt2.getAppAttemptId();
        rm1.waitForState(attemptId2, SCHEDULED);
        MockNM nm1 = new MockNM("h1:1234", 15120, getResourceTrackerService());
        MockNM nm2 = new MockNM("h2:5678", 15120, getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        // kick the scheduling
        nm1.nodeHeartbeat(true);
        // app1 should be allocated now
        rm1.waitForState(attemptId1, ALLOCATED);
        rm1.waitForState(attemptId2, SCHEDULED);
        nm2.nodeHeartbeat(true);
        // app2 should be allocated now
        rm1.waitForState(attemptId1, ALLOCATED);
        rm1.waitForState(attemptId2, ALLOCATED);
        stop();
    }

    // This is to test AM Host and rpc port are invalidated after the am attempt
    // is killed or failed, so that client doesn't get the wrong information.
    @Test(timeout = 80000)
    public void testInvalidateAMHostPortWhenAMFailedOrKilled() throws Exception {
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        MockRM rm1 = new MockRM(conf);
        start();
        // a succeeded app
        RMApp app1 = rm1.submitApp(200);
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        MockRM.finishAMAndVerifyAppState(app1, rm1, nm1, am1);
        // a failed app
        RMApp app2 = rm1.submitApp(200);
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm1);
        nm1.nodeHeartbeat(am2.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(am2.getApplicationAttemptId(), FAILED);
        rm1.waitForState(app2.getApplicationId(), RMAppState.FAILED);
        // a killed app
        RMApp app3 = rm1.submitApp(200);
        MockAM am3 = MockRM.launchAndRegisterAM(app3, rm1, nm1);
        rm1.killApp(app3.getApplicationId());
        rm1.waitForState(app3.getApplicationId(), KILLED);
        rm1.waitForState(am3.getApplicationAttemptId(), RMAppAttemptState.KILLED);
        GetApplicationsRequest request1 = GetApplicationsRequest.newInstance(EnumSet.of(YarnApplicationState.FINISHED, YarnApplicationState.KILLED, YarnApplicationState.FAILED));
        GetApplicationsResponse response1 = getClientRMService().getApplications(request1);
        List<ApplicationReport> appList1 = response1.getApplicationList();
        Assert.assertEquals(3, appList1.size());
        for (ApplicationReport report : appList1) {
            // killed/failed apps host and rpc port are invalidated.
            if ((report.getApplicationId().equals(app2.getApplicationId())) || (report.getApplicationId().equals(app3.getApplicationId()))) {
                Assert.assertEquals("N/A", report.getHost());
                Assert.assertEquals((-1), report.getRpcPort());
            }
            // succeeded app's host and rpc port is not invalidated
            if (report.getApplicationId().equals(app1.getApplicationId())) {
                Assert.assertFalse(report.getHost().equals("N/A"));
                Assert.assertTrue(((report.getRpcPort()) != (-1)));
            }
        }
    }

    @Test(timeout = 60000)
    public void testInvalidatedAMHostPortOnAMRestart() throws Exception {
        MockRM rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // a failed app
        RMApp app2 = rm1.submitApp(200);
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm1);
        nm1.nodeHeartbeat(am2.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(am2.getApplicationAttemptId(), FAILED);
        rm1.waitForState(app2.getApplicationId(), ACCEPTED);
        // before new attempt is launched, the app report returns the invalid AM
        // host and port.
        GetApplicationReportRequest request1 = GetApplicationReportRequest.newInstance(app2.getApplicationId());
        ApplicationReport report1 = getClientRMService().getApplicationReport(request1).getApplicationReport();
        Assert.assertEquals("N/A", report1.getHost());
        Assert.assertEquals((-1), report1.getRpcPort());
    }

    /**
     * Validate killing an application when it is at accepted state.
     *
     * @throws Exception
     * 		exception
     */
    @Test(timeout = 60000)
    public void testApplicationKillAtAcceptedState() throws Exception {
        final Dispatcher dispatcher = new DrainDispatcher() {
            @Override
            public EventHandler<Event> getEventHandler() {
                class EventArgMatcher implements ArgumentMatcher<AbstractEvent> {
                    @Override
                    public boolean matches(AbstractEvent argument) {
                        if (argument instanceof RMAppAttemptEvent) {
                            if (getType().equals(KILL)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                EventHandler handler = Mockito.spy(super.getEventHandler());
                Mockito.doNothing().when(handler).handle(ArgumentMatchers.argThat(new EventArgMatcher()));
                return handler;
            }
        };
        MockRM rm = new MockRM(conf) {
            @Override
            protected Dispatcher createDispatcher() {
                return dispatcher;
            }
        };
        // test metrics
        QueueMetrics metrics = getResourceScheduler().getRootQueueMetrics();
        int appsKilled = metrics.getAppsKilled();
        int appsSubmitted = metrics.getAppsSubmitted();
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // a failed app
        RMApp application = rm.submitApp(200);
        MockAM am = MockRM.launchAM(application, rm, nm1);
        rm.waitForState(am.getApplicationAttemptId(), LAUNCHED);
        nm1.nodeHeartbeat(am.getApplicationAttemptId(), 1, ContainerState.RUNNING);
        rm.waitForState(application.getApplicationId(), ACCEPTED);
        // Now kill the application before new attempt is launched, the app report
        // returns the invalid AM host and port.
        KillApplicationRequest request = KillApplicationRequest.newInstance(application.getApplicationId());
        getClientRMService().forceKillApplication(request);
        // Specific test for YARN-1689 follows
        // Now let's say a race causes AM to register now. This should not crash RM.
        am.registerAppAttempt(false);
        // We explicitly intercepted the kill-event to RMAppAttempt, so app should
        // still be in KILLING state.
        rm.waitForState(application.getApplicationId(), KILLING);
        // AM should now be in running
        rm.waitForState(am.getApplicationAttemptId(), RMAppAttemptState.RUNNING);
        // Simulate that appAttempt is killed.
        getRMContext().getDispatcher().getEventHandler().handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(application.getApplicationId(), RMAppEventType.ATTEMPT_KILLED));
        rm.waitForState(application.getApplicationId(), KILLED);
        // test metrics
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return ((appsKilled + 1) == (metrics.getAppsKilled())) && ((appsSubmitted + 1) == (metrics.getAppsSubmitted()));
            }
        }, 100, 10000);
        Assert.assertEquals((appsKilled + 1), metrics.getAppsKilled());
        Assert.assertEquals((appsSubmitted + 1), metrics.getAppsSubmitted());
    }

    // Test Kill an app while the app is finishing in the meanwhile.
    @Test(timeout = 30000)
    public void testKillFinishingApp() throws Exception {
        // this dispatcher ignores RMAppAttemptEventType.KILL event
        final Dispatcher dispatcher = new DrainDispatcher() {
            @Override
            public EventHandler<Event> getEventHandler() {
                class EventArgMatcher implements ArgumentMatcher<AbstractEvent> {
                    @Override
                    public boolean matches(AbstractEvent argument) {
                        if (argument instanceof RMAppAttemptEvent) {
                            if (getType().equals(KILL)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                EventHandler handler = Mockito.spy(super.getEventHandler());
                Mockito.doNothing().when(handler).handle(ArgumentMatchers.argThat(new EventArgMatcher()));
                return handler;
            }
        };
        MockRM rm1 = new MockRM(conf) {
            @Override
            protected Dispatcher createDispatcher() {
                return dispatcher;
            }
        };
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        rm1.killApp(app1.getApplicationId());
        FinishApplicationMasterRequest req = FinishApplicationMasterRequest.newInstance(SUCCEEDED, "", "");
        am1.unregisterAppAttempt(req, true);
        rm1.waitForState(am1.getApplicationAttemptId(), FINISHING);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(am1.getApplicationAttemptId(), FINISHED);
        rm1.waitForState(app1.getApplicationId(), RMAppState.FINISHED);
    }

    // Test Kill an app while the app is failing
    @Test(timeout = 30000)
    public void testKillFailingApp() throws Exception {
        // this dispatcher ignores RMAppAttemptEventType.KILL event
        final Dispatcher dispatcher = new DrainDispatcher() {
            @Override
            public EventHandler<Event> getEventHandler() {
                class EventArgMatcher implements ArgumentMatcher<AbstractEvent> {
                    @Override
                    public boolean matches(AbstractEvent argument) {
                        if (argument instanceof RMAppAttemptEvent) {
                            if (getType().equals(KILL)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                EventHandler handler = Mockito.spy(super.getEventHandler());
                Mockito.doNothing().when(handler).handle(ArgumentMatchers.argThat(new EventArgMatcher()));
                return handler;
            }
        };
        MockRM rm1 = new MockRM(conf) {
            @Override
            protected Dispatcher createDispatcher() {
                return dispatcher;
            }
        };
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        rm1.killApp(app1.getApplicationId());
        // fail the app by sending container_finished event.
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(am1.getApplicationAttemptId(), FAILED);
        // app is killed, not launching a new attempt
        rm1.waitForState(app1.getApplicationId(), KILLED);
    }
}

