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
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
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
public class OperationExecutorImpl_RunTest extends OperationExecutorImpl_AbstractTest {
    @Test(expected = NullPointerException.class)
    public void test_whenNull() {
        initExecutor();
        executor.run(null);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromNormalThread() {
        initExecutor();
        OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        executor.run(genericOperation);
        OperationExecutorImpl_AbstractTest.DummyOperationRunner adhocHandler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunnerFactory) (handlerFactory)).adhocHandler;
        HazelcastTestSupport.assertContains(adhocHandler.operations, genericOperation);
    }

    @Test
    public void test_whenGenericOperation_andCallingFromGenericThread() {
        config.setProperty(GroupProperty.GENERIC_OPERATION_THREAD_COUNT.getName(), "1");
        config.setProperty(GroupProperty.PRIORITY_GENERIC_OPERATION_THREAD_COUNT.getName(), "0");
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyOperationRunner genericOperationHandler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunnerFactory) (handlerFactory)).genericOperationHandlers.get(0);
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(Operation.GENERIC_PARTITION_ID) {
            @Override
            public Object call() {
                executor.run(genericOperation);
                return null;
            }
        };
        executor.execute(task);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                boolean contains = genericOperationHandler.operations.contains(genericOperation);
                Assert.assertTrue("operation is not found in the generic operation handler", contains);
            }
        });
    }

    @Test
    public void test_whenGenericOperation_andCallingFromPartitionThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        final int partitionId = 0;
        PartitionSpecificCallable task = new PartitionSpecificCallable(partitionId) {
            @Override
            public Object call() {
                executor.run(genericOperation);
                return null;
            }
        };
        executor.execute(task);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                OperationExecutorImpl_AbstractTest.DummyOperationRunner handler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunner) (executor.getPartitionOperationRunners()[partitionId]));
                HazelcastTestSupport.assertContains(handler.operations, genericOperation);
            }
        });
    }

    // IO thread is now allowed to run any operation, so we expect an IllegalThreadStateException
    @Test
    public void test_whenGenericOperation_andCallingFromIOThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyGenericOperation genericOperation = new OperationExecutorImpl_AbstractTest.DummyGenericOperation();
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    executor.run(genericOperation);
                    return Boolean.FALSE;
                } catch (IllegalThreadStateException e) {
                    return Boolean.TRUE;
                }
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread thread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        thread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.TRUE);
    }

    @Test(expected = IllegalThreadStateException.class)
    public void test_whenPartitionOperation_andCallingFromNormalThread() {
        initExecutor();
        OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        executor.run(partitionOperation);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromGenericThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        PartitionSpecificCallable task = new PartitionSpecificCallable(Operation.GENERIC_PARTITION_ID) {
            @Override
            public Object call() {
                try {
                    executor.run(partitionOperation);
                    return Boolean.FALSE;
                } catch (IllegalThreadStateException e) {
                    return Boolean.TRUE;
                }
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromPartitionThread_andWrongPartition() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        int wrongPartitionId = (partitionOperation.getPartitionId()) + 1;
        PartitionSpecificCallable task = new PartitionSpecificCallable(wrongPartitionId) {
            @Override
            public Object call() {
                try {
                    executor.run(partitionOperation);
                    return Boolean.FALSE;
                } catch (IllegalThreadStateException e) {
                    return Boolean.TRUE;
                }
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromPartitionThread_andRightPartition() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        final int partitionId = partitionOperation.getPartitionId();
        PartitionSpecificCallable task = new PartitionSpecificCallable(partitionId) {
            @Override
            public Object call() {
                executor.run(partitionOperation);
                return Boolean.TRUE;
            }
        };
        executor.execute(task);
        OperationExecutorImpl_AbstractTest.assertEqualsEventually(task, Boolean.TRUE);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                OperationExecutorImpl_AbstractTest.DummyOperationRunner handler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunner) (executor.getPartitionOperationRunners()[partitionId]));
                HazelcastTestSupport.assertContains(handler.operations, partitionOperation);
            }
        });
    }

    @Test
    public void test_whenPartitionOperation_andCallingFromIOThread() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyPartitionOperation partitionOperation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation();
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    executor.run(partitionOperation);
                    return Boolean.FALSE;
                } catch (IllegalThreadStateException e) {
                    return Boolean.TRUE;
                }
            }
        });
        OperationExecutorImpl_AbstractTest.DummyOperationHostileThread thread = new OperationExecutorImpl_AbstractTest.DummyOperationHostileThread(futureTask);
        thread.start();
        HazelcastTestSupport.assertEqualsEventually(futureTask, Boolean.TRUE);
    }
}

