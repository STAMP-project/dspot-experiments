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


import com.hazelcast.config.Config;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_TimeoutTest extends HazelcastTestSupport {
    private static final Object RESPONSE = "someresponse";

    /**
     * Tests if the get is called with a timeout, and the operation takes more time to execute then the timeout, that the call
     * fails with a TimeoutException.
     */
    @Test
    public void whenGetTimeout_thenTimeoutException() throws InterruptedException, ExecutionException, TimeoutException {
        Config config = new Config();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Future future = opService.invokeOnPartition(null, new SlowOperation(TimeUnit.SECONDS.toMillis(10), Invocation_TimeoutTest.RESPONSE), HazelcastTestSupport.getPartitionId(remote));
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail();
        } catch (TimeoutException ignored) {
        }
        // so even though the previous get failed with a timeout, the future can still provide a valid result.
        Assert.assertEquals(Invocation_TimeoutTest.RESPONSE, future.get());
    }

    @Test
    public void whenMultipleThreadsCallGetOnSameLongRunningOperation() throws InterruptedException, ExecutionException {
        long callTimeout = 5000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeout));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        final Future future = opService.invokeOnPartition(null, new SlowOperation((callTimeout * 3), Invocation_TimeoutTest.RESPONSE), HazelcastTestSupport.getPartitionId(remote));
        List<Future> futures = new LinkedList<Future>();
        for (int k = 0; k < 10; k++) {
            futures.add(HazelcastTestSupport.spawn(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return future.get();
                }
            }));
        }
        for (Future sf : futures) {
            Assert.assertEquals(Invocation_TimeoutTest.RESPONSE, sf.get());
        }
    }

    // ==================== long running operation ===============================================================================
    // Tests that a long running operation is not going to give any problems.
    // 
    // When an operation is running for a long time, so a much longer time than the call timeout and heartbeat time, due to
    // the heartbeats being detected, the call will not timeout and returns a valid response.
    // ===========================================================================================================================
    @Test
    public void sync_whenLongRunningOperation() throws InterruptedException, ExecutionException, TimeoutException {
        long callTimeout = 10000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeout));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Future future = opService.invokeOnPartition(null, new SlowOperation((6 * callTimeout), Invocation_TimeoutTest.RESPONSE), HazelcastTestSupport.getPartitionId(remote));
        Object result = future.get(120, TimeUnit.SECONDS);
        Assert.assertEquals(Invocation_TimeoutTest.RESPONSE, result);
    }

    @Test
    public void async_whenLongRunningOperation() throws InterruptedException, ExecutionException, TimeoutException {
        long callTimeout = 10000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeout));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        ICompletableFuture<Object> future = opService.invokeOnPartition(null, new SlowOperation((6 * callTimeout), Invocation_TimeoutTest.RESPONSE), HazelcastTestSupport.getPartitionId(remote));
        final ExecutionCallback<Object> callback = Invocation_TimeoutTest.getExecutionCallbackMock();
        future.andThen(callback);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback).onResponse(Invocation_TimeoutTest.RESPONSE);
            }
        });
    }

    // ==================== operation heartbeat timeout ==========================================================================
    // This test verifies that an Invocation is going to timeout when no heartbeat is received.
    // 
    // This is simulated by executing an void operation (an operation that doesn't send a response). After the execution of this
    // operation, no heartbeats will be received since it has executed successfully. So eventually the heartbeat timeout should
    // kick in.
    // ===========================================================================================================================
    @Test
    public void sync_whenHeartbeatTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 5000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Future future = opService.invokeOnPartition(null, new VoidOperation(), HazelcastTestSupport.getPartitionId(remote));
        try {
            future.get((5 * callTimeoutMs), TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            HazelcastTestSupport.assertInstanceOf(OperationTimeoutException.class, cause);
            HazelcastTestSupport.assertContains(cause.getMessage(), "operation-heartbeat-timeout");
        }
    }

    @Test
    public void async_whenHeartbeatTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 1000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        ICompletableFuture<Object> future = opService.invokeOnPartition(null, new VoidOperation(), HazelcastTestSupport.getPartitionId(remote));
        ExecutionCallback<Object> callback = Invocation_TimeoutTest.getExecutionCallbackMock();
        future.andThen(callback);
        Invocation_TimeoutTest.assertEventuallyFailsWithHeartbeatTimeout(callback);
    }

    // ==================== eventually operation heartbeat timeout ===============================================================
    // This test verifies that an Invocation is going to timeout when initially there was a heartbeat, but eventually this
    // heartbeat stops
    // 
    // This is done by creating a void operation that runs for an extended period and on completion, the void operation doesn't
    // send a response.
    // ===========================================================================================================================
    @Test
    public void sync_whenEventuallyHeartbeatTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 5000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Future future = opService.invokeOnPartition(null, new VoidOperation((callTimeoutMs * 5)), HazelcastTestSupport.getPartitionId(remote));
        try {
            future.get((10 * callTimeoutMs), TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            HazelcastTestSupport.assertInstanceOf(OperationTimeoutException.class, cause);
            HazelcastTestSupport.assertContains(cause.getMessage(), "operation-heartbeat-timeout");
        }
    }

    @Test
    public void async_whenEventuallyHeartbeatTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 5000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        ICompletableFuture<Object> future = opService.invokeOnPartition(null, new VoidOperation((callTimeoutMs * 5)), HazelcastTestSupport.getPartitionId(remote));
        final ExecutionCallback<Object> callback = Invocation_TimeoutTest.getExecutionCallbackMock();
        future.andThen(callback);
        Invocation_TimeoutTest.assertEventuallyFailsWithHeartbeatTimeout(callback);
    }

    // ==================== operation call timeout ===============================================================================
    // This test verifies that an operation doesn't get executed after its timeout expires. This is done by
    // executing an operation in front of the operation that takes a lot of time to execute.
    // ===========================================================================================================================
    @Test
    public void sync_whenCallTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 60000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        int partitionId = HazelcastTestSupport.getPartitionId(remote);
        long slowOperationDurationMs = ((long) (callTimeoutMs * 1.1));
        opService.invokeOnPartition(setPartitionId(partitionId));
        Future future = opService.invokeOnPartition(setPartitionId(partitionId));
        try {
            future.get((3 * callTimeoutMs), TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            HazelcastTestSupport.assertInstanceOf(OperationTimeoutException.class, cause);
            HazelcastTestSupport.assertContains(cause.getMessage(), "operation-call-timeout");
        }
    }

    @Test
    public void async_whenCallTimeout_thenOperationTimeoutException() throws Exception {
        long callTimeoutMs = 60000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMs));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(local, remote);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        int partitionId = HazelcastTestSupport.getPartitionId(remote);
        long slowOperationDurationMs = ((long) (callTimeoutMs * 1.1));
        opService.invokeOnPartition(setPartitionId(partitionId));
        ICompletableFuture<Object> future = opService.invokeOnPartition(setPartitionId(partitionId));
        ExecutionCallback<Object> callback = Invocation_TimeoutTest.getExecutionCallbackMock();
        future.andThen(callback);
        Invocation_TimeoutTest.assertEventuallyFailsWithCallTimeout(callback);
    }
}

