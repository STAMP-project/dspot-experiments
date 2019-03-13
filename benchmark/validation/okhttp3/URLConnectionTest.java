/**
 * Copyright (C) 2009 The Android Open Source Project
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


import Call.Factory;
import ConnectionSpec.COMPATIBLE_TLS;
import ConnectionSpec.MODERN_TLS;
import ConnectionSpec.RESTRICTED_TLS;
import Headers.Builder;
import Internal.instance;
import Protocol.HTTP_1_0;
import Protocol.HTTP_1_1;
import Protocol.HTTP_2;
import TlsVersion.TLS_1_0;
import TlsVersion.TLS_1_2;
import TlsVersion.TLS_1_3;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.internal.RecordingAuthenticator;
import okhttp3.internal.RecordingOkAuthenticator;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.platform.Platform;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.tls.HandshakeCertificates;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Utf8;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static java.net.Authenticator.RequestorType.PROXY;
import static java.net.Authenticator.RequestorType.SERVER;


/**
 * Android's URLConnectionTest, ported to exercise OkHttp's Call API.
 */
public final class URLConnectionTest {
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final MockWebServer server2 = new MockWebServer();

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    private HandshakeCertificates handshakeCertificates = localhost();

    private OkHttpClient client = clientTestRule.client;

    @Nullable
    private Cache cache;

