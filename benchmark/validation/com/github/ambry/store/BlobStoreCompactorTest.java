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


import BlobStoreCompactor.COMPACTION_CLEANUP_JOB_NAME;
import BlobStoreCompactor.INDEX_SEGMENT_READ_JOB_NAME;
import BlobStoreCompactor.TEMP_LOG_SEGMENTS_FILTER;
import IndexValue.Flags.Delete_Index;
import IndexValue.UNKNOWN_ORIGINAL_MESSAGE_OFFSET;
import PersistentIndex.INDEX_ENTRIES_OFFSET_COMPARATOR;
import PersistentIndex.IndexEntryType;
import StoreErrorCodes.ID_Deleted;
import StoreGetOptions.Store_Include_Deleted;
import TestUtils.RANDOM;
import Time.MsPerSec;
import Utils.Infinite_Time;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BlobStoreCompactor.TARGET_INDEX_CLEAN_SHUTDOWN_FILE_NAME;
import static IndexValue.UNKNOWN_ORIGINAL_MESSAGE_OFFSET;
import static LogSegment.HEADER_SIZE;


/**
 * Tests for {@link BlobStoreCompactor}.
 */
@RunWith(Parameterized.class)
public class BlobStoreCompactorTest {
    private static final String STORE_ID = "compactor_example_store";

    private static final DiskIOScheduler DISK_IO_SCHEDULER = new DiskIOScheduler(null);

    private static final String EXCEPTION_MSG = UtilsTest.getRandomString(10);

    private final File tempDir;

    private final String tempDirStr;

    private final boolean doDirectIO;

    private CuratedLogIndexState state = null;

    private BlobStoreCompactor compactor = null;

    // indicates whether any of InterruptionInducers induced the close/crash.
    private boolean closeOrExceptionInduced = false;

    // for InterruptionInducingLog and InterruptionInducingIndex, an exception is thrown after the operation
    // if throwExceptionBeforeOperation is not true.
    private boolean throwExceptionInsteadOfClose = false;

    // not applicable to the InterruptionInducingDiskIOScheduler. Throws in InterruptionInducingLog and
    // InterruptionInducingIndex irrespective of the value of throwExceptionInsteadOfClose
    private boolean throwExceptionBeforeOperation = false;

    private MetricRegistry metricRegistry;

    private byte[] bundleReadBuffer = new byte[(((int) (CuratedLogIndexState.PUT_RECORD_SIZE)) * 2) + 1];

    /**
     * Creates a temporary directory for the store.
     *
     * @throws Exception
     * 		
     */
    public BlobStoreCompactorTest(boolean doDirectIO) throws Exception {
        tempDir = StoreTestUtils.createTempDirectory(("compactorDir-" + (UtilsTest.getRandomString(10))));
        tempDirStr = tempDir.getAbsolutePath();
        this.doDirectIO = doDirectIO;
        if (doDirectIO) {
            Assume.assumeTrue(Utils.isLinux());
        }
    }

