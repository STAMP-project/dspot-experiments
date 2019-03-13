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


import ClusterServiceImpl.SERVICE_NAME;
import ClusterState.FROZEN;
import ClusterState.NO_MIGRATION;
import ClusterState.PASSIVE;
import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.impl.operations.PromoteLiteMemberOp;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SerializationSamplesExcluded;
import com.hazelcast.util.UuidUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class, SerializationSamplesExcluded.class })
public class PromoteLiteMemberTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void liteMaster_promoted() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config());
        hz1.getCluster().promoteLocalLiteMember();
        Assert.assertFalse(PromoteLiteMemberTest.getMember(hz1).isLiteMember());
        PromoteLiteMemberTest.assertAllNormalMembers(hz1.getCluster());
        PromoteLiteMemberTest.assertAllNormalMembersEventually(hz2.getCluster());
        PromoteLiteMemberTest.assertAllNormalMembersEventually(hz3.getCluster());
    }

    @Test
    public void liteMember_promoted() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config());
        hz2.getCluster().promoteLocalLiteMember();
        Assert.assertFalse(PromoteLiteMemberTest.getMember(hz2).isLiteMember());
        PromoteLiteMemberTest.assertAllNormalMembers(hz1.getCluster());
        PromoteLiteMemberTest.assertAllNormalMembersEventually(hz2.getCluster());
        PromoteLiteMemberTest.assertAllNormalMembersEventually(hz3.getCluster());
    }

    @Test
    public void normalMember_promotion_shouldFail_onLocal() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        exception.expect(IllegalStateException.class);
        hz1.getCluster().promoteLocalLiteMember();
    }

    @Test
    public void normalMember_promotion_shouldFail_onNonMaster() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config());
        PromoteLiteMemberOp op = new PromoteLiteMemberOp();
        op.setCallerUuid(PromoteLiteMemberTest.getMember(hz2).getUuid());
        InternalCompletableFuture<MembersView> future = HazelcastTestSupport.getOperationService(hz2).invokeOnTarget(SERVICE_NAME, op, HazelcastTestSupport.getAddress(hz3));
        exception.expect(IllegalStateException.class);
        future.join();
    }

    @Test
    public void normalMember_promotion_shouldBeNoop_onMaster() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        PromoteLiteMemberOp op = new PromoteLiteMemberOp();
        op.setCallerUuid(PromoteLiteMemberTest.getMember(hz2).getUuid());
        InternalCompletableFuture<MembersView> future = HazelcastTestSupport.getOperationService(hz2).invokeOnTarget(SERVICE_NAME, op, HazelcastTestSupport.getAddress(hz1));
        future.join();
    }

    @Test
    public void notExistingMember_promotion_shouldFail() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        PromoteLiteMemberOp op = new PromoteLiteMemberOp();
        op.setCallerUuid(UuidUtil.newUnsecureUuidString());
        InternalCompletableFuture<MembersView> future = HazelcastTestSupport.getOperationService(hz2).invokeOnTarget(SERVICE_NAME, op, HazelcastTestSupport.getAddress(hz1));
        exception.expect(IllegalStateException.class);
        future.join();
    }

    @Test
    public void standaloneLiteMember_promoted() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance(new Config().setLiteMember(true));
        hz.getCluster().promoteLocalLiteMember();
        Assert.assertFalse(PromoteLiteMemberTest.getMember(hz).isLiteMember());
        PromoteLiteMemberTest.assertAllNormalMembers(hz.getCluster());
        HazelcastTestSupport.warmUpPartitions(hz);
        PromoteLiteMemberTest.assertPartitionsAssigned(hz);
    }

    @Test
    public void promotedMasterLiteMember_shouldHave_partitionsAssigned() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config());
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        PromoteLiteMemberTest.assertNoPartitionsAssigned(hz1);
        hz1.getCluster().promoteLocalLiteMember();
        PromoteLiteMemberTest.assertPartitionsAssignedEventually(hz1);
    }

    @Test
    public void promotedLiteMember_shouldHave_partitionsAssigned() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config());
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        PromoteLiteMemberTest.assertNoPartitionsAssigned(hz2);
        hz2.getCluster().promoteLocalLiteMember();
        PromoteLiteMemberTest.assertPartitionsAssignedEventually(hz2);
    }

    @Test
    public void promotion_shouldFail_whenClusterStatePassive() {
        promotion_shouldFail_whenClusterState_NotAllowMigration(PASSIVE);
    }

    @Test
    public void promotion_shouldFail_whenClusterStateFrozen() {
        promotion_shouldFail_whenClusterState_NotAllowMigration(FROZEN);
    }

    @Test
    public void promotion_shouldFail_whenClusterStateNoMigration() {
        promotion_shouldFail_whenClusterState_NotAllowMigration(NO_MIGRATION);
    }

    @Test
    public void promotion_shouldFail_whenMastershipClaimInProgress_duringPromotion() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        // artificially set mastership claim flag
        ClusterServiceImpl clusterService = HazelcastTestSupport.getNode(hz1).getClusterService();
        clusterService.getClusterJoinManager().setMastershipClaimInProgress();
        Cluster cluster = hz2.getCluster();
        exception.expect(IllegalStateException.class);
        cluster.promoteLocalLiteMember();
    }

    @Test
    public void promotion_shouldFail_whenMasterLeaves_duringPromotion() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        HazelcastInstance hz3 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        PacketFiltersUtil.dropOperationsBetween(hz3, hz1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.PROMOTE_LITE_MEMBER));
        final Cluster cluster = hz3.getCluster();
        Future<Exception> future = HazelcastTestSupport.spawn(new Callable<Exception>() {
            @Override
            public Exception call() throws Exception {
                try {
                    cluster.promoteLocalLiteMember();
                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        });
        assertPromotionInvocationStarted(hz3);
        hz1.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz2, hz3);
        Exception exception = future.get();
        // MemberLeftException is wrapped by HazelcastException
        HazelcastTestSupport.assertInstanceOf(MemberLeftException.class, exception.getCause());
    }

    @Test
    public void promotion_shouldFail_whenMasterIsSuspected_duringPromotion() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz1 = factory.newHazelcastInstance(new Config());
        final HazelcastInstance hz2 = factory.newHazelcastInstance(new Config());
        final HazelcastInstance hz3 = factory.newHazelcastInstance(new Config().setLiteMember(true));
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        PacketFiltersUtil.rejectOperationsBetween(hz3, hz1, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.PROMOTE_LITE_MEMBER, ClusterDataSerializerHook.EXPLICIT_SUSPICION));
        PacketFiltersUtil.dropOperationsFrom(hz2, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.MEMBER_INFO_UPDATE, ClusterDataSerializerHook.EXPLICIT_SUSPICION));
        PacketFiltersUtil.dropOperationsFrom(hz1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.HEARTBEAT));
        final Cluster cluster = hz3.getCluster();
        Future future = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                cluster.promoteLocalLiteMember();
            }
        });
        assertPromotionInvocationStarted(hz3);
        HazelcastTestSupport.suspectMember(HazelcastTestSupport.getNode(hz3), HazelcastTestSupport.getNode(hz1));
        HazelcastTestSupport.suspectMember(HazelcastTestSupport.getNode(hz2), HazelcastTestSupport.getNode(hz1));
        HazelcastTestSupport.assertMasterAddressEventually(HazelcastTestSupport.getAddress(hz2), hz3);
        PacketFiltersUtil.dropOperationsBetween(hz3, hz1, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.EXPLICIT_SUSPICION));
        try {
            future.get();
            Assert.fail("Promotion should fail!");
        } catch (ExecutionException e) {
            HazelcastTestSupport.assertInstanceOf(IllegalStateException.class, e.getCause());
        }
    }

    @Test
    public void masterMemberAttributes_arePreserved_afterPromotion() throws Exception {
        memberAttributes_arePreserved_afterPromotion(true);
    }

    @Test
    public void normalMemberAttributes_arePreserved_afterPromotion() throws Exception {
        memberAttributes_arePreserved_afterPromotion(false);
    }
}

