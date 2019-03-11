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
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * RedirectSessionTest
 *
 * Test that creating a session and then doing a redirect preserves the session.
 */
public class RedirectSessionTest {
    @Test
    public void testSessionRedirect() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer testServer = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        ServletContextHandler testServletContextHandler = testServer.addContext("/context");
        testServletContextHandler.addServlet(RedirectSessionTest.Servlet1.class, "/one");
        testServletContextHandler.addServlet(RedirectSessionTest.Servlet2.class, "/two");
        try {
            testServer.start();
            int serverPort = testServer.getPort();
            HttpClient client = new HttpClient();
            client.setFollowRedirects(true);// ensure client handles redirects

            client.start();
            try {
                // make a request to the first servlet, which will redirect
                ContentResponse response = client.GET((("http://localhost:" + serverPort) + "/context/one"));
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            testServer.stop();
        }
    }

    public static class Servlet1 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // create a session
            HttpSession session = request.getSession(true);
            Assertions.assertNotNull(session);
            session.setAttribute("servlet1", "servlet1");
            response.sendRedirect("/context/two");
        }
    }

    public static class Servlet2 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // the session should exist after the redirect
            HttpSession sess = request.getSession(false);
            Assertions.assertNotNull(sess);
            Assertions.assertNotNull(sess.getAttribute("servlet1"));
            Assertions.assertEquals("servlet1", sess.getAttribute("servlet1"));
        }
    }
}

