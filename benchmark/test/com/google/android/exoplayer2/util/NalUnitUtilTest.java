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
package com.google.android.exoplayer2.util;


import NalUnitUtil.SpsData;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link NalUnitUtil}.
 */
@RunWith(RobolectricTestRunner.class)
public final class NalUnitUtilTest {
    private static final int TEST_PARTIAL_NAL_POSITION = 4;

    private static final int TEST_NAL_POSITION = 10;

    private static final byte[] SPS_TEST_DATA = createByteArray(0, 0, 1, 103, 77, 64, 22, 236, 160, 80, 23, 252, 184, 8, 128, 0, 0, 3, 0, 128, 0, 0, 15, 71, 139, 22, 203);

    private static final int SPS_TEST_DATA_OFFSET = 3;

    @Test
    public void testFindNalUnit() {
        byte[] data = NalUnitUtilTest.buildTestData();
        // Should find NAL unit.
        int result = NalUnitUtil.findNalUnit(data, 0, data.length, null);
        assertThat(result).isEqualTo(NalUnitUtilTest.TEST_NAL_POSITION);
        // Should find NAL unit whose prefix ends one byte before the limit.
        result = NalUnitUtil.findNalUnit(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 4), null);
        assertThat(result).isEqualTo(NalUnitUtilTest.TEST_NAL_POSITION);
        // Shouldn't find NAL unit whose prefix ends at the limit (since the limit is exclusive).
        result = NalUnitUtil.findNalUnit(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 3), null);
        assertThat(result).isEqualTo(((NalUnitUtilTest.TEST_NAL_POSITION) + 3));
        // Should find NAL unit whose prefix starts at the offset.
        result = NalUnitUtil.findNalUnit(data, NalUnitUtilTest.TEST_NAL_POSITION, data.length, null);
        assertThat(result).isEqualTo(NalUnitUtilTest.TEST_NAL_POSITION);
        // Shouldn't find NAL unit whose prefix starts one byte past the offset.
        result = NalUnitUtil.findNalUnit(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1), data.length, null);
        assertThat(result).isEqualTo(data.length);
    }

    @Test
    public void testFindNalUnitWithPrefix() {
        byte[] data = NalUnitUtilTest.buildTestData();
        // First byte of NAL unit in data1, rest in data2.
        boolean[] prefixFlags = new boolean[3];
        byte[] data1 = Arrays.copyOfRange(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1));
        byte[] data2 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1), data.length);
        int result = NalUnitUtil.findNalUnit(data1, 0, data1.length, prefixFlags);
        assertThat(result).isEqualTo(data1.length);
        result = NalUnitUtil.findNalUnit(data2, 0, data2.length, prefixFlags);
        assertThat(result).isEqualTo((-1));
        NalUnitUtilTest.assertPrefixFlagsCleared(prefixFlags);
        // First three bytes of NAL unit in data1, rest in data2.
        prefixFlags = new boolean[3];
        data1 = Arrays.copyOfRange(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 3));
        data2 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 3), data.length);
        result = NalUnitUtil.findNalUnit(data1, 0, data1.length, prefixFlags);
        assertThat(result).isEqualTo(data1.length);
        result = NalUnitUtil.findNalUnit(data2, 0, data2.length, prefixFlags);
        assertThat(result).isEqualTo((-3));
        NalUnitUtilTest.assertPrefixFlagsCleared(prefixFlags);
        // First byte of NAL unit in data1, second byte in data2, rest in data3.
        prefixFlags = new boolean[3];
        data1 = Arrays.copyOfRange(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1));
        data2 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1), ((NalUnitUtilTest.TEST_NAL_POSITION) + 2));
        byte[] data3 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 2), data.length);
        result = NalUnitUtil.findNalUnit(data1, 0, data1.length, prefixFlags);
        assertThat(result).isEqualTo(data1.length);
        result = NalUnitUtil.findNalUnit(data2, 0, data2.length, prefixFlags);
        assertThat(result).isEqualTo(data2.length);
        result = NalUnitUtil.findNalUnit(data3, 0, data3.length, prefixFlags);
        assertThat(result).isEqualTo((-2));
        NalUnitUtilTest.assertPrefixFlagsCleared(prefixFlags);
        // NAL unit split with one byte in four arrays.
        prefixFlags = new boolean[3];
        data1 = Arrays.copyOfRange(data, 0, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1));
        data2 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 1), ((NalUnitUtilTest.TEST_NAL_POSITION) + 2));
        data3 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 2), ((NalUnitUtilTest.TEST_NAL_POSITION) + 3));
        byte[] data4 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_NAL_POSITION) + 2), data.length);
        result = NalUnitUtil.findNalUnit(data1, 0, data1.length, prefixFlags);
        assertThat(result).isEqualTo(data1.length);
        result = NalUnitUtil.findNalUnit(data2, 0, data2.length, prefixFlags);
        assertThat(result).isEqualTo(data2.length);
        result = NalUnitUtil.findNalUnit(data3, 0, data3.length, prefixFlags);
        assertThat(result).isEqualTo(data3.length);
        result = NalUnitUtil.findNalUnit(data4, 0, data4.length, prefixFlags);
        assertThat(result).isEqualTo((-3));
        NalUnitUtilTest.assertPrefixFlagsCleared(prefixFlags);
        // NAL unit entirely in data2. data1 ends with partial prefix.
        prefixFlags = new boolean[3];
        data1 = Arrays.copyOfRange(data, 0, ((NalUnitUtilTest.TEST_PARTIAL_NAL_POSITION) + 2));
        data2 = Arrays.copyOfRange(data, ((NalUnitUtilTest.TEST_PARTIAL_NAL_POSITION) + 2), data.length);
        result = NalUnitUtil.findNalUnit(data1, 0, data1.length, prefixFlags);
        assertThat(result).isEqualTo(data1.length);
        result = NalUnitUtil.findNalUnit(data2, 0, data2.length, prefixFlags);
        assertThat(result).isEqualTo(4);
        NalUnitUtilTest.assertPrefixFlagsCleared(prefixFlags);
    }

    @Test
    public void testParseSpsNalUnit() {
        NalUnitUtil.SpsData data = NalUnitUtil.parseSpsNalUnit(NalUnitUtilTest.SPS_TEST_DATA, NalUnitUtilTest.SPS_TEST_DATA_OFFSET, NalUnitUtilTest.SPS_TEST_DATA.length);
        assertThat(data.width).isEqualTo(640);
        assertThat(data.height).isEqualTo(360);
        assertThat(data.deltaPicOrderAlwaysZeroFlag).isFalse();
        assertThat(data.frameMbsOnlyFlag).isTrue();
        assertThat(data.frameNumLength).isEqualTo(4);
        assertThat(data.picOrderCntLsbLength).isEqualTo(6);
        assertThat(data.seqParameterSetId).isEqualTo(0);
        assertThat(data.pixelWidthAspectRatio).isEqualTo(1.0F);
        assertThat(data.picOrderCountType).isEqualTo(0);
        assertThat(data.separateColorPlaneFlag).isFalse();
    }

    @Test
    public void testUnescapeDoesNotModifyBuffersWithoutStartCodes() {
        NalUnitUtilTest.assertUnescapeDoesNotModify("");
        NalUnitUtilTest.assertUnescapeDoesNotModify("0000");
        NalUnitUtilTest.assertUnescapeDoesNotModify("172BF38A3C");
        NalUnitUtilTest.assertUnescapeDoesNotModify("000004");
    }

    @Test
    public void testUnescapeModifiesBuffersWithStartCodes() {
        NalUnitUtilTest.assertUnescapeMatchesExpected("00000301", "000001");
        NalUnitUtilTest.assertUnescapeMatchesExpected("0000030200000300", "000002000000");
    }

    @Test
    public void testDiscardToSps() {
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("00", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("FFFF000001", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("00000001", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("00000001FF67", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("00000001000167", "");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("0000000167", "0000000167");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("0000000167FF", "0000000167FF");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("0000000167FF", "0000000167FF");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("0000000167FF000000016700", "0000000167FF000000016700");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("000000000167FF", "0000000167FF");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("0001670000000167FF", "0000000167FF");
        NalUnitUtilTest.assertDiscardToSpsMatchesExpected("FF00000001660000000167FF", "0000000167FF");
    }
}

