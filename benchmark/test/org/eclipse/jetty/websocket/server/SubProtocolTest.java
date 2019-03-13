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


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.jupiter.api.Test;


public class SubProtocolTest {
    @WebSocket
    public static class ProtocolEchoSocket {
        private Session session;

        private String acceptedProtocol;

        @OnWebSocketConnect
        public void onConnect(Session session) {
            this.session = session;
            this.acceptedProtocol = session.getUpgradeResponse().getAcceptedSubProtocol();
        }

        @OnWebSocketMessage
        public void onMsg(String msg) {
            session.getRemote().sendStringByFuture(("acceptedSubprotocol=" + (acceptedProtocol)));
        }
    }

    public static class ProtocolCreator implements WebSocketCreator {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            // Accept first sub-protocol
            if ((req.getSubProtocols()) != null) {
                if (!(req.getSubProtocols().isEmpty())) {
                    String subProtocol = req.getSubProtocols().get(0);
                    resp.setAcceptedSubProtocol(subProtocol);
                }
            }
            return new SubProtocolTest.ProtocolEchoSocket();
        }
    }

    public static class ProtocolServlet extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.setCreator(new SubProtocolTest.ProtocolCreator());
        }
    }

    private static BlockheadClient client;

    private static SimpleServletServer server;

    @Test
    public void testSingleProtocol() throws Exception {
        testSubProtocol("echo", "echo");
    }

    @Test
    public void testMultipleProtocols() throws Exception {
        testSubProtocol("chat,info,echo", "chat");
    }
}

