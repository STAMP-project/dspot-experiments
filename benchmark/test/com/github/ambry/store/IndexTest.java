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


import IndexSegment.BLOOM_FILE_NAME_SUFFIX;
import IndexSegment.INDEX_SEGMENT_FILE_NAME_SUFFIX;
import PersistentIndex.INDEX_SEGMENT_FILE_COMPARATOR;
import PersistentIndex.INDEX_SEGMENT_FILE_FILTER;
import PersistentIndex.IndexEntryType;
import PersistentIndex.IndexEntryType.DELETE;
import PersistentIndex.IndexEntryType.PUT;
import PersistentIndex.IndexPersistor;
import PersistentIndex.VERSION_0;
import PersistentIndex.VERSION_1;
import StoreErrorCodes.Already_Updated;
import StoreErrorCodes.ID_Deleted;
import StoreErrorCodes.ID_Not_Found;
import StoreErrorCodes.Illegal_Index_State;
import StoreErrorCodes.Index_Creation_Failure;
import StoreErrorCodes.Initialization_Error;
import StoreErrorCodes.TTL_Expired;
import StoreErrorCodes.Unknown_Error;
import StoreGetOptions.Store_Include_Deleted;
import StoreGetOptions.Store_Include_Expired;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.config.StoreConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static IndexValue.FLAGS_DEFAULT_VALUE;
import static IndexValue.INDEX_VALUE_SIZE_IN_BYTES_V1;
import static StoreErrorCodes.ID_Deleted;
import static StoreErrorCodes.TTL_Expired;


/**
 * Tests for {@link PersistentIndex}. Tests both segmented and non segmented log use cases.
 */
@RunWith(Parameterized.class)
public class IndexTest {
    private final boolean isLogSegmented;

    private final File tempDir;

    private final CuratedLogIndexState state;

