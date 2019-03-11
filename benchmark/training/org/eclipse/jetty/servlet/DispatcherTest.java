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


import Dispatcher.FORWARD_CONTEXT_PATH;
import Dispatcher.FORWARD_PATH_INFO;
import Dispatcher.FORWARD_QUERY_STRING;
import Dispatcher.FORWARD_REQUEST_URI;
import Dispatcher.FORWARD_SERVLET_PATH;
import Dispatcher.INCLUDE_CONTEXT_PATH;
import Dispatcher.INCLUDE_PATH_INFO;
import Dispatcher.INCLUDE_QUERY_STRING;
import Dispatcher.INCLUDE_REQUEST_URI;
import Dispatcher.INCLUDE_SERVLET_PATH;
import DispatcherType.REQUEST;
import HttpServletResponse.SC_OK;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DispatcherTest {
    private Server _server;

    private LocalConnector _connector;

    private ContextHandlerCollection _contextCollection;

    private ServletContextHandler _contextHandler;

    private ResourceHandler _resourceHandler;

    @Test
    public void testForward() throws Exception {
        _contextHandler.addServlet(DispatcherTest.ForwardServlet.class, "/ForwardServlet/*");
        _contextHandler.addServlet(DispatcherTest.AssertForwardServlet.class, "/AssertForwardServlet/*");
        String expected = "HTTP/1.1 200 OK\r\n" + (("Content-Type: text/html\r\n" + "Content-Length: 0\r\n") + "\r\n");
        String responses = _connector.getResponse("GET /context/ForwardServlet?do=assertforward&do=more&test=1 HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testForwardNonUTF8() throws Exception {
        _contextHandler.addServlet(DispatcherTest.ForwardNonUTF8Servlet.class, "/ForwardServlet/*");
        _contextHandler.addServlet(DispatcherTest.AssertNonUTF8ForwardServlet.class, "/AssertForwardServlet/*");
        String expected = "HTTP/1.1 200 OK\r\n" + (("Content-Type: text/html\r\n" + "Content-Length: 0\r\n") + "\r\n");
        String responses = _connector.getResponse("GET /context/ForwardServlet?do=assertforward&foreign=%d2%e5%ec%ef%e5%f0%e0%f2%f3%f0%e0&test=1 HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testForwardWithParam() throws Exception {
        _contextHandler.addServlet(DispatcherTest.ForwardServlet.class, "/ForwardServlet/*");
        _contextHandler.addServlet(DispatcherTest.EchoURIServlet.class, "/EchoURI/*");
        String expected = "HTTP/1.1 200 OK\r\n" + (((((("Content-Type: text/plain\r\n" + "Content-Length: 54\r\n") + "\r\n") + "/context\r\n") + "/EchoURI\r\n") + "/x x\r\n") + "/context/EchoURI/x%20x;a=1\r\n");
        String responses = _connector.getResponse("GET /context/ForwardServlet;ignore=true?do=req.echo&uri=EchoURI%2Fx%2520x%3Ba=1%3Fb=2 HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testForwardWithBadParams() throws Exception {
        try (StacklessLogging nostack = new StacklessLogging(ServletHandler.class)) {
            Log.getLogger(ServletHandler.class).info("Expect Not valid UTF8 warnings...");
            _contextHandler.addServlet(DispatcherTest.AlwaysForwardServlet.class, "/forward/*");
            _contextHandler.addServlet(DispatcherTest.EchoServlet.class, "/echo/*");
            String response;
            response = _connector.getResponse("GET /context/forward/?echo=allgood HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString("allgood"));
            response = _connector.getResponse("GET /context/forward/params?echo=allgood HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString("allgood"));
            MatcherAssert.assertThat(response, Matchers.containsString("forward"));
            response = _connector.getResponse("GET /context/forward/badparams?echo=badparams HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 500 "));
            response = _connector.getResponse("GET /context/forward/?echo=badclient&bad=%88%A4 HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 400 "));
            response = _connector.getResponse("GET /context/forward/params?echo=badclient&bad=%88%A4 HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 400 "));
            response = _connector.getResponse("GET /context/forward/badparams?echo=badclientandparam&bad=%88%A4 HTTP/1.0\n\n");
            MatcherAssert.assertThat(response, Matchers.containsString(" 500 "));
        }
    }

    @Test
    public void testInclude() throws Exception {
        _contextHandler.addServlet(DispatcherTest.IncludeServlet.class, "/IncludeServlet/*");
        _contextHandler.addServlet(DispatcherTest.AssertIncludeServlet.class, "/AssertIncludeServlet/*");
        String expected = "HTTP/1.1 200 OK\r\n" + "\r\n";
        String responses = _connector.getResponse("GET /context/IncludeServlet?do=assertinclude&do=more&test=1 HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testForwardThenInclude() throws Exception {
        _contextHandler.addServlet(DispatcherTest.ForwardServlet.class, "/ForwardServlet/*");
        _contextHandler.addServlet(DispatcherTest.IncludeServlet.class, "/IncludeServlet/*");
        _contextHandler.addServlet(DispatcherTest.AssertForwardIncludeServlet.class, "/AssertForwardIncludeServlet/*");
        String expected = "HTTP/1.1 200 OK\r\n" + "\r\n";
        String responses = _connector.getResponse("GET /context/ForwardServlet/forwardpath?do=include HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testIncludeThenForward() throws Exception {
        _contextHandler.addServlet(DispatcherTest.IncludeServlet.class, "/IncludeServlet/*");
        _contextHandler.addServlet(DispatcherTest.ForwardServlet.class, "/ForwardServlet/*");
        _contextHandler.addServlet(DispatcherTest.AssertIncludeForwardServlet.class, "/AssertIncludeForwardServlet/*");
        String expected = "HTTP/1.1 200 OK\r\n" + "\r\n";
        String responses = _connector.getResponse("GET /context/IncludeServlet/includepath?do=forward HTTP/1.0\n\n");
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testServletForward() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchServletServlet.class, "/dispatch/*");
        _contextHandler.addServlet(DispatcherTest.RogerThatServlet.class, "/roger/*");
        String expected = "HTTP/1.1 200 OK\r\n" + (("Content-Length: 11\r\n" + "\r\n") + "Roger That!");
        String responses = _connector.getResponse(("GET /context/dispatch/test?forward=/roger/that HTTP/1.0\n" + "Host: localhost\n\n"));
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testServletForwardDotDot() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchServletServlet.class, "/dispatch/*");
        _contextHandler.addServlet(DispatcherTest.RogerThatServlet.class, "/roger/that");
        String requests = "GET /context/dispatch/test?forward=/%2e%2e/roger/that HTTP/1.0\n" + "Host: localhost\n\n";
        String responses = _connector.getResponse(requests);
        MatcherAssert.assertThat(responses, Matchers.startsWith("HTTP/1.1 404 "));
    }

    @Test
    public void testServletForwardEncodedDotDot() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchServletServlet.class, "/dispatch/*");
        _contextHandler.addServlet(DispatcherTest.RogerThatServlet.class, "/roger/that");
        String requests = "GET /context/dispatch/test?forward=/%252e%252e/roger/that HTTP/1.0\n" + "Host: localhost\n\n";
        String responses = _connector.getResponse(requests);
        MatcherAssert.assertThat(responses, Matchers.startsWith("HTTP/1.1 404 "));
    }

    @Test
    public void testServletInclude() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchServletServlet.class, "/dispatch/*");
        _contextHandler.addServlet(DispatcherTest.RogerThatServlet.class, "/roger/*");
        String expected = "HTTP/1.1 200 OK\r\n" + (("Content-Length: 11\r\n" + "\r\n") + "Roger That!");
        String responses = _connector.getResponse(("GET /context/dispatch/test?include=/roger/that HTTP/1.0\n" + "Host: localhost\n\n"));
        Assertions.assertEquals(expected, responses);
    }

    @Test
    public void testWorkingResourceHandler() throws Exception {
        String responses = _connector.getResponse(("GET /resource/content.txt HTTP/1.0\n" + "Host: localhost\n\n"));
        MatcherAssert.assertThat(responses, Matchers.containsString("content goes here"));// from inside the context.txt file

    }

    @Test
    public void testIncludeToResourceHandler() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchToResourceServlet.class, "/resourceServlet/*");
        String responses = _connector.getResponse(("GET /context/resourceServlet/content.txt?do=include HTTP/1.0\n" + "Host: localhost\n\n"));
        // from inside the context.txt file
        Assertions.assertNotNull(responses);
        MatcherAssert.assertThat(responses, Matchers.containsString("content goes here"));
    }

    @Test
    public void testForwardToResourceHandler() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchToResourceServlet.class, "/resourceServlet/*");
        String responses = _connector.getResponse(("GET /context/resourceServlet/content.txt?do=forward HTTP/1.0\n" + "Host: localhost\n\n"));
        // from inside the context.txt file
        MatcherAssert.assertThat(responses, Matchers.containsString("content goes here"));
    }

    @Test
    public void testWrappedIncludeToResourceHandler() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchToResourceServlet.class, "/resourceServlet/*");
        String responses = _connector.getResponse(("GET /context/resourceServlet/content.txt?do=include&wrapped=true HTTP/1.0\n" + "Host: localhost\n\n"));
        // from inside the context.txt file
        MatcherAssert.assertThat(responses, Matchers.containsString("content goes here"));
    }

    @Test
    public void testWrappedForwardToResourceHandler() throws Exception {
        _contextHandler.addServlet(DispatcherTest.DispatchToResourceServlet.class, "/resourceServlet/*");
        String responses = _connector.getResponse(("GET /context/resourceServlet/content.txt?do=forward&wrapped=true HTTP/1.0\n" + "Host: localhost\n\n"));
        // from inside the context.txt file
        MatcherAssert.assertThat(responses, Matchers.containsString("content goes here"));
    }

    @Test
    public void testForwardFilterToRogerServlet() throws Exception {
        _contextHandler.addServlet(DispatcherTest.RogerThatServlet.class, "/*");
        _contextHandler.addServlet(DispatcherTest.ReserveEchoServlet.class, "/recho/*");
        _contextHandler.addServlet(DispatcherTest.EchoServlet.class, "/echo/*");
        _contextHandler.addFilter(DispatcherTest.ForwardFilter.class, "/*", EnumSet.of(REQUEST));
        String rogerResponse = _connector.getResponse(("GET /context/ HTTP/1.0\n" + "Host: localhost\n\n"));
        String echoResponse = _connector.getResponse(("GET /context/foo?echo=echoText HTTP/1.0\n" + "Host: localhost\n\n"));
        String rechoResponse = _connector.getResponse(("GET /context/?echo=echoText HTTP/1.0\n" + "Host: localhost\n\n"));
        MatcherAssert.assertThat(rogerResponse, Matchers.containsString("Roger That!"));
        MatcherAssert.assertThat(echoResponse, Matchers.containsString("echoText"));
        MatcherAssert.assertThat(rechoResponse, Matchers.containsString("txeTohce"));
    }

    public static class ForwardServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            RequestDispatcher dispatcher = null;
            if (request.getParameter("do").equals("include"))
                dispatcher = getServletContext().getRequestDispatcher("/IncludeServlet/includepath?do=assertforwardinclude");
            else
                if (request.getParameter("do").equals("assertincludeforward"))
                    dispatcher = getServletContext().getRequestDispatcher("/AssertIncludeForwardServlet/assertpath?do=end");
                else
                    if (request.getParameter("do").equals("assertforward"))
                        dispatcher = getServletContext().getRequestDispatcher("/AssertForwardServlet?do=end&do=the");
                    else
                        if (request.getParameter("do").equals("ctx.echo"))
                            dispatcher = getServletContext().getRequestDispatcher(request.getParameter("uri"));
                        else
                            if (request.getParameter("do").equals("req.echo"))
                                dispatcher = request.getRequestDispatcher(request.getParameter("uri"));





            dispatcher.forward(request, response);
        }
    }

    public static class AlwaysForwardServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ("/params".equals(request.getPathInfo()))
                getServletContext().getRequestDispatcher("/echo?echo=forward").forward(request, response);
            else
                if ("/badparams".equals(request.getPathInfo()))
                    getServletContext().getRequestDispatcher("/echo?echo=forward&fbad=%88%A4").forward(request, response);
                else
                    getServletContext().getRequestDispatcher("/echo").forward(request, response);


        }
    }

    public static class ForwardNonUTF8Servlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            RequestDispatcher dispatcher = null;
            request.setAttribute("org.eclipse.jetty.server.Request.queryEncoding", "cp1251");
            dispatcher = getServletContext().getRequestDispatcher("/AssertForwardServlet?do=end&else=%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BD%D0%BE%3D%D0%A2%D0%B5%D0%BC%D0%BF%D0%B5%D1%80%D0%B0%D1%82%D1%83%D1%80%D0%B0");
            dispatcher.forward(request, response);
        }
    }

    /* Forward filter works with roger, echo and reverse echo servlets to test various
    forwarding bits using filters.

    when there is an echo parameter and the path info is / it forwards to the reverse echo
    anything else in the pathInfo and it sends straight to the echo servlet...otherwise its
    all roger servlet
     */
    public static class ForwardFilter implements Filter {
        ServletContext servletContext;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            servletContext = filterConfig.getServletContext().getContext("/context");
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if ((((servletContext) == null) || (!(request instanceof HttpServletRequest))) || (!(response instanceof HttpServletResponse))) {
                chain.doFilter(request, response);
                return;
            }
            HttpServletRequest req = ((HttpServletRequest) (request));
            HttpServletResponse resp = ((HttpServletResponse) (response));
            if (((req.getParameter("echo")) != null) && ("/".equals(req.getPathInfo()))) {
                RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/recho");
                dispatcher.forward(request, response);
            } else
                if ((req.getParameter("echo")) != null) {
                    RequestDispatcher dispatcher = servletContext.getRequestDispatcher("/echo");
                    dispatcher.forward(request, response);
                } else {
                    chain.doFilter(request, response);
                    return;
                }

        }

        @Override
        public void destroy() {
        }
    }

    public static class DispatchServletServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            RequestDispatcher dispatcher = null;
            if ((request.getParameter("include")) != null) {
                dispatcher = getServletContext().getRequestDispatcher(request.getParameter("include"));
                dispatcher.include(new javax.servlet.ServletRequestWrapper(request), new javax.servlet.ServletResponseWrapper(response));
            } else
                if ((request.getParameter("forward")) != null) {
                    dispatcher = getServletContext().getRequestDispatcher(request.getParameter("forward"));
                    if (dispatcher != null)
                        dispatcher.forward(new javax.servlet.ServletRequestWrapper(request), new javax.servlet.ServletResponseWrapper(response));
                    else
                        response.sendError(404);

                }

        }
    }

    public static class IncludeServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            RequestDispatcher dispatcher = null;
            if (request.getParameter("do").equals("forward"))
                dispatcher = getServletContext().getRequestDispatcher("/ForwardServlet/forwardpath?do=assertincludeforward");
            else
                if (request.getParameter("do").equals("assertforwardinclude"))
                    dispatcher = getServletContext().getRequestDispatcher("/AssertForwardIncludeServlet/assertpath?do=end");
                else
                    if (request.getParameter("do").equals("assertinclude"))
                        dispatcher = getServletContext().getRequestDispatcher("/AssertIncludeServlet?do=end&do=the");



            dispatcher.include(request, response);
        }
    }

    public static class RogerThatServlet extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            res.getWriter().print("Roger That!");
        }
    }

    public static class EchoServlet extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            String[] echoText = req.getParameterValues("echo");
            if ((echoText == null) || ((echoText.length) == 0)) {
                throw new ServletException("echo is a required parameter");
            } else
                if ((echoText.length) == 1) {
                    res.getWriter().print(echoText[0]);
                } else {
                    for (String text : echoText)
                        res.getWriter().print(text);

                }

        }
    }

    public static class ReserveEchoServlet extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            String echoText = req.getParameter("echo");
            if (echoText == null) {
                throw new ServletException("echo is a required parameter");
            } else {
                res.getWriter().print(new StringBuffer(echoText).reverse().toString());
            }
        }
    }

    public static class DispatchToResourceServlet extends HttpServlet implements Servlet {
        @Override
        public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            ServletContext targetContext = getServletContext().getContext("/resource");
            RequestDispatcher dispatcher = targetContext.getRequestDispatcher(req.getPathInfo());
            if ("true".equals(req.getParameter("wrapped"))) {
                if (req.getParameter("do").equals("forward")) {
                    dispatcher.forward(new javax.servlet.http.HttpServletRequestWrapper(req), new javax.servlet.http.HttpServletResponseWrapper(res));
                } else
                    if (req.getParameter("do").equals("include")) {
                        dispatcher.include(new javax.servlet.http.HttpServletRequestWrapper(req), new javax.servlet.http.HttpServletResponseWrapper(res));
                    } else {
                        throw new ServletException("type of forward or include is required");
                    }

            } else {
                if (req.getParameter("do").equals("forward")) {
                    dispatcher.forward(req, res);
                } else
                    if (req.getParameter("do").equals("include")) {
                        dispatcher.include(req, res);
                    } else {
                        throw new ServletException("type of forward or include is required");
                    }

            }
        }
    }

    public static class EchoURIServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/plain");
            response.setStatus(SC_OK);
            response.getOutputStream().println(request.getContextPath());
            response.getOutputStream().println(request.getServletPath());
            response.getOutputStream().println(request.getPathInfo());
            response.getOutputStream().println(request.getRequestURI());
        }
    }

    public static class AssertForwardServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            Assertions.assertEquals("/context/ForwardServlet", request.getAttribute(FORWARD_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(FORWARD_CONTEXT_PATH));
            Assertions.assertEquals("/ForwardServlet", request.getAttribute(FORWARD_SERVLET_PATH));
            Assertions.assertEquals(null, request.getAttribute(FORWARD_PATH_INFO));
            Assertions.assertEquals("do=assertforward&do=more&test=1", request.getAttribute(FORWARD_QUERY_STRING));
            List<String> expectedAttributeNames = Arrays.asList(FORWARD_REQUEST_URI, FORWARD_CONTEXT_PATH, FORWARD_SERVLET_PATH, FORWARD_QUERY_STRING);
            List<String> requestAttributeNames = Collections.list(request.getAttributeNames());
            Assertions.assertTrue(requestAttributeNames.containsAll(expectedAttributeNames));
            Assertions.assertEquals(null, request.getPathInfo());
            Assertions.assertEquals(null, request.getPathTranslated());
            Assertions.assertEquals("do=end&do=the&test=1", request.getQueryString());
            Assertions.assertEquals("/context/AssertForwardServlet", request.getRequestURI());
            Assertions.assertEquals("/context", request.getContextPath());
            Assertions.assertEquals("/AssertForwardServlet", request.getServletPath());
            response.setContentType("text/html");
            response.setStatus(SC_OK);
        }
    }

    public static class AssertNonUTF8ForwardServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            byte[] cp1251_bytes = TypeUtil.fromHexString("d2e5ecefe5f0e0f2f3f0e0");
            String expectedCP1251String = new String(cp1251_bytes, "cp1251");
            Assertions.assertEquals("/context/ForwardServlet", request.getAttribute(FORWARD_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(FORWARD_CONTEXT_PATH));
            Assertions.assertEquals("/ForwardServlet", request.getAttribute(FORWARD_SERVLET_PATH));
            Assertions.assertEquals(null, request.getAttribute(FORWARD_PATH_INFO));
            Assertions.assertEquals("do=assertforward&foreign=%d2%e5%ec%ef%e5%f0%e0%f2%f3%f0%e0&test=1", request.getAttribute(FORWARD_QUERY_STRING));
            List<String> expectedAttributeNames = Arrays.asList(FORWARD_REQUEST_URI, FORWARD_CONTEXT_PATH, FORWARD_SERVLET_PATH, FORWARD_QUERY_STRING);
            List<String> requestAttributeNames = Collections.list(request.getAttributeNames());
            Assertions.assertTrue(requestAttributeNames.containsAll(expectedAttributeNames));
            Assertions.assertEquals(null, request.getPathInfo());
            Assertions.assertEquals(null, request.getPathTranslated());
            UrlEncoded query = new UrlEncoded();
            query.decode(request.getQueryString());
            MatcherAssert.assertThat(query.getString("do"), Matchers.is("end"));
            // Russian for "selected=Temperature"
            UrlEncoded q2 = new UrlEncoded();
            q2.decode(query.getString("else"));
            String russian = q2.encode();
            MatcherAssert.assertThat(russian, Matchers.is("%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BD%D0%BE=%D0%A2%D0%B5%D0%BC%D0%BF%D0%B5%D1%80%D0%B0%D1%82%D1%83%D1%80%D0%B0"));
            MatcherAssert.assertThat(query.getString("test"), Matchers.is("1"));
            MatcherAssert.assertThat(query.containsKey("foreign"), Matchers.is(true));
            String[] vals = request.getParameterValues("foreign");
            Assertions.assertTrue((vals != null));
            Assertions.assertEquals(1, vals.length);
            Assertions.assertEquals(expectedCP1251String, vals[0]);
            Assertions.assertEquals("/context/AssertForwardServlet", request.getRequestURI());
            Assertions.assertEquals("/context", request.getContextPath());
            Assertions.assertEquals("/AssertForwardServlet", request.getServletPath());
            response.setContentType("text/html");
            response.setStatus(SC_OK);
        }
    }

    public static class AssertIncludeServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            Assertions.assertEquals("/context/AssertIncludeServlet", request.getAttribute(INCLUDE_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(INCLUDE_CONTEXT_PATH));
            Assertions.assertEquals("/AssertIncludeServlet", request.getAttribute(INCLUDE_SERVLET_PATH));
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_PATH_INFO));
            Assertions.assertEquals("do=end&do=the", request.getAttribute(INCLUDE_QUERY_STRING));
            List expectedAttributeNames = Arrays.asList(INCLUDE_REQUEST_URI, INCLUDE_CONTEXT_PATH, INCLUDE_SERVLET_PATH, INCLUDE_QUERY_STRING);
            List requestAttributeNames = Collections.list(request.getAttributeNames());
            Assertions.assertTrue(requestAttributeNames.containsAll(expectedAttributeNames));
            Assertions.assertEquals(null, request.getPathInfo());
            Assertions.assertEquals(null, request.getPathTranslated());
            Assertions.assertEquals("do=assertinclude&do=more&test=1", request.getQueryString());
            Assertions.assertEquals("/context/IncludeServlet", request.getRequestURI());
            Assertions.assertEquals("/context", request.getContextPath());
            Assertions.assertEquals("/IncludeServlet", request.getServletPath());
            response.setContentType("text/html");
            response.setStatus(SC_OK);
        }
    }

    public static class AssertForwardIncludeServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // include doesn't hide forward
            Assertions.assertEquals("/context/ForwardServlet/forwardpath", request.getAttribute(FORWARD_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(FORWARD_CONTEXT_PATH));
            Assertions.assertEquals("/ForwardServlet", request.getAttribute(FORWARD_SERVLET_PATH));
            Assertions.assertEquals("/forwardpath", request.getAttribute(FORWARD_PATH_INFO));
            Assertions.assertEquals("do=include", request.getAttribute(FORWARD_QUERY_STRING));
            Assertions.assertEquals("/context/AssertForwardIncludeServlet/assertpath", request.getAttribute(INCLUDE_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(INCLUDE_CONTEXT_PATH));
            Assertions.assertEquals("/AssertForwardIncludeServlet", request.getAttribute(INCLUDE_SERVLET_PATH));
            Assertions.assertEquals("/assertpath", request.getAttribute(INCLUDE_PATH_INFO));
            Assertions.assertEquals("do=end", request.getAttribute(INCLUDE_QUERY_STRING));
            List expectedAttributeNames = Arrays.asList(FORWARD_REQUEST_URI, FORWARD_CONTEXT_PATH, FORWARD_SERVLET_PATH, FORWARD_PATH_INFO, FORWARD_QUERY_STRING, INCLUDE_REQUEST_URI, INCLUDE_CONTEXT_PATH, INCLUDE_SERVLET_PATH, INCLUDE_PATH_INFO, INCLUDE_QUERY_STRING);
            List requestAttributeNames = Collections.list(request.getAttributeNames());
            Assertions.assertTrue(requestAttributeNames.containsAll(expectedAttributeNames));
            Assertions.assertEquals("/includepath", request.getPathInfo());
            Assertions.assertEquals(null, request.getPathTranslated());
            Assertions.assertEquals("do=assertforwardinclude", request.getQueryString());
            Assertions.assertEquals("/context/IncludeServlet/includepath", request.getRequestURI());
            Assertions.assertEquals("/context", request.getContextPath());
            Assertions.assertEquals("/IncludeServlet", request.getServletPath());
            response.setContentType("text/html");
            response.setStatus(SC_OK);
        }
    }

    public static class AssertIncludeForwardServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // forward hides include
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_REQUEST_URI));
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_CONTEXT_PATH));
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_SERVLET_PATH));
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_PATH_INFO));
            Assertions.assertEquals(null, request.getAttribute(INCLUDE_QUERY_STRING));
            Assertions.assertEquals("/context/IncludeServlet/includepath", request.getAttribute(FORWARD_REQUEST_URI));
            Assertions.assertEquals("/context", request.getAttribute(FORWARD_CONTEXT_PATH));
            Assertions.assertEquals("/IncludeServlet", request.getAttribute(FORWARD_SERVLET_PATH));
            Assertions.assertEquals("/includepath", request.getAttribute(FORWARD_PATH_INFO));
            Assertions.assertEquals("do=forward", request.getAttribute(FORWARD_QUERY_STRING));
            List expectedAttributeNames = Arrays.asList(FORWARD_REQUEST_URI, FORWARD_CONTEXT_PATH, FORWARD_SERVLET_PATH, FORWARD_PATH_INFO, FORWARD_QUERY_STRING);
            List requestAttributeNames = Collections.list(request.getAttributeNames());
            Assertions.assertTrue(requestAttributeNames.containsAll(expectedAttributeNames));
            Assertions.assertEquals("/assertpath", request.getPathInfo());
            Assertions.assertEquals(null, request.getPathTranslated());
            Assertions.assertEquals("do=end", request.getQueryString());
            Assertions.assertEquals("/context/AssertIncludeForwardServlet/assertpath", request.getRequestURI());
            Assertions.assertEquals("/context", request.getContextPath());
            Assertions.assertEquals("/AssertIncludeForwardServlet", request.getServletPath());
            response.setContentType("text/html");
            response.setStatus(SC_OK);
        }
    }
}

