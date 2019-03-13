/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.geode.internal.Assert;
import org.apache.geode.internal.cache.client.protocol.ClientProtocolProcessor;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;


@Category({ ClientServerTest.class })
public class ProtobufServerConnectionTest {
    private ClientHealthMonitor clientHealthMonitorMock;

    @Test
    public void testProcessFlag() throws Exception {
        ServerConnection serverConnection = IOExceptionThrowingServerConnection();
        Assert.assertTrue(serverConnection.processMessages);
        serverConnection.doOneMessage();
        Assert.assertTrue((!(serverConnection.processMessages)));
    }

    @Test
    public void emergencyCloseClosesSocket() throws IOException {
        Socket socketMock = Mockito.mock(Socket.class);
        Mockito.when(socketMock.getInetAddress()).thenReturn(InetAddress.getByName("localhost"));
        AcceptorImpl acceptorStub = Mockito.mock(AcceptorImpl.class);
        ClientProtocolProcessor clientProtocolProcessorMock = Mockito.mock(ClientProtocolProcessor.class);
        ProtobufServerConnection protobufServerConnection = getServerConnection(socketMock, clientProtocolProcessorMock, acceptorStub);
        protobufServerConnection.emergencyClose();
        Mockito.verify(socketMock).close();
    }

    @Test
    public void testClientHealthMonitorRegistration() throws IOException {
        AcceptorImpl acceptorStub = Mockito.mock(AcceptorImpl.class);
        ClientProtocolProcessor clientProtocolProcessor = Mockito.mock(ClientProtocolProcessor.class);
        ServerConnection serverConnection = getServerConnection(clientProtocolProcessor, acceptorStub);
        ArgumentCaptor<ClientProxyMembershipID> registerCpmidArgumentCaptor = ArgumentCaptor.forClass(ClientProxyMembershipID.class);
        ArgumentCaptor<ClientProxyMembershipID> addConnectionCpmidArgumentCaptor = ArgumentCaptor.forClass(ClientProxyMembershipID.class);
        Mockito.verify(clientHealthMonitorMock).addConnection(addConnectionCpmidArgumentCaptor.capture(), ArgumentMatchers.eq(serverConnection));
        Mockito.verify(clientHealthMonitorMock).registerClient(registerCpmidArgumentCaptor.capture());
        assertEquals("identity(localhost<ec>:0,connection=1", registerCpmidArgumentCaptor.getValue().toString());
        assertEquals("identity(localhost<ec>:0,connection=1", addConnectionCpmidArgumentCaptor.getValue().toString());
    }

    @Test
    public void testDoOneMessageNotifiesClientHealthMonitor() throws IOException {
        AcceptorImpl acceptorStub = Mockito.mock(AcceptorImpl.class);
        ClientProtocolProcessor clientProtocolProcessor = Mockito.mock(ClientProtocolProcessor.class);
        ServerConnection serverConnection = getServerConnection(clientProtocolProcessor, acceptorStub);
        serverConnection.doOneMessage();
        ArgumentCaptor<ClientProxyMembershipID> clientProxyMembershipIDArgumentCaptor = ArgumentCaptor.forClass(ClientProxyMembershipID.class);
        Mockito.verify(clientHealthMonitorMock).receivedPing(clientProxyMembershipIDArgumentCaptor.capture());
        assertEquals("identity(localhost<ec>:0,connection=1", clientProxyMembershipIDArgumentCaptor.getValue().toString());
    }
}

