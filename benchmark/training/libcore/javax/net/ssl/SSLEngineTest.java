/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.javax.net.ssl;


import StandardNames.CIPHER_SUITES;
import StandardNames.CIPHER_SUITES_SSLENGINE;
import StandardNames.SSL_SOCKET_PROTOCOLS;
import StandardNames.SSL_SOCKET_PROTOCOLS_SSLENGINE;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;
import libcore.java.security.TestKeyStore;


public class SSLEngineTest extends TestCase {
    public void test_SSLEngine_getSupportedCipherSuites_names() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        String[] cipherSuites = e.getSupportedCipherSuites();
        StandardNames.assertSupportedCipherSuites(CIPHER_SUITES_SSLENGINE, cipherSuites);
        TestCase.assertNotSame(cipherSuites, e.getSupportedCipherSuites());
        c.close();
    }

    public void test_SSLEngine_getSupportedCipherSuites_connect() throws Exception {
        // note the rare usage of non-RSA keys
        TestKeyStore testKeyStore = new TestKeyStore.Builder().keyAlgorithms("RSA", "DSA", "EC", "EC_RSA").aliasPrefix("rsa-dsa-ec").ca(true).build();
        test_SSLEngine_getSupportedCipherSuites_connect(testKeyStore, false);
        if (StandardNames.IS_RI) {
            test_SSLEngine_getSupportedCipherSuites_connect(testKeyStore, true);
        }
    }

    public void test_SSLEngine_getEnabledCipherSuites() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        String[] cipherSuites = e.getEnabledCipherSuites();
        StandardNames.assertValidCipherSuites(CIPHER_SUITES, cipherSuites);
        TestCase.assertNotSame(cipherSuites, e.getEnabledCipherSuites());
        c.close();
    }

    public void test_SSLEngine_setEnabledCipherSuites() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        try {
            e.setEnabledCipherSuites(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            e.setEnabledCipherSuites(new String[1]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            e.setEnabledCipherSuites(new String[]{ "Bogus" });
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        e.setEnabledCipherSuites(new String[0]);
        e.setEnabledCipherSuites(e.getEnabledCipherSuites());
        e.setEnabledCipherSuites(e.getSupportedCipherSuites());
        c.close();
    }

    public void test_SSLEngine_getSupportedProtocols() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        String[] protocols = e.getSupportedProtocols();
        StandardNames.assertSupportedProtocols(SSL_SOCKET_PROTOCOLS_SSLENGINE, protocols);
        TestCase.assertNotSame(protocols, e.getSupportedProtocols());
        c.close();
    }

    public void test_SSLEngine_getEnabledProtocols() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        String[] protocols = e.getEnabledProtocols();
        StandardNames.assertValidProtocols(SSL_SOCKET_PROTOCOLS, protocols);
        TestCase.assertNotSame(protocols, e.getEnabledProtocols());
        c.close();
    }

    public void test_SSLEngine_setEnabledProtocols() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        try {
            e.setEnabledProtocols(null);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            e.setEnabledProtocols(new String[1]);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            e.setEnabledProtocols(new String[]{ "Bogus" });
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        e.setEnabledProtocols(new String[0]);
        e.setEnabledProtocols(e.getEnabledProtocols());
        e.setEnabledProtocols(e.getSupportedProtocols());
        c.close();
    }

    public void test_SSLEngine_getSession() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        SSLSession session = e.getSession();
        TestCase.assertNotNull(session);
        TestCase.assertFalse(session.isValid());
        c.close();
    }

    public void test_SSLEngine_beginHandshake() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        try {
            c.clientContext.createSSLEngine().beginHandshake();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        assertConnected(TestSSLEnginePair.create(null));
        c.close();
    }

    public void test_SSLEngine_beginHandshake_noKeyStore() throws Exception {
        TestSSLContext c = TestSSLContext.create(null, null, null, null, null, null, null, null, SSLContext.getDefault(), SSLContext.getDefault());
        try {
            // TODO Fix KnownFailure AlertException "NO SERVER CERTIFICATE FOUND"
            // ServerHandshakeImpl.selectSuite should not select a suite without a required cert
            TestSSLEnginePair.connect(c, null);
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
        }
        c.close();
    }

    public void test_SSLEngine_beginHandshake_noClientCertificate() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine[] engines = TestSSLEnginePair.connect(c, null);
        assertConnected(engines[0], engines[1]);
        c.close();
    }

    public void test_SSLEngine_getUseClientMode() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        TestCase.assertFalse(c.clientContext.createSSLEngine().getUseClientMode());
        TestCase.assertFalse(c.clientContext.createSSLEngine(null, (-1)).getUseClientMode());
        c.close();
    }

    public void test_SSLEngine_setUseClientMode() throws Exception {
        // client is client, server is server
        assertConnected(test_SSLEngine_setUseClientMode(true, false));
        // client is server, server is client
        assertConnected(test_SSLEngine_setUseClientMode(false, true));
        // both are client
        assertNotConnected(test_SSLEngine_setUseClientMode(true, true));
        // both are server
        assertNotConnected(test_SSLEngine_setUseClientMode(false, false));
    }

    public void test_SSLEngine_setUseClientMode_afterHandshake() throws Exception {
        // can't set after handshake
        TestSSLEnginePair pair = TestSSLEnginePair.create(null);
        try {
            pair.server.setUseClientMode(false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            pair.client.setUseClientMode(false);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void test_SSLEngine_clientAuth() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        TestCase.assertFalse(e.getWantClientAuth());
        TestCase.assertFalse(e.getNeedClientAuth());
        // confirm turning one on by itself
        e.setWantClientAuth(true);
        TestCase.assertTrue(e.getWantClientAuth());
        TestCase.assertFalse(e.getNeedClientAuth());
        // confirm turning setting on toggles the other
        e.setNeedClientAuth(true);
        TestCase.assertFalse(e.getWantClientAuth());
        TestCase.assertTrue(e.getNeedClientAuth());
        // confirm toggling back
        e.setWantClientAuth(true);
        TestCase.assertTrue(e.getWantClientAuth());
        TestCase.assertFalse(e.getNeedClientAuth());
        // TODO Fix KnownFailure "init - invalid private key"
        TestSSLContext clientAuthContext = TestSSLContext.create(TestKeyStore.getClientCertificate(), TestKeyStore.getServer());
        TestSSLEnginePair p = TestSSLEnginePair.create(clientAuthContext, new TestSSLEnginePair.Hooks() {
            @Override
            void beforeBeginHandshake(SSLEngine client, SSLEngine server) {
                server.setWantClientAuth(true);
            }
        });
        assertConnected(p);
        TestCase.assertNotNull(p.client.getSession().getLocalCertificates());
        TestKeyStore.assertChainLength(p.client.getSession().getLocalCertificates());
        TestSSLContext.assertClientCertificateChain(clientAuthContext.clientTrustManager, p.client.getSession().getLocalCertificates());
        clientAuthContext.close();
        c.close();
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=31903
     * This test case directly tests the fix for the issue.
     */
    public void test_SSLEngine_clientAuthWantedNoClientCert() throws Exception {
        TestSSLContext clientAuthContext = TestSSLContext.create(TestKeyStore.getClient(), TestKeyStore.getServer());
        TestSSLEnginePair p = TestSSLEnginePair.create(clientAuthContext, new TestSSLEnginePair.Hooks() {
            @Override
            void beforeBeginHandshake(SSLEngine client, SSLEngine server) {
                server.setWantClientAuth(true);
            }
        });
        assertConnected(p);
        clientAuthContext.close();
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=31903
     * This test case verifies that if the server requires a client cert
     * (setNeedClientAuth) but the client does not provide one SSL connection
     * establishment will fail
     */
    public void test_SSLEngine_clientAuthNeededNoClientCert() throws Exception {
        boolean handshakeExceptionCaught = false;
        TestSSLContext clientAuthContext = TestSSLContext.create(TestKeyStore.getClient(), TestKeyStore.getServer());
        try {
            TestSSLEnginePair.create(clientAuthContext, new TestSSLEnginePair.Hooks() {
                @Override
                void beforeBeginHandshake(SSLEngine client, SSLEngine server) {
                    server.setNeedClientAuth(true);
                }
            });
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
        } finally {
            clientAuthContext.close();
        }
    }

    public void test_SSLEngine_getEnableSessionCreation() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        TestCase.assertTrue(e.getEnableSessionCreation());
        c.close();
    }

    public void test_SSLEngine_setEnableSessionCreation_server() throws Exception {
        TestSSLEnginePair p = TestSSLEnginePair.create(new TestSSLEnginePair.Hooks() {
            @Override
            void beforeBeginHandshake(SSLEngine client, SSLEngine server) {
                server.setEnableSessionCreation(false);
            }
        });
        assertNotConnected(p);
    }

    public void test_SSLEngine_setEnableSessionCreation_client() throws Exception {
        try {
            TestSSLEnginePair.create(new TestSSLEnginePair.Hooks() {
                @Override
                void beforeBeginHandshake(SSLEngine client, SSLEngine server) {
                    client.setEnableSessionCreation(false);
                }
            });
            TestCase.fail();
        } catch (SSLException expected) {
        }
    }

    public void test_SSLEngine_getSSLParameters() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        SSLParameters p = e.getSSLParameters();
        TestCase.assertNotNull(p);
        String[] cipherSuites = p.getCipherSuites();
        StandardNames.assertValidCipherSuites(CIPHER_SUITES, cipherSuites);
        TestCase.assertNotSame(cipherSuites, e.getEnabledCipherSuites());
        TestCase.assertEquals(Arrays.asList(cipherSuites), Arrays.asList(e.getEnabledCipherSuites()));
        String[] protocols = p.getProtocols();
        StandardNames.assertValidProtocols(SSL_SOCKET_PROTOCOLS, protocols);
        TestCase.assertNotSame(protocols, e.getEnabledProtocols());
        TestCase.assertEquals(Arrays.asList(protocols), Arrays.asList(e.getEnabledProtocols()));
        TestCase.assertEquals(p.getWantClientAuth(), e.getWantClientAuth());
        TestCase.assertEquals(p.getNeedClientAuth(), e.getNeedClientAuth());
        c.close();
    }

    public void test_SSLEngine_setSSLParameters() throws Exception {
        TestSSLContext c = TestSSLContext.create();
        SSLEngine e = c.clientContext.createSSLEngine();
        String[] defaultCipherSuites = e.getEnabledCipherSuites();
        String[] defaultProtocols = e.getEnabledProtocols();
        String[] supportedCipherSuites = e.getSupportedCipherSuites();
        String[] supportedProtocols = e.getSupportedProtocols();
        {
            SSLParameters p = new SSLParameters();
            e.setSSLParameters(p);
            TestCase.assertEquals(Arrays.asList(defaultCipherSuites), Arrays.asList(e.getEnabledCipherSuites()));
            TestCase.assertEquals(Arrays.asList(defaultProtocols), Arrays.asList(e.getEnabledProtocols()));
        }
        {
            SSLParameters p = new SSLParameters(supportedCipherSuites, supportedProtocols);
            e.setSSLParameters(p);
            TestCase.assertEquals(Arrays.asList(supportedCipherSuites), Arrays.asList(e.getEnabledCipherSuites()));
            TestCase.assertEquals(Arrays.asList(supportedProtocols), Arrays.asList(e.getEnabledProtocols()));
        }
        {
            SSLParameters p = new SSLParameters();
            p.setNeedClientAuth(true);
            TestCase.assertFalse(e.getNeedClientAuth());
            TestCase.assertFalse(e.getWantClientAuth());
            e.setSSLParameters(p);
            TestCase.assertTrue(e.getNeedClientAuth());
            TestCase.assertFalse(e.getWantClientAuth());
            p.setWantClientAuth(true);
            TestCase.assertTrue(e.getNeedClientAuth());
            TestCase.assertFalse(e.getWantClientAuth());
            e.setSSLParameters(p);
            TestCase.assertFalse(e.getNeedClientAuth());
            TestCase.assertTrue(e.getWantClientAuth());
            p.setWantClientAuth(false);
            TestCase.assertFalse(e.getNeedClientAuth());
            TestCase.assertTrue(e.getWantClientAuth());
            e.setSSLParameters(p);
            TestCase.assertFalse(e.getNeedClientAuth());
            TestCase.assertFalse(e.getWantClientAuth());
        }
        c.close();
    }

    public void test_TestSSLEnginePair_create() throws Exception {
        TestSSLEnginePair test = TestSSLEnginePair.create(null);
        TestCase.assertNotNull(test.c);
        TestCase.assertNotNull(test.server);
        TestCase.assertNotNull(test.client);
        assertConnected(test);
    }
}

