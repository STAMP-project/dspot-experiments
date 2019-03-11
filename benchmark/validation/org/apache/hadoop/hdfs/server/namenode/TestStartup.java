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


import DFSConfigKeys.DFS_IMAGE_COMPRESSION_CODEC_KEY;
import DFSConfigKeys.DFS_IMAGE_COMPRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAX_XATTRS_PER_INODE_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAX_XATTR_SIZE_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_MAX_XATTR_SIZE_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import DatanodeReportType.LIVE;
import ExitUtil.EXIT_EXCEPTION_MESSAGE;
import NameNodeDirType.IMAGE_AND_EDITS;
import NameNodeFile.EDITS;
import NameNodeFile.IMAGE;
import SafeModeAction.SAFEMODE_ENTER;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.common.InconsistentFSStateException;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.common.Util;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.ExitUtil.ExitException;
import org.apache.hadoop.util.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Startup and checkpoint tests
 */
public class TestStartup {
    public static final String NAME_NODE_HOST = "localhost:";

    public static final String WILDCARD_HTTP_HOST = "0.0.0.0:";

    private static final Logger LOG = LoggerFactory.getLogger(TestStartup.class.getName());

    private Configuration config;

    private File hdfsDir = null;

    static final long seed = 178958063L;

    static final int blockSize = 4096;

    static final int fileSize = 8192;

    private long editsLength = 0;

    private long fsimageLength = 0;

