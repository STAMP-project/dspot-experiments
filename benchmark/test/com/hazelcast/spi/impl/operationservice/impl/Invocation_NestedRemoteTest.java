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
import com.hazelcast.spi.OperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static com.hazelcast.spi.impl.operationservice.impl.Invocation_NestedAbstractTest.OuterOperation.<init>;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_NestedRemoteTest extends Invocation_NestedAbstractTest {
    private static final String RESPONSE = "someresponse";

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void invokeOnPartition_outerGeneric_innerGeneric_forbidden() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(1).newInstances();
        HazelcastInstance local = cluster[0];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        expected.expect(Exception.class);
        InternalCompletableFuture<Object> future = operationService.invokeOnPartition(null, outerOperation, outerOperation.getPartitionId());
        future.join();
    }

    @Test
    public void invokeOnPartition_outerRemote_innerGeneric() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        int partitionId = HazelcastTestSupport.getPartitionId(remote);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, partitionId);
        InternalCompletableFuture future = operationService.invokeOnPartition(null, outerOperation, partitionId);
        Assert.assertEquals(Invocation_NestedRemoteTest.RESPONSE, future.join());
    }

    @Test
    public void invokeOnPartition_outerRemote_innerSameInstance_samePartition() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        int partitionId = HazelcastTestSupport.getPartitionId(remote);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, partitionId);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, partitionId);
        InternalCompletableFuture future = operationService.invokeOnPartition(null, outerOperation, partitionId);
        Assert.assertEquals(Invocation_NestedRemoteTest.RESPONSE, future.join());
    }

    @Test
    public void invokeOnPartition_outerRemote_innerSameInstance_callsDifferentPartition_mappedToSameThread() throws Exception {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        int outerPartitionId = HazelcastTestSupport.getPartitionId(remote);
        int innerPartitionId = Invocation_NestedAbstractTest.randomPartitionIdMappedToSameThreadAsGivenPartitionIdOnInstance(outerPartitionId, remote, operationService);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, innerPartitionId);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, outerPartitionId);
        InternalCompletableFuture future = operationService.invokeOnPartition(null, outerOperation, outerPartitionId);
        expected.expect(IllegalThreadStateException.class);
        expected.expectMessage("cannot make remote call");
        future.join();
    }

    @Test
    public void invokeOnPartition_outerRemote_innerDifferentInstance_forbidden() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        int outerPartitionId = HazelcastTestSupport.getPartitionId(remote);
        int innerPartitionId = HazelcastTestSupport.getPartitionId(local);
        Assert.assertNotEquals("partitions should be different", innerPartitionId, outerPartitionId);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, innerPartitionId);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, outerPartitionId);
        InternalCompletableFuture future = operationService.invokeOnPartition(null, outerOperation, outerPartitionId);
        expected.expect(IllegalThreadStateException.class);
        expected.expectMessage("cannot make remote call");
        future.join();
    }

    @Test
    public void invokeOnPartition_outerLocal_innerDifferentInstance_forbidden() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        int outerPartitionId = HazelcastTestSupport.getPartitionId(local);
        int innerPartitionId = HazelcastTestSupport.getPartitionId(remote);
        Assert.assertNotEquals("partitions should be different", innerPartitionId, outerPartitionId);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, innerPartitionId);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, outerPartitionId);
        InternalCompletableFuture future = operationService.invokeOnPartition(null, outerOperation, outerPartitionId);
        expected.expect(IllegalThreadStateException.class);
        expected.expectMessage("cannot make remote call");
        future.join();
    }

    @Test
    public void invokeOnTarget_outerGeneric_innerGeneric() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        InternalCompletableFuture future = operationService.invokeOnTarget(null, outerOperation, HazelcastTestSupport.getAddress(remote));
        Assert.assertEquals(Invocation_NestedRemoteTest.RESPONSE, future.join());
    }

    @Test
    public void invokeOnTarget_outerGeneric_innerSameInstance() {
        HazelcastInstance[] cluster = createHazelcastInstanceFactory(2).newInstances();
        HazelcastInstance local = cluster[0];
        HazelcastInstance remote = cluster[1];
        OperationService operationService = HazelcastTestSupport.getOperationService(local);
        Invocation_NestedAbstractTest.InnerOperation innerOperation = new Invocation_NestedAbstractTest.InnerOperation(Invocation_NestedRemoteTest.RESPONSE, 0);
        Invocation_NestedAbstractTest.OuterOperation outerOperation = new Invocation_NestedAbstractTest.OuterOperation(innerOperation, Invocation_NestedAbstractTest.GENERIC_OPERATION);
        InternalCompletableFuture future = operationService.invokeOnTarget(null, outerOperation, HazelcastTestSupport.getAddress(remote));
        Assert.assertEquals(Invocation_NestedRemoteTest.RESPONSE, future.join());
    }
}

