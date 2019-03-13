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
package com.spotify.apollo.request;


import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.RequestMetadata;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.dispatch.EndpointInfo;
import com.spotify.apollo.environment.IncomingRequestAwareClient;
import com.spotify.apollo.route.RuleMatch;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerImplTest {
    @Mock
    RequestRunnableFactory requestFactory;

    @Mock
    EndpointRunnableFactory endpointFactory;

    @Mock
    RequestRunnable requestRunnable;

    @Mock
    Runnable runnable;

    @Mock
    OngoingRequest ongoingRequest;

    @Mock
    RuleMatch<Endpoint> match;

    @Mock
    Endpoint endpoint;

    @Mock
    EndpointInfo info;

    @Captor
    ArgumentCaptor<BiConsumer<OngoingRequest, RuleMatch<Endpoint>>> continuationCaptor;

    @Captor
    ArgumentCaptor<RequestContext> requestContextCaptor;

    RequestHandlerImpl requestHandler;

    private RequestMetadata requestMetadata;

    @Test
    public void shouldRunRequestRunnable() throws Exception {
        requestHandler.handle(ongoingRequest);
        Mockito.verify(requestRunnable).run(ArgumentMatchers.any());
    }

    @Test
    public void shouldRunEndpointRunnable() throws Exception {
        requestHandler.handle(ongoingRequest);
        Mockito.verify(requestRunnable).run(continuationCaptor.capture());
        continuationCaptor.getValue().accept(ongoingRequest, match);
        Mockito.verify(endpointFactory).create(ArgumentMatchers.eq(ongoingRequest), ArgumentMatchers.any(RequestContext.class), ArgumentMatchers.eq(endpoint));
        Mockito.verify(runnable).run();
    }

    @Test
    public void shouldReplySafelyForExceptions() throws Exception {
        Mockito.doThrow(new NullPointerException("expected")).when(requestRunnable).run(ArgumentMatchers.any());
        requestHandler.handle(ongoingRequest);
        Mockito.verify(ongoingRequest).reply(Response.forStatus(Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void shouldSetRequestContextMetadata() throws Exception {
        requestHandler.handle(ongoingRequest);
        Mockito.verify(requestRunnable).run(continuationCaptor.capture());
        continuationCaptor.getValue().accept(ongoingRequest, match);
        final RequestContext requestContext = requestContextCaptor.getValue();
        Assert.assertThat(requestContext.metadata(), Matchers.is(requestMetadata));
    }

    private static class NoopClient implements IncomingRequestAwareClient {
        @Override
        public CompletionStage<Response<ByteString>> send(Request request, Optional<Request> incoming) {
            throw new UnsupportedOperationException();
        }
    }
}

