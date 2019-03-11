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
package org.eclipse.jetty.http;


import HttpHeader.HOST;
import HttpVersion.HTTP_1_0;
import HttpVersion.HTTP_1_1;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class HttpTesterTest {
    @Test
    public void testGetRequestBuffer10() {
        HttpTester.Request request = HttpTester.parseRequest(("GET /uri HTTP/1.0\r\n" + ((((("Host: localhost\r\n" + "Connection: keep-alive\r\n") + "\r\n") + "GET /some/other/request /HTTP/1.0\r\n") + "Host: localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(request.getMethod(), Matchers.is("GET"));
        MatcherAssert.assertThat(request.getUri(), Matchers.is("/uri"));
        MatcherAssert.assertThat(request.getVersion(), Matchers.is(HTTP_1_0));
        MatcherAssert.assertThat(request.get(HOST), Matchers.is("localhost"));
        MatcherAssert.assertThat(request.getContent(), Matchers.is(""));
    }

    @Test
    public void testGetRequestBuffer11() {
        HttpTester.Request request = HttpTester.parseRequest(("GET /uri HTTP/1.1\r\n" + (((("Host: localhost\r\n" + "\r\n") + "GET /some/other/request /HTTP/1.1\r\n") + "Host: localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(request.getMethod(), Matchers.is("GET"));
        MatcherAssert.assertThat(request.getUri(), Matchers.is("/uri"));
        MatcherAssert.assertThat(request.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(request.get(HOST), Matchers.is("localhost"));
        MatcherAssert.assertThat(request.getContent(), Matchers.is(""));
    }

    @Test
    public void testPostRequestBuffer10() {
        HttpTester.Request request = HttpTester.parseRequest(("POST /uri HTTP/1.0\r\n" + (((((((("Host: localhost\r\n" + "Connection: keep-alive\r\n") + "Content-Length: 16\r\n") + "\r\n") + "0123456789ABCDEF") + "\r\n") + "GET /some/other/request /HTTP/1.0\r\n") + "Host: localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(request.getMethod(), Matchers.is("POST"));
        MatcherAssert.assertThat(request.getUri(), Matchers.is("/uri"));
        MatcherAssert.assertThat(request.getVersion(), Matchers.is(HTTP_1_0));
        MatcherAssert.assertThat(request.get(HOST), Matchers.is("localhost"));
        MatcherAssert.assertThat(request.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testPostRequestBuffer11() {
        HttpTester.Request request = HttpTester.parseRequest(("POST /uri HTTP/1.1\r\n" + ((((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "6\r\n") + "ABCDEF\r\n") + "0\r\n") + "\r\n") + "GET /some/other/request /HTTP/1.1\r\n") + "Host: localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(request.getMethod(), Matchers.is("POST"));
        MatcherAssert.assertThat(request.getUri(), Matchers.is("/uri"));
        MatcherAssert.assertThat(request.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(request.get(HOST), Matchers.is("localhost"));
        MatcherAssert.assertThat(request.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testResponseEOFBuffer() {
        HttpTester.Response response = HttpTester.parseResponse(("HTTP/1.1 200 OK\r\n" + ((("Header: value\r\n" + "Connection: close\r\n") + "\r\n") + "0123456789ABCDEF")));
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Header"), Matchers.is("value"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testResponseLengthBuffer() {
        HttpTester.Response response = HttpTester.parseResponse(("HTTP/1.1 200 OK\r\n" + ((((("Header: value\r\n" + "Content-Length: 16\r\n") + "\r\n") + "0123456789ABCDEF") + "HTTP/1.1 200 OK\r\n") + "\r\n")));
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Header"), Matchers.is("value"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testResponseChunkedBuffer() {
        HttpTester.Response response = HttpTester.parseResponse(("HTTP/1.1 200 OK\r\n" + (((((((((("Header: value\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "6\r\n") + "ABCDEF\r\n") + "0\r\n") + "\r\n") + "HTTP/1.1 200 OK\r\n") + "\r\n")));
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Header"), Matchers.is("value"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testResponsesInput() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream(("HTTP/1.1 200 OK\r\n" + ((((((((((((("Header: value\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "6\r\n") + "ABCDEF\r\n") + "0\r\n") + "\r\n") + "HTTP/1.1 400 OK\r\n") + "Next: response\r\n") + "Content-Length: 16\r\n") + "\r\n") + "0123456789ABCDEF")).getBytes(StandardCharsets.ISO_8859_1));
        HttpTester.Input in = HttpTester.from(stream);
        HttpTester.Response response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Header"), Matchers.is("value"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
        response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(400));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Next"), Matchers.is("response"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
    }

    @Test
    public void testResponsesSplitInput() throws Exception {
        PipedOutputStream src = new PipedOutputStream();
        PipedInputStream stream = new PipedInputStream(src) {
            @Override
            public synchronized int read(byte[] b, int off, int len) throws IOException {
                if ((available()) == 0)
                    return 0;

                return super.read(b, off, len);
            }
        };
        HttpTester.Input in = HttpTester.from(stream);
        src.write(("HTTP/1.1 200 OK\r\n" + (((((("Header: value\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "6\r\n") + "ABC")).getBytes(StandardCharsets.ISO_8859_1));
        HttpTester.Response response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response, Matchers.nullValue());
        src.write(("DEF\r\n" + (((((("0\r\n" + "\r\n") + "HTTP/1.1 400 OK\r\n") + "Next: response\r\n") + "Content-Length: 16\r\n") + "\r\n") + "0123456789")).getBytes(StandardCharsets.ISO_8859_1));
        response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Header"), Matchers.is("value"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
        response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response, Matchers.nullValue());
        src.write("ABCDEF".getBytes(StandardCharsets.ISO_8859_1));
        response = HttpTester.parseResponse(in);
        MatcherAssert.assertThat(response.getVersion(), Matchers.is(HTTP_1_1));
        MatcherAssert.assertThat(response.getStatus(), Matchers.is(400));
        MatcherAssert.assertThat(response.getReason(), Matchers.is("OK"));
        MatcherAssert.assertThat(get("Next"), Matchers.is("response"));
        MatcherAssert.assertThat(response.getContent(), Matchers.is("0123456789ABCDEF"));
    }
}

