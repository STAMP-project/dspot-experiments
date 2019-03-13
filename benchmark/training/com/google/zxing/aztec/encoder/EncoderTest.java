/**
 * Copyright 2013 ZXing authors
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
package com.google.zxing.aztec.encoder;


import BarcodeFormat.AZTEC;
import Encoder.DEFAULT_AZTEC_LAYERS;
import Encoder.DEFAULT_EC_PERCENT;
import com.google.zxing.ResultPoint;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


/**
 * Aztec 2D generator unit tests.
 *
 * @author Rustam Abdullaev
 * @author Frank Yellin
 */
public final class EncoderTest extends Assert {
    private static final Pattern DOTX = Pattern.compile("[^.X]");

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];

    // real life tests
    @Test
    public void testEncode1() {
        EncoderTest.testEncode("This is an example Aztec symbol for Wikipedia.", true, 3, ("X     X X       X     X X     X     X         \n" + ((((((((((((((((((((("X         X     X X     X   X X   X X       X \n" + "X X   X X X X X   X X X                 X     \n") + "X X                 X X   X       X X X X X X \n") + "    X X X   X   X     X X X X         X X     \n") + "  X X X   X X X X   X     X   X     X X   X   \n") + "        X X X X X     X X X X   X   X     X   \n") + "X       X   X X X X X X X X X X X     X   X X \n") + "X   X     X X X               X X X X   X X   \n") + "X     X X   X X   X X X X X   X X   X   X X X \n") + "X   X         X   X       X   X X X X       X \n") + "X       X     X   X   X   X   X   X X   X     \n") + "      X   X X X   X       X   X     X X X     \n") + "    X X X X X X   X X X X X   X X X X X X   X \n") + "  X X   X   X X               X X X   X X X X \n") + "  X   X       X X X X X X X X X X X X   X X   \n") + "  X X   X       X X X   X X X       X X       \n") + "  X               X   X X     X     X X X     \n") + "  X   X X X   X X   X   X X X X   X   X X X X \n") + "    X   X   X X X   X   X   X X X X     X     \n") + "        X               X                 X   \n") + "        X X     X   X X   X   X   X       X X \n") + "  X   X   X X       X   X         X X X     X \n")));
    }

    @Test
    public void testEncode2() {
        EncoderTest.testEncode(("Aztec Code is a public domain 2D matrix barcode symbology" + (" of nominally square symbols built on a square grid with a " + "distinctive square bullseye pattern at their center.")), false, 6, ("        X X     X X     X     X     X   X X X         X   X         X   X X       \n" + ((((((((((((((((((((((((((((((((((((((("  X       X X     X   X X   X X       X             X     X   X X   X           X \n" + "  X   X X X     X   X   X X     X X X   X   X X               X X       X X     X \n") + "X X X             X   X         X         X     X     X   X     X X       X   X   \n") + "X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X \n") + "    X X   X   X   X X X               X       X       X X     X X   X X       X   \n") + "X X     X       X       X X X X   X   X X       X   X X   X       X X   X X   X   \n") + "  X       X   X     X X   X   X X   X X   X X X X X X   X X           X   X   X X \n") + "X X   X X   X   X X X X   X X X X X X X X   X   X       X X   X X X X   X X X     \n") + "  X       X   X     X       X X     X X   X   X   X     X X   X X X   X     X X X \n") + "  X   X X X   X X       X X X         X X           X   X   X   X X X   X X     X \n") + "    X     X   X X     X X X X     X   X     X X X X   X X   X X   X X X     X   X \n") + "X X X   X             X         X X X X X   X   X X   X   X   X X   X   X   X   X \n") + "          X       X X X   X X     X   X           X   X X X X   X X               \n") + "  X     X X   X   X       X X X X X X X X X X X X X X X   X   X X   X   X X X     \n") + "    X X                 X   X                       X X   X       X         X X X \n") + "        X   X X   X X X X X X   X X X X X X X X X   X     X X           X X X X   \n") + "          X X X   X     X   X   X               X   X X     X X X   X X           \n") + "X X     X     X   X   X   X X   X   X X X X X   X   X X X X X X X       X   X X X \n") + "X X X X       X       X   X X   X   X       X   X   X     X X X     X X       X X \n") + "X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X \n") + "    X     X       X         X   X   X       X   X   X     X   X X                 \n") + "        X X     X X X X X   X   X   X X X X X   X   X X X     X X X X   X         \n") + "X     X   X   X         X   X   X               X   X X   X X   X X X     X   X   \n") + "  X   X X X   X   X X   X X X   X X X X X X X X X   X X         X X     X X X X   \n") + "    X X   X   X   X X X     X                       X X X   X X   X   X     X     \n") + "    X X X X   X         X   X X X X X X X X X X X X X X   X       X X   X X   X X \n") + "            X   X   X X       X X X X X     X X X       X       X X X         X   \n") + "X       X         X   X X X X   X     X X     X X     X X           X   X       X \n") + "X     X       X X X X X     X   X X X X   X X X     X       X X X X   X   X X   X \n") + "  X X X X X               X     X X X   X       X X   X X   X X X X     X X       \n") + "X             X         X   X X   X X     X     X     X   X   X X X X             \n") + "    X   X X       X     X       X   X X X X X X   X X   X X X X X X X X X   X   X \n") + "    X         X X   X       X     X   X   X       X     X X X     X       X X X X \n") + "X     X X     X X X X X X             X X X   X               X   X     X     X X \n") + "X   X X     X               X X X X X     X X     X X X X X X X X     X   X   X X \n") + "X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X   X \n") + "X           X     X X X X     X     X         X         X   X       X X   X X X   \n") + "X   X   X X   X X X   X         X X     X X X X     X X   X   X     X   X       X \n") + "      X     X     X     X X     X   X X   X X   X         X X       X       X   X \n") + "X       X           X   X   X     X X   X               X     X     X X X         \n")));
    }

    @Test
    public void testAztecWriter() throws Exception {
        for (int i = 0; i < 1000; i++) {
            EncoderTest.testWriter("\u20ac 1 sample data.", "ISO-8859-1", 25, true, 2);
            EncoderTest.testWriter("\u20ac 1 sample data.", "ISO-8859-15", 25, true, 2);
            EncoderTest.testWriter("\u20ac 1 sample data.", "UTF-8", 25, true, 2);
            EncoderTest.testWriter("\u20ac 1 sample data.", "UTF-8", 100, true, 3);
            EncoderTest.testWriter("\u20ac 1 sample data.", "UTF-8", 300, true, 4);
            EncoderTest.testWriter("\u20ac 1 sample data.", "UTF-8", 500, false, 5);
            // Test AztecWriter defaults
            String data = "In ut magna vel mauris malesuada";
            AztecWriter writer = new AztecWriter();
            BitMatrix matrix = writer.encode(data, AZTEC, 0, 0);
            AztecCode aztec = Encoder.encode(data.getBytes(StandardCharsets.ISO_8859_1), DEFAULT_EC_PERCENT, DEFAULT_AZTEC_LAYERS);
            BitMatrix expectedMatrix = aztec.getMatrix();
            Assert.assertEquals(matrix, expectedMatrix);
        }
    }

    // synthetic tests (encode-decode round-trip)
    @Test
    public void testEncodeDecode1() throws Exception {
        EncoderTest.testEncodeDecode("Abc123!", true, 1);
    }

    @Test
    public void testEncodeDecode2() throws Exception {
        EncoderTest.testEncodeDecode("Lorem ipsum. http://test/", true, 2);
    }

    @Test
    public void testEncodeDecode3() throws Exception {
        EncoderTest.testEncodeDecode("AAAANAAAANAAAANAAAANAAAANAAAANAAAANAAAANAAAANAAAAN", true, 3);
    }

    @Test
    public void testEncodeDecode4() throws Exception {
        EncoderTest.testEncodeDecode("http://test/~!@#*^%&)__ ;:\'\"[]{}\\|-+-=`1029384", true, 4);
    }

    @Test
    public void testEncodeDecode5() throws Exception {
        EncoderTest.testEncodeDecode(("http://test/~!@#*^%&)__ ;:\'\"[]{}\\|-+-=`1029384756<>/?abc" + "Four score and seven our forefathers brought forth"), false, 5);
    }

    @Test
    public void testEncodeDecode10() throws Exception {
        EncoderTest.testEncodeDecode(("In ut magna vel mauris malesuada dictum. Nulla ullamcorper metus quis diam" + ((((" cursus facilisis. Sed mollis quam id justo rutrum sagittis. Donec laoreet rutrum" + " est, nec convallis mauris condimentum sit amet. Phasellus gravida, justo et congue") + " auctor, nisi ipsum viverra erat, eget hendrerit felis turpis nec lorem. Nulla") + " ultrices, elit pellentesque aliquet laoreet, justo erat pulvinar nisi, id") + " elementum sapien dolor et diam.")), false, 10);
    }

    @Test
    public void testEncodeDecode23() throws Exception {
        EncoderTest.testEncodeDecode(("In ut magna vel mauris malesuada dictum. Nulla ullamcorper metus quis diam" + ((((((((((((((((((((" cursus facilisis. Sed mollis quam id justo rutrum sagittis. Donec laoreet rutrum" + " est, nec convallis mauris condimentum sit amet. Phasellus gravida, justo et congue") + " auctor, nisi ipsum viverra erat, eget hendrerit felis turpis nec lorem. Nulla") + " ultrices, elit pellentesque aliquet laoreet, justo erat pulvinar nisi, id") + " elementum sapien dolor et diam. Donec ac nunc sodales elit placerat eleifend.") + " Sed ornare luctus ornare. Vestibulum vehicula, massa at pharetra fringilla, risus") + " justo faucibus erat, nec porttitor nibh tellus sed est. Ut justo diam, lobortis eu") + " tristique ac, p.In ut magna vel mauris malesuada dictum. Nulla ullamcorper metus") + " quis diam cursus facilisis. Sed mollis quam id justo rutrum sagittis. Donec") + " laoreet rutrum est, nec convallis mauris condimentum sit amet. Phasellus gravida,") + " justo et congue auctor, nisi ipsum viverra erat, eget hendrerit felis turpis nec") + " lorem. Nulla ultrices, elit pellentesque aliquet laoreet, justo erat pulvinar") + " nisi, id elementum sapien dolor et diam. Donec ac nunc sodales elit placerat") + " eleifend. Sed ornare luctus ornare. Vestibulum vehicula, massa at pharetra") + " fringilla, risus justo faucibus erat, nec porttitor nibh tellus sed est. Ut justo") + " diam, lobortis eu tristique ac, p. In ut magna vel mauris malesuada dictum. Nulla") + " ullamcorper metus quis diam cursus facilisis. Sed mollis quam id justo rutrum") + " sagittis. Donec laoreet rutrum est, nec convallis mauris condimentum sit amet.") + " Phasellus gravida, justo et congue auctor, nisi ipsum viverra erat, eget hendrerit") + " felis turpis nec lorem. Nulla ultrices, elit pellentesque aliquet laoreet, justo") + " erat pulvinar nisi, id elementum sapien dolor et diam.")), false, 23);
    }

    @Test
    public void testEncodeDecode31() throws Exception {
        EncoderTest.testEncodeDecode(("In ut magna vel mauris malesuada dictum. Nulla ullamcorper metus quis diam" + (((((((((((((((((((((((((((((((((((" cursus facilisis. Sed mollis quam id justo rutrum sagittis. Donec laoreet rutrum" + " est, nec convallis mauris condimentum sit amet. Phasellus gravida, justo et congue") + " auctor, nisi ipsum viverra erat, eget hendrerit felis turpis nec lorem. Nulla") + " ultrices, elit pellentesque aliquet laoreet, justo erat pulvinar nisi, id") + " elementum sapien dolor et diam. Donec ac nunc sodales elit placerat eleifend.") + " Sed ornare luctus ornare. Vestibulum vehicula, massa at pharetra fringilla, risus") + " justo faucibus erat, nec porttitor nibh tellus sed est. Ut justo diam, lobortis eu") + " tristique ac, p.In ut magna vel mauris malesuada dictum. Nulla ullamcorper metus") + " quis diam cursus facilisis. Sed mollis quam id justo rutrum sagittis. Donec") + " laoreet rutrum est, nec convallis mauris condimentum sit amet. Phasellus gravida,") + " justo et congue auctor, nisi ipsum viverra erat, eget hendrerit felis turpis nec") + " lorem. Nulla ultrices, elit pellentesque aliquet laoreet, justo erat pulvinar") + " nisi, id elementum sapien dolor et diam. Donec ac nunc sodales elit placerat") + " eleifend. Sed ornare luctus ornare. Vestibulum vehicula, massa at pharetra") + " fringilla, risus justo faucibus erat, nec porttitor nibh tellus sed est. Ut justo") + " diam, lobortis eu tristique ac, p. In ut magna vel mauris malesuada dictum. Nulla") + " ullamcorper metus quis diam cursus facilisis. Sed mollis quam id justo rutrum") + " sagittis. Donec laoreet rutrum est, nec convallis mauris condimentum sit amet.") + " Phasellus gravida, justo et congue auctor, nisi ipsum viverra erat, eget hendrerit") + " felis turpis nec lorem. Nulla ultrices, elit pellentesque aliquet laoreet, justo") + " erat pulvinar nisi, id elementum sapien dolor et diam. Donec ac nunc sodales elit") + " placerat eleifend. Sed ornare luctus ornare. Vestibulum vehicula, massa at") + " pharetra fringilla, risus justo faucibus erat, nec porttitor nibh tellus sed est.") + " Ut justo diam, lobortis eu tristique ac, p.In ut magna vel mauris malesuada") + " dictum. Nulla ullamcorper metus quis diam cursus facilisis. Sed mollis quam id") + " justo rutrum sagittis. Donec laoreet rutrum est, nec convallis mauris condimentum") + " sit amet. Phasellus gravida, justo et congue auctor, nisi ipsum viverra erat,") + " eget hendrerit felis turpis nec lorem. Nulla ultrices, elit pellentesque aliquet") + " laoreet, justo erat pulvinar nisi, id elementum sapien dolor et diam. Donec ac") + " nunc sodales elit placerat eleifend. Sed ornare luctus ornare. Vestibulum vehicula,") + " massa at pharetra fringilla, risus justo faucibus erat, nec porttitor nibh tellus") + " sed est. Ut justo diam, lobortis eu tris. In ut magna vel mauris malesuada dictum.") + " Nulla ullamcorper metus quis diam cursus facilisis. Sed mollis quam id justo rutrum") + " sagittis. Donec laoreet rutrum est, nec convallis mauris condimentum sit amet.") + " Phasellus gravida, justo et congue auctor, nisi ipsum viverra erat, eget") + " hendrerit felis turpis nec lorem.")), false, 31);
    }

    @Test
    public void testGenerateModeMessage() {
        EncoderTest.testModeMessage(true, 2, 29, ".X .XXX.. ...X XX.. ..X .XX. .XX.X");
        EncoderTest.testModeMessage(true, 4, 64, "XX XXXXXX .X.. ...X ..XX .X.. XX..");
        EncoderTest.testModeMessage(false, 21, 660, "X.X.. .X.X..X..XX .XXX ..X.. .XXX. .X... ..XXX");
        EncoderTest.testModeMessage(false, 32, 4096, "XXXXX XXXXXXXXXXX X.X. ..... XXX.X ..X.. X.XXX");
    }

    @Test
    public void testStuffBits() {
        EncoderTest.testStuffBits(5, ".X.X. X.X.X .X.X.", ".X.X. X.X.X .X.X.");
        EncoderTest.testStuffBits(5, ".X.X. ..... .X.X", ".X.X. ....X ..X.X");
        EncoderTest.testStuffBits(3, "XX. ... ... ..X XXX .X. ..", "XX. ..X ..X ..X ..X .XX XX. .X. ..X");
        EncoderTest.testStuffBits(6, ".X.X.. ...... ..X.XX", ".X.X.. .....X. ..X.XX XXXX.");
        EncoderTest.testStuffBits(6, ".X.X.. ...... ...... ..X.X.", ".X.X.. .....X .....X ....X. X.XXXX");
        EncoderTest.testStuffBits(6, ".X.X.. XXXXXX ...... ..X.XX", ".X.X.. XXXXX. X..... ...X.X XXXXX.");
        EncoderTest.testStuffBits(6, "...... ..XXXX X..XX. .X.... .X.X.X .....X .X.... ...X.X .....X ....XX ..X... ....X. X..XXX X.XX.X", ".....X ...XXX XX..XX ..X... ..X.X. X..... X.X... ....X. X..... X....X X..X.. .....X X.X..X XXX.XX .XXXXX");
    }

    @Test
    public void testHighLevelEncode() {
        // 'A'  P/S   '. ' L/L    b    D/L    '.'
        EncoderTest.testHighLevelEncodeString("A. b.", "...X. ..... ...XX XXX.. ...XX XXXX. XX.X");
        // 'L'  L/L   'o'   'r'   'e'   'm'   ' '   'i'   'p'   's'   'u'   'm'   D/L   '.'
        EncoderTest.testHighLevelEncodeString("Lorem ipsum.", ".XX.X XXX.. X.... X..XX ..XX. .XXX. ....X .X.X. X...X X.X.. X.XX. .XXX. XXXX. XX.X");
        // 'L'  L/L   'o'   P/S   '. '  U/S   'T'   'e'   's'   't'    D/L   ' '  '1'  '2'  '3'  '.'
        EncoderTest.testHighLevelEncodeString("Lo. Test 123.", ".XX.X XXX.. X.... ..... ...XX XXX.. X.X.X ..XX. X.X.. X.X.X  XXXX. ...X ..XX .X.. .X.X XX.X");
        // 'L'  L/L   'o'   D/L   '.'  '.'  '.'  U/L  L/L   'x'
        EncoderTest.testHighLevelEncodeString("Lo...x", ".XX.X XXX.. X.... XXXX. XX.X XX.X XX.X XXX. XXX.. XX..X");
        // P/S   '. '  L/L   'x'   P/S   ':'   P/S   '/'   P/S   '/'   'a'   'b'   'c'   P/S   '/'   D/L   '.'
        EncoderTest.testHighLevelEncodeString(". x://abc/.", "..... ...XX XXX.. XX..X ..... X.X.X ..... X.X.. ..... X.X.. ...X. ...XX ..X.. ..... X.X.. XXXX. XX.X");
        // Uses Binary/Shift rather than Lower/Shift to save two bits.
        // 'A'   'B'   'C'   B/S    =1    'd'     'E'   'F'   'G'
        EncoderTest.testHighLevelEncodeString("ABCdEFG", "...X. ...XX ..X.. XXXXX ....X .XX..X.. ..XX. ..XXX .X...");
        // Found on an airline boarding pass.  Several stretches of Binary shift are
        // necessary to keep the bitcount so low.
        EncoderTest.testHighLevelEncodeString(("09  UAG    ^160MEUCIQC0sYS/HpKxnBELR1uB85R20OoqqwFGa0q2uEi" + "Ygh6utAIgLl1aBVM4EOTQtMQQYH9M2Z3Dp4qnA/fwWuQ+M8L3V8U="), 823);
    }

    @Test
    public void testHighLevelEncodeBinary() {
        // binary short form single byte
        // 'N'  B/S    =1   '\0'      N
        EncoderTest.testHighLevelEncodeString("N\u0000N", ".XXXX XXXXX ....X ........ .XXXX");// Encode "N" in UPPER

        // 'N'  B/S    =2   '\0'       'n'
        EncoderTest.testHighLevelEncodeString("N\u0000n", ".XXXX XXXXX ...X. ........ .XX.XXX.");// Encode "n" in BINARY

        // binary short form consecutive bytes
        // 'N'  B/S    =2    '\0'    \u0080   ' '  'A'
        EncoderTest.testHighLevelEncodeString("N\u0000\u0080 A", ".XXXX XXXXX ...X. ........ X....... ....X ...X.");
        // binary skipping over single character
        // B/S  =4    '\0'      'a'     '\3ff'   '\200'   ' '   'A'
        EncoderTest.testHighLevelEncodeString("\u0000a\u00ff\u0080 A", "XXXXX ..X.. ........ .XX....X XXXXXXXX X....... ....X ...X.");
        // getting into binary mode from digit mode
        // D/L   '1'  '2'  '3'  '4'  U/L  B/S    =1    \0
        EncoderTest.testHighLevelEncodeString("1234\u0000", "XXXX. ..XX .X.. .X.X .XX. XXX. XXXXX ....X ........");
        // Create a string in which every character requires binary
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 3000; i++) {
            sb.append(((char) (128 + (i % 30))));
        }
        // Test the output generated by Binary/Switch, particularly near the
        // places where the encoding changes: 31, 62, and 2047+31=2078
        for (int i : new int[]{ 1, 2, 3, 10, 29, 30, 31, 32, 33, 60, 61, 62, 63, 64, 2076, 2077, 2078, 2079, 2080, 2100 }) {
            // This is the expected length of a binary string of length "i"
            int expectedLength = (8 * i) + (i <= 31 ? 10 : i <= 62 ? 20 : i <= 2078 ? 21 : 31);
            // Verify that we are correct about the length.
            EncoderTest.testHighLevelEncodeString(sb.substring(0, i), expectedLength);
            if (((i != 1) && (i != 32)) && (i != 2079)) {
                // The addition of an 'a' at the beginning or end gets merged into the binary code
                // in those cases where adding another binary character only adds 8 or 9 bits to the result.
                // So we exclude the border cases i=1,32,2079
                // A lower case letter at the beginning will be merged into binary mode
                EncoderTest.testHighLevelEncodeString(('a' + (sb.substring(0, (i - 1)))), expectedLength);
                // A lower case letter at the end will also be merged into binary mode
                EncoderTest.testHighLevelEncodeString(((sb.substring(0, (i - 1))) + 'a'), expectedLength);
            }
            // A lower case letter at both ends will enough to latch us into LOWER.
            EncoderTest.testHighLevelEncodeString((('a' + (sb.substring(0, i))) + 'b'), (expectedLength + 15));
        }
        sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append('?');// ? forces binary encoding

        }
        sb.setCharAt(1, 'A');
        // expect B/S(1) A B/S(30)
        EncoderTest.testHighLevelEncodeString(sb.toString(), ((5 + 20) + (31 * 8)));
        sb = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            sb.append('?');
        }
        sb.setCharAt(1, 'A');
        // expect B/S(31)
        EncoderTest.testHighLevelEncodeString(sb.toString(), (10 + (31 * 8)));
        sb = new StringBuilder();
        for (int i = 0; i < 34; i++) {
            sb.append('?');
        }
        sb.setCharAt(1, 'A');
        // expect B/S(31) B/S(3)
        EncoderTest.testHighLevelEncodeString(sb.toString(), (20 + (34 * 8)));
        sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            sb.append('?');
        }
        sb.setCharAt(30, 'A');
        // expect B/S(64)
        EncoderTest.testHighLevelEncodeString(sb.toString(), (21 + (64 * 8)));
    }

    @Test
    public void testHighLevelEncodePairs() {
        // Typical usage
        // A     B    C    P/S   .<sp>   D    E     F    P/S   \r\n
        EncoderTest.testHighLevelEncodeString("ABC. DEF\r\n", "...X. ...XX ..X.. ..... ...XX ..X.X ..XX. ..XXX ..... ...X.");
        // We should latch to PUNCT mode, rather than shift.  Also check all pairs
        // 'A'    M/L   P/L   ". "  ": "   ", " "\r\n"
        EncoderTest.testHighLevelEncodeString("A. : , \r\n", "...X. XXX.X XXXX. ...XX ..X.X  ..X.. ...X.");
        // Latch to DIGIT rather than shift to PUNCT
        // 'A'  D/L   '.'  ' '  '1' '2'   '3'  '4'
        EncoderTest.testHighLevelEncodeString("A. 1234", "...X. XXXX. XX.X ...X ..XX .X.. .X.X .X X.");
        // Don't bother leaving Binary Shift.
        // 'A'  B/S    =2    \200      "."     " "     \200
        EncoderTest.testHighLevelEncodeString("A\u0080. \u0080", "...X. XXXXX ..X.. X....... ..X.XXX. ..X..... X.......");
    }

    @Test
    public void testUserSpecifiedLayers() {
        byte[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.ISO_8859_1);
        AztecCode aztec = Encoder.encode(alphabet, 25, (-2));
        Assert.assertEquals(2, aztec.getLayers());
        Assert.assertTrue(aztec.isCompact());
        aztec = Encoder.encode(alphabet, 25, 32);
        Assert.assertEquals(32, aztec.getLayers());
        Assert.assertFalse(aztec.isCompact());
        try {
            Encoder.encode(alphabet, 25, 33);
            Assert.fail("Encode should have failed.  No such thing as 33 layers");
        } catch (IllegalArgumentException expected) {
            // continue
        }
        try {
            Encoder.encode(alphabet, 25, (-1));
            Assert.fail("Encode should have failed.  Text can't fit in 1-layer compact");
        } catch (IllegalArgumentException expected) {
            // continue
        }
    }

    @Test
    public void testBorderCompact4Case() {
        // Compact(4) con hold 608 bits of information, but at most 504 can be data.  Rest must
        // be error correction
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // encodes as 26 * 5 * 4 = 520 bits of data
        String alphabet4 = ((alphabet + alphabet) + alphabet) + alphabet;
        byte[] data = alphabet4.getBytes(StandardCharsets.ISO_8859_1);
        try {
            Encoder.encode(data, 0, (-4));
            Assert.fail("Encode should have failed.  Text can't fit in 1-layer compact");
        } catch (IllegalArgumentException expected) {
            // continue
        }
        // If we just try to encode it normally, it will go to a non-compact 4 layer
        AztecCode aztecCode = Encoder.encode(data, 0, DEFAULT_AZTEC_LAYERS);
        Assert.assertFalse(aztecCode.isCompact());
        Assert.assertEquals(4, aztecCode.getLayers());
        // But shortening the string to 100 bytes (500 bits of data), compact works fine, even if we
        // include more error checking.
        aztecCode = Encoder.encode(alphabet4.substring(0, 100).getBytes(StandardCharsets.ISO_8859_1), 10, DEFAULT_AZTEC_LAYERS);
        Assert.assertTrue(aztecCode.isCompact());
        Assert.assertEquals(4, aztecCode.getLayers());
    }
}

