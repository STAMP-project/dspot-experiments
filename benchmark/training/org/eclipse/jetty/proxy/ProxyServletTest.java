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
package org.eclipse.jetty.proxy;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ProxyServletTest {
    private static final String PROXIED_HEADER = "X-Proxied";

    private HttpClient client;

    private Server proxy;

    private ServerConnector proxyConnector;

    private ServletContextHandler proxyContext;

    private AbstractProxyServlet proxyServlet;

    private Server server;

    private ServerConnector serverConnector;

    /**
     * Only tests overridden ProxyServlet behavior, see CachingProxyServlet
     */
    @Test
    public void testCachingProxy() throws Exception {
        final byte[] content = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        startServer(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                if ((req.getHeader("Via")) != null)
                    resp.addHeader(ProxyServletTest.PROXIED_HEADER, "true");

                resp.getOutputStream().write(content);
            }
        });
        startProxy(CachingProxyServlet.class);
        startClient();
        // First request
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        MatcherAssert.assertThat(response.getHeaders(), containsHeader(ProxyServletTest.PROXIED_HEADER));
        Assertions.assertArrayEquals(content, response.getContent());
        // Second request should be cached
        response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        MatcherAssert.assertThat(response.getHeaders(), containsHeader(CachingProxyServlet.CACHE_HEADER));
        Assertions.assertArrayEquals(content, response.getContent());
    }
}

