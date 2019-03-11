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


public class SpdyHeaderBlockRawDecoderTest {
    private static final int maxHeaderSize = 16;

    private static final String name = "name";

    private static final String value = "value";

    private static final byte[] nameBytes = SpdyHeaderBlockRawDecoderTest.name.getBytes();

    private static final byte[] valueBytes = SpdyHeaderBlockRawDecoderTest.value.getBytes();

    private SpdyHeaderBlockRawDecoder decoder;

    private SpdyHeadersFrame frame;

    @Test
    public void testEmptyHeaderBlock() throws Exception {
        ByteBuf headerBlock = Unpooled.EMPTY_BUFFER;
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testZeroNameValuePairs() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(4);
        headerBlock.writeInt(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testNegativeNameValuePairs() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(4);
        headerBlock.writeInt((-1));
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testOneNameValuePair() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(21);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testMissingNameLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(4);
        headerBlock.writeInt(1);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testZeroNameLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(8);
        headerBlock.writeInt(1);
        headerBlock.writeInt(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testNegativeNameLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(8);
        headerBlock.writeInt(1);
        headerBlock.writeInt((-1));
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testMissingName() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(8);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testIllegalNameOnlyNull() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(18);
        headerBlock.writeInt(1);
        headerBlock.writeInt(1);
        headerBlock.writeByte(0);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testMissingValueLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(12);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testZeroValueLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(16);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals("", frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testNegativeValueLength() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(16);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt((-1));
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testMissingValue() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(16);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testIllegalValueOnlyNull() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(17);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(1);
        headerBlock.writeByte(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testIllegalValueStartsWithNull() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(22);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(6);
        headerBlock.writeByte(0);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testIllegalValueEndsWithNull() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(22);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(6);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testMultipleValues() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(27);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(11);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(2, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).get(0));
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).get(1));
        headerBlock.release();
    }

    @Test
    public void testMultipleValuesEndsWithNull() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(28);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(12);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testIllegalValueMultipleNulls() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(28);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(12);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        headerBlock.writeByte(0);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testMissingNextNameValuePair() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(21);
        headerBlock.writeInt(2);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testMultipleNames() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(38);
        headerBlock.writeInt(2);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testExtraData() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(22);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        headerBlock.writeByte(0);
        decoder.decode(DEFAULT, headerBlock, frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testMultipleDecodes() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(21);
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        int readableBytes = headerBlock.readableBytes();
        for (int i = 0; i < readableBytes; i++) {
            ByteBuf headerBlockSegment = headerBlock.slice(i, 1);
            decoder.decode(DEFAULT, headerBlockSegment, frame);
            Assert.assertFalse(headerBlockSegment.isReadable());
        }
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        headerBlock.release();
    }

    @Test
    public void testContinueAfterInvalidHeaders() throws Exception {
        ByteBuf numHeaders = Unpooled.buffer(4);
        numHeaders.writeInt(1);
        ByteBuf nameBlock = Unpooled.buffer(8);
        nameBlock.writeInt(4);
        nameBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        ByteBuf valueBlock = Unpooled.buffer(9);
        valueBlock.writeInt(5);
        valueBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, numHeaders, frame);
        decoder.decode(DEFAULT, nameBlock, frame);
        frame.setInvalid();
        decoder.decode(DEFAULT, valueBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(numHeaders.isReadable());
        Assert.assertFalse(nameBlock.isReadable());
        Assert.assertFalse(valueBlock.isReadable());
        Assert.assertEquals(1, frame.headers().names().size());
        Assert.assertTrue(frame.headers().contains(SpdyHeaderBlockRawDecoderTest.name));
        Assert.assertEquals(1, frame.headers().getAll(SpdyHeaderBlockRawDecoderTest.name).size());
        Assert.assertEquals(SpdyHeaderBlockRawDecoderTest.value, frame.headers().get(SpdyHeaderBlockRawDecoderTest.name));
        numHeaders.release();
        nameBlock.release();
        valueBlock.release();
    }

    @Test
    public void testTruncatedHeaderName() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(((SpdyHeaderBlockRawDecoderTest.maxHeaderSize) + 18));
        headerBlock.writeInt(1);
        headerBlock.writeInt(((SpdyHeaderBlockRawDecoderTest.maxHeaderSize) + 1));
        for (int i = 0; i < ((SpdyHeaderBlockRawDecoderTest.maxHeaderSize) + 1); i++) {
            headerBlock.writeByte('a');
        }
        headerBlock.writeInt(5);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.valueBytes);
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isTruncated());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }

    @Test
    public void testTruncatedHeaderValue() throws Exception {
        ByteBuf headerBlock = Unpooled.buffer(((SpdyHeaderBlockRawDecoderTest.maxHeaderSize) + 13));
        headerBlock.writeInt(1);
        headerBlock.writeInt(4);
        headerBlock.writeBytes(SpdyHeaderBlockRawDecoderTest.nameBytes);
        headerBlock.writeInt(13);
        for (int i = 0; i < ((SpdyHeaderBlockRawDecoderTest.maxHeaderSize) - 3); i++) {
            headerBlock.writeByte('a');
        }
        decoder.decode(DEFAULT, headerBlock, frame);
        decoder.endHeaderBlock(frame);
        Assert.assertFalse(headerBlock.isReadable());
        Assert.assertTrue(frame.isTruncated());
        Assert.assertFalse(frame.isInvalid());
        Assert.assertEquals(0, frame.headers().names().size());
        headerBlock.release();
    }
}

