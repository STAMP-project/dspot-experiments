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
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class OperationExecutorImpl_IsInvocationAllowedTest extends OperationExecutorImpl_AbstractTest {
    @Test(expected = NullPointerException.class)
    public void test_whenNullOperation() {
        initExecutor();
        executor.isInvocationAllowed(null, false);
    }

    // ============= generic operations ==============================
    @Test
    public void test_whenGenericOperation_andCallingFromUserThread() {
        initExecutor();
        OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        boolean result = executor.isInvocationAllowed(genericOperation, false);
        Assert.assertTrue(result);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromPartitionOperationThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(0) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(genericOperation, false);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromGenericOperationThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(Operation.GENERIC_PARTITION_ID) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(genericOperation, false);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromOperationHostileThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return executor.isInvocationAllowed(genericOperation, false);
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread thread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        thread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.FALSE);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromOperationHostileThread_andAsync() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return executor.isInvocationAllowed(genericOperation, true);
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread hostileThread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        hostileThread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.FALSE);
    }

    // ===================== partition specific operations ========================
    @Test
    public void test_whenPartitionOperation_andCallingFromUserThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        boolean result = executor.isInvocationAllowed(partitionOperation, false);
        Assert.assertTrue(result);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromGenericOperationThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(Operation.GENERIC_PARTITION_ID) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(partitionOperation, false);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromPartitionOperationThread_andCorrectPartition() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(partitionOperation.getPartitionId()) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(partitionOperation, false);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromPartitionOperationThread_andWrongPartition() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        int wrongPartition = (partitionOperation.getPartitionId()) + 1;
        PartitionSpecificCallable task = new PartitionSpecificCallable(wrongPartition) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(partitionOperation, false);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.FALSE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromPartitionOperationThread_andWrongPartition_andAsync() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        int wrongPartition = (partitionOperation.getPartitionId()) + 1;
        PartitionSpecificCallable task = new PartitionSpecificCallable(wrongPartition) {
            @Override
            public Object call() {
                return executor.isInvocationAllowed(partitionOperation, true);
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromOperationHostileThread() {
        initExecutor();
        final Operation operation = new OperationExecutorImpl_AbstractTest.DummyOperation(1);
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return executor.isInvocationAllowed(operation, false);
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread thread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        thread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.FALSE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromOperationHostileThread_andAsync() {
        initExecutor();
        final Operation operation = new OperationExecutorImpl_AbstractTest.DummyOperation(1);
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return executor.isInvocationAllowed(operation, true);
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread thread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        thread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.FALSE);
    }
}

