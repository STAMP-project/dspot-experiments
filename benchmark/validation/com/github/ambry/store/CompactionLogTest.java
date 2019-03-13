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


import CompactionLog.Phase.CLEANUP;
import CompactionLog.Phase.COMMIT;
import CompactionLog.Phase.COPY;
import CompactionLog.Phase.DONE;
import CompactionLog.Phase.PREPARE;
import TestUtils.RANDOM;
import com.github.ambry.utils.CrcOutputStream;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static CompactionLog.COMPACTION_LOG_SUFFIX;
import static CompactionLog.CURRENT_VERSION;
import static CompactionLog.VERSION_0;


/**
 * Tests for {@link CompactionLog}.
 */
public class CompactionLogTest {
    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = new MockIdFactory();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // Variables that represent the folder where the data resides
    private final File tempDir;

    private final String tempDirStr;

    // the time instance that will be used in the index
    private final Time time = new MockTime();

    private final Set<String> generatedSegmentNames = new HashSet<>();

    /**
     * Creates a temporary directory for the compaction log file.
     *
     * @throws IOException
     * 		
     */
    public CompactionLogTest() throws IOException {
        tempDir = StoreTestUtils.createTempDirectory(("storeDir-" + (UtilsTest.getRandomString(10))));
        tempDirStr = tempDir.getAbsolutePath();
    }

    /**
     * Tests the use of {@link CompactionLog} when it is used without closing b/w operations and compaction cycles.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void iterationWithoutReloadTest() throws IOException {
        String storeName = "store";
        List<CompactionDetails> detailsList = getCompactionDetailsList(5);
        CompactionDetails combined = combineListOfDetails(detailsList);
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
        CompactionLog cLog = new CompactionLog(tempDirStr, storeName, time, combined);
        Assert.assertTrue("Compaction should be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
        int currentIdx = 0;
        Iterator<CompactionDetails> detailsIterator = detailsList.iterator();
        CompactionDetails currDetails = detailsIterator.next();
        while (currDetails != null) {
            detailsIterator.remove();
            Assert.assertEquals("CurrentIdx not as expected", currentIdx, cLog.getCurrentIdx());
            verifyEquality(combined, cLog.getCompactionDetails());
            Assert.assertEquals("Should be in the PREPARE phase", PREPARE, cLog.getCompactionPhase());
            cLog.markCopyStart();
            Assert.assertEquals("Should be in the COPY phase", COPY, cLog.getCompactionPhase());
            Offset offset = new Offset(LogSegmentNameHelper.generateFirstSegmentName(true), Utils.getRandomLong(RANDOM, Long.MAX_VALUE));
            cLog.setStartOffsetOfLastIndexSegmentForDeleteCheck(offset);
            Assert.assertEquals("Offset that was set was not the one returned", offset, cLog.getStartOffsetOfLastIndexSegmentForDeleteCheck());
            StoreFindToken safeToken = new StoreFindToken(new MockId("dummy"), new Offset(LogSegmentNameHelper.generateFirstSegmentName(true), 0), new UUID(1, 1), new UUID(1, 1));
            cLog.setSafeToken(safeToken);
            Assert.assertEquals("Returned token not the same as the one that was set", safeToken, cLog.getSafeToken());
            CompactionDetails nextDetails = (detailsIterator.hasNext()) ? detailsIterator.next() : null;
            if (nextDetails != null) {
                cLog.splitCurrentCycle(nextDetails.getLogSegmentsUnderCompaction().get(0));
                verifyEquality(currDetails, cLog.getCompactionDetails());
            }
            cLog.markCommitStart();
            Assert.assertEquals("Should be in the SWITCH phase", COMMIT, cLog.getCompactionPhase());
            cLog.markCleanupStart();
            Assert.assertEquals("Should be in the CLEANUP phase", CLEANUP, cLog.getCompactionPhase());
            cLog.markCycleComplete();
            currentIdx++;
            currDetails = nextDetails;
            if (nextDetails != null) {
                combined = combineListOfDetails(detailsList);
            }
        } 
        Assert.assertEquals("CurrentIdx not as expected", (-1), cLog.getCurrentIdx());
        Assert.assertEquals("Should be in the DONE phase", DONE, cLog.getCompactionPhase());
        cLog.close();
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
    }

    /**
     * Tests the use of {@link CompactionLog} when it is closed and reloaded b/w operations and compaction cycles.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void iterationWithReloadTest() throws IOException {
        String storeName = "store";
        List<CompactionDetails> detailsList = getCompactionDetailsList(5);
        CompactionDetails combined = combineListOfDetails(detailsList);
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
        CompactionLog cLog = new CompactionLog(tempDirStr, storeName, time, combined);
        Assert.assertTrue("Compaction should should be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
        int currentIdx = 0;
        Iterator<CompactionDetails> detailsIterator = detailsList.iterator();
        CompactionDetails currDetails = detailsIterator.next();
        while (currDetails != null) {
            detailsIterator.remove();
            Assert.assertEquals("CurrentIdx not as expected", currentIdx, cLog.getCurrentIdx());
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            verifyEquality(combined, cLog.getCompactionDetails());
            Assert.assertEquals("Should be in the PREPARE phase", PREPARE, cLog.getCompactionPhase());
            cLog.markCopyStart();
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.assertEquals("Should be in the COPY phase", COPY, cLog.getCompactionPhase());
            Offset offset = new Offset(LogSegmentNameHelper.generateFirstSegmentName(true), Utils.getRandomLong(RANDOM, Long.MAX_VALUE));
            cLog.setStartOffsetOfLastIndexSegmentForDeleteCheck(offset);
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.assertEquals("Offset that was set was not the one returned", offset, cLog.getStartOffsetOfLastIndexSegmentForDeleteCheck());
            StoreFindToken safeToken = new StoreFindToken(new MockId("dummy"), new Offset(LogSegmentNameHelper.generateFirstSegmentName(true), 0), new UUID(1, 1), new UUID(1, 1));
            cLog.setSafeToken(safeToken);
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.assertEquals("Returned token not the same as the one that was set", safeToken, cLog.getSafeToken());
            CompactionDetails nextDetails = (detailsIterator.hasNext()) ? detailsIterator.next() : null;
            if (nextDetails != null) {
                cLog.splitCurrentCycle(nextDetails.getLogSegmentsUnderCompaction().get(0));
                verifyEquality(currDetails, cLog.getCompactionDetails());
                cLog.close();
                cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
                verifyEquality(currDetails, cLog.getCompactionDetails());
            }
            cLog.markCommitStart();
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.assertEquals("Should be in the SWITCH phase", COMMIT, cLog.getCompactionPhase());
            cLog.markCleanupStart();
            cLog.close();
            cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.assertEquals("Should be in the CLEANUP phase", CLEANUP, cLog.getCompactionPhase());
            cLog.markCycleComplete();
            currentIdx++;
            currDetails = nextDetails;
            if (nextDetails != null) {
                combined = combineListOfDetails(detailsList);
            }
        } 
        Assert.assertEquals("CurrentIdx not as expected", (-1), cLog.getCurrentIdx());
        Assert.assertEquals("Should be in the DONE phase", DONE, cLog.getCompactionPhase());
        cLog.close();
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
    }

    /**
     * Tests phase transition order is enforced during phase transitions
     */
    @Test
    public void phaseTransitionEnforcementTest() throws IOException {
        String storeName = "store";
        CompactionLog cLog = new CompactionLog(tempDirStr, storeName, time, getCompactionDetails());
        Assert.assertEquals("Should be in the PREPARE phase", PREPARE, cLog.getCompactionPhase());
        checkTransitionFailure(cLog, COMMIT, CLEANUP, DONE);
        cLog.markCopyStart();
        Assert.assertEquals("Should be in the COPY phase", COPY, cLog.getCompactionPhase());
        checkTransitionFailure(cLog, COPY, CLEANUP, DONE);
        cLog.markCommitStart();
        Assert.assertEquals("Should be in the SWITCH phase", COMMIT, cLog.getCompactionPhase());
        checkTransitionFailure(cLog, COPY, COMMIT, DONE);
        cLog.markCleanupStart();
        Assert.assertEquals("Should be in the CLEANUP phase", CLEANUP, cLog.getCompactionPhase());
        checkTransitionFailure(cLog, COPY, COMMIT, CLEANUP);
        cLog.markCycleComplete();
        Assert.assertEquals("Should be in the DONE phase", DONE, cLog.getCompactionPhase());
        checkTransitionFailure(cLog, COPY, COMMIT, CLEANUP, DONE);
        cLog.close();
    }

