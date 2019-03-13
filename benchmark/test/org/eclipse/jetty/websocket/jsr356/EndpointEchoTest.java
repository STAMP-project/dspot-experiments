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
package org.eclipse.jetty.websocket.jsr356;


import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.jsr356.samples.EchoStringEndpoint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class EndpointEchoTest {
    private static final Logger LOG = Log.getLogger(EndpointEchoTest.class);

    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    @Test
    public void testBasicEchoInstance() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        EndpointEchoTest.server.addBean(container);// allow to shutdown with server

        EndpointEchoClient echoer = new EndpointEchoClient();
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        // Issue connect using instance of class that extends Endpoint
        Session session = container.connectToServer(echoer, EndpointEchoTest.serverUri);
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Connected: {}", session);

        session.getBasicRemote().sendText("Echo");
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Message Sent");

        String echoed = echoer.textCapture.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed message", echoed, Matchers.is("Echo"));
    }

    @Test
    public void testBasicEchoClassref() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        EndpointEchoTest.server.addBean(container);// allow to shutdown with server

        // Issue connect using class reference (class extends Endpoint)
        Session session = container.connectToServer(EndpointEchoClient.class, EndpointEchoTest.serverUri);
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Connected: {}", session);

        session.getBasicRemote().sendText("Echo");
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Message Sent");

        EndpointEchoClient client = ((EndpointEchoClient) (session.getUserProperties().get("endpoint")));
        String echoed = client.textCapture.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed message", echoed, Matchers.is("Echo"));
    }

    @Test
    public void testAbstractEchoInstance() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        EndpointEchoTest.server.addBean(container);// allow to shutdown with server

        EchoStringEndpoint echoer = new EchoStringEndpoint();
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        // Issue connect using instance of class that extends abstract that extends Endpoint
        Session session = container.connectToServer(echoer, EndpointEchoTest.serverUri);
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Connected: {}", session);

        session.getBasicRemote().sendText("Echo");
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Message Sent");

        String echoed = echoer.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed message", echoed, Matchers.is("Echo"));
    }

    @Test
    public void testAbstractEchoClassref() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        EndpointEchoTest.server.addBean(container);// allow to shutdown with server

        // Issue connect using class reference (class that extends abstract that extends Endpoint)
        Session session = container.connectToServer(EchoStringEndpoint.class, EndpointEchoTest.serverUri);
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Connected: {}", session);

        session.getBasicRemote().sendText("Echo");
        if (EndpointEchoTest.LOG.isDebugEnabled())
            EndpointEchoTest.LOG.debug("Client Message Sent");

        EchoStringEndpoint client = ((EchoStringEndpoint) (session.getUserProperties().get("endpoint")));
        String echoed = client.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed message", echoed, Matchers.is("Echo"));
    }
}

