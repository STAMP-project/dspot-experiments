/**
 * -\-\-
 * Spotify Apollo Jetty HTTP Server Module
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
package com.spotify.apollo.http.server;


import Service.Instance;
import com.google.common.collect.Lists;
import com.spotify.apollo.Status;
import com.spotify.apollo.core.Service;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.request.RequestHandler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.com.spotify.apollo.Request;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class HttpServerModuleTest {
    private static final String[] NO_ARGS = new String[0];

    @Rule
    public ExpectedException exception = ExpectedException.none();

    OkHttpClient okHttpClient = new OkHttpClient();

    @Test
    public void testCanStartRegularModule() throws Exception {
        int port = 9083;
        try (Service.Instance instance = service().start(HttpServerModuleTest.NO_ARGS, onPort(port))) {
            HttpServer server = HttpServerModule.server(instance);
            HttpServerModuleTest.assertCanNotConnect(port);
            HttpServerModuleTest.TestHandler testHandler = new HttpServerModuleTest.TestHandler();
            server.start(testHandler);
            HttpServerModuleTest.assertCanConnect(port);
            Request request = new Request.Builder().get().url(((baseUrl(port)) + "/hello/world")).build();
            Response response = okHttpClient.newCall(request).execute();
            Assert.assertThat(response.code(), CoreMatchers.is(Status.IM_A_TEAPOT.code()));
            Assert.assertThat(testHandler.requests.size(), CoreMatchers.is(1));
            OngoingRequest incomingRequest = testHandler.requests.get(0);
            Assert.assertThat(incomingRequest.request().uri(), CoreMatchers.is("/hello/world"));
            server.close();
            HttpServerModuleTest.assertCanNotConnect(port);
        }
    }

    @Test
    public void testParsesQueryParameters() throws Exception {
        int port = 9084;
        try (Service.Instance instance = service().start(HttpServerModuleTest.NO_ARGS, onPort(port))) {
            HttpServer server = HttpServerModule.server(instance);
            HttpServerModuleTest.TestHandler testHandler = new HttpServerModuleTest.TestHandler();
            server.start(testHandler);
            Request httpRequest = new Request.Builder().get().url(((baseUrl(port)) + "/query?a=foo&b=bar&b=baz")).build();
            Response response = okHttpClient.newCall(httpRequest).execute();
            Assert.assertThat(response.code(), CoreMatchers.is(Status.IM_A_TEAPOT.code()));
            Assert.assertThat(testHandler.requests.size(), CoreMatchers.is(1));
            final com.spotify.apollo.Request apolloRequest = testHandler.requests.get(0).request();
            Assert.assertThat(apolloRequest.uri(), CoreMatchers.is("/query?a=foo&b=bar&b=baz"));
            Assert.assertThat(apolloRequest.parameter("a"), CoreMatchers.is(Optional.of("foo")));
            Assert.assertThat(apolloRequest.parameter("b"), CoreMatchers.is(Optional.of("bar")));
            Assert.assertThat(apolloRequest.parameters().get("b"), CoreMatchers.is(Arrays.asList("bar", "baz")));
            Assert.assertThat(apolloRequest.parameter("c"), CoreMatchers.is(Optional.empty()));
        }
    }

    @Test
    public void testParsesHeadersParameters() throws Exception {
        int port = 9085;
        try (Service.Instance instance = service().start(HttpServerModuleTest.NO_ARGS, onPort(port))) {
            HttpServer server = HttpServerModule.server(instance);
            HttpServerModuleTest.TestHandler testHandler = new HttpServerModuleTest.TestHandler();
            server.start(testHandler);
            Request httpRequest = new Request.Builder().get().url(((baseUrl(port)) + "/headers")).addHeader("Foo", "bar").addHeader("Repeat", "once").addHeader("Repeat", "twice").build();
            Response response = okHttpClient.newCall(httpRequest).execute();
            Assert.assertThat(response.code(), CoreMatchers.is(Status.IM_A_TEAPOT.code()));
            Assert.assertThat(testHandler.requests.size(), CoreMatchers.is(1));
            final com.spotify.apollo.Request apolloRequest = testHandler.requests.get(0).request();
            Assert.assertThat(apolloRequest.uri(), CoreMatchers.is("/headers"));
            Assert.assertThat(apolloRequest.header("Foo"), CoreMatchers.is(Optional.of("bar")));
            Assert.assertThat(apolloRequest.header("Repeat"), CoreMatchers.is(Optional.of("once,twice")));
            Assert.assertThat(apolloRequest.header("Baz"), CoreMatchers.is(Optional.empty()));
            System.out.println(("apolloRequest.headers() = " + (apolloRequest.headers())));
        }
    }

    private static class TestHandler implements RequestHandler {
        List<OngoingRequest> requests = Lists.newLinkedList();

        @Override
        public void handle(OngoingRequest request) {
            requests.add(request);
            request.reply(com.spotify.apollo.Response.forStatus(Status.IM_A_TEAPOT));
        }
    }
}

