/**
 * Copyright 2006 Jeremias Maerki.
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
package com.google.zxing.datamatrix.encoder;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link HighLevelEncoder}.
 */
public final class HighLevelEncodeTestCase extends Assert {
    private static final SymbolInfo[] TEST_SYMBOLS = // The last entries are fake entries to test special conditions with C40 encoding
    new SymbolInfo[]{ new SymbolInfo(false, 3, 5, 8, 8, 1), new SymbolInfo(false, 5, 7, 10, 10, 1), new SymbolInfo(true, 5, 7, 16, 6, 1), new SymbolInfo(false, 8, 10, 12, 12, 1), new SymbolInfo(true, 10, 11, 14, 6, 2), new SymbolInfo(false, 13, 0, 0, 0, 1), new SymbolInfo(false, 77, 0, 0, 0, 1) }// The last entries are fake entries to test special conditions with C40 encoding
    ;

    @Test
    public void testASCIIEncodation() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("123456");
        Assert.assertEquals("142 164 186", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("123456?");
        Assert.assertEquals("142 164 186 235 36", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("30Q324343430794<OQQ");
        Assert.assertEquals("160 82 162 173 173 173 137 224 61 80 82 82", visualized);
    }

    @Test
    public void testC40EncodationBasic1() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIM");
        Assert.assertEquals("230 91 11 91 11 91 11 254", visualized);
        // 230 shifts to C40 encodation, 254 unlatches, "else" case
    }

    @Test
    public void testC40EncodationBasic2() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIAB");
        Assert.assertEquals("230 91 11 90 255 254 67 129", visualized);
        // "B" is normally encoded as "15" (one C40 value)
        // "else" case: "B" is encoded as ASCII
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIAb");
        Assert.assertEquals("66 74 78 66 74 66 99 129", visualized);// Encoded as ASCII

        // Alternative solution:
        // assertEquals("230 91 11 90 255 254 99 129", visualized);
        // "b" is normally encoded as "Shift 3, 2" (two C40 values)
        // "else" case: "b" is encoded as ASCII
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIM?");
        Assert.assertEquals("230 91 11 91 11 91 11 254 235 76", visualized);
        // Alternative solution:
        // assertEquals("230 91 11 91 11 91 11 11 9 254", visualized);
        // Expl: 230 = shift to C40, "91 11" = "AIM",
        // "11 9" = "?" = "Shift 2, UpperShift, <char>
        // "else" case
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIM?");
        Assert.assertEquals("230 91 11 91 11 91 11 254 235 108", visualized);// Activate when additional rectangulars are available

