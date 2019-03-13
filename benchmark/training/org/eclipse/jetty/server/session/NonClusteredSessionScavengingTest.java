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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * NonClusteredSessionScavengingTest
 *
 * Create a session, wait for it to be scavenged, re-present the cookie and check that  a
 * new session is created.
 */
public class NonClusteredSessionScavengingTest extends AbstractTestBase {
    public SessionDataStore _dataStore;

    @Test
    public void testNoScavenging() throws Exception {
        String contextPath = "/";
        String servletMapping = "/server";
        int inactivePeriod = 3;
        int scavengePeriod = 0;// turn off scavenging

        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context1 = server1.addContext(contextPath);
        context1.addServlet(NonClusteredSessionScavengingTest.TestServlet.class, servletMapping);
        TestHttpSessionListener listener = new TestHttpSessionListener();
        context1.getSessionHandler().addEventListener(listener);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        context1.addEventListener(scopeListener);
        try {
            server1.start();
            int port1 = server1.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                String url = (("http://localhost:" + port1) + contextPath) + (servletMapping.substring(1));
                // Create the session
                CountDownLatch latch = new CountDownLatch(1);
                scopeListener.setExitSynchronizer(latch);
                ContentResponse response1 = client.GET((url + "?action=create"));
                Assertions.assertEquals(SC_OK, response1.getStatus());
                String sessionCookie = response1.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // ensure request fully finished processing
                latch.await(5, TimeUnit.SECONDS);
                // test session was created
                SessionHandler m1 = context1.getSessionHandler();
                Assertions.assertEquals(1, m1.getSessionsCreated());
                // Wait a while to ensure that the session should have expired, if the
                // scavenger was running
                pause((2 * inactivePeriod));
                Assertions.assertEquals(1, m1.getSessionsCreated());
                if (m1 instanceof TestSessionHandler) {
                    assertCandidatesForExpiry(0);
                }
                // check the session listener did not get called
                Assertions.assertTrue(listener.destroyedSessions.isEmpty());
            } finally {
                client.stop();
            }
        } finally {
            server1.stop();
        }
    }

    @Test
    public void testNewSession() throws Exception {
        String servletMapping = "/server";
        int scavengePeriod = 3;
        int maxInactivePeriod = 1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        setGracePeriodSec(scavengePeriod);
        TestServer server = new TestServer(0, maxInactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext("/");
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        context.addEventListener(scopeListener);
        _dataStore = context.getSessionHandler().getSessionCache().getSessionDataStore();
        context.addServlet(NonClusteredSessionScavengingTest.TestServlet.class, servletMapping);
        String contextPath = "/";
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                CountDownLatch latch = new CountDownLatch(1);
                scopeListener.setExitSynchronizer(latch);
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + (servletMapping.substring(1))) + "?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // ensure request fully finished processing
                latch.await(5, TimeUnit.SECONDS);
                // Let's wait for the scavenger to run
                pause((maxInactivePeriod + scavengePeriod));
                Assertions.assertFalse(_dataStore.exists(TestServer.extractSessionId(sessionCookie)));
                // The session should not be there anymore, but we present an old cookie
                // The server should create a new session.
                Request request = client.newRequest((((("http://localhost:" + port) + contextPath) + (servletMapping.substring(1))) + "?action=old-create"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie2 = response.getHeaders().get("Set-Cookie");
                Assertions.assertNotEquals(TestServer.extractSessionId(sessionCookie), TestServer.extractSessionId(sessionCookie2));
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testImmortalSession() throws Exception {
        String servletMapping = "/server";
        int scavengePeriod = 1;
        int maxInactivePeriod = 0;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        setGracePeriodSec(scavengePeriod);
        TestServer server = new TestServer(0, maxInactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext("/");
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        context.addEventListener(scopeListener);
        _dataStore = context.getSessionHandler().getSessionCache().getSessionDataStore();
        context.addServlet(NonClusteredSessionScavengingTest.TestServlet.class, servletMapping);
        String contextPath = "/";
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                // create an immortal session
                CountDownLatch latch = new CountDownLatch(1);
                scopeListener.setExitSynchronizer(latch);
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + (servletMapping.substring(1))) + "?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // ensure request fully finished processing
                latch.await(5, TimeUnit.SECONDS);
                // Let's wait for the scavenger to run
                pause((2 * scavengePeriod));
                Assertions.assertTrue(_dataStore.exists(TestServer.extractSessionId(sessionCookie)));
                // Test that the session is still there
                Request request = client.newRequest((((("http://localhost:" + port) + contextPath) + (servletMapping.substring(1))) + "?action=old-test"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    public static class TestServlet extends HttpServlet {
        String id;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                HttpSession session = request.getSession(true);
                Assertions.assertTrue(session.isNew());
                id = session.getId();
            } else
                if ("old-create".equals(action)) {
                    HttpSession s = request.getSession(false);
                    Assertions.assertNull(s);
                    s = request.getSession(true);
                    Assertions.assertNotNull(s);
                    Assertions.assertFalse(s.getId().equals(id));
                } else
                    if ("old-test".equals(action)) {
                        HttpSession s = request.getSession(false);
                        Assertions.assertNotNull(s);
                        Assertions.assertTrue(s.getId().equals(id));
                    } else {
                        Assertions.fail("Unknown servlet action");
                    }


        }
    }
}