    @Test
    public void requestHeaders() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).addHeader("D", "e").addHeader("D", "f").build();
        Assert.assertEquals("f", request.header("D"));
        Assert.assertEquals("f", request.header("d"));
        Headers requestHeaders = request.headers();
        Assert.assertEquals(newSet("e", "f"), new java.util.LinkedHashSet(requestHeaders.values("D")));
        Assert.assertEquals(newSet("e", "f"), new java.util.LinkedHashSet(requestHeaders.values("d")));
        try {
            new Request.Builder().header(null, "j");
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Request.Builder().addHeader(null, "k");
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Request.Builder().addHeader("NullValue", null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Request.Builder().addHeader("AnotherNullValue", null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        Response response = getResponse(request);
        response.close();
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(Arrays.asList("e", "f"), recordedRequest.getHeaders().values("D"));
        Assert.assertNull(recordedRequest.getHeader("G"));
        Assert.assertNull(recordedRequest.getHeader("null"));
    }

    @Test
    public void getRequestPropertyReturnsLastValue() {
        Request request = new Request.Builder().url(server.url("/")).addHeader("A", "value1").addHeader("A", "value2").build();
        Assert.assertEquals("value2", request.header("A"));
    }

    @Test
    public void responseHeaders() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.0 200 Fantastic").addHeader("A: c").addHeader("B: d").addHeader("A: e").setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8));
        Request request = newRequest("/");
        Response response = getResponse(request);
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("Fantastic", response.message());
        try {
            response.header(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        Headers responseHeaders = response.headers();
        Assert.assertEquals(newSet("c", "e"), new java.util.LinkedHashSet(responseHeaders.values("A")));
        Assert.assertEquals(newSet("c", "e"), new java.util.LinkedHashSet(responseHeaders.values("a")));
        Assert.assertEquals("A", responseHeaders.name(0));
        Assert.assertEquals("c", responseHeaders.value(0));
        Assert.assertEquals("B", responseHeaders.name(1));
        Assert.assertEquals("d", responseHeaders.value(1));
        Assert.assertEquals("A", responseHeaders.name(2));
        Assert.assertEquals("e", responseHeaders.value(2));
        response.body().close();
    }

    @Test
    public void serverSendsInvalidStatusLine() {
        server.enqueue(new MockResponse().setStatus("HTP/1.1 200 OK"));
        Request request = newRequest("/");
        try {
            getResponse(request);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void serverSendsInvalidCodeTooLarge() {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 2147483648 OK"));
        Request request = newRequest("/");
        try {
            getResponse(request);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void serverSendsInvalidCodeNotANumber() {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 00a OK"));
        Request request = newRequest("/");
        try {
            getResponse(request);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void serverSendsUnnecessaryWhitespace() {
        server.enqueue(new MockResponse().setStatus(" HTTP/1.1 2147483648 OK"));
        Request request = newRequest("/");
        try {
            getResponse(request);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void connectRetriesUntilConnectedOrFailed() throws Exception {
        Request request = newRequest("/foo");
        server.shutdown();
        try {
            getResponse(request);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void requestBodySurvivesRetriesWithFixedLength() throws Exception {
        testRequestBodySurvivesRetries(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void requestBodySurvivesRetriesWithChunkedStreaming() throws Exception {
        testRequestBodySurvivesRetries(URLConnectionTest.TransferKind.CHUNKED);
    }

    // Check that if we don't read to the end of a response, the next request on the
    // recycled connection doesn't get the unread tail of the first request's response.
    // http://code.google.com/p/android/issues/detail?id=2939
    @Test
    public void bug2939() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8);
        server.enqueue(response);
        server.enqueue(response);
        Request request = newRequest("/");
        Response c1 = getResponse(request);
        assertContent("ABCDE", c1, 5);
        Response c2 = getResponse(request);
        assertContent("ABCDE", c2, 5);
        c1.close();
        c2.close();
    }

    @Test
    public void connectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setBody("ABCDEFGHIJKLMNOPQR");
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/foo")));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/bar?baz=quux")));
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/z")));
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void chunkedConnectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDEFGHIJKLMNOPQR", 5);
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/foo")));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/bar?baz=quux")));
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", getResponse(newRequest("/z")));
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void serverClosesSocket() throws Exception {
        testServerClosesOutput(DISCONNECT_AT_END);
    }

    @Test
    public void serverShutdownInput() throws Exception {
        testServerClosesOutput(SHUTDOWN_INPUT_AT_END);
    }

    @Test
    public void serverShutdownOutput() throws Exception {
        testServerClosesOutput(SHUTDOWN_OUTPUT_AT_END);
    }

    @Test
    public void invalidHost() throws Exception {
        // Note that 1234.1.1.1 is an invalid host in a URI, but URL isn't as strict.
        client = client.newBuilder().dns(new FakeDns()).build();
        try {
            getResponse(new Request.Builder().url(HttpUrl.get("http://1234.1.1.1/index.html")).build());
            Assert.fail();
        } catch (UnknownHostException expected) {
        }
    }

    enum WriteKind {

        BYTE_BY_BYTE,
        SMALL_BUFFERS,
        LARGE_BUFFERS;}

    @Test
    public void chunkedUpload_byteByByte() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.BYTE_BY_BYTE);
    }

    @Test
    public void chunkedUpload_smallBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.SMALL_BUFFERS);
    }

    @Test
    public void chunkedUpload_largeBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.LARGE_BUFFERS);
    }

    @Test
    public void fixedLengthUpload_byteByByte() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.BYTE_BY_BYTE);
    }

    @Test
    public void fixedLengthUpload_smallBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.SMALL_BUFFERS);
    }

    @Test
    public void fixedLengthUpload_largeBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.LARGE_BUFFERS);
    }

    @Test
    public void connectViaHttps() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response = getResponse(newRequest("/foo"));
        assertContent("this response comes via HTTPS", response);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void connectViaHttpsReusingConnections() throws Exception {
        connectViaHttpsReusingConnections(false);
    }

    @Test
    public void connectViaHttpsReusingConnectionsAfterRebuildingClient() throws Exception {
        connectViaHttpsReusingConnections(true);
    }

    @Test
    public void connectViaHttpsReusingConnectionsDifferentFactories() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.enqueue(new MockResponse().setBody("another response via HTTPS"));
        // install a custom SSL socket factory so the server can be authorized
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response1 = getResponse(newRequest("/"));
        assertContent("this response comes via HTTPS", response1);
        SSLContext sslContext2 = Platform.get().getSSLContext();
        sslContext2.init(null, null, null);
        SSLSocketFactory sslSocketFactory2 = sslContext2.getSocketFactory();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(((KeyStore) (null)));
        X509TrustManager trustManager = ((X509TrustManager) (trustManagerFactory.getTrustManagers()[0]));
        client = client.newBuilder().sslSocketFactory(sslSocketFactory2, trustManager).build();
        try {
            getResponse(newRequest("/"));
            Assert.fail("without an SSL socket factory, the connection should fail");
        } catch (SSLException expected) {
        }
    }

    // TODO(jwilson): tests below this marker need to be migrated to OkHttp's request/response API.
    @Test
    public void connectViaHttpsWithSSLFallback() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("this response comes via SSL"));
        client = // Attempt RESTRICTED_TLS then fall back to MODERN_TLS.
        client.newBuilder().hostnameVerifier(new RecordingHostnameVerifier()).connectionSpecs(Arrays.asList(RESTRICTED_TLS, MODERN_TLS)).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        Response response = getResponse(newRequest("/foo"));
        assertContent("this response comes via SSL", response);
        RecordedRequest failHandshakeRequest = server.takeRequest();
        Assert.assertNull(failHandshakeRequest.getRequestLine());
        RecordedRequest fallbackRequest = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", fallbackRequest.getRequestLine());
        Assert.assertThat(fallbackRequest.getTlsVersion(), CoreMatchers.either(CoreMatchers.equalTo(TLS_1_2)).or(CoreMatchers.equalTo(TLS_1_3)));
    }

    @Test
    public void connectViaHttpsWithSSLFallbackFailuresRecorded() {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        client = client.newBuilder().connectionSpecs(Arrays.asList(MODERN_TLS, COMPATIBLE_TLS)).hostnameVerifier(new RecordingHostnameVerifier()).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        try {
            getResponse(newRequest("/foo"));
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals(1, expected.getSuppressed().length);
        }
    }

    /**
     * When a pooled connection fails, don't blame the route. Otherwise pooled connection failures can
     * cause unnecessary SSL fallbacks.
     *
     * https://github.com/square/okhttp/issues/515
     */
    @Test
    public void sslFallbackNotUsedWhenRecycledConnectionFails() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("abc").setSocketPolicy(DISCONNECT_AT_END));
        server.enqueue(new MockResponse().setBody("def"));
        client = client.newBuilder().hostnameVerifier(new RecordingHostnameVerifier()).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        assertContent("abc", getResponse(newRequest("/")));
        // Give the server time to disconnect.
        Thread.sleep(500);
        assertContent("def", getResponse(newRequest("/")));
        Set<TlsVersion> tlsVersions = EnumSet.of(TLS_1_0, TLS_1_2, TLS_1_3);// v1.2 on OpenJDK 8.

        RecordedRequest request1 = server.takeRequest();
        Assert.assertTrue(tlsVersions.contains(request1.getTlsVersion()));
        RecordedRequest request2 = server.takeRequest();
        Assert.assertTrue(tlsVersions.contains(request2.getTlsVersion()));
    }

    /**
     * Verify that we don't retry connections on certificate verification errors.
     *
     * http://code.google.com/p/android/issues/detail?id=13178
     */
    @Test
    public void connectViaHttpsToUntrustedServer() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse());// unused

        try {
            getResponse(newRequest("/foo"));
            Assert.fail();
        } catch (SSLHandshakeException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof CertificateException));
        }
        Assert.assertEquals(0, server.getRequestCount());
    }

    @Test
    public void connectViaProxyUsingProxyArg() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.CREATE_ARG);
    }

    @Test
    public void connectViaProxyUsingProxySystemProperty() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    @Test
    public void connectViaProxyUsingHttpProxySystemProperty() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    @Test
    public void contentDisagreesWithContentLengthHeaderBodyTooLong() throws IOException {
        server.enqueue(new MockResponse().setBody("abc\r\nYOU SHOULD NOT SEE THIS").clearHeaders().addHeader("Content-Length: 3"));
        assertContent("abc", getResponse(newRequest("/")));
    }

    @Test
    public void contentDisagreesWithContentLengthHeaderBodyTooShort() throws IOException {
        server.enqueue(new MockResponse().setBody("abc").setHeader("Content-Length", "5").setSocketPolicy(DISCONNECT_AT_END));
        try {
            Response response = getResponse(newRequest("/"));
            response.body().source().readUtf8(5);
            Assert.fail();
        } catch (ProtocolException expected) {
        }
    }

    @Test
    public void connectHttpViaSocketFactory() throws Exception {
        testConnectViaSocketFactory(false);
    }

    @Test
    public void connectHttpsViaSocketFactory() throws Exception {
        testConnectViaSocketFactory(true);
    }

    @Test
    public void contentDisagreesWithChunkedHeaderBodyTooLong() throws IOException {
        MockResponse mockResponse = new MockResponse().setChunkedBody("abc", 3);
        Buffer buffer = mockResponse.getBody();
        buffer.writeUtf8("\r\nYOU SHOULD NOT SEE THIS");
        mockResponse.setBody(buffer);
        mockResponse.clearHeaders();
        mockResponse.addHeader("Transfer-encoding: chunked");
        server.enqueue(mockResponse);
        assertContent("abc", getResponse(newRequest("/")));
    }

    @Test
    public void contentDisagreesWithChunkedHeaderBodyTooShort() {
        MockResponse mockResponse = new MockResponse().setChunkedBody("abcdefg", 5);
        Buffer truncatedBody = new Buffer();
        Buffer fullBody = mockResponse.getBody();
        truncatedBody.write(fullBody, 4);
        mockResponse.setBody(truncatedBody);
        mockResponse.clearHeaders();
        mockResponse.addHeader("Transfer-encoding: chunked");
        mockResponse.setSocketPolicy(DISCONNECT_AT_END);
        server.enqueue(mockResponse);
        try {
            Response response = getResponse(newRequest("/"));
            response.body().source().readUtf8(7);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void connectViaHttpProxyToHttpsUsingProxyArgWithNoProxy() throws Exception {
        testConnectViaDirectProxyToHttps(URLConnectionTest.ProxyConfig.NO_PROXY);
    }

    @Test
    public void connectViaHttpProxyToHttpsUsingHttpProxySystemProperty() throws Exception {
        // https should not use http proxy
        testConnectViaDirectProxyToHttps(URLConnectionTest.ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    @Test
    public void connectViaHttpProxyToHttpsUsingProxyArg() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.CREATE_ARG);
    }

    /**
     * We weren't honoring all of the appropriate proxy system properties when connecting via HTTPS.
     * http://b/3097518
     */
    @Test
    public void connectViaHttpProxyToHttpsUsingProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    @Test
    public void connectViaHttpProxyToHttpsUsingHttpsProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.HTTPS_PROXY_SYSTEM_PROPERTY);
    }

    /**
     * Tolerate bad https proxy response when using HttpResponseCache. Android bug 6754912.
     */
    @Test
    public void connectViaHttpProxyToHttpsUsingBadProxyAndHttpResponseCache() throws Exception {
        initResponseCache();
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        // The inclusion of a body in the response to a CONNECT is key to reproducing b/6754912.
        MockResponse badProxyResponse = new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).setBody("bogus proxy connect response content");
        server.enqueue(badProxyResponse);
        server.enqueue(new MockResponse().setBody("response"));
        // Configure a single IP address for the host and a single configuration, so we only need one
        // failure to fail permanently.
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).connectionSpecs(Util.immutableList(MODERN_TLS)).hostnameVerifier(new RecordingHostnameVerifier()).proxy(server.toProxyAddress()).build();
        Response response = getResponse(new Request.Builder().url(HttpUrl.get("https://android.com/foo")).build());
        assertContent("response", response);
        RecordedRequest connect = server.takeRequest();
        Assert.assertEquals("CONNECT android.com:443 HTTP/1.1", connect.getRequestLine());
        Assert.assertEquals("android.com:443", connect.getHeader("Host"));
    }

    /**
     * Test which headers are sent unencrypted to the HTTP proxy.
     */
    @Test
    public void proxyConnectIncludesProxyHeadersOnly() throws Exception {
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("encrypted response from the origin server"));
        client = client.newBuilder().proxy(server.toProxyAddress()).sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(hostnameVerifier).build();
        Response response = getResponse(new Request.Builder().url(HttpUrl.get("https://android.com/foo")).header("Private", "Secret").header("Proxy-Authorization", "bar").header("User-Agent", "baz").build());
        assertContent("encrypted response from the origin server", response);
        RecordedRequest connect = server.takeRequest();
        Assert.assertNull(connect.getHeader("Private"));
        Assert.assertNull(connect.getHeader("Proxy-Authorization"));
        Assert.assertEquals(Version.userAgent(), connect.getHeader("User-Agent"));
        Assert.assertEquals("android.com:443", connect.getHeader("Host"));
        Assert.assertEquals("Keep-Alive", connect.getHeader("Proxy-Connection"));
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("Secret", get.getHeader("Private"));
        Assert.assertEquals(Arrays.asList("verify android.com"), hostnameVerifier.calls);
    }

    @Test
    public void proxyAuthenticateOnConnect() throws Exception {
        Authenticator.setDefault(new RecordingAuthenticator());
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\""));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("A"));
        client = client.newBuilder().proxyAuthenticator(new JavaNetAuthenticator()).proxy(server.toProxyAddress()).sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response = getResponse(new Request.Builder().url(HttpUrl.parse("https://android.com/foo")).build());
        assertContent("A", response);
        RecordedRequest connect1 = server.takeRequest();
        Assert.assertEquals("CONNECT android.com:443 HTTP/1.1", connect1.getRequestLine());
        Assert.assertNull(connect1.getHeader("Proxy-Authorization"));
        RecordedRequest connect2 = server.takeRequest();
        Assert.assertEquals("CONNECT android.com:443 HTTP/1.1", connect2.getRequestLine());
        Assert.assertEquals(("Basic " + (RecordingAuthenticator.BASE_64_CREDENTIALS)), connect2.getHeader("Proxy-Authorization"));
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", get.getRequestLine());
        Assert.assertNull(get.getHeader("Proxy-Authorization"));
    }

    // Don't disconnect after building a tunnel with CONNECT
    // http://code.google.com/p/android/issues/detail?id=37221
    @Test
    public void proxyWithConnectionClose() throws IOException {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("this response comes via a proxy"));
        client = client.newBuilder().proxy(server.toProxyAddress()).sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response = getResponse(new Request.Builder().url("https://android.com/foo").header("Connection", "close").build());
        assertContent("this response comes via a proxy", response);
    }

    @Test
    public void proxyWithConnectionReuse() throws IOException {
        SSLSocketFactory socketFactory = handshakeCertificates.sslSocketFactory();
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        server.useHttps(socketFactory, true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("response 1"));
        server.enqueue(new MockResponse().setBody("response 2"));
        client = client.newBuilder().proxy(server.toProxyAddress()).sslSocketFactory(socketFactory, handshakeCertificates.trustManager()).hostnameVerifier(hostnameVerifier).build();
        assertContent("response 1", getResponse(newRequest(HttpUrl.get("https://android.com/foo"))));
        assertContent("response 2", getResponse(newRequest(HttpUrl.get("https://android.com/foo"))));
    }

    @Test
    public void proxySelectorHttpWithConnectionReuse() throws IOException {
        server.enqueue(new MockResponse().setBody("response 1"));
        server.enqueue(new MockResponse().setResponseCode(407));
        client = client.newBuilder().proxySelector(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                return Collections.singletonList(server.toProxyAddress());
            }

            @Override
            public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {
            }
        }).build();
        HttpUrl url = HttpUrl.get("http://android.com/foo");
        assertContent("response 1", getResponse(newRequest(url)));
        Assert.assertEquals(407, getResponse(newRequest(url)).code());
    }

    @Test
    public void disconnectedConnection() throws IOException {
        server.enqueue(new MockResponse().throttleBody(2, 100, TimeUnit.MILLISECONDS).setBody("ABCD"));
        Call call = client.newCall(newRequest("/"));
        Response response = call.execute();
        InputStream in = response.body().byteStream();
        Assert.assertEquals('A', ((char) (in.read())));
        call.cancel();
        try {
            // Reading 'B' may succeed if it's buffered.
            in.read();
            // But 'C' shouldn't be buffered (the response is throttled) and this should fail.
            in.read();
            Assert.fail("Expected a connection closed exception");
        } catch (IOException expected) {
        }
        in.close();
    }

    @Test
    public void disconnectDuringConnect_cookieJar() {
        AtomicReference<Call> callReference = new AtomicReference<>();
        class DisconnectingCookieJar implements CookieJar {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                callReference.get().cancel();
                return Collections.emptyList();
            }
        }
        client = client.newBuilder().cookieJar(new DisconnectingCookieJar()).build();
        Call call = client.newCall(newRequest("/"));
        callReference.set(call);
        try {
            call.execute();
            Assert.fail("Connection should not be established");
        } catch (IOException expected) {
            Assert.assertEquals("Canceled", expected.getMessage());
        }
    }

    @Test
    public void disconnectBeforeConnect() {
        server.enqueue(new MockResponse().setBody("A"));
        Call call = client.newCall(newRequest("/"));
        call.cancel();
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void defaultRequestProperty() {
        URLConnection.setDefaultRequestProperty("X-testSetDefaultRequestProperty", "A");
        Assert.assertNull(URLConnection.getDefaultRequestProperty("X-setDefaultRequestProperty"));
    }

    @Test
    public void markAndResetWithContentLengthHeader() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void markAndResetWithChunkedEncoding() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void markAndResetWithNoLengthHeaders() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    /**
     * We've had a bug where we forget the HTTP response when we see response code 401. This causes a
     * new HTTP request to be issued for every call into the URLConnection.
     */
    @Test
    public void unauthorizedResponseHandling() throws IOException {
        MockResponse mockResponse = new MockResponse().addHeader("WWW-Authenticate: challenge").setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED).setBody("Unauthorized");
        server.enqueue(mockResponse);
        server.enqueue(mockResponse);
        server.enqueue(mockResponse);
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(401, response.code());
        Assert.assertEquals(401, response.code());
        Assert.assertEquals(401, response.code());
        Assert.assertEquals(1, server.getRequestCount());
        response.body().close();
    }

    @Test
    public void nonHexChunkSize() {
        server.enqueue(new MockResponse().setBody("5\r\nABCDE\r\nG\r\nFGHIJKLMNOPQRSTU\r\n0\r\n\r\n").clearHeaders().addHeader("Transfer-encoding: chunked"));
        try (Response response = getResponse(newRequest("/"))) {
            response.body().string();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void malformedChunkSize() {
        server.enqueue(new MockResponse().setBody("5:x\r\nABCDE\r\n0\r\n\r\n").clearHeaders().addHeader("Transfer-encoding: chunked"));
        try (Response response = getResponse(newRequest("/"))) {
            readAscii(response.body().byteStream(), Integer.MAX_VALUE);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void extensionAfterChunkSize() throws IOException {
        server.enqueue(new MockResponse().setBody("5;x\r\nABCDE\r\n0\r\n\r\n").clearHeaders().addHeader("Transfer-encoding: chunked"));
        try (Response response = getResponse(newRequest("/"))) {
            assertContent("ABCDE", response);
        }
    }

    @Test
    public void missingChunkBody() {
        server.enqueue(new MockResponse().setBody("5").clearHeaders().addHeader("Transfer-encoding: chunked").setSocketPolicy(DISCONNECT_AT_END));
        try (Response response = getResponse(newRequest("/"))) {
            readAscii(response.body().byteStream(), Integer.MAX_VALUE);
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    /**
     * This test checks whether connections are gzipped by default. This behavior in not required by
     * the API, so a failure of this test does not imply a bug in the implementation.
     */
    @Test
    public void gzipEncodingEnabledByDefault() throws Exception {
        server.enqueue(new MockResponse().setBody(gzip("ABCABCABC")).addHeader("Content-Encoding: gzip"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("ABCABCABC", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        Assert.assertNull(response.header("Content-Encoding"));
        Assert.assertEquals((-1L), response.body().contentLength());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("gzip", request.getHeader("Accept-Encoding"));
    }

    @Test
    public void clientConfiguredGzipContentEncoding() throws Exception {
        Buffer bodyBytes = gzip("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        server.enqueue(new MockResponse().setBody(bodyBytes).addHeader("Content-Encoding: gzip"));
        Response response = getResponse(new Request.Builder().url(server.url("/")).header("Accept-Encoding", "gzip").build());
        InputStream gunzippedIn = new GZIPInputStream(response.body().byteStream());
        Assert.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", readAscii(gunzippedIn, Integer.MAX_VALUE));
        Assert.assertEquals(bodyBytes.size(), response.body().contentLength());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("gzip", request.getHeader("Accept-Encoding"));
    }

    @Test
    public void gzipAndConnectionReuseWithFixedLength() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.FIXED_LENGTH, false);
    }

    @Test
    public void gzipAndConnectionReuseWithChunkedEncoding() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.CHUNKED, false);
    }

    @Test
    public void gzipAndConnectionReuseWithFixedLengthAndTls() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.FIXED_LENGTH, true);
    }

    @Test
    public void gzipAndConnectionReuseWithChunkedEncodingAndTls() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.CHUNKED, true);
    }

    @Test
    public void clientConfiguredCustomContentEncoding() throws Exception {
        server.enqueue(new MockResponse().setBody("ABCDE").addHeader("Content-Encoding: custom"));
        Response response = getResponse(new Request.Builder().url(server.url("/")).header("Accept-Encoding", "custom").build());
        Assert.assertEquals("ABCDE", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("custom", request.getHeader("Accept-Encoding"));
    }

    @Test
    public void transparentGzipWorksAfterExceptionRecovery() throws Exception {
        server.enqueue(new MockResponse().setBody("a").setSocketPolicy(SHUTDOWN_INPUT_AT_END));
        server.enqueue(new MockResponse().addHeader("Content-Encoding: gzip").setBody(gzip("b")));
        // Seed the pool with a bad connection.
        assertContent("a", getResponse(newRequest("/")));
        // Give the server time to disconnect.
        Thread.sleep(500);
        // This connection will need to be recovered. When it is, transparent gzip should still work!
        assertContent("b", getResponse(newRequest("/")));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// Connection is not pooled.

    }

    @Test
    public void endOfStreamResponseIsNotPooled() throws Exception {
        client.connectionPool().evictAll();
        server.enqueue(new MockResponse().setBody("{}").clearHeaders().setSocketPolicy(DISCONNECT_AT_END));
        Response response = getResponse(newRequest("/"));
        assertContent("{}", response);
        Assert.assertEquals(0, client.connectionPool().idleConnectionCount());
    }

    @Test
    public void earlyDisconnectDoesntHarmPoolingWithChunkedEncoding() throws Exception {
        testEarlyDisconnectDoesntHarmPooling(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void earlyDisconnectDoesntHarmPoolingWithFixedLengthEncoding() throws Exception {
        testEarlyDisconnectDoesntHarmPooling(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void streamDiscardingIsTimely() throws Exception {
        // This response takes at least a full second to serve: 10,000 bytes served 100 bytes at a time.
        server.enqueue(new MockResponse().setBody(new Buffer().write(new byte[10000])).throttleBody(100, 10, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setBody("A"));
        long startNanos = System.nanoTime();
        Response connection1 = getResponse(newRequest("/"));
        InputStream in = connection1.body().byteStream();
        in.close();
        long elapsedNanos = (System.nanoTime()) - startNanos;
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
        // If we're working correctly, this should be greater than 100ms, but less than double that.
        // Previously we had a bug where we would download the entire response body as long as no
        // individual read took longer than 100ms.
        Assert.assertTrue(Util.format("Time to close: %sms", elapsedMillis), (elapsedMillis < 500));
        // Do another request to confirm that the discarded connection was not pooled.
        assertContent("A", getResponse(newRequest("/")));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// Connection is not pooled.

    }

    @Test
    public void setChunkedStreamingMode() throws Exception {
        server.enqueue(new MockResponse());
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(URLConnectionTest.TransferKind.CHUNKED.newRequestBody("ABCDEFGHIJKLMNOPQ")).build());
        Assert.assertEquals(200, response.code());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("ABCDEFGHIJKLMNOPQ", request.getBody().readUtf8());
        Assert.assertEquals(Arrays.asList("ABCDEFGHIJKLMNOPQ".length()), request.getChunkSizes());
    }

    @Test
    public void authenticateWithFixedLengthStreaming() throws Exception {
        testAuthenticateWithStreamingPost(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void authenticateWithChunkedStreaming() throws Exception {
        testAuthenticateWithStreamingPost(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void postBodyRetransmittedAfterAuthorizationFail() throws Exception {
        postBodyRetransmittedAfterAuthorizationFail("abc");
    }

    @Test
    public void postBodyRetransmittedAfterAuthorizationFail_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        postBodyRetransmittedAfterAuthorizationFail("abc");
    }

    /**
     * Don't explode when resending an empty post. https://github.com/square/okhttp/issues/1131
     */
    @Test
    public void postEmptyBodyRetransmittedAfterAuthorizationFail() throws Exception {
        postBodyRetransmittedAfterAuthorizationFail("");
    }

    @Test
    public void postEmptyBodyRetransmittedAfterAuthorizationFail_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        postBodyRetransmittedAfterAuthorizationFail("");
    }

    @Test
    public void nonStandardAuthenticationScheme() throws Exception {
        List<String> calls = authCallsForHeader("WWW-Authenticate: Foo");
        Assert.assertEquals(Collections.<String>emptyList(), calls);
    }

    @Test
    public void nonStandardAuthenticationSchemeWithRealm() throws Exception {
        List<String> calls = authCallsForHeader("WWW-Authenticate: Foo realm=\"Bar\"");
        Assert.assertEquals(0, calls.size());
    }

    // Digest auth is currently unsupported. Test that digest requests should fail reasonably.
    // http://code.google.com/p/android/issues/detail?id=11140
    @Test
    public void digestAuthentication() throws Exception {
        List<String> calls = authCallsForHeader(("WWW-Authenticate: Digest " + (("realm=\"testrealm@host.com\", qop=\"auth,auth-int\", " + "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", ") + "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"")));
        Assert.assertEquals(0, calls.size());
    }

    @Test
    public void allAttributesSetInServerAuthenticationCallbacks() throws Exception {
        List<String> calls = authCallsForHeader("WWW-Authenticate: Basic realm=\"Bar\"");
        Assert.assertEquals(1, calls.size());
        URL url = server.url("/").url();
        String call = calls.get(0);
        Assert.assertTrue(call, call.contains(("host=" + (url.getHost()))));
        Assert.assertTrue(call, call.contains(("port=" + (url.getPort()))));
        Assert.assertTrue(call, call.contains(("site=" + (url.getHost()))));
        Assert.assertTrue(call, call.contains(("url=" + url)));
        Assert.assertTrue(call, call.contains(("type=" + (SERVER))));
        Assert.assertTrue(call, call.contains("prompt=Bar"));
        Assert.assertTrue(call, call.contains("protocol=http"));
        Assert.assertTrue(call, call.toLowerCase(Locale.US).contains("scheme=basic"));// lowercase for the RI.

    }

    @Test
    public void allAttributesSetInProxyAuthenticationCallbacks() throws Exception {
        List<String> calls = authCallsForHeader("Proxy-Authenticate: Basic realm=\"Bar\"");
        Assert.assertEquals(1, calls.size());
        URL url = server.url("/").url();
        String call = calls.get(0);
        Assert.assertTrue(call, call.contains(("host=" + (url.getHost()))));
        Assert.assertTrue(call, call.contains(("port=" + (url.getPort()))));
        Assert.assertTrue(call, call.contains(("site=" + (url.getHost()))));
        Assert.assertTrue(call, call.contains("url=http://android.com"));
        Assert.assertTrue(call, call.contains(("type=" + (PROXY))));
        Assert.assertTrue(call, call.contains("prompt=Bar"));
        Assert.assertTrue(call, call.contains("protocol=http"));
        Assert.assertTrue(call, call.toLowerCase(Locale.US).contains("scheme=basic"));// lowercase for the RI.

    }

    @Test
    public void setValidRequestMethod() {
        assertMethodForbidsRequestBody("GET");
        assertMethodPermitsRequestBody("DELETE");
        assertMethodForbidsRequestBody("HEAD");
        assertMethodPermitsRequestBody("OPTIONS");
        assertMethodPermitsRequestBody("POST");
        assertMethodPermitsRequestBody("PUT");
        assertMethodPermitsRequestBody("TRACE");
        assertMethodPermitsRequestBody("PATCH");
        assertMethodPermitsNoRequestBody("GET");
        assertMethodPermitsNoRequestBody("DELETE");
        assertMethodPermitsNoRequestBody("HEAD");
        assertMethodPermitsNoRequestBody("OPTIONS");
        assertMethodForbidsNoRequestBody("POST");
        assertMethodForbidsNoRequestBody("PUT");
        assertMethodPermitsNoRequestBody("TRACE");
        assertMethodForbidsNoRequestBody("PATCH");
    }

    @Test
    public void setInvalidRequestMethodLowercase() throws Exception {
        assertValidRequestMethod("get");
    }

    @Test
    public void setInvalidRequestMethodConnect() throws Exception {
        assertValidRequestMethod("CONNECT");
    }

    @Test
    public void shoutcast() throws Exception {
        server.enqueue(new MockResponse().setStatus("ICY 200 OK").addHeader("Accept-Ranges: none").addHeader("Content-Type: audio/mpeg").addHeader("icy-br:128").addHeader("ice-audio-info: bitrate=128;samplerate=44100;channels=2").addHeader("icy-br:128").addHeader("icy-description:Rock").addHeader("icy-genre:riders").addHeader("icy-name:A2RRock").addHeader("icy-pub:1").addHeader("icy-url:http://www.A2Rradio.com").addHeader("Server: Icecast 2.3.3-kh8").addHeader("Cache-Control: no-cache").addHeader("Pragma: no-cache").addHeader("Expires: Mon, 26 Jul 1997 05:00:00 GMT").addHeader("icy-metaint:16000").setBody("mp3 data"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("OK", response.message());
        assertContent("mp3 data", response);
    }

    @Test
    public void secureFixedLengthStreaming() throws Exception {
        testSecureStreamingPost(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void secureChunkedStreaming() throws Exception {
        testSecureStreamingPost(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void authenticateWithPost() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate.");
        // Fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time.
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        Authenticator.setDefault(new RecordingAuthenticator());
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(RequestBody.create(null, "ABCD")).build());
        Assert.assertEquals("Successful auth!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        // No authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        Assert.assertNull(request.getHeader("Authorization"));
        // ...but the three requests that follow include an authorization header.
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            Assert.assertEquals("POST / HTTP/1.1", request.getRequestLine());
            Assert.assertEquals(("Basic " + (RecordingAuthenticator.BASE_64_CREDENTIALS)), request.getHeader("Authorization"));
            Assert.assertEquals("ABCD", request.getBody().readUtf8());
        }
    }

    @Test
    public void authenticateWithGet() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate.");
        // Fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time.
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        Authenticator.setDefault(new RecordingAuthenticator());
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("Successful auth!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        // No authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        Assert.assertNull(request.getHeader("Authorization"));
        // ...but the three requests that follow requests include an authorization header.
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            Assert.assertEquals("GET / HTTP/1.1", request.getRequestLine());
            Assert.assertEquals(("Basic " + (RecordingAuthenticator.BASE_64_CREDENTIALS)), request.getHeader("Authorization"));
        }
    }

    @Test
    public void authenticateWithCharset() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\", charset=\"UTF-8\"").setBody("Please authenticate with UTF-8."));
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate with ISO-8859-1."));
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        Authenticator.setDefault(new RecordingAuthenticator(new PasswordAuthentication("username", "m?torhead".toCharArray())));
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("Successful auth!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        // No authorization header for the first request...
        RecordedRequest request1 = server.takeRequest();
        Assert.assertNull(request1.getHeader("Authorization"));
        // UTF-8 encoding for the first credential.
        RecordedRequest request2 = server.takeRequest();
        Assert.assertEquals("Basic dXNlcm5hbWU6bcO2dG9yaGVhZA==", request2.getHeader("Authorization"));
        // ISO-8859-1 encoding for the second credential.
        RecordedRequest request3 = server.takeRequest();
        Assert.assertEquals("Basic dXNlcm5hbWU6bfZ0b3JoZWFk", request3.getHeader("Authorization"));
    }

    /**
     * https://code.google.com/p/android/issues/detail?id=74026
     */
    @Test
    public void authenticateWithGetAndTransparentGzip() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate.");
        // Fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time.
        MockResponse successfulResponse = new MockResponse().addHeader("Content-Encoding", "gzip").setBody(gzip("Successful auth!"));
        server.enqueue(successfulResponse);
        Authenticator.setDefault(new RecordingAuthenticator());
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("Successful auth!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        // no authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        Assert.assertNull(request.getHeader("Authorization"));
        // ...but the three requests that follow requests include an authorization header
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            Assert.assertEquals("GET / HTTP/1.1", request.getRequestLine());
            Assert.assertEquals(("Basic " + (RecordingAuthenticator.BASE_64_CREDENTIALS)), request.getHeader("Authorization"));
        }
    }

    /**
     * https://github.com/square/okhttp/issues/342
     */
    @Test
    public void authenticateRealmUppercase() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("wWw-aUtHeNtIcAtE: bAsIc rEaLm=\"pRoTeCtEd aReA\"").setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        Authenticator.setDefault(new RecordingAuthenticator());
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("Successful auth!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
    }

    @Test
    public void redirectedWithChunkedEncoding() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.CHUNKED, true);
    }

    @Test
    public void redirectedWithContentLengthHeader() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.FIXED_LENGTH, true);
    }

    @Test
    public void redirectedWithNoLengthHeaders() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.END_OF_STREAM, false);
    }

    @Test
    public void redirectedOnHttps() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("This is the new location!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest first = server.takeRequest();
        Assert.assertEquals("GET / HTTP/1.1", first.getRequestLine());
        RecordedRequest retry = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", retry.getRequestLine());
        Assert.assertEquals("Expected connection reuse", 1, retry.getSequenceNumber());
    }

    @Test
    public void notRedirectedFromHttpsToHttp() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: http://anyhost/foo").setBody("This page has moved!"));
        client = client.newBuilder().followSslRedirects(false).sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("This page has moved!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
    }

    @Test
    public void notRedirectedFromHttpToHttps() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: https://anyhost/foo").setBody("This page has moved!"));
        client = client.newBuilder().followSslRedirects(false).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("This page has moved!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
    }

    @Test
    public void redirectedFromHttpsToHttpFollowingProtocolRedirects() throws Exception {
        server2.enqueue(new MockResponse().setBody("This is insecure HTTP!"));
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server2.url("/").url()))).setBody("This page has moved!"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).followSslRedirects(true).build();
        Response response = getResponse(newRequest("/"));
        assertContent("This is insecure HTTP!", response);
        Assert.assertNull(response.handshake());
    }

    @Test
    public void redirectedFromHttpToHttpsFollowingProtocolRedirects() throws Exception {
        server2.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server2.enqueue(new MockResponse().setBody("This is secure HTTPS!"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server2.url("/").url()))).setBody("This page has moved!"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).followSslRedirects(true).build();
        Response response = getResponse(newRequest("/"));
        assertContent("This is secure HTTPS!", response);
    }

    @Test
    public void redirectToAnotherOriginServer() throws Exception {
        redirectToAnotherOriginServer(false);
    }

    @Test
    public void redirectToAnotherOriginServerWithHttps() throws Exception {
        redirectToAnotherOriginServer(true);
    }

    @Test
    public void redirectWithProxySelector() throws Exception {
        final List<URI> proxySelectionRequests = new ArrayList<>();
        client = client.newBuilder().proxySelector(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                proxySelectionRequests.add(uri);
                MockWebServer proxyServer = ((uri.getPort()) == (server.getPort())) ? server : server2;
                return Arrays.asList(proxyServer.toProxyAddress());
            }

            @Override
            public void connectFailed(URI uri, SocketAddress address, IOException failure) {
                throw new AssertionError();
            }
        }).build();
        server2.enqueue(new MockResponse().setBody("This is the 2nd server!"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server2.url("/b").toString()))).setBody("This page has moved!"));
        assertContent("This is the 2nd server!", getResponse(newRequest("/a")));
        Assert.assertEquals(Arrays.asList(server.url("/").url().toURI(), server2.url("/").url().toURI()), proxySelectionRequests);
    }

    @Test
    public void redirectWithAuthentication() throws Exception {
        server2.enqueue(new MockResponse().setBody("Page 2"));
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setResponseCode(302).addHeader(("Location: " + (server2.url("/b")))));
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(Credentials.basic("jesse", "secret"), null)).build();
        assertContent("Page 2", getResponse(newRequest("/a")));
        RecordedRequest redirectRequest = server2.takeRequest();
        Assert.assertNull(redirectRequest.getHeader("Authorization"));
        Assert.assertEquals("/b", redirectRequest.getPath());
    }

    @Test
    public void response300MultipleChoiceWithPost() throws Exception {
        // Chrome doesn't follow the redirect, but Firefox and the RI both do
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MULT_CHOICE, URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void response301MovedPermanentlyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_PERM, URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void response302MovedTemporarilyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_TEMP, URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void response303SeeOtherWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_SEE_OTHER, URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void postRedirectToGetWithChunkedRequest() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_TEMP, URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void postRedirectToGetWithStreamedRequest() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_TEMP, URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void redirectedPostStripsRequestBodyHeaders() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /page2"));
        server.enqueue(new MockResponse().setBody("Page 2"));
        Response response = getResponse(new Request.Builder().url(server.url("/page1")).post(RequestBody.create(MediaType.get("text/plain; charset=utf-8"), "ABCD")).header("Transfer-Encoding", "identity").build());
        Assert.assertEquals("Page 2", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        Assert.assertEquals("POST /page1 HTTP/1.1", server.takeRequest().getRequestLine());
        RecordedRequest page2 = server.takeRequest();
        Assert.assertEquals("GET /page2 HTTP/1.1", page2.getRequestLine());
        Assert.assertNull(page2.getHeader("Content-Length"));
        Assert.assertNull(page2.getHeader("Content-Type"));
        Assert.assertNull(page2.getHeader("Transfer-Encoding"));
    }

    @Test
    public void response305UseProxy() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_USE_PROXY).addHeader(("Location: " + (server.url("/").url()))).setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Proxy Response"));
        Response response = getResponse(newRequest("/foo"));
        // Fails on the RI, which gets "Proxy Response".
        Assert.assertEquals("This page has moved!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest page1 = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", page1.getRequestLine());
        Assert.assertEquals(1, server.getRequestCount());
    }

    @Test
    public void response307WithGet() throws Exception {
        testRedirect(true, "GET");
    }

    @Test
    public void response307WithHead() throws Exception {
        testRedirect(true, "HEAD");
    }

    @Test
    public void response307WithOptions() throws Exception {
        testRedirect(true, "OPTIONS");
    }

    @Test
    public void response307WithPost() throws Exception {
        testRedirect(true, "POST");
    }

    @Test
    public void response308WithGet() throws Exception {
        testRedirect(false, "GET");
    }

    @Test
    public void response308WithHead() throws Exception {
        testRedirect(false, "HEAD");
    }

    @Test
    public void response308WithOptions() throws Exception {
        testRedirect(false, "OPTIONS");
    }

    @Test
    public void response308WithPost() throws Exception {
        testRedirect(false, "POST");
    }

    @Test
    public void follow20Redirects() throws Exception {
        for (int i = 0; i < 20; i++) {
            server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        server.enqueue(new MockResponse().setBody("Success!"));
        Response response = getResponse(newRequest("/0"));
        assertContent("Success!", response);
        Assert.assertEquals(server.url("/20"), response.request().url());
    }

    @Test
    public void doesNotFollow21Redirects() throws Exception {
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        try {
            getResponse(newRequest("/0"));
            Assert.fail();
        } catch (ProtocolException expected) {
            Assert.assertEquals("Too many follow-up requests: 21", expected.getMessage());
        }
    }

    @Test
    public void httpsWithCustomTrustManager() throws Exception {
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        URLConnectionTest.RecordingTrustManager trustManager = new URLConnectionTest.RecordingTrustManager(handshakeCertificates.trustManager());
        SSLContext sslContext = Platform.get().getSSLContext();
        sslContext.init(null, new TrustManager[]{ trustManager }, null);
        client = client.newBuilder().hostnameVerifier(hostnameVerifier).sslSocketFactory(sslContext.getSocketFactory(), trustManager).build();
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.enqueue(new MockResponse().setBody("GHI"));
        assertContent("ABC", getResponse(newRequest("/")));
        assertContent("DEF", getResponse(newRequest("/")));
        assertContent("GHI", getResponse(newRequest("/")));
        Assert.assertEquals(Arrays.asList(("verify " + (server.getHostName()))), hostnameVerifier.calls);
        Assert.assertEquals(Arrays.asList("checkServerTrusted [CN=localhost 1]"), trustManager.calls);
    }

    @Test
    public void getClientRequestTimeout() throws Exception {
        enqueueClientRequestTimeoutResponses();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("Body", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
    }

    @Test
    public void bufferedBodyWithClientRequestTimeout() throws Exception {
        enqueueClientRequestTimeoutResponses();
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(RequestBody.create(null, "Hello")).build());
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("Body", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest request1 = server.takeRequest();
        Assert.assertEquals("Hello", request1.getBody().readUtf8());
        RecordedRequest request2 = server.takeRequest();
        Assert.assertEquals("Hello", request2.getBody().readUtf8());
    }

    @Test
    public void streamedBodyWithClientRequestTimeout() throws Exception {
        enqueueClientRequestTimeoutResponses();
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(URLConnectionTest.TransferKind.CHUNKED.newRequestBody("Hello")).build());
        Assert.assertEquals(200, response.code());
        assertContent("Body", response);
        response.close();
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void readTimeouts() throws IOException {
        // This relies on the fact that MockWebServer doesn't close the
        // connection after a response has been sent. This causes the client to
        // try to read more bytes than are sent, which results in a timeout.
        server.enqueue(new MockResponse().setBody("ABC").clearHeaders().addHeader("Content-Length: 4"));
        server.enqueue(new MockResponse().setBody("unused"));// to keep the server alive

        Response response = getResponse(newRequest("/"));
        BufferedSource in = response.body().source();
        in.timeout().timeout(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals('A', in.readByte());
        Assert.assertEquals('B', in.readByte());
        Assert.assertEquals('C', in.readByte());
        try {
            in.readByte();// If Content-Length was accurate, this would return -1 immediately.

            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
        in.close();
    }

    /**
     * Confirm that an unacknowledged write times out.
     */
    @Test
    public void writeTimeouts() throws IOException {
        MockWebServer server = new MockWebServer();
        // Sockets on some platforms can have large buffers that mean writes do not block when
        // required. These socket factories explicitly set the buffer sizes on sockets created.
        final int SOCKET_BUFFER_SIZE = 4 * 1024;
        server.setServerSocketFactory(new DelegatingServerSocketFactory(ServerSocketFactory.getDefault()) {
            @Override
            protected ServerSocket configureServerSocket(ServerSocket serverSocket) throws IOException {
                serverSocket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
                return serverSocket;
            }
        });
        client = client.newBuilder().socketFactory(new DelegatingSocketFactory(SocketFactory.getDefault()) {
            @Override
            protected Socket configureSocket(Socket socket) throws IOException {
                socket.setReceiveBufferSize(SOCKET_BUFFER_SIZE);
                socket.setSendBufferSize(SOCKET_BUFFER_SIZE);
                return socket;
            }
        }).writeTimeout(500, TimeUnit.MILLISECONDS).build();
        server.start();
        server.enqueue(new MockResponse().throttleBody(1, 1, TimeUnit.SECONDS));// Prevent the server from reading!

        Request request = new Request.Builder().url(server.url("/")).post(new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                byte[] data = new byte[(2 * 1024) * 1024];// 2 MiB.

                sink.write(data);
            }
        }).build();
        try {
            getResponse(request);
            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    @Test
    public void setChunkedEncodingAsRequestProperty() throws Exception {
        server.enqueue(new MockResponse());
        Response response = getResponse(new Request.Builder().url(server.url("/")).header("Transfer-encoding", "chunked").post(URLConnectionTest.TransferKind.CHUNKED.newRequestBody("ABC")).build());
        Assert.assertEquals(200, response.code());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("ABC", request.getBody().readUtf8());
    }

    @Test
    public void connectionCloseInRequest() throws Exception {
        server.enqueue(new MockResponse());// Server doesn't honor the connection: close header!

        server.enqueue(new MockResponse());
        Response a = getResponse(new Request.Builder().url(server.url("/")).header("Connection", "close").build());
        Assert.assertEquals(200, a.code());
        Response b = getResponse(newRequest("/"));
        Assert.assertEquals(200, b.code());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void connectionCloseInResponse() throws Exception {
        server.enqueue(new MockResponse().addHeader("Connection: close"));
        server.enqueue(new MockResponse());
        Response a = getResponse(newRequest("/"));
        Assert.assertEquals(200, a.code());
        Response b = getResponse(newRequest("/"));
        Assert.assertEquals(200, b.code());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void connectionCloseWithRedirect() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").addHeader("Connection: close"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("This is the new location!", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    /**
     * Retry redirects if the socket is closed.
     * https://code.google.com/p/android/issues/detail?id=41576
     */
    @Test
    public void sameConnectionRedirectAndReuse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setSocketPolicy(SHUTDOWN_INPUT_AT_END).addHeader("Location: /foo"));
        server.enqueue(new MockResponse().setBody("This is the new page!"));
        assertContent("This is the new page!", getResponse(newRequest("/")));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void responseCodeDisagreesWithHeaders() {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT).setBody("This body is not allowed!"));
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("HTTP 204 had non-zero Content-Length: 25", expected.getMessage());
        }
    }

    @Test
    public void singleByteReadIsSigned() throws IOException {
        server.enqueue(new MockResponse().setBody(new Buffer().writeByte((-2)).writeByte((-1))));
        Response response = getResponse(newRequest("/"));
        InputStream in = response.body().byteStream();
        Assert.assertEquals(254, in.read());
        Assert.assertEquals(255, in.read());
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void flushAfterStreamTransmittedWithChunkedEncoding() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void flushAfterStreamTransmittedWithFixedLength() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void flushAfterStreamTransmittedWithNoLengthHeaders() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void getHeadersThrows() {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void dnsFailureThrowsIOException() {
        client = client.newBuilder().dns(new FakeDns()).build();
        try {
            getResponse(newRequest(HttpUrl.get("http://host.unlikelytld")));
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void malformedUrlThrowsUnknownHostException() throws IOException {
        try {
            getResponse(newRequest(HttpUrl.get("http://./foo.html")));
            Assert.fail();
        } catch (UnknownHostException expected) {
        }
    }

    @Test
    public void getKeepAlive() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        // The request should work once and then fail.
        Response connection1 = getResponse(newRequest("/"));
        BufferedSource source1 = connection1.body().source();
        source1.timeout().timeout(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals("ABC", readAscii(source1.inputStream(), Integer.MAX_VALUE));
        server.shutdown();
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (ConnectException expected) {
        }
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=14562
     */
    @Test
    public void readAfterLastByte() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC").clearHeaders().addHeader("Connection: close").setSocketPolicy(DISCONNECT_AT_END));
        Response response = getResponse(newRequest("/"));
        InputStream in = response.body().byteStream();
        Assert.assertEquals("ABC", readAscii(in, 3));
        Assert.assertEquals((-1), in.read());
        Assert.assertEquals((-1), in.read());// throws IOException in Gingerbread.

    }

    @Test
    public void getOutputStreamOnGetFails() {
        try {
            new Request.Builder().url(server.url("/")).method("GET", RequestBody.create(null, "abc")).build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void clientSendsContentLength() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(RequestBody.create(null, "ABC")).build());
        Assert.assertEquals("A", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("3", request.getHeader("Content-Length"));
        response.body().close();
    }

    @Test
    public void getContentLengthConnects() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(3L, response.body().contentLength());
        response.body().close();
    }

    @Test
    public void getContentTypeConnects() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Type: text/plain").setBody("ABC"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(MediaType.get("text/plain"), response.body().contentType());
        response.body().close();
    }

    @Test
    public void getContentEncodingConnects() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Encoding: identity").setBody("ABC"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals("identity", response.header("Content-Encoding"));
        response.body().close();
    }

    @Test
    public void urlContainsQueryButNoPath() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        HttpUrl url = server.url("?query");
        Response response = getResponse(newRequest(url));
        Assert.assertEquals("A", readAscii(response.body().byteStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("GET /?query HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void doOutputForMethodThatDoesntSupportOutput() {
        try {
            new Request.Builder().url(server.url("/")).method("HEAD", RequestBody.create(null, "")).build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    // http://code.google.com/p/android/issues/detail?id=20442
    @Test
    public void inputStreamAvailableWithChunkedEncoding() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.CHUNKED);
    }

    @Test
    public void inputStreamAvailableWithContentLengthHeader() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    @Test
    public void inputStreamAvailableWithNoLengthHeaders() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    @Test
    public void postFailsWithBufferedRequestForSmallRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.END_OF_STREAM, 1024);
    }

    @Test
    public void postFailsWithBufferedRequestForLargeRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.END_OF_STREAM, 16384);
    }

    @Test
    public void postFailsWithChunkedRequestForSmallRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.CHUNKED, 1024);
    }

    @Test
    public void postFailsWithChunkedRequestForLargeRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.CHUNKED, 16384);
    }

    @Test
    public void postFailsWithFixedLengthRequestForSmallRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.FIXED_LENGTH, 1024);
    }

    @Test
    public void postFailsWithFixedLengthRequestForLargeRequest() throws Exception {
        reusedConnectionFailsWithPost(URLConnectionTest.TransferKind.FIXED_LENGTH, 16384);
    }

    @Test
    public void postBodyRetransmittedOnFailureRecovery() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));
        server.enqueue(new MockResponse().setBody("def"));
        // Seed the connection pool so we have something that can fail.
        assertContent("abc", getResponse(newRequest("/")));
        Response post = getResponse(new Request.Builder().url(server.url("/")).post(RequestBody.create(null, "body!")).build());
        assertContent("def", post);
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals(0, get.getSequenceNumber());
        RecordedRequest post1 = server.takeRequest();
        Assert.assertEquals("body!", post1.getBody().readUtf8());
        Assert.assertEquals(1, post1.getSequenceNumber());
        RecordedRequest post2 = server.takeRequest();
        Assert.assertEquals("body!", post2.getBody().readUtf8());
        Assert.assertEquals(0, post2.getSequenceNumber());
    }

    @Test
    public void fullyBufferedPostIsTooShort() {
        server.enqueue(new MockResponse().setBody("A"));
        RequestBody requestBody = new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 4L;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("abc");
            }
        };
        try {
            getResponse(new Request.Builder().url(server.url("/b")).post(requestBody).build());
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void fullyBufferedPostIsTooLong() {
        server.enqueue(new MockResponse().setBody("A"));
        RequestBody requestBody = new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 3L;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("abcd");
            }
        };
        try {
            getResponse(new Request.Builder().url(server.url("/b")).post(requestBody).build());
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void emptyRequestHeaderValueIsAllowed() throws Exception {
        server.enqueue(new MockResponse().setBody("body"));
        Response response = getResponse(new Request.Builder().url(server.url("/")).header("B", "").build());
        assertContent("body", response);
        Assert.assertEquals("", response.request().header("B"));
    }

    @Test
    public void emptyResponseHeaderValueIsAllowed() throws Exception {
        server.enqueue(new MockResponse().addHeader("A:").setBody("body"));
        Response response = getResponse(newRequest("/"));
        assertContent("body", response);
        Assert.assertEquals("", response.header("A"));
    }

    @Test
    public void emptyRequestHeaderNameIsStrict() {
        try {
            new Request.Builder().url(server.url("/")).header("", "A").build();
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void emptyResponseHeaderNameIsLenient() throws Exception {
        Headers.Builder headers = new Headers.Builder();
        instance.addLenient(headers, ":A");
        server.enqueue(new MockResponse().setHeaders(headers.build()).setBody("body"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("A", response.header(""));
        response.body().close();
    }

    @Test
    public void requestHeaderValidationIsStrict() {
        try {
            new Request.Builder().addHeader("a\tb", "Value");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            new Request.Builder().addHeader("Name", "c\u007fd");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            new Request.Builder().addHeader("", "Value");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            new Request.Builder().addHeader("\ud83c\udf69", "Value");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            new Request.Builder().addHeader("Name", "\u2615\ufe0f");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void responseHeaderParsingIsLenient() throws Exception {
        Headers headers = new Headers.Builder().add("Content-Length", "0").addLenient("a\tb: c\u007fd").addLenient(": ef").addLenient("\ud83c\udf69: \u2615\ufe0f").build();
        server.enqueue(new MockResponse().setHeaders(headers));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("c\u007fd", response.header("a\tb"));
        Assert.assertEquals("\u2615\ufe0f", response.header("\ud83c\udf69"));
        Assert.assertEquals("ef", response.header(""));
    }

    @Test
    public void customBasicAuthenticator() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("A"));
        String credential = Credentials.basic("jesse", "peanutbutter");
        RecordingOkAuthenticator authenticator = new RecordingOkAuthenticator(credential, null);
        client = client.newBuilder().authenticator(authenticator).build();
        assertContent("A", getResponse(newRequest("/private")));
        Assert.assertNull(server.takeRequest().getHeader("Authorization"));
        Assert.assertEquals(credential, server.takeRequest().getHeader("Authorization"));
        Assert.assertEquals(Proxy.NO_PROXY, authenticator.onlyRoute().proxy());
        Response response = authenticator.onlyResponse();
        Assert.assertEquals("/private", response.request().url().url().getPath());
        Assert.assertEquals(Arrays.asList(new Challenge("Basic", "protected area")), response.challenges());
    }

    @Test
    public void customTokenAuthenticator() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Bearer realm=\"oauthed\"").setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("A"));
        RecordingOkAuthenticator authenticator = new RecordingOkAuthenticator("oauthed abc123", "Bearer");
        client = client.newBuilder().authenticator(authenticator).build();
        assertContent("A", getResponse(newRequest("/private")));
        Assert.assertNull(server.takeRequest().getHeader("Authorization"));
        Assert.assertEquals("oauthed abc123", server.takeRequest().getHeader("Authorization"));
        Response response = authenticator.onlyResponse();
        Assert.assertEquals("/private", response.request().url().url().getPath());
        Assert.assertEquals(Arrays.asList(new Challenge("Bearer", "oauthed")), response.challenges());
    }

    @Test
    public void authenticateCallsTrackedAsRedirects() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(302).addHeader("Location: /b"));
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\""));
        server.enqueue(new MockResponse().setBody("c"));
        RecordingOkAuthenticator authenticator = new RecordingOkAuthenticator(Credentials.basic("jesse", "peanutbutter"), "Basic");
        client = client.newBuilder().authenticator(authenticator).build();
        assertContent("c", getResponse(newRequest("/a")));
        Response challengeResponse = authenticator.responses.get(0);
        Assert.assertEquals("/b", challengeResponse.request().url().url().getPath());
        Response redirectedBy = challengeResponse.priorResponse();
        Assert.assertEquals("/a", redirectedBy.request().url().url().getPath());
    }

    @Test
    public void attemptAuthorization20Times() throws Exception {
        for (int i = 0; i < 20; i++) {
            server.enqueue(new MockResponse().setResponseCode(401));
        }
        server.enqueue(new MockResponse().setBody("Success!"));
        String credential = Credentials.basic("jesse", "peanutbutter");
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(credential, null)).build();
        Response response = getResponse(newRequest("/0"));
        assertContent("Success!", response);
    }

    @Test
    public void doesNotAttemptAuthorization21Times() throws Exception {
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(401));
        }
        String credential = Credentials.basic("jesse", "peanutbutter");
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(credential, null)).build();
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (ProtocolException expected) {
            Assert.assertEquals("Too many follow-up requests: 21", expected.getMessage());
        }
    }

    @Test
    public void setsNegotiatedProtocolHeader_HTTP_2() throws Exception {
        setsNegotiatedProtocolHeader(HTTP_2);
    }

    @Test
    public void http10SelectedProtocol() throws IOException {
        server.enqueue(new MockResponse().setStatus("HTTP/1.0 200 OK"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(HTTP_1_0, response.protocol());
    }

    @Test
    public void http11SelectedProtocol() throws IOException {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 200 OK"));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(HTTP_1_1, response.protocol());
    }

    /**
     * For example, empty Protobuf RPC messages end up as a zero-length POST.
     */
    @Test
    public void zeroLengthPost() throws Exception {
        zeroLengthPayload("POST");
    }

    @Test
    public void zeroLengthPost_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        zeroLengthPost();
    }

    /**
     * For example, creating an Amazon S3 bucket ends up as a zero-length POST.
     */
    @Test
    public void zeroLengthPut() throws Exception {
        zeroLengthPayload("PUT");
    }

    @Test
    public void zeroLengthPut_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        zeroLengthPut();
    }

    @Test
    public void setProtocols() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        client = client.newBuilder().protocols(Arrays.asList(HTTP_1_1)).build();
        assertContent("A", getResponse(newRequest("/")));
    }

    @Test
    public void setProtocolsWithoutHttp11() {
        try {
            new OkHttpClient.Builder().protocols(Arrays.asList(HTTP_2));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void setProtocolsWithNull() {
        try {
            new OkHttpClient.Builder().protocols(Arrays.asList(HTTP_1_1, null));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void veryLargeFixedLengthRequest() throws Exception {
        server.setBodyLimit(0);
        server.enqueue(new MockResponse());
        long contentLength = (Integer.MAX_VALUE) + 1L;
        Response response = getResponse(new Request.Builder().url(server.url("/")).post(new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return contentLength;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                byte[] buffer = new byte[1024 * 1024];
                for (long bytesWritten = 0; bytesWritten < contentLength;) {
                    int byteCount = ((int) (Math.min(buffer.length, (contentLength - bytesWritten))));
                    bytesWritten += byteCount;
                    sink.write(buffer, 0, byteCount);
                }
            }
        }).build());
        assertContent("", response);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals(Long.toString(contentLength), request.getHeader("Content-Length"));
    }

    @Test
    public void testNoSslFallback() throws Exception {
        /* tunnelProxy */
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("Response that would have needed fallbacks"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).build();
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (SSLProtocolException expected) {
            // RI response to the FAIL_HANDSHAKE
        } catch (SSLHandshakeException expected) {
            // Android's response to the FAIL_HANDSHAKE
        } catch (SSLException expected) {
            // JDK 1.9 response to the FAIL_HANDSHAKE
            // javax.net.ssl.SSLException: Unexpected handshake message: client_hello
        } catch (SocketException expected) {
            // Conscrypt's response to the FAIL_HANDSHAKE
        }
    }

    /**
     * We had a bug where we attempted to gunzip responses that didn't have a body. This only came up
     * with 304s since that response code can include headers (like "Content-Encoding") without any
     * content to go along with it. https://github.com/square/okhttp/issues/358
     */
    @Test
    public void noTransparentGzipFor304NotModified() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED).addHeader("Content-Encoding: gzip"));
        server.enqueue(new MockResponse().setBody("b"));
        Response response1 = getResponse(newRequest("/"));
        Assert.assertEquals(HttpURLConnection.HTTP_NOT_MODIFIED, response1.code());
        assertContent("", response1);
        Response response2 = getResponse(newRequest("/"));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response2.code());
        assertContent("b", response2);
        RecordedRequest requestA = server.takeRequest();
        Assert.assertEquals(0, requestA.getSequenceNumber());
        RecordedRequest requestB = server.takeRequest();
        Assert.assertEquals(1, requestB.getSequenceNumber());
    }

    @Test
    public void nullSSLSocketFactory_throws() {
        try {
            client.newBuilder().sslSocketFactory(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    /**
     * We had a bug where we weren't closing Gzip streams on redirects.
     * https://github.com/square/okhttp/issues/441
     */
    @Test
    public void gzipWithRedirectAndConnectionReuse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").addHeader("Content-Encoding: gzip").setBody(gzip("Moved! Moved! Moved!")));
        server.enqueue(new MockResponse().setBody("This is the new page!"));
        Response response = getResponse(newRequest("/"));
        assertContent("This is the new page!", response);
        RecordedRequest requestA = server.takeRequest();
        Assert.assertEquals(0, requestA.getSequenceNumber());
        RecordedRequest requestB = server.takeRequest();
        Assert.assertEquals(1, requestB.getSequenceNumber());
    }

    /**
     * The RFC is unclear in this regard as it only specifies that this should invalidate the cache
     * entry (if any).
     */
    @Test
    public void bodyPermittedOnDelete() throws Exception {
        server.enqueue(new MockResponse());
        Response response = getResponse(new Request.Builder().url(server.url("/")).delete(RequestBody.create(null, "BODY")).build());
        Assert.assertEquals(200, response.code());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("DELETE", request.getMethod());
        Assert.assertEquals("BODY", request.getBody().readUtf8());
    }

    @Test
    public void userAgentDefaultsToOkHttpVersion() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        assertContent("abc", getResponse(newRequest("/")));
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals(Version.userAgent(), request.getHeader("User-Agent"));
    }

    @Test
    public void urlWithSpaceInHost() {
        try {
            HttpUrl.get("http://and roid.com/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void urlWithSpaceInHostViaHttpProxy() {
        try {
            HttpUrl.get("http://and roid.com/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void urlHostWithNul() {
        try {
            HttpUrl.get("http://host\u0000/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void urlRedirectToHostWithNul() throws Exception {
        String redirectUrl = "http://host\u0000/";
        server.enqueue(new MockResponse().setResponseCode(302).addHeaderLenient("Location", redirectUrl));
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(302, response.code());
        Assert.assertEquals(redirectUrl, response.header("Location"));
    }

    @Test
    public void urlWithBadAsciiHost() {
        try {
            HttpUrl.get("http://host\u0001/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void setSslSocketFactoryFailsOnJdk9() {
        Assume.assumeTrue(getPlatform().equals("jdk9"));
        try {
            client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory());
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    /**
     * Confirm that runtime exceptions thrown inside of OkHttp propagate to the caller.
     */
    @Test
    public void unexpectedExceptionSync() throws Exception {
        client = client.newBuilder().dns(( hostname) -> {
            throw new RuntimeException("boom!");
        }).build();
        server.enqueue(new MockResponse());
        try {
            getResponse(newRequest("/"));
            Assert.fail();
        } catch (RuntimeException expected) {
            Assert.assertEquals("boom!", expected.getMessage());
        }
    }

    @Test
    public void streamedBodyIsRetriedOnHttp2Shutdown() throws Exception {
        enableProtocol(HTTP_2);
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_END).setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        // Send a separate request which will trigger a GOAWAY frame on the healthy connection.
        Response response = getResponse(newRequest("/"));
        assertContent("abc", response);
        // Ensure the GOAWAY frame has time to be read and processed.
        Thread.sleep(500);
        assertContent("def", getResponse(new Request.Builder().url(server.url("/")).post(RequestBody.create(null, "123")).build()));
        RecordedRequest request1 = server.takeRequest();
        Assert.assertEquals(0, request1.getSequenceNumber());
        RecordedRequest request2 = server.takeRequest();
        Assert.assertEquals("123", request2.getBody().readUtf8());
        Assert.assertEquals(0, request2.getSequenceNumber());
    }

    @Test
    public void authenticateNoConnection() throws Exception {
        server.enqueue(new MockResponse().addHeader("Connection: close").setResponseCode(401).setSocketPolicy(SocketPolicy.DISCONNECT_AT_END));
        Authenticator.setDefault(new RecordingAuthenticator(null));
        client = client.newBuilder().authenticator(new JavaNetAuthenticator()).build();
        Response response = getResponse(newRequest("/"));
        Assert.assertEquals(401, response.code());
    }

    enum TransferKind {

        CHUNKED() {
            @Override
            void setBody(MockResponse response, Buffer content, int chunkSize) {
                response.setChunkedBody(content, chunkSize);
            }

            @Override
            RequestBody newRequestBody(String body) {
                return new RequestBody() {
                    @Override
                    public long contentLength() {
                        return -1L;
                    }

                    @Override
                    @Nullable
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.writeUtf8(body);
                    }
                };
            }
        },
        FIXED_LENGTH() {
            @Override
            void setBody(MockResponse response, Buffer content, int chunkSize) {
                response.setBody(content);
            }

            @Override
            RequestBody newRequestBody(String body) {
                return new RequestBody() {
                    @Override
                    public long contentLength() {
                        return Utf8.size(body);
                    }

                    @Override
                    @Nullable
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.writeUtf8(body);
                    }
                };
            }
        },
        END_OF_STREAM() {
            @Override
            void setBody(MockResponse response, Buffer content, int chunkSize) {
                response.setBody(content);
                response.setSocketPolicy(DISCONNECT_AT_END);
                response.removeHeader("Content-Length");
            }

            @Override
            RequestBody newRequestBody(String body) {
                throw new AssumptionViolatedException("END_OF_STREAM not implemented for requests");
            }
        };
        abstract void setBody(MockResponse response, Buffer content, int chunkSize) throws IOException;

        abstract RequestBody newRequestBody(String body);

        void setBody(MockResponse response, String content, int chunkSize) throws IOException {
            setBody(response, new Buffer().writeUtf8(content), chunkSize);
        }
    }

    enum ProxyConfig {

        NO_PROXY() {
            @Override
            public Factory connect(MockWebServer server, OkHttpClient client) {
                return client.newBuilder().proxy(Proxy.NO_PROXY).build();
            }
        },
        CREATE_ARG() {
            @Override
            public Factory connect(MockWebServer server, OkHttpClient client) {
                return client.newBuilder().proxy(server.toProxyAddress()).build();
            }
        },
        PROXY_SYSTEM_PROPERTY() {
            @Override
            public Factory connect(MockWebServer server, OkHttpClient client) {
                System.setProperty("proxyHost", server.getHostName());
                System.setProperty("proxyPort", Integer.toString(server.getPort()));
                return client;
            }
        },
        HTTP_PROXY_SYSTEM_PROPERTY() {
            @Override
            public Factory connect(MockWebServer server, OkHttpClient client) {
                System.setProperty("http.proxyHost", server.getHostName());
                System.setProperty("http.proxyPort", Integer.toString(server.getPort()));
                return client;
            }
        },
        HTTPS_PROXY_SYSTEM_PROPERTY() {
            @Override
            public Factory connect(MockWebServer server, OkHttpClient client) {
                System.setProperty("https.proxyHost", server.getHostName());
                System.setProperty("https.proxyPort", Integer.toString(server.getPort()));
                return client;
            }
        };
        public abstract Factory connect(MockWebServer server, OkHttpClient client) throws IOException;

        public Call connect(MockWebServer server, OkHttpClient client, HttpUrl url) throws IOException {
            Request request = new Request.Builder().url(url).build();
            return connect(server, client).newCall(request);
        }
    }

    private static class RecordingTrustManager implements X509TrustManager {
        private final List<String> calls = new ArrayList<>();

        private final X509TrustManager delegate;

        RecordingTrustManager(X509TrustManager delegate) {
            this.delegate = delegate;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return delegate.getAcceptedIssuers();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            calls.add(("checkClientTrusted " + (certificatesToString(chain))));
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            calls.add(("checkServerTrusted " + (certificatesToString(chain))));
        }

        private String certificatesToString(X509Certificate[] certificates) {
            List<String> result = new ArrayList<>();
            for (X509Certificate certificate : certificates) {
                result.add((((certificate.getSubjectDN()) + " ") + (certificate.getSerialNumber())));
            }
            return result.toString();
        }
    }
}

