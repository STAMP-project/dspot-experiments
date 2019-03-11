package brave.http;


import brave.Span;
import brave.SpanCustomizer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class HttpHandlerTest {
    CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.create();

    TraceContext context = TraceContext.newBuilder().traceId(1L).spanId(10L).build();

    @Mock
    HttpAdapter<Object, Object> adapter;

    @Mock
    Span span;

    @Mock
    SpanCustomizer spanCustomizer;

    Object request = new Object();

    Object response = new Object();

    HttpHandler<Object, Object, HttpAdapter<Object, Object>> handler;

    @Test
    public void handleStart_nothingOnNoop_success() {
        Mockito.when(span.isNoop()).thenReturn(true);
        handler.handleStart(request, span);
        Mockito.verify(span, Mockito.never()).start();
    }

    @Test
    public void handleStart_parsesTagsInScope() {
        handler = new HttpHandler(currentTraceContext, adapter, new HttpParser()) {
            @Override
            void parseRequest(Object request, Span span) {
                assertThat(currentTraceContext.get()).isNotNull();
            }
        };
        handler.handleStart(request, span);
    }

    @Test
    public void handleStart_addsRemoteEndpointWhenParsed() {
        handler = new HttpHandler(currentTraceContext, adapter, new HttpParser()) {
            @Override
            void parseRequest(Object request, Span span) {
                span.remoteIpAndPort("1.2.3.4", 0);
            }
        };
        handler.handleStart(request, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 0);
    }

    @Test
    public void handleFinish_nothingOnNoop_success() {
        Mockito.when(span.isNoop()).thenReturn(true);
        handler.handleFinish(response, null, span);
        Mockito.verify(span, Mockito.never()).finish();
    }

    @Test
    public void handleFinish_nothingOnNoop_error() {
        Mockito.when(span.isNoop()).thenReturn(true);
        handler.handleFinish(null, new RuntimeException("drat"), span);
        Mockito.verify(span, Mockito.never()).finish();
    }

    @Test
    public void handleFinish_parsesTagsWithCustomizer() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(404);
        Mockito.when(span.customizer()).thenReturn(spanCustomizer);
        handler.handleFinish(response, null, span);
        Mockito.verify(spanCustomizer).tag("http.status_code", "404");
        Mockito.verify(spanCustomizer).tag("error", "404");
        Mockito.verifyNoMoreInteractions(spanCustomizer);
    }

    @Test
    public void handleFinish_parsesTagsInScope() {
        handler = new HttpHandler(currentTraceContext, adapter, new HttpParser() {
            @Override
            public <Resp> void response(HttpAdapter<?, Resp> adapter, Resp res, Throwable error, SpanCustomizer customizer) {
                assertThat(currentTraceContext.get()).isNotNull();
            }
        }) {
            @Override
            void parseRequest(Object request, Span span) {
            }
        };
        handler.handleFinish(response, null, span);
    }

    @Test
    public void handleFinish_finishesWhenSpanNotInScope() {
        Mockito.doAnswer(( invocation) -> {
            assertThat(currentTraceContext.get()).isNull();
            return null;
        }).when(span).finish();
        handler.handleFinish(response, null, span);
    }

    @Test
    public void handleFinish_finishesWhenSpanNotInScope_clearingIfNecessary() {
        try (CurrentTraceContext.Scope ws = currentTraceContext.newScope(context)) {
            handleFinish_finishesWhenSpanNotInScope();
        }
    }

    @Test
    public void handleFinish_finishedEvenIfAdapterThrows() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenThrow(new RuntimeException());
        try {
            handler.handleFinish(response, null, span);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
            Mockito.verify(span).finish();
        }
    }
}

