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
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReverseProxyTest {
    private Server server;

    private ServerConnector serverConnector;

    private Server proxy;

    private ServerConnector proxyConnector;

    private HttpClient client;

    @Test
    public void testHostHeaderUpdatedWhenSentToServer() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                Assertions.assertEquals("127.0.0.1", request.getServerName());
                Assertions.assertEquals(serverConnector.getLocalPort(), request.getServerPort());
            }
        });
        startProxy(null);
        startClient();
        ContentResponse response = client.newRequest("localhost", proxyConnector.getLocalPort()).send();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void testHostHeaderPreserved() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                Assertions.assertEquals("localhost", request.getServerName());
                Assertions.assertEquals(proxyConnector.getLocalPort(), request.getServerPort());
            }
        });
        startProxy(new HashMap<String, String>() {
            {
                put("preserveHost", "true");
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", proxyConnector.getLocalPort()).send();
        Assertions.assertEquals(200, response.getStatus());
    }
}

