/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.net;


import com.google.android.agera.net.test.matchers.HasPrivateConstructor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public final class HttpFunctionsTest {
    private static final String TEST_PROTOCOL = "httptest";

    private static final String TEST_URI = (HttpFunctionsTest.TEST_PROTOCOL) + "://path";

    private static final HttpRequest HTTP_GET_REQUEST = HttpRequests.httpGetRequest(HttpFunctionsTest.TEST_URI).compile();

    private static final HttpRequest HTTP_POST_REQUEST = HttpRequests.httpPostRequest(HttpFunctionsTest.TEST_URI).compile();

    private static final HttpRequest HTTP_PUT_REQUEST = HttpRequests.httpPutRequest(HttpFunctionsTest.TEST_URI).compile();

    private static final HttpRequest HTTP_DELETE_REQUEST = HttpRequests.httpDeleteRequest(HttpFunctionsTest.TEST_URI).compile();

    private static final byte[] RESPONSE_BODY = new byte[]{ 2, 3, 4 };

    private static final byte[] REQUEST_BODY = new byte[]{ 1, 2, 3 };

    private static final HttpRequest HTTP_GET_REQUEST_WITH_HEADERS = HttpRequests.httpGetRequest(HttpFunctionsTest.TEST_URI).headerField("name", "value").headerField("name2", "value2").compile();

    private static final HttpRequest HTTP_POST_WITH_BODY_REQUEST = HttpRequests.httpPostRequest(HttpFunctionsTest.TEST_URI).body(HttpFunctionsTest.REQUEST_BODY).compile();

    private static final HttpRequest HTTP_PUT_WITH_BODY_REQUEST = HttpRequests.httpPutRequest(HttpFunctionsTest.TEST_URI).body(HttpFunctionsTest.REQUEST_BODY).compile();

    private static final String GET_METHOD = "GET";

    private static final String POST_METHOD = "POST";

    private static final String PUT_METHOD = "PUT";

    private static final String DELETE_METHOD = "DELETE";

    private static final byte[] EMPTY_BODY = new byte[0];

    private static HttpURLConnection mockHttpURLConnection;

    @Test
    public void shouldPassOnGetMethod() throws Throwable {
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setRequestMethod(HttpFunctionsTest.GET_METHOD);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldPassOnPostMethod() throws Throwable {
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_POST_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setRequestMethod(HttpFunctionsTest.POST_METHOD);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldPassOnPutMethod() throws Throwable {
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_PUT_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setRequestMethod(HttpFunctionsTest.PUT_METHOD);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldPassOnDeleteMethod() throws Throwable {
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_DELETE_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setRequestMethod(HttpFunctionsTest.DELETE_METHOD);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldGracefullyHandleProtocolExceptionForInvalidMethod() throws Throwable {
        Mockito.doThrow(ProtocolException.class).when(HttpFunctionsTest.mockHttpURLConnection).setRequestMethod(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_DELETE_REQUEST).getFailure(), Matchers.instanceOf(ProtocolException.class));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldPassOnRequestHeaders() throws Throwable {
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST_WITH_HEADERS), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).addRequestProperty("name", "value");
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).addRequestProperty("name2", "value2");
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldPassOnResponseHeadersAsLowerCase() throws Throwable {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn(HttpFunctionsTest.RESPONSE_BODY.length);
        final Map<String, List<String>> headerFields = new HashMap<>();
        headerFields.put("NAmE", Collections.singletonList("value"));
        headerFields.put("naMe2", Collections.singletonList("value2"));
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getHeaderFields()).thenReturn(headerFields);
        final HttpResponse httpResponse = HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get();
        MatcherAssert.assertThat(httpResponse.header.size(), Matchers.is(2));
        MatcherAssert.assertThat(httpResponse.header, Matchers.hasEntry("name", "value"));
        MatcherAssert.assertThat(httpResponse.header, Matchers.hasEntry("name2", "value2"));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldNotPassOnNullResponseHeader() throws Throwable {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn(HttpFunctionsTest.RESPONSE_BODY.length);
        final Map<String, List<String>> headerFields = new HashMap<>();
        headerFields.put(null, Collections.singletonList("value"));
        headerFields.put("naMe2", Collections.singletonList("value2"));
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getHeaderFields()).thenReturn(headerFields);
        final HttpResponse httpResponse = HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get();
        MatcherAssert.assertThat(httpResponse.header.size(), Matchers.is(1));
        MatcherAssert.assertThat(httpResponse.header, Matchers.hasEntry("name2", "value2"));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldGetOutputStreamForPutWithBody() throws Throwable {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_PUT_WITH_BODY_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setDoInput(true);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
        MatcherAssert.assertThat(outputStream.toByteArray(), Matchers.is(HttpFunctionsTest.REQUEST_BODY));
    }

    @Test
    public void shouldGetOutputStreamForPostWithBody() throws Throwable {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_POST_WITH_BODY_REQUEST), Matchers.is(Matchers.notNullValue()));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).setDoInput(true);
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
        MatcherAssert.assertThat(outputStream.toByteArray(), Matchers.is(HttpFunctionsTest.REQUEST_BODY));
    }

    @Test
    public void shouldGetByteArrayFromGetResponse() throws Throwable {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn(HttpFunctionsTest.RESPONSE_BODY.length);
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get().getBody(), Matchers.is(HttpFunctionsTest.RESPONSE_BODY));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldGetByteArrayFromGetResponseOfUnknownLength() throws Throwable {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn((-1));
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get().getBody(), Matchers.is(HttpFunctionsTest.RESPONSE_BODY));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldGetEmptyBodyFromGetResponseOfZeroLength() throws Throwable {
        final InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn(0);
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get().getBody(), Matchers.is(HttpFunctionsTest.EMPTY_BODY));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
        Mockito.verifyZeroInteractions(inputStream);
    }

    @Test
    public void shouldReturnErrorStreamForFailingInputStream() throws Throwable {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(HttpFunctionsTest.RESPONSE_BODY);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getContentLength()).thenReturn((-1));
        // noinspection unchecked
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getInputStream()).thenThrow(IOException.class);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getErrorStream()).thenReturn(inputStream);
        MatcherAssert.assertThat(HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get().getBody(), Matchers.is(HttpFunctionsTest.RESPONSE_BODY));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldReturnResponseCodeAndMessage() throws Throwable {
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getResponseCode()).thenReturn(200);
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getResponseMessage()).thenReturn("message");
        final HttpResponse httpResponse = HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get();
        MatcherAssert.assertThat(httpResponse.getResponseCode(), Matchers.is(200));
        MatcherAssert.assertThat(httpResponse.getResponseMessage(), Matchers.is("message"));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldReturnEmptyStringForNullResponseMessage() throws Throwable {
        Mockito.when(HttpFunctionsTest.mockHttpURLConnection.getResponseMessage()).thenReturn(null);
        final HttpResponse httpResponse = HttpFunctions.httpFunction().apply(HttpFunctionsTest.HTTP_GET_REQUEST).get();
        MatcherAssert.assertThat(httpResponse.getResponseMessage(), Matchers.is(""));
        Mockito.verify(HttpFunctionsTest.mockHttpURLConnection).disconnect();
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(HttpFunctions.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

