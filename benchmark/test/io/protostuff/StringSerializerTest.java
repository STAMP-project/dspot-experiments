/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff;


import io.protostuff.StringSerializer.STRING;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;

import static StringSerializer.FOUR_BYTE_LOWER_LIMIT;
import static StringSerializer.THREE_BYTE_LOWER_LIMIT;


/**
 * Tests for UTF-8 Encoding
 *
 * @author David Yu
 * @unknown Jul 6, 2010
 */
public class StringSerializerTest extends TestCase {
    // 4*3-byte
    static final String three_byte_utf8 = "\u1234\u8000\uf800\u0800";

    // 4*2-byte
    static final String two_byte_utf8 = "\u07ff\u00e1\u0088\u00b4";

    // 26 total
    static final String alphabet = "abcdefghijklmnopqrstuvwyxz";

    static final String alphabet_to_upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static final String surrogatePairs = "\ud83c\udfe0\ud83c\udf4e\ud83d\udca9";

    // Result of surrogatePairs.getBytes("UTF-8"), which is what protobuf uses.
    static final byte[] nativeSurrogatePairsSerialized = new byte[]{ -16, -97, -113, -96, -16, -97, -115, -114, -16, -97, -110, -87 };

    // Result of writeUTF8 before 3-byte surrogate fix (i.e. no 4-byte encoding)
    static final byte[] legacySurrogatePairSerialized = new byte[]{ -19, -96, -68, -19, -65, -96, -19, -96, -68, -19, -67, -114, -19, -96, -67, -19, -78, -87 };

    // 10 total
    static final String numeric = "0123456789";

    // 3 total
    static final String whitespace = "\r\n\t";

    // 71 total
    static final String foo = (((((StringSerializerTest.alphabet) + (StringSerializerTest.three_byte_utf8)) + (StringSerializerTest.numeric)) + (StringSerializerTest.two_byte_utf8)) + (StringSerializerTest.whitespace)) + (StringSerializerTest.surrogatePairs);

    static final String str_len_130 = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

    static final String[] targets = new String[]{ StringSerializerTest.three_byte_utf8, StringSerializerTest.two_byte_utf8, StringSerializerTest.alphabet, StringSerializerTest.alphabet_to_upper, StringSerializerTest.numeric, StringSerializerTest.whitespace, StringSerializerTest.foo, StringSerializerTest.str_len_130, StringSerializerTest.surrogatePairs, StringSerializerTest.repeatChar('a', (2048 - 16)), StringSerializerTest.repeatChar('a', (2048 + 16)), StringSerializerTest.repeatChar('a', (32768 - 16)), StringSerializerTest.repeatChar('a', (32768 + 16)) };

    static final String[] ascii_targets = new String[]{ StringSerializerTest.alphabet, StringSerializerTest.alphabet_to_upper, StringSerializerTest.numeric, StringSerializerTest.whitespace, StringSerializerTest.str_len_130, StringSerializerTest.repeatChar('b', (2048 - 16)), StringSerializerTest.repeatChar('b', (2048 + 16)), StringSerializerTest.repeatChar('b', (32768 - 16)), StringSerializerTest.repeatChar('b', (32768 + 16)) };

    static final int[] int_targets = new int[]{ 0, 1, -1, 10, -10, 100, -100, 1000, -1000, 10001, -10001, 1110001, -1110001, 111110001, -111110001, 1234567890, -1234567890, Integer.MAX_VALUE, Integer.MIN_VALUE };

    static final long[] long_targets = new long[]{ 0L, 1L, -1L, 10L, -10L, 100L, -100L, 1000L, -1000L, 10001L, -10001L, 1110001L, -1110001L, 111110001L, -111110001L, 11111110001L, -11111110001L, 1111111110001L, -1111111110001L, 111111111110001L, -111111111110001L, 11111111111110001L, -11111111111110001L, 1234567890123456789L, -1234567890123456789L, Long.MAX_VALUE, Long.MIN_VALUE };

