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
package okhttp3.internal.http2;


import ErrorCode.INTERNAL_ERROR;
import ErrorCode.REFUSED_STREAM;
import ErrorCode.REFUSED_STREAM.httpCode;
import Http2Connection.OKHTTP_CLIENT_WINDOW_SIZE;
import Settings.MAX_CONCURRENT_STREAMS;
import SocketPolicy.DISCONNECT_AT_END;
import SocketPolicy.NO_RESPONSE;
import SocketPolicy.RESET_STREAM_AT_START;
import SocketPolicy.STALL_SOCKET_AT_START;
import SocketPolicy.UPGRADE_TO_SSL_AT_END;
import Util.EMPTY_REQUEST;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.okhttp3.Authenticator;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClientTestRule;
import okhttp3.Protocol;
import okhttp3.RecordingCookieJar;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TestLogHandler;
import okhttp3.TestUtil;
import okhttp3.internal.DoubleInetAddressDns;
import okhttp3.internal.RecordingOkAuthenticator;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.PushPromise;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.tls.HandshakeCertificates;
import okio.Buffer;
import okio.BufferedSink;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Http2Connection.OKHTTP_CLIENT_WINDOW_SIZE;


/**
 * Test how HTTP/2 interacts with HTTP features.
 */
@RunWith(Parameterized.class)
public final class HttpOverHttp2Test {
    private static final Logger http2Logger = Logger.getLogger(Http2.class.getName());

    private static final HandshakeCertificates handshakeCertificates = localhost();

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    @Rule
    public final Timeout timeout = new Timeout(5, TimeUnit.SECONDS);

    private OkHttpClient client;

    private Cache cache;

    private TestLogHandler http2Handler = new TestLogHandler();

    private Level previousLevel;

    private String scheme;

    private Protocol protocol;

    public HttpOverHttp2Test(Protocol protocol) {
        this.client = (protocol == (Protocol.HTTP_2)) ? buildHttp2Client() : buildH2PriorKnowledgeClient();
        this.scheme = (protocol == (Protocol.HTTP_2)) ? "https" : "http";
        this.protocol = protocol;
    }

    @Test
    public void get() throws Exception {
        server.enqueue(new MockResponse().setBody("ABCDE").setStatus("HTTP/1.1 200 Sweet"));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("", response.message());
        Assert.assertEquals(protocol, response.protocol());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
        Assert.assertEquals(scheme, request.getHeader(":scheme"));
        Assert.assertEquals((((server.getHostName()) + ":") + (server.getPort())), request.getHeader(":authority"));
    }

