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
package org.apache.hadoop.yarn.server.nodemanager.timelineservice;


import ContainerMetricsConstants.DIAGNOSTICS_INFO;
import ContainerMetricsConstants.EXIT_STATUS_INFO;
import ResourceCalculatorProcessTree.UNAVAILABLE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.timelineservice.ContainerEntity;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.client.api.impl.TimelineV2ClientImpl;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationContainerFinishedEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestNMTimelinePublisher {
    private static final String MEMORY_ID = "MEMORY";

    private static final String CPU_ID = "CPU";

    private NMTimelinePublisher publisher;

    private TestNMTimelinePublisher.DummyTimelineClient timelineClient;

    private Configuration conf;

    private DrainDispatcher dispatcher;

    @Test
    public void testPublishContainerFinish() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(0, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId cId = ContainerId.newContainerId(appAttemptId, 1);
        String diag = "test-diagnostics";
        int exitStatus = 0;
        ContainerStatus cStatus = Mockito.mock(ContainerStatus.class);
        Mockito.when(cStatus.getContainerId()).thenReturn(cId);
        Mockito.when(cStatus.getDiagnostics()).thenReturn(diag);
        Mockito.when(cStatus.getExitStatus()).thenReturn(exitStatus);
        long timeStamp = System.currentTimeMillis();
        ApplicationContainerFinishedEvent finishedEvent = new ApplicationContainerFinishedEvent(cStatus, timeStamp);
        publisher.createTimelineClient(appId);
        publisher.publishApplicationEvent(finishedEvent);
        publisher.stopTimelineClient(appId);
        dispatcher.await();
        ContainerEntity cEntity = new ContainerEntity();
        cEntity.setId(cId.toString());
        TimelineEntity[] lastPublishedEntities = timelineClient.getLastPublishedEntities();
        Assert.assertNotNull(lastPublishedEntities);
        Assert.assertEquals(1, lastPublishedEntities.length);
        TimelineEntity entity = lastPublishedEntities[0];
        Assert.assertTrue(cEntity.equals(entity));
        Assert.assertEquals(diag, entity.getInfo().get(DIAGNOSTICS_INFO));
        Assert.assertEquals(exitStatus, entity.getInfo().get(EXIT_STATUS_INFO));
    }

    @Test
    public void testContainerResourceUsage() {
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        publisher.createTimelineClient(appId);
        Container aContainer = Mockito.mock(Container.class);
        Mockito.when(aContainer.getContainerId()).thenReturn(ContainerId.newContainerId(ApplicationAttemptId.newInstance(appId, 1), 0L));
        publisher.reportContainerResourceUsage(aContainer, 1024L, 8.0F);
        verifyPublishedResourceUsageMetrics(timelineClient, 1024L, 8);
        timelineClient.reset();
        publisher.reportContainerResourceUsage(aContainer, 1024L, 0.8F);
        verifyPublishedResourceUsageMetrics(timelineClient, 1024L, 1);
        timelineClient.reset();
        publisher.reportContainerResourceUsage(aContainer, 1024L, 0.49F);
        verifyPublishedResourceUsageMetrics(timelineClient, 1024L, 0);
        timelineClient.reset();
        publisher.reportContainerResourceUsage(aContainer, 1024L, ((float) (UNAVAILABLE)));
        verifyPublishedResourceUsageMetrics(timelineClient, 1024L, UNAVAILABLE);
    }

    protected static class DummyTimelineClient extends TimelineV2ClientImpl {
        public DummyTimelineClient(ApplicationId appId) {
            super(appId);
        }

        private TimelineEntity[] lastPublishedEntities;

        @Override
        public void putEntitiesAsync(TimelineEntity... entities) throws IOException, YarnException {
            this.lastPublishedEntities = entities;
        }

        @Override
        public void putEntities(TimelineEntity... entities) throws IOException, YarnException {
            this.lastPublishedEntities = entities;
        }

        public TimelineEntity[] getLastPublishedEntities() {
            return lastPublishedEntities;
        }

        public void reset() {
            lastPublishedEntities = null;
        }
    }
}

