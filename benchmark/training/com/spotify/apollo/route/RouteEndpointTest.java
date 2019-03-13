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


import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.Route.DocString;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RouteEndpointTest {
    RouteEndpoint endpoint;

    Request request;

    RequestContext requestContext;

    Map<String, String> pathArgs;

    SyncHandler<Response<ByteString>> syncEndpointHandler;

    AsyncHandler<Response<ByteString>> asyncHandler;

    Response<ByteString> response;

    ByteString theData;

    @Test
    public void shouldReturnResultOfSyncRoute() throws Exception {
        Mockito.when(syncEndpointHandler.invoke(requestContext)).thenReturn(response);
        endpoint = new RouteEndpoint(Route.sync("GET", "http://foo", syncEndpointHandler));
        Response<?> actualResponse = endpoint.invoke(requestContext).toCompletableFuture().get();
        Assert.assertThat(actualResponse.payload(), CoreMatchers.equalTo(Optional.of(theData)));
    }

    @Test
    public void shouldReturnResultOfAsyncRoute() throws Exception {
        Mockito.when(asyncHandler.invoke(requestContext)).thenReturn(CompletableFuture.completedFuture(response));
        endpoint = new RouteEndpoint(Route.create("GET", "http://foo", asyncHandler));
        Response<?> actualResponse = endpoint.invoke(requestContext).toCompletableFuture().get();
        Assert.assertThat(actualResponse.payload(), CoreMatchers.equalTo(Optional.of(theData)));
    }

    @Test
    public void shouldIncludeDocstringInEndpointInfo() throws Exception {
        endpoint = new RouteEndpoint(Route.sync("GET", "http://blah", ( ctx) -> Response.<ByteString>ok()).withDocString("summarium", "this is a kewl description"));
        Assert.assertThat(endpoint.info().getDocString(), CoreMatchers.equalTo(Optional.of(DocString.doc("summarium", "this is a kewl description"))));
    }
}

