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


import RaftNodeStatus.ACTIVE;
import RaftNodeStatus.TERMINATED;
import RaftNodeStatus.TERMINATING;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.CannotReplicateException;
import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.dataservice.ApplyRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.testing.LocalRaftGroup;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DestroyRaftGroupTest extends HazelcastTestSupport {
    private LocalRaftGroup group;

    @Test
    public void when_destroyOpIsAppendedButNotCommitted_then_cannotAppendNewEntry() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(2, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        RaftNodeImpl follower = group.getAnyFollowerNode();
        group.dropAllMessagesToMember(leader.getLocalMember(), follower.getLocalMember());
        leader.replicate(new DestroyRaftGroupCmd());
        try {
            leader.replicate(new ApplyRaftRunnable("val")).get();
            Assert.fail();
        } catch (CannotReplicateException ignored) {
        }
    }

    @Test
    public void when_destroyOpIsAppended_then_statusIsTerminating() {
        group = RaftUtil.newGroupWithService(2, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl follower = group.getAnyFollowerNode();
        group.dropAllMessagesToMember(follower.getLocalMember(), leader.getLocalMember());
        leader.replicate(new DestroyRaftGroupCmd());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(TERMINATING, RaftUtil.getStatus(leader));
                Assert.assertEquals(TERMINATING, RaftUtil.getStatus(follower));
            }
        });
    }

    @Test
    public void when_destroyOpIsCommitted_then_raftNodeIsTerminated() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(2, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl follower = group.getAnyFollowerNode();
        leader.replicate(new DestroyRaftGroupCmd()).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, RaftUtil.getCommitIndex(leader));
                Assert.assertEquals(1, RaftUtil.getCommitIndex(follower));
                Assert.assertEquals(TERMINATED, RaftUtil.getStatus(leader));
                Assert.assertEquals(TERMINATED, RaftUtil.getStatus(follower));
            }
        });
        try {
            leader.replicate(new ApplyRaftRunnable("val")).get();
            Assert.fail();
        } catch (CPGroupDestroyedException ignored) {
        }
        try {
            follower.replicate(new ApplyRaftRunnable("val")).get();
            Assert.fail();
        } catch (CPGroupDestroyedException ignored) {
        }
    }

    @Test
    public void when_destroyOpIsTruncated_then_statusIsActive() throws InterruptedException, ExecutionException {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl[] followers = group.getNodesExcept(leader.getLocalMember());
        group.dropMessagesToAll(leader.getLocalMember(), AppendRequest.class);
        leader.replicate(new DestroyRaftGroupCmd());
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
        final RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followers[0]));
        for (int i = 0; i < 10; i++) {
            newLeader.replicate(new ApplyRaftRunnable(("val" + i))).get();
        }
        group.merge();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl raftNode : group.getNodes()) {
                    Assert.assertEquals(ACTIVE, RaftUtil.getStatus(raftNode));
                }
            }
        });
    }
}

