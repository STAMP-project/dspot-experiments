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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SuspendResumeTest {
    @WebSocket
    public static class EchoSocket {
        private Session session;

        @OnWebSocketConnect
        public void onConnect(Session session) {
            this.session = session;
        }

        @OnWebSocketMessage
        public void onMessage(String message) {
            SuspendToken suspendToken = this.session.suspend();
            this.session.getRemote().sendString(message, new WriteCallback() {
                @Override
                public void writeSuccess() {
                    suspendToken.resume();
                }

                @Override
                public void writeFailed(Throwable t) {
                    Assertions.fail(t);
                }
            });
        }
    }

    public static class EchoCreator implements WebSocketCreator {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            return new SuspendResumeTest.EchoSocket();
        }
    }

    public static class EchoServlet extends WebSocketServlet {
        private static final long serialVersionUID = 1L;

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.setCreator(new SuspendResumeTest.EchoCreator());
        }
    }

    private static SimpleServletServer server;

    private static BlockheadClient client;

    @Test
    public void testSuspendResume() throws Exception {
        BlockheadClientRequest request = SuspendResumeTest.client.newWsRequest(SuspendResumeTest.server.getServerUri());
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("echo1"));
            clientConn.write(new TextFrame().setPayload("echo2"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat(((SuspendResumeTest.EchoSocket.class.getSimpleName()) + ".onMessage()"), tf.getPayloadAsUTF8(), Matchers.is("echo1"));
            tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat(((SuspendResumeTest.EchoSocket.class.getSimpleName()) + ".onMessage()"), tf.getPayloadAsUTF8(), Matchers.is("echo2"));
        }
    }
}

