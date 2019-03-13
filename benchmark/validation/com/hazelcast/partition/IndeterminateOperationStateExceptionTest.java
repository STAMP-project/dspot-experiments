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
package com.hazelcast.partition;


import InternalPartitionService.SERVICE_NAME;
import SpiDataSerializerHook.BACKUP;
import SpiDataSerializerHook.F_ID;
import SpiDataSerializerHook.NORMAL_RESPONSE;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndeterminateOperationStateExceptionTest extends HazelcastTestSupport {
    private HazelcastInstance instance1;

    private HazelcastInstance instance2;

    @Test
    public void partitionInvocation_shouldFailOnBackupTimeout_whenConfigurationEnabledGlobally() throws InterruptedException, TimeoutException {
        setup(true);
        PacketFiltersUtil.dropOperationsBetween(instance1, instance2, SpiDataSerializerHook.F_ID, Collections.singletonList(BACKUP));
        int partitionId = HazelcastTestSupport.getPartitionId(instance1);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instance1).getOperationService();
        InternalCompletableFuture<Object> future = operationService.createInvocationBuilder(SERVICE_NAME, new IndeterminateOperationStateExceptionTest.PrimaryOperation(), partitionId).invoke();
        try {
            future.get(2, TimeUnit.MINUTES);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IndeterminateOperationStateException));
        }
    }

    @Test
    public void partitionInvocation_shouldFailOnBackupTimeout_whenConfigurationEnabledForInvocation() throws InterruptedException, TimeoutException {
        setup(false);
        PacketFiltersUtil.dropOperationsBetween(instance1, instance2, SpiDataSerializerHook.F_ID, Collections.singletonList(BACKUP));
        int partitionId = HazelcastTestSupport.getPartitionId(instance1);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instance1).getOperationService();
        InternalCompletableFuture<Object> future = operationService.createInvocationBuilder(SERVICE_NAME, new IndeterminateOperationStateExceptionTest.PrimaryOperation(), partitionId).setFailOnIndeterminateOperationState(true).invoke();
        try {
            future.get(2, TimeUnit.MINUTES);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IndeterminateOperationStateException));
        }
    }

    @Test
    public void partitionInvocation_shouldFail_whenPartitionPrimaryLeaves() throws InterruptedException, TimeoutException {
        setup(true);
        int partitionId = HazelcastTestSupport.getPartitionId(instance2);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instance1).getOperationService();
        InternalCompletableFuture<Object> future = operationService.createInvocationBuilder(SERVICE_NAME, new IndeterminateOperationStateExceptionTest.SilentOperation(), partitionId).invoke();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(instance2.getUserContext().containsKey(IndeterminateOperationStateExceptionTest.SilentOperation.EXECUTION_STARTED));
            }
        });
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                instance2.getLifecycleService().terminate();
            }
        });
        try {
            future.get(2, TimeUnit.MINUTES);
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IndeterminateOperationStateException));
        }
    }

    @Test
    public void readOnlyPartitionInvocation_shouldSucceed_whenPartitionPrimaryLeaves() throws InterruptedException, ExecutionException, TimeoutException {
        setup(true);
        PacketFiltersUtil.dropOperationsBetween(instance2, instance1, F_ID, Collections.singletonList(NORMAL_RESPONSE));
        int partitionId = HazelcastTestSupport.getPartitionId(instance2);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instance1).getOperationService();
        InternalCompletableFuture<Boolean> future = operationService.createInvocationBuilder(SERVICE_NAME, new IndeterminateOperationStateExceptionTest.DummyReadOperation(), partitionId).invoke();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                instance2.getLifecycleService().terminate();
            }
        });
        boolean response = future.get(2, TimeUnit.MINUTES);
        Assert.assertTrue(response);
        Assert.assertEquals(HazelcastTestSupport.getAddress(instance1), instance1.getUserContext().get(IndeterminateOperationStateExceptionTest.DummyReadOperation.LAST_INVOCATION_ADDRESS));
    }

    @Test
    public void transaction_shouldFail_whenBackupTimeoutOccurs() {
        setup(true);
        PacketFiltersUtil.dropOperationsBetween(instance1, instance2, SpiDataSerializerHook.F_ID, Collections.singletonList(BACKUP));
        PacketFiltersUtil.dropOperationsBetween(instance2, instance1, SpiDataSerializerHook.F_ID, Collections.singletonList(BACKUP));
        String name = HazelcastTestSupport.randomMapName();
        String key1 = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        String key2 = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        TransactionContext context = instance1.newTransactionContext();
        context.beginTransaction();
        try {
            TransactionalMap<Object, Object> map = context.getMap(name);
            map.put(key1, "value");
            map.put(key2, "value");
            context.commitTransaction();
            Assert.fail("Should fail with IndeterminateOperationStateException");
        } catch (IndeterminateOperationStateException e) {
            context.rollbackTransaction();
        }
        IMap<Object, Object> map = instance1.getMap(name);
        Assert.assertNull(map.get(key1));
        Assert.assertNull(map.get(key2));
    }

    @Test(expected = MemberLeftException.class)
    public void targetInvocation_shouldFailWithMemberLeftException_onTargetMemberLeave() throws Exception {
        setup(true);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(instance1).getOperationService();
        Address target = HazelcastTestSupport.getAddress(instance2);
        InternalCompletableFuture<Object> future = operationService.createInvocationBuilder(SERVICE_NAME, new IndeterminateOperationStateExceptionTest.SilentOperation(), target).invoke();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(instance2.getUserContext().containsKey(IndeterminateOperationStateExceptionTest.SilentOperation.EXECUTION_STARTED));
            }
        });
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                instance2.getLifecycleService().terminate();
            }
        });
        future.get(2, TimeUnit.MINUTES);
    }

    public static class PrimaryOperation extends Operation implements BackupAwareOperation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public boolean returnsResponse() {
            return true;
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 1;
        }

        @Override
        public int getAsyncBackupCount() {
            return 0;
        }

        @Override
        public Operation getBackupOperation() {
            return new IndeterminateOperationStateExceptionTest.BackupOperation();
        }
    }

    public static class BackupOperation extends Operation {
        public static final String EXECUTION_DONE = "execution-done";

        @Override
        public void run() throws Exception {
            getNodeEngine().getHazelcastInstance().getUserContext().put(IndeterminateOperationStateExceptionTest.BackupOperation.EXECUTION_DONE, new Object());
        }
    }

    public static class SilentOperation extends Operation {
        static final String EXECUTION_STARTED = "execution-started";

        @Override
        public void run() throws Exception {
            getNodeEngine().getHazelcastInstance().getUserContext().put(IndeterminateOperationStateExceptionTest.SilentOperation.EXECUTION_STARTED, new Object());
        }

        @Override
        public boolean returnsResponse() {
            return false;
        }
    }

    public static class DummyReadOperation extends Operation implements ReadonlyOperation {
        static final String LAST_INVOCATION_ADDRESS = "last-invocation-address";

        @Override
        public void run() throws Exception {
            Address address = getNodeEngine().getThisAddress();
            getNodeEngine().getHazelcastInstance().getUserContext().put(IndeterminateOperationStateExceptionTest.DummyReadOperation.LAST_INVOCATION_ADDRESS, address);
        }

        @Override
        public boolean returnsResponse() {
            return true;
        }

        @Override
        public Object getResponse() {
            return true;
        }
    }
}

