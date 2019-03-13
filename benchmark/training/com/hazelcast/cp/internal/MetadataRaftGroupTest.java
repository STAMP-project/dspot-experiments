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


import CPGroupStatus.DESTROYED;
import NodeState.SHUT_DOWN;
import RaftService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.Member;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.internal.operation.RestartCPMemberOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveCPMembersOp;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterDataSerializerHook;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MetadataRaftGroupTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    @Test
    public void when_clusterStartsWithNonCPNodes_then_metadataClusterIsInitialized() {
        int cpNodeCount = 3;
        instances = newInstances(cpNodeCount, cpNodeCount, 2);
        final List<Address> raftAddresses = new ArrayList<Address>();
        for (int i = 0; i < cpNodeCount; i++) {
            raftAddresses.add(HazelcastTestSupport.getAddress(instances[i]));
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    if (raftAddresses.contains(HazelcastTestSupport.getAddress(instance))) {
                        Assert.assertNotNull(HazelcastRaftTestSupport.getRaftNode(instance, HazelcastRaftTestSupport.getMetadataGroupId(instance)));
                    }
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    if (!(raftAddresses.contains(HazelcastTestSupport.getAddress(instance)))) {
                        Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(instance, HazelcastRaftTestSupport.getMetadataGroupId(instance)));
                    }
                }
            }
        }, 10);
    }

    @Test
    public void when_clusterStartsWithCPNodes_then_CPDiscoveryCompleted() {
        final int nodeCount = 3;
        instances = newInstances(nodeCount);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    MetadataRaftGroupManager metadataGroupManager = HazelcastRaftTestSupport.getRaftService(instance).getMetadataGroupManager();
                    Assert.assertEquals(MetadataRaftGroupInitStatus.SUCCESSFUL, metadataGroupManager.getInitializationStatus());
                    Assert.assertEquals(nodeCount, metadataGroupManager.getActiveMembers().size());
                    Assert.assertNotNull(metadataGroupManager.getInitialCPMembers());
                    Assert.assertEquals(nodeCount, metadataGroupManager.getInitialCPMembers().size());
                    Assert.assertTrue(metadataGroupManager.getInitializedCPMembers().isEmpty());
                    Assert.assertTrue(metadataGroupManager.getInitializationCommitIndices().isEmpty());
                }
            }
        });
    }

    @Test
    public void when_slaveMissesItsJoinResponse_then_CPDiscoveryCompleted() throws InterruptedException, ExecutionException {
        final Config config = new Config();
        config.getCPSubsystemConfig().setCPMemberCount(3);
        final HazelcastInstance master = factory.newHazelcastInstance(config);
        final HazelcastInstance slave1 = factory.newHazelcastInstance(config);
        final Address slaveAddress2 = factory.nextAddress();
        PacketFiltersUtil.dropOperationsToAddresses(master, Collections.singletonList(slaveAddress2), ClusterDataSerializerHook.F_ID, Collections.singletonList(ClusterDataSerializerHook.FINALIZE_JOIN));
        Future<HazelcastInstance> f = HazelcastTestSupport.spawn(new Callable<HazelcastInstance>() {
            @Override
            public HazelcastInstance call() {
                return factory.newHazelcastInstance(slaveAddress2, config);
            }
        });
        HazelcastTestSupport.assertClusterSizeEventually(3, master);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                MetadataRaftGroupManager metadataGroupManager = HazelcastRaftTestSupport.getRaftService(master).getMetadataGroupManager();
                Assert.assertNotNull(metadataGroupManager.getInitialCPMembers());
                Assert.assertEquals(3, metadataGroupManager.getInitialCPMembers().size());
                Assert.assertEquals(2, metadataGroupManager.getInitializedCPMembers().size());
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                MetadataRaftGroupManager metadataGroupManager = HazelcastRaftTestSupport.getRaftService(master).getMetadataGroupManager();
                Assert.assertEquals(MetadataRaftGroupInitStatus.IN_PROGRESS, metadataGroupManager.getInitializationStatus());
                Assert.assertEquals(2, metadataGroupManager.getInitializedCPMembers().size());
            }
        }, 10);
        PacketFiltersUtil.resetPacketFiltersFrom(master);
        final HazelcastInstance slave2 = f.get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : Arrays.asList(master, slave1, slave2)) {
                    RaftService service = HazelcastRaftTestSupport.getRaftService(instance);
                    Assert.assertTrue(service.getMetadataGroupManager().isDiscoveryCompleted());
                }
            }
        });
    }

    @Test
    public void when_raftGroupIsCreatedWithAllCPNodes_then_raftNodeIsCreatedOnAll() {
        int nodeCount = 5;
        instances = newInstances(nodeCount);
        final CPGroupId groupId = createNewRaftGroup(instances[0], "id", nodeCount);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertNotNull(HazelcastRaftTestSupport.getRaftNode(instance, groupId));
                }
            }
        });
    }

    @Test
    public void when_raftGroupIsCreatedWithSomeCPNodes_then_raftNodeIsCreatedOnOnlyThem() {
        final int nodeCount = 5;
        final int metadataGroupSize = 3;
        instances = newInstances(nodeCount, metadataGroupSize, 0);
        final List<Member> raftMembers = new ArrayList<Member>();
        for (int i = 0; i < nodeCount; i++) {
            raftMembers.add(instances[i].getCluster().getLocalMember());
        }
        Collections.sort(raftMembers, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getUuid().compareTo(o2.getUuid());
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (Member member : raftMembers.subList(0, metadataGroupSize)) {
                    HazelcastInstance instance = factory.getInstance(member.getAddress());
                    Assert.assertNotNull(HazelcastRaftTestSupport.getRaftNode(instance, HazelcastRaftTestSupport.getMetadataGroupId(instance)));
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (Member member : raftMembers.subList(metadataGroupSize, raftMembers.size())) {
                    HazelcastInstance instance = factory.getInstance(member.getAddress());
                    Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(instance, HazelcastRaftTestSupport.getMetadataGroupId(instance)));
                }
            }
        }, 10);
    }

    @Test
    public void when_raftGroupIsCreatedWithSomeCPNodes_then_raftNodeIsCreatedOnOnlyTheSelectedEndpoints() {
        when_raftGroupIsCreatedWithSomeCPNodes_then_raftNodeIsCreatedOnOnlyTheSelectedEndpoints(true);
    }

    @Test
    public void when_raftGroupIsCreatedFromNonCPNode_then_raftNodeIsCreatedOnOnlyTheSelectedEndpoints() {
        when_raftGroupIsCreatedWithSomeCPNodes_then_raftNodeIsCreatedOnOnlyTheSelectedEndpoints(false);
    }

    @Test
    public void when_sizeOfRaftGroupIsLargerThanCPNodeCount_then_raftGroupCannotBeCreated() {
        int nodeCount = 3;
        instances = newInstances(nodeCount);
        try {
            createNewRaftGroup(instances[0], "id", (nodeCount + 1));
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void when_raftGroupIsCreatedWithSameSizeMultipleTimes_then_itSucceeds() {
        int nodeCount = 3;
        instances = newInstances(nodeCount);
        CPGroupId groupId1 = createNewRaftGroup(instances[0], "id", nodeCount);
        CPGroupId groupId2 = createNewRaftGroup(instances[1], "id", nodeCount);
        Assert.assertEquals(groupId1, groupId2);
    }

    @Test
    public void when_raftGroupIsCreatedWithDifferentSizeMultipleTimes_then_itFails() {
        int nodeCount = 3;
        instances = newInstances(nodeCount);
        createNewRaftGroup(instances[0], "id", nodeCount);
        try {
            createNewRaftGroup(instances[0], "id", (nodeCount - 1));
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void when_raftGroupDestroyTriggered_then_raftGroupIsDestroyed() throws InterruptedException, ExecutionException {
        int metadataGroupSize = 3;
        int cpNodeCount = 5;
        instances = newInstances(cpNodeCount, metadataGroupSize, 0);
        final CPGroupId groupId = createNewRaftGroup(instances[0], "id", cpNodeCount);
        CPGroup group = instances[0].getCPSubsystem().getCPSubsystemManagementService().getCPGroup("id").get();
        Assert.assertNotNull(group);
        Assert.assertEquals(groupId, group.id());
        destroyRaftGroup(instances[0], groupId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(instance, groupId));
                }
            }
        });
        final RaftInvocationManager invocationService = getRaftInvocationManager(instances[0]);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Future<CPGroupInfo> f = invocationService.query(HazelcastRaftTestSupport.getMetadataGroupId(instances[0]), new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId), QueryPolicy.LEADER_LOCAL);
                CPGroupInfo group = f.get();
                Assert.assertEquals(DESTROYED, group.status());
            }
        });
    }

    @Test
    public void when_raftGroupDestroyTriggeredMultipleTimes_then_destroyDoesNotFail() {
        int metadataGroupSize = 3;
        int cpNodeCount = 5;
        instances = newInstances(cpNodeCount, metadataGroupSize, 0);
        CPGroupId groupId = createNewRaftGroup(instances[0], "id", cpNodeCount);
        destroyRaftGroup(instances[0], groupId);
        destroyRaftGroup(instances[0], groupId);
    }

    @Test
    public void when_raftGroupIsDestroyed_then_itCanBeCreatedAgain() {
        int metadataGroupSize = 3;
        int cpNodeCount = 5;
        instances = newInstances(cpNodeCount, metadataGroupSize, 0);
        final CPGroupId groupId = createNewRaftGroup(instances[0], "id", cpNodeCount);
        destroyRaftGroup(instances[0], groupId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(instance, groupId));
                }
            }
        });
        createNewRaftGroup(instances[0], "id", (cpNodeCount - 1));
    }

    @Test
    public void when_nonMetadataRaftGroupIsAlive_then_itCanBeForceDestroyed() throws InterruptedException, ExecutionException {
        instances = newInstances(3);
        waitAllForLeaderElection(instances, MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        final CPGroupId groupId = createNewRaftGroup(instances[0], "id", 3);
        CPGroup group = instances[0].getCPSubsystem().getCPSubsystemManagementService().getCPGroup("id").get();
        Assert.assertNotNull(group);
        Assert.assertEquals(groupId, group.id());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertNotNull(HazelcastRaftTestSupport.getRaftService(instance).getRaftNode(groupId));
                }
            }
        });
        HazelcastRaftTestSupport.getRaftService(instances[0]).forceDestroyCPGroup(groupId.name()).get();
        group = getRaftInvocationManager(instances[0]).<CPGroupInfo>query(HazelcastRaftTestSupport.getMetadataGroupId(instances[0]), new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId), QueryPolicy.LEADER_LOCAL).get();
        Assert.assertEquals(DESTROYED, group.status());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(instance, groupId));
                }
            }
        });
        CPGroupId newGroupId = createNewRaftGroup(instances[0], "id", 3);
        Assert.assertNotEquals(groupId, newGroupId);
    }

    @Test
    public void when_nonMetadataRaftGroupLosesMajority_then_itCanBeForceDestroyed() throws InterruptedException, ExecutionException {
        instances = newInstances(5);
        waitAllForLeaderElection(instances, MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        final CPGroupId groupId = createNewRaftGroup(instances[0], "id", 3);
        CPGroup group = instances[0].getCPSubsystem().getCPSubsystemManagementService().getCPGroup("id").get();
        Assert.assertNotNull(group);
        Assert.assertEquals(groupId, group.id());
        final CPMemberInfo[] groupMembers = membersArray();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (CPMemberInfo member : groupMembers) {
                    HazelcastInstance instance = factory.getInstance(member.getAddress());
                    Assert.assertNotNull(HazelcastRaftTestSupport.getRaftNode(instance, groupId));
                }
            }
        });
        factory.getInstance(groupMembers[0].getAddress()).getLifecycleService().terminate();
        factory.getInstance(groupMembers[1].getAddress()).getLifecycleService().terminate();
        final HazelcastInstance runningInstance = factory.getInstance(groupMembers[2].getAddress());
        HazelcastRaftTestSupport.getRaftService(runningInstance).forceDestroyCPGroup(groupId.name()).get();
        group = getRaftInvocationManager(runningInstance).<CPGroupInfo>query(HazelcastRaftTestSupport.getMetadataGroupId(runningInstance), new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId), QueryPolicy.LEADER_LOCAL).get();
        Assert.assertEquals(DESTROYED, group.status());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(HazelcastRaftTestSupport.getRaftNode(runningInstance, groupId));
            }
        });
    }

    @Test
    public void when_metadataClusterNodeFallsFarBehind_then_itInstallsSnapshot() {
        int nodeCount = 3;
        int commitCountToSnapshot = 5;
        Config config = createConfig(nodeCount, nodeCount);
        config.getCPSubsystemConfig().getRaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(commitCountToSnapshot);
        final HazelcastInstance[] instances = new HazelcastInstance[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            instances[i] = factory.newHazelcastInstance(config);
        }
        waitUntilCPDiscoveryCompleted(instances);
        waitAllForLeaderElection(instances, MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        CPMemberInfo leaderEndpoint = RaftUtil.getLeaderMember(HazelcastRaftTestSupport.getRaftNode(instances[0], HazelcastRaftTestSupport.getMetadataGroupId(instances[0])));
        final HazelcastInstance leader = factory.getInstance(leaderEndpoint.getAddress());
        HazelcastInstance follower = null;
        for (HazelcastInstance instance : instances) {
            if (!(HazelcastTestSupport.getAddress(instance).equals(leaderEndpoint.getAddress()))) {
                follower = instance;
                break;
            }
        }
        Assert.assertNotNull(follower);
        SplitBrainTestSupport.blockCommunicationBetween(leader, follower);
        final List<CPGroupId> groupIds = new ArrayList<CPGroupId>();
        for (int i = 0; i < commitCountToSnapshot; i++) {
            CPGroupId groupId = createNewRaftGroup(leader, ("id" + i), nodeCount);
            groupIds.add(groupId);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(HazelcastRaftTestSupport.getRaftNode(leader, HazelcastRaftTestSupport.getMetadataGroupId(leader))).index()) > 0));
            }
        });
        SplitBrainTestSupport.unblockCommunicationBetween(leader, follower);
        final HazelcastInstance f = follower;
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (CPGroupId groupId : groupIds) {
                    Assert.assertNotNull(HazelcastRaftTestSupport.getRaftNode(f, groupId));
                }
            }
        });
    }

    @Test
    public void when_raftGroupIsCreated_onNonMetadataMembers_thenLeaderShouldBeElected() throws InterruptedException, ExecutionException {
        int metadataGroupSize = 3;
        int otherRaftGroupSize = 2;
        instances = newInstances((metadataGroupSize + otherRaftGroupSize), metadataGroupSize, 0);
        final HazelcastInstance leaderInstance = getLeaderInstance(instances, MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        final RaftService raftService = HazelcastRaftTestSupport.getRaftService(leaderInstance);
        Collection<CPMemberInfo> allEndpoints = raftService.getMetadataGroupManager().getActiveMembers();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(raftService.getCPGroupLocally(HazelcastRaftTestSupport.getMetadataGroupId(leaderInstance)));
            }
        });
        CPGroupInfo metadataGroup = raftService.getCPGroupLocally(HazelcastRaftTestSupport.getMetadataGroupId(leaderInstance));
        final Collection<CPMemberInfo> endpoints = new HashSet<CPMemberInfo>(otherRaftGroupSize);
        for (CPMemberInfo endpoint : allEndpoints) {
            if (!(metadataGroup.containsMember(endpoint))) {
                endpoints.add(endpoint);
            }
        }
        Assert.assertEquals(otherRaftGroupSize, endpoints.size());
        ICompletableFuture<CPGroupId> f = raftService.getInvocationManager().invoke(HazelcastRaftTestSupport.getMetadataGroupId(leaderInstance), new com.hazelcast.cp.internal.raftop.metadata.CreateRaftGroupOp("test", endpoints));
        final CPGroupId groupId = f.get();
        for (final HazelcastInstance instance : instances) {
            if (endpoints.contains(instance.getCPSubsystem().getLocalCPMember())) {
                HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                    @Override
                    public void run() {
                        RaftNodeImpl raftNode = HazelcastRaftTestSupport.getRaftNode(instance, groupId);
                        Assert.assertNotNull(raftNode);
                        RaftUtil.waitUntilLeaderElected(raftNode);
                    }
                });
            }
        }
    }

    @Test
    public void when_shutdownLeader_thenNewLeaderElected() {
        int cpNodeCount = 6;
        int metadataGroupSize = 5;
        instances = newInstances(cpNodeCount, metadataGroupSize, 0);
        final HazelcastInstance leaderInstance = getLeaderInstance(instances, MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        leaderInstance.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    if (instance == leaderInstance) {
                        continue;
                    }
                    RaftNodeImpl raftNode = HazelcastRaftTestSupport.getRaftNode(instance, HazelcastRaftTestSupport.getMetadataGroupId(instance));
                    Assert.assertNotNull(raftNode);
                    RaftUtil.waitUntilLeaderElected(raftNode);
                }
            }
        });
    }

    @Test
    public void when_memberIsShutdown_then_itIsRemovedFromRaftGroups() throws InterruptedException, ExecutionException {
        int cpNodeCount = 7;
        int metadataGroupSize = 5;
        int atomicLong1GroupSize = 3;
        instances = newInstances(cpNodeCount, metadataGroupSize, 0);
        CPGroupId groupId1 = createNewRaftGroup(instances[0], "id1", atomicLong1GroupSize);
        CPGroupId groupId2 = createNewRaftGroup(instances[0], "id2", cpNodeCount);
        CPMemberInfo endpoint = findCommonEndpoint(instances[0], HazelcastRaftTestSupport.getMetadataGroupId(instances[0]), groupId1);
        Assert.assertNotNull(endpoint);
        RaftInvocationManager invocationService = null;
        HazelcastInstance aliveInstance = null;
        for (HazelcastInstance instance : instances) {
            if (!(HazelcastTestSupport.getAddress(instance).equals(endpoint.getAddress()))) {
                aliveInstance = instance;
                invocationService = getRaftInvocationManager(instance);
                break;
            }
        }
        Assert.assertNotNull(invocationService);
        factory.getInstance(endpoint.getAddress()).shutdown();
        CPGroupId metadataGroupId = HazelcastRaftTestSupport.getMetadataGroupId(aliveInstance);
        ICompletableFuture<List<CPMemberInfo>> f1 = invocationService.query(metadataGroupId, new GetActiveCPMembersOp(), QueryPolicy.LEADER_LOCAL);
        List<CPMemberInfo> activeEndpoints = f1.get();
        Assert.assertThat(activeEndpoints, Matchers.not(Matchers.hasItem(endpoint)));
        ICompletableFuture<CPGroupInfo> f2 = invocationService.query(metadataGroupId, new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(metadataGroupId), QueryPolicy.LEADER_LOCAL);
        ICompletableFuture<CPGroupInfo> f3 = invocationService.query(metadataGroupId, new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId1), QueryPolicy.LEADER_LOCAL);
        ICompletableFuture<CPGroupInfo> f4 = invocationService.query(metadataGroupId, new com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp(groupId2), QueryPolicy.LEADER_LOCAL);
        CPGroupInfo metadataGroup = f2.get();
        Assert.assertFalse(metadataGroup.containsMember(endpoint));
        Assert.assertEquals(metadataGroupSize, metadataGroup.memberCount());
        CPGroupInfo atomicLongGroup1 = f3.get();
        Assert.assertFalse(atomicLongGroup1.containsMember(endpoint));
        Assert.assertEquals(atomicLong1GroupSize, atomicLongGroup1.memberCount());
        CPGroupInfo atomicLongGroup2 = f4.get();
        Assert.assertFalse(atomicLongGroup2.containsMember(endpoint));
        Assert.assertEquals((cpNodeCount - 1), atomicLongGroup2.memberCount());
    }

    @Test
    public void when_nonReachableEndpointsExist_createRaftGroupPrefersReachableEndpoints() throws InterruptedException, ExecutionException {
        final HazelcastInstance[] instances = newInstances(5);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    Collection<CPMemberInfo> raftMembers = HazelcastRaftTestSupport.getRaftService(instance).getMetadataGroupManager().getActiveMembers();
                    Assert.assertFalse(raftMembers.isEmpty());
                }
            }
        });
        CPMember endpoint3 = instances[3].getCPSubsystem().getLocalCPMember();
        CPMember endpoint4 = instances[4].getCPSubsystem().getLocalCPMember();
        instances[3].getLifecycleService().terminate();
        instances[4].getLifecycleService().terminate();
        RaftInvocationManager invocationManager = getRaftInvocationManager(instances[0]);
        CPGroupId g3 = invocationManager.createRaftGroup("g3", 3).get();
        CPGroupId g4 = invocationManager.createRaftGroup("g4", 4).get();
        RaftNodeImpl leaderNode = waitAllForLeaderElection(Arrays.copyOf(instances, 3), MetadataRaftGroupManager.INITIAL_METADATA_GROUP_ID);
        HazelcastInstance leader = factory.getInstance(((CPMemberInfo) (leaderNode.getLocalMember())).getAddress());
        CPGroupInfo g3Group = HazelcastRaftTestSupport.getRaftGroupLocally(leader, g3);
        Assert.assertThat(g3Group.members(), Matchers.not(Matchers.hasItem(endpoint3)));
        Assert.assertThat(g3Group.members(), Matchers.not(Matchers.hasItem(endpoint4)));
        CPGroupInfo g4Group = HazelcastRaftTestSupport.getRaftGroupLocally(leader, g4);
        boolean b3 = g4Group.containsMember(((CPMemberInfo) (endpoint3)));
        boolean b4 = g4Group.containsMember(((CPMemberInfo) (endpoint4)));
        Assert.assertTrue((b3 ^ b4));
    }

    @Test
    public void when_noCpNodeCountConfigured_then_cpDiscoveryCompletes() {
        final HazelcastInstance[] instances = newInstances(0, 0, 3);
        waitUntilCPDiscoveryCompleted(instances);
    }

    @Test
    public void when_membersLeaveDuringInitialDiscovery_thenAllMembersTerminate() {
        final int nodeCount = 5;
        final int startedNodeCount = nodeCount - 1;
        Config config = createConfig(nodeCount, nodeCount);
        final HazelcastInstance[] instances = new HazelcastInstance[startedNodeCount];
        final Node[] nodes = new Node[startedNodeCount];
        for (int i = 0; i < startedNodeCount; i++) {
            instances[i] = factory.newHazelcastInstance(config);
            nodes[i] = HazelcastTestSupport.getNode(instances[i]);
        }
        // wait for the cp discovery process to start
        HazelcastTestSupport.sleepAtLeastSeconds(10);
        instances[0].getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < startedNodeCount; i++) {
                    Assert.assertEquals(SHUT_DOWN, nodes[i].getState());
                }
            }
        });
    }

    @Test
    public void when_membersLeaveDuringDiscoveryAfterCPSubsystemRestart_then_discoveryIsCancelled() throws InterruptedException, ExecutionException {
        final int nodeCount = 5;
        Config config = createConfig(nodeCount, nodeCount);
        final HazelcastInstance[] instances = new HazelcastInstance[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            instances[i] = factory.newHazelcastInstance(config);
        }
        waitUntilCPDiscoveryCompleted(instances);
        instances[2].getLifecycleService().terminate();
        instances[3].getLifecycleService().terminate();
        instances[4].getLifecycleService().terminate();
        instances[2] = factory.newHazelcastInstance(config);
        instances[3] = factory.newHazelcastInstance(config);
        // we are triggering the restart mechanism while there is a missing member already.
        // the cp subsystem discovery won't be able to complete and we kill another member meanwhile
        long seed = System.currentTimeMillis();
        for (HazelcastInstance instance : Arrays.copyOf(instances, (nodeCount - 1))) {
            Address address = instance.getCluster().getLocalMember().getAddress();
            HazelcastTestSupport.getNodeEngineImpl(instance).getOperationService().invokeOnTarget(SERVICE_NAME, new RestartCPMemberOp(seed), address).get();
        }
        // wait for the cp discovery process to start
        HazelcastTestSupport.sleepAtLeastSeconds(10);
        instances[3].getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    Assert.assertTrue(HazelcastRaftTestSupport.getRaftService(instances[i]).getMetadataGroupManager().isDiscoveryCompleted());
                    Assert.assertNull(instances[i].getCPSubsystem().getLocalCPMember());
                }
            }
        });
    }
}

