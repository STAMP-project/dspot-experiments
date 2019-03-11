package brave.internal.handler;


import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import zipkin2.Span;


public class ZipkinFinishedSpanHandlerTest {
    List<Span> spans = new ArrayList<>();

    ZipkinFinishedSpanHandler zipkinFinishedSpanHandler;

    @Test
    public void reportsSampledSpan() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).sampled(true).build();
        zipkinFinishedSpanHandler.handle(context, new MutableSpan());
        assertThat(spans.get(0)).isEqualToComparingFieldByField(Span.newBuilder().traceId("1").id("2").localEndpoint(zipkinFinishedSpanHandler.converter.localEndpoint).build());
    }

    @Test
    public void reportsDebugSpan() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).debug(true).build();
        zipkinFinishedSpanHandler.handle(context, new MutableSpan());
        assertThat(spans.get(0)).isEqualToComparingFieldByField(Span.newBuilder().traceId("1").id("2").debug(true).localEndpoint(zipkinFinishedSpanHandler.converter.localEndpoint).build());
    }

    @Test
    public void doesntReportUnsampledSpan() {
        TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).sampled(false).sampledLocal(true).build();
        zipkinFinishedSpanHandler.handle(context, new MutableSpan());
        assertThat(spans).isEmpty();
    }
}

