/**
 * -\-\-
 * Spotify Apollo Testing Helpers
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
package com.spotify.apollo.test;


import Status.ACCEPTED;
import Status.BAD_GATEWAY;
import Status.FORBIDDEN;
import Status.IM_A_TEAPOT;
import Status.OK;
import StubClient.NoMatchingResponseFoundException;
import StubClient.StubbedResponseBuilder;
import com.google.common.collect.ImmutableSet;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.StatusType;
import com.spotify.apollo.test.response.ResponseSource;
import com.spotify.apollo.test.response.ResponseWithDelay;
import com.spotify.apollo.test.response.Responses;
import com.spotify.apollo.test.unit.StatusTypeMatchers;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StubClientTest {
    StubClient stubClient;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final ResponseWithDelay HELLO_WORLD = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("Hello World")));

    @Test
    public void shouldSimulateDelay() throws Exception {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("Hello World"))).in(400, TimeUnit.MILLISECONDS).to("http://ping");
        final long t0 = System.currentTimeMillis();
        final CompletionStage<String> call = getStringFromPing();
        final String reply = call.toCompletableFuture().get();
        final long elapsed = (System.currentTimeMillis()) - t0;
        Assert.assertThat(reply, Matchers.is("Hello World"));
        // this can be really slow on the build server when it's under heavy load.
        Assert.assertThat(elapsed, Matchers.is(Matchers.both(Matchers.lessThan(2000L)).and(Matchers.greaterThan(350L))));
    }

    @Test
    public void shouldReturnConfiguredStatusCode() throws Exception {
        stubClient.respond(Response.of(IM_A_TEAPOT, ByteString.encodeUtf8("Hello World"))).to("http://ping");
        Response<ByteString> response = getResponseFromPing().toCompletableFuture().get();
        Assert.assertThat(response.status(), Matchers.is(IM_A_TEAPOT));
    }

    @Test
    public void shouldReplyWithStatusOnly() throws Exception {
        stubClient.respond(Response.forStatus(IM_A_TEAPOT)).to("http://ping");
        Response<ByteString> response = getResponseFromPing().toCompletableFuture().get();
        Assert.assertThat(response.status(), Matchers.is(IM_A_TEAPOT));
    }

    @Test
    public void shouldSupportNoPayloads() throws Exception {
        stubClient.respond(Response.forStatus(IM_A_TEAPOT)).to("http://ping");
        Response<ByteString> response = getResponseFromPing().toCompletableFuture().get();
        Assert.assertThat(response.payload().isPresent(), Matchers.is(false));
    }

    @Test
    public void shouldReturnStatusCodeIntegers() throws Exception {
        ResponseWithDelay response = ResponseWithDelay.forResponse(Response.of(Status.createForCode(666), ByteString.encodeUtf8("constant response")));
        ResponseSource responses = Responses.constant(response);
        stubClient.respond(responses).to("http://ping");
        Response<ByteString> reply = getResponseFromPing().toCompletableFuture().get();
        Assert.assertThat(reply.status(), StatusTypeMatchers.withCode(666));
    }

    @Test
    public void shouldReturnConstantReplies() throws Exception {
        ResponseWithDelay response = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("constant response")));
        ResponseSource responses = Responses.constant(response);
        stubClient.respond(responses).to("http://ping");
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("constant response"));
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("constant response"));
    }

    @Test
    public void shouldReturnIterativeReplies() throws Exception {
        ResponseWithDelay response1 = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("first response")));
        ResponseWithDelay response2 = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("second response")));
        List<ResponseWithDelay> responses = Arrays.asList(response1, response2);
        ResponseSource responseSequence = Responses.sequence(responses);
        stubClient.respond(responseSequence).to("http://ping");
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("first response"));
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("second response"));
    }

    @Test
    public void shouldReturnIterativeRepliesWithVaryingStatusCodes() throws Exception {
        ResponseWithDelay response1 = ResponseWithDelay.forResponse(Response.of(ACCEPTED, ByteString.encodeUtf8("first response")));
        ResponseWithDelay response2 = ResponseWithDelay.forResponse(Response.of(FORBIDDEN, ByteString.encodeUtf8("second response")));
        List<ResponseWithDelay> responses = Arrays.asList(response1, response2);
        ResponseSource responseSequence = Responses.sequence(responses);
        stubClient.respond(responseSequence).to("http://ping");
        CompletionStage<Response<ByteString>> reply1 = getResponseFromPing();
        CompletionStage<Response<ByteString>> reply2 = getResponseFromPing();
        Response<ByteString> message1 = reply1.toCompletableFuture().get();
        Response<ByteString> message2 = reply2.toCompletableFuture().get();
        Assert.assertThat(message1.status(), Matchers.is(ACCEPTED));
        Assert.assertThat(message2.status(), Matchers.is(FORBIDDEN));
        Assert.assertThat(message1.payload().get().utf8(), Matchers.is("first response"));
        Assert.assertThat(message2.payload().get().utf8(), Matchers.is("second response"));
    }

    @Test
    public void shouldDisallowInfiniteIterativeReplies() throws Exception {
        ResponseWithDelay response1 = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("first response")));
        ResponseWithDelay response2 = ResponseWithDelay.forResponse(Response.forPayload(ByteString.encodeUtf8("second response")));
        List<ResponseWithDelay> responses = Arrays.asList(response1, response2);
        ResponseSource constantResponses = Responses.sequence(responses);
        stubClient.respond(constantResponses).to("http://ping");
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("first response"));
        Assert.assertThat(getStringFromPing().toCompletableFuture().get(), Matchers.is("second response"));
        try {
            getStringFromPing().toCompletableFuture().get();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("No more responses specified!"));
        }
    }

    @Test
    public void shouldRespectCustomRequestMatcher() throws Exception {
        final String mockedUri = "http://ping";
        final String effectiveUri = "http://ping?key=value";
        Matcher<Request> requestMatcher = StubClientTest.uriStartsWith(mockedUri);
        stubClient.respond(Responses.constant(StubClientTest.HELLO_WORLD)).to(requestMatcher);
        final String reply = getString(effectiveUri).toCompletableFuture().get();
        Assert.assertThat(reply, Matchers.is("Hello World"));
    }

    @Test
    public void shouldClearSetupRequestsOnClear() throws Throwable {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("Hello World"))).to("http://ping");
        stubClient.clear();
        CompletionStage<String> future = getString("http://ping");
        exception.expect(Matchers.isA(NoMatchingResponseFoundException.class));
        try {
            future.toCompletableFuture().get();
        } catch (ExecutionException ee) {
            throw ee.getCause();
        }
        Assert.fail("should throw");
    }

    @Test
    public void shouldDisallowChangingDelayForResponseSource() throws Exception {
        StubClient.StubbedResponseBuilder builder = stubClient.respond(Responses.constant(StubClientTest.HELLO_WORLD));
        exception.expect(IllegalStateException.class);
        builder.in(13, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSupportHeadersForResponses() throws Exception {
        stubClient.respond(Response.<ByteString>ok().withHeader("foo", "bar")).to("http://ping");
        Response reply = getResponse("http://ping").toCompletableFuture().get();
        Assert.assertThat(reply.header("foo").get(), CoreMatchers.equalTo("bar"));
    }

    @Test
    public void shouldDisallowAddingDelayForResponseSource() throws Exception {
        StubClient.StubbedResponseBuilder builder = stubClient.respond(Responses.constant(StubClientTest.HELLO_WORLD));
        exception.expect(IllegalStateException.class);
        builder.in(14, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSupportTrackingRequests() throws Exception {
        stubClient.respond(Response.ok()).to(CoreMatchers.any(Request.class));
        getResponse("http://ping").toCompletableFuture().get();
        getResponse("http://pong").toCompletableFuture().get();
        Set<String> uris = stubClient.sentRequests().stream().map(Request::uri).collect(Collectors.toSet());
        Assert.assertThat(uris, CoreMatchers.equalTo(ImmutableSet.of("http://ping", "http://pong")));
    }

    @Test
    public void shouldSupportClearingRequests() throws Exception {
        stubClient.respond(Response.ok()).to(CoreMatchers.any(Request.class));
        getResponse("http://ping").toCompletableFuture().get();
        stubClient.clearRequests();
        getResponse("http://pong").toCompletableFuture().get();
        Set<String> uris = stubClient.sentRequests().stream().map(Request::uri).collect(Collectors.toSet());
        Assert.assertThat(uris, CoreMatchers.equalTo(ImmutableSet.of("http://pong")));
    }

    @Test
    public void shouldSupportTrackingRequestsAndResponses() throws Exception {
        stubClient.respond(Response.ok()).to("http://ping");
        stubClient.respond(Response.forStatus(BAD_GATEWAY)).to("http://pong");
        getResponse("http://ping").toCompletableFuture().get();
        getResponse("http://pong").toCompletableFuture().get();
        Set<StatusType> statii = stubClient.requestsAndResponses().stream().map(( requestAndResponse) -> requestAndResponse.response().status()).collect(Collectors.toSet());
        Assert.assertThat(statii, CoreMatchers.equalTo(ImmutableSet.of(OK, BAD_GATEWAY)));
    }
}

