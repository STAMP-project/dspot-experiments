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
package com.linecorp.armeria.it.tracing;


import HelloService.Iface;
import brave.ScopedSpan;
import brave.Span;
import brave.Tracer.SpanInScope;
import brave.Tracing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.tracing.HelloService.AsyncIface;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Rule;
import org.junit.Test;
import zipkin2.reporter.Reporter;


public class HttpTracingIntegrationTest {
    private static final HttpTracingIntegrationTest.ReporterImpl spanReporter = new HttpTracingIntegrationTest.ReporterImpl();

    private Iface fooClient;

    private Iface fooClientWithoutTracing;

    private AsyncIface barClient;

    private AsyncIface quxClient;

    private Iface zipClient;

    private HttpClient poolHttpClient;

    @Rule
    public final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/foo", HttpTracingIntegrationTest.decorate("service/foo", THttpService.of(((AsyncIface) (( name, resultHandler) -> barClient.hello(("Miss. " + name), new com.linecorp.armeria.it.tracing.DelegatingCallback(resultHandler)))))));
            sb.service("/bar", HttpTracingIntegrationTest.decorate("service/bar", THttpService.of(((AsyncIface) (( name, resultHandler) -> {
                if (name.startsWith("Miss. ")) {
                    name = "Ms. " + (name.substring(6));
                }
                quxClient.hello(name, new com.linecorp.armeria.it.tracing.DelegatingCallback(resultHandler));
            })))));
            sb.service("/zip", HttpTracingIntegrationTest.decorate("service/zip", THttpService.of(((AsyncIface) (( name, resultHandler) -> {
                final ThriftCompletableFuture<String> f1 = new ThriftCompletableFuture<>();
                final ThriftCompletableFuture<String> f2 = new ThriftCompletableFuture<>();
                quxClient.hello(name, f1);
                quxClient.hello(name, f2);
                CompletableFuture.allOf(f1, f2).whenCompleteAsync(( aVoid, throwable) -> {
                    resultHandler.onComplete((((f1.join()) + ", and ") + (f2.join())));
                }, RequestContext.current().contextAwareExecutor());
            })))));
            sb.service("/qux", HttpTracingIntegrationTest.decorate("service/qux", THttpService.of(((AsyncIface) (( name, resultHandler) -> resultHandler.onComplete((("Hello, " + name) + '!')))))));
            sb.service("/pool", HttpTracingIntegrationTest.decorate("service/pool", new AbstractHttpService() {
                @Override
                protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
                    final CountDownLatch countDownLatch = new CountDownLatch(2);
                    final ListenableFuture<List<Object>> spanAware = Futures.allAsList(IntStream.range(1, 3).mapToObj(( i) -> executorService.submit(RequestContext.current().makeContextAware(() -> {
                        if (i == 2) {
                            countDownLatch.countDown();
                            countDownLatch.await();
                        }
                        Span span = Tracing.currentTracer().nextSpan().start();
                        try (SpanInScope spanInScope = Tracing.currentTracer().withSpanInScope(span)) {
                            if (i == 1) {
                                countDownLatch.countDown();
                                countDownLatch.await();
                                // to wait second task get span.
                                Thread.sleep(1000L);
                            }
                        } finally {
                            span.finish();
                        }
                        return null;
                    }))).collect(ImmutableList.toImmutableList()));
                    final CompletableFuture<HttpResponse> responseFuture = new CompletableFuture<>();
                    final HttpResponse res = HttpResponse.from(responseFuture);
                    Futures.transformAsync(spanAware, ( result) -> allAsList(IntStream.range(1, 3).mapToObj(( i) -> executorService.submit(RequestContext.current().makeContextAware(() -> {
                        ScopedSpan span = Tracing.currentTracer().startScopedSpan("aloha");
                        try {
                            return null;
                        } finally {
                            span.finish();
                        }
                    }))).collect(toImmutableList())), RequestContext.current().contextAwareExecutor()).addListener(() -> {
                        responseFuture.complete(HttpResponse.of(OK, MediaType.PLAIN_TEXT_UTF_8, "Lee"));
                    }, RequestContext.current().contextAwareExecutor());
                    return res;
                }
            }));
        }
    };

    @Test(timeout = 10000)
    public void testServiceHasMultipleClientRequests() throws Exception {
        assertThat(zipClient.hello("Lee")).isEqualTo("Hello, Lee!, and Hello, Lee!");
        final zipkin2.Span[] spans = HttpTracingIntegrationTest.spanReporter.take(6);
        final String traceId = spans[0].traceId();
        assertThat(spans).allMatch(( s) -> s.traceId().equals(traceId));
    }

    @Test(timeout = 10000)
    public void testClientInitiatedTrace() throws Exception {
        assertThat(fooClient.hello("Lee")).isEqualTo("Hello, Ms. Lee!");
        final zipkin2.Span[] spans = HttpTracingIntegrationTest.spanReporter.take(6);
        final String traceId = spans[0].traceId();
        assertThat(spans).allMatch(( s) -> s.traceId().equals(traceId));
        // Find all spans.
        final zipkin2.Span clientFooSpan = HttpTracingIntegrationTest.findSpan(spans, "client/foo");
        final zipkin2.Span serviceFooSpan = HttpTracingIntegrationTest.findSpan(spans, "service/foo");
        final zipkin2.Span clientBarSpan = HttpTracingIntegrationTest.findSpan(spans, "client/bar");
        final zipkin2.Span serviceBarSpan = HttpTracingIntegrationTest.findSpan(spans, "service/bar");
        final zipkin2.Span clientQuxSpan = HttpTracingIntegrationTest.findSpan(spans, "client/qux");
        final zipkin2.Span serviceQuxSpan = HttpTracingIntegrationTest.findSpan(spans, "service/qux");
        // client/foo and service/foo should have no parents.
        assertThat(clientFooSpan.parentId()).isNull();
        assertThat(serviceFooSpan.parentId()).isNull();
        // client/foo and service/foo should have the ID values identical with their traceIds.
        assertThat(clientFooSpan.id()).isEqualTo(traceId);
        assertThat(serviceFooSpan.id()).isEqualTo(traceId);
        // The spans that do not cross the network boundary should have the same ID.
        assertThat(clientFooSpan.id()).isEqualTo(serviceFooSpan.id());
        assertThat(clientBarSpan.id()).isEqualTo(serviceBarSpan.id());
        assertThat(clientQuxSpan.id()).isEqualTo(serviceQuxSpan.id());
        // Check the parentIds.
        assertThat(clientBarSpan.parentId()).isEqualTo(clientFooSpan.id());
        assertThat(serviceBarSpan.parentId()).isEqualTo(clientFooSpan.id());
        assertThat(clientQuxSpan.parentId()).isEqualTo(clientBarSpan.id());
        assertThat(serviceQuxSpan.parentId()).isEqualTo(clientBarSpan.id());
        // Check the service names.
        assertThat(clientFooSpan.localServiceName()).isEqualTo("client/foo");
        assertThat(serviceFooSpan.localServiceName()).isEqualTo("service/foo");
        assertThat(clientBarSpan.localServiceName()).isEqualTo("client/bar");
        assertThat(serviceBarSpan.localServiceName()).isEqualTo("service/bar");
        assertThat(clientQuxSpan.localServiceName()).isEqualTo("client/qux");
        assertThat(serviceQuxSpan.localServiceName()).isEqualTo("service/qux");
        // Check the span names.
        assertThat(spans).allMatch(( s) -> "hello".equals(s.name()));
        // Check wire times
        final long clientStartTime = clientFooSpan.timestampAsLong();
        final long clientWireSendTime = clientFooSpan.annotations().stream().filter(( a) -> a.value().equals("ws")).findFirst().get().timestamp();
        final long clientWireReceiveTime = clientFooSpan.annotations().stream().filter(( a) -> a.value().equals("wr")).findFirst().get().timestamp();
        final long clientEndTime = clientStartTime + (clientFooSpan.durationAsLong());
        final long serverStartTime = serviceFooSpan.timestampAsLong();
        final long serverWireSendTime = serviceFooSpan.annotations().stream().filter(( a) -> a.value().equals("ws")).findFirst().get().timestamp();
        final long serverWireReceiveTime = serviceFooSpan.annotations().stream().filter(( a) -> a.value().equals("wr")).findFirst().get().timestamp();
        final long serverEndTime = serverStartTime + (serviceFooSpan.durationAsLong());
        // These values are taken at microsecond precision and should be reliable to compare to each other.
        // Because of the small deltas among these numbers in a unit test, a thread context switch can cause
        // client - server values to not compare correctly. We go ahead and only verify values recorded from the
        // same thread.
        assertThat(clientStartTime).isNotZero();
        assertThat(clientWireSendTime).isGreaterThanOrEqualTo(clientStartTime);
        assertThat(clientWireReceiveTime).isGreaterThanOrEqualTo(clientWireSendTime);
        assertThat(clientEndTime).isGreaterThanOrEqualTo(clientWireReceiveTime);
        // Server start time and wire receive time are essentially the same in our current model, and whether
        // one is greater than the other is mostly an implementation detail, so we don't compare them to each
        // other.
        assertThat(serverWireSendTime).isGreaterThanOrEqualTo(serverStartTime);
        assertThat(serverWireSendTime).isGreaterThanOrEqualTo(serverWireReceiveTime);
        assertThat(serverEndTime).isGreaterThanOrEqualTo(serverWireSendTime);
    }

    @Test(timeout = 10000)
    public void testServiceInitiatedTrace() throws Exception {
        assertThat(fooClientWithoutTracing.hello("Lee")).isEqualTo("Hello, Ms. Lee!");
        final zipkin2.Span[] spans = HttpTracingIntegrationTest.spanReporter.take(5);
        final String traceId = spans[0].traceId();
        assertThat(spans).allMatch(( s) -> s.traceId().equals(traceId));
        // Find all spans.
        final zipkin2.Span serviceFooSpan = HttpTracingIntegrationTest.findSpan(spans, "service/foo");
        final zipkin2.Span clientBarSpan = HttpTracingIntegrationTest.findSpan(spans, "client/bar");
        final zipkin2.Span serviceBarSpan = HttpTracingIntegrationTest.findSpan(spans, "service/bar");
        final zipkin2.Span clientQuxSpan = HttpTracingIntegrationTest.findSpan(spans, "client/qux");
        final zipkin2.Span serviceQuxSpan = HttpTracingIntegrationTest.findSpan(spans, "service/qux");
        // service/foo should have no parent.
        assertThat(serviceFooSpan.parentId()).isNull();
        // service/foo should have the ID value identical with its traceId.
        assertThat(serviceFooSpan.id()).isEqualTo(traceId);
        // The spans that do not cross the network boundary should have the same ID.
        assertThat(clientBarSpan.id()).isEqualTo(serviceBarSpan.id());
        assertThat(clientQuxSpan.id()).isEqualTo(serviceQuxSpan.id());
        // Check the parentIds
        assertThat(clientBarSpan.parentId()).isEqualTo(serviceFooSpan.id());
        assertThat(serviceBarSpan.parentId()).isEqualTo(serviceFooSpan.id());
        assertThat(clientQuxSpan.parentId()).isEqualTo(serviceBarSpan.id());
        assertThat(serviceQuxSpan.parentId()).isEqualTo(serviceBarSpan.id());
        // Check the service names.
        assertThat(serviceFooSpan.localServiceName()).isEqualTo("service/foo");
        assertThat(clientBarSpan.localServiceName()).isEqualTo("client/bar");
        assertThat(serviceBarSpan.localServiceName()).isEqualTo("service/bar");
        assertThat(clientQuxSpan.localServiceName()).isEqualTo("client/qux");
        assertThat(serviceQuxSpan.localServiceName()).isEqualTo("service/qux");
        // Check the span names.
        assertThat(spans).allMatch(( s) -> "hello".equals(s.name()));
    }

    @Test(timeout = 10000)
    public void testSpanInThreadPoolHasSameTraceId() throws Exception {
        poolHttpClient.get("pool").aggregate().get();
        final zipkin2.Span[] spans = HttpTracingIntegrationTest.spanReporter.take(5);
        assertThat(Arrays.stream(spans).map(Span::traceId).collect(ImmutableSet.toImmutableSet())).hasSize(1);
        assertThat(Arrays.stream(spans).map(Span::parentId).filter(Objects::nonNull).collect(ImmutableSet.toImmutableSet())).hasSize(1);
    }

    private static class DelegatingCallback implements AsyncMethodCallback<String> {
        private final AsyncMethodCallback<Object> resultHandler;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        DelegatingCallback(AsyncMethodCallback resultHandler) {
            this.resultHandler = resultHandler;
        }

        @Override
        public void onComplete(String response) {
            resultHandler.onComplete(response);
        }

        @Override
        public void onError(Exception exception) {
            resultHandler.onError(exception);
        }
    }

    private static class ReporterImpl implements Reporter<zipkin2.Span> {
        private final BlockingQueue<zipkin2.Span> spans = new LinkedBlockingQueue<>();

        @Override
        public void report(zipkin2.Span span) {
            spans.add(span);
        }

        zipkin2.Span[] take(int numSpans) throws InterruptedException {
            final List<zipkin2.Span> taken = new ArrayList<>();
            while ((taken.size()) < numSpans) {
                taken.add(spans.take());
            } 
            // Reverse the collected spans to sort the spans by request time.
            Collections.reverse(taken);
            return taken.toArray(new zipkin2.Span[numSpans]);
        }
    }
}