    /**
     * Tests basic init/close.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void initCloseTest() throws Exception {
        refreshState(false, true);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.initialize(state.index);
        compactor.close(0);
    }

    /**
     * Tests closing without initialization.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void closeWithoutInitTest() throws Exception {
        refreshState(false, true);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.close(0);
    }

    /**
     * Tests attempt to use the service without initializing the service.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void useServiceWithoutInitTest() throws Exception {
        refreshState(false, true);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        String firstSegmentName = state.log.getFirstSegment().getName();
        CompactionDetails details = new CompactionDetails(state.time.milliseconds(), Collections.singletonList(firstSegmentName));
        try {
            compactor.compact(details, bundleReadBuffer);
            Assert.fail("Should have failed to do anything because compactor has not been initialized");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
        // create compaction log so that resumeCompaction() thinks there is a compaction in progress
        try (CompactionLog cLog = new CompactionLog(tempDirStr, BlobStoreCompactorTest.STORE_ID, state.time, details)) {
            compactor.resumeCompaction(bundleReadBuffer);
            Assert.fail("Should have failed to do anything because compactor has not been initialized");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests APIs with bad input and ensures that they fail fast.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badInputTest() throws Exception {
        refreshState(false, true);
        // compaction range contains a log segment that is still in the journal
        String firstLogSegmentName = state.referenceIndex.firstKey().getName();
        String secondLogSegmentName = state.log.getNextSegment(state.log.getSegment(firstLogSegmentName)).getName();
        String lastLogSegmentName = state.referenceIndex.lastKey().getName();
        CompactionDetails details = new CompactionDetails(((state.time.milliseconds()) + (Time.MsPerSec)), Arrays.asList(firstLogSegmentName, lastLogSegmentName));
        ensureArgumentFailure(details, "Should have failed because compaction range contains offsets still in the journal");
        // compaction range contains segments in the wrong order
        details = new CompactionDetails(((state.time.milliseconds()) + (Time.MsPerSec)), Arrays.asList(secondLogSegmentName, firstLogSegmentName));
        ensureArgumentFailure(details, "Should have failed because compaction range contains offsets still in the journal");
        // compaction contains segments that don't exist
        details = new CompactionDetails(0, Collections.singletonList(LogSegmentNameHelper.getNextPositionName(lastLogSegmentName)));
        ensureArgumentFailure(details, "Should have failed because compaction range contains offsets still in the journal");
    }

    /**
     * Tests to make sure that {@link BlobStoreCompactor#compact(CompactionDetails, byte[])} fails when a compaction is
     * already in progress.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void compactWithCompactionInProgressTest() throws Exception {
        refreshState(false, true);
        List<String> segmentsUnderCompaction = getLogSegments(0, 2);
        CompactionDetails details = new CompactionDetails(0, segmentsUnderCompaction);
        // create a compaction log in order to mimic a compaction being in progress
        CompactionLog cLog = new CompactionLog(tempDirStr, BlobStoreCompactorTest.STORE_ID, state.time, details);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.initialize(state.index);
        try {
            compactor.compact(details, bundleReadBuffer);
            Assert.fail("compact() should have failed because a compaction is already in progress");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        } finally {
            cLog.close();
            compactor.close(0);
        }
    }

    /**
     * Tests the case where {@link BlobStoreCompactor#resumeCompaction(byte[])} is called without any compaction being in
     * progress.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void resumeCompactionWithoutAnyInProgressTest() throws Exception {
        refreshState(false, true);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.initialize(state.index);
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, BlobStoreCompactorTest.STORE_ID));
        Assert.assertEquals("Temp log segment should not be found", 0, compactor.getSwapSegmentsInUse());
        try {
            compactor.resumeCompaction(bundleReadBuffer);
            Assert.fail("Should have failed because there is no compaction in progress");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        } finally {
            compactor.close(0);
        }
    }

    /**
     * A basic test for compaction that selects the first two log segments and compacts them into one log segment.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicTest() throws Exception {
        refreshState(false, true);
        List<String> segmentsUnderCompaction = getLogSegments(0, 2);
        long deleteReferenceTimeMs = reduceValidDataSizeInLogSegments(segmentsUnderCompaction, ((state.log.getSegmentCapacity()) - (HEADER_SIZE)));
        compactAndVerify(segmentsUnderCompaction, deleteReferenceTimeMs, true);
    }

    /**
     * A test similar to basicTest but doesn't use bundleReadBuffer.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void basicTestNoBundleReadBuffer() throws Exception {
        bundleReadBuffer = null;
        refreshState(false, true);
        List<String> segmentsUnderCompaction = getLogSegments(0, 2);
        long deleteReferenceTimeMs = reduceValidDataSizeInLogSegments(segmentsUnderCompaction, ((state.log.getSegmentCapacity()) - (HEADER_SIZE)));
        compactAndVerify(segmentsUnderCompaction, deleteReferenceTimeMs, true);
    }

    /**
     * Compacts the whole log (except the last log segment) but without any changes expected i.e all data is valid and is
     * simply copied over from the old log segments to the new log segments.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void compactWholeLogWithNoChangeExpectedTest() throws Exception {
        long delayBeforeLastLogSegmentWrite = 20 * (Time.MsPerSec);
        refreshState(false, false);
        // write data until the very last segment is reached
        long requiredCount = (state.log.getCapacityInBytes()) / (state.log.getSegmentCapacity());
        // write entries with an expiry time such that no records are actually expired at the time of compaction
        long expiryTimeMs = (getInvalidationTime(requiredCount)) + delayBeforeLastLogSegmentWrite;
        writeDataToMeetRequiredSegmentCount((requiredCount - 1), Collections.singletonList(expiryTimeMs));
        state.advanceTime(delayBeforeLastLogSegmentWrite);
        writeDataToMeetRequiredSegmentCount(requiredCount, Collections.singletonList(expiryTimeMs));
        // reload index to make sure journal is on only the latest log segment
        state.reloadIndex(true, false);
        long deleteReferenceTimeMs = state.time.milliseconds();
        List<String> segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, deleteReferenceTimeMs, false);
    }

    /**
     * Compacts the whole log (except the last log segment) and a changed size is expected i.e. there is some invalid
     * data.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void compactWholeLogWithChangeExpectedTest() throws Exception {
        refreshState(false, true);
        long requiredCount = ((state.log.getCapacityInBytes()) / (state.log.getSegmentCapacity())) - 2;
        writeDataToMeetRequiredSegmentCount(requiredCount, Arrays.asList(((state.time.milliseconds()) / 2), state.time.milliseconds(), ((state.time.milliseconds()) * 2)));
        // do some random deleting.
        int deleteCount = Math.min(((state.liveKeys.size()) / 3), ((int) ((1.8 * (state.log.getSegmentCapacity())) / (CuratedLogIndexState.DELETE_RECORD_SIZE))));
        List<MockId> allLiveKeys = new ArrayList(state.liveKeys);
        for (int i = 0; i < deleteCount; i++) {
            MockId idToDelete = allLiveKeys.remove(RANDOM.nextInt(allLiveKeys.size()));
            state.addDeleteEntry(idToDelete);
        }
        // reload index to make sure journal is on only the latest log segment
        state.reloadIndex(true, false);
        long deleteReferenceTimeMs = state.time.milliseconds();
        List<String> segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, deleteReferenceTimeMs, true);
    }

    /**
     * Compacts the whole log multiple times with some data compacted each time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void compactWholeLogMultipleTimesTest() throws Exception {
        refreshState(false, true);
        long requiredCount = ((state.log.getCapacityInBytes()) / (state.log.getSegmentCapacity())) - 3;
        long expiryTimeMs = getInvalidationTime(requiredCount);
        List<Long> expiryTimesMs = Arrays.asList(((state.time.milliseconds()) / 2), expiryTimeMs, (expiryTimeMs * 2));
        writeDataToMeetRequiredSegmentCount(requiredCount, expiryTimesMs);
        List<String> segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        Set<MockId> idsInCompactedLogSegments = getIdsWithPutInSegments(segmentsUnderCompaction);
        for (long setTimeMs : expiryTimesMs) {
            if ((state.time.milliseconds()) < (setTimeMs + (Time.MsPerSec))) {
                state.advanceTime(((setTimeMs + (Time.MsPerSec)) - (state.time.milliseconds())));
            }
            long deleteReferenceTimeMs = state.time.milliseconds();
            long logSegmentSizeSumBeforeCompaction = getSumOfLogSegmentEndOffsets();
            CompactionDetails details = new CompactionDetails(deleteReferenceTimeMs, segmentsUnderCompaction);
            compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
            compactor.initialize(state.index);
            long logSegmentsBeforeCompaction = state.index.getLogSegmentCount();
            try {
                compactor.compact(details, bundleReadBuffer);
            } finally {
                compactor.close(0);
            }
            Assert.assertFalse("Sum of size of log segments did not change after compaction", (logSegmentSizeSumBeforeCompaction == (getSumOfLogSegmentEndOffsets())));
            verifyDataPostCompaction(idsInCompactedLogSegments, deleteReferenceTimeMs);
            state.reloadLog(true);
            verifyDataPostCompaction(idsInCompactedLogSegments, deleteReferenceTimeMs);
            segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
            state.verifyRealIndexSanity();
            // no clean shutdown file should exist
            Assert.assertFalse("Clean shutdown file not deleted", new File(tempDirStr, TARGET_INDEX_CLEAN_SHUTDOWN_FILE_NAME).exists());
            // there should be no temp files
            Assert.assertEquals("There are some temp log segments", 0, tempDir.listFiles(TEMP_LOG_SEGMENTS_FILTER).length);
            verifySavedBytesCount(logSegmentsBeforeCompaction, 0);
        }
    }

    /**
     * Compacts the whole log (except the last log segment) and a changed size is expected i.e. there is some invalid
     * data. All this is done with hard delete enabled (compactor is expected to pause it).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void compactWholeLogWithHardDeleteEnabledTest() throws Exception {
        // no interruptions
        doCompactWholeLogWithHardDeleteEnabledTest(false, false);
        // close in the middle of copying
        doCompactWholeLogWithHardDeleteEnabledTest(true, true);
        // crash in the middle of copying
        throwExceptionInsteadOfClose = true;
        doCompactWholeLogWithHardDeleteEnabledTest(true, true);
        // crash in the middle of commit
        doCompactWholeLogWithHardDeleteEnabledTest(true, false);
    }

    /**
     * Tests the case where there is no valid data at all in the segments under compaction and they are essentially
     * dropped.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void dropAllSegmentsUnderCompactionTest() throws Exception {
        Pair<Long, List<String>> deleteTimeAndSegmentsUnderCompaction = setupStateWithDeletedBlobsAtSpecificTime();
        List<String> segmentsUnderCompaction = deleteTimeAndSegmentsUnderCompaction.getSecond();
        // delete all the blobs in the segments under compaction
        Set<MockId> ids = getIdsWithPutInSegments(segmentsUnderCompaction);
        for (MockId id : ids) {
            if (state.liveKeys.contains(id)) {
                state.addDeleteEntry(id);
            }
        }
        long deleteReferenceTimeMs = (state.time.milliseconds()) + (Time.MsPerSec);
        state.advanceTime((deleteReferenceTimeMs - (state.time.milliseconds())));
        Assert.assertEquals("Valid size in the segments under compaction should be 0", 0, getValidDataSize(segmentsUnderCompaction, deleteReferenceTimeMs));
        compactAndVerify(segmentsUnderCompaction, deleteReferenceTimeMs, true);
    }

    /**
     * Tests the case where expiration time is enforced i.e. data is considered valid before expiry time and is copied
     * over and data is considered invalid after expiry time and is not copied over.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void expirationTimeEnforcementTest() throws Exception {
        // no change before expiry time
        Pair<Long, List<String>> expiryTimeAndSegmentsUnderCompaction = setupStateWithExpiredBlobsAtSpecificTime();
        Map<String, Long> oldSegmentNamesAndEndOffsets = getEndOffsets(expiryTimeAndSegmentsUnderCompaction.getSecond());
        compactAndVerify(expiryTimeAndSegmentsUnderCompaction.getSecond(), state.time.milliseconds(), false);
        verifyNoChangeInEndOffsets(oldSegmentNamesAndEndOffsets);
        // no change at expiry time.
        expiryTimeAndSegmentsUnderCompaction = setupStateWithExpiredBlobsAtSpecificTime();
        oldSegmentNamesAndEndOffsets = getEndOffsets(expiryTimeAndSegmentsUnderCompaction.getSecond());
        state.advanceTime(((expiryTimeAndSegmentsUnderCompaction.getFirst()) - (state.time.milliseconds())));
        compactAndVerify(expiryTimeAndSegmentsUnderCompaction.getSecond(), state.time.milliseconds(), false);
        verifyNoChangeInEndOffsets(oldSegmentNamesAndEndOffsets);
        // there will be changes past expiration time
        expiryTimeAndSegmentsUnderCompaction = setupStateWithExpiredBlobsAtSpecificTime();
        state.advanceTime((((expiryTimeAndSegmentsUnderCompaction.getFirst()) + (Time.MsPerSec)) - (state.time.milliseconds())));
        compactAndVerify(expiryTimeAndSegmentsUnderCompaction.getSecond(), state.time.milliseconds(), true);
    }

    /**
     * Tests the case where deletion time is enforced i.e. data is considered valid before reference time and is copied
     * over and data is considered invalid after reference time and is not copied over.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deletionTimeEnforcementTest() throws Exception {
        // no change before delete time
        Pair<Long, List<String>> deleteTimeAndSegmentsUnderCompaction = setupStateWithDeletedBlobsAtSpecificTime();
        long deleteReferenceTimeMs = (deleteTimeAndSegmentsUnderCompaction.getFirst()) - (Time.MsPerSec);
        Map<String, Long> oldSegmentNamesAndEndOffsets = getEndOffsets(deleteTimeAndSegmentsUnderCompaction.getSecond());
        compactAndVerify(deleteTimeAndSegmentsUnderCompaction.getSecond(), deleteReferenceTimeMs, false);
        verifyNoChangeInEndOffsets(oldSegmentNamesAndEndOffsets);
        // no change at delete time.
        deleteTimeAndSegmentsUnderCompaction = setupStateWithDeletedBlobsAtSpecificTime();
        deleteReferenceTimeMs = deleteTimeAndSegmentsUnderCompaction.getFirst();
        oldSegmentNamesAndEndOffsets = getEndOffsets(deleteTimeAndSegmentsUnderCompaction.getSecond());
        compactAndVerify(deleteTimeAndSegmentsUnderCompaction.getSecond(), deleteReferenceTimeMs, false);
        verifyNoChangeInEndOffsets(oldSegmentNamesAndEndOffsets);
        // there will be changes past delete time
        deleteTimeAndSegmentsUnderCompaction = setupStateWithDeletedBlobsAtSpecificTime();
        state.advanceTime(MsPerSec);
        compactAndVerify(deleteTimeAndSegmentsUnderCompaction.getSecond(), state.time.milliseconds(), true);
    }

    /**
     * Tests the case where the segments being compacted have keys that are deleted and expired but the deleted keys
     * don't count as deleted at the provided reference time (but the expired keys need to be cleaned up).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void differentDeleteAndExpiryTimesTest() throws Exception {
        Pair<Long, List<String>> expiryTimeAndSegmentsUnderCompaction = setupStateWithExpiredBlobsAtSpecificTime();
        state.advanceTime((((expiryTimeAndSegmentsUnderCompaction.getFirst()) + (Time.MsPerSec)) - (state.time.milliseconds())));
        long deleteReferenceTimeMs = state.time.milliseconds();
        state.advanceTime(MsPerSec);
        int deleteCount = 10;
        Set<MockId> idsInSegments = getIdsWithPutInSegments(expiryTimeAndSegmentsUnderCompaction.getSecond());
        List<MockId> idsToExamine = new ArrayList<>();
        for (MockId id : idsInSegments) {
            if (state.liveKeys.contains(id)) {
                state.addDeleteEntry(id);
                idsToExamine.add(id);
                if ((idsToExamine.size()) == deleteCount) {
                    break;
                }
            }
        }
        // reload index to make sure journal is on only the latest log segment
        state.reloadIndex(true, false);
        // compact with deleteReferenceTimeMs < the earliest delete time. None of the deleted keys count as deleted.
        compactAndVerify(expiryTimeAndSegmentsUnderCompaction.getSecond(), deleteReferenceTimeMs, true);
        // ensure that idsToExamine can still be fetched
        state.reloadIndex(true, false);
        for (MockId id : idsToExamine) {
            // should not throw exception since they should be untouched.
            // Data has already been verified if this is true (by the verifiers).
            state.index.getBlobReadInfo(id, EnumSet.of(Store_Include_Deleted)).close();
        }
    }

    /**
     * Tests the case where deletes and expired blobs are interspersed and the expired blobs are eligible for cleanup
     * but deleted blobs (also includes blobs that have been put and deleted in the same index segment) are not.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interspersedDeletedAndExpiredBlobsTest() throws Exception {
        refreshState(false, false);
        state.properties.put("store.index.max.number.of.inmem.elements", Integer.toString(5));
        state.initIndex(null);
        int numFinalSegmentsCount = 3;
        long expiryTimeMs = getInvalidationTime((numFinalSegmentsCount + 1));
        // fill up one segment.
        writeDataToMeetRequiredSegmentCount(1, null);
        // IS 1.1 starts
        // 1. Put entry that contains a delete entry in the same index segment and is not counted as deleted.
        // won't be cleaned up.
        IndexEntry entry = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        Assert.assertEquals("There should have been a new log segment created", 2, state.index.getLogSegmentCount());
        String logSegmentName = entry.getValue().getOffset().getName();
        MockId delUnexpPutSameIdxSegId = ((MockId) (entry.getKey()));
        state.addDeleteEntry(delUnexpPutSameIdxSegId);
        // 2. Put entry that has expired and contains a delete entry in the same index segment. Does not count as deleted
        // but is expired.
        // will be cleaned up, but delete record remains
        MockId delExpPutSameIdxSegId = ((MockId) (state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, 0).get(0).getKey()));
        state.addDeleteEntry(delExpPutSameIdxSegId);
        // 3. Put entry that will be deleted.
        // won't be cleaned up.
        MockId willBeDelPut = ((MockId) (state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0).getKey()));
        // IS 1.1 ends
        // roll over. IS 1.2 starts
        // 4. Put entry that has expired.
        // will be cleaned up.
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, 0).get(0).getKey();
        // 5. Put entry with an expiry time  that is not expired and has a delete entry in the same index segment and is not
        // counted as deleted or expired. In the compacted log, the put entry will be in one index segment and the delete
        // in another
        // won't be cleaned up.
        MockId delUnexpPutDiffIdxSegId = ((MockId) (state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiryTimeMs).get(0).getKey()));
        state.addDeleteEntry(delUnexpPutDiffIdxSegId);
        // 6. Put entry that will expire (but is valid right now).
        // won't be cleaned up.
        MockId willExpPut = ((MockId) (state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiryTimeMs).get(0).getKey()));
        // 7. Delete entry for an id that is in another index segment
        // won't be cleaned up.
        state.addDeleteEntry(willBeDelPut);
        // IS 1.2 ends
        // rollover. IS 1.3 starts
        // 8. Delete entry for an id that is in another log segment
        // won't be cleaned up.
        MockId idFromAnotherSegment = state.getIdToDeleteFromLogSegment(state.log.getFirstSegment(), false);
        state.addDeleteEntry(idFromAnotherSegment);
        // 9. Delete entry for a Put entry that doesn't exist. However, if it existed, it wouldn't have been eligible for
        // cleanup
        // the delete record itself won't be cleaned up
        MockId uniqueId = state.getUniqueId();
        state.addDeleteEntry(uniqueId, new MessageInfo(uniqueId, Integer.MAX_VALUE, expiryTimeMs, uniqueId.getAccountId(), uniqueId.getContainerId(), state.time.milliseconds()));
        // fill up the rest of the segment + one more
        writeDataToMeetRequiredSegmentCount(numFinalSegmentsCount, null);
        // reload index to keep the journal only in the last log segment
        state.reloadIndex(true, false);
        long deleteReferenceTimeMs = 0;
        List<String> segmentsUnderCompaction = Collections.singletonList(logSegmentName);
        long endOffsetOfSegmentBeforeCompaction = state.log.getSegment(logSegmentName).getEndOffset();
        CompactionDetails details = new CompactionDetails(deleteReferenceTimeMs, segmentsUnderCompaction);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.initialize(state.index);
        long logSegmentCountBeforeCompaction = state.index.getLogSegmentCount();
        try {
            compactor.compact(details, bundleReadBuffer);
        } finally {
            compactor.close(0);
        }
        String compactedLogSegmentName = LogSegmentNameHelper.getNextGenerationName(logSegmentName);
        LogSegment compactedLogSegment = state.log.getSegment(compactedLogSegmentName);
        long cleanedUpSize = 2 * (CuratedLogIndexState.PUT_RECORD_SIZE);
        Assert.assertEquals("End offset of log segment not as expected after compaction", (endOffsetOfSegmentBeforeCompaction - cleanedUpSize), state.log.getSegment(compactedLogSegmentName).getEndOffset());
        FindEntriesCondition condition = new FindEntriesCondition(Long.MAX_VALUE);
        // get the first index segment that refers to the compacted segment
        IndexSegment indexSegment = state.index.getIndexSegments().get(new Offset(compactedLogSegmentName, compactedLogSegment.getStartOffset()));
        List<IndexEntry> indexEntries = new ArrayList<>();
        Assert.assertTrue("Should have got some index entries", indexSegment.getIndexEntriesSince(null, condition, indexEntries, new AtomicLong(0), true));
        Assert.assertEquals("There should be 5 index entries returned", 4, indexEntries.size());
        indexEntries.sort(INDEX_ENTRIES_OFFSET_COMPARATOR);
        long logSegmentStartOffset = compactedLogSegment.getStartOffset();
        long currentExpectedOffset = logSegmentStartOffset + (CuratedLogIndexState.PUT_RECORD_SIZE);
        // first index entry should be a delete and it should have an original message offset
        verifyIndexEntry(indexEntries.get(0), delUnexpPutSameIdxSegId, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, Infinite_Time, true, logSegmentStartOffset);
        currentExpectedOffset += CuratedLogIndexState.DELETE_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(1), delExpPutSameIdxSegId, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, 0, true, UNKNOWN_ORIGINAL_MESSAGE_OFFSET);
        currentExpectedOffset += CuratedLogIndexState.DELETE_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(2), willBeDelPut, currentExpectedOffset, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time, false, currentExpectedOffset);
        long willBeDelOffset = currentExpectedOffset;
        currentExpectedOffset += CuratedLogIndexState.PUT_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(3), delUnexpPutDiffIdxSegId, currentExpectedOffset, CuratedLogIndexState.PUT_RECORD_SIZE, expiryTimeMs, false, currentExpectedOffset);
        currentExpectedOffset += CuratedLogIndexState.PUT_RECORD_SIZE;
        // get the second index segment
        indexSegment = state.index.getIndexSegments().higherEntry(indexSegment.getStartOffset()).getValue();
        indexEntries.clear();
        Assert.assertTrue("Should have got some index entries", indexSegment.getIndexEntriesSince(null, condition, indexEntries, new AtomicLong(0), true));
        Assert.assertEquals("There should be 5 index entries returned", 5, indexEntries.size());
        indexEntries.sort(INDEX_ENTRIES_OFFSET_COMPARATOR);
        verifyIndexEntry(indexEntries.get(0), delUnexpPutDiffIdxSegId, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, expiryTimeMs, true, (currentExpectedOffset - (CuratedLogIndexState.PUT_RECORD_SIZE)));
        currentExpectedOffset += CuratedLogIndexState.DELETE_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(1), willExpPut, currentExpectedOffset, CuratedLogIndexState.PUT_RECORD_SIZE, expiryTimeMs, false, currentExpectedOffset);
        currentExpectedOffset += CuratedLogIndexState.PUT_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(2), willBeDelPut, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, Infinite_Time, true, willBeDelOffset);
        currentExpectedOffset += CuratedLogIndexState.DELETE_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(3), idFromAnotherSegment, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, Infinite_Time, true, UNKNOWN_ORIGINAL_MESSAGE_OFFSET);
        currentExpectedOffset += CuratedLogIndexState.DELETE_RECORD_SIZE;
        verifyIndexEntry(indexEntries.get(4), uniqueId, currentExpectedOffset, CuratedLogIndexState.DELETE_RECORD_SIZE, expiryTimeMs, true, UNKNOWN_ORIGINAL_MESSAGE_OFFSET);
        // no clean shutdown file should exist
        Assert.assertFalse("Clean shutdown file not deleted", new File(tempDirStr, TARGET_INDEX_CLEAN_SHUTDOWN_FILE_NAME).exists());
        // there should be no temp files
        Assert.assertEquals("There are some temp log segments", 0, tempDir.listFiles(TEMP_LOG_SEGMENTS_FILTER).length);
        verifySavedBytesCount(logSegmentCountBeforeCompaction, 0);
    }

    /**
     * Tests the case where there is an interruption (crash/close) of compaction during log commit or cleanup.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interruptionDuringLogCommitAndCleanupTest() throws Exception {
        // close testing
        // close during commit
        doTestWithInterruptionInducingLog(1, Integer.MAX_VALUE);
        // close during cleanup
        doTestWithInterruptionInducingLog(Integer.MAX_VALUE, 1);
        // crash testing
        // crash after executing operation
        throwExceptionInsteadOfClose = true;
        throwExceptionBeforeOperation = false;
        // crash after commit
        doTestWithInterruptionInducingLog(1, Integer.MAX_VALUE);
        // crash after cleanup
        doTestWithInterruptionInducingLog(Integer.MAX_VALUE, 1);
        // crash before executing operation
        throwExceptionBeforeOperation = true;
        // crash before commit
        doTestWithInterruptionInducingLog(1, Integer.MAX_VALUE);
        // crash before cleanup
        doTestWithInterruptionInducingLog(Integer.MAX_VALUE, 1);
    }

    /**
     * Tests the case where there is an interruption (crash/close) of compaction during index commit.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interruptionDuringIndexCommitTest() throws Exception {
        // close testing
        doInterruptionDuringIndexCommitTest();
        // crash testing
        // crash after executing operation
        throwExceptionInsteadOfClose = true;
        throwExceptionBeforeOperation = false;
        doInterruptionDuringIndexCommitTest();
        // crash before executing operation
        throwExceptionBeforeOperation = true;
        doInterruptionDuringIndexCommitTest();
    }

    /**
     * Tests the case where there is an interruption (crash/close) of compaction during copy after a few index segments
     * have been processed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interruptionDuringOrAfterIndexSegmentProcessingTest() throws Exception {
        // close testing
        doInterruptionDuringOrAfterIndexSegmentProcessingTest();
        // crash testing
        // crash after executing operation
        throwExceptionInsteadOfClose = true;
        throwExceptionBeforeOperation = false;
        doInterruptionDuringOrAfterIndexSegmentProcessingTest();
        // crash before executing operation
        throwExceptionBeforeOperation = true;
        doInterruptionDuringOrAfterIndexSegmentProcessingTest();
    }

    /**
     * Tests the case where there is an interruption (crash/close) of compaction during copy when a few records from
     * an index segment have been copied over.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interruptionDuringRecordCopyTest() throws Exception {
        // close testing
        doInterruptionDuringRecordCopyTest();
        // crash testing
        throwExceptionInsteadOfClose = true;
        doInterruptionDuringRecordCopyTest();
    }

    /**
     * Tests the case where there is an interruption (crash/close) of compaction during log commit of the very last
     * cycle of compaction (tests the case where compaction finishes in {@link BlobStoreCompactor#fixStateIfRequired()}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void interruptionDuringLastCommitTest() throws Exception {
        // keep hard delete enabled
        refreshState(true, true);
        List<String> segmentsUnderCompaction = getLogSegments(0, 2);
        long deleteReferenceTimeMs = reduceValidDataSizeInLogSegments(segmentsUnderCompaction, ((state.log.getSegmentCapacity()) - (HEADER_SIZE)));
        throwExceptionBeforeOperation = true;
        Log log = new BlobStoreCompactorTest.InterruptionInducingLog(1, Integer.MAX_VALUE);
        Assert.assertTrue("Hard delete should be running", state.index.hardDeleter.isRunning());
        compactWithRecoveryAndVerify(log, BlobStoreCompactorTest.DISK_IO_SCHEDULER, state.index, segmentsUnderCompaction, deleteReferenceTimeMs, true, false);
        Assert.assertTrue("Hard delete should be running", state.index.hardDeleter.isRunning());
    }

    /**
     * Tests compaction on a log that has all combinations of PUT and DELETE records.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void allEntryTypesTest() throws Exception {
        // NOTE: There is no need to add tests for cases here that are already covered by CuratedLogIndexState. This test
        // repeats a lot of that state (CuratedLogIndexState had some gaps before). Special cases for TTL updates covered
        // in a different test
        List<IndexEntry> otherPuts = new ArrayList<>();
        // types of records
        // put
        // p1 - no expiry, not deleted (retain)
        // p2 - not expired, not deleted (retain)
        // p3 - expired, not deleted (clean)
        // p4 - not expired, deleted, delete not in effect (retain)
        // p5 - not expired, deleted, delete in effect (clean)
        // p6 - expired, deleted, delete not in effect (clean)
        // p7 - expired, deleted, delete in effect (clean)
        // p8 - no expiry, deleted, delete not in effect (retain)
        // p9 - no expiry, deleted, delete in effect (clean)
        // delete
        // d1 - put in the same index segment
        // d2 - put in the same log segment but in diff index segment
        // d3 - put in a diff log segment (also in diff index segment as a result)
        // note on the naming of index entry variables
        // index entry variables will be named as puttype,deletetype
        // for example, index entry for type p4 that will have a delete record of type d2 will be named p4d2
        refreshState(false, false);
        state.properties.setProperty("store.index.max.number.of.inmem.elements", "5");
        state.reloadIndex(true, false);
        long notExpiredMs = (state.time.milliseconds()) + (TimeUnit.SECONDS.toMillis(Short.MAX_VALUE));
        long expiredMs = state.time.milliseconds();
        // LS (Log Segment) 0
        // IS (Index Segment) 0.1
        IndexEntry p1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        IndexEntry p2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        IndexEntry p3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        IndexEntry p5d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        state.addDeleteEntry(((MockId) (p5d1.getKey())));
        // IS 0.2
        IndexEntry p5d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        IndexEntry p7d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        state.addDeleteEntry(((MockId) (p7d1.getKey())));
        IndexEntry p9d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        state.addDeleteEntry(((MockId) (p9d1.getKey())));
        // IS 0.3
        IndexEntry p7d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        IndexEntry p9d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        IndexEntry p5d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        IndexEntry p7d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        IndexEntry p9d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        // IS 0.4
        state.addDeleteEntry(((MockId) (p5d2.getKey())));
        state.addDeleteEntry(((MockId) (p7d2.getKey())));
        state.addDeleteEntry(((MockId) (p9d2.getKey())));
        long lastRecSize = (state.log.getSegmentCapacity()) - (state.index.getCurrentEndOffset().getOffset());
        IndexEntry other = state.addPutEntries(1, lastRecSize, Infinite_Time).get(0);
        otherPuts.add(other);
        // LS 1
        // IS 1.1
        state.addDeleteEntry(((MockId) (p5d3.getKey())));
        state.addDeleteEntry(((MockId) (p7d3.getKey())));
        state.addDeleteEntry(((MockId) (p9d3.getKey())));
        otherPuts.addAll(state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time));
        otherPuts.addAll(state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time));
        // this is where we cut off the time for compaction (retention time)
        long deleteReferenceTimeMs = (state.time.milliseconds()) + (TimeUnit.SECONDS.toMillis(1));
        // IS 1.2
        IndexEntry p4d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        state.addDeleteEntry(((MockId) (p4d1.getKey())));
        IndexEntry p6d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        state.addDeleteEntry(((MockId) (p6d1.getKey())));
        IndexEntry p4d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        // IS 1.3
        IndexEntry p8d1 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        state.addDeleteEntry(((MockId) (p8d1.getKey())));
        state.addDeleteEntry(((MockId) (p4d2.getKey())));
        IndexEntry p6d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        IndexEntry p8d2 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        // IS 1.4
        state.addDeleteEntry(((MockId) (p6d2.getKey())));
        state.addDeleteEntry(((MockId) (p8d2.getKey())));
        IndexEntry p4d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, notExpiredMs).get(0);
        IndexEntry p6d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiredMs).get(0);
        IndexEntry p8d3 = state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0);
        // IS 1.5
        lastRecSize = (state.log.getSegmentCapacity()) - (state.index.getCurrentEndOffset().getOffset());
        otherPuts.addAll(state.addPutEntries(1, lastRecSize, Infinite_Time));
        // LS 2
        // IS 2.1
        state.addDeleteEntry(((MockId) (p4d3.getKey())));
        state.addDeleteEntry(((MockId) (p6d3.getKey())));
        state.addDeleteEntry(((MockId) (p8d3.getKey())));
        // get everything except the last log segment entries out of the journal
        state.reloadIndex(true, false);
        List<String> segmentsUnderCompaction = getLogSegments(0, 2);
        long logSegmentSizeSumBeforeCompaction = getSumOfLogSegmentEndOffsets();
        CompactionDetails details = new CompactionDetails(deleteReferenceTimeMs, segmentsUnderCompaction);
        compactor = getCompactor(state.log, BlobStoreCompactorTest.DISK_IO_SCHEDULER);
        compactor.initialize(state.index);
        long logSegmentCountBeforeCompaction = state.index.getLogSegmentCount();
        try {
            compactor.compact(details, bundleReadBuffer);
        } finally {
            compactor.close(0);
        }
        Assert.assertFalse("Sum of size of log segments did not change after compaction", (logSegmentSizeSumBeforeCompaction == (getSumOfLogSegmentEndOffsets())));
        // check all delete records to make sure they remain
        for (MockId deletedKey : state.deletedKeys) {
            Assert.assertTrue((deletedKey + " should be deleted"), state.index.findKey(deletedKey).isFlagSet(Delete_Index));
            checkIndexValue(deletedKey);
        }
        // make sure all of otherPuts and p1 and p2 are ok.
        otherPuts.add(p1);
        otherPuts.add(p2);
        for (IndexEntry entry : otherPuts) {
            MockId id = ((MockId) (entry.getKey()));
            Assert.assertFalse((id + " should not be deleted"), state.index.findKey(id).isFlagSet(Delete_Index));
            checkIndexValue(id);
            try (BlobReadOptions options = state.index.getBlobReadInfo(id, EnumSet.noneOf(StoreGetOptions.class))) {
                checkRecord(id, options);
            }
        }
        // p3 should not be found
        Assert.assertNull(("There should be no record of " + (p3.getKey())), state.index.findKey(p3.getKey()));
        // no p5, p6, p7, p9 records
        IndexEntry[] cleaned = new IndexEntry[]{ p5d1, p5d2, p5d3, p6d1, p6d2, p6d3, p7d1, p7d2, p7d3, p9d1, p9d2, p9d3 };
        for (IndexEntry entry : cleaned) {
            MockId id = ((MockId) (entry.getKey()));
            IndexValue value = state.index.findKey(id);
            // the delete record should remain
            Assert.assertTrue((id + " should be deleted"), value.isFlagSet(Delete_Index));
            // the put record should be cleaned up
            Assert.assertEquals("There should no original message offset", UNKNOWN_ORIGINAL_MESSAGE_OFFSET, value.getOriginalMessageOffset());
            try {
                state.index.getBlobReadInfo(id, EnumSet.allOf(StoreGetOptions.class));
                Assert.fail(("Should not be able to GET " + id));
            } catch (StoreException e) {
                Assert.assertEquals(((id + " failed with error code ") + (e.getErrorCode())), ID_Deleted, e.getErrorCode());
            }
        }
        // put records of p4, p8 should remain
        IndexEntry[] retained = new IndexEntry[]{ p4d1, p4d2, p4d3, p8d1, p8d2, p8d3 };
        for (IndexEntry entry : retained) {
            MockId id = ((MockId) (entry.getKey()));
            IndexValue value = state.index.findKey(id);
            // the delete record should remain
            Assert.assertTrue((id + " should be deleted"), value.isFlagSet(Delete_Index));
            // the put record however should not be cleaned up
            if ((value.getOriginalMessageOffset()) == (UNKNOWN_ORIGINAL_MESSAGE_OFFSET)) {
                // PUT record should exist
                try (BlobReadOptions options = state.index.getBlobReadInfo(id, EnumSet.allOf(StoreGetOptions.class))) {
                    checkRecord(id, options);
                }
            } else {
                // PUT record exists.
            }
        }
        verifySavedBytesCount(logSegmentCountBeforeCompaction, 0);
    }

    /**
     * Tests compaction on a log that contains a PUT record that has no corresponding entry in the index (this can happen
     * due to recovery corner cases - refer to recovery code in PersistentIndex.java).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void orphanedPutRecordsTest() throws Exception {
        refreshState(false, false);
        // write a PUT record that will be "lost"
        MockId orphanedId = ((MockId) (state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time).get(0).getKey()));
        // add a delete entry for  orphanedId
        state.addDeleteEntry(orphanedId);
        // get the index value and "lose" the PUT record. This works because we get a reference to the value in the index.
        IndexValue value = state.index.findKey(orphanedId);
        value.clearOriginalMessageOffset();
        // add a put entry that spans the rest of the log segment
        long lastRecSize = (state.log.getSegmentCapacity()) - (state.index.getCurrentEndOffset().getOffset());
        state.addPutEntries(1, lastRecSize, Infinite_Time);
        // add an entry so that a new log segment is created
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        state.reloadIndex(true, false);
        List<String> segmentsUnderCompaction = getLogSegments(0, 1);
        compactAndVerify(segmentsUnderCompaction, state.time.milliseconds(), true);
        // make sure the delete still exists
        checkIndexValue(orphanedId);
        // the first log segment should not contain the the PUT that had no index entry
        LogSegment firstLogSegment = state.log.getFirstSegment();
        long size = (firstLogSegment.getEndOffset()) - (firstLogSegment.getStartOffset());
        Assert.assertEquals("Segment size not as expected", (lastRecSize + (CuratedLogIndexState.DELETE_RECORD_SIZE)), size);
    }

    /**
     * Tests cases specific to TTL updates
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ttlUpdateSpecificTest() throws Exception {
        // ensure that puts are not cleaned up if they have ttl updates
        Pair<List<MockId>, Long> idsAndExpiryTimeMs = createStateWithPutAndTtlUpdate();
        Assert.assertTrue("Current time should be beyond expiry time of blobs", ((state.time.milliseconds()) > (idsAndExpiryTimeMs.getSecond())));
        List<String> segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, state.time.milliseconds(), false);
        // compact everything
        // using expire time as the ref time
        long expiryTimeMs = createStateWithPutTtlUpdateAndDelete();
        segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, expiryTimeMs, true);
        // using delete time as the ref time
        createStateWithPutTtlUpdateAndDelete();
        state.advanceTime(TimeUnit.SECONDS.toMillis(1));
        segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, state.time.milliseconds(), true);
        // compact everything except the first log segment (all the TTL updates and deletes will be retained - no change)
        // using expire time as the ref time
        expiryTimeMs = createStateWithPutTtlUpdateAndDelete();
        segmentsUnderCompaction = getLogSegments(1, ((state.index.getLogSegmentCount()) - 2));
        compactAndVerify(segmentsUnderCompaction, expiryTimeMs, false);
        // using delete time as the ref time
        createStateWithPutTtlUpdateAndDelete();
        state.advanceTime(TimeUnit.SECONDS.toMillis(1));
        segmentsUnderCompaction = getLogSegments(1, ((state.index.getLogSegmentCount()) - 2));
        compactAndVerify(segmentsUnderCompaction, state.time.milliseconds(), false);
        // segment that has only ttl updates and deletes (no corresponding puts). All the ttl updates should be cleaned up
        Set<MockId> ids = createStateWithTtlUpdatesAndDeletes();
        state.advanceTime(TimeUnit.SECONDS.toMillis(1));
        segmentsUnderCompaction = getLogSegments(0, ((state.index.getLogSegmentCount()) - 1));
        compactAndVerify(segmentsUnderCompaction, state.time.milliseconds(), true);
        // there should be no ttl updates in the final index (but the deletes should be there)
        Set<MockId> seenIds = new HashSet<>();
        List<IndexEntry> indexEntries = new ArrayList<>();
        FindEntriesCondition condition = new FindEntriesCondition(Long.MAX_VALUE);
        AtomicLong currentTotalSize = new AtomicLong(0);
        for (IndexSegment segment : state.index.getIndexSegments().values()) {
            if ((LogSegmentNameHelper.getGeneration(segment.getLogSegmentName())) == 0) {
                break;
            }
            segment.getIndexEntriesSince(null, condition, indexEntries, currentTotalSize, false);
        }
        indexEntries.forEach(( entry) -> {
            assertTrue("There cannot be a non-delete entry", entry.getValue().isFlagSet(IndexValue.Flags.Delete_Index));
            assertTrue("Every key should be seen only once", seenIds.add(((MockId) (entry.getKey()))));
        });
        Assert.assertEquals("All ids not present", ids, seenIds);
    }

    /**
     * Tests some recovery scenarios related to TTL update records in particular
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ttlUpdateSpecificRecoveryTest() throws Exception {
        bundleReadBuffer = null;
        // close testing
        doTtlUpdateSrcDupTest();
        doTtlUpdateTgtDupTest();
        // crash testing
        throwExceptionInsteadOfClose = true;
        doTtlUpdateSrcDupTest();
        doTtlUpdateTgtDupTest();
    }

    /**
     * Extension of {@link DiskIOScheduler} that interrupts the compaction process based on provided parameters.
     */
    private class InterruptionInducingDiskIOScheduler extends DiskIOScheduler {
        private final int indexSegmentCountToCutoffAt;

