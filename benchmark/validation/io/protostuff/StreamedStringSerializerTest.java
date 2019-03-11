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
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import junit.framework.TestCase;

import static StringSerializer.FOUR_BYTE_LOWER_LIMIT;
import static StringSerializer.THREE_BYTE_LOWER_LIMIT;


/**
 * Tests for streaming UTF-8 Encoding
 *
 * @author David Yu
 * @unknown Sep 18, 2010
 */
public class StreamedStringSerializerTest extends TestCase {
    static final int NUM_BUF_SIZE = 32;

    static final int BUF_SIZE = 256;

    public void testVarDelimitedBoundryTwoByte() throws Exception {
        int size = (THREE_BYTE_LOWER_LIMIT) - 1;// takes 2 bytes for size and is larger than buffer

        checkVarDelimitedBoundry(1, size);// 1st str does not fit

        checkVarDelimitedBoundry(2, size);// 1st str fits

        checkVarDelimitedBoundry(3, size);// 2nd str varint doesn't fit

        checkVarDelimitedBoundry(4, size);// Only 2nd varint fits (slow)

    }

    public void testVarDelimitedBoundryThreeByte() throws Exception {
        int size = (FOUR_BYTE_LOWER_LIMIT) - 1;// takes 3 bytes for size

        checkVarDelimitedBoundry(1, size);// 1st str does not fit

        checkVarDelimitedBoundry(2, size);// 1st str fits

        checkVarDelimitedBoundry(3, size);// 2nd str varint doesn't fit

        checkVarDelimitedBoundry(4, size);// same as above

        checkVarDelimitedBoundry(5, size);// Only 2nd varint fits (slow)

    }

