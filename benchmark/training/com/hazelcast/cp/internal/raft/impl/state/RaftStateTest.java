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
package com.hazelcast.cp.internal.raft.impl.state;


import RaftRole.CANDIDATE;
import RaftRole.FOLLOWER;
import RaftRole.LEADER;
import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.RaftLog;
import com.hazelcast.cp.internal.raft.impl.testing.TestRaftMember;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftStateTest {
    private RaftState state;

    private String name = HazelcastTestSupport.randomName();

    private CPGroupId groupId;

    private TestRaftMember localMember;

    private Collection<Endpoint> members;

    @Test
    public void test_initialState() {
        Assert.assertEquals(name, state.name());
        Assert.assertEquals(groupId, state.groupId());
        Assert.assertEquals(members.size(), state.memberCount());
        Assert.assertEquals(members, state.members());
        Collection<Endpoint> remoteMembers = new HashSet<Endpoint>(members);
        remoteMembers.remove(localMember);
        Assert.assertEquals(remoteMembers, state.remoteMembers());
        Assert.assertEquals(0, state.term());
        Assert.assertEquals(FOLLOWER, state.role());
        Assert.assertNull(state.leader());
        Assert.assertEquals(0, state.commitIndex());
        Assert.assertEquals(0, state.lastApplied());
        Assert.assertEquals(3, state.majority());
        Assert.assertNull(state.votedFor());
        Assert.assertEquals(0, state.lastVoteTerm());
        Assert.assertNull(state.leaderState());
        Assert.assertNull(state.candidateState());
        RaftLog log = state.log();
        Assert.assertEquals(0, log.lastLogOrSnapshotIndex());
        Assert.assertEquals(0, log.lastLogOrSnapshotTerm());
    }

    @Test
    public void incrementTerm() {
        int term = state.incrementTerm();
        Assert.assertEquals(1, term);
        Assert.assertEquals(term, state.term());
    }

    @Test
    public void test_Leader() {
        state.leader(localMember);
        Assert.assertEquals(localMember, state.leader());
    }

    @Test
    public void test_commitIndex() {
        int ix = 123;
        state.commitIndex(ix);
        Assert.assertEquals(ix, state.commitIndex());
    }

    @Test
    public void test_lastApplied() {
        int last = 123;
        state.lastApplied(last);
        Assert.assertEquals(last, state.lastApplied());
    }

    @Test
    public void persistVote() {
        int term = 13;
        state.persistVote(term, localMember);
        Assert.assertEquals(term, state.lastVoteTerm());
        Assert.assertEquals(localMember, state.votedFor());
    }

    @Test
    public void toFollower_fromCandidate() {
        state.toCandidate();
        int term = 23;
        state.toFollower(term);
        Assert.assertEquals(term, state.term());
        Assert.assertEquals(FOLLOWER, state.role());
        Assert.assertNull(state.leader());
        Assert.assertNull(state.leaderState());
        Assert.assertNull(state.candidateState());
    }

    @Test
    public void toFollower_fromLeader() {
        state.toLeader();
        int term = 23;
        state.toFollower(term);
        Assert.assertEquals(term, state.term());
        Assert.assertEquals(FOLLOWER, state.role());
        Assert.assertNull(state.leader());
        Assert.assertNull(state.leaderState());
        Assert.assertNull(state.candidateState());
    }

    @Test
    public void toCandidate_fromFollower() {
        int term = 23;
        state.toFollower(term);
        state.toCandidate();
        Assert.assertEquals(CANDIDATE, state.role());
        Assert.assertNull(state.leaderState());
        Assert.assertEquals((term + 1), state.lastVoteTerm());
        Assert.assertEquals(localMember, state.votedFor());
        CandidateState candidateState = state.candidateState();
        Assert.assertNotNull(candidateState);
        Assert.assertEquals(state.majority(), candidateState.majority());
        Assert.assertFalse(candidateState.isMajorityGranted());
        Assert.assertEquals(1, candidateState.voteCount());
    }

    @Test
    public void toLeader_fromCandidate() {
        state.toCandidate();
        int term = state.term();
        RaftLog log = state.log();
        log.appendEntries(new LogEntry(term, 1, null), new LogEntry(term, 2, null), new LogEntry(term, 3, null));
        long lastLogIndex = log.lastLogOrSnapshotIndex();
        state.toLeader();
        Assert.assertEquals(LEADER, state.role());
        Assert.assertEquals(localMember, state.leader());
        Assert.assertNull(state.candidateState());
        LeaderState leaderState = state.leaderState();
        Assert.assertNotNull(leaderState);
        for (Endpoint endpoint : state.remoteMembers()) {
            FollowerState followerState = leaderState.getFollowerState(endpoint);
            Assert.assertEquals(0, followerState.matchIndex());
            Assert.assertEquals((lastLogIndex + 1), followerState.nextIndex());
        }
        long[] matchIndices = leaderState.matchIndices();
        Assert.assertEquals(((state.remoteMembers().size()) + 1), matchIndices.length);
        for (long index : matchIndices) {
            Assert.assertEquals(0, index);
        }
    }

    @Test
    public void isKnownEndpoint() {
        for (Endpoint endpoint : members) {
            Assert.assertTrue(state.isKnownMember(endpoint));
        }
        Assert.assertFalse(state.isKnownMember(RaftUtil.newRaftMember(1234)));
        Assert.assertFalse(state.isKnownMember(new TestRaftMember(HazelcastTestSupport.randomString(), localMember.getPort())));
        Assert.assertFalse(state.isKnownMember(new TestRaftMember(localMember.getUuid(), 1234)));
    }

    @Test
    public void test_majority_withOddMemberGroup() {
        test_majority(7);
    }

    @Test
    public void test_majority_withEvenMemberGroup() {
        test_majority(8);
    }
}

