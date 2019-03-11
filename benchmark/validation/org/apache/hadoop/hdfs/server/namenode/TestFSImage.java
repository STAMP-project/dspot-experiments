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


import BlockType.CONTIGUOUS;
import BlockUCState.UNDER_CONSTRUCTION;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_IMAGE_COMPRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY;
import ErasureCodingPolicyState.DISABLED;
import INodeSection.INodeFile.Builder;
import NameNodeDirType.IMAGE;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import StartupOption.UPGRADE;
import SyncFlag.UPDATE_LENGTH;
import SystemErasureCodingPolicies.RS_10_4_POLICY_ID;
import SystemErasureCodingPolicies.RS_3_2_POLICY_ID;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.AddErasureCodingPolicyResponse;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.BlockType;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.protocolPB.PBHelperClient;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoContiguous;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection;
import org.apache.hadoop.hdfs.server.namenode.LeaseManager.Lease;
import org.apache.hadoop.hdfs.util.MD5FileUtils;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestFSImage {
    private static final String HADOOP_2_7_ZER0_BLOCK_SIZE_TGZ = "image-with-zero-block-size.tar.gz";

    private static final ErasureCodingPolicy testECPolicy = SystemErasureCodingPolicies.getByID(RS_10_4_POLICY_ID);

    @Test
    public void testPersist() throws IOException {
        Configuration conf = new Configuration();
        testPersistHelper(conf);
    }

    @Test
    public void testCompression() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_IMAGE_COMPRESS_KEY, true);
        setCompressCodec(conf, "org.apache.hadoop.io.compress.DefaultCodec");
        setCompressCodec(conf, "org.apache.hadoop.io.compress.GzipCodec");
        setCompressCodec(conf, "org.apache.hadoop.io.compress.BZip2Codec");
    }

    @Test
    public void testNativeCompression() throws IOException {
        Assume.assumeTrue(NativeCodeLoader.isNativeCodeLoaded());
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_IMAGE_COMPRESS_KEY, true);
        setCompressCodec(conf, "org.apache.hadoop.io.compress.Lz4Codec");
    }

    /**
     * Test if a INodeFile with BlockInfoStriped can be saved by
     * FSImageSerialization and loaded by FSImageFormat#Loader.
     */
    @Test
    public void testSaveAndLoadStripedINodeFile() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            DFSTestUtil.enableAllECPolicies(cluster.getFileSystem());
            testSaveAndLoadStripedINodeFile(cluster.getNamesystem(), conf, false);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test if a INodeFileUnderConstruction with BlockInfoStriped can be
     * saved and loaded by FSImageSerialization
     */
    @Test
    public void testSaveAndLoadStripedINodeFileUC() throws IOException {
        // construct a INode with StripedBlock for saving and loading
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            DFSTestUtil.enableAllECPolicies(cluster.getFileSystem());
            testSaveAndLoadStripedINodeFile(cluster.getNamesystem(), conf, true);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * On checkpointing , stale fsimage checkpoint file should be deleted.
     */
    @Test
    public void testRemovalStaleFsimageCkpt() throws IOException {
        MiniDFSCluster cluster = null;
        SecondaryNameNode secondary = null;
        Configuration conf = new HdfsConfiguration();
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).format(true).build();
            conf.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "0.0.0.0:0");
            secondary = new SecondaryNameNode(conf);
            // Do checkpointing
            secondary.doCheckpoint();
            NNStorage storage = secondary.getFSImage().storage;
            File currentDir = FSImageTestUtil.getCurrentDirs(storage, IMAGE).get(0);
            // Create a stale fsimage.ckpt file
            File staleCkptFile = new File(((currentDir.getPath()) + "/fsimage.ckpt_0000000000000000002"));
            staleCkptFile.createNewFile();
            Assert.assertTrue(staleCkptFile.exists());
            // After checkpoint stale fsimage.ckpt file should be deleted
            secondary.doCheckpoint();
            Assert.assertFalse(staleCkptFile.exists());
        } finally {
            if (secondary != null) {
                secondary.shutdown();
                secondary = null;
            }
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
    }

    /**
     * Ensure that the digest written by the saver equals to the digest of the
     * file.
     */
    @Test
    public void testDigest() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            DistributedFileSystem fs = cluster.getFileSystem();
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            File currentDir = FSImageTestUtil.getNameNodeCurrentDirs(cluster, 0).get(0);
            File fsimage = FSImageTestUtil.findNewestImageFile(currentDir.getAbsolutePath());
            Assert.assertEquals(MD5FileUtils.readStoredMd5ForFile(fsimage), MD5FileUtils.computeMd5ForFile(fsimage));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Ensure mtime and atime can be loaded from fsimage.
     */
    @Test(timeout = 60000)
    public void testLoadMtimeAtime() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            DistributedFileSystem hdfs = cluster.getFileSystem();
            String userDir = hdfs.getHomeDirectory().toUri().getPath().toString();
            Path file = new Path(userDir, "file");
            Path dir = new Path(userDir, "/dir");
            Path link = new Path(userDir, "/link");
            hdfs.createNewFile(file);
            hdfs.mkdirs(dir);
            hdfs.createSymlink(file, link, false);
            long mtimeFile = hdfs.getFileStatus(file).getModificationTime();
            long atimeFile = hdfs.getFileStatus(file).getAccessTime();
            long mtimeDir = hdfs.getFileStatus(dir).getModificationTime();
            long mtimeLink = hdfs.getFileLinkStatus(link).getModificationTime();
            long atimeLink = hdfs.getFileLinkStatus(link).getAccessTime();
            // save namespace and restart cluster
            hdfs.setSafeMode(HdfsConstants.SafeModeAction.SAFEMODE_ENTER);
            hdfs.saveNamespace();
            hdfs.setSafeMode(HdfsConstants.SafeModeAction.SAFEMODE_LEAVE);
            cluster.shutdown();
            cluster = new MiniDFSCluster.Builder(conf).format(false).numDataNodes(1).build();
            cluster.waitActive();
            hdfs = cluster.getFileSystem();
            Assert.assertEquals(mtimeFile, hdfs.getFileStatus(file).getModificationTime());
            Assert.assertEquals(atimeFile, hdfs.getFileStatus(file).getAccessTime());
            Assert.assertEquals(mtimeDir, hdfs.getFileStatus(dir).getModificationTime());
            Assert.assertEquals(mtimeLink, hdfs.getFileLinkStatus(link).getModificationTime());
            Assert.assertEquals(atimeLink, hdfs.getFileLinkStatus(link).getAccessTime());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Ensure ctime is set during namenode formatting.
     */
    @Test(timeout = 60000)
    public void testCtime() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            final long pre = Time.now();
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final long post = Time.now();
            final long ctime = cluster.getNamesystem().getCTime();
            Assert.assertTrue((pre <= ctime));
            Assert.assertTrue((ctime <= post));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * In this test case, I have created an image with a file having
     * preferredblockSize = 0. We are trying to read this image (since file with
     * preferredblockSize = 0 was allowed pre 2.1.0-beta version. The namenode
     * after 2.6 version will not be able to read this particular file.
     * See HDFS-7788 for more information.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testZeroBlockSize() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        String tarFile = ((System.getProperty("test.cache.data", "build/test/cache")) + "/") + (TestFSImage.HADOOP_2_7_ZER0_BLOCK_SIZE_TGZ);
        String testDir = PathUtils.getTestDirName(getClass());
        File dfsDir = new File(testDir, "image-with-zero-block-size");
        if ((dfsDir.exists()) && (!(FileUtil.fullyDelete(dfsDir)))) {
            throw new IOException((("Could not delete dfs directory '" + dfsDir) + "'"));
        }
        FileUtil.unTar(new File(tarFile), new File(testDir));
        File nameDir = new File(dfsDir, "name");
        GenericTestUtils.assertExists(nameDir);
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, nameDir.getAbsolutePath());
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).waitSafeMode(false).startupOption(UPGRADE).build();
        try {
            FileSystem fs = cluster.getFileSystem();
            Path testPath = new Path("/tmp/zeroBlockFile");
            Assert.assertTrue("File /tmp/zeroBlockFile doesn't exist ", fs.exists(testPath));
            Assert.assertTrue("Name node didn't come up", cluster.isNameNodeUp(0));
        } finally {
            cluster.shutdown();
            // Clean up
            FileUtil.fullyDelete(dfsDir);
        }
    }

    /**
     * Ensure that FSImage supports BlockGroup.
     */
    @Test(timeout = 60000)
    public void testSupportBlockGroup() throws Exception {
        final short GROUP_SIZE = ((short) ((TestFSImage.testECPolicy.getNumDataUnits()) + (TestFSImage.testECPolicy.getNumParityUnits())));
        final int BLOCK_SIZE = (8 * 1024) * 1024;
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, BLOCK_SIZE);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(GROUP_SIZE).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            DFSTestUtil.enableAllECPolicies(fs);
            Path parentDir = new Path("/ec-10-4");
            Path childDir = new Path(parentDir, "ec-3-2");
            ErasureCodingPolicy ec32Policy = SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID);
            // Create directories and files
            fs.mkdirs(parentDir);
            fs.mkdirs(childDir);
            fs.setErasureCodingPolicy(parentDir, TestFSImage.testECPolicy.getName());
            fs.setErasureCodingPolicy(childDir, ec32Policy.getName());
            Path file_10_4 = new Path(parentDir, "striped_file_10_4");
            Path file_3_2 = new Path(childDir, "striped_file_3_2");
            // Write content to files
            byte[] bytes = StripedFileTestUtil.generateBytes(BLOCK_SIZE);
            DFSTestUtil.writeFile(fs, file_10_4, new String(bytes));
            DFSTestUtil.writeFile(fs, file_3_2, new String(bytes));
            // Save namespace and restart NameNode
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNodes();
            fs = cluster.getFileSystem();
            Assert.assertTrue(fs.exists(file_10_4));
            Assert.assertTrue(fs.exists(file_3_2));
            // check the information of file_10_4
            FSNamesystem fsn = cluster.getNamesystem();
            INodeFile inode = fsn.dir.getINode(file_10_4.toString()).asFile();
            Assert.assertTrue(inode.isStriped());
            Assert.assertEquals(TestFSImage.testECPolicy.getId(), inode.getErasureCodingPolicyID());
            BlockInfo[] blks = inode.getBlocks();
            Assert.assertEquals(1, blks.length);
            Assert.assertTrue(blks[0].isStriped());
            Assert.assertEquals(TestFSImage.testECPolicy.getId(), fs.getErasureCodingPolicy(file_10_4).getId());
            Assert.assertEquals(TestFSImage.testECPolicy.getId(), getErasureCodingPolicy().getId());
            Assert.assertEquals(TestFSImage.testECPolicy.getNumDataUnits(), getDataBlockNum());
            Assert.assertEquals(TestFSImage.testECPolicy.getNumParityUnits(), getParityBlockNum());
            byte[] content = DFSTestUtil.readFileAsBytes(fs, file_10_4);
            Assert.assertArrayEquals(bytes, content);
            // check the information of file_3_2
            inode = fsn.dir.getINode(file_3_2.toString()).asFile();
            Assert.assertTrue(inode.isStriped());
            Assert.assertEquals(SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID).getId(), inode.getErasureCodingPolicyID());
            blks = inode.getBlocks();
            Assert.assertEquals(1, blks.length);
            Assert.assertTrue(blks[0].isStriped());
            Assert.assertEquals(ec32Policy.getId(), fs.getErasureCodingPolicy(file_3_2).getId());
            Assert.assertEquals(ec32Policy.getNumDataUnits(), getDataBlockNum());
            Assert.assertEquals(ec32Policy.getNumParityUnits(), getParityBlockNum());
            content = DFSTestUtil.readFileAsBytes(fs, file_3_2);
            Assert.assertArrayEquals(bytes, content);
            // check the EC policy on parent Dir
            ErasureCodingPolicy ecPolicy = fsn.getErasureCodingPolicy(parentDir.toString());
            Assert.assertNotNull(ecPolicy);
            Assert.assertEquals(TestFSImage.testECPolicy.getId(), ecPolicy.getId());
            // check the EC policy on child Dir
            ecPolicy = fsn.getErasureCodingPolicy(childDir.toString());
            Assert.assertNotNull(ecPolicy);
            Assert.assertEquals(ec32Policy.getId(), ecPolicy.getId());
            // check the EC policy on root directory
            ecPolicy = fsn.getErasureCodingPolicy("/");
            Assert.assertNull(ecPolicy);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testHasNonEcBlockUsingStripedIDForLoadFile() throws IOException {
        // start a cluster
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(9).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            FSNamesystem fns = cluster.getNamesystem();
            String testDir = "/test_block_manager";
            String testFile = "testfile_loadfile";
            String testFilePath = (testDir + "/") + testFile;
            String clientName = "testUser_loadfile";
            String clientMachine = "testMachine_loadfile";
            long blkId = -1;
            long blkNumBytes = 1024;
            long timestamp = 1426222918;
            fs.mkdir(new Path(testDir), new FsPermission("755"));
            Path p = new Path(testFilePath);
            DFSTestUtil.createFile(fs, p, 0, ((short) (1)), 1);
            BlockInfoContiguous cBlk = new BlockInfoContiguous(new Block(blkId, blkNumBytes, timestamp), ((short) (3)));
            INodeFile file = ((INodeFile) (fns.getFSDirectory().getINode(testFilePath)));
            file.toUnderConstruction(clientName, clientMachine);
            file.addBlock(cBlk);
            TestINodeFile.toCompleteFile(file);
            fns.enterSafeMode(false);
            fns.saveNamespace(0, 0);
            cluster.restartNameNodes();
            cluster.waitActive();
            fns = cluster.getNamesystem();
            Assert.assertTrue(fns.getBlockManager().hasNonEcBlockUsingStripedID());
            // after nonEcBlockUsingStripedID is deleted
            // the hasNonEcBlockUsingStripedID is set to false
            fs = cluster.getFileSystem();
            fs.delete(p, false);
            fns.enterSafeMode(false);
            fns.saveNamespace(0, 0);
            cluster.restartNameNodes();
            cluster.waitActive();
            fns = cluster.getNamesystem();
            Assert.assertFalse(fns.getBlockManager().hasNonEcBlockUsingStripedID());
            cluster.shutdown();
            cluster = null;
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testHasNonEcBlockUsingStripedIDForLoadUCFile() throws IOException {
        // start a cluster
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(9).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            FSNamesystem fns = cluster.getNamesystem();
            String testDir = "/test_block_manager";
            String testFile = "testfile_loaducfile";
            String testFilePath = (testDir + "/") + testFile;
            String clientName = "testUser_loaducfile";
            String clientMachine = "testMachine_loaducfile";
            long blkId = -1;
            long blkNumBytes = 1024;
            long timestamp = 1426222918;
            fs.mkdir(new Path(testDir), new FsPermission("755"));
            Path p = new Path(testFilePath);
            DFSTestUtil.createFile(fs, p, 0, ((short) (1)), 1);
            BlockInfoContiguous cBlk = new BlockInfoContiguous(new Block(blkId, blkNumBytes, timestamp), ((short) (3)));
            INodeFile file = ((INodeFile) (fns.getFSDirectory().getINode(testFilePath)));
            file.toUnderConstruction(clientName, clientMachine);
            file.addBlock(cBlk);
            fns.enterSafeMode(false);
            fns.saveNamespace(0, 0);
            cluster.restartNameNodes();
            cluster.waitActive();
            fns = cluster.getNamesystem();
            Assert.assertTrue(fns.getBlockManager().hasNonEcBlockUsingStripedID());
            cluster.shutdown();
            cluster = null;
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testHasNonEcBlockUsingStripedIDForLoadSnapshot() throws IOException {
        // start a cluster
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(9).build();
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            FSNamesystem fns = cluster.getNamesystem();
            String testDir = "/test_block_manager";
            String testFile = "testfile_loadSnapshot";
            String testFilePath = (testDir + "/") + testFile;
            String clientName = "testUser_loadSnapshot";
            String clientMachine = "testMachine_loadSnapshot";
            long blkId = -1;
            long blkNumBytes = 1024;
            long timestamp = 1426222918;
            Path d = new Path(testDir);
            fs.mkdir(d, new FsPermission("755"));
            fs.allowSnapshot(d);
            Path p = new Path(testFilePath);
            DFSTestUtil.createFile(fs, p, 0, ((short) (1)), 1);
            BlockInfoContiguous cBlk = new BlockInfoContiguous(new Block(blkId, blkNumBytes, timestamp), ((short) (3)));
            INodeFile file = ((INodeFile) (fns.getFSDirectory().getINode(testFilePath)));
            file.toUnderConstruction(clientName, clientMachine);
            file.addBlock(cBlk);
            TestINodeFile.toCompleteFile(file);
            fs.createSnapshot(d, "testHasNonEcBlockUsingStripeID");
            fs.truncate(p, 0);
            fns.enterSafeMode(false);
            fns.saveNamespace(0, 0);
            cluster.restartNameNodes();
            cluster.waitActive();
            fns = cluster.getNamesystem();
            Assert.assertTrue(fns.getBlockManager().hasNonEcBlockUsingStripedID());
            cluster.shutdown();
            cluster = null;
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testBlockTypeProtoDefaultsToContiguous() throws Exception {
        INodeSection.INodeFile.Builder builder = INodeSection.INodeFile.newBuilder();
        INodeSection.INodeFile inodeFile = builder.build();
        BlockType defaultBlockType = PBHelperClient.convert(inodeFile.getBlockType());
        Assert.assertEquals(defaultBlockType, CONTIGUOUS);
    }

    /**
     * Test if a INodeFile under a replication EC policy directory
     * can be saved by FSImageSerialization and loaded by FSImageFormat#Loader.
     */
    @Test
    public void testSaveAndLoadFileUnderReplicationPolicyDir() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            FSNamesystem fsn = cluster.getNamesystem();
            DistributedFileSystem fs = cluster.getFileSystem();
            DFSTestUtil.enableAllECPolicies(fs);
            ErasureCodingPolicy replicaPolicy = SystemErasureCodingPolicies.getReplicationPolicy();
            ErasureCodingPolicy defaultEcPolicy = StripedFileTestUtil.getDefaultECPolicy();
            final Path ecDir = new Path("/ec");
            final Path replicaDir = new Path(ecDir, "replica");
            final Path replicaFile1 = new Path(replicaDir, "f1");
            final Path replicaFile2 = new Path(replicaDir, "f2");
            // create root directory
            fs.mkdir(ecDir, null);
            fs.setErasureCodingPolicy(ecDir, defaultEcPolicy.getName());
            // create directory, and set replication Policy
            fs.mkdir(replicaDir, null);
            fs.setErasureCodingPolicy(replicaDir, replicaPolicy.getName());
            // create an empty file f1
            fs.create(replicaFile1).close();
            // create an under-construction file f2
            FSDataOutputStream out = fs.create(replicaFile2, ((short) (2)));
            out.writeBytes("hello");
            ((DFSOutputStream) (out.getWrappedStream())).hsync(EnumSet.of(UPDATE_LENGTH));
            // checkpoint
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNode();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            Assert.assertTrue(fs.getFileStatus(ecDir).isDirectory());
            Assert.assertTrue(fs.getFileStatus(replicaDir).isDirectory());
            Assert.assertTrue(fs.exists(replicaFile1));
            Assert.assertTrue(fs.exists(replicaFile2));
            // check directories
            Assert.assertEquals("Directory should have default EC policy.", defaultEcPolicy, fs.getErasureCodingPolicy(ecDir));
            Assert.assertEquals("Directory should hide replication EC policy.", null, fs.getErasureCodingPolicy(replicaDir));
            // check file1
            Assert.assertEquals("File should not have EC policy.", null, fs.getErasureCodingPolicy(replicaFile1));
            // check internals of file2
            INodeFile file2Node = fsn.dir.getINode4Write(replicaFile2.toString()).asFile();
            Assert.assertEquals("hello".length(), file2Node.computeFileSize());
            Assert.assertTrue(file2Node.isUnderConstruction());
            BlockInfo[] blks = file2Node.getBlocks();
            Assert.assertEquals(1, blks.length);
            Assert.assertEquals(UNDER_CONSTRUCTION, blks[0].getBlockUCState());
            Assert.assertEquals("File should return expected replication factor.", 2, blks[0].getReplication());
            Assert.assertEquals("File should not have EC policy.", null, fs.getErasureCodingPolicy(replicaFile2));
            // check lease manager
            Lease lease = fsn.leaseManager.getLease(file2Node);
            Assert.assertNotNull(lease);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test persist and load erasure coding policies.
     */
    @Test
    public void testSaveAndLoadErasureCodingPolicies() throws IOException {
        Configuration conf = new Configuration();
        final int blockSize = (16 * 1024) * 1024;
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(10).build()) {
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            DFSTestUtil.enableAllECPolicies(fs);
            // Save namespace and restart NameNode
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNodes();
            cluster.waitActive();
            Assert.assertEquals("Erasure coding policy number should match", SystemErasureCodingPolicies.getPolicies().size(), ErasureCodingPolicyManager.getInstance().getPolicies().length);
            // Add new erasure coding policy
            ECSchema newSchema = new ECSchema("rs", 5, 4);
            ErasureCodingPolicy newPolicy = new ErasureCodingPolicy(newSchema, (2 * 1024), ((byte) (254)));
            ErasureCodingPolicy[] policies = new ErasureCodingPolicy[]{ newPolicy };
            AddErasureCodingPolicyResponse[] ret = fs.addErasureCodingPolicies(policies);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(true, ret[0].isSucceed());
            newPolicy = ret[0].getPolicy();
            // Save namespace and restart NameNode
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNodes();
            cluster.waitActive();
            Assert.assertEquals("Erasure coding policy number should match", ((SystemErasureCodingPolicies.getPolicies().size()) + 1), ErasureCodingPolicyManager.getInstance().getPolicies().length);
            ErasureCodingPolicy ecPolicy = ErasureCodingPolicyManager.getInstance().getByID(newPolicy.getId());
            Assert.assertEquals("Newly added erasure coding policy is not found", newPolicy, ecPolicy);
            Assert.assertEquals("Newly added erasure coding policy should be of disabled state", DISABLED, DFSTestUtil.getECPolicyState(ecPolicy));
            // Test enable/disable/remove user customized erasure coding policy
            testChangeErasureCodingPolicyState(cluster, blockSize, newPolicy, false);
            // Test enable/disable default built-in erasure coding policy
            testChangeErasureCodingPolicyState(cluster, blockSize, SystemErasureCodingPolicies.getByID(((byte) (1))), true);
            // Test enable/disable non-default built-in erasure coding policy
            testChangeErasureCodingPolicyState(cluster, blockSize, SystemErasureCodingPolicies.getByID(((byte) (2))), false);
        }
    }
}

