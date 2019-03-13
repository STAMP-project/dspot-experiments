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
package org.apache.ambari.server.notifications.dispatchers;


import AmbariSNMPDispatcher.AMBARI_ALERT_COMPONENT_NAME_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_DEFINITION_ID_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_DEFINITION_NAME_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_HOST_NAME_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_NAME_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_SERVICE_NAME_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_STATE_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_TEXT_OID;
import AmbariSNMPDispatcher.AMBARI_ALERT_TRAP_OID;
import AmbariSNMPDispatcher.COMMUNITY_PROPERTY;
import AmbariSNMPDispatcher.InvalidSnmpConfigurationException;
import AmbariSNMPDispatcher.PORT_PROPERTY;
import AmbariSNMPDispatcher.SECURITY_AUTH_PASSPHRASE_PROPERTY;
import AmbariSNMPDispatcher.SECURITY_LEVEL_PROPERTY;
import AmbariSNMPDispatcher.SECURITY_PRIV_PASSPHRASE_PROPERTY;
import AmbariSNMPDispatcher.SECURITY_USERNAME_PROPERTY;
import AmbariSNMPDispatcher.SNMP_VERSION_PROPERTY;
import AmbariSNMPDispatcher.SnmpVersion;
import PDU.TRAP;
import PDU.V1TRAP;
import SnmpConstants.snmpTrapOID;
import SnmpConstants.version1;
import SnmpConstants.version2c;
import SnmpConstants.version3;
import TargetConfigurationResult.Status.INVALID;
import TargetConfigurationResult.Status.VALID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.notifications.DispatchCallback;
import org.apache.ambari.server.notifications.Notification;
import org.apache.ambari.server.notifications.NotificationDispatcher;
import org.apache.ambari.server.notifications.Recipient;
import org.apache.ambari.server.notifications.TargetConfigurationResult;
import org.apache.ambari.server.state.AlertState;
import org.apache.ambari.server.state.alert.AlertNotification;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;


public class AmbariSNMPDispatcherTest {
    private static final int DEFAULT_SNMP_PORT = 31444;

    public static final String DEFINITION_NAME = "definition name";

    public static final String ALERT_LABEL = "alert name";

    public static final String ALERT_TEXT = "alert text";

    public static final String ALERT_HOSTNAME = "hostname";

    public static final String ALERT_SERVICE_NAME = "service name";

    public static final String ALERT_COMPONENT_NAME = "component name";

    public static final Long DEFINITION_ID = 1L;

    public static final AlertState ALERT_STATE = AlertState.OK;

