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
package org.apache.nifi.processors.websocket;


import AbstractWebSocketGatewayProcessor.REL_CONNECTED;
import AbstractWebSocketGatewayProcessor.REL_MESSAGE_BINARY;
import AbstractWebSocketGatewayProcessor.REL_MESSAGE_TEXT;
import ListenWebSocket.PROP_SERVER_URL_PATH;
import ListenWebSocket.PROP_WEBSOCKET_SERVER_SERVICE;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessSession;
import org.apache.nifi.util.SharedSessionState;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.apache.nifi.websocket.AbstractWebSocketSession;
import org.apache.nifi.websocket.WebSocketServerService;
import org.apache.nifi.websocket.WebSocketSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestListenWebSocket {
    @Test
    public void testValidationError() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(ListenWebSocket.class);
        final WebSocketServerService service = Mockito.mock(WebSocketServerService.class);
        final String serviceId = "ws-service";
        final String endpointId = "test";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        runner.setProperty(PROP_WEBSOCKET_SERVER_SERVICE, serviceId);
        runner.setProperty(PROP_SERVER_URL_PATH, endpointId);
        try {
            runner.run();
            Assert.fail("Should fail with validation error.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.toString().contains("'server-url-path' is invalid because Must starts with"));
        }
    }

    @Test
    public void testSuccess() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(ListenWebSocket.class);
        final ListenWebSocket processor = ((ListenWebSocket) (runner.getProcessor()));
        final SharedSessionState sharedSessionState = new SharedSessionState(processor, new AtomicLong(0));
        // Use this custom session factory implementation so that createdSessions can be read from test case,
        // because MockSessionFactory doesn't expose it.
        final Set<MockProcessSession> createdSessions = new HashSet<>();
        final ProcessSessionFactory sessionFactory = () -> {
            final MockProcessSession session = new MockProcessSession(sharedSessionState, processor);
            createdSessions.add(session);
            return session;
        };
        final WebSocketServerService service = Mockito.mock(WebSocketServerService.class);
        final WebSocketSession webSocketSession = Mockito.spy(AbstractWebSocketSession.class);
        Mockito.when(webSocketSession.getSessionId()).thenReturn("ws-session-id");
        Mockito.when(webSocketSession.getLocalAddress()).thenReturn(new InetSocketAddress("localhost", 12345));
        Mockito.when(webSocketSession.getRemoteAddress()).thenReturn(new InetSocketAddress("example.com", 80));
        final String serviceId = "ws-service";
        final String endpointId = "/test";
        final String textMessageReceived = "message from server.";
        final AtomicReference<Boolean> registered = new AtomicReference<>(false);
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        Mockito.doAnswer(( invocation) -> {
            registered.set(true);
            processor.connected(webSocketSession);
            // Two times.
            processor.consume(webSocketSession, textMessageReceived);
            processor.consume(webSocketSession, textMessageReceived);
            // Three times.
            final byte[] binaryMessage = textMessageReceived.getBytes();
            processor.consume(webSocketSession, binaryMessage, 0, binaryMessage.length);
            processor.consume(webSocketSession, binaryMessage, 0, binaryMessage.length);
            processor.consume(webSocketSession, binaryMessage, 0, binaryMessage.length);
            return null;
        }).when(service).registerProcessor(endpointId, processor);
        Mockito.doAnswer(( invocation) -> registered.get()).when(service).isProcessorRegistered(ArgumentMatchers.eq(endpointId), ArgumentMatchers.eq(processor));
        Mockito.doAnswer(( invocation) -> {
            registered.set(false);
            return null;
        }).when(service).deregisterProcessor(ArgumentMatchers.eq(endpointId), ArgumentMatchers.eq(processor));
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        runner.setProperty(PROP_WEBSOCKET_SERVER_SERVICE, serviceId);
        runner.setProperty(PROP_SERVER_URL_PATH, endpointId);
        processor.onTrigger(runner.getProcessContext(), sessionFactory);
        Map<Relationship, List<MockFlowFile>> transferredFlowFiles = getAllTransferredFlowFiles(createdSessions, processor);
        List<MockFlowFile> connectedFlowFiles = transferredFlowFiles.get(REL_CONNECTED);
        Assert.assertEquals(1, connectedFlowFiles.size());
        connectedFlowFiles.forEach(( ff) -> {
            assertFlowFile(webSocketSession, serviceId, endpointId, ff, null);
        });
        List<MockFlowFile> textFlowFiles = transferredFlowFiles.get(REL_MESSAGE_TEXT);
        Assert.assertEquals(2, textFlowFiles.size());
        textFlowFiles.forEach(( ff) -> {
            assertFlowFile(webSocketSession, serviceId, endpointId, ff, WebSocketMessage.Type.TEXT);
        });
        List<MockFlowFile> binaryFlowFiles = transferredFlowFiles.get(REL_MESSAGE_BINARY);
        Assert.assertEquals(3, binaryFlowFiles.size());
        binaryFlowFiles.forEach(( ff) -> {
            assertFlowFile(webSocketSession, serviceId, endpointId, ff, WebSocketMessage.Type.BINARY);
        });
        final List<ProvenanceEventRecord> provenanceEvents = sharedSessionState.getProvenanceEvents();
        Assert.assertEquals(6, provenanceEvents.size());
        Assert.assertTrue(provenanceEvents.stream().allMatch(( event) -> ProvenanceEventType.RECEIVE.equals(event.getEventType())));
        runner.clearTransferState();
        runner.clearProvenanceEvents();
        createdSessions.clear();
        Assert.assertEquals(0, createdSessions.size());
        // Simulate that the processor has started, and it get's triggered again
        processor.onTrigger(runner.getProcessContext(), sessionFactory);
        Assert.assertEquals("No session should be created", 0, createdSessions.size());
        // Simulate that the processor is stopped.
        processor.onStopped(runner.getProcessContext());
        Assert.assertEquals("No session should be created", 0, createdSessions.size());
        // Simulate that the processor is restarted.
        // And the mock service will emit consume msg events.
        processor.onTrigger(runner.getProcessContext(), sessionFactory);
        Assert.assertEquals("Processor should register it with the service again", 6, createdSessions.size());
    }
}

