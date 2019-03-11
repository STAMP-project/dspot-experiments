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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY;
import DFSConfigKeys.DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY;
import DFSConfigKeys.DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import DataStorage.VolumeBuilder;
import HdfsServerConstants.NodeType;
import StorageType.DEFAULT;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.BlockReader;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.DataStorage;
import org.apache.hadoop.hdfs.server.datanode.ReplicaHandler;
import org.apache.hadoop.hdfs.server.datanode.ReplicaInfo;
import org.apache.hadoop.hdfs.server.datanode.StorageLocation;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi.FsVolumeReferences;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.io.MultipleIOException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFsDatasetImpl {
    Logger LOG = LoggerFactory.getLogger(TestFsDatasetImpl.class);

    private static final String BASE_DIR = new FileSystemTestHelper().getTestRootDir();

    private static final int NUM_INIT_VOLUMES = 2;

    private static final String CLUSTER_ID = "cluser-id";

    private static final String[] BLOCK_POOL_IDS = new String[]{ "bpid-0", "bpid-1" };

    // Use to generate storageUuid
    private static final DataStorage dsForStorageUuid = new DataStorage(new org.apache.hadoop.hdfs.server.common.StorageInfo(NodeType.DATA_NODE));

    private Configuration conf;

    private DataNode datanode;

    private DataStorage storage;

    private FsDatasetImpl dataset;

    private static final String BLOCKPOOL = "BP-TEST";

    @Test
    public void testAddVolumes() throws IOException {
        final int numNewVolumes = 3;
        final int numExistingVolumes = getNumVolumes();
        final int totalVolumes = numNewVolumes + numExistingVolumes;
        Set<String> expectedVolumes = new HashSet<String>();
        List<NamespaceInfo> nsInfos = Lists.newArrayList();
        for (String bpid : TestFsDatasetImpl.BLOCK_POOL_IDS) {
            nsInfos.add(new NamespaceInfo(0, TestFsDatasetImpl.CLUSTER_ID, bpid, 1));
        }
        for (int i = 0; i < numNewVolumes; i++) {
            String path = ((TestFsDatasetImpl.BASE_DIR) + "/newData") + i;
            String pathUri = new Path(path).toUri().toString();
            expectedVolumes.add(new File(pathUri).getAbsolutePath());
            StorageLocation loc = StorageLocation.parse(pathUri);
            Storage.StorageDirectory sd = TestFsDatasetImpl.createStorageDirectory(new File(path), conf);
            DataStorage.VolumeBuilder builder = new DataStorage.VolumeBuilder(storage, sd);
            Mockito.when(storage.prepareVolume(ArgumentMatchers.eq(datanode), ArgumentMatchers.eq(loc), ArgumentMatchers.anyList())).thenReturn(builder);
            dataset.addVolume(loc, nsInfos);
            LOG.info(((("expectedVolumes " + i) + " is ") + (new File(pathUri).getAbsolutePath())));
        }
        Assert.assertEquals(totalVolumes, getNumVolumes());
        Assert.assertEquals(totalVolumes, dataset.storageMap.size());
        Set<String> actualVolumes = new HashSet<String>();
        try (FsDatasetSpi.FsVolumeReferences volumes = dataset.getFsVolumeReferences()) {
            for (int i = 0; i < numNewVolumes; i++) {
                String volumeName = volumes.get((numExistingVolumes + i)).toString();
                actualVolumes.add(volumeName);
                LOG.info(((("actualVolume " + i) + " is ") + volumeName));
            }
        }
        Assert.assertEquals(actualVolumes.size(), expectedVolumes.size());
        Assert.assertTrue(actualVolumes.containsAll(expectedVolumes));
    }

    @Test
    public void testAddVolumeWithSameStorageUuid() throws IOException {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            cluster.waitActive();
            Assert.assertTrue(cluster.getDataNodes().get(0).isConnectedToNN(cluster.getNameNode().getServiceRpcAddress()));
            MiniDFSCluster.DataNodeProperties dn = cluster.stopDataNode(0);
            File vol0 = cluster.getStorageDir(0, 0);
            File vol1 = cluster.getStorageDir(0, 1);
            Storage.StorageDirectory sd0 = new Storage.StorageDirectory(vol0);
            Storage.StorageDirectory sd1 = new Storage.StorageDirectory(vol1);
            FileUtils.copyFile(sd0.getVersionFile(), sd1.getVersionFile());
            cluster.restartDataNode(dn, true);
            cluster.waitActive();
            Assert.assertFalse(cluster.getDataNodes().get(0).isConnectedToNN(cluster.getNameNode().getServiceRpcAddress()));
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testRemoveVolumes() throws IOException {
        // Feed FsDataset with block metadata.
        final int NUM_BLOCKS = 100;
        for (int i = 0; i < NUM_BLOCKS; i++) {
            String bpid = TestFsDatasetImpl.BLOCK_POOL_IDS[(NUM_BLOCKS % (TestFsDatasetImpl.BLOCK_POOL_IDS.length))];
            ExtendedBlock eb = new ExtendedBlock(bpid, i);
            try (ReplicaHandler replica = dataset.createRbw(DEFAULT, null, eb, false)) {
            }
        }
        final String[] dataDirs = conf.get(DFS_DATANODE_DATA_DIR_KEY).split(",");
        final String volumePathToRemove = dataDirs[0];
        Set<StorageLocation> volumesToRemove = new HashSet<>();
        volumesToRemove.add(StorageLocation.parse(volumePathToRemove));
        FsVolumeReferences volReferences = dataset.getFsVolumeReferences();
        FsVolumeImpl volumeToRemove = null;
        for (FsVolumeSpi vol : volReferences) {
            if (vol.getStorageLocation().equals(volumesToRemove.iterator().next())) {
                volumeToRemove = ((FsVolumeImpl) (vol));
            }
        }
        Assert.assertTrue((volumeToRemove != null));
        volReferences.close();
        dataset.removeVolumes(volumesToRemove, true);
        int expectedNumVolumes = (dataDirs.length) - 1;
        Assert.assertEquals("The volume has been removed from the volumeList.", expectedNumVolumes, getNumVolumes());
        Assert.assertEquals("The volume has been removed from the storageMap.", expectedNumVolumes, dataset.storageMap.size());
        try {
            dataset.asyncDiskService.execute(volumeToRemove, new Runnable() {
                @Override
                public void run() {
                }
            });
            Assert.fail(("Expect RuntimeException: the volume has been removed from the " + "AsyncDiskService."));
        } catch (RuntimeException e) {
            GenericTestUtils.assertExceptionContains("Cannot find volume", e);
        }
        int totalNumReplicas = 0;
        for (String bpid : dataset.volumeMap.getBlockPoolList()) {
            totalNumReplicas += dataset.volumeMap.size(bpid);
        }
        Assert.assertEquals(("The replica infos on this volume has been removed from the " + "volumeMap."), (NUM_BLOCKS / (TestFsDatasetImpl.NUM_INIT_VOLUMES)), totalNumReplicas);
    }

    @Test(timeout = 5000)
    public void testRemoveNewlyAddedVolume() throws IOException {
        final int numExistingVolumes = getNumVolumes();
        List<NamespaceInfo> nsInfos = new ArrayList<>();
        for (String bpid : TestFsDatasetImpl.BLOCK_POOL_IDS) {
            nsInfos.add(new NamespaceInfo(0, TestFsDatasetImpl.CLUSTER_ID, bpid, 1));
        }
        String newVolumePath = (TestFsDatasetImpl.BASE_DIR) + "/newVolumeToRemoveLater";
        StorageLocation loc = StorageLocation.parse(newVolumePath);
        Storage.StorageDirectory sd = TestFsDatasetImpl.createStorageDirectory(new File(newVolumePath), conf);
        DataStorage.VolumeBuilder builder = new DataStorage.VolumeBuilder(storage, sd);
        Mockito.when(storage.prepareVolume(ArgumentMatchers.eq(datanode), ArgumentMatchers.eq(loc), ArgumentMatchers.anyList())).thenReturn(builder);
        dataset.addVolume(loc, nsInfos);
        Assert.assertEquals((numExistingVolumes + 1), getNumVolumes());
        Mockito.when(storage.getNumStorageDirs()).thenReturn((numExistingVolumes + 1));
        Mockito.when(storage.getStorageDir(numExistingVolumes)).thenReturn(sd);
        Set<StorageLocation> volumesToRemove = new HashSet<>();
        volumesToRemove.add(loc);
        dataset.removeVolumes(volumesToRemove, true);
        Assert.assertEquals(numExistingVolumes, getNumVolumes());
    }

    @Test
    public void testAddVolumeFailureReleasesInUseLock() throws IOException {
        FsDatasetImpl spyDataset = Mockito.spy(dataset);
        FsVolumeImpl mockVolume = Mockito.mock(FsVolumeImpl.class);
        File badDir = new File(TestFsDatasetImpl.BASE_DIR, "bad");
        badDir.mkdirs();
        Mockito.doReturn(mockVolume).when(spyDataset).createFsVolume(ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageDirectory.class), ArgumentMatchers.any(StorageLocation.class));
        Mockito.doThrow(new IOException("Failed to getVolumeMap()")).when(mockVolume).getVolumeMap(ArgumentMatchers.anyString(), ArgumentMatchers.any(ReplicaMap.class), ArgumentMatchers.any(RamDiskReplicaLruTracker.class));
        Storage.StorageDirectory sd = TestFsDatasetImpl.createStorageDirectory(badDir, conf);
        sd.lock();
        DataStorage.VolumeBuilder builder = new DataStorage.VolumeBuilder(storage, sd);
        Mockito.when(storage.prepareVolume(ArgumentMatchers.eq(datanode), ArgumentMatchers.eq(StorageLocation.parse(badDir.toURI().toString())), ArgumentMatchers.anyList())).thenReturn(builder);
        StorageLocation location = StorageLocation.parse(badDir.toString());
        List<NamespaceInfo> nsInfos = Lists.newArrayList();
        for (String bpid : TestFsDatasetImpl.BLOCK_POOL_IDS) {
            nsInfos.add(new NamespaceInfo(0, TestFsDatasetImpl.CLUSTER_ID, bpid, 1));
        }
        try {
            spyDataset.addVolume(location, nsInfos);
            Assert.fail("Expect to throw MultipleIOException");
        } catch (MultipleIOException e) {
        }
        FsDatasetTestUtil.assertFileLockReleased(badDir.toString());
    }

    @Test
    public void testDeletingBlocks() throws IOException {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            DataNode dn = cluster.getDataNodes().get(0);
            FsDatasetSpi<?> ds = DataNodeTestUtils.getFSDataset(dn);
            ds.addBlockPool(TestFsDatasetImpl.BLOCKPOOL, conf);
            FsVolumeImpl vol;
            try (FsDatasetSpi.FsVolumeReferences volumes = ds.getFsVolumeReferences()) {
                vol = ((FsVolumeImpl) (volumes.get(0)));
            }
            ExtendedBlock eb;
            ReplicaInfo info;
            List<Block> blockList = new ArrayList<>();
            for (int i = 1; i <= 63; i++) {
                eb = new ExtendedBlock(TestFsDatasetImpl.BLOCKPOOL, i, 1, (1000 + i));
                cluster.getFsDatasetTestUtils(0).createFinalizedReplica(eb);
                blockList.add(eb.getLocalBlock());
            }
            ds.invalidate(TestFsDatasetImpl.BLOCKPOOL, blockList.toArray(new Block[0]));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Nothing to do
            }
            Assert.assertTrue(ds.isDeletingBlock(TestFsDatasetImpl.BLOCKPOOL, blockList.get(0).getBlockId()));
            blockList.clear();
            eb = new ExtendedBlock(TestFsDatasetImpl.BLOCKPOOL, 64, 1, 1064);
            cluster.getFsDatasetTestUtils(0).createFinalizedReplica(eb);
            blockList.add(eb.getLocalBlock());
            ds.invalidate(TestFsDatasetImpl.BLOCKPOOL, blockList.toArray(new Block[0]));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Nothing to do
            }
            Assert.assertFalse(ds.isDeletingBlock(TestFsDatasetImpl.BLOCKPOOL, blockList.get(0).getBlockId()));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testDuplicateReplicaResolution() throws IOException {
        FsVolumeImpl fsv1 = Mockito.mock(FsVolumeImpl.class);
        FsVolumeImpl fsv2 = Mockito.mock(FsVolumeImpl.class);
        File f1 = new File("d1/block");
        File f2 = new File("d2/block");
        ReplicaInfo replicaOlder = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 1, 1, fsv1, f1);
        ReplicaInfo replica = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 2, 2, fsv1, f1);
        ReplicaInfo replicaSame = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 2, 2, fsv1, f1);
        ReplicaInfo replicaNewer = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 3, 3, fsv1, f1);
        ReplicaInfo replicaOtherOlder = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 1, 1, fsv2, f2);
        ReplicaInfo replicaOtherSame = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 2, 2, fsv2, f2);
        ReplicaInfo replicaOtherNewer = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(1, 3, 3, fsv2, f2);
        // equivalent path so don't remove either
        Assert.assertNull(BlockPoolSlice.selectReplicaToDelete(replicaSame, replica));
        Assert.assertNull(BlockPoolSlice.selectReplicaToDelete(replicaOlder, replica));
        Assert.assertNull(BlockPoolSlice.selectReplicaToDelete(replicaNewer, replica));
        // keep latest found replica
        Assert.assertSame(replica, BlockPoolSlice.selectReplicaToDelete(replicaOtherSame, replica));
        Assert.assertSame(replicaOtherOlder, BlockPoolSlice.selectReplicaToDelete(replicaOtherOlder, replica));
        Assert.assertSame(replica, BlockPoolSlice.selectReplicaToDelete(replicaOtherNewer, replica));
    }

    @Test
    public void testLoadingDfsUsedForVolumes() throws IOException, InterruptedException {
        long waitIntervalTime = 5000;
        // Initialize the cachedDfsUsedIntervalTime larger than waitIntervalTime
        // to avoid cache-dfsused time expired
        long cachedDfsUsedIntervalTime = waitIntervalTime + 1000;
        conf.setLong(DFSConfigKeys.DFS_DN_CACHED_DFSUSED_CHECK_INTERVAL_MS, cachedDfsUsedIntervalTime);
        long cacheDfsUsed = 1024;
        long dfsUsed = getDfsUsedValueOfNewVolume(cacheDfsUsed, waitIntervalTime);
        Assert.assertEquals(cacheDfsUsed, dfsUsed);
    }

    @Test
    public void testLoadingDfsUsedForVolumesExpired() throws IOException, InterruptedException {
        long waitIntervalTime = 5000;
        // Initialize the cachedDfsUsedIntervalTime smaller than waitIntervalTime
        // to make cache-dfsused time expired
        long cachedDfsUsedIntervalTime = waitIntervalTime - 1000;
        conf.setLong(DFSConfigKeys.DFS_DN_CACHED_DFSUSED_CHECK_INTERVAL_MS, cachedDfsUsedIntervalTime);
        long cacheDfsUsed = 1024;
        long dfsUsed = getDfsUsedValueOfNewVolume(cacheDfsUsed, waitIntervalTime);
        // Because the cache-dfsused expired and the dfsUsed will be recalculated
        Assert.assertTrue((cacheDfsUsed != dfsUsed));
    }

    @Test(timeout = 60000)
    public void testRemoveVolumeBeingWritten() throws Exception {
        // Will write and remove on dn0.
        final ExtendedBlock eb = new ExtendedBlock(TestFsDatasetImpl.BLOCK_POOL_IDS[0], 0);
        final CountDownLatch startFinalizeLatch = new CountDownLatch(1);
        final CountDownLatch blockReportReceivedLatch = new CountDownLatch(1);
        final CountDownLatch volRemoveStartedLatch = new CountDownLatch(1);
        final CountDownLatch volRemoveCompletedLatch = new CountDownLatch(1);
        class BlockReportThread extends Thread {
            public void run() {
                // Lets wait for the volume remove process to start
                try {
                    volRemoveStartedLatch.await();
                } catch (Exception e) {
                    LOG.info("Unexpected exception when waiting for vol removal:", e);
                }
                LOG.info("Getting block report");
                dataset.getBlockReports(eb.getBlockPoolId());
                LOG.info("Successfully received block report");
                blockReportReceivedLatch.countDown();
            }
        }
        class ResponderThread extends Thread {
            public void run() {
                try (ReplicaHandler replica = dataset.createRbw(DEFAULT, null, eb, false)) {
                    LOG.info("CreateRbw finished");
                    startFinalizeLatch.countDown();
                    // Slow down while we're holding the reference to the volume.
                    // As we finalize a block, the volume is removed in parallel.
                    // Ignore any interrupts coming out of volume shutdown.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOG.info("Ignoring ", ie);
                    }
                    // Lets wait for the other thread finish getting block report
                    blockReportReceivedLatch.await();
                    dataset.finalizeBlock(eb, false);
                    LOG.info("FinalizeBlock finished");
                } catch (Exception e) {
                    LOG.warn("Exception caught. This should not affect the test", e);
                }
            }
        }
        class VolRemoveThread extends Thread {
            public void run() {
                Set<StorageLocation> volumesToRemove = new HashSet<>();
                try {
                    volumesToRemove.add(dataset.getVolume(eb).getStorageLocation());
                } catch (Exception e) {
                    LOG.info("Problem preparing volumes to remove: ", e);
                    Assert.fail(("Exception in remove volume thread, check log for " + "details."));
                }
                LOG.info(("Removing volume " + volumesToRemove));
                dataset.removeVolumes(volumesToRemove, true);
                volRemoveCompletedLatch.countDown();
                LOG.info(("Removed volume " + volumesToRemove));
            }
        }
        // Start the volume write operation
        ResponderThread responderThread = new ResponderThread();
        responderThread.start();
        startFinalizeLatch.await();
        // Start the block report get operation
        final BlockReportThread blockReportThread = new BlockReportThread();
        blockReportThread.start();
        // Start the volume remove operation
        VolRemoveThread volRemoveThread = new VolRemoveThread();
        volRemoveThread.start();
        // Let volume write and remove operation be
        // blocked for few seconds
        Thread.sleep(2000);
        // Signal block report receiver and volume writer
        // thread to complete their operations so that vol
        // remove can proceed
        volRemoveStartedLatch.countDown();
        // Verify if block report can be received
        // when volume is in use and also being removed
        blockReportReceivedLatch.await();
        // Verify if volume can be removed safely when there
        // are read/write operation in-progress
        volRemoveCompletedLatch.await();
    }

    /**
     * Tests stopping all the active DataXceiver thread on volume failure event.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCleanShutdownOfVolume() throws Exception {
        MiniDFSCluster cluster = null;
        try {
            Configuration config = new HdfsConfiguration();
            config.setLong(DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY, 1000);
            config.setTimeDuration(DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY, 0, TimeUnit.MILLISECONDS);
            config.setInt(DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY, 1);
            cluster = new MiniDFSCluster.Builder(config, GenericTestUtils.getRandomizedTestDir()).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            DataNode dataNode = cluster.getDataNodes().get(0);
            Path filePath = new Path("test.dat");
            // Create a file and keep the output stream unclosed.
            FSDataOutputStream out = fs.create(filePath, ((short) (1)));
            out.write(1);
            out.hflush();
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, filePath);
            final FsVolumeImpl volume = ((FsVolumeImpl) (dataNode.getFSDataset().getVolume(block)));
            File finalizedDir = volume.getFinalizedDir(cluster.getNamesystem().getBlockPoolId());
            LocatedBlock lb = DFSTestUtil.getAllBlocks(fs, filePath).get(0);
            DatanodeInfo info = lb.getLocations()[0];
            if (finalizedDir.exists()) {
                // Remove write and execute access so that checkDiskErrorThread detects
                // this volume is bad.
                finalizedDir.setExecutable(false);
                Assert.assertTrue(FileUtil.setWritable(finalizedDir, false));
            }
            Assert.assertTrue(("Reference count for the volume should be greater " + "than 0"), ((volume.getReferenceCount()) > 0));
            // Invoke the synchronous checkDiskError method
            dataNode.checkDiskError();
            // Sleep for 1 second so that datanode can interrupt and cluster clean up
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (volume.getReferenceCount()) == 0;
                }
            }, 100, 1000);
            Assert.assertThat(dataNode.getFSDataset().getNumFailedVolumes(), Is.is(1));
            try {
                out.close();
                Assert.fail(("This is not a valid code path. " + "out.close should have thrown an exception."));
            } catch (IOException ioe) {
                GenericTestUtils.assertExceptionContains(info.getXferAddr(), ioe);
            }
            Assert.assertTrue(FileUtil.setWritable(finalizedDir, true));
            finalizedDir.setExecutable(true);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testReportBadBlocks() throws Exception {
        boolean threwException = false;
        MiniDFSCluster cluster = null;
        try {
            Configuration config = new HdfsConfiguration();
            cluster = new MiniDFSCluster.Builder(config).numDataNodes(1).build();
            cluster.waitActive();
            Assert.assertEquals(0, cluster.getNamesystem().getCorruptReplicaBlocks());
            DataNode dataNode = cluster.getDataNodes().get(0);
            ExtendedBlock block = new ExtendedBlock(cluster.getNamesystem().getBlockPoolId(), 0);
            try {
                // Test the reportBadBlocks when the volume is null
                dataNode.reportBadBlocks(block);
            } catch (NullPointerException npe) {
                threwException = true;
            }
            Thread.sleep(3000);
            Assert.assertFalse(threwException);
            Assert.assertEquals(0, cluster.getNamesystem().getCorruptReplicaBlocks());
            FileSystem fs = cluster.getFileSystem();
            Path filePath = new Path("testData");
            DFSTestUtil.createFile(fs, filePath, 1, ((short) (1)), 0);
            block = DFSTestUtil.getFirstBlock(fs, filePath);
            // Test for the overloaded method reportBadBlocks
            dataNode.reportBadBlocks(block, dataNode.getFSDataset().getFsVolumeReferences().get(0));
            Thread.sleep(3000);
            BlockManagerTestUtil.updateState(cluster.getNamesystem().getBlockManager());
            // Verify the bad block has been reported to namenode
            Assert.assertEquals(1, cluster.getNamesystem().getCorruptReplicaBlocks());
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testMoveBlockFailure() {
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.DISK }).storagesPerDatanode(2).build();
            FileSystem fs = cluster.getFileSystem();
            DataNode dataNode = cluster.getDataNodes().get(0);
            Path filePath = new Path("testData");
            DFSTestUtil.createFile(fs, filePath, 100, ((short) (1)), 0);
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, filePath);
            FsDatasetImpl fsDataSetImpl = ((FsDatasetImpl) (dataNode.getFSDataset()));
            ReplicaInfo newReplicaInfo = createNewReplicaObj(block, fsDataSetImpl);
            // Append to file to update its GS
            FSDataOutputStream out = fs.append(filePath, ((short) (1)));
            out.write(100);
            out.hflush();
            // Call finalizeNewReplica
            LOG.info("GenerationStamp of old replica: {}", block.getGenerationStamp());
            LOG.info("GenerationStamp of new replica: {}", fsDataSetImpl.getReplicaInfo(block.getBlockPoolId(), newReplicaInfo.getBlockId()).getGenerationStamp());
            LambdaTestUtils.intercept(IOException.class, ("Generation Stamp " + "should be monotonically increased."), () -> fsDataSetImpl.finalizeNewReplica(newReplicaInfo, block));
        } catch (Exception ex) {
            LOG.info("Exception in testMoveBlockFailure ", ex);
            Assert.fail("Exception while testing testMoveBlockFailure ");
        } finally {
            if (cluster.isClusterUp()) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 30000)
    public void testMoveBlockSuccess() {
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.DISK }).storagesPerDatanode(2).build();
            FileSystem fs = cluster.getFileSystem();
            DataNode dataNode = cluster.getDataNodes().get(0);
            Path filePath = new Path("testData");
            DFSTestUtil.createFile(fs, filePath, 100, ((short) (1)), 0);
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, filePath);
            FsDatasetImpl fsDataSetImpl = ((FsDatasetImpl) (dataNode.getFSDataset()));
            ReplicaInfo newReplicaInfo = createNewReplicaObj(block, fsDataSetImpl);
            fsDataSetImpl.finalizeNewReplica(newReplicaInfo, block);
        } catch (Exception ex) {
            LOG.info("Exception in testMoveBlockSuccess ", ex);
            Assert.fail("MoveBlock operation should succeed");
        } finally {
            if (cluster.isClusterUp()) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 3000000)
    public void testBlockReadOpWhileMovingBlock() throws IOException {
        MiniDFSCluster cluster = null;
        try {
            // Setup cluster
            conf.setInt(DFS_REPLICATION_KEY, 1);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.DISK }).storagesPerDatanode(2).build();
            FileSystem fs = cluster.getFileSystem();
            DataNode dataNode = cluster.getDataNodes().get(0);
            // Create test file with ASCII data
            Path filePath = new Path("/tmp/testData");
            String blockData = RandomStringUtils.randomAscii((512 * 4));
            FSDataOutputStream fout = fs.create(filePath);
            fout.writeBytes(blockData);
            fout.close();
            Assert.assertEquals(blockData, DFSTestUtil.readFile(fs, filePath));
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, filePath);
            BlockReaderTestUtil util = new BlockReaderTestUtil(cluster, new HdfsConfiguration(conf));
            LocatedBlock blk = util.getFileBlocks(filePath, (512 * 2)).get(0);
            File[] blkFiles = cluster.getAllBlockFiles(block);
            // Part 1: Read partial data from block
            LOG.info("Reading partial data for block {} before moving it: ", blk.getBlock().toString());
            BlockReader blkReader = BlockReaderTestUtil.getBlockReader(((DistributedFileSystem) (fs)), blk, 0, (512 * 2));
            byte[] buf = new byte[512 * 2];
            blkReader.read(buf, 0, 512);
            Assert.assertEquals(blockData.substring(0, 512), new String(buf, StandardCharsets.US_ASCII).substring(0, 512));
            // Part 2: Move block and than read remaining block
            FsDatasetImpl fsDataSetImpl = ((FsDatasetImpl) (dataNode.getFSDataset()));
            ReplicaInfo replicaInfo = fsDataSetImpl.getReplicaInfo(block);
            FsVolumeSpi destVolume = getDestinationVolume(block, fsDataSetImpl);
            Assert.assertNotNull("Destination volume should not be null.", destVolume);
            fsDataSetImpl.moveBlock(block, replicaInfo, destVolume.obtainReference());
            // Trigger block report to update block info in NN
            cluster.triggerBlockReports();
            blkReader.read(buf, 512, 512);
            Assert.assertEquals(blockData.substring(0, (512 * 2)), new String(buf, StandardCharsets.US_ASCII).substring(0, (512 * 2)));
            blkReader = BlockReaderTestUtil.getBlockReader(((DistributedFileSystem) (fs)), blk, 0, blockData.length());
            buf = new byte[512 * 4];
            blkReader.read(buf, 0, (512 * 4));
            Assert.assertEquals(blockData, new String(buf, StandardCharsets.US_ASCII));
            // Part 3: 1. Close the block reader
            // 2. Assert source block doesn't exist on initial volume
            // 3. Assert new file location for block is different
            // 4. Confirm client can read data from new location
            blkReader.close();
            ExtendedBlock block2 = DFSTestUtil.getFirstBlock(fs, filePath);
            File[] blkFiles2 = cluster.getAllBlockFiles(block2);
            blk = util.getFileBlocks(filePath, (512 * 4)).get(0);
            blkReader = BlockReaderTestUtil.getBlockReader(((DistributedFileSystem) (fs)), blk, 0, blockData.length());
            blkReader.read(buf, 0, (512 * 4));
            Assert.assertFalse(Files.exists(Paths.get(blkFiles[0].getAbsolutePath())));
            Assert.assertNotEquals(blkFiles[0], blkFiles2[0]);
            Assert.assertEquals(blockData, new String(buf, StandardCharsets.US_ASCII));
        } finally {
            if (cluster.isClusterUp()) {
                cluster.shutdown();
            }
        }
    }
}

