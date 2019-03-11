/**
 * Copyright 2015 The Netty Project
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


import CharsetUtil.US_ASCII;
import CharsetUtil.UTF_8;
import ClientAuth.NONE;
import ClientAuth.OPTIONAL;
import ClientAuth.REQUIRE;
import InsecureTrustManagerFactory.INSTANCE;
import SslProvider.JDK;
import UnpooledByteBufAllocator.DEFAULT;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ResourcesUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mock;

import static SslProvider.JDK;
import static SslProvider.OPENSSL;
import static SslProvider.OPENSSL_REFCNT;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
import static javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;
import static javax.net.ssl.SSLEngineResult.Status.CLOSED;
import static javax.net.ssl.SSLEngineResult.Status.OK;


public abstract class SSLEngineTest {
    private static final String X509_CERT_PEM = "-----BEGIN CERTIFICATE-----\n" + ((((((((((("MIIB9jCCAV+gAwIBAgIJAO9fzyjyV5BhMA0GCSqGSIb3DQEBCwUAMBQxEjAQBgNV\n" + "BAMMCWxvY2FsaG9zdDAeFw0xNjA5MjAxOTI0MTVaFw00NDEwMDMxOTI0MTVaMBQx\n") + "EjAQBgNVBAMMCWxvY2FsaG9zdDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA\n") + "1Kp6DmwRiI+TNs3rZ3WvceDYQ4VTxZQk9jgHVKhHTeA0LptOaazbm9g+aOPiCc6V\n") + "5ysu8T8YRLWjej3by2/1zPBO1k25dQRK8dHr0Grmo20FW7+ES+YxohOfmi7bjOVm\n") + "NrI3NoVZjf3fQjAlrtKCmaxRPgYEwOT0ucGfJiEyV9cCAwEAAaNQME4wHQYDVR0O\n") + "BBYEFIba521hTU1P+1QHcIqAOdAEgd1QMB8GA1UdIwQYMBaAFIba521hTU1P+1QH\n") + "cIqAOdAEgd1QMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADgYEAHG5hBy0b\n") + "ysXKJqWQ/3bNId3VCzD9U557oxEYYAuPG0TqyvjvZ3wNQto079Na7lYkTt2kTIYN\n") + "/HPW2eflDyXAwXmdNM1Gre213NECY9VxDBTCYJ1R4f2Ogv9iehwzZ4aJGxEDay69\n") + "wrGrxKIrKL4OMl/E+R4mi+yZ0i6bfQuli5s=\n") + "-----END CERTIFICATE-----\n");

    private static final String PRIVATE_KEY_PEM = "-----BEGIN PRIVATE KEY-----\n" + (((((((((((((("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANSqeg5sEYiPkzbN\n" + "62d1r3Hg2EOFU8WUJPY4B1SoR03gNC6bTmms25vYPmjj4gnOlecrLvE/GES1o3o9\n") + "28tv9czwTtZNuXUESvHR69Bq5qNtBVu/hEvmMaITn5ou24zlZjayNzaFWY3930Iw\n") + "Ja7SgpmsUT4GBMDk9LnBnyYhMlfXAgMBAAECgYAeyc+B5wNi0eZuOMGr6M3Nns+w\n") + "dsz5/cicHOBy0SoBjEQBu1pO0ke4+EWQye0fnlj1brsNEiVhTSqtt+bqPPtIvKtZ\n") + "U4Z2M5euUQL390LnVM+jlaRyKUFVYzFnWfNgciT6SLsrbGRz9EhMH2jM6gi8O/cI\n") + "n8Do9fgHon9dILOPAQJBAO/3xc0/sWP94Cv25ogsvOLRgXY2NqY/PDfWat31MFt4\n") + "pKh9aad7SrqR7oRXIEuJ+16drM0O+KveJEjFnHgcq18CQQDi38CqycxrsL2pzq53\n") + "XtlhbzOBpDaNjySMmdg8wIXVVGmFC7Y2zWq+cEirrI0n2BJOC4LLDNvlT6IjnYqF\n") + "qp6JAkBQfB0Wyz8XF4aBmG0XzVGJDdXLLUHFHr52x+7OBTez5lHrxSyTpPGag+mo\n") + "74QAcgYiZOYZXOUg1//5fHYPfyYnAkANKyenwibXaV7Y6GJAE4VSnn3C3KE9/j0E\n") + "3Dks7Y/XHhsx2cgtziaP/zx4mn9m/KezV/+zgX+SA9lJb++GaqzhAkEAoNfjQ4jd\n") + "3fsY99ZVoC5YFASSKf+DBqcVOkiUtF1pRwBzLDgKW14+nM/v7X+HJzkfnNTj4cW/\n") + "nUq37uAS7oJc4g==\n") + "-----END PRIVATE KEY-----\n");

    private static final String CLIENT_X509_CERT_PEM = "-----BEGIN CERTIFICATE-----\n" + ((((((((("MIIBmTCCAQICAQEwDQYJKoZIhvcNAQELBQAwFDESMBAGA1UEAwwJbG9jYWxob3N0\n" + "MCAXDTE3MDkyMTAzNDUwMVoYDzIxMTcwOTIyMDM0NTAxWjAUMRIwEAYDVQQDEwl0\n") + "bHNjbGllbnQwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAMHX2Jc5i3I+npww\n") + "mIb0L1A3D+ujpJam/0fTA9w8GFZGs8Em9emlVbEwzFi4kVIoLxwZGqkr6TSH2iaf\n") + "aX5zVF4oUQyLRyxlFkwaORRi/T+iXq2XPQIW9A5TmVHGSHUlYj8/X9vfrMkJO/I0\n") + "RXi6mMBXV4C7bu3BLyEGs8rb6kirAgMBAAEwDQYJKoZIhvcNAQELBQADgYEAYLYI\n") + "5wvUaGRqJn7pA4xR9nEhsNpQbza3bJayQvyiJsB5rn9yBJsk5ch3tBBCfh/MA6PW\n") + "xcy2hS5rhZUTve6FK3Kr2GiUYy+keYmbna1UJPKPgIR3BX66g+Ev5RUinmbieC2J\n") + "eE0EtFfLq3uzj8HjakuxOGJz9h+bnCGBhgWWOBo=\n") + "-----END CERTIFICATE-----\n");

    private static final String CLIENT_PRIVATE_KEY_PEM = "-----BEGIN PRIVATE KEY-----\n" + (((((((((((((("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMHX2Jc5i3I+npww\n" + "mIb0L1A3D+ujpJam/0fTA9w8GFZGs8Em9emlVbEwzFi4kVIoLxwZGqkr6TSH2iaf\n") + "aX5zVF4oUQyLRyxlFkwaORRi/T+iXq2XPQIW9A5TmVHGSHUlYj8/X9vfrMkJO/I0\n") + "RXi6mMBXV4C7bu3BLyEGs8rb6kirAgMBAAECgYBKPKrzh5NTJo5CDQ5tKNlx5BSR\n") + "zzM6iyxbSoJA9zbu29b90zj8yVgfKywnkk/9Yexg23Btd6axepHeltClH/AgD1GL\n") + "QE9bpeBMm8r+/9v/XR/mn5GjTxspj/q29mqOdg8CrKb8M6r1gtj70r8nI8aqmDwV\n") + "b6/ZTpsei+tN635Y2QJBAP1FHtIK2Z4t2Ro7oNaiv3s3TsYDlj14ladG+DKi2tW+\n") + "9PW7AO8rLAx2LWmrilXDFc7UG6hvhmUVkp7wXRCK0dcCQQDD7r3g8lswdEAVI0tF\n") + "fzJO61vDR12Kxv4flar9GwWdak7EzCp//iYNPWc+S7ONlYRbbI+uKVL/KBlvkU9E\n") + "4M1NAkEAy0ZGzl5W+1XhAeUJ2jsVZFenqdYHJ584veGAI2QCL7vr763/ufX0jKvt\n") + "FvrPNLY3MqGa8T1RqJ//5gEVMMm6UQJAKpBJpX1gu/T1GuJw7qcEKcrNQ23Ub1pt\n") + "SDU+UP+2x4yZkfz8WpO+dm/ZZtoRJnfNqgK6b85AXne6ltcNTlw7nQJBAKnFel18\n") + "Tg2ea308CyM+SJQxpfmU+1yeO2OYHNmimjWFhQPuxIDP9JUzpW39DdCDdTcd++HK\n") + "xJ5gsU/5OLk6ySo=\n") + "-----END PRIVATE KEY-----\n");

    private static final String CLIENT_X509_CERT_CHAIN_PEM = (SSLEngineTest.CLIENT_X509_CERT_PEM) + (SSLEngineTest.X509_CERT_PEM);

    private static final String PRINCIPAL_NAME = "CN=e8ac02fa0d65a84219016045db8b05c485b4ecdf.netty.test";

    @Mock
    protected SSLEngineTest.MessageReceiver serverReceiver;

    @Mock
    protected SSLEngineTest.MessageReceiver clientReceiver;

    protected Throwable serverException;

    protected Throwable clientException;

    protected SslContext serverSslCtx;

    protected SslContext clientSslCtx;

    protected ServerBootstrap sb;

    protected Bootstrap cb;

    protected Channel serverChannel;

    protected Channel serverConnectedChannel;

    protected Channel clientChannel;

    protected CountDownLatch serverLatch;

    protected CountDownLatch clientLatch;

    interface MessageReceiver {
        void messageReceived(ByteBuf msg);
    }

    protected static final class MessageDelegatorChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private final SSLEngineTest.MessageReceiver receiver;

        private final CountDownLatch latch;

        public MessageDelegatorChannelHandler(SSLEngineTest.MessageReceiver receiver, CountDownLatch latch) {
            super(false);
            this.receiver = receiver;
            this.latch = latch;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            receiver.messageReceived(msg);
            latch.countDown();
        }
    }

    enum BufferType {

        Direct,
        Heap,
        Mixed;}

    static final class ProtocolCipherCombo {
        private static final SSLEngineTest.ProtocolCipherCombo TLSV12 = new SSLEngineTest.ProtocolCipherCombo(PROTOCOL_TLS_V1_2, "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

        private static final SSLEngineTest.ProtocolCipherCombo TLSV13 = new SSLEngineTest.ProtocolCipherCombo(PROTOCOL_TLS_V1_3, "TLS_AES_128_GCM_SHA256");

        final String protocol;

        final String cipher;

        private ProtocolCipherCombo(String protocol, String cipher) {
            this.protocol = protocol;
            this.cipher = cipher;
        }

        static SSLEngineTest.ProtocolCipherCombo tlsv12() {
            return SSLEngineTest.ProtocolCipherCombo.TLSV12;
        }

        static SSLEngineTest.ProtocolCipherCombo tlsv13() {
            return SSLEngineTest.ProtocolCipherCombo.TLSV13;
        }

        @Override
        public String toString() {
            return (((((("ProtocolCipherCombo{" + "protocol='") + (protocol)) + '\'') + ", cipher='") + (cipher)) + '\'') + '}';
        }
    }

    private final SSLEngineTest.BufferType type;

    private final SSLEngineTest.ProtocolCipherCombo protocolCipherCombo;

    private final boolean delegate;

    private ExecutorService delegatingExecutor;

    protected SSLEngineTest(SSLEngineTest.BufferType type, SSLEngineTest.ProtocolCipherCombo protocolCipherCombo, boolean delegate) {
        this.type = type;
        this.protocolCipherCombo = protocolCipherCombo;
        this.delegate = delegate;
    }

    private static final class TestByteBufAllocator implements ByteBufAllocator {
        private final ByteBufAllocator allocator;

        private final SSLEngineTest.BufferType type;

        TestByteBufAllocator(ByteBufAllocator allocator, SSLEngineTest.BufferType type) {
            this.allocator = allocator;
            this.type = type;
        }

        @Override
        public ByteBuf buffer() {
            switch (type) {
                case Direct :
                    return allocator.directBuffer();
                case Heap :
                    return allocator.heapBuffer();
                case Mixed :
                    return PlatformDependent.threadLocalRandom().nextBoolean() ? allocator.directBuffer() : allocator.heapBuffer();
                default :
                    throw new Error();
            }
        }

        @Override
        public ByteBuf buffer(int initialCapacity) {
            switch (type) {
                case Direct :
                    return allocator.directBuffer(initialCapacity);
                case Heap :
                    return allocator.heapBuffer(initialCapacity);
                case Mixed :
                    return PlatformDependent.threadLocalRandom().nextBoolean() ? allocator.directBuffer(initialCapacity) : allocator.heapBuffer(initialCapacity);
                default :
                    throw new Error();
            }
        }

        @Override
        public ByteBuf buffer(int initialCapacity, int maxCapacity) {
            switch (type) {
                case Direct :
                    return allocator.directBuffer(initialCapacity, maxCapacity);
                case Heap :
                    return allocator.heapBuffer(initialCapacity, maxCapacity);
                case Mixed :
                    return PlatformDependent.threadLocalRandom().nextBoolean() ? allocator.directBuffer(initialCapacity, maxCapacity) : allocator.heapBuffer(initialCapacity, maxCapacity);
                default :
                    throw new Error();
            }
        }

        @Override
        public ByteBuf ioBuffer() {
            return allocator.ioBuffer();
        }

        @Override
        public ByteBuf ioBuffer(int initialCapacity) {
            return allocator.ioBuffer(initialCapacity);
        }

        @Override
        public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
            return allocator.ioBuffer(initialCapacity, maxCapacity);
        }

        @Override
        public ByteBuf heapBuffer() {
            return allocator.heapBuffer();
        }

        @Override
        public ByteBuf heapBuffer(int initialCapacity) {
            return allocator.heapBuffer(initialCapacity);
        }

        @Override
        public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
            return allocator.heapBuffer(initialCapacity, maxCapacity);
        }

        @Override
        public ByteBuf directBuffer() {
            return allocator.directBuffer();
        }

        @Override
        public ByteBuf directBuffer(int initialCapacity) {
            return allocator.directBuffer(initialCapacity);
        }

        @Override
        public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
            return allocator.directBuffer(initialCapacity, maxCapacity);
        }

        @Override
        public CompositeByteBuf compositeBuffer() {
            switch (type) {
                case Direct :
                    return allocator.compositeDirectBuffer();
                case Heap :
                    return allocator.compositeHeapBuffer();
                case Mixed :
                    return PlatformDependent.threadLocalRandom().nextBoolean() ? allocator.compositeDirectBuffer() : allocator.compositeHeapBuffer();
                default :
                    throw new Error();
            }
        }

        @Override
        public CompositeByteBuf compositeBuffer(int maxNumComponents) {
            switch (type) {
                case Direct :
                    return allocator.compositeDirectBuffer(maxNumComponents);
                case Heap :
                    return allocator.compositeHeapBuffer(maxNumComponents);
                case Mixed :
                    return PlatformDependent.threadLocalRandom().nextBoolean() ? allocator.compositeDirectBuffer(maxNumComponents) : allocator.compositeHeapBuffer(maxNumComponents);
                default :
                    throw new Error();
            }
        }

        @Override
        public CompositeByteBuf compositeHeapBuffer() {
            return allocator.compositeHeapBuffer();
        }

        @Override
        public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
            return allocator.compositeHeapBuffer(maxNumComponents);
        }

        @Override
        public CompositeByteBuf compositeDirectBuffer() {
            return allocator.compositeDirectBuffer();
        }

        @Override
        public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
            return allocator.compositeDirectBuffer(maxNumComponents);
        }

        @Override
        public boolean isDirectBufferPooled() {
            return allocator.isDirectBufferPooled();
        }

        @Override
        public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
            return allocator.calculateNewCapacity(minNewCapacity, maxCapacity);
        }
    }

    @Test
    public void testMutualAuthSameCerts() throws Throwable {
        mySetupMutualAuth(ResourcesUtil.getFile(getClass(), "test_unencrypted.pem"), ResourcesUtil.getFile(getClass(), "test.crt"), null);
        runTest(null);
        Assert.assertTrue(serverLatch.await(2, TimeUnit.SECONDS));
        Throwable cause = serverException;
        if (cause != null) {
            throw cause;
        }
    }

    @Test
    public void testMutualAuthDiffCerts() throws Exception {
        File serverKeyFile = ResourcesUtil.getFile(getClass(), "test_encrypted.pem");
        File serverCrtFile = ResourcesUtil.getFile(getClass(), "test.crt");
        String serverKeyPassword = "12345";
        File clientKeyFile = ResourcesUtil.getFile(getClass(), "test2_encrypted.pem");
        File clientCrtFile = ResourcesUtil.getFile(getClass(), "test2.crt");
        String clientKeyPassword = "12345";
        mySetupMutualAuth(clientCrtFile, serverKeyFile, serverCrtFile, serverKeyPassword, serverCrtFile, clientKeyFile, clientCrtFile, clientKeyPassword);
        runTest(null);
        Assert.assertTrue(serverLatch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testMutualAuthDiffCertsServerFailure() throws Exception {
        File serverKeyFile = ResourcesUtil.getFile(getClass(), "test_encrypted.pem");
        File serverCrtFile = ResourcesUtil.getFile(getClass(), "test.crt");
        String serverKeyPassword = "12345";
        File clientKeyFile = ResourcesUtil.getFile(getClass(), "test2_encrypted.pem");
        File clientCrtFile = ResourcesUtil.getFile(getClass(), "test2.crt");
        String clientKeyPassword = "12345";
        // Client trusts server but server only trusts itself
        mySetupMutualAuth(serverCrtFile, serverKeyFile, serverCrtFile, serverKeyPassword, serverCrtFile, clientKeyFile, clientCrtFile, clientKeyPassword);
        Assert.assertTrue(serverLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(((serverException) instanceof SSLHandshakeException));
    }

    @Test
    public void testMutualAuthDiffCertsClientFailure() throws Exception {
        File serverKeyFile = ResourcesUtil.getFile(getClass(), "test_unencrypted.pem");
        File serverCrtFile = ResourcesUtil.getFile(getClass(), "test.crt");
        String serverKeyPassword = null;
        File clientKeyFile = ResourcesUtil.getFile(getClass(), "test2_unencrypted.pem");
        File clientCrtFile = ResourcesUtil.getFile(getClass(), "test2.crt");
        String clientKeyPassword = null;
        // Server trusts client but client only trusts itself
        mySetupMutualAuth(clientCrtFile, serverKeyFile, serverCrtFile, serverKeyPassword, clientCrtFile, clientKeyFile, clientCrtFile, clientKeyPassword);
        Assert.assertTrue(clientLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(((clientException) instanceof SSLHandshakeException));
    }

    @Test
    public void testMutualAuthInvalidIntermediateCASucceedWithOptionalClientAuth() throws Exception {
        testMutualAuthInvalidClientCertSucceed(NONE);
    }

    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithOptionalClientAuth() throws Exception {
        testMutualAuthClientCertFail(OPTIONAL);
    }

    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithRequiredClientAuth() throws Exception {
        testMutualAuthClientCertFail(REQUIRE);
    }

    @Test
    public void testMutualAuthValidClientCertChainTooLongFailOptionalClientAuth() throws Exception {
        testMutualAuthClientCertFail(OPTIONAL, "mutual_auth_client.p12", true);
    }

    @Test
    public void testMutualAuthValidClientCertChainTooLongFailRequireClientAuth() throws Exception {
        testMutualAuthClientCertFail(REQUIRE, "mutual_auth_client.p12", true);
    }

    @Test
    public void testClientHostnameValidationSuccess() throws InterruptedException, SSLException {
        mySetupClientHostnameValidation(ResourcesUtil.getFile(getClass(), "localhost_server.pem"), ResourcesUtil.getFile(getClass(), "localhost_server.key"), ResourcesUtil.getFile(getClass(), "mutual_auth_ca.pem"), false);
        Assert.assertTrue(clientLatch.await(5, TimeUnit.SECONDS));
        Assert.assertNull(clientException);
        Assert.assertTrue(serverLatch.await(5, TimeUnit.SECONDS));
        Assert.assertNull(serverException);
    }

    @Test
    public void testClientHostnameValidationFail() throws InterruptedException, SSLException {
        mySetupClientHostnameValidation(ResourcesUtil.getFile(getClass(), "notlocalhost_server.pem"), ResourcesUtil.getFile(getClass(), "notlocalhost_server.key"), ResourcesUtil.getFile(getClass(), "mutual_auth_ca.pem"), true);
        Assert.assertTrue(clientLatch.await(5, TimeUnit.SECONDS));
        Assert.assertTrue(("unexpected exception: " + (clientException)), mySetupMutualAuthServerIsValidClientException(clientException));
        Assert.assertTrue(serverLatch.await(5, TimeUnit.SECONDS));
        Assert.assertTrue(("unexpected exception: " + (serverException)), mySetupMutualAuthServerIsValidServerException(serverException));
    }

    @Test
    public void testGetCreationTime() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).build();
        SSLEngine engine = null;
        try {
            engine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            Assert.assertTrue(((engine.getSession().getCreationTime()) <= (System.currentTimeMillis())));
        } finally {
            cleanupClientSslEngine(engine);
        }
    }

    @Test
    public void testSessionInvalidate() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            SSLSession session = serverEngine.getSession();
            Assert.assertTrue(session.isValid());
            session.invalidate();
            Assert.assertFalse(session.isValid());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test
    public void testSSLSessionId() throws Exception {
        clientSslCtx = // This test only works for non TLSv1.3 for now
        SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(PROTOCOL_TLS_V1_2).sslContextProvider(clientSslContextProvider()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = // This test only works for non TLSv1.3 for now
        SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(PROTOCOL_TLS_V1_2).sslContextProvider(serverSslContextProvider()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            // Before the handshake the id should have length == 0
            Assert.assertEquals(0, clientEngine.getSession().getId().length);
            Assert.assertEquals(0, serverEngine.getSession().getId().length);
            handshake(clientEngine, serverEngine);
            // After the handshake the id should have length > 0
            Assert.assertNotEquals(0, clientEngine.getSession().getId().length);
            Assert.assertNotEquals(0, serverEngine.getSession().getId().length);
            Assert.assertArrayEquals(clientEngine.getSession().getId(), serverEngine.getSession().getId());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test(timeout = 30000)
    public void clientInitiatedRenegotiationWithFatalAlertDoesNotInfiniteLoopServer() throws InterruptedException, CertificateException, ExecutionException, SSLException {
        Assume.assumeTrue(((PlatformDependent.javaVersion()) >= 11));
        final SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        sb = new ServerBootstrap().group(new NioEventLoopGroup(1)).channel(NioServerSocketChannel.class).childHandler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.config().setAllocator(new SSLEngineTest.TestByteBufAllocator(ch.config().getAllocator(), type));
                ChannelPipeline p = ch.pipeline();
                SslHandler handler = ((delegatingExecutor) == null) ? serverSslCtx.newHandler(ch.alloc()) : serverSslCtx.newHandler(ch.alloc(), delegatingExecutor);
                p.addLast(handler);
                p.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                        if ((evt instanceof SslHandshakeCompletionEvent) && (isSuccess())) {
                            // This data will be sent to the client before any of the re-negotiation data can be
                            // sent. The client will read this, detect that it is not the response to
                            // renegotiation which was expected, and respond with a fatal alert.
                            ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(100));
                        }
                        ctx.fireUserEventTriggered(evt);
                    }

                    @Override
                    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
                        ReferenceCountUtil.release(msg);
                        // The server then attempts to trigger a flush operation once the application data is
                        // received from the client. The flush will encrypt all data and should not result in
                        // deadlock.
                        ctx.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(101));
                            }
                        }, 500, TimeUnit.MILLISECONDS);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) {
                        serverLatch.countDown();
                    }
                });
                serverConnectedChannel = ch;
            }
        });
        serverChannel = sb.bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
        clientSslCtx = // OpenSslEngine doesn't support renegotiation on client side
        SslContextBuilder.forClient().sslProvider(JDK).trustManager(INSTANCE).protocols(protocols()).ciphers(ciphers()).build();
        cb = new Bootstrap();
        cb.group(new NioEventLoopGroup(1)).channel(NioSocketChannel.class).handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.config().setAllocator(new SSLEngineTest.TestByteBufAllocator(ch.config().getAllocator(), type));
                ChannelPipeline p = ch.pipeline();
                SslHandler sslHandler = ((delegatingExecutor) == null) ? clientSslCtx.newHandler(ch.alloc()) : clientSslCtx.newHandler(ch.alloc(), delegatingExecutor);
                // The renegotiate is not expected to succeed, so we should stop trying in a timely manner so
                // the unit test can terminate relativley quicly.
                sslHandler.setHandshakeTimeout(1, TimeUnit.SECONDS);
                p.addLast(sslHandler);
                p.addLast(new ChannelInboundHandlerAdapter() {
                    private int handshakeCount;

                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                        // OpenSSL SSLEngine sends a fatal alert for the renegotiation handshake because the
                        // user data read as part of the handshake. The client receives this fatal alert and is
                        // expected to shutdown the connection. The "invalid data" during the renegotiation
                        // handshake is also delivered to channelRead(..) on the server.
                        // JDK SSLEngine completes the renegotiation handshake and delivers the "invalid data"
                        // is also delivered to channelRead(..) on the server. JDK SSLEngine does not send a
                        // fatal error and so for testing purposes we close the connection after we have
                        // completed the first renegotiation handshake (which is the second handshake).
                        if ((evt instanceof SslHandshakeCompletionEvent) && ((++(handshakeCount)) == 2)) {
                            ctx.close();
                            return;
                        }
                        ctx.fireUserEventTriggered(evt);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                        ReferenceCountUtil.release(msg);
                        // Simulate a request that the server's application logic will think is invalid.
                        ctx.writeAndFlush(ctx.alloc().buffer(1).writeByte(102));
                        ctx.pipeline().get(SslHandler.class).renegotiate();
                    }
                });
            }
        });
        ChannelFuture ccf = cb.connect(serverChannel.localAddress());
        Assert.assertTrue(isSuccess());
        clientChannel = ccf.channel();
        serverLatch.await();
        ssc.delete();
    }

    @Test(timeout = 30000)
    public void testMutualAuthSameCertChain() throws Exception {
        serverSslCtx = SslContextBuilder.forServer(new ByteArrayInputStream(SSLEngineTest.X509_CERT_PEM.getBytes(UTF_8)), new ByteArrayInputStream(SSLEngineTest.PRIVATE_KEY_PEM.getBytes(UTF_8))).trustManager(new ByteArrayInputStream(SSLEngineTest.X509_CERT_PEM.getBytes(UTF_8))).clientAuth(REQUIRE).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        sb = new ServerBootstrap();
        sb.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        sb.channel(NioServerSocketChannel.class);
        final Promise<String> promise = sb.config().group().next().newPromise();
        serverChannel = sb.childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.config().setAllocator(new SSLEngineTest.TestByteBufAllocator(ch.config().getAllocator(), type));
                SslHandler sslHandler = ((delegatingExecutor) == null) ? serverSslCtx.newHandler(ch.alloc()) : serverSslCtx.newHandler(ch.alloc(), delegatingExecutor);
                ch.pipeline().addFirst(sslHandler);
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        if (evt instanceof SslHandshakeCompletionEvent) {
                            Throwable cause = cause();
                            if (cause == null) {
                                SSLSession session = engine().getSession();
                                X509Certificate[] peerCertificateChain = session.getPeerCertificateChain();
                                Certificate[] peerCertificates = session.getPeerCertificates();
                                if (peerCertificateChain == null) {
                                    promise.setFailure(new NullPointerException("peerCertificateChain"));
                                } else
                                    if (peerCertificates == null) {
                                        promise.setFailure(new NullPointerException("peerCertificates"));
                                    } else
                                        if (((peerCertificateChain.length) + (peerCertificates.length)) != 4) {
                                            String excTxtFmt = "peerCertificateChain.length:%s, peerCertificates.length:%s";
                                            promise.setFailure(new IllegalStateException(String.format(excTxtFmt, peerCertificateChain.length, peerCertificates.length)));
                                        } else {
                                            for (int i = 0; i < (peerCertificateChain.length); i++) {
                                                if (((peerCertificateChain[i]) == null) || ((peerCertificates[i]) == null)) {
                                                    promise.setFailure(new IllegalStateException("Certificate in chain is null"));
                                                    return;
                                                }
                                            }
                                            promise.setSuccess(null);
                                        }


                            } else {
                                promise.setFailure(cause);
                            }
                        }
                    }
                });
                serverConnectedChannel = ch;
            }
        }).bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
        clientSslCtx = SslContextBuilder.forClient().keyManager(new ByteArrayInputStream(SSLEngineTest.CLIENT_X509_CERT_CHAIN_PEM.getBytes(UTF_8)), new ByteArrayInputStream(SSLEngineTest.CLIENT_PRIVATE_KEY_PEM.getBytes(UTF_8))).trustManager(new ByteArrayInputStream(SSLEngineTest.X509_CERT_PEM.getBytes(UTF_8))).sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        cb = new Bootstrap();
        cb.group(new NioEventLoopGroup());
        cb.channel(NioSocketChannel.class);
        clientChannel = cb.handler(new io.netty.channel.ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.config().setAllocator(new SSLEngineTest.TestByteBufAllocator(ch.config().getAllocator(), type));
                ch.pipeline().addLast(new SslHandler(wrapEngine(clientSslCtx.newEngine(ch.alloc()))));
            }
        }).connect(serverChannel.localAddress()).syncUninterruptibly().channel();
        promise.syncUninterruptibly();
    }

    @Test
    public void testUnwrapBehavior() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        byte[] bytes = "Hello World".getBytes(US_ASCII);
        try {
            ByteBuffer plainClientOut = allocateBuffer(client.getSession().getApplicationBufferSize());
            ByteBuffer encryptedClientToServer = allocateBuffer(((server.getSession().getPacketBufferSize()) * 2));
            ByteBuffer plainServerIn = allocateBuffer(server.getSession().getApplicationBufferSize());
            handshake(client, server);
            // create two TLS frames
            // first frame
            plainClientOut.put(bytes, 0, 5);
            plainClientOut.flip();
            SSLEngineResult result = client.wrap(plainClientOut, encryptedClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(5, result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
            Assert.assertFalse(plainClientOut.hasRemaining());
            // second frame
            plainClientOut.clear();
            plainClientOut.put(bytes, 5, 6);
            plainClientOut.flip();
            result = client.wrap(plainClientOut, encryptedClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(6, result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
            // send over to server
            encryptedClientToServer.flip();
            // try with too small output buffer first (to check BUFFER_OVERFLOW case)
            int remaining = encryptedClientToServer.remaining();
            ByteBuffer small = allocateBuffer(3);
            result = server.unwrap(encryptedClientToServer, small);
            Assert.assertEquals(BUFFER_OVERFLOW, result.getStatus());
            Assert.assertEquals(remaining, encryptedClientToServer.remaining());
            // now with big enough buffer
            result = server.unwrap(encryptedClientToServer, plainServerIn);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(5, result.bytesProduced());
            Assert.assertTrue(encryptedClientToServer.hasRemaining());
            result = server.unwrap(encryptedClientToServer, plainServerIn);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(6, result.bytesProduced());
            Assert.assertFalse(encryptedClientToServer.hasRemaining());
            plainServerIn.flip();
            Assert.assertEquals(ByteBuffer.wrap(bytes), plainServerIn);
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testProtocolMatch() throws Exception {
        testProtocol(new String[]{ "TLSv1.2" }, new String[]{ "TLSv1", "TLSv1.1", "TLSv1.2" });
    }

    @Test(expected = SSLHandshakeException.class)
    public void testProtocolNoMatch() throws Exception {
        testProtocol(new String[]{ "TLSv1.2" }, new String[]{ "TLSv1", "TLSv1.1" });
    }

    @Test
    public void testHandshakeCompletesWithNonContiguousProtocolsTLSv1_2CipherOnly() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        // Select a mandatory cipher from the TLSv1.2 RFC https://www.ietf.org/rfc/rfc5246.txt so handshakes won't fail
        // due to no shared/supported cipher.
        final String sharedCipher = "TLS_RSA_WITH_AES_128_CBC_SHA";
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).ciphers(Arrays.asList(sharedCipher)).protocols(PROTOCOL_TLS_V1_2, PROTOCOL_TLS_V1).sslProvider(sslClientProvider()).build();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).ciphers(Arrays.asList(sharedCipher)).protocols(PROTOCOL_TLS_V1_2, PROTOCOL_TLS_V1).sslProvider(sslServerProvider()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test
    public void testHandshakeCompletesWithoutFilteringSupportedCipher() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        // Select a mandatory cipher from the TLSv1.2 RFC https://www.ietf.org/rfc/rfc5246.txt so handshakes won't fail
        // due to no shared/supported cipher.
        final String sharedCipher = "TLS_RSA_WITH_AES_128_CBC_SHA";
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).ciphers(Arrays.asList(sharedCipher), SupportedCipherSuiteFilter.INSTANCE).protocols(PROTOCOL_TLS_V1_2, PROTOCOL_TLS_V1).sslProvider(sslClientProvider()).build();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).ciphers(Arrays.asList(sharedCipher), SupportedCipherSuiteFilter.INSTANCE).protocols(PROTOCOL_TLS_V1_2, PROTOCOL_TLS_V1).sslProvider(sslServerProvider()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test
    public void testPacketBufferSizeLimit() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            // Allocate an buffer that is bigger then the max plain record size.
            ByteBuffer plainServerOut = allocateBuffer(((server.getSession().getApplicationBufferSize()) * 2));
            handshake(client, server);
            // Fill the whole buffer and flip it.
            plainServerOut.position(plainServerOut.capacity());
            plainServerOut.flip();
            ByteBuffer encryptedServerToClient = allocateBuffer(server.getSession().getPacketBufferSize());
            int encryptedServerToClientPos = encryptedServerToClient.position();
            int plainServerOutPos = plainServerOut.position();
            SSLEngineResult result = server.wrap(plainServerOut, encryptedServerToClient);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(((plainServerOut.position()) - plainServerOutPos), result.bytesConsumed());
            Assert.assertEquals(((encryptedServerToClient.position()) - encryptedServerToClientPos), result.bytesProduced());
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testSSLEngineUnwrapNoSslRecord() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer src = allocateBuffer(client.getSession().getApplicationBufferSize());
            ByteBuffer dst = allocateBuffer(client.getSession().getPacketBufferSize());
            ByteBuffer empty = allocateBuffer(0);
            SSLEngineResult clientResult = client.wrap(empty, dst);
            Assert.assertEquals(OK, clientResult.getStatus());
            Assert.assertEquals(NEED_UNWRAP, clientResult.getHandshakeStatus());
            try {
                client.unwrap(src, dst);
                Assert.fail();
            } catch (SSLException expected) {
                // expected
            }
        } finally {
            cleanupClientSslEngine(client);
        }
    }

    @Test
    public void testBeginHandshakeAfterEngineClosed() throws SSLException {
        clientSslCtx = SslContextBuilder.forClient().sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        try {
            client.closeInbound();
            client.closeOutbound();
            try {
                client.beginHandshake();
                Assert.fail();
            } catch (SSLException expected) {
                // expected
            }
        } finally {
            cleanupClientSslEngine(client);
        }
    }

    @Test
    public void testBeginHandshakeCloseOutbound() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            testBeginHandshakeCloseOutbound(client);
            testBeginHandshakeCloseOutbound(server);
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testCloseInboundAfterBeginHandshake() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            SSLEngineTest.testCloseInboundAfterBeginHandshake(client);
            SSLEngineTest.testCloseInboundAfterBeginHandshake(server);
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testCloseNotifySequence() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = // This test only works for non TLSv1.3 for now
        SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(PROTOCOL_TLS_V1_2).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = // This test only works for non TLSv1.3 for now
        SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(PROTOCOL_TLS_V1_2).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer plainClientOut = allocateBuffer(client.getSession().getApplicationBufferSize());
            ByteBuffer plainServerOut = allocateBuffer(server.getSession().getApplicationBufferSize());
            ByteBuffer encryptedClientToServer = allocateBuffer(client.getSession().getPacketBufferSize());
            ByteBuffer encryptedServerToClient = allocateBuffer(server.getSession().getPacketBufferSize());
            ByteBuffer empty = allocateBuffer(0);
            handshake(client, server);
            // This will produce a close_notify
            client.closeOutbound();
            // Something still pending in the outbound buffer.
            Assert.assertFalse(client.isOutboundDone());
            Assert.assertFalse(client.isInboundDone());
            // Now wrap and so drain the outbound buffer.
            SSLEngineResult result = client.wrap(empty, encryptedClientToServer);
            encryptedClientToServer.flip();
            Assert.assertEquals(CLOSED, result.getStatus());
            // Need an UNWRAP to read the response of the close_notify
            if (((PlatformDependent.javaVersion()) >= 12) && ((sslClientProvider()) == (JDK))) {
                // This is a workaround for a possible JDK12+ bug.
                // 
                // See http://mail.openjdk.java.net/pipermail/security-dev/2019-February/019406.html.
                Assert.assertEquals(NOT_HANDSHAKING, result.getHandshakeStatus());
            } else {
                Assert.assertEquals(NEED_UNWRAP, result.getHandshakeStatus());
            }
            int produced = result.bytesProduced();
            int consumed = result.bytesConsumed();
            int closeNotifyLen = produced;
            Assert.assertTrue((produced > 0));
            Assert.assertEquals(0, consumed);
            Assert.assertEquals(produced, encryptedClientToServer.remaining());
            // Outbound buffer should be drained now.
            Assert.assertTrue(client.isOutboundDone());
            Assert.assertFalse(client.isInboundDone());
            Assert.assertFalse(server.isOutboundDone());
            Assert.assertFalse(server.isInboundDone());
            result = server.unwrap(encryptedClientToServer, plainServerOut);
            plainServerOut.flip();
            Assert.assertEquals(CLOSED, result.getStatus());
            // Need a WRAP to respond to the close_notify
            Assert.assertEquals(NEED_WRAP, result.getHandshakeStatus());
            produced = result.bytesProduced();
            consumed = result.bytesConsumed();
            Assert.assertEquals(closeNotifyLen, consumed);
            Assert.assertEquals(0, produced);
            // Should have consumed the complete close_notify
            Assert.assertEquals(0, encryptedClientToServer.remaining());
            Assert.assertEquals(0, plainServerOut.remaining());
            Assert.assertFalse(server.isOutboundDone());
            Assert.assertTrue(server.isInboundDone());
            result = server.wrap(empty, encryptedServerToClient);
            encryptedServerToClient.flip();
            Assert.assertEquals(CLOSED, result.getStatus());
            // UNWRAP/WRAP are not expected after this point
            Assert.assertEquals(NOT_HANDSHAKING, result.getHandshakeStatus());
            produced = result.bytesProduced();
            consumed = result.bytesConsumed();
            Assert.assertEquals(closeNotifyLen, produced);
            Assert.assertEquals(0, consumed);
            Assert.assertEquals(produced, encryptedServerToClient.remaining());
            Assert.assertTrue(server.isOutboundDone());
            Assert.assertTrue(server.isInboundDone());
            result = client.unwrap(encryptedServerToClient, plainClientOut);
            plainClientOut.flip();
            Assert.assertEquals(CLOSED, result.getStatus());
            // UNWRAP/WRAP are not expected after this point
            Assert.assertEquals(NOT_HANDSHAKING, result.getHandshakeStatus());
            produced = result.bytesProduced();
            consumed = result.bytesConsumed();
            Assert.assertEquals(closeNotifyLen, consumed);
            Assert.assertEquals(0, produced);
            Assert.assertEquals(0, encryptedServerToClient.remaining());
            Assert.assertTrue(client.isOutboundDone());
            Assert.assertTrue(client.isInboundDone());
            // Ensure that calling wrap or unwrap again will not produce a SSLException
            encryptedServerToClient.clear();
            plainServerOut.clear();
            result = server.wrap(plainServerOut, encryptedServerToClient);
            SSLEngineTest.assertEngineRemainsClosed(result);
            encryptedClientToServer.clear();
            plainServerOut.clear();
            result = server.unwrap(encryptedClientToServer, plainServerOut);
            SSLEngineTest.assertEngineRemainsClosed(result);
            encryptedClientToServer.clear();
            plainClientOut.clear();
            result = client.wrap(plainClientOut, encryptedClientToServer);
            SSLEngineTest.assertEngineRemainsClosed(result);
            encryptedServerToClient.clear();
            plainClientOut.clear();
            result = client.unwrap(encryptedServerToClient, plainClientOut);
            SSLEngineTest.assertEngineRemainsClosed(result);
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testWrapAfterCloseOutbound() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer dst = allocateBuffer(client.getSession().getPacketBufferSize());
            ByteBuffer src = allocateBuffer(1024);
            handshake(client, server);
            // This will produce a close_notify
            client.closeOutbound();
            SSLEngineResult result = client.wrap(src, dst);
            Assert.assertEquals(CLOSED, result.getStatus());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
            Assert.assertTrue(client.isOutboundDone());
            Assert.assertFalse(client.isInboundDone());
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testMultipleRecordsInOneBufferWithNonZeroPosition() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            // Choose buffer size small enough that we can put multiple buffers into one buffer and pass it into the
            // unwrap call without exceed MAX_ENCRYPTED_PACKET_LENGTH.
            ByteBuffer plainClientOut = allocateBuffer(1024);
            ByteBuffer plainServerOut = allocateBuffer(server.getSession().getApplicationBufferSize());
            ByteBuffer encClientToServer = allocateBuffer(client.getSession().getPacketBufferSize());
            int positionOffset = 1;
            // We need to be able to hold 2 records + positionOffset
            ByteBuffer combinedEncClientToServer = allocateBuffer((((encClientToServer.capacity()) * 2) + positionOffset));
            combinedEncClientToServer.position(positionOffset);
            handshake(client, server);
            plainClientOut.limit(plainClientOut.capacity());
            SSLEngineResult result = client.wrap(plainClientOut, encClientToServer);
            Assert.assertEquals(plainClientOut.capacity(), result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
            encClientToServer.flip();
            // Copy the first record into the combined buffer
            combinedEncClientToServer.put(encClientToServer);
            plainClientOut.clear();
            encClientToServer.clear();
            result = client.wrap(plainClientOut, encClientToServer);
            Assert.assertEquals(plainClientOut.capacity(), result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
            encClientToServer.flip();
            int encClientToServerLen = encClientToServer.remaining();
            // Copy the first record into the combined buffer
            combinedEncClientToServer.put(encClientToServer);
            encClientToServer.clear();
            combinedEncClientToServer.flip();
            combinedEncClientToServer.position(positionOffset);
            // Ensure we have the first record and a tiny amount of the second record in the buffer
            combinedEncClientToServer.limit(((combinedEncClientToServer.limit()) - (encClientToServerLen - positionOffset)));
            result = server.unwrap(combinedEncClientToServer, plainServerOut);
            Assert.assertEquals(encClientToServerLen, result.bytesConsumed());
            Assert.assertTrue(((result.bytesProduced()) > 0));
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testMultipleRecordsInOneBufferBiggerThenPacketBufferSize() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer plainClientOut = allocateBuffer(4096);
            ByteBuffer plainServerOut = allocateBuffer(server.getSession().getApplicationBufferSize());
            ByteBuffer encClientToServer = allocateBuffer(((server.getSession().getPacketBufferSize()) * 2));
            handshake(client, server);
            int srcLen = plainClientOut.remaining();
            SSLEngineResult result;
            int count = 0;
            do {
                int plainClientOutPosition = plainClientOut.position();
                int encClientToServerPosition = encClientToServer.position();
                result = client.wrap(plainClientOut, encClientToServer);
                if ((result.getStatus()) == (BUFFER_OVERFLOW)) {
                    // We did not have enough room to wrap
                    Assert.assertEquals(plainClientOutPosition, plainClientOut.position());
                    Assert.assertEquals(encClientToServerPosition, encClientToServer.position());
                    break;
                }
                Assert.assertEquals(OK, result.getStatus());
                Assert.assertEquals(srcLen, result.bytesConsumed());
                Assert.assertTrue(((result.bytesProduced()) > 0));
                plainClientOut.clear();
                ++count;
            } while ((encClientToServer.position()) < (server.getSession().getPacketBufferSize()) );
            // Check that we were able to wrap multiple times.
            Assert.assertTrue((count >= 2));
            encClientToServer.flip();
            result = server.unwrap(encClientToServer, plainServerOut);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertTrue(((result.bytesConsumed()) > 0));
            Assert.assertTrue(((result.bytesProduced()) > 0));
            Assert.assertTrue(encClientToServer.hasRemaining());
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testBufferUnderFlow() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer plainClient = allocateBuffer(1024);
            plainClient.limit(plainClient.capacity());
            ByteBuffer encClientToServer = allocateBuffer(client.getSession().getPacketBufferSize());
            ByteBuffer plainServer = allocateBuffer(server.getSession().getApplicationBufferSize());
            handshake(client, server);
            SSLEngineResult result = client.wrap(plainClient, encClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), plainClient.capacity());
            // Flip so we can read it.
            encClientToServer.flip();
            int remaining = encClientToServer.remaining();
            // We limit the buffer so we have less then the header to read, this should result in an BUFFER_UNDERFLOW.
            encClientToServer.limit(((SSL_RECORD_HEADER_LENGTH) - 1));
            result = server.unwrap(encClientToServer, plainServer);
            SSLEngineTest.assertResultIsBufferUnderflow(result);
            // We limit the buffer so we can read the header but not the rest, this should result in an
            // BUFFER_UNDERFLOW.
            encClientToServer.limit(SSL_RECORD_HEADER_LENGTH);
            result = server.unwrap(encClientToServer, plainServer);
            SSLEngineTest.assertResultIsBufferUnderflow(result);
            // We limit the buffer so we can read the header and partly the rest, this should result in an
            // BUFFER_UNDERFLOW.
            encClientToServer.limit(((((SSL_RECORD_HEADER_LENGTH) + remaining) - 1) - (SSL_RECORD_HEADER_LENGTH)));
            result = server.unwrap(encClientToServer, plainServer);
            SSLEngineTest.assertResultIsBufferUnderflow(result);
            // Reset limit so we can read the full record.
            encClientToServer.limit(remaining);
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), remaining);
            Assert.assertTrue(((result.bytesProduced()) > 0));
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testWrapDoesNotZeroOutSrc() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT));
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            ByteBuffer plainServerOut = allocateBuffer(((server.getSession().getApplicationBufferSize()) / 2));
            handshake(client, server);
            // Fill the whole buffer and flip it.
            for (int i = 0; i < (plainServerOut.capacity()); i++) {
                plainServerOut.put(i, ((byte) (i)));
            }
            plainServerOut.position(plainServerOut.capacity());
            plainServerOut.flip();
            ByteBuffer encryptedServerToClient = allocateBuffer(server.getSession().getPacketBufferSize());
            SSLEngineResult result = server.wrap(plainServerOut, encryptedServerToClient);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertTrue(((result.bytesConsumed()) > 0));
            for (int i = 0; i < (plainServerOut.capacity()); i++) {
                Assert.assertEquals(((byte) (i)), plainServerOut.get(i));
            }
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testDisableProtocols() throws Exception {
        testDisableProtocols(PROTOCOL_SSL_V2, PROTOCOL_SSL_V2);
        testDisableProtocols(PROTOCOL_SSL_V3, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3);
        testDisableProtocols(PROTOCOL_TLS_V1, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3, PROTOCOL_TLS_V1);
        testDisableProtocols(PROTOCOL_TLS_V1_1, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3, PROTOCOL_TLS_V1, PROTOCOL_TLS_V1_1);
        testDisableProtocols(PROTOCOL_TLS_V1_2, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3, PROTOCOL_TLS_V1, PROTOCOL_TLS_V1_1, PROTOCOL_TLS_V1_2);
    }

    @Test
    public void testUsingX509TrustManagerVerifiesHostname() throws Exception {
        SslProvider clientProvider = sslClientProvider();
        if ((clientProvider == (OPENSSL)) || (clientProvider == (OPENSSL_REFCNT))) {
            // Need to check if we support hostname validation in the current used OpenSSL version before running
            // the test.
            Assume.assumeTrue(OpenSsl.supportsHostnameValidation());
        }
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(new TrustManagerFactory(new TrustManagerFactorySpi() {
            @Override
            protected void engineInit(KeyStore keyStore) {
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[]{ new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return EmptyArrays.EMPTY_X509_CERTIFICATES;
                    }
                } };
            }

            @Override
            protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
            }
        }, null, TrustManagerFactory.getDefaultAlgorithm()) {}).sslProvider(sslClientProvider()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newEngine(DEFAULT, "netty.io", 1234));
        SSLParameters sslParameters = client.getSSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        client.setSSLParameters(sslParameters);
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            handshake(client, server);
            Assert.fail();
        } catch (SSLException expected) {
            // expected as the hostname not matches.
        } finally {
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
            cert.delete();
        }
    }

    @Test
    public void testInvalidCipher() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        List<String> cipherList = new ArrayList<String>();
        Collections.addAll(cipherList, ((SSLSocketFactory) (SSLSocketFactory.getDefault())).getDefaultCipherSuites());
        cipherList.add("InvalidCipher");
        SSLEngine server = null;
        try {
            serverSslCtx = SslContextBuilder.forServer(cert.key(), cert.cert()).sslProvider(sslClientProvider()).ciphers(cipherList).build();
            server = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            // expected when invalid cipher is used.
        } catch (SSLException expected) {
            // expected when invalid cipher is used.
        } finally {
            cert.delete();
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testGetCiphersuite() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            String clientCipher = clientEngine.getSession().getCipherSuite();
            String serverCipher = serverEngine.getSession().getCipherSuite();
            Assert.assertEquals(clientCipher, serverCipher);
            Assert.assertEquals(protocolCipherCombo.cipher, clientCipher);
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test
    public void testSessionBindingEvent() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            SSLSession session = clientEngine.getSession();
            Assert.assertEquals(0, session.getValueNames().length);
            class SSLSessionBindingEventValue implements SSLSessionBindingListener {
                SSLSessionBindingEvent boundEvent;

                SSLSessionBindingEvent unboundEvent;

                @Override
                public void valueBound(SSLSessionBindingEvent sslSessionBindingEvent) {
                    Assert.assertNull(boundEvent);
                    boundEvent = sslSessionBindingEvent;
                }

                @Override
                public void valueUnbound(SSLSessionBindingEvent sslSessionBindingEvent) {
                    Assert.assertNull(unboundEvent);
                    unboundEvent = sslSessionBindingEvent;
                }
            }
            String name = "name";
            String name2 = "name2";
            SSLSessionBindingEventValue value1 = new SSLSessionBindingEventValue();
            session.putValue(name, value1);
            SSLEngineTest.assertSSLSessionBindingEventValue(name, session, value1.boundEvent);
            Assert.assertNull(value1.unboundEvent);
            Assert.assertEquals(1, session.getValueNames().length);
            session.putValue(name2, "value");
            SSLSessionBindingEventValue value2 = new SSLSessionBindingEventValue();
            session.putValue(name, value2);
            Assert.assertEquals(2, session.getValueNames().length);
            SSLEngineTest.assertSSLSessionBindingEventValue(name, session, value1.unboundEvent);
            SSLEngineTest.assertSSLSessionBindingEventValue(name, session, value2.boundEvent);
            Assert.assertNull(value2.unboundEvent);
            Assert.assertEquals(2, session.getValueNames().length);
            session.removeValue(name);
            SSLEngineTest.assertSSLSessionBindingEventValue(name, session, value2.unboundEvent);
            Assert.assertEquals(1, session.getValueNames().length);
            session.removeValue(name2);
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    @Test
    public void testSessionAfterHandshake() throws Exception {
        testSessionAfterHandshake0(false, false);
    }

    @Test
    public void testSessionAfterHandshakeMutualAuth() throws Exception {
        testSessionAfterHandshake0(false, true);
    }

    @Test
    public void testSessionAfterHandshakeKeyManagerFactory() throws Exception {
        testSessionAfterHandshake0(true, false);
    }

    @Test
    public void testSessionAfterHandshakeKeyManagerFactoryMutualAuth() throws Exception {
        testSessionAfterHandshake0(true, true);
    }

    @Test
    public void testHandshakeSession() throws Exception {
        final SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SSLEngineTest.TestTrustManagerFactory clientTmf = new SSLEngineTest.TestTrustManagerFactory(ssc.cert());
        final SSLEngineTest.TestTrustManagerFactory serverTmf = new SSLEngineTest.TestTrustManagerFactory(ssc.cert());
        clientSslCtx = SslContextBuilder.forClient().trustManager(new SimpleTrustManagerFactory() {
            @Override
            protected void engineInit(KeyStore keyStore) {
                // NOOP
            }

            @Override
            protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
                // NOOP
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[]{ clientTmf };
            }
        }).keyManager(newKeyManagerFactory(ssc)).sslProvider(sslClientProvider()).sslContextProvider(clientSslContextProvider()).protocols(protocols()).ciphers(ciphers()).build();
        serverSslCtx = SslContextBuilder.forServer(newKeyManagerFactory(ssc)).trustManager(new SimpleTrustManagerFactory() {
            @Override
            protected void engineInit(KeyStore keyStore) {
                // NOOP
            }

            @Override
            protected void engineInit(ManagerFactoryParameters managerFactoryParameters) {
                // NOOP
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[]{ serverTmf };
            }
        }).sslProvider(sslServerProvider()).sslContextProvider(serverSslContextProvider()).protocols(protocols()).ciphers(ciphers()).clientAuth(REQUIRE).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            Assert.assertTrue(clientTmf.isVerified());
            Assert.assertTrue(serverTmf.isVerified());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
            ssc.delete();
        }
    }

    private final class TestTrustManagerFactory extends X509ExtendedTrustManager {
        private final Certificate localCert;

        private volatile boolean verified;

        TestTrustManagerFactory(Certificate localCert) {
            this.localCert = localCert;
        }

        boolean isVerified() {
            return verified;
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s, Socket socket) {
            Assert.fail();
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s, Socket socket) {
            Assert.fail();
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
            verified = true;
            Assert.assertFalse(sslEngine.getUseClientMode());
            SSLSession session = sslEngine.getHandshakeSession();
            Assert.assertNotNull(session);
            Certificate[] localCertificates = session.getLocalCertificates();
            Assert.assertNotNull(localCertificates);
            Assert.assertEquals(1, localCertificates.length);
            Assert.assertEquals(localCert, localCertificates[0]);
            Assert.assertNotNull(session.getLocalPrincipal());
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
            verified = true;
            Assert.assertTrue(sslEngine.getUseClientMode());
            SSLSession session = sslEngine.getHandshakeSession();
            Assert.assertNotNull(session);
            Assert.assertNull(session.getLocalCertificates());
            Assert.assertNull(session.getLocalPrincipal());
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
            Assert.fail();
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
            Assert.fail();
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    }
}

