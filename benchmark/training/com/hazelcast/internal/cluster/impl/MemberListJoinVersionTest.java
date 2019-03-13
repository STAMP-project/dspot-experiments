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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SerializationSamplesExcluded;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class, SerializationSamplesExcluded.class })
public class MemberListJoinVersionTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    @Test
    public void when_singletonClusterStarted_then_memberListJoinVersionShouldBeAssigned() {
        HazelcastInstance instance = factory.newHazelcastInstance();
        MemberImpl localMember = HazelcastTestSupport.getNode(instance).getLocalMember();
        Assert.assertEquals(MemberMap.SINGLETON_MEMBER_LIST_VERSION, localMember.getMemberListJoinVersion());
        Assert.assertEquals(MemberMap.SINGLETON_MEMBER_LIST_VERSION, HazelcastTestSupport.getClusterService(instance).getMemberListJoinVersion());
        MemberImpl member = HazelcastTestSupport.getClusterService(instance).getMember(localMember.getAddress());
        Assert.assertEquals(MemberMap.SINGLETON_MEMBER_LIST_VERSION, member.getMemberListJoinVersion());
    }

    @Test
    public void when_multipleMembersStarted_then_memberListJoinVersionShouldBeAssigned() {
        HazelcastInstance master = factory.newHazelcastInstance();
        HazelcastInstance slave1 = factory.newHazelcastInstance();
        HazelcastInstance slave2 = factory.newHazelcastInstance();
        HazelcastInstance slave3 = factory.newHazelcastInstance();
        HazelcastTestSupport.assertClusterSizeEventually(4, slave1, slave2);
        MemberListJoinVersionTest.assertJoinMemberListVersions(master, slave1, slave2, slave3);
    }

    @Test
    public void when_splitSubClustersMerge_then_targetClusterShouldIncrementMemberListVersion() {
        Config config = new Config();
        config.setProperty(GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), "15").setProperty(GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), "1").setProperty(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS.getName(), "5").setProperty(GroupProperty.MERGE_FIRST_RUN_DELAY_SECONDS.getName(), "5").setProperty(GroupProperty.MERGE_NEXT_RUN_DELAY_SECONDS.getName(), "5");
        final HazelcastInstance member1 = factory.newHazelcastInstance(config);
        final HazelcastInstance member2 = factory.newHazelcastInstance(config);
        final HazelcastInstance member3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, member2);
        final CountDownLatch mergeLatch = new CountDownLatch(1);
        member3.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if ((event.getState()) == (MERGED)) {
                    mergeLatch.countDown();
                }
            }
        });
        PacketFiltersUtil.rejectOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Arrays.asList(ClusterDataSerializerHook.HEARTBEAT, ClusterDataSerializerHook.SPLIT_BRAIN_MERGE_VALIDATION));
        HazelcastTestSupport.assertClusterSizeEventually(2, member1, member2);
        PacketFiltersUtil.rejectOperationsFrom(member3, ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.SPLIT_BRAIN_MERGE_VALIDATION));
        HazelcastTestSupport.assertClusterSizeEventually(1, member3);
        int beforeJoinVersionOnMember3 = HazelcastTestSupport.getClusterService(member3).getMemberListVersion();
        PacketFiltersUtil.resetPacketFiltersFrom(member3);
        HazelcastTestSupport.assertOpenEventually(mergeLatch);
        int afterJoinVersionOnMember1 = HazelcastTestSupport.getClusterService(member1).getMemberListVersion();
        Assert.assertNotEquals(afterJoinVersionOnMember1, beforeJoinVersionOnMember3);
        int versionOnLocalMember3 = HazelcastTestSupport.getNode(member3).getLocalMember().getMemberListJoinVersion();
        Assert.assertEquals(afterJoinVersionOnMember1, versionOnLocalMember3);
        MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member1), MembershipUpdateTest.getMemberMap(member3));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                MembershipUpdateTest.assertMemberViewsAreSame(MembershipUpdateTest.getMemberMap(member1), MembershipUpdateTest.getMemberMap(member2));
            }
        });
        MemberListJoinVersionTest.assertJoinMemberListVersions(member1, member2, member3);
    }
}

