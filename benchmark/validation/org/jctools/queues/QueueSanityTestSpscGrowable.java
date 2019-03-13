package org.jctools.queues;


import java.util.Queue;
import org.hamcrest.Matchers;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class QueueSanityTestSpscGrowable extends QueueSanityTest {
    public QueueSanityTestSpscGrowable(ConcurrentQueueSpec spec, Queue<Integer> queue) {
        super(spec, queue);
    }

    @Test
    public void testSizeNeverExceedCapacity() {
        final SpscGrowableArrayQueue<Integer> q = new SpscGrowableArrayQueue(8, 16);
        final Integer v = 0;
        final int capacity = q.capacity();
        for (int i = 0; i < capacity; i++) {
            Assert.assertTrue(q.offer(v));
        }
        Assert.assertFalse(q.offer(v));
        Assert.assertThat(q.size(), Matchers.is(capacity));
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals(v, q.poll());
        }
        // the consumer is left in the chunk previous the last and biggest one
        Assert.assertThat(q.size(), Matchers.is((capacity - 6)));
        for (int i = 0; i < 6; i++) {
            q.offer(v);
        }
        Assert.assertThat(q.size(), Matchers.is(capacity));
        Assert.assertFalse(q.offer(v));
    }
}

