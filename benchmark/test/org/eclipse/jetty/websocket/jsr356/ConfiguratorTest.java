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


import ClientEndpointConfig.Builder;
import ClientEndpointConfig.Configurator;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests of {@link javax.websocket.ClientEndpointConfig.Configurator}
 */
public class ConfiguratorTest {
    public class TrackingConfigurator extends ClientEndpointConfig.Configurator {
        public HandshakeResponse response;

        public Map<String, List<String>> request;

        @Override
        public void afterResponse(HandshakeResponse hr) {
            this.response = hr;
        }

        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            this.request = headers;
        }
    }

    private static Server server;

    private static EchoHandler handler;

    private static URI serverUri;

    @Test
    public void testEndpointHandshakeInfo() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        ConfiguratorTest.server.addBean(container);// allow to shutdown with server

        EndpointEchoClient echoer = new EndpointEchoClient();
        // Build Config
        ClientEndpointConfig.Builder cfgbldr = Builder.create();
        ConfiguratorTest.TrackingConfigurator configurator = new ConfiguratorTest.TrackingConfigurator();
        cfgbldr.configurator(configurator);
        ClientEndpointConfig config = cfgbldr.build();
        // Connect
        Session session = container.connectToServer(echoer, config, ConfiguratorTest.serverUri);
        // Send Simple Message
        session.getBasicRemote().sendText("Echo");
        // Wait for echo
        String echoed = echoer.textCapture.messages.poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Echoed", echoed, Matchers.is("Echo"));
        // Validate client side configurator use
        MatcherAssert.assertThat("configurator.request", configurator.request, Matchers.notNullValue());
        MatcherAssert.assertThat("configurator.response", configurator.response, Matchers.notNullValue());
    }
}

