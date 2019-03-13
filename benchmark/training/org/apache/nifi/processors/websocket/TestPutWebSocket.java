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


import PutWebSocket.PROP_WS_MESSAGE_TYPE;
import PutWebSocket.REL_FAILURE;
import PutWebSocket.REL_SUCCESS;
import WebSocketMessage.Type.BINARY;
import WebSocketMessage.Type.TEXT;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.apache.nifi.websocket.SendMessage;
import org.apache.nifi.websocket.WebSocketService;
import org.apache.nifi.websocket.WebSocketSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestPutWebSocket {
    @Test
    public void testSessionIsNotSpecified() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(PutWebSocket.class);
        final WebSocketService service = Mockito.spy(WebSocketService.class);
        final String serviceId = "ws-service";
        final String endpointId = "client-1";
        final String textMessageFromServer = "message from server.";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_CS_ID, serviceId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_ENDPOINT_ID, endpointId);
        runner.enqueue(textMessageFromServer, attributes);
        runner.run();
        final List<MockFlowFile> succeededFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        // assertEquals(0, succeededFlowFiles.size());   //No longer valid test after NIFI-3318 since not specifying sessionid will send to all clients
        Assert.assertEquals(1, succeededFlowFiles.size());
        final List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        // assertEquals(1, failedFlowFiles.size());      //No longer valid test after NIFI-3318
        Assert.assertEquals(0, failedFlowFiles.size());
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(0, provenanceEvents.size());
    }

    @Test
    public void testServiceIsNotFound() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(PutWebSocket.class);
        final ControllerService service = Mockito.spy(ControllerService.class);
        final WebSocketSession webSocketSession = getWebSocketSession();
        final String serviceId = "ws-service";
        final String endpointId = "client-1";
        final String textMessageFromServer = "message from server.";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_CS_ID, "different-service-id");
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_ENDPOINT_ID, endpointId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_SESSION_ID, webSocketSession.getSessionId());
        runner.enqueue(textMessageFromServer, attributes);
        runner.run();
        final List<MockFlowFile> succeededFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, succeededFlowFiles.size());
        final List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failedFlowFiles.size());
        final MockFlowFile failedFlowFile = failedFlowFiles.iterator().next();
        Assert.assertNotNull(failedFlowFile.getAttribute(WebSocketProcessorAttributes.ATTR_WS_FAILURE_DETAIL));
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(0, provenanceEvents.size());
    }

    @Test
    public void testServiceIsNotWebSocketService() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(PutWebSocket.class);
        final ControllerService service = Mockito.spy(ControllerService.class);
        final WebSocketSession webSocketSession = getWebSocketSession();
        final String serviceId = "ws-service";
        final String endpointId = "client-1";
        final String textMessageFromServer = "message from server.";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_CS_ID, serviceId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_ENDPOINT_ID, endpointId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_SESSION_ID, webSocketSession.getSessionId());
        runner.enqueue(textMessageFromServer, attributes);
        runner.run();
        final List<MockFlowFile> succeededFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, succeededFlowFiles.size());
        final List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failedFlowFiles.size());
        final MockFlowFile failedFlowFile = failedFlowFiles.iterator().next();
        Assert.assertNotNull(failedFlowFile.getAttribute(WebSocketProcessorAttributes.ATTR_WS_FAILURE_DETAIL));
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(0, provenanceEvents.size());
    }

    @Test
    public void testSendFailure() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(PutWebSocket.class);
        final WebSocketService service = Mockito.spy(WebSocketService.class);
        final WebSocketSession webSocketSession = getWebSocketSession();
        final String serviceId = "ws-service";
        final String endpointId = "client-1";
        final String textMessageFromServer = "message from server.";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        Mockito.doAnswer(( invocation) -> {
            final SendMessage sendMessage = getArgumentAt(2, SendMessage.class);
            sendMessage.send(webSocketSession);
            return null;
        }).when(service).sendMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(SendMessage.class));
        Mockito.doThrow(new IOException("Sending message failed.")).when(webSocketSession).sendString(ArgumentMatchers.anyString());
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_CS_ID, serviceId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_ENDPOINT_ID, endpointId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_SESSION_ID, webSocketSession.getSessionId());
        runner.enqueue(textMessageFromServer, attributes);
        runner.run();
        final List<MockFlowFile> succeededFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, succeededFlowFiles.size());
        final List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failedFlowFiles.size());
        final MockFlowFile failedFlowFile = failedFlowFiles.iterator().next();
        Assert.assertNotNull(failedFlowFile.getAttribute(WebSocketProcessorAttributes.ATTR_WS_FAILURE_DETAIL));
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(0, provenanceEvents.size());
    }

    @Test
    public void testSuccess() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(PutWebSocket.class);
        final WebSocketService service = Mockito.spy(WebSocketService.class);
        final WebSocketSession webSocketSession = getWebSocketSession();
        final String serviceId = "ws-service";
        final String endpointId = "client-1";
        final String textMessageFromServer = "message from server.";
        Mockito.when(service.getIdentifier()).thenReturn(serviceId);
        Mockito.doAnswer(( invocation) -> {
            final SendMessage sendMessage = getArgumentAt(2, SendMessage.class);
            sendMessage.send(webSocketSession);
            return null;
        }).when(service).sendMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(SendMessage.class));
        runner.addControllerService(serviceId, service);
        runner.enableControllerService(service);
        runner.setProperty(PROP_WS_MESSAGE_TYPE, (("${" + (WebSocketProcessorAttributes.ATTR_WS_MESSAGE_TYPE)) + "}"));
        // Enqueue 1st file as Text.
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_CS_ID, serviceId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_ENDPOINT_ID, endpointId);
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_SESSION_ID, webSocketSession.getSessionId());
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_MESSAGE_TYPE, TEXT.name());
        runner.enqueue(textMessageFromServer, attributes);
        // Enqueue 2nd file as Binary.
        attributes.put(WebSocketProcessorAttributes.ATTR_WS_MESSAGE_TYPE, BINARY.name());
        runner.enqueue(textMessageFromServer.getBytes(), attributes);
        runner.run(2);
        final List<MockFlowFile> succeededFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, succeededFlowFiles.size());
        assertFlowFile(webSocketSession, serviceId, endpointId, succeededFlowFiles.get(0), TEXT);
        assertFlowFile(webSocketSession, serviceId, endpointId, succeededFlowFiles.get(1), BINARY);
        final List<MockFlowFile> failedFlowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        final List<ProvenanceEventRecord> provenanceEvents = runner.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
    }
}

