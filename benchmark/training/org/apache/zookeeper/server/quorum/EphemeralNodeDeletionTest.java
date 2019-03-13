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


import CreateMode.EPHEMERAL;
import Ids.OPEN_ACL_UNSAFE;
import ServerState.FOLLOWING;
import ServerState.LEADING;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;

import static Leader.PROPOSAL;


public class EphemeralNodeDeletionTest extends QuorumPeerTestBase {
    private static int SERVER_COUNT = 3;

    private QuorumPeerTestBase.MainThread[] mt = new QuorumPeerTestBase.MainThread[EphemeralNodeDeletionTest.SERVER_COUNT];

    /**
     * Test case for https://issues.apache.org/jira/browse/ZOOKEEPER-2355.
     * ZooKeeper ephemeral node is never deleted if follower fail while reading
     * the proposal packet.
     */
    @Test(timeout = 120000)
    public void testEphemeralNodeDeletion() throws Exception {
        final int[] clientPorts = new int[EphemeralNodeDeletionTest.SERVER_COUNT];
        StringBuilder sb = new StringBuilder();
        String server;
        for (int i = 0; i < (EphemeralNodeDeletionTest.SERVER_COUNT); i++) {
            clientPorts[i] = PortAssignment.unique();
            server = (((((("server." + i) + "=127.0.0.1:") + (PortAssignment.unique())) + ":") + (PortAssignment.unique())) + ":participant;127.0.0.1:") + (clientPorts[i]);
            sb.append((server + "\n"));
        }
        String currentQuorumCfgSection = sb.toString();
        // start all the servers
        for (int i = 0; i < (EphemeralNodeDeletionTest.SERVER_COUNT); i++) {
            mt[i] = new QuorumPeerTestBase.MainThread(i, clientPorts[i], currentQuorumCfgSection, false) {
                @Override
                public QuorumPeerTestBase.TestQPMain getTestQPMain() {
                    return new EphemeralNodeDeletionTest.MockTestQPMain();
                }
            };
            mt[i].start();
        }
        // ensure all servers started
        for (int i = 0; i < (EphemeralNodeDeletionTest.SERVER_COUNT); i++) {
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts[i])), ClientBase.CONNECTION_TIMEOUT));
        }
        ClientBase.CountdownWatcher watch = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(("127.0.0.1:" + (clientPorts[1])), ClientBase.CONNECTION_TIMEOUT, watch);
        watch.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        /**
         * now the problem scenario starts
         */
        Stat firstEphemeralNode = new Stat();
        // 1: create ephemeral node
        String nodePath = "/e1";
        zk.create(nodePath, "1".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL, firstEphemeralNode);
        Assert.assertEquals("Current session and ephemeral owner should be same", zk.getSessionId(), firstEphemeralNode.getEphemeralOwner());
        // 2: inject network problem in one of the follower
        EphemeralNodeDeletionTest.CustomQuorumPeer follower = ((EphemeralNodeDeletionTest.CustomQuorumPeer) (getByServerState(mt, FOLLOWING)));
        follower.setInjectError(true);
        // 3: close the session so that ephemeral node is deleted
        zk.close();
        // remove the error
        follower.setInjectError(false);
        Assert.assertTrue("Faulted Follower should have joined quorum by now", ClientBase.waitForServerUp(("127.0.0.1:" + (getClientPort())), ClientBase.CONNECTION_TIMEOUT));
        QuorumPeer leader = getByServerState(mt, LEADING);
        Assert.assertNotNull("Leader should not be null", leader);
        Assert.assertTrue("Leader must be running", ClientBase.waitForServerUp(("127.0.0.1:" + (leader.getClientPort())), ClientBase.CONNECTION_TIMEOUT));
        watch = new ClientBase.CountdownWatcher();
        zk = new ZooKeeper(("127.0.0.1:" + (leader.getClientPort())), ClientBase.CONNECTION_TIMEOUT, watch);
        watch.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        Stat exists = zk.exists(nodePath, false);
        Assert.assertNull("Node must have been deleted from leader", exists);
        ClientBase.CountdownWatcher followerWatch = new ClientBase.CountdownWatcher();
        ZooKeeper followerZK = new ZooKeeper(("127.0.0.1:" + (getClientPort())), ClientBase.CONNECTION_TIMEOUT, followerWatch);
        followerWatch.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        Stat nodeAtFollower = followerZK.exists(nodePath, false);
        // Problem 1: Follower had one extra ephemeral node /e1
        Assert.assertNull("ephemeral node must not exist", nodeAtFollower);
        // Create the node with another session
        Stat currentEphemeralNode = new Stat();
        zk.create(nodePath, "2".getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL, currentEphemeralNode);
        // close the session and newly created ephemeral node should be deleted
        zk.close();
        EphemeralNodeDeletionTest.SyncCallback cb = new EphemeralNodeDeletionTest.SyncCallback();
        followerZK.sync(nodePath, cb, null);
        cb.sync.await(ClientBase.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        nodeAtFollower = followerZK.exists(nodePath, false);
        // Problem 2: Before fix, after session close the ephemeral node
        // was not getting deleted. But now after the fix after session close
        // ephemeral node is getting deleted.
        Assert.assertNull("After session close ephemeral node must be deleted", nodeAtFollower);
        followerZK.close();
    }

    static class CustomQuorumPeer extends QuorumPeer {
        private boolean injectError = false;

        public CustomQuorumPeer() throws SaslException {
        }

        @Override
        protected Follower makeFollower(FileTxnSnapLog logFactory) throws IOException {
            return new Follower(this, new FollowerZooKeeperServer(logFactory, this, getZkDb())) {
                @Override
                void readPacket(QuorumPacket pp) throws IOException {
                    /**
                     * In real scenario got SocketTimeoutException while reading
                     * the packet from leader because of network problem, but
                     * here throwing SocketTimeoutException based on whether
                     * error is injected or not
                     */
                    super.readPacket(pp);
                    if ((injectError) && ((pp.getType()) == (PROPOSAL))) {
                        String type = LearnerHandler.packetToString(pp);
                        throw new SocketTimeoutException(("Socket timeout while reading the packet for operation " + type));
                    }
                }
            };
        }

        public void setInjectError(boolean injectError) {
            this.injectError = injectError;
        }
    }

    static class MockTestQPMain extends QuorumPeerTestBase.TestQPMain {
        @Override
        protected QuorumPeer getQuorumPeer() throws SaslException {
            return new EphemeralNodeDeletionTest.CustomQuorumPeer();
        }
    }

    private static class SyncCallback implements AsyncCallback.VoidCallback {
        private final CountDownLatch sync = new CountDownLatch(1);

        @Override
        public void processResult(int rc, String path, Object ctx) {
            sync.countDown();
        }
    }
}