        private final long numBytesToCutoffAt;

        private int indexSegmentsCopied = 0;

        private long numBytesCopied = 0;

        /**
         * Creates an instance of InterruptionInducingDiskIOScheduler.
         *
         * @param indexSegmentCountToCutoffAt
         * 		interrupts once these many index segments have been reported copied.
         * @param numBytesToCutoffAt
         * 		interrupts once these many bytes have been reported copied.
         */
        InterruptionInducingDiskIOScheduler(int indexSegmentCountToCutoffAt, long numBytesToCutoffAt) {
            super(null);
            this.indexSegmentCountToCutoffAt = indexSegmentCountToCutoffAt;
            this.numBytesToCutoffAt = numBytesToCutoffAt;
        }

        @Override
        long getSlice(String jobType, String jobId, long usedSinceLastCall) {
            if (jobType.equals(INDEX_SEGMENT_READ_JOB_NAME)) {
                indexSegmentsCopied += usedSinceLastCall;
            } else
                if (jobType.equals(COMPACTION_CLEANUP_JOB_NAME)) {
                    numBytesCopied += usedSinceLastCall;
                }

            if (((indexSegmentsCopied) == (indexSegmentCountToCutoffAt)) || ((numBytesCopied) >= (numBytesToCutoffAt))) {
                closeCompactorOrThrowException();
            }
            return Long.MAX_VALUE;
        }
    }

