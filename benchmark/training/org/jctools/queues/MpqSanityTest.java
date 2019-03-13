package org.jctools.queues;


import Ordering.FIFO;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.jctools.queues.spec.Ordering;
import org.jctools.util.Pow2;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public abstract class MpqSanityTest {
    public static final int SIZE = 8192 * 2;

    static final int CONCURRENT_TEST_DURATION = 500;

    static final int TEST_TIMEOUT = 30000;

    private final MessagePassingQueue<Integer> queue;

    private final ConcurrentQueueSpec spec;

    int count = 0;

    Integer p;

    public MpqSanityTest(ConcurrentQueueSpec spec, MessagePassingQueue<Integer> queue) {
        this.queue = queue;
        this.spec = spec;
    }

    @Test(expected = NullPointerException.class)
    public void relaxedOfferNullResultsInNPE() {
        queue.relaxedOffer(null);
    }

    @Test
    public void sanity() {
        for (int i = 0; i < (MpqSanityTest.SIZE); i++) {
            Assert.assertNull(queue.relaxedPoll());
            Assert.assertTrue(queue.isEmpty());
            Assert.assertTrue(((queue.size()) == 0));
        }
        int i = 0;
        while ((i < (MpqSanityTest.SIZE)) && (queue.relaxedOffer(i))) {
            i++;
        } 
        int size = i;
        Assert.assertEquals(size, queue.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            i = 0;
            Integer p;
            Integer e;
            while ((p = queue.relaxedPeek()) != null) {
                e = queue.relaxedPoll();
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
            while ((e = queue.relaxedPoll()) != null) {
                Assert.assertEquals((--size), queue.size());
                sum -= e;
            } 
            Assert.assertEquals(0, sum);
        }
    }

    int sum;

    @Test
    public void sanityDrainBatch() {
        Assert.assertEquals(0, queue.drain(( e) -> {
        }, MpqSanityTest.SIZE));
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(((queue.size()) == 0));
        count = 0;
        sum = 0;
        int i = queue.fill(() -> {
            final int val = (count)++;
            sum += val;
            return val;
        }, MpqSanityTest.SIZE);
        final int size = i;
        Assert.assertEquals(size, queue.size());
        if ((spec.ordering) == (Ordering.FIFO)) {
            // expect FIFO
            count = 0;
            int drainCount = 0;
            i = 0;
            do {
                i += drainCount = queue.drain(( e) -> {
                    assertEquals(((count)++), e.intValue());
                });
            } while (drainCount != 0 );
            Assert.assertEquals(size, i);
            Assert.assertTrue(queue.isEmpty());
            Assert.assertTrue(((queue.size()) == 0));
        } else {
            int drainCount = 0;
            i = 0;
            do {
                i += drainCount = queue.drain(( e) -> {
                    sum -= e.intValue();
                });
            } while (drainCount != 0 );
            Assert.assertEquals(size, i);
            Assert.assertTrue(queue.isEmpty());
            Assert.assertTrue(((queue.size()) == 0));
            Assert.assertEquals(0, sum);
        }
    }

    @Test
    public void testSizeIsTheNumberOfOffers() {
        int currentSize = 0;
        while ((currentSize < (MpqSanityTest.SIZE)) && (queue.relaxedOffer(currentSize))) {
            currentSize++;
            Assert.assertFalse(queue.isEmpty());
            Assert.assertTrue(((queue.size()) == currentSize));
        } 
        if (spec.isBounded()) {
            Assert.assertEquals(spec.capacity, currentSize);
        } else {
            Assert.assertEquals(MpqSanityTest.SIZE, currentSize);
        }
    }

    @Test
    public void supplyMessageUntilFull() {
        Assume.assumeThat(spec.isBounded(), Matchers.is(Boolean.TRUE));
        final MpqSanityTest.Val instances = new MpqSanityTest.Val();
        instances.value = 0;
        final MessagePassingQueue.Supplier<Integer> messageFactory = () -> instances.value++;
        final int capacity = queue.capacity();
        int filled = 0;
        while (filled < capacity) {
            filled += queue.fill(messageFactory, (capacity - filled));
        } 
        Assert.assertEquals(instances.value, capacity);
        final int noItems = queue.fill(messageFactory, 1);
        Assert.assertEquals(noItems, 0);
        Assert.assertEquals(instances.value, capacity);
    }

    @Test
    public void whenFirstInThenFirstOut() {
        Assume.assumeThat(spec.ordering, Matchers.is(FIFO));
        // Arrange
        int i = 0;
        while ((i < (MpqSanityTest.SIZE)) && (queue.relaxedOffer(i))) {
            i++;
        } 
        final int size = queue.size();
        // Act
        i = 0;
        Integer prev;
        while ((prev = queue.relaxedPeek()) != null) {
            final Integer item = queue.relaxedPoll();
            Assert.assertThat(item, Matchers.is(prev));
            Assert.assertEquals((size - (i + 1)), queue.size());
            Assert.assertThat(item, Matchers.is(i));
            i++;
        } 
        // Assert
        Assert.assertThat(i, Matchers.is(size));
    }

    @Test
    public void test_FIFO_PRODUCER_Ordering() throws Exception {
        Assume.assumeThat(spec.ordering, Matchers.is(FIFO));
        // Arrange
        int i = 0;
        while ((i < (MpqSanityTest.SIZE)) && (queue.relaxedOffer(i))) {
            i++;
        } 
        int size = queue.size();
        // Act
        // expect sum of elements is (size - 1) * size / 2 = 0 + 1 + .... + (size - 1)
        int sum = ((size - 1) * size) / 2;
        Integer e;
        while ((e = queue.relaxedPoll()) != null) {
            size--;
            Assert.assertEquals(size, queue.size());
            sum -= e;
        } 
        // Assert
        Assert.assertThat(sum, Matchers.is(0));
    }

    @Test
    public void whenOfferItemAndPollItemThenSameInstanceReturnedAndQueueIsEmpty() {
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(((queue.size()) == 0));
        // Act
        final Integer e = new Integer(1876876);
        queue.relaxedOffer(e);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertEquals(1, queue.size());
        final Integer oh = queue.relaxedPoll();
        Assert.assertEquals(e, oh);
        // Assert
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(((queue.size()) == 0));
    }

    @Test
    public void testPowerOf2Capacity() {
        Assume.assumeThat(spec.isBounded(), Matchers.is(true));
        int n = Pow2.roundToPowerOfTwo(spec.capacity);
        for (int i = 0; i < n; i++) {
            Assert.assertTrue(("Failed to insert:" + i), queue.relaxedOffer(i));
        }
        Assert.assertFalse(queue.relaxedOffer(n));
    }

    @Test(timeout = MpqSanityTest.TEST_TIMEOUT)
    public void testHappensBefore() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final MessagePassingQueue q = queue;
        final MpqSanityTest.Val fail = new MpqSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 1; i <= 10; i++) {
                        MpqSanityTest.Val v = new MpqSanityTest.Val();
                        v.value = i;
                        q.relaxedOffer(v);
                    }
                    // slow down the producer, this will make the queue mostly empty encouraging visibility
                    // issues.
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
                        MpqSanityTest.Val v1 = ((MpqSanityTest.Val) (q.relaxedPeek()));
                        if ((v1 != null) && ((v1.value) == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        } else {
                            continue;
                        }
                        MpqSanityTest.Val v2 = ((MpqSanityTest.Val) (q.relaxedPoll()));
                        if ((v2 == null) || (v1 != v2)) {
                            fail.value = 2;
                            stop.set(true);
                        }
                    }
                } 
            }
        });
        stopAll(stop, producers, consumer);
        Assert.assertEquals("reordering detected", 0, fail.value);
    }

    @Test(timeout = MpqSanityTest.TEST_TIMEOUT)
    public void testHappensBeforePerpetualDrain() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final MessagePassingQueue q = queue;
        final MpqSanityTest.Val fail = new MpqSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 1; i <= 10; i++) {
                        MpqSanityTest.Val v = new MpqSanityTest.Val();
                        v.value = i;
                        q.relaxedOffer(v);
                    }
                    // slow down the producer, this will make the queue mostly empty encouraging visibility
                    // issues.
                    Thread.yield();
                } 
            }
        };
        Thread[] producers = producers(runnable);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.drain(( e) -> {
                        org.jctools.queues.Val v = ((org.jctools.queues.Val) (e));
                        if ((v != null) && (v.value == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        }
                        if (v == null) {
                            fail.value = 1;
                            stop.set(true);
                            System.out.println("Unexpected: v == null");
                        }
                    }, ( idle) -> {
                        return idle;
                    }, () -> {
                        return !(stop.get());
                    });
                } 
            }
        });
        stopAll(stop, producers, consumer);
        Assert.assertEquals("reordering detected", 0, fail.value);
    }

    @Test(timeout = MpqSanityTest.TEST_TIMEOUT)
    public void testHappensBeforePerpetualFill() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final MessagePassingQueue q = queue;
        final MpqSanityTest.Val fail = new MpqSanityTest.Val();
        final Runnable runnable = new Runnable() {
            int counter;

            @Override
            public void run() {
                counter = 1;
                q.fill(() -> {
                    org.jctools.queues.Val v = new org.jctools.queues.Val();
                    v.value = 1 + (((counter)++) % 10);
                    return v;
                }, ( e) -> {
                    return e;
                }, () -> {
                    // slow down the producer, this will make the queue mostly empty encouraging visibility
                    // issues.
                    Thread.yield();
                    return !(stop.get());
                });
            }
        };
        Thread[] producers = producers(runnable);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    for (int i = 0; i < 10; i++) {
                        MpqSanityTest.Val v1 = ((MpqSanityTest.Val) (q.relaxedPeek()));
                        int r;
                        if ((v1 != null) && ((r = v1.value) == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        } else {
                            continue;
                        }
                        MpqSanityTest.Val v2 = ((MpqSanityTest.Val) (q.relaxedPoll()));
                        if ((v2 == null) || (v1 != v2)) {
                            fail.value = 1;
                            stop.set(true);
                        }
                    }
                } 
            }
        });
        stopAll(stop, producers, consumer);
        Assert.assertEquals("reordering detected", 0, fail.value);
    }

    @Test(timeout = MpqSanityTest.TEST_TIMEOUT)
    public void testHappensBeforePerpetualFillDrain() throws Exception {
        final AtomicBoolean stop = new AtomicBoolean();
        final MessagePassingQueue q = queue;
        final MpqSanityTest.Val fail = new MpqSanityTest.Val();
        final Runnable runnable = new Runnable() {
            int counter;

            @Override
            public void run() {
                counter = 1;
                q.fill(() -> {
                    org.jctools.queues.Val v = new org.jctools.queues.Val();
                    v.value = 1 + (((counter)++) % 10);
                    return v;
                }, ( e) -> {
                    return e;
                }, () -> {
                    // slow down the producer, this will make the queue mostly empty encouraging
                    // visibility issues.
                    Thread.yield();
                    return !(stop.get());
                });
            }
        };
        Thread[] producers = producers(runnable);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.drain(( e) -> {
                        org.jctools.queues.Val v = ((org.jctools.queues.Val) (e));
                        if ((v != null) && (v.value == 0)) {
                            fail.value = 1;
                            stop.set(true);
                        }
                        if (v == null) {
                            fail.value = 1;
                            stop.set(true);
                            System.out.println("Unexpected: v == null");
                        }
                    }, ( idle) -> {
                        return idle;
                    }, () -> {
                        return !(stop.get());
                    });
                } 
            }
        });
        stopAll(stop, producers, consumer);
        Assert.assertEquals("reordering detected", 0, fail.value);
        queue.clear();
    }

    @Test(timeout = MpqSanityTest.TEST_TIMEOUT)
    public void testRelaxedOfferPollObservedSize() throws Exception {
        final int capacity = ((spec.capacity) == 0) ? Integer.MAX_VALUE : spec.capacity;
        final AtomicBoolean stop = new AtomicBoolean();
        final MessagePassingQueue<Integer> q = queue;
        final MpqSanityTest.Val fail = new MpqSanityTest.Val();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!(stop.get())) {
                    q.relaxedOffer(1);
                    q.relaxedPoll();
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
        stopAll(stop, producersConsumers, observer);
        Assert.assertEquals("Unexpected size observed", 0, fail.value);
    }

    static final class Val {
        public int value;
    }
}

