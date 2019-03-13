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
package org.jboss.netty.handler.codec.frame;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSocketFixedLengthEchoTest {
    private static final Random random = new Random();

    static final byte[] data = new byte[1048576];

    private static ExecutorService executor;

    static {
        AbstractSocketFixedLengthEchoTest.random.nextBytes(AbstractSocketFixedLengthEchoTest.data);
    }

    @Test
    public void testFixedLengthEcho() throws Throwable {
        ServerBootstrap sb = new ServerBootstrap(newServerSocketChannelFactory(AbstractSocketFixedLengthEchoTest.executor));
        ClientBootstrap cb = new ClientBootstrap(newClientSocketChannelFactory(AbstractSocketFixedLengthEchoTest.executor));
        AbstractSocketFixedLengthEchoTest.EchoHandler sh = new AbstractSocketFixedLengthEchoTest.EchoHandler();
        AbstractSocketFixedLengthEchoTest.EchoHandler ch = new AbstractSocketFixedLengthEchoTest.EchoHandler();
        sb.getPipeline().addLast("decoder", new FixedLengthFrameDecoder(1024));
        sb.getPipeline().addAfter("decoder", "handler", sh);
        cb.getPipeline().addLast("decoder", new FixedLengthFrameDecoder(1024));
        cb.getPipeline().addAfter("decoder", "handler", ch);
        Channel sc = sb.bind(new InetSocketAddress(0));
        int port = ((InetSocketAddress) (sc.getLocalAddress())).getPort();
        Channel cc = cb.connect(new InetSocketAddress(TestUtil.getLocalHost(), port)).syncUninterruptibly().getChannel();
        for (int i = 0; i < (AbstractSocketFixedLengthEchoTest.data.length);) {
            int length = Math.min(AbstractSocketFixedLengthEchoTest.random.nextInt((1024 * 3)), ((AbstractSocketFixedLengthEchoTest.data.length) - i));
            cc.write(ChannelBuffers.wrappedBuffer(AbstractSocketFixedLengthEchoTest.data, i, length));
            i += length;
        }
        while ((ch.counter) < (AbstractSocketFixedLengthEchoTest.data.length)) {
            if ((sh.exception.get()) != null) {
                break;
            }
            if ((ch.exception.get()) != null) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // Ignore.
            }
        } 
        while ((sh.counter) < (AbstractSocketFixedLengthEchoTest.data.length)) {
            if ((sh.exception.get()) != null) {
                break;
            }
            if ((ch.exception.get()) != null) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // Ignore.
            }
        } 
        sh.channel.close().awaitUninterruptibly();
        ch.channel.close().awaitUninterruptibly();
        sc.close().awaitUninterruptibly();
        cb.shutdown();
        sb.shutdown();
        cb.releaseExternalResources();
        sb.releaseExternalResources();
        if (((sh.exception.get()) != null) && (!((sh.exception.get()) instanceof IOException))) {
            throw sh.exception.get();
        }
        if (((ch.exception.get()) != null) && (!((ch.exception.get()) instanceof IOException))) {
            throw ch.exception.get();
        }
        if ((sh.exception.get()) != null) {
            throw sh.exception.get();
        }
        if ((ch.exception.get()) != null) {
            throw ch.exception.get();
        }
    }

    private static class EchoHandler extends SimpleChannelUpstreamHandler {
        volatile Channel channel;

        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        volatile int counter;

        EchoHandler() {
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            channel = e.getChannel();
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            ChannelBuffer m = ((ChannelBuffer) (e.getMessage()));
            Assert.assertEquals(1024, m.readableBytes());
            byte[] actual = new byte[m.readableBytes()];
            m.getBytes(0, actual);
            int lastIdx = counter;
            for (int i = 0; i < (actual.length); i++) {
                Assert.assertEquals(AbstractSocketFixedLengthEchoTest.data[(i + lastIdx)], actual[i]);
            }
            if ((channel.getParent()) != null) {
                channel.write(m);
            }
            counter += actual.length;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            if (exception.compareAndSet(null, e.getCause())) {
                e.getChannel().close();
            }
        }
    }
}

