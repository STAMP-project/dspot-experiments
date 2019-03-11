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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ComplianceViolations2616Test {
    private static Server server;

    private static LocalConnector connector;

    public static class ReportViolationsFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (request instanceof HttpServletRequest) {
                List<String> violations = ((List<String>) (request.getAttribute("org.eclipse.jetty.http.compliance.violations")));
                if (violations != null) {
                    HttpServletResponse httpResponse = ((HttpServletResponse) (response));
                    int i = 0;
                    for (String violation : violations) {
                        httpResponse.setHeader(("X-Http-Violation-" + (i++)), violation);
                    }
                }
            }
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
        }
    }

    public static class DumpRequestHeadersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            List<String> headerNames = new ArrayList<>();
            headerNames.addAll(Collections.list(req.getHeaderNames()));
            Collections.sort(headerNames);
            for (String name : headerNames) {
                out.printf("[%s] = [%s]%n", name, req.getHeader(name));
            }
        }
    }

    @Test
    public void testNoColonHeader_Middle() throws Exception {
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /dump/ HTTP/1.1\r\n");
        req1.append("Name\r\n");
        req1.append("Host: local\r\n");
        req1.append("Accept: */*\r\n");
        req1.append("Connection: close\r\n");
        req1.append("\r\n");
        String response = ComplianceViolations2616Test.connector.getResponse(req1.toString());
        MatcherAssert.assertThat("Response status", response, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat("Response headers", response, Matchers.containsString("X-Http-Violation-0: Fields must have a Colon"));
        MatcherAssert.assertThat("Response body", response, Matchers.containsString("[Name] = []"));
    }

    @Test
    public void testNoColonHeader_End() throws Exception {
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /dump/ HTTP/1.1\r\n");
        req1.append("Host: local\r\n");
        req1.append("Connection: close\r\n");
        req1.append("Accept: */*\r\n");
        req1.append("Name\r\n");
        req1.append("\r\n");
        String response = ComplianceViolations2616Test.connector.getResponse(req1.toString());
        MatcherAssert.assertThat("Response status", response, Matchers.containsString("HTTP/1.1 200"));
        MatcherAssert.assertThat("Response headers", response, Matchers.containsString("X-Http-Violation-0: Fields must have a Colon"));
        MatcherAssert.assertThat("Response body", response, Matchers.containsString("[Name] = []"));
    }

    @Test
    public void testFoldedHeader() throws Exception {
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /dump/ HTTP/1.1\r\n");
        req1.append("Host: local\r\n");
        req1.append("Name: Some\r\n");
        req1.append(" Value\r\n");
        req1.append("Connection: close\r\n");
        req1.append("Accept: */*\r\n");
        req1.append("\r\n");
        String response = ComplianceViolations2616Test.connector.getResponse(req1.toString());
        MatcherAssert.assertThat("Response status", response, Matchers.containsString("HTTP/1.1 200"));
        MatcherAssert.assertThat("Response headers", response, Matchers.containsString("X-Http-Violation-0: No line Folding"));
        MatcherAssert.assertThat("Response body", response, Matchers.containsString("[Name] = [Some Value]"));
    }
}

