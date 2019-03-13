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
package org.jboss.netty.handler.codec.frame;


import CharsetUtil.US_ASCII;
import org.hamcrest.CoreMatchers;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Assert;
import org.junit.Test;


public class LineBasedFrameDecoderTest {
    @Test
    public void testDecodeWithStrip() throws Exception {
        DecoderEmbedder<ChannelBuffer> embedder = new DecoderEmbedder<ChannelBuffer>(new LineBasedFrameDecoder(8192, true, false));
        Assert.assertTrue(embedder.offer(copiedBuffer("first\r\nsecond\nthird", US_ASCII)));
        Assert.assertTrue(embedder.finish());
        Assert.assertEquals("first", embedder.poll().toString(US_ASCII));
        Assert.assertEquals("second", embedder.poll().toString(US_ASCII));
        Assert.assertNull(embedder.poll());
    }

    @Test
    public void testDecodeWithoutStrip() throws Exception {
        DecoderEmbedder<ChannelBuffer> embedder = new DecoderEmbedder<ChannelBuffer>(new LineBasedFrameDecoder(8192, false, false));
        Assert.assertTrue(embedder.offer(copiedBuffer("first\r\nsecond\nthird", US_ASCII)));
        Assert.assertTrue(embedder.finish());
        Assert.assertEquals("first\r\n", embedder.poll().toString(US_ASCII));
        Assert.assertEquals("second\n", embedder.poll().toString(US_ASCII));
        Assert.assertNull(embedder.poll());
    }

    @Test
    public void testTooLongLine1() throws Exception {
        DecoderEmbedder<ChannelBuffer> embedder = new DecoderEmbedder<ChannelBuffer>(new LineBasedFrameDecoder(16, false, false));
        try {
            embedder.offer(copiedBuffer("12345678901234567890\r\nfirst\nsecond", US_ASCII));
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        embedder.offer(wrappedBuffer(new byte[1]));// A workaround that triggers decode() once again.

        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertTrue(embedder.finish());
        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertThat(embedder.poll().toString(US_ASCII), CoreMatchers.is("first\n"));
    }

    @Test
    public void testTooLongLine2() throws Exception {
        DecoderEmbedder<ChannelBuffer> embedder = new DecoderEmbedder<ChannelBuffer>(new LineBasedFrameDecoder(16, false, false));
        Assert.assertFalse(embedder.offer(copiedBuffer("12345678901234567", US_ASCII)));
        try {
            embedder.offer(copiedBuffer("890\r\nfirst\r\n", US_ASCII));
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        embedder.offer(wrappedBuffer(new byte[1]));// A workaround that triggers decode() once again.

        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertTrue(embedder.finish());
        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertEquals("first\r\n", embedder.poll().toString(US_ASCII));
    }

    @Test
    public void testTooLongLineWithFailFast() throws Exception {
        DecoderEmbedder<ChannelBuffer> embedder = new DecoderEmbedder<ChannelBuffer>(new LineBasedFrameDecoder(16, false, true));
        try {
            embedder.offer(copiedBuffer("12345678901234567", US_ASCII));
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(TooLongFrameException.class)));
        }
        Assert.assertThat(embedder.offer(copiedBuffer("890", US_ASCII)), CoreMatchers.is(false));
        Assert.assertThat(embedder.offer(copiedBuffer("123\r\nfirst\r\n", US_ASCII)), CoreMatchers.is(true));
        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertTrue(embedder.finish());
        Assert.assertThat(embedder.size(), CoreMatchers.is(1));
        Assert.assertEquals("first\r\n", embedder.poll().toString(US_ASCII));
    }
}

