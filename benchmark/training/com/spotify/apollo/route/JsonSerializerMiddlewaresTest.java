/**
 * -\-\-
 * Spotify Apollo Extra
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
package com.spotify.apollo.route;


import Status.CONFLICT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spotify.apollo.Response;
import java.util.Optional;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonSerializerMiddlewaresTest {
    private static final ObjectWriter WRITER = new ObjectMapper().writer();

    private static class TestBean {
        public int getX() {
            return 3;
        }
    }

    @Test
    public void shouldJsonSerialize() throws Exception {
        Response<ByteString> response = Route.sync("GET", "/foo", ( rq) -> new com.spotify.apollo.route.TestBean()).withMiddleware(JsonSerializerMiddlewares.jsonSerialize(JsonSerializerMiddlewaresTest.WRITER)).handler().invoke(null).toCompletableFuture().get();
        JsonSerializerMiddlewaresTest.checkPayloadAndContentType(response);
    }

    @Test
    public void shouldJsonSerializeResponse() throws Exception {
        Response<ByteString> response = Route.sync("GET", "/foo", ( rq) -> Response.forStatus(Status.CONFLICT).withPayload(new com.spotify.apollo.route.TestBean())).withMiddleware(JsonSerializerMiddlewares.jsonSerializeResponse(JsonSerializerMiddlewaresTest.WRITER)).handler().invoke(null).toCompletableFuture().get();
        JsonSerializerMiddlewaresTest.checkPayloadAndContentType(response);
        Assert.assertThat(response.status(), CoreMatchers.equalTo(CONFLICT));
    }

    @Test
    public void shouldJsonSerializeEmptyResponse() throws Exception {
        Response<ByteString> response = Route.sync("GET", "/foo", ( rq) -> Response.forStatus(Status.CONFLICT)).withMiddleware(JsonSerializerMiddlewares.jsonSerializeResponse(JsonSerializerMiddlewaresTest.WRITER)).handler().invoke(null).toCompletableFuture().get();
        JsonSerializerMiddlewaresTest.checkContentType(response);
        Assert.assertThat(response.payload(), CoreMatchers.equalTo(Optional.empty()));
        Assert.assertThat(response.status(), CoreMatchers.equalTo(CONFLICT));
    }

    @Test
    public void shouldJsonSerializeSync() throws Exception {
        Middleware<SyncHandler<JsonSerializerMiddlewaresTest.TestBean>, AsyncHandler<Response<ByteString>>> sync = JsonSerializerMiddlewares.jsonSerializeSync(JsonSerializerMiddlewaresTest.WRITER);
        Response<ByteString> response = sync.apply(( rq) -> new com.spotify.apollo.route.TestBean()).invoke(null).toCompletableFuture().get();
        JsonSerializerMiddlewaresTest.checkPayloadAndContentType(response);
    }

    @Test
    public void shouldJsonSerializeResponseSync() throws Exception {
        Middleware<SyncHandler<Response<JsonSerializerMiddlewaresTest.TestBean>>, AsyncHandler<Response<ByteString>>> sync = JsonSerializerMiddlewares.jsonSerializeResponseSync(JsonSerializerMiddlewaresTest.WRITER);
        Response<ByteString> response = sync.apply(( rq) -> Response.forStatus(Status.CONFLICT).withPayload(new com.spotify.apollo.route.TestBean())).invoke(null).toCompletableFuture().get();
        JsonSerializerMiddlewaresTest.checkPayloadAndContentType(response);
        Assert.assertThat(response.status(), CoreMatchers.equalTo(CONFLICT));
    }
}

