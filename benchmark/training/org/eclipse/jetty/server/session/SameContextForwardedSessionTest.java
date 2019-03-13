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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.RequestDispatcher;
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


/**
 * SameContextForwardedSessionTest
 *
 * Test that creating a session inside a forward on the same context works, and that
 * attributes set after the forward returns are preserved.
 */
public class SameContextForwardedSessionTest {
    @Test
    public void testSessionCreateInForward() throws Exception {
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer testServer = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        ServletContextHandler testServletContextHandler = testServer.addContext("/context");
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        testServletContextHandler.addEventListener(scopeListener);
        ServletHolder holder = new ServletHolder(new SameContextForwardedSessionTest.Servlet1());
        testServletContextHandler.addServlet(holder, "/one");
        testServletContextHandler.addServlet(SameContextForwardedSessionTest.Servlet2.class, "/two");
        testServletContextHandler.addServlet(SameContextForwardedSessionTest.Servlet3.class, "/three");
        testServletContextHandler.addServlet(SameContextForwardedSessionTest.Servlet4.class, "/four");
        try {
            testServer.start();
            int serverPort = testServer.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                // make a request to the first servlet, which will forward it to other servlets
                CountDownLatch latch = new CountDownLatch(1);
                scopeListener.setExitSynchronizer(latch);
                ContentResponse response = client.GET((("http://localhost:" + serverPort) + "/context/one"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // wait until all of the request handling has finished
                latch.await(5, TimeUnit.SECONDS);
                // test that the session was created, and that it contains the attributes from servlet3 and servlet1
                testServletContextHandler.getSessionHandler().getSessionCache().contains(TestServer.extractSessionId(sessionCookie));
                testServletContextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(TestServer.extractSessionId(sessionCookie));
                // Make a fresh request
                Request request = client.newRequest((("http://localhost:" + serverPort) + "/context/four"));
                response = request.send();
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
            // Don't create a session, just forward to another session in the same context
            Assertions.assertNull(request.getSession(false));
            // The session will be created by the other servlet, so will exist as this dispatch returns
            RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/two");
            dispatcher.forward(request, response);
            HttpSession sess = request.getSession(false);
            Assertions.assertNotNull(sess);
            Assertions.assertNotNull(sess.getAttribute("servlet3"));
            sess.setAttribute("servlet1", "servlet1");
        }
    }

    public static class Servlet2 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // forward to yet another servlet to do the creation
            Assertions.assertNull(request.getSession(false));
            RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/three");
            dispatcher.forward(request, response);
            // the session should exist after the forward
            HttpSession sess = request.getSession(false);
            Assertions.assertNotNull(sess);
            Assertions.assertNotNull(sess.getAttribute("servlet3"));
        }
    }

    public static class Servlet3 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // No session yet
            Assertions.assertNull(request.getSession(false));
            // Create it
            HttpSession session = request.getSession();
            Assertions.assertNotNull(session);
            // Set an attribute on it
            session.setAttribute("servlet3", "servlet3");
        }
    }

    public static class Servlet4 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // Check that the session contains attributes set during and after the session forward
            HttpSession session = request.getSession();
            Assertions.assertNotNull(session);
            Assertions.assertNotNull(session.getAttribute("servlet1"));
            Assertions.assertNotNull(session.getAttribute("servlet3"));
        }
    }
}

