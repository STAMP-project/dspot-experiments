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


import HandshakeProtocol.ALTS;
import RpcProtocolVersions.Version;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AltsHandshakerClient}.
 */
@RunWith(JUnit4.class)
public class AltsHandshakerClientTest {
    private static final int IN_BYTES_SIZE = 100;

    private static final int BYTES_CONSUMED = 30;

    private static final int PREFIX_POSITION = 20;

    private static final String TEST_TARGET_NAME = "target name";

    private static final String TEST_TARGET_SERVICE_ACCOUNT = "peer service account";

    private AltsHandshakerStub mockStub;

    private AltsHandshakerClient handshaker;

    private AltsClientOptions clientOptions;

    @Test
    public void startClientHandshakeFailure() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getErrorResponse());
        try {
            handshaker.startClientHandshake();
            Assert.fail("Exception expected");
        } catch (GeneralSecurityException ex) {
            assertThat(ex).hasMessageThat().contains(MockAltsHandshakerResp.getTestErrorDetails());
        }
    }

    @Test
    public void startClientHandshakeSuccess() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(0));
        ByteBuffer outFrame = handshaker.startClientHandshake();
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
    }

    @Test
    public void startClientHandshakeWithOptions() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(0));
        ByteBuffer outFrame = handshaker.startClientHandshake();
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        HandshakerReq req = HandshakerReq.newBuilder().setClientStart(StartClientHandshakeReq.newBuilder().setHandshakeSecurityProtocol(ALTS).addApplicationProtocols(AltsHandshakerClient.getApplicationProtocol()).addRecordProtocols(AltsHandshakerClient.getRecordProtocol()).setTargetName(AltsHandshakerClientTest.TEST_TARGET_NAME).addTargetIdentities(Identity.newBuilder().setServiceAccount(AltsHandshakerClientTest.TEST_TARGET_SERVICE_ACCOUNT)).build()).build();
        Mockito.verify(mockStub).send(req);
    }

    @Test
    public void startServerHandshakeFailure() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getErrorResponse());
        try {
            ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
            handshaker.startServerHandshake(inBytes);
            Assert.fail("Exception expected");
        } catch (GeneralSecurityException ex) {
            assertThat(ex).hasMessageThat().contains(MockAltsHandshakerResp.getTestErrorDetails());
        }
    }

    @Test
    public void startServerHandshakeSuccess() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        ByteBuffer outFrame = handshaker.startServerHandshake(inBytes);
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
        Assert.assertEquals(((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.remaining());
    }

    @Test
    public void startServerHandshakeEmptyOutFrame() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getEmptyOutFrameResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        ByteBuffer outFrame = handshaker.startServerHandshake(inBytes);
        Assert.assertEquals(0, outFrame.remaining());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
        Assert.assertEquals(((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.remaining());
    }

    @Test
    public void startServerHandshakeWithPrefixBuffer() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        inBytes.position(AltsHandshakerClientTest.PREFIX_POSITION);
        ByteBuffer outFrame = handshaker.startServerHandshake(inBytes);
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
        Assert.assertEquals(((AltsHandshakerClientTest.PREFIX_POSITION) + (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.position());
        Assert.assertEquals((((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)) - (AltsHandshakerClientTest.PREFIX_POSITION)), inBytes.remaining());
    }

    @Test
    public void nextFailure() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getErrorResponse());
        try {
            ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
            handshaker.next(inBytes);
            Assert.fail("Exception expected");
        } catch (GeneralSecurityException ex) {
            assertThat(ex).hasMessageThat().contains(MockAltsHandshakerResp.getTestErrorDetails());
        }
    }

    @Test
    public void nextSuccess() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        ByteBuffer outFrame = handshaker.next(inBytes);
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
        Assert.assertEquals(((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.remaining());
    }

    @Test
    public void nextEmptyOutFrame() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getEmptyOutFrameResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        ByteBuffer outFrame = handshaker.next(inBytes);
        Assert.assertEquals(0, outFrame.remaining());
        Assert.assertFalse(handshaker.isFinished());
        Assert.assertNull(handshaker.getResult());
        Assert.assertNull(handshaker.getKey());
        Assert.assertEquals(((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.remaining());
    }

    @Test
    public void nextFinished() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getFinishedResponse(AltsHandshakerClientTest.BYTES_CONSUMED));
        ByteBuffer inBytes = ByteBuffer.allocate(AltsHandshakerClientTest.IN_BYTES_SIZE);
        ByteBuffer outFrame = handshaker.next(inBytes);
        Assert.assertEquals(ByteString.copyFrom(outFrame), MockAltsHandshakerResp.getOutFrame());
        Assert.assertTrue(handshaker.isFinished());
        Assert.assertArrayEquals(handshaker.getKey(), MockAltsHandshakerResp.getTestKeyData());
        Assert.assertEquals(((AltsHandshakerClientTest.IN_BYTES_SIZE) - (AltsHandshakerClientTest.BYTES_CONSUMED)), inBytes.remaining());
    }

    @Test
    public void setRpcVersions() throws Exception {
        Mockito.when(mockStub.send(Matchers.<HandshakerReq>any())).thenReturn(MockAltsHandshakerResp.getOkResponse(0));
        RpcProtocolVersions rpcVersions = RpcProtocolVersions.newBuilder().setMinRpcVersion(Version.newBuilder().setMajor(3).setMinor(4).build()).setMaxRpcVersion(Version.newBuilder().setMajor(5).setMinor(6).build()).build();
        clientOptions = new AltsClientOptions.Builder().setTargetName(AltsHandshakerClientTest.TEST_TARGET_NAME).setTargetServiceAccounts(ImmutableList.of(AltsHandshakerClientTest.TEST_TARGET_SERVICE_ACCOUNT)).setRpcProtocolVersions(rpcVersions).build();
        handshaker = new AltsHandshakerClient(mockStub, clientOptions);
        handshaker.startClientHandshake();
        ArgumentCaptor<HandshakerReq> reqCaptor = ArgumentCaptor.forClass(HandshakerReq.class);
        Mockito.verify(mockStub).send(reqCaptor.capture());
        Assert.assertEquals(rpcVersions, reqCaptor.getValue().getClientStart().getRpcVersions());
    }
}