    /**
     * Extension of {@link PersistentIndex} that interrupts the compaction when index commit is being executed.
     */
    private class InterruptionInducingIndex extends PersistentIndex {
        InterruptionInducingIndex() throws StoreException {
            super(tempDirStr, tempDirStr, state.scheduler, state.log, new com.github.ambry.config.StoreConfig(new VerifiableProperties(state.properties)), CuratedLogIndexState.STORE_KEY_FACTORY, state.recovery, state.hardDelete, CuratedLogIndexState.DISK_IO_SCHEDULER, new StoreMetrics(new MetricRegistry()), state.time, state.sessionId, state.incarnationId);
        }

        @Override
        void changeIndexSegments(List<File> segmentFilesToAdd, Set<Offset> segmentsToRemove) throws StoreException {
            throwExceptionIfRequired();
            super.changeIndexSegments(segmentFilesToAdd, segmentsToRemove);
            closeCompactorOrThrowException();
        }
    }

    /**
     * Extension of {@link Log} that interrupts the compaction when a certain number of calls to
     * {@link #addSegment(LogSegment, boolean)} or {@link #dropSegment(String, boolean)} have been made.
     */
    private class InterruptionInducingLog extends Log {
        private final int addSegmentCallCountToInterruptAt;

        private final int dropSegmentCallCountToInterruptAt;

