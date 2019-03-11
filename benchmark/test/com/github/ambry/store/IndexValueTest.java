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


import IndexValue.Flags;
import IndexValue.Flags.Delete_Index;
import IndexValue.Flags.Ttl_Update_Index;
import IndexValue.UNKNOWN_ORIGINAL_MESSAGE_OFFSET;
import TestUtils.RANDOM;
import Utils.Infinite_Time;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link IndexValue}.
 */
@RunWith(Parameterized.class)
public class IndexValueTest {
    private final short version;

    /**
     * Creates a temporary directory and sets up metrics.
     *
     * @throws IOException
     * 		
     */
    public IndexValueTest(short version) {
        this.version = version;
    }

    /**
     * Tests an {@link IndexValue} that is representative of a PUT index entry value.
     */
    @Test
    public void putValueTest() {
        long pos = Utils.getRandomLong(RANDOM, 1000);
        long gen = Utils.getRandomLong(RANDOM, 1000);
        String logSegmentName = LogSegmentNameHelper.getName(pos, gen);
        long size = Utils.getRandomLong(RANDOM, 1000);
        long offset = Utils.getRandomLong(RANDOM, 1000);
        long operationTimeAtMs = (Utils.getRandomLong(RANDOM, 1000000)) + (SystemTime.getInstance().milliseconds());
        long expectedOperationTimeV1 = Utils.getTimeInMsToTheNearestSec(operationTimeAtMs);
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        List<Long> testExpiresAtMs = new ArrayList<>();
        // random value
        long expirationTimeAtMs = (Utils.getRandomLong(RANDOM, 1000000)) + (SystemTime.getInstance().milliseconds());
        testExpiresAtMs.add(expirationTimeAtMs);
        // no expiry
        testExpiresAtMs.add(Infinite_Time);
        // max value -1
        expirationTimeAtMs = TimeUnit.SECONDS.toMillis(((Integer.MAX_VALUE) - 1));
        testExpiresAtMs.add(expirationTimeAtMs);
        // max value
        expirationTimeAtMs = TimeUnit.SECONDS.toMillis(Integer.MAX_VALUE);
        testExpiresAtMs.add(expirationTimeAtMs);
        // expiry > Integer.MAX_VALUE, expected to be -1
        expirationTimeAtMs = TimeUnit.SECONDS.toMillis((((long) (Integer.MAX_VALUE)) + 1));
        testExpiresAtMs.add(expirationTimeAtMs);
        // expiry < 0. This is to test how negative expiration values are treated in deser path.
        expirationTimeAtMs = (-1) * (TimeUnit.DAYS.toMillis(1));
        testExpiresAtMs.add(expirationTimeAtMs);
        // expiry < 0. This is to test how negative expiration values are treated in deser path.
        expirationTimeAtMs = ((long) (Integer.MIN_VALUE));
        testExpiresAtMs.add(expirationTimeAtMs);
        for (long expiresAtMs : testExpiresAtMs) {
            IndexValue value = IndexValueTest.getIndexValue(size, new Offset(logSegmentName, offset), expiresAtMs, operationTimeAtMs, accountId, containerId, version);
            verifyIndexValue(value, logSegmentName, size, offset, EnumSet.noneOf(Flags.class), expiresAtMs, offset, expectedOperationTimeV1, accountId, containerId);
        }
    }

