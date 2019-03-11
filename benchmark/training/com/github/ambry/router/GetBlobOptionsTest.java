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


import GetBlobOptions.OperationType;
import GetBlobOptions.OperationType.All;
import GetBlobOptions.OperationType.BlobInfo;
import GetBlobOptions.OperationType.Data;
import GetOption.Include_All;
import com.github.ambry.protocol.GetOption;
import com.github.ambry.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link GetBlobOptions} class for correctness.
 */
public class GetBlobOptionsTest {
    /**
     * Test that the range option can be assigned and retrieved correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRangeOption() throws Exception {
        long startOffset = 1;
        long endOffset = 2;
        ByteRange range = ByteRanges.fromOffsetRange(startOffset, endOffset);
        GetBlobOptions options = new GetBlobOptionsBuilder().operationType(All).range(ByteRanges.fromOffsetRange(startOffset, endOffset)).build();
        Assert.assertEquals("Range from options not as expected.", range, options.getRange());
    }

    /**
     * Test that the OperationType option can be assigned and retrieved correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOperationTypeOption() {
        GetBlobOptions options = new GetBlobOptionsBuilder().operationType(BlobInfo).build();
        Assert.assertEquals("OperationType from options not as expected.", BlobInfo, options.getOperationType());
    }

    /**
     * Test that the {@link GetOption} option can be assigned and retrieved correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetOptionOption() {
        GetBlobOptions options = new GetBlobOptionsBuilder().operationType(BlobInfo).getOption(Include_All).build();
        Assert.assertEquals("GetOption from options not as expected.", Include_All, options.getGetOption());
    }

    /**
     * Test the rawMode option
     */
    @Test
    public void testRawModeOption() {
        for (Boolean boolVal : TestUtils.BOOLEAN_VALUES) {
            GetBlobOptions options = new GetBlobOptionsBuilder().operationType(All).rawMode(boolVal).build();
            Assert.assertEquals("RawMode from options not as expected.", boolVal, options.isRawMode());
        }
    }

    /**
     * Test rawMode with invalid combinations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRawModeWithInvalidCombinations() throws Exception {
        // Test that using rawMode and range together fails.
        GetBlobOptionsBuilder options = new GetBlobOptionsBuilder().operationType(All).range(ByteRanges.fromOffsetRange(0, 1)).rawMode(true);
        TestUtils.assertException(IllegalArgumentException.class, () -> options.build(), null);
        // Test that using rawMode fails with OperationType other than ALL.
        options.range(null);
        options.operationType(BlobInfo);
        TestUtils.assertException(IllegalArgumentException.class, () -> options.build(), null);
        options.operationType(Data);
        TestUtils.assertException(IllegalArgumentException.class, () -> options.build(), null);
    }

    /**
     * Test toString, equals, and hashCode methods.
     */
    @Test
    public void testToStringEqualsAndHashcode() {
        ByteRange byteRange = ByteRanges.fromLastNBytes(4);
        GetOption getOption = GetOption.None;
        GetBlobOptions.OperationType type = OperationType.Data;
        GetBlobOptions a = new GetBlobOptionsBuilder().operationType(type).getOption(getOption).range(byteRange).build();
        GetBlobOptions b = new GetBlobOptionsBuilder().operationType(type).getOption(getOption).range(byteRange).build();
        Assert.assertEquals("GetBlobOptions should be equal", a, b);
        Assert.assertEquals("GetBlobOptions hashcodes should be equal", a.hashCode(), b.hashCode());
        Assert.assertEquals("GetBlobOptions toString should be equal", a.toString(), b.toString());
        // Change OperationType
        b = new GetBlobOptionsBuilder().operationType(All).getOption(getOption).range(byteRange).build();
        GetBlobOptionsTest.assertOptionsAreDistinct(a, b);
        // Change GetOption
        b = new GetBlobOptionsBuilder().operationType(type).getOption(Include_All).range(byteRange).build();
        GetBlobOptionsTest.assertOptionsAreDistinct(a, b);
        // Change range
        b = new GetBlobOptionsBuilder().operationType(type).getOption(getOption).range(ByteRanges.fromOffsetRange(2, 7)).build();
        GetBlobOptionsTest.assertOptionsAreDistinct(a, b);
        // Change rawMode (need to omit range)
        a = new GetBlobOptionsBuilder().operationType(All).getOption(getOption).build();
        b = new GetBlobOptionsBuilder().operationType(All).getOption(getOption).rawMode(true).build();
        GetBlobOptionsTest.assertOptionsAreDistinct(a, b);
    }
}

