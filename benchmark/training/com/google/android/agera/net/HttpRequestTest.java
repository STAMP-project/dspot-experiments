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


import com.google.android.agera.net.HttpRequestCompilerStates.HTBodyHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile;
import com.google.android.agera.net.HttpRequestCompilerStates.HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile;
import com.google.android.agera.net.test.matchers.HasPrivateConstructor;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class HttpRequestTest {
    private static final String URL = "http://agera";

    private static final byte[] DATA = "Body data".getBytes();

    @Test
    public void shouldCreateHttpGetRequest() {
        final HttpRequest httpRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("GET"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
    }

    @Test
    public void shouldCreateHttpPostRequest() {
        final HttpRequest httpRequest = HttpRequests.httpPostRequest(HttpRequestTest.URL).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("POST"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
    }

    @Test
    public void shouldCreateHttpPostRequestWithData() {
        final HttpRequest httpRequest = HttpRequests.httpPostRequest(HttpRequestTest.URL).body(HttpRequestTest.DATA).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("POST"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
        MatcherAssert.assertThat(httpRequest.body, Matchers.is(HttpRequestTest.DATA));
    }

    @Test
    public void shouldCreateHttpPutRequest() {
        final HttpRequest httpRequest = HttpRequests.httpPutRequest(HttpRequestTest.URL).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("PUT"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
    }

    @Test
    public void shouldCreateHttpPutRequestWithBody() {
        final HttpRequest httpRequest = HttpRequests.httpPutRequest(HttpRequestTest.URL).body(HttpRequestTest.DATA).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("PUT"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
        MatcherAssert.assertThat(httpRequest.body, Matchers.is(HttpRequestTest.DATA));
    }

    @Test
    public void shouldCreateHttpDeleteRequest() {
        final HttpRequest httpRequest = HttpRequests.httpDeleteRequest(HttpRequestTest.URL).compile();
        MatcherAssert.assertThat(httpRequest.method, Matchers.is("DELETE"));
        MatcherAssert.assertThat(httpRequest.url, Matchers.is(HttpRequestTest.URL));
    }

    @Test
    public void shouldCreateSetHeaderFields() {
        final HttpRequest httpRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL).headerField("HEADER1", "VALUE1").headerField("HEADER2", "VALUE2").compile();
        final Map<String, String> header = httpRequest.header;
        MatcherAssert.assertThat(header, Matchers.hasEntry("HEADER1", "VALUE1"));
        MatcherAssert.assertThat(header, Matchers.hasEntry("HEADER2", "VALUE2"));
    }

    @Test
    public void shouldHaveDefaultValuesForRedirectCachesAndTimeouts() {
        final HttpRequest httpRequest = HttpRequests.httpDeleteRequest(HttpRequestTest.URL).compile();
        MatcherAssert.assertThat(httpRequest.connectTimeoutMs, Matchers.is(HttpRequestCompiler.CONNECT_TIMEOUT_MS));
        MatcherAssert.assertThat(httpRequest.readTimeoutMs, Matchers.is(HttpRequestCompiler.READ_TIMEOUT_MS));
        MatcherAssert.assertThat(httpRequest.followRedirects, Matchers.is(true));
        MatcherAssert.assertThat(httpRequest.useCaches, Matchers.is(true));
    }

    @Test
    public void shouldDisableCaches() {
        MatcherAssert.assertThat(HttpRequests.httpDeleteRequest(HttpRequestTest.URL).noCaches().compile().useCaches, Matchers.is(false));
    }

    @Test
    public void shouldDisableFollowRedirects() {
        MatcherAssert.assertThat(HttpRequests.httpDeleteRequest(HttpRequestTest.URL).noRedirects().compile().followRedirects, Matchers.is(false));
    }

    @Test
    public void shouldSetReadTimeout() {
        MatcherAssert.assertThat(HttpRequests.httpDeleteRequest(HttpRequestTest.URL).readTimeoutMs(2).compile().readTimeoutMs, Matchers.is(2));
    }

    @Test
    public void shouldSetConnectTimeout() {
        MatcherAssert.assertThat(HttpRequests.httpDeleteRequest(HttpRequestTest.URL).connectTimeoutMs(3).compile().connectTimeoutMs, Matchers.is(3));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfNoRedirects() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.noRedirects();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfNoCaches() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.noCaches();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfConnectTimeoutMs() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.connectTimeoutMs(1);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfReadTimeoutMs() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.readTimeoutMs(1);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfCompile() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.compile();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfHeaderField() {
        final HTHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpGetRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.headerField("", "");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForReuseOfCompilerOfBody() {
        final HTBodyHeaderFieldRedirectsCachesConnectionTimeoutReadTimeoutCompile incompleteRequest = HttpRequests.httpPostRequest(HttpRequestTest.URL);
        incompleteRequest.compile();
        incompleteRequest.body(new byte[]{  });
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.forClass(HttpRequest.class).verify();
    }

    @Test
    public void shouldHaveToString() {
        MatcherAssert.assertThat(HttpRequests.httpGetRequest(HttpRequestTest.URL).compile(), Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(HttpRequests.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