    public void testInt() throws Exception {
        for (int i : StringSerializerTest.int_targets) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LinkedBuffer lb = new LinkedBuffer(StreamedStringSerializerTest.BUF_SIZE);
            WriteSession session = new WriteSession(lb, out);
            StreamedStringSerializer.writeInt(i, session, lb);
            LinkedBuffer.writeTo(out, lb);
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            LinkedBuffer lb2 = new LinkedBuffer(StreamedStringSerializerTest.NUM_BUF_SIZE);
            WriteSession session2 = new WriteSession(lb2, out2);
            StreamedStringSerializer.writeInt(i, session2, lb2);
            LinkedBuffer.writeTo(out2, lb2);
            byte[] buffered = out.toByteArray();
            byte[] buffered_needed_to_flush = out2.toByteArray();
            byte[] builtin = STRING.ser(Integer.toString(i));
            StreamedStringSerializerTest.assertEquals(builtin, buffered);
            StreamedStringSerializerTest.assertEquals(builtin, buffered_needed_to_flush);
        }
    }

    public void testLong() throws Exception {
        for (long i : StringSerializerTest.long_targets) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LinkedBuffer lb = new LinkedBuffer(StreamedStringSerializerTest.BUF_SIZE);
            WriteSession session = new WriteSession(lb, out);
            StreamedStringSerializer.writeLong(i, session, lb);
            LinkedBuffer.writeTo(out, lb);
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            LinkedBuffer lb2 = new LinkedBuffer(StreamedStringSerializerTest.NUM_BUF_SIZE);
            WriteSession session2 = new WriteSession(lb2, out2);
            StreamedStringSerializer.writeLong(i, session2, lb2);
            LinkedBuffer.writeTo(out2, lb2);
            byte[] buffered = out.toByteArray();
            byte[] buffered_needed_to_flush = out2.toByteArray();
            byte[] builtin = STRING.ser(Long.toString(i));
            StreamedStringSerializerTest.assertEquals(builtin, buffered);
            StreamedStringSerializerTest.assertEquals(builtin, buffered_needed_to_flush);
        }
    }

    public void testFloat() throws Exception {
        for (float i : StringSerializerTest.float_targets) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LinkedBuffer lb = new LinkedBuffer(StreamedStringSerializerTest.BUF_SIZE);
            WriteSession session = new WriteSession(lb, out);
            StreamedStringSerializer.writeFloat(i, session, lb);
            LinkedBuffer.writeTo(out, lb);
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            LinkedBuffer lb2 = new LinkedBuffer(StreamedStringSerializerTest.NUM_BUF_SIZE);
            WriteSession session2 = new WriteSession(lb2, out2);
            StreamedStringSerializer.writeFloat(i, session2, lb2);
            LinkedBuffer.writeTo(out2, lb2);
            byte[] buffered = out.toByteArray();
            byte[] buffered_needed_to_flush = out2.toByteArray();
            byte[] builtin = STRING.ser(Float.toString(i));
            StreamedStringSerializerTest.assertEquals(builtin, buffered);
            StreamedStringSerializerTest.assertEquals(builtin, buffered_needed_to_flush);
        }
    }

    public void testDouble() throws Exception {
        for (double i : StringSerializerTest.double_targets) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            LinkedBuffer lb = new LinkedBuffer(StreamedStringSerializerTest.BUF_SIZE);
            WriteSession session = new WriteSession(lb, out);
            StreamedStringSerializer.writeDouble(i, session, lb);
            LinkedBuffer.writeTo(out, lb);
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            LinkedBuffer lb2 = new LinkedBuffer(StreamedStringSerializerTest.NUM_BUF_SIZE);
            WriteSession session2 = new WriteSession(lb2, out2);
            StreamedStringSerializer.writeDouble(i, session2, lb2);
            LinkedBuffer.writeTo(out2, lb2);
            byte[] buffered = out.toByteArray();
            byte[] buffered_needed_to_flush = out2.toByteArray();
            byte[] builtin = STRING.ser(Double.toString(i));
            StreamedStringSerializerTest.assertEquals(builtin, buffered);
            StreamedStringSerializerTest.assertEquals(builtin, buffered_needed_to_flush);
        }
    }

    public void testAscii() throws Exception {
        for (String s : StringSerializerTest.ascii_targets)
            StreamedStringSerializerTest.checkAscii(s);

    }

    public void testUTF8() throws Exception {
        for (String s : StringSerializerTest.targets)
            StreamedStringSerializerTest.check(s);

        StreamedStringSerializerTest.check("");
        StreamedStringSerializerTest.check(StringSerializerTest.str_len_130);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StreamedStringSerializerTest.check(lessThan2048);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StreamedStringSerializerTest.check(moreThan2048);
    }

    public void testUTF8VarDelimited() throws Exception {
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.foo, 1, 71);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.whitespace, 1, 3);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.numeric, 1, 10);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.alphabet, 1, 26);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.alphabet_to_upper, 1, 26);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.two_byte_utf8, 1, (4 * 2));
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.three_byte_utf8, 1, (4 * 3));
        StreamedStringSerializerTest.checkVarDelimited("1234567890123456789012345678901234567890", 1, 40);
        StreamedStringSerializerTest.checkVarDelimited("", 1, 0);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.str_len_130, 2, 130);
        StreamedStringSerializerTest.checkVarDelimited(StringSerializerTest.str_len_130.substring(10), 1, 120);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StreamedStringSerializerTest.checkVarDelimited(lessThan2048, 2, lt2048Len);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StreamedStringSerializerTest.checkVarDelimited(moreThan2048, 2, expectedLen);
        String str16383 = repeatChar('z', 16383);
        String str16384 = str16383 + "g";
        StreamedStringSerializerTest.checkVarDelimited(str16383, 2, str16383.length());
        StreamedStringSerializerTest.checkVarDelimited(str16384, 3, str16384.length());
    }

    public void testUTF8FixedDelimited() throws Exception {
        for (String s : StringSerializerTest.targets)
            StreamedStringSerializerTest.checkFixedDelimited(s);

        StreamedStringSerializerTest.checkFixedDelimited("1234567890123456789012345678901234567890");
        StreamedStringSerializerTest.checkFixedDelimited("");
        StreamedStringSerializerTest.checkFixedDelimited(StringSerializerTest.str_len_130);
        String lessThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 14; i++)
            lessThan2048 += StringSerializerTest.str_len_130;

        int lt2048Len = 130 * 15;
        TestCase.assertTrue(((lessThan2048.length()) == lt2048Len));
        StreamedStringSerializerTest.checkFixedDelimited(lessThan2048);
        String moreThan2048 = StringSerializerTest.str_len_130;
        for (int i = 0; i < 20; i++)
            moreThan2048 += StringSerializerTest.str_len_130;

        int expectedLen = 130 * 21;
        TestCase.assertTrue(((moreThan2048.length()) == expectedLen));
        StreamedStringSerializerTest.checkFixedDelimited(moreThan2048);
    }

    public void testSurrogatePairs() throws Exception {
        // This test is mainly for Java 8+, where 3-byte surrogates
        // and 6-byte surrogate pairs are not allowed.
        LinkedBuffer lb = new LinkedBuffer(256);
        WriteSession session = new WriteSession(lb);
        StreamedStringSerializer.writeUTF8(StringSerializerTest.surrogatePairs, session, lb);
        byte[] buffered = session.toByteArray();
        // Does our own serialization match native?
        TestCase.assertTrue(Arrays.equals(buffered, StringSerializerTest.nativeSurrogatePairsSerialized));
        // Does our own serialization / deserialization work?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deserCustomOnly(buffered));
        // Can we decode legacy encodings?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, STRING.deserCustomOnly(StringSerializerTest.legacySurrogatePairSerialized));
        // Does the built in serialization work?
        TestCase.assertEquals(StringSerializerTest.surrogatePairs, new String(StringSerializerTest.nativeSurrogatePairsSerialized, "UTF-8"));
        // Should be encoded using 4-byte code points, instead of 6-byte surrogate pairs.
        TestCase.assertFalse(Arrays.equals(buffered, StringSerializerTest.legacySurrogatePairSerialized));
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
        // 3 bytes can't hold a 4-byte encoding, but we
        // don't expect it to use the 4-byte encoding path,
        // since it's not a pair
        LinkedBuffer lb = new LinkedBuffer(3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WriteSession session = new WriteSession(lb, out);
        StreamedStringSerializer.writeUTF8(partial, session, lb);
        byte[] buffered = session.toByteArray();
    }

    public void testMultipleLargeStringsExceedingBufferSize() throws Exception {
        LinkedBuffer buffer = LinkedBuffer.allocate(256);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WriteSession session = new WriteSession(buffer, out);
        String utf8OneByte = repeatChar('a', 1024);
        String utf8TwoBytes = repeatChar(((char) (2047)), (1024 / 2));
        String utf8ThreeBytes = repeatChar(((char) (2048)), (1024 / 3));
        StreamedStringSerializerTest.writeToSession(utf8OneByte, utf8TwoBytes, utf8ThreeBytes, session, false);
        TestCase.assertTrue(((session.tail) == (session.head)));
        // flush remaining
        LinkedBuffer.writeTo(out, buffer);
        // clear
        buffer.clear();
        byte[] data = out.toByteArray();
        LinkedBuffer buffer2 = LinkedBuffer.allocate(256);
        WriteSession session2 = new WriteSession(buffer2);
        StreamedStringSerializerTest.writeToSession(utf8OneByte, utf8TwoBytes, utf8ThreeBytes, session2, false);
        byte[] data2 = session2.toByteArray();
        StreamedStringSerializerTest.assertEquals(STRING.deser(data), STRING.deser(data2));
    }

    public void testMultipleLargeStringsExceedingBufferSizeDelimited() throws Exception {
        LinkedBuffer buffer = LinkedBuffer.allocate(256);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WriteSession session = new WriteSession(buffer, out);
        String utf8OneByte = repeatChar('a', 1024);
        String utf8TwoBytes = repeatChar(((char) (2047)), (1024 / 2));
        String utf8ThreeBytes = repeatChar(((char) (2048)), (1024 / 3));
        StreamedStringSerializerTest.writeToSession(utf8OneByte, utf8TwoBytes, utf8ThreeBytes, session, true);
        TestCase.assertTrue(((session.tail) == (session.head)));
        // temporary buffers will remain
        TestCase.assertTrue(((session.tail.next) != null));
        // flush remaining
        LinkedBuffer.writeTo(out, buffer);
        // clear
        buffer.clear();
        byte[] data = out.toByteArray();
        LinkedBuffer buffer2 = LinkedBuffer.allocate(256);
        WriteSession session2 = new WriteSession(buffer2);
        StreamedStringSerializerTest.writeToSession(utf8OneByte, utf8TwoBytes, utf8ThreeBytes, session2, true);
        byte[] data2 = session2.toByteArray();
        // We cannot do a direct STRING.deser, since it's not supposed to handle
        // multiple fields. Thus, in order to test this, we have to iterate through each
        // field, and compare the STRING.deser of that. Since we know the length ahead of time,
        // we can do simple verifications to make life easier.
        // Make sure the output bytes are the same.
        TestCase.assertEquals(data.length, data2.length);
        // Make sure the data is identical.
        for (int i = 0; i < (data.length); i++)
            TestCase.assertEquals(data[i], data2[i]);

        // Now that we've confirmed they're identical, doing checks on the
        // validity is the same as doing it on the other.
        // 
        // We will check that the VarDelimited value was encoded properly,
        // and that we can use that value to properly deserialize the String.
        int offset = 0;
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8OneByte, offset, 1024);
        offset += 1024 + 2;
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8OneByte, offset, 1024);
        offset += 1024 + 2;
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8TwoBytes, offset, 1024);
        offset += 1024 + 2;
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8TwoBytes, offset, 1024);
        offset += 1024 + 2;
        // These guy's length is actually 1023, because 1024/3 is 341.
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8ThreeBytes, offset, 1023);
        offset += 1023 + 2;
        StreamedStringSerializerTest.checkEncodedStringWithVarDelimited(data, utf8ThreeBytes, offset, 1023);
        offset += 1023 + 2;
        TestCase.assertEquals(offset, data.length);
    }
}

