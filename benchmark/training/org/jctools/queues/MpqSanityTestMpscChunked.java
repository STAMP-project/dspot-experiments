package org.jctools.queues;


import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MpqSanityTestMpscChunked extends MpqSanityTest {
    public MpqSanityTestMpscChunked(ConcurrentQueueSpec spec, MessagePassingQueue<Integer> queue) {
        super(spec, queue);
    }

    @Test
    public void testMaxSizeQueue() {
        MpscChunkedArrayQueue queue = new MpscChunkedArrayQueue<Object>(1024, ((1000 * 1024) * 1024));
        for (int i = 0; i < 400001; i++) {
            queue.offer(i);
        }
    }
}

