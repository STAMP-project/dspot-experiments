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
package org.apache.nifi.remote;


import CoreAttributes.UUID;
import Relationship.ANONYMOUS;
import SiteToSiteAttributes.S2S_ADDRESS;
import SiteToSiteAttributes.S2S_HOST;
import SiteToSiteAttributes.S2S_PORT_ID;
import SiteToSiteTransportProtocol.HTTP;
import SiteToSiteTransportProtocol.RAW;
import TransferDirection.RECEIVE;
import TransferDirection.SEND;
import java.io.ByteArrayInputStream;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.controller.ProcessScheduler;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.groups.RemoteProcessGroup;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.remote.io.http.HttpCommunicationsSession;
import org.apache.nifi.remote.io.socket.SocketChannelCommunicationsSession;
import org.apache.nifi.remote.protocol.CommunicationsSession;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.util.StandardDataPacket;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.MockProcessSession;
import org.apache.nifi.util.SharedSessionState;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestStandardRemoteGroupPort {
    private static final String ID = "remote-group-port-id";

    private static final String NAME = "remote-group-port-name";

    private RemoteProcessGroup remoteGroup;

    private ProcessScheduler scheduler;

    private SiteToSiteClient siteToSiteClient;

    private Transaction transaction;

    private EventReporter eventReporter;

    private ProcessGroup processGroup;

    private static final String REMOTE_CLUSTER_URL = "http://node0.example.com:8080/nifi";

    private StandardRemoteGroupPort port;

    private SharedSessionState sessionState;

    private MockProcessSession processSession;

    private MockProcessContext processContext;

    @Test
    public void testSendRaw() throws Exception {
        setupMock(RAW, SEND);
        setupMockProcessSession();
        final String peerUrl = "nifi://node1.example.com:9090";
        final PeerDescription peerDescription = new PeerDescription("node1.example.com", 9090, true);
        try (final SocketChannel socketChannel = SocketChannel.open()) {
            final CommunicationsSession commsSession = new SocketChannelCommunicationsSession(socketChannel);
            commsSession.setUserDn("nifi.node1.example.com");
            final Peer peer = new Peer(peerDescription, commsSession, peerUrl, TestStandardRemoteGroupPort.REMOTE_CLUSTER_URL);
            Mockito.doReturn(peer).when(transaction).getCommunicant();
            final MockFlowFile flowFile = processSession.createFlowFile("0123456789".getBytes());
            sessionState.getFlowFileQueue().offer(flowFile);
            port.onTrigger(processContext, processSession);
            // Assert provenance.
            final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
            Assert.assertEquals(1, provenanceEvents.size());
            final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
            Assert.assertEquals(ProvenanceEventType.SEND, provenanceEvent.getEventType());
            Assert.assertEquals(((peerUrl + "/") + (flowFile.getAttribute(UUID.key()))), provenanceEvent.getTransitUri());
            Assert.assertEquals("Remote DN=nifi.node1.example.com", provenanceEvent.getDetails());
            Assert.assertEquals("remote-group-port-id", provenanceEvent.getAttribute(S2S_PORT_ID.key()));
        }
    }

    @Test
    public void testReceiveRaw() throws Exception {
        setupMock(RAW, RECEIVE);
        setupMockProcessSession();
        final String peerUrl = "nifi://node1.example.com:9090";
        final PeerDescription peerDescription = new PeerDescription("node1.example.com", 9090, true);
        try (final SocketChannel socketChannel = SocketChannel.open()) {
            final CommunicationsSession commsSession = new SocketChannelCommunicationsSession(socketChannel);
            commsSession.setUserDn("nifi.node1.example.com");
            final Peer peer = new Peer(peerDescription, commsSession, peerUrl, TestStandardRemoteGroupPort.REMOTE_CLUSTER_URL);
            Mockito.doReturn(peer).when(transaction).getCommunicant();
            final String sourceFlowFileUuid = "flowfile-uuid";
            final Map<String, String> attributes = new HashMap<>();
            attributes.put(UUID.key(), sourceFlowFileUuid);
            final byte[] dataPacketContents = "DataPacket Contents".getBytes();
            final ByteArrayInputStream dataPacketInputStream = new ByteArrayInputStream(dataPacketContents);
            final DataPacket dataPacket = new StandardDataPacket(attributes, dataPacketInputStream, dataPacketContents.length);
            // Return null when it gets called second time.
            Mockito.doReturn(dataPacket).doReturn(null).when(this.transaction).receive();
            port.onTrigger(processContext, processSession);
            // Assert provenance.
            final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
            Assert.assertEquals(1, provenanceEvents.size());
            final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
            Assert.assertEquals(ProvenanceEventType.RECEIVE, provenanceEvent.getEventType());
            Assert.assertEquals(((peerUrl + "/") + sourceFlowFileUuid), provenanceEvent.getTransitUri());
            Assert.assertEquals("Remote DN=nifi.node1.example.com", provenanceEvent.getDetails());
            // Assert received flow files.
            processSession.assertAllFlowFilesTransferred(ANONYMOUS);
            final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(ANONYMOUS);
            Assert.assertEquals(1, flowFiles.size());
            final MockFlowFile flowFile = flowFiles.get(0);
            flowFile.assertAttributeEquals(S2S_HOST.key(), peer.getHost());
            flowFile.assertAttributeEquals(S2S_ADDRESS.key(), (((peer.getHost()) + ":") + (peer.getPort())));
            flowFile.assertAttributeEquals(S2S_PORT_ID.key(), "remote-group-port-id");
        }
    }

    @Test
    public void testSendHttp() throws Exception {
        setupMock(HTTP, SEND);
        setupMockProcessSession();
        final String peerUrl = "https://node1.example.com:8080/nifi";
        final PeerDescription peerDescription = new PeerDescription("node1.example.com", 8080, true);
        final HttpCommunicationsSession commsSession = new HttpCommunicationsSession();
        commsSession.setUserDn("nifi.node1.example.com");
        final Peer peer = new Peer(peerDescription, commsSession, peerUrl, TestStandardRemoteGroupPort.REMOTE_CLUSTER_URL);
        final String flowFileEndpointUri = "https://node1.example.com:8080/nifi-api/output-ports/port-id/transactions/transaction-id/flow-files";
        Mockito.doReturn(peer).when(transaction).getCommunicant();
        commsSession.setDataTransferUrl(flowFileEndpointUri);
        final MockFlowFile flowFile = processSession.createFlowFile("0123456789".getBytes());
        sessionState.getFlowFileQueue().offer(flowFile);
        port.onTrigger(processContext, processSession);
        // Assert provenance.
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
        Assert.assertEquals(ProvenanceEventType.SEND, provenanceEvent.getEventType());
        Assert.assertEquals(flowFileEndpointUri, provenanceEvent.getTransitUri());
        Assert.assertEquals("Remote DN=nifi.node1.example.com", provenanceEvent.getDetails());
        Assert.assertEquals("remote-group-port-id", provenanceEvent.getAttribute(S2S_PORT_ID.key()));
    }

    @Test
    public void testSendBatchByCount() throws Exception {
        final SiteToSiteClientConfig siteToSiteClientConfig = new SiteToSiteClient.Builder().requestBatchCount(2).buildConfig();
        setupMock(HTTP, SEND, siteToSiteClientConfig);
        // t1 = {0, 1}, t2 = {2, 3}, t3 = {4}
        final int[] expectedNumberOfPackets = new int[]{ 2, 2, 1 };
        testSendBatch(expectedNumberOfPackets);
    }

    @Test
    public void testSendBatchBySize() throws Exception {
        final SiteToSiteClientConfig siteToSiteClientConfig = new SiteToSiteClient.Builder().requestBatchSize(30).buildConfig();
        setupMock(HTTP, SEND, siteToSiteClientConfig);
        // t1 = {10, 11, 12}, t2 = {13, 14}
        final int[] expectedNumberOfPackets = new int[]{ 3, 2 };
        testSendBatch(expectedNumberOfPackets);
    }

    @Test
    public void testSendBatchByDuration() throws Exception {
        final SiteToSiteClientConfig siteToSiteClientConfig = new SiteToSiteClient.Builder().requestBatchDuration(1, TimeUnit.NANOSECONDS).buildConfig();
        setupMock(HTTP, SEND, siteToSiteClientConfig);
        // t1 = {1}, t2 = {2} .. and so on.
        final int[] expectedNumberOfPackets = new int[]{ 1, 1, 1, 1, 1 };
        testSendBatch(expectedNumberOfPackets);
    }

    @Test
    public void testReceiveHttp() throws Exception {
        setupMock(HTTP, RECEIVE);
        setupMockProcessSession();
        final String peerUrl = "https://node1.example.com:8080/nifi";
        final PeerDescription peerDescription = new PeerDescription("node1.example.com", 8080, true);
        final HttpCommunicationsSession commsSession = new HttpCommunicationsSession();
        commsSession.setUserDn("nifi.node1.example.com");
        final Peer peer = new Peer(peerDescription, commsSession, peerUrl, TestStandardRemoteGroupPort.REMOTE_CLUSTER_URL);
        final String flowFileEndpointUri = "https://node1.example.com:8080/nifi-api/output-ports/port-id/transactions/transaction-id/flow-files";
        Mockito.doReturn(peer).when(transaction).getCommunicant();
        commsSession.setDataTransferUrl(flowFileEndpointUri);
        final Map<String, String> attributes = new HashMap<>();
        final byte[] dataPacketContents = "DataPacket Contents".getBytes();
        final ByteArrayInputStream dataPacketInputStream = new ByteArrayInputStream(dataPacketContents);
        final DataPacket dataPacket = new StandardDataPacket(attributes, dataPacketInputStream, dataPacketContents.length);
        // Return null when it gets called second time.
        Mockito.doReturn(dataPacket).doReturn(null).when(transaction).receive();
        port.onTrigger(processContext, processSession);
        // Assert provenance.
        final List<ProvenanceEventRecord> provenanceEvents = sessionState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent = provenanceEvents.get(0);
        Assert.assertEquals(ProvenanceEventType.RECEIVE, provenanceEvent.getEventType());
        Assert.assertEquals(flowFileEndpointUri, provenanceEvent.getTransitUri());
        Assert.assertEquals("Remote DN=nifi.node1.example.com", provenanceEvent.getDetails());
        // Assert received flow files.
        processSession.assertAllFlowFilesTransferred(ANONYMOUS);
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(ANONYMOUS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile flowFile = flowFiles.get(0);
        flowFile.assertAttributeEquals(S2S_HOST.key(), peer.getHost());
        flowFile.assertAttributeEquals(S2S_ADDRESS.key(), (((peer.getHost()) + ":") + (peer.getPort())));
        flowFile.assertAttributeEquals(S2S_PORT_ID.key(), "remote-group-port-id");
    }
}

