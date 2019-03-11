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


import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.server.helper.CaptureSocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class WebSocketOverSSLTest {
    public static final int CONNECT_TIMEOUT = 15000;

    public static final int FUTURE_TIMEOUT_SEC = 30;

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private static SimpleServletServer server;

    /**
     * Test the requirement of issuing socket and receiving echo response
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testEcho() throws Exception {
        MatcherAssert.assertThat("server scheme", WebSocketOverSSLTest.server.getServerUri().getScheme(), Matchers.is("wss"));
        WebSocketClient client = new WebSocketClient(WebSocketOverSSLTest.server.getSslContextFactory(), null, bufferPool);
        try {
            client.start();
            CaptureSocket clientSocket = new CaptureSocket();
            URI requestUri = WebSocketOverSSLTest.server.getServerUri();
            System.err.printf("Request URI: %s%n", requestUri.toASCIIString());
            Future<Session> fut = client.connect(clientSocket, requestUri);
            // wait for connect
            Session session = fut.get(WebSocketOverSSLTest.FUTURE_TIMEOUT_SEC, TimeUnit.SECONDS);
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            RemoteEndpoint remote = session.getRemote();
            remote.sendString(msg);
            if ((remote.getBatchMode()) == (BatchMode.ON))
                remote.flush();

            // Read frame (hopefully text frame)
            LinkedBlockingQueue<String> captured = clientSocket.messages;
            MatcherAssert.assertThat("Text Message", captured.poll(POLL_EVENT, POLL_EVENT_UNIT), Matchers.is(msg));
            // Shutdown the socket
            clientSocket.close();
        } finally {
            client.stop();
        }
    }

    /**
     * Test that server session reports as secure
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testServerSessionIsSecure() throws Exception {
        MatcherAssert.assertThat("server scheme", WebSocketOverSSLTest.server.getServerUri().getScheme(), Matchers.is("wss"));
        WebSocketClient client = new WebSocketClient(WebSocketOverSSLTest.server.getSslContextFactory(), null, bufferPool);
        try {
            client.setConnectTimeout(WebSocketOverSSLTest.CONNECT_TIMEOUT);
            client.start();
            CaptureSocket clientSocket = new CaptureSocket();
            URI requestUri = WebSocketOverSSLTest.server.getServerUri();
            System.err.printf("Request URI: %s%n", requestUri.toASCIIString());
            Future<Session> fut = client.connect(clientSocket, requestUri);
            // wait for connect
            Session session = fut.get(WebSocketOverSSLTest.FUTURE_TIMEOUT_SEC, TimeUnit.SECONDS);
            // Generate text frame
            RemoteEndpoint remote = session.getRemote();
            remote.sendString("session.isSecure");
            if ((remote.getBatchMode()) == (BatchMode.ON))
                remote.flush();

            // Read frame (hopefully text frame)
            LinkedBlockingQueue<String> captured = clientSocket.messages;
            MatcherAssert.assertThat("Server.session.isSecure", captured.poll(POLL_EVENT, POLL_EVENT_UNIT), Matchers.is("session.isSecure=true"));
            // Shutdown the socket
            clientSocket.close();
        } finally {
            client.stop();
        }
    }

    /**
     * Test that server session.upgradeRequest.requestURI reports correctly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testServerSessionRequestURI() throws Exception {
        MatcherAssert.assertThat("server scheme", WebSocketOverSSLTest.server.getServerUri().getScheme(), Matchers.is("wss"));
        WebSocketClient client = new WebSocketClient(WebSocketOverSSLTest.server.getSslContextFactory(), null, bufferPool);
        try {
            client.setConnectTimeout(WebSocketOverSSLTest.CONNECT_TIMEOUT);
            client.start();
            CaptureSocket clientSocket = new CaptureSocket();
            URI requestUri = WebSocketOverSSLTest.server.getServerUri().resolve("/deep?a=b");
            System.err.printf("Request URI: %s%n", requestUri.toASCIIString());
            Future<Session> fut = client.connect(clientSocket, requestUri);
            // wait for connect
            Session session = fut.get(WebSocketOverSSLTest.FUTURE_TIMEOUT_SEC, TimeUnit.SECONDS);
            // Generate text frame
            RemoteEndpoint remote = session.getRemote();
            remote.sendString("session.upgradeRequest.requestURI");
            if ((remote.getBatchMode()) == (BatchMode.ON))
                remote.flush();

            // Read frame (hopefully text frame)
            LinkedBlockingQueue<String> captured = clientSocket.messages;
            String expected = String.format("session.upgradeRequest.requestURI=%s", requestUri.toASCIIString());
            MatcherAssert.assertThat("session.upgradeRequest.requestURI", captured.poll(POLL_EVENT, POLL_EVENT_UNIT), Matchers.is(expected));
            // Shutdown the socket
            clientSocket.close();
        } finally {
            client.stop();
        }
    }
}

