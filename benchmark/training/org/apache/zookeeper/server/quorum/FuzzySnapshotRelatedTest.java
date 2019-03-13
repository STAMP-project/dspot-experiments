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


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import LearnerHandler.FORCE_SNAP_SYNC;
import States.CONNECTED;
import States.CONNECTING;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.security.sasl.SaslException;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.DataNode;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test cases used to catch corner cases due to fuzzy snapshot.
 */
public class FuzzySnapshotRelatedTest extends QuorumPeerTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(FuzzySnapshotRelatedTest.class);

    QuorumPeerTestBase.MainThread[] mt = null;

    ZooKeeper[] zk = null;

    int[] clientPorts = null;

    int leaderId;

    int followerA;

    @Test
    public void testMultiOpConsistency() throws Exception {
        FuzzySnapshotRelatedTest.LOG.info("Create a parent node");
        final String path = "/testMultiOpConsistency";
        createEmptyNode(zk[followerA], path);
        FuzzySnapshotRelatedTest.LOG.info("Hook to catch the 2nd sub create node txn in multi-op");
        FuzzySnapshotRelatedTest.CustomDataTree dt = ((FuzzySnapshotRelatedTest.CustomDataTree) (getZkDb().getDataTree()));
        final ZooKeeperServer zkServer = mt[followerA].main.quorumPeer.getActiveServer();
        String node1 = path + "/1";
        String node2 = path + "/2";
        dt.addNodeCreateListener(node2, new FuzzySnapshotRelatedTest.NodeCreateListener() {
            @Override
            public void process(String path) {
                FuzzySnapshotRelatedTest.LOG.info("Take a snapshot");
                zkServer.takeSnapshot(true);
            }
        });
        FuzzySnapshotRelatedTest.LOG.info("Issue a multi op to create 2 nodes");
        zk[followerA].multi(Arrays.asList(Op.create(node1, node1.getBytes(), OPEN_ACL_UNSAFE, PERSISTENT), Op.create(node2, node2.getBytes(), OPEN_ACL_UNSAFE, PERSISTENT)));
        FuzzySnapshotRelatedTest.LOG.info("Restart the server");
        mt[followerA].shutdown();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTING);
        mt[followerA].start();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTED);
        FuzzySnapshotRelatedTest.LOG.info("Make sure the node consistent with leader");
        Assert.assertEquals(new String(zk[leaderId].getData(node2, null, null)), new String(zk[followerA].getData(node2, null, null)));
    }

    /**
     * It's possibel during SNAP sync, the parent is serialized before the
     * child get deleted during sending the snapshot over.
     *
     * In which case, we need to make sure the pzxid get correctly updated
     * when applying the txns received.
     */
    @Test
    public void testPZxidUpdatedDuringSnapSyncing() throws Exception {
        FuzzySnapshotRelatedTest.LOG.info("Enable force snapshot sync");
        System.setProperty(FORCE_SNAP_SYNC, "true");
        final String parent = "/testPZxidUpdatedWhenDeletingNonExistNode";
        final String child = parent + "/child";
        createEmptyNode(zk[leaderId], parent);
        createEmptyNode(zk[leaderId], child);
        FuzzySnapshotRelatedTest.LOG.info("shutdown follower {}", followerA);
        mt[followerA].shutdown();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTING);
        FuzzySnapshotRelatedTest.LOG.info("Set up ZKDatabase to catch the node serializing in DataTree");
        addSerializeListener(leaderId, parent, child);
        FuzzySnapshotRelatedTest.LOG.info("Restart follower A to trigger a SNAP sync with leader");
        mt[followerA].start();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTED);
        FuzzySnapshotRelatedTest.LOG.info(("Check and make sure the pzxid of the parent is the same " + "on leader and follower A"));
        compareStat(parent, leaderId, followerA);
    }

    /**
     * It's possible during taking fuzzy snapshot, the parent is serialized
     * before the child get deleted in the fuzzy range.
     *
     * In which case, we need to make sure the pzxid get correctly updated
     * when replaying the txns.
     */
    @Test
    public void testPZxidUpdatedWhenLoadingSnapshot() throws Exception {
        final String parent = "/testPZxidUpdatedDuringTakingSnapshot";
        final String child = parent + "/child";
        createEmptyNode(zk[followerA], parent);
        createEmptyNode(zk[followerA], child);
        FuzzySnapshotRelatedTest.LOG.info("Set up ZKDatabase to catch the node serializing in DataTree");
        addSerializeListener(followerA, parent, child);
        FuzzySnapshotRelatedTest.LOG.info("Take snapshot on follower A");
        ZooKeeperServer zkServer = mt[followerA].main.quorumPeer.getActiveServer();
        zkServer.takeSnapshot(true);
        FuzzySnapshotRelatedTest.LOG.info("Restarting follower A to load snapshot");
        mt[followerA].shutdown();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTING);
        mt[followerA].start();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTED);
        FuzzySnapshotRelatedTest.LOG.info(("Check and make sure the pzxid of the parent is the same " + "on leader and follower A"));
        compareStat(parent, leaderId, followerA);
    }

    @Test
    public void testGlobalSessionConsistency() throws Exception {
        FuzzySnapshotRelatedTest.LOG.info("Hook to catch the commitSession event on followerA");
        FuzzySnapshotRelatedTest.CustomizedQPMain followerAMain = ((FuzzySnapshotRelatedTest.CustomizedQPMain) (mt[followerA].main));
        final ZooKeeperServer zkServer = followerAMain.quorumPeer.getActiveServer();
        // only take snapshot for the next global session we're going to create
        final AtomicBoolean shouldTakeSnapshot = new AtomicBoolean(true);
        followerAMain.setCommitSessionListener(new FuzzySnapshotRelatedTest.CommitSessionListener() {
            @Override
            public void process(long sessionId) {
                FuzzySnapshotRelatedTest.LOG.info("Take snapshot");
                if (shouldTakeSnapshot.getAndSet(false)) {
                    zkServer.takeSnapshot(true);
                }
            }
        });
        FuzzySnapshotRelatedTest.LOG.info("Create a global session");
        ZooKeeper globalClient = new ZooKeeper(("127.0.0.1:" + (clientPorts[followerA])), ClientBase.CONNECTION_TIMEOUT, this);
        QuorumPeerMainTest.waitForOne(globalClient, CONNECTED);
        FuzzySnapshotRelatedTest.LOG.info("Restart followerA to load the data from disk");
        mt[followerA].shutdown();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTING);
        mt[followerA].start();
        QuorumPeerMainTest.waitForOne(zk[followerA], CONNECTED);
        FuzzySnapshotRelatedTest.LOG.info("Make sure the global sessions are consistent with leader");
        Map<Long, Integer> globalSessionsOnLeader = getZkDb().getSessionWithTimeOuts();
        if ((mt[followerA].main.quorumPeer) == null) {
            FuzzySnapshotRelatedTest.LOG.info("quorumPeer is null");
        }
        if ((getZkDb()) == null) {
            FuzzySnapshotRelatedTest.LOG.info("zkDb is null");
        }
        Map<Long, Integer> globalSessionsOnFollowerA = getZkDb().getSessionWithTimeOuts();
        FuzzySnapshotRelatedTest.LOG.info("sessions are {}, {}", globalSessionsOnLeader.keySet(), globalSessionsOnFollowerA.keySet());
        Assert.assertTrue(globalSessionsOnFollowerA.keySet().containsAll(globalSessionsOnLeader.keySet()));
    }

    static interface NodeCreateListener {
        public void process(String path);
    }

    static class CustomDataTree extends DataTree {
        Map<String, FuzzySnapshotRelatedTest.NodeCreateListener> nodeCreateListeners = new HashMap<String, FuzzySnapshotRelatedTest.NodeCreateListener>();

        Map<String, FuzzySnapshotRelatedTest.NodeSerializeListener> listeners = new HashMap<String, FuzzySnapshotRelatedTest.NodeSerializeListener>();

        @Override
        public void serializeNodeData(OutputArchive oa, String path, DataNode node) throws IOException {
            super.serializeNodeData(oa, path, node);
            FuzzySnapshotRelatedTest.NodeSerializeListener listener = listeners.get(path);
            if (listener != null) {
                listener.nodeSerialized(path);
            }
        }

        public void addListener(String path, FuzzySnapshotRelatedTest.NodeSerializeListener listener) {
            listeners.put(path, listener);
        }

        @Override
        public void createNode(final String path, byte[] data, List<ACL> acl, long ephemeralOwner, int parentCVersion, long zxid, long time, Stat outputStat) throws NoNodeException, NodeExistsException {
            FuzzySnapshotRelatedTest.NodeCreateListener listener = nodeCreateListeners.get(path);
            if (listener != null) {
                listener.process(path);
            }
            super.createNode(path, data, acl, ephemeralOwner, parentCVersion, zxid, time, outputStat);
        }

        public void addNodeCreateListener(String path, FuzzySnapshotRelatedTest.NodeCreateListener listener) {
            nodeCreateListeners.put(path, listener);
        }
    }

    static interface NodeSerializeListener {
        public void nodeSerialized(String path);
    }

    static interface CommitSessionListener {
        public void process(long sessionId);
    }

    static class CustomizedQPMain extends QuorumPeerTestBase.TestQPMain {
        FuzzySnapshotRelatedTest.CommitSessionListener commitSessionListener;

        public void setCommitSessionListener(FuzzySnapshotRelatedTest.CommitSessionListener listener) {
            this.commitSessionListener = listener;
        }

        @Override
        protected QuorumPeer getQuorumPeer() throws SaslException {
            return new QuorumPeer() {
                @Override
                public void setZKDatabase(ZKDatabase database) {
                    super.setZKDatabase(new ZKDatabase(getTxnFactory()) {
                        @Override
                        public DataTree createDataTree() {
                            return new FuzzySnapshotRelatedTest.CustomDataTree();
                        }
                    });
                }

                @Override
                protected Follower makeFollower(FileTxnSnapLog logFactory) throws IOException {
                    return new Follower(this, new FollowerZooKeeperServer(logFactory, this, getZkDb()) {
                        @Override
                        public void createSessionTracker() {
                            sessionTracker = new LearnerSessionTracker(this, getZKDatabase().getSessionWithTimeOuts(), this.tickTime, self.getId(), self.areLocalSessionsEnabled(), getZooKeeperServerListener()) {
                                public synchronized boolean commitSession(long sessionId, int sessionTimeout) {
                                    if ((commitSessionListener) != null) {
                                        commitSessionListener.process(sessionId);
                                    }
                                    return super.commitSession(sessionId, sessionTimeout);
                                }
                            };
                        }
                    });
                }
            };
        }
    }
}

