package org.I0Itec.zkclient;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;


public class DistributedQueueTest {
    private TestingServer _zkServer;

    private ZkClient _zkClient;

    @Test(timeout = 15000)
    public void testDistributedQueue() {
        _zkClient.createPersistent("/queue");
        DistributedQueue<Long> distributedQueue = new DistributedQueue<Long>(_zkClient, "/queue");
        distributedQueue.offer(17L);
        distributedQueue.offer(18L);
        distributedQueue.offer(19L);
        Assert.assertEquals(Long.valueOf(17L), distributedQueue.poll());
        Assert.assertEquals(Long.valueOf(18L), distributedQueue.poll());
        Assert.assertEquals(Long.valueOf(19L), distributedQueue.poll());
        Assert.assertNull(distributedQueue.poll());
    }

    @Test(timeout = 15000)
    public void testPeek() {
        _zkClient.createPersistent("/queue");
        DistributedQueue<Long> distributedQueue = new DistributedQueue<Long>(_zkClient, "/queue");
        distributedQueue.offer(17L);
        distributedQueue.offer(18L);
        Assert.assertEquals(Long.valueOf(17L), distributedQueue.peek());
        Assert.assertEquals(Long.valueOf(17L), distributedQueue.peek());
        Assert.assertEquals(Long.valueOf(17L), distributedQueue.poll());
        Assert.assertEquals(Long.valueOf(18L), distributedQueue.peek());
        Assert.assertEquals(Long.valueOf(18L), distributedQueue.poll());
        Assert.assertNull(distributedQueue.peek());
    }

    @Test(timeout = 30000)
    public void testMultipleReadingThreads() throws InterruptedException {
        _zkClient.createPersistent("/queue");
        final DistributedQueue<Long> distributedQueue = new DistributedQueue<Long>(_zkClient, "/queue");
        // insert 100 elements
        for (int i = 0; i < 100; i++) {
            distributedQueue.offer(new Long(i));
        }
        // 3 reading threads
        final Set<Long> readElements = Collections.synchronizedSet(new HashSet<Long>());
        List<Thread> threads = new ArrayList<Thread>();
        final List<Exception> exceptions = new Vector<Exception>();
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Long value = distributedQueue.poll();
                            if (value == null) {
                                return;
                            }
                            readElements.add(value);
                        } 
                    } catch (Exception e) {
                        exceptions.add(e);
                        e.printStackTrace();
                    }
                }
            };
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertEquals(0, exceptions.size());
        Assert.assertEquals(100, readElements.size());
    }
}

