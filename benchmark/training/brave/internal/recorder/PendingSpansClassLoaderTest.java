package brave.internal.recorder;


import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.internal.Platform;
import brave.propagation.TraceContext;
import brave.test.util.ClassLoaders;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;


public class PendingSpansClassLoaderTest {
    @Test
    public void unloadable_afterCreateAndRemove() {
        ClassLoaders.assertRunIsUnloadable(PendingSpansClassLoaderTest.CreateAndRemove.class, getClass().getClassLoader());
    }

    static class CreateAndRemove implements Runnable {
        @Override
        public void run() {
            PendingSpans pendingSpans = new PendingSpans(Platform.get().clock(), new FinishedSpanHandler() {
                @Override
                public boolean handle(TraceContext context, MutableSpan span) {
                    return true;
                }
            }, new AtomicBoolean());
            TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).build();
            pendingSpans.getOrCreate(context, true);
            pendingSpans.remove(context);
        }
    }

    @Test
    public void unloadable_afterErrorReporting() {
        ClassLoaders.assertRunIsUnloadable(PendingSpansClassLoaderTest.ErrorReporting.class, getClass().getClassLoader());
    }

    static class ErrorReporting implements Runnable {
        @Override
        public void run() {
            PendingSpans pendingSpans = new PendingSpans(Platform.get().clock(), new FinishedSpanHandler() {
                @Override
                public boolean handle(TraceContext context, MutableSpan span) {
                    throw new RuntimeException();
                }
            }, new AtomicBoolean());
            TraceContext context = TraceContext.newBuilder().traceId(1).spanId(2).build();
            pendingSpans.getOrCreate(context, true);
            context = null;// orphan the context

            try {
                PendingSpansClassLoaderTest.blockOnGC();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            pendingSpans.reportOrphanedSpans();
        }
    }
}

