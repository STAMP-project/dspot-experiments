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
package org.apache.ambari.server.events.listeners.upgrade;


import MaintenanceState.OFF;
import MaintenanceState.ON;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.ambari.server.events.AlertUpdateEvent;
import org.apache.ambari.server.events.MaintenanceModeEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.events.publishers.STOMPUpdatePublisher;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.server.orm.entities.AlertCurrentEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.MaintenanceState;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class AlertMaintenanceModeListenerTest {
    private static final String HOSTNAME = "c6401.ambari.apache.org";

    private static final String SERVICE = "HDFS";

    private static final String COMPONENT = "NAMENODE";

    private static final Long CLUSTER_ID = 1L;

    private static final String DEFINITION_NAME = "definition_name";

    @Inject
    private AmbariEventPublisher m_eventPublisher;

    @Inject
    private AlertsDAO m_alertsDAO;

    private Injector injector;

    /**
     * Tests that only the host alert has its maintenance mode changed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHostMaintenanceMode() throws Exception {
        List<AlertCurrentEntity> alerts = getMockAlerts("HOST");
        AlertCurrentEntity hostAlert = alerts.get(0);
        AlertCurrentEntity serviceAlert = alerts.get(1);
        AlertCurrentEntity componentAlert = alerts.get(2);
        EasyMock.expect(hostAlert.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        hostAlert.setMaintenanceState(ON);
        EasyMock.expectLastCall().once();
        EasyMock.expect(m_alertsDAO.merge(hostAlert)).andReturn(hostAlert).once();
        Host host = EasyMock.createNiceMock(Host.class);
        EasyMock.expect(host.getHostName()).andReturn(AlertMaintenanceModeListenerTest.HOSTNAME).atLeastOnce();
        STOMPUpdatePublisher stompUpdatePublisher = injector.getInstance(STOMPUpdatePublisher.class);
        Capture<AlertUpdateEvent> alertUpdateEventCapture = EasyMock.newCapture();
        stompUpdatePublisher.publish(EasyMock.capture(alertUpdateEventCapture));
        EasyMock.replay(hostAlert, serviceAlert, componentAlert, host, m_alertsDAO, stompUpdatePublisher);
        MaintenanceModeEvent hostEvent = /* cluster id */
        new MaintenanceModeEvent(MaintenanceState.ON, 1, host);
        m_eventPublisher.publish(hostEvent);
        EasyMock.verify(hostAlert, serviceAlert, componentAlert, host, m_alertsDAO);
        AlertUpdateEvent alertUpdateEvent = alertUpdateEventCapture.getValue();
        Assert.assertNotNull(alertUpdateEvent);
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().containsKey(AlertMaintenanceModeListenerTest.CLUSTER_ID));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).containsKey(AlertMaintenanceModeListenerTest.DEFINITION_NAME));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).get(AlertMaintenanceModeListenerTest.DEFINITION_NAME).State.Ok.MaintenanceCount);
    }

    /**
     * Tests that only the service alert has its maintenance mode changed.
     */
    @Test
    public void testServiceMaintenanceMode() throws Exception {
        List<AlertCurrentEntity> alerts = getMockAlerts("SERVICE");
        AlertCurrentEntity hostAlert = alerts.get(0);
        AlertCurrentEntity serviceAlert = alerts.get(1);
        AlertCurrentEntity componentAlert = alerts.get(2);
        EasyMock.expect(serviceAlert.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        serviceAlert.setMaintenanceState(ON);
        EasyMock.expectLastCall().once();
        EasyMock.expect(m_alertsDAO.merge(serviceAlert)).andReturn(serviceAlert).once();
        Service service = EasyMock.createNiceMock(Service.class);
        EasyMock.expect(service.getName()).andReturn(AlertMaintenanceModeListenerTest.SERVICE).atLeastOnce();
        STOMPUpdatePublisher stompUpdatePublisher = injector.getInstance(STOMPUpdatePublisher.class);
        Capture<AlertUpdateEvent> alertUpdateEventCapture = EasyMock.newCapture();
        stompUpdatePublisher.publish(EasyMock.capture(alertUpdateEventCapture));
        EasyMock.replay(hostAlert, serviceAlert, componentAlert, service, m_alertsDAO, stompUpdatePublisher);
        MaintenanceModeEvent serviceEvent = new MaintenanceModeEvent(MaintenanceState.ON, service);
        m_eventPublisher.publish(serviceEvent);
        EasyMock.verify(hostAlert, serviceAlert, componentAlert, service, m_alertsDAO);
        AlertUpdateEvent alertUpdateEvent = alertUpdateEventCapture.getValue();
        Assert.assertNotNull(alertUpdateEvent);
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().containsKey(AlertMaintenanceModeListenerTest.CLUSTER_ID));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).containsKey(AlertMaintenanceModeListenerTest.DEFINITION_NAME));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).get(AlertMaintenanceModeListenerTest.DEFINITION_NAME).State.Ok.MaintenanceCount);
    }

    @Test
    public void testComponentMaintenanceMode() throws Exception {
        List<AlertCurrentEntity> alerts = getMockAlerts("SCH");
        AlertCurrentEntity hostAlert = alerts.get(0);
        AlertCurrentEntity serviceAlert = alerts.get(1);
        AlertCurrentEntity componentAlert = alerts.get(2);
        EasyMock.expect(componentAlert.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        componentAlert.setMaintenanceState(ON);
        EasyMock.expectLastCall().once();
        EasyMock.expect(m_alertsDAO.merge(componentAlert)).andReturn(componentAlert).once();
        ServiceComponentHost serviceComponentHost = EasyMock.createNiceMock(ServiceComponentHost.class);
        EasyMock.expect(serviceComponentHost.getHostName()).andReturn(AlertMaintenanceModeListenerTest.HOSTNAME).atLeastOnce();
        EasyMock.expect(serviceComponentHost.getServiceName()).andReturn(AlertMaintenanceModeListenerTest.SERVICE).atLeastOnce();
        EasyMock.expect(serviceComponentHost.getServiceComponentName()).andReturn(AlertMaintenanceModeListenerTest.COMPONENT).atLeastOnce();
        STOMPUpdatePublisher stompUpdatePublisher = injector.getInstance(STOMPUpdatePublisher.class);
        Capture<AlertUpdateEvent> alertUpdateEventCapture = EasyMock.newCapture();
        stompUpdatePublisher.publish(EasyMock.capture(alertUpdateEventCapture));
        EasyMock.replay(hostAlert, serviceAlert, componentAlert, serviceComponentHost, m_alertsDAO, stompUpdatePublisher);
        MaintenanceModeEvent serviceComponentHostEvent = new MaintenanceModeEvent(MaintenanceState.ON, serviceComponentHost);
        m_eventPublisher.publish(serviceComponentHostEvent);
        EasyMock.verify(hostAlert, serviceAlert, componentAlert, serviceComponentHost, m_alertsDAO);
        AlertUpdateEvent alertUpdateEvent = alertUpdateEventCapture.getValue();
        Assert.assertNotNull(alertUpdateEvent);
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().containsKey(AlertMaintenanceModeListenerTest.CLUSTER_ID));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).size());
        Assert.assertTrue(alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).containsKey(AlertMaintenanceModeListenerTest.DEFINITION_NAME));
        Assert.assertEquals(1, alertUpdateEvent.getSummaries().get(AlertMaintenanceModeListenerTest.CLUSTER_ID).get(AlertMaintenanceModeListenerTest.DEFINITION_NAME).State.Ok.MaintenanceCount);
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(Binder binder) {
            Cluster cluster = EasyMock.createNiceMock(Cluster.class);
            binder.bind(Clusters.class).toInstance(createNiceMock(Clusters.class));
            binder.bind(DBAccessor.class).toInstance(createNiceMock(DBAccessor.class));
            binder.bind(Cluster.class).toInstance(cluster);
            binder.bind(AlertDefinitionDAO.class).toInstance(createNiceMock(AlertDefinitionDAO.class));
            binder.bind(AlertsDAO.class).toInstance(createNiceMock(AlertsDAO.class));
            binder.bind(EntityManager.class).toInstance(createNiceMock(EntityManager.class));
            binder.bind(STOMPUpdatePublisher.class).toInstance(createNiceMock(STOMPUpdatePublisher.class));
        }
    }
}

