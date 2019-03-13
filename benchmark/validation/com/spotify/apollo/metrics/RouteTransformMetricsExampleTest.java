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


import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.spotify.metrics.core.SemanticMetricRegistry;
import java.util.Collections;
import okio.ByteString;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class RouteTransformMetricsExampleTest {
    private SemanticMetricRegistry registry;

    private String serviceName;

    public RouteTransformMetricsExampleTest() {
        registry = new SemanticMetricRegistry();
        serviceName = "example";
    }

    @Test
    public void shouldTrackResponsePayloadSize() throws Exception {
        Route<AsyncHandler<Response<ByteString>>> testRoute = Route.sync("GET", "/foo/<name>", ( context) -> Response.forPayload(ByteString.encodeUtf8(context.pathArgs().get("name"))));
        Route<AsyncHandler<Response<ByteString>>> trackedRoute = withResponsePayloadSizeHistogram(testRoute);
        RequestContext context = Mockito.mock(RequestContext.class);
        Mockito.when(context.pathArgs()).thenReturn(Collections.singletonMap("name", "bar"));
        trackedRoute.handler().invoke(context).toCompletableFuture().get();
        MatcherAssert.assertThat(registry.getHistograms().keySet(), Matchers.containsInAnyOrder(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "example"), Matchers.hasEntry("what", "endpoint-response-size"), Matchers.hasEntry("endpoint", "GET:/foo/<name>")))));
    }
}

