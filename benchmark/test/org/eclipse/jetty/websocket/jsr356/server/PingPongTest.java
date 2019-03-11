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


import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.websocket.WebSocketContainer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class PingPongTest {
    private static WSServer server;

    private static URI serverUri;

    private static WebSocketContainer client;

    @Test
    public void testPingEndpoint() throws Exception {
        EchoClientSocket socket = new EchoClientSocket();
        URI toUri = PingPongTest.serverUri.resolve("ping");
        try {
            // Connect
            PingPongTest.client.connectToServer(socket, toUri);
            socket.waitForConnected(1, TimeUnit.SECONDS);
            // Send Ping
            String msg = "hello";
            socket.sendPing(msg);
            // Validate Responses
            String actual = socket.eventQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Received Ping Response", actual, Matchers.containsString(("PongMessage[/ping]:" + msg)));
        } finally {
            // Close
            socket.close();
        }
    }

    @Test
    public void testPongEndpoint() throws Exception {
        EchoClientSocket socket = new EchoClientSocket();
        URI toUri = PingPongTest.serverUri.resolve("pong");
        try {
            // Connect
            PingPongTest.client.connectToServer(socket, toUri);
            socket.waitForConnected(1, TimeUnit.SECONDS);
            // Send Ping
            String msg = "hello";
            socket.sendPong(msg);
            // Validate Responses
            String received = socket.eventQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Received Ping Responses", received, Matchers.containsString(("PongMessage[/pong]:" + msg)));
        } finally {
            // Close
            socket.close();
        }
    }

    @Test
    public void testPingSocket() throws Exception {
        EchoClientSocket socket = new EchoClientSocket();
        URI toUri = PingPongTest.serverUri.resolve("ping-socket");
        try {
            // Connect
            PingPongTest.client.connectToServer(socket, toUri);
            socket.waitForConnected(1, TimeUnit.SECONDS);
            // Send Ping
            String msg = "hello";
            socket.sendPing(msg);
            // Validate Responses
            String actual = socket.eventQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Received Ping Response", actual, Matchers.containsString(("@OnMessage(PongMessage)[/ping-socket]:" + msg)));
        } finally {
            // Close
            socket.close();
        }
    }

    @Test
    public void testPongSocket() throws Exception {
        EchoClientSocket socket = new EchoClientSocket();
        URI toUri = PingPongTest.serverUri.resolve("pong-socket");
        try {
            // Connect
            PingPongTest.client.connectToServer(socket, toUri);
            socket.waitForConnected(1, TimeUnit.SECONDS);
            // Send Ping
            String msg = "hello";
            socket.sendPong(msg);
            // Validate Responses
            String received = socket.eventQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Received Ping Responses", received, Matchers.containsString(("@OnMessage(PongMessage)[/pong-socket]:" + msg)));
        } finally {
            // Close
            socket.close();
        }
    }
}

