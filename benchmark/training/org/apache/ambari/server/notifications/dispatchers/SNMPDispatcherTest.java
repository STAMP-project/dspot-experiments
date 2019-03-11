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


import PDU.TRAP;
import PDU.V1TRAP;
import SNMPDispatcher.BODY_OID_PROPERTY;
import SNMPDispatcher.COMMUNITY_PROPERTY;
import SNMPDispatcher.InvalidSnmpConfigurationException;
import SNMPDispatcher.PORT_PROPERTY;
import SNMPDispatcher.SECURITY_AUTH_PASSPHRASE_PROPERTY;
import SNMPDispatcher.SECURITY_LEVEL_PROPERTY;
import SNMPDispatcher.SECURITY_PRIV_PASSPHRASE_PROPERTY;
import SNMPDispatcher.SECURITY_USERNAME_PROPERTY;
import SNMPDispatcher.SNMP_VERSION_PROPERTY;
import SNMPDispatcher.SUBJECT_OID_PROPERTY;
import SNMPDispatcher.SnmpVersion;
import SNMPDispatcher.TRAP_OID_PROPERTY;
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
import java.util.Map;
import org.apache.ambari.server.notifications.DispatchCallback;
import org.apache.ambari.server.notifications.Notification;
import org.apache.ambari.server.notifications.NotificationDispatcher;
import org.apache.ambari.server.notifications.Recipient;
import org.apache.ambari.server.notifications.TargetConfigurationResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;


public class SNMPDispatcherTest {
    private static final int DEFAULT_SNMP_PORT = 31444;

    @Test
    public void testDispatch_nullProperties() throws Exception {
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatchUdpTransportMappingCrash() throws Exception {
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT));
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doThrow(new IOException()).when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
        Assert.assertNull(dispatcher.getTransportMapping());
    }

    @Test
    public void testDispatch_notDefinedProperties() throws Exception {
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        notification.DispatchProperties = new HashMap();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_nullRecipients() throws Exception {
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_noRecipients() throws Exception {
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = new ArrayList();
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_sendTrapError() throws Exception {
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT));
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doThrow(new RuntimeException()).when(dispatcher).sendTraps(ArgumentMatchers.eq(notification), ArgumentMatchers.any(SnmpVersion.class));
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_incorrectSnmpVersion() throws Exception {
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT));
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv11");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = Arrays.asList(new Recipient());
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback, Mockito.never()).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v1() throws Exception {
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT));
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doNothing().when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback, Mockito.never()).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v2() throws Exception {
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT));
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        Notification notification = Mockito.mock(Notification.class);
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "3");
        properties.put(COMMUNITY_PROPERTY, "4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        notification.Recipients = Arrays.asList(new Recipient());
        Mockito.doNothing().when(dispatcher).sendTraps(notification, snmpVersion);
        dispatcher.dispatch(notification);
        Mockito.verify(notification.Callback, Mockito.never()).onFailure(notification.CallbackIds);
        Mockito.verify(notification.Callback).onSuccess(notification.CallbackIds);
    }

    @Test
    public void testDispatch_successful_v3() throws Exception {
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = new Notification();
        notification.Callback = Mockito.mock(DispatchCallback.class);
        notification.CallbackIds = new ArrayList();
        notification.Body = "body";
        notification.Subject = "subject";
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(COMMUNITY_PROPERTY, "public");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
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
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = new Notification();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(TRAP_OID_PROPERTY, "3");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        PDU pdu = dispatcher.prepareTrap(notification, snmpVersion);
        Assert.assertEquals(V1TRAP, pdu.getType());
        Map<String, VariableBinding> variableBindings = new HashMap<>();
        for (VariableBinding variableBinding : pdu.toArray()) {
            variableBindings.put(variableBinding.getOid().toString(), variableBinding);
        }
        Assert.assertEquals(3, variableBindings.size());
        Assert.assertEquals("subject", variableBindings.get("1").toValueString());
        Assert.assertEquals("body", variableBindings.get("2").toValueString());
        Assert.assertEquals("3", variableBindings.get(snmpTrapOID.toString()).toValueString());
    }

    @Test
    public void testPrepareTrap_v2c() throws Exception {
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        SNMPDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        Notification notification = new Notification();
        Map<String, String> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(TRAP_OID_PROPERTY, "4");
        notification.DispatchProperties = properties;
        notification.Body = "body";
        notification.Subject = "subject";
        PDU pdu = dispatcher.prepareTrap(notification, snmpVersion);
        Assert.assertEquals(TRAP, pdu.getType());
        Map<String, VariableBinding> variableBindings = new HashMap<>();
        for (VariableBinding variableBinding : pdu.toArray()) {
            variableBindings.put(variableBinding.getOid().toString(), variableBinding);
        }
        Assert.assertEquals(3, variableBindings.size());
        Assert.assertEquals("subject", variableBindings.get("1").toValueString());
        Assert.assertEquals("body", variableBindings.get("2").toValueString());
        Assert.assertEquals("4", variableBindings.get(snmpTrapOID.toString()).toValueString());
    }

    @Test
    public void testSendTraps_v1() throws Exception {
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv1;
        Snmp snmp = Mockito.mock(Snmp.class);
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new Notification();
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
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv2c;
        Snmp snmp = Mockito.mock(Snmp.class);
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new Notification();
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
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv3;
        Snmp snmp = Mockito.mock(Snmp.class);
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new Notification();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
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
        SNMPDispatcher.SnmpVersion snmpVersion = SnmpVersion.SNMPv3;
        Snmp snmp = Mockito.mock(Snmp.class);
        SNMPDispatcher dispatcher = Mockito.spy(new SNMPDispatcher(snmp));
        PDU trap = Mockito.mock(PDU.class);
        Notification notification = new Notification();
        Map<String, String> properties = new HashMap<>();
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
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
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_incorrectSNMPversion() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv4");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv1_invalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv1");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv2c() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(COMMUNITY_PROPERTY, "public");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv2c_invalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv2c");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_incorrectSecurityLevel() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "INCORRECT");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_noAuthNoPriv() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "NOAUTH_NOPRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthNoPriv_valid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthNoPriv_invalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_NOPRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_valid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_PRIV_PASSPHRASE_PROPERTY, "PASSPHRASE2");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(VALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_noPassphrases() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }

    @Test
    public void testValidateAlertValidation_SNMPv3_AuthPriv_onlyAuthPassphrase() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(SUBJECT_OID_PROPERTY, "1");
        properties.put(BODY_OID_PROPERTY, "2");
        properties.put(PORT_PROPERTY, "162");
        properties.put(SNMP_VERSION_PROPERTY, "SNMPv3");
        properties.put(TRAP_OID_PROPERTY, "1.3.6.1.6.3.1.1.5.4");
        properties.put(SECURITY_USERNAME_PROPERTY, "USER");
        properties.put(SECURITY_AUTH_PASSPHRASE_PROPERTY, "PASSPHRASE1");
        properties.put(SECURITY_LEVEL_PROPERTY, "AUTH_PRIV");
        NotificationDispatcher dispatcher = new SNMPDispatcher(SNMPDispatcherTest.DEFAULT_SNMP_PORT);
        TargetConfigurationResult configValidationResult = dispatcher.validateTargetConfig(properties);
        Assert.assertEquals(INVALID, configValidationResult.getStatus());
    }
}

