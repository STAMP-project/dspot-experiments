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


import Dispatcher.ERROR_EXCEPTION;
import Dispatcher.ERROR_EXCEPTION_TYPE;
import Dispatcher.ERROR_MESSAGE;
import Dispatcher.ERROR_REQUEST_URI;
import Dispatcher.ERROR_SERVLET_NAME;
import Dispatcher.ERROR_STATUS_CODE;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ErrorPageTest {
    private Server _server;

    private LocalConnector _connector;

    private StacklessLogging _stackless;

    @Test
    public void testSendErrorClosedResponse() throws Exception {
        String response = _connector.getResponse("GET /fail-closed/ HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 599 599"));
        MatcherAssert.assertThat(response, Matchers.containsString("DISPATCH: ERROR"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /599"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 599"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$FailClosedServlet-"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /fail-closed/"));
        MatcherAssert.assertThat(response, CoreMatchers.not(CoreMatchers.containsString("This shouldn't be seen")));
    }

    @Test
    public void testErrorCode() throws Exception {
        String response = _connector.getResponse("GET /fail/code?code=599 HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 599 599"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /599"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 599"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$FailServlet-"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /fail/code"));
    }

    @Test
    public void testErrorException() throws Exception {
        try (StacklessLogging stackless = new StacklessLogging(HttpChannel.class)) {
            String response = _connector.getResponse("GET /fail/exception HTTP/1.0\r\n\r\n");
            MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 500 Server Error"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /TestException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 500"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: javax.servlet.ServletException: java.lang.IllegalStateException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: class javax.servlet.ServletException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$FailServlet-"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /fail/exception"));
        }
    }

    @Test
    public void testGlobalErrorCode() throws Exception {
        String response = _connector.getResponse("GET /fail/global?code=598 HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 598 598"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /GlobalErrorPage"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 598"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: null"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$FailServlet-"));
        MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /fail/global"));
    }

    @Test
    public void testGlobalErrorException() throws Exception {
        try (StacklessLogging stackless = new StacklessLogging(HttpChannel.class)) {
            String response = _connector.getResponse("GET /fail/global?code=NAN HTTP/1.0\r\n\r\n");
            MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 500 Server Error"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /GlobalErrorPage"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 500"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: java.lang.NumberFormatException: For input string: \"NAN\""));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: class java.lang.NumberFormatException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$FailServlet-"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /fail/global"));
        }
    }

    @Test
    public void testBadMessage() throws Exception {
        try (StacklessLogging ignore = new StacklessLogging(Dispatcher.class)) {
            String response = _connector.getResponse("GET /app?baa=%88%A4 HTTP/1.0\r\n\r\n");
            MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 400 Bad query encoding"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_PAGE: /BadMessageException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_MESSAGE: Bad query encoding"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_CODE: 400"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION: org.eclipse.jetty.http.BadMessageException: 400: Bad query encoding"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_EXCEPTION_TYPE: class org.eclipse.jetty.http.BadMessageException"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_SERVLET: org.eclipse.jetty.servlet.ErrorPageTest$AppServlet-"));
            MatcherAssert.assertThat(response, Matchers.containsString("ERROR_REQUEST_URI: /app"));
            MatcherAssert.assertThat(response, Matchers.containsString("getParameterMap()= {}"));
        }
    }

    public static class AppServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            request.getRequestDispatcher("/longer.app/").forward(request, response);
        }
    }

    public static class LongerAppServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            PrintWriter writer = response.getWriter();
            writer.println(request.getRequestURI());
        }
    }

    public static class FailServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String code = request.getParameter("code");
            if (code != null)
                response.sendError(Integer.parseInt(code));
            else
                throw new ServletException(new IllegalStateException());

        }
    }

    public static class FailClosedServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.sendError(599);
            // The below should result in no operation, as response should be closed.
            try {
                response.setStatus(200);// this status code should not be seen

                response.getWriter().append("This shouldn't be seen");
            } catch (Throwable ignore) {
            }
        }
    }

    public static class ErrorServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            PrintWriter writer = response.getWriter();
            writer.println(("DISPATCH: " + (request.getDispatcherType().name())));
            writer.println(("ERROR_PAGE: " + (request.getPathInfo())));
            writer.println(("ERROR_MESSAGE: " + (request.getAttribute(ERROR_MESSAGE))));
            writer.println(("ERROR_CODE: " + (request.getAttribute(ERROR_STATUS_CODE))));
            writer.println(("ERROR_EXCEPTION: " + (request.getAttribute(ERROR_EXCEPTION))));
            writer.println(("ERROR_EXCEPTION_TYPE: " + (request.getAttribute(ERROR_EXCEPTION_TYPE))));
            writer.println(("ERROR_SERVLET: " + (request.getAttribute(ERROR_SERVLET_NAME))));
            writer.println(("ERROR_REQUEST_URI: " + (request.getAttribute(ERROR_REQUEST_URI))));
            writer.println(("getParameterMap()= " + (request.getParameterMap())));
        }
    }
}

