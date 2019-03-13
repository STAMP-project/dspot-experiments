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
package io.netty.handler.ssl;


import ByteBufAllocator.DEFAULT;
import InsecureTrustManagerFactory.INSTANCE;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
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
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.DomainNameMapping;
import io.netty.util.DomainNameMappingBuilder;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLEngine;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static SslUtils.SSL_CONTENT_TYPE_ALERT;


@RunWith(Parameterized.class)
public class SniHandlerTest {
    private final SslProvider provider;

    public SniHandlerTest(SslProvider provider) {
        this.provider = provider;
    }

    @Test
    public void testNonSslRecord() throws Exception {
        SslContext nettyContext = SniHandlerTest.makeSslContext(provider, false);
        try {
            final AtomicReference<SslHandshakeCompletionEvent> evtRef = new AtomicReference<SslHandshakeCompletionEvent>();
            SniHandler handler = new SniHandler(new DomainNameMappingBuilder<SslContext>(nettyContext).build());
            EmbeddedChannel ch = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter() {
                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                    if (evt instanceof SslHandshakeCompletionEvent) {
                        Assert.assertTrue(evtRef.compareAndSet(null, ((SslHandshakeCompletionEvent) (evt))));
                    }
                }
            });
            try {
                byte[] bytes = new byte[1024];
                bytes[0] = SSL_CONTENT_TYPE_ALERT;
                try {
                    ch.writeInbound(Unpooled.wrappedBuffer(bytes));
                    Assert.fail();
                } catch (DecoderException e) {
                    Assert.assertTrue(((e.getCause()) instanceof NotSslRecordException));
                }
                Assert.assertFalse(ch.finish());
            } finally {
                ch.finishAndReleaseAll();
            }
            Assert.assertTrue(((evtRef.get().cause()) instanceof NotSslRecordException));
        } finally {
            SniHandlerTest.releaseAll(nettyContext);
        }
    }

    @Test
    public void testServerNameParsing() throws Exception {
        SslContext nettyContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext2 = SniHandlerTest.makeSslContext(provider, false);
        try {
            DomainNameMapping<SslContext> mapping = // a hostname conflict with previous one, since we are using order-sensitive config,
            // the engine won't be used with the handler.
            // input with custom cases
            new DomainNameMappingBuilder<SslContext>(nettyContext).add("*.netty.io", nettyContext).add("*.LEANCLOUD.CN", leanContext).add("chat4.leancloud.cn", leanContext2).build();
            final AtomicReference<SniCompletionEvent> evtRef = new AtomicReference<SniCompletionEvent>();
            SniHandler handler = new SniHandler(mapping);
            EmbeddedChannel ch = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter() {
                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                    if (evt instanceof SniCompletionEvent) {
                        Assert.assertTrue(evtRef.compareAndSet(null, ((SniCompletionEvent) (evt))));
                    } else {
                        ctx.fireUserEventTriggered(evt);
                    }
                }
            });
            try {
                // hex dump of a client hello packet, which contains hostname "CHAT4.LEANCLOUD.CN"
                String tlsHandshakeMessageHex1 = "16030100";
                // part 2
                String tlsHandshakeMessageHex = "c6010000c20303bb0855d66532c05a0ef784f7c384feeafa68b3" + ((((("b655ac7288650d5eed4aa3fb52000038c02cc030009fcca9cca8ccaac02b" + "c02f009ec024c028006bc023c0270067c00ac0140039c009c0130033009d") + "009c003d003c0035002f00ff010000610000001700150000124348415434") + "2e4c45414e434c4f55442e434e000b000403000102000a000a0008001d00") + "170019001800230000000d0020001e060106020603050105020503040104") + "0204030301030203030201020202030016000000170000");
                ch.writeInbound(Unpooled.wrappedBuffer(StringUtil.decodeHexDump(tlsHandshakeMessageHex1)));
                ch.writeInbound(Unpooled.wrappedBuffer(StringUtil.decodeHexDump(tlsHandshakeMessageHex)));
                // This should produce an alert
                Assert.assertTrue(ch.finish());
                Assert.assertThat(handler.hostname(), CoreMatchers.is("chat4.leancloud.cn"));
                Assert.assertThat(handler.sslContext(), CoreMatchers.is(leanContext));
                SniCompletionEvent evt = evtRef.get();
                Assert.assertNotNull(evt);
                Assert.assertEquals("chat4.leancloud.cn", evt.hostname());
                Assert.assertTrue(evt.isSuccess());
                Assert.assertNull(evt.cause());
            } finally {
                ch.finishAndReleaseAll();
            }
        } finally {
            SniHandlerTest.releaseAll(leanContext, leanContext2, nettyContext);
        }
    }

    @Test(expected = DecoderException.class)
    public void testNonAsciiServerNameParsing() throws Exception {
        SslContext nettyContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext2 = SniHandlerTest.makeSslContext(provider, false);
        try {
            DomainNameMapping<SslContext> mapping = // a hostname conflict with previous one, since we are using order-sensitive config,
            // the engine won't be used with the handler.
            // input with custom cases
            new DomainNameMappingBuilder<SslContext>(nettyContext).add("*.netty.io", nettyContext).add("*.LEANCLOUD.CN", leanContext).add("chat4.leancloud.cn", leanContext2).build();
            SniHandler handler = new SniHandler(mapping);
            EmbeddedChannel ch = new EmbeddedChannel(handler);
            try {
                // hex dump of a client hello packet, which contains an invalid hostname "CHAT4?LEANCLOUD?CN"
                String tlsHandshakeMessageHex1 = "16030100";
                // part 2
                String tlsHandshakeMessageHex = "bd010000b90303a74225676d1814ba57faff3b366" + (((("3656ed05ee9dbb2a4dbb1bb1c32d2ea5fc39e0000000100008c0000001700150000164348" + "415434E380824C45414E434C4F5544E38082434E000b000403000102000a00340032000e0") + "00d0019000b000c00180009000a0016001700080006000700140015000400050012001300") + "0100020003000f0010001100230000000d0020001e0601060206030501050205030401040") + "20403030103020303020102020203000f00010133740000");
                // Push the handshake message.
                // Decode should fail because of the badly encoded "HostName" string in the SNI extension
                // that isn't ASCII as per RFC 6066 - https://tools.ietf.org/html/rfc6066#page-6
                ch.writeInbound(Unpooled.wrappedBuffer(StringUtil.decodeHexDump(tlsHandshakeMessageHex1)));
                ch.writeInbound(Unpooled.wrappedBuffer(StringUtil.decodeHexDump(tlsHandshakeMessageHex)));
            } finally {
                ch.finishAndReleaseAll();
            }
        } finally {
            SniHandlerTest.releaseAll(leanContext, leanContext2, nettyContext);
        }
    }

    @Test
    public void testFallbackToDefaultContext() throws Exception {
        SslContext nettyContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext = SniHandlerTest.makeSslContext(provider, false);
        SslContext leanContext2 = SniHandlerTest.makeSslContext(provider, false);
        try {
            DomainNameMapping<SslContext> mapping = // a hostname conflict with previous one, since we are using order-sensitive config,
            // the engine won't be used with the handler.
            // input with custom cases
            new DomainNameMappingBuilder<SslContext>(nettyContext).add("*.netty.io", nettyContext).add("*.LEANCLOUD.CN", leanContext).add("chat4.leancloud.cn", leanContext2).build();
            SniHandler handler = new SniHandler(mapping);
            EmbeddedChannel ch = new EmbeddedChannel(handler);
            // invalid
            byte[] message = new byte[]{ 22, 3, 1, 0, 0 };
            try {
                // Push the handshake message.
                ch.writeInbound(Unpooled.wrappedBuffer(message));
                // TODO(scott): This should fail becasue the engine should reject zero length records during handshake.
                // See https://github.com/netty/netty/issues/6348.
                // fail();
            } catch (Exception e) {
                // expected
            }
            ch.close();
            // When the channel is closed the SslHandler will write an empty buffer to the channel.
            ByteBuf buf = ch.readOutbound();
            // TODO(scott): if the engine is shutdown correctly then this buffer shouldn't be null!
            // See https://github.com/netty/netty/issues/6348.
            if (buf != null) {
                Assert.assertFalse(buf.isReadable());
                buf.release();
            }
            Assert.assertThat(ch.finish(), CoreMatchers.is(false));
            Assert.assertThat(handler.hostname(), CoreMatchers.nullValue());
            Assert.assertThat(handler.sslContext(), CoreMatchers.is(nettyContext));
        } finally {
            SniHandlerTest.releaseAll(leanContext, leanContext2, nettyContext);
        }
    }

    @Test
    public void testSniWithApnHandler() throws Exception {
        SslContext nettyContext = SniHandlerTest.makeSslContext(provider, true);
        SslContext sniContext = SniHandlerTest.makeSslContext(provider, true);
        final SslContext clientContext = SniHandlerTest.makeSslClientContext(provider, true);
        try {
            final CountDownLatch serverApnDoneLatch = new CountDownLatch(1);
            final CountDownLatch clientApnDoneLatch = new CountDownLatch(1);
            final DomainNameMapping<SslContext> mapping = new DomainNameMappingBuilder<SslContext>(nettyContext).add("*.netty.io", nettyContext).add("sni.fake.site", sniContext).build();
            final SniHandler handler = new SniHandler(mapping);
            EventLoopGroup group = new NioEventLoopGroup(2);
            Channel serverChannel = null;
            Channel clientChannel = null;
            try {
                ServerBootstrap sb = new ServerBootstrap();
                sb.group(group);
                sb.channel(NioServerSocketChannel.class);
                sb.childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // Server side SNI.
                        p.addLast(handler);
                        // Catch the notification event that APN has completed successfully.
                        p.addLast(new ApplicationProtocolNegotiationHandler("foo") {
                            @Override
                            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                                serverApnDoneLatch.countDown();
                            }
                        });
                    }
                });
                Bootstrap cb = new Bootstrap();
                cb.group(group);
                cb.channel(NioSocketChannel.class);
                cb.handler(new io.netty.channel.ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new SslHandler(clientContext.newEngine(ch.alloc(), "sni.fake.site", (-1))));
                        // Catch the notification event that APN has completed successfully.
                        ch.pipeline().addLast(new ApplicationProtocolNegotiationHandler("foo") {
                            @Override
                            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                                clientApnDoneLatch.countDown();
                            }
                        });
                    }
                });
                serverChannel = sb.bind(new InetSocketAddress(0)).sync().channel();
                ChannelFuture ccf = cb.connect(serverChannel.localAddress());
                Assert.assertTrue(ccf.awaitUninterruptibly().isSuccess());
                clientChannel = ccf.channel();
                Assert.assertTrue(serverApnDoneLatch.await(5, TimeUnit.SECONDS));
                Assert.assertTrue(clientApnDoneLatch.await(5, TimeUnit.SECONDS));
                Assert.assertThat(handler.hostname(), CoreMatchers.is("sni.fake.site"));
                Assert.assertThat(handler.sslContext(), CoreMatchers.is(sniContext));
            } finally {
                if (serverChannel != null) {
                    serverChannel.close().sync();
                }
                if (clientChannel != null) {
                    clientChannel.close().sync();
                }
                group.shutdownGracefully(0, 0, TimeUnit.MICROSECONDS);
            }
        } finally {
            SniHandlerTest.releaseAll(clientContext, nettyContext, sniContext);
        }
    }

    @Test(timeout = 30000)
    public void testReplaceHandler() throws Exception {
        switch (provider) {
            case OPENSSL :
            case OPENSSL_REFCNT :
                final String sniHost = "sni.netty.io";
                LocalAddress address = new LocalAddress(("testReplaceHandler-" + (Math.random())));
                EventLoopGroup group = new DefaultEventLoopGroup(1);
                Channel sc = null;
                Channel cc = null;
                SslContext sslContext = null;
                SelfSignedCertificate cert = new SelfSignedCertificate();
                try {
                    final SslContext sslServerContext = SslContextBuilder.forServer(cert.key(), cert.cert()).sslProvider(provider).build();
                    final Mapping<String, SslContext> mapping = new Mapping<String, SslContext>() {
                        @Override
                        public SslContext map(String input) {
                            return sslServerContext;
                        }
                    };
                    final Promise<Void> releasePromise = group.next().newPromise();
                    final SniHandler handler = new SniHandler(mapping) {
                        @Override
                        protected void replaceHandler(ChannelHandlerContext ctx, String hostname, final SslContext sslContext) throws Exception {
                            boolean success = false;
                            try {
                                // The SniHandler's replaceHandler() method allows us to implement custom behavior.
                                // As an example, we want to release() the SslContext upon channelInactive() or rather
                                // when the SslHandler closes it's SslEngine. If you take a close look at SslHandler
                                // you'll see that it's doing it in the #handlerRemoved0() method.
                                SSLEngine sslEngine = sslContext.newEngine(ctx.alloc());
                                try {
                                    SslHandler customSslHandler = new SniHandlerTest.CustomSslHandler(sslContext, sslEngine) {
                                        @Override
                                        public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
                                            try {
                                                super.handlerRemoved0(ctx);
                                            } finally {
                                                releasePromise.trySuccess(null);
                                            }
                                        }
                                    };
                                    ctx.pipeline().replace(this, SniHandlerTest.CustomSslHandler.class.getName(), customSslHandler);
                                    success = true;
                                } finally {
                                    if (!success) {
                                        ReferenceCountUtil.safeRelease(sslEngine);
                                    }
                                }
                            } finally {
                                if (!success) {
                                    ReferenceCountUtil.safeRelease(sslContext);
                                    releasePromise.cancel(true);
                                }
                            }
                        }
                    };
                    ServerBootstrap sb = new ServerBootstrap();
                    sc = sb.group(group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addFirst(handler);
                        }
                    }).bind(address).syncUninterruptibly().channel();
                    sslContext = SslContextBuilder.forClient().sslProvider(provider).trustManager(INSTANCE).build();
                    Bootstrap cb = new Bootstrap();
                    cc = cb.group(group).channel(LocalChannel.class).handler(new SslHandler(sslContext.newEngine(DEFAULT, sniHost, (-1)))).connect(address).syncUninterruptibly().channel();
                    cc.writeAndFlush(Unpooled.wrappedBuffer("Hello, World!".getBytes())).syncUninterruptibly();
                    // Notice how the server's SslContext refCnt is 1
                    Assert.assertEquals(1, refCnt());
                    // The client disconnects
                    cc.close().syncUninterruptibly();
                    if (!(releasePromise.awaitUninterruptibly(10L, TimeUnit.SECONDS))) {
                        throw new IllegalStateException("It doesn't seem #replaceHandler() got called.");
                    }
                    // We should have successfully release() the SslContext
                    Assert.assertEquals(0, refCnt());
                } finally {
                    if (cc != null) {
                        cc.close().syncUninterruptibly();
                    }
                    if (sc != null) {
                        sc.close().syncUninterruptibly();
                    }
                    if (sslContext != null) {
                        ReferenceCountUtil.release(sslContext);
                    }
                    group.shutdownGracefully();
                    cert.delete();
                }
            case JDK :
                return;
            default :
                throw new Error();
        }
    }

    /**
     * This is a {@link SslHandler} that will call {@code release()} on the {@link SslContext} when
     * the client disconnects.
     *
     * @see SniHandlerTest#testReplaceHandler()
     */
    private static class CustomSslHandler extends SslHandler {
        private final SslContext sslContext;

        CustomSslHandler(SslContext sslContext, SSLEngine sslEngine) {
            super(sslEngine);
            this.sslContext = ObjectUtil.checkNotNull(sslContext, "sslContext");
        }

        @Override
        public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved0(ctx);
            ReferenceCountUtil.release(sslContext);
        }
    }
}

