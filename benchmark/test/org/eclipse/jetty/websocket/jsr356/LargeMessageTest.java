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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class LargeMessageTest {
    private static final int LARGER_THAN_DEFAULT_SIZE;

    private Server server;

    static {
        WebSocketPolicy defaultPolicy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        LARGER_THAN_DEFAULT_SIZE = (defaultPolicy.getMaxTextMessageSize()) * 3;
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testLargeEcho_AsEndpointInstance() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        server.addBean(container);// allow to shutdown with server

        container.setDefaultMaxTextMessageBufferSize(LargeMessageTest.LARGER_THAN_DEFAULT_SIZE);
        EndpointEchoClient echoer = new EndpointEchoClient();
        MatcherAssert.assertThat(echoer, Matchers.instanceOf(Endpoint.class));
        URI wsUri = WSURI.toWebsocket(server.getURI()).resolve("/");
        // Issue connect using instance of class that extends Endpoint
        Session session = container.connectToServer(echoer, wsUri);
        byte[] buf = new byte[LargeMessageTest.LARGER_THAN_DEFAULT_SIZE];
        Arrays.fill(buf, ((byte) ('x')));
        String message = new String(buf, StandardCharsets.UTF_8);
        session.getBasicRemote().sendText(message);
        String echoed = echoer.textCapture.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed", echoed, Matchers.is(message));
    }
}

