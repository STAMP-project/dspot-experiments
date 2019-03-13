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
package io.netty.handler.codec;


import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LineBasedFrameDecoderTest {
    @Test
    public void testDecodeWithStrip() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(8192, true, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(8192, false, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(16, false, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(16, false, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(16, false, true));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(8192, false, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(8192, false, false));
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
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(8192, true, false));
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
    public void testNotFailFast() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new LineBasedFrameDecoder(2, false, false));
        Assert.assertFalse(ch.writeInbound(wrappedBuffer(new byte[]{ 0, 1, 2 })));
        Assert.assertFalse(ch.writeInbound(wrappedBuffer(new byte[]{ 3, 4 })));
        try {
            ch.writeInbound(wrappedBuffer(new byte[]{ '\n' }));
            Assert.fail();
        } catch (TooLongFrameException expected) {
            // Expected once we received a full frame.
        }
        Assert.assertFalse(ch.writeInbound(wrappedBuffer(new byte[]{ '5' })));
        Assert.assertTrue(ch.writeInbound(wrappedBuffer(new byte[]{ '\n' })));
        ByteBuf expected = wrappedBuffer(new byte[]{ '5', '\n' });
        ByteBuf buffer = ch.readInbound();
        Assert.assertEquals(expected, buffer);
        expected.release();
        buffer.release();
        Assert.assertFalse(ch.finish());
    }
}

