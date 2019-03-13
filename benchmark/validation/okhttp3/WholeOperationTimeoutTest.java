/**
 * Copyright (C) 2018 Square, Inc.
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


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public final class WholeOperationTimeoutTest {
    /**
     * A large response body. Smaller bodies might successfully read after the socket is closed!
     */
    private static final String BIG_ENOUGH_BODY = TestUtil.repeat('a', (64 * 1024));

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    private OkHttpClient client = clientTestRule.client;

    @Test
    public void defaultConfigIsNoTimeout() throws Exception {
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        Assert.assertEquals(0, call.timeout().timeoutNanos());
    }

    @Test
    public void configureClientDefault() throws Exception {
        Request request = new Request.Builder().url(server.url("/")).build();
        OkHttpClient timeoutClient = client.newBuilder().callTimeout(456, TimeUnit.MILLISECONDS).build();
        Call call = timeoutClient.newCall(request);
        Assert.assertEquals(TimeUnit.MILLISECONDS.toNanos(456), call.timeout().timeoutNanos());
    }

    @Test
    public void timeoutWritingRequest() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).post(sleepingRequestBody(500)).build();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("timeout", e.getMessage());
            Assert.assertTrue(call.isCanceled());
        }
    }

    @Test
    public void timeoutWritingRequestWithEnqueue() throws Exception {
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/")).post(sleepingRequestBody(500)).build();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                exceptionRef.set(e);
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
                latch.countDown();
            }
        });
        latch.await();
        Assert.assertTrue(call.isCanceled());
        Assert.assertNotNull(exceptionRef.get());
    }

    @Test
    public void timeoutProcessing() throws Exception {
        server.enqueue(new MockResponse().setHeadersDelay(500, TimeUnit.MILLISECONDS));
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("timeout", e.getMessage());
            Assert.assertTrue(call.isCanceled());
        }
    }

    @Test
    public void timeoutProcessingWithEnqueue() throws Exception {
        server.enqueue(new MockResponse().setHeadersDelay(500, TimeUnit.MILLISECONDS));
        Request request = new Request.Builder().url(server.url("/")).build();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                exceptionRef.set(e);
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
                latch.countDown();
            }
        });
        latch.await();
        Assert.assertTrue(call.isCanceled());
        Assert.assertNotNull(exceptionRef.get());
    }

    @Test
    public void timeoutReadingResponse() throws Exception {
        server.enqueue(new MockResponse().setBody(WholeOperationTimeoutTest.BIG_ENOUGH_BODY));
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        Response response = call.execute();
        Thread.sleep(500);
        try {
            response.body().source().readUtf8();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("timeout", e.getMessage());
            Assert.assertTrue(call.isCanceled());
        }
    }

    @Test
    public void timeoutReadingResponseWithEnqueue() throws Exception {
        server.enqueue(new MockResponse().setBody(WholeOperationTimeoutTest.BIG_ENOUGH_BODY));
        Request request = new Request.Builder().url(server.url("/")).build();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
                try {
                    response.body().source().readUtf8();
                    Assert.fail();
                } catch (IOException e) {
                    exceptionRef.set(e);
                } finally {
                    latch.countDown();
                }
            }
        });
        latch.await();
        Assert.assertTrue(call.isCanceled());
        Assert.assertNotNull(exceptionRef.get());
    }

    @Test
    public void singleTimeoutForAllFollowUpRequests() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", "/b").setHeadersDelay(100, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", "/c").setHeadersDelay(100, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", "/d").setHeadersDelay(100, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", "/e").setHeadersDelay(100, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", "/f").setHeadersDelay(100, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse());
        Request request = new Request.Builder().url(server.url("/a")).build();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("timeout", e.getMessage());
            Assert.assertTrue(call.isCanceled());
        }
    }

    @Test
    public void timeoutFollowingRedirectOnNewConnection() throws Exception {
        MockWebServer otherServer = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP).setHeader("Location", otherServer.url("/")));
        otherServer.enqueue(new MockResponse().setHeadersDelay(500, TimeUnit.MILLISECONDS));
        Request request = new Request.Builder().url(server.url("/")).build();
        Call call = client.newCall(request);
        call.timeout().timeout(250, TimeUnit.MILLISECONDS);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("timeout", e.getMessage());
            Assert.assertTrue(call.isCanceled());
        }
    }

    @Test
    public void noTimeout() throws Exception {
        server.enqueue(new MockResponse().setHeadersDelay(250, TimeUnit.MILLISECONDS).setBody(WholeOperationTimeoutTest.BIG_ENOUGH_BODY));
        Request request = new Request.Builder().url(server.url("/")).post(sleepingRequestBody(250)).build();
        Call call = client.newCall(request);
        call.timeout().timeout(1000, TimeUnit.MILLISECONDS);
        Response response = call.execute();
        Thread.sleep(250);
        response.body().source().readUtf8();
        response.close();
        Assert.assertFalse(call.isCanceled());
    }
}

