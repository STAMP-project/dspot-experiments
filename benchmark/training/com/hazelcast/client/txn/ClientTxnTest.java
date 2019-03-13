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
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientTxnTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private HazelcastInstance server;

    @Test
    public void testTxnRollback() throws Exception {
        final String queueName = randomString();
        final TransactionContext context = client.newTransactionContext();
        CountDownLatch txnRollbackLatch = new CountDownLatch(1);
        final CountDownLatch memberRemovedLatch = new CountDownLatch(1);
        client.getCluster().addMembershipListener(new MembershipAdapter() {
            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
                memberRemovedLatch.countDown();
            }
        });
        try {
            context.beginTransaction();
            Assert.assertNotNull(context.getTxnId());
            final TransactionalQueue queue = context.getQueue(queueName);
            queue.offer(randomString());
            server.shutdown();
            context.commitTransaction();
            Assert.fail("commit should throw exception!!!");
        } catch (TransactionException e) {
            context.rollbackTransaction();
            txnRollbackLatch.countDown();
        }
        assertOpenEventually(txnRollbackLatch);
        assertOpenEventually(memberRemovedLatch);
        final IQueue<Object> q = client.getQueue(queueName);
        Assert.assertNull(q.poll());
        Assert.assertEquals(0, q.size());
    }

    @Test
    public void testTxnRollbackOnServerCrash() throws Exception {
        final String queueName = randomString();
        final TransactionContext context = client.newTransactionContext();
        CountDownLatch txnRollbackLatch = new CountDownLatch(1);
        final CountDownLatch memberRemovedLatch = new CountDownLatch(1);
        context.beginTransaction();
        final TransactionalQueue queue = context.getQueue(queueName);
        queue.offer(randomString());
        client.getCluster().addMembershipListener(new MembershipAdapter() {
            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
                memberRemovedLatch.countDown();
            }
        });
        server.getLifecycleService().terminate();
        try {
            context.commitTransaction();
            Assert.fail("commit should throw exception !");
        } catch (TransactionException e) {
            context.rollbackTransaction();
            txnRollbackLatch.countDown();
        }
        assertOpenEventually(txnRollbackLatch);
        assertOpenEventually(memberRemovedLatch);
        final IQueue<Object> q = client.getQueue(queueName);
        Assert.assertNull(q.poll());
        Assert.assertEquals(0, q.size());
    }

    @Test
    public void testRollbackOnTimeout() {
        String name = randomString();
        IQueue<Object> queue = client.getQueue(name);
        queue.offer(randomString());
        TransactionOptions options = new TransactionOptions().setTimeout(3, TimeUnit.SECONDS);
        TransactionContext context = client.newTransactionContext(options);
        context.beginTransaction();
        try {
            try {
                context.getQueue(name).take();
            } catch (InterruptedException e) {
                Assert.fail();
            }
            sleepAtLeastSeconds(5);
            context.commitTransaction();
            Assert.fail();
        } catch (TransactionException e) {
            context.rollbackTransaction();
        }
        Assert.assertEquals("Queue size should be 1", 1, queue.size());
    }
}

