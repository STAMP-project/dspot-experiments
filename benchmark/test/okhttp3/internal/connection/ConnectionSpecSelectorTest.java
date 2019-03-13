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
package okhttp3.internal.connection;


import ConnectionSpec.COMPATIBLE_TLS;
import ConnectionSpec.MODERN_TLS;
import TlsVersion.SSL_3_0;
import TlsVersion.TLS_1_0;
import TlsVersion.TLS_1_1;
import TlsVersion.TLS_1_2;
import java.io.IOException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;
import okhttp3.internal.Internal;
import okhttp3.tls.HandshakeCertificates;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionSpecSelectorTest {
    static {
        Internal.initializeInstanceForTests();
    }

    public static final SSLHandshakeException RETRYABLE_EXCEPTION = new SSLHandshakeException("Simulated handshake exception");

    private HandshakeCertificates handshakeCertificates = localhost();

    @Test
    public void nonRetryableIOException() throws Exception {
        ConnectionSpecSelector connectionSpecSelector = ConnectionSpecSelectorTest.createConnectionSpecSelector(MODERN_TLS, COMPATIBLE_TLS);
        SSLSocket socket = createSocketWithEnabledProtocols(TLS_1_1, TLS_1_0);
        connectionSpecSelector.configureSecureSocket(socket);
        boolean retry = connectionSpecSelector.connectionFailed(new IOException("Non-handshake exception"));
        Assert.assertFalse(retry);
        socket.close();
    }

    @Test
    public void nonRetryableSSLHandshakeException() throws Exception {
        ConnectionSpecSelector connectionSpecSelector = ConnectionSpecSelectorTest.createConnectionSpecSelector(MODERN_TLS, COMPATIBLE_TLS);
        SSLSocket socket = createSocketWithEnabledProtocols(TLS_1_1, TLS_1_0);
        connectionSpecSelector.configureSecureSocket(socket);
        SSLHandshakeException trustIssueException = new SSLHandshakeException("Certificate handshake exception");
        trustIssueException.initCause(new CertificateException());
        boolean retry = connectionSpecSelector.connectionFailed(trustIssueException);
        Assert.assertFalse(retry);
        socket.close();
    }

    @Test
    public void retryableSSLHandshakeException() throws Exception {
        ConnectionSpecSelector connectionSpecSelector = ConnectionSpecSelectorTest.createConnectionSpecSelector(MODERN_TLS, COMPATIBLE_TLS);
        SSLSocket socket = createSocketWithEnabledProtocols(TLS_1_2, TLS_1_1, TLS_1_0);
        connectionSpecSelector.configureSecureSocket(socket);
        boolean retry = connectionSpecSelector.connectionFailed(ConnectionSpecSelectorTest.RETRYABLE_EXCEPTION);
        Assert.assertTrue(retry);
        socket.close();
    }

    @Test
    public void someFallbacksSupported() throws Exception {
        ConnectionSpec sslV3 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(SSL_3_0).build();
        ConnectionSpecSelector connectionSpecSelector = ConnectionSpecSelectorTest.createConnectionSpecSelector(MODERN_TLS, COMPATIBLE_TLS, sslV3);
        TlsVersion[] enabledSocketTlsVersions = new TlsVersion[]{ TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0 };
        SSLSocket socket = createSocketWithEnabledProtocols(enabledSocketTlsVersions);
        // MODERN_TLS is used here.
        connectionSpecSelector.configureSecureSocket(socket);
        ConnectionSpecSelectorTest.assertEnabledProtocols(socket, TLS_1_2);
        boolean retry = connectionSpecSelector.connectionFailed(ConnectionSpecSelectorTest.RETRYABLE_EXCEPTION);
        Assert.assertTrue(retry);
        socket.close();
        // COMPATIBLE_TLS is used here.
        socket = createSocketWithEnabledProtocols(enabledSocketTlsVersions);
        connectionSpecSelector.configureSecureSocket(socket);
        ConnectionSpecSelectorTest.assertEnabledProtocols(socket, TLS_1_2, TLS_1_1, TLS_1_0);
        retry = connectionSpecSelector.connectionFailed(ConnectionSpecSelectorTest.RETRYABLE_EXCEPTION);
        Assert.assertFalse(retry);
        socket.close();
        // sslV3 is not used because SSLv3 is not enabled on the socket.
    }
}

