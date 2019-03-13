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
package org.eclipse.jetty.memcached.session;


import HttpServletResponse.SC_OK;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.AbstractSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.Session;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * TestMemcachedSessions
 */
public class TestMemcachedSessions {
    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            String arg = req.getParameter("action");
            if (arg == null)
                return;

            HttpSession s = null;
            if ("set".equals(arg)) {
                s = req.getSession(true);
                Assertions.assertNotNull(s);
                s.setAttribute("val", req.getParameter("value"));
            } else
                if ("get".equals(arg)) {
                    s = req.getSession(false);
                    System.err.println(("GET: s=" + s));
                } else
                    if ("del".equals(arg)) {
                        s = req.getSession();
                        Assertions.assertNotNull(s);
                        s.invalidate();
                        s = null;
                    }


            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            if (s == null)
                w.write("No session");
            else
                w.write(((String) (s.getAttribute("val"))));

        }
    }

    public static class NullSessionCache extends AbstractSessionCache {
        public NullSessionCache(SessionHandler handler) {
            super(handler);
        }

        @Override
        public void shutdown() {
        }

        @Override
        public Session newSession(SessionData data) {
            return new Session(_handler, data);
        }

        @Override
        public Session newSession(HttpServletRequest request, SessionData data) {
            return new Session(_handler, request, data);
        }

        @Override
        public Session doGet(String id) {
            return null;
        }

        @Override
        public Session doPutIfAbsent(String id, Session session) {
            return null;
        }

        @Override
        public boolean doReplace(String id, Session oldValue, Session newValue) {
            return true;
        }

        @Override
        public Session doDelete(String id) {
            return null;
        }
    }

    @Test
    public void testMemcached() throws Exception {
        String contextPath = "/";
        Server server = new Server(0);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(System.getProperty("java.io.tmpdir"));
        server.setHandler(context);
        TestMemcachedSessions.NullSessionCache dsc = new TestMemcachedSessions.NullSessionCache(context.getSessionHandler());
        setSessionDataStore(new org.eclipse.jetty.server.session.CachingSessionDataStore(new MemcachedSessionDataMap("localhost", "11211"), new NullSessionDataStore()));
        context.getSessionHandler().setSessionCache(dsc);
        // Add a test servlet
        ServletHolder h = new ServletHolder();
        h.setServlet(new TestMemcachedSessions.TestServlet());
        context.addServlet(h, "/");
        try {
            server.start();
            int port = getLocalPort();
            HttpClient client = new HttpClient();
            client.start();
            try {
                int value = 42;
                ContentResponse response = client.GET((((("http://localhost:" + port) + contextPath) + "?action=set&value=") + value));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String sessionCookie = response.getHeaders().get("Set-Cookie");
                Assertions.assertTrue((sessionCookie != null));
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookie = sessionCookie.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                String resp = response.getContentAsString();
                Assertions.assertEquals(resp.trim(), String.valueOf(value));
                // Be sure the session value is still there
                Request request = client.newRequest(((("http://localhost:" + port) + contextPath) + "?action=get"));
                request.header("Cookie", sessionCookie);
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                resp = response.getContentAsString();
                Assertions.assertEquals(String.valueOf(value), resp.trim());
                // Delete the session
                request = client.newRequest(((("http://localhost:" + port) + contextPath) + "?action=del"));
                request.header("Cookie", sessionCookie);
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                // Check that the session is gone
                request = client.newRequest(((("http://localhost:" + port) + contextPath) + "?action=get"));
                request.header("Cookie", sessionCookie);
                response = request.send();
                Assertions.assertEquals(SC_OK, response.getStatus());
                resp = response.getContentAsString();
                Assertions.assertEquals("No session", resp.trim());
            } finally {
                client.stop();
            }
        } finally {
            server.stop();
        }
    }
}

