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
import HostState.HEALTHY;
import HostState.HEARTBEAT_LOST;
import MaintenanceState.OFF;
import MaintenanceState.ON;
import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.agent.stomp.AlertDefinitionsHolder;
import org.apache.ambari.server.agent.stomp.dto.AlertCluster;
import org.apache.ambari.server.events.AlertDefinitionEventType;
import org.apache.ambari.server.events.AlertDefinitionsAgentUpdateEvent;
import org.apache.ambari.server.events.AlertReceivedEvent;
import org.apache.ambari.server.events.MockEventListener;
import org.apache.ambari.server.events.publishers.AlertEventPublisher;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.entities.AlertCurrentEntity;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.AlertHistoryEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.alert.AlertDefinition;
import org.apache.ambari.server.state.alert.AlertHelper;
import org.apache.ambari.server.testutils.PartialNiceMockBinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link StaleAlertRunnableTest}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ StaleAlertRunnable.class, ManagementFactory.class })
public class StaleAlertRunnableTest {
    private static final long CLUSTER_ID = 1;

    private static final String CLUSTER_NAME = "c1";

    private static final String DEFINITION_NAME = "ambari_server_stale_alerts";

    private static final String DEFINITION_SERVICE = "AMBARI";

    private static final String DEFINITION_COMPONENT = "AMBARI_SERVER";

    private static final String DEFINITION_LABEL = "Mock Definition";

    private static final int DEFINITION_INTERVAL = 1;

    private Clusters m_clusters;

    private Cluster m_cluster;

    private Injector m_injector;

    private AlertsDAO m_alertsDao;

    private AlertDefinitionDAO m_definitionDao;

    private AlertDefinitionEntity m_definition;

    private List<AlertCurrentEntity> m_currentAlerts = new ArrayList<>();

    private MockEventListener m_listener;

    private AlertHelper m_alertHelper;

    private Host m_host;

    private AlertEventPublisher m_eventPublisher;

    private EventBus m_synchronizedBus;

    private RuntimeMXBean m_runtimeMXBean;

    @Test
    public void testPrepareHostDefinitions() {
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        AlertDefinitionsHolder alertDefinitionHolder = m_injector.getInstance(AlertDefinitionsHolder.class);
        Long alertDefinitionId1 = 1L;
        Long alertDefinitionId2 = 2L;
        Long alertDefinitionId3 = 3L;
        Long alertDefinitionId4 = 4L;
        Long hostId1 = 1L;
        Long hostId2 = 2L;
        /* host1:
          cluster1
            alertDefinition1
          cluster2
            alertDefinition2

        host2:
          cluster1
            alertDefinition1
            alertDefinition3
          cluster2
            alertDefinition4
         */
        AlertDefinition alertDefinition1 = new AlertDefinition();
        alertDefinition1.setDefinitionId(alertDefinitionId1);
        AlertDefinition alertDefinition2 = new AlertDefinition();
        alertDefinition2.setDefinitionId(alertDefinitionId2);
        AlertDefinition alertDefinition3 = new AlertDefinition();
        alertDefinition3.setDefinitionId(alertDefinitionId3);
        AlertDefinition alertDefinition4 = new AlertDefinition();
        alertDefinition4.setDefinitionId(alertDefinitionId4);
        AlertCluster alertCluster1host1 = new AlertCluster(Collections.singletonMap(alertDefinitionId1, alertDefinition1), "host1");
        AlertCluster alertCluster2host1 = new AlertCluster(Collections.singletonMap(alertDefinitionId2, alertDefinition2), "host1");
        AlertCluster alertCluster1host2 = new AlertCluster(new HashMap() {
            {
                put(alertDefinitionId3, alertDefinition3);
                put(alertDefinitionId1, alertDefinition1);
            }
        }, "host2");
        AlertCluster alertCluster2host2 = new AlertCluster(Collections.singletonMap(alertDefinitionId4, alertDefinition4), "host2");
        AlertDefinitionsAgentUpdateEvent hostUpdate1 = new AlertDefinitionsAgentUpdateEvent(AlertDefinitionEventType.CREATE, new HashMap() {
            {
                put(1L, alertCluster1host1);
                put(2L, alertCluster2host1);
            }
        }, "host1", hostId1);
        AlertDefinitionsAgentUpdateEvent hostUpdate2 = new AlertDefinitionsAgentUpdateEvent(AlertDefinitionEventType.CREATE, new HashMap() {
            {
                put(1L, alertCluster1host2);
                put(2L, alertCluster2host2);
            }
        }, "host2", hostId2);
        alertDefinitionHolder.setData(hostUpdate1, 1L);
        alertDefinitionHolder.setData(hostUpdate2, 2L);
        m_injector.injectMembers(runnable);
        Map<Long, List<Long>> alertDefinitionsToHost = runnable.prepareHostDefinitions(hostId1);
        Assert.assertEquals(2, alertDefinitionsToHost.size());
        Assert.assertNotNull(alertDefinitionsToHost.get(alertDefinitionId1));
        Assert.assertEquals(2, alertDefinitionsToHost.get(alertDefinitionId1).size());
        Assert.assertTrue(alertDefinitionsToHost.get(alertDefinitionId1).contains(hostId1));
        Assert.assertTrue(alertDefinitionsToHost.get(alertDefinitionId1).contains(hostId2));
        Assert.assertNotNull(alertDefinitionsToHost.get(alertDefinitionId3));
        Assert.assertEquals(1, alertDefinitionsToHost.get(alertDefinitionId3).size());
        Assert.assertEquals(Long.valueOf(hostId2), alertDefinitionsToHost.get(alertDefinitionId3).get(0));
        alertDefinitionsToHost = runnable.prepareHostDefinitions(hostId2);
        Assert.assertEquals(2, alertDefinitionsToHost.size());
        Assert.assertNotNull(alertDefinitionsToHost.get(alertDefinitionId2));
        Assert.assertEquals(1, alertDefinitionsToHost.get(alertDefinitionId2).size());
        Assert.assertEquals(Long.valueOf(hostId1), alertDefinitionsToHost.get(alertDefinitionId2).get(0));
        Assert.assertNotNull(alertDefinitionsToHost.get(alertDefinitionId4));
        Assert.assertEquals(1, alertDefinitionsToHost.get(alertDefinitionId4).size());
        Assert.assertEquals(Long.valueOf(hostId2), alertDefinitionsToHost.get(alertDefinitionId4).get(0));
    }