    static final float[] float_targets = new float[]{ 0.0F, 10.01F, -10.01F, 1234.4321F - 1234.4321F, 56789.99F, -56789.99F, Float.MAX_VALUE, Float.MIN_VALUE };

    static final double[] double_targets = new double[]{ 0.0, 10.01, -10.01, 1234.4321 - 1234.4321, 56789.98765, -56789.98765, 1.2345678900987654E9, -1.2345678900987654E9, Double.MAX_VALUE, Double.MIN_VALUE };

    public void testVarDelimitedBoundryTwoByte() throws Exception {
        int size = (THREE_BYTE_LOWER_LIMIT) - 1;// takes 2 bytes for size and is larger than buffer

        StringSerializerTest.checkVarDelimitedBoundry(1, size);// 1st str does not fit

        StringSerializerTest.checkVarDelimitedBoundry(2, size);// 1st str fits

        StringSerializerTest.checkVarDelimitedBoundry(3, size);// 2nd str varint doesn't fit

        StringSerializerTest.checkVarDelimitedBoundry(4, size);// Only 2nd varint fits (slow)

    }

    public void testVarDelimitedBoundryThreeByte() throws Exception {
        int size = (FOUR_BYTE_LOWER_LIMIT) - 1;// takes 3 bytes for size

        StringSerializerTest.checkVarDelimitedBoundry(1, size);// 1st str does not fit

        StringSerializerTest.checkVarDelimitedBoundry(2, size);// 1st str fits

        StringSerializerTest.checkVarDelimitedBoundry(3, size);// 2nd str varint doesn't fit

        StringSerializerTest.checkVarDelimitedBoundry(4, size);// same as above

        StringSerializerTest.checkVarDelimitedBoundry(5, size);// Only 2nd varint fits (slow)

    }

    public void testInt() throws Exception {
        for (int i : StringSerializerTest.int_targets) {
            LinkedBuffer lb = new LinkedBuffer(256);
            WriteSession session = new WriteSession(lb);
            StringSerializer.writeInt(i, session, lb);
            LinkedBuffer lb2 = new LinkedBuffer(1);
            WriteSession session2 = new WriteSession(lb2);
            StringSerializer.writeInt(i, session2, lb2);
            byte[] buffered = session.toByteArray();
            byte[] buffered_needed_to_grow = session2.toByteArray();
            byte[] builtin = STRING.ser(Integer.toString(i));
            StringSerializerTest.assertEquals(builtin, buffered);
            StringSerializerTest.assertEquals(builtin, buffered_needed_to_grow);
        }
    }

    public void testLong() throws Exception {
        for (long i : StringSerializerTest.long_targets) {
            LinkedBuffer lb = new LinkedBuffer(256);
            WriteSession session = new WriteSession(lb);
            StringSerializer.writeLong(i, session, lb);
            LinkedBuffer lb2 = new LinkedBuffer(1);
            WriteSession session2 = new WriteSession(lb2);
            StringSerializer.writeLong(i, session2, lb2);
            byte[] buffered = session.toByteArray();
            byte[] buffered_needed_to_grow = session2.toByteArray();
            byte[] builtin = STRING.ser(Long.toString(i));
            StringSerializerTest.assertEquals(builtin, buffered);
            StringSerializerTest.assertEquals(builtin, buffered_needed_to_grow);
        }
    }

    public void testFloat() throws Exception {
        for (float i : StringSerializerTest.float_targets) {
            LinkedBuffer lb = new LinkedBuffer(256);
            WriteSession session = new WriteSession(lb);
            StringSerializer.writeFloat(i, session, lb);
            LinkedBuffer lb2 = new LinkedBuffer(1);
            WriteSession session2 = new WriteSession(lb2);
            StringSerializer.writeFloat(i, session2, lb2);
            byte[] buffered = session.toByteArray();
            byte[] buffered_needed_to_grow = session2.toByteArray();
            byte[] builtin = STRING.ser(Float.toString(i));
            StringSerializerTest.assertEquals(builtin, buffered);
            StringSerializerTest.assertEquals(builtin, buffered_needed_to_grow);
        }
    }

