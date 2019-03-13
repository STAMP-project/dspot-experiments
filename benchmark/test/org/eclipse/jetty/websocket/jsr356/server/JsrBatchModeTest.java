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
package org.eclipse.jetty.websocket.jsr356.server;


import ClientEndpointConfig.Builder;
import RemoteEndpoint.Async;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JsrBatchModeTest {
    private Server server;

    private ServerConnector connector;

    private WebSocketContainer client;

    @Test
    public void testBatchModeOn() throws Exception {
        ClientEndpointConfig config = Builder.create().build();
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        final CountDownLatch latch = new CountDownLatch(1);
        JsrBatchModeTest.EndpointAdapter endpoint = new JsrBatchModeTest.EndpointAdapter() {
            @Override
            public void onMessage(String message) {
                latch.countDown();
            }
        };
        try (Session session = client.connectToServer(endpoint, config, uri)) {
            RemoteEndpoint.Async remote = session.getAsyncRemote();
            remote.setBatchingAllowed(true);
            Future<Void> future = remote.sendText("batch_mode_on");
            // The write is aggregated and therefore completes immediately.
            future.get(1, TimeUnit.MICROSECONDS);
            // Did not flush explicitly, so the message should not be back yet.
            Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
            // Explicitly flush.
            remote.flushBatch();
            // Wait for the echo.
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testBatchModeOff() throws Exception {
        ClientEndpointConfig config = Builder.create().build();
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        final CountDownLatch latch = new CountDownLatch(1);
        JsrBatchModeTest.EndpointAdapter endpoint = new JsrBatchModeTest.EndpointAdapter() {
            @Override
            public void onMessage(String message) {
                latch.countDown();
            }
        };
        try (Session session = client.connectToServer(endpoint, config, uri)) {
            RemoteEndpoint.Async remote = session.getAsyncRemote();
            remote.setBatchingAllowed(false);
            Future<Void> future = remote.sendText("batch_mode_off");
            // The write is immediate.
            future.get(1, TimeUnit.SECONDS);
            // Wait for the echo.
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testBatchModeAuto() throws Exception {
        ClientEndpointConfig config = Builder.create().build();
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        final CountDownLatch latch = new CountDownLatch(1);
        JsrBatchModeTest.EndpointAdapter endpoint = new JsrBatchModeTest.EndpointAdapter() {
            @Override
            public void onMessage(String message) {
                latch.countDown();
            }
        };
        try (Session session = client.connectToServer(endpoint, config, uri)) {
            RemoteEndpoint.Async remote = session.getAsyncRemote();
            Future<Void> future = remote.sendText("batch_mode_auto");
            // The write is immediate, as per the specification.
            future.get(1, TimeUnit.SECONDS);
            // Wait for the echo.
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    private abstract static class EndpointAdapter extends Endpoint implements MessageHandler.Whole<String> {
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(this);
        }
    }
}

