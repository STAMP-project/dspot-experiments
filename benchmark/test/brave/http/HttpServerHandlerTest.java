package brave.http;


import SamplingFlags.EMPTY;
import SamplingFlags.NOT_SAMPLED;
import Span.Kind.SERVER;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.Tracing;
import brave.brave.Span;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import zipkin2.Span;


@RunWith(MockitoJUnitRunner.class)
public class HttpServerHandlerTest {
    List<Span> spans = new ArrayList<>();

    Tracer tracer;

    @Mock
    HttpSampler sampler;

    @Mock
    HttpServerAdapter<Object, Object> adapter;

    @Mock
    TraceContext.Extractor<Object> extractor;

    Object request = new Object();

    HttpServerHandler<Object, Object> handler;

    @Test
    public void handleStart_parsesTagsWithCustomizer() {
        brave.Span span = Mockito.mock(brave.Span.class);
        brave.SpanCustomizer spanCustomizer = Mockito.mock(SpanCustomizer.class);
        Mockito.when(span.kind(SERVER)).thenReturn(span);
        Mockito.when(adapter.method(request)).thenReturn("GET");
        Mockito.when(span.customizer()).thenReturn(spanCustomizer);
        handler.handleStart(request, span);
        Mockito.verify(spanCustomizer).name("GET");
        Mockito.verify(spanCustomizer).tag("http.method", "GET");
        Mockito.verifyNoMoreInteractions(spanCustomizer);
    }

    @Test
    public void handleReceive_defaultsToMakeNewTrace() {
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(EMPTY));
        // request sampler abstains (trace ID sampler will say true)
        Mockito.when(sampler.trySample(adapter, request)).thenReturn(null);
        brave.Span newSpan = handler.handleReceive(extractor, request);
        assertThat(newSpan.isNoop()).isFalse();
        assertThat(newSpan.context().shared()).isFalse();
    }

    @Test
    public void handleReceive_reusesTraceId() {
        HttpTracing httpTracing = HttpTracing.create(Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).supportsJoin(false).spanReporter(spans::add).build());
        tracer = httpTracing.tracing().tracer();
        handler = HttpServerHandler.create(httpTracing, adapter);
        TraceContext incomingContext = tracer.nextSpan().context();
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(incomingContext));
        assertThat(handler.handleReceive(extractor, request).context()).extracting(TraceContext::traceId, TraceContext::parentId, TraceContext::shared).containsOnly(incomingContext.traceId(), incomingContext.spanId(), false);
    }

    @Test
    public void handleReceive_reusesSpanIds() {
        TraceContext incomingContext = tracer.nextSpan().context();
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(incomingContext));
        assertThat(handler.handleReceive(extractor, request).context()).isEqualTo(incomingContext.toBuilder().shared(true).build());
    }

    @Test
    public void handleReceive_honorsSamplingFlags() {
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(NOT_SAMPLED));
        assertThat(handler.handleReceive(extractor, request).isNoop()).isTrue();
    }

    @Test
    public void handleReceive_makesRequestBasedSamplingDecision_flags() {
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(EMPTY));
        // request sampler says false eventhough trace ID sampler would have said true
        Mockito.when(sampler.trySample(adapter, request)).thenReturn(false);
        assertThat(handler.handleReceive(extractor, request).isNoop()).isTrue();
    }

    @Test
    public void handleReceive_makesRequestBasedSamplingDecision_context() {
        TraceContext incomingContext = tracer.nextSpan().context().toBuilder().sampled(null).build();
        Mockito.when(extractor.extract(request)).thenReturn(TraceContextOrSamplingFlags.create(incomingContext));
        // request sampler says false eventhough trace ID sampler would have said true
        Mockito.when(sampler.trySample(adapter, request)).thenReturn(false);
        assertThat(handler.handleReceive(extractor, request).isNoop()).isTrue();
    }
}

