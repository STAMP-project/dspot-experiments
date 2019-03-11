/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import CharsetUtil.ISO_8859_1;
import CharsetUtil.UTF_8;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.EXPECT;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpVersion.HTTP_1_0;
import HttpVersion.HTTP_1_1;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpMethod.POST;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_0;
import static HttpVersion.HTTP_1_1;


public class HttpUtilTest {
    @Test
    public void testRemoveTransferEncodingIgnoreCase() {
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(TRANSFER_ENCODING, "Chunked");
        Assert.assertFalse(message.headers().isEmpty());
        HttpUtil.setTransferEncodingChunked(message, false);
        Assert.assertTrue(message.headers().isEmpty());
    }

    // Test for https://github.com/netty/netty/issues/1690
    @Test
    public void testGetOperations() {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeadersTestUtils.of("Foo"), HttpHeadersTestUtils.of("1"));
        headers.add(HttpHeadersTestUtils.of("Foo"), HttpHeadersTestUtils.of("2"));
        Assert.assertEquals("1", headers.get(HttpHeadersTestUtils.of("Foo")));
        List<String> values = headers.getAll(HttpHeadersTestUtils.of("Foo"));
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("1", values.get(0));
        Assert.assertEquals("2", values.get(1));
    }

    @Test
    public void testGetCharsetAsRawCharSequence() {
        String QUOTES_CHARSET_CONTENT_TYPE = "text/html; charset=\"utf8\"";
        String SIMPLE_CONTENT_TYPE = "text/html";
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_TYPE, QUOTES_CHARSET_CONTENT_TYPE);
        Assert.assertEquals("\"utf8\"", HttpUtil.getCharsetAsSequence(message));
        Assert.assertEquals("\"utf8\"", HttpUtil.getCharsetAsSequence(QUOTES_CHARSET_CONTENT_TYPE));
        message.headers().set(CONTENT_TYPE, "text/html");
        Assert.assertNull(HttpUtil.getCharsetAsSequence(message));
        Assert.assertNull(HttpUtil.getCharsetAsSequence(SIMPLE_CONTENT_TYPE));
    }

    @Test
    public void testGetCharset() {
        String NORMAL_CONTENT_TYPE = "text/html; charset=utf-8";
        String UPPER_CASE_NORMAL_CONTENT_TYPE = "TEXT/HTML; CHARSET=UTF-8";
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_TYPE, NORMAL_CONTENT_TYPE);
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(message));
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(NORMAL_CONTENT_TYPE));
        message.headers().set(CONTENT_TYPE, UPPER_CASE_NORMAL_CONTENT_TYPE);
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(message));
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(UPPER_CASE_NORMAL_CONTENT_TYPE));
    }

    @Test
    public void testGetCharsetIfNotLastParameter() {
        String NORMAL_CONTENT_TYPE_WITH_PARAMETERS = "application/soap-xml; charset=utf-8; " + "action=\"http://www.soap-service.by/foo/add\"";
        HttpMessage message = new DefaultHttpRequest(HTTP_1_1, POST, "http://localhost:7788/foo");
        message.headers().set(CONTENT_TYPE, NORMAL_CONTENT_TYPE_WITH_PARAMETERS);
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(message));
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(NORMAL_CONTENT_TYPE_WITH_PARAMETERS));
        Assert.assertEquals("utf-8", HttpUtil.getCharsetAsSequence(message));
        Assert.assertEquals("utf-8", HttpUtil.getCharsetAsSequence(NORMAL_CONTENT_TYPE_WITH_PARAMETERS));
    }

    @Test
    public void testGetCharset_defaultValue() {
        final String SIMPLE_CONTENT_TYPE = "text/html";
        final String CONTENT_TYPE_WITH_INCORRECT_CHARSET = "text/html; charset=UTFFF";
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_TYPE, SIMPLE_CONTENT_TYPE);
        Assert.assertEquals(ISO_8859_1, HttpUtil.getCharset(message));
        Assert.assertEquals(ISO_8859_1, HttpUtil.getCharset(SIMPLE_CONTENT_TYPE));
        message.headers().set(CONTENT_TYPE, SIMPLE_CONTENT_TYPE);
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(message, StandardCharsets.UTF_8));
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(SIMPLE_CONTENT_TYPE, StandardCharsets.UTF_8));
        message.headers().set(CONTENT_TYPE, CONTENT_TYPE_WITH_INCORRECT_CHARSET);
        Assert.assertEquals(ISO_8859_1, HttpUtil.getCharset(message));
        Assert.assertEquals(ISO_8859_1, HttpUtil.getCharset(CONTENT_TYPE_WITH_INCORRECT_CHARSET));
        message.headers().set(CONTENT_TYPE, CONTENT_TYPE_WITH_INCORRECT_CHARSET);
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(message, StandardCharsets.UTF_8));
        Assert.assertEquals(UTF_8, HttpUtil.getCharset(CONTENT_TYPE_WITH_INCORRECT_CHARSET, StandardCharsets.UTF_8));
    }

    @Test
    public void testGetMimeType() {
        final String SIMPLE_CONTENT_TYPE = "text/html";
        final String NORMAL_CONTENT_TYPE = "text/html; charset=utf-8";
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        Assert.assertNull(HttpUtil.getMimeType(message));
        message.headers().set(CONTENT_TYPE, "");
        Assert.assertNull(HttpUtil.getMimeType(message));
        Assert.assertNull(HttpUtil.getMimeType(""));
        message.headers().set(CONTENT_TYPE, SIMPLE_CONTENT_TYPE);
        Assert.assertEquals("text/html", HttpUtil.getMimeType(message));
        Assert.assertEquals("text/html", HttpUtil.getMimeType(SIMPLE_CONTENT_TYPE));
        message.headers().set(CONTENT_TYPE, NORMAL_CONTENT_TYPE);
        Assert.assertEquals("text/html", HttpUtil.getMimeType(message));
        Assert.assertEquals("text/html", HttpUtil.getMimeType(NORMAL_CONTENT_TYPE));
    }

    @Test
    public void testGetContentLengthThrowsNumberFormatException() {
        final HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_LENGTH, "bar");
        try {
            HttpUtil.getContentLength(message);
            Assert.fail();
        } catch (final NumberFormatException e) {
            // a number format exception is expected here
        }
    }

    @Test
    public void testGetContentLengthIntDefaultValueThrowsNumberFormatException() {
        final HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_LENGTH, "bar");
        try {
            HttpUtil.getContentLength(message, 1);
            Assert.fail();
        } catch (final NumberFormatException e) {
            // a number format exception is expected here
        }
    }

    @Test
    public void testGetContentLengthLongDefaultValueThrowsNumberFormatException() {
        final HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().set(CONTENT_LENGTH, "bar");
        try {
            HttpUtil.getContentLength(message, 1L);
            Assert.fail();
        } catch (final NumberFormatException e) {
            // a number format exception is expected here
        }
    }

    @Test
    public void testDoubleChunkedHeader() {
        HttpMessage message = new DefaultHttpResponse(HTTP_1_1, OK);
        message.headers().add(TRANSFER_ENCODING, "chunked");
        HttpUtil.setTransferEncodingChunked(message, true);
        List<String> expected = Collections.singletonList("chunked");
        Assert.assertEquals(expected, message.headers().getAll(TRANSFER_ENCODING));
    }

    @Test
    public void testIs100Continue() {
        // test all possible cases of 100-continue
        for (final String continueCase : HttpUtilTest.allPossibleCasesOfContinue()) {
            HttpUtilTest.run100ContinueTest(HTTP_1_1, ("100-" + continueCase), true);
        }
        HttpUtilTest.run100ContinueTest(HTTP_1_1, null, false);
        HttpUtilTest.run100ContinueTest(HTTP_1_1, "chocolate=yummy", false);
        HttpUtilTest.run100ContinueTest(HTTP_1_0, "100-continue", false);
        final HttpMessage message = new DefaultFullHttpResponse(HTTP_1_1, OK);
        message.headers().set(EXPECT, "100-continue");
        HttpUtilTest.run100ContinueTest(message, false);
    }

    @Test
    public void testContainsUnsupportedExpectation() {
        // test all possible cases of 100-continue
        for (final String continueCase : HttpUtilTest.allPossibleCasesOfContinue()) {
            HttpUtilTest.runUnsupportedExpectationTest(HTTP_1_1, ("100-" + continueCase), false);
        }
        HttpUtilTest.runUnsupportedExpectationTest(HTTP_1_1, null, false);
        HttpUtilTest.runUnsupportedExpectationTest(HTTP_1_1, "chocolate=yummy", true);
        HttpUtilTest.runUnsupportedExpectationTest(HTTP_1_0, "100-continue", false);
        final HttpMessage message = new DefaultFullHttpResponse(HTTP_1_1, OK);
        message.headers().set("Expect", "100-continue");
        HttpUtilTest.runUnsupportedExpectationTest(message, false);
    }

    @Test
    public void testFormatHostnameForHttpFromResolvedAddressWithHostname() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 8080);
        Assert.assertEquals("localhost", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testFormatHostnameForHttpFromUnesolvedAddressWithHostname() {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("localhost", 80);
        Assert.assertEquals("localhost", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testIpv6() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("::1"), 8080);
        Assert.assertEquals("[::1]", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testIpv6Unresolved() {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("::1", 8080);
        Assert.assertEquals("[::1]", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testIpv4() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 8080);
        Assert.assertEquals("10.0.0.1", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testIpv4Unresolved() {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("10.0.0.1", 8080);
        Assert.assertEquals("10.0.0.1", HttpUtil.formatHostnameForHttp(socketAddress));
    }

    @Test
    public void testKeepAliveIfConnectionHeaderAbsent() {
        HttpMessage http11Message = new DefaultHttpRequest(HTTP_1_1, GET, "http:localhost/http_1_1");
        Assert.assertTrue(HttpUtil.isKeepAlive(http11Message));
        HttpMessage http10Message = new DefaultHttpRequest(HTTP_1_0, GET, "http:localhost/http_1_0");
        Assert.assertFalse(HttpUtil.isKeepAlive(http10Message));
    }
}