        private int segmentsAdded = 0;

        private int segmentsDropped = 0;

        /**
         * Creates an instance of InterruptionInducingLog.
         *
         * @param addSegmentCallCountToInterruptAt
         * 		number of allowed calls to {@link #addSegment(LogSegment, boolean)}.
         * @param dropSegmentCallCountToInterruptAt
         * 		number of allowed calls to {@link #dropSegment(String, boolean)}.
         * @throws IOException
         * 		
         */
        InterruptionInducingLog(int addSegmentCallCountToInterruptAt, int dropSegmentCallCountToInterruptAt) throws IOException {
            super(tempDirStr, state.log.getCapacityInBytes(), state.log.getSegmentCapacity(), StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, new StoreMetrics(new MetricRegistry()));
            // set end offsets correctly
            LogSegment original = state.log.getFirstSegment();
            while (original != null) {
                LogSegment dup = getSegment(original.getName());
                dup.setEndOffset(original.getEndOffset());
                original = state.log.getNextSegment(original);
            } 
            if ((addSegmentCallCountToInterruptAt <= 0) || (dropSegmentCallCountToInterruptAt <= 0)) {
                throw new IllegalArgumentException("Arguments cannot be <= 0");
            }
            this.addSegmentCallCountToInterruptAt = addSegmentCallCountToInterruptAt;
            this.dropSegmentCallCountToInterruptAt = dropSegmentCallCountToInterruptAt;
        }

