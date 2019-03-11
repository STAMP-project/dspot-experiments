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
package org.apache.ambari.server.alerts;


import AlertState.CRITICAL;
import AlertState.OK;
import HostState.HEARTBEAT_LOST;
import HostState.UNHEALTHY;
import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.events.AlertEvent;
import org.apache.ambari.server.events.AlertReceivedEvent;
import org.apache.ambari.server.events.MockEventListener;
import org.apache.ambari.server.events.publishers.AlertEventPublisher;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.state.Alert;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.testutils.PartialNiceMockBinder;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests {@link AgentHeartbeatAlertRunnable}.
 */
public class AgentHeartbeatAlertRunnableTest {
    private static final long CLUSTER_ID = 1;

    private static final String CLUSTER_NAME = "c1";

    private static final String HOSTNAME = "c6401.ambari.apache.org";

    private static final String DEFINITION_NAME = "ambari_server_agent_heartbeat";

    private static final String DEFINITION_SERVICE = "AMBARI";

    private static final String DEFINITION_COMPONENT = "AMBARI_SERVER";

    private static final String DEFINITION_LABEL = "Mock Definition";

    private Clusters m_clusters;

    private Cluster m_cluster;

    private Host m_host;

    private Injector m_injector;

    private AlertDefinitionDAO m_definitionDao;

    private AlertDefinitionEntity m_definition;

    private MockEventListener m_listener;

    private AlertEventPublisher m_eventPublisher;

    private EventBus m_synchronizedBus;

    @Test
    public void testHealthyHostAlert() {
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        AgentHeartbeatAlertRunnable runnable = new AgentHeartbeatAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        Assert.assertEquals(1, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        List<AlertEvent> events = m_listener.getAlertEventInstances(AlertReceivedEvent.class);
        Assert.assertEquals(1, events.size());
        AlertReceivedEvent event = ((AlertReceivedEvent) (events.get(0)));
        Alert alert = event.getAlert();
        Assert.assertEquals("AMBARI", alert.getService());
        Assert.assertEquals("AMBARI_SERVER", alert.getComponent());
        Assert.assertEquals(OK, alert.getState());
        Assert.assertEquals(AgentHeartbeatAlertRunnableTest.DEFINITION_NAME, alert.getName());
        verify(m_definition, m_host, m_cluster, m_clusters, m_definitionDao);
    }

    @Test
    public void testLostHeartbeatAlert() {
        EasyMock.reset(m_host);
        expect(m_host.getState()).andReturn(HEARTBEAT_LOST).atLeastOnce();
        replay(m_host);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        AgentHeartbeatAlertRunnable runnable = new AgentHeartbeatAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        Assert.assertEquals(1, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        List<AlertEvent> events = m_listener.getAlertEventInstances(AlertReceivedEvent.class);
        Assert.assertEquals(1, events.size());
        AlertReceivedEvent event = ((AlertReceivedEvent) (events.get(0)));
        Alert alert = event.getAlert();
        Assert.assertEquals("AMBARI", alert.getService());
        Assert.assertEquals("AMBARI_SERVER", alert.getComponent());
        Assert.assertEquals(CRITICAL, alert.getState());
        Assert.assertEquals(AgentHeartbeatAlertRunnableTest.DEFINITION_NAME, alert.getName());
        verify(m_definition, m_host, m_cluster, m_clusters, m_definitionDao);
    }

    @Test
    public void testUnhealthyHostAlert() {
        EasyMock.reset(m_host);
        expect(m_host.getState()).andReturn(UNHEALTHY).atLeastOnce();
        replay(m_host);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        AgentHeartbeatAlertRunnable runnable = new AgentHeartbeatAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        Assert.assertEquals(1, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        List<AlertEvent> events = m_listener.getAlertEventInstances(AlertReceivedEvent.class);
        Assert.assertEquals(1, events.size());
        AlertReceivedEvent event = ((AlertReceivedEvent) (events.get(0)));
        Alert alert = event.getAlert();
        Assert.assertEquals("AMBARI", alert.getService());
        Assert.assertEquals("AMBARI_SERVER", alert.getComponent());
        Assert.assertEquals(CRITICAL, alert.getState());
        Assert.assertEquals(AgentHeartbeatAlertRunnableTest.DEFINITION_NAME, alert.getName());
        verify(m_definition, m_host, m_cluster, m_clusters, m_definitionDao);
    }

    /**
     *
     */
    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            PartialNiceMockBinder.newBuilder().addConfigsBindings().addAlertDefinitionBinding().addLdapBindings().build().configure(binder);
        }
    }
}

