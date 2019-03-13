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


import InvocationConstant.CALL_TIMEOUT;
import InvocationConstant.INTERRUPTED;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvocationFuture_IsDoneTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private InternalOperationService operationService;

    @Test
    public void whenNullResponse() throws InterruptedException, ExecutionException {
        DummyOperation op = new DummyOperation(null);
        InternalCompletableFuture future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        future.get();
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void whenInterruptedResponse() {
        DummyOperation op = new InvocationFuture_IsDoneTest.GetLostPartitionOperation();
        InvocationFuture future = ((InvocationFuture) (operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local))));
        future.complete(INTERRUPTED);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void whenTimeoutResponse() {
        DummyOperation op = new InvocationFuture_IsDoneTest.GetLostPartitionOperation();
        InvocationFuture future = ((InvocationFuture) (operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local))));
        future.complete(CALL_TIMEOUT);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void isDone_whenNoResponse() {
        DummyOperation op = new InvocationFuture_IsDoneTest.GetLostPartitionOperation();
        InternalCompletableFuture future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void isDone_whenObjectResponse() {
        DummyOperation op = new DummyOperation("foobar");
        InternalCompletableFuture future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        Assert.assertTrue(future.isDone());
    }

    // needed to have an invocation and this is the easiest way how to get one and do not bother with its result
    private static class GetLostPartitionOperation extends DummyOperation {
        {
            // we need to set the call ID to prevent running the operation on the calling-thread
            setPartitionId(1);
        }

        @Override
        public void run() throws Exception {
            Thread.sleep(5000);
        }
    }
}

