/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.tracing;


import Kind.SERVER;
import RequestContextCurrentTraceContext.DEFAULT;
import brave.Tracing;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.common.tracing.SpanCollectingReporter;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import zipkin2.Span;


public class HttpTracingServiceTest {
    private static final String TEST_SERVICE = "test-service";

    private static final String TEST_METHOD = "hello";

    @Test
    public void newDecorator_shouldFailFastWhenRequestContextCurrentTraceContextNotConfigured() {
        assertThatThrownBy(() -> HttpTracingService.newDecorator(Tracing.newBuilder().build())).isInstanceOf(IllegalStateException.class).hasMessage(("Tracing.currentTraceContext is not a RequestContextCurrentTraceContext scope. " + "Please call Tracing.Builder.currentTraceContext(RequestContextCurrentTraceContext.INSTANCE)."));
    }

    @Test
    public void newDecorator_shouldWorkWhenRequestContextCurrentTraceContextConfigured() {
        HttpTracingService.newDecorator(Tracing.newBuilder().currentTraceContext(DEFAULT).build());
    }

    @Test(timeout = 20000)
    public void shouldSubmitSpanWhenRequestIsSampled() throws Exception {
        final SpanCollectingReporter reporter = HttpTracingServiceTest.testServiceInvocation(1.0F);
        // check span name
        final Span span = reporter.spans().take();
        assertThat(span.name()).isEqualTo(HttpTracingServiceTest.TEST_METHOD);
        // check kind
        assertThat(span.kind()).isSameAs(SERVER);
        // only one span should be submitted
        assertThat(reporter.spans().poll(1, TimeUnit.SECONDS)).isNull();
        // check # of annotations (we add wire annotations)
        assertThat(span.annotations()).hasSize(2);
        // check tags
        assertThat(span.tags()).containsAllEntriesOf(ImmutableMap.of("http.host", "foo.com", "http.method", "POST", "http.path", "/hello/trustin", "http.status_code", "200", "http.url", "none+h2c://foo.com/hello/trustin"));
        // check service name
        assertThat(span.localServiceName()).isEqualTo(HttpTracingServiceTest.TEST_SERVICE);
    }

    @Test
    public void shouldNotSubmitSpanWhenRequestIsNotSampled() throws Exception {
        final SpanCollectingReporter reporter = HttpTracingServiceTest.testServiceInvocation(0.0F);
        // don't submit any spans
        assertThat(reporter.spans().poll(1, TimeUnit.SECONDS)).isNull();
    }
}

