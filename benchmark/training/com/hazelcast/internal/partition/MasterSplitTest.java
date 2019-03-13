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
package com.hazelcast.internal.partition;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.operation.FetchPartitionStateOperation;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MasterSplitTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    @Test
    public void test_migrationFailsOnMasterMismatch_onSource() throws InterruptedException {
        HazelcastInstance member1 = factory.newHazelcastInstance();
        HazelcastInstance member2 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(member1, member2);
        MigrationInfo migration = createMigrationInfo(member1, member2);
        int partitionStateVersion = HazelcastTestSupport.getPartitionService(member1).getPartitionStateVersion();
        Operation op = new com.hazelcast.internal.partition.operation.MigrationRequestOperation(migration, Collections.<MigrationInfo>emptyList(), partitionStateVersion, true);
        InvocationBuilder invocationBuilder = HazelcastTestSupport.getOperationServiceImpl(member1).createInvocationBuilder(IPartitionService.SERVICE_NAME, op, HazelcastTestSupport.getAddress(member2)).setCallTimeout(TimeUnit.MINUTES.toMillis(1));
        Future future = invocationBuilder.invoke();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void test_migrationFailsOnMasterMismatch_onDestination() throws InterruptedException {
        HazelcastInstance member1 = factory.newHazelcastInstance();
        HazelcastInstance member2 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(member1, member2);
        MigrationInfo migration = createMigrationInfo(member1, member2);
        int partitionStateVersion = HazelcastTestSupport.getPartitionService(member1).getPartitionStateVersion();
        ReplicaFragmentMigrationState migrationState = new ReplicaFragmentMigrationState(Collections.<ServiceNamespace, long[]>emptyMap(), Collections.<Operation>emptySet());
        Operation op = new com.hazelcast.internal.partition.operation.MigrationOperation(migration, Collections.<MigrationInfo>emptyList(), partitionStateVersion, migrationState, true, true);
        InvocationBuilder invocationBuilder = HazelcastTestSupport.getOperationServiceImpl(member1).createInvocationBuilder(IPartitionService.SERVICE_NAME, op, HazelcastTestSupport.getAddress(member2)).setCallTimeout(TimeUnit.MINUTES.toMillis(1));
        Future future = invocationBuilder.invoke();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void test_fetchPartitionStateFailsOnMasterMismatch() throws InterruptedException {
        HazelcastInstance member1 = factory.newHazelcastInstance();
        HazelcastInstance member2 = factory.newHazelcastInstance();
        HazelcastInstance member3 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(member1, member2, member3);
        InternalCompletableFuture<Object> future = HazelcastTestSupport.getOperationServiceImpl(member2).createInvocationBuilder(IPartitionService.SERVICE_NAME, new FetchPartitionStateOperation(), HazelcastTestSupport.getAddress(member3)).setTryCount(Integer.MAX_VALUE).setCallTimeout(Long.MAX_VALUE).invoke();
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
    }
}

