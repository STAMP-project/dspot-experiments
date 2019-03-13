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


import ClusterState.ACTIVE;
import ClusterState.NO_MIGRATION;
import GroupProperty.HEARTBEAT_INTERVAL_SECONDS;
import GroupProperty.MAX_NO_HEARTBEAT_SECONDS;
import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.cluster.impl.AdvancedClusterStateTest;
import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.ChangeLoggingRule;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MigrationInvocationsSafetyTest extends PartitionCorrectnessTestSupport {
    @ClassRule
    public static ChangeLoggingRule changeLoggingRule = new ChangeLoggingRule("log4j2-debug.xml");

    @Test
    public void members_shouldAgree_onPartitionTable_whenMasterChanges() {
        final HazelcastInstance initialMaster = factory.newHazelcastInstance();
        final HazelcastInstance nextMaster = factory.newHazelcastInstance();
        final HazelcastInstance slave1 = factory.newHazelcastInstance();
        final HazelcastInstance slave2 = factory.newHazelcastInstance();
        final HazelcastInstance slave3 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSizeEventually(5, nextMaster, slave1, slave2, slave3);
        // nextMaster & slave1 won't receive partition table updates from initialMaster.
        PacketFiltersUtil.dropOperationsBetween(initialMaster, Arrays.asList(slave1, nextMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.PARTITION_STATE_OP));
        PacketFiltersUtil.dropOperationsBetween(nextMaster, Collections.singletonList(initialMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.ASSIGN_PARTITIONS));
        PacketFiltersUtil.dropOperationsBetween(slave1, Collections.singletonList(initialMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.ASSIGN_PARTITIONS));
        MigrationInvocationsSafetyTest.ensurePartitionsInitialized(initialMaster, slave2, slave3);
        final int initialPartitionStateVersion = HazelcastTestSupport.getPartitionService(initialMaster).getPartitionStateVersion();
        Assert.assertEquals(initialPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave2).getPartitionStateVersion());
        Assert.assertEquals(initialPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave3).getPartitionStateVersion());
        Assert.assertEquals(0, HazelcastTestSupport.getPartitionService(nextMaster).getPartitionStateVersion());
        Assert.assertEquals(0, HazelcastTestSupport.getPartitionService(slave1).getPartitionStateVersion());
        PacketFiltersUtil.dropOperationsBetween(nextMaster, slave3, PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.FETCH_PARTITION_STATE));
        TestUtil.terminateInstance(initialMaster);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.assertClusterSizeEventually(4, nextMaster, slave1, slave2, slave3);
                HazelcastTestSupport.sleepSeconds(10);
                PacketFiltersUtil.resetPacketFiltersFrom(nextMaster);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int nextPartitionStateVersion = HazelcastTestSupport.getPartitionService(nextMaster).getPartitionStateVersion();
                Assert.assertThat(nextPartitionStateVersion, Matchers.greaterThan(initialPartitionStateVersion));
                Assert.assertEquals(nextPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave1).getPartitionStateVersion());
                Assert.assertEquals(nextPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave2).getPartitionStateVersion());
                Assert.assertEquals(nextPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave3).getPartitionStateVersion());
            }
        });
    }

    @Test
    public void members_shouldAgree_onPartitionTable_whenMasterChanges_and_anotherMemberCrashes() {
        final HazelcastInstance initialMaster = factory.newHazelcastInstance();
        final HazelcastInstance nextMaster = factory.newHazelcastInstance();
        final HazelcastInstance slave1 = factory.newHazelcastInstance();
        final HazelcastInstance slave2 = factory.newHazelcastInstance();
        final HazelcastInstance slave3 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSizeEventually(5, nextMaster, slave1, slave2, slave3);
        // nextMaster & slave1 won't receive partition table updates from initialMaster.
        PacketFiltersUtil.dropOperationsBetween(initialMaster, Arrays.asList(slave1, nextMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.PARTITION_STATE_OP));
        PacketFiltersUtil.dropOperationsBetween(nextMaster, Collections.singletonList(initialMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.ASSIGN_PARTITIONS));
        PacketFiltersUtil.dropOperationsBetween(slave1, Collections.singletonList(initialMaster), PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.ASSIGN_PARTITIONS));
        MigrationInvocationsSafetyTest.ensurePartitionsInitialized(initialMaster, slave2, slave3);
        final int initialPartitionStateVersion = HazelcastTestSupport.getPartitionService(initialMaster).getPartitionStateVersion();
        Assert.assertEquals(initialPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave2).getPartitionStateVersion());
        Assert.assertEquals(initialPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave3).getPartitionStateVersion());
        Assert.assertEquals(0, HazelcastTestSupport.getPartitionService(nextMaster).getPartitionStateVersion());
        Assert.assertEquals(0, HazelcastTestSupport.getPartitionService(slave1).getPartitionStateVersion());
        PacketFiltersUtil.dropOperationsBetween(nextMaster, slave3, PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.FETCH_PARTITION_STATE));
        TestUtil.terminateInstance(initialMaster);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.assertClusterSizeEventually(4, nextMaster, slave1, slave2, slave3);
                HazelcastTestSupport.sleepSeconds(10);
                TestUtil.terminateInstance(slave3);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int nextPartitionStateVersion = HazelcastTestSupport.getPartitionService(nextMaster).getPartitionStateVersion();
                Assert.assertThat(nextPartitionStateVersion, Matchers.greaterThan(initialPartitionStateVersion));
                Assert.assertEquals(nextPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave1).getPartitionStateVersion());
                Assert.assertEquals(nextPartitionStateVersion, HazelcastTestSupport.getPartitionService(slave2).getPartitionStateVersion());
            }
        });
    }

    @Test
    public void partitionState_shouldNotBeSafe_duringPartitionTableFetch_whenMasterLeaves() {
        partitionState_shouldNotBeSafe_duringPartitionTableFetch_whenMasterChanges(false);
    }

    @Test
    public void partitionState_shouldNotBeSafe_duringPartitionTableFetch_whenLiteMasterLeaves() {
        partitionState_shouldNotBeSafe_duringPartitionTableFetch_whenMasterChanges(true);
    }

    @Test
    public void migrationCommit_shouldBeRetried_whenTargetNotResponds() throws Exception {
        Config config = getConfig(true, true).setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "5").setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "3000");
        final HazelcastInstance master = factory.newHazelcastInstance(config);
        final HazelcastInstance slave1 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1, slave2);
        HazelcastTestSupport.warmUpPartitions(master, slave1, slave2);
        fillData(master);
        assertSizeAndDataEventually();
        // prevent migrations before adding migration listeners when slave3 joins the cluster
        AdvancedClusterStateTest.changeClusterStateEventually(master, NO_MIGRATION);
        final HazelcastInstance slave3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2);
        // set migration listener to drop migration commit response after a migration to slave3 is completed
        setMigrationListenerToDropCommitResponse(master, slave3);
        // enable migrations
        AdvancedClusterStateTest.changeClusterStateEventually(master, ACTIVE);
        // wait for retry of migration commit
        HazelcastTestSupport.sleepSeconds(10);
        // reset filters to allow sending migration commit response
        PacketFiltersUtil.resetPacketFiltersFrom(slave3);
        HazelcastTestSupport.waitAllForSafeState(master, slave1, slave2, slave3);
        final PartitionTableView masterPartitionTable = HazelcastTestSupport.getPartitionService(master).createPartitionTableView();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave1).createPartitionTableView());
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave2).createPartitionTableView());
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave3).createPartitionTableView());
            }
        });
        assertSizeAndData();
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(master);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave1);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave2);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave3);
    }

    @Test
    public void migrationCommit_shouldRollback_whenTargetCrashes() throws Exception {
        Config config = getConfig(true, true).setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "5").setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "3000");
        final HazelcastInstance master = factory.newHazelcastInstance(config);
        final HazelcastInstance slave1 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1, slave2);
        HazelcastTestSupport.warmUpPartitions(master, slave1, slave2);
        fillData(master);
        assertSizeAndDataEventually();
        // prevent migrations before adding migration listeners when slave3 joins the cluster
        AdvancedClusterStateTest.changeClusterStateEventually(master, NO_MIGRATION);
        final HazelcastInstance slave3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2);
        // set migration listener to drop migration commit response after a migration to slave3 is completed
        setMigrationListenerToDropCommitResponse(master, slave3);
        // enable migrations
        AdvancedClusterStateTest.changeClusterStateEventually(master, ACTIVE);
        // wait for retry of migration commit
        HazelcastTestSupport.sleepSeconds(10);
        TestUtil.terminateInstance(slave3);
        HazelcastTestSupport.waitAllForSafeState(master, slave1, slave2);
        final PartitionTableView masterPartitionTable = HazelcastTestSupport.getPartitionService(master).createPartitionTableView();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave1).createPartitionTableView());
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave2).createPartitionTableView());
            }
        });
        assertSizeAndData();
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(master);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave1);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave2);
    }

    @Test
    public void promotionCommit_shouldBeRetried_whenTargetNotResponds() throws Exception {
        Config config = getConfig(true, true).setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "5").setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "3000");
        final HazelcastInstance master = factory.newHazelcastInstance(config);
        final HazelcastInstance slave1 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave2 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2, slave3);
        HazelcastTestSupport.warmUpPartitions(master, slave1, slave2, slave3);
        fillData(master);
        assertSizeAndDataEventually();
        // reject promotion commits from master to prevent promotions complete when slave3 leaves the cluster
        PacketFiltersUtil.rejectOperationsFrom(master, PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.PROMOTION_COMMIT));
        TestUtil.terminateInstance(slave3);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1, slave2);
        // set migration listener to drop promotion commit response after a promotion is completed
        setMigrationListenerToPromotionResponse(master, slave2);
        // allow promotion commits
        PacketFiltersUtil.resetPacketFiltersFrom(master);
        HazelcastTestSupport.sleepSeconds(10);
        PacketFiltersUtil.resetPacketFiltersFrom(slave2);
        HazelcastTestSupport.waitAllForSafeState(master, slave1, slave2);
        final PartitionTableView masterPartitionTable = HazelcastTestSupport.getPartitionService(master).createPartitionTableView();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave1).createPartitionTableView());
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave2).createPartitionTableView());
            }
        });
        assertSizeAndData();
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(master);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave1);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave2);
    }

    @Test
    public void promotionCommit_shouldRollback_whenTargetCrashes() throws Exception {
        Config config = getConfig(true, true).setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "5").setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "3000");
        final HazelcastInstance master = factory.newHazelcastInstance(config);
        final HazelcastInstance slave1 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave2 = factory.newHazelcastInstance(config);
        final HazelcastInstance slave3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2, slave3);
        HazelcastTestSupport.warmUpPartitions(master, slave1, slave2, slave3);
        fillData(master);
        assertSizeAndDataEventually();
        // reject promotion commits from master to prevent promotions complete when slave3 leaves the cluster
        PacketFiltersUtil.rejectOperationsFrom(master, PartitionDataSerializerHook.F_ID, Collections.singletonList(PartitionDataSerializerHook.PROMOTION_COMMIT));
        TestUtil.terminateInstance(slave3);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1, slave2);
        // set migration listener to drop promotion commit response after a promotion is completed
        setMigrationListenerToPromotionResponse(master, slave2);
        // allow promotion commits
        PacketFiltersUtil.resetPacketFiltersFrom(master);
        HazelcastTestSupport.sleepSeconds(10);
        TestUtil.terminateInstance(slave2);
        HazelcastTestSupport.waitAllForSafeState(master, slave1);
        final PartitionTableView masterPartitionTable = HazelcastTestSupport.getPartitionService(master).createPartitionTableView();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(masterPartitionTable, HazelcastTestSupport.getPartitionService(slave1).createPartitionTableView());
            }
        });
        assertSizeAndData();
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(master);
        MigrationInvocationsSafetyTest.assertNoDuplicateMigrations(slave1);
    }
}