    /**
     * Creates a temporary directory and sets up some test state.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    public IndexTest(boolean isLogSegmented) throws StoreException, IOException {
        this.isLogSegmented = isLogSegmented;
        tempDir = StoreTestUtils.createTempDirectory(("indexDir-" + (UtilsTest.getRandomString(10))));
        state = new CuratedLogIndexState(isLogSegmented, tempDir, true);
    }

    /**
     * Tests for {@link PersistentIndex#findKey(StoreKey)}.
     * Cases:
     * 1. Live keys
     * 2. Expired keys
     * 3. Deleted keys
     * 4. Non existent keys
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void findKeyTest() throws StoreException {
        for (MockId id : state.allKeys.keySet()) {
            IndexValue value = state.index.findKey(id);
            verifyValue(id, value);
        }
        // search for a non existent key
        MockId nonExistentId = state.getUniqueId();
        verifyValue(nonExistentId, state.index.findKey(nonExistentId));
    }

    /**
     * Tests for {@link PersistentIndex#findKey(StoreKey, FileSpan, EnumSet)}.
     * Cases:
     * 1. FileSpan exactly that of the message
     * 2. FileSpan before and after message including it on the boundary
     * 3. FileSpan that includes the message.
     * 4. FileSpan before and after message not including it
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void findKeyWithFileSpanTest() throws StoreException {
        for (MockId id : state.allKeys.keySet()) {
            doFindKeyWithFileSpanTest(id, EnumSet.of(PUT, DELETE));
            doFindKeyWithFileSpanTest(id, EnumSet.allOf(IndexEntryType.class));
            for (PersistentIndex.IndexEntryType type : IndexEntryType.values()) {
                doFindKeyWithFileSpanTest(id, EnumSet.of(type));
            }
        }
    }

    /**
     * Tests {@link PersistentIndex#getBlobReadInfo(StoreKey, EnumSet)}.
     * Cases (all cases on all types of keys - live, expired, deleted):
     * 1. With no options
     * 2. With combinations of parameters in {@link StoreGetOptions}.
     * </p>
     * Also tests non existent keys.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void getBlobReadInfoTest() throws StoreException {
        final AtomicReference<MockId> idRequested = new AtomicReference<>();
        state.hardDelete = new MessageStoreHardDelete() {
            @Override
            public Iterator<HardDeleteInfo> getHardDeleteMessages(MessageReadSet readSet, StoreKeyFactory factory, List<byte[]> recoveryInfoList) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public MessageInfo getMessageInfo(Read read, long offset, StoreKeyFactory factory) throws IOException {
                MockId id = idRequested.get();
                if (id == null) {
                    throw new IllegalStateException("No ID was set before making a call to getBlobReadInfo()");
                }
                IndexValue value = state.getExpectedValue(id, true);
                return new MessageInfo(id, value.getSize(), value.getExpiresAtMs(), value.getAccountId(), value.getContainerId(), value.getOperationTimeInMs());
            }
        };
        state.reloadIndex(true, false);
        List<EnumSet<StoreGetOptions>> allCombos = new ArrayList<>();
        allCombos.add(EnumSet.noneOf(StoreGetOptions.class));
        allCombos.add(EnumSet.of(Store_Include_Expired));
        allCombos.add(EnumSet.of(Store_Include_Deleted));
        allCombos.add(EnumSet.allOf(StoreGetOptions.class));
        for (MockId id : state.allKeys.keySet()) {
            idRequested.set(id);
            for (EnumSet<StoreGetOptions> getOptions : allCombos) {
                if (state.liveKeys.contains(id)) {
                    verifyBlobReadOptions(id, getOptions, null);
                } else
                    if (state.expiredKeys.contains(id)) {
                        StoreErrorCodes expectedErrorCode = (getOptions.contains(Store_Include_Expired)) ? null : TTL_Expired;
                        verifyBlobReadOptions(id, getOptions, expectedErrorCode);
                    } else
                        if (state.deletedKeys.contains(id)) {
                            StoreErrorCodes expectedErrorCode = (((state.getExpectedValue(id, true)) != null) && (getOptions.contains(Store_Include_Deleted))) ? null : ID_Deleted;
                            verifyBlobReadOptions(id, getOptions, expectedErrorCode);
                        }


            }
        }
        // try to get BlobReadOption for a non existent key
        MockId nonExistentId = state.getUniqueId();
        verifyBlobReadOptions(nonExistentId, EnumSet.allOf(StoreGetOptions.class), ID_Not_Found);
    }

    /**
     * Tests {@link PersistentIndex#findMissingKeys(List)}.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void findMissingKeysTest() throws StoreException {
        List<StoreKey> idsToProvide = new ArrayList<StoreKey>(state.allKeys.keySet());
        Set<StoreKey> nonExistentIds = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            nonExistentIds.add(state.getUniqueId());
        }
        idsToProvide.addAll(nonExistentIds);
        Collections.shuffle(idsToProvide);
        Set<StoreKey> missingKeys = state.index.findMissingKeys(idsToProvide);
        Assert.assertEquals("Set of missing keys not as expected", nonExistentIds, missingKeys);
    }

    /**
     * Tests error cases for {@link PersistentIndex#addToIndex(IndexEntry, FileSpan)}.
     * Cases:
     * 1. FileSpan end offset < currentIndexEndOffset
     * 2. FileSpan is across segments
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void addEntryBadInputTest() throws StoreException {
        // FileSpan end offset < currentIndexEndOffset
        FileSpan fileSpan = state.log.getFileSpanForMessage(state.index.getStartOffset(), 1);
        IndexValue value = new IndexValue(1, state.index.getStartOffset(), FLAGS_DEFAULT_VALUE, Utils.Infinite_Time, state.time.milliseconds(), Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM));
        try {
            state.index.addToIndex(new IndexEntry(state.getUniqueId(), value), fileSpan);
            Assert.fail("Should have failed because filespan provided < currentIndexEndOffset");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        if (isLogSegmented) {
            // FileSpan spans across segments
            Offset startOffset = state.index.getCurrentEndOffset();
            String nextLogSegmentName = LogSegmentNameHelper.getNextPositionName(startOffset.getName());
            Offset endOffset = new Offset(nextLogSegmentName, 0);
            fileSpan = new FileSpan(startOffset, endOffset);
            try {
                state.index.addToIndex(new IndexEntry(state.getUniqueId(), value), fileSpan);
                Assert.fail("Should have failed because fileSpan provided spanned across segments");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
    }

    /**
     * Tests error cases for {@link PersistentIndex#markAsDeleted(StoreKey, FileSpan, long)}.
     * Cases
     * 1. FileSpan end offset < currentIndexEndOffset
     * 2. FileSpan is across segments
     * 3. ID does not exist
     * 4. ID already deleted
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void markAsDeletedBadInputTest() throws StoreException, IOException {
        // FileSpan end offset < currentIndexEndOffset
        FileSpan fileSpan = state.log.getFileSpanForMessage(state.index.getStartOffset(), 1);
        try {
            state.index.markAsDeleted(state.liveKeys.iterator().next(), fileSpan, state.time.milliseconds());
            Assert.fail("Should have failed because filespan provided < currentIndexEndOffset");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        if (isLogSegmented) {
            // FileSpan spans across segments
            Offset startOffset = state.index.getCurrentEndOffset();
            String nextLogSegmentName = LogSegmentNameHelper.getNextPositionName(startOffset.getName());
            Offset endOffset = new Offset(nextLogSegmentName, 0);
            fileSpan = new FileSpan(startOffset, endOffset);
            try {
                state.index.markAsDeleted(state.liveKeys.iterator().next(), fileSpan, state.time.milliseconds());
                Assert.fail("Should have failed because fileSpan provided spanned across segments");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
        state.appendToLog(5);
        fileSpan = state.log.getFileSpanForMessage(state.index.getCurrentEndOffset(), 5);
        // ID does not exist
        try {
            state.index.markAsDeleted(state.getUniqueId(), fileSpan, state.time.milliseconds());
            Assert.fail("Should have failed because ID provided for delete does not exist");
        } catch (StoreException e) {
            Assert.assertEquals("Unexpected StoreErrorCode", ID_Not_Found, e.getErrorCode());
        }
        // ID already deleted
        try {
            state.index.markAsDeleted(state.deletedKeys.iterator().next(), fileSpan, state.time.milliseconds());
            Assert.fail("Should have failed because ID provided for delete is already deleted");
        } catch (StoreException e) {
            Assert.assertEquals("Unexpected StoreErrorCode", ID_Deleted, e.getErrorCode());
        }
    }

    /**
     * Tests that hard delete is kicked off by the index.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void hardDeleteKickOffTest() throws StoreException {
        Assert.assertFalse("HardDelete should not be enabled", state.index.hardDeleter.isRunning());
        state.properties.put("store.enable.hard.delete", "true");
        state.reloadIndex(true, false);
        Assert.assertTrue("HardDelete is not enabled", state.index.hardDeleter.isRunning());
    }

    /**
     * Tests that hard delete zeros out blobs correctly and makes progress as expected.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void hardDeleteTest() throws StoreException, IOException {
        state.properties.put("store.deleted.message.retention.days", Integer.toString(1));
        state.properties.put("store.hard.delete.operations.bytes.per.sec", Integer.toString(((Integer.MAX_VALUE) / 10)));
        state.reloadIndex(true, false);
        state.index.hardDeleter.enabled.set(true);
        Assert.assertFalse("Hard delete did work even though no message is past retention time", state.index.hardDeleter.hardDelete());
        // IndexSegment still uses real time so advance time so that it goes 2 days past the real time.
        state.advanceTime(((SystemTime.getInstance().milliseconds()) + (TimeUnit.DAYS.toMillis(2))));
        Assert.assertTrue("Hard delete did not do any work", state.index.hardDeleter.hardDelete());
        long expectedProgress = state.index.getAbsolutePositionInLogForOffset(state.logOrder.lastKey());
        Assert.assertEquals("Hard delete did not make expected progress", expectedProgress, state.index.hardDeleter.getProgress());
        state.verifyEntriesForHardDeletes(state.deletedKeys);
    }

    /**
     * Tests that hard delete pause and resume where in hard deletes is done by the daemon thread and not explicitly
     * invoked by the tests
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void hardDeletePauseResumeTest() throws StoreException, IOException, InterruptedException {
        testHardDeletePauseResume(false);
    }

    /**
     * Tests that hard delete pause and resume with reloading of index, where in hard deletes is done by the daemon thread
     * and not explicitly invoked by the tests
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void hardDeletePauseResumeRestartTest() throws StoreException, IOException, InterruptedException {
        testHardDeletePauseResume(true);
    }

    /**
     * Tests that expired values are correctly handled.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void expirationTest() throws StoreException, IOException {
        // add a PUT entry that will expire if time advances
        // advance time so that time moves to whole second with no residual milliseconds
        state.time.sleep(((Time.MsPerSec) - (state.time.milliseconds())));
        long expiresAtMs = ((state.time.milliseconds()) + (CuratedLogIndexState.DELAY_BETWEEN_LAST_MODIFIED_TIMES_MS)) + 1000;
        state.addPutEntries(1, 1, expiresAtMs);
        MockId id = state.logOrder.lastEntry().getValue().getFirst();
        verifyBlobReadOptions(id, EnumSet.noneOf(StoreGetOptions.class), null);
        state.advanceTime(((expiresAtMs - (state.time.milliseconds())) + (TimeUnit.SECONDS.toMillis(1))));
        verifyBlobReadOptions(id, EnumSet.noneOf(StoreGetOptions.class), TTL_Expired);
    }

    /**
     * Tests that end offsets are set correctly in log segments when the index is created.
     * Cases
     * 1. Current end offsets have been set correctly
     * 2. Add data to log but not index, restart and check that end offsets have been reset.
     * 3. Add data to log such that a new log segment is created but not to index, restart and check that the new log
     * segment is gone.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void setEndOffsetsTest() throws StoreException, IOException {
        // check that current end offsets set are correct
        LogSegment segment = state.log.getFirstSegment();
        while (segment != null) {
            Offset lastRecordStartOffset = getLastRecordOffset(segment);
            long size = state.logOrder.get(lastRecordStartOffset).getSecond().indexValue.getSize();
            Offset expectedEndOffset = state.log.getFileSpanForMessage(lastRecordStartOffset, size).getEndOffset();
            Assert.assertEquals("End offset of segment not as expected", expectedEndOffset.getOffset(), segment.getEndOffset());
            segment = state.log.getNextSegment(segment);
        } 
        // write some data to the log but not the index, check that end offset of the segment has changed
        // reload the index and check the end offset has been reset
        LogSegment activeSegment = state.log.getSegment(state.index.getCurrentEndOffset().getName());
        long offsetBeforeAppend = activeSegment.getEndOffset();
        state.appendToLog(CuratedLogIndexState.PUT_RECORD_SIZE);
        Assert.assertEquals("End offset of active segment did not change", (offsetBeforeAppend + (CuratedLogIndexState.PUT_RECORD_SIZE)), activeSegment.getEndOffset());
        state.reloadIndex(true, false);
        Assert.assertEquals("End offset of active segment should have been reset", offsetBeforeAppend, activeSegment.getEndOffset());
        if (isLogSegmented) {
            // this test works under the assumption that log segments are not allocated until they are required
            // this is a fair assumption because the PersistentIndex works under the same assumption and would break if it
            // were not true (which this test failing would indicate).
            // write some data to the log but not the index such that new segment is created, check that end offset of the
            // segment has changed and a new segment created, reload the index and check the end offset has been reset and
            // the new segment does not exist.
            activeSegment = state.log.getSegment(state.index.getCurrentEndOffset().getName());
            offsetBeforeAppend = activeSegment.getEndOffset();
            // fill up this segment
            state.appendToLog(((activeSegment.getCapacityInBytes()) - (activeSegment.getEndOffset())));
            Assert.assertEquals("End offset of active segment did not change", activeSegment.getCapacityInBytes(), activeSegment.getEndOffset());
            // write a little more so that a new segment is created
            state.appendToLog(CuratedLogIndexState.PUT_RECORD_SIZE);
            LogSegment nextActiveSegment = state.log.getNextSegment(activeSegment);
            Assert.assertNotNull("New segment has not been created", nextActiveSegment);
            Assert.assertEquals("Unexpected end offset for new segment", CuratedLogIndexState.PUT_RECORD_SIZE, ((nextActiveSegment.getEndOffset()) - (nextActiveSegment.getStartOffset())));
            state.reloadIndex(true, false);
            // there should no longer be a "next" segment to the old active segment
            Assert.assertNull("There should have been no more segments", state.log.getNextSegment(activeSegment));
            Assert.assertEquals("End offset of active segment should have been reset", offsetBeforeAppend, activeSegment.getEndOffset());
        }
    }

    /**
     * Tests {@link PersistentIndex#changeIndexSegments(List, Set)} for good and bad cases.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void changeIndexSegmentsTest() throws StoreException {
        ConcurrentSkipListMap<Offset, IndexSegment> indexes = state.index.getIndexSegments();
        Set<Offset> saved = indexes.clone().keySet();
        Map<Offset, IndexSegment> toRemoveOne = new HashMap<>();
        Set<Offset> toRetainOne = new HashSet<>();
        partitionIndexSegments(toRemoveOne, toRetainOne);
        // remove the first batch without adding anything
        state.index.changeIndexSegments(Collections.EMPTY_LIST, toRemoveOne.keySet());
        Assert.assertEquals("Offsets in index do not match expected", toRetainOne, state.index.getIndexSegments().keySet());
        // make sure persist does not throw errors
        state.index.persistIndex();
        Map<Offset, IndexSegment> toRemoveTwo = new HashMap<>();
        Set<Offset> toRetainTwo = new HashSet<>();
        partitionIndexSegments(toRemoveTwo, toRetainTwo);
        // remove the second batch and add the batch that was removed earlier
        toRetainTwo.addAll(toRemoveOne.keySet());
        List<File> filesToAdd = new ArrayList<>();
        for (IndexSegment indexSegment : toRemoveOne.values()) {
            filesToAdd.add(indexSegment.getFile());
        }
        state.index.changeIndexSegments(filesToAdd, toRemoveTwo.keySet());
        Assert.assertEquals("Offsets in index do not match expected", toRetainTwo, state.index.getIndexSegments().keySet());
        // make sure persist does not throw errors
        state.index.persistIndex();
        // add the second batch that was removed
        filesToAdd.clear();
        for (IndexSegment indexSegment : toRemoveTwo.values()) {
            filesToAdd.add(indexSegment.getFile());
        }
        state.index.changeIndexSegments(filesToAdd, Collections.EMPTY_SET);
        Assert.assertEquals("Offsets in index do not match expected", saved, state.index.getIndexSegments().keySet());
        // make sure persist does not throw errors
        state.index.persistIndex();
        // error case
        // try to remove the last segment (its offset is in the journal)
        try {
            state.index.changeIndexSegments(Collections.EMPTY_LIST, Collections.singleton(state.referenceIndex.lastKey()));
            Assert.fail("Should have failed to remove index segment because start offset is past the first offset in the journal");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // add an entry into the journal for the very first offset in the index
        state.index.journal.addEntry(state.logOrder.firstKey(), state.logOrder.firstEntry().getValue().getFirst());
        // remove the first index segment
        IndexSegment firstSegment = state.index.getIndexSegments().remove(state.index.getIndexSegments().firstKey());
        // try to add it back and it should result in an error
        try {
            state.index.changeIndexSegments(Collections.singletonList(firstSegment.getFile()), Collections.EMPTY_SET);
            Assert.fail("Should have failed to add index segment because its end offset is past the first offset in the journal");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Test that verifies that there are no concurrency issues with the execution of
     * {@link PersistentIndex#addToIndex(IndexEntry, FileSpan)} and {@link PersistentIndex#changeIndexSegments(List, Set)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void addToIndexAndChangeIndexSegmentsConcurrencyTest() throws Exception {
        long ITERATIONS = 500;
        final Set<Offset> indexSegmentStartOffsets = new HashSet(state.referenceIndex.keySet());
        final AtomicReference<Offset> logEndOffset = new AtomicReference(state.log.getEndOffset());
        final AtomicReference<Exception> exception = new AtomicReference<>(null);
        final AtomicReference<CountDownLatch> latch = new AtomicReference<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // make sure rollover occurs on every index entry
        state.properties.put("store.index.max.number.of.inmem.elements", "1");
        state.reloadIndex(true, false);
        state.appendToLog(ITERATIONS);
        final Set<IndexEntry> entriesAdded = Collections.newSetFromMap(new ConcurrentHashMap<IndexEntry, Boolean>());
        Runnable adder = new Runnable() {
            @Override
            public void run() {
                try {
                    FileSpan fileSpan = state.log.getFileSpanForMessage(logEndOffset.get(), 1);
                    IndexValue value = new IndexValue(1, fileSpan.getStartOffset(), Utils.Infinite_Time, state.time.milliseconds(), Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM));
                    IndexEntry entry = new IndexEntry(state.getUniqueId(), value);
                    state.index.addToIndex(entry, fileSpan);
                    logEndOffset.set(fileSpan.getEndOffset());
                    entriesAdded.add(entry);
                    indexSegmentStartOffsets.add(fileSpan.getStartOffset());
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    latch.get().countDown();
                }
            }
        };
        final List<IndexSegment> candidateIndexSegments = new ArrayList<>();
        for (IndexSegment indexSegment : state.index.getIndexSegments().values()) {
            if ((indexSegment.getEndOffset().compareTo(state.index.journal.getFirstOffset())) < 0) {
                candidateIndexSegments.add(indexSegment);
            }
        }
        Runnable changer = new Runnable() {
            @Override
            public void run() {
                try {
                    int idx = RANDOM.nextInt(candidateIndexSegments.size());
                    IndexSegment segmentToUse = candidateIndexSegments.get(idx);
                    // remove the index segment
                    state.index.changeIndexSegments(Collections.EMPTY_LIST, Collections.singleton(segmentToUse.getStartOffset()));
                    // ensure that the relevant index segment is gone.
                    if (state.index.getIndexSegments().containsKey(segmentToUse.getStartOffset())) {
                        throw new IllegalStateException(((("Segment with offset " + (segmentToUse.getStartOffset())) + " should have been") + " removed."));
                    }
                    // add it back
                    state.index.changeIndexSegments(Collections.singletonList(segmentToUse.getFile()), Collections.EMPTY_SET);
                    // ensure that the relevant index segment is back.
                    if (!(state.index.getIndexSegments().containsKey(segmentToUse.getStartOffset()))) {
                        throw new IllegalStateException(((("Segment with offset " + (segmentToUse.getStartOffset())) + " should have been") + " present."));
                    }
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    latch.get().countDown();
                }
            }
        };
        for (int i = 0; i < ITERATIONS; i++) {
            latch.set(new CountDownLatch(2));
            executorService.submit(adder);
            executorService.submit(changer);
            Assert.assertTrue("Took too long to add/change index segments", latch.get().await(1, TimeUnit.SECONDS));
            if ((exception.get()) != null) {
                throw exception.get();
            }
            Assert.assertEquals("Index segment start offsets do not match expected", indexSegmentStartOffsets, state.index.getIndexSegments().keySet());
        }
    }

    /**
     * Tests success cases for recovery.
     * Cases
     * 1. Single segment recovery
     * 2. Multiple segment recovery
     * 3. Recovery after index is completely lost
     * In all cases, the tests also verify that end offsets are set correctly.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void recoverySuccessTest() throws StoreException, IOException {
        state.advanceTime(1);
        singleSegmentRecoveryTest();
        if (isLogSegmented) {
            multipleSegmentRecoveryTest();
        }
        totalIndexLossRecoveryTest();
    }

    /**
     * Tests recovery failure cases.
     * Cases
     * 1. Recovery info contains a PUT for a key that already exists
     * 2. Recovery info contains a PUT for a key that has been deleted
     * 3. Recovery info contains a DELETE for a key that has been deleted
     * 4. Recovery info that contains a DELETE for a key that has no PUT record
     * 5. Recovery info that contains a PUT beyond the end offset of the log segment
     */
    @Test
    public void recoveryFailureTest() {
        // recovery info contains a PUT for a key that already exists
        MessageInfo info = new MessageInfo(state.liveKeys.iterator().next(), CuratedLogIndexState.PUT_RECORD_SIZE, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), Utils.Infinite_Time);
        doRecoveryFailureTest(info, Initialization_Error);
        // recovery info contains a PUT for a key that has been deleted
        info = new MessageInfo(state.deletedKeys.iterator().next(), CuratedLogIndexState.PUT_RECORD_SIZE, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), Utils.Infinite_Time);
        doRecoveryFailureTest(info, Initialization_Error);
        // recovery info contains a Ttl Update for a key that does not exist and there is no delete info that follows
        MockId nonExistentId = state.getUniqueId();
        info = new MessageInfo(nonExistentId, CuratedLogIndexState.TTL_UPDATE_RECORD_SIZE, false, true, nonExistentId.getAccountId(), nonExistentId.getContainerId(), state.time.milliseconds());
        doRecoveryFailureTest(info, Initialization_Error);
        // recovery info contains a Ttl Update for a key that is already Ttl updated
        MockId updatedId = null;
        for (MockId id : state.ttlUpdatedKeys) {
            if (state.liveKeys.contains(id)) {
                updatedId = id;
                break;
            }
        }
        Assert.assertNotNull("There is no key that is ttl updated but not deleted", updatedId);
        info = new MessageInfo(updatedId, CuratedLogIndexState.TTL_UPDATE_RECORD_SIZE, false, true, updatedId.getAccountId(), updatedId.getContainerId(), state.time.milliseconds());
        doRecoveryFailureTest(info, Already_Updated);
        // recovery info contains a Ttl Update for a key that is already deleted
        MockId deletedId = state.deletedKeys.iterator().next();
        info = new MessageInfo(deletedId, CuratedLogIndexState.TTL_UPDATE_RECORD_SIZE, false, true, deletedId.getAccountId(), deletedId.getContainerId(), state.time.milliseconds());
        doRecoveryFailureTest(info, ID_Deleted);
        // recovery info contains a DELETE for a key that has been deleted
        info = new MessageInfo(state.deletedKeys.iterator().next(), CuratedLogIndexState.DELETE_RECORD_SIZE, true, false, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), state.time.milliseconds());
        doRecoveryFailureTest(info, ID_Deleted);
        // recovery info that contains a PUT beyond the end offset of the log segment
        info = new MessageInfo(state.getUniqueId(), CuratedLogIndexState.PUT_RECORD_SIZE, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), Utils.Infinite_Time);
        doRecoveryFailureTest(info, Index_Creation_Failure);
    }

    /**
     * Tests {@link PersistentIndex#findEntriesSince(FindToken, long)} for various cases
     * 1. All cases that result in getting an index based token
     * 2. All cases that result in getting a journal based token
     * 3. Getting entries one by one
     * 4. Getting entries using an index based token for an offset in the journal
     * 5. Error case - trying to findEntriesSince() using an index based token that contains the last index segment
     * 6. Using findEntriesSince() in an empty index
     * 7. Token that has the log end offset
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void findEntriesSinceTest() throws StoreException, IOException {
        // add some more entries so that the journal gets entries across segments and doesn't start at the beginning
        // of an index segment.
        state.addPutEntries(7, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        state.addDeleteEntry(state.getIdToDeleteFromIndexSegment(state.referenceIndex.lastKey(), false));
        // token with log end offset should not return anything
        StoreFindToken token = new StoreFindToken(state.log.getEndOffset(), state.sessionId, state.incarnationId, false);
        token.setBytesRead(state.index.getLogUsedCapacity());
        doFindEntriesSinceTest(token, Long.MAX_VALUE, Collections.EMPTY_SET, token);
        findEntriesSinceToIndexBasedTest();
        findEntriesSinceToJournalBasedTest();
        findEntriesSinceOneByOneTest();
        findEntriesSinceIndexBasedTokenForOffsetInJournalTest();
        // error case - can never have provided an index based token that is contains the offset of the last segment
        token = new StoreFindToken(state.referenceIndex.lastEntry().getValue().firstKey(), state.referenceIndex.lastKey(), state.sessionId, state.incarnationId);
        doFindEntriesSinceFailureTest(token, Unknown_Error);
        findEntriesSinceInEmptyIndexTest(false);
        findEntriesSinceTtlUpdateCornerCaseTest();
    }

    /**
     * Tests behaviour of {@link PersistentIndex#findEntriesSince(FindToken, long)} on crash-restart of index and some
     * recovery. Specifically tests cases where tokens have been handed out before the "crash" failure.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void findEntriesSinceOnRestartTest() throws StoreException, IOException {
        Offset lastRecordOffset = state.index.journal.getLastOffset();
        state.appendToLog((2 * (CuratedLogIndexState.PUT_RECORD_SIZE)));
        // this record will be recovered.
        FileSpan firstRecordFileSpan = state.log.getFileSpanForMessage(state.index.getCurrentEndOffset(), CuratedLogIndexState.PUT_RECORD_SIZE);
        // this record will not be recovered.
        FileSpan secondRecordFileSpan = state.log.getFileSpanForMessage(firstRecordFileSpan.getEndOffset(), CuratedLogIndexState.PUT_RECORD_SIZE);
        // if there is no bad shutdown but the store token is past the index end offset, it is an error state
        StoreFindToken startToken = new StoreFindToken(secondRecordFileSpan.getStartOffset(), new UUID(1, 1), state.incarnationId, false);
        doFindEntriesSinceFailureTest(startToken, Unknown_Error);
        UUID oldSessionId = state.sessionId;
        final MockId newId = state.getUniqueId();
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long operationTimeMs = state.time.milliseconds();
        // add to allKeys() so that doFindEntriesSinceTest() works correctly.
        IndexValue putValue = new IndexValue(CuratedLogIndexState.PUT_RECORD_SIZE, firstRecordFileSpan.getStartOffset(), Utils.Infinite_Time, operationTimeMs, accountId, containerId);
        state.allKeys.computeIfAbsent(newId, ( k) -> new TreeSet<>()).add(putValue);
        state.recovery = new MessageStoreRecovery() {
            @Override
            public List<MessageInfo> recover(Read read, long startOffset, long endOffset, StoreKeyFactory factory) throws IOException {
                return Collections.singletonList(new MessageInfo(newId, CuratedLogIndexState.PUT_RECORD_SIZE, accountId, containerId, operationTimeMs));
            }
        };
        state.reloadIndex(true, true);
        // If there is no incarnationId in the incoming token, for backwards compatibility purposes we consider it as valid
        // and proceed with session id validation and so on.
        UUID[] incarnationIds = new UUID[]{ state.incarnationId, null };
        for (UUID incarnationIdToTest : incarnationIds) {
            long bytesRead = state.index.getAbsolutePositionInLogForOffset(firstRecordFileSpan.getEndOffset());
            // create a token that will be past the index end offset on startup after recovery.
            if (incarnationIdToTest == null) {
                startToken = getTokenWithNullIncarnationId(new StoreFindToken(secondRecordFileSpan.getEndOffset(), oldSessionId, state.incarnationId, false));
                Assert.assertNull("IncarnationId is expected to be null ", startToken.getIncarnationId());
            } else {
                startToken = new StoreFindToken(secondRecordFileSpan.getEndOffset(), oldSessionId, incarnationIdToTest, false);
            }
            // token should get reset internally, no keys should be returned and the returned token should be correct (offset
            // in it will be the current log end offset = firstRecordFileSpan.getEndOffset()).
            StoreFindToken expectedEndToken = new StoreFindToken(firstRecordFileSpan.getEndOffset(), state.sessionId, state.incarnationId, true);
            expectedEndToken.setBytesRead(bytesRead);
            doFindEntriesSinceTest(startToken, Long.MAX_VALUE, Collections.EMPTY_SET, expectedEndToken);
            // create a token that is not past the index end offset on startup after recovery. Should work as expected
            if (incarnationIdToTest == null) {
                startToken = getTokenWithNullIncarnationId(new StoreFindToken(lastRecordOffset, oldSessionId, state.incarnationId, false));
                Assert.assertNull("IncarnationId is expected to be null ", startToken.getIncarnationId());
            } else {
                startToken = new StoreFindToken(lastRecordOffset, oldSessionId, incarnationIdToTest, false);
            }
            expectedEndToken = new StoreFindToken(firstRecordFileSpan.getStartOffset(), state.sessionId, state.incarnationId, false);
            expectedEndToken.setBytesRead(bytesRead);
            doFindEntriesSinceTest(startToken, Long.MAX_VALUE, Collections.singleton(newId), expectedEndToken);
        }
    }

    /**
     * Tests {@link PersistentIndex#getLogSegmentsNotInJournal()}
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void getLogSegmentsNotInJournalTest() throws StoreException, IOException {
        if (isLogSegmented) {
            testGetLogSegmentsNotInJournal(2, (2 * (state.getMaxInMemElements())));
            testGetLogSegmentsNotInJournal(3, (2 * (state.getMaxInMemElements())));
        } else {
            Assert.assertEquals("LogSegments mismatch for non segmented log ", null, state.index.getLogSegmentsNotInJournal());
        }
        state.closeAndClearIndex();
        state.reloadIndex(false, false);
        Assert.assertNull("There should be no offsets in the journal", state.index.journal.getFirstOffset());
        Assert.assertNull("There should be no log segments returned", state.index.getLogSegmentsNotInJournal());
    }

    /**
     * Tests the cases where tokens have to be revalidated on a call to
     * {@link PersistentIndex#findEntriesSince(FindToken, long)}.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void tokenRevalidationTest() throws StoreException {
        // this test is valid only when the log has > 1 log segments i.e. when it can undergo compaction.
        if (isLogSegmented) {
            StoreFindToken absoluteEndToken = new StoreFindToken(state.logOrder.lastKey(), state.sessionId, state.incarnationId, false);
            absoluteEndToken.setBytesRead(state.index.getLogUsedCapacity());
            Offset firstIndexSegmentStartOffset = state.referenceIndex.firstKey();
            Assert.assertTrue("The first index segment must have an offset > 0", ((firstIndexSegmentStartOffset.getOffset()) > 0));
            // generate an offset that does not exist.
            String newName = LogSegmentNameHelper.getNextGenerationName(firstIndexSegmentStartOffset.getName());
            long newOffset = (firstIndexSegmentStartOffset.getOffset()) + 1;
            // Generate an offset that is below the index start offset
            long offsetBeforeStart = (firstIndexSegmentStartOffset.getOffset()) - 1;
            Offset[] invalidOffsets = new Offset[]{ new Offset(newName, newOffset), new Offset(firstIndexSegmentStartOffset.getName(), offsetBeforeStart) };
            MockId firstIdInFirstIndexSegment = state.referenceIndex.firstEntry().getValue().firstKey();
            for (Offset invalidOffset : invalidOffsets) {
                // Generate an index based token from invalidOffset
                StoreFindToken startToken = new StoreFindToken(firstIdInFirstIndexSegment, invalidOffset, state.sessionId, state.incarnationId);
                doFindEntriesSinceTest(startToken, Long.MAX_VALUE, state.allKeys.keySet(), absoluteEndToken);
                // Generate a journal based token from invalidOffset (not in journal and is invalid)
                startToken = new StoreFindToken(invalidOffset, state.sessionId, state.incarnationId, false);
                doFindEntriesSinceTest(startToken, Long.MAX_VALUE, state.allKeys.keySet(), absoluteEndToken);
            }
        }
    }

    /**
     * Tests behaviour of {@link PersistentIndex#findEntriesSince(FindToken, long)} on crash restart for the following
     * scenario
     * After restart, lets say no new writes have gone into the store.
     * For a findEntriesSince(offset beyond logEndOffsetOnStartup with different session id) call, index will reset the
     * token to logEndOffsetOnStartup and returns the same.
     * On the subsequent findEntriesSince() call, the index should start returning entries
     * starting from that offset and should not consider that token as non-inclusive
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void findEntriesSinceOnCrashRestartTest() throws StoreException, IOException {
        UUID oldSessionId = state.sessionId;
        state.addPutEntries(3, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        state.reloadIndex(true, true);
        // create a token that will be past the log end offset on start up after restart.
        StoreFindToken startToken = new StoreFindToken(new Offset(state.log.getEndOffset().getName(), ((state.log.getEndOffset().getOffset()) + (2 * (CuratedLogIndexState.PUT_RECORD_SIZE)))), oldSessionId, state.incarnationId, false);
        // end token should point to log end offset on startup
        long bytesRead = state.index.getAbsolutePositionInLogForOffset(state.log.getEndOffset());
        StoreFindToken expectedEndToken = new StoreFindToken(state.log.getEndOffset(), state.sessionId, state.incarnationId, true);
        expectedEndToken.setBytesRead(bytesRead);
        // Fetch the FindToken returned from findEntriesSince
        FindInfo findInfo = state.index.findEntriesSince(startToken, Long.MAX_VALUE);
        Assert.assertEquals("EndToken mismatch ", expectedEndToken, findInfo.getFindToken());
        Assert.assertEquals("No entries should have been returned ", 0, findInfo.getMessageEntries().size());
        // add 2 entries to index and log
        Set<MockId> expectedEntries = new HashSet<>();
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        expectedEntries.add(state.logOrder.lastEntry().getValue().getFirst());
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        expectedEntries.add(state.logOrder.lastEntry().getValue().getFirst());
        bytesRead = state.index.getAbsolutePositionInLogForOffset(state.index.getCurrentEndOffset());
        expectedEndToken = new StoreFindToken(state.index.journal.getLastOffset(), state.sessionId, state.incarnationId, false);
        expectedEndToken.setBytesRead(bytesRead);
        doFindEntriesSinceTest(((StoreFindToken) (findInfo.getFindToken())), Long.MAX_VALUE, expectedEntries, expectedEndToken);
    }

    /**
     * Tests behaviour of {@link PersistentIndex#findEntriesSince(FindToken, long)} relating to incarnationId
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void findEntriesSinceIncarnationIdTest() throws StoreException, IOException {
        Offset lastRecordOffset = state.index.journal.getLastOffset();
        state.appendToLog((2 * (CuratedLogIndexState.PUT_RECORD_SIZE)));
        // will be recovered
        FileSpan firstRecordFileSpan = state.log.getFileSpanForMessage(state.index.getCurrentEndOffset(), CuratedLogIndexState.PUT_RECORD_SIZE);
        // will not be recovered
        FileSpan secondRecordFileSpan = state.log.getFileSpanForMessage(firstRecordFileSpan.getEndOffset(), CuratedLogIndexState.PUT_RECORD_SIZE);
        UUID oldSessionId = state.sessionId;
        UUID oldIncarnationId = state.incarnationId;
        final MockId newId = state.getUniqueId();
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long operationTimeMs = state.time.milliseconds();
        // add to allKeys() so that doFindEntriesSinceTest() works correctly.
        IndexValue putValue = new IndexValue(CuratedLogIndexState.PUT_RECORD_SIZE, firstRecordFileSpan.getStartOffset(), Utils.Infinite_Time, operationTimeMs, accountId, containerId);
        state.allKeys.computeIfAbsent(newId, ( k) -> new TreeSet<>()).add(putValue);
        state.recovery = new MessageStoreRecovery() {
            @Override
            public List<MessageInfo> recover(Read read, long startOffset, long endOffset, StoreKeyFactory factory) throws IOException {
                return Collections.singletonList(new MessageInfo(newId, CuratedLogIndexState.PUT_RECORD_SIZE, accountId, containerId, operationTimeMs));
            }
        };
        // change in incarnationId
        state.incarnationId = UUID.randomUUID();
        state.reloadIndex(true, true);
        long bytesRead = state.index.getAbsolutePositionInLogForOffset(firstRecordFileSpan.getEndOffset());
        // create a token that will be past the index end offset on startup after recovery with old incarnationId
        StoreFindToken startToken = new StoreFindToken(secondRecordFileSpan.getEndOffset(), oldSessionId, oldIncarnationId, false);
        // token should get reset internally, all keys should be returned and the returned token should be pointing to
        // start offset of firstRecordFileSpan.
        StoreFindToken expectedEndToken = new StoreFindToken(firstRecordFileSpan.getStartOffset(), state.sessionId, state.incarnationId, false);
        expectedEndToken.setBytesRead(bytesRead);
        doFindEntriesSinceTest(startToken, Long.MAX_VALUE, state.allKeys.keySet(), expectedEndToken);
        // create a token that is not past the index end offset on startup after recovery with old incarnationId.
        // token should get reset internally, all keys should be returned and the returned token should be be pointing to
        // start offset of firstRecordFileSpan.
        startToken = new StoreFindToken(lastRecordOffset, oldSessionId, oldIncarnationId, false);
        expectedEndToken = new StoreFindToken(firstRecordFileSpan.getStartOffset(), state.sessionId, state.incarnationId, false);
        expectedEndToken.setBytesRead(bytesRead);
        doFindEntriesSinceTest(startToken, Long.MAX_VALUE, state.allKeys.keySet(), expectedEndToken);
    }

    /**
     * Tests {@link PersistentIndex#findDeletedEntriesSince(FindToken, long, long)} for various cases
     * 1. All cases that result in getting an index based token
     * 2. All cases that result in getting a journal based token
     * 3. Getting entries one by one
     * 4. Getting entries using an index based token for an offset in the journal
     * 5. Using findDeletedEntriesSince() in an empty index
     * 6. Token that has the log end offset
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void findDeletedEntriesSinceTest() throws StoreException, IOException {
        // add some more entries so that the journal gets entries across segments and doesn't start at the beginning
        // of an index segment.
        state.addPutEntries(7, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        MockId idToDelete = state.getIdToDeleteFromIndexSegment(state.referenceIndex.lastKey(), false);
        state.addDeleteEntry(idToDelete);
        // token with log end offset should not return anything
        StoreFindToken token = new StoreFindToken(state.log.getEndOffset(), state.sessionId, state.incarnationId, false);
        doFindDeletedEntriesSinceTest(token, Long.MAX_VALUE, Collections.EMPTY_SET, token);
        findDeletedEntriesSinceToIndexBasedTest();
        findDeletedEntriesSinceToJournalBasedTest();
        findDeletedEntriesSinceOneByOneTest();
        findDeletedEntriesSinceIndexBasedTokenForOffsetInJournalTest();
        findEntriesSinceInEmptyIndexTest(true);
    }

    /**
     * Tests the index segment roll over when there is a change in IndexValue size. With introduction of
     * {@link PersistentIndex#VERSION_1} there is a change in IndexValue.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testIndexSegmentRollOverNewIndexSegmentVersion() throws StoreException, IOException {
        indexSegmentRollOverTest(VERSION_0, true);
        indexSegmentRollOverTest(VERSION_0, false);
        indexSegmentRollOverTest(VERSION_1, true);
        indexSegmentRollOverTest(VERSION_1, false);
    }

    /**
     * Tests index segment rollover behavior with respect to key size changes. For the current version
     * ({@link PersistentIndex#VERSION_2}), a change in key size should cause a rollover only if the persistedEntrySize of
     * the active segment is smaller than the entry size of the incoming entry.
     * Also test index segment rollover with respect to a value size change.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testIndexSegmentRollOverKeySizeAndValueSizeChange() throws StoreException, IOException {
        state.closeAndClearIndex();
        int persistedEntryMinBytes = 100;
        state.properties.put("store.index.persisted.entry.min.bytes", Long.toString(persistedEntryMinBytes));
        state.properties.put("store.index.max.number.of.inmem.elements", Integer.toString(10));
        StoreConfig config = new StoreConfig(new VerifiableProperties(state.properties));
        state.reloadIndex(false, false);
        List<IndexEntry> indexEntries = new ArrayList<>();
        int indexCount = state.index.getIndexSegments().size();
        int serOverheadBytes = (state.getUniqueId(10).sizeInBytes()) - 10;
        int latestSegmentExpectedEntrySize = config.storeIndexPersistedEntryMinBytes;
        // add first entry with size under storeIndexPersistedEntryMinBytes.
        int keySize = (((config.storeIndexPersistedEntryMinBytes) / 2) - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 1, (++indexCount), latestSegmentExpectedEntrySize, false);
        // Now, the active segment consists of one element. Add 2nd element of a smaller key size; and entry size still under
        // storeIndexPersistedEntryMinBytes.
        keySize = keySize / 2;
        addEntriesAndAssert(indexEntries, keySize, 1, indexCount, latestSegmentExpectedEntrySize, false);
        // 3rd element with key size greater than the first entry, but still under storeIndexPersistedEntryMinBytes.
        keySize = keySize * 3;
        addEntriesAndAssert(indexEntries, keySize, 1, indexCount, latestSegmentExpectedEntrySize, false);
        // 4th element with key size increase, and entry size at exactly storeIndexPersistedEntryMinBytes.
        // This should also not cause a rollover.
        keySize = ((config.storeIndexPersistedEntryMinBytes) - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 1, indexCount, latestSegmentExpectedEntrySize, false);
        // 5th element key size increase, and above storeIndexPersistedEntryMinBytes. This continues to be supported via
        // a rollover.
        keySize = (((config.storeIndexPersistedEntryMinBytes) - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes) + 1;
        addEntriesAndAssert(indexEntries, keySize, 1, (++indexCount), (++latestSegmentExpectedEntrySize), false);
        // 2nd and 3rd element in the next segment of original size. This should be accommodated in the same segment.
        keySize = ((config.storeIndexPersistedEntryMinBytes) - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 2, indexCount, latestSegmentExpectedEntrySize, false);
        // verify index values
        verifyIndexValues(indexEntries);
        // Verify that a decrease in the config value for persistedEntryMinBytes does not affect the loaded segment.
        // Now close and reload index with a change in the minPersistedBytes.
        state.properties.put("store.index.persisted.entry.min.bytes", Long.toString((persistedEntryMinBytes / 2)));
        state.reloadIndex(true, false);
        keySize = (latestSegmentExpectedEntrySize - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 7, indexCount, latestSegmentExpectedEntrySize, false);
        // At this point, index will rollover due to max number of entries being reached. Verify that the new segment that is
        // created honors the new config value.
        config = new StoreConfig(new VerifiableProperties(state.properties));
        latestSegmentExpectedEntrySize = config.storeIndexPersistedEntryMinBytes;
        keySize = ((config.storeIndexPersistedEntryMinBytes) - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 1, (++indexCount), latestSegmentExpectedEntrySize, false);
        // verify index values
        verifyIndexValues(indexEntries);
        // Verify that an increase in the config value for persistedEntryMinBytes does not affect the loaded segment.
        // Now close and reload index with a change in the minPersistedBytes.
        state.properties.put("store.index.persisted.entry.min.bytes", Long.toString((persistedEntryMinBytes * 2)));
        state.reloadIndex(true, false);
        // Make sure we add entries that can fit in the latest segment.
        keySize = (latestSegmentExpectedEntrySize - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        addEntriesAndAssert(indexEntries, keySize, 9, indexCount, latestSegmentExpectedEntrySize, false);
        // At this point, index will rollover due to max number of entries being reached. Verify that the new segment that is
        // created has the new entry size.
        config = new StoreConfig(new VerifiableProperties(state.properties));
        keySize = (latestSegmentExpectedEntrySize - (INDEX_VALUE_SIZE_IN_BYTES_V1)) - serOverheadBytes;
        latestSegmentExpectedEntrySize = config.storeIndexPersistedEntryMinBytes;
        addEntriesAndAssert(indexEntries, keySize, 1, (++indexCount), latestSegmentExpectedEntrySize, false);
        // verify index values
        verifyIndexValues(indexEntries);
        // Verify that a value size change will cause a rollover
        addEntriesAndAssert(indexEntries, keySize, 1, (++indexCount), latestSegmentExpectedEntrySize, true);
        verifyIndexValues(indexEntries);
    }

    /**
     * Tests the Index persistor for all cases
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void indexPersistorTest() throws StoreException, IOException {
        // make sure persistor is persisting all segments
        int numIndexSegments = state.index.getIndexSegments().size();
        state.index.persistIndex();
        List<File> indexFiles = Arrays.asList(tempDir.listFiles(INDEX_SEGMENT_FILE_FILTER));
        indexFiles.sort(INDEX_SEGMENT_FILE_COMPARATOR);
        Assert.assertEquals("Not as many files written as there were index segments", numIndexSegments, indexFiles.size());
        long prevFilePersistTimeMs = Long.MIN_VALUE;
        for (File indexFile : indexFiles) {
            long thisFilePersistTimeMs = indexFile.lastModified();
            Assert.assertTrue("A later index segment was persisted earlier", (prevFilePersistTimeMs <= thisFilePersistTimeMs));
            prevFilePersistTimeMs = thisFilePersistTimeMs;
        }
        // add new entries which may not be persisted
        // call persist and reload the index (index.close() is never called)
        Offset indexEndOffset = state.index.getCurrentEndOffset();
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        MockId id = state.logOrder.lastEntry().getValue().getFirst();
        state.index.persistIndex();
        state.reloadIndex(false, false);
        Assert.assertEquals("Index End offset mismatch ", new Offset(indexEndOffset.getName(), ((indexEndOffset.getOffset()) + (CuratedLogIndexState.PUT_RECORD_SIZE))), state.index.getCurrentEndOffset());
        verifyValue(id, state.index.findKey(id));
        // add entries to index alone.
        // index.persist() should throw an exception since log end offset is < index end offset
        FileSpan fileSpan = new FileSpan(state.index.getCurrentEndOffset(), new Offset(state.index.getCurrentEndOffset().getName(), ((state.index.getCurrentEndOffset().getOffset()) + (CuratedLogIndexState.PUT_RECORD_SIZE))));
        IndexValue value = new IndexValue(CuratedLogIndexState.PUT_RECORD_SIZE, fileSpan.getStartOffset(), Utils.Infinite_Time, state.time.milliseconds(), Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM));
        state.index.addToIndex(new IndexEntry(state.getUniqueId(), value), fileSpan);
        try {
            state.index.persistIndex();
            Assert.fail("Should have thrown exception since index has entries which log does not have");
        } catch (StoreException e) {
            Assert.assertEquals("StoreException error code mismatch ", Illegal_Index_State, e.getErrorCode());
        }
        // append to log so that log and index are in sync with each other
        state.appendToLog(CuratedLogIndexState.PUT_RECORD_SIZE);
    }

    /**
     * Tests {@link PersistentIndex#getAbsolutePositionInLogForOffset(Offset)}.
     */
    @Test
    public void getAbsolutePositionForOffsetTest() {
        List<LogSegment> logSegments = new ArrayList<>();
        LogSegment segment = state.log.getFirstSegment();
        while (segment != null) {
            logSegments.add(segment);
            segment = state.log.getNextSegment(segment);
        } 
        long numLogSegmentsPreceding = 0;
        for (LogSegment logSegment : logSegments) {
            verifyAbsolutePosition(new Offset(logSegment.getName(), 0L), numLogSegmentsPreceding);
            verifyAbsolutePosition(new Offset(logSegment.getName(), logSegment.getStartOffset()), numLogSegmentsPreceding);
            long randomPosRange = ((logSegment.getEndOffset()) - (logSegment.getStartOffset())) - 1;
            long randomPos = ((Utils.getRandomLong(RANDOM, randomPosRange)) + (logSegment.getStartOffset())) + 1;
            verifyAbsolutePosition(new Offset(logSegment.getName(), randomPos), numLogSegmentsPreceding);
            verifyAbsolutePosition(new Offset(logSegment.getName(), logSegment.getEndOffset()), numLogSegmentsPreceding);
            numLogSegmentsPreceding++;
        }
    }

    /**
     * Tests {@link PersistentIndex#getAbsolutePositionInLogForOffset(Offset)} with bad arguments.
     */
    @Test
    public void getAbsolutePositionForOffsetBadArgsTest() {
        Offset badSegmentOffset = new Offset(LogSegmentNameHelper.getName(((state.index.getLogSegmentCount()) + 1), 0), 0);
        Offset badOffsetOffset = new Offset(state.log.getFirstSegment().getName(), ((state.log.getFirstSegment().getCapacityInBytes()) + 1));
        List<Offset> offsetsToCheck = new ArrayList<>();
        offsetsToCheck.add(badSegmentOffset);
        offsetsToCheck.add(badOffsetOffset);
        for (Offset offset : offsetsToCheck) {
            try {
                state.index.getAbsolutePositionInLogForOffset(offset);
                Assert.fail(("Should have failed to get absolute position for invalid offset input: " + offset));
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
    }

    /**
     * Test that verifies that everything is ok even if {@link MessageStoreHardDelete} instance provided is null.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void hardDeleteNullTest() throws StoreException, IOException {
        state.hardDelete = null;
        state.reloadIndex(true, false);
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        MockId idToCheck = state.logOrder.lastEntry().getValue().getFirst();
        state.reloadIndex(true, false);
        verifyValue(idToCheck, state.index.findKey(idToCheck));
    }

    /**
     * Tests correctness of {@link PersistentIndex#getIndexSegmentFilesForLogSegment(String, String)} and makes sure
     * it picks up all the files.
     */
    @Test
    public void getIndexSegmentFilesForLogSegmentTest() {
        LogSegment logSegment = state.log.getFirstSegment();
        while (logSegment != null) {
            LogSegment nextSegment = state.log.getNextSegment(logSegment);
            File[] indexSegmentFiles = PersistentIndex.getIndexSegmentFilesForLogSegment(tempDir.getAbsolutePath(), logSegment.getName());
            Arrays.sort(indexSegmentFiles, INDEX_SEGMENT_FILE_COMPARATOR);
            Offset from = new Offset(logSegment.getName(), logSegment.getStartOffset());
            Offset to = new Offset(logSegment.getName(), logSegment.getEndOffset());
            Set<Offset> offsets = state.referenceIndex.subMap(from, true, to, true).keySet();
            Assert.assertEquals("Number of index segment files inconsistent", offsets.size(), indexSegmentFiles.length);
            int i = 0;
            for (Offset offset : offsets) {
                Assert.assertEquals("Index segment file inconsistent with offset expected", offset, IndexSegment.getIndexSegmentStartOffset(indexSegmentFiles[i].getName()));
                i++;
            }
            logSegment = nextSegment;
        } 
    }

    /**
     * Tests {@link PersistentIndex#close()} can correctly cancel the scheduled persistor task and makes sure no persistor
     * is running background after index closed.
     *
     * @throws StoreException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void closeIndexToCancelPersistorTest() throws StoreException, InterruptedException {
        long SCHEDULER_PERIOD_MS = 10;
        state.index.close();
        // re-initialize index by using mock scheduler (the intention is to speed up testing by using shorter period)
        ScheduledThreadPoolExecutor scheduler = ((ScheduledThreadPoolExecutor) (Utils.newScheduler(1, false)));
        ScheduledThreadPoolExecutor mockScheduler = Mockito.spy(scheduler);
        AtomicReference<ScheduledFuture> persistorTask = new AtomicReference<>();
        class MockPersistor implements Runnable {
            private CountDownLatch invokeCountDown;

            @Override
            public void run() {
                invokeCountDown.countDown();
            }
        }
        MockPersistor mockPersistor = new MockPersistor();
        mockPersistor.invokeCountDown = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            persistorTask.set(mockScheduler.scheduleAtFixedRate(mockPersistor, 0, SCHEDULER_PERIOD_MS, TimeUnit.MILLISECONDS));
            return persistorTask.get();
        }).when(mockScheduler).scheduleAtFixedRate(ArgumentMatchers.any(IndexPersistor.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.SECONDS.getClass()));
        state.initIndex(mockScheduler);
        // verify that the persistor task is successfully scheduled
        Assert.assertTrue("The persistor task wasn't invoked within the expected time", mockPersistor.invokeCountDown.await((2 * SCHEDULER_PERIOD_MS), TimeUnit.MILLISECONDS));
        state.index.close();
        mockPersistor.invokeCountDown = new CountDownLatch(1);
        // verify that the persisitor task is canceled after index closed and is never invoked again.
        Assert.assertTrue("The persistor task should be canceled after index closed", persistorTask.get().isCancelled());
        Assert.assertFalse("The persistor task should not be invoked again", mockPersistor.invokeCountDown.await((2 * SCHEDULER_PERIOD_MS), TimeUnit.MILLISECONDS));
    }

    /**
     * Tests {@link PersistentIndex#cleanupIndexSegmentFilesForLogSegment(String, String)} and makes sure it deletes all
     * the relevant files and no more.
     *
     * @throws IOException
     * 		
     * @throws StoreException
     * 		
     */
    @Test
    public void cleanupIndexSegmentFilesForLogSegmentTest() throws StoreException, IOException {
        state.index.close();
        LogSegment logSegment = state.log.getFirstSegment();
        while (logSegment != null) {
            LogSegment nextSegment = state.log.getNextSegment(logSegment);
            final String logSegmentName = logSegment.getName();
            int totalFilesInDir = tempDir.listFiles().length;
            File[] filesToCheck = tempDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.startsWith(logSegmentName)) && ((name.endsWith(INDEX_SEGMENT_FILE_NAME_SUFFIX)) || (name.endsWith(BLOOM_FILE_NAME_SUFFIX)));
                }
            });
            Offset from = new Offset(logSegment.getName(), logSegment.getStartOffset());
            Offset to = new Offset(logSegment.getName(), logSegment.getEndOffset());
            int expectedNumFilesToDelete = (state.referenceIndex.subMap(from, true, to, true).size()) * 2;
            if (nextSegment == null) {
                // latest index segment does not have a bloom file
                expectedNumFilesToDelete--;
            }
            Assert.assertEquals("Number of files to check does not match expectation", expectedNumFilesToDelete, filesToCheck.length);
            PersistentIndex.cleanupIndexSegmentFilesForLogSegment(tempDir.getAbsolutePath(), logSegmentName);
            for (File fileToCheck : filesToCheck) {
                Assert.assertFalse((fileToCheck + " should have been deleted"), fileToCheck.exists());
            }
            Assert.assertEquals("More than the expected number of files were deleted", (totalFilesInDir - (filesToCheck.length)), tempDir.listFiles().length);
            logSegment = nextSegment;
        } 
    }

    /**
     * Tests the behavior of {@link Journal} bootstrap.
     */
    @Test
    public void journalBootstrapTest() throws StoreException, IOException {
        if ((state.getMaxInMemElements()) <= 1) {
            Assert.fail("This test can work only if the max in mem elements config > 1");
        }
        if ((state.index.getIndexSegments().lastEntry().getValue().getNumberOfItems()) <= 1) {
            state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        }
        Assert.assertTrue("There should be more than one element in the last index segment for this test to work", ((state.index.getIndexSegments().lastEntry().getValue().getNumberOfItems()) > 1));
        // change the config to lower max journal size
        state.properties.put("store.index.max.number.of.inmem.elements", "1");
        state.reloadIndex(true, false);
        int entriesInJournal = state.index.journal.getCurrentNumberOfEntries();
        Assert.assertEquals("Number of entries in the journal should be equal to the number of elements in the last index segment", state.index.getIndexSegments().lastEntry().getValue().getNumberOfItems(), entriesInJournal);
        // add some entries to trigger rollover.
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        Assert.assertEquals("Number of entries in the journal should not have changed", entriesInJournal, state.index.journal.getCurrentNumberOfEntries());
        // Reload the index and the journal size should now reflect the config change
        state.reloadIndex(true, false);
        Assert.assertEquals("Number of entries in the journal should be exactly 1", 1, state.index.journal.getCurrentNumberOfEntries());
    }
}

