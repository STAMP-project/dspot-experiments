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
package org.apache.hadoop.hdfs.server.mover;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_MOVER_MOVEDWINWIDTH_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_DEFAULT;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY;
import DataTransferProtocol.LOG;
import ExitStatus.NO_MOVE_BLOCK;
import ExitStatus.SUCCESS;
import HdfsConstants.COLD_STORAGE_POLICY_NAME;
import HdfsConstants.HOT_STORAGE_POLICY_NAME;
import HdfsConstants.WARM_STORAGE_POLICY_NAME;
import HdfsFileStatus.EMPTY_NAME;
import Mover.Cli;
import Mover.StorageTypeDiff;
import StorageType.ARCHIVE;
import StorageType.DISK;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.protocol.DirectoryListing;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.HdfsLocatedFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.balancer.Dispatcher;
import org.apache.hadoop.hdfs.server.balancer.ExitStatus;
import org.apache.hadoop.hdfs.server.balancer.TestBalancer;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockStoragePolicySuite;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Test the data migration tool (for Archival Storage)
 */
public class TestStorageMover {
    static final Logger LOG = LoggerFactory.getLogger(TestStorageMover.class);

    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(BlockPlacementPolicy.class), Level.TRACE);
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(Dispatcher.class), Level.TRACE);
        GenericTestUtils.setLogLevel(DataTransferProtocol.LOG, Level.TRACE);
    }

    private static final int BLOCK_SIZE = 1024;

    private static final short REPL = 3;

    private static final int NUM_DATANODES = 6;

    private static final Configuration DEFAULT_CONF = new HdfsConfiguration();

    private static final BlockStoragePolicySuite DEFAULT_POLICIES;

    private static final BlockStoragePolicy HOT;

    private static final BlockStoragePolicy WARM;

    private static final BlockStoragePolicy COLD;

    static {
        TestStorageMover.DEFAULT_CONF.setLong(DFS_BLOCK_SIZE_KEY, TestStorageMover.BLOCK_SIZE);
        TestStorageMover.DEFAULT_CONF.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        TestStorageMover.DEFAULT_CONF.setLong(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 2L);
        TestStorageMover.DEFAULT_CONF.setLong(DFS_MOVER_MOVEDWINWIDTH_KEY, 2000L);
        TestStorageMover.DEFAULT_CONF.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, DFS_STORAGE_POLICY_SATISFIER_MODE_DEFAULT);
        DEFAULT_POLICIES = BlockStoragePolicySuite.createDefaultSuite();
        HOT = TestStorageMover.DEFAULT_POLICIES.getPolicy(HOT_STORAGE_POLICY_NAME);
        WARM = TestStorageMover.DEFAULT_POLICIES.getPolicy(WARM_STORAGE_POLICY_NAME);
        COLD = TestStorageMover.DEFAULT_POLICIES.getPolicy(COLD_STORAGE_POLICY_NAME);
        TestBalancer.initTestSetup();
        Dispatcher.setDelayAfterErrors(1000L);
    }

    /**
     * This scheme defines files/directories and their block storage policies. It
     * also defines snapshots.
     */
    static class NamespaceScheme {
        final List<Path> dirs;

        final List<Path> files;

        final long fileSize;

        final Map<Path, List<String>> snapshotMap;

        final Map<Path, BlockStoragePolicy> policyMap;

        NamespaceScheme(List<Path> dirs, List<Path> files, long fileSize, Map<Path, List<String>> snapshotMap, Map<Path, BlockStoragePolicy> policyMap) {
            this.dirs = (dirs == null) ? Collections.<Path>emptyList() : dirs;
            this.files = (files == null) ? Collections.<Path>emptyList() : files;
            this.fileSize = fileSize;
            this.snapshotMap = (snapshotMap == null) ? Collections.<Path, List<String>>emptyMap() : snapshotMap;
            this.policyMap = policyMap;
        }

        /**
         * Create files/directories/snapshots.
         */
        void prepare(DistributedFileSystem dfs, short repl) throws Exception {
            for (Path d : dirs) {
                dfs.mkdirs(d);
            }
            for (Path file : files) {
                DFSTestUtil.createFile(dfs, file, fileSize, repl, 0L);
            }
            for (Map.Entry<Path, List<String>> entry : snapshotMap.entrySet()) {
                for (String snapshot : entry.getValue()) {
                    SnapshotTestHelper.createSnapshot(dfs, entry.getKey(), snapshot);
                }
            }
        }

        /**
         * Set storage policies according to the corresponding scheme.
         */
        void setStoragePolicy(DistributedFileSystem dfs) throws Exception {
            for (Map.Entry<Path, BlockStoragePolicy> entry : policyMap.entrySet()) {
                dfs.setStoragePolicy(entry.getKey(), entry.getValue().getName());
            }
        }
    }

    /**
     * This scheme defines DataNodes and their storage, including storage types
     * and remaining capacities.
     */
    static class ClusterScheme {
        final Configuration conf;

        final int numDataNodes;

        final short repl;

        final StorageType[][] storageTypes;

        final long[][] storageCapacities;

        ClusterScheme() {
            this(TestStorageMover.DEFAULT_CONF, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        }

        ClusterScheme(Configuration conf, int numDataNodes, short repl, StorageType[][] types, long[][] capacities) {
            Preconditions.checkArgument(((types == null) || ((types.length) == numDataNodes)));
            Preconditions.checkArgument(((capacities == null) || ((capacities.length) == numDataNodes)));
            this.conf = conf;
            this.numDataNodes = numDataNodes;
            this.repl = repl;
            this.storageTypes = types;
            this.storageCapacities = capacities;
        }
    }

    class MigrationTest {
        private final TestStorageMover.ClusterScheme clusterScheme;

        private final TestStorageMover.NamespaceScheme nsScheme;

        private final Configuration conf;

        private MiniDFSCluster cluster;

        private DistributedFileSystem dfs;

        private final BlockStoragePolicySuite policies;

        MigrationTest(TestStorageMover.ClusterScheme cScheme, TestStorageMover.NamespaceScheme nsScheme) {
            this.clusterScheme = cScheme;
            this.nsScheme = nsScheme;
            this.conf = clusterScheme.conf;
            this.policies = TestStorageMover.DEFAULT_POLICIES;
        }

        /**
         * Set up the cluster and start NameNode and DataNodes according to the
         * corresponding scheme.
         */
        void setupCluster() throws Exception {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(clusterScheme.numDataNodes).storageTypes(clusterScheme.storageTypes).storageCapacities(clusterScheme.storageCapacities).build();
            cluster.waitActive();
            dfs = cluster.getFileSystem();
        }

        private void runBasicTest(boolean shutdown) throws Exception {
            setupCluster();
            try {
                prepareNamespace();
                verify(true);
                setStoragePolicy();
                migrate(SUCCESS);
                verify(true);
            } finally {
                if (shutdown) {
                    shutdownCluster();
                }
            }
        }

        void shutdownCluster() throws Exception {
            IOUtils.cleanup(null, dfs);
            if ((cluster) != null) {
                cluster.shutdown();
            }
        }

        /**
         * Create files/directories and set their storage policies according to the
         * corresponding scheme.
         */
        void prepareNamespace() throws Exception {
            nsScheme.prepare(dfs, clusterScheme.repl);
        }

        void setStoragePolicy() throws Exception {
            nsScheme.setStoragePolicy(dfs);
        }

        /**
         * Run the migration tool.
         */
        void migrate(ExitStatus expectedExitCode) throws Exception {
            runMover(expectedExitCode);
            Thread.sleep(5000);// let the NN finish deletion

        }

        /**
         * Verify block locations after running the migration tool.
         */
        void verify(boolean verifyAll) throws Exception {
            for (DataNode dn : cluster.getDataNodes()) {
                DataNodeTestUtils.triggerBlockReport(dn);
            }
            if (verifyAll) {
                verifyNamespace();
            }
        }

        private void runMover(ExitStatus expectedExitCode) throws Exception {
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Map<URI, List<Path>> nnMap = Maps.newHashMap();
            for (URI nn : namenodes) {
                nnMap.put(nn, null);
            }
            int result = Mover.run(nnMap, conf);
            Assert.assertEquals(expectedExitCode.getExitCode(), result);
        }

        private void verifyNamespace() throws Exception {
            HdfsFileStatus status = dfs.getClient().getFileInfo("/");
            verifyRecursively(null, status);
        }

        private void verifyRecursively(final Path parent, final HdfsFileStatus status) throws Exception {
            if (status.isDirectory()) {
                Path fullPath = (parent == null) ? new Path("/") : status.getFullPath(parent);
                DirectoryListing children = dfs.getClient().listPaths(fullPath.toString(), EMPTY_NAME, true);
                for (HdfsFileStatus child : children.getPartialListing()) {
                    verifyRecursively(fullPath, child);
                }
            } else
                if (!(status.isSymlink())) {
                    // is file
                    verifyFile(parent, status, null);
                }

        }

        void verifyFile(final Path file, final Byte expectedPolicyId) throws Exception {
            final Path parent = file.getParent();
            DirectoryListing children = dfs.getClient().listPaths(parent.toString(), EMPTY_NAME, true);
            for (HdfsFileStatus child : children.getPartialListing()) {
                if (child.getLocalName().equals(file.getName())) {
                    verifyFile(parent, child, expectedPolicyId);
                    return;
                }
            }
            Assert.fail((("File " + file) + " not found."));
        }

        private void verifyFile(final Path parent, final HdfsFileStatus status, final Byte expectedPolicyId) throws Exception {
            HdfsLocatedFileStatus fileStatus = ((HdfsLocatedFileStatus) (status));
            byte policyId = fileStatus.getStoragePolicy();
            BlockStoragePolicy policy = policies.getPolicy(policyId);
            if (expectedPolicyId != null) {
                Assert.assertEquals(((byte) (expectedPolicyId)), policy.getId());
            }
            final List<StorageType> types = policy.chooseStorageTypes(status.getReplication());
            for (LocatedBlock lb : fileStatus.getLocatedBlocks().getLocatedBlocks()) {
                final Mover.StorageTypeDiff diff = new Mover.StorageTypeDiff(types, lb.getStorageTypes());
                Assert.assertTrue((((((((fileStatus.getFullName(parent.toString())) + " with policy ") + policy) + " has non-empty overlap: ") + diff) + ", the corresponding block is ") + (lb.getBlock().getLocalBlock())), diff.removeOverlap(true));
            }
        }

        TestStorageMover.Replication getReplication(Path file) throws IOException {
            return getOrVerifyReplication(file, null);
        }

        TestStorageMover.Replication verifyReplication(Path file, int expectedDiskCount, int expectedArchiveCount) throws IOException {
            final TestStorageMover.Replication r = new TestStorageMover.Replication();
            r.disk = expectedDiskCount;
            r.archive = expectedArchiveCount;
            return getOrVerifyReplication(file, r);
        }

        private TestStorageMover.Replication getOrVerifyReplication(Path file, TestStorageMover.Replication expected) throws IOException {
            final List<LocatedBlock> lbs = dfs.getClient().getLocatedBlocks(file.toString(), 0).getLocatedBlocks();
            Assert.assertEquals(1, lbs.size());
            LocatedBlock lb = lbs.get(0);
            StringBuilder types = new StringBuilder();
            final TestStorageMover.Replication r = new TestStorageMover.Replication();
            for (StorageType t : lb.getStorageTypes()) {
                types.append(t).append(", ");
                if (t == (StorageType.DISK)) {
                    (r.disk)++;
                } else
                    if (t == (StorageType.ARCHIVE)) {
                        (r.archive)++;
                    } else {
                        Assert.fail(("Unexpected storage type " + t));
                    }

            }
            if (expected != null) {
                final String s = ((("file = " + file) + "\n  types = [") + types) + "]";
                Assert.assertEquals(s, expected, r);
            }
            return r;
        }
    }

    static class Replication {
        int disk;

        int archive;

        @Override
        public int hashCode() {
            return (disk) ^ (archive);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this)) {
                return true;
            } else
                if ((obj == null) || (!(obj instanceof TestStorageMover.Replication))) {
                    return false;
                }

            final TestStorageMover.Replication that = ((TestStorageMover.Replication) (obj));
            return ((this.disk) == (that.disk)) && ((this.archive) == (that.archive));
        }

        @Override
        public String toString() {
            return ((("[disk=" + (disk)) + ", archive=") + (archive)) + "]";
        }
    }

    private static class PathPolicyMap {
        final Map<Path, BlockStoragePolicy> map = Maps.newHashMap();

        final Path hot = new Path("/hot");

        final Path warm = new Path("/warm");

        final Path cold = new Path("/cold");

        final List<Path> files;

        PathPolicyMap(int filesPerDir) {
            map.put(hot, TestStorageMover.HOT);
            map.put(warm, TestStorageMover.WARM);
            map.put(cold, TestStorageMover.COLD);
            files = new ArrayList<Path>();
            for (Path dir : map.keySet()) {
                for (int i = 0; i < filesPerDir; i++) {
                    files.add(new Path(dir, ("file" + i)));
                }
            }
        }

        TestStorageMover.NamespaceScheme newNamespaceScheme() {
            return new TestStorageMover.NamespaceScheme(Arrays.asList(hot, warm, cold), files, ((TestStorageMover.BLOCK_SIZE) / 2), null, map);
        }

        /**
         * Move hot files to warm and cold, warm files to hot and cold,
         * and cold files to hot and warm.
         */
        void moveAround(DistributedFileSystem dfs) throws Exception {
            for (Path srcDir : map.keySet()) {
                int i = 0;
                for (Path dstDir : map.keySet()) {
                    if (!(srcDir.equals(dstDir))) {
                        final Path src = new Path(srcDir, ("file" + (i++)));
                        final Path dst = new Path(dstDir, (((srcDir.getName()) + "2") + (dstDir.getName())));
                        TestStorageMover.LOG.info(((("rename " + src) + " to ") + dst));
                        dfs.rename(src, dst);
                    }
                }
            }
        }
    }

    /**
     * A normal case for Mover: move a file into archival storage
     */
    @Test
    public void testMigrateFileToArchival() throws Exception {
        TestStorageMover.LOG.info("testMigrateFileToArchival");
        final Path foo = new Path("/foo");
        Map<Path, BlockStoragePolicy> policyMap = Maps.newHashMap();
        policyMap.put(foo, TestStorageMover.COLD);
        TestStorageMover.NamespaceScheme nsScheme = new TestStorageMover.NamespaceScheme(null, Arrays.asList(foo), (2 * (TestStorageMover.BLOCK_SIZE)), null, policyMap);
        TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme(TestStorageMover.DEFAULT_CONF, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        new TestStorageMover.MigrationTest(clusterScheme, nsScheme).runBasicTest(true);
    }

    /**
     * Run Mover with arguments specifying files and directories
     */
    @Test
    public void testMoveSpecificPaths() throws Exception {
        TestStorageMover.LOG.info("testMoveSpecificPaths");
        final Path foo = new Path("/foo");
        final Path barFile = new Path(foo, "bar");
        final Path foo2 = new Path("/foo2");
        final Path bar2File = new Path(foo2, "bar2");
        Map<Path, BlockStoragePolicy> policyMap = Maps.newHashMap();
        policyMap.put(foo, TestStorageMover.COLD);
        policyMap.put(foo2, TestStorageMover.WARM);
        TestStorageMover.NamespaceScheme nsScheme = new TestStorageMover.NamespaceScheme(Arrays.asList(foo, foo2), Arrays.asList(barFile, bar2File), TestStorageMover.BLOCK_SIZE, null, policyMap);
        TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme(TestStorageMover.DEFAULT_CONF, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        TestStorageMover.MigrationTest test = new TestStorageMover.MigrationTest(clusterScheme, nsScheme);
        test.setupCluster();
        try {
            test.prepareNamespace();
            test.setStoragePolicy();
            Map<URI, List<Path>> map = Cli.getNameNodePathsToMove(test.conf, "-p", "/foo/bar", "/foo2");
            int result = Mover.run(map, test.conf);
            Assert.assertEquals(SUCCESS.getExitCode(), result);
            Thread.sleep(5000);
            test.verify(true);
        } finally {
            test.shutdownCluster();
        }
    }

    /**
     * Move an open file into archival storage
     */
    @Test
    public void testMigrateOpenFileToArchival() throws Exception {
        TestStorageMover.LOG.info("testMigrateOpenFileToArchival");
        final Path fooDir = new Path("/foo");
        Map<Path, BlockStoragePolicy> policyMap = Maps.newHashMap();
        policyMap.put(fooDir, TestStorageMover.COLD);
        TestStorageMover.NamespaceScheme nsScheme = new TestStorageMover.NamespaceScheme(Arrays.asList(fooDir), null, TestStorageMover.BLOCK_SIZE, null, policyMap);
        TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme(TestStorageMover.DEFAULT_CONF, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        TestStorageMover.MigrationTest test = new TestStorageMover.MigrationTest(clusterScheme, nsScheme);
        test.setupCluster();
        // create an open file
        TestStorageMover.banner("writing to file /foo/bar");
        final Path barFile = new Path(fooDir, "bar");
        DFSTestUtil.createFile(test.dfs, barFile, TestStorageMover.BLOCK_SIZE, ((short) (1)), 0L);
        FSDataOutputStream out = test.dfs.append(barFile);
        out.writeBytes("hello, ");
        hsync();
        try {
            TestStorageMover.banner("start data migration");
            test.setStoragePolicy();// set /foo to COLD

            test.migrate(SUCCESS);
            // make sure the under construction block has not been migrated
            LocatedBlocks lbs = test.dfs.getClient().getLocatedBlocks(barFile.toString(), TestStorageMover.BLOCK_SIZE);
            TestStorageMover.LOG.info(("Locations: " + lbs));
            List<LocatedBlock> blks = lbs.getLocatedBlocks();
            Assert.assertEquals(1, blks.size());
            Assert.assertEquals(1, blks.get(0).getLocations().length);
            TestStorageMover.banner("finish the migration, continue writing");
            // make sure the writing can continue
            out.writeBytes("world!");
            hsync();
            IOUtils.cleanupWithLogger(TestStorageMover.LOG, out);
            lbs = test.dfs.getClient().getLocatedBlocks(barFile.toString(), TestStorageMover.BLOCK_SIZE);
            TestStorageMover.LOG.info(("Locations: " + lbs));
            blks = lbs.getLocatedBlocks();
            Assert.assertEquals(1, blks.size());
            Assert.assertEquals(1, blks.get(0).getLocations().length);
            TestStorageMover.banner("finish writing, starting reading");
            // check the content of /foo/bar
            FSDataInputStream in = test.dfs.open(barFile);
            byte[] buf = new byte[13];
            // read from offset 1024
            in.readFully(TestStorageMover.BLOCK_SIZE, buf, 0, buf.length);
            IOUtils.cleanupWithLogger(TestStorageMover.LOG, in);
            Assert.assertEquals("hello, world!", new String(buf));
        } finally {
            test.shutdownCluster();
        }
    }

    /**
     * Test directories with Hot, Warm and Cold polices.
     */
    @Test
    public void testHotWarmColdDirs() throws Exception {
        TestStorageMover.LOG.info("testHotWarmColdDirs");
        TestStorageMover.PathPolicyMap pathPolicyMap = new TestStorageMover.PathPolicyMap(3);
        TestStorageMover.NamespaceScheme nsScheme = pathPolicyMap.newNamespaceScheme();
        TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme();
        TestStorageMover.MigrationTest test = new TestStorageMover.MigrationTest(clusterScheme, nsScheme);
        try {
            test.runBasicTest(false);
            pathPolicyMap.moveAround(test.dfs);
            test.migrate(SUCCESS);
            test.verify(true);
        } finally {
            test.shutdownCluster();
        }
    }

    /**
     * Test DISK is running out of spaces.
     */
    @Test
    public void testNoSpaceDisk() throws Exception {
        TestStorageMover.LOG.info("testNoSpaceDisk");
        final TestStorageMover.PathPolicyMap pathPolicyMap = new TestStorageMover.PathPolicyMap(0);
        final TestStorageMover.NamespaceScheme nsScheme = pathPolicyMap.newNamespaceScheme();
        Configuration conf = new Configuration(TestStorageMover.DEFAULT_CONF);
        final TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme(conf, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        final TestStorageMover.MigrationTest test = new TestStorageMover.MigrationTest(clusterScheme, nsScheme);
        try {
            test.runBasicTest(false);
            // create 2 hot files with replication 3
            final short replication = 3;
            for (int i = 0; i < 2; i++) {
                final Path p = new Path(pathPolicyMap.hot, ("file" + i));
                DFSTestUtil.createFile(test.dfs, p, TestStorageMover.BLOCK_SIZE, replication, 0L);
                waitForAllReplicas(replication, p, test.dfs, 10);
            }
            // set all the DISK volume to full
            for (DataNode dn : test.cluster.getDataNodes()) {
                setVolumeFull(dn, DISK);
                DataNodeTestUtils.triggerHeartbeat(dn);
            }
            // test increasing replication.  Since DISK is full,
            // new replicas should be stored in ARCHIVE as a fallback storage.
            final Path file0 = new Path(pathPolicyMap.hot, "file0");
            final TestStorageMover.Replication r = test.getReplication(file0);
            final short newReplication = ((short) (5));
            test.dfs.setReplication(file0, newReplication);
            waitForAllReplicas(newReplication, file0, test.dfs, 10);
            test.verifyReplication(file0, r.disk, (newReplication - (r.disk)));
            // test creating a cold file and then increase replication
            final Path p = new Path(pathPolicyMap.cold, "foo");
            DFSTestUtil.createFile(test.dfs, p, TestStorageMover.BLOCK_SIZE, replication, 0L);
            waitForAllReplicas(replication, p, test.dfs, 10);
            test.verifyReplication(p, 0, replication);
            test.dfs.setReplication(p, newReplication);
            waitForAllReplicas(newReplication, p, test.dfs, 10);
            test.verifyReplication(p, 0, newReplication);
            // test move a hot file to warm
            final Path file1 = new Path(pathPolicyMap.hot, "file1");
            test.dfs.rename(file1, pathPolicyMap.warm);
            test.migrate(NO_MOVE_BLOCK);
            test.verifyFile(new Path(pathPolicyMap.warm, "file1"), TestStorageMover.WARM.getId());
        } finally {
            test.shutdownCluster();
        }
    }

    /**
     * Test ARCHIVE is running out of spaces.
     */
    @Test
    public void testNoSpaceArchive() throws Exception {
        TestStorageMover.LOG.info("testNoSpaceArchive");
        final TestStorageMover.PathPolicyMap pathPolicyMap = new TestStorageMover.PathPolicyMap(0);
        final TestStorageMover.NamespaceScheme nsScheme = pathPolicyMap.newNamespaceScheme();
        final TestStorageMover.ClusterScheme clusterScheme = new TestStorageMover.ClusterScheme(TestStorageMover.DEFAULT_CONF, TestStorageMover.NUM_DATANODES, TestStorageMover.REPL, TestStorageMover.genStorageTypes(TestStorageMover.NUM_DATANODES), null);
        final TestStorageMover.MigrationTest test = new TestStorageMover.MigrationTest(clusterScheme, nsScheme);
        try {
            test.runBasicTest(false);
            // create 2 hot files with replication 3
            final short replication = 3;
            for (int i = 0; i < 2; i++) {
                final Path p = new Path(pathPolicyMap.cold, ("file" + i));
                DFSTestUtil.createFile(test.dfs, p, TestStorageMover.BLOCK_SIZE, replication, 0L);
                waitForAllReplicas(replication, p, test.dfs, 10);
            }
            // set all the ARCHIVE volume to full
            for (DataNode dn : test.cluster.getDataNodes()) {
                setVolumeFull(dn, ARCHIVE);
                DataNodeTestUtils.triggerHeartbeat(dn);
            }
            {
                // test increasing replication but new replicas cannot be created
                // since no more ARCHIVE space.
                final Path file0 = new Path(pathPolicyMap.cold, "file0");
                final TestStorageMover.Replication r = test.getReplication(file0);
                Assert.assertEquals(0, r.disk);
                final short newReplication = ((short) (5));
                test.dfs.setReplication(file0, newReplication);
                waitForAllReplicas(r.archive, file0, test.dfs, 10);
                test.verifyReplication(file0, 0, r.archive);
            }
            {
                // test creating a hot file
                final Path p = new Path(pathPolicyMap.hot, "foo");
                DFSTestUtil.createFile(test.dfs, p, TestStorageMover.BLOCK_SIZE, ((short) (3)), 0L);
            }
            {
                // test move a cold file to warm
                final Path file1 = new Path(pathPolicyMap.cold, "file1");
                test.dfs.rename(file1, pathPolicyMap.warm);
                test.migrate(SUCCESS);
                test.verify(true);
            }
        } finally {
            test.shutdownCluster();
        }
    }
}

