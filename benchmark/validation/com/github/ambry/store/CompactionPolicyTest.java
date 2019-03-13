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
package com.github.ambry.store;


import com.github.ambry.config.StoreConfig;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests different {@link CompactionPolicy}
 */
@RunWith(Parameterized.class)
public class CompactionPolicyTest {
    static final long CAPACITY_IN_BYTES = (10 * 1024) * 1024;

    private static final long SEGMENT_CAPACITY_IN_BYTES = (CompactionPolicyTest.CAPACITY_IN_BYTES) / 10;

    private static final long DEFAULT_USED_CAPACITY_IN_BYTES = ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 6) / 10;

    private static final long DEFAULT_MAX_BLOB_SIZE = (CompactionPolicyTest.CAPACITY_IN_BYTES) / 100;

    private static final long SEGMENT_HEADER_SIZE = (CompactionPolicyTest.CAPACITY_IN_BYTES) / 50;

    // the properties that will used to generate a StoreConfig. Clear before use if required.
    private final Properties properties = new Properties();

    private Time time;

    private MockBlobStore blobStore;

    private StoreConfig config;

    private long messageRetentionTimeInMs;

    private MockBlobStoreStats mockBlobStoreStats;

    private CompactionPolicy compactionPolicy;

    /**
     * Instantiates {@link CompactionPolicyTest} with the required cast
     *
     * @throws InterruptedException
     * 		
     */
    public CompactionPolicyTest(String compactionPolicyFactoryStr) throws Exception {
        time = new MockTime();
        properties.setProperty("store.compaction.policy.factory", compactionPolicyFactoryStr);
        Pair<MockBlobStore, StoreConfig> initState = CompactionPolicyTest.initializeBlobStore(properties, time, (-1), (-1), CompactionPolicyTest.DEFAULT_MAX_BLOB_SIZE);
        config = initState.getSecond();
        messageRetentionTimeInMs = TimeUnit.DAYS.toMillis(config.storeDeletedMessageRetentionDays);
        blobStore = initState.getFirst();
        mockBlobStoreStats = blobStore.getBlobStoreStats();
        CompactionPolicyFactory compactionPolicyFactory = Utils.getObj(compactionPolicyFactoryStr, config, time);
        compactionPolicy = compactionPolicyFactory.getCompactionPolicy();
    }

    /**
     * Tests {@link CompactionManager#getCompactionDetails(BlobStore)} for different values for {@link BlobStore}'s
     * used capacity
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void testDifferentUsedCapacities() throws StoreException {
        List<String> bestCandidates = null;
        if ((compactionPolicy) instanceof StatsBasedCompactionPolicy) {
            bestCandidates = setUpStateForStatsBasedCompactionPolicy(blobStore, mockBlobStoreStats);
        } else
            if ((compactionPolicy) instanceof CompactAllPolicy) {
                blobStore.logSegmentsNotInJournal = CompactionPolicyTest.generateRandomLogSegmentName(3);
                bestCandidates = blobStore.logSegmentsNotInJournal;
            }

        // if used capacity is <= 60%, compaction details will be null. If not, logSegmentsNotInJournal needs to be returned.
        Long[] usedCapacities = new Long[]{ ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 2) / 10, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 4) / 10, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 5) / 10, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 51) / 100, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 6) / 10, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 7) / 10, ((CompactionPolicyTest.CAPACITY_IN_BYTES) * 9) / 10 };
        for (Long usedCapacity : usedCapacities) {
            blobStore.usedCapacity = usedCapacity;
            if ((blobStore.usedCapacity) < (((config.storeMinUsedCapacityToTriggerCompactionInPercentage) / 100.0) * (blobStore.capacityInBytes))) {
                CompactionPolicyTest.verifyCompactionDetails(null, blobStore, compactionPolicy);
            } else {
                CompactionPolicyTest.verifyCompactionDetails(new CompactionDetails(((time.milliseconds()) - (messageRetentionTimeInMs)), bestCandidates), blobStore, compactionPolicy);
            }
        }
    }

    /**
     * Tests {@link CompactionManager#getCompactionDetails(BlobStore)} for different values for
     * {@link StoreConfig#storeMinUsedCapacityToTriggerCompactionInPercentage}
     */
    @Test
    public void testDifferentThresholdsForMinLogSizeToCompact() throws StoreException, InterruptedException {
        int[] minLogSizeToTriggerCompactionInPercentages = new int[]{ 10, 20, 35, 40, 50, 59, 60, 61, 65, 70, 80, 95 };
        // when used capacity(60%) is <= (minLogSize) % of total capacity, compactionDetails is expected to be null.
        // If not, logSegmentsNotInJournal needs to be returned
        List<String> bestCandidates = null;
        for (int minLogSize : minLogSizeToTriggerCompactionInPercentages) {
            CompactionPolicyTest.initializeBlobStore(properties, time, minLogSize, (-1), CompactionPolicyTest.DEFAULT_MAX_BLOB_SIZE);
            if ((compactionPolicy) instanceof StatsBasedCompactionPolicy) {
                bestCandidates = setUpStateForStatsBasedCompactionPolicy(blobStore, mockBlobStoreStats);
            } else
                if ((compactionPolicy) instanceof CompactAllPolicy) {
                    blobStore.logSegmentsNotInJournal = CompactionPolicyTest.generateRandomLogSegmentName(3);
                    bestCandidates = blobStore.logSegmentsNotInJournal;
                }

            if ((blobStore.usedCapacity) < (((config.storeMinUsedCapacityToTriggerCompactionInPercentage) / 100.0) * (blobStore.capacityInBytes))) {
                CompactionPolicyTest.verifyCompactionDetails(null, blobStore, compactionPolicy);
            } else {
                CompactionPolicyTest.verifyCompactionDetails(new CompactionDetails(((time.milliseconds()) - (messageRetentionTimeInMs)), bestCandidates), blobStore, compactionPolicy);
            }
        }
    }

    /**
     * Tests {@link CompactionManager#getCompactionDetails(BlobStore)} for different values for
     * {@link StoreConfig#storeDeletedMessageRetentionDays}
     */
    @Test
    public void testDifferentMessageRetentionDays() throws StoreException, InterruptedException {
        List<String> bestCandidates = null;
        int[] messageRetentionDayValues = new int[]{ 1, 2, 3, 6, 9 };
        for (int messageRetentionDays : messageRetentionDayValues) {
            time = new MockTime();
            Pair<MockBlobStore, StoreConfig> initState = CompactionPolicyTest.initializeBlobStore(properties, time, (-1), messageRetentionDays, CompactionPolicyTest.DEFAULT_MAX_BLOB_SIZE);
            if ((compactionPolicy) instanceof StatsBasedCompactionPolicy) {
                bestCandidates = setUpStateForStatsBasedCompactionPolicy(blobStore, mockBlobStoreStats);
                compactionPolicy = new StatsBasedCompactionPolicy(initState.getSecond(), time);
            } else
                if ((compactionPolicy) instanceof CompactAllPolicy) {
                    blobStore.logSegmentsNotInJournal = CompactionPolicyTest.generateRandomLogSegmentName(3);
                    bestCandidates = blobStore.logSegmentsNotInJournal;
                    compactionPolicy = new CompactAllPolicy(initState.getSecond(), time);
                }

            CompactionPolicyTest.verifyCompactionDetails(new CompactionDetails(((time.milliseconds()) - (TimeUnit.DAYS.toMillis(messageRetentionDays))), bestCandidates), blobStore, compactionPolicy);
        }
    }
}

