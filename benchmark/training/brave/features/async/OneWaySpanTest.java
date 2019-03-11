package brave.features.async;


import Request.Builder;
import Span.Kind.CLIENT;
import brave.Span;
import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import zipkin2.Span.Kind.SERVER;
import zipkin2.storage.InMemoryStorage;


/**
 * This is an example of a one-way span, which is possible by use of the {@link Span#flush()}
 * operator.
 */
public class OneWaySpanTest {
    @Rule
    public MockWebServer server = new MockWebServer();

    InMemoryStorage storage = InMemoryStorage.newBuilder().build();

    /**
     * Use different tracers for client and server as usually they are on different hosts.
     */
    Tracing clientTracing = Tracing.newBuilder().localServiceName("client").currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build()).spanReporter(( s) -> storage.spanConsumer().accept(Collections.singletonList(s))).build();

    Tracing serverTracing = Tracing.newBuilder().localServiceName("server").currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(( s) -> storage.spanConsumer().accept(Collections.singletonList(s))).build();

    CountDownLatch flushedIncomingRequest = new CountDownLatch(1);

    @Test
    public void startWithOneTracerAndStopWithAnother() throws Exception {
        // start a new span representing a request
        Span span = clientTracing.tracer().newTrace();
        // inject the trace context into the request
        Request.Builder request = new Request.Builder().url(server.url("/"));
        clientTracing.propagation().injector(Request.Builder::addHeader).inject(span.context(), request);
        // fire off the request asynchronously, totally dropping any response
        new OkHttpClient().newCall(request.build()).enqueue(Mockito.mock(Callback.class));
        // start the client side and flush instead of processing a response
        span.kind(CLIENT).start().flush();
        // block on the server handling the request, so we can run assertions
        flushedIncomingRequest.await();
        // // zipkin doesn't backfill timestamp and duration when storing raw spans
        List<zipkin2.Span> spans = storage.spanStore().getTrace(span.context().traceIdString()).execute();
        // check that the client send arrived first
        zipkin2.Span clientSpan = spans.get(0);
        assertThat(clientSpan.name()).isNull();
        assertThat(clientSpan.localServiceName()).isEqualTo("client");
        assertThat(clientSpan.kind()).isEqualTo(zipkin2.Span.Kind.CLIENT);
        // check that the server receive arrived last
        zipkin2.Span serverSpan = spans.get(1);
        assertThat(serverSpan.name()).isEqualTo("get");
        assertThat(serverSpan.localServiceName()).isEqualTo("server");
        assertThat(serverSpan.kind()).isEqualTo(SERVER);
        // check that the server span is shared
        assertThat(serverSpan.shared()).isTrue();
        // check that no spans reported duration
        assertThat(clientSpan.duration()).isNull();
        assertThat(serverSpan.duration()).isNull();
    }
}

