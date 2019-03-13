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
import StatusCode.SHUTDOWN;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.server.helper.RFCSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class IdleTimeoutTest {
    public static final int IDLE_TIMEOUT_MS_WEBSOCKET_SERVER = 500;

    public static final int IDLE_TIMEOUT_ON_SERVER = 1000;

    public static final int IDLE_TIMEOUT_MS_WEBSOCKET_CLIENT = 2500;

    @SuppressWarnings("serial")
    public static class TimeoutServlet extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.getPolicy().setIdleTimeout(IdleTimeoutTest.IDLE_TIMEOUT_MS_WEBSOCKET_SERVER);
            factory.register(RFCSocket.class);
        }
    }

    private static BlockheadClient client;

    private static SimpleServletServer server;

    /**
     * Test IdleTimeout on server.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testIdleTimeout() throws Exception {
        BlockheadClientRequest request = IdleTimeoutTest.client.newWsRequest(IdleTimeoutTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "onConnect");
        request.idleTimeout(IdleTimeoutTest.IDLE_TIMEOUT_MS_WEBSOCKET_CLIENT, TimeUnit.MILLISECONDS);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // This wait should be shorter than client timeout above, but
            // longer than server timeout configured in TimeoutServlet
            TimeUnit.MILLISECONDS.sleep(IdleTimeoutTest.IDLE_TIMEOUT_ON_SERVER);
            // Write to server
            // This action is possible, but does nothing.
            // Server could be in a half-closed state at this point.
            // Where the server read is closed (due to timeout), but the server write is still open.
            // The server could not read this frame, if it is in this half closed state
            clientConn.write(new TextFrame().setPayload("Hello"));
            // Expect server to have closed due to its own timeout
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("frame opcode", frame.getOpCode(), Matchers.is(CLOSE));
            CloseInfo close = new CloseInfo(frame);
            MatcherAssert.assertThat("close code", close.getStatusCode(), Matchers.is(SHUTDOWN));
            MatcherAssert.assertThat("close reason", close.getReason(), Matchers.containsString("timeout"));
        }
    }
}

