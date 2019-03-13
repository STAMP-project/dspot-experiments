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
 * ReentrantRequestSessionTest
 *
 * While a request is still active in a context, make another
 * request to it to ensure both share same session.
 */
public class ReentrantRequestSessionTest {
    @Test
    public void testReentrantRequestSession() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        SessionDataStoreFactory storeFactory = new NullSessionDataStoreFactory();
        TestServer server = new TestServer(0, (-1), 60, cacheFactory, storeFactory);
        ServletContextHandler context = server.addContext(contextPath);
        context.addServlet(ReentrantRequestSessionTest.TestServlet.class, servletMapping);
        TestContextScopeListener scopeListener = new TestContextScopeListener();
        context.addEventListener(scopeListener);
        try {
            server.start();
            int port = server.getPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                // create the session
                CountDownLatch latch = new CountDownLatch(1);
                scopeListener.setExitSynchronizer(latch);
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=create"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // ensure request fully finished processing
                latch.await(5, TimeUnit.SECONDS);
                // make a request that will make a simultaneous request for the same session
                Request request = client.newRequest((((((((("http://localhost:" + port) + contextPath) + servletMapping) + "?action=reenter&port=") + port) + "&path=") + contextPath) + servletMapping));
                request.header("Cookie", sessionCookie);
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
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            doPost(request, response);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                request.getSession(true);
                return;
            }
            HttpSession session = request.getSession(false);
            if ("reenter".equals(action)) {
                if (session == null)
                    session = request.getSession(true);

                int port = Integer.parseInt(request.getParameter("port"));
                String path = request.getParameter("path");
                // We want to make another request
                // while this request is still pending, to see if the locking is
                // fine grained (per session at least).
                try {
                    HttpClient client = new HttpClient();
                    client.start();
                    try {
                        ContentResponse resp = client.GET(((((("http://localhost:" + port) + path) + ";jsessionid=") + (session.getId())) + "?action=none"));
                        Assertions.assertEquals(SC_OK, resp.getStatus());
                        Assertions.assertEquals("true", session.getAttribute("reentrant"));
                    } finally {
                        client.stop();
                    }
                } catch (Exception x) {
                    throw new ServletException(x);
                }
            } else {
                Assertions.assertTrue((session != null));
                session.setAttribute("reentrant", "true");
            }
        }
    }
}

