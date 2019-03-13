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
package org.eclipse.jetty.memcached.sessions;


import HttpServletResponse.SC_OK;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.server.session.CachingSessionDataStore;
import org.eclipse.jetty.server.session.DefaultSessionCacheFactory;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataMap;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.eclipse.jetty.server.session.TestServer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * CachingSessionDataStoreTest
 */
public class CachingSessionDataStoreTest {
    @Test
    public void testSessionCRUD() throws Exception {
        String servletMapping = "/server";
        int scavengePeriod = -1;
        int maxInactivePeriod = -1;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        SessionDataStoreFactory storeFactory = MemcachedTestHelper.newSessionDataStoreFactory();
        // Make sure sessions are evicted on request exit so they will need to be reloaded via cache/persistent store
        TestServer server = new TestServer(0, maxInactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext("/");
        context.addServlet(CachingSessionDataStoreTest.TestServlet.class, servletMapping);
        String contextPath = "";
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                // 
                // Create a session
                // 
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                String id = TestServer.extractSessionId(sessionCookie);
                // check that the memcache contains the session, and the session data store contains the session
                CachingSessionDataStore ds = ((CachingSessionDataStore) (context.getSessionHandler().getSessionCache().getSessionDataStore()));
                Assertions.assertNotNull(ds);
                SessionDataStore persistentStore = ds.getSessionStore();
                SessionDataMap dataMap = ds.getSessionDataMap();
                // the backing persistent store contains the session
                Assertions.assertNotNull(persistentStore.load(id));
                // the memcache cache contains the session
                Assertions.assertNotNull(dataMap.load(id));
                // 
                // Update a session and check that is is NOT loaded via the persistent store
                // 
                ((MemcachedTestHelper.MockDataStore) (persistentStore)).zeroLoadCount();
                Request request = client.newRequest((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=update"));
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                Assertions.assertEquals(0, ((MemcachedTestHelper.MockDataStore) (persistentStore)).getLoadCount());
                // check it was updated in the persistent store
                SessionData sd = persistentStore.load(id);
                Assertions.assertNotNull(sd);
                Assertions.assertEquals("bar", sd.getAttribute("foo"));
                // check it was updated in the cache
                sd = dataMap.load(id);
                Assertions.assertNotNull(sd);
                Assertions.assertEquals("bar", sd.getAttribute("foo"));
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
                return;
            }
            if ("update".equals(action)) {
                HttpSession session = request.getSession(false);
                Assertions.assertNotNull(session);
                session.setAttribute("foo", "bar");
                return;
            }
        }
    }
}

