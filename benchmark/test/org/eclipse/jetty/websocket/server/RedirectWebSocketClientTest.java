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
import java.util.concurrent.Future;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class RedirectWebSocketClientTest {
    public static Server server;

    public static URI serverWsUri;

    public static URI serverWssUri;

    @Test
    public void testRedirect() throws Exception {
        SslContextFactory ssl = RedirectWebSocketClientTest.newSslContextFactory();
        ssl.setTrustAll(false);
        ssl.setEndpointIdentificationAlgorithm(null);
        HttpClient httpClient = new HttpClient(ssl);
        WebSocketClient client = new WebSocketClient(httpClient);
        client.addBean(httpClient, true);
        client.start();
        try {
            URI wsUri = RedirectWebSocketClientTest.serverWsUri.resolve("/echo");
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            Future<Session> sessionFuture = client.connect(new RedirectWebSocketClientTest.EmptyWebSocket(), wsUri, request);
            Session session = sessionFuture.get();
            MatcherAssert.assertThat(session, Matchers.is(Matchers.notNullValue()));
        } finally {
            client.stop();
        }
    }

    @WebSocket
    public static class EmptyWebSocket {}
}