    /**
     * Tests for construction of {@link CompactionLog} with bad arguments/state.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void constructionBadArgsTest() throws IOException {
        String storeName = "store";
        CompactionLog cLog = new CompactionLog(tempDirStr, storeName, time, getCompactionDetails());
        cLog.close();
        // log file already exists
        try {
            new CompactionLog(tempDirStr, storeName, time, getCompactionDetails());
            Assert.fail("Construction should have failed because compaction log file already exists");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // make sure file disappears
        cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
        cLog.markCopyStart();
        cLog.markCommitStart();
        cLog.markCleanupStart();
        cLog.markCycleComplete();
        cLog.close();
        Assert.assertFalse("Compaction should not be in progress", CompactionLog.isCompactionInProgress(tempDirStr, storeName));
        // log file does not exist
        try {
            new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
            Assert.fail("Construction should have failed because compaction log file does not exist");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests the reading of versions older than the current versions.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void oldVersionsReadTest() throws IOException {
        String storeName = "store";
        long startTimeMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        long referenceTimeMs = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        CompactionDetails details = getCompactionDetails(referenceTimeMs);
        for (int i = 0; i < (CURRENT_VERSION); i++) {
            File file = new File(tempDir, (storeName + (COMPACTION_LOG_SUFFIX)));
            switch (i) {
                case VERSION_0 :
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        CrcOutputStream crcOutputStream = new CrcOutputStream(fileOutputStream);
                        DataOutputStream stream = new DataOutputStream(crcOutputStream);
                        stream.writeShort(i);
                        stream.writeLong(startTimeMs);
                        stream.writeInt(0);
                        stream.writeInt(1);
                        stream.write(toBytes());
                        stream.writeLong(crcOutputStream.getValue());
                        fileOutputStream.getChannel().force(true);
                    }
                    CompactionLog cLog = new CompactionLog(tempDirStr, storeName, CompactionLogTest.STORE_KEY_FACTORY, time);
                    verifyEquality(details, cLog.getCompactionDetails());
                    Assert.assertEquals("Current Idx not as expected", 0, cLog.getCurrentIdx());
                    break;
                default :
                    throw new IllegalStateException(("No serialization implementation for version: " + i));
            }
        }
    }
}

