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


import StatusCode.ABNORMAL;
import StatusCode.NORMAL;
import StatusCode.SERVER_ERROR;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests various close scenarios
 */
public class ServerCloseTest {
    private WebSocketClient client;

    private Server server;

    private ServerCloseCreator serverEndpointCreator;

    /**
     * Test fast close (bug #403817)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void fastClose() throws Exception {
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("fastclose");
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> futSession = client.connect(clientEndpoint, wsUri, request);
        Session session = null;
        try {
            session = futSession.get(5, TimeUnit.SECONDS);
            // Verify that client got close
            clientEndpoint.assertReceivedCloseEvent(5000, Matchers.is(NORMAL), Matchers.containsString(""));
            // Verify that server socket got close event
            AbstractCloseEndpoint serverEndpoint = serverEndpointCreator.pollLastCreated();
            MatcherAssert.assertThat("Fast Close Latch", serverEndpoint.closeLatch.await(5, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("Fast Close.statusCode", serverEndpoint.closeStatusCode, Matchers.is(ABNORMAL));
        } finally {
            close(session);
        }
    }

    /**
     * Test fast fail (bug #410537)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void fastFail() throws Exception {
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("fastfail");
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> futSession = client.connect(clientEndpoint, wsUri, request);
        Session session = null;
        try (StacklessLogging ignore = new StacklessLogging(FastFailEndpoint.class, WebSocketSession.class)) {
            session = futSession.get(5, TimeUnit.SECONDS);
            // Verify that client got close indicating SERVER_ERROR
            clientEndpoint.assertReceivedCloseEvent(5000, Matchers.is(SERVER_ERROR), Matchers.containsString("Intentional FastFail"));
            // Verify that server socket got close event
            AbstractCloseEndpoint serverEndpoint = serverEndpointCreator.pollLastCreated();
            serverEndpoint.assertReceivedCloseEvent(5000, Matchers.is(SERVER_ERROR), Matchers.containsString("Intentional FastFail"));
            // Validate errors (must be "java.lang.RuntimeException: Intentional Exception from onWebSocketConnect")
            MatcherAssert.assertThat("socket.onErrors", serverEndpoint.errors.size(), Matchers.greaterThanOrEqualTo(1));
            Throwable cause = serverEndpoint.errors.poll(5, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(RuntimeException.class));
            // ... with optional ClosedChannelException
            cause = serverEndpoint.errors.peek();
            if (cause != null) {
                MatcherAssert.assertThat("Error type", cause, Matchers.instanceOf(ClosedChannelException.class));
            }
        } finally {
            close(session);
        }
    }

    @Test
    public void dropConnection() throws Exception {
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("container");
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> futSession = client.connect(clientEndpoint, wsUri, request);
        Session session = null;
        try (StacklessLogging ignore = new StacklessLogging(WebSocketSession.class)) {
            session = futSession.get(5, TimeUnit.SECONDS);
            // Cause a client endpoint failure
            clientEndpoint.getEndPoint().close();
            // Verify that client got close
            clientEndpoint.assertReceivedCloseEvent(5000, Matchers.is(ABNORMAL), Matchers.containsString("Disconnected"));
            // Verify that server socket got close event
            AbstractCloseEndpoint serverEndpoint = serverEndpointCreator.pollLastCreated();
            serverEndpoint.assertReceivedCloseEvent(5000, Matchers.is(ABNORMAL), Matchers.containsString("Disconnected"));
        } finally {
            close(session);
        }
    }

    /**
     * Test session open session cleanup (bug #474936)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testOpenSessionCleanup() throws Exception {
        fastFail();
        fastClose();
        dropConnection();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("container");
        CloseTrackingEndpoint clientEndpoint = new CloseTrackingEndpoint();
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        Future<Session> futSession = client.connect(clientEndpoint, wsUri, request);
        Session session = null;
        try (StacklessLogging ignore = new StacklessLogging(WebSocketSession.class)) {
            session = futSession.get(5, TimeUnit.SECONDS);
            session.getRemote().sendString("openSessions");
            String msg = clientEndpoint.messageQueue.poll(5, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Should only have 1 open session", msg, Matchers.containsString("openSessions.size=1\n"));
            // Verify that client got close
            clientEndpoint.assertReceivedCloseEvent(5000, Matchers.is(NORMAL), Matchers.containsString("ContainerEndpoint"));
            // Verify that server socket got close event
            AbstractCloseEndpoint serverEndpoint = serverEndpointCreator.pollLastCreated();
            MatcherAssert.assertThat("Server Open Sessions Latch", serverEndpoint.closeLatch.await(5, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("Server Open Sessions.statusCode", serverEndpoint.closeStatusCode, Matchers.is(NORMAL));
            MatcherAssert.assertThat("Server Open Sessions.errors", serverEndpoint.errors.size(), Matchers.is(0));
        } finally {
            close(session);
        }
    }
}

