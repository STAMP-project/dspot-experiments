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


import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.ServletTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public abstract class AbstractDoSFilterTest {
    protected ServletTester _tester;

    protected String _host;

    protected int _port;

    protected long _requestMaxTime = 200;

    @Test
    public void testEvenLowRateIP() throws Exception {
        String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String last = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests(request, 11, 300, 300, last);
        Assertions.assertEquals(12, count(responses, "HTTP/1.1 200 OK"));
        Assertions.assertEquals(0, count(responses, "DoSFilter:"));
    }

    @Test
    public void testBurstLowRateIP() throws Exception {
        String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String last = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests((((request + request) + request) + request), 2, 1100, 1100, last);
        Assertions.assertEquals(9, count(responses, "HTTP/1.1 200 OK"));
        Assertions.assertEquals(0, count(responses, "DoSFilter:"));
    }

    @Test
    public void testDelayedIP() throws Exception {
        String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String last = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests(((((request + request) + request) + request) + request), 2, 1100, 1100, last);
        MatcherAssert.assertThat(count(responses, "DoSFilter: delayed"), Matchers.greaterThanOrEqualTo(2));
        MatcherAssert.assertThat(count(responses, "HTTP/1.1 200 OK"), Matchers.is(11));
    }

    @Test
    public void testThrottledIP() throws Exception {
        Thread other = new Thread() {
            @Override
            public void run() {
                try {
                    // Cause a delay, then sleep while holding pass
                    String request = "GET /ctx/dos/sleeper HTTP/1.1\r\nHost: localhost\r\n\r\n";
                    String last = "GET /ctx/dos/sleeper?sleep=2000 HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
                    doRequests((((request + request) + request) + request), 1, 0, 0, last);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        other.start();
        Thread.sleep(1500);
        String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String last = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests((((request + request) + request) + request), 1, 0, 0, last);
        // System.out.println("responses are " + responses);
        Assertions.assertEquals(5, count(responses, "HTTP/1.1 200 OK"), "200 OK responses");
        Assertions.assertEquals(1, count(responses, "DoSFilter: delayed"), "delayed responses");
        Assertions.assertEquals(1, count(responses, "DoSFilter: throttled"), "throttled responses");
        Assertions.assertEquals(0, count(responses, "DoSFilter: unavailable"), "unavailable responses");
        other.join();
    }

    @Test
    public void testUnavailableIP() throws Exception {
        Thread other = new Thread() {
            @Override
            public void run() {
                try {
                    // Cause a delay, then sleep while holding pass
                    String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
                    String last = "GET /ctx/dos/test?sleep=5000 HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
                    doRequests((((request + request) + request) + request), 1, 0, 0, last);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        other.start();
        Thread.sleep(500);
        String request = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String last = "GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests((((request + request) + request) + request), 1, 0, 0, last);
        // System.err.println("RESPONSES: \n"+responses);
        Assertions.assertEquals(4, count(responses, "HTTP/1.1 200 OK"));
        Assertions.assertEquals(1, count(responses, "HTTP/1.1 429"));
        Assertions.assertEquals(1, count(responses, "DoSFilter: delayed"));
        Assertions.assertEquals(1, count(responses, "DoSFilter: throttled"));
        Assertions.assertEquals(1, count(responses, "DoSFilter: unavailable"));
        other.join();
    }

    @Test
    public void testSessionTracking() throws Exception {
        // get a session, first
        String requestSession = "GET /ctx/dos/test?session=true HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String response = doRequests("", 1, 0, 0, requestSession);
        String sessionId = response.substring(((response.indexOf("Set-Cookie: ")) + 12), response.indexOf(";"));
        // all other requests use this session
        String request = ("GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nCookie: " + sessionId) + "\r\n\r\n";
        String last = ("GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\nCookie: " + sessionId) + "\r\n\r\n";
        String responses = doRequests(((((request + request) + request) + request) + request), 2, 1100, 1100, last);
        Assertions.assertEquals(11, count(responses, "HTTP/1.1 200 OK"));
        Assertions.assertEquals(2, count(responses, "DoSFilter: delayed"));
    }

    @Test
    public void testMultipleSessionTracking() throws Exception {
        // get some session ids, first
        String requestSession = "GET /ctx/dos/test?session=true HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String closeRequest = "GET /ctx/dos/test?session=true HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String response = doRequests((requestSession + requestSession), 1, 0, 0, closeRequest);
        String[] sessions = response.split("\r\n\r\n");
        String sessionId1 = sessions[0].substring(((sessions[0].indexOf("Set-Cookie: ")) + 12), sessions[0].indexOf(";"));
        String sessionId2 = sessions[1].substring(((sessions[1].indexOf("Set-Cookie: ")) + 12), sessions[1].indexOf(";"));
        // alternate between sessions
        String request1 = ("GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nCookie: " + sessionId1) + "\r\n\r\n";
        String request2 = ("GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nCookie: " + sessionId2) + "\r\n\r\n";
        String last = ("GET /ctx/dos/test HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\nCookie: " + sessionId2) + "\r\n\r\n";
        // ensure the sessions are new
        doRequests((request1 + request2), 1, 1100, 1100, last);
        Thread.sleep(1000);
        String responses = doRequests(((((request1 + request2) + request1) + request2) + request1), 2, 1100, 1100, last);
        Assertions.assertEquals(11, count(responses, "HTTP/1.1 200 OK"));
        Assertions.assertEquals(0, count(responses, "DoSFilter: delayed"));
        // alternate between sessions
        responses = doRequests(((((request1 + request2) + request1) + request2) + request1), 2, 250, 250, last);
        // System.err.println(responses);
        Assertions.assertEquals(11, count(responses, "HTTP/1.1 200 OK"));
        int delayedRequests = count(responses, "DoSFilter: delayed");
        Assertions.assertTrue(((delayedRequests >= 2) && (delayedRequests <= 5)), (("delayedRequests: " + delayedRequests) + " is not between 2 and 5"));
    }

    @Test
    public void testUnresponsiveClient() throws Exception {
        int numRequests = 1000;
        String last = ("GET /ctx/timeout/unresponsive?lines=" + numRequests) + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests("", 0, 0, 0, last);
        // was expired, and stopped before reaching the end of the requests
        int responseLines = count(responses, "Line:");
        MatcherAssert.assertThat(responseLines, Matchers.greaterThan(0));
        MatcherAssert.assertThat(responseLines, Matchers.lessThan(numRequests));
    }

    public static class TestServlet extends HttpServlet implements Servlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if ((request.getParameter("session")) != null)
                request.getSession(true);

            if ((request.getParameter("sleep")) != null) {
                try {
                    Thread.sleep(Long.parseLong(request.getParameter("sleep")));
                } catch (InterruptedException e) {
                }
            }
            if ((request.getParameter("lines")) != null) {
                int count = Integer.parseInt(request.getParameter("lines"));
                for (int i = 0; i < count; ++i) {
                    response.getWriter().append((("Line: " + i) + "\n"));
                    response.flushBuffer();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
            response.setContentType("text/plain");
        }
    }
}

