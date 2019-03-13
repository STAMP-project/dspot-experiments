package org.jctools.queues;


import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jctools.queues.matchers.Matchers;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.util.Pow2;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public abstract class QueueSanityTest {
    public static final int SIZE = 8192 * 2;

    static final int CONCURRENT_TEST_DURATION = 500;

    static final int TEST_TIMEOUT = 30000;

    protected final Queue<Integer> queue;

    protected final ConcurrentQueueSpec spec;

    public QueueSanityTest(ConcurrentQueueSpec spec, Queue<Integer> queue) {
        this.queue = queue;
        this.spec = spec;
    }

    @Test
    public void toStringWorks() {
        Assert.assertNotNull(queue.toString());
    }

    @Test
    public void sanity() {
        for (int i = 0; i < (QueueSanityTest.SIZE); i++) {
            Assert.assertNull(queue.poll());
            Assert.assertThat(queue, Matchers.emptyAndZeroSize());
        }
        int i = 0;
        while ((i < (QueueSanityTest.SIZE)) && (queue.offer(i))) {
            i++;
        } 
        int size = i;
        Assert.assertEquals(size, queue.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            i = 0;
            Integer p;
            Integer e;
            while ((p = queue.peek()) != null) {
                e = queue.poll();
                Assert.assertEquals(p, e);
                Assert.assertEquals((size - (i + 1)), queue.size());
                Assert.assertEquals((i++), e.intValue());
            } 
            Assert.assertEquals(size, i);
        } else {
            // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
            int sum = ((size - 1) * size) / 2;
            i = 0;
            Integer e;
            while ((e = queue.poll()) != null) {
                Assert.assertEquals((--size), queue.size());
                sum -= e;
            } 
            Assert.assertEquals(0, sum);
        }
        Assert.assertNull(queue.poll());
        Assert.assertThat(queue, Matchers.emptyAndZeroSize());
    }

    @Test
    public void testSizeIsTheNumberOfOffers() {
        int currentSize = 0;
        while ((currentSize < (QueueSanityTest.SIZE)) && (queue.offer(currentSize))) {
            currentSize++;
            Assert.assertThat(queue, hasSize(currentSize));
        } 
    }

    @Test
    public void whenFirstInThenFirstOut() {
        Assume.assumeThat(spec.ordering, is(Ordering.FIFO));
        // Arrange
        int i = 0;
        while ((i < (QueueSanityTest.SIZE)) && (queue.offer(i))) {
            i++;
        } 
        final int size = queue.size();
        // Act
        i = 0;
        Integer prev;
        while ((prev = queue.peek()) != null) {
            final Integer item = queue.poll();
            Assert.assertThat(item, is(prev));
            Assert.assertThat(queue, hasSize((size - (i + 1))));
            Assert.assertThat(item, is(i));
            i++;
        } 
        // Assert
        Assert.assertThat(i, is(size));
    }

    @Test
    public void test_FIFO_PRODUCER_Ordering() throws Exception {
        Assume.assumeThat(spec.ordering, is(Ordering.FIFO));
        // Arrange
        int i = 0;
        while ((i < (QueueSanityTest.SIZE)) && (queue.offer(i))) {
            i++;
        } 
        int size = queue.size();
        // Act
        // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
        int sum = ((size - 1) * size) / 2;
        Integer e;
        while ((e = queue.poll()) != null) {
            size--;
            Assert.assertThat(queue, hasSize(size));
            sum -= e;
        } 
        // Assert
        Assert.assertThat(sum, is(0));
    }

    @Test(expected = NullPointerException.class)
    public void offerNullResultsInNPE() {
        queue.offer(null);
    }

    @Test
    public void whenOfferItemAndPollItemThenSameInstanceReturnedAndQueueIsEmpty() {
        Assert.assertThat(queue, Matchers.emptyAndZeroSize());
        // Act
        final Integer e = new Integer(1876876);
        queue.offer(e);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertEquals(1, queue.size());
        final Integer oh = queue.poll();
        Assert.assertEquals(e, oh);
        // Assert
        Assert.assertThat(oh, sameInstance(e));
        Assert.assertThat(queue, Matchers.emptyAndZeroSize());
    }

    @Test
    public void testPowerOf2Capacity() {
        Assume.assumeThat(spec.isBounded(), is(true));
        int n = Pow2.roundToPowerOfTwo(spec.capacity);
        for (int i = 0; i < n; i++) {
            Assert.assertTrue(("Failed to insert:" + i), queue.offer(i));
        }
        Assert.assertFalse(queue.offer(n));
    }

    @Test(timeout = QueueSanityTest.TEST_TIMEOUT)
    public void testHappensBefore() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 1; i <= 10; i++) {
                        QueueSanityTest.Val v = new QueueSanityTest.Val();
                        v.value = i;
                        q.offer(v);
                    }
                    // slow down the producer, this will make the queue mostly empty encouraging visibility issues.
                    Thread.yield();
                } 
            }
        };
        Thread[] producers = producers(runnable);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 0; i < 10; i++) {
                        QueueSanityTest.Val v = ((QueueSanityTest.Val) (q.peek()));
                        if ((v != null) && ((v.value) == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        }
                        q.poll();
                    }
                } 
            }
        });
        startWaitJoin(stop, producers, consumer);
        Assert.assertEquals("reordering detected", 0, fail.value);
    }

    @Test(timeout = QueueSanityTest.TEST_TIMEOUT)
    public void testSize() throws Exception {
        final int capacity = ((spec.capacity) == 0) ? Integer.MAX_VALUE : spec.capacity;
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue<Integer> q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.offer(1);
                    q.poll();
                } 
            }
        };
        final Thread[] producersConsumers;
        if (!(spec.isMpmc())) {
            producersConsumers = new Thread[1];
        } else {
            producersConsumers = new Thread[(Runtime.getRuntime().availableProcessors()) - 1];
        }
        for (int i = 0; i < (producersConsumers.length); i++) {
            producersConsumers[i] = new Thread(runnable);
        }
        Thread observer = new Thread(new Runnable() {
            @Override
            public void run() {
                final int max = Math.min(producersConsumers.length, capacity);
                while (!(stop.get())) {
                    int size = q.size();
                    if ((size < 0) && (size > max)) {
                        (fail.value)++;
                    }
                } 
            }
        });
        startWaitJoin(stop, producersConsumers, observer);
        Assert.assertEquals("Unexpected size observed", 0, fail.value);
    }

    @Test(timeout = QueueSanityTest.TEST_TIMEOUT)
    public void testPollAfterIsEmpty() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final Queue<Integer> q = queue;
        final QueueSanityTest.Val fail = new QueueSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.offer(1);
                    // slow down the producer, this will make the queue mostly empty encouraging visibility issues.
                    Thread.yield();
                } 
            }
        };
        Thread[] producers = producers(runnable);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    if ((!(q.isEmpty())) && ((q.poll()) == null)) {
                        (fail.value)++;
                    }
                } 
            }
        });
        startWaitJoin(stop, producers, consumer);
        Assert.assertEquals("Observed no element in non-empty queue", 0, fail.value);
    }

    static final class Val {
        public int value;
    }
}

