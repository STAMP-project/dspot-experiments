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
 * Tests {@link JfifUtil}
 */
@RunWith(RobolectricTestRunner.class)
public class JfifUtilTest {
    // Test cases without APP1 block
    final String NO_ORI_IMAGE_1 = ((((((((JfifTestUtils.SOI) + (JfifTestUtils.APP0)) + (JfifTestUtils.APP2)) + (JfifTestUtils.DQT)) + (JfifTestUtils.DHT)) + (JfifTestUtils.DRI)) + (JfifTestUtils.SOF)) + (JfifTestUtils.SOS)) + (JfifTestUtils.EOI);

    final String NO_ORI_IMAGE_2 = (((((JfifTestUtils.SOI) + (JfifTestUtils.DQT)) + (JfifTestUtils.DHT)) + (JfifTestUtils.SOF)) + (JfifTestUtils.SOS)) + (JfifTestUtils.EOI);

    @Test
    public void testGetOrientation_NoAPP1() {
        Assert.assertEquals(0, JfifUtil.getOrientation(JfifTestUtils.hexStringToByteArray(NO_ORI_IMAGE_1)));
        Assert.assertEquals(0, JfifUtil.getOrientation(JfifTestUtils.hexStringToByteArray(NO_ORI_IMAGE_2)));
    }

    @Test
    public void testGetOrientation_BigEndian() {
        testGetOrientation_WithEndian(false);
    }

    @Test
    public void testGetOrientation_LittleEndian() {
        testGetOrientation_WithEndian(true);
    }
}

