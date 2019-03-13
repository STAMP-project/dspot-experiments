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


import ClusterState.FROZEN;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InternalPartitionServiceImplTest extends HazelcastTestSupport {
    private HazelcastInstance instance;

    private InternalPartitionServiceImpl partitionService;

    private Member localMember;

    private int partitionCount;

    private final AtomicBoolean startupDone = new AtomicBoolean(true);

    @Test(expected = HazelcastInstanceNotActiveException.class)
    public void test_getPartitionOwnerOrWait_throwsException_afterNodeShutdown() throws Exception {
        instance.shutdown();
        partitionService.getPartitionOwnerOrWait(0);
    }

    @Test
    public void test_initialAssignment() {
        partitionService.firstArrangement();
        int partitionCount = partitionService.getPartitionCount();
        for (int i = 0; i < partitionCount; i++) {
            Assert.assertTrue(partitionService.isPartitionOwner(i));
        }
    }

    @Test
    public void test_initialAssignment_whenStartNotCompleted() {
        startupDone.set(false);
        partitionService.firstArrangement();
        Assert.assertFalse(partitionService.getPartitionStateManager().isInitialized());
        Assert.assertEquals(0, partitionService.getPartitionStateVersion());
        Assert.assertNull(partitionService.getPartitionOwner(0));
    }

    @Test
    public void test_initialAssignment_whenClusterNotActive() {
        instance.getCluster().changeClusterState(FROZEN);
        partitionService.firstArrangement();
        Assert.assertFalse(partitionService.getPartitionStateManager().isInitialized());
        Assert.assertEquals(0, partitionService.getPartitionStateVersion());
        Assert.assertNull(partitionService.getPartitionOwner(0));
    }

    @Test(expected = IllegalStateException.class)
    public void test_getPartitionOwnerOrWait_whenClusterNotActive() {
        instance.getCluster().changeClusterState(FROZEN);
        partitionService.firstArrangement();
        partitionService.getPartitionOwnerOrWait(0);
    }

    @Test
    public void test_setInitialState() {
        PartitionReplica[][] replicas = new PartitionReplica[partitionCount][InternalPartition.MAX_REPLICA_COUNT];
        for (int i = 0; i < (partitionCount); i++) {
            replicas[i][0] = PartitionReplica.from(localMember);
        }
        partitionService.setInitialState(new com.hazelcast.internal.partition.PartitionTableView(replicas, partitionCount));
        for (int i = 0; i < (partitionCount); i++) {
            Assert.assertTrue(partitionService.isPartitionOwner(i));
        }
        Assert.assertEquals(partitionCount, partitionService.getPartitionStateVersion());
    }

    @Test(expected = IllegalStateException.class)
    public void test_setInitialState_multipleTimes() {
        PartitionReplica[][] addresses = new PartitionReplica[partitionCount][InternalPartition.MAX_REPLICA_COUNT];
        for (int i = 0; i < (partitionCount); i++) {
            addresses[i][0] = PartitionReplica.from(localMember);
        }
        partitionService.setInitialState(new com.hazelcast.internal.partition.PartitionTableView(addresses, 0));
        partitionService.setInitialState(new com.hazelcast.internal.partition.PartitionTableView(addresses, 0));
    }

    @Test
    public void test_setInitialState_listenerShouldNOTBeCalled() {
        PartitionReplica[][] addresses = new PartitionReplica[partitionCount][InternalPartition.MAX_REPLICA_COUNT];
        for (int i = 0; i < (partitionCount); i++) {
            addresses[i][0] = PartitionReplica.from(localMember);
        }
        InternalPartitionServiceImplTest.TestPartitionListener listener = new InternalPartitionServiceImplTest.TestPartitionListener();
        partitionService.addPartitionListener(listener);
        partitionService.setInitialState(new com.hazelcast.internal.partition.PartitionTableView(addresses, 0));
        Assert.assertEquals(0, listener.eventCount);
    }

    @Test
    public void test_getMemberPartitions_whenNotInitialized() {
        List<Integer> partitions = partitionService.getMemberPartitions(HazelcastTestSupport.getAddress(instance));
        Assert.assertTrue(partitionService.getPartitionStateManager().isInitialized());
        Assert.assertEquals(partitionCount, partitions.size());
    }

    @Test
    public void test_getMemberPartitions_whenInitialized() {
        partitionService.firstArrangement();
        List<Integer> partitions = partitionService.getMemberPartitions(HazelcastTestSupport.getAddress(instance));
        Assert.assertEquals(partitionCount, partitions.size());
    }

    @Test
    public void test_getMemberPartitionsIfAssigned_whenNotInitialized() {
        List<Integer> partitions = partitionService.getMemberPartitionsIfAssigned(HazelcastTestSupport.getAddress(instance));
        Assert.assertThat(partitions, Matchers.empty());
    }

    @Test
    public void test_getMemberPartitionsIfAssigned_whenInitialized() {
        partitionService.firstArrangement();
        List<Integer> partitions = partitionService.getMemberPartitionsIfAssigned(HazelcastTestSupport.getAddress(instance));
        Assert.assertEquals(partitionCount, partitions.size());
    }

    private static class TestPartitionListener implements PartitionListener {
        private int eventCount;

        @Override
        public void replicaChanged(PartitionReplicaChangeEvent event) {
            (eventCount)++;
        }
    }
}

