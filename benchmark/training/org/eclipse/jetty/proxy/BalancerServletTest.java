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
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BalancerServletTest {
    private static final String CONTEXT_PATH = "/context";

    private static final String SERVLET_PATH = "/mapping";

    private boolean stickySessions;

    private Server server1;

    private Server server2;

    private Server balancer;

    private HttpClient client;

    @Test
    public void testRoundRobinBalancer() throws Exception {
        stickySessions = false;
        startBalancer(BalancerServletTest.CounterServlet.class);
        for (int i = 0; i < 10; i++) {
            byte[] responseBytes = sendRequestToBalancer("/roundRobin");
            String returnedCounter = readFirstLine(responseBytes);
            // Counter should increment every other request
            String expectedCounter = String.valueOf((i / 2));
            Assertions.assertEquals(expectedCounter, returnedCounter);
        }
    }

    @Test
    public void testStickySessionsBalancer() throws Exception {
        stickySessions = true;
        startBalancer(BalancerServletTest.CounterServlet.class);
        for (int i = 0; i < 10; i++) {
            byte[] responseBytes = sendRequestToBalancer("/stickySessions");
            String returnedCounter = readFirstLine(responseBytes);
            // Counter should increment every request
            String expectedCounter = String.valueOf(i);
            Assertions.assertEquals(expectedCounter, returnedCounter);
        }
    }

    @Test
    public void testProxyPassReverse() throws Exception {
        stickySessions = false;
        startBalancer(BalancerServletTest.RelocationServlet.class);
        byte[] responseBytes = sendRequestToBalancer("/index.html");
        String msg = readFirstLine(responseBytes);
        Assertions.assertEquals("success", msg);
    }

    public static final class CounterServlet extends HttpServlet {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            // Force session creation
            req.getSession();
            resp.setContentType("text/plain");
            resp.getWriter().print(counter.getAndIncrement());
        }
    }

    public static final class RelocationServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            if (req.getRequestURI().endsWith("/index.html")) {
                resp.sendRedirect((((("http://localhost:" + (req.getLocalPort())) + (req.getContextPath())) + (req.getServletPath())) + "/other.html?secret=pipo+molo"));
            } else {
                resp.setContentType("text/plain");
                if ("pipo molo".equals(req.getParameter("secret")))
                    resp.getWriter().println("success");

            }
        }
    }
}

