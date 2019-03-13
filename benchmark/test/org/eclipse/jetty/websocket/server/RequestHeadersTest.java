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


import HttpHeader.COOKIE;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.server.helper.EchoSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class RequestHeadersTest {
    private static class EchoCreator implements WebSocketCreator {
        private UpgradeRequest lastRequest;

        private UpgradeResponse lastResponse;

        private EchoSocket echoSocket = new EchoSocket();

        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            this.lastRequest = req;
            this.lastResponse = resp;
            return echoSocket;
        }

        public UpgradeRequest getLastRequest() {
            return lastRequest;
        }

        @SuppressWarnings("unused")
        public UpgradeResponse getLastResponse() {
            return lastResponse;
        }
    }

    public static class EchoRequestServlet extends WebSocketServlet {
        private static final long serialVersionUID = -6575001979901924179L;

        private final WebSocketCreator creator;

        public EchoRequestServlet(WebSocketCreator creator) {
            this.creator = creator;
        }

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.setCreator(this.creator);
        }
    }

    private static BlockheadClient client;

    private static SimpleServletServer server;

    private static RequestHeadersTest.EchoCreator echoCreator;

    @Test
    public void testAccessRequestCookies() throws Exception {
        BlockheadClientRequest request = RequestHeadersTest.client.newWsRequest(RequestHeadersTest.server.getServerUri());
        request.idleTimeout(1, TimeUnit.SECONDS);
        request.header(COOKIE, "fruit=Pear; type=Anjou");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection ignore = connFut.get(CONNECT, CONNECT_UNIT)) {
            UpgradeRequest req = RequestHeadersTest.echoCreator.getLastRequest();
            MatcherAssert.assertThat("Last Request", req, Matchers.notNullValue());
            List<HttpCookie> cookies = req.getCookies();
            MatcherAssert.assertThat("Request cookies", cookies, Matchers.notNullValue());
            MatcherAssert.assertThat("Request cookies.size", cookies.size(), Matchers.is(2));
            for (HttpCookie cookie : cookies) {
                MatcherAssert.assertThat("Cookie name", cookie.getName(), Matchers.anyOf(Matchers.is("fruit"), Matchers.is("type")));
                MatcherAssert.assertThat("Cookie value", cookie.getValue(), Matchers.anyOf(Matchers.is("Pear"), Matchers.is("Anjou")));
            }
        }
    }

    @Test
    public void testRequestURI() throws Exception {
        URI destUri = RequestHeadersTest.server.getServerUri().resolve("/?abc=x%20z&breakfast=bacon%26eggs&2*2%3d5=false");
        BlockheadClientRequest request = RequestHeadersTest.client.newWsRequest(destUri);
        request.idleTimeout(1, TimeUnit.SECONDS);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection ignore = connFut.get(CONNECT, CONNECT_UNIT)) {
            UpgradeRequest req = RequestHeadersTest.echoCreator.getLastRequest();
            MatcherAssert.assertThat("Last Request", req, Matchers.notNullValue());
            MatcherAssert.assertThat("Request.host", req.getHost(), Matchers.is(RequestHeadersTest.server.getServerUri().getHost()));
            MatcherAssert.assertThat("Request.queryString", req.getQueryString(), Matchers.is("abc=x%20z&breakfast=bacon%26eggs&2*2%3d5=false"));
            MatcherAssert.assertThat("Request.uri.path", req.getRequestURI().getPath(), Matchers.is("/"));
            MatcherAssert.assertThat("Request.uri.rawQuery", req.getRequestURI().getRawQuery(), Matchers.is("abc=x%20z&breakfast=bacon%26eggs&2*2%3d5=false"));
            MatcherAssert.assertThat("Request.uri.query", req.getRequestURI().getQuery(), Matchers.is("abc=x z&breakfast=bacon&eggs&2*2=5=false"));
        }
    }
}

