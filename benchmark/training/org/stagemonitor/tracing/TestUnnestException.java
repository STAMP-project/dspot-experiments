package org.stagemonitor.tracing;


import io.opentracing.Span;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.util.NestedServletException;
import org.stagemonitor.tracing.utils.SpanUtils;


public class TestUnnestException {
    @Test
    public void testUnnestNestedServletException() throws Exception {
        final TracingPlugin tracingPlugin = new TracingPlugin();
        final Span span = Mockito.mock(Span.class);
        SpanUtils.setException(span, new NestedServletException("Eat this!", new RuntimeException("bazinga!")), tracingPlugin.getIgnoreExceptions(), tracingPlugin.getUnnestExceptions());
        Mockito.verify(span).setTag("exception.class", "java.lang.RuntimeException");
        Mockito.verify(span).setTag("exception.message", "bazinga!");
    }
}

