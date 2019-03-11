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


import DirOp.WRITE;
import FSEditLogLoader.LOG;
import HdfsConstants.LEASE_HARDLIMIT_PERIOD;
import HdfsConstants.LEASE_SOFTLIMIT_PERIOD;
import HdfsServerConstants.BlockUCState.UNDER_RECOVERY;
import HdfsServerConstants.NAMENODE_LEASE_HOLDER;
import NameNode.stateChangeLog;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import StartupOption.REGULAR;
import StartupOption.ROLLBACK;
import StartupOption.UPGRADE;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoContiguous;
import org.apache.hadoop.hdfs.server.datanode.FsDatasetTestUtils;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestFileTruncate {
    static {
        GenericTestUtils.setLogLevel(stateChangeLog, Level.TRACE);
        GenericTestUtils.setLogLevel(FSEditLogLoader.LOG, Level.TRACE);
    }

    static final Logger LOG = LoggerFactory.getLogger(TestFileTruncate.class);

    static final int BLOCK_SIZE = 4;

    static final short REPLICATION = 3;

    static final int DATANODE_NUM = 3;

    static final int SUCCESS_ATTEMPTS = 300;

    static final int RECOVERY_ATTEMPTS = 600;

    static final long SLEEP = 100L;

    static final long LOW_SOFTLIMIT = 100L;

    static final long LOW_HARDLIMIT = 200L;

    static final int SHORT_HEARTBEAT = 1;

    static Configuration conf;

    static MiniDFSCluster cluster;

    static DistributedFileSystem fs;

    private Path parent;

    /**
     * Truncate files of different sizes byte by byte.
     */
    @Test
    public void testBasicTruncate() throws IOException {
        int startingFileSize = 3 * (TestFileTruncate.BLOCK_SIZE);
        TestFileTruncate.fs.mkdirs(parent);
        TestFileTruncate.fs.setQuota(parent, 100, 1000);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        for (int fileLength = startingFileSize; fileLength > 0; fileLength -= (TestFileTruncate.BLOCK_SIZE) - 1) {
            for (int toTruncate = 0; toTruncate <= fileLength; toTruncate++) {
                final Path p = new Path(parent, ("testBasicTruncate" + fileLength));
                TestFileTruncate.writeContents(contents, fileLength, p);
                int newLength = fileLength - toTruncate;
                boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
                TestFileTruncate.LOG.info(((((((("fileLength=" + fileLength) + ", newLength=") + newLength) + ", toTruncate=") + toTruncate) + ", isReady=") + isReady));
                Assert.assertEquals(("File must be closed for zero truncate" + " or truncating at the block boundary"), isReady, ((toTruncate == 0) || ((newLength % (TestFileTruncate.BLOCK_SIZE)) == 0)));
                if (!isReady) {
                    TestFileTruncate.checkBlockRecovery(p);
                }
                ContentSummary cs = TestFileTruncate.fs.getContentSummary(parent);
                Assert.assertEquals("Bad disk space usage", cs.getSpaceConsumed(), (newLength * (TestFileTruncate.REPLICATION)));
                // validate the file content
                TestFileTruncate.checkFullFile(p, newLength, contents);
            }
        }
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * Truncate the same file multiple times until its size is zero.
     */
    @Test
    public void testMultipleTruncate() throws IOException {
        Path dir = new Path("/testMultipleTruncate");
        TestFileTruncate.fs.mkdirs(dir);
        final Path p = new Path(dir, "file");
        final byte[] data = new byte[100 * (TestFileTruncate.BLOCK_SIZE)];
        ThreadLocalRandom.current().nextBytes(data);
        TestFileTruncate.writeContents(data, data.length, p);
        for (int n = data.length; n > 0;) {
            final int newLength = ThreadLocalRandom.current().nextInt(n);
            final boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
            TestFileTruncate.LOG.info(((("newLength=" + newLength) + ", isReady=") + isReady));
            Assert.assertEquals("File must be closed for truncating at the block boundary", isReady, ((newLength % (TestFileTruncate.BLOCK_SIZE)) == 0));
            Assert.assertEquals("Truncate is not idempotent", isReady, TestFileTruncate.fs.truncate(p, newLength));
            if (!isReady) {
                TestFileTruncate.checkBlockRecovery(p);
            }
            TestFileTruncate.checkFullFile(p, newLength, data);
            n = newLength;
        }
        TestFileTruncate.fs.delete(dir, true);
    }

    /**
     * Truncate the same file multiple times until its size is zero.
     */
    @Test
    public void testSnapshotTruncateThenDeleteSnapshot() throws IOException {
        Path dir = new Path("/testSnapshotTruncateThenDeleteSnapshot");
        TestFileTruncate.fs.mkdirs(dir);
        TestFileTruncate.fs.allowSnapshot(dir);
        final Path p = new Path(dir, "file");
        final byte[] data = new byte[TestFileTruncate.BLOCK_SIZE];
        ThreadLocalRandom.current().nextBytes(data);
        TestFileTruncate.writeContents(data, data.length, p);
        final String snapshot = "s0";
        TestFileTruncate.fs.createSnapshot(dir, snapshot);
        Block lastBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock().getBlock().getLocalBlock();
        final int newLength = (data.length) - 1;
        assert (newLength % (TestFileTruncate.BLOCK_SIZE)) != 0 : " newLength must not be multiple of BLOCK_SIZE";
        final boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        TestFileTruncate.LOG.info(((("newLength=" + newLength) + ", isReady=") + isReady));
        Assert.assertEquals("File must be closed for truncating at the block boundary", isReady, ((newLength % (TestFileTruncate.BLOCK_SIZE)) == 0));
        TestFileTruncate.fs.deleteSnapshot(dir, snapshot);
        if (!isReady) {
            TestFileTruncate.checkBlockRecovery(p);
        }
        TestFileTruncate.checkFullFile(p, newLength, data);
        TestFileTruncate.assertBlockNotPresent(lastBlock);
        TestFileTruncate.fs.delete(dir, true);
    }

    /**
     * Truncate files and then run other operations such as
     * rename, set replication, set permission, etc.
     */
    @Test
    public void testTruncateWithOtherOperations() throws IOException {
        Path dir = new Path("/testTruncateOtherOperations");
        TestFileTruncate.fs.mkdirs(dir);
        final Path p = new Path(dir, "file");
        final byte[] data = new byte[2 * (TestFileTruncate.BLOCK_SIZE)];
        ThreadLocalRandom.current().nextBytes(data);
        TestFileTruncate.writeContents(data, data.length, p);
        final int newLength = (data.length) - 1;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        Assert.assertFalse(isReady);
        TestFileTruncate.fs.setReplication(p, ((short) ((TestFileTruncate.REPLICATION) - 1)));
        TestFileTruncate.fs.setPermission(p, FsPermission.createImmutable(((short) (292))));
        final Path q = new Path(dir, "newFile");
        TestFileTruncate.fs.rename(p, q);
        TestFileTruncate.checkBlockRecovery(q);
        TestFileTruncate.checkFullFile(q, newLength, data);
        TestFileTruncate.cluster.restartNameNode();
        TestFileTruncate.checkFullFile(q, newLength, data);
        TestFileTruncate.fs.delete(dir, true);
    }

    @Test
    public void testSnapshotWithAppendTruncate() throws IOException {
        testSnapshotWithAppendTruncate(0, 1, 2);
        testSnapshotWithAppendTruncate(0, 2, 1);
        testSnapshotWithAppendTruncate(1, 0, 2);
        testSnapshotWithAppendTruncate(1, 2, 0);
        testSnapshotWithAppendTruncate(2, 0, 1);
        testSnapshotWithAppendTruncate(2, 1, 0);
    }

    /**
     * Create three snapshots with file truncated 3 times.
     * Delete snapshots in the specified order and verify that
     * remaining snapshots are still readable.
     */
    @Test
    public void testSnapshotWithTruncates() throws IOException {
        testSnapshotWithTruncates(0, 1, 2);
        testSnapshotWithTruncates(0, 2, 1);
        testSnapshotWithTruncates(1, 0, 2);
        testSnapshotWithTruncates(1, 2, 0);
        testSnapshotWithTruncates(2, 0, 1);
        testSnapshotWithTruncates(2, 1, 0);
    }

    /**
     * Failure / recovery test for truncate.
     * In this failure the DNs fail to recover the blocks and the NN triggers
     * lease recovery.
     * File stays in RecoveryInProgress until DataNodes report recovery.
     */
    @Test
    public void testTruncateFailure() throws IOException {
        int startingFileSize = (2 * (TestFileTruncate.BLOCK_SIZE)) + ((TestFileTruncate.BLOCK_SIZE) / 2);
        int toTruncate = 1;
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        final Path dir = new Path("/dir");
        final Path p = new Path(dir, "testTruncateFailure");
        {
            FSDataOutputStream out = TestFileTruncate.fs.create(p, false, TestFileTruncate.BLOCK_SIZE, TestFileTruncate.REPLICATION, TestFileTruncate.BLOCK_SIZE);
            out.write(contents, 0, startingFileSize);
            try {
                TestFileTruncate.fs.truncate(p, 0);
                Assert.fail("Truncate must fail on open file.");
            } catch (IOException expected) {
                GenericTestUtils.assertExceptionContains("Failed to TRUNCATE_FILE", expected);
            } finally {
                out.close();
            }
        }
        {
            FSDataOutputStream out = TestFileTruncate.fs.append(p);
            try {
                TestFileTruncate.fs.truncate(p, 0);
                Assert.fail("Truncate must fail for append.");
            } catch (IOException expected) {
                GenericTestUtils.assertExceptionContains("Failed to TRUNCATE_FILE", expected);
            } finally {
                out.close();
            }
        }
        try {
            TestFileTruncate.fs.truncate(p, (-1));
            Assert.fail("Truncate must fail for a negative new length.");
        } catch (HadoopIllegalArgumentException expected) {
            GenericTestUtils.assertExceptionContains("Cannot truncate to a negative file size", expected);
        }
        try {
            TestFileTruncate.fs.truncate(p, (startingFileSize + 1));
            Assert.fail("Truncate must fail for a larger new length.");
        } catch (Exception expected) {
            GenericTestUtils.assertExceptionContains("Cannot truncate to a larger file size", expected);
        }
        try {
            TestFileTruncate.fs.truncate(dir, 0);
            Assert.fail("Truncate must fail for a directory.");
        } catch (Exception expected) {
            GenericTestUtils.assertExceptionContains("Path is not a file", expected);
        }
        try {
            TestFileTruncate.fs.truncate(new Path(dir, "non-existing"), 0);
            Assert.fail("Truncate must fail for a non-existing file.");
        } catch (Exception expected) {
            GenericTestUtils.assertExceptionContains("File does not exist", expected);
        }
        TestFileTruncate.fs.setPermission(p, FsPermission.createImmutable(((short) (436))));
        {
            final UserGroupInformation fooUgi = UserGroupInformation.createUserForTesting("foo", new String[]{ "foo" });
            try {
                final FileSystem foofs = DFSTestUtil.getFileSystemAs(fooUgi, TestFileTruncate.conf);
                foofs.truncate(p, 0);
                Assert.fail("Truncate must fail for no WRITE permission.");
            } catch (Exception expected) {
                GenericTestUtils.assertExceptionContains("Permission denied", expected);
            }
        }
        TestFileTruncate.cluster.shutdownDataNodes();
        NameNodeAdapter.getLeaseManager(TestFileTruncate.cluster.getNamesystem()).setLeasePeriod(TestFileTruncate.LOW_SOFTLIMIT, TestFileTruncate.LOW_HARDLIMIT);
        int newLength = startingFileSize - toTruncate;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        Assert.assertThat("truncate should have triggered block recovery.", isReady, Is.is(false));
        {
            try {
                TestFileTruncate.fs.truncate(p, 0);
                Assert.fail("Truncate must fail since a trancate is already in pregress.");
            } catch (IOException expected) {
                GenericTestUtils.assertExceptionContains("Failed to TRUNCATE_FILE", expected);
            }
        }
        boolean recoveryTriggered = false;
        for (int i = 0; i < (TestFileTruncate.RECOVERY_ATTEMPTS); i++) {
            String leaseHolder = NameNodeAdapter.getLeaseHolderForPath(TestFileTruncate.cluster.getNameNode(), p.toUri().getPath());
            if (leaseHolder.startsWith(NAMENODE_LEASE_HOLDER)) {
                recoveryTriggered = true;
                break;
            }
            try {
                Thread.sleep(TestFileTruncate.SLEEP);
            } catch (InterruptedException ignored) {
            }
        }
        Assert.assertThat((("lease recovery should have occurred in ~" + ((TestFileTruncate.SLEEP) * (TestFileTruncate.RECOVERY_ATTEMPTS))) + " ms."), recoveryTriggered, Is.is(true));
        TestFileTruncate.cluster.startDataNodes(TestFileTruncate.conf, TestFileTruncate.DATANODE_NUM, true, REGULAR, null);
        TestFileTruncate.cluster.waitActive();
        TestFileTruncate.checkBlockRecovery(p);
        NameNodeAdapter.getLeaseManager(TestFileTruncate.cluster.getNamesystem()).setLeasePeriod(LEASE_SOFTLIMIT_PERIOD, LEASE_HARDLIMIT_PERIOD);
        TestFileTruncate.checkFullFile(p, newLength, contents);
        TestFileTruncate.fs.delete(p, false);
    }

    /**
     * The last block is truncated at mid. (non copy-on-truncate)
     * dn0 is shutdown before truncate and restart after truncate successful.
     */
    @Test(timeout = 60000)
    public void testTruncateWithDataNodesRestart() throws Exception {
        int startingFileSize = 3 * (TestFileTruncate.BLOCK_SIZE);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        final Path p = new Path(parent, "testTruncateWithDataNodesRestart");
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        LocatedBlock oldBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        int dn = 0;
        int toTruncateLength = 1;
        int newLength = startingFileSize - toTruncateLength;
        TestFileTruncate.cluster.getDataNodes().get(dn).shutdown();
        truncateAndRestartDN(p, dn, newLength);
        TestFileTruncate.checkBlockRecovery(p);
        LocatedBlock newBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        /* For non copy-on-truncate, the truncated block id is the same, but the 
        GS should increase.
        The truncated block will be replicated to dn0 after it restarts.
         */
        Assert.assertEquals(newBlock.getBlock().getBlockId(), oldBlock.getBlock().getBlockId());
        Assert.assertEquals(newBlock.getBlock().getGenerationStamp(), ((oldBlock.getBlock().getGenerationStamp()) + 1));
        Thread.sleep(2000);
        // trigger the second time BR to delete the corrupted replica if there's one
        TestFileTruncate.cluster.triggerBlockReports();
        // Wait replicas come to 3
        DFSTestUtil.waitReplication(TestFileTruncate.fs, p, TestFileTruncate.REPLICATION);
        // Old replica is disregarded and replaced with the truncated one
        FsDatasetTestUtils utils = TestFileTruncate.cluster.getFsDatasetTestUtils(dn);
        Assert.assertEquals(utils.getStoredDataLength(newBlock.getBlock()), newBlock.getBlockSize());
        Assert.assertEquals(utils.getStoredGenerationStamp(newBlock.getBlock()), newBlock.getBlock().getGenerationStamp());
        // Validate the file
        FileStatus fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.checkFullFile(p, newLength, contents);
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * The last block is truncated at mid. (copy-on-truncate)
     * dn1 is shutdown before truncate and restart after truncate successful.
     */
    @Test(timeout = 60000)
    public void testCopyOnTruncateWithDataNodesRestart() throws Exception {
        int startingFileSize = 3 * (TestFileTruncate.BLOCK_SIZE);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        final Path p = new Path(parent, "testCopyOnTruncateWithDataNodesRestart");
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        LocatedBlock oldBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        TestFileTruncate.fs.allowSnapshot(parent);
        TestFileTruncate.fs.createSnapshot(parent, "ss0");
        int dn = 1;
        int toTruncateLength = 1;
        int newLength = startingFileSize - toTruncateLength;
        TestFileTruncate.cluster.getDataNodes().get(dn).shutdown();
        truncateAndRestartDN(p, dn, newLength);
        TestFileTruncate.checkBlockRecovery(p);
        LocatedBlock newBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        /* For copy-on-truncate, new block is made with new block id and new GS.
        The replicas of the new block is 2, then it will be replicated to dn1.
         */
        Assert.assertNotEquals(newBlock.getBlock().getBlockId(), oldBlock.getBlock().getBlockId());
        Assert.assertEquals(newBlock.getBlock().getGenerationStamp(), ((oldBlock.getBlock().getGenerationStamp()) + 1));
        // Wait replicas come to 3
        DFSTestUtil.waitReplication(TestFileTruncate.fs, p, TestFileTruncate.REPLICATION);
        FsDatasetTestUtils utils = TestFileTruncate.cluster.getFsDatasetTestUtils(dn);
        // New block is replicated to dn1
        Assert.assertEquals(utils.getStoredDataLength(newBlock.getBlock()), newBlock.getBlockSize());
        // Old replica exists too since there is snapshot
        Assert.assertEquals(utils.getStoredDataLength(oldBlock.getBlock()), oldBlock.getBlockSize());
        Assert.assertEquals(utils.getStoredGenerationStamp(oldBlock.getBlock()), oldBlock.getBlock().getGenerationStamp());
        // Validate the file
        FileStatus fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.checkFullFile(p, newLength, contents);
        TestFileTruncate.fs.deleteSnapshot(parent, "ss0");
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * The last block is truncated at mid. (non copy-on-truncate)
     * dn0, dn1 are restarted immediately after truncate.
     */
    @Test(timeout = 60000)
    public void testTruncateWithDataNodesRestartImmediately() throws Exception {
        int startingFileSize = 3 * (TestFileTruncate.BLOCK_SIZE);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        final Path p = new Path(parent, "testTruncateWithDataNodesRestartImmediately");
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        LocatedBlock oldBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        int dn0 = 0;
        int dn1 = 1;
        int toTruncateLength = 1;
        int newLength = startingFileSize - toTruncateLength;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        Assert.assertFalse(isReady);
        TestFileTruncate.cluster.restartDataNode(dn0, false, true);
        TestFileTruncate.cluster.restartDataNode(dn1, false, true);
        TestFileTruncate.cluster.waitActive();
        TestFileTruncate.checkBlockRecovery(p);
        LocatedBlock newBlock = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock();
        /* For non copy-on-truncate, the truncated block id is the same, but the 
        GS should increase.
         */
        Assert.assertEquals(newBlock.getBlock().getBlockId(), oldBlock.getBlock().getBlockId());
        Assert.assertEquals(newBlock.getBlock().getGenerationStamp(), ((oldBlock.getBlock().getGenerationStamp()) + 1));
        Thread.sleep(2000);
        // trigger the second time BR to delete the corrupted replica if there's one
        TestFileTruncate.cluster.triggerBlockReports();
        // Wait replicas come to 3
        DFSTestUtil.waitReplication(TestFileTruncate.fs, p, TestFileTruncate.REPLICATION);
        // Old replica is disregarded and replaced with the truncated one on dn0
        FsDatasetTestUtils utils = TestFileTruncate.cluster.getFsDatasetTestUtils(dn0);
        Assert.assertEquals(utils.getStoredDataLength(newBlock.getBlock()), newBlock.getBlockSize());
        Assert.assertEquals(utils.getStoredGenerationStamp(newBlock.getBlock()), newBlock.getBlock().getGenerationStamp());
        // Old replica is disregarded and replaced with the truncated one on dn1
        utils = TestFileTruncate.cluster.getFsDatasetTestUtils(dn1);
        Assert.assertEquals(utils.getStoredDataLength(newBlock.getBlock()), newBlock.getBlockSize());
        Assert.assertEquals(utils.getStoredGenerationStamp(newBlock.getBlock()), newBlock.getBlock().getGenerationStamp());
        // Validate the file
        FileStatus fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.checkFullFile(p, newLength, contents);
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * The last block is truncated at mid. (non copy-on-truncate)
     * shutdown the datanodes immediately after truncate.
     */
    @Test(timeout = 60000)
    public void testTruncateWithDataNodesShutdownImmediately() throws Exception {
        int startingFileSize = 3 * (TestFileTruncate.BLOCK_SIZE);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        final Path p = new Path(parent, "testTruncateWithDataNodesShutdownImmediately");
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        int toTruncateLength = 1;
        int newLength = startingFileSize - toTruncateLength;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        Assert.assertFalse(isReady);
        TestFileTruncate.cluster.shutdownDataNodes();
        TestFileTruncate.cluster.setDataNodesDead();
        try {
            for (int i = 0; (i < (TestFileTruncate.SUCCESS_ATTEMPTS)) && (TestFileTruncate.cluster.isDataNodeUp()); i++) {
                Thread.sleep(TestFileTruncate.SLEEP);
            }
            Assert.assertFalse("All DataNodes should be down.", TestFileTruncate.cluster.isDataNodeUp());
            LocatedBlocks blocks = TestFileTruncate.getLocatedBlocks(p);
            Assert.assertTrue(blocks.isUnderConstruction());
        } finally {
            TestFileTruncate.cluster.startDataNodes(TestFileTruncate.conf, TestFileTruncate.DATANODE_NUM, true, REGULAR, null);
            TestFileTruncate.cluster.waitActive();
        }
        TestFileTruncate.checkBlockRecovery(p);
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * EditLogOp load test for Truncate.
     */
    @Test
    public void testTruncateEditLogLoad() throws IOException {
        // purge previously accumulated edits
        TestFileTruncate.fs.setSafeMode(SAFEMODE_ENTER);
        TestFileTruncate.fs.saveNamespace();
        TestFileTruncate.fs.setSafeMode(SAFEMODE_LEAVE);
        int startingFileSize = (2 * (TestFileTruncate.BLOCK_SIZE)) + ((TestFileTruncate.BLOCK_SIZE) / 2);
        int toTruncate = 1;
        final String s = "/testTruncateEditLogLoad";
        final Path p = new Path(s);
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        int newLength = startingFileSize - toTruncate;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLength);
        Assert.assertThat("truncate should have triggered block recovery.", isReady, Is.is(false));
        TestFileTruncate.cluster.restartNameNode();
        String holder = UserGroupInformation.getCurrentUser().getUserName();
        TestFileTruncate.cluster.getNamesystem().recoverLease(s, holder, "");
        TestFileTruncate.checkBlockRecovery(p);
        TestFileTruncate.checkFullFile(p, newLength, contents);
        TestFileTruncate.fs.delete(p, false);
    }

    /**
     * Upgrade, RollBack, and restart test for Truncate.
     */
    @Test
    public void testUpgradeAndRestart() throws IOException {
        TestFileTruncate.fs.mkdirs(parent);
        TestFileTruncate.fs.setQuota(parent, 100, 1000);
        TestFileTruncate.fs.allowSnapshot(parent);
        String truncateFile = "testUpgrade";
        final Path p = new Path(parent, truncateFile);
        int startingFileSize = 2 * (TestFileTruncate.BLOCK_SIZE);
        int toTruncate = 1;
        byte[] contents = AppendTestUtil.initBuffer(startingFileSize);
        TestFileTruncate.writeContents(contents, startingFileSize, p);
        Path snapshotDir = TestFileTruncate.fs.createSnapshot(parent, "ss0");
        Path snapshotFile = new Path(snapshotDir, truncateFile);
        int newLengthBeforeUpgrade = startingFileSize - toTruncate;
        boolean isReady = TestFileTruncate.fs.truncate(p, newLengthBeforeUpgrade);
        Assert.assertThat("truncate should have triggered block recovery.", isReady, Is.is(false));
        TestFileTruncate.checkBlockRecovery(p);
        TestFileTruncate.checkFullFile(p, newLengthBeforeUpgrade, contents);
        TestFileTruncate.assertFileLength(snapshotFile, startingFileSize);
        long totalBlockBefore = TestFileTruncate.cluster.getNamesystem().getBlocksTotal();
        TestFileTruncate.restartCluster(UPGRADE);
        Assert.assertThat("SafeMode should be OFF", TestFileTruncate.cluster.getNamesystem().isInSafeMode(), Is.is(false));
        Assert.assertThat("NameNode should be performing upgrade.", TestFileTruncate.cluster.getNamesystem().isUpgradeFinalized(), Is.is(false));
        FileStatus fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLengthBeforeUpgrade))));
        int newLengthAfterUpgrade = newLengthBeforeUpgrade - toTruncate;
        Block oldBlk = TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock().getBlock().getLocalBlock();
        isReady = TestFileTruncate.fs.truncate(p, newLengthAfterUpgrade);
        Assert.assertThat("truncate should have triggered block recovery.", isReady, Is.is(false));
        fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLengthAfterUpgrade))));
        Assert.assertThat("Should copy on truncate during upgrade", TestFileTruncate.getLocatedBlocks(p).getLastLocatedBlock().getBlock().getLocalBlock().getBlockId(), Is.is(CoreMatchers.not(CoreMatchers.equalTo(oldBlk.getBlockId()))));
        TestFileTruncate.checkBlockRecovery(p);
        TestFileTruncate.checkFullFile(p, newLengthAfterUpgrade, contents);
        Assert.assertThat("Total block count should be unchanged from copy-on-truncate", TestFileTruncate.cluster.getNamesystem().getBlocksTotal(), Is.is(totalBlockBefore));
        TestFileTruncate.restartCluster(ROLLBACK);
        Assert.assertThat(("File does not exist " + p), TestFileTruncate.fs.exists(p), Is.is(true));
        fileStatus = TestFileTruncate.fs.getFileStatus(p);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLengthBeforeUpgrade))));
        TestFileTruncate.checkFullFile(p, newLengthBeforeUpgrade, contents);
        Assert.assertThat("Total block count should be unchanged from rolling back", TestFileTruncate.cluster.getNamesystem().getBlocksTotal(), Is.is(totalBlockBefore));
        TestFileTruncate.restartCluster(REGULAR);
        Assert.assertThat("Total block count should be unchanged from start-up", TestFileTruncate.cluster.getNamesystem().getBlocksTotal(), Is.is(totalBlockBefore));
        TestFileTruncate.checkFullFile(p, newLengthBeforeUpgrade, contents);
        TestFileTruncate.assertFileLength(snapshotFile, startingFileSize);
        // empty edits and restart
        TestFileTruncate.fs.setSafeMode(SAFEMODE_ENTER);
        TestFileTruncate.fs.saveNamespace();
        TestFileTruncate.cluster.restartNameNode(true);
        Assert.assertThat("Total block count should be unchanged from start-up", TestFileTruncate.cluster.getNamesystem().getBlocksTotal(), Is.is(totalBlockBefore));
        TestFileTruncate.checkFullFile(p, newLengthBeforeUpgrade, contents);
        TestFileTruncate.assertFileLength(snapshotFile, startingFileSize);
        TestFileTruncate.fs.deleteSnapshot(parent, "ss0");
        TestFileTruncate.fs.delete(parent, true);
        Assert.assertThat((("File " + p) + " shouldn't exist"), TestFileTruncate.fs.exists(p), Is.is(false));
    }

    /**
     * Check truncate recovery.
     */
    @Test
    public void testTruncateRecovery() throws IOException {
        FSNamesystem fsn = TestFileTruncate.cluster.getNamesystem();
        String client = "client";
        String clientMachine = "clientMachine";
        String src = "/test/testTruncateRecovery";
        Path srcPath = new Path(src);
        byte[] contents = AppendTestUtil.initBuffer(TestFileTruncate.BLOCK_SIZE);
        TestFileTruncate.writeContents(contents, TestFileTruncate.BLOCK_SIZE, srcPath);
        INodesInPath iip = fsn.getFSDirectory().getINodesInPath(src, WRITE);
        INodeFile file = iip.getLastINode().asFile();
        long initialGenStamp = file.getLastBlock().getGenerationStamp();
        // Test that prepareFileForTruncate sets up in-place truncate.
        fsn.writeLock();
        try {
            Block oldBlock = file.getLastBlock();
            Block truncateBlock = FSDirTruncateOp.prepareFileForTruncate(fsn, iip, client, clientMachine, 1, null);
            // In-place truncate uses old block id with new genStamp.
            Assert.assertThat(truncateBlock.getBlockId(), Is.is(CoreMatchers.equalTo(oldBlock.getBlockId())));
            Assert.assertThat(truncateBlock.getNumBytes(), Is.is(oldBlock.getNumBytes()));
            Assert.assertThat(truncateBlock.getGenerationStamp(), Is.is(fsn.getBlockManager().getBlockIdManager().getGenerationStamp()));
            Assert.assertThat(file.getLastBlock().getBlockUCState(), Is.is(UNDER_RECOVERY));
            long blockRecoveryId = file.getLastBlock().getUnderConstructionFeature().getBlockRecoveryId();
            Assert.assertThat(blockRecoveryId, Is.is((initialGenStamp + 1)));
            fsn.getEditLog().logTruncate(src, client, clientMachine, ((TestFileTruncate.BLOCK_SIZE) - 1), Time.now(), truncateBlock);
        } finally {
            fsn.writeUnlock();
        }
        // Re-create file and ensure we are ready to copy on truncate
        TestFileTruncate.writeContents(contents, TestFileTruncate.BLOCK_SIZE, srcPath);
        TestFileTruncate.fs.allowSnapshot(parent);
        TestFileTruncate.fs.createSnapshot(parent, "ss0");
        iip = fsn.getFSDirectory().getINodesInPath(src, WRITE);
        file = iip.getLastINode().asFile();
        file.recordModification(iip.getLatestSnapshotId(), true);
        Assert.assertThat(file.isBlockInLatestSnapshot(((BlockInfoContiguous) (file.getLastBlock()))), Is.is(true));
        initialGenStamp = file.getLastBlock().getGenerationStamp();
        // Test that prepareFileForTruncate sets up copy-on-write truncate
        fsn.writeLock();
        try {
            Block oldBlock = file.getLastBlock();
            Block truncateBlock = FSDirTruncateOp.prepareFileForTruncate(fsn, iip, client, clientMachine, 1, null);
            // Copy-on-write truncate makes new block with new id and genStamp
            Assert.assertThat(truncateBlock.getBlockId(), Is.is(CoreMatchers.not(CoreMatchers.equalTo(oldBlock.getBlockId()))));
            Assert.assertThat(((truncateBlock.getNumBytes()) < (oldBlock.getNumBytes())), Is.is(true));
            Assert.assertThat(truncateBlock.getGenerationStamp(), Is.is(fsn.getBlockManager().getBlockIdManager().getGenerationStamp()));
            Assert.assertThat(file.getLastBlock().getBlockUCState(), Is.is(UNDER_RECOVERY));
            long blockRecoveryId = file.getLastBlock().getUnderConstructionFeature().getBlockRecoveryId();
            Assert.assertThat(blockRecoveryId, Is.is((initialGenStamp + 1)));
            fsn.getEditLog().logTruncate(src, client, clientMachine, ((TestFileTruncate.BLOCK_SIZE) - 1), Time.now(), truncateBlock);
        } finally {
            fsn.writeUnlock();
        }
        TestFileTruncate.checkBlockRecovery(srcPath);
        TestFileTruncate.fs.deleteSnapshot(parent, "ss0");
        TestFileTruncate.fs.delete(parent, true);
    }

    @Test
    public void testTruncateShellCommand() throws Exception {
        final Path src = new Path("/test/testTruncateShellCommand");
        final int oldLength = (2 * (TestFileTruncate.BLOCK_SIZE)) + 1;
        final int newLength = (TestFileTruncate.BLOCK_SIZE) + 1;
        String[] argv = new String[]{ "-truncate", String.valueOf(newLength), src.toString() };
        runTruncateShellCommand(src, oldLength, argv);
        // wait for block recovery
        TestFileTruncate.checkBlockRecovery(src);
        Assert.assertThat(TestFileTruncate.fs.getFileStatus(src).getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.fs.delete(parent, true);
    }

    @Test
    public void testTruncateShellCommandOnBlockBoundary() throws Exception {
        final Path src = new Path("/test/testTruncateShellCommandOnBoundary");
        final int oldLength = 2 * (TestFileTruncate.BLOCK_SIZE);
        final int newLength = TestFileTruncate.BLOCK_SIZE;
        String[] argv = new String[]{ "-truncate", String.valueOf(newLength), src.toString() };
        runTruncateShellCommand(src, oldLength, argv);
        // shouldn't need to wait for block recovery
        Assert.assertThat(TestFileTruncate.fs.getFileStatus(src).getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.fs.delete(parent, true);
    }

    @Test
    public void testTruncateShellCommandWithWaitOption() throws Exception {
        final Path src = new Path("/test/testTruncateShellCommandWithWaitOption");
        final int oldLength = (2 * (TestFileTruncate.BLOCK_SIZE)) + 1;
        final int newLength = (TestFileTruncate.BLOCK_SIZE) + 1;
        String[] argv = new String[]{ "-truncate", "-w", String.valueOf(newLength), src.toString() };
        runTruncateShellCommand(src, oldLength, argv);
        // shouldn't need to wait for block recovery
        Assert.assertThat(TestFileTruncate.fs.getFileStatus(src).getLen(), Is.is(((long) (newLength))));
        TestFileTruncate.fs.delete(parent, true);
    }

    @Test
    public void testTruncate4Symlink() throws IOException {
        final int fileLength = 3 * (TestFileTruncate.BLOCK_SIZE);
        TestFileTruncate.fs.mkdirs(parent);
        final byte[] contents = AppendTestUtil.initBuffer(fileLength);
        final Path file = new Path(parent, "testTruncate4Symlink");
        TestFileTruncate.writeContents(contents, fileLength, file);
        final Path link = new Path(parent, "link");
        TestFileTruncate.fs.createSymlink(file, link, false);
        final int newLength = fileLength / 3;
        boolean isReady = TestFileTruncate.fs.truncate(link, newLength);
        Assert.assertTrue("Recovery is not expected.", isReady);
        FileStatus fileStatus = TestFileTruncate.fs.getFileStatus(file);
        Assert.assertThat(fileStatus.getLen(), Is.is(((long) (newLength))));
        ContentSummary cs = TestFileTruncate.fs.getContentSummary(parent);
        Assert.assertEquals("Bad disk space usage", cs.getSpaceConsumed(), (newLength * (TestFileTruncate.REPLICATION)));
        // validate the file content
        TestFileTruncate.checkFullFile(file, newLength, contents);
        TestFileTruncate.fs.delete(parent, true);
    }

    /**
     * While rolling upgrade is in-progress the test truncates a file
     * such that copy-on-truncate is triggered, then deletes the file,
     * and makes sure that no blocks involved in truncate are hanging around.
     */
    @Test
    public void testTruncateWithRollingUpgrade() throws Exception {
        final DFSAdmin dfsadmin = new DFSAdmin(TestFileTruncate.cluster.getConfiguration(0));
        DistributedFileSystem dfs = TestFileTruncate.cluster.getFileSystem();
        // start rolling upgrade
        dfs.setSafeMode(SAFEMODE_ENTER);
        int status = dfsadmin.run(new String[]{ "-rollingUpgrade", "prepare" });
        Assert.assertEquals("could not prepare for rolling upgrade", 0, status);
        dfs.setSafeMode(SAFEMODE_LEAVE);
        Path dir = new Path("/testTruncateWithRollingUpgrade");
        TestFileTruncate.fs.mkdirs(dir);
        final Path p = new Path(dir, "file");
        final byte[] data = new byte[3];
        ThreadLocalRandom.current().nextBytes(data);
        TestFileTruncate.writeContents(data, data.length, p);
        Assert.assertEquals("block num should 1", 1, TestFileTruncate.cluster.getNamesystem().getFSDirectory().getBlockManager().getTotalBlocks());
        final boolean isReady = TestFileTruncate.fs.truncate(p, 2);
        Assert.assertFalse("should be copy-on-truncate", isReady);
        Assert.assertEquals("block num should 2", 2, TestFileTruncate.cluster.getNamesystem().getFSDirectory().getBlockManager().getTotalBlocks());
        TestFileTruncate.fs.delete(p, true);
        Assert.assertEquals("block num should 0", 0, TestFileTruncate.cluster.getNamesystem().getFSDirectory().getBlockManager().getTotalBlocks());
        status = dfsadmin.run(new String[]{ "-rollingUpgrade", "finalize" });
        Assert.assertEquals("could not finalize rolling upgrade", 0, status);
    }
}

