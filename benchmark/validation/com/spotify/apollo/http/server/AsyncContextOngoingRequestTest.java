/**
 * -\-\-
 * Spotify Apollo Jetty HTTP Server Module
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
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


import Status.ACCEPTED;
import Status.INTERNAL_SERVER_ERROR;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.request.RequestMetadataImpl;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;


public class AsyncContextOngoingRequestTest {
    private static final Request REQUEST = Request.forUri("http://localhost:888");

    private static final Response<ByteString> DROPPED = Response.forStatus(Status.INTERNAL_SERVER_ERROR.withReasonPhrase("dropped"));

    private final TestLogger testLogger = TestLoggerFactory.getTestLogger(AsyncContextOngoingRequest.class);

    private AsyncContextOngoingRequest ongoingRequest;

    private MockHttpServletResponse response;

    @Mock
    private RequestOutcomeConsumer logger;

    @Mock
    AsyncContext asyncContext;

    @Mock
    ServletOutputStream outputStream;

    @Rule
    public TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();

    // note: this test may fail when running in IntelliJ, due to
    // https://youtrack.jetbrains.com/issue/IDEA-122783
    @Test
    public void shouldLogWarningOnErrorWritingResponse() throws Exception {
        HttpServletResponse spy = Mockito.spy(response);
        Mockito.when(asyncContext.getResponse()).thenReturn(spy);
        Mockito.doReturn(outputStream).when(spy).getOutputStream();
        Mockito.doThrow(new IOException("expected")).when(outputStream).write(ArgumentMatchers.any(byte[].class));
        ongoingRequest.reply(Response.forPayload(ByteString.encodeUtf8("floop")));
        List<LoggingEvent> events = testLogger.getLoggingEvents().stream().filter(( event) -> (event.getLevel()) == Level.WARN).filter(( event) -> event.getMessage().contains("Failed to write response")).collect(Collectors.toList());
        Assert.assertThat(events, Matchers.hasSize(1));
    }

    @Test
    public void shouldCompleteContextOnReply() throws Exception {
        ongoingRequest.reply(Response.forStatus(ACCEPTED));
        Mockito.verify(asyncContext).complete();
    }

    @Test
    public void shouldReplyOnlyOnce() throws Exception {
        ongoingRequest.reply(Response.forStatus(ACCEPTED));
        ongoingRequest.reply(Response.forStatus(INTERNAL_SERVER_ERROR));
        Assert.assertThat(response.getStatus(), CoreMatchers.is(ACCEPTED.code()));
    }

    @Test
    public void shouldForwardRepliesToJetty() throws Exception {
        ongoingRequest.reply(Response.forStatus(Status.IM_A_TEAPOT).withPayload(ByteString.encodeUtf8("hi there")));
        Assert.assertThat(response.getStatus(), CoreMatchers.is(Status.IM_A_TEAPOT.code()));
        Assert.assertThat(response.getErrorMessage(), CoreMatchers.is(Status.IM_A_TEAPOT.reasonPhrase()));
        Assert.assertThat(response.getContentAsString(), CoreMatchers.is("hi there"));
    }

    @Test
    public void shouldRespond500ForDrop() throws Exception {
        ongoingRequest.drop();
        Mockito.verify(asyncContext).complete();
        Assert.assertThat(response.getStatus(), CoreMatchers.is(500));
        Assert.assertThat(response.getErrorMessage(), CoreMatchers.is("dropped"));
    }

    @Test
    public void shouldSendResponsesToConsumer() throws Exception {
        Response<ByteString> hi = Response.forStatus(Status.BAD_REQUEST.withReasonPhrase("hi"));
        ongoingRequest.reply(hi);
        Mockito.verify(logger).accept(ongoingRequest, Optional.of(hi));
    }

    @Test
    public void shouldSendDropsToConsumer() throws Exception {
        ongoingRequest.drop();
        Mockito.verify(logger).accept(ongoingRequest, Optional.of(AsyncContextOngoingRequestTest.DROPPED));
    }

    @Test
    public void shouldNotAllowOverridingDropHandling() throws Exception {
        OngoingRequest ongoingRequest = new AsyncContextOngoingRequestTest.Subclassed(AsyncContextOngoingRequestTest.REQUEST, asyncContext, logger);
        ongoingRequest.drop();
        Mockito.verify(logger).accept(ongoingRequest, Optional.of(AsyncContextOngoingRequestTest.DROPPED));
    }

    private static class Subclassed extends AsyncContextOngoingRequest {
        Subclassed(Request request, AsyncContext asyncContext, RequestOutcomeConsumer logger) {
            super(request, asyncContext, logger, RequestMetadataImpl.create(Instant.EPOCH, Optional.empty(), Optional.empty()));
        }

        @Override
        public void reply(Response<ByteString> response) {
            super.reply(Response.forStatus(response.status().withReasonPhrase("overridden")));
        }
    }
}