    /**
     * secnn-6
     * checkpoint for edits and image is the same directory
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testChkpointStartup2() throws IOException {
        TestStartup.LOG.info("--starting checkpointStartup2 - same directory for checkpoint");
        // different name dirs
        config.set(DFS_NAMENODE_NAME_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "name")).toString());
        config.set(DFS_NAMENODE_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "edits")).toString());
        // same checkpoint dirs
        config.set(DFS_NAMENODE_CHECKPOINT_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt")).toString());
        config.set(DFS_NAMENODE_CHECKPOINT_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt")).toString());
        createCheckPoint(1);
        corruptNameNodeFiles();
        checkNameNodeFiles();
    }

    /**
     * seccn-8
     * checkpoint for edits and image are different directories
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testChkpointStartup1() throws IOException {
        // setUpConfig();
        TestStartup.LOG.info("--starting testStartup Recovery");
        // different name dirs
        config.set(DFS_NAMENODE_NAME_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "name")).toString());
        config.set(DFS_NAMENODE_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "edits")).toString());
        // same checkpoint dirs
        config.set(DFS_NAMENODE_CHECKPOINT_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt_edits")).toString());
        config.set(DFS_NAMENODE_CHECKPOINT_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt")).toString());
        createCheckPoint(1);
        corruptNameNodeFiles();
        checkNameNodeFiles();
    }

    /**
     * secnn-7
     * secondary node copies fsimage and edits into correct separate directories.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSNNStartup() throws IOException {
        // setUpConfig();
        TestStartup.LOG.info("--starting SecondNN startup test");
        // different name dirs
        config.set(DFS_NAMENODE_NAME_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "name")).toString());
        config.set(DFS_NAMENODE_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "name")).toString());
        // same checkpoint dirs
        config.set(DFS_NAMENODE_CHECKPOINT_EDITS_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt_edits")).toString());
        config.set(DFS_NAMENODE_CHECKPOINT_DIR_KEY, Util.fileAsURI(new File(hdfsDir, "chkpt")).toString());
        TestStartup.LOG.info("--starting NN ");
        MiniDFSCluster cluster = null;
        SecondaryNameNode sn = null;
        NameNode nn = null;
        try {
            cluster = new MiniDFSCluster.Builder(config).manageDataDfsDirs(false).manageNameDfsDirs(false).build();
            cluster.waitActive();
            nn = cluster.getNameNode();
            Assert.assertNotNull(nn);
            // start secondary node
            TestStartup.LOG.info("--starting SecondNN");
            sn = new SecondaryNameNode(config);
            Assert.assertNotNull(sn);
            TestStartup.LOG.info("--doing checkpoint");
            sn.doCheckpoint();// this shouldn't fail

            TestStartup.LOG.info("--done checkpoint");
            // now verify that image and edits are created in the different directories
            FSImage image = nn.getFSImage();
            StorageDirectory sd = image.getStorage().getStorageDir(0);// only one

            Assert.assertEquals(sd.getStorageDirType(), IMAGE_AND_EDITS);
            image.getStorage();
            File imf = NNStorage.getStorageFile(sd, IMAGE, 0);
            image.getStorage();
            File edf = NNStorage.getStorageFile(sd, EDITS, 0);
            TestStartup.LOG.info(((("--image file " + (imf.getAbsolutePath())) + "; len = ") + (imf.length())));
            TestStartup.LOG.info(((("--edits file " + (edf.getAbsolutePath())) + "; len = ") + (edf.length())));
            FSImage chkpImage = sn.getFSImage();
            verifyDifferentDirs(chkpImage, imf.length(), edf.length());
        } catch (IOException e) {
            Assert.fail(StringUtils.stringifyException(e));
            System.err.println("checkpoint failed");
            throw e;
        } finally {
            if (sn != null)
                sn.shutdown();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test(timeout = 30000)
    public void testSNNStartupWithRuntimeException() throws Exception {
        String[] argv = new String[]{ "-checkpoint" };
        try {
            SecondaryNameNode.main(argv);
            Assert.fail("Failed to handle runtime exceptions during SNN startup!");
        } catch (ExitException ee) {
            GenericTestUtils.assertExceptionContains(EXIT_EXCEPTION_MESSAGE, ee);
            Assert.assertTrue("Didn't terminate properly ", ExitUtil.terminateCalled());
        }
    }

    @Test
    public void testCompression() throws IOException {
        TestStartup.LOG.info("Test compressing image.");
        Configuration conf = new Configuration();
        FileSystem.setDefaultUri(conf, "hdfs://localhost:0");
        conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, "127.0.0.1:0");
        File base_dir = new File(PathUtils.getTestDir(getClass()), "dfs/");
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, new File(base_dir, "name").getPath());
        conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
        DFSTestUtil.formatNameNode(conf);
        // create an uncompressed image
        TestStartup.LOG.info("Create an uncompressed fsimage");
        NameNode namenode = new NameNode(conf);
        namenode.getNamesystem().mkdirs("/test", new org.apache.hadoop.fs.permission.PermissionStatus("hairong", null, FsPermission.getDefault()), true);
        NamenodeProtocols nnRpc = namenode.getRpcServer();
        Assert.assertTrue(nnRpc.getFileInfo("/test").isDirectory());
        nnRpc.setSafeMode(SAFEMODE_ENTER, false);
        nnRpc.saveNamespace(0, 0);
        namenode.stop();
        namenode.join();
        namenode.joinHttpServer();
        // compress image using default codec
        TestStartup.LOG.info("Read an uncomressed image and store it compressed using default codec.");
        conf.setBoolean(DFS_IMAGE_COMPRESS_KEY, true);
        checkNameSpace(conf);
        // read image compressed using the default and compress it using Gzip codec
        TestStartup.LOG.info("Read a compressed image and store it using a different codec.");
        conf.set(DFS_IMAGE_COMPRESSION_CODEC_KEY, "org.apache.hadoop.io.compress.GzipCodec");
        checkNameSpace(conf);
        // read an image compressed in Gzip and store it uncompressed
        TestStartup.LOG.info("Read a compressed image and store it as uncompressed.");
        conf.setBoolean(DFS_IMAGE_COMPRESS_KEY, false);
        checkNameSpace(conf);
        // read an uncomrpessed image and store it uncompressed
        TestStartup.LOG.info("Read an uncompressed image and store it as uncompressed.");
        checkNameSpace(conf);
    }

    @Test
    public void testImageChecksum() throws Exception {
        TestStartup.LOG.info("Test uncompressed image checksum");
        testImageChecksum(false);
        TestStartup.LOG.info("Test compressed image checksum");
        testImageChecksum(true);
    }

    @Test(timeout = 30000)
    public void testCorruptImageFallback() throws IOException {
        // Create two checkpoints
        createCheckPoint(2);
        // Delete a single md5sum
        corruptFSImageMD5(false);
        // Should still be able to start
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(config).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).build();
        try {
            cluster.waitActive();
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testCorruptImageFallbackLostECPolicy() throws IOException {
        final ErasureCodingPolicy defaultPolicy = StripedFileTestUtil.getDefaultECPolicy();
        final String policy = defaultPolicy.getName();
        final Path f1 = new Path("/f1");
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(config).numDataNodes(0).format(true).build();
        try {
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            fs.enableErasureCodingPolicy(policy);
            // set root directory to use the default ec policy
            Path srcECDir = new Path("/");
            fs.setErasureCodingPolicy(srcECDir, defaultPolicy.getName());
            // create a file which will use the default ec policy
            fs.create(f1);
            FileStatus fs1 = fs.getFileStatus(f1);
            Assert.assertTrue(fs1.isErasureCoded());
            ErasureCodingPolicy fs1Policy = fs.getErasureCodingPolicy(f1);
            Assert.assertEquals(fs1Policy, defaultPolicy);
        } finally {
            cluster.close();
        }
        // Delete a single md5sum
        corruptFSImageMD5(false);
        // Should still be able to start
        cluster = new MiniDFSCluster.Builder(config).numDataNodes(0).format(false).build();
        try {
            cluster.waitActive();
            ErasureCodingPolicy[] ecPolicies = cluster.getNameNode().getNamesystem().getErasureCodingPolicyManager().getEnabledPolicies();
            DistributedFileSystem fs = cluster.getFileSystem();
            // make sure the ec policy of the file is still correct
            Assert.assertEquals(fs.getErasureCodingPolicy(f1), defaultPolicy);
            // make sure after fsimage fallback, enabled ec policies are not cleared.
            Assert.assertTrue(((ecPolicies.length) == 1));
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * This test tests hosts include list contains host names.  After namenode
     * restarts, the still alive datanodes should not have any trouble in getting
     * registrant again.
     */
    @Test
    public void testNNRestart() throws IOException, InterruptedException {
        MiniDFSCluster cluster = null;
        int HEARTBEAT_INTERVAL = 1;// heartbeat interval in seconds

        HostsFileWriter hostsFileWriter = new HostsFileWriter();
        hostsFileWriter.initialize(config, "work-dir/restartnn");
        byte[] b = new byte[]{ 127, 0, 0, 1 };
        InetAddress inetAddress = InetAddress.getByAddress(b);
        hostsFileWriter.initIncludeHosts(new String[]{ inetAddress.getHostName() });
        int numDatanodes = 1;
        try {
            cluster = new MiniDFSCluster.Builder(config).numDataNodes(numDatanodes).setupHostsFile(true).build();
            cluster.waitActive();
            cluster.restartNameNode();
            NamenodeProtocols nn = cluster.getNameNodeRpc();
            Assert.assertNotNull(nn);
            Assert.assertTrue(cluster.isDataNodeUp());
            DatanodeInfo[] info = nn.getDatanodeReport(LIVE);
            for (int i = 0; (i < 5) && ((info.length) != numDatanodes); i++) {
                Thread.sleep((HEARTBEAT_INTERVAL * 1000));
                info = nn.getDatanodeReport(LIVE);
            }
            Assert.assertEquals(("Number of live nodes should be " + numDatanodes), numDatanodes, info.length);
        } catch (IOException e) {
            Assert.fail(StringUtils.stringifyException(e));
            throw e;
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            hostsFileWriter.cleanup();
        }
    }

