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


import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CannotReplicateException;
import com.hazelcast.cp.exception.LeaderDemotedException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.raft.impl.dataservice.ApplyRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dataservice.RaftDataService;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.testing.LocalRaftGroup;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalRaftTest extends HazelcastTestSupport {
    private LocalRaftGroup group;

    @Test
    public void when_twoNodeCluster_then_leaderIsElected() {
        testLeaderElection(2);
    }

    @Test
    public void when_threeNodeCluster_then_leaderIsElected() {
        testLeaderElection(3);
    }

    @Test
    public void when_twoNodeCluster_then_singleEntryCommitted() throws Exception {
        testSingleCommitEntry(2);
    }

    @Test
    public void when_threeNodeCluster_then_singleEntryCommitted() throws Exception {
        testSingleCommitEntry(3);
    }

    @Test(expected = NotLeaderException.class)
    public void when_followerAttemptsToReplicate_then_itFails() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        followers[0].replicate(new ApplyRaftRunnable("val")).get();
        for (RaftNodeImpl raftNode : group.getNodes()) {
            RaftDataService service = group.getIntegration(raftNode.getLocalMember()).getService();
            Assert.assertEquals(0, service.size());
        }
    }

    @Test
    public void when_twoNodeCluster_then_leaderCannotCommitWithOnlyLocalAppend() throws InterruptedException, ExecutionException {
        testNoCommitWhenOnlyLeaderAppends(2);
    }

    @Test
    public void when_threeNodeCluster_then_leaderCannotCommitWithOnlyLocalAppend() throws InterruptedException, ExecutionException {
        testNoCommitWhenOnlyLeaderAppends(3);
    }

    @Test
    public void when_leaderAppendsToMinority_then_itCannotCommit() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(5, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        for (int i = 1; i < (followers.length); i++) {
            group.dropMessagesToMember(leader.getLocalMember(), followers[i].getLocalMember(), AppendRequest.class);
        }
        Future f = leader.replicate(new ApplyRaftRunnable("val"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, RaftUtil.getLastLogOrSnapshotEntry(leader).index());
                Assert.assertEquals(1, RaftUtil.getLastLogOrSnapshotEntry(followers[0]).index());
            }
        });
        try {
            f.get(10, TimeUnit.SECONDS);
            Assert.fail();
        } catch (TimeoutException ignored) {
        }
        for (RaftNodeImpl raftNode : group.getNodes()) {
            Assert.assertEquals(0, RaftUtil.getCommitIndex(raftNode));
            RaftDataService service = group.getIntegration(raftNode.getLocalMember()).getService();
            Assert.assertEquals(0, service.size());
        }
    }

    @Test
    public void when_fourNodeCluster_then_leaderReplicateEntriesSequentially() throws InterruptedException, ExecutionException {
        testReplicateEntriesSequentially(4);
    }

    @Test
    public void when_fiveNodeCluster_then_leaderReplicateEntriesSequentially() throws InterruptedException, ExecutionException {
        testReplicateEntriesSequentially(5);
    }

    @Test
    public void when_fourNodeCluster_then_leaderReplicatesEntriesConcurrently() throws InterruptedException, ExecutionException {
        testReplicateEntriesConcurrently(4);
    }

    @Test
    public void when_fiveNodeCluster_then_leaderReplicatesEntriesConcurrently() throws InterruptedException, ExecutionException {
        testReplicateEntriesConcurrently(5);
    }

    @Test
    public void when_fourNodeCluster_then_entriesAreSubmittedInParallel() throws InterruptedException {
        testReplicateEntriesInParallel(4);
    }

    @Test
    public void when_fiveNodeCluster_then_entriesAreSubmittedInParallel() throws InterruptedException {
        testReplicateEntriesInParallel(5);
    }

    @Test
    public void when_followerSlowsDown_then_itCatchesLeaderEventually() throws InterruptedException, ExecutionException {
        final int entryCount = 100;
        group = RaftUtil.newGroupWithService(3, LocalRaftTest.newRaftConfigWithNoSnapshotting(entryCount));
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl slowFollower = group.getAnyFollowerNode();
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        for (int i = 0; i < entryCount; i++) {
            final Object val = "val" + i;
            leader.replicate(new ApplyRaftRunnable(val)).get();
        }
        Assert.assertEquals(0, RaftUtil.getCommitIndex(slowFollower));
        group.resetAllRulesFrom(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(entryCount, RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(entryCount, service.size());
                    Set<Object> values = service.values();
                    for (int i = 0; i < entryCount; i++) {
                        Object val = "val" + i;
                        Assert.assertTrue(values.contains(val));
                    }
                }
            }
        });
    }

    @Test
    public void when_disruptiveFollowerStartsElection_then_itCannotTakeOverLeadershipFromLegitimateLeader() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        final int leaderTerm = RaftUtil.getTerm(leader);
        final RaftNodeImpl disruptiveFollower = group.getAnyFollowerNode();
        group.dropMessagesToMember(leader.getLocalMember(), disruptiveFollower.getLocalMember(), AppendRequest.class);
        leader.replicate(new ApplyRaftRunnable("val")).get();
        group.split(disruptiveFollower.getLocalMember());
        final int[] disruptiveFollowerTermRef = new int[1];
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                int followerTerm = RaftUtil.getTerm(disruptiveFollower);
                Assert.assertEquals(leaderTerm, followerTerm);
                disruptiveFollowerTermRef[0] = followerTerm;
            }
        }, 5);
        group.resetAllRulesFrom(leader.getLocalMember());
        group.merge();
        RaftNodeImpl newLeader = group.waitUntilLeaderElected();
        Assert.assertNotEquals(disruptiveFollower.getLocalMember(), newLeader.getLocalMember());
        Assert.assertEquals(RaftUtil.getTerm(newLeader), disruptiveFollowerTermRef[0]);
    }

    @Test
    public void when_followerTerminatesInMinority_then_clusterRemainsAvailable() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leaderNode = group.waitUntilLeaderElected();
        int leaderIndex = group.getLeaderIndex();
        for (int i = 0; i < (group.size()); i++) {
            if (i != leaderIndex) {
                group.terminateNode(i);
                break;
            }
        }
        String value = "value";
        Future future = leaderNode.replicate(new ApplyRaftRunnable(value));
        Assert.assertEquals(value, future.get());
    }

    @Test
    public void when_leaderTerminatesInMinority_then_clusterRemainsAvailable() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leaderNode = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leaderNode.getLocalMember());
        int leaderTerm = RaftUtil.getTerm(leaderNode);
        group.terminateNode(leaderNode.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertNotEquals(leaderNode.getLocalMember(), RaftUtil.getLeaderMember(raftNode));
                }
            }
        });
        RaftNodeImpl newLeader = group.waitUntilLeaderElected();
        Assert.assertTrue(((RaftUtil.getTerm(newLeader)) > leaderTerm));
        String value = "value";
        Future future = newLeader.replicate(new ApplyRaftRunnable(value));
        Assert.assertEquals(value, future.get());
    }

    @Test
    public void when_leaderStaysInMajorityDuringSplit_thenItMergesBackSuccessfully() {
        group = new LocalRaftGroup(5);
        group.start();
        group.waitUntilLeaderElected();
        final int[] split = group.createMinoritySplitIndexes(false);
        group.split(split);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int ix : split) {
                    Assert.assertNull(RaftUtil.getLeaderMember(group.getNode(ix)));
                }
            }
        });
        group.merge();
        group.waitUntilLeaderElected();
    }

    @Test
    public void when_leaderStaysInMinorityDuringSplit_thenItMergesBackSuccessfully() {
        int nodeCount = 5;
        group = new LocalRaftGroup(nodeCount);
        group.start();
        final Endpoint leaderEndpoint = group.waitUntilLeaderElected().getLocalMember();
        final int[] split = group.createMajoritySplitIndexes(false);
        group.split(split);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int ix : split) {
                    Assert.assertNotEquals(leaderEndpoint, RaftUtil.getLeaderMember(group.getNode(ix)));
                }
            }
        });
        for (int i = 0; i < nodeCount; i++) {
            if ((Arrays.binarySearch(split, i)) < 0) {
                Assert.assertEquals(leaderEndpoint, RaftUtil.getLeaderMember(group.getNode(i)));
            }
        }
        group.merge();
        group.waitUntilLeaderElected();
    }

    @Test
    public void when_leaderCrashes_then_theFollowerWithLongestLogBecomesLeader() throws Exception {
        group = RaftUtil.newGroupWithService(4, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val1")).get();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl nextLeader = followers[0];
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
        for (int i = 1; i < (followers.length); i++) {
            group.dropMessagesToMember(leader.getLocalMember(), followers[i].getLocalMember(), AppendRequest.class);
        }
        leader.replicate(new ApplyRaftRunnable("val2"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(nextLeader).index()) > commitIndex));
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        }, 10);
        group.terminateNode(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertEquals(nextLeader.getLocalMember(), RaftUtil.getLeaderMember(raftNode));
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                    Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(raftNode).index()) > commitIndex));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(1, service.size());
                    Assert.assertEquals("val1", service.get(1));
                    // val2 not committed yet
                }
            }
        });
    }

    @Test
    public void when_followerBecomesLeaderWithUncommittedEntries_then_thoseEntriesAreCommittedWithANewEntryOfCurrentTerm() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val1")).get();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl nextLeader = followers[0];
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), AppendRequest.class);
        group.dropMessagesToMember(nextLeader.getLocalMember(), leader.getLocalMember(), AppendSuccessResponse.class);
        leader.replicate(new ApplyRaftRunnable("val2"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(nextLeader).index()) > commitIndex));
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        }, 10);
        group.terminateNode(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertEquals(nextLeader.getLocalMember(), RaftUtil.getLeaderMember(raftNode));
                }
            }
        });
        nextLeader.replicate(new ApplyRaftRunnable("val3"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertEquals(3, RaftUtil.getCommitIndex(raftNode));
                    Assert.assertEquals(3, RaftUtil.getLastLogOrSnapshotEntry(raftNode).index());
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(3, service.size());
                    Assert.assertEquals("val1", service.get(1));
                    Assert.assertEquals("val2", service.get(2));
                    Assert.assertEquals("val3", service.get(3));
                }
            }
        });
    }

    @Test
    public void when_leaderCrashes_then_theFollowerWithLongestLogMayNotBecomeLeaderIfItsLogIsNotMajority() throws Exception {
        group = RaftUtil.newGroupWithService(5, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val1")).get();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl followerWithLongestLog = followers[0];
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
        for (int i = 1; i < (followers.length); i++) {
            group.dropMessagesToMember(leader.getLocalMember(), followers[i].getLocalMember(), AppendRequest.class);
        }
        leader.replicate(new ApplyRaftRunnable("val2"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(followerWithLongestLog).index()) > commitIndex));
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(raftNode));
                }
            }
        }, 10);
        group.dropMessagesToMember(followerWithLongestLog.getLocalMember(), followers[1].getLocalMember(), VoteRequest.class);
        group.dropMessagesToMember(followerWithLongestLog.getLocalMember(), followers[2].getLocalMember(), VoteRequest.class);
        group.terminateNode(leader.getLocalMember());
        RaftNodeImpl newLeader = group.waitUntilLeaderElected();
        // followerWithLongestLog has 2 entries, other 3 followers have 1 entry
        // and those 3 followers will elect a leader among themselves
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Endpoint newLeader = RaftUtil.getLeaderMember(raftNode);
                    Assert.assertNotEquals(leader.getLocalMember(), newLeader);
                    Assert.assertNotEquals(followerWithLongestLog.getLocalMember(), newLeader);
                }
            }
        });
        for (int i = 1; i < (followers.length); i++) {
            Assert.assertEquals(commitIndex, RaftUtil.getCommitIndex(followers[i]));
            Assert.assertEquals(commitIndex, RaftUtil.getLastLogOrSnapshotEntry(followers[i]).index());
        }
        // followerWithLongestLog does not truncate its extra log entry until the new leader appends a new entry
        Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(followerWithLongestLog).index()) > commitIndex));
        newLeader.replicate(new ApplyRaftRunnable("val3")).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : followers) {
                    Assert.assertEquals(2, RaftUtil.getCommitIndex(follower));
                    RaftDataService service = group.getService(follower);
                    Assert.assertEquals(2, service.size());
                    Assert.assertEquals("val1", service.get(1));
                    Assert.assertEquals("val3", service.get(2));
                }
            }
        });
        Assert.assertEquals(2, RaftUtil.getLastLogOrSnapshotEntry(followerWithLongestLog).index());
    }

    @Test
    public void when_leaderStaysInMinorityDuringSplit_then_itCannotCommitNewEntries() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, LocalRaftTest.newRaftConfigWithNoSnapshotting(100));
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("val1")).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(1, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        group.split(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Endpoint leaderEndpoint = RaftUtil.getLeaderMember(raftNode);
                    Assert.assertNotNull(leaderEndpoint);
                    Assert.assertNotEquals(leader.getLocalMember(), leaderEndpoint);
                }
            }
        });
        List<Future> isolatedFutures = new ArrayList<Future>();
        for (int i = 0; i < 10; i++) {
            isolatedFutures.add(leader.replicate(new ApplyRaftRunnable(("isolated" + i))));
        }
        RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followers[0]));
        for (int i = 0; i < 10; i++) {
            newLeader.replicate(new ApplyRaftRunnable(("valNew" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertEquals(11, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
        group.merge();
        RaftNodeImpl finalLeader = group.waitUntilLeaderElected();
        Assert.assertNotEquals(leader.getLocalMember(), finalLeader.getLocalMember());
        for (Future f : isolatedFutures) {
            try {
                f.get();
                Assert.fail();
            } catch (LeaderDemotedException ignored) {
            }
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(11, service.size());
                    Assert.assertEquals("val1", service.get(1));
                    for (int i = 0; i < 10; i++) {
                        Assert.assertEquals(("valNew" + i), service.get((i + 2)));
                    }
                }
            }
        });
    }

    @Test
    public void when_thereAreTooManyInflightAppendedEntries_then_newAppendsAreRejected() throws InterruptedException, ExecutionException {
        int uncommittedEntryCount = 10;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setUncommittedEntryCountToRejectNewAppends(uncommittedEntryCount);
        group = RaftUtil.newGroupWithService(2, raftAlgorithmConfig);
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl follower = group.getAnyFollowerNode();
        group.terminateNode(follower.getLocalMember());
        for (int i = 0; i < uncommittedEntryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i)));
        }
        try {
            leader.replicate(new ApplyRaftRunnable("valFinal")).get();
            Assert.fail();
        } catch (CannotReplicateException ignored) {
        }
    }
}

