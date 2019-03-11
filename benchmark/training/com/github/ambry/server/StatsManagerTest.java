/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.server;


import Map.Entry;
import StatsHeader.StatsDescription;
import StatsReportType.ACCOUNT_REPORT;
import StatsReportType.PARTITION_CLASS_REPORT;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.DataNodeId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.MockDataNodeId;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.clustermap.ReplicaId;
import com.github.ambry.config.StatsManagerConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.network.PortType;
import com.github.ambry.store.FindInfo;
import com.github.ambry.store.FindToken;
import com.github.ambry.store.MessageWriteSet;
import com.github.ambry.store.StorageManager;
import com.github.ambry.store.Store;
import com.github.ambry.store.StoreErrorCodes;
import com.github.ambry.store.StoreException;
import com.github.ambry.store.StoreGetOptions;
import com.github.ambry.store.StoreInfo;
import com.github.ambry.store.StoreKey;
import com.github.ambry.store.StoreStats;
import com.github.ambry.store.TimeRange;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StatsManager}.
 */
public class StatsManagerTest {
    private static final int MAX_ACCOUNT_COUNT = 10;

    private static final int MIN_ACCOUNT_COUNT = 5;

    private static final int MAX_CONTAINER_COUNT = 6;

    private static final int MIN_CONTAINER_COUNT = 3;

    private final StatsManager statsManager;

    private final String outputFileString;

    private final File tempDir;

    private final StatsSnapshot preAggregatedSnapshot;

    private final Map<PartitionId, Store> storeMap;

    private final Map<PartitionId, StatsSnapshot> partitionToSnapshot;

    private final List<ReplicaId> replicas;

    private final Random random = new Random();

    private final ObjectMapper mapper = new ObjectMapper();

    private final StatsManagerConfig config;

