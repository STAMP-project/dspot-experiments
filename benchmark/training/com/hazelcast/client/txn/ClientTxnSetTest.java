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
import com.hazelcast.core.ISet;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientTxnSetTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Test
    public void testAdd_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        Assert.assertTrue(txnSet.add(element));
        Assert.assertEquals(1, txnSet.size());
        context.commitTransaction();
    }

    @Test
    public void testSetSizeAfterAdd_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        txnSet.add(element);
        context.commitTransaction();
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testRemove_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        set.add(element);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        Assert.assertTrue(txnSet.remove(element));
        Assert.assertFalse(txnSet.remove("NOT_THERE"));
        context.commitTransaction();
    }

    @Test
    public void testSetSizeAfterRemove_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        set.add(element);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        txnSet.remove(element);
        context.commitTransaction();
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testAddDuplicateElement_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        Assert.assertTrue(txnSet.add(element));
        Assert.assertFalse(txnSet.add(element));
        context.commitTransaction();
        Assert.assertEquals(1, client.getSet(setName).size());
    }

    @Test
    public void testAddExistingElement_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        set.add(element);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        Assert.assertFalse(txnSet.add(element));
        context.commitTransaction();
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testSetSizeAfterAddingDuplicateElement_withinTxn() throws Exception {
        final String element = "item1";
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        set.add(element);
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> txnSet = context.getSet(setName);
        txnSet.add(element);
        context.commitTransaction();
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testAddRollBack() throws Exception {
        final String setName = HazelcastTestSupport.randomString();
        final ISet set = client.getSet(setName);
        set.add("item1");
        final TransactionContext context = client.newTransactionContext();
        context.beginTransaction();
        final TransactionalSet<Object> setTxn = context.getSet(setName);
        setTxn.add("item2");
        context.rollbackTransaction();
        Assert.assertEquals(1, set.size());
    }
}

