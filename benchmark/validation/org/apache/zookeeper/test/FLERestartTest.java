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
package org.apache.zookeeper.test;


import ServerState.LOOKING;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.Vote;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FLERestartTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(FLETest.class);

    private int count;

    private Map<Long, QuorumServer> peers;

    private List<FLERestartTest.FLERestartThread> restartThreads;

    private File[] tmpdir;

    private int[] port;

    private Semaphore finish;

    static class TestVote {
        long leader;

        TestVote(int id, long leader) {
            this.leader = leader;
        }
    }

    class FLERestartThread extends Thread {
        int i;

        QuorumPeer peer;

        int peerRound = 0;

        FLERestartThread(QuorumPeer peer, int i) {
            this.i = i;
            this.peer = peer;
            FLERestartTest.LOG.info(("Constructor: " + (getName())));
        }

        public void run() {
            try {
                Vote v = null;
                while (true) {
                    peer.setPeerState(LOOKING);
                    FLERestartTest.LOG.info("Going to call leader election again.");
                    v = peer.getElectionAlg().lookForLeader();
                    if (v == null) {
                        FLERestartTest.LOG.info((("Thread " + (i)) + " got a null vote"));
                        break;
                    }
                    /* A real zookeeper would take care of setting the current vote. Here
                    we do it manually.
                     */
                    peer.setCurrentVote(v);
                    FLERestartTest.LOG.info(((("Finished election: " + (i)) + ", ") + (v.getId())));
                    // votes[i] = v;
                    switch (i) {
                        case 0 :
                            if ((peerRound) == 0) {
                                FLERestartTest.LOG.info("First peer, shutting it down");
                                QuorumBase.shutdown(peer);
                                shutdown();
                                peer = new QuorumPeer(peers, tmpdir[i], tmpdir[i], port[i], 3, i, 1000, 2, 2);
                                peer.startLeaderElection();
                                (peerRound)++;
                            } else {
                                finish.release(2);
                                return;
                            }
                            break;
                        case 1 :
                            FLERestartTest.LOG.info("Second entering case");
                            finish.acquire();
                            // if(threads.get(0).peer.getPeerState() == ServerState.LEADING ){
                            FLERestartTest.LOG.info("Release");
                            return;
                        case 2 :
                            FLERestartTest.LOG.info("First peer, do nothing, just join");
                            finish.acquire();
                            // if(threads.get(0).peer.getPeerState() == ServerState.LEADING ){
                            FLERestartTest.LOG.info("Release");
                            return;
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLERestart() throws Exception {
        FLERestartTest.LOG.info(((("TestLE: " + (getTestName())) + ", ") + (count)));
        for (int i = 0; i < (count); i++) {
            peers.put(Long.valueOf(i), new QuorumServer(i, new InetSocketAddress("127.0.0.1", PortAssignment.unique()), new InetSocketAddress("127.0.0.1", PortAssignment.unique())));
            tmpdir[i] = ClientBase.createTmpDir();
            port[i] = PortAssignment.unique();
        }
        for (int i = 0; i < (count); i++) {
            QuorumPeer peer = new QuorumPeer(peers, tmpdir[i], tmpdir[i], port[i], 3, i, 1000, 2, 2);
            peer.startLeaderElection();
            FLERestartTest.FLERestartThread thread = new FLERestartTest.FLERestartThread(peer, i);
            thread.start();
            restartThreads.add(thread);
        }
        FLERestartTest.LOG.info(("Started threads " + (getTestName())));
        for (int i = 0; i < (restartThreads.size()); i++) {
            restartThreads.get(i).join(10000);
            if (restartThreads.get(i).isAlive()) {
                Assert.fail("Threads didn't join");
            }
        }
    }
}

