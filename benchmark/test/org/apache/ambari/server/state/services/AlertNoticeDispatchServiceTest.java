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
package org.apache.ambari.server.state.services;


import NotificationState.DISPATCHED;
import TargetType.AMBARI_SNMP;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executor;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.notifications.DispatchFactory;
import org.apache.ambari.server.notifications.Notification;
import org.apache.ambari.server.notifications.NotificationDispatcher;
import org.apache.ambari.server.notifications.TargetConfigurationResult;
import org.apache.ambari.server.notifications.dispatchers.AmbariSNMPDispatcher;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.dao.AlertDispatchDAO;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.orm.entities.AlertHistoryEntity;
import org.apache.ambari.server.orm.entities.AlertNoticeEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.testutils.PartialNiceMockBinder;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.transport.DefaultUdpTransportMapping;


/**
 * Tests the {@link AlertNoticeDispatchService}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmbariSNMPDispatcher.class, ManagementFactory.class })
public class AlertNoticeDispatchServiceTest extends AlertNoticeDispatchService {
    static final String ALERT_NOTICE_UUID_1 = UUID.randomUUID().toString();

    static final String ALERT_NOTICE_UUID_2 = UUID.randomUUID().toString();

    static final String ALERT_UNIQUE_TEXT = "0eeda438-2b13-4869-a416-137e35ff76e9";

    static final String HOSTNAME = "c6401.ambari.apache.org";

    static final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private AmbariMetaInfo m_metaInfo = null;

    private DispatchFactory m_dispatchFactory = null;

    private AlertDispatchDAO m_dao = null;

    private Injector m_injector;

    private RuntimeMXBean m_runtimeMXBean;

    List<AlertDefinitionEntity> m_definitions = new ArrayList<>();

    List<AlertHistoryEntity> m_histories = new ArrayList<>();

    /**
     * Tests the parsing of the {@link AlertHistoryEntity} list into
     * {@link AlertSummaryInfo}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlertInfo() throws Exception {
        AlertHistoryEntity history = m_histories.get(0);
        AlertInfo alertInfo = new AlertInfo(history);
        Assert.assertEquals(history.getAlertDefinition().getLabel(), alertInfo.getAlertName());
        Assert.assertEquals(history.getAlertState(), alertInfo.getAlertState());
        Assert.assertEquals(history.getAlertText(), alertInfo.getAlertText());
        Assert.assertEquals(history.getComponentName(), alertInfo.getComponentName());
        Assert.assertEquals(history.getHostName(), alertInfo.getHostName());
        Assert.assertEquals(history.getServiceName(), alertInfo.getServiceName());
        Assert.assertEquals(false, alertInfo.hasComponentName());
        Assert.assertEquals(true, alertInfo.hasHostName());
    }

    /**
     * Tests the parsing of the {@link AlertHistoryEntity} list into
     * {@link AlertSummaryInfo}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlertSummaryInfo() throws Exception {
        AlertSummaryInfo alertInfo = new AlertSummaryInfo(m_histories);
        Assert.assertEquals(50, alertInfo.getAlerts().size());
        Assert.assertEquals(10, alertInfo.getAlerts("Service 1").size());
        Assert.assertEquals(10, alertInfo.getAlerts("Service 2").size());
        Assert.assertEquals(8, alertInfo.getAlerts("Service 1", "OK").size());
        Assert.assertEquals(2, alertInfo.getAlerts("Service 1", "CRITICAL").size());
        Assert.assertNull(alertInfo.getAlerts("Service 1", "WARNING"));
        Assert.assertNull(alertInfo.getAlerts("Service 1", "UNKNOWN"));
        Assert.assertEquals(5, alertInfo.getServices().size());
    }

    /**
     * Tests that the dispatcher is not called when there are no notices.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoDispatch() throws Exception {
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(new ArrayList()).once();
        // m_dispatchFactory should not be called at all
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
    }

    /**
     * Tests a digest dispatch for email.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDigestDispatch() throws Exception {
        AlertNoticeDispatchServiceTest.MockEmailDispatcher dispatcher = new AlertNoticeDispatchServiceTest.MockEmailDispatcher();
        List<AlertNoticeEntity> notices = getSingleMockNotice(dispatcher.getType());
        AlertNoticeEntity notice = notices.get(0);
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher("EMAIL")).andReturn(dispatcher).once();
        EasyMock.expect(m_dao.merge(notice)).andReturn(notice).atLeastOnce();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
        Notification notification = dispatcher.getNotification();
        Assert.assertNotNull(notification);
        Assert.assertTrue(notification.Subject.contains("OK[1]"));
        Assert.assertTrue(notification.Subject.contains("Critical[0]"));
        Assert.assertTrue(notification.Body.contains(AlertNoticeDispatchServiceTest.ALERT_UNIQUE_TEXT));
    }

    @Test
    public void testExceptionHandling() throws Exception {
        List<AlertNoticeEntity> notices = getSingleMockNotice("EMAIL");
        AlertNoticeEntity notice = notices.get(0);
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher("EMAIL")).andReturn(null).once();
        EasyMock.expect(m_dao.merge(notice)).andReturn(notice).atLeastOnce();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        // no exceptions should be thrown
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
    }

    /**
     * Tests a digest dispatch for SNMP.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleSnmpDispatch() throws Exception {
        AlertNoticeDispatchServiceTest.MockSnmpDispatcher dispatcher = new AlertNoticeDispatchServiceTest.MockSnmpDispatcher();
        List<AlertNoticeEntity> notices = getSnmpMockNotices("SNMP");
        AlertNoticeEntity notice1 = notices.get(0);
        AlertNoticeEntity notice2 = notices.get(1);
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dao.merge(notice1)).andReturn(notice1).once();
        EasyMock.expect(m_dao.merge(notice2)).andReturn(notice2).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher("SNMP")).andReturn(dispatcher).atLeastOnce();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
        List<Notification> notifications = dispatcher.getNotifications();
        Assert.assertEquals(2, notifications.size());
    }

    /**
     * Tests a digest dispatch for Ambari SNMP.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAmbariSnmpSingleDispatch() throws Exception {
        AlertNoticeDispatchServiceTest.MockAmbariSnmpDispatcher dispatcher = new AlertNoticeDispatchServiceTest.MockAmbariSnmpDispatcher();
        List<AlertNoticeEntity> notices = getSnmpMockNotices("AMBARI_SNMP");
        AlertNoticeEntity notice1 = notices.get(0);
        AlertNoticeEntity notice2 = notices.get(1);
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dao.merge(notice1)).andReturn(notice1).once();
        EasyMock.expect(m_dao.merge(notice2)).andReturn(notice2).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher("AMBARI_SNMP")).andReturn(dispatcher).atLeastOnce();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
        List<Notification> notifications = dispatcher.getNotifications();
        Assert.assertEquals(2, notifications.size());
    }

    /**
     * Tests a real dispatch for Ambari SNMP.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAmbariSnmpRealDispatch() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(8081);
        List<AlertNoticeEntity> notices = getSnmpMockNotices("AMBARI_SNMP");
        AlertNoticeEntity notice1 = notices.get(0);
        AlertNoticeEntity notice2 = notices.get(1);
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dao.merge(notice1)).andReturn(notice1).once();
        EasyMock.expect(m_dao.merge(notice2)).andReturn(notice2).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher("AMBARI_SNMP")).andReturn(dispatcher).once();
        EasyMock.expect(m_dao.findNoticeByUuid(AlertNoticeDispatchServiceTest.ALERT_NOTICE_UUID_1)).andReturn(notice1).once();
        EasyMock.expect(m_dao.merge(notice1)).andReturn(notice1).once();
        EasyMock.expect(m_dao.findNoticeByUuid(AlertNoticeDispatchServiceTest.ALERT_NOTICE_UUID_2)).andReturn(notice2).once();
        EasyMock.expect(m_dao.merge(notice2)).andReturn(notice2).once();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // "startup" the service so that its initialization is done
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        AlertNoticeDispatchServiceTest.SnmpReceiver snmpReceiver = new AlertNoticeDispatchServiceTest.SnmpReceiver();
        service.runOneIteration();
        Thread.sleep(1000);
        EasyMock.verify(m_dao, m_dispatchFactory);
        List<Vector> expectedTrapVectors = new LinkedList<>();
        Vector firstVector = new Vector();
        firstVector.add(new org.snmp4j.smi.VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(360000L)));
        firstVector.add(new org.snmp4j.smi.VariableBinding(SnmpConstants.snmpTrapOID, new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_TRAP_OID)));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_DEFINITION_ID_OID), new Integer32(new BigDecimal(1L).intValueExact())));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_DEFINITION_NAME_OID), new OctetString("alert-definition-1")));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_DEFINITION_HASH_OID), new OctetString("1")));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_NAME_OID), new OctetString("Alert Definition 1")));
        Vector secondVector = new Vector(firstVector);
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_TEXT_OID), new OctetString(AlertNoticeDispatchServiceTest.ALERT_UNIQUE_TEXT)));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_STATE_OID), new Integer32(0)));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_HOST_NAME_OID), new OctetString("null")));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_SERVICE_NAME_OID), new OctetString("HDFS")));
        firstVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_COMPONENT_NAME_OID), new OctetString("null")));
        secondVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_TEXT_OID), new OctetString(((AlertNoticeDispatchServiceTest.ALERT_UNIQUE_TEXT) + " CRITICAL"))));
        secondVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_STATE_OID), new Integer32(3)));
        secondVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_HOST_NAME_OID), new OctetString("null")));
        secondVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_SERVICE_NAME_OID), new OctetString("HDFS")));
        secondVector.add(new org.snmp4j.smi.VariableBinding(new org.snmp4j.smi.OID(AmbariSNMPDispatcher.AMBARI_ALERT_COMPONENT_NAME_OID), new OctetString("null")));
        expectedTrapVectors.add(firstVector);
        expectedTrapVectors.add(secondVector);
        Assert.assertNotNull(snmpReceiver.receivedTrapsVectors);
        Assert.assertTrue(((snmpReceiver.receivedTrapsVectors.size()) == 2));
        Assert.assertEquals(expectedTrapVectors, snmpReceiver.receivedTrapsVectors);
    }

    /**
     * Tests that a failed dispatch invokes the callback to mark the UUIDs of the
     * notices as FAILED.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailedDispatch() throws Exception {
        AlertNoticeDispatchServiceTest.MockEmailDispatcher dispatcher = new AlertNoticeDispatchServiceTest.MockEmailDispatcher();
        List<AlertNoticeEntity> notices = getSingleMockNotice(dispatcher.getType());
        AlertNoticeEntity notice = notices.get(0);
        // these expectations happen b/c we need to mark the notice as FAILED
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dao.merge(notice)).andReturn(notice).once();
        EasyMock.expect(m_dao.findNoticeByUuid(AlertNoticeDispatchServiceTest.ALERT_NOTICE_UUID_1)).andReturn(notice).once();
        EasyMock.expect(m_dao.merge(notice)).andReturn(notice).once();
        EasyMock.expect(m_dispatchFactory.getDispatcher(dispatcher.getType())).andReturn(dispatcher).once();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // do NOT startup the service which will force a template NPE
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
        Notification notification = dispatcher.getNotification();
        Assert.assertNull(notification);
    }

    /**
     * Tests that when a dispatcher doesn't call back, the
     * {@link AlertNoticeEntity} will be put from
     * {@link NotificationState#PENDING} to {@link NotificationState#DISPATCHED}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDispatcherWithoutCallbacks() throws Exception {
        AlertNoticeDispatchServiceTest.MockNoCallbackDispatcher dispatcher = new AlertNoticeDispatchServiceTest.MockNoCallbackDispatcher();
        List<AlertNoticeEntity> notices = getSingleMockNotice(dispatcher.getType());
        AlertNoticeEntity notice = notices.get(0);
        // these expectations happen b/c we need to mark the notice as FAILED
        EasyMock.expect(m_dao.findPendingNotices()).andReturn(notices).once();
        EasyMock.expect(m_dao.merge(notice)).andReturn(notice).atLeastOnce();
        EasyMock.expect(m_dispatchFactory.getDispatcher(dispatcher.getType())).andReturn(dispatcher).once();
        EasyMock.replay(m_dao, m_dispatchFactory);
        // do NOT startup the service which will force a template NPE
        AlertNoticeDispatchService service = m_injector.getInstance(AlertNoticeDispatchService.class);
        service.startUp();
        // service trigger with mock executor that blocks
        service.setExecutor(new AlertNoticeDispatchServiceTest.MockExecutor());
        service.runOneIteration();
        EasyMock.verify(m_dao, m_dispatchFactory);
        Notification notification = dispatcher.getNotification();
        Assert.assertNotNull(notification);
        // the most important part of this test; ensure that notices that are
        // processed but have no callbacks are in the DISPATCHED state
        Assert.assertEquals(DISPATCHED, notice.getNotifyState());
    }

    /**
     * A mock dispatcher that captures the {@link Notification}.
     */
    private static final class MockEmailDispatcher implements NotificationDispatcher {
        private Notification m_notificaiton;

        /**
         * {@inheritDoc }
         */
        @Override
        public String getType() {
            return "EMAIL";
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isDigestSupported() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isNotificationContentGenerationRequired() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void dispatch(Notification notification) {
            m_notificaiton = notification;
        }

        @Override
        public TargetConfigurationResult validateTargetConfig(Map<String, Object> properties) {
            return null;
        }

        public Notification getNotification() {
            return m_notificaiton;
        }
    }

    /**
     * A mock dispatcher that captures the {@link Notification}.
     */
    private static class MockSnmpDispatcher implements NotificationDispatcher {
        private List<Notification> m_notifications = new ArrayList<>();

        /**
         * {@inheritDoc }
         */
        @Override
        public String getType() {
            return "SNMP";
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isNotificationContentGenerationRequired() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isDigestSupported() {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void dispatch(Notification notification) {
            m_notifications.add(notification);
        }

        public List<Notification> getNotifications() {
            return m_notifications;
        }

        @Override
        public TargetConfigurationResult validateTargetConfig(Map<String, Object> properties) {
            return null;
        }
    }

    private static final class MockAmbariSnmpDispatcher extends AlertNoticeDispatchServiceTest.MockSnmpDispatcher {
        @Override
        public String getType() {
            return AMBARI_SNMP.name();
        }
    }

    /**
     * A mock dispatcher that captures the {@link Notification}.
     */
    private static final class MockNoCallbackDispatcher implements NotificationDispatcher {
        private Notification m_notificaiton;

        /**
         * {@inheritDoc }
         */
        @Override
        public String getType() {
            return "NO_CALLBACK";
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isNotificationContentGenerationRequired() {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isDigestSupported() {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void dispatch(Notification notification) {
            m_notificaiton = notification;
        }

        @Override
        public TargetConfigurationResult validateTargetConfig(Map<String, Object> properties) {
            return null;
        }

        public Notification getNotification() {
            return m_notificaiton;
        }
    }

    /**
     * An {@link Executor} that calls {@link Runnable#run()} directly in the
     * current thread.
     */
    private static final class MockExecutor implements Executor {
        /**
         * {@inheritDoc }
         */
        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }
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
            Cluster cluster = EasyMock.createNiceMock(Cluster.class);
            PartialNiceMockBinder.newBuilder().addDBAccessorBinding().addAmbariMetaInfoBinding().addLdapBindings().build().configure(binder);
            binder.bind(AlertDispatchDAO.class).toInstance(m_dao);
            binder.bind(DispatchFactory.class).toInstance(m_dispatchFactory);
            binder.bind(AmbariMetaInfo.class).toInstance(m_metaInfo);
            binder.bind(Cluster.class).toInstance(cluster);
            binder.bind(AlertDefinitionDAO.class).toInstance(createNiceMock(AlertDefinitionDAO.class));
            binder.bind(AlertsDAO.class).toInstance(createNiceMock(AlertsDAO.class));
            binder.bind(AlertNoticeDispatchService.class).toInstance(new AlertNoticeDispatchService());
            EasyMock.expect(m_metaInfo.getServerVersion()).andReturn("2.0.0").anyTimes();
            EasyMock.replay(m_metaInfo);
        }
    }

    private class SnmpReceiver {
        private Snmp snmp = null;

        private Address targetAddress = GenericAddress.parse("udp:127.0.0.1/8000");

        private TransportMapping transport = null;

        public List<Vector> receivedTrapsVectors = null;

        public SnmpReceiver() throws Exception {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            receivedTrapsVectors = new LinkedList<>();
            CommandResponder trapPrinter = new CommandResponder() {
                @Override
                public synchronized void processPdu(CommandResponderEvent e) {
                    PDU command = e.getPDU();
                    if (command != null) {
                        receivedTrapsVectors.add(command.getVariableBindings());
                    }
                }
            };
            snmp.addNotificationListener(targetAddress, trapPrinter);
        }
    }
}

