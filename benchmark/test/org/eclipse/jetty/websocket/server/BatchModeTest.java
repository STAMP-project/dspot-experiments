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


import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


// TODO: currently not possible to configure the Jetty WebSocket Session with the batch mode.
public class BatchModeTest {
    private Server server;

    private ServerConnector connector;

    private WebSocketClient client;

    @Test
    public void testBatchModeAuto() throws Exception {
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        final CountDownLatch latch = new CountDownLatch(1);
        WebSocketAdapter adapter = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(String message) {
                latch.countDown();
            }
        };
        try (Session session = client.connect(adapter, uri).get()) {
            RemoteEndpoint remote = session.getRemote();
            Future<Void> future = remote.sendStringByFuture("batch_mode_on");
            // The write is aggregated and therefore completes immediately.
            future.get(1, TimeUnit.MICROSECONDS);
            // Wait for the echo.
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }
}

