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
package org.jboss.netty.channel.socket;


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
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSocketEchoTest {
    private static final Random random = new Random();

    static final byte[] data = new byte[1048576];

    private static ExecutorService executor;

    static {
        AbstractSocketEchoTest.random.nextBytes(AbstractSocketEchoTest.data);
    }

    @Test
    public void testSimpleEcho() throws Throwable {
        ServerBootstrap sb = new ServerBootstrap(newServerSocketChannelFactory(AbstractSocketEchoTest.executor));
        ClientBootstrap cb = new ClientBootstrap(newClientSocketChannelFactory(AbstractSocketEchoTest.executor));
        AbstractSocketEchoTest.EchoHandler sh = new AbstractSocketEchoTest.EchoHandler();
        AbstractSocketEchoTest.EchoHandler ch = new AbstractSocketEchoTest.EchoHandler();
        sb.getPipeline().addFirst("handler", sh);
        cb.getPipeline().addFirst("handler", ch);
        Channel sc = sb.bind(new InetSocketAddress(0));
        int port = ((InetSocketAddress) (sc.getLocalAddress())).getPort();
        ChannelFuture ccf = cb.connect(new InetSocketAddress(TestUtil.getLocalHost(), port));
        Assert.assertTrue(ccf.awaitUninterruptibly().isSuccess());
        Channel cc = ccf.getChannel();
        for (int i = 0; i < (AbstractSocketEchoTest.data.length);) {
            int length = Math.min(AbstractSocketEchoTest.random.nextInt((1024 * 64)), ((AbstractSocketEchoTest.data.length) - i));
            cc.write(ChannelBuffers.wrappedBuffer(AbstractSocketEchoTest.data, i, length));
            i += length;
        }
        while ((ch.counter) < (AbstractSocketEchoTest.data.length)) {
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
        while ((sh.counter) < (AbstractSocketEchoTest.data.length)) {
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
            byte[] actual = new byte[m.readableBytes()];
            m.getBytes(0, actual);
            int lastIdx = counter;
            for (int i = 0; i < (actual.length); i++) {
                Assert.assertEquals(AbstractSocketEchoTest.data[(i + lastIdx)], actual[i]);
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

