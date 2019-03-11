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
package org.apache.nifi.remote.protocol.http;


import HandshakeProperty.BATCH_COUNT;
import HandshakeProperty.PORT_IDENTIFIER;
import ProvenanceEventType.RECEIVE;
import ProvenanceEventType.SEND;
import Relationship.ANONYMOUS;
import ResponseCode.BAD_CHECKSUM;
import ResponseCode.CONFIRM_TRANSACTION;
import ResponseCode.MISSING_PROPERTY;
import ResponseCode.PORTS_DESTINATION_FULL;
import ResponseCode.PORT_NOT_IN_VALID_STATE;
import ResponseCode.UNAUTHORIZED;
import ResponseCode.UNKNOWN_PORT;
import SiteToSiteAttributes.S2S_ADDRESS;
import SiteToSiteAttributes.S2S_HOST;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.nifi.connectable.Connection;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.remote.Peer;
import org.apache.nifi.remote.PortAuthorizationResult;
import org.apache.nifi.remote.RootGroupPort;
import org.apache.nifi.remote.codec.FlowFileCodec;
import org.apache.nifi.remote.codec.StandardFlowFileCodec;
import org.apache.nifi.remote.exception.HandshakeException;
import org.apache.nifi.remote.io.http.HttpServerCommunicationsSession;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.MockProcessSession;
import org.apache.nifi.util.SharedSessionState;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestHttpFlowFileServerProtocol {
    private SharedSessionState sessionState;

    private MockProcessSession processSession;

    private MockProcessContext processContext;

    @Test
    public void testIllegalHandshakeProperty() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        getHandshakeParams().clear();
        try {
            serverProtocol.handshake(peer);
            Assert.fail();
        } catch (final HandshakeException e) {
            Assert.assertEquals(MISSING_PROPERTY, e.getResponseCode());
        }
        Assert.assertFalse(serverProtocol.isHandshakeSuccessful());
    }

    @Test
    public void testUnknownPort() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        ((HttpServerCommunicationsSession) (peer.getCommunicationsSession())).putHandshakeParam(PORT_IDENTIFIER, "port-identifier");
        final ProcessGroup processGroup = Mockito.mock(ProcessGroup.class);
        Mockito.doReturn(true).when(processGroup).isRootGroup();
        serverProtocol.setRootProcessGroup(processGroup);
        try {
            serverProtocol.handshake(peer);
            Assert.fail();
        } catch (final HandshakeException e) {
            Assert.assertEquals(UNKNOWN_PORT, e.getResponseCode());
        }
        Assert.assertFalse(serverProtocol.isHandshakeSuccessful());
    }

    @Test
    public void testUnauthorized() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        ((HttpServerCommunicationsSession) (peer.getCommunicationsSession())).putHandshakeParam(PORT_IDENTIFIER, "port-identifier");
        final ProcessGroup processGroup = Mockito.mock(ProcessGroup.class);
        final RootGroupPort port = Mockito.mock(RootGroupPort.class);
        final PortAuthorizationResult authResult = Mockito.mock(PortAuthorizationResult.class);
        Mockito.doReturn(true).when(processGroup).isRootGroup();
        Mockito.doReturn(port).when(processGroup).getOutputPort("port-identifier");
        Mockito.doReturn(authResult).when(port).checkUserAuthorization(ArgumentMatchers.any(String.class));
        serverProtocol.setRootProcessGroup(processGroup);
        try {
            serverProtocol.handshake(peer);
            Assert.fail();
        } catch (final HandshakeException e) {
            Assert.assertEquals(UNAUTHORIZED, e.getResponseCode());
        }
        Assert.assertFalse(serverProtocol.isHandshakeSuccessful());
    }

    @Test
    public void testPortNotInValidState() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        ((HttpServerCommunicationsSession) (peer.getCommunicationsSession())).putHandshakeParam(PORT_IDENTIFIER, "port-identifier");
        final ProcessGroup processGroup = Mockito.mock(ProcessGroup.class);
        final RootGroupPort port = Mockito.mock(RootGroupPort.class);
        final PortAuthorizationResult authResult = Mockito.mock(PortAuthorizationResult.class);
        Mockito.doReturn(true).when(processGroup).isRootGroup();
        Mockito.doReturn(port).when(processGroup).getOutputPort("port-identifier");
        Mockito.doReturn(authResult).when(port).checkUserAuthorization(ArgumentMatchers.any(String.class));
        Mockito.doReturn(true).when(authResult).isAuthorized();
        serverProtocol.setRootProcessGroup(processGroup);
        try {
            serverProtocol.handshake(peer);
            Assert.fail();
        } catch (final HandshakeException e) {
            Assert.assertEquals(PORT_NOT_IN_VALID_STATE, e.getResponseCode());
        }
        Assert.assertFalse(serverProtocol.isHandshakeSuccessful());
    }

    @Test
    public void testPortDestinationFull() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        ((HttpServerCommunicationsSession) (peer.getCommunicationsSession())).putHandshakeParam(PORT_IDENTIFIER, "port-identifier");
        final ProcessGroup processGroup = Mockito.mock(ProcessGroup.class);
        final RootGroupPort port = Mockito.mock(RootGroupPort.class);
        final PortAuthorizationResult authResult = Mockito.mock(PortAuthorizationResult.class);
        Mockito.doReturn(true).when(processGroup).isRootGroup();
        Mockito.doReturn(port).when(processGroup).getOutputPort("port-identifier");
        Mockito.doReturn(authResult).when(port).checkUserAuthorization(ArgumentMatchers.any(String.class));
        Mockito.doReturn(true).when(authResult).isAuthorized();
        Mockito.doReturn(true).when(port).isValid();
        Mockito.doReturn(true).when(port).isRunning();
        final Set<Connection> connections = new HashSet<>();
        final Connection connection = Mockito.mock(Connection.class);
        connections.add(connection);
        Mockito.doReturn(connections).when(port).getConnections();
        final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.doReturn(flowFileQueue).when(connection).getFlowFileQueue();
        Mockito.doReturn(true).when(flowFileQueue).isFull();
        serverProtocol.setRootProcessGroup(processGroup);
        try {
            serverProtocol.handshake(peer);
            Assert.fail();
        } catch (final HandshakeException e) {
            Assert.assertEquals(PORTS_DESTINATION_FULL, e.getResponseCode());
        }
        Assert.assertFalse(serverProtocol.isHandshakeSuccessful());
    }

    @Test
    public void testShutdown() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        serverProtocol.handshake(peer);
        Assert.assertTrue(serverProtocol.isHandshakeSuccessful());
        final FlowFileCodec negotiatedCoded = serverProtocol.negotiateCodec(peer);
        Assert.assertTrue((negotiatedCoded instanceof StandardFlowFileCodec));
        Assert.assertEquals(negotiatedCoded, serverProtocol.getPreNegotiatedCodec());
        Assert.assertEquals(1234, serverProtocol.getRequestExpiration());
        serverProtocol.shutdown(peer);
        final ProcessContext context = null;
        final ProcessSession processSession = null;
        try {
            serverProtocol.transferFlowFiles(peer, context, processSession, negotiatedCoded);
            Assert.fail("transferFlowFiles should fail since it's already shutdown.");
        } catch (final IllegalStateException e) {
        }
        try {
            serverProtocol.receiveFlowFiles(peer, context, processSession, negotiatedCoded);
            Assert.fail("receiveFlowFiles should fail since it's already shutdown.");
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void testTransferZeroFile() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer();
        serverProtocol.handshake(peer);
        Assert.assertTrue(serverProtocol.isHandshakeSuccessful());
        final FlowFileCodec negotiatedCoded = serverProtocol.negotiateCodec(peer);
        final ProcessContext context = null;
        final ProcessSession processSession = Mockito.mock(ProcessSession.class);
        // Execute test using mock
        final int flowFileSent = serverProtocol.transferFlowFiles(peer, context, processSession, negotiatedCoded);
        Assert.assertEquals(0, flowFileSent);
    }

    @Test
    public void testTransferOneFile() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final String transactionId = "testTransferOneFile";
        final Peer peer = getDefaultPeer(transactionId);
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        final String endpointUri = ("https://remote-host:8443/nifi-api/output-ports/port-id/transactions/" + transactionId) + "/flow-files";
        commsSession.putHandshakeParam(BATCH_COUNT, "1");
        commsSession.setUserDn("unit-test");
        commsSession.setDataTransferUrl(endpointUri);
        transferFlowFiles(serverProtocol, transactionId, peer, ( processSession) -> {
            final MockFlowFile flowFile = processSession.createFlowFile("Server content".getBytes());
            final HashMap<String, String> attributes = new HashMap<>();
            attributes.put("uuid", "server-uuid");
            attributes.put("filename", "server-filename");
            attributes.put("server-attr-1", "server-attr-1-value");
            attributes.put("server-attr-2", "server-attr-2-value");
            flowFile.putAttributes(attributes);
            return Arrays.asList(flowFile);
        });
        // Commit transaction
        final int flowFileSent = serverProtocol.commitTransferTransaction(peer, "3229577812");
        Assert.assertEquals(1, flowFileSent);
        // Assert provenance
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
        Assert.assertEquals(SEND, provenanceEvent.getEventType());
        Assert.assertEquals(endpointUri, provenanceEvent.getTransitUri());
        Assert.assertEquals("Remote Host=peer-host, Remote DN=unit-test", provenanceEvent.getDetails());
    }

    @Test
    public void testTransferOneFileBadChecksum() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final String transactionId = "testTransferOneFileBadChecksum";
        final Peer peer = getDefaultPeer(transactionId);
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        final String endpointUri = ("https://remote-host:8443/nifi-api/output-ports/port-id/transactions/" + transactionId) + "/flow-files";
        commsSession.putHandshakeParam(BATCH_COUNT, "1");
        commsSession.setUserDn("unit-test");
        commsSession.setDataTransferUrl(endpointUri);
        transferFlowFiles(serverProtocol, transactionId, peer, ( processSession) -> {
            final MockFlowFile flowFile = processSession.createFlowFile("Server content".getBytes());
            final HashMap<String, String> attributes = new HashMap<>();
            attributes.put("uuid", "server-uuid");
            attributes.put("filename", "server-filename");
            attributes.put("server-attr-1", "server-attr-1-value");
            attributes.put("server-attr-2", "server-attr-2-value");
            flowFile.putAttributes(attributes);
            return Arrays.asList(flowFile);
        });
        // Commit transaction
        try {
            serverProtocol.commitTransferTransaction(peer, "client-sent-wrong-checksum");
            Assert.fail();
        } catch (final IOException e) {
            Assert.assertTrue(e.getMessage().contains("CRC32 Checksum"));
        }
    }

    @Test
    public void testTransferTwoFiles() throws Exception {
        final String transactionId = "testTransferTwoFiles";
        final Peer peer = getDefaultPeer(transactionId);
        final String endpointUri = ("https://remote-host:8443/nifi-api/output-ports/port-id/transactions/" + transactionId) + "/flow-files";
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        commsSession.putHandshakeParam(BATCH_COUNT, "2");
        commsSession.setUserDn("unit-test");
        commsSession.setDataTransferUrl(endpointUri);
        transferFlowFiles(serverProtocol, transactionId, peer, ( processSession) -> IntStream.of(1, 2).mapToObj(( i) -> {
            final MockFlowFile flowFile = processSession.createFlowFile(("Server content " + i).getBytes());
            final HashMap<String, String> attributes = new HashMap<>();
            attributes.put("uuid", ("server-uuid-" + i));
            attributes.put("filename", ("server-filename-" + i));
            attributes.put((("server-attr-" + i) + "-1"), (("server-attr-" + i) + "-1-value"));
            attributes.put((("server-attr-" + i) + "-2"), (("server-attr-" + i) + "-2-value"));
            flowFile.putAttributes(attributes);
            return flowFile;
        }).collect(Collectors.toList()));
        // Commit transaction
        final int flowFileSent = serverProtocol.commitTransferTransaction(peer, "3058746557");
        Assert.assertEquals(2, flowFileSent);
        // Assert provenance
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
        for (final ProvenanceEventRecord provenanceEvent : provenanceEvents) {
            Assert.assertEquals(SEND, provenanceEvent.getEventType());
            Assert.assertEquals(endpointUri, provenanceEvent.getTransitUri());
            Assert.assertEquals("Remote Host=peer-host, Remote DN=unit-test", provenanceEvent.getDetails());
        }
    }

    @Test
    public void testReceiveZeroFile() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer("testReceiveZeroFile");
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        commsSession.setUserDn("unit-test");
        serverProtocol.handshake(peer);
        Assert.assertTrue(serverProtocol.isHandshakeSuccessful());
        final FlowFileCodec negotiatedCoded = serverProtocol.negotiateCodec(peer);
        final ProcessContext context = null;
        final ProcessSession processSession = Mockito.mock(ProcessSession.class);
        final InputStream httpInputStream = new ByteArrayInputStream(new byte[]{  });
        setInputStream(httpInputStream);
        // Execute test using mock
        final int flowFileReceived = serverProtocol.receiveFlowFiles(peer, context, processSession, negotiatedCoded);
        Assert.assertEquals(0, flowFileReceived);
    }

    @Test
    public void testReceiveOneFile() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final String transactionId = "testReceiveOneFile";
        final String endpointUri = ("https://remote-host:8443/nifi-api/input-ports/port-id/transactions/" + transactionId) + "/flow-files";
        final Peer peer = getDefaultPeer(transactionId);
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        commsSession.putHandshakeParam(BATCH_COUNT, "1");
        commsSession.setUserDn("unit-test");
        commsSession.setDataTransferUrl(endpointUri);
        final DataPacket dataPacket = createClientDataPacket();
        receiveFlowFiles(serverProtocol, transactionId, peer, dataPacket);
        // Commit transaction
        commsSession.setResponseCode(CONFIRM_TRANSACTION);
        final int flowFileReceived = serverProtocol.commitReceiveTransaction(peer);
        Assert.assertEquals(1, flowFileReceived);
        // Assert provenance.
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent.getEventType());
        Assert.assertEquals(endpointUri, provenanceEvent.getTransitUri());
        Assert.assertEquals("Remote Host=peer-host, Remote DN=unit-test", provenanceEvent.getDetails());
        // Assert received flow files.
        processSession.assertAllFlowFilesTransferred(ANONYMOUS);
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(ANONYMOUS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile flowFile = flowFiles.get(0);
        flowFile.assertAttributeEquals(S2S_HOST.key(), peer.getHost());
        flowFile.assertAttributeEquals(S2S_ADDRESS.key(), (((peer.getHost()) + ":") + (peer.getPort())));
        flowFile.assertAttributeEquals("client-attr-1", "client-attr-1-value");
        flowFile.assertAttributeEquals("client-attr-2", "client-attr-2-value");
    }

    @Test
    public void testReceiveOneFileBadChecksum() throws Exception {
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final String transactionId = "testReceiveOneFileBadChecksum";
        final Peer peer = getDefaultPeer(transactionId);
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        receiveFlowFiles(serverProtocol, transactionId, peer, createClientDataPacket());
        // Commit transaction
        commsSession.setResponseCode(BAD_CHECKSUM);
        try {
            serverProtocol.commitReceiveTransaction(peer);
            Assert.fail();
        } catch (final IOException e) {
            Assert.assertTrue(e.getMessage().contains("Received a BadChecksum response"));
        }
    }

    @Test
    public void testReceiveTwoFiles() throws Exception {
        final String transactionId = "testReceiveTwoFile";
        final String endpointUri = ("https://remote-host:8443/nifi-api/input-ports/port-id/transactions/" + transactionId) + "/flow-files";
        final HttpFlowFileServerProtocol serverProtocol = getDefaultHttpFlowFileServerProtocol();
        final Peer peer = getDefaultPeer(transactionId);
        final HttpServerCommunicationsSession commsSession = ((HttpServerCommunicationsSession) (peer.getCommunicationsSession()));
        commsSession.putHandshakeParam(BATCH_COUNT, "2");
        commsSession.setUserDn("unit-test");
        commsSession.setDataTransferUrl(endpointUri);
        receiveFlowFiles(serverProtocol, transactionId, peer, createClientDataPacket(), createClientDataPacket());
        // Commit transaction
        commsSession.setResponseCode(CONFIRM_TRANSACTION);
        final int flowFileReceived = serverProtocol.commitReceiveTransaction(peer);
        Assert.assertEquals(2, flowFileReceived);
        // Assert provenance.
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
        for (final ProvenanceEventRecord provenanceEvent : provenanceEvents) {
            Assert.assertEquals(RECEIVE, provenanceEvent.getEventType());
            Assert.assertEquals(endpointUri, provenanceEvent.getTransitUri());
            Assert.assertEquals("Remote Host=peer-host, Remote DN=unit-test", provenanceEvent.getDetails());
        }
        // Assert received flow files.
        processSession.assertAllFlowFilesTransferred(ANONYMOUS);
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(ANONYMOUS);
        Assert.assertEquals(2, flowFiles.size());
        for (final MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeEquals(S2S_HOST.key(), peer.getHost());
            flowFile.assertAttributeEquals(S2S_ADDRESS.key(), (((peer.getHost()) + ":") + (peer.getPort())));
            flowFile.assertAttributeEquals("client-attr-1", "client-attr-1-value");
            flowFile.assertAttributeEquals("client-attr-2", "client-attr-2-value");
        }
    }
}

