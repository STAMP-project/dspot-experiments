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


import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import SessionCache.NEVER_EVICT;
import TestServer.DEFAULT_SCAVENGE_SEC;
import java.io.IOException;
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
 * ModifyMaxInactiveIntervalTest
 */
public class ModifyMaxInactiveIntervalTest extends AbstractTestBase {
    public static int __scavenge = 1;

    /**
     * Test that setting an integer overflow valued max inactive interval
     * results in an immortal session (value -1).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHugeMaxInactiveInterval() throws Exception {
        int inactivePeriod = (Integer.MAX_VALUE) * 60;// integer overflow

        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, inactivePeriod, ModifyMaxInactiveIntervalTest.__scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                String id = TestServer.extractSessionId(sessionCookie);
                // check that the maxInactive is -1
                Session s = ctxA.getSessionHandler().getSession(id);
                Assertions.assertEquals((-1), s.getMaxInactiveInterval());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testReduceMaxInactiveInterval() throws Exception {
        int oldMaxInactive = 30;
        int newMaxInactive = 1;
        int scavengeSec = ModifyMaxInactiveIntervalTest.__scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, scavengeSec, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to reduce the maxinactive interval
                Request request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // Wait for the session to expire
                Thread.sleep(TimeUnit.SECONDS.toMillis((newMaxInactive + scavengeSec)));
                // do another request using the cookie to ensure the session is NOT there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testIncreaseMaxInactiveInterval() throws Exception {
        int oldMaxInactive = 1;
        int newMaxInactive = 10;
        int scavengeSec = ModifyMaxInactiveIntervalTest.__scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, scavengeSec, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to increase the maxinactive interval
                Request request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // wait until the old inactive interval should have expired
                Thread.sleep(TimeUnit.SECONDS.toMillis((scavengeSec + oldMaxInactive)));
                // do another request using the cookie to ensure the session is still there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSetMaxInactiveIntervalWithImmortalSessionAndEviction() throws Exception {
        int oldMaxInactive = -1;
        int newMaxInactive = 120;// 2min

        int evict = 2;
        int sleep = evict;
        int scavenge = ModifyMaxInactiveIntervalTest.__scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(evict);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to reduce the maxinactive interval
                Request request = client.newRequest(((((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive) + "&wait=") + sleep));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // do another request using the cookie to ensure the session is still there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSetMaxInactiveIntervalWithNonImmortalSessionAndEviction() throws Exception {
        int oldMaxInactive = 10;
        int newMaxInactive = 2;
        int evict = 4;
        int sleep = evict;
        int scavenge = ModifyMaxInactiveIntervalTest.__scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(evict);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to reduce the maxinactive interval
                Request request = client.newRequest(((((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive) + "&wait=") + sleep));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // do another request using the cookie to ensure the session is still there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testChangeMaxInactiveIntervalForImmortalSessionNoEviction() throws Exception {
        int oldMaxInactive = -1;
        int newMaxInactive = 120;
        int scavenge = ModifyMaxInactiveIntervalTest.__scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to change the maxinactive interval
                Request request = client.newRequest(((((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive) + "&wait=") + 2));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // do another request using the cookie to ensure the session is still there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testNoExpireSessionInUse() throws Exception {
        int maxInactive = 3;
        int scavenge = ModifyMaxInactiveIntervalTest.__scavenge;
        int sleep = maxInactive + scavenge;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, maxInactive, scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request that will sleep long enough for the session expiry time to have passed
                // before trying to access the session and ensure it is still there
                Request request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=sleep&val=") + sleep));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSessionExpiryAfterModifiedMaxInactiveInterval() throws Exception {
        int oldMaxInactive = 4;
        int newMaxInactive = 20;
        int sleep = oldMaxInactive + (ModifyMaxInactiveIntervalTest.__scavenge);
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, oldMaxInactive, ModifyMaxInactiveIntervalTest.__scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // do another request to change the maxinactive interval
                Request request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=change&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // wait for longer than the old inactive interval
                Thread.sleep(TimeUnit.SECONDS.toMillis(sleep));
                // do another request using the cookie to ensure the session is still there
                request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + newMaxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testGetMaxInactiveIntervalWithNegativeMaxInactiveInterval() throws Exception {
        int maxInactive = -1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = createSessionDataStoreFactory();
        ((AbstractSessionDataStoreFactory) (storeFactory)).setGracePeriodSec(DEFAULT_SCAVENGE_SEC);
        TestServer server = new TestServer(0, maxInactive, ModifyMaxInactiveIntervalTest.__scavenge, cacheFactory, storeFactory);
        ServletContextHandler ctxA = server.addContext("/mod");
        ctxA.addServlet(ModifyMaxInactiveIntervalTest.TestModServlet.class, "/test");
        server.start();
        int port = server.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform a request to create a session
                ContentResponse response = client.GET((("http://localhost:" + port) + "/mod/test?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // Test that the maxInactiveInterval matches the expected value
                Request request = client.newRequest(((("http://localhost:" + port) + "/mod/test?action=test&val=") + maxInactive));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    public static class TestModServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                HttpSession session = request.getSession(true);
                Assertions.assertNotNull(session);
                return;
            }
            if ("change".equals(action)) {
                // change the expiry time for the session, maybe sleeping before the change
                String tmp = request.getParameter("val");
                int interval = -1;
                interval = (tmp == null) ? -1 : Integer.parseInt(tmp);
                tmp = request.getParameter("wait");
                int wait = (tmp == null) ? 0 : Integer.parseInt(tmp);
                if (wait > 0) {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(wait));
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
                HttpSession session = request.getSession(false);
                if (session == null)
                    throw new ServletException("Session is null for action=change");

                if (interval > 0)
                    session.setMaxInactiveInterval(interval);

                session = request.getSession(false);
                if (session == null)
                    throw new ServletException("Null session after maxInactiveInterval change");

                return;
            }
            if ("sleep".equals(action)) {
                // sleep before trying to access the session
                HttpSession session = request.getSession(false);
                if (session == null)
                    throw new ServletException("Session is null for action=sleep");

                String tmp = request.getParameter("val");
                int interval = 0;
                interval = (tmp == null) ? 0 : Integer.parseInt(tmp);
                if (interval > 0) {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(interval));
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
                session = request.getSession(false);
                if (session == null)
                    throw new ServletException("Session null after sleep");

                return;
            }
            if ("test".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session == null) {
                    response.sendError(500, "Session does not exist");
                    return;
                }
                String tmp = request.getParameter("val");
                int interval = 0;
                interval = (tmp == null) ? 0 : Integer.parseInt(tmp);
                Assertions.assertEquals(interval, session.getMaxInactiveInterval());
                return;
            }
        }
    }
}

