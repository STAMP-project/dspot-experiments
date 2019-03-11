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
package com.google.android.exoplayer2.extractor.mkv;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import java.io.EOFException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link VarintReader}.
 */
@RunWith(RobolectricTestRunner.class)
public final class VarintReaderTest {
    private static final byte MAX_BYTE = ((byte) (255));

    private static final byte[] DATA_1_BYTE_0 = new byte[]{ ((byte) (128)) };

    private static final byte[] DATA_2_BYTE_0 = new byte[]{ 64, 0 };

    private static final byte[] DATA_3_BYTE_0 = new byte[]{ 32, 0, 0 };

    private static final byte[] DATA_4_BYTE_0 = new byte[]{ 16, 0, 0, 0 };

    private static final byte[] DATA_5_BYTE_0 = new byte[]{ 8, 0, 0, 0, 0 };

    private static final byte[] DATA_6_BYTE_0 = new byte[]{ 4, 0, 0, 0, 0, 0 };

    private static final byte[] DATA_7_BYTE_0 = new byte[]{ 2, 0, 0, 0, 0, 0, 0 };

    private static final byte[] DATA_8_BYTE_0 = new byte[]{ 1, 0, 0, 0, 0, 0, 0, 0 };

    private static final byte[] DATA_1_BYTE_64 = new byte[]{ ((byte) (192)) };

    private static final byte[] DATA_2_BYTE_64 = new byte[]{ 64, 64 };

    private static final byte[] DATA_3_BYTE_64 = new byte[]{ 32, 0, 64 };

    private static final byte[] DATA_4_BYTE_64 = new byte[]{ 16, 0, 0, 64 };

    private static final byte[] DATA_5_BYTE_64 = new byte[]{ 8, 0, 0, 0, 64 };

    private static final byte[] DATA_6_BYTE_64 = new byte[]{ 4, 0, 0, 0, 0, 64 };

    private static final byte[] DATA_7_BYTE_64 = new byte[]{ 2, 0, 0, 0, 0, 0, 64 };

    private static final byte[] DATA_8_BYTE_64 = new byte[]{ 1, 0, 0, 0, 0, 0, 0, 64 };