        @Override
        void addSegment(LogSegment segment, boolean increaseUsedSegmentCount) {
            (segmentsAdded)++;
            if ((segmentsAdded) == (addSegmentCallCountToInterruptAt)) {
                throwExceptionIfRequired();
            }
            super.addSegment(segment, increaseUsedSegmentCount);
            if ((segmentsAdded) == (addSegmentCallCountToInterruptAt)) {
                closeCompactorOrThrowException();
            }
        }

        @Override
        void dropSegment(String segmentName, boolean decreaseUsedSegmentCount) throws IOException {
            (segmentsDropped)++;
            if ((segmentsDropped) == (dropSegmentCallCountToInterruptAt)) {
                throwExceptionIfRequired();
            }
            super.dropSegment(segmentName, decreaseUsedSegmentCount);
            if ((segmentsDropped) == (dropSegmentCallCountToInterruptAt)) {
                closeCompactorOrThrowException();
            }
        }
    }

    /**
     * A representation of a log entry.
     */
    private class LogEntry {
        /**
         * The ID of the entry.
         */
        MockId id;

        /**
         * The type of the entry.
         */
        IndexEntryType entryType;

        long size;

        /**
         * Create an instance with {@code id} and {@code entryType}
         *
         * @param id
         * 		the {@link MockId} of the entry.
         * @param entryType
         * 		the type of the entry.
         * @param size
         * 		the size of the log entry.
         */
        LogEntry(MockId id, PersistentIndex.IndexEntryType entryType, long size) {
            this.id = id;
            this.entryType = entryType;
            this.size = size;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            BlobStoreCompactorTest.LogEntry logEntry = ((BlobStoreCompactorTest.LogEntry) (o));
            return ((id.equals(logEntry.id)) && ((entryType) == (logEntry.entryType))) && ((size) == (logEntry.size));
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + (entryType.hashCode());
            result = (31 * result) + ((int) (size));
            return result;
        }

        @Override
        public String toString() {
            return ((((("[id: " + (id)) + " entryType: ") + (entryType)) + " size: ") + (size)) + "]";
        }
    }
}

