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


import GroupProperty.MASTERSHIP_CLAIM_TIMEOUT_SECONDS;
import GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetectorType;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MembershipFailureTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public ClusterFailureDetectorType failureDetectorType;

    private TestHazelcastInstanceFactory factory;

    @Test
    public void slave_shutdown() {
        slave_goesDown(false);
    }

    @Test
    public void slave_crash() {
        slave_goesDown(true);
    }

    @Test
    public void master_shutdown() {
        master_goesDown(false);
    }

    @Test
    public void master_crash() {
        master_goesDown(true);
    }

    @Test
    public void masterAndMasterCandidate_crashSequentially() {
        masterAndMasterCandidate_crash(false);
    }

    @Test
    public void masterAndMasterCandidate_crashSimultaneously() {
        masterAndMasterCandidate_crash(true);
    }

    @Test
    public void slaveCrash_duringMastershipClaim() {
        HazelcastInstance master = newHazelcastInstance();
        HazelcastInstance masterCandidate = newHazelcastInstance();
        HazelcastInstance slave1 = newHazelcastInstance();
        HazelcastInstance slave2 = newHazelcastInstance();
        HazelcastTestSupport.assertClusterSize(4, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(4, masterCandidate, slave1);
        // drop FETCH_MEMBER_LIST_STATE packets to block mastership claim process
        PacketFiltersUtil.dropOperationsBetween(masterCandidate, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE));
        TestUtil.terminateInstance(master);
        final ClusterServiceImpl clusterService = HazelcastTestSupport.getNode(masterCandidate).getClusterService();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(clusterService.getClusterJoinManager().isMastershipClaimInProgress());
            }
        });
        HazelcastTestSupport.sleepSeconds(3);
        TestUtil.terminateInstance(slave1);
        HazelcastTestSupport.assertClusterSizeEventually(2, masterCandidate);
        HazelcastTestSupport.assertClusterSizeEventually(2, slave2);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(masterCandidate), masterCandidate, slave2);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(masterCandidate), MembershipUpdateTest.getMemberMap(slave2));
    }

    @Test
    public void masterCandidateCrash_duringMastershipClaim() {
        HazelcastInstance master = newHazelcastInstance();
        HazelcastInstance masterCandidate = newHazelcastInstance();
        HazelcastInstance slave1 = newHazelcastInstance();
        HazelcastInstance slave2 = newHazelcastInstance();
        HazelcastTestSupport.assertClusterSize(4, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(4, masterCandidate, slave1);
        // drop FETCH_MEMBER_LIST_STATE packets to block mastership claim process
        PacketFiltersUtil.dropOperationsBetween(masterCandidate, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE));
        TestUtil.terminateInstance(master);
        final ClusterServiceImpl clusterService = HazelcastTestSupport.getNode(masterCandidate).getClusterService();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(clusterService.getClusterJoinManager().isMastershipClaimInProgress());
            }
        });
        HazelcastTestSupport.sleepSeconds(3);
        TestUtil.terminateInstance(masterCandidate);
        HazelcastTestSupport.assertClusterSizeEventually(2, slave1, slave2);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(slave1), slave1, slave2);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(slave1), MembershipUpdateTest.getMemberMap(slave2));
    }

    @Test
    public void slave_heartbeat_timeout() {
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "15").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        PacketFiltersUtil.dropOperationsFrom(slave2, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        HazelcastTestSupport.assertClusterSizeEventually(2, master, slave1);
        HazelcastTestSupport.assertClusterSizeEventually(1, slave2);
    }

    @Test
    public void master_heartbeat_timeout() {
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "15").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "3");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        PacketFiltersUtil.dropOperationsFrom(master, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT_COMPLAINT));
        PacketFiltersUtil.dropOperationsFrom(slave2, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT_COMPLAINT));
        HazelcastTestSupport.assertClusterSizeEventually(1, master);
        HazelcastTestSupport.assertClusterSizeEventually(2, slave1, slave2);
    }

    @Test
    public void heartbeat_not_sent_to_suspected_member() {
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "10").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        // prevent heartbeat from master to slave to prevent suspect to be removed
        PacketFiltersUtil.dropOperationsBetween(master, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        HazelcastTestSupport.suspectMember(slave1, master);
        HazelcastTestSupport.assertClusterSizeEventually(2, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(1, slave1);
    }

    @Test
    public void slave_heartbeat_removes_suspicion() {
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "10").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        final HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        PacketFiltersUtil.dropOperationsBetween(slave2, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        final MembershipManager membershipManager = HazelcastTestSupport.getNode(slave1).getClusterService().getMembershipManager();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(membershipManager.isMemberSuspected(HazelcastTestSupport.getAddress(slave2)));
            }
        });
        PacketFiltersUtil.resetPacketFiltersFrom(slave2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(membershipManager.isMemberSuspected(HazelcastTestSupport.getAddress(slave2)));
            }
        });
    }

    @Test
    public void slave_receives_member_list_from_non_master() {
        String infiniteTimeout = Integer.toString(Integer.MAX_VALUE);
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), infiniteTimeout).setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastInstance slave3 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(4, master, slave3);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2);
        PacketFiltersUtil.dropOperationsFrom(master, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(slave2, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(slave3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        HazelcastTestSupport.suspectMember(slave2, master);
        HazelcastTestSupport.suspectMember(slave2, slave1);
        HazelcastTestSupport.suspectMember(slave3, master);
        HazelcastTestSupport.suspectMember(slave3, slave1);
        HazelcastTestSupport.assertClusterSizeEventually(2, slave2, slave3);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(slave2), MembershipUpdateTest.getMemberMap(slave3));
        HazelcastTestSupport.assertClusterSizeEventually(2, master, slave1);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(master), MembershipUpdateTest.getMemberMap(slave1));
    }

    @Test
    public void master_candidate_has_stale_member_list() {
        Config config = new Config().setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(2, master);
        HazelcastTestSupport.assertClusterSize(2, slave1);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        HazelcastTestSupport.assertClusterSize(3, slave2);
        PacketFiltersUtil.rejectOperationsBetween(master, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        HazelcastInstance slave3 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(4, slave3);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave2);
        HazelcastTestSupport.assertClusterSize(3, slave1);
        master.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1, slave2, slave3);
        Address newMasterAddress = HazelcastTestSupport.getAddress(slave1);
        HazelcastTestSupport.assertMasterAddress(newMasterAddress, slave1, slave2, slave3);
    }

    @Test
    public void master_candidate_discovers_member_list_recursively() {
        Config config = new Config().setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master, slave2);
        HazelcastTestSupport.assertClusterSizeEventually(3, slave1);
        // master, slave1, slave2
        PacketFiltersUtil.rejectOperationsBetween(master, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        HazelcastInstance slave3 = newHazelcastInstance(config);
        // master, slave1, slave2, slave3
        HazelcastTestSupport.assertClusterSizeEventually(4, slave3, slave2);
        HazelcastTestSupport.assertClusterSize(3, slave1);
        PacketFiltersUtil.rejectOperationsBetween(master, Arrays.asList(slave1, slave2), ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        HazelcastInstance slave4 = newHazelcastInstance(config);
        // master, slave1, slave2, slave3, slave4
        HazelcastTestSupport.assertClusterSizeEventually(5, slave4, slave3);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave2);
        HazelcastTestSupport.assertClusterSize(3, slave1);
        PacketFiltersUtil.rejectOperationsBetween(master, Arrays.asList(slave1, slave2, slave3), ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        HazelcastInstance slave5 = newHazelcastInstance(config);
        // master, slave1, slave2, slave3, slave4, slave5
        HazelcastTestSupport.assertClusterSize(6, slave5);
        HazelcastTestSupport.assertClusterSizeEventually(6, slave4);
        HazelcastTestSupport.assertClusterSizeEventually(5, slave3);
        HazelcastTestSupport.assertClusterSizeEventually(4, slave2);
        HazelcastTestSupport.assertClusterSize(3, slave1);
        master.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(5, slave1, slave2, slave3);
        Address newMasterAddress = HazelcastTestSupport.getAddress(slave1);
        HazelcastTestSupport.assertMasterAddress(newMasterAddress, slave2, slave3, slave4, slave5);
    }

    @Test
    public void master_candidate_and_new_member_splits_on_master_failure() {
        Config config = new Config().setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "15").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance master = newHazelcastInstance(config);
        HazelcastInstance slave1 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(2, master, slave1);
        PacketFiltersUtil.rejectOperationsBetween(master, slave1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        HazelcastInstance slave2 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, master);
        HazelcastTestSupport.assertClusterSize(3, slave2);
        HazelcastTestSupport.assertClusterSize(2, slave1);
        master.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(1, slave1);
        HazelcastTestSupport.assertClusterSizeEventually(1, slave2);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(slave1), slave1);
        HazelcastTestSupport.assertMasterAddress(HazelcastTestSupport.getAddress(slave2), slave2);
    }

    @Test
    public void slave_splits_and_eventually_merges_back() {
        Config config = new Config();
        config.setProperty(GroupProperty.MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "5").setProperty(GroupProperty.MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "5");
        final HazelcastInstance member1 = newHazelcastInstance(config);
        final HazelcastInstance member2 = newHazelcastInstance(config);
        final HazelcastInstance member3 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(3, member1, member3);
        HazelcastTestSupport.assertClusterSizeEventually(3, member2);
        final CountDownLatch mergeLatch = new CountDownLatch(1);
        member3.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (LifecycleState.MERGED)) {
                    mergeLatch.countDown();
                }
            }
        });
        // prevent heartbeats to member3 to prevent suspicion to be removed
        PacketFiltersUtil.dropOperationsBetween(member1, member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsBetween(member2, member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.SPLIT_BRAIN_MERGE_VALIDATION));
        HazelcastTestSupport.suspectMember(member3, member1);
        HazelcastTestSupport.suspectMember(member3, member2);
        HazelcastTestSupport.assertClusterSizeEventually(1, member3);
        PacketFiltersUtil.resetPacketFiltersFrom(member1);
        PacketFiltersUtil.resetPacketFiltersFrom(member2);
        PacketFiltersUtil.resetPacketFiltersFrom(member3);
        HazelcastTestSupport.assertOpenEventually(mergeLatch);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member1), MembershipUpdateTest.getMemberMap(member3));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member1), MembershipUpdateTest.getMemberMap(member2));
            }
        });
    }

    @Test
    public void masterCandidate_canGracefullyShutdown_whenMasterShutdown() throws Exception {
        masterCandidate_canGracefullyShutdown_whenMasterGoesDown(false);
    }

    @Test
    public void masterCandidate_canGracefullyShutdown_whenMasterCrashes() throws Exception {
        masterCandidate_canGracefullyShutdown_whenMasterGoesDown(true);
    }

    @Test
    public void secondMastershipClaimByYounger_shouldRetry_when_firstMastershipClaimByElder_accepted() {
        Config config = new Config();
        config.setProperty(MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance member1 = newHazelcastInstance(config);
        final HazelcastInstance member2 = newHazelcastInstance(config);
        HazelcastInstance member3 = newHazelcastInstance(new Config().setProperty(MASTERSHIP_CLAIM_TIMEOUT_SECONDS.getName(), "10"));
        final HazelcastInstance member4 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(4, member1, member4);
        HazelcastTestSupport.assertClusterSizeEventually(4, member2, member3);
        PacketFiltersUtil.dropOperationsFrom(member1, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE, ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(member2, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE));
        // If we allow explicit suspicions from member3, when member4 sends a heartbeat to member3
        // after member3 splits from the cluster, member3 will send an explicit suspicion to member4
        // and member4 will start its own mastership claim.
        PacketFiltersUtil.dropOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE, ClusterDataSerializerHook.EXPLICIT_SUSPICION));
        HazelcastTestSupport.suspectMember(member2, member3);
        HazelcastTestSupport.suspectMember(member3, member2);
        HazelcastTestSupport.suspectMember(member3, member1);
        HazelcastTestSupport.suspectMember(member2, member1);
        HazelcastTestSupport.suspectMember(member4, member1);
        HazelcastTestSupport.suspectMember(member4, member2);
        // member2 will complete mastership claim, but member4 won't learn new member list
        PacketFiltersUtil.dropOperationsFrom(member2, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        // member4 should accept member2 as master during mastership claim
        HazelcastTestSupport.assertMasterAddressEventually(HazelcastTestSupport.getAddress(member2), member4);
        PacketFiltersUtil.resetPacketFiltersFrom(member3);
        // member3 will be split when master claim timeouts
        HazelcastTestSupport.assertClusterSizeEventually(1, member3);
        // member4 will learn member list
        PacketFiltersUtil.resetPacketFiltersFrom(member2);
        HazelcastTestSupport.assertClusterSizeEventually(2, member2, member4);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member2), MembershipUpdateTest.getMemberMap(member4));
        PacketFiltersUtil.resetPacketFiltersFrom(member1);
        HazelcastTestSupport.assertClusterSizeEventually(1, member1);
    }

    @Test
    public void secondMastershipClaimByElder_shouldFail_when_firstMastershipClaimByYounger_accepted() {
        Config config = new Config();
        config.setProperty(MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5");
        HazelcastInstance member1 = newHazelcastInstance(config);
        final HazelcastInstance member2 = newHazelcastInstance(config);
        final HazelcastInstance member3 = newHazelcastInstance(config);
        final HazelcastInstance member4 = newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSize(4, member1, member4);
        HazelcastTestSupport.assertClusterSizeEventually(4, member2, member3);
        PacketFiltersUtil.dropOperationsFrom(member1, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE, ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(member2, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE, ClusterDataSerializerHook.HEARTBEAT));
        PacketFiltersUtil.dropOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.FETCH_MEMBER_LIST_STATE));
        HazelcastTestSupport.suspectMember(member2, member3);
        HazelcastTestSupport.suspectMember(member3, member2);
        HazelcastTestSupport.suspectMember(member3, member1);
        HazelcastTestSupport.suspectMember(member2, member1);
        HazelcastTestSupport.suspectMember(member4, member1);
        HazelcastTestSupport.suspectMember(member4, member2);
        // member3 will complete mastership claim, but member4 won't learn new member list
        PacketFiltersUtil.dropOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE));
        // member4 should accept member3 as master during mastership claim
        HazelcastTestSupport.assertMasterAddressEventually(HazelcastTestSupport.getAddress(member3), member4);
        PacketFiltersUtil.resetPacketFiltersFrom(member2);
        // member2 will be split when master claim timeouts
        HazelcastTestSupport.assertClusterSizeEventually(1, member2);
        // member4 will learn member list
        PacketFiltersUtil.resetPacketFiltersFrom(member3);
        HazelcastTestSupport.assertClusterSizeEventually(2, member3, member4);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member3), MembershipUpdateTest.getMemberMap(member4));
        PacketFiltersUtil.resetPacketFiltersFrom(member1);
        HazelcastTestSupport.assertClusterSizeEventually(1, member1);
    }

    @Test
    public void test_whenNodesStartedTerminatedConcurrently() {
        newHazelcastInstance();
        for (int i = 0; i < 3; i++) {
            startInstancesConcurrently(4);
            HazelcastTestSupport.assertClusterSizeEventually((i + 5), getAllHazelcastInstances());
            terminateRandomInstancesConcurrently(3);
            HazelcastInstance[] instances = getAllHazelcastInstances().toArray(new HazelcastInstance[0]);
            Assert.assertEquals((i + 2), instances.length);
            for (HazelcastInstance instance : instances) {
                HazelcastTestSupport.assertClusterSizeEventually(instances.length, instance);
                MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(instances[0]), MembershipUpdateTest.getMemberMap(instance));
            }
        }
    }
}

