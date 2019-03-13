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
package com.hazelcast.spi.impl.operationservice.impl;


import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_RetryTest extends HazelcastTestSupport {
    private static final int NUMBER_OF_INVOCATIONS = 100;

    @Test
    public void whenPartitionTargetMemberDiesThenOperationSendToNewPartitionOwner() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance();
        HazelcastInstance remote = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService service = HazelcastTestSupport.getOperationService(local);
        Operation op = new Invocation_RetryTest.PartitionTargetOperation();
        Future future = service.createInvocationBuilder(null, op, HazelcastTestSupport.getPartitionId(remote)).setCallTimeout(30000).invoke();
        HazelcastTestSupport.sleepSeconds(1);
        remote.shutdown();
        // future.get() should work without a problem because the operation should be re-targeted at the newest owner
        // for the given partition
        future.get();
    }

    @Test(expected = MemberLeftException.class)
    public void whenTargetMemberDiesThenOperationAbortedWithMembersLeftException() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance();
        HazelcastInstance remote = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService service = HazelcastTestSupport.getOperationService(local);
        Operation op = new Invocation_RetryTest.TargetOperation();
        Address address = new Address(remote.getCluster().getLocalMember().getSocketAddress());
        Future future = service.createInvocationBuilder(null, op, address).invoke();
        HazelcastTestSupport.sleepSeconds(1);
        remote.getLifecycleService().terminate();
        future.get();
    }

    @Test
    public void testNoStuckInvocationsWhenRetriedMultipleTimes() throws Exception {
        Config config = new Config();
        config.setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "3000");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        NodeEngineImpl localNodeEngine = HazelcastTestSupport.getNodeEngineImpl(local);
        NodeEngineImpl remoteNodeEngine = HazelcastTestSupport.getNodeEngineImpl(remote);
        final OperationServiceImpl operationService = ((OperationServiceImpl) (localNodeEngine.getOperationService()));
        Invocation_RetryTest.NonResponsiveOperation op = new Invocation_RetryTest.NonResponsiveOperation();
        setValidateTarget(false);
        setPartitionId(1);
        InvocationFuture future = ((InvocationFuture) (operationService.invokeOnTarget(null, op, remoteNodeEngine.getThisAddress())));
        Field invocationField = InvocationFuture.class.getDeclaredField("invocation");
        invocationField.setAccessible(true);
        Invocation invocation = ((Invocation) (invocationField.get(future)));
        invocation.notifyError(new RetryableHazelcastException());
        invocation.notifyError(new RetryableHazelcastException());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Iterator<Invocation> invocations = operationService.invocationRegistry.iterator();
                Assert.assertFalse(invocations.hasNext());
            }
        });
    }

    @Test
    public void invocationShouldComplete_whenRetried_DuringShutdown() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2, hz3);
        InternalOperationService operationService = HazelcastTestSupport.getNodeEngineImpl(hz1).getOperationService();
        Future[] futures = new Future[Invocation_RetryTest.NUMBER_OF_INVOCATIONS];
        for (int i = 0; i < (Invocation_RetryTest.NUMBER_OF_INVOCATIONS); i++) {
            int partitionId = Invocation_RetryTest.getRandomPartitionId(hz2);
            futures[i] = operationService.createInvocationBuilder(null, new Invocation_RetryTest.RetryingOperation(), partitionId).setTryCount(Integer.MAX_VALUE).setCallTimeout(Long.MAX_VALUE).invoke();
        }
        hz3.getLifecycleService().terminate();
        hz1.getLifecycleService().terminate();
        for (Future future : futures) {
            try {
                future.get(2, TimeUnit.MINUTES);
            } catch (ExecutionException ignored) {
            } catch (TimeoutException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void invocationShouldComplete_whenOperationsPending_DuringShutdown() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz);
        InternalOperationService operationService = nodeEngine.getOperationService();
        operationService.invokeOnPartition(setPartitionId(0));
        HazelcastTestSupport.sleepSeconds(1);
        Future[] futures = new Future[Invocation_RetryTest.NUMBER_OF_INVOCATIONS];
        for (int i = 0; i < (Invocation_RetryTest.NUMBER_OF_INVOCATIONS); i++) {
            futures[i] = operationService.createInvocationBuilder(null, new Invocation_RetryTest.RetryingOperation(), 0).setTryCount(Integer.MAX_VALUE).setCallTimeout(Long.MAX_VALUE).invoke();
        }
        hz.getLifecycleService().terminate();
        for (Future future : futures) {
            try {
                future.get(2, TimeUnit.MINUTES);
            } catch (ExecutionException ignored) {
            } catch (TimeoutException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    /**
     * Non-responsive operation.
     */
    public static class NonResponsiveOperation extends Operation {
        @Override
        public void run() throws InterruptedException {
        }

        @Override
        public boolean returnsResponse() {
            return false;
        }
    }

    /**
     * Operation send to a specific member.
     */
    private static class TargetOperation extends Operation {
        @Override
        public void run() throws InterruptedException {
            Thread.sleep(10000);
        }
    }

    /**
     * Operation send to a specific target partition.
     */
    private static class PartitionTargetOperation extends Operation implements PartitionAwareOperation {
        @Override
        public void run() throws InterruptedException {
            Thread.sleep(5000);
        }
    }

    private static class RetryingOperation extends Operation implements AllowedDuringPassiveState {
        @Override
        public void run() throws Exception {
            throw new RetryableHazelcastException();
        }
    }

    private static class SleepingOperation extends Operation implements AllowedDuringPassiveState {
        private long sleepMillis;

        public SleepingOperation() {
        }

        SleepingOperation(long sleepMillis) {
            this.sleepMillis = sleepMillis;
        }

        @Override
        public void run() throws Exception {
            Thread.sleep(sleepMillis);
        }
    }
}

