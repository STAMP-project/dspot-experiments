/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron.cluster;


import Cluster.Role.CANDIDATE;
import Cluster.Role.FOLLOWER;
import Cluster.Role.LEADER;
import ConsensusModule.Context;
import Election.State.CANDIDATE_BALLOT;
import Election.State.CANVASS;
import Election.State.FOLLOWER_BALLOT;
import Election.State.FOLLOWER_READY;
import Election.State.FOLLOWER_REPLAY;
import Election.State.INIT;
import Election.State.LEADER_READY;
import Election.State.LEADER_REPLAY;
import Election.State.NOMINATE;
import io.aeron.Aeron;
import io.aeron.Counter;
import io.aeron.Subscription;
import io.aeron.cluster.service.ClusterMarkFile;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.CachedEpochClock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


@SuppressWarnings("MethodLength")
public class ElectionTest {
    public static final long RECORDING_ID = 1L;

    private final Aeron aeron = Mockito.mock(Aeron.class);

    private final Counter electionStateCounter = Mockito.mock(Counter.class);

    private final RecordingLog recordingLog = Mockito.mock(RecordingLog.class);

    private final ClusterMarkFile clusterMarkFile = Mockito.mock(ClusterMarkFile.class);

    private final MemberStatusAdapter memberStatusAdapter = Mockito.mock(MemberStatusAdapter.class);

    private final MemberStatusPublisher memberStatusPublisher = Mockito.mock(MemberStatusPublisher.class);

    private final ConsensusModuleAgent consensusModuleAgent = Mockito.mock(ConsensusModuleAgent.class);

    private final Context ctx = new ConsensusModule.Context().epochClock(new CachedEpochClock()).aeron(aeron).recordingLog(recordingLog).random(new java.util.Random()).clusterMarkFile(clusterMarkFile);

    private final long electionStatusIntervalMs = TimeUnit.NANOSECONDS.toMillis(ctx.electionStatusIntervalNs());

    private final long electionTimeoutMs = TimeUnit.NANOSECONDS.toMillis(ctx.electionTimeoutNs());

    private final long startupCanvassTimeoutMs = TimeUnit.NANOSECONDS.toMillis(ctx.startupCanvassTimeoutNs());

