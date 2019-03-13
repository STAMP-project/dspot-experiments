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
package com.hazelcast.internal.partition.impl;


import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionReplicaStateChecker_triggerAndWaitForReplicaSyncTest extends HazelcastTestSupport {
    private List<InternalPartition> partitions = new ArrayList<InternalPartition>();

    private Node node;

    private PartitionStateManager partitionStateManager;

    private MigrationManager migrationManager;

    private PartitionReplicaStateChecker replicaStateChecker;

    @Test
    public void whenCalledWithZeroTimeout_thenDoNothing() {
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(0, TimeUnit.MILLISECONDS));
    }

    @Test
    public void whenHasMissingReplicaOwners_withAddress_thenWaitForMissingReplicaOwners() throws Exception {
        configureNeedsReplicaStateCheckResponse();
        Address address = new Address("127.0.0.1", 5701);
        InternalPartition partition = new DummyInternalPartition(new PartitionReplica[]{ new PartitionReplica(address, UuidUtil.newUnsecureUuidString()) }, 1);
        partitions.add(partition);
        Assert.assertEquals(PartitionServiceState.REPLICA_NOT_OWNED, replicaStateChecker.getPartitionServiceState());
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenHasMissingReplicaOwners_withoutAddress_thenWaitForMissingReplicaOwners() {
        configureNeedsReplicaStateCheckResponse();
        InternalPartition partition = new DummyInternalPartition(new PartitionReplica[0], 1);
        partitions.add(partition);
        Assert.assertEquals(PartitionServiceState.REPLICA_NOT_OWNED, replicaStateChecker.getPartitionServiceState());
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenHasOngoingMigration_withLocalMigration_thenWaitForOngoingMigrations() {
        Mockito.when(migrationManager.hasOnGoingMigration()).thenReturn(true);
        Assert.assertEquals(PartitionServiceState.MIGRATION_LOCAL, replicaStateChecker.getPartitionServiceState());
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenHasOngoingMigration_withMigrationOnMaster_thenWaitForOngoingMigrations() {
        Mockito.when(node.getMasterAddress()).thenReturn(null);
        Mockito.when(node.getClusterService().isJoined()).thenReturn(true);
        Assert.assertEquals(PartitionServiceState.MIGRATION_ON_MASTER, replicaStateChecker.getPartitionServiceState());
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenCheckAndTriggerReplicaSync() {
        configureNeedsReplicaStateCheckResponseOnEachSecondCall();
        InternalPartition partition = new DummyInternalPartition(new PartitionReplica[]{ null }, 1);
        partitions.add(partition);
        Assert.assertEquals(PartitionServiceState.REPLICA_NOT_SYNC, replicaStateChecker.getPartitionServiceState());
        Assert.assertFalse(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenCheckAndTriggerReplicaSync_withNeedsReplicaStateCheck_thenReturnTrue() {
        configureNeedsReplicaStateCheckResponseOnEachSecondCall();
        Assert.assertEquals(PartitionServiceState.SAFE, replicaStateChecker.getPartitionServiceState());
        Assert.assertTrue(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    @Test
    public void whenCheckAndTriggerReplicaSync_withoutNeedsReplicaStateCheck_thenReturnTrue() {
        Assert.assertEquals(PartitionServiceState.SAFE, replicaStateChecker.getPartitionServiceState());
        Assert.assertTrue(replicaStateChecker.triggerAndWaitForReplicaSync(10, TimeUnit.MILLISECONDS, 5));
    }

    /**
     * Alternately returns {@code true} and {@code false}, beginning with {@code false}.
     */
    private static class AlternatingAnswer implements Answer<Boolean> {
        private boolean state = true;

        @Override
        public Boolean answer(InvocationOnMock invocationOnMock) {
            state = !(state);
            return state;
        }
    }
}

