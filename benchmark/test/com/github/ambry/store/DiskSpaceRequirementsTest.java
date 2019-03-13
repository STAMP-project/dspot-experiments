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


import TestUtils.RANDOM;
import com.github.ambry.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link DiskSpaceRequirements} for expected behavior.
 */
public class DiskSpaceRequirementsTest {
    /**
     * Test invalid constructor arguments
     */
    @Test
    public void invalidArgumentsTest() {
        doInvalidArgumentsTest(0, 0, 0);
        doInvalidArgumentsTest(1, (-1), 0);
        doInvalidArgumentsTest(1, 0, (-1));
    }

    /**
     * Test constructor and getter methods with valid constructor arguments.
     */
    @Test
    public void validArgumentsTest() {
        long segmentSizeInBytes = (Utils.getRandomLong(RANDOM, Long.MAX_VALUE)) + 1;
        long segmentsNeeded = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        long swapUsed = Utils.getRandomLong(RANDOM, Long.MAX_VALUE);
        DiskSpaceRequirements requirements = new DiskSpaceRequirements(segmentSizeInBytes, segmentsNeeded, swapUsed);
        Assert.assertEquals("segment size doesn't match", segmentSizeInBytes, requirements.getSegmentSizeInBytes());
        Assert.assertEquals("segments needed doesn't match", segmentsNeeded, requirements.getSegmentsNeeded());
        Assert.assertEquals("swap used doesn't match", swapUsed, requirements.getSwapSegmentsInUse());
    }
}

