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
package org.eclipse.jetty.websocket.client;


import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TomcatServerQuirksTest {
    public static class LatchedSocket extends WebSocketAdapter {
        final CountDownLatch openLatch = new CountDownLatch(1);

        final CountDownLatch dataLatch = new CountDownLatch(1);

        final CountDownLatch closeLatch = new CountDownLatch(1);

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            closeLatch.countDown();
        }

        @Override
        public void onWebSocketConnect(Session session) {
            openLatch.countDown();
        }

        @Override
        public void onWebSocketText(String message) {
            dataLatch.countDown();
        }
    }

    private static BlockheadServer server;

    /**
     * Test for when encountering a "Transfer-Encoding: chunked" on a Upgrade Response header.
     * <ul>
     * <li><a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=393075">Eclipse Jetty Bug #393075</a></li>
     * <li><a href="https://issues.apache.org/bugzilla/show_bug.cgi?id=54067">Apache Tomcat Bug #54067</a></li>
     * </ul>
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testTomcat7_0_32_WithTransferEncoding() throws Exception {
        WebSocketClient client = new WebSocketClient();
        TomcatServerQuirksTest.server.setRequestHandling(( req, resp) -> {
            // Add the extra problematic header that triggers bug found in jetty-io
            resp.setHeader("Transfer-Encoding", "chunked");
            return false;
        });
        try {
            final int bufferSize = 512;
            // Setup Client Factory
            client.start();
            // Create End User WebSocket Class
            TomcatServerQuirksTest.LatchedSocket websocket = new TomcatServerQuirksTest.LatchedSocket();
            CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
            TomcatServerQuirksTest.server.addConnectFuture(serverConnFut);
            // Open connection
            URI wsURI = TomcatServerQuirksTest.server.getWsUri();
            client.connect(websocket, wsURI);
            // Wait for proper upgrade
            Assertions.assertTrue(websocket.openLatch.await(1, TimeUnit.SECONDS), "Timed out waiting for Client side WebSocket open event");
            try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
                // Have server write frame.
                byte[] payload = new byte[bufferSize / 2];
                Arrays.fill(payload, ((byte) ('x')));
                ByteBuffer serverFrame = BufferUtil.allocate(bufferSize);
                BufferUtil.flipToFill(serverFrame);
                serverFrame.put(((byte) (128 | 1)));// FIN + TEXT

                serverFrame.put(((byte) (126)));// No MASK and 2 bytes length

                serverFrame.put(((byte) ((payload.length) >> 8)));// first length byte

                serverFrame.put(((byte) ((payload.length) & 255)));// second length byte

                serverFrame.put(payload);
                BufferUtil.flipToFlush(serverFrame, 0);
                serverConn.writeRaw(serverFrame);
            }
            Assertions.assertTrue(websocket.dataLatch.await(1000, TimeUnit.SECONDS));
        } finally {
            client.stop();
        }
    }
}

