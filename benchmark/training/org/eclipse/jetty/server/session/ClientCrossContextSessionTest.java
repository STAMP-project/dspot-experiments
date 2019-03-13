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
package org.eclipse.jetty.server.session;


import HttpServletResponse.SC_OK;
import SessionCache.NEVER_EVICT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static TestServer.DEFAULT_MAX_INACTIVE;
import static TestServer.DEFAULT_SCAVENGE_SEC;


/**
 * ClientCrossContextSessionTest
 *
 * Test that a client can create a session on one context and
 * then re-use that session id on a request to another context,
 * but the session contents are separate on each.
 */
public class ClientCrossContextSessionTest {
    @Test
    public void testCrossContextDispatch() throws Exception {
        String contextA = "/contextA";
        String contextB = "/contextB";
        String servletMapping = "/server";
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new NullSessionDataStoreFactory();
        TestServer server = new TestServer(0, DEFAULT_MAX_INACTIVE, DEFAULT_SCAVENGE_SEC, cacheFactory, storeFactory);
        ClientCrossContextSessionTest.TestServletA servletA = new ClientCrossContextSessionTest.TestServletA();
        ServletHolder holderA = new ServletHolder(servletA);
        ServletContextHandler ctxA = server.addContext(contextA);
        ctxA.addServlet(holderA, servletMapping);
        ServletContextHandler ctxB = server.addContext(contextB);
        ClientCrossContextSessionTest.TestServletB servletB = new ClientCrossContextSessionTest.TestServletB();
        ServletHolder holderB = new ServletHolder(servletB);
        ctxB.addServlet(holderB, servletMapping);
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to contextA
                ContentResponse response = client.GET(((("http://localhost:" + port) + contextA) + servletMapping));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookie = sessionCookie.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                // Perform a request to contextB with the same session cookie
                Request request = client.newRequest(((("http://localhost:" + port) + contextB) + servletMapping));
                request.header("Cookie", sessionCookie);
                ContentResponse responseB = request.send();
                Assertions.assertEquals(SC_OK, responseB.getStatus());
                Assertions.assertEquals(servletA.sessionId, servletB.sessionId);
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    public static class TestServletA extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public String sessionId;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            HttpSession session = request.getSession(false);
            if (session == null) {
                session = request.getSession(true);
                sessionId = session.getId();
            }
            // Add something to the session
            session.setAttribute("A", "A");
            // Check that we don't see things put in session by contextB
            Object objectB = session.getAttribute("B");
            Assertions.assertTrue((objectB == null));
        }
    }

    public static class TestServletB extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public String sessionId;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            HttpSession session = request.getSession(false);
            if (session == null)
                session = request.getSession(true);

            sessionId = session.getId();
            // Add something to the session
            session.setAttribute("B", "B");
            // Check that we don't see things put in session by contextA
            Object objectA = session.getAttribute("A");
            Assertions.assertTrue((objectA == null));
        }
    }
}

