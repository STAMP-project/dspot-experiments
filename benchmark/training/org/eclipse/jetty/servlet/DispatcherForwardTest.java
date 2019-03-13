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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


// TODO: add multipart tests
@SuppressWarnings("serial")
public class DispatcherForwardTest {
    private Server server;

    private LocalConnector connector;

    private HttpServlet servlet1;

    private HttpServlet servlet2;

    private List<Throwable> failures = new ArrayList<>();

    @Test
    public void testQueryRetainedByForwardWithoutQuery() throws Exception {
        // 1. request /one?a=1%20one
        // 1. forward /two
        // 2. assert query => a=1 one
        // 1. assert query => a=1 one
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        servlet1 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher("/two").forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
            }
        };
        prepare();
        String request = ((((("" + "GET /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n";
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(response.startsWith("HTTP/1.1 200"), response);
    }

    @Test
    public void testQueryReplacedByForwardWithQuery() throws Exception {
        // 1. request /one?a=1
        // 1. forward /two?a=2
        // 2. assert query => a=2
        // 1. assert query => a=1
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one&b=2%20two";
        final String query2 = "a=3%20three";
        final String query3 = "a=3%20three&b=2%20two";
        servlet1 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query3));
                checkThat(req.getParameter("a"), Matchers.equalTo("3 three"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
            }
        };
        prepare();
        String request = ((((("" + "GET /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n";
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(response.startsWith("HTTP/1.1 200"), response);
    }

    @Test
    public void testQueryMergedByForwardWithQuery() throws Exception {
        // 1. request /one?a=1
        // 1. forward /two?b=2
        // 2. assert query => a=1&b=2
        // 1. assert query => a=1
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String query2 = "b=2%20two";
        final String query3 = "b=2%20two&a=1%20one";
        servlet1 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query3));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
            }
        };
        prepare();
        String request = ((((("" + "GET /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n";
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(response.startsWith("HTTP/1.1 200"), response);
    }

    @Test
    public void testQueryAggregatesWithFormByForwardWithoutQuery() throws Exception {
        // 1. request /one?a=1 + content a=2
        // 1. forward /two
        // 2. assert query => a=1 + params => a=1,2
        // 1. assert query => a=1 + params => a=1,2
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String form = "a=2%20two";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher("/two").forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                String[] values = req.getParameterValues("a");
                checkThat(values, Matchers.notNullValue());
                checkThat(2, Matchers.equalTo(values.length));
                checkThat(values, Matchers.arrayContainingInAnyOrder("1 one", "2 two"));
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                String[] values = req.getParameterValues("a");
                checkThat(values, Matchers.notNullValue());
                checkThat(2, Matchers.equalTo(values.length));
                checkThat(values, Matchers.arrayContainingInAnyOrder("1 one", "2 two"));
            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }

    @Test
    public void testQueryAggregatesWithFormReplacedByForwardWithQuery() throws Exception {
        // 1. request /one?a=1 + content a=2
        // 1. forward /two?a=3
        // 2. assert query => a=3 + params => a=3,2,1
        // 1. assert query => a=1 + params => a=1,2
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String query2 = "a=3%20three";
        final String form = "a=2%20two";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                String[] values = req.getParameterValues("a");
                checkThat(values, Matchers.notNullValue());
                checkThat(2, Matchers.equalTo(values.length));
                checkThat(values, Matchers.arrayContainingInAnyOrder("1 one", "2 two"));
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query2));
                String[] values = req.getParameterValues("a");
                checkThat(values, Matchers.notNullValue());
                checkThat(3, Matchers.equalTo(values.length));
                checkThat(values, Matchers.arrayContainingInAnyOrder("3 three", "2 two", "1 one"));
            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }

    @Test
    public void testQueryAggregatesWithFormMergedByForwardWithQuery() throws Exception {
        // 1. request /one?a=1 + content b=2
        // 1. forward /two?c=3
        // 2. assert query => a=1&c=3 + params => a=1&b=2&c=3
        // 1. assert query => a=1 + params => a=1&b=2
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String query2 = "c=3%20three";
        final String query3 = "c=3%20three&a=1%20one";
        final String form = "b=2%20two";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                checkThat(req.getParameter("c"), Matchers.nullValue());
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query3));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                checkThat(req.getParameter("c"), Matchers.equalTo("3 three"));
            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }

    @Test
    public void testQueryAggregatesWithFormBeforeAndAfterForward() throws Exception {
        // 1. request /one?a=1 + content b=2
        // 1. assert params => a=1&b=2
        // 1. forward /two?c=3
        // 2. assert query => a=1&c=3 + params => a=1&b=2&c=3
        // 1. assert query => a=1 + params => a=1&b=2
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String query2 = "c=3%20three";
        final String query3 = "c=3%20three&a=1%20one";
        final String form = "b=2%20two";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                checkThat(req.getParameter("c"), Matchers.nullValue());
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query3));
                checkThat(req.getParameter("a"), Matchers.equalTo("1 one"));
                checkThat(req.getParameter("b"), Matchers.equalTo("2 two"));
                checkThat(req.getParameter("c"), Matchers.equalTo("3 three"));
            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }

    @Test
    public void testContentCanBeReadViaInputStreamAfterForwardWithoutQuery() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String form = "c=3%20three";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher("/two").forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("c"), Matchers.nullValue());
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                ServletInputStream input = req.getInputStream();
                for (int i = 0; i < (form.length()); ++i)
                    checkThat(((form.charAt(i)) & 65535), Matchers.equalTo(input.read()));

            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }

    @Test
    public void testContentCanBeReadViaInputStreamAfterForwardWithQuery() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final String query1 = "a=1%20one";
        final String query2 = "b=2%20two";
        final String query3 = "b=2%20two&a=1%20one";
        final String form = "c=3%20three";
        servlet1 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                req.getRequestDispatcher(("/two?" + query2)).forward(req, resp);
                checkThat(req.getQueryString(), Matchers.equalTo(query1));
                checkThat(req.getParameter("c"), Matchers.nullValue());
                latch.countDown();
            }
        };
        servlet2 = new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                checkThat(req.getQueryString(), Matchers.equalTo(query3));
                ServletInputStream input = req.getInputStream();
                for (int i = 0; i < (form.length()); ++i)
                    checkThat(((form.charAt(i)) & 65535), Matchers.equalTo(input.read()));

                checkThat((-1), Matchers.equalTo(input.read()));
            }
        };
        prepare();
        String request = (((((((((("" + "POST /one?") + query1) + " HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: application/x-www-form-urlencoded\r\n") + "Content-Length: ") + (form.length())) + "\r\n") + "Connection: close\r\n") + "\r\n") + form;
        String response = connector.getResponse(request);
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, StringStartsWith.startsWith("HTTP/1.1 200"));
    }
}

