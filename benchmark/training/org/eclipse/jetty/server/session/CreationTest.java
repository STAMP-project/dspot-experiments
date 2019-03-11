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


import HttpHeader.SET_COOKIE;
import HttpServletResponse.SC_OK;
import SessionCache.EVICT_ON_SESSION_EXIT;
import SessionCache.NEVER_EVICT;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * CreationTest
 *
 * Test combinations of creating, forwarding and invalidating
 * a session.
 */
public class CreationTest {
    /**
     * Test creating a session when the cache is set to
     * evict after the request exits.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateWithEviction() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(EVICT_ON_SESSION_EXIT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        servlet.setStore(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore());
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = ((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=create&check=false";
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            // make a request to set up a session on the server
            ContentResponse response = client.GET(url);
            Assertions.assertEquals(SC_OK, response.getStatus());
            String sessionCookie = response.getHeaders().get("Set-Cookie");
            Assertions.assertTrue((sessionCookie != null));
            // ensure request has finished being handled
            synchronizer.await(5, TimeUnit.SECONDS);
            // session should now be evicted from the cache
            String id = TestServer.extractSessionId(sessionCookie);
            Assertions.assertFalse(contextHandler.getSessionHandler().getSessionCache().contains(id));
            Assertions.assertTrue(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(id));
            synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            // make another request for the same session
            Request request = client.newRequest((((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=test"));
            response = request.send();
            Assertions.assertEquals(SC_OK, response.getStatus());
            // ensure request has finished being handled
            synchronizer.await(5, TimeUnit.SECONDS);
            // session should now be evicted from the cache again
            Assertions.assertFalse(contextHandler.getSessionHandler().getSessionCache().contains(TestServer.extractSessionId(sessionCookie)));
        } finally {
            server1.stop();
        }
    }

    /**
     * Create and then invalidate a session in the same request.
     * Set SessionCache.setSaveOnCreate(false), so that the creation
     * and immediate invalidation of the session means it is never stored.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateAndInvalidateNoSave() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int inactivePeriod = 20;
        int scavengePeriod = 3;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        servlet.setStore(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore());
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = ((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=createinv&check=false";
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            // make a request to set up a session on the server
            ContentResponse response = client.GET(url);
            Assertions.assertEquals(SC_OK, response.getStatus());
            // ensure request has finished being handled
            synchronizer.await(5, TimeUnit.SECONDS);
            // check that the session does not exist
            Assertions.assertFalse(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
        } finally {
            server1.stop();
        }
    }

    /**
     * Create and then invalidate a session in the same request.
     * Use SessionCache.setSaveOnCreate(true) and verify the session
     * exists before it is invalidated.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateAndInvalidateWithSave() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int inactivePeriod = 20;
        int scavengePeriod = 3;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        cacheFactory.setSaveOnCreate(true);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        servlet.setStore(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore());
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = ((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=createinv&check=true";
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            // make a request to set up a session on the server
            ContentResponse response = client.GET(url);
            Assertions.assertEquals(SC_OK, response.getStatus());
            synchronizer.await(5, TimeUnit.SECONDS);
            // check that the session does not exist
            Assertions.assertFalse(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
        } finally {
            server1.stop();
        }
    }

    /**
     * Create and then invalidate and then create a session in the same request
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateInvalidateCreate() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int inactivePeriod = 20;
        int scavengePeriod = 3;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        servlet.setStore(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore());
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = ((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=createinvcreate&check=false";
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            // make a request to set up a session on the server
            ContentResponse response = client.GET(url);
            Assertions.assertEquals(SC_OK, response.getStatus());
            // ensure request has finished being handled
            synchronizer.await(5, TimeUnit.SECONDS);
            // check that the session does not exist
            Assertions.assertTrue(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
            MatcherAssert.assertThat(response.getHeaders().getValuesList(SET_COOKIE).size(), Matchers.is(1));
        } finally {
            server1.stop();
        }
    }

    /**
     * Create a session in a context, forward to another context and create a
     * session in it too. Check that both sessions exist after the response
     * completes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateForward() throws Exception {
        String contextPath = "";
        String contextB = "/contextB";
        String servletMapping = "/server";
        int inactivePeriod = 20;
        int scavengePeriod = 3;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        ServletContextHandler ctxB = server1.addContext(contextB);
        ctxB.addServlet(CreationTest.TestServletB.class, servletMapping);
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = (("http://localhost:" + port1) + contextPath) + servletMapping;
            // make a request to set up a session on the server
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            ContentResponse response = client.GET((url + "?action=forward"));
            Assertions.assertEquals(SC_OK, response.getStatus());
            // wait for request to have exited server completely
            synchronizer.await(5, TimeUnit.SECONDS);
            // check that the sessions exist persisted
            Assertions.assertTrue(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
            Assertions.assertTrue(ctxB.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
        } finally {
            server1.stop();
        }
    }

    /**
     * Create a session in one context, forward to another context and create another session
     * in it, then invalidate the session in the original context: that should invalidate the
     * session in both contexts and no session should exist after the response completes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSessionCreateForwardAndInvalidate() throws Exception {
        String contextPath = "";
        String contextB = "/contextB";
        String servletMapping = "/server";
        int inactivePeriod = 20;
        int scavengePeriod = 3;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server1 = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        CreationTest.TestServlet servlet = new CreationTest.TestServlet();
        ServletHolder holder = new ServletHolder(servlet);
        ServletContextHandler contextHandler = server1.addContext(contextPath);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        contextHandler.addEventListener(scopeListener);
        contextHandler.addServlet(holder, servletMapping);
        ServletContextHandler ctxB = server1.addContext(contextB);
        ctxB.addServlet(CreationTest.TestServletB.class, servletMapping);
        server1.start();
        int port1 = server1.getPort();
        try (StacklessLogging stackless = new StacklessLogging(Log.getLogger("org.eclipse.jetty.server.session"))) {
            HttpClient client = new HttpClient();
            client.start();
            String url = (("http://localhost:" + port1) + contextPath) + servletMapping;
            // make a request to set up a session on the server
            CountDownLatch synchronizer = new CountDownLatch(1);
            scopeListener.setExitSynchronizer(synchronizer);
            ContentResponse response = client.GET((url + "?action=forwardinv"));
            Assertions.assertEquals(SC_OK, response.getStatus());
            // wait for request to have exited server completely
            synchronizer.await(5, TimeUnit.SECONDS);
            // check that the session does not exist
            Assertions.assertFalse(contextHandler.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
            Assertions.assertFalse(ctxB.getSessionHandler().getSessionCache().getSessionDataStore().exists(servlet._id));
        } finally {
            server1.stop();
        }
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public String _id = null;

        public SessionDataStore _store;

        public void setStore(SessionDataStore store) {
            _store = store;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ((action != null) && (action.startsWith("forward"))) {
                HttpSession session = request.getSession(true);
                _id = session.getId();
                session.setAttribute("value", new Integer(1));
                ServletContext contextB = getServletContext().getContext("/contextB");
                RequestDispatcher dispatcherB = contextB.getRequestDispatcher(request.getServletPath());
                dispatcherB.forward(request, httpServletResponse);
                if (action.endsWith("inv"))
                    session.invalidate();
                else {
                    session = request.getSession(false);
                    Assertions.assertNotNull(session);
                    Assertions.assertEquals(_id, session.getId());
                    Assertions.assertNotNull(session.getAttribute("value"));
                    Assertions.assertNull(session.getAttribute("B"));// check we don't see stuff from other context

                }
                return;
            } else
                if ((action != null) && ("test".equals(action))) {
                    HttpSession session = request.getSession(false);
                    Assertions.assertNotNull(session);
                    return;
                } else
                    if ((action != null) && (action.startsWith("create"))) {
                        HttpSession session = request.getSession(true);
                        _id = session.getId();
                        session.setAttribute("value", new Integer(1));
                        String check = request.getParameter("check");
                        if ((!(StringUtil.isBlank(check))) && ((_store) != null)) {
                            boolean exists;
                            try {
                                exists = _store.exists(_id);
                            } catch (Exception e) {
                                throw new ServletException(e);
                            }
                            if ("false".equalsIgnoreCase(check))
                                Assertions.assertFalse(exists);
                            else
                                Assertions.assertTrue(exists);

                        }
                        if ("createinv".equals(action)) {
                            session.invalidate();
                            Assertions.assertNull(request.getSession(false));
                            Assertions.assertNotNull(session);
                        } else
                            if ("createinvcreate".equals(action)) {
                                session.invalidate();
                                Assertions.assertNull(request.getSession(false));
                                Assertions.assertNotNull(session);
                                session = request.getSession(true);
                                _id = session.getId();
                            }

                    }


        }
    }

    public static class TestServletB extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            HttpSession session = request.getSession(false);
            Assertions.assertNull(session);
            if (session == null)
                session = request.getSession(true);

            // Be sure nothing from contextA is present
            Object objectA = session.getAttribute("value");
            Assertions.assertTrue((objectA == null));
            // Add something, so in contextA we can check if it is visible (it must not).
            session.setAttribute("B", "B");
        }
    }
}

