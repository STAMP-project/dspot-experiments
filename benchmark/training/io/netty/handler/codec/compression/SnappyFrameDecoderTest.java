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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class SnappyFrameDecoderTest {
    private EmbeddedChannel channel;

    @Test(expected = DecompressionException.class)
    public void testReservedUnskippableChunkTypeCausesError() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 3, 1, 0, 0, 0 });
        channel.writeInbound(in);
    }

    @Test(expected = DecompressionException.class)
    public void testInvalidStreamIdentifierLength() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ -128, 5, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test(expected = DecompressionException.class)
    public void testInvalidStreamIdentifierValue() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 's', 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test(expected = DecompressionException.class)
    public void testReservedSkippableBeforeStreamIdentifier() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ -127, 6, 0, 0, 's', 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test(expected = DecompressionException.class)
    public void testUncompressedDataBeforeStreamIdentifier() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 1, 5, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test(expected = DecompressionException.class)
    public void testCompressedDataBeforeStreamIdentifier() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ 0, 5, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test
    public void testReservedSkippableSkipsInput() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89, -127, 5, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
        Assert.assertNull(channel.readInbound());
        Assert.assertFalse(in.isReadable());
    }

    @Test
    public void testUncompressedDataAppendsToOut() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89, 1, 9, 0, 0, 0, 0, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 'n', 'e', 't', 't', 'y' });
        ByteBuf actual = channel.readInbound();
        Assert.assertEquals(expected, actual);
        expected.release();
        actual.release();
    }

    @Test
    public void testCompressedDataDecodesAndAppendsToOut() throws Exception {
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89, 0, 11, 0, 0, 0, 0, 0, 0, 5// preamble length
        , 4 << 2// literal tag + length
        , 110, 101, 116, 116, 121// "netty"
         });
        channel.writeInbound(in);
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 'n', 'e', 't', 't', 'y' });
        ByteBuf actual = channel.readInbound();
        Assert.assertEquals(expected, actual);
        expected.release();
        actual.release();
    }

    // The following two tests differ in only the checksum provided for the literal
    // uncompressed string "netty"
    @Test(expected = DecompressionException.class)
    public void testInvalidChecksumThrowsException() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new SnappyFrameDecoder(true));
        // checksum here is presented as 0
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89, 1, 9, 0, 0, 0, 0, 0, 0, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }

    @Test
    public void testInvalidChecksumDoesNotThrowException() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new SnappyFrameDecoder(true));
        // checksum here is presented as a282986f (little endian)
        ByteBuf in = Unpooled.wrappedBuffer(new byte[]{ ((byte) (255)), 6, 0, 0, 115, 78, 97, 80, 112, 89, 1, 9, 0, 0, 111, -104, -126, -94, 'n', 'e', 't', 't', 'y' });
        channel.writeInbound(in);
    }
}

