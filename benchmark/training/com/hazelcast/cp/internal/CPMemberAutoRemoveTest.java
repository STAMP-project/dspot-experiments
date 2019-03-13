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
package com.hazelcast.cp.internal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPMember;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CPMemberAutoRemoveTest extends HazelcastRaftTestSupport {
    private int missingRaftMemberRemovalSeconds;

    @Test
    public void when_missingCPNodeDoesNotJoin_then_itIsAutomaticallyRemoved() {
        missingRaftMemberRemovalSeconds = 10;
        final HazelcastInstance[] instances = newInstances(3, 3, 0);
        final CPMemberInfo terminatedMember = ((CPMemberInfo) (instances[2].getCPSubsystem().getLocalCPMember()));
        instances[2].getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Collection<CPMemberInfo> activeMembers = HazelcastRaftTestSupport.getRaftService(instances[0]).getMetadataGroupManager().getActiveMembers();
                Assert.assertThat(activeMembers, Matchers.not(Matchers.hasItem(terminatedMember)));
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[0]).getMissingMembers(), Matchers.<CPMemberInfo>empty());
            }
        });
    }

    @Test
    public void when_missingCPNodeJoins_then_itIsNotAutomaticallyRemoved() {
        missingRaftMemberRemovalSeconds = 300;
        final HazelcastInstance[] instances = newInstances(3, 3, 0);
        final CPMember cpMember0 = instances[0].getCPSubsystem().getLocalCPMember();
        final CPMember cpMember1 = instances[1].getCPSubsystem().getLocalCPMember();
        final CPMember cpMember2 = instances[2].getCPSubsystem().getLocalCPMember();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertEquals(3, HazelcastRaftTestSupport.getRaftService(instance).getMetadataGroupManager().getActiveMembers().size());
                }
            }
        });
        SplitBrainTestSupport.blockCommunicationBetween(instances[1], instances[2]);
        SplitBrainTestSupport.blockCommunicationBetween(instances[0], instances[2]);
        HazelcastTestSupport.closeConnectionBetween(instances[1], instances[2]);
        HazelcastTestSupport.closeConnectionBetween(instances[0], instances[2]);
        HazelcastTestSupport.assertClusterSizeEventually(2, instances[0], instances[1]);
        HazelcastTestSupport.assertClusterSizeEventually(1, instances[2]);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[0]).getMissingMembers(), Matchers.hasItem(((CPMemberInfo) (cpMember2))));
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[1]).getMissingMembers(), Matchers.hasItem(((CPMemberInfo) (cpMember2))));
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[2]).getMissingMembers(), Matchers.hasItem(((CPMemberInfo) (cpMember0))));
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[2]).getMissingMembers(), Matchers.hasItem(((CPMemberInfo) (cpMember1))));
            }
        });
        SplitBrainTestSupport.unblockCommunicationBetween(instances[1], instances[2]);
        SplitBrainTestSupport.unblockCommunicationBetween(instances[0], instances[2]);
        HazelcastTestSupport.assertClusterSizeEventually(3, instances);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[0]).getMissingMembers(), Matchers.<CPMemberInfo>empty());
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[1]).getMissingMembers(), Matchers.<CPMemberInfo>empty());
                Assert.assertThat(HazelcastRaftTestSupport.getRaftService(instances[2]).getMissingMembers(), Matchers.<CPMemberInfo>empty());
            }
        });
    }
}

