package org.jctools.queues;


import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class QueueSanityTestMpmcArray extends QueueSanityTest {
    public QueueSanityTestMpmcArray(ConcurrentQueueSpec spec, Queue<Integer> queue) {
        super(spec, queue);
    }

    @Test
    public void testOfferPollSemantics() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue<Integer> q = queue;
        // fill up the queue
        while (q.offer(1)) {
        } 
        // queue has 2 empty slots
        q.poll();
        q.poll();
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    if (!(q.offer(1))) {
                        (fail.value)++;
                    }
                    if ((q.poll()) == null) {
                        (fail.value)++;
                    }
                } 
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    if (!(q.offer(1))) {
                        (fail.value)++;
                    }
                    if ((q.poll()) == null) {
                        (fail.value)++;
                    }
                } 
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(1000);
        stop.set(true);
        t1.join();
        t2.join();
        Assert.assertEquals("Unexpected offer/poll observed", 0, fail.value);
    }
}

