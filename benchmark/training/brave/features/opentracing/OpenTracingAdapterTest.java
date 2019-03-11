package brave.features.opentracing;


import B3Propagation.FACTORY;
import Format.Builtin.HTTP_HEADERS;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.propagation.TextMapInjectAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import zipkin2.Span;


/**
 * This shows how one might make an OpenTracing adapter for Brave, and how to navigate in and out of
 * the core concepts.
 */
public class OpenTracingAdapterTest {
    List<Span> spans = new ArrayList<>();

    Tracing brave = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build()).propagationFactory(ExtraFieldPropagation.newFactory(FACTORY, "client-id")).spanReporter(spans::add).build();

    BraveTracer opentracing = BraveTracer.wrap(brave);

    @Test
    public void startWithOpenTracingAndFinishWithBrave() {
        io.opentracing.Span openTracingSpan = opentracing.buildSpan("encode").withTag("lc", "codec").withStartTimestamp(1L).start();
        brave.Span braveSpan = ((BraveSpan) (openTracingSpan)).unwrap();
        braveSpan.annotate(2L, "pump fake");
        braveSpan.finish(3L);
        checkSpanReportedToZipkin();
    }

    @Test
    public void startWithBraveAndFinishWithOpenTracing() {
        brave.Span braveSpan = brave.tracer().newTrace().name("encode").tag("lc", "codec").start(1L);
        io.opentracing.Span openTracingSpan = BraveSpan.wrap(braveSpan);
        openTracingSpan.log(2L, "pump fake");
        openTracingSpan.finish(3L);
        checkSpanReportedToZipkin();
    }

    @Test
    public void extractTraceContext() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("X-B3-TraceId", "0000000000000001");
        map.put("X-B3-SpanId", "0000000000000002");
        map.put("X-B3-Sampled", "1");
        map.put("Client-Id", "sammy");
        BraveSpanContext openTracingContext = opentracing.extract(HTTP_HEADERS, new TextMapExtractAdapter(map));
        assertThat(openTracingContext.unwrap()).isEqualTo(TraceContext.newBuilder().traceId(1L).spanId(2L).shared(true).sampled(true).build());
        assertThat(openTracingContext.baggageItems()).containsExactly(entry("client-id", "sammy"));
    }

    @Test
    public void injectTraceContext() throws Exception {
        TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(2L).sampled(true).build();
        Map<String, String> map = new LinkedHashMap<>();
        TextMapInjectAdapter carrier = new TextMapInjectAdapter(map);
        opentracing.inject(BraveSpanContext.wrap(context), HTTP_HEADERS, carrier);
        assertThat(map).containsExactly(entry("X-B3-TraceId", "0000000000000001"), entry("X-B3-SpanId", "0000000000000002"), entry("X-B3-Sampled", "1"));
    }
}

