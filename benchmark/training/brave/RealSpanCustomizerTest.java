package brave;


import brave.propagation.ThreadLocalCurrentTraceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import zipkin2.Span;

import static zipkin2.Span;


public class RealSpanCustomizerTest {
    List<Span> spans = new ArrayList();

    Tracing tracing = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(spans::add).build();

    Span span = tracing.tracer().newTrace();

    SpanCustomizer spanCustomizer = span.customizer();

    @Test
    public void name() {
        spanCustomizer.name("foo");
        span.flush();
        assertThat(spans).extracting(Span::name).containsExactly("foo");
    }

    @Test
    public void annotate() {
        spanCustomizer.annotate("foo");
        span.flush();
        assertThat(spans).flatExtracting(Span::annotations).extracting(Annotation::value).containsExactly("foo");
    }

    @Test
    public void tag() {
        spanCustomizer.tag("foo", "bar");
        span.flush();
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("foo", "bar"));
    }
}