    @Test
    public void shouldElectSingleNodeClusterLeader() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ClusterMember.parse("0,clientEndpoint,memberEndpoint,logEndpoint,transferEndpoint,archiveEndpoint");
        final ClusterMember thisMember = clusterMembers[0];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, thisMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long newLeadershipTermId = leadershipTermId + 1;
        final long t1 = 0;
        election.doWork(t1);
        election.doWork(t1);
        Mockito.verify(consensusModuleAgent).becomeLeader(ArgumentMatchers.eq(newLeadershipTermId), ArgumentMatchers.eq(logPosition), ArgumentMatchers.anyInt());
        Mockito.verify(recordingLog).appendTerm(ElectionTest.RECORDING_ID, newLeadershipTermId, logPosition, t1);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_READY));
    }

    @Test
    public void shouldElectAppointedLeader() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember candidateMember = clusterMembers[0];
        final CachedEpochClock clock = ((CachedEpochClock) (ctx.epochClock()));
        ctx.appointedLeaderId(candidateMember.id());
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, candidateMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long candidateTermId = leadershipTermId + 1;
        final long t1 = 1;
        clock.update(t1);
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 1);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t2 = 2;
        clock.update(t2);
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t3 = t2 + ((electionStatusIntervalMs) * (Election.NOMINATION_TIMEOUT_MULTIPLIER));
        clock.update(t3);
        election.doWork(t3);
        election.doWork(t3);
        Mockito.verify(memberStatusPublisher).requestVote(clusterMembers[1].publication(), leadershipTermId, logPosition, candidateTermId, candidateMember.id());
        Mockito.verify(memberStatusPublisher).requestVote(clusterMembers[2].publication(), leadershipTermId, logPosition, candidateTermId, candidateMember.id());
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        Mockito.verify(consensusModuleAgent).role(CANDIDATE);
        Mockito.when(consensusModuleAgent.role()).thenReturn(CANDIDATE);
        election.onVote(candidateTermId, leadershipTermId, logPosition, candidateMember.id(), clusterMembers[1].id(), true);
        election.onVote(candidateTermId, leadershipTermId, logPosition, candidateMember.id(), clusterMembers[2].id(), true);
        final long t4 = t3 + 1;
        clock.update(t3);
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_REPLAY));
        final long t5 = t4 + 1;
        clock.update(t5);
        election.doWork(t5);
        election.doWork(t5);
        Mockito.verify(consensusModuleAgent).becomeLeader(ArgumentMatchers.eq(candidateTermId), ArgumentMatchers.eq(logPosition), ArgumentMatchers.anyInt());
        Mockito.verify(recordingLog).appendTerm(ElectionTest.RECORDING_ID, candidateTermId, logPosition, t5);
        Assert.assertThat(clusterMembers[1].logPosition(), CoreMatchers.is(NULL_POSITION));
        Assert.assertThat(clusterMembers[2].logPosition(), CoreMatchers.is(NULL_POSITION));
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_READY));
        Assert.assertThat(election.leadershipTermId(), CoreMatchers.is(candidateTermId));
        final long t6 = t5 + (TimeUnit.NANOSECONDS.toMillis(ctx.leaderHeartbeatIntervalNs()));
        clock.update(t6);
        final int logSessionId = -7;
        election.logSessionId(logSessionId);
        election.doWork(t6);
        Mockito.verify(memberStatusPublisher).newLeadershipTerm(clusterMembers[1].publication(), leadershipTermId, logPosition, candidateTermId, logPosition, candidateMember.id(), logSessionId);
        Mockito.verify(memberStatusPublisher).newLeadershipTerm(clusterMembers[2].publication(), leadershipTermId, logPosition, candidateTermId, logPosition, candidateMember.id(), logSessionId);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_READY));
        Mockito.when(consensusModuleAgent.electionComplete(ArgumentMatchers.anyLong())).thenReturn(true);
        final long t7 = t6 + 1;
        clock.update(t7);
        election.onAppendedPosition(candidateTermId, logPosition, clusterMembers[1].id());
        election.onAppendedPosition(candidateTermId, logPosition, clusterMembers[2].id());
        election.doWork(t7);
        final InOrder inOrder = Mockito.inOrder(consensusModuleAgent, electionStateCounter);
        inOrder.verify(consensusModuleAgent).electionComplete(t7);
        inOrder.verify(electionStateCounter).close();
    }

    @Test
    public void shouldVoteForAppointedLeader() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final int candidateId = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[1];
        final CachedEpochClock clock = ((CachedEpochClock) (ctx.epochClock()));
        ctx.appointedLeaderId(candidateId).epochClock(clock);
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long candidateTermId = leadershipTermId + 1;
        final long t2 = 2;
        clock.update(t2);
        election.onRequestVote(leadershipTermId, logPosition, candidateTermId, candidateId);
        Mockito.verify(memberStatusPublisher).placeVote(clusterMembers[candidateId].publication(), candidateTermId, leadershipTermId, logPosition, candidateId, followerMember.id(), true);
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(FOLLOWER_BALLOT));
        final int logSessionId = -7;
        election.onNewLeadershipTerm(leadershipTermId, logPosition, candidateTermId, logPosition, candidateId, logSessionId);
        Assert.assertThat(election.state(), CoreMatchers.is(FOLLOWER_REPLAY));
        Mockito.when(consensusModuleAgent.createAndRecordLogSubscriptionAsFollower(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(Subscription.class));
        Mockito.when(memberStatusPublisher.catchupPosition(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(Boolean.TRUE);
        Mockito.when(consensusModuleAgent.hasAppendReachedLivePosition(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(consensusModuleAgent.hasAppendReachedPosition(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong())).thenReturn(Boolean.TRUE);
        final long t3 = 3;
        election.doWork(t3);
        election.doWork(t3);
        election.doWork(t3);
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(FOLLOWER_READY));
        Mockito.when(memberStatusPublisher.appendedPosition(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(Boolean.TRUE);
        Mockito.when(consensusModuleAgent.electionComplete(ArgumentMatchers.anyLong())).thenReturn(true);
        final long t4 = 4;
        election.doWork(t4);
        final InOrder inOrder = Mockito.inOrder(memberStatusPublisher, consensusModuleAgent, electionStateCounter);
        inOrder.verify(memberStatusPublisher).appendedPosition(clusterMembers[candidateId].publication(), candidateTermId, logPosition, followerMember.id());
        inOrder.verify(consensusModuleAgent).electionComplete(t4);
        inOrder.verify(electionStateCounter).close();
    }

    @Test
    public void shouldCanvassMembersForSuccessfulLeadershipBid() {
        final long logPosition = 0;
        final long leadershipTermId = Aeron.NULL_VALUE;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long t2 = t1 + (electionStatusIntervalMs);
        election.doWork(t2);
        Mockito.verify(memberStatusPublisher).canvassPosition(clusterMembers[0].publication(), leadershipTermId, logPosition, followerMember.id());
        Mockito.verify(memberStatusPublisher).canvassPosition(clusterMembers[2].publication(), leadershipTermId, logPosition, followerMember.id());
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t3 = t2 + 1;
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
    }

    @Test
    public void shouldCanvassMembersForUnSuccessfulLeadershipBid() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[0];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long t2 = t1 + (electionStatusIntervalMs);
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition((leadershipTermId + 1), logPosition, 1);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t3 = t2 + 1;
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
    }

    @Test
    public void shouldVoteForCandidateDuringNomination() {
        final long logPosition = 0;
        final long leadershipTermId = Aeron.NULL_VALUE;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long t2 = t1 + (electionStatusIntervalMs);
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t3 = t2 + 1;
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t4 = t3 + 1;
        final long candidateTermId = leadershipTermId + 1;
        election.onRequestVote(leadershipTermId, logPosition, candidateTermId, 0);
        election.doWork(t4);
        Assert.assertThat(election.state(), CoreMatchers.is(FOLLOWER_BALLOT));
    }

    @Test
    public void shouldTimeoutCanvassWithMajority() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onAppendedPosition(leadershipTermId, logPosition, 0);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long t2 = t1 + 1;
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long t3 = t2 + (startupCanvassTimeoutMs);
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
    }

    @Test
    public void shouldWinCandidateBallotWithMajority() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember candidateMember = clusterMembers[1];
        final Election election = newElection(false, leadershipTermId, logPosition, clusterMembers, candidateMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t2 = t1 + 1;
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t3 = t2 + ((electionStatusIntervalMs) * (Election.NOMINATION_TIMEOUT_MULTIPLIER));
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t4 = t3 + (electionTimeoutMs);
        final long candidateTermId = leadershipTermId + 1;
        Mockito.when(consensusModuleAgent.role()).thenReturn(CANDIDATE);
        election.onVote(candidateTermId, leadershipTermId, logPosition, candidateMember.id(), clusterMembers[2].id(), true);
        election.doWork(t4);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_REPLAY));
    }

    @Test
    public void shouldElectCandidateWithFullVote() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember candidateMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, candidateMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t2 = t1 + 1;
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t3 = t2 + ((electionStatusIntervalMs) * (Election.NOMINATION_TIMEOUT_MULTIPLIER));
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t4 = t3 + 1;
        final long candidateTermId = leadershipTermId + 1;
        Mockito.when(consensusModuleAgent.role()).thenReturn(CANDIDATE);
        election.onVote(candidateTermId, leadershipTermId, logPosition, candidateMember.id(), clusterMembers[0].id(), false);
        election.onVote(candidateTermId, leadershipTermId, logPosition, candidateMember.id(), clusterMembers[2].id(), true);
        election.doWork(t4);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_REPLAY));
    }

    @Test
    public void shouldTimeoutCandidateBallotWithoutMajority() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember candidateMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, candidateMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        election.onCanvassPosition(leadershipTermId, logPosition, 2);
        final long t2 = t1 + 1;
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t3 = t2 + ((electionStatusIntervalMs) * (Election.NOMINATION_TIMEOUT_MULTIPLIER));
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t4 = t3 + (electionTimeoutMs);
        election.doWork(t4);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        Assert.assertThat(election.leadershipTermId(), CoreMatchers.is(leadershipTermId));
        Assert.assertThat(election.candidateTermId(), CoreMatchers.is((leadershipTermId + 1)));
    }

    @Test
    public void shouldTimeoutFailedCandidateBallotOnSplitVoteThenSucceedOnRetry() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember candidateMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, candidateMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        final long t2 = t1 + (startupCanvassTimeoutMs);
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t3 = t2 + ((electionStatusIntervalMs) * (Election.NOMINATION_TIMEOUT_MULTIPLIER));
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t4 = t3 + 1;
        Mockito.when(consensusModuleAgent.role()).thenReturn(CANDIDATE);
        election.onVote((leadershipTermId + 1), leadershipTermId, logPosition, candidateMember.id(), clusterMembers[2].id(), false);
        election.doWork(t4);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t5 = t4 + (electionTimeoutMs);
        election.doWork(t5);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        election.onCanvassPosition(leadershipTermId, logPosition, 0);
        final long t6 = t5 + 1;
        election.doWork(t6);
        final long t7 = t6 + (electionTimeoutMs);
        election.doWork(t7);
        Assert.assertThat(election.state(), CoreMatchers.is(NOMINATE));
        final long t8 = t7 + (electionTimeoutMs);
        election.doWork(t8);
        Assert.assertThat(election.state(), CoreMatchers.is(CANDIDATE_BALLOT));
        final long t9 = t8 + 1;
        election.doWork(t9);
        final long candidateTermId = leadershipTermId + 2;
        election.onVote(candidateTermId, (leadershipTermId + 1), logPosition, candidateMember.id(), clusterMembers[2].id(), true);
        final long t10 = t9 + (electionTimeoutMs);
        election.doWork(t10);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_REPLAY));
        final long t11 = t10 + 1;
        election.doWork(t11);
        election.doWork(t11);
        Assert.assertThat(election.state(), CoreMatchers.is(LEADER_READY));
        Assert.assertThat(election.leadershipTermId(), CoreMatchers.is(candidateTermId));
    }

    @Test
    public void shouldTimeoutFollowerBallotWithoutLeaderEmerging() {
        final long leadershipTermId = Aeron.NULL_VALUE;
        final long logPosition = 0L;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember followerMember = clusterMembers[1];
        final Election election = newElection(leadershipTermId, logPosition, clusterMembers, followerMember);
        Assert.assertThat(election.state(), CoreMatchers.is(INIT));
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        final long candidateTermId = leadershipTermId + 1;
        election.onRequestVote(leadershipTermId, logPosition, candidateTermId, 0);
        final long t2 = t1 + 1;
        election.doWork(t2);
        Assert.assertThat(election.state(), CoreMatchers.is(FOLLOWER_BALLOT));
        final long t3 = t2 + (electionTimeoutMs);
        election.doWork(t3);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        Assert.assertThat(election.leadershipTermId(), CoreMatchers.is(leadershipTermId));
    }

    @Test
    public void shouldBecomeFollowerIfEnteringNewElection() {
        final long leadershipTermId = 1;
        final long logPosition = 120;
        final ClusterMember[] clusterMembers = ElectionTest.prepareClusterMembers();
        final ClusterMember thisMember = clusterMembers[0];
        Mockito.when(consensusModuleAgent.role()).thenReturn(LEADER);
        final Election election = newElection(false, leadershipTermId, logPosition, clusterMembers, thisMember);
        final long t1 = 1;
        election.doWork(t1);
        Assert.assertThat(election.state(), CoreMatchers.is(CANVASS));
        Mockito.verify(consensusModuleAgent).prepareForNewLeadership(logPosition);
        Mockito.verify(consensusModuleAgent).role(FOLLOWER);
    }
}

