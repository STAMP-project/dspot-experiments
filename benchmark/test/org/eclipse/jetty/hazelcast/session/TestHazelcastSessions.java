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
package org.eclipse.jetty.hazelcast.session;


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
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestHazelcastSessions {
    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            String arg = req.getParameter("action");
            if (arg == null) {
                return;
            }
            HttpSession s = null;
            if ("set".equals(arg)) {
                s = req.getSession(true);
                Assertions.assertNotNull(s);
                s.setAttribute("val", req.getParameter("value"));
            } else
                if ("get".equals(arg)) {
                    s = req.getSession(false);
                    System.err.println(((("GET: s=" + s) + ",id=") + (s != null ? s.getId() : "")));
                } else
                    if ("del".equals(arg)) {
                        s = req.getSession();
                        Assertions.assertNotNull(s);
                        s.invalidate();
                        s = null;
                    }


            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            if (s == null) {
                w.write("No session");
            } else {
                w.write(((String) (s.getAttribute("val"))));
            }
        }
    }

    private HazelcastSessionDataStore hazelcastSessionDataStore;

    private HazelcastSessionDataStoreFactory hazelcastSessionDataStoreFactory;

    private Server server;

    private ServerConnector serverConnector;

    String contextPath = "/";

    @Test
    public void testHazelcast() throws Exception {
        int port = serverConnector.getLocalPort();
        HttpClient client = new HttpClient();
        client.start();
        try {
            int value = 42;
            ContentResponse response = client.GET((((("http://localhost:" + port) + (contextPath)) + "?action=set&value=") + value));
            Assertions.assertEquals(SC_OK, response.getStatus());
            String sessionCookie = response.getHeaders().get("Set-Cookie");
            Assertions.assertTrue((sessionCookie != null));
            // Mangle the cookie, replacing Path with $Path, etc.
            sessionCookie = sessionCookie.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
            String resp = response.getContentAsString();
            Assertions.assertEquals(resp.trim(), String.valueOf(value));
            // Be sure the session value is still there
            Request request = client.newRequest(((("http://localhost:" + port) + (contextPath)) + "?action=get"));
            request.header("Cookie", sessionCookie);
            response = request.send();
            Assertions.assertEquals(SC_OK, response.getStatus());
            resp = response.getContentAsString();
            Assertions.assertEquals(String.valueOf(value), resp.trim());
            // Delete the session
            request = client.newRequest(((("http://localhost:" + port) + (contextPath)) + "?action=del"));
            request.header("Cookie", sessionCookie);
            response = request.send();
            Assertions.assertEquals(SC_OK, response.getStatus());
            // Check that the session is gone
            request = client.newRequest(((("http://localhost:" + port) + (contextPath)) + "?action=get"));
            request.header("Cookie", sessionCookie);
            response = request.send();
            Assertions.assertEquals(SC_OK, response.getStatus());
            resp = response.getContentAsString();
            Assertions.assertEquals("No session", resp.trim());
        } finally {
            client.stop();
        }
    }
}