    @Test
    public void emptyResponse() throws IOException {
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).build());
        Response response = call.execute();
        Assert.assertEquals((-1), response.body().byteStream().read());
        response.body().close();
    }

    @Test
    public void noDefaultContentLengthOnStreamingPost() throws Exception {
        byte[] postBytes = "FGHIJ".getBytes(StandardCharsets.UTF_8);
        server.enqueue(new MockResponse().setBody("ABCDE"));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).post(new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(postBytes);
            }
        }).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("POST /foo HTTP/1.1", request.getRequestLine());
        Assert.assertArrayEquals(postBytes, request.getBody().readByteArray());
        Assert.assertNull(request.getHeader("Content-Length"));
    }

    @Test
    public void userSuppliedContentLengthHeader() throws Exception {
        byte[] postBytes = "FGHIJ".getBytes(StandardCharsets.UTF_8);
        server.enqueue(new MockResponse().setBody("ABCDE"));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).post(new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain; charset=utf-8");
            }

            @Override
            public long contentLength() {
                return postBytes.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(postBytes);
            }
        }).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("POST /foo HTTP/1.1", request.getRequestLine());
        Assert.assertArrayEquals(postBytes, request.getBody().readByteArray());
        Assert.assertEquals(postBytes.length, Integer.parseInt(request.getHeader("Content-Length")));
    }

    @Test
    public void closeAfterFlush() throws Exception {
        byte[] postBytes = "FGHIJ".getBytes(StandardCharsets.UTF_8);
        server.enqueue(new MockResponse().setBody("ABCDE"));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).post(new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("text/plain; charset=utf-8");
            }

            @Override
            public long contentLength() {
                return postBytes.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(postBytes);// push bytes into the stream's buffer

                sink.flush();// Http2Connection.writeData subject to write window

                sink.close();// Http2Connection.writeData empty frame

            }
        }).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("POST /foo HTTP/1.1", request.getRequestLine());
        Assert.assertArrayEquals(postBytes, request.getBody().readByteArray());
        Assert.assertEquals(postBytes.length, Integer.parseInt(request.getHeader("Content-Length")));
    }

    @Test
    public void connectionReuse() throws Exception {
        server.enqueue(new MockResponse().setBody("ABCDEF"));
        server.enqueue(new MockResponse().setBody("GHIJKL"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/r1")).build());
        Call call2 = client.newCall(new Request.Builder().url(server.url("/r1")).build());
        Response response1 = call1.execute();
        Response response2 = call2.execute();
        Assert.assertEquals("ABC", response1.body().source().readUtf8(3));
        Assert.assertEquals("GHI", response2.body().source().readUtf8(3));
        Assert.assertEquals("DEF", response1.body().source().readUtf8(3));
        Assert.assertEquals("JKL", response2.body().source().readUtf8(3));
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        response1.close();
        response2.close();
    }

    @Test
    public void connectionWindowUpdateAfterCanceling() throws Exception {
        server.enqueue(new MockResponse().setBody(new Buffer().write(new byte[(OKHTTP_CLIENT_WINDOW_SIZE) + 1])));
        server.enqueue(new MockResponse().setBody("abc"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        waitForDataFrames(OKHTTP_CLIENT_WINDOW_SIZE);
        // Cancel the call and discard what we've buffered for the response body. This should free up
        // the connection flow-control window so new requests can proceed.
        call1.cancel();
        Assert.assertFalse("Call should not have completed successfully.", Util.discard(response1.body().source(), 1, TimeUnit.SECONDS));
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("abc", response2.body().string());
    }

    @Test
    public void connectionWindowUpdateOnClose() throws Exception {
        server.enqueue(new MockResponse().setBody(new Buffer().write(new byte[(OKHTTP_CLIENT_WINDOW_SIZE) + 1])));
        server.enqueue(new MockResponse().setBody("abc"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        waitForDataFrames(OKHTTP_CLIENT_WINDOW_SIZE);
        // Cancel the call and close the response body. This should discard the buffered data and update
        // the connection flow-control window.
        call1.cancel();
        response1.close();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("abc", response2.body().string());
    }

    @Test
    public void concurrentRequestWithEmptyFlowControlWindow() throws Exception {
        server.enqueue(new MockResponse().setBody(new Buffer().write(new byte[OKHTTP_CLIENT_WINDOW_SIZE])));
        server.enqueue(new MockResponse().setBody("abc"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        waitForDataFrames(OKHTTP_CLIENT_WINDOW_SIZE);
        Assert.assertEquals(OKHTTP_CLIENT_WINDOW_SIZE, response1.body().contentLength());
        int read = response1.body().source().read(new byte[8192]);
        Assert.assertEquals(8192, read);
        // Make a second call that should transmit the response headers. The response body won't be
        // transmitted until the flow-control window is updated from the first request.
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals(200, response2.code());
        // Close the response body. This should discard the buffered data and update the connection
        // flow-control window.
        response1.close();
        Assert.assertEquals("abc", response2.body().string());
    }

    @Test
    public void gzippedResponseBody() throws Exception {
        server.enqueue(new MockResponse().addHeader("Content-Encoding: gzip").setBody(gzip("ABCABCABC")));
        Call call = client.newCall(new Request.Builder().url(server.url("/r1")).build());
        Response response = call.execute();
        Assert.assertEquals("ABCABCABC", response.body().string());
    }

    @Test
    public void authenticate() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED).addHeader("www-authenticate: Basic realm=\"protected area\"").setBody("Please authenticate."));
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        String credential = Credentials.basic("username", "password");
        client = client.newBuilder().authenticator(new RecordingOkAuthenticator(credential, "Basic")).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("Successful auth!", response.body().string());
        RecordedRequest denied = server.takeRequest();
        Assert.assertNull(denied.getHeader("Authorization"));
        RecordedRequest accepted = server.takeRequest();
        Assert.assertEquals("GET / HTTP/1.1", accepted.getRequestLine());
        Assert.assertEquals(credential, accepted.getHeader("Authorization"));
    }

    @Test
    public void redirect() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).addHeader("Location: /foo").setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("This is the new location!", response.body().string());
        RecordedRequest request1 = server.takeRequest();
        Assert.assertEquals("/", request1.getPath());
        RecordedRequest request2 = server.takeRequest();
        Assert.assertEquals("/foo", request2.getPath());
    }

    @Test
    public void readAfterLastByte() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        InputStream in = response.body().byteStream();
        Assert.assertEquals('A', in.read());
        Assert.assertEquals('B', in.read());
        Assert.assertEquals('C', in.read());
        Assert.assertEquals((-1), in.read());
        Assert.assertEquals((-1), in.read());
        in.close();
    }

    @Test
    public void readResponseHeaderTimeout() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(NO_RESPONSE));
        server.enqueue(new MockResponse().setBody("A"));
        client = client.newBuilder().readTimeout(1000, TimeUnit.MILLISECONDS).build();
        // Make a call expecting a timeout reading the response headers.
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        try {
            call1.execute();
            Assert.fail("Should have timed out!");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("timeout", expected.getMessage());
        }
        // Confirm that a subsequent request on the same connection is not impacted.
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("A", response2.body().string());
        // Confirm that the connection was reused.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    /**
     * Test to ensure we don't  throw a read timeout on responses that are progressing.  For this
     * case, we take a 4KiB body and throttle it to 1KiB/second.  We set the read timeout to two
     * seconds.  If our implementation is acting correctly, it will not throw, as it is progressing.
     */
    @Test
    public void readTimeoutMoreGranularThanBodySize() throws Exception {
        char[] body = new char[4096];// 4KiB to read.

        Arrays.fill(body, 'y');
        server.enqueue(new MockResponse().setBody(new String(body)).throttleBody(1024, 1, TimeUnit.SECONDS));// Slow connection 1KiB/second.

        client = client.newBuilder().readTimeout(2, TimeUnit.SECONDS).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals(new String(body), response.body().string());
    }

    /**
     * Test to ensure we throw a read timeout on responses that are progressing too slowly.  For this
     * case, we take a 2KiB body and throttle it to 1KiB/second.  We set the read timeout to half a
     * second.  If our implementation is acting correctly, it will throw, as a byte doesn't arrive in
     * time.
     */
    @Test
    public void readTimeoutOnSlowConnection() throws Exception {
        String body = TestUtil.repeat('y', 2048);
        server.enqueue(new MockResponse().setBody(body).throttleBody(1024, 1, TimeUnit.SECONDS));// Slow connection 1KiB/second.

        server.enqueue(new MockResponse().setBody(body));
        client = // Half a second to read something.
        client.newBuilder().readTimeout(500, TimeUnit.MILLISECONDS).build();
        // Make a call expecting a timeout reading the response body.
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        try {
            response1.body().string();
            Assert.fail("Should have timed out!");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("timeout", expected.getMessage());
        }
        // Confirm that a subsequent request on the same connection is not impacted.
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals(body, response2.body().string());
        // Confirm that the connection was reused.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void connectionTimeout() throws Exception {
        server.enqueue(new MockResponse().setBody("A").setBodyDelay(1, TimeUnit.SECONDS));
        OkHttpClient client1 = client.newBuilder().readTimeout(2000, TimeUnit.MILLISECONDS).build();
        Call call1 = client1.newCall(new Request.Builder().url(server.url("/")).build());
        OkHttpClient client2 = client.newBuilder().readTimeout(200, TimeUnit.MILLISECONDS).build();
        Call call2 = client2.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals("A", response1.body().string());
        try {
            call2.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        // Confirm that the connection was reused.
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void responsesAreCached() throws IOException {
        client = client.newBuilder().cache(cache).build();
        server.enqueue(new MockResponse().addHeader("cache-control: max-age=60").setBody("A"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals("A", response1.body().string());
        Assert.assertEquals(1, cache.requestCount());
        Assert.assertEquals(1, cache.networkCount());
        Assert.assertEquals(0, cache.hitCount());
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("A", response2.body().string());
        Call call3 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response3 = call3.execute();
        Assert.assertEquals("A", response3.body().string());
        Assert.assertEquals(3, cache.requestCount());
        Assert.assertEquals(1, cache.networkCount());
        Assert.assertEquals(2, cache.hitCount());
    }

    @Test
    public void conditionalCache() throws IOException {
        client = client.newBuilder().cache(cache).build();
        server.enqueue(new MockResponse().addHeader("ETag: v1").setBody("A"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals("A", response1.body().string());
        Assert.assertEquals(1, cache.requestCount());
        Assert.assertEquals(1, cache.networkCount());
        Assert.assertEquals(0, cache.hitCount());
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("A", response2.body().string());
        Assert.assertEquals(2, cache.requestCount());
        Assert.assertEquals(2, cache.networkCount());
        Assert.assertEquals(1, cache.hitCount());
    }

    @Test
    public void responseCachedWithoutConsumingFullBody() throws IOException {
        client = client.newBuilder().cache(cache).build();
        server.enqueue(new MockResponse().addHeader("cache-control: max-age=60").setBody("ABCD"));
        server.enqueue(new MockResponse().addHeader("cache-control: max-age=60").setBody("EFGH"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals("AB", response1.body().source().readUtf8(2));
        response1.body().close();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("ABCD", response2.body().source().readUtf8());
        response2.body().close();
    }

    @Test
    public void sendRequestCookies() throws Exception {
        RecordingCookieJar cookieJar = new RecordingCookieJar();
        Cookie requestCookie = new Cookie.Builder().name("a").value("b").domain(server.getHostName()).build();
        cookieJar.enqueueRequestCookies(requestCookie);
        client = client.newBuilder().cookieJar(cookieJar).build();
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("", response.body().string());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("a=b", request.getHeader("Cookie"));
    }

    @Test
    public void receiveResponseCookies() throws Exception {
        RecordingCookieJar cookieJar = new RecordingCookieJar();
        client = client.newBuilder().cookieJar(cookieJar).build();
        server.enqueue(new MockResponse().addHeader("set-cookie: a=b"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("", response.body().string());
        cookieJar.assertResponseCookies("a=b; path=/");
    }

    @Test
    public void cancelWithStreamNotCompleted() throws Exception {
        server.enqueue(new MockResponse().setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        // Disconnect before the stream is created. A connection is still established!
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call1.execute();
        call1.cancel();
        // That connection is pooled, and it works.
        Assert.assertEquals(1, client.connectionPool().connectionCount());
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("def", response2.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        // Clean up the connection.
        response.close();
    }

    @Test
    public void recoverFromOneRefusedStreamReusesConnection() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(RESET_STREAM_AT_START).setHttp2ErrorCode(httpCode));
        server.enqueue(new MockResponse().setBody("abc"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("abc", response.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Reused connection.

    }

    @Test
    public void recoverFromOneInternalErrorRequiresNewConnection() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(RESET_STREAM_AT_START).setHttp2ErrorCode(ErrorCode.INTERNAL_ERROR.httpCode));
        server.enqueue(new MockResponse().setBody("abc"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("abc", response.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

    }

    @Test
    public void recoverFromMultipleRefusedStreamsRequiresNewConnection() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(RESET_STREAM_AT_START).setHttp2ErrorCode(httpCode));
        server.enqueue(new MockResponse().setSocketPolicy(RESET_STREAM_AT_START).setHttp2ErrorCode(httpCode));
        server.enqueue(new MockResponse().setBody("abc"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("abc", response.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Reused connection.

        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection.

    }

    @Test
    public void recoverFromCancelReusesConnection() throws Exception {
        server.enqueue(new MockResponse().setBodyDelay(10, TimeUnit.SECONDS).setBody("abc"));
        server.enqueue(new MockResponse().setBody("def"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        callAndCancel(0);
        // Make a second request to ensure the connection is reused.
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("def", response.body().string());
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void recoverFromMultipleCancelReusesConnection() throws Exception {
        server.enqueue(new MockResponse().setBodyDelay(10, TimeUnit.SECONDS).setBody("abc"));
        server.enqueue(new MockResponse().setBodyDelay(10, TimeUnit.SECONDS).setBody("def"));
        server.enqueue(new MockResponse().setBody("ghi"));
        client = client.newBuilder().dns(new DoubleInetAddressDns()).build();
        callAndCancel(0);
        callAndCancel(1);
        // Make a third request to ensure the connection is reused.
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("ghi", response.body().string());
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void noRecoveryFromRefusedStreamWithRetryDisabled() throws Exception {
        noRecoveryFromErrorWithRetryDisabled(REFUSED_STREAM);
    }

    @Test
    public void noRecoveryFromInternalErrorWithRetryDisabled() throws Exception {
        noRecoveryFromErrorWithRetryDisabled(INTERNAL_ERROR);
    }

    @Test
    public void recoverFromConnectionNoNewStreamsOnFollowUp() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setSocketPolicy(RESET_STREAM_AT_START).setHttp2ErrorCode(ErrorCode.INTERNAL_ERROR.httpCode));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location", "/foo"));
        server.enqueue(new MockResponse().setBody("ABC"));
        CountDownLatch latch = new CountDownLatch(1);
        BlockingQueue<String> responses = new SynchronousQueue<>();
        okhttp3.Authenticator authenticator = ( route, response) -> {
            responses.offer(response.body().string());
            try {
                latch.await();
            } catch ( e) {
                throw new AssertionError();
            }
            return response.request();
        };
        OkHttpClient blockingAuthClient = client.newBuilder().authenticator(authenticator).build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Assert.fail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responses.offer(response.body().string());
            }
        };
        // Make the first request waiting until we get our auth challenge.
        Request request = new Request.Builder().url(server.url("/")).build();
        blockingAuthClient.newCall(request).enqueue(callback);
        String response1 = responses.take();
        Assert.assertEquals("", response1);
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        // Now make the second request which will restrict the first HTTP/2 connection from creating new
        // streams.
        client.newCall(request).enqueue(callback);
        String response2 = responses.take();
        Assert.assertEquals("DEF", response2);
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        // Let the first request proceed. It should discard the the held HTTP/2 connection and get a new
        // one.
        latch.countDown();
        String response3 = responses.take();
        Assert.assertEquals("ABC", response3);
        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void nonAsciiResponseHeader() throws Exception {
        server.enqueue(new MockResponse().addHeaderLenient("Alpha", "?").addHeaderLenient("?", "Beta"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        response.close();
        Assert.assertEquals("?", response.header("Alpha"));
        Assert.assertEquals("Beta", response.header("?"));
    }

    @Test
    public void serverSendsPushPromise_GET() throws Exception {
        PushPromise pushPromise = new PushPromise("GET", "/foo/bar", Headers.of("foo", "bar"), new MockResponse().setBody("bar").setStatus("HTTP/1.1 200 Sweet"));
        server.enqueue(new MockResponse().setBody("ABCDE").setStatus("HTTP/1.1 200 Sweet").withPush(pushPromise));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("", response.message());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
        Assert.assertEquals(scheme, request.getHeader(":scheme"));
        Assert.assertEquals((((server.getHostName()) + ":") + (server.getPort())), request.getHeader(":authority"));
        RecordedRequest pushedRequest = server.takeRequest();
        Assert.assertEquals("GET /foo/bar HTTP/1.1", pushedRequest.getRequestLine());
        Assert.assertEquals("bar", pushedRequest.getHeader("foo"));
    }

    @Test
    public void serverSendsPushPromise_HEAD() throws Exception {
        PushPromise pushPromise = new PushPromise("HEAD", "/foo/bar", Headers.of("foo", "bar"), new MockResponse().setStatus("HTTP/1.1 204 Sweet"));
        server.enqueue(new MockResponse().setBody("ABCDE").setStatus("HTTP/1.1 200 Sweet").withPush(pushPromise));
        Call call = client.newCall(new Request.Builder().url(server.url("/foo")).build());
        Response response = call.execute();
        Assert.assertEquals("ABCDE", response.body().string());
        Assert.assertEquals(200, response.code());
        Assert.assertEquals("", response.message());
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
        Assert.assertEquals(scheme, request.getHeader(":scheme"));
        Assert.assertEquals((((server.getHostName()) + ":") + (server.getPort())), request.getHeader(":authority"));
        RecordedRequest pushedRequest = server.takeRequest();
        Assert.assertEquals("HEAD /foo/bar HTTP/1.1", pushedRequest.getRequestLine());
        Assert.assertEquals("bar", pushedRequest.getHeader("foo"));
    }

    @Test
    public void noDataFramesSentWithNullRequestBody() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).method("DELETE", null).build());
        Response response = call.execute();
        Assert.assertEquals("ABC", response.body().string());
        Assert.assertEquals(protocol, response.protocol());
        List<String> logs = http2Handler.takeAll();
        Assert.assertThat("header logged", firstFrame(logs, "HEADERS"), CoreMatchers.containsString("HEADERS       END_STREAM|END_HEADERS"));
    }

    @Test
    public void emptyDataFrameSentWithEmptyBody() throws Exception {
        server.enqueue(new MockResponse().setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).method("DELETE", EMPTY_REQUEST).build());
        Response response = call.execute();
        Assert.assertEquals("ABC", response.body().string());
        Assert.assertEquals(protocol, response.protocol());
        List<String> logs = http2Handler.takeAll();
        Assert.assertThat("header logged", firstFrame(logs, "HEADERS"), CoreMatchers.containsString("HEADERS       END_HEADERS"));
        Assert.assertThat("data logged", firstFrame(logs, "DATA"), CoreMatchers.containsString("0 DATA          END_STREAM"));
    }

    @Test
    public void pingsTransmitted() throws Exception {
        // Ping every 500 ms, starting at 500 ms.
        client = client.newBuilder().pingInterval(500, TimeUnit.MILLISECONDS).build();
        // Delay the response to give 1 ping enough time to be sent and replied to.
        server.enqueue(new MockResponse().setBodyDelay(750, TimeUnit.MILLISECONDS).setBody("ABC"));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("ABC", response.body().string());
        Assert.assertEquals(protocol, response.protocol());
        // Confirm a single ping was sent and received, and its reply was sent and received.
        List<String> logs = http2Handler.takeAll();
        Assert.assertEquals(1, countFrames(logs, "FINE: >> 0x00000000     8 PING          "));
        Assert.assertEquals(1, countFrames(logs, "FINE: << 0x00000000     8 PING          "));
        Assert.assertEquals(1, countFrames(logs, "FINE: >> 0x00000000     8 PING          ACK"));
        Assert.assertEquals(1, countFrames(logs, "FINE: << 0x00000000     8 PING          ACK"));
    }

    @Test
    public void missingPongsFailsConnection() throws Exception {
        // Ping every 500 ms, starting at 500 ms.
        client = // Confirm we fail before the read timeout.
        client.newBuilder().readTimeout(10, TimeUnit.SECONDS).pingInterval(500, TimeUnit.MILLISECONDS).build();
        // Set up the server to ignore the socket. It won't respond to pings!
        server.enqueue(new MockResponse().setSocketPolicy(STALL_SOCKET_AT_START));
        // Make a call. It'll fail as soon as our pings detect a problem.
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        long executeAtNanos = System.nanoTime();
        try {
            call.execute();
            Assert.fail();
        } catch (StreamResetException expected) {
            Assert.assertEquals("stream was reset: PROTOCOL_ERROR", expected.getMessage());
        }
        long elapsedUntilFailure = (System.nanoTime()) - executeAtNanos;
        Assert.assertEquals(1000, TimeUnit.NANOSECONDS.toMillis(elapsedUntilFailure), 250.0);
        // Confirm a single ping was sent but not acknowledged.
        List<String> logs = http2Handler.takeAll();
        Assert.assertEquals(1, countFrames(logs, "FINE: >> 0x00000000     8 PING          "));
        Assert.assertEquals(0, countFrames(logs, "FINE: << 0x00000000     8 PING          ACK"));
    }

    /**
     * Push a setting that permits up to 2 concurrent streams, then make 3 concurrent requests and
     * confirm that the third concurrent request prepared a new connection.
     */
    @Test
    public void settingsLimitsMaxConcurrentStreams() throws Exception {
        Settings settings = new Settings();
        settings.set(MAX_CONCURRENT_STREAMS, 2);
        // Read & write a full request to confirm settings are accepted.
        server.enqueue(new MockResponse().withSettings(settings));
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("", response.body().string());
        server.enqueue(new MockResponse().setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.enqueue(new MockResponse().setBody("GHI"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Call call3 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response3 = call3.execute();
        Assert.assertEquals("ABC", response1.body().string());
        Assert.assertEquals("DEF", response2.body().string());
        Assert.assertEquals("GHI", response3.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// Settings connection.

        Assert.assertEquals(1, server.takeRequest().getSequenceNumber());// Reuse settings connection.

        Assert.assertEquals(2, server.takeRequest().getSequenceNumber());// Reuse settings connection.

        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());// New connection!

    }

    @Test
    public void connectionNotReusedAfterShutdown() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        Call call1 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response1 = call1.execute();
        Assert.assertEquals("ABC", response1.body().string());
        Call call2 = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response2 = call2.execute();
        Assert.assertEquals("DEF", response2.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    /**
     * This simulates a race condition where we receive a healthy HTTP/2 connection and just prior to
     * writing our request, we get a GOAWAY frame from the server.
     */
    @Test
    public void connectionShutdownAfterHealthCheck() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        OkHttpClient client2 = client.newBuilder().addNetworkInterceptor(new Interceptor() {
            boolean executedCall;

            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!(executedCall)) {
                    // At this point, we have a healthy HTTP/2 connection. This call will trigger the
                    // server to send a GOAWAY frame, leaving the connection in a shutdown state.
                    executedCall = true;
                    Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
                    Response response = call.execute();
                    Assert.assertEquals("ABC", response.body().string());
                    // Wait until the GOAWAY has been processed.
                    RealConnection connection = ((RealConnection) (chain.connection()));
                    while (connection.isHealthy(false));
                }
                return chain.proceed(chain.request());
            }
        }).build();
        Call call = client2.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("DEF", response.body().string());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
        Assert.assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    @Test
    public void responseHeadersAfterGoaway() throws Exception {
        server.enqueue(new MockResponse().setHeadersDelay(1, TimeUnit.SECONDS).setBody("ABC"));
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).setBody("DEF"));
        BlockingQueue<String> bodies = new SynchronousQueue<>();
        Callback callback = new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bodies.add(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
            }
        };
        client.newCall(new Request.Builder().url(server.url("/")).build()).enqueue(callback);
        client.newCall(new Request.Builder().url(server.url("/")).build()).enqueue(callback);
        Assert.assertEquals("DEF", bodies.poll(2, TimeUnit.SECONDS));
        Assert.assertEquals("ABC", bodies.poll(2, TimeUnit.SECONDS));
        Assert.assertEquals(2, server.getRequestCount());
    }

    /**
     * We don't know if the connection will support HTTP/2 until after we've connected. When multiple
     * connections are requested concurrently OkHttp will pessimistically connect multiple times, then
     * close any unnecessary connections. This test confirms that behavior works as intended.
     *
     * <p>This test uses proxy tunnels to get a hook while a connection is being established.
     */
    @Test
    public void concurrentHttp2ConnectionsDeduplicated() throws Exception {
        Assume.assumeTrue(((protocol) == (Protocol.HTTP_2)));
        server.useHttps(HttpOverHttp2Test.handshakeCertificates.sslSocketFactory(), true);
        QueueDispatcher queueDispatcher = new QueueDispatcher();
        queueDispatcher.enqueueResponse(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        queueDispatcher.enqueueResponse(new MockResponse().setSocketPolicy(UPGRADE_TO_SSL_AT_END).clearHeaders());
        queueDispatcher.enqueueResponse(new MockResponse().setBody("call2 response"));
        queueDispatcher.enqueueResponse(new MockResponse().setBody("call1 response"));
        // We use a re-entrant dispatcher to initiate one HTTPS connection while the other is in flight.
        server.setDispatcher(new Dispatcher() {
            int requestCount;

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                MockResponse result = queueDispatcher.dispatch(request);
                (requestCount)++;
                if ((requestCount) == 1) {
                    // Before handling call1's CONNECT we do all of call2. This part re-entrant!
                    try {
                        Call call2 = client.newCall(new Request.Builder().url("https://android.com/call2").build());
                        Response response2 = call2.execute();
                        Assert.assertEquals("call2 response", response2.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return result;
            }

            @Override
            public MockResponse peek() {
                return queueDispatcher.peek();
            }

            @Override
            public void shutdown() {
                queueDispatcher.shutdown();
            }
        });
        client = client.newBuilder().proxy(server.toProxyAddress()).build();
        Call call1 = client.newCall(new Request.Builder().url("https://android.com/call1").build());
        Response response2 = call1.execute();
        Assert.assertEquals("call1 response", response2.body().string());
        RecordedRequest call1Connect = server.takeRequest();
        Assert.assertEquals("CONNECT", call1Connect.getMethod());
        Assert.assertEquals(0, call1Connect.getSequenceNumber());
        RecordedRequest call2Connect = server.takeRequest();
        Assert.assertEquals("CONNECT", call2Connect.getMethod());
        Assert.assertEquals(0, call2Connect.getSequenceNumber());
        RecordedRequest call2Get = server.takeRequest();
        Assert.assertEquals("GET", call2Get.getMethod());
        Assert.assertEquals("/call2", call2Get.getPath());
        Assert.assertEquals(0, call2Get.getSequenceNumber());
        RecordedRequest call1Get = server.takeRequest();
        Assert.assertEquals("GET", call1Get.getMethod());
        Assert.assertEquals("/call1", call1Get.getPath());
        Assert.assertEquals(1, call1Get.getSequenceNumber());
        Assert.assertEquals(1, client.connectionPool().connectionCount());
    }

    /**
     * https://github.com/square/okhttp/issues/3103
     */
    @Test
    public void domainFronting() throws Exception {
        client = client.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().header("Host", "privateobject.com").build();
                return chain.proceed(request);
            }
        }).build();
        server.enqueue(new MockResponse());
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        Assert.assertEquals("", response.body().string());
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("privateobject.com", recordedRequest.getHeader(":authority"));
    }

    class AsyncRequest implements Runnable {
        String path;

        CountDownLatch countDownLatch;

        AsyncRequest(String path, CountDownLatch countDownLatch) {
            this.path = path;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                Call call = client.newCall(new Request.Builder().url(server.url(path)).build());
                Response response = call.execute();
                Assert.assertEquals("A", response.body().string());
                countDownLatch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

