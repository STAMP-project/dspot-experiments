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


import Segment.SIZE;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Segment.SIZE;


@RunWith(Parameterized.class)
public class BufferedSourceTest {
    private static final BufferedSourceTest.Factory BUFFER_FACTORY = new BufferedSourceTest.Factory() {
        @Override
        public BufferedSourceTest.Pipe pipe() {
            Buffer buffer = new Buffer();
            BufferedSourceTest.Pipe result = new BufferedSourceTest.Pipe();
            result.sink = buffer;
            result.source = buffer;
            return result;
        }

        @Override
        public String toString() {
            return "Buffer";
        }
    };

    private static final BufferedSourceTest.Factory REAL_BUFFERED_SOURCE_FACTORY = new BufferedSourceTest.Factory() {
        @Override
        public BufferedSourceTest.Pipe pipe() {
            Buffer buffer = new Buffer();
            BufferedSourceTest.Pipe result = new BufferedSourceTest.Pipe();
            result.sink = buffer;
            result.source = new RealBufferedSource(buffer);
            return result;
        }

        @Override
        public String toString() {
            return "RealBufferedSource";
        }
    };

    private static final BufferedSourceTest.Factory ONE_BYTE_AT_A_TIME_FACTORY = new BufferedSourceTest.Factory() {
        @Override
        public BufferedSourceTest.Pipe pipe() {
            Buffer buffer = new Buffer();
            BufferedSourceTest.Pipe result = new BufferedSourceTest.Pipe();
            result.sink = buffer;
            result.source = new RealBufferedSource(new ForwardingSource(buffer) {
                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    return super.read(sink, Math.min(byteCount, 1L));
                }
            });
            return result;
        }

