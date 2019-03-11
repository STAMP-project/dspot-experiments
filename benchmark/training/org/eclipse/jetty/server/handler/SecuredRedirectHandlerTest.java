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
package org.eclipse.jetty.server.handler;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class SecuredRedirectHandlerTest {
    private static Server server;

    private static HostnameVerifier origVerifier;

    private static SSLSocketFactory origSsf;

    private static URI serverHttpUri;

    private static URI serverHttpsUri;

    @Test
    public void testRedirectUnsecuredRoot() throws Exception {
        URL url = SecuredRedirectHandlerTest.serverHttpUri.resolve("/").toURL();
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setInstanceFollowRedirects(false);
        connection.setAllowUserInteraction(false);
        MatcherAssert.assertThat("response code", connection.getResponseCode(), Matchers.is(302));
        MatcherAssert.assertThat("location header", connection.getHeaderField("Location"), Matchers.is(SecuredRedirectHandlerTest.serverHttpsUri.resolve("/").toASCIIString()));
        connection.disconnect();
    }

    @Test
    public void testRedirectSecuredRoot() throws Exception {
        URL url = SecuredRedirectHandlerTest.serverHttpsUri.resolve("/").toURL();
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setInstanceFollowRedirects(false);
        connection.setAllowUserInteraction(false);
        MatcherAssert.assertThat("response code", connection.getResponseCode(), Matchers.is(200));
        String content = getContent(connection);
        MatcherAssert.assertThat("response content", content, Matchers.containsString("<a href=\"/test1\">"));
        connection.disconnect();
    }

    @Test
    public void testAccessUnsecuredHandler() throws Exception {
        URL url = SecuredRedirectHandlerTest.serverHttpUri.resolve("/test1").toURL();
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setInstanceFollowRedirects(false);
        connection.setAllowUserInteraction(false);
        MatcherAssert.assertThat("response code", connection.getResponseCode(), Matchers.is(302));
        MatcherAssert.assertThat("location header", connection.getHeaderField("Location"), Matchers.is(SecuredRedirectHandlerTest.serverHttpsUri.resolve("/test1").toASCIIString()));
        connection.disconnect();
    }

    @Test
    public void testAccessUnsecured404() throws Exception {
        URL url = SecuredRedirectHandlerTest.serverHttpUri.resolve("/nothing/here").toURL();
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setInstanceFollowRedirects(false);
        connection.setAllowUserInteraction(false);
        MatcherAssert.assertThat("response code", connection.getResponseCode(), Matchers.is(302));
        MatcherAssert.assertThat("location header", connection.getHeaderField("Location"), Matchers.is(SecuredRedirectHandlerTest.serverHttpsUri.resolve("/nothing/here").toASCIIString()));
        connection.disconnect();
    }

    @Test
    public void testAccessSecured404() throws Exception {
        URL url = SecuredRedirectHandlerTest.serverHttpsUri.resolve("/nothing/here").toURL();
        HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setInstanceFollowRedirects(false);
        connection.setAllowUserInteraction(false);
        MatcherAssert.assertThat("response code", connection.getResponseCode(), Matchers.is(404));
        connection.disconnect();
    }

    public static class HelloHandler extends AbstractHandler {
        private final String msg;

        public HelloHandler(String msg) {
            this.msg = msg;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/plain");
            response.getWriter().printf("%s%n", msg);
            baseRequest.setHandled(true);
        }
    }

    public static class RootHandler extends AbstractHandler {
        private final String[] childContexts;

        public RootHandler(String... children) {
            this.childContexts = children;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (!("/".equals(target))) {
                response.sendError(404);
                return;
            }
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head><title>Contexts</title></head>");
            out.println("<body>");
            out.println("<h4>Child Contexts</h4>");
            out.println("<ul>");
            for (String child : childContexts) {
                out.printf("<li><a href=\"%s\">%s</a></li>%n", child, child);
            }
            out.println("</ul>");
            out.println("</body></html>");
            baseRequest.setHandled(true);
        }
    }
}

