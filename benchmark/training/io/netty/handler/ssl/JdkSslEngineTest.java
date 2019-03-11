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


import EmptyArrays.EMPTY_STRINGS;
import Protocol.ALPN;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator.ProtocolSelector;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator.ProtocolSelectorFactory;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import java.security.Provider;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static IdentityCipherSuiteFilter.INSTANCE;
import static JdkBaseApplicationProtocolNegotiator.FAIL_SELECTION_LISTENER_FACTORY;
import static SslUtils.PROTOCOL_TLS_V1_2;


@RunWith(Parameterized.class)
public class JdkSslEngineTest extends SSLEngineTest {
    public enum ProviderType {

        NPN_JETTY() {
            @Override
            boolean isAvailable() {
                return JettyNpnSslEngine.isAvailable();
            }

            @Override
            Protocol protocol() {
                return Protocol.NPN;
            }

            @Override
            Provider provider() {
                return null;
            }
        },
        ALPN_JETTY() {
            @Override
            boolean isAvailable() {
                return JettyAlpnSslEngine.isAvailable();
            }

            @Override
            Protocol protocol() {
                return Protocol.ALPN;
            }

            @Override
            Provider provider() {
                // Use the default provider.
                return null;
            }
        },
        ALPN_JAVA9() {
            @Override
            boolean isAvailable() {
                return ((PlatformDependent.javaVersion()) >= 9) && (Java9SslUtils.supportsAlpn());
            }

            @Override
            Protocol protocol() {
                return Protocol.ALPN;
            }

            @Override
            Provider provider() {
                // Use the default provider.
                return null;
            }
        },
        ALPN_CONSCRYPT() {
            private Provider provider;

            @Override
            boolean isAvailable() {
                return Conscrypt.isAvailable();
            }

            @Override
            Protocol protocol() {
                return Protocol.ALPN;
            }

            @Override
            Provider provider() {
                try {
                    if ((provider) == null) {
                        provider = ((Provider) (Class.forName("org.conscrypt.OpenSSLProvider").getConstructor().newInstance()));
                    }
                    return provider;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        abstract boolean isAvailable();

        abstract Protocol protocol();

        abstract Provider provider();

        final void activate(JdkSslEngineTest instance) {
            // Typical code will not have to check this, but will get a initialization error on class load.
            // Check in this test just in case we have multiple tests that just the class and we already ignored the
            // initialization error.
            if (!(isAvailable())) {
                throw JdkSslEngineTest.tlsExtensionNotFound(protocol());
            }
            instance.provider = provider();
        }
    }

    private static final String PREFERRED_APPLICATION_LEVEL_PROTOCOL = "my-protocol-http2";

    private static final String FALLBACK_APPLICATION_LEVEL_PROTOCOL = "my-protocol-http1_1";

    private static final String APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE = "my-protocol-FOO";

    private final JdkSslEngineTest.ProviderType providerType;

    private Provider provider;

    public JdkSslEngineTest(JdkSslEngineTest.ProviderType providerType, SSLEngineTest.BufferType bufferType, SSLEngineTest.ProtocolCipherCombo protocolCipherCombo, boolean delegate) {
        super(bufferType, protocolCipherCombo, delegate);
        this.providerType = providerType;
    }

    @Test
    public void testTlsExtension() throws Exception {
        try {
            providerType.activate(this);
            ApplicationProtocolConfig apn = JdkSslEngineTest.failingNegotiator(providerType.protocol(), JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
            setupHandlers(apn);
            runTest();
        } catch (JdkSslEngineTest.SkipTestException e) {
            // ALPN availability is dependent on the java version. If ALPN is not available because of
            // java version incompatibility don't fail the test, but instead just skip the test
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testTlsExtensionNoCompatibleProtocolsNoHandshakeFailure() throws Exception {
        try {
            providerType.activate(this);
            ApplicationProtocolConfig clientApn = JdkSslEngineTest.acceptingNegotiator(providerType.protocol(), JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
            ApplicationProtocolConfig serverApn = JdkSslEngineTest.acceptingNegotiator(providerType.protocol(), JdkSslEngineTest.APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE);
            setupHandlers(serverApn, clientApn);
            runTest(null);
        } catch (JdkSslEngineTest.SkipTestException e) {
            // ALPN availability is dependent on the java version. If ALPN is not available because of
            // java version incompatibility don't fail the test, but instead just skip the test
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testTlsExtensionNoCompatibleProtocolsClientHandshakeFailure() throws Exception {
        try {
            providerType.activate(this);
            if ((providerType) == (JdkSslEngineTest.ProviderType.NPN_JETTY)) {
                ApplicationProtocolConfig clientApn = JdkSslEngineTest.failingNegotiator(providerType.protocol(), JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
                ApplicationProtocolConfig serverApn = JdkSslEngineTest.acceptingNegotiator(providerType.protocol(), JdkSslEngineTest.APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE);
                setupHandlers(serverApn, clientApn);
                Assert.assertTrue(clientLatch.await(2, TimeUnit.SECONDS));
                Assert.assertTrue(((clientException) instanceof SSLHandshakeException));
            } else {
                // ALPN
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                JdkApplicationProtocolNegotiator clientApn = new JdkAlpnApplicationProtocolNegotiator(true, true, JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
                JdkApplicationProtocolNegotiator serverApn = new JdkAlpnApplicationProtocolNegotiator(new ProtocolSelectorFactory() {
                    @Override
                    public ProtocolSelector newSelector(SSLEngine engine, Set<String> supportedProtocols) {
                        return new ProtocolSelector() {
                            @Override
                            public void unsupported() {
                            }

                            @Override
                            public String select(List<String> protocols) {
                                return JdkSslEngineTest.APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE;
                            }
                        };
                    }
                }, FAIL_SELECTION_LISTENER_FACTORY, JdkSslEngineTest.APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE);
                SslContext serverSslCtx = new JdkSslServerContext(providerType.provider(), ssc.certificate(), ssc.privateKey(), null, null, INSTANCE, serverApn, 0, 0);
                SslContext clientSslCtx = new JdkSslClientContext(providerType.provider(), null, InsecureTrustManagerFactory.INSTANCE, null, INSTANCE, clientApn, 0, 0);
                setupHandlers(new JdkSslEngineTest.TestDelegatingSslContext(serverSslCtx), new JdkSslEngineTest.TestDelegatingSslContext(clientSslCtx));
                Assert.assertTrue(clientLatch.await(2, TimeUnit.SECONDS));
                // When using TLSv1.3 the handshake is NOT sent in an extra round trip which means there will be
                // no exception reported in this case but just the channel will be closed.
                Assert.assertTrue((((clientException) instanceof SSLHandshakeException) || ((clientException) == null)));
            }
        } catch (JdkSslEngineTest.SkipTestException e) {
            // ALPN availability is dependent on the java version. If ALPN is not available because of
            // java version incompatibility don't fail the test, but instead just skip the test
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testTlsExtensionNoCompatibleProtocolsServerHandshakeFailure() throws Exception {
        try {
            providerType.activate(this);
            ApplicationProtocolConfig clientApn = JdkSslEngineTest.acceptingNegotiator(providerType.protocol(), JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
            ApplicationProtocolConfig serverApn = JdkSslEngineTest.failingNegotiator(providerType.protocol(), JdkSslEngineTest.APPLICATION_LEVEL_PROTOCOL_NOT_COMPATIBLE);
            setupHandlers(serverApn, clientApn);
            Assert.assertTrue(serverLatch.await(2, TimeUnit.SECONDS));
            Assert.assertTrue(((serverException) instanceof SSLHandshakeException));
        } catch (JdkSslEngineTest.SkipTestException e) {
            // ALPN availability is dependent on the java version. If ALPN is not available because of
            // java version incompatibility don't fail the test, but instead just skip the test
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testAlpnCompatibleProtocolsDifferentClientOrder() throws Exception {
        try {
            providerType.activate(this);
            if ((providerType) == (JdkSslEngineTest.ProviderType.NPN_JETTY)) {
                // This test only applies to ALPN.
                throw JdkSslEngineTest.tlsExtensionNotFound(providerType.protocol());
            }
            // Even the preferred application protocol appears second in the client's list, it will be picked
            // because it's the first one on server's list.
            ApplicationProtocolConfig clientApn = JdkSslEngineTest.acceptingNegotiator(ALPN, JdkSslEngineTest.FALLBACK_APPLICATION_LEVEL_PROTOCOL, JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
            ApplicationProtocolConfig serverApn = JdkSslEngineTest.failingNegotiator(ALPN, JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL, JdkSslEngineTest.FALLBACK_APPLICATION_LEVEL_PROTOCOL);
            setupHandlers(serverApn, clientApn);
            Assert.assertNull(serverException);
            runTest(JdkSslEngineTest.PREFERRED_APPLICATION_LEVEL_PROTOCOL);
        } catch (JdkSslEngineTest.SkipTestException e) {
            // ALPN availability is dependent on the java version. If ALPN is not available because of
            // java version incompatibility don't fail the test, but instead just skip the test
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testEnablingAnAlreadyDisabledSslProtocol() throws Exception {
        testEnablingAnAlreadyDisabledSslProtocol(new String[]{  }, new String[]{ PROTOCOL_TLS_V1_2 });
    }

    private static final class SkipTestException extends RuntimeException {
        private static final long serialVersionUID = 9214869217774035223L;

        SkipTestException(String message) {
            super(message);
        }
    }

    private final class TestDelegatingSslContext extends DelegatingSslContext {
        TestDelegatingSslContext(SslContext ctx) {
            super(ctx);
        }

        @Override
        protected void initEngine(SSLEngine engine) {
            engine.setEnabledProtocols(protocols());
            engine.setEnabledCipherSuites(ciphers().toArray(EMPTY_STRINGS));
        }
    }
}

