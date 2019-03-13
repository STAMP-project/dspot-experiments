/**
 * Copyright (C) 2014 Square, Inc.
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


import Protocol.HTTP_1_1;
import SocketPolicy.DISCONNECT_AT_END;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;


public final class InterceptorTest {
    @Rule
    public MockWebServer server = new MockWebServer();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    private OkHttpClient client = clientTestRule.client;

    private RecordingCallback callback = new RecordingCallback();

    @Test
    public void applicationInterceptorsCanShortCircuitResponses() throws Exception {
        server.shutdown();// Accept no connections.

        Request request = new Request.Builder().url("https://localhost:1/").build();
        Response interceptorResponse = new Response.Builder().request(request).protocol(HTTP_1_1).code(200).message("Intercepted!").body(ResponseBody.create(MediaType.get("text/plain; charset=utf-8"), "abc")).build();
        client = client.newBuilder().addInterceptor(( chain) -> interceptorResponse).build();
        Response response = client.newCall(request).execute();
        Assert.assertSame(interceptorResponse, response);
    }

    @Test
    public void networkInterceptorsCannotShortCircuitResponses() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));
        Interceptor interceptor = ( chain) -> new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200).message("Intercepted!").body(ResponseBody.create(MediaType.get("text/plain; charset=utf-8"), "abc")).build();
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals((("network interceptor " + interceptor) + " must call proceed() exactly once"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorsCannotCallProceedMultipleTimes() throws Exception {
        server.enqueue(new MockResponse());
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            chain.proceed(chain.request());
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals((("network interceptor " + interceptor) + " must call proceed() exactly once"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorsCannotChangeServerAddress() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));
        Interceptor interceptor = ( chain) -> {
            Address address = chain.connection().route().address();
            String sameHost = address.url().host();
            int differentPort = (address.url().port()) + 1;
            return chain.proceed(chain.request().newBuilder().url((((("http://" + sameHost) + ":") + differentPort) + "/")).build());
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals((("network interceptor " + interceptor) + " must retain the same host and port"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorsHaveConnectionAccess() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            Connection connection = chain.connection();
            assertNotNull(connection);
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        client.newCall(request).execute();
    }

    @Test
    public void networkInterceptorsObserveNetworkHeaders() throws Exception {
        server.enqueue(new MockResponse().setBody(gzip("abcabcabc")).addHeader("Content-Encoding: gzip"));
        Interceptor interceptor = ( chain) -> {
            // The network request has everything: User-Agent, Host, Accept-Encoding.
            Request networkRequest = chain.request();
            assertNotNull(networkRequest.header("User-Agent"));
            assertEquals((((server.getHostName()) + ":") + (server.getPort())), networkRequest.header("Host"));
            assertNotNull(networkRequest.header("Accept-Encoding"));
            // The network response also has everything, including the raw gzipped content.
            Response networkResponse = chain.proceed(networkRequest);
            assertEquals("gzip", networkResponse.header("Content-Encoding"));
            return networkResponse;
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        // No extra headers in the application's request.
        Assert.assertNull(request.header("User-Agent"));
        Assert.assertNull(request.header("Host"));
        Assert.assertNull(request.header("Accept-Encoding"));
        // No extra headers in the application's response.
        Response response = client.newCall(request).execute();
        Assert.assertNull(request.header("Content-Encoding"));
        Assert.assertEquals("abcabcabc", response.body().string());
    }

    @Test
    public void networkInterceptorsCanChangeRequestMethodFromGetToPost() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            Request originalRequest = chain.request();
            MediaType mediaType = MediaType.get("text/plain");
            RequestBody body = RequestBody.create(mediaType, "abc");
            return chain.proceed(originalRequest.newBuilder().method("POST", body).header("Content-Type", mediaType.toString()).header("Content-Length", Long.toString(body.contentLength())).build());
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).get().build();
        client.newCall(request).execute();
        RecordedRequest recordedRequest = server.takeRequest();
        Assert.assertEquals("POST", recordedRequest.getMethod());
        Assert.assertEquals("abc", recordedRequest.getBody().readUtf8());
    }

    @Test
    public void applicationInterceptorsRewriteRequestToServer() throws Exception {
        rewriteRequestToServer(false);
    }

    @Test
    public void networkInterceptorsRewriteRequestToServer() throws Exception {
        rewriteRequestToServer(true);
    }

    @Test
    public void applicationInterceptorsRewriteResponseFromServer() throws Exception {
        rewriteResponseFromServer(false);
    }

    @Test
    public void networkInterceptorsRewriteResponseFromServer() throws Exception {
        rewriteResponseFromServer(true);
    }

    @Test
    public void multipleApplicationInterceptors() throws Exception {
        multipleInterceptors(false);
    }

    @Test
    public void multipleNetworkInterceptors() throws Exception {
        multipleInterceptors(true);
    }

    @Test
    public void asyncApplicationInterceptors() throws Exception {
        asyncInterceptors(false);
    }

    @Test
    public void asyncNetworkInterceptors() throws Exception {
        asyncInterceptors(true);
    }

    @Test
    public void applicationInterceptorsCanMakeMultipleRequestsToServer() throws Exception {
        server.enqueue(new MockResponse().setBody("a"));
        server.enqueue(new MockResponse().setBody("b"));
        client = client.newBuilder().addInterceptor(( chain) -> {
            Response response1 = chain.proceed(chain.request());
            response1.body().close();
            return chain.proceed(chain.request());
        }).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(response.body().string(), "b");
    }

    /**
     * Make sure interceptors can interact with the OkHttp client.
     */
    @Test
    public void interceptorMakesAnUnrelatedRequest() throws Exception {
        server.enqueue(new MockResponse().setBody("a"));// Fetched by interceptor.

        server.enqueue(new MockResponse().setBody("b"));// Fetched directly.

        client = client.newBuilder().addInterceptor(( chain) -> {
            if (chain.request().url().encodedPath().equals("/b")) {
                Request requestA = new Request.Builder().url(server.url("/a")).build();
                Response responseA = client.newCall(requestA).execute();
                assertEquals("a", responseA.body().string());
            }
            return chain.proceed(chain.request());
        }).build();
        Request requestB = new Request.Builder().url(server.url("/b")).build();
        Response responseB = client.newCall(requestB).execute();
        Assert.assertEquals("b", responseB.body().string());
    }

    /**
     * Make sure interceptors can interact with the OkHttp client asynchronously.
     */
    @Test
    public void interceptorMakesAnUnrelatedAsyncRequest() throws Exception {
        server.enqueue(new MockResponse().setBody("a"));// Fetched by interceptor.

        server.enqueue(new MockResponse().setBody("b"));// Fetched directly.

        client = client.newBuilder().addInterceptor(( chain) -> {
            if (chain.request().url().encodedPath().equals("/b")) {
                Request requestA = new Request.Builder().url(server.url("/a")).build();
                try {
                    RecordingCallback callbackA = new RecordingCallback();
                    client.newCall(requestA).enqueue(callbackA);
                    callbackA.await(requestA.url()).assertBody("a");
                } catch ( e) {
                    throw new <e>RuntimeException();
                }
            }
            return chain.proceed(chain.request());
        }).build();
        Request requestB = new Request.Builder().url(server.url("/b")).build();
        RecordingCallback callbackB = new RecordingCallback();
        client.newCall(requestB).enqueue(callbackB);
        callbackB.await(requestB.url()).assertBody("b");
    }

    @Test
    public void applicationInterceptorThrowsRuntimeExceptionSynchronous() throws Exception {
        interceptorThrowsRuntimeExceptionSynchronous(false);
    }

    @Test
    public void networkInterceptorThrowsRuntimeExceptionSynchronous() throws Exception {
        interceptorThrowsRuntimeExceptionSynchronous(true);
    }

    @Test
    public void networkInterceptorModifiedRequestIsReturned() throws IOException {
        server.enqueue(new MockResponse());
        Interceptor modifyHeaderInterceptor = ( chain) -> {
            Request modifiedRequest = chain.request().newBuilder().header("User-Agent", "intercepted request").build();
            return chain.proceed(modifiedRequest);
        };
        client = client.newBuilder().addNetworkInterceptor(modifyHeaderInterceptor).build();
        Request request = new Request.Builder().url(server.url("/")).header("User-Agent", "user request").build();
        Response response = client.newCall(request).execute();
        Assert.assertNotNull(response.request().header("User-Agent"));
        Assert.assertEquals("user request", response.request().header("User-Agent"));
        Assert.assertEquals("intercepted request", response.networkResponse().request().header("User-Agent"));
    }

    @Test
    public void applicationInterceptorThrowsRuntimeExceptionAsynchronous() throws Exception {
        interceptorThrowsRuntimeExceptionAsynchronous(false);
    }

    @Test
    public void networkInterceptorThrowsRuntimeExceptionAsynchronous() throws Exception {
        interceptorThrowsRuntimeExceptionAsynchronous(true);
    }

    @Test
    public void applicationInterceptorReturnsNull() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            chain.proceed(chain.request());
            return null;
        };
        client = client.newBuilder().addInterceptor(interceptor).build();
        InterceptorTest.ExceptionCatchingExecutor executor = new InterceptorTest.ExceptionCatchingExecutor();
        client = client.newBuilder().dispatcher(new Dispatcher(executor)).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals((("interceptor " + interceptor) + " returned null"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorReturnsNull() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            chain.proceed(chain.request());
            return null;
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        InterceptorTest.ExceptionCatchingExecutor executor = new InterceptorTest.ExceptionCatchingExecutor();
        client = client.newBuilder().dispatcher(new Dispatcher(executor)).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (NullPointerException expected) {
            Assert.assertEquals((("interceptor " + interceptor) + " returned null"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorReturnsConnectionOnEmptyBody() throws Exception {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_END).addHeader("Connection", "Close"));
        Interceptor interceptor = ( chain) -> {
            Response response = chain.proceed(chain.request());
            assertNotNull(chain.connection());
            return response;
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = client.newCall(request).execute();
        response.body().close();
    }

    @Test
    public void applicationInterceptorResponseMustHaveBody() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            Response response = chain.proceed(chain.request());
            return response.newBuilder().body(null).build();
        };
        client = client.newBuilder().addInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals((("interceptor " + interceptor) + " returned a response with no body"), expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorResponseMustHaveBody() throws Exception {
        server.enqueue(new MockResponse());
        Interceptor interceptor = ( chain) -> {
            Response response = chain.proceed(chain.request());
            return response.newBuilder().body(null).build();
        };
        client = client.newBuilder().addNetworkInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        try {
            client.newCall(request).execute();
            Assert.fail();
        } catch (IllegalStateException expected) {
            Assert.assertEquals((("interceptor " + interceptor) + " returned a response with no body"), expected.getMessage());
        }
    }

    @Test
    public void connectTimeout() throws Exception {
        Interceptor interceptor1 = ( chainA) -> {
            assertEquals(5000, chainA.connectTimeoutMillis());
            Interceptor.Chain chainB = chainA.withConnectTimeout(100, TimeUnit.MILLISECONDS);
            assertEquals(100, chainB.connectTimeoutMillis());
            return chainB.proceed(chainA.request());
        };
        Interceptor interceptor2 = ( chain) -> {
            assertEquals(100, chain.connectTimeoutMillis());
            return chain.proceed(chain.request());
        };
        ServerSocket serverSocket = new ServerSocket(0, 1);
        // Fill backlog queue with this request so subsequent requests will be blocked.
        new Socket().connect(serverSocket.getLocalSocketAddress());
        client = client.newBuilder().connectTimeout(5, SECONDS).addInterceptor(interceptor1).addInterceptor(interceptor2).build();
        Request request1 = new Request.Builder().url(((("http://" + (serverSocket.getInetAddress().getCanonicalHostName())) + ":") + (serverSocket.getLocalPort()))).build();
        Call call = client.newCall(request1);
        try {
            call.execute();
            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
        serverSocket.close();
    }

    @Test
    public void chainWithReadTimeout() throws Exception {
        Interceptor interceptor1 = ( chainA) -> {
            assertEquals(5000, chainA.readTimeoutMillis());
            Interceptor.Chain chainB = chainA.withReadTimeout(100, TimeUnit.MILLISECONDS);
            assertEquals(100, chainB.readTimeoutMillis());
            return chainB.proceed(chainA.request());
        };
        Interceptor interceptor2 = ( chain) -> {
            assertEquals(100, chain.readTimeoutMillis());
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().readTimeout(5, SECONDS).addInterceptor(interceptor1).addInterceptor(interceptor2).build();
        server.enqueue(new MockResponse().setBody("abc").throttleBody(1, 1, SECONDS));
        Request request1 = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request1);
        Response response = call.execute();
        ResponseBody body = response.body();
        try {
            body.string();
            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    @Test
    public void chainWithWriteTimeout() throws Exception {
        Interceptor interceptor1 = ( chainA) -> {
            assertEquals(5000, chainA.writeTimeoutMillis());
            Interceptor.Chain chainB = chainA.withWriteTimeout(100, TimeUnit.MILLISECONDS);
            assertEquals(100, chainB.writeTimeoutMillis());
            return chainB.proceed(chainA.request());
        };
        Interceptor interceptor2 = ( chain) -> {
            assertEquals(100, chain.writeTimeoutMillis());
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().writeTimeout(5, SECONDS).addInterceptor(interceptor1).addInterceptor(interceptor2).build();
        server.enqueue(new MockResponse().setBody("abc").throttleBody(1, 1, SECONDS));
        byte[] data = new byte[(2 * 1024) * 1024];// 2 MiB.

        Request request1 = new Request.Builder().url(server.url("/")).post(RequestBody.create(MediaType.get("text/plain"), data)).build();
        Call call = client.newCall(request1);
        try {
            call.execute();// we want this call to throw a SocketTimeoutException

            Assert.fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    @Test
    public void chainCanCancelCall() throws Exception {
        AtomicReference<Call> callRef = new AtomicReference<>();
        Interceptor interceptor = ( chain) -> {
            Call call = chain.call();
            callRef.set(call);
            assertFalse(call.isCanceled());
            call.cancel();
            assertTrue(call.isCanceled());
            return chain.proceed(chain.request());
        };
        client = client.newBuilder().addInterceptor(interceptor).build();
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
        Assert.assertSame(call, callRef.get());
    }

    /**
     * Catches exceptions that are otherwise headed for the uncaught exception handler.
     */
    private static class ExceptionCatchingExecutor extends ThreadPoolExecutor {
        private final BlockingQueue<Exception> exceptions = new LinkedBlockingQueue<>();

        public ExceptionCatchingExecutor() {
            super(1, 1, 0, SECONDS, new SynchronousQueue<>());
        }

        @Override
        public void execute(Runnable runnable) {
            super.execute(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    exceptions.add(e);
                }
            });
        }

        public Exception takeException() throws Exception {
            return exceptions.take();
        }
    }
}

