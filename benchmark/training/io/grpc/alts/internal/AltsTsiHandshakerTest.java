/**
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.alts.internal;


import AltsTsiHandshaker.TSI_SERVICE_ACCOUNT_PEER_PROPERTY;
import RpcProtocolVersions.Version;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AltsTsiHandshaker}.
 */
@RunWith(JUnit4.class)
public class AltsTsiHandshakerTest {
    private static final String TEST_KEY_DATA = "super secret 123";

    private static final String TEST_APPLICATION_PROTOCOL = "grpc";

    private static final String TEST_RECORD_PROTOCOL = "ALTSRP_GCM_AES128";

    private static final String TEST_CLIENT_SERVICE_ACCOUNT = "client@developer.gserviceaccount.com";

    private static final String TEST_SERVER_SERVICE_ACCOUNT = "server@developer.gserviceaccount.com";

    private static final int OUT_FRAME_SIZE = 100;

    private static final int TRANSPORT_BUFFER_SIZE = 200;

    private static final int TEST_MAX_RPC_VERSION_MAJOR = 3;

    private static final int TEST_MAX_RPC_VERSION_MINOR = 2;

    private static final int TEST_MIN_RPC_VERSION_MAJOR = 2;

    private static final int TEST_MIN_RPC_VERSION_MINOR = 1;

    private static final RpcProtocolVersions TEST_RPC_PROTOCOL_VERSIONS = RpcProtocolVersions.newBuilder().setMaxRpcVersion(Version.newBuilder().setMajor(AltsTsiHandshakerTest.TEST_MAX_RPC_VERSION_MAJOR).setMinor(AltsTsiHandshakerTest.TEST_MAX_RPC_VERSION_MINOR).build()).setMinRpcVersion(Version.newBuilder().setMajor(AltsTsiHandshakerTest.TEST_MIN_RPC_VERSION_MAJOR).setMinor(AltsTsiHandshakerTest.TEST_MIN_RPC_VERSION_MINOR).build()).build();

    private AltsHandshakerClient mockClient;

    private AltsHandshakerClient mockServer;

    private AltsTsiHandshaker handshakerClient;

    private AltsTsiHandshaker handshakerServer;

