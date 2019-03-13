/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.util;


import HttpStatusCodes.STATUS_CODE_NO_CONTENT;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link KeysDownloader}.
 */
@RunWith(JUnit4.class)
public class KeysDownloaderTest {
    private static final long INITIAL_CURRENT_TIME_IN_MILLIS = 1000;

    private CountDownLatch backgroundFetchFinishedLatch;

    private CountDownLatch delayHttpResponseLatch;

    private ExecutorService executor;

    private KeysDownloaderTest.HttpResponseBuilder httpResponseBuilder;

    private AtomicInteger backgroundFetchStartedCount;

    private AtomicInteger httpTransportGetCount;

    private boolean executorIsAcceptingRunnables;

    private long currentTimeInMillis;

    @Test
    public void builderShouldThrowIllegalArgumentExceptionWhenUrlIsNotHttps() {
        try {
            new KeysDownloader.Builder().setUrl("http://abc").build();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected.
        }
    }

    @Test
    public void shouldFetchKeys() throws Exception {
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys");
        Assert.assertEquals("keys", download());
    }

    @Test
    public void shouldThrowOnSuccessHttpResponsesThatAreNotOk() throws Exception {
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setStatusCode(STATUS_CODE_NO_CONTENT);
        KeysDownloader instance = newInstanceForTests();
        try {
            instance.download();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals(("Unexpected status code = " + (HttpStatusCodes.STATUS_CODE_NO_CONTENT)), expected.getMessage());
        }
    }

    @Test
    public void shouldThrowOnNonSuccessHttpResponses() throws Exception {
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setStatusCode(STATUS_CODE_NO_CONTENT);
        KeysDownloader instance = newInstanceForTests();
        try {
            instance.download();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertTrue(((("Message " + (expected.getMessage())) + " should contain ") + (HttpStatusCodes.STATUS_CODE_NO_CONTENT)), expected.getMessage().contains(Integer.toString(STATUS_CODE_NO_CONTENT)));
        }
    }

