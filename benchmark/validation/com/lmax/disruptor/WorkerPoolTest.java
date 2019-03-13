package com.lmax.disruptor;


import DaemonThreadFactory.INSTANCE;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WorkerPoolTest {
    @SuppressWarnings("unchecked")
    @Test
    public void shouldProcessEachMessageByOnlyOneWorker() throws Exception {
        Executor executor = Executors.newCachedThreadPool(INSTANCE);
        WorkerPool<AtomicLong> pool = new WorkerPool<AtomicLong>(new WorkerPoolTest.AtomicLongEventFactory(), new FatalExceptionHandler(), new WorkerPoolTest.AtomicLongWorkHandler(), new WorkerPoolTest.AtomicLongWorkHandler());
        RingBuffer<AtomicLong> ringBuffer = pool.start(executor);
        ringBuffer.next();
        ringBuffer.next();
        ringBuffer.publish(0);
        ringBuffer.publish(1);
        Thread.sleep(500);
        Assert.assertThat(ringBuffer.get(0).get(), CoreMatchers.is(1L));
        Assert.assertThat(ringBuffer.get(1).get(), CoreMatchers.is(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldProcessOnlyOnceItHasBeenPublished() throws Exception {
        Executor executor = Executors.newCachedThreadPool(INSTANCE);
        WorkerPool<AtomicLong> pool = new WorkerPool<AtomicLong>(new WorkerPoolTest.AtomicLongEventFactory(), new FatalExceptionHandler(), new WorkerPoolTest.AtomicLongWorkHandler(), new WorkerPoolTest.AtomicLongWorkHandler());
        RingBuffer<AtomicLong> ringBuffer = pool.start(executor);
        ringBuffer.next();
        ringBuffer.next();
        Thread.sleep(1000);
        Assert.assertThat(ringBuffer.get(0).get(), CoreMatchers.is(0L));
        Assert.assertThat(ringBuffer.get(1).get(), CoreMatchers.is(0L));
    }

    private static class AtomicLongWorkHandler implements WorkHandler<AtomicLong> {
        @Override
        public void onEvent(AtomicLong event) throws Exception {
            event.incrementAndGet();
        }
    }

    private static class AtomicLongEventFactory implements EventFactory<AtomicLong> {
        @Override
        public AtomicLong newInstance() {
            return new AtomicLong(0);
        }
    }
}

