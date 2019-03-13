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
package org.jboss.netty.handler.codec.http;


import CharsetUtil.US_ASCII;
import org.easymock.EasyMock;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

import static HttpChunk.LAST_CHUNK;
import static HttpVersion.HTTP_1_1;


public class HttpChunkAggregatorTest {
    @Test
    public void testAggregate() {
        HttpChunkAggregator aggr = new HttpChunkAggregator((1024 * 1024));
        DecoderEmbedder<HttpMessage> embedder = new DecoderEmbedder<HttpMessage>(aggr);
        HttpMessage message = new DefaultHttpMessage(HTTP_1_1);
        HttpHeaders.setHeader(message, "X-Test", true);
        message.setChunked(true);
        HttpChunk chunk1 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test", US_ASCII));
        HttpChunk chunk2 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test2", US_ASCII));
        HttpChunk chunk3 = new DefaultHttpChunk(ChannelBuffers.EMPTY_BUFFER);
        Assert.assertFalse(embedder.offer(message));
        Assert.assertFalse(embedder.offer(chunk1));
        Assert.assertFalse(embedder.offer(chunk2));
        // this should trigger a messageReceived event so return true
        Assert.assertTrue(embedder.offer(chunk3));
        Assert.assertTrue(embedder.finish());
        HttpMessage aggratedMessage = embedder.poll();
        Assert.assertNotNull(aggratedMessage);
        Assert.assertEquals(((chunk1.getContent().readableBytes()) + (chunk2.getContent().readableBytes())), HttpHeaders.getContentLength(aggratedMessage));
        Assert.assertEquals(aggratedMessage.getHeader("X-Test"), Boolean.TRUE.toString());
        HttpChunkAggregatorTest.checkContentBuffer(aggratedMessage);
        Assert.assertNull(embedder.poll());
    }

    @Test
    public void testAggregateWithTrailer() {
        HttpChunkAggregator aggr = new HttpChunkAggregator((1024 * 1024));
        DecoderEmbedder<HttpMessage> embedder = new DecoderEmbedder<HttpMessage>(aggr);
        HttpMessage message = new DefaultHttpMessage(HTTP_1_1);
        HttpHeaders.setHeader(message, "X-Test", true);
        message.setChunked(true);
        HttpChunk chunk1 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test", US_ASCII));
        HttpChunk chunk2 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test2", US_ASCII));
        HttpChunkTrailer trailer = new DefaultHttpChunkTrailer();
        trailer.setHeader("X-Trailer", true);
        Assert.assertFalse(embedder.offer(message));
        Assert.assertFalse(embedder.offer(chunk1));
        Assert.assertFalse(embedder.offer(chunk2));
        // this should trigger a messageReceived event so return true
        Assert.assertTrue(embedder.offer(trailer));
        Assert.assertTrue(embedder.finish());
        HttpMessage aggratedMessage = embedder.poll();
        Assert.assertNotNull(aggratedMessage);
        Assert.assertEquals(((chunk1.getContent().readableBytes()) + (chunk2.getContent().readableBytes())), HttpHeaders.getContentLength(aggratedMessage));
        Assert.assertEquals(aggratedMessage.getHeader("X-Test"), Boolean.TRUE.toString());
        Assert.assertEquals(aggratedMessage.getHeader("X-Trailer"), Boolean.TRUE.toString());
        HttpChunkAggregatorTest.checkContentBuffer(aggratedMessage);
        Assert.assertNull(embedder.poll());
    }

    @Test
    public void testTooLongFrameException() {
        HttpChunkAggregator aggr = new HttpChunkAggregator(4);
        DecoderEmbedder<HttpMessage> embedder = new DecoderEmbedder<HttpMessage>(aggr);
        HttpMessage message = new DefaultHttpMessage(HTTP_1_1);
        message.setChunked(true);
        HttpMessage message2 = new DefaultHttpMessage(HTTP_1_1);
        message2.setChunked(true);
        HttpChunk chunk1 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test", US_ASCII));
        HttpChunk chunk2 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test2", US_ASCII));
        HttpChunk chunk3 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test3", US_ASCII));
        HttpChunk chunk4 = LAST_CHUNK;
        HttpChunk chunk5 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test", US_ASCII));
        HttpChunk chunk6 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test2", US_ASCII));
        HttpChunk chunk7 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test3", US_ASCII));
        HttpChunk chunk8 = LAST_CHUNK;
        Assert.assertFalse(embedder.offer(message));
        Assert.assertFalse(embedder.offer(chunk1));
        try {
            embedder.offer(chunk2);
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertTrue(((e.getCause()) instanceof TooLongFrameException));
        }
        Assert.assertFalse(embedder.offer(chunk3));
        Assert.assertFalse(embedder.offer(chunk4));
        Assert.assertFalse(embedder.offer(message2));
        Assert.assertFalse(embedder.offer(chunk5));
        try {
            embedder.offer(chunk6);
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertTrue(((e.getCause()) instanceof TooLongFrameException));
        }
        Assert.assertFalse(embedder.offer(chunk7));
        Assert.assertFalse(embedder.offer(chunk8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructorUsage() {
        new HttpChunkAggregator(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMaxCumulationBufferComponents() {
        HttpChunkAggregator aggr = new HttpChunkAggregator(Integer.MAX_VALUE);
        aggr.setMaxCumulationBufferComponents(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetMaxCumulationBufferComponentsAfterInit() throws Exception {
        HttpChunkAggregator aggr = new HttpChunkAggregator(Integer.MAX_VALUE);
        ChannelHandlerContext ctx = EasyMock.createMock(ChannelHandlerContext.class);
        EasyMock.replay(ctx);
        aggr.beforeAdd(ctx);
        aggr.setMaxCumulationBufferComponents(10);
    }

    @Test
    public void testAggregateTransferEncodingChunked() {
        HttpChunkAggregator aggr = new HttpChunkAggregator((1024 * 1024));
        DecoderEmbedder<HttpMessage> embedder = new DecoderEmbedder<HttpMessage>(aggr);
        HttpMessage message = new DefaultHttpMessage(HTTP_1_1);
        HttpHeaders.setHeader(message, "X-Test", true);
        HttpHeaders.setHeader(message, "Transfer-Encoding", "Chunked");
        message.setChunked(true);
        HttpChunk chunk1 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test", US_ASCII));
        HttpChunk chunk2 = new DefaultHttpChunk(ChannelBuffers.copiedBuffer("test2", US_ASCII));
        HttpChunk chunk3 = new DefaultHttpChunk(ChannelBuffers.EMPTY_BUFFER);
        Assert.assertFalse(embedder.offer(message));
        Assert.assertFalse(embedder.offer(chunk1));
        Assert.assertFalse(embedder.offer(chunk2));
        // this should trigger a messageReceived event so return true
        Assert.assertTrue(embedder.offer(chunk3));
        Assert.assertTrue(embedder.finish());
        HttpMessage aggratedMessage = embedder.poll();
        Assert.assertNotNull(aggratedMessage);
        Assert.assertEquals(((chunk1.getContent().readableBytes()) + (chunk2.getContent().readableBytes())), HttpHeaders.getContentLength(aggratedMessage));
        Assert.assertEquals(aggratedMessage.getHeader("X-Test"), Boolean.TRUE.toString());
        HttpChunkAggregatorTest.checkContentBuffer(aggratedMessage);
        Assert.assertNull(embedder.poll());
    }
}

