/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;


import Leader.XidRolloverException;
import LearnerType.OBSERVER;
import LearnerType.PARTICIPANT;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LeaderBeanTest {
    private Leader leader;

    private LeaderBean leaderBean;

    private FileTxnSnapLog fileTxnSnapLog;

    private LeaderZooKeeperServer zks;

    private QuorumPeer qp;

    private QuorumVerifier quorumVerifierMock;

    @Test
    public void testGetName() {
        Assert.assertEquals("Leader", leaderBean.getName());
    }

    @Test
    public void testGetCurrentZxid() {
        // Arrange
        zks.setZxid(1);
        // Assert
        Assert.assertEquals("0x1", leaderBean.getCurrentZxid());
    }

    @Test
    public void testGetElectionTimeTaken() {
        // Arrange
        qp.setElectionTimeTaken(1);
        // Assert
        Assert.assertEquals(1, leaderBean.getElectionTimeTaken());
    }

    @Test
    public void testGetProposalSize() throws XidRolloverException, IOException {
        // Arrange
        Request req = createMockRequest();
        // Act
        leader.propose(req);
        // Assert
        byte[] data = SerializeUtils.serializeRequest(req);
        Assert.assertEquals(data.length, leaderBean.getLastProposalSize());
        Assert.assertEquals(data.length, leaderBean.getMinProposalSize());
        Assert.assertEquals(data.length, leaderBean.getMaxProposalSize());
    }

    @Test
    public void testResetProposalStats() throws XidRolloverException, IOException {
        // Arrange
        int initialProposalSize = leaderBean.getLastProposalSize();
        Request req = createMockRequest();
        // Act
        leader.propose(req);
        // Assert
        Assert.assertNotEquals(initialProposalSize, leaderBean.getLastProposalSize());
        leaderBean.resetProposalStatistics();
        Assert.assertEquals(initialProposalSize, leaderBean.getLastProposalSize());
        Assert.assertEquals(initialProposalSize, leaderBean.getMinProposalSize());
        Assert.assertEquals(initialProposalSize, leaderBean.getMaxProposalSize());
    }

    @Test
    public void testFollowerInfo() throws IOException {
        Map<Long, QuorumServer> votingMembers = new HashMap<Long, QuorumServer>();
        votingMembers.put(1L, null);
        votingMembers.put(2L, null);
        votingMembers.put(3L, null);
        Mockito.when(quorumVerifierMock.getVotingMembers()).thenReturn(votingMembers);
        LearnerHandler follower = Mockito.mock(LearnerHandler.class);
        Mockito.when(follower.getLearnerType()).thenReturn(PARTICIPANT);
        Mockito.when(follower.toString()).thenReturn("1");
        Mockito.when(follower.getSid()).thenReturn(1L);
        leader.addLearnerHandler(follower);
        leader.addForwardingFollower(follower);
        Assert.assertEquals("1\n", leaderBean.followerInfo());
        Assert.assertEquals("", leaderBean.nonVotingFollowerInfo());
        LearnerHandler observer = Mockito.mock(LearnerHandler.class);
        Mockito.when(observer.getLearnerType()).thenReturn(OBSERVER);
        Mockito.when(observer.toString()).thenReturn("2");
        leader.addLearnerHandler(observer);
        Assert.assertEquals("1\n", leaderBean.followerInfo());
        Assert.assertEquals("", leaderBean.nonVotingFollowerInfo());
        LearnerHandler nonVotingFollower = Mockito.mock(LearnerHandler.class);
        Mockito.when(nonVotingFollower.getLearnerType()).thenReturn(PARTICIPANT);
        Mockito.when(nonVotingFollower.toString()).thenReturn("5");
        Mockito.when(nonVotingFollower.getSid()).thenReturn(5L);
        leader.addLearnerHandler(nonVotingFollower);
        leader.addForwardingFollower(nonVotingFollower);
        String followerInfo = leaderBean.followerInfo();
        Assert.assertTrue(followerInfo.contains("1"));
        Assert.assertTrue(followerInfo.contains("5"));
        Assert.assertEquals("5\n", leaderBean.nonVotingFollowerInfo());
    }
}

