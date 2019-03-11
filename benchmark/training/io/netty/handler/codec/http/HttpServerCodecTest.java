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
import CharsetUtil.UTF_8;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpMethod.HEAD;
import LastHttpContent.EMPTY_LAST_CONTENT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpHeaderNames.CONTENT_LENGTH;
import static HttpResponseStatus.CREATED;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class HttpServerCodecTest {
    /**
     * Testcase for https://github.com/netty/netty/issues/433
     */
    @Test
    public void testUnfinishedChunkedHttpRequestIsLastFlag() throws Exception {
        int maxChunkSize = 2000;
        HttpServerCodec httpServerCodec = new HttpServerCodec(1000, 1000, maxChunkSize);
        EmbeddedChannel decoderEmbedder = new EmbeddedChannel(httpServerCodec);
        int totalContentLength = maxChunkSize * 5;
        decoderEmbedder.writeInbound(Unpooled.copiedBuffer((((("PUT /test HTTP/1.1\r\n" + "Content-Length: ") + totalContentLength) + "\r\n") + "\r\n"), UTF_8));
        int offeredContentLength = ((int) (maxChunkSize * 2.5));
        decoderEmbedder.writeInbound(HttpServerCodecTest.prepareDataChunk(offeredContentLength));
        decoderEmbedder.finish();
        HttpMessage httpMessage = decoderEmbedder.readInbound();
        Assert.assertNotNull(httpMessage);
        boolean empty = true;
        int totalBytesPolled = 0;
        for (; ;) {
            HttpContent httpChunk = decoderEmbedder.readInbound();
            if (httpChunk == null) {
                break;
            }
            empty = false;
            totalBytesPolled += httpChunk.content().readableBytes();
            Assert.assertFalse((httpChunk instanceof LastHttpContent));
            httpChunk.release();
        }
        Assert.assertFalse(empty);
        Assert.assertEquals(offeredContentLength, totalBytesPolled);
    }

    @Test
    public void test100Continue() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpServerCodec(), new HttpObjectAggregator(1024));
        // Send the request headers.
        ch.writeInbound(Unpooled.copiedBuffer(("PUT /upload-large HTTP/1.1\r\n" + ("Expect: 100-continue\r\n" + "Content-Length: 1\r\n\r\n")), UTF_8));
        // Ensure the aggregator generates nothing.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Ensure the aggregator writes a 100 Continue response.
        ByteBuf continueResponse = ch.readOutbound();
        Assert.assertThat(continueResponse.toString(UTF_8), CoreMatchers.is("HTTP/1.1 100 Continue\r\n\r\n"));
        continueResponse.release();
        // But nothing more.
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Send the content of the request.
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 42 }));
        // Ensure the aggregator generates a full request.
        FullHttpRequest req = ch.readInbound();
        Assert.assertThat(req.headers().get(CONTENT_LENGTH), CoreMatchers.is("1"));
        Assert.assertThat(req.content().readableBytes(), CoreMatchers.is(1));
        Assert.assertThat(req.content().readByte(), CoreMatchers.is(((byte) (42))));
        req.release();
        // But nothing more.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Send the actual response.
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, CREATED);
        res.content().writeBytes("OK".getBytes(UTF_8));
        res.headers().setInt(CONTENT_LENGTH, 2);
        ch.writeOutbound(res);
        // Ensure the encoder handles the response after handling 100 Continue.
        ByteBuf encodedRes = ch.readOutbound();
        Assert.assertThat(encodedRes.toString(UTF_8), CoreMatchers.is((("HTTP/1.1 201 Created\r\n" + (CONTENT_LENGTH)) + ": 2\r\n\r\nOK")));
        encodedRes.release();
        ch.finish();
    }

    @Test
    public void testChunkedHeadResponse() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpServerCodec());
        // Send the request headers.
        Assert.assertTrue(ch.writeInbound(Unpooled.copiedBuffer("HEAD / HTTP/1.1\r\n\r\n", UTF_8)));
        HttpRequest request = ch.readInbound();
        Assert.assertEquals(HEAD, request.method());
        LastHttpContent content = ch.readInbound();
        Assert.assertFalse(content.content().isReadable());
        content.release();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setTransferEncodingChunked(response, true);
        Assert.assertTrue(ch.writeOutbound(response));
        Assert.assertTrue(ch.writeOutbound(EMPTY_LAST_CONTENT));
        Assert.assertTrue(ch.finish());
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals("HTTP/1.1 200 OK\r\ntransfer-encoding: chunked\r\n\r\n", buf.toString(US_ASCII));
        buf.release();
        buf = ch.readOutbound();
        Assert.assertFalse(buf.isReadable());
        buf.release();
        Assert.assertFalse(ch.finishAndReleaseAll());
    }

    @Test
    public void testChunkedHeadFullHttpResponse() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpServerCodec());
        // Send the request headers.
        Assert.assertTrue(ch.writeInbound(Unpooled.copiedBuffer("HEAD / HTTP/1.1\r\n\r\n", UTF_8)));
        HttpRequest request = ch.readInbound();
        Assert.assertEquals(HEAD, request.method());
        LastHttpContent content = ch.readInbound();
        Assert.assertFalse(content.content().isReadable());
        content.release();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        HttpUtil.setTransferEncodingChunked(response, true);
        Assert.assertTrue(ch.writeOutbound(response));
        Assert.assertTrue(ch.finish());
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals("HTTP/1.1 200 OK\r\ntransfer-encoding: chunked\r\n\r\n", buf.toString(US_ASCII));
        buf.release();
        Assert.assertFalse(ch.finishAndReleaseAll());
    }
}

