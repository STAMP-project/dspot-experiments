/**
 * Copyright 2013 The Netty Project
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
package io.netty.handler.ssl;


import ByteBufAllocator.DEFAULT;
import InsecureTrustManagerFactory.INSTANCE;
import SslProvider.JDK;
import SslProvider.OPENSSL;
import SslUtils.PROTOCOL_SSL_V3;
import Unpooled.EMPTY_BUFFER;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Promise;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class SslHandlerTest {
    @Test
    public void testNoSslHandshakeEventWhenNoHandshake() throws Exception {
        final AtomicBoolean inActive = new AtomicBoolean(false);
        SSLEngine engine = SSLContext.getDefault().createSSLEngine();
        EmbeddedChannel ch = new EmbeddedChannel(DefaultChannelId.newInstance(), false, false, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ctx.close();
            }
        }, new SslHandler(engine) {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                inActive.set(true);
                super.handlerAdded(ctx);
                inActive.set(false);
            }
        }, new ChannelInboundHandlerAdapter() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof SslHandshakeCompletionEvent) {
                    throw ((Exception) (cause()));
                }
            }
        }) {
            @Override
            public boolean isActive() {
                return (!(inActive.get())) && (super.isActive());
            }
        };
        ch.register();
        Assert.assertFalse(ch.finishAndReleaseAll());
    }

    @Test(expected = SSLException.class, timeout = 3000)
    public void testClientHandshakeTimeout() throws Exception {
        SslHandlerTest.testHandshakeTimeout(true);
    }

    @Test(expected = SSLException.class, timeout = 3000)
    public void testServerHandshakeTimeout() throws Exception {
        SslHandlerTest.testHandshakeTimeout(false);
    }

    @Test
    public void testTruncatedPacket() throws Exception {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        EmbeddedChannel ch = new EmbeddedChannel(new SslHandler(engine));
        // Push the first part of a 5-byte handshake message.
        ch.writeInbound(wrappedBuffer(new byte[]{ 22, 3, 1, 0, 5 }));
        // Should decode nothing yet.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        try {
            // Push the second part of the 5-byte handshake message.
            ch.writeInbound(wrappedBuffer(new byte[]{ 2, 0, 0, 1, 0 }));
            Assert.fail();
        } catch (DecoderException e) {
            // Be sure we cleanup the channel and release any pending messages that may have been generated because
            // of an alert.
            // See https://github.com/netty/netty/issues/6057.
            ch.finishAndReleaseAll();
            // The pushed message is invalid, so it should raise an exception if it decoded the message correctly.
            Assert.assertThat(e.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(SSLProtocolException.class)));
        }
    }

    @Test
    public void testNonByteBufWriteIsReleased() throws Exception {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        EmbeddedChannel ch = new EmbeddedChannel(new SslHandler(engine));
        AbstractReferenceCounted referenceCounted = new AbstractReferenceCounted() {
            @Override
            public ReferenceCounted touch(Object hint) {
                return this;
            }

            @Override
            protected void deallocate() {
            }
        };
        try {
            ch.write(referenceCounted).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(UnsupportedMessageTypeException.class)));
        }
        Assert.assertEquals(0, referenceCounted.refCnt());
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test(expected = UnsupportedMessageTypeException.class)
    public void testNonByteBufNotPassThrough() throws Exception {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        EmbeddedChannel ch = new EmbeddedChannel(new SslHandler(engine));
        try {
            ch.writeOutbound(new Object());
        } finally {
            ch.finishAndReleaseAll();
        }
    }

    @Test
    public void testIncompleteWriteDoesNotCompletePromisePrematurely() throws NoSuchAlgorithmException {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        EmbeddedChannel ch = new EmbeddedChannel(new SslHandler(engine));
        ChannelPromise promise = ch.newPromise();
        ByteBuf buf = Unpooled.buffer(10).writeZero(10);
        ch.writeAndFlush(buf, promise);
        Assert.assertFalse(promise.isDone());
        Assert.assertTrue(ch.finishAndReleaseAll());
        Assert.assertTrue(promise.isDone());
        Assert.assertThat(promise.cause(), CoreMatchers.is(CoreMatchers.instanceOf(SSLException.class)));
    }

    @Test
    public void testReleaseSslEngine() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SelfSignedCertificate cert = new SelfSignedCertificate();
        try {
            SslContext sslContext = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(OPENSSL).build();
            try {
                SSLEngine sslEngine = sslContext.newEngine(DEFAULT);
                EmbeddedChannel ch = new EmbeddedChannel(new SslHandler(sslEngine));
                Assert.assertEquals(1, refCnt());
                Assert.assertEquals(1, refCnt());
                Assert.assertTrue(ch.finishAndReleaseAll());
                ch.close().syncUninterruptibly();
                Assert.assertEquals(1, refCnt());
                Assert.assertEquals(0, refCnt());
            } finally {
                ReferenceCountUtil.release(sslContext);
            }
        } finally {
            cert.delete();
        }
    }

    private static final class TlsReadTest extends ChannelOutboundHandlerAdapter {
        private volatile boolean readIssued;

        @Override
        public void read(ChannelHandlerContext ctx) throws Exception {
            readIssued = true;
            super.read(ctx);
        }

        public void test(final boolean dropChannelActive) throws Exception {
            SSLEngine engine = SSLContext.getDefault().createSSLEngine();
            engine.setUseClientMode(true);
            EmbeddedChannel ch = new EmbeddedChannel(false, false, this, new SslHandler(engine), new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    if (!dropChannelActive) {
                        ctx.fireChannelActive();
                    }
                }
            });
            ch.config().setAutoRead(false);
            Assert.assertFalse(ch.config().isAutoRead());
            ch.register();
            Assert.assertTrue(readIssued);
            readIssued = false;
            Assert.assertTrue(ch.writeOutbound(EMPTY_BUFFER));
            Assert.assertTrue(readIssued);
            Assert.assertTrue(ch.finishAndReleaseAll());
        }
    }

    @Test
    public void testIssueReadAfterActiveWriteFlush() throws Exception {
        // the handshake is initiated by channelActive
        new SslHandlerTest.TlsReadTest().test(false);
    }

    @Test
    public void testIssueReadAfterWriteFlushActive() throws Exception {
        // the handshake is initiated by flush
        new SslHandlerTest.TlsReadTest().test(true);
    }

    @Test(timeout = 30000)
    public void testRemoval() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel sc = null;
        Channel cc = null;
        try {
            final Promise<Void> clientPromise = group.next().newPromise();
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(SslHandlerTest.newHandler(SslContextBuilder.forClient().trustManager(INSTANCE).build(), clientPromise));
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            final Promise<Void> serverPromise = group.next().newPromise();
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(group, group).channel(NioServerSocketChannel.class).childHandler(SslHandlerTest.newHandler(SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build(), serverPromise));
            sc = serverBootstrap.bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
            cc = bootstrap.connect(sc.localAddress()).syncUninterruptibly().channel();
            serverPromise.syncUninterruptibly();
            clientPromise.syncUninterruptibly();
        } finally {
            if (cc != null) {
                cc.close().syncUninterruptibly();
            }
            if (sc != null) {
                sc.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
        }
    }

    @Test
    public void testCloseFutureNotified() throws Exception {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        SslHandler handler = new SslHandler(engine);
        EmbeddedChannel ch = new EmbeddedChannel(handler);
        ch.close();
        // When the channel is closed the SslHandler will write an empty buffer to the channel.
        ByteBuf buf = ch.readOutbound();
        Assert.assertFalse(buf.isReadable());
        buf.release();
        Assert.assertFalse(ch.finishAndReleaseAll());
        Assert.assertTrue(((cause()) instanceof ClosedChannelException));
        Assert.assertTrue(((cause()) instanceof ClosedChannelException));
    }

    @Test(timeout = 5000)
    public void testEventsFired() throws Exception {
        SSLEngine engine = SslHandlerTest.newServerModeSSLEngine();
        final BlockingQueue<SslCompletionEvent> events = new LinkedBlockingQueue<SslCompletionEvent>();
        EmbeddedChannel channel = new EmbeddedChannel(new SslHandler(engine), new ChannelInboundHandlerAdapter() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof SslCompletionEvent) {
                    events.add(((SslCompletionEvent) (evt)));
                }
            }
        });
        Assert.assertTrue(events.isEmpty());
        Assert.assertTrue(channel.finishAndReleaseAll());
        SslCompletionEvent evt = events.take();
        Assert.assertTrue((evt instanceof SslHandshakeCompletionEvent));
        Assert.assertTrue(((evt.cause()) instanceof ClosedChannelException));
        evt = events.take();
        Assert.assertTrue((evt instanceof SslCloseCompletionEvent));
        Assert.assertTrue(((evt.cause()) instanceof ClosedChannelException));
        Assert.assertTrue(events.isEmpty());
    }

    @Test(timeout = 5000)
    public void testHandshakeFailBeforeWritePromise() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SslContext sslServerCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        final CountDownLatch latch = new CountDownLatch(2);
        final CountDownLatch latch2 = new CountDownLatch(2);
        final BlockingQueue<Object> events = new LinkedBlockingQueue<Object>();
        Channel serverChannel = null;
        Channel clientChannel = null;
        EventLoopGroup group = new DefaultEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(sslServerCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
                            ByteBuf buf = ctx.alloc().buffer(10);
                            buf.writeZero(buf.capacity());
                            ctx.writeAndFlush(buf).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    events.add(future);
                                    latch.countDown();
                                }
                            });
                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                            if (evt instanceof SslCompletionEvent) {
                                events.add(evt);
                                latch.countDown();
                                latch2.countDown();
                            }
                        }
                    });
                }
            });
            Bootstrap cb = new Bootstrap();
            cb.group(group).channel(LocalChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
                            ByteBuf buf = ctx.alloc().buffer(1000);
                            buf.writeZero(buf.capacity());
                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });
            serverChannel = sb.bind(new LocalAddress("SslHandlerTest")).sync().channel();
            clientChannel = cb.connect(serverChannel.localAddress()).sync().channel();
            latch.await();
            SslCompletionEvent evt = ((SslCompletionEvent) (events.take()));
            Assert.assertTrue((evt instanceof SslHandshakeCompletionEvent));
            Assert.assertThat(evt.cause(), CoreMatchers.is(CoreMatchers.instanceOf(SSLException.class)));
            ChannelFuture future = ((ChannelFuture) (events.take()));
            Assert.assertThat(future.cause(), CoreMatchers.is(CoreMatchers.instanceOf(SSLException.class)));
            serverChannel.close().sync();
            serverChannel = null;
            clientChannel.close().sync();
            clientChannel = null;
            latch2.await();
            evt = ((SslCompletionEvent) (events.take()));
            Assert.assertTrue((evt instanceof SslCloseCompletionEvent));
            Assert.assertThat(evt.cause(), CoreMatchers.is(CoreMatchers.instanceOf(ClosedChannelException.class)));
            Assert.assertTrue(events.isEmpty());
        } finally {
            if (serverChannel != null) {
                serverChannel.close();
            }
            if (clientChannel != null) {
                clientChannel.close();
            }
            group.shutdownGracefully();
        }
    }

    @Test
    public void writingReadOnlyBufferDoesNotBreakAggregation() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SslContext sslServerCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        final SslContext sslClientCtx = SslContextBuilder.forClient().trustManager(INSTANCE).build();
        EventLoopGroup group = new NioEventLoopGroup();
        Channel sc = null;
        Channel cc = null;
        final CountDownLatch serverReceiveLatch = new CountDownLatch(1);
        try {
            final int expectedBytes = 11;
            sc = new ServerBootstrap().group(group).channel(NioServerSocketChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslServerCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler<ByteBuf>() {
                        private int readBytes;

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                            readBytes += msg.readableBytes();
                            if ((readBytes) >= expectedBytes) {
                                serverReceiveLatch.countDown();
                            }
                        }
                    });
                }
            }).bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
            cc = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslClientCtx.newHandler(ch.alloc()));
                }
            }).connect(sc.localAddress()).syncUninterruptibly().channel();
            // We first write a ReadOnlyBuffer because SslHandler will attempt to take the first buffer and append to it
            // until there is no room, or the aggregation size threshold is exceeded. We want to verify that we don't
            // throw when a ReadOnlyBuffer is used and just verify that we don't aggregate in this case.
            ByteBuf firstBuffer = Unpooled.buffer(10);
            firstBuffer.writeByte(0);
            firstBuffer = firstBuffer.asReadOnly();
            ByteBuf secondBuffer = Unpooled.buffer(10);
            secondBuffer.writeZero(secondBuffer.capacity());
            cc.write(firstBuffer);
            cc.writeAndFlush(secondBuffer).syncUninterruptibly();
            serverReceiveLatch.countDown();
        } finally {
            if (cc != null) {
                cc.close().syncUninterruptibly();
            }
            if (sc != null) {
                sc.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
            ReferenceCountUtil.release(sslServerCtx);
            ReferenceCountUtil.release(sslClientCtx);
        }
    }

    @Test(timeout = 10000)
    public void testCloseOnHandshakeFailure() throws Exception {
        final SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SslContext sslServerCtx = SslContextBuilder.forServer(ssc.key(), ssc.cert()).build();
        final SslContext sslClientCtx = SslContextBuilder.forClient().trustManager(new SelfSignedCertificate().cert()).build();
        EventLoopGroup group = new NioEventLoopGroup(1);
        Channel sc = null;
        Channel cc = null;
        try {
            LocalAddress address = new LocalAddress(((getClass().getSimpleName()) + ".testCloseOnHandshakeFailure"));
            ServerBootstrap sb = new ServerBootstrap().group(group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(sslServerCtx.newHandler(ch.alloc()));
                }
            });
            sc = sb.bind(address).syncUninterruptibly().channel();
            final AtomicReference<SslHandler> sslHandlerRef = new AtomicReference<SslHandler>();
            Bootstrap b = new Bootstrap().group(group).channel(LocalChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    SslHandler handler = sslClientCtx.newHandler(ch.alloc());
                    // We propagate the SslHandler via an AtomicReference to the outer-scope as using
                    // pipeline.get(...) may return null if the pipeline was teared down by the time we call it.
                    // This will happen if the channel was closed in the meantime.
                    sslHandlerRef.set(handler);
                    ch.pipeline().addLast(handler);
                }
            });
            cc = b.connect(sc.localAddress()).syncUninterruptibly().channel();
            SslHandler handler = sslHandlerRef.get();
            handler.handshakeFuture().awaitUninterruptibly();
            Assert.assertFalse(handler.handshakeFuture().isSuccess());
            cc.closeFuture().syncUninterruptibly();
        } finally {
            if (cc != null) {
                cc.close().syncUninterruptibly();
            }
            if (sc != null) {
                sc.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
            ReferenceCountUtil.release(sslServerCtx);
            ReferenceCountUtil.release(sslClientCtx);
        }
    }

    @Test
    public void testOutboundClosedAfterChannelInactive() throws Exception {
        SslContext context = SslContextBuilder.forClient().build();
        SSLEngine engine = context.newEngine(UnpooledByteBufAllocator.DEFAULT);
        EmbeddedChannel channel = new EmbeddedChannel();
        Assert.assertFalse(channel.finish());
        channel.pipeline().addLast(new SslHandler(engine));
        Assert.assertFalse(engine.isOutboundDone());
        channel.close().syncUninterruptibly();
        Assert.assertTrue(engine.isOutboundDone());
    }

    @Test(timeout = 10000)
    public void testHandshakeFailedByWriteBeforeChannelActive() throws Exception {
        final SslContext sslClientCtx = SslContextBuilder.forClient().protocols(PROTOCOL_SSL_V3).trustManager(INSTANCE).sslProvider(JDK).build();
        EventLoopGroup group = new NioEventLoopGroup();
        Channel sc = null;
        Channel cc = null;
        final CountDownLatch activeLatch = new CountDownLatch(1);
        final AtomicReference<AssertionError> errorRef = new AtomicReference<AssertionError>();
        final SslHandler sslHandler = sslClientCtx.newHandler(UnpooledByteBufAllocator.DEFAULT);
        try {
            sc = new ServerBootstrap().group(group).channel(NioServerSocketChannel.class).childHandler(new ChannelInboundHandlerAdapter()).bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
            cc = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslHandler);
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            if (cause instanceof AssertionError) {
                                errorRef.set(((AssertionError) (cause)));
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            activeLatch.countDown();
                        }
                    });
                }
            }).connect(sc.localAddress()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    // Write something to trigger the handshake before fireChannelActive is called.
                    future.channel().writeAndFlush(wrappedBuffer(new byte[]{ 1, 2, 3, 4 }));
                }
            }).syncUninterruptibly().channel();
            // Ensure there is no AssertionError thrown by having the handshake failed by the writeAndFlush(...) before
            // channelActive(...) was called. Let's first wait for the activeLatch countdown to happen and after this
            // check if we saw and AssertionError (even if we timed out waiting).
            activeLatch.await(5, TimeUnit.SECONDS);
            AssertionError error = errorRef.get();
            if (error != null) {
                throw error;
            }
            Assert.assertThat(cause(), CoreMatchers.<Throwable>instanceOf(SSLException.class));
        } finally {
            if (cc != null) {
                cc.close().syncUninterruptibly();
            }
            if (sc != null) {
                sc.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
            ReferenceCountUtil.release(sslClientCtx);
        }
    }

    @Test(timeout = 10000)
    public void testHandshakeTimeoutFlushStartsHandshake() throws Exception {
        SslHandlerTest.testHandshakeTimeout0(false);
    }

    @Test(timeout = 10000)
    public void testHandshakeTimeoutStartTLS() throws Exception {
        SslHandlerTest.testHandshakeTimeout0(true);
    }

    @Test
    public void testHandshakeWithExecutorThatExecuteDirecty() throws Exception {
        testHandshakeWithExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }

    @Test
    public void testHandshakeWithImmediateExecutor() throws Exception {
        testHandshakeWithExecutor(ImmediateExecutor.INSTANCE);
    }

    @Test
    public void testHandshakeWithImmediateEventExecutor() throws Exception {
        testHandshakeWithExecutor(ImmediateEventExecutor.INSTANCE);
    }

    @Test
    public void testHandshakeWithExecutor() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            testHandshakeWithExecutor(executorService);
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testClientHandshakeTimeoutBecauseExecutorNotExecute() throws Exception {
        testHandshakeTimeoutBecauseExecutorNotExecute(true);
    }

    @Test
    public void testServerHandshakeTimeoutBecauseExecutorNotExecute() throws Exception {
        testHandshakeTimeoutBecauseExecutorNotExecute(false);
    }
}

