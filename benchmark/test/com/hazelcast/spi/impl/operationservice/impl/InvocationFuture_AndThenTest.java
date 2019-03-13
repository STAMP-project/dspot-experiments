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


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvocationFuture_AndThenTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private InternalOperationService operationService;

    @Test(expected = IllegalArgumentException.class)
    public void whenNullCallback() {
        DummyOperation op = new DummyOperation(null);
        InternalCompletableFuture<Object> future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        future.andThen(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullCallback2() {
        DummyOperation op = new DummyOperation(null);
        InternalCompletableFuture<Object> future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        future.andThen(null, Mockito.mock(Executor.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullExecutor() {
        DummyOperation op = new DummyOperation(null);
        InternalCompletableFuture<Object> future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        future.andThen(InvocationFuture_AndThenTest.getExecutionCallbackMock(), null);
    }

    // there is a bug: https://github.com/hazelcast/hazelcast/issues/5001
    @Test
    public void whenNullResponse_thenCallbackExecuted() throws InterruptedException, ExecutionException {
        DummyOperation op = new DummyOperation(null);
        final ExecutionCallback<Object> callback = InvocationFuture_AndThenTest.getExecutionCallbackMock();
        InternalCompletableFuture<Object> future = operationService.invokeOnTarget(null, op, HazelcastTestSupport.getAddress(local));
        future.get();
        // callback can be completed immediately, since a response (NULL_RESPONSE) has been already set
        future.andThen(callback);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(callback, Mockito.times(1)).onResponse(ArgumentMatchers.isNull());
            }
        });
    }
}

