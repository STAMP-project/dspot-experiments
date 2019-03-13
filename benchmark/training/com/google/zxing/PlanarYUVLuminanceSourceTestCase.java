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
 * Tests {@link PlanarYUVLuminanceSource}.
 */
public final class PlanarYUVLuminanceSourceTestCase extends Assert {
    private static final byte[] YUV = new byte[]{ 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 0, -1, -1, -2, -3, -5, -8, -13, -21, -34, -55, -89, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127 };

    private static final int COLS = 6;

    private static final int ROWS = 4;

    private static final byte[] Y = new byte[(PlanarYUVLuminanceSourceTestCase.COLS) * (PlanarYUVLuminanceSourceTestCase.ROWS)];

    static {
        System.arraycopy(PlanarYUVLuminanceSourceTestCase.YUV, 0, PlanarYUVLuminanceSourceTestCase.Y, 0, PlanarYUVLuminanceSourceTestCase.Y.length);
    }

    @Test
    public void testNoCrop() {
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(PlanarYUVLuminanceSourceTestCase.YUV, PlanarYUVLuminanceSourceTestCase.COLS, PlanarYUVLuminanceSourceTestCase.ROWS, 0, 0, PlanarYUVLuminanceSourceTestCase.COLS, PlanarYUVLuminanceSourceTestCase.ROWS, false);
        PlanarYUVLuminanceSourceTestCase.assertEquals(PlanarYUVLuminanceSourceTestCase.Y, 0, source.getMatrix(), 0, PlanarYUVLuminanceSourceTestCase.Y.length);
        for (int r = 0; r < (PlanarYUVLuminanceSourceTestCase.ROWS); r++) {
            PlanarYUVLuminanceSourceTestCase.assertEquals(PlanarYUVLuminanceSourceTestCase.Y, (r * (PlanarYUVLuminanceSourceTestCase.COLS)), source.getRow(r, null), 0, PlanarYUVLuminanceSourceTestCase.COLS);
        }
    }

    @Test
    public void testCrop() {
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(PlanarYUVLuminanceSourceTestCase.YUV, PlanarYUVLuminanceSourceTestCase.COLS, PlanarYUVLuminanceSourceTestCase.ROWS, 1, 1, ((PlanarYUVLuminanceSourceTestCase.COLS) - 2), ((PlanarYUVLuminanceSourceTestCase.ROWS) - 2), false);
        Assert.assertTrue(source.isCropSupported());
        byte[] cropMatrix = source.getMatrix();
        for (int r = 0; r < ((PlanarYUVLuminanceSourceTestCase.ROWS) - 2); r++) {
            PlanarYUVLuminanceSourceTestCase.assertEquals(PlanarYUVLuminanceSourceTestCase.Y, (((r + 1) * (PlanarYUVLuminanceSourceTestCase.COLS)) + 1), cropMatrix, (r * ((PlanarYUVLuminanceSourceTestCase.COLS) - 2)), ((PlanarYUVLuminanceSourceTestCase.COLS) - 2));
        }
        for (int r = 0; r < ((PlanarYUVLuminanceSourceTestCase.ROWS) - 2); r++) {
            PlanarYUVLuminanceSourceTestCase.assertEquals(PlanarYUVLuminanceSourceTestCase.Y, (((r + 1) * (PlanarYUVLuminanceSourceTestCase.COLS)) + 1), source.getRow(r, null), 0, ((PlanarYUVLuminanceSourceTestCase.COLS) - 2));
        }
    }

    @Test
    public void testThumbnail() {
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(PlanarYUVLuminanceSourceTestCase.YUV, PlanarYUVLuminanceSourceTestCase.COLS, PlanarYUVLuminanceSourceTestCase.ROWS, 0, 0, PlanarYUVLuminanceSourceTestCase.COLS, PlanarYUVLuminanceSourceTestCase.ROWS, false);
        Assert.assertArrayEquals(new int[]{ -16777216, -16711423, -16579837, -16777216, -1, -131587 }, source.renderThumbnail());
    }
}

