/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;


import java.io.StringWriter;
import org.apache.zookeeper.server.ServerStats;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.command.StatResetCommand;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StatResetCommandTest {
    private StatResetCommand statResetCommand;

    private StringWriter outputWriter;

    private ZooKeeperServer zks;

    private ServerStats serverStats;

    @Test
    public void testStatResetWithZKNotRunning() {
        // Arrange
        Mockito.when(zks.isRunning()).thenReturn(false);
        // Act
        statResetCommand.commandRun();
        // Assert
        String output = outputWriter.toString();
        Assert.assertEquals(((ZK_NOT_SERVING) + "\n"), output);
    }

    @Test
    public void testStatResetWithFollower() {
        // Arrange
        Mockito.when(zks.isRunning()).thenReturn(true);
        Mockito.when(serverStats.getServerState()).thenReturn("follower");
        // Act
        statResetCommand.commandRun();
        // Assert
        String output = outputWriter.toString();
        Assert.assertEquals("Server stats reset.\n", output);
        Mockito.verify(serverStats, Mockito.times(1)).reset();
    }

    @Test
    public void testStatResetWithLeader() {
        // Arrange
        LeaderZooKeeperServer leaderZks = Mockito.mock(LeaderZooKeeperServer.class);
        Mockito.when(leaderZks.isRunning()).thenReturn(true);
        Mockito.when(leaderZks.serverStats()).thenReturn(serverStats);
        Leader leader = Mockito.mock(Leader.class);
        Mockito.when(leaderZks.getLeader()).thenReturn(leader);
        statResetCommand.setZkServer(leaderZks);
        Mockito.when(serverStats.getServerState()).thenReturn("leader");
        BufferStats bufferStats = Mockito.mock(BufferStats.class);
        Mockito.when(leader.getProposalStats()).thenReturn(bufferStats);
        // Act
        statResetCommand.commandRun();
        // Assert
        String output = outputWriter.toString();
        Assert.assertEquals("Server stats reset.\n", output);
        Mockito.verify(serverStats, Mockito.times(1)).reset();
        Mockito.verify(bufferStats, Mockito.times(1)).reset();
    }
}

