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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import HdfsConstants.LEASE_HARDLIMIT_PERIOD;
import HdfsConstants.LEASE_SOFTLIMIT_PERIOD;
import INode.LOG;
import SyncFlag.UPDATE_LENGTH;
import java.util.EnumSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClientAdapter;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.INodeDirectory;
import org.apache.hadoop.hdfs.server.namenode.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.hdfs.server.namenode.snapshot.DirectoryWithSnapshotFeature.DirectoryDiff;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * Test snapshot functionalities while file appending.
 */
public class TestINodeFileUnderConstructionWithSnapshot {
    {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
        SnapshotTestHelper.disableLogs();
    }

    static final long seed = 0;

    static final short REPLICATION = 3;

    static final int BLOCKSIZE = 1024;

    private final Path dir = new Path("/TestSnapshot");

    Configuration conf;

    MiniDFSCluster cluster;

    FSNamesystem fsn;

    DistributedFileSystem hdfs;

    FSDirectory fsdir;

    /**
     * Test snapshot after file appending
     */
    @Test(timeout = 60000)
    public void testSnapshotAfterAppending() throws Exception {
        Path file = new Path(dir, "file");
        // 1. create snapshot --> create file --> append
        SnapshotTestHelper.createSnapshot(hdfs, dir, "s0");
        DFSTestUtil.createFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, TestINodeFileUnderConstructionWithSnapshot.REPLICATION, TestINodeFileUnderConstructionWithSnapshot.seed);
        DFSTestUtil.appendFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        INodeFile fileNode = ((INodeFile) (fsdir.getINode(file.toString())));
        // 2. create snapshot --> modify the file --> append
        hdfs.createSnapshot(dir, "s1");
        hdfs.setReplication(file, ((short) ((TestINodeFileUnderConstructionWithSnapshot.REPLICATION) - 1)));
        DFSTestUtil.appendFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        // check corresponding inodes
        fileNode = ((INodeFile) (fsdir.getINode(file.toString())));
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.REPLICATION) - 1), fileNode.getFileReplication());
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 3), fileNode.computeFileSize());
        // 3. create snapshot --> append
        hdfs.createSnapshot(dir, "s2");
        DFSTestUtil.appendFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        // check corresponding inodes
        fileNode = ((INodeFile) (fsdir.getINode(file.toString())));
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.REPLICATION) - 1), fileNode.getFileReplication());
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 4), fileNode.computeFileSize());
    }

    /**
     * Test snapshot during file appending, before the corresponding
     * {@link FSDataOutputStream} instance closes.
     */
    @Test(timeout = 60000)
    public void testSnapshotWhileAppending() throws Exception {
        Path file = new Path(dir, "file");
        DFSTestUtil.createFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, TestINodeFileUnderConstructionWithSnapshot.REPLICATION, TestINodeFileUnderConstructionWithSnapshot.seed);
        // 1. append without closing stream --> create snapshot
        HdfsDataOutputStream out = appendFileWithoutClosing(file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        out.hsync(EnumSet.of(UPDATE_LENGTH));
        SnapshotTestHelper.createSnapshot(hdfs, dir, "s0");
        out.close();
        // check: an INodeFileUnderConstructionWithSnapshot should be stored into s0's
        // deleted list, with size BLOCKSIZE*2
        INodeFile fileNode = ((INodeFile) (fsdir.getINode(file.toString())));
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2), fileNode.computeFileSize());
        INodeDirectory dirNode = fsdir.getINode(dir.toString()).asDirectory();
        DirectoryDiff last = dirNode.getDiffs().getLast();
        // 2. append without closing stream
        out = appendFileWithoutClosing(file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        out.hsync(EnumSet.of(UPDATE_LENGTH));
        // re-check nodeInDeleted_S0
        dirNode = fsdir.getINode(dir.toString()).asDirectory();
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2), fileNode.computeFileSize(last.getSnapshotId()));
        // 3. take snapshot --> close stream
        hdfs.createSnapshot(dir, "s1");
        out.close();
        // check: an INodeFileUnderConstructionWithSnapshot with size BLOCKSIZE*3 should
        // have been stored in s1's deleted list
        fileNode = ((INodeFile) (fsdir.getINode(file.toString())));
        dirNode = fsdir.getINode(dir.toString()).asDirectory();
        last = dirNode.getDiffs().getLast();
        Assert.assertTrue(fileNode.isWithSnapshot());
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 3), fileNode.computeFileSize(last.getSnapshotId()));
        // 4. modify file --> append without closing stream --> take snapshot -->
        // close stream
        hdfs.setReplication(file, ((short) ((TestINodeFileUnderConstructionWithSnapshot.REPLICATION) - 1)));
        out = appendFileWithoutClosing(file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        hdfs.createSnapshot(dir, "s2");
        out.close();
        // re-check the size of nodeInDeleted_S1
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 3), fileNode.computeFileSize(last.getSnapshotId()));
    }

    /**
     * call DFSClient#callGetBlockLocations(...) for snapshot file. Make sure only
     * blocks within the size range are returned.
     */
    @Test
    public void testGetBlockLocations() throws Exception {
        final Path root = new Path("/");
        final Path file = new Path("/file");
        DFSTestUtil.createFile(hdfs, file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, TestINodeFileUnderConstructionWithSnapshot.REPLICATION, TestINodeFileUnderConstructionWithSnapshot.seed);
        // take a snapshot on root
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        final Path fileInSnapshot = SnapshotTestHelper.getSnapshotPath(root, "s1", file.getName());
        FileStatus status = hdfs.getFileStatus(fileInSnapshot);
        // make sure we record the size for the file
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, status.getLen());
        // append data to file
        DFSTestUtil.appendFile(hdfs, file, ((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) - 1));
        status = hdfs.getFileStatus(fileInSnapshot);
        // the size of snapshot file should still be BLOCKSIZE
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, status.getLen());
        // the size of the file should be (2 * BLOCKSIZE - 1)
        status = hdfs.getFileStatus(file);
        Assert.assertEquals((((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2) - 1), status.getLen());
        // call DFSClient#callGetBlockLocations for the file in snapshot
        LocatedBlocks blocks = DFSClientAdapter.callGetBlockLocations(cluster.getNameNodeRpc(), fileInSnapshot.toString(), 0, Long.MAX_VALUE);
        List<LocatedBlock> blockList = blocks.getLocatedBlocks();
        // should be only one block
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, blocks.getFileLength());
        Assert.assertEquals(1, blockList.size());
        // check the last block
        LocatedBlock lastBlock = blocks.getLastLocatedBlock();
        Assert.assertEquals(0, lastBlock.getStartOffset());
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, lastBlock.getBlockSize());
        // take another snapshot
        SnapshotTestHelper.createSnapshot(hdfs, root, "s2");
        final Path fileInSnapshot2 = SnapshotTestHelper.getSnapshotPath(root, "s2", file.getName());
        // append data to file without closing
        HdfsDataOutputStream out = appendFileWithoutClosing(file, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE);
        out.hsync(EnumSet.of(UPDATE_LENGTH));
        status = hdfs.getFileStatus(fileInSnapshot2);
        // the size of snapshot file should be BLOCKSIZE*2-1
        Assert.assertEquals((((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2) - 1), status.getLen());
        // the size of the file should be (3 * BLOCKSIZE - 1)
        status = hdfs.getFileStatus(file);
        Assert.assertEquals((((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 3) - 1), status.getLen());
        blocks = DFSClientAdapter.callGetBlockLocations(cluster.getNameNodeRpc(), fileInSnapshot2.toString(), 0, Long.MAX_VALUE);
        Assert.assertFalse(blocks.isUnderConstruction());
        Assert.assertTrue(blocks.isLastBlockComplete());
        blockList = blocks.getLocatedBlocks();
        // should be 2 blocks
        Assert.assertEquals((((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2) - 1), blocks.getFileLength());
        Assert.assertEquals(2, blockList.size());
        // check the last block
        lastBlock = blocks.getLastLocatedBlock();
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, lastBlock.getStartOffset());
        Assert.assertEquals(TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, lastBlock.getBlockSize());
        blocks = DFSClientAdapter.callGetBlockLocations(cluster.getNameNodeRpc(), fileInSnapshot2.toString(), TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, 0);
        blockList = blocks.getLocatedBlocks();
        Assert.assertEquals(1, blockList.size());
        // check blocks for file being written
        blocks = DFSClientAdapter.callGetBlockLocations(cluster.getNameNodeRpc(), file.toString(), 0, Long.MAX_VALUE);
        blockList = blocks.getLocatedBlocks();
        Assert.assertEquals(3, blockList.size());
        Assert.assertTrue(blocks.isUnderConstruction());
        Assert.assertFalse(blocks.isLastBlockComplete());
        lastBlock = blocks.getLastLocatedBlock();
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) * 2), lastBlock.getStartOffset());
        Assert.assertEquals(((TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE) - 1), lastBlock.getBlockSize());
        out.close();
    }

    @Test
    public void testLease() throws Exception {
        try {
            NameNodeAdapter.setLeasePeriod(fsn, 100, 200);
            final Path foo = new Path(dir, "foo");
            final Path bar = new Path(foo, "bar");
            DFSTestUtil.createFile(hdfs, bar, TestINodeFileUnderConstructionWithSnapshot.BLOCKSIZE, TestINodeFileUnderConstructionWithSnapshot.REPLICATION, 0);
            HdfsDataOutputStream out = appendFileWithoutClosing(bar, 100);
            out.hsync(EnumSet.of(UPDATE_LENGTH));
            SnapshotTestHelper.createSnapshot(hdfs, dir, "s0");
            hdfs.delete(foo, true);
            Thread.sleep(1000);
            try {
                fsn.writeLock();
                NameNodeAdapter.getLeaseManager(fsn).runLeaseChecks();
            } finally {
                fsn.writeUnlock();
            }
        } finally {
            NameNodeAdapter.setLeasePeriod(fsn, LEASE_SOFTLIMIT_PERIOD, LEASE_HARDLIMIT_PERIOD);
        }
    }
}

