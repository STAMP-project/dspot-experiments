/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import CompactionManager.THREAD_NAME_PREFIX;
import HardwareState.AVAILABLE;
import TestUtils.RANDOM;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.ClusterParticipant;
import com.github.ambry.clustermap.DiskId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.MockDataNodeId;
import com.github.ambry.clustermap.MockPartitionId;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.clustermap.ReplicaStatusDelegate;
import com.github.ambry.config.DiskManagerConfig;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test {@link StorageManager} and {@link DiskManager}
 */
public class StorageManagerTest {
    private static final Random RANDOM = new Random();

    private DiskManagerConfig diskManagerConfig;

    private StoreConfig storeConfig;

    private MockClusterMap clusterMap;

    private MetricRegistry metricRegistry;

    /**
     * Test that stores on a disk without a valid mount path are not started.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void mountPathNotFoundTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<String> mountPaths = dataNode.getMountPaths();
        String mountPathToDelete = mountPaths.get(StorageManagerTest.RANDOM.nextInt(mountPaths.size()));
        int downReplicaCount = 0;
        for (ReplicaId replica : replicas) {
            if (replica.getMountPath().equals(mountPathToDelete)) {
                downReplicaCount++;
            }
        }
        Utils.deleteFileOrDirectory(new File(mountPathToDelete));
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        Map<String, Counter> counters = metricRegistry.getCounters();
        Assert.assertEquals("DiskSpaceAllocator should not have failed to start.", 0, getCounterValue(counters, DiskSpaceAllocator.class.getName(), "DiskSpaceAllocatorInitFailureCount"));
        Assert.assertEquals("Unexpected number of store start failures", downReplicaCount, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreStartFailures"));
        Assert.assertEquals("Expected 1 disk mount path failure", 1, getCounterValue(counters, DiskManager.class.getName(), "DiskMountPathFailures"));
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        checkStoreAccessibility(replicas, mountPathToDelete, storageManager);
        Assert.assertEquals("Compaction thread count is incorrect", ((mountPaths.size()) - 1), TestUtils.numThreadsByThisName(THREAD_NAME_PREFIX));
        StorageManagerTest.verifyCompactionThreadCount(storageManager, ((mountPaths.size()) - 1));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
        Assert.assertEquals("Compaction thread count is incorrect", 0, storageManager.getCompactionThreadCount());
    }

    /**
     * Tests that schedule compaction and control compaction in StorageManager
     *
     * @throws Exception
     * 		
     */
    @Test
    public void scheduleAndControlCompactionTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<MockDataNodeId> dataNodes = new ArrayList<>();
        dataNodes.add(dataNode);
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, dataNodes, 0);
        List<? extends ReplicaId> invalidPartitionReplicas = invalidPartition.getReplicaIds();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be 1 unexpected partition reported", 1, getNumUnrecognizedPartitionsReported());
        // add invalid replica id
        replicas.add(invalidPartitionReplicas.get(0));
        for (int i = 0; i < (replicas.size()); i++) {
            ReplicaId replica = replicas.get(i);
            PartitionId id = replica.getPartitionId();
            if (i == ((replicas.size()) - 1)) {
                Assert.assertFalse("Schedule compaction should fail", storageManager.scheduleNextForCompaction(id));
                Assert.assertFalse("Disable compaction should fail", storageManager.controlCompactionForBlobStore(id, false));
                Assert.assertFalse("Enable compaction should fail", storageManager.controlCompactionForBlobStore(id, true));
            } else {
                Assert.assertTrue("Enable compaction should succeed", storageManager.controlCompactionForBlobStore(id, true));
                Assert.assertTrue("Schedule compaction should succeed", storageManager.scheduleNextForCompaction(id));
            }
        }
        ReplicaId replica = replicas.get(0);
        PartitionId id = replica.getPartitionId();
        Assert.assertTrue("Disable compaction should succeed", storageManager.controlCompactionForBlobStore(id, false));
        Assert.assertFalse("Schedule compaction should fail", storageManager.scheduleNextForCompaction(id));
        Assert.assertTrue("Enable compaction should succeed", storageManager.controlCompactionForBlobStore(id, true));
        Assert.assertTrue("Schedule compaction should succeed", storageManager.scheduleNextForCompaction(id));
        replica = replicas.get(1);
        id = replica.getPartitionId();
        Assert.assertTrue("Schedule compaction should succeed", storageManager.scheduleNextForCompaction(id));
        replicas.remove(((replicas.size()) - 1));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Test start BlobStore with given {@link PartitionId}.
     */
    @Test
    public void startBlobStoreTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<MockDataNodeId> dataNodes = new ArrayList<>();
        dataNodes.add(dataNode);
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, dataNodes, 0);
        List<? extends ReplicaId> invalidPartitionReplicas = invalidPartition.getReplicaIds();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        PartitionId id = null;
        storageManager.start();
        Assert.assertEquals("There should be 1 unexpected partition reported", 1, getNumUnrecognizedPartitionsReported());
        // shutdown all the replicas first
        for (ReplicaId replica : replicas) {
            id = replica.getPartitionId();
            Assert.assertTrue("Shutdown should succeed on given store", storageManager.shutdownBlobStore(id));
        }
        ReplicaId replica = replicas.get(0);
        id = replica.getPartitionId();
        // test start a store successfully
        Assert.assertTrue("Start should succeed on given store", storageManager.startBlobStore(id));
        // test start the store which is already started
        Assert.assertTrue("Start should succeed on the store which is already started", storageManager.startBlobStore(id));
        // test invalid partition
        replica = invalidPartitionReplicas.get(0);
        id = replica.getPartitionId();
        Assert.assertFalse("Start should fail on given invalid replica", storageManager.startBlobStore(id));
        // test start the store whose DiskManager is not running
        replica = replicas.get(((replicas.size()) - 1));
        id = replica.getPartitionId();
        storageManager.getDiskManager(id).shutdown();
        Assert.assertFalse("Start should fail on given store whose DiskManager is not running", storageManager.startBlobStore(id));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Test get DiskManager with given {@link PartitionId}.
     */
    @Test
    public void getDiskManagerTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<MockDataNodeId> dataNodes = new ArrayList<>();
        dataNodes.add(dataNode);
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, dataNodes, 0);
        List<? extends ReplicaId> invalidPartitionReplicas = invalidPartition.getReplicaIds();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        PartitionId id = null;
        storageManager.start();
        Assert.assertEquals("There should be 1 unexpected partition reported", 1, getNumUnrecognizedPartitionsReported());
        for (ReplicaId replica : replicas) {
            id = replica.getPartitionId();
            Assert.assertNotNull("DiskManager should not be null for valid partition", storageManager.getDiskManager(id));
        }
        // test invalid partition
        ReplicaId replica = invalidPartitionReplicas.get(0);
        id = replica.getPartitionId();
        Assert.assertNull("DiskManager should be null for invalid partition", storageManager.getDiskManager(id));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Test shutdown blobstore with given {@link PartitionId}.
     */
    @Test
    public void shutdownBlobStoreTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<MockDataNodeId> dataNodes = new ArrayList<>();
        dataNodes.add(dataNode);
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, dataNodes, 0);
        List<? extends ReplicaId> invalidPartitionReplicas = invalidPartition.getReplicaIds();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be 1 unexpected partition reported", 1, getNumUnrecognizedPartitionsReported());
        for (int i = 1; i < ((replicas.size()) - 1); i++) {
            ReplicaId replica = replicas.get(i);
            PartitionId id = replica.getPartitionId();
            Assert.assertTrue("Shutdown should succeed on given store", storageManager.shutdownBlobStore(id));
        }
        // test shutdown the store which is not started
        ReplicaId replica = replicas.get(((replicas.size()) - 1));
        PartitionId id = replica.getPartitionId();
        Store store = storageManager.getStore(id);
        store.shutdown();
        Assert.assertTrue("Shutdown should succeed on the store which is not started", storageManager.shutdownBlobStore(id));
        // test shutdown the store whose DiskManager is not running
        replica = replicas.get(0);
        id = replica.getPartitionId();
        storageManager.getDiskManager(id).shutdown();
        Assert.assertFalse("Shutdown should fail on given store whose DiskManager is not running", storageManager.shutdownBlobStore(id));
        // test invalid partition
        replica = invalidPartitionReplicas.get(0);
        id = replica.getPartitionId();
        Assert.assertFalse("Shutdown should fail on given invalid replica", storageManager.shutdownBlobStore(id));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Test set stopped state of blobstore with given list of {@link PartitionId} in failure cases.
     */
    @Test
    public void setBlobStoreStoppedStateFailureTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<MockDataNodeId> dataNodes = new ArrayList<>();
        dataNodes.add(dataNode);
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, dataNodes, 0);
        List<? extends ReplicaId> invalidPartitionReplicas = invalidPartition.getReplicaIds();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be 1 unexpected partition reported", 1, getNumUnrecognizedPartitionsReported());
        // test set the state of store whose replicaStatusDelegate is null
        ReplicaId replica = replicas.get(0);
        PartitionId id = replica.getPartitionId();
        storageManager.getDiskManager(id).shutdown();
        List<PartitionId> failToUpdateList = storageManager.setBlobStoreStoppedState(Arrays.asList(id), true);
        Assert.assertEquals("Set store stopped state should fail on given store whose replicaStatusDelegate is null", id, failToUpdateList.get(0));
        // test invalid partition case (where diskManager == null)
        replica = invalidPartitionReplicas.get(0);
        id = replica.getPartitionId();
        failToUpdateList = storageManager.setBlobStoreStoppedState(Arrays.asList(id), true);
        Assert.assertEquals("Set store stopped state should fail on given invalid replica", id, failToUpdateList.get(0));
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Test successfully set stopped state of blobstore with given list of {@link PartitionId}.
     */
    @Test
    public void setBlobStoreStoppedStateSuccessTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<PartitionId> partitionIds = new ArrayList<>();
        Map<DiskId, List<ReplicaId>> diskToReplicas = new HashMap<>();
        // test set the state of store with instantiated replicaStatusDelegate
        ReplicaStatusDelegate replicaStatusDelegate = new StorageManagerTest.MockReplicaStatusDelegate();
        ReplicaStatusDelegate replicaStatusDelegateSpy = Mockito.spy(replicaStatusDelegate);
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, replicaStatusDelegateSpy);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        for (ReplicaId replica : replicas) {
            partitionIds.add(replica.getPartitionId());
            diskToReplicas.computeIfAbsent(replica.getDiskId(), ( disk) -> new ArrayList<>()).add(replica);
        }
        List<PartitionId> failToUpdateList;
        // add a list of stores to STOPPED list. Note that the stores are residing on 3 disks.
        failToUpdateList = storageManager.setBlobStoreStoppedState(partitionIds, true);
        // make sure the update operation succeeds
        Assert.assertTrue("Add stores to stopped list should succeed, failToUpdateList should be empty", failToUpdateList.isEmpty());
        // make sure the stopped list contains all the added stores
        Set<String> stoppedReplicasCopy = new HashSet(replicaStatusDelegateSpy.getStoppedReplicas());
        for (ReplicaId replica : replicas) {
            Assert.assertTrue(("The stopped list should contain the replica: " + (replica.getPartitionId().toPathString())), stoppedReplicasCopy.contains(replica.getPartitionId().toPathString()));
        }
        // make sure replicaStatusDelegate is invoked 3 times and each time the input replica list conforms with stores on particular disk
        for (List<ReplicaId> replicasPerDisk : diskToReplicas.values()) {
            Mockito.verify(replicaStatusDelegateSpy, Mockito.times(1)).markStopped(replicasPerDisk);
        }
        // remove a list of stores from STOPPED list. Note that the stores are residing on 3 disks.
        storageManager.setBlobStoreStoppedState(partitionIds, false);
        // make sure the update operation succeeds
        Assert.assertTrue("Remove stores from stopped list should succeed, failToUpdateList should be empty", failToUpdateList.isEmpty());
        // make sure the stopped list is empty because all the stores are successfully removed.
        Assert.assertTrue("The stopped list should be empty after removing all stores", replicaStatusDelegateSpy.getStoppedReplicas().isEmpty());
        // make sure replicaStatusDelegate is invoked 3 times and each time the input replica list conforms with stores on particular disk
        for (List<ReplicaId> replicasPerDisk : diskToReplicas.values()) {
            Mockito.verify(replicaStatusDelegateSpy, Mockito.times(1)).unmarkStopped(replicasPerDisk);
        }
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Tests that{@link StorageManager} can correctly determine if disk is unavailable based on states of all stores.
     */
    @Test
    public void isDiskAvailableTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        Map<DiskId, List<ReplicaId>> diskToReplicas = new HashMap<>();
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        for (ReplicaId replica : replicas) {
            diskToReplicas.computeIfAbsent(replica.getDiskId(), ( disk) -> new ArrayList<>()).add(replica);
        }
        // for each disk, shutdown all the stores except for the last one
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            for (int i = 0; i < ((replicasOnDisk.size()) - 1); ++i) {
                storageManager.getStore(replicasOnDisk.get(i).getPartitionId()).shutdown();
            }
        }
        // verify all disks are still available because at least one store on them is up
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            Assert.assertTrue("Disk should be available", storageManager.isDiskAvailable(replicasOnDisk.get(0).getDiskId()));
            Assert.assertEquals("Disk state be available", AVAILABLE, replicasOnDisk.get(0).getDiskId().getState());
        }
        // now, shutdown the last store on each disk
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            storageManager.getStore(replicasOnDisk.get(((replicasOnDisk.size()) - 1)).getPartitionId()).shutdown();
        }
        // verify all disks are unavailable because all stores are down
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            Assert.assertFalse("Disk should be unavailable", storageManager.isDiskAvailable(replicasOnDisk.get(0).getDiskId()));
        }
        // then, start the one store on each disk to test if disk is up again
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            storageManager.startBlobStore(replicasOnDisk.get(0).getPartitionId());
        }
        // verify all disks are available again because one store is started
        for (List<ReplicaId> replicasOnDisk : diskToReplicas.values()) {
            Assert.assertTrue("Disk should be available", storageManager.isDiskAvailable(replicasOnDisk.get(0).getDiskId()));
            Assert.assertEquals("Disk state be available", AVAILABLE, replicasOnDisk.get(0).getDiskId().getState());
        }
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Tests that {@link StorageManager} can start even when certain stores cannot be started. Checks that these stores
     * are not accessible. We can make the replica path non-readable to induce a store starting failure.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void storeStartFailureTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        Set<Integer> badReplicaIndexes = new HashSet<>(Arrays.asList(2, 7));
        for (Integer badReplicaIndex : badReplicaIndexes) {
            new File(replicas.get(badReplicaIndex).getReplicaPath()).setReadable(false);
        }
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        Map<String, Counter> counters = metricRegistry.getCounters();
        Assert.assertEquals(0, getCounterValue(counters, DiskSpaceAllocator.class.getName(), "DiskSpaceAllocatorInitFailureCount"));
        Assert.assertEquals(badReplicaIndexes.size(), getCounterValue(counters, DiskManager.class.getName(), "TotalStoreStartFailures"));
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "DiskMountPathFailures"));
        for (int i = 0; i < (replicas.size()); i++) {
            ReplicaId replica = replicas.get(i);
            PartitionId id = replica.getPartitionId();
            if (badReplicaIndexes.contains(i)) {
                Assert.assertNull("This store should not be accessible.", storageManager.getStore(id));
                Assert.assertFalse("Compaction should not be scheduled", storageManager.scheduleNextForCompaction(id));
            } else {
                Store store = storageManager.getStore(id);
                Assert.assertTrue("Store should be started", isStarted());
                Assert.assertTrue("Compaction should be scheduled", storageManager.scheduleNextForCompaction(id));
            }
        }
        Assert.assertEquals("Compaction thread count is incorrect", dataNode.getMountPaths().size(), TestUtils.numThreadsByThisName(THREAD_NAME_PREFIX));
        StorageManagerTest.verifyCompactionThreadCount(storageManager, dataNode.getMountPaths().size());
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
        Assert.assertEquals("Compaction thread count is incorrect", 0, storageManager.getCompactionThreadCount());
    }

    /**
     * Tests that {@link StorageManager} can start when all of the stores on one disk fail to start. Checks that these
     * stores are not accessible. We can make the replica path non-readable to induce a store starting failure.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void storeStartFailureOnOneDiskTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<String> mountPaths = dataNode.getMountPaths();
        String badDiskMountPath = mountPaths.get(StorageManagerTest.RANDOM.nextInt(mountPaths.size()));
        int downReplicaCount = 0;
        for (ReplicaId replica : replicas) {
            if (replica.getMountPath().equals(badDiskMountPath)) {
                new File(replica.getReplicaPath()).setReadable(false);
                downReplicaCount++;
            }
        }
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        Map<String, Counter> counters = metricRegistry.getCounters();
        Assert.assertEquals(0, getCounterValue(counters, DiskSpaceAllocator.class.getName(), "DiskSpaceAllocatorInitFailureCount"));
        Assert.assertEquals(downReplicaCount, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreStartFailures"));
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "DiskMountPathFailures"));
        checkStoreAccessibility(replicas, badDiskMountPath, storageManager);
        Assert.assertEquals("Compaction thread count is incorrect", mountPaths.size(), TestUtils.numThreadsByThisName(THREAD_NAME_PREFIX));
        StorageManagerTest.verifyCompactionThreadCount(storageManager, mountPaths.size());
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
        Assert.assertEquals("Compaction thread count is incorrect", 0, storageManager.getCompactionThreadCount());
    }

    /**
     * Test that stores on a disk are inaccessible if the {@link DiskSpaceAllocator} fails to start.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void diskSpaceAllocatorTest() throws Exception {
        generateConfigs(true);
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        List<String> mountPaths = dataNode.getMountPaths();
        // There should be 1 unallocated segment per replica on a mount path (each replica can have 2 segments) and the
        // swap segments.
        int expectedSegmentsInPool = ((replicas.size()) / (mountPaths.size())) + (diskManagerConfig.diskManagerRequiredSwapSegmentsPerSize);
        // Test that StorageManager starts correctly when segments are created in the reserve pool.
        // Startup/shutdown one more time to verify the restart scenario.
        for (int i = 0; i < 2; i++) {
            metricRegistry = new MetricRegistry();
            StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
            storageManager.start();
            Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
            checkStoreAccessibility(replicas, null, storageManager);
            Map<String, Counter> counters = metricRegistry.getCounters();
            Assert.assertEquals(0, getCounterValue(counters, DiskSpaceAllocator.class.getName(), "DiskSpaceAllocatorInitFailureCount"));
            Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreStartFailures"));
            Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "DiskMountPathFailures"));
            for (String mountPath : dataNode.getMountPaths()) {
                DiskSpaceAllocatorTest.verifyPoolState(new File(mountPath, diskManagerConfig.diskManagerReserveFileDirName), new DiskSpaceAllocatorTest.ExpectedState().add(storeConfig.storeSegmentSizeInBytes, expectedSegmentsInPool));
            }
            StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
            Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreShutdownFailures"));
        }
        // Induce a initializePool failure by:
        // 1. deleting a file size directory
        // 2. instantiating the DiskManagers (this will not fail b/c the directory just won't be inventory)
        // 3. creating a regular file with the same name as the file size directory
        // 4. start the DiskManagers (this should cause the DiskSpaceAllocator to fail to initialize when it sees the
        // file where the directory should be created.
        metricRegistry = new MetricRegistry();
        String diskToFail = mountPaths.get(StorageManagerTest.RANDOM.nextInt(mountPaths.size()));
        File reservePoolDir = new File(diskToFail, diskManagerConfig.diskManagerReserveFileDirName);
        File fileSizeDir = new File(reservePoolDir, DiskSpaceAllocator.generateFileSizeDirName(storeConfig.storeSegmentSizeInBytes));
        Utils.deleteFileOrDirectory(fileSizeDir);
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        Assert.assertTrue("File creation should have succeeded", fileSizeDir.createNewFile());
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        checkStoreAccessibility(replicas, diskToFail, storageManager);
        Map<String, Counter> counters = metricRegistry.getCounters();
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreShutdownFailures"));
    }

    /**
     * Test that stores for all partitions on a node have been started and partitions not present on this node are
     * inaccessible. Also tests all stores are shutdown on shutting down the storage manager
     *
     * @throws Exception
     * 		
     */
    @Test
    public void successfulStartupShutdownTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        checkStoreAccessibility(replicas, null, storageManager);
        Map<String, Counter> counters = metricRegistry.getCounters();
        Assert.assertEquals(0, getCounterValue(counters, DiskSpaceAllocator.class.getName(), "DiskSpaceAllocatorInitFailureCount"));
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreStartFailures"));
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "DiskMountPathFailures"));
        MockPartitionId invalidPartition = new MockPartitionId(Long.MAX_VALUE, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.<MockDataNodeId>emptyList(), 0);
        Assert.assertNull("Should not have found a store for an invalid partition.", storageManager.getStore(invalidPartition));
        Assert.assertEquals("Compaction thread count is incorrect", dataNode.getMountPaths().size(), TestUtils.numThreadsByThisName(THREAD_NAME_PREFIX));
        StorageManagerTest.verifyCompactionThreadCount(storageManager, dataNode.getMountPaths().size());
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
        Assert.assertEquals("Compaction thread count is incorrect", 0, storageManager.getCompactionThreadCount());
        Assert.assertEquals(0, getCounterValue(counters, DiskManager.class.getName(), "TotalStoreShutdownFailures"));
    }

    /**
     * Test the stopped stores are correctly skipped and not started during StorageManager's startup.
     */
    @Test
    public void skipStoppedStoresTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        StorageManagerTest.MockReplicaStatusDelegate replicaStatusDelegate = new StorageManagerTest.MockReplicaStatusDelegate();
        replicaStatusDelegate.stoppedReplicas.add(replicas.get(0).getPartitionId().toPathString());
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, replicaStatusDelegate);
        storageManager.start();
        Assert.assertEquals("There should be no unexpected partitions reported", 0, getNumUnrecognizedPartitionsReported());
        for (int i = 0; i < (replicas.size()); ++i) {
            PartitionId id = replicas.get(i).getPartitionId();
            if (i == 0) {
                Assert.assertNull("Store should be null because stopped stores will be skipped and will not be started", storageManager.getStore(id));
                Assert.assertFalse("Compaction should not be scheduled", storageManager.scheduleNextForCompaction(id));
            } else {
                Store store = storageManager.getStore(id);
                Assert.assertTrue("Store should be started", isStarted());
                Assert.assertTrue("Compaction should be scheduled", storageManager.scheduleNextForCompaction(id));
            }
        }
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * Tests that unrecognized directories are reported correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void unrecognizedDirsOnDiskTest() throws Exception {
        MockDataNodeId dataNode = clusterMap.getDataNodes().get(0);
        List<ReplicaId> replicas = clusterMap.getReplicaIds(dataNode);
        int extraDirsCount = 0;
        Set<String> createdMountPaths = new HashSet<>();
        for (ReplicaId replicaId : replicas) {
            if (createdMountPaths.add(replicaId.getMountPath())) {
                int count = (TestUtils.RANDOM.nextInt(6)) + 5;
                createFilesAndDirsAtPath(new File(replicaId.getDiskId().getMountPath()), (count - 1), count);
                // the extra files should not get reported
                extraDirsCount += count;
            }
        }
        StorageManager storageManager = createStorageManager(replicas, metricRegistry, null);
        storageManager.start();
        Assert.assertEquals("There should be some unexpected partitions reported", extraDirsCount, getNumUnrecognizedPartitionsReported());
        checkStoreAccessibility(replicas, null, storageManager);
        StorageManagerTest.shutdownAndAssertStoresInaccessible(storageManager, replicas);
    }

    /**
     * An extension of {@link ReplicaStatusDelegate} to help with tests.
     */
    private static class MockReplicaStatusDelegate extends ReplicaStatusDelegate {
        Set<String> stoppedReplicas = new HashSet<>();

        MockReplicaStatusDelegate() {
            super(Mockito.mock(ClusterParticipant.class));
        }

        @Override
        public boolean markStopped(List<ReplicaId> replicaIds) {
            replicaIds.forEach(( replicaId) -> stoppedReplicas.add(replicaId.getPartitionId().toPathString()));
            return true;
        }

        @Override
        public boolean unmarkStopped(List<ReplicaId> replicaIds) {
            replicaIds.forEach(( replicaId) -> stoppedReplicas.remove(replicaId.getPartitionId().toPathString()));
            return true;
        }

        @Override
        public List<String> getStoppedReplicas() {
            return new ArrayList<>(stoppedReplicas);
        }
    }
}

