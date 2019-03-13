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


import DFSConfigKeys.DFS_BALANCER_MAX_ITERATION_TIME_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import DFSConfigKeys.DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY;
import DFSConfigKeys.DFS_MOVER_MOVERTHREADS_KEY;
import DFSConfigKeys.DFS_MOVER_RETRY_MAX_ATTEMPTS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY;
import ExitStatus.IO_EXCEPTION;
import ExitStatus.NO_MOVE_BLOCK;
import ExitStatus.NO_MOVE_PROGRESS;
import ExitStatus.SUCCESS;
import HdfsConstants.HOT_STORAGE_POLICY_NAME;
import HdfsServerConstants.MOVER_ID_PATH;
import Mover.Cli;
import StoragePolicySatisfierMode.EXTERNAL;
import StoragePolicySatisfierMode.NONE;
import StorageType.ARCHIVE;
import StorageType.DEFAULT;
import StorageType.DISK;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.balancer.Dispatcher.DBlock;
import org.apache.hadoop.hdfs.server.balancer.TestBalancer;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.hdfs.server.mover.Mover.MLocation;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMover {
    private static final Logger LOG = LoggerFactory.getLogger(TestMover.class);

    private static final int DEFAULT_BLOCK_SIZE = 100;

    private File keytabFile;

    private String principal;

    static {
        TestBalancer.initTestSetup();
    }

    @Test
    public void testScheduleSameBlock() throws IOException {
        final Configuration conf = new HdfsConfiguration();
        conf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testScheduleSameBlock/file";
            {
                final FSDataOutputStream out = dfs.create(new Path(file));
                out.writeChars("testScheduleSameBlock");
                out.close();
            }
            final Mover mover = TestMover.newMover(conf);
            mover.init();
            final Mover.Processor processor = mover.new Processor();
            final LocatedBlock lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            final List<MLocation> locations = MLocation.toLocations(lb);
            final MLocation ml = locations.get(0);
            final DBlock db = mover.newDBlock(lb, locations, null);
            final List<StorageType> storageTypes = new ArrayList<StorageType>(Arrays.asList(DEFAULT, DEFAULT));
            Assert.assertTrue(processor.scheduleMoveReplica(db, ml, storageTypes));
            Assert.assertFalse(processor.scheduleMoveReplica(db, ml, storageTypes));
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testWithFederateClusterWithinSameNode() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).nnTopology(MiniDFSNNTopology.simpleFederatedTopology(2)).build();
        DFSTestUtil.setFederatedConfiguration(cluster, conf);
        try {
            cluster.waitActive();
            final String file = "/test/file";
            Path dir = new Path("/test");
            final DistributedFileSystem dfs1 = cluster.getFileSystem(0);
            final DistributedFileSystem dfs2 = cluster.getFileSystem(1);
            URI nn1 = dfs1.getUri();
            URI nn2 = dfs2.getUri();
            setupStoragePoliciesAndPaths(dfs1, dfs2, dir, file);
            // move to ARCHIVE
            dfs1.setStoragePolicy(dir, "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", nn1 + (dir.toString()) });
            Assert.assertEquals("Movement to ARCHIVE should be successful", 0, rc);
            // move to DISK
            dfs2.setStoragePolicy(dir, "HOT");
            rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", nn2 + (dir.toString()) });
            Assert.assertEquals("Movement to DISK should be successful", 0, rc);
            // Wait till namenode notified about the block location details
            waitForLocatedBlockWithArchiveStorageType(dfs1, file, 3);
            waitForLocatedBlockWithDiskStorageType(dfs2, file, 3);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testWithFederatedCluster() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).nnTopology(MiniDFSNNTopology.simpleFederatedTopology(2)).numDataNodes(4).build();
        DFSTestUtil.setFederatedConfiguration(cluster, conf);
        try {
            cluster.waitActive();
            final String file = "/test/file";
            Path dir = new Path("/test");
            final DistributedFileSystem dfs1 = cluster.getFileSystem(0);
            final DistributedFileSystem dfs2 = cluster.getFileSystem(1);
            URI nn1 = dfs1.getUri();
            URI nn2 = dfs2.getUri();
            setupStoragePoliciesAndPaths(dfs1, dfs2, dir, file);
            // Changing storage policies
            dfs1.setStoragePolicy(dir, "COLD");
            dfs2.setStoragePolicy(dir, "HOT");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", nn1 + (dir.toString()), nn2 + (dir.toString()) });
            Assert.assertEquals("Movement to DISK should be successful", 0, rc);
            waitForLocatedBlockWithArchiveStorageType(dfs1, file, 3);
            waitForLocatedBlockWithDiskStorageType(dfs2, file, 3);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testWithFederatedHACluster() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).nnTopology(MiniDFSNNTopology.simpleHAFederatedTopology(2)).numDataNodes(4).build();
        DFSTestUtil.setFederatedHAConfiguration(cluster, conf);
        try {
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Iterator<URI> iter = namenodes.iterator();
            URI nn1 = iter.next();
            URI nn2 = iter.next();
            cluster.transitionToActive(0);
            cluster.transitionToActive(2);
            final String file = "/test/file";
            Path dir = new Path("/test");
            final DistributedFileSystem dfs1 = ((DistributedFileSystem) (FileSystem.get(nn1, conf)));
            final DistributedFileSystem dfs2 = ((DistributedFileSystem) (FileSystem.get(nn2, conf)));
            setupStoragePoliciesAndPaths(dfs1, dfs2, dir, file);
            // Changing Storage Policies
            dfs1.setStoragePolicy(dir, "COLD");
            dfs2.setStoragePolicy(dir, "HOT");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", nn1 + (dir.toString()), nn2 + (dir.toString()) });
            Assert.assertEquals("Movement to DISK should be successful", 0, rc);
            waitForLocatedBlockWithArchiveStorageType(dfs1, file, 3);
            waitForLocatedBlockWithDiskStorageType(dfs2, file, 3);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testScheduleBlockWithinSameNode() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        testWithinSameNode(conf);
    }

    /**
     * Test Mover Cli by specifying a list of files/directories using option "-p".
     * There is only one namenode (and hence name service) specified in the conf.
     */
    @Test
    public void testMoverCli() throws Exception {
        final Configuration clusterConf = new HdfsConfiguration();
        clusterConf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(clusterConf).numDataNodes(0).build();
        try {
            final Configuration conf = cluster.getConfiguration(0);
            try {
                Cli.getNameNodePathsToMove(conf, "-p", "/foo", "bar");
                Assert.fail("Expected exception for illegal path bar");
            } catch (IllegalArgumentException e) {
                GenericTestUtils.assertExceptionContains("bar is not absolute", e);
            }
            Map<URI, List<Path>> movePaths = Cli.getNameNodePathsToMove(conf);
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Assert.assertEquals(1, namenodes.size());
            Assert.assertEquals(1, movePaths.size());
            URI nn = namenodes.iterator().next();
            Assert.assertTrue(movePaths.containsKey(nn));
            Assert.assertNull(movePaths.get(nn));
            movePaths = Cli.getNameNodePathsToMove(conf, "-p", "/foo", "/bar");
            namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Assert.assertEquals(1, movePaths.size());
            nn = namenodes.iterator().next();
            Assert.assertTrue(movePaths.containsKey(nn));
            checkMovePaths(movePaths.get(nn), new Path("/foo"), new Path("/bar"));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testMoverCliWithHAConf() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        conf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        HATestUtil.setFailoverConfigurations(cluster, conf, "MyCluster");
        try {
            Map<URI, List<Path>> movePaths = Cli.getNameNodePathsToMove(conf, "-p", "/foo", "/bar");
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Assert.assertEquals(1, namenodes.size());
            Assert.assertEquals(1, movePaths.size());
            URI nn = namenodes.iterator().next();
            Assert.assertEquals(new URI("hdfs://MyCluster"), nn);
            Assert.assertTrue(movePaths.containsKey(nn));
            checkMovePaths(movePaths.get(nn), new Path("/foo"), new Path("/bar"));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testMoverCliWithFederation() throws Exception {
        final Configuration clusterConf = new HdfsConfiguration();
        clusterConf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(clusterConf).nnTopology(MiniDFSNNTopology.simpleFederatedTopology(3)).numDataNodes(0).build();
        final Configuration conf = new HdfsConfiguration();
        clusterConf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        DFSTestUtil.setFederatedConfiguration(cluster, conf);
        try {
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Assert.assertEquals(3, namenodes.size());
            try {
                Cli.getNameNodePathsToMove(conf, "-p", "/foo");
                Assert.fail("Expect exception for missing authority information");
            } catch (IllegalArgumentException e) {
                GenericTestUtils.assertExceptionContains("does not contain scheme and authority", e);
            }
            try {
                Cli.getNameNodePathsToMove(conf, "-p", "hdfs:///foo");
                Assert.fail("Expect exception for missing authority information");
            } catch (IllegalArgumentException e) {
                GenericTestUtils.assertExceptionContains("does not contain scheme and authority", e);
            }
            try {
                Cli.getNameNodePathsToMove(conf, "-p", "wrong-hdfs://ns1/foo");
                Assert.fail("Expect exception for wrong scheme");
            } catch (IllegalArgumentException e) {
                GenericTestUtils.assertExceptionContains("Cannot resolve the path", e);
            }
            Iterator<URI> iter = namenodes.iterator();
            URI nn1 = iter.next();
            URI nn2 = iter.next();
            Map<URI, List<Path>> movePaths = Cli.getNameNodePathsToMove(conf, "-p", (nn1 + "/foo"), (nn1 + "/bar"), (nn2 + "/foo/bar"));
            Assert.assertEquals(2, movePaths.size());
            checkMovePaths(movePaths.get(nn1), new Path("/foo"), new Path("/bar"));
            checkMovePaths(movePaths.get(nn2), new Path("/foo/bar"));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testMoverCliWithFederationHA() throws Exception {
        final Configuration clusterConf = new HdfsConfiguration();
        clusterConf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(clusterConf).nnTopology(MiniDFSNNTopology.simpleHAFederatedTopology(3)).numDataNodes(0).build();
        final Configuration conf = new HdfsConfiguration();
        clusterConf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        DFSTestUtil.setFederatedHAConfiguration(cluster, conf);
        try {
            Collection<URI> namenodes = DFSUtil.getInternalNsRpcUris(conf);
            Assert.assertEquals(3, namenodes.size());
            Iterator<URI> iter = namenodes.iterator();
            URI nn1 = iter.next();
            URI nn2 = iter.next();
            URI nn3 = iter.next();
            Map<URI, List<Path>> movePaths = Cli.getNameNodePathsToMove(conf, "-p", (nn1 + "/foo"), (nn1 + "/bar"), (nn2 + "/foo/bar"), (nn3 + "/foobar"));
            Assert.assertEquals(3, movePaths.size());
            checkMovePaths(movePaths.get(nn1), new Path("/foo"), new Path("/bar"));
            checkMovePaths(movePaths.get(nn2), new Path("/foo/bar"));
            checkMovePaths(movePaths.get(nn3), new Path("/foobar"));
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testTwoReplicaSameStorageTypeShouldNotSelect() throws Exception {
        // HDFS-8147
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testForTwoReplicaSameStorageTypeShouldNotSelect";
            // write to DISK
            final FSDataOutputStream out = dfs.create(new Path(file), ((short) (2)));
            out.writeChars("testForTwoReplicaSameStorageTypeShouldNotSelect");
            out.close();
            // verify before movement
            LocatedBlock lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            StorageType[] storageTypes = lb.getStorageTypes();
            for (StorageType storageType : storageTypes) {
                Assert.assertTrue(((StorageType.DISK) == storageType));
            }
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            Assert.assertEquals("Movement to ARCHIVE should be successful", 0, rc);
            // Wait till namenode notified about the block location details
            waitForLocatedBlockWithArchiveStorageType(dfs, file, 2);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testMoveWhenStoragePolicyNotSatisfying() throws Exception {
        // HDFS-8147
        final Configuration conf = new HdfsConfiguration();
        conf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testMoveWhenStoragePolicyNotSatisfying";
            // write to DISK
            final FSDataOutputStream out = dfs.create(new Path(file));
            out.writeChars("testMoveWhenStoragePolicyNotSatisfying");
            out.close();
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            int exitcode = NO_MOVE_BLOCK.getExitCode();
            Assert.assertEquals(("Exit code should be " + exitcode), exitcode, rc);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testMoveWhenStoragePolicySatisfierIsRunning() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        conf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, EXTERNAL.toString());
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK } }).build();
        try {
            cluster.waitActive();
            // Simulate External sps by creating #getNameNodeConnector instance.
            DFSTestUtil.getNameNodeConnector(conf, MOVER_ID_PATH, 1, true);
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testMoveWhenStoragePolicySatisfierIsRunning";
            // write to DISK
            final FSDataOutputStream out = dfs.create(new Path(file));
            out.writeChars("testMoveWhenStoragePolicySatisfierIsRunning");
            out.close();
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            int exitcode = IO_EXCEPTION.getExitCode();
            Assert.assertEquals(("Exit code should be " + exitcode), exitcode, rc);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testMoverFailedRetry() throws Exception {
        // HDFS-8147
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        conf.set(DFS_MOVER_RETRY_MAX_ATTEMPTS_KEY, "2");
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testMoverFailedRetry";
            // write to DISK
            final FSDataOutputStream out = dfs.create(new Path(file), ((short) (2)));
            out.writeChars("testMoverFailedRetry");
            out.close();
            // Delete block file so, block move will fail with FileNotFoundException
            LocatedBlock lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            cluster.corruptBlockOnDataNodesByDeletingBlockFile(lb.getBlock());
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            Assert.assertEquals("Movement should fail after some retry", NO_MOVE_PROGRESS.getExitCode(), rc);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 100000)
    public void testBalancerMaxIterationTimeNotAffectMover() throws Exception {
        long blockSize = (10 * 1024) * 1024;
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        conf.setInt(DFS_MOVER_MOVERTHREADS_KEY, 1);
        conf.setInt(DFS_DATANODE_BALANCE_MAX_NUM_CONCURRENT_MOVES_KEY, 1);
        // set a fairly large block size to run into the limitation
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        conf.setLong(DFS_BYTES_PER_CHECKSUM_KEY, blockSize);
        // set a somewhat grater than zero max iteration time to have the move time
        // to surely exceed it
        conf.setLong(DFS_BALANCER_MAX_ITERATION_TIME_KEY, 200L);
        conf.setInt(DFS_MOVER_RETRY_MAX_ATTEMPTS_KEY, 1);
        // set client socket timeout to have an IN_PROGRESS notification back from
        // the DataNode about the copy in every second.
        conf.setLong(DFS_CLIENT_SOCKET_TIMEOUT_KEY, 1000L);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem fs = cluster.getFileSystem();
            final String file = "/testMaxIterationTime.dat";
            final Path path = new Path(file);
            short rep_factor = 1;
            int seed = 16448250;
            // write to DISK
            DFSTestUtil.createFile(fs, path, (4L * blockSize), rep_factor, seed);
            // move to ARCHIVE
            fs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file });
            Assert.assertEquals("Retcode expected to be ExitStatus.SUCCESS (0).", SUCCESS.getExitCode(), rc);
        } finally {
            cluster.shutdown();
        }
    }

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripesPerBlock = 4;

    private final int defaultBlockSize = (cellSize) * (stripesPerBlock);

    @Test(timeout = 300000)
    public void testMoverWithStripedFile() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        initConfWithStripe(conf);
        // start 10 datanodes
        int numOfDatanodes = 10;
        int storagesPerDatanode = 2;
        long capacity = 10 * (defaultBlockSize);
        long[][] capacities = new long[numOfDatanodes][storagesPerDatanode];
        for (int i = 0; i < numOfDatanodes; i++) {
            for (int j = 0; j < storagesPerDatanode; j++) {
                capacities[i][j] = capacity;
            }
        }
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numOfDatanodes).storagesPerDatanode(storagesPerDatanode).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } }).storageCapacities(capacities).build();
        try {
            cluster.waitActive();
            cluster.getFileSystem().enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
            // set "/bar" directory with HOT storage policy.
            ClientProtocol client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            String barDir = "/bar";
            client.mkdirs(barDir, new FsPermission(((short) (777))), true);
            client.setStoragePolicy(barDir, HOT_STORAGE_POLICY_NAME);
            // set an EC policy on "/bar" directory
            client.setErasureCodingPolicy(barDir, StripedFileTestUtil.getDefaultECPolicy().getName());
            // write file to barDir
            final String fooFile = "/bar/foo";
            long fileLen = 20 * (defaultBlockSize);
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path(fooFile), fileLen, ((short) (3)), 0);
            // verify storage types and locations
            LocatedBlocks locatedBlocks = client.getBlockLocations(fooFile, 0, fileLen);
            for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                for (StorageType type : lb.getStorageTypes()) {
                    Assert.assertEquals(DISK, type);
                }
            }
            StripedFileTestUtil.verifyLocatedStripedBlocks(locatedBlocks, ((dataBlocks) + (parityBlocks)));
            // start 5 more datanodes
            numOfDatanodes += 5;
            capacities = new long[5][storagesPerDatanode];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < storagesPerDatanode; j++) {
                    capacities[i][j] = capacity;
                }
            }
            cluster.startDataNodes(conf, 5, new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } }, true, null, null, null, capacities, null, false, false, false, null);
            cluster.triggerHeartbeats();
            // move file to ARCHIVE
            client.setStoragePolicy(barDir, "COLD");
            // run Mover
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", barDir });
            Assert.assertEquals("Movement to ARCHIVE should be successful", 0, rc);
            // verify storage types and locations
            locatedBlocks = client.getBlockLocations(fooFile, 0, fileLen);
            for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                for (StorageType type : lb.getStorageTypes()) {
                    Assert.assertEquals(ARCHIVE, type);
                }
            }
            StripedFileTestUtil.verifyLocatedStripedBlocks(locatedBlocks, ((dataBlocks) + (parityBlocks)));
            // start 5 more datanodes
            numOfDatanodes += 5;
            capacities = new long[5][storagesPerDatanode];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < storagesPerDatanode; j++) {
                    capacities[i][j] = capacity;
                }
            }
            cluster.startDataNodes(conf, 5, new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }, true, null, null, null, capacities, null, false, false, false, null);
            cluster.triggerHeartbeats();
            // move file blocks to ONE_SSD policy
            client.setStoragePolicy(barDir, "ONE_SSD");
            // run Mover
            rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", barDir });
            // verify storage types and locations
            // Movements should have been ignored for the unsupported policy on
            // striped file
            locatedBlocks = client.getBlockLocations(fooFile, 0, fileLen);
            for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                for (StorageType type : lb.getStorageTypes()) {
                    Assert.assertEquals(ARCHIVE, type);
                }
            }
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test Mover runs fine when logging in with a keytab in kerberized env.
     * Reusing testWithinSameNode here for basic functionality testing.
     */
    @Test(timeout = 300000)
    public void testMoverWithKeytabs() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        try {
            initSecureConf(conf);
            final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabFile.getAbsolutePath());
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // verify that mover runs Ok.
                    testWithinSameNode(conf);
                    // verify that UGI was logged in using keytab.
                    Assert.assertTrue(UserGroupInformation.isLoginKeytabBased());
                    return null;
                }
            });
        } finally {
            // Reset UGI so that other tests are not affected.
            UserGroupInformation.reset();
            UserGroupInformation.setConfiguration(new Configuration());
        }
    }

    /**
     * Test to verify that mover can't move pinned blocks.
     */
    @Test(timeout = 90000)
    public void testMoverWithPinnedBlocks() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        // Sets bigger retry max attempts value so that test case will timed out if
        // block pinning errors are not handled properly during block movement.
        conf.setInt(DFS_MOVER_RETRY_MAX_ATTEMPTS_KEY, 10000);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testMoverWithPinnedBlocks/file";
            Path dir = new Path("/testMoverWithPinnedBlocks");
            dfs.mkdirs(dir);
            // write to DISK
            dfs.setStoragePolicy(dir, "HOT");
            final FSDataOutputStream out = dfs.create(new Path(file));
            byte[] fileData = StripedFileTestUtil.generateBytes(((TestMover.DEFAULT_BLOCK_SIZE) * 3));
            out.write(fileData);
            out.close();
            // verify before movement
            LocatedBlock lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            StorageType[] storageTypes = lb.getStorageTypes();
            for (StorageType storageType : storageTypes) {
                Assert.assertTrue(((StorageType.DISK) == storageType));
            }
            // Adding one SSD based data node to the cluster.
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD } };
            startAdditionalDNs(conf, 1, newtypes, cluster);
            // Mock FsDatasetSpi#getPinning to show that the block is pinned.
            for (int i = 0; i < (cluster.getDataNodes().size()); i++) {
                DataNode dn = cluster.getDataNodes().get(i);
                TestMover.LOG.info("Simulate block pinning in datanode {}", dn);
                InternalDataNodeTestUtils.mockDatanodeBlkPinning(dn, true);
            }
            // move file blocks to ONE_SSD policy
            dfs.setStoragePolicy(dir, "ONE_SSD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", dir.toString() });
            int exitcode = NO_MOVE_BLOCK.getExitCode();
            Assert.assertEquals("Movement should fail", exitcode, rc);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test to verify that mover should work well with pinned blocks as well as
     * failed blocks. Mover should continue retrying the failed blocks only.
     */
    @Test(timeout = 90000)
    public void testMoverFailedRetryWithPinnedBlocks() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        conf.set(DFS_MOVER_RETRY_MAX_ATTEMPTS_KEY, "2");
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String parenDir = "/parent";
            dfs.mkdirs(new Path(parenDir));
            final String file1 = "/parent/testMoverFailedRetryWithPinnedBlocks1";
            // write to DISK
            final FSDataOutputStream out = dfs.create(new Path(file1), ((short) (2)));
            byte[] fileData = StripedFileTestUtil.generateBytes(((TestMover.DEFAULT_BLOCK_SIZE) * 2));
            out.write(fileData);
            out.close();
            // Adding pinned blocks.
            createFileWithFavoredDatanodes(conf, cluster, dfs);
            // Delete block file so, block move will fail with FileNotFoundException
            LocatedBlocks locatedBlocks = dfs.getClient().getLocatedBlocks(file1, 0);
            Assert.assertEquals("Wrong block count", 2, locatedBlocks.locatedBlockCount());
            LocatedBlock lb = locatedBlocks.get(0);
            cluster.corruptBlockOnDataNodesByDeletingBlockFile(lb.getBlock());
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(parenDir), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", parenDir.toString() });
            Assert.assertEquals("Movement should fail after some retry", NO_MOVE_PROGRESS.getExitCode(), rc);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testMoverWhenStoragePolicyUnset() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestMover.initConf(conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageTypes(new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } }).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final String file = "/testMoverWhenStoragePolicyUnset";
            // write to DISK
            DFSTestUtil.createFile(dfs, new Path(file), 1L, ((short) (1)), 0L);
            // move to ARCHIVE
            dfs.setStoragePolicy(new Path(file), "COLD");
            int rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            Assert.assertEquals("Movement to ARCHIVE should be successful", 0, rc);
            // Wait till namenode notified about the block location details
            waitForLocatedBlockWithArchiveStorageType(dfs, file, 1);
            // verify before unset policy
            LocatedBlock lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            Assert.assertTrue(((StorageType.ARCHIVE) == (lb.getStorageTypes()[0])));
            // unset storage policy
            dfs.unsetStoragePolicy(new Path(file));
            rc = ToolRunner.run(conf, new Mover.Cli(), new String[]{ "-p", file.toString() });
            Assert.assertEquals("Movement to DISK should be successful", 0, rc);
            lb = dfs.getClient().getLocatedBlocks(file, 0).get(0);
            Assert.assertTrue(((StorageType.DISK) == (lb.getStorageTypes()[0])));
        } finally {
            cluster.shutdown();
        }
    }
}

