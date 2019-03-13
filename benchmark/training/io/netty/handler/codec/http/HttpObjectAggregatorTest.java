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
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.EXPECT;
import HttpHeaderValues.APPLICATION_JSON;
import HttpHeaderValues.TEXT_PLAIN;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpResponseStatus.CONTINUE;
import HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
import LastHttpContent.EMPTY_LAST_CONTENT;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.ClosedChannelException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static HttpMethod.GET;
import static HttpMethod.POST;
import static HttpMethod.PUT;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_0;
import static HttpVersion.HTTP_1_1;
import static LastHttpContent.EMPTY_LAST_CONTENT;


public class HttpObjectAggregatorTest {
    @Test
    public void testAggregate() {
        HttpObjectAggregator aggr = new HttpObjectAggregator((1024 * 1024));
        EmbeddedChannel embedder = new EmbeddedChannel(aggr);
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost");
        message.headers().set(HttpHeadersTestUtils.of("X-Test"), true);
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test2", US_ASCII));
        HttpContent chunk3 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        Assert.assertFalse(embedder.writeInbound(message));
        Assert.assertFalse(embedder.writeInbound(chunk1));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        // this should trigger a channelRead event so return true
        Assert.assertTrue(embedder.writeInbound(chunk3));
        Assert.assertTrue(embedder.finish());
        FullHttpRequest aggregatedMessage = embedder.readInbound();
        Assert.assertNotNull(aggregatedMessage);
        Assert.assertEquals(((chunk1.content().readableBytes()) + (chunk2.content().readableBytes())), HttpUtil.getContentLength(aggregatedMessage));
        Assert.assertEquals(Boolean.TRUE.toString(), aggregatedMessage.headers().get(HttpHeadersTestUtils.of("X-Test")));
        HttpObjectAggregatorTest.checkContentBuffer(aggregatedMessage);
        Assert.assertNull(embedder.readInbound());
    }

