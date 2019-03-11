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


import DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_ACCESSTIME_PRECISION_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY;
import DFSConfigKeys.DFS_NAMENODE_REPLICATION_MIN_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import DirOp.READ;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import HdfsConstants.BLOCK_STORAGE_POLICY_ID_UNSPECIFIED;
import NamenodeFsck.CORRUPT_STATUS;
import NamenodeFsck.DECOMMISSIONED_STATUS;
import NamenodeFsck.DECOMMISSIONING_STATUS;
import NamenodeFsck.ENTERING_MAINTENANCE_STATUS;
import NamenodeFsck.FAILURE_STATUS;
import NamenodeFsck.HEALTHY_STATUS;
import NamenodeFsck.IN_MAINTENANCE_STATUS;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.UnresolvedLinkException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.CorruptFileBlocks;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.LocatedStripedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockCollection;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory.DirOp;
import org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.Result;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.hdfs.util.StripedBlockUtil;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test for doing fsck.
 */
public class TestFsck {
    private static final Logger LOG = LoggerFactory.getLogger(TestFsck.class.getName());

    static final String AUDITLOG_FILE = GenericTestUtils.getTempPath("TestFsck-audit.log");

    // Pattern for:
    // allowed=true ugi=name ip=/address cmd=FSCK src=/ dst=null perm=null
    static final Pattern FSCK_PATTERN = Pattern.compile(("allowed=.*?\\s" + (((("ugi=.*?\\s" + "ip=/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s") + "cmd=fsck\\ssrc=\\/\\sdst=null\\s") + "perm=null\\s") + "proto=.*")));

    static final Pattern GET_FILE_INFO_PATTERN = Pattern.compile(("allowed=.*?\\s" + (((("ugi=.*?\\s" + "ip=/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s") + "cmd=getfileinfo\\ssrc=\\/\\sdst=null\\s") + "perm=null\\s") + "proto=.*")));

    static final Pattern NUM_MISSING_BLOCKS_PATTERN = Pattern.compile(".*Missing blocks:\t\t([0123456789]*).*");

    static final Pattern NUM_CORRUPT_BLOCKS_PATTERN = Pattern.compile(".*Corrupt blocks:\t\t([0123456789]*).*");

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private MiniDFSCluster cluster = null;

    private Configuration conf = null;

