package brave;


import NoopSpanCustomizer.INSTANCE;
import org.junit.Test;


public class NoopSpanTest {
    Tracer tracer = Tracing.newBuilder().sampler(Sampler.NEVER_SAMPLE).currentTraceContext(brave.propagation.ThreadLocalCurrentTraceContext.create()).clock(() -> {
        throw new AssertionError();
    }).spanReporter(( s) -> {
        throw new AssertionError();
    }).build().tracer();

    Span span = tracer.newTrace();

    @Test
    public void isNoop() {
        assertThat(span.isNoop()).isTrue();
    }

    @Test
    public void hasRealContext() {
        assertThat(span.context().spanId()).isNotZero();
    }

    @Test
    public void hasNoopCustomizer() {
        assertThat(span.customizer()).isSameAs(INSTANCE);
    }

    @Test
    public void doesNothing() {
        // Since our clock and spanReporter throw, we know this is doing nothing
        span.start();
        span.start(1L);
        span.annotate("foo");
        span.annotate(2L, "foo");
        span.tag("bar", "baz");
        span.remoteServiceName("aloha");
        span.remoteIpAndPort("1.2.3.4", 9000);
        span.finish(1L);
        span.finish();
        span.abandon();
        span.flush();
    }
}

