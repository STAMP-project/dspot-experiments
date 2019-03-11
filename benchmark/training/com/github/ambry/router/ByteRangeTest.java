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
package com.github.ambry.router;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link ByteRange} class.
 */
public class ByteRangeTest {
    /**
     * Test that we can create valid ranges and read their offsets correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testValidRange() throws Exception {
        testByteRangeCreationOffsetRange(0, 0, true);
        testByteRangeCreationFromStartOffset(0, true);
        testByteRangeCreationFromStartOffset(15, true);
        testByteRangeCreationLastNBytes(20, true);
        testByteRangeCreationLastNBytes(0, true);
        testByteRangeCreationOffsetRange(22, 44, true);
        testByteRangeCreationFromStartOffset(Long.MAX_VALUE, true);
    }

    /**
     * Ensure that we cannot create invalid ranges.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInvalidRanges() throws Exception {
        // negative indices
        testByteRangeCreationOffsetRange((-2), 1, false);
        testByteRangeCreationOffsetRange(5, (-1), false);
        testByteRangeCreationOffsetRange(0, (-1), false);
        testByteRangeCreationOffsetRange((-3), (-2), false);
        testByteRangeCreationFromStartOffset((-1), false);
        testByteRangeCreationLastNBytes((-2), false);
        // start greater than end offset
        testByteRangeCreationOffsetRange(32, 4, false);
        testByteRangeCreationOffsetRange(1, 0, false);
        testByteRangeCreationOffsetRange(Long.MAX_VALUE, ((Long.MAX_VALUE) - 1), false);
    }

    /**
     * Test that resolving {@link ByteRange}s with a blob size to generate ranges with defined start/end offsets works as
     * expected.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResolvedByteRange() throws Exception {
        // 0-0 (0th byte)
        ByteRange range = ByteRanges.fromOffsetRange(0, 0);
        assertRangeResolutionFailure(range, 0);
        assertRangeResolutionFailure(range, (-1));
        assertRangeResolutionSuccess(range, 2, 0, 0);
        // 0- (bytes after/including 0)
        range = ByteRanges.fromStartOffset(0);
        assertRangeResolutionFailure(range, 0);
        assertRangeResolutionFailure(range, (-1));
        assertRangeResolutionSuccess(range, 20, 0, 19);
        // 15- (bytes after/including 15)
        range = ByteRanges.fromStartOffset(15);
        assertRangeResolutionFailure(range, 15);
        assertRangeResolutionFailure(range, (-1));
        assertRangeResolutionSuccess(range, 20, 15, 19);
        assertRangeResolutionSuccess(range, 16, 15, 15);
        // -20 (last 20 bytes)
        range = ByteRanges.fromLastNBytes(20);
        assertRangeResolutionFailure(range, (-1));
        assertRangeResolutionSuccess(range, 0, 0, (-1));
        assertRangeResolutionSuccess(range, 19, 0, 18);
        assertRangeResolutionSuccess(range, 20, 0, 19);
        assertRangeResolutionSuccess(range, 30, 10, 29);
        // 22-44 (bytes 22 through 44, inclusive)
        range = ByteRanges.fromOffsetRange(22, 44);
        assertRangeResolutionSuccess(range, 44, 22, 43);
        assertRangeResolutionSuccess(range, 45, 22, 44);
        // {MAX_LONG-50}- (bytes after/including MAX_LONG-50)
        range = ByteRanges.fromStartOffset(((Long.MAX_VALUE) - 50));
        assertRangeResolutionFailure(range, 0);
        assertRangeResolutionFailure(range, (-1));
        assertRangeResolutionFailure(range, 20);
        assertRangeResolutionSuccess(range, Long.MAX_VALUE, ((Long.MAX_VALUE) - 50), ((Long.MAX_VALUE) - 1));
        // Last 0 bytes
        range = ByteRanges.fromLastNBytes(0);
        assertRangeResolutionSuccess(range, 0, 0, (-1));
        assertRangeResolutionSuccess(range, 20, 20, 19);
    }

    /**
     * Test toString, equals, and hashCode methods.
     */
    @Test
    public void testToStringEqualsAndHashcode() {
        ByteRange a = ByteRanges.fromLastNBytes(4);
        ByteRange b = ByteRanges.fromLastNBytes(4);
        Assert.assertEquals("ByteRanges should be equal", a, b);
        Assert.assertEquals("ByteRange hashcodes should be equal", a.hashCode(), b.hashCode());
        Assert.assertEquals("toString output not as expected", "ByteRange{lastNBytes=4}", a.toString());
        a = ByteRanges.fromOffsetRange(2, 5);
        Assert.assertFalse("ByteRanges should not be equal", a.equals(b));
        b = ByteRanges.fromOffsetRange(2, 5);
        Assert.assertEquals("ByteRanges should be equal", a, b);
        Assert.assertEquals("ByteRange hashcodes should be equal", a.hashCode(), b.hashCode());
        Assert.assertEquals("toString output not as expected", "ByteRange{startOffset=2, endOffset=5}", a.toString());
        a = ByteRanges.fromStartOffset(7);
        Assert.assertFalse("ByteRanges should not be equal", a.equals(b));
        b = ByteRanges.fromStartOffset(7);
        Assert.assertEquals("ByteRanges should be equal", a, b);
        Assert.assertEquals("ByteRange hashcodes should be equal", a.hashCode(), b.hashCode());
        Assert.assertEquals("toString output not as expected", "ByteRange{startOffset=7}", a.toString());
    }
}

