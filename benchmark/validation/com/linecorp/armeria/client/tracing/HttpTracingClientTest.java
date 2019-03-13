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
package com.linecorp.armeria.client.tracing;


import Kind.CLIENT;
import RequestContextCurrentTraceContext.DEFAULT;
import brave.Tracing;
import brave.sampler.Sampler;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.common.tracing.SpanCollectingReporter;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import zipkin2.Span;


public class HttpTracingClientTest {
    private static final String TEST_SERVICE = "test-service";

    private static final String TEST_SPAN = "hello";

    @Test
    public void newDecorator_shouldFailFastWhenRequestContextCurrentTraceContextNotConfigured() {
        assertThatThrownBy(() -> HttpTracingClient.newDecorator(Tracing.newBuilder().build())).isInstanceOf(IllegalStateException.class).hasMessage(("Tracing.currentTraceContext is not a RequestContextCurrentTraceContext scope. " + "Please call Tracing.Builder.currentTraceContext(RequestContextCurrentTraceContext.INSTANCE)."));
    }

    @Test
    public void newDecorator_shouldWorkWhenRequestContextCurrentTraceContextConfigured() {
        HttpTracingClient.newDecorator(Tracing.newBuilder().currentTraceContext(DEFAULT).build());
    }

    @Test(timeout = 20000)
    public void shouldSubmitSpanWhenSampled() throws Exception {
        final SpanCollectingReporter reporter = new SpanCollectingReporter();
        final Tracing tracing = Tracing.newBuilder().localServiceName(HttpTracingClientTest.TEST_SERVICE).spanReporter(reporter).sampler(Sampler.create(1.0F)).build();
        HttpTracingClientTest.testRemoteInvocation(tracing, null);
        // check span name
        final Span span = reporter.spans().take();
        assertThat(span.name()).isEqualTo(HttpTracingClientTest.TEST_SPAN);
        // check kind
        assertThat(span.kind()).isSameAs(CLIENT);
        // only one span should be submitted
        assertThat(reporter.spans().poll(1, TimeUnit.SECONDS)).isNull();
        // check # of annotations (we add wire annotations)
        assertThat(span.annotations()).hasSize(2);
        // check tags
        assertThat(span.tags()).containsAllEntriesOf(ImmutableMap.of("http.host", "foo.com", "http.method", "POST", "http.path", "/hello/armeria", "http.status_code", "200", "http.url", "none+h2c://foo.com/hello/armeria"));
        // check service name
        assertThat(span.localServiceName()).isEqualTo(HttpTracingClientTest.TEST_SERVICE);
        // check remote service name
        assertThat(span.remoteServiceName()).isEqualTo("foo.com");
    }

    @Test(timeout = 20000)
    public void shouldSubmitSpanWithCustomRemoteName() throws Exception {
        final SpanCollectingReporter reporter = new SpanCollectingReporter();
        final Tracing tracing = Tracing.newBuilder().localServiceName(HttpTracingClientTest.TEST_SERVICE).spanReporter(reporter).sampler(Sampler.create(1.0F)).build();
        HttpTracingClientTest.testRemoteInvocation(tracing, "fooService");
        // check span name
        final Span span = reporter.spans().take();
        // check tags
        assertThat(span.tags()).containsAllEntriesOf(ImmutableMap.of("http.host", "foo.com", "http.method", "POST", "http.path", "/hello/armeria", "http.status_code", "200", "http.url", "none+h2c://foo.com/hello/armeria"));
        // check service name
        assertThat(span.localServiceName()).isEqualTo(HttpTracingClientTest.TEST_SERVICE);
        // check remote service name, lower-cased
        assertThat(span.remoteServiceName()).isEqualTo("fooservice");
    }

    @Test
    public void shouldNotSubmitSpanWhenNotSampled() throws Exception {
        final SpanCollectingReporter reporter = new SpanCollectingReporter();
        final Tracing tracing = Tracing.newBuilder().localServiceName(HttpTracingClientTest.TEST_SERVICE).spanReporter(reporter).sampler(Sampler.create(0.0F)).build();
        HttpTracingClientTest.testRemoteInvocation(tracing, null);
        assertThat(reporter.spans().poll(1, TimeUnit.SECONDS)).isNull();
    }
}

