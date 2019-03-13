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
package org.apache.hadoop.hdfs.server.datanode;


import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import FsDatasetSpi.FsVolumeReferences;
import Storage.StorageDirectory;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsDatasetTestUtil;
import org.apache.hadoop.hdfs.server.protocol.VolumeFailureSummary;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Fine-grain testing of block files and locations after volume failure.
 */
public class TestDataNodeVolumeFailure {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataNodeVolumeFailure.class);

    private final int block_size = 512;

    MiniDFSCluster cluster = null;

    private Configuration conf;

    final int dn_num = 2;

    final int blocks_num = 30;

    final short repl = 2;

    File dataDir = null;

    File data_fail = null;

    File failedDir = null;

    private FileSystem fs;

    // mapping blocks to Meta files(physical files) and locs(NameNode locations)
    private class BlockLocs {
        public int num_files = 0;

        public int num_locs = 0;
    }

    // block id to BlockLocs
    final Map<String, TestDataNodeVolumeFailure.BlockLocs> block_map = new HashMap<String, TestDataNodeVolumeFailure.BlockLocs>();

    // specific the timeout for entire test class
    @Rule
    public Timeout timeout = new Timeout((120 * 1000));

    /* Verify the number of blocks and files are correct after volume failure,
    and that we can replicate to both datanodes even after a single volume
    failure if the configuration parameter allows this.
     */
    @Test(timeout = 120000)
    public void testVolumeFailure() throws Exception {
        System.out.println(("Data dir: is " + (dataDir.getPath())));
        // Data dir structure is dataDir/data[1-4]/[current,tmp...]
        // data1,2 is for datanode 1, data2,3 - datanode2
        String filename = "/test.txt";
        Path filePath = new Path(filename);
        // we use only small number of blocks to avoid creating subdirs in the data dir..
        int filesize = (block_size) * (blocks_num);
        DFSTestUtil.createFile(fs, filePath, filesize, repl, 1L);
        DFSTestUtil.waitReplication(fs, filePath, repl);
        System.out.println((((("file " + filename) + "(size ") + filesize) + ") is created and replicated"));
        // fail the volume
        // delete/make non-writable one of the directories (failed volume)
        data_fail = cluster.getInstanceStorageDir(1, 0);
        failedDir = MiniDFSCluster.getFinalizedDir(data_fail, cluster.getNamesystem().getBlockPoolId());
        if ((failedDir.exists()) && // !FileUtil.fullyDelete(failedDir)
        (!(deteteBlocks(failedDir)))) {
            throw new IOException((("Could not delete hdfs directory '" + (failedDir)) + "'"));
        }
        data_fail.setReadOnly();
        failedDir.setReadOnly();
        System.out.println(((("Deleteing " + (failedDir.getPath())) + "; exist=") + (failedDir.exists())));
        // access all the blocks on the "failed" DataNode,
        // we need to make sure that the "failed" volume is being accessed -
        // and that will cause failure, blocks removal, "emergency" block report
        triggerFailure(filename, filesize);
        // DN eventually have latest volume failure information for next heartbeat
        final DataNode dn = cluster.getDataNodes().get(1);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                final VolumeFailureSummary summary = dn.getFSDataset().getVolumeFailureSummary();
                return ((summary != null) && ((summary.getFailedStorageLocations()) != null)) && ((summary.getFailedStorageLocations().length) == 1);
            }
        }, 10, (30 * 1000));
        // trigger DN to send heartbeat
        DataNodeTestUtils.triggerHeartbeat(dn);
        final BlockManager bm = cluster.getNamesystem().getBlockManager();
        // trigger NN handel heartbeat
        BlockManagerTestUtil.checkHeartbeat(bm);
        // NN now should have latest volume failure
        Assert.assertEquals(1, cluster.getNamesystem().getVolumeFailuresTotal());
        // verify number of blocks and files...
        verify(filename, filesize);
        // create another file (with one volume failed).
        System.out.println("creating file test1.txt");
        Path fileName1 = new Path("/test1.txt");
        DFSTestUtil.createFile(fs, fileName1, filesize, repl, 1L);
        // should be able to replicate to both nodes (2 DN, repl=2)
        DFSTestUtil.waitReplication(fs, fileName1, repl);
        System.out.println((("file " + (fileName1.getName())) + " is created and replicated"));
    }

    /**
     * Test that DataStorage and BlockPoolSliceStorage remove the failed volume
     * after failure.
     */
    @Test(timeout = 150000)
    public void testFailedVolumeBeingRemovedFromDataNode() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        Path file1 = new Path("/test1");
        DFSTestUtil.createFile(fs, file1, 1024, ((short) (2)), 1L);
        DFSTestUtil.waitReplication(fs, file1, ((short) (2)));
        File dn0Vol1 = cluster.getInstanceStorageDir(0, 0);
        DataNodeTestUtils.injectDataDirFailure(dn0Vol1);
        DataNode dn0 = cluster.getDataNodes().get(0);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol1));
        // Verify dn0Vol1 has been completely removed from DN0.
        // 1. dn0Vol1 is removed from DataStorage.
        DataStorage storage = dn0.getStorage();
        Assert.assertEquals(1, storage.getNumStorageDirs());
        for (int i = 0; i < (storage.getNumStorageDirs()); i++) {
            Storage.StorageDirectory sd = storage.getStorageDir(i);
            Assert.assertFalse(sd.getRoot().getAbsolutePath().startsWith(dn0Vol1.getAbsolutePath()));
        }
        final String bpid = cluster.getNamesystem().getBlockPoolId();
        BlockPoolSliceStorage bpsStorage = storage.getBPStorage(bpid);
        Assert.assertEquals(1, bpsStorage.getNumStorageDirs());
        for (int i = 0; i < (bpsStorage.getNumStorageDirs()); i++) {
            Storage.StorageDirectory sd = bpsStorage.getStorageDir(i);
            Assert.assertFalse(sd.getRoot().getAbsolutePath().startsWith(dn0Vol1.getAbsolutePath()));
        }
        // 2. dn0Vol1 is removed from FsDataset
        FsDatasetSpi<? extends FsVolumeSpi> data = dn0.getFSDataset();
        try (FsDatasetSpi.FsVolumeReferences vols = data.getFsVolumeReferences()) {
            for (FsVolumeSpi volume : vols) {
                Assert.assertFalse(new File(volume.getStorageLocation().getUri()).getAbsolutePath().startsWith(dn0Vol1.getAbsolutePath()));
            }
        }
        // 3. all blocks on dn0Vol1 have been removed.
        for (ReplicaInfo replica : FsDatasetTestUtil.getReplicas(data, bpid)) {
            Assert.assertNotNull(replica.getVolume());
            Assert.assertFalse(new File(replica.getVolume().getStorageLocation().getUri()).getAbsolutePath().startsWith(dn0Vol1.getAbsolutePath()));
        }
        // 4. dn0Vol1 is not in DN0's configuration and dataDirs anymore.
        String[] dataDirStrs = dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY).split(",");
        Assert.assertEquals(1, dataDirStrs.length);
        Assert.assertFalse(dataDirStrs[0].contains(dn0Vol1.getAbsolutePath()));
    }

    /**
     * Test DataNode stops when the number of failed volumes exceeds
     * dfs.datanode.failed.volumes.tolerated .
     */
    @Test(timeout = 10000)
    public void testDataNodeShutdownAfterNumFailedVolumeExceedsTolerated() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        // make both data directories to fail on dn0
        final File dn0Vol1 = cluster.getInstanceStorageDir(0, 0);
        final File dn0Vol2 = cluster.getInstanceStorageDir(0, 1);
        DataNodeTestUtils.injectDataDirFailure(dn0Vol1, dn0Vol2);
        DataNode dn0 = cluster.getDataNodes().get(0);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol1));
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol2));
        // DN0 should stop after the number of failure disks exceed tolerated
        // value (1).
        dn0.checkDiskError();
        Assert.assertFalse(dn0.shouldRun());
    }

    /**
     * Test that DN does not shutdown, as long as failure volumes being hot swapped.
     */
    @Test
    public void testVolumeFailureRecoveredByHotSwappingVolume() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        final File dn0Vol1 = cluster.getInstanceStorageDir(0, 0);
        final File dn0Vol2 = cluster.getInstanceStorageDir(0, 1);
        final DataNode dn0 = cluster.getDataNodes().get(0);
        final String oldDataDirs = dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY);
        // Fail dn0Vol1 first.
        DataNodeTestUtils.injectDataDirFailure(dn0Vol1);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol1));
        // Hot swap out the failure volume.
        String dataDirs = dn0Vol2.getPath();
        Assert.assertThat(dn0.reconfigurePropertyImpl(DFS_DATANODE_DATA_DIR_KEY, dataDirs), Is.is(dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY)));
        // Fix failure volume dn0Vol1 and remount it back.
        DataNodeTestUtils.restoreDataDirFromFailure(dn0Vol1);
        Assert.assertThat(dn0.reconfigurePropertyImpl(DFS_DATANODE_DATA_DIR_KEY, oldDataDirs), Is.is(dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY)));
        // Fail dn0Vol2. Now since dn0Vol1 has been fixed, DN0 has sufficient
        // resources, thus it should keep running.
        DataNodeTestUtils.injectDataDirFailure(dn0Vol2);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol2));
        Assert.assertTrue(dn0.shouldRun());
    }

    /**
     * Test changing the number of volumes does not impact the disk failure
     * tolerance.
     */
    @Test
    public void testTolerateVolumeFailuresAfterAddingMoreVolumes() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        final File dn0Vol1 = cluster.getInstanceStorageDir(0, 0);
        final File dn0Vol2 = cluster.getInstanceStorageDir(0, 1);
        final File dn0VolNew = new File(dataDir, "data_new");
        final DataNode dn0 = cluster.getDataNodes().get(0);
        final String oldDataDirs = dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY);
        // Add a new volume to DN0
        Assert.assertThat(dn0.reconfigurePropertyImpl(DFS_DATANODE_DATA_DIR_KEY, ((oldDataDirs + ",") + (dn0VolNew.getAbsolutePath()))), Is.is(dn0.getConf().get(DFS_DATANODE_DATA_DIR_KEY)));
        // Fail dn0Vol1 first and hot swap it.
        DataNodeTestUtils.injectDataDirFailure(dn0Vol1);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol1));
        Assert.assertTrue(dn0.shouldRun());
        // Fail dn0Vol2, now dn0 should stop, because we only tolerate 1 disk failure.
        DataNodeTestUtils.injectDataDirFailure(dn0Vol2);
        DataNodeTestUtils.waitForDiskError(dn0, DataNodeTestUtils.getVolume(dn0, dn0Vol2));
        dn0.checkDiskError();
        Assert.assertFalse(dn0.shouldRun());
    }

    /**
     * Test that there are under replication blocks after vol failures
     */
    @Test
    public void testUnderReplicationAfterVolFailure() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        // Bring up one more datanode
        cluster.startDataNodes(conf, 1, true, null, null);
        cluster.waitActive();
        final BlockManager bm = cluster.getNamesystem().getBlockManager();
        Path file1 = new Path("/test1");
        DFSTestUtil.createFile(fs, file1, 1024, ((short) (3)), 1L);
        DFSTestUtil.waitReplication(fs, file1, ((short) (3)));
        // Fail the first volume on both datanodes
        File dn1Vol1 = cluster.getInstanceStorageDir(0, 0);
        File dn2Vol1 = cluster.getInstanceStorageDir(1, 0);
        DataNodeTestUtils.injectDataDirFailure(dn1Vol1, dn2Vol1);
        Path file2 = new Path("/test2");
        DFSTestUtil.createFile(fs, file2, 1024, ((short) (3)), 1L);
        DFSTestUtil.waitReplication(fs, file2, ((short) (3)));
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                // underReplicatedBlocks are due to failed volumes
                long underReplicatedBlocks = (bm.getLowRedundancyBlocksCount()) + (bm.getPendingReconstructionBlocksCount());
                if (underReplicatedBlocks > 0) {
                    return true;
                }
                TestDataNodeVolumeFailure.LOG.info("There is no under replicated block after volume failure.");
                return false;
            }
        }, 500, 60000);
    }

    /**
     * Test if there is volume failure, the DataNode will fail to start.
     *
     * We fail a volume by setting the parent directory non-writable.
     */
    @Test(timeout = 120000)
    public void testDataNodeFailToStartWithVolumeFailure() throws Exception {
        // Method to simulate volume failures is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        failedDir = new File(dataDir, "failedDir");
        Assert.assertTrue("Failed to fail a volume by setting it non-writable", ((failedDir.mkdir()) && (failedDir.setReadOnly())));
        startNewDataNodeWithDiskFailure(new File(failedDir, "newDir1"), false);
    }

    /**
     * DataNode will start and tolerate one failing disk according to config.
     *
     * We fail a volume by setting the parent directory non-writable.
     */
    @Test(timeout = 120000)
    public void testDNStartAndTolerateOneVolumeFailure() throws Exception {
        // Method to simulate volume failures is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        failedDir = new File(dataDir, "failedDir");
        Assert.assertTrue("Failed to fail a volume by setting it non-writable", ((failedDir.mkdir()) && (failedDir.setReadOnly())));
        startNewDataNodeWithDiskFailure(new File(failedDir, "newDir1"), true);
    }

    /**
     * Test if data directory is not readable/writable, DataNode won't start.
     */
    @Test(timeout = 120000)
    public void testDNFailToStartWithDataDirNonWritable() throws Exception {
        // Method to simulate volume failures is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        final File readOnlyDir = new File(dataDir, "nonWritable");
        Assert.assertTrue("Set the data dir permission non-writable", ((readOnlyDir.mkdir()) && (readOnlyDir.setReadOnly())));
        startNewDataNodeWithDiskFailure(new File(readOnlyDir, "newDir1"), false);
    }

    /**
     * DataNode will start and tolerate one non-writable data directory
     * according to config.
     */
    @Test(timeout = 120000)
    public void testDNStartAndTolerateOneDataDirNonWritable() throws Exception {
        // Method to simulate volume failures is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        final File readOnlyDir = new File(dataDir, "nonWritable");
        Assert.assertTrue("Set the data dir permission non-writable", ((readOnlyDir.mkdir()) && (readOnlyDir.setReadOnly())));
        startNewDataNodeWithDiskFailure(new File(readOnlyDir, "newDir1"), true);
    }
}

