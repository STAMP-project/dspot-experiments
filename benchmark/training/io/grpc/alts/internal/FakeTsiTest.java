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


import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static io.grpc.alts.internal.FakeTsiHandshaker.State.CLIENT_FINISHED;
import static io.grpc.alts.internal.FakeTsiHandshaker.State.CLIENT_INIT;
import static io.grpc.alts.internal.FakeTsiHandshaker.State.SERVER_FINISHED;
import static io.grpc.alts.internal.FakeTsiHandshaker.State.SERVER_INIT;


/**
 * Unit tests for {@link TsiHandshaker}.
 */
@RunWith(JUnit4.class)
public class FakeTsiTest {
    private static final int OVERHEAD = (FakeChannelCrypter.getTagBytes()) + (AltsTsiFrameProtector.getHeaderBytes());

    private final List<ReferenceCounted> references = new ArrayList<>();

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
    public void handshakeStateOrderTest() {
        try {
            TsiTest.Handshakers handshakers = FakeTsiTest.newHandshakers();
            TsiHandshaker clientHandshaker = handshakers.getClient();
            TsiHandshaker serverHandshaker = handshakers.getServer();
            byte[] transportBufferBytes = new byte[TsiTest.getDefaultTransportBufferSize()];
            ByteBuffer transportBuffer = ByteBuffer.wrap(transportBufferBytes);
            transportBuffer.limit(0);// Start off with an empty buffer

            transportBuffer.clear();
            clientHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertEquals(CLIENT_INIT.toString().trim(), new String(transportBufferBytes, 4, transportBuffer.remaining(), StandardCharsets.UTF_8).trim());
            serverHandshaker.processBytesFromPeer(transportBuffer);
            Assert.assertFalse(transportBuffer.hasRemaining());
            // client shouldn't offer any more bytes
            transportBuffer.clear();
            clientHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertFalse(transportBuffer.hasRemaining());
            transportBuffer.clear();
            serverHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertEquals(SERVER_INIT.toString().trim(), new String(transportBufferBytes, 4, transportBuffer.remaining(), StandardCharsets.UTF_8).trim());
            clientHandshaker.processBytesFromPeer(transportBuffer);
            Assert.assertFalse(transportBuffer.hasRemaining());
            // server shouldn't offer any more bytes
            transportBuffer.clear();
            serverHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertFalse(transportBuffer.hasRemaining());
            transportBuffer.clear();
            clientHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertEquals(CLIENT_FINISHED.toString().trim(), new String(transportBufferBytes, 4, transportBuffer.remaining(), StandardCharsets.UTF_8).trim());
            serverHandshaker.processBytesFromPeer(transportBuffer);
            Assert.assertFalse(transportBuffer.hasRemaining());
            // client shouldn't offer any more bytes
            transportBuffer.clear();
            clientHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertFalse(transportBuffer.hasRemaining());
            transportBuffer.clear();
            serverHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertEquals(SERVER_FINISHED.toString().trim(), new String(transportBufferBytes, 4, transportBuffer.remaining(), StandardCharsets.UTF_8).trim());
            clientHandshaker.processBytesFromPeer(transportBuffer);
            Assert.assertFalse(transportBuffer.hasRemaining());
            // server shouldn't offer any more bytes
            transportBuffer.clear();
            serverHandshaker.getBytesToSendToPeer(transportBuffer);
            transportBuffer.flip();
            Assert.assertFalse(transportBuffer.hasRemaining());
        } catch (GeneralSecurityException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void handshake() throws GeneralSecurityException {
        TsiTest.handshakeTest(FakeTsiTest.newHandshakers());
    }

    @Test
    public void handshakeSmallBuffer() throws GeneralSecurityException {
        TsiTest.handshakeSmallBufferTest(FakeTsiTest.newHandshakers());
    }

    @Test
    public void pingPong() throws GeneralSecurityException {
        TsiTest.pingPongTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void pingPongExactFrameSize() throws GeneralSecurityException {
        TsiTest.pingPongExactFrameSizeTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallBuffer() throws GeneralSecurityException {
        TsiTest.pingPongSmallBufferTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallFrame() throws GeneralSecurityException {
        TsiTest.pingPongSmallFrameTest(FakeTsiTest.OVERHEAD, FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void pingPongSmallFrameSmallBuffer() throws GeneralSecurityException {
        TsiTest.pingPongSmallFrameSmallBufferTest(FakeTsiTest.OVERHEAD, FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void corruptedCounter() throws GeneralSecurityException {
        TsiTest.corruptedCounterTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void corruptedCiphertext() throws GeneralSecurityException {
        TsiTest.corruptedCiphertextTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void corruptedTag() throws GeneralSecurityException {
        TsiTest.corruptedTagTest(FakeTsiTest.newHandshakers(), ref);
    }

    @Test
    public void reflectedCiphertext() throws GeneralSecurityException {
        TsiTest.reflectedCiphertextTest(FakeTsiTest.newHandshakers(), ref);
    }
}

