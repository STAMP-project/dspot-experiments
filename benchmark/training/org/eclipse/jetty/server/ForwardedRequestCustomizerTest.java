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
package org.eclipse.jetty.server;


import MimeTypes.Type.FORM_ENCODED;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ForwardedRequestCustomizerTest {
    private Server _server;

    private LocalConnector _connector;

    private ForwardedRequestCustomizerTest.RequestHandler _handler;

    final Deque<String> _results = new ArrayDeque<>();

    final AtomicBoolean _wasSecure = new AtomicBoolean(false);

    final AtomicReference<String> _sslSession = new AtomicReference<>();

    final AtomicReference<String> _sslCertificate = new AtomicReference<>();

    ForwardedRequestCustomizer _customizer;

    @Test
    public void testRFC7239_Examples_4() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + ((((("Host: myhost\n" + "Forwarded: for=\"_gazonk\"\n") + "Forwarded: For=\"[2001:db8:cafe::17]:4711\"\n") + "Forwarded: for=192.0.2.60;proto=http;by=203.0.113.43\n") + "Forwarded: for=192.0.2.43, for=198.51.100.17\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("[2001:db8:cafe::17]", _results.poll());
        Assertions.assertEquals("4711", _results.poll());
    }

    @Test
    public void testRFC7239_Examples_7_1() throws Exception {
        _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Forwarded: for=192.0.2.43,for=\"[2001:db8:cafe::17]\",for=unknown\n") + "\n")));
        _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Forwarded: for=192.0.2.43, for=\"[2001:db8:cafe::17]\", for=unknown\n") + "\n")));
        _connector.getResponse(("GET / HTTP/1.1\n" + ((("Host: myhost\n" + "Forwarded: for=192.0.2.43\n") + "Forwarded: for=\"[2001:db8:cafe::17]\", for=unknown\n") + "\n")));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("192.0.2.43", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("192.0.2.43", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("192.0.2.43", _results.poll());
        Assertions.assertEquals("0", _results.poll());
    }

    @Test
    public void testRFC7239_Examples_7_4() throws Exception {
        _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Forwarded: for=192.0.2.43, for=\"[2001:db8:cafe::17]\"\n") + "\n")));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("192.0.2.43", _results.poll());
        Assertions.assertEquals("0", _results.poll());
    }

    @Test
    public void testRFC7239_Examples_7_5() throws Exception {
        _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Forwarded: for=192.0.2.43,for=198.51.100.17;by=203.0.113.60;proto=http;host=example.com\n") + "\n")));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("example.com", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("192.0.2.43", _results.poll());
        Assertions.assertEquals("0", _results.poll());
    }

    @Test
    public void testProto() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + ((("Host: myhost\n" + "X-Forwarded-Proto: foobar\n") + "Forwarded: proto=https\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("https", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("443", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
    }

    @Test
    public void testFor() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "X-Forwarded-For: 10.9.8.7,6.5.4.3\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("10.9.8.7", _results.poll());
        Assertions.assertEquals("0", _results.poll());
    }

    @Test
    public void testForIpv4WithPort() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "X-Forwarded-For: 10.9.8.7:1111,6.5.4.3:2222\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("10.9.8.7", _results.poll());
        Assertions.assertEquals("1111", _results.poll());
    }

    @Test
    public void testForIpv6WithPort() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "X-Forwarded-For: [2001:db8:cafe::17]:1111,6.5.4.3:2222\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("[2001:db8:cafe::17]", _results.poll());
        Assertions.assertEquals("1111", _results.poll());
    }

    @Test
    public void testLegacyProto() throws Exception {
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "X-Proxied-Https: on\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("https", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("443", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertTrue(_wasSecure.get());
    }

    @Test
    public void testSslSession() throws Exception {
        _customizer.setSslIsSecure(false);
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Proxy-Ssl-Id: Wibble\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertFalse(_wasSecure.get());
        Assertions.assertEquals("Wibble", _sslSession.get());
        _customizer.setSslIsSecure(true);
        response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Proxy-Ssl-Id: 0123456789abcdef\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("https", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("443", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertTrue(_wasSecure.get());
        Assertions.assertEquals("0123456789abcdef", _sslSession.get());
    }

    @Test
    public void testSslCertificate() throws Exception {
        _customizer.setSslIsSecure(false);
        String response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Proxy-auth-cert: Wibble\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("http", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("80", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertFalse(_wasSecure.get());
        Assertions.assertEquals("Wibble", _sslCertificate.get());
        _customizer.setSslIsSecure(true);
        response = _connector.getResponse(("GET / HTTP/1.1\n" + (("Host: myhost\n" + "Proxy-auth-cert: 0123456789abcdef\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        Assertions.assertEquals("https", _results.poll());
        Assertions.assertEquals("myhost", _results.poll());
        Assertions.assertEquals("443", _results.poll());
        Assertions.assertEquals("0.0.0.0", _results.poll());
        Assertions.assertEquals("0", _results.poll());
        Assertions.assertTrue(_wasSecure.get());
        Assertions.assertEquals("0123456789abcdef", _sslCertificate.get());
    }

    interface RequestTester {
        boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }

    private class RequestHandler extends AbstractHandler {
        private ForwardedRequestCustomizerTest.RequestTester _checker;

        @SuppressWarnings("unused")
        private String _content;

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            setHandled(true);
            if ((((request.getContentLength()) > 0) && (!(FORM_ENCODED.asString().equals(request.getContentType())))) && (!(request.getContentType().startsWith("multipart/form-data"))))
                _content = IO.toString(request.getInputStream());

            if (((_checker) != null) && (_checker.check(request, response)))
                response.setStatus(200);
            else
                response.sendError(500);

        }
    }
}

