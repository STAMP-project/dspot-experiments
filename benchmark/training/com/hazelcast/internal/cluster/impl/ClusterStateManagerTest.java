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


import com.hazelcast.cluster.ClusterState;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.util.LockGuard;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.util.Clock;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterStateManagerTest {
    private static final String TXN = "txn";

    private static final String ANOTHER_TXN = "another-txn";

    private static final MemberVersion CURRENT_NODE_VERSION = MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion());

    private static final Version CURRENT_CLUSTER_VERSION = Version.of(BuildInfoProvider.getBuildInfo().getVersion());

    private static final int MEMBERLIST_VERSION = 1;

    private static final int PARTITION_VERSION = 1;

    private final Node node = Mockito.mock(Node.class);

    private final InternalPartitionService partitionService = Mockito.mock(InternalPartitionService.class);

    private final MembershipManager membershipManager = Mockito.mock(MembershipManager.class);

    private final ClusterServiceImpl clusterService = Mockito.mock(ClusterServiceImpl.class);

    private final Lock lock = Mockito.mock(Lock.class);

    private ClusterStateManager clusterStateManager;

    @Test
    public void test_defaultState() {
        Assert.assertEquals(ClusterState.ACTIVE, clusterStateManager.getState());
    }

    @Test
    public void test_initialClusterState_ACTIVE() {
        clusterStateManager.initialClusterState(ClusterState.ACTIVE, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Assert.assertEquals(ClusterState.ACTIVE, clusterStateManager.getState());
        Assert.assertEquals(ClusterStateManagerTest.CURRENT_CLUSTER_VERSION, clusterStateManager.getClusterVersion());
    }

    @Test
    public void test_initialClusterState_FROZEN() {
        clusterStateManager.initialClusterState(ClusterState.FROZEN, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Assert.assertEquals(ClusterState.FROZEN, clusterStateManager.getState());
        Assert.assertEquals(ClusterStateManagerTest.CURRENT_CLUSTER_VERSION, clusterStateManager.getClusterVersion());
    }

    @Test
    public void test_initialClusterState_PASSIVE() {
        clusterStateManager.initialClusterState(ClusterState.PASSIVE, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Assert.assertEquals(ClusterState.PASSIVE, clusterStateManager.getState());
        Assert.assertEquals(ClusterStateManagerTest.CURRENT_CLUSTER_VERSION, clusterStateManager.getClusterVersion());
    }

    @Test
    public void test_initialClusterState_rejected() {
        clusterStateManager.initialClusterState(ClusterState.FROZEN, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        clusterStateManager.initialClusterState(ClusterState.ACTIVE, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Assert.assertEquals(ClusterState.FROZEN, clusterStateManager.getState());
        Assert.assertEquals(ClusterStateManagerTest.CURRENT_CLUSTER_VERSION, clusterStateManager.getClusterVersion());
    }

    @Test(expected = NullPointerException.class)
    public void test_lockClusterState_nullState() throws Exception {
        Address initiator = newAddress();
        clusterStateManager.lockClusterState(null, initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test(expected = NullPointerException.class)
    public void test_lockClusterState_nullInitiator() throws Exception {
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), null, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test(expected = NullPointerException.class)
    public void test_lockClusterState_nullTransactionId() throws Exception {
        Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, null, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_lockClusterState_nonPositiveLeaseTime() throws Exception {
        Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, (-1000), ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test
    public void test_lockClusterState_success() throws Exception {
        Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        assertLockedBy(initiator);
    }

    @Test(expected = TransactionException.class)
    public void test_lockClusterState_fail() throws Exception {
        Address initiator = newAddress();
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.FROZEN);
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.ANOTHER_TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test(expected = IllegalStateException.class)
    public void test_lockClusterState_forFrozenState_whenHasOnGoingMigration() throws Exception {
        Mockito.when(partitionService.hasOnGoingMigrationLocal()).thenReturn(true);
        Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test(expected = IllegalStateException.class)
    public void test_lockClusterState_forActiveState_whenHasOnGoingMigration() throws Exception {
        Mockito.when(partitionService.hasOnGoingMigrationLocal()).thenReturn(true);
        clusterStateManager.initialClusterState(ClusterState.FROZEN, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Address initiator = newAddress();
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.ACTIVE);
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        assertLockedBy(initiator);
    }

    @Test(expected = IllegalStateException.class)
    public void test_lockClusterState_forPassiveState_whenHasOnGoingMigration() throws Exception {
        Mockito.when(partitionService.hasOnGoingMigrationLocal()).thenReturn(true);
        clusterStateManager.initialClusterState(ClusterState.FROZEN, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        Address initiator = newAddress();
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.PASSIVE);
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        assertLockedBy(initiator);
    }

    @Test(expected = NullPointerException.class)
    public void test_unlockClusterState_nullTransactionId() throws Exception {
        clusterStateManager.rollbackClusterState(null);
    }

    @Test
    public void test_unlockClusterState_fail_whenNotLocked() throws Exception {
        Assert.assertFalse(clusterStateManager.rollbackClusterState(ClusterStateManagerTest.TXN));
    }

    @Test
    public void test_unlockClusterState_fail_whenLockedByElse() throws Exception {
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        Assert.assertFalse(clusterStateManager.rollbackClusterState(ClusterStateManagerTest.ANOTHER_TXN));
    }

    @Test(expected = IllegalStateException.class)
    public void test_lockClusterState_fail_withDifferentPartitionStateVersions() throws Exception {
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ((ClusterStateManagerTest.PARTITION_VERSION) + 1));
    }

    @Test(expected = IllegalStateException.class)
    public void test_lockClusterState_fail_withDifferentMemberListVersions() throws Exception {
        clusterStateManager.clusterVersion = Versions.CURRENT_CLUSTER_VERSION;
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN, 1000, ((ClusterStateManagerTest.MEMBERLIST_VERSION) - 1), ClusterStateManagerTest.PARTITION_VERSION);
    }

    @Test
    public void test_unlockClusterState_success() throws Exception {
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN, 1000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        Assert.assertTrue(clusterStateManager.rollbackClusterState(ClusterStateManagerTest.TXN));
    }

    @Test
    public void test_lockClusterState_getLockExpiryTime() throws Exception {
        final Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, TimeUnit.DAYS.toMillis(1), ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        final LockGuard stateLock = clusterStateManager.getStateLock();
        Assert.assertTrue((((Clock.currentTimeMillis()) + (TimeUnit.HOURS.toMillis(12))) < (stateLock.getLockExpiryTime())));
    }

    @Test
    public void test_lockClusterState_extendLease() throws Exception {
        final Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, 10000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, TimeUnit.DAYS.toMillis(1), ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        final LockGuard stateLock = clusterStateManager.getStateLock();
        Assert.assertTrue((((Clock.currentTimeMillis()) + (TimeUnit.HOURS.toMillis(12))) < (stateLock.getLockExpiryTime())));
    }

    @Test
    public void test_lockClusterState_expiry() throws Exception {
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN, 1, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final LockGuard stateLock = clusterStateManager.getStateLock();
                Assert.assertFalse(stateLock.isLocked());
                Assert.assertEquals(ClusterState.ACTIVE, clusterStateManager.getState());
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void test_changeLocalClusterState_nullState() throws Exception {
        clusterStateManager.commitClusterState(null, newAddress(), ClusterStateManagerTest.TXN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_changeLocalClusterState_IN_TRANSITION() throws Exception {
        clusterStateManager.commitClusterState(ClusterStateChange.from(ClusterState.IN_TRANSITION), newAddress(), ClusterStateManagerTest.TXN);
    }

    @Test(expected = NullPointerException.class)
    public void test_changeLocalClusterState_nullTransactionId() throws Exception {
        clusterStateManager.commitClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), null);
    }

    @Test(expected = TransactionException.class)
    public void test_changeLocalClusterState_fail_notLocked() throws Exception {
        clusterStateManager.commitClusterState(ClusterStateChange.from(ClusterState.FROZEN), newAddress(), ClusterStateManagerTest.TXN);
    }

    @Test(expected = TransactionException.class)
    public void test_changeLocalClusterState_fail_whenLockedByElse() throws Exception {
        final Address initiator = newAddress();
        clusterStateManager.lockClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.TXN, 10000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.commitClusterState(ClusterStateChange.from(ClusterState.FROZEN), initiator, ClusterStateManagerTest.ANOTHER_TXN);
    }

    @Test
    public void test_changeLocalClusterState_success() throws Exception {
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.FROZEN);
        final Address initiator = newAddress();
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 10000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.commitClusterState(newState, initiator, ClusterStateManagerTest.TXN);
        Assert.assertEquals(newState.getNewState(), clusterStateManager.getState());
        final LockGuard stateLock = clusterStateManager.getStateLock();
        Assert.assertFalse(stateLock.isLocked());
    }

    @Test
    public void changeLocalClusterState_shouldChangeNodeStateToShuttingDown_whenStateBecomes_PASSIVE() throws Exception {
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.PASSIVE);
        final Address initiator = newAddress();
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 10000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.commitClusterState(newState, initiator, ClusterStateManagerTest.TXN);
        Assert.assertEquals(newState.getNewState(), clusterStateManager.getState());
        Mockito.verify(node, Mockito.times(1)).changeNodeStateToPassive();
    }

    @Test
    public void changeLocalClusterState_shouldRemoveMembersDeadWhileFrozen_whenStateBecomes_ACTIVE() throws Exception {
        final ClusterStateChange newState = ClusterStateChange.from(ClusterState.ACTIVE);
        final Address initiator = newAddress();
        clusterStateManager.initialClusterState(ClusterState.FROZEN, ClusterStateManagerTest.CURRENT_CLUSTER_VERSION);
        clusterStateManager.lockClusterState(newState, initiator, ClusterStateManagerTest.TXN, 10000, ClusterStateManagerTest.MEMBERLIST_VERSION, ClusterStateManagerTest.PARTITION_VERSION);
        clusterStateManager.commitClusterState(newState, initiator, ClusterStateManagerTest.TXN);
        Assert.assertEquals(newState.getNewState(), clusterStateManager.getState());
        Mockito.verify(membershipManager, Mockito.times(1)).removeAllMissingMembers();
    }
}

