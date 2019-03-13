/**
 * Copyright (C) 2013 Square, Inc.
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


import ConnectionSpec.CLEARTEXT;
import ConnectionSpec.COMPATIBLE_TLS;
import ConnectionSpec.MODERN_TLS;
import ConnectionSpec.RESTRICTED_TLS;
import Dns.SYSTEM;
import Protocol.H2_PRIOR_KNOWLEDGE;
import Protocol.HTTP_1_1;
import Protocol.HTTP_2;
import Request.Builder;
import SocketPolicy.CONTINUE_ALWAYS;
import SocketPolicy.DISCONNECT_AFTER_REQUEST;
import SocketPolicy.DISCONNECT_AT_END;
import SocketPolicy.EXPECT_CONTINUE;
import SocketPolicy.FAIL_HANDSHAKE;
import SocketPolicy.NO_RESPONSE;
import SocketPolicy.STALL_SOCKET_AT_START;
import SocketPolicy.UPGRADE_TO_SSL_AT_END;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.internal.DoubleInetAddressDns;
import okhttp3.internal.RecordingOkAuthenticator;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.RecordingProxySelector;
import okhttp3.internal.io.InMemoryFileSystem;
import okhttp3.internal.platform.PlatformTest;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.okhttp3.Dispatcher;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import static ConnectionSpec.MODERN_TLS;
import static TestUtil.UNREACHABLE_ADDRESS;
import static java.net.Proxy.Type.HTTP;


public final class CallTest {
    @Rule
    public final TestRule timeout = new Timeout(30000, TimeUnit.MILLISECONDS);

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final MockWebServer server2 = new MockWebServer();

    @Rule
    public final InMemoryFileSystem fileSystem = new InMemoryFileSystem();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    private final RecordingEventListener listener = new RecordingEventListener();

    private HandshakeCertificates handshakeCertificates = localhost();

    private OkHttpClient client = clientTestRule.client.newBuilder().eventListener(listener).build();

    private RecordingCallback callback = new RecordingCallback();

    private TestLogHandler logHandler = new TestLogHandler();

    private Cache cache = new Cache(new File("/cache/"), Integer.MAX_VALUE, fileSystem);

    private Logger logger = Logger.getLogger(OkHttpClient.class.getName());

    @Test
    public void get() throws Exception {
        server.enqueue(new MockResponse().setBody("abc").clearHeaders().addHeader("content-type: text/plain").addHeader("content-length", "3"));
        long sentAt = System.currentTimeMillis();
        RecordedResponse recordedResponse = executeSynchronously("/", "User-Agent", "SyncApiTest");
        long receivedAt = System.currentTimeMillis();
        recordedResponse.assertCode(200).assertSuccessful().assertHeaders(new Headers.Builder().add("content-type", "text/plain").add("content-length", "3").build()).assertBody("abc").assertSentRequestAtMillis(sentAt, receivedAt).assertReceivedResponseAtMillis(sentAt, receivedAt);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("GET", recordedRequest.getMethod());
        Assert.assertEquals("SyncApiTest", recordedRequest.getHeader("User-Agent"));
        Assert.assertEquals(0, recordedRequest.getBody().size());
        Assert.assertNull(recordedRequest.getHeader("Content-Length"));
    }

    @Test
    public void buildRequestUsingHttpUrl() throws Exception {
        server.enqueue(new MockResponse());
        executeSynchronously("/").assertSuccessful();
    }

    @Test
    public void invalidScheme() throws Exception {
        Request.Builder requestBuilder = new Request.Builder();
        try {
            requestBuilder.url("ftp://hostname/path");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Expected URL scheme 'http' or 'https' but was 'ftp'", expected.getMessage());
        }
    }

    @Test
    public void invalidPort() throws Exception {
        Request.Builder requestBuilder = new Request.Builder();
        try {
            requestBuilder.url("http://localhost:65536/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Invalid URL port: \"65536\"", expected.getMessage());
        }
    }

    @Test
    public void getReturns500() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));
        executeSynchronously("/").assertCode(500).assertNotSuccessful();
    }

    @Test
    public void get_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        get();
    }

    @Test
    public void get_HTTPS() throws Exception {
        enableTls();
        get();
    }

    @Test
    public void repeatedHeaderNames() throws Exception {
        server.enqueue(new MockResponse().addHeader("B", "123").addHeader("B", "234"));
        executeSynchronously("/", "A", "345", "A", "456").assertCode(200).assertHeader("B", "123", "234");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(Arrays.asList("345", "456"), recordedRequest.getHeaders().values("A"));
    }

    @Test
    public void repeatedHeaderNames_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        repeatedHeaderNames();
    }

    @Test
    public void getWithRequestBody() throws Exception {
        server.enqueue(new MockResponse());
        try {
            new Request.Builder().method("GET", RequestBody.create(MediaType.get("text/plain"), "abc"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void head() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Type: text/plain"));
        Request request = new Request.Builder().url(server.url("/")).head().header("User-Agent", "SyncApiTest").build();
        executeSynchronously(request).assertCode(200).assertHeader("Content-Type", "text/plain");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("HEAD", recordedRequest.getMethod());
        Assert.assertEquals("SyncApiTest", recordedRequest.getHeader("User-Agent"));
        Assert.assertEquals(0, recordedRequest.getBody().size());
        Assert.assertNull(recordedRequest.getHeader("Content-Length"));
    }

    @Test
    public void headResponseContentLengthIsIgnored() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().addHeader("Content-Length", "100"));
        server.enqueue(new MockResponse().setBody("abc"));
        Request headRequest = new Request.Builder().url(server.url("/")).head().build();
        Response response = client.newCall(headRequest).execute();
        Assert.assertEquals(200, response.code());
        Assert.assertArrayEquals(new byte[0], response.body().bytes());
        Request getRequest = new Request.Builder().url(server.url("/")).build();
        executeSynchronously(getRequest).assertCode(200).assertBody("abc");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void headResponseContentEncodingIsIgnored() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().addHeader("Content-Encoding", "chunked"));
        server.enqueue(new MockResponse().setBody("abc"));
        Request headRequest = new Request.Builder().url(server.url("/")).head().build();
        executeSynchronously(headRequest).assertCode(200).assertHeader("Content-Encoding", "chunked").assertBody("");
        Request getRequest = new Request.Builder().url(server.url("/")).build();
        executeSynchronously(getRequest).assertCode(200).assertBody("abc");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void head_HTTPS() throws Exception {
        enableTls();
        head();
    }

    @Test
    public void head_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        head();
    }

    @Test
    public void post() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "def")).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("text/plain; charset=utf-8", recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void post_HTTPS() throws Exception {
        enableTls();
        post();
    }

    @Test
    public void post_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        post();
    }

    @Test
    public void postZeroLength() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).method("POST", RequestBody.create(null, new byte[0])).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
        Assert.assertEquals(0, recordedRequest.getBody().size());
        Assert.assertEquals("0", recordedRequest.getHeader("Content-Length"));
        Assert.assertNull(recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void postZerolength_HTTPS() throws Exception {
        enableTls();
        postZeroLength();
    }

    @Test
    public void postZerolength_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        postZeroLength();
    }

    @Test
    public void postBodyRetransmittedAfterAuthorizationFail() throws Exception {
        postBodyRetransmittedAfterAuthorizationFail("abc");
    }

    @Test
    public void postBodyRetransmittedAfterAuthorizationFail_HTTPS() throws Exception {
        enableTls();
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
    public void postEmptyBodyRetransmittedAfterAuthorizationFail_HTTPS() throws Exception {
        enableTls();
        postBodyRetransmittedAfterAuthorizationFail("");
    }

    @Test
    public void postEmptyBodyRetransmittedAfterAuthorizationFail_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        postBodyRetransmittedAfterAuthorizationFail("");
    }

    @Test
    public void attemptAuthorization20Times() throws Exception {
        for (int i = 0; i < 20; i++) {
            server.enqueue(new MockResponse().setResponseCode(401));
        }
        server.enqueue(new MockResponse().setBody("Success!"));
        String credential = Credentials.basic("jesse", "secret");
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(credential, null)).build();
        executeSynchronously("/").assertCode(200).assertBody("Success!");
    }

    @Test
    public void doesNotAttemptAuthorization21Times() throws Exception {
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(401));
        }
        String credential = Credentials.basic("jesse", "secret");
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(credential, null)).build();
        try {
            client.newCall(new Request.Builder().url(server.url("/0")).build()).execute();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("Too many follow-up requests: 21", expected.getMessage());
        }
    }

    /**
     * We had a bug where we were passing a null route to the authenticator.
     * https://github.com/square/okhttp/issues/3809
     */
    @Test
    public void authenticateWithNoConnection() throws Exception {
        server.enqueue(new MockResponse().addHeader("Connection: close").setResponseCode(401).setSocketPolicy(DISCONNECT_AT_END));
        RecordingOkAuthenticator authenticator = new RecordingOkAuthenticator(null, null);
        client = client.newBuilder().authenticator(authenticator).build();
        executeSynchronously("/").assertCode(401);
        Assert.assertNotNull(authenticator.onlyRoute());
    }

    @Test
    public void delete() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).delete().build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("DELETE", recordedRequest.getMethod());
        Assert.assertEquals(0, recordedRequest.getBody().size());
        Assert.assertEquals("0", recordedRequest.getHeader("Content-Length"));
        Assert.assertNull(recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void delete_HTTPS() throws Exception {
        enableTls();
        delete();
    }

    @Test
    public void delete_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        delete();
    }

    @Test
    public void deleteWithRequestBody() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).method("DELETE", RequestBody.create(MediaType.get("text/plain"), "def")).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("DELETE", recordedRequest.getMethod());
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void put() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).put(RequestBody.create(MediaType.get("text/plain"), "def")).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("PUT", recordedRequest.getMethod());
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("text/plain; charset=utf-8", recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void put_HTTPS() throws Exception {
        enableTls();
        put();
    }

    @Test
    public void put_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        put();
    }

    @Test
    public void patch() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).patch(RequestBody.create(MediaType.get("text/plain"), "def")).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("PATCH", recordedRequest.getMethod());
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("text/plain; charset=utf-8", recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void patch_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        patch();
    }

    @Test
    public void patch_HTTPS() throws Exception {
        enableTls();
        patch();
    }

    @Test
    public void customMethodWithBody() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).method("CUSTOM", RequestBody.create(MediaType.get("text/plain"), "def")).build();
        executeSynchronously(request).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("CUSTOM", recordedRequest.getMethod());
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("text/plain; charset=utf-8", recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void unspecifiedRequestBodyContentTypeDoesNotGetDefault() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).method("POST", RequestBody.create(null, "abc")).build();
        executeSynchronously(request).assertCode(200);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertNull(recordedRequest.getHeader("Content-Type"));
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("abc", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void illegalToExecuteTwice() throws Exception {
        server.enqueue(new MockResponse().setBody("abc").addHeader("Content-Type: text/plain"));
        Request request = new Request.Builder().url(server.url("/")).header("User-Agent", "SyncApiTest").build();
        Call call = client.newCall(request);
        Response response = call.execute();
        response.body().close();
        try {
            call.execute();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Already Executed", e.getMessage());
        }
        try {
            call.enqueue(callback);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Already Executed", e.getMessage());
        }
        Assert.assertEquals("SyncApiTest", server.takeRequest().getHeader("User-Agent"));
    }

    @Test
    public void illegalToExecuteTwice_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc").addHeader("Content-Type: text/plain"));
        Request request = new Request.Builder().url(server.url("/")).header("User-Agent", "SyncApiTest").build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        try {
            call.execute();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Already Executed", e.getMessage());
        }
        try {
            call.enqueue(callback);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Already Executed", e.getMessage());
        }
        Assert.assertEquals("SyncApiTest", server.takeRequest().getHeader("User-Agent"));
        callback.await(request.url()).assertSuccessful();
    }

    @Test
    public void legalToExecuteTwiceCloning() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        Response response1 = call.execute();
        Call cloned = call.clone();
        Response response2 = cloned.execute();
        Assert.assertEquals(response1.body().string(), "abc");
        Assert.assertEquals(response2.body().string(), "def");
    }

    @Test
    public void legalToExecuteTwiceCloning_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        Call cloned = call.clone();
        cloned.enqueue(callback);
        RecordedResponse firstResponse = callback.await(request.url()).assertSuccessful();
        RecordedResponse secondResponse = callback.await(request.url()).assertSuccessful();
        Set<String> bodies = new LinkedHashSet<>();
        bodies.add(firstResponse.getBody());
        bodies.add(secondResponse.getBody());
        Assert.assertTrue(bodies.contains("abc"));
        Assert.assertTrue(bodies.contains("def"));
    }

    @Test
    public void get_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc").addHeader("Content-Type: text/plain"));
        Request request = new Request.Builder().url(server.url("/")).header("User-Agent", "AsyncApiTest").build();
        client.newCall(request).enqueue(callback);
        callback.await(request.url()).assertCode(200).assertHeader("Content-Type", "text/plain").assertBody("abc");
        Assert.assertEquals("AsyncApiTest", server.takeRequest().getHeader("User-Agent"));
    }

    @Test
    public void exceptionThrownByOnResponseIsRedactedAndLogged() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/secret")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Assert.fail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                throw new IOException("a");
            }
        });
        Assert.assertEquals((("INFO: Callback failure for call to " + (server.url("/"))) + "..."), logHandler.take());
    }

    @Test
    public void connectionPooling() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        server.enqueue(new MockResponse().setBody("ghi"));
        executeSynchronously("/a").assertBody("abc");
        executeSynchronously("/b").assertBody("def");
        executeSynchronously("/c").assertBody("ghi");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void connectionPooling_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        server.enqueue(new MockResponse().setBody("ghi"));
        client.newCall(new Request.Builder().url(server.url("/a")).build()).enqueue(callback);
        callback.await(server.url("/a")).assertBody("abc");
        client.newCall(new Request.Builder().url(server.url("/b")).build()).enqueue(callback);
        callback.await(server.url("/b")).assertBody("def");
        client.newCall(new Request.Builder().url(server.url("/c")).build()).enqueue(callback);
        callback.await(server.url("/c")).assertBody("ghi");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void connectionReuseWhenResponseBodyConsumed_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        Request request = new Request.Builder().url(server.url("/a")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new AssertionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream bytes = response.body().byteStream();
                Assert.assertEquals('a', bytes.read());
                Assert.assertEquals('b', bytes.read());
                Assert.assertEquals('c', bytes.read());
                // This request will share a connection with 'A' cause it's all done.
                client.newCall(new Request.Builder().url(server.url("/b")).build()).enqueue(callback);
            }
        });
        callback.await(server.url("/b")).assertCode(200).assertBody("def");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Connection reuse!

    }

    @Test
    public void timeoutsUpdatedOnReusedConnections() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def").throttleBody(1, 750, TimeUnit.MILLISECONDS));
        // First request: time out after 1000ms.
        client = client.newBuilder().readTimeout(1000, TimeUnit.MILLISECONDS).build();
        executeSynchronously("/a").assertBody("abc");
        // Second request: time out after 250ms.
        client = client.newBuilder().readTimeout(250, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(server.url("/b")).build();
        Response response = client.newCall(request).execute();
        BufferedSource bodySource = response.body().source();
        Assert.assertEquals('d', bodySource.readByte());
        // The second byte of this request will be delayed by 750ms so we should time out after 250ms.
        long startNanos = System.nanoTime();
        try {
            bodySource.readByte();
            Assert.fail();
        } catch (IOException expected) {
            // Timed out as expected.
            long elapsedNanos = (System.nanoTime()) - startNanos;
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
            Assert.assertTrue(Util.format("Timed out: %sms", elapsedMillis), (elapsedMillis < 500));
        } finally {
            bodySource.close();
        }
    }

    /**
     * https://github.com/square/okhttp/issues/442
     */
    @Test
    public void tlsTimeoutsNotRetried() throws Exception {
        enableTls();
        server.enqueue(new MockResponse().setSocketPolicy(NO_RESPONSE));
        server.enqueue(new MockResponse().setBody("unreachable!"));
        client = client.newBuilder().readTimeout(100, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            // If this succeeds, too many requests were made.
            client.newCall(request).execute();
            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
    }

    /**
     * Make a request with two routes. The first route will time out because it's connecting to a
     * special address that never connects. The automatic retry will succeed.
     */
    @Test
    public void connectTimeoutsAttemptsAlternateRoute() throws Exception {
        RecordingProxySelector proxySelector = new RecordingProxySelector();
        proxySelector.proxies.add(new Proxy(HTTP, UNREACHABLE_ADDRESS));
        proxySelector.proxies.add(server.toProxyAddress());
        server.enqueue(new MockResponse().setBody("success!"));
        client = client.newBuilder().proxySelector(proxySelector).readTimeout(100, TimeUnit.MILLISECONDS).connectTimeout(100, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url("http://android.com/").build();
        executeSynchronously(request).assertCode(200).assertBody("success!");
    }

    /**
     * Make a request with two routes. The first route will fail because the null server connects but
     * never responds. The manual retry will succeed.
     */
    @Test
    public void readTimeoutFails() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(STALL_SOCKET_AT_START));
        server2.enqueue(new MockResponse().setBody("success!"));
        RecordingProxySelector proxySelector = new RecordingProxySelector();
        proxySelector.proxies.add(server.toProxyAddress());
        proxySelector.proxies.add(server2.toProxyAddress());
        client = client.newBuilder().proxySelector(proxySelector).readTimeout(100, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url("http://android.com/").build();
        executeSynchronously(request).assertFailure(SocketTimeoutException.class);
        executeSynchronously(request).assertCode(200).assertBody("success!");
    }

    /**
     * https://github.com/square/okhttp/issues/1801
     */
    @Test
    public void asyncCallEngineInitialized() throws Exception {
        OkHttpClient c = clientTestRule.client.newBuilder().addInterceptor(( chain) -> {
            throw new IOException();
        }).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        c.newCall(request).enqueue(callback);
        RecordedResponse response = callback.await(request.url());
        Assert.assertEquals(request, response.request);
    }

    @Test
    public void reusedSinksGetIndependentTimeoutInstances() throws Exception {
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        // Call 1: set a deadline on the request body.
        RequestBody requestBody1 = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("abc");
                sink.timeout().deadline(5, TimeUnit.SECONDS);
            }
        };
        Request request1 = new Request.Builder().url(server.url("/")).method("POST", requestBody1).build();
        Response response1 = client.newCall(request1).execute();
        Assert.assertEquals(200, response1.code());
        // Call 2: check for the absence of a deadline on the request body.
        RequestBody requestBody2 = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Assert.assertFalse(sink.timeout().hasDeadline());
                sink.writeUtf8("def");
            }
        };
        Request request2 = new Request.Builder().url(server.url("/")).method("POST", requestBody2).build();
        Response response2 = client.newCall(request2).execute();
        Assert.assertEquals(200, response2.code());
        // Use sequence numbers to confirm the connection was pooled.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void reusedSourcesGetIndependentTimeoutInstances() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        // Call 1: set a deadline on the response body.
        Request request1 = new Request.Builder().url(server.url("/")).build();
        Response response1 = client.newCall(request1).execute();
        BufferedSource body1 = response1.body().source();
        Assert.assertEquals("abc", body1.readUtf8());
        body1.timeout().deadline(5, TimeUnit.SECONDS);
        // Call 2: check for the absence of a deadline on the request body.
        Request request2 = new Request.Builder().url(server.url("/")).build();
        Response response2 = client.newCall(request2).execute();
        BufferedSource body2 = response2.body().source();
        Assert.assertEquals("def", body2.readUtf8());
        Assert.assertFalse(body2.timeout().hasDeadline());
        // Use sequence numbers to confirm the connection was pooled.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void tls() throws Exception {
        enableTls();
        server.enqueue(new MockResponse().setBody("abc").addHeader("Content-Type: text/plain"));
        executeSynchronously("/").assertHandshake();
    }

    @Test
    public void tls_Async() throws Exception {
        enableTls();
        server.enqueue(new MockResponse().setBody("abc").addHeader("Content-Type: text/plain"));
        Request request = new Request.Builder().url(server.url("/")).build();
        client.newCall(request).enqueue(callback);
        callback.await(request.url()).assertHandshake();
    }

    @Test
    public void recoverWhenRetryOnConnectionFailureIsTrue() throws Exception {
        server.enqueue(new MockResponse().setBody("seed connection pool"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));
        server.enqueue(new MockResponse().setBody("retry success"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        Assert.assertTrue(client.retryOnConnectionFailure());
        executeSynchronously("/").assertBody("seed connection pool");
        executeSynchronously("/").assertBody("retry success");
        // The call that seeds the connection pool.
        listener.removeUpToEvent(RecordingEventListener.CallEnd.class);
        // The ResponseFailed event is not necessarily fatal!
        listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        listener.removeUpToEvent(RecordingEventListener.ResponseFailed.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectionReleased.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectionAcquired.class);
        listener.removeUpToEvent(RecordingEventListener.ConnectionReleased.class);
        listener.removeUpToEvent(RecordingEventListener.CallEnd.class);
    }

    @Test
    public void recoverWhenRetryOnConnectionFailureIsTrue_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        recoverWhenRetryOnConnectionFailureIsTrue();
    }

    @Test
    public void noRecoverWhenRetryOnConnectionFailureIsFalse() throws Exception {
        server.enqueue(new MockResponse().setBody("seed connection pool"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));
        server.enqueue(new MockResponse().setBody("unreachable!"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).retryOnConnectionFailure(false).build();
        executeSynchronously("/").assertBody("seed connection pool");
        // If this succeeds, too many requests were made.
        executeSynchronously("/").assertFailure(IOException.class).assertFailureMatches("stream was reset: CANCEL", ("unexpected end of stream on " + (server.url("/").redact())));
    }

    @Test
    public void recoverWhenRetryOnConnectionFailureIsFalse_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        noRecoverWhenRetryOnConnectionFailureIsFalse();
    }

    @Test
    public void tlsHandshakeFailure_noFallbackByDefault() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("response that will never be received"));
        RecordedResponse response = executeSynchronously("/");
        // JDK 11 response to the FAIL_HANDSHAKE
        // RI response to the FAIL_HANDSHAKE
        // Android's response to the FAIL_HANDSHAKE
        response.assertFailure(SSLException.class, SSLProtocolException.class, SSLHandshakeException.class);
        Assert.assertFalse(client.connectionSpecs().contains(COMPATIBLE_TLS));
    }

    @Test
    public void recoverFromTlsHandshakeFailure() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("abc"));
        client = // Attempt RESTRICTED_TLS then fall back to MODERN_TLS.
        client.newBuilder().hostnameVerifier(new RecordingHostnameVerifier()).connectionSpecs(Arrays.asList(RESTRICTED_TLS, MODERN_TLS)).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        executeSynchronously("/").assertBody("abc");
    }

    @Test
    public void recoverFromTlsHandshakeFailure_tlsFallbackScsvEnabled() throws Exception {
        final String tlsFallbackScsv = "TLS_FALLBACK_SCSV";
        List<String> supportedCiphers = Arrays.asList(handshakeCertificates.sslSocketFactory().getSupportedCipherSuites());
        if (!(supportedCiphers.contains(tlsFallbackScsv))) {
            // This only works if the client socket supports TLS_FALLBACK_SCSV.
            return;
        }
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        CallTest.RecordingSSLSocketFactory clientSocketFactory = new CallTest.RecordingSSLSocketFactory(handshakeCertificates.sslSocketFactory());
        client = // Attempt RESTRICTED_TLS then fall back to MODERN_TLS.
        client.newBuilder().sslSocketFactory(clientSocketFactory, handshakeCertificates.trustManager()).connectionSpecs(Arrays.asList(RESTRICTED_TLS, MODERN_TLS)).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (SSLHandshakeException expected) {
        }
        List<SSLSocket> clientSockets = clientSocketFactory.getSocketsCreated();
        SSLSocket firstSocket = clientSockets.get(0);
        Assert.assertFalse(Arrays.asList(firstSocket.getEnabledCipherSuites()).contains(tlsFallbackScsv));
        SSLSocket secondSocket = clientSockets.get(1);
        Assert.assertTrue(Arrays.asList(secondSocket.getEnabledCipherSuites()).contains(tlsFallbackScsv));
    }

    @Test
    public void recoverFromTlsHandshakeFailure_Async() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        server.enqueue(new MockResponse().setBody("abc"));
        client = // Attempt RESTRICTED_TLS then fall back to MODERN_TLS.
        client.newBuilder().hostnameVerifier(new RecordingHostnameVerifier()).connectionSpecs(Arrays.asList(RESTRICTED_TLS, MODERN_TLS)).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        client.newCall(request).enqueue(callback);
        callback.await(request.url()).assertBody("abc");
    }

    @Test
    public void noRecoveryFromTlsHandshakeFailureWhenTlsFallbackIsDisabled() throws Exception {
        client = client.newBuilder().connectionSpecs(Arrays.asList(MODERN_TLS, CLEARTEXT)).hostnameVerifier(new RecordingHostnameVerifier()).sslSocketFactory(suppressTlsFallbackClientSocketFactory(), handshakeCertificates.trustManager()).build();
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(FAIL_HANDSHAKE));
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (SSLProtocolException expected) {
            // RI response to the FAIL_HANDSHAKE
        } catch (SSLHandshakeException expected) {
            // Android's response to the FAIL_HANDSHAKE
        } catch (SSLException expected) {
            // JDK 11 response to the FAIL_HANDSHAKE
            String jvmVersion = System.getProperty("java.specification.version");
            Assert.assertEquals("11", jvmVersion);
        }
    }

    @Test
    public void tlsHostnameVerificationFailure() throws Exception {
        server.enqueue(new MockResponse());
        HeldCertificate serverCertificate = // Unusued for hostname verification.
        new HeldCertificate.Builder().commonName("localhost").addSubjectAlternativeName("wronghostname").build();
        HandshakeCertificates serverCertificates = new HandshakeCertificates.Builder().heldCertificate(serverCertificate).build();
        HandshakeCertificates clientCertificates = new HandshakeCertificates.Builder().addTrustedCertificate(serverCertificate.certificate()).build();
        client = client.newBuilder().sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager()).build();
        server.useHttps(serverCertificates.sslSocketFactory(), false);
        executeSynchronously("/").assertFailureMatches("(?s)Hostname localhost not verified.*");
    }

    /**
     * Anonymous cipher suites were disabled in OpenJDK because they're rarely used and permit
     * man-in-the-middle attacks. https://bugs.openjdk.java.net/browse/JDK-8212823
     */
    @Test
    public void anonCipherSuiteUnsupported() throws Exception {
        // The _anon_ suites became unsupported in "1.8.0_201" and "11.0.2".
        Assume.assumeFalse(System.getProperty("java.version", "unknown").matches("1\\.8\\.0_1\\d\\d"));
        Assume.assumeFalse(System.getProperty("java.version", "unknown").matches("11"));
        server.enqueue(new MockResponse());
        CipherSuite cipherSuite = CipherSuite.TLS_DH_anon_WITH_AES_128_GCM_SHA256;
        HandshakeCertificates clientCertificates = new HandshakeCertificates.Builder().build();
        client = client.newBuilder().sslSocketFactory(socketFactoryWithCipherSuite(clientCertificates.sslSocketFactory(), cipherSuite), clientCertificates.trustManager()).connectionSpecs(Arrays.asList(new ConnectionSpec.Builder(MODERN_TLS).cipherSuites(cipherSuite).build())).build();
        HandshakeCertificates serverCertificates = new HandshakeCertificates.Builder().build();
        server.useHttps(socketFactoryWithCipherSuite(serverCertificates.sslSocketFactory(), cipherSuite), false);
        executeSynchronously("/").assertFailure(SSLHandshakeException.class);
    }

    @Test
    public void cleartextCallsFailWhenCleartextIsDisabled() throws Exception {
        // Configure the client with only TLS configurations. No cleartext!
        client = client.newBuilder().connectionSpecs(Arrays.asList(MODERN_TLS, COMPATIBLE_TLS)).build();
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (UnknownServiceException expected) {
            Assert.assertEquals("CLEARTEXT communication not enabled for client", expected.getMessage());
        }
    }

    @Test
    public void httpsCallsFailWhenProtocolIsH2PriorKnowledge() throws Exception {
        client = client.newBuilder().protocols(Collections.singletonList(H2_PRIOR_KNOWLEDGE)).build();
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        try {
            call.execute();
            Assert.fail();
        } catch (UnknownServiceException expected) {
            Assert.assertEquals("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS", expected.getMessage());
        }
    }

    @Test
    public void setFollowSslRedirectsFalse() throws Exception {
        enableTls();
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location: http://square.com"));
        client = client.newBuilder().followSslRedirects(false).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(301, response.code());
        response.body().close();
    }

    @Test
    public void matchingPinnedCertificate() throws Exception {
        // TODO https://github.com/square/okhttp/issues/4598
        // java.util.NoSuchElementException
        // at java.base/java.util.ArrayDeque.removeFirst(ArrayDeque.java:363)
        // at okhttp3.internal.tls.BasicCertificateChainCleaner.clean(BasicCertificateChainCleaner.java:58)
        // at okhttp3.CertificatePinner.check(CertificatePinner.java:166)
        Assume.assumeFalse(PlatformTest.getJvmSpecVersion().equals("11"));
        enableTls();
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        // Make a first request without certificate pinning. Use it to collect certificates to pin.
        Request request1 = new Request.Builder().url(server.url("/")).build();
        Response response1 = client.newCall(request1).execute();
        CertificatePinner.Builder certificatePinnerBuilder = new CertificatePinner.Builder();
        for (Certificate certificate : response1.handshake().peerCertificates()) {
            certificatePinnerBuilder.add(server.getHostName(), CertificatePinner.pin(certificate));
        }
        response1.body().close();
        // Make another request with certificate pinning. It should complete normally.
        client = client.newBuilder().certificatePinner(certificatePinnerBuilder.build()).build();
        Request request2 = new Request.Builder().url(server.url("/")).build();
        Response response2 = client.newCall(request2).execute();
        Assert.assertNotSame(response2.handshake(), response1.handshake());
        response2.body().close();
    }

    @Test
    public void unmatchingPinnedCertificate() throws Exception {
        enableTls();
        server.enqueue(new MockResponse());
        // Pin publicobject.com's cert.
        client = client.newBuilder().certificatePinner(new CertificatePinner.Builder().add(server.getHostName(), "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=").build()).build();
        // When we pin the wrong certificate, connectivity fails.
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (SSLPeerUnverifiedException expected) {
            Assert.assertTrue(expected.getMessage().startsWith("Certificate pinning failure!"));
        }
    }

    @Test
    public void post_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "def")).build();
        client.newCall(request).enqueue(callback);
        callback.await(request.url()).assertCode(200).assertBody("abc");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("def", recordedRequest.getBody().readUtf8());
        Assert.assertEquals("3", recordedRequest.getHeader("Content-Length"));
        Assert.assertEquals("text/plain; charset=utf-8", recordedRequest.getHeader("Content-Type"));
    }

    @Test
    public void postBodyRetransmittedOnFailureRecovery() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));
        server.enqueue(new MockResponse().setBody("def"));
        // Seed the connection pool so we have something that can fail.
        Request request1 = new Request.Builder().url(server.url("/")).build();
        Response response1 = client.newCall(request1).execute();
        Assert.assertEquals("abc", response1.body().string());
        Request request2 = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "body!")).build();
        Response response2 = client.newCall(request2).execute();
        Assert.assertEquals("def", response2.body().string());
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
    public void postBodyRetransmittedOnFailureRecovery_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        postBodyRetransmittedOnFailureRecovery();
    }

    @Test
    public void cacheHit() throws Exception {
        server.enqueue(new MockResponse().addHeader("ETag: v1").addHeader("Cache-Control: max-age=60").addHeader("Vary: Accept-Charset").setBody("A"));
        client = client.newBuilder().cache(cache).build();
        // Store a response in the cache.
        HttpUrl url = server.url("/");
        long request1SentAt = System.currentTimeMillis();
        executeSynchronously("/", "Accept-Language", "fr-CA", "Accept-Charset", "UTF-8").assertCode(200).assertBody("A");
        long request1ReceivedAt = System.currentTimeMillis();
        Assert.assertNull(server.takeRequest().getHeader("If-None-Match"));
        // Hit that stored response. It's different, but Vary says it doesn't matter.
        Thread.sleep(10);// Make sure the timestamps are unique.

        RecordedResponse cacheHit = executeSynchronously("/", "Accept-Language", "en-US", "Accept-Charset", "UTF-8");
        // Check the merged response. The request is the application's original request.
        cacheHit.assertCode(200).assertBody("A").assertHeaders(new Headers.Builder().add("ETag", "v1").add("Cache-Control", "max-age=60").add("Vary", "Accept-Charset").add("Content-Length", "1").build()).assertRequestUrl(url).assertRequestHeader("Accept-Language", "en-US").assertRequestHeader("Accept-Charset", "UTF-8").assertSentRequestAtMillis(request1SentAt, request1ReceivedAt).assertReceivedResponseAtMillis(request1SentAt, request1ReceivedAt);
        // Check the cached response. Its request contains only the saved Vary headers.
        cacheHit.cacheResponse().assertCode(200).assertHeaders(new Headers.Builder().add("ETag", "v1").add("Cache-Control", "max-age=60").add("Vary", "Accept-Charset").add("Content-Length", "1").build()).assertRequestMethod("GET").assertRequestUrl(url).assertRequestHeader("Accept-Language").assertRequestHeader("Accept-Charset", "UTF-8").assertSentRequestAtMillis(request1SentAt, request1ReceivedAt).assertReceivedResponseAtMillis(request1SentAt, request1ReceivedAt);
        cacheHit.assertNoNetworkResponse();
    }

    @Test
    public void conditionalCacheHit() throws Exception {
        server.enqueue(new MockResponse().addHeader("ETag: v1").addHeader("Vary: Accept-Charset").addHeader("Donut: a").setBody("A"));
        server.enqueue(new MockResponse().clearHeaders().addHeader("Donut: b").setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED));
        client = client.newBuilder().cache(cache).build();
        // Store a response in the cache.
        long request1SentAt = System.currentTimeMillis();
        executeSynchronously("/", "Accept-Language", "fr-CA", "Accept-Charset", "UTF-8").assertCode(200).assertHeader("Donut", "a").assertBody("A");
        long request1ReceivedAt = System.currentTimeMillis();
        Assert.assertNull(server.takeRequest().getHeader("If-None-Match"));
        // Hit that stored response. It's different, but Vary says it doesn't matter.
        Thread.sleep(10);// Make sure the timestamps are unique.

        long request2SentAt = System.currentTimeMillis();
        RecordedResponse cacheHit = executeSynchronously("/", "Accept-Language", "en-US", "Accept-Charset", "UTF-8");
        long request2ReceivedAt = System.currentTimeMillis();
        Assert.assertEquals("v1", server.takeRequest().getHeader("If-None-Match"));
        // Check the merged response. The request is the application's original request.
        // No If-None-Match on the user's request.
        cacheHit.assertCode(200).assertBody("A").assertHeader("Donut", "b").assertRequestUrl(server.url("/")).assertRequestHeader("Accept-Language", "en-US").assertRequestHeader("Accept-Charset", "UTF-8").assertRequestHeader("If-None-Match").assertSentRequestAtMillis(request2SentAt, request2ReceivedAt).assertReceivedResponseAtMillis(request2SentAt, request2ReceivedAt);
        // Check the cached response. Its request contains only the saved Vary headers.
        // This wasn't present in the original request.
        // Because of Vary on Accept-Charset.
        // No Vary on Accept-Language.
        cacheHit.cacheResponse().assertCode(200).assertHeader("Donut", "a").assertHeader("ETag", "v1").assertRequestUrl(server.url("/")).assertRequestHeader("Accept-Language").assertRequestHeader("Accept-Charset", "UTF-8").assertRequestHeader("If-None-Match").assertSentRequestAtMillis(request1SentAt, request1ReceivedAt).assertReceivedResponseAtMillis(request1SentAt, request1ReceivedAt);
        // Check the network response. It has the caller's request, plus some caching headers.
        // If-None-Match in the validation request.
        cacheHit.networkResponse().assertCode(304).assertHeader("Donut", "b").assertRequestHeader("Accept-Language", "en-US").assertRequestHeader("Accept-Charset", "UTF-8").assertRequestHeader("If-None-Match", "v1").assertSentRequestAtMillis(request2SentAt, request2ReceivedAt).assertReceivedResponseAtMillis(request2SentAt, request2ReceivedAt);
    }

    @Test
    public void conditionalCacheHit_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("A").addHeader("ETag: v1"));
        server.enqueue(new MockResponse().clearHeaders().setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED));
        client = client.newBuilder().cache(cache).build();
        Request request1 = new Request.Builder().url(server.url("/")).build();
        client.newCall(request1).enqueue(callback);
        callback.await(request1.url()).assertCode(200).assertBody("A");
        Assert.assertNull(server.takeRequest().getHeader("If-None-Match"));
        Request request2 = new Request.Builder().url(server.url("/")).build();
        client.newCall(request2).enqueue(callback);
        callback.await(request2.url()).assertCode(200).assertBody("A");
        Assert.assertEquals("v1", server.takeRequest().getHeader("If-None-Match"));
    }

    @Test
    public void conditionalCacheMiss() throws Exception {
        server.enqueue(new MockResponse().addHeader("ETag: v1").addHeader("Vary: Accept-Charset").addHeader("Donut: a").setBody("A"));
        server.enqueue(new MockResponse().addHeader("Donut: b").setBody("B"));
        client = client.newBuilder().cache(cache).build();
        long request1SentAt = System.currentTimeMillis();
        executeSynchronously("/", "Accept-Language", "fr-CA", "Accept-Charset", "UTF-8").assertCode(200).assertBody("A");
        long request1ReceivedAt = System.currentTimeMillis();
        Assert.assertNull(server.takeRequest().getHeader("If-None-Match"));
        // Different request, but Vary says it doesn't matter.
        Thread.sleep(10);// Make sure the timestamps are unique.

        long request2SentAt = System.currentTimeMillis();
        RecordedResponse cacheMiss = executeSynchronously("/", "Accept-Language", "en-US", "Accept-Charset", "UTF-8");
        long request2ReceivedAt = System.currentTimeMillis();
        Assert.assertEquals("v1", server.takeRequest().getHeader("If-None-Match"));
        // Check the user response. It has the application's original request.
        cacheMiss.assertCode(200).assertBody("B").assertHeader("Donut", "b").assertRequestUrl(server.url("/")).assertSentRequestAtMillis(request2SentAt, request2ReceivedAt).assertReceivedResponseAtMillis(request2SentAt, request2ReceivedAt);
        // Check the cache response. Even though it's a miss, we used the cache.
        cacheMiss.cacheResponse().assertCode(200).assertHeader("Donut", "a").assertHeader("ETag", "v1").assertRequestUrl(server.url("/")).assertSentRequestAtMillis(request1SentAt, request1ReceivedAt).assertReceivedResponseAtMillis(request1SentAt, request1ReceivedAt);
        // Check the network response. It has the network request, plus caching headers.
        // If-None-Match in the validation request.
        cacheMiss.networkResponse().assertCode(200).assertHeader("Donut", "b").assertRequestHeader("If-None-Match", "v1").assertRequestUrl(server.url("/")).assertSentRequestAtMillis(request2SentAt, request2ReceivedAt).assertReceivedResponseAtMillis(request2SentAt, request2ReceivedAt);
    }

    @Test
    public void conditionalCacheMiss_Async() throws Exception {
        server.enqueue(new MockResponse().setBody("A").addHeader("ETag: v1"));
        server.enqueue(new MockResponse().setBody("B"));
        client = client.newBuilder().cache(cache).build();
        Request request1 = new Request.Builder().url(server.url("/")).build();
        client.newCall(request1).enqueue(callback);
        callback.await(request1.url()).assertCode(200).assertBody("A");
        Assert.assertNull(server.takeRequest().getHeader("If-None-Match"));
        Request request2 = new Request.Builder().url(server.url("/")).build();
        client.newCall(request2).enqueue(callback);
        callback.await(request2.url()).assertCode(200).assertBody("B");
        Assert.assertEquals("v1", server.takeRequest().getHeader("If-None-Match"));
    }

    @Test
    public void onlyIfCachedReturns504WhenNotCached() throws Exception {
        executeSynchronously("/", "Cache-Control", "only-if-cached").assertCode(504).assertBody("").assertNoNetworkResponse().assertNoCacheResponse();
    }

    @Test
    public void networkDropsOnConditionalGet() throws IOException {
        client = client.newBuilder().cache(cache).build();
        // Seed the cache.
        server.enqueue(new MockResponse().addHeader("ETag: v1").setBody("A"));
        executeSynchronously("/").assertCode(200).assertBody("A");
        // Attempt conditional cache validation and a DNS miss.
        client = client.newBuilder().dns(new FakeDns()).build();
        executeSynchronously("/").assertFailure(UnknownHostException.class);
    }

    @Test
    public void redirect() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location: /b").addHeader("Test", "Redirect from /a to /b").setBody("/a has moved!"));
        server.enqueue(new MockResponse().setResponseCode(302).addHeader("Location: /c").addHeader("Test", "Redirect from /b to /c").setBody("/b has moved!"));
        server.enqueue(new MockResponse().setBody("C"));
        executeSynchronously("/a").assertCode(200).assertBody("C").priorResponse().assertCode(302).assertHeader("Test", "Redirect from /b to /c").priorResponse().assertCode(301).assertHeader("Test", "Redirect from /a to /b");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Connection reused.

        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());// Connection reused again!

    }

    @Test
    public void postRedirectsToGet() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /page2").setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Page 2"));
        Response response = client.newCall(new Request.Builder().url(server.url("/page1")).post(RequestBody.create(MediaType.get("text/plain"), "Request Body")).build()).execute();
        Assert.assertEquals("Page 2", response.body().string());
        RecordedRequest page1 = server.takeRequest();
        Assert.assertEquals("POST /page1 HTTP/1.1", page1.getRequestLine());
        Assert.assertEquals("Request Body", page1.getBody().readUtf8());
        RecordedRequest page2 = server.takeRequest();
        Assert.assertEquals("GET /page2 HTTP/1.1", page2.getRequestLine());
    }

    @Test
    public void getClientRequestTimeout() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setBody("You took too long!"));
        server.enqueue(new MockResponse().setBody("Body"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("Body", response.body().string());
    }

    @Test
    public void getClientRequestTimeoutWithBackPressure() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setHeader("Retry-After", "1").setBody("You took too long!"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("You took too long!", response.body().string());
    }

    @Test
    public void requestBodyRetransmittedOnClientRequestTimeout() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setBody("You took too long!"));
        server.enqueue(new MockResponse().setBody("Body"));
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "Hello")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("Body", response.body().string());
        RecordedRequest request1 = server.takeRequest();
        Assert.assertEquals("Hello", request1.getBody().readUtf8());
        RecordedRequest request2 = server.takeRequest();
        Assert.assertEquals("Hello", request2.getBody().readUtf8());
    }

    @Test
    public void disableClientRequestTimeoutRetry() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setBody("You took too long!"));
        client = client.newBuilder().retryOnConnectionFailure(false).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(408, response.code());
        Assert.assertEquals("You took too long!", response.body().string());
    }

    @Test
    public void maxClientRequestTimeoutRetries() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setBody("You took too long!"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(408).setHeader("Connection", "Close").setBody("You took too long!"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(408, response.code());
        Assert.assertEquals("You took too long!", response.body().string());
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void maxUnavailableTimeoutRetries() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(503).setHeader("Connection", "Close").setHeader("Retry-After", "0").setBody("You took too long!"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(503).setHeader("Connection", "Close").setHeader("Retry-After", "0").setBody("You took too long!"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(503, response.code());
        Assert.assertEquals("You took too long!", response.body().string());
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void retryOnUnavailableWith0RetryAfter() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setResponseCode(503).setHeader("Connection", "Close").setHeader("Retry-After", "0").setBody("You took too long!"));
        server.enqueue(new MockResponse().setBody("Body"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("Body", response.body().string());
    }

    @Test
    public void canRetryNormalRequestBody() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503).setHeader("Retry-After", "0").setBody("please retry"));
        server.enqueue(new MockResponse().setBody("thank you for retrying"));
        Request request = new Request.Builder().url(server.url("/")).post(new RequestBody() {
            int attempt = 0;

            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(("attempt " + ((attempt)++)));
            }
        }).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("thank you for retrying", response.body().string());
        Assert.assertEquals("attempt 0", server.takeRequest().getBody().readUtf8());
        Assert.assertEquals("attempt 1", server.takeRequest().getBody().readUtf8());
        Assert.assertEquals(2, server.getRequestCount());
    }

    @Test
    public void cannotRetryOneShotRequestBody() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503).setHeader("Retry-After", "0").setBody("please retry"));
        server.enqueue(new MockResponse().setBody("thank you for retrying"));
        Request request = new Request.Builder().url(server.url("/")).post(new RequestBody() {
            int attempt = 0;

            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(("attempt " + ((attempt)++)));
            }

            @Override
            public boolean isOneShot() {
                return true;
            }
        }).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(503, response.code());
        Assert.assertEquals("please retry", response.body().string());
        Assert.assertEquals("attempt 0", server.takeRequest().getBody().readUtf8());
        Assert.assertEquals(1, server.getRequestCount());
    }

    @Test
    public void propfindRedirectsToPropfindAndMaintainsRequestBody() throws Exception {
        // given
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /page2").setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Page 2"));
        // when
        Response response = client.newCall(new Request.Builder().url(server.url("/page1")).method("PROPFIND", RequestBody.create(MediaType.get("text/plain"), "Request Body")).build()).execute();
        // then
        Assert.assertEquals("Page 2", response.body().string());
        RecordedRequest page1 = server.takeRequest();
        Assert.assertEquals("PROPFIND /page1 HTTP/1.1", page1.getRequestLine());
        Assert.assertEquals("Request Body", page1.getBody().readUtf8());
        RecordedRequest page2 = server.takeRequest();
        Assert.assertEquals("PROPFIND /page2 HTTP/1.1", page2.getRequestLine());
        Assert.assertEquals("Request Body", page2.getBody().readUtf8());
    }

    @Test
    public void responseCookies() throws Exception {
        server.enqueue(new MockResponse().addHeader("Set-Cookie", "a=b; Expires=Thu, 01 Jan 1970 00:00:00 GMT").addHeader("Set-Cookie", "c=d; Expires=Fri, 02 Jan 1970 23:59:59 GMT; path=/bar; secure"));
        RecordingCookieJar cookieJar = new RecordingCookieJar();
        client = client.newBuilder().cookieJar(cookieJar).build();
        executeSynchronously("/").assertCode(200);
        List<Cookie> responseCookies = cookieJar.takeResponseCookies();
        Assert.assertEquals(2, responseCookies.size());
        Assert.assertEquals("a=b; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/", responseCookies.get(0).toString());
        Assert.assertEquals("c=d; expires=Fri, 02 Jan 1970 23:59:59 GMT; path=/bar; secure", responseCookies.get(1).toString());
    }

    @Test
    public void requestCookies() throws Exception {
        server.enqueue(new MockResponse());
        RecordingCookieJar cookieJar = new RecordingCookieJar();
        cookieJar.enqueueRequestCookies(new Cookie.Builder().name("a").value("b").domain(server.getHostName()).build(), new Cookie.Builder().name("c").value("d").domain(server.getHostName()).build());
        client = client.newBuilder().cookieJar(cookieJar).build();
        executeSynchronously("/").assertCode(200);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("a=b; c=d", recordedRequest.getHeader("Cookie"));
    }

    @Test
    public void redirectsDoNotIncludeTooManyCookies() throws Exception {
        server2.enqueue(new MockResponse().setBody("Page 2"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader(("Location: " + (server2.url("/")))));
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        HttpCookie cookie = new HttpCookie("c", "cookie");
        cookie.setDomain(server.getHostName());
        cookie.setPath("/");
        String portList = Integer.toString(server.getPort());
        cookie.setPortlist(portList);
        cookieManager.getCookieStore().add(server.url("/").uri(), cookie);
        client = client.newBuilder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
        Response response = client.newCall(new Request.Builder().url(server.url("/page1")).build()).execute();
        Assert.assertEquals("Page 2", response.body().string());
        RecordedRequest request1 = server.takeRequest();
        Assert.assertEquals("c=cookie", request1.getHeader("Cookie"));
        RecordedRequest request2 = server2.takeRequest();
        Assert.assertNull(request2.getHeader("Cookie"));
    }

    @Test
    public void redirectsDoNotIncludeTooManyAuthHeaders() throws Exception {
        server2.enqueue(new MockResponse().setBody("Page 2"));
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setResponseCode(302).addHeader(("Location: " + (server2.url("/b")))));
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(Credentials.basic("jesse", "secret"), null)).build();
        Request request = new Request.Builder().url(server.url("/a")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("Page 2", response.body().string());
        RecordedRequest redirectRequest = server2.takeRequest();
        Assert.assertNull(redirectRequest.getHeader("Authorization"));
        Assert.assertEquals("/b", redirectRequest.getPath());
    }

    @Test
    public void redirect_Async() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location: /b").addHeader("Test", "Redirect from /a to /b").setBody("/a has moved!"));
        server.enqueue(new MockResponse().setResponseCode(302).addHeader("Location: /c").addHeader("Test", "Redirect from /b to /c").setBody("/b has moved!"));
        server.enqueue(new MockResponse().setBody("C"));
        Request request = new Request.Builder().url(server.url("/a")).build();
        client.newCall(request).enqueue(callback);
        callback.await(server.url("/a")).assertCode(200).assertBody("C").priorResponse().assertCode(302).assertHeader("Test", "Redirect from /b to /c").priorResponse().assertCode(301).assertHeader("Test", "Redirect from /a to /b");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Connection reused.

        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());// Connection reused again!

    }

    @Test
    public void follow20Redirects() throws Exception {
        for (int i = 0; i < 20; i++) {
            server.enqueue(new MockResponse().setResponseCode(301).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        server.enqueue(new MockResponse().setBody("Success!"));
        executeSynchronously("/0").assertCode(200).assertBody("Success!");
    }

    @Test
    public void follow20Redirects_Async() throws Exception {
        for (int i = 0; i < 20; i++) {
            server.enqueue(new MockResponse().setResponseCode(301).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        server.enqueue(new MockResponse().setBody("Success!"));
        Request request = new Request.Builder().url(server.url("/0")).build();
        client.newCall(request).enqueue(callback);
        callback.await(server.url("/0")).assertCode(200).assertBody("Success!");
    }

    @Test
    public void doesNotFollow21Redirects() throws Exception {
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(301).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        try {
            client.newCall(new Request.Builder().url(server.url("/0")).build()).execute();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("Too many follow-up requests: 21", expected.getMessage());
        }
    }

    @Test
    public void doesNotFollow21Redirects_Async() throws Exception {
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(301).addHeader(("Location: /" + (i + 1))).setBody(("Redirecting to /" + (i + 1))));
        }
        Request request = new Request.Builder().url(server.url("/0")).build();
        client.newCall(request).enqueue(callback);
        callback.await(server.url("/0")).assertFailure("Too many follow-up requests: 21");
    }

    @Test
    public void http204WithBodyDisallowed() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(204).setBody("I'm not even supposed to be here today."));
        executeSynchronously("/").assertFailure("HTTP 204 had non-zero Content-Length: 39");
    }

    @Test
    public void http205WithBodyDisallowed() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(205).setBody("I'm not even supposed to be here today."));
        executeSynchronously("/").assertFailure("HTTP 205 had non-zero Content-Length: 39");
    }

    @Test
    public void httpWithExcessiveHeaders() throws IOException {
        String longLine = ("HTTP/1.1 200 " + (stringFill('O', (256 * 1024)))) + "K";
        server.setProtocols(Collections.singletonList(HTTP_1_1));
        server.enqueue(new MockResponse().setStatus(longLine).setBody("I'm not even supposed to be here today."));
        executeSynchronously("/").assertFailureMatches((".*unexpected end of stream on " + (server.url("/").redact())));
    }

    @Test
    public void canceledBeforeExecute() throws Exception {
        Call call = client.newCall(new Request.Builder().url(server.url("/a")).build());
        call.cancel();
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        Assert.assertEquals(0, server.getRequestCount());
    }

    @Test
    public void cancelDuringHttpConnect() throws Exception {
        cancelDuringConnect("http");
    }

    @Test
    public void cancelDuringHttpsConnect() throws Exception {
        cancelDuringConnect("https");
    }

    @Test
    public void cancelImmediatelyAfterEnqueue() throws Exception {
        server.enqueue(new MockResponse());
        final CountDownLatch latch = new CountDownLatch(1);
        client = client.newBuilder().addNetworkInterceptor(( chain) -> {
            try {
                latch.await();
            } catch ( e) {
                throw new <e>AssertionError();
            }
            return chain.proceed(chain.request());
        }).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/a")).build());
        call.enqueue(callback);
        call.cancel();
        latch.countDown();
        callback.await(server.url("/a")).assertFailure("Canceled", "Socket closed");
    }

    @Test
    public void cancelAll() throws Exception {
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        call.enqueue(callback);
        client.dispatcher().cancelAll();
        callback.await(server.url("/")).assertFailure("Canceled", "Socket closed");
    }

    @Test
    public void cancelWhileRequestHeadersAreSent() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        EventListener listener = new EventListener() {
            @Override
            public void requestHeadersStart(Call call) {
                try {
                    // Cancel call from another thread to avoid reentrance.
                    cancelLater(call, 0).join();
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        };
        client = client.newBuilder().eventListener(listener).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/a")).build());
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void cancelWhileRequestHeadersAreSent_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        cancelWhileRequestHeadersAreSent();
    }

    @Test
    public void cancelBeforeBodyIsRead() throws Exception {
        server.enqueue(new MockResponse().setBody("def").throttleBody(1, 750, TimeUnit.MILLISECONDS));
        final Call call = client.newCall(new Request.Builder().url(server.url("/a")).build());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Response> result = executor.submit(call::execute);
        Thread.sleep(100);// wait for it to go in flight.

        call.cancel();
        try {
            result.get().body().bytes();
            Assert.fail();
        } catch (IOException expected) {
        }
        Assert.assertEquals(1, server.getRequestCount());
    }

    @Test
    public void cancelInFlightBeforeResponseReadThrowsIOE() throws Exception {
        Request request = new Request.Builder().url(server.url("/a")).build();
        final Call call = client.newCall(request);
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                call.cancel();
                return new MockResponse().setBody("A");
            }
        });
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void cancelInFlightBeforeResponseReadThrowsIOE_HTTPS() throws Exception {
        enableTls();
        cancelInFlightBeforeResponseReadThrowsIOE();
    }

    @Test
    public void cancelInFlightBeforeResponseReadThrowsIOE_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        cancelInFlightBeforeResponseReadThrowsIOE();
    }

    /**
     * This test puts a request in front of one that is to be canceled, so that it is canceled before
     * I/O takes place.
     */
    @Test
    public void canceledBeforeIOSignalsOnFailure() throws Exception {
        // Force requests to be executed serially.
        okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher(client.dispatcher().executorService());
        dispatcher.setMaxRequests(1);
        client = client.newBuilder().dispatcher(dispatcher).build();
        Request requestA = new Request.Builder().url(server.url("/a")).build();
        Request requestB = new Request.Builder().url(server.url("/b")).build();
        final Call callA = client.newCall(requestA);
        final Call callB = client.newCall(requestB);
        server.setDispatcher(new Dispatcher() {
            char nextResponse = 'A';

            @Override
            public MockResponse dispatch(RecordedRequest request) {
                callB.cancel();
                return new MockResponse().setBody(Character.toString(((nextResponse)++)));
            }
        });
        callA.enqueue(callback);
        callB.enqueue(callback);
        Assert.assertEquals("/a", server.takeRequest().getPath());
        callback.await(requestA.url()).assertBody("A");
        // At this point we know the callback is ready, and that it will receive a cancel failure.
        callback.await(requestB.url()).assertFailure("Canceled", "Socket closed");
    }

    @Test
    public void canceledBeforeIOSignalsOnFailure_HTTPS() throws Exception {
        enableTls();
        canceledBeforeIOSignalsOnFailure();
    }

    @Test
    public void canceledBeforeIOSignalsOnFailure_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        canceledBeforeIOSignalsOnFailure();
    }

    @Test
    public void canceledBeforeResponseReadSignalsOnFailure() throws Exception {
        Request requestA = new Request.Builder().url(server.url("/a")).build();
        final Call call = client.newCall(requestA);
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                call.cancel();
                return new MockResponse().setBody("A");
            }
        });
        call.enqueue(callback);
        Assert.assertEquals("/a", server.takeRequest().getPath());
        callback.await(requestA.url()).assertFailure("Canceled", "stream was reset: CANCEL", "Socket closed");
    }

    @Test
    public void canceledBeforeResponseReadSignalsOnFailure_HTTPS() throws Exception {
        enableTls();
        canceledBeforeResponseReadSignalsOnFailure();
    }

    @Test
    public void canceledBeforeResponseReadSignalsOnFailure_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        canceledBeforeResponseReadSignalsOnFailure();
    }

    /**
     * There's a race condition where the cancel may apply after the stream has already been
     * processed.
     */
    @Test
    public void canceledAfterResponseIsDeliveredBreaksStreamButSignalsOnce() throws Exception {
        server.enqueue(new MockResponse().setBody("A"));
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> bodyRef = new AtomicReference<>();
        final AtomicBoolean failureRef = new AtomicBoolean();
        Request request = new Request.Builder().url(server.url("/a")).build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failureRef.set(true);
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                call.cancel();
                try {
                    bodyRef.set(response.body().string());
                } catch (IOException e) {
                    // It is ok if this broke the stream.
                    bodyRef.set("A");
                    throw e;// We expect to not loop into onFailure in this case.

                } finally {
                    latch.countDown();
                }
            }
        });
        latch.await();
        Assert.assertEquals("A", bodyRef.get());
        Assert.assertFalse(failureRef.get());
    }

    @Test
    public void canceledAfterResponseIsDeliveredBreaksStreamButSignalsOnce_HTTPS() throws Exception {
        enableTls();
        canceledAfterResponseIsDeliveredBreaksStreamButSignalsOnce();
    }

    @Test
    public void canceledAfterResponseIsDeliveredBreaksStreamButSignalsOnce_HTTP_2() throws Exception {
        enableProtocol(HTTP_2);
        canceledAfterResponseIsDeliveredBreaksStreamButSignalsOnce();
    }

    @Test
    public void cancelWithInterceptor() throws Exception {
        client = client.newBuilder().addInterceptor(( chain) -> {
            chain.proceed(chain.request());
            throw new AssertionError();// We expect an exception.

        }).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/a")).build());
        call.cancel();
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        Assert.assertEquals(0, server.getRequestCount());
    }

    @Test
    public void gzip() throws Exception {
        Buffer gzippedBody = gzip("abcabcabc");
        String bodySize = Long.toString(gzippedBody.size());
        server.enqueue(new MockResponse().setBody(gzippedBody).addHeader("Content-Encoding: gzip"));
        // Confirm that the user request doesn't have Accept-Encoding, and the user
        // response doesn't have a Content-Encoding or Content-Length.
        RecordedResponse userResponse = executeSynchronously("/");
        userResponse.assertCode(200).assertRequestHeader("Accept-Encoding").assertHeader("Content-Encoding").assertHeader("Content-Length").assertBody("abcabcabc");
        // But the network request doesn't lie. OkHttp used gzip for this call.
        userResponse.networkResponse().assertHeader("Content-Encoding", "gzip").assertHeader("Content-Length", bodySize).assertRequestHeader("Accept-Encoding", "gzip");
    }

    /**
     * https://github.com/square/okhttp/issues/1927
     */
    @Test
    public void gzipResponseAfterAuthenticationChallenge() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setBody(gzip("abcabcabc")).addHeader("Content-Encoding: gzip"));
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator("password", null)).build();
        executeSynchronously("/").assertBody("abcabcabc");
    }

    @Test
    public void rangeHeaderPreventsAutomaticGzip() throws Exception {
        Buffer gzippedBody = gzip("abcabcabc");
        // Enqueue a gzipped response. Our request isn't expecting it, but that's okay.
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_PARTIAL).setBody(gzippedBody).addHeader("Content-Encoding: gzip").addHeader(("Content-Range: bytes 0-" + ((gzippedBody.size()) - 1))));
        // Make a range request.
        Request request = new Request.Builder().url(server.url("/")).header("Range", "bytes=0-").build();
        Call call = client.newCall(request);
        // The response is not decompressed.
        Response response = call.execute();
        Assert.assertEquals("gzip", response.header("Content-Encoding"));
        Assert.assertEquals(gzippedBody.snapshot(), response.body().source().readByteString());
        // The request did not offer gzip support.
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertNull(recordedRequest.getHeader("Accept-Encoding"));
    }

    @Test
    public void asyncResponseCanBeConsumedLater() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        Request request = new Request.Builder().url(server.url("/")).header("User-Agent", "SyncApiTest").build();
        final BlockingQueue<Response> responseRef = new SynchronousQueue<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new AssertionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    responseRef.put(response);
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        });
        Response response = responseRef.take();
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("abc", response.body().string());
        // Make another request just to confirm that that connection can be reused...
        executeSynchronously("/").assertBody("def");
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Connection reused.

        // ... even before we close the response body!
        response.body().close();
    }

    @Test
    public void userAgentIsIncludedByDefault() throws Exception {
        server.enqueue(new MockResponse());
        executeSynchronously("/");
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertTrue(recordedRequest.getHeader("User-Agent").matches(Version.userAgent()));
    }

    @Test
    public void setFollowRedirectsFalse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(302).addHeader("Location: /b").setBody("A"));
        server.enqueue(new MockResponse().setBody("B"));
        client = client.newBuilder().followRedirects(false).build();
        executeSynchronously("/a").assertBody("A").assertCode(302);
    }

    @Test
    public void expect100ContinueNonEmptyRequestBody() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(EXPECT_CONTINUE));
        Request request = new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        executeSynchronously(request).assertCode(200).assertSuccessful();
        Assert.assertEquals("abc", server.takeRequest().getBody().readUtf8());
    }

    @Test
    public void expect100ContinueEmptyRequestBody() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "")).build();
        executeSynchronously(request).assertCode(200).assertSuccessful();
    }

    @Test
    public void expect100ContinueEmptyRequestBody_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        expect100ContinueEmptyRequestBody();
    }

    @Test
    public void expect100ContinueTimesOutWithoutContinue() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(NO_RESPONSE));
        client = client.newBuilder().readTimeout(500, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        Call call = client.newCall(request);
        try {
            call.execute();
            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void expect100ContinueTimesOutWithoutContinue_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        expect100ContinueTimesOutWithoutContinue();
    }

    @Test
    public void serverRespondsWithUnsolicited100Continue() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(CONTINUE_ALWAYS));
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        executeSynchronously(request).assertCode(200).assertSuccessful();
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("abc", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void serverRespondsWithUnsolicited100Continue_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        serverRespondsWithUnsolicited100Continue();
    }

    @Test
    public void serverRespondsWith100ContinueOnly() throws Exception {
        client = client.newBuilder().readTimeout(1, TimeUnit.SECONDS).build();
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 100 Continue"));
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        Call call = client.newCall(request);
        try {
            call.execute();
            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("abc", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void serverRespondsWith100ContinueOnly_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        serverRespondsWith100ContinueOnly();
    }

    @Test
    public void successfulExpectContinuePermitsConnectionReuse() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(EXPECT_CONTINUE));
        server.enqueue(new MockResponse());
        executeSynchronously(new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build());
        executeSynchronously(new Request.Builder().url(server.url("/")).build());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void successfulExpectContinuePermitsConnectionReuseWithHttp2() throws Exception {
        enableProtocol(HTTP_2);
        successfulExpectContinuePermitsConnectionReuse();
    }

    @Test
    public void unsuccessfulExpectContinuePreventsConnectionReuse() throws Exception {
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        executeSynchronously(new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build());
        executeSynchronously(new Request.Builder().url(server.url("/")).build());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void unsuccessfulExpectContinuePermitsConnectionReuseWithHttp2() throws Exception {
        enableProtocol(HTTP_2);
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        executeSynchronously(new Request.Builder().url(server.url("/")).header("Expect", "100-continue").post(RequestBody.create(MediaType.get("text/plain"), "abc")).build());
        executeSynchronously(new Request.Builder().url(server.url("/")).build());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    /**
     * We forbid non-ASCII characters in outgoing request headers, but accept UTF-8.
     */
    @Test
    public void responseHeaderParsingIsLenient() throws Exception {
        Headers headers = new Headers.Builder().add("Content-Length", "0").addLenient("a\tb: c\u007fd").addLenient(": ef").addLenient("\ud83c\udf69: \u2615\ufe0f").build();
        server.enqueue(new MockResponse().setHeaders(headers));
        executeSynchronously("/").assertHeader("a\tb", "c\u007fd").assertHeader("\ud83c\udf69", "\u2615\ufe0f").assertHeader("", "ef");
    }

    @Test
    public void customDns() throws Exception {
        // Configure a DNS that returns our local MockWebServer for android.com.
        FakeDns dns = new FakeDns();
        dns.set("android.com", SYSTEM.lookup(server.url("/").host()));
        client = client.newBuilder().dns(dns).build();
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/").newBuilder().host("android.com").build()).build();
        executeSynchronously(request).assertCode(200);
        dns.assertRequests("android.com");
    }

    @Test
    public void dnsReturnsZeroIpAddresses() throws Exception {
        // Configure a DNS that returns our local MockWebServer for android.com.
        FakeDns dns = new FakeDns();
        List<InetAddress> ipAddresses = new ArrayList<>();
        dns.set("android.com", ipAddresses);
        client = client.newBuilder().dns(dns).build();
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/").newBuilder().host("android.com").build()).build();
        executeSynchronously(request).assertFailure((dns + " returned no addresses for android.com"));
        dns.assertRequests("android.com");
    }

    /**
     * We had a bug where failed HTTP/2 calls could break the entire connection.
     */
    @Test
    public void failingCallsDoNotInterfereWithConnection() throws Exception {
        enableProtocol(HTTP_2);
        server.enqueue(new MockResponse().setBody("Response 1"));
        server.enqueue(new MockResponse().setBody("Response 2"));
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("abc");
                sink.flush();
                makeFailingCall();
                sink.writeUtf8("def");
                sink.flush();
            }
        };
        Call call = client.newCall(new Request.Builder().url(server.url("/")).post(requestBody).build());
        Assert.assertEquals("Response 1", call.execute().body().string());
    }

    /**
     * Test which headers are sent unencrypted to the HTTP proxy.
     */
    @Test
    public void proxyConnectOmitsApplicationHeaders() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("encrypted response from the origin server"));
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).hostnameVerifier(hostnameVerifier).build();
        Request request = new Request.Builder().url("https://android.com/foo").header("Private", "Secret").header("User-Agent", "App 1.0").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("encrypted response from the origin server", response.body().string());
        RecordedRequest connect = server.takeRequest();
        Assert.assertNull(connect.getHeader("Private"));
        Assert.assertEquals(Version.userAgent(), connect.getHeader("User-Agent"));
        Assert.assertEquals("Keep-Alive", connect.getHeader("Proxy-Connection"));
        Assert.assertEquals("android.com:443", connect.getHeader("Host"));
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("Secret", get.getHeader("Private"));
        Assert.assertEquals("App 1.0", get.getHeader("User-Agent"));
        Assert.assertEquals(Arrays.asList("verify android.com"), hostnameVerifier.calls);
    }

    /**
     * Respond to a proxy authorization challenge.
     */
    @Test
    public void proxyAuthenticateOnConnect() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\""));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("response body"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).proxyAuthenticator(new RecordingOkAuthenticator("password", "Basic")).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Request request = new Request.Builder().url("https://android.com/foo").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("response body", response.body().string());
        RecordedRequest connect1 = server.takeRequest();
        Assert.assertEquals("CONNECT android.com:443 HTTP/1.1", connect1.getRequestLine());
        Assert.assertNull(connect1.getHeader("Proxy-Authorization"));
        RecordedRequest connect2 = server.takeRequest();
        Assert.assertEquals("CONNECT android.com:443 HTTP/1.1", connect2.getRequestLine());
        Assert.assertEquals("password", connect2.getHeader("Proxy-Authorization"));
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", get.getRequestLine());
        Assert.assertNull(get.getHeader("Proxy-Authorization"));
    }

    /**
     * Confirm that the proxy authenticator works for unencrypted HTTP proxies.
     */
    @Test
    public void httpProxyAuthenticate() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\""));
        server.enqueue(new MockResponse().setBody("response body"));
        client = client.newBuilder().proxy(server.toProxyAddress()).proxyAuthenticator(new RecordingOkAuthenticator("password", "Basic")).build();
        Request request = new Request.Builder().url("http://android.com/foo").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("response body", response.body().string());
        RecordedRequest get1 = server.takeRequest();
        Assert.assertEquals("GET http://android.com/foo HTTP/1.1", get1.getRequestLine());
        Assert.assertNull(get1.getHeader("Proxy-Authorization"));
        RecordedRequest get2 = server.takeRequest();
        Assert.assertEquals("GET http://android.com/foo HTTP/1.1", get2.getRequestLine());
        Assert.assertEquals("password", get2.getHeader("Proxy-Authorization"));
    }

    /**
     * OkHttp has a bug where a `Connection: close` response header is not honored when establishing a
     * TLS tunnel. https://github.com/square/okhttp/issues/2426
     */
    @Test
    public void proxyAuthenticateOnConnectWithConnectionClose() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.setProtocols(Collections.singletonList(HTTP_1_1));
        server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\"").addHeader("Connection: close"));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("response body"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).proxyAuthenticator(new RecordingOkAuthenticator("password", "Basic")).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Request request = new Request.Builder().url("https://android.com/foo").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("response body", response.body().string());
        // First CONNECT call needs a new connection.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        // Second CONNECT call needs a new connection.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        // GET reuses the connection from the second connect.
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void tooManyProxyAuthFailuresWithConnectionClose() throws IOException {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.setProtocols(Collections.singletonList(HTTP_1_1));
        for (int i = 0; i < 21; i++) {
            server.enqueue(new MockResponse().setResponseCode(407).addHeader("Proxy-Authenticate: Basic realm=\"localhost\"").addHeader("Connection: close"));
        }
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).proxyAuthenticator(new RecordingOkAuthenticator("password", "Basic")).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Request request = new Request.Builder().url("https://android.com/foo").build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (ProtocolException expected) {
        }
    }

    /**
     * Confirm that we don't send the Proxy-Authorization header from the request to the proxy server.
     * We used to have that behavior but it is problematic because unrelated requests end up sharing
     * credentials. Worse, that approach leaks proxy credentials to the origin server.
     */
    @Test
    public void noPreemptiveProxyAuthorization() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("response body"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).hostnameVerifier(new RecordingHostnameVerifier()).build();
        Request request = new Request.Builder().url("https://android.com/foo").header("Proxy-Authorization", "password").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("response body", response.body().string());
        RecordedRequest connect1 = server.takeRequest();
        Assert.assertNull(connect1.getHeader("Proxy-Authorization"));
        RecordedRequest connect2 = server.takeRequest();
        Assert.assertEquals("password", connect2.getHeader("Proxy-Authorization"));
    }

    /**
     * Confirm that we can send authentication information without being prompted first.
     */
    @Test
    public void preemptiveProxyAuthentication() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("encrypted response from the origin server"));
        final String credential = Credentials.basic("jesse", "password1");
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).hostnameVerifier(new RecordingHostnameVerifier()).proxyAuthenticator(( route, response) -> {
            assertEquals("CONNECT", response.request().method());
            assertEquals(HttpURLConnection.HTTP_PROXY_AUTH, response.code());
            assertEquals("android.com", response.request().url().host());
            List<Challenge> challenges = response.challenges();
            assertEquals("OkHttp-Preemptive", challenges.get(0).scheme());
            return response.request().newBuilder().header("Proxy-Authorization", credential).build();
        }).build();
        Request request = new Request.Builder().url("https://android.com/foo").build();
        executeSynchronously(request).assertSuccessful();
        RecordedRequest connect = server.takeRequest();
        Assert.assertEquals("CONNECT", connect.getMethod());
        Assert.assertEquals(credential, connect.getHeader("Proxy-Authorization"));
        Assert.assertEquals("/", connect.getPath());
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("GET", get.getMethod());
        Assert.assertNull(get.getHeader("Proxy-Authorization"));
        Assert.assertEquals("/foo", get.getPath());
    }

    @Test
    public void preemptiveThenReactiveProxyAuthentication() throws Exception {
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_PROXY_AUTH).addHeader("Proxy-Authenticate", "Basic realm=\"localhost\"").setBody("proxy auth required"));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse());
        final List<String> challengeSchemes = new ArrayList<>();
        final String credential = Credentials.basic("jesse", "password1");
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).proxy(server.toProxyAddress()).hostnameVerifier(new RecordingHostnameVerifier()).proxyAuthenticator(( route, response) -> {
            List<Challenge> challenges = response.challenges();
            challengeSchemes.add(challenges.get(0).scheme());
            return response.request().newBuilder().header("Proxy-Authorization", credential).build();
        }).build();
        Request request = new Request.Builder().url("https://android.com/foo").build();
        executeSynchronously(request).assertSuccessful();
        RecordedRequest connect1 = server.takeRequest();
        Assert.assertEquals("CONNECT", connect1.getMethod());
        Assert.assertEquals(credential, connect1.getHeader("Proxy-Authorization"));
        RecordedRequest connect2 = server.takeRequest();
        Assert.assertEquals("CONNECT", connect2.getMethod());
        Assert.assertEquals(credential, connect2.getHeader("Proxy-Authorization"));
        Assert.assertEquals(Arrays.asList("OkHttp-Preemptive", "Basic"), challengeSchemes);
    }

    @Test
    public void interceptorGetsHttp2() throws Exception {
        enableProtocol(HTTP_2);
        // Capture the protocol as it is observed by the interceptor.
        final AtomicReference<Protocol> protocolRef = new AtomicReference<>();
        Interceptor interceptor = ( chain) -> {
            protocolRef.set(chain.connection().protocol());
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        // Make an HTTP/2 request and confirm that the protocol matches.
        server.enqueue(new MockResponse());
        executeSynchronously("/");
        Assert.assertEquals(HTTP_2, protocolRef.get());
    }

    @Test
    public void serverSendsInvalidResponseHeaders() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTP/1.1 200 OK"));
        executeSynchronously("/").assertFailure("Unexpected status line: HTP/1.1 200 OK");
    }

    @Test
    public void serverSendsInvalidCodeTooLarge() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 2147483648 OK"));
        executeSynchronously("/").assertFailure("Unexpected status line: HTTP/1.1 2147483648 OK");
    }

    @Test
    public void serverSendsInvalidCodeNotANumber() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 00a OK"));
        executeSynchronously("/").assertFailure("Unexpected status line: HTTP/1.1 00a OK");
    }

    @Test
    public void serverSendsUnnecessaryWhitespace() throws Exception {
        server.enqueue(new MockResponse().setStatus(" HTTP/1.1 200 OK"));
        executeSynchronously("/").assertFailure("Unexpected status line:  HTTP/1.1 200 OK");
    }

    @Test
    public void requestHeaderNameWithSpaceForbidden() throws Exception {
        try {
            new Request.Builder().addHeader("a b", "c");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0x20 at 1 in header name: a b", expected.getMessage());
        }
    }

    @Test
    public void requestHeaderNameWithTabForbidden() throws Exception {
        try {
            new Request.Builder().addHeader("a\tb", "c");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Unexpected char 0x09 at 1 in header name: a\tb", expected.getMessage());
        }
    }

    @Test
    public void responseHeaderNameWithSpacePermitted() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().addHeader("content-length: 0").addHeaderLenient("a b", "c"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("c", response.header("a b"));
    }

    @Test
    public void responseHeaderNameWithTabPermitted() throws Exception {
        server.enqueue(new MockResponse().clearHeaders().addHeader("content-length: 0").addHeaderLenient("a\tb", "c"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("c", response.header("a\tb"));
    }

    @Test
    public void connectFails() throws Exception {
        server.shutdown();
        executeSynchronously("/").assertFailure(IOException.class);
    }

    @Test
    public void requestBodySurvivesRetries() throws Exception {
        server.enqueue(new MockResponse());
        // Enable a misconfigured proxy selector to guarantee that the request is retried.
        client = client.newBuilder().proxySelector(new FakeProxySelector().addProxy(server2.toProxyAddress()).addProxy(Proxy.NO_PROXY)).build();
        server2.shutdown();
        Request request = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), "abc")).build();
        executeSynchronously(request);
        Assert.assertEquals("abc", server.takeRequest().getBody().readUtf8());
    }

    @Test
    public void uploadBodySmallChunkedEncoding() throws Exception {
        upload(true, 1048576, 256);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(1048576, recordedRequest.getBodySize());
        Assert.assertFalse(recordedRequest.getChunkSizes().isEmpty());
    }

    @Test
    public void uploadBodyLargeChunkedEncoding() throws Exception {
        upload(true, 1048576, 65536);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(1048576, recordedRequest.getBodySize());
        Assert.assertFalse(recordedRequest.getChunkSizes().isEmpty());
    }

    @Test
    public void uploadBodySmallFixedLength() throws Exception {
        upload(false, 1048576, 256);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(1048576, recordedRequest.getBodySize());
        Assert.assertTrue(recordedRequest.getChunkSizes().isEmpty());
    }

    @Test
    public void uploadBodyLargeFixedLength() throws Exception {
        upload(false, 1048576, 65536);
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(1048576, recordedRequest.getBodySize());
        Assert.assertTrue(recordedRequest.getChunkSizes().isEmpty());
    }

    /**
     * https://github.com/square/okhttp/issues/2344
     */
    @Test
    public void ipv6HostHasSquareBraces() throws Exception {
        // Use a proxy to fake IPv6 connectivity, even if localhost doesn't have IPv6.
        server.useHttps(handshakeCertificates.sslSocketFactory(), true);
        server.setProtocols(Collections.singletonList(HTTP_1_1));
        server.enqueue(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        server.enqueue(new MockResponse().setBody("response body"));
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).proxy(server.toProxyAddress()).build();
        Request request = new Request.Builder().url("https://[::1]/").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals("response body", response.body().string());
        RecordedRequest connect = server.takeRequest();
        Assert.assertEquals("CONNECT [::1]:443 HTTP/1.1", connect.getRequestLine());
        Assert.assertEquals("[::1]:443", connect.getHeader("Host"));
        RecordedRequest get = server.takeRequest();
        Assert.assertEquals("GET / HTTP/1.1", get.getRequestLine());
        Assert.assertEquals("[::1]", get.getHeader("Host"));
    }

    @Test
    public void emptyResponseBody() throws Exception {
        server.enqueue(new MockResponse().addHeader("abc", "def"));
        executeSynchronously("/").assertCode(200).assertHeader("abc", "def").assertBody("");
    }

    @Test
    public void leakedResponseBodyLogsStackTrace() throws Exception {
        server.enqueue(new MockResponse().setBody("This gets leaked."));
        client = clientTestRule.client.newBuilder().connectionPool(new ConnectionPool(0, 10, TimeUnit.MILLISECONDS)).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Level original = logger.getLevel();
        logger.setLevel(Level.FINE);
        logHandler.setFormatter(new SimpleFormatter());
        try {
            client.newCall(request).execute();// Ignore the response so it gets leaked then GC'd.

            TestUtil.awaitGarbageCollection();
            String message = logHandler.take();
            Assert.assertTrue(message.contains(((("A connection to " + (server.url("/"))) + " was leaked.") + " Did you forget to close a response body?")));
            Assert.assertTrue(message.contains("okhttp3.RealCall.execute("));
            Assert.assertTrue(message.contains("okhttp3.CallTest.leakedResponseBodyLogsStackTrace("));
        } finally {
            logger.setLevel(original);
        }
    }

    @Test
    public void asyncLeakedResponseBodyLogsStackTrace() throws Exception {
        server.enqueue(new MockResponse().setBody("This gets leaked."));
        client = clientTestRule.client.newBuilder().connectionPool(new ConnectionPool(0, 10, TimeUnit.MILLISECONDS)).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Level original = logger.getLevel();
        logger.setLevel(Level.FINE);
        logHandler.setFormatter(new SimpleFormatter());
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Assert.fail();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Ignore the response so it gets leaked then GC'd.
                    latch.countDown();
                }
            });
            latch.await();
            // There's some flakiness when triggering a GC for objects in a separate thread. Adding a
            // small delay appears to ensure the objects will get GC'd.
            Thread.sleep(200);
            TestUtil.awaitGarbageCollection();
            String message = logHandler.take();
            Assert.assertTrue(message.contains(((("A connection to " + (server.url("/"))) + " was leaked.") + " Did you forget to close a response body?")));
            Assert.assertTrue(message.contains("okhttp3.RealCall.enqueue("));
            Assert.assertTrue(message.contains("okhttp3.CallTest.asyncLeakedResponseBodyLogsStackTrace("));
        } finally {
            logger.setLevel(original);
        }
    }

    @Test
    public void failedAuthenticatorReleasesConnection() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(401));
        client = client.newBuilder().authenticator(( route, response) -> {
            throw new IOException("IOException!");
        }).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        executeSynchronously(request).assertFailure(IOException.class);
        Assert.assertEquals(1, client.connectionPool().idleConnectionCount());
    }

    @Test
    public void failedProxyAuthenticatorReleasesConnection() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(407));
        client = client.newBuilder().proxyAuthenticator(( route, response) -> {
            throw new IOException("IOException!");
        }).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        executeSynchronously(request).assertFailure(IOException.class);
        Assert.assertEquals(1, client.connectionPool().idleConnectionCount());
    }

    @Test
    public void httpsWithIpAddress() throws Exception {
        String localIpAddress = InetAddress.getLoopbackAddress().getHostAddress();
        // Create a certificate with an IP address in the subject alt name.
        HeldCertificate heldCertificate = new HeldCertificate.Builder().commonName("example.com").addSubjectAlternativeName(localIpAddress).build();
        HandshakeCertificates handshakeCertificates = new HandshakeCertificates.Builder().heldCertificate(heldCertificate).addTrustedCertificate(heldCertificate.certificate()).build();
        // Use that certificate on the server and trust it on the client.
        server.useHttps(handshakeCertificates.sslSocketFactory(), false);
        client = client.newBuilder().sslSocketFactory(handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager()).hostnameVerifier(new RecordingHostnameVerifier()).protocols(Collections.singletonList(HTTP_1_1)).build();
        // Make a request.
        server.enqueue(new MockResponse());
        HttpUrl url = server.url("/").newBuilder().host(localIpAddress).build();
        Request request = new Request.Builder().url(url).build();
        executeSynchronously(request).assertCode(200);
        // Confirm that the IP address was used in the host header.
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals(((localIpAddress + ":") + (server.getPort())), recordedRequest.getHeader("Host"));
    }

    @Test
    public void postWithFileNotFound() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        RequestBody body = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.get("application/octet-stream");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                called.incrementAndGet();
                throw new FileNotFoundException();
            }
        };
        Request request = new Request.Builder().url(server.url("/")).post(body).build();
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        executeSynchronously(request).assertFailure(FileNotFoundException.class);
        Assert.assertEquals(1L, called.get());
    }

    @Test
    public void clientReadsHeadersDataTrailersHttp1ChunkedTransferEncoding() throws Exception {
        MockResponse mockResponse = new MockResponse().clearHeaders().addHeader("h1", "v1").addHeader("h2", "v2").setChunkedBody("HelloBonjour", 1024).setTrailers(Headers.of("trailers", "boom"));
        server.enqueue(mockResponse);
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        BufferedSource source = response.body().source();
        Assert.assertEquals("v1", response.header("h1"));
        Assert.assertEquals("v2", response.header("h2"));
        Assert.assertEquals("Hello", source.readUtf8(5));
        Assert.assertEquals("Bonjour", source.readUtf8(7));
        Assert.assertTrue(source.exhausted());
        Assert.assertEquals(Headers.of("trailers", "boom"), response.trailers());
    }

    @Test
    public void clientReadsHeadersDataTrailersHttp2() throws IOException {
        MockResponse mockResponse = new MockResponse().clearHeaders().addHeader("h1", "v1").addHeader("h2", "v2").setBody("HelloBonjour").setTrailers(Headers.of("trailers", "boom"));
        server.enqueue(mockResponse);
        enableProtocol(HTTP_2);
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        try (Response response = call.execute()) {
            BufferedSource source = response.body().source();
            Assert.assertEquals("v1", response.header("h1"));
            Assert.assertEquals("v2", response.header("h2"));
            Assert.assertEquals("Hello", source.readUtf8(5));
            Assert.assertEquals("Bonjour", source.readUtf8(7));
            Assert.assertTrue(source.exhausted());
            Assert.assertEquals(Headers.of("trailers", "boom"), response.trailers());
        }
    }

    @Test
    public void requestBodyThrowsUnrelatedToNetwork() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).post(new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                throw new IOException("boom");
            }
        }).build();
        executeSynchronously(request).assertFailure("boom");
    }

    @Test
    public void requestBodyThrowsUnrelatedToNetwork_HTTP2() throws Exception {
        enableProtocol(HTTP_2);
        requestBodyThrowsUnrelatedToNetwork();
    }

    /**
     * https://github.com/square/okhttp/issues/4583
     */
    @Test
    public void lateCancelCallsOnFailure() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        AtomicBoolean closed = new AtomicBoolean();
        client = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                chain.call().cancel();// Cancel after we have the response.

                ForwardingSource closeTrackingSource = new ForwardingSource(response.body().source()) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                };
                return response.newBuilder().body(ResponseBody.create(null, (-1L), Okio.buffer(closeTrackingSource))).build();
            }
        }).build();
        executeSynchronously("/").assertFailure("Canceled");
        Assert.assertTrue(closed.get());
    }

    private static class RecordingSSLSocketFactory extends DelegatingSSLSocketFactory {
        private List<SSLSocket> socketsCreated = new ArrayList<>();

        public RecordingSSLSocketFactory(SSLSocketFactory delegate) {
            super(delegate);
        }

        @Override
        protected SSLSocket configureSocket(SSLSocket sslSocket) throws IOException {
            socketsCreated.add(sslSocket);
            return sslSocket;
        }

        public List<SSLSocket> getSocketsCreated() {
            return socketsCreated;
        }
    }
}

