/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.collection.impl.queue;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueueTestsFrom2X extends HazelcastTestSupport {
    @Test
    public void testQueueItemListener() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<String> queue = instance.getQueue("testQueueItemListener");
        final String value = "hello";
        final CountDownLatch latch = new CountDownLatch(8);
        queue.addItemListener(new com.hazelcast.core.ItemListener<String>() {
            public void itemAdded(ItemEvent<String> itemEvent) {
                Assert.assertEquals(value, itemEvent.getItem());
                latch.countDown();
            }

            public void itemRemoved(ItemEvent<String> itemEvent) {
                Assert.assertEquals(value, itemEvent.getItem());
                latch.countDown();
            }
        }, true);
        queue.offer(value);
        Assert.assertEquals(value, queue.poll());
        queue.offer(value);
        Assert.assertTrue(queue.remove(value));
        queue.add(value);
        Assert.assertEquals(value, queue.remove());
        queue.put(value);
        Assert.assertEquals(value, queue.take());
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testQueueAddAll() {
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<String> queue = instance.getQueue("testQueueAddAll");
        String[] items = new String[]{ "one", "two", "three", "four" };
        queue.addAll(Arrays.asList(items));
        Assert.assertEquals(4, queue.size());
        queue.addAll(Arrays.asList(items));
        Assert.assertEquals(8, queue.size());
    }

    @Test
    public void testQueueContains() {
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<String> queue = instance.getQueue("testQueueContains");
        String[] items = new String[]{ "one", "two", "three", "four" };
        queue.addAll(Arrays.asList(items));
        HazelcastTestSupport.assertContains(queue, "one");
        HazelcastTestSupport.assertContains(queue, "two");
        HazelcastTestSupport.assertContains(queue, "three");
        HazelcastTestSupport.assertContains(queue, "four");
    }

    @Test
    public void testQueueContainsAll() {
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<String> queue = instance.getQueue("testQueueContainsAll");
        List<String> list = Arrays.asList("one", "two", "three", "four");
        queue.addAll(list);
        HazelcastTestSupport.assertContainsAll(queue, list);
    }

    @Test
    public void testQueueRemove() {
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<String> q = instance.getQueue("testQueueRemove");
        for (int i = 0; i < 10; i++) {
            q.offer(("item" + i));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertNotNull(q.poll());
        }
        Assert.assertEquals("item5", q.peek());
        boolean removed = q.remove("item5");
        Assert.assertTrue(removed);
        Iterator<String> it = q.iterator();
        int i = 6;
        while (it.hasNext()) {
            String o = it.next();
            String expectedValue = "item" + (i++);
            Assert.assertEquals(o, expectedValue);
        } 
        Assert.assertEquals(4, q.size());
    }

    @Test
    public void issue370() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance h1 = factory.newHazelcastInstance(getConfig());
        final HazelcastInstance h2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(h1, h2);
        final Queue<String> q1 = h1.getQueue("q");
        final Queue<String> q2 = h2.getQueue("q");
        for (int i = 0; i < 5; i++) {
            q1.offer(("item" + i));
        }
        Assert.assertEquals(5, q1.size());
        Assert.assertEquals(5, q2.size());
        Assert.assertEquals("item0", q2.poll());
        Assert.assertEquals("item1", q2.poll());
        Assert.assertEquals("item2", q2.poll());
        Assert.assertEquals(2, q1.size());
        Assert.assertEquals(2, q2.size());
        h1.shutdown();
        Assert.assertEquals(2, q2.size());
        final HazelcastInstance h3 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(h2, h3);
        final Queue<String> q3 = h3.getQueue("q");
        Assert.assertEquals(2, q2.size());
        Assert.assertEquals(2, q3.size());
        h2.shutdown();
        Assert.assertEquals(2, q3.size());
    }

    @Test
    public void issue391() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final int total = 10;
        final Collection<Integer> results = new ArrayList<Integer>(5);
        final HazelcastInstance hz1 = factory.newHazelcastInstance(getConfig());
        final CountDownLatch latchOffer = new CountDownLatch(1);
        final CountDownLatch latchTake = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < total; i++) {
                        results.add(((Integer) (hz1.getQueue("q").take())));
                    }
                    latchTake.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        final HazelcastInstance hz2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2);
        HazelcastTestSupport.spawn(new Runnable() {
            public void run() {
                for (int i = 0; i < total; i++) {
                    hz2.getQueue("q").offer(i);
                }
                latchOffer.countDown();
            }
        });
        Assert.assertTrue(latchOffer.await(100, TimeUnit.SECONDS));
        Assert.assertTrue(latchTake.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(hz1.getQueue("q").isEmpty());
        hz1.shutdown();
        Assert.assertTrue(hz2.getQueue("q").isEmpty());
        final Object[] objects = new Object[total];
        for (int i = 0; i < total; i++) {
            objects[i] = i;
        }
        Assert.assertArrayEquals(objects, results.toArray());
    }

    @Test
    public void issue427QOfferIncorrectWithinTransaction() {
        Config config = new Config();
        config.getQueueConfig("default").setMaxSize(100);
        HazelcastInstance instance = createHazelcastInstance(config);
        TransactionContext transactionContext = instance.newTransactionContext();
        transactionContext.beginTransaction();
        TransactionalQueue<Integer> queue = transactionContext.getQueue("default");
        for (int i = 0; i < 100; i++) {
            queue.offer(i);
        }
        boolean result = queue.offer(100);
        Assert.assertEquals(100, queue.size());
        transactionContext.commitTransaction();
        Assert.assertEquals(100, instance.getQueue("default").size());
        Assert.assertFalse(result);
        instance.shutdown();
    }

    @Test
    public void testListenerLifecycle() throws Exception {
        long sleep = 2000;
        String name = "listenerLifecycle";
        HazelcastInstance instance = createHazelcastInstance();
        IQueue<Integer> queue = instance.getQueue(name);
        try {
            final CountDownLatch latch = new CountDownLatch(3);
            com.hazelcast.core.ItemListener<Integer> listener = new com.hazelcast.core.ItemListener<Integer>() {
                public void itemAdded(ItemEvent<Integer> itemEvent) {
                    latch.countDown();
                }

                public void itemRemoved(ItemEvent<Integer> itemEvent) {
                }
            };
            queue.addItemListener(listener, false);
            queue.offer(1);
            Thread.sleep(sleep);
            queue.destroy();
            queue = instance.getQueue(name);
            String id = queue.addItemListener(listener, false);
            queue.offer(2);
            Thread.sleep(sleep);
            queue.removeItemListener(id);
            queue.offer(3);
            Thread.sleep(sleep);
            Assert.assertEquals(1, latch.getCount());
            latch.countDown();
            Assert.assertTrue(("Remaining: " + (latch.getCount())), latch.await(3, TimeUnit.SECONDS));
        } finally {
            queue.destroy();
        }
    }

    @Test
    public void testQueueOfferCommitSize() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(getConfig());
        HazelcastInstance instance2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        TransactionContext context = instance1.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQ1 = context.getQueue("testQueueOfferCommitSize");
        TransactionalQueue<String> txnQ2 = context.getQueue("testQueueOfferCommitSize");
        txnQ1.offer("item");
        Assert.assertEquals(1, txnQ1.size());
        Assert.assertEquals(1, txnQ2.size());
        context.commitTransaction();
        Assert.assertEquals(1, instance1.getQueue("testQueueOfferCommitSize").size());
        Assert.assertEquals(1, instance2.getQueue("testQueueOfferCommitSize").size());
        Assert.assertEquals("item", instance2.getQueue("testQueueOfferCommitSize").poll());
    }

    @Test
    public void testQueueOfferRollbackSize() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(getConfig());
        HazelcastInstance instance2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        TransactionContext context = instance1.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQ1 = context.getQueue("testQueueOfferRollbackSize");
        TransactionalQueue<String> txnQ2 = context.getQueue("testQueueOfferRollbackSize");
        txnQ1.offer("item");
        Assert.assertEquals(1, txnQ1.size());
        Assert.assertEquals(1, txnQ2.size());
        context.rollbackTransaction();
        Assert.assertEquals(0, instance1.getQueue("testQueueOfferRollbackSize").size());
        Assert.assertEquals(0, instance2.getQueue("testQueueOfferRollbackSize").size());
    }

    @Test
    public void testQueuePollCommitSize() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(getConfig());
        HazelcastInstance instance2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        TransactionContext context = instance1.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQ1 = context.getQueue("testQueuePollCommitSize");
        TransactionalQueue<String> txnQ2 = context.getQueue("testQueuePollCommitSize");
        txnQ1.offer("item1");
        txnQ1.offer("item2");
        Assert.assertEquals(2, txnQ1.size());
        Assert.assertEquals(2, txnQ2.size());
        Assert.assertEquals("item1", txnQ1.poll());
        Assert.assertEquals(1, txnQ1.size());
        Assert.assertEquals(1, txnQ2.size());
        context.commitTransaction();
        Assert.assertEquals(1, instance1.getQueue("testQueuePollCommitSize").size());
        Assert.assertEquals(1, instance2.getQueue("testQueuePollCommitSize").size());
        Assert.assertEquals("item2", instance1.getQueue("testQueuePollCommitSize").poll());
        Assert.assertEquals(0, instance1.getQueue("testQueuePollCommitSize").size());
    }

    @Test
    public void testQueuePollRollbackSize() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance1 = factory.newHazelcastInstance(getConfig());
        final HazelcastInstance instance2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        TransactionContext context = instance1.newTransactionContext();
        IQueue<Object> queue = instance1.getQueue("testQueuePollRollbackSize");
        queue.offer("item1");
        queue.offer("item2");
        Assert.assertEquals(2, queue.size());
        context.beginTransaction();
        TransactionalQueue txnQ1 = context.getQueue("testQueuePollRollbackSize");
        Assert.assertEquals("item1", txnQ1.poll());
        Assert.assertEquals(1, txnQ1.size());
        Assert.assertEquals(1, queue.size());
        context.rollbackTransaction();
        Assert.assertEquals(2, queue.size());
        Assert.assertEquals("item1", queue.poll());
        Assert.assertEquals("item2", queue.poll());
    }

    @Test
    public void testQueueOrderAfterPollRollback() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(getConfig());
        HazelcastInstance instance2 = factory.newHazelcastInstance(getConfig());
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        TransactionContext context = instance1.newTransactionContext();
        IQueue<Integer> queue = instance1.getQueue("testQueueOrderAfterPollRollback");
        context.beginTransaction();
        TransactionalQueue<Integer> txn1 = context.getQueue("testQueueOrderAfterPollRollback");
        txn1.offer(1);
        txn1.offer(2);
        txn1.offer(3);
        context.commitTransaction();
        Assert.assertEquals(3, queue.size());
        TransactionContext context2 = instance2.newTransactionContext();
        context2.beginTransaction();
        TransactionalQueue<Integer> txn2 = context2.getQueue("testQueueOrderAfterPollRollback");
        Assert.assertEquals(1, txn2.poll().intValue());
        context2.rollbackTransaction();
        Assert.assertEquals(1, queue.poll().intValue());
        Assert.assertEquals(2, queue.poll().intValue());
        Assert.assertEquals(3, queue.poll().intValue());
    }

    /**
     * Github issue #99
     */
    @Test
    public void issue99TestQueueTakeAndDuringRollback() throws Exception {
        final String name = "issue99TestQueueTakeAndDuringRollback";
        final HazelcastInstance hz = createHazelcastInstance();
        IQueue<String> q = hz.getQueue(name);
        q.offer("item");
        Thread t1 = new Thread() {
            public void run() {
                TransactionContext context = hz.newTransactionContext();
                try {
                    context.beginTransaction();
                    context.getQueue(name).poll(1, TimeUnit.DAYS);
                    Thread.sleep(1000);
                    throw new RuntimeException();
                } catch (InterruptedException e) {
                    Assert.fail(e.getMessage());
                } catch (Exception e) {
                    context.rollbackTransaction();
                }
            }
        };
        final AtomicBoolean fail = new AtomicBoolean(false);
        Thread t2 = new Thread() {
            public void run() {
                TransactionContext context = hz.newTransactionContext();
                try {
                    context.beginTransaction();
                    context.getQueue(name).poll(1, TimeUnit.DAYS);
                    context.commitTransaction();
                    fail.set(false);
                } catch (Exception e) {
                    context.rollbackTransaction();
                    e.printStackTrace();
                    fail.set(true);
                }
            }
        };
        t1.start();
        Thread.sleep(500);
        t2.start();
        t2.join();
        Assert.assertFalse("Queue take failed after rollback!", fail.get());
    }

    /**
     * Github issue #114
     */
    @Test
    public void issue114TestQueueListenersUnderTransaction() throws Exception {
        String name = "issue99TestQueueTakeAndDuringRollback";
        HazelcastInstance hz = createHazelcastInstance();
        IQueue<String> testQueue = hz.getQueue(name);
        final CountDownLatch offerLatch = new CountDownLatch(2);
        final CountDownLatch pollLatch = new CountDownLatch(2);
        testQueue.addItemListener(new com.hazelcast.core.ItemListener<String>() {
            public void itemAdded(ItemEvent<String> item) {
                offerLatch.countDown();
            }

            public void itemRemoved(ItemEvent<String> item) {
                pollLatch.countDown();
            }
        }, true);
        TransactionContext context = hz.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<Object> queue = context.getQueue(name);
        queue.offer("tx Hello");
        queue.offer("tx World");
        context.commitTransaction();
        TransactionContext context2 = hz.newTransactionContext();
        context2.beginTransaction();
        TransactionalQueue<Object> queue2 = context2.getQueue(name);
        Assert.assertEquals("tx Hello", queue2.poll());
        Assert.assertEquals("tx World", queue2.poll());
        context2.commitTransaction();
        Assert.assertTrue(("Remaining offer listener count: " + (offerLatch.getCount())), offerLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(("Remaining poll listener count: " + (pollLatch.getCount())), pollLatch.await(2, TimeUnit.SECONDS));
    }
}