    /**
     * Tests that the event is triggerd with a status of OK.
     */
    @Test
    public void testAllAlertsAreCurrent() {
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(1L).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        replay(current1, history1);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(OK);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * Tests that a stale alert triggers the event with a status of CRITICAL.
     */
    @Test
    public void testAmbariStaleAlert() {
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts that are stale
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(1L).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        // a really old timestampt to trigger the alert
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(1L).atLeastOnce();
        replay(current1, history1);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(CRITICAL);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * Tests that a stale message from agent triggers the event with a status of CRITICAL.
     */
    @Test
    public void testStaleAlertFromAgent() {
        Long alertDefinitionId = 1L;
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts that are stale
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(alertDefinitionId).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(history1.getHostName()).andReturn("host1").atLeastOnce();
        reset(m_alertHelper);
        expect(m_alertHelper.getWaitFactorMultiplier(anyObject(AlertDefinition.class))).andReturn(2).anyTimes();
        expect(m_alertHelper.getStaleAlerts(anyLong())).andReturn(Collections.singletonMap(alertDefinitionId, 0L)).atLeastOnce();
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        replay(current1, history1, m_alertHelper);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(CRITICAL);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * <AlertCurrentEntity> currentAlerts = m_alertsDao.findCurrentByCluster(
     * cluster.getClusterId());
     *
     * long now = System.currentTimeMillis();
     *
     * Map<Long, List<Long>> alertDefinitionsToHosts = prepareHostDefinitions(cluster.getClusterId());
     * Tests that a heartbeat loose triggers the event with a status of CRITICAL.
     */
    @Test
    public void testStaleAlertHeartbeatLost() {
        Long alertDefinitionId = 1L;
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts that are stale
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(alertDefinitionId).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(history1.getHostName()).andReturn("host1").atLeastOnce();
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        reset(m_cluster, m_host);
        m_host = createNiceMock(Host.class);
        expect(m_host.getHostId()).andReturn(1L);
        expect(m_host.getState()).andReturn(HEARTBEAT_LOST);
        expect(m_cluster.getClusterId()).andReturn(StaleAlertRunnableTest.CLUSTER_ID).atLeastOnce();
        expect(m_cluster.getHost(anyString())).andReturn(m_host).anyTimes();
        replay(current1, history1, m_host, m_cluster);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(CRITICAL);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * Tests alerts with ignoreHost == true. One host is in HEARTBEAT_LOST state.
     * host1:
     *   cluster1
     *     alertDefinition1
     *
     * host2:
     *   cluster1
     *     alertDefinition1
     */
    @Test
    public void testStaleAlertWithHostIgnore() {
        Long alertDefinitionId = 1L;
        prepareAlertHolderWithHostAlert(alertDefinitionId);
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts that are stale
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(alertDefinitionId).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        reset(m_cluster);
        Host host1 = createNiceMock(Host.class);
        Host host2 = createNiceMock(Host.class);
        expect(host1.getHostId()).andReturn(1L);
        expect(host1.getState()).andReturn(HEARTBEAT_LOST).atLeastOnce();
        expect(host1.getLastHeartbeatTime()).andReturn(1L);
        expect(host2.getHostId()).andReturn(2L);
        expect(host2.getState()).andReturn(HEALTHY).atLeastOnce();
        expect(host2.getLastHeartbeatTime()).andReturn(2L);
        expect(m_cluster.getClusterId()).andReturn(StaleAlertRunnableTest.CLUSTER_ID).atLeastOnce();
        expect(m_cluster.getHost(eq(1L))).andReturn(host1).anyTimes();
        expect(m_cluster.getHost(eq(2L))).andReturn(host2).anyTimes();
        replay(current1, history1, host1, host2, m_cluster);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(OK);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * Tests alerts with ignoreHost == true. Both hosts are in HEARTBEAT_LOST state.
     * host1:
     *   cluster1
     *     alertDefinition1
     *
     * host2:
     *   cluster1
     *     alertDefinition1
     */
    @Test
    public void testStaleAlertWithHostIgnoreCritical() {
        Long alertDefinitionId = 1L;
        prepareAlertHolderWithHostAlert(alertDefinitionId);
        // create current alerts that are not stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts that are stale
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(current1.getDefinitionId()).andReturn(alertDefinitionId).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(current1.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        reset(m_cluster);
        Host host1 = createNiceMock(Host.class);
        Host host2 = createNiceMock(Host.class);
        expect(host1.getHostId()).andReturn(1L);
        expect(host1.getState()).andReturn(HEARTBEAT_LOST).atLeastOnce();
        expect(host1.getLastHeartbeatTime()).andReturn(1L);
        expect(host2.getHostId()).andReturn(2L);
        expect(host2.getState()).andReturn(HEARTBEAT_LOST).atLeastOnce();
        expect(host2.getLastHeartbeatTime()).andReturn(2L);
        expect(m_cluster.getClusterId()).andReturn(StaleAlertRunnableTest.CLUSTER_ID).atLeastOnce();
        expect(m_cluster.getHost(eq(1L))).andReturn(host1).anyTimes();
        expect(m_cluster.getHost(eq(2L))).andReturn(host2).anyTimes();
        replay(current1, history1, host1, host2, m_cluster);
        m_currentAlerts.add(current1);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(CRITICAL);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
    }

    /**
     * Tests that a stale alert in maintenance mode doesn't trigger the event.
     */
    @Test
    public void testStaleAlertInMaintenaceMode() {
        // create current alerts that are stale
        AlertDefinitionEntity definition = new AlertDefinitionEntity();
        definition.setClusterId(StaleAlertRunnableTest.CLUSTER_ID);
        definition.setDefinitionName("foo-definition");
        definition.setServiceName("HDFS");
        definition.setComponentName("NAMENODE");
        definition.setEnabled(true);
        definition.setScheduleInterval(1);
        // create current alerts where 1 is stale but in maintence mode
        AlertCurrentEntity current1 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history1 = createNiceMock(AlertHistoryEntity.class);
        AlertCurrentEntity current2 = createNiceMock(AlertCurrentEntity.class);
        AlertHistoryEntity history2 = createNiceMock(AlertHistoryEntity.class);
        expect(current1.getAlertHistory()).andReturn(history1).atLeastOnce();
        expect(history1.getAlertDefinition()).andReturn(definition).atLeastOnce();
        expect(current2.getAlertHistory()).andReturn(history2).atLeastOnce();
        expect(history2.getAlertDefinition()).andReturn(definition).atLeastOnce();
        // maintenance mode with a really old timestamp
        expect(current1.getMaintenanceState()).andReturn(ON).atLeastOnce();
        expect(current1.getLatestTimestamp()).andReturn(1L).atLeastOnce();
        // an that that is not stale
        expect(current2.getMaintenanceState()).andReturn(OFF).atLeastOnce();
        expect(current2.getLatestTimestamp()).andReturn(System.currentTimeMillis()).atLeastOnce();
        replay(current1, history1, current2, history2);
        m_currentAlerts.add(current1);
        m_currentAlerts.add(current2);
        // precondition that no events were fired
        Assert.assertEquals(0, m_listener.getAlertEventReceivedCount(AlertReceivedEvent.class));
        // instantiate and inject mocks
        StaleAlertRunnable runnable = new StaleAlertRunnable(m_definition.getDefinitionName());
        m_injector.injectMembers(runnable);
        // run the alert
        runnable.run();
        checkSingleEventToState(OK);
        verify(m_cluster, m_clusters, m_definitionDao, m_alertHelper);
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
            PartialNiceMockBinder.newBuilder().addConfigsBindings().addAlertDefinitionBinding().addLdapBindings().build().configure(binder);
            binder.bind(AlertsDAO.class).toInstance(createNiceMock(AlertsDAO.class));
            binder.bind(HostRoleCommandDAO.class).toInstance(createNiceMock(HostRoleCommandDAO.class));
            binder.bind(AlertHelper.class).toInstance(createNiceMock(AlertHelper.class));
        }
    }
}

