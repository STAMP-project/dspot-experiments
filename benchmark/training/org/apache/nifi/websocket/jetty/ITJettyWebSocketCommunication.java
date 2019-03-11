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
package org.apache.nifi.websocket.jetty;


import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.websocket.BinaryMessageConsumer;
import org.apache.nifi.websocket.ConnectedListener;
import org.apache.nifi.websocket.TextMessageConsumer;
import org.apache.nifi.websocket.WebSocketClientService;
import org.apache.nifi.websocket.WebSocketServerService;
import org.apache.nifi.websocket.WebSocketSessionInfo;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ITJettyWebSocketCommunication {
    protected int serverPort;

    protected String serverPath = "/test";

    protected WebSocketServerService serverService;

    protected ControllerServiceTestContext serverServiceContext;

    protected WebSocketClientService clientService;

    protected ControllerServiceTestContext clientServiceContext;

    protected interface MockWebSocketProcessor extends Processor , BinaryMessageConsumer , ConnectedListener , TextMessageConsumer {}

    @Test
    public void testClientServerCommunication() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        // Expectations.
        final CountDownLatch serverIsConnectedByClient = new CountDownLatch(1);
        final CountDownLatch clientConnectedServer = new CountDownLatch(1);
        final CountDownLatch serverReceivedTextMessageFromClient = new CountDownLatch(1);
        final CountDownLatch serverReceivedBinaryMessageFromClient = new CountDownLatch(1);
        final CountDownLatch clientReceivedTextMessageFromServer = new CountDownLatch(1);
        final CountDownLatch clientReceivedBinaryMessageFromServer = new CountDownLatch(1);
        final String textMessageFromClient = "Message from client.";
        final String textMessageFromServer = "Message from server.";
        final ITJettyWebSocketCommunication.MockWebSocketProcessor serverProcessor = Mockito.mock(ITJettyWebSocketCommunication.MockWebSocketProcessor.class);
        getIdentifier();
        final AtomicReference<String> serverSessionIdRef = new AtomicReference<>();
        connected(ArgumentMatchers.any(WebSocketSessionInfo.class));
        Mockito.doAnswer(( invocation) -> assertConsumeTextMessage(serverReceivedTextMessageFromClient, textMessageFromClient, invocation)).when(serverProcessor).consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.anyString());
        consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        serverService.registerProcessor(serverPath, serverProcessor);
        final String clientId = "client1";
        final ITJettyWebSocketCommunication.MockWebSocketProcessor clientProcessor = Mockito.mock(ITJettyWebSocketCommunication.MockWebSocketProcessor.class);
        getIdentifier();
        final AtomicReference<String> clientSessionIdRef = new AtomicReference<>();
        connected(ArgumentMatchers.any(WebSocketSessionInfo.class));
        Mockito.doAnswer(( invocation) -> assertConsumeTextMessage(clientReceivedTextMessageFromServer, textMessageFromServer, invocation)).when(clientProcessor).consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.anyString());
        consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        clientService.registerProcessor(clientId, clientProcessor);
        clientService.connect(clientId);
        Assert.assertTrue("WebSocket client should be able to fire connected event.", clientConnectedServer.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket server should be able to fire connected event.", serverIsConnectedByClient.await(5, TimeUnit.SECONDS));
        clientService.sendMessage(clientId, clientSessionIdRef.get(), ( sender) -> sender.sendString(textMessageFromClient));
        clientService.sendMessage(clientId, clientSessionIdRef.get(), ( sender) -> sender.sendBinary(ByteBuffer.wrap(textMessageFromClient.getBytes())));
        Assert.assertTrue("WebSocket server should be able to consume text message.", serverReceivedTextMessageFromClient.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket server should be able to consume binary message.", serverReceivedBinaryMessageFromClient.await(5, TimeUnit.SECONDS));
        serverService.sendMessage(serverPath, serverSessionIdRef.get(), ( sender) -> sender.sendString(textMessageFromServer));
        serverService.sendMessage(serverPath, serverSessionIdRef.get(), ( sender) -> sender.sendBinary(ByteBuffer.wrap(textMessageFromServer.getBytes())));
        Assert.assertTrue("WebSocket client should be able to consume text message.", clientReceivedTextMessageFromServer.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket client should be able to consume binary message.", clientReceivedBinaryMessageFromServer.await(5, TimeUnit.SECONDS));
        clientService.deregisterProcessor(clientId, clientProcessor);
        serverService.deregisterProcessor(serverPath, serverProcessor);
    }

    @Test
    public void testClientServerCommunicationRecovery() throws Exception {
        Assume.assumeFalse(isWindowsEnvironment());
        // Expectations.
        final CountDownLatch serverIsConnectedByClient = new CountDownLatch(1);
        final CountDownLatch clientConnectedServer = new CountDownLatch(1);
        final CountDownLatch serverReceivedTextMessageFromClient = new CountDownLatch(1);
        final CountDownLatch serverReceivedBinaryMessageFromClient = new CountDownLatch(1);
        final CountDownLatch clientReceivedTextMessageFromServer = new CountDownLatch(1);
        final CountDownLatch clientReceivedBinaryMessageFromServer = new CountDownLatch(1);
        final String textMessageFromClient = "Message from client.";
        final String textMessageFromServer = "Message from server.";
        final ITJettyWebSocketCommunication.MockWebSocketProcessor serverProcessor = Mockito.mock(ITJettyWebSocketCommunication.MockWebSocketProcessor.class);
        getIdentifier();
        final AtomicReference<String> serverSessionIdRef = new AtomicReference<>();
        connected(ArgumentMatchers.any(WebSocketSessionInfo.class));
        Mockito.doAnswer(( invocation) -> assertConsumeTextMessage(serverReceivedTextMessageFromClient, textMessageFromClient, invocation)).when(serverProcessor).consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.anyString());
        consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        serverService.registerProcessor(serverPath, serverProcessor);
        final String clientId = "client1";
        final ITJettyWebSocketCommunication.MockWebSocketProcessor clientProcessor = Mockito.mock(ITJettyWebSocketCommunication.MockWebSocketProcessor.class);
        getIdentifier();
        final AtomicReference<String> clientSessionIdRef = new AtomicReference<>();
        connected(ArgumentMatchers.any(WebSocketSessionInfo.class));
        Mockito.doAnswer(( invocation) -> assertConsumeTextMessage(clientReceivedTextMessageFromServer, textMessageFromServer, invocation)).when(clientProcessor).consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.anyString());
        consume(ArgumentMatchers.any(WebSocketSessionInfo.class), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        clientService.registerProcessor(clientId, clientProcessor);
        clientService.connect(clientId);
        Assert.assertTrue("WebSocket client should be able to fire connected event.", clientConnectedServer.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket server should be able to fire connected event.", serverIsConnectedByClient.await(5, TimeUnit.SECONDS));
        // Nothing happens if maintenance is executed while sessions are alive.
        maintainSessions();
        // Restart server.
        serverService.stopServer();
        serverService.startServer(serverServiceContext.getConfigurationContext());
        // Sessions will be recreated with the same session ids.
        maintainSessions();
        clientService.sendMessage(clientId, clientSessionIdRef.get(), ( sender) -> sender.sendString(textMessageFromClient));
        clientService.sendMessage(clientId, clientSessionIdRef.get(), ( sender) -> sender.sendBinary(ByteBuffer.wrap(textMessageFromClient.getBytes())));
        Assert.assertTrue("WebSocket server should be able to consume text message.", serverReceivedTextMessageFromClient.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket server should be able to consume binary message.", serverReceivedBinaryMessageFromClient.await(5, TimeUnit.SECONDS));
        serverService.sendMessage(serverPath, serverSessionIdRef.get(), ( sender) -> sender.sendString(textMessageFromServer));
        serverService.sendMessage(serverPath, serverSessionIdRef.get(), ( sender) -> sender.sendBinary(ByteBuffer.wrap(textMessageFromServer.getBytes())));
        Assert.assertTrue("WebSocket client should be able to consume text message.", clientReceivedTextMessageFromServer.await(5, TimeUnit.SECONDS));
        Assert.assertTrue("WebSocket client should be able to consume binary message.", clientReceivedBinaryMessageFromServer.await(5, TimeUnit.SECONDS));
        clientService.deregisterProcessor(clientId, clientProcessor);
        serverService.deregisterProcessor(serverPath, serverProcessor);
    }
}