    /**
     * Tests an {@link IndexValue} that is representative of a update index entry value. Tests both when the update
     * value is in the same log segment and a different one.
     */
    @Test
    public void updateRecordTest() {
        long pos = Utils.getRandomLong(RANDOM, 1000);
        long gen = Utils.getRandomLong(RANDOM, 1000);
        String logSegmentName = LogSegmentNameHelper.getName(pos, gen);
        long size = Utils.getRandomLong(RANDOM, 1000);
        long offset = Utils.getRandomLong(RANDOM, 1000);
        long expiresAtMs = Utils.getRandomLong(RANDOM, 1000000);
        long operationTimeAtMs = Utils.getRandomLong(RANDOM, 1000000);
        long expectedOperationTimeMs = Utils.getTimeInMsToTheNearestSec(operationTimeAtMs);
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        IndexValue value = IndexValueTest.getIndexValue(size, new Offset(logSegmentName, offset), expiresAtMs, operationTimeAtMs, accountId, containerId, version);
        long originalMsgOffset = Utils.getRandomLong(RANDOM, 1000);
        long originalMsgSize = Utils.getRandomLong(RANDOM, 1000);
        long expectedOffset;
        long expectedSize;
        long expectedOriginalMsgOffset;
        for (IndexValue.Flags flag : Flags.values()) {
            // update in the same log segment
            IndexValue newValue = new IndexValue(logSegmentName, value.getBytes(), version);
            newValue.setFlag(flag);
            Offset originalMsgOff = new Offset(logSegmentName, originalMsgOffset);
            if (flag.equals(Delete_Index)) {
                expectedOffset = originalMsgOffset;
                expectedSize = originalMsgSize;
                expectedOriginalMsgOffset = offset;
                newValue.setNewOffset(originalMsgOff);
                newValue.setNewSize(originalMsgSize);
            } else {
                expectedOffset = offset;
                expectedSize = size;
                expectedOriginalMsgOffset = originalMsgOffset;
                newValue.setOriginalMessageOffset(originalMsgOff);
            }
            verifyIndexValue(newValue, logSegmentName, expectedSize, expectedOffset, EnumSet.of(flag), expiresAtMs, expectedOriginalMsgOffset, expectedOperationTimeMs, accountId, containerId);
            // original message offset cleared
            newValue.clearOriginalMessageOffset();
            verifyIndexValue(newValue, logSegmentName, expectedSize, expectedOffset, EnumSet.of(flag), expiresAtMs, UNKNOWN_ORIGINAL_MESSAGE_OFFSET, expectedOperationTimeMs, accountId, containerId);
            newValue = new IndexValue(logSegmentName, value.getBytes(), version);
            String newLogSegmentName = LogSegmentNameHelper.getNextPositionName(logSegmentName);
            String expectedLogSegmentName;
            // update not in the same log segment
            newValue.setFlag(flag);
            originalMsgOff = new Offset(newLogSegmentName, originalMsgOffset);
            if (flag.equals(Delete_Index)) {
                expectedLogSegmentName = newLogSegmentName;
                // no need to set the other "expected" because they have already been set
                newValue.setNewOffset(originalMsgOff);
                newValue.setNewSize(originalMsgSize);
            } else {
                expectedLogSegmentName = logSegmentName;
                newValue.setOriginalMessageOffset(originalMsgOff);
            }
            verifyIndexValue(newValue, expectedLogSegmentName, expectedSize, expectedOffset, EnumSet.of(flag), expiresAtMs, UNKNOWN_ORIGINAL_MESSAGE_OFFSET, expectedOperationTimeMs, accountId, containerId);
        }
        // clearFlag() test
        IndexValue newValue = new IndexValue(logSegmentName, value.getBytes(), version);
        // set two flags
        newValue.setFlag(Ttl_Update_Index);
        newValue.setFlag(Delete_Index);
        verifyIndexValue(newValue, logSegmentName, size, offset, EnumSet.of(Ttl_Update_Index, Delete_Index), expiresAtMs, offset, expectedOperationTimeMs, accountId, containerId);
        // clear the delete flag
        newValue.clearFlag(Delete_Index);
        verifyIndexValue(newValue, logSegmentName, size, offset, EnumSet.of(Ttl_Update_Index), expiresAtMs, offset, expectedOperationTimeMs, accountId, containerId);
        // clear again (to check that it does not cause problems)
        newValue.clearFlag(Delete_Index);
        // clear the ttl update flag
        newValue.clearFlag(Ttl_Update_Index);
        verifyIndexValue(newValue, logSegmentName, size, offset, EnumSet.noneOf(Flags.class), expiresAtMs, offset, expectedOperationTimeMs, accountId, containerId);
    }
}

