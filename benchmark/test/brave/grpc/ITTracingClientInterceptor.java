package brave.grpc;


import ForwardingClientCall.SimpleForwardingClientCall;
import GreeterGrpc.GreeterFutureStub;
import Sampler.ALWAYS_SAMPLE;
import Sampler.NEVER_SAMPLE;
import Span.Kind.CLIENT;
import brave.ScopedSpan;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GraterGrpc;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import zipkin2.Span;


public class ITTracingClientInterceptor {
    Logger testLogger = LogManager.getLogger();

    /**
     * See brave.http.ITHttp for rationale on using a concurrent blocking queue eventhough some calls,
     * like those using blocking clients, happen on the main thread.
     */
    BlockingQueue<Span> spans = new LinkedBlockingQueue<>();

    GrpcTracing tracing = GrpcTracing.create(tracingBuilder(ALWAYS_SAMPLE).build());

    Tracer tracer = tracing.tracing.tracer();

    TestServer server = new TestServer();

    ManagedChannel client;

    // See brave.http.ITHttp for rationale on polling after tests complete
    @Rule
    public TestRule assertSpansEmpty = new TestWatcher() {
        // only check success path to avoid masking assertion errors or exceptions
        @Override
        protected void succeeded(Description description) {
            try {
                assertThat(spans.poll(100, TimeUnit.MILLISECONDS)).withFailMessage("Span remaining in queue. Check for redundant reporting").isNull();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Test
    public void propagatesSpan() throws Exception {
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        TraceContext context = server.takeRequest().context();
        assertThat(context.parentId()).isNull();
        assertThat(context.sampled()).isTrue();
        takeSpan();
    }

    @Test
    public void makesChildOfCurrentSpan() throws Exception {
        ScopedSpan parent = tracer.startScopedSpan("test");
        try {
            GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        } finally {
            parent.finish();
        }
        TraceContext context = server.takeRequest().context();
        assertThat(context.traceId()).isEqualTo(parent.context().traceId());
        assertThat(context.parentId()).isEqualTo(parent.context().spanId());
        // we report one in-process and one RPC client span
        assertThat(Arrays.asList(takeSpan(), takeSpan())).extracting(Span::kind).containsOnly(null, CLIENT);
    }

    /**
     * This tests that the parent is determined at the time the request was made, not when the request
     * was executed.
     */
    @Test
    public void usesParentFromInvocationTime() throws Exception {
        server.enqueueDelay(TimeUnit.SECONDS.toMillis(1));
        GreeterGrpc.GreeterFutureStub futureStub = GreeterGrpc.newFutureStub(client);
        ScopedSpan parent = tracer.startScopedSpan("test");
        try {
            futureStub.sayHello(GreeterImpl.HELLO_REQUEST);
            futureStub.sayHello(GreeterImpl.HELLO_REQUEST);
        } finally {
            parent.finish();
        }
        ScopedSpan otherSpan = tracer.startScopedSpan("test2");
        try {
            for (int i = 0; i < 2; i++) {
                TraceContext context = server.takeRequest().context();
                assertThat(context.traceId()).isEqualTo(parent.context().traceId());
                assertThat(context.parentId()).isEqualTo(parent.context().spanId());
            }
        } finally {
            otherSpan.finish();
        }
        // Check we reported 2 local spans and 2 client spans
        assertThat(Arrays.asList(takeSpan(), takeSpan(), takeSpan(), takeSpan())).extracting(Span::kind).containsOnly(null, CLIENT);
    }

    /**
     * Unlike Brave 3, Brave 4 propagates trace ids even when unsampled
     */
    @Test
    public void propagates_sampledFalse() throws Exception {
        tracing = GrpcTracing.create(tracingBuilder(NEVER_SAMPLE).build());
        closeClient(client);
        client = newClient();
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        TraceContextOrSamplingFlags extracted = server.takeRequest();
        assertThat(extracted.sampled()).isFalse();
        // @After will check that nothing is reported
    }

    @Test
    public void reportsClientKindToZipkin() throws Exception {
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        Span span = takeSpan();
        assertThat(span.kind()).isEqualTo(CLIENT);
    }

    @Test
    public void defaultSpanNameIsMethodName() throws Exception {
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        Span span = takeSpan();
        assertThat(span.name()).isEqualTo("helloworld.greeter/sayhello");
    }

    @Test
    public void onTransportException_reportsSpan() throws Exception {
        spanFromTransportException();
    }

    @Test
    public void onTransportException_addsErrorTag() throws Exception {
        Span span = spanFromTransportException();
        assertThat(span.tags()).containsExactly(entry("error", "UNAVAILABLE"), entry("grpc.status_code", "UNAVAILABLE"));
    }

    @Test
    public void addsErrorTag_onUnimplemented() throws Exception {
        try {
            GraterGrpc.newBlockingStub(client).seyHallo(GreeterImpl.HELLO_REQUEST);
            failBecauseExceptionWasNotThrown(StatusRuntimeException.class);
        } catch (StatusRuntimeException e) {
        }
        Span span = takeSpan();
        assertThat(span.tags()).containsExactly(entry("error", "UNIMPLEMENTED"), entry("grpc.status_code", "UNIMPLEMENTED"));
    }

    @Test
    public void addsErrorTag_onCanceledFuture() throws Exception {
        server.enqueueDelay(TimeUnit.SECONDS.toMillis(1));
        ListenableFuture<HelloReply> resp = GreeterGrpc.newFutureStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        Assume.assumeTrue("lost race on cancel", resp.cancel(true));
        Span span = takeSpan();
        assertThat(span.tags()).containsExactly(entry("error", "CANCELLED"), entry("grpc.status_code", "CANCELLED"));
    }

    /**
     * NOTE: for this to work, the tracing interceptor must be last (so that it executes first)
     *
     * <p>Also notice that we are only making the current context available in the request side.
     */
    @Test
    public void currentSpanVisibleToUserInterceptors() throws Exception {
        closeClient(client);
        client = newClient(new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                testLogger.info("in span!");
                tracer.currentSpanCustomizer().annotate("before");
                return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        tracer.currentSpanCustomizer().annotate("start");
                        super.start(responseListener, headers);
                    }
                };
            }
        }, tracing.newClientInterceptor());
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        assertThat(takeSpan().annotations()).extracting(Annotation::value).containsOnly("before", "start");
    }

    @Test
    public void clientParserTest() throws Exception {
        closeClient(client);
        tracing = tracing.toBuilder().clientParser(new GrpcClientParser() {
            @Override
            protected <M> void onMessageSent(M message, SpanCustomizer span) {
                span.tag("grpc.message_sent", message.toString());
                if ((tracing.tracing.currentTraceContext().get()) != null) {
                    span.tag("grpc.message_sent.visible", "true");
                }
            }

            @Override
            protected <M> void onMessageReceived(M message, SpanCustomizer span) {
                span.tag("grpc.message_received", message.toString());
                if ((tracing.tracing.currentTraceContext().get()) != null) {
                    span.tag("grpc.message_received.visible", "true");
                }
            }

            @Override
            protected <ReqT, RespT> String spanName(MethodDescriptor<ReqT, RespT> methodDescriptor) {
                return methodDescriptor.getType().name();
            }
        }).build();
        client = newClient();
        GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        Span span = takeSpan();
        assertThat(span.name()).isEqualTo("unary");
        assertThat(span.tags()).containsKeys("grpc.message_received", "grpc.message_sent", "grpc.message_received.visible", "grpc.message_sent.visible");
    }

    @Test
    public void clientParserTestStreamingResponse() throws Exception {
        closeClient(client);
        tracing = tracing.toBuilder().clientParser(new GrpcClientParser() {
            int receiveCount = 0;

            @Override
            protected <M> void onMessageReceived(M message, SpanCustomizer span) {
                span.tag(("grpc.message_received." + ((receiveCount)++)), message.toString());
            }
        }).build();
        client = newClient();
        Iterator<HelloReply> replies = GreeterGrpc.newBlockingStub(client).sayHelloWithManyReplies(HelloRequest.newBuilder().setName("this is dog").build());
        assertThat(replies).toIterable().hasSize(10);
        Span span = takeSpan();
        // all response messages are tagged to the same span
        assertThat(span.tags()).hasSize(10);
    }
}

