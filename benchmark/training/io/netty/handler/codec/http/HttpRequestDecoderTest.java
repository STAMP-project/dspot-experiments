/**
 * Copyright 2013 The Netty Project
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


import CharsetUtil.US_ASCII;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpVersion.HTTP_1_0;
import HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AsciiString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpRequestDecoderTest {
    private static final byte[] CONTENT_CRLF_DELIMITERS = HttpRequestDecoderTest.createContent("\r\n");

    private static final byte[] CONTENT_LF_DELIMITERS = HttpRequestDecoderTest.createContent("\n");

    private static final byte[] CONTENT_MIXED_DELIMITERS = HttpRequestDecoderTest.createContent("\r\n", "\n");

    private static final int CONTENT_LENGTH = 8;

    @Test
    public void testDecodeWholeRequestAtOnceCRLFDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestAtOnce(HttpRequestDecoderTest.CONTENT_CRLF_DELIMITERS);
    }

    @Test
    public void testDecodeWholeRequestAtOnceLFDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestAtOnce(HttpRequestDecoderTest.CONTENT_LF_DELIMITERS);
    }

    @Test
    public void testDecodeWholeRequestAtOnceMixedDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestAtOnce(HttpRequestDecoderTest.CONTENT_MIXED_DELIMITERS);
    }

    @Test
    public void testDecodeWholeRequestInMultipleStepsCRLFDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestInMultipleSteps(HttpRequestDecoderTest.CONTENT_CRLF_DELIMITERS);
    }

    @Test
    public void testDecodeWholeRequestInMultipleStepsLFDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestInMultipleSteps(HttpRequestDecoderTest.CONTENT_LF_DELIMITERS);
    }

    @Test
    public void testDecodeWholeRequestInMultipleStepsMixedDelimiters() {
        HttpRequestDecoderTest.testDecodeWholeRequestInMultipleSteps(HttpRequestDecoderTest.CONTENT_MIXED_DELIMITERS);
    }

    @Test
    public void testMultiLineHeader() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder());
        String crlf = "\r\n";
        String request = ((((((((((("GET /some/path HTTP/1.1" + crlf) + "Host: localhost") + crlf) + "MyTestHeader: part1") + crlf) + "              newLinePart2") + crlf) + "MyTestHeader2: part21") + crlf) + "\t            newLinePart22") + crlf) + crlf;
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(request, US_ASCII)));
        HttpRequest req = channel.readInbound();
        Assert.assertEquals("part1 newLinePart2", req.headers().get(HttpHeadersTestUtils.of("MyTestHeader")));
        Assert.assertEquals("part21 newLinePart22", req.headers().get(HttpHeadersTestUtils.of("MyTestHeader2")));
        LastHttpContent c = channel.readInbound();
        c.release();
        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testEmptyHeaderValue() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder());
        String crlf = "\r\n";
        String request = ((((("GET /some/path HTTP/1.1" + crlf) + "Host: localhost") + crlf) + "EmptyHeader:") + crlf) + crlf;
        channel.writeInbound(Unpooled.copiedBuffer(request, US_ASCII));
        HttpRequest req = channel.readInbound();
        Assert.assertEquals("", req.headers().get(HttpHeadersTestUtils.of("EmptyHeader")));
    }

    @Test
    public void test100Continue() {
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        String oversized = "PUT /file HTTP/1.1\r\n" + ("Expect: 100-continue\r\n" + "Content-Length: 1048576000\r\n\r\n");
        channel.writeInbound(Unpooled.copiedBuffer(oversized, US_ASCII));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(HttpRequest.class)));
        // At this point, we assume that we sent '413 Entity Too Large' to the peer without closing the connection
        // so that the client can try again.
        decoder.reset();
        String query = "GET /max-file-size HTTP/1.1\r\n\r\n";
        channel.writeInbound(Unpooled.copiedBuffer(query, US_ASCII));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(HttpRequest.class)));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        Assert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void test100ContinueWithBadClient() {
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        String oversized = "PUT /file HTTP/1.1\r\n" + (("Expect: 100-continue\r\n" + "Content-Length: 1048576000\r\n\r\n") + "WAY_TOO_LARGE_DATA_BEGINS");
        channel.writeInbound(Unpooled.copiedBuffer(oversized, US_ASCII));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(HttpRequest.class)));
        HttpContent prematureData = channel.readInbound();
        prematureData.release();
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // At this point, we assume that we sent '413 Entity Too Large' to the peer without closing the connection
        // so that the client can try again.
        decoder.reset();
        String query = "GET /max-file-size HTTP/1.1\r\n\r\n";
        channel.writeInbound(Unpooled.copiedBuffer(query, US_ASCII));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(HttpRequest.class)));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        Assert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void testMessagesSplitBetweenMultipleBuffers() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder());
        String crlf = "\r\n";
        String str1 = (((((("GET /some/path HTTP/1.1" + crlf) + "Host: localhost1") + crlf) + crlf) + "GET /some/other/path HTTP/1.0") + crlf) + "Hos";
        String str2 = ((("t: localhost2" + crlf) + "content-length: 0") + crlf) + crlf;
        channel.writeInbound(Unpooled.copiedBuffer(str1, US_ASCII));
        HttpRequest req = channel.readInbound();
        Assert.assertEquals(HTTP_1_1, req.protocolVersion());
        Assert.assertEquals("/some/path", req.uri());
        Assert.assertEquals(1, req.headers().size());
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("localhost1", req.headers().get(HOST)));
        LastHttpContent cnt = channel.readInbound();
        cnt.release();
        channel.writeInbound(Unpooled.copiedBuffer(str2, US_ASCII));
        req = channel.readInbound();
        Assert.assertEquals(HTTP_1_0, req.protocolVersion());
        Assert.assertEquals("/some/other/path", req.uri());
        Assert.assertEquals(2, req.headers().size());
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("localhost2", req.headers().get(HOST)));
        Assert.assertTrue(AsciiString.contentEqualsIgnoreCase("0", req.headers().get(HttpHeaderNames.CONTENT_LENGTH)));
        cnt = channel.readInbound();
        cnt.release();
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void testTooLargeInitialLine() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder(10, 1024, 1024));
        String requestStr = "GET /some/path HTTP/1.1\r\n" + "Host: localhost1\r\n\r\n";
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(requestStr, US_ASCII)));
        HttpRequest request = channel.readInbound();
        Assert.assertTrue(request.decoderResult().isFailure());
        Assert.assertTrue(((request.decoderResult().cause()) instanceof TooLongFrameException));
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testTooLargeHeaders() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestDecoder(1024, 10, 1024));
        String requestStr = "GET /some/path HTTP/1.1\r\n" + "Host: localhost1\r\n\r\n";
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(requestStr, US_ASCII)));
        HttpRequest request = channel.readInbound();
        Assert.assertTrue(request.decoderResult().isFailure());
        Assert.assertTrue(((request.decoderResult().cause()) instanceof TooLongFrameException));
        Assert.assertFalse(channel.finish());
    }
}

