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


public class Bzip2DecoderTest extends AbstractDecoderTest {
    private static final byte[] DATA = new byte[]{ 66, 90, 104, 55, 49, 65, 89, 38, 83, 89, 119, 123, ((byte) (202)), ((byte) (192)), 0, 0, 0, 5, ((byte) (128)), 0, 1, 2, 0, 4, 32, 32, 0, 48, ((byte) (205)), 52, 25, ((byte) (166)), ((byte) (137)), ((byte) (153)), ((byte) (197)), ((byte) (220)), ((byte) (145)), 78, 20, 36, 29, ((byte) (222)), ((byte) (242)), ((byte) (176)), 0 };

    public Bzip2DecoderTest() throws Exception {
    }

    @Test
    public void testUnexpectedStreamIdentifier() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("Unexpected stream identifier contents");
        ByteBuf in = Unpooled.buffer();
        in.writeLong(1823080128301928729L);// random value

        writeInboundDestroyAndExpectDecompressionException(in);
    }

    @Test
    public void testInvalidBlockSize() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("block size is invalid");
        ByteBuf in = Unpooled.buffer();
        in.writeMedium(MAGIC_NUMBER);
        in.writeByte('0');// incorrect block size

        channel.writeInbound(in);
    }

    @Test
    public void testBadBlockHeader() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("bad block header");
        ByteBuf in = Unpooled.buffer();
        in.writeMedium(MAGIC_NUMBER);
        in.writeByte('1');// block size

        in.writeMedium(11);// incorrect block header

        in.writeMedium(11);// incorrect block header

        in.writeInt(11111);// block CRC

        channel.writeInbound(in);
    }

    @Test
    public void testStreamCrcErrorOfEmptyBlock() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("stream CRC error");
        ByteBuf in = Unpooled.buffer();
        in.writeMedium(MAGIC_NUMBER);
        in.writeByte('1');// block size

        in.writeMedium(END_OF_STREAM_MAGIC_1);
        in.writeMedium(END_OF_STREAM_MAGIC_2);
        in.writeInt(1);// wrong storedCombinedCRC

        channel.writeInbound(in);
    }

    @Test
    public void testStreamCrcError() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("stream CRC error");
        final byte[] data = Arrays.copyOf(Bzip2DecoderTest.DATA, Bzip2DecoderTest.DATA.length);
        data[41] = ((byte) (221));
        AbstractDecoderTest.tryDecodeAndCatchBufLeaks(channel, Unpooled.wrappedBuffer(data));
    }

    @Test
    public void testIncorrectHuffmanGroupsNumber() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("incorrect huffman groups number");
        final byte[] data = Arrays.copyOf(Bzip2DecoderTest.DATA, Bzip2DecoderTest.DATA.length);
        data[25] = 112;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testIncorrectSelectorsNumber() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("incorrect selectors number");
        final byte[] data = Arrays.copyOf(Bzip2DecoderTest.DATA, Bzip2DecoderTest.DATA.length);
        data[25] = 47;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        channel.writeInbound(in);
    }

    @Test
    public void testBlockCrcError() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("block CRC error");
        final byte[] data = Arrays.copyOf(Bzip2DecoderTest.DATA, Bzip2DecoderTest.DATA.length);
        data[11] = 119;
        ByteBuf in = Unpooled.wrappedBuffer(data);
        writeInboundDestroyAndExpectDecompressionException(in);
    }

    @Test
    public void testStartPointerInvalid() throws Exception {
        expected.expect(DecompressionException.class);
        expected.expectMessage("start pointer invalid");
        final byte[] data = Arrays.copyOf(Bzip2DecoderTest.DATA, Bzip2DecoderTest.DATA.length);
        data[14] = ((byte) (255));
        ByteBuf in = Unpooled.wrappedBuffer(data);
        writeInboundDestroyAndExpectDecompressionException(in);
    }
}

