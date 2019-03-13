/**
 * -\-\-
 * Spotify Apollo okhttp Client Module
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.http.client;


import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Optional;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;


public class HttpClientTest {
    @Rule
    public final MockServerRule mockServerRule = new MockServerRule(this);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // this field gets set by the MockServerRule
    @SuppressWarnings("unused")
    private MockServerClient mockServerClient;

    @Test
    public void testSend() throws Exception {
        mockServerClient.when(request().withMethod("GET").withPath("/foo.php").withQueryStringParameter("bar", "baz").withQueryStringParameter("qur", "quz")).respond(response().withStatusCode(204));
        String uri = String.format("http://localhost:%d/foo.php?bar=baz&qur=quz", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "GET");
        Response<ByteString> response = HttpClient.createUnconfigured().send(request, Optional.empty()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), HttpClientTest.withCode(204));
        MatcherAssert.assertThat(response.payload(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testSendWithCustomHeader() throws Exception {
        mockServerClient.when(request().withMethod("GET").withPath("/foo.php").withHeader("x-my-special-header", "yes")).respond(response().withStatusCode(200).withHeader("x-got-the-special-header", "yup"));
        String uri = String.format("http://localhost:%d/foo.php", mockServerRule.getHttpPort());
        Response<ByteString> response = HttpClient.createUnconfigured().send(Request.forUri(uri, "GET").withHeader("x-my-special-header", "yes"), Optional.empty()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), HttpClientTest.withCode(200));
        MatcherAssert.assertThat(response.header("x-got-the-special-header").get(), CoreMatchers.equalTo("yup"));
    }

    @Test
    public void testSendWithBody() throws Exception {
        mockServerClient.when(request().withMethod("POST").withPath("/foo.php").withQueryStringParameter("bar", "baz").withQueryStringParameter("qur", "quz").withHeader("Content-Type", "application/x-spotify-greeting").withBody("hello")).respond(response().withStatusCode(200).withHeader("Content-Type", "application/x-spotify-location").withHeader("Vary", "Content-Type").withHeader("Vary", "Accept").withBody("world"));
        String uri = String.format("http://localhost:%d/foo.php?bar=baz&qur=quz", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "POST").withHeader("Content-Type", "application/x-spotify-greeting").withPayload(ByteString.encodeUtf8("hello"));
        Response<ByteString> response = HttpClient.createUnconfigured().send(request, Optional.empty()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), HttpClientTest.withCode(200));
        MatcherAssert.assertThat(response.headerEntries(), Matchers.allOf(CoreMatchers.hasItem(new AbstractMap.SimpleEntry("Content-Type", "application/x-spotify-location")), CoreMatchers.hasItem(new AbstractMap.SimpleEntry("Vary", "Content-Type, Accept"))));
        MatcherAssert.assertThat(response.payload(), Matchers.is(Optional.of(ByteString.encodeUtf8("world"))));
    }

    @Test
    public void testTimeout() throws Exception {
        callback(callback().withCallbackClass(SleepCallback.class.getCanonicalName()));
        String uri = String.format("http://localhost:%d/foo.php", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "GET");
        Response<ByteString> response = HttpClient.createUnconfigured().send(request, Optional.empty()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), HttpClientTest.withCode(200));
    }

    @Test
    public void testTimeoutFail() throws Exception {
        callback(callback().withCallbackClass(SleepCallback.class.getCanonicalName()));
        String uri = String.format("http://localhost:%d/foo.php", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "GET").withTtl(Duration.ofMillis(200));
        thrown.expect(HttpClientTest.hasCause(Matchers.instanceOf(SocketTimeoutException.class)));
        HttpClient.createUnconfigured().send(request, Optional.empty()).toCompletableFuture().get();
    }

    @Test
    public void testAuthContextPropagation() throws Exception {
        mockServerClient.when(request().withHeader("Authorization", "Basic dXNlcjpwYXNz")).respond(response().withHeader("x-auth-was-fine", "yes"));
        String uri = String.format("http://localhost:%d/foo.php", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "GET");
        Request originalRequest = Request.forUri("http://original.uri/").withHeader("Authorization", "Basic dXNlcjpwYXNz");
        final Response<ByteString> response = HttpClient.createUnconfigured().send(request, Optional.of(originalRequest)).toCompletableFuture().get();
        MatcherAssert.assertThat(response.header("x-auth-was-fine").get(), CoreMatchers.equalTo("yes"));
    }

    @Test
    public void testSendWeirdStatus() throws Exception {
        mockServerClient.when(request().withMethod("GET").withPath("/foo.php")).respond(response().withStatusCode(299));
        String uri = String.format("http://localhost:%d/foo.php", mockServerRule.getHttpPort());
        Request request = Request.forUri(uri, "GET");
        final Response<ByteString> response = HttpClient.createUnconfigured().send(request, Optional.empty()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), HttpClientTest.withCode(299));
        MatcherAssert.assertThat(response.payload(), Matchers.is(Optional.empty()));
    }
}

