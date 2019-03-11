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
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * RemoveSessionTest
 *
 * Test that invalidating a session does not return the session on the next request.
 */
public class RemoveSessionTest {
    @Test
    public void testRemoveSession() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new TestSessionDataStoreFactory();
        TestServer server = new TestServer(0, (-1), (-1), cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext(contextPath);
        context.addServlet(RemoveSessionTest.TestServlet.class, servletMapping);
        RemoveSessionTest.TestEventListener testListener = new RemoveSessionTest.TestEventListener();
        context.getSessionHandler().addEventListener(testListener);
        SessionHandler m = context.getSessionHandler();
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // ensure sessionCreated bindingListener is called
                Assertions.assertTrue(testListener.isCreated());
                Assertions.assertEquals(1, m.getSessionsCreated());
                Assertions.assertEquals(1, getSessionsMax());
                Assertions.assertEquals(1, getSessionsTotal());
                // now delete the session
                Request request = client.newRequest((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=delete"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // ensure sessionDestroyed bindingListener is called
                Assertions.assertTrue(testListener.isDestroyed());
                Assertions.assertEquals(0, getSessionsCurrent());
                Assertions.assertEquals(1, getSessionsMax());
                Assertions.assertEquals(1, getSessionsTotal());
                // check the session is not persisted any more
                Assertions.assertFalse(m.getSessionCache().getSessionDataStore().exists(TestServer.extractSessionId(sessionCookie)));
                // The session is not there anymore, even if we present an old cookie
                request = client.newRequest((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=check"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                Assertions.assertEquals(0, getSessionsCurrent());
                Assertions.assertEquals(1, getSessionsMax());
                Assertions.assertEquals(1, getSessionsTotal());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }

    public static class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                request.getSession(true);
            } else
                if ("delete".equals(action)) {
                    HttpSession s = request.getSession(false);
                    Assertions.assertNotNull(s);
                    s.invalidate();
                    s = request.getSession(false);
                    Assertions.assertNull(s);
                } else {
                    HttpSession s = request.getSession(false);
                    Assertions.assertNull(s);
                }

        }
    }

    public static class TestEventListener implements HttpSessionListener {
        boolean wasCreated;

        boolean wasDestroyed;

        @Override
        public void sessionCreated(HttpSessionEvent se) {
            wasCreated = true;
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            wasDestroyed = true;
        }

        public boolean isDestroyed() {
            return wasDestroyed;
        }

        public boolean isCreated() {
            return wasCreated;
        }
    }
}

