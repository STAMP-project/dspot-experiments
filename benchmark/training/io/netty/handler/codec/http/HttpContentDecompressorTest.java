/**
 * Copyright 2019 The Netty Project
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


import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class HttpContentDecompressorTest {
    // See https://github.com/netty/netty/issues/8915.
    @Test
    public void testInvokeReadWhenNotProduceMessage() {
        final AtomicInteger readCalled = new AtomicInteger();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void read(ChannelHandlerContext ctx) {
                readCalled.incrementAndGet();
                ctx.read();
            }
        }, new HttpContentDecompressor(), new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ctx.fireChannelRead(msg);
                ctx.read();
            }
        });
        channel.config().setAutoRead(false);
        readCalled.set(0);
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_ENCODING, "gzip");
        response.headers().set(CONTENT_TYPE, "application/json;charset=UTF-8");
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        Assert.assertTrue(channel.writeInbound(response));
        // we triggered read explicitly
        Assert.assertEquals(1, readCalled.get());
        Assert.assertTrue(((channel.readInbound()) instanceof HttpResponse));
        Assert.assertFalse(channel.writeInbound(new DefaultHttpContent(Unpooled.EMPTY_BUFFER)));
        // read was triggered by the HttpContentDecompressor itself as it did not produce any message to the next
        // inbound handler.
        Assert.assertEquals(2, readCalled.get());
        Assert.assertFalse(channel.finishAndReleaseAll());
    }
}

