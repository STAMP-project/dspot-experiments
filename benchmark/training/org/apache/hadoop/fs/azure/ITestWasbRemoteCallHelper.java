/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.azure;


import CachingAuthorizer.KEY_AUTH_SERVICE_CACHING_ENABLE;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test class to hold all WasbRemoteCallHelper tests.
 */
public class ITestWasbRemoteCallHelper extends AbstractWasbTestBase {
    public static final String EMPTY_STRING = "";

    private static final int INVALID_HTTP_STATUS_CODE_999 = 999;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Test invalid status-code.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testInvalidStatusCode() throws Throwable {
        setupExpectations();
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(ITestWasbRemoteCallHelper.INVALID_HTTP_STATUS_CODE_999));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test invalid Content-Type.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testInvalidContentType() throws Throwable {
        setupExpectations();
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "text/plain"));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test missing Content-Length.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testMissingContentLength() throws Throwable {
        setupExpectations();
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test Content-Length exceeds max.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testContentLengthExceedsMax() throws Throwable {
        setupExpectations();
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "2048"));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test invalid Content-Length value
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testInvalidContentLengthValue() throws Throwable {
        setupExpectations();
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "20abc48"));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test valid JSON response.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testValidJSONResponse() throws Throwable {
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        HttpEntity mockHttpEntity = Mockito.mock(HttpEntity.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        Mockito.when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8)));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test malformed JSON response.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testMalFormedJSONResponse() throws Throwable {
        expectedEx.expect(WasbAuthorizationException.class);
        expectedEx.expectMessage("com.fasterxml.jackson.core.JsonParseException: Unexpected end-of-input in FIELD_NAME");
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        HttpEntity mockHttpEntity = Mockito.mock(HttpEntity.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        Mockito.when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream(malformedJsonResponse().getBytes(StandardCharsets.UTF_8)));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    /**
     * Test valid JSON response failure response code.
     *
     * @throws Throwable
     * 		
     */
    // (expected = WasbAuthorizationException.class)
    @Test
    public void testFailureCodeJSONResponse() throws Throwable {
        expectedEx.expect(WasbAuthorizationException.class);
        expectedEx.expectMessage("Remote authorization service encountered an error Unauthorized");
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse mockHttpResponse = Mockito.mock(HttpResponse.class);
        HttpEntity mockHttpEntity = Mockito.mock(HttpEntity.class);
        Mockito.when(mockHttpClient.execute(Mockito.<HttpGet>any())).thenReturn(mockHttpResponse);
        Mockito.when(mockHttpResponse.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponse.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        Mockito.when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream(failureCodeJsonResponse().getBytes(StandardCharsets.UTF_8)));
        // finished setting up mocks
        performop(mockHttpClient);
    }

    @Test
    public void testWhenOneInstanceIsDown() throws Throwable {
        boolean isAuthorizationCachingEnabled = fs.getConf().getBoolean(KEY_AUTH_SERVICE_CACHING_ENABLE, false);
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpEntity mockHttpEntity = Mockito.mock(HttpEntity.class);
        HttpResponse mockHttpResponseService1 = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseService1.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Mockito.when(mockHttpResponseService1.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseService1.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseService1.getEntity()).thenReturn(mockHttpEntity);
        HttpResponse mockHttpResponseService2 = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseService2.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_OK));
        Mockito.when(mockHttpResponseService2.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseService2.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseService2.getEntity()).thenReturn(mockHttpEntity);
        HttpResponse mockHttpResponseServiceLocal = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseServiceLocal.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Mockito.when(mockHttpResponseServiceLocal.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseServiceLocal.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseServiceLocal.getEntity()).thenReturn(mockHttpEntity);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService1()))).thenReturn(mockHttpResponseService1);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService2()))).thenReturn(mockHttpResponseService2);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForServiceLocal()))).thenReturn(mockHttpResponseServiceLocal);
        // Need 2 times because performop()  does 2 fs operations.
        Mockito.when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8)));
        // finished setting up mocks
        performop(mockHttpClient);
        int expectedNumberOfInvocations = (isAuthorizationCachingEnabled) ? 2 : 3;
        Mockito.verify(mockHttpClient, Mockito.times(expectedNumberOfInvocations)).execute(Mockito.argThat(new ITestWasbRemoteCallHelper.HttpGetForServiceLocal()));
        Mockito.verify(mockHttpClient, Mockito.times(expectedNumberOfInvocations)).execute(Mockito.argThat(new ITestWasbRemoteCallHelper.HttpGetForService2()));
    }

    @Test
    public void testWhenServiceInstancesAreDown() throws Throwable {
        // expectedEx.expect(WasbAuthorizationException.class);
        // set up mocks
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpEntity mockHttpEntity = Mockito.mock(HttpEntity.class);
        HttpResponse mockHttpResponseService1 = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseService1.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Mockito.when(mockHttpResponseService1.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseService1.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseService1.getEntity()).thenReturn(mockHttpEntity);
        HttpResponse mockHttpResponseService2 = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseService2.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Mockito.when(mockHttpResponseService2.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseService2.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseService2.getEntity()).thenReturn(mockHttpEntity);
        HttpResponse mockHttpResponseService3 = Mockito.mock(HttpResponse.class);
        Mockito.when(mockHttpResponseService3.getStatusLine()).thenReturn(newStatusLine(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        Mockito.when(mockHttpResponseService3.getFirstHeader("Content-Type")).thenReturn(newHeader("Content-Type", "application/json"));
        Mockito.when(mockHttpResponseService3.getFirstHeader("Content-Length")).thenReturn(newHeader("Content-Length", "1024"));
        Mockito.when(mockHttpResponseService3.getEntity()).thenReturn(mockHttpEntity);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService1()))).thenReturn(mockHttpResponseService1);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService2()))).thenReturn(mockHttpResponseService2);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForServiceLocal()))).thenReturn(mockHttpResponseService3);
        // Need 3 times because performop()  does 3 fs operations.
        Mockito.when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8))).thenReturn(new ByteArrayInputStream(validJsonResponse().getBytes(StandardCharsets.UTF_8)));
        // finished setting up mocks
        try {
            performop(mockHttpClient);
        } catch (WasbAuthorizationException e) {
            e.printStackTrace();
            Mockito.verify(mockHttpClient, Mockito.atLeast(2)).execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService1()));
            Mockito.verify(mockHttpClient, Mockito.atLeast(2)).execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForService2()));
            Mockito.verify(mockHttpClient, Mockito.atLeast(3)).execute(ArgumentMatchers.argThat(new ITestWasbRemoteCallHelper.HttpGetForServiceLocal()));
            Mockito.verify(mockHttpClient, Mockito.times(7)).execute(Mockito.<HttpGet>any());
        }
    }

    private class HttpGetForService1 implements ArgumentMatcher<HttpGet> {
        @Override
        public boolean matches(HttpGet httpGet) {
            return ITestWasbRemoteCallHelper.checkHttpGetMatchHost(httpGet, "localhost1");
        }
    }

    private class HttpGetForService2 implements ArgumentMatcher<HttpGet> {
        @Override
        public boolean matches(HttpGet httpGet) {
            return ITestWasbRemoteCallHelper.checkHttpGetMatchHost(httpGet, "localhost2");
        }
    }

    private class HttpGetForServiceLocal implements ArgumentMatcher<HttpGet> {
        @Override
        public boolean matches(HttpGet httpGet) {
            try {
                return ITestWasbRemoteCallHelper.checkHttpGetMatchHost(httpGet, InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException e) {
                return ITestWasbRemoteCallHelper.checkHttpGetMatchHost(httpGet, "localhost");
            }
        }
    }
}

