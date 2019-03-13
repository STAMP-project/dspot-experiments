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
package com.hazelcast.cp.internal.raft.impl;


import MembershipChangeMode.ADD;
import MembershipChangeMode.REMOVE;
import RaftNodeStatus.ACTIVE;
import RaftNodeStatus.STEPPED_DOWN;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CannotReplicateException;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.exception.MemberAlreadyExistsException;
import com.hazelcast.cp.internal.raft.exception.MemberDoesNotExistException;
import com.hazelcast.cp.internal.raft.impl.dataservice.ApplyRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dataservice.RaftDataService;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.state.RaftGroupMembers;
import com.hazelcast.cp.internal.raft.impl.testing.LocalRaftGroup;
import com.hazelcast.cp.internal.raft.impl.testing.RaftRunnable;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MembershipChangeTest extends HazelcastTestSupport {
    private LocalRaftGroup group;

    @Test
    public void when_newRaftNodeJoins_then_itAppendsMissingEntries() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        final RaftNodeImpl newRaftNode = group.createNewRaftNode();
        leader.replicateMembershipChange(newRaftNode.getLocalMember(), ADD).get();
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(newRaftNode));
            }
        });
        final RaftGroupMembers lastGroupMembers = RaftUtil.getLastGroupMembers(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftNodeImpl[] nodes = group.getNodes();
                for (RaftNodeImpl raftNode : nodes) {
                    Assert.assertEquals(ACTIVE, RaftUtil.getStatus(raftNode));
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getLastGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getLastGroupMembers(raftNode).index());
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getCommittedGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getCommittedGroupMembers(raftNode).index());
                }
            }
        });
        RaftDataService service = group.getService(newRaftNode);
        Assert.assertEquals(1, service.size());
        Assert.assertTrue(service.values().contains("val"));
    }

    @Test
    public void when_followerLeaves_then_itIsRemovedFromTheGroupMembers() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl leavingFollower = followers[0];
        final RaftNodeImpl stayingFollower = followers[1];
        leader.replicate(new ApplyRaftRunnable("val")).get();
        leader.replicateMembershipChange(leavingFollower.getLocalMember(), MembershipChangeMode.REMOVE).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : Arrays.asList(leader, stayingFollower)) {
                    Assert.assertFalse(RaftUtil.getLastGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                }
            }
        });
        group.terminateNode(leavingFollower.getLocalMember());
    }

    @Test
    public void when_newRaftNodeJoinsAfterAnotherNodeLeaves_then_itAppendsMissingEntries() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl leavingFollower = followers[0];
        final RaftNodeImpl stayingFollower = followers[1];
        leader.replicateMembershipChange(leavingFollower.getLocalMember(), MembershipChangeMode.REMOVE).get();
        final RaftNodeImpl newRaftNode = group.createNewRaftNode();
        leader.replicateMembershipChange(newRaftNode.getLocalMember(), ADD).get();
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(newRaftNode));
            }
        });
        final RaftGroupMembers lastGroupMembers = RaftUtil.getLastGroupMembers(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : Arrays.asList(leader, stayingFollower, newRaftNode)) {
                    Assert.assertEquals(ACTIVE, RaftUtil.getStatus(raftNode));
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getLastGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getLastGroupMembers(raftNode).index());
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getCommittedGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getCommittedGroupMembers(raftNode).index());
                    Assert.assertFalse(RaftUtil.getLastGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                }
            }
        });
        RaftDataService service = group.getService(newRaftNode);
        Assert.assertEquals(1, service.size());
        Assert.assertTrue(service.values().contains("val"));
    }

    @Test
    public void when_newRaftNodeJoinsAfterAnotherNodeLeavesAndSnapshotIsTaken_then_itAppendsMissingEntries() throws InterruptedException, ExecutionException {
        int commitIndexAdvanceCountToSnapshot = 10;
        RaftAlgorithmConfig config = new RaftAlgorithmConfig();
        config.setCommitIndexAdvanceCountToSnapshot(commitIndexAdvanceCountToSnapshot);
        group = RaftUtil.newGroupWithService(3, config);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl leavingFollower = followers[0];
        final RaftNodeImpl stayingFollower = followers[1];
        leader.replicateMembershipChange(leavingFollower.getLocalMember(), MembershipChangeMode.REMOVE).get();
        for (int i = 0; i < commitIndexAdvanceCountToSnapshot; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(leader).index()) > 0));
            }
        });
        final RaftNodeImpl newRaftNode = group.createNewRaftNode();
        leader.replicateMembershipChange(newRaftNode.getLocalMember(), ADD).get();
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(newRaftNode));
            }
        });
        final RaftGroupMembers lastGroupMembers = RaftUtil.getLastGroupMembers(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : Arrays.asList(leader, stayingFollower, newRaftNode)) {
                    Assert.assertEquals(ACTIVE, RaftUtil.getStatus(raftNode));
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getLastGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getLastGroupMembers(raftNode).index());
                    Assert.assertEquals(lastGroupMembers.members(), RaftUtil.getCommittedGroupMembers(raftNode).members());
                    Assert.assertEquals(lastGroupMembers.index(), RaftUtil.getCommittedGroupMembers(raftNode).index());
                    Assert.assertFalse(RaftUtil.getLastGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(raftNode).isKnownMember(leavingFollower.getLocalMember()));
                }
            }
        });
        RaftDataService service = group.getService(newRaftNode);
        Assert.assertEquals((commitIndexAdvanceCountToSnapshot + 1), service.size());
        Assert.assertTrue(service.values().contains("val"));
        for (int i = 0; i < commitIndexAdvanceCountToSnapshot; i++) {
            Assert.assertTrue(service.values().contains(("val" + i)));
        }
    }

    @Test
    public void when_leaderLeaves_then_itIsRemovedFromTheGroupMembers() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        leader.replicate(new ApplyRaftRunnable("val")).get();
        leader.replicateMembershipChange(leader.getLocalMember(), MembershipChangeMode.REMOVE).get();
        Assert.assertEquals(STEPPED_DOWN, RaftUtil.getStatus(leader));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertFalse(RaftUtil.getLastGroupMembers(raftNode).isKnownMember(leader.getLocalMember()));
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(raftNode).isKnownMember(leader.getLocalMember()));
                }
            }
        });
    }

    @Test
    public void when_leaderLeaves_then_itCannotVoteForCommitOfMemberChange() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig().setLeaderHeartbeatPeriodInMillis(1000));
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        group.dropMessagesToMember(followers[0].getLocalMember(), leader.getLocalMember(), AppendSuccessResponse.class);
        leader.replicate(new ApplyRaftRunnable("val")).get();
        leader.replicateMembershipChange(leader.getLocalMember(), REMOVE);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, RaftUtil.getCommitIndex(leader));
            }
        }, 10);
    }

    @Test
    public void when_leaderLeaves_then_followersElectNewLeader() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        leader.replicate(new ApplyRaftRunnable("val")).get();
        leader.replicateMembershipChange(leader.getLocalMember(), MembershipChangeMode.REMOVE).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertFalse(RaftUtil.getLastGroupMembers(raftNode).isKnownMember(leader.getLocalMember()));
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(raftNode).isKnownMember(leader.getLocalMember()));
                }
            }
        });
        group.terminateNode(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertNotEquals(leader.getLocalMember(), RaftUtil.getLeaderMember(raftNode));
                }
            }
        });
    }

    @Test
    public void when_membershipChangeRequestIsMadeWithWrongType_then_theChangeFails() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        try {
            leader.replicateMembershipChange(leader.getLocalMember(), null).get();
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void when_nonExistingEndpointIsRemoved_then_theChangeFails() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl leavingFollower = group.getAnyFollowerNode();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        leader.replicateMembershipChange(leavingFollower.getLocalMember(), REMOVE).get();
        try {
            leader.replicateMembershipChange(leavingFollower.getLocalMember(), REMOVE).get();
            Assert.fail();
        } catch (MemberDoesNotExistException ignored) {
        }
    }

    @Test
    public void when_existingEndpointIsAdded_then_theChangeFails() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val")).get();
        try {
            leader.replicateMembershipChange(leader.getLocalMember(), ADD).get();
            Assert.fail();
        } catch (MemberAlreadyExistsException ignored) {
        }
    }

    @Test
    public void when_thereIsNoCommitInTheCurrentTerm_then_cannotMakeMemberChange() throws InterruptedException, ExecutionException {
        // https://groups.google.com/forum/#!msg/raft-dev/t4xj6dJTP6E/d2D9LrWRza8J
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        try {
            leader.replicateMembershipChange(leader.getLocalMember(), REMOVE).get();
            Assert.fail();
        } catch (CannotReplicateException ignored) {
        }
    }

    @Test
    public void when_appendNopEntryOnLeaderElection_then_canMakeMemberChangeAfterNopEntryCommitted() {
        // https://groups.google.com/forum/#!msg/raft-dev/t4xj6dJTP6E/d2D9LrWRza8J
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig(), true);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                // may fail until nop-entry is committed
                try {
                    leader.replicateMembershipChange(leader.getLocalMember(), REMOVE).get();
                } catch (CannotReplicateException e) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    @Test
    public void when_newJoiningNodeFirstReceivesSnapshot_then_itInstallsSnapshot() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(5));
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        for (int i = 0; i < 4; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        final RaftNodeImpl newRaftNode = group.createNewRaftNode();
        group.dropMessagesToMember(leader.getLocalMember(), newRaftNode.getLocalMember(), AppendRequest.class);
        leader.replicateMembershipChange(newRaftNode.getLocalMember(), ADD).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(leader).index()) > 0));
            }
        });
        group.resetAllRulesFrom(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(RaftUtil.getCommitIndex(leader), RaftUtil.getCommitIndex(newRaftNode));
                Assert.assertEquals(RaftUtil.getLastGroupMembers(leader).members(), RaftUtil.getLastGroupMembers(newRaftNode).members());
                Assert.assertEquals(RaftUtil.getLastGroupMembers(leader).members(), RaftUtil.getCommittedGroupMembers(newRaftNode).members());
                RaftDataService service = group.getService(newRaftNode);
                Assert.assertEquals(4, service.size());
            }
        });
    }

    @Test
    public void when_leaderFailsWhileLeavingRaftGroup_othersCommitTheMemberChange() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        leader.replicate(new ApplyRaftRunnable("val")).get();
        for (RaftNodeImpl follower : followers) {
            group.dropMessagesToMember(follower.getLocalMember(), leader.getLocalMember(), AppendSuccessResponse.class);
            group.dropMessagesToMember(follower.getLocalMember(), leader.getLocalMember(), AppendFailureResponse.class);
        }
        leader.replicateMembershipChange(leader.getLocalMember(), REMOVE);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Assert.assertEquals(2, RaftUtil.getLastLogOrSnapshotEntry(follower).index());
                }
            }
        });
        group.terminateNode(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Endpoint newLeaderEndpoint = RaftUtil.getLeaderMember(follower);
                    Assert.assertNotNull(newLeaderEndpoint);
                    Assert.assertNotEquals(leader.getLocalMember(), newLeaderEndpoint);
                }
            }
        });
        final RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followers[0]));
        newLeader.replicate(new ApplyRaftRunnable("val2"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Assert.assertFalse(RaftUtil.getCommittedGroupMembers(follower).isKnownMember(leader.getLocalMember()));
                }
            }
        });
    }

    @Test
    public void when_followerAppendsMultipleMembershipChangesAtOnce_then_itCommitsThemCorrectly() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(5, new RaftAlgorithmConfig().setLeaderHeartbeatPeriodInMillis(1000));
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        leader.replicate(new ApplyRaftRunnable("val")).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Assert.assertEquals(1L, RaftUtil.getCommitIndex(follower));
                }
            }
        });
        final RaftNodeImpl slowFollower = followers[0];
        for (RaftNodeImpl follower : followers) {
            if (follower != slowFollower) {
                group.dropMessagesToMember(follower.getLocalMember(), follower.getLeader(), AppendSuccessResponse.class);
                group.dropMessagesToMember(follower.getLocalMember(), follower.getLeader(), AppendFailureResponse.class);
            }
        }
        final RaftNodeImpl newRaftNode1 = group.createNewRaftNode();
        group.dropMessagesToMember(leader.getLocalMember(), newRaftNode1.getLocalMember(), AppendRequest.class);
        final Future f1 = leader.replicateMembershipChange(newRaftNode1.getLocalMember(), ADD);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Assert.assertEquals(2L, RaftUtil.getLastLogOrSnapshotEntry(follower).index());
                }
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        for (RaftNodeImpl follower : followers) {
            if (follower != slowFollower) {
                group.allowAllMessagesToMember(follower.getLocalMember(), leader.getLeader());
            }
        }
        f1.get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    if (follower != slowFollower) {
                        Assert.assertEquals(6, RaftUtil.getCommittedGroupMembers(follower).memberCount());
                    } else {
                        Assert.assertEquals(5, RaftUtil.getCommittedGroupMembers(follower).memberCount());
                        Assert.assertEquals(6, RaftUtil.getLastGroupMembers(follower).memberCount());
                    }
                }
            }
        });
        final RaftNodeImpl newRaftNode2 = group.createNewRaftNode();
        leader.replicateMembershipChange(newRaftNode2.getLocalMember(), ADD).get();
        group.allowAllMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember());
        group.allowAllMessagesToMember(slowFollower.getLocalMember(), leader.getLocalMember());
        group.allowAllMessagesToMember(leader.getLocalMember(), newRaftNode1.getLocalMember());
        final RaftGroupMembers leaderCommittedGroupMembers = RaftUtil.getCommittedGroupMembers(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(leaderCommittedGroupMembers.index(), RaftUtil.getCommittedGroupMembers(slowFollower).index());
                Assert.assertEquals(leaderCommittedGroupMembers.index(), RaftUtil.getCommittedGroupMembers(newRaftNode1).index());
                Assert.assertEquals(leaderCommittedGroupMembers.index(), RaftUtil.getCommittedGroupMembers(newRaftNode2).index());
            }
        });
    }

    @Test
    public void when_leaderIsSteppingDown_then_itDoesNotAcceptNewAppends() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig(), true);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        group.dropMessagesToMember(leader.getLocalMember(), followers[0].getLocalMember(), AppendRequest.class);
        group.dropMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), AppendRequest.class);
        final Future f1 = leader.replicateMembershipChange(leader.getLocalMember(), MembershipChangeMode.REMOVE);
        final Future f2 = leader.replicate(new MembershipChangeTest.PostponedResponseRaftRunnable());
        Assert.assertFalse(f1.isDone());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(f2.isDone());
            }
        });
        try {
            f2.get();
            Assert.fail();
        } catch (CannotReplicateException ignored) {
        }
    }

    static class PostponedResponseRaftRunnable implements RaftRunnable {
        @Override
        public Object run(Object service, long commitIndex) {
            return PostponedResponse.INSTANCE;
        }
    }
}