    private static final byte[] DATA_1_BYTE_MAX = new byte[]{ VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_2_BYTE_MAX = new byte[]{ 127, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_3_BYTE_MAX = new byte[]{ 63, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_4_BYTE_MAX = new byte[]{ 31, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_5_BYTE_MAX = new byte[]{ 15, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_6_BYTE_MAX = new byte[]{ 7, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_7_BYTE_MAX = new byte[]{ 3, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final byte[] DATA_8_BYTE_MAX = new byte[]{ 1, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE, VarintReaderTest.MAX_BYTE };

    private static final long VALUE_1_BYTE_MAX = 127;

    private static final long VALUE_1_BYTE_MAX_WITH_MASK = 255;

    private static final long VALUE_2_BYTE_MAX = 16383;

    private static final long VALUE_2_BYTE_MAX_WITH_MASK = 32767;

    private static final long VALUE_3_BYTE_MAX = 2097151;

    private static final long VALUE_3_BYTE_MAX_WITH_MASK = 4194303;

    private static final long VALUE_4_BYTE_MAX = 268435455;

    private static final long VALUE_4_BYTE_MAX_WITH_MASK = 536870911;

    private static final long VALUE_5_BYTE_MAX = 34359738367L;

    private static final long VALUE_5_BYTE_MAX_WITH_MASK = 68719476735L;

    private static final long VALUE_6_BYTE_MAX = 4398046511103L;

    private static final long VALUE_6_BYTE_MAX_WITH_MASK = 8796093022207L;

    private static final long VALUE_7_BYTE_MAX = 562949953421311L;

    private static final long VALUE_7_BYTE_MAX_WITH_MASK = 1125899906842623L;

    private static final long VALUE_8_BYTE_MAX = 72057594037927935L;

    private static final long VALUE_8_BYTE_MAX_WITH_MASK = 144115188075855871L;

    @Test
    public void testReadVarintEndOfInputAtStart() throws IOException, InterruptedException {
        VarintReader reader = new VarintReader();
        // Build an input with no data.
        ExtractorInput input = new FakeExtractorInput.Builder().setSimulateUnknownLength(true).build();
        // End of input allowed.
        long result = reader.readUnsignedVarint(input, true, false, 8);
        assertThat(result).isEqualTo(C.RESULT_END_OF_INPUT);
        // End of input not allowed.
        try {
            reader.readUnsignedVarint(input, false, false, 8);
            Assert.fail();
        } catch (EOFException e) {
            // Expected.
        }
    }

    @Test
    public void testReadVarintExceedsMaximumAllowedLength() throws IOException, InterruptedException {
        VarintReader reader = new VarintReader();
        ExtractorInput input = new FakeExtractorInput.Builder().setData(VarintReaderTest.DATA_8_BYTE_0).setSimulateUnknownLength(true).build();
        long result = reader.readUnsignedVarint(input, false, true, 4);
        assertThat(result).isEqualTo(C.RESULT_MAX_LENGTH_EXCEEDED);
    }

    @Test
    public void testReadVarint() throws IOException, InterruptedException {
        VarintReader reader = new VarintReader();
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_1_BYTE_0, 1, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_2_BYTE_0, 2, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_3_BYTE_0, 3, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_4_BYTE_0, 4, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_5_BYTE_0, 5, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_6_BYTE_0, 6, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_7_BYTE_0, 7, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_8_BYTE_0, 8, 0);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_1_BYTE_64, 1, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_2_BYTE_64, 2, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_3_BYTE_64, 3, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_4_BYTE_64, 4, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_5_BYTE_64, 5, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_6_BYTE_64, 6, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_7_BYTE_64, 7, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_8_BYTE_64, 8, 64);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_1_BYTE_MAX, 1, VarintReaderTest.VALUE_1_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_2_BYTE_MAX, 2, VarintReaderTest.VALUE_2_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_3_BYTE_MAX, 3, VarintReaderTest.VALUE_3_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_4_BYTE_MAX, 4, VarintReaderTest.VALUE_4_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_5_BYTE_MAX, 5, VarintReaderTest.VALUE_5_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_6_BYTE_MAX, 6, VarintReaderTest.VALUE_6_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_7_BYTE_MAX, 7, VarintReaderTest.VALUE_7_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, true, VarintReaderTest.DATA_8_BYTE_MAX, 8, VarintReaderTest.VALUE_8_BYTE_MAX);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_1_BYTE_MAX, 1, VarintReaderTest.VALUE_1_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_2_BYTE_MAX, 2, VarintReaderTest.VALUE_2_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_3_BYTE_MAX, 3, VarintReaderTest.VALUE_3_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_4_BYTE_MAX, 4, VarintReaderTest.VALUE_4_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_5_BYTE_MAX, 5, VarintReaderTest.VALUE_5_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_6_BYTE_MAX, 6, VarintReaderTest.VALUE_6_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_7_BYTE_MAX, 7, VarintReaderTest.VALUE_7_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarint(reader, false, VarintReaderTest.DATA_8_BYTE_MAX, 8, VarintReaderTest.VALUE_8_BYTE_MAX_WITH_MASK);
    }

    @Test
    public void testReadVarintFlaky() throws IOException, InterruptedException {
        VarintReader reader = new VarintReader();
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_1_BYTE_0, 1, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_2_BYTE_0, 2, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_3_BYTE_0, 3, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_4_BYTE_0, 4, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_5_BYTE_0, 5, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_6_BYTE_0, 6, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_7_BYTE_0, 7, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_8_BYTE_0, 8, 0);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_1_BYTE_64, 1, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_2_BYTE_64, 2, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_3_BYTE_64, 3, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_4_BYTE_64, 4, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_5_BYTE_64, 5, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_6_BYTE_64, 6, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_7_BYTE_64, 7, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_8_BYTE_64, 8, 64);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_1_BYTE_MAX, 1, VarintReaderTest.VALUE_1_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_2_BYTE_MAX, 2, VarintReaderTest.VALUE_2_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_3_BYTE_MAX, 3, VarintReaderTest.VALUE_3_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_4_BYTE_MAX, 4, VarintReaderTest.VALUE_4_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_5_BYTE_MAX, 5, VarintReaderTest.VALUE_5_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_6_BYTE_MAX, 6, VarintReaderTest.VALUE_6_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_7_BYTE_MAX, 7, VarintReaderTest.VALUE_7_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, true, VarintReaderTest.DATA_8_BYTE_MAX, 8, VarintReaderTest.VALUE_8_BYTE_MAX);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_1_BYTE_MAX, 1, VarintReaderTest.VALUE_1_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_2_BYTE_MAX, 2, VarintReaderTest.VALUE_2_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_3_BYTE_MAX, 3, VarintReaderTest.VALUE_3_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_4_BYTE_MAX, 4, VarintReaderTest.VALUE_4_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_5_BYTE_MAX, 5, VarintReaderTest.VALUE_5_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_6_BYTE_MAX, 6, VarintReaderTest.VALUE_6_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_7_BYTE_MAX, 7, VarintReaderTest.VALUE_7_BYTE_MAX_WITH_MASK);
        VarintReaderTest.testReadVarintFlaky(reader, false, VarintReaderTest.DATA_8_BYTE_MAX, 8, VarintReaderTest.VALUE_8_BYTE_MAX_WITH_MASK);
    }
}

