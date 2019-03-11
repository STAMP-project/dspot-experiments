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


import HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL;
import OpCode.CLOSE;
import StatusCode.MESSAGE_TOO_LARGE;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AnnotatedMaxMessageSizeTest {
    private static BlockheadClient client;

    private static Server server;

    private static ServerConnector connector;

    private static URI serverUri;

    @Test
    public void testEchoGood() throws Exception {
        BlockheadClientRequest request = AnnotatedMaxMessageSizeTest.client.newWsRequest(AnnotatedMaxMessageSizeTest.serverUri);
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "echo");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            clientConn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }

    @Test
    public void testEchoTooBig() throws Exception {
        BlockheadClientRequest request = AnnotatedMaxMessageSizeTest.client.newWsRequest(AnnotatedMaxMessageSizeTest.serverUri);
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "echo");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT);StacklessLogging ignore = new StacklessLogging(Parser.class)) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(8), () -> {
                // Generate text frame
                int size = 120 * 1024;
                byte[] buf = new byte[size];// buffer bigger than maxMessageSize

                Arrays.fill(buf, ((byte) ('x')));
                clientConn.write(new TextFrame().setPayload(ByteBuffer.wrap(buf)));
                // Read frame (hopefully close frame saying its too large)
                LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
                WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
                MatcherAssert.assertThat("Frame is close", tf.getOpCode(), Matchers.is(CLOSE));
                CloseInfo close = new CloseInfo(tf);
                MatcherAssert.assertThat("Close Code", close.getStatusCode(), Matchers.is(MESSAGE_TOO_LARGE));
            });
        }
    }
}

