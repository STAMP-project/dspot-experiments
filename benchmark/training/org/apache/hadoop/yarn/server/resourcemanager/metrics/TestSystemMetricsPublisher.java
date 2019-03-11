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
package org.apache.hadoop.yarn.server.resourcemanager.metrics;


import AppAttemptMetricsConstants.DIAGNOSTICS_INFO;
import AppAttemptMetricsConstants.FINAL_STATUS_INFO;
import AppAttemptMetricsConstants.HOST_INFO;
import AppAttemptMetricsConstants.MASTER_CONTAINER_INFO;
import AppAttemptMetricsConstants.ORIGINAL_TRACKING_URL_INFO;
import AppAttemptMetricsConstants.PARENT_PRIMARY_FILTER;
import AppAttemptMetricsConstants.REGISTERED_EVENT_TYPE;
import AppAttemptMetricsConstants.RPC_PORT_INFO;
import AppAttemptMetricsConstants.STATE_INFO;
import AppAttemptMetricsConstants.TRACKING_URL_INFO;
import ApplicationMetricsConstants.ACLS_UPDATED_EVENT_TYPE;
import ApplicationMetricsConstants.AM_CONTAINER_LAUNCH_COMMAND;
import ApplicationMetricsConstants.AM_NODE_LABEL_EXPRESSION;
import ApplicationMetricsConstants.APPLICATION_PRIORITY_INFO;
import ApplicationMetricsConstants.APP_CPU_METRICS;
import ApplicationMetricsConstants.APP_CPU_PREEMPT_METRICS;
import ApplicationMetricsConstants.APP_MEM_METRICS;
import ApplicationMetricsConstants.APP_MEM_PREEMPT_METRICS;
import ApplicationMetricsConstants.APP_NODE_LABEL_EXPRESSION;
import ApplicationMetricsConstants.APP_VIEW_ACLS_ENTITY_INFO;
import ApplicationMetricsConstants.CREATED_EVENT_TYPE;
import ApplicationMetricsConstants.DIAGNOSTICS_INFO_EVENT_INFO;
import ApplicationMetricsConstants.ENTITY_TYPE;
import ApplicationMetricsConstants.FINAL_STATUS_EVENT_INFO;
import ApplicationMetricsConstants.FINISHED_EVENT_TYPE;
import ApplicationMetricsConstants.LAUNCHED_EVENT_TYPE;
import ApplicationMetricsConstants.NAME_ENTITY_INFO;
import ApplicationMetricsConstants.QUEUE_ENTITY_INFO;
import ApplicationMetricsConstants.STATE_EVENT_INFO;
import ApplicationMetricsConstants.STATE_UPDATED_EVENT_TYPE;
import ApplicationMetricsConstants.SUBMITTED_TIME_ENTITY_INFO;
import ApplicationMetricsConstants.TYPE_ENTITY_INFO;
import ApplicationMetricsConstants.UNMANAGED_APPLICATION_ENTITY_INFO;
import ApplicationMetricsConstants.UPDATED_EVENT_TYPE;
import ApplicationMetricsConstants.USER_ENTITY_INFO;
import ApplicationMetricsConstants.YARN_APP_CALLER_CONTEXT;
import ContainerMetricsConstants.ALLOCATED_HOST_INFO;
import ContainerMetricsConstants.ALLOCATED_MEMORY_INFO;
import ContainerMetricsConstants.ALLOCATED_PORT_INFO;
import ContainerMetricsConstants.ALLOCATED_PRIORITY_INFO;
import ContainerMetricsConstants.ALLOCATED_VCORE_INFO;
import ContainerMetricsConstants.EXIT_STATUS_INFO;
import ContainerMetricsConstants.PARENT_PRIMARIY_FILTER;
import FinalApplicationStatus.UNDEFINED;
import RMAppState.FINISHED;
import YarnApplicationState.RUNNING;
import java.util.Collections;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryServer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.timeline.TimelineReader.Field;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSystemMetricsPublisher {
    private static ApplicationHistoryServer timelineServer;

    private static TimelineServiceV1Publisher metricsPublisher;

    private static TimelineStore store;

    @Test(timeout = 10000)
    public void testPublishApplicationMetrics() throws Exception {
        long stateUpdateTimeStamp = System.currentTimeMillis();
        for (int i = 1; i <= 2; ++i) {
            ApplicationId appId = ApplicationId.newInstance(0, i);
            RMApp app = TestSystemMetricsPublisher.createRMApp(appId);
            TestSystemMetricsPublisher.metricsPublisher.appCreated(app, app.getStartTime());
            TestSystemMetricsPublisher.metricsPublisher.appLaunched(app, app.getLaunchTime());
            if (i == 1) {
                Mockito.when(app.getQueue()).thenReturn("new test queue");
                ApplicationSubmissionContext asc = Mockito.mock(ApplicationSubmissionContext.class);
                Mockito.when(asc.getUnmanagedAM()).thenReturn(false);
                Mockito.when(asc.getPriority()).thenReturn(Priority.newInstance(1));
                Mockito.when(asc.getNodeLabelExpression()).thenReturn("high-cpu");
                ContainerLaunchContext containerLaunchContext = Mockito.mock(ContainerLaunchContext.class);
                Mockito.when(containerLaunchContext.getCommands()).thenReturn(Collections.singletonList("java -Xmx1024m"));
                Mockito.when(asc.getAMContainerSpec()).thenReturn(containerLaunchContext);
                Mockito.when(app.getApplicationSubmissionContext()).thenReturn(asc);
                Mockito.when(app.getApplicationPriority()).thenReturn(Priority.newInstance(1));
                TestSystemMetricsPublisher.metricsPublisher.appUpdated(app, 4L);
            } else {
                TestSystemMetricsPublisher.metricsPublisher.appUpdated(app, 4L);
            }
            TestSystemMetricsPublisher.metricsPublisher.appStateUpdated(app, RUNNING, stateUpdateTimeStamp);
            TestSystemMetricsPublisher.metricsPublisher.appFinished(app, FINISHED, app.getFinishTime());
            if (i == 1) {
                TestSystemMetricsPublisher.metricsPublisher.appACLsUpdated(app, "uers1,user2", 4L);
            } else {
                // in case user doesn't specify the ACLs
                TestSystemMetricsPublisher.metricsPublisher.appACLsUpdated(app, null, 4L);
            }
            TimelineEntity entity = null;
            do {
                entity = TestSystemMetricsPublisher.store.getEntity(appId.toString(), ENTITY_TYPE, EnumSet.allOf(Field.class));
                // ensure Five events are both published before leaving the loop
            } while ((entity == null) || ((entity.getEvents().size()) < 6) );
            // verify all the fields
            Assert.assertEquals(ENTITY_TYPE, entity.getEntityType());
            Assert.assertEquals(app.getApplicationId().toString(), entity.getEntityId());
            Assert.assertEquals(app.getName(), entity.getOtherInfo().get(NAME_ENTITY_INFO));
            if (i != 1) {
                Assert.assertEquals(app.getQueue(), entity.getOtherInfo().get(QUEUE_ENTITY_INFO));
            }
            Assert.assertEquals(app.getApplicationSubmissionContext().getUnmanagedAM(), entity.getOtherInfo().get(UNMANAGED_APPLICATION_ENTITY_INFO));
            if (i != 1) {
                Assert.assertEquals(app.getApplicationSubmissionContext().getPriority().getPriority(), entity.getOtherInfo().get(APPLICATION_PRIORITY_INFO));
            }
            Assert.assertEquals(app.getAmNodeLabelExpression(), entity.getOtherInfo().get(AM_NODE_LABEL_EXPRESSION));
            Assert.assertEquals(app.getApplicationSubmissionContext().getNodeLabelExpression(), entity.getOtherInfo().get(APP_NODE_LABEL_EXPRESSION));
            Assert.assertEquals(app.getUser(), entity.getOtherInfo().get(USER_ENTITY_INFO));
            Assert.assertEquals(app.getApplicationType(), entity.getOtherInfo().get(TYPE_ENTITY_INFO));
            Assert.assertEquals(app.getSubmitTime(), entity.getOtherInfo().get(SUBMITTED_TIME_ENTITY_INFO));
            Assert.assertTrue(TestSystemMetricsPublisher.verifyAppTags(app.getApplicationTags(), entity.getOtherInfo()));
            if (i == 1) {
                Assert.assertEquals("uers1,user2", entity.getOtherInfo().get(APP_VIEW_ACLS_ENTITY_INFO));
                Assert.assertEquals(app.getApplicationSubmissionContext().getAMContainerSpec().getCommands(), entity.getOtherInfo().get(AM_CONTAINER_LAUNCH_COMMAND));
            } else {
                Assert.assertEquals("", entity.getOtherInfo().get(APP_VIEW_ACLS_ENTITY_INFO));
                Assert.assertEquals(app.getRMAppMetrics().getMemorySeconds(), Long.parseLong(entity.getOtherInfo().get(APP_MEM_METRICS).toString()));
                Assert.assertEquals(app.getRMAppMetrics().getVcoreSeconds(), Long.parseLong(entity.getOtherInfo().get(APP_CPU_METRICS).toString()));
                Assert.assertEquals(app.getRMAppMetrics().getPreemptedMemorySeconds(), Long.parseLong(entity.getOtherInfo().get(APP_MEM_PREEMPT_METRICS).toString()));
                Assert.assertEquals(app.getRMAppMetrics().getPreemptedVcoreSeconds(), Long.parseLong(entity.getOtherInfo().get(APP_CPU_PREEMPT_METRICS).toString()));
            }
            Assert.assertEquals("context", entity.getOtherInfo().get(YARN_APP_CALLER_CONTEXT));
            boolean hasCreatedEvent = false;
            boolean hasLaunchedEvent = false;
            boolean hasUpdatedEvent = false;
            boolean hasFinishedEvent = false;
            boolean hasACLsUpdatedEvent = false;
            boolean hasStateUpdateEvent = false;
            for (TimelineEvent event : entity.getEvents()) {
                if (event.getEventType().equals(CREATED_EVENT_TYPE)) {
                    hasCreatedEvent = true;
                    Assert.assertEquals(app.getStartTime(), event.getTimestamp());
                } else
                    if (event.getEventType().equals(LAUNCHED_EVENT_TYPE)) {
                        hasLaunchedEvent = true;
                        Assert.assertEquals(app.getLaunchTime(), event.getTimestamp());
                    } else
                        if (event.getEventType().equals(FINISHED_EVENT_TYPE)) {
                            hasFinishedEvent = true;
                            Assert.assertEquals(app.getFinishTime(), event.getTimestamp());
                            Assert.assertEquals(app.getDiagnostics().toString(), event.getEventInfo().get(DIAGNOSTICS_INFO_EVENT_INFO));
                            Assert.assertEquals(app.getFinalApplicationStatus().toString(), event.getEventInfo().get(FINAL_STATUS_EVENT_INFO));
                            Assert.assertEquals(YarnApplicationState.FINISHED.toString(), event.getEventInfo().get(STATE_EVENT_INFO));
                        } else
                            if (event.getEventType().equals(UPDATED_EVENT_TYPE)) {
                                hasUpdatedEvent = true;
                                Assert.assertEquals(4L, event.getTimestamp());
                                if (1 == i) {
                                    Assert.assertEquals(1, event.getEventInfo().get(APPLICATION_PRIORITY_INFO));
                                    Assert.assertEquals("new test queue", event.getEventInfo().get(QUEUE_ENTITY_INFO));
                                }
                            } else
                                if (event.getEventType().equals(ACLS_UPDATED_EVENT_TYPE)) {
                                    hasACLsUpdatedEvent = true;
                                    Assert.assertEquals(4L, event.getTimestamp());
                                } else
                                    if (event.getEventType().equals(STATE_UPDATED_EVENT_TYPE)) {
                                        hasStateUpdateEvent = true;
                                        Assert.assertEquals(event.getTimestamp(), stateUpdateTimeStamp);
                                        Assert.assertEquals(RUNNING.toString(), event.getEventInfo().get(STATE_EVENT_INFO));
                                    }





            }
            // Do assertTrue verification separately for easier debug
            Assert.assertTrue(hasCreatedEvent);
            Assert.assertTrue(hasLaunchedEvent);
            Assert.assertTrue(hasFinishedEvent);
            Assert.assertTrue(hasACLsUpdatedEvent);
            Assert.assertTrue(hasUpdatedEvent);
            Assert.assertTrue(hasStateUpdateEvent);
        }
    }

    @Test(timeout = 10000)
    public void testPublishAppAttemptMetricsForUnmanagedAM() throws Exception {
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1);
        RMAppAttempt appAttempt = TestSystemMetricsPublisher.createRMAppAttempt(appAttemptId, true);
        TestSystemMetricsPublisher.metricsPublisher.appAttemptRegistered(appAttempt, ((Integer.MAX_VALUE) + 1L));
        RMApp app = Mockito.mock(RMApp.class);
        Mockito.when(app.getFinalApplicationStatus()).thenReturn(UNDEFINED);
        TestSystemMetricsPublisher.metricsPublisher.appAttemptFinished(appAttempt, RMAppAttemptState.FINISHED, app, ((Integer.MAX_VALUE) + 2L));
        TimelineEntity entity = null;
        do {
            entity = TestSystemMetricsPublisher.store.getEntity(appAttemptId.toString(), AppAttemptMetricsConstants.ENTITY_TYPE, EnumSet.allOf(Field.class));
            // ensure two events are both published before leaving the loop
        } while ((entity == null) || ((entity.getEvents().size()) < 2) );
    }

    @Test(timeout = 10000)
    public void testPublishAppAttemptMetrics() throws Exception {
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1);
        RMAppAttempt appAttempt = TestSystemMetricsPublisher.createRMAppAttempt(appAttemptId, false);
        TestSystemMetricsPublisher.metricsPublisher.appAttemptRegistered(appAttempt, ((Integer.MAX_VALUE) + 1L));
        RMApp app = Mockito.mock(RMApp.class);
        Mockito.when(app.getFinalApplicationStatus()).thenReturn(UNDEFINED);
        TestSystemMetricsPublisher.metricsPublisher.appAttemptFinished(appAttempt, RMAppAttemptState.FINISHED, app, ((Integer.MAX_VALUE) + 2L));
        TimelineEntity entity = null;
        do {
            entity = TestSystemMetricsPublisher.store.getEntity(appAttemptId.toString(), AppAttemptMetricsConstants.ENTITY_TYPE, EnumSet.allOf(Field.class));
            // ensure two events are both published before leaving the loop
        } while ((entity == null) || ((entity.getEvents().size()) < 2) );
        // verify all the fields
        Assert.assertEquals(AppAttemptMetricsConstants.ENTITY_TYPE, entity.getEntityType());
        Assert.assertEquals(appAttemptId.toString(), entity.getEntityId());
        Assert.assertEquals(appAttemptId.getApplicationId().toString(), entity.getPrimaryFilters().get(PARENT_PRIMARY_FILTER).iterator().next());
        boolean hasRegisteredEvent = false;
        boolean hasFinishedEvent = false;
        for (TimelineEvent event : entity.getEvents()) {
            if (event.getEventType().equals(REGISTERED_EVENT_TYPE)) {
                hasRegisteredEvent = true;
                Assert.assertEquals(appAttempt.getHost(), event.getEventInfo().get(HOST_INFO));
                Assert.assertEquals(appAttempt.getRpcPort(), event.getEventInfo().get(RPC_PORT_INFO));
                Assert.assertEquals(appAttempt.getMasterContainer().getId().toString(), event.getEventInfo().get(MASTER_CONTAINER_INFO));
            } else
                if (event.getEventType().equals(AppAttemptMetricsConstants.FINISHED_EVENT_TYPE)) {
                    hasFinishedEvent = true;
                    Assert.assertEquals(appAttempt.getDiagnostics(), event.getEventInfo().get(DIAGNOSTICS_INFO));
                    Assert.assertEquals(appAttempt.getTrackingUrl(), event.getEventInfo().get(TRACKING_URL_INFO));
                    Assert.assertEquals(appAttempt.getOriginalTrackingUrl(), event.getEventInfo().get(ORIGINAL_TRACKING_URL_INFO));
                    Assert.assertEquals(UNDEFINED.toString(), event.getEventInfo().get(FINAL_STATUS_INFO));
                    Assert.assertEquals(YarnApplicationAttemptState.FINISHED.toString(), event.getEventInfo().get(STATE_INFO));
                }

        }
        Assert.assertTrue((hasRegisteredEvent && hasFinishedEvent));
    }

    @Test(timeout = 10000)
    public void testPublishHostPortInfoOnContainerFinished() throws Exception {
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1);
        RMContainer container = TestSystemMetricsPublisher.createRMContainer(containerId);
        TestSystemMetricsPublisher.metricsPublisher.containerFinished(container, container.getFinishTime());
        TimelineEntity entity = null;
        do {
            entity = TestSystemMetricsPublisher.store.getEntity(containerId.toString(), ContainerMetricsConstants.ENTITY_TYPE, EnumSet.allOf(Field.class));
        } while ((entity == null) || ((entity.getEvents().size()) < 1) );
        Assert.assertNotNull(entity.getOtherInfo());
        Assert.assertEquals(2, entity.getOtherInfo().size());
        Assert.assertNotNull(entity.getOtherInfo().get(ALLOCATED_HOST_INFO));
        Assert.assertNotNull(entity.getOtherInfo().get(ALLOCATED_PORT_INFO));
        Assert.assertEquals(container.getAllocatedNode().getHost(), entity.getOtherInfo().get(ALLOCATED_HOST_INFO));
        Assert.assertEquals(container.getAllocatedNode().getPort(), entity.getOtherInfo().get(ALLOCATED_PORT_INFO));
    }

    @Test(timeout = 10000)
    public void testPublishContainerMetrics() throws Exception {
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1);
        RMContainer container = TestSystemMetricsPublisher.createRMContainer(containerId);
        TestSystemMetricsPublisher.metricsPublisher.containerCreated(container, container.getCreationTime());
        TestSystemMetricsPublisher.metricsPublisher.containerFinished(container, container.getFinishTime());
        TimelineEntity entity = null;
        do {
            entity = TestSystemMetricsPublisher.store.getEntity(containerId.toString(), ContainerMetricsConstants.ENTITY_TYPE, EnumSet.allOf(Field.class));
            // ensure two events are both published before leaving the loop
        } while ((entity == null) || ((entity.getEvents().size()) < 2) );
        // verify all the fields
        Assert.assertEquals(ContainerMetricsConstants.ENTITY_TYPE, entity.getEntityType());
        Assert.assertEquals(containerId.toString(), entity.getEntityId());
        Assert.assertEquals(containerId.getApplicationAttemptId().toString(), entity.getPrimaryFilters().get(PARENT_PRIMARIY_FILTER).iterator().next());
        Assert.assertEquals(container.getAllocatedNode().getHost(), entity.getOtherInfo().get(ALLOCATED_HOST_INFO));
        Assert.assertEquals(container.getAllocatedNode().getPort(), entity.getOtherInfo().get(ALLOCATED_PORT_INFO));
        // KeyValueBasedTimelineStore could cast long to integer, need make sure
        // variables for compare have same type.
        Assert.assertEquals(container.getAllocatedResource().getMemorySize(), ((Integer) (entity.getOtherInfo().get(ALLOCATED_MEMORY_INFO))).longValue());
        Assert.assertEquals(container.getAllocatedResource().getVirtualCores(), entity.getOtherInfo().get(ALLOCATED_VCORE_INFO));
        Assert.assertEquals(container.getAllocatedPriority().getPriority(), entity.getOtherInfo().get(ALLOCATED_PRIORITY_INFO));
        boolean hasCreatedEvent = false;
        boolean hasFinishedEvent = false;
        for (TimelineEvent event : entity.getEvents()) {
            if (event.getEventType().equals(ContainerMetricsConstants.CREATED_EVENT_TYPE)) {
                hasCreatedEvent = true;
                Assert.assertEquals(container.getCreationTime(), event.getTimestamp());
            } else
                if (event.getEventType().equals(ContainerMetricsConstants.FINISHED_EVENT_TYPE)) {
                    hasFinishedEvent = true;
                    Assert.assertEquals(container.getFinishTime(), event.getTimestamp());
                    Assert.assertEquals(container.getDiagnosticsInfo(), event.getEventInfo().get(ContainerMetricsConstants.DIAGNOSTICS_INFO));
                    Assert.assertEquals(container.getContainerExitStatus(), event.getEventInfo().get(EXIT_STATUS_INFO));
                    Assert.assertEquals(container.getContainerState().toString(), event.getEventInfo().get(ContainerMetricsConstants.STATE_INFO));
                }

        }
        Assert.assertTrue((hasCreatedEvent && hasFinishedEvent));
    }
}