    public StatsManagerTest() throws StoreException, IOException {
        tempDir = Files.createTempDirectory(("nodeStatsDir-" + (UtilsTest.getRandomString(10)))).toFile();
        tempDir.deleteOnExit();
        outputFileString = new File(tempDir.getAbsolutePath(), "stats_output.json").getAbsolutePath();
        storeMap = new HashMap();
        partitionToSnapshot = new HashMap();
        preAggregatedSnapshot = generateRandomSnapshot().get(ACCOUNT_REPORT);
        Pair<StatsSnapshot, StatsSnapshot> baseSliceAndNewSlice = new Pair(preAggregatedSnapshot, null);
        replicas = new ArrayList();
        PartitionId partitionId;
        DataNodeId dataNodeId;
        for (int i = 0; i < 2; i++) {
            dataNodeId = new MockDataNodeId(Collections.singletonList(new com.github.ambry.network.Port(6667, PortType.PLAINTEXT)), Collections.singletonList("/tmp"), "DC1");
            partitionId = new com.github.ambry.clustermap.MockPartitionId(i, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
            baseSliceAndNewSlice = decomposeSnapshot(baseSliceAndNewSlice.getFirst());
            Map<StatsReportType, StatsSnapshot> snapshotsByType = new HashMap<>();
            snapshotsByType.put(ACCOUNT_REPORT, baseSliceAndNewSlice.getSecond());
            StoreStats storeStats = new StatsManagerTest.MockStoreStats(snapshotsByType, false);
            storeMap.put(partitionId, new StatsManagerTest.MockStore(storeStats));
            partitionToSnapshot.put(partitionId, snapshotsByType.get(ACCOUNT_REPORT));
            replicas.add(partitionId.getReplicaIds().get(0));
        }
        Map<StatsReportType, StatsSnapshot> snapshotsByType = new HashMap<>();
        snapshotsByType.put(ACCOUNT_REPORT, baseSliceAndNewSlice.getFirst());
        partitionId = new com.github.ambry.clustermap.MockPartitionId(2, MockClusterMap.DEFAULT_PARTITION_CLASS);
        storeMap.put(partitionId, new StatsManagerTest.MockStore(new StatsManagerTest.MockStoreStats(snapshotsByType, false)));
        partitionToSnapshot.put(partitionId, snapshotsByType.get(ACCOUNT_REPORT));
        StorageManager storageManager = new StatsManagerTest.MockStorageManager(storeMap);
        Properties properties = new Properties();
        properties.put("stats.output.file.path", outputFileString);
        config = new StatsManagerConfig(new VerifiableProperties(properties));
        statsManager = new StatsManager(storageManager, replicas, new MetricRegistry(), config, new MockTime());
    }

    /**
     * Test to verify that the {@link StatsManager} is collecting, aggregating and publishing correctly using randomly
     * generated data sets and mock {@link Store}s and {@link StorageManager}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testStatsManagerCollectAggregateAndPublish() throws IOException {
        StatsSnapshot actualSnapshot = new StatsSnapshot(0L, null);
        List<PartitionId> unreachablePartitions = Collections.emptyList();
        for (PartitionId partitionId : storeMap.keySet()) {
            statsManager.collectAndAggregate(actualSnapshot, partitionId, unreachablePartitions);
        }
        Assert.assertTrue("Actual aggregated StatsSnapshot does not match with expected snapshot", preAggregatedSnapshot.equals(actualSnapshot));
        List<String> unreachableStores = statsManager.examineUnreachablePartitions(unreachablePartitions);
        StatsHeader statsHeader = new StatsHeader(StatsDescription.QUOTA, SystemTime.getInstance().milliseconds(), storeMap.keySet().size(), storeMap.keySet().size(), unreachableStores);
        File outputFile = new File(outputFileString);
        if (outputFile.exists()) {
            outputFile.createNewFile();
        }
        long fileLengthBefore = outputFile.length();
        statsManager.publish(new StatsWrapper(statsHeader, actualSnapshot));
        Assert.assertTrue("Failed to publish stats to file", ((outputFile.length()) > fileLengthBefore));
    }

    /**
     * Test to verify the behavior when dealing with {@link Store} that is null and when {@link StoreException} is thrown.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void testStatsManagerWithProblematicStores() throws StoreException, IOException {
        DataNodeId dataNodeId = new MockDataNodeId(Collections.singletonList(new com.github.ambry.network.Port(6667, PortType.PLAINTEXT)), Collections.singletonList("/tmp"), "DC1");
        Map<PartitionId, Store> problematicStoreMap = new HashMap<>();
        PartitionId partitionId1 = new com.github.ambry.clustermap.MockPartitionId(1, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        PartitionId partitionId2 = new com.github.ambry.clustermap.MockPartitionId(2, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        problematicStoreMap.put(partitionId1, null);
        Map<StatsReportType, StatsSnapshot> snapshotsByType = new HashMap<>();
        snapshotsByType.put(ACCOUNT_REPORT, new StatsSnapshot(0L, null));
        Store exceptionStore = new StatsManagerTest.MockStore(new StatsManagerTest.MockStoreStats(snapshotsByType, true));
        problematicStoreMap.put(partitionId2, exceptionStore);
        StatsManager testStatsManager = new StatsManager(new StatsManagerTest.MockStorageManager(problematicStoreMap), Arrays.asList(partitionId1.getReplicaIds().get(0), partitionId2.getReplicaIds().get(0)), new MetricRegistry(), config, new MockTime());
        List<PartitionId> unreachablePartitions = new ArrayList<>();
        StatsSnapshot actualSnapshot = new StatsSnapshot(0L, null);
        for (PartitionId partitionId : problematicStoreMap.keySet()) {
            testStatsManager.collectAndAggregate(actualSnapshot, partitionId, unreachablePartitions);
        }
        Assert.assertEquals("Aggregated StatsSnapshot should not contain any value", 0L, actualSnapshot.getValue());
        Assert.assertEquals("Unreachable store count mismatch with expected value", 2, unreachablePartitions.size());
        String statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        StatsWrapper statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        List<String> unreachableStores = statsWrapper.getHeader().getUnreachableStores();
        Assert.assertTrue("The unreachable store list should contain Partition1 and Partition2", unreachableStores.containsAll(Arrays.asList(partitionId1.toPathString(), partitionId2.toPathString())));
        // test for the scenario where some stores are healthy and some are bad
        Map<PartitionId, Store> mixedStoreMap = new HashMap(storeMap);
        unreachablePartitions.clear();
        PartitionId partitionId3 = new com.github.ambry.clustermap.MockPartitionId(3, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        PartitionId partitionId4 = new com.github.ambry.clustermap.MockPartitionId(4, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        mixedStoreMap.put(partitionId3, null);
        mixedStoreMap.put(partitionId4, exceptionStore);
        testStatsManager = new StatsManager(new StatsManagerTest.MockStorageManager(mixedStoreMap), Arrays.asList(partitionId3.getReplicaIds().get(0), partitionId4.getReplicaIds().get(0)), new MetricRegistry(), config, new MockTime());
        actualSnapshot = new StatsSnapshot(0L, null);
        for (PartitionId partitionId : mixedStoreMap.keySet()) {
            testStatsManager.collectAndAggregate(actualSnapshot, partitionId, unreachablePartitions);
        }
        Assert.assertTrue("Actual aggregated StatsSnapshot does not match with expected snapshot", preAggregatedSnapshot.equals(actualSnapshot));
        Assert.assertEquals("Unreachable store count mismatch with expected value", 2, unreachablePartitions.size());
        // test fetchSnapshot method in StatsManager
        unreachablePartitions.clear();
        // partition 0, 1, 2 are healthy stores, partition 3, 4 are bad ones.
        for (PartitionId partitionId : mixedStoreMap.keySet()) {
            StatsSnapshot snapshot = testStatsManager.fetchSnapshot(partitionId, unreachablePartitions, ACCOUNT_REPORT);
            if ((Integer.valueOf(partitionId.toPathString())) < 3) {
                Assert.assertTrue("Actual StatsSnapshot does not match with expected snapshot", snapshot.equals(partitionToSnapshot.get(partitionId)));
            }
        }
        Assert.assertEquals("Unreachable store count mismatch with expected value", 2, unreachablePartitions.size());
    }

    /**
     * Test to verify the {@link StatsManager} behaves correctly when dynamically adding/removing {@link ReplicaId}.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testAddAndRemoveBlobStore() throws StoreException, IOException {
        // setup testing environment
        Map<PartitionId, Store> testStoreMap = new HashMap<>();
        List<ReplicaId> testReplicas = new ArrayList<>();
        DataNodeId dataNodeId = new MockDataNodeId(Collections.singletonList(new com.github.ambry.network.Port(6667, PortType.PLAINTEXT)), Collections.singletonList("/tmp"), "DC1");
        Map<StatsReportType, StatsSnapshot> snapshotsByType = new HashMap<>();
        snapshotsByType.put(ACCOUNT_REPORT, preAggregatedSnapshot);
        for (int i = 0; i < 3; i++) {
            PartitionId partitionId = new com.github.ambry.clustermap.MockPartitionId(i, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
            testStoreMap.put(partitionId, new StatsManagerTest.MockStore(new StatsManagerTest.MockStoreStats(snapshotsByType, false)));
            testReplicas.add(partitionId.getReplicaIds().get(0));
        }
        StorageManager mockStorageManager = new StatsManagerTest.MockStorageManager(testStoreMap);
        StatsManager testStatsManager = new StatsManager(mockStorageManager, testReplicas, new MetricRegistry(), config, new MockTime());
        // verify that adding an existing store to StatsManager should fail
        Assert.assertFalse("Adding a store which already exists should fail", testStatsManager.addReplica(testReplicas.get(0)));
        PartitionId partitionId3 = new com.github.ambry.clustermap.MockPartitionId(3, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        testStoreMap.put(partitionId3, new StatsManagerTest.MockStore(new StatsManagerTest.MockStoreStats(snapshotsByType, false)));
        // verify that partitionId3 is not in stats report before adding to statsManager
        String statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        StatsWrapper statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        Assert.assertFalse("Partition3 should not present in stats report", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId3.toPathString()));
        // verify that after adding into statsManager, PartitionId3 is in stats report
        testStatsManager.addReplica(partitionId3.getReplicaIds().get(0));
        statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        Assert.assertTrue("Partition3 should present in stats report", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId3.toPathString()));
        // verify that after removing PartitionId0 (corresponding to the first replica in replicas list), PartitionId0 is not in the stats report
        PartitionId partitionId0 = testReplicas.get(0).getPartitionId();
        Assert.assertTrue("Partition0 should present in stats report before removal", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId0.toPathString()));
        testStoreMap.remove(testReplicas.get(0).getPartitionId());
        testStatsManager.removeReplica(testReplicas.get(0));
        statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        Assert.assertFalse("Partition0 should not present in stats report after removal", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId0.toPathString()));
        // verify that removing the PartitionId0 should fail because it no longer exists in StatsManager
        Assert.assertFalse(testStatsManager.removeReplica(testReplicas.get(0)));
        // concurrent remove test
        CountDownLatch getStatsCountdown1 = new CountDownLatch(1);
        CountDownLatch waitRemoveCountdown = new CountDownLatch(1);
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).waitOperationCountdown = waitRemoveCountdown;
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).firstCall = true;
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).unreachablePartitions.clear();
        for (Store store : testStoreMap.values()) {
            ((StatsManagerTest.MockStore) (store)).getStatsCountdown = getStatsCountdown1;
            ((StatsManagerTest.MockStore) (store)).isCollected = false;
        }
        List<PartitionId> partitionRemoved = new ArrayList<>();
        Utils.newThread(() -> {
            // wait until at least one store has been collected (this ensures stats aggregation using old snapshot of map)
            try {
                getStatsCountdown1.await();
            } catch ( e) {
                throw new <e>IllegalStateException("CountDown await was interrupted");
            }
            // find one store which hasn't been collected
            ReplicaId replicaToRemove = null;
            for (Entry<PartitionId, Store> partitionToStore : testStoreMap.entrySet()) {
                com.github.ambry.server.MockStore store = ((com.github.ambry.server.MockStore) (partitionToStore.getValue()));
                if (!store.isCollected) {
                    replicaToRemove = partitionToStore.getKey().getReplicaIds().get(0);
                    break;
                }
            }
            if (replicaToRemove != null) {
                testStatsManager.removeReplica(replicaToRemove);
                testStoreMap.remove(replicaToRemove.getPartitionId());
                partitionRemoved.add(replicaToRemove.getPartitionId());
                // count down to allow stats aggregation to proceed
                waitRemoveCountdown.countDown();
            }
        }, false).start();
        statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        // verify that the removed store is indeed unreachable during stats aggregation
        Assert.assertTrue("The removed partition should be unreachable during aggregation", ((StatsManagerTest.MockStorageManager) (mockStorageManager)).unreachablePartitions.contains(partitionRemoved.get(0)));
        // verify unreachable store list doesn't contain the store which is removed.
        List<String> unreachableStores = statsWrapper.getHeader().getUnreachableStores();
        Assert.assertFalse("The removed partition should not present in unreachable list", unreachableStores.contains(partitionRemoved.get(0).toPathString()));
        // concurrent add test
        CountDownLatch getStatsCountdown2 = new CountDownLatch(1);
        CountDownLatch waitAddCountdown = new CountDownLatch(1);
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).waitOperationCountdown = waitAddCountdown;
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).firstCall = true;
        ((StatsManagerTest.MockStorageManager) (mockStorageManager)).unreachablePartitions.clear();
        for (Store store : testStoreMap.values()) {
            ((StatsManagerTest.MockStore) (store)).getStatsCountdown = getStatsCountdown2;
            ((StatsManagerTest.MockStore) (store)).isCollected = false;
        }
        PartitionId partitionId4 = new com.github.ambry.clustermap.MockPartitionId(4, MockClusterMap.DEFAULT_PARTITION_CLASS, Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
        Utils.newThread(() -> {
            // wait until at least one store has been collected (this ensures stats aggregation using old snapshot of map)
            try {
                getStatsCountdown2.await();
            } catch ( e) {
                throw new <e>IllegalStateException("CountDown await was interrupted");
            }
            testStatsManager.addReplica(partitionId4.getReplicaIds().get(0));
            testStoreMap.put(partitionId4, new com.github.ambry.server.MockStore(new com.github.ambry.server.MockStoreStats(snapshotsByType, false)));
            // count down to allow stats aggregation to proceed
            waitAddCountdown.countDown();
        }, false).start();
        statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        // verify that new added PartitionId4 is not in report for this round of aggregation
        Assert.assertFalse("Partition4 should not present in stats report", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId4.toPathString()));
        // verify that new added PartitionId4 will be collected for next round of aggregation
        statsJSON = testStatsManager.getNodeStatsInJSON(ACCOUNT_REPORT);
        statsWrapper = mapper.readValue(statsJSON, StatsWrapper.class);
        Assert.assertTrue("Partition4 should present in stats report", statsWrapper.getSnapshot().getSubMap().containsKey(partitionId4.toPathString()));
    }

    /**
     * Test that the {@link StatsManager} can correctly collect and aggregate all type of stats on the node. This
     * test is using randomly generated account snapshot and partitionClass snapshot in mock {@link StoreStats}.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testGetNodeStatsInJSON() throws StoreException, IOException {
        // initialize StatsManager and create all types of snapshots for testing
        List<ReplicaId> replicaIds = new ArrayList<>();
        PartitionId partitionId;
        DataNodeId dataNodeId;
        Map<PartitionId, Store> storeMap = new HashMap<>();
        List<StatsSnapshot> partitionClassSnapshots = new ArrayList<>();
        List<StatsSnapshot> accountSnapshots = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            dataNodeId = new MockDataNodeId(Collections.singletonList(new com.github.ambry.network.Port(6667, PortType.PLAINTEXT)), Collections.singletonList("/tmp"), "DC1");
            partitionId = new com.github.ambry.clustermap.MockPartitionId(i, ((i % 2) == 0 ? MockClusterMap.DEFAULT_PARTITION_CLASS : MockClusterMap.SPECIAL_PARTITION_CLASS), Collections.singletonList(((MockDataNodeId) (dataNodeId))), 0);
            Map<StatsReportType, StatsSnapshot> allSnapshots = generateRandomSnapshot();
            partitionClassSnapshots.add(allSnapshots.get(PARTITION_CLASS_REPORT));
            accountSnapshots.add(allSnapshots.get(ACCOUNT_REPORT));
            storeMap.put(partitionId, new StatsManagerTest.MockStore(new StatsManagerTest.MockStoreStats(allSnapshots, false)));
            replicaIds.add(partitionId.getReplicaIds().get(0));
        }
        StorageManager storageManager = new StatsManagerTest.MockStorageManager(storeMap);
        StatsManager statsManager = new StatsManager(storageManager, replicaIds, new MetricRegistry(), config, new MockTime());
        StatsSnapshot expectAccountSnapshot = new StatsSnapshot(0L, new HashMap());
        StatsSnapshot expectPartitionClassSnapshot = new StatsSnapshot(0L, new HashMap());
        for (int i = 0; i < (accountSnapshots.size()); ++i) {
            Map<String, StatsSnapshot> partitionToAccountSnapshot = new HashMap<>();
            Map<String, StatsSnapshot> partitionToPartitionClassSnapshot = new HashMap<>();
            Map<String, StatsSnapshot> partitionClassSnapshotMap = new HashMap<>();
            String partitionIdStr = String.valueOf(i);
            String partitionClassStr = ((i % 2) == 0) ? MockClusterMap.DEFAULT_PARTITION_CLASS : MockClusterMap.SPECIAL_PARTITION_CLASS;
            partitionToAccountSnapshot.put(partitionIdStr, accountSnapshots.get(i));
            partitionToPartitionClassSnapshot.put(partitionIdStr, partitionClassSnapshots.get(i));
            partitionClassSnapshotMap.put(partitionClassStr, new StatsSnapshot(partitionClassSnapshots.get(i).getValue(), partitionToPartitionClassSnapshot));
            // aggregate two types of snapshots respectively
            StatsSnapshot.aggregate(expectAccountSnapshot, new StatsSnapshot(accountSnapshots.get(i).getValue(), partitionToAccountSnapshot));
            StatsSnapshot.aggregate(expectPartitionClassSnapshot, new StatsSnapshot(partitionClassSnapshots.get(i).getValue(), partitionClassSnapshotMap));
        }
        // Get node level stats in JSON to verify
        for (StatsReportType type : EnumSet.of(ACCOUNT_REPORT, PARTITION_CLASS_REPORT)) {
            String statsInJSON = statsManager.getNodeStatsInJSON(type);
            StatsSnapshot actualSnapshot = mapper.readValue(statsInJSON, StatsWrapper.class).getSnapshot();
            switch (type) {
                case ACCOUNT_REPORT :
                    Assert.assertTrue("Mismatch in aggregated node stats at account level", expectAccountSnapshot.equals(actualSnapshot));
                    break;
                case PARTITION_CLASS_REPORT :
                    Assert.assertTrue("Mismatch in aggregated node stats at partitionClass level", expectPartitionClassSnapshot.equals(actualSnapshot));
                    break;
                default :
                    throw new IllegalArgumentException(("Unrecognized stats report type: " + type));
            }
        }
    }

    /**
     * Test to verify {@link StatsManager} can start and shutdown properly.
     */
    @Test
    public void testStatsManagerStartAndShutdown() {
        statsManager.start();
        statsManager.shutdown();
    }

    /**
     * Test to verify {@link StatsManager} can shutdown properly even before it's started.
     */
    @Test
    public void testShutdownBeforeStart() {
        statsManager.shutdown();
    }

    /**
     * Mocked {@link StorageManager} that is intended to have only the overwritten methods to be called and return
     * predefined values.
     */
    static class MockStorageManager extends StorageManager {
        private static final VerifiableProperties VPROPS = new VerifiableProperties(new Properties());

        private final Map<PartitionId, Store> storeMap;

        CountDownLatch waitOperationCountdown;

        boolean firstCall;

        List<PartitionId> unreachablePartitions;

        MockStorageManager(Map<PartitionId, Store> map) throws StoreException {
            super(new com.github.ambry.config.StoreConfig(StatsManagerTest.MockStorageManager.VPROPS), new com.github.ambry.config.DiskManagerConfig(StatsManagerTest.MockStorageManager.VPROPS), null, new MetricRegistry(), new ArrayList(), null, null, null, null, SystemTime.getInstance());
            storeMap = map;
            waitOperationCountdown = new CountDownLatch(0);
            firstCall = true;
            unreachablePartitions = new ArrayList();
        }

        @Override
        public Store getStore(PartitionId id) {
            if (!(firstCall)) {
                try {
                    waitOperationCountdown.await();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("CountDown await was interrupted", e);
                }
            }
            firstCall = false;
            Store storeToReturn = storeMap.get(id);
            if (storeToReturn == null) {
                unreachablePartitions.add(id);
            }
            return storeToReturn;
        }
    }

    /**
     * Mocked {@link Store} that is intended to return a predefined {@link StoreStats} when getStoreStats is called.
     */
    private class MockStore implements Store {
        private final StoreStats storeStats;

        CountDownLatch getStatsCountdown;

        boolean isCollected;

        MockStore(StoreStats storeStats) {
            this.storeStats = storeStats;
            getStatsCountdown = new CountDownLatch(0);
            isCollected = false;
        }

        @Override
        public void start() throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public StoreInfo get(List<? extends StoreKey> ids, EnumSet<StoreGetOptions> storeGetOptions) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void put(MessageWriteSet messageSetToWrite) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void delete(MessageWriteSet messageSetToDelete) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void updateTtl(MessageWriteSet messageSetToUpdate) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public FindInfo findEntriesSince(FindToken token, long maxTotalSizeOfEntries) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public Set<StoreKey> findMissingKeys(List<StoreKey> keys) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public StoreStats getStoreStats() {
            isCollected = true;
            getStatsCountdown.countDown();
            return storeStats;
        }

        @Override
        public boolean isKeyDeleted(StoreKey key) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public long getSizeInBytes() {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public boolean isEmpty() {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void shutdown() throws StoreException {
            throw new IllegalStateException("Not implemented");
        }
    }

    /**
     * Mocked {@link StoreStats} to return predefined {@link StatsSnapshot} when getStatsSnapshot is called.
     */
    private class MockStoreStats implements StoreStats {
        private final Map<StatsReportType, StatsSnapshot> snapshotsByType;

        private final boolean throwStoreException;

        MockStoreStats(Map<StatsReportType, StatsSnapshot> snapshotsByType, boolean throwStoreException) {
            this.snapshotsByType = snapshotsByType;
            this.throwStoreException = throwStoreException;
        }

        @Override
        public Pair<Long, Long> getValidSize(TimeRange timeRange) throws StoreException {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public Map<StatsReportType, StatsSnapshot> getStatsSnapshots(Set<StatsReportType> statsReportTypes, long referenceTimeInMs) throws StoreException {
            if (throwStoreException) {
                throw new StoreException("Test", StoreErrorCodes.Unknown_Error);
            }
            return snapshotsByType;
        }
    }
}

