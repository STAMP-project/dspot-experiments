package brave.dubbo.rpc;


import Span.Kind.SERVER;
import com.alibaba.dubbo.rpc.RpcContext;
import org.junit.Test;
import zipkin2.Span;


public class ITTracingFilter_Provider extends ITTracingFilter {
    @Test
    public void usesExistingTraceId() throws Exception {
        final String traceId = "463ac35c9f6413ad";
        final String parentId = traceId;
        final String spanId = "48485a3953bb6124";
        RpcContext.getContext().getAttachments().put("X-B3-TraceId", traceId);
        RpcContext.getContext().getAttachments().put("X-B3-ParentSpanId", parentId);
        RpcContext.getContext().getAttachments().put("X-B3-SpanId", spanId);
        RpcContext.getContext().getAttachments().put("X-B3-Sampled", "1");
        client.get().sayHello("jorge");
        Span span = takeSpan();
        assertThat(span.traceId()).isEqualTo(traceId);
        assertThat(span.parentId()).isEqualTo(parentId);
        assertThat(span.id()).isEqualTo(spanId);
        assertThat(span.shared()).isTrue();
    }

    @Test
    public void createsChildWhenJoinDisabled() throws Exception {
        setTracing(tracingBuilder(NEVER_SAMPLE).supportsJoin(false).build());
        final String traceId = "463ac35c9f6413ad";
        final String parentId = traceId;
        final String spanId = "48485a3953bb6124";
        RpcContext.getContext().getAttachments().put("X-B3-TraceId", traceId);
        RpcContext.getContext().getAttachments().put("X-B3-ParentSpanId", parentId);
        RpcContext.getContext().getAttachments().put("X-B3-SpanId", spanId);
        RpcContext.getContext().getAttachments().put("X-B3-Sampled", "1");
        client.get().sayHello("jorge");
        Span span = takeSpan();
        assertThat(span.traceId()).isEqualTo(traceId);
        assertThat(span.parentId()).isEqualTo(spanId);
        assertThat(span.id()).isNotEqualTo(spanId);
        assertThat(span.shared()).isNull();
    }

    @Test
    public void samplingDisabled() throws Exception {
        setTracing(tracingBuilder(NEVER_SAMPLE).build());
        client.get().sayHello("jorge");
        // @After will check that nothing is reported
    }

    @Test
    public void currentSpanVisibleToImpl() throws Exception {
        assertThat(client.get().sayHello("jorge")).isNotEmpty();
        takeSpan();
    }

    @Test
    public void reportsServerKindToZipkin() throws Exception {
        client.get().sayHello("jorge");
        Span span = takeSpan();
        assertThat(span.kind()).isEqualTo(SERVER);
    }

    @Test
    public void defaultSpanNameIsMethodName() throws Exception {
        client.get().sayHello("jorge");
        Span span = takeSpan();
        assertThat(span.name()).isEqualTo("genericservice/sayhello");
    }

    @Test
    public void addsErrorTagOnException() throws Exception {
        try {
            client.get().sayHello("bad");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            Span span = takeSpan();
            assertThat(span.tags()).containsExactly(entry("error", "IllegalArgumentException"));
        }
    }
}

