package brave.http;


import brave.ScopedSpan;
import brave.SpanCustomizer;
import brave.propagation.TraceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import zipkin2.Span;
import zipkin2.brave.Span;

import static HttpSampler.TRACE_ID;


@RunWith(MockitoJUnitRunner.class)
public class HttpClientHandlerTest {
    List<Span> spans = new ArrayList<>();

    HttpTracing httpTracing;

    HttpSampler sampler = TRACE_ID;

    @Mock
    HttpClientAdapter<Object, Object> adapter;

    @Mock
    TraceContext.Injector<Object> injector;

    Object request = new Object();

    HttpClientHandler<Object, Object> handler;

    @Test
    public void handleStart_parsesTagsWithCustomizer() {
        brave.Span span = Mockito.mock(Span.class);
        brave.SpanCustomizer spanCustomizer = Mockito.mock(SpanCustomizer.class);
        Mockito.when(adapter.method(request)).thenReturn("GET");
        Mockito.when(span.customizer()).thenReturn(spanCustomizer);
        handler.handleStart(request, span);
        Mockito.verify(spanCustomizer).name("GET");
        Mockito.verify(spanCustomizer).tag("http.method", "GET");
        Mockito.verifyNoMoreInteractions(spanCustomizer);
    }

    @Test
    public void handleSend_defaultsToMakeNewTrace() {
        assertThat(handler.handleSend(injector, request)).extracting(( s) -> s.isNoop(), ( s) -> s.context().parentId()).containsExactly(false, null);
    }

    @Test
    public void handleSend_makesAChild() {
        ScopedSpan parent = httpTracing.tracing().tracer().startScopedSpan("test");
        try {
            assertThat(handler.handleSend(injector, request)).extracting(( s) -> s.isNoop(), ( s) -> s.context().parentId()).containsExactly(false, parent.context().spanId());
        } finally {
            parent.finish();
        }
    }

    @Test
    public void handleSend_makesRequestBasedSamplingDecision() {
        sampler = Mockito.mock(HttpSampler.class);
        // request sampler says false eventhough trace ID sampler would have said true
        Mockito.when(sampler.trySample(adapter, request)).thenReturn(false);
        assertThat(handler.handleSend(injector, request).isNoop()).isTrue();
    }

    @Test
    public void handleSend_injectsTheTraceContext() {
        TraceContext context = handler.handleSend(injector, request).context();
        Mockito.verify(injector).inject(context, request);
    }

    @Test
    public void handleSend_injectsTheTraceContext_onTheCarrier() {
        Object customCarrier = new Object();
        TraceContext context = handler.handleSend(injector, customCarrier, request).context();
        Mockito.verify(injector).inject(context, customCarrier);
    }

    @Test
    public void handleSend_addsClientAddressWhenOnlyServiceName() {
        httpTracing = httpTracing.clientOf("remote-service");
        HttpClientHandler.create(httpTracing, adapter).handleSend(injector, request).finish();
        assertThat(spans).extracting(Span::remoteServiceName).containsExactly("remote-service");
    }

    @Test
    public void handleSend_skipsClientAddressWhenUnparsed() {
        handler.handleSend(injector, request).finish();
        assertThat(spans).extracting(Span::remoteServiceName).containsNull();
    }
}

