package brave;


import Reporter.NOOP;
import brave.test.util.ClassLoaders;
import org.junit.Test;


public class TracingClassLoaderTest {
    @Test
    public void unloadable_afterClose() {
        ClassLoaders.assertRunIsUnloadable(TracingClassLoaderTest.ClosesTracing.class, getClass().getClassLoader());
    }

    static class ClosesTracing implements Runnable {
        @Override
        public void run() {
            try (Tracing tracing = Tracing.newBuilder().spanReporter(NOOP).build()) {
            }
        }
    }

    @Test
    public void unloadable_afterBasicUsage() {
        ClassLoaders.assertRunIsUnloadable(TracingClassLoaderTest.BasicUsage.class, getClass().getClassLoader());
    }

    static class BasicUsage implements Runnable {
        @Override
        public void run() {
            try (Tracing tracing = Tracing.newBuilder().spanReporter(NOOP).build()) {
                tracing.tracer().newTrace().start().finish();
            }
        }
    }
}

