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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ServletContextResourcesTest {
    public static class ResourceAsStreamServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("utf-8");
            String pathInfo = req.getPathInfo();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = req.getServletContext().getResourceAsStream(pathInfo)) {
                if (in == null) {
                    out.write("<null>".getBytes(StandardCharsets.UTF_8));
                } else {
                    IO.copy(in, out);
                }
            }
            String resourceContents = new String(out.toByteArray(), StandardCharsets.UTF_8);
            resp.getWriter().printf("Resource '%s': %s", pathInfo, resourceContents);
        }
    }

    private Server server;

    private LocalConnector connector;

    private ServletContextHandler context;

    @Test
    public void testGetResourceAsStream_Root() throws Exception {
        context.addServlet(ServletContextResourcesTest.ResourceAsStreamServlet.class, "/*");
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /context/ HTTP/1.1\r\n");
        req1.append("Host: local\r\n");
        req1.append("Connection: close\r\n");
        req1.append("\r\n");
        String response = connector.getResponse(req1.toString());
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Resource '/': <null>"));
    }

    @Test
    public void testGetResourceAsStream_Content() throws Exception {
        context.addServlet(ServletContextResourcesTest.ResourceAsStreamServlet.class, "/*");
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /context/content.txt HTTP/1.1\r\n");
        req1.append("Host: local\r\n");
        req1.append("Connection: close\r\n");
        req1.append("\r\n");
        String response = connector.getResponse(req1.toString());
        MatcherAssert.assertThat("Response", response, Matchers.containsString("Resource '/content.txt': content goes here"));
    }
}

