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
package io.netty.handler.codec;


import ByteToMessageDecoder.COMPOSITE_CUMULATOR;
import ByteToMessageDecoder.MERGE_CUMULATOR;
import UnpooledByteBufAllocator.DEFAULT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.internal.PlatformDependent;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Assert;
import org.junit.Test;


public class ByteToMessageDecoderTest {
    @Test
    public void testRemoveItself() {
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            private boolean removed;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(removed);
                in.readByte();
                ctx.pipeline().remove(this);
                removed = true;
            }
        });
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'a', 'b', 'c' });
        channel.writeInbound(buf.copy());
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(b, buf.skipBytes(1));
        b.release();
        buf.release();
    }

    @Test
    public void testRemoveItselfWriteBuffer() {
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a', 'b', 'c' });
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            private boolean removed;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(removed);
                in.readByte();
                ctx.pipeline().remove(this);
                // This should not let it keep call decode
                buf.writeByte('d');
                removed = true;
            }
        });
        channel.writeInbound(buf.copy());
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 'b', 'c' });
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(expected, b);
        expected.release();
        buf.release();
        b.release();
    }

    /**
     * Verifies that internal buffer of the ByteToMessageDecoder is released once decoder is removed from pipeline. In
     * this case input is read fully.
     */
    @Test
    public void testInternalBufferClearReadAll() {
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a' });
        EmbeddedChannel channel = newInternalBufferTestChannel();
        Assert.assertFalse(channel.writeInbound(buf));
        Assert.assertFalse(channel.finish());
    }

    /**
     * Verifies that internal buffer of the ByteToMessageDecoder is released once decoder is removed from pipeline. In
     * this case input was not fully read.
     */
    @Test
    public void testInternalBufferClearReadPartly() {
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a', 'b' });
        EmbeddedChannel channel = newInternalBufferTestChannel();
        Assert.assertTrue(channel.writeInbound(buf));
        Assert.assertTrue(channel.finish());
        ByteBuf expected = Unpooled.wrappedBuffer(new byte[]{ 'b' });
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(expected, b);
        Assert.assertNull(channel.readInbound());
        expected.release();
        b.release();
    }

    @Test
    public void handlerRemovedWillNotReleaseBufferIfDecodeInProgress() {
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                ctx.pipeline().remove(this);
                Assert.assertTrue(((in.refCnt()) != 0));
            }

            @Override
            protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
                ByteToMessageDecoderTest.assertCumulationReleased(internalBuffer());
            }
        });
        byte[] bytes = new byte[1024];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(bytes)));
        Assert.assertTrue(channel.finishAndReleaseAll());
    }

    @Test
    public void testFireChannelReadCompleteOnInactive() throws InterruptedException {
        final BlockingQueue<Integer> queue = new LinkedBlockingDeque<Integer>();
        final ByteBuf buf = Unpooled.buffer().writeBytes(new byte[]{ 'a', 'b' });
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                int readable = in.readableBytes();
                Assert.assertTrue((readable > 0));
                in.skipBytes(readable);
            }

            @Override
            protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(in.isReadable());
                out.add("data");
            }
        }, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                queue.add(3);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                queue.add(1);
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                if (!(ctx.channel().isActive())) {
                    queue.add(2);
                }
            }
        });
        Assert.assertFalse(channel.writeInbound(buf));
        channel.finish();
        Assert.assertEquals(1, ((int) (queue.take())));
        Assert.assertEquals(2, ((int) (queue.take())));
        Assert.assertEquals(3, ((int) (queue.take())));
        Assert.assertTrue(queue.isEmpty());
    }

    // See https://github.com/netty/netty/issues/4635
    @Test
    public void testRemoveWhileInCallDecode() {
        final Object upgradeMessage = new Object();
        final ByteToMessageDecoder decoder = new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertEquals('a', in.readByte());
                out.add(upgradeMessage);
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(decoder, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg == upgradeMessage) {
                    ctx.pipeline().remove(decoder);
                    return;
                }
                ctx.fireChannelRead(msg);
            }
        });
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'a', 'b', 'c' });
        Assert.assertTrue(channel.writeInbound(buf.copy()));
        ByteBuf b = channel.readInbound();
        Assert.assertEquals(b, buf.skipBytes(1));
        Assert.assertFalse(channel.finish());
        buf.release();
        b.release();
    }

    @Test
    public void testDecodeLastEmptyBuffer() {
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                int readable = in.readableBytes();
                Assert.assertTrue((readable > 0));
                out.add(in.readBytes(readable));
            }
        });
        byte[] bytes = new byte[1024];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(bytes)));
        ByteToMessageDecoderTest.assertBuffer(Unpooled.wrappedBuffer(bytes), ((ByteBuf) (channel.readInbound())));
        Assert.assertNull(channel.readInbound());
        Assert.assertFalse(channel.finish());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testDecodeLastNonEmptyBuffer() {
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            private boolean decodeLast;

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                int readable = in.readableBytes();
                Assert.assertTrue((readable > 0));
                if ((!(decodeLast)) && (readable == 1)) {
                    return;
                }
                out.add(in.readBytes((decodeLast ? readable : readable - 1)));
            }

            @Override
            protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                Assert.assertFalse(decodeLast);
                decodeLast = true;
                super.decodeLast(ctx, in, out);
            }
        });
        byte[] bytes = new byte[1024];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(bytes)));
        ByteToMessageDecoderTest.assertBuffer(Unpooled.wrappedBuffer(bytes, 0, ((bytes.length) - 1)), ((ByteBuf) (channel.readInbound())));
        Assert.assertNull(channel.readInbound());
        Assert.assertTrue(channel.finish());
        ByteToMessageDecoderTest.assertBuffer(Unpooled.wrappedBuffer(bytes, ((bytes.length) - 1), 1), ((ByteBuf) (channel.readInbound())));
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testReadOnlyBuffer() {
        EmbeddedChannel channel = new EmbeddedChannel(new ByteToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            }
        });
        Assert.assertFalse(channel.writeInbound(Unpooled.buffer(8).writeByte(1).asReadOnly()));
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{ ((byte) (2)) })));
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void releaseWhenMergeCumulateThrows() {
        final Error error = new Error();
        ByteBuf cumulation = new UnpooledHeapByteBuf(UnpooledByteBufAllocator.DEFAULT, 0, 64) {
            @Override
            public ByteBuf writeBytes(ByteBuf src) {
                throw error;
            }
        };
        ByteBuf in = Unpooled.buffer().writeZero(12);
        try {
            MERGE_CUMULATOR.cumulate(DEFAULT, cumulation, in);
            Assert.fail();
        } catch (Error expected) {
            Assert.assertSame(error, expected);
            Assert.assertEquals(0, in.refCnt());
        }
    }

    @Test
    public void releaseWhenCompositeCumulateThrows() {
        final Error error = new Error();
        ByteBuf cumulation = new CompositeByteBuf(UnpooledByteBufAllocator.DEFAULT, false, 64) {
            @Override
            public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
                throw error;
            }
        };
        ByteBuf in = Unpooled.buffer().writeZero(12);
        try {
            COMPOSITE_CUMULATOR.cumulate(DEFAULT, cumulation, in);
            Assert.fail();
        } catch (Error expected) {
            Assert.assertSame(error, expected);
            Assert.assertEquals(0, in.refCnt());
        }
    }
}

