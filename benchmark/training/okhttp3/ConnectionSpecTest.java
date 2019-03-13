/**
 * Copyright (C) 2015 Square, Inc.
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
package okhttp3;


import CipherSuite.TLS_RSA_WITH_RC4_128_MD5;
import CipherSuite.TLS_RSA_WITH_RC4_128_SHA;
import ConnectionSpec.CLEARTEXT;
import ConnectionSpec.COMPATIBLE_TLS;
import ConnectionSpec.MODERN_TLS;
import TlsVersion.SSL_3_0;
import TlsVersion.TLS_1_1;
import TlsVersion.TLS_1_2;
import TlsVersion.TLS_1_2.javaName;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.junit.Assert;
import org.junit.Test;

import static ConnectionSpec.MODERN_TLS;


public final class ConnectionSpecTest {
    @Test
    public void noTlsVersions() throws Exception {
        try {
            new ConnectionSpec.Builder(MODERN_TLS).tlsVersions(new TlsVersion[0]).build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("At least one TLS version is required", expected.getMessage());
        }
    }

    @Test
    public void noCipherSuites() throws Exception {
        try {
            new ConnectionSpec.Builder(MODERN_TLS).cipherSuites(new CipherSuite[0]).build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("At least one cipher suite is required", expected.getMessage());
        }
    }

    @Test
    public void cleartextBuilder() throws Exception {
        ConnectionSpec cleartextSpec = new ConnectionSpec.Builder(false).build();
        Assert.assertFalse(cleartextSpec.isTls());
    }

    @Test
    public void tlsBuilder_explicitCiphers() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).cipherSuites(TLS_RSA_WITH_RC4_128_MD5).tlsVersions(TLS_1_2).supportsTlsExtensions(true).build();
        Assert.assertEquals(Arrays.asList(TLS_RSA_WITH_RC4_128_MD5), tlsSpec.cipherSuites());
        Assert.assertEquals(Arrays.asList(TLS_1_2), tlsSpec.tlsVersions());
        Assert.assertTrue(tlsSpec.supportsTlsExtensions());
    }

    @Test
    public void tlsBuilder_defaultCiphers() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).tlsVersions(TLS_1_2).supportsTlsExtensions(true).build();
        Assert.assertNull(tlsSpec.cipherSuites());
        Assert.assertEquals(Arrays.asList(TLS_1_2), tlsSpec.tlsVersions());
        Assert.assertTrue(tlsSpec.supportsTlsExtensions());
    }

    @Test
    public void tls_defaultCiphers_noFallbackIndicator() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).tlsVersions(TLS_1_2).supportsTlsExtensions(false).build();
        SSLSocket socket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_MD5.javaName, TLS_RSA_WITH_RC4_128_SHA.javaName });
        socket.setEnabledProtocols(new String[]{ TLS_1_2.javaName, TLS_1_1.javaName });
        Assert.assertTrue(tlsSpec.isCompatible(socket));
        /* isFallback */
        tlsSpec.apply(socket, false);
        Assert.assertEquals(ConnectionSpecTest.set(javaName), ConnectionSpecTest.set(socket.getEnabledProtocols()));
        Set<String> expectedCipherSet = ConnectionSpecTest.set(CipherSuite.TLS_RSA_WITH_RC4_128_MD5.javaName, CipherSuite.TLS_RSA_WITH_RC4_128_SHA.javaName);
        Assert.assertEquals(expectedCipherSet, expectedCipherSet);
    }

    @Test
    public void tls_defaultCiphers_withFallbackIndicator() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).tlsVersions(TLS_1_2).supportsTlsExtensions(false).build();
        SSLSocket socket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_MD5.javaName, TLS_RSA_WITH_RC4_128_SHA.javaName });
        socket.setEnabledProtocols(new String[]{ TLS_1_2.javaName, TLS_1_1.javaName });
        Assert.assertTrue(tlsSpec.isCompatible(socket));
        /* isFallback */
        tlsSpec.apply(socket, true);
        Assert.assertEquals(ConnectionSpecTest.set(javaName), ConnectionSpecTest.set(socket.getEnabledProtocols()));
        Set<String> expectedCipherSet = ConnectionSpecTest.set(CipherSuite.TLS_RSA_WITH_RC4_128_MD5.javaName, CipherSuite.TLS_RSA_WITH_RC4_128_SHA.javaName);
        if (Arrays.asList(socket.getSupportedCipherSuites()).contains("TLS_FALLBACK_SCSV")) {
            expectedCipherSet.add("TLS_FALLBACK_SCSV");
        }
        Assert.assertEquals(expectedCipherSet, expectedCipherSet);
    }

    @Test
    public void tls_explicitCiphers() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).cipherSuites(TLS_RSA_WITH_RC4_128_MD5).tlsVersions(TLS_1_2).supportsTlsExtensions(false).build();
        SSLSocket socket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_MD5.javaName, TLS_RSA_WITH_RC4_128_SHA.javaName });
        socket.setEnabledProtocols(new String[]{ TLS_1_2.javaName, TLS_1_1.javaName });
        Assert.assertTrue(tlsSpec.isCompatible(socket));
        /* isFallback */
        tlsSpec.apply(socket, true);
        Assert.assertEquals(ConnectionSpecTest.set(javaName), ConnectionSpecTest.set(socket.getEnabledProtocols()));
        Set<String> expectedCipherSet = ConnectionSpecTest.set(CipherSuite.TLS_RSA_WITH_RC4_128_MD5.javaName);
        if (Arrays.asList(socket.getSupportedCipherSuites()).contains("TLS_FALLBACK_SCSV")) {
            expectedCipherSet.add("TLS_FALLBACK_SCSV");
        }
        Assert.assertEquals(expectedCipherSet, expectedCipherSet);
    }

    @Test
    public void tls_stringCiphersAndVersions() throws Exception {
        // Supporting arbitrary input strings allows users to enable suites and versions that are not
        // yet known to the library, but are supported by the platform.
        ConnectionSpec tlsSpec = cipherSuites("MAGIC-CIPHER").tlsVersions("TLS9k").build();
    }

    @Test
    public void tls_missingRequiredCipher() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).cipherSuites(TLS_RSA_WITH_RC4_128_MD5).tlsVersions(TLS_1_2).supportsTlsExtensions(false).build();
        SSLSocket socket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        socket.setEnabledProtocols(new String[]{ TLS_1_2.javaName, TLS_1_1.javaName });
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_SHA.javaName, TLS_RSA_WITH_RC4_128_MD5.javaName });
        Assert.assertTrue(tlsSpec.isCompatible(socket));
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_SHA.javaName });
        Assert.assertFalse(tlsSpec.isCompatible(socket));
    }

    @Test
    public void allEnabledCipherSuites() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(MODERN_TLS).allEnabledCipherSuites().build();
        Assert.assertNull(tlsSpec.cipherSuites());
        SSLSocket sslSocket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        sslSocket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_SHA.javaName, TLS_RSA_WITH_RC4_128_MD5.javaName });
        tlsSpec.apply(sslSocket, false);
        Assert.assertEquals(Arrays.asList(CipherSuite.TLS_RSA_WITH_RC4_128_SHA.javaName, CipherSuite.TLS_RSA_WITH_RC4_128_MD5.javaName), Arrays.asList(sslSocket.getEnabledCipherSuites()));
    }

    @Test
    public void allEnabledTlsVersions() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(MODERN_TLS).allEnabledTlsVersions().build();
        Assert.assertNull(tlsSpec.tlsVersions());
        SSLSocket sslSocket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        sslSocket.setEnabledProtocols(new String[]{ SSL_3_0.javaName(), TLS_1_1.javaName() });
        tlsSpec.apply(sslSocket, false);
        Assert.assertEquals(Arrays.asList(SSL_3_0.javaName(), TLS_1_1.javaName()), Arrays.asList(sslSocket.getEnabledProtocols()));
    }

    @Test
    public void tls_missingTlsVersion() throws Exception {
        ConnectionSpec tlsSpec = new ConnectionSpec.Builder(true).cipherSuites(TLS_RSA_WITH_RC4_128_MD5).tlsVersions(TLS_1_2).supportsTlsExtensions(false).build();
        SSLSocket socket = ((SSLSocket) (SSLSocketFactory.getDefault().createSocket()));
        socket.setEnabledCipherSuites(new String[]{ TLS_RSA_WITH_RC4_128_MD5.javaName });
        socket.setEnabledProtocols(new String[]{ TLS_1_2.javaName, TLS_1_1.javaName });
        Assert.assertTrue(tlsSpec.isCompatible(socket));
        socket.setEnabledProtocols(new String[]{ TLS_1_1.javaName });
        Assert.assertFalse(tlsSpec.isCompatible(socket));
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        ConnectionSpec allCipherSuites = new ConnectionSpec.Builder(MODERN_TLS).allEnabledCipherSuites().build();
        ConnectionSpec allTlsVersions = new ConnectionSpec.Builder(MODERN_TLS).allEnabledTlsVersions().build();
        Set<Object> set = new CopyOnWriteArraySet<>();
        Assert.assertTrue(set.add(MODERN_TLS));
        Assert.assertTrue(set.add(COMPATIBLE_TLS));
        Assert.assertTrue(set.add(CLEARTEXT));
        Assert.assertTrue(set.add(allTlsVersions));
        Assert.assertTrue(set.add(allCipherSuites));
        Assert.assertTrue(set.remove(MODERN_TLS));
        Assert.assertTrue(set.remove(COMPATIBLE_TLS));
        Assert.assertTrue(set.remove(CLEARTEXT));
        Assert.assertTrue(set.remove(allTlsVersions));
        Assert.assertTrue(set.remove(allCipherSuites));
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void allEnabledToString() throws Exception {
        ConnectionSpec connectionSpec = new ConnectionSpec.Builder(MODERN_TLS).allEnabledTlsVersions().allEnabledCipherSuites().build();
        Assert.assertEquals(("ConnectionSpec(cipherSuites=[all enabled], tlsVersions=[all enabled], " + "supportsTlsExtensions=true)"), connectionSpec.toString());
    }

    @Test
    public void simpleToString() throws Exception {
        ConnectionSpec connectionSpec = new ConnectionSpec.Builder(MODERN_TLS).tlsVersions(TLS_1_2).cipherSuites(TLS_RSA_WITH_RC4_128_MD5).build();
        Assert.assertEquals(("ConnectionSpec(cipherSuites=[SSL_RSA_WITH_RC4_128_MD5], tlsVersions=[TLS_1_2], " + "supportsTlsExtensions=true)"), connectionSpec.toString());
    }
}

