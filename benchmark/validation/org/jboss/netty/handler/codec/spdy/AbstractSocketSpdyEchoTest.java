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
package org.jboss.netty.handler.codec.spdy;


import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSocketSpdyEchoTest {
    private static final Random random = new Random();

    static final int ignoredBytes = 20;

    @Test
    public void testSpdyEcho() throws Throwable {
        for (int version = SPDY_MIN_VERSION; version <= (SPDY_MAX_VERSION); version++) {
            testSpdyEcho(version);
        }
    }

    private static class EchoHandler extends SimpleChannelUpstreamHandler {
        volatile Channel channel;

        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        final ChannelBuffer frames;

        volatile int counter;

        final boolean server;

        EchoHandler(ChannelBuffer frames, boolean server) {
            this.frames = frames;
            this.server = server;
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            channel = e.getChannel();
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            if (server) {
                Channels.write(channel, e.getMessage(), e.getRemoteAddress());
            } else {
                ChannelBuffer m = ((ChannelBuffer) (e.getMessage()));
                byte[] actual = new byte[m.readableBytes()];
                m.getBytes(0, actual);
                int lastIdx = counter;
                for (int i = 0; i < (actual.length); i++) {
                    Assert.assertEquals(frames.getByte((((AbstractSocketSpdyEchoTest.ignoredBytes) + i) + lastIdx)), actual[i]);
                }
                counter += actual.length;
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            if (exception.compareAndSet(null, e.getCause())) {
                e.getChannel().close();
            }
        }
    }
}