    @Test
    public void shouldCacheKeysOnFetches() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1");
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // Old keys are returned
        Assert.assertEquals("keys1", instance.download());
    }

    @Test
    public void shouldFetchKeysAgainIfNoCacheControlHeadersAreSent() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").clearCacheControl();
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // New keys are fetched and returned
        Assert.assertEquals("keys2", instance.download());
    }

    @Test
    public void shouldFetchKeysAgainAfterExpiration() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 3 seconds later ...
        currentTimeInMillis += 3000L;
        // New keys are fetched and returned
        Assert.assertEquals("keys2", instance.download());
    }

    @Test
    public void shouldReturnCachedKeysBeforeExpiration() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 3 seconds - 1ms later ...
        currentTimeInMillis += 3000L - 1;
        // Old keys are sill returned
        Assert.assertEquals("keys1", instance.download());
    }

    @Test
    public void shouldFetchKeysAgainAfterExpirationAccountingForAgeHeader() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L).setAgeInSeconds(1L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 2 seconds later ...
        currentTimeInMillis += 2000L;
        // New keys are fetched and returned
        Assert.assertEquals("keys2", instance.download());
    }

    @Test
    public void shouldReturnCachedKeysBeforeExpirationAccountingForAgeHeader() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L).setAgeInSeconds(1L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 2 seconds - 1ms later ...
        currentTimeInMillis += 2000L - 1;
        // Old keys are sill returned
        Assert.assertEquals("keys1", instance.download());
    }

    @Test
    public void shouldTriggerBackgroundRefreshHalfWayThroughExpiration() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 1.5 seconds later ...
        currentTimeInMillis += 1500L;
        // Old keys are sill returned, but a background fetch is initiated
        Assert.assertEquals("keys1", instance.download());
        // Wait background fetch to complete
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        // 10ms later ...
        currentTimeInMillis += 10;
        // Keys changed again
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys3");
        // Keys fetched in the background are used
        Assert.assertEquals("keys2", instance.download());
        // Single background fetch should have been triggered
        Assert.assertEquals(1, backgroundFetchStartedCount.get());
    }

    @Test
    public void shouldNotTriggerBackgroundRefreshBeforeHalfWayThroughExpiration() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 1.5 seconds - 1ms later ...
        currentTimeInMillis += 1500L - 1;
        // Old keys are sill returned
        Assert.assertEquals("keys1", instance.download());
        // No background fetch should have been triggered
        Assert.assertEquals(0, backgroundFetchStartedCount.get());
    }

    @Test
    public void shouldPerformBackgroundRefreshWhenRequestedAndHaveCacheKeys() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cache keys
        instance.refreshInBackground();
        // Wait background fetch to complete
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // Keys fetched in the background are used
        Assert.assertEquals("keys1", instance.download());
        // Single background fetch should have been triggered
        Assert.assertEquals(1, backgroundFetchStartedCount.get());
        // Single http fetch should have been triggered
        Assert.assertEquals(1, httpTransportGetCount.get());
    }

    @Test
    public void shouldPerformMultipleRefreshesWhenRequested() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1");
        instance.refreshInBackground();
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        backgroundFetchFinishedLatch = new CountDownLatch(1);
        instance.refreshInBackground();
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        // Keys fetched in the background are used
        Assert.assertEquals("keys2", instance.download());
        // Multiple background fetch should have been triggered
        Assert.assertEquals(2, backgroundFetchStartedCount.get());
        // Multiple http fetch should have been triggered
        Assert.assertEquals(2, httpTransportGetCount.get());
    }

    @Test
    public void shouldPerformRefreshAfterExecutorTransientFailure() throws Exception {
        KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1");
        instance.download();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // Executor temporarily full, rejecting new Runnable instances
        executorIsAcceptingRunnables = false;
        try {
            instance.refreshInBackground();
            Assert.fail();
        } catch (RejectedExecutionException expected) {
        }
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys3");
        // Executor available again, accepting new Runnable instances
        executorIsAcceptingRunnables = true;
        instance.refreshInBackground();
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        // Keys fetched in the background are used
        Assert.assertEquals("keys3", instance.download());
        // Only a single background fetch should have started
        Assert.assertEquals(1, backgroundFetchStartedCount.get());
    }

    @Test
    public void shouldFetchOnlyOnceWhenMultipleThreadsTryToGetKeys() throws Exception {
        final KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys");
        List<FutureTask<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return instance.download();
                }
            }));
        }
        // Force the HTTP responses to be delayed until the latch goes down to 0.
        delayHttpResponseLatch = new CountDownLatch(1);
        // Execute the all fetches in parallel.
        for (FutureTask<String> task : tasks) {
            executor.execute(task);
        }
        // Releasing the response.
        delayHttpResponseLatch.countDown();
        for (FutureTask<String> task : tasks) {
            Assert.assertEquals("keys", task.get(5, TimeUnit.SECONDS));
        }
        // Should only have hit the network once.
        Assert.assertEquals(1, httpTransportGetCount.get());
    }

    @Test
    public void shouldFetchOnlyOnceInBackgroundHalfWayThroughExpirationWhenMultipleThreadsTryToGetKeys() throws Exception {
        final KeysDownloader instance = newInstanceForTests();
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys1").setCacheControlWithMaxAgeInSeconds(3L);
        // Fetched and cached keys
        Assert.assertEquals("keys1", instance.download());
        // Keys changed
        httpResponseBuilder = new KeysDownloaderTest.HttpResponseBuilder().setContent("keys2");
        // 1.5 seconds later
        currentTimeInMillis += 1500L;
        List<FutureTask<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return instance.download();
                }
            }));
        }
        // Resetting counters
        httpTransportGetCount.set(0);
        backgroundFetchStartedCount.set(0);
        // Force the HTTP responses to be delayed until the latch goes down to 0.
        delayHttpResponseLatch = new CountDownLatch(1);
        // Execute the all fetches in parallel.
        for (FutureTask<String> task : tasks) {
            executor.execute(task);
        }
        // Wait for all of them to complete (will use old keys that were cached)
        for (FutureTask<String> task : tasks) {
            Assert.assertEquals("keys1", task.get(5, TimeUnit.SECONDS));
        }
        // Releasing the response.
        delayHttpResponseLatch.countDown();
        // Waiting background fetch to finish
        KeysDownloaderTest.waitForLatch(backgroundFetchFinishedLatch);
        // Only a single background fetch should have been triggered
        Assert.assertEquals(1, backgroundFetchStartedCount.get());
        // Should only have hit the network once.
        Assert.assertEquals(1, httpTransportGetCount.get());
    }

    private static class TestKeysDownloader extends KeysDownloader {
        private static KeysDownloaderTest sTestInstance;

        TestKeysDownloader(Executor backgroundExecutor, HttpTransport httpTransport, String keysUrl) {
            super(backgroundExecutor, httpTransport, keysUrl);
        }

        @Override
        long getCurrentTimeInMillis() {
            return KeysDownloaderTest.TestKeysDownloader.sTestInstance.currentTimeInMillis;
        }
    }

    private static class HttpResponseBuilder {
        private String content = "content";

        private Long maxAgeInSeconds = 10L;

        private Long ageInSeconds;

        private int statusCode = HttpStatusCodes.STATUS_CODE_OK;

        public KeysDownloaderTest.HttpResponseBuilder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public KeysDownloaderTest.HttpResponseBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public KeysDownloaderTest.HttpResponseBuilder setCacheControlWithMaxAgeInSeconds(Long maxAgeInSeconds) {
            this.maxAgeInSeconds = maxAgeInSeconds;
            return this;
        }

        public KeysDownloaderTest.HttpResponseBuilder clearCacheControl() {
            this.maxAgeInSeconds = null;
            return this;
        }

        public KeysDownloaderTest.HttpResponseBuilder setAgeInSeconds(Long ageInSeconds) {
            this.ageInSeconds = ageInSeconds;
            return this;
        }

        public MockLowLevelHttpResponse build() {
            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setContent(content).setStatusCode(statusCode);
            if ((ageInSeconds) != null) {
                response.addHeader("Age", Long.toString(ageInSeconds));
            }
            if ((maxAgeInSeconds) != null) {
                response.addHeader("Cache-Control", ("public, max-age=" + (maxAgeInSeconds)));
            }
            return response;
        }
    }
}

