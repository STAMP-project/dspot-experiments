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


import Cluster.Role;
import Cluster.Role.FOLLOWER;
import Cluster.Role.LEADER;
import io.aeron.Aeron;
import io.aeron.cluster.service.Cluster;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class ClusterTest {
    private static final String MSG = "Hello World!";

    @Test(timeout = 30000)
    public void shouldStopFollowerAndRestartFollower() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            cluster.awaitLeader();
            TestNode follower = cluster.followers().get(0);
            cluster.stopNode(follower);
            Thread.sleep(1000);
            follower = cluster.startStaticNode(follower.index(), false);
            Thread.sleep(1000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
        }
    }

    @Test(timeout = 30000)
    public void clientShouldBeSentNewLeaderEventOnLeaderChange() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.stopNode(leader);
            cluster.awaitLeadershipEvent(1);
        }
    }

    @Test(timeout = 30000)
    public void shouldStopLeaderAndFollowersAndRestartAllWithSnapshot() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.takeSnapshot(leader);
            cluster.awaitSnapshotCounter(cluster.node(0), 1);
            cluster.awaitSnapshotCounter(cluster.node(1), 1);
            cluster.awaitSnapshotCounter(cluster.node(2), 1);
            cluster.stopNode(cluster.node(0));
            cluster.stopNode(cluster.node(1));
            cluster.stopNode(cluster.node(2));
            Thread.sleep(1000);
            cluster.startStaticNode(0, false);
            cluster.startStaticNode(1, false);
            cluster.startStaticNode(2, false);
            cluster.awaitLeader();
            Assert.assertThat(cluster.followers().size(), Is.is(2));
            cluster.awaitSnapshotLoadedForService(cluster.node(0));
            cluster.awaitSnapshotLoadedForService(cluster.node(1));
            cluster.awaitSnapshotLoadedForService(cluster.node(2));
        }
    }

    @Test(timeout = 30000)
    public void shouldShutdownClusterAndRestartWithSnapshots() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.node(0).terminationExpected(true);
            cluster.node(1).terminationExpected(true);
            cluster.node(2).terminationExpected(true);
            cluster.shutdownCluster(leader);
            cluster.awaitNodeTermination(cluster.node(0));
            cluster.awaitNodeTermination(cluster.node(1));
            cluster.awaitNodeTermination(cluster.node(2));
            Assert.assertTrue(cluster.node(0).service().wasSnapshotTaken());
            Assert.assertTrue(cluster.node(1).service().wasSnapshotTaken());
            Assert.assertTrue(cluster.node(2).service().wasSnapshotTaken());
            cluster.stopNode(cluster.node(0));
            cluster.stopNode(cluster.node(1));
            cluster.stopNode(cluster.node(2));
            Thread.sleep(1000);
            cluster.startStaticNode(0, false);
            cluster.startStaticNode(1, false);
            cluster.startStaticNode(2, false);
            cluster.awaitLeader();
            Assert.assertThat(cluster.followers().size(), Is.is(2));
            cluster.awaitSnapshotLoadedForService(cluster.node(0));
            cluster.awaitSnapshotLoadedForService(cluster.node(1));
            cluster.awaitSnapshotLoadedForService(cluster.node(2));
        }
    }

    @Test(timeout = 30000)
    public void shouldAbortClusterAndRestart() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.node(0).terminationExpected(true);
            cluster.node(1).terminationExpected(true);
            cluster.node(2).terminationExpected(true);
            cluster.abortCluster(leader);
            cluster.awaitNodeTermination(cluster.node(0));
            cluster.awaitNodeTermination(cluster.node(1));
            cluster.awaitNodeTermination(cluster.node(2));
            Assert.assertFalse(cluster.node(0).service().wasSnapshotTaken());
            Assert.assertFalse(cluster.node(1).service().wasSnapshotTaken());
            Assert.assertFalse(cluster.node(2).service().wasSnapshotTaken());
            cluster.stopNode(cluster.node(0));
            cluster.stopNode(cluster.node(1));
            cluster.stopNode(cluster.node(2));
            Thread.sleep(1000);
            cluster.startStaticNode(0, false);
            cluster.startStaticNode(1, false);
            cluster.startStaticNode(2, false);
            cluster.awaitLeader();
            Assert.assertThat(cluster.followers().size(), Is.is(2));
            Assert.assertFalse(cluster.node(0).service().wasSnapshotLoaded());
            Assert.assertFalse(cluster.node(1).service().wasSnapshotLoaded());
            Assert.assertFalse(cluster.node(2).service().wasSnapshotLoaded());
        }
    }

    @Test(timeout = 30000)
    public void shouldAbortClusterOnTerminationTimeout() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            Assert.assertThat(followers.size(), Is.is(2));
            final TestNode followerA = followers.get(0);
            final TestNode followerB = followers.get(1);
            leader.terminationExpected(true);
            followerA.terminationExpected(true);
            cluster.stopNode(followerB);
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.abortCluster(leader);
            cluster.awaitNodeTermination(leader);
            cluster.awaitNodeTermination(followerA);
            cluster.stopNode(leader);
            cluster.stopNode(followerA);
        }
    }

    @Test(timeout = 30000)
    public void shouldEchoMessagesThenContinueOnNewLeader() throws Exception {
        final int preFailureMessageCount = 10;
        final int postFailureMessageCount = 7;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode originalLeader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(preFailureMessageCount);
            cluster.awaitResponses(preFailureMessageCount);
            cluster.awaitMessageCountForService(cluster.node(0), preFailureMessageCount);
            cluster.awaitMessageCountForService(cluster.node(1), preFailureMessageCount);
            cluster.awaitMessageCountForService(cluster.node(2), preFailureMessageCount);
            Assert.assertThat(cluster.client().leaderMemberId(), Is.is(originalLeader.index()));
            cluster.stopNode(originalLeader);
            final TestNode newLeader = cluster.awaitLeader(originalLeader.index());
            cluster.sendMessages(postFailureMessageCount);
            cluster.awaitResponses((preFailureMessageCount + postFailureMessageCount));
            Assert.assertThat(cluster.client().leaderMemberId(), Is.is(newLeader.index()));
            final TestNode follower = cluster.followers().get(0);
            cluster.awaitMessageCountForService(newLeader, (preFailureMessageCount + postFailureMessageCount));
            cluster.awaitMessageCountForService(follower, (preFailureMessageCount + postFailureMessageCount));
        }
    }

    @Test(timeout = 30000)
    public void shouldStopLeaderAndRestartAfterElectionAsFollower() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode originalLeader = cluster.awaitLeader();
            cluster.stopNode(originalLeader);
            cluster.awaitLeader(originalLeader.index());
            final TestNode follower = cluster.startStaticNode(originalLeader.index(), false);
            Thread.sleep(5000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
            Assert.assertThat(follower.electionState(), Is.is(((Election.State) (null))));
        }
    }

    @Test(timeout = 30000)
    public void shouldStopLeaderAndRestartAfterElectionAsFollowerWithSendingAfter() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode originalLeader = cluster.awaitLeader();
            cluster.stopNode(originalLeader);
            cluster.awaitLeader(originalLeader.index());
            final TestNode follower = cluster.startStaticNode(originalLeader.index(), false);
            Thread.sleep(5000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
            Assert.assertThat(follower.electionState(), Is.is(((Election.State) (null))));
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
        }
    }

    @Test(timeout = 60000)
    public void shouldStopLeaderAndRestartAfterElectionAsFollowerWithSendingAfterThenStopLeader() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode originalLeader = cluster.awaitLeader();
            cluster.stopNode(originalLeader);
            cluster.awaitLeader(originalLeader.index());
            final TestNode follower = cluster.startStaticNode(originalLeader.index(), false);
            Thread.sleep(5000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
            Assert.assertThat(follower.electionState(), Is.is(((Election.State) (null))));
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            final TestNode leader = cluster.awaitLeader();
            cluster.stopNode(leader);
            cluster.awaitLeader(leader.index());
        }
    }

    @Test(timeout = 30000)
    public void shouldAcceptMessagesAfterSingleNodeCleanRestart() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            cluster.awaitLeader();
            TestNode follower = cluster.followers().get(0);
            cluster.stopNode(follower);
            Thread.sleep(10000);
            follower = cluster.startStaticNode(follower.index(), true);
            Thread.sleep(1000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.awaitMessageCountForService(follower, messageCount);
        }
    }

    @Test(timeout = 30000)
    public void followerShouldRecoverWhenSnapshotTakenWhileDown() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            TestNode follower = cluster.followers().get(0);
            cluster.stopNode(follower);
            Thread.sleep(10000);
            cluster.takeSnapshot(leader);
            cluster.awaitSnapshotCounter(leader, 1);
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            follower = cluster.startStaticNode(follower.index(), false);
            Thread.sleep(1000);
            Assert.assertThat(follower.role(), Is.is(FOLLOWER));
            cluster.awaitMessageCountForService(follower, messageCount);
            Assert.assertThat(follower.errors(), Is.is(0L));
        }
    }

    @Test(timeout = 45000)
    public void shouldTolerateMultipleLeaderFailures() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode firstLeader = cluster.awaitLeader();
            cluster.stopNode(firstLeader);
            final TestNode secondLeader = cluster.awaitLeader();
            cluster.startStaticNode(firstLeader.index(), false);
            cluster.stopNode(secondLeader);
            cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
        }
    }

    @Test(timeout = 30000)
    public void shouldAcceptMessagesAfterTwoNodeCleanRestart() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            TestNode followerA = followers.get(0);
            TestNode followerB = followers.get(1);
            cluster.stopNode(followerA);
            cluster.stopNode(followerB);
            Thread.sleep(5000);
            followerA = cluster.startStaticNode(followerA.index(), true);
            followerB = cluster.startStaticNode(followerB.index(), true);
            Thread.sleep(1000);
            Assert.assertThat(followerA.role(), Is.is(FOLLOWER));
            Assert.assertThat(followerB.role(), Is.is(FOLLOWER));
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.awaitMessageCountForService(followerA, messageCount);
            cluster.awaitMessageCountForService(followerB, messageCount);
        }
    }

    @Test(timeout = 30000)
    public void membersShouldHaveOneCommitPositionCounter() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            final TestNode followerA = followers.get(0);
            final TestNode followerB = followers.get(1);
            cluster.stopNode(leader);
            cluster.awaitLeader(leader.index());
            Assert.assertThat(countersOfType(followerA.countersReader(), COMMIT_POSITION_TYPE_ID), Is.is(1));
            Assert.assertThat(countersOfType(followerB.countersReader(), COMMIT_POSITION_TYPE_ID), Is.is(1));
        }
    }

    @Test(timeout = 30000)
    public void shouldCallOnRoleChangeOnBecomingLeader() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            TestNode leader = cluster.awaitLeader();
            List<TestNode> followers = cluster.followers();
            final TestNode followerA = followers.get(0);
            final TestNode followerB = followers.get(1);
            Assert.assertThat(leader.service().roleChangedTo(), Is.is(LEADER));
            Assert.assertThat(followerA.service().roleChangedTo(), Is.is(((Cluster.Role) (null))));
            Assert.assertThat(followerB.service().roleChangedTo(), Is.is(((Cluster.Role) (null))));
            cluster.stopNode(leader);
            leader = cluster.awaitLeader(leader.index());
            followers = cluster.followers();
            final TestNode follower = followers.get(0);
            Assert.assertThat(leader.service().roleChangedTo(), Is.is(LEADER));
            Assert.assertThat(follower.service().roleChangedTo(), Is.is(((Cluster.Role) (null))));
        }
    }

    @Test(timeout = 30000)
    public void shouldLooseLeadershipWhenNoActiveQuorumOfFollowers() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            final TestNode followerA = followers.get(0);
            final TestNode followerB = followers.get(1);
            Assert.assertThat(leader.service().roleChangedTo(), Is.is(LEADER));
            cluster.stopNode(followerA);
            cluster.stopNode(followerB);
            while ((leader.service().roleChangedTo()) == (Role.LEADER)) {
                TestUtil.checkInterruptedStatus();
                Thread.yield();
            } 
            Assert.assertThat(leader.service().roleChangedTo(), Is.is(FOLLOWER));
        }
    }

    @Test(timeout = 60000)
    public void followerShouldRecoverWhileMessagesContinue() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            final TestNode followerA = followers.get(0);
            TestNode followerB = followers.get(1);
            cluster.connectClient();
            final Thread messageThread = startMessageThread(cluster, TimeUnit.MICROSECONDS.toNanos(500));
            try {
                cluster.stopNode(followerB);
                Thread.sleep(10000);
                followerB = cluster.startStaticNode(followerB.index(), false);
                Thread.sleep(30000);
            } finally {
                messageThread.interrupt();
                messageThread.join();
            }
            Assert.assertThat(leader.errors(), Is.is(0L));
            Assert.assertThat(followerA.errors(), Is.is(0L));
            Assert.assertThat(followerB.errors(), Is.is(0L));
            Assert.assertThat(followerB.electionState(), Is.is(((Election.State) (null))));
        }
    }

    @Test(timeout = 30000)
    public void shouldCatchupFromEmptyLog() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            TestNode followerB = followers.get(1);
            cluster.stopNode(followerB);
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            followerB = cluster.startStaticNode(followerB.index(), true);
            cluster.awaitMessageCountForService(followerB, messageCount);
        }
    }

    @Test(timeout = 30000)
    public void shouldCatchupFromEmptyLogThenSnapshotAfterShutdownAndFollowerCleanStart() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final List<TestNode> followers = cluster.followers();
            final TestNode followerA = followers.get(0);
            final TestNode followerB = followers.get(1);
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            leader.terminationExpected(true);
            followerA.terminationExpected(true);
            followerB.terminationExpected(true);
            cluster.shutdownCluster(leader);
            cluster.awaitNodeTermination(cluster.node(0));
            cluster.awaitNodeTermination(cluster.node(1));
            cluster.awaitNodeTermination(cluster.node(2));
            Assert.assertTrue(cluster.node(0).service().wasSnapshotTaken());
            Assert.assertTrue(cluster.node(1).service().wasSnapshotTaken());
            Assert.assertTrue(cluster.node(2).service().wasSnapshotTaken());
            cluster.stopNode(cluster.node(0));
            cluster.stopNode(cluster.node(1));
            cluster.stopNode(cluster.node(2));
            Thread.sleep(1000);
            cluster.startStaticNode(0, false);
            cluster.startStaticNode(1, false);
            cluster.startStaticNode(2, true);
            final TestNode newLeader = cluster.awaitLeader();
            Assert.assertNotEquals(newLeader.index(), Is.is(2));
            Assert.assertTrue(cluster.node(0).service().wasSnapshotLoaded());
            Assert.assertTrue(cluster.node(1).service().wasSnapshotLoaded());
            Assert.assertFalse(cluster.node(2).service().wasSnapshotLoaded());
            cluster.awaitMessageCountForService(cluster.node(2), messageCount);
            cluster.awaitSnapshotCounter(cluster.node(2), 1);
            Assert.assertTrue(cluster.node(2).service().wasSnapshotTaken());
        }
    }

    @Test(timeout = 30000)
    public void shouldCatchUpAfterFollowerMissesOneMessage() throws Exception {
        shouldCatchUpAfterFollowerMissesMessage(TestMessages.NO_OP);
    }

    @Test(timeout = 30000)
    public void shouldCatchUpAfterFollowerMissesTimerRegistration() throws Exception {
        shouldCatchUpAfterFollowerMissesMessage(TestMessages.REGISTER_TIMER);
    }
}

