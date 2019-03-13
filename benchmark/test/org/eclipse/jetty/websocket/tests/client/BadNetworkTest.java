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
package org.eclipse.jetty.websocket.tests.client;


import StatusCode.NO_CLOSE;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests for conditions due to bad networking.
 */
public class BadNetworkTest {
    private Server server;

    private WebSocketClient client;

    @Test
    public void testAbruptClientClose() throws Exception {
        CloseTrackingEndpoint wsocket = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> future = client.connect(wsocket, wsUri);
        // Validate that we are connected
        future.get(30, TimeUnit.SECONDS);
        // Have client disconnect abruptly
        Session session = getSession();
        session.disconnect();
        // Client Socket should see a close event, with status NO_CLOSE
        // This event is automatically supplied by the underlying WebSocketClientConnection
        // in the situation of a bad network connection.
        wsocket.assertReceivedCloseEvent(5000, Matchers.is(NO_CLOSE), Matchers.containsString(""));
    }

    @Test
    public void testAbruptServerClose() throws Exception {
        CloseTrackingEndpoint wsocket = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> future = client.connect(wsocket, wsUri);
        // Validate that we are connected
        Session session = future.get(30, TimeUnit.SECONDS);
        // Have server disconnect abruptly
        session.getRemote().sendString("abort");
        // Client Socket should see a close event, with status NO_CLOSE
        // This event is automatically supplied by the underlying WebSocketClientConnection
        // in the situation of a bad network connection.
        wsocket.assertReceivedCloseEvent(5000, Matchers.is(NO_CLOSE), Matchers.containsString(""));
    }

    public static class ServerEndpoint implements WebSocketListener {
        private static final Logger LOG = Log.getLogger(ClientCloseTest.ServerEndpoint.class);

        private Session session;

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
        }

        @Override
        public void onWebSocketText(String message) {
            try {
                if (message.equals("abort")) {
                    session.disconnect();
                } else {
                    // simple echo
                    session.getRemote().sendString(message);
                }
            } catch (IOException e) {
                BadNetworkTest.ServerEndpoint.LOG.warn(e);
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
        }

        @Override
        public void onWebSocketConnect(Session session) {
            this.session = session;
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            if (BadNetworkTest.ServerEndpoint.LOG.isDebugEnabled()) {
                BadNetworkTest.ServerEndpoint.LOG.debug(cause);
            }
        }
    }
}

