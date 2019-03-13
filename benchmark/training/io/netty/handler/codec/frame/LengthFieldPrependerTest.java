/**
 * Copyright 2013 The Netty Project
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
package io.netty.handler.codec.frame;


import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.LengthFieldPrepender;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;


public class LengthFieldPrependerTest {
    private ByteBuf msg;

    @Test
    public void testPrependLength() throws Exception {
        final EmbeddedChannel ch = new EmbeddedChannel(new LengthFieldPrepender(4));
        ch.writeOutbound(msg);
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals(4, buf.readableBytes());
        Assert.assertEquals(msg.readableBytes(), buf.readInt());
        buf.release();
        buf = ch.readOutbound();
        Assert.assertSame(buf, msg);
        buf.release();
    }

    @Test
    public void testPrependLengthIncludesLengthFieldLength() throws Exception {
        final EmbeddedChannel ch = new EmbeddedChannel(new LengthFieldPrepender(4, true));
        ch.writeOutbound(msg);
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals(4, buf.readableBytes());
        Assert.assertEquals(5, buf.readInt());
        buf.release();
        buf = ch.readOutbound();
        Assert.assertSame(buf, msg);
        buf.release();
    }

    @Test
    public void testPrependAdjustedLength() throws Exception {
        final EmbeddedChannel ch = new EmbeddedChannel(new LengthFieldPrepender(4, (-1)));
        ch.writeOutbound(msg);
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals(4, buf.readableBytes());
        Assert.assertEquals(((msg.readableBytes()) - 1), buf.readInt());
        buf.release();
        buf = ch.readOutbound();
        Assert.assertSame(buf, msg);
        buf.release();
    }

    @Test
    public void testAdjustedLengthLessThanZero() throws Exception {
        final EmbeddedChannel ch = new EmbeddedChannel(new LengthFieldPrepender(4, (-2)));
        try {
            ch.writeOutbound(msg);
            Assert.fail(((EncoderException.class.getSimpleName()) + " must be raised."));
        } catch (EncoderException e) {
            // Expected
        }
    }

    @Test
    public void testPrependLengthInLittleEndian() throws Exception {
        final EmbeddedChannel ch = new EmbeddedChannel(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
        ch.writeOutbound(msg);
        ByteBuf buf = ch.readOutbound();
        Assert.assertEquals(4, buf.readableBytes());
        byte[] writtenBytes = new byte[buf.readableBytes()];
        buf.getBytes(0, writtenBytes);
        Assert.assertEquals(1, writtenBytes[0]);
        Assert.assertEquals(0, writtenBytes[1]);
        Assert.assertEquals(0, writtenBytes[2]);
        Assert.assertEquals(0, writtenBytes[3]);
        buf.release();
        buf = ch.readOutbound();
        Assert.assertSame(buf, msg);
        buf.release();
        Assert.assertFalse("The channel must have been completely read", ch.finish());
    }
}

