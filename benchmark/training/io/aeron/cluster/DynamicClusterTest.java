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


import Cluster.Role.FOLLOWER;
import ClusterTool.ClusterMembersInfo;
import io.aeron.Aeron;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class DynamicClusterTest {
    @Test(timeout = 10000)
    public void shouldQueryClusterMembers() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final ClusterTool.ClusterMembersInfo clusterMembersInfo = leader.clusterMembersInfo();
            Assert.assertThat(clusterMembersInfo.leaderMemberId, Is.is(leader.index()));
            Assert.assertThat(clusterMembersInfo.passiveMembers, Is.is(""));
            Assert.assertThat(clusterMembersInfo.activeMembers, Is.is(cluster.staticClusterMembers()));
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeNoSnapshots() throws Exception {
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            Thread.sleep(1000);
            Assert.assertThat(dynamicMember.role(), Is.is(FOLLOWER));
            final ClusterTool.ClusterMembersInfo clusterMembersInfo = leader.clusterMembersInfo();
            Assert.assertThat(clusterMembersInfo.leaderMemberId, Is.is(leader.index()));
            Assert.assertThat(clusterMembersInfo.passiveMembers, Is.is(""));
            Assert.assertThat(numberOfMembers(clusterMembersInfo), Is.is(4));
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeNoSnapshotsThenSend() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            Thread.sleep(1000);
            Assert.assertThat(dynamicMember.role(), Is.is(FOLLOWER));
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.awaitMessageCountForService(leader, messageCount);
            cluster.awaitMessageCountForService(dynamicMember, messageCount);
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeNoSnapshotsWithCatchup() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.awaitMessageCountForService(leader, messageCount);
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            cluster.awaitMessageCountForService(dynamicMember, messageCount);
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeWithEmptySnapshot() throws Exception {
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.takeSnapshot(leader);
            cluster.awaitSnapshotCounter(cluster.node(0), 1);
            cluster.awaitSnapshotCounter(cluster.node(1), 1);
            cluster.awaitSnapshotCounter(cluster.node(2), 1);
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            Thread.sleep(1000);
            Assert.assertThat(dynamicMember.role(), Is.is(FOLLOWER));
            cluster.awaitSnapshotLoadedForService(dynamicMember);
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeWithSnapshot() throws Exception {
        final int messageCount = 10;
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(messageCount);
            cluster.awaitResponses(messageCount);
            cluster.takeSnapshot(leader);
            cluster.awaitSnapshotCounter(cluster.node(0), 1);
            cluster.awaitSnapshotCounter(cluster.node(1), 1);
            cluster.awaitSnapshotCounter(cluster.node(2), 1);
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            Thread.sleep(1000);
            Assert.assertThat(dynamicMember.role(), Is.is(FOLLOWER));
            cluster.awaitSnapshotLoadedForService(dynamicMember);
            Assert.assertThat(dynamicMember.service().messageCount(), Is.is(messageCount));
        }
    }

    @Test(timeout = 10000)
    public void shouldDynamicallyJoinClusterOfThreeWithSnapshotThenSend() throws Exception {
        final int preSnapshotMessageCount = 10;
        final int postSnapshotMessageCount = 7;
        final int totalMessageCount = preSnapshotMessageCount + postSnapshotMessageCount;
        try (TestCluster cluster = TestCluster.startCluster(3, 1)) {
            final TestNode leader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(preSnapshotMessageCount);
            cluster.awaitResponses(preSnapshotMessageCount);
            cluster.takeSnapshot(leader);
            cluster.awaitSnapshotCounter(cluster.node(0), 1);
            cluster.awaitSnapshotCounter(cluster.node(1), 1);
            cluster.awaitSnapshotCounter(cluster.node(2), 1);
            final TestNode dynamicMember = cluster.startDynamicNode(3, true);
            Thread.sleep(1000);
            Assert.assertThat(dynamicMember.role(), Is.is(FOLLOWER));
            cluster.awaitSnapshotLoadedForService(dynamicMember);
            Assert.assertThat(dynamicMember.service().messageCount(), Is.is(preSnapshotMessageCount));
            cluster.sendMessages(postSnapshotMessageCount);
            cluster.awaitResponses(totalMessageCount);
            cluster.awaitMessageCountForService(dynamicMember, totalMessageCount);
        }
    }

    @Test(timeout = 10000)
    public void shouldRemoveFollower() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            final TestNode leader = cluster.awaitLeader();
            final TestNode follower = cluster.followers().get(0);
            follower.terminationExpected(true);
            leader.removeMember(follower.index(), false);
            cluster.awaitNodeTermination(follower);
            cluster.stopNode(follower);
            final ClusterTool.ClusterMembersInfo clusterMembersInfo = leader.clusterMembersInfo();
            Assert.assertThat(clusterMembersInfo.leaderMemberId, Is.is(leader.index()));
            Assert.assertThat(numberOfMembers(clusterMembersInfo), Is.is(2));
        }
    }

    @Test(timeout = 10000)
    public void shouldRemoveLeader() throws Exception {
        try (TestCluster cluster = TestCluster.startThreeNodeStaticCluster(Aeron.NULL_VALUE)) {
            TestNode leader = cluster.awaitLeader();
            leader.terminationExpected(true);
            leader.removeMember(leader.index(), false);
            cluster.awaitNodeTermination(leader);
            cluster.stopNode(leader);
            leader = cluster.awaitLeader(leader.index());
            final ClusterTool.ClusterMembersInfo clusterMembersInfo = leader.clusterMembersInfo();
            Assert.assertThat(clusterMembersInfo.leaderMemberId, Is.is(leader.index()));
            Assert.assertThat(numberOfMembers(clusterMembersInfo), Is.is(2));
        }
    }
}

