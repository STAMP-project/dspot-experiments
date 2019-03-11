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
package org.eclipse.jetty.fcgi.server.proxy;


import java.io.IOException;
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


public class TryFilesFilterTest {
    private Server server;

    private ServerConnector connector;

    private ServerConnector sslConnector;

    private HttpClient client;

    private String forwardPath;

    @Test
    public void testHTTPSRequestIsForwarded() throws Exception {
        final String path = "/one/";
        prepare(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                Assertions.assertTrue("https".equalsIgnoreCase(req.getScheme()));
                Assertions.assertTrue(req.isSecure());
                Assertions.assertEquals(forwardPath, req.getRequestURI());
                Assertions.assertTrue(req.getQueryString().endsWith(path));
            }
        });
        ContentResponse response = client.newRequest("localhost", sslConnector.getLocalPort()).scheme("https").path(path).send();
        Assertions.assertEquals(200, response.getStatus());
    }
}

