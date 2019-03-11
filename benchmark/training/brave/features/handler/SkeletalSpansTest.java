package brave.features.handler;


import Reporter.NOOP;
import brave.Tracer;
import brave.Tracing;
import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import zipkin2.DependencyLink;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.Span.Builder;
import zipkin2.Span.Kind;
import zipkin2.reporter.Reporter;


/**
 * This shows how you can skip local spans to reduce the cost of storage. In this example we are
 * cherry-picking data used by the dependency linker, mostly to make it simpler.
 */
public class SkeletalSpansTest {
    class ReportSkeletalSpans extends FinishedSpanHandler {
        final String localServiceName;

        final Reporter<Span> delegate;

        ReportSkeletalSpans(String localServiceName, Reporter<Span> delegate) {
            this.localServiceName = localServiceName;
            this.delegate = delegate;
        }

        @Override
        public boolean handle(TraceContext context, MutableSpan span) {
            if ((span.kind()) == null)
                return false;
            // skip local spans

            Builder builder = // rewrite the parent ID
            Span.newBuilder().traceId(context.traceIdString()).parentId((context.isLocalRoot() ? null : context.localRootIdString())).id(context.spanIdString()).name(span.name()).kind(Kind.valueOf(span.kind().name())).localEndpoint(Endpoint.newBuilder().serviceName(localServiceName).build());
            if (((span.error()) != null) || ((span.tag("error")) != null)) {
                builder.putTag("error", "");// linking counts errors: the value isn't important

            }
            if ((span.remoteServiceName()) != null) {
                builder.remoteEndpoint(Endpoint.newBuilder().serviceName(span.remoteServiceName()).build());
            }
            delegate.report(builder.build());
            return false;// end of the line

        }
    }

    Map<String, List<Span>> spans = new LinkedHashMap<>();

    Map<String, List<Span>> skeletalSpans = new LinkedHashMap<>();

    Tracer server1Tracer = Tracing.newBuilder().localServiceName("server1").spanReporter(SkeletalSpansTest.toReporter(spans)).build().tracer();

    Tracer server2Tracer = Tracing.newBuilder().localServiceName("server2").spanReporter(SkeletalSpansTest.toReporter(spans)).build().tracer();

    Tracer server1SkeletalTracer = Tracing.newBuilder().addFinishedSpanHandler(new SkeletalSpansTest.ReportSkeletalSpans("server1", SkeletalSpansTest.toReporter(skeletalSpans))).spanReporter(NOOP).build().tracer();

    Tracer server2SkeletalTracer = Tracing.newBuilder().addFinishedSpanHandler(new SkeletalSpansTest.ReportSkeletalSpans("server2", SkeletalSpansTest.toReporter(skeletalSpans))).spanReporter(NOOP).build().tracer();

    @Test
    public void skeletalSpans_skipLocalSpans() {
        assertThat(spans.values()).extracting(( s) -> s.stream().map(zipkin2.Span::name).collect(Collectors.toList())).containsExactly(Arrays.asList("post", "post", "controller", "get"), Arrays.asList("async1"), Arrays.asList("async2"), Arrays.asList("post", "post", "post", "controller2", "get"));
        assertThat(skeletalSpans.values()).extracting(( s) -> s.stream().map(zipkin2.Span::name).collect(Collectors.toList())).containsExactly(Arrays.asList("post", "post", "get"), Arrays.asList("post", "post", "post", "get"));
    }

    @Test
    public void skeletalSpans_produceSameServiceGraph() {
        assertThat(SkeletalSpansTest.link(spans)).containsExactly(DependencyLink.newBuilder().parent("server1").child("server2").callCount(2L).errorCount(1L).build(), DependencyLink.newBuilder().parent("server1").child("uninstrumentedserver").callCount(1L).errorCount(1L).build()).isEqualTo(SkeletalSpansTest.link(skeletalSpans));
    }
}

