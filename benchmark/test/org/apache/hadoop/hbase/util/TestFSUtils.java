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
package org.apache.hadoop.hbase.util;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_CLIENT_READ_PREFETCH_SIZE_KEY;
import DFSConfigKeys.DFS_CLIENT_RETRY_WINDOW_BASE;
import DFSConfigKeys.DFS_DFSCLIENT_HEDGED_READ_THREADPOOL_SIZE;
import DFSConfigKeys.DFS_DFSCLIENT_HEDGED_READ_THRESHOLD_MILLIS;
import HConstants.DATA_FILE_UMASK_KEY;
import HConstants.DEFAULT_WAL_STORAGE_POLICY;
import HConstants.ENABLE_DATA_FILE_UMASK;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HDFSBlocksDistribution;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hdfs.DFSHedgedReadMetrics;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FSUtils.FULL_RWX_PERMISSIONS;


/**
 * Test {@link FSUtils}.
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestFSUtils {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFSUtils.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFSUtils.class);

    private HBaseTestingUtility htu;

    private FileSystem fs;

    private Configuration conf;

    @Test
    public void testIsHDFS() throws Exception {
        Assert.assertFalse(FSUtils.isHDFS(conf));
        MiniDFSCluster cluster = null;
        try {
            cluster = htu.startMiniDFSCluster(1);
            Assert.assertTrue(FSUtils.isHDFS(conf));
        } finally {
            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testcomputeHDFSBlocksDistribution() throws Exception {
        final int DEFAULT_BLOCK_SIZE = 1024;
        conf.setLong("dfs.blocksize", DEFAULT_BLOCK_SIZE);
        MiniDFSCluster cluster = null;
        Path testFile = null;
        try {
            // set up a cluster with 3 nodes
            String[] hosts = new String[]{ "host1", "host2", "host3" };
            cluster = htu.startMiniDFSCluster(hosts);
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create a file with two blocks
            testFile = new Path("/test1.txt");
            WriteDataToHDFS(fs, testFile, (2 * DEFAULT_BLOCK_SIZE));
            // given the default replication factor is 3, the same as the number of
            // datanodes; the locality index for each host should be 100%,
            // or getWeight for each host should be the same as getUniqueBlocksWeights
            final long maxTime = (System.currentTimeMillis()) + 2000;
            boolean ok;
            do {
                ok = true;
                FileStatus status = fs.getFileStatus(testFile);
                HDFSBlocksDistribution blocksDistribution = FSUtils.computeHDFSBlocksDistribution(fs, status, 0, status.getLen());
                long uniqueBlocksTotalWeight = blocksDistribution.getUniqueBlocksTotalWeight();
                for (String host : hosts) {
                    long weight = blocksDistribution.getWeight(host);
                    ok = ok && (uniqueBlocksTotalWeight == weight);
                }
            } while ((!ok) && ((System.currentTimeMillis()) < maxTime) );
            Assert.assertTrue(ok);
        } finally {
            htu.shutdownMiniDFSCluster();
        }
        try {
            // set up a cluster with 4 nodes
            String[] hosts = new String[]{ "host1", "host2", "host3", "host4" };
            cluster = htu.startMiniDFSCluster(hosts);
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create a file with three blocks
            testFile = new Path("/test2.txt");
            WriteDataToHDFS(fs, testFile, (3 * DEFAULT_BLOCK_SIZE));
            // given the default replication factor is 3, we will have total of 9
            // replica of blocks; thus the host with the highest weight should have
            // weight == 3 * DEFAULT_BLOCK_SIZE
            final long maxTime = (System.currentTimeMillis()) + 2000;
            long weight;
            long uniqueBlocksTotalWeight;
            do {
                FileStatus status = fs.getFileStatus(testFile);
                HDFSBlocksDistribution blocksDistribution = FSUtils.computeHDFSBlocksDistribution(fs, status, 0, status.getLen());
                uniqueBlocksTotalWeight = blocksDistribution.getUniqueBlocksTotalWeight();
                String tophost = blocksDistribution.getTopHosts().get(0);
                weight = blocksDistribution.getWeight(tophost);
                // NameNode is informed asynchronously, so we may have a delay. See HBASE-6175
            } while ((uniqueBlocksTotalWeight != weight) && ((System.currentTimeMillis()) < maxTime) );
            Assert.assertTrue((uniqueBlocksTotalWeight == weight));
        } finally {
            htu.shutdownMiniDFSCluster();
        }
        try {
            // set up a cluster with 4 nodes
            String[] hosts = new String[]{ "host1", "host2", "host3", "host4" };
            cluster = htu.startMiniDFSCluster(hosts);
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            // create a file with one block
            testFile = new Path("/test3.txt");
            WriteDataToHDFS(fs, testFile, DEFAULT_BLOCK_SIZE);
            // given the default replication factor is 3, we will have total of 3
            // replica of blocks; thus there is one host without weight
            final long maxTime = (System.currentTimeMillis()) + 2000;
            HDFSBlocksDistribution blocksDistribution;
            do {
                FileStatus status = fs.getFileStatus(testFile);
                blocksDistribution = FSUtils.computeHDFSBlocksDistribution(fs, status, 0, status.getLen());
                // NameNode is informed asynchronously, so we may have a delay. See HBASE-6175
            } while (((blocksDistribution.getTopHosts().size()) != 3) && ((System.currentTimeMillis()) < maxTime) );
            Assert.assertEquals("Wrong number of hosts distributing blocks.", 3, blocksDistribution.getTopHosts().size());
        } finally {
            htu.shutdownMiniDFSCluster();
        }
    }

    @Test
    public void testVersion() throws IOException, DeserializationException {
        final Path rootdir = getDataTestDir();
        final FileSystem fs = rootdir.getFileSystem(conf);
        Assert.assertNull(FSUtils.getVersion(fs, rootdir));
        // Write out old format version file.  See if we can read it in and convert.
        Path versionFile = new Path(rootdir, HConstants.VERSION_FILE_NAME);
        FSDataOutputStream s = fs.create(versionFile);
        final String version = HConstants.FILE_SYSTEM_VERSION;
        s.writeUTF(version);
        s.close();
        Assert.assertTrue(fs.exists(versionFile));
        FileStatus[] status = fs.listStatus(versionFile);
        Assert.assertNotNull(status);
        Assert.assertTrue(((status.length) > 0));
        String newVersion = FSUtils.getVersion(fs, rootdir);
        Assert.assertEquals(version.length(), newVersion.length());
        Assert.assertEquals(version, newVersion);
        // File will have been converted. Exercise the pb format
        Assert.assertEquals(version, FSUtils.getVersion(fs, rootdir));
        FSUtils.checkVersion(fs, rootdir, true);
    }

    @Test
    public void testPermMask() throws Exception {
        final Path rootdir = getDataTestDir();
        final FileSystem fs = rootdir.getFileSystem(conf);
        // default fs permission
        FsPermission defaultFsPerm = FSUtils.getFilePermissions(fs, conf, DATA_FILE_UMASK_KEY);
        // 'hbase.data.umask.enable' is false. We will get default fs permission.
        Assert.assertEquals(FsPermission.getFileDefault(), defaultFsPerm);
        conf.setBoolean(ENABLE_DATA_FILE_UMASK, true);
        // first check that we don't crash if we don't have perms set
        FsPermission defaultStartPerm = FSUtils.getFilePermissions(fs, conf, DATA_FILE_UMASK_KEY);
        // default 'hbase.data.umask'is 000, and this umask will be used when
        // 'hbase.data.umask.enable' is true.
        // Therefore we will not get the real fs default in this case.
        // Instead we will get the starting point FULL_RWX_PERMISSIONS
        Assert.assertEquals(new FsPermission(FULL_RWX_PERMISSIONS), defaultStartPerm);
        conf.setStrings(DATA_FILE_UMASK_KEY, "077");
        // now check that we get the right perms
        FsPermission filePerm = FSUtils.getFilePermissions(fs, conf, DATA_FILE_UMASK_KEY);
        Assert.assertEquals(new FsPermission("700"), filePerm);
        // then that the correct file is created
        Path p = new Path((("target" + (File.separator)) + (getRandomUUID().toString())));
        try {
            FSDataOutputStream out = FSUtils.create(conf, fs, p, filePerm, null);
            out.close();
            FileStatus stat = fs.getFileStatus(p);
            Assert.assertEquals(new FsPermission("700"), stat.getPermission());
            // and then cleanup
        } finally {
            fs.delete(p, true);
        }
    }

    @Test
    public void testDeleteAndExists() throws Exception {
        final Path rootdir = getDataTestDir();
        final FileSystem fs = rootdir.getFileSystem(conf);
        conf.setBoolean(ENABLE_DATA_FILE_UMASK, true);
        FsPermission perms = FSUtils.getFilePermissions(fs, conf, DATA_FILE_UMASK_KEY);
        // then that the correct file is created
        String file = getRandomUUID().toString();
        Path p = new Path(htu.getDataTestDir(), (("temptarget" + (File.separator)) + file));
        Path p1 = new Path(htu.getDataTestDir(), (("temppath" + (File.separator)) + file));
        try {
            FSDataOutputStream out = FSUtils.create(conf, fs, p, perms, null);
            out.close();
            Assert.assertTrue("The created file should be present", FSUtils.isExists(fs, p));
            // delete the file with recursion as false. Only the file will be deleted.
            FSUtils.delete(fs, p, false);
            // Create another file
            FSDataOutputStream out1 = FSUtils.create(conf, fs, p1, perms, null);
            out1.close();
            // delete the file with recursion as false. Still the file only will be deleted
            FSUtils.delete(fs, p1, true);
            Assert.assertFalse("The created file should be present", FSUtils.isExists(fs, p1));
            // and then cleanup
        } finally {
            FSUtils.delete(fs, p, true);
            FSUtils.delete(fs, p1, true);
        }
    }

    @Test
    public void testFilteredStatusDoesNotThrowOnNotFound() throws Exception {
        MiniDFSCluster cluster = htu.startMiniDFSCluster(1);
        try {
            Assert.assertNull(FSUtils.listStatusWithStatusFilter(cluster.getFileSystem(), new Path("definitely/doesn't/exist"), null));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testRenameAndSetModifyTime() throws Exception {
        MiniDFSCluster cluster = htu.startMiniDFSCluster(1);
        Assert.assertTrue(FSUtils.isHDFS(conf));
        FileSystem fs = FileSystem.get(conf);
        Path testDir = htu.getDataTestDirOnTestFS("testArchiveFile");
        String file = getRandomUUID().toString();
        Path p = new Path(testDir, file);
        FSDataOutputStream out = fs.create(p);
        out.close();
        Assert.assertTrue("The created file should be present", FSUtils.isExists(fs, p));
        long expect = (System.currentTimeMillis()) + 1000;
        Assert.assertNotEquals(expect, fs.getFileStatus(p).getModificationTime());
        ManualEnvironmentEdge mockEnv = new ManualEnvironmentEdge();
        mockEnv.setValue(expect);
        EnvironmentEdgeManager.injectEdge(mockEnv);
        try {
            String dstFile = getRandomUUID().toString();
            Path dst = new Path(testDir, dstFile);
            Assert.assertTrue(FSUtils.renameAndSetModifyTime(fs, p, dst));
            Assert.assertFalse("The moved file should not be present", FSUtils.isExists(fs, p));
            Assert.assertTrue("The dst file should be present", FSUtils.isExists(fs, dst));
            Assert.assertEquals(expect, fs.getFileStatus(dst).getModificationTime());
            cluster.shutdown();
        } finally {
            EnvironmentEdgeManager.reset();
        }
    }

    @Test
    public void testSetStoragePolicyDefault() throws Exception {
        verifyNoHDFSApiInvocationForDefaultPolicy();
        verifyFileInDirWithStoragePolicy(DEFAULT_WAL_STORAGE_POLICY);
    }

    class AlwaysFailSetStoragePolicyFileSystem extends DistributedFileSystem {
        @Override
        public void setStoragePolicy(final Path src, final String policyName) throws IOException {
            throw new IOException("The setStoragePolicy method is invoked");
        }
    }

    /* might log a warning, but still work. (always warning on Hadoop < 2.6.0) */
    @Test
    public void testSetStoragePolicyValidButMaybeNotPresent() throws Exception {
        verifyFileInDirWithStoragePolicy("ALL_SSD");
    }

    final String INVALID_STORAGE_POLICY = "1772";

    /* should log a warning, but still work. (different warning on Hadoop < 2.6.0) */
    @Test
    public void testSetStoragePolicyInvalid() throws Exception {
        verifyFileInDirWithStoragePolicy(INVALID_STORAGE_POLICY);
    }

    /**
     * Ugly test that ensures we can get at the hedged read counters in dfsclient.
     * Does a bit of preading with hedged reads enabled using code taken from hdfs TestPread.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDFSHedgedReadMetrics() throws Exception {
        // Enable hedged reads and set it so the threshold is really low.
        // Most of this test is taken from HDFS, from TestPread.
        conf.setInt(DFS_DFSCLIENT_HEDGED_READ_THREADPOOL_SIZE, 5);
        conf.setLong(DFS_DFSCLIENT_HEDGED_READ_THRESHOLD_MILLIS, 0);
        conf.setLong(DFS_BLOCK_SIZE_KEY, 4096);
        conf.setLong(DFS_CLIENT_READ_PREFETCH_SIZE_KEY, 4096);
        // Set short retry timeouts so this test runs faster
        conf.setInt(DFS_CLIENT_RETRY_WINDOW_BASE, 0);
        conf.setBoolean("dfs.datanode.transferTo.allowed", false);
        MiniDFSCluster cluster = numDataNodes(3).build();
        // Get the metrics.  Should be empty.
        DFSHedgedReadMetrics metrics = FSUtils.getDFSHedgedReadMetrics(conf);
        Assert.assertEquals(0, metrics.getHedgedReadOps());
        FileSystem fileSys = cluster.getFileSystem();
        try {
            Path p = new Path("preadtest.dat");
            // We need > 1 blocks to test out the hedged reads.
            DFSTestUtil.createFile(fileSys, p, (12 * (TestFSUtils.blockSize)), (12 * (TestFSUtils.blockSize)), TestFSUtils.blockSize, ((short) (3)), TestFSUtils.seed);
            pReadFile(fileSys, p);
            cleanupFile(fileSys, p);
            Assert.assertTrue(((metrics.getHedgedReadOps()) > 0));
        } finally {
            fileSys.close();
            cluster.shutdown();
        }
    }

    @Test
    public void testCopyFilesParallel() throws Exception {
        MiniDFSCluster cluster = htu.startMiniDFSCluster(1);
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        Path src = new Path("/src");
        fs.mkdirs(src);
        for (int i = 0; i < 50; i++) {
            WriteDataToHDFS(fs, new Path(src, String.valueOf(i)), 1024);
        }
        Path sub = new Path(src, "sub");
        fs.mkdirs(sub);
        for (int i = 0; i < 50; i++) {
            WriteDataToHDFS(fs, new Path(sub, String.valueOf(i)), 1024);
        }
        Path dst = new Path("/dst");
        List<Path> allFiles = FSUtils.copyFilesParallel(fs, src, fs, dst, conf, 4);
        Assert.assertEquals(102, allFiles.size());
        FileStatus[] list = fs.listStatus(dst);
        Assert.assertEquals(51, list.length);
        FileStatus[] sublist = fs.listStatus(new Path(dst, "sub"));
        Assert.assertEquals(50, sublist.length);
    }

    // Below is taken from TestPread over in HDFS.
    static final int blockSize = 4096;

    static final long seed = 3735928559L;

    private static final boolean STREAM_CAPABILITIES_IS_PRESENT;

    static {
        boolean tmp = false;
        try {
            Class.forName("org.apache.hadoop.fs.StreamCapabilities");
            tmp = true;
            TestFSUtils.LOG.debug("Test thought StreamCapabilities class was present.");
        } catch (ClassNotFoundException exception) {
            TestFSUtils.LOG.debug("Test didn't think StreamCapabilities class was present.");
        } finally {
            STREAM_CAPABILITIES_IS_PRESENT = tmp;
        }
    }

    // Here instead of TestCommonFSUtils because we need a minicluster
    @Test
    public void checkStreamCapabilitiesOnHdfsDataOutputStream() throws Exception {
        MiniDFSCluster cluster = htu.startMiniDFSCluster(1);
        try (FileSystem filesystem = cluster.getFileSystem()) {
            FSDataOutputStream stream = filesystem.create(new Path("/tmp/foobar"));
            Assert.assertTrue(FSUtils.hasCapability(stream, "hsync"));
            Assert.assertTrue(FSUtils.hasCapability(stream, "hflush"));
            Assert.assertNotEquals(("We expect HdfsDataOutputStream to say it has a dummy capability iff the " + "StreamCapabilities class is not defined."), TestFSUtils.STREAM_CAPABILITIES_IS_PRESENT, FSUtils.hasCapability(stream, "a capability that hopefully HDFS doesn't add."));
        } finally {
            cluster.shutdown();
        }
    }
}

