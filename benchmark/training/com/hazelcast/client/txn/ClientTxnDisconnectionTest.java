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
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.spi.impl.operationparker.impl.OperationParkerImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientTxnDisconnectionTest {
    private static final String BOUNDED_QUEUE_PREFIX = "bounded-queue-*";

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private OperationParkerImpl waitNotifyService;

    private HazelcastInstance client;

    @Test
    public void testQueueTake() {
        testQueue(new Callable() {
            @Override
            public Object call() throws InterruptedException {
                TransactionContext context = client.newTransactionContext();
                context.beginTransaction();
                TransactionalQueue<Object> queue = context.getQueue(HazelcastTestSupport.randomString());
                return queue.take();
            }
        });
    }

    @Test
    public void testQueuePoll() {
        testQueue(new Callable() {
            @Override
            public Object call() throws InterruptedException {
                TransactionContext context = client.newTransactionContext();
                context.beginTransaction();
                TransactionalQueue<Object> queue = context.getQueue(HazelcastTestSupport.randomString());
                return queue.poll(20, TimeUnit.SECONDS);
            }
        });
    }

    @Test
    public void testQueueOffer() {
        testQueue(new Callable() {
            @Override
            public Object call() throws InterruptedException {
                String name = (ClientTxnDisconnectionTest.BOUNDED_QUEUE_PREFIX) + (HazelcastTestSupport.randomString());
                client.getQueue(name).offer(HazelcastTestSupport.randomString());
                TransactionContext context = client.newTransactionContext();
                context.beginTransaction();
                TransactionalQueue<Object> queue = context.getQueue(name);
                return queue.offer(HazelcastTestSupport.randomString(), 20, TimeUnit.SECONDS);
            }
        });
    }
}

