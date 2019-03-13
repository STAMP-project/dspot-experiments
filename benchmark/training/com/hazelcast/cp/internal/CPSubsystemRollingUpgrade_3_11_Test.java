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


import RaftAtomicLongService.SERVICE_NAME;
import Versions.V3_11;
import Versions.V3_12;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongService;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.mocknetwork.MockNodeContext;
import com.hazelcast.version.Version;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


// RU_COMPAT_3_11
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CPSubsystemRollingUpgrade_3_11_Test extends HazelcastRaftTestSupport {
    @Rule
    public final OverridePropertyRule version_3_11_rule = OverridePropertyRule.set(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION, V3_11.toString());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenGetCPSubsystem_onVersion_3_11_thenFailsWithUnsupportedOperationException() {
        HazelcastInstance instance = factory.newHazelcastInstance(createConfig(3, 3));
        expectedException.expect(UnsupportedOperationException.class);
        instance.getCPSubsystem();
    }

    @Test
    public void whenGetCPSubsystem_afterVersionUpgrade_thenShouldSuccess() {
        HazelcastInstance instance = newUpgradableHazelcastInstance(createConfig(3, 3));
        instance.getCluster().changeClusterVersion(V3_12);
        instance.getCPSubsystem();
    }

    @Test
    public void whenCPSubsystemEnabled_onVersion_3_11_thenCPDiscoveryShouldNotComplete() {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = factory.newHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftService service = HazelcastRaftTestSupport.getRaftService(instance);
                    MetadataRaftGroupManager metadataGroupManager = service.getMetadataGroupManager();
                    Assert.assertNull(metadataGroupManager.getLocalCPMember());
                    Assert.assertFalse(metadataGroupManager.isDiscoveryCompleted());
                    Assert.assertThat(metadataGroupManager.getActiveMembers(), Matchers.<CPMemberInfo>empty());
                }
            }
        }, 5);
    }

    @Test
    public void whenCPSubsystemEnabled_afterVersion_thenCPDiscoveryShouldComplete() {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = newUpgradableHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        instances[0].getCluster().changeClusterVersion(V3_12);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftService service = HazelcastRaftTestSupport.getRaftService(instance);
                    MetadataRaftGroupManager metadataGroupManager = service.getMetadataGroupManager();
                    Assert.assertTrue(metadataGroupManager.isDiscoveryCompleted());
                    Assert.assertNotNull(metadataGroupManager.getLocalCPMember());
                    Assert.assertThat(metadataGroupManager.getActiveMembers(), Matchers.not(Matchers.<CPMemberInfo>empty()));
                }
            }
        });
    }

    @Test
    public void whenCreateRaftGroupCalled_onVersion_3_11_thenFailsWithUnsupportedOperationException() {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = factory.newHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        RaftService service = HazelcastRaftTestSupport.getRaftService(instances[0]);
        expectedException.expect(UnsupportedOperationException.class);
        service.getInvocationManager().createRaftGroup("default", 3).join();
    }

    @Test
    public void whenCreateRaftGroupCalled_afterUpgrade_thenShouldSuccess() throws Exception {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = newUpgradableHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        instances[0].getCluster().changeClusterVersion(V3_12);
        RaftService service = HazelcastRaftTestSupport.getRaftService(instances[0]);
        service.getInvocationManager().createRaftGroup("default", 3).get();
    }

    @Test
    public void whenCPDataStructureCreated_onVersion_3_11_thenFailsWithUnsupportedOperationException() {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = factory.newHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        RaftAtomicLongService service = HazelcastTestSupport.getNodeEngineImpl(instances[0]).getService(SERVICE_NAME);
        expectedException.expect(UnsupportedOperationException.class);
        service.createProxy("atomic");
    }

    @Test
    public void whenCPDataStructureCreated_afterUpgrade_thenShouldSuccess() {
        int clusterSize = 3;
        final HazelcastInstance[] instances = new HazelcastInstance[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            instances[i] = newUpgradableHazelcastInstance(createConfig(clusterSize, clusterSize));
        }
        instances[0].getCluster().changeClusterVersion(V3_12);
        instances[0].getCPSubsystem().getAtomicLong("atomic").get();
    }

    private class CustomNodeContext extends MockNodeContext {
        CustomNodeContext() {
            super(factory.getRegistry(), factory.nextAddress());
        }

        @Override
        public NodeExtension createNodeExtension(Node node) {
            return new CPSubsystemRollingUpgrade_3_11_Test.AlwaysCompatibleNodeExtension(node);
        }
    }

    private static class AlwaysCompatibleNodeExtension extends DefaultNodeExtension {
        AlwaysCompatibleNodeExtension(Node node) {
            super(node);
        }

        @Override
        public boolean isNodeVersionCompatibleWith(Version clusterVersion) {
            return true;
        }
    }
}

