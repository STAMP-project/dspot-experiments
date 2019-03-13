/**
 * Copyright 2014 ZXing authors
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
package com.google.zxing;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link RGBLuminanceSource}.
 */
public final class RGBLuminanceSourceTestCase extends Assert {
    private static final RGBLuminanceSource SOURCE = new RGBLuminanceSource(3, 3, new int[]{ 0, 8355711, 16777215, 16711680, 65280, 255, 255, 65280, 16711680 });

    @Test
    public void testCrop() {
        Assert.assertTrue(RGBLuminanceSourceTestCase.SOURCE.isCropSupported());
        LuminanceSource cropped = RGBLuminanceSourceTestCase.SOURCE.crop(1, 1, 1, 1);
        Assert.assertEquals(1, cropped.getHeight());
        Assert.assertEquals(1, cropped.getWidth());
        Assert.assertArrayEquals(new byte[]{ 127 }, cropped.getRow(0, null));
    }

    @Test
    public void testMatrix() {
        Assert.assertArrayEquals(new byte[]{ 0, 127, ((byte) (255)), 63, 127, 63, 63, 127, 63 }, RGBLuminanceSourceTestCase.SOURCE.getMatrix());
        LuminanceSource croppedFullWidth = RGBLuminanceSourceTestCase.SOURCE.crop(0, 1, 3, 2);
        Assert.assertArrayEquals(new byte[]{ 63, 127, 63, 63, 127, 63 }, croppedFullWidth.getMatrix());
        LuminanceSource croppedCorner = RGBLuminanceSourceTestCase.SOURCE.crop(1, 1, 2, 2);
        Assert.assertArrayEquals(new byte[]{ 127, 63, 127, 63 }, croppedCorner.getMatrix());
    }

    @Test
    public void testGetRow() {
        Assert.assertArrayEquals(new byte[]{ 63, 127, 63 }, RGBLuminanceSourceTestCase.SOURCE.getRow(2, new byte[3]));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("#+ \n#+#\n#+#\n", RGBLuminanceSourceTestCase.SOURCE.toString());
    }
}

