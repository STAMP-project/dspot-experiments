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
package com.google.android.exoplayer2.util;


import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link ParsableByteArray}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ParsableByteArrayTest {
    private static final byte[] TEST_DATA = new byte[]{ 15, ((byte) (255)), ((byte) (66)), ((byte) (15)), 0, 0, 0, 0 };

    @Test
    public void testReadShort() {
        ParsableByteArrayTest.testReadShort(((short) (-1)));
        ParsableByteArrayTest.testReadShort(((short) (0)));
        ParsableByteArrayTest.testReadShort(((short) (1)));
        ParsableByteArrayTest.testReadShort(Short.MIN_VALUE);
        ParsableByteArrayTest.testReadShort(Short.MAX_VALUE);
    }

    @Test
    public void testReadInt() {
        ParsableByteArrayTest.testReadInt(0);
        ParsableByteArrayTest.testReadInt(1);
        ParsableByteArrayTest.testReadInt((-1));
        ParsableByteArrayTest.testReadInt(Integer.MIN_VALUE);
        ParsableByteArrayTest.testReadInt(Integer.MAX_VALUE);
    }

    @Test
    public void testReadUnsignedInt() {
        ParsableByteArrayTest.testReadUnsignedInt(0);
        ParsableByteArrayTest.testReadUnsignedInt(1);
        ParsableByteArrayTest.testReadUnsignedInt(Integer.MAX_VALUE);
        ParsableByteArrayTest.testReadUnsignedInt(((Integer.MAX_VALUE) + 1L));
        ParsableByteArrayTest.testReadUnsignedInt(4294967295L);
    }

    @Test
    public void testReadUnsignedIntToInt() {
        ParsableByteArrayTest.testReadUnsignedIntToInt(0);
        ParsableByteArrayTest.testReadUnsignedIntToInt(1);
        ParsableByteArrayTest.testReadUnsignedIntToInt(Integer.MAX_VALUE);
        try {
            ParsableByteArrayTest.testReadUnsignedIntToInt((-1));
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        try {
            ParsableByteArrayTest.testReadUnsignedIntToInt(Integer.MIN_VALUE);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void testReadUnsignedLongToLong() {
        ParsableByteArrayTest.testReadUnsignedLongToLong(0);
        ParsableByteArrayTest.testReadUnsignedLongToLong(1);
        ParsableByteArrayTest.testReadUnsignedLongToLong(Long.MAX_VALUE);
        try {
            ParsableByteArrayTest.testReadUnsignedLongToLong((-1));
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
        try {
            ParsableByteArrayTest.testReadUnsignedLongToLong(Long.MIN_VALUE);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void testReadLong() {
        ParsableByteArrayTest.testReadLong(0);
        ParsableByteArrayTest.testReadLong(1);
        ParsableByteArrayTest.testReadLong((-1));
        ParsableByteArrayTest.testReadLong(Long.MIN_VALUE);
        ParsableByteArrayTest.testReadLong(Long.MAX_VALUE);
    }

    @Test
    public void testReadingMovesPosition() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // Given an array at the start
        assertThat(parsableByteArray.getPosition()).isEqualTo(0);
        // When reading an integer, the position advances
        parsableByteArray.readUnsignedInt();
        assertThat(parsableByteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testOutOfBoundsThrows() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // Given an array at the end
        parsableByteArray.readUnsignedLongToLong();
        assertThat(parsableByteArray.getPosition()).isEqualTo(ParsableByteArrayTest.TEST_DATA.length);
        // Then reading more data throws.
        try {
            parsableByteArray.readUnsignedInt();
            Assert.fail();
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testModificationsAffectParsableArray() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // When modifying the wrapped byte array
        byte[] data = parsableByteArray.data;
        long readValue = parsableByteArray.readUnsignedInt();
        data[0] = ((byte) ((ParsableByteArrayTest.TEST_DATA[0]) + 1));
        parsableByteArray.setPosition(0);
        // Then the parsed value changes.
        assertThat(parsableByteArray.readUnsignedInt()).isNotEqualTo(readValue);
    }

    @Test
    public void testReadingUnsignedLongWithMsbSetThrows() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // Given an array with the most-significant bit set on the top byte
        byte[] data = parsableByteArray.data;
        data[0] = ((byte) (128));
        // Then reading an unsigned long throws.
        try {
            parsableByteArray.readUnsignedLongToLong();
            Assert.fail();
        } catch (Exception e) {
            // Expected.
        }
    }

    @Test
    public void testReadUnsignedFixedPoint1616() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // When reading the integer part of a 16.16 fixed point value
        int value = parsableByteArray.readUnsignedFixedPoint1616();
        // Then the read value is equal to the array elements interpreted as a short.
        assertThat(value).isEqualTo((((255 & (ParsableByteArrayTest.TEST_DATA[0])) << 8) | ((ParsableByteArrayTest.TEST_DATA[1]) & 255)));
        assertThat(parsableByteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testReadingBytesReturnsCopy() {
        ParsableByteArray parsableByteArray = ParsableByteArrayTest.getTestDataArray();
        // When reading all the bytes back
        int length = parsableByteArray.limit();
        assertThat(length).isEqualTo(ParsableByteArrayTest.TEST_DATA.length);
        byte[] copy = new byte[length];
        parsableByteArray.readBytes(copy, 0, length);
        // Then the array elements are the same.
        assertThat(copy).isEqualTo(parsableByteArray.data);
    }

    @Test
    public void testReadLittleEndianLong() {
        ParsableByteArray byteArray = new ParsableByteArray(new byte[]{ 1, 0, 0, 0, 0, 0, 0, ((byte) (255)) });
        assertThat(byteArray.readLittleEndianLong()).isEqualTo(-72057594037927935L);
        assertThat(byteArray.getPosition()).isEqualTo(8);
    }

    @Test
    public void testReadLittleEndianUnsignedInt() {
        ParsableByteArray byteArray = new ParsableByteArray(new byte[]{ 16, 0, 0, ((byte) (255)) });
        assertThat(byteArray.readLittleEndianUnsignedInt()).isEqualTo(4278190096L);
        assertThat(byteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testReadLittleEndianInt() {
        ParsableByteArray byteArray = new ParsableByteArray(new byte[]{ 1, 0, 0, ((byte) (255)) });
        assertThat(byteArray.readLittleEndianInt()).isEqualTo(-16777215);
        assertThat(byteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testReadLittleEndianUnsignedInt24() {
        byte[] data = new byte[]{ 1, 2, ((byte) (255)) };
        ParsableByteArray byteArray = new ParsableByteArray(data);
        assertThat(byteArray.readLittleEndianUnsignedInt24()).isEqualTo(16712193);
        assertThat(byteArray.getPosition()).isEqualTo(3);
    }

    @Test
    public void testReadInt24Positive() {
        byte[] data = new byte[]{ 1, 2, ((byte) (255)) };
        ParsableByteArray byteArray = new ParsableByteArray(data);
        assertThat(byteArray.readInt24()).isEqualTo(66303);
        assertThat(byteArray.getPosition()).isEqualTo(3);
    }

    @Test
    public void testReadInt24Negative() {
        byte[] data = new byte[]{ ((byte) (255)), 2, ((byte) (1)) };
        ParsableByteArray byteArray = new ParsableByteArray(data);
        assertThat(byteArray.readInt24()).isEqualTo(-65023);
        assertThat(byteArray.getPosition()).isEqualTo(3);
    }

    @Test
    public void testReadLittleEndianUnsignedShort() {
        ParsableByteArray byteArray = new ParsableByteArray(new byte[]{ 1, ((byte) (255)), 2, ((byte) (255)) });
        assertThat(byteArray.readLittleEndianUnsignedShort()).isEqualTo(65281);
        assertThat(byteArray.getPosition()).isEqualTo(2);
        assertThat(byteArray.readLittleEndianUnsignedShort()).isEqualTo(65282);
        assertThat(byteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testReadLittleEndianShort() {
        ParsableByteArray byteArray = new ParsableByteArray(new byte[]{ 1, ((byte) (255)), 2, ((byte) (255)) });
        assertThat(byteArray.readLittleEndianShort()).isEqualTo(((short) (65281)));
        assertThat(byteArray.getPosition()).isEqualTo(2);
        assertThat(byteArray.readLittleEndianShort()).isEqualTo(((short) (65282)));
        assertThat(byteArray.getPosition()).isEqualTo(4);
    }

    @Test
    public void testReadString() {
        byte[] data = new byte[]{ ((byte) (195)), ((byte) (164)), ((byte) (32)), ((byte) (195)), ((byte) (182)), ((byte) (32)), ((byte) (194)), ((byte) (174)), ((byte) (32)), ((byte) (207)), ((byte) (128)), ((byte) (32)), ((byte) (226)), ((byte) (136)), ((byte) (154)), ((byte) (32)), ((byte) (194)), ((byte) (177)), ((byte) (32)), ((byte) (232)), ((byte) (176)), ((byte) (162)), ((byte) (32)) };
        ParsableByteArray byteArray = new ParsableByteArray(data);
        assertThat(byteArray.readString(data.length)).isEqualTo("? ? ? ? ? ? ? ");
        assertThat(byteArray.getPosition()).isEqualTo(data.length);
    }

    @Test
    public void testReadAsciiString() {
        byte[] data = new byte[]{ 't', 'e', 's', 't' };
        ParsableByteArray testArray = new ParsableByteArray(data);
        assertThat(testArray.readString(data.length, Charset.forName("US-ASCII"))).isEqualTo("test");
        assertThat(testArray.getPosition()).isEqualTo(data.length);
    }

    @Test
    public void testReadStringOutOfBoundsDoesNotMovePosition() {
        byte[] data = new byte[]{ ((byte) (195)), ((byte) (164)), ((byte) (32)) };
        ParsableByteArray byteArray = new ParsableByteArray(data);
        try {
            byteArray.readString(((data.length) + 1));
            Assert.fail();
        } catch (StringIndexOutOfBoundsException e) {
            assertThat(byteArray.getPosition()).isEqualTo(0);
        }
    }

    @Test
    public void testReadEmptyString() {
        byte[] bytes = new byte[0];
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isNull();
    }

    @Test
    public void testReadNullTerminatedStringWithLengths() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', 0, 'b', 'a', 'r', 0 };
        // Test with lengths that match NUL byte positions.
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readNullTerminatedString(4)).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(4);
        assertThat(parser.readNullTerminatedString(4)).isEqualTo("bar");
        assertThat(parser.getPosition()).isEqualTo(8);
        assertThat(parser.readNullTerminatedString()).isNull();
        // Test with lengths that do not match NUL byte positions.
        parser = new ParsableByteArray(bytes);
        assertThat(parser.readNullTerminatedString(2)).isEqualTo("fo");
        assertThat(parser.getPosition()).isEqualTo(2);
        assertThat(parser.readNullTerminatedString(2)).isEqualTo("o");
        assertThat(parser.getPosition()).isEqualTo(4);
        assertThat(parser.readNullTerminatedString(3)).isEqualTo("bar");
        assertThat(parser.getPosition()).isEqualTo(7);
        assertThat(parser.readNullTerminatedString(1)).isEqualTo("");
        assertThat(parser.getPosition()).isEqualTo(8);
        assertThat(parser.readNullTerminatedString()).isNull();
        // Test with limit at NUL
        parser = new ParsableByteArray(bytes, 4);
        assertThat(parser.readNullTerminatedString(4)).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(4);
        assertThat(parser.readNullTerminatedString()).isNull();
        // Test with limit before NUL
        parser = new ParsableByteArray(bytes, 3);
        assertThat(parser.readNullTerminatedString(3)).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(3);
        assertThat(parser.readNullTerminatedString()).isNull();
    }

    @Test
    public void testReadNullTerminatedString() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', 0, 'b', 'a', 'r', 0 };
        // Test normal case.
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readNullTerminatedString()).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(4);
        assertThat(parser.readNullTerminatedString()).isEqualTo("bar");
        assertThat(parser.getPosition()).isEqualTo(8);
        assertThat(parser.readNullTerminatedString()).isNull();
        // Test with limit at NUL.
        parser = new ParsableByteArray(bytes, 4);
        assertThat(parser.readNullTerminatedString()).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(4);
        assertThat(parser.readNullTerminatedString()).isNull();
        // Test with limit before NUL.
        parser = new ParsableByteArray(bytes, 3);
        assertThat(parser.readNullTerminatedString()).isEqualTo("foo");
        assertThat(parser.getPosition()).isEqualTo(3);
        assertThat(parser.readNullTerminatedString()).isNull();
    }

    @Test
    public void testReadNullTerminatedStringWithoutEndingNull() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', 0, 'b', 'a', 'r' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readNullTerminatedString()).isEqualTo("foo");
        assertThat(parser.readNullTerminatedString()).isEqualTo("bar");
        assertThat(parser.readNullTerminatedString()).isNull();
    }

    @Test
    public void testReadSingleLineWithoutEndingTrail() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isEqualTo("foo");
        assertThat(parser.readLine()).isNull();
    }

    @Test
    public void testReadSingleLineWithEndingLf() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', '\n' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isEqualTo("foo");
        assertThat(parser.readLine()).isNull();
    }

    @Test
    public void testReadTwoLinesWithCrFollowedByLf() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', '\r', '\n', 'b', 'a', 'r' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isEqualTo("foo");
        assertThat(parser.readLine()).isEqualTo("bar");
        assertThat(parser.readLine()).isNull();
    }

    @Test
    public void testReadThreeLinesWithEmptyLine() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', '\r', '\n', '\r', 'b', 'a', 'r' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isEqualTo("foo");
        assertThat(parser.readLine()).isEqualTo("");
        assertThat(parser.readLine()).isEqualTo("bar");
        assertThat(parser.readLine()).isNull();
    }

    @Test
    public void testReadFourLinesWithLfFollowedByCr() {
        byte[] bytes = new byte[]{ 'f', 'o', 'o', '\n', '\r', '\r', 'b', 'a', 'r', '\r', '\n' };
        ParsableByteArray parser = new ParsableByteArray(bytes);
        assertThat(parser.readLine()).isEqualTo("foo");
        assertThat(parser.readLine()).isEqualTo("");
        assertThat(parser.readLine()).isEqualTo("");
        assertThat(parser.readLine()).isEqualTo("bar");
        assertThat(parser.readLine()).isNull();
    }
}

