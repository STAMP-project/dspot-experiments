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
package com.spotify.apollo.dispatch;


import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.request.OngoingRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.helpers.MessageFormatter;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;


@RunWith(MockitoJUnitRunner.class)
public class EndpointInvocationHandlerTest {
    private EndpointInvocationHandler handler;

    @Mock
    private OngoingRequest ongoingRequest;

    @Mock
    private RequestContext requestContext;

    @Mock
    private Endpoint endpoint;

    @Mock
    private Response<ByteString> response;

    @Captor
    private ArgumentCaptor<Response<ByteString>> messageArgumentCaptor;

    private CompletableFuture<Response<ByteString>> future;

    private TestLogger testLogger = TestLoggerFactory.getTestLogger(EndpointInvocationHandler.class);

    @Rule
    public TestLoggerFactoryResetRule resetRule = new TestLoggerFactoryResetRule();

    @Test
    public void shouldRespondWithResponseMessageForOk() throws Exception {
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(CompletableFuture.completedFuture(response));
        handler.handle(ongoingRequest, requestContext, endpoint);
        Mockito.verify(ongoingRequest).reply(response);
    }

    @Test
    public void shouldRespondWith500ForGeneralException() throws Exception {
        RuntimeException exception = new RuntimeException("expected");
        future.completeExceptionally(exception);
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(future);
        handler.handle(ongoingRequest, requestContext, endpoint);
        Mockito.verify(ongoingRequest).reply(messageArgumentCaptor.capture());
        Assert.assertThat(messageArgumentCaptor.getValue().status().code(), CoreMatchers.equalTo(Status.INTERNAL_SERVER_ERROR.code()));
    }

    @Test
    public void shouldRespondWithDetailMessageForExceptionsToNonClientCallers() throws Exception {
        RuntimeException exception = new RuntimeException("expected");
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(future);
        future.completeExceptionally(exception);
        handler.handle(ongoingRequest, requestContext, endpoint);
        Mockito.verify(ongoingRequest).reply(messageArgumentCaptor.capture());
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.containsString(exception.getMessage()));
    }

    @Test
    public void shouldNotBlockOnFutures() throws Exception {
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(future);
        handler.handle(ongoingRequest, requestContext, endpoint);
        // wait a little to at least make this test flaky if the underlying code is broken
        Thread.sleep(50);
        Mockito.verifyZeroInteractions(ongoingRequest);
        future.complete(response);
        Mockito.verify(ongoingRequest).reply(response);
    }

    @Test
    public void shouldUnwrapMultiLineExceptions() throws Exception {
        RuntimeException exception = new RuntimeException("expected\nwith multiple\rlines");
        future.completeExceptionally(exception);
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(future);
        handler.handle(ongoingRequest, requestContext, endpoint);
        Mockito.verify(ongoingRequest).reply(messageArgumentCaptor.capture());
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.containsString("expected"));
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.containsString("with multiple"));
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.containsString("lines"));
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.not(CoreMatchers.containsString("\r")));
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.not(CoreMatchers.containsString("\n")));
    }

    @Test
    public void shouldRespondWithDetailMessageForSyncExceptionsToNonClientCallers() throws Exception {
        RuntimeException exception = new RuntimeException("expected");
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenThrow(exception);
        handler.handle(ongoingRequest, requestContext, endpoint);
        Mockito.verify(ongoingRequest).reply(messageArgumentCaptor.capture());
        Assert.assertThat(messageArgumentCaptor.getValue().status().reasonPhrase(), CoreMatchers.containsString(exception.getMessage()));
    }

    @Test
    public void shouldLogExceptionsWhenReplying() throws Exception {
        Mockito.when(endpoint.invoke(ArgumentMatchers.any(RequestContext.class))).thenReturn(CompletableFuture.completedFuture(response));
        // noinspection unchecked
        Mockito.doThrow(new RuntimeException("log this exception!")).when(ongoingRequest).reply(ArgumentMatchers.any(Response.class));
        handler.handle(ongoingRequest, requestContext, endpoint);
        List<String> eventMessages = testLogger.getLoggingEvents().stream().filter(( loggingEvent) -> (loggingEvent.getLevel()) == Level.ERROR).map(( loggingEvent) -> MessageFormatter.arrayFormat(loggingEvent.getMessage(), loggingEvent.getArguments().toArray()).getMessage()).collect(Collectors.toList());
        Assert.assertThat(eventMessages, Matchers.hasSize(1));
        Assert.assertThat(eventMessages, CoreMatchers.hasItem(CoreMatchers.containsString("Exception caught when replying")));
    }
}

