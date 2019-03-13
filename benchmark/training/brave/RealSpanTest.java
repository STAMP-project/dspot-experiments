package brave;


import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.Span.Kind.CLIENT;
import zipkin2.Span.Kind.SERVER;

import static zipkin2.Span;


public class RealSpanTest {
    List<Span> spans = new ArrayList();

    Tracing tracing = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(spans::add).build();

    Span span = tracing.tracer().newTrace();

    @Test
    public void isNotNoop() {
        assertThat(span.isNoop()).isFalse();
    }

    @Test
    public void hasRealContext() {
        assertThat(span.context().spanId()).isNotZero();
    }

    @Test
    public void hasRealCustomizer() {
        assertThat(span.customizer()).isInstanceOf(RealSpanCustomizer.class);
    }

    @Test
    public void start() {
        span.start();
        span.flush();
        assertThat(spans).hasSize(1).first().extracting(Span::timestamp).isNotNull();
    }

    @Test
    public void start_timestamp() {
        span.start(2);
        span.flush();
        assertThat(spans).hasSize(1).first().extracting(Span::timestamp).isEqualTo(2L);
    }

    @Test
    public void finish() {
        span.start();
        span.finish();
        assertThat(spans).hasSize(1).first().extracting(Span::duration).isNotNull();
    }

    @Test
    public void finish_timestamp() {
        span.start(2);
        span.finish(5);
        assertThat(spans).hasSize(1).first().extracting(Span::duration).isEqualTo(3L);
    }

    @Test
    public void abandon() {
        span.start();
        span.abandon();
        assertThat(spans).hasSize(0);
    }

    @Test
    public void annotate() {
        span.annotate("foo");
        span.flush();
        assertThat(spans).flatExtracting(Span::annotations).extracting(Annotation::value).containsExactly("foo");
    }

    @Test
    public void remoteEndpoint_nulls() {
        span.remoteEndpoint(Endpoint.newBuilder().build());
        span.flush();
        assertThat(spans.get(0).remoteEndpoint()).isNull();
    }

    @Test
    public void annotate_timestamp() {
        span.annotate(2, "foo");
        span.flush();
        assertThat(spans).flatExtracting(Span::annotations).containsExactly(Annotation.create(2L, "foo"));
    }

    @Test
    public void tag() {
        span.tag("foo", "bar");
        span.flush();
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("foo", "bar"));
    }

    @Test
    public void finished_client_annotation() {
        finish("cs", "cr", CLIENT);
    }

    @Test
    public void finished_server_annotation() {
        finish("sr", "ss", SERVER);
    }

    @Test
    public void doubleFinishDoesntDoubleReport() {
        Span span = tracing.tracer().newTrace().name("foo").start();
        span.finish();
        span.finish();
        assertThat(spans).hasSize(1);
    }

    @Test
    public void finishAfterAbandonDoesntReport() {
        span.start();
        span.abandon();
        span.finish();
        assertThat(spans).hasSize(0);
    }

    @Test
    public void abandonAfterFinishDoesNothing() {
        span.start();
        span.finish();
        span.abandon();
        assertThat(spans).hasSize(1);
    }

    @Test
    public void error() {
        span.error(new RuntimeException("this cake is a lie"));
        span.flush();
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("error", "this cake is a lie"));
    }

    @Test
    public void error_noMessage() {
        span.error(new RuntimeException());
        span.flush();
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("error", "RuntimeException"));
    }
}

