package brave.grpc;


import B3Propagation.FACTORY;
import RpcMeasureConstants.RPC_METHOD;
import brave.ScopedSpan;
import brave.internal.Nullable;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.opencensus.common.Scope;
import io.opencensus.tags.TagKey;
import io.opencensus.tags.TagValue;
import io.opencensus.tags.Tags;
import io.opencensus.testing.export.TestHandler;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.export.SpanData;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Test;


public class ITCensusInterop {
    static class TagsGreeterImpl extends GreeterImpl {
        TagsGreeterImpl(@Nullable
        GrpcTracing grpcTracing) {
            super(grpcTracing);
        }

        /**
         * This verifies internal state by writing to both brave and census apis
         */
        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            Map<TagKey, TagValue> censusTags = getTags();
            // Read in-process tags from census and write to both span apis
            Span censusSpan = Tracing.getTracer().getCurrentSpan();
            for (Map.Entry<TagKey, TagValue> entry : censusTags.entrySet()) {
                spanCustomizer.tag(entry.getKey().getName(), entry.getValue().asString());
                censusSpan.putAttribute(entry.getKey().getName(), AttributeValue.stringAttributeValue(entry.getValue().asString()));
            }
            // Read in-process tags from brave's grpc hooks and write to both span apis
            TraceContext currentTraceContext = ((tracing) != null) ? tracing.currentTraceContext().get() : null;
            if (currentTraceContext == null) {
                super.sayHello(req, responseObserver);
                return;
            }
            ScopedSpan child = tracing.tracer().startScopedSpanWithParent("child", currentTraceContext);
            try {
                GrpcPropagation.Tags tags = child.context().findExtra(Tags.class);
                if ((tags.parentMethod) != null) {
                    child.tag("parentMethod", tags.parentMethod);
                    censusSpan.putAttribute("parentMethod", AttributeValue.stringAttributeValue(tags.parentMethod));
                }
                for (Map.Entry<String, String> entry : tags.toMap().entrySet()) {
                    child.tag(entry.getKey(), entry.getValue());
                    censusSpan.putAttribute(entry.getKey(), AttributeValue.stringAttributeValue(entry.getValue()));
                }
                super.sayHello(req, responseObserver);
            } finally {
                child.finish();
            }
        }
    }

    final TestHandler testHandler = new TestHandler();

    /**
     * See brave.http.ITHttp for rationale on using a concurrent blocking queue
     */
    BlockingQueue<zipkin2.Span> spans = new LinkedBlockingQueue<>();

    brave.Tracing tracing = brave.Tracing.newBuilder().propagationFactory(GrpcPropagation.newFactory(FACTORY)).spanReporter(spans::add).currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build()).build();

    GrpcTracing grpcTracing = GrpcTracing.create(tracing);

    Server server;

    ManagedChannel client;

    @Test
    public void readsCensusPropagation() throws Exception {
        initServer(true);// trace server with brave

        initClient(false);// trace client with census

        io.grpc.examples.helloworld.GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        // this takes 5 seconds due to hard-coding in ExportComponentImpl
        SpanData clientSpan = testHandler.waitForExport(1).get(0);
        zipkin2.Span serverSpan = takeSpan();
        assertThat(clientSpan.getContext().getTraceId().toLowerBase16()).isEqualTo(serverSpan.traceId());
        assertThat(clientSpan.getContext().getSpanId().toLowerBase16()).isEqualTo(serverSpan.parentId());
        assertThat(serverSpan.tags()).containsEntry("method", "helloworld.Greeter/SayHello");
    }

    @Test
    public void readsCensusPropagation_withIncomingMethod() throws Exception {
        initServer(true);// trace server with brave

        initClient(false);// trace client with census

        try (Scope tagger = Tags.getTagger().emptyBuilder().put(RPC_METHOD, TagValue.create("edge.Ingress/InitialRoute")).buildScoped()) {
            io.grpc.examples.helloworld.GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        }
        // this takes 5 seconds due to hard-coding in ExportComponentImpl
        SpanData clientSpan = testHandler.waitForExport(1).get(0);
        zipkin2.Span serverSpan = takeSpan();
        zipkin2.Span childSpan = takeSpan();
        assertThat(clientSpan.getContext().getTraceId().toLowerBase16()).isEqualTo(serverSpan.traceId());
        assertThat(clientSpan.getContext().getSpanId().toLowerBase16()).isEqualTo(serverSpan.parentId());
        assertThat(serverSpan.tags()).containsExactly(entry("method", "helloworld.Greeter/SayHello"));
        // Show that parentMethod inherits in-process
        assertThat(childSpan.tags()).containsExactly(entry("parentMethod", "edge.Ingress/InitialRoute"));
    }

    @Test
    public void writesCensusPropagation() throws Exception {
        initServer(false);// trace server with census

        initClient(true);// trace client with brave

        io.grpc.examples.helloworld.GreeterGrpc.newBlockingStub(client).sayHello(GreeterImpl.HELLO_REQUEST);
        // this takes 5 seconds due to hard-coding in ExportComponentImpl
        SpanData serverSpan = testHandler.waitForExport(1).get(0);
        zipkin2.Span clientSpan = takeSpan();
        assertThat(clientSpan.traceId()).isEqualTo(serverSpan.getContext().getTraceId().toLowerBase16());
        assertThat(clientSpan.id()).isEqualTo(serverSpan.getParentSpanId().toLowerBase16());
        assertThat(serverSpan.getAttributes().getAttributeMap()).containsEntry("method", AttributeValue.stringAttributeValue("helloworld.Greeter/SayHello"));
    }
}

