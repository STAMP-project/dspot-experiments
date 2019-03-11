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


import C.TRACK_TYPE_AUDIO;
import C.TRACK_TYPE_VIDEO;
import C.TYPE_OTHER;
import C.TYPE_SS;
import com.google.android.exoplayer2.testutil.TestUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link Util}.
 */
@RunWith(RobolectricTestRunner.class)
public class UtilTest {
    @Test
    public void testAddWithOverflowDefault() {
        long res = /* overflowResult= */
        Util.addWithOverflowDefault(5, 10, 0);
        assertThat(res).isEqualTo(15);
        res = /* overflowResult= */
        Util.addWithOverflowDefault(((Long.MAX_VALUE) - 1), 1, 12345);
        assertThat(res).isEqualTo(Long.MAX_VALUE);
        res = /* overflowResult= */
        Util.addWithOverflowDefault(((Long.MIN_VALUE) + 1), (-1), 12345);
        assertThat(res).isEqualTo(Long.MIN_VALUE);
        res = /* overflowResult= */
        Util.addWithOverflowDefault(Long.MAX_VALUE, 1, 12345);
        assertThat(res).isEqualTo(12345);
        res = /* overflowResult= */
        Util.addWithOverflowDefault(Long.MIN_VALUE, (-1), 12345);
        assertThat(res).isEqualTo(12345);
    }

    @Test
    public void testSubtrackWithOverflowDefault() {
        long res = /* overflowResult= */
        Util.subtractWithOverflowDefault(5, 10, 0);
        assertThat(res).isEqualTo((-5));
        res = /* overflowResult= */
        Util.subtractWithOverflowDefault(((Long.MIN_VALUE) + 1), 1, 12345);
        assertThat(res).isEqualTo(Long.MIN_VALUE);
        res = /* overflowResult= */
        Util.subtractWithOverflowDefault(((Long.MAX_VALUE) - 1), (-1), 12345);
        assertThat(res).isEqualTo(Long.MAX_VALUE);
        res = /* overflowResult= */
        Util.subtractWithOverflowDefault(Long.MIN_VALUE, 1, 12345);
        assertThat(res).isEqualTo(12345);
        res = /* overflowResult= */
        Util.subtractWithOverflowDefault(Long.MAX_VALUE, (-1), 12345);
        assertThat(res).isEqualTo(12345);
    }

    @Test
    public void testInferContentType() {
        assertThat(Util.inferContentType("http://a.b/c.ism")).isEqualTo(TYPE_SS);
        assertThat(Util.inferContentType("http://a.b/c.isml")).isEqualTo(TYPE_SS);
        assertThat(Util.inferContentType("http://a.b/c.ism/Manifest")).isEqualTo(TYPE_SS);
        assertThat(Util.inferContentType("http://a.b/c.isml/manifest")).isEqualTo(TYPE_SS);
        assertThat(Util.inferContentType("http://a.b/c.isml/manifest(filter=x)")).isEqualTo(TYPE_SS);
        assertThat(Util.inferContentType("http://a.b/c.ism/prefix-manifest")).isEqualTo(TYPE_OTHER);
        assertThat(Util.inferContentType("http://a.b/c.ism/manifest-suffix")).isEqualTo(TYPE_OTHER);
    }

    @Test
    public void testArrayBinarySearchFloor() {
        long[] values = new long[0];
        assertThat(Util.binarySearchFloor(values, 0, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, false, true)).isEqualTo(0);
        values = new long[]{ 1, 3, 5 };
        assertThat(Util.binarySearchFloor(values, 0, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, true, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, false, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 0, true, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 1, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, false, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, true, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 4, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 4, true, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 5, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 5, true, false)).isEqualTo(2);
        assertThat(Util.binarySearchFloor(values, 6, false, false)).isEqualTo(2);
        assertThat(Util.binarySearchFloor(values, 6, true, false)).isEqualTo(2);
    }

    @Test
    public void testListBinarySearchFloor() {
        List<Integer> values = new ArrayList<>();
        assertThat(Util.binarySearchFloor(values, 0, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, false, true)).isEqualTo(0);
        values.add(1);
        values.add(3);
        values.add(5);
        assertThat(Util.binarySearchFloor(values, 0, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, true, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 0, false, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 0, true, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, false, false)).isEqualTo((-1));
        assertThat(Util.binarySearchFloor(values, 1, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, false, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 1, true, true)).isEqualTo(0);
        assertThat(Util.binarySearchFloor(values, 4, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 4, true, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 5, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchFloor(values, 5, true, false)).isEqualTo(2);
        assertThat(Util.binarySearchFloor(values, 6, false, false)).isEqualTo(2);
        assertThat(Util.binarySearchFloor(values, 6, true, false)).isEqualTo(2);
    }

    @Test
    public void testArrayBinarySearchCeil() {
        long[] values = new long[0];
        assertThat(Util.binarySearchCeil(values, 0, false, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 0, false, true)).isEqualTo((-1));
        values = new long[]{ 1, 3, 5 };
        assertThat(Util.binarySearchCeil(values, 0, false, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 0, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 1, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 1, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 2, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 2, true, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 5, false, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 5, true, false)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 5, false, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 5, true, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 6, false, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 6, true, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 6, false, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 6, true, true)).isEqualTo(2);
    }

