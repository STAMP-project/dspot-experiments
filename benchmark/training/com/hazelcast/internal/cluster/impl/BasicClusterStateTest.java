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
import EventServiceImpl.EVENT_SYNC_FREQUENCY_PROP;
import GroupProperty.PARTITION_MAX_PARALLEL_REPLICATIONS;
import GroupProperty.PARTITION_MIGRATION_INTERVAL;
import InternalPartitionService.SERVICE_NAME;
import TransactionType.ONE_PHASE;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.IndeterminateOperationStateExceptionTest;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionOptions;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static com.hazelcast.partition.IndeterminateOperationStateExceptionTest.BackupOperation.EXECUTION_DONE;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BasicClusterStateTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void clusterState_isActive_whenInstancesStarted() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.assertClusterState(ACTIVE, instances);
    }

    @Test
    public void joinNotAllowed_whenClusterState_isFrozen() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(FROZEN);
        expectedException.expect(IllegalStateException.class);
        factory.newHazelcastInstance();
        Assert.fail(("New node should not start when cluster state is: " + (ClusterState.FROZEN)));
    }

    @Test
    public void joinNotAllowed_whenClusterState_isPassive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(PASSIVE);
        expectedException.expect(IllegalStateException.class);
        factory.newHazelcastInstance();
        Assert.fail(("New node should not start when cluster state is: " + (ClusterState.PASSIVE)));
    }

    @Test
    public void joinAllowed_whenClusterState_isNoMigration() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        instances[((instances.length) - 1)].getCluster().changeClusterState(NO_MIGRATION);
        HazelcastInstance hz = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSize(4, hz);
    }

    @Test
    public void joinAllowed_whenKnownMemberReJoins_whenClusterState_isFrozen() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastInstance hz1 = instances[((instances.length) - 1)];
        hz1.getCluster().changeClusterState(FROZEN);
        HazelcastInstance hz2 = instances[0];
        Address address = HazelcastTestSupport.getNode(hz2).getThisAddress();
        hz2.getLifecycleService().terminate();
        hz2 = factory.newHazelcastInstance(address);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2);
        Assert.assertEquals(NodeState.ACTIVE, HazelcastTestSupport.getNode(hz2).getState());
    }

    @Test
    public void joinAllowed_whenKnownMemberReJoins_whenClusterState_isPassive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = new HazelcastInstance[3];
        for (int i = 0; i < 3; i++) {
            instances[i] = factory.newHazelcastInstance();
        }
        HazelcastInstance hz1 = instances[((instances.length) - 1)];
        hz1.getCluster().changeClusterState(PASSIVE);
        HazelcastInstance hz2 = instances[0];
        Address address = HazelcastTestSupport.getNode(hz2).getThisAddress();
        hz2.getLifecycleService().terminate();
        hz2 = factory.newHazelcastInstance(address);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2);
        Assert.assertEquals(NodeState.PASSIVE, HazelcastTestSupport.getNode(hz2).getState());
    }

    @Test
    public void changeClusterState_toNoMigration_shouldFail_whilePartitionsMigrating() {
        Config config = new Config();
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "10");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        expectedException.expect(IllegalStateException.class);
        hz2.getCluster().changeClusterState(NO_MIGRATION);
    }

    @Test
    public void changeClusterState_toFrozen_shouldFail_whilePartitionsMigrating() {
        Config config = new Config();
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "10");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        expectedException.expect(IllegalStateException.class);
        hz2.getCluster().changeClusterState(FROZEN);
    }

    @Test
    public void changeClusterState_toPassive_shouldFail_whilePartitionsMigrating() {
        Config config = new Config();
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "10");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(hz);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz);
        expectedException.expect(IllegalStateException.class);
        hz2.getCluster().changeClusterState(PASSIVE);
    }

    @Test
    public void changeClusterState_toActive_isAllowed_whileReplicationInProgress() {
        Config config = new Config();
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "10");
        config.setProperty(PARTITION_MAX_PARALLEL_REPLICATIONS.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        HazelcastTestSupport.warmUpPartitions(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        TestUtil.terminateInstance(hz1);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz2, hz3);
        // try until member is removed and partition-service takes care of removal
        AdvancedClusterStateTest.changeClusterStateEventually(hz3, ACTIVE);
        HazelcastTestSupport.assertClusterState(ACTIVE, hz2, hz3);
    }

    @Test
    public void changeClusterState_toPassive_isAllowed_whileReplicationInProgress() {
        Config config = new Config();
        config.setProperty(PARTITION_MIGRATION_INTERVAL.getName(), "10");
        config.setProperty(PARTITION_MAX_PARALLEL_REPLICATIONS.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance hz1 = instances[0];
        HazelcastInstance hz2 = instances[1];
        HazelcastInstance hz3 = instances[2];
        HazelcastTestSupport.warmUpPartitions(instances);
        AdvancedClusterStateTest.changeClusterStateEventually(hz2, FROZEN);
        TestUtil.terminateInstance(hz1);
        HazelcastTestSupport.assertClusterSizeEventually(2, hz2, hz3);
        // try until member is removed and partition-service takes care of removal
        AdvancedClusterStateTest.changeClusterStateEventually(hz3, PASSIVE);
        HazelcastTestSupport.assertClusterState(PASSIVE, hz2, hz3);
    }

    @Test
    public void changeClusterState_toFrozen_makesNodeStates_Active() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(FROZEN);
        BasicClusterStateTest.assertNodeState(instances, NodeState.ACTIVE);
    }

    @Test
    public void changeClusterState_toPassive_makesNodeStates_Passive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(PASSIVE);
        BasicClusterStateTest.assertNodeState(instances, NodeState.PASSIVE);
    }

    @Test
    public void changeClusterState_fromPassiveToActive_makesNodeStates_Active() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(PASSIVE);
        hz.getCluster().changeClusterState(ACTIVE);
        BasicClusterStateTest.assertNodeState(instances, NodeState.ACTIVE);
    }

    @Test
    public void changeClusterState_fromPassiveToFrozen_makesNodeStates_Active() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastInstance hz = instances[((instances.length) - 1)];
        hz.getCluster().changeClusterState(PASSIVE);
        hz.getCluster().changeClusterState(ACTIVE);
        BasicClusterStateTest.assertNodeState(instances, NodeState.ACTIVE);
    }

    @Test
    public void changeClusterState_transaction_mustBe_TWO_PHASE() {
        HazelcastInstance hz = createHazelcastInstance();
        TransactionOptions options = new TransactionOptions().setTransactionType(ONE_PHASE);
        expectedException.expect(IllegalArgumentException.class);
        hz.getCluster().changeClusterState(FROZEN, options);
    }

    @Test
    public void readOperations_succeed_whenClusterState_passive() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastInstance hz = instances[((instances.length) - 1)];
        Map map = hz.getMap(HazelcastTestSupport.randomMapName());
        AdvancedClusterStateTest.changeClusterStateEventually(hz, PASSIVE);
        map.get(1);
    }

    @Test(timeout = 300000)
    public void test_noMigration_whenNodeLeaves_onClusterState_FROZEN() {
        testNoMigrationWhenNodeLeaves(FROZEN);
    }

    @Test(timeout = 300000)
    public void test_noMigration_whenNodeLeaves_onClusterState_PASSIVE() {
        testNoMigrationWhenNodeLeaves(PASSIVE);
    }

    @Test
    public void test_listener_registration_whenClusterState_PASSIVE() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance master = factory.newHazelcastInstance();
        final HazelcastInstance other = factory.newHazelcastInstance();
        AdvancedClusterStateTest.changeClusterStateEventually(master, PASSIVE);
        master.getPartitionService().addPartitionLostListener(Mockito.mock(PartitionLostListener.class));
        // Expected = 7 -> 1 added + 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers * instances
        assertRegistrationsSizeEventually(master, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 7);
        assertRegistrationsSizeEventually(other, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 7);
    }

    @Test
    public void test_listener_deregistration_whenClusterState_PASSIVE() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance master = factory.newHazelcastInstance();
        final HazelcastInstance other = factory.newHazelcastInstance();
        final String registrationId = master.getPartitionService().addPartitionLostListener(Mockito.mock(PartitionLostListener.class));
        // Expected = 7 -> 1 added + 1 from {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers * instances
        assertRegistrationsSizeEventually(master, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 7);
        assertRegistrationsSizeEventually(other, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 7);
        AdvancedClusterStateTest.changeClusterStateEventually(master, PASSIVE);
        master.getPartitionService().removePartitionLostListener(registrationId);
        // Expected = 6 -> see {@link com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService}
        // + 2 from map and cache ExpirationManagers* instances
        assertRegistrationsSizeEventually(other, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 6);
        assertRegistrationsSizeEventually(master, SERVICE_NAME, InternalPartitionService.PARTITION_LOST_EVENT_TOPIC, 6);
    }

    @Test
    public void test_eventsDispatched_whenClusterState_PASSIVE() {
        System.setProperty(EVENT_SYNC_FREQUENCY_PROP, "1");
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance master = factory.newHazelcastInstance();
        final HazelcastInstance other = factory.newHazelcastInstance();
        AdvancedClusterStateTest.changeClusterStateEventually(master, PASSIVE);
        master.getMap(HazelcastTestSupport.randomMapName());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, HazelcastTestSupport.getNodeEngineImpl(other).getProxyService().getProxyCount());
            }
        });
    }

    @Test
    public void pendingInvocations_shouldBeNotified_whenMemberLeft_whenClusterState_PASSIVE() throws Exception {
        pendingInvocations_shouldBeNotified_whenMemberLeft_whenClusterState_doesNotAllowJoin(PASSIVE);
    }

    @Test
    public void pendingInvocations_shouldBeNotified_whenMemberLeft_whenClusterState_FROZEN() throws Exception {
        pendingInvocations_shouldBeNotified_whenMemberLeft_whenClusterState_doesNotAllowJoin(FROZEN);
    }

    @Test
    public void backupOperation_shouldBeAllowed_whenClusterState_PASSIVE() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2);
        int partitionId = HazelcastTestSupport.getPartitionId(hz2);
        AdvancedClusterStateTest.changeClusterStateEventually(hz1, PASSIVE);
        InternalCompletableFuture future = HazelcastTestSupport.getOperationService(hz1).invokeOnPartition(null, new BasicClusterStateTest.PrimaryAllowedDuringPassiveStateOperation(), partitionId);
        future.join();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(hz1.getUserContext().containsKey(EXECUTION_DONE));
            }
        });
    }

    private static class PrimaryAllowedDuringPassiveStateOperation extends Operation implements BackupAwareOperation , AllowedDuringPassiveState {
        @Override
        public void run() throws Exception {
        }

        @Override
        public boolean shouldBackup() {
            return true;
        }

        @Override
        public int getSyncBackupCount() {
            return 1;
        }

        @Override
        public int getAsyncBackupCount() {
            return 0;
        }

        @Override
        public Operation getBackupOperation() {
            return new IndeterminateOperationStateExceptionTest.BackupOperation();
        }
    }
}

