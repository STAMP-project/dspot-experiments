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
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Segment.SIZE;


@RunWith(Parameterized.class)
public class BufferedSinkTest {
    private interface Factory {
        BufferedSink create(Buffer data);
    }

    // ANDROID-BEGIN
    @Parameterized.Parameter
    public BufferedSinkTest.Factory factory = ((BufferedSinkTest.Factory) (BufferedSinkTest.parameters().get(0)[0]));

    // ANDROID-END
    private Buffer data;

    private BufferedSink sink;

    @Test
    public void writeNothing() throws IOException {
        sink.writeUtf8("");
        sink.flush();
        Assert.assertEquals(0, data.size());
    }

    @Test
    public void writeBytes() throws Exception {
        sink.writeByte(171);
        sink.writeByte(205);
        sink.flush();
        Assert.assertEquals("Buffer[size=2 data=abcd]", data.toString());
    }

    @Test
    public void writeLastByteInSegment() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 1)));
        sink.writeByte(32);
        sink.writeByte(33);
        sink.flush();
        Assert.assertEquals(Arrays.asList(SIZE, 1), data.segmentSizes());
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) - 1)), data.readUtf8(((SIZE) - 1)));
        Assert.assertEquals("Buffer[size=2 data=2021]", data.toString());
    }

    @Test
    public void writeShort() throws Exception {
        sink.writeShort(43981);
        sink.writeShort(17185);
        sink.flush();
        Assert.assertEquals("Buffer[size=4 data=abcd4321]", data.toString());
    }

    @Test
    public void writeShortLe() throws Exception {
        sink.writeShortLe(43981);
        sink.writeShortLe(17185);
        sink.flush();
        Assert.assertEquals("Buffer[size=4 data=cdab2143]", data.toString());
    }

    @Test
    public void writeInt() throws Exception {
        sink.writeInt(-1412567295);
        sink.writeInt(-2023406815);
        sink.flush();
        Assert.assertEquals("Buffer[size=8 data=abcdef0187654321]", data.toString());
    }

    @Test
    public void writeLastIntegerInSegment() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 4)));
        sink.writeInt(-1412567295);
        sink.writeInt(-2023406815);
        sink.flush();
        Assert.assertEquals(Arrays.asList(SIZE, 4), data.segmentSizes());
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) - 4)), data.readUtf8(((SIZE) - 4)));
        Assert.assertEquals("Buffer[size=8 data=abcdef0187654321]", data.toString());
    }

    @Test
    public void writeIntegerDoesNotQuiteFitInSegment() throws Exception {
        sink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 3)));
        sink.writeInt(-1412567295);
        sink.writeInt(-2023406815);
        sink.flush();
        Assert.assertEquals(Arrays.asList(((SIZE) - 3), 8), data.segmentSizes());
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) - 3)), data.readUtf8(((SIZE) - 3)));
        Assert.assertEquals("Buffer[size=8 data=abcdef0187654321]", data.toString());
    }

    @Test
    public void writeIntLe() throws Exception {
        sink.writeIntLe(-1412567295);
        sink.writeIntLe(-2023406815);
        sink.flush();
        Assert.assertEquals("Buffer[size=8 data=01efcdab21436587]", data.toString());
    }

    @Test
    public void writeLong() throws Exception {
        sink.writeLong(-6066930333152623839L);
        sink.writeLong(-3819410105792635904L);
        sink.flush();
        Assert.assertEquals("Buffer[size=16 data=abcdef0187654321cafebabeb0b15c00]", data.toString());
    }

    @Test
    public void writeLongLe() throws Exception {
        sink.writeLongLe(-6066930333152623839L);
        sink.writeLongLe(-3819410105792635904L);
        sink.flush();
        Assert.assertEquals("Buffer[size=16 data=2143658701efcdab005cb1b0bebafeca]", data.toString());
    }

    @Test
    public void writeStringUtf8() throws IOException {
        sink.writeUtf8("t??ran??s?r");
        sink.flush();
        Assert.assertEquals(ByteString.decodeHex("74c999cb8872616ec999cb8c73c3b472"), data.readByteString());
    }

    @Test
    public void writeSubstringUtf8() throws IOException {
        sink.writeUtf8("t??ran??s?r", 3, 7);
        sink.flush();
        Assert.assertEquals(ByteString.decodeHex("72616ec999"), data.readByteString());
    }

    @Test
    public void writeStringWithCharset() throws IOException {
        sink.writeString("t??ran??s?r", Charset.forName("utf-32be"));
        sink.flush();
        Assert.assertEquals(ByteString.decodeHex(("0000007400000259000002c800000072000000610000006e00000259" + "000002cc00000073000000f400000072")), data.readByteString());
    }

    @Test
    public void writeSubstringWithCharset() throws IOException {
        sink.writeString("t??ran??s?r", 3, 7, Charset.forName("utf-32be"));
        sink.flush();
        Assert.assertEquals(ByteString.decodeHex("00000072000000610000006e00000259"), data.readByteString());
    }

    @Test
    public void writeAll() throws Exception {
        Buffer source = new Buffer().writeUtf8("abcdef");
        Assert.assertEquals(6, sink.writeAll(source));
        Assert.assertEquals(0, source.size());
        sink.flush();
        Assert.assertEquals("abcdef", data.readUtf8());
    }

    @Test
    public void writeSource() throws Exception {
        Buffer source = new Buffer().writeUtf8("abcdef");
        // Force resolution of the Source method overload.
        sink.write(((Source) (source)), 4);
        sink.flush();
        Assert.assertEquals("abcd", data.readUtf8());
        Assert.assertEquals("ef", source.readUtf8());
    }

    @Test
    public void writeSourceReadsFully() throws Exception {
        Source source = new ForwardingSource(new Buffer()) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                sink.writeUtf8("abcd");
                return 4;
            }
        };
        sink.write(source, 8);
        sink.flush();
        Assert.assertEquals("abcdabcd", data.readUtf8());
    }

    @Test
    public void writeSourcePropagatesEof() throws IOException {
        Source source = new Buffer().writeUtf8("abcd");
        try {
            sink.write(source, 8);
            Assert.fail();
        } catch (EOFException expected) {
        }
        // Ensure that whatever was available was correctly written.
        sink.flush();
        Assert.assertEquals("abcd", data.readUtf8());
    }

    @Test
    public void writeSourceWithZeroIsNoOp() throws IOException {
        // This test ensures that a zero byte count never calls through to read the source. It may be
        // tied to something like a socket which will potentially block trying to read a segment when
        // ultimately we don't want any data.
        Source source = new ForwardingSource(new Buffer()) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                throw new AssertionError();
            }
        };
        sink.write(source, 0);
        Assert.assertEquals(0, data.size());
    }

    @Test
    public void writeAllExhausted() throws Exception {
        Buffer source = new Buffer();
        Assert.assertEquals(0, sink.writeAll(source));
        Assert.assertEquals(0, source.size());
    }

    @Test
    public void closeEmitsBufferedBytes() throws IOException {
        sink.writeByte('a');
        sink.close();
        Assert.assertEquals('a', data.readByte());
    }

    @Test
    public void outputStream() throws Exception {
        OutputStream out = sink.outputStream();
        out.write('a');
        out.write(TestUtil.repeat('b', 9998).getBytes(Util.UTF_8));
        out.write('c');
        out.flush();
        Assert.assertEquals((("a" + (TestUtil.repeat('b', 9998))) + "c"), data.readUtf8());
    }

    @Test
    public void outputStreamBounds() throws Exception {
        OutputStream out = sink.outputStream();
        try {
            out.write(new byte[100], 50, 51);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void longDecimalString() throws IOException {
        assertLongDecimalString(0);
        assertLongDecimalString(Long.MIN_VALUE);
        assertLongDecimalString(Long.MAX_VALUE);
        for (int i = 1; i < 20; i++) {
            long value = BigInteger.valueOf(10L).pow(i).longValue();
            assertLongDecimalString((value - 1));
            assertLongDecimalString(value);
        }
    }

    @Test
    public void longHexString() throws IOException {
        assertLongHexString(0);
        assertLongHexString(Long.MIN_VALUE);
        assertLongHexString(Long.MAX_VALUE);
        for (int i = 0; i < 16; i++) {
            assertLongHexString(((1 << i) - 1));
            assertLongHexString((1 << i));
        }
    }
}

