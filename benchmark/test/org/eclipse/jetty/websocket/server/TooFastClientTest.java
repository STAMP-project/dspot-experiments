/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.server;


import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Test simulating a client that talks too quickly.
 * <p>
 * There is a class of client that will send the GET+Upgrade Request along with a few websocket frames in a single
 * network packet. This test attempts to perform this behavior as close as possible.
 */
public class TooFastClientTest {
    private static SimpleServletServer server;

    private static BlockheadClient client;

    @Test
    public void testUpgradeWithSmallFrames() throws Exception {
        BlockheadClientRequest request = TooFastClientTest.client.newWsRequest(TooFastClientTest.server.getServerUri());
        String msg1 = "Echo 1";
        String msg2 = "This is also an echooooo!";
        ByteBuffer initialPacket = createInitialPacket(msg1, msg2);
        request.setInitialBytes(initialPacket);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Read frames (hopefully text frames)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame/msg1", tf.getPayloadAsUTF8(), Matchers.is(msg1));
            tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame/msg2", tf.getPayloadAsUTF8(), Matchers.is(msg2));
        }
    }

    /**
     * Test where were a client sends a HTTP Upgrade to websocket AND enough websocket frame(s)
     * to completely overfill the {@link org.eclipse.jetty.io.AbstractConnection#getInputBufferSize()}
     * to test a situation where the WebSocket connection opens with prefill that exceeds
     * the normal input buffer sizes.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testUpgradeWithLargeFrame() throws Exception {
        BlockheadClientRequest request = TooFastClientTest.client.newWsRequest(TooFastClientTest.server.getServerUri());
        byte[] bigMsgBytes = new byte[64 * 1024];
        Arrays.fill(bigMsgBytes, ((byte) ('x')));
        String bigMsg = new String(bigMsgBytes, StandardCharsets.UTF_8);
        ByteBuffer initialPacket = createInitialPacket(bigMsg);
        request.setInitialBytes(initialPacket);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Read frames (hopefully text frames)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame/msg1", tf.getPayloadAsUTF8(), Matchers.is(bigMsg));
        }
    }
}

