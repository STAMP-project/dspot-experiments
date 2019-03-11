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
package org.eclipse.jetty.websocket.server.misbehaving;


import HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL;
import OpCode.CLOSE;
import StatusCode.SERVER_ERROR;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.server.SimpleServletServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Testing badly behaving Socket class implementations to get the best
 * error messages and state out of the websocket implementation.
 */
public class MisbehavingClassTest {
    private static SimpleServletServer server;

    private static BadSocketsServlet badSocketsServlet;

    private static BlockheadClient client;

    @Test
    public void testListenerRuntimeOnConnect() throws Exception {
        BlockheadClientRequest request = MisbehavingClassTest.client.newWsRequest(MisbehavingClassTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "listener-runtime-connect");
        request.idleTimeout(1, TimeUnit.SECONDS);
        ListenerRuntimeOnConnectSocket socket = MisbehavingClassTest.badSocketsServlet.listenerRuntimeConnect;
        socket.reset();
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (StacklessLogging ignore = new StacklessLogging(ListenerRuntimeOnConnectSocket.class, WebSocketSession.class);BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("frames[0].opcode", frame.getOpCode(), Matchers.is(CLOSE));
            CloseInfo close = new CloseInfo(frame);
            MatcherAssert.assertThat("Close Status Code", close.getStatusCode(), Matchers.is(SERVER_ERROR));
            clientConn.write(close.asFrame());// respond with close

            // ensure server socket got close event
            MatcherAssert.assertThat("Close Latch", socket.closeLatch.await(1, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("closeStatusCode", socket.closeStatusCode, Matchers.is(SERVER_ERROR));
            // Validate errors (must be "java.lang.RuntimeException: Intentional Exception from onWebSocketConnect")
            MatcherAssert.assertThat("socket.onErrors", socket.errors.size(), Matchers.greaterThanOrEqualTo(1));
            Throwable cause = socket.errors.pop();
            MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(RuntimeException.class));
            // ... with optional ClosedChannelException
            cause = socket.errors.peek();
            if (cause != null)
                MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(ClosedChannelException.class));

        }
    }

    @Test
    public void testAnnotatedRuntimeOnConnect() throws Exception {
        BlockheadClientRequest request = MisbehavingClassTest.client.newWsRequest(MisbehavingClassTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "annotated-runtime-connect");
        request.idleTimeout(1, TimeUnit.SECONDS);
        AnnotatedRuntimeOnConnectSocket socket = MisbehavingClassTest.badSocketsServlet.annotatedRuntimeConnect;
        socket.reset();
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (StacklessLogging ignore = /* , WebSocketSession.class */
        new StacklessLogging(AnnotatedRuntimeOnConnectSocket.class);BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("frames[0].opcode", frame.getOpCode(), Matchers.is(CLOSE));
            CloseInfo close = new CloseInfo(frame);
            MatcherAssert.assertThat("Close Status Code", close.getStatusCode(), Matchers.is(SERVER_ERROR));
            clientConn.write(close.asFrame());// respond with close

            // ensure server socket got close event
            MatcherAssert.assertThat("Close Latch", socket.closeLatch.await(1, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("closeStatusCode", socket.closeStatusCode, Matchers.is(SERVER_ERROR));
            // Validate errors (must be "java.lang.RuntimeException: Intentional Exception from onWebSocketConnect")
            MatcherAssert.assertThat("socket.onErrors", socket.errors.size(), Matchers.greaterThanOrEqualTo(1));
            Throwable cause = socket.errors.pop();
            MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(RuntimeException.class));
            // ... with optional ClosedChannelException
            cause = socket.errors.peek();
            if (cause != null)
                MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(ClosedChannelException.class));

        }
    }
}

