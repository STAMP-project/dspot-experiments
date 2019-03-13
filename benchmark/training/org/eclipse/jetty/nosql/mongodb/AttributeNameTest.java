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
package org.eclipse.jetty.nosql.mongodb;


import HttpHeader.SET_COOKIE;
import HttpServletResponse.SC_OK;
import SessionCache.NEVER_EVICT;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.server.session.DefaultSessionCacheFactory;
import org.eclipse.jetty.server.session.Session;
import org.eclipse.jetty.server.session.TestServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * AttributeNameTest
 *
 * Test that attribute names that have special characters with meaning to mongo (eg ".") are
 * properly escaped and not accidentally removed.
 * See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=444595
 */
public class AttributeNameTest {
    @Test
    public void testAttributeNamesWithDots() throws Exception {
        String contextPath = "";
        String servletMapping = "/server";
        int maxInactivePeriod = 10000;
        int scavengePeriod = 20000;
        DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
        cacheFactory.setEvictionPolicy(NEVER_EVICT);
        MongoSessionDataStoreFactory storeFactory = MongoTestHelper.newSessionDataStoreFactory();
        storeFactory.setGracePeriodSec(scavengePeriod);
        TestServer server1 = new TestServer(0, maxInactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        server1.addContext(contextPath).addServlet(AttributeNameTest.TestServlet.class, servletMapping);
        server1.start();
        int port1 = server1.getPort();
        TestServer server2 = new TestServer(0, maxInactivePeriod, scavengePeriod, cacheFactory, storeFactory);
        server2.addContext(contextPath).addServlet(AttributeNameTest.TestServlet.class, servletMapping);
        server2.start();
        int port2 = server2.getPort();
        try {
            HttpClient client = new HttpClient();
            client.start();
            try {
                // Perform one request to server1 to create a session with attribute with dotted name
                ContentResponse response = client.GET((((("http://localhost:" + port1) + contextPath) + servletMapping) + "?action=init"));
                Assertions.assertEquals(SC_OK, response.getStatus());
                String resp = response.getContentAsString();
                String[] sessionTestResponse = resp.split("/");
                Assertions.assertEquals("a.b.c", sessionTestResponse[0]);
                String sessionCookie = response.getHeaders().get(SET_COOKIE);
                Assertions.assertTrue((sessionCookie != null));
                // Mangle the cookie, replacing Path with $Path, etc.
                sessionCookie = sessionCookie.replaceFirst("(\\W)(P|p)ath=", "$1\\$Path=");
                // Make a request to the 2nd server which will do a refresh, use TestServlet to ensure that the
                // session attribute with dotted name is not removed
                Request request2 = client.newRequest((((("http://localhost:" + port2) + contextPath) + servletMapping) + "?action=get"));
                request2.header("Cookie", sessionCookie);
                ContentResponse response2 = request2.send();
                Assertions.assertEquals(SC_OK, response2.getStatus());
            } finally {
                client.stop();
            }
        } finally {
            server1.stop();
            server2.stop();
        }
    }

    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            String action = request.getParameter("action");
            if ("init".equals(action)) {
                Session session = ((Session) (request.getSession(true)));
                session.setAttribute("a.b.c", TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
                sendResult(session, httpServletResponse.getWriter());
            } else {
                Session session = ((Session) (request.getSession(false)));
                Assertions.assertNotNull(session);
                Assertions.assertNotNull(session.getAttribute("a.b.c"));
                sendResult(session, httpServletResponse.getWriter());
            }
        }

        private void sendResult(Session session, PrintWriter writer) {
            if (session != null) {
                if ((session.getAttribute("a.b.c")) != null)
                    writer.print(("a.b.c/" + (session.getAttribute("a.b.c"))));
                else
                    writer.print("-/0");

            } else {
                writer.print("0/0");
            }
        }
    }
}

