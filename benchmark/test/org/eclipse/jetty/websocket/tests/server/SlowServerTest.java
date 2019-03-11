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
package org.eclipse.jetty.websocket.tests.server;


import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * This Regression Test Exists because of Server side Idle timeout, Write, and Generator bugs.
 */
public class SlowServerTest {
    private Server server;

    private WebSocketClient client;

    @Test
    public void testServerSlowToSend() throws Exception {
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        client.setMaxIdleTimeout(60000);
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> future = client.connect(clientEndpoint, wsUri);
        Session session = null;
        try {
            // Confirm connected
            session = future.get(5, TimeUnit.SECONDS);
            int messageCount = 10;
            session.getRemote().sendString(("send-slow|" + messageCount));
            // Verify receive
            LinkedBlockingQueue<String> responses = clientEndpoint.messageQueue;
            for (int i = 0; i < messageCount; i++) {
                String response = responses.poll(5, TimeUnit.SECONDS);
                MatcherAssert.assertThat((("Server Message[" + i) + "]"), response, Matchers.is((("Hello/" + i) + "/")));
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

