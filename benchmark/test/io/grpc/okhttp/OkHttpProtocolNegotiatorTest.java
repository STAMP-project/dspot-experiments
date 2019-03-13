/**
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.okhttp;


import Protocol.GRPC_EXP;
import Protocol.HTTP_2;
import TlsExtensionType.ALPN_AND_NPN;
import TlsExtensionType.NPN;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import io.grpc.okhttp.OkHttpProtocolNegotiator.AndroidNegotiator;
import io.grpc.okhttp.internal.Platform;
import java.io.IOException;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link OkHttpProtocolNegotiator}.
 */
@RunWith(JUnit4.class)
public class OkHttpProtocolNegotiatorTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final SSLSocket sock = Mockito.mock(SSLSocket.class);

    private final Platform platform = Mockito.mock(Platform.class);

    @Test
    public void createNegotiator_isAndroid() {
        ClassLoader cl = new ClassLoader(this.getClass().getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                // Just don't throw.
                if ("com.android.org.conscrypt.OpenSSLSocketImpl".equals(name)) {
                    return null;
                }
                return super.findClass(name);
            }
        };
        OkHttpProtocolNegotiator negotiator = OkHttpProtocolNegotiator.createNegotiator(cl);
        Assert.assertEquals(AndroidNegotiator.class, negotiator.getClass());
    }

    @Test
    public void createNegotiator_isAndroidLegacy() {
        ClassLoader cl = new ClassLoader(this.getClass().getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                // Just don't throw.
                if ("org.apache.harmony.xnet.provider.jsse.OpenSSLSocketImpl".equals(name)) {
                    return null;
                }
                return super.findClass(name);
            }
        };
        OkHttpProtocolNegotiator negotiator = OkHttpProtocolNegotiator.createNegotiator(cl);
        Assert.assertEquals(AndroidNegotiator.class, negotiator.getClass());
    }

    @Test
    public void createNegotiator_notAndroid() {
        ClassLoader cl = new ClassLoader(this.getClass().getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if ("com.android.org.conscrypt.OpenSSLSocketImpl".equals(name)) {
                    throw new ClassNotFoundException();
                }
                return super.findClass(name);
            }
        };
        OkHttpProtocolNegotiator negotiator = OkHttpProtocolNegotiator.createNegotiator(cl);
        Assert.assertEquals(OkHttpProtocolNegotiator.class, negotiator.getClass());
    }

    @Test
    public void negotiatorNotNull() {
        Assert.assertNotNull(OkHttpProtocolNegotiator.get());
    }

    @Test
    public void negotiate_handshakeFails() throws IOException {
        SSLParameters parameters = new SSLParameters();
        OkHttpProtocolNegotiator negotiator = OkHttpProtocolNegotiator.get();
        Mockito.doReturn(parameters).when(sock).getSSLParameters();
        Mockito.doThrow(new IOException()).when(sock).startHandshake();
        thrown.expect(IOException.class);
        negotiator.negotiate(sock, "hostname", ImmutableList.of(HTTP_2));
    }

    @Test
    public void negotiate_noSelectedProtocol() throws Exception {
        Platform platform = Mockito.mock(Platform.class);
        OkHttpProtocolNegotiator negotiator = new OkHttpProtocolNegotiator(platform);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("TLS ALPN negotiation failed");
        negotiator.negotiate(sock, "hostname", ImmutableList.of(HTTP_2));
    }

    @Test
    public void negotiate_success() throws Exception {
        Mockito.when(platform.getSelectedProtocol(Mockito.<SSLSocket>any())).thenReturn("h2");
        OkHttpProtocolNegotiator negotiator = new OkHttpProtocolNegotiator(platform);
        String actual = negotiator.negotiate(sock, "hostname", ImmutableList.of(HTTP_2));
        Assert.assertEquals("h2", actual);
        Mockito.verify(sock).startHandshake();
        Mockito.verify(platform).getSelectedProtocol(sock);
        Mockito.verify(platform).afterHandshake(sock);
    }

    @Test
    public void negotiate_preferGrpcExp() throws Exception {
        // This test doesn't actually verify that grpc-exp is preferred, since the
        // mocking of getSelectedProtocol() causes the protocol list to be ignored.
        // The main usefulness of the test is for future changes to
        // OkHttpProtocolNegotiator, where we can catch any change that would affect
        // grpc-exp preference.
        Mockito.when(platform.getSelectedProtocol(Mockito.<SSLSocket>any())).thenReturn("grpc-exp");
        OkHttpProtocolNegotiator negotiator = new OkHttpProtocolNegotiator(platform);
        String actual = negotiator.negotiate(sock, "hostname", ImmutableList.of(GRPC_EXP, HTTP_2));
        Assert.assertEquals("grpc-exp", actual);
        Mockito.verify(sock).startHandshake();
        Mockito.verify(platform).getSelectedProtocol(sock);
        Mockito.verify(platform).afterHandshake(sock);
    }

    // Checks that the super class is properly invoked.
    @Test
    public void negotiate_android_handshakeFails() throws Exception {
        Mockito.when(platform.getTlsExtensionType()).thenReturn(ALPN_AND_NPN);
        AndroidNegotiator negotiator = new AndroidNegotiator(platform);
        OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket androidSock = new OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket() {
            @Override
            public void startHandshake() throws IOException {
                throw new IOException("expected");
            }
        };
        thrown.expect(IOException.class);
        thrown.expectMessage("expected");
        negotiator.negotiate(androidSock, "hostname", ImmutableList.of(HTTP_2));
    }

    @VisibleForTesting
    public static class FakeAndroidSslSocketAlpn extends OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket {
        @Override
        public byte[] getAlpnSelectedProtocol() {
            return "h2".getBytes(Charsets.UTF_8);
        }
    }

    @Test
    public void getSelectedProtocol_alpn() throws Exception {
        Mockito.when(platform.getTlsExtensionType()).thenReturn(ALPN_AND_NPN);
        AndroidNegotiator negotiator = new AndroidNegotiator(platform);
        OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket androidSock = new OkHttpProtocolNegotiatorTest.FakeAndroidSslSocketAlpn();
        String actual = negotiator.getSelectedProtocol(androidSock);
        Assert.assertEquals("h2", actual);
    }

    @VisibleForTesting
    public static class FakeAndroidSslSocketNpn extends OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket {
        @Override
        public byte[] getNpnSelectedProtocol() {
            return "h2".getBytes(Charsets.UTF_8);
        }
    }

    @Test
    public void getSelectedProtocol_npn() throws Exception {
        Mockito.when(platform.getTlsExtensionType()).thenReturn(NPN);
        AndroidNegotiator negotiator = new AndroidNegotiator(platform);
        OkHttpProtocolNegotiatorTest.FakeAndroidSslSocket androidSock = new OkHttpProtocolNegotiatorTest.FakeAndroidSslSocketNpn();
        String actual = negotiator.getSelectedProtocol(androidSock);
        Assert.assertEquals("h2", actual);
    }

    // A fake of org.conscrypt.OpenSSLSocketImpl
    // Must be public for reflection to work
    @VisibleForTesting
    public static class FakeAndroidSslSocket extends SSLSocket {
        public void setUseSessionTickets(boolean arg) {
        }

        public void setHostname(String arg) {
        }

        public byte[] getAlpnSelectedProtocol() {
            return null;
        }

        public void setAlpnProtocols(byte[] arg) {
        }

        public byte[] getNpnSelectedProtocol() {
            return null;
        }

        public void setNpnProtocols(byte[] arg) {
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
        }

        @Override
        public boolean getEnableSessionCreation() {
            return false;
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return null;
        }

        @Override
        public String[] getEnabledProtocols() {
            return null;
        }

        @Override
        public boolean getNeedClientAuth() {
            return false;
        }

        @Override
        public SSLSession getSession() {
            return null;
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return null;
        }

        @Override
        public String[] getSupportedProtocols() {
            return null;
        }

        @Override
        public boolean getUseClientMode() {
            return false;
        }

        @Override
        public boolean getWantClientAuth() {
            return false;
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
        }

        @Override
        public void setEnableSessionCreation(boolean flag) {
        }

        @Override
        public void setEnabledCipherSuites(String[] suites) {
        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
        }

        @Override
        public void setNeedClientAuth(boolean need) {
        }

        @Override
        public void setUseClientMode(boolean mode) {
        }

        @Override
        public void setWantClientAuth(boolean want) {
        }

        @Override
        public void startHandshake() throws IOException {
        }
    }
}

