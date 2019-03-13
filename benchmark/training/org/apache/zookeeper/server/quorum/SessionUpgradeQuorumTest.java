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
import States.CLOSED;
import States.CONNECTED;
import States.CONNECTING;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import ZooDefs.OpCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.server.ByteBufferInputStream;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionUpgradeQuorumTest extends QuorumPeerTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(SessionUpgradeQuorumTest.class);

    public static final int CONNECTION_TIMEOUT = ClientBase.CONNECTION_TIMEOUT;

    public static final int SERVER_COUNT = 3;

    private QuorumPeerTestBase.MainThread[] mt;

    private int[] clientPorts;

    private SessionUpgradeQuorumTest.TestQPMainDropSessionUpgrading[] qpMain;

    @Test
    public void testLocalSessionUpgradeSnapshot() throws IOException, InterruptedException {
        // select the candidate of follower
        int leader = -1;
        int followerA = -1;
        for (int i = (SessionUpgradeQuorumTest.SERVER_COUNT) - 1; i >= 0; i--) {
            if ((mt[i].main.quorumPeer.leader) != null) {
                leader = i;
            } else
                if (followerA == (-1)) {
                    followerA = i;
                }

        }
        SessionUpgradeQuorumTest.LOG.info("follower A is {}", followerA);
        qpMain[followerA].setDropCreateSession(true);
        // create a client, and create an ephemeral node to trigger the
        // upgrading process
        final String node = "/node-1";
        ZooKeeper zk = new ZooKeeper(("127.0.0.1:" + (clientPorts[followerA])), ClientBase.CONNECTION_TIMEOUT, this);
        waitForOne(zk, CONNECTED);
        // clone the session id and passwd for later usage
        long sessionId = zk.getSessionId();
        // should fail because of the injection
        try {
            zk.create(node, new byte[2], OPEN_ACL_UNSAFE, EPHEMERAL);
            Assert.fail(("expect to failed to upgrade session due to the " + "TestQPMainDropSessionUpgrading is being used"));
        } catch (KeeperException e) {
            SessionUpgradeQuorumTest.LOG.info("KeeperException when create ephemeral node, {}", e);
        }
        // force to take snapshot
        qpMain[followerA].quorumPeer.follower.zk.takeSnapshot(true);
        // wait snapshot finish
        Thread.sleep(500);
        // shutdown all servers
        for (int i = 0; i < (SessionUpgradeQuorumTest.SERVER_COUNT); i++) {
            mt[i].shutdown();
        }
        ArrayList<States> waitStates = new ArrayList<States>();
        waitStates.add(CONNECTING);
        waitStates.add(CLOSED);
        waitForOne(zk, waitStates);
        // start the servers again, start follower A last as we want to
        // keep it running as follower
        for (int i = 0; i < (SessionUpgradeQuorumTest.SERVER_COUNT); i++) {
            mt[i].start();
        }
        for (int i = 0; i < (SessionUpgradeQuorumTest.SERVER_COUNT); i++) {
            Assert.assertTrue((("waiting for server " + i) + " being up"), ClientBase.waitForServerUp(("127.0.0.1:" + (clientPorts[i])), SessionUpgradeQuorumTest.CONNECTION_TIMEOUT));
        }
        // check global session not exist on follower A
        for (int i = 0; i < (SessionUpgradeQuorumTest.SERVER_COUNT); i++) {
            ConcurrentHashMap<Long, Integer> sessions = getZkDb().getSessionWithTimeOuts();
            Assert.assertFalse((((("server " + i) + " should not have global ") + "session ") + sessionId), sessions.containsKey(sessionId));
        }
        zk.close();
    }

    @Test
    public void testOnlyUpgradeSessionOnce() throws IOException, InterruptedException, KeeperException {
        // create a client, and create an ephemeral node to trigger the
        // upgrading process
        final String node = "/node-1";
        ZooKeeper zk = new ZooKeeper(("127.0.0.1:" + (clientPorts[0])), ClientBase.CONNECTION_TIMEOUT, this);
        waitForOne(zk, CONNECTED);
        long sessionId = zk.getSessionId();
        QuorumZooKeeperServer server = ((QuorumZooKeeperServer) (mt[0].main.quorumPeer.getActiveServer()));
        Request create1 = createEphemeralRequest("/data-1", sessionId);
        Request create2 = createEphemeralRequest("/data-2", sessionId);
        Assert.assertNotNull("failed to upgrade on a ephemeral create", server.checkUpgradeSession(create1));
        Assert.assertNull("tried to upgrade again", server.checkUpgradeSession(create2));
        // clean al the setups and close the zk
        zk.close();
    }

    private static class TestQPMainDropSessionUpgrading extends QuorumPeerTestBase.TestQPMain {
        private volatile boolean shouldDrop = false;

        public void setDropCreateSession(boolean dropCreateSession) {
            shouldDrop = dropCreateSession;
        }

        @Override
        protected QuorumPeer getQuorumPeer() throws SaslException {
            return new QuorumPeer() {
                @Override
                protected Follower makeFollower(FileTxnSnapLog logFactory) throws IOException {
                    return new Follower(this, new FollowerZooKeeperServer(logFactory, this, getZkDb())) {
                        @Override
                        protected void request(Request request) throws IOException {
                            if (!(shouldDrop)) {
                                super.request(request);
                                return;
                            }
                            SessionUpgradeQuorumTest.LOG.info("request is {}, cnxn {}", request.type, request.cnxn);
                            if ((request.type) == (OpCode.createSession)) {
                                SessionUpgradeQuorumTest.LOG.info("drop createSession request {}", request);
                                return;
                            }
                            if (((request.type) == (OpCode.create)) && ((request.cnxn) != null)) {
                                CreateRequest createRequest = new CreateRequest();
                                request.request.rewind();
                                ByteBufferInputStream.byteBuffer2Record(request.request, createRequest);
                                request.request.rewind();
                                try {
                                    CreateMode createMode = CreateMode.fromFlag(createRequest.getFlags());
                                    if (createMode.isEphemeral()) {
                                        request.cnxn.sendCloseSession();
                                    }
                                } catch (KeeperException e) {
                                }
                                return;
                            }
                            super.request(request);
                        }
                    };
                }
            };
        }
    }
}

