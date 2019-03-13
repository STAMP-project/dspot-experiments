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


import QueryPolicy.ANY_LOCAL;
import QueryPolicy.LEADER_LOCAL;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.raft.impl.dataservice.ApplyRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dataservice.QueryRaftRunnable;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.testing.LocalRaftGroup;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalQueryTest extends HazelcastTestSupport {
    private LocalRaftGroup group;

    @Test
    public void when_queryFromLeader_withoutAnyCommit_thenReturnDefaultValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        Object o = leader.query(new QueryRaftRunnable(), LEADER_LOCAL).get();
        Assert.assertNull(o);
    }

    @Test
    public void when_queryFromFollower_withoutAnyCommit_thenReturnDefaultValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        group.waitUntilLeaderElected();
        RaftNodeImpl follower = group.getAnyFollowerNode();
        Object o = follower.query(new QueryRaftRunnable(), ANY_LOCAL).get();
        Assert.assertNull(o);
    }

    @Test
    public void when_queryFromLeader_onStableCluster_thenReadLatestValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        int count = 3;
        for (int i = 1; i <= count; i++) {
            leader.replicate(new ApplyRaftRunnable(("value" + i))).get();
        }
        Object result = leader.query(new QueryRaftRunnable(), LEADER_LOCAL).get();
        Assert.assertEquals(("value" + count), result);
    }

    @Test(expected = NotLeaderException.class)
    public void when_queryFromFollower_withLeaderLocalPolicy_thenFail() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("value")).get();
        RaftNodeImpl follower = group.getAnyFollowerNode();
        follower.query(new QueryRaftRunnable(), LEADER_LOCAL).get();
    }

    @Test
    public void when_queryFromFollower_onStableCluster_thenReadLatestValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        int count = 3;
        for (int i = 1; i <= count; i++) {
            leader.replicate(new ApplyRaftRunnable(("value" + i))).get();
        }
        RaftNodeImpl follower = group.getAnyFollowerNode();
        Object result = follower.query(new QueryRaftRunnable(), ANY_LOCAL).get();
        Assert.assertEquals(("value" + count), result);
    }

    @Test
    public void when_queryFromSlowFollower_thenReadStaleValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        final RaftNodeImpl slowFollower = group.getAnyFollowerNode();
        Object firstValue = "value1";
        leader.replicate(new ApplyRaftRunnable(firstValue)).get();
        final long leaderCommitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(leaderCommitIndex, RaftUtil.getCommitIndex(slowFollower));
            }
        });
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        leader.replicate(new ApplyRaftRunnable("value2")).get();
        Object result = slowFollower.query(new QueryRaftRunnable(), ANY_LOCAL).get();
        Assert.assertEquals(firstValue, result);
    }

    @Test
    public void when_queryFromSlowFollower_thenEventuallyReadLatestValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        RaftNodeImpl leader = group.waitUntilLeaderElected();
        leader.replicate(new ApplyRaftRunnable("value1")).get();
        final RaftNodeImpl slowFollower = group.getAnyFollowerNode();
        group.dropMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember(), AppendRequest.class);
        final Object lastValue = "value2";
        leader.replicate(new ApplyRaftRunnable(lastValue)).get();
        group.allowAllMessagesToMember(leader.getLocalMember(), slowFollower.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Object result = slowFollower.query(new QueryRaftRunnable(), ANY_LOCAL).get();
                Assert.assertEquals(lastValue, result);
            }
        });
    }

    @Test
    public void when_queryFromSplitLeader_thenReadStaleValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        Object firstValue = "value1";
        leader.replicate(new ApplyRaftRunnable(firstValue)).get();
        final long leaderCommitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl node : group.getNodes()) {
                    Assert.assertEquals(leaderCommitIndex, RaftUtil.getCommitIndex(node));
                }
            }
        });
        final RaftNodeImpl followerNode = group.getAnyFollowerNode();
        group.split(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Endpoint leaderEndpoint = RaftUtil.getLeaderMember(followerNode);
                Assert.assertNotNull(leaderEndpoint);
                Assert.assertNotEquals(leader.getLocalMember(), leaderEndpoint);
            }
        });
        RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followerNode));
        Object lastValue = "value2";
        newLeader.replicate(new ApplyRaftRunnable(lastValue)).get();
        Object result1 = newLeader.query(new QueryRaftRunnable(), ANY_LOCAL).get();
        Assert.assertEquals(lastValue, result1);
        Object result2 = leader.query(new QueryRaftRunnable(), ANY_LOCAL).get();
        Assert.assertEquals(firstValue, result2);
    }

    @Test
    public void when_queryFromSplitLeader_thenEventuallyReadLatestValue() throws Exception {
        group = RaftUtil.newGroupWithService(3, new RaftAlgorithmConfig());
        group.start();
        final RaftNodeImpl leader = group.waitUntilLeaderElected();
        Object firstValue = "value1";
        leader.replicate(new ApplyRaftRunnable(firstValue)).get();
        final long leaderCommitIndex = RaftUtil.getCommitIndex(leader);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (RaftNodeImpl node : group.getNodes()) {
                    Assert.assertEquals(leaderCommitIndex, RaftUtil.getCommitIndex(node));
                }
            }
        });
        final RaftNodeImpl followerNode = group.getAnyFollowerNode();
        group.split(leader.getLocalMember());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Endpoint leaderEndpoint = RaftUtil.getLeaderMember(followerNode);
                Assert.assertNotNull(leaderEndpoint);
                Assert.assertNotEquals(leader.getLocalMember(), leaderEndpoint);
            }
        });
        RaftNodeImpl newLeader = group.getNode(RaftUtil.getLeaderMember(followerNode));
        final Object lastValue = "value2";
        newLeader.replicate(new ApplyRaftRunnable(lastValue)).get();
        group.merge();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Object result = leader.query(new QueryRaftRunnable(), ANY_LOCAL).get();
                Assert.assertEquals(lastValue, result);
            }
        });
    }
}

