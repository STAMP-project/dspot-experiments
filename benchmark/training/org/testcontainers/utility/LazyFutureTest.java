package org.testcontainers.utility;


import com.google.common.util.concurrent.Futures;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.Test;


public class LazyFutureTest {
    @Test
    public void testLazyness() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        Future<Integer> lazyFuture = new LazyFuture<Integer>() {
            @Override
            protected Integer resolve() {
                return counter.incrementAndGet();
            }
        };
        assertEquals("No resolve() invocations before get()", 0, counter.get());
        assertEquals("get() call returns proper result", 1, lazyFuture.get());
        assertEquals("resolve() was called only once after single get() call", 1, counter.get());
        counter.incrementAndGet();
        assertEquals("result of resolve() must be cached", 1, lazyFuture.get());
    }

    @Test(timeout = 5000)
    public void timeoutWorks() throws Exception {
        Future<Void> lazyFuture = new LazyFuture<Void>() {
            @Override
            @SneakyThrows(InterruptedException.class)
            protected Void resolve() {
                TimeUnit.MINUTES.sleep(1);
                return null;
            }
        };
        assertThrows("Should timeout", TimeoutException.class, () -> lazyFuture.get(10, TimeUnit.MILLISECONDS));
        pass("timeout works");
    }

    @Test(timeout = 5000)
    public void testThreadSafety() throws Exception {
        final int numOfThreads = 3;
        CountDownLatch latch = new CountDownLatch(numOfThreads);
        AtomicInteger counter = new AtomicInteger();
        Future<Integer> lazyFuture = new LazyFuture<Integer>() {
            @Override
            @SneakyThrows(InterruptedException.class)
            protected Integer resolve() {
                latch.await();
                return counter.incrementAndGet();
            }
        };
        Future<List<Integer>> task = new ForkJoinPool(numOfThreads).submit(() -> {
            return IntStream.rangeClosed(1, numOfThreads).parallel().mapToObj(( i) -> Futures.getUnchecked(lazyFuture)).collect(Collectors.toList());
        });
        while ((latch.getCount()) > 0) {
            latch.countDown();
        } 
        assertEquals("All threads receives the same result", Collections.nCopies(numOfThreads, 1), task.get());
    }
}

