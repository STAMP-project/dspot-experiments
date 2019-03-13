package org.jctools.queues;


import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MpqSanityTestSpscChunked extends MpqSanityTest {
    public MpqSanityTestSpscChunked(ConcurrentQueueSpec spec, MessagePassingQueue<Integer> queue) {
        super(spec, queue);
    }

    @Test
    public void testMaxSizeQueue() {
        SpscChunkedArrayQueue queue = new SpscChunkedArrayQueue<Object>(1024, ((1000 * 1024) * 1024));
        for (int i = 0; i < 400001; i++) {
            queue.offer(i);
        }
    }
}

