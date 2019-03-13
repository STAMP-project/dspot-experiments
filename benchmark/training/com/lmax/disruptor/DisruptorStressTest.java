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


public class DisruptorStressTest {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void shouldHandleLotsOfThreads() throws Exception {
        Disruptor<DisruptorStressTest.TestEvent> disruptor = new Disruptor<DisruptorStressTest.TestEvent>(DisruptorStressTest.TestEvent.FACTORY, (1 << 16), DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new BusySpinWaitStrategy());
        RingBuffer<DisruptorStressTest.TestEvent> ringBuffer = disruptor.getRingBuffer();
        disruptor.setDefaultExceptionHandler(new FatalExceptionHandler());
        int threads = Math.max(1, ((Runtime.getRuntime().availableProcessors()) / 2));
        int iterations = 200000;
        int publisherCount = threads;
        int handlerCount = threads;
        CyclicBarrier barrier = new CyclicBarrier(publisherCount);
        CountDownLatch latch = new CountDownLatch(publisherCount);
        DisruptorStressTest.TestEventHandler[] handlers = initialise(disruptor, new DisruptorStressTest.TestEventHandler[handlerCount]);
        DisruptorStressTest.Publisher[] publishers = initialise(new DisruptorStressTest.Publisher[publisherCount], ringBuffer, iterations, barrier, latch);
        disruptor.start();
        for (DisruptorStressTest.Publisher publisher : publishers) {
            executor.execute(publisher);
        }
        latch.await();
        while ((ringBuffer.getCursor()) < (iterations - 1)) {
            LockSupport.parkNanos(1);
        } 
        disruptor.shutdown();
        for (DisruptorStressTest.Publisher publisher : publishers) {
            Assert.assertThat(publisher.failed, CoreMatchers.is(false));
        }
        for (DisruptorStressTest.TestEventHandler handler : handlers) {
            Assert.assertThat(handler.messagesSeen, CoreMatchers.is(CoreMatchers.not(0)));
            Assert.assertThat(handler.failureCount, CoreMatchers.is(0));
        }
    }

    private static class TestEventHandler implements EventHandler<DisruptorStressTest.TestEvent> {
        public int failureCount = 0;

        public int messagesSeen = 0;

        TestEventHandler() {
        }

        @Override
        public void onEvent(DisruptorStressTest.TestEvent event, long sequence, boolean endOfBatch) throws Exception {
            if (((((event.sequence) != sequence) || ((event.a) != (sequence + 13))) || ((event.b) != (sequence - 7))) || (!(("wibble-" + sequence).equals(event.s)))) {
                (failureCount)++;
            }
            (messagesSeen)++;
        }
    }

    private static class Publisher implements Runnable {
        private final RingBuffer<DisruptorStressTest.TestEvent> ringBuffer;

        private final CyclicBarrier barrier;

        private final int iterations;

        private final CountDownLatch shutdownLatch;

        public boolean failed = false;

        Publisher(RingBuffer<DisruptorStressTest.TestEvent> ringBuffer, int iterations, CyclicBarrier barrier, CountDownLatch shutdownLatch) {
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
                    DisruptorStressTest.TestEvent testEvent = ringBuffer.get(next);
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

        public static final EventFactory<DisruptorStressTest.TestEvent> FACTORY = new EventFactory<DisruptorStressTest.TestEvent>() {
            @Override
            public DisruptorStressTest.TestEvent newInstance() {
                return new DisruptorStressTest.TestEvent();
            }
        };
    }
}

