package org.jctools.queues.alt;


import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ConcurrentQueueSanityTest {
    private static final int SIZE = 8192 * 2;

    final ConcurrentQueue<Integer> q;

    final ConcurrentQueueSpec spec;

    public ConcurrentQueueSanityTest(ConcurrentQueueSpec spec) {
        q = ConcurrentQueueFactory.newQueue(spec);
        this.spec = spec;
    }

    @Test
    public void sanity() {
        final ConcurrentQueueConsumer<Integer> consumer = q.consumer();
        for (int i = 0; i < (ConcurrentQueueSanityTest.SIZE); i++) {
            Assert.assertNull(consumer.poll());
            Assert.assertEquals(0, q.size());
        }
        int i = 0;
        final ConcurrentQueueProducer<Integer> producer = q.producer();
        while ((i < (ConcurrentQueueSanityTest.SIZE)) && (producer.offer(i)))
            i++;

        int size = i;
        Assert.assertEquals(size, q.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            i = 0;
            Integer e;
            while ((e = consumer.poll()) != null) {
                Assert.assertEquals((size - (i + 1)), q.size());
                Assert.assertEquals(e.intValue(), (i++));
            } 
            Assert.assertEquals(size, i);
        } else {
            // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
            int sum = ((size - 1) * size) / 2;
            i = 0;
            Integer e;
            while ((e = consumer.poll()) != null) {
                Assert.assertEquals((--size), q.size());
                sum -= e;
            } 
            Assert.assertEquals(0, sum);
        }
    }

    @Test
    public void sanityWeak() {
        final ConcurrentQueueConsumer<Integer> consumer = q.consumer();
        for (int i = 0; i < (ConcurrentQueueSanityTest.SIZE); i++) {
            Assert.assertNull(consumer.weakPoll());
            Assert.assertEquals(0, q.size());
        }
        int i = 0;
        final ConcurrentQueueProducer<Integer> producer = q.producer();
        while ((i < (ConcurrentQueueSanityTest.SIZE)) && (producer.weakOffer(i)))
            i++;

        int size = i;
        Assert.assertEquals(size, q.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            i = 0;
            Integer e;
            while ((e = consumer.weakPoll()) != null) {
                Assert.assertEquals((size - (i + 1)), q.size());
                Assert.assertEquals(e.intValue(), (i++));
            } 
            Assert.assertEquals(size, i);
        } else {
            // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
            int sum = ((size - 1) * size) / 2;
            i = 0;
            Integer e;
            while ((e = consumer.weakPoll()) != null) {
                Assert.assertEquals((--size), q.size());
                sum -= e;
            } 
            Assert.assertEquals(0, sum);
        }
    }

    int testCounter = 0;

    @Test
    public void sanityBatch() {
        final ConcurrentQueueConsumer<Integer> consumer = q.consumer();
        // consume batch will consume nothing, queue is empty
        for (int i = 0; i < (ConcurrentQueueSanityTest.SIZE); i += 10) {
            Assert.assertEquals(0, consumer.consume(( e) -> {
                Assert.fail("expecting no elements");
            }, 10));
            Assert.assertEquals(0, q.size());
        }
        final ConcurrentQueueProducer<Integer> producer = q.producer();
        if (!(spec.isBounded()))
            return;

        int produced = 0;
        for (int i = 0; i < ((q.capacity()) - 10); i += 10) {
            produced += producer.produce(() -> (testCounter) < (SIZE) ? (testCounter)++ : null, 10);
            Assert.assertEquals((i + 10), produced);
        }
        produced += producer.produce(() -> (testCounter) < (SIZE) ? (testCounter)++ : null, ((q.capacity()) - produced));
        int size = testCounter;
        Assert.assertEquals(size, q.size());
        Assert.assertEquals(size, produced);
        if ((spec.ordering) != (Ordering.FIFO))
            return;

        int i = 0;
        // expect FIFO
        testCounter = 0;
        Integer e;
        while ((e = consumer.weakPoll()) != null) {
            Assert.assertEquals((size - (i + 1)), q.size());
            Assert.assertEquals(e.intValue(), (i++));
        } 
        Assert.assertEquals(size, i);
    }
}

