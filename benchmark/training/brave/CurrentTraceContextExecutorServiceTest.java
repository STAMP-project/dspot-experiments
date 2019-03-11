package brave;


import brave.propagation.CurrentTraceContext;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;


/**
 * This class is in a separate test as ExecutorService has more features than everything else
 *
 * <p>Tests were ported from com.github.kristofa.brave.BraveExecutorServiceTest
 */
public class CurrentTraceContextExecutorServiceTest {
    // Ensures one at-a-time, but also on a different thread
    ExecutorService wrappedExecutor = Executors.newSingleThreadExecutor();

    // override default so that it isn't inheritable
    CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build();

    ExecutorService executor = currentTraceContext.executorService(wrappedExecutor);

    TraceContext context = TraceContext.newBuilder().traceId(1).spanId(1).build();

    TraceContext context2 = TraceContext.newBuilder().traceId(2).spanId(1).build();

    final TraceContext[] threadValues = new TraceContext[2];

    CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void execute() throws Exception {
        eachTaskHasCorrectSpanAttached(() -> {
            executor.execute(() -> {
                threadValues[0] = currentTraceContext.get();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });
            // this won't run immediately because the other is blocked
            executor.execute(() -> threadValues[1] = currentTraceContext.get());
            return null;
        });
    }

    @Test
    public void submit_Runnable() throws Exception {
        eachTaskHasCorrectSpanAttached(() -> {
            executor.submit(() -> {
                threadValues[0] = currentTraceContext.get();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });
            // this won't run immediately because the other is blocked
            return executor.submit(() -> threadValues[1] = currentTraceContext.get());
        });
    }

    @Test
    public void submit_Callable() throws Exception {
        eachTaskHasCorrectSpanAttached(() -> {
            executor.submit(() -> {
                threadValues[0] = currentTraceContext.get();
                latch.await();
                return true;
            });
            // this won't run immediately because the other is blocked
            return executor.submit(() -> threadValues[1] = currentTraceContext.get());
        });
    }

    @Test
    public void invokeAll() throws Exception {
        eachTaskHasCorrectSpanAttached(() -> executor.invokeAll(// this won't run immediately because the other is blocked
        Arrays.asList(() -> {
            threadValues[0] = currentTraceContext.get();
            // Can't use externally supplied latch as invokeAll calls get before returning!
            Thread.sleep(100);// block the queue in a dodgy compromise

            return true;
        }, () -> threadValues[1] = currentTraceContext.get())));
    }
}

