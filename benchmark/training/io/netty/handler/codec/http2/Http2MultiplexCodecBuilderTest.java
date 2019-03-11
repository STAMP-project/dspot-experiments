/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.codec.http2;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link Http2MultiplexCodec}.
 */
public class Http2MultiplexCodecBuilderTest {
    private static EventLoopGroup group;

    private Channel serverChannel;

    private volatile Channel serverConnectedChannel;

    private Channel clientChannel;

    private LastInboundHandler serverLastInboundHandler;

    @Test
    public void multipleOutboundStreams() throws Exception {
        Http2StreamChannel childChannel1 = newOutboundStream(new TestChannelInitializer());
        Assert.assertTrue(childChannel1.isActive());
        Assert.assertFalse(Http2CodecUtil.isStreamIdValid(childChannel1.stream().id()));
        Http2StreamChannel childChannel2 = newOutboundStream(new TestChannelInitializer());
        Assert.assertTrue(childChannel2.isActive());
        Assert.assertFalse(Http2CodecUtil.isStreamIdValid(childChannel2.stream().id()));
        Http2Headers headers1 = new DefaultHttp2Headers();
        Http2Headers headers2 = new DefaultHttp2Headers();
        // Test that streams can be made active (headers sent) in different order than the corresponding channels
        // have been created.
        childChannel2.writeAndFlush(new DefaultHttp2HeadersFrame(headers2));
        childChannel1.writeAndFlush(new DefaultHttp2HeadersFrame(headers1));
        Http2HeadersFrame headersFrame2 = serverLastInboundHandler.blockingReadInbound();
        Assert.assertNotNull(headersFrame2);
        Assert.assertEquals(3, headersFrame2.stream().id());
        Http2HeadersFrame headersFrame1 = serverLastInboundHandler.blockingReadInbound();
        Assert.assertNotNull(headersFrame1);
        Assert.assertEquals(5, headersFrame1.stream().id());
        Assert.assertEquals(3, childChannel2.stream().id());
        Assert.assertEquals(5, childChannel1.stream().id());
        childChannel1.close();
        childChannel2.close();
        serverLastInboundHandler.checkException();
    }

    @Test
    public void createOutboundStream() throws Exception {
        Channel childChannel = newOutboundStream(new TestChannelInitializer());
        Assert.assertTrue(childChannel.isRegistered());
        Assert.assertTrue(childChannel.isActive());
        Http2Headers headers = new DefaultHttp2Headers();
        childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(headers));
        ByteBuf data = Unpooled.buffer(100).writeZero(100);
        childChannel.writeAndFlush(new DefaultHttp2DataFrame(data, true));
        Http2HeadersFrame headersFrame = serverLastInboundHandler.blockingReadInbound();
        Assert.assertNotNull(headersFrame);
        Assert.assertEquals(3, headersFrame.stream().id());
        Assert.assertEquals(headers, headersFrame.headers());
        Http2DataFrame dataFrame = serverLastInboundHandler.blockingReadInbound();
        Assert.assertNotNull(dataFrame);
        Assert.assertEquals(3, dataFrame.stream().id());
        Assert.assertEquals(data.resetReaderIndex(), dataFrame.content());
        Assert.assertTrue(dataFrame.isEndStream());
        dataFrame.release();
        childChannel.close();
        Http2ResetFrame rstFrame = serverLastInboundHandler.blockingReadInbound();
        Assert.assertNotNull(rstFrame);
        Assert.assertEquals(3, rstFrame.stream().id());
        serverLastInboundHandler.checkException();
    }

    @Sharable
    private static class SharableLastInboundHandler extends LastInboundHandler {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelInactive();
        }
    }

    private static class SharableChannelHandler1 extends ChannelHandlerAdapter {
        @Override
        public boolean isSharable() {
            return true;
        }
    }

    @Sharable
    private static class SharableChannelHandler2 extends ChannelHandlerAdapter {}

    private static class UnsharableChannelHandler extends ChannelHandlerAdapter {
        @Override
        public boolean isSharable() {
            return false;
        }
    }

    @Test
    public void testSharableCheck() {
        Assert.assertNotNull(Http2MultiplexCodecBuilder.forServer(new Http2MultiplexCodecBuilderTest.SharableChannelHandler1()));
        Assert.assertNotNull(Http2MultiplexCodecBuilder.forServer(new Http2MultiplexCodecBuilderTest.SharableChannelHandler2()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsharableHandler() {
        Http2MultiplexCodecBuilder.forServer(new Http2MultiplexCodecBuilderTest.UnsharableChannelHandler());
    }
}

