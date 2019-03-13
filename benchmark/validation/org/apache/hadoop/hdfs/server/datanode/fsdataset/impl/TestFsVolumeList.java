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


import DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY;
import DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY;
import DFSConfigKeys.DFS_DATANODE_VOLUMES_REPLICA_ADD_THREADPOOL_SIZE_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import ReservedSpaceCalculator.ReservedSpaceCalculatorPercentage;
import StorageType.DEFAULT;
import StorageType.RAM_DISK;
import StorageType.SSD;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DF;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.BlockScanner;
import org.apache.hadoop.hdfs.server.datanode.StorageLocation;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeReference;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.VolumeChoosingPolicy;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.AutoCloseableLock;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestFsVolumeList {
    private Configuration conf;

    private VolumeChoosingPolicy<FsVolumeImpl> blockChooser = new org.apache.hadoop.hdfs.server.datanode.fsdataset.RoundRobinVolumeChoosingPolicy();

    private FsDatasetImpl dataset = null;

    private String baseDir;

    private BlockScanner blockScanner;

    @Test(timeout = 30000)
    public void testGetNextVolumeWithClosedVolume() throws IOException {
        FsVolumeList volumeList = new FsVolumeList(Collections.<VolumeFailureInfo>emptyList(), blockScanner, blockChooser);
        final List<FsVolumeImpl> volumes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            File curDir = new File(baseDir, ("nextvolume-" + i));
            curDir.mkdirs();
            FsVolumeImpl volume = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(curDir.getPath()))).build();
            volume.setCapacityForTesting(((1024 * 1024) * 1024));
            volumes.add(volume);
            volumeList.addVolume(volume.obtainReference());
        }
        // Close the second volume.
        volumes.get(1).setClosed();
        try {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return volumes.get(1).checkClosed();
                }
            }, 100, 3000);
        } catch (TimeoutException e) {
            Assert.fail("timed out while waiting for volume to be removed.");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        for (int i = 0; i < 10; i++) {
            try (FsVolumeReference ref = volumeList.getNextVolume(DEFAULT, null, 128)) {
                // volume No.2 will not be chosen.
                Assert.assertNotEquals(ref.getVolume(), volumes.get(1));
            }
        }
    }

    @Test(timeout = 30000)
    public void testReleaseVolumeRefIfNoBlockScanner() throws IOException {
        FsVolumeList volumeList = new FsVolumeList(Collections.<VolumeFailureInfo>emptyList(), null, blockChooser);
        File volDir = new File(baseDir, "volume-0");
        volDir.mkdirs();
        FsVolumeImpl volume = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(volDir.getPath()))).build();
        FsVolumeReference ref = volume.obtainReference();
        volumeList.addVolume(ref);
        Assert.assertNull(ref.getVolume());
    }

    @Test
    public void testDfsReservedForDifferentStorageTypes() throws IOException {
        Configuration conf = new Configuration();
        conf.setLong(DFS_DATANODE_DU_RESERVED_KEY, 100L);
        File volDir = new File(baseDir, "volume-0");
        volDir.mkdirs();
        // when storage type reserved is not configured,should consider
        // dfs.datanode.du.reserved.
        FsVolumeImpl volume = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[RAM_DISK]" + (volDir.getPath()))))).setStorageID("storage-id").setConf(conf).build();
        Assert.assertEquals("", 100L, volume.getReserved());
        // when storage type reserved is configured.
        conf.setLong((((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".") + (StringUtils.toLowerCase(RAM_DISK.toString()))), 1L);
        conf.setLong((((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".") + (StringUtils.toLowerCase(SSD.toString()))), 2L);
        FsVolumeImpl volume1 = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[RAM_DISK]" + (volDir.getPath()))))).setStorageID("storage-id").setConf(conf).build();
        Assert.assertEquals("", 1L, volume1.getReserved());
        FsVolumeImpl volume2 = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[SSD]" + (volDir.getPath()))))).setStorageID("storage-id").setConf(conf).build();
        Assert.assertEquals("", 2L, volume2.getReserved());
        FsVolumeImpl volume3 = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[DISK]" + (volDir.getPath()))))).setStorageID("storage-id").setConf(conf).build();
        Assert.assertEquals("", 100L, volume3.getReserved());
        FsVolumeImpl volume4 = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(volDir.getPath()))).setStorageID("storage-id").setConf(conf).build();
        Assert.assertEquals("", 100L, volume4.getReserved());
    }

    @Test
    public void testNonDfsUsedMetricForVolume() throws Exception {
        File volDir = new File(baseDir, "volume-0");
        volDir.mkdirs();
        /* Lets have the example.
        Capacity - 1000
        Reserved - 100
        DfsUsed  - 200
        Actual Non-DfsUsed - 300 -->(expected)
        ReservedForReplicas - 50
         */
        long diskCapacity = 1000L;
        long duReserved = 100L;
        long dfsUsage = 200L;
        long actualNonDfsUsage = 300L;
        long reservedForReplicas = 50L;
        conf.setLong(DFS_DATANODE_DU_RESERVED_KEY, duReserved);
        FsVolumeImpl volume = new FsVolumeImplBuilder().setDataset(dataset).setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(volDir.getPath()))).setStorageID("storage-id").setConf(conf).build();
        FsVolumeImpl spyVolume = Mockito.spy(volume);
        // Set Capacity for testing
        long testCapacity = diskCapacity - duReserved;
        spyVolume.setCapacityForTesting(testCapacity);
        // Mock volume.getDfAvailable()
        long dfAvailable = (diskCapacity - dfsUsage) - actualNonDfsUsage;
        Mockito.doReturn(dfAvailable).when(spyVolume).getDfAvailable();
        // Mock dfsUsage
        Mockito.doReturn(dfsUsage).when(spyVolume).getDfsUsed();
        // Mock reservedForReplcas
        Mockito.doReturn(reservedForReplicas).when(spyVolume).getReservedForReplicas();
        Mockito.doReturn(actualNonDfsUsage).when(spyVolume).getActualNonDfsUsed();
        long expectedNonDfsUsage = actualNonDfsUsage - duReserved;
        Assert.assertEquals(expectedNonDfsUsage, spyVolume.getNonDfsUsed());
    }

    @Test
    public void testDfsReservedPercentageForDifferentStorageTypes() throws IOException {
        conf.setClass(DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculatorPercentage.class, ReservedSpaceCalculator.class);
        conf.setLong(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY, 15);
        File volDir = new File(baseDir, "volume-0");
        volDir.mkdirs();
        DF usage = Mockito.mock(DF.class);
        Mockito.when(usage.getCapacity()).thenReturn(4000L);
        Mockito.when(usage.getAvailable()).thenReturn(1000L);
        // when storage type reserved is not configured, should consider
        // dfs.datanode.du.reserved.pct
        FsVolumeImpl volume = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[RAM_DISK]" + (volDir.getPath()))))).setUsage(usage).build();
        Assert.assertEquals(600, volume.getReserved());
        Assert.assertEquals(3400, volume.getCapacity());
        Assert.assertEquals(400, volume.getAvailable());
        // when storage type reserved is configured.
        conf.setLong((((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".") + (StringUtils.toLowerCase(RAM_DISK.toString()))), 10);
        conf.setLong((((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".") + (StringUtils.toLowerCase(SSD.toString()))), 50);
        FsVolumeImpl volume1 = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[RAM_DISK]" + (volDir.getPath()))))).setUsage(usage).build();
        Assert.assertEquals(400, volume1.getReserved());
        Assert.assertEquals(3600, volume1.getCapacity());
        Assert.assertEquals(600, volume1.getAvailable());
        FsVolumeImpl volume2 = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[SSD]" + (volDir.getPath()))))).setUsage(usage).build();
        Assert.assertEquals(2000, volume2.getReserved());
        Assert.assertEquals(2000, volume2.getCapacity());
        Assert.assertEquals(0, volume2.getAvailable());
        FsVolumeImpl volume3 = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(("[DISK]" + (volDir.getPath()))))).setUsage(usage).build();
        Assert.assertEquals(600, volume3.getReserved());
        FsVolumeImpl volume4 = new FsVolumeImplBuilder().setConf(conf).setDataset(dataset).setStorageID("storage-id").setStorageDirectory(new org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory(StorageLocation.parse(volDir.getPath()))).setUsage(usage).build();
        Assert.assertEquals(600, volume4.getReserved());
    }

    @Test(timeout = 60000)
    public void testAddRplicaProcessorForAddingReplicaInMap() throws Exception {
        Configuration cnf = new Configuration();
        int poolSize = 5;
        cnf.setInt(DFS_REPLICATION_KEY, 1);
        cnf.setInt(DFS_DATANODE_VOLUMES_REPLICA_ADD_THREADPOOL_SIZE_KEY, poolSize);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(cnf).numDataNodes(1).storagesPerDatanode(1).build();
        DistributedFileSystem fs = cluster.getFileSystem();
        // Generate data blocks.
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<?>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            DFSTestUtil.createFile(fs, new Path((("File_" + (getName())) + j)), 10, ((short) (1)), 0);
                        } catch (IllegalArgumentException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.setName(("FileWriter" + i));
            futureList.add(pool.submit(thread));
        }
        // Wait for data generation
        for (Future<?> f : futureList) {
            f.get();
        }
        fs.close();
        FsDatasetImpl fsDataset = ((FsDatasetImpl) (cluster.getDataNodes().get(0).getFSDataset()));
        ReplicaMap volumeMap = new ReplicaMap(new AutoCloseableLock());
        RamDiskReplicaTracker ramDiskReplicaMap = RamDiskReplicaTracker.getInstance(conf, fsDataset);
        FsVolumeImpl vol = ((FsVolumeImpl) (fsDataset.getFsVolumeReferences().get(0)));
        String bpid = cluster.getNamesystem().getBlockPoolId();
        // It will create BlockPoolSlice.AddReplicaProcessor task's and lunch in
        // ForkJoinPool recursively
        vol.getVolumeMap(bpid, volumeMap, ramDiskReplicaMap);
        Assert.assertTrue("Failed to add all the replica to map", ((volumeMap.replicas(bpid).size()) == 1000));
        Assert.assertTrue(("Fork pool size should be " + poolSize), ((BlockPoolSlice.getAddReplicaForkPoolSize()) == poolSize));
    }
}