    @Test
    public void testDispatch_nullProperties() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatchUdpTransportMappingCrash() throws Exception {
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT));
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        notification.DispatchProperties = properties;
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doThrow(new IOException()).when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
        Assert.assertNull(dispatcher.getTransportMapping());
    }

    @Test
    public void testDispatch_notDefinedProperties() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        notification.DispatchProperties = new HashMap();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_nullRecipients() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = getAlertNotification(true);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        notification.DispatchProperties = properties;
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_noRecipients() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = getAlertNotification(true);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        notification.DispatchProperties = properties;
        notification.Recipients = new ArrayList();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_sendTrapError() throws Exception {
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT));
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        notification.DispatchProperties = properties;
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doThrow(new RuntimeException()).when(dispatcher).sendTraps(ArgumentMatchers.eq(notification), ArgumentMatchers.any(SnmpVersion.class));
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_incorrectSnmpVersion() throws Exception {
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT));
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv11");
        notification.DispatchProperties = properties;
        notification.Recipients = Arrays.asList(new Recipient());
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v1() throws Exception {
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT));
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        notification.DispatchProperties = properties;
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doNothing().when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback, Mockito.never()).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v2() throws Exception {
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT));
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        Notification notification = Mockito.mock(AlertNotification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        notification.DispatchProperties = properties;
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doNothing().when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback, Mockito.never()).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v3() throws Exception {
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = getAlertNotification(true);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = Mockito.mock(List.class);
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(COMMUNITY_PROPERTY, "public");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        notification.DispatchProperties = properties;
        Recipient recipient = new Recipient();
        recipient.Identifier = "192.168.0.2";
        notification.Recipients = Arrays.asList(recipient);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback, Mockito.never()).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testPrepareTrap_v1() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = getAlertNotification(true);
        PDU pdu = dispatcher.prepareTrap(notification, snmpVersion);
        Assert.assertEquals(V1TRAP, pdu.getType());
        Map<String, VariableBinding> variableBindings = new HashMap<>();
        for (VariableBinding variableBinding : pdu.toArray()) {
            variableBindings.put(variableBinding.getOid().toString(), variableBinding);
        }
        Assert.assertEquals(11, variableBindings.size());
        Assert.assertEquals(AMBARI_ALERT_TRAP_OID, variableBindings.get(snmpTrapOID.toString()).toValueString());
        Assert.assertTrue(((variableBindings.get(snmpTrapOID.toString()).getVariable()) instanceof OID));
        Assert.assertEquals(String.valueOf(AmbariSNMPDispatcherTest.DEFINITION_ID), variableBindings.get(AMBARI_ALERT_DEFINITION_ID_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_DEFINITION_ID_OID).getVariable()) instanceof Integer32));
        Assert.assertEquals(AmbariSNMPDispatcherTest.DEFINITION_NAME, variableBindings.get(AMBARI_ALERT_DEFINITION_NAME_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_DEFINITION_NAME_OID).getVariable()) instanceof OctetString));
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_LABEL, variableBindings.get(AMBARI_ALERT_NAME_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_NAME_OID).getVariable()) instanceof OctetString));
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_TEXT, variableBindings.get(AMBARI_ALERT_TEXT_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_TEXT_OID).getVariable()) instanceof OctetString));
        Assert.assertEquals(String.valueOf(AmbariSNMPDispatcherTest.ALERT_STATE.getIntValue()), variableBindings.get(AMBARI_ALERT_STATE_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_STATE_OID).getVariable()) instanceof Integer32));
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_HOSTNAME, variableBindings.get(AMBARI_ALERT_HOST_NAME_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_HOST_NAME_OID).getVariable()) instanceof OctetString));
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_SERVICE_NAME, variableBindings.get(AMBARI_ALERT_SERVICE_NAME_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_SERVICE_NAME_OID).getVariable()) instanceof OctetString));
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_COMPONENT_NAME, variableBindings.get(AMBARI_ALERT_COMPONENT_NAME_OID).toValueString());
        Assert.assertTrue(((variableBindings.get(AMBARI_ALERT_COMPONENT_NAME_OID).getVariable()) instanceof OctetString));
    }

    @Test
    public void testPrepareTrapNull() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        AlertNotification notification = ((AlertNotification) (getAlertNotification(false)));
        PDU pdu = dispatcher.prepareTrap(notification, snmpVersion);
        Assert.assertEquals(V1TRAP, pdu.getType());
        Map<String, VariableBinding> variableBindings = new HashMap<>();
        for (VariableBinding variableBinding : pdu.toArray()) {
            variableBindings.put(variableBinding.getOid().toString(), variableBinding);
        }
        Assert.assertEquals(11, variableBindings.size());
        Assert.assertEquals("null", variableBindings.get(AMBARI_ALERT_COMPONENT_NAME_OID).toValueString());
    }

    @Test
    public void testPrepareTrap_v2c() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        AmbariSNMPDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = getAlertNotification(true);
        PDU pdu = dispatcher.prepareTrap(notification, snmpVersion);
        Assert.assertEquals(TRAP, pdu.getType());
        Map<String, VariableBinding> variableBindings = new HashMap<>();
        for (VariableBinding variableBinding : pdu.toArray()) {
            variableBindings.put(variableBinding.getOid().toString(), variableBinding);
        }
        Assert.assertEquals(11, variableBindings.size());
        Assert.assertEquals(AMBARI_ALERT_TRAP_OID, variableBindings.get(snmpTrapOID.toString()).toValueString());
        Assert.assertEquals(String.valueOf(AmbariSNMPDispatcherTest.DEFINITION_ID), variableBindings.get(AMBARI_ALERT_DEFINITION_ID_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.DEFINITION_NAME, variableBindings.get(AMBARI_ALERT_DEFINITION_NAME_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_LABEL, variableBindings.get(AMBARI_ALERT_NAME_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_TEXT, variableBindings.get(AMBARI_ALERT_TEXT_OID).toValueString());
        Assert.assertEquals(String.valueOf(AmbariSNMPDispatcherTest.ALERT_STATE.getIntValue()), variableBindings.get(AMBARI_ALERT_STATE_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_HOSTNAME, variableBindings.get(AMBARI_ALERT_HOST_NAME_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_SERVICE_NAME, variableBindings.get(AMBARI_ALERT_SERVICE_NAME_OID).toValueString());
        Assert.assertEquals(AmbariSNMPDispatcherTest.ALERT_COMPONENT_NAME, variableBindings.get(AMBARI_ALERT_COMPONENT_NAME_OID).toValueString());
    }

    @Test
    public void testSendTraps_v1() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Snmp snmp = Mockito.mock(Snmp.class);
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new AlertNotification();
        Map<String, String> properties = new HashMap<>();
        properties.put(COMMUNITY_PROPERTY, "public");
        properties.put(PORT_PROPERTY, "162");
        notification.DispatchProperties = properties;
        Recipient rec1 = new Recipient();
        rec1.Identifier = "192.168.0.2";
        notification.Recipients = Arrays.asList(rec1);
        Mockito.doReturn(trap).when(dispatcher).prepareTrap(notification, snmpVersion);
        dispatcher.sendTraps(notification, snmpVersion);
        ArgumentCaptor<Target> argument = ArgumentCaptor.forClass(Target.class);
        Mockito.verify(snmp, Mockito.times(1)).send(ArgumentMatchers.eq(trap), argument.capture());
        Assert.assertEquals("192.168.0.2/162", argument.getValue().getAddress().toString());
        Assert.assertEquals(version1, argument.getValue().getVersion());
    }

    @Test
    public void testSendTraps_v2() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        Snmp snmp = Mockito.mock(Snmp.class);
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new AlertNotification();
        Map<String, String> properties = new HashMap<>();
        properties.put(COMMUNITY_PROPERTY, "public");
        properties.put(PORT_PROPERTY, "162");
        notification.DispatchProperties = properties;
        Recipient rec1 = new Recipient();
        rec1.Identifier = "192.168.0.2";
        notification.Recipients = Arrays.asList(rec1);
        Mockito.doReturn(trap).when(dispatcher).prepareTrap(notification, snmpVersion);
        dispatcher.sendTraps(notification, snmpVersion);
        ArgumentCaptor<Target> argument = ArgumentCaptor.forClass(Target.class);
        Mockito.verify(snmp, Mockito.times(1)).send(ArgumentMatchers.eq(trap), argument.capture());
        Assert.assertEquals("192.168.0.2/162", argument.getValue().getAddress().toString());
        Assert.assertEquals(version2c, argument.getValue().getVersion());
    }

    @Test
    public void testSendTraps_v3() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv3;
        Snmp snmp = Mockito.mock(Snmp.class);
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new AlertNotification();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        notification.DispatchProperties = properties;
        Recipient rec1 = new Recipient();
        rec1.Identifier = "192.168.0.2";
        notification.Recipients = Arrays.asList(rec1);
        Mockito.doReturn(trap).when(dispatcher).prepareTrap(notification, snmpVersion);
        dispatcher.sendTraps(notification, snmpVersion);
        ArgumentCaptor<Target> argument = ArgumentCaptor.forClass(Target.class);
        Mockito.verify(snmp, Mockito.times(1)).send(ArgumentMatchers.eq(trap), argument.capture());
        Assert.assertEquals("192.168.0.2/162", argument.getValue().getAddress().toString());
        Assert.assertEquals(version3, argument.getValue().getVersion());
    }

    @Test(expected = InvalidSnmpConfigurationException.class)
    public void testSendTraps_v3_incorrectSecurityLevelVersion() throws Exception {
        AmbariSNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv3;
        Snmp snmp = Mockito.mock(Snmp.class);
        AmbariSNMPDispatcher dispatcher = Mockito.spy(new AmbariSNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new AlertNotification();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "INCORRECT");
        notification.DispatchProperties = properties;
        Recipient rec1 = new Recipient();
        rec1.Identifier = "192.168.0.2";
        notification.Recipients = Arrays.asList(rec1);
        Mockito.doReturn(trap).when(dispatcher).prepareTrap(notification, snmpVersion);
        dispatcher.sendTraps(notification, snmpVersion);
    }

    @Test
    public void testValidateAlertValidation_SNMPv1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SNMPDispatcher.PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_incorrectSNMPversion() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv4");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv1_invalid_noPort() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv2c() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv2c_invalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_incorrectSecurityLevel() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "INCORRECT");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_noAuthNoPriv() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "NOAUTH_NOPRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthNoPriv_valid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthNoPriv_invalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_valid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_noPassphrases() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_onlyAuthPassphrase() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new AmbariSNMPDispatcher(AmbariSNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }
}