    @Test(timeout = 120000)
    public void testXattrConfiguration() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            conf.setInt(DFS_NAMENODE_MAX_XATTR_SIZE_KEY, (-1));
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(true).build();
            Assert.fail("Expected exception with negative xattr size");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("The maximum size of an xattr should be > 0", e);
        } finally {
            conf.setInt(DFS_NAMENODE_MAX_XATTR_SIZE_KEY, DFS_NAMENODE_MAX_XATTR_SIZE_DEFAULT);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
        try {
            conf.setInt(DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY, (-1));
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(true).build();
            Assert.fail("Expected exception with negative # xattrs per inode");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Cannot set a negative limit on the number of xattrs per inode", e);
        } finally {
            conf.setInt(DFS_NAMENODE_MAX_XATTRS_PER_INODE_KEY, DFS_NAMENODE_MAX_XATTRS_PER_INODE_DEFAULT);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 30000)
    public void testNNFailToStartOnReadOnlyNNDir() throws Exception {
        /* set NN dir */
        final String nnDirStr = Paths.get(hdfsDir.toString(), GenericTestUtils.getMethodName(), "name").toString();
        config.set(DFS_NAMENODE_NAME_DIR_KEY, nnDirStr);
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(config).numDataNodes(1).manageNameDfsDirs(false).build()) {
            cluster.waitActive();
            /* get and verify NN dir */
            final Collection<URI> nnDirs = FSNamesystem.getNamespaceDirs(config);
            Assert.assertNotNull(nnDirs);
            Assert.assertTrue(nnDirs.iterator().hasNext());
            Assert.assertEquals("NN dir should be created after NN startup.", new File(nnDirStr), new File(nnDirs.iterator().next().getPath()));
            final File nnDir = new File(nnDirStr);
            Assert.assertTrue(nnDir.exists());
            Assert.assertTrue(nnDir.isDirectory());
            try {
                /* set read only */
                Assert.assertTrue("Setting NN dir read only should succeed.", FileUtil.setWritable(nnDir, false));
                cluster.restartNameNodes();
                Assert.fail("Restarting NN should fail on read only NN dir.");
            } catch (InconsistentFSStateException e) {
                Assert.assertThat(e.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("InconsistentFSStateException"), CoreMatchers.containsString(nnDirStr), CoreMatchers.containsString("in an inconsistent state"), CoreMatchers.containsString("storage directory does not exist or is not accessible."))));
            } finally {
                /* set back to writable in order to clean it */
                Assert.assertTrue("Setting NN dir should succeed.", FileUtil.setWritable(nnDir, true));
            }
        }
    }

    /**
     * Verify the following scenario.
     * 1. NN restarts.
     * 2. Heartbeat RPC will retry and succeed. NN asks DN to reregister.
     * 3. After reregistration completes, DN will send Heartbeat, followed by
     *    Blockreport.
     * 4. NN will mark DatanodeStorageInfo#blockContentsStale to false.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testStorageBlockContentsStaleAfterNNRestart() throws Exception {
        MiniDFSCluster dfsCluster = null;
        try {
            Configuration config = new Configuration();
            dfsCluster = new MiniDFSCluster.Builder(config).numDataNodes(1).build();
            dfsCluster.waitActive();
            dfsCluster.restartNameNode(true);
            BlockManagerTestUtil.checkHeartbeat(dfsCluster.getNamesystem().getBlockManager());
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanNameFsns = new ObjectName("Hadoop:service=NameNode,name=FSNamesystemState");
            Integer numStaleStorages = ((Integer) (mbs.getAttribute(mxbeanNameFsns, "NumStaleStorages")));
            Assert.assertEquals(0, numStaleStorages.intValue());
        } finally {
            if (dfsCluster != null) {
                dfsCluster.shutdown();
            }
        }
        return;
    }
}

