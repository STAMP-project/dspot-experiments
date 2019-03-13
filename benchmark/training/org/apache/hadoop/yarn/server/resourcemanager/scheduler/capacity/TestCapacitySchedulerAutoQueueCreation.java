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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import QueueState.STOPPED;
import RMAppAttemptState.KILLED;
import RMAppState.FAILED;
import ResourceRequest.ANY;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.placement.ApplicationPlacementContext;
import org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.queuemanagement.GuaranteedOrZeroCapacityOverTimePolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy.FairOrderingPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for creation and reinitialization of auto created leaf queues
 * and capacity management under a ManagedParentQueue.
 */
public class TestCapacitySchedulerAutoQueueCreation extends TestCapacitySchedulerAutoCreatedQueueBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestCapacitySchedulerAutoQueueCreation.class);

    private static final Resource TEMPLATE_MAX_RES = Resource.newInstance((16 * (TestCapacitySchedulerAutoCreatedQueueBase.GB)), 48);

    private static final Resource TEMPLATE_MIN_RES = Resource.newInstance(1638, 4);

    @Test(timeout = 20000)
    public void testAutoCreateLeafQueueCreation() throws Exception {
        try {
            // submit an app
            submitApp(mockRM, cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE), TestCapacitySchedulerAutoCreatedQueueBase.USER0, TestCapacitySchedulerAutoCreatedQueueBase.USER0, 1, 1);
            // check preconditions
            List<ApplicationAttemptId> appsInC = cs.getAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            Assert.assertEquals(1, appsInC.size());
            Assert.assertNotNull(cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0));
            AutoCreatedLeafQueue autoCreatedLeafQueue = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0)));
            ManagedParentQueue parentQueue = ((ManagedParentQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE)));
            Assert.assertEquals(parentQueue, autoCreatedLeafQueue.getParent());
            Map<String, Float> expectedChildQueueAbsCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER0, expectedChildQueueAbsCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            validateUserAndAppLimits(autoCreatedLeafQueue, 1000, 1000);
            Assert.assertTrue(((autoCreatedLeafQueue.getOrderingPolicy()) instanceof FairOrderingPolicy));
            TestCapacitySchedulerAutoCreatedQueueBase.setupGroupQueueMappings("d", cs.getConfiguration(), "%user");
            cs.reinitialize(cs.getConfiguration(), getRMContext());
            submitApp(mockRM, cs.getQueue("d"), TestCapacitySchedulerAutoCreatedQueueBase.TEST_GROUPUSER, TestCapacitySchedulerAutoCreatedQueueBase.TEST_GROUPUSER, 1, 1);
            autoCreatedLeafQueue = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.TEST_GROUPUSER)));
            parentQueue = ((ManagedParentQueue) (cs.getQueue("d")));
            Assert.assertEquals(parentQueue, autoCreatedLeafQueue.getParent());
            expectedChildQueueAbsCapacity = new HashMap<String, Float>() {
                {
                    put(NO_LABEL, 0.02F);
                }
            };
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.TEST_GROUPUSER, expectedChildQueueAbsCapacity, new HashSet<String>() {
                {
                    add(NO_LABEL);
                }
            });
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.TEST_GROUPUSER);
        }
    }

    @Test
    public void testReinitializeStoppedAutoCreatedLeafQueue() throws Exception {
        try {
            String host = "127.0.0.1";
            RMNode node = MockNodes.newNodeInfo(0, MockNodes.newResource((4 * (TestCapacitySchedulerAutoCreatedQueueBase.GB))), 1, host);
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node));
            // submit an app
            RMApp app1 = mockRM.submitApp(TestCapacitySchedulerAutoCreatedQueueBase.GB, "test-auto-queue-creation-1", TestCapacitySchedulerAutoCreatedQueueBase.USER0, null, TestCapacitySchedulerAutoCreatedQueueBase.USER0);
            RMApp app2 = mockRM.submitApp(TestCapacitySchedulerAutoCreatedQueueBase.GB, "test-auto-queue-creation-2", TestCapacitySchedulerAutoCreatedQueueBase.USER1, null, TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            // check preconditions
            List<ApplicationAttemptId> appsInC = cs.getAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            Assert.assertEquals(2, appsInC.size());
            Assert.assertNotNull(cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0));
            Assert.assertNotNull(cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1));
            AutoCreatedLeafQueue user0Queue = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0)));
            AutoCreatedLeafQueue user1Queue = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0)));
            ManagedParentQueue parentQueue = ((ManagedParentQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE)));
            Assert.assertEquals(parentQueue, user0Queue.getParent());
            Assert.assertEquals(parentQueue, user1Queue.getParent());
            Map<String, Float> expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(2);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER0, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            ApplicationAttemptId appAttemptId = appsInC.get(0);
            Priority priority = TestUtils.createMockPriority(1);
            RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
            ResourceRequest r1 = TestUtils.createResourceRequest(ANY, (1 * (TestCapacitySchedulerAutoCreatedQueueBase.GB)), 1, true, priority, recordFactory);
            cs.allocate(appAttemptId, Collections.<ResourceRequest>singletonList(r1), null, Collections.<ContainerId>emptyList(), Collections.singletonList(host), null, TestCapacitySchedulerAutoCreatedQueueBase.NULL_UPDATE_REQUESTS);
            // And this will result in container assignment for app1
            CapacityScheduler.schedule(cs);
            // change state to draining
            user0Queue.stopQueue();
            cs.killAllAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0);
            mockRM.waitForState(appAttemptId, KILLED);
            mockRM.waitForState(appAttemptId.getApplicationId(), RMAppState.KILLED);
            // change state to stopped
            user0Queue.stopQueue();
            Assert.assertEquals(STOPPED, user0Queue.getQueueInfo().getQueueState());
            cs.reinitialize(cs.getConf(), getRMContext());
            AutoCreatedLeafQueue user0QueueReinited = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0)));
            validateCapacities(user0QueueReinited, 0.0F, 0.0F, 1.0F, 1.0F);
            AutoCreatedLeafQueue leafQueue = ((AutoCreatedLeafQueue) (cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1)));
            expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(parentQueue, leafQueue.getQueueName(), expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0);
        }
    }

    @Test
    public void testConvertAutoCreateDisabledOnManagedParentQueueFails() throws Exception {
        CapacityScheduler newCS = new CapacityScheduler();
        try {
            CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration();
            TestCapacitySchedulerAutoCreatedQueueBase.setupQueueConfiguration(newConf);
            newConf.setAutoCreateChildQueueEnabled(TestCapacitySchedulerAutoCreatedQueueBase.C, false);
            newCS.setConf(new YarnConfiguration());
            newCS.setRMContext(getRMContext());
            newCS.init(cs.getConf());
            newCS.start();
            newCS.reinitialize(newConf, new org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(newConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(newConf), new ClientToAMTokenSecretManagerInRM(), null));
        } catch (IOException e) {
            // expected exception
        } finally {
            newCS.stop();
        }
    }

    @Test
    public void testConvertLeafQueueToParentQueueWithAutoCreate() throws Exception {
        CapacityScheduler newCS = new CapacityScheduler();
        try {
            CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration();
            TestCapacitySchedulerAutoCreatedQueueBase.setupQueueConfiguration(newConf);
            newConf.setAutoCreatedLeafQueueConfigCapacity(TestCapacitySchedulerAutoCreatedQueueBase.A1, ((TestCapacitySchedulerAutoCreatedQueueBase.A1_CAPACITY) / 10));
            newConf.setAutoCreateChildQueueEnabled(TestCapacitySchedulerAutoCreatedQueueBase.A1, true);
            newCS.setConf(new YarnConfiguration());
            newCS.setRMContext(getRMContext());
            newCS.init(cs.getConf());
            newCS.start();
            final LeafQueue a1Queue = ((LeafQueue) (newCS.getQueue("a1")));
            a1Queue.stopQueue();
            newCS.reinitialize(newConf, new org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(newConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(newConf), new ClientToAMTokenSecretManagerInRM(), null));
        } finally {
            newCS.stop();
        }
    }

    @Test
    public void testConvertFailsFromParentQueueToManagedParentQueue() throws Exception {
        CapacityScheduler newCS = new CapacityScheduler();
        try {
            CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration();
            TestCapacitySchedulerAutoCreatedQueueBase.setupQueueConfiguration(newConf);
            newConf.setAutoCreatedLeafQueueConfigCapacity(TestCapacitySchedulerAutoCreatedQueueBase.A, ((TestCapacitySchedulerAutoCreatedQueueBase.A_CAPACITY) / 10));
            newConf.setAutoCreateChildQueueEnabled(TestCapacitySchedulerAutoCreatedQueueBase.A, true);
            newCS.setConf(new YarnConfiguration());
            newCS.setRMContext(getRMContext());
            newCS.init(cs.getConf());
            newCS.start();
            final ParentQueue a1Queue = ((ParentQueue) (newCS.getQueue("a")));
            a1Queue.stopQueue();
            newCS.reinitialize(newConf, new org.apache.hadoop.yarn.server.resourcemanager.RMContextImpl(null, null, null, null, null, null, new org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager(newConf), new org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM(newConf), new ClientToAMTokenSecretManagerInRM(), null));
            Assert.fail(("Expected exception while converting a parent queue to" + " an auto create enabled parent queue"));
        } catch (IOException e) {
            // expected exception
        } finally {
            newCS.stop();
        }
    }

    @Test(timeout = 10000)
    public void testAutoCreateLeafQueueFailsWithNoQueueMapping() throws Exception {
        final String INVALID_USER = "invalid_user";
        // submit an app under a different queue name which does not exist
        // and queue mapping does not exist for this user
        RMApp app = mockRM.submitApp(TestCapacitySchedulerAutoCreatedQueueBase.GB, "app", INVALID_USER, null, INVALID_USER, false);
        mockRM.drainEvents();
        mockRM.waitForState(app.getApplicationId(), FAILED);
        Assert.assertEquals(FAILED, app.getState());
    }

    @Test(timeout = 10000)
    public void testQueueMappingValidationFailsWithInvalidParentQueueInMapping() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        try {
            CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
            // "a" is not auto create enabled
            // dynamic queue mapping
            try {
                setupQueueMapping(newCS, UserGroupMappingPlacementRule.CURRENT_USER_MAPPING, "a", UserGroupMappingPlacementRule.CURRENT_USER_MAPPING);
                newCS.updatePlacementRules();
                Assert.fail("Expected invalid parent queue mapping failure");
            } catch (IOException e) {
                // expected exception
                Assert.assertTrue(e.getMessage().contains(("invalid parent queue which does not have auto creation of leaf " + (("queues enabled [" + "a") + "]"))));
            }
            // "a" is not auto create enabled and app_user does not exist as a leaf
            // queue
            // static queue mapping
            try {
                setupQueueMapping(newCS, "app_user", "INVALID_PARENT_QUEUE", "app_user");
                newCS.updatePlacementRules();
                Assert.fail("Expected invalid parent queue mapping failure");
            } catch (IOException e) {
                // expected exception
                Assert.assertTrue(e.getMessage().contains(("invalid parent queue [" + ("INVALID_PARENT_QUEUE" + "]"))));
            }
        } finally {
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }

    @Test(timeout = 10000)
    public void testQueueMappingUpdatesFailsOnRemovalOfParentQueueInMapping() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        try {
            CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
            setupQueueMapping(newCS, UserGroupMappingPlacementRule.CURRENT_USER_MAPPING, "c", UserGroupMappingPlacementRule.CURRENT_USER_MAPPING);
            newCS.updatePlacementRules();
            try {
                setupQueueMapping(newCS, UserGroupMappingPlacementRule.CURRENT_USER_MAPPING, "", UserGroupMappingPlacementRule.CURRENT_USER_MAPPING);
                newCS.updatePlacementRules();
                Assert.fail("Expected invalid parent queue mapping failure");
            } catch (IOException e) {
                // expected exception
                Assert.assertTrue(e.getMessage().contains("invalid parent queue []"));
            }
        } finally {
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }

    @Test
    public void testParentQueueUpdateInQueueMappingFailsAfterAutoCreation() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
        try {
            submitApp(newCS, TestCapacitySchedulerAutoCreatedQueueBase.USER0, TestCapacitySchedulerAutoCreatedQueueBase.USER0, TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            Assert.assertNotNull(newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER0));
            setupQueueMapping(newCS, TestCapacitySchedulerAutoCreatedQueueBase.USER0, "d", TestCapacitySchedulerAutoCreatedQueueBase.USER0);
            newCS.updatePlacementRules();
            RMContext rmContext = Mockito.mock(RMContext.class);
            Mockito.when(rmContext.getDispatcher()).thenReturn(dispatcher);
            newCS.setRMContext(rmContext);
            ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
            SchedulerEvent addAppEvent = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent(appId, TestCapacitySchedulerAutoCreatedQueueBase.USER0, TestCapacitySchedulerAutoCreatedQueueBase.USER0, new ApplicationPlacementContext(TestCapacitySchedulerAutoCreatedQueueBase.USER0, "d"));
            newCS.handle(addAppEvent);
            RMAppEvent event = new RMAppEvent(appId, RMAppEventType.APP_REJECTED, "error");
            dispatcher.spyOnNextEvent(event, 10000);
        } finally {
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }

    @Test
    public void testAutoCreationFailsWhenParentCapacityExceeded() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
        try {
            CapacitySchedulerConfiguration conf = newCS.getConfiguration();
            conf.setShouldFailAutoQueueCreationWhenGuaranteedCapacityExceeded(TestCapacitySchedulerAutoCreatedQueueBase.C, true);
            newCS.reinitialize(conf, getRMContext());
            // Test add one auto created queue dynamically and manually modify
            // capacity
            ManagedParentQueue parentQueue = ((ManagedParentQueue) (newCS.getQueue("c")));
            AutoCreatedLeafQueue c1 = new AutoCreatedLeafQueue(newCS, "c1", parentQueue);
            newCS.addQueue(c1);
            c1.setCapacity(0.5F);
            c1.setAbsoluteCapacity(((c1.getParent().getAbsoluteCapacity()) * 1.0F));
            c1.setMaxCapacity(1.0F);
            setEntitlement(c1, new QueueEntitlement(0.5F, 1.0F));
            AutoCreatedLeafQueue c2 = new AutoCreatedLeafQueue(newCS, "c2", parentQueue);
            newCS.addQueue(c2);
            setEntitlement(c2, new QueueEntitlement(0.5F, 1.0F));
            try {
                AutoCreatedLeafQueue c3 = new AutoCreatedLeafQueue(newCS, "c3", parentQueue);
                newCS.addQueue(c3);
                Assert.fail("Expected exception for auto queue creation failure");
            } catch (SchedulerDynamicEditException e) {
                // expected exception
            }
        } finally {
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }

    @Test
    public void testAutoCreatedQueueActivationDeactivation() throws Exception {
        try {
            CSQueue parentQueue = cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            // submit app1 as USER1
            ApplicationId user1AppId = submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1, 1, 1);
            Map<String, Float> expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // submit another app2 as USER2
            ApplicationId user2AppId = submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, TestCapacitySchedulerAutoCreatedQueueBase.USER2, 2, 1);
            expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(2);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // submit another app3 as USER1
            submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1, 3, 2);
            // validate total activated abs capacity remains the same
            GuaranteedOrZeroCapacityOverTimePolicy autoCreatedQueueManagementPolicy = ((GuaranteedOrZeroCapacityOverTimePolicy) (getAutoCreatedQueueManagementPolicy()));
            for (String nodeLabel : TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC) {
                Assert.assertEquals(expectedAbsChildQueueCapacity.get(nodeLabel), autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(nodeLabel), CSQueueUtils.EPSILON);
            }
            // submit user_3 app. This cant be allocated since there is no capacity
            // in NO_LABEL, SSD but can be in GPU label
            submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER3, 4, 1);
            final CSQueue user3LeafQueue = cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
            validateCapacities(((AutoCreatedLeafQueue) (user3LeafQueue)), 0.0F, 0.0F, 1.0F, 1.0F);
            validateCapacitiesByLabel(((ManagedParentQueue) (parentQueue)), ((AutoCreatedLeafQueue) (user3LeafQueue)), TestCapacitySchedulerAutoCreatedQueueBase.NODEL_LABEL_GPU);
            Assert.assertEquals(0.2F, autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), CSQueueUtils.EPSILON);
            Assert.assertEquals(0.9F, autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(TestCapacitySchedulerAutoCreatedQueueBase.NODEL_LABEL_GPU), CSQueueUtils.EPSILON);
            // Verify that AMs can be allocated
            // Node 1 has SSD and default node label expression on C is SSD.
            // This validates that the default node label expression with SSD is set
            // on the AM attempt
            // and app attempt reaches ALLOCATED state for a dynamic queue 'USER1'
            mockRM.launchAM(getRMContext().getRMApps().get(user1AppId), mockRM, nm1);
            // //deactivate USER2 queue
            cs.killAllAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            mockRM.waitForState(user2AppId, RMAppState.KILLED);
            // Verify if USER_2 can be deactivated since it has no pending apps
            List<QueueManagementChange> queueManagementChanges = autoCreatedQueueManagementPolicy.computeQueueManagementChanges();
            ManagedParentQueue managedParentQueue = ((ManagedParentQueue) (parentQueue));
            managedParentQueue.validateAndApplyQueueManagementChanges(queueManagementChanges);
            validateDeactivatedQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, expectedAbsChildQueueCapacity, queueManagementChanges);
            // USER_3 should now get activated for SSD, NO_LABEL
            Set<String> expectedNodeLabelsUpdated = new HashSet<>();
            expectedNodeLabelsUpdated.add(NO_LABEL);
            expectedNodeLabelsUpdated.add(TestCapacitySchedulerAutoCreatedQueueBase.NODEL_LABEL_SSD);
            validateActivatedQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, expectedAbsChildQueueCapacity, queueManagementChanges, expectedNodeLabelsUpdated);
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
        }
    }

    @Test
    public void testClusterResourceUpdationOnAutoCreatedLeafQueues() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        try {
            CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
            CSQueue parentQueue = newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            // submit app1 as USER1
            submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1, 1, 1);
            Map<String, Float> expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(newCS, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // submit another app2 as USER2
            ApplicationId user2AppId = submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, TestCapacitySchedulerAutoCreatedQueueBase.USER2, 2, 1);
            expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(2);
            validateInitialQueueEntitlement(newCS, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // validate total activated abs capacity remains the same
            GuaranteedOrZeroCapacityOverTimePolicy autoCreatedQueueManagementPolicy = ((GuaranteedOrZeroCapacityOverTimePolicy) (getAutoCreatedQueueManagementPolicy()));
            Assert.assertEquals(autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), 0.2F, CSQueueUtils.EPSILON);
            // submit user_3 app. This cant be scheduled since there is no capacity
            submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER3, 3, 1);
            final CSQueue user3LeafQueue = newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
            validateCapacities(((AutoCreatedLeafQueue) (user3LeafQueue)), 0.0F, 0.0F, 1.0F, 1.0F);
            Assert.assertEquals(autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), 0.2F, CSQueueUtils.EPSILON);
            // add new NM.
            newMockRM.registerNode("127.0.0.3:1234", (125 * (TestCapacitySchedulerAutoCreatedQueueBase.GB)), 20);
            // There will be change in effective resource when nodes are added
            // since we deal with percentages
            Resource MAX_RES = Resources.addTo(TestCapacitySchedulerAutoQueueCreation.TEMPLATE_MAX_RES, Resources.createResource((125 * (TestCapacitySchedulerAutoCreatedQueueBase.GB)), 20));
            Resource MIN_RES = Resources.createResource(14438, 6);
            Assert.assertEquals("Effective Min resource for USER3 is not correct", Resources.none(), user3LeafQueue.getQueueResourceQuotas().getEffectiveMinResource());
            Assert.assertEquals("Effective Max resource for USER3 is not correct", MAX_RES, user3LeafQueue.getQueueResourceQuotas().getEffectiveMaxResource());
            CSQueue user1LeafQueue = newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            CSQueue user2LeafQueue = newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            Assert.assertEquals("Effective Min resource for USER2 is not correct", MIN_RES, user1LeafQueue.getQueueResourceQuotas().getEffectiveMinResource());
            Assert.assertEquals("Effective Max resource for USER2 is not correct", MAX_RES, user1LeafQueue.getQueueResourceQuotas().getEffectiveMaxResource());
            Assert.assertEquals("Effective Min resource for USER1 is not correct", MIN_RES, user2LeafQueue.getQueueResourceQuotas().getEffectiveMinResource());
            Assert.assertEquals("Effective Max resource for USER1 is not correct", MAX_RES, user2LeafQueue.getQueueResourceQuotas().getEffectiveMaxResource());
            // unregister one NM.
            newMockRM.unRegisterNode(nm3);
            Resource MIN_RES_UPDATED = Resources.createResource(12800, 2);
            Resource MAX_RES_UPDATED = Resources.createResource(128000, 20);
            // After loosing one NM, resources will reduce
            Assert.assertEquals("Effective Min resource for USER2 is not correct", MIN_RES_UPDATED, user1LeafQueue.getQueueResourceQuotas().getEffectiveMinResource());
            Assert.assertEquals("Effective Max resource for USER2 is not correct", MAX_RES_UPDATED, user2LeafQueue.getQueueResourceQuotas().getEffectiveMaxResource());
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }

    @Test
    public void testReinitializeQueuesWithAutoCreatedLeafQueues() throws Exception {
        MockRM newMockRM = setupSchedulerInstance();
        try {
            CapacityScheduler newCS = ((CapacityScheduler) (getResourceScheduler()));
            CapacitySchedulerConfiguration conf = newCS.getConfiguration();
            CSQueue parentQueue = newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            // submit app1 as USER1
            submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1, 1, 1);
            Map<String, Float> expectedChildQueueAbsCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(newCS, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, expectedChildQueueAbsCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // submit another app2 as USER2
            ApplicationId user2AppId = submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, TestCapacitySchedulerAutoCreatedQueueBase.USER2, 2, 1);
            expectedChildQueueAbsCapacity = populateExpectedAbsCapacityByLabelForParentQueue(2);
            validateInitialQueueEntitlement(newCS, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, expectedChildQueueAbsCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // update parent queue capacity
            conf.setCapacity(TestCapacitySchedulerAutoCreatedQueueBase.C, 30.0F);
            conf.setCapacity(TestCapacitySchedulerAutoCreatedQueueBase.D, 10.0F);
            conf.setMaximumCapacity(TestCapacitySchedulerAutoCreatedQueueBase.C, 50.0F);
            newCS.reinitialize(conf, getRMContext());
            // validate that leaf queues abs capacity is now changed
            AutoCreatedLeafQueue user0Queue = ((AutoCreatedLeafQueue) (newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1)));
            validateCapacities(user0Queue, 0.5F, 0.15F, 1.0F, 0.5F);
            validateUserAndAppLimits(user0Queue, 1500, 1500);
            // update leaf queue template capacities
            conf.setAutoCreatedLeafQueueConfigCapacity(TestCapacitySchedulerAutoCreatedQueueBase.C, 30.0F);
            conf.setAutoCreatedLeafQueueConfigMaxCapacity(TestCapacitySchedulerAutoCreatedQueueBase.C, 40.0F);
            newCS.reinitialize(conf, getRMContext());
            validateCapacities(user0Queue, 0.3F, 0.09F, 0.4F, 0.2F);
            validateUserAndAppLimits(user0Queue, 900, 900);
            // submit app1 as USER3
            submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER3, 3, 1);
            AutoCreatedLeafQueue user3Queue = ((AutoCreatedLeafQueue) (newCS.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1)));
            validateCapacities(user3Queue, 0.3F, 0.09F, 0.4F, 0.2F);
            validateUserAndAppLimits(user3Queue, 900, 900);
            // submit app1 as USER1 - is already activated. there should be no diff
            // in capacities
            submitApp(newMockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER3, 4, 2);
            validateCapacities(user3Queue, 0.3F, 0.09F, 0.4F, 0.2F);
            validateUserAndAppLimits(user3Queue, 900, 900);
            GuaranteedOrZeroCapacityOverTimePolicy autoCreatedQueueManagementPolicy = ((GuaranteedOrZeroCapacityOverTimePolicy) (getAutoCreatedQueueManagementPolicy()));
            Assert.assertEquals(0.27F, autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), CSQueueUtils.EPSILON);
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            if (newMockRM != null) {
                stop();
                stop();
            }
        }
    }
}

