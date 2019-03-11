/**
 * Copyright 2012 The Netty Project
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
import DecoderResult.SUCCESS;
import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import HttpHeaderValues.IDENTITY;
import LastHttpContent.EMPTY_LAST_CONTENT;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.ReferenceCountUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpResponseStatus.CONTINUE;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class HttpContentCompressorTest {
    @Test
    public void testGetTargetContentEncoding() throws Exception {
        HttpContentCompressor compressor = new HttpContentCompressor();
        String[] tests = new String[]{ // Accept-Encoding -> Content-Encoding
        "", null, "*", "gzip", "*;q=0.0", null, "gzip", "gzip", "compress, gzip;q=0.5", "gzip", "gzip; q=0.5, identity", "gzip", "gzip ; q=0.1", "gzip", "gzip; q=0, deflate", "deflate", " deflate ; q=0 , *;q=0.5", "gzip" };
        for (int i = 0; i < (tests.length); i += 2) {
            String acceptEncoding = tests[i];
            String contentEncoding = tests[(i + 1)];
            ZlibWrapper targetWrapper = compressor.determineWrapper(acceptEncoding);
            String targetEncoding = null;
            if (targetWrapper != null) {
                switch (targetWrapper) {
                    case GZIP :
                        targetEncoding = "gzip";
                        break;
                    case ZLIB :
                        targetEncoding = "deflate";
                        break;
                    default :
                        Assert.fail();
                }
            }
            Assert.assertEquals(contentEncoding, targetEncoding);
        }
    }

    @Test
    public void testSplitContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        ch.writeOutbound(new DefaultHttpResponse(HTTP_1_1, OK));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("Hell", US_ASCII)));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("o, w", US_ASCII)));
        ch.writeOutbound(new DefaultLastHttpContent(Unpooled.copiedBuffer("orld", US_ASCII)));
        HttpContentCompressorTest.assertEncodedResponse(ch);
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("1f8b0800000000000000f248cdc901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("cad7512807000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("ca2fca4901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("0300c2a99ae70c000000"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testChunkedContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        HttpContentCompressorTest.assertEncodedResponse(ch);
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("Hell", US_ASCII)));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("o, w", US_ASCII)));
        ch.writeOutbound(new DefaultLastHttpContent(Unpooled.copiedBuffer("orld", US_ASCII)));
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("1f8b0800000000000000f248cdc901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("cad7512807000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("ca2fca4901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("0300c2a99ae70c000000"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testChunkedContentWithTrailingHeader() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        HttpContentCompressorTest.assertEncodedResponse(ch);
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("Hell", US_ASCII)));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.copiedBuffer("o, w", US_ASCII)));
        LastHttpContent content = new DefaultLastHttpContent(Unpooled.copiedBuffer("orld", US_ASCII));
        content.trailingHeaders().set(HttpHeadersTestUtils.of("X-Test"), HttpHeadersTestUtils.of("Netty"));
        ch.writeOutbound(content);
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("1f8b0800000000000000f248cdc901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("cad7512807000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("ca2fca4901000000ffff"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("0300c2a99ae70c000000"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        Assert.assertEquals("Netty", trailingHeaders().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(SUCCESS, chunk.decoderResult());
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testFullContentWithContentLength() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        FullHttpResponse fullRes = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("Hello, World", US_ASCII));
        fullRes.headers().set(CONTENT_LENGTH, fullRes.content().readableBytes());
        ch.writeOutbound(fullRes);
        HttpResponse res = ch.readOutbound();
        Assert.assertThat(res, CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(HttpContent.class))));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is("gzip"));
        long contentLengthHeaderValue = HttpUtil.getContentLength(res);
        long observedLength = 0;
        HttpContent c = ch.readOutbound();
        observedLength += c.content().readableBytes();
        Assert.assertThat(ByteBufUtil.hexDump(c.content()), CoreMatchers.is("1f8b0800000000000000f248cdc9c9d75108cf2fca4901000000ffff"));
        c.release();
        c = ch.readOutbound();
        observedLength += c.content().readableBytes();
        Assert.assertThat(ByteBufUtil.hexDump(c.content()), CoreMatchers.is("0300c6865b260c000000"));
        c.release();
        LastHttpContent last = ch.readOutbound();
        Assert.assertThat(last.content().readableBytes(), CoreMatchers.is(0));
        last.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertEquals(contentLengthHeaderValue, observedLength);
    }

    @Test
    public void testFullContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("Hello, World", US_ASCII));
        ch.writeOutbound(res);
        HttpContentCompressorTest.assertEncodedResponse(ch);
        HttpContent c = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(c.content()), CoreMatchers.is("1f8b0800000000000000f248cdc9c9d75108cf2fca4901000000ffff"));
        c.release();
        c = ch.readOutbound();
        Assert.assertThat(ByteBufUtil.hexDump(c.content()), CoreMatchers.is("0300c6865b260c000000"));
        c.release();
        LastHttpContent last = ch.readOutbound();
        Assert.assertThat(last.content().readableBytes(), CoreMatchers.is(0));
        last.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * If the length of the content is unknown, {@link HttpContentEncoder} should not skip encoding the content
     * even if the actual length is turned out to be 0.
     */
    @Test
    public void testEmptySplitContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        ch.writeOutbound(new DefaultHttpResponse(HTTP_1_1, OK));
        HttpContentCompressorTest.assertEncodedResponse(ch);
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContent chunk = ((HttpContent) (ch.readOutbound()));
        Assert.assertThat(ByteBufUtil.hexDump(chunk.content()), CoreMatchers.is("1f8b080000000000000003000000000000000000"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * If the length of the content is 0 for sure, {@link HttpContentEncoder} should skip encoding.
     */
    @Test
    public void testEmptyFullContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER);
        ch.writeOutbound(res);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        // Content encoding shouldn't be modified.
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.content().readableBytes(), CoreMatchers.is(0));
        Assert.assertThat(res.content().toString(US_ASCII), CoreMatchers.is(""));
        res.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testEmptyFullContentWithTrailer() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(HttpContentCompressorTest.newRequest());
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER);
        res.trailingHeaders().set(HttpHeadersTestUtils.of("X-Test"), HttpHeadersTestUtils.of("Netty"));
        ch.writeOutbound(res);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        // Content encoding shouldn't be modified.
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.content().readableBytes(), CoreMatchers.is(0));
        Assert.assertThat(res.content().toString(US_ASCII), CoreMatchers.is(""));
        Assert.assertEquals("Netty", res.trailingHeaders().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(SUCCESS, res.decoderResult());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void test100Continue() throws Exception {
        FullHttpRequest request = HttpContentCompressorTest.newRequest();
        HttpUtil.set100ContinueExpected(request, true);
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(request);
        FullHttpResponse continueResponse = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ch.writeOutbound(continueResponse);
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER);
        res.trailingHeaders().set(HttpHeadersTestUtils.of("X-Test"), HttpHeadersTestUtils.of("Netty"));
        ch.writeOutbound(res);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertSame(continueResponse, res);
        res.release();
        o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        // Content encoding shouldn't be modified.
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.content().readableBytes(), CoreMatchers.is(0));
        Assert.assertThat(res.content().toString(US_ASCII), CoreMatchers.is(""));
        Assert.assertEquals("Netty", res.trailingHeaders().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(SUCCESS, res.decoderResult());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testTooManyResponses() throws Exception {
        FullHttpRequest request = HttpContentCompressorTest.newRequest();
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        ch.writeInbound(request);
        ch.writeOutbound(new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER));
        try {
            ch.writeOutbound(new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER));
            Assert.fail();
        } catch (EncoderException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
        Assert.assertTrue(ch.finish());
        for (; ;) {
            Object message = ch.readOutbound();
            if (message == null) {
                break;
            }
            ReferenceCountUtil.release(message);
        }
        for (; ;) {
            Object message = ch.readInbound();
            if (message == null) {
                break;
            }
            ReferenceCountUtil.release(message);
        }
    }

    @Test
    public void testIdentity() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("Hello, World", US_ASCII));
        int len = res.content().readableBytes();
        res.headers().set(CONTENT_LENGTH, len);
        res.headers().set(CONTENT_ENCODING, IDENTITY);
        Assert.assertTrue(ch.writeOutbound(res));
        FullHttpResponse response = ((FullHttpResponse) (ch.readOutbound()));
        Assert.assertEquals(String.valueOf(len), response.headers().get(CONTENT_LENGTH));
        Assert.assertEquals(IDENTITY.toString(), response.headers().get(CONTENT_ENCODING));
        Assert.assertEquals("Hello, World", response.content().toString(US_ASCII));
        response.release();
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test
    public void testCustomEncoding() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("Hello, World", US_ASCII));
        int len = res.content().readableBytes();
        res.headers().set(CONTENT_LENGTH, len);
        res.headers().set(CONTENT_ENCODING, "ascii");
        Assert.assertTrue(ch.writeOutbound(res));
        FullHttpResponse response = ((FullHttpResponse) (ch.readOutbound()));
        Assert.assertEquals(String.valueOf(len), response.headers().get(CONTENT_LENGTH));
        Assert.assertEquals("ascii", response.headers().get(CONTENT_ENCODING));
        Assert.assertEquals("Hello, World", response.content().toString(US_ASCII));
        response.release();
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test
    public void testCompressThresholdAllCompress() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor());
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res1023 = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[1023]));
        Assert.assertTrue(ch.writeOutbound(res1023));
        DefaultHttpResponse response1023 = ch.readOutbound();
        Assert.assertThat(response1023.headers().get(CONTENT_ENCODING), CoreMatchers.is("gzip"));
        ch.releaseOutbound();
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res1024 = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[1024]));
        Assert.assertTrue(ch.writeOutbound(res1024));
        DefaultHttpResponse response1024 = ch.readOutbound();
        Assert.assertThat(response1024.headers().get(CONTENT_ENCODING), CoreMatchers.is("gzip"));
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test
    public void testCompressThresholdNotCompress() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentCompressor(6, 15, 8, 1024));
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res1023 = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[1023]));
        Assert.assertTrue(ch.writeOutbound(res1023));
        DefaultHttpResponse response1023 = ch.readOutbound();
        Assert.assertFalse(response1023.headers().contains(CONTENT_ENCODING));
        ch.releaseOutbound();
        Assert.assertTrue(ch.writeInbound(HttpContentCompressorTest.newRequest()));
        FullHttpResponse res1024 = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[1024]));
        Assert.assertTrue(ch.writeOutbound(res1024));
        DefaultHttpResponse response1024 = ch.readOutbound();
        Assert.assertThat(response1024.headers().get(CONTENT_ENCODING), CoreMatchers.is("gzip"));
        Assert.assertTrue(ch.finishAndReleaseAll());
    }
}

