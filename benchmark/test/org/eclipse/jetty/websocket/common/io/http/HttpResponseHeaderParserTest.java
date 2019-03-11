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
package org.eclipse.jetty.websocket.common.io.http;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class HttpResponseHeaderParserTest {
    @Test
    public void testParseNotFound() {
        StringBuilder resp = new StringBuilder();
        resp.append("HTTP/1.1 404 Not Found\r\n");
        resp.append("Date: Fri, 26 Apr 2013 21:43:08 GMT\r\n");
        resp.append("Content-Type: text/html; charset=ISO-8859-1\r\n");
        resp.append("Cache-Control: must-revalidate,no-cache,no-store\r\n");
        resp.append("Content-Length: 38\r\n");
        resp.append("Server: Jetty(9.0.0.v20130308)\r\n");
        resp.append("\r\n");
        // and some body content
        resp.append("What you are looking for is not here\r\n");
        ByteBuffer buf = BufferUtil.toBuffer(resp.toString(), StandardCharsets.UTF_8);
        HttpResponseParseCapture capture = new HttpResponseParseCapture();
        HttpResponseHeaderParser parser = new HttpResponseHeaderParser(capture);
        MatcherAssert.assertThat("Parser.parse", parser.parse(buf), Matchers.notNullValue());
        MatcherAssert.assertThat("Response.statusCode", capture.getStatusCode(), Matchers.is(404));
        MatcherAssert.assertThat("Response.statusReason", capture.getStatusReason(), Matchers.is("Not Found"));
        MatcherAssert.assertThat("Response.headers[Content-Length]", capture.getHeader("Content-Length"), Matchers.is("38"));
        MatcherAssert.assertThat("Response.remainingBuffer", capture.getRemainingBuffer().remaining(), Matchers.is(38));
    }

    @Test
    public void testParseRealWorldResponse() {
        // Arbitrary Http Response Headers seen in the wild.
        // Request URI -> https://ssl.google-analytics.com/__utm.gif
        List<String> expected = new ArrayList<>();
        expected.add("HTTP/1.0 200 OK");
        expected.add("Date: Thu, 09 Aug 2012 16:16:39 GMT");
        expected.add("Content-Length: 35");
        expected.add("X-Content-Type-Options: nosniff");
        expected.add("Pragma: no-cache");
        expected.add("Expires: Wed, 19 Apr 2000 11:43:00 GMT");
        expected.add("Last-Modified: Wed, 21 Jan 2004 19:51:30 GMT");
        expected.add("Content-Type: image/gif");
        expected.add("Cache-Control: private, no-cache, no-cache=Set-Cookie, proxy-revalidate");
        expected.add("Age: 518097");
        expected.add("Server: GFE/2.0");
        expected.add("Connection: Keep-Alive");
        expected.add("");
        // Prepare Buffer
        ByteBuffer buf = ByteBuffer.allocate(512);
        for (String line : expected) {
            appendUtf8(buf, (line + "\r\n"));
        }
        BufferUtil.flipToFlush(buf, 0);
        // Parse Buffer
        HttpResponseParseCapture capture = new HttpResponseParseCapture();
        HttpResponseHeaderParser parser = new HttpResponseHeaderParser(capture);
        MatcherAssert.assertThat("Parser.parse", parser.parse(buf), Matchers.notNullValue());
        MatcherAssert.assertThat("Response.statusCode", capture.getStatusCode(), Matchers.is(200));
        MatcherAssert.assertThat("Response.statusReason", capture.getStatusReason(), Matchers.is("OK"));
        MatcherAssert.assertThat("Response.header[age]", capture.getHeader("age"), Matchers.is("518097"));
    }

    @Test
    public void testParseRealWorldResponse_SmallBuffers() {
        // Arbitrary Http Response Headers seen in the wild.
        // Request URI -> https://ssl.google-analytics.com/__utm.gif
        List<String> expected = new ArrayList<>();
        expected.add("HTTP/1.0 200 OK");
        expected.add("Date: Thu, 09 Aug 2012 16:16:39 GMT");
        expected.add("Content-Length: 35");
        expected.add("X-Content-Type-Options: nosniff");
        expected.add("Pragma: no-cache");
        expected.add("Expires: Wed, 19 Apr 2000 11:43:00 GMT");
        expected.add("Last-Modified: Wed, 21 Jan 2004 19:51:30 GMT");
        expected.add("Content-Type: image/gif");
        expected.add("Cache-Control: private, no-cache, no-cache=Set-Cookie, proxy-revalidate");
        expected.add("Age: 518097");
        expected.add("Server: GFE/2.0");
        expected.add("Connection: Keep-Alive");
        expected.add("");
        // Prepare Buffer
        ByteBuffer buf = ByteBuffer.allocate(512);
        for (String line : expected) {
            appendUtf8(buf, (line + "\r\n"));
        }
        BufferUtil.flipToFlush(buf, 0);
        // Prepare small buffers to simulate a slow read/fill/parse from the network
        ByteBuffer small1 = buf.slice();
        ByteBuffer small2 = buf.slice();
        ByteBuffer small3 = buf.slice();
        small1.limit(50);
        small2.position(50);
        small2.limit(70);
        small3.position(70);
        // Parse Buffer
        HttpResponseParseCapture capture = new HttpResponseParseCapture();
        HttpResponseHeaderParser parser = new HttpResponseHeaderParser(capture);
        MatcherAssert.assertThat("Parser.parse", parser.parse(buf), Matchers.notNullValue());
        // Parse small 1
        MatcherAssert.assertThat("Small 1", parser.parse(small1), Matchers.nullValue());
        // Parse small 2
        MatcherAssert.assertThat("Small 2", parser.parse(small2), Matchers.nullValue());
        // Parse small 3
        MatcherAssert.assertThat("Small 3", parser.parse(small3), Matchers.notNullValue());
        MatcherAssert.assertThat("Response.statusCode", capture.getStatusCode(), Matchers.is(200));
        MatcherAssert.assertThat("Response.statusReason", capture.getStatusReason(), Matchers.is("OK"));
        MatcherAssert.assertThat("Response.header[age]", capture.getHeader("age"), Matchers.is("518097"));
    }

    @Test
    public void testParseUpgrade() {
        // Example from RFC6455 - Section 1.2 (Protocol Overview)
        StringBuilder resp = new StringBuilder();
        resp.append("HTTP/1.1 101 Switching Protocols\r\n");
        resp.append("Upgrade: websocket\r\n");
        resp.append("Connection: Upgrade\r\n");
        resp.append("Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=\r\n");
        resp.append("Sec-WebSocket-Protocol: chat\r\n");
        resp.append("\r\n");
        ByteBuffer buf = BufferUtil.toBuffer(resp.toString(), StandardCharsets.UTF_8);
        HttpResponseParseCapture capture = new HttpResponseParseCapture();
        HttpResponseHeaderParser parser = new HttpResponseHeaderParser(capture);
        MatcherAssert.assertThat("Parser.parse", parser.parse(buf), Matchers.notNullValue());
        MatcherAssert.assertThat("Response.statusCode", capture.getStatusCode(), Matchers.is(101));
        MatcherAssert.assertThat("Response.statusReason", capture.getStatusReason(), Matchers.is("Switching Protocols"));
        MatcherAssert.assertThat("Response.headers[Upgrade]", capture.getHeader("Upgrade"), Matchers.is("websocket"));
        MatcherAssert.assertThat("Response.headers[Connection]", capture.getHeader("Connection"), Matchers.is("Upgrade"));
        MatcherAssert.assertThat("Buffer.remaining", buf.remaining(), Matchers.is(0));
    }
}

