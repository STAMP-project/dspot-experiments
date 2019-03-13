/**
 * -\-\-
 * Spotify Apollo API Implementations
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


import Status.BAD_REQUEST;
import Status.OK;
import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Payloads;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Serializer;
import com.spotify.apollo.Status;
import com.spotify.apollo.StatusType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MiddlewaresTest {
    AsyncHandler<Response<ByteString>> delegate;

    AsyncHandler<Object> serializationDelegate;

    @Mock
    RequestContext requestContext;

    @Mock
    Request request;

    @Mock
    Serializer serializer;

    CompletableFuture<Response<ByteString>> future;

    CompletableFuture<Object> serializationFuture;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void asShouldSerializeCharSequenceAsString() throws Exception {
        serializationFuture.complete(new StringBuilder("hi there"));
        Assert.assertThat(getResult(Middlewares.autoSerialize(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(ByteString.encodeUtf8("hi there"))));
    }

    @Test
    public void asShouldSerializeStringAsString() throws Exception {
        serializationFuture.complete("hi there");
        Assert.assertThat(getResult(Middlewares.autoSerialize(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(ByteString.encodeUtf8("hi there"))));
    }

    @Test
    public void asShouldSerializeResponseCharSequenceAsString() throws Exception {
        serializationFuture.complete(Response.forPayload("hi there"));
        Assert.assertThat(getResult(Middlewares.autoSerialize(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(ByteString.encodeUtf8("hi there"))));
    }

    @Test
    public void asShouldNotSerializeByteString() throws Exception {
        ByteString byteString = ByteString.encodeUtf8("this is binary");
        serializationFuture.complete(byteString);
        Assert.assertThat(getResult(Middlewares.autoSerialize(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(byteString)));
    }

    @Test
    public void asShouldNotSerializeResponseByteString() throws Exception {
        ByteString byteString = ByteString.encodeUtf8("this is binary");
        serializationFuture.complete(Response.forPayload(byteString));
        Assert.assertThat(getResult(Middlewares.autoSerialize(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(byteString)));
    }

    @Test
    public void asShouldSerializeObjectAsJson() throws Exception {
        class TestData {
            public String theString = "hi";

            public int theInteger = 42;
        }
        serializationFuture.complete(new TestData());
        // noinspection ConstantConditions
        String json = getResult(Middlewares.autoSerialize(serializationDelegate)).payload().get().utf8();
        Assert.assertThat(json, Matchers.equalToIgnoringWhiteSpace("{\"theString\":\"hi\",\"theInteger\":42}"));
    }

    @Test
    public void asShouldSerializeResponseObjectAsJson() throws Exception {
        class TestData {
            public String theString = "hi";

            public int theInteger = 42;
        }
        serializationFuture.complete(Response.forPayload(new TestData()));
        // noinspection ConstantConditions
        String json = getResult(Middlewares.autoSerialize(serializationDelegate)).payload().get().utf8();
        Assert.assertThat(json, Matchers.equalToIgnoringWhiteSpace("{\"theString\":\"hi\",\"theInteger\":42}"));
    }

    @Test
    public void httpShouldSetContentLengthFor200() throws Exception {
        future.complete(Response.of(OK, ByteString.of(((byte) (14)), ((byte) (19)))));
        String header = getResult(Middlewares.httpPayloadSemantics(delegate)).header("Content-Length").get();
        Assert.assertThat(Integer.parseInt(header), CoreMatchers.equalTo(2));
    }

    @Test
    public void httpShouldAppendPayloadFor200() throws Exception {
        ByteString payload = ByteString.of(((byte) (14)), ((byte) (19)));
        future.complete(Response.of(OK, payload));
        Assert.assertThat(getResult(Middlewares.httpPayloadSemantics(delegate)).payload(), CoreMatchers.equalTo(Optional.of(payload)));
    }

    @Test
    public void httpShouldNotSetContentLengthOrAppendPayloadForInvalidStatusCodes() throws Exception {
        List<StatusType> invalid = ImmutableList.of(Status.createForCode(100), Status.NO_CONTENT, Status.NOT_MODIFIED);
        for (StatusType status : invalid) {
            CompletableFuture<Response<ByteString>> future = new CompletableFuture<>();
            delegate = ( requestContext) -> future;
            future.complete(Response.of(status, ByteString.of(((byte) (14)), ((byte) (19)))));
            Response<ByteString> result = getResult(Middlewares.httpPayloadSemantics(delegate));
            Optional<String> header = result.header("Content-Length");
            Assert.assertThat(("no content-length for " + status), header, CoreMatchers.is(Optional.empty()));
            Assert.assertThat(("no payload for " + status), result.payload(), CoreMatchers.is(Optional.<ByteString>empty()));
        }
    }

    @Test
    public void httpShouldSetContentLengthForHeadAnd200() throws Exception {
        Mockito.when(request.method()).thenReturn("HEAD");
        future.complete(Response.of(OK, ByteString.of(((byte) (14)), ((byte) (19)))));
        String header = getResult(Middlewares.httpPayloadSemantics(delegate)).header("Content-Length").get();
        Assert.assertThat(Integer.parseInt(header), CoreMatchers.equalTo(2));
    }

    @Test
    public void httpShouldNotAppendPayloadForHeadAnd200() throws Exception {
        Mockito.when(request.method()).thenReturn("HEAD");
        future.complete(Response.of(OK, ByteString.of(((byte) (14)), ((byte) (19)))));
        Assert.assertThat(getResult(Middlewares.httpPayloadSemantics(delegate)).payload(), CoreMatchers.is(Optional.<ByteString>empty()));
    }

    @Test
    public void contentTypeShouldAddToNonResponse() throws Exception {
        serializationFuture.complete("hi");
        String contentType = getResult(Middlewares.replyContentType("text/plain").apply(serializationDelegate())).header("Content-Type").get();
        Assert.assertThat(contentType, CoreMatchers.equalTo("text/plain"));
    }

    @Test
    public void contentTypeShouldAddToResponse() throws Exception {
        serializationFuture.complete(Response.forPayload("hi"));
        String contentType = getResult(Middlewares.replyContentType("text/plain").apply(serializationDelegate())).header("Content-Type").get();
        Assert.assertThat(contentType, CoreMatchers.equalTo("text/plain"));
    }

    @Test
    public void serializerShouldSerialize() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload));
        serializationFuture.complete(response);
        Assert.assertThat(getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(serializedPayload)));
    }

    @Test
    public void serializerShouldSerializeResponses() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload));
        serializationFuture.complete(Response.forPayload(response));
        Assert.assertThat(getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).payload(), CoreMatchers.equalTo(Optional.of(serializedPayload)));
    }

    @Test
    public void serializerShouldCopyHeadersFromResponses() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload));
        serializationFuture.complete(Response.forPayload(response).withHeader("X-Foo", "Fie"));
        Optional<String> header = getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).header("X-Foo");
        Assert.assertThat(header.get(), CoreMatchers.equalTo("Fie"));
    }

    @Test
    public void serializerShouldCopyStatusCodeFromResponses() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload));
        serializationFuture.complete(Response.of(Status.CREATED, response));
        Assert.assertThat(getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).status(), CoreMatchers.equalTo(Status.CREATED));
    }

    @Test
    public void serializerShouldSetContentTypeIfPresent() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload, "coool stuff"));
        serializationFuture.complete(response);
        String contentType = getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).header("Content-Type").get();
        Assert.assertThat(contentType, CoreMatchers.equalTo("coool stuff"));
    }

    @Test
    public void serializerShouldNotSetContentTypeIfAbsent() throws Exception {
        Object response = new Object();
        ByteString serializedPayload = ByteString.encodeUtf8("hi there");
        Mockito.when(serializer.serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.eq(response))).thenReturn(Payloads.create(serializedPayload, Optional.<String>empty()));
        serializationFuture.complete(response);
        Optional<String> contentType = getResult(Middlewares.serialize(serializer).apply(serializationDelegate)).header("Content-Type");
        Assert.assertThat(contentType, CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void serializerShouldNotSerializeNull() throws Exception {
        serializationFuture.complete(Response.forStatus(BAD_REQUEST));
        Response<ByteString> response = getResult(Middlewares.serialize(serializer).apply(serializationDelegate));
        Assert.assertThat(response.status(), CoreMatchers.equalTo(BAD_REQUEST));
        Mockito.verify(serializer, Mockito.never()).serialize(ArgumentMatchers.any(Request.class), ArgumentMatchers.any());
    }
}

