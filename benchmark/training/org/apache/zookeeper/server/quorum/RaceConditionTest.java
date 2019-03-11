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


import QuorumStats.Provider.FOLLOWING_STATE;
import QuorumStats.Provider.LOOKING_STATE;
import ZooDefs.OpCode;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.server.FinalRequestProcessor;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.SyncRequestProcessor;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Leader.PING;


/**
 * This test class contains test cases related to race condition in complete
 * ZooKeeper
 */
public class RaceConditionTest extends QuorumPeerTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(RaceConditionTest.class);

    private static int SERVER_COUNT = 3;

    private QuorumPeerTestBase.MainThread[] mt;

    /**
     * Test case for https://issues.apache.org/jira/browse/ZOOKEEPER-2380.
     * Deadlock while shutting down the ZooKeeper
     */
    @Test(timeout = 30000)
    public void testRaceConditionBetweenLeaderAndAckRequestProcessor() throws Exception {
        mt = startQuorum();
        // get leader
        QuorumPeer leader = getLeader(mt);
        long oldLeaderCurrentEpoch = leader.getCurrentEpoch();
        Assert.assertNotNull("Leader should not be null", leader);
        // shutdown 2 followers so that leader does not have majority and goes
        // into looking state or following/leading state.
        shutdownFollowers(mt);
        /**
         * <pre>
         * Verify that there is no deadlock in following ways:
         * 1) If leader is in LOOKING or FOLLOWING, we are sure there is no deadlock.
         * 2) If leader in in LEADING state then we have to check that this LEADING state is
         * after the leader election, not the old LEADING state.
         * </pre>
         */
        boolean leaderStateChanged = ClientBase.waitForServerState(leader, 15000, LOOKING_STATE, FOLLOWING_STATE);
        // Wait for the old leader to start completely
        Assert.assertTrue("Failed to bring up the old leader server", ClientBase.waitForServerUp(("127.0.0.1:" + (leader.getClientPort())), ClientBase.CONNECTION_TIMEOUT));
        Assert.assertTrue(("Leader failed to transition to new state. Current state is " + (leader.getServerState())), (leaderStateChanged || ((leader.getCurrentEpoch()) > oldLeaderCurrentEpoch)));
    }

    private static class CustomQuorumPeer extends QuorumPeer {
        private boolean stopPing;

        public CustomQuorumPeer() throws SaslException {
        }

        public void setStopPing(boolean stopPing) {
            this.stopPing = stopPing;
        }

        @Override
        protected Follower makeFollower(FileTxnSnapLog logFactory) throws IOException {
            return new Follower(this, new FollowerZooKeeperServer(logFactory, this, getZkDb())) {
                @Override
                protected void processPacket(QuorumPacket qp) throws Exception {
                    if ((stopPing) && ((qp.getType()) == (PING))) {
                        RaceConditionTest.LOG.info("Follower skipped ping");
                        throw new SocketException("Socket time out while sending the ping response");
                    } else {
                        super.processPacket(qp);
                    }
                }
            };
        }

        @Override
        protected Leader makeLeader(FileTxnSnapLog logFactory) throws IOException, X509Exception {
            LeaderZooKeeperServer zk = new LeaderZooKeeperServer(logFactory, this, getZkDb()) {
                @Override
                protected void setupRequestProcessors() {
                    /**
                     * This method is overridden to make a place to inject
                     * MockSyncRequestProcessor
                     */
                    RequestProcessor finalProcessor = new FinalRequestProcessor(this);
                    RequestProcessor toBeAppliedProcessor = new Leader.ToBeAppliedRequestProcessor(finalProcessor, getLeader());
                    commitProcessor = new CommitProcessor(toBeAppliedProcessor, Long.toString(getServerId()), false, getZooKeeperServerListener());
                    commitProcessor.start();
                    ProposalRequestProcessor proposalProcessor = new RaceConditionTest.MockProposalRequestProcessor(this, commitProcessor);
                    proposalProcessor.initialize();
                    prepRequestProcessor = new org.apache.zookeeper.server.PrepRequestProcessor(this, proposalProcessor);
                    prepRequestProcessor.start();
                    firstProcessor = new LeaderRequestProcessor(this, prepRequestProcessor);
                }
            };
            return new Leader(this, zk);
        }
    }

    private static class MockSyncRequestProcessor extends SyncRequestProcessor {
        public MockSyncRequestProcessor(ZooKeeperServer zks, RequestProcessor nextProcessor) {
            super(zks, nextProcessor);
        }

        @Override
        public void shutdown() {
            /**
             * Add a request so that something is there for SyncRequestProcessor
             * to process, while we are in shutdown flow
             */
            Request request = new Request(null, 0, 0, OpCode.delete, ByteBuffer.wrap("/deadLockIssue".getBytes()), null);
            processRequest(request);
            super.shutdown();
        }
    }

    private static class MockProposalRequestProcessor extends ProposalRequestProcessor {
        public MockProposalRequestProcessor(LeaderZooKeeperServer zks, RequestProcessor nextProcessor) {
            super(zks, nextProcessor);
            /**
             * The only purpose here is to inject the mocked
             * SyncRequestProcessor
             */
            AckRequestProcessor ackProcessor = new AckRequestProcessor(zks.getLeader());
            syncProcessor = new RaceConditionTest.MockSyncRequestProcessor(zks, ackProcessor);
        }
    }

    private static class MockTestQPMain extends QuorumPeerTestBase.TestQPMain {
        @Override
        protected QuorumPeer getQuorumPeer() throws SaslException {
            return new RaceConditionTest.CustomQuorumPeer();
        }
    }
}

