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
package org.eclipse.jetty.servlet;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class RequestHeadersTest {
    @SuppressWarnings("serial")
    private static class RequestHeaderServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            out.printf("X-Camel-Type = %s", req.getHeader("X-Camel-Type"));
        }
    }

    private static Server server;

    private static ServerConnector connector;

    private static URI serverUri;

    @Test
    public void testGetLowercaseHeader() throws IOException {
        HttpURLConnection http = null;
        try {
            http = ((HttpURLConnection) (RequestHeadersTest.serverUri.toURL().openConnection()));
            // Set header in all lowercase
            http.setRequestProperty("x-camel-type", "bactrian");
            try (InputStream in = http.getInputStream()) {
                String resp = IO.toString(in, StandardCharsets.UTF_8);
                MatcherAssert.assertThat("Response", resp, Matchers.is("X-Camel-Type = bactrian"));
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}

