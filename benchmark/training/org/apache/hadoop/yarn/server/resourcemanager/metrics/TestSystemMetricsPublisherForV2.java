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


import AppAttemptMetricsConstants.REGISTERED_EVENT_TYPE;
import ApplicationMetricsConstants.CREATED_EVENT_TYPE;
import ContainerMetricsConstants.CREATED_IN_RM_EVENT_TYPE;
import FinalApplicationStatus.UNDEFINED;
import RMAppState.FINISHED;
import YarnConfiguration.DEFAULT_RM_PUBLISH_CONTAINER_EVENTS_ENABLED;
import YarnConfiguration.RM_PUBLISH_CONTAINER_EVENTS_ENABLED;
import java.io.File;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntityType;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.timelineservice.RMTimelineCollectorManager;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineWriterImpl;
import org.apache.hadoop.yarn.util.TimelineServiceHelper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSystemMetricsPublisherForV2 {
    /**
     * The folder where the FileSystemTimelineWriterImpl writes the entities.
     */
    private static File testRootDir = new File("target", ((TestSystemMetricsPublisherForV2.class.getName()) + "-localDir")).getAbsoluteFile();

    private static TimelineServiceV2Publisher metricsPublisher;

    private static DrainDispatcher dispatcher = new DrainDispatcher();

    private static ConcurrentMap<ApplicationId, RMApp> rmAppsMapInContext;

    private static RMTimelineCollectorManager rmTimelineCollectorManager;

    @Test
    public void testSystemMetricPublisherInitialization() {
        @SuppressWarnings("resource")
        TimelineServiceV2Publisher publisher = new TimelineServiceV2Publisher(Mockito.mock(RMTimelineCollectorManager.class));
        try {
            Configuration conf = TestSystemMetricsPublisherForV2.getTimelineV2Conf();
            conf.setBoolean(RM_PUBLISH_CONTAINER_EVENTS_ENABLED, DEFAULT_RM_PUBLISH_CONTAINER_EVENTS_ENABLED);
            publisher.init(conf);
            Assert.assertFalse("Default configuration should not publish container events from RM", publisher.isPublishContainerEvents());
            publisher.stop();
            publisher = new TimelineServiceV2Publisher(Mockito.mock(RMTimelineCollectorManager.class));
            conf = TestSystemMetricsPublisherForV2.getTimelineV2Conf();
            publisher.init(conf);
            Assert.assertTrue(("Expected to have registered event handlers and set ready to " + "publish events after init"), publisher.isPublishContainerEvents());
            publisher.start();
            Assert.assertTrue("Expected to publish container events from RM", publisher.isPublishContainerEvents());
        } finally {
            publisher.stop();
        }
    }

    @Test(timeout = 10000)
    public void testPublishApplicationMetrics() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        RMApp app = createAppAndRegister(appId);
        TestSystemMetricsPublisherForV2.metricsPublisher.appCreated(app, app.getStartTime());
        TestSystemMetricsPublisherForV2.metricsPublisher.appLaunched(app, app.getLaunchTime());
        TestSystemMetricsPublisherForV2.metricsPublisher.appACLsUpdated(app, "user1,user2", 4L);
        TestSystemMetricsPublisherForV2.metricsPublisher.appFinished(app, FINISHED, app.getFinishTime());
        TestSystemMetricsPublisherForV2.dispatcher.await();
        String outputDirApp = (((getTimelineEntityDir(app)) + "/") + (TimelineEntityType.YARN_APPLICATION)) + "/";
        File entityFolder = new File(outputDirApp);
        Assert.assertTrue(entityFolder.isDirectory());
        // file name is <entityId>.thist
        String timelineServiceFileName = (appId.toString()) + (FileSystemTimelineWriterImpl.TIMELINE_SERVICE_STORAGE_EXTENSION);
        File appFile = new File(outputDirApp, timelineServiceFileName);
        Assert.assertTrue(appFile.exists());
        TestSystemMetricsPublisherForV2.verifyEntity(appFile, 4, CREATED_EVENT_TYPE, 8, 0);
    }

    @Test(timeout = 10000)
    public void testPublishAppAttemptMetrics() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        RMApp app = TestSystemMetricsPublisherForV2.rmAppsMapInContext.get(appId);
        if (app == null) {
            app = createAppAndRegister(appId);
        }
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        RMAppAttempt appAttempt = TestSystemMetricsPublisherForV2.createRMAppAttempt(appAttemptId);
        TestSystemMetricsPublisherForV2.metricsPublisher.appAttemptRegistered(appAttempt, ((Integer.MAX_VALUE) + 1L));
        Mockito.when(app.getFinalApplicationStatus()).thenReturn(UNDEFINED);
        TestSystemMetricsPublisherForV2.metricsPublisher.appAttemptFinished(appAttempt, RMAppAttemptState.FINISHED, app, ((Integer.MAX_VALUE) + 2L));
        TestSystemMetricsPublisherForV2.dispatcher.await();
        String outputDirApp = (((getTimelineEntityDir(app)) + "/") + (TimelineEntityType.YARN_APPLICATION_ATTEMPT)) + "/";
        File entityFolder = new File(outputDirApp);
        Assert.assertTrue(entityFolder.isDirectory());
        // file name is <entityId>.thist
        String timelineServiceFileName = (appAttemptId.toString()) + (FileSystemTimelineWriterImpl.TIMELINE_SERVICE_STORAGE_EXTENSION);
        File appFile = new File(outputDirApp, timelineServiceFileName);
        Assert.assertTrue(appFile.exists());
        TestSystemMetricsPublisherForV2.verifyEntity(appFile, 2, REGISTERED_EVENT_TYPE, 0, TimelineServiceHelper.invertLong(appAttemptId.getAttemptId()));
    }

    @Test(timeout = 10000)
    public void testPublishContainerMetrics() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        RMApp app = TestSystemMetricsPublisherForV2.rmAppsMapInContext.get(appId);
        if (app == null) {
            app = createAppAndRegister(appId);
        }
        ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(appId, 1), 1);
        RMContainer container = TestSystemMetricsPublisherForV2.createRMContainer(containerId);
        TestSystemMetricsPublisherForV2.metricsPublisher.containerCreated(container, container.getCreationTime());
        TestSystemMetricsPublisherForV2.metricsPublisher.containerFinished(container, container.getFinishTime());
        TestSystemMetricsPublisherForV2.dispatcher.await();
        String outputDirApp = (((getTimelineEntityDir(app)) + "/") + (TimelineEntityType.YARN_CONTAINER)) + "/";
        File entityFolder = new File(outputDirApp);
        Assert.assertTrue(entityFolder.isDirectory());
        // file name is <entityId>.thist
        String timelineServiceFileName = (containerId.toString()) + (FileSystemTimelineWriterImpl.TIMELINE_SERVICE_STORAGE_EXTENSION);
        File appFile = new File(outputDirApp, timelineServiceFileName);
        Assert.assertTrue(appFile.exists());
        TestSystemMetricsPublisherForV2.verifyEntity(appFile, 2, CREATED_IN_RM_EVENT_TYPE, 0, 0);
    }
}

