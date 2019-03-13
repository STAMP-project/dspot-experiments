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
package com.squareup.picasso3;


import CacheControl.FORCE_CACHE;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.squareup.picasso3.RequestHandler.Result;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class NetworkRequestHandlerTest {
    final BlockingDeque<Response> responses = new LinkedBlockingDeque<>();

    final BlockingDeque<Request> requests = new LinkedBlockingDeque<>();

    @Mock
    Picasso picasso;

    @Mock
    Stats stats;

    @Mock
    Dispatcher dispatcher;

    private NetworkRequestHandler networkHandler;

    @Test
    public void doesNotForceLocalCacheOnlyWithAirplaneModeOffAndRetryCount() throws Exception {
        responses.add(NetworkRequestHandlerTest.responseOf(ResponseBody.create(null, new byte[10])));
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        final CountDownLatch latch = new CountDownLatch(1);
        networkHandler.load(picasso, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                try {
                    assertThat(requests.takeFirst().cacheControl().toString()).isEmpty();
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                throw new AssertionError(t);
            }
        });
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void withZeroRetryCountForcesLocalCacheOnly() throws Exception {
        responses.add(NetworkRequestHandlerTest.responseOf(ResponseBody.create(null, new byte[10])));
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        PlatformLruCache cache = new PlatformLruCache(0);
        BitmapHunter hunter = new BitmapHunter(picasso, dispatcher, cache, stats, action, networkHandler);
        hunter.retryCount = 0;
        hunter.hunt();
        assertThat(requests.takeFirst().cacheControl().toString()).isEqualTo(FORCE_CACHE.toString());
    }

    @Test
    public void shouldRetryTwiceWithAirplaneModeOffAndNoNetworkInfo() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        PlatformLruCache cache = new PlatformLruCache(0);
        BitmapHunter hunter = new BitmapHunter(picasso, dispatcher, cache, stats, action, networkHandler);
        assertThat(hunter.shouldRetry(false, null)).isTrue();
        assertThat(hunter.shouldRetry(false, null)).isTrue();
        assertThat(hunter.shouldRetry(false, null)).isFalse();
    }

    @Test
    public void shouldRetryWithUnknownNetworkInfo() {
        assertThat(networkHandler.shouldRetry(false, null)).isTrue();
        assertThat(networkHandler.shouldRetry(true, null)).isTrue();
    }

    @Test
    public void shouldRetryWithConnectedNetworkInfo() {
        NetworkInfo info = TestUtils.mockNetworkInfo();
        Mockito.when(info.isConnected()).thenReturn(true);
        assertThat(networkHandler.shouldRetry(false, info)).isTrue();
        assertThat(networkHandler.shouldRetry(true, info)).isTrue();
    }

    @Test
    public void shouldNotRetryWithDisconnectedNetworkInfo() {
        NetworkInfo info = TestUtils.mockNetworkInfo();
        Mockito.when(info.isConnectedOrConnecting()).thenReturn(false);
        assertThat(networkHandler.shouldRetry(false, info)).isFalse();
        assertThat(networkHandler.shouldRetry(true, info)).isFalse();
    }

    @Test
    public void noCacheAndKnownContentLengthDispatchToStats() throws Exception {
        responses.add(NetworkRequestHandlerTest.responseOf(ResponseBody.create(null, new byte[10])));
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        final CountDownLatch latch = new CountDownLatch(1);
        networkHandler.load(picasso, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                Mockito.verify(stats).dispatchDownloadFinished(10);
                latch.countDown();
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                throw new AssertionError(t);
            }
        });
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void unknownContentLengthFromDiskThrows() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 0;
            }

            @Override
            public BufferedSource source() {
                return new Buffer();
            }

            @Override
            public void close() {
                closed.set(true);
                super.close();
            }
        };
        responses.add(NetworkRequestHandlerTest.responseOf(body).newBuilder().cacheResponse(NetworkRequestHandlerTest.responseOf(null)).build());
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        final CountDownLatch latch = new CountDownLatch(1);
        networkHandler.load(picasso, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                throw new AssertionError();
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                Mockito.verifyZeroInteractions(stats);
                Assert.assertTrue(closed.get());
                latch.countDown();
            }
        });
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void cachedResponseDoesNotDispatchToStats() throws Exception {
        responses.add(NetworkRequestHandlerTest.responseOf(ResponseBody.create(null, new byte[10])).newBuilder().cacheResponse(NetworkRequestHandlerTest.responseOf(null)).build());
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        final CountDownLatch latch = new CountDownLatch(1);
        networkHandler.load(picasso, action.request, new RequestHandler.Callback() {
            @Override
            public void onSuccess(Result result) {
                Mockito.verifyZeroInteractions(stats);
                latch.countDown();
            }

            @Override
            public void onError(@NonNull
            Throwable t) {
                throw new AssertionError(t);
            }
        });
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void shouldHandleSchemeInsensitiveCase() {
        String[] schemes = new String[]{ "http", "https", "HTTP", "HTTPS", "HTtP" };
        for (String scheme : schemes) {
            Uri uri = TestUtils.URI_1.buildUpon().scheme(scheme).build();
            assertThat(networkHandler.canHandleRequest(TestUtils.mockRequest(uri))).isTrue();
        }
    }
}

