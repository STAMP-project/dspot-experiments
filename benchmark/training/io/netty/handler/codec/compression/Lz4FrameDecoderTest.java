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
package io.netty.handler.codec.compression;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import org.junit.Test;


public class Lz4FrameDecoderTest extends AbstractDecoderTest {
    private static final byte[] DATA = new byte[]{ 76, 90, 52, 66, 108, 111, 99, 107// magic bytes
    , 22, // token
    5, 0, 0, 0, 5, 0, 0, 0// compr. and decompr. length
    , ((byte) (134)), ((byte) (228)), 121, 15// checksum
    , 78, 101, 116, 116, 121// data
    , 76, 90, 52, 66, 108, 111, 99, 107// magic bytes
    , 22, // token
    0, 0, 0, 0, 0, 0, 0, 0// last empty block
    , 0, 0, 0, 0 };

    public Lz4FrameDecoderTest() throws Exception {
    }

    @Test
    public void testUnexpectedBlockIdentifier() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("unexpected block identifier");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[1] = 0;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testInvalidCompressedLength() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("invalid compressedLength");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[12] = ((byte) (255));
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testInvalidDecompressedLength() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("invalid decompressedLength");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[16] = ((byte) (255));
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testDecompressedAndCompressedLengthMismatch() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("mismatch");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[13] = 1;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testUnexpectedBlockType() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("unexpected blockType");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[8] = 54;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testMismatchingChecksum() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("mismatching checksum");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[17] = 1;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testChecksumErrorOfLastBlock() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("checksum error");
        final byte[] data = Arrays.copyOf(Lz4FrameDecoderTest.DATA, Lz4FrameDecoderTest.DATA.length);
        data[44] = 1;
        AbstractDecoderTest.tryDecodeAndCatchBufLeaks(channel, Unpooled.wrappedBuffer(data));
    }
}

