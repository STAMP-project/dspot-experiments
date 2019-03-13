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
package com.google.zxing.oned;


import BarcodeFormat.CODE_128;
import com.google.zxing.Result;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitMatrixTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Code128Writer}.
 */
public class Code128WriterTestCase extends Assert {
    private static final String FNC1 = "11110101110";

    private static final String FNC2 = "11110101000";

    private static final String FNC3 = "10111100010";

    private static final String FNC4A = "11101011110";

    private static final String FNC4B = "10111101110";

    private static final String START_CODE_A = "11010000100";

    private static final String START_CODE_B = "11010010000";

    private static final String START_CODE_C = "11010011100";

    private static final String SWITCH_CODE_A = "11101011110";

    private static final String SWITCH_CODE_B = "10111101110";

    private static final String QUIET_SPACE = "00000";

    private static final String STOP = "1100011101011";

    private static final String LF = "10000110010";

    private Writer writer;

    private Code128Reader reader;

    @Test
    public void testEncodeWithFunc3() throws WriterException {
        String toEncode = "\u00f3" + "123";
        // "1"            "2"             "3"          check digit 51
        String expected = ((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_B)) + (Code128WriterTestCase.FNC3)) + "10011100110") + "11001110010") + "11001011100") + "11101000110") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE);
        BitMatrix result = writer.encode(toEncode, CODE_128, 0, 0);
        String actual = BitMatrixTestCase.matrixToString(result);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeWithFunc2() throws WriterException {
        String toEncode = "\u00f2" + "123";
        // "1"            "2"             "3"          check digit 56
        String expected = ((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_B)) + (Code128WriterTestCase.FNC2)) + "10011100110") + "11001110010") + "11001011100") + "11100010110") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE);
        BitMatrix result = writer.encode(toEncode, CODE_128, 0, 0);
        String actual = BitMatrixTestCase.matrixToString(result);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeWithFunc1() throws WriterException {
        String toEncode = "\u00f1" + "123";
        // "12"                           "3"          check digit 92
        String expected = ((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_C)) + (Code128WriterTestCase.FNC1)) + "10110011100") + (Code128WriterTestCase.SWITCH_CODE_B)) + "11001011100") + "10101111000") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE);
        BitMatrix result = writer.encode(toEncode, CODE_128, 0, 0);
        String actual = BitMatrixTestCase.matrixToString(result);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testRoundtrip() throws Exception {
        String toEncode = "\u00f1" + (("10958" + "\u00f1") + "17160526");
        String expected = "1095817160526";
        BitMatrix encResult = writer.encode(toEncode, CODE_128, 0, 0);
        BitArray row = encResult.getRow(0, null);
        Result rtResult = reader.decodeRow(0, row, null);
        String actual = rtResult.getText();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeWithFunc4() throws WriterException {
        String toEncode = "\u00f4" + "123";
        // "1"            "2"             "3"          check digit 59
        String expected = ((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_B)) + (Code128WriterTestCase.FNC4B)) + "10011100110") + "11001110010") + "11001011100") + "11100011010") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE);
        BitMatrix result = writer.encode(toEncode, CODE_128, 0, 0);
        String actual = BitMatrixTestCase.matrixToString(result);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeWithFncsAndNumberInCodesetA() throws Exception {
        String toEncode = "\n" + ((("\u00f1" + "\u00f4") + "1") + "\n");
        String expected = (((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_A)) + (Code128WriterTestCase.LF)) + (Code128WriterTestCase.FNC1)) + (Code128WriterTestCase.FNC4A)) + "10011100110") + (Code128WriterTestCase.LF)) + "10101111000") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE);
        BitMatrix result = writer.encode(toEncode, CODE_128, 0, 0);
        String actual = BitMatrixTestCase.matrixToString(result);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEncodeSwitchBetweenCodesetsAAndB() throws Exception {
        // start with A switch to B and back to A
        // "\0"            "A"             "B"             Switch to B     "a"             "b"             Switch to A     "\u0010"        check digit
        testEncode("\u0000ABab\u0010", (((((((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_A)) + "10100001100") + "10100011000") + "10001011000") + (Code128WriterTestCase.SWITCH_CODE_B)) + "10010110000") + "10010000110") + (Code128WriterTestCase.SWITCH_CODE_A)) + "10100111100") + "11001110100") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE)));
        // start with B switch to A and back to B
        // "a"             "b"             Switch to A     "\0             "Switch to B"   "a"             "b"             check digit
        testEncode("ab\u0000ab", ((((((((((((Code128WriterTestCase.QUIET_SPACE) + (Code128WriterTestCase.START_CODE_B)) + "10010110000") + "10010000110") + (Code128WriterTestCase.SWITCH_CODE_A)) + "10100001100") + (Code128WriterTestCase.SWITCH_CODE_B)) + "10010110000") + "10010000110") + "11010001110") + (Code128WriterTestCase.STOP)) + (Code128WriterTestCase.QUIET_SPACE)));
    }
}

