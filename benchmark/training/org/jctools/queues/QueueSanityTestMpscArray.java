package org.jctools.queues;


import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class QueueSanityTestMpscArray extends QueueSanityTest {
    public QueueSanityTestMpscArray(ConcurrentQueueSpec spec, Queue<Integer> queue) {
        super(spec, queue);
    }

    @Test
    public void testOfferPollSemantics() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final AtomicBoolean consumerLock = new AtomicBoolean(true);
        final Queue<Integer> q = new MpscArrayQueue<Integer>(2);
        // fill up the queue
        while (q.offer(1)) {
        } 
        // queue has 2 empty slots
        q.poll();
        q.poll();
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    if (!(q.offer(1))) {
                        (fail.value)++;
                    }
                    while (!(consumerLock.compareAndSet(true, false))) {
                    } 
                    if ((q.poll()) == null) {
                        (fail.value)++;
                    }
                    consumerLock.lazySet(true);
                } 
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
}