        @Override
        public String toString() {
            return "OneByteAtATime";
        }
    };

    private interface Factory {
        BufferedSourceTest.Pipe pipe();
    }

    private static class Pipe {
        BufferedSink sink;

        BufferedSource source;
    }

    // ANDROID-BEGIN
    @Parameterized.Parameter
    public BufferedSourceTest.Factory factory = ((BufferedSourceTest.Factory) (BufferedSourceTest.parameters().get(0)[0]));

    // ANDROID-END
    private BufferedSink sink;

    private BufferedSource source;

    @Test
    public void readBytes() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)) });
        Assert.assertEquals(171, ((source.readByte()) & 255));
        Assert.assertEquals(205, ((source.readByte()) & 255));
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readShort() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (1)) });
        Assert.assertEquals(((short) (43981)), source.readShort());
        Assert.assertEquals(((short) (61185)), source.readShort());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readShortLe() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (16)) });
        Assert.assertEquals(((short) (52651)), source.readShortLe());
        Assert.assertEquals(((short) (4335)), source.readShortLe());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readShortSplitAcrossMultipleSegments() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 1)));
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)) });
        source.skip(((SIZE) - 1));
        Assert.assertEquals(((short) (43981)), source.readShort());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readInt() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (1)), ((byte) (135)), ((byte) (101)), ((byte) (67)), ((byte) (33)) });
        Assert.assertEquals(-1412567295, source.readInt());
        Assert.assertEquals(-2023406815, source.readInt());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readIntLe() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (16)), ((byte) (135)), ((byte) (101)), ((byte) (67)), ((byte) (33)) });
        Assert.assertEquals(284151211, source.readIntLe());
        Assert.assertEquals(558065031, source.readIntLe());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readIntSplitAcrossMultipleSegments() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 3)));
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (1)) });
        source.skip(((SIZE) - 3));
        Assert.assertEquals(-1412567295, source.readInt());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readLong() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (16)), ((byte) (135)), ((byte) (101)), ((byte) (67)), ((byte) (33)), ((byte) (54)), ((byte) (71)), ((byte) (88)), ((byte) (105)), ((byte) (18)), ((byte) (35)), ((byte) (52)), ((byte) (69)) });
        Assert.assertEquals(-6066930268728114399L, source.readLong());
        Assert.assertEquals(3911192009693672517L, source.readLong());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readLongLe() throws Exception {
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (16)), ((byte) (135)), ((byte) (101)), ((byte) (67)), ((byte) (33)), ((byte) (54)), ((byte) (71)), ((byte) (88)), ((byte) (105)), ((byte) (18)), ((byte) (35)), ((byte) (52)), ((byte) (69)) });
        Assert.assertEquals(2396871057470377387L, source.readLongLe());
        Assert.assertEquals(4986649249389758262L, source.readLongLe());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readLongSplitAcrossMultipleSegments() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 7)));
        sink.write(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)), ((byte) (1)), ((byte) (135)), ((byte) (101)), ((byte) (67)), ((byte) (33)) });
        source.skip(((SIZE) - 7));
        Assert.assertEquals(-6066930333152623839L, source.readLong());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readAll() throws IOException {
        source.buffer().writeUtf8("abc");
        sink.writeUtf8("def");
        Buffer sink = new Buffer();
        Assert.assertEquals(6, source.readAll(sink));
        Assert.assertEquals("abcdef", sink.readUtf8());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readAllExhausted() throws IOException {
        MockSink mockSink = new MockSink();
        Assert.assertEquals(0, source.readAll(mockSink));
        Assert.assertTrue(source.exhausted());
        mockSink.assertLog();
    }

    @Test
    public void readExhaustedSource() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('a', 10));
        Assert.assertEquals((-1), source.read(sink, 10));
        Assert.assertEquals(10, sink.size());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readZeroBytesFromSource() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('a', 10));
        // Either 0 or -1 is reasonable here. For consistency with Android's
        // ByteArrayInputStream we return 0.
        Assert.assertEquals((-1), source.read(sink, 0));
        Assert.assertEquals(10, sink.size());
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void readFully() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', 10000));
        Buffer sink = new Buffer();
        source.readFully(sink, 9999);
        Assert.assertEquals(TestUtil.repeat('a', 9999), sink.readUtf8());
        Assert.assertEquals("a", source.readUtf8());
    }

    @Test
    public void readFullyTooShortThrows() throws IOException {
        sink.writeUtf8("Hi");
        Buffer sink = new Buffer();
        try {
            source.readFully(sink, 5);
            Assert.fail();
        } catch (EOFException ignored) {
        }
        // Verify we read all that we could from the source.
        Assert.assertEquals("Hi", sink.readUtf8());
    }

    @Test
    public void readFullyByteArray() throws IOException {
        Buffer data = new Buffer();
        data.writeUtf8("Hello").writeUtf8(TestUtil.repeat('e', SIZE));
        byte[] expected = data.clone().readByteArray();
        sink.write(data, data.size());
        byte[] sink = new byte[(SIZE) + 5];
        source.readFully(sink);
        TestUtil.assertByteArraysEquals(expected, sink);
    }

    @Test
    public void readFullyByteArrayTooShortThrows() throws IOException {
        sink.writeUtf8("Hello");
        byte[] sink = new byte[6];
        try {
            source.readFully(sink);
            Assert.fail();
        } catch (EOFException ignored) {
        }
        // Verify we read all that we could from the source.
        TestUtil.assertByteArraysEquals(new byte[]{ 'H', 'e', 'l', 'l', 'o', 0 }, sink);
    }

    @Test
    public void readIntoByteArray() throws IOException {
        sink.writeUtf8("abcd");
        byte[] sink = new byte[3];
        int read = source.read(sink);
        if ((factory) == (BufferedSourceTest.ONE_BYTE_AT_A_TIME_FACTORY)) {
            Assert.assertEquals(1, read);
            byte[] expected = new byte[]{ 'a', 0, 0 };
            TestUtil.assertByteArraysEquals(expected, sink);
        } else {
            Assert.assertEquals(3, read);
            byte[] expected = new byte[]{ 'a', 'b', 'c' };
            TestUtil.assertByteArraysEquals(expected, sink);
        }
    }

    @Test
    public void readIntoByteArrayNotEnough() throws IOException {
        sink.writeUtf8("abcd");
        byte[] sink = new byte[5];
        int read = source.read(sink);
        if ((factory) == (BufferedSourceTest.ONE_BYTE_AT_A_TIME_FACTORY)) {
            Assert.assertEquals(1, read);
            byte[] expected = new byte[]{ 'a', 0, 0, 0, 0 };
            TestUtil.assertByteArraysEquals(expected, sink);
        } else {
            Assert.assertEquals(4, read);
            byte[] expected = new byte[]{ 'a', 'b', 'c', 'd', 0 };
            TestUtil.assertByteArraysEquals(expected, sink);
        }
    }

    @Test
    public void readIntoByteArrayOffsetAndCount() throws IOException {
        sink.writeUtf8("abcd");
        byte[] sink = new byte[7];
        int read = source.read(sink, 2, 3);
        if ((factory) == (BufferedSourceTest.ONE_BYTE_AT_A_TIME_FACTORY)) {
            Assert.assertEquals(1, read);
            byte[] expected = new byte[]{ 0, 0, 'a', 0, 0, 0, 0 };
            TestUtil.assertByteArraysEquals(expected, sink);
        } else {
            Assert.assertEquals(3, read);
            byte[] expected = new byte[]{ 0, 0, 'a', 'b', 'c', 0, 0 };
            TestUtil.assertByteArraysEquals(expected, sink);
        }
    }

    @Test
    public void readByteArray() throws IOException {
        String string = "abcd" + (TestUtil.repeat('e', SIZE));
        sink.writeUtf8(string);
        TestUtil.assertByteArraysEquals(string.getBytes(Util.UTF_8), source.readByteArray());
    }

    @Test
    public void readByteArrayPartial() throws IOException {
        sink.writeUtf8("abcd");
        Assert.assertEquals("[97, 98, 99]", Arrays.toString(source.readByteArray(3)));
        Assert.assertEquals("d", source.readUtf8(1));
    }

    @Test
    public void readByteString() throws IOException {
        sink.writeUtf8("abcd").writeUtf8(TestUtil.repeat('e', SIZE));
        Assert.assertEquals(("abcd" + (TestUtil.repeat('e', SIZE))), source.readByteString().utf8());
    }

    @Test
    public void readByteStringPartial() throws IOException {
        sink.writeUtf8("abcd").writeUtf8(TestUtil.repeat('e', SIZE));
        Assert.assertEquals("abc", source.readByteString(3).utf8());
        Assert.assertEquals("d", source.readUtf8(1));
    }

    @Test
    public void readSpecificCharsetPartial() throws Exception {
        sink.write(ByteString.decodeHex(("0000007600000259000002c80000006c000000e40000007300000259" + "000002cc000000720000006100000070000000740000025900000072")));
        Assert.assertEquals("v??l?s?", source.readString((7 * 4), Charset.forName("utf-32")));
    }

    @Test
    public void readSpecificCharset() throws Exception {
        sink.write(ByteString.decodeHex(("0000007600000259000002c80000006c000000e40000007300000259" + "000002cc000000720000006100000070000000740000025900000072")));
        Assert.assertEquals("v??l?s??rapt?r", source.readString(Charset.forName("utf-32")));
    }

    @Test
    public void readUtf8SpansSegments() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        source.skip(((SIZE) - 1));
        Assert.assertEquals("aa", source.readUtf8(2));
    }

    @Test
    public void readUtf8Segment() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', SIZE));
        Assert.assertEquals(TestUtil.repeat('a', SIZE), source.readUtf8(SIZE));
    }

    @Test
    public void readUtf8PartialBuffer() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) + 20)));
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) + 10)), source.readUtf8(((SIZE) + 10)));
    }

    @Test
    public void readUtf8EntireBuffer() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) * 2)), source.readUtf8());
    }

    @Test
    public void skip() throws Exception {
        sink.writeUtf8("a");
        sink.writeUtf8(TestUtil.repeat('b', SIZE));
        sink.writeUtf8("c");
        source.skip(1);
        Assert.assertEquals('b', ((source.readByte()) & 255));
        source.skip(((SIZE) - 2));
        Assert.assertEquals('b', ((source.readByte()) & 255));
        source.skip(1);
        Assert.assertTrue(source.exhausted());
    }

    @Test
    public void skipInsufficientData() throws Exception {
        sink.writeUtf8("a");
        try {
            source.skip(2);
            Assert.fail();
        } catch (EOFException ignored) {
        }
    }

    @Test
    public void indexOf() throws Exception {
        // The segment is empty.
        Assert.assertEquals((-1), source.indexOf(((byte) ('a'))));
        // The segment has one value.
        sink.writeUtf8("a");// a

        Assert.assertEquals(0, source.indexOf(((byte) ('a'))));
        Assert.assertEquals((-1), source.indexOf(((byte) ('b'))));
        // The segment has lots of data.
        sink.writeUtf8(TestUtil.repeat('b', ((SIZE) - 2)));// ab...b

        Assert.assertEquals(0, source.indexOf(((byte) ('a'))));
        Assert.assertEquals(1, source.indexOf(((byte) ('b'))));
        Assert.assertEquals((-1), source.indexOf(((byte) ('c'))));
        // The segment doesn't start at 0, it starts at 2.
        source.skip(2);// b...b

        Assert.assertEquals((-1), source.indexOf(((byte) ('a'))));
        Assert.assertEquals(0, source.indexOf(((byte) ('b'))));
        Assert.assertEquals((-1), source.indexOf(((byte) ('c'))));
        // The segment is full.
        sink.writeUtf8("c");// b...bc

        Assert.assertEquals((-1), source.indexOf(((byte) ('a'))));
        Assert.assertEquals(0, source.indexOf(((byte) ('b'))));
        Assert.assertEquals(((SIZE) - 3), source.indexOf(((byte) ('c'))));
        // The segment doesn't start at 2, it starts at 4.
        source.skip(2);// b...bc

        Assert.assertEquals((-1), source.indexOf(((byte) ('a'))));
        Assert.assertEquals(0, source.indexOf(((byte) ('b'))));
        Assert.assertEquals(((SIZE) - 5), source.indexOf(((byte) ('c'))));
        // Two segments.
        sink.writeUtf8("d");// b...bcd, d is in the 2nd segment.

        Assert.assertEquals(((SIZE) - 4), source.indexOf(((byte) ('d'))));
        Assert.assertEquals((-1), source.indexOf(((byte) ('e'))));
    }

    @Test
    public void indexOfWithOffset() throws IOException {
        sink.writeUtf8("a").writeUtf8(TestUtil.repeat('b', SIZE)).writeUtf8("c");
        Assert.assertEquals((-1), source.indexOf(((byte) ('a')), 1));
        Assert.assertEquals(15, source.indexOf(((byte) ('b')), 15));
    }

    @Test
    public void indexOfByteString() throws IOException {
        Assert.assertEquals((-1), source.indexOf(ByteString.encodeUtf8("flop")));
        sink.writeUtf8("flip flop");
        Assert.assertEquals(5, source.indexOf(ByteString.encodeUtf8("flop")));
        source.readUtf8();// Clear stream.

        // Make sure we backtrack and resume searching after partial match.
        sink.writeUtf8("hi hi hi hey");
        Assert.assertEquals(3, source.indexOf(ByteString.encodeUtf8("hi hi hey")));
    }

    @Test
    public void indexOfByteStringWithOffset() throws IOException {
        Assert.assertEquals((-1), source.indexOf(ByteString.encodeUtf8("flop"), 1));
        sink.writeUtf8("flop flip flop");
        Assert.assertEquals(10, source.indexOf(ByteString.encodeUtf8("flop"), 1));
        source.readUtf8();// Clear stream

        // Make sure we backtrack and resume searching after partial match.
        sink.writeUtf8("hi hi hi hi hey");
        Assert.assertEquals(6, source.indexOf(ByteString.encodeUtf8("hi hi hey"), 1));
    }

    @Test
    public void indexOfByteStringInvalidArgumentsThrows() throws IOException {
        try {
            source.indexOf(ByteString.of());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("bytes is empty", e.getMessage());
        }
        try {
            source.indexOf(ByteString.encodeUtf8("hi"), (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("fromIndex < 0", e.getMessage());
        }
    }

    @Test
    public void indexOfElement() throws IOException {
        sink.writeUtf8("a").writeUtf8(TestUtil.repeat('b', SIZE)).writeUtf8("c");
        Assert.assertEquals(0, source.indexOfElement(ByteString.encodeUtf8("DEFGaHIJK")));
        Assert.assertEquals(1, source.indexOfElement(ByteString.encodeUtf8("DEFGHIJKb")));
        Assert.assertEquals(((SIZE) + 1), source.indexOfElement(ByteString.encodeUtf8("cDEFGHIJK")));
        Assert.assertEquals(1, source.indexOfElement(ByteString.encodeUtf8("DEFbGHIc")));
        Assert.assertEquals((-1L), source.indexOfElement(ByteString.encodeUtf8("DEFGHIJK")));
        Assert.assertEquals((-1L), source.indexOfElement(ByteString.encodeUtf8("")));
    }

    @Test
    public void indexOfElementWithOffset() throws IOException {
        sink.writeUtf8("a").writeUtf8(TestUtil.repeat('b', SIZE)).writeUtf8("c");
        Assert.assertEquals((-1), source.indexOfElement(ByteString.encodeUtf8("DEFGaHIJK"), 1));
        Assert.assertEquals(15, source.indexOfElement(ByteString.encodeUtf8("DEFGHIJKb"), 15));
    }

    @Test
    public void request() throws IOException {
        sink.writeUtf8("a").writeUtf8(TestUtil.repeat('b', SIZE)).writeUtf8("c");
        Assert.assertTrue(source.request(((SIZE) + 2)));
        Assert.assertFalse(source.request(((SIZE) + 3)));
    }

    @Test
    public void require() throws IOException {
        sink.writeUtf8("a").writeUtf8(TestUtil.repeat('b', SIZE)).writeUtf8("c");
        source.require(((SIZE) + 2));
        try {
            source.require(((SIZE) + 3));
            Assert.fail();
        } catch (EOFException expected) {
        }
    }

    @Test
    public void inputStream() throws Exception {
        sink.writeUtf8("abc");
        InputStream in = source.inputStream();
        byte[] bytes = new byte[]{ 'z', 'z', 'z' };
        int read = in.read(bytes);
        if ((factory) == (BufferedSourceTest.ONE_BYTE_AT_A_TIME_FACTORY)) {
            Assert.assertEquals(1, read);
            TestUtil.assertByteArrayEquals("azz", bytes);
            read = in.read(bytes);
            Assert.assertEquals(1, read);
            TestUtil.assertByteArrayEquals("bzz", bytes);
            read = in.read(bytes);
            Assert.assertEquals(1, read);
            TestUtil.assertByteArrayEquals("czz", bytes);
        } else {
            Assert.assertEquals(3, read);
            TestUtil.assertByteArrayEquals("abc", bytes);
        }
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void inputStreamOffsetCount() throws Exception {
        sink.writeUtf8("abcde");
        InputStream in = source.inputStream();
        byte[] bytes = new byte[]{ 'z', 'z', 'z', 'z', 'z' };
        int read = in.read(bytes, 1, 3);
        if ((factory) == (BufferedSourceTest.ONE_BYTE_AT_A_TIME_FACTORY)) {
            Assert.assertEquals(1, read);
            TestUtil.assertByteArrayEquals("zazzz", bytes);
        } else {
            Assert.assertEquals(3, read);
            TestUtil.assertByteArrayEquals("zabcz", bytes);
        }
    }

    @Test
    public void inputStreamSkip() throws Exception {
        sink.writeUtf8("abcde");
        InputStream in = source.inputStream();
        Assert.assertEquals(4, in.skip(4));
        Assert.assertEquals('e', in.read());
    }

    @Test
    public void inputStreamCharByChar() throws Exception {
        sink.writeUtf8("abc");
        InputStream in = source.inputStream();
        Assert.assertEquals('a', in.read());
        Assert.assertEquals('b', in.read());
        Assert.assertEquals('c', in.read());
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void inputStreamBounds() throws IOException {
        sink.writeUtf8(TestUtil.repeat('a', 100));
        InputStream in = source.inputStream();
        try {
            in.read(new byte[100], 50, 51);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void longHexString() throws IOException {
        assertLongHexString("8000000000000000", -9223372036854775808L);
        assertLongHexString("fffffffffffffffe", -2L);
        assertLongHexString("FFFFFFFFFFFFFFFe", -2L);
        assertLongHexString("ffffffffffffffff", -1L);
        assertLongHexString("FFFFFFFFFFFFFFFF", -1L);
        assertLongHexString("0000000000000000", 0);
        assertLongHexString("0000000000000001", 1);
        assertLongHexString("7999999999999999", 8762203435012037017L);
        assertLongHexString("FF", 255);
        assertLongHexString("0000000000000001", 1);
    }

    @Test
    public void hexStringWithManyLeadingZeros() throws IOException {
        assertLongHexString("00000000000000001", 1);
        assertLongHexString("0000000000000000ffffffffffffffff", -1L);
        assertLongHexString("00000000000000007fffffffffffffff", 9223372036854775807L);
        assertLongHexString(((TestUtil.repeat('0', ((SIZE) + 1))) + "1"), 1);
    }

    @Test
    public void longHexStringAcrossSegment() throws IOException {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 8))).writeUtf8("FFFFFFFFFFFFFFFF");
        source.skip(((SIZE) - 8));
        Assert.assertEquals((-1), source.readHexadecimalUnsignedLong());
    }

    @Test
    public void longHexStringTooLongThrows() throws IOException {
        try {
            sink.writeUtf8("fffffffffffffffff");
            source.readHexadecimalUnsignedLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Number too large: fffffffffffffffff", e.getMessage());
        }
    }

    @Test
    public void longHexStringTooShortThrows() throws IOException {
        try {
            sink.writeUtf8(" ");
            source.readHexadecimalUnsignedLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Expected leading [0-9a-fA-F] character but was 0x20", e.getMessage());
        }
    }

    @Test
    public void longHexEmptySourceThrows() throws IOException {
        try {
            sink.writeUtf8("");
            source.readHexadecimalUnsignedLong();
            Assert.fail();
        } catch (IllegalStateException | EOFException expected) {
        }
    }

    @Test
    public void longDecimalString() throws IOException {
        assertLongDecimalString("-9223372036854775808", -9223372036854775808L);
        assertLongDecimalString("-1", (-1L));
        assertLongDecimalString("0", 0L);
        assertLongDecimalString("1", 1L);
        assertLongDecimalString("9223372036854775807", 9223372036854775807L);
        assertLongDecimalString("00000001", 1L);
        assertLongDecimalString("-000001", (-1L));
    }

    @Test
    public void longDecimalStringAcrossSegment() throws IOException {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 8))).writeUtf8("1234567890123456");
        sink.writeUtf8("zzz");
        source.skip(((SIZE) - 8));
        Assert.assertEquals(1234567890123456L, source.readDecimalLong());
        Assert.assertEquals("zzz", source.readUtf8());
    }

    @Test
    public void longDecimalStringTooLongThrows() throws IOException {
        try {
            sink.writeUtf8("12345678901234567890");// Too many digits.

            source.readDecimalLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Number too large: 12345678901234567890", e.getMessage());
        }
    }

    @Test
    public void longDecimalStringTooHighThrows() throws IOException {
        try {
            sink.writeUtf8("9223372036854775808");// Right size but cannot fit.

            source.readDecimalLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Number too large: 9223372036854775808", e.getMessage());
        }
    }

    @Test
    public void longDecimalStringTooLowThrows() throws IOException {
        try {
            sink.writeUtf8("-9223372036854775809");// Right size but cannot fit.

            source.readDecimalLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Number too large: -9223372036854775809", e.getMessage());
        }
    }

    @Test
    public void longDecimalStringTooShortThrows() throws IOException {
        try {
            sink.writeUtf8(" ");
            source.readDecimalLong();
            Assert.fail();
        } catch (NumberFormatException e) {
            Assert.assertEquals("Expected leading [0-9] or '-' character but was 0x20", e.getMessage());
        }
    }

    @Test
    public void longDecimalEmptyThrows() throws IOException {
        try {
            sink.writeUtf8("");
            source.readDecimalLong();
            Assert.fail();
        } catch (IllegalStateException | EOFException expected) {
        }
    }

    @Test
    public void codePoints() throws IOException {
        sink.write(ByteString.decodeHex("7f"));
        Assert.assertEquals(127, source.readUtf8CodePoint());
        sink.write(ByteString.decodeHex("dfbf"));
        Assert.assertEquals(2047, source.readUtf8CodePoint());
        sink.write(ByteString.decodeHex("efbfbf"));
        Assert.assertEquals(65535, source.readUtf8CodePoint());
        sink.write(ByteString.decodeHex("f48fbfbf"));
        Assert.assertEquals(1114111, source.readUtf8CodePoint());
    }

    @Test
    public void decimalStringWithManyLeadingZeros() throws IOException {
        assertLongDecimalString("00000000000000001", 1);
        assertLongDecimalString("00000000000000009223372036854775807", 9223372036854775807L);
        assertLongDecimalString("-00000000000000009223372036854775808", -9223372036854775808L);
        assertLongDecimalString(((TestUtil.repeat('0', ((SIZE) + 1))) + "1"), 1);
    }
}

