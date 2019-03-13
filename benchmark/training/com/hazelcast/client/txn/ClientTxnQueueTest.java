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
package com.hazelcast.client.txn;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientTxnQueueTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Test
    public void testTransactionalOfferPoll() {
        final String item = "offered";
        final String queueName = HazelcastTestSupport.randomString();
        final IQueue queue = client.getQueue(queueName);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue txnQueue = context.getQueue(queueName);
        txnQueue.offer(item);
        Assert.assertEquals(item, txnQueue.poll());
        context.commitTransaction();
    }

    @Test
    public void testQueueSizeAfterTxnOfferPoll() {
        final String item = "offered";
        final String queueName = HazelcastTestSupport.randomString();
        final IQueue queue = client.getQueue(queueName);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue txnQueue = context.getQueue(queueName);
        txnQueue.offer(item);
        txnQueue.poll();
        context.commitTransaction();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testTransactionalOfferTake() throws InterruptedException {
        final String item = "offered";
        final String queueName = HazelcastTestSupport.randomString();
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQueue = context.getQueue(queueName);
        Assert.assertTrue(txnQueue.offer(item));
        Assert.assertEquals(1, txnQueue.size());
        Assert.assertEquals(item, txnQueue.take());
        context.commitTransaction();
    }

    @Test
    public void testTransactionalQueueGetsOfferedItems_whenBlockedOnPoll() throws InterruptedException {
        final String item = "offered1";
        final String queueName = HazelcastTestSupport.randomString();
        final IQueue queue1 = client.getQueue(queueName);
        final CountDownLatch justBeforeBlocked = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    justBeforeBlocked.await();
                    HazelcastTestSupport.sleepSeconds(1);
                    queue1.offer(item);
                } catch (InterruptedException e) {
                    Assert.fail(("failed" + e));
                }
            }
        }.start();
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue txnQueue1 = context.getQueue(queueName);
        justBeforeBlocked.countDown();
        Object result = txnQueue1.poll(5, TimeUnit.SECONDS);
        Assert.assertEquals("TransactionalQueue while blocked in pol should get item offered from client queue", item, result);
        context.commitTransaction();
    }

    @Test
    public void testTransactionalPeek() {
        final String item = "offered";
        final String queunName = HazelcastTestSupport.randomString();
        final IQueue queue = client.getQueue(queunName);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue txnQueue = context.getQueue(queunName);
        txnQueue.offer(item);
        Assert.assertEquals(item, txnQueue.peek());
        Assert.assertEquals(item, txnQueue.peek());
        context.commitTransaction();
    }

    @Test
    public void testTransactionalOfferRoleBack() {
        final String name = HazelcastTestSupport.randomString();
        final IQueue queue = client.getQueue(name);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> qTxn = context.getQueue(name);
        qTxn.offer("ITEM");
        context.rollbackTransaction();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testTransactionalQueueSize() {
        final String item = "offered";
        final String name = HazelcastTestSupport.randomString();
        final IQueue queue = client.getQueue(name);
        queue.offer(item);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQueue = context.getQueue(name);
        txnQueue.offer(item);
        Assert.assertEquals(2, txnQueue.size());
        context.rollbackTransaction();
    }

    @Test
    public void testTransactionalOfferAndPollWithTimeout() throws InterruptedException {
        final String item = "offered";
        final String name = HazelcastTestSupport.randomString();
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        TransactionalQueue<String> txnQueue = context.getQueue(name);
        Assert.assertTrue(txnQueue.offer(item));
        Assert.assertEquals(1, txnQueue.size());
        Assert.assertEquals(item, txnQueue.poll(5, TimeUnit.SECONDS));
        context.commitTransaction();
    }
}