    public void testDouble() throws Exception {
        for (double i : StringSerializerTest.double_targets) {
            LinkedBuffer lb = new LinkedBuffer(256);
            WriteSession session = new WriteSession(lb);
            StringSerializer.writeDouble(i, session, lb);
            LinkedBuffer lb2 = new LinkedBuffer(1);
            WriteSession session2 = new WriteSession(lb2);
            StringSerializer.writeDouble(i, session2, lb2);
            byte[] buffered = session.toByteArray();
            byte[] buffered_needed_to_grow = session2.toByteArray();
            byte[] builtin = STRING.ser(Double.toString(i));
            StringSerializerTest.assertEquals(builtin, buffered);
            StringSerializerTest.assertEquals(builtin, buffered_needed_to_grow);
        }
    }

    public void testAscii() throws Exception {
        for (String s : StringSerializerTest.ascii_targets)
            StringSerializerTest.checkAscii(s);

    }

    public void testUTF8() throws Exception {
        for (String s : StringSerializerTest.targets)
            StringSerializerTest.check(s);

        StringSerializerTest.check("");
        StringSerializerTest.check(StringSerializerTest.str_len_130);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StringSerializerTest.check(lessThan2048);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StringSerializerTest.check(moreThan2048);
    }

    public void testUTF8VarDelimited() throws Exception {
        StringSerializerTest.checkVarDelimited(StringSerializerTest.foo, 1, 71);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.whitespace, 1, 3);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.numeric, 1, 10);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.alphabet, 1, 26);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.alphabet_to_upper, 1, 26);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.two_byte_utf8, 1, (4 * 2));
        StringSerializerTest.checkVarDelimited(StringSerializerTest.three_byte_utf8, 1, (4 * 3));
        StringSerializerTest.checkVarDelimited("1234567890123456789012345678901234567890", 1, 40);
        StringSerializerTest.checkVarDelimited("", 1, 0);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.str_len_130, 2, 130);
        StringSerializerTest.checkVarDelimited(StringSerializerTest.str_len_130.substring(10), 1, 120);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StringSerializerTest.checkVarDelimited(lessThan2048, 2, lt2048Len);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StringSerializerTest.checkVarDelimited(moreThan2048, 2, expectedLen);
        String str16383 = StringSerializerTest.repeatChar('z', 16383);
        String str16384 = str16383 + "g";
        StringSerializerTest.checkVarDelimited(str16383, 2, str16383.length());
        StringSerializerTest.checkVarDelimited(str16384, 3, str16384.length());
    }

    public void testUTF8FixedDelimited() throws Exception {
        for (String s : StringSerializerTest.targets)
            StringSerializerTest.checkFixedDelimited(s);

        StringSerializerTest.checkFixedDelimited("1234567890123456789012345678901234567890");
        StringSerializerTest.checkFixedDelimited("");
        StringSerializerTest.checkFixedDelimited(StringSerializerTest.str_len_130);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StringSerializerTest.checkFixedDelimited(lessThan2048);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StringSerializerTest.checkFixedDelimited(moreThan2048);
    }

    public void testLegacySurrogatePairs() throws Exception {
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        StringSerializer.writeUTF8(StringSerializerTest.surrogatePairs, session, lb);
        byte[] buffered = session.toByteArray();
        // By default, STRING.deser should not use the custom
        // decoder (specifically, should not check the decoded
        // string for REPLACE characters). This means that legacy
        // encoded Strings with surrogate pairs will result in
        // malformed Strings
        TestCase.assertNotSame(StringSerializerTest.surrogatePairs, STRING.deser(StringSerializerTest.legacySurrogatePairSerialized));
        // New Strings, however, should be deserialized fine
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deser(buffered));
    }

    public void testSurrogatePairsCustomOnly() throws Exception {
        // This test is mainly for Java 8+, where 3-byte surrogates
        // and 6-byte surrogate pairs are not allowed.
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        StringSerializer.writeUTF8(StringSerializerTest.surrogatePairs, session, lb);
        byte[] fastPathBuffered = session.toByteArray();
        lb = new LinkedBuffer(1);
        session = new WriteSession(lb);
        StringSerializer.writeUTF8(StringSerializerTest.surrogatePairs, session, lb);
        byte[] slowPathBuffered = session.toByteArray();
        // Output of fast path and slow path should be identical.
        TestCase.assertTrue(Arrays.equals(fastPathBuffered, slowPathBuffered));
        // Does our own serialization match native?
        TestCase.assertTrue(Arrays.equals(fastPathBuffered, StringSerializerTest.nativeSurrogatePairsSerialized));
        // Does our own serialization / deserialization work?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deserCustomOnly(fastPathBuffered));
        // Can we decode legacy encodings?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deserCustomOnly(StringSerializerTest.legacySurrogatePairSerialized));
        // Does the built in serialization work?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, new String(StringSerializerTest.nativeSurrogatePairsSerialized, "UTF-8"));
        // Should be encoded using 4-byte code points, instead of 6-byte surrogate pairs.
        TestCase.assertFalse(Arrays.equals(fastPathBuffered, StringSerializerTest.legacySurrogatePairSerialized));
        try {
            // Can we deserialize from a protobuf source (specifically java generated)
            // using our first method?
            TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deserCustomOnly(StringSerializerTest.nativeSurrogatePairsSerialized));
        } catch (RuntimeException ex) {
            // No? Fallback should catch this.
            TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deser(StringSerializerTest.nativeSurrogatePairsSerialized));
            // But it means there's a bug in the deserializer
            TestCase.fail("Deserializer should not have used built in decoder.");
        }
    }

    public void testPartialSurrogatePair() throws Exception {
        // Make sure that we don't overflow or get out of bounds,
        // since pairs require 2 characters.
        String partial = "\ud83c";
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        StringSerializer.writeUTF8(partial, session, lb);
        byte[] buffered = session.toByteArray();
        // Force the use of 'slow' path
        lb = new LinkedBuffer(1);
        session = new WriteSession(lb);
        StringSerializer.writeUTF8(partial, session, lb);
        buffered = session.toByteArray();
    }

    public void testDataInputStreamDecoding() throws Exception {
        // Unfortuneatley, DataInputStream uses Modified UTF-8,
        // which does not support 4-byte characters, as used in the
        // 'Standard UTF-8'. This is a sacrifice of generating
        // standard conformant UTF-8 Strings.
        // 
        // Note: this only happens for surrogate pairs (e.g. emoji's)
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        StringSerializer.writeUTF8FixedDelimited(StringSerializerTest.surrogatePairs, session, lb);
        byte[] buffered = session.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(buffered);
        DataInputStream din = new DataInputStream(in);
        try {
            String dinResult = din.readUTF();
            TestCase.fail();
        } catch (IOException ex) {
            // Decoding failed of 4-byte format.
        }
    }

    public void testReallyLongString() throws Exception {
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        // The motivation of this test is to make sure
        // that the serializer/deserializer can handle very large Strings.
        // DataInputStream only supports Strings up to Short.MAX_VALUE,
        // so we should make sure our implementation can support more than that.
        // 
        // Ideally, we'd like to test all the way up to Integer.MAX_VALUE, but that
        // may be unfeasable in many test environments, so we will just do 3 * Short.MAX_VALUE,
        // which would overflow an unsigned short.
        StringBuilder sb = new StringBuilder((3 * (Short.MAX_VALUE)));
        for (int i = 0; i < (3 * (Short.MAX_VALUE)); i++) {
            sb.append((i % 10));
        }
        String bigString = sb.toString();
        StringSerializer.writeUTF8(bigString, session, lb);
        byte[] buffered = session.toByteArray();
        // We want to make sure it's our implementation
        // that can handle the large string
        TestCase.assertEquals(bigString, STRING.deserCustomOnly(buffered));
    }
}

