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
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.ReferenceCounted;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class NettyTsiHandshakerTest {
    private final UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;

    private final List<ReferenceCounted> references = new ArrayList<>();

    private final NettyTsiHandshaker clientHandshaker = new NettyTsiHandshaker(FakeTsiHandshaker.newFakeHandshakerClient());

    private final NettyTsiHandshaker serverHandshaker = new NettyTsiHandshaker(FakeTsiHandshaker.newFakeHandshakerServer());

    @Test
    public void failsOnNullHandshaker() {
        try {
            new NettyTsiHandshaker(null);
            Assert.fail("Exception expected");
        } catch (NullPointerException ex) {
            // Do nothing.
        }
    }

    @Test
    public void processPeerHandshakeShouldAcceptPartialFrames() throws GeneralSecurityException {
        for (int i = 0; i < 1024; i++) {
            ByteBuf clientData = ref(alloc.buffer(1));
            clientHandshaker.getBytesToSendToPeer(clientData);
            if (clientData.isReadable()) {
                if (serverHandshaker.processBytesFromPeer(clientData)) {
                    // Done.
                    return;
                }
            }
        }
        Assert.fail("Failed to process the handshake frame.");
    }

    @Test
    public void handshakeShouldSucceed() throws GeneralSecurityException {
        doHandshake();
    }

    @Test
    public void isInProgress() throws GeneralSecurityException {
        Assert.assertTrue(clientHandshaker.isInProgress());
        Assert.assertTrue(serverHandshaker.isInProgress());
        doHandshake();
        Assert.assertFalse(clientHandshaker.isInProgress());
        Assert.assertFalse(serverHandshaker.isInProgress());
    }

    @Test
    public void extractPeer_notNull() throws GeneralSecurityException {
        doHandshake();
        Assert.assertNotNull(serverHandshaker.extractPeer());
        Assert.assertNotNull(clientHandshaker.extractPeer());
    }

    @Test
    public void extractPeer_failsBeforeHandshake() throws GeneralSecurityException {
        try {
            clientHandshaker.extractPeer();
            Assert.fail("Exception expected");
        } catch (IllegalStateException ex) {
            // Do nothing.
        }
    }

    @Test
    public void extractPeerObject_notNull() throws GeneralSecurityException {
        doHandshake();
        Assert.assertNotNull(serverHandshaker.extractPeerObject());
        Assert.assertNotNull(clientHandshaker.extractPeerObject());
    }

    @Test
    public void extractPeerObject_failsBeforeHandshake() throws GeneralSecurityException {
        try {
            clientHandshaker.extractPeerObject();
            Assert.fail("Exception expected");
        } catch (IllegalStateException ex) {
            // Do nothing.
        }
    }

    /**
     * NettyTsiHandshaker just converts {@link ByteBuffer} to {@link ByteBuf}, so check that the other
     * methods are otherwise the same.
     */
    @Test
    public void handshakerMethodsMatch() {
        List<String> expectedMethods = new ArrayList<>();
        for (Method m : TsiHandshaker.class.getDeclaredMethods()) {
            expectedMethods.add(m.getName());
        }
        List<String> actualMethods = new ArrayList<>();
        for (Method m : NettyTsiHandshaker.class.getDeclaredMethods()) {
            actualMethods.add(m.getName());
        }
        assertThat(actualMethods).containsAllIn(expectedMethods);
    }

    /**
     * A mirror of java.util.function.Function without the Java 8 dependency.
     */
    private interface Function<T, R> {
        R apply(T t);
    }
}

