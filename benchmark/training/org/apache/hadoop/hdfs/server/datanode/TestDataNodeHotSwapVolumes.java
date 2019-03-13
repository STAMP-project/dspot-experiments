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


import DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DataNode.ChangedVolumes;
import FsDatasetSpi.FsVolumeReferences;
import Storage.StorageDirectory;
import StorageType.SSD;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.ReconfigurationException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.BlockMissingException;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.BlockListAsLongs;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsVolumeImpl;
import org.apache.hadoop.hdfs.server.protocol.BlockReportContext;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.hdfs.server.protocol.StorageBlockReport;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDataNodeHotSwapVolumes {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataNodeHotSwapVolumes.class);

    private static final int BLOCK_SIZE = 512;

    private static final int DEFAULT_STORAGES_PER_DATANODE = 2;

    private MiniDFSCluster cluster;

    private Configuration conf;

    @Test
    public void testParseChangedVolumes() throws IOException {
        startDFSCluster(1, 1);
        DataNode dn = cluster.getDataNodes().get(0);
        Configuration conf = dn.getConf();
        String oldPaths = conf.get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY);
        List<StorageLocation> oldLocations = new ArrayList<StorageLocation>();
        for (String path : oldPaths.split(",")) {
            oldLocations.add(StorageLocation.parse(path));
        }
        Assert.assertFalse(oldLocations.isEmpty());
        String newPaths = (new File(oldLocations.get(0).getUri()).getAbsolutePath()) + ",/foo/path1,/foo/path2";
        DataNode.ChangedVolumes changedVolumes = dn.parseChangedVolumes(newPaths);
        List<StorageLocation> newVolumes = changedVolumes.newLocations;
        Assert.assertEquals(2, newVolumes.size());
        Assert.assertEquals(new File("/foo/path1").getAbsolutePath(), new File(newVolumes.get(0).getUri()).getAbsolutePath());
        Assert.assertEquals(new File("/foo/path2").getAbsolutePath(), new File(newVolumes.get(1).getUri()).getAbsolutePath());
        List<StorageLocation> removedVolumes = changedVolumes.deactivateLocations;
        Assert.assertEquals(1, removedVolumes.size());
        Assert.assertEquals(oldLocations.get(1).getNormalizedUri(), removedVolumes.get(0).getNormalizedUri());
        Assert.assertEquals(1, changedVolumes.unchangedLocations.size());
        Assert.assertEquals(oldLocations.get(0).getNormalizedUri(), changedVolumes.unchangedLocations.get(0).getNormalizedUri());
    }

    @Test
    public void testParseChangedVolumesFailures() throws IOException {
        startDFSCluster(1, 1);
        DataNode dn = cluster.getDataNodes().get(0);
        try {
            dn.parseChangedVolumes("");
            Assert.fail("Should throw IOException: empty inputs.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("No directory is specified.", e);
        }
    }

    @Test
    public void testParseStorageTypeChanges() throws IOException {
        startDFSCluster(1, 1);
        DataNode dn = cluster.getDataNodes().get(0);
        Configuration conf = dn.getConf();
        List<StorageLocation> oldLocations = DataNode.getStorageLocations(conf);
        // Change storage type of an existing StorageLocation
        String newLoc = String.format("[%s]%s", SSD, oldLocations.get(1).getUri());
        String newDataDirs = ((oldLocations.get(0).toString()) + ",") + newLoc;
        try {
            dn.parseChangedVolumes(newDataDirs);
            Assert.fail("should throw IOE because storage type changes.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Changing storage type is not allowed", e);
        }
    }

    /**
     * Test adding one volume on a running MiniDFSCluster with only one NameNode.
     */
    @Test(timeout = 60000)
    public void testAddOneNewVolume() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 1);
        String bpid = cluster.getNamesystem().getBlockPoolId();
        final int numBlocks = 10;
        addVolumes(1);
        Path testFile = new Path("/test");
        createFile(testFile, numBlocks);
        List<Map<DatanodeStorage, BlockListAsLongs>> blockReports = cluster.getAllBlockReports(bpid);
        Assert.assertEquals(1, blockReports.size());// 1 DataNode

        Assert.assertEquals(3, blockReports.get(0).size());// 3 volumes

        // FSVolumeList uses Round-Robin block chooser by default. Thus the new
        // blocks should be evenly located in all volumes.
        int minNumBlocks = Integer.MAX_VALUE;
        int maxNumBlocks = Integer.MIN_VALUE;
        for (BlockListAsLongs blockList : blockReports.get(0).values()) {
            minNumBlocks = Math.min(minNumBlocks, blockList.getNumberOfBlocks());
            maxNumBlocks = Math.max(maxNumBlocks, blockList.getNumberOfBlocks());
        }
        Assert.assertTrue(((Math.abs((maxNumBlocks - maxNumBlocks))) <= 1));
        TestDataNodeHotSwapVolumes.verifyFileLength(cluster.getFileSystem(), testFile, numBlocks);
    }

    @Test(timeout = 60000)
    public void testAddVolumesDuringWrite() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 1);
        int numVolumes = cluster.getStoragesPerDatanode();
        String bpid = cluster.getNamesystem().getBlockPoolId();
        Path testFile = new Path("/test");
        // Each volume has 2 blocks
        int initialBlockCount = numVolumes * 2;
        createFile(testFile, initialBlockCount);
        int newVolumeCount = 5;
        addVolumes(newVolumeCount);
        numVolumes += newVolumeCount;
        int additionalBlockCount = 9;
        int totalBlockCount = initialBlockCount + additionalBlockCount;
        // Continue to write the same file, thus the new volumes will have blocks.
        DFSTestUtil.appendFile(cluster.getFileSystem(), testFile, ((TestDataNodeHotSwapVolumes.BLOCK_SIZE) * additionalBlockCount));
        TestDataNodeHotSwapVolumes.verifyFileLength(cluster.getFileSystem(), testFile, totalBlockCount);
        // After appending data, each new volume added should
        // have 1 block each.
        List<Integer> expectedNumBlocks = Arrays.asList(1, 1, 1, 1, 1, 4, 4);
        List<Map<DatanodeStorage, BlockListAsLongs>> blockReports = cluster.getAllBlockReports(bpid);
        Assert.assertEquals(1, blockReports.size());// 1 DataNode

        Assert.assertEquals(numVolumes, blockReports.get(0).size());// 7 volumes

        Map<DatanodeStorage, BlockListAsLongs> dnReport = blockReports.get(0);
        List<Integer> actualNumBlocks = new ArrayList<Integer>();
        for (BlockListAsLongs blockList : dnReport.values()) {
            actualNumBlocks.add(blockList.getNumberOfBlocks());
        }
        Collections.sort(actualNumBlocks);
        Assert.assertEquals(expectedNumBlocks, actualNumBlocks);
    }

    @Test(timeout = 180000)
    public void testAddVolumesConcurrently() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 1, 10);
        int numVolumes = cluster.getStoragesPerDatanode();
        String blockPoolId = cluster.getNamesystem().getBlockPoolId();
        Path testFile = new Path("/test");
        // Each volume has 2 blocks
        int initialBlockCount = numVolumes * 2;
        createFile(testFile, initialBlockCount);
        DataNode dn = cluster.getDataNodes().get(0);
        final FsDatasetSpi<? extends FsVolumeSpi> data = dn.data;
        dn.data = Mockito.spy(data);
        final int newVolumeCount = 40;
        List<Thread> addVolumeDelayedThreads = new ArrayList<>();
        AtomicBoolean addVolumeError = new AtomicBoolean(false);
        AtomicBoolean listStorageError = new AtomicBoolean(false);
        CountDownLatch addVolumeCompletionLatch = new CountDownLatch(newVolumeCount);
        // Thread to list all storage available at DataNode,
        // when the volumes are being added in parallel.
        final Thread listStorageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while ((addVolumeCompletionLatch.getCount()) != newVolumeCount) {
                    int i = 0;
                    while ((i++) < 1000) {
                        try {
                            dn.getStorage().listStorageDirectories();
                        } catch (Exception e) {
                            listStorageError.set(true);
                            TestDataNodeHotSwapVolumes.LOG.error(("Error listing storage: " + e));
                        }
                    } 
                } 
            }
        });
        listStorageThread.start();
        // FsDatasetImpl addVolume mocked to perform the operation asynchronously
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Random r = new Random();
                Thread addVolThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            r.setSeed(Time.now());
                            // Let 50% of add volume operations
                            // start after an initial delay.
                            if ((r.nextInt(10)) > 4) {
                                int s = (r.nextInt(10)) + 1;
                                Thread.sleep((s * 100));
                            }
                            invocationOnMock.callRealMethod();
                        } catch (Throwable throwable) {
                            addVolumeError.set(true);
                            TestDataNodeHotSwapVolumes.LOG.error(("Error adding volume: " + throwable));
                        } finally {
                            addVolumeCompletionLatch.countDown();
                        }
                    }
                });
                addVolumeDelayedThreads.add(addVolThread);
                addVolThread.start();
                return null;
            }
        }).when(dn.data).addVolume(ArgumentMatchers.any(StorageLocation.class), ArgumentMatchers.any(List.class));
        addVolumes(newVolumeCount, addVolumeCompletionLatch);
        numVolumes += newVolumeCount;
        // Wait for all addVolume and listStorage Threads to complete
        for (Thread t : addVolumeDelayedThreads) {
            t.join();
        }
        listStorageThread.join();
        // Verify errors while adding volumes and listing storage directories
        Assert.assertEquals("Error adding volumes!", false, addVolumeError.get());
        Assert.assertEquals("Error listing storage!", false, listStorageError.get());
        int additionalBlockCount = 9;
        int totalBlockCount = initialBlockCount + additionalBlockCount;
        // Continue to write the same file, thus the new volumes will have blocks.
        DFSTestUtil.appendFile(cluster.getFileSystem(), testFile, ((TestDataNodeHotSwapVolumes.BLOCK_SIZE) * additionalBlockCount));
        TestDataNodeHotSwapVolumes.verifyFileLength(cluster.getFileSystem(), testFile, totalBlockCount);
        List<Map<DatanodeStorage, BlockListAsLongs>> blockReports = cluster.getAllBlockReports(blockPoolId);
        Assert.assertEquals(1, blockReports.size());
        Assert.assertEquals(numVolumes, blockReports.get(0).size());
    }

    @Test(timeout = 60000)
    public void testAddVolumesToFederationNN() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        // Starts a Cluster with 2 NameNode and 3 DataNodes. Each DataNode has 2
        // volumes.
        final int numNameNodes = 2;
        final int numDataNodes = 1;
        startDFSCluster(numNameNodes, numDataNodes);
        Path testFile = new Path("/test");
        // Create a file on the first namespace with 4 blocks.
        createFile(0, testFile, 4);
        // Create a file on the second namespace with 4 blocks.
        createFile(1, testFile, 4);
        // Add 2 volumes to the first DataNode.
        final int numNewVolumes = 2;
        addVolumes(numNewVolumes);
        // Append to the file on the first namespace.
        DFSTestUtil.appendFile(cluster.getFileSystem(0), testFile, ((TestDataNodeHotSwapVolumes.BLOCK_SIZE) * 8));
        List<List<Integer>> actualNumBlocks = getNumBlocksReport(0);
        Assert.assertEquals(cluster.getDataNodes().size(), actualNumBlocks.size());
        List<Integer> blocksOnFirstDN = actualNumBlocks.get(0);
        Collections.sort(blocksOnFirstDN);
        Assert.assertEquals(Arrays.asList(2, 2, 4, 4), blocksOnFirstDN);
        // Verify the second namespace also has the new volumes and they are empty.
        actualNumBlocks = getNumBlocksReport(1);
        Assert.assertEquals(4, actualNumBlocks.get(0).size());
        Assert.assertEquals(numNewVolumes, Collections.frequency(actualNumBlocks.get(0), 0));
    }

    @Test(timeout = 60000)
    public void testRemoveOneVolume() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 1);
        final short replFactor = 1;
        Path testFile = new Path("/test");
        createFile(testFile, 10, replFactor);
        DataNode dn = cluster.getDataNodes().get(0);
        Collection<String> oldDirs = TestDataNodeHotSwapVolumes.getDataDirs(dn);
        String newDirs = oldDirs.iterator().next();// Keep the first volume.

        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFS_DATANODE_DATA_DIR_KEY, newDirs), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
        TestDataNodeHotSwapVolumes.assertFileLocksReleased(new ArrayList<String>(oldDirs).subList(1, oldDirs.size()));
        dn.scheduleAllBlockReport(0);
        try {
            DFSTestUtil.readFile(cluster.getFileSystem(), testFile);
            Assert.fail("Expect to throw BlockMissingException.");
        } catch (BlockMissingException e) {
            GenericTestUtils.assertExceptionContains("Could not obtain block", e);
        }
        Path newFile = new Path("/newFile");
        createFile(newFile, 6);
        String bpid = cluster.getNamesystem().getBlockPoolId();
        List<Map<DatanodeStorage, BlockListAsLongs>> blockReports = cluster.getAllBlockReports(bpid);
        Assert.assertEquals(((int) (replFactor)), blockReports.size());
        BlockListAsLongs blocksForVolume1 = blockReports.get(0).values().iterator().next();
        // The first volume has half of the testFile and full of newFile.
        Assert.assertEquals(((10 / 2) + 6), blocksForVolume1.getNumberOfBlocks());
    }

    @Test(timeout = 60000)
    public void testReplicatingAfterRemoveVolume() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 2);
        final FileSystem fs = cluster.getFileSystem();
        final short replFactor = 2;
        Path testFile = new Path("/test");
        createFile(testFile, 4, replFactor);
        DataNode dn = cluster.getDataNodes().get(0);
        Collection<String> oldDirs = TestDataNodeHotSwapVolumes.getDataDirs(dn);
        // Findout the storage with block and remove it
        ExtendedBlock block = DFSTestUtil.getAllBlocks(fs, testFile).get(1).getBlock();
        FsVolumeSpi volumeWithBlock = dn.getFSDataset().getVolume(block);
        String dirWithBlock = (("[" + (volumeWithBlock.getStorageType())) + "]") + (volumeWithBlock.getStorageLocation().getUri());
        String newDirs = dirWithBlock;
        for (String dir : oldDirs) {
            if (dirWithBlock.startsWith(dir)) {
                continue;
            }
            newDirs = dir;
            break;
        }
        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFS_DATANODE_DATA_DIR_KEY, newDirs), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
        oldDirs.remove(newDirs);
        TestDataNodeHotSwapVolumes.assertFileLocksReleased(oldDirs);
        TestDataNodeHotSwapVolumes.triggerDeleteReport(dn);
        TestDataNodeHotSwapVolumes.waitReplication(fs, testFile, 1, 1);
        DFSTestUtil.waitReplication(fs, testFile, replFactor);
    }

    @Test
    public void testAddVolumeFailures() throws IOException {
        startDFSCluster(1, 1);
        final String dataDir = cluster.getDataDirectory();
        DataNode dn = cluster.getDataNodes().get(0);
        List<String> newDirs = Lists.newArrayList();
        final int NUM_NEW_DIRS = 4;
        for (int i = 0; i < NUM_NEW_DIRS; i++) {
            File newVolume = new File(dataDir, ("new_vol" + i));
            newDirs.add(newVolume.toString());
            if ((i % 2) == 0) {
                // Make addVolume() fail.
                newVolume.createNewFile();
            }
        }
        String newValue = ((dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)) + ",") + (Joiner.on(",").join(newDirs));
        try {
            dn.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, newValue);
            Assert.fail("Expect to throw IOException.");
        } catch (ReconfigurationException e) {
            String errorMessage = e.getCause().getMessage();
            String[] messages = errorMessage.split("\\r?\\n");
            Assert.assertEquals(2, messages.length);
            Assert.assertThat(messages[0], CoreMatchers.containsString("new_vol0"));
            Assert.assertThat(messages[1], CoreMatchers.containsString("new_vol2"));
        }
        // Make sure that vol0 and vol2's metadata are not left in memory.
        FsDatasetSpi<?> dataset = dn.getFSDataset();
        try (FsDatasetSpi.FsVolumeReferences volumes = dataset.getFsVolumeReferences()) {
            for (FsVolumeSpi volume : volumes) {
                Assert.assertThat(new File(volume.getStorageLocation().getUri()).toString(), Is.is(CoreMatchers.not(CoreMatchers.anyOf(Is.is(newDirs.get(0)), Is.is(newDirs.get(2))))));
            }
        }
        DataStorage storage = dn.getStorage();
        for (int i = 0; i < (storage.getNumStorageDirs()); i++) {
            Storage.StorageDirectory sd = storage.getStorageDir(i);
            Assert.assertThat(sd.getRoot().toString(), Is.is(CoreMatchers.not(CoreMatchers.anyOf(Is.is(newDirs.get(0)), Is.is(newDirs.get(2))))));
        }
        // The newly effective conf does not have vol0 and vol2.
        String[] effectiveVolumes = dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY).split(",");
        Assert.assertEquals(4, effectiveVolumes.length);
        for (String ev : effectiveVolumes) {
            Assert.assertThat(new File(StorageLocation.parse(ev).getUri()).getCanonicalPath(), Is.is(CoreMatchers.not(CoreMatchers.anyOf(Is.is(newDirs.get(0)), Is.is(newDirs.get(2))))));
        }
    }

    @Test(timeout = 600000)
    public void testRemoveVolumeBeingWritten() throws IOException, InterruptedException, BrokenBarrierException, TimeoutException, ReconfigurationException {
        // test against removing volumes on the different DataNode on the pipeline.
        for (int i = 0; i < 3; i++) {
            testRemoveVolumeBeingWrittenForDatanode(i);
        }
    }

    @Test(timeout = 60000)
    public void testAddBackRemovedVolume() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        startDFSCluster(1, 2);
        // Create some data on every volume.
        createFile(new Path("/test"), 32);
        DataNode dn = cluster.getDataNodes().get(0);
        Configuration conf = dn.getConf();
        String oldDataDir = conf.get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY);
        String keepDataDir = oldDataDir.split(",")[0];
        String removeDataDir = oldDataDir.split(",")[1];
        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, keepDataDir), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
        for (int i = 0; i < (cluster.getNumNameNodes()); i++) {
            String bpid = cluster.getNamesystem(i).getBlockPoolId();
            BlockPoolSliceStorage bpsStorage = dn.getStorage().getBPStorage(bpid);
            // Make sure that there is no block pool level storage under removeDataDir.
            for (int j = 0; j < (bpsStorage.getNumStorageDirs()); j++) {
                Storage.StorageDirectory sd = bpsStorage.getStorageDir(j);
                Assert.assertFalse(sd.getRoot().getAbsolutePath().startsWith(new File(removeDataDir).getAbsolutePath()));
            }
            Assert.assertEquals(dn.getStorage().getBPStorage(bpid).getNumStorageDirs(), 1);
        }
        // Bring the removed directory back. It only successes if all metadata about
        // this directory were removed from the previous step.
        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, oldDataDir), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
    }

    /**
     * Verify that {@link DataNode#checkDiskError()} removes all metadata in
     * DataNode upon a volume failure. Thus we can run reconfig on the same
     * configuration to reload the new volume on the same directory as the failed one.
     */
    @Test(timeout = 60000)
    public void testDirectlyReloadAfterCheckDiskError() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        startDFSCluster(1, 2);
        createFile(new Path("/test"), 32, ((short) (2)));
        DataNode dn = cluster.getDataNodes().get(0);
        final String oldDataDir = dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY);
        File dirToFail = cluster.getInstanceStorageDir(0, 0);
        FsVolumeImpl failedVolume = DataNodeTestUtils.getVolume(dn, dirToFail);
        Assert.assertTrue(("No FsVolume was found for " + dirToFail), (failedVolume != null));
        long used = failedVolume.getDfsUsed();
        DataNodeTestUtils.injectDataDirFailure(dirToFail);
        // Call and wait DataNode to detect disk failure.
        DataNodeTestUtils.waitForDiskError(dn, failedVolume);
        createFile(new Path("/test1"), 32, ((short) (2)));
        Assert.assertEquals(used, failedVolume.getDfsUsed());
        DataNodeTestUtils.restoreDataDirFromFailure(dirToFail);
        TestDataNodeHotSwapVolumes.LOG.info("reconfiguring DN ");
        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, oldDataDir), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
        createFile(new Path("/test2"), 32, ((short) (2)));
        FsVolumeImpl restoredVolume = DataNodeTestUtils.getVolume(dn, dirToFail);
        Assert.assertTrue((restoredVolume != null));
        Assert.assertTrue((restoredVolume != failedVolume));
        // More data has been written to this volume.
        Assert.assertTrue(((restoredVolume.getDfsUsed()) > used));
    }

    /**
     * Test that a full block report is sent after hot swapping volumes
     */
    @Test(timeout = 100000)
    public void testFullBlockReportAfterRemovingVolumes() throws IOException, ReconfigurationException {
        Configuration conf = new Configuration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, TestDataNodeHotSwapVolumes.BLOCK_SIZE);
        // Similar to TestTriggerBlockReport, set a really long value for
        // dfs.heartbeat.interval, so that incremental block reports and heartbeats
        // won't be sent during this test unless they're triggered
        // manually.
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 10800000L);
        conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1080L);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        cluster.waitActive();
        final DataNode dn = cluster.getDataNodes().get(0);
        DatanodeProtocolClientSideTranslatorPB spy = InternalDataNodeTestUtils.spyOnBposToNN(dn, cluster.getNameNode());
        // Remove a data dir from datanode
        File dataDirToKeep = cluster.getInstanceStorageDir(0, 0);
        Assert.assertThat("DN did not update its own config", dn.reconfigurePropertyImpl(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, dataDirToKeep.toString()), Is.is(dn.getConf().get(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)));
        // We should get 1 full report
        Mockito.verify(spy, Mockito.timeout(60000).times(1)).blockReport(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageBlockReport[].class), ArgumentMatchers.any(BlockReportContext.class));
    }
}

