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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.Vote;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FLENewEpochTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(FLENewEpochTest.class);

    int count;

    HashMap<Long, QuorumServer> peers;

    ArrayList<FLENewEpochTest.LEThread> threads;

    File[] tmpdir;

    int[] port;

    volatile int[] round;

    Semaphore start0;

    Semaphore finish3;

    Semaphore finish0;

    class LEThread extends Thread {
        int i;

        QuorumPeer peer;

        LEThread(QuorumPeer peer, int i) {
            this.i = i;
            this.peer = peer;
            FLENewEpochTest.LOG.info(("Constructor: " + (getName())));
        }

        public void run() {
            boolean flag = true;
            try {
                while (flag) {
                    Vote v = null;
                    peer.setPeerState(LOOKING);
                    FLENewEpochTest.LOG.info(("Going to call leader election again: " + (i)));
                    v = peer.getElectionAlg().lookForLeader();
                    if (v == null) {
                        Assert.fail((("Thread " + (i)) + " got a null vote"));
                    }
                    /* A real zookeeper would take care of setting the current vote. Here
                    we do it manually.
                     */
                    peer.setCurrentVote(v);
                    FLENewEpochTest.LOG.info(((("Finished election: " + (i)) + ", ") + (v.getId())));
                    // votes[i] = v;
                    switch (i) {
                        case 0 :
                            FLENewEpochTest.LOG.info("First peer, do nothing, just join");
                            if (finish0.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                                // if(threads.get(0).peer.getPeerState() == ServerState.LEADING ){
                                FLENewEpochTest.LOG.info("Setting flag to false");
                                flag = false;
                            }
                            break;
                        case 1 :
                            FLENewEpochTest.LOG.info("Second entering case");
                            if ((round[1]) != 0) {
                                finish0.release();
                                flag = false;
                            } else {
                                finish3.acquire();
                                start0.release();
                            }
                            FLENewEpochTest.LOG.info("Second is going to start second round");
                            (round[1])++;
                            break;
                        case 2 :
                            FLENewEpochTest.LOG.info("Third peer, shutting it down");
                            QuorumBase.shutdown(peer);
                            flag = false;
                            round[2] = 1;
                            finish3.release();
                            FLENewEpochTest.LOG.info("Third leaving");
                            break;
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLENewEpoch() throws Exception {
        FLENewEpochTest.LOG.info(((("TestLE: " + (getTestName())) + ", ") + (count)));
        for (int i = 0; i < (count); i++) {
            peers.put(Long.valueOf(i), new QuorumServer(i, new InetSocketAddress("127.0.0.1", PortAssignment.unique()), new InetSocketAddress("127.0.0.1", PortAssignment.unique())));
            tmpdir[i] = ClientBase.createTmpDir();
            port[i] = PortAssignment.unique();
        }
        for (int i = 1; i < (count); i++) {
            QuorumPeer peer = new QuorumPeer(peers, tmpdir[i], tmpdir[i], port[i], 3, i, 1000, 2, 2);
            peer.startLeaderElection();
            FLENewEpochTest.LEThread thread = new FLENewEpochTest.LEThread(peer, i);
            thread.start();
            threads.add(thread);
        }
        if (!(start0.tryAcquire(4000, TimeUnit.MILLISECONDS)))
            Assert.fail("First leader election failed");

        QuorumPeer peer = new QuorumPeer(peers, tmpdir[0], tmpdir[0], port[0], 3, 0, 1000, 2, 2);
        peer.startLeaderElection();
        FLENewEpochTest.LEThread thread = new FLENewEpochTest.LEThread(peer, 0);
        thread.start();
        threads.add(thread);
        FLENewEpochTest.LOG.info(("Started threads " + (getTestName())));
        for (int i = 0; i < (threads.size()); i++) {
            threads.get(i).join(10000);
            if (threads.get(i).isAlive()) {
                Assert.fail("Threads didn't join");
            }
        }
    }
}

