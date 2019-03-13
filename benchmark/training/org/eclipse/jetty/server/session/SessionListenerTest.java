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
import java.io.Serializable;
import java.net.HttpCookie;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * SessionListenerTest
 *
 * Test that session listeners are called.
 */
public class SessionListenerTest {
    /**
     * Test that listeners are called when a session is deliberately invalidated.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testListenerWithInvalidation() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int inactivePeriod = 6;
        int scavengePeriod = -1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        setGracePeriodSec(scavengePeriod);
        TestServer server = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext(contextPath);
        TestHttpSessionListener listener = new TestHttpSessionListener(true);
        context.getSessionHandler().addEventListener(listener);
        SessionListenerTest.TestServlet servlet = new SessionListenerTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        context.addServlet(holder, servletMapping);
        try {
            server.start();
            int port1 = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                String url = (("http://localhost:" + port1) + contextPath) + servletMapping;
                // Create the session
                ContentResponse response1 = client.GET((url + "?action=init"));
                Assertions.assertEquals(SC_OK, response1.getStatus());
                String sessionCookie = response1.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                Assertions.assertTrue(SessionListenerTest.TestServlet.bindingListener.bound);
                String sessionId = TestServer.extractSessionId(sessionCookie);
                MatcherAssert.assertThat(sessionId, Matchers.isIn(listener.createdSessions));
                // Make a request which will invalidate the existing session
                Request request2 = client.newRequest((url + "?action=test"));
                ContentResponse response2 = request2.send();
                Assertions.assertEquals(SC_OK, response2.getStatus());
                Assertions.assertTrue(SessionListenerTest.TestServlet.bindingListener.unbound);
                Assertions.assertTrue(listener.destroyedSessions.contains(sessionId));
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    /**
     * Test that listeners are called when a session expires.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionExpiresWithListener() throws Exception {
        String contextPath = "/";
        String servletMapping = "/server";
        int inactivePeriod = 3;
        int scavengePeriod = 1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        setGracePeriodSec(scavengePeriod);
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        SessionListenerTest.TestServlet servlet = new SessionListenerTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler context = server1.addContext(contextPath);
        context.addServlet(holder, servletMapping);
        TestHttpSessionListener listener = new TestHttpSessionListener(true);
        context.getSessionHandler().addEventListener(listener);
        server1.start();
        int port1 = server1.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            String url = (("http://localhost:" + port1) + contextPath) + (servletMapping.substring(1));
            // make a request to set up a session on the server
            ContentResponse response1 = client.GET((url + "?action=init"));
            Assertions.assertEquals(SC_OK, response1.getStatus());
            String sessionCookie = response1.getHeaders().get("Set-Cookie");
            Assertions.assertTrue((sessionCookie != null));
            String sessionId = TestServer.extractSessionId(sessionCookie);
            MatcherAssert.assertThat(sessionId, Matchers.isIn(listener.createdSessions));
            // and wait until the session should have expired
            Thread.currentThread().sleep(TimeUnit.SECONDS.toMillis((inactivePeriod + scavengePeriod)));
            MatcherAssert.assertThat(sessionId, Matchers.isIn(listener.destroyedSessions));
            Assertions.assertNull(listener.ex);
        } finally {
            server1.stop();
        }
    }

    /**
     * Check that a session that is expired cannot be reused, and expiry listeners are called for it
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExpiredSession() throws Exception {
        String contextPath = "/";
        String servletMapping = "/server";
        int inactivePeriod = 4;
        int scavengePeriod = 1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        setGracePeriodSec(scavengePeriod);
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        SessionListenerTest.SimpleTestServlet servlet = new SessionListenerTest.SimpleTestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler context = server1.addContext(contextPath);
        context.addServlet(holder, servletMapping);
        TestHttpSessionListener listener = new TestHttpSessionListener();
        context.getSessionHandler().addEventListener(listener);
        server1.start();
        int port1 = server1.getPort();
        try {
            // save a session that has already expired
            long now = System.currentTimeMillis();
            SessionData data = context.getSessionHandler().getSessionCache().getSessionDataStore().newSessionData("1234", (now - 10), (now - 5), (now - 10), 30000);
            data.setExpiry(100);// make it expired a long time ago

            context.getSessionHandler().getSessionCache().getSessionDataStore().store("1234", data);
            HttpClient client = new HttpClient();
            client.start();
            port1 = server1.getPort();
            String url = (("http://localhost:" + port1) + contextPath) + (servletMapping.substring(1));
            // make another request using the id of the expired session
            Request request = client.newRequest((url + "?action=test"));
            request.cookie(new HttpCookie("JSESSIONID", "1234"));
            ContentResponse response = request.send();
            Assertions.assertEquals(SC_OK, response.getStatus());
            // should be a new session id
            String cookie2 = response.getHeaders().get("Set-Cookie");
            Assertions.assertNotEquals("1234", TestServer.extractSessionId(cookie2));
            Assertions.assertTrue(listener.destroyedSessions.contains("1234"));
            Assertions.assertNull(listener.ex);
        } finally {
            server1.stop();
        }
    }

    public static class MySessionBindingListener implements Serializable , HttpSessionBindingListener {
        private static final long serialVersionUID = 1L;

        boolean unbound = false;

        boolean bound = false;

        public void valueUnbound(HttpSessionBindingEvent event) {
            unbound = true;
        }

        public void valueBound(HttpSessionBindingEvent event) {
            bound = true;
        }
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public static final SessionListenerTest.MySessionBindingListener bindingListener = new SessionListenerTest.MySessionBindingListener();

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("init".equals(action)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("foo", SessionListenerTest.TestServlet.bindingListener);
                Assertions.assertNotNull(session);
            } else
                if ("test".equals(action)) {
                    HttpSession session = request.getSession(false);
                    Assertions.assertNotNull(session);
                    // invalidate existing session
                    session.invalidate();
                }

        }
    }

    public static class SimpleTestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("test".equals(action)) {
                HttpSession session = request.getSession(true);
                Assertions.assertTrue((session != null));
            }
        }
    }
}

