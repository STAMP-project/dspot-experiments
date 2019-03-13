/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.ssl;


import InsecureTrustManagerFactory.INSTANCE;
import SslUtils.PROTOCOL_TLS_V1_2;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * The purpose of this unit test is to act as a canary and catch changes in supported cipher suites.
 */
@RunWith(Parameterized.class)
public class CipherSuiteCanaryTest {
    private static EventLoopGroup GROUP;

    private static SelfSignedCertificate CERT;

    private final SslProvider serverSslProvider;

    private final SslProvider clientSslProvider;

    private final String rfcCipherName;

    private final boolean delegate;

    public CipherSuiteCanaryTest(SslProvider serverSslProvider, SslProvider clientSslProvider, String rfcCipherName, boolean delegate) {
        this.serverSslProvider = serverSslProvider;
        this.clientSslProvider = clientSslProvider;
        this.rfcCipherName = rfcCipherName;
        this.delegate = delegate;
    }

    @Test
    public void testHandshake() throws Exception {
        // Check if the cipher is supported at all which may not be the case for various JDK versions and OpenSSL API
        // implementations.
        CipherSuiteCanaryTest.assumeCipherAvailable(serverSslProvider, rfcCipherName);
        CipherSuiteCanaryTest.assumeCipherAvailable(clientSslProvider, rfcCipherName);
        List<String> ciphers = Collections.singletonList(rfcCipherName);
        final SslContext sslServerContext = // As this is not a TLSv1.3 cipher we should ensure we talk something else.
        SslContextBuilder.forServer(CipherSuiteCanaryTest.CERT.certificate(), CipherSuiteCanaryTest.CERT.privateKey()).sslProvider(serverSslProvider).ciphers(ciphers).protocols(PROTOCOL_TLS_V1_2).build();
        final ExecutorService executorService = (delegate) ? Executors.newCachedThreadPool() : null;
        try {
            final SslContext sslClientContext = // As this is not a TLSv1.3 cipher we should ensure we talk something else.
            SslContextBuilder.forClient().sslProvider(clientSslProvider).ciphers(ciphers).protocols(PROTOCOL_TLS_V1_2).trustManager(INSTANCE).build();
            try {
                final Promise<Object> serverPromise = CipherSuiteCanaryTest.GROUP.next().newPromise();
                final Promise<Object> clientPromise = CipherSuiteCanaryTest.GROUP.next().newPromise();
                ChannelHandler serverHandler = new io.netty.channel.ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(CipherSuiteCanaryTest.newSslHandler(sslServerContext, ch.alloc(), executorService));
                        pipeline.addLast(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                serverPromise.cancel(true);
                                ctx.fireChannelInactive();
                            }

                            @Override
                            public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (serverPromise.trySuccess(null)) {
                                    ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{ 'P', 'O', 'N', 'G' }));
                                }
                                ctx.close();
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                if (!(serverPromise.tryFailure(cause))) {
                                    ctx.fireExceptionCaught(cause);
                                }
                            }
                        });
                    }
                };
                LocalAddress address = new LocalAddress(((((("test-" + (serverSslProvider)) + '-') + (clientSslProvider)) + '-') + (rfcCipherName)));
                Channel server = CipherSuiteCanaryTest.server(address, serverHandler);
                try {
                    ChannelHandler clientHandler = new io.netty.channel.ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(CipherSuiteCanaryTest.newSslHandler(sslClientContext, ch.alloc(), executorService));
                            pipeline.addLast(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    clientPromise.cancel(true);
                                    ctx.fireChannelInactive();
                                }

                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    clientPromise.trySuccess(null);
                                    ctx.close();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    if (!(clientPromise.tryFailure(cause))) {
                                        ctx.fireExceptionCaught(cause);
                                    }
                                }
                            });
                        }
                    };
                    Channel client = CipherSuiteCanaryTest.client(server, clientHandler);
                    try {
                        client.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{ 'P', 'I', 'N', 'G' })).syncUninterruptibly();
                        Assert.assertTrue("client timeout", clientPromise.await(5L, TimeUnit.SECONDS));
                        Assert.assertTrue("server timeout", serverPromise.await(5L, TimeUnit.SECONDS));
                        clientPromise.sync();
                        serverPromise.sync();
                    } finally {
                        client.close().sync();
                    }
                } finally {
                    server.close().sync();
                }
            } finally {
                ReferenceCountUtil.release(sslClientContext);
            }
        } finally {
            ReferenceCountUtil.release(sslServerContext);
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }
}

