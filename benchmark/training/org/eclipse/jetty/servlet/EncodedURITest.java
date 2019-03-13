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
package org.eclipse.jetty.servlet;


import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.URIUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class EncodedURITest {
    private Server _server;

    private LocalConnector _connector;

    private ContextHandlerCollection _contextCollection;

    private ServletContextHandler _context0;

    private ServletContextHandler _context1;

    @Test
    public void testTestServlet() throws Exception {
        String response = _connector.getResponse("GET /c%6Fntext%20path/test%20servlet/path%20info HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 "));
        MatcherAssert.assertThat(response, Matchers.containsString("requestURI=/c%6Fntext%20path/test%20servlet/path%20info"));
        MatcherAssert.assertThat(response, Matchers.containsString("contextPath=/context%20path"));
        MatcherAssert.assertThat(response, Matchers.containsString("servletPath=/test servlet"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path info"));
    }

    @Test
    public void testAsyncFilterTestServlet() throws Exception {
        String response = _connector.getResponse("GET /context%20path/test%20servlet/path%20info?async=true HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 "));
        MatcherAssert.assertThat(response, Matchers.containsString("requestURI=/context%20path/test%20servlet/path%20info"));
        MatcherAssert.assertThat(response, Matchers.containsString("contextPath=/context%20path"));
        MatcherAssert.assertThat(response, Matchers.containsString("servletPath=/test servlet"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path info"));
    }

    @Test
    public void testAsyncFilterWrapTestServlet() throws Exception {
        String response = _connector.getResponse("GET /context%20path/test%20servlet/path%20info?async=true&wrap=true HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 "));
        MatcherAssert.assertThat(response, Matchers.containsString("requestURI=/context%20path/test%20servlet/path%20info"));
        MatcherAssert.assertThat(response, Matchers.containsString("contextPath=/context%20path"));
        MatcherAssert.assertThat(response, Matchers.containsString("servletPath=/test servlet"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path info"));
    }

    @Test
    public void testAsyncServletTestServlet() throws Exception {
        String response = _connector.getResponse("GET /context%20path/async%20servlet/path%20info HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 "));
        MatcherAssert.assertThat(response, Matchers.containsString("requestURI=/context%20path/test servlet/path info"));
        MatcherAssert.assertThat(response, Matchers.containsString("contextPath=/context%20path"));
        MatcherAssert.assertThat(response, Matchers.containsString("servletPath=/test servlet"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path info"));
    }

    @Test
    public void testAsyncServletTestServletEncoded() throws Exception {
        String response = _connector.getResponse("GET /context%20path/async%20servlet/path%20info?encode=true HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 "));
        MatcherAssert.assertThat(response, Matchers.containsString("requestURI=/context%20path/test%20servlet/path%20info"));
        MatcherAssert.assertThat(response, Matchers.containsString("contextPath=/context%20path"));
        MatcherAssert.assertThat(response, Matchers.containsString("servletPath=/test servlet"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path info"));
    }

    public static class TestServlet extends HttpServlet {
        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/plain");
            response.getWriter().println(("requestURI=" + (request.getRequestURI())));
            response.getWriter().println(("contextPath=" + (request.getContextPath())));
            response.getWriter().println(("servletPath=" + (request.getServletPath())));
            response.getWriter().println(("pathInfo=" + (request.getPathInfo())));
        }
    }

    public static class AsyncServlet extends HttpServlet {
        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            AsyncContext async = (Boolean.parseBoolean(request.getParameter("wrap"))) ? request.startAsync(request, response) : request.startAsync();
            if (Boolean.parseBoolean(request.getParameter("encode")))
                async.dispatch(("/test%20servlet" + (URIUtil.encodePath(request.getPathInfo()))));
            else
                async.dispatch(("/test servlet/path info" + (request.getPathInfo())));

            return;
        }
    }

    public static class AsyncFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if ((Boolean.parseBoolean(request.getParameter("async"))) && (!(Boolean.parseBoolean(((String) (request.getAttribute("async"))))))) {
                request.setAttribute("async", "true");
                AsyncContext async = (Boolean.parseBoolean(request.getParameter("wrap"))) ? request.startAsync(request, response) : request.startAsync();
                async.dispatch();
                return;
            }
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
        }
    }
}

