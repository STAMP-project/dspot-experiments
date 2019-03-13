/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.audit;


import Operation.Configure;
import Operation.Start;
import Operation.Stop;
import SiteToSiteTransportProtocol.RAW;
import java.util.Collection;
import java.util.Iterator;
import org.apache.nifi.action.Action;
import org.apache.nifi.groups.RemoteProcessGroup;
import org.apache.nifi.remote.RemoteGroupPort;
import org.apache.nifi.web.api.dto.BatchSettingsDTO;
import org.apache.nifi.web.api.dto.RemoteProcessGroupDTO;
import org.apache.nifi.web.api.dto.RemoteProcessGroupPortDTO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestRemoteProcessGroupAuditor {
    @Test
    public void testEnableTransmission() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.isTransmitting()).thenReturn(false);
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setTransmitting(true);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Start, action.getOperation());
        Assert.assertNull(action.getActionDetails());
    }

    @Test
    public void testDisableTransmission() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.isTransmitting()).thenReturn(true);
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setTransmitting(false);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Stop, action.getOperation());
        Assert.assertNull(action.getActionDetails());
    }

    @Test
    public void testConfigureCommunicationsTimeout() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getCommunicationsTimeout()).thenReturn("30 sec");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setCommunicationsTimeout("31 sec");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Communications Timeout", existingRPG.getCommunicationsTimeout(), inputRPGDTO.getCommunicationsTimeout());
    }

    @Test
    public void testConfigureYieldDuration() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getYieldDuration()).thenReturn("10 sec");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setYieldDuration("11 sec");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Yield Duration", existingRPG.getYieldDuration(), inputRPGDTO.getYieldDuration());
    }

    @Test
    public void testConfigureTransportProtocol() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getTransportProtocol()).thenReturn(RAW);
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setTransportProtocol("HTTP");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Transport Protocol", existingRPG.getTransportProtocol().name(), inputRPGDTO.getTransportProtocol());
    }

    @Test
    public void testConfigureProxyHost() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyHost("proxy.example.com");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Host", existingRPG.getProxyHost(), inputRPGDTO.getProxyHost());
    }

    @Test
    public void testConfigureProxyHostUpdate() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getProxyHost()).thenReturn("proxy1.example.com");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyHost("proxy2.example.com");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Host", existingRPG.getProxyHost(), inputRPGDTO.getProxyHost());
    }

    @Test
    public void testConfigureProxyHostClear() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getProxyHost()).thenReturn("proxy.example.com");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyHost("");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Host", existingRPG.getProxyHost(), inputRPGDTO.getProxyHost());
    }

    @Test
    public void testConfigureProxyPort() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyPort(3128);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Port", existingRPG.getProxyPort(), inputRPGDTO.getProxyPort());
    }

    @Test
    public void testConfigureProxyPortClear() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getProxyPort()).thenReturn(3128);
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyPort(null);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Port", existingRPG.getProxyPort(), inputRPGDTO.getProxyPort());
    }

    @Test
    public void testConfigureProxyUser() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyUser("proxy-user");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy User", existingRPG.getProxyUser(), inputRPGDTO.getProxyUser());
    }

    @Test
    public void testConfigureProxyUserClear() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getProxyUser()).thenReturn("proxy-user");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyUser(null);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy User", existingRPG.getProxyUser(), inputRPGDTO.getProxyUser());
    }

    @Test
    public void testConfigureProxyPassword() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyPassword("proxy-password");
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Password", "", SENSITIVE_VALUE_MASK);
    }

    @Test
    public void testConfigureProxyPasswordClear() throws Throwable {
        final RemoteProcessGroup existingRPG = defaultRemoteProcessGroup();
        Mockito.when(existingRPG.getProxyPassword()).thenReturn("proxy-password");
        final RemoteProcessGroupDTO inputRPGDTO = defaultInput();
        inputRPGDTO.setProxyPassword(null);
        final Collection<Action> actions = updateProcessGroupConfiguration(inputRPGDTO, existingRPG);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "Proxy Password", SENSITIVE_VALUE_MASK, "");
    }

    @Test
    public void testEnablePort() throws Throwable {
        final RemoteGroupPort existingRPGPort = defaultRemoteGroupPort();
        Mockito.when(existingRPGPort.getName()).thenReturn("input-port-1");
        Mockito.when(existingRPGPort.isRunning()).thenReturn(false);
        final RemoteProcessGroupPortDTO inputRPGPortDTO = defaultRemoteProcessGroupPortDTO();
        inputRPGPortDTO.setTransmitting(true);
        final Collection<Action> actions = updateProcessGroupInputPortConfiguration(inputRPGPortDTO, existingRPGPort);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Transmission", "disabled", "enabled");
    }

    @Test
    public void testDisablePort() throws Throwable {
        final RemoteGroupPort existingRPGPort = defaultRemoteGroupPort();
        Mockito.when(existingRPGPort.getName()).thenReturn("input-port-1");
        Mockito.when(existingRPGPort.isRunning()).thenReturn(true);
        final RemoteProcessGroupPortDTO inputRPGPortDTO = defaultRemoteProcessGroupPortDTO();
        inputRPGPortDTO.setTransmitting(false);
        final Collection<Action> actions = updateProcessGroupInputPortConfiguration(inputRPGPortDTO, existingRPGPort);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Transmission", "enabled", "disabled");
    }

    @Test
    public void testConfigurePortConcurrency() throws Throwable {
        final RemoteGroupPort existingRPGPort = defaultRemoteGroupPort();
        Mockito.when(existingRPGPort.getName()).thenReturn("input-port-1");
        final RemoteProcessGroupPortDTO inputRPGPortDTO = defaultRemoteProcessGroupPortDTO();
        inputRPGPortDTO.setConcurrentlySchedulableTaskCount(2);
        final Collection<Action> actions = updateProcessGroupInputPortConfiguration(inputRPGPortDTO, existingRPGPort);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Concurrent Tasks", "1", "2");
    }

    @Test
    public void testConfigurePortCompression() throws Throwable {
        final RemoteGroupPort existingRPGPort = defaultRemoteGroupPort();
        Mockito.when(existingRPGPort.getName()).thenReturn("input-port-1");
        final RemoteProcessGroupPortDTO inputRPGPortDTO = defaultRemoteProcessGroupPortDTO();
        inputRPGPortDTO.setUseCompression(true);
        final Collection<Action> actions = updateProcessGroupInputPortConfiguration(inputRPGPortDTO, existingRPGPort);
        Assert.assertEquals(1, actions.size());
        final Action action = actions.iterator().next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Compressed", "false", "true");
    }

    @Test
    public void testConfigurePortBatchSettings() throws Throwable {
        final RemoteGroupPort existingRPGPort = defaultRemoteGroupPort();
        Mockito.when(existingRPGPort.getName()).thenReturn("input-port-1");
        final RemoteProcessGroupPortDTO inputRPGPortDTO = defaultRemoteProcessGroupPortDTO();
        final BatchSettingsDTO batchSettingsDTO = new BatchSettingsDTO();
        batchSettingsDTO.setCount(1234);
        batchSettingsDTO.setSize("64KB");
        batchSettingsDTO.setDuration("10sec");
        inputRPGPortDTO.setBatchSettings(batchSettingsDTO);
        final Collection<Action> actions = updateProcessGroupInputPortConfiguration(inputRPGPortDTO, existingRPGPort);
        Assert.assertEquals(3, actions.size());
        final Iterator<Action> iterator = actions.iterator();
        Action action = iterator.next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Batch Count", "0", "1234");
        action = iterator.next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Batch Size", "", "64KB");
        action = iterator.next();
        Assert.assertEquals(Configure, action.getOperation());
        assertConfigureDetails(action.getActionDetails(), "input-port-1.Batch Duration", "", "10sec");
    }
}

