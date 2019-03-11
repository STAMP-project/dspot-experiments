/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.tracing.tests.it1;


import ClientTracingFilter.CURRENT_SPAN_CONTEXT_PROPERTY_NAME;
import ClientTracingFilter.TRACER_PROPERTY_NAME;
import brave.propagation.TraceContext;
import io.helidon.webserver.WebServer;
import io.opentracing.Tracer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import zipkin2.Span;


/**
 * The ZipkinClientTest.
 */
public class OpentraceableClientE2ETest {
    private static WebServer server;

    private static final int EXPECTED_TRACE_EVENTS_COUNT = 4;

    private static final CountDownLatch EVENTS_LATCH = new CountDownLatch(OpentraceableClientE2ETest.EXPECTED_TRACE_EVENTS_COUNT);

    private static final Map<String, Span> EVENTS_MAP = new ConcurrentHashMap<>();

    private static Client client;

    @Test
    public void e2e() throws Exception {
        Tracer tracer = OpentraceableClientE2ETest.tracer("test-client");
        io.opentracing.Span start = tracer.buildSpan("client-call").start();
        Response response = OpentraceableClientE2ETest.client.target(("http://localhost:" + (OpentraceableClientE2ETest.server.port()))).property(TRACER_PROPERTY_NAME, tracer).property(CURRENT_SPAN_CONTEXT_PROPERTY_NAME, start.context()).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        start.finish();
        if (!(OpentraceableClientE2ETest.EVENTS_LATCH.await(10, TimeUnit.SECONDS))) {
            Assertions.fail(((("Expected " + (OpentraceableClientE2ETest.EXPECTED_TRACE_EVENTS_COUNT)) + " trace events but received only: ") + (OpentraceableClientE2ETest.EVENTS_MAP.size())));
        }
        TraceContext traceContext = unwrap();
        assertSpanChain(OpentraceableClientE2ETest.EVENTS_MAP.remove(traceContext.traceIdString()), OpentraceableClientE2ETest.EVENTS_MAP);
        MatcherAssert.assertThat(OpentraceableClientE2ETest.EVENTS_MAP.entrySet(), Matchers.hasSize(0));
    }
}

