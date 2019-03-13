/**
 * -\-\-
 * Spotify Apollo Metrics Module
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
package com.spotify.apollo.metrics;


import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.dispatch.EndpointInfo;
import com.spotify.apollo.request.EndpointRunnableFactory;
import com.spotify.apollo.request.OngoingRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
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
public class MetricsCollectingEndpointRunnableFactoryDecoratorTest {
    @Mock
    ServiceMetrics metrics;

    @Mock
    RequestMetrics requestStats;

    @Mock
    OngoingRequest ongoingRequest;

    @Mock
    Client client;

    @Mock
    Endpoint endpoint;

    @Mock
    EndpointInfo info;

    @Mock
    EndpointRunnableFactory delegate;

    @Mock
    Runnable delegateRunnable;

    @Captor
    ArgumentCaptor<OngoingRequest> ongoingRequestCaptor;

    @Captor
    ArgumentCaptor<RequestContext> requestContextCaptor;

    private EndpointRunnableFactory decorated;

    private RequestContext requestContext;

    private Request request;

    @Test
    public void shouldRunDelegate() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        Mockito.verify(delegateRunnable).run();
    }

    @Test
    public void shouldFinishOngoingRequest() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        ongoingRequestCaptor.getValue().reply(Response.ok());
        // noinspection unchecked
        Mockito.verify(ongoingRequest).reply(ArgumentMatchers.any(Response.class));
    }

    @Test
    public void shouldTrackFanout() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        requestContextCaptor.getValue().requestScopedClient().send(Request.forUri("http://example.com"));
        ongoingRequestCaptor.getValue().reply(Response.ok());
        Mockito.verify(requestStats).fanout(1);
    }

    @Test
    public void shouldTrackRequest() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        Mockito.verify(requestStats).incoming(request);
    }

    @Test
    public void shouldTrackResponse() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        Response<ByteString> response = Response.ok();
        ongoingRequestCaptor.getValue().reply(response);
        Mockito.verify(requestStats).response(response);
    }

    @Test
    public void shouldForwardDrops() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(decorated.create(ongoingRequest, requestContext, endpoint)).get();
        ongoingRequestCaptor.getValue().drop();
        Mockito.verify(requestStats).drop();
    }

    @Test
    public void shouldCopyMetadataFromIncomingRequestContext() throws Exception {
        decorated.create(ongoingRequest, requestContext, endpoint).run();
        RequestContext copied = requestContextCaptor.getValue();
        Assert.assertThat(copied.metadata(), CoreMatchers.is(requestContext.metadata()));
    }
}

