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
import java.io.IOException;
import java.util.Set;
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
 * SessionEvictionFailureTest
 */
public class SessionEvictionFailureTest {
    /**
     * MockSessionDataStore
     */
    public static class MockSessionDataStore extends AbstractSessionDataStore {
        public boolean[] _nextStoreResult;

        public int i = 0;

        public MockSessionDataStore(boolean[] results) {
            _nextStoreResult = results;
        }

        @Override
        public boolean isPassivating() {
            return false;
        }

        @Override
        public boolean exists(String id) throws Exception {
            return true;
        }

        @Override
        public SessionData doLoad(String id) throws Exception {
            return null;
        }

        @Override
        public boolean delete(String id) throws Exception {
            return false;
        }

        @Override
        public void doStore(String id, SessionData data, long lastSaveTime) throws Exception {
            if (((_nextStoreResult) != null) && (!(_nextStoreResult[((i)++)]))) {
                throw new IllegalStateException("Testing store");
            }
        }

        @Override
        public Set<String> doGetExpired(Set<String> candidates) {
            return candidates;
        }
    }

    /**
     * MockSessionDataStoreFactory
     */
    public static class MockSessionDataStoreFactory extends AbstractSessionDataStoreFactory {
        public boolean[] _nextStoreResults;

        public void setNextStoreResults(boolean[] storeResults) {
            _nextStoreResults = storeResults;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.server.session.SessionDataStoreFactory#getSessionDataStore(org.eclipse.jetty.server.session.SessionHandler)
         */
        @Override
        public SessionDataStore getSessionDataStore(SessionHandler handler) throws Exception {
            return new SessionEvictionFailureTest.MockSessionDataStore(_nextStoreResults);
        }
    }

    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("init".equals(action)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("aaa", new Integer(0));
            } else
                if ("test".equals(action)) {
                    HttpSession session = request.getSession(false);
                    Assertions.assertNotNull(session);
                    Integer count = ((Integer) (session.getAttribute("aaa")));
                    Assertions.assertNotNull(count);
                    session.setAttribute("aaa", new Integer(((count.intValue()) + 1)));
                }

        }
    }

    @Test
    public void testEvictFailure() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int inactivePeriod = 0;
        int scavengePeriod = 20;
        int evictionPeriod = 5;// evict after  inactivity

        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(evictionPeriod);
        SessionDataStoreFactory storeFactory = new SessionEvictionFailureTest.MockSessionDataStoreFactory();
        TestServer server = new TestServer(0, inactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext(contextPath);
        context.getSessionHandler().getSessionCache().setSaveOnInactiveEviction(true);
        // test values: allow first save, fail evict save, allow save, fail evict save, allow save, allow save on shutdown
        SessionEvictionFailureTest.MockSessionDataStore ds = new SessionEvictionFailureTest.MockSessionDataStore(new boolean[]{ true, false, true, false, true, true });
        context.getSessionHandler().getSessionCache().setSessionDataStore(ds);
        SessionEvictionFailureTest.TestServlet servlet = new SessionEvictionFailureTest.TestServlet();
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
                ContentResponse response = client.GET((url + "?action=init"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertNotNull(sessionCookie);
                // Wait for the eviction period to expire - save on evict should fail but session
                // should remain in the cache
                SessionEvictionFailureTest.pause((evictionPeriod + ((int) (evictionPeriod * 0.5))));
                // Make another request to see if the session is still in the cache and can be used,
                // allow it to be saved this time
                Request request = client.newRequest((url + "?action=test"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                Assertions.assertNull(response.getHeaders().get("Set-Cookie"));// check that the cookie wasn't reset

                // Wait for the eviction period to expire again
                SessionEvictionFailureTest.pause((evictionPeriod + ((int) (evictionPeriod * 0.5))));
                request = client.newRequest((url + "?action=test"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }
}

