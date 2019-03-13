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


import StatusCode.NORMAL;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * This Regression Test Exists because of Client side Idle timeout, Read, and Parser bugs.
 */
public class SlowClientTest {
    private Server server;

    private WebSocketClient client;

    @Test
    public void testClientSlowToSend() throws Exception {
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        client.getPolicy().setIdleTimeout(60000);
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> future = client.connect(clientEndpoint, wsUri);
        // Confirm connected
        Session session = future.get(5, TimeUnit.SECONDS);
        int messageCount = 10;
        try {
            // Have client write slowly.
            ClientWriteThread writer = new ClientWriteThread(getSession());
            writer.setMessageCount(messageCount);
            writer.setMessage("Hello");
            writer.setSlowness(10);
            writer.start();
            writer.join();
            // Close
            getSession().close(NORMAL, "Done");
            // confirm close received on server
            clientEndpoint.assertReceivedCloseEvent(10000, Matchers.is(NORMAL), Matchers.containsString("Done"));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

