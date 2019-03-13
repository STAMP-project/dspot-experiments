/**
 * Copyright 2012 The Netty Project
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
package io.netty.handler.codec.compression;


import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.CharBuffer;
import org.junit.Assert;
import org.junit.Test;


public class SnappyTest {
    private final Snappy.Snappy snappy = new Snappy.Snappy();

    @Test
    public void testDecodeLiteral() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 5// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
         });
        ByteBuf out = Unpooled.buffer(5);
        snappy.decode(in, out);
        // "netty"
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 110, 101, 116, 116, 121 });
        Assert.assertEquals("Literal was not decoded correctly", expected, out);
        in.release();
        out.release();
        expected.release();
    }

    @Test
    public void testDecodeCopyWith1ByteOffset() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 10// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
        , (1 << 2) | 1// copy with 1-byte offset + length
        , 5// offset
         });
        ByteBuf out = Unpooled.buffer(10);
        snappy.decode(in, out);
        // "nettynetty" - we saved a whole byte :)
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 110, 101, 116, 116, 121, 110, 101, 116, 116, 121 });
        Assert.assertEquals("Copy was not decoded correctly", expected, out);
        in.release();
        out.release();
        expected.release();
    }

    @Test(expected = DecompressionException.class)
    public void testDecodeCopyWithTinyOffset() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 11// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
        , (5 << 2) | 1// copy with 1-byte offset + length
        , 0// INVALID offset (< 1)
         });
        ByteBuf out = Unpooled.buffer(10);
        try {
            snappy.decode(in, out);
        } finally {
            in.release();
            out.release();
        }
    }

    @Test(expected = DecompressionException.class)
    public void testDecodeCopyWithOffsetBeforeChunk() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 10// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
        , (5 << 2) | 1// copy with 1-byte offset + length
        , 11// INVALID offset (greater than chunk size)
         });
        ByteBuf out = Unpooled.buffer(10);
        try {
            snappy.decode(in, out);
        } finally {
            in.release();
            out.release();
        }
    }

    @Test(expected = DecompressionException.class)
    public void testDecodeWithOverlyLongPreamble() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ -128, -128, -128, -128, 127// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
         });
        ByteBuf out = Unpooled.buffer(10);
        try {
            snappy.decode(in, out);
        } finally {
            in.release();
            out.release();
        }
    }

    @Test
    public void encodeShortTextIsLiteral() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 110, 101, 116, 116, 121 });
        ByteBuf out = Unpooled.buffer(7);
        snappy.encode(in, out, 5);
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 5// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
         });
        Assert.assertEquals("Encoded literal was invalid", expected, out);
        in.release();
        out.release();
        expected.release();
    }

    @Test
    public void encodeAndDecodeLongTextUsesCopy() throws Exception {
        String srcStr = "Netty has been designed carefully with the experiences " + (("earned from the implementation of a lot of protocols " + "such as FTP, SMTP, HTTP, and various binary and ") + "text-based legacy protocols");
        ByteBuf in = Unpooled.wrappedBuffer(srcStr.getBytes("US-ASCII"));
        ByteBuf out = Unpooled.buffer(180);
        snappy.encode(in, out, in.readableBytes());
        // The only compressibility in the above are the words:
        // "the ", "rotocols", " of ", "TP, " and "and ". So this is a literal,
        // followed by a copy followed by another literal, followed by another copy...
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ -73, 1// preamble length
        , -16, 66// literal tag + length
        , // Literal
        78, 101, 116, 116, 121, 32, 104, 97, 115, 32, 98, 101, 101, 110, 32, 100, 101, 115, 105, 103, 110, 101, 100, 32, 99, 97, 114, 101, 102, 117, 108, 108, 121, 32, 119, 105, 116, 104, 32, 116, 104, 101, 32, 101, 120, 112, 101, 114, 105, 101, 110, 99, 101, 115, 32, 101, 97, 114, 110, 101, 100, 32, 102, 114, 111, 109, 32, // copy of "the "
        1, 28, 88, // Next literal
        105, 109, 112, 108, 101, 109, 101, 110, 116, 97, 116, 105, 111, 110, 32, 111, 102, 32, 97, 32, 108, 111, 116, // copy of " of "
        1, 9, 96, // literal
        112, 114, 111, 116, 111, 99, 111, 108, 115, 32, 115, 117, 99, 104, 32, 97, 115, 32, 70, 84, 80, 44, 32, 83, 77, // copy of " TP, "
        1, 6, 4, // literal
        72, 84, // copy of " TP, "
        1, 6, 68, // literal
        97, 110, 100, 32, 118, 97, 114, 105, 111, 117, 115, 32, 98, 105, 110, 97, 114, 121, // copy of "and "
        5, 19, 72, // literal
        116, 101, 120, 116, 45, 98, 97, 115, 101, 100, 32, 108, 101, 103, 97, 99, 121, 32, 112, // copy of "rotocols"
        17, 76 });
        Assert.assertEquals("Encoded result was incorrect", expected, out);
        // Decode
        ByteBuf outDecoded = Unpooled.buffer();
        snappy.decode(out, outDecoded);
        Assert.assertEquals(CharBuffer.wrap(srcStr), CharBuffer.wrap(outDecoded.getCharSequence(0, outDecoded.writerIndex(), US_ASCII)));
        in.release();
        out.release();
        outDecoded.release();
    }

    @Test
    public void testCalculateChecksum() {
        ByteBuf input = Unpooled.wrappedBuffer(new byte[]{ 'n', 'e', 't', 't', 'y' });
        Assert.assertEquals(maskChecksum(-691303595), calculateChecksum(input));
        input.release();
    }

    @Test
    public void testValidateChecksumMatches() {
        ByteBuf input = Unpooled.wrappedBuffer(new byte[]{ 'y', 't', 't', 'e', 'n' });
        validateChecksum(maskChecksum(760034613), input);
        input.release();
    }

    @Test(expected = DecompressionException.class)
    public void testValidateChecksumFails() {
        ByteBuf input = Unpooled.wrappedBuffer(new byte[]{ 'y', 't', 't', 'e', 'n' });
        try {
            validateChecksum(maskChecksum(-691303595), input);
        } finally {
            input.release();
        }
    }

    @Test
    public void testEncodeLiteralAndDecodeLiteral() {
        int[] lengths = new int[]{ 17// default
        , 256// case 60
        , 4096// case 61
        , 1048576// case 62
        , 16777217// case 63
         };
        for (int len : lengths) {
            ByteBuf in = Unpooled.wrappedBuffer(new byte[len]);
            ByteBuf encoded = Unpooled.buffer(10);
            ByteBuf decoded = Unpooled.buffer(10);
            ByteBuf expected = Unpooled.wrappedBuffer(new byte[len]);
            try {
                encodeLiteral(in, encoded, len);
                byte tag = encoded.readByte();
                decodeLiteral(tag, encoded, decoded);
                Assert.assertEquals("Encoded or decoded literal was incorrect", expected, decoded);
            } finally {
                in.release();
                encoded.release();
                decoded.release();
                expected.release();
            }
        }
    }

    @Test
    public void testLarge2ByteLiteralLengthAndCopyOffset() {
        ByteBuf compressed = Unpooled.buffer();
        ByteBuf actualDecompressed = Unpooled.buffer();
        ByteBuf expectedDecompressed = Unpooled.buffer().writeByte(1).writeZero(32768).writeByte(1);
        try {
            // Generate a Snappy-encoded buffer that can only be decompressed correctly if
            // the decoder treats 2-byte literal lengths and 2-byte copy offsets as unsigned values.
            // Write preamble, uncompressed content length (0x8002) encoded as varint.
            compressed.writeByte(130).writeByte(128).writeByte(2);
            // Write a literal consisting of 0x01 followed by 0x8000 zeroes.
            // The total length of this literal is 0x8001, which gets encoded as 0x8000 (length - 1).
            // This length was selected because the encoded form is one larger than the maximum value
            // representable using a signed 16-bit integer, and we want to assert the decoder is reading
            // the length as an unsigned value.
            compressed.writeByte((61 << 2));// tag for LITERAL with a 2-byte length

            compressed.writeShortLE(32768);// length - 1

            compressed.writeByte(1).writeZero(32768);// literal content

            // Similarly, for a 2-byte copy operation we want to ensure the offset is treated as unsigned.
            // Copy the initial 0x01 which was written 0x8001 bytes back in the stream.
            compressed.writeByte(2);// tag for COPY with 2-byte offset, length = 1

            compressed.writeShortLE(32769);// offset

            snappy.decode(compressed, actualDecompressed);
            Assert.assertEquals(expectedDecompressed, actualDecompressed);
        } finally {
            compressed.release();
            actualDecompressed.release();
            expectedDecompressed.release();
        }
    }
}

