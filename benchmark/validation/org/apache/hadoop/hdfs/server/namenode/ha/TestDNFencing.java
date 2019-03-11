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
package org.apache.hadoop.hdfs.server.namenode.ha;


import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyDefault;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.DelayAnswer;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDNFencing {
    protected static final Logger LOG = LoggerFactory.getLogger(TestDNFencing.class);

    private static final String TEST_FILE = "/testStandbyIsHot";

    private static final Path TEST_FILE_PATH = new Path(TestDNFencing.TEST_FILE);

    private static final int SMALL_BLOCK = 1024;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private NameNode nn1;

    private NameNode nn2;

    private FileSystem fs;

    static {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
    }

    @Test
    public void testDnFencing() throws Exception {
        // Create a file with replication level 3.
        DFSTestUtil.createFile(fs, TestDNFencing.TEST_FILE_PATH, (30 * (TestDNFencing.SMALL_BLOCK)), ((short) (3)), 1L);
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, TestDNFencing.TEST_FILE_PATH);
        // Drop its replication count to 1, so it becomes over-replicated.
        // Then compute the invalidation of the extra blocks and trigger
        // heartbeats so the invalidations are flushed to the DNs.
        nn1.getRpcServer().setReplication(TestDNFencing.TEST_FILE, ((short) (1)));
        BlockManagerTestUtil.computeInvalidationWork(nn1.getNamesystem().getBlockManager());
        cluster.triggerHeartbeats();
        // Transition nn2 to active even though nn1 still thinks it's active.
        banner("Failing to NN2 but let NN1 continue to think it's active");
        NameNodeAdapter.abortEditLogs(nn1);
        NameNodeAdapter.enterSafeMode(nn1, false);
        cluster.transitionToActive(1);
        // Check that the standby picked up the replication change.
        Assert.assertEquals(1, nn2.getRpcServer().getFileInfo(TestDNFencing.TEST_FILE).getReplication());
        // Dump some info for debugging purposes.
        banner("NN2 Metadata immediately after failover");
        doMetasave(nn2);
        banner("Triggering heartbeats and block reports so that fencing is completed");
        cluster.triggerHeartbeats();
        cluster.triggerBlockReports();
        banner("Metadata after nodes have all block-reported");
        doMetasave(nn2);
        // Force a rescan of postponedMisreplicatedBlocks.
        BlockManager nn2BM = nn2.getNamesystem().getBlockManager();
        BlockManagerTestUtil.checkHeartbeat(nn2BM);
        BlockManagerTestUtil.rescanPostponedMisreplicatedBlocks(nn2BM);
        // The blocks should no longer be postponed.
        Assert.assertEquals(0, nn2.getNamesystem().getPostponedMisreplicatedBlocks());
        // Wait for NN2 to enact its deletions (redundancy monitor has to run, etc)
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        cluster.triggerHeartbeats();
        HATestUtil.waitForDNDeletions(cluster);
        cluster.triggerDeletionReports();
        Assert.assertEquals(0, nn2.getNamesystem().getUnderReplicatedBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getPendingReplicationBlocks());
        banner("Making sure the file is still readable");
        FileSystem fs2 = cluster.getFileSystem(1);
        DFSTestUtil.readFile(fs2, TestDNFencing.TEST_FILE_PATH);
        banner("Waiting for the actual block files to get deleted from DNs.");
        waitForTrueReplication(cluster, block, 1);
    }

    /**
     * Test case which restarts the standby node in such a way that,
     * when it exits safemode, it will want to invalidate a bunch
     * of over-replicated block replicas. Ensures that if we failover
     * at this point it won't lose data.
     */
    @Test
    public void testNNClearsCommandsOnFailoverAfterStartup() throws Exception {
        // Make lots of blocks to increase chances of triggering a bug.
        DFSTestUtil.createFile(fs, TestDNFencing.TEST_FILE_PATH, (30 * (TestDNFencing.SMALL_BLOCK)), ((short) (3)), 1L);
        banner("Shutting down NN2");
        cluster.shutdownNameNode(1);
        banner("Setting replication to 1, rolling edit log.");
        nn1.getRpcServer().setReplication(TestDNFencing.TEST_FILE, ((short) (1)));
        nn1.getRpcServer().rollEditLog();
        // Start NN2 again. When it starts up, it will see all of the
        // blocks as over-replicated, since it has the metadata for
        // replication=1, but the DNs haven't yet processed the deletions.
        banner("Starting NN2 again.");
        cluster.restartNameNode(1);
        nn2 = cluster.getNameNode(1);
        banner("triggering BRs");
        cluster.triggerBlockReports();
        // We expect that both NN1 and NN2 will have some number of
        // deletions queued up for the DNs.
        banner("computing invalidation on nn1");
        BlockManagerTestUtil.computeInvalidationWork(nn1.getNamesystem().getBlockManager());
        banner("computing invalidation on nn2");
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        // Dump some info for debugging purposes.
        banner("Metadata immediately before failover");
        doMetasave(nn2);
        // Transition nn2 to active even though nn1 still thinks it's active
        banner("Failing to NN2 but let NN1 continue to think it's active");
        NameNodeAdapter.abortEditLogs(nn1);
        NameNodeAdapter.enterSafeMode(nn1, false);
        cluster.transitionToActive(1);
        // Check that the standby picked up the replication change.
        Assert.assertEquals(1, nn2.getRpcServer().getFileInfo(TestDNFencing.TEST_FILE).getReplication());
        // Dump some info for debugging purposes.
        banner("Metadata immediately after failover");
        doMetasave(nn2);
        banner("Triggering heartbeats and block reports so that fencing is completed");
        cluster.triggerHeartbeats();
        cluster.triggerBlockReports();
        banner("Metadata after nodes have all block-reported");
        doMetasave(nn2);
        // Force a rescan of postponedMisreplicatedBlocks.
        BlockManager nn2BM = nn2.getNamesystem().getBlockManager();
        BlockManagerTestUtil.checkHeartbeat(nn2BM);
        BlockManagerTestUtil.rescanPostponedMisreplicatedBlocks(nn2BM);
        // The block should no longer be postponed.
        Assert.assertEquals(0, nn2.getNamesystem().getPostponedMisreplicatedBlocks());
        // Wait for NN2 to enact its deletions (redundancy monitor has to run, etc)
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        HATestUtil.waitForNNToIssueDeletions(nn2);
        cluster.triggerHeartbeats();
        HATestUtil.waitForDNDeletions(cluster);
        cluster.triggerDeletionReports();
        Assert.assertEquals(0, nn2.getNamesystem().getUnderReplicatedBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getPendingReplicationBlocks());
        banner("Making sure the file is still readable");
        FileSystem fs2 = cluster.getFileSystem(1);
        DFSTestUtil.readFile(fs2, TestDNFencing.TEST_FILE_PATH);
    }

    /**
     * Test case that reduces replication of a file with a lot of blocks
     * and then fails over right after those blocks enter the DN invalidation
     * queues on the active. Ensures that fencing is correct and no replicas
     * are lost.
     */
    @Test
    public void testNNClearsCommandsOnFailoverWithReplChanges() throws Exception {
        // Make lots of blocks to increase chances of triggering a bug.
        DFSTestUtil.createFile(fs, TestDNFencing.TEST_FILE_PATH, (30 * (TestDNFencing.SMALL_BLOCK)), ((short) (1)), 1L);
        banner("rolling NN1's edit log, forcing catch-up");
        HATestUtil.waitForStandbyToCatchUp(nn1, nn2);
        // Get some new replicas reported so that NN2 now considers
        // them over-replicated and schedules some more deletions
        nn1.getRpcServer().setReplication(TestDNFencing.TEST_FILE, ((short) (2)));
        while ((BlockManagerTestUtil.getComputedDatanodeWork(nn1.getNamesystem().getBlockManager())) > 0) {
            TestDNFencing.LOG.info("Getting more replication work computed");
        } 
        BlockManager bm1 = nn1.getNamesystem().getBlockManager();
        while ((bm1.getPendingReconstructionBlocksCount()) > 0) {
            BlockManagerTestUtil.updateState(bm1);
            cluster.triggerHeartbeats();
            Thread.sleep(1000);
        } 
        banner("triggering BRs");
        cluster.triggerBlockReports();
        nn1.getRpcServer().setReplication(TestDNFencing.TEST_FILE, ((short) (1)));
        banner("computing invalidation on nn1");
        BlockManagerTestUtil.computeInvalidationWork(nn1.getNamesystem().getBlockManager());
        doMetasave(nn1);
        banner("computing invalidation on nn2");
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        doMetasave(nn2);
        // Dump some info for debugging purposes.
        banner("Metadata immediately before failover");
        doMetasave(nn2);
        // Transition nn2 to active even though nn1 still thinks it's active
        banner("Failing to NN2 but let NN1 continue to think it's active");
        NameNodeAdapter.abortEditLogs(nn1);
        NameNodeAdapter.enterSafeMode(nn1, false);
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        cluster.transitionToActive(1);
        // Check that the standby picked up the replication change.
        Assert.assertEquals(1, nn2.getRpcServer().getFileInfo(TestDNFencing.TEST_FILE).getReplication());
        // Dump some info for debugging purposes.
        banner("Metadata immediately after failover");
        doMetasave(nn2);
        banner("Triggering heartbeats and block reports so that fencing is completed");
        cluster.triggerHeartbeats();
        cluster.triggerBlockReports();
        banner("Metadata after nodes have all block-reported");
        doMetasave(nn2);
        // Force a rescan of postponedMisreplicatedBlocks.
        BlockManager nn2BM = nn2.getNamesystem().getBlockManager();
        BlockManagerTestUtil.checkHeartbeat(nn2BM);
        BlockManagerTestUtil.rescanPostponedMisreplicatedBlocks(nn2BM);
        // The block should no longer be postponed.
        Assert.assertEquals(0, nn2.getNamesystem().getPostponedMisreplicatedBlocks());
        // Wait for NN2 to enact its deletions (redundancy monitor has to run, etc)
        BlockManagerTestUtil.computeInvalidationWork(nn2.getNamesystem().getBlockManager());
        HATestUtil.waitForNNToIssueDeletions(nn2);
        cluster.triggerHeartbeats();
        HATestUtil.waitForDNDeletions(cluster);
        cluster.triggerDeletionReports();
        Assert.assertEquals(0, nn2.getNamesystem().getUnderReplicatedBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getPendingReplicationBlocks());
        banner("Making sure the file is still readable");
        FileSystem fs2 = cluster.getFileSystem(1);
        DFSTestUtil.readFile(fs2, TestDNFencing.TEST_FILE_PATH);
    }

    /**
     * Regression test for HDFS-2742. The issue in this bug was:
     * - DN does a block report while file is open. This BR contains
     *   the block in RBW state.
     * - Standby queues the RBW state in PendingDatanodeMessages
     * - Standby processes edit logs during failover. Before fixing
     *   this bug, it was mistakenly applying the RBW reported state
     *   after the block had been completed, causing the block to get
     *   marked corrupt. Instead, we should now be applying the RBW
     *   message on OP_ADD, and then the FINALIZED message on OP_CLOSE.
     */
    @Test
    public void testBlockReportsWhileFileBeingWritten() throws Exception {
        FSDataOutputStream out = fs.create(TestDNFencing.TEST_FILE_PATH);
        try {
            AppendTestUtil.write(out, 0, 10);
            out.hflush();
            // Block report will include the RBW replica, but will be
            // queued on the StandbyNode.
            cluster.triggerBlockReports();
        } finally {
            IOUtils.closeStream(out);
        }
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        // Verify that no replicas are marked corrupt, and that the
        // file is readable from the failed-over standby.
        BlockManagerTestUtil.updateState(nn1.getNamesystem().getBlockManager());
        BlockManagerTestUtil.updateState(nn2.getNamesystem().getBlockManager());
        Assert.assertEquals(0, nn1.getNamesystem().getCorruptReplicaBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getCorruptReplicaBlocks());
        DFSTestUtil.readFile(fs, TestDNFencing.TEST_FILE_PATH);
    }

    /**
     * Test that, when a block is re-opened for append, the related
     * datanode messages are correctly queued by the SBN because
     * they have future states and genstamps.
     */
    @Test
    public void testQueueingWithAppend() throws Exception {
        int numQueued = 0;
        int numDN = cluster.getDataNodes().size();
        // case 1: create file and call hflush after write
        FSDataOutputStream out = fs.create(TestDNFencing.TEST_FILE_PATH);
        try {
            AppendTestUtil.write(out, 0, 10);
            out.hflush();
            // Opening the file will report RBW replicas, but will be
            // queued on the StandbyNode.
            // However, the delivery of RBW messages is delayed by HDFS-7217 fix.
            // Apply cluster.triggerBlockReports() to trigger the reporting sooner.
            // 
            cluster.triggerBlockReports();
            numQueued += numDN;// RBW messages

            // The cluster.triggerBlockReports() call above does a full
            // block report that incurs 3 extra RBW messages
            numQueued += numDN;// RBW messages

        } finally {
            IOUtils.closeStream(out);
            numQueued += numDN;// blockReceived messages

        }
        cluster.triggerBlockReports();
        numQueued += numDN;
        Assert.assertEquals(numQueued, cluster.getNameNode(1).getNamesystem().getPendingDataNodeMessageCount());
        // case 2: append to file and call hflush after write
        try {
            out = fs.append(TestDNFencing.TEST_FILE_PATH);
            AppendTestUtil.write(out, 10, 10);
            out.hflush();
            cluster.triggerBlockReports();
            numQueued += numDN * 2;// RBW messages, see comments in case 1

        } finally {
            IOUtils.closeStream(out);
            cluster.triggerHeartbeats();
            numQueued += numDN;// blockReceived

        }
        Assert.assertEquals(numQueued, cluster.getNameNode(1).getNamesystem().getPendingDataNodeMessageCount());
        // case 3: similar to case 2, except no hflush is called.
        try {
            out = fs.append(TestDNFencing.TEST_FILE_PATH);
            AppendTestUtil.write(out, 20, 10);
        } finally {
            // The write operation in the try block is buffered, thus no RBW message
            // is reported yet until the closeStream call here. When closeStream is
            // called, before HDFS-7217 fix, there would be three RBW messages
            // (blockReceiving), plus three FINALIZED messages (blockReceived)
            // delivered to NN. However, because of HDFS-7217 fix, the reporting of
            // RBW  messages is postponed. In this case, they are even overwritten
            // by the blockReceived messages of the same block when they are waiting
            // to be delivered. All this happens within the closeStream() call.
            // What's delivered to NN is the three blockReceived messages. See
            // BPServiceActor#addPendingReplicationBlockInfo
            // 
            IOUtils.closeStream(out);
            numQueued += numDN;// blockReceived

        }
        cluster.triggerBlockReports();
        numQueued += numDN;
        TestDNFencing.LOG.info(((("Expect " + numQueued) + " and got: ") + (cluster.getNameNode(1).getNamesystem().getPendingDataNodeMessageCount())));
        Assert.assertEquals(numQueued, cluster.getNameNode(1).getNamesystem().getPendingDataNodeMessageCount());
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        // Verify that no replicas are marked corrupt, and that the
        // file is readable from the failed-over standby.
        BlockManagerTestUtil.updateState(nn1.getNamesystem().getBlockManager());
        BlockManagerTestUtil.updateState(nn2.getNamesystem().getBlockManager());
        Assert.assertEquals(0, nn1.getNamesystem().getCorruptReplicaBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getCorruptReplicaBlocks());
        AppendTestUtil.check(fs, TestDNFencing.TEST_FILE_PATH, 30);
    }

    /**
     * Another regression test for HDFS-2742. This tests the following sequence:
     * - DN does a block report while file is open. This BR contains
     *   the block in RBW state.
     * - The block report is delayed in reaching the standby.
     * - The file is closed.
     * - The standby processes the OP_ADD and OP_CLOSE operations before
     *   the RBW block report arrives.
     * - The standby should not mark the block as corrupt.
     */
    @Test
    public void testRBWReportArrivesAfterEdits() throws Exception {
        final CountDownLatch brFinished = new CountDownLatch(1);
        DelayAnswer delayer = new GenericTestUtils.DelayAnswer(TestDNFencing.LOG) {
            @Override
            protected Object passThrough(InvocationOnMock invocation) throws Throwable {
                try {
                    return super.passThrough(invocation);
                } finally {
                    // inform the test that our block report went through.
                    brFinished.countDown();
                }
            }
        };
        FSDataOutputStream out = fs.create(TestDNFencing.TEST_FILE_PATH);
        try {
            AppendTestUtil.write(out, 0, 10);
            out.hflush();
            DataNode dn = cluster.getDataNodes().get(0);
            DatanodeProtocolClientSideTranslatorPB spy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn2);
            Mockito.doAnswer(delayer).when(spy).blockReport(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
            dn.scheduleAllBlockReport(0);
            delayer.waitForCall();
        } finally {
            IOUtils.closeStream(out);
        }
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        delayer.proceed();
        brFinished.await();
        // Verify that no replicas are marked corrupt, and that the
        // file is readable from the failed-over standby.
        BlockManagerTestUtil.updateState(nn1.getNamesystem().getBlockManager());
        BlockManagerTestUtil.updateState(nn2.getNamesystem().getBlockManager());
        Assert.assertEquals(0, nn1.getNamesystem().getCorruptReplicaBlocks());
        Assert.assertEquals(0, nn2.getNamesystem().getCorruptReplicaBlocks());
        DFSTestUtil.readFile(fs, TestDNFencing.TEST_FILE_PATH);
    }

    /**
     * A BlockPlacementPolicy which, rather than using space available, makes
     * random decisions about which excess replica to delete. This is because,
     * in the test cases, the two NNs will usually (but not quite always)
     * make the same decision of which replica to delete. The fencing issues
     * are exacerbated when the two NNs make different decisions, which can
     * happen in "real life" when they have slightly out-of-sync heartbeat
     * information regarding disk usage.
     */
    public static class RandomDeleterPolicy extends BlockPlacementPolicyDefault {
        public RandomDeleterPolicy() {
            super();
        }

        @Override
        public DatanodeStorageInfo chooseReplicaToDelete(Collection<DatanodeStorageInfo> moreThanOne, Collection<DatanodeStorageInfo> exactlyOne, List<StorageType> excessTypes, Map<String, List<DatanodeStorageInfo>> rackMap) {
            Collection<DatanodeStorageInfo> chooseFrom = (!(moreThanOne.isEmpty())) ? moreThanOne : exactlyOne;
            List<DatanodeStorageInfo> l = Lists.newArrayList(chooseFrom);
            return l.get(ThreadLocalRandom.current().nextInt(l.size()));
        }
    }
}

