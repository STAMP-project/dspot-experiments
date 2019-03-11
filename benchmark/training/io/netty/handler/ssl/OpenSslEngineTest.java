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


import CharsetUtil.UTF_8;
import EmptyArrays.EMPTY_BYTES;
import InsecureTrustManagerFactory.INSTANCE;
import Protocol.ALPN;
import Protocol.NPN;
import SslUtils.SSL_RECORD_HEADER_LENGTH;
import UnpooledByteBufAllocator.DEFAULT;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static SslUtils.SSL_RECORD_HEADER_LENGTH;
import static javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;
import static javax.net.ssl.SSLEngineResult.Status.BUFFER_UNDERFLOW;
import static javax.net.ssl.SSLEngineResult.Status.OK;


@RunWith(Parameterized.class)
public class OpenSslEngineTest extends SSLEngineTest {
    private static final String PREFERRED_APPLICATION_LEVEL_PROTOCOL = "my-protocol-http2";

    private static final String FALLBACK_APPLICATION_LEVEL_PROTOCOL = "my-protocol-http1_1";

    public OpenSslEngineTest(SSLEngineTest.BufferType type, SSLEngineTest.ProtocolCipherCombo cipherCombo, boolean delegate) {
        super(type, cipherCombo, delegate);
    }

    @Override
    @Test
    public void testSessionAfterHandshakeKeyManagerFactory() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testSessionAfterHandshakeKeyManagerFactory();
    }

    @Override
    @Test
    public void testSessionAfterHandshakeKeyManagerFactoryMutualAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testSessionAfterHandshakeKeyManagerFactoryMutualAuth();
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCASucceedWithOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCASucceedWithOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCAFailWithOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithRequiredClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCAFailWithRequiredClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthValidClientCertChainTooLongFailOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthValidClientCertChainTooLongFailOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthValidClientCertChainTooLongFailRequireClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthValidClientCertChainTooLongFailRequireClientAuth();
    }

    @Override
    @Test
    public void testClientHostnameValidationSuccess() throws InterruptedException, SSLException {
        Assume.assumeTrue(OpenSsl.supportsHostnameValidation());
        super.testClientHostnameValidationSuccess();
    }

    @Override
    @Test
    public void testClientHostnameValidationFail() throws InterruptedException, SSLException {
        Assume.assumeTrue(OpenSsl.supportsHostnameValidation());
        super.testClientHostnameValidationFail();
    }

    @Test
    public void testNpn() throws Exception {
        String versionString = OpenSsl.versionString();
        Assume.assumeTrue(("LibreSSL 2.6.1 removed NPN support, detected " + versionString), OpenSslEngineTest.isNpnSupported(versionString));
        ApplicationProtocolConfig apn = OpenSslEngineTest.acceptingNegotiator(NPN, OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
        setupHandlers(apn);
        runTest(OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
    }

    @Test
    public void testAlpn() throws Exception {
        Assume.assumeTrue(OpenSsl.isAlpnSupported());
        ApplicationProtocolConfig apn = OpenSslEngineTest.acceptingNegotiator(ALPN, OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
        setupHandlers(apn);
        runTest(OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
    }

    @Test
    public void testAlpnCompatibleProtocolsDifferentClientOrder() throws Exception {
        Assume.assumeTrue(OpenSsl.isAlpnSupported());
        ApplicationProtocolConfig clientApn = OpenSslEngineTest.acceptingNegotiator(ALPN, OpenSslEngineTest.FALLBACK_APPLICATION_LEVEL_PROTOCOL, OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
        ApplicationProtocolConfig serverApn = OpenSslEngineTest.acceptingNegotiator(ALPN, OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL, OpenSslEngineTest.FALLBACK_APPLICATION_LEVEL_PROTOCOL);
        setupHandlers(serverApn, clientApn);
        Assert.assertNull(serverException);
        runTest(OpenSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
    }

    @Test
    public void testEnablingAnAlreadyDisabledSslProtocol() throws Exception {
        testEnablingAnAlreadyDisabledSslProtocol(new String[]{ SslUtils.PROTOCOL_SSL_V2_HELLO }, new String[]{ SslUtils.PROTOCOL_SSL_V2_HELLO, SslUtils.PROTOCOL_TLS_V1_2 });
    }

    @Test
    public void testWrapBuffersNoWritePendingError() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            ByteBuffer src = allocateBuffer((1024 * 10));
            byte[] data = new byte[src.capacity()];
            PlatformDependent.threadLocalRandom().nextBytes(data);
            src.put(data).flip();
            ByteBuffer dst = allocateBuffer(1);
            // Try to wrap multiple times so we are more likely to hit the issue.
            for (int i = 0; i < 100; i++) {
                src.position(0);
                dst.position(0);
                Assert.assertSame(BUFFER_OVERFLOW, clientEngine.wrap(src, dst).getStatus());
            }
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
        }
    }

    @Test
    public void testOnlySmallBufferNeededForWrap() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            // Allocate a buffer which is small enough and set the limit to the capacity to mark its whole content
            // as readable.
            int srcLen = 1024;
            ByteBuffer src = allocateBuffer(srcLen);
            ByteBuffer dstTooSmall = allocateBuffer((((src.capacity()) + (unwrapEngine(clientEngine).maxWrapOverhead())) - 1));
            ByteBuffer dst = allocateBuffer(((src.capacity()) + (unwrapEngine(clientEngine).maxWrapOverhead())));
            // Check that we fail to wrap if the dst buffers capacity is not at least
            // src.capacity() + ReferenceCountedOpenSslEngine.MAX_TLS_RECORD_OVERHEAD_LENGTH
            SSLEngineResult result = clientEngine.wrap(src, dstTooSmall);
            Assert.assertEquals(BUFFER_OVERFLOW, result.getStatus());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            Assert.assertEquals(src.remaining(), src.capacity());
            Assert.assertEquals(dst.remaining(), dst.capacity());
            // Check that we can wrap with a dst buffer that has the capacity of
            // src.capacity() + ReferenceCountedOpenSslEngine.MAX_TLS_RECORD_OVERHEAD_LENGTH
            result = clientEngine.wrap(src, dst);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(srcLen, result.bytesConsumed());
            Assert.assertEquals(0, src.remaining());
            Assert.assertTrue(((result.bytesProduced()) > srcLen));
            Assert.assertEquals(((src.capacity()) - (result.bytesConsumed())), src.remaining());
            Assert.assertEquals(((dst.capacity()) - (result.bytesProduced())), dst.remaining());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
        }
    }

    @Test
    public void testNeededDstCapacityIsCorrectlyCalculated() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            ByteBuffer src = allocateBuffer(1024);
            ByteBuffer src2 = src.duplicate();
            ByteBuffer dst = allocateBuffer(((src.capacity()) + (unwrapEngine(clientEngine).maxWrapOverhead())));
            SSLEngineResult result = clientEngine.wrap(new ByteBuffer[]{ src, src2 }, dst);
            Assert.assertEquals(BUFFER_OVERFLOW, result.getStatus());
            Assert.assertEquals(0, src.position());
            Assert.assertEquals(0, src2.position());
            Assert.assertEquals(0, dst.position());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
        }
    }

    @Test
    public void testSrcsLenOverFlowCorrectlyHandled() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        SSLEngine serverEngine = null;
        try {
            clientEngine = wrapEngine(clientSslCtx.newEngine(DEFAULT));
            serverEngine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
            handshake(clientEngine, serverEngine);
            ByteBuffer src = allocateBuffer(1024);
            List<ByteBuffer> srcList = new ArrayList<ByteBuffer>();
            long srcsLen = 0;
            long maxLen = ((long) (Integer.MAX_VALUE)) * 2;
            while (srcsLen < maxLen) {
                ByteBuffer dup = src.duplicate();
                srcList.add(dup);
                srcsLen += dup.capacity();
            } 
            ByteBuffer[] srcs = srcList.toArray(new ByteBuffer[0]);
            ByteBuffer dst = allocateBuffer(((unwrapEngine(clientEngine).maxEncryptedPacketLength()) - 1));
            SSLEngineResult result = clientEngine.wrap(srcs, dst);
            Assert.assertEquals(BUFFER_OVERFLOW, result.getStatus());
            for (ByteBuffer buffer : srcs) {
                Assert.assertEquals(0, buffer.position());
            }
            Assert.assertEquals(0, dst.position());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
        } finally {
            cleanupClientSslEngine(clientEngine);
            cleanupServerSslEngine(serverEngine);
        }
    }

    @Test
    public void testCalculateOutNetBufSizeOverflow() throws SSLException {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        try {
            clientEngine = clientSslCtx.newEngine(DEFAULT);
            int value = calculateMaxLengthForWrap(Integer.MAX_VALUE, 1);
            Assert.assertTrue(("unexpected value: " + value), (value > 0));
        } finally {
            cleanupClientSslEngine(clientEngine);
        }
    }

    @Test
    public void testCalculateOutNetBufSize0() throws SSLException {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine clientEngine = null;
        try {
            clientEngine = clientSslCtx.newEngine(DEFAULT);
            Assert.assertTrue(((((ReferenceCountedOpenSslEngine) (clientEngine)).calculateMaxLengthForWrap(0, 1)) > 0));
        } finally {
            cleanupClientSslEngine(clientEngine);
        }
    }

    @Test
    public void testCorrectlyCalculateSpaceForAlert() throws Exception {
        testCorrectlyCalculateSpaceForAlert(true);
    }

    @Test
    public void testCorrectlyCalculateSpaceForAlertJDKCompatabilityModeOff() throws Exception {
        testCorrectlyCalculateSpaceForAlert(false);
    }

    @Test
    public void testWrapWithDifferentSizesTLSv1() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).build();
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ECDHE-RSA-AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "AECDH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ADH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "EDH-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ADH-RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "IDEA-CBC-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "AECDH-RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ECDHE-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ECDHE-RSA-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1, "ECDHE-RSA-RC4-SHA");
    }

    @Test
    public void testWrapWithDifferentSizesTLSv1_1() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).build();
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "ECDHE-RSA-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "ECDHE-RSA-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "IDEA-CBC-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "AECDH-RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "ADH-RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "ECDHE-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "EDH-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "AECDH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "ADH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_1, "DES-CBC3-SHA");
    }

    @Test
    public void testWrapWithDifferentSizesTLSv1_2() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).build();
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES128-GCM-SHA256");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES256-SHA384");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AECDH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES256-GCM-SHA384");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES256-SHA256");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES128-GCM-SHA256");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES128-SHA256");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ADH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "EDH-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ADH-RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AES128-SHA256");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "AECDH-RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES256-GCM-SHA384");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_TLS_V1_2, "ECDHE-RSA-RC4-SHA");
    }

    @Test
    public void testWrapWithDifferentSizesSSLv3() throws Exception {
        clientSslCtx = SslContextBuilder.forClient().trustManager(INSTANCE).sslProvider(sslClientProvider()).build();
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).build();
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "AECDH-AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "AECDH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "DHE-RSA-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "EDH-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-RC4-MD5");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "IDEA-CBC-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "DHE-RSA-AES128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "AECDH-RC4-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "DHE-RSA-SEED-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "AECDH-AES256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ECDHE-RSA-DES-CBC3-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ADH-CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "DHE-RSA-CAMELLIA256-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "DHE-RSA-CAMELLIA128-SHA");
        testWrapWithDifferentSizes(SslUtils.PROTOCOL_SSL_V3, "ECDHE-RSA-RC4-SHA");
    }

    @Test
    public void testMultipleRecordsInOneBufferWithNonZeroPositionJDKCompatabilityModeOff() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newHandler(DEFAULT).engine());
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newHandler(DEFAULT).engine());
        try {
            // Choose buffer size small enough that we can put multiple buffers into one buffer and pass it into the
            // unwrap call without exceed MAX_ENCRYPTED_PACKET_LENGTH.
            final int plainClientOutLen = 1024;
            ByteBuffer plainClientOut = allocateBuffer(plainClientOutLen);
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
            // Copy the first record into the combined buffer
            combinedEncClientToServer.put(encClientToServer);
            encClientToServer.clear();
            combinedEncClientToServer.flip();
            combinedEncClientToServer.position(positionOffset);
            // Make sure the limit takes positionOffset into account to the content we are looking at is correct.
            combinedEncClientToServer.limit(((combinedEncClientToServer.limit()) - positionOffset));
            final int combinedEncClientToServerLen = combinedEncClientToServer.remaining();
            result = server.unwrap(combinedEncClientToServer, plainServerOut);
            Assert.assertEquals(0, combinedEncClientToServer.remaining());
            Assert.assertEquals(combinedEncClientToServerLen, result.bytesConsumed());
            Assert.assertEquals(plainClientOutLen, result.bytesProduced());
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testInputTooBigAndFillsUpBuffersJDKCompatabilityModeOff() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newHandler(DEFAULT).engine());
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newHandler(DEFAULT).engine());
        try {
            ByteBuffer plainClient = allocateBuffer(((ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH) + 100));
            ByteBuffer plainClient2 = allocateBuffer(512);
            ByteBuffer plainClientTotal = allocateBuffer(((plainClient.capacity()) + (plainClient2.capacity())));
            plainClientTotal.put(plainClient);
            plainClientTotal.put(plainClient2);
            plainClient.clear();
            plainClient2.clear();
            plainClientTotal.flip();
            // The capacity is designed to trigger an overflow condition.
            ByteBuffer encClientToServerTooSmall = allocateBuffer(((ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH) + 28));
            ByteBuffer encClientToServer = allocateBuffer(client.getSession().getApplicationBufferSize());
            ByteBuffer encClientToServerTotal = allocateBuffer(((client.getSession().getApplicationBufferSize()) << 1));
            ByteBuffer plainServer = allocateBuffer(((server.getSession().getApplicationBufferSize()) << 1));
            handshake(client, server);
            int plainClientRemaining = plainClient.remaining();
            int encClientToServerTooSmallRemaining = encClientToServerTooSmall.remaining();
            SSLEngineResult result = client.wrap(plainClient, encClientToServerTooSmall);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals((plainClientRemaining - (plainClient.remaining())), result.bytesConsumed());
            Assert.assertEquals((encClientToServerTooSmallRemaining - (encClientToServerTooSmall.remaining())), result.bytesProduced());
            result = client.wrap(plainClient, encClientToServerTooSmall);
            Assert.assertEquals(BUFFER_OVERFLOW, result.getStatus());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            plainClientRemaining = plainClient.remaining();
            int encClientToServerRemaining = encClientToServer.remaining();
            result = client.wrap(plainClient, encClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(plainClientRemaining, result.bytesConsumed());
            Assert.assertEquals((encClientToServerRemaining - (encClientToServer.remaining())), result.bytesProduced());
            Assert.assertEquals(0, plainClient.remaining());
            final int plainClient2Remaining = plainClient2.remaining();
            encClientToServerRemaining = encClientToServer.remaining();
            result = client.wrap(plainClient2, encClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(plainClient2Remaining, result.bytesConsumed());
            Assert.assertEquals((encClientToServerRemaining - (encClientToServer.remaining())), result.bytesProduced());
            // Concatenate the too small buffer
            encClientToServerTooSmall.flip();
            encClientToServer.flip();
            encClientToServerTotal.put(encClientToServerTooSmall);
            encClientToServerTotal.put(encClientToServer);
            encClientToServerTotal.flip();
            // Unwrap in a single call.
            final int encClientToServerTotalRemaining = encClientToServerTotal.remaining();
            result = server.unwrap(encClientToServerTotal, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(encClientToServerTotalRemaining, result.bytesConsumed());
            plainServer.flip();
            Assert.assertEquals(plainClientTotal, plainServer);
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testPartialPacketUnwrapJDKCompatabilityModeOff() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newHandler(DEFAULT).engine());
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newHandler(DEFAULT).engine());
        try {
            ByteBuffer plainClient = allocateBuffer(1024);
            ByteBuffer plainClient2 = allocateBuffer(512);
            ByteBuffer plainClientTotal = allocateBuffer(((plainClient.capacity()) + (plainClient2.capacity())));
            plainClientTotal.put(plainClient);
            plainClientTotal.put(plainClient2);
            plainClient.clear();
            plainClient2.clear();
            plainClientTotal.flip();
            ByteBuffer encClientToServer = allocateBuffer(client.getSession().getPacketBufferSize());
            ByteBuffer plainServer = allocateBuffer(server.getSession().getApplicationBufferSize());
            handshake(client, server);
            SSLEngineResult result = client.wrap(plainClient, encClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), plainClient.capacity());
            final int encClientLen = result.bytesProduced();
            result = client.wrap(plainClient2, encClientToServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), plainClient2.capacity());
            final int encClientLen2 = result.bytesProduced();
            // Flip so we can read it.
            encClientToServer.flip();
            // Consume a partial TLS packet.
            ByteBuffer encClientFirstHalf = encClientToServer.duplicate();
            encClientFirstHalf.limit((encClientLen / 2));
            result = server.unwrap(encClientFirstHalf, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), (encClientLen / 2));
            encClientToServer.position(result.bytesConsumed());
            // We now have half of the first packet and the whole second packet, so lets decode all but the last byte.
            ByteBuffer encClientAllButLastByte = encClientToServer.duplicate();
            final int encClientAllButLastByteLen = (encClientAllButLastByte.remaining()) - 1;
            encClientAllButLastByte.limit(((encClientAllButLastByte.limit()) - 1));
            result = server.unwrap(encClientAllButLastByte, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), encClientAllButLastByteLen);
            encClientToServer.position(((encClientToServer.position()) + (result.bytesConsumed())));
            // Read the last byte and verify the original content has been decrypted.
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(result.bytesConsumed(), 1);
            plainServer.flip();
            Assert.assertEquals(plainClientTotal, plainServer);
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testBufferUnderFlowAvoidedIfJDKCompatabilityModeOff() throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        clientSslCtx = SslContextBuilder.forClient().trustManager(cert.cert()).sslProvider(sslClientProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine client = wrapEngine(clientSslCtx.newHandler(DEFAULT).engine());
        serverSslCtx = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine server = wrapEngine(serverSslCtx.newHandler(DEFAULT).engine());
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
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(((SSL_RECORD_HEADER_LENGTH) - 1), result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            remaining -= result.bytesConsumed();
            // We limit the buffer so we can read the header but not the rest, this should result in an
            // BUFFER_UNDERFLOW.
            encClientToServer.limit(SSL_RECORD_HEADER_LENGTH);
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(1, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            remaining -= result.bytesConsumed();
            // We limit the buffer so we can read the header and partly the rest, this should result in an
            // BUFFER_UNDERFLOW.
            encClientToServer.limit(((((SSL_RECORD_HEADER_LENGTH) + remaining) - 1) - (SSL_RECORD_HEADER_LENGTH)));
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(((encClientToServer.limit()) - (SSL_RECORD_HEADER_LENGTH)), result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            remaining -= result.bytesConsumed();
            // Reset limit so we can read the full record.
            encClientToServer.limit(remaining);
            Assert.assertEquals(0, encClientToServer.remaining());
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(BUFFER_UNDERFLOW, result.getStatus());
            Assert.assertEquals(0, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
            encClientToServer.position(0);
            result = server.unwrap(encClientToServer, plainServer);
            Assert.assertEquals(OK, result.getStatus());
            Assert.assertEquals(remaining, result.bytesConsumed());
            Assert.assertEquals(0, result.bytesProduced());
        } finally {
            cert.delete();
            cleanupClientSslEngine(client);
            cleanupServerSslEngine(server);
        }
    }

    @Test
    public void testSNIMatchersDoesNotThrow() throws Exception {
        Assume.assumeTrue(((PlatformDependent.javaVersion()) >= 8));
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine engine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            SSLParameters parameters = new SSLParameters();
            Java8SslTestUtils.setSNIMatcher(parameters, EMPTY_BYTES);
            engine.setSSLParameters(parameters);
        } finally {
            cleanupServerSslEngine(engine);
            ssc.delete();
        }
    }

    @Test
    public void testSNIMatchersWithSNINameWithUnderscore() throws Exception {
        Assume.assumeTrue(((PlatformDependent.javaVersion()) >= 8));
        byte[] name = "rb8hx3pww30y3tvw0mwy.v1_1".getBytes(UTF_8);
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine engine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            SSLParameters parameters = new SSLParameters();
            Java8SslTestUtils.setSNIMatcher(parameters, name);
            engine.setSSLParameters(parameters);
            Assert.assertTrue(unwrapEngine(engine).checkSniHostnameMatch(name));
            Assert.assertFalse(unwrapEngine(engine).checkSniHostnameMatch("other".getBytes(UTF_8)));
        } finally {
            cleanupServerSslEngine(engine);
            ssc.delete();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlgorithmConstraintsThrows() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        serverSslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(sslServerProvider()).protocols(protocols()).ciphers(ciphers()).build();
        SSLEngine engine = wrapEngine(serverSslCtx.newEngine(DEFAULT));
        try {
            SSLParameters parameters = new SSLParameters();
            parameters.setAlgorithmConstraints(new AlgorithmConstraints() {
                @Override
                public boolean permits(Set<CryptoPrimitive> primitives, String algorithm, AlgorithmParameters parameters) {
                    return false;
                }

                @Override
                public boolean permits(Set<CryptoPrimitive> primitives, Key key) {
                    return false;
                }

                @Override
                public boolean permits(Set<CryptoPrimitive> primitives, String algorithm, Key key, AlgorithmParameters parameters) {
                    return false;
                }
            });
            engine.setSSLParameters(parameters);
        } finally {
            cleanupServerSslEngine(engine);
            ssc.delete();
        }
    }
}

