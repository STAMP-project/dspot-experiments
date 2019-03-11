/**
 * Copyright (C) 2016 The Android Open Source Project
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
package libcore.java.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import junit.framework.TestCase;


public class Base64Test extends TestCase {
    /**
     * The base 64 alphabet from RFC 4648 Table 1.
     */
    private static final Set<Character> TABLE_1 = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/')));

    /**
     * The "URL and Filename safe" Base 64 Alphabet from RFC 4648 Table 2.
     */
    private static final Set<Character> TABLE_2 = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_')));

    public void testAlphabet_plain() {
        Base64Test.checkAlphabet(Base64Test.TABLE_1, "", Base64.getEncoder());
    }

    public void testAlphabet_mime() {
        Base64Test.checkAlphabet(Base64Test.TABLE_1, "\r\n", Base64.getMimeEncoder());
    }

    public void testAlphabet_url() {
        Base64Test.checkAlphabet(Base64Test.TABLE_2, "", Base64.getUrlEncoder());
    }

    /**
     * Checks decoding of bytes containing a value outside of the allowed
     * {@link #TABLE_1 "basic" alphabet}.
     */
    public void testDecoder_extraChars_basic() throws Exception {
        Base64.Decoder basicDecoder = Base64.getDecoder();// uses Table 1

        // Check failure cases common to both RFC4648 Table 1 and Table 2 decoding.
        Base64Test.checkDecoder_extraChars_common(basicDecoder);
        // Tests characters that are part of RFC4848 Table 2 but not Table 1.
        Base64Test.assertDecodeThrowsIAe(basicDecoder, "_aGVsbG8sIHdvcmx");
        Base64Test.assertDecodeThrowsIAe(basicDecoder, "aGV_sbG8sIHdvcmx");
        Base64Test.assertDecodeThrowsIAe(basicDecoder, "aGVsbG8sIHdvcmx_");
    }

    /**
     * Checks decoding of bytes containing a value outside of the allowed
     * {@link #TABLE_2 url alphabet}.
     */
    public void testDecoder_extraChars_url() throws Exception {
        Base64.Decoder urlDecoder = Base64.getUrlDecoder();// uses Table 2

        // Check failure cases common to both RFC4648 table 1 and table 2 decoding.
        Base64Test.checkDecoder_extraChars_common(urlDecoder);
        // Tests characters that are part of RFC4848 Table 1 but not Table 2.
        Base64Test.assertDecodeThrowsIAe(urlDecoder, "/aGVsbG8sIHdvcmx");
        Base64Test.assertDecodeThrowsIAe(urlDecoder, "aGV/sbG8sIHdvcmx");
        Base64Test.assertDecodeThrowsIAe(urlDecoder, "aGVsbG8sIHdvcmx/");
    }

    public void testDecoder_extraChars_mime() throws Exception {
        Base64.Decoder mimeDecoder = Base64.getMimeDecoder();
        // Characters outside alphabet before padding.
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, " aGVsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGV sbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk "));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "_aGVsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGV_sbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk_"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "*aGVsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGV*sbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk*"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "\r\naGVsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGV\r\nsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk\r\n"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "\naGVsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGV\nsbG8sIHdvcmxk"));
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk\n"));
        // padding 0
        TestCase.assertEquals("hello, world", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxk"));
        // Extra padding
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxk=");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxk==");
        // Characters outside alphabet intermixed with (too much) padding.
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxk =");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxk = = ");
        // padding 1
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE="));
        // Missing padding
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE"));
        // Characters outside alphabet before padding.
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE ="));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE*="));
        // Trailing characters, otherwise valid.
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE= "));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=*"));
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=X");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=XY");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=XYZ");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=XYZA");
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=\n"));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=\r\n"));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE= "));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE=="));
        // Characters outside alphabet intermixed with (too much) padding.
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE =="));
        TestCase.assertEquals("hello, world?!", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkPyE = = "));
        // padding 2
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg=="));
        // Missing padding
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg"));
        // Partially missing padding
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg=");
        // Characters outside alphabet before padding.
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg =="));
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg*=="));
        // Trailing characters, otherwise valid.
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg== "));
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg==*"));
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg==X");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg==XY");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg==XYZ");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg==XYZA");
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg==\n"));
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg==\r\n"));
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg== "));
        TestCase.assertEquals("hello, world.", Base64Test.decodeToAscii(mimeDecoder, "aGVsbG8sIHdvcmxkLg==="));
        // Characters outside alphabet inside padding are not allowed by the MIME decoder.
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg= =");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg=*=");
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg=\r\n=");
        // Characters inside alphabet inside padding.
        Base64Test.assertDecodeThrowsIAe(mimeDecoder, "aGVsbG8sIHdvcmxkLg=X=");
    }

    public void testDecoder_nonPrintableBytes_basic() throws Exception {
        Base64Test.checkDecoder_nonPrintableBytes_table1(Base64.getDecoder());
    }

    public void testDecoder_nonPrintableBytes_mime() throws Exception {
        Base64Test.checkDecoder_nonPrintableBytes_table1(Base64.getMimeDecoder());
    }

    /**
     * Check decoding sample non-ASCII byte[] values from a {@link #TABLE_2}
     * (url safe) encoded String.
     */
    public void testDecoder_nonPrintableBytes_url() throws Exception {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, decoder.decode(""));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 1, decoder.decode("_w=="));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 2, decoder.decode("_-4="));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 3, decoder.decode("_-7d"));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 4, decoder.decode("_-7dzA=="));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 5, decoder.decode("_-7dzLs="));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 6, decoder.decode("_-7dzLuq"));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 7, decoder.decode("_-7dzLuqmQ=="));
        Base64Test.assertArrayPrefixEquals(Base64Test.SAMPLE_NON_ASCII_BYTES, 8, decoder.decode("_-7dzLuqmYg="));
    }

    private static final byte[] SAMPLE_NON_ASCII_BYTES = new byte[]{ ((byte) (255)), ((byte) (238)), ((byte) (221)), ((byte) (204)), ((byte) (187)), ((byte) (170)), ((byte) (153)), ((byte) (136)), ((byte) (119)) };

    public void testDecoder_closedStream() {
        try {
            Base64Test.closedDecodeStream().available();
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
        try {
            Base64Test.closedDecodeStream().read();
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
        try {
            Base64Test.closedDecodeStream().read(new byte[23]);
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
        try {
            Base64Test.closedDecodeStream().read(new byte[23], 0, 1);
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
    }

    /**
     * Tests {@link Decoder#decode(byte[], byte[])} for correctness as well as
     * for consistency with other methods tested elsewhere.
     */
    public void testDecoder_decodeArrayToArray() {
        Base64.Decoder decoder = Base64.getDecoder();
        // Empty input
        TestCase.assertEquals(0, decoder.decode(new byte[0], new byte[0]));
        // Test data for non-empty input
        String inputString = "YWJjZWZnaGk=";
        byte[] input = inputString.getBytes(StandardCharsets.US_ASCII);
        String expectedString = "abcefghi";
        byte[] decodedBytes = expectedString.getBytes(StandardCharsets.US_ASCII);
        // check test data consistency with other methods that are tested elsewhere
        Base64Test.assertRoundTrip(Base64.getEncoder(), decoder, inputString, decodedBytes);
        // Non-empty input: output array too short
        byte[] tooShort = new byte[(decodedBytes.length) - 1];
        try {
            decoder.decode(input, tooShort);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // Non-empty input: output array longer than required
        byte[] tooLong = new byte[(decodedBytes.length) + 1];
        int tooLongBytesDecoded = decoder.decode(input, tooLong);
        TestCase.assertEquals(decodedBytes.length, tooLongBytesDecoded);
        TestCase.assertEquals(0, tooLong[((tooLong.length) - 1)]);
        Base64Test.assertArrayPrefixEquals(tooLong, decodedBytes.length, decodedBytes);
        // Non-empty input: output array has exact minimum required size
        byte[] justRight = new byte[decodedBytes.length];
        int justRightBytesDecoded = decoder.decode(input, justRight);
        TestCase.assertEquals(decodedBytes.length, justRightBytesDecoded);
        Base64Test.assertArrayEquals(decodedBytes, justRight);
    }

    public void testDecoder_decodeByteBuffer() {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] emptyByteArray = new byte[0];
        ByteBuffer emptyByteBuffer = ByteBuffer.wrap(emptyByteArray);
        ByteBuffer emptyDecodedBuffer = decoder.decode(emptyByteBuffer);
        TestCase.assertEquals(emptyByteBuffer, emptyDecodedBuffer);
        TestCase.assertNotSame(emptyByteArray, emptyDecodedBuffer);
        // Test the two types of byte buffer.
        String inputString = "YWJjZWZnaGk=";
        byte[] input = inputString.getBytes(StandardCharsets.US_ASCII);
        String expectedString = "abcefghi";
        byte[] expectedBytes = expectedString.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer inputBuffer = ByteBuffer.allocate(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.checkDecoder_decodeByteBuffer(decoder, inputBuffer, expectedBytes);
        inputBuffer = ByteBuffer.allocateDirect(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.checkDecoder_decodeByteBuffer(decoder, inputBuffer, expectedBytes);
    }

    public void testDecoder_decodeByteBuffer_invalidData() {
        Base64.Decoder decoder = Base64.getDecoder();
        // Test the two types of byte buffer.
        String inputString = "AAAA AAAA";
        byte[] input = inputString.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer inputBuffer = ByteBuffer.allocate(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.checkDecoder_decodeByteBuffer_invalidData(decoder, inputBuffer);
        inputBuffer = ByteBuffer.allocateDirect(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.checkDecoder_decodeByteBuffer_invalidData(decoder, inputBuffer);
    }

    public void testDecoder_nullArgs() {
        Base64Test.checkDecoder_nullArgs(Base64.getDecoder());
        Base64Test.checkDecoder_nullArgs(Base64.getMimeDecoder());
        Base64Test.checkDecoder_nullArgs(Base64.getUrlDecoder());
    }

    public void testEncoder_nullArgs() {
        Base64Test.checkEncoder_nullArgs(Base64.getEncoder());
        Base64Test.checkEncoder_nullArgs(Base64.getMimeEncoder());
        Base64Test.checkEncoder_nullArgs(Base64.getUrlEncoder());
        Base64Test.checkEncoder_nullArgs(Base64.getMimeEncoder(20, new byte[]{ '*' }));
        Base64Test.checkEncoder_nullArgs(Base64.getEncoder().withoutPadding());
        Base64Test.checkEncoder_nullArgs(Base64.getMimeEncoder().withoutPadding());
        Base64Test.checkEncoder_nullArgs(Base64.getUrlEncoder().withoutPadding());
        Base64Test.checkEncoder_nullArgs(Base64.getMimeEncoder(20, new byte[]{ '*' }).withoutPadding());
    }

    public void testEncoder_nonPrintableBytes() throws Exception {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        TestCase.assertEquals("", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 0)));
        TestCase.assertEquals("_w==", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 1)));
        TestCase.assertEquals("_-4=", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 2)));
        TestCase.assertEquals("_-7d", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 3)));
        TestCase.assertEquals("_-7dzA==", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 4)));
        TestCase.assertEquals("_-7dzLs=", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 5)));
        TestCase.assertEquals("_-7dzLuq", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 6)));
        TestCase.assertEquals("_-7dzLuqmQ==", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 7)));
        TestCase.assertEquals("_-7dzLuqmYg=", encoder.encodeToString(Arrays.copyOfRange(Base64Test.SAMPLE_NON_ASCII_BYTES, 0, 8)));
    }

    public void testEncoder_lineLength() throws Exception {
        String in_56 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcd";
        String in_57 = in_56 + "e";
        String in_58 = in_56 + "ef";
        String in_59 = in_56 + "efg";
        String in_60 = in_56 + "efgh";
        String in_61 = in_56 + "efghi";
        String prefix = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFi";
        String out_56 = prefix + "Y2Q=";
        String out_57 = prefix + "Y2Rl";
        String out_58 = prefix + "Y2Rl\r\nZg==";
        String out_59 = prefix + "Y2Rl\r\nZmc=";
        String out_60 = prefix + "Y2Rl\r\nZmdo";
        String out_61 = prefix + "Y2Rl\r\nZmdoaQ==";
        Base64.Encoder encoder = Base64.getMimeEncoder();
        Base64.Decoder decoder = Base64.getMimeDecoder();
        TestCase.assertEquals("", Base64Test.encodeFromAscii(encoder, decoder, ""));
        TestCase.assertEquals(out_56, Base64Test.encodeFromAscii(encoder, decoder, in_56));
        TestCase.assertEquals(out_57, Base64Test.encodeFromAscii(encoder, decoder, in_57));
        TestCase.assertEquals(out_58, Base64Test.encodeFromAscii(encoder, decoder, in_58));
        TestCase.assertEquals(out_59, Base64Test.encodeFromAscii(encoder, decoder, in_59));
        TestCase.assertEquals(out_60, Base64Test.encodeFromAscii(encoder, decoder, in_60));
        TestCase.assertEquals(out_61, Base64Test.encodeFromAscii(encoder, decoder, in_61));
        encoder = Base64.getUrlEncoder();
        decoder = Base64.getUrlDecoder();
        TestCase.assertEquals(out_56.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_56));
        TestCase.assertEquals(out_57.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_57));
        TestCase.assertEquals(out_58.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_58));
        TestCase.assertEquals(out_59.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_59));
        TestCase.assertEquals(out_60.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_60));
        TestCase.assertEquals(out_61.replaceAll("\r\n", ""), Base64Test.encodeFromAscii(encoder, decoder, in_61));
    }

    public void testGetMimeEncoder_invalidLineSeparator() {
        byte[] invalidLineSeparator = new byte[]{ 'A' };
        try {
            Base64.getMimeEncoder(20, invalidLineSeparator);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Base64.getMimeEncoder(0, invalidLineSeparator);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Base64.getMimeEncoder(20, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            Base64.getMimeEncoder(0, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testEncoder_closedStream() {
        try {
            Base64Test.closedEncodeStream().write(100);
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
        try {
            Base64Test.closedEncodeStream().write(new byte[100]);
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
        try {
            Base64Test.closedEncodeStream().write(new byte[100], 0, 1);
            TestCase.fail("Should have thrown");
        } catch (IOException expected) {
        }
    }

    /**
     * Tests {@link Decoder#decode(byte[], byte[])} for correctness.
     */
    public void testEncoder_encodeArrayToArray() {
        Base64.Encoder encoder = Base64.getEncoder();
        // Empty input
        TestCase.assertEquals(0, encoder.encode(new byte[0], new byte[0]));
        // Test data for non-empty input
        byte[] input = "abcefghi".getBytes(StandardCharsets.US_ASCII);
        String expectedString = "YWJjZWZnaGk=";
        byte[] encodedBytes = expectedString.getBytes(StandardCharsets.US_ASCII);
        // Non-empty input: output array too short
        byte[] tooShort = new byte[(encodedBytes.length) - 1];
        try {
            encoder.encode(input, tooShort);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // Non-empty input: output array longer than required
        byte[] tooLong = new byte[(encodedBytes.length) + 1];
        int tooLongBytesEncoded = encoder.encode(input, tooLong);
        TestCase.assertEquals(encodedBytes.length, tooLongBytesEncoded);
        TestCase.assertEquals(0, tooLong[((tooLong.length) - 1)]);
        Base64Test.assertArrayPrefixEquals(tooLong, encodedBytes.length, encodedBytes);
        // Non-empty input: output array has exact minimum required size
        byte[] justRight = new byte[encodedBytes.length];
        int justRightBytesEncoded = encoder.encode(input, justRight);
        TestCase.assertEquals(encodedBytes.length, justRightBytesEncoded);
        Base64Test.assertArrayEquals(encodedBytes, justRight);
    }

    public void testEncoder_encodeByteBuffer() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] emptyByteArray = new byte[0];
        ByteBuffer emptyByteBuffer = ByteBuffer.wrap(emptyByteArray);
        ByteBuffer emptyEncodedBuffer = encoder.encode(emptyByteBuffer);
        TestCase.assertEquals(emptyByteBuffer, emptyEncodedBuffer);
        TestCase.assertNotSame(emptyByteArray, emptyEncodedBuffer);
        // Test the two types of byte buffer.
        byte[] input = "abcefghi".getBytes(StandardCharsets.US_ASCII);
        String expectedString = "YWJjZWZnaGk=";
        byte[] expectedBytes = expectedString.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer inputBuffer = ByteBuffer.allocate(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.testEncoder_encodeByteBuffer(encoder, inputBuffer, expectedBytes);
        inputBuffer = ByteBuffer.allocateDirect(input.length);
        inputBuffer.put(input);
        inputBuffer.position(0);
        Base64Test.testEncoder_encodeByteBuffer(encoder, inputBuffer, expectedBytes);
    }

    /**
     * Checks that all encoders/decoders map {@code new byte[0]} to "" and vice versa.
     */
    public void testRoundTrip_empty() {
        Base64Test.checkRoundTrip_empty(Base64.getEncoder(), Base64.getDecoder());
        Base64Test.checkRoundTrip_empty(Base64.getMimeEncoder(), Base64.getMimeDecoder());
        byte[] sep = new byte[]{ '\r', '\n' };
        Base64Test.checkRoundTrip_empty(Base64.getMimeEncoder((-1), sep), Base64.getMimeDecoder());
        Base64Test.checkRoundTrip_empty(Base64.getMimeEncoder(20, new byte[0]), Base64.getMimeDecoder());
        Base64Test.checkRoundTrip_empty(Base64.getMimeEncoder(23, sep), Base64.getMimeDecoder());
        Base64Test.checkRoundTrip_empty(Base64.getMimeEncoder(76, sep), Base64.getMimeDecoder());
        Base64Test.checkRoundTrip_empty(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    }

    /**
     * Encoding of byte values 0..255 using the non-URL alphabet.
     */
    private static final String ALL_BYTE_VALUES_ENCODED = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4" + ((("OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3Bx" + "cnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmq") + "q6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj") + "5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/w==");

    public void testRoundTrip_allBytes_plain() {
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getEncoder(), Base64.getDecoder());
    }

    /**
     * Checks that if the lineSeparator is empty or the line length is {@code <= 3}
     * or larger than the data to be encoded, a single line is returned.
     */
    public void testRoundTrip_allBytes_mime_singleLine() {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(76, new byte[0]), decoder);
        // Line lengths <= 3 mean no wrapping; the separator is ignored in that case.
        byte[] separator = new byte[]{ '*' };
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(Integer.MIN_VALUE, separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder((-1), separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(0, separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(1, separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(2, separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(3, separator), decoder);
        // output fits into the permitted line length
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(Base64Test.ALL_BYTE_VALUES_ENCODED.length(), separator), decoder);
        Base64Test.checkRoundTrip_allBytes_singleLine(Base64.getMimeEncoder(Integer.MAX_VALUE, separator), decoder);
    }

    /**
     * Checks round-trip encoding/decoding for a few simple examples that
     * should work the same across three Encoder/Decoder pairs: This is
     * because they only use characters that are in both RFC 4648 Table 1
     * and Table 2, and are short enough to fit into a single line.
     */
    public void testRoundTrip_simple_basic() throws Exception {
        // uses Table 1, never adds linebreaks
        Base64Test.checkRoundTrip_simple(Base64.getEncoder(), Base64.getDecoder());
        // uses Table 1, allows 76 chars in a line
        Base64Test.checkRoundTrip_simple(Base64.getMimeEncoder(), Base64.getMimeDecoder());
        // uses Table 2, never adds linebreaks
        Base64Test.checkRoundTrip_simple(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    }

    /**
     * check a range of possible line lengths
     */
    public void testRoundTrip_allBytes_mime_lineLength() {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] separator = new byte[]{ '*' };
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(4, separator), decoder, Base64Test.wrapLines("*", Base64Test.ALL_BYTE_VALUES_ENCODED, 4));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(8, separator), decoder, Base64Test.wrapLines("*", Base64Test.ALL_BYTE_VALUES_ENCODED, 8));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(20, separator), decoder, Base64Test.wrapLines("*", Base64Test.ALL_BYTE_VALUES_ENCODED, 20));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(100, separator), decoder, Base64Test.wrapLines("*", Base64Test.ALL_BYTE_VALUES_ENCODED, 100));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(((Integer.MAX_VALUE) & (~3)), separator), decoder, Base64Test.wrapLines("*", Base64Test.ALL_BYTE_VALUES_ENCODED, ((Integer.MAX_VALUE) & (~3))));
    }

    public void testRoundTrip_allBytes_mime_lineLength_defaultsTo76Chars() {
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(), Base64.getMimeDecoder(), Base64Test.wrapLines("\r\n", Base64Test.ALL_BYTE_VALUES_ENCODED, 76));
    }

    /**
     * checks that the specified line length is rounded down to the nearest multiple of 4.
     */
    public void testRoundTrip_allBytes_mime_lineLength_isRoundedDown() {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] separator = new byte[]{ '\r', '\n' };
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(60, separator), decoder, Base64Test.wrapLines("\r\n", Base64Test.ALL_BYTE_VALUES_ENCODED, 60));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(63, separator), decoder, Base64Test.wrapLines("\r\n", Base64Test.ALL_BYTE_VALUES_ENCODED, 60));
        Base64Test.checkRoundTrip_allBytes(Base64.getMimeEncoder(10, separator), decoder, Base64Test.wrapLines("\r\n", Base64Test.ALL_BYTE_VALUES_ENCODED, 8));
    }

    public void testRoundTrip_allBytes_url() {
        String encodedUrl = Base64Test.ALL_BYTE_VALUES_ENCODED.replace('+', '-').replace('/', '_');
        Base64Test.checkRoundTrip_allBytes(Base64.getUrlEncoder(), Base64.getUrlDecoder(), encodedUrl);
    }

    public void testRoundTrip_variousSizes_plain() {
        Base64Test.checkRoundTrip_variousSizes(Base64.getEncoder(), Base64.getDecoder());
    }

    public void testRoundTrip_variousSizes_mime() {
        Base64Test.checkRoundTrip_variousSizes(Base64.getMimeEncoder(), Base64.getMimeDecoder());
    }

    public void testRoundTrip_variousSizes_url() {
        Base64Test.checkRoundTrip_variousSizes(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    }

    public void testRoundtrip_wrap_basic() throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        Base64.Decoder decoder = Base64.getDecoder();
        Base64Test.checkRoundTrip_wrapInputStream(encoder, decoder);
    }

    public void testRoundtrip_wrap_mime() throws Exception {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        Base64.Decoder decoder = Base64.getMimeDecoder();
        Base64Test.checkRoundTrip_wrapInputStream(encoder, decoder);
    }

    public void testRoundTrip_wrap_url() throws Exception {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        Base64.Decoder decoder = Base64.getUrlDecoder();
        Base64Test.checkRoundTrip_wrapInputStream(encoder, decoder);
    }

    public void testDecoder_wrap_singleByteReads() throws IOException {
        InputStream in = Base64.getDecoder().wrap(new ByteArrayInputStream("/v8=".getBytes()));
        TestCase.assertEquals(254, in.read());
        TestCase.assertEquals(255, in.read());
        TestCase.assertEquals((-1), in.read());
    }

    public void testEncoder_withoutPadding() {
        byte[] bytes = new byte[]{ ((byte) (254)), ((byte) (255)) };
        TestCase.assertEquals("/v8=", Base64.getEncoder().encodeToString(bytes));
        TestCase.assertEquals("/v8", Base64.getEncoder().withoutPadding().encodeToString(bytes));
        TestCase.assertEquals("/v8=", Base64.getMimeEncoder().encodeToString(bytes));
        TestCase.assertEquals("/v8", Base64.getMimeEncoder().withoutPadding().encodeToString(bytes));
        TestCase.assertEquals("_v8=", Base64.getUrlEncoder().encodeToString(bytes));
        TestCase.assertEquals("_v8", Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }

    public void testEncoder_wrap_plain() throws Exception {
        Base64Test.checkWrapOutputStreamConsistentWithEncode(Base64.getEncoder());
    }

    public void testEncoder_wrap_url() throws Exception {
        Base64Test.checkWrapOutputStreamConsistentWithEncode(Base64.getUrlEncoder());
    }

    public void testEncoder_wrap_mime() throws Exception {
        Base64Test.checkWrapOutputStreamConsistentWithEncode(Base64.getMimeEncoder());
    }

    /**
     * A way of writing bytes to an OutputStream.
     */
    interface WriteStrategy {
        void write(byte[] bytes, OutputStream out) throws IOException;
    }
}

