/**
 * Copyright 2008 ZXing authors
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
package com.google.zxing.qrcode.encoder;


import ErrorCorrectionLevel.H;
import ErrorCorrectionLevel.M;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.qrcode.decoder.Version;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author mysen@google.com (Chris Mysen) - ported from C++
 */
public final class MatrixUtilTestCase extends Assert {
    @Test
    public void testToString() {
        ByteMatrix array = new ByteMatrix(3, 3);
        array.set(0, 0, 0);
        array.set(1, 0, 1);
        array.set(2, 0, 0);
        array.set(0, 1, 1);
        array.set(1, 1, 0);
        array.set(2, 1, 1);
        array.set(0, 2, (-1));
        array.set(1, 2, (-1));
        array.set(2, 2, (-1));
        String expected = " 0 1 0\n" + (" 1 0 1\n" + "      \n");
        Assert.assertEquals(expected, array.toString());
    }

    @Test
    public void testClearMatrix() {
        ByteMatrix matrix = new ByteMatrix(2, 2);
        MatrixUtil.clearMatrix(matrix);
        Assert.assertEquals((-1), matrix.get(0, 0));
        Assert.assertEquals((-1), matrix.get(1, 0));
        Assert.assertEquals((-1), matrix.get(0, 1));
        Assert.assertEquals((-1), matrix.get(1, 1));
    }

