package brave.propagation;


import Reporter.NOOP;
import ThreadLocalSpan.CURRENT_TRACER;
import brave.Tracing;
import brave.test.util.ClassLoaders;
import org.junit.Test;

import static ThreadLocalSpan.CURRENT_TRACER;


public class ThreadLocalSpanClassLoaderTest {
    @Test
    public void noop_unloadable() {
        ClassLoaders.assertRunIsUnloadable(ThreadLocalSpanClassLoaderTest.CurrentTracerUnassigned.class, getClass().getClassLoader());
    }

    static class CurrentTracerUnassigned implements Runnable {
        @Override
        public void run() {
            CURRENT_TRACER.next();
        }
    }

    @Test
    public void currentTracer_basicUsage_unloadable() {
        ClassLoaders.assertRunIsUnloadable(ThreadLocalSpanClassLoaderTest.ExplicitTracerBasicUsage.class, getClass().getClassLoader());
    }

    static class ExplicitTracerBasicUsage implements Runnable {
        @Override
        public void run() {
            try (Tracing tracing = Tracing.newBuilder().spanReporter(NOOP).build()) {
                ThreadLocalSpan tlSpan = ThreadLocalSpan.create(tracing.tracer());
                tlSpan.next();
                tlSpan.remove().finish();
            }
        }
    }

    @Test
    public void explicitTracer_basicUsage_unloadable() {
        ClassLoaders.assertRunIsUnloadable(ThreadLocalSpanClassLoaderTest.CurrentTracerBasicUsage.class, getClass().getClassLoader());
    }

    static class CurrentTracerBasicUsage implements Runnable {
        @Override
        public void run() {
            try (Tracing tracing = Tracing.newBuilder().spanReporter(NOOP).build()) {
                ThreadLocalSpan tlSpan = CURRENT_TRACER;
                tlSpan.next();
                tlSpan.remove().finish();
            }
        }
    }

    /**
     * TODO: While it is an instrumentation bug to not complete a thread-local span, we should be
     * tolerant, for example considering weak references or similar.
     */
    @Test(expected = AssertionError.class)
    public void unfinishedSpan_preventsUnloading() {
        ClassLoaders.assertRunIsUnloadable(ThreadLocalSpanClassLoaderTest.CurrentTracerDoesntFinishSpan.class, getClass().getClassLoader());
    }

    static class CurrentTracerDoesntFinishSpan implements Runnable {
        @Override
        public void run() {
            try (Tracing tracing = Tracing.newBuilder().spanReporter(NOOP).build()) {
                CURRENT_TRACER.next();
            }
        }
    }
}

