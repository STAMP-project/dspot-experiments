/**
 * Copyright 2018 LinkedIn Corp. All rights reserved.
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


import Utils.Infinite_Time;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import org.junit.Test;


/**
 * Tests for {@link MessageInfo}
 */
public class MessageInfoTest {
    /**
     * Tests all constructors and getters
     */
    @Test
    public void basicAllConstructorsTest() {
        short[] accountIds = new short[]{ 100, 101, 102, 103 };
        short[] containerIds = new short[]{ 10, 11, 12, 13 };
        boolean[] isDeletedVals = new boolean[]{ false, true, false, true };
        boolean[] isTtlUpdatedVals = new boolean[]{ true, false, false, true };
        Long[] crcs = new Long[]{ null, 100L, Long.MIN_VALUE, Long.MAX_VALUE };
        StoreKey[] keys = new StoreKey[]{ new MockId(UtilsTest.getRandomString(10), accountIds[0], containerIds[0]), new MockId(UtilsTest.getRandomString(10), accountIds[1], containerIds[1]), new MockId(UtilsTest.getRandomString(10), accountIds[2], containerIds[2]), new MockId(UtilsTest.getRandomString(10), accountIds[3], containerIds[3]) };
        long[] blobSizes = new long[]{ 1024, 2048, 4096, 8192 };
        long[] times = new long[]{ (SystemTime.getInstance().milliseconds()) + 100, (SystemTime.getInstance().milliseconds()) - 1, (SystemTime.getInstance().milliseconds()) + 300, Utils.Infinite_Time };
        for (int i = 0; i < (keys.length); i++) {
            MessageInfo info = new MessageInfo(keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], times[i], crcs[i], accountIds[i], containerIds[i], times[i]);
            MessageInfoTest.checkGetters(info, keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], times[i], crcs[i], accountIds[i], containerIds[i], times[i]);
            info = new MessageInfo(keys[i], blobSizes[i], accountIds[i], containerIds[i], times[i]);
            MessageInfoTest.checkGetters(info, keys[i], blobSizes[i], false, false, Infinite_Time, null, accountIds[i], containerIds[i], times[i]);
            info = new MessageInfo(keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], times[i], accountIds[i], containerIds[i], times[i]);
            MessageInfoTest.checkGetters(info, keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], times[i], null, accountIds[i], containerIds[i], times[i]);
            info = new MessageInfo(keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], accountIds[i], containerIds[i], times[i]);
            MessageInfoTest.checkGetters(info, keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], Infinite_Time, null, accountIds[i], containerIds[i], times[i]);
            info = new MessageInfo(keys[i], blobSizes[i], times[i], accountIds[i], containerIds[i], times[i]);
            MessageInfoTest.checkGetters(info, keys[i], blobSizes[i], false, false, times[i], null, accountIds[i], containerIds[i], times[i]);
        }
    }
}

