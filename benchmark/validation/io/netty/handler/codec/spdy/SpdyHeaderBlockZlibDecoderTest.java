/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.spdy;


import ByteBufAllocator.DEFAULT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;


public class SpdyHeaderBlockZlibDecoderTest {
    // zlib header indicating 32K window size fastest deflate algorithm with SPDY dictionary
    private static final byte[] zlibHeader = new byte[]{ 120, 63, ((byte) (227)), ((byte) (198)), ((byte) (167)), ((byte) (194)) };

    private static final byte[] zlibSyncFlush = new byte[]{ 0, 0, 0, ((byte) (255)), ((byte) (255)) };

    private static final int maxHeaderSize = 8192;

    private static final String name = "name";

    private static final String value = "value";

    private static final byte[] nameBytes = SpdyHeaderBlockZlibDecoderTest.name.getBytes();

    private static final byte[] valueBytes = SpdyHeaderBlockZlibDecoderTest.value.getBytes();

    private SpdyHeaderBlockZlibDecoder decoder;

    private SpdyHeadersFrame frame;

    @Test
    public void testHeaderBlock() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(37);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(21);// little-endian length (21)

        headerBlock.writeByte(0);// little-endian length (21)

        headerBlock.writeByte(234);// one's compliment of length

        headerBlock.writeByte(255);// one's compliment of length

        headerBlock.writeInt(1);// number of Name/Value pairs

        headerBlock.writeInt(4);// length of name

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.nameBytes);
        headerBlock.writeInt(5);// length of value

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.valueBytes);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibSyncFlush);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockZlibDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockZlibDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockZlibDecoderTest.value, frame.headers().get(SpdyHeaderBlockZlibDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testHeaderBlockMultipleDecodes() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(37);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(21);// little-endian length (21)

        headerBlock.writeByte(0);// little-endian length (21)

        headerBlock.writeByte(234);// one's compliment of length

        headerBlock.writeByte(255);// one's compliment of length

        headerBlock.writeInt(1);// number of Name/Value pairs

        headerBlock.writeInt(4);// length of name

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.nameBytes);
        headerBlock.writeInt(5);// length of value

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.valueBytes);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibSyncFlush);
        int readableBytes = headerBlock.readableBytes();
        for (int i = 0; i < readableBytes; i++) {
            ByteBuf headerBlockSegment = headerBlock.slice(i, 1);
            decoder.decode(DEFAULT, headerBlockSegment, frame);
            Assert.assertFalse(headerBlockSegment.isReadable());
        }
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockZlibDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockZlibDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockZlibDecoderTest.value, frame.headers().get(SpdyHeaderBlockZlibDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testLargeHeaderName() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(8220);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(12);// little-endian length (8204)

        headerBlock.writeByte(32);// little-endian length (8204)

        headerBlock.writeByte(243);// one's compliment of length

        headerBlock.writeByte(223);// one's compliment of length

        headerBlock.writeInt(1);// number of Name/Value pairs

        headerBlock.writeInt(8192);// length of name

        for (int i = 0; i < 8192; i++) {
            headerBlock.writeByte('n');
        }
        headerBlock.writeInt(0);// length of value

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibSyncFlush);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertFalse(frame.isTruncated());
        Assert.assertEquals(1, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testLargeHeaderValue() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(8220);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(12);// little-endian length (8204)

        headerBlock.writeByte(32);// little-endian length (8204)

        headerBlock.writeByte(243);// one's compliment of length

        headerBlock.writeByte(223);// one's compliment of length

        headerBlock.writeInt(1);// number of Name/Value pairs

        headerBlock.writeInt(1);// length of name

        headerBlock.writeByte('n');
        headerBlock.writeInt(8191);// length of value

        for (int i = 0; i < 8191; i++) {
            headerBlock.writeByte('v');
        }
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibSyncFlush);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertFalse(frame.isTruncated());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertEquals(8191, frame.headers().get("n").length());
        headerBlock.release();
    }

    @Test(expected = SpdyProtocolException.class)
    public void testHeaderBlockExtraData() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(37);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(21);// little-endian length (21)

        headerBlock.writeByte(0);// little-endian length (21)

        headerBlock.writeByte(234);// one's compliment of length

        headerBlock.writeByte(255);// one's compliment of length

        headerBlock.writeInt(1);// number of Name/Value pairs

        headerBlock.writeInt(4);// length of name

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.nameBytes);
        headerBlock.writeInt(5);// length of value

        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.valueBytes);
        headerBlock.writeByte(25);// adler-32 checksum

        headerBlock.writeByte(165);// adler-32 checksum

        headerBlock.writeByte(3);// adler-32 checksum

        headerBlock.writeByte(201);// adler-32 checksum

        headerBlock.writeByte(0);// Data following zlib stream

        decoder.decode(DEFAULT, headerBlock, frame);
        headerBlock.release();
    }

    @Test(expected = SpdyProtocolException.class)
    public void testHeaderBlockInvalidDictionary() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(7);
        headerBlock.writeByte(120);
        headerBlock.writeByte(63);
        headerBlock.writeByte(1);// Unknown dictionary

        headerBlock.writeByte(2);// Unknown dictionary

        headerBlock.writeByte(3);// Unknown dictionary

        headerBlock.writeByte(4);// Unknown dictionary

        headerBlock.writeByte(0);// Non-compressed block

        decoder.decode(DEFAULT, headerBlock, frame);
        headerBlock.release();
    }

    @Test(expected = SpdyProtocolException.class)
    public void testHeaderBlockInvalidDeflateBlock() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(11);
        headerBlock.writeBytes(SpdyHeaderBlockZlibDecoderTest.zlibHeader);
        headerBlock.writeByte(0);// Non-compressed block

        headerBlock.writeByte(0);// little-endian length (0)

        headerBlock.writeByte(0);// little-endian length (0)

        headerBlock.writeByte(0);// invalid one's compliment

        headerBlock.writeByte(0);// invalid one's compliment

        decoder.decode(DEFAULT, headerBlock, frame);
        headerBlock.release();
    }
}

