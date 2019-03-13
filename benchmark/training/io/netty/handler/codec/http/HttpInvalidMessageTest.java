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


import CharsetUtil.UTF_8;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class HttpInvalidMessageTest {
    private final Random rnd = new Random();

    @Test
    public void testRequestWithBadInitialLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("GET / HTTP/1.0 with extra\r\n", UTF_8));
        HttpRequest req = ch.readInbound();
        DecoderResult dr = req.decoderResult();
        Assert.assertFalse(dr.isSuccess());
        Assert.assertTrue(dr.isFailure());
        ensureInboundTrafficDiscarded(ch);
    }

    @Test
    public void testRequestWithBadHeader() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("GET /maybe-something HTTP/1.0\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("Good_Name: Good Value\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("Bad=Name: Bad Value\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", UTF_8));
        HttpRequest req = ch.readInbound();
        DecoderResult dr = req.decoderResult();
        Assert.assertFalse(dr.isSuccess());
        Assert.assertTrue(dr.isFailure());
        Assert.assertEquals("Good Value", req.headers().get(HttpHeadersTestUtils.of("Good_Name")));
        Assert.assertEquals("/maybe-something", req.uri());
        ensureInboundTrafficDiscarded(ch);
    }

    @Test
    public void testResponseWithBadInitialLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.0 BAD_CODE Bad Server\r\n", UTF_8));
        HttpResponse res = ch.readInbound();
        DecoderResult dr = res.decoderResult();
        Assert.assertFalse(dr.isSuccess());
        Assert.assertTrue(dr.isFailure());
        ensureInboundTrafficDiscarded(ch);
    }

    @Test
    public void testResponseWithBadHeader() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.0 200 Maybe OK\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("Good_Name: Good Value\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("Bad=Name: Bad Value\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", UTF_8));
        HttpResponse res = ch.readInbound();
        DecoderResult dr = res.decoderResult();
        Assert.assertFalse(dr.isSuccess());
        Assert.assertTrue(dr.isFailure());
        Assert.assertEquals("Maybe OK", res.status().reasonPhrase());
        Assert.assertEquals("Good Value", res.headers().get(HttpHeadersTestUtils.of("Good_Name")));
        ensureInboundTrafficDiscarded(ch);
    }

    @Test
    public void testBadChunk() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("GET / HTTP/1.0\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("Transfer-Encoding: chunked\r\n\r\n", UTF_8));
        ch.writeInbound(Unpooled.copiedBuffer("BAD_LENGTH\r\n", UTF_8));
        HttpRequest req = ch.readInbound();
        Assert.assertTrue(req.decoderResult().isSuccess());
        LastHttpContent chunk = ch.readInbound();
        DecoderResult dr = chunk.decoderResult();
        Assert.assertFalse(dr.isSuccess());
        Assert.assertTrue(dr.isFailure());
        ensureInboundTrafficDiscarded(ch);
    }
}

