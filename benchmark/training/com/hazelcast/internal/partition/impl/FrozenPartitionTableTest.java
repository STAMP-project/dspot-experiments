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
import ClusterState.PASSIVE;
import GroupProperty.MAX_JOIN_SECONDS;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.StaticMemberNodeContext;
import com.hazelcast.internal.cluster.impl.AdvancedClusterStateTest;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FrozenPartitionTableTest extends HazelcastTestSupport {
    @Test
    public void partitionTable_isFrozen_whenNodesLeave_duringClusterStateIsFrozen() {
        testPartitionTableIsFrozenDuring(FROZEN);
    }

    @Test
    public void partitionTable_isFrozen_whenNodesLeave_duringClusterStateIsPassive() {
        testPartitionTableIsFrozenDuring(PASSIVE);
    }

    @Test
    public void partitionTable_isFrozen_whenMemberReJoins_duringClusterStateIsFrozen() {
        partitionTable_isFrozen_whenMemberReJoins_duringClusterStateIs(FROZEN);
    }

    @Test
    public void partitionTable_isFrozen_whenMemberReJoins_duringClusterStateIsPassive() {
        partitionTable_isFrozen_whenMemberReJoins_duringClusterStateIs(PASSIVE);
    }

    @Test
    public void partitionTable_shouldBeFixed_whenMemberLeaves_inFrozenState_thenStateChangesToActive() {
        testPartitionTableIsHealedWhenClusterStateIsActiveAfter(FROZEN);
    }

    @Test
    public void partitionTable_shouldBeFixed_whenMemberLeaves_inPassiveState_thenStateChangesToActive() {
        testPartitionTableIsHealedWhenClusterStateIsActiveAfter(PASSIVE);
    }

    @Test
    public void partitionTable_shouldBeFixed_whenMemberRestarts_usingNewUuid() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config configMaster = new Config();
        configMaster.setProperty(MAX_JOIN_SECONDS.getName(), "5");
        HazelcastInstance hz1 = factory.newHazelcastInstance(configMaster);
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2, hz3);
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3);
        AdvancedClusterStateTest.changeClusterStateEventually(hz3, FROZEN);
        int member3PartitionId = HazelcastTestSupport.getPartitionId(hz3);
        MemberImpl member3 = HazelcastTestSupport.getNode(hz3).getLocalMember();
        hz3.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        HazelcastInstanceFactory.newHazelcastInstance(TestHazelcastInstanceFactory.initOrCreateConfig(new Config()), HazelcastTestSupport.randomName(), new StaticMemberNodeContext(factory, UuidUtil.newUnsecureUuidString(), member3.getAddress()));
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2);
        InternalOperationService operationService = HazelcastTestSupport.getOperationService(hz1);
        operationService.invokeOnPartition(null, new FrozenPartitionTableTest.NonRetryablePartitionOperation(), member3PartitionId).join();
    }

    @Test
    public void partitionTable_shouldBeFixed_whenMemberRestarts_usingUuidOfAnotherMissingMember() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config configMaster = new Config();
        configMaster.setProperty(MAX_JOIN_SECONDS.getName(), "5");
        HazelcastInstance hz1 = factory.newHazelcastInstance(configMaster);
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastInstance hz3 = factory.newHazelcastInstance();
        HazelcastInstance hz4 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSizeEventually(4, hz2, hz3);
        HazelcastTestSupport.warmUpPartitions(hz1, hz2, hz3, hz4);
        AdvancedClusterStateTest.changeClusterStateEventually(hz4, FROZEN);
        int member3PartitionId = HazelcastTestSupport.getPartitionId(hz3);
        int member4PartitionId = HazelcastTestSupport.getPartitionId(hz4);
        MemberImpl member3 = HazelcastTestSupport.getNode(hz3).getLocalMember();
        MemberImpl member4 = HazelcastTestSupport.getNode(hz4).getLocalMember();
        hz3.shutdown();
        hz4.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz1, hz2);
        HazelcastInstanceFactory.newHazelcastInstance(TestHazelcastInstanceFactory.initOrCreateConfig(new Config()), HazelcastTestSupport.randomName(), new StaticMemberNodeContext(factory, member4.getUuid(), member3.getAddress()));
        HazelcastTestSupport.assertClusterSizeEventually(3, hz1, hz2);
        InternalOperationService operationService = HazelcastTestSupport.getOperationService(hz1);
        operationService.invokeOnPartition(null, new FrozenPartitionTableTest.NonRetryablePartitionOperation(), member3PartitionId).join();
        try {
            operationService.invokeOnPartition(null, new FrozenPartitionTableTest.NonRetryablePartitionOperation(), member4PartitionId).join();
            Assert.fail("Invocation to missing member should have failed!");
        } catch (TargetNotMemberException ignored) {
        }
    }

    public static class NonRetryablePartitionOperation extends Operation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public ExceptionAction onInvocationException(Throwable throwable) {
            if ((throwable instanceof WrongTargetException) || (throwable instanceof TargetNotMemberException)) {
                return ExceptionAction.THROW_EXCEPTION;
            }
            return super.onInvocationException(throwable);
        }
    }
}

