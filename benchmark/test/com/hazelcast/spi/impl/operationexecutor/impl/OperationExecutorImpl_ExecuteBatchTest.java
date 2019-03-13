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
package com.hazelcast.spi.impl.operationexecutor.impl;


import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationExecutorImpl_ExecuteBatchTest extends OperationExecutorImpl_AbstractTest {
    @Test(expected = NullPointerException.class)
    public void whenNullFactory() {
        initExecutor();
        executor.executeOnPartitions(null, new BitSet());
    }

    @Test(expected = NullPointerException.class)
    public void whenNullPartitions() {
        initExecutor();
        executor.executeOnPartitions(Mockito.mock(PartitionTaskFactory.class), null);
    }

    @Test
    public void executeOnEachPartition() {
        config.setProperty(GroupProperty.PARTITION_OPERATION_THREAD_COUNT.getName(), "16");
        initExecutor();
        final BitSet partitions = newPartitions();
        final OperationExecutorImpl_ExecuteBatchTest.DummyPartitionTaskFactory taskFactory = new OperationExecutorImpl_ExecuteBatchTest.DummyPartitionTaskFactory();
        executor.executeOnPartitions(taskFactory, partitions);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(partitions.length(), taskFactory.completed.get());
            }
        });
    }

    @Test
    public void noMoreBubble() {
        config.setProperty(GroupProperty.PARTITION_OPERATION_THREAD_COUNT.getName(), "1");
        initExecutor();
        final OperationExecutorImpl_ExecuteBatchTest.DummyPartitionTaskFactory taskFactory = new OperationExecutorImpl_ExecuteBatchTest.DummyPartitionTaskFactory();
        taskFactory.delayMs = 1000;
        executor.executeOnPartitions(taskFactory, newPartitions());
        final OperationExecutorImpl_ExecuteBatchTest.DummyOperation op = new OperationExecutorImpl_ExecuteBatchTest.DummyOperation();
        executor.execute(op);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(op.completed);
            }
        }, 5);
    }

    class DummyOperation extends Operation {
        private volatile boolean completed;

        @Override
        public void run() {
            completed = true;
        }
    }

    class DummyPartitionTaskFactory implements PartitionTaskFactory {
        private final AtomicLong completed = new AtomicLong();

        private int delayMs;

        @Override
        public Object create(int partitionId) {
            return new OperationExecutorImpl_ExecuteBatchTest.DummyTask(completed, delayMs);
        }
    }

    class DummyTask implements Runnable {
        private final AtomicLong completed;

        private final int delayMs;

        DummyTask(AtomicLong completed, int delayMs) {
            this.completed = completed;
            this.delayMs = delayMs;
        }

        @Override
        public void run() {
            HazelcastTestSupport.sleepMillis(delayMs);
            completed.incrementAndGet();
        }
    }
}

