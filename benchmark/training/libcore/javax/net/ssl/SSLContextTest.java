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
import StandardNames.IS_RI;
import StandardNames.JSSE_PROVIDER_NAME;
import StandardNames.SSL_CONTEXT_PROTOCOLS_DEFAULT;
import StandardNames.SSL_SOCKET_PROTOCOLS;
import java.security.KeyManagementException;
import java.security.Provider;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;


public class SSLContextTest extends TestCase {
    public void test_SSLContext_getDefault() throws Exception {
        SSLContext sslContext = SSLContext.getDefault();
        TestCase.assertNotNull(sslContext);
        try {
            sslContext.init(null, null, null);
        } catch (KeyManagementException expected) {
        }
    }

    public void test_SSLContext_setDefault() throws Exception {
        try {
            SSLContext.setDefault(null);
        } catch (NullPointerException expected) {
        }
        SSLContext defaultContext = SSLContext.getDefault();
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext oldContext = SSLContext.getDefault();
            TestCase.assertNotNull(oldContext);
            SSLContext newContext = SSLContext.getInstance(protocol);
            TestCase.assertNotNull(newContext);
            TestCase.assertNotSame(oldContext, newContext);
            SSLContext.setDefault(newContext);
            TestCase.assertSame(newContext, SSLContext.getDefault());
        }
        SSLContext.setDefault(defaultContext);
    }

    public void test_SSLContext_getInstance() throws Exception {
        try {
            SSLContext.getInstance(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            TestCase.assertNotNull(SSLContext.getInstance(protocol));
            TestCase.assertNotSame(SSLContext.getInstance(protocol), SSLContext.getInstance(protocol));
        }
        try {
            SSLContext.getInstance(null, ((String) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            SSLContext.getInstance(null, "");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            try {
                SSLContext.getInstance(protocol, ((String) (null)));
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
        }
        try {
            SSLContext.getInstance(null, JSSE_PROVIDER_NAME);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_SSLContext_getProtocol() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            String protocolName = SSLContext.getInstance(protocol).getProtocol();
            TestCase.assertNotNull(protocolName);
            TestCase.assertTrue(protocol.startsWith(protocolName));
        }
    }

    public void test_SSLContext_getProvider() throws Exception {
        Provider provider = SSLContext.getDefault().getProvider();
        TestCase.assertNotNull(provider);
        TestCase.assertEquals(JSSE_PROVIDER_NAME, provider.getName());
    }

    public void test_SSLContext_init() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            if (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT)) {
                try {
                    sslContext.init(null, null, null);
                } catch (KeyManagementException expected) {
                }
            } else {
                sslContext.init(null, null, null);
            }
        }
    }

    public void test_SSLContext_getSocketFactory() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            if (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT)) {
                SSLContext.getInstance(protocol).getSocketFactory();
            } else {
                try {
                    SSLContext.getInstance(protocol).getSocketFactory();
                    TestCase.fail();
                } catch (IllegalStateException expected) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance(protocol);
            if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                sslContext.init(null, null, null);
            }
            SocketFactory sf = sslContext.getSocketFactory();
            TestCase.assertNotNull(sf);
            TestCase.assertTrue(SSLSocketFactory.class.isAssignableFrom(sf.getClass()));
        }
    }

    public void test_SSLContext_getServerSocketFactory() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            if (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT)) {
                SSLContext.getInstance(protocol).getServerSocketFactory();
            } else {
                try {
                    SSLContext.getInstance(protocol).getServerSocketFactory();
                    TestCase.fail();
                } catch (IllegalStateException expected) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance(protocol);
            if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                sslContext.init(null, null, null);
            }
            ServerSocketFactory ssf = sslContext.getServerSocketFactory();
            TestCase.assertNotNull(ssf);
            TestCase.assertTrue(SSLServerSocketFactory.class.isAssignableFrom(ssf.getClass()));
        }
    }

    public void test_SSLContext_createSSLEngine() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            if (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT)) {
                SSLContext.getInstance(protocol).createSSLEngine();
            } else {
                try {
                    SSLContext.getInstance(protocol).createSSLEngine();
                    TestCase.fail();
                } catch (IllegalStateException expected) {
                }
            }
            if (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT)) {
                SSLContext.getInstance(protocol).createSSLEngine(null, (-1));
            } else {
                try {
                    SSLContext.getInstance(protocol).createSSLEngine(null, (-1));
                    TestCase.fail();
                } catch (IllegalStateException expected) {
                }
            }
            {
                SSLContext sslContext = SSLContext.getInstance(protocol);
                if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                    sslContext.init(null, null, null);
                }
                SSLEngine se = sslContext.createSSLEngine();
                TestCase.assertNotNull(se);
            }
            {
                SSLContext sslContext = SSLContext.getInstance(protocol);
                if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                    sslContext.init(null, null, null);
                }
                SSLEngine se = sslContext.createSSLEngine(null, (-1));
                TestCase.assertNotNull(se);
            }
        }
    }

    public void test_SSLContext_getServerSessionContext() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            SSLSessionContext sessionContext = sslContext.getServerSessionContext();
            TestCase.assertNotNull(sessionContext);
            if ((!(StandardNames.IS_RI)) && (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                TestCase.assertSame(SSLContext.getInstance(protocol).getServerSessionContext(), sessionContext);
            } else {
                TestCase.assertNotSame(SSLContext.getInstance(protocol).getServerSessionContext(), sessionContext);
            }
        }
    }

    public void test_SSLContext_getClientSessionContext() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            SSLSessionContext sessionContext = sslContext.getClientSessionContext();
            TestCase.assertNotNull(sessionContext);
            if ((!(StandardNames.IS_RI)) && (protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                TestCase.assertSame(SSLContext.getInstance(protocol).getClientSessionContext(), sessionContext);
            } else {
                TestCase.assertNotSame(SSLContext.getInstance(protocol).getClientSessionContext(), sessionContext);
            }
        }
    }

    public void test_SSLContext_getDefaultSSLParameters() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                sslContext.init(null, null, null);
            }
            SSLParameters p = sslContext.getDefaultSSLParameters();
            TestCase.assertNotNull(p);
            String[] cipherSuites = p.getCipherSuites();
            TestCase.assertNotNull(cipherSuites);
            StandardNames.assertValidCipherSuites(CIPHER_SUITES, cipherSuites);
            String[] protocols = p.getProtocols();
            TestCase.assertNotNull(protocols);
            StandardNames.assertValidCipherSuites(SSL_SOCKET_PROTOCOLS, protocols);
            TestCase.assertFalse(p.getWantClientAuth());
            TestCase.assertFalse(p.getNeedClientAuth());
        }
    }

    public void test_SSLContext_getSupportedSSLParameters() throws Exception {
        for (String protocol : StandardNames.SSL_CONTEXT_PROTOCOLS) {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            if (!(protocol.equals(SSL_CONTEXT_PROTOCOLS_DEFAULT))) {
                sslContext.init(null, null, null);
            }
            SSLParameters p = sslContext.getSupportedSSLParameters();
            TestCase.assertNotNull(p);
            String[] cipherSuites = p.getCipherSuites();
            TestCase.assertNotNull(cipherSuites);
            StandardNames.assertSupportedCipherSuites(CIPHER_SUITES, cipherSuites);
            String[] protocols = p.getProtocols();
            TestCase.assertNotNull(protocols);
            StandardNames.assertSupportedProtocols(SSL_SOCKET_PROTOCOLS, protocols);
            TestCase.assertFalse(p.getWantClientAuth());
            TestCase.assertFalse(p.getNeedClientAuth());
        }
    }

    public void test_SSLContextTest_TestSSLContext_create() {
        TestSSLContext testContext = TestSSLContext.create();
        TestCase.assertNotNull(testContext);
        TestCase.assertNotNull(testContext.clientKeyStore);
        TestCase.assertNull(testContext.clientStorePassword);
        TestCase.assertNotNull(testContext.serverKeyStore);
        TestCase.assertEquals(IS_RI, ((testContext.serverStorePassword) != null));
        TestCase.assertNotNull(testContext.clientKeyManager);
        TestCase.assertNotNull(testContext.serverKeyManager);
        TestCase.assertNotNull(testContext.clientTrustManager);
        TestCase.assertNotNull(testContext.serverTrustManager);
        TestCase.assertNotNull(testContext.clientContext);
        TestCase.assertNotNull(testContext.serverContext);
        TestCase.assertNotNull(testContext.serverSocket);
        TestCase.assertNotNull(testContext.host);
        TestCase.assertTrue(((testContext.port) != 0));
        testContext.close();
    }
}

