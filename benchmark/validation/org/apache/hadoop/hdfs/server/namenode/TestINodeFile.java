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
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_DEFAULT;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_LIST_LIMIT;
import DFSConfigKeys.DFS_NAMENODE_ACLS_ENABLED_KEY;
import FsAction.READ_WRITE;
import HdfsFileStatus.EMPTY_NAME;
import INodeId.ROOT_INODE_ID;
import Options.Rename.OVERWRITE;
import Path.SEPARATOR;
import XAttr.NameSpace.USER;
import com.google.common.collect.ImmutableList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DirectoryListingStartAfterNotFoundException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.BlockType;
import org.apache.hadoop.hdfs.protocol.DirectoryListing;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.QuotaExceededException;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FSDirectory.CHECK_RESERVED_FILE_NAMES;
import static INodeDirectory.ROOT_NAME;
import static INodeId.ROOT_INODE_ID;


public class TestINodeFile {
    // Re-enable symlinks for tests, see HADOOP-10020 and HADOOP-10052
    static {
        FileSystem.enableSymlinks();
    }

    public static final Logger LOG = LoggerFactory.getLogger(TestINodeFile.class);

    static final short BLOCKBITS = 48;

    static final long BLKSIZE_MAXVALUE = ~(65535L << (TestINodeFile.BLOCKBITS));

    private static final PermissionStatus perm = new PermissionStatus("userName", null, FsPermission.getDefault());

    private short replication;

    private long preferredBlockSize = 1024;

