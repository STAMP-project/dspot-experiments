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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY;
import DFSConfigKeys.DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.FsDatasetTestUtils;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.DelayAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the replication of a DFS file.
 */
public class TestReplication {
    private static final long seed = 3735928559L;

    private static final int blockSize = 8192;

    private static final int fileSize = 16384;

    private static final String[] racks = new String[]{ "/d1/r1", "/d1/r1", "/d1/r2", "/d1/r2", "/d1/r2", "/d2/r3", "/d2/r3" };

    private static final int numDatanodes = TestReplication.racks.length;

    private static final Logger LOG = LoggerFactory.getLogger(TestReplication.class);

    /* Test if Datanode reports bad blocks during replication request */
    @Test
    public void testBadBlockReportOnTransfer() throws Exception {
        testBadBlockReportOnTransfer(false);
    }

    /* Test if Datanode reports bad blocks during replication request
    with missing block file
     */
    @Test
    public void testBadBlockReportOnTransferMissingBlockFile() throws Exception {
        testBadBlockReportOnTransfer(true);
    }

    @Test
    public void testReplicationSimulatedStorag() throws IOException {
        runReplication(true);
    }

    @Test
    public void testReplication() throws IOException {
        runReplication(false);
    }

    /* This test makes sure that NameNode retries all the available blocks 
    for under replicated blocks. 

    It creates a file with one block and replication of 4. It corrupts 
    two of the blocks and removes one of the replicas. Expected behavior is
    that missing replica will be copied from one valid source.
     */
    @Test
    public void testPendingReplicationRetry() throws IOException {
        MiniDFSCluster cluster = null;
        int numDataNodes = 4;
        String testFile = "/replication-test-file";
        Path testPath = new Path(testFile);
        byte[] buffer = new byte[1024];
        for (int i = 0; i < (buffer.length); i++) {
            buffer[i] = '1';
        }
        try {
            Configuration conf = new HdfsConfiguration();
            conf.set(DFS_REPLICATION_KEY, Integer.toString(numDataNodes));
            // first time format
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
            OutputStream out = cluster.getFileSystem().create(testPath);
            out.write(buffer);
            out.close();
            waitForBlockReplication(testFile, dfsClient.getNamenode(), numDataNodes, (-1));
            // get first block of the file.
            ExtendedBlock block = dfsClient.getNamenode().getBlockLocations(testFile, 0, Long.MAX_VALUE).get(0).getBlock();
            List<FsDatasetTestUtils.MaterializedReplica> replicas = new ArrayList<>();
            for (int dnIndex = 0; dnIndex < 3; dnIndex++) {
                replicas.add(cluster.getMaterializedReplica(dnIndex, block));
            }
            Assert.assertEquals(3, replicas.size());
            cluster.shutdown();
            int fileCount = 0;
            // Choose 3 copies of block file - delete 1 and corrupt the remaining 2
            for (FsDatasetTestUtils.MaterializedReplica replica : replicas) {
                if (fileCount == 0) {
                    TestReplication.LOG.info(("Deleting block " + replica));
                    replica.deleteData();
                } else {
                    // corrupt it.
                    TestReplication.LOG.info(("Corrupting file " + replica));
                    replica.corruptData();
                }
                fileCount++;
            }
            /* Start the MiniDFSCluster with more datanodes since once a writeBlock
            to a datanode node fails, same block can not be written to it
            immediately. In our case some replication attempts will fail.
             */
            TestReplication.LOG.info("Restarting minicluster after deleting a replica and corrupting 2 crcs");
            conf = new HdfsConfiguration();
            conf.set(DFS_REPLICATION_KEY, Integer.toString(numDataNodes));
            conf.set(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, Integer.toString(2));
            conf.set("dfs.datanode.block.write.timeout.sec", Integer.toString(5));
            conf.set(DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY, "0.75f");// only 3 copies exist

            cluster = new MiniDFSCluster.Builder(conf).numDataNodes((numDataNodes * 2)).format(false).build();
            cluster.waitActive();
            dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
            waitForBlockReplication(testFile, dfsClient.getNamenode(), numDataNodes, (-1));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test if replication can detect mismatched length on-disk blocks
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReplicateLenMismatchedBlock() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration()).numDataNodes(2).build();
        try {
            cluster.waitActive();
            // test truncated block
            changeBlockLen(cluster, (-1));
            // test extended block
            changeBlockLen(cluster, 1);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test that blocks should get replicated if we have corrupted blocks and
     * having good replicas at least equal or greater to minreplication
     *
     * Simulate rbw blocks by creating dummy copies, then a DN restart to detect
     * those corrupted blocks asap.
     */
    @Test(timeout = 30000)
    public void testReplicationWhenBlockCorruption() throws Exception {
        MiniDFSCluster cluster = null;
        try {
            Configuration conf = new HdfsConfiguration();
            conf.setLong(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, 1);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storagesPerDatanode(1).build();
            FileSystem fs = cluster.getFileSystem();
            Path filePath = new Path("/test");
            FSDataOutputStream create = fs.create(filePath);
            fs.setReplication(filePath, ((short) (1)));
            create.write(new byte[1024]);
            create.close();
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, filePath);
            int numReplicaCreated = 0;
            for (final DataNode dn : cluster.getDataNodes()) {
                if (!(dn.getFSDataset().contains(block))) {
                    cluster.getFsDatasetTestUtils(dn).injectCorruptReplica(block);
                    numReplicaCreated++;
                }
            }
            Assert.assertEquals(2, numReplicaCreated);
            fs.setReplication(filePath, ((short) (3)));
            cluster.restartDataNodes();// Lets detect all DNs about dummy copied

            // blocks
            cluster.waitActive();
            cluster.triggerBlockReports();
            DFSTestUtil.waitReplication(fs, filePath, ((short) (3)));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * This test makes sure that, when a file is closed before all
     * of the datanodes in the pipeline have reported their replicas,
     * the NameNode doesn't consider the block under-replicated too
     * aggressively. It is a regression test for HDFS-1172.
     */
    @Test(timeout = 60000)
    public void testNoExtraReplicationWhenBlockReceivedIsLate() throws Exception {
        TestReplication.LOG.info("Test block replication when blockReceived is late");
        final short numDataNodes = 3;
        final short replication = 3;
        final Configuration conf = new Configuration();
        conf.setInt(DFS_BLOCK_SIZE_KEY, 1024);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
        final String testFile = "/replication-test-file";
        final Path testPath = new Path(testFile);
        final BlockManager bm = cluster.getNameNode().getNamesystem().getBlockManager();
        try {
            cluster.waitActive();
            // Artificially delay IBR from 1 DataNode.
            // this ensures that the client's completeFile() RPC will get to the
            // NN before some of the replicas are reported.
            NameNode nn = cluster.getNameNode();
            DataNode dn = cluster.getDataNodes().get(0);
            DatanodeProtocolClientSideTranslatorPB spy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn);
            DelayAnswer delayer = new GenericTestUtils.DelayAnswer(TestReplication.LOG);
            Mockito.doAnswer(delayer).when(spy).blockReceivedAndDeleted(ArgumentMatchers.any(), Mockito.anyString(), ArgumentMatchers.any());
            FileSystem fs = cluster.getFileSystem();
            // Create and close a small file with two blocks
            DFSTestUtil.createFile(fs, testPath, 1500, replication, 0);
            // schedule replication via BlockManager#computeReplicationWork
            BlockManagerTestUtil.computeAllPendingWork(bm);
            // Initially, should have some pending replication since the close()
            // is earlier than at lease one of the reportReceivedDeletedBlocks calls
            Assert.assertTrue(((pendingReplicationCount(bm)) > 0));
            // release pending IBR.
            delayer.waitForCall();
            delayer.proceed();
            delayer.waitForResult();
            // make sure DataNodes do replication work if exists
            for (DataNode d : cluster.getDataNodes()) {
                DataNodeTestUtils.triggerHeartbeat(d);
            }
            // Wait until there is nothing pending
            try {
                GenericTestUtils.waitFor(new Supplier<Boolean>() {
                    @Override
                    public Boolean get() {
                        return (pendingReplicationCount(bm)) == 0;
                    }
                }, 100, 3000);
            } catch (TimeoutException e) {
                Assert.fail("timed out while waiting for no pending replication.");
            }
            // Check that none of the datanodes have serviced a replication request.
            // i.e. that the NameNode didn't schedule any spurious replication.
            assertNoReplicationWasPerformed(cluster);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * This test makes sure that, if a file is under construction, blocks
     * in the middle of that file are properly re-replicated if they
     * become corrupt.
     */
    @Test(timeout = 60000)
    public void testReplicationWhileUnderConstruction() throws Exception {
        TestReplication.LOG.info("Test block replication in under construction");
        MiniDFSCluster cluster = null;
        final short numDataNodes = 6;
        final short replication = 3;
        String testFile = "/replication-test-file";
        Path testPath = new Path(testFile);
        FSDataOutputStream stm = null;
        try {
            Configuration conf = new Configuration();
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            stm = AppendTestUtil.createFile(fs, testPath, replication);
            // Write a full block
            byte[] buffer = AppendTestUtil.initBuffer(AppendTestUtil.BLOCK_SIZE);
            stm.write(buffer);// block 1

            stm.write(buffer);// block 2

            stm.write(buffer, 0, 1);// start block 3

            stm.hflush();// make sure blocks are persisted, etc

            // Everything should be fully replicated
            waitForBlockReplication(testFile, cluster.getNameNodeRpc(), replication, 30000, true, true);
            // Check that none of the datanodes have serviced a replication request.
            // i.e. that the NameNode didn't schedule any spurious replication.
            assertNoReplicationWasPerformed(cluster);
            // Mark one the blocks corrupt
            List<LocatedBlock> blocks;
            FSDataInputStream in = fs.open(testPath);
            try {
                blocks = DFSTestUtil.getAllBlocks(in);
            } finally {
                in.close();
            }
            LocatedBlock lb = blocks.get(0);
            LocatedBlock lbOneReplica = new LocatedBlock(lb.getBlock(), new DatanodeInfo[]{ lb.getLocations()[0] });
            cluster.getNameNodeRpc().reportBadBlocks(new LocatedBlock[]{ lbOneReplica });
            // Everything should be fully replicated
            waitForBlockReplication(testFile, cluster.getNameNodeRpc(), replication, 30000, true, true);
        } finally {
            if (stm != null) {
                IOUtils.closeStream(stm);
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

