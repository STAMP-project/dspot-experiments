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


import LocalConnector.LocalEndPoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.QuietServletException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.DateCache;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class CustomRequestLogTest {
    CustomRequestLog _log;

    Server _server;

    LocalConnector _connector;

    BlockingQueue<String> _entries = new org.eclipse.jetty.util.BlockingArrayQueue();

    BlockingQueue<Long> requestTimes = new org.eclipse.jetty.util.BlockingArrayQueue();

    ServerConnector _serverConnector;

    URI _serverURI;

    private static final long DELAY = 2000;

    @Test
    public void testModifier() throws Exception {
        testHandlerServerStart("%s: %!404,301{Referer}i");
        _connector.getResponse("GET /error404 HTTP/1.0\nReferer: testReferer\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("404: -"));
        _connector.getResponse("GET /error301 HTTP/1.0\nReferer: testReferer\n\n");
        log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("301: -"));
        _connector.getResponse("GET /success HTTP/1.0\nReferer: testReferer\n\n");
        log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("200: testReferer"));
    }

    @Test
    public void testDoublePercent() throws Exception {
        testHandlerServerStart("%%%%%%a");
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("%%%a"));
    }

    @Test
    public void testLogAddress() throws Exception {
        testHandlerServerStart(("%{local}a|%{local}p|" + (("%{remote}a|%{remote}p|" + "%{server}a|%{server}p|") + "%{client}a|%{client}p")));
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = ((NetworkInterface) (e.nextElement()));
            if (n.isLoopback()) {
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = ((InetAddress) (ee.nextElement()));
                    try (Socket client = newSocket(i.getHostAddress(), _serverURI.getPort())) {
                        OutputStream os = client.getOutputStream();
                        String request = "GET / HTTP/1.0\n" + (("Host: webtide.com:1234\n" + "Forwarded: For=10.1.2.3:1337\n") + "\n\n");
                        os.write(request.getBytes(StandardCharsets.ISO_8859_1));
                        os.flush();
                        String[] log = _entries.poll(5, TimeUnit.SECONDS).split("\\|");
                        MatcherAssert.assertThat(log.length, Matchers.is(8));
                        String localAddr = log[0];
                        String localPort = log[1];
                        String remoteAddr = log[2];
                        String remotePort = log[3];
                        String serverAddr = log[4];
                        String serverPort = log[5];
                        String clientAddr = log[6];
                        String clientPort = log[7];
                        MatcherAssert.assertThat(serverPort, Matchers.is("1234"));
                        MatcherAssert.assertThat(clientPort, Matchers.is("1337"));
                        MatcherAssert.assertThat(remotePort, Matchers.not(clientPort));
                        MatcherAssert.assertThat(localPort, Matchers.not(serverPort));
                        MatcherAssert.assertThat(serverAddr, Matchers.is("webtide.com"));
                        MatcherAssert.assertThat(clientAddr, Matchers.is("10.1.2.3"));
                        MatcherAssert.assertThat(InetAddress.getByName(remoteAddr), Matchers.is(client.getInetAddress()));
                        MatcherAssert.assertThat(InetAddress.getByName(localAddr), Matchers.is(i));
                    }
                } 
            }
        } 
    }

    @Test
    public void testLogBytesSent() throws Exception {
        testHandlerServerStart("BytesSent: %O");
        _connector.getResponse("GET / HTTP/1.0\necho: hello world\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("BytesSent: 11"));
    }

    @Test
    public void testLogBytesReceived() throws Exception {
        testHandlerServerStart("BytesReceived: %I");
        _connector.getResponse(("GET / HTTP/1.0\n" + ("Content-Length: 11\n\n" + "hello world")));
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("BytesReceived: 11"));
    }

    @Test
    public void testLogBytesTransferred() throws Exception {
        testHandlerServerStart("BytesTransferred: %S");
        _connector.getResponse(("GET / HTTP/1.0\n" + (("echo: hello world\n" + "Content-Length: 11\n\n") + "hello world")));
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("BytesTransferred: 22"));
    }

    @Test
    public void testLogRequestCookie() throws Exception {
        testHandlerServerStart("RequestCookies: %{cookieName}C, %{cookie2}C, %{cookie3}C");
        _connector.getResponse("GET / HTTP/1.0\nCookie: cookieName=cookieValue; cookie2=value2\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("RequestCookies: cookieValue, value2, -"));
    }

    @Test
    public void testLogRequestCookies() throws Exception {
        testHandlerServerStart("RequestCookies: %C");
        _connector.getResponse("GET / HTTP/1.0\nCookie: cookieName=cookieValue; cookie2=value2\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("RequestCookies: cookieName=cookieValue;cookie2=value2"));
    }

    @Test
    public void testLogEnvironmentVar() throws Exception {
        testHandlerServerStart("EnvironmentVar: %{JAVA_HOME}e");
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        String envVar = System.getenv("JAVA_HOME");
        MatcherAssert.assertThat(log, Matchers.is(("EnvironmentVar: " + (envVar == null ? "-" : envVar))));
    }

    @Test
    public void testLogRequestProtocol() throws Exception {
        testHandlerServerStart("%H");
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("HTTP/1.0"));
    }

    @Test
    public void testLogRequestHeader() throws Exception {
        testHandlerServerStart("RequestHeader: %{Header1}i, %{Header2}i, %{Header3}i");
        _connector.getResponse("GET / HTTP/1.0\nHeader1: value1\nHeader2: value2\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("RequestHeader: value1, value2, -"));
    }

    @Test
    public void testLogKeepAliveRequests() throws Exception {
        testHandlerServerStart("KeepAliveRequests: %k");
        LocalConnector.LocalEndPoint connect = _connector.connect();
        connect.addInput(("GET /a HTTP/1.0\n" + "Connection: keep-alive\n\n"));
        connect.addInput(("GET /a HTTP/1.1\n" + "Host: localhost\n\n"));
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("200 OK"));
        connect.addInput("GET /a HTTP/1.0\n\n");
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("KeepAliveRequests: 1"));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("KeepAliveRequests: 2"));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("KeepAliveRequests: 3"));
    }

    @Test
    public void testLogRequestMethod() throws Exception {
        testHandlerServerStart("RequestMethod: %m");
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("RequestMethod: GET"));
    }

    @Test
    public void testLogResponseHeader() throws Exception {
        testHandlerServerStart("ResponseHeader: %{Header1}o, %{Header2}o, %{Header3}o");
        _connector.getResponse("GET /responseHeaders HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("ResponseHeader: value1, value2, -"));
    }

    @Test
    public void testLogQueryString() throws Exception {
        testHandlerServerStart("QueryString: %q");
        _connector.getResponse("GET /path?queryString HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("QueryString: ?queryString"));
    }

    @Test
    public void testLogRequestFirstLine() throws Exception {
        testHandlerServerStart("RequestFirstLin: %r");
        _connector.getResponse("GET /path?query HTTP/1.0\nHeader: null\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("RequestFirstLin: GET /path?query HTTP/1.0"));
    }

    @Test
    public void testLogResponseStatus() throws Exception {
        testHandlerServerStart("LogResponseStatus: %s");
        _connector.getResponse("GET /error404 HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("LogResponseStatus: 404"));
        _connector.getResponse("GET /error301 HTTP/1.0\n\n");
        log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("LogResponseStatus: 301"));
        _connector.getResponse("GET / HTTP/1.0\n\n");
        log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("LogResponseStatus: 200"));
    }

    @Test
    public void testLogRequestTime() throws Exception {
        testHandlerServerStart("RequestTime: %t");
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        long requestTime = requestTimes.poll(5, TimeUnit.SECONDS);
        DateCache dateCache = new DateCache(_log.DEFAULT_DATE_FORMAT, Locale.getDefault(), "GMT");
        MatcherAssert.assertThat(log, Matchers.is((("RequestTime: [" + (dateCache.format(requestTime))) + "]")));
    }

    @Test
    public void testLogRequestTimeCustomFormats() throws Exception {
        testHandlerServerStart(("%{EEE MMM dd HH:mm:ss zzz yyyy}t\n" + ("%{EEE MMM dd HH:mm:ss zzz yyyy|EST}t\n" + "%{EEE MMM dd HH:mm:ss zzz yyyy|EST|ja}t")));
        _connector.getResponse("GET / HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        long requestTime = requestTimes.poll(5, TimeUnit.SECONDS);
        DateCache dateCache1 = new DateCache("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault(), "GMT");
        DateCache dateCache2 = new DateCache("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault(), "EST");
        DateCache dateCache3 = new DateCache("EEE MMM dd HH:mm:ss zzz yyyy", Locale.forLanguageTag("ja"), "EST");
        String[] logs = log.split("\n");
        MatcherAssert.assertThat(logs[0], Matchers.is((("[" + (dateCache1.format(requestTime))) + "]")));
        MatcherAssert.assertThat(logs[1], Matchers.is((("[" + (dateCache2.format(requestTime))) + "]")));
        MatcherAssert.assertThat(logs[2], Matchers.is((("[" + (dateCache3.format(requestTime))) + "]")));
    }

    @Test
    public void testLogLatencyMicroseconds() throws Exception {
        testHandlerServerStart("%{us}T");
        _connector.getResponse("GET /delay HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        long lowerBound = requestTimes.poll(5, TimeUnit.SECONDS);
        long upperBound = System.currentTimeMillis();
        long measuredDuration = Long.parseLong(log);
        long durationLowerBound = TimeUnit.MILLISECONDS.toMicros(CustomRequestLogTest.DELAY);
        long durationUpperBound = TimeUnit.MILLISECONDS.toMicros((upperBound - lowerBound));
        MatcherAssert.assertThat(measuredDuration, Matchers.greaterThanOrEqualTo(durationLowerBound));
        MatcherAssert.assertThat(measuredDuration, Matchers.lessThanOrEqualTo(durationUpperBound));
    }

    @Test
    public void testLogLatencyMilliseconds() throws Exception {
        testHandlerServerStart("%{ms}T");
        _connector.getResponse("GET /delay HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        long lowerBound = requestTimes.poll(5, TimeUnit.SECONDS);
        long upperBound = System.currentTimeMillis();
        long measuredDuration = Long.parseLong(log);
        long durationLowerBound = CustomRequestLogTest.DELAY;
        long durationUpperBound = upperBound - lowerBound;
        MatcherAssert.assertThat(measuredDuration, Matchers.greaterThanOrEqualTo(durationLowerBound));
        MatcherAssert.assertThat(measuredDuration, Matchers.lessThanOrEqualTo(durationUpperBound));
    }

    @Test
    public void testLogLatencySeconds() throws Exception {
        testHandlerServerStart("%{s}T");
        _connector.getResponse("GET /delay HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        long lowerBound = requestTimes.poll(5, TimeUnit.SECONDS);
        long upperBound = System.currentTimeMillis();
        long measuredDuration = Long.parseLong(log);
        long durationLowerBound = TimeUnit.MILLISECONDS.toSeconds(CustomRequestLogTest.DELAY);
        long durationUpperBound = TimeUnit.MILLISECONDS.toSeconds((upperBound - lowerBound));
        MatcherAssert.assertThat(measuredDuration, Matchers.greaterThanOrEqualTo(durationLowerBound));
        MatcherAssert.assertThat(measuredDuration, Matchers.lessThanOrEqualTo(durationUpperBound));
    }

    @Test
    public void testLogUrlRequestPath() throws Exception {
        testHandlerServerStart("UrlRequestPath: %U");
        _connector.getResponse("GET /path?query HTTP/1.0\n\n");
        String log = _entries.poll(5, TimeUnit.SECONDS);
        MatcherAssert.assertThat(log, Matchers.is("UrlRequestPath: /path"));
    }

    @Test
    public void testLogConnectionStatus() throws Exception {
        testHandlerServerStart("%U ConnectionStatus: %s %X");
        _connector.getResponse("GET /one HTTP/1.0\n\n");
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/one ConnectionStatus: 200 -"));
        _connector.getResponse(("GET /two HTTP/1.1\n" + (("Host: localhost\n" + "Connection: close\n") + "\n")));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/two ConnectionStatus: 200 -"));
        LocalConnector.LocalEndPoint connect = _connector.connect();
        connect.addInput(("GET /three HTTP/1.0\n" + "Connection: keep-alive\n\n"));
        connect.addInput(("GET /four HTTP/1.1\n" + "Host: localhost\n\n"));
        connect.addInput("GET /BAD HTTP/1.1\n\n");
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(connect.getResponse(), Matchers.containsString("400 "));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/three ConnectionStatus: 200 +"));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/four ConnectionStatus: 200 +"));
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/BAD ConnectionStatus: 400 -"));
        _connector.getResponse(("GET /abort HTTP/1.1\n" + ("Host: localhost\n" + "\n")));
        connect.getResponse();
        MatcherAssert.assertThat(_entries.poll(5, TimeUnit.SECONDS), Matchers.is("/abort ConnectionStatus: 200 X"));
    }

    class TestRequestLogWriter implements RequestLog.Writer {
        @Override
        public void write(String requestEntry) {
            try {
                _entries.add(requestEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (request.getRequestURI().contains("error404")) {
                response.setStatus(404);
            } else
                if (request.getRequestURI().contains("error301")) {
                    response.setStatus(301);
                } else
                    if ((request.getHeader("echo")) != null) {
                        ServletOutputStream outputStream = response.getOutputStream();
                        outputStream.print(request.getHeader("echo"));
                    } else
                        if (request.getRequestURI().contains("responseHeaders")) {
                            response.addHeader("Header1", "value1");
                            response.addHeader("Header2", "value2");
                        } else
                            if (request.getRequestURI().contains("/abort")) {
                                response.getOutputStream().println("data");
                                response.flushBuffer();
                                baseRequest.getHttpChannel().abort(new QuietServletException("test abort"));
                            } else
                                if (request.getRequestURI().contains("delay")) {
                                    try {
                                        Thread.sleep(CustomRequestLogTest.DELAY);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }





            requestTimes.offer(baseRequest.getTimeStamp());
            baseRequest.setHandled(true);
            if ((request.getContentLength()) > 0) {
                InputStream in = request.getInputStream();
                while ((in.read()) > 0);
            }
        }
    }
}

