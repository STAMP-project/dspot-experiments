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
package com.hazelcast.quorum.queue;


import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TransactionalQueueQuorumWriteTest extends AbstractQuorumTest {
    @Parameterized.Parameter(0)
    public static TransactionOptions options;

    @Parameterized.Parameter(1)
    public static QuorumType quorumType;

    @Test
    public void offer_quorum() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.offer("object");
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void offer_noQuorum() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.offer("object");
        transactionContext.commitTransaction();
    }

    @Test
    public void offerTimeout_quorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.offer("object", 10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void offerTimeout_noQuorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.offer("object", 10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test
    public void poll_quorum() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.poll();
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void poll_noQuorum() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.poll();
        transactionContext.commitTransaction();
    }

    @Test
    public void pollTimeout_quorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.poll(10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void pollTimeout_noQuorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.poll(10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test
    public void take_quorum() throws Exception {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.take();
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void take_noQuorum() throws Exception {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumWriteTest.quorumType.name())));
        q.take();
        transactionContext.commitTransaction();
    }
}

