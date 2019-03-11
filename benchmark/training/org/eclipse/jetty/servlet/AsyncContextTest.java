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


import AsyncContext.ASYNC_CONTEXT_PATH;
import AsyncContext.ASYNC_PATH_INFO;
import AsyncContext.ASYNC_QUERY_STRING;
import AsyncContext.ASYNC_REQUEST_URI;
import AsyncContext.ASYNC_SERVLET_PATH;
import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import HttpTester.Response;
import RequestDispatcher.ERROR_EXCEPTION;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.QuietServletException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This tests the correct functioning of the AsyncContext
 * <p/>
 * tests for #371649 and #371635
 */
public class AsyncContextTest {
    private Server _server;

    private ServletContextHandler _contextHandler;

    private LocalConnector _connector;

    @Test
    public void testSimpleAsyncContext() throws Exception {
        String request = "GET /ctx/servletPath HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat(responseBody, Matchers.containsString("doGet:getServletPath:/servletPath"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("doGet:async:getServletPath:/servletPath"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("async:run:attr:servletPath:/servletPath"));
    }

    @Test
    public void testStartThrow() throws Exception {
        String request = "GET /ctx/startthrow HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request, 10, TimeUnit.MINUTES));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_INTERNAL_SERVER_ERROR));
        String responseBody = response.getContent();
        MatcherAssert.assertThat(responseBody, Matchers.containsString("ERROR: /error"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("PathInfo= /IOE"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("EXCEPTION: org.eclipse.jetty.server.QuietServletException: java.io.IOException: Test"));
    }

    @Test
    public void testStartDispatchThrow() throws Exception {
        String request = "" + ((("GET /ctx/startthrow?dispatch=true HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_INTERNAL_SERVER_ERROR));
        String responseBody = response.getContent();
        MatcherAssert.assertThat(responseBody, Matchers.containsString("ERROR: /error"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("PathInfo= /IOE"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("EXCEPTION: org.eclipse.jetty.server.QuietServletException: java.io.IOException: Test"));
    }

    @Test
    public void testStartCompleteThrow() throws Exception {
        String request = "GET /ctx/startthrow?complete=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_INTERNAL_SERVER_ERROR));
        String responseBody = response.getContent();
        MatcherAssert.assertThat(responseBody, Matchers.containsString("ERROR: /error"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("PathInfo= /IOE"));
        MatcherAssert.assertThat(responseBody, Matchers.containsString("EXCEPTION: org.eclipse.jetty.server.QuietServletException: java.io.IOException: Test"));
    }

    @Test
    public void testStartFlushCompleteThrow() throws Exception {
        try (StacklessLogging ignore = new StacklessLogging(HttpChannel.class)) {
            String request = "GET /ctx/startthrow?flush=true&complete=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
            HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
            MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
            String responseBody = response.getContent();
            MatcherAssert.assertThat("error servlet", responseBody, Matchers.containsString("completeBeforeThrow"));
        }
    }

    @Test
    public void testDispatchAsyncContext() throws Exception {
        String request = "GET /ctx/servletPath?dispatch=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("servlet gets right path", responseBody, Matchers.containsString("doGet:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("async context gets right path in get", responseBody, Matchers.containsString("doGet:async:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("servlet path attr is original", responseBody, Matchers.containsString("async:run:attr:servletPath:/servletPath"));
        MatcherAssert.assertThat("path info attr is correct", responseBody, Matchers.containsString("async:run:attr:pathInfo:null"));
        MatcherAssert.assertThat("query string attr is correct", responseBody, Matchers.containsString("async:run:attr:queryString:dispatch=true"));
        MatcherAssert.assertThat("context path attr is correct", responseBody, Matchers.containsString("async:run:attr:contextPath:/ctx"));
        MatcherAssert.assertThat("request uri attr is correct", responseBody, Matchers.containsString("async:run:attr:requestURI:/ctx/servletPath"));
    }

    @Test
    public void testDispatchAsyncContext_EncodedUrl() throws Exception {
        String request = "GET /ctx/test/hello%2fthere?dispatch=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        // initial values
        MatcherAssert.assertThat("servlet gets right path", responseBody, Matchers.containsString("doGet:getServletPath:/test2"));
        MatcherAssert.assertThat("request uri has correct encoding", responseBody, Matchers.containsString("doGet:getRequestURI:/ctx/test2/something%2felse"));
        MatcherAssert.assertThat("request url has correct encoding", responseBody, Matchers.containsString("doGet:getRequestURL:http://localhost/ctx/test2/something%2felse"));
        MatcherAssert.assertThat("path info has correct encoding", responseBody, Matchers.containsString("doGet:getPathInfo:/something/else"));
        // async values
        MatcherAssert.assertThat("async servlet gets right path", responseBody, Matchers.containsString("doGet:async:getServletPath:/test2"));
        MatcherAssert.assertThat("async request uri has correct encoding", responseBody, Matchers.containsString("doGet:async:getRequestURI:/ctx/test2/something%2felse"));
        MatcherAssert.assertThat("async request url has correct encoding", responseBody, Matchers.containsString("doGet:async:getRequestURL:http://localhost/ctx/test2/something%2felse"));
        MatcherAssert.assertThat("async path info has correct encoding", responseBody, Matchers.containsString("doGet:async:getPathInfo:/something/else"));
        // async run attributes
        MatcherAssert.assertThat("async run attr servlet path is original", responseBody, Matchers.containsString("async:run:attr:servletPath:/test"));
        MatcherAssert.assertThat("async run attr path info has correct encoding", responseBody, Matchers.containsString("async:run:attr:pathInfo:/hello/there"));
        MatcherAssert.assertThat("async run attr query string", responseBody, Matchers.containsString("async:run:attr:queryString:dispatch=true"));
        MatcherAssert.assertThat("async run context path", responseBody, Matchers.containsString("async:run:attr:contextPath:/ctx"));
        MatcherAssert.assertThat("async run request uri has correct encoding", responseBody, Matchers.containsString("async:run:attr:requestURI:/ctx/test/hello%2fthere"));
    }

    @Test
    public void testDispatchAsyncContext_SelfEncodedUrl() throws Exception {
        String request = "GET /ctx/self/hello%2fthere?dispatch=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("servlet request uri initial", responseBody, Matchers.containsString("doGet.REQUEST.requestURI:/ctx/self/hello%2fthere"));
        MatcherAssert.assertThat("servlet request uri async", responseBody, Matchers.containsString("doGet.ASYNC.requestURI:/ctx/self/hello%2fthere"));
    }

    @Test
    public void testDispatchAsyncContextEncodedPathAndQueryString() throws Exception {
        String request = "GET /ctx/path%20with%20spaces/servletPath?dispatch=true&queryStringWithEncoding=space%20space HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("servlet gets right path", responseBody, Matchers.containsString("doGet:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("async context gets right path in get", responseBody, Matchers.containsString("doGet:async:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("servlet path attr is original", responseBody, Matchers.containsString("async:run:attr:servletPath:/path with spaces/servletPath"));
        MatcherAssert.assertThat("path info attr is correct", responseBody, Matchers.containsString("async:run:attr:pathInfo:null"));
        MatcherAssert.assertThat("query string attr is correct", responseBody, Matchers.containsString("async:run:attr:queryString:dispatch=true&queryStringWithEncoding=space%20space"));
        MatcherAssert.assertThat("context path attr is correct", responseBody, Matchers.containsString("async:run:attr:contextPath:/ctx"));
        MatcherAssert.assertThat("request uri attr is correct", responseBody, Matchers.containsString("async:run:attr:requestURI:/ctx/path%20with%20spaces/servletPath"));
    }

    @Test
    public void testSimpleWithContextAsyncContext() throws Exception {
        String request = "GET /ctx/servletPath HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("servlet gets right path", responseBody, Matchers.containsString("doGet:getServletPath:/servletPath"));
        MatcherAssert.assertThat("async context gets right path in get", responseBody, Matchers.containsString("doGet:async:getServletPath:/servletPath"));
        MatcherAssert.assertThat("async context gets right path in async", responseBody, Matchers.containsString("async:run:attr:servletPath:/servletPath"));
    }

    @Test
    public void testDispatchWithContextAsyncContext() throws Exception {
        String request = "GET /ctx/servletPath?dispatch=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("servlet gets right path", responseBody, Matchers.containsString("doGet:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("async context gets right path in get", responseBody, Matchers.containsString("doGet:async:getServletPath:/servletPath2"));
        MatcherAssert.assertThat("servlet path attr is original", responseBody, Matchers.containsString("async:run:attr:servletPath:/servletPath"));
        MatcherAssert.assertThat("path info attr is correct", responseBody, Matchers.containsString("async:run:attr:pathInfo:null"));
        MatcherAssert.assertThat("query string attr is correct", responseBody, Matchers.containsString("async:run:attr:queryString:dispatch=true"));
        MatcherAssert.assertThat("context path attr is correct", responseBody, Matchers.containsString("async:run:attr:contextPath:/ctx"));
        MatcherAssert.assertThat("request uri attr is correct", responseBody, Matchers.containsString("async:run:attr:requestURI:/ctx/servletPath"));
    }

    @Test
    public void testDispatch() throws Exception {
        String request = "GET /ctx/forward HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        String responseString = _connector.getResponse(request);
        HttpTester.Response response = HttpTester.parseResponse(responseString);
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("!ForwardingServlet", responseBody, Matchers.containsString("Dispatched back to ForwardingServlet"));
    }

    @Test
    public void testDispatchRequestResponse() throws Exception {
        String request = "GET /ctx/forward?dispatchRequestResponse=true HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        String responseString = _connector.getResponse(request);
        HttpTester.Response response = HttpTester.parseResponse(responseString);
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_OK));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("!AsyncDispatchingServlet", responseBody, Matchers.containsString("Dispatched back to AsyncDispatchingServlet"));
    }

    private class ForwardingServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            if ((request.getDispatcherType()) == (DispatcherType.ASYNC)) {
                response.getOutputStream().print("Dispatched back to ForwardingServlet");
            } else {
                request.getRequestDispatcher("/dispatchingServlet").forward(request, response);
            }
        }
    }

    private class SelfDispatchingServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            DispatcherType dispatcherType = request.getDispatcherType();
            response.getOutputStream().print((((("doGet." + (dispatcherType.name())) + ".requestURI:") + (request.getRequestURI())) + "\n"));
            if (dispatcherType == (DispatcherType.ASYNC)) {
                response.getOutputStream().print((("Dispatched back to " + (AsyncContextTest.SelfDispatchingServlet.class.getSimpleName())) + "\n"));
            } else {
                final AsyncContext asyncContext = request.startAsync(request, response);
                new Thread(() -> asyncContext.dispatch()).start();
            }
        }
    }

    private class AsyncDispatchingServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, final HttpServletResponse response) throws IOException, ServletException {
            Request request = ((Request) (req));
            if ((request.getDispatcherType()) == (DispatcherType.ASYNC)) {
                response.getOutputStream().print("Dispatched back to AsyncDispatchingServlet");
            } else {
                boolean wrapped = false;
                final AsyncContext asyncContext;
                if ((request.getParameter("dispatchRequestResponse")) != null) {
                    wrapped = true;
                    asyncContext = request.startAsync(request, new AsyncContextTest.Wrapper(response));
                } else {
                    asyncContext = request.startAsync();
                }
                new Thread(new AsyncContextTest.DispatchingRunnable(asyncContext, wrapped)).start();
            }
        }
    }

    @Test
    public void testExpire() throws Exception {
        String request = "GET /ctx/expire HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_INTERNAL_SERVER_ERROR));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("error servlet", responseBody, Matchers.containsString("ERROR: /error"));
    }

    @Test
    public void testBadExpire() throws Exception {
        String request = "GET /ctx/badexpire HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        HttpTester.Response response = HttpTester.parseResponse(_connector.getResponse(request));
        MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_INTERNAL_SERVER_ERROR));
        String responseBody = response.getContent();
        MatcherAssert.assertThat("error servlet", responseBody, Matchers.containsString("ERROR: /error"));
        MatcherAssert.assertThat("error servlet", responseBody, Matchers.containsString("PathInfo= /500"));
        MatcherAssert.assertThat("error servlet", responseBody, Matchers.containsString("EXCEPTION: java.lang.RuntimeException: TEST"));
    }

    private class DispatchingRunnable implements Runnable {
        private AsyncContext asyncContext;

        private boolean wrapped;

        public DispatchingRunnable(AsyncContext asyncContext, boolean wrapped) {
            this.asyncContext = asyncContext;
            this.wrapped = wrapped;
        }

        @Override
        public void run() {
            if (wrapped)
                Assertions.assertTrue(((asyncContext.getResponse()) instanceof AsyncContextTest.Wrapper));

            asyncContext.dispatch();
        }
    }

    private class ErrorServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.getOutputStream().print((("ERROR: " + (request.getServletPath())) + "\n"));
            response.getOutputStream().print((("PathInfo= " + (request.getPathInfo())) + "\n"));
            if ((request.getAttribute(ERROR_EXCEPTION)) != null)
                response.getOutputStream().print((("EXCEPTION: " + (request.getAttribute(ERROR_EXCEPTION))) + "\n"));

        }
    }

