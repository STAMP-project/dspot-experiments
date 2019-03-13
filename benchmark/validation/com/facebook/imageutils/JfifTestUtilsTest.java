/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imageutils;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link JfifTestUtils}
 */
@RunWith(RobolectricTestRunner.class)
public class JfifTestUtilsTest {
    private String mTestStr = "0123456789";

    @Test
    public void testMakeSOFSection() {
        Assert.assertEquals("FFC0000A0300FF0136000000", // length
        // bit depth
        // width
        JfifTestUtils.makeSOFSection(10, 3, 310, 255));// height

        Assert.assertEquals("FFC0001401013600FF00000000000000000000000000", // length
        // bit depth
        // width
        JfifTestUtils.makeSOFSection(20, 1, 255, 310));// height

    }

    @Test
    public void testNumBytes() {
        Assert.assertEquals(1, JfifTestUtils.numBytes("    3F        "));
        Assert.assertEquals(4, JfifTestUtils.numBytes("1A 2B 3C 4D"));
        Assert.assertEquals(6, JfifTestUtils.numBytes("1A2B 3C4D5E6F"));
    }

    @Test
    public void testHexStringToByteArray() {
        Assert.assertArrayEquals(new byte[]{ 63 }, JfifTestUtils.hexStringToByteArray("    3F        "));
        Assert.assertArrayEquals(new byte[]{ 26, 43, 60, 77 }, JfifTestUtils.hexStringToByteArray("1A 2B 3C 4D"));
        Assert.assertArrayEquals(new byte[]{ 26, 43, 60, 77, 94, 111 }, JfifTestUtils.hexStringToByteArray("1A2B 3C4D5E6F"));
    }

    @Test
    public void testEncodeInt2HexString() {
        Assert.assertEquals("5B6FF432", JfifTestUtils.encodeInt2HexString(1534063666, 4, false));
        Assert.assertEquals("6FF4", JfifTestUtils.encodeInt2HexString(28660, 2, false));
        Assert.assertEquals("B6", JfifTestUtils.encodeInt2HexString(182, 1, false));
        Assert.assertEquals("32F46F5B", JfifTestUtils.encodeInt2HexString(1534063666, 4, true));
        Assert.assertEquals("F46F", JfifTestUtils.encodeInt2HexString(28660, 2, true));
        Assert.assertEquals("B6", JfifTestUtils.encodeInt2HexString(182, 1, true));
    }

    @Test
    public void testMakeOrientationEntry() {
        Assert.assertEquals("011200030000000100050000", JfifTestUtils.makeOrientationEntry(5, false));
        Assert.assertEquals("120103000100000005000000", JfifTestUtils.makeOrientationEntry(5, true));
    }

    @Test
    public void testMakeIfdEntry() {
        Assert.assertEquals("011200030000000100060000", JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_ORI_TAG, JfifTestUtils.TYPE_SHORT, 1, 6, 2, false));
        Assert.assertEquals("120103000200000003000000", JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_ORI_TAG, JfifTestUtils.TYPE_SHORT, 2, 3, 2, true));
    }

    @Test
    public void testMakeIfd() {
        // Test big endian
        String IFD_ENTRY_1 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_1, JfifTestUtils.TYPE_SHORT, 1, 255, 2, false);
        String IFD_ENTRY_2 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_2, JfifTestUtils.TYPE_SHORT, 1, 255, 2, false);
        String IFD_ENTRY_3 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_3, JfifTestUtils.TYPE_SHORT, 1, 255, 2, false);
        Assert.assertEquals(("0003" + ((("011A00030000000100FF0000" + "011B00030000000100FF0000") + "011C00030000000100FF0000") + "00000008")), JfifTestUtils.makeIfd(new String[]{ IFD_ENTRY_1, IFD_ENTRY_2, IFD_ENTRY_3 }, 8, false));
        // Test little endian
        IFD_ENTRY_1 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_1, JfifTestUtils.TYPE_SHORT, 1, 255, 2, true);
        IFD_ENTRY_2 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_2, JfifTestUtils.TYPE_SHORT, 1, 255, 2, true);
        IFD_ENTRY_3 = JfifTestUtils.makeIfdEntry(JfifTestUtils.IFD_ENTRY_TAG_3, JfifTestUtils.TYPE_SHORT, 1, 255, 2, true);
        Assert.assertEquals(("0300" + ((("1A01030001000000FF000000" + "1B01030001000000FF000000") + "1C01030001000000FF000000") + "09000000")), JfifTestUtils.makeIfd(new String[]{ IFD_ENTRY_1, IFD_ENTRY_2, IFD_ENTRY_3 }, 9, true));
    }

    @Test
    public void testMakeTiff() {
        Assert.assertEquals(((JfifTestUtils.TIFF_HEADER_BE) + (mTestStr)), JfifTestUtils.makeTiff(mTestStr, false));
        Assert.assertEquals(((JfifTestUtils.TIFF_HEADER_LE) + (mTestStr)), JfifTestUtils.makeTiff(mTestStr, true));
    }

    @Test
    public void testMakeAPP1_EXIF() {
        Assert.assertEquals(((((JfifTestUtils.APP1_MARKER) + "000D") + (JfifTestUtils.APP1_EXIF_MAGIC)) + (mTestStr)), JfifTestUtils.makeAPP1_EXIF(mTestStr));
    }

    @Test
    public void testMakeTestImageWithAPP1() {
        Assert.assertEquals(((((((((JfifTestUtils.SOI) + (JfifTestUtils.APP0)) + (mTestStr)) + (JfifTestUtils.DQT)) + (JfifTestUtils.DHT)) + (JfifTestUtils.SOF)) + (JfifTestUtils.SOS)) + (JfifTestUtils.EOI)), JfifTestUtils.makeTestImageWithAPP1(mTestStr));
    }
}

