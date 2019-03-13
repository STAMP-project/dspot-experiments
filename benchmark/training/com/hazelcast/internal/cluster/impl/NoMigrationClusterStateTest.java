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
package com.hazelcast.internal.cluster.impl;


import ClusterState.ACTIVE;
import ClusterState.FROZEN;
import ClusterState.NO_MIGRATION;
import ClusterState.PASSIVE;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NoMigrationClusterStateTest extends HazelcastTestSupport {
    private final NoMigrationClusterStateTest.NoReplicationService service = new NoMigrationClusterStateTest.NoReplicationService();

    @Test
    public void rebalancing_shouldNotHappen_whenMemberLeaves() {
        Config config = newConfigWithMigrationAwareService();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(config, 3);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        TestUtil.terminateInstance(instances[0]);
        final HazelcastInstance hz = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            final Node node = HazelcastTestSupport.getNode(hz);

            final InternalPartitionService partitionService = node.getPartitionService();

            @Override
            public void run() throws Exception {
                List<Integer> memberPartitions = partitionService.getMemberPartitions(node.getThisAddress());
                Assert.assertThat(memberPartitions, Matchers.empty());
                service.assertNoReplication();
            }
        }, 10);
    }

    @Test
    public void promotions_shouldHappen_whenMemberLeaves() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(new Config(), 3);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        TestUtil.terminateInstance(instances[0]);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[1]);
        NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[1], 1);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[2]);
        NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[2], 1);
    }

    @Test
    public void lostPartitions_shouldBeAssigned_toAvailableMembers() {
        int clusterSize = (InternalPartition.MAX_REPLICA_COUNT) + 3;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(new Config(), clusterSize);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            TestUtil.terminateInstance(instances[i]);
        }
        for (int i = InternalPartition.MAX_REPLICA_COUNT; i < clusterSize; i++) {
            HazelcastTestSupport.assertClusterSizeEventually((clusterSize - (InternalPartition.MAX_REPLICA_COUNT)), instances[i]);
            NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[i], 1);
        }
    }

    @Test
    public void lostPartitions_shouldBeAssigned_toAvailableMembers_whenMembersRemovedWhenClusterPASSIVE() {
        int clusterSize = (InternalPartition.MAX_REPLICA_COUNT) + 3;
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(new Config(), clusterSize);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], PASSIVE);
        for (int i = 0; i < (InternalPartition.MAX_REPLICA_COUNT); i++) {
            TestUtil.terminateInstance(instances[i]);
        }
        AdvancedClusterStateTest.changeClusterStateEventually(instances[((instances.length) - 1)], NO_MIGRATION);
        for (int i = InternalPartition.MAX_REPLICA_COUNT; i < clusterSize; i++) {
            HazelcastTestSupport.assertClusterSizeEventually((clusterSize - (InternalPartition.MAX_REPLICA_COUNT)), instances[i]);
            NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[i], 1);
        }
    }

    @Test
    public void lostPartitions_shouldBeAssigned_toNewMembers() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(new Config(), InternalPartition.MAX_REPLICA_COUNT);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        HazelcastInstance[] newInstances = factory.newInstances(new Config(), 3);
        for (HazelcastInstance instance : newInstances) {
            HazelcastTestSupport.assertClusterSizeEventually(((InternalPartition.MAX_REPLICA_COUNT) + (newInstances.length)), instance);
        }
        for (HazelcastInstance instance : instances) {
            TestUtil.terminateInstance(instance);
        }
        for (HazelcastInstance instance : newInstances) {
            HazelcastTestSupport.assertClusterSizeEventually(newInstances.length, instance);
            NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instance, newInstances.length);
        }
    }

    @Test
    public void backupReplication_shouldNotHappen_whenMemberLeaves() {
        Config config = newConfigWithMigrationAwareService();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(config, 3);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        TestUtil.terminateInstance(instances[0]);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[1], instances[2]);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                service.assertNoReplication();
            }
        }, 10);
    }

    @Test
    public void rebalancing_shouldHappen_whenStateBecomesActive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(new Config(), 3);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        TestUtil.terminateInstance(instances[0]);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[1], instances[2]);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], ACTIVE);
        NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[1], 2);
        NoMigrationClusterStateTest.assertAllPartitionsAreAssigned(instances[2], 2);
    }

    @Test
    public void rebalancing_shouldNotHappen_whenStateBecomesFrozen() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance[] instances = factory.newInstances(newConfigWithMigrationAwareService(), 3);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], NO_MIGRATION);
        TestUtil.terminateInstance(instances[0]);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[1], instances[2]);
        AdvancedClusterStateTest.changeClusterStateEventually(instances[1], FROZEN);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                service.assertNoReplication();
            }
        }, 10);
    }

    private static class NoReplicationService implements MigrationAwareService {
        private final AtomicReference<AssertionError> replicationRequested = new AtomicReference<AssertionError>();

        @Override
        public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
            AssertionError error = new AssertionError(("Replication requested: " + event));
            replicationRequested.compareAndSet(null, error);
            throw error;
        }

        @Override
        public void beforeMigration(PartitionMigrationEvent event) {
        }

        @Override
        public void commitMigration(PartitionMigrationEvent event) {
        }

        @Override
        public void rollbackMigration(PartitionMigrationEvent event) {
        }

        void assertNoReplication() {
            AssertionError error = replicationRequested.get();
            if (error != null) {
                throw error;
            }
        }
    }
}