    @Test
    public void testListBinarySearchCeil() {
        List<Integer> values = new ArrayList<>();
        assertThat(Util.binarySearchCeil(values, 0, false, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 0, false, true)).isEqualTo((-1));
        values.add(1);
        values.add(3);
        values.add(5);
        assertThat(Util.binarySearchCeil(values, 0, false, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 0, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 1, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 1, true, false)).isEqualTo(0);
        assertThat(Util.binarySearchCeil(values, 2, false, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 2, true, false)).isEqualTo(1);
        assertThat(Util.binarySearchCeil(values, 5, false, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 5, true, false)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 5, false, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 5, true, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 6, false, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 6, true, false)).isEqualTo(3);
        assertThat(Util.binarySearchCeil(values, 6, false, true)).isEqualTo(2);
        assertThat(Util.binarySearchCeil(values, 6, true, true)).isEqualTo(2);
    }

    @Test
    public void testParseXsDuration() {
        assertThat(Util.parseXsDuration("PT150.279S")).isEqualTo(150279L);
        assertThat(Util.parseXsDuration("PT1.500S")).isEqualTo(1500L);
    }

    @Test
    public void testParseXsDateTime() throws Exception {
        assertThat(Util.parseXsDateTime("2014-06-19T23:07:42")).isEqualTo(1403219262000L);
        assertThat(Util.parseXsDateTime("2014-08-06T11:00:00Z")).isEqualTo(1407322800000L);
        assertThat(Util.parseXsDateTime("2014-08-06T11:00:00,000Z")).isEqualTo(1407322800000L);
        assertThat(Util.parseXsDateTime("2014-09-19T13:18:55-08:00")).isEqualTo(1411161535000L);
        assertThat(Util.parseXsDateTime("2014-09-19T13:18:55-0800")).isEqualTo(1411161535000L);
        assertThat(Util.parseXsDateTime("2014-09-19T13:18:55.000-0800")).isEqualTo(1411161535000L);
        assertThat(Util.parseXsDateTime("2014-09-19T13:18:55.000-800")).isEqualTo(1411161535000L);
    }

    @Test
    public void testGetCodecsOfType() {
        assertThat(Util.getCodecsOfType(null, TRACK_TYPE_VIDEO)).isNull();
        assertThat(Util.getCodecsOfType("avc1.64001e,vp9.63.1", TRACK_TYPE_AUDIO)).isNull();
        assertThat(Util.getCodecsOfType(" vp9.63.1, ec-3 ", TRACK_TYPE_AUDIO)).isEqualTo("ec-3");
        assertThat(Util.getCodecsOfType("avc1.61e, vp9.63.1, ec-3 ", TRACK_TYPE_VIDEO)).isEqualTo("avc1.61e,vp9.63.1");
        assertThat(Util.getCodecsOfType("avc1.61e, vp9.63.1, ec-3 ", TRACK_TYPE_VIDEO)).isEqualTo("avc1.61e,vp9.63.1");
        assertThat(Util.getCodecsOfType("invalidCodec1, invalidCodec2 ", TRACK_TYPE_AUDIO)).isNull();
    }

    @Test
    public void testUnescapeInvalidFileName() {
        assertThat(Util.unescapeFileName("%a")).isNull();
        assertThat(Util.unescapeFileName("%xyz")).isNull();
    }

    @Test
    public void testEscapeUnescapeFileName() {
        UtilTest.assertEscapeUnescapeFileName("just+a regular+fileName", "just+a regular+fileName");
        UtilTest.assertEscapeUnescapeFileName("key:value", "key%3avalue");
        UtilTest.assertEscapeUnescapeFileName("<>:\"/\\|?*%", "%3c%3e%3a%22%2f%5c%7c%3f%2a%25");
        Random random = new Random(0);
        for (int i = 0; i < 1000; i++) {
            String string = TestUtil.buildTestString(1000, random);
            UtilTest.assertEscapeUnescapeFileName(string);
        }
    }

    @Test
    public void testInflate() {
        byte[] testData = /* arbitrary test data size */
        TestUtil.buildTestData((256 * 1024));
        byte[] compressedData = new byte[(testData.length) * 2];
        Deflater compresser = new Deflater(9);
        compresser.setInput(testData);
        compresser.finish();
        int compressedDataLength = compresser.deflate(compressedData);
        compresser.end();
        ParsableByteArray input = new ParsableByteArray(compressedData, compressedDataLength);
        ParsableByteArray output = new ParsableByteArray();
        assertThat(/* inflater= */
        Util.inflate(input, output, null)).isTrue();
        assertThat(output.limit()).isEqualTo(testData.length);
        assertThat(Arrays.copyOf(output.data, output.limit())).isEqualTo(testData);
    }
}

