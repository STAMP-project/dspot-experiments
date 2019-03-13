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
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AltsTsiHandshaker}.
 */
@RunWith(JUnit4.class)
public class AltsTsiTest {
    private static final int OVERHEAD = (FakeChannelCrypter.getTagBytes()) + (AltsTsiFrameProtector.getHeaderBytes());

    private final List<ReferenceCounted> references = new ArrayList<>();

    private AltsHandshakerClient client;

    private AltsHandshakerClient server;

    private final ByteBufTestUtils.RegisterRef ref = new ByteBufTestUtils.RegisterRef() {
        @Override
        public ByteBuf register(ByteBuf buf) {
            if (buf != null) {
                references.add(buf);
            }
            return buf;
        }
    };

    @Test
    public void verifyHandshakePeer() throws Exception {
        TsiTest.Handshakers handshakers = newHandshakers();
        TsiTest.performHandshake(TsiTest.getDefaultTransportBufferSize(), handshakers);
        TsiPeer clientPeer = handshakers.getClient().extractPeer();
        Assert.assertEquals(1, clientPeer.getProperties().size());
        Assert.assertEquals(MockAltsHandshakerResp.getTestPeerAccount(), clientPeer.getProperty("service_account").getValue());
        TsiPeer serverPeer = handshakers.getServer().extractPeer();
        Assert.assertEquals(1, serverPeer.getProperties().size());
        Assert.assertEquals(MockAltsHandshakerResp.getTestPeerAccount(), serverPeer.getProperty("service_account").getValue());
    }

    @Test
    public void handshake() throws GeneralSecurityException {
        TsiTest.handshakeTest(newHandshakers());
    }

    @Test
    public void handshakeSmallBuffer() throws GeneralSecurityException {
        TsiTest.handshakeSmallBufferTest(newHandshakers());
    }

    @Test
    public void pingPong() throws GeneralSecurityException {
        TsiTest.pingPongTest(newHandshakers(), ref);
    }

    @Test
    public void pingPongExactFrameSize() throws GeneralSecurityException {
        TsiTest.pingPongExactFrameSizeTest(newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallBuffer() throws GeneralSecurityException {
        TsiTest.pingPongSmallBufferTest(newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallFrame() throws GeneralSecurityException {
        TsiTest.pingPongSmallFrameTest(AltsTsiTest.OVERHEAD, newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallFrameSmallBuffer() throws GeneralSecurityException {
        TsiTest.pingPongSmallFrameSmallBufferTest(AltsTsiTest.OVERHEAD, newHandshakers(), ref);
    }

    @Test
    public void corruptedCounter() throws GeneralSecurityException {
        TsiTest.corruptedCounterTest(newHandshakers(), ref);
    }

    @Test
    public void corruptedCiphertext() throws GeneralSecurityException {
        TsiTest.corruptedCiphertextTest(newHandshakers(), ref);
    }

    @Test
    public void corruptedTag() throws GeneralSecurityException {
        TsiTest.corruptedTagTest(newHandshakers(), ref);
    }

    @Test
    public void reflectedCiphertext() throws GeneralSecurityException {
        TsiTest.reflectedCiphertextTest(newHandshakers(), ref);
    }

    private static class MockAltsHandshakerStub extends AltsHandshakerStub {
        private boolean started = false;

        @Override
        public HandshakerResp send(HandshakerReq req) {
            if (started) {
                // Expect handshake next message.
                if ((req.getReqOneofCase().getNumber()) != 3) {
                    return MockAltsHandshakerResp.getErrorResponse();
                }
                return MockAltsHandshakerResp.getFinishedResponse(req.getNext().getInBytes().size());
            } else {
                List<String> recordProtocols;
                int bytesConsumed = 0;
                switch (req.getReqOneofCase().getNumber()) {
                    case 1 :
                        recordProtocols = req.getClientStart().getRecordProtocolsList();
                        break;
                    case 2 :
                        recordProtocols = req.getServerStart().getHandshakeParametersMap().get(ALTS.getNumber()).getRecordProtocolsList();
                        bytesConsumed = req.getServerStart().getInBytes().size();
                        break;
                    default :
                        return MockAltsHandshakerResp.getErrorResponse();
                }
                if (recordProtocols.isEmpty()) {
                    return MockAltsHandshakerResp.getErrorResponse();
                }
                started = true;
                return MockAltsHandshakerResp.getOkResponse(bytesConsumed);
            }
        }

        @Override
        public void close() {
        }
    }
}

