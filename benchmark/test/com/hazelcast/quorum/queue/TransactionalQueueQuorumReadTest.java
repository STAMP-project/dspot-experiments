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
public class TransactionalQueueQuorumReadTest extends AbstractQuorumTest {
    @Parameterized.Parameter(0)
    public static TransactionOptions options;

    @Parameterized.Parameter(1)
    public static QuorumType quorumType;

    @Test
    public void peek_quorum() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.peek();
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void peek_noQuorum() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.peek();
        transactionContext.commitTransaction();
    }

    @Test
    public void peekTimeout_quorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.peek(10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void peekTimeout_noQuorum() throws InterruptedException {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.peek(10L, TimeUnit.MILLISECONDS);
        transactionContext.commitTransaction();
    }

    @Test
    public void size_quorum() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.size();
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void size_noQuorum() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalQueue<Object> q = transactionContext.getQueue(((AbstractQuorumTest.QUEUE_NAME) + (TransactionalQueueQuorumReadTest.quorumType.name())));
        q.size();
        transactionContext.commitTransaction();
    }
}