    private class ExpireServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ((request.getDispatcherType()) == (DispatcherType.REQUEST)) {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(100);
            }
        }
    }

    private class BadExpireServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ((request.getDispatcherType()) == (DispatcherType.REQUEST)) {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.addListener(new AsyncListener() {
                    @Override
                    public void onTimeout(AsyncEvent event) throws IOException {
                        throw new RuntimeException("TEST");
                    }

                    @Override
                    public void onStartAsync(AsyncEvent event) throws IOException {
                    }

                    @Override
                    public void onError(AsyncEvent event) throws IOException {
                    }

                    @Override
                    public void onComplete(AsyncEvent event) throws IOException {
                    }
                });
                asyncContext.setTimeout(100);
            }
        }
    }

    private class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        private String dispatchPath = "/servletPath2";

        @Override
        public void init() throws ServletException {
            String dispatchTo = getServletConfig().getInitParameter("dispatchPath");
            if (StringUtil.isNotBlank(dispatchTo)) {
                this.dispatchPath = dispatchTo;
            }
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ((request.getParameter("dispatch")) != null) {
                AsyncContext asyncContext = request.startAsync(request, response);
                asyncContext.dispatch(dispatchPath);
            } else {
                response.getOutputStream().print((("doGet:getServletPath:" + (request.getServletPath())) + "\n"));
                response.getOutputStream().print((("doGet:getRequestURI:" + (request.getRequestURI())) + "\n"));
                response.getOutputStream().print((("doGet:getRequestURL:" + (request.getRequestURL())) + "\n"));
                response.getOutputStream().print((("doGet:getPathInfo:" + (request.getPathInfo())) + "\n"));
                AsyncContext asyncContext = request.startAsync(request, response);
                HttpServletRequest asyncRequest = ((HttpServletRequest) (asyncContext.getRequest()));
                response.getOutputStream().print((("doGet:async:getServletPath:" + (asyncRequest.getServletPath())) + "\n"));
                response.getOutputStream().print((("doGet:async:getRequestURI:" + (asyncRequest.getRequestURI())) + "\n"));
                response.getOutputStream().print((("doGet:async:getRequestURL:" + (asyncRequest.getRequestURL())) + "\n"));
                response.getOutputStream().print((("doGet:async:getPathInfo:" + (asyncRequest.getPathInfo())) + "\n"));
                asyncContext.start(new AsyncContextTest.AsyncRunnable(asyncContext));
            }
        }
    }

    private class TestServlet2 extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.getOutputStream().print((("doGet:getServletPath:" + (request.getServletPath())) + "\n"));
            response.getOutputStream().print((("doGet:getRequestURI:" + (request.getRequestURI())) + "\n"));
            response.getOutputStream().print((("doGet:getRequestURL:" + (request.getRequestURL())) + "\n"));
            response.getOutputStream().print((("doGet:getPathInfo:" + (request.getPathInfo())) + "\n"));
            AsyncContext asyncContext = request.startAsync(request, response);
            HttpServletRequest asyncRequest = ((HttpServletRequest) (asyncContext.getRequest()));
            response.getOutputStream().print((("doGet:async:getServletPath:" + (asyncRequest.getServletPath())) + "\n"));
            response.getOutputStream().print((("doGet:async:getRequestURI:" + (asyncRequest.getRequestURI())) + "\n"));
            response.getOutputStream().print((("doGet:async:getRequestURL:" + (asyncRequest.getRequestURL())) + "\n"));
            response.getOutputStream().print((("doGet:async:getPathInfo:" + (asyncRequest.getPathInfo())) + "\n"));
            asyncContext.start(new AsyncContextTest.AsyncRunnable(asyncContext));
        }
    }

    private class TestStartThrowServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ((request.getDispatcherType()) == (DispatcherType.REQUEST)) {
                request.startAsync(request, response);
                if (Boolean.parseBoolean(request.getParameter("dispatch"))) {
                    request.getAsyncContext().dispatch();
                }
                if (Boolean.parseBoolean(request.getParameter("complete"))) {
                    response.getOutputStream().write("completeBeforeThrow".getBytes());
                    if (Boolean.parseBoolean(request.getParameter("flush")))
                        response.flushBuffer();

                    request.getAsyncContext().complete();
                }
                throw new QuietServletException(new IOException("Test"));
            }
        }
    }

    private class AsyncRunnable implements Runnable {
        private AsyncContext _context;

        public AsyncRunnable(AsyncContext context) {
            _context = context;
        }

        @Override
        public void run() {
            HttpServletRequest req = ((HttpServletRequest) (_context.getRequest()));
            try {
                _context.getResponse().getOutputStream().print((("async:run:attr:servletPath:" + (req.getAttribute(ASYNC_SERVLET_PATH))) + "\n"));
                _context.getResponse().getOutputStream().print((("async:run:attr:pathInfo:" + (req.getAttribute(ASYNC_PATH_INFO))) + "\n"));
                _context.getResponse().getOutputStream().print((("async:run:attr:queryString:" + (req.getAttribute(ASYNC_QUERY_STRING))) + "\n"));
                _context.getResponse().getOutputStream().print((("async:run:attr:contextPath:" + (req.getAttribute(ASYNC_CONTEXT_PATH))) + "\n"));
                _context.getResponse().getOutputStream().print((("async:run:attr:requestURI:" + (req.getAttribute(ASYNC_REQUEST_URI))) + "\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            _context.complete();
        }
    }

    private class Wrapper extends HttpServletResponseWrapper {
        public Wrapper(HttpServletResponse response) {
            super(response);
        }
    }
}

