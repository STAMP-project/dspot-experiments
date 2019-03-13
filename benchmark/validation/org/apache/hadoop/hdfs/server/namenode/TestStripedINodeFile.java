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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import HdfsConstants.ONESSD_STORAGE_POLICY_NAME;
import HdfsServerConstants.BlockUCState.UNDER_CONSTRUCTION;
import StorageType.DISK;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.protocol.BlockType;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoStriped;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockStoragePolicySuite;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests INodeFile with striped feature.
 */
public class TestStripedINodeFile {
    public static final Logger LOG = LoggerFactory.getLogger(TestINodeFile.class);

    private static final PermissionStatus perm = new PermissionStatus("userName", null, FsPermission.getDefault());

    private final BlockStoragePolicySuite defaultSuite = BlockStoragePolicySuite.createDefaultSuite();

    private final BlockStoragePolicy defaultPolicy = defaultSuite.getDefaultPolicy();

    // use hard coded policy - see HDFS-9816
    private static final ErasureCodingPolicy testECPolicy = StripedFileTestUtil.getDefaultECPolicy();

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidECPolicy() throws IllegalArgumentException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Could not find EC policy with ID 0xbb");
        new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, null, ((byte) (187)), 1024L, HdfsConstants.COLD_STORAGE_POLICY_ID, BlockType.STRIPED);
    }

    @Test
    public void testBlockStripedFeature() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Assert.assertTrue(inf.isStriped());
    }

    @Test
    public void testBlockStripedTotalBlockCount() {
        Block blk = new Block(1);
        BlockInfoStriped blockInfoStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        Assert.assertEquals(9, blockInfoStriped.getTotalBlockNum());
    }

    @Test
    public void testStripedLayoutRedundancy() {
        INodeFile inodeFile;
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, new Short(((short) (3))), StripedFileTestUtil.getDefaultECPolicy().getId(), 1024L, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.STRIPED);
            Assert.fail(("INodeFile construction should fail when both replication and " + "ECPolicy requested!"));
        } catch (IllegalArgumentException iae) {
            TestStripedINodeFile.LOG.info("Expected exception: ", iae);
        }
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, null, null, 1024L, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.STRIPED);
            Assert.fail(("INodeFile construction should fail when EC Policy param not " + "provided for striped layout!"));
        } catch (IllegalArgumentException iae) {
            TestStripedINodeFile.LOG.info("Expected exception: ", iae);
        }
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, null, Byte.MAX_VALUE, 1024L, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.STRIPED);
            Assert.fail(("INodeFile construction should fail when EC Policy is " + "not in the supported list!"));
        } catch (IllegalArgumentException iae) {
            TestStripedINodeFile.LOG.info("Expected exception: ", iae);
        }
        final Byte ecPolicyID = StripedFileTestUtil.getDefaultECPolicy().getId();
        try {
            /* replication */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, null, ecPolicyID, 1024L, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.CONTIGUOUS);
            Assert.fail(("INodeFile construction should fail when replication param is " + "provided for striped layout!"));
        } catch (IllegalArgumentException iae) {
            TestStripedINodeFile.LOG.info("Expected exception: ", iae);
        }
        inodeFile = /* replication */
        new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestStripedINodeFile.perm, 0L, 0L, null, null, ecPolicyID, 1024L, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.STRIPED);
        Assert.assertTrue(inodeFile.isStriped());
        Assert.assertEquals(ecPolicyID.byteValue(), inodeFile.getErasureCodingPolicyID());
    }

    @Test
    public void testBlockStripedLength() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped blockInfoStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        inf.addBlock(blockInfoStriped);
        Assert.assertEquals(1, inf.getBlocks().length);
    }

    @Test
    public void testBlockStripedConsumedSpace() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped blockInfoStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        blockInfoStriped.setNumBytes(1);
        inf.addBlock(blockInfoStriped);
        // 0. Calculate the total bytes per stripes <Num Bytes per Stripes>
        // 1. Calculate the number of stripes in this block group. <Num Stripes>
        // 2. Calculate the last remaining length which does not make a stripe. <Last Stripe Length>
        // 3. Total consumed space is the total of
        // a. The total of the full cells of data blocks and parity blocks.
        // b. The remaining of data block which does not make a stripe.
        // c. The last parity block cells. These size should be same
        // to the first cell in this stripe.
        // So the total consumed space is the sum of
        // a. <Cell Size> * (<Num Stripes> - 1) * <Total Block Num> = 0
        // b. <Num Bytes> % <Num Bytes per Stripes> = 1
        // c. <Last Stripe Length> * <Parity Block Num> = 1 * 3
        Assert.assertEquals(4, inf.storagespaceConsumedStriped().getStorageSpace());
        Assert.assertEquals(4, inf.storagespaceConsumed(defaultPolicy).getStorageSpace());
    }

    @Test
    public void testMultipleBlockStripedConsumedSpace() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk1 = new Block(1);
        BlockInfoStriped blockInfoStriped1 = new BlockInfoStriped(blk1, TestStripedINodeFile.testECPolicy);
        blockInfoStriped1.setNumBytes(1);
        Block blk2 = new Block(2);
        BlockInfoStriped blockInfoStriped2 = new BlockInfoStriped(blk2, TestStripedINodeFile.testECPolicy);
        blockInfoStriped2.setNumBytes(1);
        inf.addBlock(blockInfoStriped1);
        inf.addBlock(blockInfoStriped2);
        // This is the double size of one block in above case.
        Assert.assertEquals((4 * 2), inf.storagespaceConsumedStriped().getStorageSpace());
        Assert.assertEquals((4 * 2), inf.storagespaceConsumed(defaultPolicy).getStorageSpace());
    }

    @Test
    public void testBlockStripedFileSize() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped blockInfoStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        blockInfoStriped.setNumBytes(100);
        inf.addBlock(blockInfoStriped);
        // Compute file size should return actual data
        // size which is retained by this file.
        Assert.assertEquals(100, inf.computeFileSize());
        Assert.assertEquals(100, inf.computeFileSize(false, false));
    }

    @Test
    public void testBlockStripedUCFileSize() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped bInfoUCStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        bInfoUCStriped.convertToBlockUnderConstruction(UNDER_CONSTRUCTION, null);
        bInfoUCStriped.setNumBytes(100);
        inf.addBlock(bInfoUCStriped);
        Assert.assertEquals(100, inf.computeFileSize());
        Assert.assertEquals(0, inf.computeFileSize(false, false));
    }

    @Test
    public void testBlockStripedComputeQuotaUsage() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped blockInfoStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        blockInfoStriped.setNumBytes(100);
        inf.addBlock(blockInfoStriped);
        QuotaCounts counts = inf.computeQuotaUsageWithStriped(defaultPolicy, new QuotaCounts.Builder().build());
        Assert.assertEquals(1, counts.getNameSpace());
        // The total consumed space is the sum of
        // a. <Cell Size> * (<Num Stripes> - 1) * <Total Block Num> = 0
        // b. <Num Bytes> % <Num Bytes per Stripes> = 100
        // c. <Last Stripe Length> * <Parity Block Num> = 100 * 3
        Assert.assertEquals(400, counts.getStorageSpace());
    }

    @Test
    public void testBlockStripedUCComputeQuotaUsage() throws IOException, InterruptedException {
        INodeFile inf = TestStripedINodeFile.createStripedINodeFile();
        Block blk = new Block(1);
        BlockInfoStriped bInfoUCStriped = new BlockInfoStriped(blk, TestStripedINodeFile.testECPolicy);
        bInfoUCStriped.convertToBlockUnderConstruction(UNDER_CONSTRUCTION, null);
        bInfoUCStriped.setNumBytes(100);
        inf.addBlock(bInfoUCStriped);
        QuotaCounts counts = inf.computeQuotaUsageWithStriped(defaultPolicy, new QuotaCounts.Builder().build());
        Assert.assertEquals(1024, inf.getPreferredBlockSize());
        Assert.assertEquals(1, counts.getNameSpace());
        // Consumed space in the case of BlockInfoStripedUC can be calculated
        // by using preferred block size. This is 1024 and total block num
        // is 9(= 3 + 6). Consumed storage space should be 1024 * 9 = 9216.
        Assert.assertEquals(9216, counts.getStorageSpace());
    }

    /**
     * Test the behavior of striped and contiguous block deletions.
     */
    @Test(timeout = 60000)
    public void testDeleteOp() throws Exception {
        MiniDFSCluster cluster = null;
        try {
            final int len = 1024;
            final Path parentDir = new Path("/parentDir");
            final Path ecDir = new Path(parentDir, "ecDir");
            final Path ecFile = new Path(ecDir, "ecFile");
            final Path contiguousFile = new Path(parentDir, "someFile");
            final DistributedFileSystem dfs;
            final Configuration conf = new Configuration();
            final short GROUP_SIZE = ((short) ((TestStripedINodeFile.testECPolicy.getNumDataUnits()) + (TestStripedINodeFile.testECPolicy.getNumParityUnits())));
            conf.setInt(DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY, 2);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(GROUP_SIZE).build();
            cluster.waitActive();
            FSNamesystem fsn = cluster.getNamesystem();
            dfs = cluster.getFileSystem();
            dfs.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
            dfs.mkdirs(ecDir);
            // set erasure coding policy
            dfs.setErasureCodingPolicy(ecDir, StripedFileTestUtil.getDefaultECPolicy().getName());
            DFSTestUtil.createFile(dfs, ecFile, len, ((short) (1)), 65261);
            DFSTestUtil.createFile(dfs, contiguousFile, len, ((short) (1)), 65261);
            final FSDirectory fsd = fsn.getFSDirectory();
            // Case-1: Verify the behavior of striped blocks
            // Get blocks of striped file
            INode inodeStriped = fsd.getINode("/parentDir/ecDir/ecFile");
            Assert.assertTrue("Failed to get INodeFile for /parentDir/ecDir/ecFile", (inodeStriped instanceof INodeFile));
            INodeFile inodeStripedFile = ((INodeFile) (inodeStriped));
            BlockInfo[] stripedBlks = inodeStripedFile.getBlocks();
            for (BlockInfo blockInfo : stripedBlks) {
                Assert.assertFalse("Mistakenly marked the block as deleted!", blockInfo.isDeleted());
            }
            // delete directory with erasure coding policy
            dfs.delete(ecDir, true);
            for (BlockInfo blockInfo : stripedBlks) {
                Assert.assertTrue("Didn't mark the block as deleted!", blockInfo.isDeleted());
            }
            // Case-2: Verify the behavior of contiguous blocks
            // Get blocks of contiguous file
            INode inode = fsd.getINode("/parentDir/someFile");
            Assert.assertTrue("Failed to get INodeFile for /parentDir/someFile", (inode instanceof INodeFile));
            INodeFile inodeFile = ((INodeFile) (inode));
            BlockInfo[] contiguousBlks = inodeFile.getBlocks();
            for (BlockInfo blockInfo : contiguousBlks) {
                Assert.assertFalse("Mistakenly marked the block as deleted!", blockInfo.isDeleted());
            }
            // delete parent directory
            dfs.delete(parentDir, true);
            for (BlockInfo blockInfo : contiguousBlks) {
                Assert.assertTrue("Didn't mark the block as deleted!", blockInfo.isDeleted());
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Tests when choosing blocks on file creation of EC striped mode should
     * ignore storage policy if that is not suitable. Supported storage policies
     * for EC Striped mode are HOT, COLD and ALL_SSD. For all other policies set
     * will be ignored and considered default policy.
     */
    @Test(timeout = 60000)
    public void testUnsuitableStoragePoliciesWithECStripedMode() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        int defaultStripedBlockSize = (TestStripedINodeFile.testECPolicy.getCellSize()) * 4;
        conf.setLong(DFS_BLOCK_SIZE_KEY, defaultStripedBlockSize);
        conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        conf.setLong(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1L);
        conf.setBoolean(DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY, false);
        // start 10 datanodes
        int numOfDatanodes = 10;
        int storagesPerDatanode = 2;
        long capacity = 10 * defaultStripedBlockSize;
        long[][] capacities = new long[numOfDatanodes][storagesPerDatanode];
        for (int i = 0; i < numOfDatanodes; i++) {
            for (int j = 0; j < storagesPerDatanode; j++) {
                capacities[i][j] = capacity;
            }
        }
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numOfDatanodes).storagesPerDatanode(storagesPerDatanode).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD } }).storageCapacities(capacities).build();
        try {
            cluster.waitActive();
            cluster.getFileSystem().enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
            // set "/foo" directory with ONE_SSD storage policy.
            ClientProtocol client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            String fooDir = "/foo";
            client.mkdirs(fooDir, new FsPermission(((short) (777))), true);
            client.setStoragePolicy(fooDir, ONESSD_STORAGE_POLICY_NAME);
            // set an EC policy on "/foo" directory
            client.setErasureCodingPolicy(fooDir, StripedFileTestUtil.getDefaultECPolicy().getName());
            // write file to fooDir
            final String barFile = "/foo/bar";
            long fileLen = 20 * defaultStripedBlockSize;
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path(barFile), fileLen, ((short) (3)), 0);
            // verify storage types and locations
            LocatedBlocks locatedBlocks = client.getBlockLocations(barFile, 0, fileLen);
            for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                for (StorageType type : lb.getStorageTypes()) {
                    Assert.assertEquals(DISK, type);
                }
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

