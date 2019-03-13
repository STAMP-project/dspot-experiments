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
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationServiceImpl_invokeTargetAwareOperationTest extends HazelcastTestSupport {
    @Rule
    public TestName testName = new TestName();

    private HazelcastInstance local;

    private InternalOperationService operationService;

    private HazelcastInstance remote;

    @Test
    public void whenInvokedOnLocalPartition() {
        Address expected = HazelcastTestSupport.getAddress(local);
        TargetAwareOperation operation = new TargetAwareOperation();
        InternalCompletableFuture<String> invocation = operationService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(local));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenInvokedOnRemotePartition() {
        Address expected = HazelcastTestSupport.getAddress(remote);
        TargetAwareOperation operation = new TargetAwareOperation();
        InternalCompletableFuture<String> invocation = operationService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenInvokedOnLocalTarget() {
        Address expected = HazelcastTestSupport.getAddress(local);
        TargetAwareOperation operation = new TargetAwareOperation();
        InternalCompletableFuture<String> invocation = operationService.invokeOnTarget(null, operation, HazelcastTestSupport.getAddress(local));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenInvokedOnRemoteTarget() {
        Address expected = HazelcastTestSupport.getAddress(remote);
        TargetAwareOperation operation = new TargetAwareOperation();
        InternalCompletableFuture<String> invocation = operationService.invokeOnTarget(null, operation, HazelcastTestSupport.getAddress(remote));
        Assert.assertEquals(expected, invocation.join());
    }

    @Test
    public void whenInvokedWithTargetAwareBackup_singleBackupHasTargetInjected() {
        TargetAwareOperation operation = new TargetAwareOperation(1, 0, HazelcastTestSupport.getPartitionId(remote));
        operationService.invokeOnPartition(null, operation, operation.getPartitionId()).join();
        // primary operation targets remote, backup targets local
        Assert.assertEquals(HazelcastTestSupport.getAddress(remote), TargetAwareOperation.TARGETS.get(0));
        Assert.assertEquals(HazelcastTestSupport.getAddress(local), TargetAwareOperation.TARGETS.get(1));
    }

    @Test
    public void whenInvokedWithTargetAwareBackups_multipleBackupsHaveTargetInjected() {
        int backupCount = 4;
        int partitionId = HazelcastTestSupport.getPartitionId(local);
        InternalPartitionService localPartitionService = HazelcastTestSupport.getPartitionService(local);
        InternalPartition partition = localPartitionService.getPartition(partitionId);
        List<Address> expectedTargetAddresses = new ArrayList<Address>();
        for (int i = 0; i < (backupCount + 1); i++) {
            expectedTargetAddresses.add(partition.getReplicaAddress(i));
        }
        TargetAwareOperation operation = new TargetAwareOperation(backupCount, 0, partitionId);
        operationService.invokeOnPartition(null, operation, operation.getPartitionId()).join();
        Assert.assertEquals(expectedTargetAddresses, TargetAwareOperation.TARGETS);
    }
}

