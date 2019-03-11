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


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TextStreamTest {
    private static final String PATH = "/echo";

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private Server server;

    private ServerConnector connector;

    private WebSocketContainer wsClient;

    @Test
    public void testEchoWithMediumMessage() throws Exception {
        testEcho(1024);
    }

    @Test
    public void testLargestMessage() throws Exception {
        testEcho(wsClient.getDefaultMaxBinaryMessageBufferSize());
    }

    @Test
    public void testMoreThanLargestMessageOneByteAtATime() throws Exception {
        int size = (wsClient.getDefaultMaxBinaryMessageBufferSize()) + 16;
        char[] data = randomChars(size);
        URI uri = URI.create((("ws://localhost:" + (connector.getLocalPort())) + (TextStreamTest.PATH)));
        TextStreamTest.ClientTextStreamer client = new TextStreamTest.ClientTextStreamer();
        Session session = wsClient.connectToServer(client, uri);
        try (Writer output = session.getBasicRemote().getSendWriter()) {
            for (int i = 0; i < size; ++i)
                output.write(data[i]);

        }
        Assertions.assertTrue(client.await(5, TimeUnit.SECONDS));
        Assertions.assertArrayEquals(data, client.getEcho());
    }

    @ClientEndpoint
    public static class ClientTextStreamer {
        private final CountDownLatch latch = new CountDownLatch(1);

        private final StringBuilder output = new StringBuilder();

        @OnMessage
        public void echoed(Reader input) throws IOException {
            while (true) {
                int read = input.read();
                if (read < 0)
                    break;

                output.append(((char) (read)));
            } 
            latch.countDown();
        }

        public char[] getEcho() {
            return output.toString().toCharArray();
        }

        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }
    }

    @ServerEndpoint(TextStreamTest.PATH)
    public static class ServerTextStreamer {
        @OnMessage
        public void echo(Session session, Reader input) throws IOException {
            char[] buffer = new char[128];
            try (Writer output = session.getBasicRemote().getSendWriter()) {
                int read;
                while ((read = input.read(buffer)) >= 0)
                    output.write(buffer, 0, read);

            }
        }
    }
}