    @Test
    public void processBytesFromPeerFalseStart() throws Exception {
        Mockito.verify(mockClient, Mockito.never()).startClientHandshake();
        Mockito.verify(mockClient, Mockito.never()).startServerHandshake(Matchers.<ByteBuffer>any());
        Mockito.verify(mockClient, Mockito.never()).next(Matchers.<ByteBuffer>any());
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        Assert.assertTrue(handshakerClient.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void processBytesFromPeerStartServer() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        Mockito.verify(mockServer, Mockito.never()).startClientHandshake();
        Mockito.verify(mockServer, Mockito.never()).next(Matchers.<ByteBuffer>any());
        // Mock transport buffer all consumed by processBytesFromPeer and there is an output frame.
        transportBuffer.position(transportBuffer.limit());
        Mockito.when(mockServer.startServerHandshake(transportBuffer)).thenReturn(outputFrame);
        Mockito.when(mockServer.isFinished()).thenReturn(false);
        Assert.assertTrue(handshakerServer.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void processBytesFromPeerStartServerEmptyOutput() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer emptyOutputFrame = ByteBuffer.allocate(0);
        Mockito.verify(mockServer, Mockito.never()).startClientHandshake();
        Mockito.verify(mockServer, Mockito.never()).next(Matchers.<ByteBuffer>any());
        // Mock transport buffer all consumed by processBytesFromPeer and output frame is empty.
        // Expect processBytesFromPeer return False, because more data are needed from the peer.
        transportBuffer.position(transportBuffer.limit());
        Mockito.when(mockServer.startServerHandshake(transportBuffer)).thenReturn(emptyOutputFrame);
        Mockito.when(mockServer.isFinished()).thenReturn(false);
        Assert.assertFalse(handshakerServer.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void processBytesFromPeerStartServerFinished() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        Mockito.verify(mockServer, Mockito.never()).startClientHandshake();
        Mockito.verify(mockServer, Mockito.never()).next(Matchers.<ByteBuffer>any());
        // Mock handshake complete after processBytesFromPeer.
        Mockito.when(mockServer.startServerHandshake(transportBuffer)).thenReturn(outputFrame);
        Mockito.when(mockServer.isFinished()).thenReturn(true);
        Assert.assertTrue(handshakerServer.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void processBytesFromPeerNoBytesConsumed() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer emptyOutputFrame = ByteBuffer.allocate(0);
        Mockito.verify(mockServer, Mockito.never()).startClientHandshake();
        Mockito.verify(mockServer, Mockito.never()).next(Matchers.<ByteBuffer>any());
        Mockito.when(mockServer.startServerHandshake(transportBuffer)).thenReturn(emptyOutputFrame);
        Mockito.when(mockServer.isFinished()).thenReturn(false);
        try {
            Assert.assertTrue(handshakerServer.processBytesFromPeer(transportBuffer));
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Handshaker did not consume any bytes.", expected.getMessage());
        }
    }

    @Test
    public void processBytesFromPeerClientNext() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        Mockito.verify(mockClient, Mockito.never()).startServerHandshake(Matchers.<ByteBuffer>any());
        Mockito.when(mockClient.startClientHandshake()).thenReturn(outputFrame);
        Mockito.when(mockClient.next(transportBuffer)).thenReturn(outputFrame);
        Mockito.when(mockClient.isFinished()).thenReturn(false);
        handshakerClient.getBytesToSendToPeer(transportBuffer);
        transportBuffer.position(transportBuffer.limit());
        Assert.assertFalse(handshakerClient.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void processBytesFromPeerClientNextFinished() throws Exception {
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        Mockito.verify(mockClient, Mockito.never()).startServerHandshake(Matchers.<ByteBuffer>any());
        Mockito.when(mockClient.startClientHandshake()).thenReturn(outputFrame);
        Mockito.when(mockClient.next(transportBuffer)).thenReturn(outputFrame);
        Mockito.when(mockClient.isFinished()).thenReturn(true);
        handshakerClient.getBytesToSendToPeer(transportBuffer);
        Assert.assertTrue(handshakerClient.processBytesFromPeer(transportBuffer));
    }

    @Test
    public void extractPeerFailure() throws Exception {
        Mockito.when(mockClient.isFinished()).thenReturn(false);
        try {
            handshakerClient.extractPeer();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Handshake is not complete.", expected.getMessage());
        }
    }

    @Test
    public void extractPeerObjectFailure() throws Exception {
        Mockito.when(mockClient.isFinished()).thenReturn(false);
        try {
            handshakerClient.extractPeerObject();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertEquals("Handshake is not complete.", expected.getMessage());
        }
    }

    @Test
    public void extractClientPeerSuccess() throws Exception {
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        Mockito.when(mockClient.startClientHandshake()).thenReturn(outputFrame);
        Mockito.when(mockClient.isFinished()).thenReturn(true);
        Mockito.when(mockClient.getResult()).thenReturn(/* isClient = */
        getHandshakerResult(true));
        handshakerClient.getBytesToSendToPeer(transportBuffer);
        TsiPeer clientPeer = handshakerClient.extractPeer();
        Assert.assertEquals(1, clientPeer.getProperties().size());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_SERVER_SERVICE_ACCOUNT, clientPeer.getProperty(TSI_SERVICE_ACCOUNT_PEER_PROPERTY).getValue());
        AltsAuthContext clientContext = ((AltsAuthContext) (handshakerClient.extractPeerObject()));
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_APPLICATION_PROTOCOL, clientContext.getApplicationProtocol());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_RECORD_PROTOCOL, clientContext.getRecordProtocol());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_SERVER_SERVICE_ACCOUNT, clientContext.getPeerServiceAccount());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_CLIENT_SERVICE_ACCOUNT, clientContext.getLocalServiceAccount());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_RPC_PROTOCOL_VERSIONS, clientContext.getPeerRpcVersions());
    }

    @Test
    public void extractServerPeerSuccess() throws Exception {
        ByteBuffer outputFrame = ByteBuffer.allocate(AltsTsiHandshakerTest.OUT_FRAME_SIZE);
        ByteBuffer transportBuffer = ByteBuffer.allocate(AltsTsiHandshakerTest.TRANSPORT_BUFFER_SIZE);
        Mockito.when(mockServer.startServerHandshake(Matchers.<ByteBuffer>any())).thenReturn(outputFrame);
        Mockito.when(mockServer.isFinished()).thenReturn(true);
        Mockito.when(mockServer.getResult()).thenReturn(/* isClient = */
        getHandshakerResult(false));
        handshakerServer.processBytesFromPeer(transportBuffer);
        handshakerServer.getBytesToSendToPeer(transportBuffer);
        TsiPeer serverPeer = handshakerServer.extractPeer();
        Assert.assertEquals(1, serverPeer.getProperties().size());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_CLIENT_SERVICE_ACCOUNT, serverPeer.getProperty(TSI_SERVICE_ACCOUNT_PEER_PROPERTY).getValue());
        AltsAuthContext serverContext = ((AltsAuthContext) (handshakerServer.extractPeerObject()));
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_APPLICATION_PROTOCOL, serverContext.getApplicationProtocol());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_RECORD_PROTOCOL, serverContext.getRecordProtocol());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_CLIENT_SERVICE_ACCOUNT, serverContext.getPeerServiceAccount());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_SERVER_SERVICE_ACCOUNT, serverContext.getLocalServiceAccount());
        Assert.assertEquals(AltsTsiHandshakerTest.TEST_RPC_PROTOCOL_VERSIONS, serverContext.getPeerRpcVersions());
    }
}

