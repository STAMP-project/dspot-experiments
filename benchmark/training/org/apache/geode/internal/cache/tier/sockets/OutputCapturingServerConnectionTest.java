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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.geode.cache.IncompatibleVersionException;
import org.apache.geode.internal.cache.client.protocol.ClientProtocolProcessor;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class OutputCapturingServerConnectionTest {
    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testEOFDoesNotCauseWarningMessage() throws IOException, IncompatibleVersionException {
        Socket socketMock = Mockito.mock(Socket.class);
        Mockito.when(socketMock.getInetAddress()).thenReturn(InetAddress.getByName("localhost"));
        Mockito.when(socketMock.isClosed()).thenReturn(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(socketMock.getOutputStream()).thenReturn(outputStream);
        AcceptorImpl acceptorStub = Mockito.mock(AcceptorImpl.class);
        ClientProtocolProcessor clientProtocolProcessor = Mockito.mock(ClientProtocolProcessor.class);
        Mockito.doThrow(new IOException()).when(clientProtocolProcessor).processMessage(ArgumentMatchers.any(), ArgumentMatchers.any());
        ServerConnection serverConnection = getServerConnection(socketMock, clientProtocolProcessor, acceptorStub);
        String expectedMessage = "invoking doOneMessage";
        String unexpectedMessage = "IOException";
        // Create some stdout content so we can tell that the capture worked.
        System.out.println(expectedMessage);
        serverConnection.doOneMessage();
        // verify that an IOException wasn't logged
        String stdoutCapture = systemOutRule.getLog();
        Assert.assertTrue(stdoutCapture.contains(expectedMessage));
        Assert.assertFalse(stdoutCapture.contains(unexpectedMessage));
    }
}

