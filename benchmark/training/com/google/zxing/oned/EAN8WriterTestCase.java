/**
 * Copyright 2009 ZXing authors
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
package com.google.zxing.oned;


import BarcodeFormat.EAN_8;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitMatrixTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ari Pollak
 */
public final class EAN8WriterTestCase extends Assert {
    @Test
    public void testEncode() throws WriterException {
        String testStr = "0000001010001011010111101111010110111010101001110111001010001001011100101000000";
        BitMatrix result = new EAN8Writer().encode("96385074", EAN_8, testStr.length(), 0);
        Assert.assertEquals(testStr, BitMatrixTestCase.matrixToString(result));
    }

    @Test
    public void testAddChecksumAndEncode() throws WriterException {
        String testStr = "0000001010001011010111101111010110111010101001110111001010001001011100101000000";
        BitMatrix result = new EAN8Writer().encode("9638507", EAN_8, testStr.length(), 0);
        Assert.assertEquals(testStr, BitMatrixTestCase.matrixToString(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncodeIllegalCharacters() throws WriterException {
        new EAN8Writer().encode("96385abc", EAN_8, 0, 0);
    }
}

