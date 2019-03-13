/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.common;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class BitMatrixTestCase extends Assert {
    private static final int[] BIT_MATRIX_POINTS = new int[]{ 1, 2, 2, 0, 3, 1 };

    @Test
    public void testGetSet() {
        BitMatrix matrix = new BitMatrix(33);
        Assert.assertEquals(33, matrix.getHeight());
        for (int y = 0; y < 33; y++) {
            for (int x = 0; x < 33; x++) {
                if (((y * x) % 3) == 0) {
                    matrix.set(x, y);
                }
            }
        }
        for (int y = 0; y < 33; y++) {
            for (int x = 0; x < 33; x++) {
                Assert.assertEquals((((y * x) % 3) == 0), matrix.get(x, y));
            }
        }
    }

    @Test
    public void testSetRegion() {
        BitMatrix matrix = new BitMatrix(5);
        matrix.setRegion(1, 1, 3, 3);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Assert.assertEquals(((((y >= 1) && (y <= 3)) && (x >= 1)) && (x <= 3)), matrix.get(x, y));
            }
        }
    }

    @Test
    public void testEnclosing() {
        BitMatrix matrix = new BitMatrix(5);
        Assert.assertNull(matrix.getEnclosingRectangle());
        matrix.setRegion(1, 1, 1, 1);
        Assert.assertArrayEquals(new int[]{ 1, 1, 1, 1 }, matrix.getEnclosingRectangle());
        matrix.setRegion(1, 1, 3, 2);
        Assert.assertArrayEquals(new int[]{ 1, 1, 3, 2 }, matrix.getEnclosingRectangle());
        matrix.setRegion(0, 0, 5, 5);
        Assert.assertArrayEquals(new int[]{ 0, 0, 5, 5 }, matrix.getEnclosingRectangle());
    }

    @Test
    public void testOnBit() {
        BitMatrix matrix = new BitMatrix(5);
        Assert.assertNull(matrix.getTopLeftOnBit());
        Assert.assertNull(matrix.getBottomRightOnBit());
        matrix.setRegion(1, 1, 1, 1);
        Assert.assertArrayEquals(new int[]{ 1, 1 }, matrix.getTopLeftOnBit());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, matrix.getBottomRightOnBit());
        matrix.setRegion(1, 1, 3, 2);
        Assert.assertArrayEquals(new int[]{ 1, 1 }, matrix.getTopLeftOnBit());
        Assert.assertArrayEquals(new int[]{ 3, 2 }, matrix.getBottomRightOnBit());
        matrix.setRegion(0, 0, 5, 5);
        Assert.assertArrayEquals(new int[]{ 0, 0 }, matrix.getTopLeftOnBit());
        Assert.assertArrayEquals(new int[]{ 4, 4 }, matrix.getBottomRightOnBit());
    }

    @Test
    public void testRectangularMatrix() {
        BitMatrix matrix = new BitMatrix(75, 20);
        Assert.assertEquals(75, matrix.getWidth());
        Assert.assertEquals(20, matrix.getHeight());
        matrix.set(10, 0);
        matrix.set(11, 1);
        matrix.set(50, 2);
        matrix.set(51, 3);
        matrix.flip(74, 4);
        matrix.flip(0, 5);
        // Should all be on
        Assert.assertTrue(matrix.get(10, 0));
        Assert.assertTrue(matrix.get(11, 1));
        Assert.assertTrue(matrix.get(50, 2));
        Assert.assertTrue(matrix.get(51, 3));
        Assert.assertTrue(matrix.get(74, 4));
        Assert.assertTrue(matrix.get(0, 5));
        // Flip a couple back off
        matrix.flip(50, 2);
        matrix.flip(51, 3);
        Assert.assertFalse(matrix.get(50, 2));
        Assert.assertFalse(matrix.get(51, 3));
    }

    @Test
    public void testRectangularSetRegion() {
        BitMatrix matrix = new BitMatrix(320, 240);
        Assert.assertEquals(320, matrix.getWidth());
        Assert.assertEquals(240, matrix.getHeight());
        matrix.setRegion(105, 22, 80, 12);
        // Only bits in the region should be on
        for (int y = 0; y < 240; y++) {
            for (int x = 0; x < 320; x++) {
                Assert.assertEquals(((((y >= 22) && (y < 34)) && (x >= 105)) && (x < 185)), matrix.get(x, y));
            }
        }
    }

    @Test
    public void testGetRow() {
        BitMatrix matrix = new BitMatrix(102, 5);
        for (int x = 0; x < 102; x++) {
            if ((x & 3) == 0) {
                matrix.set(x, 2);
            }
        }
        // Should allocate
        BitArray array = matrix.getRow(2, null);
        Assert.assertEquals(102, array.getSize());
        // Should reallocate
        BitArray array2 = new BitArray(60);
        array2 = matrix.getRow(2, array2);
        Assert.assertEquals(102, array2.getSize());
        // Should use provided object, with original BitArray size
        BitArray array3 = new BitArray(200);
        array3 = matrix.getRow(2, array3);
        Assert.assertEquals(200, array3.getSize());
        for (int x = 0; x < 102; x++) {
            boolean on = (x & 3) == 0;
            Assert.assertEquals(on, array.get(x));
            Assert.assertEquals(on, array2.get(x));
            Assert.assertEquals(on, array3.get(x));
        }
    }

    @Test
    public void testRotate180Simple() {
        BitMatrix matrix = new BitMatrix(3, 3);
        matrix.set(0, 0);
        matrix.set(0, 1);
        matrix.set(1, 2);
        matrix.set(2, 1);
        matrix.rotate180();
        Assert.assertTrue(matrix.get(2, 2));
        Assert.assertTrue(matrix.get(2, 1));
        Assert.assertTrue(matrix.get(1, 0));
        Assert.assertTrue(matrix.get(0, 1));
    }

    @Test
    public void testRotate180() {
        BitMatrixTestCase.testRotate180(7, 4);
        BitMatrixTestCase.testRotate180(7, 5);
        BitMatrixTestCase.testRotate180(8, 4);
        BitMatrixTestCase.testRotate180(8, 5);
    }

    @Test
    public void testParse() {
        BitMatrix emptyMatrix = new BitMatrix(3, 3);
        BitMatrix fullMatrix = new BitMatrix(3, 3);
        fullMatrix.setRegion(0, 0, 3, 3);
        BitMatrix centerMatrix = new BitMatrix(3, 3);
        centerMatrix.setRegion(1, 1, 1, 1);
        BitMatrix emptyMatrix24 = new BitMatrix(2, 4);
        Assert.assertEquals(emptyMatrix, BitMatrix.parse("   \n   \n   \n", "x", " "));
        Assert.assertEquals(emptyMatrix, BitMatrix.parse("   \n   \r\r\n   \n\r", "x", " "));
        Assert.assertEquals(emptyMatrix, BitMatrix.parse("   \n   \n   ", "x", " "));
        Assert.assertEquals(fullMatrix, BitMatrix.parse("xxx\nxxx\nxxx\n", "x", " "));
        Assert.assertEquals(centerMatrix, BitMatrix.parse("   \n x \n   \n", "x", " "));
        Assert.assertEquals(centerMatrix, BitMatrix.parse("      \n  x   \n      \n", "x ", "  "));
        try {
            Assert.assertEquals(centerMatrix, BitMatrix.parse("   \n xy\n   \n", "x", " "));
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // good
        }
        Assert.assertEquals(emptyMatrix24, BitMatrix.parse("  \n  \n  \n  \n", "x", " "));
        Assert.assertEquals(centerMatrix, BitMatrix.parse(centerMatrix.toString("x", "."), "x", "."));
    }

    @Test
    public void testUnset() {
        BitMatrix emptyMatrix = new BitMatrix(3, 3);
        BitMatrix matrix = emptyMatrix.clone();
        matrix.set(1, 1);
        Assert.assertNotEquals(emptyMatrix, matrix);
        matrix.unset(1, 1);
        Assert.assertEquals(emptyMatrix, matrix);
        matrix.unset(1, 1);
        Assert.assertEquals(emptyMatrix, matrix);
    }

    @Test
    public void testXOR() {
        BitMatrix emptyMatrix = new BitMatrix(3, 3);
        BitMatrix fullMatrix = new BitMatrix(3, 3);
        fullMatrix.setRegion(0, 0, 3, 3);
        BitMatrix centerMatrix = new BitMatrix(3, 3);
        centerMatrix.setRegion(1, 1, 1, 1);
        BitMatrix invertedCenterMatrix = fullMatrix.clone();
        invertedCenterMatrix.unset(1, 1);
        BitMatrix badMatrix = new BitMatrix(4, 4);
        BitMatrixTestCase.testXOR(emptyMatrix, emptyMatrix, emptyMatrix);
        BitMatrixTestCase.testXOR(emptyMatrix, centerMatrix, centerMatrix);
        BitMatrixTestCase.testXOR(emptyMatrix, fullMatrix, fullMatrix);
        BitMatrixTestCase.testXOR(centerMatrix, emptyMatrix, centerMatrix);
        BitMatrixTestCase.testXOR(centerMatrix, centerMatrix, emptyMatrix);
        BitMatrixTestCase.testXOR(centerMatrix, fullMatrix, invertedCenterMatrix);
        BitMatrixTestCase.testXOR(invertedCenterMatrix, emptyMatrix, invertedCenterMatrix);
        BitMatrixTestCase.testXOR(invertedCenterMatrix, centerMatrix, fullMatrix);
        BitMatrixTestCase.testXOR(invertedCenterMatrix, fullMatrix, centerMatrix);
        BitMatrixTestCase.testXOR(fullMatrix, emptyMatrix, fullMatrix);
        BitMatrixTestCase.testXOR(fullMatrix, centerMatrix, invertedCenterMatrix);
        BitMatrixTestCase.testXOR(fullMatrix, fullMatrix, emptyMatrix);
        try {
            emptyMatrix.clone().xor(badMatrix);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // good
        }
        try {
            badMatrix.clone().xor(emptyMatrix);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // good
        }
    }
}

