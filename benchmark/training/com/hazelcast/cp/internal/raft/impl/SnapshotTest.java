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
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.StaleAppendRequestException;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.dataservice.ApplyRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dataservice.RaftDataService;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.testing.LocalRaftGroup;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SnapshotTest extends HazelcastTestSupport {
    private LocalRaftGroup group;

    @Test
    public void when_commitLogAdvances_then_snapshotIsTaken() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        for (int i = 0; i < entryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(entryCount, RaftUtil.getCommitIndex(raftNode));
                    Assert.assertEquals(entryCount, RaftUtil.getSnapshotEntry(raftNode).index());
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(entryCount, service.size());
                    for (int i = 0; i < entryCount; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                }
            }
        });
    }

    @Test
    public void when_snapshotIsTaken_then_nextEntryIsCommitted() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        for (int i = 0; i < entryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(entryCount, RaftUtil.getCommitIndex(raftNode));
                    Assert.assertEquals(entryCount, RaftUtil.getSnapshotEntry(raftNode).index());
                }
            }
        });
        leader.replicate(new ApplyRaftRunnable("valFinal")).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals((entryCount + 1), RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals((entryCount + 1), service.size());
                    for (int i = 0; i < entryCount; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                    Assert.assertEquals("valFinal", service.get(51));
                }
            }
        });
    }

    @Test
    public void when_followerFallsTooFarBehind_then_itInstallsSnapshot() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl slowFollower = followers[1];
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        for (int i = 0; i < entryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(entryCount, RaftUtil.getSnapshotEntry(leader).index());
            }
        });
        leader.replicate(new ApplyRaftRunnable("valFinal")).get();
        group.resetAllRulesFrom(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals((entryCount + 1), RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals((entryCount + 1), service.size());
                    for (int i = 0; i < entryCount; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                    Assert.assertEquals("valFinal", service.get(51));
                }
            }
        });
    }

    @Test
    public void when_leaderMissesInstallSnapshotResponse_then_itAdvancesMatchIndexWithNextInstallSnapshotResponse() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount).setAppendRequestBackoffTimeoutInMillis(1000);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl slowFollower = followers[1];
        // the leader cannot send AppendEntriesRPC to the follower
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        // the follower cannot send append response to the leader after installing the snapshot
        group.dropMessagesToMember(slowFollower.getLocalMember(), leader.getLocalMember(), AppendSuccessResponse.class);
        for (int i = 0; i < entryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(entryCount, RaftUtil.getSnapshotEntry(leader).index());
            }
        });
        leader.replicate(new ApplyRaftRunnable("valFinal")).get();
        group.resetAllRulesFrom(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodesExcept(slowFollower.getLocalMember())) {
                    Assert.assertEquals((entryCount + 1), RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals((entryCount + 1), service.size());
                    for (int i = 0; i < entryCount; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                    Assert.assertEquals("valFinal", service.get(51));
                }
                Assert.assertEquals(entryCount, RaftUtil.getCommitIndex(slowFollower));
                RaftDataService service = group.getService(slowFollower);
                Assert.assertEquals(entryCount, service.size());
                for (int i = 0; i < entryCount; i++) {
                    Assert.assertEquals(("val" + i), service.get((i + 1)));
                }
            }
        });
        group.resetAllRulesFrom(slowFollower.getLocalMember());
        final long commitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNode raftNode : group.getNodesExcept(leader.getLocalMember())) {
                    Assert.assertEquals(commitIndex, RaftUtil.getMatchIndex(leader, raftNode.getLocalMember()));
                }
            }
        });
    }

    @Test
    public void when_followerMissesTheLastEntryThatGoesIntoTheSnapshot_then_itInstallsSnapshot() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl slowFollower = followers[1];
        for (int i = 0; i < (entryCount - 1); i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl follower : group.getNodesExcept(leader.getLocalMember())) {
                    Assert.assertEquals((entryCount - 1), RaftUtil.getMatchIndex(leader, follower.getLocalMember()));
                }
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        leader.replicate(new ApplyRaftRunnable(("val" + (entryCount - 1)))).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(entryCount, RaftUtil.getSnapshotEntry(leader).index());
            }
        });
        leader.replicate(new ApplyRaftRunnable("valFinal")).get();
        group.resetAllRulesFrom(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals((entryCount + 1), RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals((entryCount + 1), service.size());
                    for (int i = 0; i < entryCount; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                    Assert.assertEquals("valFinal", service.get(51));
                }
            }
        }, 30);
    }

    @Test
    public void when_isolatedLeaderAppendsEntries_then_itInvalidatesTheirFeaturesUponInstallSnapshot() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        RaftAlgorithmConfig raftAlgorithmConfig = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount);
        group = RaftUtil.newGroupWithService(3, raftAlgorithmConfig);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        for (int i = 0; i < 40; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(40, RaftUtil.getCommitIndex(raftNode));
                }
            }
        });
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
        List<Future> futures = new ArrayList<Future>();
        for (int i = 40; i < 45; i++) {
            Future f = leader.replicate(new ApplyRaftRunnable(("isolated" + i)));
            futures.add(f);
        }
        final RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followers[0]));
        for (int i = 40; i < 51; i++) {
            newLeader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : followers) {
                    Assert.assertTrue(((RaftUtil.getSnapshotEntry(raftNode).index()) > 0));
                }
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), followers[0].getLocalMember(), AppendRequest.class);
        group.dropMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), AppendRequest.class);
        group.merge();
        for (Future f : futures) {
            try {
                f.get();
                Assert.fail();
            } catch (StaleAppendRequestException ignored) {
            }
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(51, RaftUtil.getCommitIndex(raftNode));
                    RaftDataService service = group.getService(raftNode);
                    Assert.assertEquals(51, service.size());
                    for (int i = 0; i < 51; i++) {
                        Assert.assertEquals(("val" + i), service.get((i + 1)));
                    }
                }
            }
        });
    }

    @Test
    public void when_followersLastAppendIsMembershipChange_then_itUpdatesRaftNodeStateWithInstalledSnapshot() throws InterruptedException, ExecutionException {
        final int entryCount = 50;
        group = RaftUtil.newGroupWithService(5, new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(entryCount));
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
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), InstallSnapshot.class);
        for (RaftNodeImpl follower : followers) {
            if (follower != slowFollower) {
                group.allowAllMessagesToMember(follower.getLocalMember(), leader.getLeader());
            }
        }
        f1.get();
        for (int i = 0; i < entryCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertThat(RaftUtil.getSnapshotEntry(leader).index(), Matchers.greaterThanOrEqualTo(((long) (entryCount))));
            }
        });
        group.allowAllMessagesToMember(leader.getLeader(), slowFollower.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertThat(RaftUtil.getSnapshotEntry(slowFollower).index(), Matchers.greaterThanOrEqualTo(((long) (entryCount))));
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(RaftUtil.getCommittedGroupMembers(leader).index(), RaftUtil.getCommittedGroupMembers(slowFollower).index());
                Assert.assertEquals(RaftNodeStatus.ACTIVE, RaftUtil.getStatus(slowFollower));
            }
        });
    }

    @Test
    public void testMembershipChangeBlocksSnapshotBug() throws InterruptedException, ExecutionException {
        // The comments below show how the code behaves before the mentioned bug is fixed.
        int commitIndexAdvanceCount = 50;
        final int uncommittedEntryCount = 10;
        RaftAlgorithmConfig config = new RaftAlgorithmConfig().setCommitIndexAdvanceCountToSnapshot(commitIndexAdvanceCount).setUncommittedEntryCountToRejectNewAppends(uncommittedEntryCount);
        group = RaftUtil.newGroupWithService(3, config);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        group.dropMessagesToMember(leader.getLocalMember(), followers[0].getLocalMember(), AppendRequest.class);
        while ((RaftUtil.getSnapshotEntry(leader).index()) == 0) {
            leader.replicate(new ApplyRaftRunnable("into_snapshot")).get();
        } 
        // now, the leader has taken a snapshot.
        // It also keeps some already committed entries in the log because followers[0] hasn't appended them.
        // LOG: [ <46 - 49>, <50>], SNAPSHOT INDEX: 50, COMMIT INDEX: 50
        long leaderCommitIndex = RaftUtil.getCommitIndex(leader);
        do {
            leader.replicate(new ApplyRaftRunnable("committed_after_snapshot")).get();
        } while ((RaftUtil.getCommitIndex(leader)) < ((leaderCommitIndex + commitIndexAdvanceCount) - 1) );
        // committing new entries.
        // one more entry is needed to take the next snapshot.
        // LOG: [ <46 - 49>, <50>, <51 - 99 (committed)> ], SNAPSHOT INDEX: 50, COMMIT INDEX: 99
        group.dropMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), AppendRequest.class);
        for (int i = 0; i < (uncommittedEntryCount - 1); i++) {
            leader.replicate(new ApplyRaftRunnable("uncommitted_after_snapshot"));
        }
        // appended some more entries which will not be committed because the leader has no majority.
        // the last uncommitted index is reserved for membership changed.
        // LOG: [ <46 - 49>, <50>, <51 - 99 (committed)>, <100 - 108 (uncommitted)> ], SNAPSHOT INDEX: 50, COMMIT INDEX: 99
        // There are only 2 empty indices in the log.
        RaftNodeImpl newRaftNode = group.createNewRaftNode();
        Function<Object, Object> alterFunc = new Function<Object, Object>() {
            @Override
            public Object apply(Object o) {
                if (o instanceof AppendRequest) {
                    AppendRequest request = ((AppendRequest) (o));
                    LogEntry[] entries = request.entries();
                    if ((entries.length) > 0) {
                        if ((entries[((entries.length) - 1)].operation()) instanceof UpdateRaftGroupMembersCmd) {
                            entries = Arrays.copyOf(entries, ((entries.length) - 1));
                            return new AppendRequest(request.leader(), request.term(), request.prevLogTerm(), request.prevLogIndex(), request.leaderCommitIndex(), entries);
                        } else
                            if ((entries[0].operation()) instanceof UpdateRaftGroupMembersCmd) {
                                entries = new LogEntry[0];
                                return new AppendRequest(request.leader(), request.term(), request.prevLogTerm(), request.prevLogIndex(), request.leaderCommitIndex(), entries);
                            }

                    }
                }
                return null;
            }
        };
        group.alterMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), alterFunc);
        group.alterMessagesToMember(leader.getLocalMember(), newRaftNode.getLocalMember(), alterFunc);
        final long lastLogIndex1 = RaftUtil.getLastLogOrSnapshotEntry(leader).index();
        leader.replicateMembershipChange(newRaftNode.getLocalMember(), ADD);
        // When the membership change entry is appended, the leader's Log will be as following:
        // LOG: [ <46 - 49>, <50>, <51 - 99 (committed)>, <100 - 108 (uncommitted)>, <109 (membership change)> ], SNAPSHOT INDEX: 50, COMMIT INDEX: 99
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(leader).index()) > lastLogIndex1));
            }
        });
        group.allowMessagesToMember(leader.getLocalMember(), followers[1].getLocalMember(), AppendRequest.class);
        System.out.println();
        // Then, only the entries before the membership change will be committed because we alter the append request. The log will be:
        // LOG: [ <46 - 49>, <50>, <51 - 108 (committed)>, <109 (membership change)> ], SNAPSHOT INDEX: 50, COMMIT INDEX: 108
        // There is only 1 empty index in the log.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(lastLogIndex1, RaftUtil.getCommitIndex(leader));
                Assert.assertEquals(lastLogIndex1, RaftUtil.getCommitIndex(followers[1]));
            }
        });
        // assertTrueEventually(new AssertTask() {
        // @Override
        // public void run()  {
        // assertEquals(lastLogIndex1 + 1, getCommitIndex(leader));
        // assertEquals(lastLogIndex1 + 1, getCommitIndex(followers[1]));
        // }
        // });
        final long lastLogIndex2 = RaftUtil.getLastLogOrSnapshotEntry(leader).index();
        leader.replicate(new ApplyRaftRunnable("after_membership_change_append"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(leader).index()) > lastLogIndex2));
            }
        });
        // Now the log is full. There is no empty space left.
        // LOG: [ <46 - 49>, <50>, <51 - 108 (committed)>, <109 (membership change)>, <110 (uncommitted)> ], SNAPSHOT INDEX: 50, COMMIT INDEX: 108
        final long lastLogIndex3 = RaftUtil.getLastLogOrSnapshotEntry(leader).index();
        Future f = leader.replicate(new ApplyRaftRunnable("after_membership_change_append"));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getLastLogOrSnapshotEntry(leader).index()) > lastLogIndex3));
            }
        });
        Assert.assertFalse(f.isDone());
    }

    @Test
    public void when_slowFollowerReceivesAppendRequestThatDoesNotFitIntoItsRaftLog_then_itTruncatesAppendRequestEntries() throws InterruptedException, ExecutionException {
        int appendRequestMaxEntryCount = 100;
        final int commitIndexAdvanceCount = 100;
        int uncommittedEntryCount = 10;
        RaftAlgorithmConfig config = new RaftAlgorithmConfig().setAppendRequestMaxEntryCount(appendRequestMaxEntryCount).setCommitIndexAdvanceCountToSnapshot(commitIndexAdvanceCount).setUncommittedEntryCountToRejectNewAppends(uncommittedEntryCount);
        group = RaftUtil.newGroupWithService(5, config);
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        final RaftNodeImpl slowFollower1 = followers[0];
        final RaftNodeImpl slowFollower2 = followers[1];
        int count = 1;
        for (int i = 0; i < commitIndexAdvanceCount; i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + (count++)))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl node : group.getNodes()) {
                    Assert.assertTrue(((RaftUtil.getSnapshotEntry(node).index()) > 0));
                }
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower1.getLocalMember(), AppendRequest.class);
        for (int i = 0; i < (commitIndexAdvanceCount - 1); i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + (count++)))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(RaftUtil.getCommitIndex(leader), RaftUtil.getCommitIndex(slowFollower2));
            }
        });
        // slowFollower2's log: [ <91 - 100 before snapshot>, <100 snapshot>, <101 - 199 committed> ]
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower2.getLocalMember(), AppendRequest.class);
        for (int i = 0; i < (commitIndexAdvanceCount / 2); i++) {
            leader.replicate(new ApplyRaftRunnable(("val" + (count++)))).get();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(leader).index()) > commitIndexAdvanceCount));
            }
        });
        // leader's log: [ <191 - 199 before snapshot>, <200 snapshot>, <201 - 249 committed> ]
        group.allowMessagesToMember(leader.getLocalMember(), slowFollower2.getLocalMember(), AppendRequest.class);
        // leader replicates 50 entries to slowFollower2 but slowFollower2 has only available capacity of 11 indices.
        // so, slowFollower2 appends 11 of these 50 entries in the first AppendRequest, takes a snapshot,
        // and receives another AppendRequest for the remaining entries...
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(RaftUtil.getCommitIndex(leader), RaftUtil.getCommitIndex(slowFollower2));
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(slowFollower2).index()) > commitIndexAdvanceCount));
            }
        });
    }
}

