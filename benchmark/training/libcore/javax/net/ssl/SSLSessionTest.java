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
import StandardNames.CIPHER_SUITE_INVALID;
import StandardNames.SSL_SOCKET_PROTOCOLS;
import java.util.Arrays;
import javax.net.ssl.SSLPeerUnverifiedException;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;


public class SSLSessionTest extends TestCase {
    public void test_SSLSocket_TestSSLSessions_create() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNotNull(s.invalid);
        TestCase.assertFalse(s.invalid.isValid());
        TestCase.assertTrue(s.server.isValid());
        TestCase.assertTrue(s.client.isValid());
        s.close();
    }

    public void test_SSLSession_getApplicationBufferSize() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertTrue(((s.invalid.getApplicationBufferSize()) > 0));
        TestCase.assertTrue(((s.server.getApplicationBufferSize()) > 0));
        TestCase.assertTrue(((s.client.getApplicationBufferSize()) > 0));
        s.close();
    }

    public void test_SSLSession_getCipherSuite() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNotNull(s.invalid.getCipherSuite());
        TestCase.assertEquals(CIPHER_SUITE_INVALID, s.invalid.getCipherSuite());
        TestCase.assertNotNull(s.server.getCipherSuite());
        TestCase.assertNotNull(s.client.getCipherSuite());
        TestCase.assertEquals(s.server.getCipherSuite(), s.client.getCipherSuite());
        TestCase.assertTrue(CIPHER_SUITES.contains(s.server.getCipherSuite()));
        s.close();
    }

    public void test_SSLSession_getCreationTime() {
        // We use OpenSSL, which only returns times accurate to the nearest second.
        // NativeCrypto just multiplies by 1000, which looks like truncation, which
        // would make it appear as if the OpenSSL side of things was created before
        // we called it.
        long t0 = (System.currentTimeMillis()) / 1000;
        TestSSLSessions s = TestSSLSessions.create();
        long t1 = (System.currentTimeMillis()) / 1000;
        TestCase.assertTrue(((s.invalid.getCreationTime()) > 0));
        long sTime = (s.server.getCreationTime()) / 1000;
        TestCase.assertTrue(((sTime + " >= ") + t0), (sTime >= t0));
        TestCase.assertTrue(((sTime + " <= ") + t1), (sTime <= t1));
        long cTime = (s.client.getCreationTime()) / 1000;
        TestCase.assertTrue(((cTime + " >= ") + t0), (cTime >= t0));
        TestCase.assertTrue(((cTime + " <= ") + t1), (cTime <= t1));
        s.close();
    }

    public void test_SSLSession_getId() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNotNull(s.invalid.getId());
        TestCase.assertNotNull(s.server.getId());
        TestCase.assertNotNull(s.client.getId());
        TestCase.assertEquals(0, s.invalid.getId().length);
        if (TestSSLContext.sslServerSocketSupportsSessionTickets()) {
            TestCase.assertEquals(0, s.server.getId().length);
        } else {
            TestCase.assertEquals(32, s.server.getId().length);
            TestCase.assertTrue(Arrays.equals(s.server.getId(), s.client.getId()));
        }
        TestCase.assertEquals(32, s.client.getId().length);
        s.close();
    }

    public void test_SSLSession_getLastAccessedTime() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertTrue(((s.invalid.getLastAccessedTime()) > 0));
        TestCase.assertTrue(((s.server.getLastAccessedTime()) > 0));
        TestCase.assertTrue(((s.client.getLastAccessedTime()) > 0));
        TestCase.assertTrue((((("s.server.getLastAccessedTime()=" + (s.server.getLastAccessedTime())) + " ") + "s.client.getLastAccessedTime()=") + (s.client.getLastAccessedTime())), ((Math.abs(((s.server.getLastAccessedTime()) - (s.client.getLastAccessedTime())))) < (1 * 1000)));
        TestCase.assertTrue(((s.server.getLastAccessedTime()) >= (s.server.getCreationTime())));
        TestCase.assertTrue(((s.client.getLastAccessedTime()) >= (s.client.getCreationTime())));
        s.close();
    }

    public void test_SSLSession_getLocalCertificates() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNull(s.invalid.getLocalCertificates());
        TestCase.assertNull(s.client.getLocalCertificates());
        TestCase.assertNotNull(s.server.getLocalCertificates());
        TestKeyStore.assertChainLength(s.server.getLocalCertificates());
        TestSSLContext.assertServerCertificateChain(s.s.c.serverTrustManager, s.server.getLocalCertificates());
        TestSSLContext.assertCertificateInKeyStore(s.server.getLocalCertificates()[0], s.s.c.serverKeyStore);
        s.close();
    }

    public void test_SSLSession_getLocalPrincipal() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNull(s.invalid.getLocalPrincipal());
        TestCase.assertNull(s.client.getLocalPrincipal());
        TestCase.assertNotNull(s.server.getLocalPrincipal());
        TestCase.assertNotNull(s.server.getLocalPrincipal().getName());
        TestSSLContext.assertCertificateInKeyStore(s.server.getLocalPrincipal(), s.s.c.serverKeyStore);
        s.close();
    }

    public void test_SSLSession_getPacketBufferSize() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertTrue(((s.invalid.getPacketBufferSize()) > 0));
        TestCase.assertTrue(((s.server.getPacketBufferSize()) > 0));
        TestCase.assertTrue(((s.client.getPacketBufferSize()) > 0));
        s.close();
    }

    public void test_SSLSession_getPeerCertificateChain() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerCertificateChain();
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        TestCase.assertNotNull(s.client.getPeerCertificates());
        TestKeyStore.assertChainLength(s.client.getPeerCertificateChain());
        try {
            TestCase.assertNull(s.server.getPeerCertificateChain());
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        s.close();
    }

    public void test_SSLSession_getPeerCertificates() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerCertificates();
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        TestCase.assertNotNull(s.client.getPeerCertificates());
        TestKeyStore.assertChainLength(s.client.getPeerCertificates());
        TestSSLContext.assertServerCertificateChain(s.s.c.serverTrustManager, s.client.getPeerCertificates());
        TestSSLContext.assertCertificateInKeyStore(s.client.getPeerCertificates()[0], s.s.c.serverKeyStore);
        try {
            s.server.getPeerCertificates();
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        s.close();
    }

    public void test_SSLSession_getPeerHost() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNull(s.invalid.getPeerHost());
        TestCase.assertNotNull(s.server.getPeerHost());
        TestCase.assertNotNull(s.client.getPeerHost());
        s.close();
    }

    public void test_SSLSession_getPeerPort() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertEquals((-1), s.invalid.getPeerPort());
        TestCase.assertTrue(((s.server.getPeerPort()) > 0));
        TestCase.assertEquals(s.s.c.port, s.client.getPeerPort());
        s.close();
    }

    public void test_SSLSession_getPeerPrincipal() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerPrincipal();
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        try {
            s.server.getPeerPrincipal();
            TestCase.fail();
        } catch (SSLPeerUnverifiedException expected) {
        }
        TestCase.assertNotNull(s.client.getPeerPrincipal());
        TestCase.assertNotNull(s.client.getPeerPrincipal().getName());
        TestSSLContext.assertCertificateInKeyStore(s.client.getPeerPrincipal(), s.s.c.serverKeyStore);
        s.close();
    }

    public void test_SSLSession_getProtocol() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNotNull(s.invalid.getProtocol());
        TestCase.assertEquals("NONE", s.invalid.getProtocol());
        TestCase.assertNotNull(s.server.getProtocol());
        TestCase.assertNotNull(s.client.getProtocol());
        TestCase.assertEquals(s.server.getProtocol(), s.client.getProtocol());
        TestCase.assertTrue(SSL_SOCKET_PROTOCOLS.contains(s.server.getProtocol()));
        s.close();
    }

    public void test_SSLSession_getSessionContext() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNull(s.invalid.getSessionContext());
        TestCase.assertNotNull(s.server.getSessionContext());
        TestCase.assertNotNull(s.client.getSessionContext());
        TestCase.assertEquals(s.s.c.serverContext.getServerSessionContext(), s.server.getSessionContext());
        TestCase.assertEquals(s.s.c.clientContext.getClientSessionContext(), s.client.getSessionContext());
        TestCase.assertNotSame(s.server.getSessionContext(), s.client.getSessionContext());
        s.close();
    }

    public void test_SSLSession_getValue() {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getValue(null);
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertNull(s.invalid.getValue("BOGUS"));
        s.close();
    }

    public void test_SSLSession_getValueNames() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertNotNull(s.invalid.getValueNames());
        TestCase.assertEquals(0, s.invalid.getValueNames().length);
        s.close();
    }

    public void test_SSLSession_invalidate() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertFalse(s.invalid.isValid());
        s.invalid.invalidate();
        TestCase.assertFalse(s.invalid.isValid());
        TestCase.assertNull(s.invalid.getSessionContext());
        TestCase.assertTrue(s.server.isValid());
        s.server.invalidate();
        TestCase.assertFalse(s.server.isValid());
        TestCase.assertNull(s.server.getSessionContext());
        TestCase.assertTrue(s.client.isValid());
        s.client.invalidate();
        TestCase.assertFalse(s.client.isValid());
        TestCase.assertNull(s.client.getSessionContext());
        s.close();
    }

    public void test_SSLSession_isValid() {
        TestSSLSessions s = TestSSLSessions.create();
        TestCase.assertFalse(s.invalid.isValid());
        TestCase.assertTrue(s.server.isValid());
        TestCase.assertTrue(s.client.isValid());
        s.close();
    }

    public void test_SSLSession_putValue() {
        TestSSLSessions s = TestSSLSessions.create();
        String key = "KEY";
        String value = "VALUE";
        TestCase.assertNull(s.invalid.getValue(key));
        TestCase.assertEquals(0, s.invalid.getValueNames().length);
        s.invalid.putValue(key, value);
        TestCase.assertSame(value, s.invalid.getValue(key));
        TestCase.assertEquals(1, s.invalid.getValueNames().length);
        TestCase.assertEquals(key, s.invalid.getValueNames()[0]);
        s.close();
    }

    public void test_SSLSession_removeValue() {
        TestSSLSessions s = TestSSLSessions.create();
        String key = "KEY";
        String value = "VALUE";
        s.invalid.putValue(key, value);
        TestCase.assertEquals(1, s.invalid.getValueNames().length);
        TestCase.assertEquals(key, s.invalid.getValueNames()[0]);
        s.invalid.removeValue(key);
        TestCase.assertNull(s.invalid.getValue(key));
        TestCase.assertEquals(0, s.invalid.getValueNames().length);
        s.close();
    }
}

