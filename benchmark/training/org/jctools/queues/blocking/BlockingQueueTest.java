package org.jctools.queues.blocking;


import java.util.concurrent.BlockingQueue;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BlockingQueueTest {
    protected static final int CAPACITY = 32768;// better to have a size power of 2 to test boundaries


    private BlockingQueue<Integer> q;

    private final ConcurrentQueueSpec spec;

    public BlockingQueueTest(ConcurrentQueueSpec spec) {
        this.spec = spec;
    }

    @Test
    public void testOffer() {
        for (int i = 0; i < (BlockingQueueTest.CAPACITY); i++) {
            Assert.assertTrue(q.offer(i));
            Assert.assertEquals((i + 1), q.size());
        }
        if (spec.isBounded()) {
            Assert.assertFalse(q.offer(0));
        }
        Assert.assertEquals(BlockingQueueTest.CAPACITY, q.size());
        Assert.assertFalse(q.isEmpty());
    }

    @Test
    public void testPoll() {
        testOffer();
        Assert.assertEquals(BlockingQueueTest.CAPACITY, q.size());
        for (int i = 0; i < (BlockingQueueTest.CAPACITY); i++) {
            Assert.assertEquals(q.poll(), new Integer(i));
            Assert.assertEquals(((BlockingQueueTest.CAPACITY) - (i + 1)), q.size());
        }
        Assert.assertNull(q.poll());
        Assert.assertEquals(q.size(), 0);
        Assert.assertTrue(q.isEmpty());
    }

    @Test
    public void testEmptyQueue() {
        Assert.assertNull(q.poll());
        Assert.assertEquals(q.size(), 0);
        Assert.assertTrue(q.isEmpty());
    }

    @Test
    public void testClear() {
        testOffer();
        q.clear();
        testEmptyQueue();
    }

    @Test
    public void testConcurrentOfferPool() throws Exception {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < (BlockingQueueTest.CAPACITY);) {
                    Integer r = q.poll();
                    if (r != null) {
                        Assert.assertEquals(r, new Integer(i));
                        i++;
                    }
                }
            }
        }.start();
        // Wait for the thread to warmup
        Thread.sleep(100);
        // Try to insert to max
        for (int i = 0; i < (BlockingQueueTest.CAPACITY); i++) {
            Assert.assertTrue(q.offer(i));
        }
        while (!(q.isEmpty())) {
            Thread.sleep(1);
        } 
    }

    @Test
    public void testNonBlockingPutTake() throws Exception {
        q.put(42);
        Integer i = q.take();
        Assert.assertNotNull(i);
        Assert.assertEquals(i, new Integer(42));
    }

    @Test
    public void testBlockingTake() throws Exception {
        q.put(0);
        Thread take = new Thread() {
            @Override
            public void run() {
                try {
                    Assert.assertEquals(new Integer(0), q.take());
                    Assert.assertEquals(new Integer(1), q.take());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        take.start();
        // Wait for the thread to read 0
        int timeout = 30;// timeout ~3s

        while ((!(q.isEmpty())) && (timeout > 0)) {
            Thread.sleep(100);
            timeout--;
        } 
        Assert.assertTrue((timeout > 0));
        Assert.assertTrue(take.isAlive());
        q.put(1);
        // take thread should be unlocked
        take.join(3000);
        Assert.assertFalse(take.isAlive());
    }

    @Test
    public void testBlockingPut() throws Exception {
        if (!(spec.isBounded())) {
            // Unbounded queues don't block on put()
            return;
        }
        // Fill the queue
        testOffer();
        Thread put = new Thread() {
            @Override
            public void run() {
                try {
                    // this should block
                    q.put((-1));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        put.start();
        Thread.sleep(100);
        Assert.assertTrue(put.isAlive());
        for (int i = 0; i < (BlockingQueueTest.CAPACITY); i++) {
            q.take();
        }
        // put(-1) have unlocked
        Assert.assertEquals(new Integer((-1)), q.take());
        // put thread should be unlocked
        put.join(3000);
        Assert.assertFalse(put.isAlive());
    }
}

