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


import Leader.ACK;
import Leader.ACKEPOCH;
import Leader.COMMIT;
import Leader.DIFF;
import Leader.FOLLOWERINFO;
import Leader.INFORM;
import Leader.LEADERINFO;
import Leader.NEWLEADER;
import Leader.OBSERVERINFO;
import Leader.PROPOSAL;
import Leader.SNAP;
import Leader.UPTODATE;
import ZooDefs.Ids;
import ZooDefs.OpCode;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ByteBufferInputStream;
import org.apache.zookeeper.server.ByteBufferOutputStream;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.Leader.XidRolloverException;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.util.ZxidUtils;
import org.apache.zookeeper.test.TestUtils;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Leader.ACK;
import static Leader.ACKEPOCH;
import static Leader.FOLLOWERINFO;
import static Leader.PING;
import static QuorumPeer.ACCEPTED_EPOCH_FILENAME;
import static QuorumPeer.CURRENT_EPOCH_FILENAME;


public class Zab1_0Test extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(Zab1_0Test.class);

    private static final File testData = new File(System.getProperty("test.data.dir", "src/test/resources/data"));

    private static final class LeadThread extends Thread {
        private final Leader leader;

        private LeadThread(Leader leader) {
            this.leader = leader;
        }

        public void run() {
            try {
                leader.lead();
            } catch (InterruptedException e) {
                Zab1_0Test.LOG.info("Leader thread interrupted", e);
            } catch (Exception e) {
                Zab1_0Test.LOG.warn("Unexpected exception in leader thread", e);
            } finally {
                leader.shutdown("lead ended");
            }
        }
    }

    public static final class FollowerMockThread extends Thread {
        private final Leader leader;

        private final long followerSid;

        public long epoch = -1;

        public String msg = null;

        private boolean onlyGetEpochToPropose;

        private FollowerMockThread(long followerSid, Leader leader, boolean onlyGetEpochToPropose) {
            this.leader = leader;
            this.followerSid = followerSid;
            this.onlyGetEpochToPropose = onlyGetEpochToPropose;
        }

        public void run() {
            if (onlyGetEpochToPropose) {
                try {
                    epoch = leader.getEpochToPropose(followerSid, 0);
                } catch (Exception e) {
                }
            } else {
                try {
                    leader.waitForEpochAck(followerSid, new StateSummary(0, 0));
                    msg = ("FollowerMockThread (id = " + (followerSid)) + ")  returned from waitForEpochAck";
                } catch (Exception e) {
                }
            }
        }
    }

    @Test
    public void testLeaderInConnectingFollowers() throws Exception {
        File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
        tmpDir.delete();
        tmpDir.mkdir();
        Leader leader = null;
        try {
            QuorumPeer peer = ZabUtils.createQuorumPeer(tmpDir);
            leader = ZabUtils.createLeader(tmpDir, peer);
            peer.leader = leader;
            peer.setAcceptedEpoch(5);
            Zab1_0Test.FollowerMockThread f1 = new Zab1_0Test.FollowerMockThread(1, leader, true);
            Zab1_0Test.FollowerMockThread f2 = new Zab1_0Test.FollowerMockThread(2, leader, true);
            f1.start();
            f2.start();
            // wait until followers time out in getEpochToPropose - they shouldn't return
            // normally because the leader didn't execute getEpochToPropose and so its epoch was not
            // accounted for
            f1.join((((leader.self.getInitLimit()) * (leader.self.getTickTime())) + 5000));
            f2.join((((leader.self.getInitLimit()) * (leader.self.getTickTime())) + 5000));
            // even though followers timed out, their ids are in connectingFollowers, and their
            // epoch were accounted for, so the leader should not block and since it started with
            // accepted epoch = 5 it should now have 6
            try {
                long epoch = leader.getEpochToPropose(leader.self.getId(), leader.self.getAcceptedEpoch());
                Assert.assertEquals("leader got wrong epoch from getEpochToPropose", 6, epoch);
            } catch (Exception e) {
                Assert.fail("leader timed out in getEpochToPropose");
            }
        } finally {
            if (leader != null) {
                leader.shutdown("end of test");
            }
            TestUtils.deleteFileRecursively(tmpDir);
        }
    }

    /**
     * In this test, the leader sets the last accepted epoch to 5. The call
     * to getEpochToPropose should set epoch to 6 and wait until another
     * follower executes it. If in getEpochToPropose we don't check if
     * lastAcceptedEpoch == epoch, then the call from the subsequent
     * follower with lastAcceptedEpoch = 6 doesn't change the value
     * of epoch, and the test fails. It passes with the fix to predicate.
     *
     * {@link https://issues.apache.org/jira/browse/ZOOKEEPER-1343}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLastAcceptedEpoch() throws Exception {
        File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
        tmpDir.delete();
        tmpDir.mkdir();
        Leader leader = null;
        Zab1_0Test.LeadThread leadThread = null;
        try {
            QuorumPeer peer = ZabUtils.createQuorumPeer(tmpDir);
            leader = ZabUtils.createMockLeader(tmpDir, peer);
            peer.leader = leader;
            peer.setAcceptedEpoch(5);
            leadThread = new Zab1_0Test.LeadThread(leader);
            leadThread.start();
            while ((((ZabUtils.MockLeader) (leader)).getCurrentEpochToPropose()) != 6) {
                Thread.sleep(20);
            } 
            try {
                long epoch = leader.getEpochToPropose(1, 6);
                Assert.assertEquals("New proposed epoch is wrong", 7, epoch);
            } catch (Exception e) {
                Assert.fail("Timed out in getEpochToPropose");
            }
        } finally {
            if (leader != null) {
                leader.shutdown("end of test");
            }
            if (leadThread != null) {
                leadThread.interrupt();
                leadThread.join();
            }
            TestUtils.deleteFileRecursively(tmpDir);
        }
    }

    @Test
    public void testLeaderInElectingFollowers() throws Exception {
        File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
        tmpDir.delete();
        tmpDir.mkdir();
        Leader leader = null;
        try {
            QuorumPeer peer = ZabUtils.createQuorumPeer(tmpDir);
            leader = ZabUtils.createLeader(tmpDir, peer);
            peer.leader = leader;
            Zab1_0Test.FollowerMockThread f1 = new Zab1_0Test.FollowerMockThread(1, leader, false);
            Zab1_0Test.FollowerMockThread f2 = new Zab1_0Test.FollowerMockThread(2, leader, false);
            // things needed for waitForEpochAck to run (usually in leader.lead(), but we're not running leader here)
            leader.leaderStateSummary = new StateSummary(leader.self.getCurrentEpoch(), leader.zk.getLastProcessedZxid());
            f1.start();
            f2.start();
            // wait until followers time out in waitForEpochAck - they shouldn't return
            // normally because the leader didn't execute waitForEpochAck
            f1.join((((leader.self.getInitLimit()) * (leader.self.getTickTime())) + 5000));
            f2.join((((leader.self.getInitLimit()) * (leader.self.getTickTime())) + 5000));
            // make sure that they timed out and didn't return normally
            Assert.assertTrue(((f1.msg) + " without waiting for leader"), ((f1.msg) == null));
            Assert.assertTrue(((f2.msg) + " without waiting for leader"), ((f2.msg) == null));
        } finally {
            if (leader != null) {
                leader.shutdown("end of test");
            }
            TestUtils.deleteFileRecursively(tmpDir);
        }
    }

    public static interface LeaderConversation {
        void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l) throws Exception;
    }

    public static interface PopulatedLeaderConversation {
        void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l, long zxid) throws Exception;
    }

    public static interface FollowerConversation {
        void converseWithFollower(InputArchive ia, OutputArchive oa, Follower f) throws Exception;
    }

    public static interface ObserverConversation {
        void converseWithObserver(InputArchive ia, OutputArchive oa, Observer o) throws Exception;
    }

    @Test
    public void testUnnecessarySnap() throws Exception {
        testPopulatedLeaderConversation(new Zab1_0Test.PopulatedLeaderConversation() {
            @Override
            public void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l, long zxid) throws Exception {
                Assert.assertEquals(1, l.self.getAcceptedEpoch());
                Assert.assertEquals(1, l.self.getCurrentEpoch());
                /* we test a normal run. everything should work out well. */
                LearnerInfo li = new LearnerInfo(1, 65536, 0);
                byte[] liBytes = new byte[20];
                ByteBufferOutputStream.record2ByteBuffer(li, ByteBuffer.wrap(liBytes));
                QuorumPacket qp = new QuorumPacket(FOLLOWERINFO, 1, liBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(LEADERINFO, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(2, 0), qp.getZxid());
                Assert.assertEquals(ByteBuffer.wrap(qp.getData()).getInt(), 65536);
                Assert.assertEquals(2, l.self.getAcceptedEpoch());
                Assert.assertEquals(1, l.self.getCurrentEpoch());
                byte[] epochBytes = new byte[4];
                final ByteBuffer wrappedEpochBytes = ByteBuffer.wrap(epochBytes);
                wrappedEpochBytes.putInt(1);
                qp = new QuorumPacket(ACKEPOCH, zxid, epochBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(DIFF, qp.getType());
            }
        }, 2);
    }

    // We want to track the change with a callback rather than depending on timing
    class TrackerWatcher implements Watcher {
        boolean changed;

        synchronized void waitForChange() throws InterruptedException {
            while (!(changed)) {
                wait();
            } 
        }

        @Override
        public void process(WatchedEvent event) {
            if ((event.getType()) == (EventType.NodeDataChanged)) {
                synchronized(this) {
                    changed = true;
                    notifyAll();
                }
            }
        }

        public synchronized boolean changed() {
            return changed;
        }
    }

    @Test
    public void testNormalFollowerRun() throws Exception {
        testFollowerConversation(new Zab1_0Test.FollowerConversation() {
            @Override
            public void converseWithFollower(InputArchive ia, OutputArchive oa, Follower f) throws Exception {
                File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
                tmpDir.delete();
                tmpDir.mkdir();
                File logDir = f.fzk.getTxnLogFactory().getDataDir().getParentFile();
                File snapDir = f.fzk.getTxnLogFactory().getSnapDir().getParentFile();
                // Spy on ZK so we can check if a snapshot happened or not.
                f.zk = Mockito.spy(f.zk);
                try {
                    Assert.assertEquals(0, f.self.getAcceptedEpoch());
                    Assert.assertEquals(0, f.self.getCurrentEpoch());
                    // Setup a database with a single /foo node
                    ZKDatabase zkDb = new ZKDatabase(new FileTxnSnapLog(tmpDir, tmpDir));
                    final long firstZxid = ZxidUtils.makeZxid(1, 1);
                    zkDb.processTxn(new TxnHeader(13, 1313, firstZxid, 33, OpCode.create), new CreateTxn("/foo", "data1".getBytes(), Ids.OPEN_ACL_UNSAFE, false, 1));
                    Stat stat = new Stat();
                    Assert.assertEquals("data1", new String(zkDb.getData("/foo", stat, null)));
                    QuorumPacket qp = new QuorumPacket();
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(FOLLOWERINFO, qp.getType());
                    Assert.assertEquals(qp.getZxid(), 0);
                    LearnerInfo learnInfo = new LearnerInfo();
                    ByteBufferInputStream.byteBuffer2Record(ByteBuffer.wrap(qp.getData()), learnInfo);
                    Assert.assertEquals(learnInfo.getProtocolVersion(), 65536);
                    Assert.assertEquals(learnInfo.getServerid(), 0);
                    // We are simulating an established leader, so the epoch is 1
                    qp.setType(LEADERINFO);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    byte[] protoBytes = new byte[4];
                    ByteBuffer.wrap(protoBytes).putInt(65536);
                    qp.setData(protoBytes);
                    oa.writeRecord(qp, null);
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACKEPOCH, qp.getType());
                    Assert.assertEquals(0, qp.getZxid());
                    Assert.assertEquals(ZxidUtils.makeZxid(0, 0), ByteBuffer.wrap(qp.getData()).getInt());
                    Assert.assertEquals(1, f.self.getAcceptedEpoch());
                    Assert.assertEquals(0, f.self.getCurrentEpoch());
                    // Send the snapshot we created earlier
                    qp.setType(SNAP);
                    qp.setData(new byte[0]);
                    qp.setZxid(zkDb.getDataTreeLastProcessedZxid());
                    oa.writeRecord(qp, null);
                    zkDb.serializeSnapshot(oa);
                    oa.writeString("BenWasHere", null);
                    Thread.sleep(10);// Give it some time to process the snap

                    // No Snapshot taken yet, the SNAP was applied in memory
                    Mockito.verify(f.zk, Mockito.never()).takeSnapshot();
                    qp.setType(NEWLEADER);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    oa.writeRecord(qp, null);
                    // Get the ack of the new leader
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    Assert.assertEquals(1, f.self.getAcceptedEpoch());
                    Assert.assertEquals(1, f.self.getCurrentEpoch());
                    // Make sure that we did take the snapshot now
                    Mockito.verify(f.zk).takeSnapshot(true);
                    Assert.assertEquals(firstZxid, f.fzk.getLastProcessedZxid());
                    // Make sure the data was recorded in the filesystem ok
                    ZKDatabase zkDb2 = new ZKDatabase(new FileTxnSnapLog(logDir, snapDir));
                    long lastZxid = zkDb2.loadDataBase();
                    Assert.assertEquals("data1", new String(zkDb2.getData("/foo", stat, null)));
                    Assert.assertEquals(firstZxid, lastZxid);
                    // Propose an update
                    long proposalZxid = ZxidUtils.makeZxid(1, 1000);
                    proposeSetData(qp, proposalZxid, "data2", 2);
                    oa.writeRecord(qp, null);
                    Zab1_0Test.TrackerWatcher watcher = new Zab1_0Test.TrackerWatcher();
                    // The change should not have happened yet, since we haven't committed
                    Assert.assertEquals("data1", new String(f.fzk.getZKDatabase().getData("/foo", stat, watcher)));
                    // The change should happen now
                    qp.setType(COMMIT);
                    qp.setZxid(proposalZxid);
                    oa.writeRecord(qp, null);
                    qp.setType(UPTODATE);
                    qp.setZxid(0);
                    oa.writeRecord(qp, null);
                    // Read the uptodate ack
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(proposalZxid, qp.getZxid());
                    watcher.waitForChange();
                    Assert.assertEquals("data2", new String(f.fzk.getZKDatabase().getData("/foo", stat, null)));
                    // check and make sure the change is persisted
                    zkDb2 = new ZKDatabase(new FileTxnSnapLog(logDir, snapDir));
                    lastZxid = zkDb2.loadDataBase();
                    Assert.assertEquals("data2", new String(zkDb2.getData("/foo", stat, null)));
                    Assert.assertEquals(proposalZxid, lastZxid);
                } finally {
                    TestUtils.deleteFileRecursively(tmpDir);
                }
            }

            private void proposeSetData(QuorumPacket qp, long zxid, String data, int version) throws IOException {
                qp.setType(PROPOSAL);
                qp.setZxid(zxid);
                TxnHeader hdr = new TxnHeader(4, 1414, qp.getZxid(), 55, OpCode.setData);
                SetDataTxn sdt = new SetDataTxn("/foo", data.getBytes(), version);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeRecord(hdr, null);
                boa.writeRecord(sdt, null);
                qp.setData(baos.toByteArray());
            }
        });
    }

    @Test
    public void testNormalFollowerRunWithDiff() throws Exception {
        testFollowerConversation(new Zab1_0Test.FollowerConversation() {
            @Override
            public void converseWithFollower(InputArchive ia, OutputArchive oa, Follower f) throws Exception {
                File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
                tmpDir.delete();
                tmpDir.mkdir();
                File logDir = f.fzk.getTxnLogFactory().getDataDir().getParentFile();
                File snapDir = f.fzk.getTxnLogFactory().getSnapDir().getParentFile();
                // Spy on ZK so we can check if a snapshot happened or not.
                f.zk = Mockito.spy(f.zk);
                try {
                    Assert.assertEquals(0, f.self.getAcceptedEpoch());
                    Assert.assertEquals(0, f.self.getCurrentEpoch());
                    // Setup a database with a single /foo node
                    ZKDatabase zkDb = new ZKDatabase(new FileTxnSnapLog(tmpDir, tmpDir));
                    final long firstZxid = ZxidUtils.makeZxid(1, 1);
                    zkDb.processTxn(new TxnHeader(13, 1313, firstZxid, 33, OpCode.create), new CreateTxn("/foo", "data1".getBytes(), Ids.OPEN_ACL_UNSAFE, false, 1));
                    Stat stat = new Stat();
                    Assert.assertEquals("data1", new String(zkDb.getData("/foo", stat, null)));
                    QuorumPacket qp = new QuorumPacket();
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(FOLLOWERINFO, qp.getType());
                    Assert.assertEquals(qp.getZxid(), 0);
                    LearnerInfo learnInfo = new LearnerInfo();
                    ByteBufferInputStream.byteBuffer2Record(ByteBuffer.wrap(qp.getData()), learnInfo);
                    Assert.assertEquals(learnInfo.getProtocolVersion(), 65536);
                    Assert.assertEquals(learnInfo.getServerid(), 0);
                    // We are simulating an established leader, so the epoch is 1
                    qp.setType(LEADERINFO);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    byte[] protoBytes = new byte[4];
                    ByteBuffer.wrap(protoBytes).putInt(65536);
                    qp.setData(protoBytes);
                    oa.writeRecord(qp, null);
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACKEPOCH, qp.getType());
                    Assert.assertEquals(0, qp.getZxid());
                    Assert.assertEquals(ZxidUtils.makeZxid(0, 0), ByteBuffer.wrap(qp.getData()).getInt());
                    Assert.assertEquals(1, f.self.getAcceptedEpoch());
                    Assert.assertEquals(0, f.self.getCurrentEpoch());
                    // Send a diff
                    qp.setType(DIFF);
                    qp.setData(new byte[0]);
                    qp.setZxid(zkDb.getDataTreeLastProcessedZxid());
                    oa.writeRecord(qp, null);
                    final long createSessionZxid = ZxidUtils.makeZxid(1, 2);
                    proposeNewSession(qp, createSessionZxid, 819);
                    oa.writeRecord(qp, null);
                    qp.setType(COMMIT);
                    qp.setZxid(createSessionZxid);
                    oa.writeRecord(qp, null);
                    qp.setType(NEWLEADER);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    qp.setData(null);
                    oa.writeRecord(qp, null);
                    qp.setType(UPTODATE);
                    qp.setZxid(0);
                    oa.writeRecord(qp, null);
                    // Read the uptodate ack
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    // Get the ack of the new leader
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    Assert.assertEquals(1, f.self.getAcceptedEpoch());
                    Assert.assertEquals(1, f.self.getCurrentEpoch());
                    // Wait for the transactions to be written out. The thread that writes them out
                    // does not send anything back when it is done.
                    long start = System.currentTimeMillis();
                    while ((createSessionZxid != (f.fzk.getLastProcessedZxid())) && (((System.currentTimeMillis()) - start) < 50)) {
                        Thread.sleep(1);
                    } 
                    Assert.assertEquals(createSessionZxid, f.fzk.getLastProcessedZxid());
                    // Make sure the data was recorded in the filesystem ok
                    ZKDatabase zkDb2 = new ZKDatabase(new FileTxnSnapLog(logDir, snapDir));
                    start = System.currentTimeMillis();
                    zkDb2.loadDataBase();
                    while ((zkDb2.getSessionWithTimeOuts().isEmpty()) && (((System.currentTimeMillis()) - start) < 50)) {
                        Thread.sleep(1);
                        zkDb2.loadDataBase();
                    } 
                    Zab1_0Test.LOG.info(("zkdb2 sessions:" + (zkDb2.getSessions())));
                    Zab1_0Test.LOG.info(("zkdb2 with timeouts:" + (zkDb2.getSessionWithTimeOuts())));
                    Assert.assertNotNull(zkDb2.getSessionWithTimeOuts().get(4L));
                    // Snapshot was never taken during very simple sync
                    Mockito.verify(f.zk, Mockito.never()).takeSnapshot();
                } finally {
                    TestUtils.deleteFileRecursively(tmpDir);
                }
            }

            private void proposeNewSession(QuorumPacket qp, long zxid, long sessionId) throws IOException {
                qp.setType(PROPOSAL);
                qp.setZxid(zxid);
                TxnHeader hdr = new TxnHeader(4, 1414, qp.getZxid(), 55, OpCode.createSession);
                CreateSessionTxn cst = new CreateSessionTxn(30000);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeRecord(hdr, null);
                boa.writeRecord(cst, null);
                qp.setData(baos.toByteArray());
            }
        });
    }

    @Test
    public void testNormalRun() throws Exception {
        testLeaderConversation(new Zab1_0Test.LeaderConversation() {
            public void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l) throws IOException {
                Assert.assertEquals(0, l.self.getAcceptedEpoch());
                Assert.assertEquals(0, l.self.getCurrentEpoch());
                /* we test a normal run. everything should work out well. */
                LearnerInfo li = new LearnerInfo(1, 65536, 0);
                byte[] liBytes = new byte[20];
                ByteBufferOutputStream.record2ByteBuffer(li, ByteBuffer.wrap(liBytes));
                QuorumPacket qp = new QuorumPacket(Leader.FOLLOWERINFO, 0, liBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(LEADERINFO, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                Assert.assertEquals(ByteBuffer.wrap(qp.getData()).getInt(), 65536);
                Assert.assertEquals(1, l.self.getAcceptedEpoch());
                Assert.assertEquals(0, l.self.getCurrentEpoch());
                qp = new QuorumPacket(Leader.ACKEPOCH, 0, new byte[4], null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(DIFF, qp.getType());
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(NEWLEADER, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                Assert.assertEquals(1, l.self.getAcceptedEpoch());
                Assert.assertEquals(1, l.self.getCurrentEpoch());
                qp = new QuorumPacket(ACK, qp.getZxid(), null, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(UPTODATE, qp.getType());
            }
        });
    }

    @Test
    public void testTxnTimeout() throws Exception {
        testLeaderConversation(new Zab1_0Test.LeaderConversation() {
            public void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l) throws IOException, InterruptedException, XidRolloverException {
                Assert.assertEquals(0, l.self.getAcceptedEpoch());
                Assert.assertEquals(0, l.self.getCurrentEpoch());
                LearnerInfo li = new LearnerInfo(1, 65536, 0);
                byte[] liBytes = new byte[20];
                ByteBufferOutputStream.record2ByteBuffer(li, ByteBuffer.wrap(liBytes));
                QuorumPacket qp = new QuorumPacket(Leader.FOLLOWERINFO, 0, liBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(LEADERINFO, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                Assert.assertEquals(ByteBuffer.wrap(qp.getData()).getInt(), 65536);
                Assert.assertEquals(1, l.self.getAcceptedEpoch());
                Assert.assertEquals(0, l.self.getCurrentEpoch());
                qp = new QuorumPacket(Leader.ACKEPOCH, 0, new byte[4], null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(DIFF, qp.getType());
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(NEWLEADER, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                Assert.assertEquals(1, l.self.getAcceptedEpoch());
                Assert.assertEquals(1, l.self.getCurrentEpoch());
                qp = new QuorumPacket(Leader.ACK, qp.getZxid(), null, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(UPTODATE, qp.getType());
                long zxid = l.zk.getZxid();
                l.propose(new org.apache.zookeeper.server.Request(1, 1, OpCode.create, new TxnHeader(1, 1, zxid, 1, OpCode.create), new CreateTxn("/test", "hola".getBytes(), null, true, 0), zxid));
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(PROPOSAL, qp.getType());
                Zab1_0Test.LOG.info("Proposal sent.");
                for (int i = 0; i < ((2 * (ZabUtils.SYNC_LIMIT)) + 2); i++) {
                    try {
                        ia.readRecord(qp, null);
                        Zab1_0Test.LOG.info(("Ping received: " + i));
                        qp = new QuorumPacket(PING, qp.getZxid(), "".getBytes(), null);
                        oa.writeRecord(qp, null);
                    } catch (EOFException e) {
                        return;
                    }
                }
                Assert.fail("Connection hasn't been closed by leader after transaction times out.");
            }
        });
    }

    @Test
    public void testNormalObserverRun() throws Exception {
        testObserverConversation(new Zab1_0Test.ObserverConversation() {
            @Override
            public void converseWithObserver(InputArchive ia, OutputArchive oa, Observer o) throws Exception {
                File tmpDir = File.createTempFile("test", "dir", Zab1_0Test.testData);
                tmpDir.delete();
                tmpDir.mkdir();
                File logDir = o.zk.getTxnLogFactory().getDataDir().getParentFile();
                File snapDir = o.zk.getTxnLogFactory().getSnapDir().getParentFile();
                try {
                    Assert.assertEquals(0, o.self.getAcceptedEpoch());
                    Assert.assertEquals(0, o.self.getCurrentEpoch());
                    // Setup a database with a single /foo node
                    ZKDatabase zkDb = new ZKDatabase(new FileTxnSnapLog(tmpDir, tmpDir));
                    final long foo1Zxid = ZxidUtils.makeZxid(1, 1);
                    final long foo2Zxid = ZxidUtils.makeZxid(1, 2);
                    zkDb.processTxn(new TxnHeader(13, 1313, foo1Zxid, 33, OpCode.create), new CreateTxn("/foo1", "data1".getBytes(), Ids.OPEN_ACL_UNSAFE, false, 1));
                    zkDb.processTxn(new TxnHeader(13, 1313, foo2Zxid, 33, OpCode.create), new CreateTxn("/foo2", "data1".getBytes(), Ids.OPEN_ACL_UNSAFE, false, 1));
                    Stat stat = new Stat();
                    Assert.assertEquals("data1", new String(zkDb.getData("/foo1", stat, null)));
                    Assert.assertEquals("data1", new String(zkDb.getData("/foo2", stat, null)));
                    QuorumPacket qp = new QuorumPacket();
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(OBSERVERINFO, qp.getType());
                    Assert.assertEquals(qp.getZxid(), 0);
                    LearnerInfo learnInfo = new LearnerInfo();
                    ByteBufferInputStream.byteBuffer2Record(ByteBuffer.wrap(qp.getData()), learnInfo);
                    Assert.assertEquals(learnInfo.getProtocolVersion(), 65536);
                    Assert.assertEquals(learnInfo.getServerid(), 0);
                    // We are simulating an established leader, so the epoch is 1
                    qp.setType(LEADERINFO);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    byte[] protoBytes = new byte[4];
                    ByteBuffer.wrap(protoBytes).putInt(65536);
                    qp.setData(protoBytes);
                    oa.writeRecord(qp, null);
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACKEPOCH, qp.getType());
                    Assert.assertEquals(0, qp.getZxid());
                    Assert.assertEquals(ZxidUtils.makeZxid(0, 0), ByteBuffer.wrap(qp.getData()).getInt());
                    Assert.assertEquals(1, o.self.getAcceptedEpoch());
                    Assert.assertEquals(0, o.self.getCurrentEpoch());
                    // Send the snapshot we created earlier
                    qp.setType(SNAP);
                    qp.setData(new byte[0]);
                    qp.setZxid(zkDb.getDataTreeLastProcessedZxid());
                    oa.writeRecord(qp, null);
                    zkDb.serializeSnapshot(oa);
                    oa.writeString("BenWasHere", null);
                    qp.setType(NEWLEADER);
                    qp.setZxid(ZxidUtils.makeZxid(1, 0));
                    oa.writeRecord(qp, null);
                    // Get the ack of the new leader
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    Assert.assertEquals(1, o.self.getAcceptedEpoch());
                    Assert.assertEquals(1, o.self.getCurrentEpoch());
                    Assert.assertEquals(foo2Zxid, o.zk.getLastProcessedZxid());
                    // Make sure the data was recorded in the filesystem ok
                    ZKDatabase zkDb2 = new ZKDatabase(new FileTxnSnapLog(logDir, snapDir));
                    long lastZxid = zkDb2.loadDataBase();
                    Assert.assertEquals("data1", new String(zkDb2.getData("/foo1", stat, null)));
                    Assert.assertEquals(foo2Zxid, lastZxid);
                    // Register watch
                    Zab1_0Test.TrackerWatcher watcher = new Zab1_0Test.TrackerWatcher();
                    Assert.assertEquals("data1", new String(o.zk.getZKDatabase().getData("/foo2", stat, watcher)));
                    // Propose /foo1 update
                    long proposalZxid = ZxidUtils.makeZxid(1, 1000);
                    proposeSetData(qp, "/foo1", proposalZxid, "data2", 2);
                    oa.writeRecord(qp, null);
                    // Commit /foo1 update
                    qp.setType(COMMIT);
                    qp.setZxid(proposalZxid);
                    oa.writeRecord(qp, null);
                    // Inform /foo2 update
                    long informZxid = ZxidUtils.makeZxid(1, 1001);
                    proposeSetData(qp, "/foo2", informZxid, "data2", 2);
                    qp.setType(INFORM);
                    oa.writeRecord(qp, null);
                    qp.setType(UPTODATE);
                    qp.setZxid(0);
                    oa.writeRecord(qp, null);
                    // Read the uptodate ack
                    Zab1_0Test.readPacketSkippingPing(ia, qp);
                    Assert.assertEquals(ACK, qp.getType());
                    Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                    // Data should get updated
                    watcher.waitForChange();
                    Assert.assertEquals("data2", new String(o.zk.getZKDatabase().getData("/foo1", stat, null)));
                    Assert.assertEquals("data2", new String(o.zk.getZKDatabase().getData("/foo2", stat, null)));
                    // Shutdown sequence guarantee that all pending requests
                    // in sync request processor get flush to disk
                    o.zk.shutdown();
                    zkDb2 = new ZKDatabase(new FileTxnSnapLog(logDir, snapDir));
                    lastZxid = zkDb2.loadDataBase();
                    Assert.assertEquals("data2", new String(zkDb2.getData("/foo1", stat, null)));
                    Assert.assertEquals("data2", new String(zkDb2.getData("/foo2", stat, null)));
                    Assert.assertEquals(informZxid, lastZxid);
                } finally {
                    TestUtils.deleteFileRecursively(tmpDir);
                }
            }

            private void proposeSetData(QuorumPacket qp, String path, long zxid, String data, int version) throws IOException {
                qp.setType(PROPOSAL);
                qp.setZxid(zxid);
                TxnHeader hdr = new TxnHeader(4, 1414, qp.getZxid(), 55, OpCode.setData);
                SetDataTxn sdt = new SetDataTxn(path, data.getBytes(), version);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeRecord(hdr, null);
                boa.writeRecord(sdt, null);
                qp.setData(baos.toByteArray());
            }
        });
    }

    @Test
    public void testLeaderBehind() throws Exception {
        testLeaderConversation(new Zab1_0Test.LeaderConversation() {
            public void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l) throws IOException {
                /* we test a normal run. everything should work out well. */
                LearnerInfo li = new LearnerInfo(1, 65536, 0);
                byte[] liBytes = new byte[20];
                ByteBufferOutputStream.record2ByteBuffer(li, ByteBuffer.wrap(liBytes));
                /* we are going to say we last acked epoch 20 */
                QuorumPacket qp = new QuorumPacket(Leader.FOLLOWERINFO, ZxidUtils.makeZxid(20, 0), liBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(LEADERINFO, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(21, 0), qp.getZxid());
                Assert.assertEquals(ByteBuffer.wrap(qp.getData()).getInt(), 65536);
                qp = new QuorumPacket(Leader.ACKEPOCH, 0, new byte[4], null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(DIFF, qp.getType());
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(NEWLEADER, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(21, 0), qp.getZxid());
                qp = new QuorumPacket(Leader.ACK, qp.getZxid(), null, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(UPTODATE, qp.getType());
            }
        });
    }

    /**
     * Tests that when a quorum of followers send LearnerInfo but do not ack the epoch (which is sent
     * by the leader upon receipt of LearnerInfo from a quorum), the leader does not start using this epoch
     * as it would in the normal case (when a quorum do ack the epoch). This tests ZK-1192
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAbandonBeforeACKEpoch() throws Exception {
        testLeaderConversation(new Zab1_0Test.LeaderConversation() {
            public void converseWithLeader(InputArchive ia, OutputArchive oa, Leader l) throws IOException, InterruptedException {
                /* we test a normal run. everything should work out well. */
                LearnerInfo li = new LearnerInfo(1, 65536, 0);
                byte[] liBytes = new byte[20];
                ByteBufferOutputStream.record2ByteBuffer(li, ByteBuffer.wrap(liBytes));
                QuorumPacket qp = new QuorumPacket(Leader.FOLLOWERINFO, 0, liBytes, null);
                oa.writeRecord(qp, null);
                Zab1_0Test.readPacketSkippingPing(ia, qp);
                Assert.assertEquals(LEADERINFO, qp.getType());
                Assert.assertEquals(ZxidUtils.makeZxid(1, 0), qp.getZxid());
                Assert.assertEquals(ByteBuffer.wrap(qp.getData()).getInt(), 65536);
                Thread.sleep((((l.self.getInitLimit()) * (l.self.getTickTime())) + 5000));
                // The leader didn't get a quorum of acks - make sure that leader's current epoch is not advanced
                Assert.assertEquals(0, l.self.getCurrentEpoch());
            }
        });
    }

    static class ConversableFollower extends Follower {
        ConversableFollower(QuorumPeer self, FollowerZooKeeperServer zk) {
            super(self, zk);
        }

        QuorumServer leaderQuorumServer;

        public void setLeaderQuorumServer(QuorumServer quorumServer) {
            leaderQuorumServer = quorumServer;
        }

        @Override
        protected QuorumServer findLeader() {
            return leaderQuorumServer;
        }
    }

    static class ConversableObserver extends Observer {
        ConversableObserver(QuorumPeer self, ObserverZooKeeperServer zk) {
            super(self, zk);
        }

        QuorumServer leaderQuorumServer;

        public void setLeaderQuorumServer(QuorumServer quorumServer) {
            leaderQuorumServer = quorumServer;
        }

        @Override
        protected QuorumServer findLeader() {
            return leaderQuorumServer;
        }
    }

    @Test
    public void testInitialAcceptedCurrent() throws Exception {
        File tmpDir = File.createTempFile("test", ".dir", Zab1_0Test.testData);
        tmpDir.delete();
        tmpDir.mkdir();
        try {
            FileTxnSnapLog logFactory = new FileTxnSnapLog(tmpDir, tmpDir);
            File version2 = new File(tmpDir, "version-2");
            version2.mkdir();
            logFactory.save(new DataTree(), new ConcurrentHashMap<Long, Integer>(), false);
            long zxid = ZxidUtils.makeZxid(3, 3);
            logFactory.append(new org.apache.zookeeper.server.Request(1, 1, OpCode.error, new TxnHeader(1, 1, zxid, 1, OpCode.error), new ErrorTxn(1), zxid));
            logFactory.commit();
            ZKDatabase zkDb = new ZKDatabase(logFactory);
            QuorumPeer peer = QuorumPeer.testingQuorumPeer();
            peer.setZKDatabase(zkDb);
            peer.setTxnFactory(logFactory);
            peer.getLastLoggedZxid();
            Assert.assertEquals(3, peer.getAcceptedEpoch());
            Assert.assertEquals(3, peer.getCurrentEpoch());
            Assert.assertEquals(3, Integer.parseInt(readContentsOfFile(new File(version2, CURRENT_EPOCH_FILENAME))));
            Assert.assertEquals(3, Integer.parseInt(readContentsOfFile(new File(version2, ACCEPTED_EPOCH_FILENAME))));
        } finally {
            TestUtils.deleteFileRecursively(tmpDir);
        }
    }
}

