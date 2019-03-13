/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream;


import C.LENGTH_UNSET;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link ByteArrayDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ByteArrayDataSourceTest {
    private static final byte[] TEST_DATA = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    private static final byte[] TEST_DATA_ODD = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    @Test
    public void testFullReadSingleBytes() {
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, LENGTH_UNSET, 1, 0, 1, false);
    }

    @Test
    public void testFullReadAllBytes() {
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, LENGTH_UNSET, 100, 0, 100, false);
    }

    @Test
    public void testLimitReadSingleBytes() {
        // Limit set to the length of the data.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, ByteArrayDataSourceTest.TEST_DATA.length, 1, 0, 1, false);
        // And less.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, 6, 1, 0, 1, false);
    }

    @Test
    public void testFullReadTwoBytes() {
        // Try with the total data length an exact multiple of the size of each individual read.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, LENGTH_UNSET, 2, 0, 2, false);
        // And not.
        readTestData(ByteArrayDataSourceTest.TEST_DATA_ODD, 0, LENGTH_UNSET, 2, 0, 2, false);
    }

    @Test
    public void testLimitReadTwoBytes() {
        // Try with the limit an exact multiple of the size of each individual read.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, 6, 2, 0, 2, false);
        // And not.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, 7, 2, 0, 2, false);
    }

    @Test
    public void testReadFromValidOffsets() {
        // Read from an offset without bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 1, LENGTH_UNSET, 1, 0, 1, false);
        // And with bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 1, 6, 1, 0, 1, false);
        // Read from the last possible offset without bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, ((ByteArrayDataSourceTest.TEST_DATA.length) - 1), LENGTH_UNSET, 1, 0, 1, false);
        // And with bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, ((ByteArrayDataSourceTest.TEST_DATA.length) - 1), 1, 1, 0, 1, false);
    }

    @Test
    public void testReadFromInvalidOffsets() {
        // Read from first invalid offset and check failure without bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, ByteArrayDataSourceTest.TEST_DATA.length, LENGTH_UNSET, 1, 0, 1, true);
        // And with bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, ByteArrayDataSourceTest.TEST_DATA.length, 1, 1, 0, 1, true);
    }

    @Test
    public void testReadWithInvalidLength() {
        // Read more data than is available.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 0, ((ByteArrayDataSourceTest.TEST_DATA.length) + 1), 1, 0, 1, true);
        // And with bound.
        readTestData(ByteArrayDataSourceTest.TEST_DATA, 1, ByteArrayDataSourceTest.TEST_DATA.length, 1, 0, 1, true);
    }
}

