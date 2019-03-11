/**
 * Copyright (C) 2014 Square, Inc.
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
package okio;


import Buffer.REPLACEMENT_CHARACTER;
import Util.UTF_8;
import java.io.EOFException;
import org.junit.Assert;
import org.junit.Test;

import static Segment.SIZE;


public final class Utf8Test {
    @Test
    public void oneByteCharacters() throws Exception {
        assertEncoded("00", 0);// Smallest 1-byte character.

        assertEncoded("20", ' ');
        assertEncoded("7e", '~');
        assertEncoded("7f", 127);// Largest 1-byte character.

    }

    @Test
    public void twoByteCharacters() throws Exception {
        assertEncoded("c280", 128);// Smallest 2-byte character.

        assertEncoded("c3bf", 255);
        assertEncoded("c480", 256);
        assertEncoded("dfbf", 2047);// Largest 2-byte character.

    }

    @Test
    public void threeByteCharacters() throws Exception {
        assertEncoded("e0a080", 2048);// Smallest 3-byte character.

        assertEncoded("e0bfbf", 4095);
        assertEncoded("e18080", 4096);
        assertEncoded("e1bfbf", 8191);
        assertEncoded("ed8080", 53248);
        assertEncoded("ed9fbf", 55295);// Largest character lower than the min surrogate.

        assertEncoded("ee8080", 57344);// Smallest character greater than the max surrogate.

        assertEncoded("eebfbf", 61439);
        assertEncoded("ef8080", 61440);
        assertEncoded("efbfbf", 65535);// Largest 3-byte character.

    }

    // @Test public void fourByteCharacters() throws Exception {
    // assertEncoded("f0908080", 0x010000); // Smallest surrogate pair.
    // assertEncoded("f48fbfbf", 0x10ffff); // Largest code point expressible by UTF-16.
    // }
    // 
    // @Test public void danglingHighSurrogate() throws Exception {
    // assertStringEncoded("3f", "\ud800"); // "?"
    // }
    // 
    // @Test public void lowSurrogateWithoutHighSurrogate() throws Exception {
    // assertStringEncoded("3f", "\udc00"); // "?"
    // }
    // 
    // @Test public void highSurrogateFollowedByNonSurrogate() throws Exception {
    // assertStringEncoded("3f61", "\ud800\u0061"); // "?a": Following character is too low.
    // assertStringEncoded("3fee8080", "\ud800\ue000"); // "?\ue000": Following character is too high.
    // }
    @Test
    public void multipleSegmentString() throws Exception {
        String a = TestUtil.repeat('a', (((SIZE) + (SIZE)) + 1));
        Buffer encoded = new Buffer().writeUtf8(a);
        Buffer expected = new Buffer().write(a.getBytes(UTF_8));
        Assert.assertEquals(expected, encoded);
    }

    @Test
    public void stringSpansSegments() throws Exception {
        Buffer buffer = new Buffer();
        String a = TestUtil.repeat('a', ((SIZE) - 1));
        String b = "bb";
        String c = TestUtil.repeat('c', ((SIZE) - 1));
        buffer.writeUtf8(a);
        buffer.writeUtf8(b);
        buffer.writeUtf8(c);
        Assert.assertEquals(((a + b) + c), buffer.readUtf8());
    }

    @Test
    public void readEmptyBufferThrowsEofException() throws Exception {
        Buffer buffer = new Buffer();
        try {
            buffer.readUtf8CodePoint();
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @Test
    public void readLeadingContinuationByteReturnsReplacementCharacter() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeByte(191);
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void readMissingContinuationBytesThrowsEofException() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeByte(223);
        try {
            buffer.readUtf8CodePoint();
            Assert.fail();
        } catch (EOFException expected) {
        }
        Assert.assertFalse(buffer.exhausted());// Prefix byte wasn't consumed.

    }

    @Test
    public void readTooLargeCodepointReturnsReplacementCharacter() throws Exception {
        // 5-byte and 6-byte code points are not supported.
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("f888808080"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void readNonContinuationBytesReturnsReplacementCharacter() throws Exception {
        // Use a non-continuation byte where a continuation byte is expected.
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("df20"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertEquals(32, buffer.readUtf8CodePoint());// Non-continuation character not consumed.

        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void readCodePointBeyondUnicodeMaximum() throws Exception {
        // A 4-byte encoding with data above the U+10ffff Unicode maximum.
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("f4908080"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void readSurrogateCodePoint() throws Exception {
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("eda080"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
        buffer.write(ByteString.decodeHex("edbfbf"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void readOverlongCodePoint() throws Exception {
        // Use 2 bytes to encode data that only needs 1 byte.
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("c080"));
        Assert.assertEquals(REPLACEMENT_CHARACTER, buffer.readUtf8CodePoint());
        Assert.assertTrue(buffer.exhausted());
    }

    @Test
    public void writeSurrogateCodePoint() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8CodePoint(55295);// Below lowest surrogate is okay.

        try {
            buffer.writeUtf8CodePoint(55296);// Lowest surrogate throws.

            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            buffer.writeUtf8CodePoint(57343);// Highest surrogate throws.

            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        buffer.writeUtf8CodePoint(57344);// Above highest surrogate is okay.

    }

    @Test
    public void writeCodePointBeyondUnicodeMaximum() throws Exception {
        Buffer buffer = new Buffer();
        try {
            buffer.writeUtf8CodePoint(1114112);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

