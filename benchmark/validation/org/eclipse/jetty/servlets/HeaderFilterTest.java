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
package org.eclipse.jetty.servlets;


import DispatcherType.REQUEST;
import HttpHeader.EXPIRES;
import HttpStatus.NO_CONTENT_204;
import HttpTester.Request;
import HttpTester.Response;
import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletTester;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class HeaderFilterTest {
    private ServletTester _tester;

    @Test
    public void testHeaderFilterSet() throws Exception {
        FilterHolder holder = new FilterHolder(HeaderFilter.class);
        holder.setInitParameter("headerConfig", "set X-Frame-Options: DENY");
        _tester.getContext().getServletHandler().addFilterWithMapping(holder, "/*", EnumSet.of(REQUEST));
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("GET");
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", "localhost");
        request.setURI("/context/test/0");
        HttpTester.Response response = HttpTester.parseResponse(_tester.getResponses(request.generate()));
        MatcherAssert.assertThat(response, containsHeaderValue("X-Frame-Options", "DENY"));
    }

    @Test
    public void testHeaderFilterAdd() throws Exception {
        FilterHolder holder = new FilterHolder(HeaderFilter.class);
        holder.setInitParameter("headerConfig", "add X-Frame-Options: DENY");
        _tester.getContext().getServletHandler().addFilterWithMapping(holder, "/*", EnumSet.of(REQUEST));
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("GET");
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", "localhost");
        request.setURI("/context/test/0");
        HttpTester.Response response = HttpTester.parseResponse(_tester.getResponses(request.generate()));
        MatcherAssert.assertThat(response, containsHeaderValue("X-Frame-Options", "DENY"));
    }

    @Test
    public void testHeaderFilterSetDate() throws Exception {
        FilterHolder holder = new FilterHolder(HeaderFilter.class);
        holder.setInitParameter("headerConfig", "setDate Expires: 100");
        _tester.getContext().getServletHandler().addFilterWithMapping(holder, "/*", EnumSet.of(REQUEST));
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("GET");
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", "localhost");
        request.setURI("/context/test/0");
        HttpTester.Response response = HttpTester.parseResponse(_tester.getResponses(request.generate()));
        MatcherAssert.assertThat(response.toString(), EXPIRES.asString(), isIn(response.getFieldNamesCollection()));
    }

    @Test
    public void testHeaderFilterAddDate() throws Exception {
        FilterHolder holder = new FilterHolder(HeaderFilter.class);
        holder.setInitParameter("headerConfig", "addDate Expires: 100");
        _tester.getContext().getServletHandler().addFilterWithMapping(holder, "/*", EnumSet.of(REQUEST));
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("GET");
        request.setVersion("HTTP/1.1");
        request.setHeader("Host", "localhost");
        request.setURI("/context/test/0");
        HttpTester.Response response = HttpTester.parseResponse(_tester.getResponses(request.generate()));
        MatcherAssert.assertThat(response.toString(), EXPIRES.asString(), isIn(response.getFieldNamesCollection()));
    }

    public static class NullServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(NO_CONTENT_204);
        }
    }
}

