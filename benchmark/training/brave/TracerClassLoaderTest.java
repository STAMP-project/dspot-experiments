package brave;


import Tracing.LoggingReporter;
import brave.test.util.ClassLoaders;
import org.junit.Test;
import zipkin2.Span;


public class TracerClassLoaderTest {
    @Test
    public void unloadable_withLoggingReporter() {
        ClassLoaders.assertRunIsUnloadable(TracerClassLoaderTest.UsingLoggingReporter.class, getClass().getClassLoader());
    }

    static class UsingLoggingReporter implements Runnable {
        @Override
        public void run() {
            Tracing.LoggingReporter reporter = new Tracing.LoggingReporter();
            reporter.report(Span.newBuilder().traceId("a").id("b").build());
        }
    }
}

