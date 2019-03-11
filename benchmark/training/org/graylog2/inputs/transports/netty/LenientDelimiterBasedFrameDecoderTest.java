/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.transports.netty;


import CharsetUtil.ISO_8859_1;
import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class LenientDelimiterBasedFrameDecoderTest {
    @Test
    public void testMultipleLinesStrippedDelimiters() {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("TestLine\r\ng\r\n", Charset.defaultCharset()));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("TestLine", buf.toString(Charset.defaultCharset()));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("g", buf2.toString(Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        buf.release();
        buf2.release();
    }

    @Test
    public void testIncompleteLinesStrippedDelimiters() {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("Test", Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.copiedBuffer("Line\r\ng\r\n", Charset.defaultCharset()));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("TestLine", buf.toString(Charset.defaultCharset()));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("g", buf2.toString(Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        buf.release();
        buf2.release();
    }

    @Test
    public void testMultipleLines() {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, false, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("TestLine\r\ng\r\n", Charset.defaultCharset()));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("TestLine\r\n", buf.toString(Charset.defaultCharset()));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("g\r\n", buf2.toString(Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        buf.release();
        buf2.release();
    }

    @Test
    public void testIncompleteLines() {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, false, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("Test", Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.copiedBuffer("Line\r\ng\r\n", Charset.defaultCharset()));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("TestLine\r\n", buf.toString(Charset.defaultCharset()));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("g\r\n", buf2.toString(Charset.defaultCharset()));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecode() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("first\r\nsecond\nthird", US_ASCII));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("first", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second", buf2.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        ReferenceCountUtil.release(ch.readInbound());
        buf.release();
        buf2.release();
    }

    @Test
    public void testFailSlowTooLongFrameRecovery() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(1, true, false, false, Delimiters.nulDelimiter()));
        for (int i = 0; i < 2; i++) {
            ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }));
            try {
                Assert.assertTrue(ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 0 })));
                Assert.fail(((DecoderException.class.getSimpleName()) + " must be raised."));
            } catch (TooLongFrameException e) {
                // Expected
            }
            ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 'A', 0 }));
            ByteBuf buf = ch.readInbound();
            Assert.assertEquals("A", buf.toString(ISO_8859_1));
            buf.release();
        }
    }

    @Test
    public void testFailFastTooLongFrameRecovery() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(1, Delimiters.nulDelimiter()));
        for (int i = 0; i < 2; i++) {
            try {
                Assert.assertTrue(ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 1, 2 })));
                Assert.fail(((DecoderException.class.getSimpleName()) + " must be raised."));
            } catch (TooLongFrameException e) {
                // Expected
            }
            ch.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 0, 'A', 0 }));
            ByteBuf buf = ch.readInbound();
            Assert.assertEquals("A", buf.toString(ISO_8859_1));
            buf.release();
        }
    }

    @Test
    public void testDecodeNulDelimiter() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, Delimiters.nulDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("first\u0000second\u0000third", US_ASCII));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("first", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second", buf2.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        ReferenceCountUtil.release(ch.readInbound());
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecodeAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("first\r\nsecond\nthird", US_ASCII));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("first", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second", buf2.toString(US_ASCII));
        // Close channel
        Assert.assertTrue(ch.finish());
        ByteBuf buf3 = ch.readInbound();
        Assert.assertEquals("third", buf3.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
        ReferenceCountUtil.release(ch.readInbound());
        buf.release();
        buf2.release();
        buf3.release();
    }

    @Test
    public void testDecodeNulDelimiterAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientDelimiterBasedFrameDecoder(8192, true, true, true, Delimiters.nulDelimiter()));
        ch.writeInbound(Unpooled.copiedBuffer("first\u0000second\u0000third", US_ASCII));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("first", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second", buf2.toString(US_ASCII));
        // Close channel
        Assert.assertTrue(ch.finish());
        ByteBuf buf3 = ch.readInbound();
        Assert.assertEquals("third", buf3.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
        ReferenceCountUtil.release(ch.readInbound());
        buf.release();
        buf2.release();
    }
}

