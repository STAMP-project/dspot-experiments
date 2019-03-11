/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.io;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.UTFDataFormatException;
import java.util.Arrays;
import junit.framework.TestCase;


public final class DataOutputStreamTest extends TestCase {
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private DataOutputStream os = new DataOutputStream(bytes);

    public void test_writeBoolean() throws Exception {
        os.writeBoolean(true);
        os.writeBoolean(false);
        TestCase.assertEquals("[01, 00]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeByte() throws Exception {
        os.writeByte((-1));
        os.writeByte(0);
        os.writeByte(1);
        os.writeByte(129);
        // writeByte takes only the bottom byte from its int parameter.
        os.writeByte(4660);
        TestCase.assertEquals("[ff, 00, 01, 81, 34]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeBytes() throws Exception {
        // writeBytes takes only the bottom byte from each character.
        os.writeBytes("0\u12341");
        TestCase.assertEquals("[30, 34, 31]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeChar() throws Exception {
        // writeChar writes two-byte big-endian characters.
        os.writeChar('0');
        os.writeChar(4660);
        TestCase.assertEquals("[00, 30, 12, 34]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeChars() throws Exception {
        // writeChars writes two-byte big-endian characters.
        os.writeChars("0\u12341");
        TestCase.assertEquals("[00, 30, 12, 34, 00, 31]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeDouble() throws Exception {
        os.writeDouble(Double.longBitsToDouble(81985529216486895L));
        TestCase.assertEquals("[01, 23, 45, 67, 89, ab, cd, ef]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeFloat() throws Exception {
        os.writeFloat(Float.intBitsToFloat(19088743));
        TestCase.assertEquals("[01, 23, 45, 67]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeInt() throws Exception {
        os.writeInt(19088743);
        TestCase.assertEquals("[01, 23, 45, 67]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeLong() throws Exception {
        os.writeLong(81985529216486895L);
        TestCase.assertEquals("[01, 23, 45, 67, 89, ab, cd, ef]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeShort() throws Exception {
        // writeShort only writes the bottommost 16 bits of its int parameter.
        os.writeShort(19088743);
        TestCase.assertEquals("[45, 67]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeUTF() throws Exception {
        // The limit is 65535 *bytes* but we want to test 2- and 3-byte characters too.
        char[] chars = new char[(65535 - 1) - 2];
        Arrays.fill(chars, 0, chars.length, 'a');
        chars[0] = '\u0666';// a two-byte character

        chars[1] = '\u2603';// a three-byte character

        String maxLength = new String(chars);
        os.writeUTF(maxLength);
        byte[] expected = new byte[2 + 65535];
        expected[0] = ((byte) (255));
        expected[1] = ((byte) (255));
        // U+0666 = 0xD9 0xA6
        expected[2] = ((byte) (217));
        expected[3] = ((byte) (166));
        // U+2603 = 0xE2 0x98 0x83
        expected[4] = ((byte) (226));
        expected[5] = ((byte) (152));
        expected[6] = ((byte) (131));
        Arrays.fill(expected, 7, expected.length, ((byte) ('a')));
        TestCase.assertEquals(Arrays.toString(expected), Arrays.toString(bytes.toByteArray()));
    }

    public void test_writeUTF_NUL() throws Exception {
        // This is a special case, represented with two non-zero bytes.
        os.writeUTF("\u0000");
        TestCase.assertEquals("[00, 02, c0, 80]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }

    public void test_writeUTF_too_long() throws Exception {
        String tooLong = new String(new char[65536]);
        try {
            os.writeUTF(tooLong);
            TestCase.fail("should throw UTFDataFormatException");
        } catch (UTFDataFormatException expected) {
        }
        TestCase.assertEquals("[]", DataOutputStreamTest.toHexString(bytes.toByteArray()));
    }
}