    @Test
    public void testAggregateWithTrailer() {
        HttpObjectAggregator aggr = new HttpObjectAggregator((1024 * 1024));
        EmbeddedChannel embedder = new EmbeddedChannel(aggr);
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost");
        message.headers().set(HttpHeadersTestUtils.of("X-Test"), true);
        HttpUtil.setTransferEncodingChunked(message, true);
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test2", US_ASCII));
        LastHttpContent trailer = new DefaultLastHttpContent();
        trailer.trailingHeaders().set(HttpHeadersTestUtils.of("X-Trailer"), true);
        Assert.assertFalse(embedder.writeInbound(message));
        Assert.assertFalse(embedder.writeInbound(chunk1));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        // this should trigger a channelRead event so return true
        Assert.assertTrue(embedder.writeInbound(trailer));
        Assert.assertTrue(embedder.finish());
        FullHttpRequest aggregatedMessage = embedder.readInbound();
        Assert.assertNotNull(aggregatedMessage);
        Assert.assertEquals(((chunk1.content().readableBytes()) + (chunk2.content().readableBytes())), HttpUtil.getContentLength(aggregatedMessage));
        Assert.assertEquals(Boolean.TRUE.toString(), aggregatedMessage.headers().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(Boolean.TRUE.toString(), aggregatedMessage.trailingHeaders().get(HttpHeadersTestUtils.of("X-Trailer")));
        HttpObjectAggregatorTest.checkContentBuffer(aggregatedMessage);
        Assert.assertNull(embedder.readInbound());
    }

    @Test
    public void testOversizedRequest() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpObjectAggregator(4));
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test2", US_ASCII));
        HttpContent chunk3 = EMPTY_LAST_CONTENT;
        Assert.assertFalse(embedder.writeInbound(message));
        Assert.assertFalse(embedder.writeInbound(chunk1));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertEquals("0", response.headers().get(CONTENT_LENGTH));
        Assert.assertFalse(embedder.isOpen());
        try {
            Assert.assertFalse(embedder.writeInbound(chunk3));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ClosedChannelException));
        }
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testOversizedRequestWithoutKeepAlive() {
        // send a HTTP/1.0 request with no keep-alive header
        HttpRequest message = new DefaultHttpRequest(HTTP_1_0, PUT, "http://localhost");
        HttpUtil.setContentLength(message, 5);
        HttpObjectAggregatorTest.checkOversizedRequest(message);
    }

    @Test
    public void testOversizedRequestWithContentLength() {
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        HttpUtil.setContentLength(message, 5);
        HttpObjectAggregatorTest.checkOversizedRequest(message);
    }

    @Test
    public void testOversizedResponse() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpObjectAggregator(4));
        HttpResponse message = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test2", US_ASCII));
        Assert.assertFalse(embedder.writeInbound(message));
        Assert.assertFalse(embedder.writeInbound(chunk1));
        try {
            embedder.writeInbound(chunk2);
            Assert.fail();
        } catch (TooLongFrameException expected) {
            // Expected
        }
        Assert.assertFalse(embedder.isOpen());
        Assert.assertFalse(embedder.finish());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructorUsage() {
        new HttpObjectAggregator((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMaxCumulationBufferComponents() {
        HttpObjectAggregator aggr = new HttpObjectAggregator(Integer.MAX_VALUE);
        aggr.setMaxCumulationBufferComponents(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetMaxCumulationBufferComponentsAfterInit() throws Exception {
        HttpObjectAggregator aggr = new HttpObjectAggregator(Integer.MAX_VALUE);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        aggr.handlerAdded(ctx);
        Mockito.verifyNoMoreInteractions(ctx);
        aggr.setMaxCumulationBufferComponents(10);
    }

    @Test
    public void testAggregateTransferEncodingChunked() {
        HttpObjectAggregator aggr = new HttpObjectAggregator((1024 * 1024));
        EmbeddedChannel embedder = new EmbeddedChannel(aggr);
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        message.headers().set(HttpHeadersTestUtils.of("X-Test"), true);
        message.headers().set(HttpHeadersTestUtils.of("Transfer-Encoding"), HttpHeadersTestUtils.of("Chunked"));
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test2", US_ASCII));
        HttpContent chunk3 = EMPTY_LAST_CONTENT;
        Assert.assertFalse(embedder.writeInbound(message));
        Assert.assertFalse(embedder.writeInbound(chunk1));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        // this should trigger a channelRead event so return true
        Assert.assertTrue(embedder.writeInbound(chunk3));
        Assert.assertTrue(embedder.finish());
        FullHttpRequest aggregatedMessage = embedder.readInbound();
        Assert.assertNotNull(aggregatedMessage);
        Assert.assertEquals(((chunk1.content().readableBytes()) + (chunk2.content().readableBytes())), HttpUtil.getContentLength(aggregatedMessage));
        Assert.assertEquals(Boolean.TRUE.toString(), aggregatedMessage.headers().get(HttpHeadersTestUtils.of("X-Test")));
        HttpObjectAggregatorTest.checkContentBuffer(aggregatedMessage);
        Assert.assertNull(embedder.readInbound());
    }

    @Test
    public void testBadRequest() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder(), new HttpObjectAggregator((1024 * 1024)));
        ch.writeInbound(Unpooled.copiedBuffer("GET / HTTP/1.0 with extra\r\n", UTF_8));
        Object inbound = ch.readInbound();
        Assert.assertThat(inbound, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpRequest.class)));
        Assert.assertTrue(decoderResult().isFailure());
        Assert.assertNull(ch.readInbound());
        ch.finish();
    }

    @Test
    public void testBadResponse() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder(), new HttpObjectAggregator((1024 * 1024)));
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.0 BAD_CODE Bad Server\r\n", UTF_8));
        Object inbound = ch.readInbound();
        Assert.assertThat(inbound, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        Assert.assertTrue(decoderResult().isFailure());
        Assert.assertNull(ch.readInbound());
        ch.finish();
    }

    @Test
    public void testOversizedRequestWith100Continue() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpObjectAggregator(8));
        // Send an oversized request with 100 continue.
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        HttpUtil.set100ContinueExpected(message, true);
        HttpUtil.setContentLength(message, 16);
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("some", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk3 = EMPTY_LAST_CONTENT;
        // Send a request with 100-continue + large Content-Length header value.
        Assert.assertFalse(embedder.writeInbound(message));
        // The aggregator should respond with '413.'
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertEquals("0", response.headers().get(CONTENT_LENGTH));
        // An ill-behaving client could continue to send data without a respect, and such data should be discarded.
        Assert.assertFalse(embedder.writeInbound(chunk1));
        // The aggregator should not close the connection because keep-alive is on.
        Assert.assertTrue(embedder.isOpen());
        // Now send a valid request.
        HttpRequest message2 = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        Assert.assertFalse(embedder.writeInbound(message2));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        Assert.assertTrue(embedder.writeInbound(chunk3));
        FullHttpRequest fullMsg = embedder.readInbound();
        Assert.assertNotNull(fullMsg);
        Assert.assertEquals(((chunk2.content().readableBytes()) + (chunk3.content().readableBytes())), HttpUtil.getContentLength(fullMsg));
        Assert.assertEquals(HttpUtil.getContentLength(fullMsg), fullMsg.content().readableBytes());
        fullMsg.release();
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testUnsupportedExpectHeaderExpectation() {
        HttpObjectAggregatorTest.runUnsupportedExceptHeaderExceptionTest(true);
        HttpObjectAggregatorTest.runUnsupportedExceptHeaderExceptionTest(false);
    }

    @Test
    public void testValidRequestWith100ContinueAndDecoder() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpRequestDecoder(), new HttpObjectAggregator(100));
        embedder.writeInbound(Unpooled.copiedBuffer(("GET /upload HTTP/1.1\r\n" + ("Expect: 100-continue\r\n" + "Content-Length: 0\r\n\r\n")), US_ASCII));
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(CONTINUE, response.status());
        FullHttpRequest request = embedder.readInbound();
        Assert.assertFalse(request.headers().contains(EXPECT));
        request.release();
        response.release();
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testOversizedRequestWith100ContinueAndDecoder() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpRequestDecoder(), new HttpObjectAggregator(4));
        embedder.writeInbound(Unpooled.copiedBuffer(("PUT /upload HTTP/1.1\r\n" + ("Expect: 100-continue\r\n" + "Content-Length: 100\r\n\r\n")), US_ASCII));
        Assert.assertNull(embedder.readInbound());
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertEquals("0", response.headers().get(CONTENT_LENGTH));
        // Keep-alive is on by default in HTTP/1.1, so the connection should be still alive.
        Assert.assertTrue(embedder.isOpen());
        // The decoder should be reset by the aggregator at this point and be able to decode the next request.
        embedder.writeInbound(Unpooled.copiedBuffer("GET /max-upload-size HTTP/1.1\r\n\r\n", US_ASCII));
        FullHttpRequest request = embedder.readInbound();
        Assert.assertThat(request.method(), CoreMatchers.is(GET));
        Assert.assertThat(request.uri(), CoreMatchers.is("/max-upload-size"));
        Assert.assertThat(request.content().readableBytes(), CoreMatchers.is(0));
        request.release();
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testOversizedRequestWith100ContinueAndDecoderCloseConnection() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpRequestDecoder(), new HttpObjectAggregator(4, true));
        embedder.writeInbound(Unpooled.copiedBuffer(("PUT /upload HTTP/1.1\r\n" + ("Expect: 100-continue\r\n" + "Content-Length: 100\r\n\r\n")), US_ASCII));
        Assert.assertNull(embedder.readInbound());
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertEquals("0", response.headers().get(CONTENT_LENGTH));
        // We are forcing the connection closed if an expectation is exceeded.
        Assert.assertFalse(embedder.isOpen());
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testRequestAfterOversized100ContinueAndDecoder() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpRequestDecoder(), new HttpObjectAggregator(15));
        // Write first request with Expect: 100-continue.
        HttpRequest message = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        HttpUtil.set100ContinueExpected(message, true);
        HttpUtil.setContentLength(message, 16);
        HttpContent chunk1 = new DefaultHttpContent(Unpooled.copiedBuffer("some", US_ASCII));
        HttpContent chunk2 = new DefaultHttpContent(Unpooled.copiedBuffer("test", US_ASCII));
        HttpContent chunk3 = EMPTY_LAST_CONTENT;
        // Send a request with 100-continue + large Content-Length header value.
        Assert.assertFalse(embedder.writeInbound(message));
        // The aggregator should respond with '413'.
        FullHttpResponse response = embedder.readOutbound();
        Assert.assertEquals(REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertEquals("0", response.headers().get(CONTENT_LENGTH));
        // An ill-behaving client could continue to send data without a respect, and such data should be discarded.
        Assert.assertFalse(embedder.writeInbound(chunk1));
        // The aggregator should not close the connection because keep-alive is on.
        Assert.assertTrue(embedder.isOpen());
        // Now send a valid request.
        HttpRequest message2 = new DefaultHttpRequest(HTTP_1_1, PUT, "http://localhost");
        Assert.assertFalse(embedder.writeInbound(message2));
        Assert.assertFalse(embedder.writeInbound(chunk2));
        Assert.assertTrue(embedder.writeInbound(chunk3));
        FullHttpRequest fullMsg = embedder.readInbound();
        Assert.assertNotNull(fullMsg);
        Assert.assertEquals(((chunk2.content().readableBytes()) + (chunk3.content().readableBytes())), HttpUtil.getContentLength(fullMsg));
        Assert.assertEquals(HttpUtil.getContentLength(fullMsg), fullMsg.content().readableBytes());
        fullMsg.release();
        Assert.assertFalse(embedder.finish());
    }

    @Test
    public void testReplaceAggregatedRequest() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpObjectAggregator((1024 * 1024)));
        Exception boom = new Exception("boom");
        HttpRequest req = new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost");
        req.setDecoderResult(DecoderResult.failure(boom));
        Assert.assertTrue(((embedder.writeInbound(req)) && (embedder.finish())));
        FullHttpRequest aggregatedReq = embedder.readInbound();
        FullHttpRequest replacedReq = aggregatedReq.replace(EMPTY_BUFFER);
        Assert.assertEquals(replacedReq.decoderResult(), aggregatedReq.decoderResult());
        aggregatedReq.release();
        replacedReq.release();
    }

    @Test
    public void testReplaceAggregatedResponse() {
        EmbeddedChannel embedder = new EmbeddedChannel(new HttpObjectAggregator((1024 * 1024)));
        Exception boom = new Exception("boom");
        HttpResponse rep = new DefaultHttpResponse(HTTP_1_1, OK);
        rep.setDecoderResult(DecoderResult.failure(boom));
        Assert.assertTrue(((embedder.writeInbound(rep)) && (embedder.finish())));
        FullHttpResponse aggregatedRep = embedder.readInbound();
        FullHttpResponse replacedRep = aggregatedRep.replace(EMPTY_BUFFER);
        Assert.assertEquals(replacedRep.decoderResult(), aggregatedRep.decoderResult());
        aggregatedRep.release();
        replacedRep.release();
    }

    @Test
    public void testSelectiveRequestAggregation() {
        HttpObjectAggregator myPostAggregator = new HttpObjectAggregator((1024 * 1024)) {
            @Override
            protected boolean isStartMessage(HttpObject msg) throws Exception {
                if (msg instanceof HttpRequest) {
                    HttpRequest request = ((HttpRequest) (msg));
                    HttpMethod method = request.method();
                    if (method.equals(POST)) {
                        return true;
                    }
                }
                return false;
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(myPostAggregator);
        try {
            // Aggregate: POST
            HttpRequest request1 = new DefaultHttpRequest(HTTP_1_1, POST, "/");
            HttpContent content1 = new DefaultHttpContent(Unpooled.copiedBuffer("Hello, World!", UTF_8));
            request1.headers().set(CONTENT_TYPE, TEXT_PLAIN);
            Assert.assertTrue(channel.writeInbound(request1, content1, EMPTY_LAST_CONTENT));
            // Getting an aggregated response out
            Object msg1 = channel.readInbound();
            try {
                Assert.assertTrue((msg1 instanceof FullHttpRequest));
            } finally {
                ReferenceCountUtil.release(msg1);
            }
            // Don't aggregate: non-POST
            HttpRequest request2 = new DefaultHttpRequest(HTTP_1_1, PUT, "/");
            HttpContent content2 = new DefaultHttpContent(Unpooled.copiedBuffer("Hello, World!", UTF_8));
            request2.headers().set(CONTENT_TYPE, TEXT_PLAIN);
            try {
                Assert.assertTrue(channel.writeInbound(request2, content2, EMPTY_LAST_CONTENT));
                // Getting the same response objects out
                Assert.assertSame(request2, channel.readInbound());
                Assert.assertSame(content2, channel.readInbound());
                Assert.assertSame(EMPTY_LAST_CONTENT, channel.readInbound());
            } finally {
                ReferenceCountUtil.release(request2);
                ReferenceCountUtil.release(content2);
            }
            Assert.assertFalse(channel.finish());
        } finally {
            channel.close();
        }
    }

    @Test
    public void testSelectiveResponseAggregation() {
        HttpObjectAggregator myTextAggregator = new HttpObjectAggregator((1024 * 1024)) {
            @Override
            protected boolean isStartMessage(HttpObject msg) throws Exception {
                if (msg instanceof HttpResponse) {
                    HttpResponse response = ((HttpResponse) (msg));
                    HttpHeaders headers = response.headers();
                    String contentType = headers.get(CONTENT_TYPE);
                    if (AsciiString.contentEqualsIgnoreCase(contentType, TEXT_PLAIN)) {
                        return true;
                    }
                }
                return false;
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(myTextAggregator);
        try {
            // Aggregate: text/plain
            HttpResponse response1 = new DefaultHttpResponse(HTTP_1_1, OK);
            HttpContent content1 = new DefaultHttpContent(Unpooled.copiedBuffer("Hello, World!", UTF_8));
            response1.headers().set(CONTENT_TYPE, TEXT_PLAIN);
            Assert.assertTrue(channel.writeInbound(response1, content1, EMPTY_LAST_CONTENT));
            // Getting an aggregated response out
            Object msg1 = channel.readInbound();
            try {
                Assert.assertTrue((msg1 instanceof FullHttpResponse));
            } finally {
                ReferenceCountUtil.release(msg1);
            }
            // Don't aggregate: application/json
            HttpResponse response2 = new DefaultHttpResponse(HTTP_1_1, OK);
            HttpContent content2 = new DefaultHttpContent(Unpooled.copiedBuffer("{key: 'value'}", UTF_8));
            response2.headers().set(CONTENT_TYPE, APPLICATION_JSON);
            try {
                Assert.assertTrue(channel.writeInbound(response2, content2, EMPTY_LAST_CONTENT));
                // Getting the same response objects out
                Assert.assertSame(response2, channel.readInbound());
                Assert.assertSame(content2, channel.readInbound());
                Assert.assertSame(EMPTY_LAST_CONTENT, channel.readInbound());
            } finally {
                ReferenceCountUtil.release(response2);
                ReferenceCountUtil.release(content2);
            }
            Assert.assertFalse(channel.finish());
        } finally {
            channel.close();
        }
    }
}

