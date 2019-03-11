/**
 * Copyright 2017 The Netty Project
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
package io.netty.handler.codec;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MessageAggregatorTest {
    private static final class ReadCounter extends ChannelOutboundHandlerAdapter {
        int value;

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            (value)++;
            ctx.read();
        }
    }

    abstract static class MockMessageAggregator extends MessageAggregator<ByteBufHolder, ByteBufHolder, ByteBufHolder, ByteBufHolder> {
        protected MockMessageAggregator() {
            super(1024);
        }

        @Override
        protected ByteBufHolder beginAggregation(ByteBufHolder start, ByteBuf content) throws Exception {
            return start.replace(content);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadFlowManagement() throws Exception {
        MessageAggregatorTest.ReadCounter counter = new MessageAggregatorTest.ReadCounter();
        ByteBufHolder first = MessageAggregatorTest.message("first");
        ByteBufHolder chunk = MessageAggregatorTest.message("chunk");
        ByteBufHolder last = MessageAggregatorTest.message("last");
        MessageAggregatorTest.MockMessageAggregator agg = Mockito.spy(MessageAggregatorTest.MockMessageAggregator.class);
        Mockito.when(agg.isStartMessage(first)).thenReturn(true);
        Mockito.when(agg.isContentMessage(chunk)).thenReturn(true);
        Mockito.when(agg.isContentMessage(last)).thenReturn(true);
        Mockito.when(agg.isLastContentMessage(last)).thenReturn(true);
        EmbeddedChannel embedded = new EmbeddedChannel(counter, agg);
        embedded.config().setAutoRead(false);
        Assert.assertFalse(embedded.writeInbound(first));
        Assert.assertFalse(embedded.writeInbound(chunk));
        Assert.assertTrue(embedded.writeInbound(last));
        Assert.assertEquals(3, counter.value);// 2 reads issued from MockMessageAggregator

        // 1 read issued from EmbeddedChannel constructor
        ByteBufHolder all = new io.netty.buffer.DefaultByteBufHolder(Unpooled.wrappedBuffer(first.content().retain(), chunk.content().retain(), last.content().retain()));
        ByteBufHolder out = embedded.readInbound();
        Assert.assertEquals(all, out);
        Assert.assertTrue(((all.release()) && (out.release())));
        Assert.assertFalse(embedded.finish());
    }
}

