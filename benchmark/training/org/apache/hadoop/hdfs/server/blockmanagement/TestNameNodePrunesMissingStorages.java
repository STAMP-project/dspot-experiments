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
package org.apache.hadoop.hdfs.server.blockmanagement;


import BlockManager.LOG;
import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY;
import DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.StorageLocation;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi.FsVolumeReferences;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNameNodePrunesMissingStorages {
    static final Logger LOG = LoggerFactory.getLogger(TestNameNodePrunesMissingStorages.class);

    /**
     * Test that the NameNode prunes empty storage volumes that are no longer
     * reported by the DataNode.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testUnusedStorageIsPruned() throws IOException {
        // Run the test with 1 storage, after the text expect 0 storages.
        TestNameNodePrunesMissingStorages.runTest(GenericTestUtils.getMethodName(), false, 1, 0);
    }

    /**
     * Verify that the NameNode does not prune storages with blocks
     * simply as a result of a heartbeat being sent missing that storage.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000)
    public void testStorageWithBlocksIsNotPruned() throws IOException {
        // Run the test with 1 storage, after the text still expect 1 storage.
        TestNameNodePrunesMissingStorages.runTest(GenericTestUtils.getMethodName(), true, 1, 1);
    }

    /**
     * Regression test for HDFS-7960.<p/>
     *
     * Shutting down a datanode, removing a storage directory, and restarting
     * the DataNode should not produce zombie storages.
     */
    @Test(timeout = 300000)
    public void testRemovingStorageDoesNotProduceZombies() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY, 1);
        conf.setInt(DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 1000);
        final int NUM_STORAGES_PER_DN = 2;
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storagesPerDatanode(NUM_STORAGES_PER_DN).build();
        try {
            cluster.waitActive();
            for (DataNode dn : cluster.getDataNodes()) {
                Assert.assertEquals(NUM_STORAGES_PER_DN, cluster.getNamesystem().getBlockManager().getDatanodeManager().getDatanode(dn.getDatanodeId()).getStorageInfos().length);
            }
            // Create a file which will end up on all 3 datanodes.
            final Path TEST_PATH = new Path("/foo1");
            DistributedFileSystem fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, 1024, ((short) (3)), -889271554);
            for (DataNode dn : cluster.getDataNodes()) {
                DataNodeTestUtils.triggerBlockReport(dn);
            }
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, new Path("/foo1"));
            cluster.getNamesystem().writeLock();
            final String storageIdToRemove;
            String datanodeUuid;
            // Find the first storage which this block is in.
            try {
                BlockInfo storedBlock = cluster.getNamesystem().getBlockManager().getStoredBlock(block.getLocalBlock());
                Iterator<DatanodeStorageInfo> storageInfoIter = cluster.getNamesystem().getBlockManager().blocksMap.getStorages(storedBlock).iterator();
                Assert.assertTrue(storageInfoIter.hasNext());
                DatanodeStorageInfo info = storageInfoIter.next();
                storageIdToRemove = info.getStorageID();
                datanodeUuid = info.getDatanodeDescriptor().getDatanodeUuid();
            } finally {
                cluster.getNamesystem().writeUnlock();
            }
            // Find the DataNode which holds that first storage.
            final DataNode datanodeToRemoveStorageFrom;
            int datanodeToRemoveStorageFromIdx = 0;
            while (true) {
                if (datanodeToRemoveStorageFromIdx >= (cluster.getDataNodes().size())) {
                    Assert.fail(("failed to find datanode with uuid " + datanodeUuid));
                    datanodeToRemoveStorageFrom = null;
                    break;
                }
                DataNode dn = cluster.getDataNodes().get(datanodeToRemoveStorageFromIdx);
                if (dn.getDatanodeUuid().equals(datanodeUuid)) {
                    datanodeToRemoveStorageFrom = dn;
                    break;
                }
                datanodeToRemoveStorageFromIdx++;
            } 
            // Find the volume within the datanode which holds that first storage.
            StorageLocation volumeLocationToRemove = null;
            try (FsVolumeReferences volumes = datanodeToRemoveStorageFrom.getFSDataset().getFsVolumeReferences()) {
                Assert.assertEquals(NUM_STORAGES_PER_DN, volumes.size());
                for (FsVolumeSpi volume : volumes) {
                    if (volume.getStorageID().equals(storageIdToRemove)) {
                        volumeLocationToRemove = volume.getStorageLocation();
                    }
                }
            }
            // Shut down the datanode and remove the volume.
            // Replace the volume directory with a regular file, which will
            // cause a volume failure.  (If we merely removed the directory,
            // it would be re-initialized with a new storage ID.)
            Assert.assertNotNull(volumeLocationToRemove);
            datanodeToRemoveStorageFrom.shutdown();
            FileUtil.fullyDelete(new File(volumeLocationToRemove.getUri()));
            FileOutputStream fos = new FileOutputStream(new File(volumeLocationToRemove.getUri()));
            try {
                fos.write(1);
            } finally {
                fos.close();
            }
            cluster.restartDataNode(datanodeToRemoveStorageFromIdx);
            // Wait for the NameNode to remove the storage.
            TestNameNodePrunesMissingStorages.LOG.info(("waiting for the datanode to remove " + storageIdToRemove));
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    final DatanodeDescriptor dnDescriptor = cluster.getNamesystem().getBlockManager().getDatanodeManager().getDatanode(datanodeToRemoveStorageFrom.getDatanodeUuid());
                    Assert.assertNotNull(dnDescriptor);
                    DatanodeStorageInfo[] infos = dnDescriptor.getStorageInfos();
                    for (DatanodeStorageInfo info : infos) {
                        if (info.getStorageID().equals(storageIdToRemove)) {
                            TestNameNodePrunesMissingStorages.LOG.info((((("Still found storage " + storageIdToRemove) + " on ") + info) + "."));
                            return false;
                        }
                    }
                    Assert.assertEquals((NUM_STORAGES_PER_DN - 1), infos.length);
                    return true;
                }
            }, 1000, 30000);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 300000)
    public void testRenamingStorageIds() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY, 0);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storagesPerDatanode(1).build();
        GenericTestUtils.setLogLevel(BlockManager.LOG, Level.ALL);
        try {
            cluster.waitActive();
            final Path TEST_PATH = new Path("/foo1");
            DistributedFileSystem fs = cluster.getFileSystem();
            // Create a file and leave it open
            DFSTestUtil.createFile(fs, TEST_PATH, 1, ((short) (1)), -559038737);
            // Find the volume within the datanode which holds that first storage.
            DataNode dn = cluster.getDataNodes().get(0);
            FsVolumeReferences volumeRefs = dn.getFSDataset().getFsVolumeReferences();
            final String newStorageId = DatanodeStorage.generateUuid();
            try {
                File currentDir = new File(new File(volumeRefs.get(0).getStorageLocation().getUri()), "current");
                File versionFile = new File(currentDir, "VERSION");
                TestNameNodePrunesMissingStorages.rewriteVersionFile(versionFile, newStorageId);
            } finally {
                volumeRefs.close();
            }
            final ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, TEST_PATH);
            cluster.restartDataNodes();
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    cluster.getNamesystem().writeLock();
                    try {
                        Iterator<DatanodeStorageInfo> storageInfoIter = cluster.getNamesystem().getBlockManager().getStorages(block.getLocalBlock()).iterator();
                        if (!(storageInfoIter.hasNext())) {
                            TestNameNodePrunesMissingStorages.LOG.info(((("Expected to find a storage for " + (block.getBlockName())) + ", but nothing was found.  ") + "Continuing to wait."));
                            return false;
                        }
                        DatanodeStorageInfo info = storageInfoIter.next();
                        if (!(newStorageId.equals(info.getStorageID()))) {
                            TestNameNodePrunesMissingStorages.LOG.info(((((((((("Expected " + (block.getBlockName())) + " to ") + "be in storage id ") + newStorageId) + ", but it ") + "was in ") + (info.getStorageID())) + ".  Continuing ") + "to wait."));
                            return false;
                        }
                        TestNameNodePrunesMissingStorages.LOG.info((((("Successfully found " + (block.getBlockName())) + " in ") + "be in storage id ") + newStorageId));
                    } finally {
                        cluster.getNamesystem().writeUnlock();
                    }
                    return true;
                }
            }, 20, 100000);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testNameNodePrunesUnreportedStorages() throws Exception {
        Configuration conf = new HdfsConfiguration();
        // Create a cluster with one datanode with two storages
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storagesPerDatanode(2).build();
        try {
            cluster.waitActive();
            // Create two files to ensure each storage has a block
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path("file1"), 102400, 102400, 102400, ((short) (1)), 29021678);
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path("file2"), 102400, 102400, 102400, ((short) (1)), 464346861);
            // Get the datanode storages and data directories
            DataNode dn = cluster.getDataNodes().get(0);
            BlockManager bm = cluster.getNameNode().getNamesystem().getBlockManager();
            DatanodeDescriptor dnDescriptor = bm.getDatanodeManager().getDatanode(cluster.getDataNodes().get(0).getDatanodeUuid());
            DatanodeStorageInfo[] dnStoragesInfosBeforeRestart = dnDescriptor.getStorageInfos();
            Collection<String> oldDirs = new ArrayList<String>(dn.getConf().getTrimmedStringCollection(DFS_DATANODE_DATA_DIR_KEY));
            // Keep the first data directory and remove the second.
            String newDirs = oldDirs.iterator().next();
            conf.set(DFS_DATANODE_DATA_DIR_KEY, newDirs);
            // Restart the datanode with the new conf
            cluster.stopDataNode(0);
            cluster.startDataNodes(conf, 1, false, null, null);
            dn = cluster.getDataNodes().get(0);
            cluster.waitActive();
            // Assert that the dnDescriptor has both the storages after restart
            Assert.assertArrayEquals(dnStoragesInfosBeforeRestart, dnDescriptor.getStorageInfos());
            // Assert that the removed storage is marked as FAILED
            // when DN heartbeats to the NN
            int numFailedStoragesWithBlocks = 0;
            DatanodeStorageInfo failedStorageInfo = null;
            for (DatanodeStorageInfo dnStorageInfo : dnDescriptor.getStorageInfos()) {
                if (dnStorageInfo.areBlocksOnFailedStorage()) {
                    numFailedStoragesWithBlocks++;
                    failedStorageInfo = dnStorageInfo;
                }
            }
            Assert.assertEquals(1, numFailedStoragesWithBlocks);
            // Heartbeat manager removes the blocks associated with this failed
            // storage
            bm.getDatanodeManager().getHeartbeatManager().heartbeatCheck();
            Assert.assertTrue((!(failedStorageInfo.areBlocksOnFailedStorage())));
            // pruneStorageMap removes the unreported storage
            cluster.triggerHeartbeats();
            // Assert that the unreported storage is pruned
            Assert.assertEquals(DataNode.getStorageLocations(dn.getConf()).size(), dnDescriptor.getStorageInfos().length);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

