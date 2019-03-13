package brave.okhttp3;


import Interceptor.Chain;
import Reporter.NOOP;
import brave.Span;
import brave.Tracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TracingInterceptorTest {
    Tracing tracing = Tracing.newBuilder().currentTraceContext(ThreadLocalCurrentTraceContext.create()).spanReporter(NOOP).build();

    @Mock
    Chain chain;

    @Mock
    Span span;

    @Test
    public void parseRouteAddress_skipsOnNoop() {
        Mockito.when(span.isNoop()).thenReturn(true);
        TracingInterceptor.parseRouteAddress(chain, span);
        Mockito.verify(span).isNoop();
        Mockito.verifyNoMoreInteractions(span);
    }
}

