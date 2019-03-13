package org.stagemonitor.tracing.reporter;


import io.opentracing.mock.MockTracer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class LoggingSpanReporterTest {
    private LoggingSpanReporter loggingSpanReporter;

    private TracingPlugin tracingPlugin;

    private MockTracer mockTracer;

    @Test
    public void report() throws Exception {
        final SpanWrapper span = new SpanWrapper(mockTracer.buildSpan("test").start(), "test", 0, 0, Collections.emptyList(), new ConcurrentHashMap());
        span.setTag("foo.bar", "baz");
        final String logMessage = loggingSpanReporter.getLogMessage(span);
        Assert.assertTrue(logMessage.contains("foo.bar: baz"));
    }

    @Test
    public void isActive() throws Exception {
        Mockito.when(tracingPlugin.isLogSpans()).thenReturn(true);
        Assert.assertTrue(loggingSpanReporter.isActive(null));
        Mockito.when(tracingPlugin.isLogSpans()).thenReturn(false);
        Assert.assertFalse(loggingSpanReporter.isActive(null));
    }

    @Test
    public void testLoadedViaServiceLoader() throws Exception {
        List<Class<? extends SpanReporter>> spanReporters = new ArrayList<>();
        ServiceLoader.load(SpanReporter.class).forEach(( reporter) -> spanReporters.add(reporter.getClass()));
        Assert.assertTrue(spanReporters.contains(LoggingSpanReporter.class));
    }
}

