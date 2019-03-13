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
package com.google.zxing.aztec.decoder;


import com.google.zxing.FormatException;
import com.google.zxing.ResultPoint;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Decoder}.
 */
public final class DecoderTest extends Assert {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];

    @Test
    public void testAztecResult() throws FormatException {
        BitMatrix matrix = BitMatrix.parse(("X X X X X     X X X       X X X     X X X     \n" + ((((((((((((((((((((("X X X     X X X     X X X X     X X X     X X \n" + "  X   X X       X   X   X X X X     X     X X \n") + "  X   X X     X X     X     X   X       X   X \n") + "  X X   X X         X               X X     X \n") + "  X X   X X X X X X X X X X X X X X X     X   \n") + "  X X X X X                       X   X X X   \n") + "  X   X   X   X X X X X X X X X   X X X   X X \n") + "  X   X X X   X               X   X X       X \n") + "  X X   X X   X   X X X X X   X   X X X X   X \n") + "  X X   X X   X   X       X   X   X   X X X   \n") + "  X   X   X   X   X   X   X   X   X   X   X   \n") + "  X X X   X   X   X       X   X   X X   X X   \n") + "  X X X X X   X   X X X X X   X   X X X   X X \n") + "X X   X X X   X               X   X   X X   X \n") + "  X       X   X X X X X X X X X   X   X     X \n") + "  X X   X X                       X X   X X   \n") + "  X X X   X X X X X X X X X X X X X X   X X   \n") + "X     X     X     X X   X X               X X \n") + "X   X X X X X   X X X X X     X   X   X     X \n") + "X X X   X X X X           X X X       X     X \n") + "X X     X X X     X X X X     X X X     X X   \n") + "    X X X     X X X       X X X     X X X X   \n")), "X ", "  ");
        AztecDetectorResult r = new AztecDetectorResult(matrix, DecoderTest.NO_POINTS, false, 30, 2);
        DecoderResult result = new Decoder().decode(r);
        Assert.assertEquals("88888TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT", result.getText());
        Assert.assertArrayEquals(new byte[]{ -11, 85, 85, 117, 107, 90, -42, -75, -83, 107, 90, -42, -75, -83, 107, 90, -42, -75, -83, 107, 90, -42, -80 }, result.getRawBytes());
        Assert.assertEquals(180, result.getNumBits());
    }

    @Test(expected = FormatException.class)
    public void testDecodeTooManyErrors() throws FormatException {
        BitMatrix matrix = BitMatrix.parse(("" + (((((((((((((((((((((((((("X X . X . . . X X . . . X . . X X X . X . X X X X X . \n" + "X X . . X X . . . . . X X . . . X X . . . X . X . . X \n") + "X . . . X X . . X X X . X X . X X X X . X X . . X . . \n") + ". . . . X . X X . . X X . X X . X . X X X X . X . . X \n") + "X X X . . X X X X X . . . . . X X . . . X . X . X . X \n") + "X X . . . . . . . . X . . . X . X X X . X . . X . . . \n") + "X X . . X . . . . . X X . . . . . X . . . . X . . X X \n") + ". . . X . X . X . . . . . X X X X X X . . . . . . X X \n") + "X . . . X . X X X X X X . . X X X . X . X X X X X X . \n") + "X . . X X X . X X X X X X X X X X X X X . . . X . X X \n") + ". . . . X X . . . X . . . . . . . X X . . . X X . X . \n") + ". . . X X X . . X X . X X X X X . X . . X . . . . . . \n") + "X . . . . X . X . X . X . . . X . X . X X . X X . X X \n") + "X . X . . X . X . X . X . X . X . X . . . . . X . X X \n") + "X . X X X . . X . X . X . . . X . X . X X X . . . X X \n") + "X X X X X X X X . X . X X X X X . X . X . X . X X X . \n") + ". . . . . . . X . X . . . . . . . X X X X . . . X X X \n") + "X X . . X . . X . X X X X X X X X X X X X X . . X . X \n") + "X X X . X X X X . . X X X X . . X . . . . X . . X X X \n") + ". . . . X . X X X . . . . X X X X . . X X X X . . . . \n") + ". . X . . X . X . . . X . X X . X X . X . . . X . X . \n") + "X X . . X . . X X X X X X X . . X . X X X X X X X . . \n") + "X . X X . . X X . . . . . X . . . . . . X X . X X X . \n") + "X . . X X . . X X . X . X . . . . X . X . . X . . X . \n") + "X . X . X . . X . X X X X X X X X . X X X X . . X X . \n") + "X X X X . . . X . . X X X . X X . . X . . . . X X X . \n") + "X X . X . X . . . X . X . . . . X X . X . . X X . . . \n")), "X ", ". ");
        AztecDetectorResult r = new AztecDetectorResult(matrix, DecoderTest.NO_POINTS, true, 16, 4);
        new Decoder().decode(r);
    }

    @Test(expected = FormatException.class)
    public void testDecodeTooManyErrors2() throws FormatException {
        BitMatrix matrix = BitMatrix.parse(("" + ((((((((((((((((((((((((((". X X . . X . X X . . . X . . X X X . . . X X . X X . \n" + "X X . X X . . X . . . X X . . . X X . X X X . X . X X \n") + ". . . . X . . . X X X . X X . X X X X . X X . . X . . \n") + "X . X X . . X . . . X X . X X . X . X X . . . . . X . \n") + "X X . X . . X . X X . . . . . X X . . . . . X . . . X \n") + "X . . X . . . . . . X . . . X . X X X X X X X . . . X \n") + "X . . X X . . X . . X X . . . . . X . . . . . X X X . \n") + ". . X X X X . X . . . . . X X X X X X . . . . . . X X \n") + "X . . . X . X X X X X X . . X X X . X . X X X X X X . \n") + "X . . X X X . X X X X X X X X X X X X X . . . X . X X \n") + ". . . . X X . . . X . . . . . . . X X . . . X X . X . \n") + ". . . X X X . . X X . X X X X X . X . . X . . . . . . \n") + "X . . . . X . X . X . X . . . X . X . X X . X X . X X \n") + "X . X . . X . X . X . X . X . X . X . . . . . X . X X \n") + "X . X X X . . X . X . X . . . X . X . X X X . . . X X \n") + "X X X X X X X X . X . X X X X X . X . X . X . X X X . \n") + ". . . . . . . X . X . . . . . . . X X X X . . . X X X \n") + "X X . . X . . X . X X X X X X X X X X X X X . . X . X \n") + "X X X . X X X X . . X X X X . . X . . . . X . . X X X \n") + ". . X X X X X . X . . . . X X X X . . X X X . X . X . \n") + ". . X X . X . X . . . X . X X . X X . . . . X X . . . \n") + "X . . . X . X . X X X X X X . . X . X X X X X . X . . \n") + ". X . . . X X X . . . . . X . . . . . X X X X X . X . \n") + "X . . X . X X X X . X . X . . . . X . X X . X . . X . \n") + "X . . . X X . X . X X X X X X X X . X X X X . . X X . \n") + ". X X X X . . X . . X X X . X X . . X . . . . X X X . \n") + "X X . . . X X . . X . X . . . . X X . X . . X . X . X \n")), "X ", ". ");
        AztecDetectorResult r = new AztecDetectorResult(matrix, DecoderTest.NO_POINTS, true, 16, 4);
        new Decoder().decode(r);
    }

    @Test
    public void testRawBytes() {
        boolean[] bool0 = new boolean[]{  };
        boolean[] bool1 = new boolean[]{ true };
        boolean[] bool7 = new boolean[]{ true, false, true, false, true, false, true };
        boolean[] bool8 = new boolean[]{ true, false, true, false, true, false, true, false };
        boolean[] bool9 = new boolean[]{ true, false, true, false, true, false, true, false, true };
        boolean[] bool16 = new boolean[]{ false, true, true, false, false, false, true, true, true, true, false, false, false, false, false, true };
        byte[] byte0 = new byte[]{  };
        byte[] byte1 = new byte[]{ -128 };
        byte[] byte7 = new byte[]{ -86 };
        byte[] byte8 = new byte[]{ -86 };
        byte[] byte9 = new byte[]{ -86, -128 };
        byte[] byte16 = new byte[]{ 99, -63 };
        Assert.assertArrayEquals(byte0, Decoder.convertBoolArrayToByteArray(bool0));
        Assert.assertArrayEquals(byte1, Decoder.convertBoolArrayToByteArray(bool1));
        Assert.assertArrayEquals(byte7, Decoder.convertBoolArrayToByteArray(bool7));
        Assert.assertArrayEquals(byte8, Decoder.convertBoolArrayToByteArray(bool8));
        Assert.assertArrayEquals(byte9, Decoder.convertBoolArrayToByteArray(bool9));
        Assert.assertArrayEquals(byte16, Decoder.convertBoolArrayToByteArray(bool16));
    }
}

