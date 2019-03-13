/**
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.google.maps;


import GeoApiContext.Builder;
import GeocodingApi.Response;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.internal.ApiConfig;
import com.google.maps.internal.ApiResponse;
import com.google.maps.model.GeocodingResult;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(MediumTests.class)
public class GeoApiContextTest {
    private MockWebServer server;

    private Builder builder;

    @SuppressWarnings("unchecked")
    @Test
    public void testGetIncludesDefaultUserAgent() throws Exception {
        // Set up a mock request
        ApiResponse<Object> fakeResponse = Mockito.mock(ApiResponse.class);
        String path = "/";
        Map<String, List<String>> params = new HashMap<>();
        params.put("key", Collections.singletonList("value"));
        // Set up the fake web server
        server.enqueue(new MockResponse());
        server.start();
        setMockBaseUrl();
        // Build & execute the request using our context
        builder.build().get(new ApiConfig(path), fakeResponse.getClass(), params).awaitIgnoreError();
        // Read the headers
        server.shutdown();
        RecordedRequest request = server.takeRequest();
        Headers headers = request.getHeaders();
        boolean headerFound = false;
        for (String headerName : headers.names()) {
            if (headerName.equals("User-Agent")) {
                headerFound = true;
                String headerValue = headers.get(headerName);
                Assert.assertTrue("User agent not in correct format", headerValue.matches("GoogleGeoApiClientJava/[^\\s]+"));
            }
        }
        Assert.assertTrue("User agent header not present", headerFound);
    }

    @Test
    public void testErrorResponseRetries() throws Exception {
        // Set up mock responses
        MockResponse errorResponse = createMockBadResponse();
        MockResponse goodResponse = createMockGoodResponse();
        server.enqueue(errorResponse);
        server.enqueue(goodResponse);
        server.start();
        // Build the context under test
        setMockBaseUrl();
        // Execute
        GeocodingResult[] result = builder.build().get(new ApiConfig("/"), Response.class, "k", "v").await();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals("1600 Amphitheatre Parkway, Mountain View, CA 94043, USA", result[0].formattedAddress);
        server.shutdown();
    }

    @Test(expected = IOException.class)
    public void testSettingMaxRetries() throws Exception {
        MockResponse errorResponse = createMockBadResponse();
        MockResponse goodResponse = createMockGoodResponse();
        // Set up the fake web server
        server.enqueue(errorResponse);
        server.enqueue(errorResponse);
        server.enqueue(errorResponse);
        server.enqueue(goodResponse);
        server.start();
        setMockBaseUrl();
        // This should limit the number of retries, ensuring that the success response is NOT returned.
        builder.maxRetries(2);
        builder.build().get(new ApiConfig("/"), Response.class, "k", "v").await();
    }

    @Test(expected = IOException.class)
    public void testRetryCanBeDisabled() throws Exception {
        // Set up 2 mock responses, an error that shouldn't be retried and a success
        MockResponse errorResponse = new MockResponse();
        errorResponse.setStatus("HTTP/1.1 500 Internal server error");
        errorResponse.setBody("Uh-oh. Server Error.");
        server.enqueue(errorResponse);
        MockResponse goodResponse = new MockResponse();
        goodResponse.setResponseCode(200);
        goodResponse.setBody("{\n   \"results\" : [],\n   \"status\" : \"ZERO_RESULTS\"\n}");
        server.enqueue(goodResponse);
        server.start();
        setMockBaseUrl();
        // This should disable the retry, ensuring that the success response is NOT returned
        builder.disableRetries();
        // We should get the error response here, not the success response.
        builder.build().get(new ApiConfig("/"), Response.class, "k", "v").await();
    }

    @Test
    public void testRetryEventuallyReturnsTheRightException() throws Exception {
        MockResponse errorResponse = new MockResponse();
        errorResponse.setStatus("HTTP/1.1 500 Internal server error");
        errorResponse.setBody("Uh-oh. Server Error.");
        // Enqueue some error responses.
        for (int i = 0; i < 10; i++) {
            server.enqueue(errorResponse);
        }
        server.start();
        // Wire the mock web server to the context
        setMockBaseUrl();
        builder.retryTimeout(5, TimeUnit.SECONDS);
        try {
            builder.build().get(new ApiConfig("/"), Response.class, "k", "v").await();
        } catch (IOException ioe) {
            // Ensure the message matches the status line in the mock responses.
            Assert.assertEquals("Server Error: 500 Internal server error", ioe.getMessage());
            return;
        }
        Assert.fail("Internal server error was expected but not observed.");
    }

    @Test
    public void testQueryParamsHaveOrderPreserved() throws Exception {
        // This test is important for APIs (such as the speed limits API) where multiple parameters
        // must be provided with the same name with order preserved.
        MockResponse response = new MockResponse();
        response.setResponseCode(200);
        response.setBody("{}");
        server.enqueue(response);
        server.start();
        setMockBaseUrl();
        builder.build().get(new ApiConfig("/"), Response.class, "a", "1", "a", "2", "a", "3").awaitIgnoreError();
        server.shutdown();
        RecordedRequest request = server.takeRequest();
        String path = request.getPath();
        Assert.assertTrue(path.contains("a=1&a=2&a=3"));
    }

    @Test
    public void testToggleIfExceptionIsAllowedToRetry() throws Exception {
        // Enqueue some error responses, although only the first should be used because the response's
        // exception is not allowed to be retried.
        MockResponse overQueryLimitResponse = new MockResponse();
        overQueryLimitResponse.setStatus("HTTP/1.1 400 Internal server error");
        overQueryLimitResponse.setBody(TestUtils.retrieveBody("OverQueryLimitResponse.json"));
        server.enqueue(overQueryLimitResponse);
        server.enqueue(overQueryLimitResponse);
        server.enqueue(overQueryLimitResponse);
        server.start();
        builder.retryTimeout(1, TimeUnit.MILLISECONDS);
        builder.maxRetries(10);
        builder.setIfExceptionIsAllowedToRetry(OverQueryLimitException.class, false);
        setMockBaseUrl();
        try {
            builder.build().get(new ApiConfig("/"), Response.class, "any-key", "any-value").await();
        } catch (OverQueryLimitException e) {
            Assert.assertEquals(1, server.getRequestCount());
            return;
        }
        Assert.fail("OverQueryLimitException was expected but not observed.");
    }

    @Test
    public void testShutdown() throws InterruptedException {
        GeoApiContext context = builder.build();
        final Thread delayThread = TestUtils.findLastThreadByName("RateLimitExecutorDelayThread");
        Assert.assertNotNull("Delay thread should be created in constructor of RateLimitExecutorService", delayThread);
        Assert.assertTrue("Delay thread should start in constructor of RateLimitExecutorService", delayThread.isAlive());
        // this is needed to make sure that delay thread has reached queue.take()
        delayThread.join(10);
        context.shutdown();
        delayThread.join(10);
        Assert.assertFalse(delayThread.isAlive());
    }
}

