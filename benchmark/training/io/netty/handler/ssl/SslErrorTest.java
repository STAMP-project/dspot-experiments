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
package io.netty.handler.ssl;


import ClientAuth.REQUIRE;
import InsecureTrustManagerFactory.INSTANCE;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.security.cert.CertPathValidatorException.BasicReason.EXPIRED;
import static java.security.cert.CertPathValidatorException.BasicReason.NOT_YET_VALID;
import static java.security.cert.CertPathValidatorException.BasicReason.REVOKED;


@RunWith(Parameterized.class)
public class SslErrorTest {
    private final SslProvider serverProvider;

    private final SslProvider clientProvider;

    private final CertificateException exception;

    public SslErrorTest(SslProvider serverProvider, SslProvider clientProvider, CertificateException exception) {
        this.serverProvider = serverProvider;
        this.clientProvider = clientProvider;
        this.exception = exception;
    }

    @Test(timeout = 30000)
    public void testCorrectAlert() throws Exception {
        // As this only works correctly at the moment when OpenSslEngine is used on the server-side there is
        // no need to run it if there is no openssl is available at all.
        Assume.assumeTrue(OpenSsl.isAvailable());
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SslContext sslServerCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(serverProvider).trustManager(new SimpleTrustManagerFactory() {
            @Override
            protected void engineInit(KeyStore keyStore) {
            }

            @Override
            protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[]{ new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        throw exception;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        // NOOP
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return EmptyArrays.EMPTY_X509_CERTIFICATES;
                    }
                } };
            }
        }).clientAuth(REQUIRE).build();
        final SslContext sslClientCtx = SslContextBuilder.forClient().trustManager(INSTANCE).keyManager(new File(getClass().getResource("test.crt").getFile()), new File(getClass().getResource("test_unencrypted.pem").getFile())).sslProvider(clientProvider).build();
        Channel serverChannel = null;
        Channel clientChannel = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            serverChannel = new ServerBootstrap().group(group).channel(NioServerSocketChannel.class).handler(new io.netty.handler.logging.LoggingHandler(LogLevel.INFO)).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslServerCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            ctx.close();
                        }
                    });
                }
            }).bind(0).sync().channel();
            final Promise<Void> promise = group.next().newPromise();
            clientChannel = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslClientCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            // Unwrap as its wrapped by a DecoderException
                            Throwable unwrappedCause = cause.getCause();
                            if (unwrappedCause instanceof SSLException) {
                                if ((exception) instanceof SslErrorTest.TestCertificateException) {
                                    CertPathValidatorException.Reason reason = ((CertPathValidatorException) (exception.getCause())).getReason();
                                    if (reason == (EXPIRED)) {
                                        SslErrorTest.verifyException(unwrappedCause, "expired", promise);
                                    } else
                                        if (reason == (NOT_YET_VALID)) {
                                            // BoringSSL uses "expired" in this case while others use "bad"
                                            if (OpenSsl.isBoringSSL()) {
                                                SslErrorTest.verifyException(unwrappedCause, "expired", promise);
                                            } else {
                                                SslErrorTest.verifyException(unwrappedCause, "bad", promise);
                                            }
                                        } else
                                            if (reason == (REVOKED)) {
                                                SslErrorTest.verifyException(unwrappedCause, "revoked", promise);
                                            }


                                } else
                                    if ((exception) instanceof CertificateExpiredException) {
                                        SslErrorTest.verifyException(unwrappedCause, "expired", promise);
                                    } else
                                        if ((exception) instanceof CertificateNotYetValidException) {
                                            // BoringSSL uses "expired" in this case while others use "bad"
                                            if (OpenSsl.isBoringSSL()) {
                                                SslErrorTest.verifyException(unwrappedCause, "expired", promise);
                                            } else {
                                                SslErrorTest.verifyException(unwrappedCause, "bad", promise);
                                            }
                                        } else
                                            if ((exception) instanceof CertificateRevokedException) {
                                                SslErrorTest.verifyException(unwrappedCause, "revoked", promise);
                                            }



                            }
                        }
                    });
                }
            }).connect(serverChannel.localAddress()).syncUninterruptibly().channel();
            // Block until we received the correct exception
            promise.syncUninterruptibly();
        } finally {
            if (clientChannel != null) {
                clientChannel.close().syncUninterruptibly();
            }
            if (serverChannel != null) {
                serverChannel.close().syncUninterruptibly();
            }
            group.shutdownGracefully();
            ReferenceCountUtil.release(sslServerCtx);
            ReferenceCountUtil.release(sslClientCtx);
        }
    }

    private static final class TestCertificateException extends CertificateException {
        private static final long serialVersionUID = -5816338303868751410L;

        TestCertificateException(Throwable cause) {
            super(cause);
        }
    }
}

