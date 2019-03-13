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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.ExceptionThrowingCallable;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationServiceImpl_invokeOnPartitionTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private InternalOperationService operationService;

    private HazelcastInstance remote;

    @Test
    public void whenLocalPartition() {
        String expected = "foobar";
        DummyOperation operation = new DummyOperation(expected);
        InternalCompletableFuture<String> invocation = operationService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(local));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenRemotePartition() {
        String expected = "foobar";
        DummyOperation operation = new DummyOperation(expected);
        InternalCompletableFuture<String> invocation = operationService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenExceptionThrownInOperationRun() {
        DummyOperation operation = new DummyOperation(new ExceptionThrowingCallable());
        InternalCompletableFuture<String> invocation = operationService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote));
        try {
            invocation.join();
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
        }
    }
}

