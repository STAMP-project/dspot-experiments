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


import AlertState.OK;
import AlertState.SKIPPED;
import AlertState.WARNING;
import Direction.UPGRADE;
import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.events.AlertEvent;
import org.apache.ambari.server.events.AlertReceivedEvent;
import org.apache.ambari.server.events.MockEventListener;
import org.apache.ambari.server.events.publishers.AlertEventPublisher;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.state.Alert;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.apache.ambari.server.testutils.PartialNiceMockBinder.newBuilder;


/**
 * Tests {@link ComponentVersionAlertRunnable}.
 */
public class ComponentVersionAlertRunnableTest extends EasyMockSupport {
    private static final long CLUSTER_ID = 1;

    private static final String CLUSTER_NAME = "c1";

    private static final String HOSTNAME_1 = "c6401.ambari.apache.org";

    private static final String HOSTNAME_2 = "c6402.ambari.apache.org";

    private static final String EXPECTED_VERSION = "2.6.0.0-1234";

    private static final String WRONG_VERSION = "9.9.9.9-9999";

    private static final String DEFINITION_NAME = "ambari_server_component_version";

    private static final String DEFINITION_SERVICE = "AMBARI";

    private static final String DEFINITION_COMPONENT = "AMBARI_SERVER";

    private static final String DEFINITION_LABEL = "Mock Definition";

    private Clusters m_clusters;

    private Cluster m_cluster;

    private Injector m_injector;

    private AlertDefinitionDAO m_definitionDao;

    private AlertDefinitionEntity m_definition;

    private MockEventListener m_listener;

    private AmbariMetaInfo m_metaInfo;

    private AlertEventPublisher m_eventPublisher;

    private EventBus m_synchronizedBus;

    private Collection<Host> m_hosts;

    private Map<String, List<ServiceComponentHost>> m_hostComponentMap = new HashMap<>();

    private StackId m_desidredStackId;

    /**
     * Tests that the alert is SKIPPED when there is an upgrade in progress.
     */
    @Test
    public void testUpgradeInProgress() throws Exception {
        UpgradeEntity upgrade = createNiceMock(UpgradeEntity.class);
        expect(upgrade.getDirection()).andReturn(UPGRADE).atLeastOnce();
        expect(m_cluster.getUpgradeInProgress()).andReturn(upgrade).once();
        replayAll();
        m_metaInfo.init();
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        ComponentVersionAlertRunnable runnable = new ComponentVersionAlertRunnable(m_definition.getDefinitionName());
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
        Assert.assertEquals(SKIPPED, alert.getState());
        Assert.assertEquals(ComponentVersionAlertRunnableTest.DEFINITION_NAME, alert.getName());
    }

    /**
     * Tests the alert that fires when all components are reporting correct
     * versions.
     */
    @Test
    public void testAllComponentVersionsCorrect() throws Exception {
        replayAll();
        m_metaInfo.init();
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        ComponentVersionAlertRunnable runnable = new ComponentVersionAlertRunnable(m_definition.getDefinitionName());
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
        Assert.assertEquals(ComponentVersionAlertRunnableTest.DEFINITION_NAME, alert.getName());
        verifyAll();
    }

    /**
     * Tests that the alert which fires when there is a mismatch is a WARNING.
     */
    @Test
    public void testomponentVersionMismatch() throws Exception {
        // reset expectation so that it returns a wrong version
        ServiceComponentHost sch = m_hostComponentMap.get(ComponentVersionAlertRunnableTest.HOSTNAME_1).get(0);
        EasyMock.reset(sch);
        expect(sch.getServiceName()).andReturn("FOO").atLeastOnce();
        expect(sch.getServiceComponentName()).andReturn("FOO_COMPONENT").atLeastOnce();
        expect(sch.getVersion()).andReturn(ComponentVersionAlertRunnableTest.WRONG_VERSION).atLeastOnce();
        replayAll();
        m_metaInfo.init();
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        ComponentVersionAlertRunnable runnable = new ComponentVersionAlertRunnable(m_definition.getDefinitionName());
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
        Assert.assertEquals(WARNING, alert.getState());
        Assert.assertEquals(ComponentVersionAlertRunnableTest.DEFINITION_NAME, alert.getName());
        verifyAll();
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            Cluster cluster = createNiceMock(Cluster.class);
            newBuilder(ComponentVersionAlertRunnableTest.this).addConfigsBindings().addDBAccessorBinding().addFactoriesInstallBinding().addAmbariMetaInfoBinding().addLdapBindings().build().configure(binder);
            binder.bind(AmbariMetaInfo.class).toInstance(createNiceMock(AmbariMetaInfo.class));
            binder.bind(Cluster.class).toInstance(cluster);
            binder.bind(AlertDefinitionDAO.class).toInstance(createNiceMock(AlertDefinitionDAO.class));
        }
    }
}

