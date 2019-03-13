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
package com.hazelcast.collection.impl.txnlist;


import TransactionOptions.TransactionType.TWO_PHASE;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.Member;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TransactionListTest extends HazelcastTestSupport {
    @Test
    public void testSingleListAtomicity() throws InterruptedException, ExecutionException {
        final int itemCount = 200;
        final HazelcastInstance instance = createHazelcastInstance();
        final String name = HazelcastTestSupport.randomString();
        Future<Integer> f = HazelcastTestSupport.spawn(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                IList<Object> set = instance.getList(name);
                while (!(set.remove("item-1"))) {
                } 
                return set.size();
            }
        });
        TransactionContext context = instance.newTransactionContext();
        context.beginTransaction();
        TransactionalList<Object> set = context.getList(name);
        for (int i = 0; i < itemCount; i++) {
            set.add(("item-" + i));
        }
        context.commitTransaction();
        int size = f.get();
        Assert.assertEquals((itemCount - 1), size);
    }

    @Test
    public void testOrder_WhenMultipleConcurrentTransactionRollback() throws InterruptedException {
        final HazelcastInstance instance = createHazelcastInstance();
        final String name = HazelcastTestSupport.randomString();
        IList<Integer> list = instance.getList(name);
        list.add(1);
        list.add(2);
        list.add(3);
        TransactionContext firstContext = instance.newTransactionContext();
        firstContext.beginTransaction();
        firstContext.getList(name).remove(1);
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                TransactionContext secondContext = instance.newTransactionContext();
                secondContext.beginTransaction();
                secondContext.getList(name).remove(2);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                secondContext.rollbackTransaction();
            }
        };
        thread.start();
        firstContext.rollbackTransaction();
        latch.countDown();
        thread.join();
        Assert.assertEquals(1, list.get(0).intValue());
        Assert.assertEquals(2, list.get(1).intValue());
        Assert.assertEquals(3, list.get(2).intValue());
    }

    @Test
    public void testAdd() {
        HazelcastInstance instance = createHazelcastInstance();
        String name = HazelcastTestSupport.randomString();
        String item = HazelcastTestSupport.randomString();
        IList<Object> list = instance.getList(name);
        TransactionContext context = instance.newTransactionContext();
        try {
            context.beginTransaction();
            TransactionalList<Object> txnList = context.getList(name);
            Assert.assertTrue(txnList.add(item));
            context.commitTransaction();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            context.rollbackTransaction();
        }
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(item, list.get(0));
    }

    @Test
    public void testRemove() {
        HazelcastInstance instance = createHazelcastInstance();
        String name = HazelcastTestSupport.randomString();
        String item = HazelcastTestSupport.randomString();
        IList<Object> list = instance.getList(name);
        list.add(item);
        TransactionContext context = instance.newTransactionContext();
        try {
            context.beginTransaction();
            TransactionalList<Object> txnList = context.getList(name);
            Assert.assertTrue(txnList.remove(item));
            context.commitTransaction();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            context.rollbackTransaction();
        }
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testRemove_withNotContainedItem() {
        HazelcastInstance instance = createHazelcastInstance();
        String name = HazelcastTestSupport.randomString();
        String item = HazelcastTestSupport.randomString();
        String notContainedItem = HazelcastTestSupport.randomString();
        IList<Object> list = instance.getList(name);
        list.add(item);
        TransactionContext context = instance.newTransactionContext();
        try {
            context.beginTransaction();
            TransactionalList<Object> txnList = context.getList(name);
            Assert.assertFalse(txnList.remove(notContainedItem));
            context.commitTransaction();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            context.rollbackTransaction();
        }
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testMigrationSerializationNotFails_whenTransactionsAreUsed() throws Exception {
        Config config = new Config();
        config.setProperty("hazelcast.partition.count", "2");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        String listName = HazelcastTestSupport.randomString();
        TransactionContext tr = instance1.newTransactionContext();
        tr.beginTransaction();
        TransactionalList<Object> list = tr.getList(listName);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        tr.commitTransaction();
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        Member owner = instance1.getPartitionService().getPartition(listName).getOwner();
        HazelcastInstance aliveInstance;
        if (instance1.getCluster().getLocalMember().equals(owner)) {
            instance1.shutdown();
            aliveInstance = instance2;
        } else {
            instance2.shutdown();
            aliveInstance = instance1;
        }
        IList<Object> l = aliveInstance.getList(listName);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, l.get(i));
        }
    }

    @Test
    public void transactionShouldBeRolledBack_whenInitiatorTerminatesBeforeCommit() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance master = factory.newHazelcastInstance();
        HazelcastInstance instance = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(instance);
        String name = HazelcastTestSupport.generateKeyOwnedBy(master);
        IList<Integer> list = master.getList(name);
        list.add(1);
        HazelcastTestSupport.waitAllForSafeState(master, instance);
        TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE);
        TransactionContext context = master.newTransactionContext(options);
        context.beginTransaction();
        TransactionalList<Integer> txList = context.getList(name);
        txList.remove(1);
        master.getLifecycleService().terminate();
        final IList<Integer> list2 = instance.getList(name);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, list2.size());
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, list2.size());
            }
        }, 3);
    }
}

