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
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class DelimiterBasedFrameDecoderTest {
    @Test
    public void testMultipleLinesStrippedDelimiters() {
        EmbeddedChannel ch = new EmbeddedChannel(new DelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
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
        EmbeddedChannel ch = new EmbeddedChannel(new DelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
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
        EmbeddedChannel ch = new EmbeddedChannel(new DelimiterBasedFrameDecoder(8192, false, Delimiters.lineDelimiter()));
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
        EmbeddedChannel ch = new EmbeddedChannel(new DelimiterBasedFrameDecoder(8192, false, Delimiters.lineDelimiter()));
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
        EmbeddedChannel ch = new EmbeddedChannel(new DelimiterBasedFrameDecoder(8192, true, Delimiters.lineDelimiter()));
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
}

