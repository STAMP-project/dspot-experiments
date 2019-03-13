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


import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LenientLineBasedFrameDecoderTest {
    @Test
    public void testDecodeWithStrip() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, true, false, false));
        ch.writeInbound(copiedBuffer("first\r\nsecond\nthird", US_ASCII));
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
    public void testDecodeWithoutStrip() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, false));
        ch.writeInbound(copiedBuffer("first\r\nsecond\nthird", US_ASCII));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("first\r\n", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second\n", buf2.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        ch.finish();
        ReferenceCountUtil.release(ch.readInbound());
        buf.release();
        buf2.release();
    }

    @Test
    public void testTooLongLine1() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, false, false));
        try {
            ch.writeInbound(copiedBuffer("12345678901234567890\r\nfirst\nsecond", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        ByteBuf buf = ch.readInbound();
        ByteBuf buf2 = copiedBuffer("first\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(buf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        buf2.release();
    }

    @Test
    public void testTooLongLine2() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, false, false));
        Assert.assertFalse(ch.writeInbound(copiedBuffer("12345678901234567", US_ASCII)));
        try {
            ch.writeInbound(copiedBuffer("890\r\nfirst\r\n", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        ByteBuf buf = ch.readInbound();
        ByteBuf buf2 = copiedBuffer("first\r\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(buf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        buf2.release();
    }

    @Test
    public void testTooLongLineWithFailFast() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, true, false));
        try {
            ch.writeInbound(copiedBuffer("12345678901234567", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        Assert.assertThat(ch.writeInbound(copiedBuffer("890", US_ASCII)), CoreMatchers.is(false));
        Assert.assertThat(ch.writeInbound(copiedBuffer("123\r\nfirst\r\n", US_ASCII)), CoreMatchers.is(true));
        ByteBuf buf = ch.readInbound();
        ByteBuf buf2 = copiedBuffer("first\r\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(buf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecodeSplitsCorrectly() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, false));
        Assert.assertTrue(ch.writeInbound(copiedBuffer("line\r\n.\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("line\r\n", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals(".\r\n", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }

    @Test
    public void testFragmentedDecode() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, false));
        Assert.assertFalse(ch.writeInbound(copiedBuffer("huu", US_ASCII)));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.writeInbound(copiedBuffer("haa\r", US_ASCII)));
        Assert.assertNull(ch.readInbound());
        Assert.assertTrue(ch.writeInbound(copiedBuffer("\nhuuhaa\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("huuhaa\r\n", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("huuhaa\r\n", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }

    @Test
    public void testEmptyLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, true, false, false));
        Assert.assertTrue(ch.writeInbound(copiedBuffer("\nabcna\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("abcna", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecodeWithStripAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, true, false, true));
        ch.writeInbound(copiedBuffer("first\r\nsecond\nthird", US_ASCII));
        ByteBuf buf1 = ch.readInbound();
        Assert.assertEquals("first", buf1.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second", buf2.toString(US_ASCII));
        // Close channel
        Assert.assertTrue(ch.finish());
        ByteBuf buf3 = ch.readInbound();
        Assert.assertEquals("third", buf3.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
        ReferenceCountUtil.release(ch.readInbound());
        buf1.release();
        buf2.release();
        buf3.release();
    }

    @Test
    public void testDecodeWithoutStripAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, true));
        ch.writeInbound(copiedBuffer("first\r\nsecond\nthird", US_ASCII));
        ByteBuf buf1 = ch.readInbound();
        Assert.assertEquals("first\r\n", buf1.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("second\n", buf2.toString(US_ASCII));
        // Close channel
        Assert.assertTrue(ch.finish());
        ByteBuf buf3 = ch.readInbound();
        Assert.assertEquals("third", buf3.toString(US_ASCII));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
        ReferenceCountUtil.release(ch.readInbound());
        buf1.release();
        buf2.release();
        buf3.release();
    }

    @Test
    public void testTooLongLine1AndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, false, true));
        try {
            ch.writeInbound(copiedBuffer("12345678901234567890\r\nfirst\nsecond", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        ByteBuf buf = ch.readInbound();
        ByteBuf expectedBuf = copiedBuffer("first\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(expectedBuf));
        // Close channel
        Assert.assertTrue(ch.finish());
        ByteBuf buf2 = ch.readInbound();
        ByteBuf expectedBuf2 = copiedBuffer("second", US_ASCII);
        Assert.assertThat(buf2, CoreMatchers.is(expectedBuf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        expectedBuf.release();
        buf2.release();
        expectedBuf2.release();
    }

    @Test
    public void testTooLongLine2AndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, false, true));
        Assert.assertFalse(ch.writeInbound(copiedBuffer("12345678901234567", US_ASCII)));
        try {
            ch.writeInbound(copiedBuffer("890\r\nfirst\r\n", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        ByteBuf buf = ch.readInbound();
        ByteBuf buf2 = copiedBuffer("first\r\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(buf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        buf2.release();
    }

    @Test
    public void testTooLongLineWithFailFastAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(16, false, true, true));
        try {
            ch.writeInbound(copiedBuffer("12345678901234567", US_ASCII));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        Assert.assertThat(ch.writeInbound(copiedBuffer("890", US_ASCII)), CoreMatchers.is(false));
        Assert.assertThat(ch.writeInbound(copiedBuffer("123\r\nfirst\r\n", US_ASCII)), CoreMatchers.is(true));
        ByteBuf buf = ch.readInbound();
        ByteBuf buf2 = copiedBuffer("first\r\n", US_ASCII);
        Assert.assertThat(buf, CoreMatchers.is(buf2));
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecodeSplitsCorrectlyAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, true));
        Assert.assertTrue(ch.writeInbound(copiedBuffer("line\r\n.\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("line\r\n", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals(".\r\n", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }

    @Test
    public void testFragmentedDecodeAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, false, false, true));
        Assert.assertFalse(ch.writeInbound(copiedBuffer("huu", US_ASCII)));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.writeInbound(copiedBuffer("haa\r", US_ASCII)));
        Assert.assertNull(ch.readInbound());
        Assert.assertTrue(ch.writeInbound(copiedBuffer("\nhuuhaa\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("huuhaa\r\n", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("huuhaa\r\n", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }

    @Test
    public void testEmptyLineAndEmitLastLine() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LenientLineBasedFrameDecoder(8192, true, false, true));
        Assert.assertTrue(ch.writeInbound(copiedBuffer("\nabcna\r\n", US_ASCII)));
        ByteBuf buf = ch.readInbound();
        Assert.assertEquals("", buf.toString(US_ASCII));
        ByteBuf buf2 = ch.readInbound();
        Assert.assertEquals("abcna", buf2.toString(US_ASCII));
        Assert.assertFalse(ch.finishAndReleaseAll());
        buf.release();
        buf2.release();
    }
}

