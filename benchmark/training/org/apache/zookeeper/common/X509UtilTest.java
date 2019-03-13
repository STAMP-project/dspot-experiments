/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.common;


import KeyStoreFileType.JKS;
import KeyStoreFileType.PEM;
import X509Exception.KeyManagerException;
import X509Exception.SSLContextException;
import X509Exception.TrustManagerException;
import X509Util.DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS;
import X509Util.DEFAULT_PROTOCOL;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.zookeeper.PortAssignment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static X509Util.DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS;


@RunWith(Parameterized.class)
public class X509UtilTest extends BaseX509ParameterizedTestCase {
    private X509Util x509Util;

    private static final String[] customCipherSuites = new String[]{ "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA" };

    public X509UtilTest(X509KeyType caKeyType, X509KeyType certKeyType, String keyPassword, Integer paramIndex) {
        super(paramIndex, () -> {
            try {
                return X509TestContext.newBuilder().setTempDir(BaseX509ParameterizedTestCase.tempDir).setKeyStorePassword(keyPassword).setKeyStoreKeyType(certKeyType).setTrustStorePassword(keyPassword).setTrustStoreKeyType(caKeyType).build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test(timeout = 5000)
    public void testCreateSSLContextWithoutCustomProtocol() throws Exception {
        SSLContext sslContext = x509Util.getDefaultSSLContext();
        Assert.assertEquals(DEFAULT_PROTOCOL, sslContext.getProtocol());
    }

    @Test(timeout = 5000)
    public void testCreateSSLContextWithCustomProtocol() throws Exception {
        final String protocol = "TLSv1.1";
        System.setProperty(x509Util.getSslProtocolProperty(), protocol);
        SSLContext sslContext = x509Util.getDefaultSSLContext();
        Assert.assertEquals(protocol, sslContext.getProtocol());
    }

    @Test(timeout = 5000)
    public void testCreateSSLContextWithoutKeyStoreLocation() throws Exception {
        System.clearProperty(x509Util.getSslKeystoreLocationProperty());
        x509Util.getDefaultSSLContext();
    }

    @Test(timeout = 5000, expected = SSLContextException.class)
    public void testCreateSSLContextWithoutKeyStorePassword() throws Exception {
        if (!(x509TestContext.isKeyStoreEncrypted())) {
            throw new X509Exception.SSLContextException("");
        }
        System.clearProperty(x509Util.getSslKeystorePasswdProperty());
        x509Util.getDefaultSSLContext();
    }

    @Test(timeout = 5000)
    public void testCreateSSLContextWithCustomCipherSuites() throws Exception {
        setCustomCipherSuites();
        SSLSocket sslSocket = x509Util.createSSLSocket();
        Assert.assertArrayEquals(X509UtilTest.customCipherSuites, sslSocket.getEnabledCipherSuites());
    }

    // It would be great to test the value of PKIXBuilderParameters#setRevocationEnabled but it does not appear to be
    // possible
    @Test(timeout = 5000)
    public void testCRLEnabled() throws Exception {
        System.setProperty(x509Util.getSslCrlEnabledProperty(), "true");
        x509Util.getDefaultSSLContext();
        Assert.assertTrue(Boolean.valueOf(System.getProperty("com.sun.net.ssl.checkRevocation")));
        Assert.assertTrue(Boolean.valueOf(System.getProperty("com.sun.security.enableCRLDP")));
        Assert.assertFalse(Boolean.valueOf(Security.getProperty("ocsp.enable")));
    }

    @Test(timeout = 5000)
    public void testCRLDisabled() throws Exception {
        x509Util.getDefaultSSLContext();
        Assert.assertFalse(Boolean.valueOf(System.getProperty("com.sun.net.ssl.checkRevocation")));
        Assert.assertFalse(Boolean.valueOf(System.getProperty("com.sun.security.enableCRLDP")));
        Assert.assertFalse(Boolean.valueOf(Security.getProperty("ocsp.enable")));
    }

    @Test(timeout = 5000)
    public void testOCSPEnabled() throws Exception {
        System.setProperty(x509Util.getSslOcspEnabledProperty(), "true");
        x509Util.getDefaultSSLContext();
        Assert.assertTrue(Boolean.valueOf(System.getProperty("com.sun.net.ssl.checkRevocation")));
        Assert.assertTrue(Boolean.valueOf(System.getProperty("com.sun.security.enableCRLDP")));
        Assert.assertTrue(Boolean.valueOf(Security.getProperty("ocsp.enable")));
    }

    @Test(timeout = 5000)
    public void testCreateSSLSocket() throws Exception {
        setCustomCipherSuites();
        SSLSocket sslSocket = x509Util.createSSLSocket();
        Assert.assertArrayEquals(X509UtilTest.customCipherSuites, sslSocket.getEnabledCipherSuites());
    }

    @Test(timeout = 5000)
    public void testCreateSSLServerSocketWithoutPort() throws Exception {
        setCustomCipherSuites();
        SSLServerSocket sslServerSocket = x509Util.createSSLServerSocket();
        Assert.assertArrayEquals(X509UtilTest.customCipherSuites, sslServerSocket.getEnabledCipherSuites());
        Assert.assertTrue(sslServerSocket.getNeedClientAuth());
    }

    @Test(timeout = 5000)
    public void testCreateSSLServerSocketWithPort() throws Exception {
        setCustomCipherSuites();
        int port = PortAssignment.unique();
        SSLServerSocket sslServerSocket = x509Util.createSSLServerSocket(port);
        Assert.assertEquals(sslServerSocket.getLocalPort(), port);
        Assert.assertArrayEquals(X509UtilTest.customCipherSuites, sslServerSocket.getEnabledCipherSuites());
        Assert.assertTrue(sslServerSocket.getNeedClientAuth());
    }

    @Test
    public void testLoadPEMKeyStore() throws Exception {
        // Make sure we can instantiate a key manager from the PEM file on disk
        X509KeyManager km = X509Util.createKeyManager(x509TestContext.getKeyStoreFile(PEM).getAbsolutePath(), x509TestContext.getKeyStorePassword(), PEM.getPropertyValue());
    }

    @Test
    public void testLoadPEMKeyStoreNullPassword() throws Exception {
        if (!(x509TestContext.getKeyStorePassword().isEmpty())) {
            return;
        }
        // Make sure that empty password and null password are treated the same
        X509KeyManager km = X509Util.createKeyManager(x509TestContext.getKeyStoreFile(PEM).getAbsolutePath(), null, PEM.getPropertyValue());
    }

    @Test
    public void testLoadPEMKeyStoreAutodetectStoreFileType() throws Exception {
        // Make sure we can instantiate a key manager from the PEM file on disk
        X509KeyManager km = /* null StoreFileType means 'autodetect from file extension' */
        X509Util.createKeyManager(x509TestContext.getKeyStoreFile(PEM).getAbsolutePath(), x509TestContext.getKeyStorePassword(), null);
    }

    @Test(expected = KeyManagerException.class)
    public void testLoadPEMKeyStoreWithWrongPassword() throws Exception {
        // Attempting to load with the wrong key password should fail
        X509KeyManager km = // intentionally use the wrong password
        X509Util.createKeyManager(x509TestContext.getKeyStoreFile(PEM).getAbsolutePath(), "wrong password", PEM.getPropertyValue());
    }

    @Test
    public void testLoadPEMTrustStore() throws Exception {
        // Make sure we can instantiate a trust manager from the PEM file on disk
        X509TrustManager tm = X509Util.createTrustManager(x509TestContext.getTrustStoreFile(PEM).getAbsolutePath(), x509TestContext.getTrustStorePassword(), PEM.getPropertyValue(), false, false, true, true);
    }

    @Test
    public void testLoadPEMTrustStoreNullPassword() throws Exception {
        if (!(x509TestContext.getTrustStorePassword().isEmpty())) {
            return;
        }
        // Make sure that empty password and null password are treated the same
        X509TrustManager tm = X509Util.createTrustManager(x509TestContext.getTrustStoreFile(PEM).getAbsolutePath(), null, PEM.getPropertyValue(), false, false, true, true);
    }

    @Test
    public void testLoadPEMTrustStoreAutodetectStoreFileType() throws Exception {
        // Make sure we can instantiate a trust manager from the PEM file on disk
        X509TrustManager tm = // null StoreFileType means 'autodetect from file extension'
        X509Util.createTrustManager(x509TestContext.getTrustStoreFile(PEM).getAbsolutePath(), x509TestContext.getTrustStorePassword(), null, false, false, true, true);
    }

    @Test
    public void testLoadJKSKeyStore() throws Exception {
        // Make sure we can instantiate a key manager from the JKS file on disk
        X509KeyManager km = X509Util.createKeyManager(x509TestContext.getKeyStoreFile(JKS).getAbsolutePath(), x509TestContext.getKeyStorePassword(), JKS.getPropertyValue());
    }

    @Test
    public void testLoadJKSKeyStoreNullPassword() throws Exception {
        if (!(x509TestContext.getKeyStorePassword().isEmpty())) {
            return;
        }
        // Make sure that empty password and null password are treated the same
        X509KeyManager km = X509Util.createKeyManager(x509TestContext.getKeyStoreFile(JKS).getAbsolutePath(), null, JKS.getPropertyValue());
    }

    @Test
    public void testLoadJKSKeyStoreAutodetectStoreFileType() throws Exception {
        // Make sure we can instantiate a key manager from the JKS file on disk
        X509KeyManager km = /* null StoreFileType means 'autodetect from file extension' */
        X509Util.createKeyManager(x509TestContext.getKeyStoreFile(JKS).getAbsolutePath(), x509TestContext.getKeyStorePassword(), null);
    }

    @Test(expected = KeyManagerException.class)
    public void testLoadJKSKeyStoreWithWrongPassword() throws Exception {
        // Attempting to load with the wrong key password should fail
        X509KeyManager km = X509Util.createKeyManager(x509TestContext.getKeyStoreFile(JKS).getAbsolutePath(), "wrong password", JKS.getPropertyValue());
    }

    @Test
    public void testLoadJKSTrustStore() throws Exception {
        // Make sure we can instantiate a trust manager from the JKS file on disk
        X509TrustManager tm = X509Util.createTrustManager(x509TestContext.getTrustStoreFile(JKS).getAbsolutePath(), x509TestContext.getTrustStorePassword(), JKS.getPropertyValue(), true, true, true, true);
    }

    @Test
    public void testLoadJKSTrustStoreNullPassword() throws Exception {
        if (!(x509TestContext.getTrustStorePassword().isEmpty())) {
            return;
        }
        // Make sure that empty password and null password are treated the same
        X509TrustManager tm = X509Util.createTrustManager(x509TestContext.getTrustStoreFile(JKS).getAbsolutePath(), null, JKS.getPropertyValue(), false, false, true, true);
    }

    @Test
    public void testLoadJKSTrustStoreAutodetectStoreFileType() throws Exception {
        // Make sure we can instantiate a trust manager from the JKS file on disk
        X509TrustManager tm = // null StoreFileType means 'autodetect from file extension'
        X509Util.createTrustManager(x509TestContext.getTrustStoreFile(JKS).getAbsolutePath(), x509TestContext.getTrustStorePassword(), null, true, true, true, true);
    }

    @Test(expected = TrustManagerException.class)
    public void testLoadJKSTrustStoreWithWrongPassword() throws Exception {
        // Attempting to load with the wrong key password should fail
        X509TrustManager tm = X509Util.createTrustManager(x509TestContext.getTrustStoreFile(JKS).getAbsolutePath(), "wrong password", JKS.getPropertyValue(), true, true, true, true);
    }

    @Test
    public void testGetSslHandshakeDetectionTimeoutMillisProperty() {
        Assert.assertEquals(DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS, x509Util.getSslHandshakeTimeoutMillis());
        // Note: need to create a new ClientX509Util each time to pick up modified property value
        String newPropertyString = Integer.toString(((DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS) + 1));
        System.setProperty(x509Util.getSslHandshakeDetectionTimeoutMillisProperty(), newPropertyString);
        try (X509Util tempX509Util = new ClientX509Util()) {
            Assert.assertEquals(((DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS) + 1), tempX509Util.getSslHandshakeTimeoutMillis());
        }
        // 0 value not allowed, will return the default
        System.setProperty(x509Util.getSslHandshakeDetectionTimeoutMillisProperty(), "0");
        try (X509Util tempX509Util = new ClientX509Util()) {
            Assert.assertEquals(DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS, tempX509Util.getSslHandshakeTimeoutMillis());
        }
        // Negative value not allowed, will return the default
        System.setProperty(x509Util.getSslHandshakeDetectionTimeoutMillisProperty(), "-1");
        try (X509Util tempX509Util = new ClientX509Util()) {
            Assert.assertEquals(DEFAULT_HANDSHAKE_DETECTION_TIMEOUT_MILLIS, tempX509Util.getSslHandshakeTimeoutMillis());
        }
    }

    @Test(expected = SSLContextException.class)
    public void testCreateSSLContext_invalidCustomSSLContextClass() throws Exception {
        ZKConfig zkConfig = new ZKConfig();
        ClientX509Util clientX509Util = new ClientX509Util();
        zkConfig.setProperty(clientX509Util.getSslContextSupplierClassProperty(), String.class.getCanonicalName());
        clientX509Util.createSSLContext(zkConfig);
    }

    @Test
    public void testCreateSSLContext_validCustomSSLContextClass() throws Exception {
        ZKConfig zkConfig = new ZKConfig();
        ClientX509Util clientX509Util = new ClientX509Util();
        zkConfig.setProperty(clientX509Util.getSslContextSupplierClassProperty(), X509UtilTest.SslContextSupplier.class.getName());
        final SSLContext sslContext = clientX509Util.createSSLContext(zkConfig);
        Assert.assertEquals(SSLContext.getDefault(), sslContext);
    }

    // This test makes sure that client-initiated TLS renegotiation does not
    // succeed. We explicitly disable it at the top of X509Util.java.
    @Test(expected = SSLHandshakeException.class)
    public void testClientRenegotiationFails() throws Throwable {
        int port = PortAssignment.unique();
        ExecutorService workerPool = Executors.newCachedThreadPool();
        final SSLServerSocket listeningSocket = x509Util.createSSLServerSocket();
        SSLSocket clientSocket = null;
        SSLSocket serverSocket = null;
        final AtomicInteger handshakesCompleted = new AtomicInteger(0);
        try {
            InetSocketAddress localServerAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
            listeningSocket.bind(localServerAddress);
            Future<SSLSocket> acceptFuture;
            acceptFuture = workerPool.submit(new Callable<SSLSocket>() {
                @Override
                public SSLSocket call() throws Exception {
                    SSLSocket sslSocket = ((SSLSocket) (listeningSocket.accept()));
                    sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                        @Override
                        public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
                            handshakesCompleted.getAndIncrement();
                        }
                    });
                    Assert.assertEquals(1, sslSocket.getInputStream().read());
                    try {
                        // 2nd read is after the renegotiation attempt and will fail
                        sslSocket.getInputStream().read();
                        return sslSocket;
                    } catch (Exception e) {
                        X509UtilTest.forceClose(sslSocket);
                        throw e;
                    }
                }
            });
            clientSocket = x509Util.createSSLSocket();
            clientSocket.connect(localServerAddress);
            clientSocket.getOutputStream().write(1);
            // Attempt to renegotiate after establishing the connection
            clientSocket.startHandshake();
            clientSocket.getOutputStream().write(1);
            // The exception is thrown on the server side, we need to unwrap it
            try {
                serverSocket = acceptFuture.get();
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        } finally {
            X509UtilTest.forceClose(serverSocket);
            X509UtilTest.forceClose(clientSocket);
            X509UtilTest.forceClose(listeningSocket);
            workerPool.shutdown();
            // Make sure the first handshake completed and only the second
            // one failed.
            Assert.assertEquals(1, handshakesCompleted.get());
        }
    }

    @Test
    public void testGetDefaultCipherSuitesJava8() {
        String[] cipherSuites = X509Util.getDefaultCipherSuitesForJavaVersion("1.8");
        // Java 8 default should have the CBC suites first
        Assert.assertTrue(cipherSuites[0].contains("CBC"));
    }

    @Test
    public void testGetDefaultCipherSuitesJava9() {
        String[] cipherSuites = X509Util.getDefaultCipherSuitesForJavaVersion("9");
        // Java 9+ default should have the GCM suites first
        Assert.assertTrue(cipherSuites[0].contains("GCM"));
    }

    @Test
    public void testGetDefaultCipherSuitesJava10() {
        String[] cipherSuites = X509Util.getDefaultCipherSuitesForJavaVersion("10");
        // Java 9+ default should have the GCM suites first
        Assert.assertTrue(cipherSuites[0].contains("GCM"));
    }

    @Test
    public void testGetDefaultCipherSuitesJava11() {
        String[] cipherSuites = X509Util.getDefaultCipherSuitesForJavaVersion("11");
        // Java 9+ default should have the GCM suites first
        Assert.assertTrue(cipherSuites[0].contains("GCM"));
    }

    @Test
    public void testGetDefaultCipherSuitesUnknownVersion() {
        String[] cipherSuites = X509Util.getDefaultCipherSuitesForJavaVersion("notaversion");
        // If version can't be parsed, use the more conservative Java 8 default
        Assert.assertTrue(cipherSuites[0].contains("CBC"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetDefaultCipherSuitesNullVersion() {
        X509Util.getDefaultCipherSuitesForJavaVersion(null);
    }

    public static class SslContextSupplier implements Supplier<SSLContext> {
        @Override
        public SSLContext get() {
            try {
                return SSLContext.getDefault();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

