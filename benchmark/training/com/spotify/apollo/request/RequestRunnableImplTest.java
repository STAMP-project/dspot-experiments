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


import Status.METHOD_NOT_ALLOWED;
import Status.NO_CONTENT;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.dispatch.EndpointInfo;
import com.spotify.apollo.route.ApplicationRouter;
import com.spotify.apollo.route.RuleMatch;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import okio.ByteString;
import org.hamcrest.core.Is;
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
public class RequestRunnableImplTest {
    @Mock
    OngoingRequest ongoingRequest;

    @Mock
    ApplicationRouter<Endpoint> applicationRouter;

    @Mock
    RuleMatch<Endpoint> match;

    @Mock
    BiConsumer<OngoingRequest, RuleMatch<Endpoint>> matchContinuation;

    @Mock
    Endpoint endpoint;

    @Mock
    EndpointInfo info;

    @Mock
    Request message;

    @Captor
    ArgumentCaptor<Response<ByteString>> responseArgumentCaptor;

    RequestRunnableImpl requestRunnable;

    @Test
    public void testRunsMatchedEndpoint() {
        requestRunnable.run(matchContinuation);
        Mockito.verify(matchContinuation, Mockito.times(1)).accept(ArgumentMatchers.eq(ongoingRequest), ArgumentMatchers.eq(match));
    }

    @Test
    public void testMatchingFails() throws Exception {
        Mockito.when(applicationRouter.match(ArgumentMatchers.any(Request.class))).thenReturn(Optional.empty());
        Mockito.when(applicationRouter.getMethodsForValidRules(ArgumentMatchers.any(Request.class))).thenReturn(Collections.emptyList());
        requestRunnable.run(matchContinuation);
        Mockito.verify(ongoingRequest).reply(Response.forStatus(Status.NOT_FOUND));
    }

    @Test
    public void testWrongMethod() throws Exception {
        Mockito.when(applicationRouter.match(ArgumentMatchers.any(Request.class))).thenReturn(Optional.empty());
        Mockito.when(applicationRouter.getMethodsForValidRules(ArgumentMatchers.any(Request.class))).thenReturn(Collections.singleton("POST"));
        Mockito.when(message.method()).thenReturn("GET");
        requestRunnable.run(matchContinuation);
        Mockito.verify(ongoingRequest).reply(responseArgumentCaptor.capture());
        Response<ByteString> reply = responseArgumentCaptor.getValue();
        Assert.assertEquals(reply.status(), METHOD_NOT_ALLOWED);
        Assert.assertEquals(reply.headerEntries(), Collections.singletonList(new AbstractMap.SimpleEntry("Allow", "OPTIONS, POST")));
    }

    @Test
    public void testWithMethodOptions() throws Exception {
        Mockito.when(applicationRouter.match(ArgumentMatchers.any(Request.class))).thenReturn(Optional.empty());
        Mockito.when(applicationRouter.getMethodsForValidRules(ArgumentMatchers.any(Request.class))).thenReturn(Collections.singleton("POST"));
        Mockito.when(message.method()).thenReturn("OPTIONS");
        requestRunnable.run(matchContinuation);
        Mockito.verify(ongoingRequest).reply(responseArgumentCaptor.capture());
        Response<ByteString> response = responseArgumentCaptor.getValue();
        Assert.assertThat(response.status(), Is.is(NO_CONTENT));
        Assert.assertThat(response.headerEntries(), Is.is(Collections.singletonList(new AbstractMap.SimpleEntry("Allow", "OPTIONS, POST"))));
    }

    @Test
    public void shouldReply500IfApplicationRouterMatchThrows() throws Exception {
        Mockito.when(applicationRouter.match(ArgumentMatchers.any(Request.class))).thenThrow(new RuntimeException("expected"));
        requestRunnable.run(matchContinuation);
        Mockito.verify(ongoingRequest).reply(Response.forStatus(Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void shouldReply500IfApplicationRouterValidMethodsThrows() throws Exception {
        Mockito.when(applicationRouter.match(ArgumentMatchers.any(Request.class))).thenReturn(Optional.empty());
        Mockito.when(applicationRouter.getMethodsForValidRules(ArgumentMatchers.any(Request.class))).thenThrow(new RuntimeException("expected"));
        requestRunnable.run(matchContinuation);
        Mockito.verify(ongoingRequest).reply(Response.forStatus(Status.INTERNAL_SERVER_ERROR));
    }
}