    /**
     * do fsck.
     */
    @Test
    public void testFsck() throws Exception {
        DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsck").setNumFiles(20).build();
        FileSystem fs = null;
        final long precision = 1L;
        conf.setLong(DFS_NAMENODE_ACCESSTIME_PRECISION_KEY, precision);
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        fs = cluster.getFileSystem();
        final String fileName = "/srcdat";
        util.createFiles(fs, fileName);
        util.waitReplication(fs, fileName, ((short) (3)));
        final Path file = new Path(fileName);
        long aTime = fs.getFileStatus(file).getAccessTime();
        Thread.sleep(precision);
        setupAuditLogs();
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        verifyAuditLogs();
        Assert.assertEquals(aTime, fs.getFileStatus(file).getAccessTime());
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        shutdownCluster();
        // restart the cluster; bring up namenode but not the data nodes
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).build();
        outStr = TestFsck.runFsck(conf, 1, true, "/");
        // expect the result is corrupt
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        System.out.println(outStr);
        // bring up data nodes & cleanup cluster
        cluster.startDataNodes(conf, 4, true, null, null);
        cluster.waitActive();
        cluster.waitClusterUp();
        fs = cluster.getFileSystem();
        util.cleanup(fs, "/srcdat");
    }

    @Test
    public void testFsckNonExistent() throws Exception {
        DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsck").setNumFiles(20).build();
        FileSystem fs = null;
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        fs = cluster.getFileSystem();
        util.createFiles(fs, "/srcdat");
        util.waitReplication(fs, "/srcdat", ((short) (3)));
        String outStr = TestFsck.runFsck(conf, 0, true, "/non-existent");
        Assert.assertEquals((-1), outStr.indexOf(HEALTHY_STATUS));
        System.out.println(outStr);
        util.cleanup(fs, "/srcdat");
    }

    /**
     * Test fsck with permission set on inodes.
     */
    @Test
    public void testFsckPermission() throws Exception {
        final DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(20).build();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        // Create a cluster with the current user, write some files
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        final MiniDFSCluster c2 = cluster;
        final String dir = "/dfsck";
        final Path dirpath = new Path(dir);
        final FileSystem fs = c2.getFileSystem();
        util.createFiles(fs, dir);
        util.waitReplication(fs, dir, ((short) (3)));
        fs.setPermission(dirpath, new FsPermission(((short) (448))));
        // run DFSck as another user, should fail with permission issue
        UserGroupInformation fakeUGI = UserGroupInformation.createUserForTesting("ProbablyNotARealUserName", new String[]{ "ShangriLa" });
        fakeUGI.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                System.out.println(TestFsck.runFsck(conf, (-1), true, dir));
                return null;
            }
        });
        // set permission and try DFSck again as the fake user, should succeed
        fs.setPermission(dirpath, new FsPermission(((short) (511))));
        fakeUGI.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final String outStr = TestFsck.runFsck(conf, 0, true, dir);
                System.out.println(outStr);
                Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
                return null;
            }
        });
        util.cleanup(fs, dir);
    }

    @Test
    public void testFsckMove() throws Exception {
        final int dfsBlockSize = 1024;
        final int numDatanodes = 4;
        conf.setLong(DFS_BLOCK_SIZE_KEY, dfsBlockSize);
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
        DFSTestUtil util = new DFSTestUtil("TestFsck", 5, 3, ((5 * dfsBlockSize) + (dfsBlockSize - 1)), (5 * dfsBlockSize));
        FileSystem fs = null;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDatanodes).build();
        String topDir = "/srcdat";
        fs = cluster.getFileSystem();
        cluster.waitActive();
        util.createFiles(fs, topDir);
        util.waitReplication(fs, topDir, ((short) (3)));
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
        String[] fileNames = util.getFileNames(topDir);
        TestFsck.CorruptedTestFile[] ctFiles = new TestFsck.CorruptedTestFile[]{ new TestFsck.CorruptedTestFile(fileNames[0], Sets.newHashSet(0), dfsClient, numDatanodes, dfsBlockSize), new TestFsck.CorruptedTestFile(fileNames[1], Sets.newHashSet(2, 3), dfsClient, numDatanodes, dfsBlockSize), new TestFsck.CorruptedTestFile(fileNames[2], Sets.newHashSet(4), dfsClient, numDatanodes, dfsBlockSize), new TestFsck.CorruptedTestFile(fileNames[3], Sets.newHashSet(0, 1, 2, 3), dfsClient, numDatanodes, dfsBlockSize), new TestFsck.CorruptedTestFile(fileNames[4], Sets.newHashSet(1, 2, 3, 4), dfsClient, numDatanodes, dfsBlockSize) };
        int totalMissingBlocks = 0;
        for (TestFsck.CorruptedTestFile ctFile : ctFiles) {
            totalMissingBlocks += ctFile.getTotalMissingBlocks();
        }
        for (TestFsck.CorruptedTestFile ctFile : ctFiles) {
            ctFile.removeBlocks(cluster);
        }
        // Wait for fsck to discover all the missing blocks
        while (true) {
            outStr = TestFsck.runFsck(conf, 1, false, "/");
            String numMissing = null;
            String numCorrupt = null;
            for (String line : outStr.split(TestFsck.LINE_SEPARATOR)) {
                Matcher m = TestFsck.NUM_MISSING_BLOCKS_PATTERN.matcher(line);
                if (m.matches()) {
                    numMissing = m.group(1);
                }
                m = TestFsck.NUM_CORRUPT_BLOCKS_PATTERN.matcher(line);
                if (m.matches()) {
                    numCorrupt = m.group(1);
                }
                if ((numMissing != null) && (numCorrupt != null)) {
                    break;
                }
            }
            if ((numMissing == null) || (numCorrupt == null)) {
                throw new IOException(("failed to find number of missing or corrupt" + " blocks in fsck output."));
            }
            if (numMissing.equals(Integer.toString(totalMissingBlocks))) {
                Assert.assertTrue(numCorrupt.equals(Integer.toString(0)));
                Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
        } 
        // Copy the non-corrupt blocks of corruptFileName to lost+found.
        outStr = TestFsck.runFsck(conf, 1, false, "/", "-move");
        TestFsck.LOG.info(("WATERMELON: outStr = " + outStr));
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        // Make sure that we properly copied the block files from the DataNodes
        // to lost+found
        for (TestFsck.CorruptedTestFile ctFile : ctFiles) {
            ctFile.checkSalvagedRemains();
        }
        // Fix the filesystem by removing corruptFileName
        outStr = TestFsck.runFsck(conf, 1, true, "/", "-delete");
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        // Check to make sure we have a healthy filesystem
        outStr = TestFsck.runFsck(conf, 0, true, "/");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        util.cleanup(fs, topDir);
    }

    private static class CorruptedTestFile {
        private final String name;

        private final Set<Integer> blocksToCorrupt;

        private final DFSClient dfsClient;

        private final int numDataNodes;

        private final int blockSize;

        private final byte[] initialContents;

        CorruptedTestFile(String name, Set<Integer> blocksToCorrupt, DFSClient dfsClient, int numDataNodes, int blockSize) throws IOException {
            this.name = name;
            this.blocksToCorrupt = blocksToCorrupt;
            this.dfsClient = dfsClient;
            this.numDataNodes = numDataNodes;
            this.blockSize = blockSize;
            this.initialContents = cacheInitialContents();
        }

        public int getTotalMissingBlocks() {
            return blocksToCorrupt.size();
        }

        private byte[] cacheInitialContents() throws IOException {
            HdfsFileStatus status = dfsClient.getFileInfo(name);
            byte[] content = new byte[((int) (status.getLen()))];
            DFSInputStream in = null;
            try {
                in = dfsClient.open(name);
                IOUtils.readFully(in, content, 0, content.length);
            } finally {
                in.close();
            }
            return content;
        }

        public void removeBlocks(MiniDFSCluster cluster) throws FileNotFoundException, IOException, UnresolvedLinkException, AccessControlException {
            for (int corruptIdx : blocksToCorrupt) {
                // Corrupt a block by deleting it
                ExtendedBlock block = dfsClient.getNamenode().getBlockLocations(name, ((blockSize) * corruptIdx), Long.MAX_VALUE).get(0).getBlock();
                for (int i = 0; i < (numDataNodes); i++) {
                    File blockFile = cluster.getBlockFile(i, block);
                    if ((blockFile != null) && (blockFile.exists())) {
                        Assert.assertTrue(blockFile.delete());
                    }
                }
            }
        }

        public void corruptBlocks(MiniDFSCluster cluster) throws IOException {
            for (int corruptIdx : blocksToCorrupt) {
                // Corrupt a block by deleting it
                ExtendedBlock block = dfsClient.getNamenode().getBlockLocations(name, ((blockSize) * corruptIdx), Long.MAX_VALUE).get(0).getBlock();
                for (int i = 0; i < (numDataNodes); i++) {
                    File blockFile = cluster.getBlockFile(i, block);
                    if ((blockFile != null) && (blockFile.exists())) {
                        FileOutputStream blockFileStream = new FileOutputStream(blockFile, false);
                        blockFileStream.write("corrupt".getBytes());
                        blockFileStream.close();
                        TestFsck.LOG.info(("Corrupted block file " + blockFile));
                    }
                }
            }
        }

        public void checkSalvagedRemains() throws IOException {
            int chainIdx = 0;
            HdfsFileStatus status = dfsClient.getFileInfo(name);
            long length = status.getLen();
            int numBlocks = ((int) (((length + (blockSize)) - 1) / (blockSize)));
            DFSInputStream in = null;
            byte[] blockBuffer = new byte[blockSize];
            try {
                for (int blockIdx = 0; blockIdx < numBlocks; blockIdx++) {
                    if (blocksToCorrupt.contains(blockIdx)) {
                        if (in != null) {
                            in.close();
                            in = null;
                        }
                        continue;
                    }
                    if (in == null) {
                        in = dfsClient.open(((("/lost+found" + (name)) + "/") + chainIdx));
                        chainIdx++;
                    }
                    int len = blockBuffer.length;
                    if (blockIdx == (numBlocks - 1)) {
                        // The last block might not be full-length
                        len = ((int) ((in.getFileLength()) % (blockSize)));
                        if (len == 0) {
                            len = blockBuffer.length;
                        }
                    }
                    IOUtils.readFully(in, blockBuffer, 0, len);
                    int startIdx = blockIdx * (blockSize);
                    for (int i = 0; i < len; i++) {
                        if ((initialContents[(startIdx + i)]) != (blockBuffer[i])) {
                            throw new IOException((((("salvaged file " + (name)) + " differed ") + "from what we expected on block ") + blockIdx));
                        }
                    }
                }
            } finally {
                IOUtils.cleanup(null, in);
            }
        }
    }

    @Test
    public void testFsckMoveAndDelete() throws Exception {
        final int maxMoveTries = 5;
        DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsckMoveAndDelete").setNumFiles(5).build();
        FileSystem fs = null;
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        String topDir = "/srcdat";
        fs = cluster.getFileSystem();
        cluster.waitActive();
        util.createFiles(fs, topDir);
        util.waitReplication(fs, topDir, ((short) (3)));
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // Corrupt a block by deleting it
        String[] fileNames = util.getFileNames(topDir);
        DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
        String corruptFileName = fileNames[0];
        ExtendedBlock block = dfsClient.getNamenode().getBlockLocations(corruptFileName, 0, Long.MAX_VALUE).get(0).getBlock();
        for (int i = 0; i < 4; i++) {
            File blockFile = cluster.getBlockFile(i, block);
            if ((blockFile != null) && (blockFile.exists())) {
                Assert.assertTrue(blockFile.delete());
            }
        }
        // We excpect the filesystem to be corrupted
        outStr = TestFsck.runFsck(conf, 1, false, "/");
        while (!(outStr.contains(CORRUPT_STATUS))) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            outStr = TestFsck.runFsck(conf, 1, false, "/");
        } 
        // After a fsck -move, the corrupted file should still exist.
        for (int i = 0; i < maxMoveTries; i++) {
            outStr = TestFsck.runFsck(conf, 1, true, "/", "-move");
            Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
            String[] newFileNames = util.getFileNames(topDir);
            boolean found = false;
            for (String f : newFileNames) {
                if (f.equals(corruptFileName)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
        // Fix the filesystem by moving corrupted files to lost+found
        outStr = TestFsck.runFsck(conf, 1, true, "/", "-move", "-delete");
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        // Check to make sure we have healthy filesystem
        outStr = TestFsck.runFsck(conf, 0, true, "/");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        util.cleanup(fs, topDir);
    }

    @Test
    public void testFsckOpenFiles() throws Exception {
        DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsck").setNumFiles(4).build();
        FileSystem fs = null;
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        String topDir = "/srcdat";
        String randomString = "HADOOP  ";
        fs = cluster.getFileSystem();
        cluster.waitActive();
        util.createFiles(fs, topDir);
        util.waitReplication(fs, topDir, ((short) (3)));
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // Open a file for writing and do not close for now
        Path openFile = new Path((topDir + "/openFile"));
        FSDataOutputStream out = fs.create(openFile);
        int writeCount = 0;
        while (writeCount != 100) {
            out.write(randomString.getBytes());
            writeCount++;
        } 
        hflush();
        // We expect the filesystem to be HEALTHY and show one open file
        outStr = TestFsck.runFsck(conf, 0, true, topDir);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertFalse(outStr.contains("OPENFORWRITE"));
        // Use -openforwrite option to list open files
        outStr = TestFsck.runFsck(conf, 0, true, topDir, "-files", "-blocks", "-locations", "-openforwrite");
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains("OPENFORWRITE"));
        Assert.assertTrue(outStr.contains("Under Construction Block:"));
        Assert.assertTrue(outStr.contains("openFile"));
        // Close the file
        out.close();
        // Now, fsck should show HEALTHY fs and should not show any open files
        outStr = TestFsck.runFsck(conf, 0, true, topDir);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertFalse(outStr.contains("OPENFORWRITE"));
        Assert.assertFalse(outStr.contains("Under Construction Block:"));
        util.cleanup(fs, topDir);
    }

    @Test
    public void testFsckOpenECFiles() throws Exception {
        DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsckECFile").setNumFiles(4).build();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();
        final int dataBlocks = ecPolicy.getNumDataUnits();
        final int cellSize = ecPolicy.getCellSize();
        final int numAllUnits = dataBlocks + (ecPolicy.getNumParityUnits());
        int blockSize = 2 * cellSize;
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes((numAllUnits + 1)).build();
        String topDir = "/myDir";
        cluster.waitActive();
        DistributedFileSystem fs = cluster.getFileSystem();
        fs.enableErasureCodingPolicy(ecPolicy.getName());
        util.createFiles(fs, topDir);
        // set topDir to EC when it has replicated files
        cluster.getFileSystem().getClient().setErasureCodingPolicy(topDir, ecPolicy.getName());
        // create a new file under topDir
        DFSTestUtil.createFile(fs, new Path(topDir, "ecFile"), 1024, ((short) (1)), 0L);
        // Open a EC file for writing and do not close for now
        Path openFile = new Path((topDir + "/openECFile"));
        FSDataOutputStream out = fs.create(openFile);
        int blockGroupSize = dataBlocks * blockSize;
        // data size is more than 1 block group and less than 2 block groups
        byte[] randomBytes = new byte[(2 * blockGroupSize) - cellSize];
        int seed = 42;
        new Random(seed).nextBytes(randomBytes);
        out.write(randomBytes);
        // make sure the fsck can correctly handle mixed ec/replicated files
        TestFsck.runFsck(conf, 0, true, topDir, "-files", "-blocks", "-openforwrite");
        // We expect the filesystem to be HEALTHY and show one open file
        String outStr = TestFsck.runFsck(conf, 0, true, openFile.toString(), "-files", "-blocks", "-openforwrite");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertTrue(outStr.contains("OPENFORWRITE"));
        Assert.assertTrue(outStr.contains(("Live_repl=" + numAllUnits)));
        Assert.assertTrue(outStr.contains(("Expected_repl=" + numAllUnits)));
        // Use -openforwrite option to list open files
        outStr = TestFsck.runFsck(conf, 0, true, openFile.toString(), "-files", "-blocks", "-locations", "-openforwrite", "-replicaDetails");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertTrue(outStr.contains("OPENFORWRITE"));
        Assert.assertTrue(outStr.contains(("Live_repl=" + numAllUnits)));
        Assert.assertTrue(outStr.contains(("Expected_repl=" + numAllUnits)));
        Assert.assertTrue(outStr.contains("Under Construction Block:"));
        // check reported blockIDs of internal blocks
        LocatedStripedBlock lsb = ((LocatedStripedBlock) (fs.getClient().getLocatedBlocks(openFile.toString(), 0, (cellSize * dataBlocks)).get(0)));
        long groupId = lsb.getBlock().getBlockId();
        byte[] indices = lsb.getBlockIndices();
        DatanodeInfo[] locs = lsb.getLocations();
        long blockId;
        for (int i = 0; i < (indices.length); i++) {
            blockId = groupId + (indices[i]);
            String str = (("blk_" + blockId) + ":") + (locs[i]);
            Assert.assertTrue(outStr.contains(str));
        }
        // check the output of under-constructed blocks doesn't include the blockIDs
        String regex = (".*Expected_repl=" + numAllUnits) + "(.*)\nStatus:.*";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(outStr);
        Assert.assertTrue(m.find());
        String ucBlockOutput = m.group(1);
        Assert.assertFalse(ucBlockOutput.contains("blk_"));
        // Close the file
        out.close();
        // Now, fsck should show HEALTHY fs and should not show any open files
        outStr = TestFsck.runFsck(conf, 0, true, openFile.toString(), "-files", "-blocks", "-locations", "-racks", "-replicaDetails");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertFalse(outStr.contains("OPENFORWRITE"));
        Assert.assertFalse(outStr.contains("Under Construction Block:"));
        Assert.assertFalse(outStr.contains(("Expected_repl=" + numAllUnits)));
        Assert.assertTrue(outStr.contains(("Live_repl=" + numAllUnits)));
        util.cleanup(fs, topDir);
    }

    @Test
    public void testCorruptBlock() throws Exception {
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000);
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        FileSystem fs = null;
        DFSClient dfsClient = null;
        LocatedBlocks blocks = null;
        int replicaCount = 0;
        Random random = new Random();
        String outStr = null;
        short factor = 1;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(1).build();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        Path file1 = new Path("/testCorruptBlock");
        DFSTestUtil.createFile(fs, file1, 1024, factor, 0);
        // Wait until file replication has completed
        DFSTestUtil.waitReplication(fs, file1, factor);
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, file1);
        // Make sure filesystem is in healthy state
        outStr = TestFsck.runFsck(conf, 0, true, "/");
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // corrupt replicas
        File blockFile = cluster.getBlockFile(0, block);
        if ((blockFile != null) && (blockFile.exists())) {
            RandomAccessFile raFile = new RandomAccessFile(blockFile, "rw");
            FileChannel channel = raFile.getChannel();
            String badString = "BADBAD";
            int rand = random.nextInt((((int) (channel.size())) / 2));
            raFile.seek(rand);
            raFile.write(badString.getBytes());
            raFile.close();
        }
        // Read the file to trigger reportBadBlocks
        try {
            IOUtils.copyBytes(fs.open(file1), new IOUtils.NullOutputStream(), conf, true);
        } catch (IOException ie) {
            Assert.assertTrue((ie instanceof ChecksumException));
        }
        dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
        blocks = dfsClient.getNamenode().getBlockLocations(file1.toString(), 0, Long.MAX_VALUE);
        replicaCount = blocks.get(0).getLocations().length;
        while (replicaCount != factor) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            blocks = dfsClient.getNamenode().getBlockLocations(file1.toString(), 0, Long.MAX_VALUE);
            replicaCount = blocks.get(0).getLocations().length;
        } 
        Assert.assertTrue(blocks.get(0).isCorrupt());
        // Check if fsck reports the same
        outStr = TestFsck.runFsck(conf, 1, true, "/");
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        Assert.assertTrue(outStr.contains("testCorruptBlock"));
    }

    @Test
    public void testUnderMinReplicatedBlock() throws Exception {
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000);
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        // Set minReplication to 2
        short minReplication = 2;
        conf.setInt(DFS_NAMENODE_REPLICATION_MIN_KEY, minReplication);
        FileSystem fs = null;
        DFSClient dfsClient = null;
        LocatedBlocks blocks = null;
        int replicaCount = 0;
        Random random = new Random();
        String outStr = null;
        short factor = 1;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(2).build();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        Path file1 = new Path("/testUnderMinReplicatedBlock");
        DFSTestUtil.createFile(fs, file1, 1024, minReplication, 0);
        // Wait until file replication has completed
        DFSTestUtil.waitReplication(fs, file1, minReplication);
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, file1);
        // Make sure filesystem is in healthy state
        outStr = TestFsck.runFsck(conf, 0, true, "/");
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // corrupt the first replica
        File blockFile = cluster.getBlockFile(0, block);
        if ((blockFile != null) && (blockFile.exists())) {
            RandomAccessFile raFile = new RandomAccessFile(blockFile, "rw");
            FileChannel channel = raFile.getChannel();
            String badString = "BADBAD";
            int rand = random.nextInt((((int) (channel.size())) / 2));
            raFile.seek(rand);
            raFile.write(badString.getBytes());
            raFile.close();
        }
        dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
        blocks = dfsClient.getNamenode().getBlockLocations(file1.toString(), 0, Long.MAX_VALUE);
        replicaCount = blocks.get(0).getLocations().length;
        while (replicaCount != factor) {
            try {
                Thread.sleep(100);
                // Read the file to trigger reportBadBlocks
                try {
                    IOUtils.copyBytes(fs.open(file1), new IOUtils.NullOutputStream(), conf, true);
                } catch (IOException ie) {
                    Assert.assertTrue((ie instanceof ChecksumException));
                }
                System.out.println(((("sleep in try: replicaCount=" + replicaCount) + "  factor=") + factor));
            } catch (InterruptedException ignore) {
            }
            blocks = dfsClient.getNamenode().getBlockLocations(file1.toString(), 0, Long.MAX_VALUE);
            replicaCount = blocks.get(0).getLocations().length;
        } 
        // Check if fsck reports the same
        outStr = TestFsck.runFsck(conf, 0, true, "/");
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertTrue(outStr.contains("UNDER MIN REPL\'D BLOCKS:\t1 (100.0 %)"));
        Assert.assertTrue(outStr.contains("MINIMAL BLOCK REPLICATION:\t2"));
    }

    @Test(timeout = 90000)
    public void testFsckReplicaDetails() throws Exception {
        final short replFactor = 1;
        short numDn = 1;
        final long blockSize = 512;
        final long fileSize = 1024;
        String[] racks = new String[]{ "/rack1" };
        String[] hosts = new String[]{ "host1" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, 1);
        DistributedFileSystem dfs;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        // create files
        final String testFile = new String("/testfile");
        final Path path = new Path(testFile);
        DFSTestUtil.createFile(dfs, path, fileSize, replFactor, 1000L);
        DFSTestUtil.waitReplication(dfs, path, replFactor);
        // make sure datanode that has replica is fine before decommission
        String fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-maintenance", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains(HEALTHY_STATUS));
        Assert.assertTrue(fsckOut.contains("(LIVE)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
        // decommission datanode
        FSNamesystem fsn = cluster.getNameNode().getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        final DatanodeManager dnm = bm.getDatanodeManager();
        DatanodeDescriptor dnDesc0 = dnm.getDatanode(cluster.getDataNodes().get(0).getDatanodeId());
        bm.getDatanodeManager().getDatanodeAdminManager().startDecommission(dnDesc0);
        final String dn0Name = dnDesc0.getXferAddr();
        // check the replica status while decommissioning
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-maintenance", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONING)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
        // Start 2nd DataNode
        cluster.startDataNodes(conf, 1, true, null, new String[]{ "/rack2" }, new String[]{ "host2" }, null, false);
        // Wait for decommission to start
        final AtomicBoolean checkDecommissionInProgress = new AtomicBoolean(false);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                DatanodeInfo datanodeInfo = null;
                try {
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dn0Name.equals(info.getXferAddr())) {
                            datanodeInfo = info;
                        }
                    }
                    if (((!(checkDecommissionInProgress.get())) && (datanodeInfo != null)) && (datanodeInfo.isDecommissionInProgress())) {
                        checkDecommissionInProgress.set(true);
                    }
                    if ((datanodeInfo != null) && (datanodeInfo.isDecommissioned())) {
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // check the replica status after decommission is done
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-maintenance", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONED)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
        DatanodeDescriptor dnDesc1 = dnm.getDatanode(cluster.getDataNodes().get(1).getDatanodeId());
        final String dn1Name = dnDesc1.getXferAddr();
        bm.getDatanodeManager().getDatanodeAdminManager().startMaintenance(dnDesc1, Long.MAX_VALUE);
        // check the replica status while entering maintenance
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-maintenance", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONED)"));
        Assert.assertTrue(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
        // check entering maintenance replicas are printed only when requested
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONED)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
        // Start 3rd DataNode
        cluster.startDataNodes(conf, 1, true, null, new String[]{ "/rack3" }, new String[]{ "host3" }, null, false);
        // Wait for the 2nd node to reach in maintenance state
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                DatanodeInfo dnInfo = null;
                try {
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dn1Name.equals(info.getXferAddr())) {
                            dnInfo = info;
                        }
                    }
                    if ((dnInfo != null) && (dnInfo.isInMaintenance())) {
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // check the replica status after decommission is done
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-maintenance", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONED)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertTrue(fsckOut.contains("(IN MAINTENANCE)"));
        // check in maintenance replicas are not printed when not requested
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-files", "-blocks", "-replicaDetails");
        Assert.assertTrue(fsckOut.contains("(DECOMMISSIONED)"));
        Assert.assertFalse(fsckOut.contains("(ENTERING MAINTENANCE)"));
        Assert.assertFalse(fsckOut.contains("(IN MAINTENANCE)"));
    }

    /**
     * Test if fsck can return -1 in case of failure.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFsckError() throws Exception {
        // bring up a one-node cluster
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).build();
        String fileName = "/test.txt";
        Path filePath = new Path(fileName);
        FileSystem fs = cluster.getFileSystem();
        // create a one-block file
        DFSTestUtil.createFile(fs, filePath, 1L, ((short) (1)), 1L);
        DFSTestUtil.waitReplication(fs, filePath, ((short) (1)));
        // intentionally corrupt NN data structure
        INodeFile node = ((INodeFile) (cluster.getNamesystem().dir.getINode(fileName, READ)));
        final BlockInfo[] blocks = node.getBlocks();
        Assert.assertEquals(blocks.length, 1);
        blocks[0].setNumBytes((-1L));// set the block length to be negative

        // run fsck and expect a failure with -1 as the error code
        String outStr = TestFsck.runFsck(conf, (-1), true, fileName);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(FAILURE_STATUS));
        // clean up file system
        fs.delete(filePath, true);
    }

    /**
     * check if option -list-corruptfiles of fsck command works properly.
     */
    @Test
    public void testFsckListCorruptFilesBlocks() throws Exception {
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000);
        conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
        FileSystem fs = null;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).build();
        cluster.waitActive();
        fs = cluster.getFileSystem();
        DFSTestUtil util = new DFSTestUtil.Builder().setName("testGetCorruptFiles").setNumFiles(3).setMaxLevels(1).setMaxSize(1024).build();
        util.createFiles(fs, "/corruptData", ((short) (1)));
        util.waitReplication(fs, "/corruptData", ((short) (1)));
        String outStr = TestFsck.runFsck(conf, 0, false, "/corruptData", "-list-corruptfileblocks");
        System.out.println(("1. good fsck out: " + outStr));
        Assert.assertTrue(outStr.contains("has 0 CORRUPT files"));
        // delete the blocks
        final String bpid = cluster.getNamesystem().getBlockPoolId();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= 1; j++) {
                File storageDir = cluster.getInstanceStorageDir(i, j);
                File dataDir = MiniDFSCluster.getFinalizedDir(storageDir, bpid);
                List<File> metadataFiles = MiniDFSCluster.getAllBlockMetadataFiles(dataDir);
                if (metadataFiles == null) {
                    continue;
                }
                for (File metadataFile : metadataFiles) {
                    File blockFile = Block.metaToBlockFile(metadataFile);
                    Assert.assertTrue("Cannot remove file.", blockFile.delete());
                    Assert.assertTrue("Cannot remove file.", metadataFile.delete());
                }
            }
        }
        // wait for the namenode to see the corruption
        final NamenodeProtocols namenode = cluster.getNameNodeRpc();
        CorruptFileBlocks corruptFileBlocks = namenode.listCorruptFileBlocks("/corruptData", null);
        int numCorrupt = corruptFileBlocks.getFiles().length;
        while (numCorrupt == 0) {
            Thread.sleep(1000);
            corruptFileBlocks = namenode.listCorruptFileBlocks("/corruptData", null);
            numCorrupt = corruptFileBlocks.getFiles().length;
        } 
        outStr = TestFsck.runFsck(conf, (-1), true, "/corruptData", "-list-corruptfileblocks");
        System.out.println(("2. bad fsck out: " + outStr));
        Assert.assertTrue(outStr.contains("has 3 CORRUPT files"));
        // Do a listing on a dir which doesn't have any corrupt blocks and validate
        util.createFiles(fs, "/goodData");
        outStr = TestFsck.runFsck(conf, 0, true, "/goodData", "-list-corruptfileblocks");
        System.out.println(("3. good fsck out: " + outStr));
        Assert.assertTrue(outStr.contains("has 0 CORRUPT files"));
        util.cleanup(fs, "/corruptData");
        util.cleanup(fs, "/goodData");
    }

    /**
     * Test for checking fsck command on illegal arguments should print the proper
     * usage.
     */
    @Test
    public void testToCheckTheFsckCommandOnIllegalArguments() throws Exception {
        // bring up a one-node cluster
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).build();
        String fileName = "/test.txt";
        Path filePath = new Path(fileName);
        FileSystem fs = cluster.getFileSystem();
        // create a one-block file
        DFSTestUtil.createFile(fs, filePath, 1L, ((short) (1)), 1L);
        DFSTestUtil.waitReplication(fs, filePath, ((short) (1)));
        // passing illegal option
        String outStr = TestFsck.runFsck(conf, (-1), true, fileName, "-thisIsNotAValidFlag");
        System.out.println(outStr);
        Assert.assertTrue((!(outStr.contains(HEALTHY_STATUS))));
        // passing multiple paths are arguments
        outStr = TestFsck.runFsck(conf, (-1), true, "/", fileName);
        System.out.println(outStr);
        Assert.assertTrue((!(outStr.contains(HEALTHY_STATUS))));
        // clean up file system
        fs.delete(filePath, true);
    }

    /**
     * Tests that the # of missing block replicas and expected replicas is
     * correct.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFsckMissingReplicas() throws IOException {
        // Desired replication factor
        // Set this higher than numReplicas so it's under-replicated
        final short replFactor = 2;
        // Number of replicas to actually start
        final short numReplicas = 1;
        // Number of blocks to write
        final short numBlocks = 3;
        // Set a small-ish blocksize
        final long blockSize = 512;
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        DistributedFileSystem dfs = null;
        // Startup a minicluster
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numReplicas).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        // Create a file that will be intentionally under-replicated
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        long fileLen = blockSize * numBlocks;
        DFSTestUtil.createFile(dfs, path, fileLen, replFactor, 1);
        // Create an under-replicated file
        NameNode namenode = cluster.getNameNode();
        NetworkTopology nettop = cluster.getNamesystem().getBlockManager().getDatanodeManager().getNetworkTopology();
        Map<String, String[]> pmap = new HashMap<String, String[]>();
        Writer result = new StringWriter();
        PrintWriter out = new PrintWriter(result, true);
        InetAddress remoteAddress = InetAddress.getLocalHost();
        NamenodeFsck fsck = new NamenodeFsck(conf, namenode, nettop, pmap, out, numReplicas, remoteAddress);
        // Run the fsck and check the Result
        final HdfsFileStatus file = namenode.getRpcServer().getFileInfo(pathString);
        Assert.assertNotNull(file);
        Result replRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ReplicationResult(conf);
        Result ecRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ErasureCodingResult(conf);
        fsck.check(pathString, file, replRes, ecRes);
        // Also print the output from the fsck, for ex post facto sanity checks
        System.out.println(result.toString());
        Assert.assertEquals(replRes.missingReplicas, ((numBlocks * replFactor) - (numBlocks * numReplicas)));
        Assert.assertEquals(replRes.numExpectedReplicas, (numBlocks * replFactor));
    }

    /**
     * Tests that the # of misreplaced replicas is correct.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFsckMisPlacedReplicas() throws IOException {
        // Desired replication factor
        final short replFactor = 2;
        // Number of replicas to actually start
        short numDn = 2;
        // Number of blocks to write
        final short numBlocks = 3;
        // Set a small-ish blocksize
        final long blockSize = 512;
        String[] racks = new String[]{ "/rack1", "/rack1" };
        String[] hosts = new String[]{ "host1", "host2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        DistributedFileSystem dfs = null;
        // Startup a minicluster
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        // Create a file that will be intentionally under-replicated
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        long fileLen = blockSize * numBlocks;
        DFSTestUtil.createFile(dfs, path, fileLen, replFactor, 1);
        // Create an under-replicated file
        NameNode namenode = cluster.getNameNode();
        NetworkTopology nettop = cluster.getNamesystem().getBlockManager().getDatanodeManager().getNetworkTopology();
        // Add a new node on different rack, so previous blocks' replicas
        // are considered to be misplaced
        nettop.add(DFSTestUtil.getDatanodeDescriptor("/rack2", "/host3"));
        numDn++;
        Map<String, String[]> pmap = new HashMap<String, String[]>();
        Writer result = new StringWriter();
        PrintWriter out = new PrintWriter(result, true);
        InetAddress remoteAddress = InetAddress.getLocalHost();
        NamenodeFsck fsck = new NamenodeFsck(conf, namenode, nettop, pmap, out, numDn, remoteAddress);
        // Run the fsck and check the Result
        final HdfsFileStatus file = namenode.getRpcServer().getFileInfo(pathString);
        Assert.assertNotNull(file);
        Result replRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ReplicationResult(conf);
        Result ecRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ErasureCodingResult(conf);
        fsck.check(pathString, file, replRes, ecRes);
        // check misReplicatedBlock number.
        Assert.assertEquals(replRes.numMisReplicatedBlocks, numBlocks);
    }

    /**
     * Test fsck with FileNotFound.
     */
    @Test
    public void testFsckFileNotFound() throws Exception {
        // Number of replicas to actually start
        final short numReplicas = 1;
        NameNode namenode = Mockito.mock(NameNode.class);
        NetworkTopology nettop = Mockito.mock(NetworkTopology.class);
        Map<String, String[]> pmap = new HashMap<>();
        Writer result = new StringWriter();
        PrintWriter out = new PrintWriter(result, true);
        InetAddress remoteAddress = InetAddress.getLocalHost();
        FSNamesystem fsName = Mockito.mock(FSNamesystem.class);
        FSDirectory fsd = Mockito.mock(FSDirectory.class);
        BlockManager blockManager = Mockito.mock(BlockManager.class);
        DatanodeManager dnManager = Mockito.mock(DatanodeManager.class);
        INodesInPath iip = Mockito.mock(INodesInPath.class);
        Mockito.when(namenode.getNamesystem()).thenReturn(fsName);
        Mockito.when(fsName.getBlockManager()).thenReturn(blockManager);
        Mockito.when(fsName.getFSDirectory()).thenReturn(fsd);
        Mockito.when(fsd.getFSNamesystem()).thenReturn(fsName);
        Mockito.when(fsd.resolvePath(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(DirOp.class))).thenReturn(iip);
        Mockito.when(blockManager.getDatanodeManager()).thenReturn(dnManager);
        NamenodeFsck fsck = new NamenodeFsck(conf, namenode, nettop, pmap, out, numReplicas, remoteAddress);
        String pathString = "/tmp/testFile";
        HdfsFileStatus file = new HdfsFileStatus.Builder().length(123L).replication(1).blocksize((128 * 1024L)).mtime(123123123L).atime(123123120L).perm(FsPermission.getDefault()).owner("foo").group("bar").path(DFSUtil.string2Bytes(pathString)).fileId(312321L).children(1).storagePolicy(BLOCK_STORAGE_POLICY_ID_UNSPECIFIED).build();
        Result replRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ReplicationResult(conf);
        Result ecRes = new org.apache.hadoop.hdfs.server.namenode.NamenodeFsck.ErasureCodingResult(conf);
        try {
            fsck.check(pathString, file, replRes, ecRes);
        } catch (Exception e) {
            Assert.fail(("Unexpected exception " + (e.getMessage())));
        }
        Assert.assertTrue(replRes.isHealthy());
    }

    /**
     * Test fsck with symlinks in the filesystem.
     */
    @Test
    public void testFsckSymlink() throws Exception {
        final DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        FileSystem fs = null;
        final long precision = 1L;
        conf.setLong(DFS_NAMENODE_ACCESSTIME_PRECISION_KEY, precision);
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(4).build();
        fs = cluster.getFileSystem();
        final String fileName = "/srcdat";
        util.createFiles(fs, fileName);
        final FileContext fc = FileContext.getFileContext(cluster.getConfiguration(0));
        final Path file = new Path(fileName);
        final Path symlink = new Path("/srcdat-symlink");
        fc.createSymlink(file, symlink, false);
        util.waitReplication(fs, fileName, ((short) (3)));
        long aTime = fc.getFileStatus(symlink).getAccessTime();
        Thread.sleep(precision);
        setupAuditLogs();
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        verifyAuditLogs();
        Assert.assertEquals(aTime, fc.getFileStatus(symlink).getAccessTime());
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        Assert.assertTrue(outStr.contains("Total symlinks:\t\t1"));
        util.cleanup(fs, fileName);
    }

    /**
     * Test for including the snapshot files in fsck report.
     */
    @Test
    public void testFsckForSnapshotFiles() throws Exception {
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(1).build();
        String runFsck = TestFsck.runFsck(conf, 0, true, "/", "-includeSnapshots", "-files");
        Assert.assertTrue(runFsck.contains("HEALTHY"));
        final String fileName = "/srcdat";
        DistributedFileSystem hdfs = cluster.getFileSystem();
        Path file1 = new Path(fileName);
        DFSTestUtil.createFile(hdfs, file1, 1024, ((short) (1)), 1000L);
        hdfs.allowSnapshot(new Path("/"));
        hdfs.createSnapshot(new Path("/"), "mySnapShot");
        runFsck = TestFsck.runFsck(conf, 0, true, "/", "-includeSnapshots", "-files");
        Assert.assertTrue(runFsck.contains("/.snapshot/mySnapShot/srcdat"));
        runFsck = TestFsck.runFsck(conf, 0, true, "/", "-files");
        Assert.assertFalse(runFsck.contains("mySnapShot"));
    }

    /**
     * Test for blockIdCK.
     */
    @Test
    public void testBlockIdCK() throws Exception {
        final short replFactor = 2;
        short numDn = 2;
        final long blockSize = 512;
        String[] racks = new String[]{ "/rack1", "/rack2" };
        String[] hosts = new String[]{ "host1", "host2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, 2);
        DistributedFileSystem dfs = null;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        util.createFile(dfs, path, 1024, replFactor, 1000L);
        util.waitReplication(dfs, path, replFactor);
        StringBuilder sb = new StringBuilder();
        for (LocatedBlock lb : util.getAllBlocks(dfs, path)) {
            sb.append(((lb.getBlock().getLocalBlock().getBlockName()) + " "));
        }
        String[] bIds = sb.toString().split(" ");
        // run fsck
        // illegal input test
        String runFsckResult = TestFsck.runFsck(conf, 0, true, "/", "-blockId", "not_a_block_id");
        Assert.assertTrue(runFsckResult.contains("Incorrect blockId format:"));
        // general test
        runFsckResult = TestFsck.runFsck(conf, 0, true, "/", "-blockId", sb.toString());
        Assert.assertTrue(runFsckResult.contains(bIds[0]));
        Assert.assertTrue(runFsckResult.contains(bIds[1]));
        Assert.assertTrue(runFsckResult.contains("Block replica on datanode/rack: host1/rack1 is HEALTHY"));
        Assert.assertTrue(runFsckResult.contains("Block replica on datanode/rack: host2/rack2 is HEALTHY"));
    }

    /**
     * Test for blockIdCK with datanode decommission.
     */
    @Test
    public void testBlockIdCKDecommission() throws Exception {
        final short replFactor = 1;
        short numDn = 2;
        final long blockSize = 512;
        boolean checkDecommissionInProgress = false;
        String[] racks = new String[]{ "/rack1", "/rack2" };
        String[] hosts = new String[]{ "host1", "host2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, 2);
        DistributedFileSystem dfs;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        util.createFile(dfs, path, 1024, replFactor, 1000L);
        util.waitReplication(dfs, path, replFactor);
        StringBuilder sb = new StringBuilder();
        for (LocatedBlock lb : util.getAllBlocks(dfs, path)) {
            sb.append(((lb.getBlock().getLocalBlock().getBlockName()) + " "));
        }
        String[] bIds = sb.toString().split(" ");
        // make sure datanode that has replica is fine before decommission
        String outStr = TestFsck.runFsck(conf, 0, true, "/", "-blockId", bIds[0]);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // decommission datanode
        FSNamesystem fsn = cluster.getNameNode().getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        ExtendedBlock eb = util.getFirstBlock(dfs, path);
        BlockCollection bc = null;
        try {
            fsn.writeLock();
            BlockInfo bi = bm.getStoredBlock(eb.getLocalBlock());
            bc = fsn.getBlockCollection(bi);
        } finally {
            fsn.writeUnlock();
        }
        DatanodeDescriptor dn = bc.getBlocks()[0].getDatanode(0);
        bm.getDatanodeManager().getDatanodeAdminManager().startDecommission(dn);
        String dnName = dn.getXferAddr();
        // wait for decommission start
        DatanodeInfo datanodeInfo = null;
        int count = 0;
        do {
            Thread.sleep(2000);
            for (DatanodeInfo info : dfs.getDataNodeStats()) {
                if (dnName.equals(info.getXferAddr())) {
                    datanodeInfo = info;
                }
            }
            // check decommissioning only once
            if (((!checkDecommissionInProgress) && (datanodeInfo != null)) && (datanodeInfo.isDecommissionInProgress())) {
                String fsckOut = TestFsck.runFsck(conf, 3, true, "/", "-blockId", bIds[0]);
                Assert.assertTrue(fsckOut.contains(DECOMMISSIONING_STATUS));
                checkDecommissionInProgress = true;
            }
        } while ((datanodeInfo != null) && (!(datanodeInfo.isDecommissioned())) );
        // check decommissioned
        String fsckOut = TestFsck.runFsck(conf, 2, true, "/", "-blockId", bIds[0]);
        Assert.assertTrue(fsckOut.contains(DECOMMISSIONED_STATUS));
    }

    /**
     * Test for blockIdCK with datanode maintenance.
     */
    @Test(timeout = 90000)
    public void testBlockIdCKMaintenance() throws Exception {
        final short replFactor = 2;
        short numDn = 2;
        final long blockSize = 512;
        String[] hosts = new String[]{ "host1", "host2" };
        String[] racks = new String[]{ "/rack1", "/rack2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, replFactor);
        conf.setInt(DFS_NAMENODE_REPLICATION_MIN_KEY, replFactor);
        conf.setInt(DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY, replFactor);
        DistributedFileSystem dfs;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        util.createFile(dfs, path, 1024, replFactor, 1000L);
        util.waitReplication(dfs, path, replFactor);
        StringBuilder sb = new StringBuilder();
        for (LocatedBlock lb : util.getAllBlocks(dfs, path)) {
            sb.append(((lb.getBlock().getLocalBlock().getBlockName()) + " "));
        }
        String[] bIds = sb.toString().split(" ");
        // make sure datanode that has replica is fine before maintenance
        String outStr = TestFsck.runFsck(conf, 0, true, "/", "-maintenance", "-blockId", bIds[0]);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        FSNamesystem fsn = cluster.getNameNode().getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        DatanodeManager dnm = bm.getDatanodeManager();
        DatanodeDescriptor dn = dnm.getDatanode(cluster.getDataNodes().get(0).getDatanodeId());
        bm.getDatanodeManager().getDatanodeAdminManager().startMaintenance(dn, Long.MAX_VALUE);
        final String dnName = dn.getXferAddr();
        // wait for the node to enter maintenance state
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                DatanodeInfo datanodeInfo = null;
                try {
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dnName.equals(info.getXferAddr())) {
                            datanodeInfo = info;
                        }
                    }
                    if ((datanodeInfo != null) && (datanodeInfo.isEnteringMaintenance())) {
                        String fsckOut = TestFsck.runFsck(conf, 5, false, "/", "-maintenance", "-blockId", bIds[0]);
                        Assert.assertTrue(fsckOut.contains(ENTERING_MAINTENANCE_STATUS));
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // Start 3rd DataNode
        cluster.startDataNodes(conf, 1, true, null, new String[]{ "/rack3" }, new String[]{ "host3" }, null, false);
        // Wait for 1st node to reach in maintenance state
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    DatanodeInfo datanodeInfo = null;
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dnName.equals(info.getXferAddr())) {
                            datanodeInfo = info;
                        }
                    }
                    if ((datanodeInfo != null) && (datanodeInfo.isInMaintenance())) {
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // check in maintenance node
        String fsckOut = TestFsck.runFsck(conf, 4, false, "/", "-maintenance", "-blockId", bIds[0]);
        Assert.assertTrue(fsckOut.contains(IN_MAINTENANCE_STATUS));
        // check in maintenance node are not printed when not requested
        fsckOut = TestFsck.runFsck(conf, 4, false, "/", "-blockId", bIds[0]);
        Assert.assertFalse(fsckOut.contains(IN_MAINTENANCE_STATUS));
    }

    /**
     * Test for blockIdCK with block corruption.
     */
    @Test
    public void testBlockIdCKCorruption() throws Exception {
        short numDn = 1;
        final long blockSize = 512;
        Random random = new Random();
        ExtendedBlock block;
        short repFactor = 1;
        String[] racks = new String[]{ "/rack1" };
        String[] hosts = new String[]{ "host1" };
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000);
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, 1);
        DistributedFileSystem dfs = null;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String pathString = new String("/testfile");
        final Path path = new Path(pathString);
        util.createFile(dfs, path, 1024, repFactor, 1000L);
        util.waitReplication(dfs, path, repFactor);
        StringBuilder sb = new StringBuilder();
        for (LocatedBlock lb : util.getAllBlocks(dfs, path)) {
            sb.append(((lb.getBlock().getLocalBlock().getBlockName()) + " "));
        }
        String[] bIds = sb.toString().split(" ");
        // make sure block is healthy before we corrupt it
        String outStr = TestFsck.runFsck(conf, 0, true, "/", "-blockId", bIds[0]);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // corrupt replicas
        block = DFSTestUtil.getFirstBlock(dfs, path);
        File blockFile = cluster.getBlockFile(0, block);
        if ((blockFile != null) && (blockFile.exists())) {
            RandomAccessFile raFile = new RandomAccessFile(blockFile, "rw");
            FileChannel channel = raFile.getChannel();
            String badString = "BADBAD";
            int rand = random.nextInt((((int) (channel.size())) / 2));
            raFile.seek(rand);
            raFile.write(badString.getBytes());
            raFile.close();
        }
        util.waitCorruptReplicas(dfs, cluster.getNamesystem(), path, block, 1);
        outStr = TestFsck.runFsck(conf, 1, false, "/", "-blockId", block.getBlockName());
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
    }

    /**
     * Test storage policy display.
     */
    @Test
    public void testStoragePoliciesCK() throws Exception {
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(3).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).build();
        cluster.waitActive();
        final DistributedFileSystem dfs = cluster.getFileSystem();
        writeFile(dfs, "/testhot", "file", "HOT");
        writeFile(dfs, "/testwarm", "file", "WARM");
        writeFile(dfs, "/testcold", "file", "COLD");
        String outStr = TestFsck.runFsck(conf, 0, true, "/", "-storagepolicies");
        Assert.assertTrue(outStr.contains("DISK:3(HOT)"));
        Assert.assertTrue(outStr.contains("DISK:1,ARCHIVE:2(WARM)"));
        Assert.assertTrue(outStr.contains("ARCHIVE:3(COLD)"));
        Assert.assertTrue(outStr.contains("All blocks satisfy specified storage policy."));
        dfs.setStoragePolicy(new Path("/testhot"), "COLD");
        dfs.setStoragePolicy(new Path("/testwarm"), "COLD");
        outStr = TestFsck.runFsck(conf, 0, true, "/", "-storagepolicies");
        Assert.assertTrue(outStr.contains("DISK:3(HOT)"));
        Assert.assertTrue(outStr.contains("DISK:1,ARCHIVE:2(WARM)"));
        Assert.assertTrue(outStr.contains("ARCHIVE:3(COLD)"));
        Assert.assertFalse(outStr.contains("All blocks satisfy specified storage policy."));
    }

    /**
     * Test for blocks on decommissioning hosts are not shown as missing.
     */
    @Test
    public void testFsckWithDecommissionedReplicas() throws Exception {
        final short replFactor = 1;
        short numDn = 2;
        final long blockSize = 512;
        final long fileSize = 1024;
        boolean checkDecommissionInProgress = false;
        String[] racks = new String[]{ "/rack1", "/rack2" };
        String[] hosts = new String[]{ "host1", "host2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, 1);
        DistributedFileSystem dfs;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String testFile = new String("/testfile");
        final Path path = new Path(testFile);
        util.createFile(dfs, path, fileSize, replFactor, 1000L);
        util.waitReplication(dfs, path, replFactor);
        // make sure datanode that has replica is fine before decommission
        String outStr = TestFsck.runFsck(conf, 0, true, testFile);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // decommission datanode
        FSNamesystem fsn = cluster.getNameNode().getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        ExtendedBlock eb = util.getFirstBlock(dfs, path);
        BlockCollection bc = null;
        try {
            fsn.writeLock();
            BlockInfo bi = bm.getStoredBlock(eb.getLocalBlock());
            bc = fsn.getBlockCollection(bi);
        } finally {
            fsn.writeUnlock();
        }
        DatanodeDescriptor dn = bc.getBlocks()[0].getDatanode(0);
        bm.getDatanodeManager().getDatanodeAdminManager().startDecommission(dn);
        String dnName = dn.getXferAddr();
        // wait for decommission start
        DatanodeInfo datanodeInfo = null;
        int count = 0;
        do {
            Thread.sleep(2000);
            for (DatanodeInfo info : dfs.getDataNodeStats()) {
                if (dnName.equals(info.getXferAddr())) {
                    datanodeInfo = info;
                }
            }
            // check the replica status should be healthy(0)
            // instead of corruption (1) during decommissioning
            if (((!checkDecommissionInProgress) && (datanodeInfo != null)) && (datanodeInfo.isDecommissionInProgress())) {
                String fsckOut = TestFsck.runFsck(conf, 0, true, testFile);
                checkDecommissionInProgress = true;
            }
        } while ((datanodeInfo != null) && (!(datanodeInfo.isDecommissioned())) );
        // check the replica status should be healthy(0) after decommission
        // is done
        String fsckOut = TestFsck.runFsck(conf, 0, true, testFile);
    }

    /**
     * Test for blocks on maintenance hosts are not shown as missing.
     */
    @Test(timeout = 90000)
    public void testFsckWithMaintenanceReplicas() throws Exception {
        final short replFactor = 2;
        short numDn = 2;
        final long blockSize = 512;
        String[] hosts = new String[]{ "host1", "host2" };
        String[] racks = new String[]{ "/rack1", "/rack2" };
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setInt(DFS_REPLICATION_KEY, replFactor);
        conf.setInt(DFS_NAMENODE_REPLICATION_MIN_KEY, replFactor);
        conf.setInt(DFS_NAMENODE_MAINTENANCE_REPLICATION_MIN_KEY, replFactor);
        DistributedFileSystem dfs;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(numDn).hosts(hosts).racks(racks).build();
        Assert.assertNotNull("Failed Cluster Creation", cluster);
        cluster.waitClusterUp();
        dfs = cluster.getFileSystem();
        Assert.assertNotNull("Failed to get FileSystem", dfs);
        DFSTestUtil util = new DFSTestUtil.Builder().setName(getClass().getSimpleName()).setNumFiles(1).build();
        // create files
        final String testFile = new String("/testfile");
        final Path path = new Path(testFile);
        util.createFile(dfs, path, 1024, replFactor, 1000L);
        util.waitReplication(dfs, path, replFactor);
        StringBuilder sb = new StringBuilder();
        for (LocatedBlock lb : util.getAllBlocks(dfs, path)) {
            sb.append(((lb.getBlock().getLocalBlock().getBlockName()) + " "));
        }
        String[] bIds = sb.toString().split(" ");
        // make sure datanode that has replica is fine before maintenance
        String outStr = TestFsck.runFsck(conf, 0, true, testFile);
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        FSNamesystem fsn = cluster.getNameNode().getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        DatanodeManager dnm = bm.getDatanodeManager();
        DatanodeDescriptor dn = dnm.getDatanode(cluster.getDataNodes().get(0).getDatanodeId());
        bm.getDatanodeManager().getDatanodeAdminManager().startMaintenance(dn, Long.MAX_VALUE);
        final String dnName = dn.getXferAddr();
        // wait for the node to enter maintenance state
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                DatanodeInfo datanodeInfo = null;
                try {
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dnName.equals(info.getXferAddr())) {
                            datanodeInfo = info;
                        }
                    }
                    if ((datanodeInfo != null) && (datanodeInfo.isEnteringMaintenance())) {
                        // verify fsck returns Healthy status
                        String fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-maintenance");
                        Assert.assertTrue(fsckOut.contains(HEALTHY_STATUS));
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // Start 3rd DataNode and wait for node to reach in maintenance state
        cluster.startDataNodes(conf, 1, true, null, new String[]{ "/rack3" }, new String[]{ "host3" }, null, false);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                DatanodeInfo datanodeInfo = null;
                try {
                    for (DatanodeInfo info : dfs.getDataNodeStats()) {
                        if (dnName.equals(info.getXferAddr())) {
                            datanodeInfo = info;
                        }
                    }
                    if ((datanodeInfo != null) && (datanodeInfo.isInMaintenance())) {
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.warn(("Unexpected exception: " + e));
                    return false;
                }
                return false;
            }
        }, 500, 30000);
        // verify fsck returns Healthy status
        String fsckOut = TestFsck.runFsck(conf, 0, true, testFile, "-maintenance");
        Assert.assertTrue(fsckOut.contains(HEALTHY_STATUS));
        // verify fsck returns Healthy status even without maintenance option
        fsckOut = TestFsck.runFsck(conf, 0, true, testFile);
        Assert.assertTrue(fsckOut.contains(HEALTHY_STATUS));
    }

    @Test
    public void testECFsck() throws Exception {
        DistributedFileSystem fs = null;
        final long precision = 1L;
        conf.setLong(DFS_NAMENODE_ACCESSTIME_PRECISION_KEY, precision);
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10000L);
        int dataBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumDataUnits();
        int parityBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumParityUnits();
        int totalSize = dataBlocks + parityBlocks;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(totalSize).build();
        fs = cluster.getFileSystem();
        fs.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
        // create a contiguous file
        Path replDirPath = new Path("/replicated");
        Path replFilePath = new Path(replDirPath, "replfile");
        final short factor = 3;
        DFSTestUtil.createFile(fs, replFilePath, 1024, factor, 0);
        DFSTestUtil.waitReplication(fs, replFilePath, factor);
        // create a large striped file
        Path ecDirPath = new Path("/striped");
        Path largeFilePath = new Path(ecDirPath, "largeFile");
        DFSTestUtil.createStripedFile(cluster, largeFilePath, ecDirPath, 1, 2, true);
        // create a small striped file
        Path smallFilePath = new Path(ecDirPath, "smallFile");
        DFSTestUtil.writeFile(fs, smallFilePath, "hello world!");
        long replTime = fs.getFileStatus(replFilePath).getAccessTime();
        long ecTime = fs.getFileStatus(largeFilePath).getAccessTime();
        Thread.sleep(precision);
        setupAuditLogs();
        String outStr = TestFsck.runFsck(conf, 0, true, "/");
        verifyAuditLogs();
        Assert.assertEquals(replTime, fs.getFileStatus(replFilePath).getAccessTime());
        Assert.assertEquals(ecTime, fs.getFileStatus(largeFilePath).getAccessTime());
        System.out.println(outStr);
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        shutdownCluster();
        // restart the cluster; bring up namenode but not the data nodes
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).build();
        outStr = TestFsck.runFsck(conf, 1, true, "/", "-files", "-blocks");
        // expect the result is corrupt
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        String[] outLines = outStr.split("\\r?\\n");
        for (String line : outLines) {
            if (line.contains(largeFilePath.toString())) {
                final HdfsFileStatus file = cluster.getNameNode().getRpcServer().getFileInfo(largeFilePath.toString());
                Assert.assertTrue(line.contains(("policy=" + (file.getErasureCodingPolicy().getName()))));
            } else
                if (line.contains(replFilePath.toString())) {
                    Assert.assertTrue(line.contains(("replication=" + (cluster.getFileSystem().getFileStatus(replFilePath).getReplication()))));
                }

        }
        System.out.println(outStr);
    }

    /**
     * Test that corrupted snapshot files are listed with full dir.
     */
    @Test
    public void testFsckListCorruptSnapshotFiles() throws Exception {
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000);
        conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
        DistributedFileSystem hdfs = null;
        final short replFactor = 1;
        int numFiles = 3;
        int numSnapshots = 0;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).build();
        cluster.waitActive();
        hdfs = cluster.getFileSystem();
        DFSTestUtil util = new DFSTestUtil.Builder().setName("testGetCorruptFiles").setNumFiles(numFiles).setMaxLevels(1).setMaxSize(1024).build();
        util.createFiles(hdfs, "/corruptData", ((short) (1)));
        final Path fp = new Path("/corruptData/file");
        util.createFile(hdfs, fp, 1024, replFactor, 1000L);
        numFiles++;
        util.waitReplication(hdfs, "/corruptData", ((short) (1)));
        hdfs.allowSnapshot(new Path("/corruptData"));
        hdfs.createSnapshot(new Path("/corruptData"), "mySnapShot");
        numSnapshots = numFiles;
        String outStr = TestFsck.runFsck(conf, 0, false, "/corruptData", "-list-corruptfileblocks");
        System.out.println(("1. good fsck out: " + outStr));
        Assert.assertTrue(outStr.contains("has 0 CORRUPT files"));
        // delete the blocks
        final String bpid = cluster.getNamesystem().getBlockPoolId();
        for (int i = 0; i < numFiles; i++) {
            for (int j = 0; j <= 1; j++) {
                File storageDir = cluster.getInstanceStorageDir(i, j);
                File dataDir = MiniDFSCluster.getFinalizedDir(storageDir, bpid);
                List<File> metadataFiles = MiniDFSCluster.getAllBlockMetadataFiles(dataDir);
                if (metadataFiles == null) {
                    continue;
                }
                for (File metadataFile : metadataFiles) {
                    File blockFile = Block.metaToBlockFile(metadataFile);
                    Assert.assertTrue("Cannot remove file.", blockFile.delete());
                    Assert.assertTrue("Cannot remove file.", metadataFile.delete());
                }
            }
        }
        // Delete file when it has a snapshot
        hdfs.delete(fp, false);
        numFiles--;
        // wait for the namenode to see the corruption
        final NamenodeProtocols namenode = cluster.getNameNodeRpc();
        CorruptFileBlocks corruptFileBlocks = namenode.listCorruptFileBlocks("/corruptData", null);
        int numCorrupt = corruptFileBlocks.getFiles().length;
        while (numCorrupt == 0) {
            Thread.sleep(1000);
            corruptFileBlocks = namenode.listCorruptFileBlocks("/corruptData", null);
            numCorrupt = corruptFileBlocks.getFiles().length;
        } 
        // with -includeSnapshots all files are reported
        outStr = TestFsck.runFsck(conf, (-1), true, "/corruptData", "-list-corruptfileblocks", "-includeSnapshots");
        System.out.println(("2. bad fsck include snapshot out: " + outStr));
        Assert.assertTrue(outStr.contains((("has " + (numFiles + numSnapshots)) + " CORRUPT files")));
        Assert.assertTrue(outStr.contains("/.snapshot/"));
        // without -includeSnapshots only non-snapshots are reported
        outStr = TestFsck.runFsck(conf, (-1), true, "/corruptData", "-list-corruptfileblocks");
        System.out.println(("3. bad fsck exclude snapshot out: " + outStr));
        Assert.assertTrue(outStr.contains((("has " + numFiles) + " CORRUPT files")));
        Assert.assertFalse(outStr.contains("/.snapshot/"));
    }

    @Test(timeout = 300000)
    public void testFsckMoveAfterCorruption() throws Exception {
        final int dfsBlockSize = 512 * 1024;
        final int numDatanodes = 1;
        final int replication = 1;
        conf.setLong(DFS_BLOCK_SIZE_KEY, dfsBlockSize);
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
        conf.setInt(DFS_DATANODE_DIRECTORYSCAN_INTERVAL_KEY, 1);
        conf.setInt(DFS_REPLICATION_KEY, replication);
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).build();
        DistributedFileSystem dfs = cluster.getFileSystem();
        cluster.waitActive();
        final String srcDir = "/srcdat";
        final DFSTestUtil util = new DFSTestUtil.Builder().setName("TestFsck").setMinSize((dfsBlockSize * 2)).setMaxSize((dfsBlockSize * 3)).setNumFiles(1).build();
        util.createFiles(dfs, srcDir, ((short) (replication)));
        final String[] fileNames = util.getFileNames(srcDir);
        TestFsck.LOG.info(("Created files: " + (Arrays.toString(fileNames))));
        // Run fsck here. The output is automatically logged for easier debugging
        String outStr = TestFsck.runFsck(conf, 0, true, "/", "-files", "-blocks");
        Assert.assertTrue(outStr.contains(HEALTHY_STATUS));
        // Corrupt the first block
        final DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
        final String blockFileToCorrupt = fileNames[0];
        final TestFsck.CorruptedTestFile ctf = new TestFsck.CorruptedTestFile(blockFileToCorrupt, Sets.newHashSet(0), dfsClient, numDatanodes, dfsBlockSize);
        ctf.corruptBlocks(cluster);
        // Wait for fsck to discover all the missing blocks
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    final String str = TestFsck.runFsck(conf, 1, false, "/");
                    String numCorrupt = null;
                    for (String line : str.split(TestFsck.LINE_SEPARATOR)) {
                        Matcher m = TestFsck.NUM_CORRUPT_BLOCKS_PATTERN.matcher(line);
                        if (m.matches()) {
                            numCorrupt = m.group(1);
                            break;
                        }
                    }
                    if (numCorrupt == null) {
                        Assert.fail("Cannot find corrupt blocks count in fsck output.");
                    }
                    if ((Integer.parseInt(numCorrupt)) == (ctf.getTotalMissingBlocks())) {
                        Assert.assertTrue(str.contains(CORRUPT_STATUS));
                        return true;
                    }
                } catch (Exception e) {
                    TestFsck.LOG.error("Exception caught", e);
                    Assert.fail("Caught unexpected exception.");
                }
                return false;
            }
        }, 1000, 60000);
        TestFsck.runFsck(conf, 1, true, "/", "-files", "-blocks", "-racks");
        TestFsck.LOG.info("Moving blocks to lost+found");
        // Fsck will return error since we corrupted a block
        TestFsck.runFsck(conf, 1, false, "/", "-move");
        final List<LocatedFileStatus> retVal = new ArrayList<>();
        final RemoteIterator<LocatedFileStatus> iter = dfs.listFiles(new Path("/lost+found"), true);
        while (iter.hasNext()) {
            retVal.add(iter.next());
        } 
        TestFsck.LOG.info(("Items in lost+found: " + retVal));
        // Expect all good blocks moved, only corrupted block skipped.
        long totalLength = 0;
        for (LocatedFileStatus lfs : retVal) {
            totalLength += lfs.getLen();
        }
        Assert.assertTrue("Nothing is moved to lost+found!", (totalLength > 0));
        util.cleanup(dfs, srcDir);
    }

    @Test(timeout = 60000)
    public void testFsckUpgradeDomain() throws Exception {
        testUpgradeDomain(false, false);
        testUpgradeDomain(false, true);
        testUpgradeDomain(true, false);
        testUpgradeDomain(true, true);
    }

    @Test(timeout = 300000)
    public void testFsckCorruptECFile() throws Exception {
        DistributedFileSystem fs = null;
        int dataBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumDataUnits();
        int parityBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumParityUnits();
        int cellSize = StripedFileTestUtil.getDefaultECPolicy().getCellSize();
        int totalSize = dataBlocks + parityBlocks;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(totalSize).build();
        fs = cluster.getFileSystem();
        fs.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
        Map<Integer, Integer> dnIndices = new HashMap<>();
        ArrayList<DataNode> dnList = cluster.getDataNodes();
        for (int i = 0; i < totalSize; i++) {
            dnIndices.put(dnList.get(i).getIpcPort(), i);
        }
        // create file
        Path ecDirPath = new Path("/striped");
        fs.mkdir(ecDirPath, FsPermission.getDirDefault());
        fs.getClient().setErasureCodingPolicy(ecDirPath.toString(), StripedFileTestUtil.getDefaultECPolicy().getName());
        Path file = new Path(ecDirPath, "corrupted");
        final int length = cellSize * dataBlocks;
        final byte[] bytes = StripedFileTestUtil.generateBytes(length);
        DFSTestUtil.writeFile(fs, file, bytes);
        LocatedStripedBlock lsb = ((LocatedStripedBlock) (fs.getClient().getLocatedBlocks(file.toString(), 0, (cellSize * dataBlocks)).get(0)));
        final LocatedBlock[] blks = StripedBlockUtil.parseStripedBlockGroup(lsb, cellSize, dataBlocks, parityBlocks);
        // make an unrecoverable ec file with corrupted blocks
        for (int i = 0; i < (parityBlocks + 1); i++) {
            int ipcPort = blks[i].getLocations()[0].getIpcPort();
            int dnIndex = dnIndices.get(ipcPort);
            File storageDir = cluster.getInstanceStorageDir(dnIndex, 0);
            File blkFile = MiniDFSCluster.getBlockFile(storageDir, blks[i].getBlock());
            Assert.assertTrue("Block file does not exist", blkFile.exists());
            FileOutputStream out = new FileOutputStream(blkFile);
            out.write("corruption".getBytes());
        }
        // disable the heart beat from DN so that the corrupted block record is
        // kept in NameNode
        for (DataNode dn : cluster.getDataNodes()) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, true);
        }
        // Read the file to trigger reportBadBlocks
        try {
            IOUtils.copyBytes(fs.open(file), new IOUtils.NullOutputStream(), conf, true);
        } catch (IOException ie) {
            Assert.assertTrue(ie.getMessage().contains(("missingChunksNum=" + (parityBlocks + 1))));
        }
        waitForUnrecoverableBlockGroup(conf);
        String outStr = TestFsck.runFsck(conf, 1, true, "/");
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        Assert.assertTrue(outStr.contains("Under-erasure-coded block groups:\t0"));
        outStr = TestFsck.runFsck(conf, (-1), true, "/", "-list-corruptfileblocks");
        Assert.assertTrue(outStr.contains("has 1 CORRUPT files"));
    }

    @Test(timeout = 300000)
    public void testFsckMissingECFile() throws Exception {
        DistributedFileSystem fs = null;
        int dataBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumDataUnits();
        int parityBlocks = StripedFileTestUtil.getDefaultECPolicy().getNumParityUnits();
        int cellSize = StripedFileTestUtil.getDefaultECPolicy().getCellSize();
        int totalSize = dataBlocks + parityBlocks;
        File builderBaseDir = new File(GenericTestUtils.getRandomizedTempPath());
        cluster = new MiniDFSCluster.Builder(conf, builderBaseDir).numDataNodes(totalSize).build();
        fs = cluster.getFileSystem();
        fs.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
        // create file
        Path ecDirPath = new Path("/striped");
        fs.mkdir(ecDirPath, FsPermission.getDirDefault());
        fs.getClient().setErasureCodingPolicy(ecDirPath.toString(), StripedFileTestUtil.getDefaultECPolicy().getName());
        Path file = new Path(ecDirPath, "missing");
        final int length = cellSize * dataBlocks;
        final byte[] bytes = StripedFileTestUtil.generateBytes(length);
        DFSTestUtil.writeFile(fs, file, bytes);
        // make an unrecoverable ec file with missing blocks
        ArrayList<DataNode> dns = cluster.getDataNodes();
        DatanodeID dnId;
        for (int i = 0; i < (parityBlocks + 1); i++) {
            dnId = dns.get(i).getDatanodeId();
            cluster.stopDataNode(dnId.getXferAddr());
            cluster.setDataNodeDead(dnId);
        }
        waitForUnrecoverableBlockGroup(conf);
        String outStr = TestFsck.runFsck(conf, 1, true, "/", "-files", "-blocks", "-locations");
        Assert.assertTrue(outStr.contains(CORRUPT_STATUS));
        Assert.assertTrue(outStr.contains(("Live_repl=" + (dataBlocks - 1))));
        Assert.assertTrue(outStr.contains("Under-erasure-coded block groups:\t0"));
        outStr = TestFsck.runFsck(conf, (-1), true, "/", "-list-corruptfileblocks");
        Assert.assertTrue(outStr.contains("has 1 CORRUPT files"));
    }

    @Test(timeout = 300000)
    public void testFsckCorruptWhenOneReplicaIsCorrupt() throws Exception {
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf, new File(GenericTestUtils.getRandomizedTempPath())).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(2).build()) {
            cluster.waitActive();
            FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
            cluster.transitionToActive(0);
            String filePath = "/appendTest";
            Path fileName = new Path(filePath);
            DFSTestUtil.createFile(fs, fileName, 512, ((short) (2)), 0);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (2)));
            Assert.assertTrue("File not created", fs.exists(fileName));
            cluster.getDataNodes().get(1).shutdown();
            DFSTestUtil.appendFile(fs, fileName, "appendCorruptBlock");
            cluster.restartDataNode(1, true);
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (cluster.getNameNode(0).getNamesystem().getCorruptReplicaBlocks()) > 0;
                }
            }, 100, 5000);
            DFSTestUtil.appendFile(fs, fileName, "appendCorruptBlock");
            TestFsck.runFsck(cluster.getConfiguration(0), 0, true, "/");
        }
    }

    @Test
    public void testFsckNonPrivilegedListCorrupt() throws Exception {
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("systest", new String[]{ "" });
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                String path = "/";
                String outStr = TestFsck.runFsck(conf, (-1), true, path, "-list-corruptfileblocks");
                Assert.assertFalse(outStr.contains((("The list of corrupt files under path '" + path) + "' are:")));
                Assert.assertFalse(outStr.contains((("The filesystem under path '" + path) + "' has ")));
                Assert.assertTrue(outStr.contains((("Failed to open path '" + path) + "': Permission denied")));
                return null;
            }
        });
    }
}