    @Test
    public void testStoragePolicyID() {
        for (byte i = 0; i < 16; i++) {
            final INodeFile f = TestINodeFile.createINodeFile(i);
            Assert.assertEquals(i, f.getStoragePolicyID());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoragePolicyIdBelowLowerBound() throws IllegalArgumentException {
        TestINodeFile.createINodeFile(((byte) (-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoragePolicyIdAboveUpperBound() throws IllegalArgumentException {
        TestINodeFile.createINodeFile(((byte) (16)));
    }

    @Test
    public void testContiguousLayoutRedundancy() {
        INodeFile inodeFile;
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, new Short(((short) (3))), StripedFileTestUtil.getDefaultECPolicy().getId(), preferredBlockSize, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.CONTIGUOUS);
            Assert.fail(("INodeFile construction should fail when both replication and " + "ECPolicy requested!"));
        } catch (IllegalArgumentException iae) {
            TestINodeFile.LOG.info("Expected exception: ", iae);
        }
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, null, null, preferredBlockSize, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.CONTIGUOUS);
            Assert.fail(("INodeFile construction should fail when replication param not " + "provided for contiguous layout!"));
        } catch (IllegalArgumentException iae) {
            TestINodeFile.LOG.info("Expected exception: ", iae);
        }
        try {
            /* replication */
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, Short.MAX_VALUE, null, preferredBlockSize, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.CONTIGUOUS);
            Assert.fail(("INodeFile construction should fail when replication param is " + "beyond the range supported!"));
        } catch (IllegalArgumentException iae) {
            TestINodeFile.LOG.info("Expected exception: ", iae);
        }
        final Short replication = new Short(((short) (3)));
        try {
            /* ec policy */
            new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, replication, null, preferredBlockSize, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.STRIPED);
            Assert.fail(("INodeFile construction should fail when replication param is " + "provided for striped layout!"));
        } catch (IllegalArgumentException iae) {
            TestINodeFile.LOG.info("Expected exception: ", iae);
        }
        inodeFile = /* ec policy */
        new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, replication, null, preferredBlockSize, HdfsConstants.WARM_STORAGE_POLICY_ID, BlockType.CONTIGUOUS);
        Assert.assertTrue((!(inodeFile.isStriped())));
        Assert.assertEquals(replication.shortValue(), inodeFile.getFileReplication());
    }

    /**
     * Test for the Replication value. Sets a value and checks if it was set
     * correct.
     */
    @Test
    public void testReplication() {
        replication = 3;
        preferredBlockSize = (128 * 1024) * 1024;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        Assert.assertEquals("True has to be returned in this case", replication, inf.getFileReplication());
    }

    /**
     * IllegalArgumentException is expected for setting below lower bound
     * for Replication.
     *
     * @throws IllegalArgumentException
     * 		as the result
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReplicationBelowLowerBound() throws IllegalArgumentException {
        replication = -1;
        preferredBlockSize = (128 * 1024) * 1024;
        createINodeFile(replication, preferredBlockSize);
    }

    /**
     * Test for the PreferredBlockSize value. Sets a value and checks if it was
     * set correct.
     */
    @Test
    public void testPreferredBlockSize() {
        replication = 3;
        preferredBlockSize = (128 * 1024) * 1024;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        Assert.assertEquals("True has to be returned in this case", preferredBlockSize, inf.getPreferredBlockSize());
    }

    @Test
    public void testPreferredBlockSizeUpperBound() {
        replication = 3;
        preferredBlockSize = TestINodeFile.BLKSIZE_MAXVALUE;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        Assert.assertEquals("True has to be returned in this case", TestINodeFile.BLKSIZE_MAXVALUE, inf.getPreferredBlockSize());
    }

    /**
     * IllegalArgumentException is expected for setting below lower bound
     * for PreferredBlockSize.
     *
     * @throws IllegalArgumentException
     * 		as the result
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPreferredBlockSizeBelowLowerBound() throws IllegalArgumentException {
        replication = 3;
        preferredBlockSize = -1;
        createINodeFile(replication, preferredBlockSize);
    }

    /**
     * IllegalArgumentException is expected for setting above upper bound
     * for PreferredBlockSize.
     *
     * @throws IllegalArgumentException
     * 		as the result
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPreferredBlockSizeAboveUpperBound() throws IllegalArgumentException {
        replication = 3;
        preferredBlockSize = (TestINodeFile.BLKSIZE_MAXVALUE) + 1;
        createINodeFile(replication, preferredBlockSize);
    }

    @Test
    public void testGetFullPathName() {
        replication = 3;
        preferredBlockSize = (128 * 1024) * 1024;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        inf.setLocalName(DFSUtil.string2Bytes("f"));
        INodeDirectory root = new INodeDirectory(HdfsConstants.GRANDFATHER_INODE_ID, ROOT_NAME, TestINodeFile.perm, 0L);
        INodeDirectory dir = new INodeDirectory(HdfsConstants.GRANDFATHER_INODE_ID, DFSUtil.string2Bytes("d"), TestINodeFile.perm, 0L);
        Assert.assertEquals("f", inf.getFullPathName());
        dir.addChild(inf);
        Assert.assertEquals((("d" + (Path.SEPARATOR)) + "f"), inf.getFullPathName());
        root.addChild(dir);
        Assert.assertEquals(((((Path.SEPARATOR) + "d") + (Path.SEPARATOR)) + "f"), inf.getFullPathName());
        Assert.assertEquals(((Path.SEPARATOR) + "d"), dir.getFullPathName());
        Assert.assertEquals(SEPARATOR, root.getFullPathName());
    }

    @Test
    public void testGetBlockType() {
        replication = 3;
        preferredBlockSize = (128 * 1024) * 1024;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        Assert.assertEquals(inf.getBlockType(), BlockType.CONTIGUOUS);
        INodeFile striped = createStripedINodeFile(preferredBlockSize);
        Assert.assertEquals(striped.getBlockType(), BlockType.STRIPED);
    }

    /**
     * FSDirectory#unprotectedSetQuota creates a new INodeDirectoryWithQuota to
     * replace the original INodeDirectory. Before HDFS-4243, the parent field of
     * all the children INodes of the target INodeDirectory is not changed to
     * point to the new INodeDirectoryWithQuota. This testcase tests this
     * scenario.
     */
    @Test
    public void testGetFullPathNameAfterSetQuota() throws Exception {
        long fileLen = 1024;
        replication = 3;
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(replication).build();
            cluster.waitActive();
            FSNamesystem fsn = cluster.getNamesystem();
            FSDirectory fsdir = fsn.getFSDirectory();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // Create a file for test
            final Path dir = new Path("/dir");
            final Path file = new Path(dir, "file");
            DFSTestUtil.createFile(dfs, file, fileLen, replication, 0L);
            // Check the full path name of the INode associating with the file
            INode fnode = fsdir.getINode(file.toString());
            Assert.assertEquals(file.toString(), fnode.getFullPathName());
            // Call FSDirectory#unprotectedSetQuota which calls
            // INodeDirectory#replaceChild
            dfs.setQuota(dir, ((Long.MAX_VALUE) - 1), (((replication) * fileLen) * 10));
            INodeDirectory dirNode = TestINodeFile.getDir(fsdir, dir);
            Assert.assertEquals(dir.toString(), dirNode.getFullPathName());
            Assert.assertTrue(dirNode.isWithQuota());
            final Path newDir = new Path("/newdir");
            final Path newFile = new Path(newDir, "file");
            // Also rename dir
            dfs.rename(dir, newDir, OVERWRITE);
            // /dir/file now should be renamed to /newdir/file
            fnode = fsdir.getINode(newFile.toString());
            // getFullPathName can return correct result only if the parent field of
            // child node is set correctly
            Assert.assertEquals(newFile.toString(), fnode.getFullPathName());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testConcatBlocks() {
        INodeFile origFile = createINodeFiles(1, "origfile")[0];
        Assert.assertEquals("Number of blocks didn't match", origFile.numBlocks(), 1L);
        INodeFile[] appendFiles = createINodeFiles(4, "appendfile");
        BlockManager bm = Mockito.mock(BlockManager.class);
        origFile.concatBlocks(appendFiles, bm);
        Assert.assertEquals("Number of blocks didn't match", origFile.numBlocks(), 5L);
    }

    /**
     * Test for the static {@link INodeFile#valueOf(INode, String)}
     * and {@link INodeFileUnderConstruction#valueOf(INode, String)} methods.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testValueOf() throws IOException {
        final String path = "/testValueOf";
        final short replication = 3;
        {
            // cast from null
            final INode from = null;
            // cast to INodeFile, should fail
            try {
                INodeFile.valueOf(from, path);
                Assert.fail();
            } catch (FileNotFoundException fnfe) {
                Assert.assertTrue(fnfe.getMessage().contains("File does not exist"));
            }
            // cast to INodeDirectory, should fail
            try {
                INodeDirectory.valueOf(from, path);
                Assert.fail();
            } catch (FileNotFoundException e) {
                Assert.assertTrue(e.getMessage().contains("Directory does not exist"));
            }
        }
        {
            // cast from INodeFile
            final INode from = createINodeFile(replication, preferredBlockSize);
            // cast to INodeFile, should success
            final INodeFile f = INodeFile.valueOf(from, path);
            Assert.assertTrue((f == from));
            // cast to INodeDirectory, should fail
            try {
                INodeDirectory.valueOf(from, path);
                Assert.fail();
            } catch (PathIsNotDirectoryException e) {
                // Expected
            }
        }
        {
            // cast from INodeFileUnderConstruction
            final INode from = new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, replication, 1024L);
            from.asFile().toUnderConstruction("client", "machine");
            // cast to INodeFile, should success
            final INodeFile f = INodeFile.valueOf(from, path);
            Assert.assertTrue((f == from));
            // cast to INodeDirectory, should fail
            try {
                INodeDirectory.valueOf(from, path);
                Assert.fail();
            } catch (PathIsNotDirectoryException expected) {
                // expected
            }
        }
        {
            // cast from INodeDirectory
            final INode from = new INodeDirectory(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L);
            // cast to INodeFile, should fail
            try {
                INodeFile.valueOf(from, path);
                Assert.fail();
            } catch (FileNotFoundException fnfe) {
                Assert.assertTrue(fnfe.getMessage().contains("Path is not a file"));
            }
            // cast to INodeDirectory, should success
            final INodeDirectory d = INodeDirectory.valueOf(from, path);
            Assert.assertTrue((d == from));
        }
    }

    /**
     * This test verifies inode ID counter and inode map functionality.
     */
    @Test
    public void testInodeId() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(DFS_BLOCK_SIZE_KEY, DFS_BYTES_PER_CHECKSUM_DEFAULT);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FSNamesystem fsn = cluster.getNamesystem();
            long lastId = fsn.dir.getLastInodeId();
            // Ensure root has the correct inode ID
            // Last inode ID should be root inode ID and inode map size should be 1
            int inodeCount = 1;
            long expectedLastInodeId = ROOT_INODE_ID;
            Assert.assertEquals(fsn.dir.rootDir.getId(), ROOT_INODE_ID);
            Assert.assertEquals(expectedLastInodeId, lastId);
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Create a directory
            // Last inode ID and inode map size should increase by 1
            FileSystem fs = cluster.getFileSystem();
            Path path = new Path("/test1");
            Assert.assertTrue(fs.mkdirs(path));
            Assert.assertEquals((++expectedLastInodeId), fsn.dir.getLastInodeId());
            Assert.assertEquals((++inodeCount), fsn.dir.getInodeMapSize());
            // Create a file
            // Last inode ID and inode map size should increase by 1
            NamenodeProtocols nnrpc = cluster.getNameNodeRpc();
            DFSTestUtil.createFile(fs, new Path("/test1/file"), 1024, ((short) (1)), 0);
            Assert.assertEquals((++expectedLastInodeId), fsn.dir.getLastInodeId());
            Assert.assertEquals((++inodeCount), fsn.dir.getInodeMapSize());
            // Ensure right inode ID is returned in file status
            HdfsFileStatus fileStatus = nnrpc.getFileInfo("/test1/file");
            Assert.assertEquals(expectedLastInodeId, fileStatus.getFileId());
            // Rename a directory
            // Last inode ID and inode map size should not change
            Path renamedPath = new Path("/test2");
            Assert.assertTrue(fs.rename(path, renamedPath));
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Delete test2/file and test2 and ensure inode map size decreases
            Assert.assertTrue(fs.delete(renamedPath, true));
            inodeCount -= 2;
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Create and concat /test/file1 /test/file2
            // Create /test1/file1 and /test1/file2
            String file1 = "/test1/file1";
            String file2 = "/test1/file2";
            DFSTestUtil.createFile(fs, new Path(file1), 512, ((short) (1)), 0);
            DFSTestUtil.createFile(fs, new Path(file2), 512, ((short) (1)), 0);
            inodeCount += 3;// test1, file1 and file2 are created

            expectedLastInodeId += 3;
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            // Concat the /test1/file1 /test1/file2 into /test1/file2
            nnrpc.concat(file2, new String[]{ file1 });
            inodeCount--;// file1 and file2 are concatenated to file2

            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertTrue(fs.delete(new Path("/test1"), true));
            inodeCount -= 2;// test1 and file2 is deleted

            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Make sure editlog is loaded correctly
            cluster.restartNameNode();
            cluster.waitActive();
            fsn = cluster.getNamesystem();
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Create two inodes test2 and test2/file2
            DFSTestUtil.createFile(fs, new Path("/test2/file2"), 1024, ((short) (1)), 0);
            expectedLastInodeId += 2;
            inodeCount += 2;
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // create /test3, and /test3/file.
            // /test3/file is a file under construction
            FSDataOutputStream outStream = fs.create(new Path("/test3/file"));
            Assert.assertTrue((outStream != null));
            expectedLastInodeId += 2;
            inodeCount += 2;
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
            // Apply editlogs to fsimage, ensure inodeUnderConstruction is handled
            fsn.enterSafeMode(false);
            fsn.saveNamespace(0, 0);
            fsn.leaveSafeMode(false);
            outStream.close();
            // The lastInodeId in fsimage should remain the same after reboot
            cluster.restartNameNode();
            cluster.waitActive();
            fsn = cluster.getNamesystem();
            Assert.assertEquals(expectedLastInodeId, fsn.dir.getLastInodeId());
            Assert.assertEquals(inodeCount, fsn.dir.getInodeMapSize());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 120000)
    public void testWriteToDeletedFile() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        Path path = new Path("/test1");
        Assert.assertTrue(fs.mkdirs(path));
        int size = conf.getInt(DFS_BYTES_PER_CHECKSUM_KEY, 512);
        byte[] data = new byte[size];
        // Create one file
        Path filePath = new Path("/test1/file");
        FSDataOutputStream fos = fs.create(filePath);
        // Delete the file
        fs.delete(filePath, false);
        // Add new block should fail since /test1/file has been deleted.
        try {
            fos.write(data, 0, data.length);
            // make sure addBlock() request gets to NN immediately
            fos.hflush();
            Assert.fail("Write should fail after delete");
        } catch (Exception e) {
            /* Ignore */
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Tests for addressing files using /.reserved/.inodes/<inodeID> in file system
     * operations.
     */
    @Test
    public void testInodeIdBasedPaths() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(DFS_BLOCK_SIZE_KEY, DFS_BYTES_PER_CHECKSUM_DEFAULT);
        conf.setBoolean(DFS_NAMENODE_ACLS_ENABLED_KEY, true);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            NamenodeProtocols nnRpc = cluster.getNameNodeRpc();
            // FileSystem#mkdirs "/testInodeIdBasedPaths"
            Path baseDir = getInodePath(ROOT_INODE_ID, "testInodeIdBasedPaths");
            Path baseDirRegPath = new Path("/testInodeIdBasedPaths");
            fs.mkdirs(baseDir);
            fs.exists(baseDir);
            long baseDirFileId = nnRpc.getFileInfo(baseDir.toString()).getFileId();
            // FileSystem#create file and FileSystem#close
            Path testFileInodePath = getInodePath(baseDirFileId, "test1");
            Path testFileRegularPath = new Path(baseDir, "test1");
            final int testFileBlockSize = 1024;
            FileSystemTestHelper.createFile(fs, testFileInodePath, 1, testFileBlockSize);
            Assert.assertTrue(fs.exists(testFileInodePath));
            // FileSystem#setPermission
            FsPermission perm = new FsPermission(((short) (438)));
            fs.setPermission(testFileInodePath, perm);
            // FileSystem#getFileStatus and FileSystem#getPermission
            FileStatus fileStatus = fs.getFileStatus(testFileInodePath);
            Assert.assertEquals(perm, fileStatus.getPermission());
            // FileSystem#setOwner
            fs.setOwner(testFileInodePath, fileStatus.getOwner(), fileStatus.getGroup());
            // FileSystem#setTimes
            fs.setTimes(testFileInodePath, 0, 0);
            fileStatus = fs.getFileStatus(testFileInodePath);
            Assert.assertEquals(0, fileStatus.getModificationTime());
            Assert.assertEquals(0, fileStatus.getAccessTime());
            // FileSystem#setReplication
            fs.setReplication(testFileInodePath, ((short) (3)));
            fileStatus = fs.getFileStatus(testFileInodePath);
            Assert.assertEquals(3, fileStatus.getReplication());
            fs.setReplication(testFileInodePath, ((short) (1)));
            // ClientProtocol#getPreferredBlockSize
            Assert.assertEquals(testFileBlockSize, nnRpc.getPreferredBlockSize(testFileInodePath.toString()));
            /* HDFS-6749 added missing calls to FSDirectory.resolvePath in the
            following four methods. The calls below ensure that
            /.reserved/.inodes paths work properly. No need to check return
            values as these methods are tested elsewhere.
             */
            {
                fs.isFileClosed(testFileInodePath);
                fs.getAclStatus(testFileInodePath);
                fs.getXAttrs(testFileInodePath);
                fs.listXAttrs(testFileInodePath);
                fs.access(testFileInodePath, READ_WRITE);
            }
            // symbolic link related tests
            // Reserved path is not allowed as a target
            String invalidTarget = new Path(baseDir, "invalidTarget").toString();
            String link = new Path(baseDir, "link").toString();
            testInvalidSymlinkTarget(nnRpc, invalidTarget, link);
            // Test creating a link using reserved inode path
            String validTarget = "/validtarget";
            testValidSymlinkTarget(nnRpc, validTarget, link);
            // FileSystem#append
            fs.append(testFileInodePath);
            // DistributedFileSystem#recoverLease
            fs.recoverLease(testFileInodePath);
            // Namenode#getBlockLocations
            LocatedBlocks l1 = nnRpc.getBlockLocations(testFileInodePath.toString(), 0, Long.MAX_VALUE);
            LocatedBlocks l2 = nnRpc.getBlockLocations(testFileRegularPath.toString(), 0, Long.MAX_VALUE);
            TestINodeFile.checkEquals(l1, l2);
            // FileSystem#rename - both the variants
            Path renameDst = getInodePath(baseDirFileId, "test2");
            fileStatus = fs.getFileStatus(testFileInodePath);
            // Rename variant 1: rename and rename bacck
            fs.rename(testFileInodePath, renameDst);
            fs.rename(renameDst, testFileInodePath);
            Assert.assertEquals(fileStatus, fs.getFileStatus(testFileInodePath));
            // Rename variant 2: rename and rename bacck
            fs.rename(testFileInodePath, renameDst, Rename.OVERWRITE);
            fs.rename(renameDst, testFileInodePath, Rename.OVERWRITE);
            Assert.assertEquals(fileStatus, fs.getFileStatus(testFileInodePath));
            // FileSystem#getContentSummary
            Assert.assertEquals(fs.getContentSummary(testFileRegularPath).toString(), fs.getContentSummary(testFileInodePath).toString());
            // FileSystem#listFiles
            TestINodeFile.checkEquals(fs.listFiles(baseDirRegPath, false), fs.listFiles(baseDir, false));
            // FileSystem#delete
            fs.delete(testFileInodePath, true);
            Assert.assertFalse(fs.exists(testFileInodePath));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Check /.reserved path is reserved and cannot be created.
     */
    @Test
    public void testReservedFileNames() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            // First start a cluster with reserved file names check turned off
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // Creation of directory or file with reserved path names is disallowed
            ensureReservedFileNamesCannotBeCreated(fs, "/.reserved", false);
            ensureReservedFileNamesCannotBeCreated(fs, "/.reserved", false);
            Path reservedPath = new Path("/.reserved");
            // Loading of fsimage or editlog with /.reserved directory should fail
            // Mkdir "/.reserved reserved path with reserved path check turned off
            CHECK_RESERVED_FILE_NAMES = false;
            fs.mkdirs(reservedPath);
            Assert.assertTrue(fs.isDirectory(reservedPath));
            ensureReservedFileNamesCannotBeLoaded(cluster);
            // Loading of fsimage or editlog with /.reserved file should fail
            // Create file "/.reserved reserved path with reserved path check turned off
            CHECK_RESERVED_FILE_NAMES = false;
            ensureClusterRestartSucceeds(cluster);
            fs.delete(reservedPath, true);
            DFSTestUtil.createFile(fs, reservedPath, 10, ((short) (1)), 0L);
            Assert.assertTrue((!(fs.isDirectory(reservedPath))));
            ensureReservedFileNamesCannotBeLoaded(cluster);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test for {@link FSDirectory#getPathComponents(INode)}
     */
    @Test
    public void testGetPathFromInode() throws QuotaExceededException {
        String path = "/a/b/c";
        INode inode = createTreeOfInodes(path);
        byte[][] expected = INode.getPathComponents(path);
        byte[][] actual = FSDirectory.getPathComponents(inode);
        DFSTestUtil.checkComponentsEquals(expected, actual);
    }

    /**
     * Tests for {@link FSDirectory#resolvePath(String, FSDirectory)}
     */
    @Test
    public void testInodePath() throws IOException {
        // For a non .inodes path the regular components are returned
        String path = "/a/b/c";
        INode inode = createTreeOfInodes(path);
        // For an any inode look up return inode corresponding to "c" from /a/b/c
        FSDirectory fsd = Mockito.mock(FSDirectory.class);
        Mockito.doReturn(inode).when(fsd).getInode(Mockito.anyLong());
        // Tests for FSDirectory#resolvePath()
        // Non inode regular path
        String resolvedPath = FSDirectory.resolvePath(path, fsd);
        Assert.assertEquals(path, resolvedPath);
        // Inode path with no trailing separator
        String testPath = "/.reserved/.inodes/1";
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals(path, resolvedPath);
        // Inode path with trailing separator
        testPath = "/.reserved/.inodes/1/";
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals(path, resolvedPath);
        // Inode relative path
        testPath = "/.reserved/.inodes/1/d/e/f";
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals("/a/b/c/d/e/f", resolvedPath);
        // A path with just .inodes  returns the path as is
        testPath = "/.reserved/.inodes";
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals(testPath, resolvedPath);
        // Root inode path
        testPath = "/.reserved/.inodes/" + (ROOT_INODE_ID);
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals("/", resolvedPath);
        // An invalid inode path should remain unresolved
        testPath = "/.invalid/.inodes/1";
        resolvedPath = FSDirectory.resolvePath(testPath, fsd);
        Assert.assertEquals(testPath, resolvedPath);
        // Test path with nonexistent(deleted or wrong id) inode
        Mockito.doReturn(null).when(fsd).getInode(Mockito.anyLong());
        testPath = "/.reserved/.inodes/1234";
        try {
            String realPath = FSDirectory.resolvePath(testPath, fsd);
            Assert.fail(("Path should not be resolved:" + realPath));
        } catch (IOException e) {
            Assert.assertTrue((e instanceof FileNotFoundException));
        }
    }

    /**
     * Test whether the inode in inodeMap has been replaced after regular inode
     * replacement
     */
    @Test
    public void testInodeReplacement() throws Exception {
        final Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final DistributedFileSystem hdfs = cluster.getFileSystem();
            final FSDirectory fsdir = cluster.getNamesystem().getFSDirectory();
            final Path dir = new Path("/dir");
            hdfs.mkdirs(dir);
            INodeDirectory dirNode = TestINodeFile.getDir(fsdir, dir);
            INode dirNodeFromNode = fsdir.getInode(dirNode.getId());
            Assert.assertSame(dirNode, dirNodeFromNode);
            // set quota to dir, which leads to node replacement
            hdfs.setQuota(dir, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
            dirNode = TestINodeFile.getDir(fsdir, dir);
            Assert.assertTrue(dirNode.isWithQuota());
            // the inode in inodeMap should also be replaced
            dirNodeFromNode = fsdir.getInode(dirNode.getId());
            Assert.assertSame(dirNode, dirNodeFromNode);
            hdfs.setQuota(dir, (-1), (-1));
            dirNode = TestINodeFile.getDir(fsdir, dir);
            // the inode in inodeMap should also be replaced
            dirNodeFromNode = fsdir.getInode(dirNode.getId());
            Assert.assertSame(dirNode, dirNodeFromNode);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testDotdotInodePath() throws Exception {
        final Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        DFSClient client = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final DistributedFileSystem hdfs = cluster.getFileSystem();
            final FSDirectory fsdir = cluster.getNamesystem().getFSDirectory();
            final Path dir = new Path("/dir");
            hdfs.mkdirs(dir);
            long dirId = fsdir.getINode(dir.toString()).getId();
            long parentId = fsdir.getINode("/").getId();
            String testPath = ("/.reserved/.inodes/" + dirId) + "/..";
            client = new DFSClient(DFSUtilClient.getNNAddress(conf), conf);
            HdfsFileStatus status = client.getFileInfo(testPath);
            Assert.assertTrue((parentId == (status.getFileId())));
            // Test root's parent is still root
            testPath = ("/.reserved/.inodes/" + parentId) + "/..";
            status = client.getFileInfo(testPath);
            Assert.assertTrue((parentId == (status.getFileId())));
        } finally {
            IOUtils.cleanupWithLogger(TestINodeFile.LOG, client);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testLocationLimitInListingOps() throws Exception {
        final Configuration conf = new Configuration();
        conf.setInt(DFS_LIST_LIMIT, 9);// 3 blocks * 3 replicas

        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
            cluster.waitActive();
            final DistributedFileSystem hdfs = cluster.getFileSystem();
            ArrayList<String> source = new ArrayList<String>();
            // tmp1 holds files with 3 blocks, 3 replicas
            // tmp2 holds files with 3 blocks, 1 replica
            hdfs.mkdirs(new Path("/tmp1"));
            hdfs.mkdirs(new Path("/tmp2"));
            source.add("f1");
            source.add("f2");
            int numEntries = source.size();
            for (int j = 0; j < numEntries; j++) {
                DFSTestUtil.createFile(hdfs, new Path(("/tmp1/" + (source.get(j)))), 4096, ((3 * 1024) - 100), 1024, ((short) (3)), 0);
            }
            byte[] start = HdfsFileStatus.EMPTY_NAME;
            for (int j = 0; j < numEntries; j++) {
                DirectoryListing dl = cluster.getNameNodeRpc().getListing("/tmp1", start, true);
                Assert.assertTrue(((dl.getPartialListing().length) == 1));
                for (int i = 0; i < (dl.getPartialListing().length); i++) {
                    source.remove(dl.getPartialListing()[i].getLocalName());
                }
                start = dl.getLastName();
            }
            // Verify we have listed all entries in the directory.
            Assert.assertTrue(((source.size()) == 0));
            // Now create 6 files, each with 3 locations. Should take 2 iterations of 3
            source.add("f1");
            source.add("f2");
            source.add("f3");
            source.add("f4");
            source.add("f5");
            source.add("f6");
            numEntries = source.size();
            for (int j = 0; j < numEntries; j++) {
                DFSTestUtil.createFile(hdfs, new Path(("/tmp2/" + (source.get(j)))), 4096, ((3 * 1024) - 100), 1024, ((short) (1)), 0);
            }
            start = HdfsFileStatus.EMPTY_NAME;
            for (int j = 0; j < (numEntries / 3); j++) {
                DirectoryListing dl = cluster.getNameNodeRpc().getListing("/tmp2", start, true);
                Assert.assertTrue(((dl.getPartialListing().length) == 3));
                for (int i = 0; i < (dl.getPartialListing().length); i++) {
                    source.remove(dl.getPartialListing()[i].getLocalName());
                }
                start = dl.getLastName();
            }
            // Verify we have listed all entries in tmp2.
            Assert.assertTrue(((source.size()) == 0));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testFilesInGetListingOps() throws Exception {
        final Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final DistributedFileSystem hdfs = cluster.getFileSystem();
            final FSDirectory fsdir = cluster.getNamesystem().getFSDirectory();
            hdfs.mkdirs(new Path("/tmp"));
            DFSTestUtil.createFile(hdfs, new Path("/tmp/f1"), 0, ((short) (1)), 0);
            DFSTestUtil.createFile(hdfs, new Path("/tmp/f2"), 0, ((short) (1)), 0);
            DFSTestUtil.createFile(hdfs, new Path("/tmp/f3"), 0, ((short) (1)), 0);
            DirectoryListing dl = cluster.getNameNodeRpc().getListing("/tmp", EMPTY_NAME, false);
            Assert.assertTrue(((dl.getPartialListing().length) == 3));
            String f2 = new String("f2");
            dl = cluster.getNameNodeRpc().getListing("/tmp", f2.getBytes(), false);
            Assert.assertTrue(((dl.getPartialListing().length) == 1));
            INode f2INode = fsdir.getINode("/tmp/f2");
            String f2InodePath = "/.reserved/.inodes/" + (f2INode.getId());
            dl = cluster.getNameNodeRpc().getListing("/tmp", f2InodePath.getBytes(), false);
            Assert.assertTrue(((dl.getPartialListing().length) == 1));
            // Test the deleted startAfter file
            hdfs.delete(new Path("/tmp/f2"), false);
            try {
                dl = cluster.getNameNodeRpc().getListing("/tmp", f2InodePath.getBytes(), false);
                Assert.fail("Didn't get exception for the deleted startAfter token.");
            } catch (IOException e) {
                Assert.assertTrue((e instanceof DirectoryListingStartAfterNotFoundException));
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testFileUnderConstruction() {
        replication = 3;
        final INodeFile file = new INodeFile(HdfsConstants.GRANDFATHER_INODE_ID, null, TestINodeFile.perm, 0L, 0L, null, replication, 1024L);
        Assert.assertFalse(file.isUnderConstruction());
        final String clientName = "client";
        final String clientMachine = "machine";
        file.toUnderConstruction(clientName, clientMachine);
        Assert.assertTrue(file.isUnderConstruction());
        FileUnderConstructionFeature uc = file.getFileUnderConstructionFeature();
        Assert.assertEquals(clientName, uc.getClientName());
        Assert.assertEquals(clientMachine, uc.getClientMachine());
        TestINodeFile.toCompleteFile(file);
        Assert.assertFalse(file.isUnderConstruction());
    }

    @Test
    public void testXAttrFeature() {
        replication = 3;
        preferredBlockSize = (128 * 1024) * 1024;
        INodeFile inf = createINodeFile(replication, preferredBlockSize);
        ImmutableList.Builder<XAttr> builder = new ImmutableList.Builder<XAttr>();
        XAttr xAttr = new XAttr.Builder().setNameSpace(USER).setName("a1").setValue(new byte[]{ 49, 50, 51 }).build();
        builder.add(xAttr);
        XAttrFeature f = new XAttrFeature(builder.build());
        inf.addXAttrFeature(f);
        XAttrFeature f1 = inf.getXAttrFeature();
        Assert.assertEquals(xAttr, f1.getXAttrs().get(0));
        inf.removeXAttrFeature();
        f1 = inf.getXAttrFeature();
        Assert.assertEquals(f1, null);
    }

    @Test
    public void testClearBlocks() {
        INodeFile toBeCleared = createINodeFiles(1, "toBeCleared")[0];
        Assert.assertEquals(1, toBeCleared.getBlocks().length);
        toBeCleared.clearBlocks();
        Assert.assertTrue(((toBeCleared.getBlocks().length) == 0));
    }
}

