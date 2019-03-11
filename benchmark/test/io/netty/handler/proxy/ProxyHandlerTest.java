/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.proxy;


import ChannelOption.AUTO_READ;
import CharsetUtil.US_ASCII;
import EmptyArrays.EMPTY_OBJECTS;
import InsecureTrustManagerFactory.INSTANCE;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ProxyHandlerTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHandlerTest.class);

    private static final InetSocketAddress DESTINATION = InetSocketAddress.createUnresolved("destination.com", 42);

    private static final InetSocketAddress BAD_DESTINATION = SocketUtils.socketAddress("1.2.3.4", 5);

    private static final String USERNAME = "testUser";

    private static final String PASSWORD = "testPassword";

    private static final String BAD_USERNAME = "badUser";

    private static final String BAD_PASSWORD = "badPassword";

    static final EventLoopGroup group = new io.netty.channel.nio.NioEventLoopGroup(3, new DefaultThreadFactory("proxy", true));

    static final SslContext serverSslCtx;

    static final SslContext clientSslCtx;

    static {
        SslContext sctx;
        SslContext cctx;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sctx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            cctx = SslContextBuilder.forClient().trustManager(INSTANCE).build();
        } catch (Exception e) {
            throw new Error(e);
        }
        serverSslCtx = sctx;
        clientSslCtx = cctx;
    }

    static final ProxyServer deadHttpProxy = new HttpProxyServer(false, TestMode.UNRESPONSIVE, null);

    static final ProxyServer interHttpProxy = new HttpProxyServer(false, TestMode.INTERMEDIARY, null);

    static final ProxyServer anonHttpProxy = new HttpProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION);

    static final ProxyServer httpProxy = new HttpProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION, ProxyHandlerTest.USERNAME, ProxyHandlerTest.PASSWORD);

    static final ProxyServer deadHttpsProxy = new HttpProxyServer(true, TestMode.UNRESPONSIVE, null);

    static final ProxyServer interHttpsProxy = new HttpProxyServer(true, TestMode.INTERMEDIARY, null);

    static final ProxyServer anonHttpsProxy = new HttpProxyServer(true, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION);

    static final ProxyServer httpsProxy = new HttpProxyServer(true, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION, ProxyHandlerTest.USERNAME, ProxyHandlerTest.PASSWORD);

    static final ProxyServer deadSocks4Proxy = new Socks4ProxyServer(false, TestMode.UNRESPONSIVE, null);

    static final ProxyServer interSocks4Proxy = new Socks4ProxyServer(false, TestMode.INTERMEDIARY, null);

    static final ProxyServer anonSocks4Proxy = new Socks4ProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION);

    static final ProxyServer socks4Proxy = new Socks4ProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION, ProxyHandlerTest.USERNAME);

    static final ProxyServer deadSocks5Proxy = new Socks5ProxyServer(false, TestMode.UNRESPONSIVE, null);

    static final ProxyServer interSocks5Proxy = new Socks5ProxyServer(false, TestMode.INTERMEDIARY, null);

    static final ProxyServer anonSocks5Proxy = new Socks5ProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION);

    static final ProxyServer socks5Proxy = new Socks5ProxyServer(false, TestMode.TERMINAL, ProxyHandlerTest.DESTINATION, ProxyHandlerTest.USERNAME, ProxyHandlerTest.PASSWORD);

    private static final Collection<ProxyServer> allProxies = Arrays.asList(ProxyHandlerTest.deadHttpProxy, ProxyHandlerTest.interHttpProxy, ProxyHandlerTest.anonHttpProxy, ProxyHandlerTest.httpProxy, ProxyHandlerTest.deadHttpsProxy, ProxyHandlerTest.interHttpsProxy, ProxyHandlerTest.anonHttpsProxy, ProxyHandlerTest.httpsProxy, ProxyHandlerTest.deadSocks4Proxy, ProxyHandlerTest.interSocks4Proxy, ProxyHandlerTest.anonSocks4Proxy, ProxyHandlerTest.socks4Proxy, ProxyHandlerTest.deadSocks5Proxy, ProxyHandlerTest.interSocks5Proxy, ProxyHandlerTest.anonSocks5Proxy, ProxyHandlerTest.socks5Proxy);

    // set to non-zero value in case you need predictable shuffling of test cases
    // look for "Seed used: *" debug message in test logs
    private static final long reproducibleSeed = 0L;

    private final ProxyHandlerTest.TestItem testItem;

    public ProxyHandlerTest(ProxyHandlerTest.TestItem testItem) {
        this.testItem = testItem;
    }

    @Test
    public void test() throws Exception {
        testItem.test();
    }

    private static final class SuccessTestHandler extends SimpleChannelInboundHandler<Object> {
        final Queue<String> received = new LinkedBlockingQueue<String>();

        final Queue<Throwable> exceptions = new LinkedBlockingQueue<Throwable>();

        volatile int eventCount;

        private static void readIfNeeded(ChannelHandlerContext ctx) {
            if (!(ctx.channel().config().isAutoRead())) {
                ctx.read();
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("A\n", US_ASCII));
            ProxyHandlerTest.SuccessTestHandler.readIfNeeded(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof ProxyConnectionEvent) {
                (eventCount)++;
                if ((eventCount) == 1) {
                    // Note that ProxyConnectionEvent can be triggered multiple times when there are multiple
                    // ProxyHandlers in the pipeline.  Therefore, we send the 'B' message only on the first event.
                    ctx.writeAndFlush(Unpooled.copiedBuffer("B\n", US_ASCII));
                }
                ProxyHandlerTest.SuccessTestHandler.readIfNeeded(ctx);
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            String str = ((ByteBuf) (msg)).toString(US_ASCII);
            received.add(str);
            if ("2".equals(str)) {
                ctx.writeAndFlush(Unpooled.copiedBuffer("C\n", US_ASCII));
            }
            ProxyHandlerTest.SuccessTestHandler.readIfNeeded(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exceptions.add(cause);
            ctx.close();
        }
    }

    private static final class FailureTestHandler extends SimpleChannelInboundHandler<Object> {
        final Queue<Throwable> exceptions = new LinkedBlockingQueue<Throwable>();

        /**
         * A latch that counts down when:
         * - a pending write attempt in {@link #channelActive(ChannelHandlerContext)} finishes, or
         * - the channel is closed.
         * By waiting until the latch goes down to 0, we can make sure all assertion failures related with all write
         * attempts have been recorded.
         */
        final CountDownLatch latch = new CountDownLatch(2);

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("A\n", US_ASCII)).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    latch.countDown();
                    if (!((future.cause()) instanceof ProxyConnectException)) {
                        exceptions.add(new AssertionError(("Unexpected failure cause for initial write: " + (future.cause()))));
                    }
                }
            });
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            latch.countDown();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof ProxyConnectionEvent) {
                Assert.fail(("Unexpected event: " + evt));
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Assert.fail(("Unexpected message: " + msg));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exceptions.add(cause);
            ctx.close();
        }
    }

    private abstract static class TestItem {
        final String name;

        final InetSocketAddress destination;

        final ChannelHandler[] clientHandlers;

        protected TestItem(String name, InetSocketAddress destination, ChannelHandler... clientHandlers) {
            this.name = name;
            this.destination = destination;
            this.clientHandlers = clientHandlers;
        }

        abstract void test() throws Exception;

        protected void assertProxyHandlers(boolean success) {
            for (ChannelHandler h : clientHandlers) {
                if (h instanceof ProxyHandler) {
                    ProxyHandler ph = ((ProxyHandler) (h));
                    String type = StringUtil.simpleClassName(ph);
                    Future<Channel> f = ph.connectFuture();
                    if (!(f.isDone())) {
                        ProxyHandlerTest.logger.warn("{}: not done", type);
                    } else
                        if (f.isSuccess()) {
                            if (success) {
                                ProxyHandlerTest.logger.debug("{}: success", type);
                            } else {
                                ProxyHandlerTest.logger.warn("{}: success", type);
                            }
                        } else {
                            if (success) {
                                ProxyHandlerTest.logger.warn("{}: failure", type, f.cause());
                            } else {
                                ProxyHandlerTest.logger.debug("{}: failure", type, f.cause());
                            }
                        }

                }
            }
            for (ChannelHandler h : clientHandlers) {
                if (h instanceof ProxyHandler) {
                    ProxyHandler ph = ((ProxyHandler) (h));
                    Assert.assertThat(ph.connectFuture().isDone(), CoreMatchers.is(true));
                    Assert.assertThat(ph.connectFuture().isSuccess(), CoreMatchers.is(success));
                }
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class SuccessTestItem extends ProxyHandlerTest.TestItem {
        private final int expectedEventCount;

        // Probably we need to be more flexible here and as for the configuration map,
        // not a single key. But as far as it works for now, I'm leaving the impl.
        // as is, in case we need to cover more cases (like, AUTO_CLOSE, TCP_NODELAY etc)
        // feel free to replace this boolean with either config or method to setup bootstrap
        private final boolean autoRead;

        SuccessTestItem(String name, InetSocketAddress destination, boolean autoRead, ChannelHandler... clientHandlers) {
            super(name, destination, clientHandlers);
            int expectedEventCount = 0;
            for (ChannelHandler h : clientHandlers) {
                if (h instanceof ProxyHandler) {
                    expectedEventCount++;
                }
            }
            this.expectedEventCount = expectedEventCount;
            this.autoRead = autoRead;
        }

        @Override
        protected void test() throws Exception {
            final ProxyHandlerTest.SuccessTestHandler testHandler = new ProxyHandlerTest.SuccessTestHandler();
            Bootstrap b = new Bootstrap();
            b.group(ProxyHandlerTest.group);
            b.channel(NioSocketChannel.class);
            b.option(AUTO_READ, this.autoRead);
            b.resolver(NoopAddressResolverGroup.INSTANCE);
            b.handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(clientHandlers);
                    p.addLast(new LineBasedFrameDecoder(64));
                    p.addLast(testHandler);
                }
            });
            boolean finished = b.connect(destination).channel().closeFuture().await(10, TimeUnit.SECONDS);
            ProxyHandlerTest.logger.debug("Received messages: {}", testHandler.received);
            if (testHandler.exceptions.isEmpty()) {
                ProxyHandlerTest.logger.debug("No recorded exceptions on the client side.");
            } else {
                for (Throwable t : testHandler.exceptions) {
                    ProxyHandlerTest.logger.debug("Recorded exception on the client side: {}", t);
                }
            }
            assertProxyHandlers(true);
            Assert.assertThat(testHandler.received.toArray(), CoreMatchers.is(new Object[]{ "0", "1", "2", "3" }));
            Assert.assertThat(testHandler.exceptions.toArray(), CoreMatchers.is(EMPTY_OBJECTS));
            Assert.assertThat(testHandler.eventCount, CoreMatchers.is(expectedEventCount));
            Assert.assertThat(finished, CoreMatchers.is(true));
        }
    }

    private static final class FailureTestItem extends ProxyHandlerTest.TestItem {
        private final String expectedMessage;

        FailureTestItem(String name, InetSocketAddress destination, String expectedMessage, ChannelHandler... clientHandlers) {
            super(name, destination, clientHandlers);
            this.expectedMessage = expectedMessage;
        }

        @Override
        protected void test() throws Exception {
            final ProxyHandlerTest.FailureTestHandler testHandler = new ProxyHandlerTest.FailureTestHandler();
            Bootstrap b = new Bootstrap();
            b.group(ProxyHandlerTest.group);
            b.channel(NioSocketChannel.class);
            b.resolver(NoopAddressResolverGroup.INSTANCE);
            b.handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(clientHandlers);
                    p.addLast(new LineBasedFrameDecoder(64));
                    p.addLast(testHandler);
                }
            });
            boolean finished = b.connect(destination).channel().closeFuture().await(10, TimeUnit.SECONDS);
            finished &= testHandler.latch.await(10, TimeUnit.SECONDS);
            ProxyHandlerTest.logger.debug("Recorded exceptions: {}", testHandler.exceptions);
            assertProxyHandlers(false);
            Assert.assertThat(testHandler.exceptions.size(), CoreMatchers.is(1));
            Throwable e = testHandler.exceptions.poll();
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(ProxyConnectException.class)));
            Assert.assertThat(String.valueOf(e), CoreMatchers.containsString(expectedMessage));
            Assert.assertThat(finished, CoreMatchers.is(true));
        }
    }

    private static final class TimeoutTestItem extends ProxyHandlerTest.TestItem {
        TimeoutTestItem(String name, ChannelHandler... clientHandlers) {
            super(name, null, clientHandlers);
        }

        @Override
        protected void test() throws Exception {
            final long TIMEOUT = 2000;
            for (ChannelHandler h : clientHandlers) {
                if (h instanceof ProxyHandler) {
                    setConnectTimeoutMillis(TIMEOUT);
                }
            }
            final ProxyHandlerTest.FailureTestHandler testHandler = new ProxyHandlerTest.FailureTestHandler();
            Bootstrap b = new Bootstrap();
            b.group(ProxyHandlerTest.group);
            b.channel(NioSocketChannel.class);
            b.resolver(NoopAddressResolverGroup.INSTANCE);
            b.handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(clientHandlers);
                    p.addLast(new LineBasedFrameDecoder(64));
                    p.addLast(testHandler);
                }
            });
            ChannelFuture cf = b.connect(ProxyHandlerTest.DESTINATION).channel().closeFuture();
            boolean finished = cf.await((TIMEOUT * 2), TimeUnit.MILLISECONDS);
            finished &= testHandler.latch.await((TIMEOUT * 2), TimeUnit.MILLISECONDS);
            ProxyHandlerTest.logger.debug("Recorded exceptions: {}", testHandler.exceptions);
            assertProxyHandlers(false);
            Assert.assertThat(testHandler.exceptions.size(), CoreMatchers.is(1));
            Throwable e = testHandler.exceptions.poll();
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(ProxyConnectException.class)));
            Assert.assertThat(String.valueOf(e), CoreMatchers.containsString("timeout"));
            Assert.assertThat(finished, CoreMatchers.is(true));
        }
    }
}

