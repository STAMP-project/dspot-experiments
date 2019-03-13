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
package libcore.java.net;


import CloseGuard.Reporter;
import SocketPolicy.FAIL_HANDSHAKE;
import SocketPolicy.UPGRADE_TO_SSL_AT_END;
import com.android.okhttp.HttpResponseCache;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;
import dalvik.system.CloseGuard;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ResponseCache;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import junit.framework.TestCase;
import libcore.java.lang.ref.FinalizationTester;
import libcore.java.security.TestKeyStore;
import libcore.javax.net.ssl.TestSSLContext;
import tests.net.StuckServer;

import static java.net.Authenticator.RequestorType.SERVER;


public final class URLConnectionTest extends TestCase {
    private MockWebServer server;

    private HttpResponseCache cache;

    private String hostName;

    public void testRequestHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection urlConnection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        urlConnection.addRequestProperty("D", "e");
        urlConnection.addRequestProperty("D", "f");
        TestCase.assertEquals("f", urlConnection.getRequestProperty("D"));
        TestCase.assertEquals("f", urlConnection.getRequestProperty("d"));
        Map<String, List<String>> requestHeaders = urlConnection.getRequestProperties();
        TestCase.assertEquals(newSet("e", "f"), new HashSet<String>(requestHeaders.get("D")));
        TestCase.assertEquals(newSet("e", "f"), new HashSet<String>(requestHeaders.get("d")));
        try {
            requestHeaders.put("G", Arrays.asList("h"));
            TestCase.fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            requestHeaders.get("D").add("i");
            TestCase.fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            urlConnection.setRequestProperty(null, "j");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            urlConnection.addRequestProperty(null, "k");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        urlConnection.setRequestProperty("NullValue", null);// should fail silently!

        TestCase.assertNull(urlConnection.getRequestProperty("NullValue"));
        urlConnection.addRequestProperty("AnotherNullValue", null);// should fail silently!

        TestCase.assertNull(urlConnection.getRequestProperty("AnotherNullValue"));
        urlConnection.getResponseCode();
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "D: e");
        assertContains(request.getHeaders(), "D: f");
        assertContainsNoneMatching(request.getHeaders(), "NullValue.*");
        assertContainsNoneMatching(request.getHeaders(), "AnotherNullValue.*");
        assertContainsNoneMatching(request.getHeaders(), "G:.*");
        assertContainsNoneMatching(request.getHeaders(), "null:.*");
        try {
            urlConnection.addRequestProperty("N", "o");
            TestCase.fail("Set header after connect");
        } catch (IllegalStateException expected) {
        }
        try {
            urlConnection.setRequestProperty("P", "q");
            TestCase.fail("Set header after connect");
        } catch (IllegalStateException expected) {
        }
        try {
            urlConnection.getRequestProperties();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testGetRequestPropertyReturnsLastValue() throws Exception {
        server.play();
        HttpURLConnection urlConnection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        urlConnection.addRequestProperty("A", "value1");
        urlConnection.addRequestProperty("A", "value2");
        TestCase.assertEquals("value2", urlConnection.getRequestProperty("A"));
    }

    public void testResponseHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setStatus("HTTP/1.0 200 Fantastic").addHeader("A: c").addHeader("B: d").addHeader("A: e").setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8));
        server.play();
        HttpURLConnection urlConnection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals(200, urlConnection.getResponseCode());
        TestCase.assertEquals("Fantastic", urlConnection.getResponseMessage());
        TestCase.assertEquals("HTTP/1.0 200 Fantastic", urlConnection.getHeaderField(null));
        Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
        TestCase.assertEquals(Arrays.asList("HTTP/1.0 200 Fantastic"), responseHeaders.get(null));
        TestCase.assertEquals(newSet("c", "e"), new HashSet<String>(responseHeaders.get("A")));
        TestCase.assertEquals(newSet("c", "e"), new HashSet<String>(responseHeaders.get("a")));
        try {
            responseHeaders.put("N", Arrays.asList("o"));
            TestCase.fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            responseHeaders.get("A").add("f");
            TestCase.fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        TestCase.assertEquals("A", urlConnection.getHeaderFieldKey(0));
        TestCase.assertEquals("c", urlConnection.getHeaderField(0));
        TestCase.assertEquals("B", urlConnection.getHeaderFieldKey(1));
        TestCase.assertEquals("d", urlConnection.getHeaderField(1));
        TestCase.assertEquals("A", urlConnection.getHeaderFieldKey(2));
        TestCase.assertEquals("e", urlConnection.getHeaderField(2));
    }

    public void testGetErrorStreamOnSuccessfulRequest() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertNull(connection.getErrorStream());
    }

    public void testGetErrorStreamOnUnsuccessfulRequest() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("A", readAscii(connection.getErrorStream(), Integer.MAX_VALUE));
    }

    // Check that if we don't read to the end of a response, the next request on the
    // recycled connection doesn't get the unread tail of the first request's response.
    // http://code.google.com/p/android/issues/detail?id=2939
    public void test_2939() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8);
        server.enqueue(response);
        server.enqueue(response);
        server.play();
        assertContent("ABCDE", server.getUrl("/").openConnection(), 5);
        assertContent("ABCDE", server.getUrl("/").openConnection(), 5);
    }

    // Check that we recognize a few basic mime types by extension.
    // http://code.google.com/p/android/issues/detail?id=10100
    public void test_10100() throws Exception {
        TestCase.assertEquals("image/jpeg", URLConnection.guessContentTypeFromName("someFile.jpg"));
        TestCase.assertEquals("application/pdf", URLConnection.guessContentTypeFromName("stuff.pdf"));
    }

    public void testConnectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setBody("ABCDEFGHIJKLMNOPQR");
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/foo").openConnection());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/bar?baz=quux").openConnection());
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/z").openConnection());
        TestCase.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    public void testChunkedConnectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDEFGHIJKLMNOPQR", 5);
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/foo").openConnection());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/bar?baz=quux").openConnection());
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/z").openConnection());
        TestCase.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    /**
     * Test that connections are added to the pool as soon as the response has
     * been consumed.
     */
    public void testConnectionsArePooledWithoutExplicitDisconnect() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.play();
        URLConnection connection1 = server.getUrl("/").openConnection();
        TestCase.assertEquals("ABC", readAscii(connection1.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        URLConnection connection2 = server.getUrl("/").openConnection();
        TestCase.assertEquals("DEF", readAscii(connection2.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    public void testServerClosesSocket() throws Exception {
        testServerClosesSocket(DISCONNECT_AT_END);
    }

    public void testServerShutdownInput() throws Exception {
        testServerClosesSocket(SHUTDOWN_INPUT_AT_END);
    }

    public void testServerShutdownOutput() throws Exception {
        // This test causes MockWebServer to log a "connection failed" stack trace
        server.enqueue(new MockResponse().setBody("Output shutdown after this response").setSocketPolicy(SHUTDOWN_OUTPUT_AT_END));
        server.enqueue(new MockResponse().setBody("This response will fail to write"));
        server.enqueue(new MockResponse().setBody("This comes after a busted connection"));
        server.play();
        assertContent("Output shutdown after this response", server.getUrl("/a").openConnection());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("This comes after a busted connection", server.getUrl("/b").openConnection());
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    public void testRetryableRequestBodyAfterBrokenConnection() throws Exception {
        // Use SSL to make an alternate route available.
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("abc").setSocketPolicy(DISCONNECT_AFTER_READING_REQUEST));
        server.enqueue(new MockResponse().setBody("abc"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setDoOutput(true);
        OutputStream out = connection.getOutputStream();
        out.write(new byte[]{ 1, 2, 3 });
        out.close();
        assertContent("abc", connection);
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    public void testNonRetryableRequestBodyAfterBrokenConnection() throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("abc").setSocketPolicy(DISCONNECT_AFTER_READING_REQUEST));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/a").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(3);
        OutputStream out = connection.getOutputStream();
        out.write(new byte[]{ 1, 2, 3 });
        out.close();
        try {
            connection.getInputStream();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    enum WriteKind {

        BYTE_BY_BYTE,
        SMALL_BUFFERS,
        LARGE_BUFFERS;}

    public void test_chunkedUpload_byteByByte() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.BYTE_BY_BYTE);
    }

    public void test_chunkedUpload_smallBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.SMALL_BUFFERS);
    }

    public void test_chunkedUpload_largeBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.CHUNKED, URLConnectionTest.WriteKind.LARGE_BUFFERS);
    }

    public void test_fixedLengthUpload_byteByByte() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.BYTE_BY_BYTE);
    }

    public void test_fixedLengthUpload_smallBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.SMALL_BUFFERS);
    }

    public void test_fixedLengthUpload_largeBuffers() throws Exception {
        doUpload(URLConnectionTest.TransferKind.FIXED_LENGTH, URLConnectionTest.WriteKind.LARGE_BUFFERS);
    }

    public void testGetResponseCodeNoResponseBody() throws Exception {
        server.enqueue(new MockResponse().addHeader("abc: def"));
        server.play();
        URL url = server.getUrl("/");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setDoInput(false);
        TestCase.assertEquals("def", conn.getHeaderField("abc"));
        TestCase.assertEquals(200, conn.getResponseCode());
        try {
            conn.getInputStream();
            TestCase.fail();
        } catch (ProtocolException expected) {
        }
    }

    public void testConnectViaHttps() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/foo").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("this response comes via HTTPS", connection);
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
        TestCase.assertEquals("TLSv1", request.getSslProtocol());
    }

    public void testConnectViaHttpsReusingConnections() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        SSLSocketFactory clientSocketFactory = testSSLContext.clientContext.getSocketFactory();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.enqueue(new MockResponse().setBody("another response via HTTPS"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(clientSocketFactory);
        assertContent("this response comes via HTTPS", connection);
        connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(clientSocketFactory);
        assertContent("another response via HTTPS", connection);
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    public void testConnectViaHttpsReusingConnectionsDifferentFactories() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.enqueue(new MockResponse().setBody("another response via HTTPS"));
        server.play();
        // install a custom SSL socket factory so the server can be authorized
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("this response comes via HTTPS", connection);
        connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            TestCase.fail("without an SSL socket factory, the connection should fail");
        } catch (SSLException expected) {
        }
    }

    public void testConnectViaHttpsWithSSLFallback() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.enqueue(new MockResponse().setBody("this response comes via SSL"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/foo").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("this response comes via SSL", connection);
        // The first request will be an incomplete (bookkeeping) request
        // that the server disconnected from at start.
        server.takeRequest();
        // The request will be retried.
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
    }

    /**
     * Verify that we don't retry connections on certificate verification errors.
     *
     * http://code.google.com/p/android/issues/detail?id=13178
     */
    public void testConnectViaHttpsToUntrustedServer() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create(TestKeyStore.getClientCA2(), TestKeyStore.getServer());
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse());// unused

        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/foo").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        try {
            connection.getInputStream();
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
            TestCase.assertTrue(((expected.getCause()) instanceof CertificateException));
        }
        TestCase.assertEquals(0, server.getRequestCount());
    }

    public void testConnectViaProxyUsingProxyArg() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.CREATE_ARG);
    }

    public void testConnectViaProxyUsingProxySystemProperty() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    public void testConnectViaProxyUsingHttpProxySystemProperty() throws Exception {
        testConnectViaProxy(URLConnectionTest.ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    public void testContentDisagreesWithContentLengthHeader() throws IOException {
        server.enqueue(new MockResponse().setBody("abc\r\nYOU SHOULD NOT SEE THIS").clearHeaders().addHeader("Content-Length: 3"));
        server.play();
        assertContent("abc", server.getUrl("/").openConnection());
    }

    public void testContentDisagreesWithChunkedHeader() throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setChunkedBody("abc", 3);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        bytesOut.write(mockResponse.getBody());
        bytesOut.write("\r\nYOU SHOULD NOT SEE THIS".getBytes());
        mockResponse.setBody(bytesOut.toByteArray());
        mockResponse.clearHeaders();
        mockResponse.addHeader("Transfer-encoding: chunked");
        server.enqueue(mockResponse);
        server.play();
        assertContent("abc", server.getUrl("/").openConnection());
    }

    public void testConnectViaHttpProxyToHttpsUsingProxyArgWithNoProxy() throws Exception {
        testConnectViaDirectProxyToHttps(URLConnectionTest.ProxyConfig.NO_PROXY);
    }

    public void testConnectViaHttpProxyToHttpsUsingHttpProxySystemProperty() throws Exception {
        // https should not use http proxy
        testConnectViaDirectProxyToHttps(URLConnectionTest.ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    public void testConnectViaHttpProxyToHttpsUsingProxyArg() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.CREATE_ARG);
    }

    /**
     * We weren't honoring all of the appropriate proxy system properties when
     * connecting via HTTPS. http://b/3097518
     */
    public void testConnectViaHttpProxyToHttpsUsingProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    public void testConnectViaHttpProxyToHttpsUsingHttpsProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(URLConnectionTest.ProxyConfig.HTTPS_PROXY_SYSTEM_PROPERTY);
    }

    /**
     * Tolerate bad https proxy response when using HttpResponseCache. http://b/6754912
     */
    public void testConnectViaHttpProxyToHttpsUsingBadProxyAndHttpResponseCache() throws Exception {
        URLConnectionTest.ProxyConfig proxyConfig = URLConnectionTest.ProxyConfig.PROXY_SYSTEM_PROPERTY;
        TestSSLContext testSSLContext = TestSSLContext.create();
        initResponseCache();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        MockResponse badProxyResponse = new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders().setBody("bogus proxy connect response content");// Key to reproducing b/6754912

        // We enqueue the bad response twice because the connection will
        // be retried with TLS_MODE_COMPATIBLE after the first connection
        // fails.
        server.enqueue(badProxyResponse);
        server.enqueue(badProxyResponse);
        server.play();
        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = ((HttpsURLConnection) (proxyConfig.connect(server, url)));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        try {
            connection.connect();
            TestCase.fail();
        } catch (SSLHandshakeException expected) {
            // Thrown when the connect causes SSLSocket.startHandshake() to throw
            // when it sees the "bogus proxy connect response content"
            // instead of a ServerHello handshake message.
        }
        RecordedRequest connect = server.takeRequest();
        TestCase.assertEquals("Connect line failure on proxy", "CONNECT android.com:443 HTTP/1.1", connect.getRequestLine());
        assertContains(connect.getHeaders(), "Host: android.com");
    }

    /**
     * Test which headers are sent unencrypted to the HTTP proxy.
     */
    public void testProxyConnectIncludesProxyHeadersOnly() throws IOException, InterruptedException {
        URLConnectionTest.RecordingHostnameVerifier hostnameVerifier = new URLConnectionTest.RecordingHostnameVerifier();
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("encrypted response from the origin server"));
        server.play();
        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection(server.toProxyAddress())));
        connection.addRequestProperty("Private", "Secret");
        connection.addRequestProperty("Proxy-Authorization", "bar");
        connection.addRequestProperty("User-Agent", "baz");
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(hostnameVerifier);
        assertContent("encrypted response from the origin server", connection);
        RecordedRequest connect = server.takeRequest();
        assertContainsNoneMatching(connect.getHeaders(), "Private.*");
        assertContains(connect.getHeaders(), "Proxy-Authorization: bar");
        assertContains(connect.getHeaders(), "User-Agent: baz");
        assertContains(connect.getHeaders(), "Host: android.com");
        assertContains(connect.getHeaders(), "Proxy-Connection: Keep-Alive");
        RecordedRequest get = server.takeRequest();
        assertContains(get.getHeaders(), "Private: Secret");
        TestCase.assertEquals(Arrays.asList("verify android.com"), hostnameVerifier.calls);
    }

    public void testProxyAuthenticateOnConnect() throws Exception {
        Authenticator.setDefault(new URLConnectionTest.SimpleAuthenticator());
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\""));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection(server.toProxyAddress())));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(new URLConnectionTest.RecordingHostnameVerifier());
        assertContent("A", connection);
        RecordedRequest connect1 = server.takeRequest();
        TestCase.assertEquals("CONNECT android.com:443 HTTP/1.1", connect1.getRequestLine());
        assertContainsNoneMatching(connect1.getHeaders(), "Proxy\\-Authorization.*");
        RecordedRequest connect2 = server.takeRequest();
        TestCase.assertEquals("CONNECT android.com:443 HTTP/1.1", connect2.getRequestLine());
        assertContains(connect2.getHeaders(), ("Proxy-Authorization: Basic " + (URLConnectionTest.SimpleAuthenticator.BASE_64_CREDENTIALS)));
        RecordedRequest get = server.takeRequest();
        TestCase.assertEquals("GET /foo HTTP/1.1", get.getRequestLine());
        assertContainsNoneMatching(get.getHeaders(), "Proxy\\-Authorization.*");
    }

    // Don't disconnect after building a tunnel with CONNECT
    // http://code.google.com/p/android/issues/detail?id=37221
    public void testProxyWithConnectionClose() throws IOException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("this response comes via a proxy"));
        server.play();
        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = ((HttpsURLConnection) (url.openConnection(server.toProxyAddress())));
        connection.setRequestProperty("Connection", "close");
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(new URLConnectionTest.RecordingHostnameVerifier());
        assertContent("this response comes via a proxy", connection);
    }

    public void testDisconnectedConnection() throws IOException {
        server.enqueue(new MockResponse().setBody("ABCDEFGHIJKLMNOPQR"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        InputStream in = connection.getInputStream();
        TestCase.assertEquals('A', ((char) (in.read())));
        connection.disconnect();
        try {
            in.read();
            TestCase.fail("Expected a connection closed exception");
        } catch (IOException expected) {
        }
    }

    public void testDisconnectBeforeConnect() throws IOException {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.disconnect();
        assertContent("A", connection);
        TestCase.assertEquals(200, connection.getResponseCode());
    }

    public void testDisconnectAfterOnlyResponseCodeCausesNoCloseGuardWarning() throws IOException {
        URLConnectionTest.CloseGuardGuard guard = new URLConnectionTest.CloseGuardGuard();
        try {
            server.enqueue(new MockResponse().setBody(gzip("ABCABCABC".getBytes("UTF-8"))).addHeader("Content-Encoding: gzip"));
            server.play();
            HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
            TestCase.assertEquals(200, connection.getResponseCode());
            connection.disconnect();
            connection = null;
            TestCase.assertFalse(guard.wasCloseGuardCalled());
        } finally {
            guard.close();
        }
    }

    public static class CloseGuardGuard implements CloseGuard.Reporter , Closeable {
        private final Reporter oldReporter = CloseGuard.getReporter();

        private AtomicBoolean closeGuardCalled = new AtomicBoolean();

        public CloseGuardGuard() {
            CloseGuard.setReporter(this);
        }

        @Override
        public void report(String message, Throwable allocationSite) {
            oldReporter.report(message, allocationSite);
            closeGuardCalled.set(true);
        }

        public boolean wasCloseGuardCalled() {
            FinalizationTester.induceFinalization();
            close();
            return closeGuardCalled.get();
        }

        @Override
        public void close() {
            CloseGuard.setReporter(oldReporter);
        }
    }

    public void testDefaultRequestProperty() throws Exception {
        URLConnection.setDefaultRequestProperty("X-testSetDefaultRequestProperty", "A");
        TestCase.assertNull(URLConnection.getDefaultRequestProperty("X-setDefaultRequestProperty"));
    }

    public void testMarkAndResetWithContentLengthHeader() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    public void testMarkAndResetWithChunkedEncoding() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.CHUNKED);
    }

    public void testMarkAndResetWithNoLengthHeaders() throws IOException {
        testMarkAndReset(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    /**
     * We've had a bug where we forget the HTTP response when we see response
     * code 401. This causes a new HTTP request to be issued for every call into
     * the URLConnection.
     */
    public void testUnauthorizedResponseHandling() throws IOException {
        MockResponse response = // UNAUTHORIZED
        new MockResponse().addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setResponseCode(401).setBody("Unauthorized");
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();
        URL url = server.getUrl("/");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        TestCase.assertEquals(401, conn.getResponseCode());
        TestCase.assertEquals(401, conn.getResponseCode());
        TestCase.assertEquals(401, conn.getResponseCode());
        TestCase.assertEquals(1, server.getRequestCount());
    }

    public void testNonHexChunkSize() throws IOException {
        server.enqueue(new MockResponse().setBody("5\r\nABCDE\r\nG\r\nFGHIJKLMNOPQRSTU\r\n0\r\n\r\n").clearHeaders().addHeader("Transfer-encoding: chunked"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    public void testMissingChunkBody() throws IOException {
        server.enqueue(new MockResponse().setBody("5").clearHeaders().addHeader("Transfer-encoding: chunked").setSocketPolicy(DISCONNECT_AT_END));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            TestCase.fail();
        } catch (IOException e) {
        }
    }

    /**
     * This test checks whether connections are gzipped by default. This
     * behavior in not required by the API, so a failure of this test does not
     * imply a bug in the implementation.
     */
    public void testGzipEncodingEnabledByDefault() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody(gzip("ABCABCABC".getBytes("UTF-8"))).addHeader("Content-Encoding: gzip"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        TestCase.assertEquals("ABCABCABC", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertNull(connection.getContentEncoding());
        TestCase.assertEquals((-1), connection.getContentLength());
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: gzip");
    }

    public void testClientConfiguredGzipContentEncoding() throws Exception {
        byte[] bodyBytes = gzip("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes("UTF-8"));
        server.enqueue(new MockResponse().setBody(bodyBytes).addHeader("Content-Encoding: gzip").addHeader(("Content-Length: " + (bodyBytes.length))));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        connection.addRequestProperty("Accept-Encoding", "gzip");
        InputStream gunzippedIn = new GZIPInputStream(connection.getInputStream());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", readAscii(gunzippedIn, Integer.MAX_VALUE));
        TestCase.assertEquals(bodyBytes.length, connection.getContentLength());
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: gzip");
    }

    public void testGzipAndConnectionReuseWithFixedLength() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    public void testGzipAndConnectionReuseWithChunkedEncoding() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(URLConnectionTest.TransferKind.CHUNKED);
    }

    public void testClientConfiguredCustomContentEncoding() throws Exception {
        server.enqueue(new MockResponse().setBody("ABCDE").addHeader("Content-Encoding: custom"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        connection.addRequestProperty("Accept-Encoding", "custom");
        TestCase.assertEquals("ABCDE", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: custom");
    }

    /**
     * Test that HEAD requests don't have a body regardless of the response
     * headers. http://code.google.com/p/android/issues/detail?id=24672
     */
    public void testHeadAndContentLength() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().addHeader("Content-Length: 100"));
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection1 = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection1.setRequestMethod("HEAD");
        TestCase.assertEquals("100", connection1.getHeaderField("Content-Length"));
        assertContent("", connection1);
        HttpURLConnection connection2 = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("A", readAscii(connection2.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    /**
     * Obnoxiously test that the chunk sizes transmitted exactly equal the
     * requested data+chunk header size. Although setChunkedStreamingMode()
     * isn't specific about whether the size applies to the data or the
     * complete chunk, the RI interprets it as a complete chunk.
     */
    public void testSetChunkedStreamingMode() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection urlConnection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        urlConnection.setChunkedStreamingMode(8);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write("ABCDEFGHIJKLMNOPQ".getBytes("US-ASCII"));
        TestCase.assertEquals(200, urlConnection.getResponseCode());
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQ", new String(request.getBody(), "US-ASCII"));
        TestCase.assertEquals(Arrays.asList(3, 3, 3, 3, 3, 2), request.getChunkSizes());
    }

    public void testAuthenticateWithFixedLengthStreaming() throws Exception {
        testAuthenticateWithStreamingPost(URLConnectionTest.StreamingMode.FIXED_LENGTH);
    }

    public void testAuthenticateWithChunkedStreaming() throws Exception {
        testAuthenticateWithStreamingPost(URLConnectionTest.StreamingMode.CHUNKED);
    }

    public void testSetValidRequestMethod() throws Exception {
        server.play();
        assertValidRequestMethod("GET");
        assertValidRequestMethod("DELETE");
        assertValidRequestMethod("HEAD");
        assertValidRequestMethod("OPTIONS");
        assertValidRequestMethod("POST");
        assertValidRequestMethod("PUT");
        assertValidRequestMethod("TRACE");
    }

    public void testSetInvalidRequestMethodLowercase() throws Exception {
        server.play();
        assertInvalidRequestMethod("get");
    }

    public void testSetInvalidRequestMethodConnect() throws Exception {
        server.play();
        assertInvalidRequestMethod("CONNECT");
    }

    public void testCannotSetNegativeFixedLengthStreamingMode() throws Exception {
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        try {
            connection.setFixedLengthStreamingMode((-2));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testCanSetNegativeChunkedStreamingMode() throws Exception {
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setChunkedStreamingMode((-2));
    }

    public void testCannotSetFixedLengthStreamingModeAfterConnect() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("A", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        try {
            connection.setFixedLengthStreamingMode(1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testCannotSetChunkedStreamingModeAfterConnect() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("A", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        try {
            connection.setChunkedStreamingMode(1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testCannotSetFixedLengthStreamingModeAfterChunkedStreamingMode() throws Exception {
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setChunkedStreamingMode(1);
        try {
            connection.setFixedLengthStreamingMode(1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testCannotSetChunkedStreamingModeAfterFixedLengthStreamingMode() throws Exception {
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setFixedLengthStreamingMode(1);
        try {
            connection.setChunkedStreamingMode(1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testSecureFixedLengthStreaming() throws Exception {
        testSecureStreamingPost(URLConnectionTest.StreamingMode.FIXED_LENGTH);
    }

    public void testSecureChunkedStreaming() throws Exception {
        testSecureStreamingPost(URLConnectionTest.StreamingMode.CHUNKED);
    }

    enum StreamingMode {

        FIXED_LENGTH,
        CHUNKED;}

    public void testAuthenticateWithPost() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate.");
        // fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();
        Authenticator.setDefault(new URLConnectionTest.SimpleAuthenticator());
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setDoOutput(true);
        byte[] requestBody = new byte[]{ 'A', 'B', 'C', 'D' };
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody);
        outputStream.close();
        TestCase.assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        // no authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        assertContainsNoneMatching(request.getHeaders(), "Authorization: .*");
        // ...but the three requests that follow include an authorization header
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            TestCase.assertEquals("POST / HTTP/1.1", request.getRequestLine());
            assertContains(request.getHeaders(), ("Authorization: Basic " + (URLConnectionTest.SimpleAuthenticator.BASE_64_CREDENTIALS)));
            TestCase.assertEquals(Arrays.toString(requestBody), Arrays.toString(request.getBody()));
        }
    }

    public void testAuthenticateWithGet() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Basic realm=\"protected area\"").setBody("Please authenticate.");
        // fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();
        URLConnectionTest.SimpleAuthenticator authenticator = new URLConnectionTest.SimpleAuthenticator();
        Authenticator.setDefault(authenticator);
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(SERVER, authenticator.requestorType);
        TestCase.assertEquals(server.getPort(), authenticator.requestingPort);
        TestCase.assertEquals(InetAddress.getByName(server.getHostName()), authenticator.requestingSite);
        TestCase.assertEquals("protected area", authenticator.requestingPrompt);
        TestCase.assertEquals("http", authenticator.requestingProtocol);
        TestCase.assertEquals("Basic", authenticator.requestingScheme);
        // no authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        assertContainsNoneMatching(request.getHeaders(), "Authorization: .*");
        // ...but the three requests that follow requests include an authorization header
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            TestCase.assertEquals("GET / HTTP/1.1", request.getRequestLine());
            assertContains(request.getHeaders(), ("Authorization: Basic " + (URLConnectionTest.SimpleAuthenticator.BASE_64_CREDENTIALS)));
        }
    }

    // bug 11473660
    public void testAuthenticateWithLowerCaseHeadersAndScheme() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse().setResponseCode(401).addHeader("www-authenticate: basic realm=\"protected area\"").setBody("Please authenticate.");
        // fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();
        URLConnectionTest.SimpleAuthenticator authenticator = new URLConnectionTest.SimpleAuthenticator();
        Authenticator.setDefault(authenticator);
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(SERVER, authenticator.requestorType);
        TestCase.assertEquals(server.getPort(), authenticator.requestingPort);
        TestCase.assertEquals(InetAddress.getByName(server.getHostName()), authenticator.requestingSite);
        TestCase.assertEquals("protected area", authenticator.requestingPrompt);
        TestCase.assertEquals("http", authenticator.requestingProtocol);
        TestCase.assertEquals("basic", authenticator.requestingScheme);
    }

    // http://code.google.com/p/android/issues/detail?id=19081
    public void testAuthenticateWithCommaSeparatedAuthenticationMethods() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader(("WWW-Authenticate: Scheme1 realm=\"a\", Basic realm=\"b\", " + "Scheme3 realm=\"c\"")).setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();
        URLConnectionTest.SimpleAuthenticator authenticator = new URLConnectionTest.SimpleAuthenticator();
        authenticator.expectedPrompt = "b";
        Authenticator.setDefault(authenticator);
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertContainsNoneMatching(server.takeRequest().getHeaders(), "Authorization: .*");
        assertContains(server.takeRequest().getHeaders(), ("Authorization: Basic " + (URLConnectionTest.SimpleAuthenticator.BASE_64_CREDENTIALS)));
        TestCase.assertEquals("Basic", authenticator.requestingScheme);
    }

    public void testAuthenticateWithMultipleAuthenticationHeaders() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).addHeader("WWW-Authenticate: Scheme1 realm=\"a\"").addHeader("WWW-Authenticate: Basic realm=\"b\"").addHeader("WWW-Authenticate: Scheme3 realm=\"c\"").setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();
        URLConnectionTest.SimpleAuthenticator authenticator = new URLConnectionTest.SimpleAuthenticator();
        authenticator.expectedPrompt = "b";
        Authenticator.setDefault(authenticator);
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertContainsNoneMatching(server.takeRequest().getHeaders(), "Authorization: .*");
        assertContains(server.takeRequest().getHeaders(), ("Authorization: Basic " + (URLConnectionTest.SimpleAuthenticator.BASE_64_CREDENTIALS)));
        TestCase.assertEquals("Basic", authenticator.requestingScheme);
    }

    public void testRedirectedWithChunkedEncoding() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.CHUNKED, true);
    }

    public void testRedirectedWithContentLengthHeader() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.FIXED_LENGTH, true);
    }

    public void testRedirectedWithNoLengthHeaders() throws Exception {
        testRedirected(URLConnectionTest.TransferKind.END_OF_STREAM, false);
    }

    public void testRedirectedOnHttps() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        TestCase.assertEquals("This is the new location!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        RecordedRequest first = server.takeRequest();
        TestCase.assertEquals("GET / HTTP/1.1", first.getRequestLine());
        RecordedRequest retry = server.takeRequest();
        TestCase.assertEquals("GET /foo HTTP/1.1", retry.getRequestLine());
        TestCase.assertEquals("Expected connection reuse", 1, retry.getSequenceNumber());
    }

    public void testNotRedirectedFromHttpsToHttp() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: http://anyhost/foo").setBody("This page has moved!"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        TestCase.assertEquals("This page has moved!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testNotRedirectedFromHttpToHttps() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: https://anyhost/foo").setBody("This page has moved!"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("This page has moved!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testRedirectToAnotherOriginServer() throws Exception {
        MockWebServer server2 = new MockWebServer();
        try {
            // RoboVM note: Modified to call server2.shutdown() after test finishes regardless of outcome.
            server2.enqueue(new MockResponse().setBody("This is the 2nd server!"));
            server2.play();
            server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server2.getUrl("/").toString()))).setBody("This page has moved!"));
            server.enqueue(new MockResponse().setBody("This is the first server again!"));
            server.play();
            URLConnection connection = server.getUrl("/").openConnection();
            TestCase.assertEquals("This is the 2nd server!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
            TestCase.assertEquals(server2.getUrl("/"), connection.getURL());
            // make sure the first server was careful to recycle the connection
            TestCase.assertEquals("This is the first server again!", readAscii(server.getUrl("/").openStream(), Integer.MAX_VALUE));
            RecordedRequest first = server.takeRequest();
            assertContains(first.getHeaders(), ((("Host: " + (hostName)) + ":") + (server.getPort())));
            RecordedRequest second = server2.takeRequest();
            assertContains(second.getHeaders(), ((("Host: " + (hostName)) + ":") + (server2.getPort())));
            RecordedRequest third = server.takeRequest();
            TestCase.assertEquals("Expected connection reuse", 1, third.getSequenceNumber());
        } finally {
            server2.shutdown();
        }
    }

    public void testResponse300MultipleChoiceWithPost() throws Exception {
        // Chrome doesn't follow the redirect, but Firefox and the RI both do
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MULT_CHOICE);
    }

    public void testResponse301MovedPermanentlyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_PERM);
    }

    public void testResponse302MovedTemporarilyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    public void testResponse303SeeOtherWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_SEE_OTHER);
    }

    public void testResponse305UseProxy() throws Exception {
        server.play();
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_USE_PROXY).addHeader(("Location: " + (server.getUrl("/")))).setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Proxy Response"));
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/foo").openConnection()));
        // Fails on the RI, which gets "Proxy Response"
        TestCase.assertEquals("This page has moved!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        RecordedRequest page1 = server.takeRequest();
        TestCase.assertEquals("GET /foo HTTP/1.1", page1.getRequestLine());
        TestCase.assertEquals(1, server.getRequestCount());
    }

    public void testHttpsWithCustomTrustManager() throws Exception {
        URLConnectionTest.RecordingHostnameVerifier hostnameVerifier = new URLConnectionTest.RecordingHostnameVerifier();
        URLConnectionTest.RecordingTrustManager trustManager = new URLConnectionTest.RecordingTrustManager();
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{ trustManager }, new SecureRandom());
        HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        try {
            TestSSLContext testSSLContext = TestSSLContext.create();
            server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
            server.enqueue(new MockResponse().setBody("ABC"));
            server.enqueue(new MockResponse().setBody("DEF"));
            server.enqueue(new MockResponse().setBody("GHI"));
            server.play();
            URL url = server.getUrl("/");
            TestCase.assertEquals("ABC", readAscii(url.openStream(), Integer.MAX_VALUE));
            TestCase.assertEquals("DEF", readAscii(url.openStream(), Integer.MAX_VALUE));
            TestCase.assertEquals("GHI", readAscii(url.openStream(), Integer.MAX_VALUE));
            TestCase.assertEquals(Arrays.asList(("verify " + (hostName))), hostnameVerifier.calls);
            TestCase.assertEquals(Arrays.asList((((((("checkServerTrusted [" + "CN=") + (hostName)) + " 1, ") + "CN=Test Intermediate Certificate Authority 1, ") + "CN=Test Root Certificate Authority 1") + "] RSA")), trustManager.calls);
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
        }
    }

    /**
     * Test that the timeout period is honored. The timeout may be doubled!
     * HttpURLConnection will wait the full timeout for each of the server's IP
     * addresses. This is typically one IPv4 address and one IPv6 address.
     */
    public void testConnectTimeouts() throws IOException {
        StuckServer ss = new StuckServer(true);
        int serverPort = ss.getLocalPort();
        String hostName = ss.getLocalSocketAddress().getAddress().getHostAddress();
        URLConnection urlConnection = new URL((((("http://" + hostName) + ":") + serverPort) + "/")).openConnection();
        int timeout = 1000;
        urlConnection.setConnectTimeout(timeout);
        long start = System.currentTimeMillis();
        try {
            urlConnection.getInputStream();
            TestCase.fail();
        } catch (SocketTimeoutException expected) {
            long elapsed = (System.currentTimeMillis()) - start;
            int attempts = InetAddress.getAllByName("localhost").length;// one per IP address

            TestCase.assertTrue(((((("timeout=" + timeout) + ", elapsed=") + elapsed) + ", attempts=") + attempts), ((Math.abs(((attempts * timeout) - elapsed))) < 500));
        } finally {
            ss.close();
        }
    }

    public void testReadTimeouts() throws IOException {
        /* This relies on the fact that MockWebServer doesn't close the
        connection after a response has been sent. This causes the client to
        try to read more bytes than are sent, which results in a timeout.
         */
        MockResponse timeout = new MockResponse().setBody("ABC").clearHeaders().addHeader("Content-Length: 4");
        server.enqueue(timeout);
        server.enqueue(new MockResponse().setBody("unused"));// to keep the server alive

        server.play();
        URLConnection urlConnection = server.getUrl("/").openConnection();
        urlConnection.setReadTimeout(1000);
        InputStream in = urlConnection.getInputStream();
        TestCase.assertEquals('A', in.read());
        TestCase.assertEquals('B', in.read());
        TestCase.assertEquals('C', in.read());
        try {
            in.read();// if Content-Length was accurate, this would return -1 immediately

            TestCase.fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    public void testSetChunkedEncodingAsRequestProperty() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection urlConnection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        urlConnection.setRequestProperty("Transfer-encoding", "chunked");
        urlConnection.setDoOutput(true);
        urlConnection.getOutputStream().write("ABC".getBytes("UTF-8"));
        TestCase.assertEquals(200, urlConnection.getResponseCode());
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("ABC", new String(request.getBody(), "UTF-8"));
    }

    public void testConnectionCloseInRequest() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());// server doesn't honor the connection: close header!

        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection a = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        a.setRequestProperty("Connection", "close");
        TestCase.assertEquals(200, a.getResponseCode());
        HttpURLConnection b = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals(200, b.getResponseCode());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    public void testConnectionCloseInResponse() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().addHeader("Connection: close"));
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection a = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals(200, a.getResponseCode());
        HttpURLConnection b = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals(200, b.getResponseCode());
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    public void testConnectionCloseWithRedirect() throws IOException, InterruptedException {
        MockResponse response = new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").addHeader("Connection: close");
        server.enqueue(response);
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        TestCase.assertEquals("This is the new location!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        TestCase.assertEquals(0, server.takeRequest().getSequenceNumber());
        TestCase.assertEquals("When connection: close is used, each request should get its own connection", 0, server.takeRequest().getSequenceNumber());
    }

    public void testResponseCodeDisagreesWithHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT).setBody("This body is not allowed!"));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        TestCase.assertEquals("This body is not allowed!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testSingleByteReadIsSigned() throws IOException {
        server.enqueue(new MockResponse().setBody(new byte[]{ -2, -1 }));
        server.play();
        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        TestCase.assertEquals(254, in.read());
        TestCase.assertEquals(255, in.read());
        TestCase.assertEquals((-1), in.read());
    }

    public void testFlushAfterStreamTransmittedWithChunkedEncoding() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.CHUNKED);
    }

    public void testFlushAfterStreamTransmittedWithFixedLength() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    public void testFlushAfterStreamTransmittedWithNoLengthHeaders() throws IOException {
        testFlushAfterStreamTransmitted(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    public void testGetHeadersThrows() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        try {
            connection.getInputStream();
            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            connection.getInputStream();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void testReadTimeoutsOnRecycledConnections() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        server.play();
        // The request should work once and then fail
        URLConnection connection = server.getUrl("").openConnection();
        // Read timeout of a day, sure to cause the test to timeout and fail.
        connection.setReadTimeout(((24 * 3600) * 1000));
        InputStream input = connection.getInputStream();
        TestCase.assertEquals("ABC", readAscii(input, Integer.MAX_VALUE));
        input.close();
        try {
            connection = server.getUrl("").openConnection();
            // Set the read timeout back to 100ms, this request will time out
            // because we've only enqueued one response.
            connection.setReadTimeout(100);
            connection.getInputStream();
            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    /**
     * This test goes through the exhaustive set of interesting ASCII characters
     * because most of those characters are interesting in some way according to
     * RFC 2396 and RFC 2732. http://b/1158780
     */
    public void testLenientUrlToUri() throws Exception {
        // alphanum
        testUrlToUriMapping("abzABZ09", "abzABZ09", "abzABZ09", "abzABZ09", "abzABZ09");
        // control characters
        testUrlToUriMapping("\u0001", "%01", "%01", "%01", "%01");
        testUrlToUriMapping("\u001f", "%1F", "%1F", "%1F", "%1F");
        // ascii characters
        testUrlToUriMapping("%20", "%20", "%20", "%20", "%20");
        testUrlToUriMapping("%20", "%20", "%20", "%20", "%20");
        testUrlToUriMapping(" ", "%20", "%20", "%20", "%20");
        testUrlToUriMapping("!", "!", "!", "!", "!");
        testUrlToUriMapping("\"", "%22", "%22", "%22", "%22");
        testUrlToUriMapping("#", null, null, null, "%23");
        testUrlToUriMapping("$", "$", "$", "$", "$");
        testUrlToUriMapping("&", "&", "&", "&", "&");
        testUrlToUriMapping("'", "'", "'", "'", "'");
        testUrlToUriMapping("(", "(", "(", "(", "(");
        testUrlToUriMapping(")", ")", ")", ")", ")");
        testUrlToUriMapping("*", "*", "*", "*", "*");
        testUrlToUriMapping("+", "+", "+", "+", "+");
        testUrlToUriMapping(",", ",", ",", ",", ",");
        testUrlToUriMapping("-", "-", "-", "-", "-");
        testUrlToUriMapping(".", ".", ".", ".", ".");
        testUrlToUriMapping("/", null, "/", "/", "/");
        testUrlToUriMapping(":", null, ":", ":", ":");
        testUrlToUriMapping(";", ";", ";", ";", ";");
        testUrlToUriMapping("<", "%3C", "%3C", "%3C", "%3C");
        testUrlToUriMapping("=", "=", "=", "=", "=");
        testUrlToUriMapping(">", "%3E", "%3E", "%3E", "%3E");
        testUrlToUriMapping("?", null, null, "?", "?");
        testUrlToUriMapping("@", "@", "@", "@", "@");
        testUrlToUriMapping("[", null, "%5B", null, "%5B");
        testUrlToUriMapping("\\", "%5C", "%5C", "%5C", "%5C");
        testUrlToUriMapping("]", null, "%5D", null, "%5D");
        testUrlToUriMapping("^", "%5E", "%5E", "%5E", "%5E");
        testUrlToUriMapping("_", "_", "_", "_", "_");
        testUrlToUriMapping("`", "%60", "%60", "%60", "%60");
        testUrlToUriMapping("{", "%7B", "%7B", "%7B", "%7B");
        testUrlToUriMapping("|", "%7C", "%7C", "%7C", "%7C");
        testUrlToUriMapping("}", "%7D", "%7D", "%7D", "%7D");
        testUrlToUriMapping("~", "~", "~", "~", "~");
        testUrlToUriMapping("~", "~", "~", "~", "~");
        testUrlToUriMapping("\u007f", "%7F", "%7F", "%7F", "%7F");
        // beyond ascii
        testUrlToUriMapping("\u0080", "%C2%80", "%C2%80", "%C2%80", "%C2%80");
        testUrlToUriMapping("\u20ac", "\u20ac", "\u20ac", "\u20ac", "\u20ac");
        testUrlToUriMapping("\ud842\udf9f", "\ud842\udf9f", "\ud842\udf9f", "\ud842\udf9f", "\ud842\udf9f");
    }

    public void testLenientUrlToUriNul() throws Exception {
        // On JB-MR2 and below, we would allow a host containing \u0000
        // and then generate a request with a Host header that violated RFC2616.
        // We now reject such hosts.
        // 
        // The ideal behaviour here is to be "lenient" about the host and rewrite
        // it, but attempting to do so introduces a new range of incompatible
        // behaviours.
        testUrlToUriMapping("\u0000", null, "%00", "%00", "%00");// RI fails this

    }

    public void testHostWithNul() throws Exception {
        URL url = new URL("http://host\u0000/");
        try {
            url.openStream();
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Don't explode if the cache returns a null body. http://b/3373699
     */
    public void testResponseCacheReturnsNullOutputStream() throws Exception {
        final AtomicBoolean aborted = new AtomicBoolean();
        ResponseCache.setDefault(new ResponseCache() {
            @Override
            public CacheResponse get(URI uri, String requestMethod, Map<String, List<String>> requestHeaders) throws IOException {
                return null;
            }

            @Override
            public CacheRequest put(URI uri, URLConnection connection) throws IOException {
                return new CacheRequest() {
                    @Override
                    public void abort() {
                        aborted.set(true);
                    }

                    @Override
                    public OutputStream getBody() throws IOException {
                        return null;
                    }
                };
            }
        });
        server.enqueue(new MockResponse().setBody("abcdef"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        InputStream in = connection.getInputStream();
        TestCase.assertEquals("abc", readAscii(in, 3));
        in.close();
        TestCase.assertFalse(aborted.get());// The best behavior is ambiguous, but RI 6 doesn't abort here

    }

    /**
     * http://code.google.com/p/android/issues/detail?id=14562
     */
    public void testReadAfterLastByte() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC").clearHeaders().addHeader("Connection: close").setSocketPolicy(SocketPolicy.DISCONNECT_AT_END));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        InputStream in = connection.getInputStream();
        TestCase.assertEquals("ABC", readAscii(in, 3));
        TestCase.assertEquals((-1), in.read());
        TestCase.assertEquals((-1), in.read());// throws IOException in Gingerbread

    }

    public void testGetContent() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        InputStream in = ((InputStream) (connection.getContent()));
        TestCase.assertEquals("A", readAscii(in, Integer.MAX_VALUE));
    }

    public void testGetContentOfType() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        try {
            connection.getContent(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            connection.getContent(new Class[]{ null });
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertNull(connection.getContent(new Class[]{ getClass() }));
        connection.disconnect();
    }

    public void testGetOutputStreamOnGetFails() throws Exception {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        try {
            connection.getOutputStream();
            TestCase.fail();
        } catch (ProtocolException expected) {
        }
    }

    public void testGetOutputAfterGetInputStreamFails() throws Exception {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setDoOutput(true);
        try {
            connection.getInputStream();
            connection.getOutputStream();
            TestCase.fail();
        } catch (ProtocolException expected) {
        }
    }

    public void testSetDoOutputOrDoInputAfterConnectFails() throws Exception {
        server.enqueue(new MockResponse());
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.connect();
        try {
            connection.setDoOutput(true);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            connection.setDoInput(true);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        connection.disconnect();
    }

    public void testClientSendsContentLength() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        connection.setDoOutput(true);
        OutputStream out = connection.getOutputStream();
        out.write(new byte[]{ 'A', 'B', 'C' });
        out.close();
        TestCase.assertEquals("A", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Content-Length: 3");
    }

    public void testGetContentLengthConnects() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals(3, connection.getContentLength());
        connection.disconnect();
    }

    public void testGetContentTypeConnects() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Type: text/plain").setBody("ABC"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("text/plain", connection.getContentType());
        connection.disconnect();
    }

    public void testGetContentEncodingConnects() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Encoding: identity").setBody("ABC"));
        server.play();
        HttpURLConnection connection = ((HttpURLConnection) (server.getUrl("/").openConnection()));
        TestCase.assertEquals("identity", connection.getContentEncoding());
        connection.disconnect();
    }

    // http://b/4361656
    public void testUrlContainsQueryButNoPath() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();
        URL url = new URL("http", server.getHostName(), server.getPort(), "?query");
        TestCase.assertEquals("A", readAscii(url.openConnection().getInputStream(), Integer.MAX_VALUE));
        RecordedRequest request = server.takeRequest();
        TestCase.assertEquals("GET /?query HTTP/1.1", request.getRequestLine());
    }

    // http://code.google.com/p/android/issues/detail?id=20442
    public void testInputStreamAvailableWithChunkedEncoding() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.CHUNKED);
    }

    public void testInputStreamAvailableWithContentLengthHeader() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.FIXED_LENGTH);
    }

    public void testInputStreamAvailableWithNoLengthHeaders() throws Exception {
        testInputStreamAvailable(URLConnectionTest.TransferKind.END_OF_STREAM);
    }

    // http://code.google.com/p/android/issues/detail?id=28095
    public void testInvalidIpv4Address() throws Exception {
        try {
            URI uri = new URI("http://1111.111.111.111/index.html");
            uri.toURL().openConnection().connect();
            TestCase.fail();
        } catch (UnknownHostException expected) {
        }
    }

    // http://code.google.com/p/android/issues/detail?id=16895
    public void testUrlWithSpaceInHost() throws Exception {
        URLConnection urlConnection = new URL("http://and roid.com/").openConnection();
        try {
            urlConnection.getInputStream();
            TestCase.fail();
        } catch (UnknownHostException expected) {
        }
    }

    public void testUrlWithSpaceInHostViaHttpProxy() throws Exception {
        server.enqueue(new MockResponse());
        server.play();
        URLConnection urlConnection = new URL("http://and roid.com/").openConnection(server.toProxyAddress());
        try {
            urlConnection.getInputStream();
            TestCase.fail();// the RI makes a bogus proxy request for "GET http://and roid.com/ HTTP/1.1"

        } catch (UnknownHostException expected) {
        }
    }

    public void testSslFallback() throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("This required a 2nd handshake"));
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        TestCase.assertEquals("This required a 2nd handshake", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        RecordedRequest first = server.takeRequest();
        TestCase.assertEquals(0, first.getSequenceNumber());
        RecordedRequest retry = server.takeRequest();
        TestCase.assertEquals(0, retry.getSequenceNumber());
        TestCase.assertEquals("SSLv3", retry.getSslProtocol());
    }

    public void testInspectSslBeforeConnect() throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse());
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        TestCase.assertNotNull(connection.getHostnameVerifier());
        try {
            connection.getLocalCertificates();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            connection.getServerCertificates();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            connection.getCipherSuite();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            connection.getPeerPrincipal();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     * Test that we can inspect the SSL session after connect().
     * http://code.google.com/p/android/issues/detail?id=24431
     */
    public void testInspectSslAfterConnect() throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse());
        server.play();
        HttpsURLConnection connection = ((HttpsURLConnection) (server.getUrl("/").openConnection()));
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.connect();
        TestCase.assertNotNull(connection.getHostnameVerifier());
        TestCase.assertNull(connection.getLocalCertificates());
        TestCase.assertNotNull(connection.getServerCertificates());
        TestCase.assertNotNull(connection.getCipherSuite());
        TestCase.assertNotNull(connection.getPeerPrincipal());
    }

    enum TransferKind {

        CHUNKED() {
            @Override
            void setBody(MockResponse response, byte[] content, int chunkSize) throws IOException {
                response.setChunkedBody(content, chunkSize);
            }
        },
        FIXED_LENGTH() {
            @Override
            void setBody(MockResponse response, byte[] content, int chunkSize) {
                response.setBody(content);
            }
        },
        END_OF_STREAM() {
            @Override
            void setBody(MockResponse response, byte[] content, int chunkSize) {
                response.setBody(content);
                response.setSocketPolicy(DISCONNECT_AT_END);
                for (Iterator<String> h = response.getHeaders().iterator(); h.hasNext();) {
                    if (h.next().startsWith("Content-Length:")) {
                        h.remove();
                        break;
                    }
                }
            }
        };
        abstract void setBody(MockResponse response, byte[] content, int chunkSize) throws IOException;

        void setBody(MockResponse response, String content, int chunkSize) throws IOException {
            setBody(response, content.getBytes("UTF-8"), chunkSize);
        }
    }

    enum ProxyConfig {

        NO_PROXY() {
            @Override
            public HttpURLConnection connect(MockWebServer server, URL url) throws IOException {
                return ((HttpURLConnection) (url.openConnection(Proxy.NO_PROXY)));
            }
        },
        CREATE_ARG() {
            @Override
            public HttpURLConnection connect(MockWebServer server, URL url) throws IOException {
                return ((HttpURLConnection) (url.openConnection(server.toProxyAddress())));
            }
        },
        PROXY_SYSTEM_PROPERTY() {
            @Override
            public HttpURLConnection connect(MockWebServer server, URL url) throws IOException {
                System.setProperty("proxyHost", "localhost");
                System.setProperty("proxyPort", Integer.toString(server.getPort()));
                return ((HttpURLConnection) (url.openConnection()));
            }
        },
        HTTP_PROXY_SYSTEM_PROPERTY() {
            @Override
            public HttpURLConnection connect(MockWebServer server, URL url) throws IOException {
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", Integer.toString(server.getPort()));
                return ((HttpURLConnection) (url.openConnection()));
            }
        },
        HTTPS_PROXY_SYSTEM_PROPERTY() {
            @Override
            public HttpURLConnection connect(MockWebServer server, URL url) throws IOException {
                System.setProperty("https.proxyHost", "localhost");
                System.setProperty("https.proxyPort", Integer.toString(server.getPort()));
                return ((HttpURLConnection) (url.openConnection()));
            }
        };
        public abstract HttpURLConnection connect(MockWebServer server, URL url) throws IOException;
    }

    private static class RecordingTrustManager implements X509TrustManager {
        private final List<String> calls = new ArrayList<String>();

        public X509Certificate[] getAcceptedIssuers() {
            calls.add("getAcceptedIssuers");
            return new X509Certificate[]{  };
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            calls.add(((("checkClientTrusted " + (certificatesToString(chain))) + " ") + authType));
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            calls.add(((("checkServerTrusted " + (certificatesToString(chain))) + " ") + authType));
        }

        private String certificatesToString(X509Certificate[] certificates) {
            List<String> result = new ArrayList<String>();
            for (X509Certificate certificate : certificates) {
                result.add((((certificate.getSubjectDN()) + " ") + (certificate.getSerialNumber())));
            }
            return result.toString();
        }
    }

    private static class RecordingHostnameVerifier implements HostnameVerifier {
        private final List<String> calls = new ArrayList<String>();

        public boolean verify(String hostname, SSLSession session) {
            calls.add(("verify " + hostname));
            return true;
        }
    }

    private static class SimpleAuthenticator extends Authenticator {
        /**
         * base64("username:password")
         */
        private static final String BASE_64_CREDENTIALS = "dXNlcm5hbWU6cGFzc3dvcmQ=";

        private String expectedPrompt;

        private Authenticator.RequestorType requestorType;

        private int requestingPort;

        private InetAddress requestingSite;

        private String requestingPrompt;

        private String requestingProtocol;

        private String requestingScheme;

        protected PasswordAuthentication getPasswordAuthentication() {
            requestorType = getRequestorType();
            requestingPort = getRequestingPort();
            requestingSite = getRequestingSite();
            requestingPrompt = getRequestingPrompt();
            requestingProtocol = getRequestingProtocol();
            requestingScheme = getRequestingScheme();
            return ((expectedPrompt) == null) || (expectedPrompt.equals(requestingPrompt)) ? new PasswordAuthentication("username", "password".toCharArray()) : null;
        }
    }
}

