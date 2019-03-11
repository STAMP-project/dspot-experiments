package com.lmax.disruptor;


import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WorkerStressTest {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void shouldHandleLotsOfThreads() throws Exception {
        Disruptor<WorkerStressTest.TestEvent> disruptor = new Disruptor<WorkerStressTest.TestEvent>(WorkerStressTest.TestEvent.FACTORY, (1 << 16), DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new SleepingWaitStrategy());
        RingBuffer<WorkerStressTest.TestEvent> ringBuffer = disruptor.getRingBuffer();
        disruptor.setDefaultExceptionHandler(new FatalExceptionHandler());
        int threads = Math.max(1, ((Runtime.getRuntime().availableProcessors()) / 2));
        int iterations = 200000;
        int publisherCount = threads;
        int handlerCount = threads;
        CyclicBarrier barrier = new CyclicBarrier(publisherCount);
        CountDownLatch latch = new CountDownLatch(publisherCount);
        WorkerStressTest.TestWorkHandler[] handlers = initialise(new WorkerStressTest.TestWorkHandler[handlerCount]);
        WorkerStressTest.Publisher[] publishers = initialise(new WorkerStressTest.Publisher[publisherCount], ringBuffer, iterations, barrier, latch);
        disruptor.handleEventsWithWorkerPool(handlers);
        disruptor.start();
        for (WorkerStressTest.Publisher publisher : publishers) {
            executor.execute(publisher);
        }
        latch.await();
        while ((ringBuffer.getCursor()) < (iterations - 1)) {
            LockSupport.parkNanos(1);
        } 
        disruptor.shutdown();
        for (WorkerStressTest.Publisher publisher : publishers) {
            Assert.assertThat(publisher.failed, CoreMatchers.is(false));
        }
        for (WorkerStressTest.TestWorkHandler handler : handlers) {
            Assert.assertThat(handler.seen, CoreMatchers.is(CoreMatchers.not(0)));
        }
    }

    private static class TestWorkHandler implements WorkHandler<WorkerStressTest.TestEvent> {
        private int seen;

        @Override
        public void onEvent(WorkerStressTest.TestEvent event) throws Exception {
            (seen)++;
        }
    }

    private static class Publisher implements Runnable {
        private final RingBuffer<WorkerStressTest.TestEvent> ringBuffer;

        private final CyclicBarrier barrier;

        private final int iterations;

        private final CountDownLatch shutdownLatch;

        public boolean failed = false;

        Publisher(RingBuffer<WorkerStressTest.TestEvent> ringBuffer, int iterations, CyclicBarrier barrier, CountDownLatch shutdownLatch) {
            this.ringBuffer = ringBuffer;
            this.barrier = barrier;
            this.iterations = iterations;
            this.shutdownLatch = shutdownLatch;
        }

        @Override
        public void run() {
            try {
                barrier.await();
                int i = iterations;
                while ((--i) != (-1)) {
                    long next = ringBuffer.next();
                    WorkerStressTest.TestEvent testEvent = ringBuffer.get(next);
                    testEvent.sequence = next;
                    testEvent.a = next + 13;
                    testEvent.b = next - 7;
                    testEvent.s = "wibble-" + next;
                    ringBuffer.publish(next);
                } 
            } catch (Exception e) {
                failed = true;
            } finally {
                shutdownLatch.countDown();
            }
        }
    }

    private static class TestEvent {
        public long sequence;

        public long a;

        public long b;

        public String s;

        public static final EventFactory<WorkerStressTest.TestEvent> FACTORY = new EventFactory<WorkerStressTest.TestEvent>() {
            @Override
            public WorkerStressTest.TestEvent newInstance() {
                return new WorkerStressTest.TestEvent();
            }
        };
    }
}

