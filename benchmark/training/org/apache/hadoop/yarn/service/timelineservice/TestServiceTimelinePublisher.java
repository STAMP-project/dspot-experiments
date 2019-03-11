/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.yarn.service.timelineservice;


import ContainerState.READY;
import ContainerState.RUNNING_BUT_UNREADY;
import FinalApplicationStatus.ENDED;
import ServiceTimelineEntityType.COMPONENT;
import ServiceTimelineEntityType.SERVICE_ATTEMPT;
import ServiceTimelineMetricsConstants.BARE_HOST;
import ServiceTimelineMetricsConstants.COMPONENT_NAME;
import ServiceTimelineMetricsConstants.STATE;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity.Identifier;
import org.apache.hadoop.yarn.client.api.TimelineV2Client;
import org.apache.hadoop.yarn.client.api.impl.TimelineV2ClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.service.ServiceContext;
import org.apache.hadoop.yarn.service.api.records.Container;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstance;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstanceId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test class for ServiceTimelinePublisher.
 */
public class TestServiceTimelinePublisher {
    private TimelineV2Client timelineClient;

    private Configuration config;

    private ServiceTimelinePublisher serviceTimelinePublisher;

    private static String SERVICE_NAME = "HBASE";

    private static String SERVICEID = "application_1490093646524_0005";

    private static String ARTIFACTID = "ARTIFACTID";

    private static String COMPONENT_NAME = "DEFAULT";

    private static String CONTAINER_ID = "container_e02_1490093646524_0005_01_000001";

    private static String CONTAINER_IP = "localhost";

    private static String CONTAINER_HOSTNAME = "cnl124-localhost.site";

    private static String CONTAINER_BAREHOST = "localhost.com";

    @Test
    public void testServiceAttemptEntity() {
        Service service = TestServiceTimelinePublisher.createMockApplication();
        serviceTimelinePublisher.serviceAttemptRegistered(service, new YarnConfiguration());
        Collection<TimelineEntity> lastPublishedEntities = ((TestServiceTimelinePublisher.DummyTimelineClient) (timelineClient)).getLastPublishedEntities();
        // 2 entities because during registration component also registered.
        Assert.assertEquals(2, lastPublishedEntities.size());
        for (TimelineEntity timelineEntity : lastPublishedEntities) {
            if ((timelineEntity.getType()) == (COMPONENT.toString())) {
                verifyComponentTimelineEntity(timelineEntity);
            } else {
                verifyServiceAttemptTimelineEntity(timelineEntity, null, true);
            }
        }
        ServiceContext context = new ServiceContext();
        context.attemptId = ApplicationAttemptId.newInstance(ApplicationId.fromString(service.getId()), 1);
        String exitDiags = "service killed";
        serviceTimelinePublisher.serviceAttemptUnregistered(context, ENDED, exitDiags);
        lastPublishedEntities = ((TestServiceTimelinePublisher.DummyTimelineClient) (timelineClient)).getLastPublishedEntities();
        for (TimelineEntity timelineEntity : lastPublishedEntities) {
            if ((timelineEntity.getType()) == (SERVICE_ATTEMPT.toString())) {
                verifyServiceAttemptTimelineEntity(timelineEntity, exitDiags, false);
            }
        }
    }

    @Test
    public void testComponentInstanceEntity() {
        Container container = new Container();
        container.id(TestServiceTimelinePublisher.CONTAINER_ID).ip(TestServiceTimelinePublisher.CONTAINER_IP).bareHost(TestServiceTimelinePublisher.CONTAINER_BAREHOST).hostname(TestServiceTimelinePublisher.CONTAINER_HOSTNAME).state(RUNNING_BUT_UNREADY).launchTime(new Date());
        ComponentInstanceId id = new ComponentInstanceId(0, TestServiceTimelinePublisher.COMPONENT_NAME);
        ComponentInstance instance = Mockito.mock(ComponentInstance.class);
        Mockito.when(instance.getCompName()).thenReturn(TestServiceTimelinePublisher.COMPONENT_NAME);
        Mockito.when(instance.getCompInstanceName()).thenReturn("comp_instance_name");
        serviceTimelinePublisher.componentInstanceStarted(container, instance);
        Collection<TimelineEntity> lastPublishedEntities = ((TestServiceTimelinePublisher.DummyTimelineClient) (timelineClient)).getLastPublishedEntities();
        Assert.assertEquals(1, lastPublishedEntities.size());
        TimelineEntity entity = lastPublishedEntities.iterator().next();
        Assert.assertEquals(1, entity.getEvents().size());
        Assert.assertEquals(TestServiceTimelinePublisher.CONTAINER_ID, entity.getId());
        Assert.assertEquals(TestServiceTimelinePublisher.CONTAINER_BAREHOST, entity.getInfo().get(BARE_HOST));
        Assert.assertEquals(TestServiceTimelinePublisher.COMPONENT_NAME, entity.getInfo().get(ServiceTimelineMetricsConstants.COMPONENT_NAME));
        Assert.assertEquals(RUNNING_BUT_UNREADY.toString(), entity.getInfo().get(STATE));
        // updated container state
        container.setState(READY);
        serviceTimelinePublisher.componentInstanceIPHostUpdated(container);
        lastPublishedEntities = ((TestServiceTimelinePublisher.DummyTimelineClient) (timelineClient)).getLastPublishedEntities();
        Assert.assertEquals(1, lastPublishedEntities.size());
        entity = lastPublishedEntities.iterator().next();
        Assert.assertEquals(2, entity.getEvents().size());
        Assert.assertEquals(READY.toString(), entity.getInfo().get(STATE));
    }

    protected static class DummyTimelineClient extends TimelineV2ClientImpl {
        private Map<Identifier, TimelineEntity> lastPublishedEntities = new HashMap<>();

        public DummyTimelineClient(ApplicationId appId) {
            super(appId);
        }

        @Override
        public void putEntitiesAsync(TimelineEntity... entities) throws IOException, YarnException {
            putEntities(entities);
        }

        @Override
        public void putEntities(TimelineEntity... entities) throws IOException, YarnException {
            for (TimelineEntity timelineEntity : entities) {
                TimelineEntity entity = lastPublishedEntities.get(timelineEntity.getIdentifier());
                if (entity == null) {
                    lastPublishedEntities.put(timelineEntity.getIdentifier(), timelineEntity);
                } else {
                    entity.addMetrics(timelineEntity.getMetrics());
                    entity.addEvents(timelineEntity.getEvents());
                    entity.addInfo(timelineEntity.getInfo());
                    entity.addConfigs(timelineEntity.getConfigs());
                    entity.addRelatesToEntities(timelineEntity.getRelatesToEntities());
                    entity.addIsRelatedToEntities(timelineEntity.getIsRelatedToEntities());
                }
            }
        }

        public Collection<TimelineEntity> getLastPublishedEntities() {
            return lastPublishedEntities.values();
        }

        public void reset() {
            lastPublishedEntities = null;
        }
    }
}

