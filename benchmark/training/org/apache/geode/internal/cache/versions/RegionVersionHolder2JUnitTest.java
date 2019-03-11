/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.versions;


import org.junit.Assert;
import org.junit.Test;

import static RegionVersionHolder.BIT_SET_WIDTH;


public class RegionVersionHolder2JUnitTest {
    @Test
    public void testCreateHolderWithBitSet() {
        createHolderTest(true);
    }

    @Test
    public void testCreateHolderWithoutBitSet() {
        createHolderTest(false);
    }

    @Test
    public void testRecordZeroDoesNothingWithBitSet() {
        recordZeroDoesNothing(true);
    }

    @Test
    public void testRecordZeroDoesNothingWithoutBitSet() {
        recordZeroDoesNothing(false);
    }

    @Test
    public void testRecordSequentialVersionsWithBitSet() {
        recordSequentialVersions(true);
    }

    @Test
    public void testRecordSequentialVersionsWithoutBitSet() {
        recordSequentialVersions(false);
    }

    @Test
    public void testSkippedVersionCreatesExceptionWithBitSet() {
        skippedVersionCreatesException(true);
    }

    @Test
    public void testSkippedVersionCreatesExceptionWithoutBitSet() {
        skippedVersionCreatesException(false);
    }

    @Test
    public void testFillExceptionWithBitSet() {
        fillException(true);
    }

    @Test
    public void testFillExceptionWithoutBitSet() {
        fillException(false);
    }

    @Test
    public void testFillLargeExceptionWithBitSet() {
        fillLargeException(true);
    }

    @Test
    public void testFillLargeExceptionWithoutBitSet() {
        fillLargeException(false);
    }

    @Test
    public void testReceiveDuplicateAfterBitSetFlushWithBitSet() {
        RegionVersionHolder h = createHolder(true);
        long bigVersion = (BIT_SET_WIDTH) + 1;
        h.recordVersion(bigVersion);
        for (long i = 0L; i < bigVersion; i++) {
            h.recordVersion(i);
        }
        h.recordVersion(bigVersion);
        Assert.assertEquals(("expected no exceptions in " + h), 0, h.getExceptionCount());
        Assert.assertEquals(bigVersion, h.getVersion());
    }

    @Test
    public void testFillSpecialExceptionWithBitSet() {
        RegionVersionHolder h = createHolder(true);
        h.recordVersion(1L);
        createSpecialException(h);
        Assert.assertEquals(1, h.getExceptionCount());
        RVVException e = ((RVVException) (h.getExceptionForTest().iterator().next()));
        Assert.assertTrue(h.isSpecialException(e, h));
        h.recordVersion(2L);
        // BUG: the exception is not removed
        // assertIndexDetailsEquals("unexpected RVV exception : " + h, 0, h.getExceptionCount());
    }
}