    @Test
    public void testEmbedBasicPatterns1() throws WriterException {
        // Version 1.
        ByteMatrix matrix = new ByteMatrix(21, 21);
        MatrixUtil.clearMatrix(matrix);
        MatrixUtil.embedBasicPatterns(Version.getVersionForNumber(1), matrix);
        String expected = " 1 1 1 1 1 1 1 0           0 1 1 1 1 1 1 1\n" + (((((((((((((((((((" 1 0 0 0 0 0 1 0           0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0           0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0           0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0           0 1 0 1 1 1 0 1\n") + " 1 0 0 0 0 0 1 0           0 1 0 0 0 0 0 1\n") + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n") + " 0 0 0 0 0 0 0 0           0 0 0 0 0 0 0 0\n") + "             1                            \n") + "             0                            \n") + "             1                            \n") + "             0                            \n") + "             1                            \n") + " 0 0 0 0 0 0 0 0 1                        \n") + " 1 1 1 1 1 1 1 0                          \n") + " 1 0 0 0 0 0 1 0                          \n") + " 1 0 1 1 1 0 1 0                          \n") + " 1 0 1 1 1 0 1 0                          \n") + " 1 0 1 1 1 0 1 0                          \n") + " 1 0 0 0 0 0 1 0                          \n") + " 1 1 1 1 1 1 1 0                          \n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testEmbedBasicPatterns2() throws WriterException {
        // Version 2.  Position adjustment pattern should apppear at right
        // bottom corner.
        ByteMatrix matrix = new ByteMatrix(25, 25);
        MatrixUtil.clearMatrix(matrix);
        MatrixUtil.embedBasicPatterns(Version.getVersionForNumber(2), matrix);
        String expected = " 1 1 1 1 1 1 1 0                   0 1 1 1 1 1 1 1\n" + (((((((((((((((((((((((" 1 0 0 0 0 0 1 0                   0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0                   0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0                   0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0                   0 1 0 1 1 1 0 1\n") + " 1 0 0 0 0 0 1 0                   0 1 0 0 0 0 0 1\n") + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n") + " 0 0 0 0 0 0 0 0                   0 0 0 0 0 0 0 0\n") + "             1                                    \n") + "             0                                    \n") + "             1                                    \n") + "             0                                    \n") + "             1                                    \n") + "             0                                    \n") + "             1                                    \n") + "             0                                    \n") + "             1                   1 1 1 1 1        \n") + " 0 0 0 0 0 0 0 0 1               1 0 0 0 1        \n") + " 1 1 1 1 1 1 1 0                 1 0 1 0 1        \n") + " 1 0 0 0 0 0 1 0                 1 0 0 0 1        \n") + " 1 0 1 1 1 0 1 0                 1 1 1 1 1        \n") + " 1 0 1 1 1 0 1 0                                  \n") + " 1 0 1 1 1 0 1 0                                  \n") + " 1 0 0 0 0 0 1 0                                  \n") + " 1 1 1 1 1 1 1 0                                  \n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testEmbedTypeInfo() throws WriterException {
        // Type info bits = 100000011001110.
        ByteMatrix matrix = new ByteMatrix(21, 21);
        MatrixUtil.clearMatrix(matrix);
        MatrixUtil.embedTypeInfo(M, 5, matrix);
        String expected = "                 0                        \n" + ((((((((((((((((((("                 1                        \n" + "                 1                        \n") + "                 1                        \n") + "                 0                        \n") + "                 0                        \n") + "                                          \n") + "                 1                        \n") + " 1 0 0 0 0 0   0 1         1 1 0 0 1 1 1 0\n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                 0                        \n") + "                 0                        \n") + "                 0                        \n") + "                 0                        \n") + "                 0                        \n") + "                 0                        \n") + "                 1                        \n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testEmbedVersionInfo() throws WriterException {
        // Version info bits = 000111 110010 010100
        // Actually, version 7 QR Code has 45x45 matrix but we use 21x21 here
        // since 45x45 matrix is too big to depict.
        ByteMatrix matrix = new ByteMatrix(21, 21);
        MatrixUtil.clearMatrix(matrix);
        MatrixUtil.maybeEmbedVersionInfo(Version.getVersionForNumber(7), matrix);
        String expected = "                     0 0 1                \n" + ((((((((((((((((((("                     0 1 0                \n" + "                     0 1 0                \n") + "                     0 1 1                \n") + "                     1 1 1                \n") + "                     0 0 0                \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + " 0 0 0 0 1 0                              \n") + " 0 1 1 1 1 0                              \n") + " 1 0 0 1 1 0                              \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n") + "                                          \n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testEmbedDataBits() throws WriterException {
        // Cells other than basic patterns should be filled with zero.
        ByteMatrix matrix = new ByteMatrix(21, 21);
        MatrixUtil.clearMatrix(matrix);
        MatrixUtil.embedBasicPatterns(Version.getVersionForNumber(1), matrix);
        BitArray bits = new BitArray();
        MatrixUtil.embedDataBits(bits, (-1), matrix);
        String expected = " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1 1 1 1 1 1 1\n" + (((((((((((((((((((" 1 0 0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 1 0 1 1 1 0 1\n") + " 1 0 0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 1\n") + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n") + " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 0 1 1 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n") + " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testBuildMatrix() throws WriterException {
        // From http://www.swetake.com/qr/qr7.html
        char[] bytes = new char[]{ 32, 65, 205, 69, 41, 220, 46, 128, 236, 42, 159, 74, 221, 244, 169, 239, 150, 138, 70, 237, 85, 224, 96, 74, 219, 61 };
        BitArray bits = new BitArray();
        for (char c : bytes) {
            bits.appendBits(c, 8);
        }
        ByteMatrix matrix = new ByteMatrix(21, 21);
        // Version 1
        // Mask pattern 3
        MatrixUtil.buildMatrix(bits, H, Version.getVersionForNumber(1), 3, matrix);
        String expected = " 1 1 1 1 1 1 1 0 0 1 1 0 0 0 1 1 1 1 1 1 1\n" + (((((((((((((((((((" 1 0 0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 0 0 1 0 0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0 0 1 1 0 0 0 1 0 1 1 1 0 1\n") + " 1 0 1 1 1 0 1 0 1 1 0 0 1 0 1 0 1 1 1 0 1\n") + " 1 0 0 0 0 0 1 0 0 0 1 1 1 0 1 0 0 0 0 0 1\n") + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n") + " 0 0 0 0 0 0 0 0 1 1 0 1 1 0 0 0 0 0 0 0 0\n") + " 0 0 1 1 0 0 1 1 1 0 0 1 1 1 1 0 1 0 0 0 0\n") + " 1 0 1 0 1 0 0 0 0 0 1 1 1 0 0 1 0 1 1 1 0\n") + " 1 1 1 1 0 1 1 0 1 0 1 1 1 0 0 1 1 1 0 1 0\n") + " 1 0 1 0 1 1 0 1 1 1 0 0 1 1 1 0 0 1 0 1 0\n") + " 0 0 1 0 0 1 1 1 0 0 0 0 0 0 1 0 1 1 1 1 1\n") + " 0 0 0 0 0 0 0 0 1 1 0 1 0 0 0 0 0 1 0 1 1\n") + " 1 1 1 1 1 1 1 0 1 1 1 1 0 0 0 0 1 0 1 1 0\n") + " 1 0 0 0 0 0 1 0 0 0 0 1 0 1 1 1 0 0 0 0 0\n") + " 1 0 1 1 1 0 1 0 0 1 0 0 1 1 0 0 1 0 0 1 1\n") + " 1 0 1 1 1 0 1 0 1 1 0 1 0 0 0 0 0 1 1 1 0\n") + " 1 0 1 1 1 0 1 0 1 1 1 1 0 0 0 0 1 1 1 0 0\n") + " 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1 0 1 0 0\n") + " 1 1 1 1 1 1 1 0 0 0 1 1 1 1 1 0 1 0 0 1 0\n");
        Assert.assertEquals(expected, matrix.toString());
    }

    @Test
    public void testFindMSBSet() {
        Assert.assertEquals(0, MatrixUtil.findMSBSet(0));
        Assert.assertEquals(1, MatrixUtil.findMSBSet(1));
        Assert.assertEquals(8, MatrixUtil.findMSBSet(128));
        Assert.assertEquals(32, MatrixUtil.findMSBSet(-2147483648));
    }

    @Test
    public void testCalculateBCHCode() {
        // Encoding of type information.
        // From Appendix C in JISX0510:2004 (p 65)
        Assert.assertEquals(220, MatrixUtil.calculateBCHCode(5, 1335));
        // From http://www.swetake.com/qr/qr6.html
        Assert.assertEquals(450, MatrixUtil.calculateBCHCode(19, 1335));
        // From http://www.swetake.com/qr/qr11.html
        Assert.assertEquals(532, MatrixUtil.calculateBCHCode(27, 1335));
        // Encoding of version information.
        // From Appendix D in JISX0510:2004 (p 68)
        Assert.assertEquals(3220, MatrixUtil.calculateBCHCode(7, 7973));
        Assert.assertEquals(1468, MatrixUtil.calculateBCHCode(8, 7973));
        Assert.assertEquals(2713, MatrixUtil.calculateBCHCode(9, 7973));
        Assert.assertEquals(1235, MatrixUtil.calculateBCHCode(10, 7973));
        Assert.assertEquals(2470, MatrixUtil.calculateBCHCode(20, 7973));
        Assert.assertEquals(3445, MatrixUtil.calculateBCHCode(30, 7973));
        Assert.assertEquals(3177, MatrixUtil.calculateBCHCode(40, 7973));
    }

    // We don't test a lot of cases in this function since we've already
    // tested them in TEST(calculateBCHCode).
    @Test
    public void testMakeVersionInfoBits() throws WriterException {
        // From Appendix D in JISX0510:2004 (p 68)
        BitArray bits = new BitArray();
        MatrixUtil.makeVersionInfoBits(Version.getVersionForNumber(7), bits);
        Assert.assertEquals(" ...XXXXX ..X..X.X ..", bits.toString());
    }

    // We don't test a lot of cases in this function since we've already
    // tested them in TEST(calculateBCHCode).
    @Test
    public void testMakeTypeInfoInfoBits() throws WriterException {
        // From Appendix C in JISX0510:2004 (p 65)
        BitArray bits = new BitArray();
        MatrixUtil.makeTypeInfoBits(M, 5, bits);
        Assert.assertEquals(" X......X X..XXX.", bits.toString());
    }
}

