package org.jctools.queues;


import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.State.WAITING;


public class QueueSanityTestMpscBlockingConsumerArrayExtended {
    @Test
    public void testOfferPollSemantics() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final AtomicBoolean consumerLock = new AtomicBoolean(true);
        final Queue<Integer> q = new MpscBlockingConsumerArrayQueue(2);
        // fill up the queue
        while (q.offer(1));
        // queue has 2 empty slots
        q.poll();
        q.poll();
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = () -> {
            while (!(stop.get())) {
                if (!(q.offer(1))) {
                    (fail.value)++;
                }
                while (!(consumerLock.compareAndSet(true, false)));
                if ((q.poll()) == null) {
                    (fail.value)++;
                }
                consumerLock.lazySet(true);
            } 
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("Unexpected offer/poll observed", 0, fail.value);
    }

    @Test
    public void testOfferTakeSemantics() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final AtomicBoolean consumerLock = new AtomicBoolean(true);
        final MpscBlockingConsumerArrayQueue<Integer> q = new MpscBlockingConsumerArrayQueue(2);
        // fill up the queue
        while (q.offer(1));
        // queue has 2 empty slots
        q.poll();
        q.poll();
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = () -> {
            while (!(stop.get())) {
                if (!(q.offer(1))) {
                    (fail.value)++;
                }
                while (!(consumerLock.compareAndSet(true, false)));
                try {
                    if ((q.take()) == null) {
                        (fail.value)++;
                    }
                } catch (InterruptedException e) {
                    (fail.value)++;
                }
                consumerLock.lazySet(true);
            } 
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("Unexpected offer/poll observed", 0, fail.value);
    }

    @Test(timeout = 1000L)
    public void testTakeBlocksAndIsInterrupted() throws Exception {
        final AtomicBoolean wasInterrupted = new AtomicBoolean();
        final MpscBlockingConsumerArrayQueue<Integer> q = new MpscBlockingConsumerArrayQueue(1024);
        Thread consumer = new Thread(() -> {
            try {
                q.take();
            } catch (InterruptedException e) {
                wasInterrupted.set(true);
            }
        });
        consumer.setDaemon(true);
        consumer.start();
        while ((consumer.getState()) != (WAITING)) {
            Thread.yield();
        } 
        // If we got here -> thread got to the waiting state -> parked
        consumer.interrupt();
        consumer.join();
        Assert.assertTrue(wasInterrupted.get());
    }

    @Test(timeout = 1000L)
    public void testTakeSomeElementsThenBlocksAndIsInterrupted() throws Exception {
        QueueSanityTest.Val v = new QueueSanityTest.Val();
        final AtomicBoolean wasInterrupted = new AtomicBoolean();
        final MpscBlockingConsumerArrayQueue<Integer> q = new MpscBlockingConsumerArrayQueue(1024);
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    Integer take = q.take();
                    Assert.assertNotNull(take);// take never returns null

                    Assert.assertEquals(take.intValue(), v.value);
                    (v.value)++;
                } 
            } catch (InterruptedException e) {
                wasInterrupted.set(true);
            }
        });
        consumer.setDaemon(true);
        consumer.start();
        while ((consumer.getState()) != (WAITING)) {
            Thread.yield();
        } 
        // If we got here -> thread got to the waiting state -> parked
        int someElements = ThreadLocalRandom.current().nextInt(10000);
        for (int i = 0; i < someElements; i++)
            while (!(q.offer(i)));

        while (!(q.isEmpty())) {
            Thread.yield();
        } 
        // Eventually queue is drained
        while ((consumer.getState()) != (WAITING)) {
            Thread.yield();
        } 
        // If we got here -> thread got to the waiting state -> parked
        consumer.interrupt();
        consumer.join();
        Assert.assertTrue(wasInterrupted.get());
        Assert.assertEquals(someElements, v.value);
    }
}