        // Expl: 230 = shift to C40, "91 11" = "AIM",
        // "?" in C40 encodes to: 1 30 2 11 which doesn't fit into a triplet
        // "10 243" =
        // 254 = unlatch, 235 = Upper Shift, 108 = ? = 0xEB/235 - 128 + 1
        // "else" case
    }

    @Test
    public void testC40EncodationSpecExample() {
        // Example in Figure 1 in the spec
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("A1B2C3D4E5F6G7H8I9J0K1L2");
        Assert.assertEquals("230 88 88 40 8 107 147 59 67 126 206 78 126 144 121 35 47 254", visualized);
    }

    @Test
    public void testC40EncodationSpecialCases1() {
        // Special tests avoiding ultra-long test strings because these tests are only used
        // with the 16x48 symbol (47 data codewords)
        HighLevelEncodeTestCase.useTestSymbols();
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMAIMAIM");
        Assert.assertEquals("230 91 11 91 11 91 11 91 11 91 11 91 11", visualized);
        // case "a": Unlatch is not required
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMAIMAI");
        Assert.assertEquals("230 91 11 91 11 91 11 91 11 91 11 90 241", visualized);
        // case "b": Add trailing shift 0 and Unlatch is not required
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMAIMA");
        Assert.assertEquals("230 91 11 91 11 91 11 91 11 91 11 254 66", visualized);
        // case "c": Unlatch and write last character in ASCII
        HighLevelEncodeTestCase.resetSymbols();
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMAIMAI");
        Assert.assertEquals("230 91 11 91 11 91 11 91 11 91 11 254 66 74 129 237", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMA");
        Assert.assertEquals("230 91 11 91 11 91 11 66", visualized);
        // case "d": Skip Unlatch and write last character in ASCII
    }

    @Test
    public void testC40EncodationSpecialCases2() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMAIMAIMAI");
        Assert.assertEquals("230 91 11 91 11 91 11 91 11 91 11 91 11 254 66 74", visualized);
        // available > 2, rest = 2 --> unlatch and encode as ASCII
    }

    @Test
    public void testTextEncodation() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaim");
        Assert.assertEquals("239 91 11 91 11 91 11 254", visualized);
        // 239 shifts to Text encodation, 254 unlatches
        visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaim'");
        Assert.assertEquals("239 91 11 91 11 91 11 254 40 129", visualized);
        // assertEquals("239 91 11 91 11 91 11 7 49 254", visualized);
        // This is an alternative, but doesn't strictly follow the rules in the spec.
        visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaIm");
        Assert.assertEquals("239 91 11 91 11 87 218 110", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaimB");
        Assert.assertEquals("239 91 11 91 11 91 11 254 67 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaim{txt}\u0004");
        Assert.assertEquals("239 91 11 91 11 91 11 16 218 236 107 181 69 254 129 237", visualized);
    }

    @Test
    public void testX12Encodation() {
        // 238 shifts to X12 encodation, 254 unlatches
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("ABC>ABC123>AB");
        Assert.assertEquals("238 89 233 14 192 100 207 44 31 67", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("ABC>ABC123>ABC");
        Assert.assertEquals("238 89 233 14 192 100 207 44 31 254 67 68", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("ABC>ABC123>ABCD");
        Assert.assertEquals("238 89 233 14 192 100 207 44 31 96 82 254", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("ABC>ABC123>ABCDE");
        Assert.assertEquals("238 89 233 14 192 100 207 44 31 96 82 70", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("ABC>ABC123>ABCDEF");
        Assert.assertEquals("238 89 233 14 192 100 207 44 31 96 82 254 70 71 129 237", visualized);
    }

    @Test
    public void testEDIFACTEncodation() {
        // 240 shifts to EDIFACT encodation
        String visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.DATA.123DATA.123DATA");
        Assert.assertEquals("240 184 27 131 198 236 238 16 21 1 187 28 179 16 21 1 187 28 179 16 21 1", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X.X2..");
        Assert.assertEquals("240 184 27 131 198 236 238 98 230 50 47 47", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X.X2.");
        Assert.assertEquals("240 184 27 131 198 236 238 98 230 50 47 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X.X2");
        Assert.assertEquals("240 184 27 131 198 236 238 98 230 50", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X.X");
        Assert.assertEquals("240 184 27 131 198 236 238 98 230 31", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X.");
        Assert.assertEquals("240 184 27 131 198 236 238 98 231 192", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".A.C1.3.X");
        Assert.assertEquals("240 184 27 131 198 236 238 89", visualized);
        // Checking temporary unlatch from EDIFACT
        visualized = HighLevelEncodeTestCase.encodeHighLevel(".XXX.XXX.XXX.XXX.XXX.XXX.?XX.XXX.XXX.XXX.XXX.XXX.XXX");
        Assert.assertEquals(("240 185 134 24 185 134 24 185 134 24 185 134 24 185 134 24 185 134 24" + (" 124 47 235 125 240"// <-- this is the temporary unlatch
         + " 97 139 152 97 139 152 97 139 152 97 139 152 97 139 152 97 139 152 89 89")), visualized);
    }

    @Test
    public void testBase256Encodation() {
        // 231 shifts to Base256 encodation
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("\u00ab\u00e4\u00f6\u00fc\u00e9\u00bb");
        Assert.assertEquals("231 44 108 59 226 126 1 104", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("\u00ab\u00e4\u00f6\u00fc\u00e9\u00e0\u00bb");
        Assert.assertEquals("231 51 108 59 226 126 1 141 254 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("\u00ab\u00e4\u00f6\u00fc\u00e9\u00e0\u00e1\u00bb");
        Assert.assertEquals("231 44 108 59 226 126 1 141 36 147", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(" 23?");// ASCII only (for reference)

        Assert.assertEquals("33 153 235 36 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("\u00ab\u00e4\u00f6\u00fc\u00e9\u00bb 234");// Mixed Base256 + ASCII

        Assert.assertEquals("231 51 108 59 226 126 1 104 99 153 53 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("\u00ab\u00e4\u00f6\u00fc\u00e9\u00bb 23\u00a3 1234567890123456789");
        Assert.assertEquals(("231 55 108 59 226 126 1 104 99 10 161 167 185 142 164 186 208" + " 220 142 164 186 208 58 129 59 209 104 254 150 45"), visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(HighLevelEncodeTestCase.createBinaryMessage(20));
        Assert.assertEquals("231 44 108 59 226 126 1 141 36 5 37 187 80 230 123 17 166 60 210 103 253 150", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(HighLevelEncodeTestCase.createBinaryMessage(19));// padding necessary at the end

        Assert.assertEquals("231 63 108 59 226 126 1 141 36 5 37 187 80 230 123 17 166 60 210 103 1 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(HighLevelEncodeTestCase.createBinaryMessage(276));
        HighLevelEncodeTestCase.assertStartsWith("231 38 219 2 208 120 20 150 35", visualized);
        HighLevelEncodeTestCase.assertEndsWith("146 40 194 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel(HighLevelEncodeTestCase.createBinaryMessage(277));
        HighLevelEncodeTestCase.assertStartsWith("231 38 220 2 208 120 20 150 35", visualized);
        HighLevelEncodeTestCase.assertEndsWith("146 40 190 87", visualized);
    }

    @Test
    public void testUnlatchingFromC40() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("AIMAIMAIMAIMaimaimaim");
        Assert.assertEquals("230 91 11 91 11 91 11 254 66 74 78 239 91 11 91 11 91 11", visualized);
    }

    @Test
    public void testUnlatchingFromText() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("aimaimaimaim12345678");
        Assert.assertEquals("239 91 11 91 11 91 11 91 11 254 142 164 186 208 129 237", visualized);
    }

    @Test
    public void testHelloWorld() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("Hello World!");
        Assert.assertEquals("73 239 116 130 175 123 148 64 158 233 254 34", visualized);
    }

    @Test
    public void testBug1664266() {
        // There was an exception and the encoder did not handle the unlatching from
        // EDIFACT encoding correctly
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("CREX-TAN:h");
        Assert.assertEquals("240 13 33 88 181 64 78 124 59 105", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("CREX-TAN:hh");
        Assert.assertEquals("240 13 33 88 181 64 78 124 59 105 105 129", visualized);
        visualized = HighLevelEncodeTestCase.encodeHighLevel("CREX-TAN:hhh");
        Assert.assertEquals("240 13 33 88 181 64 78 124 59 105 105 105", visualized);
    }

    @Test
    public void testX12Unlatch() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("*DTCP01");
        Assert.assertEquals("238 9 10 104 141 254 50 129", visualized);
    }

    @Test
    public void testX12Unlatch2() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("*DTCP0");
        Assert.assertEquals("238 9 10 104 141", visualized);
    }

    @Test
    public void testBug3048549() {
        // There was an IllegalArgumentException for an illegal character here because
        // of an encoding problem of the character 0x0060 in Java source code.
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("fiykmj*Rh2`,e6");
        Assert.assertEquals("239 122 87 154 40 7 171 115 207 12 130 71 155 254 129 237", visualized);
    }

    @Test
    public void testMacroCharacters() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("[)>\u001e05\u001d5555\u001c6666\u001e\u0004");
        // assertEquals("92 42 63 31 135 30 185 185 29 196 196 31 5 129 87 237", visualized);
        Assert.assertEquals("236 185 185 29 196 196 129 56", visualized);
    }

    @Test
    public void testEncodingWithStartAsX12AndLatchToEDIFACTInTheMiddle() {
        String visualized = HighLevelEncodeTestCase.encodeHighLevel("*MEMANT-1F-MESTECH");
        Assert.assertEquals("238 10 99 164 204 254 240 82 220 70 180 209 83 80 80 200", visualized);
    }
}

