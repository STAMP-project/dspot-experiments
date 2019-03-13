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
package io.netty.handler.ssl;


import ClientAuth.REQUIRE;
import InsecureTrustManagerFactory.INSTANCE;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ResourcesUtil;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ParameterizedSslHandlerTest {
    private final SslProvider clientProvider;

    private final SslProvider serverProvider;

    public ParameterizedSslHandlerTest(SslProvider clientProvider, SslProvider serverProvider) {
        this.clientProvider = clientProvider;
        this.serverProvider = serverProvider;
    }

    @Test(timeout = 480000)
    public void testCompositeBufSizeEstimationGuaranteesSynchronousWrite() throws InterruptedException, CertificateException, ExecutionException, SSLException {
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, true, true, true);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, true, true, false);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, true, false, true);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, true, false, false);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, false, true, true);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, false, true, false);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, false, false, true);
        ParameterizedSslHandlerTest.compositeBufSizeEstimationGuaranteesSynchronousWrite(serverProvider, clientProvider, false, false, false);
    }

    @Test(timeout = 30000)
    public void testAlertProducedAndSend() throws Exception {
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
                        // Fail verification which should produce an alert that is send back to the client.
                        throw new CertificateException();
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        // NOOP
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return EmptyArrays.EMPTY_X509_CERTIFICATES;
                    }
                } };
            }
        }).clientAuth(REQUIRE).build();
        final SslContext sslClientCtx = SslContextBuilder.forClient().trustManager(INSTANCE).keyManager(ResourcesUtil.getFile(getClass(), "test.crt"), ResourcesUtil.getFile(getClass(), "test_unencrypted.pem")).sslProvider(clientProvider).build();
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel sc = null;
        Channel cc = null;
        try {
            final Promise<Void> promise = group.next().newPromise();
            sc = new ServerBootstrap().group(group).channel(NioServerSocketChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslServerCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            // Just trigger a close
                            ctx.close();
                        }
                    });
                }
            }).bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
            cc = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(sslClientCtx.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            if ((cause.getCause()) instanceof SSLException) {
                                // We received the alert and so produce an SSLException.
                                promise.trySuccess(null);
                            }
                        }
                    });
                }
            }).connect(sc.localAddress()).syncUninterruptibly().channel();
            promise.syncUninterruptibly();
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

    @Test(timeout = 30000)
    public void testCloseNotify() throws Exception {
        testCloseNotify(5000, false);
    }

    @Test(timeout = 30000)
    public void testCloseNotifyReceivedTimeout() throws Exception {
        testCloseNotify(100, true);
    }

    @Test(timeout = 30000)
    public void testCloseNotifyNotWaitForResponse() throws Exception {
        testCloseNotify(0, false);
    }
}

